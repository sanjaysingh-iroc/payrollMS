package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DeleteDocuments extends ActionSupport implements IStatements, ServletRequestAware{

	private String documentId;
	String type;
	

	public String execute() throws Exception {
		 
		if(getType()!=null && getType().equals("candidate")){
			deleteCandidateDocument();
		} else {
			deleteDocument();
		}
		return SUCCESS;
	
	}

	private void deleteCandidateDocument() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("DELETE FROM candidate_documents_details WHERE  documents_id=?");
			pst.setInt(1,uF.parseToInt(getDocumentId()));
			int x = pst.executeUpdate();
			pst.close();
			if(x > 0){
				request.setAttribute("STATUS_MSG", "<b><font color=\"blue\">This document is deleted</font></b>");
			} else {
				request.setAttribute("STATUS_MSG", "<b><font color=\"blue\">This document is not deleted. Plsese try again</font></b>");
			}
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "<b><font color=\"blue\">This document is not deleted. Plsese try again</font></b>");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void deleteDocument(){

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("DELETE FROM documents_details WHERE  documents_id=?");
			pst.setInt(1,uF.parseToInt(getDocumentId()));
			int x = pst.executeUpdate();
			pst.close();
			if(x > 0){
				request.setAttribute("STATUS_MSG", "<b><font color=\"blue\">This document is deleted</font></b>");
			} else {
				request.setAttribute("STATUS_MSG", "<b><font color=\"blue\">This document is not deleted. Plsese try again</font></b>");
			}
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "<b><font color=\"blue\">This document is not deleted. Plsese try again</font></b>");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
