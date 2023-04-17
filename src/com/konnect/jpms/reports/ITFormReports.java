package com.konnect.jpms.reports;

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
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class ITFormReports implements ServletRequestAware,ServletResponseAware, IStatements {

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	
	String strUserType;
	String strSessionEmpId;
	String formType;
	String strMonth;
	String strSubmit;
	String financialYear;
	String strSelectedEmpId;
	String emp_id;
	double aggregateDeductionAmount;
	double TDSPaidAmount;
	double totalEducationCess;
	double totalStandardCess;
	double totalTax;
	double totalSalary;
	double totalTaxableAmount;
	double totalTaxFreeAmount;
//	List<FillMonth> monthList;
//	List<FillFinancialYears> financialYearList; 
	List<FillEmployee> empNamesList;
	CommonFunctions CF;
//	UtilityFunctions uF = new UtilityFunctions();
	
	String f_org;
	String f_level;
	
	public void execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		
		empNamesList = new FillEmployee(request).fillEmployeeName(null, null, session);

		if(getStrMonth()==null){
			setStrMonth("1");
		}
		if(getStrSelectedEmpId()==null && empNamesList!=null){
			setStrSelectedEmpId(empNamesList.get(0).getEmployeeId());
		}

		
		if(getFormType()!=null && getFormType().equals("form3A"))
		{
			viewForm3A(uF);
			generateForm3A(uF);
		}else if(getFormType()!=null && getFormType().equals("form10"))
		{
			viewForm10(uF);
			generateForm10(uF);
		}else if(getFormType()!=null && getFormType().equals("form5"))
		{
			viewForm5(uF);
			generateForm5(uF);
		}else if(getFormType()!=null && getFormType().equals("form6A"))
		{
			viewForm6A(uF);
			generateForm6A(uF);
		}
//		return "";
	}
	
	public Map<String, String> getDeductionDetailsMap(int empid, String strFinancialYearStart, String strFinancialYearEnd) {
		Map hmDeductionDetailsMap = new HashMap();

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);


			pst = con.prepareStatement("select distinct(section_code) as section_code,amount_paid from investment_details id," +
					"section_details sd where sd.section_id=id.section_id and fy_from=? and fy_to=? and status = true and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') and emp_id=? group by " +
					"section_code,amount_paid");
			
			
			pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,empid);
			rs = pst.executeQuery();
			
			int count = 0;
			while (rs.next()) {
				Map hmInner=new HashMap();
				aggregateDeductionAmount+=rs.getDouble("amount_paid");
				hmInner.put("AMOUNTPAID", rs.getString("amount_paid"));
				hmInner.put("SECTIONCODE", rs.getString("section_code"));
				hmDeductionDetailsMap.put("DECUCTION"+count,hmInner);
				count++;
			}
			rs.close();
			pst.close();
			
			Map hmInner=new HashMap();
			
			pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where financial_year_from_date=? and financial_year_to_date=? and emp_id = ? and salary_head_id =? and is_paid=true");
			pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, empid);
			pst.setInt(4, EMPLOYEE_EPF);
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				aggregateDeductionAmount+=rs.getDouble("amount");
				hmInner.put("AMOUNTPAID", rs.getString("amount"));
				hmInner.put("SECTIONCODE", "EPF");
				hmDeductionDetailsMap.put("DECUCTION"+count,hmInner);
				count++;
			}
			rs.close();
			pst.close();
			
			
			double dblInvestmentExemption = 0;
			pst = con.prepareStatement("select * from settings");
			rs = pst.executeQuery();
			while(rs.next()){
				if(rs.getString("options").equalsIgnoreCase("INVESTMENT_EXEMPTION")){
					dblInvestmentExemption = uF.parseToDouble(rs.getString("value"));
				}
			}
			rs.close();
			pst.close();
			
			
			
			aggregateDeductionAmount = Math.min(dblInvestmentExemption, aggregateDeductionAmount);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmDeductionDetailsMap;
	} 
	
	
	public Map<String, String> getOtherDetailsMap(int empid) {
		Map<String, String> hmOtherDetailsMap = new HashMap<String, String>();

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select org_tan_no,org_pan_no from org_details where org_id=(select org_id from employee_official_details where emp_id=?)");
			pst.setInt(1,empid);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOtherDetailsMap.put("DEDUCTOR_TAN", rs.getString("org_tan_no"));
				hmOtherDetailsMap.put("DEDUCTOR_PAN", rs.getString("org_pan_no"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select emp_pan_no from employee_personal_details where emp_per_id=?");
			pst.setInt(1, empid);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOtherDetailsMap.put("EMPLOYEE_PAN",rs.getString("emp_pan_no"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmOtherDetailsMap;
	} 
	
	public Map<String, Map<String, String>> getTaxPaidMap(int empid) {
		Map<String, Map<String, String>> hmTaxPaidMap = new HashMap<String, Map<String, String>>();

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
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
			
			double dbCess1 = 0;
			double dbCess2 = 0;
			pst = con.prepareStatement("Select * FROM settings");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getString("options").equalsIgnoreCase(O_STANDARD_CESS)){
					dbCess1 = uF.parseToDouble(rs.getString("value"));
				}
				if(rs.getString("options").equalsIgnoreCase(O_EDUCATION_CESS)){
					dbCess2 = uF.parseToDouble(rs.getString("value"));
				}
			}
			rs.close();
			pst.close();
		
			pst = con.prepareStatement("select amount,paid_date,challan_no,cheque_no,brc_code from challan_details where emp_id=? and is_paid=? and challan_type=? and "
							+ "financial_year_from_date=? and financial_year_to_date=?");
			pst.setInt(1, empid);
			pst.setBoolean(2, true);
			pst.setInt(3, TDS);
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			double eduCess=0.0;
			double stdCess=0.0;
			double TDSpaid=0.0;
			int count = 0;
			while (rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				totalTax += rs.getDouble("amount");
				
				double dblX = totalTax *100 / (100 + dbCess1 + dbCess2);
				
				
				
				eduCess=(dblX*dbCess2)/100;
				stdCess=(dblX*dbCess1)/100;
				
//				TDSpaid=rs.getDouble("amount")-eduCess - stdCess;
				TDSpaid = dblX;
				
				TDSPaidAmount+=TDSpaid;
				totalEducationCess+=eduCess;
				totalStandardCess+=stdCess;
				
				hmInner.put("TOTALTAX", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
				hmInner.put("EDUCESS", uF.formatIntoTwoDecimalWithOutComma(eduCess));
				hmInner.put("STDCESS", uF.formatIntoTwoDecimalWithOutComma(stdCess));
				hmInner.put("TDSPAID", uF.formatIntoTwoDecimalWithOutComma(TDSpaid));
				hmInner.put("PAIDDATE", rs.getString("paid_date"));
				hmInner.put("CHALLANNO", rs.getString("challan_no"));
				hmInner.put("CHEQUENO", rs.getString("cheque_no"));
				hmInner.put("BRSCODE", rs.getString("brc_code"));
				
				hmTaxPaidMap.put("TAXDETAILS" + count, hmInner);
				count++;
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmTaxPaidMap;
	} 
	public double getTaxAndSalaryDetails(){

		double total_salary = 0.0;

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
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

			
			pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where emp_id=? and " +
					"financial_year_from_date=? and financial_year_to_date=? and earning_deduction=? and is_paid = true");
			pst.setInt(1,uF.parseToInt(getEmp_id()));
			pst.setDate(2,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(4,"E");
			rs = pst.executeQuery();
			while (rs.next()) {
				total_salary=rs.getDouble("amount");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	
		return total_salary;
	}
	



	
	public double calculateTDS(double dblTotalTaxableSalary, double dblCess1, double dblCess2, String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, String strGender, String strAge,String slabType) {

		Connection con = null;
		PreparedStatement pst = null, pst1 = null;
		ResultSet rst = null, rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		double dblTotalTDSPayable = 0.0d;
		
		try {

			con = db.makeConnection(con);

			int countBug = 0;
			
			double dblUpperDeductionSlabLimit = 0;
			// double dbGrossSalarySlab = 0;

			do {
				pst = con.prepareStatement("select * from deduction_tax_details where age_from <= ? and age_to >= ? and gender = ? and _year = ?  and _from <= ? and _from>=?  and slab_type = ? order by _from limit 1");
				pst.setDouble(1, uF.parseToDouble(strAge));
				pst.setDouble(2, uF.parseToDouble(strAge));
				pst.setString(3, strGender);
				pst.setInt(4, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
				pst.setDouble(5, dblTotalTaxableSalary);
				pst.setDouble(6, dblUpperDeductionSlabLimit);
				pst.setInt(7,uF.parseToInt(slabType));

//				System.out.println(strAge + " = " + uF.parseToInt(strAge) + " strAge " + strEmpId + " Deduction PST==>" + pst);

				rs = pst.executeQuery();

				double dblDeductionAmount = 0;
				String strDeductionType = null;

				if (rs.next()) {
					dblDeductionAmount = rs.getDouble("deduction_amount");
					strDeductionType = rs.getString("deduction_type");
					dblUpperDeductionSlabLimit = rs.getDouble("_to");
				}
				rs.close();
				pst.close();

				// if(dbGrossSalarySlab==0){
				// dbGrossSalarySlab = dblTotalTaxableSalary;
				// }

				if (dblTotalTaxableSalary >= dblUpperDeductionSlabLimit) {
					dblTotalTDSPayable += ((dblDeductionAmount / 100) * dblUpperDeductionSlabLimit);
				} else {
					dblTotalTDSPayable += ((dblDeductionAmount / 100) * dblTotalTaxableSalary);
				}

				dblTotalTaxableSalary = dblTotalTaxableSalary - dblUpperDeductionSlabLimit;

//				System.out.println("dblTotalTaxableSalary=" + dblTotalTaxableSalary);
//				System.out.println("dblUpperDeductionSlabLimit=" + dblUpperDeductionSlabLimit);
//				System.out.println("dblTotalTDSPayable=" + dblTotalTDSPayable);
//				System.out.println("dblTotalTaxableSalary=" + dblTotalTaxableSalary);
//				System.out.println("dblDeductionAmount=" + dblDeductionAmount);

				// System.out.println("dblExemptions="+dblExemptions);
				// System.out.println("dblTotalGrossSalary="+dblTotalGrossSalary);
				// System.out.println("dblTotalTDSPayable="+dblTotalTDSPayable);
				// System.out.println("dbGrossSalarySlab="+dbGrossSalarySlab);
				// System.out.println("dblTDSMonth="+dblTDSMonth);

				if (countBug == 15)
					break; // in case of any bug, this condition is used to
							// avoid any stoppage
				countBug++;

			} while (dblTotalTaxableSalary > 0);

			// Service tax + Education cess

			double dblCess = dblTotalTDSPayable * (dblCess1 / 100);
			dblCess += dblTotalTDSPayable * (dblCess2 / 100);

			dblTotalTDSPayable += dblCess;


//			System.out.println("dblTotalGrossSalary=" + dblTotalTaxableSalary);
//			System.out.println("dblTotalTDSPayable=" + dblTotalTDSPayable);
//			System.out.println("dblTotalTDSPayable=" + dblTotalTDSPayable);
//			System.out.println("=============" + strEmpId + "=================");

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return dblTotalTDSPayable;

	}
	
	
	public void generateForm10(UtilityFunctions uF){
		try {

			String  strMonth ="";
			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			List alList = (List)request.getAttribute("alList");
			strMonth = (String)request.getAttribute("strMonth");
			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}
			Map<String, String>  hmOrg = (Map<String, String>)request.getAttribute("hmOrg");
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, buffer);
			document.open();
					
			String img = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\"><b>Form 10</b></font></td></tr>" +
					"<tr><td align=\"center\"><font size=\"1\"><b>THE EMPLOYEES' PROVIDENT FUND SCHEME, 1952 [PARA 36(2)(a)&(b)]</b></font></td></tr>" +
					"<tr><td align=\"center\"><font size=\"1\"><b>AND THE EMPLOYEES' PENSION SCHEME, 1995 [Para - 20 (2)]</b></font></td></tr>" +
					"</table>";
			List<Element> supList = HTMLWorker.parseToList(new StringReader(img), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);
			
			String h = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
			"<tr><td colspan=\"3\"><font size=\"1\">Return of members leaving service during the month of "+((strMonth!=null)?strMonth.toUpperCase():"")+"</font></td></tr>" +
			"<tr><td valign=\"top\"><font size=\"1\">Name and address of the establishment :</font></td>" +
			"<td><font size=\"1\">"+uF.showData(hmOrg.get("ORG_NAME"), "")+"</font><br/>" +
			"<font size=\"1\">"+uF.showData(hmOrg.get("ORG_ADDRESS"), "")+"</font>" +
			"<td><font size=\"1\"></font></td></tr>" +
			"<tr><td></td><td></td><td><font size=\"1\">Code No: "+uF.showData(hmOrg.get("ORG_ESTABLISH_CODE_NO"), "")+"</font></td></tr></table>";
			List<Element> supList0 = HTMLWorker.parseToList(new StringReader(h), null);
			Phrase phrase0 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase0.add(supList0.get(0));
			document.add(phrase0);
			
			document.add(new Paragraph(" "));
			
			String tbl="<table width=\"100%\" border=\"1\"><tr><td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Sr. No.<br/>(1)</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Account No.<br/>(2)</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Name of the Member<br/>(3)</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Father's Name(or Husband's name in case of married woman)<br/>(4)</font></td>" +
			"<td align=\"center\"><font size=\"1\">Date of Leaving service<br/>(5)</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Reason of leaving service<br/>(6)</font></td>" +
			"<td width=\"30%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Remarks<br/>(7)</font></td></tr>" +
			"</table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase1.add(supList1.get(0));
			document.add(phrase1);
	
			for(int i=0; alList!=null && i<alList.size(); i++){
				List alInner = (List)alList.get(i);
				if(alInner==null)alInner=new ArrayList();
				String tbldate="<table border=\"1\"><tr><td align=\"center\"><font size=\"1\">"+(i+1)+"</font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\">"+uF.showData((String)alInner.get(0), "")+"</font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\">"+uF.showData((String)alInner.get(1), "")+"</font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\">"+uF.showData((String)alInner.get(2), "")+"</font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\">"+uF.showData((String)alInner.get(3), "")+"</font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\">"+uF.showData((String)alInner.get(4), "")+"</font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\">"+uF.showData((String)alInner.get(5), "")+"</font></td>" +
				"</tr></table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbldate), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
			}
			if(alList.size()==0){
				String total="<table border=\"1\"><tr><td align=\"center\" ><font size=\"1\"><b>No Employee found</b></font></td>" +
				"</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(total), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}
			
			document.add(new Paragraph(" "));
			
			String tbl6="<table border=\"0\"><tr><td align=\"left\" ><font size=\"1\">Date</font></td><td align=\"right\" ><font size=\"1\">Signature of Employer or other authorised officer<br/>Stamp of the Factory/Establishment</font></td>" +
			"</tr></table>";
			List<Element> supList6 = HTMLWorker.parseToList(new StringReader(tbl6), null);
			Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase6.add(supList6.get(0));
			document.add(phrase6);
			
			String tbl5= "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
			"<tr><td width=\"60%\"><font size=\"1\">*Please state whether the member is (a) retaining according to para 69(1)(a) or (b)of the scheme (b) leaving india for parmenent settlement abroad</font></td></tr>" +
			"<tr><td><font size=\"1\">(c) retrenchment (d) Pt. & total disablement due to employment injury (e)discharged (f) resiging from or leaving service (g) taking up employment</font></td></tr>" +
			"<tr><td><font size=\"1\">else where(the name and address of the new employer shuld be stated) (h)death (i) atained the age of 58 years.</font></td></tr>" +
			"</table>";			
			List<Element> supList5 = HTMLWorker.parseToList(new StringReader(tbl5), null);
			Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase5.add(supList5.get(0));
			document.add(phrase5);
			
			document.add(new Paragraph(" "));
			
			String tbl7= "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
			"<tr><td width=\"60%\"><font size=\"1\">\"Certified that the member mentioned at serial No..................." +
			"Shri................................................................................................" +
			"was paid/not paid retrenchment compensation of Rs......................................" +
			"under the Industrial Dispute Act. 1947\"</font></td></tr>" +
			"</table>";			
			List<Element> supList7 = HTMLWorker.parseToList(new StringReader(tbl7), null);
			Phrase phrase7 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase7.add(supList7.get(0));
			document.add(phrase7);
			
			
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));
			
			String tbl8="<table border=\"0\"><tr><td align=\"right\" ><font size=\"1\">Signature of the Employer</font></td>" +
			"</tr></table>";
			List<Element> supList8 = HTMLWorker.parseToList(new StringReader(tbl8), null);
			Phrase phrase8 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase8.add(supList8.get(0));
			document.add(phrase8);
			
			
			document.close();
			
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=Form10Reports.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
		public void viewForm10(UtilityFunctions uF){
			
			Connection con = null;
			PreparedStatement pst=null;
			ResultSet rs = null;
			Database db = new Database();
			db.setRequest(request);

			try {
				
				String[] strPayCycleDates = null;
				String strFinancialYearStart = null;
				String strFinancialYearEnd = null;
				String strMonth = null;
				
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
				

				int nselectedMonth = uF.parseToInt(getStrMonth());
				int nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"));
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
				if(nselectedMonth>=nFYSMonth){
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
				}else{
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy")));
				}
				
				
				int nMonthStart = cal.getActualMinimum(Calendar.DATE);
				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
				
				String strDateStart =  nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
				String strDateEnd =  nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
				
				strMonth = uF.getDateFormat(strDateStart, DATE_FORMAT, "MMMM - yyyy");			
				
				
				
				List<List<String>> alList = new ArrayList<List<String>>();
				
				con = db.makeConnection(con);
				
				pst = con.prepareStatement("select * from org_details where org_id = ? ");
				pst.setInt(1, uF.parseToInt(getF_org()));
				rs = pst.executeQuery();
				Map<String, String> hmOrg=new HashMap<String, String>();
				while (rs.next()) {
					hmOrg.put("ORG_ID", rs.getString("org_id"));
					hmOrg.put("ORG_NAME", rs.getString("org_name"));
					hmOrg.put("ORG_LOGO", rs.getString("org_logo"));
					hmOrg.put("ORG_ADDRESS", rs.getString("org_address"));
					hmOrg.put("ORG_PINCODE", rs.getString("org_pincode"));
					hmOrg.put("ORG_CONTACT", rs.getString("org_contact1"));
					hmOrg.put("ORG_EMAIL", rs.getString("org_email"));
					hmOrg.put("ORG_STATE_ID", rs.getString("org_state_id"));
					hmOrg.put("ORG_COUNTRY_ID", rs.getString("org_country_id"));
					hmOrg.put("ORG_CITY", rs.getString("org_city"));
					hmOrg.put("ORG_CODE", rs.getString("org_code"));
					hmOrg.put("ORG_DISPLAY_PAYCYCLE", rs.getString("display_paycycle"));
					hmOrg.put("ORG_DURATION_PAYCYCLE", rs.getString("duration_paycycle"));
					hmOrg.put("ORG_SALARY_CAL_BASIS", rs.getString("salary_cal_basis"));
					hmOrg.put("ORG_START_PAYCYCLE",uF.getDateFormat(rs.getString("start_paycycle"), DBDATE, DATE_FORMAT) );
					hmOrg.put("ORG_ESTABLISH_CODE_NO", rs.getString("establish_code_no"));
				}
				rs.close();
				pst.close();  
				
				pst = con.prepareStatement("select * from emp_family_members where emp_id in (select epd.emp_per_id from employee_personal_details epd," +
						"employee_official_details eod  where epd.emp_per_id=eod.emp_id and employment_end_date between ? and ? and eod.org_id=?) order by emp_id");
				pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getF_org()));
				rs= pst.executeQuery();
//				System.out.println("pst=== 1 ===>"+pst);
				Map<String, String> hmEmpFamilyDetails = new HashMap<String, String>();
				while(rs.next()){
					if(rs.getString("member_type")!=null && rs.getString("member_type").equalsIgnoreCase("SPOUSE")){
						hmEmpFamilyDetails.put(rs.getString("emp_id")+"_SPOUSE", rs.getString("member_name"));
					}else if(rs.getString("member_type")!=null && rs.getString("member_type").equalsIgnoreCase("FATHER")){
						hmEmpFamilyDetails.put(rs.getString("emp_id")+"_FATHER", rs.getString("member_name"));
					}
				}
				rs.close();
				pst.close();
				
				
				Map<String, String> hmOffBoardReason = new HashMap<String, String>();
				pst = con.prepareStatement("select * from emp_off_board where emp_id in (select epd.emp_per_id from employee_personal_details epd," +
						"employee_official_details eod  where epd.emp_per_id=eod.emp_id and employment_end_date between ? and ?   and eod.org_id=?)");
				pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getF_org()));
//				System.out.println("pst=== 2 ===>"+pst);
				rs= pst.executeQuery();
				while(rs.next()){
					hmOffBoardReason.put(rs.getString("emp_id"), rs.getString("emp_reason"));	
				}
				rs.close();
				pst.close();
				
				

				StringBuilder sbQuery = new StringBuilder();
				
				sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where eod.emp_id = epd.emp_per_id");
				sbQuery.append(" and employment_end_date between ? and ?  and eod.org_id=? order by emp_fname, emp_lname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getF_org()));
//				System.out.println("pst=== 3 ===>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					List<String> alInner = new ArrayList<String>();
					
					alInner.add(rs.getString("emp_pf_no"));
					alInner.add(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
					if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("M")){
						alInner.add(hmEmpFamilyDetails.get(rs.getString("emp_per_id")+"_FATHER"));
					}else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("F") && rs.getString("marital_status")!=null && rs.getString("marital_status").equalsIgnoreCase("M")){
						alInner.add(hmEmpFamilyDetails.get(rs.getString("emp_per_id")+"_SPOUSE"));
					}else{
						alInner.add(hmEmpFamilyDetails.get(rs.getString("emp_per_id")+"_FATHER"));
					}
					
					
					alInner.add(uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, CF.getStrReportDateFormat()));
					alInner.add(uF.showData((String)hmOffBoardReason.get(rs.getString("emp_per_id")), ""));
					alInner.add("");
					
					alList.add(alInner);
					
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmOrg", hmOrg);
				request.setAttribute("alList", alList);
				request.setAttribute("strFinancialYearStart", strFinancialYearStart);
				request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
				request.setAttribute("strMonth", strMonth);
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
			
	}
	
	
	public void generateForm5(UtilityFunctions uF){
		try {


			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			String  strMonth = (String)request.getAttribute("strMonth");
			
			Map<String, String>  hmOrg = (Map<String, String>)request.getAttribute("hmOrg");
			if(hmOrg == null) hmOrg = new HashMap<String, String>();

			String  strFinancialYearStart1 = null;
			String  strFinancialYearEnd1 = null;
			List alList = (List)request.getAttribute("alList");
			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart1 = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd1 = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();
					
			String img = "<table><tr><td><table border=\"1\"><tr><td><table border=\"0\"><tr><td><font size=\"1\">Name and address of the establishment </font></td></tr><tr><td><font size=\"1\"><b>"+uF.showData(hmOrg.get("ORG_NAME"), "")+"</b></font></td></tr>" +
					"</tr><tr><td><font size=\"1\"><b>"+uF.showData(hmOrg.get("ORG_ADDRESS"), "")+"</b></font></td></tr>" +
					"</table></td></tr><tr><td><font size=\"1\">Code of establishment : <b>"+uF.showData(hmOrg.get("ORG_ESTABLISH_CODE_NO"), "")+"</b></font></td></tr></table></td><td><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\"><b>Form 5</b></font></td></tr>" +
					"<tr><td align=\"center\"><font size=\"1\"><b>THE EMPLOYEES' PROVIDENT FUND SCHEME, 1952[PARA 36(2)(A)] </b></font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\"><b>AND THE EMPLOYEE PENSION SCHEME, 1995[PARA 20(4)]</b> </font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\">Return of Employees qualifying for membership of the employee's Provident Fund,</font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\">Employee's Pension Fund & Employee's Deposit Linked Insurance Fund for the first time </font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\">during the month of "+((strMonth!=null)?strMonth:"-")+"</font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\">[To be sent to the commissioner with Form 2 (EPF & EPS)]</font></td></tr>" +
			"</table></td></tr></table>";
			List<Element> supList = HTMLWorker.parseToList(new StringReader(img), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);
			
			document.add(new Paragraph(" "));
			
			String tbl="<table width=\"100%\" border=\"1\"><tr><td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Sr. No.<br/>(1)</font></td>" +
					"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Account Number<br/>(2)</font></td>" +
					"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Name of the Employee (In block letters)<br/>(3)</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Father's Name (or Husband's name in case of Married women)<br/>(4)</font></td>" +
			"<td align=\"center\"><font size=\"1\">Date of Birth<br/>(5)</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Sex<br/>(6)</font></td>" +
			"<td align=\"center\" colspan=\"2\"><font size=\"1\">Date of Joining the Fund<br/>(7)</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Date period of previous services as on the date of joining the Fund(enclose Scheme Certificate if applicable)<br/>(8)</font></td>" +
			"<td width=\"30%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Remarks<br/>(9)</font></td></tr>" +
			"</table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase1.add(supList1.get(0));
			document.add(phrase1);
	
			for(int i=0; i<alList.size(); i++){
				List alInner = (List)alList.get(i);
				if(alInner==null)alInner=new ArrayList();
				String tbldate="<table border=\"1\"><tr><td align=\"center\"><font size=\"1\">"+(i+1)+"</font></td>" +
				"<td width=\"10%\" align=\"left\" nowrap=\"nowrap\"><font size=\"1\">"+uF.showData((String)alInner.get(0), "")+"</font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\">"+uF.showData((String)alInner.get(1), "")+"</font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\">"+uF.showData((String)alInner.get(2), "")+"</font></td>" +
				"<td width=\"10%\" align=\"center\"><font size=\"1\">"+uF.showData((String)alInner.get(3), "")+"</font></td>" +
				"<td width=\"10%\" align=\"center\"><font size=\"1\">"+uF.showData((String)alInner.get(4), "")+"</font></td>" +
				"<td align=\"center\" colspan=\"2\"><font size=\"1\">"+uF.showData((String)alInner.get(5), "")+"</font></td>" +
				"<td width=\"10%\" align=\"center\"><font size=\"1\">"+uF.showData((String)alInner.get(6), "")+"</font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\">"+uF.showData((String)alInner.get(7), "")+"</font></td>" +
						"" +
				"</tr></table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbldate), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
	}
			if(alList.size()==0){
				String tblblank="<table border=\"1\"><tr>" +
				"<td width=\"10%\" align=\"center\"><font size=\"1\">No Employee found</font></td>" +
				"</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tblblank), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
				
			}
			
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));
			
			String tbl6="<table border=\"0\"><tr><td align=\"left\" ><font size=\"1\">Date &nbsp;&nbsp;&nbsp;&nbsp;30/07/2009</font></td><td align=\"right\" ><font size=\"1\">Signature of Employer or other authorised officer/Stamp of <br/> the Factory/Establishment</font></td>" +
			"</tr></table>";
			List<Element> supList6 = HTMLWorker.parseToList(new StringReader(tbl6), null);
			Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase6.add(supList6.get(0));
			document.add(phrase6);
			
			document.close();
			
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=Form5Reports.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
public void viewForm5(UtilityFunctions uF){
	
	Connection con = null;
	PreparedStatement pst=null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);

	try {
		
		String[] strPayCycleDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		String strMonth = null;

		
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

		int nselectedMonth = uF.parseToInt(getStrMonth());
		int nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"));
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
		if(nselectedMonth>=nFYSMonth){
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
		}else{
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy")));
		}
		
		
		int nMonthStart = cal.getActualMinimum(Calendar.DATE);
		int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
		
		String strDateStart =  nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
		String strDateEnd =  nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
		
		strMonth = uF.getDateFormat(strDateStart, DATE_FORMAT, "MMMM - yyyy");
		
		
		
		List<List<String>> alList = new ArrayList<List<String>>();
		Map<String, String> hmEmpFamilyDetails = new HashMap<String, String>();
		Map<String, String> hmEmpPrevEmploymentDetails = new HashMap<String, String>();
		
		con = db.makeConnection(con);
		
		pst = con.prepareStatement("select * from org_details where org_id = ? ");
		pst.setInt(1, uF.parseToInt(getF_org()));
		rs = pst.executeQuery();
		Map<String, String> hmOrg=new HashMap<String, String>();
		while (rs.next()) {
			hmOrg.put("ORG_ID", rs.getString("org_id"));
			hmOrg.put("ORG_NAME", rs.getString("org_name"));
			hmOrg.put("ORG_LOGO", rs.getString("org_logo"));
			hmOrg.put("ORG_ADDRESS", rs.getString("org_address"));
			hmOrg.put("ORG_PINCODE", rs.getString("org_pincode"));
			hmOrg.put("ORG_CONTACT", rs.getString("org_contact1"));
			hmOrg.put("ORG_EMAIL", rs.getString("org_email"));
			hmOrg.put("ORG_STATE_ID", rs.getString("org_state_id"));
			hmOrg.put("ORG_COUNTRY_ID", rs.getString("org_country_id"));
			hmOrg.put("ORG_CITY", rs.getString("org_city"));
			hmOrg.put("ORG_CODE", rs.getString("org_code"));
			hmOrg.put("ORG_DISPLAY_PAYCYCLE", rs.getString("display_paycycle"));
			hmOrg.put("ORG_DURATION_PAYCYCLE", rs.getString("duration_paycycle"));
			hmOrg.put("ORG_SALARY_CAL_BASIS", rs.getString("salary_cal_basis"));
			hmOrg.put("ORG_START_PAYCYCLE",uF.getDateFormat(rs.getString("start_paycycle"), DBDATE, DATE_FORMAT) );
			hmOrg.put("ORG_ESTABLISH_CODE_NO", rs.getString("establish_code_no"));
		}
		rs.close();
		pst.close();  
		
		pst = con.prepareStatement("select * from emp_prev_employment where emp_id in (select epd.emp_per_id from employee_personal_details epd," +
				"employee_official_details eod  where epd.emp_per_id=eod.emp_id and epd.joining_date between ? and ? and eod.org_id=?) order by emp_id, to_date desc");
		pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(getF_org()));
		rs= pst.executeQuery();
//		System.out.println("pst=== 1 ===>"+pst);
		String strEmpIdNew=null;
		String strEmpIdOld=null;
		while(rs.next()){
			strEmpIdNew = rs.getString("emp_id");
			if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
				hmEmpPrevEmploymentDetails.put(rs.getString("emp_id"), rs.getString("to_date"));
			}
			strEmpIdOld = strEmpIdNew;
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select * from emp_family_members where emp_id in (select epd.emp_per_id from employee_personal_details epd," +
				"employee_official_details eod  where epd.emp_per_id=eod.emp_id and epd.joining_date between ? and ? and eod.org_id=?) order by emp_id");
		pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(getF_org()));
		rs= pst.executeQuery();
//		System.out.println("pst=== 2 ===>"+pst);
		while(rs.next()){
			if(rs.getString("member_type")!=null && rs.getString("member_type").equalsIgnoreCase("SPOUSE")){
				hmEmpFamilyDetails.put(rs.getString("emp_id")+"_SPOUSE", rs.getString("member_name"));
			}else if(rs.getString("member_type")!=null && rs.getString("member_type").equalsIgnoreCase("FATHER")){
				hmEmpFamilyDetails.put(rs.getString("emp_id")+"_FATHER", rs.getString("member_name"));
			}
			
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select * from employee_personal_details epd,employee_official_details eod " +
				"where epd.emp_per_id=eod.emp_id and epd.joining_date between ? and ? and eod.org_id=?");
		pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(getF_org()));
