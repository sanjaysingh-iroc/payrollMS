package com.konnect.jpms.util;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;

public class UploadProjectDocuments extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	public CommonFunctions CF;
	HttpSession session;
	public String execute() throws Exception {
		request.setAttribute(PAGE, PMyProfile);
		 
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		String strReturn = uploadProjectDocuments();
 
		return strReturn; 
	}
	 
	public String uploadProjectDocuments() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			// File Uploading code goes here....

			int random = new Random().nextInt();
			String filePath = request.getRealPath("/userImages/");
			String fileName = "";
			con = db.makeConnection(con);
			
			/*if(getDocType()!=null && getDocType().equalsIgnoreCase("PROJECT_DOCUMENTS")) {
				 
				if(CF.getProjectDocumentFolder()==null) { 
					fileName = uF.uploadProjectDocuments(request, DOCUMENT_LOCATION, getDocumentFile(), getDocumentFileFileName(), getDocumentFileFileName(), CF);
				}else{
					fileName = uF.uploadProjectDocuments(request, CF.getProjectDocumentFolder(), getDocumentFile(), getDocumentFileFileName(), getDocumentFileFileName(), CF);
				}
				
				pst = con.prepareStatement(updateCompanyLogo);
				pst.setString(1, "logo.png");
				pst.setString(2, O_ORG_LOGO);			
				pst.execute();
				
				session.setAttribute("ORG_LOGO", fileName);
				
				return "config";
				
			} else*/ if(getDocType()!=null && getDocType().equalsIgnoreCase("PROJECT_FOLDER_DOCUMENTS")) {
				
				if(CF.getProjectDocumentFolder()==null) { 
					fileName = uF.uploadProjectDocuments(request, DOCUMENT_LOCATION, getDocumentFile(), getDocumentFileFileName(), getDocumentFileFileName(), CF);
				} else {
					fileName = uF.uploadProjectDocuments(request, getRealFolderPath(), getDocumentFile(), getDocumentFileFileName(), getDocumentFileFileName(), CF);
				}
//				System.out.println("fileName ======>> " + fileName);
				pst = con.prepareStatement("update project_document_details set document_name = ? where pro_document_id=?");
				pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "");
				pst.setInt(2, uF.parseToInt(getProDocumentId()));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("update communication_1 set doc_or_image = ? where communication_id=?");
				pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "");
				pst.setInt(2, uF.parseToInt(getFeedId()));
				pst.execute();
				pst.close();
				
				return "profile";
			} else if(getDocType()!=null && getDocType().equalsIgnoreCase("PROJECT_DOCUMENTS")) {
				
				if(CF.getProjectDocumentFolder()==null) { 
					fileName = uF.uploadProjectDocuments(request, DOCUMENT_LOCATION, getDocumentFile(), getDocumentFileFileName(), getDocumentFileFileName(), CF);
				} else {
					fileName = uF.uploadProjectDocuments(request, getRealFolderPath(), getDocumentFile(), getDocumentFileFileName(), getDocumentFileFileName(), CF);
				}
//				System.out.println("fileName ======>> " + fileName);
				pst = con.prepareStatement("update project_document_details set folder_name = ?, document_name = ? where pro_document_id=?");
				pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "");
				pst.setString(2, (fileName!=null && fileName.length()>0) ? fileName : "");
				pst.setInt(3, uF.parseToInt(getProDocumentId()));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("update communication_1 set doc_or_image = ? where communication_id=?");
				pst.setString(1, (fileName!=null && fileName.length()>0) ? fileName : "");
				pst.setInt(2, uF.parseToInt(getFeedId()));
				pst.execute();
				pst.close();
				
				return "profile";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS; 
	}

	private File documentFile;
	String documentFileFileName;
	String realFolderPath;
	String docType;
	String proId;
	String clientId;
	String orgId;
	String proDocumentId;
	String feedId;

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public File getDocumentFile() {
		return documentFile;
	}

	public void setDocumentFile(File documentFile) {
		this.documentFile = documentFile;
	}

	public String getDocumentFileFileName() {
		return documentFileFileName;
	}

	public void setDocumentFileFileName(String documentFileFileName) {
		this.documentFileFileName = documentFileFileName;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	public String getRealFolderPath() {
		return realFolderPath;
	}

	public void setRealFolderPath(String realFolderPath) {
		this.realFolderPath = realFolderPath;
	}

	public String getProDocumentId() {
		return proDocumentId;
	}

	public void setProDocumentId(String proDocumentId) {
		this.proDocumentId = proDocumentId;
	}

	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}

	public void setCF(CommonFunctions CF) {
		this.CF = CF;
	}
}
