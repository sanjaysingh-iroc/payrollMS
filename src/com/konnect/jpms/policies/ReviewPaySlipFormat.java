package com.konnect.jpms.policies;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;


public class ReviewPaySlipFormat implements ServletRequestAware, ServletResponseAware, IStatements{

	private static final long serialVersionUID = 1L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF=null;
	String paySlipFormatId;
	
	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	UtilityFunctions uF = new UtilityFunctions();
	List<String> salaryHeadName = new ArrayList<String>();
	List<String> deductionHeadName = new ArrayList<String>();
	List<Double> salHeadAmountGross = new ArrayList<Double>();
	List<Double> deductionHeadAmount = new ArrayList<Double>();
	List<String> leaveName = new ArrayList<String>();
	List<Integer> noOfLeave = new ArrayList<Integer>(); 
	ArrayList<String> payEmpHead = new ArrayList<String>();
	Map hmBalanceLeave = new HashMap();
	
	
     public void execute() throws Exception  {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return;
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		
		if(uF.parseToInt(getPaySlipFormatId()) == 1) {
			viewSalarySlipFirstFormat();
		} else if(uF.parseToInt(getPaySlipFormatId()) == 2) {
			viewSalarySlipSecondFormat();
		} else if(uF.parseToInt(getPaySlipFormatId()) == 3) {
			viewSalarySlipThirdFormat();
		} else if(uF.parseToInt(getPaySlipFormatId()) == 4) {
			viewSalarySlipFourthFormat();
		} else if(uF.parseToInt(getPaySlipFormatId()) == 5) {
			viewSalarySlipFifthFormat();
		} else if(uF.parseToInt(getPaySlipFormatId()) == 6) {
			viewSalarySlipSixthFormat();
		} else if(uF.parseToInt(getPaySlipFormatId()) == 7) {
			viewSalarySlipSeventhFormat();
	//===start parvez date: 02-09-2022===		
		} else if(uF.parseToInt(getPaySlipFormatId()) == 8) {
			viewSalarySlipEightFormat();
		}
	//===end parvez date: 02-09-2022===	
		

	}
    

     private void viewSalarySlipSeventhFormat() {

   		try{
   			ByteArrayOutputStream baos = new ByteArrayOutputStream();
   			Document document = new Document();
   			PdfWriter.getInstance(document, baos);
   			document.open();

   			String filePathCompanyLOgo = null;

   			payEmpHead.add("Emp No.");
   			payEmpHead.add("UAN No.");
   			payEmpHead.add("Name ");
   			payEmpHead.add("Calender Days ");
   			payEmpHead.add("Department ");
   			payEmpHead.add("W.Offs/ Holidays");
   			payEmpHead.add("Designation");
   			payEmpHead.add("Leave Days");
   			payEmpHead.add("Joining Dt");
   			payEmpHead.add("Paid Days");
   			payEmpHead.add("Pan");
   			payEmpHead.add("O T Hrs");
   			
   			salaryHeadName.add("Basic Pay");
   			salaryHeadName.add("Spa All");
   			salaryHeadName.add("H.R.A");
   			salaryHeadName.add("Veriable Allowance");
   			salaryHeadName.add("Conveyance");
   			salaryHeadName.add("MEDICAL ALL");
   			salaryHeadName.add("Over Time Pay");
   			salaryHeadName.add("Inc/Mob/Petrol");
   			salaryHeadName.add("Total Payment");
   			
   			deductionHeadName.add("Prof. Tax ");
   			deductionHeadName.add("P.F (E.C)");
   			deductionHeadName.add("Insurance/Medical Bill");
   			deductionHeadName.add("Advance Salary");
   			deductionHeadName.add("T.D.S. Ded.");
   			deductionHeadName.add("Mise/Hostel/Ele-Bill");
   			deductionHeadName.add("L.W.F.");
   			deductionHeadName.add("Security Deposit");
   			deductionHeadName.add("Deductions ");
   			deductionHeadName.add("Net Payable");
   		
  			filePathCompanyLOgo = request.getRealPath("/userImages/company_avatar_photo1.png");
  			 
  			Image imageLogo=null;
  	
  			try{
  				
  				FileInputStream fileInputStream1=null;
  		        File file1 = new File(filePathCompanyLOgo);
  		        byte[] bFile1 = new byte[(int) file1.length()];
  		        fileInputStream1 = new FileInputStream(file1);
  			    fileInputStream1.read(bFile1);
  			    fileInputStream1.close();
  		        imageLogo = Image.getInstance(bFile1);
  		        
  			}catch(FileNotFoundException e){
  				imageLogo = Image.getInstance(filePathCompanyLOgo);
  			}
   			
  			
  			com.itextpdf.text.Font normalwithbold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
  			com.itextpdf.text.Font small = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7);
  			com.itextpdf.text.Font smallBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.WHITE);
  			
  			
  			
  			com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(10);
  			table.setWidthPercentage(100);       
  			int[] cols = {3,10,10,10,10,10,10,10,10,10};
  			table.setWidths(cols);

  			PdfPTable companyNameTable = new PdfPTable(1);
  			int[] arrheaderwidthsh = { 100 }; // percentage
  			companyNameTable.getDefaultCell().setBorderWidth(0);
  			companyNameTable.setWidths(arrheaderwidthsh);
  			PdfPCell companyNamecell = new PdfPCell(new Phrase("Company Name", FontFactory.getFont("Verdana", 14,Font.BOLD)));
  			

  			companyNamecell.setBorderWidthTop(0);
  			companyNamecell.setBorderWidthBottom(0);
  			companyNamecell.setBorderWidthLeft(0);
  			companyNamecell.setBorderWidthRight(0);

  			companyNamecell.setHorizontalAlignment(Element.ALIGN_RIGHT);
  			companyNameTable.addCell(companyNamecell);
  			PdfPCell companyNamecell1 = new PdfPCell(new Phrase("Company Address", FontFactory.getFont("Verdana", 10,Font.NORMAL)));

  			companyNamecell1.setBorderWidthTop(0);
  			companyNamecell1.setBorderWidthBottom(0);
  			companyNamecell1.setBorderWidthLeft(0);
  			companyNamecell1.setBorderWidthRight(0);

  			companyNamecell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
  			companyNameTable.addCell(companyNamecell1);
  			
//  			
  			com.itextpdf.text.pdf.PdfPCell row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("",smallBold));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setBorder(Rectangle.NO_BORDER);
  	       row1.setColspan(10);
  	       row1.setPadding(2.5f);
  	       table.addCell(row1);
  	     
  	     //New Row
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Payslip for Oct 2020", normalwithbold));
  	       row1.setPadding(2.5f);
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setBorder(Rectangle.NO_BORDER);	
  	       row1.setColspan(10);
  	       table.addCell(row1);
  	     
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(10);
  	       row1.setBorder(Rectangle.NO_BORDER);	
  	       table.addCell(row1);
  	         
