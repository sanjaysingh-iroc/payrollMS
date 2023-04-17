package com.konnect.jpms.export.payroll;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.util.UtilityFunctions;

public class ExcelSheetDesign {

	
	public void getFullFinalExcelSheetDesignData(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<List<List<DataStyle>>> allReportData) {
//		Row headerRow = sheet.createRow(4);
		Row reportNameRow = sheet.createRow(1);
		
		HSSFCellStyle cellStyleForReportName = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight((short)1200);	
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);	
		
		DataStyle ds = (DataStyle)header.get(0);
		
		//Cell reportName = reportNameRow.createCell(header.size()/2);
		Cell reportName = reportNameRow.createCell((short)0);
		
		reportName.setCellValue(ds.getStrData());
		cellStyleForReportName.setFont(reportNameFont);
		reportName.setCellStyle(cellStyleForReportName);
		
		sheet.addMergedRegion(CellRangeAddress.valueOf("A2:D2"));
		
//		for(int i=1,y=1;i<header.size();i++,y++){
//			Cell headerCell = headerRow.createCell(y);
//			ds = (DataStyle)header.get(i);
//			headerCell.setCellValue("  "+ds.getStrData()+"  ");
//			HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
//			cellStyleForHeader.setBorderBottom(ds.getBorderStyle());
//			cellStyleForHeader.setBorderLeft(ds.getBorderStyle());
//			cellStyleForHeader.setBorderRight(ds.getBorderStyle());
//			cellStyleForHeader.setBorderTop(ds.getBorderStyle());
//			cellStyleForHeader.setAlignment(ds.getCellDataAlign());
//			cellStyleForHeader.setFillForegroundColor(ds.getHSSFbackRoundColor());
//			cellStyleForHeader.setFillPattern(ds.getFillPattern());
//			cellStyleForHeader.setFont(font);
//			//sheet.autoSizeColumn((short)y);
//			headerCell.setCellStyle(cellStyleForHeader);
//		}
		
		int rownum = 2;
		int rowcnt = 3;
		HSSFCellStyle cellStyleForData;
		
		for (int a = 0; allReportData != null && a < allReportData.size(); a++) {
			List<List<DataStyle>> reportData = allReportData.get(a);
			
			if(a == 0 || a == 2 || a == 4 || a == 7 || a == 9 || a == 11) {
				for (int j = 0; reportData != null && j < reportData.size(); j++) {
					Row row = sheet.createRow(rownum);
					List<DataStyle> userData = reportData.get(j);
					for (int k = 0, l=0; k < userData.size(); k++,l++) {
						Cell cell = row.createCell(l);
						ds = (DataStyle)userData.get(k);
					//===start parvez date: 16-08-2022===	
						cell.setCellValue(ds.getStrData());	
					//===end parvez date: 16-08-2022===	
						cellStyleForData = workbook.createCellStyle();
						cellStyleForData.setBorderTop(ds.getBorderStyle());
						cellStyleForData.setBorderBottom(ds.getBorderStyle());
						cellStyleForData.setBorderLeft(ds.getBorderStyle());
						cellStyleForData.setBorderRight(ds.getBorderStyle());
						cellStyleForData.setAlignment(ds.getCellDataAlign());						
//						sheet.autoSizeColumn((short)l);
						cell.setCellStyle(cellStyleForData);
					}
					rownum++;
					rowcnt++;
				}
			} else if(a == 3 || a == 13) {
				for (int j = 0; reportData != null && j < reportData.size(); j++) {
					Row row = sheet.createRow(rownum);
					List<DataStyle> userData = reportData.get(j);
					for (int k = 0, l=0; k < userData.size(); k++,l++) {
						Cell cell = row.createCell(l);
						ds = (DataStyle)userData.get(k);
					//===start parvez date: 16-08-2022===	
						cell.setCellValue(ds.getStrData());	
					//===end parvez date: 16-08-2022===	
						cellStyleForData = workbook.createCellStyle();
						cellStyleForData.setBorderTop(ds.getBorderStyle());
						cellStyleForData.setBorderBottom(ds.getBorderStyle());
						cellStyleForData.setBorderLeft(ds.getBorderStyle());
						cellStyleForData.setBorderRight(ds.getBorderStyle());
						cellStyleForData.setAlignment(ds.getCellDataAlign());
//						if(k == 0) {
//							sheet.addMergedRegion(CellRangeAddress.valueOf("A"+rowcnt + ":B"+rowcnt));
//							System.out.println("cell ===>>> " + cell);
//						} else {
//							sheet.addMergedRegion(CellRangeAddress.valueOf("C"+rowcnt + ":D"+rowcnt));
//							System.out.println("cell ===>>> " + cell);
//						}
						if(a == 3) {
							cellStyleForData.setFont(reportNameFont);
						}
//						sheet.autoSizeColumn((short)l);
						cell.setCellStyle(cellStyleForData);
					}
					rownum++;
					rowcnt++;
				}
			} else if(a == 1 || a == 5|| a == 8) {
				for (int j = 0; reportData != null && j < reportData.size(); j++) {
					Row row = sheet.createRow(rownum);
					List<DataStyle> userData = reportData.get(j);
					for (int k = 0, l=0; k < userData.size(); k++,l++) {
						Cell cell = row.createCell(l);
						ds = (DataStyle)userData.get(k);
					//===start parvez date: 16-08-2022===	
						cell.setCellValue(ds.getStrData());	
					//===start parvez date: 16-08-2022===	
						cellStyleForData = workbook.createCellStyle();
						cellStyleForData.setBorderTop(ds.getBorderStyle());
						cellStyleForData.setBorderBottom(ds.getBorderStyle());
						cellStyleForData.setBorderLeft(ds.getBorderStyle());
						cellStyleForData.setBorderRight(ds.getBorderStyle());
						cellStyleForData.setAlignment(ds.getCellDataAlign());
						if(k > 0) {
							sheet.addMergedRegion(CellRangeAddress.valueOf("B"+rowcnt + ":D"+rowcnt));
						}
						if(a == 5 || a == 8) {
							cellStyleForData.setFont(reportNameFont);
						}
//						sheet.autoSizeColumn((short)l);
						cell.setCellStyle(cellStyleForData);
					}
					rownum++;
					rowcnt++;
				}
			} else if(a == 6 || a == 10 || a == 12) {
				for (int j = 0; reportData != null && j < reportData.size(); j++) {
					Row row = sheet.createRow(rownum);
					List<DataStyle> userData = reportData.get(j);
					for (int k = 0, l=0; k < userData.size(); k++,l++) {
						Cell cell = row.createCell(l);
						ds = (DataStyle)userData.get(k);
					//===start parvez date: 16-08-2022===	
						cell.setCellValue(ds.getStrData());	
					//===end parvez date: 16-08-2022===	
						cellStyleForData = workbook.createCellStyle();
						cellStyleForData.setBorderTop(ds.getBorderStyle());
						cellStyleForData.setBorderBottom(ds.getBorderStyle());
						cellStyleForData.setBorderLeft(ds.getBorderStyle());
						cellStyleForData.setBorderRight(ds.getBorderStyle());
						cellStyleForData.setAlignment(ds.getCellDataAlign());
						sheet.addMergedRegion(CellRangeAddress.valueOf("A"+rowcnt + ":D"+rowcnt));
						if(a == 6) {
							cellStyleForData.setFont(reportNameFont);
						}
//						sheet.autoSizeColumn((short)l);
						cell.setCellStyle(cellStyleForData);
					}
					rownum++;
					rowcnt++;
				}
			}
		}
	}
	
	
	public void generateExcelSheetforMonthlySalarySummary(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header,Map hmEarningSalaryMap,Map hmDeductionSalaryMap,Map hmSalaryHeadMap,Map hmEarningSalaryTotalMap,Map hmDeductionSalaryTotalMap) {

		Row mainHeadingRow = sheet.createRow(0);
		Row reportOrgName = sheet.createRow(2);
		Row reportNameRow = sheet.createRow(4);
	    //Row rDate = sheet.createRow(6);
		Row rGross = sheet.createRow(6);
		//Row detailsRow = sheet.createRow(8);
		
		Font reportHeadingFont = workbook.createFont();
		reportHeadingFont.setBoldweight((short)1200);	
		HSSFCellStyle cellStyleForMainHeadingRow = workbook.createCellStyle();
		cellStyleForMainHeadingRow.setAlignment(cellStyleForMainHeadingRow.ALIGN_CENTER);
		Cell reportHeading = mainHeadingRow.createCell((short)6);		
		reportHeading.setCellValue("Salary Monthly Summary Report");
		cellStyleForMainHeadingRow.setFont(reportHeadingFont);
		reportHeading.setCellStyle(cellStyleForMainHeadingRow);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("G1:K1"));
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);	
		
		HSSFCellStyle cellStyleForSummary = workbook.createCellStyle();
		cellStyleForSummary.setAlignment(cellStyleForSummary.ALIGN_LEFT);
	
		DataStyle ds1 = (DataStyle)header.get(0);		
		Cell reportsummarydtls = reportOrgName.createCell((short)1);		
		reportsummarydtls.setCellValue(ds1.getStrData());
		cellStyleForSummary.setFont(reportNameFont);
		reportsummarydtls.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("B3:D3"));
		
		HSSFCellStyle cellStyleForReportName = workbook.createCellStyle();
		cellStyleForReportName.setAlignment(cellStyleForReportName.ALIGN_RIGHT);
		Font font = workbook.createFont();
		font.setBoldweight((short)1200);	
		
		DataStyle dsOrg = (DataStyle)header.get(1);		
		Cell reportOrg = reportOrgName.createCell((short)6);		
		reportOrg.setCellValue(dsOrg.getStrData());
		cellStyleForReportName.setFont(reportNameFont);
		reportOrg.setCellStyle(cellStyleForReportName);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("G3:K3"));
		
		DataStyle ds = (DataStyle)header.get(2);		
		Cell reportName = reportNameRow.createCell((short)1);		
		reportName.setCellValue(ds.getStrData());
		reportName.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("B5:D5"));
		
		DataStyle dsgross = (DataStyle)header.get(3);		
		Cell gross = rGross.createCell((short)1);		
		gross.setCellValue(dsgross.getStrData());
		gross.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("B7:D7"));
		
		DataStyle dsdeduction = (DataStyle)header.get(4);		
		Cell deduction = rGross.createCell((short)6);		
		deduction.setCellValue(dsdeduction.getStrData());
		deduction.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("G7:J7"));
		
		DataStyle dsnet = (DataStyle)header.get(5);		
		Cell net = rGross.createCell((short)12);		
		net.setCellValue(dsnet.getStrData());
		net.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("M7:P7"));
		/*DataStyle dsDate = (DataStyle)header.get(3);		
		Cell reportDate = rDate.createCell((short)1);		
		reportDate.setCellValue(dsDate.getStrData());
		cellStyleForReportName.setFont(reportNameFont);
		reportDate.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("B7:D7"));*/
		
		/*for(int i=4,y=1;i<header.size();i++,y++){
			Cell headerCell = headerRow.createCell(y);
			ds = (DataStyle)header.get(i);
			
			headerCell.setCellValue("  "+ds.getStrData()+"  ");
			HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
			cellStyleForHeader.setBorderBottom(ds.getBorderStyle());
			cellStyleForHeader.setBorderLeft(ds.getBorderStyle());
			cellStyleForHeader.setBorderRight(ds.getBorderStyle());
			cellStyleForHeader.setBorderTop(ds.getBorderStyle());
			cellStyleForHeader.setAlignment(ds.getCellDataAlign());
			cellStyleForHeader.setFillForegroundColor(ds.getHSSFbackRoundColor());
			cellStyleForHeader.setFillPattern(ds.getFillPattern());
			cellStyleForHeader.setFont(font);
//			sheet.autoSizeColumn((short)y);
			headerCell.setCellStyle(cellStyleForHeader);
		}*/
		
		int rownum = 10;
		HSSFCellStyle cellStyleForData;
		
		List<List<String>>salaryData = new ArrayList<List<String>>();
		List<String>innerList = new ArrayList<String>();
	    Row tableheader = sheet.createRow(9);
	    Cell headerCell1 = tableheader.createCell(1);
	    Cell headerCell2= tableheader.createCell(2);
	    Cell headerCell3 = tableheader.createCell(6);
	    Cell headerCell4 = tableheader.createCell(7);
	    headerCell1.setCellValue("Earnings");
	    headerCell2.setCellValue("Amount");
	    headerCell3.setCellValue("Deductions");
	    headerCell4.setCellValue("Amount");
	    
			Set setE = hmEarningSalaryMap.keySet();
			Iterator itE = setE.iterator();
			
			List<String> strSalaryHeadId = new ArrayList<String>();
			List<String> strSalaryHeadId1 = new ArrayList<String>();
			while (itE.hasNext()) {
				strSalaryHeadId.add((String) itE.next());
			}
			int i =0,j=0;
			Set setD = hmDeductionSalaryMap.keySet();
			Iterator itD = setD.iterator();
			while (itD.hasNext()) {
				strSalaryHeadId1.add((String) itD.next());
			}
			  while (i<strSalaryHeadId.size() || j<strSalaryHeadId1.size()){
				 Row row = sheet.createRow(rownum);
				 if(i<strSalaryHeadId.size()){
				Cell cell1 = row.createCell((short)1);
			    cell1.setCellValue((String)hmSalaryHeadMap.get(strSalaryHeadId.get(i)));
			    Cell cell2 = row.createCell((short)2);
			    cell2.setCellValue((String)hmEarningSalaryMap.get(strSalaryHeadId.get(i)));
				 }
			    Cell cell3 =row.createCell((short)6);
			    cell3.setCellValue((String)hmSalaryHeadMap.get(strSalaryHeadId1.get(i)));
			    Cell cell4 = row.createCell((short)7);
			    cell4.setCellValue((String)hmDeductionSalaryMap.get(strSalaryHeadId1.get(i)));
			    i++;
			    j++;
			    rownum++;
			  }
			  Row totalRow = sheet.createRow(rownum);
			  Cell earningsTotal = totalRow.createCell((short)1);
			  earningsTotal.setCellStyle(cellStyleForSummary);
			  earningsTotal.setCellValue("Total Earnings");
			  Cell earningsTotalAmt = totalRow.createCell((short)2);
			  if(hmEarningSalaryTotalMap.get("TOTAL")=="" || hmEarningSalaryTotalMap.get("TOTAL")==null){
				  earningsTotalAmt.setCellValue("0");  
			  }
			  else{
				  earningsTotalAmt.setCellValue((String)hmEarningSalaryTotalMap.get("TOTAL"));
			  }
			  Cell deductionsTotal = totalRow.createCell((short)6);
			  deductionsTotal.setCellStyle(cellStyleForSummary);
			  deductionsTotal.setCellValue("Total Deductions");
			  Cell deductionsTotalAmt = totalRow.createCell((short)7);
			  if(hmDeductionSalaryTotalMap.get("TOTAL")=="" || hmDeductionSalaryTotalMap.get("TOTAL")==null){
				  deductionsTotalAmt.setCellValue("0");  
			  }
			  else{
				  deductionsTotalAmt.setCellValue((String)hmDeductionSalaryTotalMap.get("TOTAL"));
			  }
