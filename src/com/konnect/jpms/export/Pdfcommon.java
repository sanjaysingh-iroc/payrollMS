package com.konnect.jpms.export;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;

public class Pdfcommon {
	List<Integer> salHeadId = new ArrayList<Integer>();
	List<Double> salHeadAmt = new ArrayList<Double>();
	List<String> salaryHeadName = new ArrayList<String>();
	List<String> deductionHeadName = new ArrayList<String>();
	List<Double> salHeadAmount = new ArrayList<Double>();
	List<Double> salHeadAmountGross = new ArrayList<Double>();
	List<Double> salHeadAmountNet = new ArrayList<Double>();
	List<Double> deductionHeadAmount = new ArrayList<Double>();
	List<String> empDetails = new ArrayList<String>();
	List<String> leaveName = new ArrayList<String>();
	List<Integer> noOfLeave = new ArrayList<Integer>(); 
	ArrayList<String> payEmpHead = new ArrayList<String>();
	List<Integer> leaveTypeName = new ArrayList<Integer>();
	List<String> totalLeave = new ArrayList<String>();
	List<String> leaveNameType = new ArrayList<String>();

	int nYear = 0;
	Double dblTotalAmt = 0.0;
	Double dblTotalDeduction = 0.0;
	Double dblGrossTotal = 0.0;
	Double dblNetSalary = 0.0;
	String strPayMode = null;

	String strCompanyAddress = null;
	String strCompanyLogo = null;
	String strEmpImage = null;
	int tDays=0;
	int pDays=0;
	int unpaidDays =0;
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
	boolean isAttachment;
	
	String strLevelId = null;
	String bankName =null;
	String monthName= null;
	String strBankPayType;
	
	public CommonFunctions CF = new CommonFunctions();
	
	
	public void createSalarySlipPdf() {
		
		
		try {
			
			PreparedStatement pst = null;
			ResultSet rs = null;
			Connection con = null;
			con = makeConnection(con);
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
			pst.setInt(1, 78);
			pst.setDate(2,java.sql.Date.valueOf("2015-04-01"));
			pst.setDate(3, java.sql.Date.valueOf("2016-03-31"));
			pst.setInt(4,12);
			pst.setInt(5,22);
			pst.setInt(6,1);
			
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
//			pst.setInt(4, uF.parseToInt(getStrMonth()));
//			pst.setInt(5, uF.parseToInt(getStrPC()));
//			pst.setInt(6, uF.parseToInt(getStrBankPayType()));
			
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
				strTotal_days =rs.getString("total_days");
				strPaid_days =rs.getString("paid_days");
				strPaymentMode =rs.getString("payment_mode");
				 pMonth =rs.getInt("month");
				 pYear =rs.getInt("year");
			}
			rs.close();
			pst.close();
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true and " +
					"isdisplay=true and effective_date = (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
					"and isdisplay=true and effective_date<=?) group by level_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, 78);
			pst.setInt(2, 78);
			pst.setDate(3, java.sql.Date.valueOf(strPayCycleEnd));
			
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setInt(2, uF.parseToInt(strEmpId));
//			pst.setDate(3, uF.getDateFormat(strPayCycleEnd, DBDATE));
			
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
			pst.setInt(1, 78);
			pst.setDate(2,java.sql.Date.valueOf("2015-04-01"));
			pst.setDate(3, java.sql.Date.valueOf("2016-03-31"));
			pst.setInt(4, 12);
			pst.setInt(5, 22);
			
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setDate(2, uF.getDateFormat(getStrFYS(), DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(getStrFYE(), DATE_FORMAT));
//			pst.setInt(4, uF.parseToInt(getStrMonth()));
//			pst.setInt(5, uF.parseToInt(getStrPC()));
//			
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
			
		    monthName = pMonth+" "+pYear;
		   
           tDays = Integer.parseInt(strTotal_days);
           pDays = Integer.parseInt(strPaid_days);
           unpaidDays = tDays - pDays;
           
       	if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("1")){
			strPaymentMode ="Bank Transfer";	
		}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
			strPaymentMode ="Cash";	
		}else if(strPaymentMode != null && strPaymentMode.equalsIgnoreCase("2")){
			strPaymentMode ="Cheque";	
		}
       	
