package com.konnect.jpms.test;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.*;
//import org.apache.struts2.interceptor.ServletRequestAware;
//import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.util.UtilityFunctions;

//import com.itextpdf.text.BaseColor;
//import com.itextpdf.text.Element;



public class ExportExcelReport1{
	

	String downloadreportname; 
	 static HSSFWorkbook workbook; 


	
	HttpSession session;
	
	String type;
	String excelType;
	 
	
	public static void main(String[] args) throws Exception {
		

		
		
		UtilityFunctions uF = new UtilityFunctions();
		
		generateExcelReport(workbook,uF);
//			createExcelFile(workbook);
		
	}

/*	public static void generateExcel(HSSFWorkbook workbook, UtilityFunctions uF) {

		FileOutputStream fileOut = null;
		
//		System.out.println("getExcelType() ===>> " + getExcelType());
//		if(getExcelType() != null) {
//			generateExcelSheetReport();
//		} else {
//		generateExcelReport();
//		}
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//			  String fileName = "c:/temp/salaryExcel.xls";

			  try {
//				OutputStream fileOut1 = new FileOutputStream("C:\\temp\\BankStatement.xlsx");
				 FileOutputStream fileOutputStream = new FileOutputStream("C:\\temp\\BankStatement.xlsx");
		   
			  System.out.println("Excel File has been created successfully.");   
			  
			
				workbook.write(fileOutputStream);
				buffer.close();
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
	

	}
	*/

	public static void generateExcelReport(HSSFWorkbook workbook, UtilityFunctions uF) {
		
		HSSFSheet firstSheet;	
		//Collection<File> files;
		//File exactFile;
		HSSFCellStyle cellStyleForHeader;
		HSSFCellStyle cellStyleForData;

		{
			workbook = new HSSFWorkbook();
			firstSheet = workbook.createSheet("Reports");
			Row headerRow = firstSheet.createRow(5);
			headerRow.setHeightInPoints(40);
			HSSFPalette pallet = workbook.getCustomPalette();
			pallet.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index, (byte)230, (byte)225, (byte)225);
		}
		
//		List<List<String>> reportData = (List)session.getAttribute("reportListExport");
		
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
	