//			  System.out.println("hmEarningSalaryTotalMap====>"+hmEarningSalaryTotalMap.get("TOTAL"));
//			  System.out.println("hmDeductionSalaryTotalMap====>"+hmDeductionSalaryTotalMap.get("TOTAL"));
		}
		
	
	public void getExcelSheetDesignData(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<List<DataStyle>> reportData) {
		Row headerRow = sheet.createRow(4);
		Row reportNameRow = sheet.createRow(2);
		
		HSSFCellStyle cellStyleForReportName = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight((short)1200);	
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);	
		
		DataStyle ds = (DataStyle)header.get(0);
		
		//Cell reportName = reportNameRow.createCell(header.size()/2);
		Cell reportName = reportNameRow.createCell((short)1);
		
		reportName.setCellValue(ds.getStrData());
		cellStyleForReportName.setFont(reportNameFont);
		reportName.setCellStyle(cellStyleForReportName);
		
		sheet.addMergedRegion(CellRangeAddress.valueOf("B3:E3"));
		
		for(int i=1,y=1;i<header.size();i++,y++){
			Cell headerCell = headerRow.createCell(y);
			ds = (DataStyle)header.get(i);
			
		//===start parvez date: 16-08-2022===	
			headerCell.setCellValue(ds.getStrData());
		//===end parvez date: 16-08-2022===	
			HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
			cellStyleForHeader.setBorderBottom(ds.getBorderStyle());
			cellStyleForHeader.setBorderLeft(ds.getBorderStyle());
			cellStyleForHeader.setBorderRight(ds.getBorderStyle());
			cellStyleForHeader.setBorderTop(ds.getBorderStyle());
			cellStyleForHeader.setAlignment(ds.getCellDataAlign());
			cellStyleForHeader.setFillForegroundColor(ds.getHSSFbackRoundColor());
			cellStyleForHeader.setFillPattern(ds.getFillPattern());
			cellStyleForHeader.setFont(font);
//			sheet.autoSizeColumn((short)y);
			headerCell.setCellStyle(cellStyleForHeader);
		}
		
		
		int rownum = 5;
		HSSFCellStyle cellStyleForData;
		
		for (int j = 0; reportData!=null && j < reportData.size(); j++) {
			Row row = sheet.createRow(rownum);
			List<DataStyle> userData = reportData.get(j);
			for (int k = 0, l=1; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(l);
				ds = (DataStyle)userData.get(k);
				
			//===start parvez date: 16-08-2022===	
				cell.setCellValue(ds.getStrData());	
			//===end parvez date: 16-08-2022===	
				cellStyleForData = workbook.createCellStyle();
				cellStyleForData.setBorderTop(ds.getBorderStyle());
				cellStyleForData.setBorderBottom(ds.getBorderStyle());
				cellStyleForData.setBorderLeft(ds.getBorderStyle());
				cellStyleForData.setBorderRight(ds.getBorderStyle());
				cellStyleForData.setAlignment(ds.getCellDataAlign());				
				
//				sheet.autoSizeColumn((short)l);
				cell.setCellStyle(cellStyleForData);
				
			}
			rownum++;
		}
		
	}

	public void generateExcelSheet(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<List<DataStyle>> reportData) {
		Row reportOrgName = sheet.createRow(2);
		Row reportNameRow = sheet.createRow(4);
		Row rDate = sheet.createRow(6);
		Row headerRow = sheet.createRow(4);
		
		HSSFCellStyle cellStyleForReportName = workbook.createCellStyle();
		cellStyleForReportName.setAlignment(cellStyleForReportName.ALIGN_RIGHT);
		Font font = workbook.createFont();
		font.setBoldweight((short)1200);	
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);	
		
		HSSFCellStyle cellStyleForSummary = workbook.createCellStyle();
		cellStyleForSummary.setAlignment(cellStyleForSummary.ALIGN_LEFT);
	
		DataStyle ds1 = (DataStyle)header.get(0);		
		Cell reportsummarydtls = reportOrgName.createCell((short)1);		
		reportsummarydtls.setCellValue(ds1.getStrData());
		cellStyleForSummary.setFont(reportNameFont);
		reportsummarydtls.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("B2:D2"));
		
		DataStyle dsOrg = (DataStyle)header.get(1);		
		Cell reportOrg = reportOrgName.createCell((short)6);		
		reportOrg.setCellValue(dsOrg.getStrData());
		cellStyleForReportName.setFont(reportNameFont);
		reportOrg.setCellStyle(cellStyleForReportName);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("G2:K2"));
		
		DataStyle ds = (DataStyle)header.get(2);		
		Cell reportName = reportNameRow.createCell((short)1);		
		reportName.setCellValue(ds.getStrData());
		cellStyleForReportName.setFont(reportNameFont);
		reportName.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("B3:D3"));
		
		DataStyle dsDate = (DataStyle)header.get(3);		
		Cell reportDate = rDate.createCell((short)1);		
		reportDate.setCellValue(dsDate.getStrData());
		cellStyleForReportName.setFont(reportNameFont);
		reportDate.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("B4:D4"));
		
		for(int i=4,y=1;i<header.size();i++,y++){
			Cell headerCell = headerRow.createCell(y);
			ds = (DataStyle)header.get(i);
			
			headerCell.setCellValue("  "+ds.getStrData()+"  ");
			HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
			cellStyleForHeader.setBorderBottom(ds.getBorderStyle());
			cellStyleForHeader.setBorderLeft(ds.getBorderStyle());
			cellStyleForHeader.setBorderRight(ds.getBorderStyle());
			cellStyleForHeader.setBorderTop(ds.getBorderStyle());
			cellStyleForHeader.setAlignment(ds.getCellDataAlign());
			cellStyleForHeader.setFillForegroundColor(ds.getHSSFbackRoundColor());
			cellStyleForHeader.setFillPattern(ds.getFillPattern());
			cellStyleForHeader.setFont(font);
//			sheet.autoSizeColumn((short)y);
			headerCell.setCellStyle(cellStyleForHeader);
		}
		
		int rownum = 5;
		HSSFCellStyle cellStyleForData;
		
		for (int j = 0; reportData!=null && j < reportData.size(); j++) {
			Row row = sheet.createRow(rownum);
			List<DataStyle> userData = reportData.get(j);
			for (int k = 0, l=1; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(l);
				ds = (DataStyle)userData.get(k);
				
			//===start parvez date: 16-08-2022===	
				cell.setCellValue(ds.getStrData());	
			//===end parvez date: 16-08-2022===	
				cellStyleForData = workbook.createCellStyle();
				cellStyleForData.setBorderTop(ds.getBorderStyle());
				cellStyleForData.setBorderBottom(ds.getBorderStyle());
				cellStyleForData.setBorderLeft(ds.getBorderStyle());
				cellStyleForData.setBorderRight(ds.getBorderStyle());
				cellStyleForData.setAlignment(ds.getCellDataAlign());						
				
//				sheet.autoSizeColumn((short)l);
				cell.setCellStyle(cellStyleForData);
			}
			rownum++;
		}
		
	}
	
	public void generateDefualtExcelSheet(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<List<DataStyle>> reportData) {
		Row headerRow = sheet.createRow(0);
//		System.out.println("HSSF");
		Font font = workbook.createFont();
		font.setBoldweight((short)1200);	
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);	
		
		for(int i=0,y=0;i<header.size();i++,y++){
			Cell headerCell = headerRow.createCell(y);
			DataStyle ds = (DataStyle)header.get(i);
			
	//===start parvez date: 16-08-2022===		
//			headerCell.setCellValue("  "+ds.getStrData()+"  ");
			headerCell.setCellValue(ds.getStrData());
	//===end parvez date: 16-08-2022===		
			HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
			cellStyleForHeader.setBorderBottom(ds.getBorderStyle());
			cellStyleForHeader.setBorderLeft(ds.getBorderStyle());
			cellStyleForHeader.setBorderRight(ds.getBorderStyle());
			cellStyleForHeader.setBorderTop(ds.getBorderStyle());
			cellStyleForHeader.setAlignment(ds.getCellDataAlign());
			cellStyleForHeader.setFillForegroundColor(ds.getHSSFbackRoundColor());
			cellStyleForHeader.setFillPattern(ds.getFillPattern());
			cellStyleForHeader.setFont(font);
//			sheet.autoSizeColumn((short)y);
			headerCell.setCellStyle(cellStyleForHeader);
		}
		
		int rownum = 1;
		HSSFCellStyle cellStyleForData;
		
		for (int j = 0; reportData!=null && j < reportData.size(); j++) {
			Row row = sheet.createRow(rownum);
			List<DataStyle> userData = reportData.get(j);
			for (int k = 0, l=0; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(l);
				DataStyle ds = (DataStyle)userData.get(k);
				
			//===start parvez date: 16-08-2022===	
//				cell.setCellValue(" "+ds.getStrData()+" ");
				cell.setCellValue(ds.getStrData());
			//===end parvez date: 16-08-2022===	
				cellStyleForData = workbook.createCellStyle();
				cellStyleForData.setBorderTop(ds.getBorderStyle());
				cellStyleForData.setBorderBottom(ds.getBorderStyle());
				cellStyleForData.setBorderLeft(ds.getBorderStyle());
				cellStyleForData.setBorderRight(ds.getBorderStyle());
				cellStyleForData.setAlignment(ds.getCellDataAlign());						
				
//				sheet.autoSizeColumn((short)l);
				cell.setCellStyle(cellStyleForData);
				
			}
			rownum++;
		}
	}
	
