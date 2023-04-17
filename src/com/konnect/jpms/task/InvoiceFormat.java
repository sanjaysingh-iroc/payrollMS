package com.konnect.jpms.task;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class InvoiceFormat extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	
	private String type;
	
	private String fromPage;
	String strSessionEmpId;
	
	
	String operation;
	
	HttpSession session;
	
	CommonFunctions CF;
	
	List<FillInvoiceFormat> invoiceFormatList;
	String invoiceFormat;
	
	String invoiceFormatId;
	
	String invoiceFormatTitle;
	String section1;
	String section2;
	String section3;
	String section4;
	String section5;
	String section6;
	String section7;
	String section8;
	String section9;
	String section10;
	String section11;
	
	String submit;
	String delete; 
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/task/InvoiceFormat.jsp");
		request.setAttribute(TITLE, "Invoice Formats");
		
		UtilityFunctions uF = new UtilityFunctions();
		if (getOperation() != null && getOperation().equalsIgnoreCase("D")) {
			deleteInvoiceFormatData(uF);
			getInvoiceFormatDataBlank(uF);
		} else if (getInvoiceFormatId() != null && uF.parseToInt(getInvoiceFormatId()) > 0 && getOperation() != null && getOperation().equalsIgnoreCase("U")) {
			//if(getSubmit() != null) {
				updateInvoiceFormat(uF);
				getInvoiceFormatDataBlank(uF);
			//}
		} else if (getOperation() == null || getOperation().equals("")) {
			if(getSubmit() != null)
				insertInvoiceFormat(uF);
		}
		
		
		getInvoiceFormatData(uF);
		if (getInvoiceFormat() != null && uF.parseToInt(getInvoiceFormat()) > 0 && getType() != null && getType().equalsIgnoreCase("pdf")){
			previewInvoice(uF);
		}
		
		invoiceFormatList = new FillInvoiceFormat(request).fillFillInvoiceFormat();
		
		return SUCCESS;

	}
	
	
	private void previewInvoice(UtilityFunctions uF) {
		try{
			
			Map<String, String> hmInvoiceContent = (Map<String, String>) request.getAttribute("hmInvoiceContent");
			
			if(hmInvoiceContent!=null && hmInvoiceContent.size()>0){
				ByteArrayOutputStream buffer = generateInvoicePreviewPdf(uF,hmInvoiceContent);
				ServletOutputStream out = response.getOutputStream();
				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=Invoice_Preview.pdf");
				buffer.writeTo(out);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private ByteArrayOutputStream generateInvoicePreviewPdf(UtilityFunctions uF, Map<String, String> hmInvoiceContent) {

		System.out.println("generateInvoicePreviewPdf ======");
		Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
		Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
		Font normal1 = new Font(Font.FontFamily.TIMES_ROMAN, 9);
		Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
		Font small = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font small1 = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
		Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try {

			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, buffer);
			document.open();

			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			// table.setExtendLastRow(true);
			table.setFooterRows(25);

			PdfPCell row1 = new PdfPCell(new Paragraph("", small));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT | Rectangle.TOP);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("  Original  /  Cenvat Copy  /  Office Copy  ", small));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setBorder(Rectangle.RIGHT | Rectangle.TOP);
			row1.setPadding(2.5f);
			row1.setColspan(2);
			table.addCell(row1);

			// New Row

			String strelement = uF.showData(hmInvoiceContent.get("SECTION_1"), "");
			List<Element> al = HTMLWorker.parseToList(new StringReader(strelement), null);
			Paragraph pr = new Paragraph("",small);
			pr.addAll(al);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);  
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al2 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceContent.get("SECTION_2"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al2);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al3 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceContent.get("SECTION_3"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al3);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			List<Element> al4 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceContent.get("SECTION_4"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al4);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al5 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceContent.get("SECTION_5"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al5);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setBorder(Rectangle.NO_BORDER);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al6 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceContent.get("SECTION_6"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al6);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(2);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			
			// New Row
			row1 = new PdfPCell(new Paragraph("P A R T I C U L A R S", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("AMOUNT ", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("AMOUNT ", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			
			
			// New Row
			for(int i = 0; i < 60; i++){
				row1 = new PdfPCell(new Paragraph("", small));
				row1.setHorizontalAlignment(Element.ALIGN_LEFT);
				row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
				row1.setColspan(4);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				row1 = new PdfPCell(new Paragraph("", small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);
				
				row1 = new PdfPCell(new Paragraph("", small));
				row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				row1.setBorder(Rectangle.RIGHT);
				row1.setPadding(2.5f);
				table.addCell(row1);
			}
			

			// New Row
			row1 = new PdfPCell(new Paragraph("TOTAL " , smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setColspan(4);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
			row1.setPadding(2.5f);
			table.addCell(row1);

			row1 = new PdfPCell(new Paragraph("", smallBold));
			row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			row1.setPadding(2.5f);
			table.addCell(row1);

			// New Row
			List<Element> al7 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceContent.get("SECTION_7"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al7);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al8 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceContent.get("SECTION_8"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al8);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			List<Element> al9 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceContent.get("SECTION_9"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al9);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			List<Element> al10 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceContent.get("SECTION_10"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al10);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_LEFT);
			row1.setBorder(Rectangle.RIGHT);
			row1.setColspan(3);
			row1.setPadding(2.5f);
			table.addCell(row1);
			
			// New Row
			List<Element> al11 =HTMLWorker.parseToList(new StringReader(uF.showData(hmInvoiceContent.get("SECTION_11"), "")), null);
			pr = new Paragraph("",small);
			pr.addAll(al11);
			row1 =new PdfPCell(new Paragraph(pr));
			row1.setHorizontalAlignment(Element.ALIGN_CENTER);
//			row1.setBorder(Rectangle.LEFT);
			row1.setColspan(6);
			row1.setPadding(2.5f);
			row1.setFixedHeight(0f);
			table.addCell(row1);
			
			document.add(table);

			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer;
	}


	private void getInvoiceFormatDataBlank(UtilityFunctions uF) {
		setInvoiceFormatId("");
		setInvoiceFormatTitle("");
		setSection1("");
		setSection2("");
		setSection3("");
		setSection4("");
		setSection5("");
		setSection6("");
		setSection7("");
		setSection8("");
		setSection9("");
		setSection10("");
		setSection11("");
	}


	private void deleteInvoiceFormatData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update invoice_formats set is_delete = true where invoice_formats_id=? ");
			pst.setInt(1, uF.parseToInt(getInvoiceFormatId()));
			pst.executeUpdate();
//			System.out.println("pst ===>> " + pst);
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getInvoiceFormatData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmInvoiceContent = new LinkedHashMap<String, String>();			
			pst = con.prepareStatement("select * from invoice_formats where invoice_formats_id=? and is_delete = false");
			pst.setInt(1, uF.parseToInt(getInvoiceFormat()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				setInvoiceFormatId(rs.getString("invoice_formats_id"));
				setInvoiceFormatTitle(rs.getString("invoice_format_name"));
				setSection1(rs.getString("section_1"));
				setSection2(rs.getString("section_2"));
				setSection3(rs.getString("section_3"));
				setSection4(rs.getString("section_4"));
				setSection5(rs.getString("section_5"));
				setSection6(rs.getString("section_6"));
				setSection7(rs.getString("section_7"));
				setSection8(rs.getString("section_8"));
				setSection9(rs.getString("section_9"));
				setSection10(rs.getString("section_10"));
				setSection11(rs.getString("section_11"));
				setOperation("U");
				
				hmInvoiceContent.put("INVOICE_FORMAT_NAME", rs.getString("invoice_format_name"));
				hmInvoiceContent.put("SECTION_1", rs.getString("section_1"));
				hmInvoiceContent.put("SECTION_2", rs.getString("section_2"));
				hmInvoiceContent.put("SECTION_3", rs.getString("section_3"));
				hmInvoiceContent.put("SECTION_4", rs.getString("section_4"));
				hmInvoiceContent.put("SECTION_5", rs.getString("section_5"));
				hmInvoiceContent.put("SECTION_6", rs.getString("section_6"));
				hmInvoiceContent.put("SECTION_7", rs.getString("section_7"));
				hmInvoiceContent.put("SECTION_8", rs.getString("section_8"));
				hmInvoiceContent.put("SECTION_9", rs.getString("section_9"));
				hmInvoiceContent.put("SECTION_10", rs.getString("section_10"));
				hmInvoiceContent.put("SECTION_11", rs.getString("section_11"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmInvoiceContent", hmInvoiceContent);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void updateInvoiceFormat(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			
			System.out.println("getSection1() =======>> " + getSection1());
			System.out.println("getSection2() =======>> " + getSection2());
			System.out.println("getSection3() =======>> " + getSection3());
			pst = con.prepareStatement("update invoice_formats set invoice_format_name=?,section_1=?,section_2=?,section_3=?,section_4=?,section_5=?," +
				"section_6=?,section_7=?,section_8=?,section_9=?,section_10=?,section_11=?,updated_by=?,update_date=? where invoice_formats_id=?");
			pst.setString(1, getInvoiceFormatTitle());
			pst.setString(2, getSection1());
			pst.setString(3, getSection2());
			pst.setString(4, getSection3());
			pst.setString(5, getSection4());
			pst.setString(6, getSection5());
			pst.setString(7, getSection6());
			pst.setString(8, getSection7());
			pst.setString(9, getSection8());
			pst.setString(10, getSection9());
			pst.setString(11, getSection10());
			pst.setString(12, getSection11());
			pst.setInt(13, uF.parseToInt(strSessionEmpId));
			pst.setDate(14, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(15, uF.parseToInt(getInvoiceFormatId()));
			pst.executeUpdate();
//			System.out.println("pst =====>> " + pst);
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void insertInvoiceFormat(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
//		System.out.println("insert fun ----------->> ");
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("insert into invoice_formats(invoice_format_name,section_1,section_2,section_3,section_4,section_5,section_6," +
				"section_7,section_8,section_9,section_10,section_11,added_by,entry_date) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
			pst.setString(1, getInvoiceFormatTitle());
			pst.setString(2, getSection1());
			pst.setString(3, getSection2());
			pst.setString(4, getSection3());
			pst.setString(5, getSection4());
			pst.setString(6, getSection5());
			pst.setString(7, getSection6());
			pst.setString(8, getSection7());
			pst.setString(9, getSection8());
			pst.setString(10, getSection9());
			pst.setString(11, getSection10());
			pst.setString(12, getSection11());
			pst.setInt(13, uF.parseToInt(strSessionEmpId));
			pst.setDate(14, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getInvoiceFormatTitle() {
		return invoiceFormatTitle;
	}

	public void setInvoiceFormatTitle(String invoiceFormatTitle) {
		this.invoiceFormatTitle = invoiceFormatTitle;
	}

	public String getSection1() {
		return section1;
	}

	public void setSection1(String section1) {
		this.section1 = section1;
	}

	public String getSection2() {
		return section2;
	}

	public void setSection2(String section2) {
		this.section2 = section2;
	}

	public String getSection3() {
		return section3;
	}

	public void setSection3(String section3) {
		this.section3 = section3;
	}

	public String getSection4() {
		return section4;
	}

	public void setSection4(String section4) {
		this.section4 = section4;
	}

	public String getSection5() {
		return section5;
	}

	public void setSection5(String section5) {
		this.section5 = section5;
	}

	public String getSection6() {
		return section6;
	}

	public void setSection6(String section6) {
		this.section6 = section6;
	}

	public String getSection7() {
		return section7;
	}

	public void setSection7(String section7) {
		this.section7 = section7;
	}

	public String getSection8() {
		return section8;
	}

	public void setSection8(String section8) {
		this.section8 = section8;
	}

	public String getSection9() {
		return section9;
	}

	public void setSection9(String section9) {
		this.section9 = section9;
	}

	public String getSection10() {
		return section10;
	}

	public void setSection10(String section10) {
		this.section10 = section10;
	}

	public String getSection11() {
		return section11;
	}

	public void setSection11(String section11) {
		this.section11 = section11;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getInvoiceFormatId() {
		return invoiceFormatId;
	}

	public void setInvoiceFormatId(String invoiceFormatId) {
		this.invoiceFormatId = invoiceFormatId;
	}

	public List<FillInvoiceFormat> getInvoiceFormatList() {
		return invoiceFormatList;
	}

	public void setInvoiceFormatList(List<FillInvoiceFormat> invoiceFormatList) {
		this.invoiceFormatList = invoiceFormatList;
	}

	public String getInvoiceFormat() {
		return invoiceFormat;
	}

	public void setInvoiceFormat(String invoiceFormat) {
		this.invoiceFormat = invoiceFormat;
	}

	public String getDelete() {
		return delete;
	}

	public void setDelete(String delete) {
		this.delete = delete;
	}
	
}
