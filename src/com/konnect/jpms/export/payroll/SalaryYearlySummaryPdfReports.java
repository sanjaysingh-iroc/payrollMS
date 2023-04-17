package com.konnect.jpms.export.payroll;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class SalaryYearlySummaryPdfReports implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	Map<String, String> hmEmployeeDetails = new HashMap<String, String>();
	UtilityFunctions uF = new UtilityFunctions();

	public void execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return;
		strUserType = (String) session.getAttribute(USERTYPE);
		viewSalaryYearlyReport();
		generateEmpSalaryYearlyPdfReport();
		return;

	}

	public void generateEmpSalaryYearlyPdfReport() {

		try {

			UtilityFunctions uF = new UtilityFunctions();
			CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

			String  strTotalEmp = (String)request.getAttribute("strTotalEmp");
			String  strMonth = (String)request.getAttribute("strMonth");

			Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
			Map hmDeductionSalaryMap = (Map)request.getAttribute("hmDeductionSalaryMap");
			Map hmEarningSalaryTotalMap = (Map)request.getAttribute("hmEarningSalaryTotalMap");
			Map hmDeductionSalaryTotalMap = (Map)request.getAttribute("hmDeductionSalaryTotalMap");
			Map hmSalaryHeadMap = (Map)request.getAttribute("hmSalaryHeadMap");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			


			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();

			StringBuilder sb = new StringBuilder();
			
			sb.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\"><tr><td align=\"center\"><font size=\"5\"><b>"+CF.getStrOrgName() + "</b></font></td></tr>");
			sb.append("<tr><td align=\"center\"><font size=\"2\"><b>Monthly Salary Register: "+uF.showData(strMonth, "") + "</b></font></td></tr>" + "</table>");
			List<Element> supList = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);
			
			
			double dblE = uF.parseToDouble((String)hmEarningSalaryTotalMap.get("TOTAL"));
			double dblD = uF.parseToDouble((String)hmDeductionSalaryTotalMap.get("TOTAL"));
			
			sb = new StringBuilder();

			sb.append("" +
			"<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">"+
			"<tr><td colspan=\"3\" align=\"left\" ><b>Grand Summary Details</b></td>"+
			"<td colspan=\"3\" align=\"right\" ><b>Summary Report for the Month & Year : "+strMonth+"</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"6\" ></td></tr>"+
			"<tr><td colspan=\"6\" ><b>Total Employee: "+uF.showData(strTotalEmp, "0") +"</b></td></tr>"+
			"<tr><td colspan=\"6\" ></td></tr>"+
			"<tr><td colspan=\"2\" ><b>Total Gross: "+uF.showData((String)hmEarningSalaryTotalMap.get("TOTAL"), "0")+"</b></td>"+
			"<td colspan=\"2\" ><b>Total Deduction: "+uF.showData((String)hmDeductionSalaryTotalMap.get("TOTAL"), "0")+"</b></td>"+
			"<td colspan=\"2\" align=\"right\" ><b>Total Net: "+uF.formatIntoTwoDecimal(dblE - dblD) +"</b></td></tr>"+
			"<tr><td colspan=\"6\" ></td></tr>"+
			"</table>"+
					
					"");
			
			
			

			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);

			
			sb = new StringBuilder();
			
			
			sb.append("<table cellpadding=\"5\" cellspacing=\"5\"><tr><td valign=\"top\">" +
					"<table cellpadding=\"0\" cellspacing=\"0\" >"+
					"<tr><td colspan=\"2\" ></td></tr>"+
			"<tr>"+
				"<td><b>Earnings</b></td>"+
				"<td align=\"right\"><b>Amount</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"2\" ></td></tr>");
			
			
			
			
			Set setE = hmEarningSalaryMap.keySet();
			Iterator itE = setE.iterator();
			while(itE.hasNext()){
				String strSalaryHeadId = (String)itE.next();
				
				
				sb.append("" +"<tr>"+
				"<td>"+uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId), "")+"</td>"+
				"<td align=\"right\">"+ (String)hmEarningSalaryMap.get(strSalaryHeadId) +"</td>"+
			"</tr>");
			
				
				
			}
			sb.append(""+
			
			"<tr>"+
				"<td><b>Total Earnings</b></td>"+
				"<td align=\"right\"><b> "+uF.showData((String)hmEarningSalaryTotalMap.get("TOTAL"), "0")+"</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"2\" ></td></tr>"+
		"</table>"+
					"");
			
			
			
			sb.append("</td><td valign=\"top\">");
			
			
			sb.append("" +
					"<table cellpadding=\"0\" cellspacing=\"0\">"+
					"<tr><td colspan=\"2\" ></td></tr>"+
			"<tr>"+
				"<td><b>Deductions</b></td>"+
				"<td align=\"right\"><b>Amount</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"2\" ></td></tr>");
			
			
			
			
			Set setD = hmDeductionSalaryMap.keySet();
			Iterator itD = setD.iterator();
			while(itD.hasNext()){
				String strSalaryHeadId = (String)itD.next();
				
				
				sb.append("" +"<tr>"+
				"<td>"+uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId), "")+"</td>"+
				"<td align=\"right\">"+ (String)hmDeductionSalaryMap.get(strSalaryHeadId) +"</td>"+
			"</tr>");
			
				
				
			}
			sb.append(""+
			
			"<tr>"+
				"<td><b>Total Deduction</b></td>"+
				"<td align=\"right\"><b> "+uF.showData((String)hmDeductionSalaryTotalMap.get("TOTAL"), "0")+"</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"2\" ></td></tr>"+
		"</table>"+
					"");
			
			
			sb.append("</td></tr></table>");
			
			
			System.out.println("sb===>"+sb.toString());
			