public void generateDefualtExcelSheet(HSSFWorkbook workbook, HSSFSheet sheet,List<List<DataStyle>> mainHeader, List<DataStyle> header, List<List<DataStyle>> reportData) {
		
		Font font = workbook.createFont();
		font.setBoldweight((short)1200);	
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);	
		
		int nMainHeader = mainHeader !=null ? mainHeader.size() : 0;
		for (int j = 0; j < nMainHeader; j++) {
			Row row = sheet.createRow(j);
			List<DataStyle> innerHeader = mainHeader.get(j);
			int nInnerHeader = innerHeader != null ? innerHeader.size() : 0;
			for (int k = 0, l=0; k < nInnerHeader; k++,l++) {
				Cell cell = row.createCell((l+1));
				DataStyle ds = (DataStyle)innerHeader.get(k);
				
		//===start parvez date: 16-08-2022===
//				cell.setCellValue(" "+ds.getStrData()+" ");
				cell.setCellValue(ds.getStrData());
		//===end parvez date: 16-08-2022===		
				
				HSSFCellStyle cellStyleForData = workbook.createCellStyle();
				cellStyleForData = workbook.createCellStyle();
				cellStyleForData.setBorderTop(ds.getBorderStyle());
				cellStyleForData.setBorderBottom(ds.getBorderStyle());
				cellStyleForData.setBorderLeft(ds.getBorderStyle());
				cellStyleForData.setBorderRight(ds.getBorderStyle());
				cellStyleForData.setAlignment(ds.getCellDataAlign());						
				
//				sheet.autoSizeColumn((short)l);
				cell.setCellStyle(cellStyleForData);
				
			}
		}
		
		Row headerRow = sheet.createRow((nMainHeader + 1));
		for(int i=0,y=0;i<header.size();i++,y++){
			Cell headerCell = headerRow.createCell(y);
			DataStyle ds = (DataStyle)header.get(i);
			
		//===start parvez date: 16-08-2022===
//			headerCell.setCellValue("  "+ds.getStrData()+"  ");
			headerCell.setCellValue(ds.getStrData());
		//===end parvez date: 16-08-2022===	
			HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
			cellStyleForHeader.setBorderBottom(ds.getBorderStyle());
			cellStyleForHeader.setBorderLeft(ds.getBorderStyle());
			cellStyleForHeader.setBorderRight(ds.getBorderStyle());
			cellStyleForHeader.setBorderTop(ds.getBorderStyle());
			cellStyleForHeader.setAlignment(ds.getCellDataAlign());
			cellStyleForHeader.setFillForegroundColor(ds.getHSSFbackRoundColor());
			cellStyleForHeader.setFillPattern(ds.getFillPattern());
			cellStyleForHeader.setFont(font);
//			sheet.autoSizeColumn((short)y);
			headerCell.setCellStyle(cellStyleForHeader);
		}
		
		
		int rownum = nMainHeader + 2;
		HSSFCellStyle cellStyleForData;
		
		for (int j = 0; reportData!=null && j < reportData.size(); j++) {
			Row row = sheet.createRow(rownum);
			List<DataStyle> userData = reportData.get(j);
			for (int k = 0, l=1; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(l);
				DataStyle ds = (DataStyle)userData.get(k);
				
			//===start parvez date: 16-08-2022===	
				cell.setCellValue(ds.getStrData());	
			//===end parvez date: 16-08-2022===
				cellStyleForData = workbook.createCellStyle();
				cellStyleForData.setBorderTop(ds.getBorderStyle());
				cellStyleForData.setBorderBottom(ds.getBorderStyle());
				cellStyleForData.setBorderLeft(ds.getBorderStyle());
				cellStyleForData.setBorderRight(ds.getBorderStyle());
				cellStyleForData.setAlignment(ds.getCellDataAlign());						
				
//				sheet.autoSizeColumn((short)l);
				cell.setCellStyle(cellStyleForData);
				
			}
			rownum++;
		}
		
	}

	public void getExcelSheetDesignDataPay(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<List<DataStyle>> reportData) {
		Row headerRow = sheet.createRow(4);
		Row reportNameRow = sheet.createRow(2);
		
		HSSFCellStyle cellStyleForReportName = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight((short)1200);	
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);	
		
		DataStyle ds = (DataStyle)header.get(0);
		
		//Cell reportName = reportNameRow.createCell(header.size()/2);
		Cell reportName = reportNameRow.createCell((short)1);
		
		reportName.setCellValue(ds.getStrData());
		cellStyleForReportName.setFont(reportNameFont);
		reportName.setCellStyle(cellStyleForReportName);
		
		sheet.addMergedRegion(CellRangeAddress.valueOf("B3:E3"));
		
		for(int i=1,y=0;i<header.size();i++,y++){
			Cell headerCell = headerRow.createCell(y);
			ds = (DataStyle)header.get(i);
			
	//===start parvez date: 16-08-2022===		
			headerCell.setCellValue(ds.getStrData());
	//===end parvez date: 16-08-2022===		
			HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
			cellStyleForHeader.setBorderBottom(ds.getBorderStyle());
			cellStyleForHeader.setBorderLeft(ds.getBorderStyle());
			cellStyleForHeader.setBorderRight(ds.getBorderStyle());
			cellStyleForHeader.setBorderTop(ds.getBorderStyle());
			cellStyleForHeader.setAlignment(ds.getCellDataAlign());
			cellStyleForHeader.setFillForegroundColor(ds.getHSSFbackRoundColor());
			cellStyleForHeader.setFillPattern(ds.getFillPattern());
			cellStyleForHeader.setFont(font);
//			sheet.autoSizeColumn((short)y);
			headerCell.setCellStyle(cellStyleForHeader);
			
			
		}
		
		
		int rownum = 5;
		HSSFCellStyle cellStyleForData;
		
		
		for (int j = 0; reportData!=null && j < reportData.size(); j++) {
			Row row = sheet.createRow(rownum);
			List<DataStyle> userData = reportData.get(j);
			for (int k = 0, l=0; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(l);
				ds = (DataStyle)userData.get(k);
				
			//===start parvez date: 16-08-2022===	
				cell.setCellValue(ds.getStrData());	
			//===end parvez date: 16-08-2022===	
				cellStyleForData = workbook.createCellStyle();
				cellStyleForData.setBorderTop(ds.getBorderStyle());
				cellStyleForData.setBorderBottom(ds.getBorderStyle());
				cellStyleForData.setBorderLeft(ds.getBorderStyle());
				cellStyleForData.setBorderRight(ds.getBorderStyle());
				cellStyleForData.setAlignment(ds.getCellDataAlign());						
				
//				sheet.autoSizeColumn((short)l);
				cell.setCellStyle(cellStyleForData);
				
			}
			rownum++;
		}	
	}
	
	
	public void generateRosterWithRulesExcelSheet(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<List<DataStyle>> reportData) {
		Row headerRow = sheet.createRow(0);
		
		Font font = workbook.createFont();
		font.setBoldweight((short)1200);	
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);	
		
		for(int i=0,y=0;i<header.size();i++,y++){
			Cell headerCell = headerRow.createCell(y);
			DataStyle ds = (DataStyle)header.get(i);
			
		//===start parvez date: 16-08-2022===	
			headerCell.setCellValue(ds.getStrData());
		//===end parvez date: 16-08-2022===	
			HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
			cellStyleForHeader.setBorderBottom(ds.getBorderStyle());
			cellStyleForHeader.setBorderLeft(ds.getBorderStyle());
			cellStyleForHeader.setBorderRight(ds.getBorderStyle());
			cellStyleForHeader.setBorderTop(ds.getBorderStyle());
			cellStyleForHeader.setAlignment(ds.getCellDataAlign());
			cellStyleForHeader.setFillForegroundColor(ds.getHSSFbackRoundColor());
			cellStyleForHeader.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
			cellStyleForHeader.setFillPattern(ds.getFillPattern());
			cellStyleForHeader.setFont(font);
//			sheet.autoSizeColumn((short)y);
			headerCell.setCellStyle(cellStyleForHeader);
		}
		
		int rownum = 1;
		HSSFCellStyle cellStyleForData;
		
		for (int j = 0; reportData!=null && j < reportData.size(); j++) {
			Row row = sheet.createRow(rownum);
			List<DataStyle> userData = reportData.get(j);
			for (int k = 0, l=0; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(l);
				DataStyle ds = (DataStyle)userData.get(k);
				
			//===start parvez date: 16-08-2022===	
				cell.setCellValue(ds.getStrData());	
			//===end parvez date: 16-08-2022===	
				cellStyleForData = workbook.createCellStyle();
				cellStyleForData.setBorderTop(ds.getBorderStyle());
				cellStyleForData.setBorderBottom(ds.getBorderStyle());
				cellStyleForData.setBorderLeft(ds.getBorderStyle());
				cellStyleForData.setBorderRight(ds.getBorderStyle());
				cellStyleForData.setAlignment(ds.getCellDataAlign());						
				
//				sheet.autoSizeColumn((short)l);
				cell.setCellStyle(cellStyleForData);
				
			}
			rownum++;
		}
	}
	
	
	
	
	
	public void createExcelMultiplicationTable(XSSFWorkbook workbook, XSSFSheet sheet, List<List<String>> header, List<List<List<String>>> reportData) throws IOException {
//        Workbook workbook = new HSSFWorkbook();
//        Sheet sheet = workbook.createSheet("multiplicationTable");
		Row headerRow = sheet.createRow(0);
		Font font = workbook.createFont();
		font.setBoldweight((short)1200);	
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);
		
		for(int i=0,y=0;i<header.size();i++,y++){
			Cell headerCell = headerRow.createCell(y);
			List<String> innList = header.get(i);
			
			headerCell.setCellValue(innList.get(0));
			XSSFCellStyle style = workbook.createCellStyle();
			if(innList.get(1) != null && !innList.get(1).equals("") && !innList.get(1).equals("#ffffff")) {
				XSSFColor color = new XSSFColor(Color.decode((innList.get(1) != null && !innList.get(1).equals("")) ? innList.get(1) : "#FFFFFF"));
				style.setFillBackgroundColor(color);
	            style.setFillPattern(CellStyle.BIG_SPOTS);
			}
			
//            style.setBorderBottom(CellStyle.BORDER_THIN);
//            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//            style.setBorderLeft(CellStyle.BORDER_THIN);
//            style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//            style.setBorderRight(CellStyle.BORDER_THIN);
//            style.setRightBorderColor(IndexedColors.BLACK.getIndex());
//            style.setBorderTop(CellStyle.BORDER_THIN);
//            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
            
			headerCell.setCellStyle(style);
		}
		
		int rownum = 1;
