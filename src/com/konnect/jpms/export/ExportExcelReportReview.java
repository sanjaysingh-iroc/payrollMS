package com.konnect.jpms.export;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.UtilityFunctions;

public class ExportExcelReportReview implements ServletRequestAware,ServletResponseAware{
	

	String downloadreportname; 
	
	int rownum = 2;
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
	
	HttpSession session;
	
	String type;
	 
	
	public void execute() throws Exception {
	
//		System.out.println("in execute ExportExcelReport====Printing ");
		
		session = request.getSession();
		
		UtilityFunctions uF = new UtilityFunctions();
		if(getType()!=null){
//			System.out.println("IF CASE");
			generateExcel(workbook,uF);
		}else{
//			System.out.println("ELSE CASE");
			createExcelFile(workbook);
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
			
	
			response.setHeader("Content-Disposition", "attachment; filename=\"" + getDownloadreportname() + ".xls\"");
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
		
		List<List<String>> reportData = (List)session.getAttribute("reportListExportScoreCard");
		try {				
				
				Row reportOrgName = firstSheet.createRow(1);
				Row reportNameRow = firstSheet.createRow(2);
				Row rDate = firstSheet.createRow(3);
				Row headerRow = firstSheet.createRow(4);
				
				cellStyleForReportName = workbook.createCellStyle();
				Font font = workbook.createFont();
				font.setBoldweight((short)1200);	
				
				Font reportNameFont = workbook.createFont();
				reportNameFont.setBoldweight((short)1200);							
				
				List header = reportData.get(0);
				DataStyle ds = (DataStyle)header.get(0);				
				//Cell reportName = reportNameRow.createCell(header.size()/2);
				Cell reportOrg = reportOrgName.createCell((short)1);
				reportOrg.setCellValue(ds.getStrData());
				cellStyleForReportName.setFont(reportNameFont);
				reportOrg.setCellStyle(cellStyleForReportName);
				firstSheet.addMergedRegion(CellRangeAddress.valueOf("B2:E2"));
				
				DataStyle ds1 = (DataStyle)header.get(1);				
				//Cell reportName = reportNameRow.createCell(header.size()/2);
				Cell reportName = reportNameRow.createCell((short)1);
				setDownloadreportname(ds1.getStrData());	
				reportName.setCellValue(ds1.getStrData());
				cellStyleForReportName.setFont(reportNameFont);
				reportName.setCellStyle(cellStyleForReportName);
				firstSheet.addMergedRegion(CellRangeAddress.valueOf("B3:E3"));
				
				DataStyle ds2 = (DataStyle)header.get(2);				
				//Cell reportName = reportNameRow.createCell(header.size()/2);
				Cell reportDate = rDate.createCell((short)1);
				reportDate.setCellValue(ds2.getStrData());
				cellStyleForReportName.setFont(reportNameFont);
				reportDate.setCellStyle(cellStyleForReportName);
				firstSheet.addMergedRegion(CellRangeAddress.valueOf("B4:E4"));
				
				
				
				// This portion is used to print the header values.
				for(int i=3,y=1;i<header.size();i++,y++){
					Cell headerCell = headerRow.createCell(y);
					ds = (DataStyle)header.get(i);
//					System.out.println("DS : "+ds.getStrData());
					headerCell.setCellValue("  "+ds.getStrData()+" 77 ");  
					cellStyleForHeader = workbook.createCellStyle();
					cellStyleForHeader.setBorderBottom(ds.getBorderStyle());
					cellStyleForHeader.setBorderLeft(ds.getBorderStyle());
					cellStyleForHeader.setBorderRight(ds.getBorderStyle());
					cellStyleForHeader.setBorderTop(ds.getBorderStyle());
					cellStyleForHeader.setAlignment(ds.getCellDataAlign());
					cellStyleForHeader.setFillForegroundColor(ds.getHSSFbackRoundColor());
					cellStyleForHeader.setFillPattern(ds.getFillPattern());
					cellStyleForHeader.setFont(font);
					firstSheet.autoSizeColumn((short)y);
					headerCell.setCellStyle(cellStyleForHeader);
				}			
			
				
				// This portion is used to print the data in cells of table.
				for (int j = 1; j < reportData.size(); j++) {
					Row row = firstSheet.createRow(rownum);
					List userData = reportData.get(j);
					for (int k = 0, l=1; k < userData.size(); k++,l++) {
						Cell cell = row.createCell(l);
						ds = (DataStyle)userData.get(k);
			//			System.out.println("data = " + "Row num = " + rownum + "  "+ ds.getCellDataAlign());
						cell.setCellValue(" "+ds.getStrData()+" ");	
						cellStyleForData = workbook.createCellStyle();
						//cellStyleForData.setIndention((short)1);
						cellStyleForData.setBorderTop(ds.getBorderStyle());
						cellStyleForData.setBorderBottom(ds.getBorderStyle());
						cellStyleForData.setBorderLeft(ds.getBorderStyle());
						cellStyleForData.setBorderRight(ds.getBorderStyle());
						cellStyleForData.setAlignment(ds.getCellDataAlign());						
						
						firstSheet.autoSizeColumn((short)l);
						cell.setCellStyle(cellStyleForData);
						
					}
					rownum++;
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public void createExcelFile(HSSFWorkbook workbook) throws Exception {

		FileOutputStream fileOut = null;
		
		writeSheetReport();
		
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			try {
				workbook.write(buffer);
				buffer.close();
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
	
			response.setHeader("Content-Disposition", "attachment; filename=\"" + getDownloadreportname() + ".xls\"");
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
	
	

	public void writeSheetReport() throws Exception {
		
		List<List<String>> reportData = (List)session.getAttribute("reportListExportScoreCard");
		try {
//			System.out.println("reportData ========>> " + reportData);
				Row headerRow = firstSheet.createRow(1);
				Row reportNameRow = firstSheet.createRow(0);
							        
				cellStyleForReportName = workbook.createCellStyle();
				Font font = workbook.createFont();
				font.setBoldweight((short)1200);	
				
				Font reportNameFont = workbook.createFont();
				reportNameFont.setBoldweight((short)1200);							
				
				List header = reportData.get(0);
				DataStyle ds = (DataStyle)header.get(0);
				UtilityFunctions uF = new UtilityFunctions();
				
				//Cell reportName = reportNameRow.createCell(header.size()/2);
				Cell reportName = reportNameRow.createCell((short)1);
				setDownloadreportname(ds.getStrData());	
				reportName.setCellValue(ds.getStrData());
				cellStyleForReportName.setFont(reportNameFont);
//				cellStyleForReportName.setFillPattern(HSSFCellStyle.NO_FILL);
//				cellStyleForReportName.setFillBackgroundColor(ds.getBackRoundColor());
				reportName.setCellStyle(cellStyleForReportName);
				firstSheet.addMergedRegion(CellRangeAddress.valueOf("B1:H1"));
				
				// This portion is used to print the header values.
				for(int i=1,y=1;i<header.size();i++,y++){
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
					firstSheet.autoSizeColumn((short)y);
					headerCell.setCellStyle(cellStyleForHeader);
				}			
			
				String[] strArr = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ",
						"AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT"};// Created by Dattatray Date : 02-July-21 Note : Added more column code
				List<String> alABCD = new ArrayList<String>();
				// This portion is used to print the data in cells of table.
				int cnt =7;
				int cnt1 =7;
				for (int j = 1; j < reportData.size(); j++) {
					Row row = firstSheet.createRow(rownum);
					List userData = reportData.get(j);
					for (int k = 0, l=1; k < userData.size(); k++,l++) {
						ds = (DataStyle)userData.get(k);
						String strData = ds.getStrData();
						boolean flag = false;
						if (row.getRowNum() == 10) {
							if(l>6) {
//								System.out.println("Header = "+ds.getStrData());
								String[] strDt = ds.getStrData().split("::");
								if(strDt.length>1) {
									System.out.println("strDt[0] ==>> "+strDt[0] + " -- strDt[1] ==>> " + strDt[1]);
//									cnt = l+(uF.parseToInt(strDt[1])-1);
//									flag = true;
//									strData = strDt[0];
									
									// Start Dattatray Date : 02-July-21
									if (uF.parseToInt(strDt[1]) > 0) {
										cnt = l+(uF.parseToInt(strDt[1])-1);
									}else {
										cnt = l;
									}
									flag = true;
									strData = strDt[0];
									// End Dattatray Date : 02-July-21
								}
							}
						}
						Cell cell = row.createCell(l);
						cell.setCellValue(strData);
//						System.out.println("cnt==>>" + cnt + "-- l ===>> " + l);
//						System.out.println("data = "+ds.getStrData() + "Row num = " + rownum + "  "+ ds.getCellDataAlign()+" cell "+row.getRowNum());
						if(flag) {
							firstSheet.addMergedRegion(CellRangeAddress.valueOf(""+strArr[l]+"11:"+strArr[cnt]+"11"));
						}
//						System.out.println("ds : "+ds.getStrData());
						cellStyleForData = workbook.createCellStyle();
						//cellStyleForData.setIndention((short)1);
						cellStyleForData.setBorderTop(ds.getBorderStyle());
						cellStyleForData.setBorderBottom(ds.getBorderStyle());
						cellStyleForData.setBorderLeft(ds.getBorderStyle());
						cellStyleForData.setBorderRight(ds.getBorderStyle());
						if(flag) {
							cellStyleForData.setAlignment(HSSFCellStyle.ALIGN_CENTER);
						}
//						cellStyleForData.setAlignment(ds.getCellDataAlign());						
						
						firstSheet.autoSizeColumn((short)l);
						cell.setCellStyle(cellStyleForData);
						if(flag) {
							l=cnt;
						}
					}
					rownum++;
				}
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
	
}	
	

