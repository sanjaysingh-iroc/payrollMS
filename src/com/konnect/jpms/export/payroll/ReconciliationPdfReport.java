package com.konnect.jpms.export.payroll;

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

import org.apache.log4j.Logger;
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
import com.opensymphony.xwork2.ActionSupport;

public class ReconciliationPdfReport  extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(ReconciliationPdfReport.class);
	 
	String financialYear;
	String strMonth;
	String f_strWLocation;
	String f_department;
	String f_level;
	String f_org;
	String f_salaryhead;
	 
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(TITLE, "Reconciliation Report");
		request.setAttribute(PAGE, "/jsp/payroll/reports/ReconciliationReport.jsp");
		

		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		getReconciliationReport(uF);

		return "";

	}
	
	
	private void getReconciliationReport(UtilityFunctions uF) {
		viewReconciliationReport(uF);
		getReconciliationPdfReport(uF);
		
		/*if(uF.parseToInt(getF_org())>-1){
			if(uF.parseToInt(getF_strWLocation())>-1){				
				if(uF.parseToInt(getF_department())>-1){
					
					if(uF.parseToInt(getF_level())>-1){
						viewReconciliationReport(uF);
					}else{
						viewPTaxReportByLevel(uF);
					}					
					
				}else{
					viewPTaxReportByDepartment(uF);
				}
			}else{
				viewPTaxReportByLocation(uF);
			}			
			
		}else{
			if(uF.parseToInt(getF_org())==-1){
				viewPTaxReportByOrg(uF);
			}
		}*/
	}



	private void getReconciliationPdfReport(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);

			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			String  strYear = (String)request.getAttribute("strYear");
			String  strMonth = (String)request.getAttribute("strMonth");

			Map hmEmpPTax = (Map)request.getAttribute("hmEmpPTax");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			Map<String, String> hmOrg=(Map<String, String>)request.getAttribute("hmOrg");

			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}


		Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
		Map<String, String> hmDept =CF.getDeptMap(con);
		Map<String, String> hmLevelMap =(Map<String, String>)request.getAttribute("hmLevelMap");

		String  strPrevMonth = (String)request.getAttribute("strPrevMonth");
		

		Map<String, String> hmCurrMonthAmt =(Map<String, String>)request.getAttribute("hmCurrMonthAmt");
		Map<String, String> hmPrevMonthAmt =(Map<String, String>)request.getAttribute("hmPrevMonthAmt");

		

		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4.rotate());
		PdfWriter.getInstance(document, buffer);
		document.open();
				
		String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>Reconciliation for the month of "+uF.getDateFormat(strMonth, "MM", "MMMM")+" "+uF.getDateFormat(strYear, "yyyy", "yyyy")+"</b></font></td></tr>" +
				"</table>";
		List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
		Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase.add(supList.get(0));
		document.add(phrase);
		
		document.add(new Paragraph(" "));
		
		String tbl4 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
		"<td align=\"center\"><font size=\"1\"><b>____________________________________________________________________________________________________________________________________________________________________________</b></font></td>" +
		"</tr></table>";                       
		List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
		Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase4.add(supList4.get(0));
		document.add(phrase4);
		
		String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td><font size=\"1\"><b>&nbsp;Sr.No.&nbsp;&nbsp;</b></font></td>" +
				"<td><font size=\"1\"><b>&nbsp;Organization&nbsp;&nbsp;</b></font></td>" +
				"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.getDateFormat(strPrevMonth, "MM", "MMMM")+"&nbsp;&nbsp;</b></font></td>" +
				"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.getDateFormat(strMonth, "MM", "MMMM")+"&nbsp;&nbsp;</b></font></td>"+
				"<td align=\"right\"><font size=\"1\"><b>&nbsp;Diffrence&nbsp;&nbsp;</b></font></td>" +
		"</tr></table>";
		List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
		Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase1.add(supList1.get(0));
		document.add(phrase1);
		

		Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase5.add(supList4.get(0));
		document.add(phrase5);
		
		Set set = hmCurrMonthAmt.keySet();
		Iterator it = set.iterator();
		int count=0;
		double dblPrevAmountTotal = 0;
		double dblCurrAmountTotal = 0;
		double dblDifferenceAmountTotal = 0;
		
		while(it.hasNext()){
			String orgid = (String)it.next();
		
			count++;
			dblPrevAmountTotal+=uF.parseToDouble((String)hmPrevMonthAmt.get(orgid));
			dblCurrAmountTotal+=uF.parseToDouble((String)hmCurrMonthAmt.get(orgid));
			double difference=uF.parseToDouble((String)hmPrevMonthAmt.get(orgid))-uF.parseToDouble((String)hmCurrMonthAmt.get(orgid));
			dblDifferenceAmountTotal+=difference;
			
			String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
			"<td><font size=\"1\"><b>&nbsp;"+(count)+"&nbsp;&nbsp;</b></font></td>" +
			"<td><font size=\"1\"><b>&nbsp;"+uF.showData((String)hmOrg.get(orgid), "")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData((String)hmPrevMonthAmt.get(orgid), "0")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData((String)hmCurrMonthAmt.get(orgid), "0")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData(uF.formatIntoTwoDecimal(difference), "0")+"&nbsp;&nbsp;</b></font></td>" +
			"</tr></table>";
			List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
			Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase2.add(supList2.get(0));
			document.add(phrase2);
	
			String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
			"<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>" +
			"</tr></table>";
	List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
	Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
	phrase3.add(supList3.get(0));
	document.add(phrase3);
		}
		if(count==0){
			String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
			"<td align=\"center\"><font size=\"1\"><b>&nbsp;No Data found</b></font></td>" +
			"</tr></table>";
			
			List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
			Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase2.add(supList2.get(0));
			document.add(phrase2);
			String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
			"<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>" +
			"</tr></table>";
			List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
			Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase3.add(supList3.get(0));
			document.add(phrase3);
		}else{
			String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
			"<td>&nbsp;</td>" +
			"<td style=\"text-align:right;\"><font size=\"2\"><strong>Total</strong></font></td>" +
			"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblPrevAmountTotal)+"</strong></font></td>" +
			"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblCurrAmountTotal)+"</strong></font></td>" +
			"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblDifferenceAmountTotal)+"</strong></font></td></tr>" +
			"</table>";
			
			List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
			Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase2.add(supList2.get(0));
			document.add(phrase2);
			String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
			"<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>" +
			"</tr></table>";
			List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
			Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase3.add(supList3.get(0));
			document.add(phrase3);
		}
		
		
		document.close();
		
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=ReconciliationReport.pdf");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		
	}catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeConnection(con);
	}
	
}



	
	
	
	public void viewReconciliationReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

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
			
			String[] tempSalaryHead = getF_salaryhead().split(",");
			
			
						
			String prevMonth=uF.parseToInt(getStrMonth())>1 ? ""+(uF.parseToInt(getStrMonth())-1):"12";
			
			StringBuilder sbQuery = new StringBuilder();
		
			
			sbQuery.append("select sum(amount)as amount,month,year,eod.org_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? " +
					" and salary_head_id  in (0");
			
			for(int i=0;tempSalaryHead!=null && i<tempSalaryHead.length;i++){
				
				
					sbQuery.append(","+tempSalaryHead[i]); 
				
			}
			sbQuery.append(")");
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}
			
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			
			sbQuery.append(" group by month,year,eod.org_id order by eod.org_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(prevMonth));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(4,uF.parseToInt(getF_salaryhead()));
			
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map<String, String> hmPrevMonthAmt = new HashMap<String, String>();			
			
			while(rs.next()){				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");

				double dblAmount = rs.getDouble("amount");

				hmPrevMonthAmt.put(rs.getString("org_id"), uF.formatIntoTwoDecimal(dblAmount));
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			
			sbQuery.append("select sum(amount)as amount,month,year,eod.org_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? " +
					" and salary_head_id  in (0");
			
			for(int i=0;tempSalaryHead!=null && i<tempSalaryHead.length;i++){
				
				
					sbQuery.append(","+tempSalaryHead[i]);
				
			}
			sbQuery.append(")");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			
			sbQuery.append(" group by month,year,eod.org_id order by eod.org_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(4,uF.parseToInt(getF_salaryhead()));
			
			
			strMonth=null;
			strYear=null;
			
			rs = pst.executeQuery();
			
			Map<String, String> hmCurrMonthAmt = new HashMap<String, String>();
			 
			String strEmpIdOld=null;
			String strEmpIdNew=null;
			
			while(rs.next()){				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");

				hmCurrMonthAmt.put(rs.getString("org_id"), uF.formatIntoTwoDecimal(rs.getDouble("amount")));
			}
			rs.close();
			pst.close();
//			System.out.println("hmPrevMonthAmt==>"+hmPrevMonthAmt);
//			System.out.println("hmCurrMonthAmt==>"+hmCurrMonthAmt);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmCurrMonthAmt", hmCurrMonthAmt);
			request.setAttribute("hmPrevMonthAmt", hmPrevMonthAmt); 
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			request.setAttribute("strPrevMonth", prevMonth);
			
			pst=con.prepareStatement("select org_id,org_name from org_details");
			Map<String, String> hmOrg=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmOrg", hmOrg);
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private HttpServletRequest request;
	private HttpServletResponse response;

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


	public String getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String getF_department() {
		return f_department;
	}


	public void setF_department(String f_department) {
		this.f_department = f_department;
	}


	public String getF_level() {
		return f_level;
	}


	public void setF_level(String f_level) {
		this.f_level = f_level;
	}


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public String getF_salaryhead() {
		return f_salaryhead;
	}


	public void setF_salaryhead(String f_salaryhead) {
		this.f_salaryhead = f_salaryhead;
	}


	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
	}

}
