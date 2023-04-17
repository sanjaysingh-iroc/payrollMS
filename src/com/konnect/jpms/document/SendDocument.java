package com.konnect.jpms.document;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.select.FillDocument;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SendDocument extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	HttpSession session;
	public CommonFunctions CF;
	String isSendMail;
	 
	public String execute() throws Exception {

		String operation = request.getParameter("operation");
		String strId = request.getParameter("param");

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		if (getStrSubject()!=null) {
			sendDocument(uF);
			return "success";
		}else{
			getOrg(uF);		
			setDoc_id(getDoc_id());
		}
		
		loadSendMail(uF);
		return "load";
	}

	String strOrgId;
	public void getOrg(UtilityFunctions uF){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select org_id from employee_official_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setStrOrgId(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public List<String> getDocIdFromNode(Connection con, UtilityFunctions uF, String strNode){
		
		List<String> alDoc = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rs =null;
		try {
			
			pst = con.prepareStatement("select doc_id from document_comm_details where trigger_nodes like '%,"+strNode+",%' and status = 1");
			rs = pst.executeQuery();
			while(rs.next()){
				alDoc.add(rs.getString("doc_id"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return alDoc;
	}
	
	public int getNodeIdFromActivity(Connection con, UtilityFunctions uF, String strActivity){
		
		int nNodeId = 0;
		PreparedStatement pst = null;
		ResultSet rs =null;
		try {
			
			pst = con.prepareStatement("select node_id from nodes where mapped_activity_id = ?");
			pst.setInt(1, uF.parseToInt(strActivity));
			rs = pst.executeQuery();
			while(rs.next()){
				nNodeId = uF.parseToInt(rs.getString("node_id"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nNodeId;
	}
	
	public String loadSendMail(UtilityFunctions uF) {

		documentList = new FillDocument(request).fillDocument(uF.parseToInt(getStrOrgId()));
		empList = new FillEmployee(request).fillEmployeeName();

		return "load";
	}

	String strEmpId;
	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	String strSubject;	
	String strMailBody;
	String doc_id;
	
	

	List<FillDocument> documentList;
	List<FillEmployee> empList;

	/*public String sendMail() {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();

		try {

			
			System.out.println("Subject:"+getStrSubject());
			System.out.println("Mail body:"+getStrMailBody());
			System.out.println("Document:"+getStrDocument());
			System.out.println("getEmp_id:"+getEmp_id());
			
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from document_comm_details where document_id =?");
			pst.setInt(1, uF.parseToInt(getStrDocument()));
			rs = pst.executeQuery();

			String strDocumentName = null;
			String strDocumentContent = null;
			while (rs.next()) {
				strDocumentName = rs.getString("document_name");
				strDocumentContent = rs.getString("document_text");
			}
			
			
			
			
			
			
			
			
			if(strDocumentName!=null){
				strDocumentName = strDocumentName.replace(" ", "");
			}
			
			
			Notifications nF = new Notifications(0, CF);
			Map<String, String> hmParsedContent = null;
			
			
			nF.setStrEmpId(getEmp_id());
			nF.setSupervisor(true);
			
			pst = con.prepareStatement(selectEmpDetails1);
			pst.setInt	(1, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
			
			System.out.println("pst===>"+pst);
			
			
			int nSupervisorId = 0;
			while(rs.next()){
				nSupervisorId = rs.getInt("supervisor_emp_id");
				nF.setStrEmpCode(rs.getString("empcode"));
				nF.setStrEmpEmail(rs.getString("emp_email"));
				nF.setStrEmailTo(rs.getString("emp_email"));
				nF.setStrEmpFname(rs.getString("emp_fname"));
				nF.setStrEmpLname(rs.getString("emp_lname"));
				nF.setStrEmpFullNamename(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
				nF.setStrEmpMobileNo(rs.getString("emp_contactno"));
				nF.setStrAccountNo(rs.getString("emp_bank_acct_nbr"));
				nF.setStrUserName(rs.getString("username"));
				nF.setStrPassword(rs.getString("password"));
			}
			if(nSupervisorId>0 && nF.isSupervisor()){
				pst = con.prepareStatement(selectEmpDetails1);
				pst.setInt	(1, nSupervisorId);
				rs = pst.executeQuery();
				
				while(rs.next()){
					nF.setStrSupervisorEmail(rs.getString("emp_email"));					
					nF.setStrSupervisorName(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
					nF.setStrSupervisorContactNo(rs.getString("emp_contactno"));
				}
			}
			
			
			
			Document document = new Document();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			
			
			String strDocName = null;
			String strDocContent = null;
			
			StringBuilder sb = new StringBuilder();
			
			
			if(strDocumentContent!=null){
				
				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				strDocName = strDocumentName;
				strDocContent = hmParsedContent.get("MAIL_BODY");
				nF.setStrDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
				
				
				PdfWriter.getInstance(document, buffer);
				document.open();
				
				
				sb.append("<table width=\"100%\"><tr>" +
						"<td align=\"left\"><img height=\"60\" src=\"http://"+CF.getStrEmailLocalHost()+":8080"+request.getContextPath()+"/userImages/"+CF.getStrOrgLogo()+"\"></td>" +
						"<td align=\"center\">"+CF.getStrOrgAddress()+"</td>" +
						"</tr></table>");
				
				List<Element> supList = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
				Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase.add(supList.get(0));
				document.add(phrase);
				
				
				supList = HTMLWorker.parseToList(new StringReader(hmParsedContent.get("MAIL_BODY")), null);
				phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase.add(supList.get(0));
				document.add(phrase);
				document.close();
			}
			
			hmParsedContent  = nF.parseContent(getStrMailBody(), "", getStrSubject());
			String strMailSubject = getStrSubject();
			String strMailBody = hmParsedContent.get("MAIL_BODY");
			byte[] bytes = buffer.toByteArray();
			nF.setStrEmailSubject(getStrSubject());
			nF.setStrEmailBody(hmParsedContent.get("MAIL_BODY"));
			
			if(strDocumentContent!=null){
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName(strDocumentName+".pdf");
			}
			saveDocumentActivity(con, uF, CF, strDocName,sb.toString()+ strDocContent,strMailSubject, strMailBody);
			nF.sendNotifications();
	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
			db.closeResultSet(rs);
			db.closeStatements(pst);
		}
		return "update";
	}*/
	
	
	
	
	
	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String sendDocument(UtilityFunctions uF) {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			con = db.makeConnection(con);
			
			
			Map<String, String> hmORG = new HashMap<String, String>();
			pst = con.prepareStatement("select org_id,org_name,org_address,org_city,org_pincode,org_logo  from org_details where org_id = (select org_id from employee_official_details where emp_id = ?)");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setStrOrgId(rs.getString("org_id"));
				hmORG.put("ORG_NAME", rs.getString("org_name"));
				hmORG.put("ORG_ADDRESS", rs.getString("org_address")+"<br/> <p/>"+ rs.getString("org_city")+" - "+rs.getString("org_pincode"));
				hmORG.put("ORG_LOGO", rs.getString("org_logo"));
			}
			rs.close();
			pst.close();
			
			Map<String, Map<String, String>> hmHeader=new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmFooter=new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from document_collateral");
			rs = pst.executeQuery();
			while(rs.next()){
				if(rs.getString("_type").equals("H")){
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rs.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rs.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rs.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rs.getString("collateral_text"),""));
					
					hmHeader.put(rs.getString("collateral_id"), hmInner);
				}else{
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rs.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rs.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rs.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rs.getString("collateral_text"),""));
					
					hmFooter.put(rs.getString("collateral_id"), hmInner);
				}
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from document_comm_details where doc_id =? and status = 1 order by document_id desc limit 1");
			pst.setInt(1, uF.parseToInt(getDoc_id()));
			rs = pst.executeQuery();
			String strDocumentName = null;
			String strDocumentContent = null;
			String strDocumentHeader = "";
			String strDocumentFooter = "";
			String strHeader = null;
			String strFooter = null;
			String strHeaderImageAlign="";
			String strHeaderCollateralText="";
			String strFooterImageAlign="";
			String strFooterCollateralText="";
			while (rs.next()) {  
				strDocumentName = rs.getString("document_name");
				strDocumentContent = rs.getString("document_text");
				
				if(rs.getString("collateral_header")!=null && !rs.getString("collateral_header").equals("") && hmHeader.get(rs.getString("collateral_header"))!=null){
					Map<String, String> hmInner=hmHeader.get(rs.getString("collateral_header"));
					strHeader = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
					strHeaderImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
					strHeaderCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
				}
				if(rs.getString("collateral_footer")!=null && !rs.getString("collateral_footer").equals("") && hmFooter.get(rs.getString("collateral_footer"))!=null){
//					strFooter = hmFooter.get(rs.getString("collateral_footer"));
					Map<String, String> hmInner=hmFooter.get(rs.getString("collateral_footer"));
					strFooter = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
					strFooterImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
					strFooterCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
				}
			}
			rs.close();
			pst.close();
			
			if(strDocumentName!=null){
				strDocumentName = strDocumentName.replace(" ", "");
			}
			
			Map<String, String> hmParsedContent = null;
			
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(0, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setStrEmpId(getStrEmpId());
			nF.setSupervisor(true);
			nF.setEmailTemplate(false);
			
			
			
			
//			Document document = new Document(PageSize.A4);
			Document document = new Document(PageSize.A4,40, 40, 10, 60); 
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			String strDocName = null;
			String strDocContent = null;
			StringBuilder sb = new StringBuilder();
			StringBuilder sbHeader = new StringBuilder();
			if(strDocumentContent!=null){
				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				
				strDocName = strDocumentName;
				strDocContent = hmParsedContent.get("MAIL_BODY");
				String strDocument = hmParsedContent.get("MAIL_BODY");
				if(strDocument!=null){
//					strDocument = strDocument.replaceAll("<br/>", "");
					
					//Satrt Dattatray Date : 31-07-21  
					if (strDocument.contains("<pre style=\"text-align:justify\">") || strDocument.contains("<pre style=\"text-align:justify;\">") || strDocument.contains("<pre style=\"text-align: justify;\">") || strDocument.contains("<pre style=\"text-align: justify\">")) {
//						System.out.println("if");
						if (strDocument.contains("<pre ")) {
							strDocument = strDocument.replaceAll("<pre ", "<p ");
						}
						 if(strDocument.contains("<pre>") ){
							 strDocument = strDocument.replaceAll("<pre>", "<p>");
						 }
						//
						/*if (strDocument.contains("><span")) {
							strDocument = strDocument.replaceAll("><span ", "><p style=\"text-align: justify\"><span ");
						}
						if (strDocument.contains("</span>")) {
							strDocument = strDocument.replaceAll("</span>", "</span></p>");
						}*/

						if (strDocument.contains("</pre>")) {
							strDocument = strDocument.replaceAll("</pre>", "</p>");
						}
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p>", "<br/><p style=\"text-align: justify\">", true, true, "<p>");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
//						strDocument = replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>", "<p style=\"text-align: justify\">", true, true, "<p style=\"text-align: justify\">");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</p>", true, true, "<p>");
//						strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
					}else {
						System.out.println("Else");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<p>", true, true, "<p>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", " </li>	", true, true, "</li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<pre>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<pre>", "<br/>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
					}
					//End Dattatray Date : 31-07-21 
				}
				nF.setStrDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
				
				String headerPath="";
				if(strHeader!=null && !strHeader.equals("")){
//					headerPath=CF.getStrDocRetriveLocation()+strHeader;
					if(CF.getStrDocRetriveLocation()==null) { 
						headerPath =  DOCUMENT_LOCATION + strHeader;
					} else { 
						headerPath = CF.getStrDocSaveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strHeader;
					}
				}

//				if(headerPath!=null){
//					strDocumentHeader="<table><tr>";
//					if(strHeaderImageAlign!=null && strHeaderImageAlign.equals("R")){ 
//						strDocumentHeader+="<td width=\"70%\" valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>" +
//								"<td align=\"right\"><img height=\"30\" src=\""+headerPath+"\"></td>";						
//					
//					}else if(strHeaderImageAlign!=null && strHeaderImageAlign.equals("C")){ 
//						strDocumentHeader+="<td align=\"Center\"><img height=\"30\" src=\""+headerPath+"\"><br/>"+strHeaderCollateralText+"</td>";
//					}else{ 
//						strDocumentHeader+="<td><img height=\"30\" src=\""+headerPath+"\"></td>" +
//								"<td valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>";
//					}
//					strDocumentHeader+="</tr></table>";
//					
//				}
//				
//				String footerPath="";   
//				if(strFooter!=null && !strFooter.equals("")) {
////					footerPath=CF.getStrDocRetriveLocation()+strFooter;
//					if(CF.getStrDocRetriveLocation()==null) { 
//						footerPath =  DOCUMENT_LOCATION + strFooter;
//					} else { 
//						footerPath = CF.getStrDocSaveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strFooter;
//					}
//				}
//							
//				if(footerPath!=null){
//					strDocumentFooter="<table><tr>";
//					if(strFooterImageAlign!=null && strFooterImageAlign.equals("R")){ 
//						strDocumentFooter+="<td width=\"70%\" valign=\"middle\" style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td>" +
//								"<td align=\"right\"><img height=\"30\" src=\""+footerPath+"\"></td>";						
//					
//					}else if(strFooterImageAlign!=null && strFooterImageAlign.equals("C")){ 
//						strDocumentFooter+="<td align=\"Center\"><img height=\"30\" src=\""+footerPath+"\"><br/>"+strFooterCollateralText+"</td>";
//					}else{ 
//						strDocumentFooter+="<td><img height=\"30\" src=\""+footerPath+"\"></td>" +
//								"<td valign=\"middle\" style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td>";
//					}
//					strDocumentFooter+="</tr></table>";
//					
//				}
				
				if(headerPath != null && !headerPath.equals("")) {
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					sbHeader.append("<td>");
					if(strHeader!=null && !strHeader.equals("")) {
						sbHeader.append("<img src=\""+headerPath+"\">");
					}
					sbHeader.append("</td>");	
					sbHeader.append("</tr></table>");
				}
				
				PdfWriter writer = PdfWriter.getInstance(document, buffer);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(sbHeader.toString(),strFooterCollateralText);
			    writer.setPageEvent(event);
				document.open();
				
				HTMLWorker hw = new HTMLWorker(document);
//				hw.parse(new StringReader(strDocumentHeader));
				hw.parse(new StringReader(strDocument));
//				hw.parse(new StringReader(strDocumentFooter));
				document.close();
				
				/*List<Element> supList = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
				Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase.add(supList.get(0));
				document.add(phrase);
				supList = HTMLWorker.parseToList(new StringReader(hmParsedContent.get("MAIL_BODY")), null);
				phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase.add(supList.get(0));
				document.add(phrase);
				document.close();*/ 
			}
			hmParsedContent  = nF.parseContent(getStrMailBody(), "", getStrSubject());
			String strMailSubject = getStrSubject();
			String strMailBody = hmParsedContent.get("MAIL_BODY");
			byte[] bytes = buffer.toByteArray();
			
			
			nF.setStrEmailSubject(getStrSubject());
			nF.setStrEmailBody(hmParsedContent.get("MAIL_BODY"));
			if(strDocumentContent!=null && uF.parseToBoolean(getIsSendMail())){
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName(strDocumentName+".pdf");
			}
//----------------------------------------------------------------------			
			
			/*if(strDocumentName!=null){
				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=" + strDocumentName + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush(); 
				buffer.close();
				out.close();
			}*/
			
			
			
			//saveDocumentActivity(con, uF, CF, strDocName,sb.toString()+ strDocContent,strMailSubject, strMailBody);
			saveDocumentActivity(con, uF, CF, strDocName, sbHeader.toString(), strDocContent, strFooterCollateralText, strMailSubject, strMailBody);
			
			if(uF.parseToBoolean(getIsSendMail())){
				nF.sendNotifications();
			}
			
			session.setAttribute(MESSAGE, SUCCESSM+"Document sent successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Document could not be sent. Please try again."+END);
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "update";
	}
	

	
	

	public void saveDocumentActivity(Connection con, UtilityFunctions uF,CommonFunctions CF, String strDocumentName, String strDocumentHeader, String strDocumentContent, String strDocumentFooter, String strMailSubject, String strMailBody){
		
		PreparedStatement pst = null;
		
		try {
		
			pst = con.prepareStatement("insert into document_activities (document_name, document_content, effective_date, entry_date, user_id, emp_id, mail_subject, mail_body, document_header, document_footer) values (?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1, strDocumentName);
			pst.setString(2, strDocumentContent);
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(6, uF.parseToInt(getStrEmpId()));
			pst.setString(7, strMailSubject);
			pst.setString(8, strMailBody);
			pst.setString(9, strDocumentHeader);
			pst.setString(10, strDocumentFooter);
			pst.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void validate() {

	}

	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getStrSubject() {
		return strSubject;
	}
	public void setStrSubject(String strSubject) {
		this.strSubject = strSubject;
	}
	public String getStrMailBody() {
		return strMailBody;
	}
	public void setStrMailBody(String strMailBody) {
		this.strMailBody = strMailBody;
	}
	public List<FillDocument> getDocumentList() {
		return documentList;
	}
	public void setDocumentList(List<FillDocument> documentList) {
		this.documentList = documentList;
	}
	public String getIsSendMail() {
		return isSendMail;
	}
	public void setIsSendMail(String isSendMail) {
		this.isSendMail = isSendMail;
	}

	public String getStrOrgId() {
		return strOrgId;
	}

	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}
	public String getDoc_id() {
		return doc_id;
	}

	public void setDoc_id(String doc_id) {
		this.doc_id = doc_id;
	}
}
