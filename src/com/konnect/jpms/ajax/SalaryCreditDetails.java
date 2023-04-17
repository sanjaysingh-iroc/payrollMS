//package com.konnect.jpms.ajax;
//
//import static java.lang.System.out;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.util.ArrayList;
//import java.util.List;
//import javax.servlet.http.HttpServletRequest;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import org.apache.struts2.interceptor.ServletRequestAware;
//import com.konnect.jpms.salary.GenerateSalarySlip;
//import com.konnect.jpms.util.Database;
//import com.konnect.jpms.util.IStatements;
//import com.konnect.jpms.util.UtilityFunctions;
//import com.opensymphony.xwork2.ActionSupport;
//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//
//import org.w3c.dom.DOMException;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Text;
//
//
//public class SalaryCreditDetails extends ActionSupport implements ServletRequestAware, IStatements{
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 8042578909347592246L;
//	String headName;
//	String headByte;
//	String headType;
//	String headValue;
//	List<List<String>> al = new ArrayList<List<String>>();
//	List<String> alInner = new ArrayList<String>();
//	
//	public String execute()	{
//		
////		System.out.println("inside execute of SalaryCreditDetails..");
//		setHeadName(request.getParameter("headName"));
//		setHeadByte(request.getParameter("headByte"));
//		setHeadType(request.getParameter("headType"));
//		setHeadValue(request.getParameter("headValue"));
////		System.out.println("headname===>>"+request.getParameter("headName"));
//		if(request.getParameter("salarySlip") != null) {
//			loadLists();
//			return generateSalarySlip();
//		}
//		
//		if(request.getParameter("headName")!=null) {
//			return insertSalaryDetails();
//		}
//		
//		if(request.getParameter("save")!=null) {
//			loadLists();
//			return updateSalaryDetails();
//		}
//		
//		return SUCCESS;
//	}
//	
//	public void loadLists() {
//		
//		String sh_id[] 		= request.getParameterValues("salary_head_id");
//		String sh_name[]	= request.getParameterValues("salary_head_name");
//		String sh_byte[]	= request.getParameterValues("salary_head_byte");	
//		String sha_type[]	= request.getParameterValues("salary_head_amount_type");
//		String sh_value[]	= request.getParameterValues("salary_head_value");	//Amount in P or A
//		String gross_value	= request.getParameter("txt_gross");
//		String deduction_value	= request.getParameter("txt_deduction");
////		System.out.println("sh_id.length="+sh_id.length);
////		System.out.println("sh_name.length="+sh_name.length);
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		for(int i=0; i<(sh_id.length); i++)
//		{
//			alInner = new ArrayList<String>();
//			alInner.add(sh_id[i]);		// 0 - ID
//			alInner.add(sh_name[i]);	// 1 - Name
//			
//			alInner.add(sha_type[i]);	// 2 - C or D Byte
//			
//			if(sh_value[i].equals("") || sh_value[i].length() == 0)	//3 - Value in P or A
//				alInner.add("0");
//			else
//				alInner.add(sh_value[i]);
//			
//			if(sh_byte[i].equals("E")) {		//4 - Actual amount in currency
//				if(sha_type[i].equals("A"))	{		
//					alInner.add(sh_value[i]);
//				}else {
//					alInner.add( Integer.toString(( (uF.parseToInt(gross_value)) * (uF.parseToInt(sh_value[i])) / 100 ) ));
//				}
//			
//			}else {
//				if(sha_type[i].equals("A"))	{		//4 - Actual amount in currency
//					alInner.add(sh_value[i]);
//				}else {
//					alInner.add( Integer.toString(( (uF.parseToInt(deduction_value)) * (uF.parseToInt(sh_value[i])) / 100 ) ));
//				}
//			}
//			
//			al.add(alInner);
////			/System.out.println("List added for id="+sh_id[i]);
//		}
////		System.out.println("List size="+al.size());
//	}
//	
//	public String generateSalarySlip() {
//			
//		GenerateSalarySlip gS = new GenerateSalarySlip();
//		gS.setServletRequest(request);
//		gS.execute();
//		return SUCCESS;
//		
//	}
//	
//	public void replace()
//	{
//		 try
//         {
//	         String fileData = "";
//	         BufferedReader reader = new BufferedReader(
//	                 new FileReader("/home/konnect/Desktop/Salary_Slip.jrxml"));
//	         char[] buf = new char[1024];
//	         int numRead=0;
//	         while((numRead=reader.read(buf)) != -1){
//	             String readData = String.valueOf(buf, 0, numRead);
//	             fileData = fileData + (readData);
//	             buf = new char[1024];
//	         }
//	         reader.close();
//	         String ltString = fileData.replace("&lt;", "<");
//	         String gtString = ltString.replace("&gt;", ">");
//	         BufferedWriter out = new BufferedWriter(new FileWriter("/home/konnect/Desktop/Salary_Slip.jrxml"));
//	         out.write(gtString);
//	         out.close();
//         
//         }catch(Exception e)
//         {
//             e.printStackTrace();
//         }
//	}
//	
//	public String updateSalaryDetails() {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//			con = db.makeConnection(con);
//			
//			for(int i=0; i<al.size(); i++) {
//				
//				pst = con.prepareStatement(updateSalaryDetails);
//				pst.setString(1, al.get(i).get(2));		//salary_head_amount_type
//				pst.setString(2, al.get(i).get(3));		//salary_head_value
//				pst.setInt(3, uF.parseToInt(al.get(i).get(0)));		//salary_head_id
//				int cnt = pst.executeUpdate();
//	            pst.close();
////				System.out.println("pst="+pst+" cnt="+cnt);
//			}
//				
//		} catch (Exception e) {
//			e.printStackTrace();
//			request.setAttribute(MESSAGE, "Error in insertion");
//			return ERROR;
//			
//		}finally{
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//			
//		return UPDATE;
//		
//	}
//	
//	
//	public String insertSalaryDetails() {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement(insertSalaryDetails);
//			pst.setString(1, getHeadName());
//			pst.setString(2, getHeadByte());
//			pst.setString(3, getHeadType());
//			pst.setString(4, getHeadValue());
////			System.out.println("pst for insert==>>"+pst);
//			pst.execute();
//            pst.close();
//			request.setAttribute(MESSAGE, getHeadName() + " added successfully!");
////			System.out.println(getHeadName() + " added successfully!");
//			setHeadName(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//			request.setAttribute(MESSAGE, "Error in insertion");
//			return ERROR;
//			
//		}finally{
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		
//		return UPDATE;
//		
//	}
//	
//	
//		/*************************************	XML Utility Methods ***********************************/ 
//	
//	public Document createDocument() {
//		Document document=null;
//        try {
//			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//			document = documentBuilder.newDocument();
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
//		}
//		return document;
//    }
//	
//	public void printToFile(Document document,String strFilePath,String strFileName) {
//        File file=new File(strFilePath);
//        if(!file.isDirectory()){
//        	out.println("Following Path is not exist:\n"+strFilePath);
//            return;
//        }
//        
//        try {
//            OutputFormat format = new OutputFormat(document);
//            format.setIndenting(true);
//            XMLSerializer serializer = new XMLSerializer(new FileOutputStream(strFilePath+strFileName), format);
//            serializer.serialize(document);
//        } catch(IOException ie) {
//            ie.printStackTrace();
//        }
//    }
//	
//	public Element addRootNode(Document document,String strRootNodeName) {
//		Element rootNode=null;
//		try {
//			rootNode = document.createElement(strRootNodeName);
//			document.appendChild(rootNode);
//		} catch (DOMException e) {
//			e.printStackTrace();
//		}
//        return rootNode;
//    }
//    
//	public Element addNode(Document document, Element parentElement, String nodeName) {
//    	Element childElement=null;
//        try {
//			childElement = document.createElement(nodeName);
//			parentElement.appendChild(childElement);
//		} catch (DOMException e) {
//			e.printStackTrace();
//		}
//		
//		return childElement;
//    }
//    
//	public static void addText(Document document, Element parentElement, String strText) {
//    	if(strText==null)
//    		return;
//    	
//        try {
//			Text text = document.createTextNode(strText);
//			parentElement.appendChild(text);
//			
//        } catch (DOMException e) {
//			e.printStackTrace();
//		}
//    }
//	
//	
//	HttpServletRequest request;
//	@Override
//	public void setServletRequest(HttpServletRequest arg0) {
//		this.request = arg0;
//	}
//	public void setHeadName(String headName)
//	{
//		this.headName = headName;
//	}
//	public String getHeadName()
//	{
//		return headName;
//	}
//	public void setHeadByte(String headByte)
//	{
//		this.headByte = headByte;
//	}
//	public String getHeadByte()
//	{
//		return headByte;
//	}
//	public void setHeadType(String HeadType)
//	{
//		this.headType = HeadType;
//	}
//	public String getHeadType()
//	{
//		return headType;
//	}
//	public void setHeadValue(String HeadValue)
//	{
//		this.headValue = HeadValue;
//	}
//	public String getHeadValue()
//	{
//		return headValue;
//	}
//	
//}
