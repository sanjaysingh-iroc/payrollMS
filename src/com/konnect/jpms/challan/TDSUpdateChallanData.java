package com.konnect.jpms.challan;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.TextField;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

import com.opensymphony.xwork2.ActionSupport;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class TDSUpdateChallanData   extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
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
	String emp_id; 
	int totalTaxAmount;
	int totalNumOfEmployee;
	double totalAmount;
	String paidDate;
	 String challanDate;
	 String challanNum;
	 String cheque_no;
	 String operation;
	 String acknowledgement_no;
	 String brc_code;
	 double totalContribution;
	double employerContribution;
	double employeeContribution;
	double incomeTax;
	double TOTAL_EDUCATION_CESS;
	String strFinancialYearStart;
	String strFinancialYearEnd;
	 String sessionEmp_Id;
	 String f_org; 
	 
	 String strIncomeTax; 
	 String underSection234; 
	 String surcharge; 
	 String eduCess;
	 String interestAmt; 
	 String penaltyAmt;
	 String bankName;
	 String bankBranch;
	 String modeTDSDeposit;
	 
	 String sbEmp;
	 
	
	public String execute() throws Exception {

		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		sessionEmp_Id=(String) session.getAttribute(EMPID);
		if(getChallanDate()!=null && getOperation()!=null && getOperation().equalsIgnoreCase("del")){
			deleteChallanDetails();
		}else if(getChallanDate()!=null && getChallanNum()!=null && getOperation().equalsIgnoreCase("update")){
			updateChallanNumber();
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("insert")){
			insertUnpaidAmount();
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("otherCharges")){
			updateOtherCharges();
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("pdf")){
			viewTDSChallanPdfReports();
			generateNewTDSTaxChallanPdfReports();
		}
		
			return SUCCESS;
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
			pst = con.prepareStatement("UPDATE challan_details SET interest_amt=?,penalty_amt=?,under_section234=?,surcharge=?,edu_cess=?,income_tax=?" +
					" WHERE entry_date =? and challan_type=? and is_paid=? and emp_id in (select emp_id from employee_official_details where  org_id=?)");
			pst.setDouble(1, uF.parseToDouble(getInterestAmt()));
			pst.setDouble(2, uF.parseToDouble(getPenaltyAmt()));
			pst.setDouble(3, uF.parseToDouble(getUnderSection234()));
			pst.setDouble(4, uF.parseToDouble(getSurcharge()));
			pst.setDouble(5, uF.parseToDouble(getEduCess()));
			pst.setDouble(6, uF.parseToDouble(getStrIncomeTax()));
			pst.setDate(7, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setInt(8,TDS);
			pst.setBoolean(9,false);
			pst.setInt(10, uF.parseToInt(getF_org()));
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
	private void generateNewTDSTaxChallanPdfReports() {
		
		try {
//			Connection con = null;
//			Database db = new Database();
//			db.setRequest(request);
//			con = db.makeConnection(con);
			UtilityFunctions uF = new UtilityFunctions();
			List<Integer> alList=(List<Integer>)request.getAttribute("alList");
			Map<String,String> hmempcnt =(Map<String,String> )request.getAttribute("hmempcnt");
			Map<Integer,Map<String,String>> hmMap=(Map<Integer,Map<String,String>>)request.getAttribute("hmMap");
			Map<String,String> hmTds=(Map<String,String>)request.getAttribute("hmTds");
			Map<String,String> hmOrg=new HashMap<String, String>();
			
			getOrgDetails(hmOrg);
				
			if(hmMap==null)hmMap=new HashMap<Integer, Map<String,String>>();
			
			Map<String,String> hmEmpWlocationMap = (Map<String,String>)request.getAttribute("hmEmpWlocationMap");
			if(hmEmpWlocationMap == null) hmEmpWlocationMap=new HashMap<String, String>();
			
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
	        
	        PdfPTable table = new PdfPTable(10);
			table.setWidthPercentage(100);        
	        
	        
	        PdfPCell row1 =new PdfPCell(new Paragraph("Imporatant: Please see notes overleaf before\nfilling up the challan",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT | Rectangle.TOP);
	        row1.setColspan(3);        
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        
	        row1 =new PdfPCell(new Paragraph("T.D.S./TCS TAX CHALLAN",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.TOP);        
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Single Copy (to be sent to the ZAO)",small));
		    row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);        
		    row1.setPadding(2.5f);
	        row1.setColspan(3);
		    table.addCell(row1);
		    
		    //NEW ROW
		    row1 =new PdfPCell(new Paragraph("CHALLAN NO.\n     ITNS\n  ITNS 281",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setColspan(3);        
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Tax Applicable (Tick One)*\nTAX DEDUCTED / COLLECTED AT SOURCE FROM",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);        
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Assessment\n    Year   \n"+ (uF.parseToInt(uF.getDateFormat(strFinancialYearStart,DATE_FORMAT, "yyyy"))+1) +"-"+ (uF.parseToInt(uF.getDateFormat(strFinancialYearEnd,DATE_FORMAT, "yyyy"))+1) ,smallBold));
		    row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.RIGHT);        
		    row1.setPadding(2.5f);
	        row1.setColspan(3);
		    table.addCell(row1);
		    
		    //New Row
		    /*TextField nameField = new TextField(writer, 
					new Rectangle(0,0,200,10), "nameField");*/
		    row1 =new PdfPCell(new Paragraph("(0020)Companies (0021)Non-Companies",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.LEFT|Rectangle.RIGHT);
	        row1.setArabicOptions(TextField.READ_ONLY);        
	        row1.setColspan(10);        
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("Tax Deduction Account No. (T.A.N.) : "+uF.showData(hmOrg.get("ORG_TAN_NO"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT|Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(10);
	        table.addCell(row1);
	        
	        
//	        //new row
//	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_TAN_NO"), ""),small));
//	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//	        row1.setBorder(Rectangle.LEFT|Rectangle.RIGHT);
//	        row1.setPadding(2.5f);
//	        row1.setColspan(10);
//	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("Full Name: "+uF.showData(hmOrg.get("ORG_NAME"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT|Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(10);
	        table.addCell(row1);
	        
//	        //New Row
//	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_NAME"), ""),small));    
//	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//	        row1.setBorder(Rectangle.LEFT|Rectangle.RIGHT);
//	        row1.setPadding(2.5f);
//	        row1.setColspan(10);
//	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("Flat/Door/Block No.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Name of Premises / Building /Village",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);        
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_ADDRESS"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);        
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Road/Street/Lane/Post Office",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Area/Locality/Taluka/Sub-Division",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);        
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);        
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Town/City/District",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("State/Union Territoy",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Pin",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);        
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph(hmOrg.get("ORG_CITY"),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(hmOrg.get("ORG_STATE"),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_PINCODE"),""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);        
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Type of payment",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Code *",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("92B",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("FOR USE IN RECEIVING BANK\n\n\nDebit to A/c Cheque credited on\n\n(DD-MM-YYYY)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5); 
	        row1.setRowspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("TDS/TCS Payable by Taxpayer",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("(200)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("TDS/TCS Regular Assessement()Raised by I.T. Deptt.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("(400)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("DETAILS OF PAYMENT",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Amount (in Rs. only)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Income Tax",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	           
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmTds.get("INCOME_TAX"), "0"),small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        //New Row 
	        row1 =new PdfPCell(new Paragraph("Surcharge",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmTds.get("SURCHARGE"), "0"),small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Space for Bank Seal",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setColspan(5); 
	        row1.setRowspan(11);
	        table.addCell(row1);
	        
	      //New Row 
	        row1 =new PdfPCell(new Paragraph("Education Cess",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmTds.get("EDU_CESS"), "0"),small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        //New Row 
	        row1 =new PdfPCell(new Paragraph("Interest",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmTds.get("INTEREST_AMT"), "0"),small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("Penalty",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmTds.get("PENALTY_AMT"), "0"),small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("Others",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmTds.get("UNDER_SECTION_234"), "0"),small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("Total",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        //INCOME_TAX EDU_CESS UNDER_SECTION_234 INTEREST_AMT PENALTY_AMT SURCHARGE
	        double totalTds = uF.parseToDouble(hmTds.get("INCOME_TAX")) + uF.parseToDouble(hmTds.get("EDU_CESS")) + uF.parseToDouble(hmTds.get("INTEREST_AMT")) +
	        uF.parseToDouble(hmTds.get("PENALTY_AMT")) + uF.parseToDouble(hmTds.get("SURCHARGE")) + uF.parseToDouble(hmTds.get("UNDER_SECTION_234"));
	        row1 =new PdfPCell(new Paragraph(""+totalTds,smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        //New Row
//	        row1 =new PdfPCell(new Paragraph("Total(in Words):\n\nRs."+uF.showData(uF.digitsToWords((int)employeeContribution),"0"),small));
	        
	        String digitTotal="";
	        String strTotalAmt=""+totalTds;
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
	        	int totalAmt1=(int)totalTds;
	        	digitTotal=uF.digitsToWords(totalAmt1);
	        }
	        
	        row1 =new PdfPCell(new Paragraph("Total(in Words):\n\nRs."+uF.showData(digitTotal,"0"),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Paid in Cash/Debit to A/c /Cheque No.  "+uF.showData(getCheque_no(),""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Dated ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);
	        table.addCell(row1);
	        
	      //New Row
//	        row1 =new PdfPCell(new Paragraph("Drawn on     "+uF.showData(hmEmpWlocationMap.get(getEmpIds()),"")+"\n\n\n\n   (Name of Bank & Branch) ",small));
	        //CHALLAN_BANK_NAME CHALLAN_BRANCH_NAME CHALLAN_MODE_TDS_DEPOSIT
	        row1 =new PdfPCell(new Paragraph("Drawn on     "+uF.showData(hmTds.get("CHALLAN_BANK_NAME"),"")+", "+uF.showData(hmTds.get("CHALLAN_BRANCH_NAME"),"")+"\n\n\n\n   (Name of Bank & Branch) ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Dated "+uF.getDateFormat(getChallanDate(),DBDATE, CF.getStrReportDateFormat()),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Siganture of person making payment",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Taxpayers Counterfol  (To be filled up by tax payer) ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT | Rectangle.TOP);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Space for Bank Seal",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.LEFT | Rectangle.BOTTOM);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        row1.setRowspan(9);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("TAN   "+uF.showData(hmOrg.get("ORG_TAN_NO"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Received from:   "+uF.showData(hmOrg.get("ORG_NAME"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Cash/Debit to A/c/Cheque No.  "+uF.showData(getCheque_no(),""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);
	        table.addCell(row1);
	        
//	        row1 =new PdfPCell(new Paragraph("For Rs.  "+employeeContribution,small));
	        row1 =new PdfPCell(new Paragraph("For Rs.  "+totalTds,small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT); 
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	      //New Row 
//	        row1 =new PdfPCell(new Paragraph("(in words)\n\n  Rs. "+uF.showData(uF.digitsToWords((int)employeeContribution),"0"),small));
	        row1 =new PdfPCell(new Paragraph("(in words)\n\n  Rs. "+uF.showData(digitTotal,"0"),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Drawn on "+uF.showData(hmTds.get("CHALLAN_BANK_NAME"),"")+", "+uF.showData(hmTds.get("CHALLAN_BRANCH_NAME"),"")+"\n\n\n\n   (Name of Bank & Branch) ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("on account of Tax Deducted at Source (TDS)/Companies/Non-Companies",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Tax Collected at source (TCS) from\nfor the Assessment Year  "+(uF.parseToInt(uF.getDateFormat(strFinancialYearStart,DATE_FORMAT, "yyyy"))+1) +"-"+ (uF.parseToInt(uF.getDateFormat(strFinancialYearEnd,DATE_FORMAT, "yyyy"))+1),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Section: ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT | Rectangle.BOTTOM);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	       
	        
	        document.add(table);
	        
	        document.close();
	        
			String filename="TDSTAXChallan_"+uF.getDateFormat(getChallanDate(),DBDATE, "MM")+"_"+uF.getDateFormat(getChallanDate(),DBDATE, "yyyy")+".pdf";
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
	private void getOrgDetails(Map<String, String> hmOrg) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from org_details WHERE org_id=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			rs=pst.executeQuery();
			
			while(rs.next()){
				hmOrg.put("ORG_ID", rs.getString("org_id"));
				hmOrg.put("ORG_NAME", rs.getString("org_name"));
				hmOrg.put("ORG_ADDRESS", rs.getString("org_address"));
				hmOrg.put("ORG_PINCODE", rs.getString("org_pincode"));
				hmOrg.put("ORG_CONTACT", rs.getString("org_contact1"));
				hmOrg.put("ORG_EMAIL", rs.getString("org_email"));
				hmOrg.put("ORG_STATE", CF.getStateNameById(con, uF, rs.getString("org_state_id")));
				hmOrg.put("ORG_COUNTRY", CF.getCountryNameById(con, uF, rs.getString("org_country_id")));
				hmOrg.put("ORG_CITY", rs.getString("org_city"));
				hmOrg.put("ORG_CODE", rs.getString("org_code")); 
				hmOrg.put("ORG_PAN_NO", rs.getString("org_pan_no"));
				hmOrg.put("ORG_TAN_NO", rs.getString("org_tan_no")); 
			}
	        rs.close();
	        pst.close();
			Map<String,String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			request.setAttribute("hmEmpWlocationMap",hmEmpWlocationMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		} 
		
	}
	public void deleteChallanDetails() {
//		System.out.println("in deleteChallanDetails");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			/*pst = con.prepareStatement("delete from challan_details WHERE entry_date =? " +
					"and is_paid=? and challan_type=? and emp_id in (select emp_id from employee_official_details where  org_id=?)");
			*/
			StringBuilder sb = new StringBuilder();
			sb.append("delete from challan_details WHERE entry_date=? and is_paid=? and challan_type=?");
			if(sbEmp!=null && sbEmp.equals("")) {
				sb.append(" and emp_id in ("+sbEmp+")");
			} else {
				sb.append(" and emp_id in (0)");
			}
			pst=con.prepareStatement(sb.toString());
			pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setBoolean(2, false);
			pst.setInt(3, TDS);
			//pst.setInt(4, uF.parseToInt(getF_org()));
//			System.out.println("pst in delete function=="+pst);
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
		
//		System.out.println("in updateChallanNumber==>");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
		try{
			if (getFinancialYear() != null) {

				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];

			} else {

				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-"+ strPayCycleDates[1]);

				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];

			}
			
			con = db.makeConnection(con);
			/*pst = con.prepareStatement("UPDATE challan_details SET is_paid =?," +
					"paid_date=?,challan_no=?,cheque_no=?,brc_code=?,acknowledgement_no=?,added_by=?,bank_name=?,bank_branch=?,mode_tds_deposit=? WHERE financial_year_from_date=?" +
						" and financial_year_to_date=? and entry_date =? and challan_type=? and is_paid=? and emp_id in (select emp_id from employee_official_details where  org_id=?)");
		*/
			StringBuilder sb=new StringBuilder();
			sb.append("UPDATE challan_details SET is_paid =?," +
					"paid_date=?,challan_no=?,cheque_no=?,brc_code=?,acknowledgement_no=?,added_by=?,bank_name=?,bank_branch=?,mode_tds_deposit=? WHERE financial_year_from_date=?" +
						" and financial_year_to_date=? and entry_date =? and challan_type=? and is_paid=?");
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
			pst.setString(5,getBrc_code());
			pst.setString(6,getAcknowledgement_no());
			pst.setInt(7,uF.parseToInt(sessionEmp_Id));
			pst.setString(8,getBankName());
			pst.setString(9,getBankBranch());
			pst.setString(10,getModeTDSDeposit());
			pst.setDate(11, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(12, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(13, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setInt(14,TDS);
			pst.setBoolean(15,false);
			//pst.setInt(16, uF.parseToInt(getF_org()));
//			System.out.println("pst for updatechallannumber=="+pst);
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
		/*String strFinancialYearStart = null;
		String strFinancialYearEnd = null;*/
		String months="";
		UtilityFunctions uF = new UtilityFunctions();
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
			
//			System.out.println("totalMonths=====>"+totalMonths);
			List<String> alMonthList = Arrays.asList(totalMonths.split(","));
			if(alMonthList == null) alMonthList = new ArrayList<String>();
			
			for(int i=0;i<getEmpIds().length;i++){
				int amount=0;
				for(int j=0;j<alMonthList.size();j++){
					pst = con.prepareStatement("select sum(actual_tds_amount) as amount from emp_tds_details where financial_year_start=?" +
							" and financial_year_end=?  and _month=? and emp_id=? ");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3,uF.parseToInt(alMonthList.get(j)));
					pst.setInt(4,uF.parseToInt(getEmpIds()[i]));
					rs = pst.executeQuery();
					while(rs.next()){
						amount=rs.getInt("amount");
					}
			        rs.close();
			        pst.close();
					
					pst = con.prepareStatement("select * from challan_details where  financial_year_from_date=? and financial_year_to_date=? " +
							"and month like '%,"+alMonthList.get(j)+",%' and emp_id=? and challan_type=?");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3,uF.parseToInt(getEmpIds()[i]));
					pst.setInt(4,TDS);
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
						pst.setInt(2,amount);
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setBoolean(4,true);
						pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setString(7,","+alMonthList.get(j)+",");
						pst.setInt(8,TDS);
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
	
	public void viewTDSChallanPdfReports()
	{
//		System.out.println("in viewTDSChallanPdfReports");
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
		
		Map<String,String> hmTds=new HashMap<String, String>();
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
			
			 double tds_amount=0;
			 double edu_tax_amount=0;
			 double std_tax_amount=0;
		
			 StringBuilder sb=null;
			 
				if (getChallanNum() != null) {
					/*pst = con.prepareStatement("select distinct(month) from challan_details where challan_no=? and challan_type="+ TDS+ " and is_paid=? " +
							"and emp_id in (select emp_id from employee_official_details where org_id=?)");
					*/
					 sb=new StringBuilder();
					sb.append(" select distinct(month) from challan_details where challan_no=? and challan_type="+ TDS+ " and is_paid=?");
					if(sbEmp!=null && !sbEmp.equals("")){
						sb.append(" and emp_id in ("+sbEmp+")");
					}else{
						sb.append(" and emp_id in (0)");
					}
					pst=con.prepareStatement(sb.toString());
					
					pst.setString(1, getChallanNum());
					pst.setBoolean(2, true);
					//pst.setInt(3, uF.parseToInt(getF_org()));
//					System.out.println(" pst 1 in viewTDSChallanPdfReports=="+pst);
				} else {
	
					/*pst = con.prepareStatement("select distinct(month) from challan_details where entry_date=? and challan_type="+ TDS+ " and is_paid=? " +
							"and emp_id in (select emp_id from employee_official_details where org_id=?)");
				*/
					sb=new StringBuilder();
					sb.append(" select distinct(month) from challan_details where entry_date=? and challan_type="+ TDS+ " and is_paid=?");
					if(sbEmp!=null && !sbEmp.equals("")){
						sb.append(" and emp_id in ("+sbEmp+")");
					}else{
						sb.append(" and emp_id in (0)");
					}
					pst=con.prepareStatement(sb.toString());
					
					pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
					pst.setBoolean(2, false);
					//pst.setInt(3, uF.parseToInt(getF_org()));
//					System.out.println(" pst 2 in viewTDSChallanPdfReports=="+pst);
				}
				
				
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
				
				
				if (getChallanNum() != null) {
					/*pst = con.prepareStatement("select entry_date,cheque_no from challan_details where financial_year_from_date=? "
									+ "and financial_year_to_date=? and challan_no=? and is_paid=? and emp_id in (select emp_id from " +
									"employee_official_details where org_id=?) and challan_type=?");
				*/
					
					sb=new StringBuilder();
					sb.append("select entry_date,cheque_no from challan_details where financial_year_from_date=? "
									+ "and financial_year_to_date=? and challan_no=? and is_paid=?");
					if(sbEmp!=null && !sbEmp.equals("")){
						sb.append(" and emp_id in ("+sbEmp+")");
					}else{
						sb.append(" and emp_id in (0)");
					}
					sb.append(" and challan_type=?");
					pst=con.prepareStatement(sb.toString());
					
					pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setString(3, getChallanNum());
					pst.setBoolean(4, true);
					//pst.setInt(5, uF.parseToInt(getF_org()));
					pst.setInt(5, TDS);
	//				System.out.println("pst new=====>"+pst);
	//				System.out.println(" pst 3 in viewTDSChallanPdfReports=="+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						setChallanDate(rs.getString("entry_date"));
						setCheque_no(rs.getString("cheque_no"));
					}
			        rs.close();
			        pst.close();
					
					
					/*pst = con.prepareStatement("select * from emp_tds_details where _month in ("+month+") and emp_id in " +
							"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
							" and challan_no=? and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=?)) " +
							"and financial_year_start=? and financial_year_end=?");
					*/
					
			        sb=new StringBuilder();
			        sb.append("select * from emp_tds_details where _month in ("+month+") and emp_id in " +
							"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
							" and challan_no=? and is_paid=?");
			        if(sbEmp!=null && !sbEmp.equals("")){
						sb.append(" and emp_id in ("+sbEmp+"))");
					}else{
						sb.append(" and emp_id in (0))");
					}
					sb.append(" and financial_year_start=? and financial_year_end=?");
					pst=con.prepareStatement(sb.toString());
					
					
					pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setString(3, getChallanNum());
					pst.setBoolean(4, true);
					//pst.setInt(5, uF.parseToInt(getF_org()));
					pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				
	//			System.out.println(" pst 4 in viewTDSChallanPdfReports=="+pst);

	//				System.out.println("pst new=====>"+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						tds_amount += rs.getDouble("tds_amount");
						edu_tax_amount += rs.getDouble("edu_tax_amount");
						std_tax_amount += rs.getDouble("std_tax_amount");
					}
			        rs.close();
			        pst.close();
			        
					/*pst = con.prepareStatement("select * from challan_details WHERE challan_no =? and challan_type=? and is_paid = true" +
						" and emp_id in (select emp_id from employee_official_details where org_id=?)");*/
			        
			        sb=new StringBuilder();
			        sb.append("select * from challan_details WHERE challan_no =? and challan_type=? and is_paid = true");
			        if(sbEmp!=null && !sbEmp.equals("")){
						sb.append(" and emp_id in ("+sbEmp+")");
					}else{
						sb.append(" and emp_id in (0)");
					}
			        pst=con.prepareStatement(sb.toString());
			        
					pst.setString(1, getChallanNum());
					pst.setInt(2, TDS);
					//pst.setInt(3, uF.parseToInt(getF_org()));
//					System.out.println("pst=======>"+pst);
//					System.out.println(" pst 5 in viewTDSChallanPdfReports=="+pst);

					rs = pst.executeQuery();
					while(rs.next()){
						hmTds.put("UNDER_SECTION_234", uF.showData(rs.getString("under_section234"), "0"));
						hmTds.put("INTEREST_AMT", uF.showData(rs.getString("interest_amt"), "0"));
						hmTds.put("PENALTY_AMT", uF.showData(rs.getString("penalty_amt"), "0"));
						hmTds.put("SURCHARGE", uF.showData(rs.getString("surcharge"), "0"));
						
						hmTds.put("CHALLAN_BANK_NAME", uF.showData(rs.getString("bank_name"), ""));
						hmTds.put("CHALLAN_BRANCH_NAME", uF.showData(rs.getString("bank_branch"), ""));
						hmTds.put("CHALLAN_MODE_TDS_DEPOSIT", uF.showData(rs.getString("mode_tds_deposit"), ""));
					}
			        rs.close(); 
			        pst.close(); 
					
				} else {
					/*pst = con.prepareStatement("select * from emp_tds_details where _month in ("+month+") and emp_id in " +
							"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
							" and entry_date=? and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=?)) " +
							" and financial_year_start=? and financial_year_end=? ");
					*/
					sb=new StringBuilder();
			        sb.append("select * from emp_tds_details where _month in ("+month+") and emp_id in " +
							"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
							" and entry_date=? and is_paid=?");
			        if(sbEmp!=null && !sbEmp.equals("")){
						sb.append(" and emp_id in ("+sbEmp+"))");
					}else{
						sb.append(" and emp_id in (0))");
					}
			        sb.append(" and financial_year_start=? and financial_year_end=?");
			        
			        pst=con.prepareStatement(sb.toString());
			        
					
					pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(getChallanDate(), DBDATE));
					pst.setBoolean(4, false);
					//pst.setInt(5, uF.parseToInt(getF_org()));
					pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				
	//				System.out.println(" pst 6 in viewTDSChallanPdfReports=="+pst);

	//				System.out.println("pst new=====>"+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						tds_amount += rs.getDouble("tds_amount");
						edu_tax_amount += rs.getDouble("edu_tax_amount");
						std_tax_amount += rs.getDouble("std_tax_amount");
					}
			        rs.close();
			        pst.close();
					
					/*pst = con.prepareStatement("select * from challan_details WHERE entry_date =? and challan_type=? and is_paid = false" +
						" and emp_id in (select emp_id from employee_official_details where org_id=?)");
					*/
					
			        sb=new StringBuilder();
			        sb.append("select * from challan_details WHERE entry_date =? and challan_type=? and is_paid = false");
					 if(sbEmp!=null && !sbEmp.equals("")){
							sb.append(" and emp_id in ("+sbEmp+")");
						}else{
							sb.append(" and emp_id in (0)");
						}
					pst=con.prepareStatement(sb.toString());
					
					pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
					pst.setInt(2, TDS);
					
//					System.out.println(" pst 7 in viewTDSChallanPdfReports=="+pst);

					//pst.setInt(3, uF.parseToInt(getF_org()));
//					System.out.println("pst=======>"+pst);
					rs = pst.executeQuery();
					while(rs.next()){
						hmTds.put("UNDER_SECTION_234", uF.showData(rs.getString("under_section234"), "0"));
						hmTds.put("INTEREST_AMT", uF.showData(rs.getString("interest_amt"), "0"));
						hmTds.put("PENALTY_AMT", uF.showData(rs.getString("penalty_amt"), "0"));
						hmTds.put("SURCHARGE", uF.showData(rs.getString("surcharge"), "0"));
					}
			        rs.close();
			        pst.close();
				}

				
				
//				double totalTDS=tds_amount+edu_tax_amount+std_tax_amount;
				
				
				hmTds.put("INCOME_TAX", ""+uF.formatIntoTwoDecimalWithOutComma(Math.round(tds_amount)));
				hmTds.put("EDU_CESS", ""+uF.formatIntoTwoDecimalWithOutComma(Math.round(edu_tax_amount)+Math.round(std_tax_amount)));
	//			hmTds.put("SURCHARGE", ""+uF.formatIntoTwoDecimalWithOutComma(Math.round(std_tax_amount)));
//				hmTds.put("TOTAL_TDS", ""+uF.formatIntoTwoDecimalWithOutComma(Math.round(totalTDS)));
				
				request.setAttribute("hmTds", hmTds);
		
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public Map getEmpOtherDetails(String empid)
	{
		Map EMPDETAILSMAP=new HashMap();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con = db.makeConnection(con);
			pst = con.prepareStatement("select wlocation_tan_no from work_location_info where wlocation_id=(select wlocation_id from employee_official_details where emp_id=?)");
			pst.setInt(1,uF.parseToInt(empid));
			rs=pst.executeQuery();
			while(rs.next()){
				EMPDETAILSMAP.put("EMPTANNUM",rs.getString("wlocation_tan_no"));	
			}
	        rs.close();
	        pst.close();
			
			pst = con.prepareStatement("select emp_address1,emp_address2,emp_pincode,emp_contactno_mob from employee_personal_details where emp_per_id=?");
			pst.setInt(1,uF.parseToInt(empid));
			rs=pst.executeQuery();
			while(rs.next()){
				EMPDETAILSMAP.put("EMPADDRESS",rs.getString("emp_address1")+rs.getString("emp_address2"));
				EMPDETAILSMAP.put("EMPPINCODE",rs.getString("emp_pincode"));
				EMPDETAILSMAP.put("EMPTELEPHONE",rs.getString("emp_contactno_mob"));
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
		return EMPDETAILSMAP;
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

	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String[] getEmpIds() {
		return empIds;
	}

	public void setEmpIds(String[] empIds) {
		this.empIds = empIds;
	}

	public String getAcknowledgement_no() {
		return acknowledgement_no;
	}
	public void setAcknowledgement_no(String acknowledgement_no) {
		this.acknowledgement_no = acknowledgement_no;
	}
	public String getBrc_code() {
		return brc_code;
	}
	public void setBrc_code(String brc_code) {
		this.brc_code = brc_code;
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
	public String getStrIncomeTax() {
		return strIncomeTax;
	}
	public void setStrIncomeTax(String strIncomeTax) {
		this.strIncomeTax = strIncomeTax;
	}
	public String getUnderSection234() {
		return underSection234;
	}
	public void setUnderSection234(String underSection234) {
		this.underSection234 = underSection234;
	}
	public String getSurcharge() {
		return surcharge;
	}
	public void setSurcharge(String surcharge) {
		this.surcharge = surcharge;
	}
	public String getEduCess() {
		return eduCess;
	}
	public void setEduCess(String eduCess) {
		this.eduCess = eduCess;
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
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankBranch() {
		return bankBranch;
	}
	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}
	public String getModeTDSDeposit() {
		return modeTDSDeposit;
	}
	public void setModeTDSDeposit(String modeTDSDeposit) {
		this.modeTDSDeposit = modeTDSDeposit;
	}
	
	public String getSbEmp() {
		return sbEmp;
	}
	public void setSbEmp(String sbEmp) {
		this.sbEmp = sbEmp;
	}

}
