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

public class ESICChallanPdfReports implements ServletRequestAware,ServletResponseAware, IStatements {

	
	
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
		generateESICChallanPdfReports();
		return;
		
		
	}

	public void generateESICChallanPdfReports(){
			
		try {
				
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document();
			PdfWriter.getInstance(document, buffer);
			document.open();
					
			String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\"><b>E.S.I.C.</b></font></td></tr>" +
					"<tr><td align=\"right\"><font size=\"1\">CHALLAN NO.</font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\"><b>EMPLOYEES STATE INSURANCE FUND ACCOUNT NO:-1</b></font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\"><b>PAY-IN-SLIP FOR CONTRIBUTION</b></font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\"><b>STATE BANK OF INDIA</b></font></td></tr>" +			
			"</table>";
			List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);
			
			document.add(new Paragraph(" "));
			
			String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">Station</font></td>" +
					"<td align=\"right\"><font size=\"1\">Date &nbsp;&nbsp;&nbsp;03/06/2009</font></td></tr>" +
			"</table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);
			
			document.add(new Paragraph(" "));
			String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
			"<tr><td><table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">" +
			"<tr><td><font size=\"1\">&nbsp;&nbsp;PARTICULARS OF CASH / &nbsp;CHEQUE NO.</font></td><td><font size=\"1\">&nbsp;Amounts in Rs.</font></td></tr>" +
			"<tr><td valign=\"top\"><font size=\"1\">&nbsp;215747</font></td><td  align=\"right\"><font size=\"1\">5882&nbsp;</font></td></tr>" +
			"<tr><td><font size=\"1\">&nbsp;Total :</font></td><td align=\"right\"><font size=\"1\">5882&nbsp;</font></td></tr>" +
			"</table></td><td><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
			"<tr><td><font size=\"1\">&nbsp;&nbsp;Paid in to the credit of the Employees' State Insurance Fund <br/>&nbsp;Account No.1 Rs.5,882.00</font></td></tr>" +
			"<tr><td valign=\"top\"><font size=\"1\">&nbsp;(Rupees. Five Thousand Eight Hundred Eighty Two Only)</font></td></tr>" +
			"<tr><td><font size=\"1\">&nbsp;in Cash /by Cheque (on realization) for payment of contribution as per<br/>&nbsp; details given below under the Employee's State Insurance Act,1948 for &nbsp;the month of June-2009</font></td></tr>" +
			"</table></td></tr></table>";
			List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
			Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase2.add(supList2.get(0));
			document.add(phrase2);
			document.add(new Paragraph(" "));
			String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">Employer's Code No.&nbsp;&nbsp;&nbsp;258746</font></td>" +
			"<td align=\"left\"><font size=\"1\">DEPOSITED BY : &nbsp;&nbsp;&nbsp;CHEQUE</font></td></tr>" +
			"</table>";
			List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
			Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase3.add(supList3.get(0));
			document.add(phrase3);
			document.add(new Paragraph(" "));
			String tbl4 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
			"<tr><td><font size=\"1\">&nbsp;&nbsp;Name and address of Factory/Establishment- :</font></td><td><font size=\"1\">"+CF.getStrOrgName()+"</font></td><td></td></tr>" +
			"<tr><td valign=\"top\"><font size=\"1\">&nbsp;&nbsp;</font></td><td><font size=\"1\">"+CF.getStrOrgAddress()+"</font></td><td></td></tr>" +
			"</table>";
			List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
			Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase4.add(supList4.get(0));
			document.add(phrase4);
			document.add(new Paragraph(" "));
			
			String tbl5 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">No. of Employees :-&nbsp;&nbsp;&nbsp;5</font></td>" +
			"<td align=\"left\"><font size=\"1\">Total Wages :&nbsp;&nbsp;&nbsp;90500.00</font></td></tr>" +
			"</table>";
			List<Element> supList5 = HTMLWorker.parseToList(new StringReader(tbl5), null);
			Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase5.add(supList5.get(0));
			document.add(phrase5);
			
			document.add(new Paragraph(" "));
			
			String tbl6 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
			"<tr><td align=\"left\"><font size=\"1\">Employees Contribution Rs.&nbsp;&nbsp;&nbsp;5</font></td>" +
			"<td align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;1583</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">Employers Contribution Rs.&nbsp;&nbsp;&nbsp;5</font></td>" +
			"<td align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;4299</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\"><b>Total.&nbsp;&nbsp;&nbsp;@6.5% :-</b></font></td>" +
			"<td align=\"left\"><font size=\"1\"><b>&nbsp;&nbsp;&nbsp;5882</b></font></td></tr>" +
			"</table>";
			List<Element> supList6 = HTMLWorker.parseToList(new StringReader(tbl6), null);
			Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase6.add(supList6.get(0));
			document.add(phrase6);
			document.add(new Paragraph(" "));
			
			
			
			String tbl16 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"+
			"<tr><td align=\"left\"><font size=\"1\"><b>_________________________________________________________________________" +
			"____________________________________________</b></font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">(For Use in Bank)</font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\"><b>&nbsp;A C K N O W L E D G E M E N T</b></font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\">(To be filled by depositor)</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">Received Payment with Cash/Cheque/Draft No.215747</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">dated 01/07/2009 for Rs.5,882.00</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">(Rupees. Five Thousand Eight Hundred Eighty Two Only)</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">Drawn on HDFC(Bank) in favour of Employees' State Insurance Fund Account No.1</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">Sl. No. in Bank's Scroll</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">Dated:-&nbsp;&nbsp;&nbsp;30/06/2009</font></td></tr>" +
			"<tr><td align=\"right\"><font size=\"1\">_____________________________________</font></td></tr>" +
			"<tr><td align=\"right\"><font size=\"1\">Authorised Signature of the receiving Bank</font></td></tr>" +
			"</table>";
			List<Element> supList16 = HTMLWorker.parseToList(new StringReader(tbl16), null);
			Phrase phrase16 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase16.add(supList16.get(0));
			document.add(phrase16);
			
			
			
			document.close();
			
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=ESICChallanReports.pdf");
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
