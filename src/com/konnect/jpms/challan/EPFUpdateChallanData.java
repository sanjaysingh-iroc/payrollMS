package com.konnect.jpms.challan;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EPFUpdateChallanData extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	String financialYear;
	String strEmpId;
	String strMonth;
	String totalMonths;
	String challanType;
	String strSubmit;
	String[] empIds;
	int totalTaxAmount;
	int totalNumOfEmployee;
	double totalAmount;
	String paidDate;
	 String challanDate;
	 String challanNum;
	 String cheque_no;
	 String operation;
	 int totalNoOfEmp;
	 double totalEPF;
	 double adminCharges;
	 double totalContribution;
	double employerContribution;
	double employeeContribution;
	String strFinancialYearStart;
	String sissionEmp_ID;
	
	String f_org;
	String sbEmp;
	
	

	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		sissionEmp_ID=(String) session.getAttribute(EMPID);
		
//		System.out.println("sbEmp in EPFUpdateChallanData==>"+sbEmp);
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getChallanDate()!=null && getOperation()!=null && getOperation().equalsIgnoreCase("del")){
			deleteChallanDetails(uF);
		}else if(getChallanDate()!=null && getChallanNum()!=null && getOperation().equalsIgnoreCase("update")){
			updateChallanNumber(uF);
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("insert")){
			insertUnpaidAmount(uF);
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("pdf")){
			viewEPFChallanPdfReports(uF);
			generateNewEPFChallanPdfReports(uF);
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("ecrexcel")){
			viewEPFECRData(uF);
			generateEPFECRExcel(uF);
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("ecrtxt")){
			viewEPFECRData(uF);
			generateEPFECRTxt(uF);
		}
		return SUCCESS;
	}

	private void generateEPFECRTxt(UtilityFunctions uF) {
		try {
			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}
			Map<String,String> hmOrg = (Map<String, String>)request.getAttribute("hmOrg"); 
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			Map<String,String> hmEPFECRData = (Map<String, String>)request.getAttribute("hmEPFECRData");
			if(hmEPFECRData == null) hmEPFECRData = new HashMap<String, String>();
			Map<String,String> hmEmployeeMap = (Map<String, String>)request.getAttribute("hmEmployeeMap");
			if(hmEmployeeMap == null) hmEmployeeMap = new LinkedHashMap<String, String>();
			Map<String,String> hmEmpEPFACNO = (Map<String, String>)request.getAttribute("hmEmpEPFACNO");
			if(hmEmpEPFACNO == null) hmEmpEPFACNO = new HashMap<String, String>();
			Map<String,String> hmEmpGender = (Map<String, String>)request.getAttribute("hmEmpGender");
			if(hmEmpGender == null) hmEmpGender = new HashMap<String, String>();
			Map<String,String> hmEmpDOB = (Map<String, String>)request.getAttribute("hmEmpDOB");
			if(hmEmpDOB == null) hmEmpDOB = new HashMap<String, String>();
			Map<String,String> hmEmpRelation = (Map<String, String>)request.getAttribute("hmEmpRelation");
			if(hmEmpRelation == null) hmEmpRelation = new HashMap<String, String>();
			Map<String,String> hmEmpFatherHusband = (Map<String, String>)request.getAttribute("hmEmpFatherHusband");
			if(hmEmpFatherHusband == null) hmEmpFatherHusband = new HashMap<String, String>();
			Map<String,String> hmEmpJoiningDate = (Map<String, String>)request.getAttribute("hmEmpJoiningDate");
			if(hmEmpJoiningDate == null) hmEmpJoiningDate = new HashMap<String, String>();
			Map<String,String> hmEmpEndDate = (Map<String, String>)request.getAttribute("hmEmpEndDate");
			if(hmEmpEndDate == null) hmEmpEndDate = new HashMap<String, String>();
			Map<String,String> hmEmpAbsentDays = (Map<String, String>)request.getAttribute("hmEmpAbsentDays");
			if(hmEmpAbsentDays == null) hmEmpAbsentDays = new HashMap<String, String>();    
			
			Map<String,String> hmEmpGrossWages = (Map<String, String>)request.getAttribute("hmEmpGrossWages");
			if(hmEmpGrossWages == null) hmEmpGrossWages = new HashMap<String, String>(); 
			Map<String,String> hmEmpUAN = (Map<String, String>)request.getAttribute("hmEmpUAN");
			if(hmEmpUAN == null) hmEmpUAN = new HashMap<String, String>(); 			
			
			StringBuffer sb = new StringBuffer();
			String TILDA  = "#~#";
			
			Iterator<String> it = hmEmployeeMap.keySet().iterator();
			while (it.hasNext()){
				String strEmpId = it.next();
				String strEmpName = hmEmployeeMap.get(strEmpId);
				
				sb.append(uF.showData(hmEmpUAN.get(strEmpId), "")); 
				sb.append(TILDA);
				sb.append(strEmpName); 
				sb.append(TILDA);
				sb.append(uF.formatIntoZeroWithOutComma(uF.parseToDouble(hmEmpGrossWages.get(strEmpId)))); 
				sb.append(TILDA);				
				sb.append(uF.formatIntoZeroWithOutComma(uF.parseToDouble(hmEPFECRData.get(strEmpId+"_EPF_MAX_LIMIT")))); 
				sb.append(TILDA);
				sb.append(uF.formatIntoZeroWithOutComma(uF.parseToDouble(hmEPFECRData.get(strEmpId+"_EPS_MAX_LIMIT")))); 
				sb.append(TILDA);
				sb.append(uF.formatIntoZeroWithOutComma(uF.parseToDouble(hmEPFECRData.get(strEmpId+"_EDLI_MAX_LIMIT")))); 
				sb.append(TILDA);				
				sb.append(uF.formatIntoZeroWithOutComma(uF.parseToDouble(hmEPFECRData.get(strEmpId+"_EEPF_CONTRIBUTION")))); 
				sb.append(TILDA);
				sb.append(uF.formatIntoZeroWithOutComma(uF.parseToDouble(hmEPFECRData.get(strEmpId+"_ERPS_CONTRIBUTION")))); 
				sb.append(TILDA);
				double diffEpfEps = uF.parseToDouble(uF.formatIntoZeroWithOutComma(uF.parseToDouble(hmEPFECRData.get(strEmpId+"_EEPF_CONTRIBUTION")))) - uF.parseToDouble(uF.formatIntoZeroWithOutComma(uF.parseToDouble(hmEPFECRData.get(strEmpId+"_ERPS_CONTRIBUTION"))));
				sb.append(uF.formatIntoZeroWithOutComma(diffEpfEps)); 
				sb.append(TILDA);
//				sb.append(uF.formatIntoZeroWithOutComma(uF.parseToDouble(hmEmpAbsentDays.get(strEmpId)))); 
				sb.append("");
				sb.append(TILDA);
				sb.append("");
				sb.append("\n");
//				sb.append(System.getProperty("line.separator"));
				
			}
			
			String strData = sb.toString();
			ServletOutputStream op = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setContentLength((int) strData.length());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + "EPF_ECR.txt");
			op.write(strData.getBytes());
			op.flush();
			op.close();
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//private void generateEPFECRTxt(UtilityFunctions uF) {
//	try {
//		String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
//		String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
//		if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
//			strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
//			strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
//		}
//		Map<String,String> hmOrg = (Map<String, String>)request.getAttribute("hmOrg"); 
//		if(hmOrg == null) hmOrg = new HashMap<String, String>();
//		Map<String,String> hmEPFECRData = (Map<String, String>)request.getAttribute("hmEPFECRData");
//		if(hmEPFECRData == null) hmEPFECRData = new HashMap<String, String>();
//		Map<String,String> hmEmployeeMap = (Map<String, String>)request.getAttribute("hmEmployeeMap");
//		if(hmEmployeeMap == null) hmEmployeeMap = new LinkedHashMap<String, String>();
//		Map<String,String> hmEmpEPFACNO = (Map<String, String>)request.getAttribute("hmEmpEPFACNO");
//		if(hmEmpEPFACNO == null) hmEmpEPFACNO = new HashMap<String, String>();
//		Map<String,String> hmEmpGender = (Map<String, String>)request.getAttribute("hmEmpGender");
//		if(hmEmpGender == null) hmEmpGender = new HashMap<String, String>();
//		Map<String,String> hmEmpDOB = (Map<String, String>)request.getAttribute("hmEmpDOB");
//		if(hmEmpDOB == null) hmEmpDOB = new HashMap<String, String>();
//		Map<String,String> hmEmpRelation = (Map<String, String>)request.getAttribute("hmEmpRelation");
//		if(hmEmpRelation == null) hmEmpRelation = new HashMap<String, String>();
//		Map<String,String> hmEmpFatherHusband = (Map<String, String>)request.getAttribute("hmEmpFatherHusband");
//		if(hmEmpFatherHusband == null) hmEmpFatherHusband = new HashMap<String, String>();
//		Map<String,String> hmEmpJoiningDate = (Map<String, String>)request.getAttribute("hmEmpJoiningDate");
//		if(hmEmpJoiningDate == null) hmEmpJoiningDate = new HashMap<String, String>();
//		Map<String,String> hmEmpEndDate = (Map<String, String>)request.getAttribute("hmEmpEndDate");
//		if(hmEmpEndDate == null) hmEmpEndDate = new HashMap<String, String>();
//		Map<String,String> hmEmpAbsentDays = (Map<String, String>)request.getAttribute("hmEmpAbsentDays");
//		if(hmEmpAbsentDays == null) hmEmpAbsentDays = new HashMap<String, String>();    
//
//		int count=0;
//		List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>(); 
////		StringBuilder sb = new StringBuilder();
//		StringBuffer sb = new StringBuffer();
//		String TILDA  = "#~#";
//		
//		Iterator<String> it = hmEmployeeMap.keySet().iterator();
//		while (it.hasNext()){
//			String strEmpId = it.next();
//			String strEmpName = hmEmployeeMap.get(strEmpId);
//			
//			sb.append(uF.showData(hmEmpEPFACNO.get(strEmpId), "")); 
//			sb.append(TILDA);
//			sb.append(strEmpName); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEPFECRData.get(strEmpId+"_EPF_MAX_LIMIT"), "0")); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEPFECRData.get(strEmpId+"_EPS_MAX_LIMIT"), "0")); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEPFECRData.get(strEmpId+"_EEPF_CONTRIBUTION"), "")); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEPFECRData.get(strEmpId+"_EEPF_CONTRIBUTION"), "")); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEPFECRData.get(strEmpId+"_ERPS_CONTRIBUTION"), "")); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEPFECRData.get(strEmpId+"_ERPS_CONTRIBUTION"), "")); 
//			sb.append(TILDA);
//			double diffEpfEps = uF.parseToDouble(hmEPFECRData.get(strEmpId+"_EEPF_CONTRIBUTION")) - uF.parseToDouble(hmEPFECRData.get(strEmpId+"_ERPS_CONTRIBUTION"));
//			sb.append(uF.formatIntoTwoDecimalWithOutComma(diffEpfEps)); 
//			sb.append(TILDA);
//			sb.append(uF.formatIntoTwoDecimalWithOutComma(diffEpfEps)); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEmpAbsentDays.get(strEmpId), "")); 
//			sb.append(TILDA);
//			sb.append("0"); 
//			sb.append(TILDA);
//			sb.append("0"); 
//			sb.append(TILDA);
//			sb.append("0"); 
//			sb.append(TILDA);
//			sb.append("0"); 
//			sb.append(TILDA);
//			sb.append("0"); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEmpFatherHusband.get(strEmpId), "")); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEmpRelation.get(strEmpId), "")); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEmpDOB.get(strEmpId), "")); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEmpGender.get(strEmpId), "")); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEmpJoiningDate.get(strEmpId), "")); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEmpJoiningDate.get(strEmpId), "")); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEmpEndDate.get(strEmpId), "")); 
//			sb.append(TILDA);
//			sb.append(uF.showData(hmEmpEndDate.get(strEmpId), "")); 
//			sb.append(TILDA);
//			sb.append(""); 
//			sb.append(System.getProperty("line.separator"));
//			
//		}
//		
//		String strData = sb.toString();
//		ServletOutputStream op = response.getOutputStream();
//		response.setContentType("application/octet-stream");
//		response.setContentLength((int) strData.length());
//		response.setHeader("Content-Disposition", "attachment; filename=\"" + "EPF_ECR.txt");
//		op.write(strData.getBytes());
//		op.flush();
//		op.close();
//		
//		
//		
//	}catch (Exception e) {
//		e.printStackTrace();
//	}
//}

