package com.konnect.jpms.document;

import java.io.StringReader;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class HeaderFooterPageEvent extends PdfPageEventHelper {
	String strHeader;
	String strFooter;
	
	PdfTemplate total;
	public void onOpenDocument(PdfWriter writer, Document document) {
		total = writer.getDirectContent().createTemplate(30, 16);
	}
	
	public HeaderFooterPageEvent(String strHeader, String strFooter) {
		super();
		this.strHeader = strHeader;
		this.strFooter = strFooter;
	} 
	public void onStartPage(PdfWriter writer, Document document) {
		try {
			HTMLWorker hw = new HTMLWorker(document); 
			hw.parse(new StringReader(strHeader));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void onEndPage(PdfWriter writer, Document document) {
		// try {
		// HTMLWorker hw = new HTMLWorker(document);
		// hw.parse(new StringReader(strFooter));
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		PdfPTable table = new PdfPTable(3);
		try {
			if(strFooter != null && !strFooter.trim().equals("")){
				Rectangle page = document.getPageSize();
				table.setWidths(new int[]{100,0,0});
				table.setTotalWidth(527);
				table.setLockedWidth(true);
				table.getDefaultCell().setFixedHeight(40);
				table.getDefaultCell().setBorder(Rectangle.TOP);
				table.setHorizontalAlignment(Element.ALIGN_CENTER);
				
//				System.out.println("strFooter======>"+strFooter);
//				String a = "<font size='2px' color='gray'>"+strFooter+"</font>";
//				String a = "<div style='font-size: 9px; color: gray;'>"+strFooter+"</div>";
//				List<Element> al = HTMLWorker.parseToList(new StringReader(a), null);
	//			List<Element> al = HTMLWorker.parseToList(new StringReader("Off No- 204,Parmar Park,Wanawrie, Pune-40, Maharashtra, India&#61480;: +91-20- 412 02 831   &#61483;: info@konnectconsultancy.comwww.konnectconsultancy.com; www.konnecttechnologies.com"), null);
//				Phrase p = new Phrase();
//				p.addAll(al);
//				table.addCell(p);
				
				// Created By Dattatray Note : Added Image in footer.
				Image image = Image.getInstance(strFooter);
		        image.setAlignment(Element.ALIGN_RIGHT);
		        image.setAbsolutePosition(20, 790);
		        image.scalePercent(7.5f, 7.5f);
		        
				PdfPCell row1 =new PdfPCell();
				row1.setImage(image);// created by Dattatray Note : Added footer image
		        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row1.setBorder(Rectangle.TOP);
		        row1.setColspan(3);        
		        row1.setPadding(2.5f);
		        table.addCell(row1);
				
	
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
	//			table.addCell(String.format("Page %d of", writer.getPageNumber()));
				table.addCell("");
				
				PdfPCell cell = new PdfPCell(Image.getInstance(total));
				cell.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell);
	
				// table.writeSelectedRows(0, -1, 34,803,writer.getDirectContent());
//				System.out.println("page.getWidth() - document.leftMargin() - document.rightMargin()=======>"+(page.getWidth() - document.leftMargin() - document.rightMargin()));
				table.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
	//			table.setTotalWidth(750);
				table.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(), writer.getDirectContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
