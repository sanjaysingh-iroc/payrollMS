package com.konnect.jpms.payroll;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployeeStatus;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PayrollRegisterNew extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	 
	CommonFunctions CF = null; 
	String profileEmpId;
	
	String strD1 = null;
	String strD2 = null;
	String strPC = null;
	
	String strApprove;
	String financialYear; 
	String paycycle;
	String approvePC;
	String strMonth;
	String []chbxApprove;
	List<FillMonth> alMonthList;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String strEmployeType;
	String strEmployeeStatus;
	
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_level; 
	String[] f_employeType;
	String[] f_employeeStatus;
	String[] f_service;
	
	List<FillPayCycles> paycycleList ;
	List<FillFinancialYears> financialYearList;
	List<FillLevel> levelList;
	List<FillWLocation> wLocationList;
	
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	
	List<FillOrganisation> organisationList;
	List<FillEmploymentType> employementTypeList;
	List<FillEmployeeStatus> employeeStatusList;
	String f_org;
	
	private static Logger log = Logger.getLogger(PayrollRegister.class);
	
	private String operation;
	String downloadreportname; 
	int rownum = 5;
	HSSFSheet firstSheet;	
	//Collection<File> files;
	HSSFWorkbook workbook; 
	//File exactFile;
	HSSFCellStyle cellStyleForHeader;
	HSSFCellStyle cellStyleForData;
	HSSFCellStyle cellStyleForReportName;
	
	{
		workbook = new HSSFWorkbook();
		firstSheet = workbook.createSheet("Reports");
		Row headerRow = firstSheet.createRow(rownum);
		headerRow.setHeightInPoints(40);
		HSSFPalette pallet = workbook.getCustomPalette();
		pallet.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index, (byte)230, (byte)225, (byte)225);
	}

	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PProllRegister);
		request.setAttribute(TITLE, TPayrollRegister);
		
		strEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(USERTYPE);
		
//			boolean isView  = CF.getAccess(session, request, uF);
//			if(!isView) {
//				request.setAttribute(PAGE, PAccessDenied);
//				request.setAttribute(TITLE, TAccessDenied);
//				return ACCESS_DENIED;
//			}
		request.setAttribute("roundOffCondition", ""+2); //uF.parseToInt(CF.getRoundOffCondtion())
		if(getF_org()==null || getF_org().trim().equals("")) {
			setF_org((String)session.getAttribute(ORGID));
		}

		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		if(getStrEmployeType() != null && !getStrEmployeType().equals("")) {
			setF_employeType(getStrEmployeType().split(","));
		} else {
			setF_employeType(null);
		}
		if(getStrEmployeeStatus() != null && !getStrEmployeeStatus().equals("")) {
			setF_employeeStatus(getStrEmployeeStatus().split(","));
		} else {
			setF_employeeStatus(null);
		}
		
		String[] strPayCycleDates = null;
		
		if(getApprovePC()!=null && !getApprovePC().equalsIgnoreCase("NULL") && getApprovePC().length()>0 && getStrApprove()!=null) {
			strPayCycleDates = getApprovePC().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		} else if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
//				strPayCycleDates = CF.getPrevPayCycle(strPayCycleDates[1], CF.getStrTimeZone(), CF);
		} else {
			strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}
		strD1 = strPayCycleDates[0];
		strD2 = strPayCycleDates[1];
		strPC = strPayCycleDates[2];
		
		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
	
		if(nSalaryStrucuterType == S_GRADE_WISE) {
			viewApporvedPayrollByGrade(uF);
		} else {
			viewApporvedPayroll(uF);
		}
