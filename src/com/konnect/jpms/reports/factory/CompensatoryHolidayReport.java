package com.konnect.jpms.reports.factory;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class CompensatoryHolidayReport extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CompensatoryHolidayReport.class);

	public String execute() throws Exception {

		request.setAttribute(PAGE, PCompensatroyHolidayReport);
		request.setAttribute(TITLE, "Compensatory Holiday Register");

		
		
		viewCompensatroyHolidayReport();
		return loadCompensatroyHolidayReport();

	}

	public String loadCompensatroyHolidayReport() {

		return LOAD;
	}

	public String viewCompensatroyHolidayReport() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();

			
			List<List<String>> alPdf = new ArrayList<List<String>>();
			List<String> alInnerPdf = new ArrayList<String>();
			
			con = db.makeConnection(con);

			

			pst = con.prepareStatement("select * from employee_personal_details order by emp_fname, emp_lname");
			rs = pst.executeQuery();

			int nCount = 0;
			while (rs.next()) {
				alInner.add(""+ ++nCount);
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
			}
			rs.close();
			pst.close();

			request.setAttribute("reportList", al);
			
			
			if(getPdfGeneration()!=null && getPdfGeneration().equalsIgnoreCase("true")){
				

				
			PdfCompensatoryHolidayReport objPdf = new PdfCompensatoryHolidayReport(alPdf,response);
			objPdf.exportPdf();
			
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
		
		
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	String pdfGeneration;

	public String getPdfGeneration() {
		return pdfGeneration;
	}

	public void setPdfGeneration(String pdfGeneration) {
		this.pdfGeneration = pdfGeneration;
	}

	HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {

		this.response=response;
	}

}


class PdfCompensatoryHolidayReport{
	
	
	private Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
	private Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
	private Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
	private Font small = new Font(Font.FontFamily.TIMES_ROMAN,7);
	private Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
	
	List reportList;
	PdfCompensatoryHolidayReport(List reportList, HttpServletResponse response){
		this.reportList = reportList;
		this.response=response;
	}
	
	public void exportPdf(){
		
		
		
		 ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
		 Document document = new Document(PageSize.LETTER.rotate());
		
		 try{
				PdfWriter.getInstance(document, bos);
				
					document.open();
					
				Paragraph blankSpace = new Paragraph(" ");
				Paragraph title = new Paragraph("FORM - 9",heading);
				title.setAlignment(Element.ALIGN_CENTER);
				Paragraph subTitle = new Paragraph("(Prescribed under Rule 103)",heading);
				subTitle.setAlignment(Element.ALIGN_CENTER);
				Paragraph registerName = new Paragraph("REGISTER OF COMPENSATORY HOLIDAYS",normalwithbold);
				registerName.setAlignment(Element.ALIGN_CENTER);
				
				PdfPTable table = new PdfPTable(16);
				table.setWidthPercentage(100);
				
				int[] cols = {6,8,6,6,6,6,6,6,6,6,6,6,6,6,8,6};
				table.setWidths(cols);
				
				for(int i=0;i<16;i++){
					if(i < 7 || i > 14){
						PdfPCell cell1 = new PdfPCell(new Paragraph(" ",normal));
						table.addCell(cell1);
					}					
					if(i==7){
						PdfPCell cell2 = new PdfPCell(new Paragraph("Weekly rest day lost due to the exempting order in",normal));
						cell2.setColspan(4);
						table.addCell(cell2);
					}
					if(i==11){
						PdfPCell cell3 = new PdfPCell(new Paragraph("Date of compensatory holidays given to",normal));
						cell3.setColspan(4);
						table.addCell(cell3);
					}
				}
				
				List<String> heading = getHeadings();
				for(int i=0;i<heading.size();i++){
					PdfPCell cell4 = new PdfPCell(new Paragraph(heading.get(i),normal));
					table.addCell(cell4);
				}
				
				for(int i=1;i<=16;i++){
					PdfPCell cell5 = new PdfPCell(new Paragraph(""+i,normal));
					cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell5);
				}
				
				for(int i=0;i<320;i++){
					PdfPCell cell6 = new PdfPCell(new Paragraph(" ",normal));
					cell6.disableBorderSide(Rectangle.TOP);
					cell6.disableBorderSide(Rectangle.BOTTOM);
					table.addCell(cell6);
				}
				
				for(int i=0;i<16;i++){
					PdfPCell cell7 = new PdfPCell(new Paragraph(" ",normal));
					cell7.disableBorderSide(Rectangle.TOP);
					table.addCell(cell7);
				}
				
				document.add(title);
				document.add(subTitle);
				document.add(registerName);
				document.add(blankSpace);
				document.add(table);
				
				
				document.close();
				
				
				response.setContentType("application/pdf");         
				 response.setContentLength(bos.size());
				 response.setHeader("Content-Disposition", "attachment; filename=CompensatoryHolidayReport.pdf");
				
				 ServletOutputStream out = response.getOutputStream();         
				 bos.writeTo(out);         
				 out.flush();      
				 bos.close();
				 out.close();
					
		}catch(Exception e){
			e.printStackTrace();
		}
	
		
	}
	public List<String> getHeadings(){
		List<String> headings = new ArrayList<String>();
		headings.add("Sl.No.");
		headings.add("Number in the register of workers");
		headings.add("Name");
		headings.add("Group of Relay No.");
		headings.add("No. and date of exempting order");
		headings.add("Year");
		headings.add("January to March");
		headings.add("April to June");
		headings.add("July to September");
		headings.add("October to December");
		headings.add("January to March");
		headings.add("April to June");
		headings.add("July to September");
		headings.add("October to December");
		headings.add("Lost rest days carried to the next year");
		headings.add("Remarks");
		
		return headings;
	}
	private HttpServletResponse response;
	
}


