package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectDocumentView extends ActionSupport implements
		ServletRequestAware, ServletResponseAware, SessionAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	int pro_id;
	String strUsreType;
	String strSessionEmpId;
	String type;
	String strOrgId;
	
	CommonFunctions CF;
	private HttpServletRequest request;

	public String execute() {
			
			session = request.getSession();
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if (CF == null)
				return LOGIN;
			strOrgId = (String)session.getAttribute(ORGID);
			strUsreType = (String)session.getAttribute(BASEUSERTYPE);
			strSessionEmpId = (String)session.getAttribute(EMPID);
			
			request.setAttribute(PAGE, "/jsp/task/ProjectDocumentView.jsp");
			request.setAttribute(TITLE, "Project Documents");
			if (session.getAttribute("pro_id") != null) {
				pro_id = ((Integer) session.getAttribute("pro_id")).intValue();
			}
			getProjectDocumentDetails();

		session.getAttribute("pro_id");
		return SUCCESS;
	}

	
	public void getProjectDocumentDetails() {
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			String proDocMainPath = CF.getProjectDocumentFolder();
			request.setAttribute("proDocMainPath", proDocMainPath);
			
			String proDocRetrivePath = CF.getRetriveProjectDocumentFolder();
			request.setAttribute("proDocRetrivePath", proDocRetrivePath);
			request.setAttribute("strOrgId", strOrgId);
			
			Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, getPro_id()+"");
			
			Map<String, List<List<String>>> hmFolderDocs = new HashMap<String, List<List<String>>>();
			List<List<String>> listDocs = new ArrayList<List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from project_document_details where pro_id=? and pro_folder_id > 0 ");
			if(getType() != null && getType().equals("MyProject")) {
				sbQuery.append(" and (sharing_type =0 or sharing_type = 1 or sharing_resources like '%,"+strSessionEmpId+",%') ");
			}
			sbQuery.append(" order by pro_document_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, getPro_id());
			rs = pst.executeQuery();
			while (rs.next()) {
				listDocs = hmFolderDocs.get(rs.getString("pro_folder_id"));
				if(listDocs == null) listDocs = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("pro_document_id"));
				innerList.add(rs.getString("document_name"));
				innerList.add(CF.getProjectNameById(con, rs.getString("pro_id")));
				innerList.add(rs.getString("pro_id"));
				innerList.add(CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(rs.getString("folder_file_type"));
				innerList.add(rs.getString("client_id"));
				innerList.add(uF.showData(rs.getString("file_size"), "-"));
				innerList.add(uF.showData(rs.getString("file_type"), "-"));
				String strAlign = null;
				if(uF.parseToInt(rs.getString("align_with")) > 0) {
					strAlign = CF.getProjectTaskNameByTaskId(con, uF, rs.getString("align_with"));
				} else {
					strAlign = "Full Project";
				}
				innerList.add(uF.showData(strAlign, "-"));
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					innerList.add(uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					innerList.add(uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				
				listDocs.add(innerList);
				hmFolderDocs.put(rs.getString("pro_folder_id"), listDocs);
			}
			rs.close();
			pst.close();
//			System.out.println("hmFolderDocs ====>>>>> " + hmFolderDocs);
			request.setAttribute("hmFolderDocs", hmFolderDocs);
			
			
			Map<String, List<String>> hmFolderDocsData = new HashMap<String, List<String>>();
//			List<List<String>> listFoldersDocs = new ArrayList<List<String>>();
			StringBuilder sbQuery1 = new StringBuilder();
			sbQuery1.append("select * from project_document_details where pro_id=? and pro_folder_id = 0 ");
			if(getType() != null && getType().equals("MyProject")) {
				sbQuery1.append(" and (sharing_type =0 or sharing_type = 1 or sharing_resources like '%,"+strSessionEmpId+",%') ");
			}
			sbQuery1.append(" order by pro_document_id desc ");
			pst = con.prepareStatement(sbQuery1.toString());
			pst.setInt(1, getPro_id());
			rs = pst.executeQuery();
			while (rs.next()) {
//				listFoldersDocs = hmFolderDocsData.get(rs.getString("folder_name"));
//				List<List<String>> listFoldersDocs = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("pro_document_id"));
				innerList.add(rs.getString("folder_name"));
				innerList.add(rs.getString("document_name"));
				innerList.add(rs.getString("pro_id"));
				innerList.add(CF.getProjectNameById(con, rs.getString("pro_id")));
				innerList.add(CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(rs.getString("folder_file_type"));
				innerList.add(rs.getString("client_id"));
				innerList.add(uF.showData(rs.getString("file_size"), "-"));
				innerList.add(uF.showData(rs.getString("file_type"), "-"));
				String strAlign = null;
				if(uF.parseToInt(rs.getString("align_with")) > 0) {
					strAlign = CF.getProjectTaskNameByTaskId(con, uF, rs.getString("align_with"));
				} else {
					strAlign = "Full Project";
				}
				innerList.add(uF.showData(strAlign, "-"));
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					innerList.add(uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					innerList.add(uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
//				listFoldersDocs.add(innerList);
				hmFolderDocsData.put(rs.getString("pro_document_id"), innerList);
			}
			rs.close();
			pst.close();
//			System.out.println("hmFolderDocsData ===>>> " + hmFolderDocsData);
			request.setAttribute("hmFolderDocsData", hmFolderDocsData);
			
			
			
//			pst = con.prepareStatement("select * from project_documents_details where pro_id=?");
//			pst.setInt(1, pro_id);
//			rs = pst.executeQuery();
//			
//			List alProDocuments = new ArrayList();
//			
//			while (rs.next()) {
//				alProDocuments.add(rs.getString("doc_id"));
//				alProDocuments.add("<a  style=\"float: left;margin-right:10px\" target=\"_blank\" href=\""+rs.getString("doc_path")+"\">"+rs.getString("doc_name")+"</a>");
//			}
//
//			
//			request.setAttribute("alProDocuments", alProDocuments);
//			
//
//			if(strUsreType!=null && strUsreType.equalsIgnoreCase(EMPLOYEE)){
//				pst = con.prepareStatement("select * from project_documents_details pdd, activity_info ai where pdd.task_id = ai.task_id and ai.task_id in (select task_id from activity_info where pro_id=?)  and emp_id = ?");
//				pst.setInt(1, pro_id);
//				pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
//				rs = pst.executeQuery();
//				
//			}else{
//				pst = con.prepareStatement("select * from project_documents_details pdd, activity_info ai where pdd.task_id = ai.task_id and ai.task_id in (select task_id from activity_info where pro_id=?)");
//				pst.setInt(1, pro_id);
//				rs = pst.executeQuery();
//				
//			}
//			
//			List alTaskDocuments = new ArrayList();
//			
//			while (rs.next()) {
//				alTaskDocuments.add(rs.getString("doc_id"));
//				alTaskDocuments.add(rs.getString("activity_name")+": "+"<a target=\"_blank\" href=\""+rs.getString("doc_path")+"\">"+rs.getString("doc_name")+"</a>");
//			}
//			
//			request.setAttribute("alTaskDocuments", alTaskDocuments);
//			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public int getPro_id() {
		return pro_id;
	}

	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setSession(Map arg0) {
		// TODO Auto-generated method stub
	}
}
