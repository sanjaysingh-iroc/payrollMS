package com.konnect.jpms.export.payroll;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;


public class EmpSalaryYearlySummaryPdfReports implements ServletRequestAware, ServletResponseAware, IStatements {

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

			String  strMonth = (String)request.getAttribute("strMonth");
			String  strWLocation = (String)request.getAttribute("strWLocation");

			Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
			Map hmDeductionSalaryMap = (Map)request.getAttribute("hmDeductionSalaryMap");
			Map hmEarningSalaryTotalMap = (Map)request.getAttribute("hmEarningSalaryTotalMap");
			Map hmDeductionSalaryTotalMap = (Map)request.getAttribute("hmDeductionSalaryTotalMap");
			Map hmSalaryHeadMap = (Map)request.getAttribute("hmSalaryHeadMap");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			List alMonth = (List)request.getAttribute("alMonth");

			List alEarning = (List)request.getAttribute("alEarning");
			List alDeduction = (List)request.getAttribute("alDeduction");
			

			int totalSize = Math.max(alEarning.size(), alDeduction.size());

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
			
			sb = new StringBuilder();

			sb.append("<table cellpadding=\"2\" cellspacing=\"0\" border=\"0\">");
			
			sb.append("<tr>");
			int i=0;
			for (i = 0; i < alEarning.size(); i++) {
				sb.append("<td align=\"right\" style=\"border-top:solid 1px #000\"><font size=\"1\">&nbsp;" + uF.showData((String)hmSalaryHeadMap.get((String)alEarning.get(i)), "")+ "&nbsp;&nbsp;</font></td>");
			}
			
			for(;i<totalSize; i++){
				sb.append("<td align=\"right\" style=\"border-top:solid 1px #000\"><font size=\"1\">&nbsp;&nbsp;&nbsp;</font></td>");
			}
			sb.append("<td align=\"right\"><font size=\"1\">&nbsp;Total Earning&nbsp;&nbsp;</font></td>");
			sb.append("<td align=\"right\"><font size=\"1\">&nbsp;Net Pay&nbsp;&nbsp;</font></td>");
			sb.append("</tr>");
			sb.append("<tr>");
			for (i = 0; i < alDeduction.size(); i++) {
				sb.append("<td align=\"right\"><font size=\"1\">&nbsp;" + uF.showData((String)hmSalaryHeadMap.get((String)alDeduction.get(i)), "") + "&nbsp;&nbsp;</font></td>");
			}
			for(;i<totalSize; i++){
				sb.append("<td align=\"right\" style=\"border-top:solid 1px #000\"><font size=\"1\">&nbsp;&nbsp;&nbsp;</font></td>");
			}
			sb.append("<td align=\"right\"><font size=\"1\">&nbsp;Total Deduction&nbsp;&nbsp;</font></td>");
			sb.append("<td align=\"right\">&nbsp;</td>");
			sb.append("</tr>");
			
			sb.append("<tr><td colspan=\""+(totalSize+2)+"\"></td></tr>");
			
//			sb.append("</table>");
/*
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);*/

			
			
			
			
			sb.append("<tr><td colspan=\""+(totalSize+2)+"\" align=\"left\"><font size=\"1\"><b>Location: </b>"+strWLocation+"</font>" +
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
					"<font size=\"1\"><b>Month: </b>"+strMonth+"</font></td></tr>");
			sb.append("</tr><td colspan=\""+(totalSize+2)+"\"></td><tr>");
//			
//			List<Element> supList2 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
//			Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
//			phrase2.add(supList2.get(0));
//			document.add(phrase2);


			
			
//			sb = new StringBuilder();
//			sb.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");

			Set set = hmEarningSalaryMap.keySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String strEmpId = (String) it.next();
				Map hmInner = (Map) hmEarningSalaryMap.get(strEmpId);
				
				
				sb.append("<tr>");
				sb.append("<tr><td colspan=\""+(totalSize+2)+"\"><font size=\"1\"><b>Emp Code: </b>"+uF.showData((String)hmEmpCode.get(strEmpId), "")+"</font>" +
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
						"<font size=\"1\"><b>Emp Name: </b>"+uF.showData((String)hmEmpName.get(strEmpId), "")+"</font></td></tr>");
				sb.append("</tr>");
				
				
				
