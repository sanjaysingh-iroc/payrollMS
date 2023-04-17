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

public class ReimbursementPdfReports implements ServletRequestAware,ServletResponseAware, IStatements {

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String financialYear;
	String strMonth;
	UtilityFunctions uF = new UtilityFunctions();
	
	String f_strWLocation;
	String f_department;
	String f_level;
	String f_org;
	String f_service;
	
	public void execute()
	{
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return; 
		strUserType = (String) session.getAttribute(USERTYPE);
		viewReimbursementStatement();
		generateReimbursementReport();
		return;
		
		
	}
	
	public void generateReimbursementReport(){
		
		try {

			
			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");

			Map hmReimbursementMap = (Map)request.getAttribute("hmReimbursementMap");
			Map hmReimbursementType = (Map)request.getAttribute("hmReimbursementType");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");

			List alMonth = (List)request.getAttribute("alMonth");

			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}



		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4.rotate());
		PdfWriter.getInstance(document, buffer);
		document.open();
				
		String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>Reimbursement statement for F.Y. "+strFinancialYearStart +" to "+strFinancialYearEnd+"</b></font></td></tr>" +
				"</table>";
		List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
		Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase.add(supList.get(0));
		document.add(phrase);		
		
		
		document.add(new Paragraph(" "));
	  	StringBuilder sb=new StringBuilder();
	  	sb.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" +
				"<td width=\"5%\"><font size=\"1\">&nbsp;Emp Code&nbsp;&nbsp;</font></td>" +
				"<td><font size=\"1\">&nbsp;Emp Name&nbsp;&nbsp;</font></td>");
	  	for(int m=0; m<alMonth.size(); m++){
	  	sb.append("<td><font size=\"1\">&nbsp;"+uF.getDateFormat((String)alMonth.get(m), "MM", "MMM")+"&nbsp;&nbsp;</font></td>");
	  	}
		sb.append("</tr></table>");
		
		List<Element> supList1 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
		Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase1.add(supList1.get(0));
		document.add(phrase1);
		
		Set set = hmReimbursementMap.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			String strEmpId = (String)it.next();
			Map hmInner = (Map)hmReimbursementMap.get(strEmpId);
			if(hmInner==null)hmInner = new HashMap();
		StringBuilder sb1=new StringBuilder();
	  	sb1.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" +
				"<td width=\"5%\"><font size=\"1\">&nbsp;"+(String)hmEmpCode.get(strEmpId)+"&nbsp;&nbsp;</font></td>" +
				"<td><font size=\"1\">&nbsp;"+(String)hmEmpName.get(strEmpId)+"&nbsp;&nbsp;</font></td>");
	  	for(int m=0; m<alMonth.size(); m++){
	  	sb1.append("<td><font size=\"1\">&nbsp;&nbsp;&nbsp;</font></td>");
	  	}
	  	sb1.append("</tr>");
	  	
	  	
	  	List alReimbursementType = (List)hmReimbursementType.get(strEmpId);
		if(alReimbursementType==null)alReimbursementType = new ArrayList();
		
		for(int i=0; i<alReimbursementType.size(); i++){
			
			
			sb1.append("<tr><td width=\"5%\"><font size=\"1\">&nbsp;"+(String)alReimbursementType.get(i)+"&nbsp;&nbsp;</font></td><td></td>");
			for(int m=0; m<alMonth.size(); m++){
				String strAmount = (String)hmInner.get((String)alMonth.get(m)+"_"+(String)alReimbursementType.get(i));
				sb1.append("<td width=\"5%\" align=\"right\"><font size=\"1\">&nbsp;"+uF.showData(strAmount, "0")+"&nbsp;&nbsp;</font></td>");
			
			}
			sb1.append("</tr>");
		}
	  	
	  	
		sb1.append("</table>");
		
		List<Element> supList2 = HTMLWorker.parseToList(new StringReader(sb1.toString()), null);
		Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase2.add(supList2.get(0));
		document.add(phrase2);
		
			
		}
		
		
		
		document.close();
		
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=ReimbursementReports.pdf");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush(); 
		buffer.close();
		out.close();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}
	
	public void viewReimbursementStatement() {

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
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM")));
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
			List alMonth = new ArrayList();
			
			for(int i=0; i<12; i++){
				alMonth.add(uF.getDateFormat(cal.get(Calendar.MONTH)+"", "MM", "MM"));
				
				cal.add(Calendar.MONTH, 1);
			}
			
			
			Map hmReimbursementMap = new HashMap();
			Map hmEmpInner = new HashMap();
			List alReimbursementType = new ArrayList();
			Map hmReimbursementType = new HashMap();
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_reimbursement where paid_date between  ? and ? ");
			
			if(uF.parseToInt(getF_service())>0 || uF.parseToInt(getF_strWLocation())>0 || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_level())>0 || uF.parseToInt(getF_org())>0){
				sbQuery.append(" and emp_id in ( select emp_id from employee_official_details where emp_id>0 ");
			}
			
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id="+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and service_id like '%,"+uF.parseToInt(getF_service())+",%' ");
			}
			
			if(uF.parseToInt(getF_service())>0 || uF.parseToInt(getF_strWLocation())>0 || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_level())>0 || uF.parseToInt(getF_org())>0){
				sbQuery.append(" )");
			}
			sbQuery.append(" order by emp_id");
			 
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
			
			rs = pst.executeQuery();
			
			
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			
			while(rs.next()){
				
				strEmpIdNew = rs.getString("emp_id");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					hmEmpInner = new HashMap();
					alReimbursementType = new ArrayList();
				}
				
				
				hmEmpInner = (Map)hmReimbursementMap.get(strEmpIdNew);
				if(hmEmpInner==null)hmEmpInner = new HashMap();
				
				double dblAmount = uF.parseToDouble((String)hmEmpInner.get(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MM"))+"_"+rs.getString("reimbursement_type"));
				dblAmount += rs.getDouble("reimbursement_amount");
				hmEmpInner.put(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MM")+"_"+rs.getString("reimbursement_type"), uF.formatIntoTwoDecimal(dblAmount));
				
				hmReimbursementMap.put(strEmpIdNew, hmEmpInner);
				
				if(!alReimbursementType.contains(rs.getString("reimbursement_type"))){
					alReimbursementType.add(rs.getString("reimbursement_type"));
				}
				
				
				hmReimbursementType.put(strEmpIdNew, alReimbursementType);
				
				strEmpIdOld  = strEmpIdNew ;
			}
			rs.close();
			pst.close();
			
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmReimbursementMap", hmReimbursementMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmReimbursementType", hmReimbursementType);
			request.setAttribute("alMonth", alMonth);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

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

	public String getF_service() {
		return f_service;
	}

	public void setF_service(String f_service) {
		this.f_service = f_service;
	}
	
}