		String strOrgId = CF.getEmpOrgId(con, uF, "78");
//		String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());

		pst = con.prepareStatement("select * from org_details where org_id = ?");
		pst.setInt(1, uF.parseToInt(strOrgId));
		rs = pst.executeQuery();
		while(rs.next()){
			hmOrganisationDetails.put("ORG_ID", rs.getString("org_id"));
			hmOrganisationDetails.put("ORG_LOGO", rs.getString("org_logo"));
			hmOrganisationDetails.put("ORG_NAME", rs.getString("org_name"));
			hmOrganisationDetails.put("PDF_TYPE", rs.getString("payslip_format"));
			hmOrganisationDetails.put("ORG_ADDRESS", rs.getString("ORG_ADDRESS"));
			hmOrganisationDetails.put("ORG_CITY", rs.getString("ORG_CITY"));

		}
			rs.close();
			pst.close();
       	
       	pst = con.prepareStatement("SELECT * FROM employee_personal_details epd, employee_official_details eod WHERE epd.emp_per_id  = eod.emp_id and emp_per_id = ?");
		pst.setInt(1, 78);
//		pst.setInt(1, uF.parseToInt(getStrEmpId()));

		rs = pst.executeQuery();
		while (rs.next()) {
			Map<String, Map<String, String>> hmWorkLocationMap = CF.getWorkLocationMap(con);
			hmTemp = (Map<String, String>)hmWorkLocationMap.get(rs.getInt("wlocation_id")+"");
			
			if(hmTemp==null)hmTemp=new HashMap();
			strCompanyAddress = hmTemp.get("WL_ADDRESS")+" " +hmTemp.get("WL_CITY")+" " +hmTemp.get("WL_PINCODE");
			
			strLevelId = hmEmpLevelMap.get(rs.getString("emp_id"));
//			
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
			empDetails.add(rs.getString("emp_fname") +strEmpMName+ " "+ rs.getString("emp_lname"));
			empDetails.add(strTotal_days);
			empDetails.add(hmEmpDepartmentMap.get(rs.getString("depart_id")));
			empDetails.add("0.00");
			empDetails.add(hmEmpDesigMap.get(rs.getString("emp_id")));
			empDetails.add("0.00");
			empDetails.add(uF.getDateFormat(rs.getString("joining_date"), "yyyy-MM-dd", "dd/MM/yyyy"));
			empDetails.add(strPaid_days);
			empDetails.add(rs.getString("emp_pan_no"));
			empDetails.add("0.00");
			empDetails.add(uF.showData(hmBankNameMap.get(rs.getString("emp_bank_name")), "-"));
			empDetails.add(uF.showData(rs.getString("emp_bank_acct_nbr"), "-"));
			empDetails.add(uF.showData(rs.getString("emp_pan_no"), "-"));
			empDetails.add(uF.showData(rs.getString("uan_no"), "-"));
			empDetails.add(uF.showData(rs.getString("uid_no"), "-"));
			
			}
		rs.close();
		pst.close();
			List alLoans = new ArrayList();
			Map hmEmpLoan = new HashMap();
			Map<String, String> hmEmpSalLastEffectiveDate = null; //need to implement this map
			CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, uF.getDateFormat(strPayCycleStart, "yyyy-MM-dd", "dd/MM/yyyy"), uF.getDateFormat(strPayCycleEnd,  "yyyy-MM-dd", "dd/MM/yyyy"), hmEmpSalLastEffectiveDate);
			
			Map hmEmpLoanInner = (Map)hmEmpLoan.get(strEmpId); 
			if(hmEmpLoanInner==null) hmEmpLoanInner=new HashMap();
			Map<String, String> hmLoanPolicies = CF.getLoanPoliciesMap(con, uF, strOrgId);
			
			Map<String, List<Map<String, String>>> hmPerkAlign = CF.getIndividualPerkAlignAmount(con, uF, CF, 78,"01/04/2015","31/03/2016","01/04/2015","31/03/2016",uF.parseToInt("2"));
