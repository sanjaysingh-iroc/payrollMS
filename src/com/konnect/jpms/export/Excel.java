package com.konnect.jpms.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;

public class Excel {

	HSSFWorkbook hwb = null;
	HSSFSheet sheet = null;
	String filePath;
	
	public Excel(String filePath, String sheetName){
		
		hwb = new HSSFWorkbook();		
		sheet = hwb.createSheet(sheetName);
		this.filePath = filePath;
		
		addLogoImage();
	}
	
	public void addLogoImage() {
		try {
 
			InputStream is = new FileInputStream(filePath+File.separator+"logo1.png");
			
			byte[] bytes = IOUtils.toByteArray(is);
			int pictureIdx = hwb.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
			is.close();

			CreationHelper helper = hwb.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();
			anchor.setCol1(1);
			anchor.setRow1(1);
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			pict.resize();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void writeExcelFile(HttpServletResponse response, String fileName) {

		FileOutputStream fileOut = null;
		try {

			
			
			
			ServletOutputStream op = response.getOutputStream();
			response.setContentType("application/vnd.ms-excel");
			response.setContentLength(hwb.getBytes().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			op.write(hwb.getBytes());
			op.flush();
			op.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	public void addHeaders(String... headerValue ){
		
		try {
			
			HSSFRow rowhead = null;				
			HSSFCell cell = null;
			rowhead = sheet.createRow(4);
			
			for(int x=0; x<headerValue.length; x++){
				cell = rowhead.createCell(x+1);
				cell.setCellValue(headerValue[x]);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	};
	
}
