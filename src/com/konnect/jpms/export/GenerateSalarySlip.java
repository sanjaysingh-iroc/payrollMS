package com.konnect.jpms.export;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;

public class GenerateSalarySlip implements ServletRequestAware, ServletResponseAware, IStatements {
	
	  
	 String[] arrUnitdo ={"", " One", " Two", " Three", " Four", " Five"," Six", " Seven", " Eight", " Nine", " Ten", " Eleven", " Twelve"," Thirteen", " Fourteen", " Fifteen",  " Sixteen", " Seventeen", " Eighteen", " Nineteen"};
		    String[] arrTens =  {"", "Ten", " Twenty", " Thirty", " Forty", " Fifty"," Sixty", " Seventy", " Eighty"," Ninety"};
		    String[] arrDigit = {"", " Hundred", " Thousand", " Lakh", " Crore"};
		   int nseprate; 
		  
	List<Integer> salHeadId = new ArrayList<Integer>();
	List<Double> salHeadAmt = new ArrayList<Double>();
	List<String> salaryHeadName = new ArrayList<String>();
	List<String> deductionHeadName = new ArrayList<String>();
	List<Double> salHeadAmount = new ArrayList<Double>();
	List<Double> salHeadAmountGross = new ArrayList<Double>();
	List<Double> salHeadAmountNet = new ArrayList<Double>();
//	List<String> alGross = new ArrayList<String>();
	List<Double> deductionHeadAmount = new ArrayList<Double>();
	List<String> empDetails = new ArrayList<String>();
	List<String> leaveName = new ArrayList<String>();
	List<Integer> noOfLeave = new ArrayList<Integer>(); 
	ArrayList<String> payEmpHead = new ArrayList<String>();
	List<Integer> leaveTypeName = new ArrayList<Integer>();
	List<String> totalLeave = new ArrayList<String>();
	List<String> leaveNameType = new ArrayList<String>();
	
	Map hmLeaveType = new HashMap();
	Map hmBalanceLeave = new HashMap();
	Map hmLeaveNameMap = new HashMap();

	int nYear = 0;
	Double dblTotalAmt = 0.0;
	Double dblTotalDeduction = 0.0;
	Double dblGrossTotal = 0.0;
	Double dblNetSalary = 0.0;
	String strPayMode = null;

	String strCompanyAddress = null;
	String strCompanyLogo = null;
	String strEmpImage = null;
//===start parvez date: 04-03-2023===	
//	int tDays=0;
//	int pDays=0;
//	int unpaidDays =0;
	double tDays=0.0;
	double pDays=0.0;
	double unpaidDays =0.0;
//===end parvez date: 04-03-2023===	
	
	String strPaymentMode= null;
	int pMonth;
	int pYear;
	
	String strEmpId;
	String strServiceId;
	String strMonth;
	String strPCS;
	String strPCE;
	String strPC;
	String strFYS;
	String strFYE;
	String strPaymentDate;
	boolean isAttachment;
	
	String strLevelId = null;
	String bankName =null;
	String monthName= null;
	String strBankPayType;
	

	private static final long serialVersionUID = 1L;
	HttpSession session;
	public CommonFunctions CF = null;
	String strSessionEmpId;
	String strUserType =  null;
	String strBaseUserType =  null;
	String strWLocationAccess =  null;
//	UtilityFunctions uF = new UtilityFunctions();
	String strPassword =  null;

	public void execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return;

		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strWLocationAccess = (String)session.getAttribute(WLOCATION_ACCESS);
		
//		System.out.println("strUserType ===>> " + strUserType);
		if(strUserType!=null && !strUserType.equals("")) {
			UtilityFunctions uF = new UtilityFunctions();
//			System.out.println("getStrEmpId() ===>> " + getStrEmpId());
//			if(!isAttachment()) {
				List<String> accessEmpList = CF.viewEmployeeIdsList(request, uF, strBaseUserType, strSessionEmpId, strWLocationAccess);
				setStrEmpId((String) request.getParameter("EID"));
//				System.out.println(getStrEmpId() + " -- accessEmpList ===>> " + accessEmpList);
//				System.out.println("EID ===>> " + (String) request.getParameter("EID"));
//				System.out.println("strBaseUserType ===>> " + strBaseUserType);
//				System.out.println("strUserType ===>> " + strUserType);
//				if((strBaseUserType != null && strUserType != null && (strBaseUserType.equals(EMPLOYEE) || strUserType.equals(EMPLOYEE))) || !accessEmpList.contains(getStrEmpId()) || ((strUserType.equals(EMPLOYEE) || strUserType.equals(MANAGER)) && accessEmpList.contains(getStrEmpId())) ) {
				if((strBaseUserType != null && strUserType != null && (strBaseUserType.equals(EMPLOYEE) || strUserType.equals(EMPLOYEE))) 
					|| !accessEmpList.contains(getStrEmpId()) || (strUserType.equals(EMPLOYEE) && accessEmpList.contains(getStrEmpId())) ) {
					setStrEmpId((String)session.getAttribute(EMPID));
				}
//				System.out.println("getStrEmpId() ===>> " + getStrEmpId());
				setStrServiceId((String) request.getParameter("SID"));
				setStrMonth((String) request.getParameter("M"));
				setStrPCS((String) request.getParameter("PCS"));
				setStrPCE((String) request.getParameter("PCE"));
				setStrPC((String) request.getParameter("PC"));
				setStrFYS((String) request.getParameter("FYS"));
				setStrFYE((String) request.getParameter("FYE"));
				setStrBankPayType((String) request.getParameter("BPT"));
//				System.out.println(getStrEmpId() + " -- accessEmpList ===>> " + accessEmpList);
//			}
	
			int pdfType = PdfFormat();
//			System.out.println("pdfType="+pdfType);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
			int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
			if (nSalaryStrucuterType == S_GRADE_WISE) {
				if(pdfType == 4) {
					createSalarySlipPdfFourthFormatByGrade(getStrEmpId(),getStrMonth());
				} else if(pdfType == 5) {
					createSalarySlipPdfFifthFormatByGrade(getStrEmpId(),getStrMonth());
				} else {
					generateSalarySlipByGrade();
				}
			} else {
//				System.out.println("GSS/179---pdfType ===>> " + pdfType);
				if(pdfType == 4) {
					createSalarySlipPdfFourthFormat(getStrEmpId(),getStrMonth());
				} else if(pdfType == 5) {
					createSalarySlipPdfFifthFormat(getStrEmpId(),getStrMonth());
				} else if(pdfType == 6) {
					createSalarySlipPdfSixthFormat(getStrEmpId(),getStrMonth());
				} else if(pdfType == 7) {
					createSalarySlipPdfSeventhFormat(getStrEmpId(),getStrMonth());
			//===start parvez date: 02-09-2022===	
				} else if(pdfType == 8) {
					createSalarySlipPdfEightFormat(getStrEmpId(),getStrMonth());
				} else {
			//===end parvez date: 02-09-2022===		
					generateSalarySlip();
				}
			}
		}
//		return SUCCESS;

	}
	
private void createSalarySlipPdfSeventhFormat(String strEmpId, String strMonth) {
	PreparedStatement pst = null;
	ResultSet rs = null;
	Connection con = null;
	Database db = new Database();
	db.setRequest(request);
	
	try {
		
		con = db.makeConnection(con);
		boolean flagEpf = false;

		UtilityFunctions uF = new UtilityFunctions();
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get("SHOW_EMPLOYEE_MIDDLE_NAMEW"));
		Map<String, String> hmTemp =null;
		Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
		Map<String, String> hmEmpDepartmentMap = CF.getDepartmentMap(con,null, null);
		Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
		Map<String, String> hmLevelMap = CF.getLevelMap(con);
		Map<String, String> hmGradeMap = CF.getGradeMap(con);
		Map<String, String> hmBankNameMap = CF.getBankNameMap(con, uF);

		Map<String, String> hmOrganisationDetails = new HashMap<String, String>();
		
		
		pst =con.prepareStatement("select max(entry_date) as entry_date, paid_from, paid_to,paid_days,total_days ,payment_mode,month,year " +
				"from payroll_generation where emp_id = ? and financial_year_from_date=? AND financial_year_to_date=? AND month=? " +
				"and paycycle=? and (bank_pay_type=? or bank_pay_type is null) group by paid_from, paid_to,paid_days,total_days,payment_mode,month,year");
//		pst.setInt(1, 78);/
//		pst.setDate(2,java.sql.Date.valueOf("2015-04-01"));
//		pst.setDate(3, java.sql.Date.valueOf("2016-03-31"));
//		pst.setInt(4,12);
//		pst.setInt(5,22);
//		pst.setInt(6,1);
		
		pst.setInt(1, uF.parseToInt(getStrEmpId()));
		pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
		pst.setInt(4, uF.parseToInt(getStrMonth()));
		pst.setInt(5, uF.parseToInt(getStrPC()));
		pst.setInt(6, uF.parseToInt(getStrBankPayType()));
		rs = pst.executeQuery();
		String strEntryDate = null;
		String strPayCycleStart = null;
		String strPayCycleEnd = null;
		String strTotal_days = null;
		String strPaid_days = null;
		String strPresent_days = null;
		while(rs.next()) {
			strEntryDate = rs.getString("entry_date");
			strPayCycleStart = rs.getString("paid_from");
			strPayCycleEnd = rs.getString("paid_to");
		//===start parvez date: 04-03-2023 commented===	
//			strTotal_days =rs.getString("total_days");
//			strPaid_days =rs.getString("paid_days");
		//===end parvez date: 04-03-2023===	
			strPaymentMode =rs.getString("payment_mode");
			 pMonth =rs.getInt("month");
			 pYear =rs.getInt("year");
		}
		rs.close();
		pst.close();
		
	//===start parvez date: 04-03-2023===
		pst =con.prepareStatement("select * from approve_attendance where emp_id = ? and financial_year_start=? AND financial_year_end=? and paycycle=?");
		pst.setInt(1, uF.parseToInt(getStrEmpId()));
		pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
		pst.setInt(4, uF.parseToInt(getStrPC()));
		rs = pst.executeQuery();
		while(rs.next()) {
			strTotal_days =rs.getString("total_days");
			strPaid_days =rs.getString("paid_days");
		}
		rs.close();
		pst.close();
	//===end parvez date: 04-03-2023===	
		
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true and " +
				"isdisplay=true and effective_date = (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
				"and isdisplay=true and effective_date<=?) group by level_id");
		pst = con.prepareStatement(sbQuery.toString());
//		pst.setInt(1, 78);
//		pst.setInt(2, 78);
//		pst.setDate(3, java.sql.Date.valueOf(strPayCycleEnd));
		
		pst.setInt(1, uF.parseToInt(strEmpId));
		pst.setInt(2, uF.parseToInt(strEmpId));
		pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
		
		String strEffectiveDate = null;
		String strEmpLevelId = null; 
		rs = pst.executeQuery();
		while(rs.next()){
			strEffectiveDate = rs.getString("effective_date");
			strEmpLevelId = rs.getString("level_id");
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select erpf_contribution, erps_contribution,erdli_contribution,pf_admin_charges,edli_admin_charges from emp_epf_details where emp_id = ? and financial_year_start=? and financial_year_end=? and _month =? and paycycle=? ");
//		pst.setInt(1, 78);
//		pst.setDate(2,java.sql.Date.valueOf("2015-04-01"));
//		pst.setDate(3, java.sql.Date.valueOf("2016-03-31"));
//		pst.setInt(4, 12);
//		pst.setInt(5, 22);
		
		pst.setInt(1, uF.parseToInt(getStrEmpId()));
		pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
		pst.setInt(4, uF.parseToInt(getStrMonth()));
		pst.setInt(5, uF.parseToInt(getStrPC()));
//		
//		System.out.println("pst 6-1 ===>> " + pst);
//		System.out.println("************************pst**************"+pst);
		rs = pst.executeQuery();
		double erpf_contb = 0 ;
		double erps_contb = 0;
		double erdli_contib = 0;
		double pFadminChrges = 0;
		double edliAdminChrges = 0;
		double total = 0;
		while(rs.next()) {
			erpf_contb =rs.getDouble("erpf_contribution");
			erps_contb =rs.getDouble("erps_contribution");
			erdli_contib =rs.getDouble("erdli_contribution");
			pFadminChrges =rs.getDouble("pf_admin_charges");
			edliAdminChrges =rs.getDouble("edli_admin_charges");
		}
		rs.close();
		pst.close();
		
		total =erpf_contb + erps_contb + erdli_contib+pFadminChrges+edliAdminChrges;
		
	    monthName = uF.getShortMonth(pMonth)+" "+pYear;
	   
	 ///===start parvez date: 04-03-2023===   
//	    tDays = uF.uF.parseToInt(strTotal_days);
//       	pDays = uF.parseToInt(strPaid_days);
	    tDays = uF.parseToDouble(strTotal_days);
	    pDays = uF.parseToDouble(strPaid_days);
	//===end parvez date: 04-03-2023===    
       	unpaidDays = tDays - pDays;
       
	   	if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("1")) {
			strPaymentMode ="Bank Transfer";
		} else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")) {
			strPaymentMode ="Cash";
		} else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")) {
			strPaymentMode ="Cheque";
		}
	   	
	//	String strOrgId = CF.getEmpOrgId(con, uF, "78");
		String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
	
		pst = con.prepareStatement("select * from org_details where org_id = ?");
		pst.setInt(1, uF.parseToInt(strOrgId));
		rs = pst.executeQuery();
		while(rs.next()) {
			hmOrganisationDetails.put("ORG_ID", rs.getString("org_id"));
			hmOrganisationDetails.put("ORG_LOGO", rs.getString("org_logo"));
			hmOrganisationDetails.put("ORG_NAME", rs.getString("org_name"));
			hmOrganisationDetails.put("PDF_TYPE", rs.getString("payslip_format"));
			hmOrganisationDetails.put("ORG_ADDRESS", rs.getString("ORG_ADDRESS"));
			hmOrganisationDetails.put("ORG_CITY", rs.getString("ORG_CITY"));
	
		}
		rs.close();
		pst.close();
		
		
		pst = con.prepareStatement("select * from emp_it_slab_access_details where emp_id=? and fyear_start=? and fyear_end=?");
		pst.setInt(1, uF.parseToInt(getStrEmpId()));
		pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
		rs = pst.executeQuery();
		String strSlabType = "OLD SCHEME";
		while(rs.next()) {
			if(rs.getInt("slab_type") == 1) {
				strSlabType = "New SCHEME";
			}
		}
		rs.close();
		pst.close();
 
		
	   	pst = con.prepareStatement("SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id = eod.emp_id and emp_per_id = ?");
	//	pst.setInt(1, 78);
		pst.setInt(1, uF.parseToInt(getStrEmpId()));
		rs = pst.executeQuery();
		while (rs.next()) {
			Map<String, Map<String, String>> hmWorkLocationMap = CF.getWorkLocationMap(con);
			hmTemp = (Map<String, String>)hmWorkLocationMap.get(rs.getInt("wlocation_id")+"");
			
			if(hmTemp==null)hmTemp=new HashMap();
			strCompanyAddress = hmTemp.get("WL_ADDRESS")+" " +hmTemp.get("WL_CITY")+" " +hmTemp.get("WL_PINCODE");
			String strEmpLocationAddress = hmTemp.get("WL_ADDRESS");
			strLevelId = hmEmpLevelMap.get(rs.getString("emp_id"));
			String strEmpWorkLocation = hmTemp.get("WL_NAME");
			String strEmpLevel = hmLevelMap.get(strLevelId);
			String strGradeId = CF.getEmpGradeId(con, rs.getString("emp_id"));
			String strEmpGrade = hmGradeMap.get(strGradeId);
			
			empDetails.add(rs.getString("empcode"));
			empDetails.add(rs.getString("uan_no"));
	//		
			String strEmpMName = "";
			if(flagMiddleName) {
				if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
					strEmpMName = " "+rs.getString("emp_mname");
				}
			}
	//		
			empDetails.add(rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname")); //2
			empDetails.add(strTotal_days);
			empDetails.add(hmEmpDepartmentMap.get(rs.getString("depart_id"))); //4
			empDetails.add("0.00");
			empDetails.add(hmEmpDesigMap.get(rs.getString("emp_id"))); //6
			empDetails.add("0.00");
			empDetails.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT)); //8
			empDetails.add(strPaid_days);
			empDetails.add(rs.getString("emp_pan_no")); //10
			empDetails.add("0.00");
			empDetails.add(uF.showData(hmBankNameMap.get(rs.getString("emp_bank_name")), "-")); //12
			empDetails.add(uF.showData(uF.getMaskedNo(rs.getString("emp_bank_acct_nbr")), "-")); //13 Bank Ac. No.
			empDetails.add(uF.showData(uF.getMaskedNo(rs.getString("emp_pan_no")), "-"));//14 Pan Card
			empDetails.add(uF.showData(rs.getString("uan_no"), "-")); //15
			empDetails.add(uF.showData(uF.getMaskedNo(rs.getString("uid_no")), "-")); //16 Adhar Card
			empDetails.add(uF.showData(strSlabType, "-")); //17
			empDetails.add(uF.showData(rs.getString("emp_pf_no"), "-")); //18
			empDetails.add(uF.getDateFormat(rs.getString("pf_start_date"), DBDATE, DATE_FORMAT)); //19
			empDetails.add(uF.showData(strEmpWorkLocation, "-")); //20
			empDetails.add(uF.showData(strEmpLocationAddress, "-")); //21
			empDetails.add(uF.showData(strEmpLevel, "-")); //22
			empDetails.add(uF.showData(strEmpGrade, "-")); //23
			
			String strEmpFName = rs.getString("emp_fname").substring(0, 1).toUpperCase()+rs.getString("emp_fname").substring(1);
			strPassword = strEmpFName+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd")+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM");
			
		}
		rs.close();
		pst.close();
		
		List alLoans = new ArrayList();
		Map hmEmpLoan = new HashMap();
		Map<String, String> hmEmpSalLastEffectiveDate = null; //need to implement this map
		CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, uF.getDateFormat(strPayCycleStart, DBDATE, DATE_FORMAT), uF.getDateFormat(strPayCycleEnd,  DBDATE, DATE_FORMAT), hmEmpSalLastEffectiveDate);
		
		Map hmEmpLoanInner = (Map)hmEmpLoan.get(strEmpId); 
		if(hmEmpLoanInner==null) hmEmpLoanInner=new HashMap();
		Map<String, String> hmLoanPolicies = CF.getLoanPoliciesMap(con, uF, strOrgId);
		
//		Map<String, List<Map<String, String>>> hmPerkAlign = CF.getIndividualPerkAlignAmount(con, uF, CF, 78,"01/04/2015","31/03/2016","01/04/2015","31/03/2016",uF.parseToInt("2"));
		Map<String, List<Map<String, String>>> hmPerkAlign = CF.getIndividualPerkAlignAmount(con, uF, CF, uF.parseToInt(getStrEmpId()),getStrFYS(),getStrFYE(),getStrPCS(),getStrPCE(),uF.parseToInt(getStrPC()));
//		
//		Map<String,String> hmsalaryAmount = getSalaryAmount(con,uF,"78","","");
		Map<String,String> hmsalaryAmount = getSalaryAmount(con,uF,getStrEmpId(),getStrFYS(),getStrFYE());
		
		Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(strEmpLevelId));	
		
		if(hmPerkAlign == null) 
			hmPerkAlign = new HashMap<String, List<Map<String,String>>>();
		
			pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND pg.financial_year_from_date=? " +
				"AND pg.financial_year_to_date=? and (bank_pay_type=? or bank_pay_type is null) and pg.salary_head_id in (select distinct(salary_head_id) as salary_head_id from " +
				"emp_salary_details where isdisplay=true and emp_id=? and level_id=?) and amount>0 order by earning_deduction desc,pg.salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4,uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			pst.setInt(7, uF.parseToInt(getStrEmpId()));
			pst.setInt(8, uF.parseToInt(strEmpLevelId));
	        rs = pst.executeQuery();    
			double presentDays = 0;
			while (rs.next()) {
				presentDays = Double.parseDouble(rs.getString("present_days"));
				setStrPaymentDate(uF.getDateFormat(rs.getString("pay_date"), DBDATE, DATE_FORMAT_STR));
				
				Double dblTotal = 0.0;
				Double dblTotalGrossAmt = 0.0;
				if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
					
					/*dblTotal = uF.parseToDouble(hmsalaryAmount.get(rs.getString("salary_head_id"))); //OTHER ALLOWANCE/EX-
					dblTotalGrossAmt = rs.getDouble("amount");
					
					if(hmPerkAlign.containsKey(rs.getString("salary_head_id"))){
						List<Map<String, String>> alPerkAlign = hmPerkAlign.get(rs.getString("salary_head_id"));
						if(alPerkAlign == null) alPerkAlign = new ArrayList<Map<String,String>>();
						int nPerkAlignSize = alPerkAlign.size();
						for(int i = 0; i < nPerkAlignSize; i++){
							Map<String, String> hmPerkAlignInner = alPerkAlign.get(i);
							if(hmPerkAlignInner == null) hmPerkAlignInner = new HashMap<String, String>();
							salaryHeadName.add(uF.showData(hmPerkAlignInner.get("PERK_NAME"), "")+" ("+hmSalaryDetailsMap.get(rs.getString("salary_head_id"))+")");
							salHeadAmount.add(uF.parseToDouble(hmsalaryAmount.get(rs.getString("salary_head_id"))));
							salHeadAmountGross.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
						}
						
					} else{
						salaryHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						salHeadAmount.add(uF.parseToDouble(hmsalaryAmount.get(rs.getString("salary_head_id"))));
						salHeadAmountGross.add(dblTotalGrossAmt);
					}										
					
					dblTotalAmt += dblTotal;
					dblGrossTotal += dblTotalGrossAmt; */

				
					
					dblTotal = rs.getDouble("amount"); //OTHER ALLOWANCE/EX-
					dblTotalGrossAmt = rs.getDouble("amount");
					
					if(hmPerkAlign.containsKey(rs.getString("salary_head_id"))) {
						List<Map<String, String>> alPerkAlign = hmPerkAlign.get(rs.getString("salary_head_id"));
						if(alPerkAlign == null) alPerkAlign = new ArrayList<Map<String,String>>();
						int nPerkAlignSize = alPerkAlign.size();
						for(int i = 0; i < nPerkAlignSize; i++) {
							Map<String, String> hmPerkAlignInner = alPerkAlign.get(i);
							if(hmPerkAlignInner == null) hmPerkAlignInner = new HashMap<String, String>();
							salaryHeadName.add(uF.showData(hmPerkAlignInner.get("PERK_NAME"), "")+" ("+hmSalaryDetailsMap.get(rs.getString("salary_head_id"))+")");
							salHeadAmount.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
							salHeadAmountGross.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
						}
						
					} else {
						salaryHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						salHeadAmount.add(dblTotal);
						salHeadAmountGross.add(dblTotalGrossAmt);
					}										
					
					dblTotalAmt += dblTotal;
					dblGrossTotal += dblTotalGrossAmt;

				} else if (rs.getString("earning_deduction").equalsIgnoreCase("D")) {

					if(uF.parseToInt(rs.getString("salary_head_id"))==17){
						for(int i=0; i<alLoans.size(); i++){
							String loan=(String)alLoans.get(i)!=null?(String)alLoans.get(i) : "";
							String loanamt=uF.showData((String)hmEmpLoanInner.get(loan), "0");
							dblTotal = uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt)));
							deductionHeadName.add(hmLoanPolicies.get((String)alLoans.get(i)));
							deductionHeadAmount.add(uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt))));
							dblTotalDeduction += dblTotal;
						}
					} else {
						dblTotal = rs.getDouble("amount");
						deductionHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						deductionHeadAmount.add(dblTotal);
						dblTotalDeduction += dblTotal;
					}
				}
				
				nYear=rs.getInt("year");
				int salaryHeadId =Integer.parseInt(rs.getString("salary_head_id"));
				if(salaryHeadId == IConstants.EMPLOYEE_EPF){
					flagEpf = true; 
				}
			}     
          
			rs.close();
			pst.close(); 
			
			salaryHeadName.add("Total");
			salHeadAmount.add(dblTotalAmt);
			deductionHeadName.add("Deductions");
			deductionHeadName.add("Net Payable");
			
			dblNetSalary = dblGrossTotal - dblTotalDeduction;
			
			deductionHeadAmount.add(dblTotalDeduction);
			deductionHeadAmount.add(dblNetSalary);
			
			salHeadAmountGross.add(dblGrossTotal);

//			Map hmLeaveDatesType = new HashMap();
//			Map<String, Map<String, String>> leaveDetailsMap = CF.getActualLeaveDates(con, CF, uF, "01/04/2015","31/03/2016",  hmLeaveDatesType, true, null);
//			Map<String, String> leaveEmpDetailsMap = leaveDetailsMap.get("35");

//			Map<String, Map<String, String>> leaveDetailsMap = CF.getActualLeaveDates(con, CF, uF, getStrFYS(), getStrFYE(),  hmLeaveDatesType, true, null);
//			Map<String, String> leaveEmpDetailsMap = leaveDetailsMap.get(getStrEmpId());
			
//			List<String> alLeaveCount = getActualLeaveDates(con, CF, uF, getStrFYS(), getStrFYE());
			List<String> alLeaveCount = getActualLeaveDates(con, CF, uF, uF.getDateFormat(strPayCycleStart, DBDATE, DATE_FORMAT), uF.getDateFormat(strPayCycleEnd, DBDATE, DATE_FORMAT));
			
			double totLeaveBalance = employeeLeaveBalance();
			
//			if(leaveEmpDetailsMap!=null) {
//				
//				Set keys = leaveEmpDetailsMap.keySet();
//				for (Iterator i = keys.iterator(); i.hasNext();) {
//					String nkey = (String) i.next();
//					String strValue = (String) leaveEmpDetailsMap.get(nkey);
//					Iterator<String> itr = leaveName.iterator();	
//				  int count=0;
//				  while(itr.hasNext()) {
//	           	  
//		           	  if(strValue.equalsIgnoreCase(itr.next())) {
//		           		  count=1;
//		           	  }
//				  }
//				
//				if(count==0) {
//					leaveName.add(strValue);
//				}
//			}
//				
//			Iterator<String> itrleave = leaveName.iterator();	
//			int nleaveToatal=0;
//			while(itrleave.hasNext()) {
//				Set keysC = leaveEmpDetailsMap.keySet();
//				String strLeave = itrleave.next();
//				int nleaveCount=0;		
//				for (Iterator i = keysC.iterator(); i.hasNext();) {
//					String nkey = (String) i.next();
//					String strValue = (String) leaveEmpDetailsMap.get(nkey);
//					
//					if(strValue!=null && strValue.equalsIgnoreCase(strLeave)) {
//						nleaveCount++;
//						nleaveToatal++;
//					}
//				}  
//				noOfLeave.add(nleaveCount);
//			}	  
//			noOfLeave.add(nleaveToatal);
//			leaveName.add("Total");
//		}
//		generateSalarySlipSeventhFormat("78", "", hmOrganisationDetails,monthName,empDetails);
		generateSalarySlipSeventhFormat(getStrEmpId(), "", hmOrganisationDetails, monthName, empDetails, alLeaveCount, totLeaveBalance);
	} catch (Exception e) {
		e.printStackTrace();
	}
}


public double employeeLeaveBalance() {
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	
	double nTotRemaining = 0;
	UtilityFunctions uF = new UtilityFunctions();
//	int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
	int nEmpId = uF.parseToInt(getStrEmpId());
	
	try {
		con = db.makeConnection(con);
		
		int nCurrentYear = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));
		
		String[] strFinacialYear = CF.getFinancialYear(con, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), CF, uF);
		
		String strWlocationid = CF.getEmpWlocationId(con, uF, ""+nEmpId);
		String strOrgid= CF.getEmpOrgId(con, uF, ""+nEmpId);
		
		pst = con.prepareStatement("select * from probation_policy where emp_id=?");
	    pst.setInt(1, nEmpId);
	    rs = pst.executeQuery();
	    String strEmpLeaveTypeId = null;
	    while (rs.next()) { 
	    	strEmpLeaveTypeId = rs.getString("leaves_types_allowed");
	    }
	    int fyCnt=0;
		int cyCnt=0;
		
	    if(strEmpLeaveTypeId!=null && !strEmpLeaveTypeId.trim().equals("") && !strEmpLeaveTypeId.trim().equalsIgnoreCase("NULL")) {
			Map<String, String> hmLeaveDetails = new HashMap<String,String>();
			Map<String, String> hmLeaveEffectiveDateType = new HashMap<String,String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_leave_type elt,leave_type lt where lt.leave_type_id = elt.leave_type_id and level_id = (select dd.level_id from level_details ld, designation_details dd, grades_details gd " +
				" where ld.level_id = dd.level_id and gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?)) and wlocation_id=? and elt.org_id=? " +
				" and lt.is_compensatory=false and is_constant_balance=false and lt.is_work_from_home=false and lt.is_maternity=false and lt.is_leave_opt_holiday=false ");
			if(strEmpLeaveTypeId!=null && !strEmpLeaveTypeId.trim().equals("") && !strEmpLeaveTypeId.trim().equalsIgnoreCase("NULL")){
				sbQuery.append("and lt.leave_type_id in ("+strEmpLeaveTypeId+")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, nEmpId);
			pst.setInt(2, uF.parseToInt(strWlocationid));
			pst.setInt(3, uF.parseToInt(strOrgid));
//			System.out.println("1 pst=====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				hmLeaveDetails.put(rs.getString("leave_type_id"), rs.getString("leave_type_name"));
				hmLeaveEffectiveDateType.put(rs.getString("leave_type_id"), rs.getString("effective_date_type"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in (select max(register_id) from leave_register1 " +
				"where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) group by emp_id,leave_type_id)");
            pst.setInt(1, nEmpId);
//            System.out.println("2 pst=====>"+pst);
            rs = pst.executeQuery();
            Map<String, String> hmMainBalance=new HashMap<String, String>();
            while (rs.next()) {
                hmMainBalance.put(rs.getString("leave_type_id"), rs.getString("balance"));
            }
            rs.close();
            pst.close();
            
            pst = con.prepareStatement("select sum(accrued) as accrued,a.leave_type_id from (select max(_date) as daa,leave_type_id from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) " +
        		"group by emp_id,leave_type_id )as a,leave_register1 lr where emp_id=? and _type!='C' and a.leave_type_id=lr.leave_type_id and a.daa<=lr._date group by a.leave_type_id");
            pst.setInt(1, nEmpId);
            pst.setInt(2, nEmpId);
//            System.out.println("3 pst=====>"+pst);
            rs = pst.executeQuery();
            Map<String, String> hmAccruedBalance=new HashMap<String, String>();
            while (rs.next()) {
            	hmAccruedBalance.put(rs.getString("leave_type_id"), rs.getString("accrued"));                
            }
			rs.close();
			pst.close();
			
			 pst = con.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) " +
        		"and register_id in (select max(register_id) from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) group by emp_id,leave_type_id) " +
        		"group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) " +
        		"and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date) as a group by leave_type_id");
            pst.setInt(1, nEmpId);
            pst.setInt(2, nEmpId);
            pst.setInt(3, nEmpId);
//            System.out.println("4 pst=====>"+pst);
            rs = pst.executeQuery();
            Map<String, String> hmPaidBalance=new HashMap<String, String>();
            while (rs.next()) {
            	hmPaidBalance.put(rs.getString("leave_type_id"), rs.getString("count"));
            }
			rs.close();
			pst.close();
			Map<String,String> hmApproveLeaveStatus=new HashMap<String, String>();
			Map<String,String> hmLeaveStatus=new HashMap<String, String>();
			
			Iterator<String> it1 = hmLeaveEffectiveDateType.keySet().iterator();
			
			while(it1.hasNext()) {
				String strLeaveTypeId = it1.next();
				String strLeaveEffectiveDateType = hmLeaveEffectiveDateType.get(strLeaveTypeId);
				pst = con.prepareStatement("select sum(emp_no_of_leave) as cnt,leave_type_id,is_approved from emp_leave_entry where emp_id = ? and is_approved !=1 " +
					" and entrydate>=? and leave_type_id=? group by leave_type_id,is_approved");
				pst.setInt(1, nEmpId);
				if(strLeaveEffectiveDateType != null && strLeaveEffectiveDateType.equalsIgnoreCase("FY")) {
					pst.setDate(2, uF.getDateFormat(strFinacialYear[0], DATE_FORMAT));
					fyCnt++;
				} else {
					pst.setDate(2, uF.getDateFormat(nCurrentYear+"-01-01", DBDATE));
					cyCnt++;
				}
				pst.setInt(3, uF.parseToInt(strLeaveTypeId));
//				System.out.println("5 pst=====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					hmLeaveStatus.put(rs.getString("leave_type_id")+"_"+rs.getString("is_approved"), ""+rs.getDouble("cnt"));				
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select sum(leave_no) as cnt,leave_type_id from leave_application_register where emp_id = ? and is_paid=true and (is_modify is null or is_modify=false) " +
					" and _date between ? and ? and leave_type_id =? group by leave_type_id");
				pst.setInt(1, nEmpId);
				if(strLeaveEffectiveDateType != null && strLeaveEffectiveDateType.equalsIgnoreCase("FY")) {
					pst.setDate(2, uF.getDateFormat(strFinacialYear[0], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strFinacialYear[1], DATE_FORMAT));
				} else {
					pst.setDate(2, uF.getDateFormat(nCurrentYear+"-01-01", DBDATE));
					pst.setDate(3, uF.getDateFormat(nCurrentYear+"-12-31", DBDATE));
				}
				pst.setInt(4, uF.parseToInt(strLeaveTypeId));
//				System.out.println("6 pst=====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					hmApproveLeaveStatus.put(rs.getString("leave_type_id"), ""+rs.getDouble("cnt"));				
				}
				rs.close();
				pst.close();
			}
			
//			double nPending 	= 0; 
//			double nApproved 	= 0;
//			double nDenied 		= 0;
			
//			double nTotal 		= 0;
			
			Iterator it = hmLeaveDetails.keySet().iterator();
			List<List<String>> reportList = new ArrayList<List<String>>();
			StringBuilder sbRemainLeave=new StringBuilder();
			StringBuilder sbApprovedLeave=new StringBuilder();
			StringBuilder sbPendingLeave=new StringBuilder();
			StringBuilder sbLeaveTypeName=new StringBuilder();
			
			while(it.hasNext()){
				String strLeaveTypeId = (String)it.next();
				String leaveTypeName = hmLeaveDetails.get(strLeaveTypeId);
				
				double dblBalance = uF.parseToDouble(hmMainBalance.get(strLeaveTypeId));
				dblBalance += uF.parseToDouble(hmAccruedBalance.get(strLeaveTypeId));
				
				double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get(strLeaveTypeId));
				
				if(dblBalance > 0 && dblBalance >= dblPaidBalance){
		            dblBalance = dblBalance - dblPaidBalance; 
		        }
				
				double dblPending = uF.parseToDouble(hmLeaveStatus.get(strLeaveTypeId+"_0"));
				double dblApproved = uF.parseToDouble(hmApproveLeaveStatus.get(strLeaveTypeId));
				double dblDenied = uF.parseToDouble(hmLeaveStatus.get(strLeaveTypeId+"_-1"));
				double dblRemaining = dblBalance;
				nTotRemaining += dblRemaining;
				double dblTotal = dblRemaining + dblApproved;
				
				List<String> innerList=new ArrayList<String>();   
				innerList.add(leaveTypeName);
				innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblTotal)+"");
				innerList.add("<font color=\"orange\">"+uF.formatIntoTwoDecimalWithOutComma(dblPending)+"</font>");
				innerList.add("<font color=\"green\">"+uF.formatIntoTwoDecimalWithOutComma(dblApproved)+"</font>");
				innerList.add("<font color=\"red\">"+uF.formatIntoTwoDecimalWithOutComma(dblDenied)+"</font>");
				innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblRemaining)+"");
				innerList.add(strLeaveTypeId);
				innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblApproved));
				innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblPending));
				
				sbLeaveTypeName.append("'"+leaveTypeName+"'");
				sbApprovedLeave.append(dblApproved);
				sbPendingLeave.append(dblPending);
				sbRemainLeave.append(dblTotal-(dblApproved+dblPending));
				
				if(it.hasNext()){
					sbLeaveTypeName.append(",");
					sbApprovedLeave.append(",");
					sbRemainLeave.append(",");
					sbPendingLeave.append(",");
				}
				
				reportList.add(innerList);
			}
			
			//System.out.println("sbApprovedLeave==>"+sbApprovedLeave);
			//System.out.println("sbRemainLeave==>"+sbRemainLeave);
			//System.out.println("sbPendingLeave==>"+sbPendingLeave);
			//System.out.println("sbLeaveTypeName==>"+sbLeaveTypeName);
			
			request.setAttribute("leaveList", reportList);
			request.setAttribute("sbLeaveTypeName", sbLeaveTypeName);
			
	    }
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	
	return nTotRemaining;
}


public List<String> getActualLeaveDates(Connection con, CommonFunctions CF, UtilityFunctions uF, String strD1, String strD2) {
	PreparedStatement pst = null;
	ResultSet rs = null;
	List<String> alLeaveCount = new ArrayList<String>();
	try {
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("select emp_id,_date,leave_no,lt.leave_type_id,lt.leave_type_code,is_paid from leave_application_register lar, leave_type lt "
			+ "where lar.leave_type_id = lt.leave_type_id and _date between ? and ? and _type = true and lar.emp_id=? ");
//		if (isPaid) {
//			sbQuery.append(" and is_paid = true ");
//		}
		sbQuery.append(" and is_modify= false and lar.leave_type_id not in (select leave_type_id from leave_type where is_compensatory = true) "
			+ "and lar.leave_id in (select leave_id from emp_leave_entry) order by emp_id");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(getStrEmpId()));
//		System.out.println("pst ===>> " + pst);
		rs = pst.executeQuery();
		double dblLeaveCount = 0;
		double dblPaidLeaveCount = 0;
		while (rs.next()) {
//			String strEmpId = rs.getString("emp_id");
//			String strLeaveDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
			String strLeaveNo = rs.getString("leave_no");
//			String strLeaveTypeId = rs.getString("leave_type_id");
			String strIsPaid = rs.getString("is_paid");

			/**
			 * Leave Type (Half day or Full day)
			 * */
				
			if (uF.parseToDouble(strLeaveNo) == 0.5) {
				if(uF.parseToBoolean(strIsPaid)) {
					dblPaidLeaveCount += 0.5;
				}
				dblLeaveCount += 0.5;
			} else if (uF.parseToDouble(strLeaveNo) == 1) {
				if(uF.parseToBoolean(strIsPaid)) {
					dblPaidLeaveCount += 1;
				}
				dblLeaveCount += 1;
			}
		}
		rs.close();
		pst.close();
		

		sbQuery = new StringBuilder();
		sbQuery.append("select * from travel_application_register where _date between ? and ? and emp_id=? and is_modify= false ");
//		if (isPaid) {
//			sbQuery.append(" and is_paid = true ");
//		}
		sbQuery.append("order by emp_id");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
		pst.setInt(3, uF.parseToInt(getStrEmpId()));
//		System.out.println("pst ===>> " + pst);
		rs = pst.executeQuery();
//		dblLeaveCount = 0;
		while (rs.next()) {
//			String strEmpId = rs.getString("emp_id");
//			String strTravelDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
			String strTravelNo = rs.getString("travel_no");
			String strIsPaid = rs.getString("is_paid");

			/**
			 * Leave Type (Half day or Full day)
			 * */
			if (uF.parseToDouble(strTravelNo) == 0.5) {
				if(uF.parseToBoolean(strIsPaid)) {
					dblPaidLeaveCount += 0.5;
				}
				dblLeaveCount += 0.5;
			} else if (uF.parseToDouble(strTravelNo) == 1) {
				if(uF.parseToBoolean(strIsPaid)) {
					dblPaidLeaveCount += 1;
				}
				dblLeaveCount += 1;
			}
		}
		rs.close();
		pst.close();
		
		alLeaveCount.add(dblPaidLeaveCount+"");
		alLeaveCount.add(dblLeaveCount+"");

	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if (pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return alLeaveCount;
}


private void generateSalarySlipSeventhFormat(String strEmpId, String strMonth, Map<String, String> hmOrganisationDetails, String monthName, 
		List<String> empDetails, List<String> alLeaveCount, double totLeaveBalance) {
	
	UtilityFunctions uF = new UtilityFunctions();
	try {
		String strCompanyName = hmOrganisationDetails.get("ORG_NAME");
		String strCompanyAddress = hmOrganisationDetails.get("ORG_ADDRESS");
		String strCompanyLogo = hmOrganisationDetails.get("ORG_LOGO");
		String strWorkLocation = empDetails.get(20); //hmOrganisationDetails.get("ORG_CITY");
		Map<String,String> hmFeatureStatus = (Map<String,String>)request.getAttribute("hmFeatureStatus");
		
		String filePath = request.getRealPath("/userImages/" +strEmpImage+ "");
		String filePathDefault=request.getRealPath("/userImages/avatar_photo.png");
		String filePathCompanyLOgo = request.getRealPath("/userImages/"+strCompanyLogo+"");
		String filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
		String filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
		
		if(CF.getStrDocSaveLocation()!=null){
			filePath = CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+ strEmpId+"/"+strEmpImage;
			filePathDefault=request.getRealPath("/userImages/avatar_photo.png");
			filePathCompanyLOgo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+strCompanyLogo;
			filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
			filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
		}else{
			filePath = request.getRealPath("/userImages/" +strEmpImage+ "");
			filePathDefault=request.getRealPath("/userImages/avatar_photo.png");
			filePathCompanyLOgo = request.getRealPath("/userImages/"+strCompanyLogo+"");
			filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
			filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
		}  
		System.out.println("filePathCompanyLogo=====>"+filePathCompanyLOgo);
		Image imagePhoto=null;
		Image imageLogo=null;

		try{
			FileInputStream fileInputStream=null;
	        File file = new File(filePath);
	        byte[] bFile = new byte[(int) file.length()];
	        fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
		    imagePhoto = Image.getInstance(bFile);
		}catch(FileNotFoundException e){
			imagePhoto = Image.getInstance(filePathDefault);
		}
		
		try{
			FileInputStream fileInputStream1=null;
	        File file1 = new File(filePathCompanyLOgo);
	        byte[] bFile1 = new byte[(int) file1.length()];
	        fileInputStream1 = new FileInputStream(file1);
		    fileInputStream1.read(bFile1);
		    fileInputStream1.close();
	        imageLogo = Image.getInstance(bFile1);
		}catch(FileNotFoundException e){
			imageLogo = Image.getInstance(filePathCompanyLOgodefault);
		}

		com.itextpdf.text.Font heading = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 13);
		com.itextpdf.text.Font normal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11);
		com.itextpdf.text.Font normalwithbold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12,Font.BOLD);
		com.itextpdf.text.Font small = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7);
		com.itextpdf.text.Font smallBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.WHITE);
		com.itextpdf.text.Font italicEffect = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
		
		// Added hardcoded path
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//		  String fileName = "c:/temp/FirstPdf.pdf";
		
       com.itextpdf.text.Document document = new com.itextpdf.text.Document();
       com.itextpdf.text.pdf.PdfWriter pdfWriter = com.itextpdf.text.pdf.PdfWriter.getInstance(document, buffer);
       if(hmFeatureStatus != null &&uF.parseToBoolean(hmFeatureStatus.get(F_PASSWORD_PROTECTED_SALARY_SLIP))){
			pdfWriter.setEncryption(strPassword.getBytes(), strPassword.getBytes(), com.itextpdf.text.pdf.PdfWriter.ALLOW_PRINTING, com.itextpdf.text.pdf.PdfWriter.ENCRYPTION_AES_128);
		}
       document.open();
       
       com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(10);
		table.setWidthPercentage(100);       
		int[] cols = {3,10,10,10,10,10,10,10,10,10};
		table.setWidths(cols);
		PdfPTable companyLogoTable = new PdfPTable(1);
		companyLogoTable.setWidthPercentage(10);
		companyLogoTable.getDefaultCell().setPadding(1);
		companyLogoTable.getDefaultCell().setBorderWidth(0);
		companyLogoTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
		companyLogoTable.addCell(imageLogo);
		
		PdfPTable companyNameTable = new PdfPTable(1);
		int[] arrheaderwidthsh = { 100 }; // percentage
		companyNameTable.getDefaultCell().setBorderWidth(0);
		companyNameTable.getDefaultCell().setPadding(1);
//		companyNameTable.setWidths(arrheaderwidthsh);
		companyNameTable.setWidthPercentage(100);
		
//		PdfPCell companyNamecell = new PdfPCell(new Phrase(strCompanyName, FontFactory.getFont("Verdana", 14,Font.BOLD)));
//		companyNamecell.setBorderWidthTop(0);
//		companyNamecell.setBorderWidthBottom(0);
//		companyNamecell.setBorderWidthLeft(0);
//		companyNamecell.setBorderWidthRight(0);
//
//		companyNamecell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//		companyNameTable.addCell(companyNamecell);
		
//		PdfPCell companyNamecell1 = new PdfPCell(new Phrase(empDetails.get(21), FontFactory.getFont("Verdana", 10,Font.NORMAL)));
		PdfPCell companyNamecell1 = new PdfPCell(new Phrase(empDetails.get(21), small));
		companyNamecell1.setBorderWidthTop(0);
		companyNamecell1.setBorderWidthBottom(0);
		companyNamecell1.setBorderWidthLeft(0);
		companyNamecell1.setBorderWidthRight(0);
		companyNamecell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		companyNameTable.addCell(companyNamecell1);
		
		com.itextpdf.text.pdf.PdfPCell row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("",smallBold));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(10);
       row1.setPadding(2.5f);
       table.addCell(row1);
     
     //New Row
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Payslip for "+monthName, normalwithbold));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);	
       row1.setColspan(10);
       table.addCell(row1);
     
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(10);
       row1.setBorder(Rectangle.NO_BORDER);	
       table.addCell(row1);
         
//       String heading2="EMPLOYEE DETAILS";
     //New Row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(empDetails.get(2),small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(5);
       table.addCell(row1);
      
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Date of Joining",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(3);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(empDetails.get(8),small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       //new row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Employee Details",smallBold));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBackgroundColor(BaseColor.BLUE);
       row1.setColspan(4);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Payment Details",smallBold));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBackgroundColor(BaseColor.BLUE);
       row1.setColspan(3);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Location Details",smallBold));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBackgroundColor(BaseColor.BLUE);
       row1.setColspan(3);
       table.addCell(row1);
       //new row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("EMP NO.",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(empDetails.get(0),small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Bank Name",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(1);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(empDetails.get(12),small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Location",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(1);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(strWorkLocation,small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       //new row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Designation",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(""+empDetails.get(6),small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Account No",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(1);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(""+empDetails.get(13),small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Level - Grade",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(1);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(empDetails.get(22)+" - " + empDetails.get(23),small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
     //new row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("PAN",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(empDetails.get(14),small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("UAN",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(1);
       table.addCell(row1);
       
       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(""+empDetails.get(15),small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Member #", small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(1);
       table.addCell(row1);
       
       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(empDetails.get(18), small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       //new row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Aadhaar No.",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(empDetails.get(16),small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Days Paid",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(1);
       table.addCell(row1);
       
//       System.out.println("GSS/1233--pDays="+pDays);
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(""+pDays,small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("LWP/Absent",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(1);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(""+unpaidDays,small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       //new row       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Leave Details",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
     
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Paid",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(1);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(uF.showData(alLeaveCount.get(0), "0"), small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(1);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Availed",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(1);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(uF.showData(alLeaveCount.get(1), "0"), small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Balance",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(1);
       table.addCell(row1);
       
//       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(""+((noOfLeave !=null && noOfLeave.size()>1) ? noOfLeave.get(2) : ""), small));
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(uF.showData(totLeaveBalance+"", "0"), small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       //new row
//       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("UAN",small));
//       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//       row1.setColspan(2);
//       table.addCell(row1);
//       
//       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(""+empDetails.get(15),small));
//       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//       row1.setColspan(3);
//       table.addCell(row1);
//       
//       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Member #", small));
//       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//       row1.setColspan(2);
//       table.addCell(row1);
//       
//       
//       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(empDetails.get(18), small));
//       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//       row1.setColspan(3);
//       table.addCell(row1);
       
//       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Start Date", small));
//       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//       row1.setColspan(1);
//       table.addCell(row1);
//       
//       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(empDetails.get(19), small));
//       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
//       row1.setColspan(2);
//       table.addCell(row1);
       
       //new row
       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(10);
       row1.setBorder(Rectangle.NO_BORDER);	
       table.addCell(row1);
       
       
       //new row
       row1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Earnings", smallBold));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBackgroundColor(BaseColor.BLUE);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Arrears", smallBold));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBackgroundColor(BaseColor.BLUE);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Current", smallBold));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBackgroundColor(BaseColor.BLUE);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Deduction", smallBold));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBackgroundColor(BaseColor.BLUE);
       row1.setColspan(2);
       table.addCell(row1);
		
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Amount", smallBold));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBackgroundColor(BaseColor.BLUE);
       row1.setColspan(2);
       table.addCell(row1);
       
       
       Double netPayable =  deductionHeadAmount.get(deductionHeadAmount.size()-1);
       if(deductionHeadName.contains("Net Payable")) {
       deductionHeadName.remove(deductionHeadName.size()-1);
        deductionHeadAmount.remove(deductionHeadAmount.size() - 1);

       }
       
       Double totalDeduction =  deductionHeadAmount.get(deductionHeadAmount.size() - 1);
       if(deductionHeadName.contains("Deductions")) {
       deductionHeadName.remove(deductionHeadName.size() - 1);
        deductionHeadAmount.remove(deductionHeadAmount.size() - 1);

       }
       
       Double totalEarning = salHeadAmount.get(salHeadAmount.size() - 1);
       if(salaryHeadName.contains("Total")){
       	salaryHeadName.remove(salaryHeadName.size() - 1);
       	salHeadAmount.remove(salHeadAmount.size() - 1);
       }
       
       int nCount = 0;
		Iterator<String> itr1 = salaryHeadName.iterator();
		int dedeductionHeadNameSize=deductionHeadName.size();
		while (itr1.hasNext()) {

			String strHeadNM = itr1.next();
			if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
				
			}
			
			  row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(strHeadNM, small));
		       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
		       row1.setColspan(2);
		       table.addCell(row1);
		       
		       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("0", small));
		       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		       row1.setColspan(2);
		       table.addCell(row1);
		       
		       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(uF.formatIntoTwoDecimal(salHeadAmount.get(nCount)),small));
		       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		       row1.setColspan(2);
		       table.addCell(row1);
		       
		       if(nCount<dedeductionHeadNameSize) {
		    	   if (null!=deductionHeadName.get(nCount) ) {
			    	   row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(deductionHeadName.get(nCount),small));
				       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				       row1.setColspan(2);
				       table.addCell(row1);

				       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCount)),small));
				       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				       row1.setColspan(2);
				       table.addCell(row1);
		    	   } else {
		    		   row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
		    		   row1.setHorizontalAlignment(Element.ALIGN_LEFT);
		    		   row1.setColspan(2);
		    		   table.addCell(row1);

		    		   row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
		    		   row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		    		   row1.setColspan(2);
		    		   table.addCell(row1);
		    	   }
			       
		       } else {
		    	   row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
			       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			       row1.setColspan(2);
			       table.addCell(row1);

			       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
			       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			       row1.setColspan(2);
			       table.addCell(row1);
		       }
		       nCount++;
		}
		
       //new row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setColspan(2);
       table.addCell(row1);
       
       //new row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total  Earnings (Arrears + Current)",small));
       row1.setHorizontalAlignment(Element.ALIGN_CENTER);
       row1.setColspan(4);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(uF.formatIntoTwoDecimal(totalEarning),small));
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total Deduction",small));
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(uF.formatIntoTwoDecimal(totalDeduction),small));
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setColspan(2);
       table.addCell(row1);
       
       //new row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("   ",small));
       row1.setBorder(Rectangle.NO_BORDER);	
       row1.setHorizontalAlignment(Element.ALIGN_CENTER);
       row1.setColspan(6);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("NET PAY",small));
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setColspan(2);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(uF.formatIntoTwoDecimal(netPayable), small));
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setColspan(2);
       table.addCell(row1);
       
       //new row
//       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("", small));
//       row1.setBorder(Rectangle.NO_BORDER);	
//       row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//       row1.setColspan(6);
//       table.addCell(row1);
//       
//       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Date of Payment", small));
//       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//       row1.setColspan(2);
//       table.addCell(row1);
//       
//       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(getStrPaymentDate(), small));
//       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//       row1.setColspan(2);
//       table.addCell(row1);
       
       //new row
//       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("", small));
//       row1.setBorder(Rectangle.NO_BORDER);	
//       row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//       row1.setColspan(6);
//       table.addCell(row1);
//       
//       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("TAX METHOD FOLLOWED", small));
//       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//       row1.setColspan(2);
//       table.addCell(row1);
//       
//       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(empDetails.get(17), small));
//       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//       row1.setColspan(2);
//       table.addCell(row1);
       
       
       //new row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("   ",small));
       row1.setBorder(Rectangle.NO_BORDER);	
       row1.setHorizontalAlignment(Element.ALIGN_CENTER);
       row1.setColspan(6);
       table.addCell(row1);
       
       
       //new row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("   ",small));
       row1.setBorder(Rectangle.NO_BORDER);	
       row1.setHorizontalAlignment(Element.ALIGN_CENTER);
       row1.setColspan(6);
       table.addCell(row1);
       
		
		PdfPTable signatureTable = new PdfPTable(1);
		int[] arrheaderwidths14 = { 100 }; // percentage
		signatureTable.getDefaultCell().setBorderWidth(1);
		signatureTable.getDefaultCell().setPadding(1);

		signatureTable.setWidths(arrheaderwidths14);
		
		int	nyeardevfirst=nYear;
		nyeardevfirst=nYear%10;
		nYear=nYear/10;
		int	nyeardevsecond=nYear%10;
		PdfPCell cellUvU = new PdfPCell(new Phrase("This is computer generated PaySlip and does not require signature.",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
		cellUvU.setBorderWidth(1);
		signatureTable.addCell(cellUvU);

       document.add(companyLogoTable);
       document.add(companyNameTable);
       document.add(table);
       document.add(signatureTable);
       document.close();
       
		String filename="PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf";
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename="+filename+"");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
	} catch (Exception e) {
		e.printStackTrace();  
	}

}


public void generateSalarySlipByGrade() throws SQLException {
		
		Database dB = new Database();
		dB.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = dB.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmTemp =null;
			Map<String, String> hmOrganisationDetails = new HashMap<String, String>();
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpDepartmentMap = CF.getDepartmentMap(con,null, null);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
//			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con);
			
//			int nEmpLevelId = CF.getEmpLevelId(getStrEmpId(), request);
//			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con, nEmpLevelId);
//			String strEmpGradeId = CF.getEmpGradeId(con, getStrEmpId());
			
			pst =con.prepareStatement("select max(entry_date) as entry_date, paid_from, paid_to,paid_days,total_days ,payment_mode,month,year " +
					"from payroll_generation where emp_id=? and financial_year_from_date=? AND financial_year_to_date=? AND month=? " +
					"and paycycle=? and (bank_pay_type=? or bank_pay_type is null) group by paid_from, paid_to,paid_days,total_days,payment_mode,month,year");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(getStrPC()));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			rs = pst.executeQuery();
			String strEntryDate = null;
			String strPayCycleStart = null;
			String strPayCycleEnd = null;
			String strTotal_days =null;
			String strPaid_days = null;
			while(rs.next()){
				strEntryDate = rs.getString("entry_date");
				strPayCycleStart = rs.getString("paid_from");
				strPayCycleEnd = rs.getString("paid_to");
			//===start parvez date: 04-03-2023 commented for paid days coming in integer===	
//				strTotal_days =rs.getString("total_days");
//				strPaid_days =rs.getString("paid_days");
			//===end parvez date: 04-03-2023===	
				strPaymentMode =rs.getString("payment_mode");
				 pMonth =rs.getInt("month");
				 pYear =rs.getInt("year");
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 04-03-2023===
			pst =con.prepareStatement("select * from approve_attendance where emp_id = ? and financial_year_start=? AND financial_year_end=? and paycycle=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrPC()));
			rs = pst.executeQuery();
			while(rs.next()) {
				strTotal_days =rs.getString("total_days");
				strPaid_days =rs.getString("paid_days");
			}
			rs.close();
			pst.close();
		//===end parvez date: 04-03-2023===
			
		    monthName = uF.getMonth(pMonth)+" "+pYear;
		//===start parvez date: 04-03-2023===   
//           tDays = uF.parseToInt(strTotal_days);
//           pDays = uF.parseToInt(strPaid_days);
           tDays = uF.parseToDouble(strTotal_days);
           pDays = uF.parseToDouble(strPaid_days);
       //===end parvez date: 04-03-2023===    
           
           unpaidDays = tDays - pDays;
           
           System.out.println("Total :"+tDays+"PaidDays :"+pDays+"Unpaid Days :"+unpaidDays);
	            
			if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("1")){
				strPaymentMode ="Bank Transfer";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cash";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cheque";	
			}
				
//			System.out.println("Payment Mode  :"+strPaymentMode);
	            
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			
			pst = con.prepareStatement("select * from org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
		//	Map<String, String> hmOrganisationDetails = new HashMap<String, String>();
			while(rs.next()){
				hmOrganisationDetails.put("ORG_ID", rs.getString("org_id"));
				hmOrganisationDetails.put("ORG_LOGO", rs.getString("org_logo"));
				hmOrganisationDetails.put("ORG_NAME", rs.getString("org_name"));
				hmOrganisationDetails.put("PDF_TYPE", rs.getString("payslip_format"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("SELECT MAX(effective_date) as effective_date, grade_id FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
//				"and isdisplay=true and effective_date<=? group by grade_id");
			sbQuery.append("SELECT MAX(effective_date) as effective_date, grade_id FROM emp_salary_details WHERE emp_id=? and is_approved=true and " +
					"isdisplay=true and effective_date = (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
					"and isdisplay=true and effective_date<=?) group by grade_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
//				System.out.println("pst ================>> " + pst);
			String strEffectiveDate = null;
			String strEmpGradeId = null;
			rs = pst.executeQuery();
			while(rs.next()) {
				strEffectiveDate = rs.getString("effective_date");
				strEmpGradeId = rs.getString("grade_id");
			}
			rs.close();
			pst.close();
//			System.out.println("strEmpGradeId ===>> " + strEmpGradeId);
			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMapByGrade(con, uF.parseToInt(strEmpGradeId));
			
			pst = con.prepareStatement("SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id  = eod.emp_id and emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map hmWorkLocationMap = CF.getWorkLocationMap(con);
				hmTemp = (Map<String, String>)hmWorkLocationMap.get(rs.getInt("wlocation_id")+"");
				if(hmTemp==null)hmTemp=new HashMap();
				strCompanyAddress = hmTemp.get("WL_ADDRESS")+" " +hmTemp.get("WL_CITY")+" " +hmTemp.get("WL_PINCODE");
				
				strLevelId = hmEmpLevelMap.get(rs.getString("emp_id"));
				
				/*
				empName = rs.getString("emp_fname") + " "+ rs.getString("emp_lname");
				empId=hmEmpDesigMap.get(rs.getString("emp_id"));
				empCode =rs.getString("empcode");
				empBankAccNo =rs.getString("emp_bank_acct_nbr");
				empGradeId =hmGradeMap.get(rs.getString("grade_id"));
				empPfNo =rs.getString("emp_pf_no");
				wl_Name =hmTemp.get("WL_NAME"); // pune
				joining_Date =uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT);
				dpartId =uF.showData(hmEmpDepartmentMap.get(rs.getString("depart_id")), ""); 
			    empPanNo =rs.getString("emp_pan_no");
				payCycleEnd =uF.getDateFormat(getStrMonth(), "MM", "MMMM")+", "+ uF.getDateFormat(strPayCycleEnd, DBDATE, "yy");
				empBankName =rs.getString("emp_bank_name");
				*/
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				empDetails.add(rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname"));
				empDetails.add(hmEmpDesigMap.get(rs.getString("emp_id")));
				empDetails.add(rs.getString("empcode"));
				if(!(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==3)){
					empDetails.add(hmLevelMap.get(strLevelId));
				}
				empDetails.add(rs.getString("emp_bank_acct_nbr"));
				if(!(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==3)){
					empDetails.add(hmGradeMap.get(rs.getString("grade_id")));
				}
//				empDetails.add(rs.getString("emp_gpf_no"));
				empDetails.add(rs.getString("emp_pf_no"));
				empDetails.add(hmTemp.get("WL_NAME"));
				empDetails.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				empDetails.add(hmEmpDepartmentMap.get(rs.getString("depart_id")));
				empDetails.add(rs.getString("emp_pan_no"));
				if(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==2){
					empDetails.add(rs.getString("emp_bank_name"));
					empDetails.add(uF.showData(hmEmpDepartmentMap.get(rs.getString("depart_id")), ""));
				}
//				empDetails.add(strPayMode);
//				if(!(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==3)){
//				 empDetails.add(uF.getDateFormat(getStrMonth(), "MM", "MMMM")+", "+ uF.getDateFormat(strPayCycleEnd, DBDATE, "yy"));
//				} else {
//				System.out.println("monthName====>"+monthName);
					empDetails.add(monthName);
					empDetails.add(rs.getString("uan_no"));
					empDetails.add(" ");
//				}
				strEmpImage = rs.getString("emp_image");
				
				String strEmpFName = rs.getString("emp_fname").substring(0, 1).toUpperCase()+rs.getString("emp_fname").substring(1);
				strPassword = strEmpFName+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd")+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM");
			
//				strOrgId =  rs.getString("org_id");
			}
			rs.close();
			pst.close();
			
			if(!(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==3)){
			pst = con.prepareStatement("select bd.bank_name as bankname from bank_details bd,branch_details brd where bd.bank_id=brd.bank_id and brd.branch_id =?");
			pst.setInt(1,uF.parseToInt(empDetails.get(11)));
			rs = pst.executeQuery();
			while(rs.next()){
				bankName = rs.getString("bankname");
			}
			rs.close();
			pst.close();
			}
			
			List alLoans = new ArrayList();
			Map hmEmpLoan = new HashMap();
			Map<String, String> hmEmpSalLastEffectiveDate = null; //need to implement this map
			CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, uF.getDateFormat(strPayCycleStart, DBDATE, DATE_FORMAT), uF.getDateFormat(strPayCycleEnd, DBDATE, DATE_FORMAT), hmEmpSalLastEffectiveDate);
			Map hmEmpLoanInner = (Map)hmEmpLoan.get(strEmpId); 
			if(hmEmpLoanInner==null) hmEmpLoanInner=new HashMap();
			Map<String, String> hmLoanPolicies = CF.getLoanPoliciesMap(con, uF, strOrgId);
//			String strEmpLevelId = CF.getEmpLevelId(con, getStrEmpId());
			
			Map<String, List<Map<String, String>>> hmPerkAlign = CF.getIndividualPerkAlignAmount(con, uF, CF, uF.parseToInt(getStrEmpId()),getStrFYS(),getStrFYE(),getStrPCS(),getStrPCE(),uF.parseToInt(getStrPC()));
			if(hmPerkAlign == null) hmPerkAlign = new HashMap<String, List<Map<String,String>>>();
			
			/*pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg,salary_details sd where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND " +
				"pg.financial_year_from_date=? AND pg.financial_year_to_date=? and sd.grade_id=? and pg.salary_head_id = sd.salary_head_id and " +
				"bank_pay_type=? and (sd.is_delete is null or sd.is_delete=false) and pg.salary_head_id in (select salary_head_id from emp_salary_details " +
				"where isdisplay=true and grade_id=? and emp_id=?) order by earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strEmpGradeId));
			pst.setInt(7, uF.parseToInt(getStrBankPayType()));
			pst.setInt(8, uF.parseToInt(strEmpGradeId));
			pst.setInt(9, uF.parseToInt(getStrEmpId()));*/
			
			pst = con.prepareStatement("SELECT sum (pg.amount) as amount, pg.salary_head_id,pg.earning_deduction,pg.year FROM payroll_generation pg where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND pg.financial_year_from_date=? " +
				"AND pg.financial_year_to_date=? and (bank_pay_type=? or bank_pay_type is null) and pg.salary_head_id in (select distinct(salary_head_id) as salary_head_id from " +
				"emp_salary_details where isdisplay=true and emp_id=? and grade_id=?) group by pg.salary_head_id,pg.earning_deduction,pg.year order by earning_deduction desc,pg.salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			pst.setInt(7, uF.parseToInt(getStrEmpId()));
			pst.setInt(8, uF.parseToInt(strEmpGradeId));
//			System.out.println("pst====>"+pst);
			rs=pst.executeQuery();            
			while (rs.next()) {

				Double dblTotal = 0.0;
				Double dblTotalGrossAmt = 0.0;
				if (rs.getString("earning_deduction").equalsIgnoreCase("E")) {
					dblTotal = rs.getDouble("amount"); //OTHER ALLOWANCE/EX-
					dblTotalGrossAmt = rs.getDouble("amount");
					if(hmPerkAlign.containsKey(rs.getString("salary_head_id"))){
						List<Map<String, String>> alPerkAlign = hmPerkAlign.get(rs.getString("salary_head_id"));
						if(alPerkAlign == null) alPerkAlign = new ArrayList<Map<String,String>>();
						int nPerkAlignSize = alPerkAlign.size();
						for(int i = 0; i < nPerkAlignSize; i++){
							Map<String, String> hmPerkAlignInner = alPerkAlign.get(i);
							if(hmPerkAlignInner == null) hmPerkAlignInner = new HashMap<String, String>();
							salaryHeadName.add(uF.showData(hmPerkAlignInner.get("PERK_NAME"), "")+" ("+hmSalaryDetailsMap.get(rs.getString("salary_head_id"))+")");
							salHeadAmount.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
							salHeadAmountGross.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
						}
					} else {
						salaryHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						salHeadAmount.add(dblTotal);
						salHeadAmountGross.add(dblTotalGrossAmt);
					}
					
					dblTotalAmt += dblTotal;
					dblGrossTotal += dblTotalGrossAmt;

				} else if (rs.getString("earning_deduction").equalsIgnoreCase("D")) {
					if(uF.parseToInt(rs.getString("salary_head_id"))==LOAN) {
						for(int i=0; i<alLoans.size(); i++) {
							String loan=(String)alLoans.get(i)!=null?(String)alLoans.get(i) : "";
							String loanamt=uF.showData((String)hmEmpLoanInner.get(loan), "0");
							dblTotal = uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt)));
							deductionHeadName.add(hmLoanPolicies.get((String)alLoans.get(i)));
							deductionHeadAmount.add(uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt))));
							dblTotalDeduction += dblTotal;
						}
					} else {
						dblTotal = rs.getDouble("amount");
						deductionHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						deductionHeadAmount.add(dblTotal);
						dblTotalDeduction += dblTotal;
					}
				}
				nYear=rs.getInt("year");
			}     
			rs.close();
			pst.close();
			
			salaryHeadName.add("Total");
			salHeadAmount.add(dblTotalAmt);
			deductionHeadName.add("Total Deduction");
			deductionHeadAmount.add(dblTotalDeduction);
			salHeadAmountGross.add(dblGrossTotal);

			dblNetSalary = dblTotalAmt - dblTotalDeduction;
//			System.out.println("dblTotalAmt=====>"+dblTotalAmt+"---dblTotalDeduction=====>"+dblTotalDeduction+"---dblNetSalary=====>"+dblNetSalary);
			
//			pst = con.prepareStatement("select * from emp_salary_details where emp_id = ? and effective_date = (select max(effective_date) as effective_date " +
//					"from emp_salary_details where effective_date <= ? and emp_id=? and grade_id=?) and grade_id=? order by salary_head_id");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setDate(2, uF.getDateFormat(strEntryDate, DBDATE));
//			pst.setInt(3, uF.parseToInt(getStrEmpId()));
//			pst.setInt(4, uF.parseToInt(strEmpGradeId));
//			pst.setInt(5, uF.parseToInt(strEmpGradeId));
//			rs = pst.executeQuery();
//			while(rs.next()){
//				alGross.add(rs.getString("amount"));
//			}
//			rs.close();
//			pst.close();
		
			/*payEmpHead.add("PAY SLIP FOR THE MONTH OF");*/
			
			payEmpHead.add("EMPLOYEE NAME");
			payEmpHead.add("DESIGNATION");
			payEmpHead.add("EMPLOYEE CODE");
			if(!(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==3)){
				payEmpHead.add("LEVEL");
			}
			payEmpHead.add("ACC. NO.");
			if(!(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==3)){
				payEmpHead.add("GRADE");
			}
//			payEmpHead.add("GPF ACC. NO.");
			payEmpHead.add("PF ACC. NO.");
			payEmpHead.add("BRANCH");
			payEmpHead.add("JOINING DATE");
			payEmpHead.add("DEPARTMENT");
			payEmpHead.add("PAN NO");
			payEmpHead.add("MONTH");
			payEmpHead.add("UAN NO.");
			payEmpHead.add("");
			
			Map hmLeaveDatesType = new HashMap();
//			Map<String, Map<String, String>> leaveDetailsMap = CF.getLeaveDates(con,getStrFYS(), getStrFYE(), CF, hmLeaveDatesType, true, null);
			Map<String, Map<String, String>> leaveDetailsMap = CF.getActualLeaveDates(con, CF, uF, getStrFYS(), getStrFYE(),  hmLeaveDatesType, true, null);
			
			Map<String, String> leaveEmpDetailsMap = leaveDetailsMap.get(getStrEmpId());

			if(leaveEmpDetailsMap!=null)
			{
			
			Set keys = leaveEmpDetailsMap.keySet();
			for (Iterator i = keys.iterator(); i.hasNext();) {
				String nkey = (String) i.next();
				String strValue = (String) leaveEmpDetailsMap.get(nkey);

				Iterator<String> itr = leaveName.iterator();	
			  int count=0;
             while(itr.hasNext())
             {
           	  
           	  if(strValue.equalsIgnoreCase(itr.next()));
           	  {
           		  count=1;
           	  }
           }
			
			if(count==0)
			{
				leaveName.add(strValue);
			}
		
		}
			
			Iterator<String> itrleave = leaveName.iterator();	
			  int nleaveToatal=0;
				while(itrleave.hasNext()) {				
					Set keysC = leaveEmpDetailsMap.keySet();
					String strLeave = itrleave.next();
					int nleaveCount=0;
					
					for (Iterator i = keysC.iterator(); i.hasNext();) {
						String nkey = (String) i.next();
						String strValue = (String) leaveEmpDetailsMap.get(nkey);
						
						if(strValue!=null && strValue.equalsIgnoreCase(strLeave)) {
							nleaveCount++;
							
							nleaveToatal++;
						}
					}  
					noOfLeave.add(nleaveCount);
				}	  
				noOfLeave.add(nleaveToatal);

				leaveName.add("Total");
			}
			
			pst = con.prepareStatement("select sum(accrued_leaves) as accru, sum(taken_leaves) as taken_leaves, (sum(accrued_leaves) - sum(taken_leaves)) as balance, leave_type_id from leave_register where emp_id = ? group by leave_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				hmBalanceLeave.put(rs.getString("leave_type_id"), rs.getString("balance"));
			}
			rs.close();
			pst.close();
			hmLeaveNameMap = CF.getLeaveTypeMap(con);
			
			if(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==2){
			    createSalarySlipPdfSecondFormatByGrade(getStrEmpId(),getStrMonth(), hmOrganisationDetails);
			} else if(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==3){
				createSalarySlipPdfThirdFormatByGrade(getStrEmpId(),getStrMonth(), hmOrganisationDetails);
			} else {
				pdfCreationPayslipByGrade(getStrEmpId(),getStrMonth(), hmOrganisationDetails);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			dB.closeResultSet(rs);
			dB.closeStatements(pst);
			dB.closeConnection(con);
		}
	}



	public void  pdfCreationPayslipByGrade(String strEmpId, String strMonth, Map<String, String> hmOrganisationDetails){
		UtilityFunctions uF = new UtilityFunctions();
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
	//		String strCompanyLogo=CF.getStrOrgLogo();
			String strCompanyLogo=hmOrganisationDetails.get("ORG_LOGO");
			String strCompanyName=hmOrganisationDetails.get("ORG_NAME");
			String orgId =hmOrganisationDetails.get("ORG_ID");
			Map<String,String> hmFeatureStatus = (Map<String,String>)request.getAttribute("hmFeatureStatus");
			
			Document document = new Document();
			PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
			if(hmFeatureStatus != null &&uF.parseToBoolean(hmFeatureStatus.get(F_PASSWORD_PROTECTED_SALARY_SLIP))){
				pdfWriter.setEncryption(strPassword.getBytes(), strPassword.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
			}
			document.open();
			document.add(new Paragraph(" "));
	
			
			String filePath = request.getRealPath("/userImages/" +strEmpImage+ "");
			String filePathDefault=request.getRealPath("/userImages/avatar_photo.png");
			String filePathCompanyLOgo = request.getRealPath("/userImages/"+strCompanyLogo+"");
			String filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
			String filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
			
			/*String filePath = null;
			String filePathDefault=null;
			String filePathCompanyLOgo = null;
			String filePathCompanyLOgodefault=null;
			String filePathproductLogo= null;*/
			
	//		if(CF.getIsRemoteLocation()){
			if(CF.getStrDocSaveLocation()!=null){
	//			filePath = CF.getStrDocRetriveLocation() +strEmpImage;
	//			filePath = CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+ strEmpId+"/"+strEmpImage;
				filePath = CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+ strEmpId+"/"+strEmpImage;
				filePathDefault=request.getRealPath("/userImages/avatar_photo.png");
				
	//			filePathCompanyLOgo = CF.getStrDocRetriveLocation()+strCompanyLogo;
	//			filePathCompanyLOgo = CF.getStrDocRetriveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+strCompanyLogo;
				filePathCompanyLOgo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+strCompanyLogo;
				filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
				filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
			}else{
				filePath = request.getRealPath("/userImages/" +strEmpImage+ "");
				filePathDefault=request.getRealPath("/userImages/avatar_photo.png");
				filePathCompanyLOgo = request.getRealPath("/userImages/"+strCompanyLogo+"");
				filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
				filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
			}  
	//		System.out.println("filePath=====>"+filePath);
			Image imagePhoto=null;
			Image imageLogo=null;
			//Image imageProductLogo=Image.getInstance(filePathproductLogo);
			try{
	//			System.out.println("filePath========>"+filePath); 
				
				FileInputStream fileInputStream=null;
		        File file = new File(filePath);
		        byte[] bFile = new byte[(int) file.length()];
		        fileInputStream = new FileInputStream(file);
			    fileInputStream.read(bFile);
			    fileInputStream.close();
			    imagePhoto = Image.getInstance(bFile);
				
	//			imagePhoto = Image.getInstance(filePath);
	//			imagePhoto = Image.getInstance(filePathDefault);
			}catch(FileNotFoundException e){
				imagePhoto = Image.getInstance(filePathDefault);
			}
			
			try{
	//			System.out.println("filePathCompanyLOgo========>"+filePathCompanyLOgo);
				
				FileInputStream fileInputStream1=null;
		        File file1 = new File(filePathCompanyLOgo);
		        byte[] bFile1 = new byte[(int) file1.length()];
		        fileInputStream1 = new FileInputStream(file1);
			    fileInputStream1.read(bFile1);
			    fileInputStream1.close();
		        imageLogo = Image.getInstance(bFile1);
		        
	//			imageLogo = Image.getInstance(filePathCompanyLOgo);
	//			imageLogo = Image.getInstance(filePathCompanyLOgodefault); 
			}catch(FileNotFoundException e){
				imageLogo = Image.getInstance(filePathCompanyLOgodefault);
			}
			
			PdfPTable photoImagetable = new PdfPTable(1);
			photoImagetable.setWidthPercentage(10);
			photoImagetable.getDefaultCell().setPadding(1);
	
			photoImagetable.addCell(imagePhoto);
	
			PdfPTable companyNameTable = new PdfPTable(1);
			int[] arrheaderwidthsh = { 100 }; // percentage
			companyNameTable.getDefaultCell().setBorderWidth(0);
			companyNameTable.setWidths(arrheaderwidthsh);
			/*PdfPCell companyNamecell = new PdfPCell(new Phrase(
					CF.getStrOrgName(), FontFactory.getFont("Verdana", 14,
							Font.BOLD)));*/
			PdfPCell companyNamecell = new PdfPCell(new Phrase(strCompanyName, FontFactory.getFont("Verdana", 14,Font.BOLD)));
			
	
			companyNamecell.setBorderWidthTop(0);
			companyNamecell.setBorderWidthBottom(1);
			companyNamecell.setBorderWidthLeft(0);
			companyNamecell.setBorderWidthRight(0);
	
			companyNamecell.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell);
			PdfPCell companyNamecell1 = new PdfPCell(new Phrase(strCompanyAddress, FontFactory.getFont("Verdana", 10,Font.BOLD)));
	
			companyNamecell1.setBorderWidthTop(0);
			companyNamecell1.setBorderWidthBottom(0);
			companyNamecell1.setBorderWidthLeft(0);
			companyNamecell1.setBorderWidthRight(0);
	
			companyNamecell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell1);
	
			PdfPTable LogoImage = new PdfPTable(2);
			int[] arrheaderwidths1 = { 70, 30 }; // percentage
			LogoImage.getDefaultCell().setBorderWidth(1);
			LogoImage.setWidths(arrheaderwidths1);
			LogoImage.setTotalWidth(600); 
			LogoImage.addCell(companyNameTable);
			LogoImage.addCell(imageLogo);
	
			PdfPTable titalTable = new PdfPTable(1);
			int[] arrheaderwidths2 = { 100 }; // percentage
			titalTable.getDefaultCell().setBorderWidth(1);
			titalTable.getDefaultCell().setPadding(1);
			titalTable.setWidths(arrheaderwidths2);
	
			PdfPCell cellb = new PdfPCell(new Phrase(" PAY SLIP ",FontFactory.getFont("Verdana", 15, Font.BOLD)));
	
			cellb.getExtraParagraphSpace();
			cellb.setBorderWidth(1);
			cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
			titalTable.addCell(cellb);
	
			PdfPTable empTable = new PdfPTable(6);
	
			int[] arrheaderwidths5 = { 31, 2, 30, 15, 2, 20 }; // percentage
			empTable.getDefaultCell().setBorderWidth(1);
	
			empTable.setWidths(arrheaderwidths5);
			empTable.setTotalWidth(800);
			
	//		Iterator<String> itr = empDetails.iterator();
			Iterator<String> itr = payEmpHead.iterator();
			int k = 0;
	
	//		while (itr.hasNext()) {
			
			for(; k<payEmpHead.size(); k++){
			
				PdfPCell cellE = new PdfPCell(new Phrase(payEmpHead.get(k),FontFactory.getFont("Verdana", 7, Font.BOLD)));
	//			System.out.println(k+"----payEmpHead.get(k)====>"+payEmpHead.get(k)+"---empDetails.get(k)====>"+empDetails.get(k));
				cellE.setBorderWidth(0);
				empTable.addCell(cellE);
	
				if(payEmpHead.get(k).isEmpty()){
					PdfPCell cellcollan = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 7, Font.BOLD)));
					cellcollan.setBorderWidth(0);
					empTable.addCell(cellcollan);
				}else{
					PdfPCell cellcollan = new PdfPCell(new Phrase(":",FontFactory.getFont("Verdana", 7, Font.BOLD)));
					cellcollan.setBorderWidth(0);
					empTable.addCell(cellcollan);
					
				}
				PdfPCell cellF = new PdfPCell(new Phrase(empDetails.get(k),FontFactory.getFont("Verdana", 7, Font.NORMAL)));
	
				cellF.setBorderWidth(0);
				empTable.addCell(cellF);
	//			k++;
			}
	
			PdfPTable imageEmpDetailTable = new PdfPTable(2);
	
			int[] arrheaderwidths4 = { 12, 88 }; // percentage
	
			imageEmpDetailTable.getDefaultCell().setBorderWidth(1);
			imageEmpDetailTable.getDefaultCell().setPadding(1);
			imageEmpDetailTable.setWidths(arrheaderwidths4);
	
			imageEmpDetailTable.addCell(photoImagetable);
			PdfPTable netGrossTital = new PdfPTable(2);
			int[] arrheaderwidths6 = { 50, 50 }; // percentage
			netGrossTital.getDefaultCell().setBorderWidth(1);
			netGrossTital.getDefaultCell().setPadding(1);
			netGrossTital.setWidths(arrheaderwidths6);
	
			PdfPCell cellQ = new PdfPCell(new Phrase("GROSS EARNINGS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
	
			cellQ.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellQ.setBorderWidth(1);
			netGrossTital.addCell(cellQ);
	
			PdfPCell cellQ1 = new PdfPCell(new Phrase("NET EARNINGS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellQ1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellQ1.setBorderWidth(1);
			netGrossTital.addCell(cellQ1);
	
			PdfPTable grossTitalTable = new PdfPTable(2);
	
			int[] arrheaderwidths7 = { 60, 40 }; // percentage
			grossTitalTable.getDefaultCell().setBorderWidth(1);
			grossTitalTable.getDefaultCell().setPadding(1);
			grossTitalTable.setWidths(arrheaderwidths7);
	
			PdfPTable GrossHeadTable = new PdfPTable(1);
			int[] arrheaderwidths7A = { 100 }; // percentage
			GrossHeadTable.getDefaultCell().setBorderWidth(1);
			GrossHeadTable.getDefaultCell().setPadding(1);
			GrossHeadTable.setWidths(arrheaderwidths7A);
	
			PdfPTable grossAmtTable = new PdfPTable(1);
			int[] arrheaderwidths7B = { 100 }; // percentage
			grossAmtTable.getDefaultCell().setBorderWidth(1);
			grossAmtTable.getDefaultCell().setPadding(1);
			grossAmtTable.setWidths(arrheaderwidths7B);
	
			int nCount = 0;
			Iterator<String> itr1 = salaryHeadName.iterator();
			while (itr1.hasNext()) {
	
				String strHeadNM = itr1.next();
	
				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
	
					cellQ2.setBorderWidth(0);
					GrossHeadTable.addCell(cellQ2);
	
	//				PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount))+" "+CF.getStrCURRENCY_FULL(), FontFactory.getFont("Verdana", 8, Font.BOLD)));
					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.BOLD)));
	
					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
	
					cellQ3.setBorderWidthTop(1);
					cellQ3.setBorderWidthBottom(0);
					cellQ3.setBorderWidthLeft(0);
					cellQ3.setBorderWidthRight(0);
					grossAmtTable.addCell(cellQ3);
	
				} else {
					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
	
					cellQ2.setBorderWidth(0);
					GrossHeadTable.addCell(cellQ2);
	
					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
	
	//				PdfPCell cellQ3 = new PdfPCell(new Phrase("--G--"
	//						+ uF.formatIntoTwoDecimal(uF.parseToDouble(alGross
	//								.get(nCount))), FontFactory.getFont(
	//						"Verdana", 8, Font.NORMAL)));
					
					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
	
					cellQ3.setBorderWidth(0);
	
					grossAmtTable.addCell(cellQ3);
				}
				nCount++;
	
			}
	
			grossTitalTable.addCell(GrossHeadTable);
			grossTitalTable.addCell(grossAmtTable);
	
			PdfPTable netGrossTable = new PdfPTable(2);
			int[] arrheaderwidths6A = { 50, 50 }; // percentage
			netGrossTable.getDefaultCell().setBorderWidth(1);
			netGrossTable.getDefaultCell().setPadding(1);
			netGrossTable.setWidths(arrheaderwidths6A);
	
			
			
			//  Removed for Wai -- -Vipin 28/02/2013
	//		PdfPCell cellQA1 = new PdfPCell(new Phrase("LEAVES TAKEN ",
	//				FontFactory.getFont("Verdana", 9, Font.BOLD)));
			PdfPCell cellQA1 = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
	
			cellQA1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellQA1.setBorderWidth(1);
			netGrossTable.addCell(cellQA1);
	
			
			
			
			PdfPCell cellQA2 = new PdfPCell(new Phrase(" DEDUCTIONS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
	
			cellQA2.setBorderWidth(1);
			cellQA2.setHorizontalAlignment(Element.ALIGN_CENTER);
			netGrossTable.addCell(cellQA2);
	
			PdfPTable leaveTable = new PdfPTable(2);
			int[] arrheaderwidths8 = { 60, 40 }; // percentage
			leaveTable.getDefaultCell().setBorderWidth(1);
			leaveTable.getDefaultCell().setPadding(1);
			leaveTable.setWidths(arrheaderwidths8);
	
			PdfPTable leaveHeadTable = new PdfPTable(1);
			int[] arrheaderwidths8A = { 100 }; // percentage
			leaveHeadTable.getDefaultCell().setBorderWidth(1);
			leaveHeadTable.getDefaultCell().setPadding(1);
			leaveHeadTable.setWidths(arrheaderwidths8A);
	
			PdfPTable leaveAmtTable = new PdfPTable(1);
			int[] arrheaderwidths8B = { 100 }; // percentage
			leaveAmtTable.getDefaultCell().setBorderWidth(1);
			leaveAmtTable.getDefaultCell().setPadding(1);
			leaveAmtTable.setWidths(arrheaderwidths8B);
	
			int nCountGross = 0;
			Iterator<String> itr12 = salaryHeadName.iterator();
			while (itr12.hasNext()) {
	
				String strHeadNM = itr12.next();
	
				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
	
					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
					leaveHeadTable.addCell(cellQ2);
	//				PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmount.get(nCountGross))+" "+CF.getStrCURRENCY_FULL(), FontFactory.getFont("Verdana", 8, Font.BOLD)));
					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidthTop(1);
					cellQ3.setBorderWidthBottom(0);
					cellQ3.setBorderWidthLeft(0);
					cellQ3.setBorderWidthRight(0);
					leaveAmtTable.addCell(cellQ3);
				} else {
					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM, FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					leaveHeadTable.addCell(cellQ2);
					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidth(0);
					leaveAmtTable.addCell(cellQ3);
				}
	
				nCountGross++;
	
			}
	
			leaveTable.addCell(leaveHeadTable);
			leaveTable.addCell(leaveAmtTable);
	
			PdfPTable Deductiontable = new PdfPTable(2);
			int[] arrheaderwidths9 = { 50, 50 }; // percentage
			Deductiontable.getDefaultCell().setBorderWidth(1);
			Deductiontable.getDefaultCell().setPadding(1);
			Deductiontable.setWidths(arrheaderwidths9);
	
			PdfPCell cellQ3 = new PdfPCell(grossTitalTable);
			cellQ3.setBorderWidth(1);
			Deductiontable.addCell(cellQ3);
	
			PdfPCell cellR = new PdfPCell(leaveTable);
			cellR.setBorderWidth(1);
			Deductiontable.addCell(cellR);
	
			PdfPTable leaveDtable = new PdfPTable(2);
			int[] arrheaderwidths10 = { 60, 40 }; // percentage
			leaveDtable.getDefaultCell().setBorderWidth(0);
			leaveDtable.getDefaultCell().setPadding(1);
			leaveDtable.setWidths(arrheaderwidths10);
	
			PdfPTable leaveDHead = new PdfPTable(1);
			int[] arrheaderwidths10A = { 100 }; // percentage
			leaveDHead.getDefaultCell().setBorderWidth(1);
			leaveDHead.getDefaultCell().setPadding(1);
			leaveDHead.setWidths(arrheaderwidths10A);
	
			PdfPTable leaveDAmt = new PdfPTable(1);
			int[] arrheaderwidths10B = { 100 }; // percentage
			leaveDAmt.getDefaultCell().setBorderWidth(1);
			leaveDAmt.getDefaultCell().setPadding(1);
			leaveDAmt.setWidths(arrheaderwidths10B);
	
			int ncount = 0;
			Iterator<String> itr121 = leaveName.iterator();
			while (itr121.hasNext()) {
	
				String strleave = itr121.next();
	
				if (strleave!=null && strleave.equalsIgnoreCase("Total")) {
	
					PdfPCell cellQ2 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
	
					if (leaveName.size() < deductionHeadName.size()) {
						cellQ2.setBorderWidthBottom(1);
					}
					leaveDHead.addCell(cellQ2);
				}
	
				else {
	
					PdfPCell cellQ2 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					leaveDHead.addCell(cellQ2);
	
				}
	
				if (strleave!=null && strleave.equalsIgnoreCase("Total")) {
					PdfPCell cellQA3 = new PdfPCell(new Phrase(""+noOfLeave.get(ncount),FontFactory.getFont("Verdana", 8, Font.BOLD)));
	
					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
	
					cellQA3.setBorderWidthTop(1);
	
					cellQA3.setBorderWidthLeft(1);
					cellQA3.setBorderWidthRight(0);
					if (noOfLeave.size() < deductionHeadName.size()) {
						cellQA3.setBorderWidthBottom(1);
					}
					leaveDAmt.addCell(cellQA3);
	
				} else {
	
					PdfPCell cellQA3 = new PdfPCell(new Phrase(""+ noOfLeave.get(ncount),FontFactory.getFont("Verdana", 8, Font.NORMAL)));
	
					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQA3.setBorderWidth(0);
					cellQA3.setBorderWidthLeft(1);
					leaveDAmt.addCell(cellQA3);
	
				}
	
				ncount++;
	
			}
	
			PdfPTable DeductionHeadTable = new PdfPTable(2);
			int[] arrheaderwidths11 = { 60, 40 }; // percentage
			DeductionHeadTable.getDefaultCell().setBorderWidth(0);
			DeductionHeadTable.getDefaultCell().setPadding(1);
			DeductionHeadTable.setWidths(arrheaderwidths11);
	
			PdfPTable DeductionName = new PdfPTable(1);
			int[] arrheaderwidths11A = { 100 }; // percentage
			DeductionName.getDefaultCell().setBorderWidth(0);
			DeductionName.getDefaultCell().setPadding(1);
			DeductionName.setWidths(arrheaderwidths11A);
	
			PdfPTable deductionAmountTable = new PdfPTable(1);
			int[] arrheaderwidths11B = { 100 }; // percentage
			deductionAmountTable.getDefaultCell().setBorderWidth(0);
			deductionAmountTable.getDefaultCell().setPadding(1);
			deductionAmountTable.setWidths(arrheaderwidths11B);
	
			int nCountDeductionAmt = 0;
			Iterator<String> itr122 = deductionHeadName.iterator();
			while (itr122.hasNext()) {
	
				String strDeductionNm = itr122.next();
	
				if (strDeductionNm!=null && strDeductionNm.equalsIgnoreCase("Total Deduction")) {
	
					PdfPCell cellQ2 = new PdfPCell(new Phrase(strDeductionNm,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
					if (deductionHeadName.size() < leaveName.size()) {
						cellQ2.setBorderWidthBottom(1);
					}
	
					DeductionName.addCell(cellQ2);
	
	//				PdfPCell cellQA3 = new PdfPCell(new Phrase(""
	//						+ uF.formatIntoTwoDecimal(deductionHeadAmount
	//								.get(nCountDeductionAmt))+" "+CF.getStrCURRENCY_FULL(),
	//						FontFactory.getFont("Verdana", 8, Font.BOLD)));
					PdfPCell cellQA3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountDeductionAmt)),FontFactory.getFont("Verdana", 8, Font.BOLD)));
	
					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQA3.setBorderWidthTop(1);
	
					if (deductionHeadName.size() < leaveName.size()) {
						cellQA3.setBorderWidthBottom(1);
					}
	
					cellQA3.setBorderWidthLeft(1);
					cellQA3.setBorderWidthRight(0);
					deductionAmountTable.addCell(cellQA3);
				} else {
	
					PdfPCell cellQ2 = new PdfPCell(new Phrase(strDeductionNm,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					DeductionName.addCell(cellQ2);
	
					PdfPCell cellQA3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountDeductionAmt)),FontFactory.getFont("Verdana", 8, Font.NORMAL)));
	
					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
	
					cellQA3.setBorderWidth(0);
					if (deductionHeadName.size() < leaveName.size()) {
						cellQA3.setBorderWidthBottom(1);
					}
	
					cellQA3.setBorderWidthLeft(1);
					deductionAmountTable.addCell(cellQA3);
				}
	
				nCountDeductionAmt++;
			}
	
			PdfPCell cellQA3 = new PdfPCell(new Phrase("", FontFactory.getFont("Verdana", 12, Font.NORMAL)));
			cellQA3.setBorderWidth(0);
			cellQA3.setBorderWidthTop(0);
	
			if (leaveName.size() != deductionHeadName.size())
	
			{
	
				if (leaveName.size() > deductionHeadName.size()) {
	
					for (int i = 0; i < (leaveName.size() - deductionHeadName.size()); i++) {
						DeductionName.addCell(cellQA3);
						deductionAmountTable.addCell(cellQA3);
					}
	
				} else {
					for (int i = 0; i < (deductionHeadName.size() - leaveName.size()); i++) {
						leaveDHead.addCell(cellQA3);
						leaveDAmt.addCell(cellQA3);
					}
	
				}
	
			}
	
			/**=================   START   ============================
			 *  Balance Leave Details
			 */
			
			PdfPCell cellQA4 = null;
			cellQA4 = new PdfPCell(new Phrase("Balance Leaves", FontFactory.getFont("Verdana", 8, Font.BOLDITALIC)));
			cellQA4.setBorderWidth(0);
			cellQA4.setBorderWidthTop(0);
			leaveDHead.addCell(cellQA4);
			cellQA4 = new PdfPCell(new Phrase("  ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellQA4.setBorderWidth(0);
			cellQA4.setBorderWidthTop(0);
			leaveDAmt.addCell(cellQA4);
			
			
			Set set = hmBalanceLeave.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()){
				String strLeaveId = (String)it.next();
				
				
				cellQA4 = new PdfPCell(new Phrase(uF.showData((String)hmLeaveNameMap.get(strLeaveId), ""), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQA4.setBorderWidth(0);
				cellQA4.setBorderWidthTop(0);
				leaveDHead.addCell(cellQA4);
				
				cellQA4 = new PdfPCell(new Phrase(uF.showData((String)hmBalanceLeave.get(strLeaveId), ""), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQA4.setBorderWidth(0); uF.formatIntoTwoDecimal(dblNetSalary);
				cellQA4.setBorderWidthTop(0);
				leaveDAmt.addCell(cellQA4);
	
				
			}
			
			/**=================   END   ============================
			 * 
			 */
			
			//  Removed for Wai -- -Vipin 28/02/2013
	//		leaveDtable.addCell(leaveDHead);
	//		leaveDtable.addCell(leaveDAmt);
	
			DeductionHeadTable.addCell(DeductionName);
			DeductionHeadTable.addCell(deductionAmountTable);
	
			PdfPTable HeadleaveDeductionTable = new PdfPTable(2);
			int[] arrheaderwidths12 = { 50, 50 }; // percentage
			HeadleaveDeductionTable.getDefaultCell().setBorderWidth(1);
			HeadleaveDeductionTable.getDefaultCell().setPadding(1);
			HeadleaveDeductionTable.setWidths(arrheaderwidths12);
	
			PdfPCell cellU = new PdfPCell(leaveDtable);
			cellU.setBorderWidth(1);
			HeadleaveDeductionTable.addCell(cellU);
	
			PdfPCell cellV = new PdfPCell(DeductionHeadTable);
			cellV.setBorderWidth(1);
			HeadleaveDeductionTable.addCell(cellV);
	
			PdfPCell celld = new PdfPCell(new PdfPCell(empTable));
	
			PdfPTable NetSalaryTable = new PdfPTable(3);
			int[] arrheaderwidths13 = { 15,20,65}; // percentage
			NetSalaryTable.getDefaultCell().setBorderWidth(1);
			NetSalaryTable.getDefaultCell().setPadding(1);
			NetSalaryTable.setWidths(arrheaderwidths13);
	
			PdfPCell cellUv = new PdfPCell(new Phrase(" Net Salary :",FontFactory.getFont("Verdana", 9, Font.BOLD)));
	
			cellUv.setBorderWidth(1);
			NetSalaryTable.addCell(cellUv);
	
	//		PdfPCell cellVu = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(dblNetSalary)+" "+CF.getStrCURRENCY_FULL(),
	//				FontFactory.getFont("Verdana", 9, Font.BOLD)));
	//		 		cellVu.setBorderWidth(1);
	//		        NetSalaryTable.addCell(cellVu);
			
			PdfPCell cellVu = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(dblNetSalary),FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellVu.setBorderWidth(1);
			NetSalaryTable.addCell(cellVu);
			
			
			String strAmountInWord=null;
			
			String	StrnetAmount=uF.formatIntoTwoDecimal(dblNetSalary);
			
			double dblAmount=uF.parseToDouble(StrnetAmount);
			
			int nNetamount=(int)dblAmount;
			
			strAmountInWord=constNumToLetter(nNetamount);
			
			strAmountInWord=strAmountInWord.concat(" Rupees");
			double npreci=(dblAmount-nNetamount);
			npreci=npreci*100;
	
			int  nprecision=(int)npreci;
	
			if(nprecision>=0) {
				strAmountInWord=strAmountInWord.concat(" And");
				strAmountInWord=strAmountInWord.concat(constNumToLetter(nprecision));
				strAmountInWord=strAmountInWord.concat(" Paise");
			
			}
			
			
			PdfPCell cellwordAmt = new PdfPCell(new Phrase(strAmountInWord,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellwordAmt.setBorderWidth(1);
			cellwordAmt.setHorizontalAlignment(Element.ALIGN_RIGHT);
			NetSalaryTable.addCell(cellwordAmt);
				
			PdfPTable signatureTable = new PdfPTable(1);
			int[] arrheaderwidths14 = { 100 }; // percentage
			signatureTable.getDefaultCell().setBorderWidth(1);
			signatureTable.getDefaultCell().setPadding(1);
	
			signatureTable.setWidths(arrheaderwidths14);
			
			
			int	nyeardevfirst=nYear;
			nyeardevfirst=nYear%10;
			nYear=nYear/10;
			int	nyeardevsecond=nYear%10;
			PdfPCell cellUvU = new PdfPCell(new Phrase("This is computer generated PaySlip and does not require signature.",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellUvU.setBorderWidth(1);
			signatureTable.addCell(cellUvU);
	
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));
	
			imageEmpDetailTable.addCell(celld);
			document.add(LogoImage);
			document.add(titalTable);
			document.add(imageEmpDetailTable);
			document.add(netGrossTital);
			document.add(Deductiontable);
			document.add(netGrossTable);
			document.add(HeadleaveDeductionTable);
			document.add(NetSalaryTable);
			document.add(signatureTable);
	
		//	Image imagexy = Image.getInstance(filePath1);
			
			/*Image imageProductLogo=Image.getInstance(filePathproductLogo);
	
			imageProductLogo.setAbsolutePosition(445, 0);
			imageProductLogo.scaleToFit(150, 150);
			document.add(imageProductLogo);*/
			
			document.close();
				
			if(isAttachment()){ 
				/*byte[] bytes = baos.toByteArray();
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_NEW_PAYSLIP_GENERATED, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(strEmpId);
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName("PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
				nF.setEmailTemplate(true);
				nF.sendNotifications();*/
			}else{
				response.setContentType("application/pdf");
				response.setContentLength(baos.size());
				response.setHeader("Content-Disposition","attachment; filename=PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
				ServletOutputStream out = response.getOutputStream();              
				baos.writeTo(out);
				out.flush();
				out.close();
				baos.close();
				out.close();
				return;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}

private void createSalarySlipPdfThirdFormatByGrade(String strEmpId2, String strMonth2, Map<String, String> hmOrganisationDetails) {
	UtilityFunctions uF = new UtilityFunctions();
	try
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
		String strCompanyLogo=hmOrganisationDetails.get("ORG_LOGO");
		String strCompanyName=hmOrganisationDetails.get("ORG_NAME");
		String orgId =hmOrganisationDetails.get("ORG_ID");
		Map<String,String> hmFeatureStatus = (Map<String,String>)request.getAttribute("hmFeatureStatus");
		
		
		Document document = new Document();
		PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
		if(hmFeatureStatus != null &&uF.parseToBoolean(hmFeatureStatus.get(F_PASSWORD_PROTECTED_SALARY_SLIP))){
			pdfWriter.setEncryption(strPassword.getBytes(), strPassword.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
		}
		document.open();
		document.add(new Paragraph(" "));

		String filePath = null;
		String filePathCompanyLOgo = null;
		String filePathCompanyLOgodefault=null;
		String filePathproductLogo= null;
		
		if(CF.getStrDocSaveLocation()!=null){
			filePath = CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+ strEmpId+"/"+strEmpImage;
			
			filePathCompanyLOgo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+strCompanyLogo;
			filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
			filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
		}else{
			filePath = request.getRealPath("/userImages/" +strEmpImage+ "");
			filePathCompanyLOgo = request.getRealPath("/userImages/"+strCompanyLogo+"");
			filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
			filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
		}  
		Image imageLogo=null;
		
		try{
//			System.out.println("filePathCompanyLOgo========>"+filePathCompanyLOgo);
			
			FileInputStream fileInputStream1=null;
	        File file1 = new File(filePathCompanyLOgo);
	        byte[] bFile1 = new byte[(int) file1.length()];
	        fileInputStream1 = new FileInputStream(file1);
		    fileInputStream1.read(bFile1);
		    fileInputStream1.close();
	        imageLogo = Image.getInstance(bFile1);
	        
		}catch(FileNotFoundException e){
			imageLogo = Image.getInstance(filePathCompanyLOgodefault);
		}
		
		PdfPTable photoImagetable = new PdfPTable(1);
		photoImagetable.setWidthPercentage(10);
		photoImagetable.getDefaultCell().setPadding(1);


		PdfPTable companyNameTable = new PdfPTable(1);
		int[] arrheaderwidthsh = { 100 }; // percentage
		companyNameTable.getDefaultCell().setBorderWidth(0);
		companyNameTable.setWidths(arrheaderwidthsh);
		PdfPCell companyNamecell = new PdfPCell(new Phrase(strCompanyName, FontFactory.getFont("Verdana", 14,Font.BOLD)));
		

		companyNamecell.setBorderWidthTop(0);
		companyNamecell.setBorderWidthBottom(1);
		companyNamecell.setBorderWidthLeft(0);
		companyNamecell.setBorderWidthRight(0);

		companyNamecell.setHorizontalAlignment(Element.ALIGN_CENTER);
		companyNameTable.addCell(companyNamecell);
		PdfPCell companyNamecell1 = new PdfPCell(new Phrase(strCompanyAddress, FontFactory.getFont("Verdana", 10,Font.BOLD)));

		companyNamecell1.setBorderWidthTop(0);
		companyNamecell1.setBorderWidthBottom(0);
		companyNamecell1.setBorderWidthLeft(0);
		companyNamecell1.setBorderWidthRight(0);

		companyNamecell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		companyNameTable.addCell(companyNamecell1);

		PdfPTable LogoImage = new PdfPTable(2);
		int[] arrheaderwidths1 = { 70, 30 }; // percentage
		LogoImage.getDefaultCell().setBorderWidth(1);
		LogoImage.setWidths(arrheaderwidths1);
		LogoImage.setTotalWidth(600); 
		LogoImage.addCell(companyNameTable);
		LogoImage.addCell(imageLogo);

		PdfPTable titalTable = new PdfPTable(1);
		int[] arrheaderwidths2 = { 100 }; // percentage
		titalTable.getDefaultCell().setBorderWidth(1);
		titalTable.getDefaultCell().setPadding(1);
		titalTable.setWidths(arrheaderwidths2);

		PdfPCell cellb = new PdfPCell(new Phrase(" PAY SLIP ",FontFactory.getFont("Verdana", 15, Font.BOLD)));

		cellb.getExtraParagraphSpace();
		cellb.setBorderWidth(1);
		cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
		titalTable.addCell(cellb);

		PdfPTable empTable = new PdfPTable(6);

		int[] arrheaderwidths5 = { 31, 2, 30, 15, 2, 20 }; // percentage
		empTable.getDefaultCell().setBorderWidth(1);

		empTable.setWidths(arrheaderwidths5);
		empTable.setTotalWidth(800);
		
		Iterator<String> itr = payEmpHead.iterator();
		int k = 0;

		
		for(; k<payEmpHead.size(); k++){
		
			PdfPCell cellE = new PdfPCell(new Phrase(payEmpHead.get(k),FontFactory.getFont("Verdana", 7, Font.BOLD)));
			
			cellE.setBorderWidth(0);
			empTable.addCell(cellE);

			if(payEmpHead.get(k).isEmpty()){
				PdfPCell cellcollan = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 7, Font.BOLD)));
				cellcollan.setBorderWidth(0);
				empTable.addCell(cellcollan);
			}else{
				PdfPCell cellcollan = new PdfPCell(new Phrase(":",FontFactory.getFont("Verdana", 7, Font.BOLD)));
				cellcollan.setBorderWidth(0);
				empTable.addCell(cellcollan);
			}
			PdfPCell cellF = new PdfPCell(new Phrase(empDetails.get(k),FontFactory.getFont("Verdana", 7, Font.NORMAL)));
			cellF.setBorderWidth(0);
			empTable.addCell(cellF);
		}

		PdfPTable imageEmpDetailTable = new PdfPTable(1);

		int[] arrheaderwidths4 = { 100 }; // percentage

		imageEmpDetailTable.getDefaultCell().setBorderWidth(1);
		imageEmpDetailTable.getDefaultCell().setPadding(1);
		imageEmpDetailTable.setWidths(arrheaderwidths4);

		PdfPTable netGrossTital = new PdfPTable(2);
		int[] arrheaderwidths6 = { 50, 50 }; // percentage
		netGrossTital.getDefaultCell().setBorderWidth(1);
		netGrossTital.getDefaultCell().setPadding(1);
		netGrossTital.setWidths(arrheaderwidths6);

		PdfPCell cellQ = new PdfPCell(new Phrase("GROSS EARNINGS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

		cellQ.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellQ.setBorderWidth(1);
		netGrossTital.addCell(cellQ);

		PdfPCell cellQ1 = new PdfPCell(new Phrase("NET EARNINGS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
		cellQ1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellQ1.setBorderWidth(1);
		netGrossTital.addCell(cellQ1);

		PdfPTable grossTitalTable = new PdfPTable(2);

		int[] arrheaderwidths7 = { 60, 40 }; // percentage
		grossTitalTable.getDefaultCell().setBorderWidth(1);
		grossTitalTable.getDefaultCell().setPadding(1);
		grossTitalTable.setWidths(arrheaderwidths7);

		PdfPTable GrossHeadTable = new PdfPTable(1);
		int[] arrheaderwidths7A = { 100 }; // percentage
		GrossHeadTable.getDefaultCell().setBorderWidth(1);
		GrossHeadTable.getDefaultCell().setPadding(1);
		GrossHeadTable.setWidths(arrheaderwidths7A);

		PdfPTable grossAmtTable = new PdfPTable(1);
		int[] arrheaderwidths7B = { 100 }; // percentage
		grossAmtTable.getDefaultCell().setBorderWidth(1);
		grossAmtTable.getDefaultCell().setPadding(1);
		grossAmtTable.setWidths(arrheaderwidths7B);

		int nCount = 0;
		Iterator<String> itr1 = salaryHeadName.iterator();
		while (itr1.hasNext()) {

			String strHeadNM = itr1.next();

			if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
				PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));

				cellQ2.setBorderWidth(0);
				GrossHeadTable.addCell(cellQ2);

				PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

				cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellQ3.setBorderWidthTop(1);
				cellQ3.setBorderWidthBottom(0);
				cellQ3.setBorderWidthLeft(0);
				cellQ3.setBorderWidthRight(0);
				grossAmtTable.addCell(cellQ3);

			} else {

				PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));

				cellQ2.setBorderWidth(0);
				GrossHeadTable.addCell(cellQ2);

				PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellQ3.setBorderWidth(0);
				grossAmtTable.addCell(cellQ3);
			}

			nCount++;

		}

		grossTitalTable.addCell(GrossHeadTable);
		grossTitalTable.addCell(grossAmtTable);

		PdfPTable netGrossTable = new PdfPTable(2);
		int[] arrheaderwidths6A = { 50, 50 }; // percentage
		netGrossTable.getDefaultCell().setBorderWidth(1);
		netGrossTable.getDefaultCell().setPadding(1);
		netGrossTable.setWidths(arrheaderwidths6A);

		PdfPCell cellQA1 = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

		cellQA1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellQA1.setBorderWidth(1);
		netGrossTable.addCell(cellQA1);

		
		PdfPCell cellQA2 = new PdfPCell(new Phrase(" DEDUCTIONS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

		cellQA2.setBorderWidth(1);
		cellQA2.setHorizontalAlignment(Element.ALIGN_CENTER);
		netGrossTable.addCell(cellQA2);

		PdfPTable leaveTable = new PdfPTable(2);
		int[] arrheaderwidths8 = { 60, 40 }; // percentage
		leaveTable.getDefaultCell().setBorderWidth(1);
		leaveTable.getDefaultCell().setPadding(1);
		leaveTable.setWidths(arrheaderwidths8);

		PdfPTable leaveHeadTable = new PdfPTable(1);
		int[] arrheaderwidths8A = { 100 }; // percentage
		leaveHeadTable.getDefaultCell().setBorderWidth(1);
		leaveHeadTable.getDefaultCell().setPadding(1);
		leaveHeadTable.setWidths(arrheaderwidths8A);

		PdfPTable leaveAmtTable = new PdfPTable(1);
		int[] arrheaderwidths8B = { 100 }; // percentage
		leaveAmtTable.getDefaultCell().setBorderWidth(1);
		leaveAmtTable.getDefaultCell().setPadding(1);
		leaveAmtTable.setWidths(arrheaderwidths8B);

		int nCountGross = 0;
		Iterator<String> itr12 = salaryHeadName.iterator();
		while (itr12.hasNext()) {

			String strHeadNM = itr12.next();

			if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {

				PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
				cellQ2.setBorderWidth(0);
				leaveHeadTable.addCell(cellQ2);

				PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

				cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

				cellQ3.setBorderWidthTop(1);
				cellQ3.setBorderWidthBottom(0);
				cellQ3.setBorderWidthLeft(0);
				cellQ3.setBorderWidthRight(0);
				leaveAmtTable.addCell(cellQ3);
			} else {

				PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM, FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQ2.setBorderWidth(0);
				leaveHeadTable.addCell(cellQ2);
				PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));

				cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

				cellQ3.setBorderWidth(0);
				leaveAmtTable.addCell(cellQ3);

			}

			nCountGross++;

		}

		leaveTable.addCell(leaveHeadTable);
		leaveTable.addCell(leaveAmtTable);

		PdfPTable Deductiontable = new PdfPTable(2);
		int[] arrheaderwidths9 = { 50, 50 }; // percentage
		Deductiontable.getDefaultCell().setBorderWidth(1);
		Deductiontable.getDefaultCell().setPadding(1);
		Deductiontable.setWidths(arrheaderwidths9);

		PdfPCell cellQ3 = new PdfPCell(grossTitalTable);
		cellQ3.setBorderWidth(1);
		Deductiontable.addCell(cellQ3);

		PdfPCell cellR = new PdfPCell(leaveTable);
		cellR.setBorderWidth(1);
		Deductiontable.addCell(cellR);

		PdfPTable leaveDtable = new PdfPTable(2);
		int[] arrheaderwidths10 = { 60, 40 }; // percentage
		leaveDtable.getDefaultCell().setBorderWidth(0);
		leaveDtable.getDefaultCell().setPadding(1);
		leaveDtable.setWidths(arrheaderwidths10);

		PdfPTable leaveDHead = new PdfPTable(1);
		int[] arrheaderwidths10A = { 100 }; // percentage
		leaveDHead.getDefaultCell().setBorderWidth(1);
		leaveDHead.getDefaultCell().setPadding(1);
		leaveDHead.setWidths(arrheaderwidths10A);

		PdfPTable leaveDAmt = new PdfPTable(1);
		int[] arrheaderwidths10B = { 100 }; // percentage
		leaveDAmt.getDefaultCell().setBorderWidth(1);
		leaveDAmt.getDefaultCell().setPadding(1);
		leaveDAmt.setWidths(arrheaderwidths10B);

		int ncount = 0;
		Iterator<String> itr121 = leaveName.iterator();
		while (itr121.hasNext()) {

			String strleave = itr121.next();

			if (strleave!=null && strleave.equalsIgnoreCase("Total")) {

				PdfPCell cellQ2 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.BOLD)));
				cellQ2.setBorderWidth(0);

				if (leaveName.size() < deductionHeadName.size()) {
					cellQ2.setBorderWidthBottom(1);
				}
				leaveDHead.addCell(cellQ2);
			}

			else {

				PdfPCell cellQ2 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQ2.setBorderWidth(0);
				leaveDHead.addCell(cellQ2);

			}

			if (strleave!=null && strleave.equalsIgnoreCase("Total")) {
				PdfPCell cellQA3 = new PdfPCell(new Phrase(""+noOfLeave.get(ncount),FontFactory.getFont("Verdana", 8, Font.BOLD)));

				cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);

				cellQA3.setBorderWidthTop(1);

				cellQA3.setBorderWidthLeft(1);
				cellQA3.setBorderWidthRight(0);
				if (noOfLeave.size() < deductionHeadName.size()) {
					cellQA3.setBorderWidthBottom(1);
				}
				leaveDAmt.addCell(cellQA3);

			} else {

				PdfPCell cellQA3 = new PdfPCell(new Phrase(""+ noOfLeave.get(ncount),FontFactory.getFont("Verdana", 8, Font.NORMAL)));

				cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellQA3.setBorderWidth(0);
				cellQA3.setBorderWidthLeft(1);
				leaveDAmt.addCell(cellQA3);

			}

			ncount++;

		}

		PdfPTable DeductionHeadTable = new PdfPTable(2);
		int[] arrheaderwidths11 = { 60, 40 }; // percentage
		DeductionHeadTable.getDefaultCell().setBorderWidth(0);
		DeductionHeadTable.getDefaultCell().setPadding(1);
		DeductionHeadTable.setWidths(arrheaderwidths11);

		PdfPTable DeductionName = new PdfPTable(1);
		int[] arrheaderwidths11A = { 100 }; // percentage
		DeductionName.getDefaultCell().setBorderWidth(0);
		DeductionName.getDefaultCell().setPadding(1);
		DeductionName.setWidths(arrheaderwidths11A);

		PdfPTable deductionAmountTable = new PdfPTable(1);
		int[] arrheaderwidths11B = { 100 }; // percentage
		deductionAmountTable.getDefaultCell().setBorderWidth(0);
		deductionAmountTable.getDefaultCell().setPadding(1);
		deductionAmountTable.setWidths(arrheaderwidths11B);

		int nCountDeductionAmt = 0;
		Iterator<String> itr122 = deductionHeadName.iterator();
		while (itr122.hasNext()) {

			String strDeductionNm = itr122.next();

			if (strDeductionNm!=null && strDeductionNm.equalsIgnoreCase("Total Deduction")) {

				PdfPCell cellQ2 = new PdfPCell(new Phrase(strDeductionNm,FontFactory.getFont("Verdana", 8, Font.BOLD)));
				cellQ2.setBorderWidth(0);
				if (deductionHeadName.size() < leaveName.size()) {
					cellQ2.setBorderWidthBottom(1);
				}

				DeductionName.addCell(cellQ2);
				
				PdfPCell cellQA3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountDeductionAmt)),FontFactory.getFont("Verdana", 8, Font.BOLD)));

				cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellQA3.setBorderWidthTop(1);

				if (deductionHeadName.size() < leaveName.size()) {
					cellQA3.setBorderWidthBottom(1);
				}

				cellQA3.setBorderWidthLeft(1);
				cellQA3.setBorderWidthRight(0);
				deductionAmountTable.addCell(cellQA3);
			} else {

				PdfPCell cellQ2 = new PdfPCell(new Phrase(strDeductionNm,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQ2.setBorderWidth(0);
				DeductionName.addCell(cellQ2);

				PdfPCell cellQA3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountDeductionAmt)),FontFactory.getFont("Verdana", 8, Font.NORMAL)));

				cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);

				cellQA3.setBorderWidth(0);
				if (deductionHeadName.size() < leaveName.size()) {
					cellQA3.setBorderWidthBottom(1);
				}

				cellQA3.setBorderWidthLeft(1);
				deductionAmountTable.addCell(cellQA3);
			}

			nCountDeductionAmt++;
		}

		PdfPCell cellQA3 = new PdfPCell(new Phrase("", FontFactory.getFont("Verdana", 12, Font.NORMAL)));
		cellQA3.setBorderWidth(0);
		cellQA3.setBorderWidthTop(0);

		if (leaveName.size() != deductionHeadName.size())

		{

			if (leaveName.size() > deductionHeadName.size()) {

				for (int i = 0; i < (leaveName.size() - deductionHeadName.size()); i++) {
					DeductionName.addCell(cellQA3);
					deductionAmountTable.addCell(cellQA3);
				}

			} else {
				for (int i = 0; i < (deductionHeadName.size() - leaveName.size()); i++) {
					leaveDHead.addCell(cellQA3);
					leaveDAmt.addCell(cellQA3);
				}

			}

		}

		/**=================   START   ============================
		 *  Balance Leave Details
		 */
		
		PdfPCell cellQA4 = null;
		cellQA4 = new PdfPCell(new Phrase("Balance Leaves", FontFactory.getFont("Verdana", 8, Font.BOLDITALIC)));
		cellQA4.setBorderWidth(0);
		cellQA4.setBorderWidthTop(0);
		leaveDHead.addCell(cellQA4);
		cellQA4 = new PdfPCell(new Phrase("  ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
		cellQA4.setBorderWidth(0);
		cellQA4.setBorderWidthTop(0);
		leaveDAmt.addCell(cellQA4);
		
		
		Set set = hmBalanceLeave.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			String strLeaveId = (String)it.next();
			
			
			cellQA4 = new PdfPCell(new Phrase(uF.showData((String)hmLeaveNameMap.get(strLeaveId), ""), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellQA4.setBorderWidth(0);
			cellQA4.setBorderWidthTop(0);
			leaveDHead.addCell(cellQA4);
			
			cellQA4 = new PdfPCell(new Phrase(uF.showData((String)hmBalanceLeave.get(strLeaveId), ""), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellQA4.setBorderWidth(0); uF.formatIntoTwoDecimal(dblNetSalary);
			cellQA4.setBorderWidthTop(0);
			leaveDAmt.addCell(cellQA4);

			
		}
		
		/**=================   END   ============================
		 * 
		 */

		DeductionHeadTable.addCell(DeductionName);
		DeductionHeadTable.addCell(deductionAmountTable);

		PdfPTable HeadleaveDeductionTable = new PdfPTable(2);
		int[] arrheaderwidths12 = { 50, 50 }; // percentage
		HeadleaveDeductionTable.getDefaultCell().setBorderWidth(1);
		HeadleaveDeductionTable.getDefaultCell().setPadding(1);
		HeadleaveDeductionTable.setWidths(arrheaderwidths12);

		PdfPCell cellU = new PdfPCell(leaveDtable);
		cellU.setBorderWidth(1);
		HeadleaveDeductionTable.addCell(cellU);

		PdfPCell cellV = new PdfPCell(DeductionHeadTable);
		cellV.setBorderWidth(1);
		HeadleaveDeductionTable.addCell(cellV);

		PdfPCell celld = new PdfPCell(new PdfPCell(empTable));

		PdfPTable NetSalaryTable = new PdfPTable(3);
		int[] arrheaderwidths13 = { 15,20,65}; // percentage
		NetSalaryTable.getDefaultCell().setBorderWidth(1);
		NetSalaryTable.getDefaultCell().setPadding(1);
		NetSalaryTable.setWidths(arrheaderwidths13);

		PdfPCell cellUv = new PdfPCell(new Phrase(" Net Salary :",FontFactory.getFont("Verdana", 9, Font.BOLD)));

		cellUv.setBorderWidth(1);
		NetSalaryTable.addCell(cellUv);
		
		PdfPCell cellVu = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(dblNetSalary),FontFactory.getFont("Verdana", 9, Font.BOLD)));
		cellVu.setBorderWidth(1);
		NetSalaryTable.addCell(cellVu);
		
		
		String strAmountInWord=null;
		
		String	StrnetAmount=uF.formatIntoTwoDecimal(dblNetSalary);
		
		double dblAmount=uF.parseToDouble(StrnetAmount);
		
		int nNetamount=(int)dblAmount;
		
		strAmountInWord=constNumToLetter(nNetamount);
		
		strAmountInWord=strAmountInWord.concat(" Rupees");
		double npreci=(dblAmount-nNetamount);
		npreci=npreci*100;

		int  nprecision=(int)npreci;

		if(nprecision>=0)
		{
			strAmountInWord=strAmountInWord.concat(" And");
			strAmountInWord=strAmountInWord.concat(constNumToLetter(nprecision));
			strAmountInWord=strAmountInWord.concat(" Paise");
		
		}
		
		
		PdfPCell cellwordAmt = new PdfPCell(new Phrase(strAmountInWord,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
		cellwordAmt.setBorderWidth(1);
		cellwordAmt.setHorizontalAlignment(Element.ALIGN_RIGHT);
		NetSalaryTable.addCell(cellwordAmt);
			
		PdfPTable signatureTable = new PdfPTable(1);
		int[] arrheaderwidths14 = { 100 }; // percentage
		signatureTable.getDefaultCell().setBorderWidth(1);
		signatureTable.getDefaultCell().setPadding(1);

		signatureTable.setWidths(arrheaderwidths14);
		
		
		int	nyeardevfirst=nYear;
		nyeardevfirst=nYear%10;
		nYear=nYear/10;
		int	nyeardevsecond=nYear%10;
		PdfPCell cellUvU = new PdfPCell(new Phrase("This is computer generated PaySlip and does not require signature.",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
		cellUvU.setBorderWidth(1);
		signatureTable.addCell(cellUvU);

		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));

		imageEmpDetailTable.addCell(celld);
		document.add(LogoImage);
		document.add(titalTable);
		document.add(imageEmpDetailTable);
		document.add(netGrossTital);
		document.add(Deductiontable);
		document.add(netGrossTable);
		document.add(HeadleaveDeductionTable);
		document.add(NetSalaryTable);
		document.add(signatureTable);
		
		document.close();
			
		
		if(isAttachment()) {
			/*byte[] bytes = baos.toByteArray();
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(N_NEW_PAYSLIP_GENERATED, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setStrEmpId(strEmpId);
			nF.setPdfData(bytes);
			nF.setStrAttachmentFileName("PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
			nF.setEmailTemplate(true);
			nF.sendNotifications();*/
		} else {
			response.setContentType("application/pdf");
			response.setContentLength(baos.size());
			response.setHeader("Content-Disposition","attachment; filename=PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
			ServletOutputStream out = response.getOutputStream();              
			baos.writeTo(out);
			out.flush();
			out.close();
			baos.close();
			out.close();
			return;
		}
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
		
}

private void createSalarySlipPdfSecondFormatByGrade(String strEmpId, String strMonth, Map<String, String> hmOrganisationDetails) {
	UtilityFunctions uF = new UtilityFunctions();
	try {
		List<Integer> alList=(List<Integer>)request.getAttribute("alList");
		Map<String,String> hmempcnt =(Map<String,String> )request.getAttribute("hmempcnt");
		Map<Integer,Map<String,String>> hmMap=(Map<Integer,Map<String,String>>)request.getAttribute("hmMap");
		if(hmMap==null)hmMap=new HashMap<Integer, Map<String,String>>();
		
		Map<String,String> hmEPFChallan=(Map<String,String>)request.getAttribute("hmEPFChallan");
		
		Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
		if(hmOrg == null) hmOrg = new HashMap<String, String>();
		
		Map<String,String> hmFeatureStatus = (Map<String,String>)request.getAttribute("hmFeatureStatus");
		
		com.itextpdf.text.Font heading = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 13);
		com.itextpdf.text.Font normal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11);
		com.itextpdf.text.Font normalwithbold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
		com.itextpdf.text.Font small = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7);
		com.itextpdf.text.Font smallBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7,Font.BOLD);
		com.itextpdf.text.Font italicEffect = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    com.itextpdf.text.Document document = new com.itextpdf.text.Document();
	    com.itextpdf.text.pdf.PdfWriter pdfWriter = com.itextpdf.text.pdf.PdfWriter.getInstance(document,buffer);
	    
	    if(hmFeatureStatus != null &&uF.parseToBoolean(hmFeatureStatus.get(F_PASSWORD_PROTECTED_SALARY_SLIP))){
			pdfWriter.setEncryption(strPassword.getBytes(), strPassword.getBytes(), com.itextpdf.text.pdf.PdfWriter.ALLOW_PRINTING, com.itextpdf.text.pdf.PdfWriter.ENCRYPTION_AES_128);
		}
	    
	    document.open();
	       
	    com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(10);
		table.setWidthPercentage(100);       
		int[] cols = {3,10,10,10,10,10,10,10,10,10};
		table.setWidths(cols);
       
		String strCompanyName=hmOrganisationDetails.get("ORG_NAME");
		String orgId =hmOrganisationDetails.get("ORG_ID");
		
		com.itextpdf.text.pdf.PdfPCell row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(strCompanyName,smallBold));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(10);
       row1.setPadding(2.5f);
       table.addCell(row1);
     
     //New Row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(strCompanyAddress,small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(10);
       table.addCell(row1);
       
     //New Row
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Payslip for the month of  : "+uF.showData(monthName, ""),smallBold));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM);
       row1.setColspan(10);
       table.addCell(row1);
     

       String heading2="EMPLOYEE DETAILS";
     //New Row
         row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(heading2,smallBold));
         row1.setHorizontalAlignment(Element.ALIGN_CENTER);
         row1.setBorder(Rectangle.NO_BORDER);
         row1.setColspan(10);
         row1.setPadding(2.5f);
         table.addCell(row1);
       
       //New Row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Emp Code : "+uF.showData(empDetails.get(2), ""),small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(5);	        
       table.addCell(row1);
     
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("PAN : "+uF.showData(empDetails.get(10), ""),small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(5);	
       table.addCell(row1);
       
     //New Row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Location : "+uF.showData(empDetails.get(7), ""),small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);	
       row1.setColspan(5);	
       table.addCell(row1);
     
     //New Row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Department Name : "+uF.showData(empDetails.get(12), ""),small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(5);	
       table.addCell(row1);
       
     //New Row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Name : "+uF.showData(empDetails.get(0), "") ,small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(5);	        
       table.addCell(row1);
     
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Designation : "+uF.showData(empDetails.get(1),""),small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(5);	
       table.addCell(row1);
     
     
     //New Row
       String heading3="PAYMENT DETAILS";
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(heading3,smallBold));
       row1.setHorizontalAlignment(Element.ALIGN_CENTER);
       row1.setBorder(Rectangle.TOP);
       row1.setColspan(10);
       row1.setPadding(2.5f);
       table.addCell(row1);
       
     //New Row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Bank Name : "+uF.showData(bankName, ""),small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(5);	        
       table.addCell(row1);
     
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Bank Account No. : "+uF.showData(empDetails.get(4),""),small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(5);	  
       table.addCell(row1);
       
     //New Row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Mode of Payment : "+uF.showData(strPaymentMode,""),small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(10);	        
       table.addCell(row1);
     
     
     //New Row
       String heading4="ATTENDENCE DETAILS";
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(heading4,smallBold));
       row1.setHorizontalAlignment(Element.ALIGN_CENTER);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(10);
       row1.setPadding(2.5f);
       table.addCell(row1);
     
     //New Row  
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total Days : "+tDays,small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(4);
       table.addCell(row1);
       
      
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Unpaid Days : "+unpaidDays,small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(3);
       table.addCell(row1);
      
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Paid Days : "+pDays,small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(3);
       table.addCell(row1);
       
       
      
     //New Row  
       String heading5="SALARY DETAILS";
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(heading5,smallBold));
       row1.setHorizontalAlignment(Element.ALIGN_CENTER);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(10);
       row1.setPadding(2.5f);
       table.addCell(row1);
       
       
     //New Row  
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Earnings",small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(3);	        
       table.addCell(row1);
       
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Amount",small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(2);	 
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Deductions",small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(3);	 
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Amount",small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(2);	 
       table.addCell(row1);
       
       
       com.itextpdf.text.pdf.PdfPTable table1 = new com.itextpdf.text.pdf.PdfPTable(5);
       table1.setWidthPercentage(100);   

       Double totalEarning = salHeadAmount.get(salHeadAmount.size() - 1);
       if(salaryHeadName.contains("Total")){
       	salaryHeadName.remove(salaryHeadName.size() - 1);
       	salHeadAmount.remove(salHeadAmount.size() - 1);
       }
       
		for(int i=0;i<salaryHeadName.size();i++) {
			
			com.itextpdf.text.pdf.PdfPCell rowinner1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(salaryHeadName.get(i),small));
			rowinner1.setPadding(2.5f);
			rowinner1.setHorizontalAlignment(Element.ALIGN_LEFT);
			rowinner1.setBorder(Rectangle.NO_BORDER);
			rowinner1.setColspan(3);
	        table1.addCell(rowinner1);
	        
//	        System.out.println("Salary Name :"+salaryHeadName.get(i));
	        
	        com.itextpdf.text.pdf.PdfPCell rowinner2 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(Double.toString(salHeadAmount.get(i)),small));
			rowinner2.setPadding(2.5f);
			rowinner2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			rowinner2.setBorder(Rectangle.NO_BORDER);
			rowinner2.setColspan(2);
	        table1.addCell(rowinner2);
	        
//	        System.out.println("Salary Ammount :"+salHeadAmount.get(i));
	      
		}
		//New Row  
       row1 =new com.itextpdf.text.pdf.PdfPCell(table1);
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(5);	        
       table.addCell(row1);
       
       
       com.itextpdf.text.pdf.PdfPTable table2 = new com.itextpdf.text.pdf.PdfPTable(5);
       table2.setWidthPercentage(100);       

       Double totalDeduction =  deductionHeadAmount.get(deductionHeadAmount.size() - 1);
       if(deductionHeadName.contains("Total Deduction")){
       deductionHeadName.remove(deductionHeadName.size() - 1);
       deductionHeadAmount.remove(deductionHeadAmount.size() - 1);
       }
       
       for(int i=0;i<deductionHeadName.size();i++) {
			
       	com.itextpdf.text.pdf.PdfPCell rowinner1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(deductionHeadName.get(i),small));
			rowinner1.setPadding(2.5f);
			rowinner1.setHorizontalAlignment(Element.ALIGN_LEFT);
			rowinner1.setBorder(Rectangle.NO_BORDER);
			rowinner1.setColspan(3);
	        table2.addCell(rowinner1);
	        
//	        System.out.println("Deduction Name : "+deductionHeadName.get(i));
	        
	        com.itextpdf.text.pdf.PdfPCell rowinner2 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(Double.toString(deductionHeadAmount.get(i)),small));
			rowinner2.setPadding(2.5f);
			rowinner2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			rowinner2.setBorder(Rectangle.NO_BORDER);
			rowinner2.setColspan(2);
	        table2.addCell(rowinner2);
	        
//	        System.out.println("Deduction Ammount : "+deductionHeadAmount.get(i));
	      
		}
   
       row1 =new com.itextpdf.text.pdf.PdfPCell(table2);
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(5);	        
       table.addCell(row1);
    
       //New Row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Gross Earning  : ",smallBold));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(3);	        
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "+totalEarning,smallBold));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(2);	        
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Gross Deduction : ",smallBold));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(3);	 
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "+totalDeduction,smallBold));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(2);	 
       table.addCell(row1);
      
       //New Row
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Net Amount :           "+uF.formatIntoTwoDecimal(dblNetSalary),smallBold));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(10);	 
       table.addCell(row1);
     
       
       int	nyeardevfirst=nYear;
		nyeardevfirst=nYear%10;
		nYear=nYear/10;
		int	nyeardevsecond=nYear%10;
       
       //New Row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Computer generated hence signature not required",small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(10);	 
       table.addCell(row1);
       
               
       document.add(table);
       
       document.close();
       
		String filename="PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf";
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename="+filename+"");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
	
}catch (Exception e) {
	e.printStackTrace();  
}
	
}
	
private void createSalarySlipPdfFifthFormatByGrade(String strEmpId, String strMonth) {
		UtilityFunctions uF=new UtilityFunctions();
		Database dB = new Database();
		dB.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		con = dB.makeConnection(con);
	
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		
		boolean flagEpf = false;
	//	UtilityFunctions uF = new UtilityFunctions();
		try {
			
			Map<String, String> hmTemp =null;
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpDepartmentMap = CF.getDepartmentMap(con,null, null);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
//			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con);
//			String strEmpGradeId = CF.getEmpGradeId(con, getStrEmpId());
			
			Map<String, String> hmOrganisationDetails = new HashMap<String, String>();
			
			pst =con.prepareStatement("select max(entry_date) as entry_date, paid_from, paid_to,paid_days,total_days ,payment_mode,month,year " +
					"from payroll_generation where emp_id = ? and financial_year_from_date=? AND financial_year_to_date=? AND month=? " +
					"and paycycle=? and (bank_pay_type=? or bank_pay_type is null) group by paid_from, paid_to,paid_days,total_days,payment_mode,month,year");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(getStrPC()));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			rs = pst.executeQuery();
			String strEntryDate = null;
			String strPayCycleStart = null;
			String strPayCycleEnd = null;
			String strTotal_days =null;
			String strPaid_days = null;
			String strPresent_days = null;
			while(rs.next()){
				strEntryDate = rs.getString("entry_date");
				strPayCycleStart = rs.getString("paid_from");
				strPayCycleEnd = rs.getString("paid_to");
			//===start parvez date: 04-03-2023 strTotal_days and strPaid_days commented===	
//				strTotal_days =rs.getString("total_days");
//				strPaid_days =rs.getString("paid_days");
			//===end parvez date: 04-03-2023===	
			//	strPresent_days = rs.getString("present_days");
				strPaymentMode =rs.getString("payment_mode");
				 pMonth =rs.getInt("month");
				 pYear =rs.getInt("year");
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 04-03-2023===
			pst =con.prepareStatement("select * from approve_attendance where emp_id = ? and financial_year_start=? AND financial_year_end=? and paycycle=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrPC()));
			rs = pst.executeQuery();
			while(rs.next()) {
				strTotal_days =rs.getString("total_days");
				strPaid_days =rs.getString("paid_days");
			}
			rs.close();
			pst.close();
		//===end parvez date: 04-03-2023===
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("SELECT MAX(effective_date) as effective_date, grade_id FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
//				"and isdisplay=true and effective_date<=? group by grade_id");
			sbQuery.append("SELECT MAX(effective_date) as effective_date, grade_id FROM emp_salary_details WHERE emp_id=? and is_approved=true and " +
					"isdisplay=true and effective_date = (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
					"and isdisplay=true and effective_date<=?) group by grade_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
//				System.out.println("pst ================>> " + pst);
			String strEffectiveDate = null;
			String strEmpGradeId = null;
			rs = pst.executeQuery();
			while(rs.next()){
				strEffectiveDate = rs.getString("effective_date");
				strEmpGradeId = rs.getString("grade_id");
			}
			rs.close();
			pst.close();
//			System.out.println("strEmpGradeId ===>> " + strEmpGradeId);
			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMapByGrade(con, uF.parseToInt(strEmpGradeId));			
			
			pst =con.prepareStatement("select erpf_contribution, erps_contribution,erdli_contribution,pf_admin_charges,edli_admin_charges from emp_epf_details where emp_id = ? and financial_year_start=? and financial_year_end=? and _month =? and paycycle=? ");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(getStrPC()));
			
//			System.out.println("************************pst**************"+pst);
			rs = pst.executeQuery();
			
			
			double erpf_contb = 0 ;
			double erps_contb = 0;
			double erdli_contib = 0;
			double pFadminChrges = 0;
			double edliAdminChrges = 0;
			double total = 0;
			while(rs.next()){
				
				erpf_contb =rs.getDouble("erpf_contribution");
				erps_contb =rs.getDouble("erps_contribution");
				erdli_contib =rs.getDouble("erdli_contribution");
				pFadminChrges =rs.getDouble("pf_admin_charges");
				edliAdminChrges =rs.getDouble("edli_admin_charges");
				
			}
			
			total =erpf_contb + erps_contb + erdli_contib+pFadminChrges+edliAdminChrges;
//			System.out.println("***********************total***********************"+total);
			
		    monthName = uF.getMonth(pMonth)+" "+pYear;
		
	//===start parvez date: 04-03-2023===	    
//           tDays = uF.parseToInt(strTotal_days);
//           pDays = uF.parseToInt(strPaid_days);
         tDays = uF.parseToDouble(strTotal_days);
         pDays = uF.parseToDouble(strPaid_days);
     //===end parvez date: 04-03-2023=== 
           
           unpaidDays = tDays - pDays;
           
	            
			if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("1")){
				strPaymentMode ="Bank Transfer";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cash";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cheque";	
			}
				
	            
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
		
			pst = con.prepareStatement("select * from org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
	
			while(rs.next()){
				hmOrganisationDetails.put("ORG_ID", rs.getString("org_id"));
				hmOrganisationDetails.put("ORG_LOGO", rs.getString("org_logo"));
				hmOrganisationDetails.put("ORG_NAME", rs.getString("org_name"));
				hmOrganisationDetails.put("PDF_TYPE", rs.getString("payslip_format"));
			}
				rs.close();
				pst.close();
			
			pst = con.prepareStatement("SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id  = eod.emp_id and emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map hmWorkLocationMap = CF.getWorkLocationMap(con);
				hmTemp = (Map<String, String>)hmWorkLocationMap.get(rs.getInt("wlocation_id")+"");
				if(hmTemp==null)hmTemp=new HashMap();
				strCompanyAddress = hmTemp.get("WL_ADDRESS")+" " +hmTemp.get("WL_CITY")+" " +hmTemp.get("WL_PINCODE");
				
				strLevelId = hmEmpLevelMap.get(rs.getString("emp_id"));
				
				empDetails.add(rs.getString("empcode"));
				empDetails.add(" ");
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				empDetails.add(rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname"));
				empDetails.add(strTotal_days);
				empDetails.add(hmEmpDepartmentMap.get(rs.getString("depart_id")));
				empDetails.add("0.00");
				empDetails.add(hmEmpDesigMap.get(rs.getString("emp_id")));
				empDetails.add("0.00");
				empDetails.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				empDetails.add(strPaid_days);
				empDetails.add(rs.getString("emp_pan_no"));
				empDetails.add("0.00");
				
				/*empDetails.add(rs.getString("emp_pf_no"));
				empDetails.add(hmTemp.get("WL_NAME"));
				empDetails.add(rs.getString("emp_bank_name"));
				empDetails.add(uF.showData(hmEmpDepartmentMap.get(rs.getString("depart_id")), ""));
				
				empDetails.add(monthName);*/
				//strEmpImage = rs.getString("emp_image");
				
				String strEmpFName = rs.getString("emp_fname").substring(0, 1).toUpperCase()+rs.getString("emp_fname").substring(1);
				strPassword = strEmpFName+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd")+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM");
				 		  
			}
			rs.close();
			pst.close();
		
			List alLoans = new ArrayList();
			Map hmEmpLoan = new HashMap();
			Map<String, String> hmEmpSalLastEffectiveDate = null; //need to implement this map
			CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, uF.getDateFormat(strPayCycleStart, DBDATE, DATE_FORMAT), uF.getDateFormat(strPayCycleEnd, DBDATE, DATE_FORMAT), hmEmpSalLastEffectiveDate);
			Map hmEmpLoanInner = (Map)hmEmpLoan.get(strEmpId); 
			if(hmEmpLoanInner==null) hmEmpLoanInner=new HashMap();
			Map<String, String> hmLoanPolicies = CF.getLoanPoliciesMap(con, uF, strOrgId);
//			String strEmpLevelId = CF.getEmpLevelId(con, getStrEmpId());

			Map<String, List<Map<String, String>>> hmPerkAlign = CF.getIndividualPerkAlignAmount(con, uF, CF, uF.parseToInt(getStrEmpId()),getStrFYS(),getStrFYE(),getStrPCS(),getStrPCE(),uF.parseToInt(getStrPC()));
			if(hmPerkAlign == null) hmPerkAlign = new HashMap<String, List<Map<String,String>>>();
			
			/*pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg,salary_details sd where pg.emp_id =? and pg.month=? AND pg.paycycle=? " +
					"AND pg.financial_year_from_date=? AND pg.financial_year_to_date=? and sd.grade_id=? and pg.salary_head_id = sd.salary_head_id " +
					"and bank_pay_type=? and (sd.is_delete is null or sd.is_delete=false) and pg.salary_head_id in (select salary_head_id " +
					"from emp_salary_details where isdisplay=true and grade_id=? and emp_id=?) order by earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strEmpGradeId));
			pst.setInt(7, uF.parseToInt(getStrBankPayType()));
			pst.setInt(8, uF.parseToInt(strEmpGradeId));
			pst.setInt(9, uF.parseToInt(getStrEmpId()));*/
			
			pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND pg.financial_year_from_date=? " +
				"AND pg.financial_year_to_date=? and (bank_pay_type=? or bank_pay_type is null) and pg.salary_head_id in (select distinct(salary_head_id) as salary_head_id from " +
				"emp_salary_details where isdisplay=true and emp_id=? and grade_id=?) order by earning_deduction desc,pg.salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			pst.setInt(7, uF.parseToInt(getStrEmpId()));
			pst.setInt(8, uF.parseToInt(strEmpGradeId));
			rs=pst.executeQuery();    
           
			double presentDays = 0;
			while (rs.next()) {
       	   
				presentDays = Double.parseDouble(rs.getString("present_days"));
				Double dblTotal = 0.0;
				Double dblTotalGrossAmt = 0.0;
				if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
					
					dblTotal = rs.getDouble("amount"); //OTHER ALLOWANCE/EX-
					dblTotalGrossAmt = rs.getDouble("amount");
					
					if(hmPerkAlign.containsKey(rs.getString("salary_head_id"))){
						List<Map<String, String>> alPerkAlign = hmPerkAlign.get(rs.getString("salary_head_id"));
						if(alPerkAlign == null) alPerkAlign = new ArrayList<Map<String,String>>();
						int nPerkAlignSize = alPerkAlign.size();
						for(int i = 0; i < nPerkAlignSize; i++){
							Map<String, String> hmPerkAlignInner = alPerkAlign.get(i);
							if(hmPerkAlignInner == null) hmPerkAlignInner = new HashMap<String, String>();
							salaryHeadName.add(uF.showData(hmPerkAlignInner.get("PERK_NAME"), "")+" ("+hmSalaryDetailsMap.get(rs.getString("salary_head_id"))+")");
							salHeadAmount.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
							salHeadAmountGross.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
						}
						
					} else{
						salaryHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						salHeadAmount.add(dblTotal);
						salHeadAmountGross.add(dblTotalGrossAmt);
					}										
					
					dblTotalAmt += dblTotal;
					dblGrossTotal += dblTotalGrossAmt;

				} else if (rs.getString("earning_deduction")
						.equalsIgnoreCase("D")) {

					
					if(uF.parseToInt(rs.getString("salary_head_id"))==LOAN){
						
						for(int i=0; i<alLoans.size(); i++){
							String loan=(String)alLoans.get(i)!=null?(String)alLoans.get(i) : "";
							String loanamt=uF.showData((String)hmEmpLoanInner.get(loan), "0");
							dblTotal = uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt)));
							deductionHeadName.add(hmLoanPolicies.get((String)alLoans.get(i)));
							deductionHeadAmount.add(uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt))));
							dblTotalDeduction += dblTotal;
						}
						
						
					}else{
						dblTotal = rs.getDouble("amount");
						deductionHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						deductionHeadAmount.add(dblTotal);
						dblTotalDeduction += dblTotal;
					}
					
				}
				
				nYear=rs.getInt("year");
				
				int salaryHeadId =Integer.parseInt(rs.getString("salary_head_id"));
				
				if(salaryHeadId == IConstants.EMPLOYEE_EPF){
					flagEpf = true; 
				}
				
				
			}     
          
			rs.close();
			pst.close(); 
			
			
			salaryHeadName.add("Total");
			salHeadAmount.add(dblTotalAmt);
			deductionHeadName.add("Deductions");
			deductionHeadName.add("Net Payable");
			
			dblNetSalary = dblTotalAmt - dblTotalDeduction;
			
			deductionHeadAmount.add(dblTotalDeduction);
			deductionHeadAmount.add(dblNetSalary);
			
			salHeadAmountGross.add(dblGrossTotal);

		

			Double totalEarning = salHeadAmount.get(salHeadAmount.size() - 1);
	      /*  if(salaryHeadName.contains("Total")){
	        	salaryHeadName.remove(salaryHeadName.size() - 1);
	        	salHeadAmount.remove(salHeadAmount.size() - 1);
	        }*/
			
			Double totalDeduction =  deductionHeadAmount.get(deductionHeadAmount.size() - 1);
	       /* if(deductionHeadName.contains("Total Deduction")){
           deductionHeadName.remove(deductionHeadName.size() - 1);
	        deductionHeadAmount.remove(deductionHeadAmount.size() - 1);
	        }*/
	        
			pst = con.prepareStatement("select sum(leave_no) as leave_no, lar.leave_type_id from leave_application_register lar, leave_type lt where lar.leave_type_id = lt.leave_type_id and leave_id in (select leave_id from emp_" +
					" where emp_id=?) and _date between ? and ? group by lar.leave_type_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strPayCycleStart, DBDATE));
			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
			
			rs = pst.executeQuery();
			
			totalLeave.add(String.valueOf(presentDays));
			String leaveNo = null ;
			int leaveType = 0;
			while(rs.next()){
				leaveNo = rs.getString("leave_no");
				leaveType = Integer.parseInt(rs.getString("leave_type_id"));
				
				leaveTypeName.add(leaveType);
				totalLeave.add(leaveNo);
				
				hmLeaveType.put(leaveTypeName, totalLeave);
			
			}
		
			
//			pst = con.prepareStatement("select * from emp_salary_details where emp_id = ? and effective_date = (select max(effective_date) as effective_date " +
//				"from emp_salary_details where effective_date <= ? and emp_id=? and grade_id=?) and grade_id=? order by salary_head_id");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setDate(2, uF.getDateFormat(strEntryDate, DBDATE));
//			pst.setInt(3, uF.parseToInt(getStrEmpId()));
//			pst.setInt(4, uF.parseToInt(strEmpGradeId));
//			pst.setInt(5, uF.parseToInt(strEmpGradeId));
//			rs = pst.executeQuery();
//			while(rs.next()){
//				alGross.add(rs.getString("amount"));
//			}
//			rs.close();
//			pst.close();
			
			payEmpHead.add("Emp No.");
			payEmpHead.add("");
			payEmpHead.add("Name");
			payEmpHead.add("Calendar Days");
			payEmpHead.add("Department ");
			payEmpHead.add("W.Offs/ Holidays");
			payEmpHead.add("Designation");
			payEmpHead.add("Leave Days");
			payEmpHead.add("Joining Dt");
			payEmpHead.add("Paid Days");
			payEmpHead.add("Pan");
			payEmpHead.add("O T Hrs");
			
			Map hmLeaveDatesType = new HashMap();
			Map<String, Map<String, String>> leaveDetailsMap = CF.getActualLeaveDates(con, CF, uF, getStrFYS(), getStrFYE(),  hmLeaveDatesType, true, null);
			

			Map<String, String> leaveEmpDetailsMap = leaveDetailsMap.get(getStrEmpId());

			if(leaveEmpDetailsMap!=null)
				{
				
				Set keys = leaveEmpDetailsMap.keySet();
						
				for (Iterator i = keys.iterator(); i.hasNext();) {
					String nkey = (String) i.next();
					String strValue = (String) leaveEmpDetailsMap.get(nkey);
					
	
					Iterator<String> itr = leaveName.iterator();	
				  int count=0;
	             while(itr.hasNext())
	             {
	           	  
	           	  if(strValue.equalsIgnoreCase(itr.next()));
	           	  {
	           		  count=1;
	           	  }
	           }
				
				if(count==0)
				{
					leaveName.add(strValue);
				}
			
			}
				
				Iterator<String> itrleave = leaveName.iterator();	
				  int nleaveToatal=0;
					while(itrleave.hasNext())
					{				
						Set keysC = leaveEmpDetailsMap.keySet();
						String strLeave = itrleave.next();
						int nleaveCount=0;		
						
						
						for (Iterator i = keysC.iterator(); i.hasNext();) {
							String nkey = (String) i.next();
							String strValue = (String) leaveEmpDetailsMap.get(nkey);
							
							if(strValue!=null && strValue.equalsIgnoreCase(strLeave))
							{
								nleaveCount++;
								
								nleaveToatal++;
							}
				
						}  
						
						noOfLeave.add(nleaveCount);
								
						
					}	  
					
					
					noOfLeave.add(nleaveToatal);
	
					leaveName.add("Total");
		}
			
			
			
			
			pst = con.prepareStatement("select sum(accrued_leaves) as accru, sum(taken_leaves) as taken_leaves, (sum(accrued_leaves) - sum(taken_leaves)) as balance, leave_type_id from leave_register where emp_id = ? group by leave_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				hmBalanceLeave.put(rs.getString("leave_type_id"), rs.getString("balance"));
			}
			rs.close();
			pst.close();
			hmLeaveNameMap = CF.getLeaveTypeMap(con);
			
			generateSalarySlipFifththFormatByGrade(getStrEmpId(),getStrMonth(), hmOrganisationDetails,strTotal_days,presentDays,dblNetSalary,totalDeduction,totalEarning,monthName,total,flagEpf);
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			dB.closeResultSet(rs);
			dB.closeStatements(pst);
			dB.closeConnection(con);
		}
	
	}

private void generateSalarySlipFifththFormatByGrade(String strEmpId, String strMonth, Map<String, String> hmOrganisationDetails, String strTotal_days, double presentDays, Double dblNetSalary, Double totalDeduction, Double totalEarning, String monthName, double total,boolean flagEpf) {
	UtilityFunctions uF = new UtilityFunctions();

	try
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		String strCompanyLogo=hmOrganisationDetails.get("ORG_LOGO");
		String strCompanyName=hmOrganisationDetails.get("ORG_NAME");
		String orgId =hmOrganisationDetails.get("ORG_ID");
		Map<String,String> hmFeatureStatus = (Map<String,String>)request.getAttribute("hmFeatureStatus");
		
		Document document = new Document();
		PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
		if(hmFeatureStatus != null &&uF.parseToBoolean(hmFeatureStatus.get(F_PASSWORD_PROTECTED_SALARY_SLIP))){
			pdfWriter.setEncryption(strPassword.getBytes(), strPassword.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
		}
		document.open();
		document.add(new Paragraph(" "));

		String filePath = null;
		String filePathCompanyLOgo = null;
		String filePathCompanyLOgodefault=null;
		String filePathproductLogo= null;
		
		if(CF.getStrDocSaveLocation()!=null){
			filePath = CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+ strEmpId+"/"+strEmpImage;
			
			filePathCompanyLOgo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+strCompanyLogo;
			filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
			filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
		}else{
			filePath = request.getRealPath("/userImages/" +strEmpImage+ "");
			filePathCompanyLOgo = request.getRealPath("/userImages/"+strCompanyLogo+"");
			filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
			filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
		}  
		Image imageLogo=null;
		
		try{
			
			FileInputStream fileInputStream1=null;
	        File file1 = new File(filePathCompanyLOgo);
	        byte[] bFile1 = new byte[(int) file1.length()];
	        fileInputStream1 = new FileInputStream(file1);
		    fileInputStream1.read(bFile1);
		    fileInputStream1.close();
	        imageLogo = Image.getInstance(bFile1);
	        
		}catch(FileNotFoundException e){
			imageLogo = Image.getInstance(filePathCompanyLOgodefault);
		}
		
		PdfPTable photoImagetable = new PdfPTable(1);
		photoImagetable.setWidthPercentage(10);
		photoImagetable.getDefaultCell().setPadding(1);


		PdfPTable companyNameTable = new PdfPTable(1);
		int[] arrheaderwidthsh = { 100 }; // percentage
		companyNameTable.getDefaultCell().setBorderWidth(0);
		companyNameTable.setWidths(arrheaderwidthsh);
		PdfPCell companyNamecell = new PdfPCell(new Phrase(strCompanyName, FontFactory.getFont("Verdana", 14,Font.BOLD)));
		

		companyNamecell.setBorderWidthTop(0);
		companyNamecell.setBorderWidthBottom(1);
		companyNamecell.setBorderWidthLeft(0);
		companyNamecell.setBorderWidthRight(0);

		companyNamecell.setHorizontalAlignment(Element.ALIGN_CENTER);
		companyNameTable.addCell(companyNamecell);
		PdfPCell companyNamecell1 = new PdfPCell(new Phrase(strCompanyAddress, FontFactory.getFont("Verdana", 10,Font.BOLD)));

		companyNamecell1.setBorderWidthTop(0);
		companyNamecell1.setBorderWidthBottom(0);
		companyNamecell1.setBorderWidthLeft(0);
		companyNamecell1.setBorderWidthRight(0);

		companyNamecell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		companyNameTable.addCell(companyNamecell1);

		PdfPTable LogoImage = new PdfPTable(2);
		int[] arrheaderwidths1 = { 30, 70 }; // percentage
		LogoImage.getDefaultCell().setBorderWidth(1);
		LogoImage.setWidths(arrheaderwidths1);
		LogoImage.setTotalWidth(600); 
		LogoImage.addCell(imageLogo);
		LogoImage.addCell(companyNameTable);
		

		PdfPTable titalTable = new PdfPTable(1);
		int[] arrheaderwidths2 = {100}; // percentage
		titalTable.getDefaultCell().setBorderWidth(0);
		titalTable.getDefaultCell().setPadding(0);
		titalTable.setWidths(arrheaderwidths2);

		PdfPCell cellb = new PdfPCell(new Phrase("DATE :  "+monthName,FontFactory.getFont("Verdana", 7, Font.BOLD)));

		cellb.getExtraParagraphSpace();
		cellb.setBorderWidth(1);
		cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
		titalTable.addCell(cellb);
		
		
		PdfPTable empTable = new PdfPTable(6);

		int[] arrheaderwidths5 = { 31, 2, 30, 15, 2, 20 }; // percentage
		empTable.getDefaultCell().setBorderWidth(1);

		empTable.setWidths(arrheaderwidths5);
		empTable.setTotalWidth(800);
		
		Iterator<String> itr = payEmpHead.iterator();
		int k = 0;

		
		for(; k<payEmpHead.size(); k++){
		
			PdfPCell cellE = new PdfPCell(new Phrase(payEmpHead.get(k),FontFactory.getFont("Verdana", 7, Font.BOLD)));
			
			cellE.setBorderWidth(0);
			empTable.addCell(cellE);

			if(payEmpHead.get(k).isEmpty()){
				PdfPCell cellcollan = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 7, Font.BOLD)));
				cellcollan.setBorderWidth(0);
				empTable.addCell(cellcollan);
				}
			else{	
				PdfPCell cellcollan = new PdfPCell(new Phrase(":",FontFactory.getFont("Verdana", 7, Font.BOLD)));
				cellcollan.setBorderWidth(0);
				empTable.addCell(cellcollan);
				}

			PdfPCell cellF = new PdfPCell(new Phrase(empDetails.get(k),FontFactory.getFont("Verdana", 7, Font.NORMAL)));
			cellF.setBorderWidth(0);
			empTable.addCell(cellF);
		}

		PdfPTable imageEmpDetailTable = new PdfPTable(1);

		int[] arrheaderwidths4 = { 100 }; // percentage

		imageEmpDetailTable.getDefaultCell().setBorderWidth(1);
		imageEmpDetailTable.getDefaultCell().setPadding(1);
		imageEmpDetailTable.setWidths(arrheaderwidths4);
		
		PdfPTable titleTable = new PdfPTable(1);
		int[] arrTitle = { 100}; // percentage
		titleTable.getDefaultCell().setBorderWidth(1);
		titleTable.getDefaultCell().setPadding(1);
		titleTable.setWidths(arrTitle);

		PdfPCell cellT = new PdfPCell(new Phrase("                                            SCALE     PAYMENTS       DEDUCTION",FontFactory.getFont("Verdana", 9, Font.BOLD)));

		cellT.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellT.setBorderWidth(1);
		titleTable.addCell(cellT);
		

		PdfPTable grossTitalTable = new PdfPTable(3);

		int[] arrheaderwidths7 = { 50,20,30 }; // percentage
		grossTitalTable.getDefaultCell().setBorderWidth(1);
		grossTitalTable.getDefaultCell().setPadding(1);
		grossTitalTable.setWidths(arrheaderwidths7);

		PdfPTable GrossHeadTable = new PdfPTable(1);
		int[] arrheaderwidths7A = { 100 }; // percentage
		GrossHeadTable.getDefaultCell().setBorderWidth(1);
		GrossHeadTable.getDefaultCell().setPadding(1);
		GrossHeadTable.setWidths(arrheaderwidths7A);

		PdfPTable grossAmtTable = new PdfPTable(1);
		int[] arrheaderwidths7B = { 100 }; // percentage
		grossAmtTable.getDefaultCell().setBorderWidth(1);
		grossAmtTable.getDefaultCell().setPadding(1);
		grossAmtTable.setWidths(arrheaderwidths7B);
		
		PdfPTable tableScale = new PdfPTable(1);
		int[] arrheaderwidths7C = { 100 }; // percentage
		tableScale.getDefaultCell().setBorderWidth(1);
		tableScale.getDefaultCell().setPadding(1);
		tableScale.setWidths(arrheaderwidths7C);

		int nCount = 0;
		Iterator<String> itr1 = salaryHeadName.iterator();
		while (itr1.hasNext()) {

			String strHeadNM = itr1.next();

			if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
				
				PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
				cellQ2.setBorderWidth(0);
				GrossHeadTable.addCell(cellQ2);
				
				PdfPCell cellQS = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));
				cellQS.setBorderWidth(0);
				tableScale.addCell(cellQS);

				PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

				cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellQ3.setBorderWidthTop(0);
				cellQ3.setBorderWidthBottom(0);
				cellQ3.setBorderWidthLeft(0);
				cellQ3.setBorderWidthRight(0);
				grossAmtTable.addCell(cellQ3);

			} else {

				PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQ2.setBorderWidth(0);
				GrossHeadTable.addCell(cellQ2);

				PdfPCell cellQS = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));
				cellQS.setBorderWidth(0);
				tableScale.addCell(cellQS);
				
				PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellQ3.setBorderWidth(0);
				grossAmtTable.addCell(cellQ3);
			}

			nCount++;

		}

		grossTitalTable.addCell(GrossHeadTable);
		grossTitalTable.addCell(tableScale);
		grossTitalTable.addCell(grossAmtTable);

//*************************************************************************		
		PdfPTable netGrossTable = new PdfPTable(2);
		int[] arrheaderwidths6A = { 50, 50 }; // percentage
		netGrossTable.getDefaultCell().setBorderWidth(1);
		netGrossTable.getDefaultCell().setPadding(1);
		netGrossTable.setWidths(arrheaderwidths6A);

		PdfPCell cellQA1 = new PdfPCell(new Phrase("Organisations Contribution ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

		cellQA1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellQA1.setBorderWidth(1);
		netGrossTable.addCell(cellQA1);

		
		PdfPCell cellQA2 = new PdfPCell(new Phrase("Payment By Bank A/C No ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

		cellQA2.setBorderWidth(1);
		cellQA2.setHorizontalAlignment(Element.ALIGN_CENTER);
		netGrossTable.addCell(cellQA2);
		
		//*************************************************************************

		
		
		PdfPTable leaveTable = new PdfPTable(2);
		int[] arrheaderwidths8 = { 60, 40 }; // percentage
		leaveTable.getDefaultCell().setBorderWidth(1);
		leaveTable.getDefaultCell().setPadding(1);
		leaveTable.setWidths(arrheaderwidths8);

		PdfPTable leaveHeadTable = new PdfPTable(1);
		int[] arrheaderwidths8A = { 100 }; // percentage
		leaveHeadTable.getDefaultCell().setBorderWidth(1);
		leaveHeadTable.getDefaultCell().setPadding(1);
		leaveHeadTable.setWidths(arrheaderwidths8A);

		PdfPTable leaveAmtTable = new PdfPTable(1);
		int[] arrheaderwidths8B = { 100 }; // percentage
		leaveAmtTable.getDefaultCell().setBorderWidth(1);
		leaveAmtTable.getDefaultCell().setPadding(1);
		leaveAmtTable.setWidths(arrheaderwidths8B);

		int nCountGross = 0;
		Iterator<String> itr12 = deductionHeadName.iterator();
		while (itr12.hasNext()) {

			String strHeadNM = itr12.next();

			if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Deductions") || strHeadNM.equalsIgnoreCase("Net Payable")) {

				PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
				cellQ2.setBorderWidth(0);
				leaveHeadTable.addCell(cellQ2);

				PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

				cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

				cellQ3.setBorderWidthTop(0);
				cellQ3.setBorderWidthBottom(0);
				cellQ3.setBorderWidthLeft(0);
				cellQ3.setBorderWidthRight(0);
				leaveAmtTable.addCell(cellQ3);
			} else {

				PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM, FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQ2.setBorderWidth(0);
				leaveHeadTable.addCell(cellQ2);
				PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));

				cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

				cellQ3.setBorderWidth(0);
				leaveAmtTable.addCell(cellQ3);

			}

			nCountGross++;

		}

		leaveTable.addCell(leaveHeadTable);
		leaveTable.addCell(leaveAmtTable);

		//*************************************************************************	
		
		PdfPTable Deductiontable = new PdfPTable(2);
		int[] arrheaderwidths9 = { 50, 50 }; // percentage
		Deductiontable.getDefaultCell().setBorderWidth(1);
		Deductiontable.getDefaultCell().setPadding(1);
		Deductiontable.setWidths(arrheaderwidths9);

		PdfPCell cellQ3 = new PdfPCell(grossTitalTable);
		cellQ3.setBorderWidth(1);
		Deductiontable.addCell(cellQ3);

		PdfPCell cellR = new PdfPCell(leaveTable);
		cellR.setBorderWidth(1);
		Deductiontable.addCell(cellR);

		

		PdfPTable HeadleaveDeductionTable = new PdfPTable(2);
		int[] arrheaderwidths12 = { 50, 50 }; // percentage
		HeadleaveDeductionTable.getDefaultCell().setBorderWidth(1);
		HeadleaveDeductionTable.getDefaultCell().setPadding(1);
		HeadleaveDeductionTable.setWidths(arrheaderwidths12);

		PdfPCell celld = new PdfPCell(new PdfPCell(empTable));

		PdfPTable orgTable = new PdfPTable(3);
		int[] arrorg = { 30, 20 ,50}; // percentage
		orgTable.getDefaultCell().setBorderWidth(1);
		orgTable.getDefaultCell().setPadding(1);
		orgTable.setWidths(arrorg);

		PdfPTable orgHeadTable = new PdfPTable(1);
		int[] arrOrgA = { 100 }; // percentage
		orgHeadTable.getDefaultCell().setBorderWidth(0);
		orgHeadTable.getDefaultCell().setPadding(1);
		orgHeadTable.setWidths(arrOrgA);

		PdfPTable orgAmtTable = new PdfPTable(1);
		int[] arrOrgB = { 100 }; // percentage
		orgAmtTable.getDefaultCell().setBorderWidth(0);
		orgAmtTable.getDefaultCell().setPadding(1);
		orgAmtTable.setWidths(arrOrgB);
		
		PdfPTable orgBlank = new PdfPTable(1);
		int[] arrOrgC = { 100 }; // percentage
		orgBlank.getDefaultCell().setBorderWidth(1);
		orgBlank.getDefaultCell().setPadding(1);
		orgBlank.setWidths(arrOrgC);

		List<String> orgHeadAmt = new ArrayList<String>();
		List<String> orgHeadName = new ArrayList<String>();
		orgHeadName.add("Employer PF (O.C)");
		//orgHeadName.add("PF (O.C)");
		orgHeadName.add("Total (O.C)");
		
		orgHeadAmt.add(String.valueOf(total));
		//orgHeadAmt.add("0.00");
		orgHeadAmt.add(String.valueOf(total));
		
		int countOrg = 0;
		Iterator<String> itrOrg = orgHeadName.iterator();
		if(flagEpf){
		while (itrOrg.hasNext()) {

			String strHeadNM = itrOrg.next();

				PdfPCell cellOrg = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellOrg.setBorderWidth(0);
				orgHeadTable.addCell(cellOrg);
				
				PdfPCell cellOrg1 = new PdfPCell(new Phrase(""+ orgHeadAmt.get(countOrg), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellOrg1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellOrg1.setBorderWidth(0);
				orgAmtTable.addCell(cellOrg1);
				
				if(countOrg <2){
				PdfPCell cellOrg2 = new PdfPCell(new Phrase(" "));
				cellOrg2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellOrg2.setBorderWidth(0);
				orgBlank.addCell(cellOrg2);
				}
				
				countOrg++;

		}
		
	}
		PdfPCell cellOrg2 = new PdfPCell(new Phrase("Remark : ",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
		cellOrg2.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellOrg2.setBorderWidth(0);
		orgBlank.addCell(cellOrg2);
		
		
		orgTable.addCell(orgHeadTable);
		orgTable.addCell(orgAmtTable);
		orgTable.addCell(orgBlank);
		
		//*************************************************************
		
		PdfPTable LastTable = new PdfPTable(2);
		int[] arrNo = { 50, 50 }; // percentage
		LastTable.getDefaultCell().setBorderWidth(1);
		LastTable.getDefaultCell().setPadding(1);
		LastTable.setWidths(arrNo);

		PdfPCell cellNo = new PdfPCell(new Phrase("TOLL FREE NO:  ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

		cellNo.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellNo.setBorderWidth(1);
		LastTable.addCell(cellNo);

		
		PdfPCell cellSign = new PdfPCell(new Phrase("This is Computer generated Slip so does not required any signature",FontFactory.getFont("Verdana", 9, Font.BOLD)));

		cellSign.setBorderWidth(1);
		cellSign.setHorizontalAlignment(Element.ALIGN_CENTER);
		LastTable.addCell(cellSign);
		
		//************************************************************
		
		int	nyeardevfirst=nYear;
		nyeardevfirst=nYear%10;
		nYear=nYear/10;
		int	nyeardevsecond=nYear%10;
		

		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));

		imageEmpDetailTable.addCell(celld);
		document.add(LogoImage);
		document.add(titalTable);
		document.add(imageEmpDetailTable);
	//	document.add(netGrossTital);
		document.add(titleTable);
		document.add(Deductiontable);
		document.add(netGrossTable);
		document.add(HeadleaveDeductionTable);
	//	document.add(NetSalaryTable);
		document.add(orgTable);
	//	document.add(signatureTable);
		document.add(LastTable);
		document.close();
			
		
		if(isAttachment()) {
			byte[] bytes = baos.toByteArray();
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(N_NEW_PAYSLIP_GENERATED, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setStrEmpId(strEmpId);
			nF.setPdfData(bytes);
			nF.setStrAttachmentFileName("PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
			nF.setEmailTemplate(true);
			nF.sendNotifications();
		} else {
			response.setContentType("application/pdf");
			response.setContentLength(baos.size());
			response.setHeader("Content-Disposition","attachment; filename=PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
			ServletOutputStream out = response.getOutputStream();              
			baos.writeTo(out);
			out.flush();
			out.close();
			baos.close();
			out.close();
			return;
		}
		
	}catch (Exception e) {
		e.printStackTrace();
	}

}
	
private void createSalarySlipPdfFourthFormatByGrade(String strEmpId2, String strMonth2) {

		
		Database dB = new Database();
		dB.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = dB.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmTemp =null;
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpDepartmentMap = CF.getDepartmentMap(con,null, null);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
//			Map<String, String> hmLevelMap = CF.getLevelMap(con);
//			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			
//			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con);
//			String strEmpGradeId = CF.getEmpGradeId(con, getStrEmpId());
			
			Map<String, String> hmOrganisationDetails = new HashMap<String, String>();
			
			pst =con.prepareStatement("select max(entry_date) as entry_date, paid_from, paid_to,paid_days,total_days ,payment_mode,month,year " +
					"from payroll_generation where emp_id = ? and financial_year_from_date=? AND financial_year_to_date=? AND month=? " +
					"and paycycle=? and (bank_pay_type=? or bank_pay_type is null) group by paid_from, paid_to,paid_days,total_days,payment_mode,month,year");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(getStrPC()));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			rs = pst.executeQuery();
			String strEntryDate = null;
			String strPayCycleStart = null;
			String strPayCycleEnd = null;
			String strTotal_days =null;
			String strPaid_days = null;
			String strPresent_days = null;
			while(rs.next()){
				strEntryDate = rs.getString("entry_date");
				strPayCycleStart = rs.getString("paid_from");
				strPayCycleEnd = rs.getString("paid_to");
			//===start parvez date: 04-03-2023 commented===	
//				strTotal_days =rs.getString("total_days");
//				strPaid_days =rs.getString("paid_days");
			//===end parvez date: 04-03-2023===	
			//	strPresent_days = rs.getString("present_days");
				strPaymentMode =rs.getString("payment_mode");
				 pMonth =rs.getInt("month");
				 pYear =rs.getInt("year");
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 04-03-2023===
			pst =con.prepareStatement("select * from approve_attendance where emp_id = ? and financial_year_start=? AND financial_year_end=? and paycycle=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrPC()));
			rs = pst.executeQuery();
			while(rs.next()) {
				strTotal_days =rs.getString("total_days");
				strPaid_days =rs.getString("paid_days");
			}
			rs.close();
			pst.close();
		//===end parvez date: 04-03-2023===
			
		    monthName = uF.getMonth(pMonth)+" "+pYear;
		   
	//===start parvez date: 04-03-2023===	    
//           tDays = uF.parseToInt(strTotal_days);
//           pDays = uF.parseToInt(strPaid_days);
           tDays = uF.parseToDouble(strTotal_days);
           pDays = uF.parseToDouble(strPaid_days);
       //===end parvez date: 04-03-2023===    
           unpaidDays = tDays - pDays;
           
	            
			if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("1")){
				strPaymentMode ="Bank Transfer";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cash";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cheque";	
			}
				
	            
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
		
			pst = con.prepareStatement("select * from org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
	
			while(rs.next()){
				hmOrganisationDetails.put("ORG_ID", rs.getString("org_id"));
				hmOrganisationDetails.put("ORG_LOGO", rs.getString("org_logo"));
				hmOrganisationDetails.put("ORG_NAME", rs.getString("org_name"));
				hmOrganisationDetails.put("PDF_TYPE", rs.getString("payslip_format"));
			}
			rs.close();
			pst.close();
		
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("SELECT MAX(effective_date) as effective_date, grade_id FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
//				"and isdisplay=true and effective_date<=? group by grade_id");
			sbQuery.append("SELECT MAX(effective_date) as effective_date, grade_id FROM emp_salary_details WHERE emp_id=? and is_approved=true and " +
					"isdisplay=true and effective_date = (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
					"and isdisplay=true and effective_date<=?) group by grade_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
//				System.out.println("pst ================>> " + pst);
			String strEffectiveDate = null;
			String strEmpGradeId = null;
			rs = pst.executeQuery();
			while(rs.next()){
				strEffectiveDate = rs.getString("effective_date");
				strEmpGradeId = rs.getString("grade_id");
			}
			rs.close();
			pst.close();
//			System.out.println("strEmpGradeId ===>> " + strEmpGradeId);
			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMapByGrade(con, uF.parseToInt(strEmpGradeId));
				
			pst = con.prepareStatement("SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id  = eod.emp_id and emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map hmWorkLocationMap = CF.getWorkLocationMap(con);
				hmTemp = (Map<String, String>)hmWorkLocationMap.get(rs.getInt("wlocation_id")+"");
				if(hmTemp==null)hmTemp=new HashMap();
				strCompanyAddress = hmTemp.get("WL_ADDRESS")+" " +hmTemp.get("WL_CITY")+" " +hmTemp.get("WL_PINCODE");
				
				strLevelId = hmEmpLevelMap.get(rs.getString("emp_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				empDetails.add(rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname"));
				empDetails.add(hmEmpDesigMap.get(rs.getString("emp_id")));
				empDetails.add(rs.getString("empcode"));
				
				empDetails.add(rs.getString("emp_pf_no"));
				empDetails.add(hmTemp.get("WL_NAME"));
				empDetails.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				empDetails.add(hmEmpDepartmentMap.get(rs.getString("depart_id")));
				empDetails.add(rs.getString("emp_pan_no"));
				
				empDetails.add(rs.getString("emp_bank_name"));
				empDetails.add(uF.showData(hmEmpDepartmentMap.get(rs.getString("depart_id")), ""));
				
				empDetails.add(monthName);
				strEmpImage = rs.getString("emp_image");
				
				String strEmpFName = rs.getString("emp_fname").substring(0, 1).toUpperCase()+rs.getString("emp_fname").substring(1);
				strPassword = strEmpFName+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd")+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM");
			}
			rs.close();
			pst.close();
		
			List alLoans = new ArrayList();
			Map hmEmpLoan = new HashMap();
			Map<String, String> hmEmpSalLastEffectiveDate = null; //need to implement this map
			CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, uF.getDateFormat(strPayCycleStart, DBDATE, DATE_FORMAT), uF.getDateFormat(strPayCycleEnd, DBDATE, DATE_FORMAT), hmEmpSalLastEffectiveDate);
			Map hmEmpLoanInner = (Map)hmEmpLoan.get(strEmpId); 
			if(hmEmpLoanInner==null) hmEmpLoanInner=new HashMap();
			Map<String, String> hmLoanPolicies = CF.getLoanPoliciesMap(con, uF, strOrgId);
//			String strEmpLevelId = CF.getEmpLevelId(con, getStrEmpId());

			Map<String, List<Map<String, String>>> hmPerkAlign = CF.getIndividualPerkAlignAmount(con, uF, CF, uF.parseToInt(getStrEmpId()),getStrFYS(),getStrFYE(),getStrPCS(),getStrPCE(),uF.parseToInt(getStrPC()));
			if(hmPerkAlign == null) hmPerkAlign = new HashMap<String, List<Map<String,String>>>();
			
			/*pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg,salary_details sd where pg.emp_id =? and pg.month=? AND pg.paycycle=? " +
					"AND pg.financial_year_from_date=? AND pg.financial_year_to_date=? and sd.grade_id=? and pg.salary_head_id = sd.salary_head_id " +
					"and bank_pay_type=? and (sd.is_delete is null or sd.is_delete=false) and pg.salary_head_id in (select salary_head_id " +
					"from emp_salary_details where isdisplay=true and emp_id=? and grade_id=?) order by earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strEmpGradeId));
			pst.setInt(7, uF.parseToInt(getStrBankPayType()));
			pst.setInt(8, uF.parseToInt(getStrEmpId()));
			pst.setInt(9, uF.parseToInt(strEmpGradeId));*/
			
			pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND pg.financial_year_from_date=? " +
				"AND pg.financial_year_to_date=? and (bank_pay_type=? or bank_pay_type is null) and pg.salary_head_id in (select distinct(salary_head_id) as salary_head_id from " +
				"emp_salary_details where isdisplay=true and emp_id=? and grade_id=?) order by earning_deduction desc,pg.salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			pst.setInt(7, uF.parseToInt(getStrEmpId()));
			pst.setInt(8, uF.parseToInt(strEmpGradeId));
			rs=pst.executeQuery();    
           
			double presentDays = 0;
			while (rs.next()) {
       	   
				presentDays = Double.parseDouble(rs.getString("present_days"));
       	     
				Double dblTotal = 0.0;
				Double dblTotalGrossAmt = 0.0;
				if (rs.getString("earning_deduction").equalsIgnoreCase("E")) {
					
					dblTotal = rs.getDouble("amount"); //OTHER ALLOWANCE/EX-
					dblTotalGrossAmt = rs.getDouble("amount");
					
					if(hmPerkAlign.containsKey(rs.getString("salary_head_id"))){
						List<Map<String, String>> alPerkAlign = hmPerkAlign.get(rs.getString("salary_head_id"));
						if(alPerkAlign == null) alPerkAlign = new ArrayList<Map<String,String>>();
						int nPerkAlignSize = alPerkAlign.size();
						for(int i = 0; i < nPerkAlignSize; i++){
							Map<String, String> hmPerkAlignInner = alPerkAlign.get(i);
							if(hmPerkAlignInner == null) hmPerkAlignInner = new HashMap<String, String>();
							salaryHeadName.add(uF.showData(hmPerkAlignInner.get("PERK_NAME"), "")+" ("+hmSalaryDetailsMap.get(rs.getString("salary_head_id"))+")");
							salHeadAmount.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
							salHeadAmountGross.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
						}
					} else{
						salaryHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						salHeadAmount.add(dblTotal);
						salHeadAmountGross.add(dblTotalGrossAmt);
					}
					
					dblTotalAmt += dblTotal;
					dblGrossTotal += dblTotalGrossAmt;

				} else if (rs.getString("earning_deduction").equalsIgnoreCase("D")) {
					if(uF.parseToInt(rs.getString("salary_head_id"))==LOAN){
						for(int i=0; i<alLoans.size(); i++){
							String loan=(String)alLoans.get(i)!=null?(String)alLoans.get(i) : "";
							String loanamt=uF.showData((String)hmEmpLoanInner.get(loan), "0");
							dblTotal = uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt)));
							deductionHeadName.add(hmLoanPolicies.get((String)alLoans.get(i)));
							deductionHeadAmount.add(uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt))));
							dblTotalDeduction += dblTotal;
						}
					}else{
						dblTotal = rs.getDouble("amount");
						deductionHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						deductionHeadAmount.add(dblTotal);
						dblTotalDeduction += dblTotal;
					}
					
				}
				
				nYear=rs.getInt("year");

			}     
          
			rs.close();
			pst.close();       			
			salaryHeadName.add("Total");
			salHeadAmount.add(dblTotalAmt);
			deductionHeadName.add("Total Deduction");
			deductionHeadAmount.add(dblTotalDeduction);
			salHeadAmountGross.add(dblGrossTotal);

			dblNetSalary = dblTotalAmt - dblTotalDeduction;

			Double totalEarning = salHeadAmount.get(salHeadAmount.size() - 1);
	        if(salaryHeadName.contains("Total")){
	        	salaryHeadName.remove(salaryHeadName.size() - 1);
	        	salHeadAmount.remove(salHeadAmount.size() - 1);
	        }
			
			Double totalDeduction =  deductionHeadAmount.get(deductionHeadAmount.size() - 1);
	        if(deductionHeadName.contains("Total Deduction")){
           deductionHeadName.remove(deductionHeadName.size() - 1);
	        deductionHeadAmount.remove(deductionHeadAmount.size() - 1);
	        }
	        
			pst = con.prepareStatement("select sum(leave_no) as leave_no, lar.leave_type_id from leave_application_register lar, leave_type lt where lar.leave_type_id = lt.leave_type_id and leave_id in (select leave_id from emp_leave_entry where emp_id=?) and _date between ? and ? group by lar.leave_type_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strPayCycleStart, DBDATE));
			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
			
			rs = pst.executeQuery();
			
			totalLeave.add(String.valueOf(presentDays));
			String leaveNo = null ;
			int leaveType = 0;
			while(rs.next()){
				leaveNo = rs.getString("leave_no");
				leaveType = Integer.parseInt(rs.getString("leave_type_id"));
				
				leaveTypeName.add(leaveType);
				totalLeave.add(leaveNo);
				
				hmLeaveType.put(leaveTypeName, totalLeave);
			
			}
			
			Iterator<Integer> itrL = leaveTypeName.iterator();	
		
			 leaveNameType.add("Present Days");
				while(itrL.hasNext())
				{	
					int lvId = itrL.next();
					
					pst = con.prepareStatement("select leave_type_name from leave_type where leave_type_id=?");
					pst.setInt(1, lvId);
					
					rs = pst.executeQuery();
					
					while(rs.next()){
						
						String Nleave = rs.getString("leave_type_name");
						leaveNameType.add(Nleave);
					}
					
				}
				
				leaveNameType.add("Total Days");
				leaveNameType.add("QB Days");
				leaveNameType.add("Earned Hrs");
				leaveNameType.add("Eff Hrs");
				leaveNameType.add("A");
				leaveNameType.add("B");
				leaveNameType.add("C");
				
				
				totalLeave.add(strTotal_days);
				totalLeave.add("0");
				totalLeave.add("0.00");
				totalLeave.add("0.00");
				totalLeave.add("0");
				totalLeave.add("0");
				totalLeave.add("0");
			
//			pst = con.prepareStatement("select * from emp_salary_details where emp_id = ? and effective_date = (select max(effective_date) as effective_date " +
//					"from emp_salary_details where effective_date <= ? and emp_id=? and grade_id=?) and grade_id=? order by salary_head_id");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setDate(2, uF.getDateFormat(strEntryDate, DBDATE));
//			pst.setInt(3, uF.parseToInt(getStrEmpId()));
//			pst.setInt(4, uF.parseToInt(strEmpGradeId));
//			pst.setInt(5, uF.parseToInt(strEmpGradeId));
//			rs = pst.executeQuery();
//			while(rs.next()){
//				alGross.add(rs.getString("amount"));
//			}
//			rs.close();
//			pst.close();
			
			payEmpHead.add("EMPLOYEE NAME");
			payEmpHead.add("DESIGNATION");
			payEmpHead.add("EMPLOYEE CODE");
			
			
			
			Map hmLeaveDatesType = new HashMap();
			Map<String, Map<String, String>> leaveDetailsMap = CF.getActualLeaveDates(con, CF, uF, getStrFYS(), getStrFYE(),  hmLeaveDatesType, true, null);
			
			Map<String, String> leaveEmpDetailsMap = leaveDetailsMap.get(getStrEmpId());

			if(leaveEmpDetailsMap!=null)
			{
			
			Set keys = leaveEmpDetailsMap.keySet();
					
			for (Iterator i = keys.iterator(); i.hasNext();) {
				String nkey = (String) i.next();
				String strValue = (String) leaveEmpDetailsMap.get(nkey);
				

				Iterator<String> itr = leaveName.iterator();	
			  int count=0;
             while(itr.hasNext())
             {
           	  
           	  if(strValue.equalsIgnoreCase(itr.next()));
           	  {
           		  count=1;
           	  }
           }
			
			if(count==0)
			{
				leaveName.add(strValue);
			}
		
		}
			
			Iterator<String> itrleave = leaveName.iterator();	
			  int nleaveToatal=0;
				while(itrleave.hasNext())
				{				
					Set keysC = leaveEmpDetailsMap.keySet();
					String strLeave = itrleave.next();
					int nleaveCount=0;		
					
					
					for (Iterator i = keysC.iterator(); i.hasNext();) {
						String nkey = (String) i.next();
						String strValue = (String) leaveEmpDetailsMap.get(nkey);
						
						if(strValue!=null && strValue.equalsIgnoreCase(strLeave))
						{
							nleaveCount++;
							
							nleaveToatal++;
						}
			
					}  
					
					noOfLeave.add(nleaveCount);
							
					
				}	  
				
				
				noOfLeave.add(nleaveToatal);

				leaveName.add("Total");
	}
			
			
			
			
			pst = con.prepareStatement("select sum(accrued_leaves) as accru, sum(taken_leaves) as taken_leaves, (sum(accrued_leaves) - sum(taken_leaves)) as balance, leave_type_id from leave_register where emp_id = ? group by leave_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				hmBalanceLeave.put(rs.getString("leave_type_id"), rs.getString("balance"));
			}
			rs.close();
			pst.close();
			hmLeaveNameMap = CF.getLeaveTypeMap(con);
			
			generateSalarySlipFourthFormatByGrade(getStrEmpId(),getStrMonth(), hmOrganisationDetails,strTotal_days,presentDays,dblNetSalary,totalDeduction,totalEarning,monthName);
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			dB.closeResultSet(rs);
			dB.closeStatements(pst);
			dB.closeConnection(con);
		}
	
	}

private void generateSalarySlipFourthFormatByGrade(String strEmpId, String strMonth, Map<String, String> hmOrganisationDetails, String strTotal_days, double presentDays, Double dblNetSalary, Double totalDeduction, Double totalEarning, String monthName) {
	UtilityFunctions uF = new UtilityFunctions();
	try {
		
		int count=0;
		int	nyeardevfirst = 0;
		int	nyeardevsecond=0;
		List<Integer> alList=(List<Integer>)request.getAttribute("alList");
		Map<String,String> hmempcnt =(Map<String,String> )request.getAttribute("hmempcnt");
		Map<Integer,Map<String,String>> hmMap=(Map<Integer,Map<String,String>>)request.getAttribute("hmMap");
		if(hmMap==null)hmMap=new HashMap<Integer, Map<String,String>>();
		
		Map<String,String> hmEPFChallan=(Map<String,String>)request.getAttribute("hmEPFChallan");
		
		Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
		if(hmOrg == null) hmOrg = new HashMap<String, String>();
		Map<String,String> hmFeatureStatus = (Map<String,String>)request.getAttribute("hmFeatureStatus");
		
		com.itextpdf.text.Font heading = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 13);
		com.itextpdf.text.Font normal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11);
		com.itextpdf.text.Font normalwithbold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
		com.itextpdf.text.Font small = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7);
		com.itextpdf.text.Font smallBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7,Font.BOLD);
		com.itextpdf.text.Font italicEffect = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		com.itextpdf.text.Document document = new com.itextpdf.text.Document();
		com.itextpdf.text.pdf.PdfWriter pdfWriter = com.itextpdf.text.pdf.PdfWriter.getInstance(document,buffer);
		
		if(hmFeatureStatus != null &&uF.parseToBoolean(hmFeatureStatus.get(F_PASSWORD_PROTECTED_SALARY_SLIP))){
			pdfWriter.setEncryption(strPassword.getBytes(), strPassword.getBytes(), com.itextpdf.text.pdf.PdfWriter.ALLOW_PRINTING, com.itextpdf.text.pdf.PdfWriter.ENCRYPTION_AES_128);
		}
		
		document.open();
       
       while(count<2){
       com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(16);
		table.setWidthPercentage(100);       
		int[] cols = {10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10};
		table.setWidths(cols);
       
		String strCompanyName=hmOrganisationDetails.get("ORG_NAME");
		String orgId =hmOrganisationDetails.get("ORG_ID");
		
		com.itextpdf.text.pdf.PdfPCell row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(strCompanyName));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.TOP |Rectangle.LEFT|Rectangle.RIGHT|Rectangle.BOTTOM);
       row1.setColspan(5);
       row1.setPadding(2.5f);
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Pay Slip For : "+monthName,small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(5);
       row1.setPadding(2.5f);
       table.addCell(row1); 
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Desig. : "+empDetails.get(1),small));
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(6);
       row1.setPadding(2.5f);
       table.addCell(row1); 
    
       //New Row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Emp No : "+uF.showData(empDetails.get(2), ""),small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(4);	        
       table.addCell(row1);
     
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Emp Name : "+uF.showData(empDetails.get(0), ""),small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(4);	
       table.addCell(row1);
       
   
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("PF No. : "+uF.showData(empDetails.get(3), ""),small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);	
       row1.setColspan(4);	
       table.addCell(row1);
     
   
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Department Name : "+uF.showData(empDetails.get(9), ""),small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(4);	
       table.addCell(row1);
     
       
       com.itextpdf.text.pdf.PdfPTable tableL = new com.itextpdf.text.pdf.PdfPTable(leaveNameType.size());
       tableL.setWidthPercentage(100);      
       
       
       for(int i=0;i<leaveNameType.size();i++) {
			
       	com.itextpdf.text.pdf.PdfPCell rowinner11 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(leaveNameType.get(i),small));
       	rowinner11.setPadding(2.5f);
       	rowinner11.setHorizontalAlignment(Element.ALIGN_CENTER);
       	rowinner11.setBorder(Rectangle.RIGHT|Rectangle.TOP|Rectangle.LEFT|Rectangle.BOTTOM);
       	
			tableL.addCell(rowinner11);
	        
			
       }
	        
       for(int i=0;i<totalLeave.size();i++) {
       	
       	com.itextpdf.text.pdf.PdfPCell rowinner22 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(totalLeave.get(i),small));
			rowinner22.setPadding(2.5f);
			rowinner22.setHorizontalAlignment(Element.ALIGN_CENTER);
			rowinner22.setBorder(Rectangle.RIGHT|Rectangle.LEFT|Rectangle.BOTTOM);
			
			tableL.addCell(rowinner22);
	        
	      
		}
   
       row1 =new com.itextpdf.text.pdf.PdfPCell(tableL);
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(16);	        
       table.addCell(row1);
    
  
     //New Row  
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Earnings",small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM);
       row1.setColspan(3);	        
       table.addCell(row1);
       
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total Earnings",small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setBorder(Rectangle.BOTTOM );
       row1.setColspan(3);	 
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("     Actual Payment",small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM);
       row1.setColspan(3);	 
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Deductions",small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM);
       row1.setColspan(3);	 
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total Deduction",small));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setBorder(Rectangle.BOTTOM);
       row1.setColspan(4);	 
       table.addCell(row1);
       
       
       com.itextpdf.text.pdf.PdfPTable table1 = new com.itextpdf.text.pdf.PdfPTable(8);
       table1.setWidthPercentage(100);   

       /*totalEarning = salHeadAmount.get(salHeadAmount.size() - 1);
       if(salaryHeadName.contains("Total")){
       	salaryHeadName.remove(salaryHeadName.size() - 1);
       	salHeadAmount.remove(salHeadAmount.size() - 1);
       }*/
       
		for(int i=0;i<salaryHeadName.size();i++) {
			
			com.itextpdf.text.pdf.PdfPCell rowinner1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(salaryHeadName.get(i),small));
			rowinner1.setPadding(2.5f);
			rowinner1.setHorizontalAlignment(Element.ALIGN_LEFT);
			rowinner1.setBorder(Rectangle.NO_BORDER);
			rowinner1.setColspan(3);
	        table1.addCell(rowinner1);
	        
//	        System.out.println("Salary Name :"+salaryHeadName.get(i));
	        
	        com.itextpdf.text.pdf.PdfPCell rowinner2 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(Double.toString(salHeadAmount.get(i)),small));
			rowinner2.setPadding(2.5f);
			rowinner2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			rowinner2.setBorder(Rectangle.NO_BORDER);
			rowinner2.setColspan(3);
	        table1.addCell(rowinner2);
	        
//	        System.out.println("Salary Ammount :"+salHeadAmount.get(i));
	        
	        com.itextpdf.text.pdf.PdfPCell rowinner3 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(Double.toString(salHeadAmount.get(i)),small));
	        rowinner3.setPadding(2.5f);
	        rowinner3.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowinner3.setBorder(Rectangle.RIGHT);
	        rowinner3.setColspan(2);
	        table1.addCell(rowinner3);
	        
//	        System.out.println("Salary Ammount :"+salHeadAmount.get(i));
	      
		}
		//New Row  
       row1 =new com.itextpdf.text.pdf.PdfPCell(table1);
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(8);	        
       table.addCell(row1);
       
       
       com.itextpdf.text.pdf.PdfPTable table2 = new com.itextpdf.text.pdf.PdfPTable(8);
       table2.setWidthPercentage(100);       

       /*totalDeduction =  deductionHeadAmount.get(deductionHeadAmount.size() - 1);
       if(deductionHeadName.contains("Total Deduction")){
       deductionHeadName.remove(deductionHeadName.size() - 1);
       deductionHeadAmount.remove(deductionHeadAmount.size() - 1);
       }*/
       
       for(int i=0;i<deductionHeadName.size();i++) {
			
       	com.itextpdf.text.pdf.PdfPCell rowinner1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(deductionHeadName.get(i),small));
			rowinner1.setPadding(2.5f);
			rowinner1.setHorizontalAlignment(Element.ALIGN_LEFT);
			rowinner1.setBorder(Rectangle.NO_BORDER);
			rowinner1.setColspan(4);
	        table2.addCell(rowinner1);
	        
	        
	        com.itextpdf.text.pdf.PdfPCell rowinner2 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(Double.toString(deductionHeadAmount.get(i)),small));
			rowinner2.setPadding(2.5f);
			rowinner2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			rowinner2.setBorder(Rectangle.NO_BORDER);
			rowinner2.setColspan(4);
	        table2.addCell(rowinner2);
	        
	      
		}
   
       row1 =new com.itextpdf.text.pdf.PdfPCell(table2);
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(8);	        
       table.addCell(row1);
    
       //New Row
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(8);	        
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("   Gross Deduction : ",smallBold));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(4);	 
       table.addCell(row1);
       
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("              "+totalDeduction,smallBold));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(4);	        
       table.addCell(row1);
       
       

       //New Row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Gross Earning  : ",smallBold));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(4);	        
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "+totalEarning,smallBold));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(4);	 
       table.addCell(row1);
      
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("   Net Pay : ",smallBold));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(4);	 
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "+uF.formatIntoTwoDecimal(dblNetSalary),smallBold));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
       row1.setColspan(4);	 
       table.addCell(row1);
     
       
       
	        //New Row
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(16);	 
       table.addCell(row1);
       
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(16);	 
       table.addCell(row1);
       
       //New Row
       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(11);	 
       table.addCell(row1);
     
	    row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Authorised Signatory",smallBold));
       row1.setPadding(2.5f);
       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
       row1.setBorder(Rectangle.NO_BORDER);
       row1.setColspan(5);	 
       table.addCell(row1);
       
       nyeardevfirst=nYear;
		nyeardevfirst=nYear%10;
		nYear=nYear/10;
		nyeardevsecond=nYear%10;
       
		document.newPage();
		
       document.add(table);
       count++;
       }
       document.close();
       
		String filename="PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf";
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename="+filename+"");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
	
}catch (Exception e) {
	e.printStackTrace();  
}
	
}





	
	private void generateSalarySlipFifththFormat(String strEmpId, String strMonth, Map<String, String> hmOrganisationDetails, String strTotal_days, double presentDays, Double dblNetSalary, Double totalDeduction, Double totalEarning, String monthName, double total,boolean flagEpf) {
		UtilityFunctions uF = new UtilityFunctions();

		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			String strCompanyLogo=hmOrganisationDetails.get("ORG_LOGO");
			String strCompanyName=hmOrganisationDetails.get("ORG_NAME");
			String orgId =hmOrganisationDetails.get("ORG_ID");
			Map<String,String> hmFeatureStatus = (Map<String,String>)request.getAttribute("hmFeatureStatus");
			
			Document document = new Document();
			PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
			if(hmFeatureStatus != null &&uF.parseToBoolean(hmFeatureStatus.get(F_PASSWORD_PROTECTED_SALARY_SLIP))){
				pdfWriter.setEncryption(strPassword.getBytes(), strPassword.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
			}
			document.open();
			document.add(new Paragraph(" "));

			String filePath = null;
			String filePathCompanyLOgo = null;
			String filePathCompanyLOgodefault=null;
			String filePathproductLogo= null;
			
			if(CF.getStrDocSaveLocation()!=null){
				filePath = CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+ strEmpId+"/"+strEmpImage;
				
				filePathCompanyLOgo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+strCompanyLogo;
				filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
				filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
			}else{
				filePath = request.getRealPath("/userImages/" +strEmpImage+ "");
				filePathCompanyLOgo = request.getRealPath("/userImages/"+strCompanyLogo+"");
				filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
				filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
			}  
			Image imageLogo=null;
			
			try{
				
				FileInputStream fileInputStream1=null;
		        File file1 = new File(filePathCompanyLOgo);
		        byte[] bFile1 = new byte[(int) file1.length()];
		        fileInputStream1 = new FileInputStream(file1);
			    fileInputStream1.read(bFile1);
			    fileInputStream1.close();
		        imageLogo = Image.getInstance(bFile1);
		        
			}catch(FileNotFoundException e){
				imageLogo = Image.getInstance(filePathCompanyLOgodefault);
			}
			
			PdfPTable photoImagetable = new PdfPTable(1);
			photoImagetable.setWidthPercentage(10);
			photoImagetable.getDefaultCell().setPadding(1);


			PdfPTable companyNameTable = new PdfPTable(1);
			int[] arrheaderwidthsh = { 100 }; // percentage
			companyNameTable.getDefaultCell().setBorderWidth(0);
			companyNameTable.setWidths(arrheaderwidthsh);
			PdfPCell companyNamecell = new PdfPCell(new Phrase(strCompanyName, FontFactory.getFont("Verdana", 14,Font.BOLD)));
			

			companyNamecell.setBorderWidthTop(0);
			companyNamecell.setBorderWidthBottom(1);
			companyNamecell.setBorderWidthLeft(0);
			companyNamecell.setBorderWidthRight(0);

			companyNamecell.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell);
			PdfPCell companyNamecell1 = new PdfPCell(new Phrase(strCompanyAddress, FontFactory.getFont("Verdana", 10,Font.BOLD)));

			companyNamecell1.setBorderWidthTop(0);
			companyNamecell1.setBorderWidthBottom(0);
			companyNamecell1.setBorderWidthLeft(0);
			companyNamecell1.setBorderWidthRight(0);

			companyNamecell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell1);

			PdfPTable LogoImage = new PdfPTable(2);
			int[] arrheaderwidths1 = { 30, 70 }; // percentage
			LogoImage.getDefaultCell().setBorderWidth(1);
			LogoImage.setWidths(arrheaderwidths1);
			LogoImage.setTotalWidth(600); 
			LogoImage.addCell(imageLogo);
			LogoImage.addCell(companyNameTable);
			

			PdfPTable titalTable = new PdfPTable(1);
			int[] arrheaderwidths2 = {100}; // percentage
			titalTable.getDefaultCell().setBorderWidth(0);
			titalTable.getDefaultCell().setPadding(0);
			titalTable.setWidths(arrheaderwidths2);

			PdfPCell cellb = new PdfPCell(new Phrase("Salary for month:  "+monthName,FontFactory.getFont("Verdana", 7, Font.BOLD)));
			cellb.getExtraParagraphSpace();
			cellb.setBorderWidth(1);
			cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
			titalTable.addCell(cellb);
			
			
			PdfPTable empTable = new PdfPTable(6);

			int[] arrheaderwidths5 = { 31, 2, 30, 15, 2, 20 }; // percentage
			empTable.getDefaultCell().setBorderWidth(1);

			empTable.setWidths(arrheaderwidths5);
			empTable.setTotalWidth(800);
			
			Iterator<String> itr = payEmpHead.iterator();
			int k = 0;

			
			for(; k<payEmpHead.size(); k++){
			
				PdfPCell cellE = new PdfPCell(new Phrase(payEmpHead.get(k),FontFactory.getFont("Verdana", 7, Font.BOLD)));
				
				cellE.setBorderWidth(0);
				empTable.addCell(cellE);

				if(payEmpHead.get(k).isEmpty()){
					PdfPCell cellcollan = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 7, Font.BOLD)));
					cellcollan.setBorderWidth(0);
					empTable.addCell(cellcollan);
					}
				else{	
					PdfPCell cellcollan = new PdfPCell(new Phrase(":",FontFactory.getFont("Verdana", 7, Font.BOLD)));
					cellcollan.setBorderWidth(0);
					empTable.addCell(cellcollan);
					}

				PdfPCell cellF = new PdfPCell(new Phrase(empDetails.get(k),FontFactory.getFont("Verdana", 7, Font.NORMAL)));
				cellF.setBorderWidth(0);
				empTable.addCell(cellF);
			}

			PdfPTable imageEmpDetailTable = new PdfPTable(1);

			int[] arrheaderwidths4 = { 100 }; // percentage

			imageEmpDetailTable.getDefaultCell().setBorderWidth(1);
			imageEmpDetailTable.getDefaultCell().setPadding(1);
			imageEmpDetailTable.setWidths(arrheaderwidths4);
			
			PdfPTable titleTable = new PdfPTable(1);
			int[] arrTitle = { 100}; // percentage
			titleTable.getDefaultCell().setBorderWidth(1);
			titleTable.getDefaultCell().setPadding(1);
			titleTable.setWidths(arrTitle);

			PdfPCell cellT = new PdfPCell(new Phrase("                                            SCALE     PAYMENTS       DEDUCTION",FontFactory.getFont("Verdana", 9, Font.BOLD)));

			cellT.setHorizontalAlignment(Element.ALIGN_LEFT);
			cellT.setBorderWidth(1);
			titleTable.addCell(cellT);
			

			PdfPTable grossTitalTable = new PdfPTable(3);

			int[] arrheaderwidths7 = { 50,20,30 }; // percentage
			grossTitalTable.getDefaultCell().setBorderWidth(1);
			grossTitalTable.getDefaultCell().setPadding(1);
			grossTitalTable.setWidths(arrheaderwidths7);

			PdfPTable GrossHeadTable = new PdfPTable(1);
			int[] arrheaderwidths7A = { 100 }; // percentage
			GrossHeadTable.getDefaultCell().setBorderWidth(1);
			GrossHeadTable.getDefaultCell().setPadding(1);
			GrossHeadTable.setWidths(arrheaderwidths7A);

			PdfPTable grossAmtTable = new PdfPTable(1);
			int[] arrheaderwidths7B = { 100 }; // percentage
			grossAmtTable.getDefaultCell().setBorderWidth(1);
			grossAmtTable.getDefaultCell().setPadding(1);
			grossAmtTable.setWidths(arrheaderwidths7B);
			
			PdfPTable tableScale = new PdfPTable(1);
			int[] arrheaderwidths7C = { 100 }; // percentage
			tableScale.getDefaultCell().setBorderWidth(1);
			tableScale.getDefaultCell().setPadding(1);
			tableScale.setWidths(arrheaderwidths7C);

			int nCount = 0;
			Iterator<String> itr1 = salaryHeadName.iterator();
			while (itr1.hasNext()) {

				String strHeadNM = itr1.next();

				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
					
					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
					GrossHeadTable.addCell(cellQ2);
					
					PdfPCell cellQS = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQS.setBorderWidth(0);
					tableScale.addCell(cellQS);

					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidthTop(0);
					cellQ3.setBorderWidthBottom(0);
					cellQ3.setBorderWidthLeft(0);
					cellQ3.setBorderWidthRight(0);
					grossAmtTable.addCell(cellQ3);

				} else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					GrossHeadTable.addCell(cellQ2);

					PdfPCell cellQS = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQS.setBorderWidth(0);
					tableScale.addCell(cellQS);
					
					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidth(0);
					grossAmtTable.addCell(cellQ3);
				}

				nCount++;

			}

			grossTitalTable.addCell(GrossHeadTable);
			grossTitalTable.addCell(tableScale);
			grossTitalTable.addCell(grossAmtTable);

	//*************************************************************************		
			PdfPTable netGrossTable = new PdfPTable(2);
			int[] arrheaderwidths6A = { 50, 50 }; // percentage
			netGrossTable.getDefaultCell().setBorderWidth(1);
			netGrossTable.getDefaultCell().setPadding(1);
			netGrossTable.setWidths(arrheaderwidths6A);

			PdfPCell cellQA1 = new PdfPCell(new Phrase("Organisations Contribution ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

			cellQA1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellQA1.setBorderWidth(1);
			netGrossTable.addCell(cellQA1);

			
			PdfPCell cellQA2 = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 9, Font.BOLD))); //Payment By Bank A/C No 

			cellQA2.setBorderWidth(1);
			cellQA2.setHorizontalAlignment(Element.ALIGN_CENTER);
			netGrossTable.addCell(cellQA2);
			
			//*************************************************************************

			
			
			PdfPTable leaveTable = new PdfPTable(2);
			int[] arrheaderwidths8 = { 60, 40 }; // percentage
			leaveTable.getDefaultCell().setBorderWidth(1);
			leaveTable.getDefaultCell().setPadding(1);
			leaveTable.setWidths(arrheaderwidths8);

			PdfPTable leaveHeadTable = new PdfPTable(1);
			int[] arrheaderwidths8A = { 100 }; // percentage
			leaveHeadTable.getDefaultCell().setBorderWidth(1);
			leaveHeadTable.getDefaultCell().setPadding(1);
			leaveHeadTable.setWidths(arrheaderwidths8A);

			PdfPTable leaveAmtTable = new PdfPTable(1);
			int[] arrheaderwidths8B = { 100 }; // percentage
			leaveAmtTable.getDefaultCell().setBorderWidth(1);
			leaveAmtTable.getDefaultCell().setPadding(1);
			leaveAmtTable.setWidths(arrheaderwidths8B);

			int nCountGross = 0;
			Iterator<String> itr12 = deductionHeadName.iterator();
			while (itr12.hasNext()) {

				String strHeadNM = itr12.next();

				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Deductions") || strHeadNM.equalsIgnoreCase("Net Payable")) {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
					leaveHeadTable.addCell(cellQ2);

					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

					cellQ3.setBorderWidthTop(0);
					cellQ3.setBorderWidthBottom(0);
					cellQ3.setBorderWidthLeft(0);
					cellQ3.setBorderWidthRight(0);
					leaveAmtTable.addCell(cellQ3);
				} else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM, FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					leaveHeadTable.addCell(cellQ2);
					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));

					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

					cellQ3.setBorderWidth(0);
					leaveAmtTable.addCell(cellQ3);

				}

				nCountGross++;

			}

			leaveTable.addCell(leaveHeadTable);
			leaveTable.addCell(leaveAmtTable);

			//*************************************************************************	
			
			PdfPTable Deductiontable = new PdfPTable(2);
			int[] arrheaderwidths9 = { 50, 50 }; // percentage
			Deductiontable.getDefaultCell().setBorderWidth(1);
			Deductiontable.getDefaultCell().setPadding(1);
			Deductiontable.setWidths(arrheaderwidths9);

			PdfPCell cellQ3 = new PdfPCell(grossTitalTable);
			cellQ3.setBorderWidth(1);
			Deductiontable.addCell(cellQ3);

			PdfPCell cellR = new PdfPCell(leaveTable);
			cellR.setBorderWidth(1);
			Deductiontable.addCell(cellR);

			

			PdfPTable HeadleaveDeductionTable = new PdfPTable(2);
			int[] arrheaderwidths12 = { 50, 50 }; // percentage
			HeadleaveDeductionTable.getDefaultCell().setBorderWidth(1);
			HeadleaveDeductionTable.getDefaultCell().setPadding(1);
			HeadleaveDeductionTable.setWidths(arrheaderwidths12);

			PdfPCell celld = new PdfPCell(new PdfPCell(empTable));

			PdfPTable orgTable = new PdfPTable(3);
			int[] arrorg = { 30, 20 ,50}; // percentage
			orgTable.getDefaultCell().setBorderWidth(1);
			orgTable.getDefaultCell().setPadding(1);
			orgTable.setWidths(arrorg);

			PdfPTable orgHeadTable = new PdfPTable(1);
			int[] arrOrgA = { 100 }; // percentage
			orgHeadTable.getDefaultCell().setBorderWidth(0);
			orgHeadTable.getDefaultCell().setPadding(1);
			orgHeadTable.setWidths(arrOrgA);

			PdfPTable orgAmtTable = new PdfPTable(1);
			int[] arrOrgB = { 100 }; // percentage
			orgAmtTable.getDefaultCell().setBorderWidth(0);
			orgAmtTable.getDefaultCell().setPadding(1);
			orgAmtTable.setWidths(arrOrgB);
			
			PdfPTable orgBlank = new PdfPTable(1);
			int[] arrOrgC = { 100 }; // percentage
			orgBlank.getDefaultCell().setBorderWidth(1);
			orgBlank.getDefaultCell().setPadding(1);
			orgBlank.setWidths(arrOrgC);

			List<String> orgHeadAmt = new ArrayList<String>();
			List<String> orgHeadName = new ArrayList<String>();
			orgHeadName.add("Employer PF (O.C)");
			//orgHeadName.add("PF (O.C)");
			orgHeadName.add("Total (O.C)");
			
			orgHeadAmt.add(String.valueOf(total));
			//orgHeadAmt.add("0.00");
			orgHeadAmt.add(String.valueOf(total));
			
			int countOrg = 0;
			Iterator<String> itrOrg = orgHeadName.iterator();
			if(flagEpf){
			while (itrOrg.hasNext()) {

				String strHeadNM = itrOrg.next();

					PdfPCell cellOrg = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellOrg.setBorderWidth(0);
					orgHeadTable.addCell(cellOrg);
					
					PdfPCell cellOrg1 = new PdfPCell(new Phrase(""+ orgHeadAmt.get(countOrg), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellOrg1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellOrg1.setBorderWidth(0);
					orgAmtTable.addCell(cellOrg1);
					
					if(countOrg <2){
					PdfPCell cellOrg2 = new PdfPCell(new Phrase(" "));
					cellOrg2.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellOrg2.setBorderWidth(0);
					orgBlank.addCell(cellOrg2);
					}
					
					countOrg++;

			}
			
		}
			PdfPCell cellOrg2 = new PdfPCell(new Phrase("Remark : ",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellOrg2.setHorizontalAlignment(Element.ALIGN_LEFT);
			cellOrg2.setBorderWidth(0);
			orgBlank.addCell(cellOrg2);
			
			
			orgTable.addCell(orgHeadTable);
			orgTable.addCell(orgAmtTable);
			orgTable.addCell(orgBlank);
			
			//*************************************************************
			
			PdfPTable LastTable = new PdfPTable(1);
			LastTable.getDefaultCell().setBorderWidth(0);
			LastTable.getDefaultCell().setPadding(0);
			LastTable.setWidths(arrheaderwidths2);

			PdfPCell cellSign = new PdfPCell(new Phrase("This is Computer generated Slip so does not required any signature",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellSign.getExtraParagraphSpace();
			cellSign.setBorderWidth(1);
			cellSign.setHorizontalAlignment(Element.ALIGN_CENTER);
			LastTable.addCell(cellSign);
			
			/*PdfPTable LastTable = new PdfPTable(2);
			int[] arrNo = { 50, 50 }; // percentage
			LastTable.getDefaultCell().setBorderWidth(1);
			LastTable.getDefaultCell().setPadding(1);
			LastTable.setWidths(arrNo);

			PdfPCell cellNo = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 9, Font.BOLD))); //TOLL FREE NO:
			cellNo.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellNo.setBorderWidth(1);
			LastTable.addCell(cellNo);
			
			PdfPCell cellSign = new PdfPCell(new Phrase("This is Computer generated Slip so does not required any signature",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellSign.setBorderWidth(1);
			cellSign.setHorizontalAlignment(Element.ALIGN_CENTER);
			LastTable.addCell(cellSign);*/
			
			//************************************************************
			
			int	nyeardevfirst=nYear;
			nyeardevfirst=nYear%10;
			nYear=nYear/10;
			int	nyeardevsecond=nYear%10;
			

			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));

			imageEmpDetailTable.addCell(celld);
			document.add(LogoImage);
			document.add(titalTable);
			document.add(imageEmpDetailTable);
		//	document.add(netGrossTital);
			document.add(titleTable);
			document.add(Deductiontable);
			document.add(netGrossTable);
			document.add(HeadleaveDeductionTable);
		//	document.add(NetSalaryTable);
			document.add(orgTable);
		//	document.add(signatureTable);
			document.add(LastTable);
			document.close();
				
			
			if(isAttachment()) {
				byte[] bytes = baos.toByteArray();
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_NEW_PAYSLIP_GENERATED, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(strEmpId);
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName("PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
				nF.setEmailTemplate(true);
				nF.sendNotifications();
			} else {
				response.setContentType("application/pdf");
				response.setContentLength(baos.size());
				response.setHeader("Content-Disposition","attachment; filename=PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
				ServletOutputStream out = response.getOutputStream();              
				baos.writeTo(out);
				out.flush();
				out.close();
				baos.close();
				out.close();
				return;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	

	private void createSalarySlipPdfSixthFormat(String strEmpId, String strMonth) {
		
		Database dB = new Database();
		dB.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		con = dB.makeConnection(con);
		boolean flagEpf = false;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmTemp =null;
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpDepartmentMap = CF.getDepartmentMap(con,null, null);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
//			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con);
//			int nEmpLevelId = CF.getEmpLevelId(getStrEmpId(), request);
			
			Map<String, String> hmOrganisationDetails = new HashMap<String, String>();
			
			pst =con.prepareStatement("select max(entry_date) as entry_date, paid_from, paid_to,paid_days,total_days ,payment_mode,month,year " +
				"from payroll_generation where emp_id = ? and financial_year_from_date=? AND financial_year_to_date=? AND month=? " +
				"and paycycle=? and (bank_pay_type=? or bank_pay_type is null) group by paid_from, paid_to,paid_days,total_days,payment_mode,month,year");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(getStrPC()));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
//			System.out.println("pst 6-0 ===>> " + pst);
			rs = pst.executeQuery();
			String strEntryDate = null;
			String strPayCycleStart = null;
			String strPayCycleEnd = null;
			String strTotal_days = null;
			String strPaid_days = null;
			String strPresent_days = null;
			while(rs.next()) {
				strEntryDate = rs.getString("entry_date");
				strPayCycleStart = rs.getString("paid_from");
				strPayCycleEnd = rs.getString("paid_to");
			//===start parvez date: 04-03-2023===	
//				strTotal_days =rs.getString("total_days");
//				strPaid_days =rs.getString("paid_days");
			//===end parvez date: 04-03-2023===	
			//	strPresent_days = rs.getString("present_days");
				strPaymentMode =rs.getString("payment_mode");
				 pMonth =rs.getInt("month");
				 pYear =rs.getInt("year");
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 04-03-2023===
			pst =con.prepareStatement("select * from approve_attendance where emp_id = ? and financial_year_start=? AND financial_year_end=? and paycycle=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrPC()));
			rs = pst.executeQuery();
			while(rs.next()) {
				strTotal_days =rs.getString("total_days");
				strPaid_days =rs.getString("paid_days");
			}
			rs.close();
			pst.close();
		//===end parvez date: 04-03-2023===
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
//				"and isdisplay=true and effective_date<=? group by level_id");
			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true and " +
					"isdisplay=true and effective_date = (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
					"and isdisplay=true and effective_date<=?) group by level_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
//			System.out.println("pst ================>> " + pst);
			String strEffectiveDate = null;
			String strEmpLevelId = null; 
			rs = pst.executeQuery();
			while(rs.next()){
				strEffectiveDate = rs.getString("effective_date");
				strEmpLevelId = rs.getString("level_id");
			}
			rs.close();
			pst.close();
//			System.out.println("strEmpLevelId ===>> " + strEmpLevelId +" strEffectiveDate ===>> " + strEffectiveDate);
			
			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(strEmpLevelId));			
			
			pst = con.prepareStatement("select erpf_contribution, erps_contribution,erdli_contribution,pf_admin_charges,edli_admin_charges from emp_epf_details where emp_id = ? and financial_year_start=? and financial_year_end=? and _month =? and paycycle=? ");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(getStrPC()));
//			System.out.println("pst 6-1 ===>> " + pst);
//			System.out.println("************************pst**************"+pst);
			rs = pst.executeQuery();
			double erpf_contb = 0 ;
			double erps_contb = 0;
			double erdli_contib = 0;
			double pFadminChrges = 0;
			double edliAdminChrges = 0;
			double total = 0;
			while(rs.next()) {
				erpf_contb =rs.getDouble("erpf_contribution");
				erps_contb =rs.getDouble("erps_contribution");
				erdli_contib =rs.getDouble("erdli_contribution");
				pFadminChrges =rs.getDouble("pf_admin_charges");
				edliAdminChrges =rs.getDouble("edli_admin_charges");
			}
			
			total =erpf_contb + erps_contb + erdli_contib+pFadminChrges+edliAdminChrges;
//			System.out.println("***********************total***********************"+total);
			
		    monthName = uF.getMonth(pMonth)+" "+pYear;
		  
		//===start parvez date: 04-03-2023===    
//           tDays = uF.parseToInt(strTotal_days);
//           pDays = uF.parseToInt(strPaid_days);
           tDays = uF.parseToDouble(strTotal_days);
           pDays = uF.parseToDouble(strPaid_days);
       //===end parvez date: 04-03-2023===    
           
           unpaidDays = tDays - pDays;
           
	            
			if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("1")){
				strPaymentMode ="Bank Transfer";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cash";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cheque";	
			}
				
	            
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
		
			pst = con.prepareStatement("select * from org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while(rs.next()){
				hmOrganisationDetails.put("ORG_ID", rs.getString("org_id"));
				hmOrganisationDetails.put("ORG_LOGO", rs.getString("org_logo"));
				hmOrganisationDetails.put("ORG_NAME", rs.getString("org_name"));
				hmOrganisationDetails.put("PDF_TYPE", rs.getString("payslip_format"));
			}
				rs.close();
				pst.close();
			
			pst = con.prepareStatement("SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id  = eod.emp_id and emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map hmWorkLocationMap = CF.getWorkLocationMap(con);
				hmTemp = (Map<String, String>)hmWorkLocationMap.get(rs.getInt("wlocation_id")+"");
				if(hmTemp==null)hmTemp=new HashMap();
				strCompanyAddress = hmTemp.get("WL_ADDRESS")+" " +hmTemp.get("WL_CITY")+" " +hmTemp.get("WL_PINCODE");
				
				strLevelId = hmEmpLevelMap.get(rs.getString("emp_id"));
				
				empDetails.add(rs.getString("empcode"));
				empDetails.add(rs.getString("uan_no"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				empDetails.add(rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname"));
				empDetails.add(strTotal_days);
				empDetails.add(hmEmpDepartmentMap.get(rs.getString("depart_id")));
				empDetails.add("0.00");
				empDetails.add(hmEmpDesigMap.get(rs.getString("emp_id")));
				empDetails.add("0.00");
				empDetails.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				empDetails.add(strPaid_days);
				empDetails.add(rs.getString("emp_pan_no"));
				empDetails.add("0.00");
				
				/*empDetails.add(rs.getString("emp_pf_no"));
				empDetails.add(hmTemp.get("WL_NAME"));
				empDetails.add(rs.getString("emp_bank_name"));
				empDetails.add(uF.showData(hmEmpDepartmentMap.get(rs.getString("depart_id")), ""));
				
				empDetails.add(monthName);*/
				//strEmpImage = rs.getString("emp_image");
				
				String strEmpFName = rs.getString("emp_fname").substring(0, 1).toUpperCase()+rs.getString("emp_fname").substring(1);
				strPassword = strEmpFName+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd")+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM");
			}
			rs.close();
			pst.close();
		
			List alLoans = new ArrayList();
			Map hmEmpLoan = new HashMap();
			Map<String, String> hmEmpSalLastEffectiveDate = null; //need to implement this map
			CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, uF.getDateFormat(strPayCycleStart, DBDATE, DATE_FORMAT), uF.getDateFormat(strPayCycleEnd, DBDATE, DATE_FORMAT), hmEmpSalLastEffectiveDate);
			Map hmEmpLoanInner = (Map)hmEmpLoan.get(strEmpId); 
			if(hmEmpLoanInner==null) hmEmpLoanInner=new HashMap();
			Map<String, String> hmLoanPolicies = CF.getLoanPoliciesMap(con, uF, strOrgId);
//			String strEmpLevelId = CF.getEmpLevelId(con, getStrEmpId());

			Map<String, List<Map<String, String>>> hmPerkAlign = CF.getIndividualPerkAlignAmount(con, uF, CF, uF.parseToInt(getStrEmpId()),getStrFYS(),getStrFYE(),getStrPCS(),getStrPCE(),uF.parseToInt(getStrPC()));
			
			Map<String,String> hmsalaryAmount = getSalaryAmount(con,uF,getStrEmpId(),getStrFYS(),getStrFYE());
			
			if(hmPerkAlign == null) hmPerkAlign = new HashMap<String, List<Map<String,String>>>();
			
			/*pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg,salary_details sd where pg.emp_id =? and pg.month=? AND pg.paycycle=? " +
					"AND pg.financial_year_from_date=? AND pg.financial_year_to_date=? and sd.level_id=? and pg.salary_head_id = sd.salary_head_id " +
					"and bank_pay_type=? and (sd.is_delete is null or sd.is_delete=false) and pg.salary_head_id in (select salary_head_id " +
					"from emp_salary_details where isdisplay=true and emp_id=? and level_id=?) order by earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strEmpLevelId));
			pst.setInt(7, uF.parseToInt(getStrBankPayType()));
			pst.setInt(8, uF.parseToInt(getStrEmpId()));
			pst.setInt(9, uF.parseToInt(strEmpLevelId));*/
			
			pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND pg.financial_year_from_date=? " +
				"AND pg.financial_year_to_date=? and (bank_pay_type=? or bank_pay_type is null) and pg.salary_head_id in (select distinct(salary_head_id) as salary_head_id from " +
				"emp_salary_details where isdisplay=true and emp_id=? and level_id=?) order by earning_deduction desc,pg.salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			pst.setInt(7, uF.parseToInt(getStrEmpId()));
			pst.setInt(8, uF.parseToInt(strEmpLevelId));
//			System.out.println("pst 6-2 ===>> " + pst);
           rs=pst.executeQuery();    
          double presentDays = 0;
          while (rs.next()) {
       	   presentDays = Double.parseDouble(rs.getString("present_days"));
				Double dblTotal = 0.0;
				Double dblTotalGrossAmt = 0.0;
				
				if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
					
					dblTotal = uF.parseToDouble(hmsalaryAmount.get(rs.getString("salary_head_id"))); //OTHER ALLOWANCE/EX-
					dblTotalGrossAmt = rs.getDouble("amount");
					
					if(hmPerkAlign.containsKey(rs.getString("salary_head_id"))){
						List<Map<String, String>> alPerkAlign = hmPerkAlign.get(rs.getString("salary_head_id"));
						if(alPerkAlign == null) alPerkAlign = new ArrayList<Map<String,String>>();
						int nPerkAlignSize = alPerkAlign.size();
						for(int i = 0; i < nPerkAlignSize; i++){
							Map<String, String> hmPerkAlignInner = alPerkAlign.get(i);
							if(hmPerkAlignInner == null) hmPerkAlignInner = new HashMap<String, String>();
							salaryHeadName.add(uF.showData(hmPerkAlignInner.get("PERK_NAME"), "")+" ("+hmSalaryDetailsMap.get(rs.getString("salary_head_id"))+")");
							salHeadAmount.add(uF.parseToDouble(hmsalaryAmount.get(rs.getString("salary_head_id"))));
							salHeadAmountGross.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
						}
						
					} else{
						salaryHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						salHeadAmount.add(uF.parseToDouble(hmsalaryAmount.get(rs.getString("salary_head_id"))));
						salHeadAmountGross.add(dblTotalGrossAmt);
					}										
					
					dblTotalAmt += dblTotal;
					dblGrossTotal += dblTotalGrossAmt;

				} else if (rs.getString("earning_deduction")
						.equalsIgnoreCase("D")) {

					
					if(uF.parseToInt(rs.getString("salary_head_id"))==LOAN){
						
						for(int i=0; i<alLoans.size(); i++){
							String loan=(String)alLoans.get(i)!=null?(String)alLoans.get(i) : "";
							String loanamt=uF.showData((String)hmEmpLoanInner.get(loan), "0");
							dblTotal = uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt)));
							deductionHeadName.add(hmLoanPolicies.get((String)alLoans.get(i)));
							deductionHeadAmount.add(uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt))));
							dblTotalDeduction += dblTotal;
						}
						
						
					}else{
						dblTotal = rs.getDouble("amount");
						deductionHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						deductionHeadAmount.add(dblTotal);
						dblTotalDeduction += dblTotal;
					}
				}
				
				nYear=rs.getInt("year");
				
				int salaryHeadId =Integer.parseInt(rs.getString("salary_head_id"));
				
				if(salaryHeadId == IConstants.EMPLOYEE_EPF){
					flagEpf = true; 
				}
			}     
			rs.close();
			pst.close(); 
			
//			System.out.println("size"+salHeadAmount.size());
			salaryHeadName.add("Total");
			salHeadAmount.add(dblTotalAmt);
			deductionHeadName.add("Deductions");
			deductionHeadName.add("Net Payable");
			
			dblNetSalary = dblGrossTotal - dblTotalDeduction;
			
			deductionHeadAmount.add(dblTotalDeduction);
			deductionHeadAmount.add(dblNetSalary);
			
			salHeadAmountGross.add(dblGrossTotal);

			Double totalEarning = salHeadAmount.get(salHeadAmount.size() - 1);
	      /*  if(salaryHeadName.contains("Total")){
	        	salaryHeadName.remove(salaryHeadName.size() - 1);
	        	salHeadAmount.remove(salHeadAmount.size() - 1);
	        }*/
			
			Double totalDeduction =  deductionHeadAmount.get(deductionHeadAmount.size() - 1);
	       /* if(deductionHeadName.contains("Total Deduction")){
           deductionHeadName.remove(deductionHeadName.size() - 1);
	        deductionHeadAmount.remove(deductionHeadAmount.size() - 1);
	        }*/
	        
			pst = con.prepareStatement("select sum(leave_no) as leave_no, lar.leave_type_id from leave_application_register lar, leave_type lt where lar.leave_type_id = lt.leave_type_id and leave_id in (select leave_id from emp_leave_entry where emp_id=?) and _date between ? and ? group by lar.leave_type_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strPayCycleStart, DBDATE));
			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
//			System.out.println("pst 6-3 ===>> " + pst);
			rs = pst.executeQuery();
			
			totalLeave.add(String.valueOf(presentDays));
			String leaveNo = null ;
			int leaveType = 0;
			while(rs.next()){
				leaveNo = rs.getString("leave_no");
				leaveType = Integer.parseInt(rs.getString("leave_type_id"));
				
				leaveTypeName.add(leaveType);
				totalLeave.add(leaveNo);
				
				hmLeaveType.put(leaveTypeName, totalLeave);
			}
		
			
//			pst = con.prepareStatement("select * from emp_salary_details where emp_id = ? and effective_date = (select max(effective_date) as effective_date " +
//				"from emp_salary_details where effective_date <= ? and emp_id=? and level_id=?) and level_id=? order by salary_head_id");
//			pst = con.prepareStatement("select * from emp_salary_details where emp_id = ? and effective_date = (select max(effective_date) as effective_date " +
//			"from emp_salary_details where effective_date <= ? and emp_id=?) order by salary_head_id");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setDate(2, uF.getDateFormat(strEntryDate, DBDATE));
//			pst.setInt(3, uF.parseToInt(getStrEmpId()));
////			pst.setInt(4, uF.parseToInt(strEmpLevelId));
////			pst.setInt(5, uF.parseToInt(strEmpLevelId));
//			System.out.println("pst 6-4 ===>> " + pst);
//			rs = pst.executeQuery();
//			while(rs.next()){
//				alGross.add(rs.getString("amount"));
//			}
//			rs.close();
//			pst.close();
			
			payEmpHead.add("Emp No.");
			payEmpHead.add("UAN NO.");
			payEmpHead.add("Name");
			payEmpHead.add("Calendar Days");
			payEmpHead.add("Department ");
			payEmpHead.add("W.Offs/ Holidays");
			payEmpHead.add("Designation");
			payEmpHead.add("Leave Days");
			payEmpHead.add("Joining Dt");
			payEmpHead.add("Paid Days");
			payEmpHead.add("Pan");
			payEmpHead.add("O T Hrs");
			
			Map hmLeaveDatesType = new HashMap();
			Map<String, Map<String, String>> leaveDetailsMap = CF.getActualLeaveDates(con, CF, uF, getStrFYS(), getStrFYE(),  hmLeaveDatesType, true, null);

			Map<String, String> leaveEmpDetailsMap = leaveDetailsMap.get(getStrEmpId());

			if(leaveEmpDetailsMap!=null) {
			
			Set keys = leaveEmpDetailsMap.keySet();
					
			for (Iterator i = keys.iterator(); i.hasNext();) {
				String nkey = (String) i.next();
				String strValue = (String) leaveEmpDetailsMap.get(nkey);

				Iterator<String> itr = leaveName.iterator();	
			  int count=0;
             while(itr.hasNext()) {
           	  
           	  if(strValue.equalsIgnoreCase(itr.next())) {
           		  count=1;
           	  }
           }
			
			if(count==0) {
				leaveName.add(strValue);
			}
		}
			
			Iterator<String> itrleave = leaveName.iterator();	
			  int nleaveToatal=0;
				while(itrleave.hasNext()) {
					Set keysC = leaveEmpDetailsMap.keySet();
					String strLeave = itrleave.next();
					int nleaveCount=0;		
					for (Iterator i = keysC.iterator(); i.hasNext();) {
						String nkey = (String) i.next();
						String strValue = (String) leaveEmpDetailsMap.get(nkey);
						
						if(strValue!=null && strValue.equalsIgnoreCase(strLeave)) {
							nleaveCount++;
							nleaveToatal++;
						}
					}  
					noOfLeave.add(nleaveCount);
				}	  
				noOfLeave.add(nleaveToatal);
				leaveName.add("Total");
	}
			
			
			
			
			pst = con.prepareStatement("select sum(accrued_leaves) as accru, sum(taken_leaves) as taken_leaves, (sum(accrued_leaves) - sum(taken_leaves)) as balance, leave_type_id from leave_register where emp_id = ? group by leave_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				hmBalanceLeave.put(rs.getString("leave_type_id"), rs.getString("balance"));
			}
			rs.close();
			pst.close();
			hmLeaveNameMap = CF.getLeaveTypeMap(con);
			
			generateSalarySlipSixthFormat(getStrEmpId(),getStrMonth(), hmOrganisationDetails,strTotal_days,presentDays,dblNetSalary,totalDeduction,totalEarning,monthName,total,flagEpf);
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			dB.closeResultSet(rs);
			dB.closeStatements(pst);
			dB.closeConnection(con);
		}
	
	}

	

	


	
	private Map<String, String> getSalaryAmount(Connection con, UtilityFunctions uF, String strEmpId, String strFYS, String strFYE) {
		Map<String,String> hmAmount = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
//		String strLevelId = CF.getEmpLevelId(con, ""+strEmpId);
		String strOrgId = CF.getEmpOrgId(con, uF, ""+strEmpId);
		Map<String, List<Map<String, String>>> hmPerkAlign = new HashMap<String, List<Map<String,String>>>();
		try {
			
//			System.out.println("getStrPC ===>> " + getStrPC());
			String[] strPayCycleDate = CF.getPayCycleDatesOnPaycycleId(con, getStrPC(), strOrgId, CF.getStrTimeZone(), CF, request);
//			System.out.println("strPayCycleDate ===>> " + strPayCycleDate);
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved = true " +
//				"and isdisplay=true and effective_date <= ? group by level_id"); 
			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true and " +
					"isdisplay=true and effective_date = (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
					"and isdisplay=true and effective_date<=?) group by level_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setDate(3, uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT));
//			System.out.println("pst ================>> " + pst);
			String strEffectiveDate = null;
			String strLevelId = null;
			rs = pst.executeQuery();
			while(rs.next()){
				strEffectiveDate = rs.getString("effective_date");
				strLevelId = rs.getString("level_id");
			}
			rs.close();
			pst.close();
				
			
			sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id, salary_head_id FROM emp_salary_details " +
				"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id=? and is_approved = true " +
				"and isdisplay=true and level_id=? AND effective_date <= ?) and level_id=? AND effective_date <= ? group by salary_head_id) a, " +
				"emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id and a.salary_head_id=esd.salary_head_id and emp_id = ? AND " +
				"effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true and isdisplay=true " +
				"and level_id=? AND effective_date <= ?) and esd.level_id=? AND effective_date <= ? ) asd RIGHT JOIN salary_details sd ON " +
				"asd.salary_head_id = sd.salary_head_id WHERE sd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or " +
				"is_delete=false) and sd.earning_deduction='E' order by sd.earning_deduction desc, weight"); 
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setInt(3, uF.parseToInt(strLevelId));
			pst.setDate(4, uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(strLevelId));
	//		pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(6, uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT));
			pst.setInt(7, uF.parseToInt(strEmpId));
			pst.setInt(8, uF.parseToInt(strEmpId));
			pst.setInt(9, uF.parseToInt(strLevelId));
			pst.setDate(10, uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT));
			pst.setInt(11, uF.parseToInt(strLevelId));
	//		pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(12, uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT));
			pst.setInt(13, uF.parseToInt(strLevelId));
//			System.out.println("pst ================>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()){
				hmAmount.put(rs.getString("salary_head_id"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmAmount ===============>> " + hmAmount);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmAmount;
	}


private void generateSalarySlipSixthFormat(String strEmpId, String strMonth, Map<String, String> hmOrganisationDetails, String strTotal_days, double presentDays, Double dblNetSalary, Double totalDeduction, Double totalEarning, String monthName, double total,boolean flagEpf) {
	UtilityFunctions uF = new UtilityFunctions();

	try {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		String strCompanyLogo=hmOrganisationDetails.get("ORG_LOGO");
		String strCompanyName=hmOrganisationDetails.get("ORG_NAME");
		String orgId =hmOrganisationDetails.get("ORG_ID");
		Map<String,String> hmFeatureStatus = (Map<String,String>)request.getAttribute("hmFeatureStatus");
		
		Document document = new Document();
		PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
		if(hmFeatureStatus != null &&uF.parseToBoolean(hmFeatureStatus.get(F_PASSWORD_PROTECTED_SALARY_SLIP))){
			pdfWriter.setEncryption(strPassword.getBytes(), strPassword.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
		}
		document.open();
		document.add(new Paragraph(" "));

		String filePath = null;
		String filePathCompanyLOgo = null;
		String filePathCompanyLOgodefault=null;
		String filePathproductLogo= null;
		
		if(CF.getStrDocSaveLocation()!=null){
			filePath = CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+ strEmpId+"/"+strEmpImage;
			
			filePathCompanyLOgo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+strCompanyLogo;
			filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
			filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
		}else{
			filePath = request.getRealPath("/userImages/" +strEmpImage+ "");
			filePathCompanyLOgo = request.getRealPath("/userImages/"+strCompanyLogo+"");
			filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
			filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
		}  
		Image imageLogo=null;
		
		try{
			
			FileInputStream fileInputStream1=null;
	        File file1 = new File(filePathCompanyLOgo);
	        byte[] bFile1 = new byte[(int) file1.length()];
	        fileInputStream1 = new FileInputStream(file1);
		    fileInputStream1.read(bFile1);
		    fileInputStream1.close();
	        imageLogo = Image.getInstance(bFile1);
	        
		}catch(FileNotFoundException e){
			imageLogo = Image.getInstance(filePathCompanyLOgodefault);
		}
		
		PdfPTable photoImagetable = new PdfPTable(1);
		photoImagetable.setWidthPercentage(10);
		photoImagetable.getDefaultCell().setPadding(1);


		PdfPTable companyNameTable = new PdfPTable(1);
		int[] arrheaderwidthsh = { 100 }; // percentage
		companyNameTable.getDefaultCell().setBorderWidth(0);
		companyNameTable.setWidths(arrheaderwidthsh);
		PdfPCell companyNamecell = new PdfPCell(new Phrase(strCompanyName, FontFactory.getFont("Verdana", 14,Font.BOLD)));
		

		companyNamecell.setBorderWidthTop(0);
		companyNamecell.setBorderWidthBottom(1);
		companyNamecell.setBorderWidthLeft(0);
		companyNamecell.setBorderWidthRight(0);

		companyNamecell.setHorizontalAlignment(Element.ALIGN_CENTER);
		companyNameTable.addCell(companyNamecell);
		PdfPCell companyNamecell1 = new PdfPCell(new Phrase(strCompanyAddress, FontFactory.getFont("Verdana", 10,Font.BOLD)));

		companyNamecell1.setBorderWidthTop(0);
		companyNamecell1.setBorderWidthBottom(0);
		companyNamecell1.setBorderWidthLeft(0);
		companyNamecell1.setBorderWidthRight(0);

		companyNamecell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		companyNameTable.addCell(companyNamecell1);

		PdfPTable LogoImage = new PdfPTable(2);
		int[] arrheaderwidths1 = { 30, 70 }; // percentage
		LogoImage.getDefaultCell().setBorderWidth(1);
		LogoImage.setWidths(arrheaderwidths1);
		LogoImage.setTotalWidth(600); 
		LogoImage.addCell(imageLogo);
		LogoImage.addCell(companyNameTable);
		

		PdfPTable titalTable = new PdfPTable(1);
		int[] arrheaderwidths2 = {100}; // percentage
		titalTable.getDefaultCell().setBorderWidth(0);
		titalTable.getDefaultCell().setPadding(0);
		titalTable.setWidths(arrheaderwidths2);

		PdfPCell cellb = new PdfPCell(new Phrase("Month :  "+monthName,FontFactory.getFont("Verdana", 7, Font.BOLD)));

		cellb.getExtraParagraphSpace();
		cellb.setBorderWidth(1);
		cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
		titalTable.addCell(cellb);
		
		
		PdfPTable empTable = new PdfPTable(6);

		int[] arrheaderwidths5 = { 31, 2, 30, 15, 2, 20 }; // percentage
		empTable.getDefaultCell().setBorderWidth(1);

		empTable.setWidths(arrheaderwidths5);
		empTable.setTotalWidth(800);
		
		Iterator<String> itr = payEmpHead.iterator();
		int k = 0;
		for(; k<payEmpHead.size(); k++) {
			PdfPCell cellE = new PdfPCell(new Phrase(payEmpHead.get(k),FontFactory.getFont("Verdana", 7, Font.BOLD)));
			
			cellE.setBorderWidth(0);
			empTable.addCell(cellE);

			if(payEmpHead.get(k).isEmpty()) {
				PdfPCell cellcollan = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 7, Font.BOLD)));
				cellcollan.setBorderWidth(0);
				empTable.addCell(cellcollan);
			} else {	
				PdfPCell cellcollan = new PdfPCell(new Phrase(":",FontFactory.getFont("Verdana", 7, Font.BOLD)));
				cellcollan.setBorderWidth(0);
				empTable.addCell(cellcollan);
			}

			PdfPCell cellF = new PdfPCell(new Phrase(empDetails.get(k),FontFactory.getFont("Verdana", 7, Font.NORMAL)));
			cellF.setBorderWidth(0);
			empTable.addCell(cellF);
		}

		PdfPTable imageEmpDetailTable = new PdfPTable(1);

		int[] arrheaderwidths4 = { 100 }; // percentage

		imageEmpDetailTable.getDefaultCell().setBorderWidth(1);
		imageEmpDetailTable.getDefaultCell().setPadding(1);
		imageEmpDetailTable.setWidths(arrheaderwidths4);
		
		PdfPTable titleTable = new PdfPTable(1);
		int[] arrTitle = { 100}; // percentage
		titleTable.getDefaultCell().setBorderWidth(1);
		titleTable.getDefaultCell().setPadding(1);
		titleTable.setWidths(arrTitle);

		PdfPCell cellT = new PdfPCell(new Phrase("                                          GROSS          NET                 DEDUCTION",FontFactory.getFont("Verdana", 9, Font.BOLD)));

		cellT.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellT.setBorderWidth(1);
		titleTable.addCell(cellT);
		

		PdfPTable grossTitalTable = new PdfPTable(3);

		int[] arrheaderwidths7 = { 50,20,30 }; // percentage
		grossTitalTable.getDefaultCell().setBorderWidth(1);
		grossTitalTable.getDefaultCell().setPadding(1);
		grossTitalTable.setWidths(arrheaderwidths7);

		PdfPTable GrossHeadTable = new PdfPTable(1);
		int[] arrheaderwidths7A = { 100 }; // percentage
		GrossHeadTable.getDefaultCell().setBorderWidth(1);
		GrossHeadTable.getDefaultCell().setPadding(1);
		GrossHeadTable.setWidths(arrheaderwidths7A);

		PdfPTable grossAmtTable = new PdfPTable(1);
		int[] arrheaderwidths7B = { 100 }; // percentage
		grossAmtTable.getDefaultCell().setBorderWidth(1);
		grossAmtTable.getDefaultCell().setPadding(1);
		grossAmtTable.setWidths(arrheaderwidths7B);
		
		PdfPTable tableScale = new PdfPTable(1);
		int[] arrheaderwidths7C = { 100 }; // percentage
		tableScale.getDefaultCell().setBorderWidth(1);
		tableScale.getDefaultCell().setPadding(1);
		tableScale.setWidths(arrheaderwidths7C);

		int nCount = 0;
		Iterator<String> itr1 = salaryHeadName.iterator();
		while (itr1.hasNext()) {

			String strHeadNM = itr1.next();

			if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
				
				PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM, FontFactory.getFont("Verdana", 8, Font.BOLD)));
				cellQ2.setBorderWidth(0);
				GrossHeadTable.addCell(cellQ2);
				
				PdfPCell cellQS = new PdfPCell(new Phrase(""+uF.formatIntoTwoDecimal(salHeadAmount.get(nCount)),FontFactory.getFont("Verdana", 8, Font.BOLD))); 
				cellQS.setBorderWidthTop(0);
				cellQS.setBorderWidthBottom(0);
				cellQS.setBorderWidthLeft(0);
				cellQS.setBorderWidthRight(0);
				cellQS.setHorizontalAlignment(Element.ALIGN_RIGHT);
				tableScale.addCell(cellQS);

				PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

				cellQ3.setBorderWidthTop(0);
				cellQ3.setBorderWidthBottom(0);
				cellQ3.setBorderWidthLeft(0);
				cellQ3.setBorderWidthRight(0);
				cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				grossAmtTable.addCell(cellQ3);

			} else {

				PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQ2.setBorderWidth(0);
				GrossHeadTable.addCell(cellQ2);
				
				PdfPCell cellQS = new PdfPCell(new Phrase(""+uF.formatIntoTwoDecimal(salHeadAmount.get(nCount)),FontFactory.getFont("Verdana", 8, Font.BOLD))); 
				cellQS.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellQS.setBorderWidth(0);
				tableScale.addCell(cellQS);
				
				PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellQ3.setBorderWidth(0);
				grossAmtTable.addCell(cellQ3);
			}

			nCount++;

		}

		grossTitalTable.addCell(GrossHeadTable);
		grossTitalTable.addCell(tableScale);
		grossTitalTable.addCell(grossAmtTable);

//*************************************************************************		
		PdfPTable netGrossTable = new PdfPTable(2);
		int[] arrheaderwidths6A = { 50, 50 }; // percentage
		netGrossTable.getDefaultCell().setBorderWidth(1);
		netGrossTable.getDefaultCell().setPadding(1);
		netGrossTable.setWidths(arrheaderwidths6A);

		PdfPCell cellQA1 = new PdfPCell(new Phrase("Organisations Contribution ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

		cellQA1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellQA1.setBorderWidth(1);
		netGrossTable.addCell(cellQA1);

		
		PdfPCell cellQA2 = new PdfPCell(new Phrase("Payment By Bank A/C No ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

		cellQA2.setBorderWidth(1);
		cellQA2.setHorizontalAlignment(Element.ALIGN_CENTER);
		netGrossTable.addCell(cellQA2);
		
		//*************************************************************************

		
		
		PdfPTable leaveTable = new PdfPTable(2);
		int[] arrheaderwidths8 = { 60, 40 }; // percentage
		leaveTable.getDefaultCell().setBorderWidth(1);
		leaveTable.getDefaultCell().setPadding(1);
		leaveTable.setWidths(arrheaderwidths8);

		PdfPTable leaveHeadTable = new PdfPTable(1);
		int[] arrheaderwidths8A = { 100 }; // percentage
		leaveHeadTable.getDefaultCell().setBorderWidth(1);
		leaveHeadTable.getDefaultCell().setPadding(1);
		leaveHeadTable.setWidths(arrheaderwidths8A);

		PdfPTable leaveAmtTable = new PdfPTable(1);
		int[] arrheaderwidths8B = { 100 }; // percentage
		leaveAmtTable.getDefaultCell().setBorderWidth(1);
		leaveAmtTable.getDefaultCell().setPadding(1);
		leaveAmtTable.setWidths(arrheaderwidths8B);

		int nCountGross = 0;
		Iterator<String> itr12 = deductionHeadName.iterator();
		while (itr12.hasNext()) {

			String strHeadNM = itr12.next();

			if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Deductions") || strHeadNM.equalsIgnoreCase("Net Payable")) {

				PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
				cellQ2.setBorderWidth(0);
				leaveHeadTable.addCell(cellQ2);

				PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

				cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

				cellQ3.setBorderWidthTop(0);
				cellQ3.setBorderWidthBottom(0);
				cellQ3.setBorderWidthLeft(0);
				cellQ3.setBorderWidthRight(0);
				leaveAmtTable.addCell(cellQ3);
			} else {

				PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM, FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQ2.setBorderWidth(0);
				leaveHeadTable.addCell(cellQ2);
				PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));

				cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

				cellQ3.setBorderWidth(0);
				leaveAmtTable.addCell(cellQ3);

			}

			nCountGross++;

		}

		leaveTable.addCell(leaveHeadTable);
		leaveTable.addCell(leaveAmtTable);

		//*************************************************************************	
		
		PdfPTable Deductiontable = new PdfPTable(2);
		int[] arrheaderwidths9 = { 50, 50 }; // percentage
		Deductiontable.getDefaultCell().setBorderWidth(1);
		Deductiontable.getDefaultCell().setPadding(1);
		Deductiontable.setWidths(arrheaderwidths9);

		PdfPCell cellQ3 = new PdfPCell(grossTitalTable);
		cellQ3.setBorderWidth(1);
		Deductiontable.addCell(cellQ3);

		PdfPCell cellR = new PdfPCell(leaveTable);
		cellR.setBorderWidth(1);
		Deductiontable.addCell(cellR);

		

		PdfPTable HeadleaveDeductionTable = new PdfPTable(2);
		int[] arrheaderwidths12 = { 50, 50 }; // percentage
		HeadleaveDeductionTable.getDefaultCell().setBorderWidth(1);
		HeadleaveDeductionTable.getDefaultCell().setPadding(1);
		HeadleaveDeductionTable.setWidths(arrheaderwidths12);

		PdfPCell celld = new PdfPCell(new PdfPCell(empTable));

		PdfPTable orgTable = new PdfPTable(3);
		int[] arrorg = { 30, 20 ,50}; // percentage
		orgTable.getDefaultCell().setBorderWidth(1);
		orgTable.getDefaultCell().setPadding(1);
		orgTable.setWidths(arrorg);

		PdfPTable orgHeadTable = new PdfPTable(1);
		int[] arrOrgA = { 100 }; // percentage
		orgHeadTable.getDefaultCell().setBorderWidth(0);
		orgHeadTable.getDefaultCell().setPadding(1);
		orgHeadTable.setWidths(arrOrgA);

		PdfPTable orgAmtTable = new PdfPTable(1);
		int[] arrOrgB = { 100 }; // percentage
		orgAmtTable.getDefaultCell().setBorderWidth(0);
		orgAmtTable.getDefaultCell().setPadding(1);
		orgAmtTable.setWidths(arrOrgB);
		
		PdfPTable orgBlank = new PdfPTable(1);
		int[] arrOrgC = { 100 }; // percentage
		orgBlank.getDefaultCell().setBorderWidth(1);
		orgBlank.getDefaultCell().setPadding(1);
		orgBlank.setWidths(arrOrgC);

		List<String> orgHeadAmt = new ArrayList<String>();
		List<String> orgHeadName = new ArrayList<String>();
		orgHeadName.add("Employer PF (O.C)");
		//orgHeadName.add("PF (O.C)");
		orgHeadName.add("Total (O.C)");
		
		orgHeadAmt.add(String.valueOf(total));
		//orgHeadAmt.add("0.00");
		orgHeadAmt.add(String.valueOf(total));
		
		int countOrg = 0;
		Iterator<String> itrOrg = orgHeadName.iterator();
		if(flagEpf){
		while (itrOrg.hasNext()) {
			    String strHeadNM = itrOrg.next();
				PdfPCell cellOrg = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellOrg.setBorderWidth(0);
				orgHeadTable.addCell(cellOrg);
				
				PdfPCell cellOrg1 = new PdfPCell(new Phrase(""+ orgHeadAmt.get(countOrg), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellOrg1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellOrg1.setBorderWidth(0);
				orgAmtTable.addCell(cellOrg1);
				
				if(countOrg <2){
				PdfPCell cellOrg2 = new PdfPCell(new Phrase(" "));
				cellOrg2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellOrg2.setBorderWidth(0);
				orgBlank.addCell(cellOrg2);
				}
				
				countOrg++;

		}
		
	}
		PdfPCell cellOrg2 = new PdfPCell(new Phrase("Remark : ",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
		cellOrg2.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellOrg2.setBorderWidth(0);
		orgBlank.addCell(cellOrg2);
		
		
		orgTable.addCell(orgHeadTable);
		orgTable.addCell(orgAmtTable);
		orgTable.addCell(orgBlank);
		
		//*************************************************************
		
			PdfPTable bottomTable = new PdfPTable(1);
			int[] arrbottomheaderwidths = { 100 }; // percentage
			titalTable.getDefaultCell().setBorderWidth(1);
			titalTable.getDefaultCell().setPadding(1);
			titalTable.setWidths(arrbottomheaderwidths);

			PdfPCell cellbottom = new PdfPCell(new Phrase("This is Computer generated Slip so does not required any signature",FontFactory.getFont("Verdana", 8, Font.BOLD)));

			cellb.getExtraParagraphSpace();
			cellb.setBorderWidth(1);
			cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
			bottomTable.addCell(cellbottom);
		
		//************************************************************
		
		int	nyeardevfirst=nYear;
		nyeardevfirst=nYear%10;
		nYear=nYear/10;
		int	nyeardevsecond=nYear%10;
		

		document.add(new Paragraph(" "));
		document.add(new Paragraph(" "));

		imageEmpDetailTable.addCell(celld);
		document.add(LogoImage);
		document.add(titalTable);
		document.add(imageEmpDetailTable);
	//	document.add(netGrossTital);
		document.add(titleTable);
		document.add(Deductiontable);
		//document.add(netGrossTable);
		document.add(HeadleaveDeductionTable);
	//	document.add(NetSalaryTable);
		//document.add(orgTable);
	//	document.add(signatureTable);
		document.add(bottomTable);
		document.close();
			
		
		if(isAttachment()) {
			byte[] bytes = baos.toByteArray();
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(N_NEW_PAYSLIP_GENERATED, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setStrEmpId(strEmpId);
			nF.setPdfData(bytes);
			nF.setStrAttachmentFileName("PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
			nF.setEmailTemplate(true);
			nF.sendNotifications();
		} else {
			response.setContentType("application/pdf");
			response.setContentLength(baos.size());
			response.setHeader("Content-Disposition","attachment; filename=PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
			ServletOutputStream out = response.getOutputStream();              
			baos.writeTo(out);
			out.flush();
			out.close();
			baos.close();
			out.close();
			return;
		}
		
	}catch (Exception e) {
		e.printStackTrace();
	}

}
	
	
	
	
	private void createSalarySlipPdfFifthFormat(String strEmpId, String strMonth) {
		
		Database dB = new Database();
		dB.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		con = dB.makeConnection(con);
		boolean flagEpf = false;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmTemp =null;
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
//			Map<String, String> hmEmpDepartmentMap = CF.getDepartmentMap(con,null, null);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
//			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con);
			Map<String, String> hmBankNameMap = CF.getBankNameMap(con, uF);
			int nEmpLevelId = CF.getEmpLevelId(getStrEmpId(), request);
			
			Map<String, String> hmOrganisationDetails = new HashMap<String, String>();
			
			pst =con.prepareStatement("select max(entry_date) as entry_date, paid_from, paid_to,paid_days,total_days ,payment_mode,month,year " +
					"from payroll_generation where emp_id = ? and financial_year_from_date=? AND financial_year_to_date=? AND month=? " +
					"and paycycle=? and (bank_pay_type=? or bank_pay_type is null) group by paid_from, paid_to,paid_days,total_days,payment_mode,month,year");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(getStrPC()));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
//			System.out.println("pst 5-1 ===>> " + pst);
			rs = pst.executeQuery();
			String strEntryDate = null;
			String strPayCycleStart = null;
			String strPayCycleEnd = null;
			String strTotal_days =null;
			String strPaid_days = null;
			String strPresent_days = null;
			while(rs.next()){
				strEntryDate = rs.getString("entry_date");
				strPayCycleStart = rs.getString("paid_from");
				strPayCycleEnd = rs.getString("paid_to");
			//===start parvez date: 04-03-2023===	
//				strTotal_days =rs.getString("total_days");
//				strPaid_days =rs.getString("paid_days");
			//===end parvez date: 04-03-2023===	
			//	strPresent_days = rs.getString("present_days");
				strPaymentMode =rs.getString("payment_mode");
				 pMonth =rs.getInt("month");
				 pYear =rs.getInt("year");
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 04-03-2023===
			pst =con.prepareStatement("select * from approve_attendance where emp_id = ? and financial_year_start=? AND financial_year_end=? and paycycle=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrPC()));
			rs = pst.executeQuery();
			while(rs.next()) {
				strTotal_days =rs.getString("total_days");
				strPaid_days =rs.getString("paid_days");
			}
			rs.close();
			pst.close();
		//===end parvez date: 04-03-2023===
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
//				"and isdisplay=true and effective_date<=? group by level_id"); 
			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true and " +
					"isdisplay=true and effective_date = (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
					"and isdisplay=true and effective_date<=?) group by level_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
//			System.out.println("pst ================>> " + pst);
			String strEffectiveDate = null;
			String strEmpLevelId = null;
			rs = pst.executeQuery();
			while(rs.next()){
				strEffectiveDate = rs.getString("effective_date");
				strEmpLevelId = rs.getString("level_id");
			}
			rs.close();
			pst.close();
//			System.out.println("strEmpLevelId ===>> " + strEmpLevelId);
			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(strEmpLevelId));			
			
			
			pst =con.prepareStatement("select erpf_contribution, erps_contribution,erdli_contribution,pf_admin_charges,edli_admin_charges from emp_epf_details where emp_id = ? and financial_year_start=? and financial_year_end=? and _month =? and paycycle=? ");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(getStrPC()));
//			System.out.println("====>pst"+pst);
//			System.out.println("************************pst**************"+pst);
			rs = pst.executeQuery();
			double erpf_contb = 0 ;
			double erps_contb = 0;
			double erdli_contib = 0;
			double pFadminChrges = 0;
			double edliAdminChrges = 0;
			double total = 0;
			while(rs.next()) {
				erpf_contb =rs.getDouble("erpf_contribution");
				erps_contb =rs.getDouble("erps_contribution");
				erdli_contib =rs.getDouble("erdli_contribution");
				pFadminChrges =rs.getDouble("pf_admin_charges");
				edliAdminChrges =rs.getDouble("edli_admin_charges");
			}
			rs.close();
			pst.close();
			
			total =erpf_contb + erps_contb + erdli_contib+pFadminChrges+edliAdminChrges;
//			System.out.println("===>Total"+total);
//			System.out.println("***********************total***********************"+total);
			
		    monthName = uF.getMonth(pMonth)+" "+pYear;
		
	//===start parvez date: 04-03-2023===	    
//           tDays = uF.parseToInt(strTotal_days);
//           pDays = uF.parseToInt(strPaid_days);
           tDays = uF.parseToDouble(strTotal_days);
           pDays = uF.parseToDouble(strPaid_days);
    //===end parvez date: 04-03-2023===       
           
           unpaidDays = tDays - pDays;
           
	            
			if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("1")){
				strPaymentMode ="Bank Transfer";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cash";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cheque";	
			}
				
	            
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
		
			pst = con.prepareStatement("select * from org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while(rs.next()){
				hmOrganisationDetails.put("ORG_ID", rs.getString("org_id"));
				hmOrganisationDetails.put("ORG_LOGO", rs.getString("org_logo"));
				hmOrganisationDetails.put("ORG_NAME", rs.getString("org_name"));
				hmOrganisationDetails.put("PDF_TYPE", rs.getString("payslip_format"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id  = eod.emp_id and emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map hmWorkLocationMap = CF.getWorkLocationMap(con);
				hmTemp = (Map<String, String>)hmWorkLocationMap.get(rs.getInt("wlocation_id")+"");
				if(hmTemp==null)hmTemp=new HashMap();
				strCompanyAddress = hmTemp.get("WL_ADDRESS")+" " +hmTemp.get("WL_CITY")+" " +hmTemp.get("WL_PINCODE");
				
				strLevelId = hmEmpLevelMap.get(rs.getString("emp_id"));
				
				empDetails.add(rs.getString("empcode"));
				empDetails.add(" ");
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				empDetails.add(rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname"));
				empDetails.add(strTotal_days);
				empDetails.add(uF.showData(hmBankNameMap.get(rs.getString("emp_bank_name")), "-"));
//				empDetails.add(hmEmpDepartmentMap.get(rs.getString("depart_id")));
				empDetails.add(uF.showData(rs.getString("emp_bank_acct_nbr"), "-"));
//				empDetails.add("0.00");
				empDetails.add(hmEmpDesigMap.get(rs.getString("emp_id")));
				empDetails.add("0.00");
				empDetails.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				empDetails.add(strPaid_days);
				empDetails.add(uF.showData(rs.getString("emp_pan_no"), "-"));
				empDetails.add(uF.showData(rs.getString("emp_pf_no"), "-"));
//				empDetails.add("0.00");
				
				/*empDetails.add(rs.getString("emp_pf_no"));
				empDetails.add(hmTemp.get("WL_NAME"));
				empDetails.add(rs.getString("emp_bank_name"));
				empDetails.add(uF.showData(hmEmpDepartmentMap.get(rs.getString("depart_id")), ""));
				
				empDetails.add(monthName);*/
				//strEmpImage = rs.getString("emp_image");
				
				String strEmpFName = rs.getString("emp_fname").substring(0, 1).toUpperCase()+rs.getString("emp_fname").substring(1);
				strPassword = strEmpFName+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd")+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM");
			}
			rs.close();
			pst.close();
		
			List alLoans = new ArrayList();
			Map hmEmpLoan = new HashMap();
			Map<String, String> hmEmpSalLastEffectiveDate = null; //need to implement this map
			CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, uF.getDateFormat(strPayCycleStart, DBDATE, DATE_FORMAT), uF.getDateFormat(strPayCycleEnd, DBDATE, DATE_FORMAT), hmEmpSalLastEffectiveDate);
			Map hmEmpLoanInner = (Map)hmEmpLoan.get(strEmpId); 
			if(hmEmpLoanInner==null) hmEmpLoanInner=new HashMap();
			Map<String, String> hmLoanPolicies = CF.getLoanPoliciesMap(con, uF, strOrgId);
//			String strEmpLevelId = CF.getEmpLevelId(con, getStrEmpId());

			Map<String, List<Map<String, String>>> hmPerkAlign = CF.getIndividualPerkAlignAmount(con, uF, CF, uF.parseToInt(getStrEmpId()),getStrFYS(),getStrFYE(),getStrPCS(),getStrPCE(),uF.parseToInt(getStrPC()));
			if(hmPerkAlign == null) hmPerkAlign = new HashMap<String, List<Map<String,String>>>();
			
			/*pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg,salary_details sd where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND pg.financial_year_from_date=? " +
				"AND pg.financial_year_to_date=? and sd.level_id=? and pg.salary_head_id = sd.salary_head_id and bank_pay_type=? and (sd.is_delete is null or sd.is_delete=false) " +
				"and pg.salary_head_id in (select salary_head_id from emp_salary_details where isdisplay=true and emp_id=? and level_id=?) order by earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strEmpLevelId));
			pst.setInt(7, uF.parseToInt(getStrBankPayType()));
			pst.setInt(8, uF.parseToInt(getStrEmpId()));
			pst.setInt(9, uF.parseToInt(strEmpLevelId));*/
			
			pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND pg.financial_year_from_date=? " +
				"AND pg.financial_year_to_date=? and (bank_pay_type=? or bank_pay_type is null) and pg.salary_head_id in (select distinct(salary_head_id) as salary_head_id from " +
				"emp_salary_details where isdisplay=true and emp_id=? and level_id=?) order by earning_deduction desc,pg.salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			pst.setInt(7, uF.parseToInt(getStrEmpId()));
			pst.setInt(8, uF.parseToInt(strEmpLevelId));
//			System.out.println("pst 5-2 ===>> " + pst);
           rs=pst.executeQuery();    
          double presentDays = 0;
          while (rs.next()) {
       	   presentDays = Double.parseDouble(rs.getString("present_days"));
				Double dblTotal = 0.0;
				Double dblTotalGrossAmt = 0.0;
				if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
					dblTotal = rs.getDouble("amount"); //OTHER ALLOWANCE/EX-
					dblTotalGrossAmt = rs.getDouble("amount");
					
					if(hmPerkAlign.containsKey(rs.getString("salary_head_id"))){
						List<Map<String, String>> alPerkAlign = hmPerkAlign.get(rs.getString("salary_head_id"));
						if(alPerkAlign == null) alPerkAlign = new ArrayList<Map<String,String>>();
						int nPerkAlignSize = alPerkAlign.size();
						for(int i = 0; i < nPerkAlignSize; i++) {
							Map<String, String> hmPerkAlignInner = alPerkAlign.get(i);
							if(hmPerkAlignInner == null) hmPerkAlignInner = new HashMap<String, String>();
							salaryHeadName.add(uF.showData(hmPerkAlignInner.get("PERK_NAME"), "")+" ("+hmSalaryDetailsMap.get(rs.getString("salary_head_id"))+")");
							salHeadAmount.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
							salHeadAmountGross.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
						}
						
					} else {
						salaryHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						salHeadAmount.add(dblTotal);
						salHeadAmountGross.add(dblTotalGrossAmt);
					}										
					
					dblTotalAmt += dblTotal;
					dblGrossTotal += dblTotalGrossAmt;

				} else if (rs.getString("earning_deduction").equalsIgnoreCase("D")) {
					
					if(uF.parseToInt(rs.getString("salary_head_id"))==LOAN){
						for(int i=0; i<alLoans.size(); i++){
							String loan=(String)alLoans.get(i)!=null?(String)alLoans.get(i) : "";
							String loanamt=uF.showData((String)hmEmpLoanInner.get(loan), "0");
							dblTotal = uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt)));
							deductionHeadName.add(hmLoanPolicies.get((String)alLoans.get(i)));
							deductionHeadAmount.add(uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt))));
							dblTotalDeduction += dblTotal;
						}
						
					}else{
						dblTotal = rs.getDouble("amount");
						deductionHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						deductionHeadAmount.add(dblTotal);
						dblTotalDeduction += dblTotal;
					}
					
				}
				
				nYear=rs.getInt("year");
				int salaryHeadId =Integer.parseInt(rs.getString("salary_head_id"));
				if(salaryHeadId == IConstants.EMPLOYEE_EPF){
					flagEpf = true; 
				}
			}     
			rs.close();
			pst.close(); 
//			System.out.println("salaryHeadName ===>> " + salaryHeadName);
			
			salaryHeadName.add("Total");
			salHeadAmount.add(dblTotalAmt);
			deductionHeadName.add("Deductions");
			deductionHeadName.add("Net Payable");
			
			dblNetSalary = dblTotalAmt - dblTotalDeduction;
			
			deductionHeadAmount.add(dblTotalDeduction);
			deductionHeadAmount.add(dblNetSalary);
			
			salHeadAmountGross.add(dblGrossTotal);

			Double totalEarning = salHeadAmount.get(salHeadAmount.size() - 1);
	      /*  if(salaryHeadName.contains("Total")){
	        	salaryHeadName.remove(salaryHeadName.size() - 1);
	        	salHeadAmount.remove(salHeadAmount.size() - 1);
	        }*/
			
			Double totalDeduction =  deductionHeadAmount.get(deductionHeadAmount.size() - 1);
	       /* if(deductionHeadName.contains("Total Deduction")){
           deductionHeadName.remove(deductionHeadName.size() - 1);
	        deductionHeadAmount.remove(deductionHeadAmount.size() - 1);
	        }*/
	        
			pst = con.prepareStatement("select sum(leave_no) as leave_no, lar.leave_type_id from leave_application_register lar, leave_type lt where lar.leave_type_id = lt.leave_type_id and leave_id in (select leave_id from emp_leave_entry where emp_id=?) and _date between ? and ? group by lar.leave_type_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strPayCycleStart, DBDATE));
			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
			rs = pst.executeQuery();
			totalLeave.add(String.valueOf(presentDays));
			String leaveNo = null ;
			int leaveType = 0;
			while(rs.next()){
				leaveNo = rs.getString("leave_no");
				leaveType = Integer.parseInt(rs.getString("leave_type_id"));
				
				leaveTypeName.add(leaveType);
				totalLeave.add(leaveNo);
				
				hmLeaveType.put(leaveTypeName, totalLeave);
			
			}
		
			
//			pst = con.prepareStatement("select * from emp_salary_details where emp_id=? and effective_date = (select max(effective_date) as effective_date " +
//					"from emp_salary_details where effective_date <= ? and emp_id=? and level_id=?) and level_id=? order by salary_head_id");
//			pst = con.prepareStatement("select * from emp_salary_details where emp_id=? and effective_date = (select max(effective_date) as effective_date " +
//			"from emp_salary_details where effective_date <= ? and emp_id=? ) order by salary_head_id");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setDate(2, uF.getDateFormat(strEntryDate, DBDATE));
//			pst.setInt(3, uF.parseToInt(getStrEmpId()));
////			pst.setInt(4, uF.parseToInt(strEmpLevelId));
////			pst.setInt(5, uF.parseToInt(strEmpLevelId));
//			System.out.println("pst 5-3 ===>> " + pst);
//			rs = pst.executeQuery();
//			while(rs.next()){
//				alGross.add(rs.getString("amount"));
//			}
//			rs.close();
//			pst.close();
			
			payEmpHead.add("Emp No.");
			payEmpHead.add("");
			payEmpHead.add("Name");
			payEmpHead.add("Calendar Days");
			payEmpHead.add("Bank Name ");
//			payEmpHead.add("Department ");
			payEmpHead.add("A/C No.");
//			payEmpHead.add("W.Offs/ Holidays");
			payEmpHead.add("Designation");
			payEmpHead.add("Leave Days");
			payEmpHead.add("Joining Dt");
			payEmpHead.add("Paid Days");
			payEmpHead.add("Pan");
			payEmpHead.add("PF No.");
//			payEmpHead.add("OT Hrs");
			
			Map hmLeaveDatesType = new HashMap();
			Map<String, Map<String, String>> leaveDetailsMap = CF.getActualLeaveDates(con, CF, uF, getStrFYS(), getStrFYE(),  hmLeaveDatesType, true, null);
			Map<String, String> leaveEmpDetailsMap = leaveDetailsMap.get(getStrEmpId());

			if(leaveEmpDetailsMap!=null) {
			Set keys = leaveEmpDetailsMap.keySet();
			for (Iterator i = keys.iterator(); i.hasNext();) {
				String nkey = (String) i.next();
				String strValue = (String) leaveEmpDetailsMap.get(nkey);

				Iterator<String> itr = leaveName.iterator();	
			  int count=0;
             while(itr.hasNext()) {
           	  if(strValue.equalsIgnoreCase(itr.next())) {
           		  count=1;
           	  }
           }
			
			if(count==0) {
				leaveName.add(strValue);
			}
		}
			
			Iterator<String> itrleave = leaveName.iterator();	
			  int nleaveToatal=0;
				while(itrleave.hasNext()) {				
					Set keysC = leaveEmpDetailsMap.keySet();
					String strLeave = itrleave.next();
					int nleaveCount=0;		
					
					for (Iterator i = keysC.iterator(); i.hasNext();) {
						String nkey = (String) i.next();
						String strValue = (String) leaveEmpDetailsMap.get(nkey);
						
						if(strValue!=null && strValue.equalsIgnoreCase(strLeave)) {
							nleaveCount++;
							
							nleaveToatal++;
						}
					}  
					noOfLeave.add(nleaveCount);
				}	  
				
				noOfLeave.add(nleaveToatal);
				leaveName.add("Total");
	}
			
			
			
			
			pst = con.prepareStatement("select sum(accrued_leaves) as accru, sum(taken_leaves) as taken_leaves, (sum(accrued_leaves) - sum(taken_leaves)) as balance, leave_type_id from leave_register where emp_id = ? group by leave_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				hmBalanceLeave.put(rs.getString("leave_type_id"), rs.getString("balance"));
			}
			rs.close();
			pst.close();
			hmLeaveNameMap = CF.getLeaveTypeMap(con);
//			System.out.println("Total===>"+total);
			generateSalarySlipFifththFormat(getStrEmpId(),getStrMonth(), hmOrganisationDetails,strTotal_days,presentDays,dblNetSalary,totalDeduction,totalEarning,monthName,total,flagEpf);
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			dB.closeResultSet(rs);
			dB.closeStatements(pst);
			dB.closeConnection(con);
		}
	
	}


	private int PdfFormat() {
		UtilityFunctions uF = new UtilityFunctions();
		int type = 0;
		try {
			
			Database dB = new Database();
			dB.setRequest(request);
			PreparedStatement pst = null;
			ResultSet rs = null;
			Connection con = null;
			con = dB.makeConnection(con);
			Map<String, String> hmOrganisationDetails = new HashMap<String, String>();
				
	        String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			
			pst = con.prepareStatement("select * from org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(strOrgId));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
	
			while(rs.next()){
				hmOrganisationDetails.put("ORG_ID", rs.getString("org_id"));
				hmOrganisationDetails.put("ORG_LOGO", rs.getString("org_logo"));
				hmOrganisationDetails.put("ORG_NAME", rs.getString("org_name"));
				hmOrganisationDetails.put("PDF_TYPE", rs.getString("payslip_format"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmOrganisationDetails ===>> " + hmOrganisationDetails);
			
		  type = uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"));
			
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return type;
		
	} 


	private void createSalarySlipPdfFourthFormat(String strEmpId2, String strMonth2) {

		
		Database dB = new Database();
		dB.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = dB.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmTemp =null;
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpDepartmentMap = CF.getDepartmentMap(con,null, null);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
//			Map<String, String> hmLevelMap = CF.getLevelMap(con);
//			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			
//			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con);
//			int nEmpLevelId = CF.getEmpLevelId(getStrEmpId(), request);
			
			Map<String, String> hmOrganisationDetails = new HashMap<String, String>();
			
			pst =con.prepareStatement("select max(entry_date) as entry_date, paid_from, paid_to,paid_days,total_days ,payment_mode,month,year " +
					"from payroll_generation where emp_id = ? and financial_year_from_date=? AND financial_year_to_date=? AND month=? " +
					"and paycycle=? and (bank_pay_type=? or bank_pay_type is null) group by paid_from, paid_to,paid_days,total_days,payment_mode,month,year");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(getStrPC()));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			rs = pst.executeQuery();
			String strEntryDate = null;
			String strPayCycleStart = null;
			String strPayCycleEnd = null;
			String strTotal_days =null;
			String strPaid_days = null;
			String strPresent_days = null;
			while(rs.next()){
				strEntryDate = rs.getString("entry_date");
				strPayCycleStart = rs.getString("paid_from");
				strPayCycleEnd = rs.getString("paid_to");
			//===start parvez date: 04-03-2023===	
//				strTotal_days =rs.getString("total_days");
//				strPaid_days =rs.getString("paid_days");
			//===end parvez date: 04-03-2023===	
			//	strPresent_days = rs.getString("present_days");
				strPaymentMode =rs.getString("payment_mode");
				 pMonth =rs.getInt("month");
				 pYear =rs.getInt("year");
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 04-03-2023===
			pst =con.prepareStatement("select * from approve_attendance where emp_id = ? and financial_year_start=? AND financial_year_end=? and paycycle=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrPC()));
			rs = pst.executeQuery();
			while(rs.next()) {
				strTotal_days =rs.getString("total_days");
				strPaid_days =rs.getString("paid_days");
			}
			rs.close();
			pst.close();
		//===end parvez date: 04-03-2023===
			
		    monthName = uF.getMonth(pMonth)+" "+pYear;
		
		//===start parvez date: 04-03-2023===    
//           tDays = uF.parseToInt(strTotal_days);
//           pDays = uF.parseToInt(strPaid_days);
           tDays = uF.parseToDouble(strTotal_days);
           pDays = uF.parseToDouble(strPaid_days);
       //===end parvez date: 04-03-2023===    
           
           unpaidDays = tDays - pDays;
           
	            
			if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("1")){
				strPaymentMode ="Bank Transfer";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cash";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cheque";	
			}
				
	            
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
		
			pst = con.prepareStatement("select * from org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
	
			while(rs.next()){
				hmOrganisationDetails.put("ORG_ID", rs.getString("org_id"));
				hmOrganisationDetails.put("ORG_LOGO", rs.getString("org_logo"));
				hmOrganisationDetails.put("ORG_NAME", rs.getString("org_name"));
				hmOrganisationDetails.put("PDF_TYPE", rs.getString("payslip_format"));
			}
				rs.close();
				pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
//				"and isdisplay=true and effective_date<=? group by level_id"); 
			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true and " +
					"isdisplay=true and effective_date = (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
					"and isdisplay=true and effective_date<=?) group by level_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
//				System.out.println("pst ================>> " + pst);
			String strEffectiveDate = null;
			String strEmpLevelId = null;
			rs = pst.executeQuery();
			while(rs.next()){
				strEffectiveDate = rs.getString("effective_date");
				strEmpLevelId = rs.getString("level_id");
			}
			rs.close();
			pst.close();
//			System.out.println("strEmpLevelId ===>> " + strEmpLevelId);
			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(strEmpLevelId));
			
			
			pst = con.prepareStatement("SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id  = eod.emp_id and emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map hmWorkLocationMap = CF.getWorkLocationMap(con);
				hmTemp = (Map<String, String>)hmWorkLocationMap.get(rs.getInt("wlocation_id")+"");
				if(hmTemp==null)hmTemp=new HashMap();
				strCompanyAddress = hmTemp.get("WL_ADDRESS")+" " +hmTemp.get("WL_CITY")+" " +hmTemp.get("WL_PINCODE");
				
				strLevelId = hmEmpLevelMap.get(rs.getString("emp_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}

				empDetails.add(rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname"));
				empDetails.add(hmEmpDesigMap.get(rs.getString("emp_id")));
				empDetails.add(rs.getString("empcode"));
				
				empDetails.add(rs.getString("emp_pf_no"));
				empDetails.add(hmTemp.get("WL_NAME"));
				empDetails.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				empDetails.add(hmEmpDepartmentMap.get(rs.getString("depart_id")));
				empDetails.add(rs.getString("emp_pan_no"));
				
				empDetails.add(rs.getString("emp_bank_name"));
				empDetails.add(uF.showData(hmEmpDepartmentMap.get(rs.getString("depart_id")), ""));
				
				empDetails.add(monthName);
				strEmpImage = rs.getString("emp_image");
			
				String strEmpFName = rs.getString("emp_fname").substring(0, 1).toUpperCase()+rs.getString("emp_fname").substring(1);
				strPassword = strEmpFName+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd")+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM");
					 		  
			}
			rs.close();
			pst.close();
		
			List alLoans = new ArrayList();
			Map hmEmpLoan = new HashMap();
			Map<String, String> hmEmpSalLastEffectiveDate = null; //need to implement this map
			CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, uF.getDateFormat(strPayCycleStart, DBDATE, DATE_FORMAT), uF.getDateFormat(strPayCycleEnd, DBDATE, DATE_FORMAT), hmEmpSalLastEffectiveDate);
			Map hmEmpLoanInner = (Map)hmEmpLoan.get(strEmpId); 
			if(hmEmpLoanInner==null) hmEmpLoanInner=new HashMap();
			Map<String, String> hmLoanPolicies = CF.getLoanPoliciesMap(con, uF, strOrgId);
//			String strEmpLevelId = CF.getEmpLevelId(con, getStrEmpId());

			Map<String, List<Map<String, String>>> hmPerkAlign = CF.getIndividualPerkAlignAmount(con, uF, CF, uF.parseToInt(getStrEmpId()),getStrFYS(),getStrFYE(),getStrPCS(),getStrPCE(),uF.parseToInt(getStrPC()));
			if(hmPerkAlign == null) hmPerkAlign = new HashMap<String, List<Map<String,String>>>();
			
			/*pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg,salary_details sd where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND pg.financial_year_from_date=? " +
				"AND pg.financial_year_to_date=? and sd.level_id=? and pg.salary_head_id = sd.salary_head_id and bank_pay_type=? and (sd.is_delete is null or sd.is_delete=false) " +
				"and pg.salary_head_id in (select salary_head_id from emp_salary_details where isdisplay=true and emp_id=? and level_id=?) order by earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strEmpLevelId));
			pst.setInt(7, uF.parseToInt(getStrBankPayType()));
			pst.setInt(8, uF.parseToInt(getStrEmpId()));
			pst.setInt(9, uF.parseToInt(strEmpLevelId));*/
			
			pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND pg.financial_year_from_date=? " +
				"AND pg.financial_year_to_date=? and (bank_pay_type=? or bank_pay_type is null) and pg.salary_head_id in (select distinct(salary_head_id) as salary_head_id from " +
				"emp_salary_details where isdisplay=true and emp_id=? and level_id=?) order by earning_deduction desc,pg.salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			pst.setInt(7, uF.parseToInt(getStrEmpId()));
			pst.setInt(8, uF.parseToInt(strEmpLevelId));
			rs=pst.executeQuery();    
			double presentDays = 0;
			while (rs.next()) {
				presentDays = Double.parseDouble(rs.getString("present_days"));
       	     
				Double dblTotal = 0.0;
				Double dblTotalGrossAmt = 0.0;
				if (rs.getString("earning_deduction").equalsIgnoreCase("E")) {
					
					dblTotal = rs.getDouble("amount"); //OTHER ALLOWANCE/EX-
					dblTotalGrossAmt = rs.getDouble("amount");
					
					if(hmPerkAlign.containsKey(rs.getString("salary_head_id"))){
						List<Map<String, String>> alPerkAlign = hmPerkAlign.get(rs.getString("salary_head_id"));
						if(alPerkAlign == null) alPerkAlign = new ArrayList<Map<String,String>>();
						int nPerkAlignSize = alPerkAlign.size();
						for(int i = 0; i < nPerkAlignSize; i++){
							Map<String, String> hmPerkAlignInner = alPerkAlign.get(i);
							if(hmPerkAlignInner == null) hmPerkAlignInner = new HashMap<String, String>();
							salaryHeadName.add(uF.showData(hmPerkAlignInner.get("PERK_NAME"), "")+" ("+hmSalaryDetailsMap.get(rs.getString("salary_head_id"))+")");
							salHeadAmount.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
							salHeadAmountGross.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
						}
					} else{
						salaryHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						salHeadAmount.add(dblTotal);
						salHeadAmountGross.add(dblTotalGrossAmt);
					}
					
					dblTotalAmt += dblTotal;
					dblGrossTotal += dblTotalGrossAmt;

				} else if (rs.getString("earning_deduction").equalsIgnoreCase("D")) {
					if(uF.parseToInt(rs.getString("salary_head_id"))==LOAN){
						for(int i=0; i<alLoans.size(); i++){
							String loan=(String)alLoans.get(i)!=null?(String)alLoans.get(i) : "";
							String loanamt=uF.showData((String)hmEmpLoanInner.get(loan), "0");
							dblTotal = uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt)));
							deductionHeadName.add(hmLoanPolicies.get((String)alLoans.get(i)));
							deductionHeadAmount.add(uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt))));
							dblTotalDeduction += dblTotal;
						}
					}else{
						dblTotal = rs.getDouble("amount");
						deductionHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						deductionHeadAmount.add(dblTotal);
						dblTotalDeduction += dblTotal;
					}
					
				}
				
				nYear=rs.getInt("year");

			}     
          
			rs.close();
			pst.close();       			
			salaryHeadName.add("Total");
			salHeadAmount.add(dblTotalAmt);
			deductionHeadName.add("Total Deduction");
			deductionHeadAmount.add(dblTotalDeduction);
			salHeadAmountGross.add(dblGrossTotal);

			dblNetSalary = dblTotalAmt - dblTotalDeduction;

			Double totalEarning = salHeadAmount.get(salHeadAmount.size() - 1);
	        if(salaryHeadName.contains("Total")){
	        	salaryHeadName.remove(salaryHeadName.size() - 1);
	        	salHeadAmount.remove(salHeadAmount.size() - 1);
	        }
			
			Double totalDeduction =  deductionHeadAmount.get(deductionHeadAmount.size() - 1);
	        if(deductionHeadName.contains("Total Deduction")){
           deductionHeadName.remove(deductionHeadName.size() - 1);
	        deductionHeadAmount.remove(deductionHeadAmount.size() - 1);
	        }
	        
			pst = con.prepareStatement("select sum(leave_no) as leave_no, lar.leave_type_id from leave_application_register lar, leave_type lt where lar.leave_type_id = lt.leave_type_id and leave_id in (select leave_id from emp_leave_entry where emp_id=?) and _date between ? and ? group by lar.leave_type_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strPayCycleStart, DBDATE));
			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
			
			rs = pst.executeQuery();
			
			totalLeave.add(String.valueOf(presentDays));
			String leaveNo = null ;
			int leaveType = 0;
			while(rs.next()){
				leaveNo = rs.getString("leave_no");
				leaveType = Integer.parseInt(rs.getString("leave_type_id"));
				
				leaveTypeName.add(leaveType);
				totalLeave.add(leaveNo);
				
				hmLeaveType.put(leaveTypeName, totalLeave);
			
			}
			
			Iterator<Integer> itrL = leaveTypeName.iterator();	
		
			 leaveNameType.add("Present Days");
				while(itrL.hasNext())
				{	
					int lvId = itrL.next();
					
					pst = con.prepareStatement("select leave_type_name from leave_type where leave_type_id=?");
					pst.setInt(1, lvId);
					
					rs = pst.executeQuery();
					
					while(rs.next()){
						
						String Nleave = rs.getString("leave_type_name");
						leaveNameType.add(Nleave);
					}
					
				}
				
				leaveNameType.add("Total Days");
				leaveNameType.add("QB Days");
				leaveNameType.add("Earned Hrs");
				leaveNameType.add("Eff Hrs");
				leaveNameType.add("A");
				leaveNameType.add("B");
				leaveNameType.add("C");
				
				
				totalLeave.add(strTotal_days);
				totalLeave.add("0");
				totalLeave.add("0.00");
				totalLeave.add("0.00");
				totalLeave.add("0");
				totalLeave.add("0");
				totalLeave.add("0");
			
//			pst = con.prepareStatement("select * from emp_salary_details where emp_id=? and effective_date = (select max(effective_date) as effective_date " +
//				"from emp_salary_details where effective_date <= ? and emp_id=? and level_id=?) and level_id=? order by salary_head_id");
//				pst = con.prepareStatement("select * from emp_salary_details where emp_id=? and effective_date = (select max(effective_date) as effective_date " +
//				"from emp_salary_details where effective_date <= ? and emp_id=?) order by salary_head_id");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setDate(2, uF.getDateFormat(strEntryDate, DBDATE));
//			pst.setInt(3, uF.parseToInt(getStrEmpId()));
////			pst.setInt(4, uF.parseToInt(strEmpLevelId));
////			pst.setInt(5, uF.parseToInt(strEmpLevelId));
//			rs = pst.executeQuery();
//			while(rs.next()){
//				alGross.add(rs.getString("amount"));
//			}
//			rs.close();
//			pst.close();
			
			payEmpHead.add("EMPLOYEE NAME");
			payEmpHead.add("DESIGNATION");
			payEmpHead.add("EMPLOYEE CODE");
			
			
			
			Map hmLeaveDatesType = new HashMap();
			Map<String, Map<String, String>> leaveDetailsMap = CF.getActualLeaveDates(con, CF, uF, getStrFYS(), getStrFYE(),  hmLeaveDatesType, true, null);
			
			Map<String, String> leaveEmpDetailsMap = leaveDetailsMap.get(getStrEmpId());

			if(leaveEmpDetailsMap!=null)
			{
			
			Set keys = leaveEmpDetailsMap.keySet();
					
			for (Iterator i = keys.iterator(); i.hasNext();) {
				String nkey = (String) i.next();
				String strValue = (String) leaveEmpDetailsMap.get(nkey);
				

				Iterator<String> itr = leaveName.iterator();	
			  int count=0;
             while(itr.hasNext())
             {
           	  
           	  if(strValue.equalsIgnoreCase(itr.next()));
           	  {
           		  count=1;
           	  }
           }
			
			if(count==0)
			{
				leaveName.add(strValue);
			}
		
		}
			
			Iterator<String> itrleave = leaveName.iterator();	
			  int nleaveToatal=0;
				while(itrleave.hasNext())
				{				
					Set keysC = leaveEmpDetailsMap.keySet();
					String strLeave = itrleave.next();
					int nleaveCount=0;		
					
					
					for (Iterator i = keysC.iterator(); i.hasNext();) {
						String nkey = (String) i.next();
						String strValue = (String) leaveEmpDetailsMap.get(nkey);
						
						if(strValue!=null && strValue.equalsIgnoreCase(strLeave))
						{
							nleaveCount++;
							
							nleaveToatal++;
						}
			
					}  
					
					noOfLeave.add(nleaveCount);
							
					
				}	  
				
				
				noOfLeave.add(nleaveToatal);

				leaveName.add("Total");
	}
			
			
			
			
			pst = con.prepareStatement("select sum(accrued_leaves) as accru, sum(taken_leaves) as taken_leaves, (sum(accrued_leaves) - sum(taken_leaves)) as balance, leave_type_id from leave_register where emp_id = ? group by leave_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				hmBalanceLeave.put(rs.getString("leave_type_id"), rs.getString("balance"));
			}
			rs.close();
			pst.close();
			hmLeaveNameMap = CF.getLeaveTypeMap(con);
			
			generateSalarySlipFourthFormat(getStrEmpId(),getStrMonth(), hmOrganisationDetails,strTotal_days,presentDays,dblNetSalary,totalDeduction,totalEarning,monthName);
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			dB.closeResultSet(rs);
			dB.closeStatements(pst);
			dB.closeConnection(con);
		}
	
	}


	private void generateSalarySlipFourthFormat(String strEmpId, String strMonth, Map<String, String> hmOrganisationDetails, String strTotal_days, double presentDays, Double dblNetSalary, Double totalDeduction, Double totalEarning, String monthName) {
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			int count=0;
			int	nyeardevfirst = 0;
			int	nyeardevsecond=0;
			List<Integer> alList=(List<Integer>)request.getAttribute("alList");
			Map<String,String> hmempcnt =(Map<String,String> )request.getAttribute("hmempcnt");
			Map<Integer,Map<String,String>> hmMap=(Map<Integer,Map<String,String>>)request.getAttribute("hmMap");
			if(hmMap==null)hmMap=new HashMap<Integer, Map<String,String>>();
			
			Map<String,String> hmEPFChallan=(Map<String,String>)request.getAttribute("hmEPFChallan");
			
			Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			Map<String,String> hmFeatureStatus = (Map<String,String>)request.getAttribute("hmFeatureStatus");
			
			com.itextpdf.text.Font heading = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 13);
			com.itextpdf.text.Font normal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11);
			com.itextpdf.text.Font normalwithbold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			com.itextpdf.text.Font small = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7);
			com.itextpdf.text.Font smallBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7,Font.BOLD);
			com.itextpdf.text.Font italicEffect = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
	        com.itextpdf.text.pdf.PdfWriter pdfWriter = com.itextpdf.text.pdf.PdfWriter.getInstance(document,buffer);
	        if(hmFeatureStatus != null &&uF.parseToBoolean(hmFeatureStatus.get(F_PASSWORD_PROTECTED_SALARY_SLIP))){
				pdfWriter.setEncryption(strPassword.getBytes(), strPassword.getBytes(), com.itextpdf.text.pdf.PdfWriter.ALLOW_PRINTING, com.itextpdf.text.pdf.PdfWriter.ENCRYPTION_AES_128);
			}
	        document.open();
	        
	        while(count<2){
	        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(16);
			table.setWidthPercentage(100);       
			int[] cols = {10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10};
			table.setWidths(cols);
	        
			String strCompanyName=hmOrganisationDetails.get("ORG_NAME");
			String orgId =hmOrganisationDetails.get("ORG_ID");
			
			com.itextpdf.text.pdf.PdfPCell row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(strCompanyName));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP |Rectangle.LEFT|Rectangle.RIGHT|Rectangle.BOTTOM);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Pay Slip For : "+monthName,small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);
	        row1.setPadding(2.5f);
	        table.addCell(row1); 
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Desig. : "+empDetails.get(1),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(6);
	        row1.setPadding(2.5f);
	        table.addCell(row1); 
	     
           //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Emp No : "+uF.showData(empDetails.get(2), ""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(4);	        
	        table.addCell(row1);
	      
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Emp Name : "+uF.showData(empDetails.get(0), ""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(4);	
	        table.addCell(row1);
	        
	    
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("PF No. : "+uF.showData(empDetails.get(3), ""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);	
	        row1.setColspan(4);	
	        table.addCell(row1);
	      
	    
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Department Name : "+uF.showData(empDetails.get(9), ""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(4);	
	        table.addCell(row1);
	      
	        
	        com.itextpdf.text.pdf.PdfPTable tableL = new com.itextpdf.text.pdf.PdfPTable(leaveNameType.size());
	        tableL.setWidthPercentage(100);      
	        
	        
           for(int i=0;i<leaveNameType.size();i++) {
				
           	com.itextpdf.text.pdf.PdfPCell rowinner11 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(leaveNameType.get(i),small));
           	rowinner11.setPadding(2.5f);
           	rowinner11.setHorizontalAlignment(Element.ALIGN_CENTER);
           	rowinner11.setBorder(Rectangle.RIGHT|Rectangle.TOP|Rectangle.LEFT|Rectangle.BOTTOM);
           	
				tableL.addCell(rowinner11);
		        
				
           }
		        
           for(int i=0;i<totalLeave.size();i++) {
           	
           	com.itextpdf.text.pdf.PdfPCell rowinner22 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(totalLeave.get(i),small));
				rowinner22.setPadding(2.5f);
				rowinner22.setHorizontalAlignment(Element.ALIGN_CENTER);
				rowinner22.setBorder(Rectangle.RIGHT|Rectangle.LEFT|Rectangle.BOTTOM);
				
				tableL.addCell(rowinner22);
		        
		      
			}
       
	        row1 =new com.itextpdf.text.pdf.PdfPCell(tableL);
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(16);	        
	        table.addCell(row1);
	     
	   
         //New Row  
           row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Earnings",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM);
	        row1.setColspan(3);	        
	        table.addCell(row1);
	        
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total Earnings",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM );
	        row1.setColspan(3);	 
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("     Actual Payment",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM);
	        row1.setColspan(3);	 
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Deductions",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM);
	        row1.setColspan(3);	 
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total Deduction",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM);
	        row1.setColspan(4);	 
	        table.addCell(row1);
	        
	        
	        com.itextpdf.text.pdf.PdfPTable table1 = new com.itextpdf.text.pdf.PdfPTable(8);
	        table1.setWidthPercentage(100);   

	        /*totalEarning = salHeadAmount.get(salHeadAmount.size() - 1);
	        if(salaryHeadName.contains("Total")){
	        	salaryHeadName.remove(salaryHeadName.size() - 1);
	        	salHeadAmount.remove(salHeadAmount.size() - 1);
	        }*/
	        
			for(int i=0;i<salaryHeadName.size();i++) {
				
				com.itextpdf.text.pdf.PdfPCell rowinner1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(salaryHeadName.get(i),small));
				rowinner1.setPadding(2.5f);
				rowinner1.setHorizontalAlignment(Element.ALIGN_LEFT);
				rowinner1.setBorder(Rectangle.NO_BORDER);
				rowinner1.setColspan(3);
		        table1.addCell(rowinner1);
		        
//		        System.out.println("Salary Name :"+salaryHeadName.get(i));
		        
		        com.itextpdf.text.pdf.PdfPCell rowinner2 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(Double.toString(salHeadAmount.get(i)),small));
				rowinner2.setPadding(2.5f);
				rowinner2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				rowinner2.setBorder(Rectangle.NO_BORDER);
				rowinner2.setColspan(3);
		        table1.addCell(rowinner2);
		        
//		        System.out.println("Salary Ammount :"+salHeadAmount.get(i));
		        
		        com.itextpdf.text.pdf.PdfPCell rowinner3 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(Double.toString(salHeadAmount.get(i)),small));
		        rowinner3.setPadding(2.5f);
		        rowinner3.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        rowinner3.setBorder(Rectangle.RIGHT);
		        rowinner3.setColspan(2);
		        table1.addCell(rowinner3);
		        
//		        System.out.println("Salary Ammount :"+salHeadAmount.get(i));
		      
			}
			//New Row  
           row1 =new com.itextpdf.text.pdf.PdfPCell(table1);
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(8);	        
	        table.addCell(row1);
	        
	        
	        com.itextpdf.text.pdf.PdfPTable table2 = new com.itextpdf.text.pdf.PdfPTable(8);
	        table2.setWidthPercentage(100);       

	        /*totalDeduction =  deductionHeadAmount.get(deductionHeadAmount.size() - 1);
	        if(deductionHeadName.contains("Total Deduction")){
           deductionHeadName.remove(deductionHeadName.size() - 1);
	        deductionHeadAmount.remove(deductionHeadAmount.size() - 1);
	        }*/
	        
           for(int i=0;i<deductionHeadName.size();i++) {
				
           	com.itextpdf.text.pdf.PdfPCell rowinner1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(deductionHeadName.get(i),small));
				rowinner1.setPadding(2.5f);
				rowinner1.setHorizontalAlignment(Element.ALIGN_LEFT);
				rowinner1.setBorder(Rectangle.NO_BORDER);
				rowinner1.setColspan(4);
		        table2.addCell(rowinner1);
		        
		        
		        com.itextpdf.text.pdf.PdfPCell rowinner2 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(Double.toString(deductionHeadAmount.get(i)),small));
				rowinner2.setPadding(2.5f);
				rowinner2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				rowinner2.setBorder(Rectangle.NO_BORDER);
				rowinner2.setColspan(4);
		        table2.addCell(rowinner2);
		        
		      
			}
       
	        row1 =new com.itextpdf.text.pdf.PdfPCell(table2);
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(8);	        
	        table.addCell(row1);
	     
	        //New Row
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(8);	        
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("   Gross Deduction : ",smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(4);	 
	        table.addCell(row1);
	        
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("              "+totalDeduction,smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(4);	        
	        table.addCell(row1);
	        
	        

	        //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Gross Earning  : ",smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(4);	        
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "+totalEarning,smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(4);	 
	        table.addCell(row1);
	       
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("   Net Pay : ",smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(4);	 
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "+uF.formatIntoTwoDecimal(dblNetSalary),smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(4);	 
	        table.addCell(row1);
	      
	        
	        
	        //New Row
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(16);	 
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(16);	 
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(11);	 
	        table.addCell(row1);
	      
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Authorised Signatory",smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	 
	        table.addCell(row1);
	        
	        nyeardevfirst=nYear;
			nyeardevfirst=nYear%10;
			nYear=nYear/10;
			nyeardevsecond=nYear%10;
	        
			document.newPage();
			
	        document.add(table);
	        count++;
	        }
	        document.close();
	        
			String filename="PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf";
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename="+filename+"");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
		
	}catch (Exception e) {
		e.printStackTrace();  
	}
		
	}
	
	
	public void generateSalarySlip() throws SQLException {
		
		Database dB = new Database();
		dB.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		
//		System.out.println("in generateSalarySlip ===========>> ");
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = dB.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmTemp =null;
			Map<String, String> hmOrganisationDetails = new HashMap<String, String>();
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpDepartmentMap = CF.getDepartmentMap(con,null, null);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
//			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con);
			
//			int nEmpLevelId = CF.getEmpLevelId(getStrEmpId(), request);
			
			pst =con.prepareStatement("select max(entry_date) as entry_date, paid_from, paid_to,paid_days,total_days,payment_mode,month,year," +
					"emp_id,sal_effective_date from payroll_generation where emp_id = ? and financial_year_from_date=? AND financial_year_to_date=? AND month=? " +
					"and paycycle=? and (bank_pay_type=? or bank_pay_type is null) group by paid_from, paid_to,paid_days,total_days,payment_mode,month,year,emp_id,sal_effective_date");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(getStrPC()));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			rs = pst.executeQuery();
			String strEntryDate = null;
			String strPayCycleStart = null;
			String strPayCycleEnd = null;
			String strTotal_days =null;
			String strPaid_days = null;
			Map<String, String> hmEmpSalLastEffectiveDate = new HashMap<String, String>();
			while(rs.next()){
				strEntryDate = rs.getString("entry_date");
				strPayCycleStart = rs.getString("paid_from");
				strPayCycleEnd = rs.getString("paid_to");
			//===start parvez date: 04-03-2023===	
//				strTotal_days =rs.getString("total_days");
//				strPaid_days =rs.getString("paid_days");
			//===end parvez date: 04-03-2023===	
				strPaymentMode =rs.getString("payment_mode");
				 pMonth =rs.getInt("month");
				 pYear =rs.getInt("year");
				 hmEmpSalLastEffectiveDate.put(rs.getString("emp_id"), rs.getString("sal_effective_date"));
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 04-03-2023===
			pst =con.prepareStatement("select * from approve_attendance where emp_id = ? and financial_year_start=? AND financial_year_end=? and paycycle=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrPC()));
//			System.out.println("GSS/8546--pst===>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				strTotal_days =rs.getString("total_days");
				strPaid_days =rs.getString("paid_days");
			}
			rs.close();
			pst.close();
		//===end parvez date: 04-03-2023===
			
		    monthName = uF.getMonth(pMonth)+" "+pYear;
		   
		//===start parvez date: 04-03-2023===    
//           tDays = uF.parseToInt(strTotal_days);
//           pDays = uF.parseToDouble(strPaid_days);
           tDays = uF.parseToDouble(strTotal_days);
           pDays = uF.parseToDouble(strPaid_days);
       //===end parvez date: 04-03-2023===    
           
           unpaidDays = tDays - pDays;
           
//           System.out.println("Total :"+tDays+"PaidDays :"+pDays+"Unpaid Days :"+unpaidDays);
	            
			if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("1")){
				strPaymentMode ="Bank Transfer";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cash";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cheque";	
			}
				
//			System.out.println("Payment Mode  :"+strPaymentMode);
	            
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			
			pst = con.prepareStatement("select * from org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
		//	Map<String, String> hmOrganisationDetails = new HashMap<String, String>();
			while(rs.next()){
				hmOrganisationDetails.put("ORG_ID", rs.getString("org_id"));
				hmOrganisationDetails.put("ORG_LOGO", rs.getString("org_logo"));
				hmOrganisationDetails.put("ORG_NAME", rs.getString("org_name"));
				hmOrganisationDetails.put("PDF_TYPE", rs.getString("payslip_format"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
//				"and isdisplay=true and effective_date<=? group by level_id"); 
			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true and " +
					"isdisplay=true and effective_date = (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
					"and isdisplay=true and effective_date<=?) group by level_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
//			System.out.println("pst ================>> " + pst);
			String strEffectiveDate = null;
			String strEmpLevelId = null;
			rs = pst.executeQuery();
			while(rs.next()){
				strEffectiveDate = rs.getString("effective_date");
				strEmpLevelId = rs.getString("level_id");
			}
			rs.close();
			pst.close();
//			System.out.println("strEmpLevelId ===>> " + strEmpLevelId);
			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(strEmpLevelId));
			
			
			pst = con.prepareStatement("SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id  = eod.emp_id and emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map hmWorkLocationMap = CF.getWorkLocationMap(con);
				hmTemp = (Map<String, String>)hmWorkLocationMap.get(rs.getInt("wlocation_id")+"");
				if(hmTemp==null)hmTemp=new HashMap();
				strCompanyAddress = hmTemp.get("WL_ADDRESS")+" " +hmTemp.get("WL_CITY")+" " +hmTemp.get("WL_PINCODE");
				
				strLevelId = hmEmpLevelMap.get(rs.getString("emp_id"));
				
				/*
				empName = rs.getString("emp_fname") + " "+ rs.getString("emp_lname");
				empId=hmEmpDesigMap.get(rs.getString("emp_id"));
				empCode =rs.getString("empcode");
				empBankAccNo =rs.getString("emp_bank_acct_nbr");
				empGradeId =hmGradeMap.get(rs.getString("grade_id"));
				empPfNo =rs.getString("emp_pf_no");
				wl_Name =hmTemp.get("WL_NAME"); // pune
				joining_Date =uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT);
				dpartId =uF.showData(hmEmpDepartmentMap.get(rs.getString("depart_id")), ""); 
			    empPanNo =rs.getString("emp_pan_no");
				payCycleEnd =uF.getDateFormat(getStrMonth(), "MM", "MMMM")+", "+ uF.getDateFormat(strPayCycleEnd, DBDATE, "yy");
				empBankName =rs.getString("emp_bank_name");
				*/
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}

				empDetails.add(rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname"));
				empDetails.add(hmEmpDesigMap.get(rs.getString("emp_id")));
				empDetails.add(rs.getString("empcode"));
				if(!(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==3)){
					empDetails.add(hmLevelMap.get(strLevelId));
				}
				empDetails.add(rs.getString("emp_bank_acct_nbr"));
				if(!(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==3)){
					empDetails.add(hmGradeMap.get(rs.getString("grade_id")));
				}
//				empDetails.add(rs.getString("emp_gpf_no"));
				empDetails.add(rs.getString("emp_pf_no"));
				empDetails.add(hmTemp.get("WL_NAME"));
				empDetails.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				empDetails.add(hmEmpDepartmentMap.get(rs.getString("depart_id")));
				empDetails.add(rs.getString("emp_pan_no"));
				if(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==2){
					empDetails.add(rs.getString("emp_bank_name"));
					empDetails.add(uF.showData(hmEmpDepartmentMap.get(rs.getString("depart_id")), ""));
				}
//				empDetails.add(strPayMode);
//				if(!(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==3)){
//				 empDetails.add(uF.getDateFormat(getStrMonth(), "MM", "MMMM")+", "+ uF.getDateFormat(strPayCycleEnd, DBDATE, "yy"));
//				} else {
//				System.out.println("monthName====>"+monthName);
					empDetails.add(monthName);
					empDetails.add(rs.getString("uan_no"));
					empDetails.add(" ");
//				}
				strEmpImage = rs.getString("emp_image");
			
//				strOrgId =  rs.getString("org_id");
				
				String strEmpFName = rs.getString("emp_fname").substring(0, 1).toUpperCase()+rs.getString("emp_fname").substring(1);
				strPassword = strEmpFName+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd")+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM");
					 		  
			}
			rs.close();
			pst.close();
			
			if(!(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==3)){
			pst = con.prepareStatement("select bd.bank_name as bankname from bank_details bd,branch_details brd where bd.bank_id=brd.bank_id and brd.branch_id =?");
			pst.setInt(1,uF.parseToInt(empDetails.get(11)));
			rs = pst.executeQuery();
			while(rs.next()){
				bankName = rs.getString("bankname");
			}
			rs.close();
			pst.close();
			}
			
			List alLoans = new ArrayList();
			Map hmEmpLoan = new HashMap();
//			Map<String, String> hmEmpSalLastEffectiveDate = null; //need to implement this map
			CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, uF.getDateFormat(strPayCycleStart, DBDATE, DATE_FORMAT), uF.getDateFormat(strPayCycleEnd, DBDATE, DATE_FORMAT), hmEmpSalLastEffectiveDate);
			Map hmEmpLoanInner = (Map)hmEmpLoan.get(strEmpId+ ((hmEmpSalLastEffectiveDate != null && hmEmpSalLastEffectiveDate.size()>0) ? ("_"+ hmEmpSalLastEffectiveDate.get(strEmpId)) : "")); 
			if(hmEmpLoanInner==null) hmEmpLoanInner=new HashMap();
			Map<String, String> hmLoanPolicies = CF.getLoanPoliciesMap(con, uF, strOrgId);
//			String strEmpLevelId = CF.getEmpLevelId(con, getStrEmpId());
			
			Map<String, List<Map<String, String>>> hmPerkAlign = CF.getIndividualPerkAlignAmount(con, uF, CF, uF.parseToInt(getStrEmpId()),getStrFYS(),getStrFYE(),getStrPCS(),getStrPCE(),uF.parseToInt(getStrPC()));
			if(hmPerkAlign == null) hmPerkAlign = new HashMap<String, List<Map<String,String>>>();
			
			/*pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg,salary_details sd where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND pg.financial_year_from_date=? " +
				"AND pg.financial_year_to_date=? and sd.level_id=? and pg.salary_head_id = sd.salary_head_id and bank_pay_type=? and (sd.is_delete is null or sd.is_delete=false) " +
				"and pg.salary_head_id in (select distinct(salary_head_id) as salary_head_id from emp_salary_details where isdisplay=true and emp_id=? and level_id=?) order by earning_deduction desc, weight");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strEmpLevelId));
//			pst.setInt(6, uF.parseToInt("-1"));
			pst.setInt(7, uF.parseToInt(getStrBankPayType()));
			pst.setInt(8, uF.parseToInt(getStrEmpId()));
			pst.setInt(9, uF.parseToInt(strEmpLevelId));*/
			
//			pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND pg.financial_year_from_date=? AND pg.financial_year_to_date=? and pg.salary_head_id in (SELECT distinct(salary_head_id) FROM salary_details order by salary_head_id) and bank_pay_type=? and pg.salary_head_id in (select distinct(salary_head_id) as salary_head_id from emp_salary_details where isdisplay=true and emp_id=? and level_id=?) order by earning_deduction desc");
			
			pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND pg.financial_year_from_date=? " +
				"AND pg.financial_year_to_date=? and (bank_pay_type=? or bank_pay_type is null) and pg.salary_head_id in (select distinct(salary_head_id) as salary_head_id from " +
				"emp_salary_details where isdisplay=true and emp_id=? and level_id=?) order by earning_deduction desc,pg.salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			pst.setInt(7, uF.parseToInt(getStrEmpId()));
			pst.setInt(8, uF.parseToInt(strEmpLevelId));
//			System.out.println("pst====>"+pst);
           rs=pst.executeQuery();            
           while (rs.next()) {
				Double dblTotal = 0.0;
				Double dblTotalGrossAmt = 0.0;
				if (rs.getString("earning_deduction").equalsIgnoreCase("E")) {
					dblTotal = rs.getDouble("amount"); //OTHER ALLOWANCE/EX-
					dblTotalGrossAmt = rs.getDouble("amount");
					if(hmPerkAlign.containsKey(rs.getString("salary_head_id"))){
						List<Map<String, String>> alPerkAlign = hmPerkAlign.get(rs.getString("salary_head_id"));
						if(alPerkAlign == null) alPerkAlign = new ArrayList<Map<String,String>>();
						int nPerkAlignSize = alPerkAlign.size();
						for(int i = 0; i < nPerkAlignSize; i++){
							Map<String, String> hmPerkAlignInner = alPerkAlign.get(i);
							if(hmPerkAlignInner == null) hmPerkAlignInner = new HashMap<String, String>();
							salaryHeadName.add(uF.showData(hmPerkAlignInner.get("PERK_NAME"), "")+" ("+hmSalaryDetailsMap.get(rs.getString("salary_head_id"))+")");
							salHeadAmount.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
							salHeadAmountGross.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
						}
					} else{
						salaryHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						salHeadAmount.add(dblTotal);
						salHeadAmountGross.add(dblTotalGrossAmt);
					}
					
					dblTotalAmt += dblTotal;
					dblGrossTotal += dblTotalGrossAmt;

				} else if (rs.getString("earning_deduction").equalsIgnoreCase("D")) {
					if(uF.parseToInt(rs.getString("salary_head_id"))==LOAN){
						for(int i=0; i<alLoans.size(); i++){
							String loan=(String)alLoans.get(i)!=null?(String)alLoans.get(i) : "";
							String loanamt=uF.showData((String)hmEmpLoanInner.get(loan), "0");
							dblTotal = uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt)));
							deductionHeadName.add(hmLoanPolicies.get((String)alLoans.get(i)));
							deductionHeadAmount.add(uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt))));
							dblTotalDeduction += dblTotal;
						}
					}else{
						dblTotal = rs.getDouble("amount");
						deductionHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						deductionHeadAmount.add(dblTotal);
						dblTotalDeduction += dblTotal;
					}
				}
				nYear=rs.getInt("year");
			}     
			rs.close();
			pst.close();   
			
//			System.out.println("salaryHeadName ===>> " + salaryHeadName);
			
			salaryHeadName.add("Total");
			salHeadAmount.add(dblTotalAmt);
			deductionHeadName.add("Total Deduction");
			deductionHeadAmount.add(dblTotalDeduction);
			salHeadAmountGross.add(dblGrossTotal);

			dblNetSalary = dblTotalAmt - dblTotalDeduction;
//			System.out.println("dblTotalAmt=====>"+dblTotalAmt+"---dblTotalDeduction=====>"+dblTotalDeduction+"---dblNetSalary=====>"+dblNetSalary);
			
//			pst = con.prepareStatement("select * from emp_salary_details where emp_id=? and effective_date = (select max(effective_date) as effective_date " +
//					"from emp_salary_details where effective_date <= ? and emp_id=? and level_id=?) and level_id=? order by salary_head_id");
//			pst = con.prepareStatement("select * from emp_salary_details where emp_id=? and effective_date = (select max(effective_date) as effective_date " +
//			"from emp_salary_details where effective_date <= ? and emp_id=?) order by salary_head_id");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setDate(2, uF.getDateFormat(strEntryDate, DBDATE));
//			pst.setInt(3, uF.parseToInt(getStrEmpId()));
////			pst.setInt(4, uF.parseToInt(strEmpLevelId));
////			pst.setInt(5, uF.parseToInt(strEmpLevelId));
//			System.out.println("pst ===>> " + pst);
//			rs = pst.executeQuery();
//			while(rs.next()){
//				alGross.add(rs.getString("amount"));
//			}
//			rs.close();
//			pst.close();
		
//			System.out.println("alGross ===>> " + alGross);
			
			/*payEmpHead.add("PAY SLIP FOR THE MONTH OF");*/
			
			payEmpHead.add("EMPLOYEE NAME");
			payEmpHead.add("DESIGNATION");
			payEmpHead.add("EMPLOYEE CODE");
			if(!(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==3)){
				payEmpHead.add("LEVEL");
			}
			payEmpHead.add("ACC. NO.");
			if(!(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==3)){
				payEmpHead.add("GRADE");
			}
//			payEmpHead.add("GPF ACC. NO.");
			payEmpHead.add("PF ACC. NO.");
			payEmpHead.add("BRANCH");
			payEmpHead.add("JOINING DATE");
			payEmpHead.add("DEPARTMENT");
			payEmpHead.add("PAN NO");
			payEmpHead.add("MONTH");
			payEmpHead.add("UAN NO.");
			payEmpHead.add("");
			
			Map hmLeaveDatesType = new HashMap();
//			Map<String, Map<String, String>> leaveDetailsMap = CF.getLeaveDates(con,getStrFYS(), getStrFYE(), CF, hmLeaveDatesType, true, null);
			Map<String, Map<String, String>> leaveDetailsMap = CF.getActualLeaveDates(con, CF, uF, getStrFYS(), getStrFYE(),  hmLeaveDatesType, true, null);
			
			Map<String, String> leaveEmpDetailsMap = leaveDetailsMap.get(getStrEmpId());

			if(leaveEmpDetailsMap!=null) {
			
			Set keys = leaveEmpDetailsMap.keySet();
			for (Iterator i = keys.iterator(); i.hasNext();) {
				String nkey = (String) i.next();
				String strValue = (String) leaveEmpDetailsMap.get(nkey);

				Iterator<String> itr = leaveName.iterator();	
			  int count=0;
             while(itr.hasNext()) {
//           	  if(strValue.equalsIgnoreCase(itr.next()));
              if(strValue.equalsIgnoreCase(itr.next())) {
           		  count=1;
           	  }
           }
			
			if(count==0) {
				leaveName.add(strValue);
			}
		}
			
			Iterator<String> itrleave = leaveName.iterator();	
			  int nleaveToatal=0;
				while(itrleave.hasNext()) {				
					Set keysC = leaveEmpDetailsMap.keySet();
					String strLeave = itrleave.next();
					int nleaveCount=0;		
					
					for (Iterator i = keysC.iterator(); i.hasNext();) {
						String nkey = (String) i.next();
						String strValue = (String) leaveEmpDetailsMap.get(nkey);
						
						if(strValue!=null && strValue.equalsIgnoreCase(strLeave))
						{
							nleaveCount++;
							
							nleaveToatal++;
						}
			
					}  
					noOfLeave.add(nleaveCount);
				}	  
				
				noOfLeave.add(nleaveToatal);
				leaveName.add("Total");
	}
			
			
			pst = con.prepareStatement("select sum(accrued_leaves) as accru, sum(taken_leaves) as taken_leaves, (sum(accrued_leaves) - sum(taken_leaves)) as balance, leave_type_id from leave_register where emp_id = ? group by leave_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				hmBalanceLeave.put(rs.getString("leave_type_id"), rs.getString("balance"));
			}
			rs.close();
			pst.close();
			hmLeaveNameMap = CF.getLeaveTypeMap(con);
			
			if(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==2){
			    createSalarySlipPdfSecondFormat(getStrEmpId(),getStrMonth(), hmOrganisationDetails);
			} else if(uF.parseToInt(hmOrganisationDetails.get("PDF_TYPE"))==3){
				createSalarySlipPdfThirdFormat(getStrEmpId(),getStrMonth(), hmOrganisationDetails);
			} else {
				pdfCreationPayslip(getStrEmpId(),getStrMonth(), hmOrganisationDetails);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			dB.closeResultSet(rs);
			dB.closeStatements(pst);
			dB.closeConnection(con);
		}
	}
	
	private void createSalarySlipPdfThirdFormat(String strEmpId2, String strMonth2, Map<String, String> hmOrganisationDetails) {
		UtilityFunctions uF = new UtilityFunctions();
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
			String strCompanyLogo=hmOrganisationDetails.get("ORG_LOGO");
			String strCompanyName=hmOrganisationDetails.get("ORG_NAME");
			String orgId =hmOrganisationDetails.get("ORG_ID");
			Map<String,String> hmFeatureStatus = (Map<String,String>)request.getAttribute("hmFeatureStatus");
			
			Document document = new Document();
			PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
			if(hmFeatureStatus != null &&uF.parseToBoolean(hmFeatureStatus.get(F_PASSWORD_PROTECTED_SALARY_SLIP))){
				pdfWriter.setEncryption(strPassword.getBytes(), strPassword.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
			}
			document.open();
			document.add(new Paragraph(" "));

			String filePath = null;
			String filePathCompanyLOgo = null;
			String filePathCompanyLOgodefault=null;
			String filePathproductLogo= null;
			
			if(CF.getStrDocSaveLocation()!=null){
				filePath = CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+ strEmpId+"/"+strEmpImage;
				
				filePathCompanyLOgo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+strCompanyLogo;
				filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
				filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
			}else{
				filePath = request.getRealPath("/userImages/" +strEmpImage+ "");
				filePathCompanyLOgo = request.getRealPath("/userImages/"+strCompanyLogo+"");
				filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
				filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
			}  
			Image imageLogo=null;
			
			try{
//				System.out.println("filePathCompanyLOgo========>"+filePathCompanyLOgo);
				
				FileInputStream fileInputStream1=null;
		        File file1 = new File(filePathCompanyLOgo);
		        byte[] bFile1 = new byte[(int) file1.length()];
		        fileInputStream1 = new FileInputStream(file1);
			    fileInputStream1.read(bFile1);
			    fileInputStream1.close();
		        imageLogo = Image.getInstance(bFile1);
		        
			}catch(FileNotFoundException e){
				imageLogo = Image.getInstance(filePathCompanyLOgodefault);
			}
			
			PdfPTable photoImagetable = new PdfPTable(1);
			photoImagetable.setWidthPercentage(10);
			photoImagetable.getDefaultCell().setPadding(1);


			PdfPTable companyNameTable = new PdfPTable(1);
			int[] arrheaderwidthsh = { 100 }; // percentage
			companyNameTable.getDefaultCell().setBorderWidth(0);
			companyNameTable.setWidths(arrheaderwidthsh);
			PdfPCell companyNamecell = new PdfPCell(new Phrase(strCompanyName, FontFactory.getFont("Verdana", 14,Font.BOLD)));
			

			companyNamecell.setBorderWidthTop(0);
			companyNamecell.setBorderWidthBottom(1);
			companyNamecell.setBorderWidthLeft(0);
			companyNamecell.setBorderWidthRight(0);

			companyNamecell.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell);
			PdfPCell companyNamecell1 = new PdfPCell(new Phrase(strCompanyAddress, FontFactory.getFont("Verdana", 10,Font.BOLD)));

			companyNamecell1.setBorderWidthTop(0);
			companyNamecell1.setBorderWidthBottom(0);
			companyNamecell1.setBorderWidthLeft(0);
			companyNamecell1.setBorderWidthRight(0);

			companyNamecell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell1);

			PdfPTable LogoImage = new PdfPTable(2);
			int[] arrheaderwidths1 = { 70, 30 }; // percentage
			LogoImage.getDefaultCell().setBorderWidth(1);
			LogoImage.setWidths(arrheaderwidths1);
			LogoImage.setTotalWidth(600); 
			LogoImage.addCell(companyNameTable);
			LogoImage.addCell(imageLogo);

			PdfPTable titalTable = new PdfPTable(1);
			int[] arrheaderwidths2 = { 100 }; // percentage
			titalTable.getDefaultCell().setBorderWidth(1);
			titalTable.getDefaultCell().setPadding(1);
			titalTable.setWidths(arrheaderwidths2);

			PdfPCell cellb = new PdfPCell(new Phrase(" PAY SLIP ",FontFactory.getFont("Verdana", 15, Font.BOLD)));

			cellb.getExtraParagraphSpace();
			cellb.setBorderWidth(1);
			cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
			titalTable.addCell(cellb);

			PdfPTable empTable = new PdfPTable(6);

			int[] arrheaderwidths5 = { 31, 2, 30, 15, 2, 20 }; // percentage
			empTable.getDefaultCell().setBorderWidth(1);

			empTable.setWidths(arrheaderwidths5);
			empTable.setTotalWidth(800);
			
			Iterator<String> itr = payEmpHead.iterator();
			int k = 0;

			
			for(; k<payEmpHead.size(); k++){
			
				PdfPCell cellE = new PdfPCell(new Phrase(payEmpHead.get(k),FontFactory.getFont("Verdana", 7, Font.BOLD)));
				
				cellE.setBorderWidth(0);
				empTable.addCell(cellE);

				if(payEmpHead.get(k).isEmpty()){
					PdfPCell cellcollan = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 7, Font.BOLD)));
					cellcollan.setBorderWidth(0);
					empTable.addCell(cellcollan);
				}else{
					PdfPCell cellcollan = new PdfPCell(new Phrase(":",FontFactory.getFont("Verdana", 7, Font.BOLD)));
					cellcollan.setBorderWidth(0);
					empTable.addCell(cellcollan);
				}
				PdfPCell cellF = new PdfPCell(new Phrase(empDetails.get(k),FontFactory.getFont("Verdana", 7, Font.NORMAL)));
				cellF.setBorderWidth(0);
				empTable.addCell(cellF);
			}

			PdfPTable imageEmpDetailTable = new PdfPTable(1);

			int[] arrheaderwidths4 = { 100 }; // percentage

			imageEmpDetailTable.getDefaultCell().setBorderWidth(1);
			imageEmpDetailTable.getDefaultCell().setPadding(1);
			imageEmpDetailTable.setWidths(arrheaderwidths4);

			PdfPTable netGrossTital = new PdfPTable(2);
			int[] arrheaderwidths6 = { 50, 50 }; // percentage
			netGrossTital.getDefaultCell().setBorderWidth(1);
			netGrossTital.getDefaultCell().setPadding(1);
			netGrossTital.setWidths(arrheaderwidths6);

			PdfPCell cellQ = new PdfPCell(new Phrase("GROSS EARNINGS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

			cellQ.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellQ.setBorderWidth(1);
			netGrossTital.addCell(cellQ);

			PdfPCell cellQ1 = new PdfPCell(new Phrase("NET EARNINGS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellQ1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellQ1.setBorderWidth(1);
			netGrossTital.addCell(cellQ1);

			PdfPTable grossTitalTable = new PdfPTable(2);

			int[] arrheaderwidths7 = { 60, 40 }; // percentage
			grossTitalTable.getDefaultCell().setBorderWidth(1);
			grossTitalTable.getDefaultCell().setPadding(1);
			grossTitalTable.setWidths(arrheaderwidths7);

			PdfPTable GrossHeadTable = new PdfPTable(1);
			int[] arrheaderwidths7A = { 100 }; // percentage
			GrossHeadTable.getDefaultCell().setBorderWidth(1);
			GrossHeadTable.getDefaultCell().setPadding(1);
			GrossHeadTable.setWidths(arrheaderwidths7A);

			PdfPTable grossAmtTable = new PdfPTable(1);
			int[] arrheaderwidths7B = { 100 }; // percentage
			grossAmtTable.getDefaultCell().setBorderWidth(1);
			grossAmtTable.getDefaultCell().setPadding(1);
			grossAmtTable.setWidths(arrheaderwidths7B);

			int nCount = 0;
			Iterator<String> itr1 = salaryHeadName.iterator();
			while (itr1.hasNext()) {

				String strHeadNM = itr1.next();

				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQ2.setBorderWidth(0);
					GrossHeadTable.addCell(cellQ2);

					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidthTop(1);
					cellQ3.setBorderWidthBottom(0);
					cellQ3.setBorderWidthLeft(0);
					cellQ3.setBorderWidthRight(0);
					grossAmtTable.addCell(cellQ3);

				} else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));

					cellQ2.setBorderWidth(0);
					GrossHeadTable.addCell(cellQ2);

					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidth(0);
					grossAmtTable.addCell(cellQ3);
				}

				nCount++;

			}

			grossTitalTable.addCell(GrossHeadTable);
			grossTitalTable.addCell(grossAmtTable);

			PdfPTable netGrossTable = new PdfPTable(2);
			int[] arrheaderwidths6A = { 50, 50 }; // percentage
			netGrossTable.getDefaultCell().setBorderWidth(1);
			netGrossTable.getDefaultCell().setPadding(1);
			netGrossTable.setWidths(arrheaderwidths6A);

			PdfPCell cellQA1 = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

			cellQA1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellQA1.setBorderWidth(1);
			netGrossTable.addCell(cellQA1);

			
			PdfPCell cellQA2 = new PdfPCell(new Phrase(" DEDUCTIONS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

			cellQA2.setBorderWidth(1);
			cellQA2.setHorizontalAlignment(Element.ALIGN_CENTER);
			netGrossTable.addCell(cellQA2);

			PdfPTable leaveTable = new PdfPTable(2);
			int[] arrheaderwidths8 = { 60, 40 }; // percentage
			leaveTable.getDefaultCell().setBorderWidth(1);
			leaveTable.getDefaultCell().setPadding(1);
			leaveTable.setWidths(arrheaderwidths8);

			PdfPTable leaveHeadTable = new PdfPTable(1);
			int[] arrheaderwidths8A = { 100 }; // percentage
			leaveHeadTable.getDefaultCell().setBorderWidth(1);
			leaveHeadTable.getDefaultCell().setPadding(1);
			leaveHeadTable.setWidths(arrheaderwidths8A);

			PdfPTable leaveAmtTable = new PdfPTable(1);
			int[] arrheaderwidths8B = { 100 }; // percentage
			leaveAmtTable.getDefaultCell().setBorderWidth(1);
			leaveAmtTable.getDefaultCell().setPadding(1);
			leaveAmtTable.setWidths(arrheaderwidths8B);

			int nCountGross = 0;
			Iterator<String> itr12 = salaryHeadName.iterator();
			while (itr12.hasNext()) {

				String strHeadNM = itr12.next();

				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
					leaveHeadTable.addCell(cellQ2);

					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

					cellQ3.setBorderWidthTop(1);
					cellQ3.setBorderWidthBottom(0);
					cellQ3.setBorderWidthLeft(0);
					cellQ3.setBorderWidthRight(0);
					leaveAmtTable.addCell(cellQ3);
				} else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,
							FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					leaveHeadTable.addCell(cellQ2);
					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));

					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

					cellQ3.setBorderWidth(0);
					leaveAmtTable.addCell(cellQ3);

				}

				nCountGross++;

			}

			leaveTable.addCell(leaveHeadTable);
			leaveTable.addCell(leaveAmtTable);

			PdfPTable Deductiontable = new PdfPTable(2);
			int[] arrheaderwidths9 = { 50, 50 }; // percentage
			Deductiontable.getDefaultCell().setBorderWidth(1);
			Deductiontable.getDefaultCell().setPadding(1);
			Deductiontable.setWidths(arrheaderwidths9);

			PdfPCell cellQ3 = new PdfPCell(grossTitalTable);
			cellQ3.setBorderWidth(1);
			Deductiontable.addCell(cellQ3);

			PdfPCell cellR = new PdfPCell(leaveTable);
			cellR.setBorderWidth(1);
			Deductiontable.addCell(cellR);

			PdfPTable leaveDtable = new PdfPTable(2);
			int[] arrheaderwidths10 = { 60, 40 }; // percentage
			leaveDtable.getDefaultCell().setBorderWidth(0);
			leaveDtable.getDefaultCell().setPadding(1);
			leaveDtable.setWidths(arrheaderwidths10);

			PdfPTable leaveDHead = new PdfPTable(1);
			int[] arrheaderwidths10A = { 100 }; // percentage
			leaveDHead.getDefaultCell().setBorderWidth(1);
			leaveDHead.getDefaultCell().setPadding(1);
			leaveDHead.setWidths(arrheaderwidths10A);

			PdfPTable leaveDAmt = new PdfPTable(1);
			int[] arrheaderwidths10B = { 100 }; // percentage
			leaveDAmt.getDefaultCell().setBorderWidth(1);
			leaveDAmt.getDefaultCell().setPadding(1);
			leaveDAmt.setWidths(arrheaderwidths10B);

			int ncount = 0;
			Iterator<String> itr121 = leaveName.iterator();
			while (itr121.hasNext()) {

				String strleave = itr121.next();

				if (strleave!=null && strleave.equalsIgnoreCase("Total")) {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);

					if (leaveName.size() < deductionHeadName.size()) {
						cellQ2.setBorderWidthBottom(1);
					}
					leaveDHead.addCell(cellQ2);
				}

				else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					leaveDHead.addCell(cellQ2);

				}

				if (strleave!=null && strleave.equalsIgnoreCase("Total")) {
					PdfPCell cellQA3 = new PdfPCell(new Phrase(""+noOfLeave.get(ncount),FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);

					cellQA3.setBorderWidthTop(1);

					cellQA3.setBorderWidthLeft(1);
					cellQA3.setBorderWidthRight(0);
					if (noOfLeave.size() < deductionHeadName.size()) {
						cellQA3.setBorderWidthBottom(1);
					}
					leaveDAmt.addCell(cellQA3);

				} else {

					PdfPCell cellQA3 = new PdfPCell(new Phrase(""+ noOfLeave.get(ncount),FontFactory.getFont("Verdana", 8, Font.NORMAL)));

					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQA3.setBorderWidth(0);
					cellQA3.setBorderWidthLeft(1);
					leaveDAmt.addCell(cellQA3);

				}

				ncount++;

			}

			PdfPTable DeductionHeadTable = new PdfPTable(2);
			int[] arrheaderwidths11 = { 60, 40 }; // percentage
			DeductionHeadTable.getDefaultCell().setBorderWidth(0);
			DeductionHeadTable.getDefaultCell().setPadding(1);
			DeductionHeadTable.setWidths(arrheaderwidths11);

			PdfPTable DeductionName = new PdfPTable(1);
			int[] arrheaderwidths11A = { 100 }; // percentage
			DeductionName.getDefaultCell().setBorderWidth(0);
			DeductionName.getDefaultCell().setPadding(1);
			DeductionName.setWidths(arrheaderwidths11A);

			PdfPTable deductionAmountTable = new PdfPTable(1);
			int[] arrheaderwidths11B = { 100 }; // percentage
			deductionAmountTable.getDefaultCell().setBorderWidth(0);
			deductionAmountTable.getDefaultCell().setPadding(1);
			deductionAmountTable.setWidths(arrheaderwidths11B);

			int nCountDeductionAmt = 0;
			Iterator<String> itr122 = deductionHeadName.iterator();
			while (itr122.hasNext()) {

				String strDeductionNm = itr122.next();

				if (strDeductionNm!=null && strDeductionNm.equalsIgnoreCase("Total Deduction")) {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strDeductionNm,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
					if (deductionHeadName.size() < leaveName.size()) {
						cellQ2.setBorderWidthBottom(1);
					}

					DeductionName.addCell(cellQ2);
					
					PdfPCell cellQA3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountDeductionAmt)),FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQA3.setBorderWidthTop(1);

					if (deductionHeadName.size() < leaveName.size()) {
						cellQA3.setBorderWidthBottom(1);
					}

					cellQA3.setBorderWidthLeft(1);
					cellQA3.setBorderWidthRight(0);
					deductionAmountTable.addCell(cellQA3);
				} else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strDeductionNm,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					DeductionName.addCell(cellQ2);

					PdfPCell cellQA3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountDeductionAmt)),FontFactory.getFont("Verdana", 8, Font.NORMAL)));

					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);

					cellQA3.setBorderWidth(0);
					if (deductionHeadName.size() < leaveName.size()) {
						cellQA3.setBorderWidthBottom(1);
					}

					cellQA3.setBorderWidthLeft(1);
					deductionAmountTable.addCell(cellQA3);
				}

				nCountDeductionAmt++;
			}

			PdfPCell cellQA3 = new PdfPCell(new Phrase("", FontFactory.getFont("Verdana", 12, Font.NORMAL)));
			cellQA3.setBorderWidth(0);
			cellQA3.setBorderWidthTop(0);

			if (leaveName.size() != deductionHeadName.size())

			{

				if (leaveName.size() > deductionHeadName.size()) {

					for (int i = 0; i < (leaveName.size() - deductionHeadName.size()); i++) {
						DeductionName.addCell(cellQA3);
						deductionAmountTable.addCell(cellQA3);
					}

				} else {
					for (int i = 0; i < (deductionHeadName.size() - leaveName.size()); i++) {
						leaveDHead.addCell(cellQA3);
						leaveDAmt.addCell(cellQA3);
					}

				}

			}

			/**=================   START   ============================
			 *  Balance Leave Details
			 */
			
			PdfPCell cellQA4 = null;
			cellQA4 = new PdfPCell(new Phrase("Balance Leaves", FontFactory.getFont("Verdana", 8, Font.BOLDITALIC)));
			cellQA4.setBorderWidth(0);
			cellQA4.setBorderWidthTop(0);
			leaveDHead.addCell(cellQA4);
			cellQA4 = new PdfPCell(new Phrase("  ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellQA4.setBorderWidth(0);
			cellQA4.setBorderWidthTop(0);
			leaveDAmt.addCell(cellQA4);
			
			
			Set set = hmBalanceLeave.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()){
				String strLeaveId = (String)it.next();
				
				
				cellQA4 = new PdfPCell(new Phrase(uF.showData((String)hmLeaveNameMap.get(strLeaveId), ""), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQA4.setBorderWidth(0);
				cellQA4.setBorderWidthTop(0);
				leaveDHead.addCell(cellQA4);
				
				cellQA4 = new PdfPCell(new Phrase(uF.showData((String)hmBalanceLeave.get(strLeaveId), ""), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQA4.setBorderWidth(0); uF.formatIntoTwoDecimal(dblNetSalary);
				cellQA4.setBorderWidthTop(0);
				leaveDAmt.addCell(cellQA4);

				
			}
			
			/**=================   END   ============================
			 * 
			 */

			DeductionHeadTable.addCell(DeductionName);
			DeductionHeadTable.addCell(deductionAmountTable);

			PdfPTable HeadleaveDeductionTable = new PdfPTable(2);
			int[] arrheaderwidths12 = { 50, 50 }; // percentage
			HeadleaveDeductionTable.getDefaultCell().setBorderWidth(1);
			HeadleaveDeductionTable.getDefaultCell().setPadding(1);
			HeadleaveDeductionTable.setWidths(arrheaderwidths12);

			PdfPCell cellU = new PdfPCell(leaveDtable);
			cellU.setBorderWidth(1);
			HeadleaveDeductionTable.addCell(cellU);

			PdfPCell cellV = new PdfPCell(DeductionHeadTable);
			cellV.setBorderWidth(1);
			HeadleaveDeductionTable.addCell(cellV);

			PdfPCell celld = new PdfPCell(new PdfPCell(empTable));

			PdfPTable NetSalaryTable = new PdfPTable(3);
			int[] arrheaderwidths13 = { 15,20,65}; // percentage
			NetSalaryTable.getDefaultCell().setBorderWidth(1);
			NetSalaryTable.getDefaultCell().setPadding(1);
			NetSalaryTable.setWidths(arrheaderwidths13);

			PdfPCell cellUv = new PdfPCell(new Phrase(" Net Salary :",FontFactory.getFont("Verdana", 9, Font.BOLD)));

			cellUv.setBorderWidth(1);
			NetSalaryTable.addCell(cellUv);
			
			PdfPCell cellVu = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(dblNetSalary),FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellVu.setBorderWidth(1);
			NetSalaryTable.addCell(cellVu);
			
			
			String strAmountInWord=null;
			
			String	StrnetAmount=uF.formatIntoTwoDecimal(dblNetSalary);
			
			double dblAmount=uF.parseToDouble(StrnetAmount);
			
			int nNetamount=(int)dblAmount;
			
			strAmountInWord=constNumToLetter(nNetamount);
			
			strAmountInWord=strAmountInWord.concat(" Rupees");
			double npreci=(dblAmount-nNetamount);
			npreci=npreci*100;

			int  nprecision=(int)npreci;
	
			if(nprecision>=0)
			{
				strAmountInWord=strAmountInWord.concat(" And");
				strAmountInWord=strAmountInWord.concat(constNumToLetter(nprecision));
				strAmountInWord=strAmountInWord.concat(" Paise");
			
			}
			
			
			PdfPCell cellwordAmt = new PdfPCell(new Phrase(strAmountInWord,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellwordAmt.setBorderWidth(1);
			cellwordAmt.setHorizontalAlignment(Element.ALIGN_RIGHT);
			NetSalaryTable.addCell(cellwordAmt);
				
			PdfPTable signatureTable = new PdfPTable(1);
			int[] arrheaderwidths14 = { 100 }; // percentage
			signatureTable.getDefaultCell().setBorderWidth(1);
			signatureTable.getDefaultCell().setPadding(1);

			signatureTable.setWidths(arrheaderwidths14);
			
			
			int	nyeardevfirst=nYear;
			nyeardevfirst=nYear%10;
			nYear=nYear/10;
			int	nyeardevsecond=nYear%10;
			PdfPCell cellUvU = new PdfPCell(new Phrase("This is computer generated PaySlip and does not require signature.",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellUvU.setBorderWidth(1);
			signatureTable.addCell(cellUvU);

			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));

			imageEmpDetailTable.addCell(celld);
			document.add(LogoImage);
			document.add(titalTable);
			document.add(imageEmpDetailTable);
			document.add(netGrossTital);
			document.add(Deductiontable);
			document.add(netGrossTable);
			document.add(HeadleaveDeductionTable);
			document.add(NetSalaryTable);
			document.add(signatureTable);
			
			document.close();
				
			
			if(isAttachment()) {
				/*byte[] bytes = baos.toByteArray();
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_NEW_PAYSLIP_GENERATED, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(strEmpId);
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName("PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
				nF.setEmailTemplate(true);
				nF.sendNotifications();*/
			} else {
				response.setContentType("application/pdf");
				response.setContentLength(baos.size());
				response.setHeader("Content-Disposition","attachment; filename=PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
				ServletOutputStream out = response.getOutputStream();              
				baos.writeTo(out);
				out.flush();
				out.close();
				baos.close();
				out.close();
				return;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
			
	}

	private void createSalarySlipPdfSecondFormat(String strEmpId, String strMonth, Map<String, String> hmOrganisationDetails) {
		UtilityFunctions uF = new UtilityFunctions();
		try {
			List<Integer> alList=(List<Integer>)request.getAttribute("alList");
			Map<String,String> hmempcnt =(Map<String,String> )request.getAttribute("hmempcnt");
			Map<Integer,Map<String,String>> hmMap=(Map<Integer,Map<String,String>>)request.getAttribute("hmMap");
			if(hmMap==null)hmMap=new HashMap<Integer, Map<String,String>>();
			
			Map<String,String> hmEPFChallan=(Map<String,String>)request.getAttribute("hmEPFChallan");
			
			Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			Map<String,String> hmFeatureStatus = (Map<String,String>)request.getAttribute("hmFeatureStatus");
			
			com.itextpdf.text.Font heading = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 13);
			com.itextpdf.text.Font normal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11);
			com.itextpdf.text.Font normalwithbold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			com.itextpdf.text.Font small = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7);
			com.itextpdf.text.Font smallBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7,Font.BOLD);
			com.itextpdf.text.Font italicEffect = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
	        com.itextpdf.text.pdf.PdfWriter pdfWriter = com.itextpdf.text.pdf.PdfWriter.getInstance(document,buffer);
	        if(hmFeatureStatus != null &&uF.parseToBoolean(hmFeatureStatus.get(F_PASSWORD_PROTECTED_SALARY_SLIP))){
				pdfWriter.setEncryption(strPassword.getBytes(), strPassword.getBytes(), com.itextpdf.text.pdf.PdfWriter.ALLOW_PRINTING, com.itextpdf.text.pdf.PdfWriter.ENCRYPTION_AES_128);
			}
	        document.open();
	        
	        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(10);
			table.setWidthPercentage(100);       
			int[] cols = {3,10,10,10,10,10,10,10,10,10};
			table.setWidths(cols);
	        
			String strCompanyName=hmOrganisationDetails.get("ORG_NAME");
			String orgId =hmOrganisationDetails.get("ORG_ID");
			
			com.itextpdf.text.pdf.PdfPCell row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(strCompanyName,smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	      
	      //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(strCompanyAddress,small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);
	        table.addCell(row1);
	        
	      //New Row
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Payslip for the month of  : "+uF.showData(monthName, ""),smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM);
	        row1.setColspan(10);
	        table.addCell(row1);
	      

	        String heading2="EMPLOYEE DETAILS";
	      //New Row
             row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(heading2,smallBold));
             row1.setHorizontalAlignment(Element.ALIGN_CENTER);
             row1.setBorder(Rectangle.NO_BORDER);
             row1.setColspan(10);
             row1.setPadding(2.5f);
             table.addCell(row1);
	        
           //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Emp Code : "+uF.showData(empDetails.get(2), ""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	      
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("PAN : "+uF.showData(empDetails.get(10), ""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Location : "+uF.showData(empDetails.get(7), ""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);	
	        row1.setColspan(5);	
	        table.addCell(row1);
	      
	      //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Department Name : "+uF.showData(empDetails.get(12), ""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Name : "+uF.showData(empDetails.get(0), "") ,small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	      
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Designation : "+uF.showData(empDetails.get(1),""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	
	        table.addCell(row1);
	      
	      
	      //New Row
	        String heading3="PAYMENT DETAILS";
	        
           row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(heading3,smallBold));
           row1.setHorizontalAlignment(Element.ALIGN_CENTER);
           row1.setBorder(Rectangle.TOP);
           row1.setColspan(10);
           row1.setPadding(2.5f);
           table.addCell(row1);
           
         //New Row
           row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Bank Name : "+uF.showData(bankName, ""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	      
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Bank Account No. : "+uF.showData(empDetails.get(4),""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(5);	  
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Mode of Payment : "+uF.showData(strPaymentMode,""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);	        
	        table.addCell(row1);
	      
	      
	      //New Row
           String heading4="ATTENDENCE DETAILS";
	        
           row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(heading4,smallBold));
           row1.setHorizontalAlignment(Element.ALIGN_CENTER);
           row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
           row1.setColspan(10);
           row1.setPadding(2.5f);
           table.addCell(row1);
	      
         //New Row  
           row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total Days : "+tDays,small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	       
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Unpaid Days : "+unpaidDays,small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        table.addCell(row1);
	       
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Paid Days : "+pDays,small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        
	       
	      //New Row  
           String heading5="SALARY DETAILS";
	        
           row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(heading5,smallBold));
           row1.setHorizontalAlignment(Element.ALIGN_CENTER);
           row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
           row1.setColspan(10);
           row1.setPadding(2.5f);
           table.addCell(row1);
           
           
         //New Row  
           row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Earnings",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(3);	        
	        table.addCell(row1);
	        
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Amount",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(2);	 
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Deductions",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(3);	 
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Amount",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(2);	 
	        table.addCell(row1);
	        
	        
	        com.itextpdf.text.pdf.PdfPTable table1 = new com.itextpdf.text.pdf.PdfPTable(5);
	        table1.setWidthPercentage(100);   

	        Double totalEarning = salHeadAmount.get(salHeadAmount.size() - 1);
	        if(salaryHeadName.contains("Total")){
	        	salaryHeadName.remove(salaryHeadName.size() - 1);
	        	salHeadAmount.remove(salHeadAmount.size() - 1);
	        }
	        
			for(int i=0;i<salaryHeadName.size();i++) {
				
				com.itextpdf.text.pdf.PdfPCell rowinner1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(salaryHeadName.get(i),small));
				rowinner1.setPadding(2.5f);
				rowinner1.setHorizontalAlignment(Element.ALIGN_LEFT);
				rowinner1.setBorder(Rectangle.NO_BORDER);
				rowinner1.setColspan(3);
		        table1.addCell(rowinner1);
		        
//		        System.out.println("Salary Name :"+salaryHeadName.get(i));
		        
		        com.itextpdf.text.pdf.PdfPCell rowinner2 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(Double.toString(salHeadAmount.get(i)),small));
				rowinner2.setPadding(2.5f);
				rowinner2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				rowinner2.setBorder(Rectangle.NO_BORDER);
				rowinner2.setColspan(2);
		        table1.addCell(rowinner2);
		        
//		        System.out.println("Salary Ammount :"+salHeadAmount.get(i));
		      
			}
			//New Row  
           row1 =new com.itextpdf.text.pdf.PdfPCell(table1);
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	        
	        
	        com.itextpdf.text.pdf.PdfPTable table2 = new com.itextpdf.text.pdf.PdfPTable(5);
	        table2.setWidthPercentage(100);       

	        Double totalDeduction =  deductionHeadAmount.get(deductionHeadAmount.size() - 1);
	        if(deductionHeadName.contains("Total Deduction")){
           deductionHeadName.remove(deductionHeadName.size() - 1);
	        deductionHeadAmount.remove(deductionHeadAmount.size() - 1);
	        }
	        
           for(int i=0;i<deductionHeadName.size();i++) {
				
           	com.itextpdf.text.pdf.PdfPCell rowinner1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(deductionHeadName.get(i),small));
				rowinner1.setPadding(2.5f);
				rowinner1.setHorizontalAlignment(Element.ALIGN_LEFT);
				rowinner1.setBorder(Rectangle.NO_BORDER);
				rowinner1.setColspan(3);
		        table2.addCell(rowinner1);
		        
//		        System.out.println("Deduction Name : "+deductionHeadName.get(i));
		        
		        com.itextpdf.text.pdf.PdfPCell rowinner2 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(Double.toString(deductionHeadAmount.get(i)),small));
				rowinner2.setPadding(2.5f);
				rowinner2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				rowinner2.setBorder(Rectangle.NO_BORDER);
				rowinner2.setColspan(2);
		        table2.addCell(rowinner2);
		        
//		        System.out.println("Deduction Ammount : "+deductionHeadAmount.get(i));
		      
			}
       
	        row1 =new com.itextpdf.text.pdf.PdfPCell(table2);
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(5);	        
	        table.addCell(row1);
	     
	        //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Gross Earning  : ",smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(3);	        
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "+totalEarning,smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(2);	        
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Gross Deduction : ",smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(3);	 
	        table.addCell(row1);
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" "+totalDeduction,smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(2);	 
	        table.addCell(row1);
	       
	        //New Row
	        
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Net Amount :           "+uF.formatIntoTwoDecimal(dblNetSalary),smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(10);	 
	        table.addCell(row1);
	      
	        
	        int	nyeardevfirst=nYear;
			nyeardevfirst=nYear%10;
			nYear=nYear/10;
			int	nyeardevsecond=nYear%10;
	        
	        //New Row
	        row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Computer generated hence signature not required",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);	 
	        table.addCell(row1);
	        
	                
	        document.add(table);
	        
	        document.close();
	        
			String filename="PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf";
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename="+filename+"");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
		
	}catch (Exception e) {
		e.printStackTrace();  
	}
		
	}

	public void  pdfCreationPayslip(String strEmpId, String strMonth, Map<String, String> hmOrganisationDetails){
		UtilityFunctions uF = new UtilityFunctions();
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
//			String strCompanyLogo=CF.getStrOrgLogo();
			String strCompanyLogo=hmOrganisationDetails.get("ORG_LOGO");
			String strCompanyName=hmOrganisationDetails.get("ORG_NAME");
			String orgId =hmOrganisationDetails.get("ORG_ID");
			Map<String,String> hmFeatureStatus = (Map<String,String>)request.getAttribute("hmFeatureStatus");
			
			Document document = new Document();
			PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
			if(hmFeatureStatus != null &&uF.parseToBoolean(hmFeatureStatus.get(F_PASSWORD_PROTECTED_SALARY_SLIP))){
				pdfWriter.setEncryption(strPassword.getBytes(), strPassword.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
			}
			document.open();
			document.add(new Paragraph(" "));

			
			String filePath = request.getRealPath("/userImages/" +strEmpImage+ "");
			String filePathDefault=request.getRealPath("/userImages/avatar_photo.png");
			String filePathCompanyLOgo = request.getRealPath("/userImages/"+strCompanyLogo+"");
			String filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
			String filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
			
			/*String filePath = null;
			String filePathDefault=null;
			String filePathCompanyLOgo = null;
			String filePathCompanyLOgodefault=null;
			String filePathproductLogo= null;*/
			
//			if(CF.getIsRemoteLocation()){
			if(CF.getStrDocSaveLocation()!=null){
//				filePath = CF.getStrDocRetriveLocation() +strEmpImage;
//				filePath = CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+ strEmpId+"/"+strEmpImage;
				filePath = CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+ strEmpId+"/"+strEmpImage;
				filePathDefault=request.getRealPath("/userImages/avatar_photo.png");
				
//				filePathCompanyLOgo = CF.getStrDocRetriveLocation()+strCompanyLogo;
//				filePathCompanyLOgo = CF.getStrDocRetriveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+strCompanyLogo;
				filePathCompanyLOgo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+strCompanyLogo;
				filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
				filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
			}else{
				filePath = request.getRealPath("/userImages/" +strEmpImage+ "");
				filePathDefault=request.getRealPath("/userImages/avatar_photo.png");
				filePathCompanyLOgo = request.getRealPath("/userImages/"+strCompanyLogo+"");
				filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
				filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
			}  
//			System.out.println("filePath=====>"+filePath);
			Image imagePhoto=null;
			Image imageLogo=null;
			//Image imageProductLogo=Image.getInstance(filePathproductLogo);
			try{
//				System.out.println("filePath========>"+filePath); 
				
				FileInputStream fileInputStream=null;
		        File file = new File(filePath);
		        byte[] bFile = new byte[(int) file.length()];
		        fileInputStream = new FileInputStream(file);
			    fileInputStream.read(bFile);
			    fileInputStream.close();
			    imagePhoto = Image.getInstance(bFile);
				
//				imagePhoto = Image.getInstance(filePath);
//				imagePhoto = Image.getInstance(filePathDefault);
			}catch(FileNotFoundException e){
				imagePhoto = Image.getInstance(filePathDefault);
			}
			
			try{
//				System.out.println("filePathCompanyLOgo========>"+filePathCompanyLOgo);
				
				FileInputStream fileInputStream1=null;
		        File file1 = new File(filePathCompanyLOgo);
		        byte[] bFile1 = new byte[(int) file1.length()];
		        fileInputStream1 = new FileInputStream(file1);
			    fileInputStream1.read(bFile1);
			    fileInputStream1.close();
		        imageLogo = Image.getInstance(bFile1);
		        
//				imageLogo = Image.getInstance(filePathCompanyLOgo);
//				imageLogo = Image.getInstance(filePathCompanyLOgodefault); 
			}catch(FileNotFoundException e){
				imageLogo = Image.getInstance(filePathCompanyLOgodefault);
			}
			
			PdfPTable photoImagetable = new PdfPTable(1);
			photoImagetable.setWidthPercentage(10);
			photoImagetable.getDefaultCell().setPadding(1);

			photoImagetable.addCell(imagePhoto);

			PdfPTable companyNameTable = new PdfPTable(1);
			int[] arrheaderwidthsh = { 100 }; // percentage
			companyNameTable.getDefaultCell().setBorderWidth(0);
			companyNameTable.setWidths(arrheaderwidthsh);
			/*PdfPCell companyNamecell = new PdfPCell(new Phrase(
					CF.getStrOrgName(), FontFactory.getFont("Verdana", 14,
							Font.BOLD)));*/
			PdfPCell companyNamecell = new PdfPCell(new Phrase(strCompanyName, FontFactory.getFont("Verdana", 14,Font.BOLD)));
			

			companyNamecell.setBorderWidthTop(0);
			companyNamecell.setBorderWidthBottom(1);
			companyNamecell.setBorderWidthLeft(0);
			companyNamecell.setBorderWidthRight(0);

			companyNamecell.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell);
			PdfPCell companyNamecell1 = new PdfPCell(new Phrase(strCompanyAddress, FontFactory.getFont("Verdana", 10,Font.BOLD)));

			companyNamecell1.setBorderWidthTop(0);
			companyNamecell1.setBorderWidthBottom(0);
			companyNamecell1.setBorderWidthLeft(0);
			companyNamecell1.setBorderWidthRight(0);

			companyNamecell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell1);

			PdfPTable LogoImage = new PdfPTable(2);
			int[] arrheaderwidths1 = { 70, 30 }; // percentage
			LogoImage.getDefaultCell().setBorderWidth(1);
			LogoImage.setWidths(arrheaderwidths1);
			LogoImage.setTotalWidth(600); 
			LogoImage.addCell(companyNameTable);
			LogoImage.addCell(imageLogo);

			PdfPTable titalTable = new PdfPTable(1);
			int[] arrheaderwidths2 = { 100 }; // percentage
			titalTable.getDefaultCell().setBorderWidth(1);
			titalTable.getDefaultCell().setPadding(1);
			titalTable.setWidths(arrheaderwidths2);

			PdfPCell cellb = new PdfPCell(new Phrase(" PAY SLIP ",FontFactory.getFont("Verdana", 15, Font.BOLD)));

			cellb.getExtraParagraphSpace();
			cellb.setBorderWidth(1);
			cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
			titalTable.addCell(cellb);

			PdfPTable empTable = new PdfPTable(6);

			int[] arrheaderwidths5 = { 31, 2, 30, 15, 2, 20 }; // percentage
			empTable.getDefaultCell().setBorderWidth(1);

			empTable.setWidths(arrheaderwidths5);
			empTable.setTotalWidth(800);
			
//			Iterator<String> itr = empDetails.iterator();
			Iterator<String> itr = payEmpHead.iterator();
			int k = 0;

//			while (itr.hasNext()) {
			
			for(; k<payEmpHead.size(); k++){
			
				PdfPCell cellE = new PdfPCell(new Phrase(payEmpHead.get(k),FontFactory.getFont("Verdana", 7, Font.BOLD)));
//				System.out.println(k+"----payEmpHead.get(k)====>"+payEmpHead.get(k)+"---empDetails.get(k)====>"+empDetails.get(k));
				cellE.setBorderWidth(0);
				empTable.addCell(cellE);

				if(payEmpHead.get(k).isEmpty()){
					PdfPCell cellcollan = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 7, Font.BOLD)));
					cellcollan.setBorderWidth(0);
					empTable.addCell(cellcollan);
				}else{
					PdfPCell cellcollan = new PdfPCell(new Phrase(":",FontFactory.getFont("Verdana", 7, Font.BOLD)));
					cellcollan.setBorderWidth(0);
					empTable.addCell(cellcollan);
					
				}
				PdfPCell cellF = new PdfPCell(new Phrase(empDetails.get(k),FontFactory.getFont("Verdana", 7, Font.NORMAL)));

				cellF.setBorderWidth(0);
				empTable.addCell(cellF);
//				k++;
			}

			PdfPTable imageEmpDetailTable = new PdfPTable(2);

			int[] arrheaderwidths4 = { 12, 88 }; // percentage

			imageEmpDetailTable.getDefaultCell().setBorderWidth(1);
			imageEmpDetailTable.getDefaultCell().setPadding(1);
			imageEmpDetailTable.setWidths(arrheaderwidths4);

			imageEmpDetailTable.addCell(photoImagetable);
			PdfPTable netGrossTital = new PdfPTable(2);
			int[] arrheaderwidths6 = { 50, 50 }; // percentage
			netGrossTital.getDefaultCell().setBorderWidth(1);
			netGrossTital.getDefaultCell().setPadding(1);
			netGrossTital.setWidths(arrheaderwidths6);

			PdfPCell cellQ = new PdfPCell(new Phrase("GROSS EARNINGS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

			cellQ.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellQ.setBorderWidth(1);
			netGrossTital.addCell(cellQ);

			PdfPCell cellQ1 = new PdfPCell(new Phrase("NET EARNINGS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellQ1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellQ1.setBorderWidth(1);
			netGrossTital.addCell(cellQ1);

			PdfPTable grossTitalTable = new PdfPTable(2);

			int[] arrheaderwidths7 = { 60, 40 }; // percentage
			grossTitalTable.getDefaultCell().setBorderWidth(1);
			grossTitalTable.getDefaultCell().setPadding(1);
			grossTitalTable.setWidths(arrheaderwidths7);

			PdfPTable GrossHeadTable = new PdfPTable(1);
			int[] arrheaderwidths7A = { 100 }; // percentage
			GrossHeadTable.getDefaultCell().setBorderWidth(1);
			GrossHeadTable.getDefaultCell().setPadding(1);
			GrossHeadTable.setWidths(arrheaderwidths7A);

			PdfPTable grossAmtTable = new PdfPTable(1);
			int[] arrheaderwidths7B = { 100 }; // percentage
			grossAmtTable.getDefaultCell().setBorderWidth(1);
			grossAmtTable.getDefaultCell().setPadding(1);
			grossAmtTable.setWidths(arrheaderwidths7B);

			int nCount = 0;
			Iterator<String> itr1 = salaryHeadName.iterator();
			while (itr1.hasNext()) {

				String strHeadNM = itr1.next();

				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQ2.setBorderWidth(0);
					GrossHeadTable.addCell(cellQ2);

//					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount))+" "+CF.getStrCURRENCY_FULL(), FontFactory.getFont("Verdana", 8, Font.BOLD)));
					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

	  
					
					cellQ3.setBorderWidthTop(1);
					cellQ3.setBorderWidthBottom(0);
					cellQ3.setBorderWidthLeft(0);
					cellQ3.setBorderWidthRight(0); 
					grossAmtTable.addCell(cellQ3);

				} else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));

					cellQ2.setBorderWidth(0);
					GrossHeadTable.addCell(cellQ2);

					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));

//					PdfPCell cellQ3 = new PdfPCell(new Phrase("--G--"
//							+ uF.formatIntoTwoDecimal(uF.parseToDouble(alGross
//									.get(nCount))), FontFactory.getFont(
//							"Verdana", 8, Font.NORMAL)));

					
					
					
					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

					cellQ3.setBorderWidth(0);

					grossAmtTable.addCell(cellQ3);
				}

				nCount++;

			}

			grossTitalTable.addCell(GrossHeadTable);
			grossTitalTable.addCell(grossAmtTable);

			PdfPTable netGrossTable = new PdfPTable(2);
			int[] arrheaderwidths6A = { 50, 50 }; // percentage
			netGrossTable.getDefaultCell().setBorderWidth(1);
			netGrossTable.getDefaultCell().setPadding(1);
			netGrossTable.setWidths(arrheaderwidths6A);

			
			
			//  Removed for Wai -- -Vipin 28/02/2013
//			PdfPCell cellQA1 = new PdfPCell(new Phrase("LEAVES TAKEN ",
//					FontFactory.getFont("Verdana", 9, Font.BOLD)));
			PdfPCell cellQA1 = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

			cellQA1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellQA1.setBorderWidth(1);
			netGrossTable.addCell(cellQA1);

			
			
			
			PdfPCell cellQA2 = new PdfPCell(new Phrase(" DEDUCTIONS ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

			cellQA2.setBorderWidth(1);
			cellQA2.setHorizontalAlignment(Element.ALIGN_CENTER);
			netGrossTable.addCell(cellQA2);

			PdfPTable leaveTable = new PdfPTable(2);
			int[] arrheaderwidths8 = { 60, 40 }; // percentage
			leaveTable.getDefaultCell().setBorderWidth(1);
			leaveTable.getDefaultCell().setPadding(1);
			leaveTable.setWidths(arrheaderwidths8);

			PdfPTable leaveHeadTable = new PdfPTable(1);
			int[] arrheaderwidths8A = { 100 }; // percentage
			leaveHeadTable.getDefaultCell().setBorderWidth(1);
			leaveHeadTable.getDefaultCell().setPadding(1);
			leaveHeadTable.setWidths(arrheaderwidths8A);

			PdfPTable leaveAmtTable = new PdfPTable(1);
			int[] arrheaderwidths8B = { 100 }; // percentage
			leaveAmtTable.getDefaultCell().setBorderWidth(1);
			leaveAmtTable.getDefaultCell().setPadding(1);
			leaveAmtTable.setWidths(arrheaderwidths8B);

			int nCountGross = 0;
			Iterator<String> itr12 = salaryHeadName.iterator();
			while (itr12.hasNext()) {

				String strHeadNM = itr12.next();

				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
					leaveHeadTable.addCell(cellQ2);

//					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmount.get(nCountGross))+" "+CF.getStrCURRENCY_FULL(), FontFactory.getFont("Verdana", 8, Font.BOLD)));
					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

					cellQ3.setBorderWidthTop(1);
					cellQ3.setBorderWidthBottom(0);
					cellQ3.setBorderWidthLeft(0);
					cellQ3.setBorderWidthRight(0);
					leaveAmtTable.addCell(cellQ3);
				} else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM, FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					leaveHeadTable.addCell(cellQ2);
					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));

					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

					cellQ3.setBorderWidth(0);
					leaveAmtTable.addCell(cellQ3);

				}

				nCountGross++;

			}

			leaveTable.addCell(leaveHeadTable);
			leaveTable.addCell(leaveAmtTable);

			PdfPTable Deductiontable = new PdfPTable(2);
			int[] arrheaderwidths9 = { 50, 50 }; // percentage
			Deductiontable.getDefaultCell().setBorderWidth(1);
			Deductiontable.getDefaultCell().setPadding(1);
			Deductiontable.setWidths(arrheaderwidths9);

			PdfPCell cellQ3 = new PdfPCell(grossTitalTable);
			cellQ3.setBorderWidth(1);
			Deductiontable.addCell(cellQ3);

			PdfPCell cellR = new PdfPCell(leaveTable);
			cellR.setBorderWidth(1);
			Deductiontable.addCell(cellR);

			PdfPTable leaveDtable = new PdfPTable(2);
			int[] arrheaderwidths10 = { 60, 40 }; // percentage
			leaveDtable.getDefaultCell().setBorderWidth(0);
			leaveDtable.getDefaultCell().setPadding(1);
			leaveDtable.setWidths(arrheaderwidths10);

			PdfPTable leaveDHead = new PdfPTable(1);
			int[] arrheaderwidths10A = { 100 }; // percentage
			leaveDHead.getDefaultCell().setBorderWidth(1);
			leaveDHead.getDefaultCell().setPadding(1);
			leaveDHead.setWidths(arrheaderwidths10A);

			PdfPTable leaveDAmt = new PdfPTable(1);
			int[] arrheaderwidths10B = { 100 }; // percentage
			leaveDAmt.getDefaultCell().setBorderWidth(1);
			leaveDAmt.getDefaultCell().setPadding(1);
			leaveDAmt.setWidths(arrheaderwidths10B);

			int ncount = 0;
			Iterator<String> itr121 = leaveName.iterator();
			while (itr121.hasNext()) {

				String strleave = itr121.next();

				if (strleave!=null && strleave.equalsIgnoreCase("Total")) {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);

					if (leaveName.size() < deductionHeadName.size()) {
						cellQ2.setBorderWidthBottom(1);
					}
					leaveDHead.addCell(cellQ2);
				}

				else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strleave,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					leaveDHead.addCell(cellQ2);

				}

				if (strleave!=null && strleave.equalsIgnoreCase("Total")) {
					PdfPCell cellQA3 = new PdfPCell(new Phrase(""+noOfLeave.get(ncount),FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);

					cellQA3.setBorderWidthTop(1);

					cellQA3.setBorderWidthLeft(1);
					cellQA3.setBorderWidthRight(0);
					if (noOfLeave.size() < deductionHeadName.size()) {
						cellQA3.setBorderWidthBottom(1);
					}
					leaveDAmt.addCell(cellQA3);

				} else {

					PdfPCell cellQA3 = new PdfPCell(new Phrase(""+ noOfLeave.get(ncount),FontFactory.getFont("Verdana", 8, Font.NORMAL)));

					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQA3.setBorderWidth(0);
					cellQA3.setBorderWidthLeft(1);
					leaveDAmt.addCell(cellQA3);

				}

				ncount++;

			}

			PdfPTable DeductionHeadTable = new PdfPTable(2);
			int[] arrheaderwidths11 = { 60, 40 }; // percentage
			DeductionHeadTable.getDefaultCell().setBorderWidth(0);
			DeductionHeadTable.getDefaultCell().setPadding(1);
			DeductionHeadTable.setWidths(arrheaderwidths11);

			PdfPTable DeductionName = new PdfPTable(1);
			int[] arrheaderwidths11A = { 100 }; // percentage
			DeductionName.getDefaultCell().setBorderWidth(0);
			DeductionName.getDefaultCell().setPadding(1);
			DeductionName.setWidths(arrheaderwidths11A);

			PdfPTable deductionAmountTable = new PdfPTable(1);
			int[] arrheaderwidths11B = { 100 }; // percentage
			deductionAmountTable.getDefaultCell().setBorderWidth(0);
			deductionAmountTable.getDefaultCell().setPadding(1);
			deductionAmountTable.setWidths(arrheaderwidths11B);

			int nCountDeductionAmt = 0;
			Iterator<String> itr122 = deductionHeadName.iterator();
			while (itr122.hasNext()) {

				String strDeductionNm = itr122.next();

				if (strDeductionNm!=null && strDeductionNm.equalsIgnoreCase("Total Deduction")) {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strDeductionNm,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
					if (deductionHeadName.size() < leaveName.size()) {
						cellQ2.setBorderWidthBottom(1);
					}

					DeductionName.addCell(cellQ2);

//					PdfPCell cellQA3 = new PdfPCell(new Phrase(""
//							+ uF.formatIntoTwoDecimal(deductionHeadAmount
//									.get(nCountDeductionAmt))+" "+CF.getStrCURRENCY_FULL(),
//							FontFactory.getFont("Verdana", 8, Font.BOLD)));
					PdfPCell cellQA3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountDeductionAmt)),FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQA3.setBorderWidthTop(1);

					if (deductionHeadName.size() < leaveName.size()) {
						cellQA3.setBorderWidthBottom(1);
					}

					cellQA3.setBorderWidthLeft(1);
					cellQA3.setBorderWidthRight(0);
					deductionAmountTable.addCell(cellQA3);
				} else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strDeductionNm,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					DeductionName.addCell(cellQ2);

					PdfPCell cellQA3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountDeductionAmt)),FontFactory.getFont("Verdana", 8, Font.NORMAL)));

					cellQA3.setHorizontalAlignment(Element.ALIGN_RIGHT);

					cellQA3.setBorderWidth(0);
					if (deductionHeadName.size() < leaveName.size()) {
						cellQA3.setBorderWidthBottom(1);
					}

					cellQA3.setBorderWidthLeft(1);
					deductionAmountTable.addCell(cellQA3);
				}

				nCountDeductionAmt++;
			}

			PdfPCell cellQA3 = new PdfPCell(new Phrase("", FontFactory.getFont("Verdana", 12, Font.NORMAL)));
			cellQA3.setBorderWidth(0);
			cellQA3.setBorderWidthTop(0);

			if (leaveName.size() != deductionHeadName.size())

			{

				if (leaveName.size() > deductionHeadName.size()) {

					for (int i = 0; i < (leaveName.size() - deductionHeadName.size()); i++) {
						DeductionName.addCell(cellQA3);
						deductionAmountTable.addCell(cellQA3);
					}

				} else {
					for (int i = 0; i < (deductionHeadName.size() - leaveName.size()); i++) {
						leaveDHead.addCell(cellQA3);
						leaveDAmt.addCell(cellQA3);
					}

				}

			}

			/**=================   START   ============================
			 *  Balance Leave Details
			 */
			
			PdfPCell cellQA4 = null;
			cellQA4 = new PdfPCell(new Phrase("Balance Leaves", FontFactory.getFont("Verdana", 8, Font.BOLDITALIC)));
			cellQA4.setBorderWidth(0);
			cellQA4.setBorderWidthTop(0);
			leaveDHead.addCell(cellQA4);
			cellQA4 = new PdfPCell(new Phrase("  ", FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellQA4.setBorderWidth(0);
			cellQA4.setBorderWidthTop(0);
			leaveDAmt.addCell(cellQA4);
			
			
			Set set = hmBalanceLeave.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()){
				String strLeaveId = (String)it.next();
				
				
				cellQA4 = new PdfPCell(new Phrase(uF.showData((String)hmLeaveNameMap.get(strLeaveId), ""), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQA4.setBorderWidth(0);
				cellQA4.setBorderWidthTop(0);
				leaveDHead.addCell(cellQA4);
				
				cellQA4 = new PdfPCell(new Phrase(uF.showData((String)hmBalanceLeave.get(strLeaveId), ""), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellQA4.setBorderWidth(0); uF.formatIntoTwoDecimal(dblNetSalary);
				cellQA4.setBorderWidthTop(0);
				leaveDAmt.addCell(cellQA4);

				
			}
			
			/**=================   END   ============================
			 * 
			 */
			
			
			//  Removed for Wai -- -Vipin 28/02/2013
//			leaveDtable.addCell(leaveDHead);
//			leaveDtable.addCell(leaveDAmt);

			DeductionHeadTable.addCell(DeductionName);
			DeductionHeadTable.addCell(deductionAmountTable);

			PdfPTable HeadleaveDeductionTable = new PdfPTable(2);
			int[] arrheaderwidths12 = { 50, 50 }; // percentage
			HeadleaveDeductionTable.getDefaultCell().setBorderWidth(1);
			HeadleaveDeductionTable.getDefaultCell().setPadding(1);
			HeadleaveDeductionTable.setWidths(arrheaderwidths12);

			PdfPCell cellU = new PdfPCell(leaveDtable);
			cellU.setBorderWidth(1);
			HeadleaveDeductionTable.addCell(cellU);

			PdfPCell cellV = new PdfPCell(DeductionHeadTable);
			cellV.setBorderWidth(1);
			HeadleaveDeductionTable.addCell(cellV);

			PdfPCell celld = new PdfPCell(new PdfPCell(empTable));

			PdfPTable NetSalaryTable = new PdfPTable(3);
			int[] arrheaderwidths13 = { 15,20,65}; // percentage
			NetSalaryTable.getDefaultCell().setBorderWidth(1);
			NetSalaryTable.getDefaultCell().setPadding(1);
			NetSalaryTable.setWidths(arrheaderwidths13);

			PdfPCell cellUv = new PdfPCell(new Phrase(" Net Salary :",FontFactory.getFont("Verdana", 9, Font.BOLD)));

			cellUv.setBorderWidth(1);
			NetSalaryTable.addCell(cellUv);

//			PdfPCell cellVu = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(dblNetSalary)+" "+CF.getStrCURRENCY_FULL(),
//					FontFactory.getFont("Verdana", 9, Font.BOLD)));
//			 		cellVu.setBorderWidth(1);
//			        NetSalaryTable.addCell(cellVu);
			
			PdfPCell cellVu = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(dblNetSalary),FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellVu.setBorderWidth(1);
			NetSalaryTable.addCell(cellVu);
			
			
			String strAmountInWord=null;
			
			String	StrnetAmount=uF.formatIntoTwoDecimal(dblNetSalary);
			
			double dblAmount=uF.parseToDouble(StrnetAmount);
			
			int nNetamount=(int)dblAmount;
			
			strAmountInWord=constNumToLetter(nNetamount);
			
			strAmountInWord=strAmountInWord.concat(" Rupees");
			double npreci=(dblAmount-nNetamount);
			npreci=npreci*100;

			int  nprecision=(int)npreci;
	
			if(nprecision>=0)
			{
				strAmountInWord=strAmountInWord.concat(" And");
				strAmountInWord=strAmountInWord.concat(constNumToLetter(nprecision));
				strAmountInWord=strAmountInWord.concat(" Paise");
			
			}
			
			
			PdfPCell cellwordAmt = new PdfPCell(new Phrase(strAmountInWord,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellwordAmt.setBorderWidth(1);
			cellwordAmt.setHorizontalAlignment(Element.ALIGN_RIGHT);
			NetSalaryTable.addCell(cellwordAmt);
				
			PdfPTable signatureTable = new PdfPTable(1);
			int[] arrheaderwidths14 = { 100 }; // percentage
			signatureTable.getDefaultCell().setBorderWidth(1);
			signatureTable.getDefaultCell().setPadding(1);

			signatureTable.setWidths(arrheaderwidths14);
			
			
			int	nyeardevfirst=nYear;
			nyeardevfirst=nYear%10;
			nYear=nYear/10;
			int	nyeardevsecond=nYear%10;
			PdfPCell cellUvU = new PdfPCell(new Phrase("This is computer generated PaySlip and does not require signature.",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellUvU.setBorderWidth(1);
			signatureTable.addCell(cellUvU);

			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));

			imageEmpDetailTable.addCell(celld);
			document.add(LogoImage);
			document.add(titalTable);
			document.add(imageEmpDetailTable);
			document.add(netGrossTital);
			document.add(Deductiontable);
			document.add(netGrossTable);
			document.add(HeadleaveDeductionTable);
			document.add(NetSalaryTable);
			document.add(signatureTable);

		//	Image imagexy = Image.getInstance(filePath1);
			
			/*Image imageProductLogo=Image.getInstance(filePathproductLogo);

			imageProductLogo.setAbsolutePosition(445, 0);
			imageProductLogo.scaleToFit(150, 150);
			document.add(imageProductLogo);*/
			
			document.close();
				
			
					
			if(isAttachment()){ 
				/*byte[] bytes = baos.toByteArray();
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_NEW_PAYSLIP_GENERATED, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(strEmpId);
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName("PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
				nF.setEmailTemplate(true);
				nF.sendNotifications();*/
			}else{
				response.setContentType("application/pdf");
				response.setContentLength(baos.size());
				response.setHeader("Content-Disposition","attachment; filename=PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
				ServletOutputStream out = response.getOutputStream();              
				baos.writeTo(out);
				out.flush();
				out.close();
				baos.close();
				out.close();
				
				
				return;
			}
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
			
	}
	
//===created by parvez date: 02-09-2022===
	//===start===
	private void createSalarySlipPdfEightFormat(String strEmpId, String strMonth) {
		
		Database dB = new Database();
		dB.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		con = dB.makeConnection(con);
		boolean flagEpf = false;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmTemp =null;
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpDepartmentMap = CF.getDepartmentMap(con,null, null);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
//			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con);
			Map<String, String> hmBankNameMap = CF.getBankNameMap(con, uF);
			int nEmpLevelId = CF.getEmpLevelId(getStrEmpId(), request);
			
			Map<String, String> hmOrganisationDetails = new HashMap<String, String>();
			
			pst =con.prepareStatement("select max(entry_date) as entry_date, paid_from, paid_to,paid_days,total_days ,payment_mode,month,year " +
					"from payroll_generation where emp_id = ? and financial_year_from_date=? AND financial_year_to_date=? AND month=? " +
					"and paycycle=? and (bank_pay_type=? or bank_pay_type is null) group by paid_from, paid_to,paid_days,total_days,payment_mode,month,year");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(getStrPC()));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
//			System.out.println("pst 5-1 ===>> " + pst);
			rs = pst.executeQuery();
			String strEntryDate = null;
			String strPayCycleStart = null;
			String strPayCycleEnd = null;
			String strTotal_days =null;
			String strPaid_days = null;
			String strPresent_days = null;
			while(rs.next()){
				strEntryDate = rs.getString("entry_date");
				strPayCycleStart = rs.getString("paid_from");
				strPayCycleEnd = rs.getString("paid_to");
			//===start parvez date: 04-03-2023===	
//				strTotal_days =rs.getString("total_days");
//				strPaid_days =rs.getString("paid_days");
			//===end parvez date: 04-03-2023===	
			//	strPresent_days = rs.getString("present_days");
				strPaymentMode =rs.getString("payment_mode");
				 pMonth =rs.getInt("month");
				 pYear =rs.getInt("year");
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 04-03-2023===
			pst =con.prepareStatement("select * from approve_attendance where emp_id = ? and financial_year_start=? AND financial_year_end=? and paycycle=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrPC()));
			rs = pst.executeQuery();
			while(rs.next()) {
				strTotal_days =rs.getString("total_days");
				strPaid_days =rs.getString("paid_days");
			}
			rs.close();
			pst.close();
		//===end parvez date: 04-03-2023===
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
//				"and isdisplay=true and effective_date<=? group by level_id"); 
			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true and " +
					"isdisplay=true and effective_date = (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
					"and isdisplay=true and effective_date<=?) group by level_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
//			System.out.println("pst ================>> " + pst);
			String strEffectiveDate = null;
			String strEmpLevelId = null;
			rs = pst.executeQuery();
			while(rs.next()){
				strEffectiveDate = rs.getString("effective_date");
				strEmpLevelId = rs.getString("level_id");
			}
			rs.close();
			pst.close();
//			System.out.println("strEmpLevelId ===>> " + strEmpLevelId);
			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(strEmpLevelId));			
			
			
			pst =con.prepareStatement("select erpf_contribution, erps_contribution,erdli_contribution,pf_admin_charges,edli_admin_charges from emp_epf_details where emp_id = ? and financial_year_start=? and financial_year_end=? and _month =? and paycycle=? ");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(getStrPC()));
//			System.out.println("====>pst"+pst);
//			System.out.println("************************pst**************"+pst);
			rs = pst.executeQuery();
			double erpf_contb = 0 ;
			double erps_contb = 0;
			double erdli_contib = 0;
			double pFadminChrges = 0;
			double edliAdminChrges = 0;
			double total = 0;
			while(rs.next()) {
				erpf_contb =rs.getDouble("erpf_contribution");
				erps_contb =rs.getDouble("erps_contribution");
				erdli_contib =rs.getDouble("erdli_contribution");
				pFadminChrges =rs.getDouble("pf_admin_charges");
				edliAdminChrges =rs.getDouble("edli_admin_charges");
			}
			rs.close();
			pst.close();
			
			total =erpf_contb + erps_contb + erdli_contib+pFadminChrges+edliAdminChrges;
//			System.out.println("===>Total"+total);
//			System.out.println("***********************total***********************"+total);
			
		    monthName = uF.getMonth(pMonth)+" "+pYear;
		   
	//===start parvez date: 04-03-2023===	    
//           tDays = uF.parseToInt(strTotal_days);
//           pDays = uF.parseToInt(strPaid_days);
           tDays = uF.parseToDouble(strTotal_days);
           pDays = uF.parseToDouble(strPaid_days);
    //===end parvez date: 04-03-2023===       
           
           unpaidDays = tDays - pDays;
           
	            
			if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("1")){
				strPaymentMode ="Bank Transfer";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cash";	
			}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
				strPaymentMode ="Cheque";	
			}
				
	            
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
		
			pst = con.prepareStatement("select * from org_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while(rs.next()){
				hmOrganisationDetails.put("ORG_ID", rs.getString("org_id"));
				hmOrganisationDetails.put("ORG_LOGO", rs.getString("org_logo"));
				hmOrganisationDetails.put("ORG_NAME", rs.getString("org_name"));
				hmOrganisationDetails.put("PDF_TYPE", rs.getString("payslip_format"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id  = eod.emp_id and emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map hmWorkLocationMap = CF.getWorkLocationMap(con);
				hmTemp = (Map<String, String>)hmWorkLocationMap.get(rs.getInt("wlocation_id")+"");
				if(hmTemp==null)hmTemp=new HashMap();
//				strCompanyAddress = hmTemp.get("WL_ADDRESS")+" " +hmTemp.get("WL_CITY")+" " +hmTemp.get("WL_PINCODE");
				strCompanyAddress = hmTemp.get("WL_ADDRESS")+"\n" +hmTemp.get("WL_CITY")+"-" +hmTemp.get("WL_PINCODE");
				
				strLevelId = hmEmpLevelMap.get(rs.getString("emp_id"));
				
				empDetails.add(rs.getString("empcode"));
				empDetails.add(" ");
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				String strEmpFName = rs.getString("emp_fname").substring(0, 1).toUpperCase()+rs.getString("emp_fname").substring(1);
				strPassword = strEmpFName+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd")+uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM");
				
				empDetails.add(rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname"));
				empDetails.add(strTotal_days);
//				empDetails.add(uF.showData(hmBankNameMap.get(rs.getString("emp_bank_name")), "-"));
				empDetails.add(hmEmpDesigMap.get(rs.getString("emp_id")));
				empDetails.add(hmEmpDepartmentMap.get(rs.getString("depart_id")));
				empDetails.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				empDetails.add("0.00");
//				empDetails.add(uF.showData(rs.getString("emp_pan_no"), "-"));
				empDetails.add("");
				empDetails.add(strPaid_days);
				empDetails.add("");
				empDetails.add(uF.showData(rs.getString("emp_pf_no"), "-"));
				
			}
			rs.close();
			pst.close();
		
			List alLoans = new ArrayList();
			Map hmEmpLoan = new HashMap();
			Map<String, String> hmEmpSalLastEffectiveDate = null; //need to implement this map
			CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, uF.getDateFormat(strPayCycleStart, DBDATE, DATE_FORMAT), uF.getDateFormat(strPayCycleEnd, DBDATE, DATE_FORMAT), hmEmpSalLastEffectiveDate);
			Map hmEmpLoanInner = (Map)hmEmpLoan.get(strEmpId); 
			if(hmEmpLoanInner==null) hmEmpLoanInner=new HashMap();
			Map<String, String> hmLoanPolicies = CF.getLoanPoliciesMap(con, uF, strOrgId);
//			String strEmpLevelId = CF.getEmpLevelId(con, getStrEmpId());

			Map<String, List<Map<String, String>>> hmPerkAlign = CF.getIndividualPerkAlignAmount(con, uF, CF, uF.parseToInt(getStrEmpId()),getStrFYS(),getStrFYE(),getStrPCS(),getStrPCE(),uF.parseToInt(getStrPC()));
			if(hmPerkAlign == null) hmPerkAlign = new HashMap<String, List<Map<String,String>>>();
			
			
			pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND pg.financial_year_from_date=? " +
				"AND pg.financial_year_to_date=? and (bank_pay_type=? or bank_pay_type is null) and pg.salary_head_id in (select distinct(salary_head_id) as salary_head_id from " +
				"emp_salary_details where isdisplay=true and emp_id=? and level_id=?) order by earning_deduction desc,pg.salary_head_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrMonth()));
			pst.setInt(3, uF.parseToInt(getStrPC()));
			pst.setDate(4, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			pst.setInt(7, uF.parseToInt(getStrEmpId()));
			pst.setInt(8, uF.parseToInt(strEmpLevelId));
//			System.out.println("pst 5-2 ===>> " + pst);
           rs=pst.executeQuery();    
          double presentDays = 0;
          while (rs.next()) {
       	   presentDays = Double.parseDouble(rs.getString("present_days"));
				Double dblTotal = 0.0;
				Double dblTotalGrossAmt = 0.0;
				if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
					dblTotal = rs.getDouble("amount"); //OTHER ALLOWANCE/EX-
					dblTotalGrossAmt = rs.getDouble("amount");
					
					if(hmPerkAlign.containsKey(rs.getString("salary_head_id"))){
						List<Map<String, String>> alPerkAlign = hmPerkAlign.get(rs.getString("salary_head_id"));
						if(alPerkAlign == null) alPerkAlign = new ArrayList<Map<String,String>>();
						int nPerkAlignSize = alPerkAlign.size();
						for(int i = 0; i < nPerkAlignSize; i++) {
							Map<String, String> hmPerkAlignInner = alPerkAlign.get(i);
							if(hmPerkAlignInner == null) hmPerkAlignInner = new HashMap<String, String>();
							salaryHeadName.add(uF.showData(hmPerkAlignInner.get("PERK_NAME"), "")+" ("+hmSalaryDetailsMap.get(rs.getString("salary_head_id"))+")");
							salHeadAmount.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
							salHeadAmountGross.add(uF.parseToDouble(hmPerkAlignInner.get("PERK_SALARY_AMOUNT")));
						}
						
					} else {
						salaryHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						salHeadAmount.add(dblTotal);
						salHeadAmountGross.add(dblTotalGrossAmt);
					}										
					
					dblTotalAmt += dblTotal;
					dblGrossTotal += dblTotalGrossAmt;

				} else if (rs.getString("earning_deduction").equalsIgnoreCase("D")) {
					
					if(uF.parseToInt(rs.getString("salary_head_id"))==LOAN){
						for(int i=0; i<alLoans.size(); i++){
							String loan=(String)alLoans.get(i)!=null?(String)alLoans.get(i) : "";
							String loanamt=uF.showData((String)hmEmpLoanInner.get(loan), "0");
							dblTotal = uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt)));
							deductionHeadName.add(hmLoanPolicies.get((String)alLoans.get(i)));
							deductionHeadAmount.add(uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(loanamt))));
							dblTotalDeduction += dblTotal;
						}
						
					}else{
						dblTotal = rs.getDouble("amount");
						deductionHeadName.add(hmSalaryDetailsMap.get(rs.getString("salary_head_id")));
						deductionHeadAmount.add(dblTotal);
						dblTotalDeduction += dblTotal;
					}
					
				}
				
				nYear=rs.getInt("year");
				int salaryHeadId =Integer.parseInt(rs.getString("salary_head_id"));
				if(salaryHeadId == IConstants.EMPLOYEE_EPF){
					flagEpf = true; 
				}
			}     
			rs.close();
			pst.close(); 
			
			
			dblNetSalary = dblTotalAmt - dblTotalDeduction;
			
			
			salHeadAmountGross.add(dblGrossTotal);

			Double totalEarning = salHeadAmount.get(salHeadAmount.size() - 1);
			
			Double totalDeduction =  deductionHeadAmount.get(deductionHeadAmount.size() - 1);
			
	        
			pst = con.prepareStatement("select sum(leave_no) as leave_no, lar.leave_type_id from leave_application_register lar, leave_type lt where lar.leave_type_id = lt.leave_type_id and leave_id in (select leave_id from emp_leave_entry where emp_id=?) and _date between ? and ? group by lar.leave_type_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strPayCycleStart, DBDATE));
			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
			rs = pst.executeQuery();
			totalLeave.add(String.valueOf(presentDays));
			String leaveNo = null ;
			int leaveType = 0;
			while(rs.next()){
				leaveNo = rs.getString("leave_no");
				leaveType = Integer.parseInt(rs.getString("leave_type_id"));
				
				leaveTypeName.add(leaveType);
				totalLeave.add(leaveNo);
				
				hmLeaveType.put(leaveTypeName, totalLeave);
			
			}
		
			
			payEmpHead.add("Emp No.");
			payEmpHead.add("");
			payEmpHead.add("Employee Name");
			payEmpHead.add("Calendar Days");
//			payEmpHead.add("Bank Name ");
			payEmpHead.add("Designation");
			payEmpHead.add("Department");
			payEmpHead.add("Joining Date");
			payEmpHead.add("Leave Days");
			payEmpHead.add("PAN No.");
			payEmpHead.add("Paid Days");
			payEmpHead.add("");
			payEmpHead.add("PF No.");
			
			
			Map hmLeaveDatesType = new HashMap();
			Map<String, Map<String, String>> leaveDetailsMap = CF.getActualLeaveDates(con, CF, uF, getStrFYS(), getStrFYE(),  hmLeaveDatesType, true, null);
			Map<String, String> leaveEmpDetailsMap = leaveDetailsMap.get(getStrEmpId());

			if(leaveEmpDetailsMap!=null) {
				Set keys = leaveEmpDetailsMap.keySet();
				for (Iterator i = keys.iterator(); i.hasNext();) {
					String nkey = (String) i.next();
					String strValue = (String) leaveEmpDetailsMap.get(nkey);
		
					Iterator<String> itr = leaveName.iterator();	
					int count=0;
					while(itr.hasNext()) {
						if(strValue.equalsIgnoreCase(itr.next())) {
							count=1;
			           	 }
					}
				
					if(count==0) {
						leaveName.add(strValue);
					}
				}
				
				Iterator<String> itrleave = leaveName.iterator();	
				int nleaveToatal=0;
				while(itrleave.hasNext()) {				
					Set keysC = leaveEmpDetailsMap.keySet();
					String strLeave = itrleave.next();
					int nleaveCount=0;		
						
					for (Iterator i = keysC.iterator(); i.hasNext();) {
						String nkey = (String) i.next();
						String strValue = (String) leaveEmpDetailsMap.get(nkey);
							
						if(strValue!=null && strValue.equalsIgnoreCase(strLeave)) {
							nleaveCount++;
								
							nleaveToatal++;
						}
					}  
					noOfLeave.add(nleaveCount);
				}	  
					
				noOfLeave.add(nleaveToatal);
				leaveName.add("Total");
			}
			
			
			pst = con.prepareStatement("select sum(accrued_leaves) as accru, sum(taken_leaves) as taken_leaves, (sum(accrued_leaves) - sum(taken_leaves)) as balance, leave_type_id from leave_register where emp_id = ? group by leave_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				hmBalanceLeave.put(rs.getString("leave_type_id"), rs.getString("balance"));
			}
			rs.close();
			pst.close();
			hmLeaveNameMap = CF.getLeaveTypeMap(con);
//			System.out.println("Total===>"+total);
			generateSalarySlipEighththFormat(getStrEmpId(),getStrMonth(), hmOrganisationDetails,strTotal_days,presentDays,dblNetSalary,totalDeduction,totalEarning,monthName,total,flagEpf,hmTemp);
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			dB.closeResultSet(rs);
			dB.closeStatements(pst);
			dB.closeConnection(con);
		}
	
	}
	//===end===
	
	
	//===cerated by parvez date: 02-09-2022===
	//===start===
	private void generateSalarySlipEighththFormat(String strEmpId, String strMonth, Map<String, String> hmOrganisationDetails, String strTotal_days, double presentDays, Double dblNetSalary, Double totalDeduction, Double totalEarning, String monthName, 
			double total,boolean flagEpf, Map<String, String> hmTemp) {
		UtilityFunctions uF = new UtilityFunctions();

		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			String strCompanyLogo=hmOrganisationDetails.get("ORG_LOGO");
			String strCompanyName=hmOrganisationDetails.get("ORG_NAME");
			String orgId =hmOrganisationDetails.get("ORG_ID");
			Map<String,String> hmFeatureStatus = (Map<String,String>)request.getAttribute("hmFeatureStatus");
			
			Document document = new Document();
//			PdfWriter.getInstance(document, baos);
			PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
			if(hmFeatureStatus != null &&uF.parseToBoolean(hmFeatureStatus.get(F_PASSWORD_PROTECTED_SALARY_SLIP))){
				pdfWriter.setEncryption(strPassword.getBytes(), strPassword.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
			}
			document.open();
			document.add(new Paragraph(" "));

			String filePath = null;
			String filePathCompanyLOgo = null;
			String filePathCompanyLOgodefault=null;
			String filePathproductLogo= null;
			
			if(CF.getStrDocSaveLocation()!=null){
				filePath = CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+ strEmpId+"/"+strEmpImage;
				
//				System.out.println("strCompanyLogo=="+strCompanyLogo);
				filePathCompanyLOgo = CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+I_IMAGE+"/"+strCompanyLogo;
				filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
				filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
			}else{
				filePath = request.getRealPath("/userImages/" +strEmpImage+ "");
				filePathCompanyLOgo = request.getRealPath("/userImages/"+strCompanyLogo+"");
				filePathCompanyLOgodefault=request.getRealPath("/userImages/Konnect_technologies.png");
				filePathproductLogo= request.getRealPath("/userImages/logo_new.png");
			}  
			Image imageLogo=null;
			
			try{
//				System.out.println("filePathCompanyLOgo=="+filePathCompanyLOgo);
				
				FileInputStream fileInputStream1=null;
		        File file1 = new File(filePathCompanyLOgo);
		        byte[] bFile1 = new byte[(int) file1.length()];
		        fileInputStream1 = new FileInputStream(file1);
			    fileInputStream1.read(bFile1);
			    fileInputStream1.close();
		        imageLogo = Image.getInstance(bFile1);
		        
			}catch(FileNotFoundException e){
				imageLogo = Image.getInstance(filePathCompanyLOgodefault);
			}
			
			PdfPTable photoImagetable = new PdfPTable(1);
			photoImagetable.setWidthPercentage(10);
			photoImagetable.getDefaultCell().setPadding(1);


			PdfPTable companyNameTable = new PdfPTable(1);
			int[] arrheaderwidthsh = { 100 }; // percentage
			companyNameTable.getDefaultCell().setBorderWidth(0);
			companyNameTable.setWidths(arrheaderwidthsh);
			PdfPCell companyNamecell = new PdfPCell(new Phrase(strCompanyName, FontFactory.getFont("Verdana", 14,Font.BOLD,new BaseColor(29,108,128))));
			

			companyNamecell.setBorderWidthTop(0);
			companyNamecell.setBorderWidthBottom(1);
			companyNamecell.setBorderWidthLeft(0);
			companyNamecell.setBorderWidthRight(0);

			companyNamecell.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell);
			
			if(hmTemp==null)hmTemp=new HashMap();
			
			PdfPCell companyNamecell1 = new PdfPCell(new Phrase(strCompanyAddress, FontFactory.getFont("Verdana", 10,Font.BOLD)));
//			PdfPCell companyNamecell1 = new PdfPCell(new Phrase(hmTemp.get("WL_ADDRESS")+"\n"+hmTemp.get("WL_CITY")+"\n"+hmTemp.get("WL_PINCODE"), FontFactory.getFont("Verdana", 10,Font.BOLD)));

			companyNamecell1.setBorderWidthTop(0);
			companyNamecell1.setBorderWidthBottom(0);
			companyNamecell1.setBorderWidthLeft(0);
			companyNamecell1.setBorderWidthRight(0);

			companyNamecell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			companyNameTable.addCell(companyNamecell1);

			PdfPTable LogoImage = new PdfPTable(2);
			int[] arrheaderwidths1 = { 30, 70 }; // percentage
			LogoImage.getDefaultCell().setBorderWidth(1);
			LogoImage.setWidths(arrheaderwidths1);
			LogoImage.setTotalWidth(600); 
//			System.out.println("imageLogo="+imageLogo);
			LogoImage.addCell(imageLogo);
			LogoImage.addCell(companyNameTable);
			

			PdfPTable titalTable = new PdfPTable(1);
			int[] arrheaderwidths2 = {100}; // percentage
			titalTable.getDefaultCell().setBorderWidth(0);
			titalTable.getDefaultCell().setPadding(0);
			titalTable.setWidths(arrheaderwidths2);

			PdfPCell cellb = new PdfPCell(new Phrase("Pay Slip for the Month:-  "+monthName,FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellb.getExtraParagraphSpace();
			cellb.setBorderWidth(1);
			cellb.setHorizontalAlignment(Element.ALIGN_CENTER);
			titalTable.addCell(cellb);
			
			
			PdfPTable empTable = new PdfPTable(6);

			int[] arrheaderwidths5 = { 31, 2, 30, 15, 2, 20 }; // percentage
			empTable.getDefaultCell().setBorderWidth(1);

			empTable.setWidths(arrheaderwidths5);
			empTable.setTotalWidth(800);
			
			Iterator<String> itr = payEmpHead.iterator();
			int k = 0;

			
			for(; k<payEmpHead.size(); k++){
//				System.out.println("payEmpHead=="+payEmpHead.get(k)+"----"+empDetails.get(k));
			
				PdfPCell cellE = new PdfPCell(new Phrase(payEmpHead.get(k),FontFactory.getFont("Verdana", 8, Font.BOLD)));
				
				cellE.setBorderWidth(0);
				empTable.addCell(cellE);

				if(payEmpHead.get(k).isEmpty()){
					PdfPCell cellcollan = new PdfPCell(new Phrase(" ",FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellcollan.setBorderWidth(0);
					empTable.addCell(cellcollan);
					}
				else{	
					PdfPCell cellcollan = new PdfPCell(new Phrase(":",FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellcollan.setBorderWidth(0);
					empTable.addCell(cellcollan);
					}

				PdfPCell cellF = new PdfPCell(new Phrase(empDetails.get(k),FontFactory.getFont("Verdana", 8, Font.NORMAL)));
				cellF.setBorderWidth(0);
				empTable.addCell(cellF);
			}

			PdfPTable imageEmpDetailTable = new PdfPTable(1);

			int[] arrheaderwidths4 = { 100 }; // percentage

			imageEmpDetailTable.getDefaultCell().setBorderWidth(1);
			imageEmpDetailTable.getDefaultCell().setPadding(1);
			imageEmpDetailTable.setWidths(arrheaderwidths4);
			
			PdfPTable titleTable = new PdfPTable(1);
			int[] arrTitle = { 100}; // percentage
			titleTable.getDefaultCell().setBorderWidth(1);
			titleTable.getDefaultCell().setPadding(1);
			titleTable.setWidths(arrTitle);

			PdfPCell cellT = new PdfPCell(new Phrase("           Gross Earnings and Amount(INR)                               Deductions and Amount(INR)",FontFactory.getFont("Verdana", 9, Font.BOLD)));

			cellT.setHorizontalAlignment(Element.ALIGN_LEFT);
			cellT.setBorderWidth(1);
			titleTable.addCell(cellT);
			
			
			PdfPTable grossTitalTable = new PdfPTable(2);

			int[] arrheaderwidths7 = { 60,40 }; // percentage
			grossTitalTable.getDefaultCell().setBorderWidth(1);
			grossTitalTable.getDefaultCell().setPadding(1);
			grossTitalTable.setWidths(arrheaderwidths7);

			PdfPTable GrossHeadTable = new PdfPTable(1);
			int[] arrheaderwidths7A = { 100 }; // percentage
			GrossHeadTable.getDefaultCell().setBorderWidth(1);
			GrossHeadTable.getDefaultCell().setPadding(1);
			GrossHeadTable.setWidths(arrheaderwidths7A);

			PdfPTable grossAmtTable = new PdfPTable(1);
			int[] arrheaderwidths7B = { 100 }; // percentage
			grossAmtTable.getDefaultCell().setBorderWidth(1);
			grossAmtTable.getDefaultCell().setPadding(1);
			grossAmtTable.setWidths(arrheaderwidths7B);
			
			
			PdfPTable totalGrossTitalTable = new PdfPTable(2);
			int[] arrheaderwidths77 = { 60,40 }; // percentage
			totalGrossTitalTable.getDefaultCell().setBorderWidth(1);
			totalGrossTitalTable.getDefaultCell().setPadding(1);
			totalGrossTitalTable.setWidths(arrheaderwidths77);
			
			PdfPTable totalGrossHeadTable = new PdfPTable(1);
			int[] arrheaderwidths7AA = { 100 }; // percentage
			totalGrossHeadTable.getDefaultCell().setBorderWidth(1);
			totalGrossHeadTable.getDefaultCell().setPadding(1);
			totalGrossHeadTable.setWidths(arrheaderwidths7AA);

			PdfPTable totalGrossAmtTable = new PdfPTable(1);
			int[] arrheaderwidths7BB = { 100 }; // percentage
			totalGrossAmtTable.getDefaultCell().setBorderWidth(1);
			totalGrossAmtTable.getDefaultCell().setPadding(1);
			totalGrossAmtTable.setWidths(arrheaderwidths7BB);

			int nCount = 0;
			Iterator<String> itr1 = salaryHeadName.iterator();
			while (itr1.hasNext()) {

				String strHeadNM = itr1.next();
//				System.out.println("strHeadNM="+strHeadNM);

				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Total")) {
					
					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
					GrossHeadTable.addCell(cellQ2);
					

					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidthTop(0);
					cellQ3.setBorderWidthBottom(0);
					cellQ3.setBorderWidthLeft(0);
					cellQ3.setBorderWidthRight(0);
					grossAmtTable.addCell(cellQ3);

				} else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					GrossHeadTable.addCell(cellQ2);

					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(salHeadAmountGross.get(nCount)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellQ3.setBorderWidth(0);
					grossAmtTable.addCell(cellQ3);
				}

				nCount++;

			}
			
			PdfPCell cellQ22 = new PdfPCell(new Phrase("Total Gross Earnings (INR)",FontFactory.getFont("Verdana", 8, Font.BOLD)));
			cellQ22.setBorderWidth(0);
			totalGrossHeadTable.addCell(cellQ22);
			
			PdfPCell cellQ33 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(dblTotalAmt), FontFactory.getFont("Verdana", 8, Font.BOLD)));

			cellQ33.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cellQ33.setBorderWidthTop(0);
			cellQ33.setBorderWidthBottom(0);
			cellQ33.setBorderWidthLeft(0);
			cellQ33.setBorderWidthRight(0);
			totalGrossAmtTable.addCell(cellQ33);

			grossTitalTable.addCell(GrossHeadTable);
//			grossTitalTable.addCell(tableScale);
			grossTitalTable.addCell(grossAmtTable);
			
			totalGrossTitalTable.addCell(totalGrossHeadTable);
			totalGrossTitalTable.addCell(totalGrossAmtTable);
			

	//*************************************************************************		
			PdfPTable netGrossTable = new PdfPTable(2);
			int[] arrheaderwidths6A = { 50, 50 }; // percentage
			netGrossTable.getDefaultCell().setBorderWidth(1);
			netGrossTable.getDefaultCell().setPadding(1);
			netGrossTable.setWidths(arrheaderwidths6A);

			PdfPCell cellQA1 = new PdfPCell(new Phrase("Organisations Contribution ",FontFactory.getFont("Verdana", 9, Font.BOLD)));

			cellQA1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellQA1.setBorderWidth(1);
			netGrossTable.addCell(cellQA1);

			
			PdfPCell cellQA2 = new PdfPCell(new Phrase("",FontFactory.getFont("Verdana", 9, Font.BOLD))); //Payment By Bank A/C No 

			cellQA2.setBorderWidth(1);
			cellQA2.setHorizontalAlignment(Element.ALIGN_CENTER);
			netGrossTable.addCell(cellQA2);
			
			//*************************************************************************

			
			
			PdfPTable leaveTable = new PdfPTable(2);
			int[] arrheaderwidths8 = { 60, 40 }; // percentage
			leaveTable.getDefaultCell().setBorderWidth(1);
			leaveTable.getDefaultCell().setPadding(1);
			leaveTable.setWidths(arrheaderwidths8);

			PdfPTable leaveHeadTable = new PdfPTable(1);
			int[] arrheaderwidths8A = { 100 }; // percentage
			leaveHeadTable.getDefaultCell().setBorderWidth(1);
			leaveHeadTable.getDefaultCell().setPadding(1);
			leaveHeadTable.setWidths(arrheaderwidths8A);

			PdfPTable leaveAmtTable = new PdfPTable(1);
			int[] arrheaderwidths8B = { 100 }; // percentage
			leaveAmtTable.getDefaultCell().setBorderWidth(1);
			leaveAmtTable.getDefaultCell().setPadding(1);
			leaveAmtTable.setWidths(arrheaderwidths8B);
			
			
			PdfPTable totalLeaveTable = new PdfPTable(2);
			int[] arrheaderwidths8888 = { 60, 40 }; // percentage
			totalLeaveTable.getDefaultCell().setBorderWidth(1);
			totalLeaveTable.getDefaultCell().setPadding(1);
			totalLeaveTable.setWidths(arrheaderwidths8888);
			
			PdfPTable totalLeaveHeadTable = new PdfPTable(1);
			int[] arrheaderwidths8AA = { 100 }; // percentage
			totalLeaveHeadTable.getDefaultCell().setBorderWidth(1);
			totalLeaveHeadTable.getDefaultCell().setPadding(1);
			totalLeaveHeadTable.setWidths(arrheaderwidths8AA);

			PdfPTable totalLeaveAmtTable = new PdfPTable(1);
			int[] arrheaderwidths8BB = { 100 }; // percentage
			totalLeaveAmtTable.getDefaultCell().setBorderWidth(1);
			totalLeaveAmtTable.getDefaultCell().setPadding(1);
			totalLeaveAmtTable.setWidths(arrheaderwidths8BB);

			int nCountGross = 0;
			Iterator<String> itr12 = deductionHeadName.iterator();
			while (itr12.hasNext()) {

				String strHeadNM = itr12.next();

				if (strHeadNM!=null && strHeadNM.equalsIgnoreCase("Deductions") || strHeadNM.equalsIgnoreCase("Net Payable")) {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.BOLD)));
					cellQ2.setBorderWidth(0);
					leaveHeadTable.addCell(cellQ2);

					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.BOLD)));

					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

					cellQ3.setBorderWidthTop(0);
					cellQ3.setBorderWidthBottom(0);
					cellQ3.setBorderWidthLeft(0);
					cellQ3.setBorderWidthRight(0);
					leaveAmtTable.addCell(cellQ3);
				} else {

					PdfPCell cellQ2 = new PdfPCell(new Phrase(strHeadNM, FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellQ2.setBorderWidth(0);
					leaveHeadTable.addCell(cellQ2);
					PdfPCell cellQ3 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(deductionHeadAmount.get(nCountGross)), FontFactory.getFont("Verdana", 8, Font.NORMAL)));

					cellQ3.setHorizontalAlignment(Element.ALIGN_RIGHT);

					cellQ3.setBorderWidth(0);
					leaveAmtTable.addCell(cellQ3);

				}

				nCountGross++;

			}
			
			PdfPCell cellQ222 = new PdfPCell(new Phrase("Total Deductions (INR)",FontFactory.getFont("Verdana", 8, Font.BOLD)));
			cellQ222.setBorderWidth(0);
			totalLeaveHeadTable.addCell(cellQ222);

			PdfPCell cellQ333 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(dblTotalDeduction), FontFactory.getFont("Verdana", 8, Font.BOLD)));

			cellQ333.setHorizontalAlignment(Element.ALIGN_RIGHT);

			cellQ333.setBorderWidthTop(0);
			cellQ333.setBorderWidthBottom(0);
			cellQ333.setBorderWidthLeft(0);
			cellQ333.setBorderWidthRight(0);
			totalLeaveAmtTable.addCell(cellQ333);
			
			
			PdfPCell cellQ55 = new PdfPCell(new Phrase("Net Payable (INR)",FontFactory.getFont("Verdana", 8, Font.BOLD)));
//			cellQ55.setBorderWidth(0);
			cellQ55.setBorderWidthTop(1);
			cellQ55.setBorderWidthBottom(0);
			cellQ55.setBorderWidthLeft(0);
			cellQ55.setBorderWidthRight(0);
			totalLeaveHeadTable.addCell(cellQ55);

			PdfPCell cellQ66 = new PdfPCell(new Phrase(""+ uF.formatIntoTwoDecimal(dblNetSalary), FontFactory.getFont("Verdana", 8, Font.BOLD)));

			cellQ66.setHorizontalAlignment(Element.ALIGN_RIGHT);

			cellQ66.setBorderWidthTop(1);
			cellQ66.setBorderWidthBottom(0);
			cellQ66.setBorderWidthLeft(0);
			cellQ66.setBorderWidthRight(0);
			totalLeaveAmtTable.addCell(cellQ66);

			leaveTable.addCell(leaveHeadTable);
			leaveTable.addCell(leaveAmtTable);
			
			totalLeaveTable.addCell(totalLeaveHeadTable);
			totalLeaveTable.addCell(totalLeaveAmtTable);
			
			//*************************************************************************
			
			PdfPTable NetSalaryTable = new PdfPTable(1);
			int[] arrheaderwidths13 = { 100}; // percentage
			NetSalaryTable.getDefaultCell().setBorderWidth(1);
			NetSalaryTable.getDefaultCell().setPadding(1);
			NetSalaryTable.setWidths(arrheaderwidths13);
			
			String strAmountInWord=null;
			
			String	StrnetAmount=uF.formatIntoTwoDecimal(dblNetSalary);
			
			double dblAmount=uF.parseToDouble(StrnetAmount);
			
			int nNetamount=(int)dblAmount;
			
			strAmountInWord=constNumToLetter(nNetamount);
			
			strAmountInWord=strAmountInWord.concat(" Rupees");
			double npreci=(dblAmount-nNetamount);
			npreci=npreci*100;

			int  nprecision=(int)npreci;
	
			if(nprecision>=0)
			{
				strAmountInWord=strAmountInWord.concat(" And");
				strAmountInWord=strAmountInWord.concat(constNumToLetter(nprecision));
				strAmountInWord=strAmountInWord.concat(" Paise");
			
			}
			
			
			PdfPCell cellwordAmt = new PdfPCell(new Phrase(strAmountInWord,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellwordAmt.setBorderWidth(1);
			cellwordAmt.setHorizontalAlignment(Element.ALIGN_RIGHT);
			NetSalaryTable.addCell(cellwordAmt);

			//*************************************************************************	
			
			PdfPTable Deductiontable = new PdfPTable(2);
			int[] arrheaderwidths9 = { 50, 50 }; // percentage
			Deductiontable.getDefaultCell().setBorderWidth(1);
			Deductiontable.getDefaultCell().setPadding(1);
			Deductiontable.setWidths(arrheaderwidths9);

			PdfPCell cellQ3 = new PdfPCell(grossTitalTable);
			cellQ3.setBorderWidth(1);
			Deductiontable.addCell(cellQ3);

			PdfPCell cellR = new PdfPCell(leaveTable);
			cellR.setBorderWidth(1);
			Deductiontable.addCell(cellR);
			
			
			PdfPTable totalDeductiontable = new PdfPTable(2);
			int[] arrheaderwidths99 = { 50, 50 }; // percentage
			totalDeductiontable.getDefaultCell().setBorderWidth(1);
			totalDeductiontable.getDefaultCell().setPadding(1);
			totalDeductiontable.setWidths(arrheaderwidths99);
			
			PdfPCell cellQ3333 = new PdfPCell(totalGrossTitalTable);
			cellQ3.setBorderWidth(1);
			totalDeductiontable.addCell(cellQ3333);

			PdfPCell cellRR = new PdfPCell(totalLeaveTable);
			cellR.setBorderWidth(1);
			totalDeductiontable.addCell(cellRR);

			

			PdfPTable HeadleaveDeductionTable = new PdfPTable(2);
			int[] arrheaderwidths12 = { 50, 50 }; // percentage
			HeadleaveDeductionTable.getDefaultCell().setBorderWidth(1);
			HeadleaveDeductionTable.getDefaultCell().setPadding(1);
			HeadleaveDeductionTable.setWidths(arrheaderwidths12);

			PdfPCell celld = new PdfPCell(new PdfPCell(empTable));

			PdfPTable orgTable = new PdfPTable(3);
			int[] arrorg = { 30, 20 ,50}; // percentage
			orgTable.getDefaultCell().setBorderWidth(1);
			orgTable.getDefaultCell().setPadding(1);
			orgTable.setWidths(arrorg);

			PdfPTable orgHeadTable = new PdfPTable(1);
			int[] arrOrgA = { 100 }; // percentage
			orgHeadTable.getDefaultCell().setBorderWidth(0);
			orgHeadTable.getDefaultCell().setPadding(1);
			orgHeadTable.setWidths(arrOrgA);

			PdfPTable orgAmtTable = new PdfPTable(1);
			int[] arrOrgB = { 100 }; // percentage
			orgAmtTable.getDefaultCell().setBorderWidth(0);
			orgAmtTable.getDefaultCell().setPadding(1);
			orgAmtTable.setWidths(arrOrgB);
			
			PdfPTable orgBlank = new PdfPTable(1);
			int[] arrOrgC = { 100 }; // percentage
			orgBlank.getDefaultCell().setBorderWidth(1);
			orgBlank.getDefaultCell().setPadding(1);
			orgBlank.setWidths(arrOrgC);

			List<String> orgHeadAmt = new ArrayList<String>();
			List<String> orgHeadName = new ArrayList<String>();
			orgHeadName.add("Employer PF (O.C)");
			//orgHeadName.add("PF (O.C)");
			orgHeadName.add("Total (O.C)");
			
			orgHeadAmt.add(String.valueOf(total));
			//orgHeadAmt.add("0.00");
			orgHeadAmt.add(String.valueOf(total));
			
			int countOrg = 0;
			Iterator<String> itrOrg = orgHeadName.iterator();
			if(flagEpf){
			while (itrOrg.hasNext()) {

				String strHeadNM = itrOrg.next();

					PdfPCell cellOrg = new PdfPCell(new Phrase(strHeadNM,FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellOrg.setBorderWidth(0);
					orgHeadTable.addCell(cellOrg);
					
					PdfPCell cellOrg1 = new PdfPCell(new Phrase(""+ orgHeadAmt.get(countOrg), FontFactory.getFont("Verdana", 8, Font.NORMAL)));
					cellOrg1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellOrg1.setBorderWidth(0);
					orgAmtTable.addCell(cellOrg1);
					
					if(countOrg <2){
					PdfPCell cellOrg2 = new PdfPCell(new Phrase(" "));
					cellOrg2.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellOrg2.setBorderWidth(0);
					orgBlank.addCell(cellOrg2);
					}
					
					countOrg++;

			}
			
		}
			PdfPCell cellOrg2 = new PdfPCell(new Phrase("Remark : ",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellOrg2.setHorizontalAlignment(Element.ALIGN_LEFT);
			cellOrg2.setBorderWidth(0);
			orgBlank.addCell(cellOrg2);
			
			
			orgTable.addCell(orgHeadTable);
			orgTable.addCell(orgAmtTable);
			orgTable.addCell(orgBlank);
			
			//*************************************************************
			
			PdfPTable LastTable = new PdfPTable(1);
			LastTable.getDefaultCell().setBorderWidth(0);
			LastTable.getDefaultCell().setPadding(0);
			LastTable.setWidths(arrheaderwidths2);

			PdfPCell cellSign = new PdfPCell(new Phrase("This is Computer generated Slip so does not required any signature",FontFactory.getFont("Verdana", 9, Font.BOLD)));
			cellSign.getExtraParagraphSpace();
			cellSign.setBorderWidth(1);
			cellSign.setHorizontalAlignment(Element.ALIGN_CENTER);
			LastTable.addCell(cellSign);
			
			
			//************************************************************
			
			int	nyeardevfirst=nYear;
			nyeardevfirst=nYear%10;
			nYear=nYear/10;
			int	nyeardevsecond=nYear%10;
			

			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));

			imageEmpDetailTable.addCell(celld);
			document.add(LogoImage);
			document.add(titalTable);
			document.add(imageEmpDetailTable);
		//	document.add(netGrossTital);
			document.add(titleTable);
			document.add(Deductiontable);
			document.add(totalDeductiontable);
			document.add(NetSalaryTable);
//			document.add(netGrossTable);
			document.add(HeadleaveDeductionTable);
		//	document.add(NetSalaryTable);
//			document.add(orgTable);
		//	document.add(signatureTable);
			document.add(LastTable);
			document.close();
				
			
			if(isAttachment()) {
				byte[] bytes = baos.toByteArray();
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_NEW_PAYSLIP_GENERATED, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(strEmpId);
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName("PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
				nF.setEmailTemplate(true);
				nF.sendNotifications();
			} else {
				response.setContentType("application/pdf");
				response.setContentLength(baos.size());
				response.setHeader("Content-Disposition","attachment; filename=PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf");
				ServletOutputStream out = response.getOutputStream();              
				baos.writeTo(out);
				out.flush();
				out.close();
				baos.close();
				out.close();
				return;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	//===end===
	
	public byte[] getImage(URL url) throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    InputStream is = url.openStream ();
	    byte[] b = new byte[4096];
	    int n;
	    while ( (n = is.read(b)) > 0 ) {
	        baos.write(b, 0, n);
	    }
	    return baos.toByteArray();
	}
	
	
		public String constNumToLetter(int nNumber)
		{
			
			int nlen, q=0, r=0;
		    String strltr = " ";
		    String strAmountString = "";
		    
		try{
		    
		    GenerateSalarySlip amt=new GenerateSalarySlip();
		
		
		    while (nNumber>0){
		
		    	nlen = amt.numberCount(nNumber);
		
		       //Take the length of the number and do letter conversion
		
		       switch (nlen){
		       
		            case 8:
		                    q=nNumber/10000000;
		                    r=nNumber%10000000;
		                    strltr = amt.twonum(q);
		                    strAmountString = strAmountString+strltr+amt.arrDigit[4];
		                    nNumber = r;
		                    break;
		
		            case 7:
		            case 6:
		                    q=nNumber/100000;
		                    r=nNumber%100000;
		                    strltr = amt.twonum(q);
		                    strAmountString = strAmountString+strltr+amt.arrDigit[3];
		                    nNumber = r;
		                    break;
		
		            case 5:
		            case 4:
		
		                     q=nNumber/1000;
		                     r=nNumber%1000;
		                     strltr = amt.twonum(q);
		                     strAmountString= strAmountString+strltr+amt.arrDigit[2];
		                     nNumber = r;
		                     break;
		
		            case 3:
		
		
		                      if (nlen == 3)
		                          r = nNumber;
		                      strltr = amt.threenum(r);
		                      strAmountString = strAmountString + strltr;
		                      nNumber = 0;
		                      break;
		
		            case 2:
		
		            	strltr = amt.twonum(nNumber);
		            	strAmountString = strAmountString + strltr;
		            	nNumber=0;
		                     break;
		
		            case 1:
		            	strAmountString = strAmountString + amt.arrUnitdo[nNumber];
		            	nNumber=0;
		                     break;
		            default:
		
		            	nNumber=0;
		                   
		                    break;
		
		
		        }
		                    if (nNumber==0)
		                    	                    	
		                    	strAmountString.concat(strAmountString);
		      }
		}catch (Exception e) {
			e.printStackTrace();
		}
		return strAmountString;
		
	}
	

	int numberCount(int num) {
		int cnt = 0;

		while (num > 0) {
			nseprate = num % 10;
			cnt++;
			num = num / 10;
		}

		return cnt;
	}

	String twonum(int numq) {
		int numr, nq;
		String ltr = "";

		nq = numq / 10;
		numr = numq % 10;

		if (numq > 19) {
			ltr = ltr + arrTens[nq] + arrUnitdo[numr];
		} else {
			ltr = ltr + arrUnitdo[numq];
		}

		return ltr;
	}

	String threenum(int numq) {
		int numr, nq;
		String ltr = "";

		nq = numq / 100;
		numr = numq % 100;

		if (numr == 0) {
			ltr = ltr + arrUnitdo[nq] + arrDigit[1];
		} else {
			ltr = ltr + arrUnitdo[nq] + arrDigit[1] + " and" + twonum(numr);
		}
		return ltr;

	}
	HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrServiceId() {
		return strServiceId;
	}

	public void setStrServiceId(String strServiceId) {
		this.strServiceId = strServiceId;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}
	public String getStrPC() {
		return strPC;
	}

	public void setStrPC(String strPC) {
		this.strPC = strPC;
	}

	public String getStrFYS() {
		return strFYS;
	}

	public void setStrFYS(String strFYS) {
		this.strFYS = strFYS;
	}

	public String getStrFYE() {
		return strFYE;
	}
	public void setStrFYE(String strFYE) {
		this.strFYE = strFYE;
	}
	public boolean isAttachment() {
		return isAttachment;
	}
	public void setAttachment(boolean isAttachment) {
		this.isAttachment = isAttachment;
	}
	public String getStrBankPayType() {
		return strBankPayType;
	}
	public void setStrBankPayType(String strBankPayType) {
		this.strBankPayType = strBankPayType;
	}

	public String getStrPCS() {
		return strPCS;
	}

	public void setStrPCS(String strPCS) {
		this.strPCS = strPCS;
	}

	public String getStrPCE() {
		return strPCE;
	}

	public void setStrPCE(String strPCE) {
		this.strPCE = strPCE;
	}

	public String getStrPaymentDate() {
		return strPaymentDate;
	}

	public void setStrPaymentDate(String strPaymentDate) {
		this.strPaymentDate = strPaymentDate;
	}

}