//		XSSFColor color1 = new XSSFColor(Color.decode("#b5b5b5"));
		for (int j = 0; reportData!=null && j < reportData.size(); j++) {
			Row row = sheet.createRow(rownum);
			List<List<String>> userData = reportData.get(j);
			for (int k = 0, l=0; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(l);
				List<String> innList = userData.get(k);
				cell.setCellValue(innList.get(0));	
				
				XSSFCellStyle style = workbook.createCellStyle();
				if(innList.get(1) != null && !innList.get(1).equals("") && !innList.get(1).equals("#ffffff")) {
					XSSFColor color = new XSSFColor(Color.decode((innList.get(1) != null && !innList.get(1).equals("")) ? innList.get(1) : "#FFFFFF"));
					style.setFillBackgroundColor(color);
		            style.setFillPattern(CellStyle.FINE_DOTS);
				}
				style.setAlignment(CellStyle.ALIGN_RIGHT);
//	            style.setBorderBottom(CellStyle.BORDER_THIN);
//	            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//	            style.setBorderLeft(CellStyle.BORDER_THIN);
//	            style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//	            style.setBorderRight(CellStyle.BORDER_THIN);
//	            style.setRightBorderColor(IndexedColors.BLACK.getIndex());
//	            style.setBorderTop(CellStyle.BORDER_THIN);
//	            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
	            
				cell.setCellStyle(style);
				sheet.autoSizeColumn(cell.getColumnIndex());
				/*if(l>1 && l<(userData.size()-7)) {
					sheet.setColumnWidth(l, 1500);
				}*/
			}
			rownum++;
		}
		
		
//		System.out.println("x ===>> " + x +" -- y ===>> " + y);
//        for (int i = 1; i <= x; i++) {
//            Row row = sheet.createRow(i - 1);
//
//            for (int j = 1; j <= y; j++) {
//                Cell cell = row.createCell(j - 1);
//                cell.setCellValue(i * j);
//
//                if (cell.getRowIndex() == 0 || cell.getColumnIndex() == 0) {
//                    CellStyle Style = workbook.createCellStyle();
//
//                    Style.setFillBackgroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
//                    Style.setFillPattern(CellStyle.BIG_SPOTS);
//                    Style.setBorderBottom(CellStyle.BORDER_THIN);
//                    Style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//                    Style.setBorderLeft(CellStyle.BORDER_THIN);
//                    Style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//                    Style.setBorderRight(CellStyle.BORDER_THIN);
//                    Style.setRightBorderColor(IndexedColors.BLACK.getIndex());
//                    Style.setBorderTop(CellStyle.BORDER_THIN);
//                    Style.setTopBorderColor(IndexedColors.BLACK.getIndex());
//
//                    cell.setCellStyle(Style);
//                } else {
//                    CellStyle Style = workbook.createCellStyle();
//
//                    Style.setBorderBottom(CellStyle.BORDER_THIN);
//                    Style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//                    Style.setBorderLeft(CellStyle.BORDER_THIN);
//                    Style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//                    Style.setBorderRight(CellStyle.BORDER_THIN);
//                    Style.setRightBorderColor(IndexedColors.BLACK.getIndex());
//                    Style.setBorderTop(CellStyle.BORDER_THIN);
//                    Style.setTopBorderColor(IndexedColors.BLACK.getIndex());
//
//                    cell.setCellStyle(Style);
//             }
//            }
//        }

//        FileOutputStream out = new FileOutputStream(fileName);
//        workbook.write(out);
//        out.close();
  }
	
	public void createExcelEmpBankDetailsTable(XSSFWorkbook workbook, XSSFSheet sheet, List<List<String>> reportData) throws IOException {
//      Workbook workbook = new HSSFWorkbook();
//      Sheet sheet = workbook.createSheet("multiplicationTable");
		Row headerRow = sheet.createRow(0);
		Font font = workbook.createFont();
		font.setBoldweight((short)1200);	
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);
		
		int rownum = 1;
//		XSSFColor color1 = new XSSFColor(Color.decode("#b5b5b5"));
		for (int j = 0; reportData!=null && j < reportData.size(); j++) {
			Row row = sheet.createRow(rownum);
			List<String> userData = reportData.get(j);
			for (int k = 0, l=0; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(l);
				cell.setCellValue(userData.get(k));	
			}
			rownum++;
		}
		
	}
	
	//===start parvez date: 25-01-2022===
	public void generateExcelSheetforMonthwiseCommitedReport(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<List<DataStyle>> reportData) {

		Row mainHeadingRow = sheet.createRow(1);
		Row subHeadingRow = sheet.createRow(8);
		Row subHeadingRow1 = sheet.createRow(9);
		Row subHeadingRow2 = sheet.createRow(10);
		
		Font reportHeadingFont = workbook.createFont();
		reportHeadingFont.setBoldweight((short)1200);	
		HSSFCellStyle cellStyleForMainHeadingRow = workbook.createCellStyle();
		cellStyleForMainHeadingRow.setAlignment(cellStyleForMainHeadingRow.ALIGN_CENTER);
		Cell reportHeading = mainHeadingRow.createCell((short)1);		
		reportHeading.setCellValue("");
		cellStyleForMainHeadingRow.setFont(reportHeadingFont);
		reportHeading.setCellStyle(cellStyleForMainHeadingRow);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("B2:U2"));
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);	
		
		HSSFCellStyle cellStyleForSummary = workbook.createCellStyle();
		cellStyleForSummary.setAlignment(cellStyleForSummary.ALIGN_CENTER);
		
		int x=3;
		int rowCnt = 2;
		for(int i=0;i<5;i++){
			Row row = sheet.createRow(rowCnt);
			
			DataStyle ds = (DataStyle)header.get(i);		
			Cell headerCell = row.createCell((short)1);		
			headerCell.setCellValue(ds.getStrData());
			cellStyleForSummary.setFont(reportNameFont);
			headerCell.setCellStyle(cellStyleForSummary);		
			sheet.addMergedRegion(CellRangeAddress.valueOf("B"+x+":"+"U"+x));
			x++;
			rowCnt++;
		}
		
		
		DataStyle ds5 = (DataStyle)header.get(5);		
		Cell totAssignment = subHeadingRow.createCell((short)3);		
		totAssignment.setCellValue(ds5.getStrData());
		totAssignment.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("D9:H9"));
		
		DataStyle ds6 = (DataStyle)header.get(6);		
		Cell plannedReceipt = subHeadingRow.createCell((short)8);		
		plannedReceipt.setCellValue(ds6.getStrData());
		plannedReceipt.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("I9:U9"));
		
		DataStyle ds7 = (DataStyle)header.get(7);		
		Cell SrNo = subHeadingRow1.createCell((short)1);		
		SrNo.setCellValue(ds7.getStrData());
		SrNo.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("B10:B10"));
		
		DataStyle ds8 = (DataStyle)header.get(8);		
		Cell division = subHeadingRow1.createCell((short)2);		
		division.setCellValue(ds8.getStrData());
		division.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("C10:C10"));
		
		DataStyle ds9 = (DataStyle)header.get(9);		
		Cell cellHead1 = subHeadingRow1.createCell((short)3);		
		cellHead1.setCellValue(ds9.getStrData());
		cellHead1.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("D10:D10"));
		
		DataStyle ds10 = (DataStyle)header.get(10);		
		Cell cellHead2 = subHeadingRow1.createCell((short)4);		
		cellHead2.setCellValue(ds10.getStrData());
		cellHead2.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("E10:F10"));
		
		DataStyle ds11 = (DataStyle)header.get(11);		
		Cell cellHead3 = subHeadingRow1.createCell((short)6);		
		cellHead3.setCellValue(ds11.getStrData());
		cellHead3.setCellStyle(cellStyleForSummary);		
		sheet.addMergedRegion(CellRangeAddress.valueOf("G10:H10"));
		
		for(int j=12, p=3; j<header.size(); j++,p++){
			DataStyle ds1 = (DataStyle)header.get(j);		
			Cell cellHead5 = subHeadingRow2.createCell(p);		
			cellHead5.setCellValue(ds1.getStrData());
			cellHead5.setCellStyle(cellStyleForSummary);
		}
		
		
		int rownum = 11;
		HSSFCellStyle cellStyleForData;
		
		for (int j = 0; reportData!=null && j < reportData.size(); j++) {
			Row row = sheet.createRow(rownum);
			List<DataStyle> userData = reportData.get(j);
			for (int k = 0, l=1; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(l);
				DataStyle dstyle = (DataStyle)userData.get(k);
				
			//===start parvez date: 16-08-2022===	
				cell.setCellValue(dstyle.getStrData());	
			//===end parvez date: 16-08-2022===	
				cellStyleForData = workbook.createCellStyle();
				cellStyleForData.setBorderTop(dstyle.getBorderStyle());
				cellStyleForData.setBorderBottom(dstyle.getBorderStyle());
				cellStyleForData.setBorderLeft(dstyle.getBorderStyle());
				cellStyleForData.setBorderRight(dstyle.getBorderStyle());
				cellStyleForData.setAlignment(dstyle.getCellDataAlign());						
				
//				sheet.autoSizeColumn((short)l);
				cell.setCellStyle(cellStyleForData);
			}
			rownum++;
		}
		
		
	}
