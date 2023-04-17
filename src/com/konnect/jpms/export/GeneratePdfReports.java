package com.konnect.jpms.export;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.charts.BarChart;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GeneratePdfReports extends ActionSupport implements ServletRequestAware, ServletResponseAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private static HttpServletRequest request;
	HttpSession session;
	String strEmpID;
	String strUserType;
	String strSessionEmpId;
	CommonFunctions CF = null;
	UtilityFunctions uF = new UtilityFunctions();
	public static List<String> pdfdatalist = new ArrayList<String>();
	public static int npdfCount = 0;
	public static ArrayList<String> empPdf = new ArrayList<String>();
	public static List<String> pdfCycle;
	public static String struserType;
	public static String strReporttypeName;

	private static Logger log = Logger.getLogger(GeneratePdfReports.class);

	public String execute() throws Exception {
		try {
			
			String strType = request.getParameter("T");
			if(strType!=null && strType.equalsIgnoreCase("WF")){
				WorkForceReports objWFR = new WorkForceReports();
				objWFR.setServletRequest(request);
				objWFR.execute();
			}
			
			
			generateWorkForceReport();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		}
		return SUCCESS;

	}

	public void generateWorkForceReport() {

		PdfWriter writer = null;
		Document document = new Document(PageSize.A4.rotate());
		BarChart bc = new BarChart();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			writer = PdfWriter.getInstance(document, baos);
			document.open();
			Image logoImage = Image.getInstance(request.getRealPath("/userImages/logo_new.png"));
			logoImage.scaleToFit(200, 100);

			document.add(logoImage);

			Paragraph paragraph = new Paragraph(strReporttypeName, FontFactory.getFont("Verdana", "sans-serif", 20));
			paragraph.setAlignment(Element.ALIGN_CENTER);
			document.add(paragraph);

			document.add(new Paragraph(" "));

			document.add(new Paragraph(struserType, FontFactory.getFont("Verdana", "sans-serif", 16)));

			document.add(new Paragraph(" "));

			int ntableSize = 6;

			if (pdfCycle.size() < 6) {
				ntableSize = pdfCycle.size();
			}

			PdfPTable workForceTable = new PdfPTable((ntableSize + 1));
			workForceTable.setTotalWidth(800);
			workForceTable.setLockedWidth(true);

			workForceTable.getDefaultCell().setBorderWidth(1);
			workForceTable.getDefaultCell().setPadding(1);

			PdfPCell blankcell = new PdfPCell(new Phrase("     ", FontFactory.getFont("Verdana", "sans-serif", 14)));
			blankcell.setBorderWidth(1);
			workForceTable.addCell(blankcell);

			int count = 1;
			ListIterator<String> litc = pdfCycle.listIterator();

			while (litc.hasNext()) {
				litc.next();
			}

			while (litc.hasPrevious() && count <= ntableSize && count <= 6) {

				PdfPCell payCycleHead = new PdfPCell(new Phrase(litc.previous(), FontFactory.getFont("Verdana", "sans-serif", 14)));
				payCycleHead.setBorderWidth(1);
				payCycleHead.setHorizontalAlignment(Element.ALIGN_CENTER);

				workForceTable.addCell(payCycleHead);
				count++;
			}

			int arrcount3 = 0;

			Iterator<String> ite = empPdf.iterator();

			while (ite.hasNext()) {

				PdfPCell empName = new PdfPCell(new Phrase(ite.next(), FontFactory.getFont("Verdana", "sans-serif", 14)));
				empName.setBorderWidth(1);
				workForceTable.addCell(empName);

				int num = 0;
				while (num < ntableSize && num < 6) {
					PdfPCell cell4 = new PdfPCell(new Phrase("   " + pdfdatalist.get(npdfCount), FontFactory.getFont("Verdana", "sans-serif", 10)));
					cell4.setBorderWidth(1);
					cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
					workForceTable.addCell(cell4);
					npdfCount++;
					arrcount3++;
					num++;
				}

			}
			document.add(workForceTable);
			java.awt.Image image = bc.createChart();
			Image imageChart = Image.getInstance(writer, image, 1.0f);
			document.add(imageChart);
			npdfCount = 0;

			document.close();
			response.setContentType("application/pdf");
			response.setContentLength(baos.size());
			response.setHeader("Content-Disposition", "attachment; filename=" + struserType + "_" + "workforce.pdf");
			ServletOutputStream out = response.getOutputStream();
			baos.writeTo(out);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);

		}
		document.close();

	}

	public void clearList() {
		empPdf.clear();
		pdfdatalist.clear();
		npdfCount = 0;
	}

	public void callPdfChartData(String strReporttypeName, String strEmpName, ArrayList<String> data) {

		this.strReporttypeName = strReporttypeName;

		empPdf.add(strEmpName);
		Iterator<String> it12 = data.iterator();
		while (it12.hasNext()) {

			String straddData = it12.next();

			if (straddData == null) {
				straddData = "0";
			}
			pdfdatalist.add(straddData);

		}
	}

	public void callCycle(List<String> alInnerChart, String rType1) {

		this.pdfCycle = alInnerChart;

		this.struserType = rType1;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	protected HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}

}
