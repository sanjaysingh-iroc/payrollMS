package com.konnect.jpms.training;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;

public class DownloadCertificate implements ServletRequestAware,ServletResponseAware, IStatements {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	
	HttpSession session;        
	CommonFunctions CF;
	String strSessionUserId;
	 
	public void execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if(CF==null)return "login";
		strSessionUserId = (String)session.getAttribute(EMPID);
		
		String strID = request.getParameter("ID");
		
		downloadDocument(strID);
		
		loadDownloadDocument();
//		return "load";
	}

	String doc_id;
	
	public String loadDownloadDocument() {

		return "load";
	}

	

	public String downloadDocument(String certificateID) {

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
			
			pst = con.prepareStatement("select certificate_name,certificate_desc from certificate_details where certificate_details_id=?");
			pst.setInt(1, uF.parseToInt(certificateID));
			rs = pst.executeQuery();
			String strDocumentName = null;
			String strDocumentContent = null;
			while (rs.next()) {
				strDocumentName = rs.getString("certificate_name");
				strDocumentContent = rs.getString("certificate_title");
			}
			rs.close();
			pst.close();
			
			if(strDocumentName!=null){
				strDocumentName = strDocumentName.replace(" ", "");
			}
			
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(0, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setEmailTemplate(false);
			Map<String, String> hmParsedContent = null;
			
			Document document = new Document();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			
			if(strDocumentContent!=null){
				
				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
							
				PdfWriter.getInstance(document, buffer);
				document.open();
				
				List<Element> supList = HTMLWorker.parseToList(new StringReader(hmParsedContent.get("MAIL_BODY")), null);
				Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase.add(supList.get(0));
				phrase.add(supList.get(1));
				
				for(int i=2; i<20; i++){
				
					if(supList.size()>i){
						phrase.add(supList.get(i));
					}else{
						break;
					}
				}
				
				document.add(phrase);
				document.close();
			}
			
			if(strDocumentName!=null){
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