//			
//			List<Element> supList2 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
//			Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
//			phrase2.add(supList2.get(0));
//			document.add(phrase2);


			

			
			List<Element> supList6 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
			Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase6.add(supList6.get(0));
			document.add(phrase6);

			document.close();

			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=SalaryYearlySummary.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	String paycycle;
	String wlocation;
	
	
	public void viewSalaryYearlyReport() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			
			
			String[] strPayCycleDates = null;
			String strPayCycleStart = null;
			String strPayCycleEnd = null;
			String strPC = null;

			if (getPaycycle() != null) {
				
				strPayCycleDates = getPaycycle().split("-");
				strPayCycleStart = strPayCycleDates[0];
				strPayCycleEnd = strPayCycleDates[1];
				strPC = strPayCycleDates[2];
			
			} else {
				
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				
				strPayCycleStart = strPayCycleDates[0];
				strPayCycleEnd = strPayCycleDates[1];
				strPC = strPayCycleDates[2];
				 
			}
			
			con = db.makeConnection(con);
			Map hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			String strMonth = uF.getDateFormat(strPayCycleStart, DATE_FORMAT, "MM/yyyy");
			
			
			Map<String, String> hmEarningSalaryMap = new LinkedHashMap<String, String>();
			Map<String, String> hmEarningSalaryTotalMap = new HashMap<String, String>();
			Map<String, String> hmDeductionSalaryMap = new LinkedHashMap<String, String>();
			Map<String, String> hmDeductionSalaryTotalMap = new HashMap<String, String>();
			
			
			
			
			/*pst = con.prepareStatement("select salary_head_id, sum(amount) as amount, month, entry_date from payroll_generation where financial_year_from_date=? and financial_year_to_date=? and earning_deduction = ? and is_paid = true group by salary_head_id, month, entry_date order by salary_head_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(3, "E");*/
			
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			
			
			sbQuery.append("select count(distinct emp_id) as count from payroll_generation where paycycle=? ");
			if(uF.parseToInt(getWlocation())>0){
				sbQuery.append("and emp_id in (select emp_id from employee_official_details where wlocation_id ="+uF.parseToInt(getWlocation())+" ) ");
			}
			sbQuery.append("and is_paid = true ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strPC));	
			rs = pst.executeQuery();
			String strTotalEmp = null;
			while(rs.next()){
				strTotalEmp = rs.getString("count");
			}
			rs.close();
			pst.close();
			
			
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(amount) as amount, salary_head_id, earning_deduction from payroll_generation where paycycle=? ");
			if(uF.parseToInt(getWlocation())>0){
				sbQuery.append("and emp_id in (select emp_id from employee_official_details where wlocation_id ="+uF.parseToInt(getWlocation())+" ) ");
			}
			sbQuery.append("and is_paid = true group by salary_head_id, earning_deduction order by earning_deduction desc, salary_head_id ");
			
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strPC));		

			
			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			
			String strMonthNew = null;
			String strMonthOld = null;
			
			while(rs.next()){
				
				strMonthNew = rs.getString("salary_head_id");
				
				if("E".equalsIgnoreCase(rs.getString("earning_deduction"))){
					
					double dblAmt =  uF.parseToDouble((String)hmEarningSalaryMap.get(rs.getString("salary_head_id")));
					dblAmt += rs.getDouble("amount");
					hmEarningSalaryMap.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimal(dblAmt));
					
					
					double dblAmount = uF.parseToDouble((String)hmEarningSalaryTotalMap.get("TOTAL"));
					dblAmount += rs.getDouble("amount");
					hmEarningSalaryTotalMap.put("TOTAL", uF.formatIntoTwoDecimal(dblAmount));
					
					
				}else if("D".equalsIgnoreCase(rs.getString("earning_deduction"))){
					
					
					
					double dblAmt =  uF.parseToDouble((String)hmDeductionSalaryMap.get(rs.getString("salary_head_id")));
					dblAmt += rs.getDouble("amount");
					hmDeductionSalaryMap.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimal(dblAmt));
					
					
					double dblAmount = uF.parseToDouble((String)hmDeductionSalaryTotalMap.get("TOTAL"));
					dblAmount += rs.getDouble("amount");
					hmDeductionSalaryTotalMap.put("TOTAL", uF.formatIntoTwoDecimal(dblAmount));
					
				}
				
				strMonthOld  = strMonthNew ;
			}
			rs.close();
			pst.close();
			
		
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmDeductionSalaryMap", hmDeductionSalaryMap);
			request.setAttribute("hmEarningSalaryTotalMap", hmEarningSalaryTotalMap);
			request.setAttribute("hmDeductionSalaryTotalMap", hmDeductionSalaryTotalMap);
			request.setAttribute("hmSalaryHeadMap", hmSalaryHeadMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strTotalEmp", strTotalEmp);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

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

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getWlocation() {
		return wlocation;
	}

	public void setWlocation(String wlocation) {
		this.wlocation = wlocation;
	}

}
