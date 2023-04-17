package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DeleteMedicalDocuments extends ActionSupport implements IStatements, ServletRequestAware {

	private String mediDocId;
	String type;
	
	public String execute() throws Exception {
		 
		if(getType()!=null && getType().equals("candidate")){
			deleteCandidateMedicalDocument();
		} else {
			deleteMedicalDocument();
		}
		return SUCCESS;
	
	}

	private void deleteCandidateMedicalDocument() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("update candidate_medical_details set description=null, filepath=null, yes_no=false WHERE medical_id=?");
			pst.setInt(1,uF.parseToInt(getMediDocId()));
//			System.out.println("pst======>"+pst);
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

	public void deleteMedicalDocument() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("update emp_medical_details set description=null, filepath=null, yes_no=false WHERE medical_id=?");
			pst.setInt(1,uF.parseToInt(getMediDocId()));
//			System.out.println("pst======>"+pst);
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
	
	public String getMediDocId() {
		return mediDocId;
	}

	public void setMediDocId(String mediDocId) {
		this.mediDocId = mediDocId;
	}
}