//		System.out.println("pst====3====="+pst);
		rs = pst.executeQuery();
		while(rs.next()){
			List<String> alInner = new ArrayList<String>();
			
			alInner.add(rs.getString("emp_pf_no"));
			alInner.add(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
			if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("M")){
				alInner.add(hmEmpFamilyDetails.get(rs.getString("emp_per_id")+"_FATHER"));
			}else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("F") && rs.getString("marital_status")!=null && rs.getString("marital_status").equalsIgnoreCase("M")){
				alInner.add(hmEmpFamilyDetails.get(rs.getString("emp_per_id")+"_SPOUSE"));
			}else{
				alInner.add(hmEmpFamilyDetails.get(rs.getString("emp_per_id")+"_FATHER"));
			}
			
			alInner.add(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, CF.getStrReportDateFormat()));
			alInner.add(rs.getString("emp_gender"));
			alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
			alInner.add(uF.getDateFormat(hmEmpPrevEmploymentDetails.get(rs.getString("emp_per_id")), DBDATE, CF.getStrReportDateFormat()));
			alInner.add("");
			
			alList.add(alInner);
			
		}
		rs.close();
		pst.close();
		
		request.setAttribute("hmOrg", hmOrg);
		request.setAttribute("alList", alList);
		request.setAttribute("strFinancialYearStart", strFinancialYearStart);
		request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
		request.setAttribute("strMonth", strMonth);
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	
}
	
	public void generateForm6A(UtilityFunctions uF){
		try {

			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			Map  hmDetails = (Map)request.getAttribute("hmDetails");
			Map  hmDetailsTotal = (Map)request.getAttribute("hmDetailsTotal");
			Map  hmEmployeeDetails = (Map)request.getAttribute("hmEmployeeDetails");

			Map<String, String>  hmEarningTotal = (Map<String, String>)request.getAttribute("hmEarningTotal");
			if(hmEarningTotal == null) hmEarningTotal = new HashMap<String, String>();

			Map<String, String>  hmOrg = (Map<String, String>)request.getAttribute("hmOrg");
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			Map<String, String>  hmReconcilation = (Map<String, String>)request.getAttribute("hmReconcilation");
			if(hmReconcilation == null) hmReconcilation = new HashMap<String, String>();

			Map<String, String>  hmReconcilationTotal = (Map<String, String>)request.getAttribute("hmReconcilationTotal");
			if(hmReconcilationTotal == null) hmReconcilationTotal = new HashMap<String, String>();
			
			 
			 
			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}
			
	
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open(); 
					
			String img = "<table><tr><td><table border=\"1\"><tr><td><table border=\"0\"><tr><td valign=\"top\"><font size=\"1\">Name and address of the establishment :</font></td></tr><tr><td><font size=\"1\"><b>"+uF.showData(hmOrg.get("ORG_NAME"), "0")+"</b></font></td></tr>" +
					"</tr><tr><td><font size=\"1\"><b>"+uF.showData(hmOrg.get("ORG_ADDRESS"), "0")+"</b></font></td></tr>" +
					"</table></td></tr><tr><td><font size=\"1\">Code of establishment : <b>"+uF.showData(hmOrg.get("ORG_ESTABLISH_CODE_NO"), "")+"</b></font></td></tr></table></td><td><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\"><b>Form 6A (Revised)</b></font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\"><b>(For unexempted Establishment Only)</b></font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\">The Employees' Provident Fund Scheme, 1952[Para 43] </font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\">And</font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\">Annual statement of contribution for the currency period from "+strFinancialYearStart+" To "+strFinancialYearEnd+"</font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\"><b>THE EMPLOYEE PENSIONSCHEME, 1995[PARA 20(4)]</b></font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\"><b>Statutory rate of contribution 12%</b></font></td></tr>" +
			"<tr><td align=\"center\"><font size=\"1\"><b>No of members voluntarily contributing at higher rate</b></font></td></tr>" +
			"</table></td></tr></table>";
			List<Element> supList = HTMLWorker.parseToList(new StringReader(img), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);
			
			document.add(new Paragraph(" "));
			
			String tbl="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"center\"><font size=\"1\"><b>Sr. No.<br/>(1)</b></font></td>" +
					"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\"><b>Account Number<br/>(2)</font></td>" +
					"<td width=\"18%\" rowspan=\"2\" align=\"center\"><font size=\"1\"><b>Name of the Member (In block letters)<br/>(3)</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\"><b>Wages, Retaining allowance (if any) & D.A. including cash value of food concession paid during currency period<br/>(4)</font></td>" +
			"<td align=\"center\"><font size=\"1\"><b>Amount of workers contributions deducted from the wages<br/>(5)</font></td>" +
			"<td align=\"center\" colspan=\"2\"><table border=\"0\"><tr><td><font size=\"1\"><b>Employer's Contribution</font></td></tr>" +
			"<tr><td><table border=\"0\"><tr><td><font size=\"1\"><b>E.P.F. difference between "+uF.showData((String)request.getAttribute("EPF_PER"), "0")+"% & "+uF.showData((String)request.getAttribute("ERPS_PER"), "0")+"%<br/>(6)</td><td>Pension Fund "+uF.showData((String)request.getAttribute("ERPS_PER"), "0")+"%<br/>(7)</font></td></tr></table></td></tr></table></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\"><b>Refund of Adv.<br/>(8)</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\"><b>Rate of higher voluntary contribution(if any)<br/>(9)</font></td>" +
			"<td width=\"30%\" rowspan=\"2\" align=\"center\"><font size=\"1\"><b>Remarks<br/>(10)</font></td></tr>" +
			"</table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase1.add(supList1.get(0));
			document.add(phrase1);
	
			Set set = hmDetails.keySet();
			Iterator it = set.iterator();
			int count=0;
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				Map hmInner = (Map)hmDetails.get(strEmpId);
				if(hmInner==null)hmInner=new HashMap();
				
				Map hmInnerEmpDetails = (Map)hmEmployeeDetails.get(strEmpId);
				if(hmInnerEmpDetails==null)hmInnerEmpDetails=new HashMap();
				
				String tbldate="<table border=\"1\"><tr><td align=\"center\"><font size=\"1\">"+(++count)+"</font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\">"+uF.showData((String)hmInnerEmpDetails.get("EPF_ACC_NO"),"")+"</font></td>" +
				"<td width=\"20%\" align=\"left\"><font size=\"1\">"+uF.showData((String)hmInnerEmpDetails.get("NAME"),"")+"</font></td>" +
				"<td width=\"10%\" align=\"right\"><font size=\"1\">"+uF.showData((String)hmEarningTotal.get(strEmpId),"")+"</font></td>" +
				"<td width=\"10%\" align=\"right\"><font size=\"1\">"+uF.showData((String)hmInner.get("EMPLOYEE_SHARE"),"")+"</font></td>" +
				"<td width=\"10%\" align=\"right\"><font size=\"1\">"+uF.showData((String)hmInner.get("EMPLOYER_SHARE_EPF"),"")+"</font></td>" +
				"<td width=\"10%\" align=\"right\"><font size=\"1\">"+uF.showData((String)hmInner.get("EMPLOYER_SHARE_EPS"),"")+"</font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\"></font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\"></font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\"></font></td>" +
				"</tr></table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbldate), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
	}
			
			String total="<table border=\"1\"><tr><td><b></b></td>" +
			"<td width=\"10%\" align=\"left\"><b></b></td>" +
			"<td width=\"10%\" align=\"left\"><b>Total</b></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"><b>"+uF.showData((String)hmDetailsTotal.get("TOTAL_EARNING"),"") +"</b></font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"><b>"+uF.showData((String)hmDetailsTotal.get("TOTAL_EMPLOYEE_SHARE"),"")+"</b></font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"><b>"+uF.showData((String)hmDetailsTotal.get("TOTAL_EMPLOYER_SHARE_EPF"),"")+"</b></font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"><b>"+uF.showData((String)hmDetailsTotal.get("TOTAL_EMPLOYER_SHARE_EPS"),"")+"</b></font></td>" +
			"<td width=\"10%\" align=\"left\"><b></b></td>" +
			"<td width=\"10%\" align=\"left\"><b></b></td>" +
			"<td width=\"10%\" align=\"left\"><b></b></td>" +
			"</tr></table>";
			List<Element> supList3 = HTMLWorker.parseToList(new StringReader(total), null);
			Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase3.add(supList3.get(0));
			document.add(phrase3);
			
			document.add(new Paragraph(" "));
			
			document.newPage();
			
			String tbl4 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
				"<tr><td align=\"center\"><font size=\"1\">RECONCILATION OF REMITTANCES</b></font></td></tr>" +
				"" +
				"</table>";
			List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
			Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase4.add(supList4.get(0));
			document.add(phrase4);
			
			document.add(new Paragraph(" "));
			
			String tbl5="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Sr. No.<br/>(1)</font></td>" +
					"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Months<br/>(2)</font></td>" +
					"<td width=\"18%\" align=\"center\" colspan=\"2\">" +
					"<table border=\"0\"><tr><td><font size=\"1\">Amount Remitted</font></td></tr>" +
			"<tr><td><table border=\"0\"><tr><td><font size=\"1\">E.P.F. contribution including refund of advance A/C No. 1<br/>(Rs)</td>" +
			"<td><font size=\"1\">Pension Fund contribution A/C No.10<br/>(Rs)</font></td></tr></table></td></tr></table>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">E.D.L.I Contribution A/C No. 21<br/>(Rs)</font></td>" +
			"<td align=\"center\"><font size=\"1\">Admin Charges A/C No. 2<br/>(Rs)</font></td>" +
			"<td align=\"center\" colspan=\"2\"><font size=\"1\">E.D.L.I Admin Charges A/C No. 22<br/>(Rs)</font></td></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Aggregate Contribution of Cols.5.6.7<br/>(Rs)</font></td>" +
			"</tr>" +
			"</table>";
			List<Element> supList5 = HTMLWorker.parseToList(new StringReader(tbl5), null);
			Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase5.add(supList5.get(0));
			document.add(phrase5);
					
			String tbl6="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"right\"><font size=\"1\">1</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"left\"><font size=\"1\">April</font></td>" +
			"<td width=\"18%\" align=\"right\">" +
			"<font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERPF_4"),"") +"</font></td>" +
			"<td width=\"10%\"align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EEPS_4"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERDLI_4"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ADMIN_4"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\" colspan=\"2\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EDLI_ADMIN_4"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"></font></td>" +
			"</tr>" +
			"</table>";
			List<Element> supList6 = HTMLWorker.parseToList(new StringReader(tbl6), null);
			Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase6.add(supList6.get(0));
			document.add(phrase6);
			
			String tbl7="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"right\"><font size=\"1\">2</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"left\"><font size=\"1\">May</font></td>" +
			"<td width=\"18%\" align=\"right\">" +
			"<font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERPF_5"),"") +"</font></td>" +
			"<td width=\"10%\"align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EEPS_5"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERDLI_5"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ADMIN_5"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\" colspan=\"2\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EDLI_ADMIN_5"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"></font></td>" +
			"</tr>" +
			"</table>";
			List<Element> supList7 = HTMLWorker.parseToList(new StringReader(tbl7), null);
			Phrase phrase7 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase7.add(supList7.get(0));
			document.add(phrase7);
			
			
			String tbl8="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"right\"><font size=\"1\">3</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"left\"><font size=\"1\">June</font></td>" +
			"<td width=\"18%\" align=\"right\">" +
			"<font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERPF_6"),"") +"</font></td>" +
			"<td width=\"10%\"align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EEPS_6"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERDLI_6"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ADMIN_6"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\" colspan=\"2\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EDLI_ADMIN_6"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"></font></td>" +
			"</tr>" +
			"</table>";
			List<Element> supList8 = HTMLWorker.parseToList(new StringReader(tbl8), null);
			Phrase phrase8 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase8.add(supList8.get(0));
			document.add(phrase8);
			
			
			String tbl9="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"right\"><font size=\"1\">4</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"left\"><font size=\"1\">July</font></td>" +
			"<td width=\"18%\" align=\"right\">" +
			"<font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERPF_7"),"") +"</font></td>" +
			"<td width=\"10%\"align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EEPS_7"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERDLI_7"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ADMIN_7"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\" colspan=\"2\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EDLI_ADMIN_7"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"></font></td>" +
			"</tr>" +
			"</table>";
			List<Element> supList9 = HTMLWorker.parseToList(new StringReader(tbl9), null);
			Phrase phrase9 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase9.add(supList9.get(0));
			document.add(phrase9);
			
			String tbl10="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"right\"><font size=\"1\">5</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"left\"><font size=\"1\">August</font></td>" +
			"<td width=\"18%\" align=\"right\">" +
			"<font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERPF_8"),"") +"</font></td>" +
			"<td width=\"10%\"align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EEPS_8"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERDLI_8"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ADMIN_8"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\" colspan=\"2\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EDLI_ADMIN_8"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"></font></td>" +
			"</tr>" +
			"</table>";
			List<Element> supList10 = HTMLWorker.parseToList(new StringReader(tbl10), null);
			Phrase phrase10 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase10.add(supList10.get(0));
			document.add(phrase10);
			
			String tbl11="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"right\"><font size=\"1\">6</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"left\"><font size=\"1\">September</font></td>" +
			"<td width=\"18%\" align=\"right\">" +
			"<font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERPF_9"),"") +"</font></td>" +
			"<td width=\"10%\"align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EEPS_9"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERDLI_9"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ADMIN_9"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\" colspan=\"2\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EDLI_ADMIN_9"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"></font></td>" +
			"</tr>" +
			"</table>";
			List<Element> supList11 = HTMLWorker.parseToList(new StringReader(tbl11), null);
			Phrase phrase11 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase11.add(supList11.get(0));
			document.add(phrase11);
			
			String tbl12="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"right\"><font size=\"1\">7</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"left\"><font size=\"1\">October</font></td>" +
			"<td width=\"18%\" align=\"right\">" +
			"<font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERPF_10"),"") +"</font></td>" +
			"<td width=\"10%\"align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EEPS_10"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERDLI_10"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ADMIN_10"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\" colspan=\"2\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EDLI_ADMIN_10"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"></font></td>" +
			"</tr>" +
			"</table>";
			List<Element> supList12 = HTMLWorker.parseToList(new StringReader(tbl12), null);
			Phrase phrase12 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase12.add(supList12.get(0));
			document.add(phrase12);
			
			String tbl13="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"right\"><font size=\"1\">8</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"left\"><font size=\"1\">November</font></td>" +
			"<td width=\"18%\" align=\"right\">" +
			"<font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERPF_11"),"") +"</font></td>" +
			"<td width=\"10%\"align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EEPS_11"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERDLI_11"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ADMIN_11"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\" colspan=\"2\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EDLI_ADMIN_11"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"></font></td>" +
			"</tr>" +
			"</table>";
			List<Element> supList13 = HTMLWorker.parseToList(new StringReader(tbl13), null);
			Phrase phrase13 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase13.add(supList13.get(0));
			document.add(phrase13);
			
			String tbl14="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"right\"><font size=\"1\">9</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"left\"><font size=\"1\">December</font></td>" +
			"<td width=\"18%\" align=\"right\">" +
			"<font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERPF_12"),"") +"</font></td>" +
			"<td width=\"10%\"align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EEPS_12"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERDLI_12"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ADMIN_12"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\" colspan=\"2\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EDLI_ADMIN_12"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"></font></td>" +
			"</tr>" +
			"</table>";
			List<Element> supList14 = HTMLWorker.parseToList(new StringReader(tbl14), null);
			Phrase phrase14 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase14.add(supList14.get(0));
			document.add(phrase14);
			
			String tbl15="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"right\"><font size=\"1\">10</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"left\"><font size=\"1\">January</font></td>" +
			"<td width=\"18%\" align=\"right\">" +
			"<font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERPF_1"),"") +"</font></td>" +
			"<td width=\"10%\"align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EEPS_1"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERDLI_1"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ADMIN_1"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\" colspan=\"2\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EDLI_ADMIN_1"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"></font></td>" +
			"</tr>" +
			"</table>";
			List<Element> supList15 = HTMLWorker.parseToList(new StringReader(tbl15), null);
			Phrase phrase15 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase15.add(supList15.get(0));
			document.add(phrase15);
			
			String tbl16="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"right\"><font size=\"1\">11</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"left\"><font size=\"1\">February</font></td>" +
			"<td width=\"18%\" align=\"right\">" +
			"<font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERPF_2"),"") +"</font></td>" +
			"<td width=\"10%\"align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EEPS_2"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERDLI_2"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ADMIN_2"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\" colspan=\"2\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EDLI_ADMIN_2"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"></font></td>" +
			"</tr>" +
			"</table>";
			List<Element> supList16 = HTMLWorker.parseToList(new StringReader(tbl16), null);
			Phrase phrase16 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase16.add(supList16.get(0));
			document.add(phrase16);
			
			String tbl17="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"right\"><font size=\"1\">12</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"left\"><font size=\"1\">March</font></td>" +
			"<td width=\"18%\" align=\"right\">" +
			"<font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERPF_3"),"") +"</font></td>" +
			"<td width=\"10%\"align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EEPS_3"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ERDLI_3"),"") +"</font></td>" +
			"<td align=\"right\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("ADMIN_3"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\" colspan=\"2\"><font size=\"1\">"+uF.showData((String)hmReconcilation.get("EDLI_ADMIN_3"),"") +"</font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"></font></td>" +
			"</tr>" +
			"</table>";
			List<Element> supList17 = HTMLWorker.parseToList(new StringReader(tbl17), null);
			Phrase phrase17 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase17.add(supList17.get(0));
			document.add(phrase17);
			
			String tbl18="<table width=\"100%\" border=\"1\"><tr><td width=\"2%\" rowspan=\"2\" align=\"right\"><font size=\"1\"></font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"left\"><font size=\"1\"><b>Total</b></font></td>" +
			"<td width=\"18%\" align=\"right\">" +
			"<font size=\"1\"><b>"+uF.showData((String)hmReconcilationTotal.get("ERPF_TOTAL"),"") +"</b></font></td>" +
			"<td width=\"10%\"align=\"right\"><font size=\"1\"><b>"+uF.showData((String)hmReconcilationTotal.get("EEPS_TOTAL"),"") +"</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>"+uF.showData((String)hmReconcilationTotal.get("ERDLI_TOTAL"),"") +"</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>"+uF.showData((String)hmReconcilationTotal.get("ADMIN_TOTAL"),"") +"</b></font></td>" +
			"<td width=\"10%\" align=\"right\" colspan=\"2\"><font size=\"1\"><b>"+uF.showData((String)hmReconcilationTotal.get("EDLI_ADMIN_TOTAL"),"") +"</b></font></td>" +
			"<td width=\"10%\" align=\"right\" ><font size=\"1\"><b></b></font></td>" +
			"</tr>" +
			"</table>";
			List<Element> supList18 = HTMLWorker.parseToList(new StringReader(tbl18), null);
			Phrase phrase18 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase18.add(supList18.get(0));
			document.add(phrase18);
			
			document.add(new Paragraph(" "));
			String tbl19 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"left\"><font size=\"1\">(1) Total No of contribution cards enclosed (Form 3-A Revised)</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">(2) Certified that Form 3-A, duly completed of all " +
			"the members listed in this statement and enclosed, except those already " +
			"sent during the course of the<br/> currency period for the final settelment, " +
			"of the concerned member's account vide \"Remarks\" furnished against the name of " +
			"the respective members above.</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
			"<tr><td align=\"left\"><font size=\"1\">&nbsp;</font></td></tr>" +
			"<tr><td align=\"right\"><font size=\"1\">Signature of the Employer(with Office Seal)</font></td></tr>" +
			"</table>";
			List<Element> supList19 = HTMLWorker.parseToList(new StringReader(tbl19), null);
			Phrase phrase19 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase19.add(supList19.get(0));
			document.add(phrase19);
			
			document.close();
					
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=Form6AReports.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
public void viewForm6A(UtilityFunctions uF){
	
	Connection con = null;
	PreparedStatement pst=null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);

	try {
		
		Map<String, Map<String, String>> hmEmployeeDetails = new HashMap<String, Map<String, String>>();
		
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
		
		pst = con.prepareStatement("select * from org_details where org_id = ? ");
		pst.setInt(1, uF.parseToInt(getF_org()));
		rs = pst.executeQuery();
		Map<String, String> hmOrg=new HashMap<String, String>();
		while (rs.next()) {
			hmOrg.put("ORG_ID", rs.getString("org_id"));
			hmOrg.put("ORG_NAME", rs.getString("org_name"));
			hmOrg.put("ORG_LOGO", rs.getString("org_logo"));
			hmOrg.put("ORG_ADDRESS", rs.getString("org_address"));
			hmOrg.put("ORG_PINCODE", rs.getString("org_pincode"));
			hmOrg.put("ORG_CONTACT", rs.getString("org_contact1"));
			hmOrg.put("ORG_EMAIL", rs.getString("org_email"));
			hmOrg.put("ORG_STATE_ID", rs.getString("org_state_id"));
			hmOrg.put("ORG_COUNTRY_ID", rs.getString("org_country_id"));
			hmOrg.put("ORG_CITY", rs.getString("org_city"));
			hmOrg.put("ORG_CODE", rs.getString("org_code"));
			hmOrg.put("ORG_DISPLAY_PAYCYCLE", rs.getString("display_paycycle"));
			hmOrg.put("ORG_DURATION_PAYCYCLE", rs.getString("duration_paycycle"));
			hmOrg.put("ORG_SALARY_CAL_BASIS", rs.getString("salary_cal_basis"));
			hmOrg.put("ORG_START_PAYCYCLE",uF.getDateFormat(rs.getString("start_paycycle"), DBDATE, DATE_FORMAT) );
			hmOrg.put("ORG_ESTABLISH_CODE_NO", rs.getString("establish_code_no"));
		}  
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select * from employee_personal_details");
		rs = pst.executeQuery();
		while(rs.next()){
			Map<String, String> hmInner = new HashMap<String, String>();
			hmInner.put("EPF_ACC_NO", rs.getString("emp_pf_no"));
			hmInner.put("NAME", rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
			
			hmEmployeeDetails.put(rs.getString("emp_per_id"), hmInner);
		}
		rs.close();
		pst.close();
		
		
		pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end=? and org_id = ? and level_id=?");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(getF_org()));
		pst.setInt(4, uF.parseToInt(getF_level()));
//		System.out.println("pst==="+pst);
		rs = pst.executeQuery();
		double dblEEPFContribution=0;
		double dblERPFContribution=0;
		double dblERPF_MAX_Contribution=0;
		double dblEEPDContribution=0;
		double dblEDLIContribution=0;
		double dblEEPFADMIN=0;
		double dblEDLIADMIN=0;
		String salary_head_id = null;
		while(rs.next()){
			dblEEPFContribution= rs.getDouble("eepf_contribution");
			dblERPF_MAX_Contribution = rs.getDouble("epf_max_limit");
			dblERPFContribution= rs.getDouble("erpf_contribution");
			dblEEPDContribution= rs.getDouble("erps_contribution");
			dblEDLIContribution= rs.getDouble("erdli_contribution");
			dblEEPFADMIN= rs.getDouble("pf_admin_charges");
			dblEDLIADMIN= rs.getDouble("edli_admin_charges");
			
			salary_head_id = rs.getString("salary_head_id");
		}
		rs.close();
		pst.close();
		
		
		double dblTotalContribution = dblERPFContribution + dblEEPDContribution + dblEDLIContribution + dblEEPFADMIN + dblEDLIADMIN;
		double dblDIFFContribution = dblEEPDContribution + dblEDLIContribution + dblEEPFADMIN + dblEDLIADMIN;
		
		request.setAttribute("TOTAL_PER", uF.formatIntoTwoDecimal(dblTotalContribution));
		request.setAttribute("EPF_PER", uF.formatIntoTwoDecimal(dblEEPFContribution));
		request.setAttribute("ERPF_PER", uF.formatIntoTwoDecimal(dblERPFContribution));
		request.setAttribute("ERPS_PER", uF.formatIntoTwoDecimal(dblEEPDContribution));
		request.setAttribute("DIFF_PER", uF.formatIntoTwoDecimal(dblDIFFContribution));
		
		
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("select emp_id,sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
				"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(pf_admin_charges) as pf_admin_charges," +
				"sum(edli_admin_charges) as edli_admin_charges  from emp_epf_details where financial_year_start = ? and financial_year_end = ? " +
				"and emp_id in (");
		sbQuery.append("select emp_id from challan_details where financial_year_from_date = ? and financial_year_to_date = ? and challan_type=? ");
		sbQuery.append(" and  month in (");
        for(int i=1; i<=12; i++){
        	if(i==1){
        		sbQuery.append("'"+i+"'");
        	} else {
        		sbQuery.append(",'"+i+"'");
        	}
        }
        sbQuery.append(") ");
		sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
				"where eod.emp_id = epd.emp_per_id and org_id=? and eod.grade_id in (select grade_id from designation_details dd, level_details ld, " +
					"grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id =?))");
		sbQuery.append(") group by emp_id");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, EMPLOYEE_EPF);
		pst.setInt(6, uF.parseToInt(getF_org()));
		pst.setInt(7, uF.parseToInt(getF_level()));
