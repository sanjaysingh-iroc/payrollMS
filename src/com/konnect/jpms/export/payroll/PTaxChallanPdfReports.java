package com.konnect.jpms.export.payroll;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class PTaxChallanPdfReports  implements ServletRequestAware,ServletResponseAware, IStatements {

	
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	String financialYear;
	List<FillMonth> monthList;
	String strFinancialYearStart;
	String strFinancialYearEnd;
	double paidamount;
	double totalamount;
	String sessionEmpId;
	public void execute()
	{
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return;
		sessionEmpId=(String) session.getAttribute(EMPID);
		monthList = new FillMonth().fillMonth();
		viewForm5PTChallanData();
		generatePTaxChallanPdfReports();
		return;
		
		
	}
	
	public void viewForm5PTChallanData()
	{
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
		
		
		String payMonts="";
		
		
		Map<Integer,Map<String,String>> hmMap = new HashMap<Integer,Map<String,String>>();
		int payYear=0;
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

			pst = con.prepareStatement("select distinct(month) from challan_details where"
							+ " financial_year_from_date=? and financial_year_to_date=? and challan_type=? and is_paid=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,PROFESSIONAL_TAX);
			pst.setBoolean(4, true);
			rs = pst.executeQuery();
			List mList = new ArrayList();
			while (rs.next()) {
				String[] monthsArray = rs.getString("month").split(",");
				for (int i = 0; i < monthsArray.length; i++) {
					if (!mList.contains(monthsArray[i])) {
						mList.add(monthsArray[i]);
					}
				}
			}
			rs.close();
			pst.close();

			
			String monthStr = "";
			for (int i = 1; i < mList.size(); i++) {
				if (i == mList.size() - 1) {
					monthStr += mList.get(i);
				} else
					monthStr += mList.get(i) + ",";
			}		
		
			
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM")));
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
			List alMonth = new ArrayList();
			
			for(int i=0; i<12; i++){
				alMonth.add(cal.get(Calendar.MONTH)+"");
				
				cal.add(Calendar.MONTH, 1);
			}
			
			
			
		List<Integer> monthList=new ArrayList<Integer>();
		for(int i=0; i<alMonth.size(); i++){
			
			monthList.add(uF.parseToInt(uF.getDateFormat((String)alMonth.get(i),"MM","MM")+""));
		}
		
		
		String paidDate="";
		StringBuilder sb = new StringBuilder();		
		sb.append("select distinct(paid_date) as paid_date,sum(amount) as amount from challan_details where financial_year_from_date=?"
						+ " and financial_year_to_date=? and challan_type=? and is_paid=?");
		for (int i = 0; i < alMonth.size(); i++) {
			if (i == 0) {
				sb.append(" and (");
			} else {
				sb.append(" OR");
			}
			sb.append(" month like '%," + uF.parseToInt(uF.getDateFormat((String)alMonth.get(i),"MM","MM")+"") + ",%'");
			if (i == alMonth.size() - 1) {
				sb.append(")");
			}
		}
		sb.append(" group by paid_date, challan_no");
		pst = con.prepareStatement(sb.toString());
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, PROFESSIONAL_TAX);
		pst.setBoolean(4, true);
		rs = pst.executeQuery();

		while (rs.next()) {
			paidamount += rs.getDouble("amount");
			paidDate=rs.getString("paid_date");
		}
		rs.close();
		pst.close();
		
		 int paidMonth=uF.parseToInt(uF.getDateFormat(paidDate + "", DBDATE,"M"));
		 double balancedAmount=0.0;
		for(int i=0;i<monthList.size();i++){
			
			pst = con.prepareStatement("select sum(amount) as total from payroll_generation where financial_year_from_date=?" +
					" and financial_year_to_date=? and month=? and salary_head_id=?");
															
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,monthList.get(i));
			pst.setInt(4,PROFESSIONAL_TAX);
			
			rs = pst.executeQuery();
			
			while(rs.next()){
				Map<String,String> hmInner = new HashMap<String,String>();
				 hmInner.put("TAXDEDUCTED",uF.formatIntoComma(rs.getDouble("total")));
				 totalamount+=rs.getInt("total");
				 
				 if(monthList.get(i)!=paidMonth){
					 balancedAmount+=rs.getInt("total");
					 hmInner.put("TAXPAID","0");
					 hmInner.put("TAXBALANCED", uF.formatIntoComma(balancedAmount));
					
				 }else{
					 balancedAmount+=rs.getInt("total");
					 hmInner.put("TAXPAID", uF.formatIntoComma(paidamount));
					 hmInner.put("TAXBALANCED", uF.formatIntoComma(balancedAmount-paidamount));
					 balancedAmount=0;
				 }
				
				 hmMap.put(monthList.get(i), hmInner);
				
			}
			rs.close();
			pst.close();
		
		}
		
		
		
//		request.setAttribute("totalamount",totalamount);
		request.setAttribute("monthList",monthList);
		request.setAttribute("hmMap",hmMap);
		
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
public void generatePTaxChallanPdfReports(){
	Connection con = null;
	Database db = new Database();
	db.setRequest(request);
		try {
			
			con = db.makeConnection(con);

			UtilityFunctions uF = new UtilityFunctions();

			Map<Integer,Map<String,String>> hmMap = (Map<Integer,Map<String,String>>)request.getAttribute("hmMap");
			List<Integer> monthList=(List<Integer>)request.getAttribute("monthList");
//			double totalamount=(Integer)request.getAttribute("totalamount");
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con,null,null);
			Map<String, String> hmEmpCodeDesig =CF.getEmpDesigMap(con);
			
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document, buffer);
		document.open();
				
		String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\"><b>Form 5</b></font></td></tr>" +
				"<tr><td align=\"center\"><font size=\"1\">(See Rule 11)</font></td></tr>" +
		"<tr><td align=\"center\"><font size=\"1\"><b>Professional Tax Annual Returns</b></font></td></tr>" +
		"</table>";
		List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
		Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase.add(supList.get(0));
		document.add(phrase);
		
		document.add(new Paragraph(" "));
		
		String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">Returns of tax payable by employer under sub-section (1) Section 6 of Karnataka Tax on Professions,</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">Trades, Cailing Employement Act , 1976.</font></td></tr>" +
		"</table>";
		List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
		Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase1.add(supList1.get(0));
		document.add(phrase1);
		
		
		String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td width=\"60%\"><font size=\"1\">1.&nbsp;&nbsp;Return of Tax payable for the year ending on:</font></td>" +
		"<td width=\"40%\"><font size=\"1\">&nbsp;<b>"+uF.getDateFormat(strFinancialYearEnd,DATE_FORMAT,CF.getStrReportDateFormat())+"</b></font></td><td></td></tr>" +
		"<tr><td><font size=\"1\">2.&nbsp;&nbsp;Name of the Employer :</font></td><td><font size=\"1\"><b>"+CF.getStrOrgName()+"</b></font></td><td></td></tr>" +
		"<tr><td valign=\"top\"><font size=\"1\">3.&nbsp;&nbsp;Address : </font></td><td><font size=\"1\"><b>"+CF.getStrOrgAddress()+"</b></font></td><td></td></tr>" +
		"<tr><td><font size=\"1\">4.&nbsp;&nbsp;Registration Certificate No :</font></td><td><font size=\"1\"><b></b></font></td><td></td></tr>" +
		"<tr><td><font size=\"1\">5.&nbsp;&nbsp;Tax paid during the year is as under :  </font></td><td><font size=\"1\"><b></b></font></td><td></td></tr>"+		
		"</table>";
		List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
		Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase2.add(supList2.get(0));
		document.add(phrase2);

		document.add(new Paragraph(" "));
		
		String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
			+ "<td align=\"center\"><font size=\"1\"><b>_________________________________________________________________________" +
					"____________________________________________</b></font></td>"
			+ "</tr></table>";
		List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
		Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase3.add(supList3.get(0));
		document.add(phrase3);
	
		String tbl4="<table width=\"100%\" border=\"0\"><tr><td width=\"4%\" align=\"left\"><font size=\"1\">&nbsp;Sr.No.<br/>1</font></td>" +
		"<td width=\"15%\" align=\"left\"><font size=\"1\">&nbsp;Month<br/>2</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;Tax Deducted<br/>3</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;Tax Paid<br/>4</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;Balance Tax<br/>5</font></td>" +
		"<td width=\"36%\" colspan=\"2\" align=\"center\"><font size=\"1\">&nbsp;Paid under Challan No. & Date<br/>6</font></td>" +
		"</tr></table>";
		
		List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
		Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase4.add(supList4.get(0));
		document.add(phrase4);
		
		document.add(phrase3);
		
		
		if(monthList!=null){
			for(int i=0;i<monthList.size();i++)
			{
				Map<String,String> hmInner =hmMap.get(monthList.get(i));
		
		String tbl5="<table width=\"100%\" border=\"0\"><tr><td width=\"4%\" align=\"left\"><font size=\"1\">&nbsp;"+(i+1)+"</font></td>" +
		"<td width=\"15%\" align=\"left\"><font size=\"1\">&nbsp;"+uF.getDateFormat(monthList.get(i).toString(),"MM","MMM")+"</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;"+uF.showData((String)hmInner.get("TAXDEDUCTED"), "0")+"</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;"+uF.showData((String)hmInner.get("TAXPAID"), "0")+"</font></td>" +
		"<td width=\"15%\" align=\"right\"><font size=\"1\">&nbsp;"+uF.showData((String)hmInner.get("TAXBALANCED"), "0")+"</font></td>" +
		"<td width=\"36%\" colspan=\"2\" align=\"center\"><font size=\"1\">&nbsp;0</font></td>" +
		"</tr></table>";
		
		List<Element> supList5 = HTMLWorker.parseToList(new StringReader(tbl5), null);
		Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase5.add(supList5.get(0));
		document.add(phrase5);
			}
		}
		document.add(phrase3);
				
		String tbl6 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td width=\"60%\"><font size=\"1\">6.&nbsp;&nbsp;Total Tax payable for the year ending :</font></td><td width=\"40%\"><font size=\"1\">&nbsp;"+uF.formatIntoComma(totalamount)+"</font></td><td></td></tr>" +
		"<tr><td><font size=\"1\">7.&nbsp;&nbsp;Tax paid as per monthly statement :</font></td><td><font size=\"1\">&nbsp;"+uF.formatIntoComma(paidamount)+"</font></td><td></td></tr>" +
		"<tr><td><font size=\"1\">8.&nbsp;&nbsp;Balance tax paid under challan No. : </font></td><td><font size=\"1\">&nbsp;0</font></td><td></td></tr>" +
		"</table>";
		List<Element> supList6 = HTMLWorker.parseToList(new StringReader(tbl6), null);
		Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
		phrase6.add(supList6.get(0));
		document.add(phrase6);
		
		document.add(new Paragraph(" "));
		
		String tbl7 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">I certify that all employees who are liable to pay the tax in my employment during the period of return have been covered by the foregoing ,</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">particulars. I also certify that the necessary revision in the amount of tax deductable from the salary or wages of the employees on account </font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">of variation in the salary or wages earned by them has been made where necessary. </font></td></tr>" +
		"</table>";
		List<Element> supList7 = HTMLWorker.parseToList(new StringReader(tbl7), null);
		Phrase phrase7 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase7.add(supList7.get(0));
		document.add(phrase7);
		
		document.add(new Paragraph(" "));
		
		String tbl8 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">I shri&nbsp;,<b>"+uF.showData(hmEmpCodeName.get(sessionEmpId), "")+"</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>"+uF.showData(hmEmpCodeDesig.get(sessionEmpId), "")+"</b>,</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">solemnly declare that the above statements are true to the best of my knowledge and belief. </font></td></tr>" +
		"</table>";
		List<Element> supList8 = HTMLWorker.parseToList(new StringReader(tbl8), null);
		Phrase phrase8 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase8.add(supList8.get(0));
		document.add(phrase8);
		
		document.add(new Paragraph(" "));
		
		String tbl9 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">Place :&nbsp;&nbsp;&nbsp;&nbsp;Mumbai</font></td><td></td><td><font size=\"1\">Signature :<br/>(Employer)&nbsp;&nbsp;&nbsp;&nbsp;</font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">Date :&nbsp;&nbsp;&nbsp;&nbsp;</font></td><td></td><td><font size=\"1\">Status :&nbsp;&nbsp;Company</font></td></tr>" +
		"</table>";
		List<Element> supList9 = HTMLWorker.parseToList(new StringReader(tbl9), null);
		Phrase phrase9 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase9.add(supList9.get(0));
		document.add(phrase9);
		
		document.add(phrase3);
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));
		String tbl10 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\"><b>(For Office use only)</b></font></td></tr>" +
		"<tr><td align=\"left\"><font size=\"1\">The return is accepted on verification</font></td></tr>" +
		"</table>";
		List<Element> supList10 = HTMLWorker.parseToList(new StringReader(tbl10), null);
		Phrase phrase10 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase10.add(supList10.get(0));
		document.add(phrase10);
		document.add(new Paragraph(" "));
		
		String tbl11 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
		"<tr><td align=\"right\"><font size=\"1\">Tax Assessed&nbsp;&nbsp;&nbsp;&nbsp;</font></td><td></td><td><font size=\"1\">Rs&nbsp;&nbsp;</font></td></tr>" +
		"<tr><td align=\"right\"><font size=\"1\">Tax Assessed&nbsp;&nbsp;&nbsp;&nbsp;</font></td><td></td><td><font size=\"1\">Rs&nbsp;&nbsp;</font></td></tr>" +
		"<tr><td align=\"right\"><font size=\"1\">Tax Assessed&nbsp;&nbsp;&nbsp;&nbsp;</font></td><td></td><td><font size=\"1\">Rs&nbsp;&nbsp;</font></td></tr>" +
		"</table>";
		List<Element> supList11 = HTMLWorker.parseToList(new StringReader(tbl11), null);
		Phrase phrase11 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase11.add(supList11.get(0));
		document.add(phrase11);
		
		document.add(new Paragraph(" "));
		
		String tbl12 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"right\"><font size=\"1\">Assessing Authority</font></td></tr>" +
		"</table>";
		List<Element> supList12 = HTMLWorker.parseToList(new StringReader(tbl12), null);
		Phrase phrase12 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase12.add(supList12.get(0));
		document.add(phrase12);
		
		document.add(new Paragraph(" "));
		
		String tbl13 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\">Note: Where the return is not acceptable, separate order of assessment should be passed</font></td></tr>" +
		"</table>";
		List<Element> supList13 = HTMLWorker.parseToList(new StringReader(tbl13), null);
		Phrase phrase13 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase13.add(supList13.get(0));
		document.add(phrase13);
		
		
		document.close();
		String filename="PTaxChallan_"+uF.getDateFormat(strFinancialYearStart,DATE_FORMAT,"yyyy")+".pdf";
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename="+filename+"");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		buffer.close();
		out.close();
		
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	} finally {
		db.closeConnection(con);
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
		// TODO Auto-generated method stub
		this.response=response;
		
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request=request;
	}

	

}