//===end parvez date: 25-01-2022

//===start parvez date: 05-02-2022===
	public void generateExcelSheetforCumulativePerformanceReport(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> mainHeader, List<DataStyle> header,List<DataStyle> subHeader, List<DataStyle> rowHeader,List<List<DataStyle>> reportData) {
		
		sheet.createFreezePane(0, 7);
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);
		
		HSSFCellStyle cellStyleForMainHeader = workbook.createCellStyle();
		cellStyleForMainHeader.setAlignment(cellStyleForMainHeader.ALIGN_LEFT);
		
		int subHeaderSize = subHeader.size();
		int headerSize = header.size()*subHeaderSize+1;
		int rowCnt = 2;
		for(int i=0; i<mainHeader.size(); i++){
			Row row = sheet.createRow(rowCnt);
			
			DataStyle ds = (DataStyle)mainHeader.get(i);		
			Cell headerCell = row.createCell((short)1);		
			headerCell.setCellValue(ds.getStrData());
			cellStyleForMainHeader.setFont(reportNameFont);
			headerCell.setCellStyle(cellStyleForMainHeader);
			sheet.addMergedRegion(new CellRangeAddress(rowCnt,rowCnt,(short)1,headerSize));
			rowCnt++;
		}
		
		Row headerRow = sheet.createRow(rowCnt);
		Row subHeaderRow = sheet.createRow(rowCnt+1);
		HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
		cellStyleForHeader.setAlignment(cellStyleForHeader.ALIGN_CENTER);
		
//		DataStyle dsHead = (DataStyle)header.get(0);		
		Cell cellHead1 = headerRow.createCell((short)1);
		cellHead1.setCellValue("PARTICULARS");
		cellStyleForHeader.setFont(reportNameFont);
		cellHead1.setCellStyle(cellStyleForHeader);		
		sheet.addMergedRegion(new CellRangeAddress(rowCnt,rowCnt+1,1,1));
		
		int startcolum = 2;
		int endcolum = 4;
		int y=2;
		
		for(int j=0; j<header.size(); j++){
//			System.out.println("startcolum="+startcolum);
			DataStyle ds = (DataStyle)header.get(j);		
			Cell headerCell = headerRow.createCell(startcolum);		
			headerCell.setCellValue(ds.getStrData());
			cellStyleForHeader.setFont(reportNameFont);
			headerCell.setCellStyle(cellStyleForHeader);
			sheet.addMergedRegion(new CellRangeAddress(rowCnt,rowCnt,startcolum,endcolum));
			startcolum = endcolum+1;
			endcolum  = endcolum+3;
			
			
			for(int k=0; k<subHeader.size(); k++){
				DataStyle ds1 = (DataStyle)subHeader.get(k);
				Cell subHeaderCell = subHeaderRow.createCell(y);
				subHeaderCell.setCellValue(ds1.getStrData());
				cellStyleForHeader.setFont(reportNameFont);
				subHeaderCell.setCellStyle(cellStyleForHeader);
				y++;
			}
		}
		
		
		Row receiptRow = sheet.createRow(7);
		
		Cell cellRowHead1 = receiptRow.createCell((short)1);		
		cellRowHead1.setCellValue("Receipts");
		cellStyleForMainHeader.setFont(reportNameFont);
		cellRowHead1.setCellStyle(cellStyleForMainHeader);		
		
		int rowCnt1 = 8;
		HSSFCellStyle cellStyleForRowHeadData;
		/*for(int l=0; l<2; l++){
			Row row = sheet.createRow(rowCnt1);
			DataStyle dstyle = (DataStyle)rowHeader.get(l);		
			Cell rowHeaderCell = row.createCell((short)1);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());
			rowHeaderCell.setCellStyle(cellStyleForRowHeadData);
			rowCnt1++;
		}*/
		
		for(int i=0; i<2; i++){
			List<DataStyle> listPE = reportData.get(i);
			Row rowPE = sheet.createRow(rowCnt1);
			for(int j=0, x=1; j<listPE.size(); j++, x++){
				DataStyle dstyle = (DataStyle)listPE.get(j);		
				Cell rowHeaderCell = rowPE.createCell(x);		
				rowHeaderCell.setCellValue(dstyle.getStrData());
				cellStyleForRowHeadData = workbook.createCellStyle();
				cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());
				rowHeaderCell.setCellStyle(cellStyleForRowHeadData);
			}
			rowCnt1++;
		}
		
		
		rowCnt1 +=1;
		List<DataStyle> listTotRecp = reportData.get(2);
		HSSFCellStyle cellStyleForRowDataHead1;
		Row totReceiptRow = sheet.createRow(rowCnt1);
		
		for(int j=0, x=1; j<listTotRecp.size(); j++, x++){
			DataStyle dstyle = (DataStyle)listTotRecp.get(j);		
			Cell rowHeaderCell = totReceiptRow.createCell(x);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowDataHead1 = workbook.createCellStyle();
			cellStyleForRowDataHead1.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowDataHead1.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowDataHead1.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowDataHead1.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowDataHead1.setAlignment(dstyle.getCellDataAlign());
			cellStyleForRowDataHead1.setFont(reportNameFont);	
			rowHeaderCell.setCellStyle(cellStyleForRowDataHead1);
		}
		
		rowCnt1 +=2;
		Row directExpensesRow = sheet.createRow(rowCnt1);
		Cell cellDirectExpenses = directExpensesRow.createCell((short)1);		
		cellDirectExpenses.setCellValue("Direct Expenses");
		cellStyleForMainHeader.setFont(reportNameFont);
		cellDirectExpenses.setCellStyle(cellStyleForMainHeader);
		
		rowCnt1 +=2;
		
		HSSFCellStyle cellStyleForColHead = workbook.createCellStyle();
		Row rowPR = sheet.createRow(rowCnt1);
		Cell cellColPR = rowPR.createCell((short)1);		
		cellColPR.setCellValue("a) Partners related");
		cellColPR.setCellStyle(cellStyleForColHead);
		rowCnt1 +=2;
		
		List<DataStyle> listEmpCost = reportData.get(3);
		Row rowEmpCost = sheet.createRow(rowCnt1);
		for(int j=0, x=1; j<listEmpCost.size(); j++, x++){
			DataStyle dstyle = (DataStyle)listEmpCost.get(j);		
			Cell rowHeaderCell = rowEmpCost.createCell(x);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());
			rowHeaderCell.setCellStyle(cellStyleForRowHeadData);
		}
		rowCnt1 +=2;
		
		List<DataStyle> listTNC = reportData.get(5);
		Row rowTNC = sheet.createRow(rowCnt1);
		for(int j=0, x=1; j<listTNC.size(); j++, x++){
			DataStyle dstyle = (DataStyle)listTNC.get(j);		
			Cell rowHeaderCell = rowTNC.createCell(x);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());
			rowHeaderCell.setCellStyle(cellStyleForRowHeadData);
		}
		rowCnt1 +=2;
		
		List<DataStyle> listTC = reportData.get(4);
		Row rowTC = sheet.createRow(rowCnt1);
		for(int j=0, x=1; j<listTC.size(); j++, x++){
			DataStyle dstyle = (DataStyle)listTC.get(j);		
			Cell rowHeaderCell = rowTC.createCell(x);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());
			rowHeaderCell.setCellStyle(cellStyleForRowHeadData);
		}
		rowCnt1 +=2;
		
		
		List<DataStyle> listTotDE = reportData.get(6);
		Row totRow = sheet.createRow(rowCnt1);
		for(int j=0, x=1; j<listTotDE.size(); j++, x++){
			DataStyle dstyle = (DataStyle)listTotDE.get(j);		
			Cell rowHeaderCell = totRow.createCell(x);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowDataHead1 = workbook.createCellStyle();
			cellStyleForRowDataHead1.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowDataHead1.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowDataHead1.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowDataHead1.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowDataHead1.setAlignment(dstyle.getCellDataAlign());
			cellStyleForRowDataHead1.setFont(reportNameFont);
			rowHeaderCell.setCellStyle(cellStyleForRowDataHead1);
		}
		
		rowCnt1 +=1;
		/*Row contributionRow = sheet.createRow(rowCnt1);
		Cell cellContribution = contributionRow.createCell((short)1);		
		cellContribution.setCellValue("Contribution");
		cellStyleForMainHeader.setFont(reportNameFont);
		cellContribution.setCellStyle(cellStyleForMainHeader);*/
		
		List<DataStyle> listContribution = reportData.get(7);
		Row contributionRow = sheet.createRow(rowCnt1);
		for(int j=0, x=1; j<listContribution.size(); j++, x++){
			DataStyle dstyle = (DataStyle)listContribution.get(j);		
			Cell rowHeaderCell = contributionRow.createCell(x);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowDataHead1 = workbook.createCellStyle();
			cellStyleForRowDataHead1.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowDataHead1.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowDataHead1.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowDataHead1.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowDataHead1.setAlignment(dstyle.getCellDataAlign());
			cellStyleForRowDataHead1.setFont(reportNameFont);
			rowHeaderCell.setCellStyle(cellStyleForRowDataHead1);
		}
		
		rowCnt1 +=2;
		Row indirectExpRow = sheet.createRow(rowCnt1);
		Cell cellIndirectExp = indirectExpRow.createCell((short)1);		
		cellIndirectExp.setCellValue("Indirect Expenses");
		cellStyleForMainHeader.setFont(reportNameFont);
		cellIndirectExp.setCellStyle(cellStyleForMainHeader);
		
		rowCnt1 +=2;
		/*for(int k1=6; k1<9; k1++){
			Row row = sheet.createRow(rowCnt1);
			DataStyle dstyle = (DataStyle)rowHeader.get(k1);		
			Cell rowHeaderCell = row.createCell((short)1);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());
			rowHeaderCell.setCellStyle(cellStyleForRowHeadData);
			rowCnt1++;
		}*/
		
		Row rowIT = sheet.createRow(rowCnt1);
		Cell cellColIT = rowIT.createCell((short)1);		
		cellColIT.setCellValue("e) I.T. 7% on Receipt");
		cellColIT.setCellStyle(cellStyleForColHead);
		rowCnt1 +=2;
		
		Row rowFExp = sheet.createRow(rowCnt1);
		Cell cellColFExp = rowFExp.createCell((short)1);		
		cellColFExp.setCellValue("f) Facilities exp");
		cellColFExp.setCellStyle(cellStyleForColHead);
		rowCnt1 +=2;
		
		Row rowAD = sheet.createRow(rowCnt1);
		Cell cellColAD = rowAD.createCell((short)1);		
		cellColAD.setCellValue("g) Admin & Office");
		cellColAD.setCellStyle(cellStyleForColHead);
		
		
		rowCnt1 +=2;
		Row totIndirectExpRow = sheet.createRow(rowCnt1);
		Cell cellTotIndirectExp = totIndirectExpRow.createCell((short)1);		
		cellTotIndirectExp.setCellValue("Total Indirect Expenses");
		cellStyleForMainHeader.setFont(reportNameFont);
		cellTotIndirectExp.setCellStyle(cellStyleForMainHeader);
		
		rowCnt1 +=2;
		Row netSurplusRow = sheet.createRow(rowCnt1);
		Cell cellnetSurplus = netSurplusRow.createCell((short)1);		
		cellnetSurplus.setCellValue("NET SURPLUS");
		cellStyleForMainHeader.setFont(reportNameFont);
		cellnetSurplus.setCellStyle(cellStyleForMainHeader);
		
	}
	
	
	public void generateExcelSheetforPartnerwiseMISReport(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<DataStyle> rowHeader, List<List<DataStyle>> reportData) {
		
//		int headerSize = header.size();
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);
		
		Row headerRow = sheet.createRow(1);
		HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
		cellStyleForHeader.setAlignment(cellStyleForHeader.ALIGN_CENTER);
			
		Cell cellHead = headerRow.createCell((short)0);
		cellHead.setCellValue("");
		cellStyleForHeader.setFont(reportNameFont);
		cellHead.setCellStyle(cellStyleForHeader);		