//		System.out.println("pst====>"+pst);
		rs = pst.executeQuery();
		Map<String, Map<String, String>> hmDetails = new HashMap<String, Map<String, String>>();
		Map<String, String> hmDetailsTotal = new HashMap<String, String>();
		double dblTotalEmployeeAmount = 0;
		double dblTotalEmployerEPFAmount = 0;  
		double dblTotalEmployerEPSAmount = 0;
		while(rs.next()){
			Map<String, String> hmInner = (Map<String, String>)hmDetails.get(rs.getString("emp_id"));
			if(hmInner==null) hmInner = new HashMap<String, String>();

			hmInner.put("EMPLOYEE_SHARE", uF.formatIntoTwoDecimal(rs.getDouble("eepf_contribution")));
			dblTotalEmployeeAmount += rs.getDouble("eepf_contribution") ;
			hmDetailsTotal.put("TOTAL_EMPLOYEE_SHARE", uF.formatIntoTwoDecimal(dblTotalEmployeeAmount));
			
			double dblEPF = rs.getDouble("eepf_contribution");
			double dblERPF = rs.getDouble("erpf_contribution");  
			double dblEEPS = rs.getDouble("erps_contribution");
			double dblEDLI = rs.getDouble("erdli_contribution");
			double dblEPFADMIN = rs.getDouble("pf_admin_charges");
			double dblEDLADMIN = rs.getDouble("edli_admin_charges");
			
//			double dblEmployerShare = dblERPF + dblEEPS + dblEDLI + dblEPFADMIN + dblEDLADMIN;
			double dblEmployerShare = dblEPF - dblEEPS;
				
			hmInner.put("EMPLOYER_SHARE_EPF", uF.formatIntoTwoDecimal(dblEmployerShare));
			dblTotalEmployerEPFAmount += dblEmployerShare ;
			hmDetailsTotal.put("TOTAL_EMPLOYER_SHARE_EPF", uF.formatIntoTwoDecimal(dblTotalEmployerEPFAmount));
				
			hmInner.put("EMPLOYER_SHARE_EPS", uF.formatIntoTwoDecimal(dblEEPS));
			dblTotalEmployerEPSAmount += dblEEPS;
			hmDetailsTotal.put("TOTAL_EMPLOYER_SHARE_EPS", uF.formatIntoTwoDecimal(dblTotalEmployerEPSAmount));
			
			
			hmDetails.put(rs.getString("emp_id"), hmInner);
		} 
		rs.close();
		pst.close();
		
		Map<String, String> hmEarningTotal = new HashMap<String, String>();
		if(salary_head_id!=null && !salary_head_id.equals("")){
			salary_head_id = salary_head_id.substring(0,salary_head_id.length()-1);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(amount) as amount,emp_id from payroll_generation where financial_year_from_date = ? and financial_year_to_date = ? " +
					"and is_paid =true and salary_head_id in("+salary_head_id+") and emp_id in (");
			sbQuery.append("select emp_id from challan_details where financial_year_from_date = ? and financial_year_to_date = ? and challan_type=? ");
			sbQuery.append(" and  month in (");
            for(int i=1; i<=12; i++){
            	if(i==1){
            		sbQuery.append("'"+i+"'");
            	} else {
            		sbQuery.append(",'"+i+"'");
            	}
            }
            sbQuery.append(") ");
			sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
					"where eod.emp_id = epd.emp_per_id and org_id=? and eod.grade_id in (select grade_id from designation_details dd, level_details ld, " +
					"grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id =?))");
			sbQuery.append(")  group by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, EMPLOYEE_EPF);
			pst.setInt(6, uF.parseToInt(getF_org()));
			pst.setInt(7, uF.parseToInt(getF_level()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			
			double dblTotalEarningAmount = 0;
			while(rs.next()){
				hmEarningTotal.put(rs.getString("emp_id"), uF.formatIntoTwoDecimal(rs.getDouble("amount")));
				
				dblTotalEarningAmount += rs.getDouble("amount") ;
				hmDetailsTotal.put("TOTAL_EARNING", uF.formatIntoTwoDecimal(dblTotalEarningAmount));
				
			} 
			rs.close();
			pst.close();
			
		}
		
		sbQuery = new StringBuilder();
		sbQuery.append("select _month,sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
				"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(pf_admin_charges) as pf_admin_charges," +
				"sum(edli_admin_charges) as edli_admin_charges  from emp_epf_details where financial_year_start = ? and financial_year_end = ? " +
				"and emp_id in (");
		sbQuery.append("select emp_id from challan_details where financial_year_from_date = ? and financial_year_to_date = ? and challan_type=? ");
		sbQuery.append(" and  month in (");
        for(int i=1; i<=12; i++){
        	if(i==1){
        		sbQuery.append("'"+i+"'");
        	} else {
        		sbQuery.append(",'"+i+"'");
        	}
        }
        sbQuery.append(") ");
		sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
				"where eod.emp_id = epd.emp_per_id and org_id=? and eod.grade_id in (select grade_id from designation_details dd, level_details ld, " +
					"grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id =?))");
		sbQuery.append(") group by _month");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, EMPLOYEE_EPF);
		pst.setInt(6, uF.parseToInt(getF_org()));
		pst.setInt(7, uF.parseToInt(getF_level()));
