package com.konnect.jpms.export;

    
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;

public class ExportPdfReport implements ServletRequestAware,ServletResponseAware {

	String FILE = "/home/dailyhrz/Desktop/PdfReports.pdf";
	String strDocumentName;
	public String getStrDocumentName() {
		return strDocumentName;
	}

	public void setStrDocumentName(String strDocumentName) {
		this.strDocumentName = strDocumentName;
	}

	Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
	Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);

	HttpSession session;
	
	public void execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		

		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		try {
			
			
			
			Document document = new Document(PageSize.LETTER.rotate());
       	PdfWriter.getInstance(document, buffer);
		//	PdfWriter.getInstance(document, new FileOutputStream(FILE));
			document.open();
			
			
			Paragraph preface = new Paragraph();
			preface.setAlignment(Element.ALIGN_CENTER);

			List<List<String>> allData = (List)session.getAttribute("reportListExport");
			List<Float> elementsizelist=(List<Float>) session.getAttribute("elementsizelist");

			
			createReport(preface, allData,elementsizelist);
			document.add(preface);
			
			
			document.close();
	//	String strDocumentName = "Report";
		
		if(strDocumentName!=null){
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=" + getStrDocumentName() + ".pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
				out.close();
		}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void createReport(Paragraph preface,java.util.List<java.util.List<String>> reportData, List<Float> elementsizelist) {
		java.util.List colHeading = reportData.get(0);

		//setting coloumn width
		float[] columnWidths = new float[elementsizelist.size()];
		/*columnWidths[0]=3.5f;		
		columnWidths[1]=4f;*/
		for(int i=0;i<elementsizelist.size();i++){
			columnWidths[i]=elementsizelist.get(i)*1f;	
		}
		
		PdfPTable table = new PdfPTable(columnWidths);
		table.setWidthPercentage(100); 

		createRowHeading(preface, table, colHeading);

		for (int i = 1; i < reportData.size(); i++) {
			java.util.List<String> data = reportData.get(i);
			insertingData(table, data);
		}

		preface.add(table);
	}

	public void createRowHeading(Paragraph preface, PdfPTable table,java.util.List colHeading) {

		
		
		DataStyle ds = (DataStyle) colHeading.get(0); // Extracting the Name of the report.
		
//		ds = (DataStyle)colHeading.get(0);
		
		
		
		String reportName = ds.getStrData();
		setStrDocumentName(reportName);
		preface.add(reportName);
		preface.setAlignment(ds.getStrAlign());
				  
		for (int i = 1; i < colHeading.size(); i++) {
			ds = (DataStyle)colHeading.get(i);
			String columnName = ds.getStrData();		//colHeading.get(i);
			if(columnName.contains("/")){
			PdfPCell headingCell = new PdfPCell(new Paragraph(columnName.substring(0,2), heading));
			headingCell.setBackgroundColor(ds.getBackRoundColor());
			//headingCell.setHorizontalAlignment(horizontalAlignment)
			headingCell.setHorizontalAlignment(ds.getStrAlign());
			table.addCell(headingCell);
			}
			else{
				PdfPCell headingCell = new PdfPCell(new Paragraph(columnName, heading));
				headingCell.setBackgroundColor(ds.getBackRoundColor());
				//headingCell.setHorizontalAlignment(horizontalAlignment)
				headingCell.setHorizontalAlignment(ds.getStrAlign());
				table.addCell(headingCell);
			}
		
			//headingCell.set
//			System.out.println("= H ===>"+ds.getStrData());
			
		}

	}

	public void insertingData(PdfPTable table, java.util.List reportData) {

		for (int i = 0; i < reportData.size(); i++) {
			 DataStyle ds = (DataStyle)reportData.get(i);
			 PdfPCell dataCell = new PdfPCell(new Paragraph(ds.getStrData(), normal));
			 dataCell.setBackgroundColor(ds.getBackRoundColor());
			 dataCell.setHorizontalAlignment(ds.getStrAlign());
			/*Object checkType = reportData.get(i);
			String data = null;
			data = String.valueOf(reportData.get(i));
			PdfPCell dataCell = new PdfPCell(new Paragraph(data, normal));

			if (i != 0 && checkType.getClass().equals(Integer.class)) {
				dataCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			}*/
			 
//			 System.out.println("= D ===>"+ds.getStrData());
			 
			table.addCell(dataCell);
		}

	}

	public List<List<String>> getEmployeeDetails() {
		Connection con = null;
		PreparedStatement pstat = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		List allData = new ArrayList();
		try {
			con = db.makeConnection(con);

			pstat = con.prepareStatement("SELECT * from employee_info");
			rs = pstat.executeQuery();

			List heading = new ArrayList();
			heading.add(new DataStyle("Employee Info",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			heading.add(new DataStyle("Emp Id",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			heading.add(new DataStyle("First Name",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			heading.add(new DataStyle("Middle Name",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			heading.add(new DataStyle("Last Name",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			heading.add(new DataStyle("Designation",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			heading.add(new DataStyle("Experience",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			heading.add(new DataStyle("Salary",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));

			allData.add(0, heading);

			while (rs.next()) {
				List records = new ArrayList();
				
				String empId = String.valueOf(rs.getInt("emp_id"));
				records.add(new DataStyle(empId,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
				String firstName = rs.getString("first_name");
				records.add(new DataStyle(firstName,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
								
				String middleName = rs.getString("middle_name"); 
				records.add(new DataStyle(middleName,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
								
				String lastName = rs.getString("last_name"); 
				records.add(new DataStyle(lastName,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
				String designation = rs.getString("designation"); 
				records.add(new DataStyle(designation,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
				String experience = String.valueOf(rs.getInt("experience"));
				records.add(new DataStyle(experience,Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
				String salary = String.valueOf(rs.getInt("salary"));
				records.add(new DataStyle(salary,Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));

				allData.add(records);
			}
			rs.close();
			pstat.close();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			db.closeConnection(con);
			db.closeStatements(pstat);
			db.closeResultSet(rs);
		}

		return allData;

	}

	HttpServletRequest request;
	HttpServletResponse response;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
		
	}
	
}