//		System.out.println("getOperation() ===>> " + getOperation());
		if(getOperation() != null && getOperation().equalsIgnoreCase("download")) {
			generateExcel(workbook, uF);
			return null;
		} else {
			return loadPaySlips();
		}
		
	}
	
	private void generateExcel(HSSFWorkbook workbook2, UtilityFunctions uF) {

		FileOutputStream fileOut = null;
		
		generateSheetReport();

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			workbook.write(buffer);
			buffer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		response.setHeader("Content-Disposition", "attachment; filename=\"Payroll_Register.xls\"");
		response.setContentType("application/vnd.ms-excel:UTF-8");
		response.setContentLength(buffer.size());
		try {
			ServletOutputStream op = response.getOutputStream();
			op = response.getOutputStream();
			op.write(buffer.toByteArray());
			op.flush();
			op.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void generateSheetReport() {
		
		UtilityFunctions uF = new UtilityFunctions();
		String strOrgName = (String) request.getAttribute("strOrgName");
		String strOrgCurrId = (String) request.getAttribute("strOrgCurrId");
		String strSelectedLocation = (String) request.getAttribute("strSelectedLocation");
		Map<String, Map<String, String>> hmCurrencyDetailsPDF = (Map<String, Map<String, String>>) request.getAttribute("hmCurrencyDetailsPDF");
		
		Map hmEmpMap = (Map) request.getAttribute("hmEmpMap");
		Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
		Map<String, String> hmPresentDays = (Map<String, String>) request.getAttribute("hmPresentDays");
		if(hmPresentDays == null) hmPresentDays = new HashMap<String, String>();
		
		Map<String, Map<String, String>> hmEmpHistory = (Map<String, Map<String, String>>) request.getAttribute("hmEmpHistory");
		if(hmEmpHistory == null) hmEmpHistory = new HashMap<String, Map<String, String>>();
		
		Map<String, String> hmWLocation = (Map<String, String>) request.getAttribute("hmWLocation");
		if(hmWLocation == null) hmWLocation = new HashMap<String, String>();
		
		Map<String, String> hmEmpWlocationMap = (Map<String, String>) request.getAttribute("hmEmpWlocationMap");
		if(hmEmpWlocationMap == null) hmEmpWlocationMap = new HashMap<String, String>();
		
		Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
		if(hmEmpCodeDesig==null) hmEmpCodeDesig = new HashMap<String, String>();
		
		Map<String, String> hmCodeDesig = (Map<String, String>) request.getAttribute("hmCodeDesig");
		if(hmCodeDesig == null) hmCodeDesig = new HashMap<String, String>();
		
		Map<String, String> hmEmpGradeMap = (Map<String, String>) request.getAttribute("hmEmpGradeMap");
		if(hmEmpGradeMap == null) hmEmpGradeMap = new HashMap<String, String>();
		
		Map<String, String> hmGradeMap = (Map<String, String>) request.getAttribute("hmGradeMap");
		if(hmGradeMap == null) hmGradeMap = new HashMap<String, String>();
		
		Map<String, String> hmGradeDesig = (Map<String, String>) request.getAttribute("hmGradeDesig");
		if(hmGradeDesig == null) hmGradeDesig = new HashMap<String, String>();
		
		Map hmPayPayroll = (Map) request.getAttribute("hmPayPayroll");
		Map hmSalaryDetails = (Map) request.getAttribute("hmSalaryDetails");
		Map hmIsApprovedSalary = (Map) request.getAttribute("hmIsApprovedSalary");
		if (hmIsApprovedSalary == null) hmIsApprovedSalary = new HashMap();
		
		List alEarnings = (List) request.getAttribute("alEarnings");
		if(alEarnings == null) alEarnings = new ArrayList();
		
		List alDeductions = (List) request.getAttribute("alDeductions");
		if(alDeductions == null) alDeductions = new ArrayList();
		
		List<String> alESalaryHeads = (List<String>) request.getAttribute("alESalaryHeads");
		if(alESalaryHeads == null) alESalaryHeads = new ArrayList<String>();
		
		List<String> alDSalaryHeads = (List<String>) request.getAttribute("alDSalaryHeads");
		if(alDSalaryHeads == null) alDSalaryHeads = new ArrayList<String>();
		
		try {				
//			Row reportOrgName = firstSheet.createRow(0);
			
			HSSFCellStyle bottomBorderStyle = workbook.createCellStyle();
			bottomBorderStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
            
			HSSFCellStyle styleForFirmName = workbook.createCellStyle();
			Font firmNameFont = workbook.createFont();
			firmNameFont.setFontHeight((short) 220);
			firmNameFont.setBoldweight((short) 600);
			styleForFirmName.setFont(firmNameFont);

			Row orgNameRow = firstSheet.createRow(0);
			orgNameRow.setHeight((short) 300);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("B1:K1"));
			
			Cell orgNameCell = orgNameRow.createCell(1);
			orgNameCell.setCellValue("Organization Name: "+uF.showData(strOrgName, "-"));
			styleForFirmName.setAlignment(styleForFirmName.ALIGN_LEFT);
			orgNameCell.setCellStyle(styleForFirmName);
			
			
			Row reportNameRow = firstSheet.createRow(1);
			reportNameRow.setHeight((short) 300);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("A2:C2"));
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("D2:K2"));
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("L2:R2"));
			
			Cell reportNameCell = reportNameRow.createCell(0);
			reportNameCell.setCellValue("Date: "+uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
			styleForFirmName.setAlignment(styleForFirmName.ALIGN_LEFT);
			reportNameCell.setCellStyle(styleForFirmName);
			
			reportNameCell = reportNameRow.createCell(3);
			reportNameCell.setCellValue("Month: "+uF.getDateFormat(strD2, DATE_FORMAT, "MMMM")+" - "+uF.getDateFormat(strD2, DATE_FORMAT, "yyyy"));
			styleForFirmName.setAlignment(styleForFirmName.ALIGN_CENTER);
			reportNameCell.setCellStyle(styleForFirmName);
			
			reportNameCell = reportNameRow.createCell(11);
			reportNameCell.setCellValue(" Branch: "+strSelectedLocation);
//			styleForFirmName.setAlignment(styleForFirmName.ALIGN_CENTER);
			styleForFirmName.setAlignment(styleForFirmName.getBorderBottom());
//			reportNameCell.setCellStyle(styleForFirmName);
			
//			Earning Heads
			Row headerRow = firstSheet.createRow(3);
			headerRow.setHeight((short) 500);
			
			Cell headerCell = headerRow.createCell(0);
			headerCell.setCellValue("Sr. No.");
			
			headerCell = headerRow.createCell(1);
			headerCell.setCellValue("Emp Code");
			
			headerCell = headerRow.createCell(2);
			headerCell.setCellValue("Employee Name \r\n Designation");
			
			headerCell = headerRow.createCell(3);
			headerCell.setCellValue("E:->");
			
			/*headerCell = headerRow.createCell(4);
			headerCell.setCellValue("Employee Status");*/
//			int intHeadCellCnt = 5;
			
			int salHeadCnt = alESalaryHeads.size();
			int maxSalHeadCnt=11;
			if(alESalaryHeads.size()>maxSalHeadCnt) {
				salHeadCnt = maxSalHeadCnt;
			}
			int intHeadCellCnt=4;
			for (int ii = 0; ii < salHeadCnt; ii++) {
				if(alEarnings.contains(alESalaryHeads.get(ii))) {
					String strEarning = (String)hmSalaryDetails.get(alESalaryHeads.get(ii))+"\r\n(+)";
					headerCell = headerRow.createCell(intHeadCellCnt);
					headerCell.setCellValue(strEarning);
					if(ii==2) {
						intHeadCellCnt++;
						headerCell = headerRow.createCell(intHeadCellCnt);
						headerCell.setCellValue("Total");
					}
					intHeadCellCnt++;
				}
			}
			if((alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) {
				for(int i=0; i<(alDeductions.size()- (alEarnings.contains(""+VDA) ? (salHeadCnt+1) : salHeadCnt)); i++) {
//			if((alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) {
//				for(int i=0; i<(alDeductions.size()-(alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size())); i++) {
					headerCell = headerRow.createCell(intHeadCellCnt);
					headerCell.setCellValue("");
					intHeadCellCnt++;
				}
			}
			
			boolean flagEHSR = false;
			if(alESalaryHeads.size()>maxSalHeadCnt) {
				headerCell = headerRow.createCell(intHeadCellCnt);
				headerCell.setCellValue("");
				
				intHeadCellCnt++;
				headerCell = headerRow.createCell(intHeadCellCnt);
				headerCell.setCellValue("");
				flagEHSR = true;
			} else {
				headerCell = headerRow.createCell(intHeadCellCnt);
				headerCell.setCellValue("Total");
				
				intHeadCellCnt++;
				headerCell = headerRow.createCell(intHeadCellCnt);
				headerCell.setCellValue("Net");
			}
			
			
			if(flagEHSR) {
//			Earning Heads Second Row
				headerRow = firstSheet.createRow(4);
				headerRow.setHeight((short) 500);
				
				headerCell = headerRow.createCell(0);
				headerCell.setCellValue("");
				
				headerCell = headerRow.createCell(1);
				headerCell.setCellValue("");
				
				headerCell = headerRow.createCell(2);
				headerCell.setCellValue("");
				
				headerCell = headerRow.createCell(3);
				headerCell.setCellValue("E:->");
				
				/*headerCell = headerRow.createCell(4);
				headerCell.setCellValue("");*/
	//			int intHeadCellCnt = 5;
				
				intHeadCellCnt=4;
				for (int ii = maxSalHeadCnt; ii < alESalaryHeads.size(); ii++) {
					if(alEarnings.contains(alESalaryHeads.get(ii))) {
						String strEarning = (String)hmSalaryDetails.get(alESalaryHeads.get(ii))+"\r\n(+)";
						headerCell = headerRow.createCell(intHeadCellCnt);
						headerCell.setCellValue(strEarning);
						if(ii==2) {
							intHeadCellCnt++;
							headerCell = headerRow.createCell(intHeadCellCnt);
							headerCell.setCellValue("Total");
						}
						intHeadCellCnt++;
					}
				}
				if((alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) {
					for(int i=0; i<(alDeductions.size()-(alEarnings.contains(""+VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt))); i++) {
//				if((alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) {
//					for(int i=0; i<(alDeductions.size()-(alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size())); i++) {
						headerCell = headerRow.createCell(intHeadCellCnt);
						headerCell.setCellValue("");
						intHeadCellCnt++;
					}
				} else if((alEarnings.contains(""+VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt)) < maxSalHeadCnt) {
					for(int i=0; i<(maxSalHeadCnt-(alEarnings.contains(""+VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt))); i++) {
						headerCell = headerRow.createCell(intHeadCellCnt);
						headerCell.setCellValue("");
						intHeadCellCnt++;
					} 
				}
				
				headerCell = headerRow.createCell(intHeadCellCnt);
				headerCell.setCellValue("Total");
				
				intHeadCellCnt++;
				headerCell = headerRow.createCell(intHeadCellCnt);
				headerCell.setCellValue("Net");
			}
			
			
//			Deduction Heads
			int deductRowNo=4;
			if(flagEHSR) {
				deductRowNo=5;
			}
			Row headerRow2 = firstSheet.createRow(deductRowNo);
			headerRow2.setHeight((short) 500);
			
			headerCell = headerRow2.createCell(0);
			headerCell.setCellValue("");
			headerCell.setCellStyle(bottomBorderStyle);
			
			headerCell = headerRow2.createCell(1);
			headerCell.setCellValue("");
			headerCell.setCellStyle(bottomBorderStyle);
			
			headerCell = headerRow2.createCell(2);
			headerCell.setCellValue("");
			headerCell.setCellStyle(bottomBorderStyle);
			
			headerCell = headerRow2.createCell(3);
			headerCell.setCellValue("D:->");
			headerCell.setCellStyle(bottomBorderStyle);
			
			/*headerCell = headerRow2.createCell(4);
			headerCell.setCellValue("");*/
			intHeadCellCnt = 4;
			for (int ii = 0; ii < alDSalaryHeads.size(); ii++) {
				if(alDeductions.contains(alDSalaryHeads.get(ii))) {
					String strDeduction = (String)hmSalaryDetails.get(alDSalaryHeads.get(ii))+"\r\n(-)";
					headerCell = headerRow2.createCell(intHeadCellCnt);
					headerCell.setCellValue(strDeduction);
					headerCell.setCellStyle(bottomBorderStyle);
					intHeadCellCnt++;
				}
			}
//			System.out.println("alEarnings.size() ===>> " + alEarnings.size());
//			System.out.println("alDeductions.size() ===>> " + alDeductions.size());
			if(alEarnings.size()>0 && (alEarnings.contains(""+VDA) ? (salHeadCnt+1) : salHeadCnt) > alDeductions.size()) {
				for(int i=0; i<((alEarnings.contains(""+VDA) ? (salHeadCnt+1) : salHeadCnt)-alDeductions.size()); i++) {
//			if(alEarnings.size() > 0 && (alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size()) > alDeductions.size()) {
//				for(int i=0; i<((alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size())-alDeductions.size()); i++) {
//					System.out.println("in loop intHeadCellCnt --->> " + intHeadCellCnt);
					headerCell = headerRow2.createCell(intHeadCellCnt);
					headerCell.setCellValue("");
					headerCell.setCellStyle(bottomBorderStyle);
					intHeadCellCnt++;
				}
			}
			headerCell = headerRow2.createCell(intHeadCellCnt);
			headerCell.setCellValue("Total");
			headerCell.setCellStyle(bottomBorderStyle);
			
			intHeadCellCnt++;
			headerCell = headerRow2.createCell(intHeadCellCnt);
			headerCell.setCellValue("");
			headerCell.setCellStyle(bottomBorderStyle);
			
			int salHeadSize = 17;
//			if(alEarnings != null && alDeductions != null) {
//				if(alEarnings.size()<alDeductions.size()) {
//					salHeadSize += alDeductions.size();
//				} else {
//					salHeadSize += alEarnings.size();
//					if(alEarnings.size()>0) {
//						salHeadSize += 1;
//					}
//				}
//			}
			
			
//			Payroll Data Start 
			Set set = hmPayPayroll.keySet();
			Iterator it = set.iterator();
			double totGross = 0.0d;
			double totNet = 0.0d;
			Map<String, String> hmTotAmtSalHead = new HashMap<String, String>();
			List<String> alEmpId = new ArrayList<String>();
			int cnt = 0;
			int intRowCnt=4;
			if(flagEHSR) {
				intRowCnt=5;
			}
			double dblBasicFDAVDATotOfTotal = 0.0d;
			double dblGrossAllTotal = 0.0d;
			double dblNetAllTotal = 0.0d;
			double dblDeductionAllTotal = 0.0d;
			while(it.hasNext()) {
				String strEmpIdWithSalEffectiveDate = (String)it.next();
				String[] strTmp = strEmpIdWithSalEffectiveDate.split("_");
				String strEmpId = strTmp[0];
				cnt++;
				Map hmPayroll = (Map)hmPayPayroll.get(strEmpIdWithSalEffectiveDate);
				if(hmPayroll==null)hmPayroll=new HashMap();
				
				Map<String, String> hm = hmEmpHistory.get(strEmpId);
				
				String strWLocation = uF.showData((String) hmWLocation.get((String)hmEmpWlocationMap.get(strEmpId)),"");
				if(hm != null && uF.parseToInt(hm.get("EMP_WLOCATION")) > 0) {
					strWLocation = uF.showData(hmWLocation.get(hm.get("EMP_WLOCATION")), "");
				}
				
				String strDesig = uF.showData((String) hmCodeDesig.get((String)hmEmpCodeDesig.get(strEmpId)),"");
				String strGrade = uF.showData((String) hmGradeMap.get((String)hmEmpGradeMap.get(strEmpId)),"");
				if(hm != null && uF.parseToInt(hm.get("EMP_GRADE")) > 0) {
					strDesig = uF.showData(hmCodeDesig.get(hmGradeDesig.get(hm.get("EMP_GRADE"))), "");
					strGrade = uF.showData(hmGradeMap.get(hm.get("EMP_GRADE")), "");
				}
				
//				Earning Head Data
				intRowCnt++;
				Row dataRow = firstSheet.createRow(intRowCnt);
				dataRow.setHeight((short) 500);
				
				Cell dataCell = dataRow.createCell(0);
				dataCell.setCellValue(""+cnt);
				
				dataCell = dataRow.createCell(1);
				dataCell.setCellValue((String)hmEmpCode.get(strEmpId));
				
				dataCell = dataRow.createCell(2);
				dataCell.setCellValue((String)hmEmpMap.get(strEmpId)+"\r\n"+strDesig+" ("+strGrade+")");
				
				dataCell = dataRow.createCell(3);
				dataCell.setCellValue("E:->");
//				dataCell.setCellValue(strWLocation);
				
//				dataCell = dataRow.createCell(4);
//				dataCell.setCellValue((String)hmPayroll.get("EMP_STATUS"));
				intHeadCellCnt = 4;
				
				double dblBasicFDAVDATot = 0.0d;
				for (int i = 0; i < salHeadCnt; i++) {
					if(alEarnings.contains(alESalaryHeads.get(i))) {
						double salHeadAmt = uF.parseToDouble((String)hmPayroll.get(alESalaryHeads.get(i)));
						double salHeadTotAmt = uF.parseToDouble(hmTotAmtSalHead.get(alESalaryHeads.get(i)));
						salHeadTotAmt += salHeadAmt;
						hmTotAmtSalHead.put(alESalaryHeads.get(i), ""+salHeadTotAmt);
						if(i<=2) {
							dblBasicFDAVDATot += salHeadAmt;
						}
						dataCell = dataRow.createCell(intHeadCellCnt);
						dataCell.setCellValue(uF.showData((String)hmPayroll.get(alESalaryHeads.get(i)), "0.00"));
						if(i==2) {
							intHeadCellCnt++;
							dataCell = dataRow.createCell(intHeadCellCnt);
							dataCell.setCellValue(uF.showData(uF.formatIntoTwoDecimalWithOutComma(dblBasicFDAVDATot), "0.00"));
						}
						intHeadCellCnt++;
					}
				}
				dblBasicFDAVDATotOfTotal += dblBasicFDAVDATot;
				
//				if(alEarnings.size() < alDeductions.size()) { 
//					for(int i=0; i<(alDeductions.size()-alEarnings.size()); i++) {
				if((alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) {
					for(int i=0; i<(alDeductions.size()- (alEarnings.contains(""+VDA) ? (salHeadCnt+1) : salHeadCnt)); i++) {
						dataCell = dataRow.createCell(intHeadCellCnt);
						dataCell.setCellValue("");
						intHeadCellCnt++;
					} 
				}
				
				if(flagEHSR) {
					dataCell = dataRow.createCell(intHeadCellCnt);
					dataCell.setCellValue("");
					intHeadCellCnt++;
					
					dataCell = dataRow.createCell(intHeadCellCnt);
					dataCell.setCellValue("");
				} else {
					dataCell = dataRow.createCell(intHeadCellCnt);
					dataCell.setCellValue(uF.showData((String)hmPayroll.get("GROSS"), "0.00"));
					intHeadCellCnt++;
					dblGrossAllTotal += uF.parseToDouble((String)hmPayroll.get("GROSS"));
					
					dataCell = dataRow.createCell(intHeadCellCnt);
					dataCell.setCellValue(uF.showData((String)hmPayroll.get("NET"), "0.00"));
					dblNetAllTotal += uF.parseToDouble((String)hmPayroll.get("NET"));
				}
				
				
				if(flagEHSR) {
//					Earning Head Data Second Row
					intRowCnt++;
					dataRow = firstSheet.createRow(intRowCnt);
					dataRow.setHeight((short) 500);
					
					dataCell = dataRow.createCell(0);
					dataCell.setCellValue("");
					
					dataCell = dataRow.createCell(1);
					dataCell.setCellValue("");
					
					dataCell = dataRow.createCell(2);
					dataCell.setCellValue("");
					
					dataCell = dataRow.createCell(3);
					dataCell.setCellValue("E:->");
					
					/*dataCell = dataRow.createCell(4);
					dataCell.setCellValue("");*/
					intHeadCellCnt = 4;
					
					for (int i = maxSalHeadCnt; i < alESalaryHeads.size(); i++) {
						if(alEarnings.contains(alESalaryHeads.get(i))) {
							double salHeadAmt = uF.parseToDouble((String)hmPayroll.get(alESalaryHeads.get(i)));
							double salHeadTotAmt = uF.parseToDouble(hmTotAmtSalHead.get(alESalaryHeads.get(i)));
							salHeadTotAmt += salHeadAmt;
							hmTotAmtSalHead.put(alESalaryHeads.get(i), ""+salHeadTotAmt);
							dataCell = dataRow.createCell(intHeadCellCnt);
							dataCell.setCellValue(uF.showData((String)hmPayroll.get(alESalaryHeads.get(i)), "0.00"));
							intHeadCellCnt++;
						}
					}
					
//					if(alEarnings.size() < alDeductions.size()) { 
//						for(int i=0; i<(alDeductions.size()-alEarnings.size()); i++) {
					if((alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) {
						for(int i=0; i<(alDeductions.size()-(alEarnings.contains(""+VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt))); i++) {
							dataCell = dataRow.createCell(intHeadCellCnt);
							dataCell.setCellValue("");
							intHeadCellCnt++;
						} 
					} else if((alEarnings.contains(""+VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt)) < maxSalHeadCnt) {
						for(int i=0; i<(maxSalHeadCnt-(alEarnings.contains(""+VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt))); i++) {
							dataCell = dataRow.createCell(intHeadCellCnt);
							dataCell.setCellValue("");
							intHeadCellCnt++;
						}
					}
					dataCell = dataRow.createCell(intHeadCellCnt);
					dataCell.setCellValue(uF.showData((String)hmPayroll.get("GROSS"), "0.00"));
					intHeadCellCnt++;
					dblGrossAllTotal += uF.parseToDouble((String)hmPayroll.get("GROSS"));
					
					dataCell = dataRow.createCell(intHeadCellCnt);
					dataCell.setCellValue(uF.showData((String)hmPayroll.get("NET"), "0.00"));
					dblNetAllTotal += uF.parseToDouble((String)hmPayroll.get("NET"));
				}
				
				
				
//				Deduction Head Data
				intRowCnt++;
				Row dataRow2 = firstSheet.createRow(intRowCnt);
				dataRow2.setHeight((short) 500);
				
				dataCell = dataRow2.createCell(0);
				dataCell.setCellValue("");
				
				dataCell = dataRow2.createCell(1);
				dataCell.setCellValue("");
				
				dataCell = dataRow2.createCell(2);
				dataCell.setCellValue("");
				
				dataCell = dataRow2.createCell(3);
				dataCell.setCellValue("D:->");
				
				/*dataCell = dataRow2.createCell(4);
				dataCell.setCellValue("");*/
				intHeadCellCnt = 4;
				
				double dblDeductionTot = 0.0d;
				for (int i = 0; i < alDSalaryHeads.size(); i++) {
					if(alDeductions.contains(alDSalaryHeads.get(i))) {
		    			double salHeadAmt = uF.parseToDouble((String)hmPayroll.get(alDSalaryHeads.get(i)));
						double salHeadTotAmt = uF.parseToDouble(hmTotAmtSalHead.get(alDSalaryHeads.get(i)));
						salHeadTotAmt += salHeadAmt;
						hmTotAmtSalHead.put(alDSalaryHeads.get(i), ""+salHeadTotAmt);
						dblDeductionTot += salHeadAmt;
						
						dataCell = dataRow2.createCell(intHeadCellCnt);
						dataCell.setCellValue(uF.showData((String)hmPayroll.get(alDSalaryHeads.get(i)), "0.00"));
						intHeadCellCnt++;
					}
				}
//				if((alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size()) > alDeductions.size()) {
//					for(int i=0; i<((alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size())-alDeductions.size()); i++) {
				if(alEarnings.size()>0 && (alEarnings.contains(""+VDA) ? (salHeadCnt+1) : salHeadCnt) > alDeductions.size()) {
					for(int i=0; i<((alEarnings.contains(""+VDA) ? (salHeadCnt+1) : salHeadCnt)-alDeductions.size()); i++) {
						dataCell = dataRow2.createCell(intHeadCellCnt);
						dataCell.setCellValue("");
						intHeadCellCnt++;
					}
				}
				dataCell = dataRow2.createCell(intHeadCellCnt);
				dataCell.setCellValue(uF.showData(uF.formatIntoTwoDecimalWithOutComma(dblDeductionTot), "0.00"));
				intHeadCellCnt++;
				dblDeductionAllTotal += dblDeductionTot;
				
				dataCell = dataRow2.createCell(intHeadCellCnt);
				dataCell.setCellValue("");
				
//				Blank Row
				intRowCnt++;
				intHeadCellCnt = 0;
				Row blankRow = firstSheet.createRow(intRowCnt);
				for(int i=0; i<salHeadSize; i++) {
					dataCell = blankRow.createCell(intHeadCellCnt);
					dataCell.setCellValue("");
					dataCell.setCellStyle(bottomBorderStyle);
					intHeadCellCnt++;
				}
				dataCell = blankRow.createCell(intHeadCellCnt);
				dataCell.setCellValue("");
				dataCell.setCellStyle(bottomBorderStyle);
				
			}
			
			
//			Earning Head Total Data
			intRowCnt++;
			Row footerRow = firstSheet.createRow(intRowCnt);
			footerRow.setHeight((short) 500);
			
			Cell footerCell = footerRow.createCell(0);
			footerCell.setCellValue("");
			
			footerCell = footerRow.createCell(1);
			footerCell.setCellValue("");
			
			footerCell = footerRow.createCell(2);
			footerCell.setCellValue("Total:");
			
			footerCell = footerRow.createCell(3);
			footerCell.setCellValue("E:->");
			
//			footerCell = footerRow.createCell(4);
//			footerCell.setCellValue("");
			intHeadCellCnt = 4;
			for (int i = 0; i < salHeadCnt; i++) {
				if(alEarnings.contains(alESalaryHeads.get(i))) { 
					footerCell = footerRow.createCell(intHeadCellCnt);
					footerCell.setCellValue(uF.showData(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmTotAmtSalHead.get(alESalaryHeads.get(i)))), "0.00"));
					if(alEarnings.contains(""+VDA) && i==2) {
						intHeadCellCnt++;
						footerCell = footerRow.createCell(intHeadCellCnt);
						footerCell.setCellValue(uF.showData(uF.formatIntoTwoDecimalWithOutComma(dblBasicFDAVDATotOfTotal), "0.00"));
					}
					intHeadCellCnt++;
				}
			}
//			if((alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) { 
//				for(int i=0; i<(alDeductions.size()-(alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size())); i++) {
			if((alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) {
				for(int i=0; i<(alDeductions.size()- (alEarnings.contains(""+VDA) ? (salHeadCnt+1) : salHeadCnt)); i++) {
					footerCell = footerRow.createCell(intHeadCellCnt);
					footerCell.setCellValue("");
					intHeadCellCnt++;
				}
			}
			if(flagEHSR) {
				footerCell = footerRow.createCell(intHeadCellCnt);
				footerCell.setCellValue("");
				intHeadCellCnt++;
				
				footerCell = footerRow.createCell(intHeadCellCnt);
				footerCell.setCellValue("");
			} else {
				footerCell = footerRow.createCell(intHeadCellCnt);
				footerCell.setCellValue(uF.formatIntoTwoDecimalWithOutComma(dblGrossAllTotal));
				intHeadCellCnt++;
				
				footerCell = footerRow.createCell(intHeadCellCnt);
				footerCell.setCellValue(uF.formatIntoTwoDecimalWithOutComma(dblNetAllTotal));
			}
			
			
			if(flagEHSR) {
//				Earning Head Total Data Second Row
				intRowCnt++;
				footerRow = firstSheet.createRow(intRowCnt);
				footerRow.setHeight((short) 500);
				
				footerCell = footerRow.createCell(0);
				footerCell.setCellValue("");
				
				footerCell = footerRow.createCell(1);
				footerCell.setCellValue("");
				
				footerCell = footerRow.createCell(2);
				footerCell.setCellValue("Total:");
				
				footerCell = footerRow.createCell(3);
				footerCell.setCellValue("E:->");
				
//				footerCell = footerRow.createCell(4);
//				footerCell.setCellValue("");
				intHeadCellCnt = 4;
				for (int i = maxSalHeadCnt; i < alESalaryHeads.size(); i++) {
					if(alEarnings.contains(alESalaryHeads.get(i))) { 
						footerCell = footerRow.createCell(intHeadCellCnt);
						footerCell.setCellValue(uF.showData(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmTotAmtSalHead.get(alESalaryHeads.get(i)))), "0.00"));
						intHeadCellCnt++;
					}
				}
//				if((alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) { 
//					for(int i=0; i<(alDeductions.size()-(alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size())); i++) {
				if((alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) {
					for(int i=0; i<(alDeductions.size()-(alEarnings.contains(""+VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt))); i++) {
						footerCell = footerRow.createCell(intHeadCellCnt);
						footerCell.setCellValue("");
						intHeadCellCnt++;
					}
				} else if((alEarnings.contains(""+VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt)) < maxSalHeadCnt) {
					for(int i=0; i<(maxSalHeadCnt-(alEarnings.contains(""+VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt))); i++) {
						footerCell = footerRow.createCell(intHeadCellCnt);
						footerCell.setCellValue("");
						intHeadCellCnt++;
					}
				}
				footerCell = footerRow.createCell(intHeadCellCnt);
				footerCell.setCellValue(uF.formatIntoTwoDecimalWithOutComma(dblGrossAllTotal));
				intHeadCellCnt++;
				
				footerCell = footerRow.createCell(intHeadCellCnt);
				footerCell.setCellValue(uF.formatIntoTwoDecimalWithOutComma(dblNetAllTotal));
			}
			
			
//			Deduction Head Total Data
			intRowCnt++;
			Row footerRow2 = firstSheet.createRow(intRowCnt);
			footerRow2.setHeight((short) 500);
			
			footerCell = footerRow2.createCell(0);
			footerCell.setCellValue("");
			
			footerCell = footerRow2.createCell(1);
			footerCell.setCellValue("");
			
			footerCell = footerRow2.createCell(2);
			footerCell.setCellValue("Total:");
			
			footerCell = footerRow2.createCell(3);
			footerCell.setCellValue("D:->");
			
//			footerCell = footerRow2.createCell(4);
//			footerCell.setCellValue("");
			intHeadCellCnt = 4;
			
			for (int i = 0; i < alDSalaryHeads.size(); i++) {
				if(alDeductions.contains(alDSalaryHeads.get(i))) {
					footerCell = footerRow2.createCell(intHeadCellCnt);
					footerCell.setCellValue(uF.showData(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmTotAmtSalHead.get(alDSalaryHeads.get(i)))), "0.00"));
					intHeadCellCnt++;
				}
			}
//			if(alEarnings.size() >0 && (alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size()) > alDeductions.size()) { 
//				for(int i=0; i<((alEarnings.contains(""+VDA) ? (alEarnings.size()+1) : alEarnings.size())-alDeductions.size()); i++) {
			if(alEarnings.size()>0 && (alEarnings.contains(""+VDA) ? (salHeadCnt+1) : salHeadCnt) > alDeductions.size()) {
				for(int i=0; i<((alEarnings.contains(""+VDA) ? (salHeadCnt+1) : salHeadCnt)-alDeductions.size()); i++) {
					footerCell = footerRow2.createCell(intHeadCellCnt);
					footerCell.setCellValue("");
					intHeadCellCnt++;
				}
			}
			footerCell = footerRow2.createCell(intHeadCellCnt);
			footerCell.setCellValue(uF.formatIntoTwoDecimalWithOutComma(dblDeductionAllTotal));
			intHeadCellCnt++;
			
			footerCell = footerRow2.createCell(intHeadCellCnt);
			footerCell.setCellValue("");
			
			
			
//			Net Total in word
			Map<String, String> hmCurr = hmCurrencyDetailsPDF.get(strOrgCurrId);
			if (hmCurr == null) hmCurr = new HashMap<String, String>();
			double totalAmt = dblNetAllTotal;
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
	        		} else {
	        			pamt=uF.parseToInt(temp[1]);
	        		}
	        		digitTotal+=" and "+uF.digitsToWords(pamt)+" "+uF.showData(hmCurr.get("CURR_SUB_DIVISION"), "");
	        	}
	        } else {
	        	int totalAmt1=(int)totalAmt;
	        	digitTotal=uF.digitsToWords(totalAmt1);
	        }
			intRowCnt++;
			footerRow2 = firstSheet.createRow(intRowCnt);
			footerRow2.setHeight((short) 500);
			
			footerCell = footerRow2.createCell(1);
			footerCell.setCellValue(" Net amount in word: "+digitTotal);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("B"+(intRowCnt+1)+":P"+(intRowCnt+1)));