private void generateEPFECRExcel(UtilityFunctions uF) {
		
	try {

		String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
		String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
		if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
			strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		}
		Map<String,String> hmOrg = (Map<String, String>)request.getAttribute("hmOrg"); 
		if(hmOrg == null) hmOrg = new HashMap<String, String>();
		Map<String,String> hmEPFECRData = (Map<String, String>)request.getAttribute("hmEPFECRData");
		if(hmEPFECRData == null) hmEPFECRData = new HashMap<String, String>();
		Map<String,String> hmEmployeeMap = (Map<String, String>)request.getAttribute("hmEmployeeMap");
		if(hmEmployeeMap == null) hmEmployeeMap = new LinkedHashMap<String, String>();
		Map<String,String> hmEmpEPFACNO = (Map<String, String>)request.getAttribute("hmEmpEPFACNO");
		if(hmEmpEPFACNO == null) hmEmpEPFACNO = new HashMap<String, String>();
		Map<String,String> hmEmpGender = (Map<String, String>)request.getAttribute("hmEmpGender");
		if(hmEmpGender == null) hmEmpGender = new HashMap<String, String>();
		Map<String,String> hmEmpDOB = (Map<String, String>)request.getAttribute("hmEmpDOB");
		if(hmEmpDOB == null) hmEmpDOB = new HashMap<String, String>();
		Map<String,String> hmEmpRelation = (Map<String, String>)request.getAttribute("hmEmpRelation");
		if(hmEmpRelation == null) hmEmpRelation = new HashMap<String, String>();
		Map<String,String> hmEmpFatherHusband = (Map<String, String>)request.getAttribute("hmEmpFatherHusband");
		if(hmEmpFatherHusband == null) hmEmpFatherHusband = new HashMap<String, String>();
		Map<String,String> hmEmpJoiningDate = (Map<String, String>)request.getAttribute("hmEmpJoiningDate");
		if(hmEmpJoiningDate == null) hmEmpJoiningDate = new HashMap<String, String>();
		Map<String,String> hmEmpEndDate = (Map<String, String>)request.getAttribute("hmEmpEndDate");
		if(hmEmpEndDate == null) hmEmpEndDate = new HashMap<String, String>();
		Map<String,String> hmEmpAbsentDays = (Map<String, String>)request.getAttribute("hmEmpAbsentDays");
		if(hmEmpAbsentDays == null) hmEmpAbsentDays = new HashMap<String, String>();    

		//===start Parvez===
		Map<String,String> hmEmpUAN = (Map<String, String>)request.getAttribute("hmEmpUAN");
		if(hmEmpUAN == null) hmEmpUAN = new HashMap<String, String>();
		//===end parvez===
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("EPF ECR");			
		
		List<DataStyle> header=new ArrayList<DataStyle>();
		header.add(new DataStyle("Member ID",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Member Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("EPF Wages",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("EPS Wages",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("EPF Contribution (EE Share) due",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("EPF Contribution (EE Share) being remitted",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("EPS Contribution due",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("EPS Contribution being remitted",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Diff EPF and EPS Contribution (ER Share) due",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Diff EPF and EPS Contribution (ER Share) being remitted",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("NCP Days",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Refund of Advances",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Arrear EPF Wages",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Arrear EPF EE Share",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Arrear EPF ER Share",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Arrear EPS Share",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Father’s/Husband’s Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Relationship with the Member",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Date of Birth",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Gender",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Date of Joining EPF",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Date of Joining EPS",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Date of Exit from EPF",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Date of Exit from EPS",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Reason for leaving",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		
		
		int count=0;
		List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>(); 
		
		Iterator<String> it = hmEmployeeMap.keySet().iterator();
		while (it.hasNext()){
			String strEmpId = it.next();
			String strEmpName = hmEmployeeMap.get(strEmpId);
			
			List<DataStyle> innerList=new ArrayList<DataStyle>();
			//===start Parvez===
			//innerList.add(new DataStyle(uF.showData(hmEmpEPFACNO.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEmpUAN.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			//===end parvez===
			innerList.add(new DataStyle(strEmpName,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEPFECRData.get(strEmpId+"_EPF_MAX_LIMIT"), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEPFECRData.get(strEmpId+"_EPS_MAX_LIMIT"), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEPFECRData.get(strEmpId+"_EEPF_CONTRIBUTION"), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEPFECRData.get(strEmpId+"_EEPF_CONTRIBUTION"), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEPFECRData.get(strEmpId+"_ERPS_CONTRIBUTION"), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEPFECRData.get(strEmpId+"_ERPS_CONTRIBUTION"), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			double diffEpfEps = uF.parseToDouble(hmEPFECRData.get(strEmpId+"_EEPF_CONTRIBUTION")) - uF.parseToDouble(hmEPFECRData.get(strEmpId+"_ERPS_CONTRIBUTION"));  
			innerList.add(new DataStyle(uF.formatIntoTwoDecimalWithOutComma(diffEpfEps),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.formatIntoTwoDecimalWithOutComma(diffEpfEps),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEmpAbsentDays.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("0",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("0",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("0",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("0",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("0",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEmpFatherHusband.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEmpRelation.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEmpDOB.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEmpGender.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEmpJoiningDate.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEmpJoiningDate.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEmpEndDate.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData(hmEmpEndDate.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			reportData.add(innerList);
		}
		
		
		ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
		sheetDesign.generateDefualtExcelSheet(workbook,sheet,header,reportData);
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		workbook.write(buffer);
		response.setContentType("application/vnd.ms-excel:UTF-8");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=EPF_ECR_Excel.xls");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		buffer.close();
		out.close();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}
	private void viewEPFECRData(UtilityFunctions uF) {
//		System.out.println(" in viewEPFECRData");
		
//		System.out.println(" in viewEPFECRData");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		String[] strPayCycleDates = null;
//		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		String strMonthYear = null;
		try{
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
			strMonthYear = uF.getDateFormat(strDateStart, DATE_FORMAT, "MM/yyyy");	
			request.setAttribute("strMonthYear", strMonthYear);
		
			Calendar cal1 = GregorianCalendar.getInstance();
			cal1.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
			cal1.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM")) -1);
			cal1.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
			List<String> alMonth = new ArrayList<String>();
			Map<String, String> hmMonthDayCount = new HashMap<String, String>();
			for(int i=0; i<12; i++){
				alMonth.add(uF.getDateFormat(""+(cal1.get(Calendar.MONTH)+1),"MM","MM"));

				int iDay = cal1.get(Calendar.DATE);
				int iMonth = cal1.get(Calendar.MONTH);
				int iYear = cal1.get(Calendar.YEAR);

				// Create a calendar object and set year and month
				Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);

				// Get the number of days in that month
				int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
				
				hmMonthDayCount.put(""+(iMonth+1), ""+daysInMonth);
				
				cal.add(Calendar.MONTH, 1);
				
			}
		
		
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
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
				hmOrg.put("ORG_TRRN_EPF", rs.getString("trrn_epf"));
				hmOrg.put("ORG_EPF_ACCOUNT_NO", rs.getString("epf_account_no"));
			} 
	        rs.close();
	        pst.close(); 
			
			Set<String> empIdsSet = new HashSet<String>();  
			/*pst = con.prepareStatement("select distinct(emp_id) as emp_id,month from challan_details where challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ")" +
					" and is_paid=? and entry_date=? and emp_id in (select emp_id from employee_official_details where org_id=?) " +
					"and financial_year_from_date=? and financial_year_to_date=?");
			*/
			
			StringBuilder sb= new StringBuilder();
			sb.append("select distinct(emp_id) as emp_id,month from challan_details where challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ")" +
					" and is_paid=? and entry_date=?");
			if(sbEmp!=null && !sbEmp.equals(""))
			{
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in(0)");
			}
			sb.append(" and financial_year_from_date=? and financial_year_to_date=?");
			pst=con.prepareStatement(sb.toString());
			
			pst.setBoolean(1, false);
			pst.setDate(2, uF.getDateFormat(getChallanDate(), DBDATE));
			//pst.setInt(3, uF.parseToInt(getF_org()));
			pst.setDate(3,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst in viewEPFECRData====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				totalNoOfEmp++;
				empIdsSet.add(rs.getString("emp_id"));
				
			}
	        rs.close();
	        pst.close();
			
			StringBuilder sbEmpIds = null;
			for(String strEmpIds : empIdsSet){
				if(sbEmpIds == null){
					sbEmpIds = new StringBuilder();
					sbEmpIds.append(strEmpIds);
				} else {
					sbEmpIds.append(","+strEmpIds);
				}
			}
			Map<String,String> hmEPFECRData=new HashMap<String, String>();
			Map<String, String> hmEmployeeMap = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpEPFACNO = new HashMap<String, String>();
			Map<String, String> hmEmpDOB = new HashMap<String, String>();
			Map<String, String> hmEmpGender = new HashMap<String, String>();
			Map<String, String> hmEmpAbsentDays = new HashMap<String, String>();
			Map<String, String> hmEmpFatherHusband = new HashMap<String, String>();
			Map<String, String> hmEmpRelation = new HashMap<String, String>();
			Map<String, String> hmEmpJoiningDate = new HashMap<String, String>();
			Map<String, String> hmEmpEndDate = new HashMap<String, String>(); 
			Map<String, String> hmEmpGrossWages = new HashMap<String, String>(); 
			Map<String, String> hmEmpUAN = new HashMap<String, String>(); 
			
			if(sbEmpIds!=null) {
				pst = con.prepareStatement("select * from emp_family_members where emp_id in (select epd.emp_per_id from employee_personal_details epd," +
						"employee_official_details eod  where epd.emp_per_id=eod.emp_id " +
						"and eod.emp_id in("+sbEmpIds.toString()+")) order by emp_id");
				
				rs= pst.executeQuery();
//				System.out.println("pst 2 in viewEPFECRData====>"+pst);
				while(rs.next()){
					if(rs.getString("member_type")!=null && rs.getString("member_type").equalsIgnoreCase("SPOUSE")){
						hmEmpFatherHusband.put(rs.getString("emp_id"),rs.getString("member_name"));
						hmEmpRelation.put(rs.getString("emp_id"), "S");
					}else if(rs.getString("member_type")!=null && rs.getString("member_type").equalsIgnoreCase("FATHER")){
						hmEmpFatherHusband.put(rs.getString("emp_id"),rs.getString("member_name"));
						hmEmpRelation.put(rs.getString("emp_id"), "F");
					}				
				}
		        rs.close();
		        pst.close();
				
				pst = con.prepareStatement("SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id " +
						"and eod.emp_id in("+sbEmpIds.toString()+") order by emp_fname");
//				System.out.println("pst 3 in viewEPFECRData====>"+pst);

				rs = pst.executeQuery();
				while (rs.next()) {
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					hmEmployeeMap.put(rs.getString("emp_id"), rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
					hmEmpEPFACNO.put(rs.getString("emp_id"), rs.getString("emp_pf_no"));
					hmEmpDOB.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT).equals("-") ? "" : uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
					hmEmpGender.put(rs.getString("emp_id"), rs.getString("emp_gender"));
					hmEmpJoiningDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT).equals("-") ? "" : uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
					hmEmpEndDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT).equals("-") ? "" : uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
					hmEmpUAN.put(rs.getString("emp_id"), uF.showData(rs.getString("uan_no"), ""));
				}
		        rs.close();
		        pst.close();
			
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from emp_epf_details where financial_year_start=?  and financial_year_end=? and _month=? and emp_id in("+sbEmpIds.toString()+") ");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getStrMonth()));
//				System.out.println("pst 4 in viewEPFECRData====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					hmEPFECRData.put(rs.getString("emp_id")+"_EPF_MAX_LIMIT", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("epf_max_limit")));
					hmEPFECRData.put(rs.getString("emp_id")+"_EPS_MAX_LIMIT", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("eps_max_limit")));
					hmEPFECRData.put(rs.getString("emp_id")+"_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("eepf_contribution")));
					hmEPFECRData.put(rs.getString("emp_id")+"_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("evpf_contribution")));
					hmEPFECRData.put(rs.getString("emp_id")+"_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("erpf_contribution")));
					hmEPFECRData.put(rs.getString("emp_id")+"_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("erps_contribution")));
					hmEPFECRData.put(rs.getString("emp_id")+"_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("erdli_contribution")));
					hmEPFECRData.put(rs.getString("emp_id")+"_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("edli_max_limit")));
					hmEPFECRData.put(rs.getString("emp_id")+"_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("pf_admin_charges")));
					hmEPFECRData.put(rs.getString("emp_id")+"_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("edli_admin_charges")));
					
				}
		        rs.close();
		        pst.close();
				
				pst = con.prepareStatement("select emp_id,paid_days,month from payroll_generation where emp_id in("+sbEmpIds.toString()+") " +
						"and financial_year_from_date=? and financial_year_to_date=? and month=? group by emp_id,paid_days,month order by emp_id,paid_days,month");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getStrMonth()));
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					String strDays = hmMonthDayCount.get(rs.getString("month"));
					double strEmpPaidDays = uF.parseToDouble(rs.getString("paid_days"));
					double dblAbsent = uF.parseToDouble(strDays) - strEmpPaidDays;
					if(dblAbsent==uF.parseToDouble(strDays)){
						dblAbsent=0.0d;
					}
					dblAbsent = dblAbsent>0.0d ? dblAbsent: 0.0d;
					
					hmEmpAbsentDays.put(rs.getString("emp_id"), ""+dblAbsent);
				}
		        rs.close();
		        pst.close();
		        
		        pst = con.prepareStatement("select emp_id,sum(amount) as amount from payroll_generation where emp_id in("+sbEmpIds.toString()+") " +
		        		"and financial_year_from_date=? and financial_year_to_date=? and month=? and earning_deduction='E' group by emp_id order by emp_id");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getStrMonth()));
		//		System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					double dblAmount = uF.parseToDouble(rs.getString("amount"));
					hmEmpGrossWages.put(rs.getString("emp_id"), ""+dblAmount);
				}
		        rs.close();
		        pst.close();
		         
			}
			
//			System.out.println("hmEPFECRData=====>"+hmEPFECRData);
			request.setAttribute("hmEPFECRData", hmEPFECRData); 
			request.setAttribute("hmOrg", hmOrg);
			request.setAttribute("hmEmployeeMap", hmEmployeeMap);
			request.setAttribute("hmEmpEPFACNO", hmEmpEPFACNO);
			request.setAttribute("hmEmpDOB", hmEmpDOB);
			request.setAttribute("hmEmpGender", hmEmpGender); 
			request.setAttribute("hmEmpAbsentDays", hmEmpAbsentDays); 
			request.setAttribute("hmEmpFatherHusband", hmEmpFatherHusband); 
			request.setAttribute("hmEmpRelation", hmEmpRelation); 
			request.setAttribute("hmEmpJoiningDate", hmEmpJoiningDate);
			request.setAttribute("hmEmpEndDate", hmEmpEndDate);
			request.setAttribute("hmEmpGrossWages", hmEmpGrossWages);
			request.setAttribute("hmEmpUAN", hmEmpUAN);
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	private void generateNewEPFChallanPdfReports(UtilityFunctions uF) {
		
		try {
			List<Integer> alList=(List<Integer>)request.getAttribute("alList");
			Map<String,String> hmempcnt =(Map<String,String> )request.getAttribute("hmempcnt");
			Map<Integer,Map<String,String>> hmMap=(Map<Integer,Map<String,String>>)request.getAttribute("hmMap");
			if(hmMap==null)hmMap=new HashMap<Integer, Map<String,String>>();
			
			Map<String,String> hmEPFChallan=(Map<String,String>)request.getAttribute("hmEPFChallan");
			
			Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
			Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
			Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			Font small = new Font(Font.FontFamily.HELVETICA,7);
			Font smallBold = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD);
			Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        Document document = new Document(PageSize.A4);
	        PdfWriter.getInstance(document,buffer);
	        document.open();
	        
	        PdfPTable table = new PdfPTable(10);
			table.setWidthPercentage(100);       
			int[] cols = {3,10,10,10,10,10,10,10,10,10};
			table.setWidths(cols);
	        
	        String heading1="COMBINED CHALLAN OF A/C N0.01,02,10,21 & 22 (With ECR)\n(STATE BANK OF INDIA)\n" +
	        		"EMPLOYEES' PROVIDENT FUND ORGANISATION\nVASHI";
	        
	        PdfPCell row1 =new PdfPCell(new Paragraph(heading1,smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("TRRN: "+uF.showData(hmOrg.get("ORG_TRRN_EPF"), "")+"\nEmployer E-Sewa",smallBold)); 
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("ESTABLISHMENT CODE & NAME : "+uF.showData(hmOrg.get("ORG_NAME"), ""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("ADDRESS : "+uF.showData(hmOrg.get("ORG_ADDRESS"), ""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);
	        table.addCell(row1);
	        //row1 =new PdfPCell(new Paragraph("Dues for the wage month of : "+uF.getDateFormat(getChallanDate(),DBDATE,CF.getStrReportDateFormat()),small));
	        row1 =new PdfPCell(new Paragraph("Dues for the wage month of : "+uF.showData((String)request.getAttribute("strMonthYear"), ""),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(10);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("TOTAL SUBSCRIBERS : ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);	        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("A/C.01 ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.showData(hmEPFChallan.get("TOTAL_EMP"),""),smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("A/C.10 ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.showData(hmEPFChallan.get("TOTAL_EMP"),""),smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("A/C.21 ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.showData(hmEPFChallan.get("TOTAL_EMP"),""),smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);	        
	        table.addCell(row1);
	        
	        //NEW ROW
	        row1 =new PdfPCell(new Paragraph("TOTAL WAGES :",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);	        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("A/C.01 ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.getRoundOffValue(0, uF.parseToDouble(hmEPFChallan.get("EPF_MAX_LIMIT"))), smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("A/C.10 ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.getRoundOffValue(0, uF.parseToDouble(hmEPFChallan.get("EPS_MAX_LIMIT"))), smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("A/C.21 ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.getRoundOffValue(0, uF.parseToDouble(hmEPFChallan.get("EDLI_MAX_LIMIT"))), smallBold));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(2);	        
	        table.addCell(row1);
	        
	        //NEW ROW
	        row1 =new PdfPCell(new Paragraph("SL.",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("PARTICULARS",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(3);	        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("A/C.01",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("A/C.02",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("A/C.10",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("A/C.21",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("A/C.22",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("TOTAL",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        table.addCell(row1);
	        
	        //NEW ROW	        
	        row1 =new PdfPCell(new Paragraph("1.",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("EMPLOYER'S SHARE OF CONT.",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);	        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.getRoundOffValue(0, uF.parseToDouble(hmEPFChallan.get("ERPF_CONTRIBUTION"))), small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.getRoundOffValue(0, uF.parseToDouble(hmEPFChallan.get("ERPS_CONTRIBUTION"))), small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.getRoundOffValue(0, uF.parseToDouble(hmEPFChallan.get("ERDLI_CONTRIBUTION"))), small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        double totalEmployerContribution=uF.parseToDouble(hmEPFChallan.get("ERPF_CONTRIBUTION"))+uF.parseToDouble(hmEPFChallan.get("ERPS_CONTRIBUTION"))+uF.parseToDouble(hmEPFChallan.get("ERDLI_CONTRIBUTION"));
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.getRoundOffValue(0, totalEmployerContribution), small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        //NEW ROW
	        row1 =new PdfPCell(new Paragraph("2.",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("EMPLOYEE'S SHARE OF CONT.",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);	        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.getRoundOffValue(0, (uF.parseToDouble(hmEPFChallan.get("EEPF_CONTRIBUTION"))+uF.parseToDouble(hmEPFChallan.get("EEVPF_CONTRIBUTION")))), small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.getRoundOffValue(0, (uF.parseToDouble(hmEPFChallan.get("EEPF_CONTRIBUTION"))+uF.parseToDouble(hmEPFChallan.get("EEVPF_CONTRIBUTION")))), small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	      //NEW ROW
	        row1 =new PdfPCell(new Paragraph("3.",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("ADMIN CHARGES",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);	        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.getRoundOffValue(0, uF.parseToDouble(hmEPFChallan.get("PF_ADMIN_CHARGES"))), small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.getRoundOffValue(0, uF.parseToDouble(hmEPFChallan.get("EDLI_ADMIN_CHARGES"))), small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        double totalAdminCharges=uF.parseToDouble(hmEPFChallan.get("PF_ADMIN_CHARGES"))+Math.round(uF.parseToDouble(hmEPFChallan.get("EDLI_ADMIN_CHARGES")));
	        row1 =new PdfPCell(new Paragraph(""+uF.getRoundOffValue(0, totalAdminCharges), small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	       
	      //NEW ROW
	        row1 =new PdfPCell(new Paragraph("4.",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("INSPECTION CHARGES",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);	        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	      //NEW ROW
	        row1 =new PdfPCell(new Paragraph("5.",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("PENAL DAMAGES",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);	        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	      //NEW ROW
	        row1 =new PdfPCell(new Paragraph("6.",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("MISC. PAYMENT (INTEREST U/S 7Q)",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setColspan(3);	        
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        double totalAmt = uF.parseToDouble(uF.formatIntoZeroWithOutComma((totalEmployerContribution+uF.parseToDouble(hmEPFChallan.get("EEPF_CONTRIBUTION"))+uF.parseToDouble(hmEPFChallan.get("EEVPF_CONTRIBUTION"))+totalAdminCharges)));
	       
	        String digitTotal="";
	        String strTotalAmt=""+totalAmt;
	        if(strTotalAmt.contains(".")){
	        	strTotalAmt=strTotalAmt.replace(".", ",");
	        	String[] temp=strTotalAmt.split(",");
	        	digitTotal=uF.digitsToWords(uF.parseToInt(temp[0]));
	        	if(uF.parseToInt(temp[1])>0){
	        		int pamt=0;
	        		if(temp[1].length()==1){
	        			pamt=uF.parseToInt(temp[1]+"0");
	        		}else{
	        			pamt=uF.parseToInt(temp[1]);
	        		}
	        		digitTotal+=" and "+uF.digitsToWords(pamt)+" paise";
	        	}
	        }else{
	        	int totalAmt1=(int)totalAmt;
	        	digitTotal=uF.digitsToWords(totalAmt1);
	        }
	        
	        
	        //NEW ROW
	        row1 =new PdfPCell(new Paragraph("GRAND TOTAL (IN WORDS) : Rs. "+digitTotal, small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        row1.setColspan(8);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(" ",small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(""+uF.getRoundOffValue(0, totalAmt), small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
	        table.addCell(row1);
	        
	        //NEW ROW
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setColspan(10);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        //NEW ROW
	        StringBuilder sbBankUse=new StringBuilder();
	        sbBankUse.append("FOR BANK USE ONLY\n");
	        sbBankUse.append("Amount Received Rs. ------------------------------------\n");
	        sbBankUse.append("Date of presentation of Cheque/DD. ---------------------\n");
	        sbBankUse.append("Date of Realisation of Cheque/DD. ----------------------\n");
	        sbBankUse.append("SBI Branch Name ----------------------------------------\n");
	        sbBankUse.append("SBI Branch Code ----------------------------------------\n");
	        
	        row1 =new PdfPCell(new Paragraph(sbBankUse.toString(),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(5); 
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	      //NEW ROW
	        StringBuilder sbEstablishmnetUse=new StringBuilder();
	        sbEstablishmnetUse.append("FOR ESTABLISHMENT USE ONLY (To be manually filled by Employer)\n");
	        sbEstablishmnetUse.append("Cheque/DD No. ------------------------- Date:-----------------\n");
	        sbEstablishmnetUse.append("Cheque/DD drawn bank & Branch --------------------------------\n");
	        sbEstablishmnetUse.append("Name of the Depositor ----------------------------------------\n");
	        sbEstablishmnetUse.append("Date of Deposit ----------------- Mobile No.------------------\n");
	        sbEstablishmnetUse.append("Signature of the Depositor -----------------------------------\n");
	        
	        row1 =new PdfPCell(new Paragraph(sbEstablishmnetUse.toString(),small));
	        row1.setPadding(2.5f);
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setColspan(5);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	      //NEW ROW
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setPadding(2.5f);
	        row1.setColspan(10);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	      //NEW ROW
	        String note="(This is a system generated challan on "+uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT)+" "+uF.getDateFormat(""+uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, TIME_FORMAT)+", the particulars shown in this challan " +
	        		"are populated from the Electronics Challan Return(ECR) updated by the establishment for the specified month and year. " +
	        		"Remittance can be made through a local Cheque/DD in any designated branch of SBI)";
	        row1 =new PdfPCell(new Paragraph(note,small));
	        row1.setPadding(2.5f);
	        row1.setColspan(10);
	        row1.setBorder(Rectangle.NO_BORDER);
	        table.addCell(row1);
	        
	        
	        
	        
	        document.add(table);
	        
	        document.close();
	        
			String filename="EPFChallan_"+uF.getDateFormat(getChallanDate(),DBDATE, "MM")+"_"+uF.getDateFormat(getChallanDate(),DBDATE, "yyyy")+".pdf";
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
	public void deleteChallanDetails(UtilityFunctions uF)
	{
//		System.out.println("in deleteChallanDetails");
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			con = db.makeConnection(con);
			
			/*pst = con.prepareStatement("delete from challan_details WHERE entry_date =? and is_paid=? and challan_type in ("+EMPLOYEE_EPF+","+EMPLOYER_EPF+") " +
					"and emp_id in (select emp_id from employee_official_details where org_id=?) ");
			*/
			
			StringBuilder sb=new StringBuilder();
			sb.append("delete from challan_details WHERE entry_date =? and is_paid=? and challan_type in ("+EMPLOYEE_EPF+","+EMPLOYER_EPF+") ");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			pst=con.prepareStatement(sb.toString());
			pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setBoolean(2,false);
//			System.out.println(" pst in for delete challan in EPF==>"+pst);
			//pst.setInt(3, uF.parseToInt(getF_org()));
			pst.executeUpdate();
	        pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		} 
		
	}
	public void updateChallanNumber(UtilityFunctions uF)
	{
//		System.out.println("in updateChallanNumber");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String[] strPayCycleDates = null;
		String strFinancialYearEnd = null;
		try{
			if (getFinancialYear() != null) {

				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];

			} else {

				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-"+ strPayCycleDates[1]);

				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];

			}
			con = db.makeConnection(con);
			/*pst = con.prepareStatement("UPDATE challan_details SET is_paid =?,paid_date=?,challan_no=?,cheque_no=?,added_by=? WHERE financial_year_from_date=?" +
						" and financial_year_to_date=? and entry_date =? and challan_type in ("+EMPLOYEE_EPF+","+EMPLOYER_EPF+") and is_paid = false " +
						"and emp_id in (select emp_id from employee_official_details where org_id=?)");
			*/
			StringBuilder sb=new StringBuilder();
			sb.append("UPDATE challan_details SET is_paid =?,paid_date=?,challan_no=?,cheque_no=?,added_by=? WHERE financial_year_from_date=?" +
						" and financial_year_to_date=? and entry_date =? and challan_type in ("+EMPLOYEE_EPF+","+EMPLOYER_EPF+") and is_paid = false ");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			pst=con.prepareStatement(sb.toString());
			
			pst.setBoolean(1,true);
			pst.setDate(2, uF.getDateFormat(getPaidDate(), DATE_FORMAT));
			pst.setString(3,getChallanNum());
			pst.setString(4,getCheque_no());
			pst.setInt(5,uF.parseToInt(sissionEmp_ID));
			pst.setDate(6, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(7, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(getChallanDate(), DBDATE));
			//pst.setInt(9, uF.parseToInt(getF_org()));
	//		System.out.println(" pst in updatechallannumber==>"+pst);
			pst.executeUpdate();
	        pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	public void insertUnpaidAmount(UtilityFunctions uF)
	{
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String[] strPayCycleDates = null;
		
		String strFinancialYearEnd = null;
		String months="";
		try{
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
			
//			System.out.println("getEmpIds===>"+getEmpIds());
			
			if(getEmpIds()!=null){
			for(int i=0;i<getEmpIds().length;i++)
			{
				double amount=0;
				pst = con.prepareStatement("select sum(eepf_contribution) as eepf_contribution,sum(evpf_contribution) as evpf_contribution from emp_epf_details where financial_year_start=?" +
						" and financial_year_end=? and _month in ("+totalMonths+") and emp_id=?");
																
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3,uF.parseToInt(getEmpIds()[i]));
				rs = pst.executeQuery();
//				System.out.println("pst===>"+pst);
				while(rs.next())
				{
					amount=rs.getDouble("eepf_contribution")+rs.getDouble("evpf_contribution");
				}
		        rs.close();
		        pst.close();
			
				
				pst = con.prepareStatement("insert into challan_details(emp_id,amount,entry_date," +
						"is_print,financial_year_from_date,financial_year_to_date,month,challan_type) values(?,?,?,?,?,?,?,?)");
				pst.setInt(1,uF.parseToInt(getEmpIds()[i]));
				pst.setDouble(2,amount); 
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setBoolean(4,true);
				pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(7,totalMonths);
				pst.setInt(8,EMPLOYEE_EPF);
				pst.executeUpdate();
		        pst.close();
				
			}
		}
			
		if(getEmpIds()!=null){
			for(int i=0;i<getEmpIds().length;i++)
			{
					double amount=0;
					pst = con.prepareStatement("select sum(erpf_contribution) as erpf_contribution," +
						"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(pf_admin_charges) as pf_admin_charges," +
						"sum(edli_admin_charges) as edli_admin_charges from emp_epf_details where financial_year_start=?" +
							" and financial_year_end=? and _month in ("+totalMonths+") and emp_id=?");
																	
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3,uF.parseToInt(getEmpIds()[i]));
					rs = pst.executeQuery();
	//				System.out.println("pst===>"+pst);
					while(rs.next()) 
					{
						amount=(rs.getDouble("erpf_contribution")+rs.getDouble("erps_contribution")+rs.getDouble("erdli_contribution")+rs.getDouble("pf_admin_charges")+rs.getDouble("edli_admin_charges"));
					}
			        rs.close();
			        pst.close();
					
				
					pst = con.prepareStatement("insert into challan_details(emp_id,amount,entry_date," +
							"is_print,financial_year_from_date,financial_year_to_date,month,challan_type) values(?,?,?,?,?,?,?,?)");
					pst.setInt(1,uF.parseToInt(getEmpIds()[i]));
					pst.setDouble(2, amount);
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setBoolean(4,true);
					pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setString(7,totalMonths);
					pst.setInt(8,EMPLOYER_EPF);
					pst.executeUpdate();
			        pst.close();
			
			
			}
//			viewForm5PTChallanPdfReports();
//			generateForm5PTChallanPdfReports();
		 }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	public void viewEPFChallanPdfReports(UtilityFunctions uF){
		
//		System.out.println("in viewEPFChallanPdfReports");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		String[] strPayCycleDates = null;
//		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		String strMonthYear = null;
		try{
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
		
		
		strMonthYear = uF.getDateFormat(strDateStart, DATE_FORMAT, "MM/yyyy");	
		

		request.setAttribute("strMonthYear", strMonthYear);
		
		
		
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from org_details where org_id = ? ");
			pst.setInt(1, uF.parseToInt(getF_org()));
//			System.out.println("pst====>"+pst);
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
				hmOrg.put("ORG_TRRN_EPF", rs.getString("trrn_epf"));
				hmOrg.put("ORG_EPF_ACCOUNT_NO", rs.getString("epf_account_no"));
			}
	        rs.close();
	        pst.close();  
			
			List<Integer> empidsList=new ArrayList<Integer>();
			
			Map<String,String> hmEPFChallan=new HashMap<String, String>();
			
			if(getChallanNum()!=null){
				
				/*pst = con.prepareStatement("select distinct(emp_id) as emp_id,month from challan_details " +
						"where challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and is_paid=? and " +
						"challan_no=? and emp_id in (select emp_id from employee_official_details where org_id=?)");*/
				
				StringBuilder sb=new StringBuilder();
				sb.append("select distinct(emp_id) as emp_id,month from challan_details " +
						"where challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and is_paid=? and " +
						"challan_no=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				pst=con.prepareStatement(sb.toString());
				
				pst.setBoolean(1, true);
				pst.setString(2, getChallanNum());
				//pst.setInt(3, uF.parseToInt(getF_org()));
				
//				System.out.println(" pst 1 for viewEPFChallanPdfReports" +pst);
				
			}else{
//				System.out.println("in else part");
				
				/*pst = con.prepareStatement("select distinct(emp_id) as emp_id,month from challan_details " +
						"where challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and is_paid=? and " +
						"entry_date=? and emp_id in (select emp_id from employee_official_details where org_id=?)");
				*/
				
				StringBuilder sb=new StringBuilder();
				sb.append(" select distinct(emp_id) as emp_id,month from challan_details " +
						"where challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") and is_paid=? and " +
						"entry_date=? ");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				pst=con.prepareStatement(sb.toString());
				
				pst.setBoolean(1, false);
				pst.setDate(2, uF.getDateFormat(getChallanDate(), DBDATE));
				//pst.setInt(3, uF.parseToInt(getF_org()));
				
//				System.out.println(" pst 2 for viewEPFChallanPdfReports" +pst);

			}
			
//			System.out.println(" pst 3 for viewEPFChallanPdfReports" +pst);

			rs = pst.executeQuery();
			StringBuilder sbEmpIds=new StringBuilder();
			
			while(rs.next()){
				totalNoOfEmp++;
				empidsList.add(rs.getInt("emp_id"));
				String[] arr = rs.getString("emp_id").split(",");
//				System.out.println("arr=="+arr);
				
				for(int i=0; arr!=null && i<arr.length; i++){
					if(arr[i]!=null && arr[i].length()>0){
						sbEmpIds.append(arr[i]+",");
					}
					
				}
				
			}
	        rs.close();
	        pst.close();
			hmEPFChallan.put("TOTAL_EMP", ""+totalNoOfEmp);
			
//			System.out.println("empidsList in epfUpdate for viewPDf=="+empidsList);
//			System.out.println();
//			System.out.println("sbEmpIds=="+sbEmpIds);
				
			sbEmpIds.replace(0, sbEmpIds.length(), sbEmpIds.substring(0, sbEmpIds.length()-1));
				
			
			/*pst = con.prepareStatement("select sum(epf_max_limit) as epf_max_limit,sum(eps_max_limit) as eps_max_limit," +
					"sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution,sum(erps_contribution) as erps_contribution," +
					"sum(erdli_contribution) as erdli_contribution,sum(edli_max_limit) as edli_max_limit," +
					"sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges,sum(evpf_contribution) as evpf_contribution from emp_epf_details " +
					"where financial_year_start=? and financial_year_end=? and _month=? " +
					"and emp_id in("+sbEmpIds.toString()+") ");
			*/
		
			StringBuilder sb=new StringBuilder();
			sb.append("select sum(epf_max_limit) as epf_max_limit,sum(eps_max_limit) as eps_max_limit," +
					"sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution,sum(erps_contribution) as erps_contribution," +
					"sum(erdli_contribution) as erdli_contribution,sum(edli_max_limit) as edli_max_limit," +
					"sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges,sum(evpf_contribution) as evpf_contribution from emp_epf_details " +
					"where financial_year_start=? and financial_year_end=? and _month=? ");
		
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmpIds.toString()+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,uF.parseToInt(getStrMonth()));
			rs = pst.executeQuery();
//			System.out.println("pst for viewEPFChallanPdfReports =====>"+pst);
			
			
			while(rs.next()){
				
				hmEPFChallan.put("EEPF_CONTRIBUTION", uF.showData(rs.getString("eepf_contribution"), ""));
				hmEPFChallan.put("ERPF_CONTRIBUTION", uF.showData(rs.getString("erpf_contribution"), ""));
				hmEPFChallan.put("PF_ADMIN_CHARGES", uF.showData(rs.getString("pf_admin_charges"), ""));
				hmEPFChallan.put("ERPS_CONTRIBUTION", uF.showData(rs.getString("erps_contribution"), ""));
				hmEPFChallan.put("ERDLI_CONTRIBUTION", uF.showData(rs.getString("erdli_contribution"), ""));
				hmEPFChallan.put("EDLI_ADMIN_CHARGES", uF.showData(rs.getString("edli_admin_charges"), ""));
				
				hmEPFChallan.put("EPF_MAX_LIMIT", uF.showData(rs.getString("epf_max_limit"), ""));
				hmEPFChallan.put("EPS_MAX_LIMIT", uF.showData(rs.getString("eps_max_limit"), ""));
				hmEPFChallan.put("EDLI_MAX_LIMIT", uF.showData(rs.getString("edli_max_limit"), ""));
				hmEPFChallan.put("EEVPF_CONTRIBUTION", uF.showData(rs.getString("evpf_contribution"), ""));
				
			}
	        rs.close();
	        pst.close();
			
//			System.out.println("hmEPFChallan=====>"+hmEPFChallan);
			request.setAttribute("hmEPFChallan", hmEPFChallan);
			request.setAttribute("hmOrg", hmOrg);
			
		}catch (Exception e) {
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

	

	public String getTotalMonths() {
		return totalMonths;
	}

	public void setTotalMonths(String totalMonths) {
		this.totalMonths = totalMonths;
	}

	public String getChallanType() {
		return challanType;
	}

	public void setChallanType(String challanType) {
		this.challanType = challanType;
	}

	public String getStrMonth() {
		return strMonth;
	}
	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}
	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

	public String[] getEmpIds() {
		return empIds;
	}

	public void setEmpIds(String[] empIds) {
		this.empIds = empIds;
	}

	public String getChallanDate() {
		return challanDate;
	}

	public void setChallanDate(String challanDate) {
		this.challanDate = challanDate;
	}

	public String getChallanNum() {
		return challanNum;
	}

	public void setChallanNum(String challanNum) {
		this.challanNum = challanNum;
	}

	public String getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(String paidDate) {
		this.paidDate = paidDate;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getCheque_no() {
		return cheque_no;
	}
	public void setCheque_no(String cheque_no) {
		this.cheque_no = cheque_no;
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
	public String getF_org() {
		return f_org;
	}
	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	
	public String getSbEmp() {
		return sbEmp;
	}

	public void setSbEmp(String sbEmp) {
		this.sbEmp = sbEmp;
	}
	
}