//		sheet.addMergedRegion(new CellRangeAddress(0,1,0,0));
		
		Cell cellHead1 = headerRow.createCell((short)1);
		cellHead1.setCellValue("Partner wise MIS till date");
		cellStyleForHeader.setFont(reportNameFont);
		cellHead1.setCellStyle(cellStyleForHeader);		
		sheet.addMergedRegion(new CellRangeAddress(1,1,1,header.size()));
		
		Row subHeaderRow = sheet.createRow((short)2);
			/*
		Cell cellHeadTot = subHeaderRow.createCell((short)1);
		cellHeadTot.setCellValue("Total KPCA");
		cellStyleForHeader.setFont(reportNameFont);
		cellHeadTot.setCellStyle(cellStyleForHeader);*/
		
		for(int i=0, y=1; i<header.size(); i++, y++){	
			DataStyle ds = (DataStyle)header.get(i);		
			Cell headerCell = subHeaderRow.createCell(y);		
			headerCell.setCellValue(ds.getStrData());
			cellStyleForHeader.setFont(reportNameFont);
			headerCell.setCellStyle(cellStyleForHeader);
		}
		
		HSSFCellStyle cellStyleForColHead = workbook.createCellStyle();
		cellStyleForColHead.setAlignment(cellStyleForColHead.ALIGN_LEFT);
		Row anualComitRow = sheet.createRow((short)3);
		Cell cellColHead = anualComitRow.createCell((short)0);		
		cellColHead.setCellValue("Annual Committed");
		cellColHead.setCellStyle(cellStyleForColHead);
		
		Row receiptRow = sheet.createRow((short)4);
		Cell cellReceiptHead = receiptRow.createCell((short)0);		
		cellReceiptHead.setCellValue("Receipts");
		cellReceiptHead.setCellStyle(cellStyleForColHead);
		
		/*HSSFCellStyle cellStyleForRowHeadData;
		int rowCnt = 5;
		for(int j=0; j<=2;j++){
			Row row = sheet.createRow(rowCnt);
			DataStyle dstyle = (DataStyle)rowHeader.get(j);		
			Cell rowHeaderCell = row.createCell((short)0);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());
			rowHeaderCell.setCellStyle(cellStyleForRowHeadData);
			
			rowCnt++;
		}*/
		
		
		HSSFCellStyle cellStyleForRowHeadData;
		
		List<DataStyle> listPE = reportData.get(1);
		Row rowPE = sheet.createRow((short)5);
		for(int j=0, x=0; j<listPE.size(); j++, x++){
			DataStyle dstyle = (DataStyle)listPE.get(j);		
			Cell rowHeaderCell = rowPE.createCell(x);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());
			rowHeaderCell.setCellStyle(cellStyleForRowHeadData);
		}
		
		Row rowIFB = sheet.createRow((short)6);
		Cell cellColIFB = rowIFB.createCell((short)0);		
		cellColIFB.setCellValue("b) Inter Firm Billing");
		cellColIFB.setCellStyle(cellStyleForColHead);
		
		List<DataStyle> listOPE = reportData.get(4);
		Row rowOPE = sheet.createRow((short)7);
		for(int j=0, x=0; j<listOPE.size(); j++, x++){
			DataStyle dstyle = (DataStyle)listOPE.get(j);		
			Cell rowHeaderCell = rowOPE.createCell(x);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());
			rowHeaderCell.setCellStyle(cellStyleForRowHeadData);
		}
		
		
		HSSFCellStyle cellStyleForColHead1 = workbook.createCellStyle();
		
		List<DataStyle> listTotRec = reportData.get(5);
		Row rowTotRec = sheet.createRow((short)8);
		for(int j=0, x=0; j<listTotRec.size(); j++, x++){
			DataStyle dstyle = (DataStyle)listTotRec.get(j);		
			Cell rowHeaderCell = rowTotRec.createCell(x);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForColHead1 = workbook.createCellStyle();
			cellStyleForColHead1.setBorderTop(dstyle.getBorderStyle());
			cellStyleForColHead1.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForColHead1.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForColHead1.setBorderRight(dstyle.getBorderStyle());
			cellStyleForColHead1.setAlignment(dstyle.getCellDataAlign());
			cellStyleForColHead1.setFont(reportNameFont);
			rowHeaderCell.setCellStyle(cellStyleForColHead1);
		}
		