//			footerCell.setCellStyle();
			
			
//			Net Total in word
			intRowCnt++;
			intRowCnt = intRowCnt+4;
			footerRow2 = firstSheet.createRow(intRowCnt);
			footerRow2.setHeight((short) 500);
			
			footerCell = footerRow2.createCell(1);
			footerCell.setCellValue(" Jr. Officer");
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("B"+(intRowCnt+1)+":C"+(intRowCnt+1)));
			
			footerCell = footerRow2.createCell(3);
			footerCell.setCellValue(" Sr. Officer");
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("D"+(intRowCnt+1)+":E"+(intRowCnt+1)));
			
			footerCell = footerRow2.createCell(5);
			footerCell.setCellValue(" Assi. Gen. Manager");
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("F"+(intRowCnt+1)+":H"+(intRowCnt+1)));
			
			footerCell = footerRow2.createCell(8);
			footerCell.setCellValue(" Dy. Gen. Manager");
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("I"+(intRowCnt+1)+":K"+(intRowCnt+1)));
			
			footerCell = footerRow2.createCell(11);
			footerCell.setCellValue(" Incharge Chief Exe. Officer");
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("L"+(intRowCnt+1)+":O"+(intRowCnt+1)));
			
//			footerCell = footerRow2.createCell(1);
//			footerCell.setCellValue("");
			
			
			
			// This portion is used to print the header values.