//  	       String heading2="EMPLOYEE DETAILS";
  	     //New Row
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(5);
  	       table.addCell(row1);
  	      
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Date of Joining",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(3);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       //new row
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Employee Details",smallBold));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setBackgroundColor(BaseColor.BLUE);
  	       row1.setColspan(4);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Payment & Leave Details",smallBold));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setBackgroundColor(BaseColor.BLUE);
  	       row1.setColspan(3);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Location Details",smallBold));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setBackgroundColor(BaseColor.BLUE);
  	       row1.setColspan(3);
  	       table.addCell(row1);
  	       //new row
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("EMP NO.",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Bank Name",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(1);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Location",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(1);
  	       table.addCell(row1);
  	       
  	       //new row
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Designation",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Account No",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(1);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Deputation to",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("N/A",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(1);
  	       table.addCell(row1);
  	       
  	     //new row
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("PAN",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Days Paid",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(4);
  	       table.addCell(row1);
  	       
  	       //new row
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Aadhaar No",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(1);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Leave Balance",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(1);
  	       table.addCell(row1);
  	     
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Earned",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(1);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-", small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(1);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Availed",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(1);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-", small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(1);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(1);
  	       table.addCell(row1);
  	       
//  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(""+((noOfLeave !=null && noOfLeave.size()>1) ? noOfLeave.get(2) : ""), small));
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-", small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(1);
  	       table.addCell(row1);
  	       
  	       //new row
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("UAN",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Member #", small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(1);
  	       table.addCell(row1);
  	       
  	       
  	       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("N/A", small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Start Date", small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(1);
  	       table.addCell(row1);
  	       
  	       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("", small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       //new row
  	       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setColspan(10);
  	       row1.setBorder(Rectangle.NO_BORDER);	
  	       table.addCell(row1);
  	       
  	       
  	       //new row
  	       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Earnings", smallBold));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setBackgroundColor(BaseColor.BLUE);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Arrears", smallBold));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setBackgroundColor(BaseColor.BLUE);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Current", smallBold));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setBackgroundColor(BaseColor.BLUE);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Deduction", smallBold));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setBackgroundColor(BaseColor.BLUE);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  			
  	       
  	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Amount", smallBold));
  	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  	       row1.setBackgroundColor(BaseColor.BLUE);
  	       row1.setColspan(2);
  	       table.addCell(row1);
  	       
  	       
  	       
//  	     Double netPayable =  deductionHeadAmount.get(deductionHeadAmount.size()-1);
//         if(deductionHeadName.contains("Net Payable")) {
//         deductionHeadName.remove(deductionHeadName.size()-1);
//          deductionHeadAmount.remove(deductionHeadAmount.size() - 1);
//
//         }
//         
//         Double totalDeduction =  deductionHeadAmount.get(deductionHeadAmount.size() - 1);
//         if(deductionHeadName.contains("Deductions")) {
//         deductionHeadName.remove(deductionHeadName.size() - 1);
//          deductionHeadAmount.remove(deductionHeadAmount.size() - 1);
//
//         }
         
//         Double totalEarning = salHeadAmount.get(salHeadAmount.size() - 1);
//         if(salaryHeadName.contains("Total")){
//         	salaryHeadName.remove(salaryHeadName.size() - 1);
//         	salHeadAmount.remove(salHeadAmount.size() - 1);
//         }
         
         int nCount = 0;
  		Iterator<String> itr1 = salaryHeadName.iterator();
  		int dedeductionHeadNameSize=deductionHeadName.size();
  		while (itr1.hasNext()) {

  			String strHeadNM = itr1.next();
  			if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
  				
  			}
  			
  			  row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(strHeadNM, small));
  		       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  		       row1.setColspan(2);
  		       table.addCell(row1);
  		       
  		       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("0", small));
  		       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
  		       row1.setColspan(2);
  		       table.addCell(row1);
  		       
  		       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(uF.formatIntoTwoDecimal(0),small));
  		       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
  		       row1.setColspan(2);
  		       table.addCell(row1);
  		       
  		       if(nCount<dedeductionHeadNameSize) {
  		    	   if (null!=deductionHeadName.get(nCount) ) {
  			    	   row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(deductionHeadName.get(nCount),small));
  				       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  				       row1.setColspan(2);
  				       table.addCell(row1);

  				       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(uF.formatIntoTwoDecimal(0),small));
  				       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
  				       row1.setColspan(2);
  				       table.addCell(row1);
  		    	   } else {
  		    		   row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
  		    		   row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  		    		   row1.setColspan(2);
  		    		   table.addCell(row1);

  		    		   row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
  		    		   row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
  		    		   row1.setColspan(2);
  		    		   table.addCell(row1);
  		    	   }
  			       
  		       } else {
  		    	   row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
  			       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
  			       row1.setColspan(2);
  			       table.addCell(row1);

  			       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
  			       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
  			       row1.setColspan(2);
  			       table.addCell(row1);
  		       }
  		       nCount++;
  		}
  		
         //new row
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
         row1.setHorizontalAlignment(Element.ALIGN_LEFT);
         row1.setColspan(2);
         table.addCell(row1);
         
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
         row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
         row1.setColspan(2);
         table.addCell(row1);
         
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
         row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
         row1.setColspan(2);
         table.addCell(row1);
         
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
         row1.setHorizontalAlignment(Element.ALIGN_LEFT);
         row1.setColspan(2);
         table.addCell(row1);
         
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
         row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
         row1.setColspan(2);
         table.addCell(row1);
         
         //new row
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total  Earnings (Arrears + Current)",small));
         row1.setHorizontalAlignment(Element.ALIGN_CENTER);
         row1.setColspan(4);
         table.addCell(row1);
         
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(uF.formatIntoTwoDecimal(0),small));
         row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
         row1.setColspan(2);
         table.addCell(row1);
         
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total Deduction",small));
         row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
         row1.setColspan(2);
         table.addCell(row1);
         
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(uF.formatIntoTwoDecimal(0),small));
         row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
         row1.setColspan(2);
         table.addCell(row1);
         
         //new row
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("   ",small));
         row1.setBorder(Rectangle.NO_BORDER);	
         row1.setHorizontalAlignment(Element.ALIGN_CENTER);
         row1.setColspan(6);
         table.addCell(row1);
         
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("NET PAY",small));
         row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
         row1.setColspan(2);
         table.addCell(row1);
         
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(uF.formatIntoTwoDecimal(0), small));
         row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
         row1.setColspan(2);
         table.addCell(row1);
         
         //new row
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("", small));
         row1.setBorder(Rectangle.NO_BORDER);	
         row1.setHorizontalAlignment(Element.ALIGN_CENTER);
         row1.setColspan(6);
         table.addCell(row1);
         
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Date of Payment", small));
         row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
         row1.setColspan(2);
         table.addCell(row1);
         
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-", small));
         row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
         row1.setColspan(2);
         table.addCell(row1);
         
         //new row
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("", small));
         row1.setBorder(Rectangle.NO_BORDER);	
         row1.setHorizontalAlignment(Element.ALIGN_CENTER);
         row1.setColspan(6);
         table.addCell(row1);
         
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("TAX METHOD FOLLOWED", small));
         row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
         row1.setColspan(2);
         table.addCell(row1);
         
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("-", small));
         row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
         row1.setColspan(2);
         table.addCell(row1);
         
         
         //new row
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("   ",small));
         row1.setBorder(Rectangle.NO_BORDER);	
         row1.setHorizontalAlignment(Element.ALIGN_CENTER);
         row1.setColspan(6);
         table.addCell(row1);
         
         
         //new row
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("   ",small));
         row1.setBorder(Rectangle.NO_BORDER);	
         row1.setHorizontalAlignment(Element.ALIGN_CENTER);
         row1.setColspan(6);
         table.addCell(row1);
         
  		
  		PdfPTable signatureTable = new PdfPTable(1);
  		int[] arrheaderwidths14 = { 100 }; // percentage
  		signatureTable.getDefaultCell().setBorderWidth(1);
  		signatureTable.getDefaultCell().setPadding(1);

  		signatureTable.setWidths(arrheaderwidths14);
  		
  		PdfPCell cellUvU = new PdfPCell(new Phrase("This is computer generated PaySlip and does not require signature.",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
  		cellUvU.setBorderWidth(1);
  		signatureTable.addCell(cellUvU);
  		
  	       
  	       
   			PdfPTable imageEmpDetailTable = new PdfPTable(1);

   			int[] arrheaderwidths4 = { 100}; // percentage

   			imageEmpDetailTable.getDefaultCell().setBorderWidth(1);
   			imageEmpDetailTable.getDefaultCell().setPadding(1);
   			imageEmpDetailTable.setWidths(arrheaderwidths4);
   			
   			
  			
  //**************************************************************************************************

//  			imageEmpDetailTable.addCell(celld);
//  			document.add(LogoImage);
//  			document.add(titalTable);
//  			document.add(imageEmpDetailTable);
//  			document.add(titleTable);
//  			document.add(Deductiontable);
  			//document.add(netGrossTable);
  			document.add(companyNameTable);
  			document.add(table);
  			document.add(signatureTable);
  			document.close();
   		
  			response.setContentType("application/pdf");
  			response.setContentLength(baos.size());
  			response.setHeader("Content-Disposition","attachment; filename=PaySlip.pdf");
  			ServletOutputStream out = response.getOutputStream();              
  			baos.writeTo(out);
  			out.flush();
  			out.close();
  			baos.close();
  			out.close();
   				
  			return;
   			
   		}catch (Exception e) {
   			e.printStackTrace();
   		}
  	
  	}


	private void viewSalarySlipSixthFormat() {

  		try{
  			ByteArrayOutputStream baos = new ByteArrayOutputStream();
  			Document document = new Document();
  			PdfWriter.getInstance(document, baos);
  			document.open();

  			String filePathCompanyLOgo = null;

  			payEmpHead.add("Emp No.");
  			payEmpHead.add("UAN No.");
  			payEmpHead.add("Name ");
  			payEmpHead.add("Calender Days ");
  			payEmpHead.add("Department ");
  			payEmpHead.add("W.Offs/ Holidays");
  			payEmpHead.add("Designation");
  			payEmpHead.add("Leave Days");
  			payEmpHead.add("Joining Dt");
  			payEmpHead.add("Paid Days");
  			payEmpHead.add("Pan");
  			payEmpHead.add("O T Hrs");
  			
  			salaryHeadName.add("Basic Pay");
  			salaryHeadName.add("Spa All");
  			salaryHeadName.add("H.R.A");
  			salaryHeadName.add("Veriable Allowance");
  			salaryHeadName.add("Conveyance");
  			salaryHeadName.add("MEDICAL ALL");
  			salaryHeadName.add("Over Time Pay");
  			salaryHeadName.add("Inc/Mob/Petrol");
  			salaryHeadName.add("Total Payment");
  			
  			deductionHeadName.add("Prof. Tax ");
  			deductionHeadName.add("P.F (E.C)");
  			deductionHeadName.add("Insurance/Medical Bill");
  			deductionHeadName.add("Advance Salary");
  			deductionHeadName.add("T.D.S. Ded.");
  			deductionHeadName.add("Mise/Hostel/Ele-Bill");
  			deductionHeadName.add("L.W.F.");
  			deductionHeadName.add("Security Deposit");
  			deductionHeadName.add("Deductions ");
  			deductionHeadName.add("Net Payable");
  		
 			filePathCompanyLOgo = request.getRealPath("/userImages/company_avatar_photo1.png");
 			 
 			Image imageLogo=null;
 	
 			try{
 				
 				FileInputStream fileInputStream1=null;
 		        File file1 = new File(filePathCompanyLOgo);
 		        byte[] bFile1 = new byte[(int) file1.length()];
 		        fileInputStream1 = new FileInputStream(file1);
 			    fileInputStream1.read(bFile1);
 			    fileInputStream1.close();
 		        imageLogo = Image.getInstance(bFile1);
 		        
 			}catch(FileNotFoundException e){
 				imageLogo = Image.getInstance(filePathCompanyLOgo);
 			}
  			
  			PdfPTable photoImagetable = new PdfPTable(1);
  			photoImagetable.setWidthPercentage(10);
  			photoImagetable.getDefaultCell().setPadding(1);
  			

  			PdfPTable companyNameTable = new PdfPTable(1);
 			int[] arrheaderwidthsh = { 100 }; // percentage
 			companyNameTable.getDefaultCell().setBorderWidth(0);
 			companyNameTable.setWidths(arrheaderwidthsh);
 			PdfPCell companyNamecell = new PdfPCell(new Phrase("Company Name", FontFactory.getFont("Verdana", 14,Font.BOLD)));
 			

 			companyNamecell.setBorderWidthTop(0);
 			companyNamecell.setBorderWidthBottom(1);
 			companyNamecell.setBorderWidthLeft(0);
 			companyNamecell.setBorderWidthRight(0);

 			companyNamecell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			companyNameTable.addCell(companyNamecell);
 			PdfPCell companyNamecell1 = new PdfPCell(new Phrase("Company Address", FontFactory.getFont("Verdana", 10,Font.BOLD)));

 			companyNamecell1.setBorderWidthTop(0);
 			companyNamecell1.setBorderWidthBottom(0);
 			companyNamecell1.setBorderWidthLeft(0);
 			companyNamecell1.setBorderWidthRight(0);

 			companyNamecell1.setHorizontalAlignment(Element.ALIGN_CENTER);
 			companyNameTable.addCell(companyNamecell1);

 			PdfPTable LogoImage = new PdfPTable(2);
 			int[] arrheaderwidths1 = { 30, 70 }; // percentage
 			LogoImage.getDefaultCell().setBorderWidth(1);
 			LogoImage.setWidths(arrheaderwidths1);
 			LogoImage.setTotalWidth(600); 
 			LogoImage.addCell(imageLogo);
 			LogoImage.addCell(companyNameTable);
 		

 			PdfPTable titalTable = new PdfPTable(1);
 			int[] arrheaderwidths2 = { 100 }; // percentage
 			titalTable.getDefaultCell().setBorderWidth(1);
 			titalTable.getDefaultCell().setPadding(1);
 			titalTable.setWidths(arrheaderwidths2);

  			PdfPCell cellb = new PdfPCell(new Phrase("Month : ",FontFactory.getFont("Verdana", 8, Font.BOLD)));

  			cellb.getExtraParagraphSpace();
  			cellb.setBorderWidth(1);
  			cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
  			titalTable.addCell(cellb);

  			PdfPTable empTable = new PdfPTable(6);

  			int[] arrheaderwidths5 = { 31, 2, 30, 15, 2, 20 }; // percentage
  			empTable.getDefaultCell().setBorderWidth(1);

  			empTable.setWidths(arrheaderwidths5);
  			empTable.setTotalWidth(800);
  			
  			Iterator<String> itr = payEmpHead.iterator();
  			int k = 0;

  			for(; k<payEmpHead.size(); k++){
  			
  				PdfPCell cellE = new PdfPCell(new Phrase(payEmpHead.get(k),FontFactory.getFont("Verdana", 7, Font.BOLD)));
  				
  				cellE.setBorderWidth(0);
  				empTable.addCell(cellE);

  				if(payEmpHead.get(k).isEmpty()){
  					
  					PdfPCell cellcollan = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 7, Font.BOLD)));
  	 				cellcollan.setBorderWidth(0);
  	 				empTable.addCell(cellcollan);
  					
  				}else{
 	 				PdfPCell cellcollan = new PdfPCell(new Phrase(":",FontFactory.getFont("Verdana", 7, Font.BOLD)));
 	 				cellcollan.setBorderWidth(0);
 	 				empTable.addCell(cellcollan);
  				}
  				PdfPCell cellF = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 7, Font.NORMAL)));

  				cellF.setBorderWidth(0);
  				empTable.addCell(cellF);
  			}

  			PdfPTable imageEmpDetailTable = new PdfPTable(1);

  			int[] arrheaderwidths4 = { 100}; // percentage

  			imageEmpDetailTable.getDefaultCell().setBorderWidth(1);
  			imageEmpDetailTable.getDefaultCell().setPadding(1);
  			imageEmpDetailTable.setWidths(arrheaderwidths4);
  			
  			
  			PdfPTable  titleTable = new PdfPTable (1);
 			int[] arrTitle = { 100}; // percentage
 			titleTable.getDefaultCell().setBorderWidth(1);
 			titleTable.getDefaultCell().setPadding(1);
 			titleTable.setWidths(arrTitle);

 			PdfPCell cellT = new PdfPCell(new Phrase("                                      GROSS            NET                DEDUCTION",FontFactory.getFont("Verdana", 9, Font.BOLD)));
 			cellT.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cellT.setBorderWidth(1);
 			titleTable.addCell(cellT);

 			PdfPTable grossTitalTable = new PdfPTable(3);
 			int[] arrheaderwidths7 = { 40,30,30 }; // percentage
 			grossTitalTable.getDefaultCell().setBorderWidth(1);
 			grossTitalTable.getDefaultCell().setPadding(1);
 			grossTitalTable.setWidths(arrheaderwidths7);

 			PdfPTable GrossHeadTable = new PdfPTable(1);
 			int[] arrheaderwidths7A = { 100 }; // percentage
 			GrossHeadTable.getDefaultCell().setBorderWidth(1);
 			GrossHeadTable.getDefaultCell().setPadding(1);
 			GrossHeadTable.setWidths(arrheaderwidths7A);

 			PdfPTable grossAmtTable = new PdfPTable(1);
 			int[] arrheaderwidths7B = { 100 }; // percentage
 			grossAmtTable.getDefaultCell().setBorderWidth(1);
 			grossAmtTable.getDefaultCell().setPadding(1);
 			grossAmtTable.setWidths(arrheaderwidths7B);
 			
 			PdfPTable tableScale = new PdfPTable(1);
 			int[] arrheaderwidths7C = { 100 }; // percentage
 			tableScale.getDefaultCell().setBorderWidth(1);
 			tableScale.getDefaultCell().setPadding(1);
 			tableScale.setWidths(arrheaderwidths7C);

 			int nCount = 0;
 			Iterator<String> itr1 = salaryHeadName.iterator();
 			while (itr1.hasNext()) {

 				String strHeadNM = itr1.next();

 				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
 					
 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQ2.setBorderWidth(0);
 					GrossHeadTable.addCell(cellQ2);
 					
 					PdfPCell cellQS = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQS.setBorderWidth(0);
 					tableScale.addCell(cellQS);

 					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

 					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQ3.setBorderWidthTop(1);
 					cellQ3.setBorderWidthBottom(0);
 					cellQ3.setBorderWidthLeft(0);
 					cellQ3.setBorderWidthRight(0);
 					grossAmtTable.addCell(cellQ3);

 				} else {

 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ2.setBorderWidth(0);
 					GrossHeadTable.addCell(cellQ2);

 					PdfPCell cellQS = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQS.setBorderWidth(0);
 					tableScale.addCell(cellQS);
 					
 					PdfPCell cellQ3 = new PdfPCell(new Phrase(" ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQ3.setBorderWidth(0);
 					grossAmtTable.addCell(cellQ3);
 				}

 				nCount++;

 			}

 			grossTitalTable.addCell(GrossHeadTable);
 			grossTitalTable.addCell(tableScale);
 			grossTitalTable.addCell(grossAmtTable);
 			
 //********************************************************************************************
 			
 		/*	PdfPTable netGrossTable = new PdfPTable(2);
 			int[] arrheaderwidths6A = { 50, 50 }; // percentage
 			netGrossTable.getDefaultCell().setBorderWidth(1);
 			netGrossTable.getDefaultCell().setPadding(1);
 			netGrossTable.setWidths(arrheaderwidths6A);

 			PdfPCell cellQA1 = new PdfPCell(new Phrase("Organisations Contribution ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
 			cellQA1.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cellQA1.setBorderWidth(1);
 			netGrossTable.addCell(cellQA1);

 			
 			PdfPCell cellQA2 = new PdfPCell(new Phrase("Payment By Bank A/C No ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
 			cellQA2.setBorderWidth(1);
 			cellQA2.setHorizontalAlignment(Element.ALIGN_CENTER);
 			netGrossTable.addCell(cellQA2);*/
 			
  //******************************************************************************************			
  			
 			PdfPTable leaveTable = new PdfPTable(2);
 			int[] arrheaderwidths8 = { 60, 40 }; // percentage
 			leaveTable.getDefaultCell().setBorderWidth(1);
 			leaveTable.getDefaultCell().setPadding(1);
 			leaveTable.setWidths(arrheaderwidths8);

 			PdfPTable leaveHeadTable = new PdfPTable(1);
 			int[] arrheaderwidths8A = { 100 }; // percentage
 			leaveHeadTable.getDefaultCell().setBorderWidth(1);
 			leaveHeadTable.getDefaultCell().setPadding(1);
 			leaveHeadTable.setWidths(arrheaderwidths8A);

 			PdfPTable leaveAmtTable = new PdfPTable(1);
 			int[] arrheaderwidths8B = { 100 }; // percentage
 			leaveAmtTable.getDefaultCell().setBorderWidth(1);
 			leaveAmtTable.getDefaultCell().setPadding(1);
 			leaveAmtTable.setWidths(arrheaderwidths8B);

 			int nCountGross = 0;
 			Iterator<String> itr12 = deductionHeadName.iterator();
 			while (itr12.hasNext()) {

 				String strHeadNM = itr12.next();

 				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {

 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQ2.setBorderWidth(0);
 					leaveHeadTable.addCell(cellQ2);

 					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQ3.setBorderWidthTop(1);
 					cellQ3.setBorderWidthBottom(0);
 					cellQ3.setBorderWidthLeft(0);
 					cellQ3.setBorderWidthRight(0);
 					leaveAmtTable.addCell(cellQ3);
 				} else {

 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,
 							FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ2.setBorderWidth(0);
 					leaveHeadTable.addCell(cellQ2);
 					
 					PdfPCell cellQ3 = new PdfPCell(new Phrase(" ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQ3.setBorderWidth(0);
 					leaveAmtTable.addCell(cellQ3);

 				}

 				nCountGross++;

 			}

 			leaveTable.addCell(leaveHeadTable);
 			leaveTable.addCell(leaveAmtTable);

 //**********************************************************************************************	

 			PdfPTable Deductiontable = new PdfPTable(2);
 			int[] arrheaderwidths9 = { 50, 50 }; // percentage
 			Deductiontable.getDefaultCell().setBorderWidth(1);
 			Deductiontable.getDefaultCell().setPadding(1);
 			Deductiontable.setWidths(arrheaderwidths9);

 			PdfPCell cellQ3 = new PdfPCell(grossTitalTable);
 			cellQ3.setBorderWidth(1);
 			Deductiontable.addCell(cellQ3);

 			PdfPCell cellR = new PdfPCell(leaveTable);
 			cellR.setBorderWidth(1);
 			Deductiontable.addCell(cellR);

 			PdfPTable HeadleaveDeductionTable = new PdfPTable(2);
 			int[] arrheaderwidths12 = { 50, 50 }; // percentage
 			HeadleaveDeductionTable.getDefaultCell().setBorderWidth(1);
 			HeadleaveDeductionTable.getDefaultCell().setPadding(1);
 			HeadleaveDeductionTable.setWidths(arrheaderwidths12);

 			PdfPCell celld = new PdfPCell(new PdfPCell(empTable));

 			PdfPTable orgTable = new PdfPTable(3);
 			int[] arrorg = { 30, 20 ,50}; // percentage
 			orgTable.getDefaultCell().setBorderWidth(1);
 			orgTable.getDefaultCell().setPadding(1);
 			orgTable.setWidths(arrorg);

 			PdfPTable orgHeadTable = new PdfPTable(1);
 			int[] arrOrgA = { 100 }; // percentage
 			orgHeadTable.getDefaultCell().setPadding(1);
 			orgHeadTable.setWidths(arrOrgA);

 			PdfPTable orgAmtTable = new PdfPTable(1);
 			int[] arrOrgB = { 100 }; // percentage
 			orgAmtTable.getDefaultCell().setPadding(1);
 			orgAmtTable.setWidths(arrOrgB);
 			
 			PdfPTable orgBlank = new PdfPTable(1);
 			int[] arrOrgC = { 100 }; // percentage
 			orgBlank.getDefaultCell().setBorderWidth(1);
 			orgBlank.getDefaultCell().setPadding(1);
 			orgBlank.setWidths(arrOrgC);

 			List<String> orgHeadAmt = new ArrayList<String>();
 			List<String> orgHeadName = new ArrayList<String>();
 			orgHeadName.add("Employer PF (O.C)");
 			orgHeadName.add("Total (O.C)");
 			
 			orgHeadAmt.add(String.valueOf("0.00"));
 			orgHeadAmt.add(String.valueOf("0.00"));
 			
 			int countOrg = 0;
 			Iterator<String> itrOrg = orgHeadName.iterator();
 			while (itrOrg.hasNext()) {

 				String strHeadNM = itrOrg.next();

 				PdfPCell cellOrg = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 				cellOrg.setBorderWidth(0);
 				orgHeadTable.addCell(cellOrg);
 					
 				PdfPCell cellOrg1 = new PdfPCell(new Phrase(""+ orgHeadAmt.get(countOrg), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 				cellOrg1.setHorizontalAlignment(Element.ALIGN_RIGHT);
 				cellOrg1.setBorderWidth(0);
 				orgAmtTable.addCell(cellOrg1);
 				
 				countOrg++;

 			}
 			
 			PdfPCell cellOrg2 = new PdfPCell(new Phrase("Remark : ",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 			cellOrg2.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cellOrg2.setBorderWidth(0);
 			orgBlank.addCell(cellOrg2);
 			
 			orgTable.addCell(orgHeadTable);
 			orgTable.addCell(orgAmtTable);
 			orgTable.addCell(orgBlank);
 			
 //***************************************************************************************************
 			
 			PdfPTable bottomTable = new PdfPTable(1);
 			int[] arrbottomheaderwidths = { 100 }; // percentage
 			titalTable.getDefaultCell().setBorderWidth(1);
 			titalTable.getDefaultCell().setPadding(1);
 			titalTable.setWidths(arrbottomheaderwidths);

  			PdfPCell cellbottom = new PdfPCell(new Phrase("This is Computer generated Slip so does not required any signature",FontFactory.getFont("Verdana", 8, Font.BOLD)));

  			cellb.getExtraParagraphSpace();
  			cellb.setBorderWidth(1);
  			cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
  			bottomTable.addCell(cellbottom);
 			
 //**************************************************************************************************

 			imageEmpDetailTable.addCell(celld);
 			document.add(LogoImage);
 			document.add(titalTable);
 			document.add(imageEmpDetailTable);
 			document.add(titleTable);
 			document.add(Deductiontable);
 			//document.add(netGrossTable);
 			document.add(HeadleaveDeductionTable);
 			//document.add(orgTable);
 			document.add(bottomTable);
 			document.close();
  		
 			response.setContentType("application/pdf");
 			response.setContentLength(baos.size());
 			response.setHeader("Content-Disposition","attachment; filename=PaySlip.pdf");
 			ServletOutputStream out = response.getOutputStream();              
 			baos.writeTo(out);
 			out.flush();
 			out.close();
 			baos.close();
 			out.close();
  				
 			return;
  			
  		}catch (Exception e) {
  			e.printStackTrace();
  		}
 	
 	}
     
     
     
     
     
	private void viewSalarySlipFifthFormat() {

 		try{
 			ByteArrayOutputStream baos = new ByteArrayOutputStream();
 			Document document = new Document();
 			PdfWriter.getInstance(document, baos);
 			document.open();

 			String filePathCompanyLOgo = null;

 			payEmpHead.add("Emp No.");
			payEmpHead.add("");
			payEmpHead.add("Name");
			payEmpHead.add("Calender Days");
			payEmpHead.add("Bank Name ");
//			payEmpHead.add("Department ");
			payEmpHead.add("A/C No.");
//			payEmpHead.add("W.Offs/ Holidays");
			payEmpHead.add("Designation");
			payEmpHead.add("Leave Days");
			payEmpHead.add("Joining Dt");
			payEmpHead.add("Paid Days");
			payEmpHead.add("Pan");
			payEmpHead.add("PF No.");
//			payEmpHead.add("OT Hrs");
			
 			salaryHeadName.add("Basic Pay");
 			salaryHeadName.add("Spa All");
 			salaryHeadName.add("H.R.A");
 			salaryHeadName.add("Veriable Allowance");
 			salaryHeadName.add("Conveyance");
 			salaryHeadName.add("MEDICAL ALL");
 			salaryHeadName.add("Over Time Pay");
 			salaryHeadName.add("Inc/Mob/Petrol");
 			salaryHeadName.add("Total Payment");
 			
 			deductionHeadName.add("Prof. Tax ");
 			deductionHeadName.add("P.F (E.C)");
 			deductionHeadName.add("Insurance/Medical Bill");
 			deductionHeadName.add("Advance Salary");
 			deductionHeadName.add("T.D.S. Ded.");
 			deductionHeadName.add("Mise/Hostel/Ele-Bill");
 			deductionHeadName.add("L.W.F.");
 			deductionHeadName.add("Security Deposit");
 			deductionHeadName.add("Deductions ");
 			deductionHeadName.add("Net Payable");
 		
			filePathCompanyLOgo = request.getRealPath("/userImages/company_avatar_photo1.png");
			 
			Image imageLogo=null;
	
			try{
				
				FileInputStream fileInputStream1=null;
		        File file1 = new File(filePathCompanyLOgo);
		        byte[] bFile1 = new byte[(int) file1.length()];
		        fileInputStream1 = new FileInputStream(file1);
			    fileInputStream1.read(bFile1);
			    fileInputStream1.close();
		        imageLogo = Image.getInstance(bFile1);
		        
			}catch(FileNotFoundException e){
				imageLogo = Image.getInstance(filePathCompanyLOgo);
			}
 			
 			PdfPTable photoImagetable = new PdfPTable(1);
 			photoImagetable.setWidthPercentage(10);
 			photoImagetable.getDefaultCell().setPadding(1);
 			

 			PdfPTable companyNameTable = new PdfPTable(1);
			int[] arrheaderwidthsh = { 100 }; // percentage
			companyNameTable.getDefaultCell().setBorderWidth(0);
			companyNameTable.setWidths(arrheaderwidthsh);
			PdfPCell companyNamecell = new PdfPCell(new Phrase("Company Name", FontFactory.getFont("Verdana", 14,Font.BOLD)));
			

			companyNamecell.setBorderWidthTop(0);
			companyNamecell.setBorderWidthBottom(1);
			companyNamecell.setBorderWidthLeft(0);
			companyNamecell.setBorderWidthRight(0);

			companyNamecell.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell);
			PdfPCell companyNamecell1 = new PdfPCell(new Phrase("Company Address", FontFactory.getFont("Verdana", 10,Font.BOLD)));

			companyNamecell1.setBorderWidthTop(0);
			companyNamecell1.setBorderWidthBottom(0);
			companyNamecell1.setBorderWidthLeft(0);
			companyNamecell1.setBorderWidthRight(0);

			companyNamecell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell1);

			PdfPTable LogoImage = new PdfPTable(2);
			int[] arrheaderwidths1 = { 30, 70 }; // percentage
			LogoImage.getDefaultCell().setBorderWidth(1);
			LogoImage.setWidths(arrheaderwidths1);
			LogoImage.setTotalWidth(600); 
			LogoImage.addCell(imageLogo);
			LogoImage.addCell(companyNameTable);
		

			PdfPTable titalTable = new PdfPTable(1);
			int[] arrheaderwidths2 = { 100 }; // percentage
			titalTable.getDefaultCell().setBorderWidth(1);
			titalTable.getDefaultCell().setPadding(1);
			titalTable.setWidths(arrheaderwidths2);

 			PdfPCell cellb = new PdfPCell(new Phrase("Salary for month: ",FontFactory.getFont("Verdana", 8, Font.BOLD)));

 			cellb.getExtraParagraphSpace();
 			cellb.setBorderWidth(1);
 			cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
 			titalTable.addCell(cellb);

 			PdfPTable empTable = new PdfPTable(6);

 			int[] arrheaderwidths5 = { 31, 2, 30, 15, 2, 20 }; // percentage
 			empTable.getDefaultCell().setBorderWidth(1);

 			empTable.setWidths(arrheaderwidths5);
 			empTable.setTotalWidth(800);
 			
 			Iterator<String> itr = payEmpHead.iterator();
 			int k = 0;

 			for(; k<payEmpHead.size(); k++){
 			
 				PdfPCell cellE = new PdfPCell(new Phrase(payEmpHead.get(k),FontFactory.getFont("Verdana", 7, Font.BOLD)));
 				
 				cellE.setBorderWidth(0);
 				empTable.addCell(cellE);

 				if(payEmpHead.get(k).isEmpty()){
 					
 					PdfPCell cellcollan = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 7, Font.BOLD)));
 	 				cellcollan.setBorderWidth(0);
 	 				empTable.addCell(cellcollan);
 					
 				}else{
	 				PdfPCell cellcollan = new PdfPCell(new Phrase(":",FontFactory.getFont("Verdana", 7, Font.BOLD)));
	 				cellcollan.setBorderWidth(0);
	 				empTable.addCell(cellcollan);
 				}
 				PdfPCell cellF = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 7, Font.NORMAL)));

 				cellF.setBorderWidth(0);
 				empTable.addCell(cellF);
 			}

 			PdfPTable imageEmpDetailTable = new PdfPTable(1);

 			int[] arrheaderwidths4 = { 100}; // percentage

 			imageEmpDetailTable.getDefaultCell().setBorderWidth(1);
 			imageEmpDetailTable.getDefaultCell().setPadding(1);
 			imageEmpDetailTable.setWidths(arrheaderwidths4);
 			
 			
 			PdfPTable  titleTable = new PdfPTable (1);
			int[] arrTitle = { 100}; // percentage
			titleTable.getDefaultCell().setBorderWidth(1);
			titleTable.getDefaultCell().setPadding(1);
			titleTable.setWidths(arrTitle);

			PdfPCell cellT = new PdfPCell(new Phrase("                                      SCALE         PAYMENTS        DEDUCTION",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellT.setHorizontalAlignment(Element.ALIGN_LEFT);
			cellT.setBorderWidth(1);
			titleTable.addCell(cellT);

			PdfPTable grossTitalTable = new PdfPTable(3);
			int[] arrheaderwidths7 = { 40,30,30 }; // percentage
			grossTitalTable.getDefaultCell().setBorderWidth(1);
			grossTitalTable.getDefaultCell().setPadding(1);
			grossTitalTable.setWidths(arrheaderwidths7);

			PdfPTable GrossHeadTable = new PdfPTable(1);
			int[] arrheaderwidths7A = { 100 }; // percentage
			GrossHeadTable.getDefaultCell().setBorderWidth(1);
			GrossHeadTable.getDefaultCell().setPadding(1);
			GrossHeadTable.setWidths(arrheaderwidths7A);

			PdfPTable grossAmtTable = new PdfPTable(1);
			int[] arrheaderwidths7B = { 100 }; // percentage
			grossAmtTable.getDefaultCell().setBorderWidth(1);
			grossAmtTable.getDefaultCell().setPadding(1);
			grossAmtTable.setWidths(arrheaderwidths7B);
			
			PdfPTable tableScale = new PdfPTable(1);
			int[] arrheaderwidths7C = { 100 }; // percentage
			tableScale.getDefaultCell().setBorderWidth(1);
			tableScale.getDefaultCell().setPadding(1);
			tableScale.setWidths(arrheaderwidths7C);

			int nCount = 0;
			Iterator<String> itr1 = salaryHeadName.iterator();
			while (itr1.hasNext()) {

				String strHeadNM = itr1.next();

				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
					
					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
					GrossHeadTable.addCell(cellQ2);
					
					PdfPCell cellQS = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQS.setBorderWidth(0);
					tableScale.addCell(cellQS);

					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidthTop(1);
					cellQ3.setBorderWidthBottom(0);
					cellQ3.setBorderWidthLeft(0);
					cellQ3.setBorderWidthRight(0);
					grossAmtTable.addCell(cellQ3);

				} else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					GrossHeadTable.addCell(cellQ2);

					PdfPCell cellQS = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQS.setBorderWidth(0);
					tableScale.addCell(cellQS);
					
					PdfPCell cellQ3 = new PdfPCell(new Phrase(" ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidth(0);
					grossAmtTable.addCell(cellQ3);
				}

				nCount++;

			}

			grossTitalTable.addCell(GrossHeadTable);
			grossTitalTable.addCell(tableScale);
			grossTitalTable.addCell(grossAmtTable);
			
//********************************************************************************************
			
			PdfPTable netGrossTable = new PdfPTable(2);
			int[] arrheaderwidths6A = { 50, 50 }; // percentage
			netGrossTable.getDefaultCell().setBorderWidth(1);
			netGrossTable.getDefaultCell().setPadding(1);
			netGrossTable.setWidths(arrheaderwidths6A);

			PdfPCell cellQA1 = new PdfPCell(new Phrase("Organisations Contribution ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellQA1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellQA1.setBorderWidth(1);
			netGrossTable.addCell(cellQA1);

			
			PdfPCell cellQA2 = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellQA2.setBorderWidth(1);
			cellQA2.setHorizontalAlignment(Element.ALIGN_CENTER);
			netGrossTable.addCell(cellQA2);
			
 //******************************************************************************************			
 			
			PdfPTable leaveTable = new PdfPTable(2);
			int[] arrheaderwidths8 = { 60, 40 }; // percentage
			leaveTable.getDefaultCell().setBorderWidth(1);
			leaveTable.getDefaultCell().setPadding(1);
			leaveTable.setWidths(arrheaderwidths8);

			PdfPTable leaveHeadTable = new PdfPTable(1);
			int[] arrheaderwidths8A = { 100 }; // percentage
			leaveHeadTable.getDefaultCell().setBorderWidth(1);
			leaveHeadTable.getDefaultCell().setPadding(1);
			leaveHeadTable.setWidths(arrheaderwidths8A);

			PdfPTable leaveAmtTable = new PdfPTable(1);
			int[] arrheaderwidths8B = { 100 }; // percentage
			leaveAmtTable.getDefaultCell().setBorderWidth(1);
			leaveAmtTable.getDefaultCell().setPadding(1);
			leaveAmtTable.setWidths(arrheaderwidths8B);

			int nCountGross = 0;
			Iterator<String> itr12 = deductionHeadName.iterator();
			while (itr12.hasNext()) {

				String strHeadNM = itr12.next();

				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
					leaveHeadTable.addCell(cellQ2);

					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidthTop(1);
					cellQ3.setBorderWidthBottom(0);
					cellQ3.setBorderWidthLeft(0);
					cellQ3.setBorderWidthRight(0);
					leaveAmtTable.addCell(cellQ3);
				} else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,
							FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					leaveHeadTable.addCell(cellQ2);
					
					PdfPCell cellQ3 = new PdfPCell(new Phrase(" ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidth(0);
					leaveAmtTable.addCell(cellQ3);

				}

				nCountGross++;

			}

			leaveTable.addCell(leaveHeadTable);
			leaveTable.addCell(leaveAmtTable);

//**********************************************************************************************	

			PdfPTable Deductiontable = new PdfPTable(2);
			int[] arrheaderwidths9 = { 50, 50 }; // percentage
			Deductiontable.getDefaultCell().setBorderWidth(1);
			Deductiontable.getDefaultCell().setPadding(1);
			Deductiontable.setWidths(arrheaderwidths9);

			PdfPCell cellQ3 = new PdfPCell(grossTitalTable);
			cellQ3.setBorderWidth(1);
			Deductiontable.addCell(cellQ3);

			PdfPCell cellR = new PdfPCell(leaveTable);
			cellR.setBorderWidth(1);
			Deductiontable.addCell(cellR);

			PdfPTable HeadleaveDeductionTable = new PdfPTable(2);
			int[] arrheaderwidths12 = { 50, 50 }; // percentage
			HeadleaveDeductionTable.getDefaultCell().setBorderWidth(1);
			HeadleaveDeductionTable.getDefaultCell().setPadding(1);
			HeadleaveDeductionTable.setWidths(arrheaderwidths12);

			PdfPCell celld = new PdfPCell(new PdfPCell(empTable));

			PdfPTable orgTable = new PdfPTable(3);
			int[] arrorg = { 30, 20 ,50}; // percentage
			orgTable.getDefaultCell().setBorderWidth(1);
			orgTable.getDefaultCell().setPadding(1);
			orgTable.setWidths(arrorg);

			PdfPTable orgHeadTable = new PdfPTable(1);
			int[] arrOrgA = { 100 }; // percentage
			orgHeadTable.getDefaultCell().setPadding(1);
			orgHeadTable.setWidths(arrOrgA);

			PdfPTable orgAmtTable = new PdfPTable(1);
			int[] arrOrgB = { 100 }; // percentage
			orgAmtTable.getDefaultCell().setPadding(1);
			orgAmtTable.setWidths(arrOrgB);
			
			PdfPTable orgBlank = new PdfPTable(1);
			int[] arrOrgC = { 100 }; // percentage
			orgBlank.getDefaultCell().setBorderWidth(1);
			orgBlank.getDefaultCell().setPadding(1);
			orgBlank.setWidths(arrOrgC);

			List<String> orgHeadAmt = new ArrayList<String>();
			List<String> orgHeadName = new ArrayList<String>();
			orgHeadName.add("Employer PF (O.C)");
			orgHeadName.add("Total (O.C)");
			
			orgHeadAmt.add(String.valueOf("0.00"));
			orgHeadAmt.add(String.valueOf("0.00"));
			
			int countOrg = 0;
			Iterator<String> itrOrg = orgHeadName.iterator();
			while (itrOrg.hasNext()) {

				String strHeadNM = itrOrg.next();

				PdfPCell cellOrg = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellOrg.setBorderWidth(0);
				orgHeadTable.addCell(cellOrg);
					
				PdfPCell cellOrg1 = new PdfPCell(new Phrase(""+ orgHeadAmt.get(countOrg), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellOrg1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellOrg1.setBorderWidth(0);
				orgAmtTable.addCell(cellOrg1);
				
				countOrg++;

			}
			
			PdfPCell cellOrg2 = new PdfPCell(new Phrase("Remark : ",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellOrg2.setHorizontalAlignment(Element.ALIGN_LEFT);
			cellOrg2.setBorderWidth(0);
			orgBlank.addCell(cellOrg2);
			
			orgTable.addCell(orgHeadTable);
			orgTable.addCell(orgAmtTable);
			orgTable.addCell(orgBlank);
			
//***************************************************************************************************
			
			PdfPTable LastTable = new PdfPTable(1);
			LastTable.getDefaultCell().setBorderWidth(0);
			LastTable.getDefaultCell().setPadding(0);
			LastTable.setWidths(arrheaderwidths2);

			PdfPCell cellSign = new PdfPCell(new Phrase("This is Computer generated Slip so does not required any signature",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellSign.getExtraParagraphSpace();
			cellSign.setBorderWidth(1);
			cellSign.setHorizontalAlignment(Element.ALIGN_CENTER);
			LastTable.addCell(cellSign);
			
			/*PdfPTable LastTable = new PdfPTable(2);
			int[] arrNo = { 50, 50 }; // percentage
			LastTable.getDefaultCell().setBorderWidth(1);
			LastTable.getDefaultCell().setPadding(1);
			LastTable.setWidths(arrNo);
			
			PdfPCell cellNo = new PdfPCell(new Phrase("TOLL FREE NO: 180 266 2006 ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellNo.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellNo.setBorderWidth(1);
			LastTable.addCell(cellNo);
			
			PdfPCell cellSign = new PdfPCell(new Phrase("This is Computer generated Slip so does not required any signature",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellSign.setBorderWidth(1);
			cellSign.setHorizontalAlignment(Element.ALIGN_CENTER);
			LastTable.addCell(cellSign);*/
			
//**************************************************************************************************

			imageEmpDetailTable.addCell(celld);
			document.add(LogoImage);
			document.add(titalTable);
			document.add(imageEmpDetailTable);
			document.add(titleTable);
			document.add(Deductiontable);
			document.add(netGrossTable);
			document.add(HeadleaveDeductionTable);
			document.add(orgTable);
			document.add(LastTable);
			document.close();
 		
			response.setContentType("application/pdf");
			response.setContentLength(baos.size());
			response.setHeader("Content-Disposition","attachment; filename=PaySlip.pdf");
			ServletOutputStream out = response.getOutputStream();              
			baos.writeTo(out);
			out.flush();
			out.close();
			baos.close();
			out.close();
 				
			return;
 			
 		}catch (Exception e) {
 			e.printStackTrace();
 		}
	
	}


	private void viewSalarySlipFourthFormat() {

 		try{
 			int count =0;
 			com.itextpdf.text.Font heading = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 13);
 			com.itextpdf.text.Font normal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11);
 			com.itextpdf.text.Font normalwithbold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
 			com.itextpdf.text.Font small = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7);
 			com.itextpdf.text.Font smallBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7,Font.BOLD);
 			com.itextpdf.text.Font italicEffect = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
 			
 			
 	        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
 	        com.itextpdf.text.pdf.PdfWriter.getInstance(document,buffer);
 	        document.open();
 	        
 	        while(count <2)
 	        {
 	        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(16);
 			table.setWidthPercentage(100);       
 			int[] cols = {10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10};
 			table.setWidths(cols);
 	        
 			
 			com.itextpdf.text.pdf.PdfPCell row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Company Name :",smallBold));
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.RIGHT |Rectangle.BOTTOM|Rectangle.TOP);
 	        row1.setColspan(5);
 	        row1.setPadding(2.5f);
 	        table.addCell(row1); 
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Pay Slip For:",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1); 
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Desig. :",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(6);
	        row1.setPadding(2.5f);
	        table.addCell(row1); 
 	      
 	      //New Row
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Emp No : ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.NO_BORDER);
 	        row1.setColspan(4);	        
 	        table.addCell(row1);
 	      
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Emp Name : ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.NO_BORDER);
 	        row1.setColspan(4);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("PF No. : ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(4);	
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Dept : ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.NO_BORDER);
 	        row1.setColspan(4);	
 	        table.addCell(row1);
 	        
 	       //New row
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Present Days",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.TOP |Rectangle.BOTTOM);
 	        row1.setColspan(2);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("W/Off ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.TOP |Rectangle.BOTTOM);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("C/Off",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.TOP |Rectangle.BOTTOM);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("EL",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.TOP |Rectangle.BOTTOM);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("SL",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.TOP |Rectangle.BOTTOM);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("CL",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.TOP |Rectangle.BOTTOM);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("PH",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.TOP |Rectangle.BOTTOM);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	       
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total Days",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.TOP |Rectangle.BOTTOM);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("QB Days",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.TOP |Rectangle.BOTTOM);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Earned Hrs ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.TOP |Rectangle.BOTTOM);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Effi Hrs",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.TOP |Rectangle.BOTTOM);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" A ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.TOP |Rectangle.BOTTOM);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" B ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.TOP |Rectangle.BOTTOM);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" C ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.TOP |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(2);	
 	        table.addCell(row1);
 	        
 	      //New Row  
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(2);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("  ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("  ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("0",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("0.00",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("0.00",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("0",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("0",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(1);	
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("0",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.LEFT |Rectangle.BOTTOM |Rectangle.RIGHT);
 	        row1.setColspan(2);	
 	        table.addCell(row1);
 	        
 	      //New Row  
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(3);	        
	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total Earnings",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
 	        row1.setColspan(3);	        
 	        table.addCell(row1);
 	        
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Actual Payment",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
 	        row1.setColspan(3);	 
 	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(3);	        
	        table.addCell(row1);
 	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total Deduction",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
 	        row1.setColspan(4);	 
 	        table.addCell(row1);
 	        
 	        //New Row
 	        row1 =new PdfPCell(new Paragraph("Besic Salary",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	        
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph("Provident Fund",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	        
	        table.addCell(row1);
 	     
	        //New Row
 	        row1 =new PdfPCell(new Paragraph("D A",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	 
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph("ESIC",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	        
 	        //New Row
 	        row1 =new PdfPCell(new Paragraph("Eff Bonus",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	         
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph("TDS",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	         
	        table.addCell(row1);
 	     
	        //New Row
 	        row1 =new PdfPCell(new Paragraph("HRA",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	 
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph("Professional Tax",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	
 	        //New Row
 	        row1 =new PdfPCell(new Paragraph("Conv. Allowance",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	        
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph("Advance",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	        
	        table.addCell(row1);
 	     
	        //New Row
 	        row1 =new PdfPCell(new Paragraph("Educational Allowance",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	 
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph("Transport Deduction",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	      
 	      //New Row
 	        row1 =new PdfPCell(new Paragraph("Medical Allowance",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	        
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph("Credit Society",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	        
	        table.addCell(row1); 
 	     
	      //New Row
 	        row1 =new PdfPCell(new Paragraph("Transport Allowance",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	 
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph("Union Contribution",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	        
 	      //New Row
 	        row1 =new PdfPCell(new Paragraph("Personal Allowance",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	        
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph("LWF",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	        
	        table.addCell(row1);
 	     
	      //New Row
 	        row1 =new PdfPCell(new Paragraph("L. T. A",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	 
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph("Other Deduction",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	        
 	      //New Row
 	        row1 =new PdfPCell(new Paragraph("E. H. I",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	        
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph("Bank Deduction",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	        
	        table.addCell(row1);
 	     
	      //New Row
 	        row1 =new PdfPCell(new Paragraph("Quality Bonus",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	 
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	        
 	      //New Row
 	        row1 =new PdfPCell(new Paragraph("Attn Bonus",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	        
 	        table.addCell(row1);
 	       
 	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	     
 	       //New Row
 	        row1 =new PdfPCell(new Paragraph("Telephone Allowance",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	 
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	        
 	        //New Row
 	        row1 =new PdfPCell(new Paragraph("W S A",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	        
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	        
 	        //New Row
 	        row1 =new PdfPCell(new Paragraph("U P I",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	        
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	        
 	        //New Row
 	        row1 =new PdfPCell(new Paragraph("Line Target",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	        
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	        
 	        //New Row
 	        row1 =new PdfPCell(new Paragraph("VA Bonus",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	        
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	        
 	        //New Row
 	        row1 =new PdfPCell(new Paragraph("PPM",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	        
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	        
 	        //New Row
 	        row1 =new PdfPCell(new Paragraph("Other Income",small));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.RIGHT);
 	        row1.setColspan(8);	        
 	        table.addCell(row1);
 	        
 	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	    
 	        //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_MIDDLE);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(8);	 
	        table.addCell(row1);
	        
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Gross Deduction : ",smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_MIDDLE);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(8);	 
	        table.addCell(row1);
 	        
	        //New Row
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Gross Earning  : ",smallBold));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
 	        row1.setColspan(8);	        
 	        table.addCell(row1);
 	       
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Net Pay: ",smallBold));
 	        row1.setPadding(2.5f);
 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
 	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
 	        row1.setColspan(8);	 
 	        table.addCell(row1);
 	        
 	       //New Row
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(16);	 
	        table.addCell(row1);
	        
	        //New Row
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(16);	 
	        table.addCell(row1);
	        
 	        //New Row
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(11);	 
	        table.addCell(row1);
	      
 	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Authorised Signatory",smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	 
	        table.addCell(row1);
 	      
 	        
	        document.newPage();
 	        document.add(table);
 	        count++;
 	        }
 	        
 	        document.close();
 			
 			String filename="SalarySlip.pdf";
 			response.setContentType("application/pdf");
 			response.setContentLength(buffer.size());
 			response.setHeader("Content-Disposition","attachment; filename="+filename+"");
 			ServletOutputStream out = response.getOutputStream();
 			buffer.writeTo(out);
 			out.flush();
 			
 	}catch (Exception e) {
 		e.printStackTrace();  
 	}
 	
}


	private void viewSalarySlipThirdFormat() {
 		try
 		{
 			ByteArrayOutputStream baos = new ByteArrayOutputStream();
 			Document document = new Document();
 			PdfWriter.getInstance(document, baos);
 			document.open();

 			String filePathCompanyLOgo = null;

 			payEmpHead.add("EMPLOYEE NAME");
 			payEmpHead.add("DESIGNATION");
 			payEmpHead.add("EMPLOYEE CODE");
 			payEmpHead.add("ACC. NO.");
 			payEmpHead.add("PF ACC. NO.");
 			payEmpHead.add("BRANCH");
 			payEmpHead.add("JOINING DATE");
 			payEmpHead.add("DEPARTMENT");
 			payEmpHead.add("PAN NO");
 			payEmpHead.add("MONTH");
 			payEmpHead.add("UAN NO.");
 			payEmpHead.add("");
 			
 			salaryHeadName.add("OTHER ALLOWANCE/EX-");
 			salaryHeadName.add("GRATIA");
 			salaryHeadName.add("KRA Allowance Fixed");
 			salaryHeadName.add("Special Allowance");
 			salaryHeadName.add("Basic");
 			salaryHeadName.add("DA");
 			salaryHeadName.add("HRA");
 			salaryHeadName.add("Conveyance");
 			salaryHeadName.add("Overtime");
 			salaryHeadName.add("Incentive");
 			salaryHeadName.add("Mobile Reimbursement");
 			salaryHeadName.add("Travel Reimbursement");
 			salaryHeadName.add("Other Reimbursement");
 			salaryHeadName.add("Medical Allowance");
 			salaryHeadName.add("Education Allowance");
 			salaryHeadName.add("Total");
 			
 			deductionHeadName.add("Professional Tax");
 			deductionHeadName.add("Employee PF");
 			deductionHeadName.add("Voluntary PF");
 			deductionHeadName.add("Mobile Recovery");
 			deductionHeadName.add("Mediclaim Deduction");
 			deductionHeadName.add("Total Deduction");
 		
			filePathCompanyLOgo = request.getRealPath("/userImages/company_avatar_photo1.png");
			 
			Image imageLogo=null;
	
			try{
				
				FileInputStream fileInputStream1=null;
		        File file1 = new File(filePathCompanyLOgo);
		        byte[] bFile1 = new byte[(int) file1.length()];
		        fileInputStream1 = new FileInputStream(file1);
			    fileInputStream1.read(bFile1);
			    fileInputStream1.close();
		        imageLogo = Image.getInstance(bFile1);
		        
			}catch(FileNotFoundException e){
				imageLogo = Image.getInstance(filePathCompanyLOgo);
			}
 			
 			PdfPTable photoImagetable = new PdfPTable(1);
 			photoImagetable.setWidthPercentage(10);
 			photoImagetable.getDefaultCell().setPadding(1);
 			

 			PdfPTable companyNameTable = new PdfPTable(1);
			int[] arrheaderwidthsh = { 100 }; // percentage
			companyNameTable.getDefaultCell().setBorderWidth(0);
			companyNameTable.setWidths(arrheaderwidthsh);
			PdfPCell companyNamecell = new PdfPCell(new Phrase("Company Name", FontFactory.getFont("Verdana", 14,Font.BOLD)));
			
			companyNamecell.setBorderWidthTop(0);
			companyNamecell.setBorderWidthBottom(1);
			companyNamecell.setBorderWidthLeft(0);
			companyNamecell.setBorderWidthRight(0);

			companyNamecell.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell);
			PdfPCell companyNamecell1 = new PdfPCell(new Phrase("Company Address", FontFactory.getFont("Verdana", 10,Font.BOLD)));

			companyNamecell1.setBorderWidthTop(0);
			companyNamecell1.setBorderWidthBottom(0);
			companyNamecell1.setBorderWidthLeft(0);
			companyNamecell1.setBorderWidthRight(0);

			companyNamecell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell1);

			PdfPTable LogoImage = new PdfPTable(2);
			int[] arrheaderwidths1 = { 70, 30 }; // percentage
			LogoImage.getDefaultCell().setBorderWidth(1);
			LogoImage.setWidths(arrheaderwidths1);
			LogoImage.setTotalWidth(600); 
			LogoImage.addCell(companyNameTable);
			LogoImage.addCell(imageLogo);

			PdfPTable titalTable = new PdfPTable(1);
			int[] arrheaderwidths2 = { 100 }; // percentage
			titalTable.getDefaultCell().setBorderWidth(1);
			titalTable.getDefaultCell().setPadding(1);
			titalTable.setWidths(arrheaderwidths2);

 			PdfPCell cellb = new PdfPCell(new Phrase(" PAY SLIP ",FontFactory.getFont("Verdana", 15, Font.BOLD)));

 			cellb.getExtraParagraphSpace();
 			cellb.setBorderWidth(1);
 			cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
 			titalTable.addCell(cellb);

 			PdfPTable empTable = new PdfPTable(6);

 			int[] arrheaderwidths5 = { 31, 2, 30, 15, 2, 20 }; // percentage
 			empTable.getDefaultCell().setBorderWidth(1);

 			empTable.setWidths(arrheaderwidths5);
 			empTable.setTotalWidth(800);
 			
 			Iterator<String> itr = payEmpHead.iterator();
 			int k = 0;
 			
 			for(; k<payEmpHead.size(); k++){
 			
 				PdfPCell cellE = new PdfPCell(new Phrase(payEmpHead.get(k),FontFactory.getFont("Verdana", 7, Font.BOLD)));
 				
 				cellE.setBorderWidth(0);
 				empTable.addCell(cellE);

 				if(payEmpHead.get(k).isEmpty()){
	 				PdfPCell cellcollan = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 7, Font.BOLD)));
	 				cellcollan.setBorderWidth(0);
	 				empTable.addCell(cellcollan);
 				}else{
 					PdfPCell cellcollan = new PdfPCell(new Phrase(":",FontFactory.getFont("Verdana", 7, Font.BOLD)));
 	 				cellcollan.setBorderWidth(0);
 	 				empTable.addCell(cellcollan);
 				}
 				PdfPCell cellF = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 7, Font.NORMAL)));

 				cellF.setBorderWidth(0);
 				empTable.addCell(cellF);
 			}

 			PdfPTable imageEmpDetailTable = new PdfPTable(1);

 			int[] arrheaderwidths4 = { 100}; // percentage

 			imageEmpDetailTable.getDefaultCell().setBorderWidth(1);
 			imageEmpDetailTable.getDefaultCell().setPadding(1);
 			imageEmpDetailTable.setWidths(arrheaderwidths4);

 			PdfPTable netGrossTital = new PdfPTable(2);
 			int[] arrheaderwidths6 = { 50, 50 }; // percentage
 			netGrossTital.getDefaultCell().setBorderWidth(1);
 			netGrossTital.getDefaultCell().setPadding(1);
 			netGrossTital.setWidths(arrheaderwidths6);

 			PdfPCell cellQ = new PdfPCell(new Phrase("GROSS EARNINGS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
 			cellQ.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cellQ.setBorderWidth(1);
 			netGrossTital.addCell(cellQ);

 			PdfPCell cellQ1 = new PdfPCell(new Phrase("NET EARNINGS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
 			cellQ1.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cellQ1.setBorderWidth(1);
 			netGrossTital.addCell(cellQ1);

 			PdfPTable grossTitalTable = new PdfPTable(2);

 			int[] arrheaderwidths7 = { 60, 40 }; // percentage
 			grossTitalTable.getDefaultCell().setBorderWidth(1);
 			grossTitalTable.getDefaultCell().setPadding(1);
 			grossTitalTable.setWidths(arrheaderwidths7);

 			PdfPTable GrossHeadTable = new PdfPTable(1);
 			int[] arrheaderwidths7A = { 100 }; // percentage
 			GrossHeadTable.getDefaultCell().setBorderWidth(1);
 			GrossHeadTable.getDefaultCell().setPadding(1);
 			GrossHeadTable.setWidths(arrheaderwidths7A);

 			PdfPTable grossAmtTable = new PdfPTable(1);
 			int[] arrheaderwidths7B = { 100 }; // percentage
 			grossAmtTable.getDefaultCell().setBorderWidth(1);
 			grossAmtTable.getDefaultCell().setPadding(1);
 			grossAmtTable.setWidths(arrheaderwidths7B);

 			int nCount = 0;
 			Iterator<String> itr1 = salaryHeadName.iterator();
 			while (itr1.hasNext()) {

 				String strHeadNM = itr1.next();

 				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQ2.setBorderWidth(0);
 					GrossHeadTable.addCell(cellQ2);

 				} else {

 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ2.setBorderWidth(0);
 					GrossHeadTable.addCell(cellQ2);

 					PdfPCell cellQ3 = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQ3.setBorderWidth(0);
 					grossAmtTable.addCell(cellQ3);
 				}

 				nCount++;

 			}

 			grossTitalTable.addCell(GrossHeadTable);
 			grossTitalTable.addCell(grossAmtTable);

 			PdfPTable netGrossTable = new PdfPTable(2);
 			int[] arrheaderwidths6A = { 50, 50 }; // percentage
 			netGrossTable.getDefaultCell().setBorderWidth(1);
 			netGrossTable.getDefaultCell().setPadding(1);
 			netGrossTable.setWidths(arrheaderwidths6A);

 			PdfPCell cellQA1 = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

 			cellQA1.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cellQA1.setBorderWidth(1);
 			netGrossTable.addCell(cellQA1);

 			PdfPCell cellQA2 = new PdfPCell(new Phrase(" DEDUCTIONS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

 			cellQA2.setBorderWidth(1);
 			cellQA2.setHorizontalAlignment(Element.ALIGN_CENTER);
 			netGrossTable.addCell(cellQA2);

 			PdfPTable leaveTable = new PdfPTable(2);
 			int[] arrheaderwidths8 = { 60, 40 }; // percentage
 			leaveTable.getDefaultCell().setBorderWidth(1);
 			leaveTable.getDefaultCell().setPadding(1);
 			leaveTable.setWidths(arrheaderwidths8);

 			PdfPTable leaveHeadTable = new PdfPTable(1);
 			int[] arrheaderwidths8A = { 100 }; // percentage
 			leaveHeadTable.getDefaultCell().setBorderWidth(1);
 			leaveHeadTable.getDefaultCell().setPadding(1);
 			leaveHeadTable.setWidths(arrheaderwidths8A);

 			PdfPTable leaveAmtTable = new PdfPTable(1);
 			int[] arrheaderwidths8B = { 100 }; // percentage
 			leaveAmtTable.getDefaultCell().setBorderWidth(1);
 			leaveAmtTable.getDefaultCell().setPadding(1);
 			leaveAmtTable.setWidths(arrheaderwidths8B);

 			int nCountGross = 0;
 			Iterator<String> itr12 = salaryHeadName.iterator();
 			while (itr12.hasNext()) {

 				String strHeadNM = itr12.next();

 				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {

 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQ2.setBorderWidth(0);
 					leaveHeadTable.addCell(cellQ2);

 					PdfPCell cellQ3 = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQ3.setBorderWidthTop(1);
 					cellQ3.setBorderWidthBottom(0);
 					cellQ3.setBorderWidthLeft(0);
 					cellQ3.setBorderWidthRight(0);
 					leaveAmtTable.addCell(cellQ3);
 				} else {

 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ2.setBorderWidth(0);
 					leaveHeadTable.addCell(cellQ2);
 					
 					PdfPCell cellQ3 = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQ3.setBorderWidth(0);
 					leaveAmtTable.addCell(cellQ3);

 				}

 				nCountGross++;

 			}

 			leaveTable.addCell(leaveHeadTable);
 			leaveTable.addCell(leaveAmtTable);

 			PdfPTable Deductiontable = new PdfPTable(2);
 			int[] arrheaderwidths9 = { 50, 50 }; // percentage
 			Deductiontable.getDefaultCell().setBorderWidth(1);
 			Deductiontable.getDefaultCell().setPadding(1);
 			Deductiontable.setWidths(arrheaderwidths9);

 			PdfPCell cellQ3 = new PdfPCell(grossTitalTable);
 			cellQ3.setBorderWidth(1);
 			Deductiontable.addCell(cellQ3);

 			PdfPCell cellR = new PdfPCell(leaveTable);
 			cellR.setBorderWidth(1);
 			Deductiontable.addCell(cellR);

 			PdfPTable leaveDtable = new PdfPTable(2);
 			int[] arrheaderwidths10 = { 60, 40 }; // percentage
 			leaveDtable.getDefaultCell().setBorderWidth(0);
 			leaveDtable.getDefaultCell().setPadding(1);
 			leaveDtable.setWidths(arrheaderwidths10);

 			PdfPTable leaveDHead = new PdfPTable(1);
 			int[] arrheaderwidths10A = { 100 }; // percentage
 			leaveDHead.getDefaultCell().setBorderWidth(1);
 			leaveDHead.getDefaultCell().setPadding(1);
 			leaveDHead.setWidths(arrheaderwidths10A);

 			PdfPTable leaveDAmt = new PdfPTable(1);
 			int[] arrheaderwidths10B = { 100 }; // percentage
 			leaveDAmt.getDefaultCell().setBorderWidth(1);
 			leaveDAmt.getDefaultCell().setPadding(1);
 			leaveDAmt.setWidths(arrheaderwidths10B);

 			int ncount = 0;
 			Iterator<String> itr121 = leaveName.iterator();
 			while (itr121.hasNext()) {

 				String strleave = itr121.next();

 				if (strleave!=null && strleave.equalsIgnoreCase("Total")) {
 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQ2.setBorderWidth(0);

 					if (leaveName.size() < deductionHeadName.size()) {
 						cellQ2.setBorderWidthBottom(1);
 					}
 					leaveDHead.addCell(cellQ2);
 				}

 				else {
 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ2.setBorderWidth(0);
 					leaveDHead.addCell(cellQ2);

 				}

 				if (strleave!=null && strleave.equalsIgnoreCase("Total")) {
 					
 					PdfPCell cellQA3 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQA3.setBorderWidthTop(1);
 					cellQA3.setBorderWidthLeft(1);
 					cellQA3.setBorderWidthRight(0);
 					if (noOfLeave.size() < deductionHeadName.size()) {
 						cellQA3.setBorderWidthBottom(1);
 					}
 					leaveDAmt.addCell(cellQA3);

 				} else {

 					PdfPCell cellQA3 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQA3.setBorderWidth(0);
 					cellQA3.setBorderWidthLeft(1);
 					leaveDAmt.addCell(cellQA3);

 				}

 				ncount++;

 			}

 			PdfPTable DeductionHeadTable = new PdfPTable(2);
 			int[] arrheaderwidths11 = { 60, 40 }; // percentage
 			DeductionHeadTable.getDefaultCell().setBorderWidth(0);
 			DeductionHeadTable.getDefaultCell().setPadding(1);
 			DeductionHeadTable.setWidths(arrheaderwidths11);

 			PdfPTable DeductionName = new PdfPTable(1);
 			int[] arrheaderwidths11A = { 100 }; // percentage
 			DeductionName.getDefaultCell().setBorderWidth(0);
 			DeductionName.getDefaultCell().setPadding(1);
 			DeductionName.setWidths(arrheaderwidths11A);

 			PdfPTable deductionAmountTable = new PdfPTable(1);
 			int[] arrheaderwidths11B = { 100 }; // percentage
 			deductionAmountTable.getDefaultCell().setBorderWidth(0);
 			deductionAmountTable.getDefaultCell().setPadding(1);
 			deductionAmountTable.setWidths(arrheaderwidths11B);

 			int nCountDeductionAmt = 0;
 			Iterator<String> itr122 = deductionHeadName.iterator();
 			while (itr122.hasNext()) {

 				String strDeductionNm = itr122.next();

 				if (strDeductionNm!=null && strDeductionNm.equalsIgnoreCase("Total Deduction")) {

 					PdfPCell cellQ2 = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQ2.setBorderWidth(0);
 					if (deductionHeadName.size() < leaveName.size()) {
 						cellQ2.setBorderWidthBottom(1);
 					}

 					DeductionName.addCell(cellQ2);

 					PdfPCell cellQA3 = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));

 					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQA3.setBorderWidthTop(1);

 					if (deductionHeadName.size() < leaveName.size()) {
 						cellQA3.setBorderWidthBottom(1);
 					}

 					cellQA3.setBorderWidthLeft(1);
 					cellQA3.setBorderWidthRight(0);
 					deductionAmountTable.addCell(cellQA3);
 				} else {

 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strDeductionNm,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ2.setBorderWidth(0);
 					DeductionName.addCell(cellQ2);

 					PdfPCell cellQA3 = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQA3.setBorderWidth(0);
 					if (deductionHeadName.size() < leaveName.size()) {
 						cellQA3.setBorderWidthBottom(1);
 					}

 					cellQA3.setBorderWidthLeft(1);
 					deductionAmountTable.addCell(cellQA3);
 				}

 				nCountDeductionAmt++;
 			}

 			PdfPCell cellQA3 = new PdfPCell(new Phrase("", FontFactory.getFont("Verdana", 12, Font.NORMAL)));
 			cellQA3.setBorderWidth(0);
 			cellQA3.setBorderWidthTop(0);

 			if (leaveName.size() != deductionHeadName.size())
 			{
 				if (leaveName.size() > deductionHeadName.size()) {
 					for (int i = 0; i < (leaveName.size() - deductionHeadName.size()); i++) {
 						DeductionName.addCell(cellQA3);
 						deductionAmountTable.addCell(cellQA3);
 					}

 				} else {
 					for (int i = 0; i < (deductionHeadName.size() - leaveName.size()); i++) {
 						leaveDHead.addCell(cellQA3);
 						leaveDAmt.addCell(cellQA3);
 					}

 				}

 			}
 			
 			/**=================   START   ============================
 			 *  Balance Leave Details
 			 */
 			
 			PdfPCell cellQA4 = null;
 			cellQA4 = new PdfPCell(new Phrase("Balance Leaves", FontFactory.getFont("Verdana", 8, Font.BOLDITALIC)));
 			cellQA4.setBorderWidth(0);
 			cellQA4.setBorderWidthTop(0);
 			leaveDHead.addCell(cellQA4);
 			
 			cellQA4 = new PdfPCell(new Phrase("  ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 			cellQA4.setBorderWidth(0);
 			cellQA4.setBorderWidthTop(0);
 			leaveDAmt.addCell(cellQA4);
 			
 			
 			Set set = hmBalanceLeave.keySet();
 			Iterator it = set.iterator();
 			while(it.hasNext()){
 				String strLeaveId = (String)it.next();
 				
 				cellQA4 = new PdfPCell(new Phrase(" ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 				cellQA4.setBorderWidth(0);
 				cellQA4.setBorderWidthTop(0);
 				leaveDHead.addCell(cellQA4);
 				
 				cellQA4 = new PdfPCell(new Phrase(" ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 				cellQA4.setBorderWidth(0);
 				cellQA4.setBorderWidthTop(0);
 				leaveDAmt.addCell(cellQA4);

 				
 			}
 			
 			/**=================   END   ============================
 			 * 
 			 */
 			
 			DeductionHeadTable.addCell(DeductionName);
 			DeductionHeadTable.addCell(deductionAmountTable);

 			PdfPTable HeadleaveDeductionTable = new PdfPTable(2);
 			int[] arrheaderwidths12 = { 50, 50 }; // percentage
 			HeadleaveDeductionTable.getDefaultCell().setBorderWidth(1);
 			HeadleaveDeductionTable.getDefaultCell().setPadding(1);
 			HeadleaveDeductionTable.setWidths(arrheaderwidths12);

 			PdfPCell cellU = new PdfPCell(leaveDtable);
 			cellU.setBorderWidth(1);
 			HeadleaveDeductionTable.addCell(cellU);

 			PdfPCell cellV = new PdfPCell(DeductionHeadTable);
 			cellV.setBorderWidth(1);
 			HeadleaveDeductionTable.addCell(cellV);

 			PdfPCell celld = new PdfPCell(new PdfPCell(empTable));

 			PdfPTable NetSalaryTable = new PdfPTable(3);
 			int[] arrheaderwidths13 = { 15,20,65}; // percentage
 			NetSalaryTable.getDefaultCell().setBorderWidth(1);
 			NetSalaryTable.getDefaultCell().setPadding(1);
 			NetSalaryTable.setWidths(arrheaderwidths13);

 			PdfPCell cellUv = new PdfPCell(new Phrase(" Net Salary :",FontFactory.getFont("Verdana", 9, Font.BOLD)));
 			cellUv.setBorderWidth(1);
 			NetSalaryTable.addCell(cellUv);

 			PdfPCell cellVu = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
 			cellVu.setBorderWidth(1);
 			NetSalaryTable.addCell(cellVu);
 			
 			PdfPCell cellwordAmt = new PdfPCell(new Phrase( " ",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 			cellwordAmt.setBorderWidth(1);
 			cellwordAmt.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			NetSalaryTable.addCell(cellwordAmt);
 				
 			PdfPTable signatureTable = new PdfPTable(1);
 			int[] arrheaderwidths14 = { 100 }; // percentage
 			signatureTable.getDefaultCell().setBorderWidth(1);
 			signatureTable.getDefaultCell().setPadding(1);

 			signatureTable.setWidths(arrheaderwidths14);
 			
 			PdfPCell cellUvU = new PdfPCell(new Phrase("This is computer generated PaySlip and does not require signature.",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 			cellUvU.setBorderWidth(1);
 			signatureTable.addCell(cellUvU);

 			imageEmpDetailTable.addCell(celld);
 			document.add(LogoImage);
 			document.add(titalTable);
 			document.add(imageEmpDetailTable);
 			document.add(netGrossTital);
 			document.add(Deductiontable);
 			document.add(netGrossTable);
 			document.add(HeadleaveDeductionTable);
 			document.add(NetSalaryTable);
 			document.add(signatureTable);

 			document.close();
 				
			response.setContentType("application/pdf");
			response.setContentLength(baos.size());
			response.setHeader("Content-Disposition","attachment; filename=PaySlip.pdf");
			ServletOutputStream out = response.getOutputStream();              
			baos.writeTo(out);
			out.flush();
			out.close();
			baos.close();
			out.close();
			
			return;
 			
 		}catch (Exception e) {
 			e.printStackTrace();
 		}
 	}

	public void  viewSalarySlipFirstFormat(){
 		try
 			{
 			ByteArrayOutputStream baos = new ByteArrayOutputStream();
 			Document document = new Document();
 			PdfWriter.getInstance(document, baos);
 			document.open();

 			String filePathDefault=null;
 			String filePathCompanyLOgo = null;
 			
 			payEmpHead.add("EMPLOYEE NAME");
 			payEmpHead.add("DESIGNATION");
 			payEmpHead.add("EMPLOYEE CODE");
 			payEmpHead.add("LEVEL");
 			payEmpHead.add("ACC. NO.");
 			payEmpHead.add("GRADE");
 			payEmpHead.add("PF ACC. NO.");
 			payEmpHead.add("BRANCH");
 			payEmpHead.add("JOINING DATE");
 			payEmpHead.add("DEPARTMENT");
 			payEmpHead.add("PAN NO");
 			payEmpHead.add("MONTH");
 			payEmpHead.add("UAN NO.");
 			payEmpHead.add("");
 			
 			
 			salaryHeadName.add("OTHER ALLOWANCE/EX-");
 			salaryHeadName.add("GRATIA");
 			salaryHeadName.add("KRA Allowance Fixed");
 			salaryHeadName.add("Special Allowance");
 			salaryHeadName.add("Basic");
 			salaryHeadName.add("DA");
 			salaryHeadName.add("HRA");
 			salaryHeadName.add("Conveyance");
 			salaryHeadName.add("Overtime");
 			salaryHeadName.add("Incentive");
 			salaryHeadName.add("Mobile Reimbursement");
 			salaryHeadName.add("Travel Reimbursement");
 			salaryHeadName.add("Other Reimbursement");
 			salaryHeadName.add("Medical Allowance");
 			salaryHeadName.add("Education Allowance");
 			salaryHeadName.add("Total");
 			
 			deductionHeadName.add("Professional Tax");
 			deductionHeadName.add("Employee PF");
 			deductionHeadName.add("Voluntary PF");
 			deductionHeadName.add("Mobile Recovery");
 			deductionHeadName.add("Mediclaim Deduction");
 			deductionHeadName.add("Total Deduction");
 		
			filePathDefault=request.getRealPath("/userImages/avatar_photo.png");
			filePathCompanyLOgo = request.getRealPath("/userImages/company_avatar_photo1.png");
			 
			Image imagePhoto=null;
			Image imageLogo=null;
			try{
				
				FileInputStream fileInputStream=null;
		        File file = new File(filePathDefault);
		        byte[] bFile = new byte[(int) file.length()];
		        fileInputStream = new FileInputStream(file);
			    fileInputStream.read(bFile);
			    fileInputStream.close();
			    imagePhoto = Image.getInstance(bFile);
				
			}catch(FileNotFoundException e){
				imagePhoto = Image.getInstance(filePathDefault);
			}
			
			try{
				
				FileInputStream fileInputStream1=null;
		        File file1 = new File(filePathCompanyLOgo);
		        byte[] bFile1 = new byte[(int) file1.length()];
		        fileInputStream1 = new FileInputStream(file1);
			    fileInputStream1.read(bFile1);
			    fileInputStream1.close();
		        imageLogo = Image.getInstance(bFile1);
		        
			}catch(FileNotFoundException e){
				imageLogo = Image.getInstance(filePathCompanyLOgo);
			}
 			
 			PdfPTable photoImagetable = new PdfPTable(1);
 			photoImagetable.setWidthPercentage(10);
 			photoImagetable.getDefaultCell().setPadding(1);
 			
 			photoImagetable.addCell(imagePhoto);

 			PdfPTable companyNameTable = new PdfPTable(1);
			int[] arrheaderwidthsh = { 100 }; // percentage
			companyNameTable.getDefaultCell().setBorderWidth(0);
			companyNameTable.setWidths(arrheaderwidthsh);
			PdfPCell companyNamecell = new PdfPCell(new Phrase("Company Name", FontFactory.getFont("Verdana", 14,Font.BOLD)));

			companyNamecell.setBorderWidthTop(0);
			companyNamecell.setBorderWidthBottom(1);
			companyNamecell.setBorderWidthLeft(0);
			companyNamecell.setBorderWidthRight(0);

			companyNamecell.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell);
			PdfPCell companyNamecell1 = new PdfPCell(new Phrase("Company Address", FontFactory.getFont("Verdana", 10,Font.BOLD)));

			companyNamecell1.setBorderWidthTop(0);
			companyNamecell1.setBorderWidthBottom(0);
			companyNamecell1.setBorderWidthLeft(0);
			companyNamecell1.setBorderWidthRight(0);

			companyNamecell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell1);

			PdfPTable LogoImage = new PdfPTable(2);
			int[] arrheaderwidths1 = { 70, 30 }; // percentage
			LogoImage.getDefaultCell().setBorderWidth(1);
			LogoImage.setWidths(arrheaderwidths1);
			LogoImage.setTotalWidth(600); 
			LogoImage.addCell(companyNameTable);
			LogoImage.addCell(imageLogo);

			PdfPTable titalTable = new PdfPTable(1);
			int[] arrheaderwidths2 = { 100 }; // percentage
			titalTable.getDefaultCell().setBorderWidth(1);
			titalTable.getDefaultCell().setPadding(1);
			titalTable.setWidths(arrheaderwidths2);

 			PdfPCell cellb = new PdfPCell(new Phrase(" PAY SLIP ",FontFactory.getFont("Verdana", 15, Font.BOLD)));
 			cellb.getExtraParagraphSpace();
 			cellb.setBorderWidth(1);
 			cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
 			titalTable.addCell(cellb);

 			PdfPTable empTable = new PdfPTable(6);
 			int[] arrheaderwidths5 = { 31, 2, 30, 15, 2, 20 }; // percentage
 			empTable.getDefaultCell().setBorderWidth(1);
 			empTable.setWidths(arrheaderwidths5);
 			empTable.setTotalWidth(800);
 			
 			int k = 0;
 			
 			for(; k<payEmpHead.size(); k++){
 			
 				PdfPCell cellE = new PdfPCell(new Phrase(payEmpHead.get(k),FontFactory.getFont("Verdana", 7, Font.BOLD)));
 				cellE.setBorderWidth(0);
 				empTable.addCell(cellE);

 				if(payEmpHead.get(k).isEmpty()){
	 				PdfPCell cellcollan = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 7, Font.BOLD)));
	 				cellcollan.setBorderWidth(0);
	 				empTable.addCell(cellcollan);
 				}else{
 					PdfPCell cellcollan = new PdfPCell(new Phrase(":",FontFactory.getFont("Verdana", 7, Font.BOLD)));
 	 				cellcollan.setBorderWidth(0);
 	 				empTable.addCell(cellcollan);
 				}
 				PdfPCell cellF = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 7, Font.NORMAL)));
 				cellF.setBorderWidth(0);
 				empTable.addCell(cellF);
 			}

 			PdfPTable imageEmpDetailTable = new PdfPTable(2);
 			int[] arrheaderwidths4 = { 12, 88 }; // percentage
 			imageEmpDetailTable.getDefaultCell().setBorderWidth(1);
 			imageEmpDetailTable.getDefaultCell().setPadding(1);
 			imageEmpDetailTable.setWidths(arrheaderwidths4);

 			imageEmpDetailTable.addCell(photoImagetable);
 			PdfPTable netGrossTital = new PdfPTable(2);
 			int[] arrheaderwidths6 = { 50, 50 }; // percentage
 			netGrossTital.getDefaultCell().setBorderWidth(1);
 			netGrossTital.getDefaultCell().setPadding(1);
 			netGrossTital.setWidths(arrheaderwidths6);

 			PdfPCell cellQ = new PdfPCell(new Phrase("GROSS EARNINGS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
 			cellQ.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cellQ.setBorderWidth(1);
 			netGrossTital.addCell(cellQ);

 			PdfPCell cellQ1 = new PdfPCell(new Phrase("NET EARNINGS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
 			cellQ1.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cellQ1.setBorderWidth(1);
 			netGrossTital.addCell(cellQ1);

 			PdfPTable grossTitalTable = new PdfPTable(2);
 			int[] arrheaderwidths7 = { 60, 40 }; // percentage
 			grossTitalTable.getDefaultCell().setBorderWidth(1);
 			grossTitalTable.getDefaultCell().setPadding(1);
 			grossTitalTable.setWidths(arrheaderwidths7);

 			PdfPTable GrossHeadTable = new PdfPTable(1);
 			int[] arrheaderwidths7A = { 100 }; // percentage
 			GrossHeadTable.getDefaultCell().setBorderWidth(1);
 			GrossHeadTable.getDefaultCell().setPadding(1);
 			GrossHeadTable.setWidths(arrheaderwidths7A);

 			PdfPTable grossAmtTable = new PdfPTable(1);
 			int[] arrheaderwidths7B = { 100 }; // percentage
 			grossAmtTable.getDefaultCell().setBorderWidth(1);
 			grossAmtTable.getDefaultCell().setPadding(1);
 			grossAmtTable.setWidths(arrheaderwidths7B);

 			int nCount = 0;
 			Iterator<String> itr1 = salaryHeadName.iterator();
 			while (itr1.hasNext()) {

 				String strHeadNM = itr1.next();

 				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQ2.setBorderWidth(0);
 					GrossHeadTable.addCell(cellQ2);

 				} else {
 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ2.setBorderWidth(0);
 					GrossHeadTable.addCell(cellQ2);

 					PdfPCell cellQ3 = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQ3.setBorderWidth(0);
 					grossAmtTable.addCell(cellQ3);
 				}

 				nCount++;
 			}

 			grossTitalTable.addCell(GrossHeadTable);
 			grossTitalTable.addCell(grossAmtTable);

 			PdfPTable netGrossTable = new PdfPTable(2);
 			int[] arrheaderwidths6A = { 50, 50 }; // percentage
 			netGrossTable.getDefaultCell().setBorderWidth(1);
 			netGrossTable.getDefaultCell().setPadding(1);
 			netGrossTable.setWidths(arrheaderwidths6A);

 			PdfPCell cellQA1 = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
 			cellQA1.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cellQA1.setBorderWidth(1);
 			netGrossTable.addCell(cellQA1);

 			PdfPCell cellQA2 = new PdfPCell(new Phrase(" DEDUCTIONS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
 			cellQA2.setBorderWidth(1);
 			cellQA2.setHorizontalAlignment(Element.ALIGN_CENTER);
 			netGrossTable.addCell(cellQA2);

 			PdfPTable leaveTable = new PdfPTable(2);
 			int[] arrheaderwidths8 = { 60, 40 }; // percentage
 			leaveTable.getDefaultCell().setBorderWidth(1);
 			leaveTable.getDefaultCell().setPadding(1);
 			leaveTable.setWidths(arrheaderwidths8);

 			PdfPTable leaveHeadTable = new PdfPTable(1);
 			int[] arrheaderwidths8A = { 100 }; // percentage
 			leaveHeadTable.getDefaultCell().setBorderWidth(1);
 			leaveHeadTable.getDefaultCell().setPadding(1);
 			leaveHeadTable.setWidths(arrheaderwidths8A);

 			PdfPTable leaveAmtTable = new PdfPTable(1);
 			int[] arrheaderwidths8B = { 100 }; // percentage
 			leaveAmtTable.getDefaultCell().setBorderWidth(1);
 			leaveAmtTable.getDefaultCell().setPadding(1);
 			leaveAmtTable.setWidths(arrheaderwidths8B);

 			int nCountGross = 0;
 			Iterator<String> itr12 = salaryHeadName.iterator();
 			while (itr12.hasNext()) {
 				String strHeadNM = itr12.next();

 				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
 					
 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQ2.setBorderWidth(0);
 					leaveHeadTable.addCell(cellQ2);

 					PdfPCell cellQ3 = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQ3.setBorderWidthTop(1);
 					cellQ3.setBorderWidthBottom(0);
 					cellQ3.setBorderWidthLeft(0);
 					cellQ3.setBorderWidthRight(0);
 					leaveAmtTable.addCell(cellQ3);
 					
 				} else {

 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ2.setBorderWidth(0);
 					leaveHeadTable.addCell(cellQ2);
 					
 					PdfPCell cellQ3 = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQ3.setBorderWidth(0);
 					leaveAmtTable.addCell(cellQ3);
 				}
 				nCountGross++;
 			}

 			leaveTable.addCell(leaveHeadTable);
 			leaveTable.addCell(leaveAmtTable);

 			PdfPTable Deductiontable = new PdfPTable(2);
 			int[] arrheaderwidths9 = { 50, 50 }; // percentage
 			Deductiontable.getDefaultCell().setBorderWidth(1);
 			Deductiontable.getDefaultCell().setPadding(1);
 			Deductiontable.setWidths(arrheaderwidths9);

 			PdfPCell cellQ3 = new PdfPCell(grossTitalTable);
 			cellQ3.setBorderWidth(1);
 			Deductiontable.addCell(cellQ3);

 			PdfPCell cellR = new PdfPCell(leaveTable);
 			cellR.setBorderWidth(1);
 			Deductiontable.addCell(cellR);

 			PdfPTable leaveDtable = new PdfPTable(2);
 			int[] arrheaderwidths10 = { 60, 40 }; // percentage
 			leaveDtable.getDefaultCell().setBorderWidth(0);
 			leaveDtable.getDefaultCell().setPadding(1);
 			leaveDtable.setWidths(arrheaderwidths10);

 			PdfPTable leaveDHead = new PdfPTable(1);
 			int[] arrheaderwidths10A = { 100 }; // percentage
 			leaveDHead.getDefaultCell().setBorderWidth(1);
 			leaveDHead.getDefaultCell().setPadding(1);
 			leaveDHead.setWidths(arrheaderwidths10A);

 			PdfPTable leaveDAmt = new PdfPTable(1);
 			int[] arrheaderwidths10B = { 100 }; // percentage
 			leaveDAmt.getDefaultCell().setBorderWidth(1);
 			leaveDAmt.getDefaultCell().setPadding(1);
 			leaveDAmt.setWidths(arrheaderwidths10B);

 			int ncount = 0;
 			Iterator<String> itr121 = leaveName.iterator();
 			while (itr121.hasNext()) {

 				String strleave = itr121.next();

 				if (strleave!=null && strleave.equalsIgnoreCase("Total")) {

 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQ2.setBorderWidth(0);

 					if (leaveName.size() < deductionHeadName.size()) {
 						cellQ2.setBorderWidthBottom(1);
 					}
 					leaveDHead.addCell(cellQ2);
 				}else {

 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ2.setBorderWidth(0);
 					leaveDHead.addCell(cellQ2);
 				}

 				if (strleave!=null && strleave.equalsIgnoreCase("Total")) {
 					
 					PdfPCell cellQA3 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQA3.setBorderWidthTop(1);
 					cellQA3.setBorderWidthLeft(1);
 					cellQA3.setBorderWidthRight(0);
 					if (noOfLeave.size() < deductionHeadName.size()) {
 						cellQA3.setBorderWidthBottom(1);
 					}
 					leaveDAmt.addCell(cellQA3);

 				} else {

 					PdfPCell cellQA3 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQA3.setBorderWidth(0);
 					cellQA3.setBorderWidthLeft(1);
 					leaveDAmt.addCell(cellQA3);
 				}
 				ncount++;
 			}

 			PdfPTable DeductionHeadTable = new PdfPTable(2);
 			int[] arrheaderwidths11 = { 60, 40 }; // percentage
 			DeductionHeadTable.getDefaultCell().setBorderWidth(0);
 			DeductionHeadTable.getDefaultCell().setPadding(1);
 			DeductionHeadTable.setWidths(arrheaderwidths11);

 			PdfPTable DeductionName = new PdfPTable(1);
 			int[] arrheaderwidths11A = { 100 }; // percentage
 			DeductionName.getDefaultCell().setBorderWidth(0);
 			DeductionName.getDefaultCell().setPadding(1);
 			DeductionName.setWidths(arrheaderwidths11A);

 			PdfPTable deductionAmountTable = new PdfPTable(1);
 			int[] arrheaderwidths11B = { 100 }; // percentage
 			deductionAmountTable.getDefaultCell().setBorderWidth(0);
 			deductionAmountTable.getDefaultCell().setPadding(1);
 			deductionAmountTable.setWidths(arrheaderwidths11B);

 			int nCountDeductionAmt = 0;
 			Iterator<String> itr122 = deductionHeadName.iterator();
 			while (itr122.hasNext()) {

 				String strDeductionNm = itr122.next();

 				if (strDeductionNm!=null && strDeductionNm.equalsIgnoreCase("Total Deduction")) {

 					PdfPCell cellQ2 = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQ2.setBorderWidth(0);
 					if (deductionHeadName.size() < leaveName.size()) {
 						cellQ2.setBorderWidthBottom(1);
 					}
 					DeductionName.addCell(cellQ2);

 					PdfPCell cellQA3 = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));
 					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQA3.setBorderWidthTop(1);

 					if (deductionHeadName.size() < leaveName.size()) {
 						cellQA3.setBorderWidthBottom(1);
 					}
 					cellQA3.setBorderWidthLeft(1);
 					cellQA3.setBorderWidthRight(0);
 					deductionAmountTable.addCell(cellQA3);
 				} else {

 					PdfPCell cellQ2 = new PdfPCell(new Phrase(strDeductionNm,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQ2.setBorderWidth(0);
 					DeductionName.addCell(cellQ2);

 					PdfPCell cellQA3 = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
 					cellQA3.setBorderWidth(0);
 					if (deductionHeadName.size() < leaveName.size()) {
 						cellQA3.setBorderWidthBottom(1);
 					}
 					cellQA3.setBorderWidthLeft(1);
 					deductionAmountTable.addCell(cellQA3);
 				}
 				nCountDeductionAmt++;
 			}

 			PdfPCell cellQA3 = new PdfPCell(new Phrase("", FontFactory.getFont("Verdana", 12, Font.NORMAL)));
 			cellQA3.setBorderWidth(0);
 			cellQA3.setBorderWidthTop(0);

 			if (leaveName.size() != deductionHeadName.size()){

 				if (leaveName.size() > deductionHeadName.size()) {

 					for (int i = 0; i < (leaveName.size() - deductionHeadName.size()); i++) {
 						DeductionName.addCell(cellQA3);
 						deductionAmountTable.addCell(cellQA3);
 					}
 				} else {
 					for (int i = 0; i < (deductionHeadName.size() - leaveName.size()); i++) {
 						leaveDHead.addCell(cellQA3);
 						leaveDAmt.addCell(cellQA3);
 					}
 				}
 			}
 			
 			/**=================   START   ============================
 			 *  Balance Leave Details
 			 */
 			
 			PdfPCell cellQA4 = null;
 			cellQA4 = new PdfPCell(new Phrase("Balance Leaves", FontFactory.getFont("Verdana", 8, Font.BOLDITALIC)));
 			cellQA4.setBorderWidth(0);
 			cellQA4.setBorderWidthTop(0);
 			leaveDHead.addCell(cellQA4);
 			
 			cellQA4 = new PdfPCell(new Phrase("  ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 			cellQA4.setBorderWidth(0);
 			cellQA4.setBorderWidthTop(0);
 			leaveDAmt.addCell(cellQA4);
 			
 			
 			Set set = hmBalanceLeave.keySet();
 			Iterator it = set.iterator();
 			while(it.hasNext()){
 				
 				cellQA4 = new PdfPCell(new Phrase(" ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 				cellQA4.setBorderWidth(0);
 				cellQA4.setBorderWidthTop(0);
 				leaveDHead.addCell(cellQA4);
 				
 				cellQA4 = new PdfPCell(new Phrase(" ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 				cellQA4.setBorderWidth(0);
 				cellQA4.setBorderWidthTop(0);
 				leaveDAmt.addCell(cellQA4);
 			}
 			
 			/**=================   END   ============================
 			 * 
 			 */
 			
 			DeductionHeadTable.addCell(DeductionName);
 			DeductionHeadTable.addCell(deductionAmountTable);

 			PdfPTable HeadleaveDeductionTable = new PdfPTable(2);
 			int[] arrheaderwidths12 = { 50, 50 }; // percentage
 			HeadleaveDeductionTable.getDefaultCell().setBorderWidth(1);
 			HeadleaveDeductionTable.getDefaultCell().setPadding(1);
 			HeadleaveDeductionTable.setWidths(arrheaderwidths12);

 			PdfPCell cellU = new PdfPCell(leaveDtable);
 			cellU.setBorderWidth(1);
 			HeadleaveDeductionTable.addCell(cellU);

 			PdfPCell cellV = new PdfPCell(DeductionHeadTable);
 			cellV.setBorderWidth(1);
 			HeadleaveDeductionTable.addCell(cellV);

 			PdfPCell celld = new PdfPCell(new PdfPCell(empTable));

 			PdfPTable NetSalaryTable = new PdfPTable(3);
 			int[] arrheaderwidths13 = { 15,20,65}; // percentage
 			NetSalaryTable.getDefaultCell().setBorderWidth(1);
 			NetSalaryTable.getDefaultCell().setPadding(1);
 			NetSalaryTable.setWidths(arrheaderwidths13);

 			PdfPCell cellUv = new PdfPCell(new Phrase(" Net Salary :",FontFactory.getFont("Verdana", 9, Font.BOLD)));
 			cellUv.setBorderWidth(1);
 			NetSalaryTable.addCell(cellUv);

 			PdfPCell cellVu = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
 			cellVu.setBorderWidth(1);
 			NetSalaryTable.addCell(cellVu);
 			
 			PdfPCell cellwordAmt = new PdfPCell(new Phrase( " ",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 			cellwordAmt.setBorderWidth(1);
 			cellwordAmt.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			NetSalaryTable.addCell(cellwordAmt);
 				
 			PdfPTable signatureTable = new PdfPTable(1);
 			int[] arrheaderwidths14 = { 100 }; // percentage
 			signatureTable.getDefaultCell().setBorderWidth(1);
 			signatureTable.getDefaultCell().setPadding(1);
 			signatureTable.setWidths(arrheaderwidths14);
 			
 			PdfPCell cellUvU = new PdfPCell(new Phrase("This is computer generated PaySlip and does not require signature.",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
 			cellUvU.setBorderWidth(1);
 			signatureTable.addCell(cellUvU);

 			imageEmpDetailTable.addCell(celld);
 			document.add(LogoImage);
 			document.add(titalTable);
 			document.add(imageEmpDetailTable);
 			document.add(netGrossTital);
 			document.add(Deductiontable);
 			document.add(netGrossTable);
 			document.add(HeadleaveDeductionTable);
 			document.add(NetSalaryTable);
 			document.add(signatureTable);
 			
 			document.close();
 				
			response.setContentType("application/pdf");
			response.setContentLength(baos.size());
			response.setHeader("Content-Disposition","attachment; filename=PaySlip.pdf");
			ServletOutputStream out = response.getOutputStream();              
			baos.writeTo(out);
			out.flush();
			out.close();
			baos.close();
			out.close();
 				
 			return;
 			
 		}catch (Exception e) {
 			e.printStackTrace();
 		}
 	}

	private void viewSalarySlipSecondFormat() {

		try{
			com.itextpdf.text.Font small = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7);
			com.itextpdf.text.Font smallBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7,Font.BOLD);
			
	        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
	        com.itextpdf.text.pdf.PdfWriter.getInstance(document,buffer);
	        document.open();
	        
	        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(10);
			table.setWidthPercentage(100);       
			int[] cols = {3,10,10,10,10,10,10,10,10,10};
			table.setWidths(cols);
	        
			com.itextpdf.text.pdf.PdfPCell row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Company Name :",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);
	        row1.setPadding(2.5f);
	        table.addCell(row1); 
	      
	      //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("ADDRESS :",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Payslip for the month of  : ",smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM);
	        row1.setColspan(10);
	        table.addCell(row1);
	
	        String heading2="EMPLOYEE DETAILS";
	      //New Row
	          row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(heading2,smallBold));
	          row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	          row1.setBorder(Rectangle.NO_BORDER);
	          row1.setColspan(10);
	          row1.setPadding(2.5f); 
	          table.addCell(row1);
	        
	      //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Emp Code : ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	      
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("PAN : ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Location : ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);	
	        row1.setColspan(5);	 
	        table.addCell(row1);
	      
	      //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Department Name : ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Name : ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	      
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Designation : ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	
	        table.addCell(row1);
	      
	      //New Row
	        String heading3="PAYMENT DETAILS";
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(heading3,smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP);
	        row1.setColspan(10);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Bank Name : ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	      
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Bank Account No. : ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	  
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Mode of Payment : ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);	        
	        table.addCell(row1);
	      
	      //New Row
	        String heading4="ATTENDENCE DETAILS";
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(heading4,smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(10);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	      
	      //New Row  
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total Days : ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Unpaid Days : ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        table.addCell(row1);
	       
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Paid Days : ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        table.addCell(row1);
	       
	      //New Row  
	        String heading5="SALARY DETAILS";
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(heading5,smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(10);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row  
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Earnings",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(3);	        
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Amount",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(2);	 
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Deductions",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(3);	 
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Amount",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(2);	 
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("Conveyance",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	     
	        row1 =new PdfPCell(new Paragraph("Professional Tax",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	 
	        table.addCell(row1);
	
	        //New Row
	        row1 =new PdfPCell(new Paragraph("Overtime",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	         
	        table.addCell(row1);
	     
	        row1 =new PdfPCell(new Paragraph("Income Tax",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	 
	        table.addCell(row1);
	
	        //New Row
	        row1 =new PdfPCell(new Paragraph("Incentive",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	     
	        row1 =new PdfPCell(new Paragraph("Employee PF",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	 
	        table.addCell(row1);
	      
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Mobile Reimbursement",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	     
	        row1 =new PdfPCell(new Paragraph("Advance",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	 
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Travel Reimbursement",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	     
	        row1 =new PdfPCell(new Paragraph("Voluntary PF",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	 
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Other Reimbursement",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	     
	        row1 =new PdfPCell(new Paragraph("Mobile Recovery",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	 
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Medical Allowance",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	     
	        row1 =new PdfPCell(new Paragraph("Mediclaim Deduction",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	 
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("Education Allowance",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);	        
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("OTHER ALLOWANCE/EX-GRATIA",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);	        
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("KRA Allowance Fixed",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);	        
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("Special Allowance",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);	        
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("Basic",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);	        
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("DA",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);	        
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("HRA",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);	        
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Gross Earning  : ",smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	       
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Gross Deduction : ",smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(5);	 
	        table.addCell(row1);
	     
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Net Amount :           ",smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(10);	 
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Computer generated hence signature not required",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);	 
	        table.addCell(row1);
	        
	        document.add(table);
	        document.close();
			
			String filename="SalarySlip.pdf";
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename="+filename+"");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
		
		}catch (Exception e) {
			e.printStackTrace();  
		}
		
	}
	
//===cerated by parvez date: 02-09-2022===
	//===start===
	private void viewSalarySlipEightFormat() {

 		try{
 			
// 			System.out.println("PDF*");
 			ByteArrayOutputStream baos = new ByteArrayOutputStream();
 			Document document = new Document();
 			PdfWriter.getInstance(document, baos);
 			document.open();

 			String filePathCompanyLOgo = null;

 			payEmpHead.add("Emp No.");
			payEmpHead.add("");
			payEmpHead.add("Employee Name");
			payEmpHead.add("Calender Days");
//			payEmpHead.add("Bank Name ");
//			payEmpHead.add("A/C No.");
//			payEmpHead.add("W.Offs/ Holidays");
			payEmpHead.add("Designation");
			payEmpHead.add("Department ");
			payEmpHead.add("Joining Date");
			payEmpHead.add("Leave Days");
			payEmpHead.add("PAN No.");
			payEmpHead.add("Paid Days");
			payEmpHead.add("");
			payEmpHead.add("PF No.");
//			payEmpHead.add("OT Hrs");
			
 			salaryHeadName.add("Basic Pay");
 			salaryHeadName.add("H.R.A");
 			salaryHeadName.add("Spa All");
 			salaryHeadName.add("Veriable Allowance");
 			salaryHeadName.add("Conveyance");
 			salaryHeadName.add("MEDICAL ALL");
 			salaryHeadName.add("Over Time Pay");
 			salaryHeadName.add("Inc/Mob/Petrol");
// 			salaryHeadName.add("Total Payment");
 			
 			deductionHeadName.add("Prof. Tax ");
 			deductionHeadName.add("P.F (E.C)");
 			deductionHeadName.add("Insurance/Medical Bill");
 			deductionHeadName.add("Advance Salary");
 			deductionHeadName.add("T.D.S. Ded.");
 			deductionHeadName.add("Mise/Hostel/Ele-Bill");
 			deductionHeadName.add("L.W.F.");
 			deductionHeadName.add("Security Deposit");
// 			deductionHeadName.add("Deductions ");
// 			deductionHeadName.add("Net Payable");
 		
			filePathCompanyLOgo = request.getRealPath("/userImages/company_avatar_photo1.png");
			 
			Image imageLogo=null;
	
			try{
				
				FileInputStream fileInputStream1=null;
		        File file1 = new File(filePathCompanyLOgo);
		        byte[] bFile1 = new byte[(int) file1.length()];
		        fileInputStream1 = new FileInputStream(file1);
			    fileInputStream1.read(bFile1);
			    fileInputStream1.close();
		        imageLogo = Image.getInstance(bFile1);
		        
			}catch(FileNotFoundException e){
				imageLogo = Image.getInstance(filePathCompanyLOgo);
			}
 			
 			PdfPTable photoImagetable = new PdfPTable(1);
 			photoImagetable.setWidthPercentage(10);
 			photoImagetable.getDefaultCell().setPadding(1);
 			

 			PdfPTable companyNameTable = new PdfPTable(1);
			int[] arrheaderwidthsh = { 100 }; // percentage
			companyNameTable.getDefaultCell().setBorderWidth(0);
			companyNameTable.setWidths(arrheaderwidthsh);
			PdfPCell companyNamecell = new PdfPCell(new Phrase("Company Name", FontFactory.getFont("Verdana", 14,Font.BOLD,new BaseColor(29,108,128))));

			companyNamecell.setBorderWidthTop(0);
			companyNamecell.setBorderWidthBottom(1);
			companyNamecell.setBorderWidthLeft(0);
			companyNamecell.setBorderWidthRight(0);

			companyNamecell.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell);
			PdfPCell companyNamecell1 = new PdfPCell(new Phrase("Company Address", FontFactory.getFont("Verdana", 10,Font.BOLD)));

			companyNamecell1.setBorderWidthTop(0);
			companyNamecell1.setBorderWidthBottom(0);
			companyNamecell1.setBorderWidthLeft(0);
			companyNamecell1.setBorderWidthRight(0);

			companyNamecell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell1);

			PdfPTable LogoImage = new PdfPTable(2);
			int[] arrheaderwidths1 = { 30, 70 }; // percentage
			LogoImage.getDefaultCell().setBorderWidth(1);
			LogoImage.setWidths(arrheaderwidths1);
			LogoImage.setTotalWidth(600); 
			LogoImage.addCell(imageLogo);
			LogoImage.addCell(companyNameTable);
		

			PdfPTable titalTable = new PdfPTable(1);
			int[] arrheaderwidths2 = { 100 }; // percentage
			titalTable.getDefaultCell().setBorderWidth(1);
			titalTable.getDefaultCell().setPadding(1);
			titalTable.setWidths(arrheaderwidths2);

 			PdfPCell cellb = new PdfPCell(new Phrase("Pay Slip for the Month:- ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

 			cellb.getExtraParagraphSpace();
 			cellb.setBorderWidth(1);
 			cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
 			titalTable.addCell(cellb);

 			PdfPTable empTable = new PdfPTable(6);

 			int[] arrheaderwidths5 = { 31, 2, 30, 15, 2, 20 }; // percentage
 			empTable.getDefaultCell().setBorderWidth(1);

 			empTable.setWidths(arrheaderwidths5);
 			empTable.setTotalWidth(800);
 			
 			Iterator<String> itr = payEmpHead.iterator();
 			int k = 0;

 			for(; k<payEmpHead.size(); k++){
 			
 				PdfPCell cellE = new PdfPCell(new Phrase(payEmpHead.get(k),FontFactory.getFont("Verdana", 8, Font.BOLD)));
 				
 				cellE.setBorderWidth(0);
 				empTable.addCell(cellE);

 				if(payEmpHead.get(k).isEmpty()){
 					
 					PdfPCell cellcollan = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 8, Font.BOLD)));
 	 				cellcollan.setBorderWidth(0);
 	 				empTable.addCell(cellcollan);
 					
 				}else{
	 				PdfPCell cellcollan = new PdfPCell(new Phrase(":",FontFactory.getFont("Verdana", 8, Font.BOLD)));
	 				cellcollan.setBorderWidth(0);
	 				empTable.addCell(cellcollan);
 				}
 				PdfPCell cellF = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 8, Font.NORMAL)));

 				cellF.setBorderWidth(0);
 				empTable.addCell(cellF);
 			}

 			PdfPTable imageEmpDetailTable = new PdfPTable(1);

 			int[] arrheaderwidths4 = { 100}; // percentage

 			imageEmpDetailTable.getDefaultCell().setBorderWidth(1);
 			imageEmpDetailTable.getDefaultCell().setPadding(1);
 			imageEmpDetailTable.setWidths(arrheaderwidths4);
 			
 			
 			PdfPTable  titleTable = new PdfPTable (1);
			int[] arrTitle = { 100}; // percentage
			titleTable.getDefaultCell().setBorderWidth(1);
			titleTable.getDefaultCell().setPadding(1);
			titleTable.setWidths(arrTitle);

			PdfPCell cellT = new PdfPCell(new Phrase("           Gross Earnings and Amount(INR)                               Deductions and Amount(INR)",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellT.setHorizontalAlignment(Element.ALIGN_LEFT);
			cellT.setBorderWidth(1);
			titleTable.addCell(cellT);

			PdfPTable grossTitalTable = new PdfPTable(2);
			int[] arrheaderwidths7 = { 60,40 }; // percentage
			grossTitalTable.getDefaultCell().setBorderWidth(1);
			grossTitalTable.getDefaultCell().setPadding(1);
			grossTitalTable.setWidths(arrheaderwidths7);

			PdfPTable GrossHeadTable = new PdfPTable(1);
			int[] arrheaderwidths7A = { 100 }; // percentage
			GrossHeadTable.getDefaultCell().setBorderWidth(1);
			GrossHeadTable.getDefaultCell().setPadding(1);
			GrossHeadTable.setWidths(arrheaderwidths7A);

			PdfPTable grossAmtTable = new PdfPTable(1);
			int[] arrheaderwidths7B = { 100 }; // percentage
			grossAmtTable.getDefaultCell().setBorderWidth(1);
			grossAmtTable.getDefaultCell().setPadding(1);
			grossAmtTable.setWidths(arrheaderwidths7B);
			
			/*PdfPTable tableScale = new PdfPTable(1);
			int[] arrheaderwidths7C = { 100 }; // percentage
			tableScale.getDefaultCell().setBorderWidth(1);
			tableScale.getDefaultCell().setPadding(1);
			tableScale.setWidths(arrheaderwidths7C);*/
			
			
			PdfPTable totalGrossTitalTable = new PdfPTable(2);
			int[] arrheaderwidths77 = { 60,40 }; // percentage
			totalGrossTitalTable.getDefaultCell().setBorderWidth(1);
			totalGrossTitalTable.getDefaultCell().setPadding(1);
			totalGrossTitalTable.setWidths(arrheaderwidths77);
			
			PdfPTable totalGrossHeadTable = new PdfPTable(1);
			int[] arrheaderwidths7AA = { 100 }; // percentage
			totalGrossHeadTable.getDefaultCell().setBorderWidth(1);
			totalGrossHeadTable.getDefaultCell().setPadding(1);
			totalGrossHeadTable.setWidths(arrheaderwidths7AA);

			PdfPTable totalGrossAmtTable = new PdfPTable(1);
			int[] arrheaderwidths7BB = { 100 }; // percentage
			totalGrossAmtTable.getDefaultCell().setBorderWidth(1);
			totalGrossAmtTable.getDefaultCell().setPadding(1);
			totalGrossAmtTable.setWidths(arrheaderwidths7BB);
			

			int nCount = 0;
			Iterator<String> itr1 = salaryHeadName.iterator();
			while (itr1.hasNext()) {

				String strHeadNM = itr1.next();

				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
					
					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
					GrossHeadTable.addCell(cellQ2);
//					
//					PdfPCell cellQS = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));
//					cellQS.setBorderWidth(0);
//					tableScale.addCell(cellQS);

					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidthTop(1);
					cellQ3.setBorderWidthBottom(0);
					cellQ3.setBorderWidthLeft(0);
					cellQ3.setBorderWidthRight(0);
					grossAmtTable.addCell(cellQ3);

				} else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					GrossHeadTable.addCell(cellQ2);

//					PdfPCell cellQS = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));
//					cellQS.setBorderWidth(0);
//					tableScale.addCell(cellQS);
					
					PdfPCell cellQ3 = new PdfPCell(new Phrase(" ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidth(0);
					grossAmtTable.addCell(cellQ3);
				}

				nCount++;

			}
			
			
			PdfPCell cellQ22 = new PdfPCell(new Phrase("Total Gross Earnings (INR)",FontFactory.getFont("Verdana", 8, Font.BOLD)));
			cellQ22.setBorderWidth(0);
			totalGrossHeadTable.addCell(cellQ22);
			
			PdfPCell cellQ33 = new PdfPCell(new Phrase(" ", FontFactory.getFont("Verdana", 8, Font.BOLD)));

			cellQ33.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cellQ33.setBorderWidthTop(0);
			cellQ33.setBorderWidthBottom(0);
			cellQ33.setBorderWidthLeft(0);
			cellQ33.setBorderWidthRight(0);
			totalGrossAmtTable.addCell(cellQ33);

			grossTitalTable.addCell(GrossHeadTable);
//			grossTitalTable.addCell(tableScale);
			grossTitalTable.addCell(grossAmtTable);
			
			totalGrossTitalTable.addCell(totalGrossHeadTable);
			totalGrossTitalTable.addCell(totalGrossAmtTable);
			
//********************************************************************************************
			
			PdfPTable netGrossTable = new PdfPTable(2);
			int[] arrheaderwidths6A = { 50, 50 }; // percentage
			netGrossTable.getDefaultCell().setBorderWidth(1);
			netGrossTable.getDefaultCell().setPadding(1);
			netGrossTable.setWidths(arrheaderwidths6A);

			PdfPCell cellQA1 = new PdfPCell(new Phrase("Organisations Contribution ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellQA1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellQA1.setBorderWidth(1);
			netGrossTable.addCell(cellQA1);

			
			PdfPCell cellQA2 = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellQA2.setBorderWidth(1);
			cellQA2.setHorizontalAlignment(Element.ALIGN_CENTER);
			netGrossTable.addCell(cellQA2);
			
 //******************************************************************************************			
 			
			PdfPTable leaveTable = new PdfPTable(2);
			int[] arrheaderwidths8 = { 60, 40 }; // percentage
			leaveTable.getDefaultCell().setBorderWidth(1);
			leaveTable.getDefaultCell().setPadding(1);
			leaveTable.setWidths(arrheaderwidths8);

			PdfPTable leaveHeadTable = new PdfPTable(1);
			int[] arrheaderwidths8A = { 100 }; // percentage
			leaveHeadTable.getDefaultCell().setBorderWidth(1);
			leaveHeadTable.getDefaultCell().setPadding(1);
			leaveHeadTable.setWidths(arrheaderwidths8A);

			PdfPTable leaveAmtTable = new PdfPTable(1);
			int[] arrheaderwidths8B = { 100 }; // percentage
			leaveAmtTable.getDefaultCell().setBorderWidth(1);
			leaveAmtTable.getDefaultCell().setPadding(1);
			leaveAmtTable.setWidths(arrheaderwidths8B);
			
			PdfPTable totalLeaveTable = new PdfPTable(2);
			int[] arrheaderwidths8888 = { 60, 40 }; // percentage
			totalLeaveTable.getDefaultCell().setBorderWidth(1);
			totalLeaveTable.getDefaultCell().setPadding(1);
			totalLeaveTable.setWidths(arrheaderwidths8888);
			
			PdfPTable totalLeaveHeadTable = new PdfPTable(1);
			int[] arrheaderwidths8AA = { 100 }; // percentage
			totalLeaveHeadTable.getDefaultCell().setBorderWidth(1);
			totalLeaveHeadTable.getDefaultCell().setPadding(1);
			totalLeaveHeadTable.setWidths(arrheaderwidths8AA);

			PdfPTable totalLeaveAmtTable = new PdfPTable(1);
			int[] arrheaderwidths8BB = { 100 }; // percentage
			totalLeaveAmtTable.getDefaultCell().setBorderWidth(1);
			totalLeaveAmtTable.getDefaultCell().setPadding(1);
			totalLeaveAmtTable.setWidths(arrheaderwidths8BB);

			int nCountGross = 0;
			Iterator<String> itr12 = deductionHeadName.iterator();
			while (itr12.hasNext()) {

				String strHeadNM = itr12.next();

				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
					leaveHeadTable.addCell(cellQ2);

					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidthTop(1);
					cellQ3.setBorderWidthBottom(0);
					cellQ3.setBorderWidthLeft(0);
					cellQ3.setBorderWidthRight(0);
					leaveAmtTable.addCell(cellQ3);
				} else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,
							FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					leaveHeadTable.addCell(cellQ2);
					
					PdfPCell cellQ3 = new PdfPCell(new Phrase(" ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidth(0);
					leaveAmtTable.addCell(cellQ3);

				}

				nCountGross++;

			}
			
			PdfPCell cellQ222 = new PdfPCell(new Phrase("Total Deductions (INR)",FontFactory.getFont("Verdana", 8, Font.BOLD)));
			cellQ222.setBorderWidth(0);
			totalLeaveHeadTable.addCell(cellQ222);

			PdfPCell cellQ333 = new PdfPCell(new Phrase(" ", FontFactory.getFont("Verdana", 8, Font.BOLD)));

			cellQ333.setHorizontalAlignment(Element.ALIGN_RIGHT);

			cellQ333.setBorderWidthTop(0);
			cellQ333.setBorderWidthBottom(0);
			cellQ333.setBorderWidthLeft(0);
			cellQ333.setBorderWidthRight(0);
			totalLeaveAmtTable.addCell(cellQ333);
			
			
			PdfPCell cellQ55 = new PdfPCell(new Phrase("Net Payable (INR)",FontFactory.getFont("Verdana", 8, Font.BOLD)));
//			cellQ55.setBorderWidth(0);
			cellQ55.setBorderWidthTop(1);
			cellQ55.setBorderWidthBottom(0);
			cellQ55.setBorderWidthLeft(0);
			cellQ55.setBorderWidthRight(0);
			totalLeaveHeadTable.addCell(cellQ55);

			PdfPCell cellQ66 = new PdfPCell(new Phrase(" ", FontFactory.getFont("Verdana", 8, Font.BOLD)));

			cellQ66.setHorizontalAlignment(Element.ALIGN_RIGHT);

			cellQ66.setBorderWidthTop(1);
			cellQ66.setBorderWidthBottom(0);
			cellQ66.setBorderWidthLeft(0);
			cellQ66.setBorderWidthRight(0);
			totalLeaveAmtTable.addCell(cellQ66);

			leaveTable.addCell(leaveHeadTable);
			leaveTable.addCell(leaveAmtTable);
			
			totalLeaveTable.addCell(totalLeaveHeadTable);
			totalLeaveTable.addCell(totalLeaveAmtTable);
			
//*************************************************************************
			
			PdfPTable NetSalaryTable = new PdfPTable(1);
			int[] arrheaderwidths13 = { 100}; // percentage
			NetSalaryTable.getDefaultCell().setBorderWidth(1);
			NetSalaryTable.getDefaultCell().setPadding(1);
			NetSalaryTable.setWidths(arrheaderwidths13);
			
			String strAmountInWord="Amount in Words";
			
			PdfPCell cellwordAmt = new PdfPCell(new Phrase(strAmountInWord,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellwordAmt.setBorderWidth(1);
			cellwordAmt.setHorizontalAlignment(Element.ALIGN_RIGHT);
			NetSalaryTable.addCell(cellwordAmt);

//**********************************************************************************************	

			PdfPTable Deductiontable = new PdfPTable(2);
			int[] arrheaderwidths9 = { 50, 50 }; // percentage
			Deductiontable.getDefaultCell().setBorderWidth(1);
			Deductiontable.getDefaultCell().setPadding(1);
			Deductiontable.setWidths(arrheaderwidths9);

			PdfPCell cellQ3 = new PdfPCell(grossTitalTable);
			cellQ3.setBorderWidth(1);
			Deductiontable.addCell(cellQ3);

			PdfPCell cellR = new PdfPCell(leaveTable);
			cellR.setBorderWidth(1);
			Deductiontable.addCell(cellR);
			
			PdfPTable totalDeductiontable = new PdfPTable(2);
			int[] arrheaderwidths99 = { 50, 50 }; // percentage
			totalDeductiontable.getDefaultCell().setBorderWidth(1);
			totalDeductiontable.getDefaultCell().setPadding(1);
			totalDeductiontable.setWidths(arrheaderwidths99);
			
			PdfPCell cellQ3333 = new PdfPCell(totalGrossTitalTable);
			cellQ3.setBorderWidth(1);
			totalDeductiontable.addCell(cellQ3333);

			PdfPCell cellRR = new PdfPCell(totalLeaveTable);
			cellR.setBorderWidth(1);
			totalDeductiontable.addCell(cellRR);

			PdfPTable HeadleaveDeductionTable = new PdfPTable(2);
			int[] arrheaderwidths12 = { 50, 50 }; // percentage
			HeadleaveDeductionTable.getDefaultCell().setBorderWidth(1);
			HeadleaveDeductionTable.getDefaultCell().setPadding(1);
			HeadleaveDeductionTable.setWidths(arrheaderwidths12);

			PdfPCell celld = new PdfPCell(new PdfPCell(empTable));

			PdfPTable orgTable = new PdfPTable(3);
			int[] arrorg = { 30, 20 ,50}; // percentage
			orgTable.getDefaultCell().setBorderWidth(1);
			orgTable.getDefaultCell().setPadding(1);
			orgTable.setWidths(arrorg);

			PdfPTable orgHeadTable = new PdfPTable(1);
			int[] arrOrgA = { 100 }; // percentage
			orgHeadTable.getDefaultCell().setPadding(1);
			orgHeadTable.setWidths(arrOrgA);

			PdfPTable orgAmtTable = new PdfPTable(1);
			int[] arrOrgB = { 100 }; // percentage
			orgAmtTable.getDefaultCell().setPadding(1);
			orgAmtTable.setWidths(arrOrgB);
			
			PdfPTable orgBlank = new PdfPTable(1);
			int[] arrOrgC = { 100 }; // percentage
			orgBlank.getDefaultCell().setBorderWidth(1);
			orgBlank.getDefaultCell().setPadding(1);
			orgBlank.setWidths(arrOrgC);

			List<String> orgHeadAmt = new ArrayList<String>();
			List<String> orgHeadName = new ArrayList<String>();
			orgHeadName.add("Employer PF (O.C)");
			orgHeadName.add("Total (O.C)");
			
			orgHeadAmt.add(String.valueOf("0.00"));
			orgHeadAmt.add(String.valueOf("0.00"));
			
			int countOrg = 0;
			Iterator<String> itrOrg = orgHeadName.iterator();
			while (itrOrg.hasNext()) {

				String strHeadNM = itrOrg.next();

				PdfPCell cellOrg = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellOrg.setBorderWidth(0);
				orgHeadTable.addCell(cellOrg);
					
				PdfPCell cellOrg1 = new PdfPCell(new Phrase(""+ orgHeadAmt.get(countOrg), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellOrg1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellOrg1.setBorderWidth(0);
				orgAmtTable.addCell(cellOrg1);
				
				countOrg++;

			}
			
			PdfPCell cellOrg2 = new PdfPCell(new Phrase("Remark : ",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellOrg2.setHorizontalAlignment(Element.ALIGN_LEFT);
			cellOrg2.setBorderWidth(0);
			orgBlank.addCell(cellOrg2);
			
			orgTable.addCell(orgHeadTable);
			orgTable.addCell(orgAmtTable);
			orgTable.addCell(orgBlank);
			
//***************************************************************************************************
			
			PdfPTable LastTable = new PdfPTable(1);
			LastTable.getDefaultCell().setBorderWidth(0);
			LastTable.getDefaultCell().setPadding(0);
			LastTable.setWidths(arrheaderwidths2);

			PdfPCell cellSign = new PdfPCell(new Phrase("This is Computer generated Slip so does not required any signature",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellSign.getExtraParagraphSpace();
			cellSign.setBorderWidth(1);
			cellSign.setHorizontalAlignment(Element.ALIGN_CENTER);
			LastTable.addCell(cellSign);
			
			/*PdfPTable LastTable = new PdfPTable(2);
			int[] arrNo = { 50, 50 }; // percentage
			LastTable.getDefaultCell().setBorderWidth(1);
			LastTable.getDefaultCell().setPadding(1);
			LastTable.setWidths(arrNo);
			
			PdfPCell cellNo = new PdfPCell(new Phrase("TOLL FREE NO: 180 266 2006 ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellNo.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellNo.setBorderWidth(1);
			LastTable.addCell(cellNo);
			
			PdfPCell cellSign = new PdfPCell(new Phrase("This is Computer generated Slip so does not required any signature",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellSign.setBorderWidth(1);
			cellSign.setHorizontalAlignment(Element.ALIGN_CENTER);
			LastTable.addCell(cellSign);*/
			
//**************************************************************************************************

			imageEmpDetailTable.addCell(celld);
			document.add(LogoImage);
			document.add(titalTable);
			document.add(imageEmpDetailTable);
			document.add(titleTable);
			document.add(Deductiontable);
			document.add(totalDeductiontable);
			document.add(NetSalaryTable);
//			document.add(netGrossTable);
			document.add(HeadleaveDeductionTable);
//			document.add(orgTable);
			document.add(LastTable);
			document.close();
 		
			response.setContentType("application/pdf");
			response.setContentLength(baos.size());
			response.setHeader("Content-Disposition","attachment; filename=PaySlip.pdf");
			ServletOutputStream out = response.getOutputStream();              
			baos.writeTo(out);
			out.flush();
			out.close();
			baos.close();
			out.close();
 				
			return;
 			
 		}catch (Exception e) {
 			e.printStackTrace();
 		}
	
	}
	//===end===

	public HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;

	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getPaySlipFormatId() {
		return paySlipFormatId;
	}

	public void setPaySlipFormatId(String paySlipFormatId) {
		this.paySlipFormatId = paySlipFormatId;
	}
	
}
