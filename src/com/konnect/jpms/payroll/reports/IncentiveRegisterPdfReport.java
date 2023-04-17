package com.konnect.jpms.payroll.reports;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
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

public class IncentiveRegisterPdfReport implements ServletRequestAware,ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;

	String financialYear;
	String strMonth;

	String strUserType;
	String strSessionEmpId;

	HttpSession session;
	CommonFunctions CF;
	UtilityFunctions uF = new UtilityFunctions();

	public void execute() throws Exception {

		session = request.getSession();

		/*
		 * setStrMonth("3"); setFinancialYear("01/04/2011-31/03/2012");
		 */
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
	//	if (CF == null)
	//		return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		viewIncentiveReport();
		generateIncentivePdfReport();

	//	return "";

	}

	public void generateIncentivePdfReport() {

		try {

			String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
			String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
			String strYear = (String) request.getAttribute("strYear");
			String strMonth = (String) request.getAttribute("strMonth");

			Map hmEmpPTax = (Map) request.getAttribute("hmEmpPTax");
			Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map) request.getAttribute("hmEmpName");

			if (strFinancialYearStart != null && strFinancialYearEnd != null) {
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();

			String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>Incentives Register for the month of "
					+ uF.getDateFormat(strMonth, "MM", "MMMM") + " " + uF.getDateFormat(strYear, "yyyy", "yyyy") + "</b></font></td></tr>" + "</table>";
			List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);

			document.add(new Paragraph(" "));

			String tbl4 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
					+ "<td align=\"center\"><font size=\"1\"><b>____________________________________________________________________________________________________________________________________________________________________________</b></font></td>"
					+ "</tr></table>";
			List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
			Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase4.add(supList4.get(0));
			document.add(phrase4);

			String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" + "<td><font size=\"1\"><b>&nbsp;Sr.No.&nbsp;&nbsp;</b></font></td>"
					+ "<td><font size=\"1\"><b>&nbsp;Employee Code&nbsp;&nbsp;</b></font></td>" + "<td><font size=\"1\"><b>&nbsp;Employee Name&nbsp;&nbsp;</b></font></td>"
					+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;Gross Salary&nbsp;&nbsp;</b></font></td>"
					+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;Incentive Amount&nbsp;&nbsp;</b></font></td></tr>" + "</table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);

			Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase5.add(supList4.get(0));
			document.add(phrase5);

			Set set = hmEmpPTax.keySet();
			Iterator it = set.iterator();
			int count = 0;
			while (it.hasNext()) {
				String strEmpId = (String) it.next();
				Map hmInner = (Map) hmEmpPTax.get(strEmpId);
				if (hmInner == null)
					hmInner = new HashMap();

				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" + "<td><font size=\"1\"><b>&nbsp;" + (++count) + "&nbsp;&nbsp;</b></font></td>"
						+ "<td><font size=\"1\"><b>&nbsp;" + uF.showData((String) hmEmpCode.get(strEmpId), "") + "&nbsp;&nbsp;</b></font></td>" + "<td><font size=\"1\"><b>&nbsp;"
						+ uF.showData((String) hmEmpName.get(strEmpId), "") + "&nbsp;&nbsp;</b></font></td>" + "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ uF.showData((String) hmInner.get("GROSS_AMOUNT"), "0") + "&nbsp;&nbsp;</b></font></td>" + "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ uF.showData((String) hmInner.get("INCENTIVE_AMOUNT"), "0") + "&nbsp;&nbsp;</b></font></td></tr>" + "</table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);

				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
						+ "<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>"
						+ "</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}
			if (count == 0) {
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" + "<td align=\"center\"><font size=\"1\"><b>&nbsp;No Employees found</b></font></td>"
						+ "</tr></table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
						+ "<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>"
						+ "</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}

			document.close();

			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=IncentiveRegister_"+strMonth+"_"+strYear+".pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void viewIncentiveReport() {

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
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			pst = con
					.prepareStatement("select emp_id, amount, month, year from payroll_generation where month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id = ?");
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, INCENTIVES);

			String strMonth = null;
			String strYear = null;

			rs = pst.executeQuery();

			Map hmEmpPTax = new HashMap();

			while (rs.next()) {
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("INCENTIVE_AMOUNT", rs.getString("amount"));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");

				hmEmpPTax.put(rs.getString("emp_id"), hmEmpInner);
			}
			rs.close();
			pst.close();

			pst = con
					.prepareStatement("select emp_id, sum(amount) as amount, month, year from payroll_generation where earning_deduction = 'E' and month=? and financial_year_from_date=? and financial_year_to_date=? group by emp_id,month, year");
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));

			rs = pst.executeQuery();

			while (rs.next()) {

				Map hmEmpInner = (Map) hmEmpPTax.get(rs.getString("emp_id"));
				if (hmEmpInner == null)
					hmEmpInner = new HashMap();
				hmEmpInner.put("GROSS_AMOUNT", rs.getString("amount"));
				hmEmpPTax.put(rs.getString("emp_id"), hmEmpInner);

				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();

			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpPTax", hmEmpPTax);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);

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

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}
}