//		System.out.println("pst====>"+pst);
		rs = pst.executeQuery();
		Map<String, String> hmReconcilation = new HashMap<String, String>();
		Map<String, String> hmReconcilationTotal = new HashMap<String, String>();
		double dblTotalEPF = 0;
		double dblTotalEPS = 0;  
		double dblTotalERDLI = 0;
		double dblTotalAdmin = 0;
		double dblTotalEDLI = 0;
		while(rs.next()){
			double dblERPF = rs.getDouble("erpf_contribution");  
			double dblEEPS = rs.getDouble("erps_contribution");
			double dblERDLI = rs.getDouble("erdli_contribution");
			double dblEPFADMIN = rs.getDouble("pf_admin_charges");
			double dblEDLADMIN = rs.getDouble("edli_admin_charges");
			
			hmReconcilation.put("ERPF_"+rs.getString("_month"), uF.formatIntoTwoDecimal(dblERPF));  
			dblTotalEPF += dblERPF;
			hmReconcilationTotal.put("ERPF_TOTAL", uF.formatIntoTwoDecimal(dblTotalEPF));
						
			hmReconcilation.put("EEPS_"+rs.getString("_month"), uF.formatIntoTwoDecimal(dblEEPS));
			dblTotalEPS += dblEEPS;
			hmReconcilationTotal.put("EEPS_TOTAL", uF.formatIntoTwoDecimal(dblTotalEPS));
			
			hmReconcilation.put("ERDLI_"+rs.getString("_month"), uF.formatIntoTwoDecimal(dblERDLI));
			dblTotalERDLI += dblERDLI;
			hmReconcilationTotal.put("ERDLI_TOTAL", uF.formatIntoTwoDecimal(dblTotalERDLI));
			
			hmReconcilation.put("ADMIN_"+rs.getString("_month"), uF.formatIntoTwoDecimal(dblEPFADMIN));
			dblTotalAdmin += dblEPFADMIN;
			hmReconcilationTotal.put("ADMIN_TOTAL", uF.formatIntoTwoDecimal(dblTotalAdmin));
			
			hmReconcilation.put("EDLI_ADMIN_"+rs.getString("_month"), uF.formatIntoTwoDecimal(dblEDLADMIN));
			dblTotalEDLI += dblEDLADMIN;
			hmReconcilationTotal.put("EDLI_ADMIN_TOTAL", uF.formatIntoTwoDecimal(dblTotalEDLI));
			
		}
		rs.close();
		pst.close();
		
		request.setAttribute("strFinancialYearStart", strFinancialYearStart);
		request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
		request.setAttribute("hmDetails", hmDetails);
		request.setAttribute("hmEarningTotal", hmEarningTotal);
		request.setAttribute("hmDetailsTotal", hmDetailsTotal);
		request.setAttribute("hmEmployeeDetails", hmEmployeeDetails);
		request.setAttribute("hmOrg",hmOrg);
		request.setAttribute("hmReconcilation", hmReconcilation);
		request.setAttribute("hmReconcilationTotal",hmReconcilationTotal); 
		
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}
	
	public void generateForm3A(UtilityFunctions uF){
		try {

			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			Map<String, Map<String, String>>  hmDetails = (Map<String, Map<String, String>>)request.getAttribute("hmDetails");
			if(hmDetails == null) hmDetails = new HashMap<String, Map<String, String>>(); 

			Map<String, String>  hmEmployeeDetails = (Map<String, String>)request.getAttribute("hmEmployeeDetails");
			if(hmEmployeeDetails == null)hmEmployeeDetails = new HashMap<String, String>();

			Map<String, String> hmEmpAbsentDays = (Map<String, String>) request.getAttribute("hmEmpAbsentDays");
			if(hmEmpAbsentDays == null) hmEmpAbsentDays = new HashMap<String, String>();
			Map<String, String> hmEPFMap = (Map<String, String>) request.getAttribute("hmEPFMap");
			if(hmEPFMap == null) hmEPFMap = new HashMap<String, String>();

			List<String>  alMonth = (List<String>)request.getAttribute("alMonth");
			if (alMonth == null) alMonth = new ArrayList<String>();

			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

			double dblTotalAmountWages = uF.parseToDouble((String)request.getAttribute("dblTotalAmountWages"));
			double dblTotalEmployeeAmount = uF.parseToDouble((String)request.getAttribute("dblTotalEmployeeAmount"));
			double dblTotalEmployerEPFAmount = uF.parseToDouble((String)request.getAttribute("dblTotalEmployerEPFAmount"));
			double dblTotalEmployerEPSAmount = uF.parseToDouble((String)request.getAttribute("dblTotalEmployerEPSAmount"));
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();
					
			String img = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"1\"><b>Form 3A (Revised)</b></font></td></tr>" +
					"<tr><td align=\"center\"><font size=\"1\">The Employees' Provident Fund Scheme, 1952(Para 35 & 42) and The Employees' Pension Scheme, 1995(para 19)</font></td></tr>" +
					"<tr><td align=\"center\"><font size=\"1\"><b>CONTRIBUTION CARD FOR CURRENCY PERIOD FROM "+strFinancialYearStart+" TO "+strFinancialYearEnd+"</b></font></td></tr>" +
					"</table>";
			List<Element> supList = HTMLWorker.parseToList(new StringReader(img), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);
			
			String h = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
					"<tr><td><font size=\"1\">PF Account No </font></td><td><font size=\"1\">"+uF.showData((String)hmEmployeeDetails.get("EPF_ACC_NO"),"")+" </font></td><td></td></tr>" +
					"<tr><td><font size=\"1\">Name/Surname </font></td><td><font size=\"1\">"+uF.showData((String)hmEmployeeDetails.get("NAME"),"")+" </font></td><td></td></tr>" +
					"<tr><td><font size=\"1\">Father's/ Husban's Name </font></td><td><font size=\"1\">"+uF.showData((String)hmEmployeeDetails.get("FATHER_SPOUSE"),"")+" </font></td><td></td></tr>" +
					"<tr><td><font size=\"1\">Statutory rate of P.F. Contribution, if any </font></td><td><font size=\"1\">"+uF.showData((String)hmEmployeeDetails.get(""),"")+"</font></td><td></td></tr>" +
					"<tr><td><font size=\"1\">Contribution "+strFinancialYearStart+" to "+strFinancialYearEnd+" </font></td><td></td><td></td></tr>"+		
					"</table><br><br>";
			List<Element> supList0 = HTMLWorker.parseToList(new StringReader(h), null);
			Phrase phrase0 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase0.add(supList0.get(0));
			document.add(phrase0);
			
			document.add(new Paragraph(" "));
			
			String tbl="<table width=\"100%\" border=\"1\"><tr><td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Payment Month<br/>(1)</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Amount of Wages<br/>(2)</font></td><td align=\"center\"><table border=\"0\"><tr><td><font size=\"1\">Worker's Share</font></td></tr><tr><td><font size=\"1\">E.P.F.(3)</font></td></tr></table></td>" +
			"<td align=\"center\" colspan=\"2\"><table border=\"0\"><tr><td><font size=\"1\">Employer's Share</font></td></tr><tr><td><table border=\"0\"><tr><td><font size=\"1\">E.P.F. difference between "+uF.showData(hmEPFMap.get("EEPF_CONTRIBUTION"), "0")+"% & "+uF.showData(hmEPFMap.get("ERPS_CONTRIBUTION"), "0")+"%</font></td>" +
			"<td><font size=\"1\">Pension Fund contribution "+uF.showData(hmEPFMap.get("ERPS_CONTRIBUTION"), "0")+"%</font></td></tr></table></td></tr></table></td><td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Refund of Adv.<br/>(5)</font></td>" +
			"<td width=\"10%\" rowspan=\"2\" align=\"center\"><font size=\"1\">No of days/period of non contributing services<br/>(6)</font></td>" +
			"<td width=\"30%\" rowspan=\"2\" align=\"center\"><font size=\"1\">Remarks<br/>(7)</font></td></tr>" +
			"</table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase1.add(supList1.get(0));
			document.add(phrase1);
			
			double dblTotalAbsent = 0.0d;
			for(int i=0; alMonth!=null && i<alMonth.size(); i++){
				Map<String, String> hmInner = (Map<String, String>)hmDetails.get((String)alMonth.get(i));
				if(hmInner==null)hmInner=new HashMap<String, String>();
				
				dblTotalAbsent += uF.parseToDouble(hmEmpAbsentDays.get((String)alMonth.get(i)));
				
				String tbldate="<table border=\"1\"><tr><td><font size=\"1\">"+uF.getDateFormat((String)alMonth.get(i), "MM", "MMMM")+"</font></td>" +
				"<td width=\"10%\" align=\"right\"><font size=\"1\">"+uF.showData((String)hmInner.get("AMOUNT_WAGES"),"0")+"</font></td>" +
				"<td width=\"10%\" align=\"right\"><font size=\"1\">"+uF.showData((String)hmInner.get("EMPLOYEE_CONTRIBUTION"),"0")+"</font></td>" +
				"<td width=\"10%\" align=\"right\"><font size=\"1\">"+uF.showData((String)hmInner.get("EMPLOYER_DIFF_SHARE"),"0")+"</font></td>" +
				"<td width=\"10%\" align=\"right\"><font size=\"1\">"+uF.showData((String)hmInner.get("EMPLOYER_SHARE_EPS"),"0")+"</font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\"></font></td>" +
				"<td width=\"10%\" align=\"right\"><font size=\"1\">"+uF.showData(hmEmpAbsentDays.get((String)alMonth.get(i)),"0")+"</font></td>" +
				"<td width=\"10%\" align=\"left\"><font size=\"1\"></font></td></tr></table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbldate), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
			}   
			
			String total="<table border=\"1\"><tr><td><font size=\"1\"><b>Total</b></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"><b>"+uF.formatIntoTwoDecimal(dblTotalAmountWages)+"</b></font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"><b>"+uF.formatIntoTwoDecimal(dblTotalEmployeeAmount)+"</b></font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"><b>"+uF.formatIntoTwoDecimal(dblTotalEmployerEPFAmount)+"</b></font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"><b>"+uF.formatIntoTwoDecimal(dblTotalEmployerEPSAmount)+"</b></font></td>" +
			"<td width=\"10%\" align=\"left\"><font size=\"1\"><b></b></font></td>" +
			"<td width=\"10%\" align=\"right\"><font size=\"1\"><b>"+uF.formatIntoTwoDecimal(dblTotalAbsent)+"</b></font></td>" +
			"<td width=\"10%\" align=\"left\"><font size=\"1\"><b></b></font></td></tr></table>";  
			List<Element> supList3 = HTMLWorker.parseToList(new StringReader(total), null);
			Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8));
			phrase3.add(supList3.get(0));
			document.add(phrase3);
			
			
			/*
			String tbl6 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" >" +
			"<tr><td align=\"left\" width=\"80%\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;Certified that the total of contribution (both shares) " +
			"indicated in this Card i.e., Rs.18,1800.00 has already been remitted in full in E.P.F.A/C No.10(vide note below).</font></td><td width=\"20%\"></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;</font></td><td width=\"20%\"></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;</font></td><td width=\"20%\"></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">Certified that the diffrence between the total of the contribution shown " +
			"under cols. 3 and 4(a) of the table and that arrived at on the total wages shown in column 2 at the prescribed rate is solely due </font></td><td width=\"20%\"></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">to rounding off of contributions to the nearest rupee under the rules.</font></td><td width=\"20%\"></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;</font></td><td width=\"20%\"></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;</font></td><td width=\"20%\"></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">Date</font></td><td width=\"20%\"></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">_______________________________________________" +
			"______________________________________________________________________________" +
			"_____________________________________________________________________________________" +
			"__________________________________________________________</font></td><td width=\"20%\"></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">Note</font></td><td width=\"20%\"></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;</font></td><td width=\"20%\"></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">(1) In respect of form 3A sent to the Regional Office" +
			" during the cource of currency period for the purpose of final settelement of the accounts " +
			"of the member, who has left service, detail;s of date</font></td><td width=\"20%\"></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\"> and resons for leaving service " +
			"should be furnished under column7(a) and (b)</font></td><td width=\"20%\"><font size=\"1\">Signatur of" +
			" employee with office seal</font></td><td width=\"20%\"></td></tr>" +
			"</table>";*/

			String tbl6 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" >" +
			"<tr><td align=\"left\" width=\"80%\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;Certified that the total of contribution (both shares) " +
			"indicated in this Card i.e., Rs.18,1800.00 has already been remitted in full in E.P.F.A/C No.10(vide note below).</font></td></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;</font></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;</font></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">Certified that the diffrence between the total of the contribution shown " +
			"under cols. 3 and 4(a) of the table and that arrived at on the total wages shown in column 2 at the prescribed  </font></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">rate is solely due to rounding off of contributions to the nearest rupee under the rules.</font></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;</font></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;</font></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">Date</font></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">_______________________________________________" +
			"______________________________________________________________________________" +
			"__________________________________________" +
			"</font></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">Note</font></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">&nbsp;&nbsp;&nbsp;&nbsp;</font></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">(1) In respect of form 3A sent to the Regional Office" +
			" during the cource of currency period for the purpose of final settelement of the accounts " +
			"of the member, who has left service,</font></td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\"> detail;s of date and resons for leaving service " +
			"should be furnished under column7(a) and (b)</font></td></tr><tr><td width=\"80%\" align=\"left\">&nbsp;&nbsp;</td></tr>" +
			"</tr><tr><td width=\"80%\" align=\"left\">&nbsp;&nbsp;</td></tr>" +
			"</tr><tr><td width=\"80%\" align=\"left\">&nbsp;&nbsp;</td></tr>" +
			"<tr><td width=\"80%\" align=\"right\"><font size=\"1\">Signatur of" +
			" employee with office seal</font></td><tr>" +
			"</tr><tr><td width=\"80%\" align=\"left\">&nbsp;&nbsp;</td></tr>" +
			"<tr><td width=\"80%\" align=\"left\"><font size=\"1\">(2) In respect of those who are not members of the Pension Fund the employers share of contribution to the EPF will be 8.33% " +
			" column 4(a).<br/> or 12% as the case may be and is to be under column 4(a) .</font></td></tr>" +
			"" +
			"</table>";
	List<Element> supList6 = HTMLWorker.parseToList(new StringReader(tbl6), null);
	Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
	phrase6.add(supList6.get(0));
	document.add(phrase6);
			
			document.close();
			
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=Form3AReports.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}  
	