//		rowCnt += 2;
		Row directExpensesRow = sheet.createRow((short)10);
		Cell cellDirectExpenses = directExpensesRow.createCell((short)0);		
		cellDirectExpenses.setCellValue("Direct Expenses");
		cellStyleForColHead1.setFont(reportNameFont);
		cellDirectExpenses.setCellStyle(cellStyleForColHead1);
		
		
		List<DataStyle> listEmpCost = reportData.get(0);
		Row rowEmpC = sheet.createRow((short)12);
		for(int j=0, x=0; j<listEmpCost.size(); j++, x++){
			DataStyle dstyle = (DataStyle)listEmpCost.get(j);		
			Cell rowHeaderCell = rowEmpC.createCell(x);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());
			rowHeaderCell.setCellStyle(cellStyleForRowHeadData);
		}
		
		Row rowPF = sheet.createRow((short)13);
		Cell cellColPF = rowPF.createCell((short)0);		
		cellColPF.setCellValue("a) Professional Fees");
		cellColPF.setCellStyle(cellStyleForColHead);
		
		List<DataStyle> listTC = reportData.get(2);
		Row rowTC = sheet.createRow((short)14);
		for(int j=0, x=0; j<listTC.size(); j++, x++){
			DataStyle dstyle = (DataStyle)listTC.get(j);		
			Cell rowHeaderCell = rowTC.createCell(x);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());
			rowHeaderCell.setCellStyle(cellStyleForRowHeadData);
		}
		
		List<DataStyle> listTNC = reportData.get(3);
		Row rowTNC = sheet.createRow((short)15);
		for(int j=0, x=0; j<listTNC.size(); j++, x++){
			DataStyle dstyle = (DataStyle)listTNC.get(j);		
			Cell rowHeaderCell = rowTNC.createCell(x);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());
			rowHeaderCell.setCellStyle(cellStyleForRowHeadData);
		}
		
		Row rowOP = sheet.createRow((short)16);
		Cell cellColOP = rowOP.createCell((short)0);		
		cellColOP.setCellValue("b) Out Of Pocket");
		cellColOP.setCellStyle(cellStyleForColHead);
		
		int rowCnt = 17;
		Row rowIFB1 = sheet.createRow(rowCnt);
		Cell cellColIFB1 = rowIFB1.createCell((short)0);		
		cellColIFB1.setCellValue("f) Inter Firm Billing");
		cellColIFB1.setCellStyle(cellStyleForColHead);
		
		rowCnt +=1;
		
		List<DataStyle> listTotDE = reportData.get(6);
		Row totDirectExpRow = sheet.createRow(rowCnt);
		for(int j=0, x=0; j<listTotDE.size(); j++, x++){
			DataStyle dstyle = (DataStyle)listTotDE.get(j);		
			Cell rowHeaderCell = totDirectExpRow.createCell(x);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForColHead1 = workbook.createCellStyle();
			cellStyleForColHead1.setBorderTop(dstyle.getBorderStyle());
			cellStyleForColHead1.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForColHead1.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForColHead1.setBorderRight(dstyle.getBorderStyle());
			cellStyleForColHead1.setAlignment(dstyle.getCellDataAlign());
			cellStyleForColHead1.setFont(reportNameFont);
			rowHeaderCell.setCellStyle(cellStyleForColHead1);
		}
		
		rowCnt +=3;
		Row contributionRow = sheet.createRow(rowCnt);
		Cell cellContribution = contributionRow.createCell((short)0);		
		cellContribution.setCellValue("Contribution");
		cellStyleForColHead1.setFont(reportNameFont);
		cellContribution.setCellStyle(cellStyleForColHead1);
		
		rowCnt +=3;
		Row partnerCostRow = sheet.createRow(rowCnt);
		Cell cellPartnerCostHead = partnerCostRow.createCell((short)0);		
		cellPartnerCostHead.setCellValue("Partner Cost");
		cellPartnerCostHead.setCellStyle(cellStyleForColHead);
		
		rowCnt +=3;
		Row netContributionRow = sheet.createRow(rowCnt);
		Cell cellNetContribution = netContributionRow.createCell((short)0);		
		cellNetContribution.setCellValue("Net Contribution ");
		cellStyleForColHead1.setFont(reportNameFont);
		cellNetContribution.setCellStyle(cellStyleForColHead1);
		
		rowCnt +=3;
		Row indirectExpRow = sheet.createRow(rowCnt);
		Cell cellIndirectExpHead = indirectExpRow.createCell((short)0);		
		cellIndirectExpHead.setCellValue("Indirect Expenses");
		cellIndirectExpHead.setCellStyle(cellStyleForColHead);
		
		rowCnt +=2;
		Row netSurplusRow = sheet.createRow(rowCnt);
		Cell cellnetSurplus = netSurplusRow.createCell((short)0);		
		cellnetSurplus.setCellValue("NET SURPLUS");
		cellStyleForColHead1.setFont(reportNameFont);
		cellnetSurplus.setCellStyle(cellStyleForColHead1);
		
	}
	
	public void generateExcelSheetforPartnerwiseReceipt(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<DataStyle> subHeader, List<DataStyle> rowHeader, List<List<DataStyle>> reportData) {
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);
		
		sheet.createFreezePane(2, 0);
		
		Row headerRow = sheet.createRow(0);
		Row subHeaderRow = sheet.createRow(1);
		HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
		cellStyleForHeader.setAlignment(cellStyleForHeader.ALIGN_CENTER);
		
		int startcolum = 2;
		int endcolum = 4;
		int y=2;
		
		for(int j=0; j<header.size(); j++){
			DataStyle ds = (DataStyle)header.get(j);		
			Cell headerCell = headerRow.createCell(startcolum);		
			headerCell.setCellValue(ds.getStrData());
			cellStyleForHeader.setFont(reportNameFont);
			headerCell.setCellStyle(cellStyleForHeader);
			sheet.addMergedRegion(new CellRangeAddress(0,0,startcolum,endcolum));
			startcolum = endcolum+1;
			endcolum  = endcolum+3;
			
		}
		
		HSSFCellStyle cellStyleForSubHeader = workbook.createCellStyle();
		cellStyleForSubHeader.setAlignment(cellStyleForSubHeader.ALIGN_LEFT);
		
		Cell cellHead1 = subHeaderRow.createCell((short)1);
		cellHead1.setCellValue("PARTICULARS");
		cellStyleForSubHeader.setFont(reportNameFont);
		cellHead1.setCellStyle(cellStyleForSubHeader);		
		
		for(int i=0; i<header.size(); i++){
			for(int k=0; k<subHeader.size(); k++){
				DataStyle ds1 = (DataStyle)subHeader.get(k);
				Cell subHeaderCell = subHeaderRow.createCell(y);
				subHeaderCell.setCellValue(ds1.getStrData());
				cellStyleForHeader.setFont(reportNameFont);
				subHeaderCell.setCellStyle(cellStyleForHeader);
				y++;
			}
		}
		
		HSSFCellStyle cellStyleForRowHeadData;
		int rowCnt = 3;
		/*for(int k=0; k<rowHeader.size(); k++){
			Row row = sheet.createRow(rowCnt);
			DataStyle dstyle = (DataStyle)rowHeader.get(k);		
			Cell rowHeaderCell = row.createCell((short)1);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());
			rowHeaderCell.setCellStyle(cellStyleForRowHeadData);	
			rowCnt++;
		}*/
		
		for (int j = 0; reportData!=null && j < reportData.size()-1; j++) {
			Row row = sheet.createRow(rowCnt);
			List<DataStyle> userData = reportData.get(j);
			for (int k = 0, l=1; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(l);
				DataStyle dstyle = (DataStyle)userData.get(k);
				
				cell.setCellValue(" "+dstyle.getStrData()+" ");	
				cellStyleForRowHeadData = workbook.createCellStyle();
				cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());						
				
				cell.setCellStyle(cellStyleForRowHeadData);
			}
			rowCnt++;
		}
		
		Row totRow = sheet.createRow(rowCnt+1);
		Cell celltotRowHead = totRow.createCell((short)1);		
		celltotRowHead.setCellValue("TOTAL");
		cellStyleForHeader.setFont(reportNameFont);
		celltotRowHead.setCellStyle(cellStyleForHeader);
		
		int reportDataSize = reportData.size()-1;
		List<DataStyle> userData1 = reportData.get(reportDataSize);
		for (int k1 = 0, z=2; k1 < userData1.size(); k1++,z++) {
			Cell cell = totRow.createCell(z);
			DataStyle dstyle = (DataStyle)userData1.get(k1);
			
		//===start parvez date: 16-08-2022===	
			cell.setCellValue(dstyle.getStrData());	
		//===end parvez date: 16-08-2022===	
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());						
			cellStyleForRowHeadData.setFont(reportNameFont);
			cell.setCellStyle(cellStyleForRowHeadData);
		}
		
	}
	
	public void generateExcelSheetforDirectcostWorkingReport(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<DataStyle> subHeader, List<List<DataStyle>> reportData) {
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);
		
		sheet.createFreezePane(7, 0);
		
		Row headerRow = sheet.createRow(0);
		Row subHeaderRow = sheet.createRow(1);
		HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
		cellStyleForHeader.setAlignment(cellStyleForHeader.ALIGN_CENTER);
		
		int startcolum = 2;
		int endcolum = 6;
		int y=2;
		
		for(int j=0; j<header.size(); j++){
			DataStyle ds = (DataStyle)header.get(j);		
			Cell headerCell = headerRow.createCell(startcolum);		
			headerCell.setCellValue(ds.getStrData());
			cellStyleForHeader.setFont(reportNameFont);
			headerCell.setCellStyle(cellStyleForHeader);
			sheet.addMergedRegion(new CellRangeAddress(0,0,startcolum,endcolum));
			startcolum = endcolum+1;
			endcolum  = endcolum+5;
			
		}
		
		HSSFCellStyle cellStyleForSubHeader = workbook.createCellStyle();
		cellStyleForSubHeader.setAlignment(cellStyleForSubHeader.ALIGN_LEFT);
		
		Cell cellHead1 = subHeaderRow.createCell((short)1);
		cellHead1.setCellValue("PARTICULARS");
		cellStyleForSubHeader.setFont(reportNameFont);
		cellHead1.setCellStyle(cellStyleForSubHeader);		
		
		for(int i=0; i<header.size(); i++){
			for(int k=0; k<subHeader.size(); k++){
				DataStyle ds1 = (DataStyle)subHeader.get(k);
				Cell subHeaderCell = subHeaderRow.createCell(y);
				subHeaderCell.setCellValue(ds1.getStrData());
				cellStyleForHeader.setFont(reportNameFont);
				subHeaderCell.setCellStyle(cellStyleForHeader);
				y++;
			}
		}
		
		HSSFCellStyle cellStyleForData;
		int rowCnt = 3;
		
		
		for (int j = 0; reportData!=null && j < reportData.size()-1; j++) {
			Row row = sheet.createRow(rowCnt);
			List<DataStyle> userData = reportData.get(j);
			for (int k = 0, l=1; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(l);
				DataStyle dstyle = (DataStyle)userData.get(k);
				
			//===start parvez date: 16-08-2022===	
				cell.setCellValue(dstyle.getStrData());	
			//===end parvez date: 16-08-2022===	
				cellStyleForData = workbook.createCellStyle();
				cellStyleForData.setBorderTop(dstyle.getBorderStyle());
				cellStyleForData.setBorderBottom(dstyle.getBorderStyle());
				cellStyleForData.setBorderLeft(dstyle.getBorderStyle());
				cellStyleForData.setBorderRight(dstyle.getBorderStyle());
				cellStyleForData.setAlignment(dstyle.getCellDataAlign());						
				
				cell.setCellStyle(cellStyleForData);
			}
			rowCnt++;
		}
		
		Row totRow = sheet.createRow(rowCnt+1);
		Cell celltotRowHead = totRow.createCell((short)1);		
		celltotRowHead.setCellValue("TOTAL");
		cellStyleForHeader.setFont(reportNameFont);
		celltotRowHead.setCellStyle(cellStyleForHeader);
		
		int reportDataSize = reportData.size()-1;
		List<DataStyle> userData1 = reportData.get(reportDataSize);
		for (int k1 = 0, z=2; k1 < userData1.size(); k1++,z++) {
			Cell cell = totRow.createCell(z);
			DataStyle dstyle = (DataStyle)userData1.get(k1);
			
		//===start parvez date: 16-08-2022===	
			cell.setCellValue(dstyle.getStrData());	
		//===end parvez date: 16-08-2022===	
			cellStyleForData = workbook.createCellStyle();
			cellStyleForData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForData.setAlignment(dstyle.getCellDataAlign());						
			
			cell.setCellStyle(cellStyleForData);
		}
		
	}
	
	public void generateExcelSheetforPartnerwiseCommitmentReport(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<DataStyle> rowHeader,List<List<DataStyle>> reportData,String currency) {
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);
		
		sheet.createFreezePane(0, 3);
//		sheet.autoSizeColumn(1000);
		
		Row headerRow = sheet.createRow(0);
		Row subHeaderRow = sheet.createRow(1);
		HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
		cellStyleForHeader.setAlignment(cellStyleForHeader.ALIGN_CENTER);
		
		Cell cellHead = subHeaderRow.createCell((short)1);
		cellHead.setCellValue("");
		cellStyleForHeader.setFont(reportNameFont);
		cellHead.setCellStyle(cellStyleForHeader);
		sheet.addMergedRegion(CellRangeAddress.valueOf("C1:D1"));
		
		for(int j=0,x=4 ; j<header.size(); j++,x++){
			DataStyle ds = (DataStyle)header.get(j);		
			Cell headerCell = headerRow.createCell(x);		
			headerCell.setCellValue(ds.getStrData());
			cellStyleForHeader.setFont(reportNameFont);
			headerCell.setCellStyle(cellStyleForHeader);
//			cellStyleForHeader.setWrapText(true);
		}
		
		HSSFCellStyle cellStyleForSubHeader = workbook.createCellStyle();
		cellStyleForSubHeader.setAlignment(cellStyleForSubHeader.ALIGN_LEFT);
		
		Cell cellHead1 = subHeaderRow.createCell((short)1);
		cellHead1.setCellValue("PARTICULARS");
		cellStyleForSubHeader.setFont(reportNameFont);
		cellHead1.setCellStyle(cellStyleForSubHeader);
//		cellStyleForSubHeader.setWrapText(true);
		
		Cell cellHead2 = subHeaderRow.createCell((short)3);
		cellHead2.setCellValue("Total Commitment ("+currency+")");
		cellStyleForSubHeader.setFont(reportNameFont);
		cellHead2.setCellStyle(cellStyleForSubHeader);
		
		
		for(int i=0 ,y=4; i<header.size(); i++, y++){
			Cell subHeaderCell = subHeaderRow.createCell(y);
			subHeaderCell.setCellValue("Commitment ("+currency+")");
			cellStyleForHeader.setFont(reportNameFont);
			subHeaderCell.setCellStyle(cellStyleForHeader);
		}
		
		Row rowCommit = sheet.createRow((short)2);
		Cell cellOpeningCommit = rowCommit.createCell((short)1);
		cellOpeningCommit.setCellValue("Opening Commitment");
		cellStyleForSubHeader.setFont(reportNameFont);
		cellOpeningCommit.setCellStyle(cellStyleForSubHeader);
		
		HSSFCellStyle cellStyleForRowHeadData;
		/*int rowCnt = 4;
		for(int k=0; k<rowHeader.size(); k++){
			Row row = sheet.createRow(rowCnt);
			DataStyle dstyle = (DataStyle)rowHeader.get(k);		
			Cell rowHeaderCell = row.createCell((short)1);		
			rowHeaderCell.setCellValue(dstyle.getStrData());
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());
			rowHeaderCell.setCellStyle(cellStyleForRowHeadData);	
			rowCnt++;
		}*/
		
		HSSFCellStyle cellStyleForData;
		int rownum = 4;
		
		for (int j = 0; reportData!=null && j < reportData.size()-1; j++) {
			Row row = sheet.createRow(rownum);
			List<DataStyle> userData = reportData.get(j);
			for (int k = 0, l=1; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(l);
				DataStyle dstyle = (DataStyle)userData.get(k);
				
			//===start parvez date: 16-08-2022===	
				cell.setCellValue(dstyle.getStrData());	
			//===end parvez date: 16-08-2022===	
				cellStyleForData = workbook.createCellStyle();
				cellStyleForData.setBorderTop(dstyle.getBorderStyle());
				cellStyleForData.setBorderBottom(dstyle.getBorderStyle());
				cellStyleForData.setBorderLeft(dstyle.getBorderStyle());
				cellStyleForData.setBorderRight(dstyle.getBorderStyle());
				cellStyleForData.setAlignment(dstyle.getCellDataAlign());						
				
				cell.setCellStyle(cellStyleForData);
			}
			rownum++;
		}
		
