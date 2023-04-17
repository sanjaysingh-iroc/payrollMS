//package com.konnect.jpms.salary;
//
//import static java.lang.System.out;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.text.DateFormat;
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.apache.struts2.interceptor.ServletRequestAware;
//import org.w3c.dom.DOMException;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Text;
//
//import com.konnect.jpms.util.CommonFunctions;
//import com.konnect.jpms.util.Database;
//import com.konnect.jpms.util.IConstants;
//import com.konnect.jpms.util.IStatements;
//import com.konnect.jpms.util.Notifications;
//import com.konnect.jpms.util.UtilityFunctions;
//import com.opensymphony.xwork2.ActionSupport;
//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
//
//
//public class generateHourlySalarySlip extends ActionSupport implements IStatements,IConstants,ServletRequestAware
//{
//	
//	
//	private static final long serialVersionUID = 1L;
//	String startDate=null,endDate=null;
//	Document document = null;
//		
//	private HttpServletRequest request;
//
//	String strDateFormat = "MM";
//	SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
//	String monthNo=sdf.format(new Date());
//	String userServiceListStr;
//	String strEmpId; 
//	private ResultSet rsLeftPart,rsWorkingDaysUpper,rsDayWise,rsLeave,rsStatic;
//	private Connection con;
//	private PreparedStatement stmt,stmt2,stmt3,stmtLeaves,stmtStatic;
//	private PreparedStatement prepForLeaves; 
//	String[] temp;
//	int countPayableOnThisMonth=0,countPayableOnThisMonthOff=0;
//	CommonFunctions cF=null;
//	String filePath;
//	
//	public String execute()
//	{
//		HttpSession session = request.getSession();
//		strEmpId = (String)request.getAttribute("empIDPay");
//
//		cF = (CommonFunctions) session.getAttribute(CommonFunctions);
//		generateHourlySlip();
//		
//		//String strEmail=getEmpEmail(strEmpId);
//		
//		List alDate = (List) request.getAttribute("alDate");
//		
//		
//		
//		
//		Notifications nF = new Notifications(N_SALARY_SLIP);
//		String strEmail = "bhushan.konnecg@gmail.com";
//		nF.setStrEmailTo(strEmail);
//		String filePath = request.getRealPath("/images1/")+File.separator;
//	
//		nF.setStrAttachmentFileName(filePath + strEmpId+".pdf");
//		nF.sendNotifications();
//		
//		return SUCCESS;
//	}
//	
//	public void generateHourlySlip()
//	{
//		final Map hm = new HashMap();
//		Element stock_exchange_node = null;
//		
//		document = createDocument();
//		Element rootNode = addRootNode(document, "jasperReport");
//		rootNode.setAttribute("name","SalaryDetails");
//		Element childnode = null;
//		JasperReport jasperReport;
//		initialize();
//		double allowance=300;
//		double PFRate=0.12, professionalTax=200;
//		double finalWage=0.00;
//		DecimalFormat df = new DecimalFormat("#.##");
//		double fixedRate=20.00;
//		double leavesAmount=00;
//		List<String> leftUpperPart= leftUpperData();
//		
//		//================Adding Image to the page header===========================
//		Element pageHeader = addNode(document, rootNode, "pageHeader");
//		Element band = addNode(document, pageHeader, "band");
//		band.setAttribute("height","100");
//		Element staticTextTitle = addNode(document, band, "staticText");
//		Element reportElementTitle = addNode(document, staticTextTitle, "reportElement");
//		reportElementTitle.setAttribute("x",Integer.toString(170));
//		reportElementTitle.setAttribute("y",Integer.toString(0));
//		reportElementTitle.setAttribute("width","400");
//		reportElementTitle.setAttribute("height","50");
//		reportElementTitle.setAttribute("mode","Opaque");
//		reportElementTitle.setAttribute("backcolor","yellow");
//		Element textElementTitle=addNode(document, staticTextTitle, "textElement");
//		textElementTitle.setAttribute("verticalAlignment","Middle");
//		Element fontTitle=addNode(document, textElementTitle, "font");
//		fontTitle.setAttribute("isBold","true");
//		fontTitle.setAttribute("size", "20");
//		
//		Element staticTextAddressTitle = addNode(document, band, "staticText");
//		Element reportElementAddressTitle = addNode(document, staticTextAddressTitle, "reportElement");
//		reportElementAddressTitle.setAttribute("x",Integer.toString(170));
//		reportElementAddressTitle.setAttribute("y",Integer.toString(38));
//		reportElementAddressTitle.setAttribute("width","400");
//		reportElementAddressTitle.setAttribute("height","50");
//		reportElementAddressTitle.setAttribute("mode","Opaque");
//		reportElementAddressTitle.setAttribute("backcolor","yellow");
//		Element textElementAddressTitle=addNode(document, staticTextAddressTitle, "textElement");
//		textElementAddressTitle.setAttribute("verticalAlignment","Middle");
//		Element fontAddressTitle=addNode(document, textElementAddressTitle, "font");
//		fontAddressTitle.setAttribute("isBold","false");
//		fontAddressTitle.setAttribute("size", "10");
//		
//		Element textFieldExpressionTitle = addNode(document,staticTextTitle, "text");
//
//		List<String> companyAddress=getAddressRelatedInfo();
//		
//		
//		String finalStrTitle="<![CDATA["+companyAddress.get(0)+"]]>";
//		addText(document,textFieldExpressionTitle,finalStrTitle);	
//		
//		Element textFieldExpressionAddressTitle = addNode(document,staticTextAddressTitle, "text");
//		String finalStrAddressTitle="<![CDATA["+companyAddress.get(2)+"]]>";
//		addText(document,textFieldExpressionAddressTitle,finalStrAddressTitle);	
//	
//	
//		Element logo = addNode(document, band, "image");
//		logo.setAttribute("hyperlinkType", "none");
//		Element reportElement = addNode(document, logo, "reportElement");
//		reportElement.setAttribute("x", "0");
//		reportElement.setAttribute("y", "0");
//		reportElement.setAttribute("width", "100");
//		reportElement.setAttribute("height", "100");
//		Element graphicElement = addNode(document, logo, "graphicElement");
//		graphicElement.setAttribute("stretchType", "RelativeToBandHeight");
//		
//		Element logoExpression = addNode(document,logo, "imageExpression");
//		logoExpression.setAttribute("class","java.io.File");
//		logoExpression.setTextContent("<![CDATA[(new File(\""+companyAddress.get(1)+"\"))]]>");
//		//==========================================================================
//		
//		//==========================================================================	
//		Element detail = addNode(document, rootNode, "detail");
//		Element band2 = addNode(document, detail, "band");
//		band2.setAttribute("height","600");
//		lines(band2,0,0,570,0);
//		texts(band2,155,5,240,30,"Pay Slip for the month/week","15","true");
//		lines(band2,0,33,560,0);
//		
//		
//		//==================Printing the Left Heads==============================
//		List<String> leftHeads=new ArrayList<String>();
//		leftHeads.add("Employee No.");
//		leftHeads.add("Name");
//		leftHeads.add("Department");
//		leftHeads.add("Bank Name");
//		leftHeads.add("Bank A/c No.");
//		leftHeads.add("Cost Centre");
//		leftHeads.add("Type of Employement");
//		leftHeads.add("Description");
//		leftHeads.add("Pay Details");
//		int count=countServicesServedInAMonth();
//		System.out.println("Count is as follows::>"+count);
//			String servicesList[]=null;
//		try
//		{
//			servicesList=leftUpperPart.get(5).split(",");
//		
//			for(int j=0; j<count; j++)
//			{
//				leftHeads.add("Normal("+ servicesList[j]+")");
//				leftHeads.add("weekend/Holiday("+servicesList[j]+")");
//				System.out.println("services list in loop header"+servicesList[j]);
//			}
//		}
//		catch(Exception e)
//		{
//			System.out.println("null pointer exception");
//		}
//		
//		leftHeads.add("Total Wages");
//		leftHeads.add("Add:Allowance");
//		leftHeads.add("Less:Deduction");
//		leftHeads.add("Permanent Deduction");
//		leftHeads.add("Professional Tax");
//		leftHeads.add("PF");
//		leftHeads.add("TDS");
//		leftHeads.add("Paid Leaves");
//		leftHeads.add("Wages Payble");
//		
//		Iterator itr = leftHeads.iterator(); 
//		int x=5,y=38;
//		int lineCounter=0;
//		for(int j=0;itr.hasNext();j++)
//		{
//				if(leftHeads.get(j).equals("Total Wages") || leftHeads.get(j).equals("Wages Payble") || leftHeads.get(j).equals("Permanent Deduction"))
//				{
//					lines(band2,0,y-5,560,0);
//					lines(band2,0,y+15,560,0);
//				}
//				texts(band2,x,y,140,25,itr.next().toString(),"9","true");
//				y=y+20;
//				lineCounter=j;
//		}			
//		//====================================================================================
//		
//		//=========================Printing the vertical lines=======lines(band,x,y,width,height)=========================
//		x=140;
//		y=33;
//		int height=20;
//		
//		for(int j=0;j<4; j++)	
//		{
//			y=33;
//			
//			for(int counter=0; counter<=lineCounter; counter++)
//			{
//				lines(band2,0,y,0,height);
//				lines(band2,x,y,0,height);
//				if(y>=193)
//					lines(band2,x+70,y-3,0,height+3);
//				y+=20;
//			}
//			x=x+140;
//		}
//			
//		
//		
//
//	//======================================================================================
//		//=====================lower Part=======================================================
//		texts(band2,145,198,140,30,"No. Of. Days","9","true");
//		texts(band2,220,198,140,30,"Hours/Day","9","true");
//		texts(band2,295,198,140,30,"Total hrs.","9","true");
//		texts(band2,370,198,140,30,"Rate","9","true");
//		texts(band2,445,198,140,30,"Adjust","9","true");
//		texts(band2,520,198,140,30,"Amount","9","true");
//	//======================================================================================
//		
//		
//	//==================Printing the RightHeads==============================
//		List<String> rightHeads=new ArrayList<String>();
//		rightHeads.add("Payable Days");
//		rightHeads.add("PF No.");
//		rightHeads.add("ESI No");
//		rightHeads.add("PAN No.");
//		rightHeads.add("Payment cycle");
//		rightHeads.add("Pay Point");
//		Iterator itrRightHeads = rightHeads.iterator(); 
//		//String rightHeads[]={"Payable Days","PF No.", "ESI No","PAN No.", "Payment cycle","Pay Point"};
//		x=285;
//		y=38;
//		for(int j=0;itrRightHeads.hasNext();j++)
//		{
//			texts(band2,x,y,140,30,itrRightHeads.next().toString(),"9","true"); //texts(band,x,y,width,height,text,textsize)
//			y=y+20;
//		}	
//  //====================================================================================	
//	
//  //=======================Horizontal lines=================lines(Element band,int x,int y,int width,int height)
//		lines(band2,0,170,560,0);
//		lines(band2,0,190,560,0);
//		lines(band2,0,210,560,0);
// //=============================================================================================================
//		
////==================Left and right upper values=================================================================
//		
//		Iterator itrLeftValues = leftUpperPart.iterator();
//		System.out.println("Left upper values"+leftUpperPart);
//		x=145;
//		y=38;
//		try {
//			while(itrLeftValues.hasNext())
//			{
//				
//				texts(band2,x,y,140,30,itrLeftValues.next().toString(),"9","false"); //texts(band,x,y,width,height,text,textsize,bold true/false)
//				y=y+20;
//			}
//		
//		} catch(Exception e) {
//			System.out.println("Exception occured..");
//		}
//		
//		List<String> rightUpperPart= rightUpperData();
//		x=425;
//		y=38;
//		Iterator itrRightValues = rightUpperPart.iterator();
//		for(int j=0; itrRightValues.hasNext(); j++)
//		{
//			texts(band2,x,y,140,30,itrRightValues.next().toString(),"9","false"); //texts(band,x,y,width,height,text,textsize,bold true/false)
//			y=y+20;
//		}
////=======================================================================================
//		
////==================middle part upper values====================================================
//		String[] passService=null;
//		try
//		{
//			passService=servicesList;
//		}
//		catch(NullPointerException e)
//		{
//			System.out.println("Null");
//		}
//		int countdays;
//		List<String> ServiceDays=new ArrayList<String>();
//		List<String> rates=new ArrayList<String>();
//		
//		x=145;
//		y=218;
//		Double totalWages=0.0;
//		Double totalBasic=0.0;
//		int countForBasic=0;
//		try
//		{
//			for(int j=0; j<passService.length; j++)
//			{
//				ServiceDays=countLigitimateDays(passService[j]);
//				rates=getRates(passService[j]);
//			
//				System.out.println(passService[j]);
//				Iterator itrDays = ServiceDays.iterator();
//				Iterator ratesList = rates.iterator();
//				while(itrDays.hasNext())
//				{
//					String current=itrDays.next().toString();
//					texts(band2,x,y,140,30,current,"9","false"); //texts(band,x,y,width,height,text,textsize,bold true/false)
//					texts(band2,x+70,y,140,30,"10","9","false"); //texts(band,x,y,width,height,text,textsize,bold true/false)
//					String calc=Integer.toString(Integer.parseInt(current)*10);
//					texts(band2,x+140,y,140,30,calc,"9","false");
//					String rate=ratesList.next().toString();
//					if(current.trim().equals("0"))
//					{
//						texts(band2,x+210,y,140,30,"0","9","false");//texts(band,x,y,width,height,text,textsize,bold true/false)
//						//ratesList.next().toString();
//					}	
//					else	
//						texts(band2,x+210,y,140,30,rate,"9","false");//texts(band,x,y,width,height,text,textsize,bold true/false)
//					double calculateAmount=Double.parseDouble(rate)*Double.parseDouble(calc);
//					totalWages=totalWages+calculateAmount;
//					if(countForBasic%2==0)
//						totalBasic=totalBasic+calculateAmount;
//					countForBasic++;
//				
//					texts(band2,x+350,y,140,30,df.format(calculateAmount),"9","false");//texts(band,x,y,width,height,text,textsize,bold true/false)
//					y=y+20;
//				}
//			}
//		}
//		catch(NullPointerException e)
//		{
//			System.out.println("null poninter exception");
//		}
//		
//		texts(band2,x+350,y,140,30,df.format(totalWages),"9","false");//texts(band,x,y,width,height,text,textsize,bold true/false)
//		y=y+20;
////==================================================================================================
//
////==========Bottom part=======================================================================
//		//allowance=getAllowance(amount)
//		texts(band2,x+350,y,140,30,Double.toString(allowance),"9","false");//texts(band,x,y,width,height,text,textsize,bold true/false)
//		y+=60;
//		texts(band2,x+350,y,140,30,Double.toString(professionalTax),"9","false");
//		y+=20;
//		double totalPF=totalBasic*PFRate;
//		
//		
//		
//		String PF=df.format(totalPF);
//		texts(band2,x+140,y,140,30,Double.toString(totalBasic),"9","false");
//		texts(band2,x+210,y,140,30,Double.toString(PFRate),"9","false");
//		
//		texts(band2,x+350,y,140,30,PF,"9","false");
//		y+=40;
//		
//		String leavesPay=leavesPay();
//		texts(band2,x+140,y,140,30,(Integer.parseInt(leavesPay)*10)+" ( "+leavesPay+"Days )","9","false");
//		texts(band2,x+210,y,140,30,Double.toString(fixedRate),"9","false");
//		
//		leavesAmount=fixedRate*Double.parseDouble(leavesPay)*10;
//		texts(band2,x+350,y,140,30,Double.toString(leavesAmount),"9","false");
//		y+=20;
//		finalWage=totalWages+allowance-totalPF-professionalTax+leavesAmount;
//		
//		texts(band2,x+350,y,140,30,Double.toString(finalWage),"9","false");
//		
//		
//		
////=============================================================================================
//		try
//		{
//			filePath = request.getRealPath("/images1/")+File.separator;
//			System.out.println("filePath=>"+filePath);
//			printToFile(document, filePath,	"company1.jrxml");
//			replace();
//			jasperReport = JasperCompileManager.compileReport(filePath+"company1.jrxml");
//			
//			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,new HashMap(), new JREmptyDataSource());
//			System.out.println("====================================================================================");
//			JasperExportManager.exportReportToPdfFile(jasperPrint,filePath + strEmpId+".pdf");
//			
////			JasperDesignViewer jasperDesignViewer = new JasperDesignViewer(jasperReport);
////			jasperDesignViewer.setVisible(true);
//		}
//		catch(JRException e)
//		{
//			e.printStackTrace();
//		}
//}
//	
//		
//
//
//
//public void replace()
//{
//	 try
//     {
//     String fileData = "";
//     BufferedReader reader = new BufferedReader(new FileReader(filePath+"company1.jrxml"));
//     char[] buf = new char[1024];
//     int numRead=0;
//     while((numRead=reader.read(buf)) != -1){
//         String readData = String.valueOf(buf, 0, numRead);
//         fileData = fileData + (readData);
////         System.out.println(fileData);
//         buf = new char[1024];
//     }
//     reader.close();
//     
//     String ltString = fileData.replace("&lt;", "<");
//     String gtString = ltString.replace("&gt;", ">");
//     BufferedWriter out = new BufferedWriter(new FileWriter(filePath+"company1.jrxml"));
//     out.write(gtString);
//     out.close();
//     
//     }catch(Exception e)
//     {
//             e.printStackTrace();
//     }
//}
//
//public void lines(Element band,int x,int y,int width,int height)
//{
//	//====================Fixed horizontal lines================================
//	Element HlineNode = addNode(document, band, "line");
//	Element HReportElement = addNode(document, HlineNode, "reportElement");
//	HReportElement.setAttribute("x", Integer.toString(x));
//	HReportElement.setAttribute("y", Integer.toString(y));
//	HReportElement.setAttribute("width", Integer.toString(width));
//	HReportElement.setAttribute("height",Integer.toString(height));	
//	//=====================================================================
//}
//public void texts(Element band,int x,int y, int width,int height,String text,String textSize,String bold)
//{
//	Element staticText = addNode(document, band, "staticText");
//	Element reportElement = addNode(document, staticText, "reportElement");
//	reportElement.setAttribute("x",Integer.toString(x));
//	reportElement.setAttribute("y",Integer.toString(y));
//	reportElement.setAttribute("width",Integer.toString(width));
//	reportElement.setAttribute("height",Integer.toString(height));
//	Element textElement=addNode(document, staticText, "textElement");
//	textElement.setAttribute("verticalAlignment","Top");
//	Element font=addNode(document, textElement, "font");
//	font.setAttribute("isBold",bold);
//	font.setAttribute("size", textSize);
//	Element textFieldExpression = addNode(document,staticText, "text");
//	String finalStr="<![CDATA["+text+"]]>";
//	addText(document,textFieldExpression,finalStr);
//}
//
//public Element addRootNode(Document document,String strRootNodeName) {
//	Element rootNode=null;
//	try {
//		rootNode = document.createElement(strRootNodeName);
//		document.appendChild(rootNode);
//	} catch (DOMException e) {
//		e.printStackTrace();
//	}
//    return rootNode;
//}
//
//public void printToFile(Document document,String strFilePath,String strFileName) {
//	
//    File file=new File(strFilePath);
//    
//    if(!file.isDirectory()){
//    	out.println("Following Path does not exist:\n"+strFilePath);
//        return;
//      }
//    
//    try {
//    	
//    	
//        OutputFormat format = new OutputFormat(document);
//        format.setIndenting(true);
//        XMLSerializer serializer = new XMLSerializer(new FileOutputStream(strFilePath+strFileName), format);
//        serializer.serialize(document);
//               
//	    } catch(IOException ie) {
//	        ie.printStackTrace();
//	    }
//}
//
//public Element addNode(Document document, Element parentElement, String nodeName) {
//	Element childElement=null;
//    try {
//		childElement = document.createElement(nodeName);
//		parentElement.appendChild(childElement);
//	} catch (DOMException e) {
//		e.printStackTrace();
//	}
//	
//	return childElement;
//}
//
//public Document createDocument() 
//{
//	Document document=null;
//    try {
//		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//		document = documentBuilder.newDocument();
//	} catch (ParserConfigurationException e) {
//		e.printStackTrace();
//	}
//	return document;
//}
//
//public static void addText(Document document, Element parentElement, String strText) {
//	if(strText==null)
//		return;
//	
//    try {
//		Text text = document.createTextNode(strText);
//		parentElement.appendChild(text);
//		
//    } catch (DOMException e) {
//		e.printStackTrace();
//	}
//}
//
//@Override
//public void setServletRequest(HttpServletRequest request) 
//{
//	this.request=request;
//}
//
//public void initialize()
//{
//	Database dbConnect;
//	UtilityFunctions uF=new UtilityFunctions();
//	CommonFunctions CF=new CommonFunctions(cF);
//	try
//	{
//		dbConnect=new Database();
//		con =  dbConnect.makeConnection(con);
//		stmt = con.prepareStatement(salaryDetailsLeftValues,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
//		stmt.setInt(1, Integer.parseInt(strEmpId));
//		System.out.println("initializing::::::::::::::::"+startDate);
//		stmt.setDate(2, uF.getDateFormat(startDate, cF.getStrReportDateFormat()));
//		stmt.setDate(3, uF.getDateFormat(endDate, cF.getStrReportDateFormat()));
//			
//		rsLeftPart=stmt.executeQuery();	
//		rsLeftPart.next();
//		//Select * from (Select * from (Select * from(select * from payroll, services where emp_id=82 and CAST(generate_date AS char(20)) like '_____09%' and payroll.service_id=services.service_id) pr LEFT JOIN employee_personal_details epd on epd.emp_per_id=pr.emp_id) apd  LEFT JOIN employee_official_details eod on apd.emp_per_id=eod.emp_id) adi LEFT JOIN department_info di on adi.depart_id = di.dept_id
//		stmt2 = con.prepareStatement(countWorkingDays,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
//		stmt2.setDate(1,  uF.getDateFormat(startDate, cF.getStrReportDateFormat()));
//		stmt2.setDate(2,  uF.getDateFormat(endDate, cF.getStrReportDateFormat()));
//		stmt2.setInt(3,Integer.parseInt(strEmpId));
//		rsWorkingDaysUpper=stmt2.executeQuery();	
//		rsWorkingDaysUpper.next();
//		countServices();
//		
//		stmtStatic = con.prepareStatement(getStaticEmployeeData,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
//		stmtStatic.setInt(1, Integer.parseInt(strEmpId));
//		rsStatic=stmtStatic.executeQuery();	
//		//rsStatic.next();
//	 }
//		catch(SQLException e)
//		{
//			e.printStackTrace();
//		}	
//				
//}
//
//public List<String> leftUpperData()
//{
//	List<String> leftDataList=new ArrayList<String>();
//	String str="";
//	UtilityFunctions uF=new UtilityFunctions();
//	
//	try
//	{
//		
//		while(rsStatic.next())	
//		{
//			//System.out.println(rsStatic.getString("emp_per_id")+rsStatic.getString("emp_fname")+" "+rsStatic.getString("emp_lname"));
//			leftDataList.add(rsStatic.getString("emp_per_id"));
//			leftDataList.add(rsStatic.getString("emp_fname")+" "+rsStatic.getString("emp_lname"));
//			leftDataList.add(rsStatic.getString("dept_name"));
//			leftDataList.add(uF.showData(rsStatic.getString("emp_bank_name"),""));
//			leftDataList.add(uF.showData(rsStatic.getString("emp_bank_acct_nbr"),""));
//		
//			if(temp!= null && temp.length > 0) {
//				for(int j=0;j<temp.length;j++)
//				{
//					if(temp[j].equals("NA")==true)
//						continue;
//					else
//						str=str+temp[j]+",";
//				}
//				userServiceListStr=str;
//	
//				System.out.println("services list::>"+str);
//				leftDataList.add(str);	
//			}
//			
//			leftDataList.add(uF.showData(rsStatic.getString("emptype"),""));
//		}
//		
//	}
//	catch(SQLException e)
//	{
//		e.printStackTrace();
//	}
//	
//	return leftDataList;
//}
//
//public List<String> rightUpperData()
//{
//	List<String> rightDataList=new ArrayList<String>();
//	UtilityFunctions uF=new UtilityFunctions();
//	try
//	{
//		
//		rightDataList.add(rsWorkingDaysUpper.getString(1));
//		rightDataList.add("");
//		rightDataList.add("");
//		rightDataList.add("");
//		rsStatic.first();
//		System.out.println();
//		while(rsStatic.next())
//			rightDataList.add((uF.showData(Integer.toString(rsStatic.getInt("emp_pan_no")),"")));
//		rightDataList.add("Monthly");
//		
//		
//	}
//	catch(SQLException e)
//	{
//		e.printStackTrace();
//	}
//	
//	return rightDataList;
//}
//
//public int countServicesServedInAMonth()
//{
//	int count=0;
//	if(temp !=null)
//	{
//		for(int i=0; i<temp.length; i++)
//		{
//			if(temp[i].equals("NA"))
//				continue;
//			else
//				count++;
//		}
//	}
//	return count;
//}
//public int countServices()
//{
//	int services=0;
//	String str="";
//	try
//	{
//		rsLeftPart.beforeFirst();
//	
//		System.out.println("Inside the countServices");
//		while(rsLeftPart.next())
//		{
//				str=str+rsLeftPart.getString("service_name")+",";
//				System.out.println("STR:::>is::>"+str);
//		}
//			
//	
//		temp=str.split(",");
//		for(int i=0; i<temp.length; i++)
//		{
//			for(int j=i+1; j<temp.length; j++)
//			{
//				if(temp[i].equals(temp[j]))
//				{
//					temp[j]="NA";
//				}
//			}
//		}
//	
//		
//	rsLeftPart.first();
//	}catch(SQLException e)
//	{
//		e.printStackTrace();
//		
//	} 
//	catch(NullPointerException e)
//	{
//		e.printStackTrace();
//		
//	} 
//	
//	return services;
//}
//
//public List<String> countLigitimateDays(String service_name)
//{
//	List<String> WorkingDaysList=new ArrayList<String>();
//	int dayOfWeek,tempCount=0; 
//	Date dateNoApprovalRequired=new Date();
//	Date dateApprovalRequired=new Date();
//	UtilityFunctions uF=new UtilityFunctions();
//	CommonFunctions CF = new CommonFunctions(cF);
//	Map<String, String> hmHolidays = new CommonFunctions(cF).getHolidayList();
//
//	System.out.println(hmHolidays);
//	
//	
//	Calendar calendar = new GregorianCalendar();
//	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//	countPayableOnThisMonth=0;
//	countPayableOnThisMonthOff=0;
//	try
//	{
//		
//		
//		//taking directly count of worked days whose _in _out entries are present=========== 
//        stmt3 = con.prepareStatement(countWeekWorkingDaysInTheMonth,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
//		stmt3.setInt(1, Integer.parseInt(strEmpId));
//		stmt3.setDate(2,  uF.getDateFormat(startDate, cF.getStrReportDateFormat()));
//		stmt3.setDate(3, uF.getDateFormat(endDate, cF.getStrReportDateFormat()));
//		stmt3.setString(4,service_name);
//		rsDayWise=stmt3.executeQuery();	
//		rsDayWise.next();
//		
//		
//					
//		countPayableOnThisMonth=Integer.parseInt((rsDayWise.getString("weekDaysWorked")));
//		WorkingDaysList.add(Integer.toString(countPayableOnThisMonth));
//		//=================================================================================
//	
//		//to check whether absent days were week ends. 
//		stmt3 = con.prepareStatement(absentButPayableDays,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
//		stmt3.setInt(1, Integer.parseInt(strEmpId));
//		stmt3.setDate(2, uF.getDateFormat(startDate, cF.getStrReportDateFormat()));
//		stmt3.setDate(3, uF.getDateFormat(endDate, cF.getStrReportDateFormat()));
//		stmt3.setString(4,service_name);
//		System.out.println(stmt3);
//		rsDayWise=stmt3.executeQuery();	
//	
//	
//				
//			while(rsDayWise.next())// loop to check where absent days were sat sunday or a holiday=======
//			{
//				dateNoApprovalRequired = (Date)formatter.parse(rsDayWise.getString("generate_date"));  
//				calendar.setTime(dateNoApprovalRequired);
//				dayOfWeek= calendar.get(Calendar.DAY_OF_WEEK);
//								
//				if(dayOfWeek==1 || dayOfWeek==7) //1 is sunday and 7 is saturday
//				{
//					System.out.println("This is inside the if");
//					countPayableOnThisMonthOff=countPayableOnThisMonthOff+1;
//				}
//				else
//				{
//					//code to check whether it was a holiday======
//					if(hmHolidays.containsKey(uF.getDateFormat(rsDayWise.getString("generate_date"), DBDATE, cF.getStrReportDateFormat())))
//					{
//						countPayableOnThisMonthOff=countPayableOnThisMonthOff+1;
//					}
//					System.out.println("Inside the else holiday::>"+hmHolidays.containsKey(uF.getDateFormat(rsDayWise.getString("generate_date"), DBDATE, cF.getStrReportDateFormat())));
//					
//				}
//			}
//			WorkingDaysList.add(Integer.toString(countPayableOnThisMonthOff));
//			
//					
//			//=========================================================================================================
//	}
//	catch(Exception e)
//	{
//		e.printStackTrace();
//	}
//	
//	return WorkingDaysList;
//}
//
//public List<String> permanentDeductionValues()
//{
//	List<String> rightDataList=new ArrayList<String>();
//	
//	
//	
//	return rightDataList;
//	
//}
//
//
//public List<String> getRates(String service_name)
//{
//		
//		CommonFunctions CF=new CommonFunctions(cF);
//		UtilityFunctions uF=new UtilityFunctions();
//		 
//        List<String> rates=new ArrayList<String>();
//        try
//        {
//                stmt3 = con.prepareStatement(getRates,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
//                stmt3.setInt(1, Integer.parseInt(strEmpId));
//                stmt3.setDate(2, uF.getDateFormat(startDate, cF.getStrReportDateFormat()));
//        		stmt3.setDate(3, uF.getDateFormat(endDate, cF.getStrReportDateFormat()));
//                stmt3.setString(4,service_name);
//                System.out.println("get rates query::>"+stmt3);
//                rsDayWise=stmt3.executeQuery();        
//               
//                                
//                if(rsDayWise.next())
//                {
//                        String rate=rsDayWise.getString("rate");
//                        rates.add(uF.showData(rate,"0"));
//                }
//                else
//                {
//                        rates.add("0");
//                }
//                
//                stmt3 = con.prepareStatement(getRates,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
//                stmt3.setInt(1, Integer.parseInt(strEmpId));
//                stmt3.setDate(2, uF.getDateFormat(startDate, cF.getStrReportDateFormat()));
//        		stmt3.setDate(3, uF.getDateFormat(endDate, cF.getStrReportDateFormat()));
//                stmt3.setString(4,service_name);
//                rsDayWise=stmt3.executeQuery();        
//                
//                
//                if(rsDayWise.next())
//                {
//                        String rate=rsDayWise.getString("rate");
//                        System.out.println(service_name+" Worked on holiday with rate::>"+rate);
//                        rates.add(uF.showData(rate,"0"));
//                }
//                else
//                {
//                        rates.add("0");
//                }
//                
//        }
//        catch(SQLException e)
//        {
//                System.out.println("error here");
//                e.printStackTrace();
//        }
//        return rates;
//}
//
//public String leavesPay()
//{
//	Calendar cal;
//	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//	int tempCount=0;
//	int countLeaves=0;
//	UtilityFunctions uF=new UtilityFunctions();
//	CommonFunctions CF=new CommonFunctions(cF);
//	Map<String, String> hmHolidays = new CommonFunctions(cF).getHolidayList();
//	
//	try
//	{
//		stmtLeaves = con.prepareStatement(countApprovedLeaves,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
//		stmtLeaves.setInt(1, uF.parseToInt(strEmpId));
//		stmtLeaves.setDate(2,uF.getDateFormat(startDate, cF.getStrReportDateFormat()));
//		stmtLeaves.setDate(3,uF.getDateFormat(endDate, cF.getStrReportDateFormat()));
//		stmtLeaves.setDate(4,uF.getDateFormat(startDate, cF.getStrReportDateFormat()));
//		stmtLeaves.setDate(5,uF.getDateFormat(endDate, cF.getStrReportDateFormat()));
//		rsLeave=stmtLeaves.executeQuery();	
//		
//	//============count the approved leaves of the 'current month' excluding saturday sunday and holidays===========================
//		while(rsLeave.next())
//		{
//			Date toDate=rsLeave.getDate("approval_to_date");
//			Date fromDate=rsLeave.getDate("approval_from");
//	
//			tempCount=0;
//			
//			cal = Calendar.getInstance();
//			while(!(cal.getTime().equals(rsLeave.getDate("approval_to_date"))))
//			{
//				cal.setTime(formatter.parse(rsLeave.getString("approval_from")));
//				cal.add( Calendar.DATE, tempCount );
//				tempCount++;
//				
//				if(cal.get(Calendar.DAY_OF_WEEK)!=1 && cal.get(Calendar.DAY_OF_WEEK)!=7 && Integer.parseInt(monthNo)-1==cal.get(Calendar.MONTH))//if day is not sat sun or of another month
//				{
//					try {
//							if(hmHolidays.containsKey(uF.getDateFormat(rsDayWise.getString("generate_date"), DBDATE, cF.getStrReportDateFormat()))!=true)
//							{
//								countLeaves++;
//							}
//						}
//					catch(NullPointerException npe) {
//						System.out.println("Value is not present!");
//					}
//				}
//				
//			}
//		
//		}
//	}
//	catch(Exception e)
//	{
//		e.printStackTrace();
//	}
//	return Integer.toString(countLeaves);	
//}
//
//private List<String> getAddressRelatedInfo()
//{
//	List<String> addressrelated=new ArrayList<String>(); 
//	PreparedStatement pst = null;
//	ResultSet rs = null;
//	Database db = new Database();
//	Connection con = null;
//	con = db.makeConnection(con);	
//	UtilityFunctions uF=new UtilityFunctions();
//	
//	try
//	{
//    	   	pst = con.prepareStatement(getAddress);
//    	  	pst.setInt(1,uF.parseToInt(strEmpId));
//    		rs=pst.executeQuery();
//    		rs.next();
//    	 	
//   			addressrelated.add(rs.getString("wlocation_name"));
//   			addressrelated.add(uF.showData(rs.getString("wlocation_logo"),""));
//   			addressrelated.add(rs.getString("city_name")+","+rs.getString("state_name")+"\n"+rs.getString("country_name")+"\n"+"Pin: "+rs.getString("wlocation_pincode")+"\n"+"Ph: "+rs.getString("wlocation_contactno")+"  "+"Fax: "+rs.getString("wlocation_faxno"));
//   	}
//	catch(SQLException e)
//	{
//		e.printStackTrace();
//	}
//	finally
//	{
//		
//		db.closeResultSet(rs);
//		db.closeStatements(pst);
//		db.closeConnection(con);
//	}
//	return addressrelated;
//}
//protected void finalize ()
//{
//	try
//	{
//		rsLeftPart.close();
//		rsWorkingDaysUpper.close();
//		con.close();
//		stmt.close();
//		stmt2.close();
//		stmt3.close();
//	}
//	catch(SQLException e)
//	{
//		e.printStackTrace();
//	}
//}
//
//
//public String getEmpEmail(String epmID)
//{
//	String Email=null;
//	PreparedStatement pst = null;
//	ResultSet rs = null;
//	Database db = new Database();
//	Connection con = null;
//	con = db.makeConnection(con);	
//	UtilityFunctions uF=new UtilityFunctions();
//	
//	try
//	{
//    	   	pst = con.prepareStatement(getEmail);
//    	  	pst.setInt(1,uF.parseToInt(strEmpId));
//    	  	System.out.println("pst to get the email::>"+pst);
//    		rs=pst.executeQuery();
//    		rs.next();
//    	 	Email=rs.getString("emp_email");  			
//   	}
//	catch(SQLException e)
//	{
//		e.printStackTrace();
//	}
//	return Email;
//	
//}
//
//public void setStartDate(String startDate)
//{
//	this.startDate=startDate;
//}
//public void setEndDate(String endDate)
//{
//	this.endDate=endDate;
//}
//
//public String getStartDate()
//{
//	return startDate;
//}
//
//public String getEndDate()
//{
//	return endDate;
//}
//}