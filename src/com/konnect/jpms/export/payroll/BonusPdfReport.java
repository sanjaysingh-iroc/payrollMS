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
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BonusPdfReport  extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(BonusPdfReport.class);
	
	
	public String execute() throws Exception { 
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession(); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		

		viewBonusReport(uF);
		generateBonusPdfReport(uF);
		
		return "";

	}
	
	
	private void generateBonusPdfReport(UtilityFunctions uF) {
		
		try {

			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			String  strYear = (String)request.getAttribute("strYear");
			String  strMonth = (String)request.getAttribute("strMonth");


			Map hmEmpBonusMap = (Map)request.getAttribute("hmEmpBonusMap");
			Map hmBonusMap = (Map)request.getAttribute("hmBonusMap");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			Map hmEmpLevel = (Map)request.getAttribute("hmEmpLevel");
			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4.rotate());
		PdfWriter.getInstance(document, buffer);
		document.open();
				
		String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>Bonus Report as per Payments of Bonus Act for the month of "+uF.getDateFormat(strMonth, "MM", "MMMM")+" "+uF.getDateFormat(strYear, "yyyy", "yyyy")+"</b></font></td></tr>" +
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
				"<td><font size=\"1\"><b>&nbsp;Employee Code&nbsp;&nbsp;</b></font></td>" +
				"<td><font size=\"1\"><b>&nbsp;Employee Name&nbsp;&nbsp;</b></font></td>" +
				"<td align=\"right\"><font size=\"1\"><b>&nbsp;Bonus Salary&nbsp;&nbsp;</b></font></td>" +
				"<td align=\"right\"><font size=\"1\"><b>&nbsp;Calculated Salary&nbsp;&nbsp;</b></font></td>" +
				"<td align=\"right\"><font size=\"1\"><b>&nbsp;Rate&nbsp;&nbsp;</b></font></td>" +
				"<td align=\"right\"><font size=\"1\"><b>&nbsp;Bonus&nbsp;&nbsp;</b></font></td>"+
				"</tr></table>";
		List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
		Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase1.add(supList1.get(0));
		document.add(phrase1);
		

		Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase5.add(supList4.get(0));
		document.add(phrase5);
		
		Set set = hmEmpBonusMap.keySet();
		Iterator it = set.iterator();
		int count=0;
		double dblGrossAmountTotal = 0;
		double dblPTaxAmountTotal = 0;
		while(it.hasNext()){
			String strEmpId = (String)it.next();
			Map hmInner = (Map)hmEmpBonusMap.get(strEmpId);
			if(hmInner==null)hmInner=new HashMap();
			
			String strLevelId = (String)hmEmpLevel.get(strEmpId);
			
			Map hmBonusInner = (Map)hmBonusMap.get(strLevelId);
			if(hmBonusInner==null)hmBonusInner=new HashMap();
			
			String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
			"<td><font size=\"1\"><b>&nbsp;"+(++count)+"&nbsp;&nbsp;</b></font></td>" +
			"<td><font size=\"1\"><b>&nbsp;"+uF.showData((String)hmEmpCode.get(strEmpId), "")+"&nbsp;&nbsp;</b></font></td>" +
			"<td><font size=\"1\"><b>&nbsp;"+uF.showData((String)hmEmpName.get(strEmpId), "")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData((String)hmInner.get("GROSS_AMOUNT"), "0")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData((String)hmBonusInner.get("MINIMUM_AMOUNT"), "0")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData((String)hmBonusInner.get("BONUS_RATE"), "0")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+uF.showData((String)hmInner.get("BONUS_PAID"), "0")+"&nbsp;&nbsp;</b></font></td>" +
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
			"<td align=\"center\"><font size=\"1\"><b>&nbsp;No Employees found</b></font></td>" +
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
		/*else{
			String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
			"<td>&nbsp;</td>" +
			"<td>&nbsp;</td>" +
			"<td style=\"text-align:right;\"><font size=\"2\"><strong>Total</strong></font></td>" +
			"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblGrossAmountTotal)+"</strong></font></td>" +
			"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblPTaxAmountTotal)+"</strong></font></td></tr>" +
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
		}*/
		
		
		document.close();
		
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=BonusPdfReport.pdf");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}


	String financialYear;
	String strMonth;
	String f_org;
	List<FillOrganisation> orgList;
	
	
	String f_strWLocation;
	String f_department;
	String f_level;
	
	
	public void viewBonusReport(UtilityFunctions uF) {

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
			Map hmEmpLevel = CF.getEmpLevelMap(con);
			
			String strMonth=null;
			String strYear=null;
			Map hmEmpBonusMap = new HashMap();
			
			//pst = con.prepareStatement("select emp_id, amount, month, year from payroll_generation where month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id = ? and emp_id in (select emp_id from employee_official_details where org_id =?)");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id, amount, month, year from payroll_generation where month=? and financial_year_from_date=? " +
					"and financial_year_to_date=? and salary_head_id = ? ");

			if(uF.parseToInt(getF_strWLocation())>0 || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_level())>0 || uF.parseToInt(getF_org())>0){
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
			
			if(uF.parseToInt(getF_strWLocation())>0 || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_level())>0 || uF.parseToInt(getF_org())>0){
				sbQuery.append(")");
			}
			pst = con.prepareStatement(sbQuery.toString());			
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, BONUS);			
			System.out.println("pst==>"+pst); 
			rs = pst.executeQuery();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("BONUS_PAID", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				
				if(rs.getDouble("amount")>0){
					hmEmpBonusMap.put(rs.getString("emp_id"), hmEmpInner);
				}
				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select emp_id, sum(amount) as amount from  emp_salary_details esd where salary_head_id in (select salary_head_id from salary_details where earning_deduction = 'E') group by emp_id");
			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpBonusMap.get(rs.getString("emp_id"));
//				if(hmEmpInner==null)hmEmpInner = new HashMap();
				if(hmEmpInner==null)continue;
				
				
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEmpBonusMap.put(rs.getString("emp_id"), hmEmpInner);
				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(selectBonus2);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			
			Map hmBonusMap = new HashMap();
			
			while(rs.next()){
				
				Map hmInner = new HashMap();
				
				hmInner.put("MINIMUM_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("bonus_minimum"))));
				hmInner.put("MAXIMUM_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("bonus_maximum"))));
				hmInner.put("BONUS_RATE", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("bonus_amount"))));
				
				hmBonusMap.put(rs.getString("level_id"), hmInner);
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpBonusMap", hmEmpBonusMap);
			request.setAttribute("hmBonusMap", hmBonusMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpLevel", hmEmpLevel);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
			
			
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
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
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




	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public List<FillOrganisation> getOrgList() {
		return orgList;
	}


	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
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



}
