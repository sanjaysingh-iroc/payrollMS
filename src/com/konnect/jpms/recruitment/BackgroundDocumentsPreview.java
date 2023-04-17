package com.konnect.jpms.recruitment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.document.HeaderFooterPageEvent;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class BackgroundDocumentsPreview implements ServletRequestAware,ServletResponseAware, IStatements  {
		
	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	private String candidateId;
	private String documentName;
	private String operation;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		downloadDocument();
		return null;
	}
	public void downloadDocument() {
	Connection con = null;
	ResultSet rst = null;
	PreparedStatement pst = null;
	Database db = new Database();
	db.setRequest(request);
	UtilityFunctions uF = new UtilityFunctions();

	try {
		con = db.makeConnection(con);
		String strDocumentFileName = null;
		String strDocumentContent = null;
		String strHeader = null;
	
		pst = con.prepareStatement("select * from candidate_documents_details where documents_name = ? and emp_id = ?");
		pst.setString(1,getDocumentName());
		pst.setInt(2,uF.parseToInt(getcandidateId()));
//		System.out.println("psttt----->"+pst);
		rst = pst.executeQuery();
		
		while (rst.next()) {  
			strDocumentFileName = rst.getString("documents_file_name");
		}
//		System.out.println("strDocumentFileName----->"+strDocumentFileName);
		rst.close();
		pst.close();
		
		
		
		Map<String, String> hmParsedContent = null;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4, 40, 40, 10, 60); 
		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbFooter = new StringBuilder();
		
		String headerPath="";
			headerPath=CF.getStrDocRetriveLocation()+strHeader;
			if(CF.getStrDocRetriveLocation()==null) { 
				headerPath =  DOCUMENT_LOCATION;
			} else { 
				headerPath = CF.getStrDocRetriveLocation();
			}
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
	//	HeaderFooterPageEvent event = new HeaderFooterPageEvent(sbHeader.toString(),strFooterCollateralText);
	 //   writer.setPageEvent(event);
		document.open();
//		System.out.println("strDocument ====> " +strDocument);
		HTMLWorker hw = new HTMLWorker(document);
//		hw.parse(new StringReader(sbHeader.toString())); 
		//hw.parse(new StringReader(strDocument));
//		hw.parse(new StringReader(sbFooter.toString()));
		document.close();
		byte[] bytes = buffer.toByteArray();		
		if(strDocumentFileName != null) {
				response.setContentType("application/pdf");
				response.setContentLength(buffer.size());
				response.setHeader("Content-Disposition", "attachment; filename=" + strDocumentFileName + ".pdf");
				ServletOutputStream out = response.getOutputStream();
				buffer.writeTo(out);
				out.flush(); 
				buffer.close();
				out.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
	public String getcandidateId() {
		return candidateId;
	}
	public void setcandidateId(String candidateId) {
		this.candidateId = candidateId;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
}