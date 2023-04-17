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
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;

public class Form6ChallanPdfReports implements ServletRequestAware,ServletResponseAware, IStatements {

	
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	
	public void execute()
	{
		
		/*session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; */
		
//		viewPTaxChallanPdfReports();
		generateForm6ChallanPdfReports();
		return;
		
		
	}
public void generateForm6ChallanPdfReports(){
		
		try {


					
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document, buffer);
		document.open();
				
		String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\"><b>Form No-6</b></font></td></tr>" +
				"<tr><td align=\"center\"><font size=\"1\"><b>(Return of Contribution) Regulation 26</b></font></td></tr>" +
		"<tr><td align=\"center\"><font size=\"1\">Employee State Insurance Corporation</font></td></tr>" +
		"</table>";
		List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
		Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase.add(supList.get(0));
		document.add(phrase);
		
		document.add(new Paragraph(" "));	
		document.add(new Paragraph(" "));
		
	
		
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
		
		document.close();
		
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=Form6ChallanReports.pdf");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		buffer.close();
		out.close();
		
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