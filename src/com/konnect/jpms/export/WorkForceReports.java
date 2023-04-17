package com.konnect.jpms.export;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import ChartDirector.BarLayer;
import ChartDirector.Chart;
import ChartDirector.XYChart;

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
import com.opensymphony.xwork2.ActionSupport;

public class WorkForceReports extends ActionSupport implements ServletRequestAware,ServletResponseAware,SessionAware  {

	private static final long serialVersionUID = 1L;
	protected final transient Log log = LogFactory.getLog(getClass());
	static HttpServletRequest request;
	private static HttpServletResponse response;
	Map session;
	public static List<String> pdfdatalist = new ArrayList<String>();
	public static ArrayList<Double> chardatalist = new ArrayList<Double>();
	public static List<String> xlsdatalist = new ArrayList<String>();
  
	public static int npdfCount= 0;
	public static int nchartCount = 0;
    public static int	nXlsCount=0;
	
	public static ArrayList<String> empPdf = new ArrayList<String>();
	public static ArrayList<String> empChart = new ArrayList<String>();
	public static ArrayList<String> empxls = new ArrayList<String>();
	public static String Empnm;
	public static int counter;
	public static List<String> Chartcycle;
	public static List<String> pdfCycle ;
	public static List<String> xlsCycle;

	public static String struserType;
	public static HashMap<Integer, Float> Hdata;

	public static int counter1 = 0;
	
	public String execute(){
		Chart.setLicenseCode("SXZVFNRN9MZ9L8LGA0E2B1BB");
		String id=request.getParameter("id");
		if(id.equals("1")){
			
			try {
			callSheet();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}else
			if(id.equals("2")){
				
				
				try
				{
					writeChartToPDF(700, 300);						
				}catch(Exception e)
				{
					
				}
			}

		return SUCCESS;
		
	}


	public static void writeChartToPDF( int width, int height) {
		PdfWriter writer = null;
		Document document = new Document(PageSize.A4.rotate());

		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			writer = PdfWriter.getInstance(document,baos);
			document.open();
			Image image1 = Image
					.getInstance(request.getRealPath("/userImages/logo_new.png"));
			
			
		//	request.getRealPath("/userImages/logo_new.png");
			document.add(image1);
			
			Paragraph paragraph = new Paragraph("Work Force Management",FontFactory.getFont("Verdana","sans-serif",25)); 
			 paragraph.setAlignment(Element.ALIGN_CENTER);
			 document.add(paragraph);
			
			document.add(new Paragraph(" "));

			if(struserType.equalsIgnoreCase("WLH"))
			{
				document.add(new Paragraph("By Location",FontFactory.getFont("Verdana","sans-serif",16)));
			}
			else if(struserType.equalsIgnoreCase("SH"))
			{
				document.add(new Paragraph("By Service",FontFactory.getFont("Verdana","sans-serif",16)));

			}
			else if(struserType.equalsIgnoreCase("DH"))
			{
				document.add(new Paragraph("By Department",FontFactory.getFont("Verdana","sans-serif",16)));

			}
			else if(struserType.equalsIgnoreCase("UTH"))
			{
				document.add(new Paragraph("By User type",FontFactory.getFont("Verdana","sans-serif",16)));

			}
				
			document.add(new Paragraph(" "));

			int ntableSize=pdfCycle.size();
			
			PdfPTable table = new PdfPTable((ntableSize+1));
			table.setTotalWidth(800);
			table.setLockedWidth(true);
			//int headerwidths[] = { 20, 20, 20, 20, 20, 20, 20 }; // percentage
			table.getDefaultCell().setBorderWidth(1);
			table.getDefaultCell().setPadding(1);
		//	table.setWidths(headerwidths);

			PdfPCell cell1 = new PdfPCell(new Phrase("     ",
					FontFactory.getFont("Verdana","sans-serif",14)));
				cell1.setBorderWidth(1);
			table.addCell(cell1);

			int count = 1;
			ListIterator<String> litc = pdfCycle.listIterator();

			while (litc.hasNext()) {
				litc.next();
			}

			while (litc.hasPrevious() && count <= ntableSize && count<=6 ) {

				PdfPCell cell2 = new PdfPCell(
						new Phrase(litc.previous(), FontFactory.getFont("Verdana","sans-serif",14)));
						cell2.setBorderWidth(1);
				table.addCell(cell2);
				count++;
			}

			int arrcount3 = 0;

			Iterator<String> ite = empPdf.iterator();

			while (ite.hasNext()) {

				PdfPCell cell3 = new PdfPCell(
						new Phrase(ite.next(),FontFactory.getFont("Verdana","sans-serif",14)));
				//cell3.setBackgroundColor(BaseColor.WHITE);
				cell3.setBorderWidth(1);
				table.addCell(cell3);

				// Iterator<String> ita = datalist1.iterator();
				int num = 0;
				while (num < ntableSize && num<6) {
					PdfPCell cell4 = new PdfPCell(new Phrase(
							"   "+pdfdatalist.get(npdfCount),FontFactory.getFont("Verdana","sans-serif",10)));
					//cell4.setBackgroundColor(BaseColor.WHITE);
					cell4.setBorderWidth(1);
					table.addCell(cell4);
					npdfCount++;
					arrcount3++;
					num++;
				}

			}

				
			document.add(table);
					
            java.awt.Image	image =createChart();
       		Image imageChart = Image.getInstance(writer, image, 1.0f);
       	//	imageChart.scaleToFit(800,325);
			document.add(imageChart);
			npdfCount=0;	

	
	document.close();
	response.setContentType("application/pdf");         
	 response.setContentLength(baos.size());
	 response.setHeader("Content-Disposition", "attachment; filename=workforce.pdf");
	 ServletOutputStream out = response.getOutputStream();         
	 baos.writeTo(out);         
	 out.flush();     
			} catch (Exception e) {
			e.printStackTrace();
		}
		document.close();
	}

