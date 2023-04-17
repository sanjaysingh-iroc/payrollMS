package com.konnect.jpms.export;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.UtilityFunctions;

public class ExportExcelMultiSheetReport implements ServletRequestAware,ServletResponseAware{
	
	String downloadreportname; 
	
	int rownum = 5;
	HSSFSheet sheet1;
	HSSFSheet sheet2;
	HSSFSheet sheet3;
	HSSFSheet sheet4;
	HSSFSheet sheet5;
	HSSFSheet sheet6;
	HSSFSheet sheet7;
	HSSFSheet sheet8;
	HSSFSheet sheet9;
	HSSFSheet sheet10;
	HSSFSheet sheet11;
	HSSFSheet sheet12;
	HSSFSheet sheet13;
	HSSFSheet sheet14;
	HSSFSheet sheet15;
	HSSFSheet sheet16;
	HSSFSheet sheet17;
	
	//Collection<File> files;
	HSSFWorkbook workbook; 
	//File exactFile;
	HSSFCellStyle cellStyleForHeader;
	HSSFCellStyle cellStyleForData;
	HSSFCellStyle cellStyleForReportName;	

	{
		workbook = new HSSFWorkbook();
		sheet1 = workbook.createSheet("Challan");
		sheet2 = workbook.createSheet("Deduction");
		sheet3 = workbook.createSheet("Salary");
		sheet4 = workbook.createSheet("Salary- 17(1)");
		sheet5 = workbook.createSheet("Other Salary");
		sheet6 = workbook.createSheet("Other Allowances");
		sheet7 = workbook.createSheet("Perquisites");
		sheet8 = workbook.createSheet("Exempt Perquisite");
		sheet9 = workbook.createSheet("Profit in lieu");
		sheet10 = workbook.createSheet("Sec.10 Exemption");
		sheet11 = workbook.createSheet("Sec.10-others");
		sheet12 = workbook.createSheet("Other income & HP loss");
		sheet13 = workbook.createSheet("Other income");
		sheet14 = workbook.createSheet("Landlord & Lender Details");
		sheet15 = workbook.createSheet("80CCC,CCD");
		sheet16 = workbook.createSheet("80C");
		sheet17 = workbook.createSheet("VI-A Deduction-others");
		
//		Row headerRow1 = sheet1.createRow(rownum);
//		Row headerRow2 = sheet2.createRow(rownum);
//		headerRow1.setHeightInPoints(40);
//		headerRow2.setHeightInPoints(40);
		HSSFPalette pallet = workbook.getCustomPalette();
		pallet.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index, (byte)230, (byte)225, (byte)225);
	}
	
	HttpSession session;
	
	String type;
	String excelType;
	 
	
	public void execute() throws Exception {
	
		System.out.println("in execute ExportExcelMultiSheetReport ===>> ");
		
		System.out.println("downloadreportname " + downloadreportname);
		session = request.getSession();
		
		UtilityFunctions uF = new UtilityFunctions();
		System.out.println("getType() ===>> " + getType());
		generateExcel(workbook,uF);
		
	}
	
	private void generateExcel(HSSFWorkbook workbook2, UtilityFunctions uF) {

		FileOutputStream fileOut = null;
		
		System.out.println("getExcelType() ===>> " + getExcelType());
//		if(getExcelType() != null) {
//			generateExcelSheetReport();
//		} else {
			generateSheetReport();
//		}
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			try {
				workbook.write(buffer);
				buffer.close();
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
	
//			response.setHeader("Content-Disposition", "attachment; filename=\"" + getDownloadreportname() + ".xls\"");
			response.setHeader("Content-Disposition", "attachment; filename=\"Form24QReport.xls\"");
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
		
		Map<String, List<List<DataStyle>>> hmReportExport = (Map<String, List<List<DataStyle>>>)session.getAttribute("hmReportExport");
		try {
			
			Iterator<String> it = hmReportExport.keySet().iterator();
			while(it.hasNext()) {
				String sheetNo = it.next();
				HSSFSheet sheet;
				if(sheetNo.equals("1")) {
					sheet = sheet1;
				} else if(sheetNo.equals("2")) {
					sheet = sheet2;
				} else if(sheetNo.equals("3")) {
					sheet = sheet3;
				} else if(sheetNo.equals("4")) {
					sheet = sheet4;
				} else if(sheetNo.equals("5")) {
					sheet = sheet5;
				} else if(sheetNo.equals("6")) {
					sheet = sheet6;
				} else if(sheetNo.equals("7")) {
					sheet = sheet7;
				} else if(sheetNo.equals("8")) {
					sheet = sheet8;
				} else if(sheetNo.equals("9")) {
					sheet = sheet9;
				} else if(sheetNo.equals("10")) {
					sheet = sheet10;
				} else if(sheetNo.equals("11")) {
					sheet = sheet11;
				} else if(sheetNo.equals("12")) {
					sheet = sheet12;
				} else if(sheetNo.equals("13")) {
					sheet = sheet13;
				} else if(sheetNo.equals("14")) {
					sheet = sheet14;
				} else if(sheetNo.equals("15")) {
					sheet = sheet15;
				} else if(sheetNo.equals("16")) {
					sheet = sheet16;
				} else {
					sheet = sheet17;
				}
				List<List<DataStyle>> reportData = hmReportExport.get(sheetNo);
				int nReportSize = reportData.size();	
				Row reportOrgName1 = sheet.createRow(1);
//				Row reportNameRow1 = sheet.createRow(2);
				Row row4 = sheet.createRow(3);
//				Row row5 = sheet.createRow(4);
				
				cellStyleForReportName = workbook.createCellStyle();
				Font font = workbook.createFont();
				font.setBoldweight((short)1200);	
				
//				Font reportNameFont = workbook.createFont();
//				reportNameFont.setBoldweight((short)1200);							
				
				/*List header = reportData.get(0);
				DataStyle ds = (DataStyle)header.get(0);				
				Cell reportOrg1 = reportOrgName1.createCell((short)1);
				reportOrg1.setCellValue(ds.getStrData());
				cellStyleForReportName.setFont(font);
				reportOrg1.setCellStyle(cellStyleForReportName);
//				sheet.addMergedRegion(CellRangeAddress.valueOf("B2:E2"));
				
				DataStyle ds1 = (DataStyle)header.get(1);				
				Cell reportName1 = reportNameRow1.createCell((short)1);
				setDownloadreportname(ds1.getStrData());	
				reportName1.setCellValue(ds1.getStrData());
				cellStyleForReportName.setFont(font);
				reportName1.setCellStyle(cellStyleForReportName);
//				sheet.addMergedRegion(CellRangeAddress.valueOf("B3:E3"));
				
				DataStyle ds2 = (DataStyle)header.get(2);				
				Cell reportDate1 = rDate1.createCell((short)1);
				reportDate1.setCellValue(ds2.getStrData());
				cellStyleForReportName.setFont(font);
				reportDate1.setCellStyle(cellStyleForReportName);
//				sheet.addMergedRegion(CellRangeAddress.valueOf("B4:E4"));
*/				
				
				
				// This portion is used to print the header values.
				rownum = 3;
				int rowMaxNo = 2;
				if(sheetNo.equals("4")) {
					rowMaxNo = 3;
					for (int j = 0; j < rowMaxNo; j++) {
						Row headerRow = sheet.createRow(rownum);
						List header = reportData.get(j);
						if(header == null) header = new ArrayList();
						for(int i=0,y=0;i<header.size();i++,y++) {
							Cell headerCell = headerRow.createCell(y);
							DataStyle ds = (DataStyle)header.get(i);
							headerCell.setCellValue("  "+ds.getStrData()+"  ");  
							cellStyleForHeader = workbook.createCellStyle();
							cellStyleForHeader.setBorderBottom(ds.getBorderStyle());
							cellStyleForHeader.setBorderLeft(ds.getBorderStyle());
							cellStyleForHeader.setBorderRight(ds.getBorderStyle());
							cellStyleForHeader.setBorderTop(ds.getBorderStyle());
							cellStyleForHeader.setAlignment(ds.getCellDataAlign());
							cellStyleForHeader.setFillForegroundColor(ds.getHSSFbackRoundColor());
							cellStyleForHeader.setFillPattern(ds.getFillPattern());
							cellStyleForHeader.setFont(font);
	//						cellStyleForHeader.setFillBackgroundColor((short)IndexedColors.GREY_25_PERCENT.getIndex());
							sheet.autoSizeColumn((short)y);
							headerCell.setCellStyle(cellStyleForHeader);
							if(j==1) {
//								System.out.println("y===>> " +y);
								if(i==4) {
									cellStyleForHeader.setAlignment(cellStyleForHeader.ALIGN_CENTER);
									sheet.addMergedRegion(CellRangeAddress.valueOf("E5:G5"));
									y=6;
								}
								if(i==5) {
									cellStyleForHeader.setAlignment(cellStyleForHeader.ALIGN_CENTER);
									sheet.addMergedRegion(CellRangeAddress.valueOf("H5:J5"));
									y=9;
								}
								if(i==7) {
									cellStyleForHeader.setAlignment(cellStyleForHeader.ALIGN_CENTER);
									sheet.addMergedRegion(CellRangeAddress.valueOf("L5:N5"));
									y=13;
								}
//								System.out.println("y after ===>> " +y);
							}
						}
						rownum++;
					}
				} else {
					for (int j = 0; j < rowMaxNo; j++) {
						Row headerRow = sheet.createRow(rownum);
						List header = reportData.get(j);
						if(header == null) header = new ArrayList();
						
						for(int i=0,y=0;i<header.size();i++,y++){
							Cell headerCell = headerRow.createCell(y);
							DataStyle ds = (DataStyle)header.get(i);
							headerCell.setCellValue("  "+ds.getStrData()+"  ");  
							cellStyleForHeader = workbook.createCellStyle();
							cellStyleForHeader.setBorderBottom(ds.getBorderStyle());
							cellStyleForHeader.setBorderLeft(ds.getBorderStyle());
							cellStyleForHeader.setBorderRight(ds.getBorderStyle());
							cellStyleForHeader.setBorderTop(ds.getBorderStyle());
							cellStyleForHeader.setAlignment(ds.getCellDataAlign());
							cellStyleForHeader.setFillForegroundColor(ds.getHSSFbackRoundColor());
							cellStyleForHeader.setFillPattern(ds.getFillPattern());
							cellStyleForHeader.setFont(font);
	//						cellStyleForHeader.setFillBackgroundColor((short)IndexedColors.GREY_25_PERCENT.getIndex());
							sheet.autoSizeColumn((short)y);
							headerCell.setCellStyle(cellStyleForHeader);
						}
						rownum++;
					}
				}
				
				// This portion is used to print the data in cells of table.
				for (int j = rowMaxNo; j < nReportSize; j++) {
					Row row = sheet.createRow(rownum);
					List userData = reportData.get(j);
					if(userData == null) userData = new ArrayList();
					
					int nUserDataSize = userData.size();
					for (int k = 0, l=0; k < nUserDataSize; k++,l++) {
						Cell cell = row.createCell(l);
						DataStyle ds = (DataStyle)userData.get(k);
			//			System.out.println("data = " + "Row num = " + rownum + "  "+ ds.getCellDataAlign());
						cell.setCellValue(""+ds.getStrData()+"");	
						cellStyleForData = workbook.createCellStyle();
						//cellStyleForData.setIndention((short)1);
						cellStyleForData.setBorderTop(ds.getBorderStyle());
						cellStyleForData.setBorderBottom(ds.getBorderStyle());
						cellStyleForData.setBorderLeft(ds.getBorderStyle());
						cellStyleForData.setBorderRight(ds.getBorderStyle());
						cellStyleForData.setAlignment(ds.getCellDataAlign());						
						
	//					sheet.autoSizeColumn((short)l);
						cell.setCellStyle(cellStyleForData);
						
					}
					rownum++;
				}
			}
			
			/*Row reportOrgName = sheet2.createRow(1);
			Row reportNameRow = sheet2.createRow(2);
			Row rDate = sheet2.createRow(3);
			Row headerRow = sheet2.createRow(4);
			
			cellStyleForReportName = workbook.createCellStyle();
			
//			DataStyle ds = (DataStyle)header.get(0);				
			//Cell reportName = reportNameRow.createCell(header.size()/2);
			Cell reportOrg = reportOrgName.createCell((short)1);
			reportOrg.setCellValue(ds.getStrData());
			cellStyleForReportName.setFont(reportNameFont);
			reportOrg.setCellStyle(cellStyleForReportName);
			sheet2.addMergedRegion(CellRangeAddress.valueOf("B2:E2"));
			
//			DataStyle ds1 = (DataStyle)header.get(1);				
			//Cell reportName = reportNameRow.createCell(header.size()/2);
			Cell reportName = reportNameRow.createCell((short)1);
			setDownloadreportname(ds1.getStrData());	
			reportName.setCellValue(ds1.getStrData());
			cellStyleForReportName.setFont(reportNameFont);
			reportName.setCellStyle(cellStyleForReportName);
			sheet2.addMergedRegion(CellRangeAddress.valueOf("B3:E3"));
			
//			DataStyle ds2 = (DataStyle)header.get(2);				
			//Cell reportName = reportNameRow.createCell(header.size()/2);
			Cell reportDate = rDate.createCell((short)1);
			reportDate.setCellValue(ds2.getStrData());
			cellStyleForReportName.setFont(reportNameFont);
			reportDate.setCellStyle(cellStyleForReportName);
			sheet2.addMergedRegion(CellRangeAddress.valueOf("B4:E4"));
			
			
			
			// This portion is used to print the header values.
			for(int i=3,y=1;i<header.size();i++,y++){
				Cell headerCell = headerRow.createCell(y);
				ds = (DataStyle)header.get(i);
				headerCell.setCellValue("  "+ds.getStrData()+"  ");  
				cellStyleForHeader = workbook.createCellStyle();
				cellStyleForHeader.setBorderBottom(ds.getBorderStyle());
				cellStyleForHeader.setBorderLeft(ds.getBorderStyle());
				cellStyleForHeader.setBorderRight(ds.getBorderStyle());
				cellStyleForHeader.setBorderTop(ds.getBorderStyle());
				cellStyleForHeader.setAlignment(ds.getCellDataAlign());
				cellStyleForHeader.setFillForegroundColor(ds.getHSSFbackRoundColor());
				cellStyleForHeader.setFillPattern(ds.getFillPattern());
				cellStyleForHeader.setFont(font);
				sheet2.autoSizeColumn((short)y);
				headerCell.setCellStyle(cellStyleForHeader);
			}
		
			
			// This portion is used to print the data in cells of table.
			for (int j = 1; j < nReportSize; j++) {
				Row row = sheet2.createRow(rownum);
				List userData = reportData.get(j);
				if(userData == null) userData = new ArrayList();
				
				int nUserDataSize = userData.size();
				for (int k = 0, l=1; k < nUserDataSize; k++,l++) {
					Cell cell = row.createCell(l);
					ds = (DataStyle)userData.get(k);
		//			System.out.println("data = " + "Row num = " + rownum + "  "+ ds.getCellDataAlign());
					cell.setCellValue(""+ds.getStrData()+"");	
					cellStyleForData = workbook.createCellStyle();
					//cellStyleForData.setIndention((short)1);
					cellStyleForData.setBorderTop(ds.getBorderStyle());
					cellStyleForData.setBorderBottom(ds.getBorderStyle());
					cellStyleForData.setBorderLeft(ds.getBorderStyle());
					cellStyleForData.setBorderRight(ds.getBorderStyle());
					cellStyleForData.setAlignment(ds.getCellDataAlign());						
					
//					sheet2.autoSizeColumn((short)l);
					cell.setCellStyle(cellStyleForData);
					
				}
				rownum++;
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	public String getReportName(){
		String name = "Updated Employee Sheet";
		return name;
	}


	HttpServletRequest request;
	HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	public String getDownloadreportname() {
		return downloadreportname;
	}

	public void setDownloadreportname(String downloadreportname) {
		this.downloadreportname = downloadreportname;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getExcelType() {
		return excelType;
	}

	public void setExcelType(String excelType) {
		this.excelType = excelType;
	}
	
}	
	