		alInnerExport.add(new DataStyle("[ FORM NO.  22 ]", Element.ALIGN_MIDDLE, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("MUSTER ROLL CUM REGISTER OF WAGES/SALARY/SUBSISTENCE ALLOWANCE",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("(See Rule 137)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("I.	Form 22 (Muster Roll-cum-Register Wages Salary/Subsistence Allowance) Rule 137 of the Karnataka Factories Rules, (1969)",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("II. Form V (Rule 29(1) of the Karnataka Minimum Wages Rules, 1958)",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("III. Form VII (Rule 29(5) of the Karnataka Minimum Wages Rules, 1958)",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("IV. Form  III (Rule 5 of the Karnataka Payment of Wages Rules, 1963)",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("V.	Form XVI  (Rule 78(1)(a)(i) of the Contract Labour (Regulation & Abolition) (Karnataka) Rules, 1974",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle("VI. Form XVII (Rules 78(1)(a)(i) of the Contract Labour (Regulation & Abolition) (Karnataka) Rules, 1974",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport.add(new DataStyle(" ",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		
		List<DataStyle> alInnerExport1 = new ArrayList<DataStyle>();
		alInnerExport1.add(new DataStyle("Name and Address of the Factory/Establishment ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport1.add(new DataStyle("Name and Address of the Contractor (if any) ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport1.add(new DataStyle("Name and Address of the Prinicpal/Employer ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		
		List<DataStyle> alInnerExport2 = new ArrayList<DataStyle>();
		alInnerExport2.add(new DataStyle(" Place of work: Haralur Bengaluru",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport2.add(new DataStyle(" Month/Year: July-19 ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		
		

		List<DataStyle> alInnerExport4 = new ArrayList<DataStyle>();
		alInnerExport4.add(new DataStyle("Basic",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport4.add(new DataStyle("DA/VDA",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport4.add(new DataStyle("HRA	Conveyance",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport4.add(new DataStyle("Med. Allowance",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport4.add(new DataStyle("ATT/bonus",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport4.add(new DataStyle("Spl. All.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport4.add(new DataStyle("OT",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport4.add(new DataStyle("Misc",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport4.add(new DataStyle("earnings",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport4.add(new DataStyle("Others",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport4.add(new DataStyle("Total",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

		
		List<DataStyle> alInnerExport5 = new ArrayList<DataStyle>();
		alInnerExport5.add(new DataStyle("ESI",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport5.add(new DataStyle("PF",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport5.add(new DataStyle("PT",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport5.add(new DataStyle("TDS",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport5.add(new DataStyle("Socy",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport5.add(new DataStyle("Adv",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport5.add(new DataStyle("Sal.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport5.add(new DataStyle("Fines",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport5.add(new DataStyle("Damages/Loss",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport5.add(new DataStyle("Others",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport5.add(new DataStyle("Net Payable",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport5.add(new DataStyle("Employee/Receiver",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport5.add(new DataStyle("Signature/Thumb impression",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		
		List<DataStyle> alInnerExport3 = new ArrayList<DataStyle>();
		alInnerExport3.add(new DataStyle("Sl.No",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport3.add(new DataStyle("Name of the \n employee Father/ Husband’s",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport3.add(new DataStyle("Sex M/F",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport3.add(new DataStyle("Emp.No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport3.add(new DataStyle("Degn/dept",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport3.add(new DataStyle("Date of Joining",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport3.add(new DataStyle("ESI.No/PF.No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport3.add(new DataStyle("No. of Payable days",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport3.add(new DataStyle("ATTENDANCE",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		
		List<DataStyle> alInnerExport6 = new ArrayList<DataStyle>();
		alInnerExport6.add(new DataStyle("1",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("Santosh ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("M",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("G001",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("IT",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("03/07/19",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("Sl.No",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("222",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

		alInnerExport6.add(new DataStyle("1",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("2",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("3",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("4",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("5",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("6",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("7",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("8",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("9",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("10",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("11",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("12",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("13",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("14",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("15",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		alInnerExport6.add(new DataStyle("16",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));


		
		reportListExport.add(alInnerExport);
		reportListExport.add(alInnerExport1);
		reportListExport.add(alInnerExport2);
		reportListExport.add(alInnerExport3);
		reportListExport.add(alInnerExport4);
		reportListExport.add(alInnerExport5);
		reportListExport.add(alInnerExport6);

		
		List<List<DataStyle>> reportData =reportListExport;


		try {				
			int nReportSize = reportData.size();	
			Row reportOrgName = firstSheet.createRow(1);
			Row reportNameRow = firstSheet.createRow(2);
			Row rDate = firstSheet.createRow(3);
			
			cellStyleForHeader = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBoldweight((short)1200);	
			
			Font reportNameFont = workbook.createFont();
			reportNameFont.setBoldweight((short)1200);	
			Font smallFont = workbook.createFont();
//			smallFont.setBold(false);
	
			
			List header = reportData.get(0);
			// Header 1
			DataStyle ds = (DataStyle)header.get(0);				
			Cell header1 = reportOrgName.createCell((short)1);
			header1.setCellValue(ds.getStrData());
			cellStyleForHeader.setFont(reportNameFont);
			header1.setCellStyle(cellStyleForHeader);
			cellStyleForHeader.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyleForHeader.setVerticalAlignment(CellStyle.ALIGN_CENTER);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("B2:N2"));
		
			// Header 2
			DataStyle ds1 = (DataStyle)header.get(1);				
			Cell header2 = reportNameRow.createCell((short)1);
			header2.setCellValue(ds1.getStrData());
			cellStyleForHeader.setFont(reportNameFont);
			header2.setCellStyle(cellStyleForHeader);
			cellStyleForHeader.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyleForHeader.setVerticalAlignment(CellStyle.ALIGN_CENTER);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("B3:N3"));
			
			// Header 3
			DataStyle ds2 = (DataStyle)header.get(2);				
			Cell header3 = rDate.createCell((short)1);
			header3.setCellValue(ds2.getStrData());
			cellStyleForHeader.setFont(reportNameFont);
			header3.setCellStyle(cellStyleForHeader);
			cellStyleForHeader.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyleForHeader.setVerticalAlignment(CellStyle.ALIGN_CENTER);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("B4:N4"));
	 
			cellStyleForData = workbook.createCellStyle();

			// Header 4
			DataStyle ds3 = (DataStyle)header.get(3);	
			Row row4 = firstSheet.createRow(4);
			Cell header4 = row4.createCell((short)2);
			header4.setCellValue(ds3.getStrData());
			cellStyleForData.setFont(reportNameFont);
			header4.setCellStyle(cellStyleForData);
			cellStyleForData.setAlignment(CellStyle.ALIGN_LEFT);
			cellStyleForData.setVerticalAlignment(CellStyle.ALIGN_LEFT);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("C5:N5"));
			
			// Header 5
			DataStyle ds4 = (DataStyle)header.get(4);	
			Row row5 = firstSheet.createRow(5);
			Cell header5 = row5.createCell((short)2);
			header5.setCellValue(ds4.getStrData());
			cellStyleForData.setFont(smallFont);
			header5.setCellStyle(cellStyleForData);
			cellStyleForData.setAlignment(CellStyle.ALIGN_LEFT);
			cellStyleForData.setVerticalAlignment(CellStyle.ALIGN_LEFT);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("C6:N6"));
			
			// Header 6
			DataStyle ds5 = (DataStyle)header.get(5);	
			Row row6 = firstSheet.createRow(6);
			Cell header6 = row6.createCell((short)2);
			header6.setCellValue(ds5.getStrData());
			cellStyleForData.setFont(smallFont);
			header6.setCellStyle(cellStyleForData);
			cellStyleForData.setAlignment(CellStyle.ALIGN_LEFT);
			cellStyleForData.setVerticalAlignment(CellStyle.ALIGN_LEFT);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("C7:N7"));
			
			// Header 7
			DataStyle ds6 = (DataStyle)header.get(6);	
			Row row7 = firstSheet.createRow(7);
			Cell header7 = row7.createCell((short)2);
			header7.setCellValue(ds6.getStrData());
			cellStyleForData.setFont(smallFont);
			header7.setCellStyle(cellStyleForData);
			cellStyleForData.setAlignment(CellStyle.ALIGN_LEFT);
			cellStyleForData.setVerticalAlignment(CellStyle.ALIGN_LEFT);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("C8:N8"));
			
			// Header 8
			DataStyle ds7 = (DataStyle)header.get(7);	
			Row row8 = firstSheet.createRow(8);
			Cell header8 = row8.createCell((short)2);
			header8.setCellValue(ds7.getStrData());
			cellStyleForData.setFont(smallFont);
			header8.setCellStyle(cellStyleForData);
			cellStyleForData.setAlignment(CellStyle.ALIGN_LEFT);
			cellStyleForData.setVerticalAlignment(CellStyle.ALIGN_LEFT);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("C9:N9"));
			
			// Header 9
			DataStyle ds8 = (DataStyle)header.get(8);	
			Row row9 = firstSheet.createRow(9);
			Cell header9 = row9.createCell((short)2);
			header9.setCellValue(ds8.getStrData());
			cellStyleForData.setFont(smallFont);
			header9.setCellStyle(cellStyleForData);
			cellStyleForData.setAlignment(CellStyle.ALIGN_LEFT);
			cellStyleForData.setVerticalAlignment(CellStyle.ALIGN_LEFT);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("C10:N10"));
			
			// Header 10
			DataStyle ds9 = (DataStyle)header.get(9);	
			Row row10 = firstSheet.createRow(10);
			Cell header10 = row10.createCell((short)1);
			header10.setCellValue(ds9.getStrData());
			cellStyleForData.setFont(smallFont);
			header10.setCellStyle(cellStyleForData);
			cellStyleForData.setAlignment(CellStyle.ALIGN_LEFT);
			cellStyleForData.setVerticalAlignment(CellStyle.ALIGN_LEFT);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("B11:N11"));

			
			//Name and Address of the Factory/Establishment Header table

			Row row11 = firstSheet.createRow(11);
			List header11 = reportData.get(1);
			DataStyle ds11 = (DataStyle)header11.get(0);

			for(int i=0,y=3;i<header11.size();i++,y++){
				Cell headerCell = row11.createCell(y);
				ds11 = (DataStyle)header11.get(i);
				headerCell.setCellValue("  "+ds11.getStrData()+"  "); 
				cellStyleForHeader = workbook.createCellStyle();
				cellStyleForHeader.setBorderBottom(ds11.getBorderStyle());
				cellStyleForHeader.setBorderLeft(ds11.getBorderStyle());
				cellStyleForHeader.setBorderRight(ds11.getBorderStyle());
				cellStyleForHeader.setBorderTop(ds11.getBorderStyle());
				cellStyleForHeader.setAlignment(ds11.getCellDataAlign());
				cellStyleForHeader.setFillForegroundColor(ds11.getHSSFbackRoundColor());
				cellStyleForHeader.setFillPattern(ds11.getFillPattern());
				cellStyleForHeader.setFont(font);
				firstSheet.autoSizeColumn((short)y);
				headerCell.setCellStyle(cellStyleForHeader);
			}
			
			// print Name and Address of the Factory/Establishment Header table
			
			for (int j = 1; j < nReportSize; j++) {
				Row row = firstSheet.createRow(12);
				List<DataStyle> userData = reportData.get(1);
				if(userData == null) userData = new ArrayList();
				
				int nUserDataSize = userData.size();
				for (int k = 0, l=3; k < nUserDataSize; k++,l++) {
					Cell cell = row.createCell(l);
					ds11 = (DataStyle)userData.get(k);
					cell.setCellValue("test data"+k+"");
//					cell.setCellValue("test data"+ds.getStrData()+"");	
					cellStyleForData.setBorderTop(ds11.getBorderStyle());
					cellStyleForData.setBorderBottom(ds11.getBorderStyle());
					cellStyleForData.setBorderLeft(ds11.getBorderStyle());
					cellStyleForData.setBorderRight(ds11.getBorderStyle());
					cellStyleForData.setAlignment(ds11.getCellDataAlign());						
					cell.setCellStyle(cellStyleForData);
					
				}
			}
			
			// Place of work header table
			Row row12 = firstSheet.createRow(14);
			List header12 = reportData.get(2);
			DataStyle ds12 = (DataStyle)header12.get(0);

			for(int i=0,y=4;i<header12.size();i++,y++){
				Cell headerCell = row12.createCell(y);
				ds12 = (DataStyle)header12.get(i);
				headerCell.setCellValue("  "+ds12.getStrData()+"  ");  
				cellStyleForHeader = workbook.createCellStyle();
				cellStyleForHeader.setBorderBottom(ds12.getBorderStyle());
				cellStyleForHeader.setBorderLeft(ds12.getBorderStyle());
				cellStyleForHeader.setBorderRight(ds12.getBorderStyle());
				cellStyleForHeader.setBorderTop(ds12.getBorderStyle());
				cellStyleForHeader.setAlignment(ds12.getCellDataAlign());
				cellStyleForHeader.setFillForegroundColor(ds12.getHSSFbackRoundColor());
				cellStyleForHeader.setFillPattern(ds12.getFillPattern());
				cellStyleForHeader.setFont(font);
				firstSheet.autoSizeColumn((short)y);
				headerCell.setCellStyle(cellStyleForHeader);
			}
			

			// EMployee detail header table
			Row row13 = firstSheet.createRow(15);
			List header13 = reportData.get(3);
			DataStyle ds13 = (DataStyle)header13.get(0);

			for(int i=0,y=2;i<header13.size();i++,y++){
				Cell headerCell = row13.createCell(y);
				ds13 = (DataStyle)header13.get(i);
				headerCell.setCellValue("  "+ds13.getStrData()+"  ");  
				cellStyleForHeader = workbook.createCellStyle();
				cellStyleForHeader.setBorderBottom(ds13.getBorderStyle());
				cellStyleForHeader.setBorderLeft(ds13.getBorderStyle());
				cellStyleForHeader.setBorderRight(ds13.getBorderStyle());
				cellStyleForHeader.setBorderTop(ds13.getBorderStyle());
				cellStyleForHeader.setAlignment(ds13.getCellDataAlign());
				cellStyleForHeader.setFillForegroundColor(ds13.getHSSFbackRoundColor());
				cellStyleForHeader.setFillPattern(ds13.getFillPattern());
				cellStyleForHeader.setFont(font);
				cellStyleForHeader.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
				cellStyleForHeader.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
				cellStyleForHeader.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
				cellStyleForHeader.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
				firstSheet.autoSizeColumn((short)y);
				headerCell.setCellStyle(cellStyleForHeader);
				
			}
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("J16:Y16"));
//			RegionUtil.setBorderTop(CellStyle.BORDER_MEDIUM, CellRangeAddress.valueOf("J16:Y16"), firstSheet, workbook);
//		    RegionUtil.setBorderLeft(CellStyle.BORDER_MEDIUM, CellRangeAddress.valueOf("J16:Y16"), firstSheet, workbook);
//		    RegionUtil.setBorderRight(CellStyle.BORDER_MEDIUM, CellRangeAddress.valueOf("J16:Y16"), firstSheet, workbook);
//		    RegionUtil.setBorderBottom(CellStyle.BORDER_MEDIUM, CellRangeAddress.valueOf("J16:Y16"), firstSheet, workbook);
			
			// // EMployee detail  data table 
			for (int j = 1; j < nReportSize; j++) {
				Row row = firstSheet.createRow(16);
				List<DataStyle> userData = reportData.get(6);
				if(userData == null) userData = new ArrayList();
				
				int nUserDataSize = userData.size();
				for (int k = 0, l=2; k < nUserDataSize; k++,l++) {
					Cell cell = row.createCell(l);
					ds13 = (DataStyle)userData.get(k);
					cell.setCellValue(""+ds13.getStrData()+"");	
					cellStyleForData.setBorderTop(ds13.getBorderStyle());
					cellStyleForData.setBorderBottom(ds13.getBorderStyle());
					cellStyleForData.setBorderLeft(ds13.getBorderStyle());
					cellStyleForData.setBorderRight(ds13.getBorderStyle());
					cellStyleForData.setAlignment(ds13.getCellDataAlign());
					cell.setCellStyle(cellStyleForData);
					
				}
			}
						
			//Month & year header
			Row row14 = firstSheet.createRow(21);
			Cell header14 = row14.createCell((short)2);
			header14.setCellValue("MONTH & YEAR");
			cellStyleForHeader.setFont(reportNameFont);
			header14.setCellStyle(cellStyleForHeader);
			cellStyleForHeader.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyleForHeader.setVerticalAlignment(CellStyle.ALIGN_CENTER);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("C22:N22"));
			
//			RegionUtil.setBorderTop(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("C22:N22"), firstSheet, workbook);
//		    RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("C22:N22"), firstSheet, workbook);
//		    RegionUtil.setBorderRight(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("C22:N22"), firstSheet, workbook);
//		    RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("C22:N22"), firstSheet, workbook);
			
			
			// RATE OF WAGES/SALARY Header
			Row row15 = firstSheet.createRow(22);
			Cell header15 = row15.createCell((short)2);
			header15.setCellValue("RATE OF WAGES/SALARY");
			cellStyleForHeader.setFont(reportNameFont);
			header15.setCellStyle(cellStyleForHeader);
			cellStyleForHeader.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyleForHeader.setVerticalAlignment(CellStyle.ALIGN_CENTER);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("C23:N23"));
			
//			RegionUtil.setBorderTop(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("C23:N23"), firstSheet, workbook);
//		    RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("C23:N23"), firstSheet, workbook);
//		    RegionUtil.setBorderRight(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("C23:N23"), firstSheet, workbook);
//		    RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("C23:N23"), firstSheet, workbook);
			
		    Row row16 = firstSheet.createRow(23);
			List header16 = reportData.get(4);
			DataStyle ds14 = (DataStyle)header16.get(0);

			// Salary details Header Table
			for(int i=0,y=2;i<header16.size();i++,y++){
				Cell headerCell = row16.createCell(y);
				ds14 = (DataStyle)header16.get(i);
				headerCell.setCellValue("  "+ds14.getStrData()+"  ");  
				cellStyleForHeader = workbook.createCellStyle();
				cellStyleForHeader.setBorderBottom(ds14.getBorderStyle());
				cellStyleForHeader.setBorderLeft(ds14.getBorderStyle());
				cellStyleForHeader.setBorderRight(ds14.getBorderStyle());
				cellStyleForHeader.setBorderTop(ds14.getBorderStyle());
				cellStyleForHeader.setAlignment(ds14.getCellDataAlign());
				cellStyleForHeader.setFillForegroundColor(ds14.getHSSFbackRoundColor());
				cellStyleForHeader.setFillPattern(ds14.getFillPattern());
				cellStyleForHeader.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
				cellStyleForHeader.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
				cellStyleForHeader.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
				cellStyleForHeader.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);

				cellStyleForHeader.setFont(font);
				firstSheet.autoSizeColumn((short)y);
				headerCell.setCellStyle(cellStyleForHeader);
			}
			
			// Salary details data Table 
			for (int j = 1; j < nReportSize; j++) {
				Row row = firstSheet.createRow(24);
				List<DataStyle> userData = reportData.get(4);
				if(userData == null) userData = new ArrayList();
				
				int nUserDataSize = userData.size();
				for (int k = 0, l=2; k < nUserDataSize; k++,l++) {
					Cell cell = row.createCell(l);
					 ds14 = (DataStyle)userData.get(k);
					cell.setCellValue("salary data"+k+"");
//					cell.setCellValue("test data"+ds.getStrData()+"");	
					cellStyleForData.setBorderTop(ds14.getBorderStyle());
					cellStyleForData.setBorderBottom(ds14.getBorderStyle());
					cellStyleForData.setBorderLeft(ds14.getBorderStyle());
					cellStyleForData.setBorderRight(ds14.getBorderStyle());
					cellStyleForData.setAlignment(ds14.getCellDataAlign());
					cell.setCellStyle(cellStyleForData);
					
				}
			}
			
			
			//Deducation header
			Row row17 = firstSheet.createRow(26);
			Cell header17 = row17.createCell((short)2);
			header17.setCellValue("DEDUCTIONS");
			cellStyleForHeader.setFont(reportNameFont);
			header17.setCellStyle(cellStyleForHeader);
			cellStyleForHeader.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyleForHeader.setVerticalAlignment(CellStyle.ALIGN_CENTER);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("C27:N27"));
			
			RegionUtil.setBorderTop(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("C27:N27"), firstSheet, workbook);
		    RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("C27:N27"), firstSheet, workbook);
		    RegionUtil.setBorderRight(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("C27:N27"), firstSheet, workbook);
		    RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("C27:N27"), firstSheet, workbook);
			
		    Row row18 = firstSheet.createRow(27);
			List header18= reportData.get(5);
			DataStyle ds15 = (DataStyle)header18.get(0);

			//Deduction header table 
			for(int i=0,y=2;i<header18.size();i++,y++){
				Cell headerCell = row18.createCell(y);
				ds15 = (DataStyle)header18.get(i);
				headerCell.setCellValue("  "+ds15.getStrData()+"  ");  
				cellStyleForHeader = workbook.createCellStyle();
				cellStyleForHeader.setBorderBottom(ds15.getBorderStyle());
				cellStyleForHeader.setBorderLeft(ds15.getBorderStyle());
				cellStyleForHeader.setBorderRight(ds15.getBorderStyle());
				cellStyleForHeader.setBorderTop(ds15.getBorderStyle());
				cellStyleForHeader.setAlignment(ds15.getCellDataAlign());
				cellStyleForHeader.setFillForegroundColor(ds15.getHSSFbackRoundColor());
				cellStyleForHeader.setFillPattern(ds15.getFillPattern());
				cellStyleForHeader.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
				cellStyleForHeader.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
				cellStyleForHeader.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
				cellStyleForHeader.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);

				cellStyleForHeader.setFont(font);
				firstSheet.autoSizeColumn((short)y);
				headerCell.setCellStyle(cellStyleForHeader);
			}
			// deducion data table
			for (int j = 1; j < nReportSize; j++) {
				Row row = firstSheet.createRow(28);
				List<DataStyle> userData = reportData.get(5);
				if(userData == null) userData = new ArrayList();
				
				int nUserDataSize = userData.size();
				for (int k = 0, l=2; k < nUserDataSize; k++,l++) {
					Cell cell = row.createCell(l);
					ds15 = (DataStyle)userData.get(k);
					cell.setCellValue("deduction data"+k+"");
//								cell.setCellValue("test data"+ds.getStrData()+"");	
					cellStyleForData.setBorderTop(ds15.getBorderStyle());
					cellStyleForData.setBorderBottom(ds15.getBorderStyle());
					cellStyleForData.setBorderLeft(ds15.getBorderStyle());
					cellStyleForData.setBorderRight(ds15.getBorderStyle());
					cellStyleForData.setAlignment(ds15.getCellDataAlign());						
					cell.setCellStyle(cellStyleForData);
					
				}
			}

			Row row19 = firstSheet.createRow(32);
			Cell header19 = row19.createCell(5);
			header19.setCellValue("Signature of the Occupier/Principal Employer/Authorized Signatory");
			cellStyleForHeader.setFont(reportNameFont);
			header19.setCellStyle(cellStyleForHeader);
			cellStyleForHeader.setAlignment(CellStyle.ALIGN_LEFT);
			cellStyleForHeader.setVerticalAlignment(CellStyle.ALIGN_LEFT);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("F33:I33"));
//			RegionUtil.setBorderTop(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("F33:I33"), firstSheet, workbook);
//		    RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("F33:I33"), firstSheet, workbook);
//		    RegionUtil.setBorderRight(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("F33:I33"), firstSheet, workbook);
//		    RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, CellRangeAddress.valueOf("F33:I33"), firstSheet, workbook);
		    
		    
		    // sheet boarder
		    firstSheet.addMergedRegion(CellRangeAddress.valueOf("B2:Z35"));
//			RegionUtil.setBorderTop(CellStyle.BORDER_MEDIUM, CellRangeAddress.valueOf("B2:Z35"), firstSheet, workbook);
//		    RegionUtil.setBorderLeft(CellStyle.BORDER_MEDIUM, CellRangeAddress.valueOf("B2:Z35"), firstSheet, workbook);
//		    RegionUtil.setBorderRight(CellStyle.BORDER_MEDIUM, CellRangeAddress.valueOf("B2:Z35"), firstSheet, workbook);
//		    RegionUtil.setBorderBottom(CellStyle.BORDER_MEDIUM, CellRangeAddress.valueOf("B2:Z35"), firstSheet, workbook);
			

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			  try {
				 FileOutputStream fileOutputStream = new FileOutputStream("C:\\temp\\SalarySlip.xls");
		   
			  System.out.println("Excel File has been created successfully.");   
			  
			
				workbook.write(fileOutputStream);
				buffer.close();
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	

	public static void createExcelFile(HSSFWorkbook workbook) throws Exception {
	
//		if(getExcelType() != null) {
//			writeExcelSheetReport();
//		} else {
//			writeSheetReport();
//		}
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			workbook.write(buffer);
			buffer.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		

//		response.setHeader("Content-Disposition", "attachment; filename=\"" + getDownloadreportname() + ".xls\"");
//		response.setContentType("application/vnd.ms-excel:UTF-8");
//		response.setContentLength(buffer.size());
//		
//
//		try {
//			ServletOutputStream op = response.getOutputStream();
//			op = response.getOutputStream();
//			op.write(buffer.toByteArray());
//			op.flush();
//			op.close();
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	
	}
	
	

	
	
	public String getReportName(){
		String name = "Updated Employee Sheet";
		return name;
	}


//	HttpServletRequest request;
//	HttpServletResponse response;

	/*@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}*/
//	public String getDownloadreportname() {
//		return downloadreportname;
//	}
//
//	public void setDownloadreportname(String downloadreportname) {
//		this.downloadreportname = downloadreportname;
//	}

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
	