//			
			Map<String,String> hmsalaryAmount = getSalaryAmount(con,uF,"78","","");
			
			
			Map<String, String> hmSalaryDetailsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(strEmpLevelId));	
			
			if(hmPerkAlign == null) 
				hmPerkAlign = new HashMap<String, List<Map<String,String>>>();
			
			pst = con.prepareStatement("SELECT pg.* FROM payroll_generation pg where pg.emp_id =? and pg.month=? AND pg.paycycle=? AND pg.financial_year_from_date=? " +
					"AND pg.financial_year_to_date=? and (bank_pay_type=? or bank_pay_type is null) and pg.salary_head_id in (select distinct(salary_head_id) as salary_head_id from " +
					"emp_salary_details where isdisplay=true and emp_id=? and level_id=?) order by earning_deduction desc,pg.salary_head_id");
				pst.setInt(1, uF.parseToInt("78"));
				pst.setInt(2, uF.parseToInt("12"));
				pst.setInt(3, uF.parseToInt("22"));
				pst.setDate(4,java.sql.Date.valueOf("2015-04-01"));
				pst.setDate(5, java.sql.Date.valueOf("2016-03-31"));
				pst.setInt(6, uF.parseToInt("1"));
				pst.setInt(7, uF.parseToInt("78"));
				pst.setInt(8, uF.parseToInt(strEmpLevelId));
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

							
							if(uF.parseToInt(rs.getString("salary_head_id"))==17){
								
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
					
					dblNetSalary = dblGrossTotal - dblTotalDeduction;
					
					deductionHeadAmount.add(dblTotalDeduction);
					deductionHeadAmount.add(dblNetSalary);
					
					salHeadAmountGross.add(dblGrossTotal);

					Map hmLeaveDatesType = new HashMap();
					Map<String, Map<String, String>> leaveDetailsMap = CF.getActualLeaveDates(con, CF, uF, "01/04/2015","31/03/2016",  hmLeaveDatesType, true, null);
					Map<String, String> leaveEmpDetailsMap = leaveDetailsMap.get("35");

//					Map<String, Map<String, String>> leaveDetailsMap = CF.getActualLeaveDates(con, CF, uF, getStrFYS(), getStrFYE(),  hmLeaveDatesType, true, null);
//					Map<String, String> leaveEmpDetailsMap = leaveDetailsMap.get(getStrEmpId());

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
			generateSalarySlipSeventhFormat("78", "", hmOrganisationDetails,monthName,empDetails);
//			generateSalarySlipSeventhFormat(getStrEmpId(), "", hmOrganisationDetails,monthName,empDetails);

           
			

		
	}catch (Exception e) {
		e.printStackTrace();
	}
		
	}
	
	

	private void generateSalarySlipSeventhFormat(String strEmpId, String strMonth, Map<String, String> hmOrganisationDetails, String monthName,List<String> empDetails) {
		
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			String strCompanyName=hmOrganisationDetails.get("ORG_NAME");
			String strCompanyAddress=hmOrganisationDetails.get("ORG_ADDRESS");
			String strWorkLocation=hmOrganisationDetails.get("ORG_CITY");
			
			com.itextpdf.text.Font heading = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 13);
			com.itextpdf.text.Font normal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11);
			com.itextpdf.text.Font normalwithbold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			com.itextpdf.text.Font small = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7);
			com.itextpdf.text.Font smallBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.WHITE);
			com.itextpdf.text.Font italicEffect = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 

			
			
			// Added hardcoded path
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			  String fileName = "c:/temp/FirstPdf.pdf";
			
	       com.itextpdf.text.Document document = new com.itextpdf.text.Document();
	       com.itextpdf.text.pdf.PdfWriter.getInstance(document,new FileOutputStream(fileName));
	       document.open();
	       
	       com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(10);
			table.setWidthPercentage(100);       
			int[] cols = {3,10,10,10,10,10,10,10,10,10};
			table.setWidths(cols);

			PdfPTable companyNameTable = new PdfPTable(1);
			int[] arrheaderwidthsh = { 100 }; // percentage
			companyNameTable.getDefaultCell().setBorderWidth(0);
			companyNameTable.setWidths(arrheaderwidthsh);
			PdfPCell companyNamecell = new PdfPCell(new Phrase(strCompanyName, FontFactory.getFont("Verdana", 14,Font.BOLD)));
			

			companyNamecell.setBorderWidthTop(0);
			companyNamecell.setBorderWidthBottom(0);
			companyNamecell.setBorderWidthLeft(0);
			companyNamecell.setBorderWidthRight(0);

			companyNamecell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			companyNameTable.addCell(companyNamecell);
			PdfPCell companyNamecell1 = new PdfPCell(new Phrase(strCompanyAddress, FontFactory.getFont("Verdana", 10,Font.NORMAL)));

			companyNamecell1.setBorderWidthTop(0);
			companyNamecell1.setBorderWidthBottom(0);
			companyNamecell1.setBorderWidthLeft(0);
			companyNamecell1.setBorderWidthRight(0);

			companyNamecell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			companyNameTable.addCell(companyNamecell1);
			
