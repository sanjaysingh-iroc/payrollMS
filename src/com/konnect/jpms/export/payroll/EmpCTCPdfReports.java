package com.konnect.jpms.export.payroll;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

public class EmpCTCPdfReports implements ServletRequestAware,ServletResponseAware, IStatements {

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String financialYear;
	String strEmpId;
	UtilityFunctions uF = new UtilityFunctions();
	public void execute() 
	{
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		viewSalaryYearlyReport();
		generateCTCPdfReport();
		return;
	}
	public void generateCTCPdfReport(){
		
		try {



			String strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			String strEmpId = (String)request.getAttribute("strEmpId");

			Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
			Map hmEarningSalaryTotalMap = (Map)request.getAttribute("hmEarningSalaryTotalMap");
			Map hmSalaryHeadMap = (Map)request.getAttribute("hmSalaryHeadMap");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			List alServiceId = (List)request.getAttribute("alServiceId");
			Map hmServiceSalaryHeadMap = (Map)request.getAttribute("hmServiceSalaryHeadMap");

			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}


		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4.rotate());
		PdfWriter.getInstance(document, buffer);
		document.open();
				
		String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>CTC report for the period of "+strFinancialYearStart+" to "+strFinancialYearEnd+"</b></font></td></tr>" +
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
				"<td width=\"20%\"><font size=\"1\"><b>&nbsp;Employee Code&nbsp;&nbsp;</b></font></td>" +
				"<td width=\"35%\"><font size=\"1\"><b>&nbsp;Employee Name&nbsp;&nbsp;</b></font></td>" +
				"<td align=\"left\" width=\"20%\"><font size=\"1\"><b>&nbsp;Component&nbsp;&nbsp;</b></font></td>" +
				"<td align=\"right\" width=\"20%\"><font size=\"1\"><b>&nbsp;Amount&nbsp;&nbsp;</b></font></td></tr>" +
		"</table>";
		List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
		Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase1.add(supList1.get(0));
		document.add(phrase1);
		

		Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase5.add(supList4.get(0));
		document.add(phrase5);
		int count=0;
		double dblAmount = 0;
		for(int i=0; i<alServiceId.size(); i++){
			String strServiceId = (String)alServiceId.get(i);
			List alSalaryList = (List)hmServiceSalaryHeadMap.get(strServiceId);
			if(alSalaryList==null)alSalaryList=new ArrayList();
			
			
			for (; count<alSalaryList.size(); count++){
				String strSalaryHeadId = (String)alSalaryList.get(count);
				dblAmount += uF.parseToDouble((String)hmEarningSalaryMap.get(strSalaryHeadId+"_"+strServiceId));
			
			
				
			String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
			"<td><font size=\"1\"><b>&nbsp;"+((count==0)?uF.showData((String)hmEmpCode.get(strEmpId),""):"")+"&nbsp;&nbsp;</b></font></td>" +
			"<td><font size=\"1\"><b>&nbsp;"+((count==0)?uF.showData((String)hmEmpName.get(strEmpId),""):"")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"left\"><font size=\"1\" ><b>&nbsp;"+uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId),"")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData((String)hmEarningSalaryMap.get(strSalaryHeadId+"_"+strServiceId),"")+"&nbsp;&nbsp;</b></font></td></tr>" +
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
		}
		
		
		
		if(count==0){
			String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"center\"><font size=\"1\"><b>&nbsp;No salary paid for this employee</b></font></td>" +
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
		
		
		
		String tbl7 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
		"<td><font size=\"1\"><b></b></font></td>" +
		"<td><font size=\"1\"><b></b></font></td>" +
		"<td><font size=\"1\"><b>&nbsp;Total</b></font></td>" +
		"<td align=\"right\"><font size=\"1\"><b>"+uF.formatIntoOneDecimalWithOutComma(dblAmount)+"&nbsp;</b></font></td>" +
		"</tr></table>";
		List<Element> supList7 = HTMLWorker.parseToList(new StringReader(tbl7), null);
		Phrase phrase7 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase7.add(supList7.get(0));
		document.add(phrase7);
		
		Phrase phrase8 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase8.add(supList4.get(0));
		document.add(phrase8);
				
		
		document.close();
		
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=EmpCTCReports.pdf");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		buffer.close();
		out.close();
		
		
	}catch (Exception e) {
		// TODO: handle exception
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

			List<String> alServiceId = new ArrayList<String>();
			List<String> alSalaryHeadId = new ArrayList<String>();
			Map hmServiceSalaryHeadMap = new HashMap();

			pst = con.prepareStatement("select * from emp_salary_details esd, salary_details sd where sd.salary_head_id = esd.salary_head_id and emp_id = ? and entry_date = (select max(entry_date) from emp_salary_details where emp_id = ? and is_approved = true)  and esd.earning_deduction = 'E' and esd.salary_head_id not in ("+GROSS+") order by weight");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			String strSalaryHeadId = null;
			String strServiceId = null;
			
			while(rs.next()){
				
				strSalaryHeadId = rs.getString("salary_head_id");
				strServiceId = rs.getString("service_id");
			
				double dblAmount = uF.parseToDouble((String)hmEarningSalaryTotalMap.get(strSalaryHeadId+strServiceId));
				dblAmount += rs.getDouble("amount");
				hmEarningSalaryMap.put(strSalaryHeadId+"_"+strServiceId, dblAmount+"");
				
				
				
				if(!alServiceId.contains(strServiceId)){
					alServiceId.add(strServiceId);
				}
				if(!alSalaryHeadId.contains(strSalaryHeadId)){
					alSalaryHeadId.add(strSalaryHeadId);
				}
				hmServiceSalaryHeadMap.put(strServiceId, alSalaryHeadId);
				
			}
			rs.close();
			pst.close();
			
			
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmEarningSalaryTotalMap", hmEarningSalaryTotalMap);
			request.setAttribute("hmSalaryHeadMap", hmSalaryHeadMap);
			request.setAttribute("alServiceId", alServiceId);
			request.setAttribute("hmServiceSalaryHeadMap", hmServiceSalaryHeadMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strEmpId", getStrEmpId());
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		

	}

	HttpServletResponse response;
	HttpServletRequest request;
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

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}
}