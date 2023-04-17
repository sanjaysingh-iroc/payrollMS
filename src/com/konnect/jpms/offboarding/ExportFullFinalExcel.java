package com.konnect.jpms.offboarding;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class ExportFullFinalExcel implements ServletRequestAware,ServletResponseAware, IStatements {

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String financialYear;
	String strMonth;
	UtilityFunctions uF = new UtilityFunctions();
	
	String emp_id;
	int resignId;
	
	
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public int getResignId() {
		return resignId;
	}
	public void setResignId(int resignId) {
		this.resignId = resignId;
	}
	
	public void execute() {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		OffboardSalaryPreview offboardSalaryPreview=new OffboardSalaryPreview();
		offboardSalaryPreview.setEmp_id(getEmp_id());
		offboardSalaryPreview.setResignId(getResignId());
		offboardSalaryPreview.setServletRequest(request);
		offboardSalaryPreview.session = session;
		offboardSalaryPreview.CF = CF;
		offboardSalaryPreview.strSessionUserType = strUserType;
//		strSessionUserType
		offboardSalaryPreview.fullSalaryPreview(getEmp_id(),CF);
		
		generateFullFinalReport();
		
	}
	
	
	private void generateFullFinalReport() {
		
		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);//Created By Dattatray Date:09-12-21
			Map<String, String> empMap = (Map<String, String>) request.getAttribute("empDetailsMp");
			Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
//			String probationRemaining = (String) request.getAttribute("PROBATION_REMAINING");
			String noticePeriod = (String) request.getAttribute("NOTICE_PERIOD");
			
			request.getAttribute("Months");
			request.getAttribute("totalWorkingDays");
			request.getAttribute("reason");
			Map<String,String> hmSalaryDetails =(Map<String,String> )request.getAttribute("hmSalaryDetails");
			List<String> alEmpSalaryDetailsEarning = (List<String>)request.getAttribute("alEmpSalaryDetailsEarning");
			List<String> alEmpSalaryDetailsDeduction = (List<String>)request.getAttribute("alEmpSalaryDetailsDeduction");

//			List<String> alEarningSalaryDuplicationTracer = (List<String>)request.getAttribute("alEarningSalaryDuplicationTracer");
//			List<String> alDeductionSalaryDuplicationTracer = (List<String>)request.getAttribute("alDeductionSalaryDuplicationTracer");

			Map<String,Double> hmSalaryAmt=(Map<String,Double>)request.getAttribute("hmSalaryAmt");
			UtilityFunctions uF=new UtilityFunctions();
			double earningTotal=0.0;
			double deductionTotal=0.0;
			
							 	  	  	
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Payment Held");			
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			header.add(new DataStyle("Full & Final Settlement Sheet/ Clearance Slip",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			List<List<List<DataStyle>>> allReportData = new ArrayList<List<List<DataStyle>>>();
			List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
			
			List<DataStyle> innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Name of Employee",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle(uF.showData(hmEmpProfile.get("NAME"),""),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("Employee Code No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle(uF.showData(hmEmpProfile.get("EMPCODE"),""),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Designation (Grade)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle(uF.showData(hmEmpProfile.get("DESIGNATION_NAME"),""),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("Location",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle(uF.showData(hmEmpProfile.get("WLOCATION_NAME"),""),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle(uF.showData(hmEmpProfile.get("DEPARTMENT_NAME"),""),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("Date of joining",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle(uF.showData(hmEmpProfile.get("JOINING_DATE"),""),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Date of Resignation approved",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle(uF.showData(empMap.get("ACCEPTED_DATE"),""),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("Date of Relieving (after notice)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle(uF.showData(empMap.get("LAST_DAY_DATE"),""),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Notice Period",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle(uF.showData(noticePeriod, "0")+" days",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("Total Years of Service",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle(uF.showData((String)request.getAttribute("totalService"),""),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			allReportData.add(reportData); //0
			
			// Started Dattatray Date:13-12-21
			if (!uF.parseToBoolean(hmFeatureStatus.get(F_DISABLE_RESIGNATION_APPROVAL_REASON_FULL_AND_FIANL_PDF))) {
				reportData = new ArrayList<List<DataStyle>>();
				innerList = new ArrayList<DataStyle>();
				innerList.add(new DataStyle("Reason for Leaving Service",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				innerList.add( new DataStyle(uF.showData(empMap.get("EMP_RESIGN_REASON"),""),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				reportData.add(innerList);
				
				innerList = new ArrayList<DataStyle>();
				innerList.add(new DataStyle("Approval Reason of Manager",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				innerList.add( new DataStyle(uF.showData(empMap.get("MANAGER_APPROVE_REASON"),""),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				reportData.add(innerList);
				
				innerList = new ArrayList<DataStyle>();
				innerList.add(new DataStyle("Approval Reason of HR Manager",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				innerList.add( new DataStyle(uF.showData(empMap.get("HR_MANAGER_APPROVE_REASON"),""),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				reportData.add(innerList);
			}// Ended Dattatray Date:13-12-21
			allReportData.add(reportData); //1
			
			reportData = new ArrayList<List<DataStyle>>();
			innerList = new ArrayList<DataStyle>();
//			innerList.add(new DataStyle("Basic Salary (from Salary Structure)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			innerList.add( new DataStyle("NA",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			innerList.add(new DataStyle("Leaves Available",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			innerList.add( new DataStyle("0",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
//			innerList.add(new DataStyle("Salary for month for F & F",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			innerList.add( new DataStyle(uF.showData(empMap.get("MONTHS"), ""),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			innerList.add(new DataStyle("Total days Payable Salary",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			innerList.add( new DataStyle(uF.showData(empMap.get("TOTALWORKINGDAYS"), ""),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			allReportData.add(reportData); //2
			
			reportData = new ArrayList<List<DataStyle>>();
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("E A R N I N G S",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("D E D U C T I O N S",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			allReportData.add(reportData); //3
			
			reportData = new ArrayList<List<DataStyle>>();
			for(int i=0;i<alEmpSalaryDetailsDeduction.size() || i<alEmpSalaryDetailsEarning.size();i++) {
				
				innerList = new ArrayList<DataStyle>();
				if(i<alEmpSalaryDetailsEarning.size()) {
					innerList.add(new DataStyle(uF.showData(hmSalaryDetails.get(alEmpSalaryDetailsEarning.get(i)), "-"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					double salHeadAmt = 0;
					if(hmSalaryAmt.get(alEmpSalaryDetailsEarning.get(i)) != null) {
						salHeadAmt = hmSalaryAmt.get(alEmpSalaryDetailsEarning.get(i));
						earningTotal += hmSalaryAmt.get(alEmpSalaryDetailsEarning.get(i));
					}
					innerList.add(new DataStyle(uF.formatIntoOneDecimal(salHeadAmt),Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				} else {
					innerList.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					innerList.add(new DataStyle("",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}
				
				if(i<alEmpSalaryDetailsDeduction.size()) {
					
					innerList.add(new DataStyle(uF.showData(hmSalaryDetails.get(alEmpSalaryDetailsDeduction.get(i)), "-"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					double salHeadAmt = 0;
					if(hmSalaryAmt.get(alEmpSalaryDetailsDeduction.get(i)) != null) {
						salHeadAmt = hmSalaryAmt.get(alEmpSalaryDetailsDeduction.get(i));
						deductionTotal += hmSalaryAmt.get(alEmpSalaryDetailsDeduction.get(i));
					}
					//Created By dattatray date:14-12-21
					innerList.add(new DataStyle(uF.formatIntoComma(uF.parseToDouble(uF.formatIntoZeroWithOutComma(salHeadAmt))),Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				} else {
					innerList.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					innerList.add(new DataStyle("",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}
				
				reportData.add(innerList);
			}
			
			double netSalaryTotal = earningTotal - deductionTotal;
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Gross Salary Earning Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle(uF.formatIntoOneDecimal(earningTotal),Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("Gross Salary Deductions Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			//Created By Dattatray Date:14-12-21
			innerList.add( new DataStyle(uF.formatIntoComma(uF.parseToDouble(uF.formatIntoZeroWithOutComma(deductionTotal))),Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			allReportData.add(reportData); //4

			
			reportData = new ArrayList<List<DataStyle>>();
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("NET SALARY TOTAL:",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			//Created By Dattatray Date:14-12-21
			innerList.add( new DataStyle(uF.formatIntoComma(uF.parseToDouble(uF.formatIntoZeroWithOutComma(netSalaryTotal))),Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			allReportData.add(reportData); //5
			
			
//			double deductionSalary=(Double)request.getAttribute("deduction_salary"); 
//			double loanAmt = (Double) request.getAttribute("loanAmt");
//			double travellingAllowance = (Double)request.getAttribute("travellingAllowance");
			
			double dblOtherEarningTotal = 0.0d;
			double dblOtherDeductionTotal = 0.0d;
			
			
			
			reportData = new ArrayList<List<DataStyle>>();
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("OTHERS",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			allReportData.add(reportData); //6
			
			double reimbursement = (Double) request.getAttribute("Reimbursement");
			dblOtherEarningTotal += reimbursement;
			double gratuity = (Double) request.getAttribute("gratuity");
			dblOtherEarningTotal += gratuity;
			double LTAAmt = (Double)request.getAttribute("LTAAmt");
			dblOtherEarningTotal += LTAAmt;
			double PerkAmt = (Double)request.getAttribute("PerkAmt");
			dblOtherEarningTotal += PerkAmt;
			
			double deductAmt = (Double)request.getAttribute("deductAmt");
			dblOtherDeductionTotal += deductAmt;
			
//			List<String> otherHeadList = getOtherHeadsDetails();
			List<String> otherHeadList = new ArrayList<String>();
			otherHeadList.add("Reimbursement");
			otherHeadList.add("Other Deduction");
			otherHeadList.add("Gratuity");
			otherHeadList.add("");
			otherHeadList.add("LTA");
			otherHeadList.add("");
			otherHeadList.add("Perk");
			otherHeadList.add("");
			otherHeadList.add("Gross Other Earnings");
			otherHeadList.add("Gross Other Deductions");
			
			Map<String,String> hmOtherHeadAmt = new LinkedHashMap<String,String>();
			hmOtherHeadAmt.put("Reimbursement", uF.formatIntoOneDecimal(reimbursement));
			hmOtherHeadAmt.put("Other Deduction", uF.formatIntoOneDecimal(deductAmt));
			hmOtherHeadAmt.put("Gratuity", uF.formatIntoOneDecimal(gratuity));
			hmOtherHeadAmt.put("", "");
			hmOtherHeadAmt.put("LTA", uF.formatIntoOneDecimal(LTAAmt));
			hmOtherHeadAmt.put("", "");
			hmOtherHeadAmt.put("Perk", uF.formatIntoOneDecimal(PerkAmt));
			hmOtherHeadAmt.put("", "");
			hmOtherHeadAmt.put("Gross Other Earnings", uF.formatIntoOneDecimal(dblOtherEarningTotal));
			hmOtherHeadAmt.put("Gross Other Deductions", uF.formatIntoOneDecimal(dblOtherDeductionTotal));
			
			reportData = new ArrayList<List<DataStyle>>();
			int cnt = 0;
			for(int i=0;i<otherHeadList.size();i++) {
				if(cnt == 0) {
					innerList = new ArrayList<DataStyle>();
				}
				cnt++;
				innerList.add(new DataStyle(otherHeadList.get(i),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				// Started Dattatray Date:13-12-21
				if(!otherHeadList.get(i).isEmpty()) {
					innerList.add(new DataStyle(uF.showData(hmOtherHeadAmt.get(otherHeadList.get(i)),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}else {
					innerList.add(new DataStyle("",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

				}// End Dattatray Date:13-12-21
				
				if(cnt == 2) {
					reportData.add(innerList);
					cnt=0;
				}
			}
			allReportData.add(reportData); //7
			
			double netOtherTotal = dblOtherEarningTotal - dblOtherDeductionTotal;
			double settlementAmount = netSalaryTotal + netOtherTotal;
			
			String amountInWords = "";
			String strTotalAmt=""+settlementAmount;
			if(strTotalAmt.contains(".")){
				strTotalAmt=strTotalAmt.replace(".", ",");
				String[] temp=strTotalAmt.split(",");
				amountInWords = uF.digitsToWords(uF.parseToInt(temp[0]));
				if(uF.parseToInt(temp[1])>0){
					int pamt=0;
					if(temp[1].length()==1){
						pamt=uF.parseToInt(temp[1]+"0");
					}else{
						pamt=uF.parseToInt(temp[1]);
					}
					amountInWords+=" and "+uF.digitsToWords(pamt)+" paise only";
				}
			}else{
				int totalAmt1=(int)settlementAmount;
				amountInWords=uF.digitsToWords(totalAmt1)+" only";
			}
			
			reportData = new ArrayList<List<DataStyle>>();
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("NET OTHER TOTAL:",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle(uF.formatIntoOneDecimal(netOtherTotal),Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Total Settlement Amount:",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			//Created By Dattatray Date:14-12-21
			innerList.add( new DataStyle(uF.formatIntoComma(uF.parseToDouble(uF.formatIntoZeroWithOutComma(settlementAmount))),Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Total Settlement Amount(in words):",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle(amountInWords,Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			allReportData.add(reportData); //8
			
			
			reportData = new ArrayList<List<DataStyle>>();
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			allReportData.add(reportData); //9
			
			
			reportData = new ArrayList<List<DataStyle>>();
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("No Dues Clearance- Approved by all Departments.",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Prepared By :" + (String)session.getAttribute(EMPNAME),Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("HR Department",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("I hereby agree and confirm having received the above amount before signing this settlement paper. There is nothing due on either side.",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			allReportData.add(reportData); //10
			
			
			reportData = new ArrayList<List<DataStyle>>();
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			allReportData.add(reportData); //11
			
			
			reportData = new ArrayList<List<DataStyle>>();
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("--For Account Department Only--",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Payment vide Cheque no.",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Date of Payment",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Name of Bank",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Date",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("For",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			allReportData.add(reportData); //12
			
			
			reportData = new ArrayList<List<DataStyle>>();
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Manager-HR",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle("Account Department",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			
			innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("(Authorized signatory)",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add( new DataStyle("(Authorized signatory)",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			reportData.add(innerList);
			allReportData.add(reportData); //13
			
			
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.getFullFinalExcelSheetDesignData(workbook,sheet,header,allReportData);
			
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=FullFinalReport.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
}
	
	
	public List<String> getOtherHeadsDetails() {
		List<String> reiumbursementDetails = new ArrayList<String>();
		reiumbursementDetails.add("Reimbursement");
		reiumbursementDetails.add("Loan Amount");
		reiumbursementDetails.add("Gratuity");
		reiumbursementDetails.add("Travel Advance");
		reiumbursementDetails.add("LTA");
		reiumbursementDetails.add("Other Advance deductions");
		reiumbursementDetails.add("Perks");
		reiumbursementDetails.add("Other Deductions (Manual)");
		reiumbursementDetails.add("Gross Other Earnings");
		reiumbursementDetails.add("Gross Other Deductions");
		
		return reiumbursementDetails;
	}
	
	
//	private void viewEPFSalaryReportByOrg() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		
//		con = db.makeConnection(con);
//
//		
//		try {
//			
//			Map hmEmpName = new HashMap();
//			Map hmEmpCode = new HashMap();
//			Map hmEmpPFNumber = new HashMap();
//			
//			String strMonthYear = null;
//			
//			String[] strPayCycleDates = null;
//			String strFinancialYearStart = null;
//			String strFinancialYearEnd = null;
//
//			
//			Map<String, String> hmEarningSalaryMap = new HashMap<String, String>();
//			List<String> alEmployees = new ArrayList<String>();
//			
//
//			Map hmEPFDetailsMap = new HashMap();
//			CF.getEPFDetailsMap(con,hmEPFDetailsMap, strFinancialYearStart, strFinancialYearEnd);
//			
//			
////			con = db.makeConnection(con);
//			
//			
//			
//			pst = con.prepareStatement("select * from employee_personal_details");
//			rs = pst.executeQuery();
//			while(rs.next()){
//				hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
//				hmEmpCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
//				hmEmpPFNumber.put(rs.getString("emp_per_id"), rs.getString("emp_pf_no"));	
//			}
//			
//			/*pst = con.prepareStatement("select sum(amount) as amount, emp_id from payroll_generation where pay_date between ? and ? and earning_deduction = 'E' group by emp_id");
//			pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));*/
//			
//			
//			
//			
//			
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select sum(epf_max_limit) as epf_max_limit,sum(eps_max_limit) as eps_max_limit," +
//					"sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
//					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(edli_max_limit) as edli_max_limit," +
//					"sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges,org_id,sum(evpf_contribution) as evpf_contribution " +
//					"from emp_epf_details eed,employee_official_details eod where eed.emp_id=eod.emp_id and financial_year_start=? " +
//					"and financial_year_end=? and _month=? ");
//
//			sbQuery.append(" group by eod.org_id");
//			
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(getStrMonth()));
//			System.out.println("org pst==>"+pst);
//			
//			rs = pst.executeQuery();
//			
//			double dblEEPFContributionTotal = 0;
//			double dblEEVPFContributionTotal = 0;
//			double dblERPFContributionTotal = 0;
//			double dblERPSContributionTotal = 0;
//			double dblERDLIContributionTotal = 0;
//			double dblEDLIMaxLimitTotal = 0;
//			double dblEPFAdminChargesTotal = 0;
//			double dblEDLIAdminChargesTotal = 0;
//			double dbl_epf_max_limit_Total = 0;
//			double dbl_eps_max_limit_Total = 0;
//			
//			while(rs.next()){
//				
//				dblEEPFContributionTotal += rs.getDouble("eepf_contribution");
//				dblEEVPFContributionTotal += rs.getDouble("evpf_contribution");
//				dblERPFContributionTotal += rs.getDouble("erpf_contribution");
//				dblERPSContributionTotal += rs.getDouble("erps_contribution");
//				dblERDLIContributionTotal += rs.getDouble("erdli_contribution");
//				dblEDLIMaxLimitTotal += rs.getDouble("edli_max_limit");
//				dblEPFAdminChargesTotal += rs.getDouble("pf_admin_charges");
//				dblEDLIAdminChargesTotal += rs.getDouble("edli_admin_charges");
//				dbl_epf_max_limit_Total += rs.getDouble("epf_max_limit");
//				dbl_eps_max_limit_Total += rs.getDouble("eps_max_limit");
//				
//				hmEarningSalaryMap.put(rs.getString("org_id")+"_EPF_MAX_LIMIT", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("epf_max_limit")));
//				hmEarningSalaryMap.put(rs.getString("org_id")+"_EPS_MAX_LIMIT", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("eps_max_limit")));
//				hmEarningSalaryMap.put(rs.getString("org_id")+"_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("eepf_contribution")));
//				hmEarningSalaryMap.put(rs.getString("org_id")+"_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("evpf_contribution")));
//				hmEarningSalaryMap.put(rs.getString("org_id")+"_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("erpf_contribution")));
//				hmEarningSalaryMap.put(rs.getString("org_id")+"_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("erps_contribution")));
//				hmEarningSalaryMap.put(rs.getString("org_id")+"_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("erdli_contribution")));
//				hmEarningSalaryMap.put(rs.getString("org_id")+"_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("edli_max_limit")));
//				hmEarningSalaryMap.put(rs.getString("org_id")+"_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("pf_admin_charges")));
//				hmEarningSalaryMap.put(rs.getString("org_id")+"_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("edli_admin_charges")));
//				
//				alEmployees.add(rs.getString("org_id"));
//			}
//			
//			
//			hmEarningSalaryMap.put("Total_EPF_MAX_LIMIT", uF.formatIntoTwoDecimalWithOutComma(dbl_epf_max_limit_Total));
//			hmEarningSalaryMap.put("Total_EPS_MAX_LIMIT", uF.formatIntoTwoDecimalWithOutComma(dbl_eps_max_limit_Total));
//			hmEarningSalaryMap.put("Total_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(dblEEPFContributionTotal));
//			hmEarningSalaryMap.put("Total_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(dblEEVPFContributionTotal));
//			hmEarningSalaryMap.put("Total_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(dblERPFContributionTotal));
//			hmEarningSalaryMap.put("Total_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(dblERPSContributionTotal));
//			hmEarningSalaryMap.put("Total_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(dblERDLIContributionTotal));
//			hmEarningSalaryMap.put("Total_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimalWithOutComma(dblEDLIMaxLimitTotal));
//			hmEarningSalaryMap.put("Total_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimalWithOutComma(dblEPFAdminChargesTotal));
//			hmEarningSalaryMap.put("Total_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimalWithOutComma(dblEDLIAdminChargesTotal));
//			
//			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
//			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
//			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
//			
//			request.setAttribute("hmEmpName", hmEmpName);
//			request.setAttribute("hmEmpCode", hmEmpCode);
//			request.setAttribute("hmEmpPFNumber", hmEmpPFNumber);
//			request.setAttribute("strMonthYear", strMonthYear);
//			request.setAttribute("alEmployees", alEmployees);
//			
//			
//			pst=con.prepareStatement("select org_id,org_name from org_details");
//			Map<String, String> hmOrg=new HashMap<String, String>();
//			rs=pst.executeQuery();
//			while(rs.next()){
//				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
//			}
//			request.setAttribute("hmOrg", hmOrg);
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//
//	}
	
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