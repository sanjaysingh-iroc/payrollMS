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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class SalaryYearlyPdfReport implements ServletRequestAware,ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String financialYear;
	UtilityFunctions uF = new UtilityFunctions();
	public void execute()
	{
		
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		viewSalaryYearlyReport();
		generateSalaryYearlyPdfReport();
		return;
		
		
	}
	public void generateSalaryYearlyPdfReport(){
		
		try {



			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");

			Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
			Map hmDeductionSalaryMap = (Map)request.getAttribute("hmDeductionSalaryMap");
			Map hmEarningSalaryTotalMap = (Map)request.getAttribute("hmEarningSalaryTotalMap");
			Map hmDeductionSalaryTotalMap = (Map)request.getAttribute("hmDeductionSalaryTotalMap");
			Map hmSalaryHeadMap = (Map)request.getAttribute("hmSalaryHeadMap");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			List alMonth = (List)request.getAttribute("alMonth");

			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

					
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4.rotate());
		PdfWriter.getInstance(document, buffer);
		document.open();
				
		String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>Yearly Salary Summary for the period of "+strFinancialYearStart+" to "+strFinancialYearEnd+"</b></font></td></tr>" +
				"</table>";
		List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
		Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase.add(supList.get(0));
		document.add(phrase);
		
		document.add(new Paragraph(" "));
		
		
		StringBuilder sb=new StringBuilder();
		
		sb.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" +
				"<td align=\"left\" width=\"35%\"><font size=\"1\">&nbsp;Components&nbsp;&nbsp;</font></td>");
		for(int i=0; i<alMonth.size(); i++){
		sb.append("<td align=\"right\"><font size=\"1\">&nbsp;"+uF.getDateFormat((String)alMonth.get(i),"MM","MMM")+"&nbsp;&nbsp;</font></td>");
		}
		sb.append("</tr></table>");
		
		List<Element> supList1 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
		Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase1.add(supList1.get(0));
		document.add(phrase1);
		

		
		String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
		"<td align=\"left\"><font size=\"1\"><b>&nbsp;Earning</b></font></td>" +
		"</tr></table>";                       
		List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
		Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase2.add(supList2.get(0));
		document.add(phrase2);
		
		
		StringBuilder sb1=new StringBuilder();
		
		sb1.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");
		
		
		Set set = hmEarningSalaryMap.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			String strSalaryHeadId = (String)it.next();
			Map hmInner = (Map)hmEarningSalaryMap.get(strSalaryHeadId);

			sb1.append("<tr><td align=\"left\"><font size=\"1\">&nbsp;"+uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId),"")+"&nbsp;&nbsp;</font></td>");
			for(int i=0; i<alMonth.size(); i++){
				String strAmount = (String)hmInner.get((String)alMonth.get(i));
				sb1.append("<td align=\"right\"><font size=\"1\">&nbsp;"+uF.showData(strAmount,"0")+"&nbsp;&nbsp;</font></td>");
			}
			sb1.append("</tr>");
		}
		sb1.append("</table>");
		
		List<Element> supList6 = HTMLWorker.parseToList(new StringReader(sb1.toString()), null);
		Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase6.add(supList6.get(0));
		document.add(phrase6);
		
		
		StringBuilder sb2=new StringBuilder();
		
		sb2.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");
		sb2.append("<tr><td align=\"left\"><font size=\"1\"><b>&nbsp;Total &nbsp;&nbsp;</b></font></td>");
		for(int i=0; i<alMonth.size(); i++){
			String strTotalAmount = (String)hmEarningSalaryTotalMap.get((String)alMonth.get(i));
			
			sb2.append("<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData(strTotalAmount,"0")+"&nbsp;&nbsp;</b></font></td>");
		}
		sb2.append("</tr>");
		sb2.append("</table>");
	
		List<Element> supList7 = HTMLWorker.parseToList(new StringReader(sb2.toString()), null);
		Phrase phrase7 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase7.add(supList7.get(0));
		document.add(phrase7);
		
		
		document.add(new Paragraph(" "));
		
		
				
		String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
		"<td align=\"left\"><font size=\"1\"><b>&nbsp;Deduction</b></font></td>" +
		"</tr></table>";                       
		List<Element> supList9 = HTMLWorker.parseToList(new StringReader(tbl3), null);
		Phrase phrase9 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase9.add(supList9.get(0));
		document.add(phrase9);
		
		
		StringBuilder sb4=new StringBuilder();
		
		sb4.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");
		
		
		set = hmDeductionSalaryMap.keySet();
		it = set.iterator();
		while(it.hasNext()){
			String strSalaryHeadId = (String)it.next();
			Map hmInner = (Map)hmDeductionSalaryMap.get(strSalaryHeadId);

			sb4.append("<tr><td align=\"left\"><font size=\"1\">&nbsp;"+uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId),"")+"&nbsp;&nbsp;</font></td>");
			for(int i=0; i<alMonth.size(); i++){
				String strAmount = (String)hmInner.get((String)alMonth.get(i));
				sb4.append("<td align=\"right\"><font size=\"1\">&nbsp;"+uF.showData(strAmount,"0")+"&nbsp;&nbsp;</font></td>");
			}
			sb4.append("</tr>");
		}
		sb4.append("</table>");
		
		List<Element> supList10 = HTMLWorker.parseToList(new StringReader(sb4.toString()), null);
		Phrase phrase10 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase10.add(supList10.get(0));
		document.add(phrase10);
		
		
		StringBuilder sb5=new StringBuilder();
		
		sb5.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");
		sb5.append("<tr><td align=\"left\"><font size=\"1\"><b>&nbsp;Total &nbsp;&nbsp;</b></font></td>");
		for(int i=0; i<alMonth.size(); i++){
			String strTotalAmount = (String)hmDeductionSalaryTotalMap.get((String)alMonth.get(i));
			
			sb5.append("<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData(strTotalAmount,"0")+"&nbsp;&nbsp;</b></font></td>");
		}
		sb5.append("</tr>");
		sb5.append("</table>");
	
		List<Element> supList11 = HTMLWorker.parseToList(new StringReader(sb5.toString()), null);
		Phrase phrase11 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase11.add(supList11.get(0));
		document.add(phrase11);
		
		
		StringBuilder sb6=new StringBuilder();
		
		sb6.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");
		sb6.append("<tr><td align=\"left\"><font size=\"1\"><b>&nbsp;Net Pay &nbsp;&nbsp;</b></font></td>");
		for(int i=0; i<alMonth.size(); i++){
			String strTotalEarAmount = (String)hmEarningSalaryTotalMap.get((String)alMonth.get(i));
			String strTotalDedAmount = (String)hmDeductionSalaryTotalMap.get((String)alMonth.get(i));
			
			String strNet = (uF.formatIntoTwoDecimal((uF.parseToDouble(strTotalEarAmount) - uF.parseToDouble(strTotalDedAmount))))+"";
			
			sb6.append("<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData(strNet,"0")+"&nbsp;&nbsp;</b></font></td>");
		}
		sb6.append("</tr>");
		sb6.append("</table>");
	
		List<Element> supList12 = HTMLWorker.parseToList(new StringReader(sb6.toString()), null);
		Phrase phrase12 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase12.add(supList12.get(0));
		document.add(phrase12);
		
		document.close();
		
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=YearlySalaryReports.pdf");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		buffer.close();
		out.close();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}
	public void viewSalaryYearlyReport() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			
			
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
			Map hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			Map hmEarningSalaryMap = new LinkedHashMap();
			Map hmEarningSalaryTotalMap = new HashMap();
			Map hmDeductionSalaryMap = new LinkedHashMap();
			Map hmDeductionSalaryTotalMap = new HashMap();
			Map hmEmpInner = new HashMap();
			
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM")));
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
			List alMonth = new ArrayList();
			
			for(int i=0; i<12; i++){
				alMonth.add(cal.get(Calendar.MONTH)+"");
				
				cal.add(Calendar.MONTH, 1);
			}
			
			
			
			pst = con.prepareStatement("select salary_head_id, sum(amount) as amount, month, entry_date from payroll_generation where financial_year_from_date=? and financial_year_to_date=? and earning_deduction = ? and is_paid = true group by salary_head_id, month, entry_date order by salary_head_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(3, "E");
			
			
			rs = pst.executeQuery();
			
			
			String strMonthNew = null;
			String strMonthOld = null;
			
			while(rs.next()){
				
				strMonthNew = rs.getString("salary_head_id");
				
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					hmEmpInner = new HashMap();
				}
				
				
				hmEmpInner.put(rs.getString("month"), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEarningSalaryMap.put(rs.getString("salary_head_id"), hmEmpInner);
				
				
				double dblAmount = uF.parseToDouble((String)hmEarningSalaryTotalMap.get(rs.getString("month")));
				dblAmount += rs.getDouble("amount");
				hmEarningSalaryTotalMap.put(rs.getString("month"), uF.formatIntoTwoDecimal(dblAmount));
				
				strMonthOld  = strMonthNew ;
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select salary_head_id, sum(amount) as amount, month, entry_date from payroll_generation where financial_year_from_date=? and financial_year_to_date=? and earning_deduction = ? and is_paid = true group by salary_head_id, month, entry_date order by salary_head_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(3, "D");
			
			
			rs = pst.executeQuery();
			while(rs.next()){
				
				strMonthNew = rs.getString("salary_head_id");
				
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					hmEmpInner = new HashMap();
				}
				
				
				hmEmpInner.put(rs.getString("month"), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmDeductionSalaryMap.put(rs.getString("salary_head_id"), hmEmpInner);
				
				
				double dblAmount = uF.parseToDouble((String)hmDeductionSalaryTotalMap.get(rs.getString("month")));
				dblAmount += rs.getDouble("amount");
				hmDeductionSalaryTotalMap.put(rs.getString("month"), uF.formatIntoTwoDecimal(dblAmount));
				
				strMonthOld  = strMonthNew ;
			}
			rs.close();
			pst.close();
			
			
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmDeductionSalaryMap", hmDeductionSalaryMap);
			request.setAttribute("hmEarningSalaryTotalMap", hmEarningSalaryTotalMap);
			request.setAttribute("hmDeductionSalaryTotalMap", hmDeductionSalaryTotalMap);
			request.setAttribute("hmSalaryHeadMap", hmSalaryHeadMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("alMonth", alMonth);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	private HttpServletRequest request;
	private HttpServletResponse response; 
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	

}
