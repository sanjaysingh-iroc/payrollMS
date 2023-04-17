package com.konnect.jpms.export.payroll;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class RegForm5ReturnChallan implements ServletRequestAware,ServletResponseAware, IStatements {

	
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	String financialYear;
	List<FillMonth> monthList;
	public void execute()
	{
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return;
		

//		setFinancialYear("01/04/2011-31/03/2012");
		monthList = new FillMonth().fillMonth();
		generatePTaxChallanPdfReports();
		return;
		
		
	}
public void generatePTaxChallanPdfReports(){
		
		try {
			UtilityFunctions uF = new UtilityFunctions();

			
			
			
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document, buffer);
		document.open();
				
		String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\"><b>Reg. Form 5</b></font></td></tr>" +
				"<tr><td align=\"left\"><font size=\"1\">Due Dates : 11th November *</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">Name of Branch Office : &nbsp;&nbsp;&nbsp;&nbsp;Andheri</font></td></tr>" +
		"<tr><td align=\"right\"><font size=\"1\">Employers Code No:&nbsp;&nbsp;&nbsp;&nbsp;258746</font></td></tr>" +
		"</table>";
		List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
		Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase.add(supList.get(0));
		document.add(phrase);
		
		document.add(new Paragraph(" "));
		
		String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\"><b>RETURN OF CONTRIBUTIONS</b></font></td></tr>" +
		"<tr><td align=\"center\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"center\"><font size=\"1\">EMPLOYEES' STATE INSURANCE CORPORATION</font></td></tr>" +
		"<tr><td align=\"center\"><font size=\"1\">(Regulation - 26)</font></td></tr>" +
		"</table>";
		List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
		Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase1.add(supList1.get(0));
		document.add(phrase1);
		
		
		String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td><font size=\"1\">&nbsp;&nbsp;Name and address of Factory/Establishment- :</font></td><td><font size=\"1\">"+CF.getStrOrgName()+"</font></td><td></td></tr>" +
		"<tr><td valign=\"top\"><font size=\"1\">&nbsp;&nbsp;</font></td><td><font size=\"1\">"+CF.getStrOrgAddress()+"</font></td><td></td></tr>" +
		"<tr><td><font size=\"1\">&nbsp;&nbsp;Particulars of the Principal employer(s)</font></td><td><font size=\"1\"></font></td><td></td></tr>" +
		"<tr><td><font size=\"1\">a)&nbsp;&nbsp;Name :  </font></td><td></td><td></td></tr>"+
		"<tr><td><font size=\"1\">b)&nbsp;&nbsp;Designation :  </font></td><td></td><td></td></tr>"+
		"<tr><td><font size=\"1\">c)&nbsp;&nbsp;Recidential Address :  </font></td><td></td><td></td></tr>"+
		"</table>";
		List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
		Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase2.add(supList2.get(0));
		document.add(phrase2);

		
		String tbl3="<table><tr><td align=\"left\"><font size=\"2\">Contribution Period from 01/04/2009 To 30/09/2009</font></td></tr>" +
				"<tr><td align=\"left\"><font size=\"1\"></font></td></tr>" +
				"<tr><td align=\"left\"><font size=\"1\">I Furnish below the details of the Employer's and employee's share of Contributions in respect of the under mentioned insured persions. I hereby </font></td></tr>" +
				"<tr><td align=\"left\"><font size=\"1\">declare that the return include each & every employee, employed directly or through an immediate employer in or in connection with </font></td></tr>" +
				"<tr><td align=\"left\"><font size=\"1\">the work of Factory/Establishment or any other work connected with the adminidtration of the factory/establishment of purchase of raw material,</font></td></tr>" +
				"<tr><td align=\"left\"><font size=\"1\">sale or distribution of finished products etc. to whom the ESI Act,1948 applies, in the Contribution period to which this return relates and that </font></td></tr>" +
				"<tr><td align=\"left\"><font size=\"1\">the contributions in respect of Employer's and Employee's share have been correctly paid in accordence with the Provisions of the Act and regulations.</font></td></tr>" +
		"</table>";
		List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
		Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase3.add(supList3.get(0));
		document.add(phrase3);
		document.add(new Paragraph(" "));
		
		
		
		String tbl4 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td><font size=\"1\">&nbsp;&nbsp;Employees' Share Rs.</font></td><td><font size=\"1\">1583</font></td><td></td></tr>" +
		"<tr><td valign=\"top\"><font size=\"1\">&nbsp;&nbsp;Employers' Share Rs.</font></td><td><font size=\"1\">4299</font></td><td></td></tr>" +
		"<tr><td><font size=\"1\">&nbsp;&nbsp;Total Contribution Rs.</font></td><td><font size=\"1\">5882</font></td><td></td></tr>" +
		"<tr><td><font size=\"1\">&nbsp;&nbsp;</font></td><td></td><td></td></tr>"+
		"<tr><td><font size=\"1\">&nbsp;&nbsp;Details of Challans :-</font></td><td></td><td></td></tr>"+
		"</table>";
		List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
		Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase4.add(supList4.get(0));
		document.add(phrase4);
		
		
		String tbl5="<table width=\"100%\" border=\"1\"><tr><td width=\"4%\" align=\"left\"><font size=\"1\">&nbsp;Sr.No.</font></td>" +
		"<td width=\"15%\" align=\"left\"><font size=\"1\">&nbsp;Month</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;Date of Challan</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;Amount(Rs)</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;Name of the Bank& Branch</font></td>" +
		"</tr></table>";
		List<Element> supList5 = HTMLWorker.parseToList(new StringReader(tbl5), null);
		Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase5.add(supList5.get(0));
		document.add(phrase5);
		
		String tbl6="<table width=\"100%\" border=\"1\"><tr><td width=\"4%\" align=\"left\"><font size=\"1\">&nbsp;1</font></td>" +
		"<td width=\"15%\" align=\"left\"><font size=\"1\">&nbsp;Apr-2009</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;0</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;0.00</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;0</font></td>" +
		"</tr></table>";
		List<Element> supList6 = HTMLWorker.parseToList(new StringReader(tbl6), null);
		Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase6.add(supList6.get(0));
		document.add(phrase6);
		
		String tbl7="<table width=\"100%\" border=\"1\"><tr><td width=\"4%\" align=\"left\"><font size=\"1\">&nbsp;</font></td>" +
		"<td width=\"15%\" align=\"left\"><font size=\"1\">&nbsp;</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\"><b>&nbsp;Total amount paid: Rs.</b></font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\"><b>&nbsp;0.00</b></font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr></table>";
		List<Element> supList7 = HTMLWorker.parseToList(new StringReader(tbl7), null);
		Phrase phrase7 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase7.add(supList7.get(0));
		document.add(phrase7);
		
		document.newPage();
		String tbl8="<table><tr><td align=\"left\"><font size=\"1\">I declare that,</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\"></font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(a) All the records and registers have been maintained as per provisions contained in ESI Act, rules and regulations framed therein.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(b) During the period of Return Nos. of Declarations forms have been submitted.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(c) During the above period - Nos. TIC's have been received.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(d) During the above period - Nos. PIC's have been received.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(e) During the above period - Nos. PIC's have been distributed amongst the eligible IPs.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(f) During the above period - Accidents have been reported to the concerned branch office.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(g) During the period of --- Nos. of employees directly employed by us have been covered and a total wages of Rs.  /- have been paid to such employees.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(h) During the period - Nos. have been directly employed by us have not been covered and the total wages of Rs. --- ---- have been paid to such employees.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(i) During the period - Nos. have been directly employed by us have been covered and the total wages of Rs.--- -----have been paid to such employees.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(j) During the period - ----no. of employees employed through immediate employer have been covered and a total wages of Rs. ----- have been paid to such employees.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(k) Following components of wages have been taken into consideration for the purpose of payment of contribution :</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1 Children Education Allowance</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2 Conveyance </font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3 House Rent Allowance</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4 Incentive</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5 OverTime</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;6 BASIC</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(l) Following components of wages have not been taken into consideration for the purpose of payment of Contribution</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(1)</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(2)</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(3)</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">The above mentioned information is based on records and any information if found incorrect will render me liable for presecutions under provisions</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\"> of ESI Act and action for recovery of contribution due along with interest and damages as per provisions of the ESI Act.</font></td></tr>" +
		"</table>";
		List<Element> supList8 = HTMLWorker.parseToList(new StringReader(tbl8), null);
		Phrase phrase8 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase8.add(supList8.get(0));
		document.add(phrase8);
		
		document.add(new Paragraph(" "));
		
		String tbl9="<table><tr><td align=\"left\"><font size=\"1\">Place</font></td><td align=\"left\"><font size=\"1\">Mumbai</font></td><td align=\"left\"><font size=\"1\">Signature & Designation of the Employer</font></td></tr>" +
				"<tr><td align=\"left\"><font size=\"1\">Date</font></td><td align=\"left\"><font size=\"1\">&nbsp;</font></td><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
				"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td><td align=\"left\"><font size=\"1\">&nbsp;</font></td><td align=\"left\"><font size=\"1\">&nbsp;(With Rubber Stamp)</font></td></tr>"+
		"</table>";
		List<Element> supList9 = HTMLWorker.parseToList(new StringReader(tbl9), null);
		Phrase phrase9 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase9.add(supList9.get(0));
		document.add(phrase9);
		
		String tbl10 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">CERTIFICATE BY CHARTERED ACCOUNTANAT.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(To be submitted in case of employers employing 40 or more employees)</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">CERTIFIED that I have Verified the above return from the records & registers of M/SPaypac, B- 10 Sai Prasad .and found it to be correct.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">Signature and seal of Chartered Accountant with</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">Membership No.</font></td></tr>" +
		"</table>";
		List<Element> supList10 = HTMLWorker.parseToList(new StringReader(tbl10), null);
		Phrase phrase10 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase10.add(supList10.get(0));
		document.add(phrase10);
		document.add(new Paragraph(" "));
		
		
		document.newPage();
		
		String tbl11="<table><tr><td align=\"left\"><font size=\"1\"><b>IMPORTANT INSTRUCTIONS :</b></font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">Information to be given in \"Remarks Columns(No. 9)\"</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(i) If any I.P. is is appointed for the first time and/or leaves the service during the Contribution Period indicate \"A\" ..............(date) and/or L.............(date in the remarks column (No.8)</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(ii) Please indicate Insurance Number in chronological (ascending) order.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(iii) Figure in columns 4,5 & 6 shall be in respect of the wage Periods ended during the contribution Period.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(iv) Invariribly strike totals of columns 4,5 & 6 of the return.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(v) No overwriting shall be made. Any corrections ,If made, should be signed by the Employer</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(vi) Every page of this return should bear full signature and rubber stamp of the Employer.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">(vii) Daily wages in Col.7 of the return shall be calculated by dividing figures in Col.5 by figure in Col.4 to two decimal places.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">For * CP ending 31st March, due date is 12th May</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">For * CP ending 30th September, due date is 11th November</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">EMPLOYEES' STATE INSURANCE CORPORATION</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"</table>";
		List<Element> supList11 = HTMLWorker.parseToList(new StringReader(tbl11), null);
		Phrase phrase11= new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase11.add(supList11.get(0));
		document.add(phrase11);
		
		String tbl12 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td><font size=\"1\">&nbsp;&nbsp;Name and address of Factory/Establishment- :</font></td><td><font size=\"1\">"+CF.getStrOrgName()+"</font></td><td></td></tr>" +
		"<tr><td valign=\"top\"><font size=\"1\">&nbsp;&nbsp;</font></td><td><font size=\"1\">"+CF.getStrOrgAddress()+"</font></td><td></td></tr>" +
		"<tr><td><font size=\"1\">&nbsp;&nbsp;Employers Code No.</font></td><td><font size=\"1\">258746</font></td><td></td></tr>" +
		"<tr><td align=\"right\"></td><td></td><td align=\"right\"><font size=\"1\">Period from 01/04/2009 to 30/09/2009  </font></td></tr>"+
		
		"</table>";
		List<Element> supList12 = HTMLWorker.parseToList(new StringReader(tbl12), null);
		Phrase phrase12 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase12.add(supList12.get(0));
		document.add(phrase12);
		
		String tbl13="<table width=\"100%\" border=\"1\"><tr><td width=\"4%\" align=\"left\"><font size=\"1\">&nbsp;Sr.No. <br/>1</font></td>" +
		"<td width=\"15%\" align=\"left\"><font size=\"1\">&nbsp;Insurance Number <br/>2</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;Name of insured person <br/>3</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;No. of days for which wages<br/>4</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;Total amount of Wages Paid<br/>5</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;Employee's contribution deducted<br/>6</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;Average Daily's Wages 5/4<br/>7</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;Whether still continues working<br/>8</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;Remarks *<br/>9</font></td>" +
		"</tr></table>";
		List<Element> supList13 = HTMLWorker.parseToList(new StringReader(tbl13), null);
		Phrase phrase13 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase13.add(supList13.get(0));
		document.add(phrase13);

		String tbl14="<table width=\"100%\" border=\"1\"><tr><td width=\"4%\" align=\"left\"><font size=\"1\">&nbsp;1</font></td>" +
		"<td width=\"15%\" align=\"left\"><font size=\"1\">&nbsp;</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;Punit D'souza</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;30.00</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;23500.00</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;411.00</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;783.33</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;Yes</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr></table>";
		List<Element> supList14 = HTMLWorker.parseToList(new StringReader(tbl14), null);
		Phrase phrase14 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase14.add(supList14.get(0));
		document.add(phrase14);
		
		
		String tbl15="<table width=\"100%\" border=\"1\"><tr><td width=\"4%\" align=\"left\"><font size=\"1\"></font></td>" +
		"<td width=\"15%\" align=\"left\"><font size=\"1\">&nbsp;</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\"><b>&nbsp;Total</b></font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\"><b>&nbsp;30.00</b></font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\"><b>&nbsp;23500.00</b></font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\"><b>&nbsp;411.00</b></font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\"><b>&nbsp;783.33</b></font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr></table>";
		List<Element> supList15 = HTMLWorker.parseToList(new StringReader(tbl15), null);
		Phrase phrase15 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase15.add(supList15.get(0));
		document.add(phrase15);

		String tbl16 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">Date of appintment and leaving the job may be given in remarks column.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"right\"><font size=\"1\">Signature of the Employer</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\"><b>_________________________________________________________________________" +
					"____________________________________________</b></font></td></tr>" +
		"</table>";
		List<Element> supList16 = HTMLWorker.parseToList(new StringReader(tbl16), null);
		Phrase phrase16 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase16.add(supList16.get(0));
		document.add(phrase16);
		
		
		String tbl17 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td align=\"left\"><font size=\"1\">(FOR OFFICIAL USE)</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;1 Entitlement position marked.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;2 Total of Col.5 of Return checked and found correct/incorrect amount is indicated.</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">&nbsp;3 Checked the amount of Employee's/Employer's contribution paid which is in order/observation memo enclosed.</font></td></tr>" +
		"<tr><td align=\"right\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"right\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"right\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">Counter Signature - - - - </font></td></tr>" +
		"<tr><td align=\"right\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"right\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"<tr><td align=\"right\"><font size=\"1\">&nbsp;</font></td></tr>" +
		"</table>";
		List<Element> supList17 = HTMLWorker.parseToList(new StringReader(tbl17), null);
		Phrase phrase17 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase17.add(supList17.get(0));
		document.add(phrase17);
		
		String tbl18 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">U.D.C.<br/>Officer</font></td>" +
				"<td align=\"center\"><font size=\"1\">Head clerk</font></td>" +
				"<td align=\"right\"><font size=\"1\">Branch</font></td></tr>" +
		"</table>";
		List<Element> supList18 = HTMLWorker.parseToList(new StringReader(tbl18), null);
		Phrase phrase18 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase18.add(supList18.get(0));
		document.add(phrase18);
		
		
		
		document.close();
		
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=RegForm5ReturnChallan.pdf");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		buffer.close();
		out.close();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}

	public String getFinancialYear() {
	return financialYear;
}

public void setFinancialYear(String financialYear) {
	this.financialYear = financialYear;
}

	private HttpServletResponse response;
	private HttpServletRequest request;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
		
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	

}