//		Row totRow = sheet.createRow(rowCnt+1);
		Row totRow = sheet.createRow(rownum+1);
		Cell celltotRowHead = totRow.createCell((short)1);		
		celltotRowHead.setCellValue("TOTAL");
		cellStyleForSubHeader.setFont(reportNameFont);
		celltotRowHead.setCellStyle(cellStyleForSubHeader);
		
		int reportDataSize = reportData.size()-1;
		List<DataStyle> userData1 = reportData.get(reportDataSize);
		for (int k1 = 0, z=3; k1 < userData1.size(); k1++,z++) {
			Cell cell = totRow.createCell(z);
			DataStyle dstyle = (DataStyle)userData1.get(k1);
			
		//===start parvez date: 16-08-2022===	
			cell.setCellValue(dstyle.getStrData());	
		//===start parvez date: 16-08-2022===	
			cellStyleForRowHeadData = workbook.createCellStyle();
			cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
			cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());						
			cellStyleForRowHeadData.setFont(reportNameFont);
			cell.setCellStyle(cellStyleForRowHeadData);
		}
	}
//===end parvez date: 05-02-2022===	
	
	public void generateExcelSheetforLocwiseBudgetReport(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<List<DataStyle>> reportData) {
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);
		
		Row headerRow = sheet.createRow((short)2);
		HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
		cellStyleForHeader.setAlignment(cellStyleForHeader.ALIGN_CENTER);
		
		for(int i=0, y=1; i<header.size(); i++, y++){	
			DataStyle ds = (DataStyle)header.get(i);		
			Cell headerCell = headerRow.createCell(y);		
			headerCell.setCellValue(ds.getStrData());
			cellStyleForHeader.setFont(reportNameFont);
			headerCell.setCellStyle(cellStyleForHeader);
		}
		
		int rowCnt = 3;
		HSSFCellStyle cellStyleForRowHeadData;
		
		for (int j = 0; reportData!=null && j < reportData.size(); j++) {
			Row row = sheet.createRow(rowCnt);
			List<DataStyle> userData = reportData.get(j);
			for (int k = 0, l=1; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(l);
				DataStyle dstyle = (DataStyle)userData.get(k);
				
				cell.setCellValue(" "+dstyle.getStrData()+" ");	
				
				cellStyleForRowHeadData = workbook.createCellStyle();
				cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());						
				
				cell.setCellStyle(cellStyleForRowHeadData);
			}
			rowCnt++;
		}
		
	}
	
	public void generateExcelSheetforClusterwiseBudgetReport(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<List<DataStyle>> reportData, List<List<DataStyle>> kpcaTotal) {
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);
		
		sheet.createFreezePane(0, 3);
		
		Row headerRow = sheet.createRow((short)2);
		HSSFCellStyle cellStyleForHeader = workbook.createCellStyle();
		cellStyleForHeader.setAlignment(cellStyleForHeader.ALIGN_CENTER);
		
		for(int i=0, y=2; i<header.size(); i++, y++){	
			DataStyle ds = (DataStyle)header.get(i);		
			Cell headerCell = headerRow.createCell(y);		
			headerCell.setCellValue(ds.getStrData());
			cellStyleForHeader.setFont(reportNameFont);
			headerCell.setCellStyle(cellStyleForHeader);
		}
		
		int rowCnt = 3;
		HSSFCellStyle cellStyleForRowHeadData;
		
		for (int j = 0; reportData!=null && j < reportData.size(); j++) {
			Row row = sheet.createRow(rowCnt);
			List<DataStyle> userData = reportData.get(j);
			
			for (int k =0, l=1; k < userData.size(); k++,l++) {
				
				if(userData.size() == 1){
					DataStyle ds1 = (DataStyle)userData.get(k);		
					Cell headerCell = row.createCell(l);		
					headerCell.setCellValue(ds1.getStrData());
					cellStyleForHeader.setFont(reportNameFont);
					headerCell.setCellStyle(cellStyleForHeader);
					sheet.addMergedRegion(new CellRangeAddress(rowCnt,rowCnt,1,header.size()+1));
				}else{
					Cell cell = row.createCell(l);
					DataStyle dstyle = (DataStyle)userData.get(k);
					
				//===start parvez date: 16-08-2022===	
					cell.setCellValue(dstyle.getStrData());	
				//===end parvez date: 16-08-2022===	
					cellStyleForRowHeadData = workbook.createCellStyle();
					cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
					cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
					cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
					cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
					cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());						
					
					cell.setCellStyle(cellStyleForRowHeadData);
				}
				
			}
			rowCnt++;
		}
		
		Row totRow = sheet.createRow(rowCnt);
		Cell celltotRowHead = totRow.createCell((short)1);		
		celltotRowHead.setCellValue("TOTAL KPCA");
		cellStyleForHeader.setFont(reportNameFont);
		celltotRowHead.setCellStyle(cellStyleForHeader);
		sheet.addMergedRegion(new CellRangeAddress(rowCnt,rowCnt,1,header.size()+1));
		
		rowCnt += 1;
		
		for (int j = 0; kpcaTotal!=null && j < kpcaTotal.size(); j++) {
			Row row = sheet.createRow(rowCnt);
			List<DataStyle> userData = kpcaTotal.get(j);
			for (int k = 0, l=1; k < userData.size(); k++,l++) {
				Cell cell = row.createCell(l);
				DataStyle dstyle = (DataStyle)userData.get(k);
				
			//===start parvez date: 16-08-2022===	
				cell.setCellValue(dstyle.getStrData());	
			//===end parvez date: 16-08-2022===	
				cellStyleForRowHeadData = workbook.createCellStyle();
				cellStyleForRowHeadData.setBorderTop(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setBorderBottom(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setBorderLeft(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setBorderRight(dstyle.getBorderStyle());
				cellStyleForRowHeadData.setAlignment(dstyle.getCellDataAlign());						
				
				cell.setCellStyle(cellStyleForRowHeadData);
			}
			rowCnt++;
		}
		
	}
	
	public void generateExcelSheetforQuarterlyProjectReport(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header,List<DataStyle> subHeader, List<List<DataStyle>> reportData) {

		UtilityFunctions uF = new UtilityFunctions();
		Row mainHeadingRow = sheet.createRow(1);
		
		Font reportHeadingFont = workbook.createFont();
		reportHeadingFont.setBoldweight((short)1200);	
		HSSFCellStyle cellStyleForMainHeadingRow = workbook.createCellStyle();
		cellStyleForMainHeadingRow.setAlignment(cellStyleForMainHeadingRow.ALIGN_CENTER);
		Cell reportHeading = mainHeadingRow.createCell((short)1);		
		DataStyle ds = (DataStyle)header.get(0);
		reportHeading.setCellValue(ds.getStrData());
		cellStyleForMainHeadingRow.setFont(reportHeadingFont);
		reportHeading.setCellStyle(cellStyleForMainHeadingRow);		
		sheet.addMergedRegion(new CellRangeAddress(1,1,1,reportData.get(0).size()));
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);	
		
		HSSFCellStyle cellStyleForSummary = workbook.createCellStyle();
		cellStyleForSummary.setAlignment(cellStyleForSummary.ALIGN_CENTER);
		
		Row row = sheet.createRow(2);
		Cell headerCell = row.createCell((short)1);		
		headerCell.setCellValue("Particulars");
		cellStyleForSummary.setFont(reportNameFont);
		headerCell.setCellStyle(cellStyleForSummary);
		sheet.addMergedRegion(new CellRangeAddress(2,3,1,1));
		int y = 2;
		for(int i=1;i<header.size();i++){
			DataStyle ds1 = (DataStyle)header.get(i);		
			Cell headerCell1 = row.createCell(y);
			String[] strHeaderData = ds1.getStrData().split("_");
			int lastCol = y+uF.parseToInt(strHeaderData[1])-1;
			System.out.println(lastCol);
//			headerCell1.setCellValue(ds1.getStrData());
			headerCell1.setCellValue(strHeaderData[0]);
			cellStyleForSummary.setFont(reportNameFont);
			headerCell1.setCellStyle(cellStyleForSummary);
			sheet.addMergedRegion(new CellRangeAddress(2,2,y,lastCol));
			y = y+uF.parseToInt(strHeaderData[1])-1;
			y++;
		}
		
		Cell headerCellTot = row.createCell(y);		
		headerCellTot.setCellValue("Total");
		cellStyleForSummary.setFont(reportNameFont);
		headerCellTot.setCellStyle(cellStyleForSummary);
		sheet.addMergedRegion(new CellRangeAddress(2,3,y,y));
		
		Row subHeaderRow = sheet.createRow(3);
		
		for(int i=0, a=2;i<subHeader.size();i++, a++){
			DataStyle ds1 = (DataStyle)subHeader.get(i);		
			Cell headerCell1 = subHeaderRow.createCell(a);		
			headerCell1.setCellValue(ds1.getStrData());
			cellStyleForSummary.setFont(reportNameFont);
			headerCell1.setCellStyle(cellStyleForSummary);
			
		}
		
		int rownum = 4;
		HSSFCellStyle cellStyleForData;
		
		for (int j = 0; reportData!=null && j < reportData.size(); j++) {
			Row reportRow = sheet.createRow(rownum);
			List<DataStyle> userData = reportData.get(j);
			for (int k = 0, l=1; k < userData.size(); k++,l++) {
				Cell cell = reportRow.createCell(l);
				DataStyle dstyle = (DataStyle)userData.get(k);
				
			//===start parvez date: 16-08-2022===	
				cell.setCellValue(dstyle.getStrData());	
			//===end parvez date: 16-08-2022===	
				cellStyleForData = workbook.createCellStyle();
				cellStyleForData.setBorderTop(dstyle.getBorderStyle());
				cellStyleForData.setBorderBottom(dstyle.getBorderStyle());
				cellStyleForData.setBorderLeft(dstyle.getBorderStyle());
				cellStyleForData.setBorderRight(dstyle.getBorderStyle());
				cellStyleForData.setAlignment(dstyle.getCellDataAlign());						
				
//				sheet.autoSizeColumn((short)l);
				cell.setCellStyle(cellStyleForData);
			}
			rownum++;
		}
		
		
	}
	
	
}//end of class