public void viewForm3A(UtilityFunctions uF){
	
	Connection con = null;
	PreparedStatement pst=null;
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
		
		
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
		cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM")) -1);
		cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
		
		List<String> alMonth = new ArrayList<String>();
		Map<String, String> hmMonthDayCount = new HashMap<String, String>();
		for(int i=0; i<12; i++){
			
//			System.out.println("=====>>>"+uF.getDateFormat(""+(cal.get(Calendar.MONTH)+1),"MM","MM"));
			alMonth.add(uF.getDateFormat(""+(cal.get(Calendar.MONTH)+1),"MM","MM"));
			
//			System.out.println("DATE=====>>>"+(cal.get(Calendar.DATE))+" month=====>>>"+(cal.get(Calendar.MONTH)+1)+" year=====>>>"+(cal.get(Calendar.YEAR)));
			

			int iDay = cal.get(Calendar.DATE);
			int iMonth = cal.get(Calendar.MONTH);
			int iYear = cal.get(Calendar.YEAR);

			// Create a calendar object and set year and month
			Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);

			// Get the number of days in that month
			int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
			
			hmMonthDayCount.put(""+(iMonth+1), ""+daysInMonth);
			
			cal.add(Calendar.MONTH, 1);
			
		}
		
		
		con = db.makeConnection(con);
		
		String orgId = CF.getEmpOrgId(con, uF, getStrSelectedEmpId());
		String levelId = CF.getEmpLevelId(con, getStrSelectedEmpId());
		
		Map<String, String> hmEmployeeDetails = new HashMap<String, String>();
		pst = con.prepareStatement("select * from emp_family_members where emp_id = ?");
		pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
		rs= pst.executeQuery();
		while(rs.next()){
			if(rs.getString("member_type")!=null && rs.getString("member_type").equalsIgnoreCase("SPOUSE")){
				hmEmployeeDetails.put("SPOUSE", rs.getString("member_name"));
			}else if(rs.getString("member_type")!=null && rs.getString("member_type").equalsIgnoreCase("FATHER")){
				hmEmployeeDetails.put("FATHER", rs.getString("member_name"));
			}
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=?");
		pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
		rs = pst.executeQuery();
		while(rs.next()){
			hmEmployeeDetails.put("EPF_ACC_NO", rs.getString("emp_pf_no"));
			hmEmployeeDetails.put("NAME", rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
			if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("M")){
				hmEmployeeDetails.put("FATHER_SPOUSE", hmEmployeeDetails.get("FATHER"));
			}else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("F") && rs.getString("marital_status")!=null && rs.getString("marital_status").equalsIgnoreCase("M")){
				hmEmployeeDetails.put("FATHER_SPOUSE", hmEmployeeDetails.get("SPOUSE"));
			}else{
				hmEmployeeDetails.put("FATHER_SPOUSE", hmEmployeeDetails.get("FATHER"));
			}
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end=? and org_id=? and level_id=?");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT)); 
		pst.setInt(3, uF.parseToInt(orgId));
		pst.setInt(4, uF.parseToInt(levelId));
		rs = pst.executeQuery();
		Map<String, String> hmEPFMap = new HashMap<String, String>();
		while(rs.next()){
			hmEPFMap.put("EEPF_CONTRIBUTION", rs.getString("eepf_contribution"));
			hmEPFMap.put("EPF_MAX_LIMIT", rs.getString("epf_max_limit"));
			hmEPFMap.put("ERPF_CONTRIBUTION", rs.getString("erpf_contribution"));
			hmEPFMap.put("ERPS_CONTRIBUTION", rs.getString("erps_contribution"));
			hmEPFMap.put("ERDLI_CONTRIBUTION", rs.getString("erdli_contribution"));
			hmEPFMap.put("PF_ADMIN_CHARGES", rs.getString("pf_admin_charges"));
			hmEPFMap.put("EDLI_ADMIN_CHARGES", rs.getString("edli_admin_charges"));
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select * from emp_epf_details where financial_year_start=? and financial_year_end=? " +
				"and emp_id in (select emp_id from challan_details where emp_id=? and challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") " +
				"and is_paid = true and financial_year_from_date=? and financial_year_to_date=?)");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(getStrSelectedEmpId()));
		pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		rs = pst.executeQuery();
		Map<String, Map<String, String>> hmDetails = new HashMap<String, Map<String, String>>();
		double dblTotalAmountWages = 0;
		double dblTotalEmployeeAmount = 0;
		double dblTotalEmployerEPFAmount = 0;
		double dblTotalEmployerEPSAmount = 0;
