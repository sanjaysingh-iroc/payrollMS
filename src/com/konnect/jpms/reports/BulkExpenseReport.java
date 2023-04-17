package com.konnect.jpms.reports;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

public class BulkExpenseReport extends ActionSupport implements ServletRequestAware, ServletResponseAware,IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CityReport.class);
	 private String download = null;
	 
	  
	public String execute() throws Exception {
		
				
		request.setAttribute(PAGE, "/jsp/reports/BulkExpenseReport.jsp");
		request.setAttribute(TITLE, "Bulk Expense Report");
		download = request.getParameter("download");
		//System.out.println("Download=======>"+download);
		if(download != null && download.equals("true")){ 
			
			/*Row reportOrgName = firstSheet.createRow(1);
			Row reportNameRow = firstSheet.createRow(2);
			Row rDate = firstSheet.createRow(3);
			Row headerRow = firstSheet.createRow(4);
			Cell reportOrg = reportOrgName.createCell((short)1);
			Cell reportOrg1 = reportOrgName.createCell((short)2);
			Cell reportOrg2 = reportOrgName.createCell((short)3);
			Cell reportOrg3 = reportOrgName.createCell((short)4);*/
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Bulk Expense");

			List<DataStyle> header = new ArrayList<DataStyle>();
			header.add(new DataStyle("Bulk Expense Report ", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Travel", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Local", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Other Expenses", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Date", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Purpose", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Flight", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Train", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Car", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Bus", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Details", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Mobile Bill", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Internet Charges", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Food Expenses", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Lodging Expenses", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Details", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Total Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));

		
			//List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();

				List<DataStyle> innerList = new ArrayList<DataStyle>();
				innerList.add(new DataStyle("12/01/18", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("test", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("1200", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("150", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Snacks", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("500", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("1000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("3000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("4000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("500", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Travel", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				
				innerList.add(new DataStyle("10/01/18", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("test", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("1200", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("150", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Snacks", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("500", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("1000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("3000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("4000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("500", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Travel", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				
				innerList.add(new DataStyle("25/01/18", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("test", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("1200", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("150", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Snacks", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("500", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("1000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("3000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("4000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("500", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Travel", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				
				innerList.add(new DataStyle("26/02/18", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("test", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("1200", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("100", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Snacks", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("500", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("2000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("1000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("900", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("400", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Travel", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				
				innerList.add(new DataStyle("28/01/18", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("test", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("1200", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("50", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Snacks", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("200", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("700", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("2000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("1000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("300", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Travel", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				
				innerList.add(new DataStyle("23/09/17", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("test", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("1400", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("100", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Snacks", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("500", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("100", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("2000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("5000", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("800", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Travel", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Total Payment/Refund", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
               
			//reportData.add(innerList);

			
		
			
			getExcelSheetDesign(workbook, sheet, header, innerList);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			System.out.println("response====>"+response);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=BulkExpenseReport.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
		/*	HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Sample sheet");
			
			
			
				Row row = sheet.createRow(0);
				
				
				
					Cell cell = row.createCell(0);
					
						cell.setCellValue("Hi");
					
				
			System.out.println("download excel");
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=BulkExpenseReport.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			System.out.println(out.toString());
			out.flush();
			buffer.close();
			out.close();*/

	        
		}
	
		return LOAD;
		

	}
	
	public void getExcelSheetDesign(HSSFWorkbook workbook, HSSFSheet sheet, List<DataStyle> header, List<DataStyle> innerList) {
		
		Row reportNameRow = sheet.createRow(2);
		Row travelRow = sheet.createRow(4);
		//Row localRow = sheet.createRow(4);
		//Row otherRow = sheet.createRow(4);
		Row headerRow = sheet.createRow(5);
		
		HSSFCellStyle cellStyleForReportName = workbook.createCellStyle();
		HSSFCellStyle cellStyleForTravel = workbook.createCellStyle();
		HSSFCellStyle cellStyleForLocal = workbook.createCellStyle();
		HSSFCellStyle cellStyleForOther = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight((short)1200);	
		
		Font reportNameFont = workbook.createFont();
		reportNameFont.setBoldweight((short)1200);	
		
		DataStyle ds = (DataStyle)header.get(0);
		
		//Cell reportName = reportNameRow.createCell(header.size()/2);
		Cell reportName = reportNameRow.createCell((short)6);
		
	//===start parvez date: 16-08-2022===	
		reportName.setCellValue(ds.getStrData());
	//===end parvez date: 16-08-2022===	
		System.out.println("Header======>"+ds.getStrData());
		cellStyleForReportName.setFont(reportNameFont);
		reportName.setCellStyle(cellStyleForReportName);
		
		sheet.addMergedRegion(CellRangeAddress.valueOf("G3:I3"));
		
       DataStyle dsTravel = (DataStyle)header.get(1);
	   //System.out.println(header.get(1).toString());
		Cell TravelTag = travelRow.createCell((short)2);
		
	//===start parvez date: 16-08-2022===	
		TravelTag.setCellValue(dsTravel.getStrData());
	//===end parvez date: 16-08-2022===	
		System.out.println("Travel======>"+dsTravel.getStrData());
		cellStyleForTravel.setFont(reportNameFont);
		TravelTag.setCellStyle(cellStyleForTravel);
		
		sheet.addMergedRegion(CellRangeAddress.valueOf("C5:F5"));
		
		
	       DataStyle dsLocal = (DataStyle)header.get(2);
	      // System.out.println(header.get(2).toString());
			Cell LocalTag = travelRow.createCell((short)6);
			
		//===start parvez date: 16-08-2022===	
			LocalTag.setCellValue(dsLocal.getStrData());
		//===end parvez date: 16-08-2022===	
			cellStyleForLocal.setFont(reportNameFont);
			LocalTag.setCellStyle(cellStyleForLocal);
			
			sheet.addMergedRegion(CellRangeAddress.valueOf("G5:H5"));
			
			 DataStyle dsOther = (DataStyle)header.get(3);
			// System.out.println(header.get(3).toString());
				Cell OtherTag = travelRow.createCell((short)12);
				
			//===start parvez date: 16-08-2022===	
				OtherTag.setCellValue(dsOther.getStrData());
			//===end parvez date: 16-08-2022===	
				cellStyleForOther.setFont(reportNameFont);
				OtherTag.setCellStyle(cellStyleForOther);
				
				sheet.addMergedRegion(CellRangeAddress.valueOf("M5:N5"));
			
		for(int i=4,y=0;i<header.size();i++,y++){
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
			sheet.autoSizeColumn((short)y);
			headerCell.setCellStyle(cellStyleForHeader);
		}
		
		
	int rownum = 6;
	Row row = sheet.createRow(rownum);
		HSSFCellStyle cellStyleForData;
		int k=0;
		for (int j = 0; innerList!=null && j < innerList.size(); j++) {
			if(k==14)
			{
				rownum++;
			    row = sheet.createRow(rownum);
			    k=0;
			}
			
			DataStyle userData = innerList.get(j);
			
				Cell cell = row.createCell(k);
						
			//===start parvez date: 16-08-2022===	
				cell.setCellValue(userData.getStrData());	
			//===end parvez date: 16-08-2022===	
				cellStyleForData = workbook.createCellStyle();
				cellStyleForData.setBorderTop(userData.getBorderStyle());
				cellStyleForData.setBorderBottom(userData.getBorderStyle());
				cellStyleForData.setBorderLeft(userData.getBorderStyle());
				cellStyleForData.setBorderRight(userData.getBorderStyle());
				cellStyleForData.setAlignment(userData.getCellDataAlign());				
				
				sheet.autoSizeColumn((short)k);
				cell.setCellStyle(cellStyleForData);
				k++;
				
			
			
		}
		
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

}
