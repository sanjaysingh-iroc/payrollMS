package com.konnect.jpms.salary;

import static java.lang.System.out;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperDesignViewer;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class GenerateSalarySlip extends ActionSupport implements IStatements, ServletRequestAware
{

	
	private HttpServletRequest request;
	HttpSession session;
	String strEmpId;
	
	String strDateFormat = "MM";
	SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
	String monthNo=sdf.format(new Date());
	private ResultSet rs,rs2;
	private Connection con;
	private PreparedStatement stmt,stmt2;
	String strEmail=null;
	//double totalEarnings=10000,totalDeduction=2000;
	
	private static final long serialVersionUID = 825379152057220110L;
	private Document document = null;
	static Map hm = new HashMap();
	double totalEarnings=10000,totalDeduction=2000;
	String filePath;
	String startDate=null, endDate=null;
	CommonFunctions cF=null;
	
	public String execute()	{
		
		HttpSession session = request.getSession();
		strEmpId = (String)request.getAttribute("empIDPay");
		cF = (CommonFunctions) session.getAttribute(CommonFunctions);
		generatePdf();
		
		
		List alDate = (List) request.getAttribute("alDate");
		
		Notifications nF = new Notifications(N_SALARY_SLIP);
		strEmail="bhushan.konnect@gmail.com";
		nF.setStrEmailTo(strEmail);
		filePath = request.getRealPath("/images1/")+File.separator;
		nF.setStrAttachmentFileName(filePath + strEmpId+".pdf");
		nF.sendNotifications();
		
		return SUCCESS;
		
	}
	

	public void generatePdf() 
	{
		
		Document document = null;
		initialize();
		
		List<List<String>> fixedArrayValues=getDataLeft();
		List<List<String>> fixedArray2Values=getDataRight();
		
		String[] array=getData();
		String[] headType=getDataLocation();
		
		String arrayValues[]=calculate(array,headType);
		
		
		String fixedArray[]={"Employee Name","Employee I.D.No.","Account No.","No.of Working days","Days present","Salary for the month"};
		String fixedArray2[]={"Joining Date","Designation","Paid Leaves","P.F.","Leave Without Pay"};
		
		int counterTitle = 5;
		int verticalX=45,horizontalY=225,verticalY=225, headTextFieldX=50,headTextFieldY=225;
		int countData=0,valueTextField=145,deductionTextFieldX=50,deductionTextFieldY=225;
		int fixedHorizontalY=55,fixedVerticalX=45,fixedVerticalY=55;
		
		String[] monthName = {"January", "February",
				  "March", "April", "May", "June", "July",
				  "August", "September", "October", "November",
				  "December"
				  };
		Calendar calendar = Calendar.getInstance();
		String month=monthName[calendar.get(Calendar.MONTH)];
		String currentMonth=month+"/"+calendar.get(java.util.Calendar.YEAR); 
		
		
		String netSalary="Ten Thousand One Hundred Eighty Five Only";
		String netSalaryNo="101085";
		
		document = createDocument();
		Element rootNode = addRootNode(document, "jasperReport");
		rootNode.setAttribute("name","SalaryDetails");
		Element childnode = null;
		JasperReport jasperReport;
	
	//================Adding Image to the page header===========================
		Element pageHeader = addNode(document, rootNode, "pageHeader");
		Element band = addNode(document, pageHeader, "band");
		band.setAttribute("height","100");
		Element staticTextTitle = addNode(document, band, "staticText");
		Element reportElementTitle = addNode(document, staticTextTitle, "reportElement");
		reportElementTitle.setAttribute("x",Integer.toString(0));
		reportElementTitle.setAttribute("y",Integer.toString(0));
		reportElementTitle.setAttribute("width","375");
		reportElementTitle.setAttribute("height","75");
		reportElementTitle.setAttribute("mode","Opaque");
		reportElementTitle.setAttribute("backcolor","yellow");
		Element textElementTitle=addNode(document, staticTextTitle, "textElement");
		textElementTitle.setAttribute("verticalAlignment","Middle");
		Element fontTitle=addNode(document, textElementTitle, "font");
		fontTitle.setAttribute("isBold","true");
		fontTitle.setAttribute("size", "20");
		
		Element staticTextTitle2 = addNode(document, band, "staticText");
		Element reportElementTitle2 = addNode(document, staticTextTitle2, "reportElement");
		reportElementTitle2.setAttribute("x",Integer.toString(0));
		reportElementTitle2.setAttribute("y",Integer.toString(35));
		reportElementTitle2.setAttribute("width","300");
		reportElementTitle2.setAttribute("height","45");
		Element textElementTitle2=addNode(document, staticTextTitle2, "textElement");
		textElementTitle2.setAttribute("verticalAlignment","Middle");
		Element fontTitle2=addNode(document, textElementTitle2, "font");
		fontTitle2.setAttribute("isBold","false");
		fontTitle2.setAttribute("size", "12");
		List<String> companyAddress=getAddressRelatedInfo();
		String finalStrTitle="<![CDATA["+companyAddress.get(0)+"]]>";
		
		Element textFieldExpressionTitle = addNode(document,staticTextTitle, "text");
		addText(document,textFieldExpressionTitle,finalStrTitle);	
		
		String finalStrTitle2="<![CDATA["+companyAddress.get(2)+"]]>";
		Element textFieldExpressionTitle2 = addNode(document,staticTextTitle2, "text");
		addText(document,textFieldExpressionTitle2,finalStrTitle2);	
	
			Element image = addNode(document, band, "image");
			image.setAttribute("hyperlinkType", "none");
				Element reportElement = addNode(document, image, "reportElement");
				reportElement.setAttribute("x", "380");
				reportElement.setAttribute("y", "0");
				reportElement.setAttribute("width", "700");
				reportElement.setAttribute("height", "100");
				
				Element graphicElement = addNode(document, image, "graphicElement");
				graphicElement.setAttribute("stretchType", "RelativeToBandHeight");
				
				Element imageExpression = addNode(document,image, "imageExpression");
				imageExpression.setAttribute("class","java.io.File");
				imageExpression.setTextContent("<![CDATA[(new File(\""+companyAddress.get(1)+"\"))]]>");
	//==========================================================================	
	
	Element detail = addNode(document, rootNode, "detail");
	Element band2 = addNode(document, detail, "band");
	band2.setAttribute("height","650");
		
	//=====================Titles============================================================
		Element staticText2 = addNode(document, band2, "staticText");
		Element reportElement4 = addNode(document, staticText2, "reportElement");
		reportElement4.setAttribute("x",Integer.toString(100));
		reportElement4.setAttribute("y",Integer.toString(210));
		reportElement4.setAttribute("width","200");
		reportElement4.setAttribute("height","26");
		Element textElement3=addNode(document, staticText2, "textElement");
		textElement3.setAttribute("verticalAlignment","Top");
		Element font2=addNode(document, textElement3, "font");
		font2.setAttribute("isBold","true");
		font2.setAttribute("size", "13");
		Element textFieldExpression4 = addNode(document,staticText2, "text");
		String finalStr="<![CDATA[EARNINGS]]>";
		addText(document,textFieldExpression4,finalStr);
		
		Element staticText5 = addNode(document, band2, "staticText");
		Element reportElement5 = addNode(document, staticText5, "reportElement");
		reportElement5.setAttribute("x",Integer.toString(285));
		reportElement5.setAttribute("y",Integer.toString(210));
		reportElement5.setAttribute("width","200");
		reportElement5.setAttribute("height","26");
		Element textElement5=addNode(document, staticText5, "textElement");
		textElement5.setAttribute("verticalAlignment","Top");
		Element font5=addNode(document, textElement5, "font");
		font5.setAttribute("isBold","true");
		font5.setAttribute("size", "13");
		Element textFieldExpression5 = addNode(document,staticText5, "text");
		String finalStr5="<![CDATA[DEDUCTION]]>";
		addText(document,textFieldExpression5,finalStr5);
	//============================================================================
	
	
	//=========permanent lines===================================================		
		Element linePermanent = addNode(document, band2, "line");
		Element reportLinePermanent = addNode(document, linePermanent, "reportElement");
		reportLinePermanent.setAttribute("x", "0");
		reportLinePermanent.setAttribute("y", "1");
		reportLinePermanent.setAttribute("width", "555");
		reportLinePermanent.setAttribute("height", "0");
		
		Element linePermanent2 = addNode(document, band2, "line");
		Element reportLinePermanent2 = addNode(document, linePermanent2, "reportElement");
		reportLinePermanent2.setAttribute("x", "45");
		reportLinePermanent2.setAttribute("y", "225");
		reportLinePermanent2.setAttribute("width", "380");
		reportLinePermanent2.setAttribute("height", "0");
	//============================================================================
		
	// ============================================================================
		
		

		//====================Fixed horizontal Salary for the month block==========
			Element HlineNode3 = addNode(document, band2, "line");
			Element HReportElement3 = addNode(document, HlineNode3, "reportElement");
			HReportElement3.setAttribute("x", Integer.toString(45));
			HReportElement3.setAttribute("y", Integer.toString(30));
			HReportElement3.setAttribute("width", "240");
			HReportElement3.setAttribute("height", "0");	
		//=========================================================================
		int tempX=45;
		
		for(int j=0;j<3;j++)
		{
			//===================fixed vertical lines==================================	
				Element VlineNode4 = addNode(document, band2, "line");
				Element VReportElement4 = addNode(document, VlineNode4, "reportElement");
				VReportElement4.setAttribute("x", Integer.toString(tempX));
				VReportElement4.setAttribute("y", Integer.toString(30));
				VReportElement4.setAttribute("width", "0");
				VReportElement4.setAttribute("height", "25");
				tempX=tempX+120;
			//===========================================================================
		}
		//printUpperPart(band2,fixedArray,fixedArrayValues,50,55);
		
		

		int x=50, y=55;
		for(int j=0;j<fixedArray.length;j++)
		{
			System.out.println("Value of j===>>"+ j);
				//=========================Printing the head Labels======================================
					Element textField = addNode(document, band2, "staticText");
					Element reportElement2 = addNode(document, textField, "reportElement");
					reportElement2.setAttribute("x",Integer.toString(x));
					reportElement2.setAttribute("y",Integer.toString(y));
					reportElement2.setAttribute("width","95");
					reportElement2.setAttribute("height","28");
					Element textElement=addNode(document, textField, "textElement");
					textElement.setAttribute("verticalAlignment","Middle");
					Element font=addNode(document, textElement, "font");
					font.setAttribute("isBold","true");
					font.setAttribute("size", "10");
					Element textFieldExpression = addNode(document,textField, "text");
				
				//====================VALUES of the heads=====================
					char char1=(char)60;
					char char2=(char)62;
					String finalString=char1+"![CDATA["+fixedArray[j]+"]]"+char2;
					addText(document,textFieldExpression,finalString);
				//============================================================
				
				//==================Head Values================================
					Element staticText = addNode(document, band2, "staticText");
					Element reportElement3 = addNode(document, staticText, "reportElement");
					reportElement3.setAttribute("x",Integer.toString(x+95));
					reportElement3.setAttribute("y",Integer.toString(y));
					reportElement3.setAttribute("width","85");
					reportElement3.setAttribute("height","24");
					Element textFieldExpression2 = addNode(document,staticText, "text");
					String finalStr1=char1+"![CDATA["+fixedArrayValues.get(0).get(j)+"]]"+char2;
					addText(document,textFieldExpression2,finalStr1);
				//===============================================================
			
					y=y+25;
				//==================================================================================
		}
	
		
		
		//printUpperPart(band2,fixedArray2,fixedArray2Values,240,55);
		x=240; y=55;
		for(int j=0;j<fixedArray2.length;j++)
		{
			System.out.println("Value of j===>>"+ j);
				//=========================Printing the head Labels======================================
					Element textField = addNode(document, band2, "staticText");
					Element reportElement2 = addNode(document, textField, "reportElement");
					reportElement2.setAttribute("x",Integer.toString(x));
					reportElement2.setAttribute("y",Integer.toString(y));
					reportElement2.setAttribute("width","95");
					reportElement2.setAttribute("height","28");
					Element textElement=addNode(document, textField, "textElement");
					textElement.setAttribute("verticalAlignment","Middle");
					Element font=addNode(document, textElement, "font");
					font.setAttribute("isBold","true");
					font.setAttribute("size", "10");
					Element textFieldExpression = addNode(document,textField, "text");
				
				//====================VALUES of the heads=====================
					char char1=(char)60;
					char char2=(char)62;
					String finalString=char1+"![CDATA["+fixedArray2[j]+"]]"+char2;
					addText(document,textFieldExpression,finalString);
				//============================================================
				
				//==================Head Values================================
					Element staticText = addNode(document, band2, "staticText");
					Element reportElement3 = addNode(document, staticText, "reportElement");
					reportElement3.setAttribute("x",Integer.toString(x+95));
					reportElement3.setAttribute("y",Integer.toString(y));
					reportElement3.setAttribute("width","85");
					reportElement3.setAttribute("height","24");
					Element textFieldExpression2 = addNode(document,staticText, "text");
					String finalStr1=char1+"![CDATA["+fixedArray2Values.get(0).get(j)+"]]"+char2;
					addText(document,textFieldExpression2,finalStr1);
				//===============================================================
			
					y=y+25;
				//==================================================================================
		}
		
		
		//====================salary for the month=========================================
			Element textField11 = addNode(document, band2, "staticText");
			Element reportElement11 = addNode(document, textField11, "reportElement");
			reportElement11.setAttribute("x","48");
			reportElement11.setAttribute("y","30");
			reportElement11.setAttribute("width","95");
			reportElement11.setAttribute("height","28");
			Element textElement11=addNode(document, textField11, "textElement");
			textElement11.setAttribute("verticalAlignment","Middle");
			Element font11=addNode(document, textElement11, "font");
			font11.setAttribute("isBold","true");
			font11.setAttribute("size", "10");
			Element textFieldExpression11 = addNode(document,textField11, "text");
			char char3=(char)60;
			char char4=(char)62;
			String finalString11=char3+"![CDATA[Salary for the Month]]"+char4;
			addText(document,textFieldExpression11,finalString11);
			
			Element staticText12 = addNode(document, band2, "staticText");
			Element reportElement12 = addNode(document, staticText12, "reportElement");
			reportElement12.setAttribute("x",Integer.toString(200));
			reportElement12.setAttribute("y",Integer.toString(30));
			reportElement12.setAttribute("width","85");
			reportElement12.setAttribute("height","24");
			Element textFieldExpression12 = addNode(document,staticText12, "text");
			String finalStr12=char3+"![CDATA["+currentMonth+"]]"+char4;
			addText(document,textFieldExpression12,finalStr12);
		//=================================================================================
		
		
		for(int j=0;j<7;j++)
		{
			//====================Fixed horizontal lines================================
			Element HlineNode1 = addNode(document, band2, "line");
			verticalY=225;
			Element HReportElement1 = addNode(document, HlineNode1, "reportElement");
			HReportElement1.setAttribute("x", Integer.toString(45));
			HReportElement1.setAttribute("y", Integer.toString(fixedHorizontalY));
			HReportElement1.setAttribute("width", "380");
			HReportElement1.setAttribute("height", "0");	
			fixedHorizontalY=fixedHorizontalY+25;
			//=====================================================================
		}	
		
		for(int j=0; j<30; j++)
		{
			//===================fixed vertical lines====================================	
				System.out.println((countData+1)*5);
				
				Element VlineNode1 = addNode(document, band2, "line");
				Element VReportElement1 = addNode(document, VlineNode1, "reportElement");
				VReportElement1.setAttribute("x", Integer.toString(fixedVerticalX));
				VReportElement1.setAttribute("y", Integer.toString(fixedVerticalY));
				VReportElement1.setAttribute("width", "0");
				VReportElement1.setAttribute("height", "25");	
				fixedVerticalX=fixedVerticalX+95;
				
				if(fixedVerticalX==520)
				{
					fixedVerticalX=45;
					fixedVerticalY=fixedVerticalY+25;
				}
		//======================================================================
		}		
		
		
		int iterate,countE=0,countD=0;
		System.out.println(headType.length);
		for(int j=0;j<headType.length;j++)
		{
			 if(headType[j]==null)	break; 
			if(headType[j].trim().equals("E"))
				countE++;
			else
				countD++;
		}
		
		if(countE>countD)
			iterate=countE;
		else
			iterate=countD;
		
		System.out.println("count E::>"+countE+"count D"+countD+", iterator"+iterate);
		for(int j=0;j<iterate+2;j++)
		{
			
			//====================horizontal lines================================
			Element HlineNode = addNode(document, band2, "line");
			horizontalY=horizontalY+25;
			verticalY=225;
			Element HReportElement = addNode(document, HlineNode, "reportElement");
			HReportElement.setAttribute("x", Integer.toString(45));
			HReportElement.setAttribute("y", Integer.toString(horizontalY));
			HReportElement.setAttribute("width", "380");
			HReportElement.setAttribute("height", "0");	
			//=====================================================================
			
			countData++;
			System.out.println(j);
		}	
				

		for(int j=0; j<((iterate+2)*5); j++)
		{
			//===================vertical lines====================================	
				if(j==((iterate+2)*5)-2)
				{
					verticalX=verticalX+95;
					continue;
				}
				Element VlineNode = addNode(document, band2, "line");
				Element VReportElement = addNode(document, VlineNode, "reportElement");
				VReportElement.setAttribute("x", Integer.toString(verticalX));
				VReportElement.setAttribute("y", Integer.toString(verticalY));
				VReportElement.setAttribute("width", "0");
				VReportElement.setAttribute("height", "25");	
				verticalX=verticalX+95;
				
				if(verticalX==520)
				{
					verticalX=45;
					verticalY=verticalY+25;
				}
				
				//======================================================================
		}		
	
	
	for(int j=0;headType[j]!=null;j++)
	{
			System.out.println(j);
			//=========================Printing the head Labels======================================
			Element textField1 = addNode(document, band2, "staticText");
			Element reportElement2 = addNode(document, textField1, "reportElement");
			if(headType[j].trim().equals("E"))
			{
				reportElement2.setAttribute("x",Integer.toString(headTextFieldX));
				reportElement2.setAttribute("y",Integer.toString(headTextFieldY));
			}
			else
			{
				reportElement2.setAttribute("x",Integer.toString(deductionTextFieldX+190));
				reportElement2.setAttribute("y",Integer.toString(deductionTextFieldY));
			}
			
			reportElement2.setAttribute("width","95");
			reportElement2.setAttribute("height","26");
			Element textElement=addNode(document, textField1, "textElement");
			textElement.setAttribute("verticalAlignment","Middle");
			Element font=addNode(document, textElement, "font");
			font.setAttribute("isBold","true");
			font.setAttribute("size", "10");
			Element textFieldExpression = addNode(document,textField1, "text");
			
			//====================VALUES of the heads=====================
			char char1=(char)60;
			char char2=(char)62;
			String finalString=char1+"![CDATA["+array[j]+"]]"+char2;
			addText(document,textFieldExpression,finalString);
			//============================================================
			
			//==================Head Values================================
				Element staticText = addNode(document, band2, "staticText");
				Element reportElement3 = addNode(document, staticText, "reportElement");
				if(headType[j].trim().equals("E"))
				{
					reportElement3.setAttribute("x",Integer.toString(headTextFieldX+95));
					reportElement3.setAttribute("y",Integer.toString(headTextFieldY));
				}
				else
				{
					reportElement3.setAttribute("x",Integer.toString(deductionTextFieldX+285));
					reportElement3.setAttribute("y",Integer.toString(deductionTextFieldY));
				}
				reportElement3.setAttribute("width","85");
				reportElement3.setAttribute("height","24");
				Element textFieldExpression2 = addNode(document,staticText, "text");
				String finalStr1=char1+"![CDATA["+arrayValues[j]+"]]"+char2;
				addText(document,textFieldExpression2,finalStr1);
			//===============================================================
			
				if(headType[j].trim().equals("E"))
				{
					headTextFieldY=headTextFieldY+25;
				}
				else
				{
					deductionTextFieldY=deductionTextFieldY+25;
				}
			//==================================================================================
	}
	int setY;
	if(headTextFieldY>deductionTextFieldY)
		setY=headTextFieldY;
	else
		setY=deductionTextFieldY;
//==================Net Values================================
	
	Element staticText6 = addNode(document, band2, "staticText");
	Element reportElement6 = addNode(document, staticText6, "reportElement");
	reportElement6.setAttribute("x",Integer.toString(headTextFieldX));
	reportElement6.setAttribute("y",Integer.toString(setY));
	reportElement6.setAttribute("width","95");
	reportElement6.setAttribute("height","32");
	Element textElement6=addNode(document, staticText6, "textElement");
	textElement6.setAttribute("verticalAlignment","Top");
	Element font6=addNode(document, textElement6, "font");
	font6.setAttribute("isBold","true");
	font6.setAttribute("size", "11");
	Element textFieldExpression6 = addNode(document,staticText6, "text");
	String finalStr6="<![CDATA[Total Earnings]]>";
	addText(document,textFieldExpression6,finalStr6);
	
	Element staticText36 = addNode(document, band2, "staticText");
	Element reportElement36 = addNode(document, staticText36, "reportElement");
	reportElement36.setAttribute("x",Integer.toString(headTextFieldX+95));
	reportElement36.setAttribute("y",Integer.toString(setY));
	reportElement36.setAttribute("width","95");
	reportElement36.setAttribute("height","32");
	Element textElement36=addNode(document, staticText36, "textElement");
	textElement36.setAttribute("verticalAlignment","Top");
	Element font36=addNode(document, textElement36, "font");
	font36.setAttribute("isBold","true");
	font36.setAttribute("size", "11");
	Element textFieldExpression36 = addNode(document,staticText36, "text");
	String finalStr36="<![CDATA["+totalEarnings+"]]>";
	addText(document,textFieldExpression36,finalStr36);
	
	Element staticText7 = addNode(document, band2, "staticText");
	Element reportElement7 = addNode(document, staticText7, "reportElement");
	reportElement7.setAttribute("x",Integer.toString(headTextFieldX+190));
	reportElement7.setAttribute("y",Integer.toString(setY));
	reportElement7.setAttribute("width","95");
	reportElement7.setAttribute("height","32");
	Element textElement7=addNode(document, staticText7, "textElement");
	textElement7.setAttribute("verticalAlignment","Top");
	Element font7=addNode(document, textElement7, "font");
	font7.setAttribute("isBold","true");
	font7.setAttribute("size", "11");
	Element textFieldExpression7= addNode(document,staticText7, "text");
	String finalStr7="<![CDATA[Total Deductions ]]>";
	addText(document,textFieldExpression7,finalStr7);
	
	Element staticText17 = addNode(document, band2, "staticText");
	Element reportElement17 = addNode(document, staticText17, "reportElement");
	reportElement17.setAttribute("x",Integer.toString(headTextFieldX+285));
	reportElement17.setAttribute("y",Integer.toString(setY));
	reportElement17.setAttribute("width","95");
	reportElement17.setAttribute("height","32");
	Element textElement17=addNode(document, staticText17, "textElement");
	textElement17.setAttribute("verticalAlignment","Top");
	Element font17=addNode(document, textElement17, "font");
	font17.setAttribute("isBold","true");
	font17.setAttribute("size", "11");
	Element textFieldExpression17= addNode(document,staticText17, "text");
	String finalStr17="<![CDATA["+totalDeduction+"]]>";
	addText(document,textFieldExpression17,finalStr17);
	
	Element staticText10 = addNode(document, band2, "staticText");
	Element reportElement10 = addNode(document, staticText10, "reportElement");
	reportElement10.setAttribute("x",Integer.toString(headTextFieldX));
	reportElement10.setAttribute("y",Integer.toString(setY+25));
	reportElement10.setAttribute("width","95");
	reportElement10.setAttribute("height","32");
	Element textElement10=addNode(document, staticText10, "textElement");
	textElement10.setAttribute("verticalAlignment","Top");
	Element font10=addNode(document, textElement10, "font");
	font10.setAttribute("isBold","true");
	font10.setAttribute("size", "13");
	Element textFieldExpression10= addNode(document,staticText10, "text");
	String finalStr10="<![CDATA[Net Salary ]]>";
	addText(document,textFieldExpression10,finalStr10);
	
	
	Element staticText13 = addNode(document, band2, "staticText");
	Element reportElement13 = addNode(document, staticText13, "reportElement");
	reportElement13.setAttribute("x",Integer.toString(headTextFieldX+95));
	reportElement13.setAttribute("y",Integer.toString(setY+23));
	reportElement13.setAttribute("width","95");
	reportElement13.setAttribute("height","32");
	Element textElement13=addNode(document, staticText13, "textElement");
	textElement13.setAttribute("verticalAlignment","Top");
	Element font13=addNode(document, textElement13, "font");
	font13.setAttribute("isBold","true");
	font13.setAttribute("size", "13");
	Element textFieldExpression13= addNode(document,staticText13, "text");
	String finalStr13="<![CDATA["+(totalEarnings-totalDeduction)+" ]]>";
	addText(document,textFieldExpression13,finalStr13);
	
	Element staticText14 = addNode(document, band2, "staticText");
	Element reportElement14 = addNode(document, staticText14, "reportElement");
	reportElement14.setAttribute("x",Integer.toString(headTextFieldX+190));
	reportElement14.setAttribute("y",Integer.toString(setY+25));
	reportElement14.setAttribute("width","200");
	reportElement14.setAttribute("height","40");
	Element textElement14=addNode(document, staticText14, "textElement");
	textElement14.setAttribute("verticalAlignment","Top");
	Element font14=addNode(document, textElement14, "font");
	font14.setAttribute("isBold","true");
	font14.setAttribute("size", "8");
	Element textFieldExpression14= addNode(document,staticText14, "text");
	String finalStr14="<![CDATA["+convert()+" ]]>";
	addText(document,textFieldExpression14,finalStr14);
	
	
	Element staticText15 = addNode(document, band2, "staticText");
	Element reportElement15 = addNode(document, staticText15, "reportElement");
	reportElement15.setAttribute("x",Integer.toString(headTextFieldX+200));
	reportElement15.setAttribute("y",Integer.toString(setY+60));
	reportElement15.setAttribute("width","250");
	reportElement15.setAttribute("height","40");
	Element textElement15=addNode(document, staticText15, "textElement");
	textElement15.setAttribute("verticalAlignment","Top");
	Element font15=addNode(document, textElement15, "font");
	font15.setAttribute("isBold","true");
	font15.setAttribute("size", "12");
	Element textFieldExpression15= addNode(document,staticText15, "text");
	String finalStr15="<![CDATA[For Konnect Consultancy Services Private Ltd.]]>";
	addText(document,textFieldExpression15,finalStr15);
	
	
	Element staticText16 = addNode(document, band2, "staticText");
	Element reportElement16 = addNode(document, staticText16, "reportElement");
	reportElement16.setAttribute("x",Integer.toString(headTextFieldX+270));
	reportElement16.setAttribute("y",Integer.toString(setY+90));
	reportElement16.setAttribute("width","250");
	reportElement16.setAttribute("height","40");
	Element textElement16=addNode(document, staticText16, "textElement");
	textElement16.setAttribute("verticalAlignment","Top");
	Element font16=addNode(document, textElement16, "font");
	font16.setAttribute("isBold","true");
	font16.setAttribute("size", "12");
	Element textFieldExpression16= addNode(document,staticText16, "text");
	String finalStr16="<![CDATA[Authorized Signatory]]>";
	addText(document,textFieldExpression16,finalStr16);
//===============================================================

	try
	{
		filePath = request.getRealPath("/images1/")+File.separator;
		
		printToFile(document, filePath,	"company1.jrxml");
		replace();
		jasperReport = JasperCompileManager.compileReport(filePath+"company1.jrxml");
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,new HashMap(), new JREmptyDataSource());
		System.out.println("====================================================================================");
		JasperExportManager.exportReportToPdfFile(jasperPrint,filePath + strEmpId+".pdf");
//		JasperDesignViewer jasperDesignViewer = new JasperDesignViewer(jasperReport);
//		jasperDesignViewer.setVisible(true);
	}
	
	catch(JRException e)
	{
		e.printStackTrace();
		
	}
}
	
	public void replace()
	{
		 try
         {
         String fileData = "";
         BufferedReader reader = new BufferedReader(
                 new FileReader(filePath+"company1.jrxml"));
         char[] buf = new char[1024];
         int numRead=0;
         while((numRead=reader.read(buf)) != -1){
             String readData = String.valueOf(buf, 0, numRead);
             fileData = fileData + (readData);
             buf = new char[1024];
         }
         reader.close();
         
         String ltString = fileData.replace("&lt;", "<");
         String gtString = ltString.replace("&gt;", ">");
         BufferedWriter out = new BufferedWriter(new FileWriter(filePath+"company1.jrxml"));
         out.write(gtString);
         out.close();
         
         }catch(Exception e)
         {
                 e.printStackTrace();
         }
	}
	
	public void printUpperPart(Element band2,String[] array,List<List<String>> array2,int x,int y)
	{}
	
	public String convert()
	{
		String Str="",Str2="";
		double i=totalEarnings-totalDeduction;
        int num=(int)i;
        Str = new originalNumToLetter().execute(Integer.toString(num));
        double temp =(i-num)*100;
        int paisa =(int)temp;
        System.out.println("i= "+i+"num="+num+" temp="+temp);
        Str2=new originalNumToLetter().execute(Integer.toString(paisa));
		if(Str2.equals("")==false)
			Str = Str+" Rupees and "+ Str2+" Paisa";
		else
			Str = Str+" Rupees";
        
		return Str;
	}
	
	
	public Document createDocument() {
		Document document=null;
        try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return document;
    }
	
	public void printToFile(Document document,String strFilePath,String strFileName) {
        File file=new File(strFilePath);
        if(!file.isDirectory()){
        	out.println("Following Path does not exist:\n"+strFilePath);
            return;
        }
        
        try {
            OutputFormat format = new OutputFormat(document);
            format.setIndenting(true);
            XMLSerializer serializer = new XMLSerializer(new FileOutputStream(strFilePath+strFileName), format);
            serializer.serialize(document);
        } catch(IOException ie) {
            ie.printStackTrace();
        }
    }
	
	public Element addRootNode(Document document,String strRootNodeName) {
		Element rootNode=null;
		try {
			rootNode = document.createElement(strRootNodeName);
			document.appendChild(rootNode);
		} catch (DOMException e) {
			e.printStackTrace();
		}
        return rootNode;
    }
    
	public Element addNode(Document document, Element parentElement, String nodeName) {
    	Element childElement=null;
        try {
			childElement = document.createElement(nodeName);
			parentElement.appendChild(childElement);
		} catch (DOMException e) {
			e.printStackTrace();
		}
		
		return childElement;
    }
    
	public static void addText(Document document, Element parentElement, String strText) {
    	if(strText==null)
    		return;
    	
        try {
			Text text = document.createTextNode(strText);
			parentElement.appendChild(text);
			
        } catch (DOMException e) {
			e.printStackTrace();
		}
    }


	@Override
	public void setServletRequest(HttpServletRequest request)
	{
		this.request=request;
	}
	
	public void initialize()
	{
		UtilityFunctions uF=new UtilityFunctions();
		try
		{
			Database dbConnect=new Database();
			con =  dbConnect.makeConnection(con);
			
			stmt = con.prepareStatement(salaryDetails,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, Integer.parseInt(strEmpId) );
			stmt.setDate(2, uF.getDateFormat(startDate, cF.getStrReportDateFormat()) );
			stmt.setDate(3, uF.getDateFormat(endDate, cF.getStrReportDateFormat()) );
			stmt.setInt(4, Integer.parseInt(strEmpId) );
			stmt.setDate(5,uF.getDateFormat(startDate, cF.getStrReportDateFormat()) );
			stmt.setDate(6,uF.getDateFormat(endDate, cF.getStrReportDateFormat()));
			stmt.setInt(7, Integer.parseInt(strEmpId));
			rs=stmt.executeQuery();
			rs.next();
			strEmail=rs.getString("emp_email");
		
			stmt2 = con.prepareStatement("select * from salary_details",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			rs2=stmt2.executeQuery();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}	
	}
	
	public void closeConn()
	{
		try
		{
			this.con.close();
			System.out.println("Connection closed");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public List<List<String>> getDataLeft()
	{
		List<List<String>> al = new ArrayList<List<String>>();
		List alInner = new ArrayList();
		
		try
		{
			alInner = new ArrayList<String>();
			alInner.add(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
			alInner.add("Emplyee ID No");
			alInner.add("Acc No.");
			alInner.add(rs.getString("no_of_working_days"));
			alInner.add(rs.getString("days_present"));
			alInner.add("Salary Number");
			al.add(alInner);                                    
		}
		catch(SQLException e)
		{
				e.printStackTrace();			
		}
		
		return al;
	}
	
	public List<List<String>> getDataRight()
	{
		List<List<String>> al = new ArrayList<List<String>>();
		List alInner = new ArrayList();
		
		try
		{
			alInner = new ArrayList<String>();
			alInner.add(rs.getString("joining_date"));
			alInner.add(rs.getString("desig_name"));
			alInner.add("Paid leaves");
			alInner.add("P.F.");
			alInner.add("Leave without pay");
			al.add(alInner);
		
			
		}
		catch(SQLException e)
		{
				e.printStackTrace();			
		}
		return al;
	}
	
	public String[] getData()
	{
		String array[]=new String[16];
		int i=0;
		try
		{
		
			while(rs2.next())
			{
				System.out.println("value of i------->>"+i);
				array[i]=rs2.getString("salary_head_name");
				i++;
				//rs2.next();
			}
						
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			
		}
		return array;
	}
	
	public String[] getDataLocation()
	{
		String array2[]=new String[16];
		int i=0;
		
		try
		{
			rs2.first();
			while(rs2.next())
			{
				array2[i]=rs2.getString("salary_head_byte");
				i++;
					//rs2.next();
			}
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();			
		}
		System.out.println(array2.length);
		return array2;
	}
	
public String[] calculate(String[] head, String [] category)
{
		String array[]=new String[16];
		String calculateType="";
		String type;
		double number;
		int j=0;
		try
		{
			rs2.first();
			while(rs2.next())
			{
				calculateType=rs2.getString("salary_head_amount_type");
				number=Double.parseDouble(((rs2.getString("salary_head_value"))));
				type=rs2.getString("salary_head_byte");
				
				if(calculateType.trim().equals("P"))
				{
					if(type.trim().equals("E"))
						array[j]=Double.toString((totalEarnings*(number/100)));
					else
						array[j]=Double.toString((totalDeduction*(number/100)));
					
				}
				else
				{
					array[j]=Double.toString(number);
				}
				j++;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();				
		}
			return array;
	}


	private List<String> getAddressRelatedInfo()
	{
		List<String> addressrelated=new ArrayList<String>(); 
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		Connection con = null;
		con = db.makeConnection(con);	
		UtilityFunctions uF=new UtilityFunctions();
	
		try
		{
			pst = con.prepareStatement(getAddress);
    	  	pst.setInt(1,uF.parseToInt(strEmpId));
    		rs=pst.executeQuery();
    		rs.next();
    	 	
   			addressrelated.add(rs.getString("wlocation_name"));
   			addressrelated.add(uF.showData(rs.getString("wlocation_logo"),""));
   			addressrelated.add(rs.getString("city_name")+","+rs.getString("state_name")+"\n"+rs.getString("country_name")+"\n"+"Pin: "+rs.getString("wlocation_pincode")+"\n"+"Ph: "+rs.getString("wlocation_contactno")+"  "+"Fax: "+rs.getString("wlocation_faxno"));
		}	
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{	
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return addressrelated;
}

public void setStartDate(String startDate)
{
	this.startDate=startDate;
}
public void setEndDate(String endDate)
{
	this.endDate=endDate;
}
	
}
