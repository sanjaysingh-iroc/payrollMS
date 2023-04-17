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
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.charts.BarchartRssource;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GeneratePdfResource extends ActionSupport implements ServletRequestAware,ServletResponseAware {

	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private static HttpServletRequest request;
	HttpSession session;
	String strEmpID;
	public static String strUserType;
	String strSessionEmpId;
	CommonFunctions CF = null;
	UtilityFunctions uF = new UtilityFunctions();
	private static Logger log = Logger.getLogger(GeneratePdfReports.class);
	
	
	public static ArrayList<Double> datalist = new ArrayList<Double>();
	public static int nNumcount = 0;
	public static ArrayList<String> strempname = new ArrayList<String>();
	public static List<String> paycycle;
	
	public String execute() throws Exception {
       	
		try{
		resourceEffortReport();
		}catch(Exception e)
		{
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
			
		}
		
					return SUCCESS;
			 
	    }
	
	
	
	public void resourceEffortReport()
	{
		
		PdfWriter writer = null;
		
		UtilityFunctions uF = new UtilityFunctions();
		Document document = new Document(PageSize.A4.rotate());
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			writer= PdfWriter.getInstance(document,baos); 
			
			document.open();

			Image image123 = Image.getInstance(request.getRealPath("/userImages/logo_new.png"));

			document.add(new PdfPCell(image123));

			Image image1 = Image.getInstance(request.getRealPath("/userImages/logo_new.png"));
			document.add(image1);

			document.add(new Paragraph(" "));
				
			Paragraph paragraph = new Paragraph("Resource Effort Reports",FontFactory.getFont("Verdana","sans-serif",20)); 
			 paragraph.setAlignment(Element.ALIGN_CENTER);
			 document.add(paragraph);
			
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));
		
				document.add(new Paragraph(strUserType, FontFactory.getFont(
						"Verdana", "sans-serif", 16)));
		
			document.add(new Paragraph(" "));

			document.add(new Paragraph(" "));

			PdfPTable HeadTable = new PdfPTable(7);
			HeadTable.setTotalWidth(800);
			HeadTable.setLockedWidth(true);
			int headerwidths[] = { 20, 20, 20, 20, 20, 20, 20 }; // percentage
			HeadTable.getDefaultCell().setBorderWidth(1);
			HeadTable.getDefaultCell().setPadding(1);
			HeadTable.setWidths(headerwidths);

			PdfPCell cell1 = new PdfPCell(new Phrase("     ",
					FontFactory.getFont("Verdana", "sans-serif", 14)));
					cell1.setBorderWidth(1);
					HeadTable.addCell(cell1);

			int count = 1;
			ListIterator<String> litc = paycycle.listIterator();

			while (litc.hasNext()) {
				litc.next();
			}

			while (litc.hasPrevious() && count <= 6) {

				PdfPCell cell2 = new PdfPCell(new Phrase("" + litc.previous(),
						FontFactory.getFont("Verdana", "sans-serif", 12)));
						cell2.setBorderWidth(1);
						cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
						HeadTable.addCell(cell2);
				count++;
			}

			PdfPTable subHeadTable = new PdfPTable(13);
			subHeadTable.setTotalWidth(800);
			subHeadTable.setLockedWidth(true);
			int headerwidths1[] = { 14, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 }; 
			subHeadTable.getDefaultCell().setBorderWidth(1);
			subHeadTable.getDefaultCell().setPadding(1);
			subHeadTable.setWidths(headerwidths1);

			PdfPCell cell5 = new PdfPCell(new Phrase("    ",
					FontFactory.getFont("Verdana", "sans-serif", 12)));
			cell5.setBorderWidth(1);
			subHeadTable.addCell(cell5);
				for (int i = 0; i < 6; i++) {

				PdfPCell cell6 = new PdfPCell(new Phrase("Actual",
				FontFactory.getFont("Verdana", "sans-serif", 12)));
				cell6.setBorderWidth(1);
				subHeadTable.addCell(cell6);

				PdfPCell cell7 = new PdfPCell(new Phrase("Roster",
						FontFactory.getFont("Verdana", "sans-serif", 12)));
				cell7.setBorderWidth(1);
				subHeadTable.addCell(cell7);

			}

			int arrcount3 = 0;

			Iterator<String> ite = strempname.iterator();

			while (ite.hasNext()) {

				PdfPCell cell3 = new PdfPCell(new Phrase(ite.next(),
						FontFactory.getFont("Verdana", "sans-serif", 10)));
				//cell3.setBackgroundColor(BaseColor.WHITE);
				cell3.setBorderWidth(1);
				subHeadTable.addCell(cell3);

				// Iterator<String> ita = datalist1.iterator();
				int num = 0;
				while (num < 12) {

				
					PdfPCell cell4 = new PdfPCell(new Phrase(
							uF.formatIntoOneDecimal(datalist.get(nNumcount)),
							FontFactory.getFont("Verdana", "sans-serif", 11)));

					cell4.setBorderWidth(1);
					subHeadTable.addCell(cell4);
					nNumcount++;
					arrcount3++;
					num++;
				}

			}
			document.add(HeadTable);
			document.add(subHeadTable);
			document.setPageSize(PageSize.A4.rotate());
			BarchartRssource br=new BarchartRssource();

			
			java.awt.Image	image =br.createChart();
	   	
			
			Image imageChart = Image.getInstance(writer, image, 1.0f);
		    
			imageChart.scaleToFit(700, 300);
			document.add(imageChart);

			PdfContentByte contentByte = writer.getDirectContent();
		
			
			
			nNumcount = 0;
			document.close();
			 
			
			response.setContentType("application/pdf");         
			 response.setContentLength(baos.size());
			 response.setHeader("Content-Disposition", "attachment; filename="+strUserType+"_"+"EmployeeHours.pdf");
			 ServletOutputStream out = response.getOutputStream();         
			 baos.writeTo(out);         
			 out.flush();      
			 baos.close();
				out.close();
             	} catch (Exception e) {
             		
             		e.printStackTrace();
            		log.error(e.getClass() + ": " + e.getMessage(), e);
		}
		document.close();
		
		
	
	
	}
	
	
	public void callEmpdata(String strEmpName, ArrayList<String> data) {

		
		strempname.add(strEmpName);
		
		Iterator<String> it12 = data.iterator();
		while (it12.hasNext()) {
			String abc = it12.next();
			if (abc == null || abc == "") {
				abc = "0";
			}
		
			datalist.add(Double.parseDouble(abc));

		}

	}

	

	
	public void callCycle(List<String> alInnerChart, String empName) {
		this.paycycle = alInnerChart;
		
		this.strUserType=empName;
			
	}
	
	
	public void clearList() {
		strempname.clear();
		datalist.clear();
		nNumcount=0;
	}
	
	
	
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;		
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	protected HttpServletRequest getRequest() {
	    return ServletActionContext.getRequest();
	}


	
	
	
}