//			for(int i=3,y=1;i<header.size();i++,y++) {
//				Cell headerCell = headerRow.createCell(y);
//				ds = (DataStyle)header.get(i);
//				headerCell.setCellValue("  "+ds.getStrData()+"  ");
//				cellStyleForHeader = workbook.createCellStyle();
//				cellStyleForHeader.setBorderBottom(ds.getBorderStyle());
//				cellStyleForHeader.setBorderLeft(ds.getBorderStyle());
//				cellStyleForHeader.setBorderRight(ds.getBorderStyle());
//				cellStyleForHeader.setBorderTop(ds.getBorderStyle());
//				cellStyleForHeader.setAlignment(ds.getCellDataAlign());
//				cellStyleForHeader.setFillForegroundColor(ds.getHSSFbackRoundColor());
//				cellStyleForHeader.setFillPattern(ds.getFillPattern());
//				cellStyleForHeader.setFont(font);
//				firstSheet.autoSizeColumn((short)y);
//				headerCell.setCellStyle(cellStyleForHeader);
//			}			
		
			
			// This portion is used to print the data in cells of table.
//			for (int j = 1; j < nReportSize; j++) {
//				Row row = firstSheet.createRow(rownum);
//				List userData = reportData.get(j);
//				if(userData == null) userData = new ArrayList();
//				
//				int nUserDataSize = userData.size();
//				for (int k = 0, l=1; k < nUserDataSize; k++,l++) {
//					Cell cell = row.createCell(l);
//					ds = (DataStyle)userData.get(k);
//		//			System.out.println("data = " + "Row num = " + rownum + "  "+ ds.getCellDataAlign());
//					cell.setCellValue(" "+ds.getStrData()+" ");	
//					cellStyleForData = workbook.createCellStyle();
//					//cellStyleForData.setIndention((short)1);
//					cellStyleForData.setBorderTop(ds.getBorderStyle());
//					cellStyleForData.setBorderBottom(ds.getBorderStyle());
//					cellStyleForData.setBorderLeft(ds.getBorderStyle());
//					cellStyleForData.setBorderRight(ds.getBorderStyle());
//					cellStyleForData.setAlignment(ds.getCellDataAlign());						
//					
////						firstSheet.autoSizeColumn((short)l);
//					cell.setCellStyle(cellStyleForData);
//					
//				}
//				rownum++;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}



	private void viewApporvedPayrollByGrade(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			Map hmEmpMap = CF.getEmpNameMap(con,strUserType, strEmpId);
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map hmSalaryDetails = CF.getSalaryHeadsMap(con);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);			
			Map<String, String> hmDept = CF.getDeptMap(con);
			if(hmDept == null) hmDept = new HashMap<String, String>();
			Map<String, String> hmEmpJoiningDate = CF.getEmpJoiningDateMap(con, uF);
			String strOrgName = CF.getOrgNameById(con, getF_org());
			request.setAttribute("strOrgName", strOrgName);
			String strOrgCurrId = CF.getOrgCurrencyIdByOrg(con, getF_org());
			request.setAttribute("strOrgCurrId", strOrgCurrId);
			Map<String, Map<String, String>> hmCurrencyDetailsPDF = CF.getCurrencyDetailsForPDF(con);
			request.setAttribute("hmCurrencyDetailsPDF", hmCurrencyDetailsPDF);
			
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			} else{
				wLocationList = new FillWLocation(request).fillWLocation(getF_org());
			}
			
			String strSelectedLocation="";
