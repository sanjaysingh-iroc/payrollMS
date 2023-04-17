package com.konnect.jpms.document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;


/**
 * @author swara
 *
 */
public class DownloadBankStatement implements ServletRequestAware,ServletResponseAware, IStatements {

	/**
	 * 
	 */
	

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strSessionUserId;
	
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
//		Row headerRow = firstSheet.createRow(rownum);
//		headerRow.setHeightInPoints(40);
		HSSFPalette pallet = workbook.getCustomPalette();
		pallet.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index, (byte)230, (byte)225, (byte)225);
	}
	
	public void execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if(CF==null)return "login";
		strSessionUserId = (String)session.getAttribute(EMPID);
		
		if(getType() !=null && getType().equalsIgnoreCase("Pdf")) {
			downloadDocument();
		} else if(getType() !=null && getType().equalsIgnoreCase("Excel")) {
			downloadExcelDocument();
		} else if(getType() !=null && getType().equalsIgnoreCase("ExcelBU")) {
			downloadBankUploaderExcelDocument();
		}
		
//		loadDownloadDocument();
//		return "load";
	}

	private void downloadBankUploaderExcelDocument() {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {    

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from payroll_bank_statement where statement_id =?");
			pst.setInt(1, uF.parseToInt(getDoc_id()));
			rs = pst.executeQuery();
			String strDocumentName = null;
			String strDocumentContent = null;
			while (rs.next()) {
				strDocumentContent = rs.getString("bank_uploader_excel");
				strDocumentName = rs.getString("statement_name");
			}
			rs.close();
			pst.close();
			
			if(strDocumentName!=null) {
				strDocumentName = strDocumentName.replace(" ", "");
			}
			
			List<String> alOuter = new ArrayList<String>();
			if(strDocumentContent !=null) {
				alOuter = Arrays.asList(strDocumentContent.split(":_:"));
			}
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			for(int i=0; alOuter!=null && i<alOuter.size(); i++) {
				List<String> alInner = Arrays.asList(alOuter.get(i).split("::"));
				List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
				for(int j=0; j<alInner.size(); j++) {
					alInnerExport.add(new DataStyle(alInner.get(j), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				}
//				alInnerExport.add(new DataStyle(alInner.get(0), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(alInner.get(1), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(alInner.get(2), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(alInner.get(3), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(alInner.get(4), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
//				if(alInner.size()>5) {
//					alInnerExport.add(new DataStyle(alInner.get(5), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
//				}
				reportListExport.add(alInnerExport);
			}
			
			if(reportListExport !=null && reportListExport.size()>0) {
				session.setAttribute("reportListExport", reportListExport);
				createBankUploaderExcelFile(workbook, strDocumentName);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	String doc_id;
	String type;
	
	public String loadDownloadDocument() {

		return "load";
	}

	private void downloadExcelDocument() {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {    

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from payroll_bank_statement where statement_id =?");
			pst.setInt(1, uF.parseToInt(getDoc_id()));
			rs = pst.executeQuery();
			String strDocumentName = null;
			String strDocumentContent = null;
			while (rs.next()) {
				strDocumentContent = rs.getString("statement_body_excel");
				strDocumentName = rs.getString("statement_name");
			}
			rs.close();
			pst.close();
			
//			System.out.println("strDocumentContent=="+strDocumentContent);
			if(strDocumentName!=null) {
				strDocumentName = strDocumentName.replace(" ", "");
			}
			
			List<String> alOuter = new ArrayList<String>();
			if(strDocumentContent !=null) {
				alOuter = Arrays.asList(strDocumentContent.split(":_:"));
			}
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			for(int i=0; alOuter!=null && i<alOuter.size(); i++) {
				List<String> alInner = Arrays.asList(alOuter.get(i).split("::"));
				List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle(alInner.get(0), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(alInner.get(1), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(alInner.get(2), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(alInner.get(3), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(alInner.get(4), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
//				System.out.println("DBS/217---alInner=="+alInner.size());
				if(alInner.size()>5) {
					alInnerExport.add(new DataStyle(alInner.get(5), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				}
				if(alInner.size()>6) {
					alInnerExport.add(new DataStyle(alInner.get(6), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				}
				reportListExport.add(alInnerExport);
			}
			
			if(reportListExport !=null && reportListExport.size()>0) {
				session.setAttribute("reportListExport", reportListExport);
				createExcelFile(workbook, strDocumentName);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
public void createBankUploaderExcelFile(HSSFWorkbook workbook, String strDocumentName) throws Exception {
		
	writeBankUploaderExcelSheetReport();
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			workbook.write(buffer);
			buffer.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		

		response.setHeader("Content-Disposition", "attachment; filename=\"" + strDocumentName + ".xls\"");
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

	
	public void createExcelFile(HSSFWorkbook workbook, String strDocumentName) throws Exception {
		
		writeExcelSheetReport();
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			workbook.write(buffer);
			buffer.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		

		response.setHeader("Content-Disposition", "attachment; filename=\"" + strDocumentName + ".xls\"");
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
	
	
	public void writeExcelSheetReport() throws Exception {
		
		List<List<String>> reportData = (List)session.getAttribute("reportListExport");
		try {
			int nReportSize = reportData.size();
//			System.out.println("reportData ========>> " + reportData);
			Row headerRow = firstSheet.createRow(1);
			Row reportNameRow = firstSheet.createRow(0);
			int rowCnt = 2;
			cellStyleForReportName = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBoldweight((short)1200);	
			
			Font reportNameFont = workbook.createFont();
			reportNameFont.setBoldweight((short)1200);							
			
			List header = reportData.get(0);
			DataStyle ds = (DataStyle)header.get(0);
			
			//Cell reportName = reportNameRow.createCell(header.size()/2);
			Cell reportName = reportNameRow.createCell((short)0);
			reportName.setCellValue(ds.getStrData());
			cellStyleForReportName.setFont(reportNameFont);
//				cellStyleForReportName.setFillPattern(HSSFCellStyle.NO_FILL);
//				cellStyleForReportName.setFillBackgroundColor(ds.getBackRoundColor());
			reportName.setCellStyle(cellStyleForReportName);
			firstSheet.addMergedRegion(CellRangeAddress.valueOf("A1:F1"));
			
			// This portion is used to print the header values.
			for(int i=1,y=0;i<header.size();i++,y++){
				Cell headerCell = headerRow.createCell(y);
				ds = (DataStyle)header.get(i);
			//===start parvez date: 16-08-2022===	
				headerCell.setCellValue(ds.getStrData());
			//===end parvez date: 16-08-2022===	
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
			for (int j = 1; j < nReportSize; j++) {
				Row row = firstSheet.createRow(rowCnt);
				List userData = reportData.get(j);
				if(userData == null) userData = new ArrayList();
				
				int nUserDataSize = userData.size();
				for (int k = 0, l=0; k < nUserDataSize; k++,l++) {
					Cell cell = row.createCell(l);
					ds = (DataStyle)userData.get(k);
		//			System.out.println("data = " + "Row num = " + rownum + "  "+ ds.getCellDataAlign());
				//===start parvez date: 16-08-2022===	
					cell.setCellValue(ds.getStrData());	
				//===end parvez date: 16-08-2022===	
					cellStyleForData = workbook.createCellStyle();
					//cellStyleForData.setIndention((short)1);
					cellStyleForData.setBorderTop(ds.getBorderStyle());
					cellStyleForData.setBorderBottom(ds.getBorderStyle());
					cellStyleForData.setBorderLeft(ds.getBorderStyle());
					cellStyleForData.setBorderRight(ds.getBorderStyle());
					cellStyleForData.setAlignment(ds.getCellDataAlign());						
					
//					firstSheet.autoSizeColumn((short)l);
					cell.setCellStyle(cellStyleForData);
					
				}
				rowCnt++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void writeBankUploaderExcelSheetReport() throws Exception {
		
		List<List<String>> reportData = (List)session.getAttribute("reportListExport");
		try {
			int nReportSize = reportData.size();
//			System.out.println("reportData ========>> " + reportData);
			Row headerRow = firstSheet.createRow(0);
//			Row reportNameRow = firstSheet.createRow(0);
//			int rowCnt = 2;
			cellStyleForReportName = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBoldweight((short)1200);	
			
			Font reportNameFont = workbook.createFont();
			reportNameFont.setBoldweight((short)1200);							
			
			List header = reportData.get(0);
			DataStyle ds = (DataStyle)header.get(0);
			
			//Cell reportName = reportNameRow.createCell(header.size()/2);
//			Cell reportName = reportNameRow.createCell((short)0);
//			reportName.setCellValue(ds.getStrData());
//			cellStyleForReportName.setFont(reportNameFont);
//				cellStyleForReportName.setFillPattern(HSSFCellStyle.NO_FILL);
//				cellStyleForReportName.setFillBackgroundColor(ds.getBackRoundColor());
//			reportName.setCellStyle(cellStyleForReportName);
//			firstSheet.addMergedRegion(CellRangeAddress.valueOf("A1:E1"));
			
			// This portion is used to print the header values.
			for(int y=0; y<header.size(); y++){
				Cell headerCell = headerRow.createCell(y);
				ds = (DataStyle)header.get(y);
			//===start parvez date: 16-08-2022===	
				headerCell.setCellValue(ds.getStrData());  
			//===start parvez date: 16-08-2022===	
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
			for (int j = 1; j < nReportSize; j++) {
				Row row = firstSheet.createRow(j);
				List userData = reportData.get(j);
				if(userData == null) userData = new ArrayList();
				
				int nUserDataSize = userData.size();
				for (int k = 0, l=0; k < nUserDataSize; k++,l++) {
					Cell cell = row.createCell(l);
					ds = (DataStyle)userData.get(k);
		//			System.out.println("data = " + "Row num = " + rownum + "  "+ ds.getCellDataAlign());
				//===start parvez date: 16-08-2022===	
					cell.setCellValue(ds.getStrData());	
				//===end parvez date: 16-08-2022===	
					cellStyleForData = workbook.createCellStyle();
					//cellStyleForData.setIndention((short)1);
					cellStyleForData.setBorderTop(ds.getBorderStyle());
					cellStyleForData.setBorderBottom(ds.getBorderStyle());
					cellStyleForData.setBorderLeft(ds.getBorderStyle());
					cellStyleForData.setBorderRight(ds.getBorderStyle());
					cellStyleForData.setAlignment(ds.getCellDataAlign());						
//					firstSheet.autoSizeColumn((short)l);
					cell.setCellStyle(cellStyleForData);
					
				}
//				rowCnt++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String downloadDocument() {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {    

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from payroll_bank_statement where statement_id =?");
			pst.setInt(1, uF.parseToInt(getDoc_id()));
			rs = pst.executeQuery();
			String strDocumentName = null;
			String strDocumentContent = null;
			while (rs.next()) {
				strDocumentContent = rs.getString("statement_body");
				strDocumentName = rs.getString("statement_name");
			}
			rs.close();
			pst.close();
			
			if(strDocumentName!=null) {
				strDocumentName = strDocumentName.replace(" ", "");
			}
			
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications NF = new Notifications(0, CF);
			NF.setDomain(strDomain);
			NF.request = request;
			NF.setStrHostAddress(CF.getStrEmailLocalHost());
			NF.setStrHostPort(CF.getStrHostPort());
			NF.setStrContextPath(request.getContextPath());
			NF.setEmailTemplate(false);
			
			Map<String, String> hmParsedContent = null;
			
			if(strDocumentContent!=null){
				
				hmParsedContent  = NF.parseContent(strDocumentContent, "", "");
				String strDocument = hmParsedContent.get("MAIL_BODY");
//				if(strDocument!=null) {
//					strDocument = strDocument.replaceAll("<br/>", "");
//					strDocument = strDocument.replaceAll("<br />", ""); 
//				}
//				System.out.println("strDocument ====>>> " + strDocument);
				
				Document document = new Document(PageSize.A4,40, 40, 10, 60); 
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				PdfWriter writer = PdfWriter.getInstance(document, buffer);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent("","");
			    writer.setPageEvent(event); 
				
				document.open();
				HTMLWorker hw = new HTMLWorker(document);
				hw.parse(new StringReader(strDocument));
				
				document.close();  
				
				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=" + strDocumentName + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush(); 
				buffer.close();
				out.close();
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "update";
	}
	

//	public String downloadDocument() {
//
//		Connection con = null;
//		ResultSet rs = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {    
//
//			con = db.makeConnection(con);
//			
//			
//			
//			
//			pst = con.prepareStatement("select * from payroll_bank_statement where statement_id =?");
//			pst.setInt(1, uF.parseToInt(getDoc_id()));
//			rs = pst.executeQuery();
//			String strDocumentName = null;
//			String strDocumentContent = null;
//			while (rs.next()) {
//				strDocumentContent = rs.getString("statement_body");
//				strDocumentName = rs.getString("statement_name");
//			}
//			rs.close();
//			pst.close();
//			
//			
//			String strDomain = request.getServerName().split("\\.")[0];
//			Notifications NF = new Notifications(0, CF);
//			NF.setDomain(strDomain);
//			NF.request = request;
//			NF.setStrHostAddress(CF.getStrEmailLocalHost());
//			NF.setStrHostPort(CF.getStrHostPort());
//			NF.setStrContextPath(request.getContextPath());
//			NF.setEmailTemplate(false);
//			
//			Map<String, String> hmParsedContent = null;
//			
//			  
//			
//			Document document = new Document();
//			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//			
//			if(strDocumentContent!=null){
//				
//				hmParsedContent  = NF.parseContent(strDocumentContent, "", "");
//							
//				PdfWriter.getInstance(document, buffer);
//				document.open();
//				
//				List<Element> supList = HTMLWorker.parseToList(new StringReader(hmParsedContent.get("MAIL_BODY")), null);
//				Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
//				
////				phrase.add(supList.get(0));
////				System.out.println("supList.size()======>"+supList.size()); 
////				for(int i=1; i<20; i++){
////				
////					if(supList.size()>i){
////						phrase.add(supList.get(i));
////					}else{
////						break;
////					}
////				}
//				phrase.add(supList.get(0));
//				System.out.println("supList.size()======>"+supList.size()); 
//				for(int i=1; i<supList.size(); i++){
//					phrase.add(supList.get(i));
//				}
//				
//				document.add(phrase);
//				
//				document.close();
//				
//			}
//			   
//			
//				response.setContentType("application/pdf");
//				response.setContentLength(buffer.size());
//				response.setHeader("Content-Disposition", "attachment; filename=" + strDocumentName + ".pdf");
//				ServletOutputStream out = response.getOutputStream();
//				buffer.writeTo(out);
//				out.flush();
//				buffer.close();
//				out.close();
//			
//			
//			
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return "update";
//	}

	
	public void validate() {

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getDoc_id() {
		return doc_id;
	}

	public void setDoc_id(String doc_id) {
		this.doc_id = doc_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
