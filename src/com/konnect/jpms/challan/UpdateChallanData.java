package com.konnect.jpms.challan;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateChallanData extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
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
	double totalTaxAmount;
	String paidDate;
	String challanDate;
	String challanNum;
	String operation;
	String cheque_no;
	String sessionEmp_id;
	
	String paid_from=null;
	String paid_to=null;
	
	String orgid;
	String ispaid;
//	String locationId;
	
	String amtTax;
	String interestAmt;
	String penaltyAmt;
	String compositionMoney;
	String fineAmt;
	String feesAmt;
	String advanceAmt;
	String totalAmt;
//	String stateId;
	String state;
	
	String sbEmp;
	
	
	
	public String execute() throws Exception {

		//System.out.println("in UpdateChallanData");
		//System.out.println("sbEmp in updateChallanData==>"+sbEmp);
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		sessionEmp_id=(String) session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getChallanDate()!=null && getOperation()!=null && getOperation().equalsIgnoreCase("del")){
			deleteChallanDetails();
		}else if(getChallanDate()!=null && getChallanNum()!=null && getOperation().equalsIgnoreCase("update")){
			updateChallanNumber();
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("insert")){
			insertUnpaidAmount();
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("otherCharges")){
			updateOtherCharges();
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("pdf")){
			//System.out.println("in opration=pdf function");
			getOrganizationDetails();
			
			if(uF.parseToInt(getState()) == 21){
				//System.out.println("in getstat 21");
				 viewFormPTChallanPdfReports();	
				 generatePdfKarnataka();
			}else if(uF.parseToInt(getState()) == 25){
				//System.out.println("in getstat 25");
				viewFormPTChallanPdfReports();	
				generatePdfTelangana();
			} else {
				//System.out.println("above viewForm5PTChallanPdfReports ");

				viewForm5PTChallanPdfReports();
				generateNewForm5PTChallanPdfReports();
			}
			
		}
		return SUCCESS;
	}
	
	private void generatePdfKarnataka() {
		//System.out.println("in generatePdfKarnataka==");
		
		try {
			
			UtilityFunctions uF = new UtilityFunctions();
			Map<String, String> hmEmpProfile = (Map<String, String>)request.getAttribute("hmEmpProfile");
			if(hmEmpProfile == null) hmEmpProfile = new HashMap<String, String>();
			
			List<Integer> alList=(List<Integer>)request.getAttribute("alList");
			Map<String,String> hmempcnt =(Map<String,String> )request.getAttribute("hmempcnt");
			Map<Integer,Map<String,String>> hmMap=(Map<Integer,Map<String,String>>)request.getAttribute("hmMap");
			if(hmMap==null)hmMap=new HashMap<Integer, Map<String,String>>();
			
			Map<String, Map<String, String>> hmOrg =(Map<String, Map<String, String>>)request.getAttribute("hmOrg");
			if(hmOrg==null)hmOrg=new HashMap<String, Map<String,String>>();
			Map<String,String> hmOrgDetails =hmOrg.get(getOrgid());
			
			Map<String,String> hmWorkLocationDetails = (Map<String,String>) request.getAttribute("hmWorkLocationDetails");
			if(hmWorkLocationDetails==null)hmWorkLocationDetails=new HashMap<String,String>();
			
			Map<String,String> hmPTOtherCharge = (Map<String,String>)request.getAttribute("hmPTOtherCharge");
			if(hmPTOtherCharge == null) hmPTOtherCharge = new HashMap<String,String>();
			
			Map<String, Map<String, String>> hmPTSlab = (Map<String, Map<String, String>>) request.getAttribute("hmPTSlab");
			if(hmPTSlab == null) hmPTSlab=new LinkedHashMap<String, Map<String, String>>();

			Map<String, Map<String, String>> hmPTDetails = (Map<String, Map<String, String>>)request.getAttribute("hmPTDetails");
			if(hmPTDetails == null) hmPTDetails = new LinkedHashMap<String, Map<String, String>>();
			
			String strChequeNo = (String) request.getAttribute("strChequeNo");
			String strChequeDate = (String) request.getAttribute("strChequeDate");
			String strMonthDetails = (String) request.getAttribute("strMonthDetails");
			
			Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
			Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
			Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			Font small = new Font(Font.FontFamily.HELVETICA,10);
			Font smallBold = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD);
			Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        Document document = new Document(PageSize.A4);
	        PdfWriter.getInstance(document,buffer);
	        document.open();
	        
	        PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);        
	        
			// New Row
			PdfPCell row1 =new PdfPCell(new Paragraph("",smallBold));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
			
		    //new row
	        String heading1="FORM 5-A";        
	        row1 =new PdfPCell(new Paragraph(heading1,smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER); 
	        row1.setBorder(Rectangle.NO_BORDER);        
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("[See Rule 11-A]",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f); 
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Statement of tax payable by employer under sub-section (1) of Section 6-A" ,small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("1. Amount of Tax payable for the month [or quarter] ending on ............",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("2. Name of the Employer:  "+uF.showData(hmOrgDetails.get("ORG_NAME"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("3. Address:  "+uF.showData(hmOrgDetails.get("ORG_ADDRESS"), "")+"-"+uF.showData(hmOrgDetails.get("ORG_PINCODE"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("4. Registration Certificate No: "+uF.showData(hmWorkLocationDetails.get("WL_PT_RC_EC"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("5. Number of employers during the month 3[or quarter] in respect of whom the tax is payable is as under:-........",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM);  
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM);  
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	
	        //new row
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT |Rectangle.BOTTOM);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        
	        row1 =new PdfPCell(new Paragraph("Employees whose monthly Salaries or Wages or both are",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Number of Employees",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Rate of tax per month",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Amount of Tax deducted",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT |Rectangle.BOTTOM);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("(1)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("(2)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("(3)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("(4)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        Iterator<String> it = hmPTSlab.keySet().iterator();
	        int i = 0;
	        double dblTaxAmt = 0.0d;
	        char ch='a';
	        int totalEmployess=0;
	        while(it.hasNext()){
	        	String strAmount = it.next();
	        	Map<String, String> hmPTSlabDetails = hmPTSlab.get(strAmount);
	        	Map<String, String> hmSalPT = hmPTDetails.get(strAmount);
	        	if (hmSalPT == null) hmSalPT = new HashMap<String, String>();
	        	
	        	String strMsg = "";
	        	String strRate = "";
	        	 totalEmployess +=uF.parseToInt(hmSalPT.get("EMP_COUNT"));
	        	String strTotalAmt = uF.showData(hmSalPT.get("TOTAL_AMOUNT"), "0");
	        	dblTaxAmt += uF.parseToDouble(hmSalPT.get("TOTAL_AMOUNT"));
	        	if(i==0){
	        		strMsg = "Not less than Rs.0 but less than Rs."+ hmPTSlabDetails.get("INCOME_TO");
	        		strRate = "Nil";
	        		strTotalAmt = "Nil";
	        	} else if(i == hmPTSlab.size()-1){
	        		strMsg = "Not less than Rs."+hmPTSlabDetails.get("INCOME_FROM");
	        		double dblAnnualamt = uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_AMOUNT"));
	        		strRate = "Rs."+(uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_AMOUNT"))) +" per annum to be paid in the following" +
	        				" manner:-\n(a)Rs."+hmPTSlabDetails.get("DEDUCTION_PAYCYCLE")+" per month except in the month of February\n" +
	        				"(b)Rs."+(uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_PAYCYCLE"))+100)+" per month of February";
	        	} else {
	        		strMsg = "Not less than Rs. "+hmPTSlabDetails.get("INCOME_FROM")+" but less than Rs. "+hmPTSlabDetails.get("INCOME_TO");
	        		strRate = "Rs."+hmPTSlabDetails.get("DEDUCTION_PAYCYCLE");
	        	}
	        	
	        	row1 =new PdfPCell(new Paragraph(ch+".",small));
	   	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	   	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT |Rectangle.BOTTOM);  
	   	        row1.setPadding(2.5f);
	   	        row1.setPaddingTop(4.0f);
	   	        table.addCell(row1);
	   	        
	   	     
		        row1 =new PdfPCell(new Paragraph(strMsg,small));
		        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(4.0f);
		        row1.setColspan(2);
		        table.addCell(row1);
		        
		       
		        row1 =new PdfPCell(new Paragraph(new Paragraph(uF.showData(hmSalPT.get("EMP_COUNT"), "0"),small)));
		        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(4.0f);
		        table.addCell(row1);
		        
		        row1 =new PdfPCell(new Paragraph(strRate,small));
		        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(4.0f);
		        table.addCell(row1);
		        
		        row1 =new PdfPCell(new Paragraph(strTotalAmt,small));
		        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(4.0f);
		        table.addCell(row1);
		        
		        i++;
		        ch++;
		        
	        }
	        // new row
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT |Rectangle.BOTTOM);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("TOTAL",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+totalEmployess,small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	      
	        row1 =new PdfPCell(new Paragraph(""+dblTaxAmt,small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        // new row
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT |Rectangle.BOTTOM);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Add interest if any payabl under Section 11(2) of the Act",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT |Rectangle.BOTTOM);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);

	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT |Rectangle.BOTTOM);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT |Rectangle.BOTTOM);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        // new row
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT |Rectangle.BOTTOM);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("GRAND TOTAL",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        

	        row1 =new PdfPCell(new Paragraph(""+totalEmployess,small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT |Rectangle.BOTTOM);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        

	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT |Rectangle.BOTTOM);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+dblTaxAmt,small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT |Rectangle.BOTTOM);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(4.0f);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("  Amount paid under Challan No:"+uF.showData(getChallanNum(), ".............")+"   Dated : "+uF.showData(getChallanDate(), "............."),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        StringBuilder sbCertify = new StringBuilder();
	        sbCertify.append("             I certify that all the employees who are liable to pay the tax in may ");
	        sbCertify.append("employ during the period of statement have been covered by the foregoing");
	        sbCertify.append("deductable from the salary or wages of the employees on account of variation");
	        sbCertify.append("inthe salary or wages earned by them has been made where necessary.");
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph(sbCertify.toString(),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	       
	        //new row
	        row1 =new PdfPCell(new Paragraph("I, "+uF.showData(hmEmpProfile.get("NAME"), "")+" solemnly declare that the above statements are true to the best of my knowledge and beleif.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Place: "+uF.showData(hmWorkLocationDetails.get("WL_NAME"), ""),small)); 
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	       
	        row1 =new PdfPCell(new Paragraph("Signature of the employer:_______________",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);

	        //new row
	        row1 =new PdfPCell(new Paragraph("Status:",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(5);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM);  
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        document.add(table); 
	        document.close();
	        
			String filename="PTChallan_"+uF.getDateFormat(getChallanDate(),DBDATE, "MM")+"_"+uF.getDateFormat(getChallanDate(),DBDATE, "yyyy")+".pdf";
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

	private void generatePdfTelangana() {
		
		try {
			
			UtilityFunctions uF = new UtilityFunctions();
			Map<String, String> hmEmpProfile = (Map<String, String>)request.getAttribute("hmEmpProfile");
			if(hmEmpProfile == null) hmEmpProfile = new HashMap<String, String>();
			
			List<Integer> alList=(List<Integer>)request.getAttribute("alList");
			Map<String,String> hmempcnt =(Map<String,String> )request.getAttribute("hmempcnt");
			Map<Integer,Map<String,String>> hmMap=(Map<Integer,Map<String,String>>)request.getAttribute("hmMap");
			if(hmMap==null)hmMap=new HashMap<Integer, Map<String,String>>();
			
			Map<String, Map<String, String>> hmOrg =(Map<String, Map<String, String>>)request.getAttribute("hmOrg");
			if(hmOrg==null)hmOrg=new HashMap<String, Map<String,String>>();
			Map<String,String> hmOrgDetails =hmOrg.get(getOrgid());
			
			Map<String,String> hmWorkLocationDetails = (Map<String,String>) request.getAttribute("hmWorkLocationDetails");
			if(hmWorkLocationDetails==null)hmWorkLocationDetails=new HashMap<String,String>();
			
			Map<String,String> hmPTOtherCharge = (Map<String,String>)request.getAttribute("hmPTOtherCharge");
			if(hmPTOtherCharge == null) hmPTOtherCharge = new HashMap<String,String>();
			
			Map<String, Map<String, String>> hmPTSlab = (Map<String, Map<String, String>>) request.getAttribute("hmPTSlab");
			if(hmPTSlab == null) hmPTSlab=new LinkedHashMap<String, Map<String, String>>();
			
			Map<String, Map<String, String>> hmPTDetails = (Map<String, Map<String, String>>)request.getAttribute("hmPTDetails");
			if(hmPTDetails == null) hmPTDetails = new LinkedHashMap<String, Map<String, String>>();
			
			String strChequeNo = (String) request.getAttribute("strChequeNo");
			String strChequeDate = (String) request.getAttribute("strChequeDate");
			String strMonthDetails = (String) request.getAttribute("strMonthDetails");
			
			Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
			Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
			Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			Font small = new Font(Font.FontFamily.HELVETICA,10);
			Font smallBold = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD);
			Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        Document document = new Document(PageSize.A4);
	        PdfWriter.getInstance(document,buffer);
	        document.open();
	        
	        PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);        
	        
			// New Row
			PdfPCell row1 =new PdfPCell(new Paragraph("",smallBold));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
			
		    //new row
	        String heading1="FORM VReturns of Tax Payable by Employer under sub-section (1) of Section 7 of the Andhra Pradesh Tax on Professions, Trades, Callings and Employments";        
	        row1 =new PdfPCell(new Paragraph(heading1,smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER); 
	        row1.setBorder(Rectangle.NO_BORDER);        
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Act, 1987(See Rule 12)",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f); 
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph(" Return of tax payable for the month ending on : ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      
	        //new row
	        row1 =new PdfPCell(new Paragraph("Name of the Employer : "+uF.showData(hmOrgDetails.get("ORG_NAME"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Address : "+uF.showData(hmOrgDetails.get("ORG_ADDRESS"), "")+"-"+uF.showData(hmOrgDetails.get("ORG_PINCODE"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Registration Certificate No: "+uF.showData(hmWorkLocationDetails.get("WL_PT_RC_EC"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Number of employees during the month in respect of whom the tax is payable is as under : ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Employees whose monthly \n salaries or wages or both are",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM );  
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Number of \n employee",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Rate of Tax per month",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Amount of tax deduction",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        table.addCell(row1);
	    
	        //new row
	        Iterator<String> it = hmPTSlab.keySet().iterator();
	        int i = 0;
	        double dblTaxAmt = 0.0d;
	        char ch='a';
	        int totalEmployess=0;
	        while(it.hasNext()){
	        	String strAmount = it.next();
	        	Map<String, String> hmPTSlabDetails = hmPTSlab.get(strAmount);
	        	Map<String, String> hmSalPT = hmPTDetails.get(strAmount);
	        	if (hmSalPT == null) hmSalPT = new HashMap<String, String>();
	        	
	        	String strMsg = "";
	        	String strRate = "";
	        	 totalEmployess +=uF.parseToInt(hmSalPT.get("EMP_COUNT"));
	        	String strTotalAmt = uF.showData(hmSalPT.get("TOTAL_AMOUNT"), "0");
	        	dblTaxAmt += uF.parseToDouble(hmSalPT.get("TOTAL_AMOUNT"));
	        	if(i==0){
	        		strMsg = "("+ch+")"+" Upto "+ hmPTSlabDetails.get("INCOME_TO")+"/-";
	        		strRate = "Nil";
	        		strTotalAmt = "Nil";
	        	} else if(i == hmPTSlab.size()-1){
	        		strMsg = "("+ch+")"+" Above "+hmPTSlabDetails.get("INCOME_FROM");
	        		double dblAnnualamt = uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_AMOUNT"));
	        		strRate = "Rs."+(uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_AMOUNT"))) +" per annum to be paid in the following" +
	        				" manner:-\n(a)Rs."+hmPTSlabDetails.get("DEDUCTION_PAYCYCLE")+" per month except in the month of February\n" +
	        				"(b)Rs."+(uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_PAYCYCLE"))+100)+" per month of February";
	        	} else {
	        		strMsg = "("+ch+")"+" From "+hmPTSlabDetails.get("INCOME_FROM")+" to "+hmPTSlabDetails.get("INCOME_TO");
	        		strRate = "Rs."+hmPTSlabDetails.get("DEDUCTION_PAYCYCLE");
	        	}
	        	
	        	
	        	row1 =new PdfPCell(new Paragraph(strMsg,small));
	 	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	 	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT );  
	 	        row1.setPadding(2.5f);
	 	        row1.setIndent(5.0f);
	 	        row1.setColspan(3);
	 	        table.addCell(row1);
	 	        
	 	        row1 =new PdfPCell(new Paragraph(new Paragraph(uF.showData(hmSalPT.get("EMP_COUNT"), "0"),small)));
	 	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	 	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	 	        row1.setPadding(2.5f);
	 	        row1.setIndent(5.0f);
	 	        table.addCell(row1);
	 	        
	 	        row1 =new PdfPCell(new Paragraph(strRate,small));
	 	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	 	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	 	        row1.setPadding(2.5f);
	 	        row1.setIndent(5.0f);
	 	        table.addCell(row1);
	 	        
	 	        row1 =new PdfPCell(new Paragraph(strTotalAmt,small));
	 	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	 	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	 	        row1.setPadding(2.5f);
	 	        row1.setIndent(5.0f);
	 	        table.addCell(row1);
	        	
		        
		        i++;
//		        System.out.println("Ch--"+ch);
		        ch++;
	        }
	    
	        // new row
	        row1 =new PdfPCell(new Paragraph("TOTAL  RS.  ",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM );  
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+totalEmployess,small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+dblTaxAmt,small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        table.addCell(row1);
	        
	        // new row 
	        row1 =new PdfPCell(new Paragraph("Add Simple interest payable (if \n any;) on the above amount at \n two per cent per month or part \n thereof (Vide Section 11 of the Act).",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT	);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM );  
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        table.addCell(row1);
	        
	        // new row 
	        row1 =new PdfPCell(new Paragraph("Grand Total",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT	);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM );  
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+totalEmployess,small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+dblTaxAmt,small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM ); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("  Amount paid under Challan No:"+uF.showData(getChallanNum(), ".............")+"   Dated : "+uF.showData(getChallanDate(), "............."),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);

	        StringBuilder sbCertify = new StringBuilder();
	        sbCertify.append("             I certify that all the employees who are liable to pay the tax in may ");
	        sbCertify.append("employ during the period of statement have been covered by the foregoing");
	        sbCertify.append("deductable from the salary or wages of the employees on account of variation");
	        sbCertify.append("inthe salary or wages earned by them has been made where necessary.");
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph(sbCertify.toString(),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	       
	        //new row
	        row1 =new PdfPCell(new Paragraph("I, "+uF.showData(hmEmpProfile.get("NAME"), "")+" solemnly declare that the above statements are true to the best of my knowledge and beleif.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Place: "+uF.showData(hmWorkLocationDetails.get("WL_NAME"), ""),small)); 
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	       
	        row1 =new PdfPCell(new Paragraph("Signature of the employer:_______________",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Date:",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Status:_______________",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);  
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(5);
	        table.addCell(row1);
	        
	      //new row
	        row1 =new PdfPCell(new Paragraph("(For Office Use) The return is accepted on verification ",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f); 
	        row1.setPaddingTop(25.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Tax Assessed Rs. __________________________",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f); 
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Tax Paid Rs. __________________________",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f); 
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Balance Rs. __________________________",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f); 
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Assessing Authority     ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f); 
	        row1.setPaddingTop(20.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Note:  Where the Return is not acceptable, separate order of assessment should be passed.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f); 
	        row1.setPaddingTop(12.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        document.add(table); 
	        document.close();
	        
			String filename="PTChallan_"+uF.getDateFormat(getChallanDate(),DBDATE, "MM")+"_"+uF.getDateFormat(getChallanDate(),DBDATE, "yyyy")+".pdf";
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
	
/*	private void generatePdfKarnataka() {
		
		try {
			
			UtilityFunctions uF = new UtilityFunctions();
			Map<String, String> hmEmpProfile = (Map<String, String>)request.getAttribute("hmEmpProfile");
			if(hmEmpProfile == null) hmEmpProfile = new HashMap<String, String>();
			
			List<Integer> alList=(List<Integer>)request.getAttribute("alList");
			Map<String,String> hmempcnt =(Map<String,String> )request.getAttribute("hmempcnt");
			Map<Integer,Map<String,String>> hmMap=(Map<Integer,Map<String,String>>)request.getAttribute("hmMap");
			if(hmMap==null)hmMap=new HashMap<Integer, Map<String,String>>();
			
			Map<String, Map<String, String>> hmOrg =(Map<String, Map<String, String>>)request.getAttribute("hmOrg");
			if(hmOrg==null)hmOrg=new HashMap<String, Map<String,String>>();
			Map<String,String> hmOrgDetails =hmOrg.get(getOrgid());
			
			Map<String, Map<String, String>> hmWorkLocation =(Map<String, Map<String, String>>)request.getAttribute("hmWorkLocation");
			if(hmWorkLocation==null)hmWorkLocation=new HashMap<String, Map<String,String>>();
			Map<String,String> hmWorkLocationDetails =hmWorkLocation.get(getLocationId());
			
			Map<String,String> hmPTOtherCharge = (Map<String,String>)request.getAttribute("hmPTOtherCharge");
			if(hmPTOtherCharge == null) hmPTOtherCharge = new HashMap<String,String>();
			
			Map<String, Map<String, String>> hmPTSlab = (Map<String, Map<String, String>>) request.getAttribute("hmPTSlab");
			if(hmPTSlab == null) hmPTSlab=new LinkedHashMap<String, Map<String, String>>();
			
			Map<String, Map<String, String>> hmPTDetails = (Map<String, Map<String, String>>)request.getAttribute("hmPTDetails");
			if(hmPTDetails == null) hmPTDetails = new LinkedHashMap<String, Map<String, String>>();
			
			String strChequeNo = (String) request.getAttribute("strChequeNo");
			String strChequeDate = (String) request.getAttribute("strChequeDate");
			String strMonthDetails = (String) request.getAttribute("strMonthDetails");
			
			Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
			Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
			Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			Font small = new Font(Font.FontFamily.HELVETICA,7);
			Font smallBold = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD);
			Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        Document document = new Document(PageSize.A4);
	        PdfWriter.getInstance(document,buffer);
	        document.open();
	        
	        PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);        
	        
			// New Row
			PdfPCell row1 =new PdfPCell(new Paragraph("",smallBold));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
			
		    //new row
	        String heading1="CHALLAN";        
	        row1 =new PdfPCell(new Paragraph(heading1,smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER); 
	        row1.setBorder(Rectangle.NO_BORDER);        
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("(See Rule 11 A)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f); 
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("RETURN OF TAX PAYABLE BY EMPLOYER UNDER SUB-SECTION(1) OF section 6 A OF THE KARANATAKA\n" +
	        		"TAX ON PROFESSIONS, TRADES, CALLINGS AND EMPLOYMENTS ACT 1976",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //new row
	        row1 =new PdfPCell(new Paragraph("1 Name of the Employer\n"+uF.showData(hmOrgDetails.get("ORG_NAME"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT);  
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("3 Return on Tax Payable for the Month of "+uF.showData(strMonthDetails, ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.RIGHT); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	      //new row
	        row1 =new PdfPCell(new Paragraph("2 Address\n"+uF.showData(hmOrgDetails.get("ORG_ADDRESS"), "")+"-"+uF.showData(hmOrgDetails.get("ORG_PINCODE"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("4 Certificate No.  ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	      //new row
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.LEFT | Rectangle.RIGHT); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("5 P.T.O Circle Number PTO District Circle Bangalore",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT); 
	        row1.setPadding(2.5f);
	        row1.setIndent(5.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	      //new row
	        row1 =new PdfPCell(new Paragraph("Number of employees during the month in respect of whom the tax is payable is as under.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //new row
			row1 =new PdfPCell(new Paragraph("Employees whose monthly Salaries or Wages \nor both are",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Number of Employees",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Rate of tax per month",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Additional Tax",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
		        
	        row1 =new PdfPCell(new Paragraph("Amount of Tax deducted",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
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
	        	String strTotalAmt = uF.showData(hmSalPT.get("TOTAL_AMOUNT"), "0");
	        	dblTaxAmt += uF.parseToDouble(hmSalPT.get("TOTAL_AMOUNT"));
	        	
	        	strMsg = "Rs. "+hmPTSlabDetails.get("INCOME_FROM")+" to "+ hmPTSlabDetails.get("INCOME_TO");
        		strRate = hmPTSlabDetails.get("DEDUCTION_PAYCYCLE");
	        	
	        	//new row
				row1 =new PdfPCell(new Paragraph(strMsg,small));
		        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        row1.setColspan(2);
		        table.addCell(row1);
		        
		        row1 =new PdfPCell(new Paragraph(uF.showData(hmSalPT.get("EMP_COUNT"), "0"),small));
		        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        table.addCell(row1);
		        
		        row1 =new PdfPCell(new Paragraph(strRate,small));
		        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        table.addCell(row1);
		        
		        row1 =new PdfPCell(new Paragraph("",small));
		        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        table.addCell(row1);
			        
		        row1 =new PdfPCell(new Paragraph(strTotalAmt,small));
		        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        table.addCell(row1);
		        
		        i++;
	        	
	        }
	        
	        //new row
			row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
			row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("TOTAL Rs.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.BOTTOM); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+dblTaxAmt,small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.TOP | Rectangle.BOTTOM); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	      //new row
			row1 =new PdfPCell(new Paragraph("Add simple interrest payable (if any) on the above amount at two" +
					"percent per month or part thereof (vide section II [2] of the Act)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Rs.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("0.00",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        
	      //new row
			row1 =new PdfPCell(new Paragraph("GRAND TOTAL",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Rs.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.TOP | Rectangle.BOTTOM); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+dblTaxAmt,small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.TOP | Rectangle.BOTTOM); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	      //new row
			row1 =new PdfPCell(new Paragraph("Amount paid under Cheque No. "+uF.showData(strChequeNo, ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Dated "+ uF.showData(strChequeDate, ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	      //new row
			row1 =new PdfPCell(new Paragraph("Name of the Bank:",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //new row
	        StringBuilder sbCertify = new StringBuilder();
	        sbCertify.append("     I certify that all employees who are liable to pay the tax in my employment during the period of return have been covered by the foregoing particulars.");
	        sbCertify.append("I also certify that the necessary revision in the amount of tax deductable from the salary or wages.");
			row1 =new PdfPCell(new Paragraph(sbCertify.toString(),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1); 
	        
	        //new row
			row1 =new PdfPCell(new Paragraph("I, "+uF.showData(hmEmpProfile.get("NAME"), "")+" solemnly declare that the above statement are true to the best of my knowledge and belief.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //new row
			row1 =new PdfPCell(new Paragraph("Place: "+uF.showData(hmWorkLocationDetails.get("WL_NAME"), ""),small)); 
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Signature of the employer:_______________",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	      //new row
			row1 =new PdfPCell(new Paragraph(uF.showData(hmEmpProfile.get("NAME"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);   
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //new row
			row1 =new PdfPCell(new Paragraph("Date: "+uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Status:",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmEmpProfile.get("DESIGNATION_NAME"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	      //new row
			row1 =new PdfPCell(new Paragraph("(FOR OFFICIAL USE)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //new row
			row1 =new PdfPCell(new Paragraph("The return is accepted on verification",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //new row
			row1 =new PdfPCell(new Paragraph("Tax assessed",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Rs.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	      //new row
			row1 =new PdfPCell(new Paragraph("Tax assessed",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Rs.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        document.add(table); 
	       
	        document.close();
	        
			String filename="PTChallan_"+uF.getDateFormat(getChallanDate(),DBDATE, "MM")+"_"+uF.getDateFormat(getChallanDate(),DBDATE, "yyyy")+".pdf";
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename="+filename+"");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}*/
	
	private void viewForm5PTChallanPdfForKaranataka() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		int year=0;
		String payMonts="";
		String emp="";
		int totalamount=0;
		Map<Integer,Map<String,String>> hmMap = new HashMap<Integer,Map<String,String>>();
		int payYear=0;
		String periodFrom="";
		String periodTo="";
		
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
		Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, sessionEmp_id);
		
		pst = con.prepareStatement("select income_from,income_to,deduction_paycycle from " +
				"deduction_details_india where financial_year_from=? and financial_year_to=? order by deduction_paycycle");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		rs = pst.executeQuery();
		int i=0;
		List<Integer> alList=new ArrayList<Integer>();
		while(rs.next())
		{
			alList.add(i);
			Map<String,String> hmInner = new HashMap<String,String>();
			 hmInner.put("AMOUNT_RANGE", uF.formatIntoComma(rs.getDouble("income_from"))+" to "+uF.formatIntoComma(rs.getDouble("income_to")));
			 hmInner.put("TAXDEDUCTION", rs.getString("deduction_paycycle"));
			 hmMap.put(i, hmInner);
			i++;
		}
        rs.close();
        pst.close();
        
		Map<String,String> hmPTOtherCharge = new HashMap<String, String>();
		if(getOperation()!=null && getOperation().equalsIgnoreCase("pdf") && getChallanNum()!=null){
			
//			pst = con.prepareStatement("select paid_from,paid_to from payroll_generation " +
//					" where emp_id in (select emp_id from challan_details where challan_no=? and emp_id in (select emp_id " +
//					"from employee_official_details where org_id=? and wlocation_id=?) ) ");	
//			pst.setString(1,getChallanNum());
//			pst.setInt(2, uF.parseToInt(getOrgid()));	
//			pst.setInt(3, uF.parseToInt(getLocationId()));
//			//,paid_from,paid_to
//			System.out.println("pst===="+pst);
//			rs = pst.executeQuery();
//			while(rs.next()){
//				paid_from=rs.getString("paid_from");
//				paid_to=rs.getString("paid_to");
//			}
			
			pst = con.prepareStatement("select * from challan_details where challan_no = ? and challan_type=? and is_paid=? and emp_id in (select emp_id " +
				"from employee_official_details where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
				"state s where wi.wlocation_state_id = s.state_id and s.state_id=?))");
		
			pst.setString(1,getChallanNum());
			pst.setInt(2,PROFESSIONAL_TAX);
			pst.setBoolean(3,true);
			pst.setInt(4, uF.parseToInt(getOrgid()));
			pst.setInt(5, uF.parseToInt(getState()));
			rs= pst.executeQuery();
			StringBuilder sbMonths = new StringBuilder();
			String strChequeNo = "";
			String strChequeDate = "";
			while(rs.next()){
				
				String[] arr = rs.getString("month").split(",");
				
				for(i=0; arr!=null && i<arr.length; i++){
					if(arr[i]!=null && arr[i].length()>0){
						sbMonths.append(arr[i]+",");
					}					
				}
				hmPTOtherCharge.put("AMT_TAX", uF.showData(rs.getString("amt_tax"), "0"));
				hmPTOtherCharge.put("INTEREST_AMT", uF.showData(rs.getString("interest_amt"), "0"));
				hmPTOtherCharge.put("PENALTY_AMT", uF.showData(rs.getString("penalty_amt"), "0"));
				hmPTOtherCharge.put("COMPOSITION_MONEY", uF.showData(rs.getString("composition_money"), "0"));
				hmPTOtherCharge.put("FINE_AMT", uF.showData(rs.getString("fine_amt"), "0"));
				hmPTOtherCharge.put("FEES_AMT", uF.showData(rs.getString("fees_amt"), "0"));
				hmPTOtherCharge.put("ADVANCE_AMT", uF.showData(rs.getString("advance_amt"), "0"));
				
				strChequeNo = rs.getString("cheque_no");
				strChequeDate = uF.getDateFormat(rs.getString("paid_date"), DBDATE, DATE_FORMAT);
			}
	        rs.close();
	        pst.close();
			
			request.setAttribute("strChequeNo", strChequeNo);
			request.setAttribute("strChequeDate", strChequeDate);
	
			sbMonths.replace(0, sbMonths.length(), sbMonths.substring(0, sbMonths.length()-1));
		
			pst = con.prepareStatement("select min(paid_from) as paid_from,max(paid_to)as paid_to from payroll_generation " +
				" where month in("+sbMonths.toString()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? " +
						"and emp_id in (select emp_id from challan_details where challan_no = ? and challan_type=? and is_paid=?" +
						" and emp_id in (select emp_id from employee_official_details where org_id=? " +
						"and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
						"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) " +
						"and financial_year_from_date=? and financial_year_to_date=?)" +
						" group by paid_from,paid_to order by paid_from,paid_to");	
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,PROFESSIONAL_TAX);
			pst.setString(4,getChallanNum());
			pst.setInt(5,PROFESSIONAL_TAX);
			pst.setBoolean(6,true);
			pst.setInt(7, uF.parseToInt(getOrgid()));
			pst.setInt(8, uF.parseToInt(getState()));
			pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst===="+pst);
			rs = pst.executeQuery();
			while(rs.next())
			{
				paid_from=rs.getString("paid_from");
				paid_to=rs.getString("paid_to");
			}
	        rs.close();
	        pst.close();
			
			
			pst = con.prepareStatement("select count(emp_id) as count1,amount from challan_details " +
					"where challan_no=? and is_paid=? and emp_id in (select emp_id from employee_official_details " +
					"where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) group by amount");
			pst.setString(1,getChallanNum());
			pst.setBoolean(2,uF.parseToBoolean(getIspaid()));
			pst.setInt(3, uF.parseToInt(getOrgid()));		
			pst.setInt(4, uF.parseToInt(getState()));
			 
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("pdf")){
			
			pst = con.prepareStatement("select * from challan_details where entry_date = ? " +
					"and challan_type=? and is_paid=? and emp_id in (select emp_id from employee_official_details " +
					"where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?))");
			
			
			pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setInt(2,PROFESSIONAL_TAX);
			pst.setBoolean(3,false);
			pst.setInt(4, uF.parseToInt(getOrgid()));
			pst.setInt(5, uF.parseToInt(getState()));
			rs= pst.executeQuery();
			StringBuilder sbMonths = new StringBuilder();
			while(rs.next()){
				
				String[] arr = rs.getString("month").split(",");
				for(i=0; arr!=null && i<arr.length; i++){
					if(arr[i]!=null && arr[i].length()>0){
						sbMonths.append(arr[i]+",");
					}					
				}
				
				hmPTOtherCharge.put("AMT_TAX", uF.showData(rs.getString("amt_tax"), "0"));
				hmPTOtherCharge.put("INTEREST_AMT", uF.showData(rs.getString("interest_amt"), "0"));
				hmPTOtherCharge.put("PENALTY_AMT", uF.showData(rs.getString("penalty_amt"), "0"));
				hmPTOtherCharge.put("COMPOSITION_MONEY", uF.showData(rs.getString("composition_money"), "0"));
				hmPTOtherCharge.put("FINE_AMT", uF.showData(rs.getString("fine_amt"), "0"));
				hmPTOtherCharge.put("FEES_AMT", uF.showData(rs.getString("fees_amt"), "0"));
				hmPTOtherCharge.put("ADVANCE_AMT", uF.showData(rs.getString("advance_amt"), "0")); 
				
			}
	        rs.close();
	        pst.close();
			
			sbMonths.replace(0, sbMonths.length(), sbMonths.substring(0, sbMonths.length()-1));
			
				pst = con.prepareStatement("select count(emp_id)as count1,min(paid_from) as paid_from,max(paid_to)as paid_to from payroll_generation " +
					" where month in("+sbMonths.toString()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? " +
					"and emp_id in (select emp_id from challan_details where entry_date = ? and challan_type=? and is_paid=?" +
					" and emp_id in (select emp_id from employee_official_details where org_id=? " +
					"and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) " +
					"and financial_year_from_date=? and financial_year_to_date=?)" +
					" group by paid_from,paid_to order by paid_from,paid_to");	
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3,PROFESSIONAL_TAX);
				pst.setDate(4, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setInt(5,PROFESSIONAL_TAX);
				pst.setBoolean(6,false);
				pst.setInt(7, uF.parseToInt(getOrgid()));
				pst.setInt(8, uF.parseToInt(getState()));
				pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst===="+pst);
				rs = pst.executeQuery();
				while(rs.next())
				{
					paid_from=rs.getString("paid_from");
					paid_to=rs.getString("paid_to");
				}
		        rs.close();
		        pst.close();
			
			pst = con.prepareStatement("select count(emp_id)as count1,amount from payroll_generation where" +
					" month in ("+sbMonths.toString()+") and financial_year_from_date=? and" +
					" financial_year_to_date=? and salary_head_id=? and emp_id in " +
					"(select emp_id from challan_details where entry_date = ? and challan_type=? and is_paid=? " +
					" and emp_id in (select emp_id from employee_official_details where org_id=? " +
					"and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) " +
					"and financial_year_from_date=? and financial_year_to_date=?) group by amount order by amount");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,PROFESSIONAL_TAX);
			pst.setDate(4, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setInt(5,PROFESSIONAL_TAX);
			pst.setBoolean(6,false);
			pst.setInt(7, uF.parseToInt(getOrgid()));
			pst.setInt(8, uF.parseToInt(getState()));
			pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					
		}else{	
			for(int j=0;j<getEmpIds().length;j++){
				emp+=getEmpIds()[j]+",";
			}
			if(emp.contains(",")){ 
				emp=emp.substring(0, emp.length()-1);
			}
			
			
			pst = con.prepareStatement("select count(emp_id)as count1,min(paid_from) as paid_from," +
					"max(paid_to)as paid_to from payroll_generation where month in ("+totalMonths+") " +
					"and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? " +
					"and emp_id in (select emp_id from employee_official_details where org_id=? " +
					"and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) group by paid_from,paid_to order by paid_from,paid_to");	
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,PROFESSIONAL_TAX);
			pst.setInt(4, uF.parseToInt(getOrgid()));
			pst.setInt(8, uF.parseToInt(getState()));
				//,paid_from,paid_to
//				System.out.println("pst===="+pst);
				
			rs = pst.executeQuery();
			while(rs.next())
			{
				paid_from=rs.getString("paid_from");
				paid_to=rs.getString("paid_to");
			}
	        rs.close();
	        pst.close();
			
			pst = con.prepareStatement("select count(emp_id)as count1,amount from payroll_generation " +
					"where month in ("+totalMonths+") and financial_year_from_date=? and financial_year_to_date=? " +
					"and salary_head_id=? and emp_id in ("+emp+") and emp_id in (select emp_id from " +
					"employee_official_details where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) group by amount order by amount");	
			
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,PROFESSIONAL_TAX);
			pst.setInt(4, uF.parseToInt(getOrgid()));
			pst.setInt(5, uF.parseToInt(getState()));
		}
		
		//,paid_from,paid_to
//		System.out.println("pst===="+pst);
		
			rs = pst.executeQuery();
			Map<String,String> hmempcnt = new HashMap<String,String>();
			while(rs.next())
			{
				int empcnt=rs.getInt("count1");
				int amnt=rs.getInt("amount");
				int total=empcnt*amnt;
				
				hmempcnt.put(amnt+"TOTAL", total+"");
				hmempcnt.put(amnt+"", empcnt+"");		
				totalTaxAmount+=total;
			}
	        rs.close();
	        pst.close();
		
			if (getChallanNum() != null) {
				pst = con.prepareStatement("select entry_date from challan_details where challan_no=?");
				pst.setString(1, getChallanNum());
				rs = pst.executeQuery();
				while (rs.next()) {
					setChallanDate(rs.getString("entry_date"));
				}
		        rs.close();
		        pst.close();
				
				
				pst = con.prepareStatement("select * from deduction_details_india where state_id=? and financial_year_from=? and financial_year_to=?");
				pst.setInt(1, uF.parseToInt(getState()));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst=====>"+pst);
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
				request.setAttribute("hmPTSlab", hmPTSlab);
				if(strPtAmt!=null && !strPtAmt.equals("")){
					List<String> alMonthList = new ArrayList<String>();
					
					pst = con.prepareStatement("select month from challan_details where challan_no=? group by month");
					pst.setString(1, getChallanNum());
//					System.out.println("pst=====>"+pst);
					rs = pst.executeQuery();
					while(rs.next()){
						//strMonth = rs.getString("month");
						String[] strTemp = rs.getString("month").split(",");
						for (int j = 0 ; j < strTemp.length; j++){
							if(!strTemp[j].trim().equals("")){
								if(!alMonthList.contains(strTemp[j].trim())){
									alMonthList.add(strTemp[j].trim());
								}
							}
						}
					}
			        rs.close();
			        pst.close();
			        StringBuilder sbMonths = null;
//					System.out.println("alMonthList=======>"+alMonthList.toString());
					String strMonthDetails = "";
					String startYear = uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yy");
					String endYear = uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yy");
					for (int j=0; j < alMonthList.size(); j++){
						if (sbMonths == null){
							sbMonths = new StringBuilder();
							sbMonths.append(alMonthList.get(j));
							
							if(uF.parseToInt(alMonthList.get(j)) == 1 || uF.parseToInt(alMonthList.get(j)) == 2 || uF.parseToInt(alMonthList.get(j)) == 3 ){
								strMonthDetails = uF.getShortMonth(uF.parseToInt(alMonthList.get(j)))+ endYear;
							} else {
								strMonthDetails = uF.getShortMonth(uF.parseToInt(alMonthList.get(j)))+ startYear;
							}
							
						} else {
							sbMonths.append(","+alMonthList.get(j));
							
							if(uF.parseToInt(alMonthList.get(j)) == 1 || uF.parseToInt(alMonthList.get(j)) == 2 || uF.parseToInt(alMonthList.get(j)) == 3 ){
								strMonthDetails +=","+ uF.getShortMonth(uF.parseToInt(alMonthList.get(j)))+ endYear;
							} else {
								strMonthDetails +=","+ uF.getShortMonth(uF.parseToInt(alMonthList.get(j)))+ startYear;
							}
						}
					} 
					request.setAttribute("strMonthDetails", strMonthDetails);
					
					Map<String, Map<String, String>> hmPTDetails = new LinkedHashMap<String, Map<String, String>>();
					
					if (sbMonths != null){
					
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("select count(eod.emp_id) as empcnt, amount,sum(amount) as totalamt from payroll_generation pg," +
								"employee_official_details eod where month in ("+sbMonths.toString()+") and financial_year_from_date=? " +
								"and financial_year_to_date=? and salary_head_id =? and is_paid=true and pg.emp_id=eod.emp_id and org_id=? " +
								"and amount in ("+strPtAmt+") and pg.emp_id in (select emp_id from challan_details where financial_year_from_date = ?" +
								" and financial_year_to_date = ? and challan_type=?  and challan_no=? and emp_id in (select emp_id from " +
								"employee_official_details where org_id=? and wlocation_id in (select wlocation_id from work_location_info where " +
								"wlocation_state_id=?))) and eod.wlocation_id in (select wlocation_id from work_location_info where wlocation_state_id=?) group by amount");
						
						pst = con.prepareStatement(sbQuery.toString());
						pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setInt(3, PROFESSIONAL_TAX);
						pst.setInt(4, uF.parseToInt(getOrgid()));
						pst.setDate(5,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(6,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setInt(7, PROFESSIONAL_TAX);
						pst.setString(8, getChallanNum());
						pst.setInt(9, uF.parseToInt(getOrgid()));
						pst.setInt(10, uF.parseToInt(getState()));
						pst.setInt(11, uF.parseToInt(getState()));
//						System.out.println("pst=====>"+pst);
						
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
//						System.out.println("hmPTDetails=====>"+hmPTDetails);
					}
				}
			}
		request.setAttribute("hmempcnt",hmempcnt);
		request.setAttribute("alList",alList);
		request.setAttribute("hmMap",hmMap);
		request.setAttribute("hmPTOtherCharge",hmPTOtherCharge);
		request.setAttribute("hmEmpProfile", hmEmpProfile);
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	
	private void updateOtherCharges() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE challan_details SET amt_tax=?,interest_amt=?,penalty_amt=?,composition_money=?,fine_amt=?,fees_amt=?,advance_amt=? " +
					" WHERE entry_date =? and challan_type=? and is_paid = false " +
					"and emp_id in (select emp_id from employee_official_details where org_id=? " +
					"and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?))");
			pst.setDouble(1,uF.parseToDouble(getAmtTax()));
			pst.setDouble(2,uF.parseToDouble(getInterestAmt()));
			pst.setDouble(3,uF.parseToDouble(getPenaltyAmt()));
			pst.setDouble(4,uF.parseToDouble(getCompositionMoney()));
			pst.setDouble(5,uF.parseToDouble(getFineAmt()));
			pst.setDouble(6,uF.parseToDouble(getFeesAmt()));
			pst.setDouble(7,uF.parseToDouble(getAdvanceAmt()));
			pst.setDate(8, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setInt(9,PROFESSIONAL_TAX);
			pst.setInt(10, uF.parseToInt(getOrgid()));
			pst.setInt(11, uF.parseToInt(getState()));
//			System.out.println("pst=======>"+pst);
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
	private void getOrganizationDetails() {
		UtilityFunctions uF = new UtilityFunctions();

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db=new Database();
		db.setRequest(request);
		
		Map<String, Map<String, String>> hmOrg = new HashMap<String, Map<String,String>>();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from org_details where org_id=?");
			pst.setInt(1, uF.parseToInt(getOrgid()));
			rs = pst.executeQuery();

			while (rs.next()) {
				Map<String, String> hmOrgDetails=new HashMap<String, String>();
				hmOrgDetails.put("ORG_ID",rs.getString("org_id"));
				hmOrgDetails.put("ORG_NAME",rs.getString("org_name"));
				hmOrgDetails.put("ORG_LOGO",rs.getString("org_logo"));
				hmOrgDetails.put("ORG_ADDRESS",rs.getString("org_address"));
				hmOrgDetails.put("ORG_PINCODE",rs.getString("org_pincode"));
				hmOrgDetails.put("ORG_CONTACT",rs.getString("org_contact1"));
				hmOrgDetails.put("ORG_EMAIL",rs.getString("org_email"));
				hmOrgDetails.put("ORG_STATE_ID",rs.getString("org_state_id"));
				hmOrgDetails.put("ORG_COUNTRY_ID",rs.getString("org_country_id"));
				hmOrgDetails.put("ORG_CITY",rs.getString("org_city"));
				hmOrgDetails.put("ORG_CODE",rs.getString("org_code"));
				
				hmOrg.put(rs.getString("org_id"), hmOrgDetails); 
			}
	        rs.close();
	        pst.close();
			
//			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con,uF.parseToInt(getOrgid()));
			
			pst = con.prepareStatement("SELECT wi.* FROM work_location_info wi,state s " +
					"where wi.wlocation_state_id = s.state_id and s.state_id=? and wi.org_id=?");
			pst.setInt(1, uF.parseToInt(getState()));
			pst.setInt(2, uF.parseToInt(getOrgid()));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			boolean isWlocation = false;
			int i=0;
			String strPTRCEC="";
			StringBuilder sbWLocationName = null; 
			while(rs.next()){
				isWlocation = true;
				if(i == 0 && rs.getString("wlocation_pt_rcec") != null && !rs.getString("wlocation_pt_rcec").trim().equals("") 
						&& !rs.getString("wlocation_pt_rcec").trim().equalsIgnoreCase("NULL")){
					strPTRCEC=uF.showData(rs.getString("wlocation_pt_rcec"), "");
					i++;
				}
				
				if(sbWLocationName == null){
					sbWLocationName = new StringBuilder();
					sbWLocationName.append(uF.showData(rs.getString("wlocation_name"), ""));
				} else {
					sbWLocationName.append(","+uF.showData(rs.getString("wlocation_name"), ""));
				}
				
				
			}
	        rs.close();
	        pst.close();
	        
	        Map<String,String> hmWorkLocationDetails = new HashMap<String, String>();
	        if(isWlocation){
	        	if(sbWLocationName == null)sbWLocationName = new StringBuilder();
	        	
//	        	hmWorkLocationDetails.put("WL_NAME", sbWLocationName.toString());
	        	hmWorkLocationDetails.put("WL_PT_RC_EC", strPTRCEC);
	        }
			
			request.setAttribute("hmOrg",hmOrg);
			request.setAttribute("hmWorkLocationDetails",hmWorkLocationDetails);/////***///

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	private void generateNewForm5PTChallanPdfReports() {
		try {
			
			UtilityFunctions uF = new UtilityFunctions();
			List<Integer> alList=(List<Integer>)request.getAttribute("alList");
			Map<String,String> hmempcnt =(Map<String,String> )request.getAttribute("hmempcnt");
			Map<Integer,Map<String,String>> hmMap=(Map<Integer,Map<String,String>>)request.getAttribute("hmMap");
			if(hmMap==null)hmMap=new HashMap<Integer, Map<String,String>>();
			
			Map<String, Map<String, String>> hmOrg =(Map<String, Map<String, String>>)request.getAttribute("hmOrg");
			if(hmOrg==null)hmOrg=new HashMap<String, Map<String,String>>();
			Map<String,String> hmOrgDetails =hmOrg.get(getOrgid());
			
			Map<String,String> hmWorkLocationDetails = (Map<String,String>) request.getAttribute("hmWorkLocationDetails");
			if(hmWorkLocationDetails==null)hmWorkLocationDetails=new HashMap<String,String>();
			
			Map<String,String> hmPTOtherCharge = (Map<String,String>)request.getAttribute("hmPTOtherCharge");
			if(hmPTOtherCharge == null) hmPTOtherCharge = new HashMap<String,String>();
			
//			System.out.println("hmWorkLocationDetails====>"+hmWorkLocationDetails);
//			System.out.println("WL_PT_RC_EC====>"+uF.showData(hmWorkLocationDetails.get("WL_PT_RC_EC"), ""));
			
			Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
			Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
			Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			Font small = new Font(Font.FontFamily.HELVETICA,7);
			Font smallBold = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD);
			Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        Document document = new Document(PageSize.A4);
	        PdfWriter.getInstance(document,buffer);
	        document.open();
	        
	        PdfPTable table = new PdfPTable(5);
			table.setWidthPercentage(100);        
	        
			// For Tressary
			PdfPCell row1 =new PdfPCell(new Paragraph("",smallBold));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT | Rectangle.TOP);
		    row1.setPadding(2.5f);
		    table.addCell(row1);
			
	        String heading1="CHALLAN\nMTR FORM NO-6\n(See Rule 11,11A,11B & 11C of PT Rules,1975)\nACCOUNT HEAD :- 00280012";        
	        row1 =new PdfPCell(new Paragraph(heading1,smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER); 
	        row1.setBorder(Rectangle.TOP);        
	        row1.setColspan(3);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("For Tressary",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.RIGHT | Rectangle.TOP); 
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("GRN",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Form ID",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("VIII",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Department",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("Department of sales tax",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(2);
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("Date",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph(uF.getDateFormat(getChallanDate(), DBDATE, DATE_FORMAT),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Type Of Payment",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("Professional Tax act,1975",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(2);
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("Payee Details",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(2);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Location",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmWorkLocationDetails.get("WL_NAME"), ""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(2);
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("Dept ID (PTRC No.)",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmWorkLocationDetails.get("WL_PT_RC_EC"), ""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Period",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(2); 
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("Full Name of the Dealer",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setRowspan(3); 
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph(hmOrgDetails.get("ORG_NAME")+"\n\n"+hmOrgDetails.get("ORG_ADDRESS"),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(2);
	        row1.setRowspan(3); 
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("From",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("To",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.getDateFormat(paid_from, DBDATE, DATE_FORMAT),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph(uF.getDateFormat(paid_to, DBDATE, DATE_FORMAT),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Account Head Details",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("Code",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("Amount in Rs.",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("Remark if any\n",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT); 
	        row1.setColspan(2);
	        row1.setRowspan(10); 
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Amount of Tax",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("1",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph(""+totalTaxAmount,small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Amount of TDS",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("2",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Interest Amt",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("3",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("INTEREST_AMT"), "0"),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table.addCell(row1);
	        
	        
	        row1 =new PdfPCell(new Paragraph("Penalty Amt",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("4",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("PENALTY_AMT"), "0"),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Composition Money",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("5",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("COMPOSITION_MONEY"), "0"),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table.addCell(row1);

	        row1 =new PdfPCell(new Paragraph("Fine",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("6",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("FINE_AMT"), "0"),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Fees",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("7",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("FEES_AMT"), "0"),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Advance Payment",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("8",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("ADVANCE_AMT"), "0"),small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Amount Forfeited",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("9",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Deposit",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("10",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table.addCell(row1);
	        
	        double dblTotal = totalTaxAmount + uF.parseToDouble(hmPTOtherCharge.get("INTEREST_AMT")) + uF.parseToDouble(hmPTOtherCharge.get("PENALTY_AMT")) 
	        + uF.parseToDouble(hmPTOtherCharge.get("COMPOSITION_MONEY")) + uF.parseToDouble(hmPTOtherCharge.get("FINE_AMT")) 
	        + uF.parseToDouble(hmPTOtherCharge.get("FEES_AMT")) + uF.parseToDouble(hmPTOtherCharge.get("ADVANCE_AMT")); 
	        
	        String digitTotal="";
	        String strTotalAmt=""+dblTotal;
	        if(strTotalAmt.contains(".")){
	        	strTotalAmt=strTotalAmt.replace(".", ",");
	        	String[] temp=strTotalAmt.split(",");
	        	digitTotal=uF.digitsToWords(uF.parseToInt(temp[0]));
	        	if(uF.parseToInt(temp[1])>0){
	        		int pamt=0;
	        		if(temp[1].length()==1){
	        			pamt=uF.parseToInt(temp[1]+"0");
	        		}else{
	        			pamt=uF.parseToInt(temp[1]);
	        		}
	        		digitTotal+=" and "+uF.digitsToWords(pamt)+" paise";
	        	}
	        }else{
	        	int totalAmt1=(int)dblTotal;
	        	digitTotal=uF.digitsToWords(totalAmt1);
	        }
	        
	        row1 =new PdfPCell(new Paragraph("Amount in Words :-\nRs. "+uF.showData(digitTotal,"0"),small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);  
	        row1.setColspan(2);
	        row1.setRowspan(2); 
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Total",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph(""+dblTotal,small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Payment Details",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(3);
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("For Use in Receiving Bank ",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Name of Bank",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(2);
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("Bank CIN No.",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Name of Branch",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(2);
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("Date/Time",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Mode of Pay",small));
	        row1.setPadding(2.5f);
	        row1.setNoWrap(true);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(2);
	        table.addCell(row1);
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        
	        row1 =new PdfPCell(new Paragraph("\n\nSignature of person who has made payment",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_BOTTOM);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setColspan(5);
	        row1.setNoWrap(true);
	        table.addCell(row1);
	        
	        document.add(table); 
	        document.newPage();
	        
	        
	        // For the Profession Tax Officer
	        PdfPTable table2 = new PdfPTable(5);
			table2.setWidthPercentage(100);        
	        
			
			PdfPCell table2Row =new PdfPCell(new Paragraph("",smallBold));
		    table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setBorder(Rectangle.LEFT | Rectangle.TOP);
		    table2Row.setPadding(2.5f);
		    table2.addCell(table2Row);
			
	        table2Row =new PdfPCell(new Paragraph(heading1,smallBold));
	        table2Row.setHorizontalAlignment(Element.ALIGN_CENTER); 
	        table2Row.setBorder(Rectangle.TOP);        
	        table2Row.setColspan(3);
	        table2Row.setPadding(2.5f);
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("For the Profession Tax Officer",small));
	        table2Row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        table2Row.setBorder(Rectangle.RIGHT | Rectangle.TOP); 
	        table2Row.setPadding(2.5f);
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("GRN",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);        
	        table2Row =new PdfPCell(new Paragraph("",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setColspan(2);
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Form ID",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("VIII",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Department",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("Department of sales tax",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setColspan(2);
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("Date",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph(uF.getDateFormat(getChallanDate(), DBDATE, DATE_FORMAT),small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Type Of Payment",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("Professional Tax act,1975",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setColspan(2);
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("Payee Details",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setColspan(2);        
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Location",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph(uF.showData(hmWorkLocationDetails.get("WL_NAME"), ""),small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setColspan(2);
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("Dept ID (PTRC No.)",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph(uF.showData(hmWorkLocationDetails.get("WL_PT_RC_EC"), ""),small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Period",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setColspan(2); 
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("Full Name of the Dealer",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setRowspan(3); 
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph(hmOrgDetails.get("ORG_NAME")+"\n\n"+hmOrgDetails.get("ORG_ADDRESS"),small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setColspan(2);
	        table2Row.setRowspan(3); 
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("From",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("To",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph(uF.getDateFormat(paid_from, DBDATE, DATE_FORMAT),small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph(uF.getDateFormat(paid_to, DBDATE, DATE_FORMAT),small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Account Head Details",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("Code",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("Amount in Rs.",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("Remark if any\n",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT); 
	        table2Row.setColspan(2);
	        table2Row.setRowspan(10); 
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Amount of Tax",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("1",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph(""+totalTaxAmount,small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Amount of TDS",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("2",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Interest Amt",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("3",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("INTEREST_AMT"), "0"),small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table2.addCell(table2Row);
	        
	        
	        table2Row =new PdfPCell(new Paragraph("Penalty Amt",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("4",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("PENALTY_AMT"), "0"),small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Composition Money",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("5",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("COMPOSITION_MONEY"), "0"),small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table2.addCell(table2Row);

	        table2Row =new PdfPCell(new Paragraph("Fine",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("6",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("FINE_AMT"), "0"),small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Fees",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("7",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("FEES_AMT"), "0"),small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Advance Payment",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("8",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("ADVANCE_AMT"), "0"),small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Amount Forfeited",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("9",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Deposit",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("10",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("Amount in Words :-\nRs. "+uF.showData(digitTotal,"0"),small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);  
	        table2Row.setColspan(2);
	        table2Row.setRowspan(2); 
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Total",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph(""+dblTotal,small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        table2Row.setColspan(2);
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Payment Details",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setColspan(3);
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("For Use in Receiving Bank ",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setColspan(2);
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Name of Bank",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setColspan(2);
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("Bank CIN No.",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Name of Branch",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setColspan(2);
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("Date/Time",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2.addCell(table2Row);
	        
	        table2Row =new PdfPCell(new Paragraph("Mode of Pay",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setNoWrap(true);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setPadding(2.5f);
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setColspan(2);
	        table2.addCell(table2Row);
	        table2Row =new PdfPCell(new Paragraph("",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table2Row.setColspan(2);
	        table2.addCell(table2Row);
	        
	        
	        table2Row =new PdfPCell(new Paragraph("\n\nSignature of person who has made payment",small));
	        table2Row.setPadding(2.5f);
	        table2Row.setHorizontalAlignment(Element.ALIGN_BOTTOM);
	        table2Row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        table2Row.setColspan(5);
	        table2Row.setNoWrap(true);
	        table2.addCell(table2Row);
	        document.add(table2);
	        
	        document.newPage();
	        
	        
	     // For the Profession Tax Payer
	        PdfPTable table3 = new PdfPTable(5);
			table3.setWidthPercentage(100);        
	        
			
			PdfPCell table3Row =new PdfPCell(new Paragraph("",smallBold));
		    table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setBorder(Rectangle.LEFT | Rectangle.TOP);
		    table3Row.setPadding(2.5f);
		    table3.addCell(table3Row);
			
	        table3Row =new PdfPCell(new Paragraph(heading1,smallBold));
	        table3Row.setHorizontalAlignment(Element.ALIGN_CENTER); 
	        table3Row.setBorder(Rectangle.TOP);        
	        table3Row.setColspan(3);
	        table3Row.setPadding(2.5f);
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("For the Profession Tax Payer",small));
	        table3Row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        table3Row.setBorder(Rectangle.RIGHT | Rectangle.TOP); 
	        table3Row.setPadding(2.5f);
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("GRN",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);        
	        table3Row =new PdfPCell(new Paragraph("",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setColspan(2);
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Form ID",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("VIII",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Department",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("Department of sales tax",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setColspan(2);
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("Date",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph(uF.getDateFormat(getChallanDate(), DBDATE, DATE_FORMAT) ,small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Type Of Payment",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("Professional Tax act,1975",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setColspan(2);
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("Payee Details",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setColspan(2);        
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Location",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph(uF.showData(hmWorkLocationDetails.get("WL_NAME"), ""),small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setColspan(2);
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("Dept ID (PTRC No.)",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph(uF.showData(hmWorkLocationDetails.get("WL_PT_RC_EC"), ""),small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Period",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setColspan(2); 
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("Full Name of the Dealer",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setRowspan(3); 
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph(hmOrgDetails.get("ORG_NAME")+"\n\n"+hmOrgDetails.get("ORG_ADDRESS"),small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setColspan(2);
	        table3Row.setRowspan(3); 
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("From",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("To",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph(uF.getDateFormat(paid_from, DBDATE, DATE_FORMAT)  ,small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph(""+uF.getDateFormat(paid_to, DBDATE, DATE_FORMAT),small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Account Head Details",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("Code",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("Amount in Rs.",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("Remark if any\n",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT); 
	        table3Row.setColspan(2);
	        table3Row.setRowspan(10); 
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Amount of Tax",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("1",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph(""+totalTaxAmount,small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Amount of TDS",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("2",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Interest Amt",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("3",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("INTEREST_AMT"), "0"),small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table3.addCell(table3Row);
	        
	        
	        table3Row =new PdfPCell(new Paragraph("Penalty Amt",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("4",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("PENALTY_AMT"), "0"),small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Composition Money",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("5",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("COMPOSITION_MONEY"), "0"),small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table3.addCell(table3Row);

	        table3Row =new PdfPCell(new Paragraph("Fine",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("6",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("FINE_AMT"), "0"),small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Fees",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("7",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("FEES_AMT"), "0"),small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Advance Payment",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("8",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph(uF.showData(hmPTOtherCharge.get("ADVANCE_AMT"), "0"),small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Amount Forfeited",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("9",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Deposit",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("10",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_CENTER);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_RIGHT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("Amount in Words :-\nRs. "+uF.showData(digitTotal,"0"),small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);  
	        table3Row.setColspan(2);
	        table3Row.setRowspan(2); 
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Total",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);        
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph(""+dblTotal,small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        table3Row.setColspan(2);
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Payment Details",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setColspan(3);
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("For Use in Receiving Bank ",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setColspan(2);
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Name of Bank",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setColspan(2);
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("Bank CIN No.",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Name of Branch",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setColspan(2);
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("Date/Time",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3.addCell(table3Row);
	        
	        table3Row =new PdfPCell(new Paragraph("Mode of Pay",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setNoWrap(true);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setPadding(2.5f);
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setColspan(2);
	        table3.addCell(table3Row);
	        table3Row =new PdfPCell(new Paragraph("",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        table3Row.setColspan(2);
	        table3.addCell(table3Row);
	        
	        
	        table3Row =new PdfPCell(new Paragraph("\n\nSignature of person who has made payment",small));
	        table3Row.setPadding(2.5f);
	        table3Row.setHorizontalAlignment(Element.ALIGN_BOTTOM);
	        table3Row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        table3Row.setColspan(5);
	        table3Row.setNoWrap(true);
	        table3.addCell(table3Row);
	        document.add(table3);
	        
	        document.close();
	        
			String filename="PTChallan_"+uF.getDateFormat(getChallanDate(),DBDATE, "MM")+"_"+uF.getDateFormat(getChallanDate(),DBDATE, "yyyy")+".pdf";
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
	public void deleteChallanDetails()
	{
//		System.out.println("in deleteChallanDetails");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con = db.makeConnection(con);
			/*pst = con.prepareStatement("delete from challan_details WHERE entry_date =? and is_paid=? and challan_type=? " +
					"and emp_id in (select emp_id from employee_official_details where org_id=? " +
					"and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) ");
			*/
			
			StringBuilder sb=new StringBuilder();
			sb.append("delete from challan_details WHERE entry_date =? and is_paid=? and challan_type=?");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in ("+sbEmp+")");
			}
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setBoolean(2,false);
			pst.setInt(3,PROFESSIONAL_TAX);
			//pst.setInt(4, uF.parseToInt(getOrgid()));
			//pst.setInt(5, uF.parseToInt(getState()));
			
//			System.out.println("pst for deleteChallan==>"+pst);
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
	public void updateChallanNumber(){
		
//		System.out.println("in updateChallanNumber");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con = db.makeConnection(con);
			/*pst = con.prepareStatement("UPDATE challan_details SET is_paid =?,paid_date=?,challan_no=?,cheque_no=?,added_by=? " +
					" WHERE entry_date =? and challan_type=? and is_paid = false " +
					"and emp_id in (select emp_id from employee_official_details " +
					"where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?))");
			*/
			
			
			StringBuilder sb=new StringBuilder();
			sb.append("UPDATE challan_details SET is_paid =?,paid_date=?,challan_no=?,cheque_no=?,added_by=? " +
					" WHERE entry_date =? and challan_type=? and is_paid = false");
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
			pst.setInt(5,uF.parseToInt(sessionEmp_id));
			pst.setDate(6, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setInt(7,PROFESSIONAL_TAX);
		//	pst.setInt(8, uF.parseToInt(getOrgid()));
		//	pst.setInt(9, uF.parseToInt(getState()));
//			System.out.println("pst=======>"+pst);
			
//			System.out.println("pst for update chalan no==>"+pst);
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
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
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
			for(int i=0;i<getEmpIds().length;i++){
				double amount=0;
				for(int j=0;j<alMonthList.size();j++){
					pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where financial_year_from_date=?" +
							" and financial_year_to_date=? and month=? and is_paid=true and salary_head_id=? and emp_id=? ");
																	
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3,uF.parseToInt(alMonthList.get(j)));
					pst.setInt(4,PROFESSIONAL_TAX);
					pst.setInt(5,uF.parseToInt(getEmpIds()[i]));
//					System.out.println("insert pst====>"+pst);
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
					pst.setInt(4,PROFESSIONAL_TAX);
//						System.out.println("insert pst====>"+pst);
					rs = pst.executeQuery();
					boolean flag=false;
					while(rs.next()){
						flag = true;
					}
			        rs.close();
			        pst.close();

//			        System.out.println("amount====>"+amount+"---flag====>"+flag);
					if(amount > 0.0d && !flag){
						pst = con.prepareStatement("insert into challan_details(emp_id,amount,entry_date,is_print,financial_year_from_date," +
								"financial_year_to_date,month,challan_type) values(?,?,?,?,?,?,?,?)");
						pst.setInt(1,uF.parseToInt(getEmpIds()[i]));
						pst.setDouble(2,amount);
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setBoolean(4,true);
						pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setString(7,","+alMonthList.get(j)+",");
						pst.setInt(8,PROFESSIONAL_TAX);
						pst.execute();
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
	
	public void viewForm5PTChallanPdfReports(){
	//	System.out.println("in viewForm5PTChallanPdfReports");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		int year=0;
		String payMonts="";
		String emp="";
		int totalamount=0;
		Map<Integer,Map<String,String>> hmMap = new HashMap<Integer,Map<String,String>>();
		int payYear=0;
		String periodFrom="";
		String periodTo="";
		
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
		
		pst = con.prepareStatement("select income_from,income_to,deduction_paycycle from " +
				"deduction_details_india where financial_year_from=? and financial_year_to=? order by deduction_paycycle");
		
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		rs = pst.executeQuery();
		int i=0;
		List<Integer> alList=new ArrayList<Integer>();
		while(rs.next())
		{
			alList.add(i);
			Map<String,String> hmInner = new HashMap<String,String>();
			 hmInner.put("AMOUNT_RANGE", uF.formatIntoComma(rs.getDouble("income_from"))+" to "+uF.formatIntoComma(rs.getDouble("income_to")));
			 hmInner.put("TAXDEDUCTION", rs.getString("deduction_paycycle"));
			 hmMap.put(i, hmInner);
			i++;
		}
        rs.close();
        pst.close();
        
		Map<String,String> hmPTOtherCharge = new HashMap<String, String>();
		if(getOperation()!=null && getOperation().equalsIgnoreCase("pdf") && getChallanNum()!=null) {
			
//			pst = con.prepareStatement("select paid_from,paid_to from payroll_generation " +
//					" where emp_id in (select emp_id from challan_details where challan_no=? and emp_id in (select emp_id " +
//					"from employee_official_details where org_id=? and wlocation_id=?) ) ");	
//			pst.setString(1,getChallanNum());
//			pst.setInt(2, uF.parseToInt(getOrgid()));	
//			pst.setInt(3, uF.parseToInt(getLocationId()));
//			//,paid_from,paid_to
//			System.out.println("pst===="+pst);
//			rs = pst.executeQuery();
//			while(rs.next()){
//				paid_from=rs.getString("paid_from");
//				paid_to=rs.getString("paid_to");
//			}
			
			/*pst = con.prepareStatement("select * from challan_details where challan_no = ? " +
					"and challan_type=? and is_paid=? and emp_id in (select emp_id " +
					"from employee_official_details where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?))");
			*/
			
			StringBuilder sb=new StringBuilder();
			
			sb.append("select * from challan_details where challan_no = ? " +
					" and challan_type=? and is_paid=?");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			pst=con.prepareStatement(sb.toString());
			
			pst.setString(1,getChallanNum());
			pst.setInt(2,PROFESSIONAL_TAX);
			pst.setBoolean(3,true);
			//pst.setInt(4, uF.parseToInt(getOrgid()));
			//pst.setInt(5, uF.parseToInt(getState()));
	//		System.out.println(" pst 1 in viewForm5PTChallanPdfReports="+pst);
			rs= pst.executeQuery();
			StringBuilder sbMonths = new StringBuilder();
			while(rs.next()){
				
				String[] arr = rs.getString("month").split(",");
				
				for(i=0; arr!=null && i<arr.length; i++){
					if(arr[i]!=null && arr[i].length()>0){
						sbMonths.append(arr[i]+",");
					}					
				}
				hmPTOtherCharge.put("AMT_TAX", uF.showData(rs.getString("amt_tax"), "0"));
				hmPTOtherCharge.put("INTEREST_AMT", uF.showData(rs.getString("interest_amt"), "0"));
				hmPTOtherCharge.put("PENALTY_AMT", uF.showData(rs.getString("penalty_amt"), "0"));
				hmPTOtherCharge.put("COMPOSITION_MONEY", uF.showData(rs.getString("composition_money"), "0"));
				hmPTOtherCharge.put("FINE_AMT", uF.showData(rs.getString("fine_amt"), "0"));
				hmPTOtherCharge.put("FEES_AMT", uF.showData(rs.getString("fees_amt"), "0"));
				hmPTOtherCharge.put("ADVANCE_AMT", uF.showData(rs.getString("advance_amt"), "0"));
			}
	        rs.close();
	        pst.close();
	
			sbMonths.replace(0, sbMonths.length(), sbMonths.substring(0, sbMonths.length()-1));
		
			/*pst = con.prepareStatement("select min(paid_from) as paid_from,max(paid_to)as paid_to from payroll_generation " +
				" where month in("+sbMonths.toString()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? " +
						"and emp_id in (select emp_id from challan_details where challan_no = ? and challan_type=? and is_paid=?" +
						" and emp_id in (select emp_id from employee_official_details where org_id=? " +
						"and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
						"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) " +
						"and financial_year_from_date=? and financial_year_to_date=?)" +
						" group by paid_from,paid_to order by paid_from,paid_to");	
			*/
			
			sb=new StringBuilder();
			
			sb.append("select min(paid_from) as paid_from,max(paid_to)as paid_to from payroll_generation " +
				" where month in("+sbMonths.toString()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? " +
						"and emp_id in (select emp_id from challan_details where challan_no = ? and challan_type=? and is_paid=?");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			sb.append(" and financial_year_from_date=? and financial_year_to_date=?)" +
						" group by paid_from,paid_to order by paid_from,paid_to");
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,PROFESSIONAL_TAX);
			pst.setString(4,getChallanNum());
			pst.setInt(5,PROFESSIONAL_TAX);
			pst.setBoolean(6,true);
			//pst.setInt(7, uF.parseToInt(getOrgid()));
			//pst.setInt(8, uF.parseToInt(getState()));
			pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			//System.out.println("pst===="+pst);
	//		System.out.println(" pst 2 in viewForm5PTChallanPdfReports="+pst);

			rs = pst.executeQuery();
			while(rs.next())
			{
				paid_from=rs.getString("paid_from");
				paid_to=rs.getString("paid_to");
			}
	        rs.close();
	        pst.close();
			
			
			/*pst = con.prepareStatement("select count(emp_id) as count1,amount from challan_details where challan_no=? and is_paid=? and emp_id in " +
					"(select emp_id from employee_official_details where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) group by amount");
			*/
			
			sb=new StringBuilder();
			sb.append("select count(emp_id) as count1,amount from challan_details where challan_no=? and is_paid=? ");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			sb.append(" group by amount");
			pst=con.prepareStatement(sb.toString());
		
			
			pst.setString(1,getChallanNum());
			pst.setBoolean(2,uF.parseToBoolean(getIspaid()));
			//pst.setInt(3, uF.parseToInt(getOrgid()));		
			//pst.setInt(4, uF.parseToInt(getState()));
		} else if(getOperation()!=null && getOperation().equalsIgnoreCase("pdf")) {
			
			/*pst = con.prepareStatement("select * from challan_details where entry_date = ? and challan_type=? and is_paid=? and emp_id in (select emp_id " +
					"from employee_official_details where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?))");
*/			
			StringBuilder sb=new StringBuilder();
			sb.append("select * from challan_details where entry_date = ? and challan_type=? and is_paid=?");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setInt(2,PROFESSIONAL_TAX);
			pst.setBoolean(3,false);
			//pst.setInt(4, uF.parseToInt(getOrgid()));
			//pst.setInt(5, uF.parseToInt(getState()));
	//		System.out.println(" pst 3 in viewForm5PTChallanPdfReports==>"+pst);
			rs= pst.executeQuery();
			StringBuilder sbMonths = new StringBuilder();
			while(rs.next()){
				
				String[] arr = rs.getString("month").split(",");
				
				for(i=0; arr!=null && i<arr.length; i++){
					if(arr[i]!=null && arr[i].length()>0){
						sbMonths.append(arr[i]+",");
					}					
				}
				hmPTOtherCharge.put("AMT_TAX", uF.showData(rs.getString("amt_tax"), "0"));
				hmPTOtherCharge.put("INTEREST_AMT", uF.showData(rs.getString("interest_amt"), "0"));
				hmPTOtherCharge.put("PENALTY_AMT", uF.showData(rs.getString("penalty_amt"), "0"));
				hmPTOtherCharge.put("COMPOSITION_MONEY", uF.showData(rs.getString("composition_money"), "0"));
				hmPTOtherCharge.put("FINE_AMT", uF.showData(rs.getString("fine_amt"), "0"));
				hmPTOtherCharge.put("FEES_AMT", uF.showData(rs.getString("fees_amt"), "0"));
				hmPTOtherCharge.put("ADVANCE_AMT", uF.showData(rs.getString("advance_amt"), "0")); 
				
			}
	        rs.close();
	        pst.close();
			
			sbMonths.replace(0, sbMonths.length(), sbMonths.substring(0, sbMonths.length()-1));
			
				/*pst = con.prepareStatement("select count(emp_id)as count1,min(paid_from) as paid_from,max(paid_to)as paid_to from payroll_generation " +
					" where month in("+sbMonths.toString()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? " +
							"and emp_id in (select emp_id from challan_details where entry_date = ? and challan_type=? and is_paid=?" +
							" and emp_id in (select emp_id from employee_official_details " +
							"where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
							"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) " +
							"and financial_year_from_date=? and financial_year_to_date=?)" +
							" group by paid_from,paid_to order by paid_from,paid_to");	
*/			
				
				sb=new StringBuilder();
				sb.append("select count(emp_id)as count1,min(paid_from) as paid_from,max(paid_to)as paid_to from payroll_generation " +
					" where month in("+sbMonths.toString()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? " +
							"and emp_id in (select emp_id from challan_details where entry_date = ? and challan_type=? and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				sb.append(" and financial_year_from_date=? and financial_year_to_date=?)" +
							" group by paid_from,paid_to order by paid_from,paid_to");
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3,PROFESSIONAL_TAX);
				pst.setDate(4, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setInt(5,PROFESSIONAL_TAX);
				pst.setBoolean(6,false);
				//pst.setInt(7, uF.parseToInt(getOrgid()));
				//pst.setInt(8, uF.parseToInt(getState()));
				pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst===="+pst);
//				System.out.println(" pst 4 in viewForm5PTChallanPdfReports==>"+pst);

				rs = pst.executeQuery();
				while(rs.next())
				{
					paid_from=rs.getString("paid_from");
					paid_to=rs.getString("paid_to");
				}
		        rs.close();
		        pst.close();
			
			
			
			/*pst = con.prepareStatement("select count(emp_id)as count1,amount from payroll_generation where" +
					" month in ("+sbMonths.toString()+") and financial_year_from_date=? and" +
					" financial_year_to_date=? and salary_head_id=? and emp_id in " +
					"(select emp_id from challan_details where entry_date = ? and challan_type=? " +
					"and is_paid=? and emp_id in (select emp_id from employee_official_details " +
					"where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) " +
					"and financial_year_from_date=? and financial_year_to_date=?) group by amount order by amount");*/
			
			sb=new StringBuilder();
			sb.append("select count(emp_id)as count1,amount from payroll_generation where" +
					" month in ("+sbMonths.toString()+") and financial_year_from_date=? and" +
					" financial_year_to_date=? and salary_head_id=? and emp_id in " +
					"(select emp_id from challan_details where entry_date = ? and challan_type=? " +
					"and is_paid=?");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			sb.append("and financial_year_from_date=? and financial_year_to_date=?) group by amount order by amount");
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,PROFESSIONAL_TAX);
			pst.setDate(4, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setInt(5,PROFESSIONAL_TAX);
			pst.setBoolean(6,false);
			//pst.setInt(7, uF.parseToInt(getOrgid()));
			//pst.setInt(8, uF.parseToInt(getState()));
			pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println(" pst 4 in viewForm5PTChallanPdfReports==>"+pst);

		} else {	
			for(int j=0;j<getEmpIds().length;j++){
				emp+=getEmpIds()[j]+",";
			}
			if(emp.contains(",")){ 
				emp=emp.substring(0, emp.length()-1);
			}
			
			/*pst = con.prepareStatement("select count(emp_id)as count1,min(paid_from) as paid_from," +
					"max(paid_to)as paid_to from payroll_generation where month in ("+totalMonths+") " +
					"and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? " +
					"and emp_id in (select emp_id from employee_official_details where org_id=? " +
					"and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) group by paid_from,paid_to order by paid_from,paid_to");	
*/			
			StringBuilder sb=new StringBuilder();
			sb.append("select count(emp_id)as count1,min(paid_from) as paid_from," +
					"max(paid_to)as paid_to from payroll_generation where month in ("+totalMonths+") " +
					"and financial_year_from_date=? and financial_year_to_date=? and is_paid=true and salary_head_id=?");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			sb.append(" group by paid_from,paid_to order by paid_from,paid_to");
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,PROFESSIONAL_TAX);
			//pst.setInt(4, uF.parseToInt(getOrgid()));
			//pst.setInt(8, uF.parseToInt(getState()));
				//,paid_from,paid_to
//				System.out.println("pst===="+pst);
//			System.out.println(" pst 5 in viewForm5PTChallanPdfReports==>"+pst);

			rs = pst.executeQuery();
			while(rs.next())
			{
				paid_from=rs.getString("paid_from");
				paid_to=rs.getString("paid_to");
			}
	        rs.close();
	        pst.close();
			
			/*pst = con.prepareStatement("select count(emp_id)as count1,amount from payroll_generation " +
					"where month in ("+totalMonths+") and financial_year_from_date=? and financial_year_to_date=? " +
					"and salary_head_id=? and emp_id in ("+emp+") and emp_id in (select emp_id " +
					"from employee_official_details where org_id=? " +
					"and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) group by amount order by amount");	
*/			
			
			sb=new StringBuilder();
			sb.append("select count(emp_id)as count1,amount from payroll_generation where month in ("+totalMonths+") and is_paid=true " +
				"and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=?");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			sb.append(" group by amount order by amount");
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,PROFESSIONAL_TAX);
			
//			System.out.println(" pst 6* in viewForm5PTChallanPdfReports==>"+pst);	
		
			//pst.setInt(4, uF.parseToInt(getOrgid()));
			//pst.setInt(5, uF.parseToInt(getState()));
		}
//		System.out.println(" pst 6 in viewForm5PTChallanPdfReports==>"+pst);
		//,paid_from,paid_to
		
			rs = pst.executeQuery();
			Map<String,String> hmempcnt = new HashMap<String,String>();
			while(rs.next()) {
				int empcnt=rs.getInt("count1");
				int amnt=rs.getInt("amount");
				int total=empcnt*amnt;
				
				hmempcnt.put(amnt+"TOTAL", total+"");
				hmempcnt.put(amnt+"", empcnt+"");		
				totalTaxAmount+=total;
			}
	        rs.close();
	        pst.close();
		
			if (getChallanNum() != null) {
				pst = con.prepareStatement("select entry_date from challan_details where challan_no=?");
				pst.setString(1, getChallanNum());
				rs = pst.executeQuery();
				while (rs.next()) {
					setChallanDate(rs.getString("entry_date"));
				}
		        rs.close();
		        pst.close();
			}
		
		request.setAttribute("hmempcnt",hmempcnt);
		request.setAttribute("alList",alList);
		request.setAttribute("hmMap",hmMap);
		request.setAttribute("hmPTOtherCharge",hmPTOtherCharge);
		
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	
	public String viewFormPTChallanPdfReports(){
		
//		System.out.println("in viewFormPTChallanPdfReports==");
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			
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
			int i=0;
			String emp="";
			if(getOperation()!=null && getOperation().equalsIgnoreCase("pdf") && getChallanNum()!=null){

				/*pst = con.prepareStatement("select * from challan_details where challan_no = ? " +
						"and challan_type=? and is_paid=? and emp_id in (select emp_id " +
						"from employee_official_details where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
						"state s where wi.wlocation_state_id = s.state_id and s.state_id=?))");
				*/
				
				StringBuilder sb=new StringBuilder();
				sb.append("select * from challan_details where challan_no = ? " +
						"and challan_type=? and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				pst=con.prepareStatement(sb.toString());
				pst.setString(1,getChallanNum());
				pst.setInt(2,PROFESSIONAL_TAX);
				pst.setBoolean(3,true);
				//pst.setInt(4, uF.parseToInt(getOrgid()));
				//pst.setInt(5, uF.parseToInt(getState()));
//				System.out.println("pst0 in viewFormPTChallanPdfReports=="+pst);
				rs= pst.executeQuery();
				StringBuilder sbMonths = new StringBuilder();
				while(rs.next()){
					
					String[] arr = rs.getString("month").split(",");
					
					for(i=0; arr!=null && i<arr.length; i++){
						if(arr[i]!=null && arr[i].length()>0){
							sbMonths.append(arr[i]+",");
						}					
					}
				}
		        rs.close();
		        pst.close();
		        
				sbMonths.replace(0, sbMonths.length(), sbMonths.substring(0, sbMonths.length()-1));
			
				/*pst = con.prepareStatement("select min(paid_from) as paid_from,max(paid_to)as paid_to from payroll_generation " +
					" where month in("+sbMonths.toString()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? " +
							"and emp_id in (select emp_id from challan_details where challan_no = ? and challan_type=? and is_paid=?" +
							" and emp_id in (select emp_id from employee_official_details " +
							"where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
							"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) " +
							"and financial_year_from_date=? and financial_year_to_date=?)" +
							" group by paid_from,paid_to order by paid_from,paid_to");	
				*/
				
				sb=new StringBuilder();
				sb.append("select min(paid_from) as paid_from,max(paid_to)as paid_to from payroll_generation " +
					" where month in("+sbMonths.toString()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? " +
							"and emp_id in (select emp_id from challan_details where challan_no = ? and challan_type=? and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				sb.append(" and financial_year_from_date=? and financial_year_to_date=?)" +
							" group by paid_from,paid_to order by paid_from,paid_to");
				
				pst=con.prepareStatement(sb.toString());
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3,PROFESSIONAL_TAX);
				pst.setString(4,getChallanNum());
				pst.setInt(5,PROFESSIONAL_TAX);
				pst.setBoolean(6,true);
				//pst.setInt(7, uF.parseToInt(getOrgid()));
				//pst.setInt(8, uF.parseToInt(getState()));
				pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				
	//			System.out.println("pst1 in viewFormPTChallanPdfReports=="+pst);

//				System.out.println("pst===="+pst);
				rs = pst.executeQuery();
				while(rs.next())
				{
					paid_from=rs.getString("paid_from");
					paid_to=rs.getString("paid_to");
				}
		        rs.close();
		        pst.close();
				
				/*
				pst = con.prepareStatement("select count(emp_id) as count1,amount from challan_details where challan_no=? and is_paid=? and emp_id in " +
						"(select emp_id from employee_official_details where org_id=? " +
						"and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
						"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) group by amount");
				*/
				
		        sb=new StringBuilder();
		        sb.append("select count(emp_id) as count1,amount from challan_details where challan_no=? and is_paid=?");
		        if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
		        sb.append(" group by amount");
		        pst=con.prepareStatement(sb.toString());
		        
		        pst.setString(1,getChallanNum());
				pst.setBoolean(2,uF.parseToBoolean(getIspaid()));
			
	//			System.out.println("pst 2 in viewFormPTChallanPdfReports"+pst);

				//pst.setInt(3, uF.parseToInt(getOrgid()));		
				//pst.setInt(4, uF.parseToInt(getState()));
				 
			}else if(getOperation()!=null && getOperation().equalsIgnoreCase("pdf")){
	//			System.out.println(" else 1");
				
				/*pst = con.prepareStatement("select * from challan_details where entry_date = ? and challan_type=? and is_paid=? and emp_id in (select emp_id " +
						"from employee_official_details where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
						"state s where wi.wlocation_state_id = s.state_id and s.state_id=?))");
				*/
				StringBuilder sb=new StringBuilder();
				sb.append("select * from challan_details where entry_date = ? and challan_type=? and is_paid=?");
				 if(sbEmp!=null && !sbEmp.equals("")){
						sb.append(" and emp_id in ("+sbEmp+")");
					}else{
						sb.append(" and emp_id in (0)");
					}
			    pst=con.prepareStatement(sb.toString());

				
				pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setInt(2,PROFESSIONAL_TAX);
				pst.setBoolean(3,false);
				//pst.setInt(4, uF.parseToInt(getOrgid()));
				//pst.setInt(5, uF.parseToInt(getState()));
	//			System.out.println("pst 3 in viewFormPTChallanPdfReports"+pst);
				rs= pst.executeQuery();
				
				StringBuilder sbMonths = new StringBuilder();
				
				while(rs.next()){
					
					String[] arr = rs.getString("month").split(",");
					for(i=0; arr!=null && i<arr.length; i++){
						if(arr[i]!=null && arr[i].length()>0){
							sbMonths.append(arr[i]+",");
						}					
					}
				}
		        rs.close();
		        pst.close();
				
				sbMonths.replace(0, sbMonths.length(), sbMonths.substring(0, sbMonths.length()-1));
				
					/*pst = con.prepareStatement("select count(emp_id)as count1,min(paid_from) as paid_from,max(paid_to)as paid_to from payroll_generation " +
						" where month in("+sbMonths.toString()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? " +
						"and emp_id in (select emp_id from challan_details where entry_date = ? and challan_type=? and is_paid=?" +
						" and emp_id in (select emp_id from employee_official_details " +
						"where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
						"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) " +
						"and financial_year_from_date=? and financial_year_to_date=?)" +
						" group by paid_from,paid_to order by paid_from,paid_to");	
					*/
					
					sb=new StringBuilder();
					
					sb.append("select count(emp_id)as count1,min(paid_from) as paid_from,max(paid_to)as paid_to from payroll_generation " +
						" where month in("+sbMonths.toString()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? " +
						"and emp_id in (select emp_id from challan_details where entry_date = ? and challan_type=? and is_paid=?");
					
					if(sbEmp!=null && !sbEmp.equals("")){
						sb.append(" and emp_id in ("+sbEmp+")");
					}else{
						sb.append(" and emp_id in (0)");
					}
					sb.append(" and financial_year_from_date=? and financial_year_to_date=?)" +
						" group by paid_from,paid_to order by paid_from,paid_to");
					pst=con.prepareStatement(sb.toString());
					
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3,PROFESSIONAL_TAX);
					pst.setDate(4, uF.getDateFormat(getChallanDate(), DBDATE));
					pst.setInt(5,PROFESSIONAL_TAX);
					pst.setBoolean(6,false);
					//pst.setInt(7, uF.parseToInt(getOrgid()));
					//pst.setInt(8, uF.parseToInt(getState()));
					pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
					pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//					System.out.println("pst===="+pst);
	//				System.out.println("pst 4 in viewFormPTChallanPdfReports"+pst);

					rs = pst.executeQuery();
					while(rs.next())
					{
						paid_from=rs.getString("paid_from");
						paid_to=rs.getString("paid_to");
					}
			        rs.close();
			        pst.close();
				
				/*pst = con.prepareStatement("select count(emp_id)as count1,amount from payroll_generation where" +
						" month in ("+sbMonths.toString()+") and financial_year_from_date=? and" +
						" financial_year_to_date=? and salary_head_id=? and emp_id in " +
						"(select emp_id from challan_details where entry_date = ? and challan_type=? and is_paid=? " +
						" and emp_id in (select emp_id from employee_official_details where org_id=? " +
						"and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
						"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) " +
						"and financial_year_from_date=? and financial_year_to_date=?) group by amount order by amount");
				*/
				
				sb=new StringBuilder();
				sb.append("select count(emp_id)as count1,amount from payroll_generation where" +
						" month in ("+sbMonths.toString()+") and financial_year_from_date=? and" +
						" financial_year_to_date=? and salary_head_id=? and emp_id in " +
						"(select emp_id from challan_details where entry_date = ? and challan_type=? and is_paid=? ");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				sb.append(" and financial_year_from_date=? and financial_year_to_date=?) group by amount order by amount");
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3,PROFESSIONAL_TAX);
				pst.setDate(4, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setInt(5,PROFESSIONAL_TAX);
				pst.setBoolean(6,false);
				//pst.setInt(7, uF.parseToInt(getOrgid()));
				//pst.setInt(8, uF.parseToInt(getState()));
				pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
	//					System.out.println("pst 5 in viewFormPTChallanPdfReports"+pst);
			}else{
				
		//		System.out.println(" else 2");
				for(int j=0;j<getEmpIds().length;j++){
					emp+=getEmpIds()[j]+",";
				}
				if(emp.contains(",")){ 
					emp=emp.substring(0, emp.length()-1);
				}
				
				
				pst = con.prepareStatement("select count(emp_id)as count1,min(paid_from) as paid_from,max(paid_to)as paid_to from payroll_generation where  " +
						" month in ("+totalMonths+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? " +
						"and emp_id in (select emp_id from employee_official_details where org_id=? " +
						"and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
						"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) group by paid_from,paid_to order by paid_from,paid_to");	
			
				StringBuilder sb=new StringBuilder();
				sb.append("select count(emp_id)as count1,min(paid_from) as paid_from,max(paid_to)as paid_to from payroll_generation where  " +
						" month in ("+totalMonths+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				sb.append(" group by paid_from,paid_to order by paid_from,paid_to");
				pst=con.prepareStatement(sb.toString());
				
				
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3,PROFESSIONAL_TAX);
				//pst.setInt(4, uF.parseToInt(getOrgid()));
				//pst.setInt(8, uF.parseToInt(getState()));
		//		System.out.println(" pst 6 in viewFormPTChallanPdfReports="+pst);
				rs = pst.executeQuery();
				while(rs.next())
				{
					paid_from=rs.getString("paid_from");
					paid_to=rs.getString("paid_to");
				}
		        rs.close();
		        pst.close();
				
				/*pst = con.prepareStatement("select count(emp_id)as count1,amount from payroll_generation where" +
					" month in ("+totalMonths+") and financial_year_from_date=? and" +
					" financial_year_to_date=? and salary_head_id=? and emp_id in ("+emp+") and emp_id in (select emp_id from " +
					"employee_official_details where org_id=? and wlocation_id in (SELECT wi.wlocation_id FROM work_location_info wi," +
					"state s where wi.wlocation_state_id = s.state_id and s.state_id=?)) group by amount order by amount");	
*/				
		        sb=new StringBuilder();
		        sb.append("select count(emp_id)as count1,amount from payroll_generation where" +
					" month in ("+totalMonths+") and financial_year_from_date=? and" +
					" financial_year_to_date=? and salary_head_id=? and emp_id in ("+emp+")");
		        if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
		        sb.append(" group by amount order by amount");
		        pst=con.prepareStatement(sb.toString());
		        	
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3,PROFESSIONAL_TAX);
				//pst.setInt(4, uF.parseToInt(getOrgid()));
				//pst.setInt(5, uF.parseToInt(getState()));
		//		System.out.println("pst 7 in viewFormPTChallanPdfReports="+pst);
			}
			rs = pst.executeQuery();
			Map<String,String> hmempcnt = new HashMap<String,String>();
			Map<String, Map<String, String>> hmPTDetails = new LinkedHashMap<String, Map<String, String>>();
			while(rs.next())
			{
				int empcnt=rs.getInt("count1");
				int amnt=rs.getInt("amount");
				int total=empcnt*amnt;
				
				hmempcnt.put(amnt+"TOTAL", total+"");
				hmempcnt.put(amnt+"", empcnt+"");		
				totalTaxAmount+=total;
				
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("EMP_COUNT",""+empcnt);
				hmInner.put("AMOUNT", ""+amnt);
				hmInner.put("TOTAL_AMOUNT", ""+total);
				
				hmPTDetails.put(rs.getString("amount"), hmInner);
				request.setAttribute("hmPTDetails", hmPTDetails);
			
			}
	        rs.close();
	        pst.close();
			
			pst = con.prepareStatement("select * from deduction_details_india where state_id=? and financial_year_from=? and financial_year_to=?");
			pst.setInt(1, uF.parseToInt(getState()));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
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
			
		/*	if(strPtAmt!=null && !strPtAmt.equals("")){
				List<String> alMonthList = new ArrayList<String>();
				
				pst = con.prepareStatement("select month from challan_details where challan_no=? group by month");
				pst.setString(1, getChallanNum());
				rs = pst.executeQuery();
				while(rs.next()){
					//strMonth = rs.getString("month");
					String[] strTemp = rs.getString("month").split(",");
					for (int j = 0 ; j < strTemp.length; j++){
						if(!strTemp[j].trim().equals("")){
							if(!alMonthList.contains(strTemp[j].trim())){
								alMonthList.add(strTemp[j].trim());
							}
						}
					}
				}
		        rs.close();
		        pst.close();
		        StringBuilder sbMonths = null;
				String strMonthDetails = "";
				String startYear = uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yy");
				String endYear = uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yy");
				for (int j=0; j < alMonthList.size(); j++){
					if (sbMonths == null){
						sbMonths = new StringBuilder();
						sbMonths.append(alMonthList.get(j));
						
						if(uF.parseToInt(alMonthList.get(j)) == 1 || uF.parseToInt(alMonthList.get(j)) == 2 || uF.parseToInt(alMonthList.get(j)) == 3 ){
							strMonthDetails = uF.getShortMonth(uF.parseToInt(alMonthList.get(j)))+ endYear;
						} else {
							strMonthDetails = uF.getShortMonth(uF.parseToInt(alMonthList.get(j)))+ startYear;
						}
						
					} else {
						sbMonths.append(","+alMonthList.get(j));
						
						if(uF.parseToInt(alMonthList.get(j)) == 1 || uF.parseToInt(alMonthList.get(j)) == 2 || uF.parseToInt(alMonthList.get(j)) == 3 ){
							strMonthDetails +=","+ uF.getShortMonth(uF.parseToInt(alMonthList.get(j)))+ endYear;
						} else {
							strMonthDetails +=","+ uF.getShortMonth(uF.parseToInt(alMonthList.get(j)))+ startYear;
						}
					}
				} 
				request.setAttribute("strMonthDetails", strMonthDetails);
//				Map<String, Map<String, String>> hmPTDetails = new LinkedHashMap<String, Map<String, String>>();
				if (sbMonths != null){
				
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select count(eod.emp_id) as empcnt, amount,sum(amount) as totalamt from payroll_generation pg," +
							"employee_official_details eod where month in ("+sbMonths.toString()+") and financial_year_from_date=? " +
							"and financial_year_to_date=? and salary_head_id =? and is_paid=true and pg.emp_id=eod.emp_id and org_id=? " +
							"and amount in ("+strPtAmt+") and pg.emp_id in (select emp_id from challan_details where financial_year_from_date = ?" +
							" and financial_year_to_date = ? and challan_type=?  and challan_no=? and emp_id in (select emp_id from " +
							"employee_official_details where org_id=? and wlocation_id in (select wlocation_id from work_location_info where " +
							"wlocation_state_id=?))) and eod.wlocation_id in (select wlocation_id from work_location_info where wlocation_state_id=?) group by amount");
					
					pst = con.prepareStatement(sbQuery.toString());
					pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3, PROFESSIONAL_TAX);
					pst.setInt(4, uF.parseToInt(getOrgid()));
					pst.setDate(5,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(6,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(7, PROFESSIONAL_TAX);
					pst.setString(8, getChallanNum());
					pst.setInt(9, uF.parseToInt(getOrgid()));
					pst.setInt(10, uF.parseToInt(getStateId()));
					pst.setInt(11, uF.parseToInt(getStateId()));
//					System.out.println("pst=====>"+pst);
					
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
				}
			}*/
			if (getChallanNum() != null) {
				pst = con.prepareStatement("select entry_date from challan_details where challan_no=?");
				pst.setString(1, getChallanNum());
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					setChallanDate(rs.getString("entry_date"));
				}
		        rs.close();
		        pst.close();
		        
			}

			Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, sessionEmp_id);
			request.setAttribute("hmEmpProfile", hmEmpProfile);
			request.setAttribute("hmPTSlab", hmPTSlab);
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return null;
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
	public String getOrgid() {
		return orgid;
	}
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
	public String getIspaid() {
		return ispaid;
	}
	public void setIspaid(String ispaid) {
		this.ispaid = ispaid;
	}
//	public String getLocationId() {
//		return locationId;
//	}
//	public void setLocationId(String locationId) {
//		this.locationId = locationId;
//	}
	public String getAmtTax() {
		return amtTax;
	}
	public void setAmtTax(String amtTax) {
		this.amtTax = amtTax;
	}
	public String getInterestAmt() {
		return interestAmt;
	}
	public void setInterestAmt(String interestAmt) {
		this.interestAmt = interestAmt;
	}
	public String getPenaltyAmt() {
		return penaltyAmt;
	}
	public void setPenaltyAmt(String penaltyAmt) {
		this.penaltyAmt = penaltyAmt;
	}
	public String getCompositionMoney() {
		return compositionMoney;
	}
	public void setCompositionMoney(String compositionMoney) {
		this.compositionMoney = compositionMoney;
	}
	public String getFineAmt() {
		return fineAmt;
	}
	public void setFineAmt(String fineAmt) {
		this.fineAmt = fineAmt;
	}
	public String getFeesAmt() {
		return feesAmt;
	}
	public void setFeesAmt(String feesAmt) {
		this.feesAmt = feesAmt;
	}
	public String getAdvanceAmt() {
		return advanceAmt;
	}
	public void setAdvanceAmt(String advanceAmt) {
		this.advanceAmt = advanceAmt;
	}
	public String getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(String totalAmt) {
		this.totalAmt = totalAmt;
	}
//	public String getStateId() {
//		return stateId;
//	}
//	public void setStateId(String stateId) {
//		this.stateId = stateId;
//	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getSbEmp() {
		return sbEmp;
	}

	public void setSbEmp(String sbEmp) {
		this.sbEmp = sbEmp;
	}

}