//			System.out.println("getF_strWLocation ===>> " +getF_strWLocation()!=null ? getF_strWLocation().length : 0);
//			System.out.println("wLocationList ===>> " +wLocationList!=null ? wLocationList.size() : 0);
			if(getF_strWLocation()!=null) {
				int k=0;
				for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
					for(int j=0;j<getF_strWLocation().length;j++) {
						if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
							if(k==0) {
								strSelectedLocation = wLocationList.get(i).getwLocationName();
							} else {
								strSelectedLocation += ", "+wLocationList.get(i).getwLocationName();
							}
							k++;
						}
					}
//					System.out.println("wLocationList.get(i).getwLocationName() in for ===>> " +wLocationList.get(i).getwLocationName());
				}
//				System.out.println("strSelectedLocation in if ===>> " +strSelectedLocation);
				if(strSelectedLocation!=null && !strSelectedLocation.equals("")) {
				} else {
					strSelectedLocation = "All Branches";
				}
			} else {
				strSelectedLocation = "All Branches";
			}
			request.setAttribute("strSelectedLocation", strSelectedLocation);
			
//			System.out.println("strSelectedLocation ===>> " +strSelectedLocation);
			
			String strEmpIds = getEmpPayrollHistory(con,uF);

			Map<String, Map<String, String>> hmEmpHistory = (Map<String, Map<String, String>>) request.getAttribute("hmEmpHistory");
			if(hmEmpHistory == null) hmEmpHistory = new HashMap<String, Map<String, String>>(); 
			
			double dblNetAmount = 0.0d;
			Map hmInner = new HashMap();
			Map hmSalary = new HashMap();
			List alEarnings = new ArrayList();
			List alDeductions = new ArrayList();
			Map hmPayPayroll = new LinkedHashMap();
			Map hmEmpPayroll = null;
			Map hmIsApprovedSalary = new HashMap();
			Map<String, String> hmPresentDays=new HashMap<String, String>();
			String strEmpIdOld = null;
			String strEmpIdNew = null;
			double dblGross = 0;
			double dblNet = 0;
