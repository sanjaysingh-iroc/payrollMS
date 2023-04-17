package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillProjectDocument implements IStatements{

	String documentId;
	String documentName;
	
	private FillProjectDocument(String documentId, String documentName) {
		this.documentId = documentId;
		this.documentName = documentName;
	}
	
	HttpServletRequest request;
	public FillProjectDocument(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillProjectDocument() {
	}
	
	
	public List<FillProjectDocument> fillProjectDocument() {
		
		List<FillProjectDocument> al = new ArrayList<FillProjectDocument>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from project_document_details where file_size is not null");
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillProjectDocument(rs.getString("pro_document_id"), rs.getString("document_name")));				
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
		return al;
	}
	
	
	public List<FillProjectDocument> fillProjectWiseDocument(String strProIds) {
		
		List<FillProjectDocument> al = new ArrayList<FillProjectDocument>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if(strProIds != null && strProIds.length()>0) {
				pst = con.prepareStatement("select * from project_document_details where file_size is not null and pro_id in ("+strProIds+")");
				rs = pst.executeQuery();
				while(rs.next()) {
					al.add(new FillProjectDocument(rs.getString("pro_document_id"), rs.getString("document_name")));				
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}


	
	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	
}  