//			
			com.itextpdf.text.pdf.PdfPCell row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("",smallBold));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setBorder(Rectangle.NO_BORDER);
	       row1.setColspan(10);
	       row1.setPadding(2.5f);
	       table.addCell(row1);
	     

	       
	     //New Row
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Payslip for the month of  : "+monthName,normalwithbold));
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
	         
//	       String heading2="EMPLOYEE DETAILS";
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
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Payment & Leave Details",smallBold));
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
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(strWorkLocation,small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(1);
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
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Deputation to",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Swiss Re",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(1);
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
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Days Paid",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("12",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(4);
	       table.addCell(row1);
	       
	       //new row
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Aadhaar No",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(empDetails.get(14),small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(1);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Leave Balance",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(1);
	       table.addCell(row1);
	     
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Earned",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(1);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("NA",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(1);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Availed",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(1);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("NA",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(1);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Total",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(1);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(""+noOfLeave.get(2),small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(1);
	       table.addCell(row1);
	       
	       //new row
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("UAN",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(""+empDetails.get(15),small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Member #",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(1);
	       table.addCell(row1);
	       
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("PYBOM21525850000010004",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Start Date",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(1);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("01/01/2020",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       //new row
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(" ",small));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setColspan(10);
	       row1.setBorder(Rectangle.NO_BORDER);	
	       table.addCell(row1);
	       
	       
	       //new row
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Earnings",smallBold));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setBackgroundColor(BaseColor.BLUE);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Arrears",smallBold));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setBackgroundColor(BaseColor.BLUE);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Current",smallBold));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setBackgroundColor(BaseColor.BLUE);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Deduction",smallBold));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setBackgroundColor(BaseColor.BLUE);
	       row1.setColspan(2);
	       table.addCell(row1);
			
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Amount",smallBold));
	       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	       row1.setBackgroundColor(BaseColor.BLUE);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       
	       Double netPayable =  deductionHeadAmount.get(deductionHeadAmount.size()-1);
	       if(deductionHeadName.contains("Net Payable")){
	       deductionHeadName.remove(deductionHeadName.size()-1);
	        deductionHeadAmount.remove(deductionHeadAmount.size() - 1);

	       }
	       
	       Double totalDeduction =  deductionHeadAmount.get(deductionHeadAmount.size() - 1);
	       if(deductionHeadName.contains("Deductions")){
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
				
				  row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(strHeadNM,small));
			       row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			       row1.setColspan(2);
			       table.addCell(row1);
			       
			       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("8000.0",small));
			       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			       row1.setColspan(2);
			       table.addCell(row1);
			       
			       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph( uF.formatIntoTwoDecimal(salHeadAmount.get(nCount)),small));
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
					 }else {
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
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(uF.formatIntoTwoDecimal(netPayable),small));
	       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       //new row
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("   ",small));
	       row1.setBorder(Rectangle.NO_BORDER);	
	       row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	       row1.setColspan(6);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("Date of Payment",small));
	       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("31  Aug 2020",small));
	       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       
	       //new row
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("   ",small));
	       row1.setBorder(Rectangle.NO_BORDER);	
	       row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	       row1.setColspan(6);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("TAX METHOD FOLLOWED",small));
	       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       row1 =new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph("OLD SCHEME",small));
	       row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	       row1.setColspan(2);
	       table.addCell(row1);
	       
	       
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
			
			
			
			PdfPCell cellUvU = new PdfPCell(new Phrase("This is computer generated PaySlip and does not require signature.",FontFactory.getFont("Verdana", 8, Font.NORMAL)));
			cellUvU.setBorderWidth(1);
			signatureTable.addCell(cellUvU);


	       document.add(companyNameTable);
	       document.add(table);
	       document.add(signatureTable);
	       document.close();
	       