//		System.out.println("pst==>"+pst);
		while(rs.next()){
			
			String month = rs.getString("_month")!=null && uF.parseToInt(rs.getString("_month"))<=9 ? "0"+rs.getString("_month") : rs.getString("_month");
			
			Map<String, String> hmInner = (Map<String, String>)hmDetails.get(month);
			if(hmInner==null) hmInner = new HashMap<String, String>();
			
			hmInner.put("AMOUNT_WAGES", uF.formatIntoTwoDecimal(rs.getDouble("epf_max_limit")));
			dblTotalAmountWages += rs.getDouble("epf_max_limit");
			
			hmInner.put("EMPLOYEE_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("eepf_contribution")));
			dblTotalEmployeeAmount += rs.getDouble("eepf_contribution");
			
			
			hmInner.put("EMPLOYER_DIFF_SHARE", uF.formatIntoTwoDecimal(rs.getDouble("eepf_contribution") - rs.getDouble("erps_contribution")));
			dblTotalEmployerEPFAmount += (rs.getDouble("eepf_contribution") - rs.getDouble("erps_contribution"));
			
			hmInner.put("EMPLOYER_SHARE_EPS", uF.formatIntoTwoDecimal(rs.getDouble("erps_contribution")));
			dblTotalEmployerEPSAmount += rs.getDouble("erps_contribution");
			
			hmDetails.put(month, hmInner);
		}
		rs.close();
		pst.close();
		request.setAttribute("dblTotalAmountWages", uF.formatIntoTwoDecimal(dblTotalAmountWages));
		request.setAttribute("dblTotalEmployeeAmount", uF.formatIntoTwoDecimal(dblTotalEmployeeAmount));
		request.setAttribute("dblTotalEmployerEPFAmount", uF.formatIntoTwoDecimal(dblTotalEmployerEPFAmount));
		request.setAttribute("dblTotalEmployerEPSAmount", uF.formatIntoTwoDecimal(dblTotalEmployerEPSAmount));
		
		
		pst = con.prepareStatement("select paid_days,month from payroll_generation where emp_id=? and is_paid = true " +
				"and financial_year_from_date=? and financial_year_to_date=? group by paid_days,month  order by paid_days,month");
		pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
		pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		rs = pst.executeQuery();
		Map<String, String> hmEmpPaidDays = new HashMap<String, String>();
		while(rs.next()){
			hmEmpPaidDays.put(rs.getString("month"), rs.getString("paid_days"));
		}
		rs.close();
		pst.close();
		
		Map<String, String> hmEmpAbsentDays = new HashMap<String, String>();
		Iterator<String> it =hmMonthDayCount.keySet().iterator();
		while(it.hasNext()){
			String strMonth = it.next();
			String strDays = hmMonthDayCount.get(strMonth);
			
			double strEmpPaidDays = uF.parseToDouble(hmEmpPaidDays.get(strMonth));
			
			double dblAbsent = uF.parseToDouble(strDays) - strEmpPaidDays;
			if(dblAbsent==uF.parseToDouble(strDays)){
				dblAbsent=0.0d;
			}
			dblAbsent = dblAbsent>0.0d ? dblAbsent: 0.0d;
			
			String months = uF.parseToInt(strMonth)>9 ? strMonth : "0"+strMonth;
			hmEmpAbsentDays.put(months, ""+dblAbsent);
			
		}
		
		
		request.setAttribute("strFinancialYearStart", strFinancialYearStart);
		request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
		request.setAttribute("hmDetails", hmDetails);
		request.setAttribute("hmEmployeeDetails", hmEmployeeDetails);
		request.setAttribute("alMonth", alMonth);
		request.setAttribute("hmEmpAbsentDays", hmEmpAbsentDays);
		request.setAttribute("hmEPFMap", hmEPFMap);
		
		
	} catch (Exception e) {  
		e.printStackTrace();
	}finally{
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

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

	/*public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}*/

	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}

	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	private HttpServletRequest request;
	private HttpServletResponse response;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	public String getStrMonth() {
		return strMonth;
	}
	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}
	/*public List<FillMonth> getMonthList() {
		return monthList;
	}
	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}*/
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public String getF_level() {
		return f_level;
	}


	public void setF_level(String f_level) {
		this.f_level = f_level;
	}
	

}