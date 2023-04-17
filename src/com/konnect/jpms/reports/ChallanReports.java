package com.konnect.jpms.reports;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ChallanReports  extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {

	
	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId;
	String strMonth;
	String financialYear;
	List<List<String>> alList = new ArrayList<List<String>>();
	Map hmDetails = new HashMap();
	 
	UtilityFunctions uF = new UtilityFunctions();
	public String execute() throws Exception {
		

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		setStrMonth("3");
		setFinancialYear("2012");
		
		
		
		
		 getPaymentDetails();
		getChallanData();
		generateChallanReports();

		
		return "";

	}
	
	public void getPaymentDetails()
	{
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String financial_year_from_date="";
		String financial_year_to_date="";
		String salary_head_id=null;
		double total_wages_due=0.0;
		double eepf_contribution=0.0;
		double erpf_contribution=0.0;
		double pf_admin_charges=0.0;
		double eepf_contribution_amount=0.0;
		double erpf_contribution_amount=0.0;
		double pf_admin_charges_amount=0.0;
		int total_emp=0;
		
		try{
			con = db.makeConnection(con);
			pst = con.prepareStatement("select financial_year_from_date,financial_year_to_date from payroll_generation " +
					"where month=? and year=? order by financial_year_from_date desc limit 1");
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setInt(2, uF.parseToInt(getFinancialYear()));
			rs= pst.executeQuery();
			while(rs.next()){
				financial_year_from_date=rs.getString("financial_year_from_date");
				financial_year_to_date=rs.getString("financial_year_to_date");
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select salary_head_id,eepf_contribution,erpf_contribution,pf_admin_charges from epf_details where financial_year_start=? and financial_year_end=?");
			pst.setDate(1,uF.getDateFormat(financial_year_from_date, DBDATE));
			pst.setDate(2, uF.getDateFormat(financial_year_to_date, DBDATE));
			rs= pst.executeQuery();
			while(rs.next()){
				salary_head_id=rs.getString("salary_head_id");
				eepf_contribution=rs.getDouble("eepf_contribution");
				erpf_contribution=rs.getDouble("erpf_contribution");
				pf_admin_charges=rs.getDouble("pf_admin_charges");
			}
			rs.close();
			pst.close();
			
			if(salary_head_id.contains(",")){
				salary_head_id=salary_head_id.substring(0, salary_head_id.length()-1);
			}
			
			
			pst = con.prepareStatement("select sum(amount) as amount,count(distinct(emp_id)) as total_emp from payroll_generation where  month=? and year=? and salary_head_id in ("+salary_head_id+")");
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setInt(2, uF.parseToInt(getFinancialYear()));
			
			rs= pst.executeQuery();
			while(rs.next()){
				total_wages_due=rs.getDouble("amount");
				total_emp=rs.getInt("total_emp");
			}
			rs.close();
			pst.close();
			
			eepf_contribution_amount=(eepf_contribution*total_wages_due)/100;
			erpf_contribution_amount=(erpf_contribution*total_wages_due)/100;
			pf_admin_charges_amount=(pf_admin_charges*total_wages_due)/100;
			/*
			System.out.println("financial_year_from_date=====>"+financial_year_from_date);
			System.out.println("financial_year_to_date=====>"+financial_year_to_date);
			System.out.println("salary_head_id=====>"+salary_head_id);
			System.out.println("eepf_contribution=====>"+eepf_contribution);
			System.out.println("erpf_contribution=====>"+erpf_contribution);*/
			System.out.println("eepf_contribution_amount=====>"+eepf_contribution_amount);
			System.out.println("erpf_contribution_amount=====>"+erpf_contribution_amount);
			System.out.println("pf_admin_charges_amount=====>"+pf_admin_charges_amount);
			
			
			
			
	
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	
	
	public void getChallanData()
	{
		
		List<String> alInner = new ArrayList<String>();
		
		alInner.add("EMPLOYER'S SHARES  OF CONT");
		alInner.add("3653");
		alInner.add("-----");
		alInner.add("2,207");
		alInner.add("133");
		alInner.add("----");
		alInner.add("5,993");
		alList.add(alInner);
		alInner.add("EMPLOYEE'S SHARES OF CONT");
		alInner.add("12,280");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("12,280");
		alList.add(alInner);
		alInner.add("ADM CHARGES");
		alInner.add("-----");
		alInner.add("537");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("3");
		alInner.add("540");
		alList.add(alInner);
		alInner.add("INSP CHARGES");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("-----");
		alList.add(alInner);
		alInner.add("PENAL DAMAGES");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("-----");
		alList.add(alInner);
		alInner.add("MISC PAYMENT");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("-----");
		alInner.add("-----");
		
		alList.add(alInner);
		
		
		hmDetails.put("TOTAL_AC1","15,933");
		hmDetails.put("TOTAL_AC2","537");
		hmDetails.put("TOTAL_AC10","2,207");
		hmDetails.put("TOTAL_AC21","133");
		hmDetails.put("TOTAL_AC22","3");
		hmDetails.put("TOTAL","18,813");
		
		
	}
	public void generateChallanReports()
	{
		try {

						
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();
			
			String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\">COMBINED CHALLAN - A/C. NO. 1,2,10,21 &  22</font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\">STATE BANK OF INDIA</font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\"><b>EMPLOYEE'S PROVIDENT FUND ORGANISATION</b></font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\">(USE SEPRATE CHALLAN FOR EACH MONTH)</font></td></tr>" +
			"</table>";
			List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase.add(supList.get(0));
			document.add(phrase);
	
			document.add(new Paragraph(" "));
			
			String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">ESTABLISHMENT CODE NO: 25487963</font></td>" +
			"<td align=\"center\"><font size=\"1\">ACCOUNT GROUP NO 12</font></td><td></td>" +
			"<td align=\"left\"><font size=\"1\">PAID BY CHEQUE/CASH : CHEQUE</font></td>" +
			"</tr></table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase1.add(supList1.get(0));
			document.add(phrase1);
				
			
			String tbl2="<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr>" +
					"<td width=\"21%\"><font size=\"1\">DUES FOR THE MONTH OF :</font></td><td width=\"14%\" align=\"right\"><font size=\"1\">Employers Share :</font></td><td width=\"21%\">" +
					"<table><tr><td><font size=\"1\">06 2009</font></td></tr></table></td><td width=\"17%\" align=\"right\"><font size=\"1\">DATE OF PAYMENT :</font></td><td width=\"27%\"><font size=\"1\">15 06 2009</font></td></tr>" +
					"<tr><td><font size=\"1\"></font></td><td align=\"right\"><font size=\"1\">Employeee Share:</font></td><td><table><tr><td><font size=\"1\">06 2009</font></td></tr></table></td>" +
					"<td></td><td></td></tr></table>";
			List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
			Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase2.add(supList2.get(0));
			document.add(phrase2);
			
			
			String tbl3="<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td>" +
					"<table border=\"0\"><tr><td><font size=\"1\">Total No of Subscribers :</font></td></tr><tr><td>&nbsp;</td></tr><tr><td><font size=\"1\">Total Wages Due :</font></td></tr></table></td>"
			    +"<td><table border=\"0\"><tr><td></td><td><font size=\"1\">5</font></td></tr><tr><td><font size=\"1\">A/C 1{</font></td><td></td></tr><tr><td></td><td><font size=\"1\">48,833</font></td></tr></table></td>"
			    +"<td><table border=\"0\"><tr><td></td><td><font size=\"1\">5</font></td></tr><tr><td><font size=\"1\">A/C 10{</font></td><td></td></tr> <tr><td></td><td><font size=\"1\">26,500</font></td></tr></table></td>"
			   +"<td><table border=\"0\"><tr><td></td><td><font size=\"1\">5</font></td></tr><tr><td><font size=\"1\">A/C 21{</font></td><td></td></tr><tr><td></td><td><font size=\"1\">26,500</font></td></tr></table></td>"
			  +"</tr></table>";

			List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
			Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase3.add(supList3.get(0));
			document.add(phrase3);
			
//			document.add(new Paragraph(" "));
			String tbl5="<table width=\"100%\" border=\"1\" align=\"center\"><tr>" +
					"<td colspan=\"2\" align=\"left\"><font size=\"1\">S. No &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;PARTICULARS</font></td>" +
					"<td width=\"12%\"><font size=\"1\">A/c No 1</font></td><td width=\"13%\"><font size=\"1\">A/c No 2</font></td><td width=\"11%\"><font size=\"1\">A/c No 10</font></td>" +
					"<td width=\"11%\"><font size=\"1\">A/c No 21</font></td><td width=\"11%\"><font size=\"1\">A/c No 22</font></td><td width=\"15%\"><font size=\"1\">TOTAL</font></td>" +
					"</tr></table>";
			List<Element> supList5 = HTMLWorker.parseToList(new StringReader(tbl5), null);
			Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase5.add(supList5.get(0));
			document.add(phrase5);
	
			String tbl6="<table width=\"100%\" border=\"1\" align=\"center\"><tr><td colspan=\"2\">&nbsp;</td>" +
			"<td>&nbsp;</td><td>&nbsp;</td><td colspan=\"2\"><font size=\"1\">...Amount (in Rupees)...</font></td>" +
			"<td>&nbsp;</td><td>&nbsp;</td></tr></table>";
			List<Element> supList6 = HTMLWorker.parseToList(new StringReader(tbl6), null);
			Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase6.add(supList6.get(0));
			document.add(phrase6);
			
			String tbl7="<table width=\"100%\" border=\"1\" align=\"center\"><tr>" +
			"<td width=\"2%\" colspan=\"2\" align=\"left\"><font size=\"1\">PART -01</font></td><td width=\"23%\" rowspan=\"2\"></td>" +
			"<td width=\"12%\"></td><td width=\"13%\"></td><td width=\"11%\"></td>" +
			"<td width=\"11%\"></td><td width=\"11%\"></td>" +
			"</tr></table>";
			List<Element> supList7 = HTMLWorker.parseToList(new StringReader(tbl7), null);
			Phrase phrase7 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase7.add(supList7.get(0));
			document.add(phrase7);
			
			for(int i=0; i<alList.size(); i++){
				List alInner = (List)alList.get(i);
				if(alInner==null)alInner=new ArrayList();
			String tbl8="<table width=\"100%\" border=\"1\" align=\"center\"><tr>" +
			"<td width=\"2%\" colspan=\"2\" align=\"left\"><font size=\"1\">"+(i+1)+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+uF.showData((String)alInner.get(0), "")+"</font></td>" +
			"<td width=\"12%\"><font size=\"1\">"+uF.showData((String)alInner.get(1), "")+"</font></td>" +
			"<td width=\"13%\"><font size=\"1\">"+uF.showData((String)alInner.get(2), "")+"</font></td>" +
			"<td width=\"11%\"><font size=\"1\">"+uF.showData((String)alInner.get(3), "")+"</font></td>" +
			"<td width=\"11%\"><font size=\"1\">"+uF.showData((String)alInner.get(4), "")+"</font></td>" +
			"<td width=\"23%\" rowspan=\"2\"><font size=\"1\">"+uF.showData((String)alInner.get(5), "")+"</font></td>" +
			"<td width=\"11%\"><font size=\"1\">"+uF.showData((String)alInner.get(6), "")+"</font></td>" +
			"</tr></table>";
			List<Element> supList8 = HTMLWorker.parseToList(new StringReader(tbl8), null);
			Phrase phrase8 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase8.add(supList8.get(0));
			document.add(phrase8);
			}
			
			
			
			if(hmDetails==null)hmDetails=new HashMap();
			if(hmDetails!=null)
			{
			String tbl8="<table width=\"100%\" border=\"1\" align=\"center\"><tr>" +
			"<td width=\"2%\" colspan=\"2\" align=\"left\"><font size=\"1\"><b>TOTAL</b></font></td>" +
			"<td width=\"12%\"><font size=\"1\">"+uF.showData((String)hmDetails.get("TOTAL_AC1"), "")+"</font>" +
			"</td><td width=\"13%\"><font size=\"1\">"+uF.showData((String)hmDetails.get("TOTAL_AC2"), "")+"</font></td>" +
			"<td width=\"11%\"><font size=\"1\">"+uF.showData((String)hmDetails.get("TOTAL_AC10"), "")+"</font></td>" +
			"<td width=\"11%\"><font size=\"1\">"+uF.showData((String)hmDetails.get("TOTAL_AC21"), "")+"</font></td>" +
			"<td width=\"23%\" rowspan=\"2\"><font size=\"1\">"+uF.showData((String)hmDetails.get("TOTAL_AC22"), "")+"</font></td>" +
			"<td width=\"11%\"><font size=\"1\">"+uF.showData((String)hmDetails.get("TOTAL"), "")+"</font></td>" +
			"</tr></table>";
			List<Element> supList8 = HTMLWorker.parseToList(new StringReader(tbl8), null);
			Phrase phrase8 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase8.add(supList8.get(0));
			document.add(phrase8);
			}
			
			
			
			String tbl9="<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
			"<tr><td> <font size=\"1\">Amount in word:" +
			"_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ " +
			"_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ </font></td></tr></table>";
			
			List<Element> supList9 = HTMLWorker.parseToList(new StringReader(tbl9), null);
			Phrase phrase9 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase9.add(supList9.get(0));
			document.add(phrase9);
			
			
			String tbl10="<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
			"<tr><td> <font size=\"1\">NAME OF ESTABLISHMENT.. Paypac_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ </font></td><td></td>" +
			"<td> <font size=\"1\">(FOR BANK USE ONLY)</font></td></tr></table>";
			List<Element> supList10 = HTMLWorker.parseToList(new StringReader(tbl10), null);
			Phrase phrase10 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase10.add(supList10.get(0));
			document.add(phrase10);
			
			String tbl11="<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
			"<tr><td> <font size=\"1\">Address.. "+CF.getStrOrgAddress()+" </font></td><td></td>" +
			"<td align=\"left\"> <font size=\"1\">Amount Received Rs.------------</font></td></tr>" +
			"<tr><td> <font size=\"1\"></font></td><td></td>" +
			"<td align=\"left\"> <font size=\"1\">For Cheques Only</font></td></tr>" +
			"</table>";
			List<Element> supList11 = HTMLWorker.parseToList(new StringReader(tbl11), null);
			Phrase phrase11 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase11.add(supList11.get(0));
			document.add(phrase11);
			
			
			
			
			String tbl12="<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
			"<tr><td> <font size=\"1\">NAME OF THE DEPOSITOR ---------------------------------</font></td><td></td>" +
			"<td align=\"left\"> <font size=\"1\">Date of Presentation :------------</font></td></tr>" +
			"<tr><td> <font size=\"1\">SIGNATURE OF THE DEPOSITOR</font></td><td></td>" +
			"<td align=\"left\"> <font size=\"1\">Date of Realisation :------------</font></td></tr>" +
			"</table>";
			List<Element> supList12 = HTMLWorker.parseToList(new StringReader(tbl12), null);
			Phrase phrase12 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase12.add(supList12.get(0));
			document.add(phrase12);
			
			String tbl13="<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
			"<tr><td> <font size=\"1\"></font></td>" +
			"<td align=\"left\"> <font size=\"1\">(TO BE FILLED IN BY EMPLOYEE)</font></td><td align=\"left\"><font size=\"1\">Branch Name</font></td></tr>" +
			"<tr><td> <font size=\"1\"></font></td>" +
			"<td align=\"left\"> <font size=\"1\"></font></td><td align=\"left\"><font size=\"1\">Branch Code No.</font></td></tr>" +
			"</table>";
			List<Element> supList13 = HTMLWorker.parseToList(new StringReader(tbl13), null);
			Phrase phrase13 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase13.add(supList13.get(0));
			document.add(phrase13);
			
			
			String tbl14="<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
			"<tr><td> <font size=\"1\">NAME OF BANK: HDFC FORMAT</font></td>" +
			"<td align=\"left\"> <font size=\"1\">CHEQUE NO..:123456</font></td><td align=\"left\"><font size=\"1\">DATE: 15/06/2009</font></td></tr>" +
			"</table>";
			List<Element> supList14 = HTMLWorker.parseToList(new StringReader(tbl14), null);
			Phrase phrase14 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 9));
			phrase14.add(supList14.get(0));
			document.add(phrase14);
			
			
			
			document.close();
			
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=ChallanReports.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
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