//			String filename="PaySlip"+strEmpId+"_"+strMonth+"_"+nyeardevsecond+""+nyeardevfirst+".pdf";
//			response.setContentType("application/pdf");
//			response.setContentLength(buffer.size());
//			response.setHeader("Content-Disposition","attachment; filename="+filename+"");
//			ServletOutputStream out = response.getOutputStream();
//			buffer.writeTo(out);
//			out.flush();
		} catch (Exception e) {
			e.printStackTrace();  
		}

	}
	

	private Map<String, String> getSalaryAmount(Connection con, UtilityFunctions uF, String strEmpId, String strFYS, String strFYE) {
		boolean flagEpf = false;
		Map<String,String> hmAmount = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
//		String strLevelId = CF.getEmpLevelId(con, ""+strEmpId);
		String strOrgId = CF.getEmpOrgId(con, uF, ""+strEmpId);
		Map<String, List<Map<String, String>>> hmPerkAlign = new HashMap<String, List<Map<String,String>>>();
		try {
			
			String[] strPayCycleDate = CF.getPayCycleDatesOnPaycycleId(con,"2", strOrgId, "Asia/Calcutta", CF, null);
			
//			String[] strPayCycleDate = CF.getPayCycleDatesOnPaycycleId(con, getStrPC(), strOrgId, CF.getStrTimeZone(), CF, request);

			for (int i = 0; i < strPayCycleDate.length; i++) {

			}
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved = true " +
//				"and isdisplay=true and effective_date <= ? group by level_id"); 
			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=?"
					+ " and is_approved=true and " +
					"isdisplay=true "
					+ "and effective_date = (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=?"
					+ " and is_approved=true " +
					"and isdisplay=true"
					+ " and effective_date<=?"
					+ ") group by level_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setDate(3, uF.getDateFormat("30/05/2018", "dd/MM/yyyy"));
			
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setInt(2, uF.parseToInt(strEmpId));
//			pst.setDate(3, uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT));
			
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
				"asd.salary_head_id = sd.salary_head_id WHERE sd.level_id = ? and asd.salary_head_id not in(500) and (is_delete is null or " +
				"is_delete=false) and sd.earning_deduction='E' order by sd.earning_deduction desc, weight"); 
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setInt(3, uF.parseToInt(strLevelId));
			pst.setDate(4, uF.getDateFormat("30/05/2018", "dd/MM/yyyy"));
			pst.setInt(5, uF.parseToInt(strLevelId));
	//		pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(6, uF.getDateFormat("30/05/2018", "dd/MM/yyyy"));
			pst.setInt(7, uF.parseToInt(strEmpId));
			pst.setInt(8, uF.parseToInt(strEmpId));
			pst.setInt(9, uF.parseToInt(strLevelId));
			pst.setDate(10, uF.getDateFormat("30/05/2018", "dd/MM/yyyy"));
			pst.setInt(11, uF.parseToInt(strLevelId));
	//		pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(12, uF.getDateFormat("30/05/2018", "dd/MM/yyyy"));
			pst.setInt(13, uF.parseToInt(strLevelId));
			
		/*	
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
//			System.out.println("pst ================>> " + pst);*/
			
			
			rs = pst.executeQuery();
			while(rs.next()){
				hmAmount.put(rs.getString("salary_head_id"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
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
	
	public Connection makeConnection(Connection con) {

		try {
		Class.forName("org.postgresql.Driver");
		con = DriverManager.getConnection("jdbc:postgresql://localhost:5433/postgres2", "postgres", "admin" );

		} catch (Exception e) {
		e.printStackTrace();
		}
		return con;
		}


}