	public static  java.awt.Image createChart() 
	{
		
		
		Chart.setLicenseCode("SXZVFNRN9MZ9L8LGA0E2B1BB");


		double []arrAdddata;
		arrAdddata = new double[6];
		double[] arrchartdata = new double[6];
	    String[] labels = new String[6];

		ListIterator<String> litc1 = Chartcycle.listIterator();
		while (litc1.hasNext()) {
			litc1.next();
			}
		
		int k = 0;
		while (k < 6 && litc1.hasPrevious()) {
			labels[k] = litc1.previous();
			k++;
		}
	
		XYChart c = new XYChart(800, 300);
	
		c.addTitle(" ", "", 10);
		c.setPlotArea(75, 65, 700, 200, 16775416, 16775416);
		c.addLegend(55, 18, false, "Arial Bold", 10).setBackground(0xffffff);
		c.yAxis().setTitle("No of Resources", "Arial Bold", 10);
		c.yAxis().setTopMargin(20);
		c.xAxis().setLabelStyle("Arial Bold", 10);
		c.xAxis().setLabels(labels);
		BarLayer layer = c.addBarLayer2(Chart.Side, 3);
		int col = 0;
		Iterator<String> ite = empChart.iterator();
		while (ite.hasNext()) {
			String strempdemo = ite.next();
			ListIterator<String> litc = Chartcycle.listIterator();
			while (litc.hasNext()) {
				litc.next();
			}
			int i = 0;
			while (i < 6 && litc.hasPrevious()) {
				double f1 = chardatalist.get(nchartCount);
				nchartCount++;
				arrAdddata[i] = f1;
				i++;
			}
			arrchartdata = arrAdddata;

			for (int j = 0; j < arrchartdata.length; j++) {
			}
			if (col == 0) {
				layer.addDataSet(arrchartdata, 0x006699, strempdemo);
			} else if (col == 1) {

				layer.addDataSet(arrchartdata, 0x990000, strempdemo);

			} else if (col == 2) {
				layer.addDataSet(arrchartdata, 0x669900, strempdemo);

			} else if (col==3) {

				layer.addDataSet(arrchartdata, 0x663399, strempdemo);
			}
			
			else if (col==4) {

				layer.addDataSet(arrchartdata, 0x999900, strempdemo);

			}
			
			else if (col==5) {

				layer.addDataSet(arrchartdata, 0x990099, strempdemo);

			}
			else if (col==6) {

				layer.addDataSet(arrchartdata, 0xFF00FF, strempdemo);

			}
			
			else if (col==7) {

				layer.addDataSet(arrchartdata, 0xFF9900, strempdemo);

			}
			
			else if (col==8) {

				layer.addDataSet(arrchartdata, 0x6600cc, strempdemo);

			}
			else if (col==9) {

				layer.addDataSet(arrchartdata, 0x330066, strempdemo);

			}
		
			else if (col==10) {

				layer.addDataSet(arrchartdata, 0xFF0000, strempdemo);

			}else
			{
				layer.addDataSet(arrchartdata, 0x003300, strempdemo);
			}
			
			col++;
			
		}
		nchartCount=0;
		
		
		return  c.makeImage();
	}
    public void callSheet() throws IOException
    {
    	
    	Workbook wb;
        	
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();

		 log.debug("insideCall sheet method ");
		 
		 try{
			 
			 
        	wb = new HSSFWorkbook();
        	InputStream is = new FileInputStream(request.getRealPath("/userImages/logo_new.png"));
	byte[] bytes = IOUtils.toByteArray(is);
	int pictureIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
	is.close();
	

		
/*java.awt.Image	image =createChart();
BufferedImage bufferImg = null;
//bufferImg=(BufferedImage) image;
ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
ImageIO.write((RenderedImage) image, "JPEG", byteArrayOut);
int pictureIdx1 = wb.addPicture(byteArrayOut.toByteArray(),HSSFWorkbook.PICTURE_TYPE_JPEG);*/
	
	
	//java.awt.image.BufferedImage bfrImage=(java.awt.image.BufferedImage) createChart();
	
	
	
	

        Map<String, CellStyle> styles = createStyles(wb);

        Sheet sheet = wb.createSheet("WorkforceManagement");
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
        Row titleRow = sheet.createRow(3);
        titleRow.setHeightInPoints(45);
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue("Work Force Management");
        
        titleCell.setCellStyle(styles.get("title"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$4:$L$4"));
        
        Row titleRow1 = sheet.createRow(4);
        titleRow1.setHeightInPoints(25);
        Cell titleCell1 = titleRow1.createCell(1);
          
        if(struserType.equalsIgnoreCase("WLH"))
		{
        	 titleCell1.setCellValue("By Location");
		}
		else if(struserType.equalsIgnoreCase("SH"))
		{
			titleCell1.setCellValue("By Service");

		}
		else if(struserType.equalsIgnoreCase("DH"))
		{
			titleCell1.setCellValue("By Department");

		}
		else if(struserType.equalsIgnoreCase("UTH"))
		{
			titleCell1.setCellValue("By User type");

		}        
        
        titleCell1.setCellStyle(styles.get("title1"));
          
        
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$1:$L$1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$2:$L$2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$3:$L$3"));

        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$5:$L$5"));
        //header row
        Row headerRow = sheet.createRow(5);
        headerRow.setHeightInPoints(16);
        Cell headerCell;
        
        ListIterator<String> litc = Chartcycle.listIterator();

    	while (litc.hasNext()) {
    		litc.next();
    	}
    	
    	headerCell = headerRow.createCell(1);
        headerCell.setCellValue(" ");
        headerCell.setCellStyle(styles.get("header1"));
        
        int l=2;
        while(litc.hasPrevious())
        		{
            headerCell = headerRow.createCell(l++);
            headerCell.setCellValue(litc.previous());
            headerCell.setCellStyle(styles.get("header"));
                 }
           
        Row headerRow1 = sheet.createRow(6);
        headerRow1.setHeightInPoints(16);
        Cell headerCell1;       
        

        ListIterator<String> litc1 = xlsCycle.listIterator();
        while(litc1.hasNext())
        {
        	litc1.next();
        }
        
        headerCell1 = headerRow1.createCell(1);
        headerCell1.setCellValue(" ");
        headerCell1.setCellStyle(styles.get("header1"));
        
        int l1=2;
        while(litc1.hasPrevious())
        {
        	
        	headerCell1 = headerRow1.createCell(l1++);
            headerCell1.setCellValue(litc1.previous());
            headerCell1.setCellStyle(styles.get("header"));
        	
        }
        
        

        Row row1 = sheet.createRow(7);
        Cell cell2=row1.createCell(1);            
        cell2.setCellValue("");
        cell2.setCellStyle(styles.get("header1"));
        
        
        for(int i=2;i<l;i++)
        {
        	Cell cell3=row1.createCell(i);            
            cell3.setCellValue("");
            cell3.setCellStyle(styles.get("header"));
            	
        }
        
        
		int rownum=8;

        
    	Iterator<String> ite = empxls.iterator();

		while (ite.hasNext()) {
			String empdemo = ite.next();
			
            Row row = sheet.createRow(rownum++);   
            Cell cell=row.createCell(1);            
            cell.setCellValue(empdemo);
            cell.setCellStyle(styles.get("header2"));
  
            
            
            int k=2;
			while (k<l) {

				
				Cell cell1=row.createCell(k++);
				cell1.setCellValue(xlsdatalist.get(nXlsCount));
		        cell1.setCellStyle(styles.get("cell"));
		        
		        nXlsCount++;

			}

		}

	
        sheet.setColumnWidth(1, 30*256); //30 characters wide
        for (int i = 1; i < l; i++) {
            sheet.setColumnWidth(i, 25*256);  //6 characters wide
        }
        sheet.setColumnWidth(10, 25*256); //10 characters wide
        
        
        CreationHelper helper = wb.getCreationHelper();

		Drawing drawing = sheet.createDrawingPatriarch();
		
		
		ClientAnchor anchor1 = helper.createClientAnchor();
		
		
		anchor1.setCol1(1);
		
		anchor1.setRow1(rownum+4);
	//	Picture pict1 = drawing.createPicture(anchor1, pictureIdx1);
	
	//	pict1.resize();
		ClientAnchor anchor = helper.createClientAnchor();
		anchor.setCol1(1);
		anchor.setRow1(1);
		Picture pict = drawing.createPicture(anchor, pictureIdx);
		pict.resize();
		nXlsCount=0;
     	

		

			wb.write(baos);
		response.setContentType("application/xls");
		response.setContentLength(baos.size());

		response.setHeader("Content-Disposition", "attachment; filename=workforce.xls");
		ServletOutputStream out = response.getOutputStream();
		baos.writeTo(out);         
		out.flush();  
			
		}catch(Exception e){
			e.printStackTrace();
			}
  	
    }
    
    
    /**
     * Create a library of cell styles
     */
    private static Map<String, CellStyle> createStyles(Workbook wb){
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        CellStyle style;
        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short)20);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        titleFont.setFontName("Verdana");

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(titleFont);
        styles.put("title", style);
        
        
        
        Font titleFont1 = wb.createFont();
        titleFont1.setFontHeightInPoints((short)14);
        titleFont1.setBoldweight(Font.BOLDWEIGHT_BOLD);
        titleFont1.setFontName("Verdana");
        
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(titleFont1);
        styles.put("title1", style);
                    

        Font monthFont = wb.createFont();
        monthFont.setFontHeightInPoints((short)11);
        monthFont.setColor(IndexedColors.BLACK.getIndex());
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(IndexedColors.TAN.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(monthFont);
        style.setWrapText(true);
        styles.put("header", style);
        
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(monthFont);
        style.setWrapText(true);
        styles.put("header1", style);
        
        
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(IndexedColors.TAN.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(monthFont);
        style.setWrapText(true);
        styles.put("header2", style);
        

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setWrapText(true);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styles.put("cell", style);

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        styles.put("formula", style);

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        styles.put("formula_2", style);

        return styles;
    }
	
    public void clearList() {
		empPdf.clear();
		empChart.clear();
		empxls.clear();
	}

	public void clearList1() {
		xlsdatalist.clear();
		nXlsCount=0;
		chardatalist.clear();
        nchartCount=0;
        pdfdatalist.clear();
        npdfCount=0;
	}
	
	
	public void callXlsdata( ArrayList<String> data1) 
	{

		Iterator<String> it12 = data1.iterator();
		while (it12.hasNext()) {
		String straddData = it12.next();
			  if(straddData==null)
			  {
				  straddData="0";
			  }
			  
			  xlsdatalist.add(straddData);
		}
		
	}
	
	public void callPdfChartData(String strEmpName, ArrayList<String> data) {
		empPdf.add(strEmpName);
		empChart.add(strEmpName);
		empxls.add(strEmpName);

		Iterator<String> it12 = data.iterator();
		while (it12.hasNext()) {
			String straddData = it12.next();
			  if(straddData==null)
			  {
				  straddData="0";
			  }
			  pdfdatalist.add(straddData);
			  
			  chardatalist.add(Double.parseDouble(straddData));
              
	      
		}
	}
	public void callCycle(List<String> alInnerChart,List<String> alInnerChart1,List<String> alInnerChart2,String rType1) {
		this.Chartcycle = alInnerChart;
		this.pdfCycle=alInnerChart1;
		this.xlsCycle=alInnerChart2;
		this.struserType=rType1;
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
	public void setSession(Map session) {
		this.session=session;
	}
}