//			strEmpIds = "3008";
			
			List<String> alESalaryHeads = new ArrayList<String>();
			List<String> alDSalaryHeads = new ArrayList<String>();
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true and paycycle= ?");
			sbQuery.append("select distinct(salary_head_id) as salary_head_id,weight,earning_deduction from salary_details where grade_id > 0 order by weight");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E") && !alESalaryHeads.contains(rs.getString("salary_head_id"))) {
					alESalaryHeads.add(rs.getString("salary_head_id"));
				} else if(rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("D") && !alDSalaryHeads.contains(rs.getString("salary_head_id"))) {
					alDSalaryHeads.add(rs.getString("salary_head_id"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("alESalaryHeads", alESalaryHeads);
			request.setAttribute("alDSalaryHeads", alDSalaryHeads);
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_activity_details where effective_date between ? and ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmEmpEffectiveDateGradeId = new HashMap<String, String>();
			while(rs.next()) {
				hmEmpEffectiveDateGradeId.put(rs.getString("emp_id")+"_"+rs.getString("effective_date"), rs.getString("grade_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpEffectiveDateGradeId", hmEmpEffectiveDateGradeId);
//			System.out.println("hmEmpEffectiveDateGradeId ===>> " + hmEmpEffectiveDateGradeId);
			
//			System.out.println("uF.parseToInt(CF.getRoundOffCondtion()) ===>> " +2);
			if(strEmpIds !=null && !strEmpIds.equals("") && strEmpIds.length() > 0) {
				sbQuery = new StringBuilder();
//				sbQuery.append("select * from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true and paycycle= ?");
				sbQuery.append("select epd.emp_status,eod.wlocation_id,dd.designation_id,pg.*,eod.* from payroll_generation pg, employee_official_details eod left join " +
					"employee_personal_details epd on epd.emp_per_id = eod.emp_id left join work_location_info wli on wli.wlocation_id = eod.wlocation_id " +
					"left join grades_details gd on gd.grade_id = eod.grade_id left join designation_details dd on dd.designation_id = gd.designation_id " +
					"where eod.emp_id = pg.emp_id and pg.is_paid=true and paycycle= ? ");
				sbQuery.append(" and pg.emp_id in ("+strEmpIds+") ");
				sbQuery.append(" order by wli.wlocation_weightage,epd.emp_status,dd.designation_weightage,pg.emp_id,pg.sal_effective_date,pg.salary_head_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strPC));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					strEmpIdNew = rs.getString("emp_id");
					
					hmEmpPayroll = (Map)hmPayPayroll.get(strEmpIdNew+"_"+rs.getString("sal_effective_date"));
					if(hmEmpPayroll == null) {
						hmEmpPayroll = new HashMap();
						dblNet = 0;
					}
//					System.out.println("dblNet ===>> " + dblNet);
					
//					if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
//						hmEmpPayroll = new HashMap();
//						dblNet = 0;
//					}
					
					if("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarnings.contains(rs.getString("salary_head_id"))) {
						alEarnings.add(rs.getString("salary_head_id"));
					} else if("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeductions.contains(rs.getString("salary_head_id"))) {
						alDeductions.add(rs.getString("salary_head_id"));
					}
					
	//				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
					hmEmpPayroll.put(rs.getString("salary_head_id"),  uF.getRoundOffValue(2, Math.round(uF.parseToDouble(rs.getString("amount")))));
					
					if("E".equalsIgnoreCase(rs.getString("earning_deduction"))) {
						double dblAmount = rs.getDouble("amount");
						dblGross = uF.parseToDouble((String)hmEmpPayroll.get("GROSS"));
						dblNet += dblAmount;
						hmEmpPayroll.put("GROSS",  uF.getRoundOffValue(2, Math.round((dblGross + dblAmount))));
						
					} else{
						double dblAmount = rs.getDouble("amount");
	//					double dblAmount = uF.parseToDouble((String)hmEmpPayroll.get("GROSS"));
						dblNet -= dblAmount;
	//					hmEmpPayroll.put("GROSS",  (dblGross + dblAmount)+"");
					}
					
					
					Map<String, String> hmCurrency = (Map)hmCurrencyDetails.get(rs.getString("currency_id"));
					if(hmCurrency==null)hmCurrency = new HashMap<String, String>();
					
//					hmEmpPayroll.put("NET", uF.showData(hmCurrency.get("LONG_CURR"), "")+" "+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet));
					hmEmpPayroll.put("NET", uF.getRoundOffValue(2, Math.round(dblNet)));
					hmEmpPayroll.put("EMP_STATUS", uF.showData(rs.getString("emp_status"), ""));
					hmPayPayroll.put(strEmpIdNew+"_"+rs.getString("sal_effective_date"), hmEmpPayroll);
					
					
	
					if(rs.getBoolean("is_paid")) {
						hmIsApprovedSalary.put(strEmpIdNew+"_"+rs.getString("sal_effective_date"), rs.getString("is_paid"));
					}
					if(!hmPresentDays.containsKey(rs.getString("emp_id")+"_"+rs.getString("sal_effective_date"))) {
						hmPresentDays.put(rs.getString("emp_id")+"_"+rs.getString("sal_effective_date"), rs.getString("present_days"));
					}
					
//					strEmpIdOld = strEmpIdNew;
				}
				rs.close();
				pst.close();
			}
//			System.out.println("getStrMonth()== 1 =>"+getStrMonth());
			
			if(getStrMonth()!=null) {
				setStrMonth(getStrMonth());
			} else{
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			
			Map<String, String> hmOrg =  CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			Map<String, String> hmEmpOrgId =  CF.getEmpOrgIdList(con, uF);
			if(hmEmpOrgId == null) hmEmpOrgId = new HashMap<String, String>();
			
			Map<String, String> hmWLocation = CF.getWLocationMap(con,null, null);
			request.setAttribute("hmWLocation", hmWLocation);
			
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			request.setAttribute("hmEmpWlocationMap", hmEmpWlocationMap);
			
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMapId(con);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			
			Map<String, String> hmCodeDesig = CF.getDesigMap(con);
			request.setAttribute("hmCodeDesig", hmCodeDesig);
			
			Map<String, String> hmEmpGradeMap = CF.getEmpGradeMap(con);
			request.setAttribute("hmEmpGradeMap", hmEmpGradeMap);
			
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			request.setAttribute("hmGradeMap", hmGradeMap);
			
			Map<String, String> hmGradeDesig = CF.getGradeDesig(con);
			request.setAttribute("hmGradeDesig", hmGradeDesig);
			
//			request.setAttribute("reportList", alReportList);
			request.setAttribute("alEarnings", alEarnings);
			request.setAttribute("alDeductions", alDeductions);
			request.setAttribute("hmPresentDays", hmPresentDays);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("hmPayPayroll", hmPayPayroll);
			request.setAttribute("hmEmpMap", hmEmpMap);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmIsApprovedSalary", hmIsApprovedSalary);
			
//			System.out.println("alReportList===>"+alReportList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	
	/*private void viewApporvedPayrollByGrade(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			con = db.makeConnection(con);
			
			Map hmEmpMap = CF.getEmpNameMap(con,strUserType, strEmpId);
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map hmSalaryDetails = CF.getSalaryHeadsMap(con);
			Map<String, String> hmEmpCode =CF.getEmpCodeMap(con);
			Map<String, String> hmEmpDepartment =CF.getEmpDepartmentMap(con);			
			Map<String, String> hmDept =CF.getDeptMap(con);
			if(hmDept == null) hmDept = new HashMap<String, String>();
			Map<String, String> hmEmpJoiningDate=CF.getEmpJoiningDateMap(con, uF);
			
			
			String strEmpIds = getEmpPayrollHistory(con,uF);

			Map<String, Map<String, String>> hmEmpHistory = (Map<String, Map<String, String>>) request.getAttribute("hmEmpHistory");
			if(hmEmpHistory == null) hmEmpHistory = new HashMap<String, Map<String,String>>(); 
			
			double dblNetAmount = 0.0d;
			Map hmInner = new HashMap();
			Map hmSalary = new HashMap();
			List alEarnings = new ArrayList();
			List alDeductions = new ArrayList();
			Map hmPayPayroll = new LinkedHashMap();
			Map hmEmpPayroll = null;
			Map hmIsApprovedSalary = new HashMap();
			Map<String, String> hmPresentDays=new HashMap<String, String>();
			String strEmpIdOld = null;
			String strEmpIdNew = null;
			double dblGross = 0;
			double dblNet = 0;
//			strEmpIds = "3008";
			if(strEmpIds !=null && !strEmpIds.equals("") && strEmpIds.length() > 0) {
				StringBuilder sbQuery = new StringBuilder();
//				sbQuery.append("select * from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true and paycycle= ?");
				sbQuery.append("select eod.wlocation_id,dd.designation_id,pg.*,eod.* from payroll_generation pg, employee_official_details eod left join work_location_info wli " +
					"on wli.wlocation_id = eod.wlocation_id left join grades_details gd on gd.grade_id = eod.grade_id left join designation_details dd " +
					"on dd.designation_id = gd.designation_id  where eod.emp_id = pg.emp_id and pg.is_paid=true and paycycle= ? ");
				sbQuery.append(" and pg.emp_id in ("+strEmpIds+") ");
//				sbQuery.append(" order by pg.emp_id");
				sbQuery.append(" order by wli.wlocation_weightage,dd.designation_weightage,pg.emp_id,pg.salary_head_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strPC));
				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					strEmpIdNew = rs.getString("emp_id");
					
					if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
						hmEmpPayroll = new HashMap();
						dblNet = 0;
					}
					
					if("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarnings.contains(rs.getString("salary_head_id"))) {
						alEarnings.add(rs.getString("salary_head_id"));
					} else if("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeductions.contains(rs.getString("salary_head_id"))) {
						alDeductions.add(rs.getString("salary_head_id"));
					}
					
	//				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
					hmEmpPayroll.put(rs.getString("salary_head_id"),  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("amount"))));
					
					if("E".equalsIgnoreCase(rs.getString("earning_deduction"))) {
						double dblAmount = rs.getDouble("amount");
						dblGross = uF.parseToDouble((String)hmEmpPayroll.get("GROSS"));
						dblNet += dblAmount;
						hmEmpPayroll.put("GROSS",  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblGross + dblAmount)));
						
					} else{
						double dblAmount = rs.getDouble("amount");
	//					double dblAmount = uF.parseToDouble((String)hmEmpPayroll.get("GROSS"));
						dblNet -= dblAmount;
	//					hmEmpPayroll.put("GROSS",  (dblGross + dblAmount)+"");
					}
					
					
					Map<String, String> hmCurrency = (Map)hmCurrencyDetails.get(rs.getString("currency_id"));
					if(hmCurrency==null)hmCurrency = new HashMap<String, String>();
					
//					hmEmpPayroll.put("NET", uF.showData(hmCurrency.get("LONG_CURR"), "")+" "+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet));
					hmEmpPayroll.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet));
					hmPayPayroll.put(strEmpIdNew, hmEmpPayroll);
					
					
	
					if(rs.getBoolean("is_paid")) {
						hmIsApprovedSalary.put(strEmpIdNew, rs.getString("is_paid"));
					}
					if(!hmPresentDays.containsKey(rs.getString("emp_id"))) {
						hmPresentDays.put(rs.getString("emp_id"), rs.getString("present_days"));
					}
					
					strEmpIdOld = strEmpIdNew;
				}
				rs.close();
				pst.close();
			}
//			System.out.println("getStrMonth()== 1 =>"+getStrMonth());
			
			if(getStrMonth()!=null) {
				setStrMonth(getStrMonth());
			} else{
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			
			Map<String, String> hmOrg =  CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			Map<String, String> hmEmpOrgId =  CF.getEmpOrgIdList(con, uF);
			if(hmEmpOrgId == null) hmEmpOrgId = new HashMap<String, String>();
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			if(hmWLocation == null) hmWLocation = new HashMap<String, String>();
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			if(hmEmpWlocationMap == null) hmEmpWlocationMap = new HashMap<String, String>();
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMapId(con);
			if(hmEmpCodeDesig==null) hmEmpCodeDesig = new HashMap<String, String>();
			Map<String, String> hmCodeDesig = CF.getDesigMap(con);
			if(hmCodeDesig == null) hmCodeDesig = new HashMap<String, String>();
			
			Map<String, String> hmEmpGradeMap = CF.getEmpGradeMap(con);
			if(hmEmpGradeMap == null) hmEmpGradeMap = new HashMap<String, String>();
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			if(hmGradeMap == null) hmGradeMap = new HashMap<String, String>();
			Map<String, String> hmGradeDesig = CF.getGradeDesig(con);
			if(hmGradeDesig == null) hmGradeDesig = new HashMap<String, String>();
			
			List alReportList = new ArrayList();
			List alReportInner = new ArrayList();
			Set set = hmPayPayroll.keySet();
			Iterator it = set.iterator();
			double totGross = 0.0d;
			double totNet = 0.0d;
			Map<String, String> hmTotAmtSalHead = new HashMap<String, String>();
//			StringBuilder sbEmpId = new StringBuilder();
			
			while(it.hasNext()) {
				
				alReportInner = new ArrayList();
				
				String strEmpId = (String)it.next();
				Map hmPayroll = (Map)hmPayPayroll.get(strEmpId);
				if(hmPayroll==null)hmPayroll=new HashMap();
				
				Map<String, String> hm = hmEmpHistory.get(strEmpId);
				
				alReportInner.add(uF.showData((String)hmEmpCode.get(strEmpId), ""));
				alReportInner.add(uF.showData((String)hmEmpMap.get(strEmpId), ""));
				
				String strOrg = uF.showData(hmOrg.get(hmEmpOrgId.get(strEmpId)), "");
				if(hm != null && uF.parseToInt(hm.get("EMP_ORG")) > 0) {
					strOrg = uF.showData(hmOrg.get(hm.get("EMP_ORG")), "");
				}
				alReportInner.add(strOrg);
				
				String strDepartment = uF.showData((String) hmDept.get((String)hmEmpDepartment.get(strEmpId)),"");
				if(hm != null && uF.parseToInt(hm.get("EMP_DEPART")) > 0) {
					strDepartment = uF.showData(hmDept.get(hm.get("EMP_DEPART")), "");
				}
				alReportInner.add(strDepartment);
				
				String strWLocation = uF.showData((String) hmWLocation.get((String)hmEmpWlocationMap.get(strEmpId)),"");
				if(hm != null && uF.parseToInt(hm.get("EMP_WLOCATION")) > 0) {
					strWLocation = uF.showData(hmWLocation.get(hm.get("EMP_WLOCATION")), "");
				}
				alReportInner.add(strWLocation);
				
				String strDesig = uF.showData((String) hmCodeDesig.get((String)hmEmpCodeDesig.get(strEmpId)),"");
				String strGrade = uF.showData((String) hmGradeMap.get((String)hmEmpGradeMap.get(strEmpId)),"");
				if(hm != null && uF.parseToInt(hm.get("EMP_GRADE")) > 0) {
					strDesig = uF.showData(hmCodeDesig.get(hmGradeDesig.get(hm.get("EMP_GRADE"))), "");
					strGrade = uF.showData(hmGradeMap.get(hm.get("EMP_GRADE")), "");
				}
//				alReportInner.add(uF.showData((String) hmCodeDesig.get((String)hmEmpCodeDesig.get(strEmpId)),"")+" ("+uF.showData((String) hmGradeMap.get((String)hmEmpGradeMap.get(strEmpId)),"")+")");
				alReportInner.add(strDesig+" ("+strGrade+")");
				
				alReportInner.add(uF.showData((String)hmEmpJoiningDate.get(strEmpId),""));
				alReportInner.add(uF.showData((String)hmPresentDays.get(strEmpId),""));
				
				alReportInner.add(uF.showData((String)hmPayroll.get("NET"), "0"));
				alReportInner.add(uF.showData((String)hmPayroll.get("GROSS"), "0"));
				totNet += uF.parseToDouble((String)hmPayroll.get("NET"));
				totGross += uF.parseToDouble((String)hmPayroll.get("GROSS"));
				
				for(int i=0; i<alEarnings.size(); i++) {
					alReportInner.add(uF.showData((String)hmPayroll.get((String)alEarnings.get(i)), "0"));
					double salHeadAmt = uF.parseToDouble((String)hmPayroll.get((String)alEarnings.get(i)));
					double salHeadTotAmt = uF.parseToDouble(hmTotAmtSalHead.get((String)alEarnings.get(i)));
					salHeadTotAmt += salHeadAmt;
					hmTotAmtSalHead.put((String)alEarnings.get(i), ""+salHeadTotAmt);
	    		}
	    	
	    		for(int i=0; i<alDeductions.size(); i++) {
	    			alReportInner.add(uF.showData((String)hmPayroll.get((String)alDeductions.get(i)), "0"));
	    			double salHeadAmt = uF.parseToDouble((String)hmPayroll.get((String)alDeductions.get(i)));
					double salHeadTotAmt = uF.parseToDouble(hmTotAmtSalHead.get((String)alDeductions.get(i)));
					salHeadTotAmt += salHeadAmt;
					hmTotAmtSalHead.put((String)alDeductions.get(i), ""+salHeadTotAmt);
	    		}
	    		
	    		alReportList.add(alReportInner);
			}
			
			alReportInner = new ArrayList();
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("<b>Total<b/>");
			
			alReportInner.add("<b>"+uF.formatIntoTwoDecimalWithOutComma(totNet)+"</b>");
			alReportInner.add("<b>"+uF.formatIntoTwoDecimalWithOutComma(totGross)+"</b>");
			
			for(int i=0; i<alEarnings.size(); i++) {
				alReportInner.add("<b>"+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(uF.showData((String)hmTotAmtSalHead.get((String)alEarnings.get(i)), "0")))+"</b>");
    		}
    	
    		for(int i=0; i<alDeductions.size(); i++) {
    			alReportInner.add("<b>"+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(uF.showData((String)hmTotAmtSalHead.get((String)alDeductions.get(i)), "0")))+"</b>");
    		}
    		alReportList.add(alReportInner);
    		
    		
//			System.out.println("sbEmpId ===>> " + sbEmpId.toString());
			
			request.setAttribute("reportList", alReportList);
			request.setAttribute("alEarnings", alEarnings);
			request.setAttribute("alDeductions", alDeductions);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("hmPayPayroll", hmPayPayroll);
			request.setAttribute("hmEmpMap", hmEmpMap);
			request.setAttribute("hmIsApprovedSalary", hmIsApprovedSalary);
			
//			System.out.println("alReportList===>"+alReportList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}*/
	


	public String loadPaySlips() {
		UtilityFunctions uF=new UtilityFunctions();
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		alMonthList = new FillMonth().fillMonth();
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
		employeeStatusList = new FillEmployeeStatus().fillEmployeeLiveStatus(request);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=organisationList.get(i).getOrgName();
					} else {
						strOrg+=", "+organisationList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("LOCATION");
		if(getF_strWLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_strWLocation().length;j++) {
					if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		alFilter.add("DEPARTMENT");
		if(getF_department()!=null) {
			String strDepartment="";
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				for(int j=0;j<getF_department().length;j++) {
					if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if(k==0) {
							strDepartment=departmentList.get(i).getDeptName();
						} else {
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if(strDepartment!=null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
		
		alFilter.add("SERVICE");
		if(getF_service()!=null) {
			String strService="";
			int k=0;
			for(int i=0;serviceList!=null && i<serviceList.size();i++) {
				for(int j=0;j<getF_service().length;j++) {
					if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if(k==0) {
							strService=serviceList.get(i).getServiceName();
						} else {
							strService+=", "+serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strService!=null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getF_level().length;j++) {
					if(getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if(k==0) {
							strLevel=levelList.get(i).getLevelCodeName();
						} else {
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}
		
		alFilter.add("EMPTYPE");
		if (getF_employeType() != null) {
			String stremptype = "";
			int k = 0;
			for (int i = 0; employementTypeList != null && i < employementTypeList.size(); i++) {
				for (int j = 0; j < getF_employeType().length; j++) {
					if (getF_employeType()[j].equals(employementTypeList.get(i).getEmpTypeId())) {
						if (k == 0) {
							stremptype = employementTypeList.get(i).getEmpTypeName();
						} else {
							stremptype += ", " + employementTypeList.get(i).getEmpTypeName();
						}
						k++;
					}
				}
			}
			if (stremptype != null && !stremptype.equals("")) {
				hmFilter.put("EMPTYPE", stremptype);
			} else {
				hmFilter.put("EMPTYPE", "All Employee Type");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employee Type");
		}
		
		alFilter.add("EMPSTATUS");
		if (getF_employeeStatus() != null) {
			String strEmpStatus = "";
			int k = 0;
			for (int i = 0; employeeStatusList != null && i < employeeStatusList.size(); i++) {
				for (int j = 0; j < getF_employeeStatus().length; j++) {
					if (getF_employeeStatus()[j].equals(employeeStatusList.get(i).getStatusId())) {
						if (k == 0) {
							strEmpStatus = employeeStatusList.get(i).getStatusName();
						} else {
							strEmpStatus += ", " + employeeStatusList.get(i).getStatusName();
						}
						k++;
					}
				}
			}
			if (strEmpStatus != null && !strEmpStatus.equals("")) {
				hmFilter.put("EMPSTATUS", strEmpStatus);
			} else {
				hmFilter.put("EMPSTATUS", "All Employee");
			}
		} else {
			hmFilter.put("EMPSTATUS", "All Employee");
		}
		
		alFilter.add("PAYCYCLE");
		String strPaycycle = "";
		String[] strPayCycleDates = null;
		if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			
			strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
		}
		hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}


	public String viewApporvedPayroll(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			con = db.makeConnection(con);
			
			Map hmEmpMap = CF.getEmpNameMap(con,strUserType, strEmpId);
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map hmSalaryDetails = CF.getSalaryHeadsMap(con);
			Map<String, String> hmEmpCode =CF.getEmpCodeMap(con);
			Map<String, String> hmEmpDepartment =CF.getEmpDepartmentMap(con);			
			Map<String, String> hmDept =CF.getDeptMap(con);
			if(hmDept == null) hmDept = new HashMap<String, String>();
			Map<String, String> hmEmpJoiningDate=CF.getEmpJoiningDateMap(con, uF);
			
			
			String strEmpIds = getEmpPayrollHistory(con,uF);

			Map<String, Map<String, String>> hmEmpHistory = (Map<String, Map<String, String>>) request.getAttribute("hmEmpHistory");
			if(hmEmpHistory == null) hmEmpHistory = new HashMap<String, Map<String,String>>(); 
			
			double dblNetAmount = 0.0d;
			Map hmInner = new HashMap();
			Map hmSalary = new HashMap();
			List alEarnings = new ArrayList();
			List alDeductions = new ArrayList();
			Map hmPayPayroll = new HashMap();
			Map hmEmpPayroll = null;
			Map hmIsApprovedSalary = new HashMap();
			Map<String, String> hmPresentDays=new HashMap<String, String>();
			String strEmpIdOld = null;
			String strEmpIdNew = null; 
			double dblGross = 0;
			double dblNet = 0;
//			strEmpIds = "3008";
			if(strEmpIds !=null && !strEmpIds.equals("") && strEmpIds.length() > 0) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select eod.wlocation_id,dd.designation_id,pg.*,eod.* from payroll_generation pg, employee_official_details eod left join " +
					"employee_personal_details epd on epd.emp_per_id = eod.emp_id left join work_location_info wli " +
					"on wli.wlocation_id = eod.wlocation_id left join grades_details gd on gd.grade_id = eod.grade_id left join designation_details dd " +
					"on dd.designation_id = gd.designation_id  where eod.emp_id = pg.emp_id and pg.is_paid=true and paycycle= ? ");
				sbQuery.append(" and pg.emp_id in ("+strEmpIds+") ");
				sbQuery.append(" order by wli.wlocation_weightage,epd.emp_status,dd.designation_weightage,pg.emp_id,pg.salary_head_id");
//				sbQuery.append("select * from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true and paycycle= ?");
//				sbQuery.append(" and pg.emp_id in ("+strEmpIds+") ");
//				sbQuery.append(" order by pg.emp_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strPC));
//				System.out.println("pst====>"+pst);
				
				rs = pst.executeQuery();
				while(rs.next()) {
					strEmpIdNew = rs.getString("emp_id");
					
					if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
						hmEmpPayroll = new HashMap();
						dblNet = 0;
					}
					
					if("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarnings.contains(rs.getString("salary_head_id"))) {
						alEarnings.add(rs.getString("salary_head_id"));
					} else if("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeductions.contains(rs.getString("salary_head_id"))) {
						alDeductions.add(rs.getString("salary_head_id"));
					}
	//				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
					
					hmEmpPayroll.put(rs.getString("salary_head_id"), uF.getRoundOffValue(2,uF.parseToDouble(rs.getString("amount"))));
					
					if("E".equalsIgnoreCase(rs.getString("earning_deduction"))) {
						double dblAmount = rs.getDouble("amount");
						dblGross = uF.parseToDouble((String)hmEmpPayroll.get("GROSS"));
						dblNet += dblAmount;
						hmEmpPayroll.put("GROSS",  uF.getRoundOffValue(2,(dblGross + dblAmount)));
					} else{
						double dblAmount = rs.getDouble("amount");
	//					double dblAmount = uF.parseToDouble((String)hmEmpPayroll.get("GROSS"));
						dblNet -= dblAmount;
	//					hmEmpPayroll.put("GROSS",  (dblGross + dblAmount)+"");
					}
					
					
					Map<String, String> hmCurrency = (Map)hmCurrencyDetails.get(rs.getString("currency_id"));
					if(hmCurrency==null)hmCurrency = new HashMap<String, String>();
					
//					hmEmpPayroll.put("NET", uF.showData(hmCurrency.get("LONG_CURR"), "")+" "+uF.formatIntoTwoDecimal(dblNet));
					hmEmpPayroll.put("NET", uF.formatIntoTwoDecimal(dblNet));
					hmPayPayroll.put(strEmpIdNew, hmEmpPayroll);
					
					
	
					if(rs.getBoolean("is_paid")) {
						hmIsApprovedSalary.put(strEmpIdNew, rs.getString("is_paid"));
					}
					if(!hmPresentDays.containsKey(rs.getString("emp_id"))) {
						hmPresentDays.put(rs.getString("emp_id"), rs.getString("present_days"));
					}
					
					strEmpIdOld = strEmpIdNew;
				}
				rs.close();
				pst.close();
			}
//			System.out.println("getStrMonth()== 1 =>"+getStrMonth());
			
			if(getStrMonth()!=null) {
				setStrMonth(getStrMonth());
			} else{
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			
			
			
			
//			System.out.println("hmPayPayroll===>"+hmPayPayroll);
			
			Map<String, String> hmOrg =  CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			Map<String, String> hmEmpOrgId =  CF.getEmpOrgIdList(con, uF);
			if(hmEmpOrgId == null) hmEmpOrgId = new HashMap<String, String>();
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			if(hmWLocation == null) hmWLocation = new HashMap<String, String>();
			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
			if(hmEmpWlocationMap == null) hmEmpWlocationMap = new HashMap<String, String>();
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMapId(con);
			if(hmEmpCodeDesig==null) hmEmpCodeDesig = new HashMap<String, String>();
			Map<String, String> hmCodeDesig = CF.getDesigMap(con);
			if(hmCodeDesig == null) hmCodeDesig = new HashMap<String, String>();
			
			Map<String, String> hmEmpGradeMap = CF.getEmpGradeMap(con);
			if(hmEmpGradeMap == null) hmEmpGradeMap = new HashMap<String, String>();
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			if(hmGradeMap == null) hmGradeMap = new HashMap<String, String>();
			Map<String, String> hmGradeDesig = CF.getGradeDesig(con);
			if(hmGradeDesig == null) hmGradeDesig = new HashMap<String, String>();
			
			List alReportList = new ArrayList();
			List alReportInner = new ArrayList();
			Set set = hmPayPayroll.keySet();
			Iterator it = set.iterator();
			double totGross = 0.0d;
			double totNet = 0.0d;
			Map<String, String> hmTotAmtSalHead = new HashMap<String, String>();
			
			while(it.hasNext()) {
				alReportInner = new ArrayList();
				String strEmpId = (String)it.next();
				Map hmPayroll = (Map)hmPayPayroll.get(strEmpId);
				if(hmPayroll==null)hmPayroll=new HashMap();
				
				Map<String, String> hm = hmEmpHistory.get(strEmpId);
				
				alReportInner.add(uF.showData((String)hmEmpCode.get(strEmpId), ""));
				alReportInner.add(uF.showData((String)hmEmpMap.get(strEmpId), ""));
				
				String strOrg = uF.showData(hmOrg.get(hmEmpOrgId.get(strEmpId)), "");
				if(hm != null && uF.parseToInt(hm.get("EMP_ORG")) > 0) {
					strOrg = uF.showData(hmOrg.get(hm.get("EMP_ORG")), "");
				}
				alReportInner.add(strOrg);
				
				String strDepartment = uF.showData((String) hmDept.get((String)hmEmpDepartment.get(strEmpId)),"");
				if(hm != null && uF.parseToInt(hm.get("EMP_DEPART")) > 0) {
					strDepartment = uF.showData(hmDept.get(hm.get("EMP_DEPART")), "");
				}
				alReportInner.add(strDepartment);
				
				String strWLocation = uF.showData((String) hmWLocation.get((String)hmEmpWlocationMap.get(strEmpId)),"");
				if(hm != null && uF.parseToInt(hm.get("EMP_WLOCATION")) > 0) {
					strWLocation = uF.showData(hmWLocation.get(hm.get("EMP_WLOCATION")), "");
				}
				alReportInner.add(strWLocation);
				
				String strDesig = uF.showData((String) hmCodeDesig.get((String)hmEmpCodeDesig.get(strEmpId)),"");
				String strGrade = uF.showData((String) hmGradeMap.get((String)hmEmpGradeMap.get(strEmpId)),"");
				if(hm != null && uF.parseToInt(hm.get("EMP_GRADE")) > 0) {
					strDesig = uF.showData(hmCodeDesig.get(hmGradeDesig.get(hm.get("EMP_GRADE"))), "");
					strGrade = uF.showData(hmGradeMap.get(hm.get("EMP_GRADE")), "");
				}
//				alReportInner.add(uF.showData((String) hmCodeDesig.get((String)hmEmpCodeDesig.get(strEmpId)),"")+" ("+uF.showData((String) hmGradeMap.get((String)hmEmpGradeMap.get(strEmpId)),"")+")");
				alReportInner.add(strDesig+" ("+strGrade+")");
				
				alReportInner.add(uF.showData((String)hmEmpJoiningDate.get(strEmpId),""));
				alReportInner.add(uF.showData((String)hmPresentDays.get(strEmpId),""));
				
				alReportInner.add(uF.showData((String)hmPayroll.get("NET"), "0"));
				alReportInner.add(uF.showData((String)hmPayroll.get("GROSS"), "0"));
				totNet += uF.parseToDouble((String)hmPayroll.get("NET"));
				totGross += uF.parseToDouble((String)hmPayroll.get("GROSS"));
				
				for(int i=0; i<alEarnings.size(); i++) {
					alReportInner.add(uF.showData((String)hmPayroll.get((String)alEarnings.get(i)), "0"));
					double salHeadAmt = uF.parseToDouble((String)hmPayroll.get((String)alEarnings.get(i)));
					double salHeadTotAmt = uF.parseToDouble(hmTotAmtSalHead.get((String)alEarnings.get(i)));
					salHeadTotAmt += salHeadAmt;
					hmTotAmtSalHead.put((String)alEarnings.get(i), ""+salHeadTotAmt);
	    		}
	    	
	    		for(int i=0; i<alDeductions.size(); i++) {
	    			alReportInner.add(uF.showData((String)hmPayroll.get((String)alDeductions.get(i)), "0"));
	    			double salHeadAmt = uF.parseToDouble((String)hmPayroll.get((String)alDeductions.get(i)));
					double salHeadTotAmt = uF.parseToDouble(hmTotAmtSalHead.get((String)alDeductions.get(i)));
					salHeadTotAmt += salHeadAmt;
					hmTotAmtSalHead.put((String)alDeductions.get(i), ""+salHeadTotAmt);
	    		}
	    		alReportList.add(alReportInner);
			}
			
			alReportInner = new ArrayList();
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			alReportInner.add("");
			
			alReportInner.add(""+totNet);
			alReportInner.add(""+totGross);
			
			for(int i=0; i<alEarnings.size(); i++) {
				alReportInner.add(uF.showData((String)hmTotAmtSalHead.get((String)alEarnings.get(i)), "0"));
    		}
    	
    		for(int i=0; i<alDeductions.size(); i++) {
    			alReportInner.add(uF.showData((String)hmTotAmtSalHead.get((String)alDeductions.get(i)), "0"));
    		}
    		alReportList.add(alReportInner);
			
			request.setAttribute("reportList", alReportList);
			request.setAttribute("alEarnings", alEarnings);
			request.setAttribute("alDeductions", alDeductions);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("hmPayPayroll", hmPayPayroll);
			request.setAttribute("hmEmpMap", hmEmpMap);
			request.setAttribute("hmIsApprovedSalary", hmIsApprovedSalary);
			
			
//			System.out.println("alReportList===>"+alReportList);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
		return SUCCESS;
		
	}
	
	public String getEmpPayrollHistory(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sbEmp = null;
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from payroll_history ph,employee_official_details eod, employee_personal_details epd where ph.emp_id=eod.emp_id and ph.emp_id=epd.emp_per_id and paycycle_from =? and paycycle_to=? and paycycle= ? ");
			if(getF_level()!=null && getF_level().length>0) {
				sbQuery.append(" and ph.grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and ph.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
            if (getF_employeeStatus() != null && getF_employeeStatus().length > 0) {
				sbQuery.append(" and epd.emp_status in ( '" + StringUtils.join(getF_employeeStatus(), "' , '") + "') ");
			}
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                	sbQuery.append(" ph.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            } 
            
            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
                sbQuery.append(" and ph.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and ph.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and ph.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and ph.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by ph.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPC));
//			System.out.println("pst1====>"+pst);
			rs = pst.executeQuery();
			Set<String> empSetlist = new HashSet<String>();
			Map<String, Map<String, String>> hmEmpHistory = new HashMap<String, Map<String,String>>(); 
			while (rs.next()) {
				empSetlist.add(rs.getString("emp_id"));
				
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("EMP_ORG", rs.getString("org_id"));
				hm.put("EMP_WLOCATION", rs.getString("wlocation_id"));
				hm.put("EMP_DEPART", rs.getString("depart_id"));
				hm.put("EMP_GRADE", rs.getString("grade_id"));
				
				hmEmpHistory.put(rs.getString("emp_id"), hm);
				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpHistory", hmEmpHistory);
//			System.out.println("1 hmEmpHistory====>"+hmEmpHistory);
//			System.out.println("1 empSetlist====>"+empSetlist.toString());
			
			sbQuery = new StringBuilder();
			sbQuery.append("select distinct(pg.emp_id) as emp_id from payroll_generation pg, employee_official_details eod, employee_personal_details epd where eod.emp_id = pg.emp_id and epd.emp_per_id = pg.emp_id and pg.is_paid=true and paid_from= ? and paid_to=? and  paycycle= ?");
			
			if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
            if (getF_employeeStatus() != null && getF_employeeStatus().length > 0) {
				sbQuery.append(" and epd.emp_status in ( '" + StringUtils.join(getF_employeeStatus(), "' , '") + "') ");
			}
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                	sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            } 
            
            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(" and pg.emp_id not in (select emp_id from payroll_history where paycycle_from =? and paycycle_to=? and paycycle= ?) ");
			sbQuery.append(" order by pg.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPC));
			pst.setDate(4, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strPC));
//			System.out.println("pst2====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				empSetlist.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("2 empSetlist====>"+empSetlist.toString());
			
			Iterator<String> it = empSetlist.iterator();
			while(it.hasNext()) {
				String strEmp = it.next();
				if(sbEmp == null) {
					sbEmp = new StringBuilder();
					sbEmp.append(strEmp);
				} else {
					sbEmp.append(","+strEmp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
		return sbEmp!=null ? sbEmp.toString() : null;
	}

	private HttpServletRequest request;
	HttpServletResponse response;

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


	public String getPaycycle() {
		return paycycle;
	}


	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}


	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}


	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}


	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}


	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}


	public String getStrMonth() {
		return strMonth;
	}


	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}


	public String getStrApprove() {
		return strApprove;
	}


	public void setStrApprove(String strApprove) {
		this.strApprove = strApprove;
	}


	public String[] getChbxApprove() {
		return chbxApprove;
	}


	public void setChbxApprove(String[] chbxApprove) {
		this.chbxApprove = chbxApprove;
	}


	public List<FillMonth> getAlMonthList() {
		return alMonthList;
	}


	public void setAlMonthList(List<FillMonth> alMonthList) {
		this.alMonthList = alMonthList;
	}


	public String getApprovePC() {
		return approvePC;
	}


	public void setApprovePC(String approvePC) {
		this.approvePC = approvePC;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}


	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}


	public List<FillServices> getServiceList() {
		return serviceList;
	}


	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}


	public String[] getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String[] getF_department() {
		return f_department;
	}


	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}


	public String[] getF_level() {
		return f_level;
	}


	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}


	public String[] getF_service() {
		return f_service;
	}


	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}


	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}


	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public String getStrLocation() {
		return strLocation;
	}


	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}


	public String getStrDepartment() {
		return strDepartment;
	}


	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}


	public String getStrSbu() {
		return strSbu;
	}


	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}


	public String getStrLevel() {
		return strLevel;
	}


	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public List<FillEmployeeStatus> getEmployeeStatusList() {
		return employeeStatusList;
	}

	public void setEmployeeStatusList(List<FillEmployeeStatus> employeeStatusList) {
		this.employeeStatusList = employeeStatusList;
	}

	public String getStrEmployeeStatus() {
		return strEmployeeStatus;
	}

	public void setStrEmployeeStatus(String strEmployeeStatus) {
		this.strEmployeeStatus = strEmployeeStatus;
	}

	public String[] getF_employeeStatus() {
		return f_employeeStatus;
	}

	public void setF_employeeStatus(String[] f_employeeStatus) {
		this.f_employeeStatus = f_employeeStatus;
	}
	
}
