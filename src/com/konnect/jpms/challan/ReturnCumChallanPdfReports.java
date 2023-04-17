package com.konnect.jpms.challan;

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
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class ReturnCumChallanPdfReports  extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {

	
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	
	public String execute()
	{
		
		/*session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;*/
		
//		viewPTaxChallanPdfReports();
		generateForm6ChallanPdfReports();
		return "";
		
		
	}
public void generateForm6ChallanPdfReports(){
		
		try {


					
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document, buffer);
		document.open();
			
		String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\"><b>FORM III &nbsp;&nbsp;&nbsp;&nbsp;RETURN-CUM-CHLLAN</b></font></td>" +
		"<td align=\"left\"><font size=\"1\">Profession Tax Registration Certificate No.</b></font></td><td align=\"right\"><font size=\"1\">for the <br/>Profession <br/>Tax Office</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\"></font></td><td align=\"left\"><font size=\"1\"><b>M.V.A.T.R.C.No.,if any</b></font></td><td align=\"center\"><font size=\"1\"></font></td></tr>" +
		"</table>";
		List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
		Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase.add(supList.get(0));
		document.add(phrase);

		document.add(new Paragraph(" "));	

		String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\">THE MAHARASHTRA STATE TAX ON PROFESSIONS, TRADES, CALLINGS</font></td></tr>" +
				"<tr><td align=\"center\"><font size=\"1\">AND EMPLOYMENTS ACT, 1975 AND RULE 11,11-A,11-B,11-C</font></td></tr>" +
		"<tr><td align=\"center\"><font size=\"1\">0028, Other Taxes on Income and Expenditure - Taxes on Professions, Trades, Callings and</font></td></tr>" +
		"<tr><td align=\"center\"><font size=\"1\">Employments - Taxes on Employments</font></td></tr>" +
		"</table>";
		List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
		Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase1.add(supList1.get(0));
		document.add(phrase1);
		
		document.add(new Paragraph(" "));	
		
		String tbl3="<table width=\"100%\" border=\"1\"><tr><td colspan=\"2\" align=\"left\"><font size=\"1\">&nbsp;Employees whose monthly Salaries, Wages are</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;Rates of tax per month</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;No. of Employees</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;Amount of Tax deducted</font></td>" +
		"</tr><tr><td colspan=\"2\" align=\"left\"><font size=\"1\">&nbsp;1 To 2,500</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;0.00</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;0</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;0.00</font></td>" +
		"</tr>" +
		"<tr><td colspan=\"2\" align=\"right\"><font size=\"1\">&nbsp;2,501 To 3,500</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;60.00</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;0</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;0.00</font></td>" +
		"</tr><tr><td colspan=\"2\" align=\"right\"><font size=\"1\">&nbsp;5,501 To 5,000</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;120.00</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;0</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;0.00</font></td>" +
		"</tr><tr><td colspan=\"2\" align=\"right\"><font size=\"1\">&nbsp;5001 To 10,000</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;175.00</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;0</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;0.00</font></td>" +
		"</tr><tr><td colspan=\"2\" align=\"right\"><font size=\"1\">&nbsp;10,001 To 9,999,999</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;200.00</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;1</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;200.00</font></td>" +
		"</tr><tr><td colspan=\"2\" align=\"right\"><font size=\"1\">&nbsp;Arrear Amount</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;0</font></td>" +
		"</tr><tr><td colspan=\"2\" align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"<td align=\"right\"><font size=\"1\"><b>&nbsp;Total Rs.</b></font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"<td align=\"right\"><font size=\"1\"><b>&nbsp;200.00</b></font></td>" +
		"</tr></table>";
		
		List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
		Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase3.add(supList3.get(0));
		document.add(phrase3);
		
		
		String tbl4="<table width=\"100%\" border=\"1\"><tr>" +
		"<td colspan=\"2\" align=\"left\"><font size=\"1\">&nbsp;Tax amount</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr><tr><td colspan=\"2\" align=\"left\"><font size=\"1\">&nbsp;Intrest amount</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr><tr><td colspan=\"2\" align=\"left\"><font size=\"1\">&nbsp;Less-Excess tax paid,if any, in the previous Year/Qtr./Month</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr><tr><td colspan=\"2\" align=\"left\"><font size=\"1\">&nbsp;Net Amount payable</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr><tr><td colspan=\"2\" align=\"left\"><font size=\"1\">&nbsp;Total amount paid(in words) Two hundred only</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr></table>";
		List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
		Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase4.add(supList4.get(0));
		document.add(phrase4);
		
		String tbl5="<table width=\"100%\" border=\"1\"><tr><td colspan=\"2\" align=\"left\"><font size=\"1\">&nbsp;Profession Tax Registration Certificate No.</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;Period From</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;Period To</font></td>" +
		"</tr><tr><td colspan=\"2\" align=\"left\"><font size=\"1\">&nbsp;</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;01/06/2009</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;30/06/2009</font></td>" +
		"</tr></table>";
		List<Element> supList5 = HTMLWorker.parseToList(new StringReader(tbl5), null);
		Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase5.add(supList5.get(0));
		document.add(phrase5);

		
		String tbl6 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">" +
		"<tr><td valign=\"top\"><font size=\"1\">&nbsp;Name and address : " +
		"Paypac,C-22,Kondhwa Road, Kondhwa ,Pune </font></td></tr></table>";
		List<Element> supList6 = HTMLWorker.parseToList(new StringReader(tbl6), null);
		Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase6.add(supList6.get(0));
		document.add(phrase6);
		
		String tbl7 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td align=\"center\"><font size=\"1\">The above statements are true to the best of my knowledge and belief</font></td></tr></table>";
		List<Element> supList7 = HTMLWorker.parseToList(new StringReader(tbl7), null);
		Phrase phrase7 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase7.add(supList7.get(0));
		document.add(phrase7);
		
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
		String tbl8 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"left\"><font size=\"1\">Date:</font></td>" +
				"<td align=\"left\"><font size=\"1\"></font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">Place:</font></td>" +
		"<td align=\"center\"><font size=\"1\">Signature & &nbsp;Designation</font></td></tr>" +
		"</table>";
		List<Element> supList8 = HTMLWorker.parseToList(new StringReader(tbl8), null);
		Phrase phrase8 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase8.add(supList8.get(0));
		document.add(phrase8);
		document.add(new Paragraph(" "));
		String tbl9 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td align=\"center\"><font size=\"1\">For the Treasury Use Only</font></td></tr></table>";
		List<Element> supList9 = HTMLWorker.parseToList(new StringReader(tbl9), null);
		Phrase phrase9 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase9.add(supList9.get(0));
		document.add(phrase9);
		
		String tbl10="<table width=\"100%\" border=\"1\"><tr><td colspan=\"2\" align=\"center\"><font size=\"1\">&nbsp;Received Rs.(in Words)</font></td>" +
		"<td align=\"center\"><font size=\"1\">&nbsp;Rupees(in Figures)</font></td>" +
		"</tr><tr><td colspan=\"2\" align=\"left\"><font size=\"1\">&nbsp;</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr><tr><td colspan=\"2\" align=\"left\"><font size=\"1\">&nbsp;Date of Entry</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;Challan No</font></td>" +
		"</tr></table>";
		List<Element> supList10 = HTMLWorker.parseToList(new StringReader(tbl10), null);
		Phrase phrase10 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase10.add(supList10.get(0));
		document.add(phrase10);
		
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
		
		String tbl11 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td align=\"left\"><font size=\"1\">Treasurer</font></td>" +
		"<td align=\"center\"><font size=\"1\">Accountanat</font></td>" +
		"<td align=\"right\"><font size=\"1\">Treasury Officer/Agent or Manager</font></td>" +
		"</tr></table>";
		List<Element> supList11 = HTMLWorker.parseToList(new StringReader(tbl11), null);
		Phrase phrase11 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase11.add(supList11.get(0));
		document.add(phrase11);
		
		document.newPage();
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
		String tbl12="<table width=\"100%\" border=\"1\"><tr><td align=\"left\"><font size=\"1\">&nbsp;Profession Tax Registration Certificate No.</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;Profession Tax Registration Certificate No.</font></td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\"><b>&nbsp;M.V.A.T.R.C.No., if any</b></font></td>" +
		"<td align=\"right\"><font size=\"1\"><b>&nbsp;M.V.A.T.R.C.No., if any</b></font></td>" +
		"</tr><tr><td align=\"center\"><font size=\"1\"><b>&nbsp;Part II</b></font></td>" +
		"<td align=\"center\"><font size=\"1\"><b>&nbsp;Part III</b></font></td>" +
		"</tr><tr><td  align=\"center\"><font size=\"1\"><b>&nbsp;FORM II&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;RETURN-CUM-CHALLAN&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;For the Tresurt</b></font></td>" +
		"<td align=\"center\"><font size=\"1\"><b>&nbsp;FORM II&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;RETURN-CUM-CHALLAN&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;For the Tresurt</b></font></td>" +
		"</tr>" +
		"<tr><td align=\"center\"><font size=\"0.5\">THE MAHARSHTRA STATE TAX ON PROFESSIONS,TRADES,CALLINGS<br/>AND EMPLOYMENTS ACT,1975 AND RULE 11,11-A,11-B,11-C<br/>0028,Other Taxes on Income and Expenditure -Taxes on<br/>Professions, Traders,Callings and <br/>Employments - Taxes on Employments</font></td>" +
		"<td align=\"center\"><font size=\"0.5\">THE MAHARSHTRA STATE TAX ON PROFESSIONS,TRADES,CALLINGS<br/>AND EMPLOYMENTS ACT,1975 AND RULE 11,11-A,11-B,11-C<br/>0028,Other Taxes on Income and Expenditure -Taxes on<br/>Professions, Traders,Callings and <br/>Employments - Taxes on Employments</font></td>" +
		"</tr>" +
		"</table>";
		List<Element> supList12 = HTMLWorker.parseToList(new StringReader(tbl12), null);
		Phrase phrase12 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase12.add(supList12.get(0));
		document.add(phrase12);
		
		
		
		String tbl13="<table width=\"100%\" border=\"0\"><tr><td>" +
		"<table width=\"100%\" border=\"1\"><tr>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;Tax amount</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;200</font></td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\">&nbsp;Intrest amount</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\">&nbsp;Less-Excess tax paid,if any, in the previous Year/Qtr./Month</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\">&nbsp;Net Amount payable</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr></table>" +
		"</td>" +
		"<td><table width=\"100%\" border=\"1\"><tr>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;Tax amount</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;200</font></td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\">&nbsp;Intrest amount</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\">&nbsp;Less-Excess tax paid,if any, in the previous Year/Qtr./Month</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\">&nbsp;Net Amount payable</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr></table></td>" +
		"</tr></table>";
		List<Element> supList13 = HTMLWorker.parseToList(new StringReader(tbl13), null);
		Phrase phrase13 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase13.add(supList13.get(0));
		document.add(phrase13);
		
		String tbl14="<table width=\"100%\" border=\"0\"><tr><td>" +
		"<table width=\"100%\" border=\"1\"><tr><td align=\"left\"><font size=\"1\">&nbsp;Profession Tax Registration Certificate No.</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;Period From</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;Period To</font></td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;01/06/2009</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;30/06/2009</font></td>" +
		"</tr></table>" +
		"</td>" +
		"<td>" +
		"<table width=\"100%\" border=\"1\"><tr><td align=\"left\"><font size=\"1\">&nbsp;Profession Tax Registration Certificate No.</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;Period From</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;Period To</font></td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;01/06/2009</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;30/06/2009</font></td>" +
		"</tr></table>" +
		"</td>" +
		"</tr></table>";
		List<Element> supList14 = HTMLWorker.parseToList(new StringReader(tbl14), null);
		Phrase phrase14 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase14.add(supList14.get(0));
		document.add(phrase14);
		
		String tbl15="<table width=\"100%\" border=\"0\"><tr><td>" +
		"<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">" +
		"<tr><td valign=\"top\"><font size=\"1\">&nbsp;Name and address : " +
		"Paypac,C-22,Kondhwa Road, Kondhwa ,Pune </font></td></tr></table>" +
		"</td>" +
		"<td>" +
		"<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">" +
		"<tr><td valign=\"top\"><font size=\"1\">&nbsp;Name and address : " +
		"Paypac,C-22,Kondhwa Road, Kondhwa ,Pune </font></td></tr></table>" +
		"</td>" +
		"</tr></table>";
		List<Element> supList15 = HTMLWorker.parseToList(new StringReader(tbl15), null);
		Phrase phrase15 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase15.add(supList15.get(0));
		document.add(phrase15);
		
		
		
		String tbl16="<table width=\"100%\" border=\"0\"><tr><td>" +
		"<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td align=\"center\"><font size=\"1\">The above statements are true to the best of my knowledge and belief</font></td></tr></table>" +
		"</td>" +
		"<td>" +
		"<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td align=\"center\"><font size=\"1\">The above statements are true to the best of my knowledge and belief</font></td></tr></table>" +
		"</td>" +
		"</tr></table>";
		List<Element> supList16 = HTMLWorker.parseToList(new StringReader(tbl16), null);
		Phrase phrase16 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase16.add(supList16.get(0));
		document.add(phrase16);
		
		String tbl17="<table width=\"100%\" border=\"1\"><tr><td>" +
		"<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"left\"><font size=\"1\">Date:</font></td>" +
				"<td align=\"left\"><font size=\"1\"></font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">Place:</font></td>" +
		"<td align=\"center\"><font size=\"1\">Signature & &nbsp;Designation</font></td></tr>" +
		"</table>" +
		"</td>" +
		"<td>" +
		"<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"left\"><font size=\"1\">Date:</font></td>" +
				"<td align=\"left\"><font size=\"1\"></font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">Place:</font></td>" +
		"<td align=\"center\"><font size=\"1\">Signature & &nbsp;Designation</font></td></tr>" +
		"</table>" +
		"</td>" +
		"</tr></table>";
		List<Element> supList17 = HTMLWorker.parseToList(new StringReader(tbl17), null);
		Phrase phrase17 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase17.add(supList17.get(0));
		document.add(phrase17);
		
		String tbl18="<table width=\"100%\" border=\"0\"><tr><td>" +
		"&nbsp;" +
		"</td>" +
		"<td>" +
		"&nbsp;" +
		"</td>" +
		"</tr><tr><td align=\"center\">" +
		"<font size=\"1\">&nbsp;For the Treasury Use Only" +
		"</font></td>" +
		"<td align=\"center\">" +
		"<font size=\"1\">&nbsp;For the Treasury Use Only" +
		"</font></td>" +
		"</tr></table>";
		List<Element> supList18 = HTMLWorker.parseToList(new StringReader(tbl18), null);
		Phrase phrase18 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase18.add(supList18.get(0));
		document.add(phrase18);
		
		
		String tbl19="<table width=\"100%\" border=\"0\"><tr><td>" +
		"<table width=\"100%\" border=\"1\"><tr><td align=\"center\"><font size=\"1\">&nbsp;Received Rs.(in Words)</font></td>" +
		"<td align=\"center\"><font size=\"1\">&nbsp;Rupees(in Figures)</font></td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\">&nbsp;Date of Entry</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;Challan No</font></td>" +
		"</tr></table>" +
		"</td>" +
		"<td>" +
		"<table width=\"100%\" border=\"1\"><tr><td align=\"center\"><font size=\"1\">&nbsp;Received Rs.(in Words)</font></td>" +
		"<td align=\"center\"><font size=\"1\">&nbsp;Rupees(in Figures)</font></td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\">&nbsp;Date of Entry</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;Challan No</font></td>" +
		"</tr></table>" +
		"</td>" +
		"</tr></table>";
		List<Element> supList19 = HTMLWorker.parseToList(new StringReader(tbl19), null);
		Phrase phrase19 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase19.add(supList19.get(0));
		document.add(phrase19);
		
		
		String tbl20="<table width=\"100%\" border=\"1\">" +
				"<tr><td>" +
		"<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td>" +
		"&nbsp;" +
		"</td>" +
		"<td>" +
		"&nbsp;" +
		"</td><td>" +
		"&nbsp;" +
		"</td>" +
		"</tr><tr><td>" +
		"&nbsp;" +
		"</td>" +
		"<td>" +
		"&nbsp;" +
		"</td><td>" +
		"&nbsp;" +
		"</td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\">Treasurer</font></td>" +
		"<td align=\"center\"><font size=\"1\">Accountanat</font></td>" +
		"<td align=\"right\"><font size=\"1\">Treasury Officer/Agent or Manager</font></td>" +
		"</tr></table>" +
		"</td>" +
		"<td>" +
		"<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td>" +
		"&nbsp;" +
		"</td>" +
		"<td>" +
		"&nbsp;" +
		"</td><td>" +
		"&nbsp;" +
		"</td>" +
		"</tr><tr><td>" +
		"&nbsp;" +
		"</td>" +
		"<td>" +
		"&nbsp;" +
		"</td><td>" +
		"&nbsp;" +
		"</td>" +
		"</tr><tr><td align=\"left\"><font size=\"1\">Treasurer</font></td>" +
		"<td align=\"center\"><font size=\"1\">Accountanat</font></td>" +
		"<td align=\"right\"><font size=\"1\">Treasury Officer/Agent or Manager</font></td>" +
		"</tr></table>" +
		"</td>" +
		"</tr></table>";
		List<Element> supList20 = HTMLWorker.parseToList(new StringReader(tbl20), null);
		Phrase phrase20 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase20.add(supList20.get(0));
		document.add(phrase20);
		
		
		/*String tbl15="<table width=\"100%\" border=\"0\"><tr><td>" +
		"" +
		"</td>" +
		"<td>" +
		"" +
		"</td>" +
		"</tr></table>";*/
		/*
		String tbl13="<table width=\"100%\" border=\"1\"><tr><td align=\"left\"><font size=\"1\">&nbsp;1</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"<td colspan=\"2\" align=\"left\"><font size=\"1\">&nbsp;Punit D'souza</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;30.00</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;23500.00</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;411.00</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;783.33</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr></table>";
		
		List<Element> supList13 = HTMLWorker.parseToList(new StringReader(tbl13), null);
		Phrase phrase13 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase13.add(supList13.get(0));
		document.add(phrase13);
		*/
		
		
		/*
		String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td align=\"left\"><font size=\"1\">Name of Local Office :...................</font></td><td></td><td align=\"left\"><font size=\"1\">Employer's Code No. 258746 </font></td></tr>" +
		"</table>";
		List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
		Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase3.add(supList3.get(0));
		document.add(phrase3);
		
		document.add(new Paragraph(" "));
		
		String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td valign=\"top\"><font size=\"1\">Name and address of the Factory or Establishment :</font></td><td><table border=\"0\">" +
		"<tr><td><font size=\"1\">Paypac</font></td></tr>" +
		"<tr><td><font size=\"1\">C-22,Kondhwa Road, Kondhwa ,Pune </font></td></tr></table></td><td><table border=\"0\"><tr><td><font size=\"1\">Contribution Period</font></td></tr>" +
		"<tr><td><font size=\"1\">From :April 2009</font></td></tr><tr><td><font size=\"1\">To : September 2009 </font></td></tr></table></td></tr></table>";
		List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
		Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase2.add(supList2.get(0));
		document.add(phrase2);
		
		document.add(new Paragraph(" "));
		
		String tbl4 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">PARTICULARS OF THE PRINCIPAL EMPLOYERS :-</font></td></tr>" +
		"</table>";
		List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
		Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase4.add(supList4.get(0));
		document.add(phrase4);
		
		String tbl5 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">a) Name :------------------------------------------</font></td><td align=\"left\"><font size=\"1\">b) Desgnation :-----------------------</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">c) Residential Address :-----------------------------------------</font></td><td align=\"right\"><font size=\"1\"></font></td></tr>" +
		"</table>";
		List<Element> supList5 = HTMLWorker.parseToList(new StringReader(tbl5), null);
		Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase5.add(supList5.get(0));
		document.add(phrase5);

		document.add(new Paragraph(" "));
		
		String tbl6 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;I Furnist Below the Details of the employer's share of contribution in respect of the undermentioned Insured Person.</font></td></tr>" +
				"<td align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;</font></td></tr>" +
				"<td align=\"left\"><font size=\"1\">I hearby declare That the return includes every employee, employed directly or through an immediate employer or in connection with the </font></td></tr>" +
				"<td align=\"left\"><font size=\"1\">work of the factory/establishment or any work connected with the administration of the factory/establishment or purchase of raw  </font></td></tr>" +
				"<td align=\"left\"><font size=\"1\">materials, sales or distribution of finished product etc., to thom the contributionperiod to which this return relates, applies and that the </font></td></tr>" +
				"<td align=\"left\"><font size=\"1\">contribution in report of employer's and employee's share have been correctly paid in accordance with the prevision of Act and </font></td></tr>" +
				"<td align=\"left\"><font size=\"1\">Regulation relating to the payment of contribution vide challans detailed below :-</font></td></tr>" +
				"<td align=\"left\"><font size=\"1\">Total Contribution amounting to Rs..........5882  Comparising of Rs.....4299   Employer's share and Rs.........1583  as Employee's share</font></td></tr>" +
				"<td align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;</font></td></tr>" +
				"<td align=\"left\"><font size=\"1\">(Total of cpl6 of the return) paid as under :</font></td></tr>" +
		"</table>";
		List<Element> supList6 = HTMLWorker.parseToList(new StringReader(tbl6), null);
		Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase6.add(supList6.get(0));
		document.add(phrase6);
		
		document.add(new Paragraph(" "));
		
		String tbl7 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td  align=\"left\"><font size=\"1\">1) Challan Dated&nbsp;&nbsp;&nbsp;&nbsp;</font></td><td  align=\"center\"><font size=\"1\">for Rs.</font></td><td align=\"right\"><font size=\"1\">0.00&nbsp;&nbsp;</font></td></tr>" +
		"<tr><td  align=\"left\"><font size=\"1\">2) Challan Dated&nbsp;&nbsp;&nbsp;&nbsp;</font></td><td  align=\"center\"><font size=\"1\">for Rs.</font></td><td align=\"right\"><font size=\"1\">0.00&nbsp;&nbsp;</font></td></tr>" +
		"<tr><td  align=\"left\"><font size=\"1\">3) Challan Dated&nbsp;&nbsp;&nbsp;&nbsp;</font></td><td  align=\"center\"><font size=\"1\">for Rs.</font></td><td align=\"right\"><font size=\"1\">5,882.00&nbsp;&nbsp;</font></td></tr>" +
		"<tr><td  align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;</font></td><td  align=\"center\"><font size=\"1\"><b>Total Rs.</b></font></td><td align=\"right\"><font size=\"1\"><b>5,882.00&nbsp;&nbsp;<b></font></td></tr>" +
		"</table>";
		List<Element> supList7 = HTMLWorker.parseToList(new StringReader(tbl7), null);
		Phrase phrase7 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase7.add(supList7.get(0));
		document.add(phrase7);
		
		document.add(new Paragraph(" "));
		
		String tbl8 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"left\"><font size=\"1\">Place </font></td>" +
				"<td align=\"left\"><font size=\"1\">Signature :</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">Date:</font></td>" +
		"<td align=\"left\"><font size=\"1\">Designation</font></td></tr>" +
		"</table>";
		List<Element> supList8 = HTMLWorker.parseToList(new StringReader(tbl8), null);
		Phrase phrase8 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase8.add(supList8.get(0));
		document.add(phrase8);
		
		document.add(new Paragraph(" "));
		
		String tbl9 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">Important Instructions </font></td></tr>" +
		"</table>";
		List<Element> supList9 = HTMLWorker.parseToList(new StringReader(tbl9), null);
		Phrase phrase9 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase9.add(supList9.get(0));
		document.add(phrase9);
		
		String tbl10 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td width=\"60%\"><font size=\"1\">1.&nbsp;&nbsp;If any 1.P is appointed for the first time and/or leaves service during contribution period, indicate \"A......(Date</font></td></tr>" +
		"<tr><td><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;and/or \"I......(Dte)\",in the remark ccolumn (No.8)</font></td><td><font size=\"1\"></font></td><td></td></tr>" +
		"<tr><td><font size=\"1\">2.&nbsp;&nbsp;Please indicate Insurance Numbers in chronological (ascending) order</font></td></tr>" +
		"<tr><td><font size=\"1\">3.&nbsp;&nbsp;Figure in columns of 4,5 & 6 of the return. </font></td></tr>"+
		"<tr><td><font size=\"1\">4.&nbsp;&nbsp;Invariably strike totals of Columns of 4,5 & 6 of the return </font></td></tr>"+
		"<tr><td><font size=\"1\">5.&nbsp;&nbsp;No over writing shall be made Any correvtions should be signed by the employer </font></td></tr>"+
		"<tr><td><font size=\"1\">6.&nbsp;&nbsp;Every page of this return should bear full signature & rubber stamp of the employer.</font></td></tr>"+
		"<tr><td><font size=\"1\">7.&nbsp;&nbsp;\"Daily Wages\" in Col.7 of the return shall be calculated by dividing figures in col. 5 by figures in Col. 4 to two decimal places. </font></td></tr>"+
		"</table>";
		List<Element> supList10 = HTMLWorker.parseToList(new StringReader(tbl10), null);
		Phrase phrase10 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase10.add(supList10.get(0));
		document.add(phrase10);
		
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
		String tbl11 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"right\"><font size=\"1\">Page No. &nbsp;&nbsp;&nbsp;&nbsp;1</font></td></tr>" +
		"</table>";
		List<Element> supList11 = HTMLWorker.parseToList(new StringReader(tbl11), null);
		Phrase phrase11 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase11.add(supList11.get(0));
		document.add(phrase11);
		
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));

		
		String tbl15 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\">Form No. 6 Cont... &nbsp;&nbsp;&nbsp;&nbsp;</font></td></tr>" +
		"</table>";
		List<Element> supList15 = HTMLWorker.parseToList(new StringReader(tbl15), null);
		Phrase phrase15 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase15.add(supList15.get(0));
		document.add(phrase15);
		
		document.add(new Paragraph(" "));
		
		String tbl12="<table width=\"100%\" border=\"1\"><tr><td align=\"left\"><font size=\"1\">&nbsp;Sr.No.<br/>1</font></td>" +
		"<td align=\"left\"><font size=\"1\">&nbsp;Insurance Number<br/>2</font></td>" +
		"<td colspan=\"2\" align=\"left\"><font size=\"1\">&nbsp;Name of insured person<br/>3</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;No of Days for which wages<br/>4</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;Total amount of wages paid<br/>5</font></td>" +
		"<td align=\"center\"><font size=\"1\">&nbsp;Employee's contribution deducted<br/>6</font></td>" +
		"<td align=\"center\"><font size=\"1\">&nbsp;Average Daily's Wages 5/4<br/>7</font></td>" +
		"<td align=\"center\"><font size=\"1\">&nbsp;Dispensary<br/>7(A)</font></td>" +
		"<td align=\"center\"><font size=\"1\">&nbsp;Remarks<br/>9</font></td>" +
		"</tr></table>";
		
		List<Element> supList12 = HTMLWorker.parseToList(new StringReader(tbl12), null);
		Phrase phrase12 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase12.add(supList12.get(0));
		document.add(phrase12);
		
		
		String tbl13="<table width=\"100%\" border=\"1\"><tr><td align=\"left\"><font size=\"1\">&nbsp;1</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"<td colspan=\"2\" align=\"left\"><font size=\"1\">&nbsp;Punit D'souza</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;30.00</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;23500.00</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;411.00</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;783.33</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr></table>";
		
		List<Element> supList13 = HTMLWorker.parseToList(new StringReader(tbl13), null);
		Phrase phrase13 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase13.add(supList13.get(0));
		document.add(phrase13);
		
				
		String tbl14 = "<table width=\"100%\" border=\"1\"><tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"<td colspan=\"2\" align=\"left\"><font size=\"1\"><b>&nbsp;Total</b></font></td>" +
		"<td align=\"right\"><font size=\"1\"><b>&nbsp;30.00</b></font></td>" +
		"<td align=\"right\"><font size=\"1\"><b>&nbsp;23500.00</b></font></td>" +
		"<td align=\"right\"><font size=\"1\"><b>&nbsp;411.00</b></font></td>" +
		"<td align=\"right\"><font size=\"1\"><b>&nbsp;783.33</b></font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"<td align=\"right\"><font size=\"1\">&nbsp;</font></td>" +
		"</tr></table>";
		List<Element> supList14 = HTMLWorker.parseToList(new StringReader(tbl14), null);
		Phrase phrase14 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase14.add(supList14.get(0));
		document.add(phrase14);
		*/
		document.close();
		
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=ReturnCumChallanReports.pdf");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
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