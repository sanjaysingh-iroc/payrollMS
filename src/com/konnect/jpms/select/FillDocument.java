package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
public class FillDocument implements IStatements{

	String documentId;
	String documentName;

	
	private FillDocument(String documentId, String documentName) {
		this.documentId = documentId;
		this.documentName = documentName;
	}
	HttpServletRequest request;
	public FillDocument(HttpServletRequest request) {
		this.request = request;
	}
	public FillDocument() {
	}
	
	public List<FillDocument> fillDocument(){
		
		List<FillDocument> al = new ArrayList<FillDocument>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from document_comm_details where status = 1");
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillDocument(rs.getString("document_id"), rs.getString("document_name")));				
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
	
	
public List<FillDocument> fillDocumentList(String activityId, String orgId) {
		
		List<FillDocument> al = new ArrayList<FillDocument>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from nodes where mapped_activity_id=?");
			pst.setInt(1, uF.parseToInt(activityId));
			rs = pst.executeQuery();
			String nodeId = "0";
			while(rs.next()){
				nodeId = rs.getString("node_id");				
			}
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from document_comm_details where status=1 and trigger_nodes like ',"+nodeId+",' ");
			if(uF.parseToInt(orgId)>0) {
				sbQuery.append(" and org_id="+uF.parseToInt(orgId));
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			al.add(new FillDocument("0", "No Document"));
			while(rs.next()) {
				al.add(new FillDocument(rs.getString("document_id"), rs.getString("document_name")));				
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


	public List<FillDocument> fillDocument(int orgId){
		
		List<FillDocument> al = new ArrayList<FillDocument>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from document_comm_details where status =1 and org_id =?");
			pst.setInt(1, orgId);
			rs = pst.executeQuery();
			while(rs.next()){
//				al.add(new FillDocument(rs.getString("document_id"), rs.getString("document_name")));
				al.add(new FillDocument(rs.getString("doc_id"), rs.getString("document_name"))); 
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