				sb.append("<tr>");
				double dblTotalE=0;
				double dblTotalD=0;
				for (i = 0; i < alEarning.size(); i++) {
					String strAmount = (String) hmInner.get((String) alEarning.get(i));
					sb.append("<td align=\"right\"><font size=\"1\">&nbsp;" + uF.showData(strAmount, "0") + "&nbsp;&nbsp;</font></td>");
					
					dblTotalE += uF.parseToDouble(strAmount);
				}
				for(;i<totalSize; i++){
					sb.append("<td align=\"right\" style=\"border-top:solid 1px #000\"><font size=\"1\">&nbsp;&nbsp;&nbsp;</font></td>");
				}
				sb.append("<td align=\"right\"><font size=\"1\">&nbsp;" + uF.formatIntoTwoDecimal(dblTotalE) + "&nbsp;&nbsp;</font></td>");
				sb.append("<td align=\"right\">&nbsp;</td>");
				sb.append("</tr>");
				
				
				sb.append("<tr>");
				for (i = 0; i < alDeduction.size(); i++) {
					String strAmount = (String) hmInner.get((String) alDeduction.get(i));
					sb.append("<td align=\"right\"><font size=\"1\">&nbsp;" + uF.showData(strAmount, "0") + "&nbsp;&nbsp;</font></td>");
					
					dblTotalD += uF.parseToDouble(strAmount);
				}
				for(;i<totalSize; i++){
					sb.append("<td align=\"right\" style=\"border-top:solid 1px #000\"><font size=\"1\">&nbsp;&nbsp;&nbsp;</font></td>");
				}
				sb.append("<td align=\"right\"><font size=\"1\">&nbsp;" + uF.formatIntoTwoDecimal(dblTotalD) + "&nbsp;&nbsp;</font></td>");
				sb.append("<td align=\"right\"><font size=\"1\">&nbsp;" + uF.formatIntoTwoDecimal(dblTotalE - dblTotalD) + "&nbsp;&nbsp;</font></td>");
				sb.append("</tr>");
				
				sb.append("</tr><td colspan=\""+(totalSize+2)+"\"></td><tr>");
			}
			sb.append("</table>");

			
			List<Element> supList6 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
			Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase6.add(supList6.get(0));
			document.add(phrase6);

			document.close();

			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=EmpSalaryYearlySummary.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();

		} catch (Exception e) {
			// TODO: handle exception
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

			String strMonth = uF.getDateFormat(strPayCycleStart, DATE_FORMAT, "MM/yyyy");
			request.setAttribute("strMonth", strMonth);
			
			con = db.makeConnection(con);
			Map hmSalaryHeadMap = CF.getSalaryHeadsMap(con);

			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);

			Map hmEarningSalaryMap = new LinkedHashMap();
			Map hmEarningSalaryTotalMap = new HashMap();
			Map hmDeductionSalaryTotalMap = new HashMap();
			Map hmEmpInner = new HashMap();

			List alEarning = new ArrayList();
			List alDeduction = new ArrayList();

			/*
			 * Calendar cal = GregorianCalendar.getInstance();
			 * cal.set(Calendar.DATE,
			 * uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(),
			 * DATE_FORMAT, "dd"))); cal.set(Calendar.MONTH,
			 * uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(),
			 * DATE_FORMAT, "MM"))-1); cal.set(Calendar.YEAR,
			 * uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(),
			 * DATE_FORMAT, "yyyy")));
			 * 
			 * List alMonth = new ArrayList();
			 * 
			 * for(int i=0; i<12; i++){
			 * alMonth.add((cal.get(Calendar.MONTH)+1)+"");
			 * 
			 * cal.add(Calendar.MONTH, 1); }
			 */

//			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from work_location_info where wlocation_id=?");
			pst.setInt(1, uF.parseToInt(getWlocation()));
			rs = pst.executeQuery();
			String strWLocation = null;
			while (rs.next()) {
				strWLocation = rs.getString("wlocation_name");
			}
			rs.close();
			pst.close();
			request.setAttribute("strWLocation", strWLocation);

			pst = con
					.prepareStatement("select amount, emp_id, salary_head_id, earning_deduction from payroll_generation where paycycle=? and emp_id in (select emp_id from employee_official_details where wlocation_id =? ) and is_paid = true order by emp_id");
			pst.setInt(1, uF.parseToInt(strPC));
			pst.setInt(2, uF.parseToInt(getWlocation()));
			rs = pst.executeQuery();
			String strEmpIdNew = null;
			String strEmpIdOld = null;

			while (rs.next()) {

				strEmpIdNew = rs.getString("emp_id");

				if (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					hmEmpInner = new HashMap();
				}

				hmEmpInner.put(rs.getString("salary_head_id"), rs.getString("amount"));
				hmEarningSalaryMap.put(strEmpIdNew, hmEmpInner);

				/*
				 * double dblAmount =
				 * uF.parseToDouble((String)hmEarningSalaryTotalMap
				 * .get(rs.getString("month"))); dblAmount +=
				 * rs.getDouble("amount");
				 * hmEarningSalaryTotalMap.put(rs.getString("month"),
				 * uF.formatIntoTwoDecimal(dblAmount));
				 */

				if ("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarning.contains(rs.getString("salary_head_id"))) {
					alEarning.add(rs.getString("salary_head_id"));
				}

				if ("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeduction.contains(rs.getString("salary_head_id"))) {
					alDeduction.add(rs.getString("salary_head_id"));
				}

				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();

//			System.out.println("hmEarningSalaryMap========>" + hmEarningSalaryMap);

			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmSalaryHeadMap", hmSalaryHeadMap);

			request.setAttribute("alEarning", alEarning);
			request.setAttribute("alDeduction", alDeduction);

			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);

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
