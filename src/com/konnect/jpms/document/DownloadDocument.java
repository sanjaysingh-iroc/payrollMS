package com.konnect.jpms.document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;

public class DownloadDocument implements ServletRequestAware,ServletResponseAware, IStatements {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strSessionUserId;
	String strOrgId;
	
	public void execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if(CF==null)return "login";
		strSessionUserId = (String)session.getAttribute(EMPID);
		strOrgId = (String)session.getAttribute(ORGID);
		
		String header = request.getParameter("header");
		downloadDocument(header);
		
		loadDownloadDocument();
//		return "load";
	}

	String doc_id;
	
	public String loadDownloadDocument() {
		return "load";
	}

	

	public String downloadDocument(String header) {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("insert into document_download_history (document_id, time_stamp, emp_id) values (?,?,?)");
			pst.setInt(1, uF.parseToInt(getDoc_id()));
			pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(3, uF.parseToInt(strSessionUserId));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("select * from document_activities where document_id =?");
			pst.setInt(1, uF.parseToInt(getDoc_id()));			
			rs = pst.executeQuery();
			String strDocumentName = null;
			String strDocumentContent = null;
			String strDocumentHeader = null;
			String strDocumentFooter = null;
			String strUserId = null;
			while (rs.next()) {
				strDocumentName = rs.getString("document_name");
				strDocumentContent = rs.getString("document_content");
				strDocumentHeader = rs.getString("document_header");
				strDocumentFooter = rs.getString("document_footer");
				strUserId = rs.getString("user_id");
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from document_signature where org_id =?");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			String strAuthSign = null;
			String strHrSign = null;
			String strRecruiterSign = null;
			while (rs.next()) {
				if(rs.getInt("signature_type") == 1) {
					strAuthSign = rs.getString("signature_image");
				}
				if(rs.getInt("signature_type") == 2) {
					strHrSign = rs.getString("signature_image");
				}
				if(rs.getInt("signature_type") == 3) {
					if(rs.getInt("user_id") == uF.parseToInt(strUserId)) {
						strRecruiterSign = rs.getString("signature_image");
					}
				}
			}
			rs.close();
			pst.close();
			
			
			if(strDocumentName!=null){
				strDocumentName = strDocumentName.replace(" ", "");
			}
			
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications NF = new Notifications(0, CF);
			NF.setDomain(strDomain);
			NF.request = request;
			NF.session = session;
			NF.setStrHostAddress(CF.getStrEmailLocalHost());
			NF.setStrHostPort(CF.getStrHostPort());
			NF.setStrContextPath(request.getContextPath());
			NF.setEmailTemplate(false);
			
			Map<String, String> hmParsedContent = null;
			
//			com.lowagie.text.Document document = new com.lowagie.text.Document();
//			Document document = new Document();
			Document document = new Document(PageSize.A4,40, 40, 10, 60);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			
			if(strDocumentContent!=null) {
				if(header!=null && header.equalsIgnoreCase("header")) {
					hmParsedContent  = NF.parseContent(strDocumentHeader+strDocumentContent+strDocumentFooter, "", "");	
				} else {
					hmParsedContent  = NF.parseContent(strDocumentContent, "", "");
				}
				
//				PdfWriter.getInstance(document, buffer);
//				document.open();
//				
				String strDocument = hmParsedContent.get("MAIL_BODY");
//				System.out.println(" Data ===>"+strDocument);
				if(strDocument!=null) {
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
//				
//				HTMLWorker hw = new HTMLWorker(document);
//				hw.parse(new StringReader(strDocument));
//				document.close();
				PdfWriter writer = PdfWriter.getInstance(document, buffer);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(strDocumentHeader,strDocumentFooter);
			    writer.setPageEvent(event);
				document.open();
//				System.out.println("strDocument ====> " +strDocument);
				HTMLWorker hw = new HTMLWorker(document);
//				hw.parse(new StringReader(sbHeader.toString())); 
					if(strDocument.contains(HR_SIGNATURE)) {
						String imageUrl=CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+strOrgId+"/"+I_DOC_SIGN+"/"+strHrSign;
						String 	strSignature ="<img src=\""+imageUrl+"\">";
						strDocument = strDocument.replace(HR_SIGNATURE, strSignature);
					}
				
					if(strDocument.contains(AUTHORITY_SIGNATURE)) {
					    String imageUrl=CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+strOrgId+"/"+I_DOC_SIGN+"/"+strAuthSign;
						String 	strSignature ="<img src=\""+imageUrl+"\">";
						strDocument = strDocument.replace(AUTHORITY_SIGNATURE, strSignature);
					}
					
//					System.out.println("strDocument ===>> " + strDocument);
					if(strDocument.contains(RECRUITER_SIGNATURE)) {
					    String imageUrl=CF.getStrDocSaveLocation()+I_ORGANISATION+"/"+strOrgId+"/"+I_DOC_SIGN+"/"+strUserId+"/"+strRecruiterSign;
						String 	strSignature ="<img src=\""+imageUrl+"\">";
//						System.out.println("strSignature ===>> " + strSignature);
						strDocument = strDocument.replace(RECRUITER_SIGNATURE, strSignature);
					}
					
					 hw.parse(new StringReader(strDocument));
			        //hw.parse(new StringReader(sbFooter.toString()));
				document.close();  
			}
			   
			if(strDocumentName!=null) {
				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=" + strDocumentName + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush(); 
				buffer.close();
				out.close();
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "update";
	}

	
	 public static PdfPCell createImageCell(String path) throws DocumentException, IOException {
	        Image img = Image.getInstance(path);
	        PdfPCell cell = new PdfPCell(img, true);
	        return cell;
	    }
	 
	public void validate() {

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getDoc_id() {
		return doc_id;
	}

	public void setDoc_id(String doc_id) {
		this.doc_id = doc_id;
	}

}
