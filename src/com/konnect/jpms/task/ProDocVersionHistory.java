package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProDocVersionHistory extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	CommonFunctions CF; 
	HttpSession session1;
	String strSessionEmpId;
	String strOrgId;
	
	String proDocumentId;
	
	String type;
	String filePath; 
	String fileDir;

	public String execute() throws Exception {
		session1 = request.getSession();
		strSessionEmpId = (String)session1.getAttribute(EMPID);
		strOrgId = (String)session1.getAttribute(ORGID);
		CF = (CommonFunctions) session1.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		getDocumentVersionHistory(uF);
		return SUCCESS;
	}

	
		
	public void getDocumentVersionHistory(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);

			Map<String, String> hmFileIcon = CF.getFileIcon();
			request.setAttribute("hmFileIcon",hmFileIcon);
			
			pst = con.prepareStatement("select * from (select * from project_document_details where doc_parent_id in (select doc_parent_id " +
					"from project_document_details where pro_document_id=? and doc_parent_id >0)" +
					"union select * from project_document_details where pro_document_id in (select doc_parent_id from project_document_details where pro_document_id=? and doc_parent_id >0)" +
					"union select * from project_document_details where pro_document_id in (select pro_document_id from project_document_details where doc_parent_id=? and doc_parent_id >0)" +
					"union select * from project_document_details where pro_document_id =? and doc_parent_id =0) a order by pro_document_id desc");
			pst.setInt(1, uF.parseToInt(getProDocumentId()));
			pst.setInt(2, uF.parseToInt(getProDocumentId()));
			pst.setInt(3, uF.parseToInt(getProDocumentId()));
			pst.setInt(4, uF.parseToInt(getProDocumentId()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alVersion = new ArrayList<Map<String,String>>();
			while (rs.next()) {
				Map<String, String> hmVersionHistory = new HashMap<String, String>();
				hmVersionHistory.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmVersionHistory.put("CLIENT_ID", rs.getString("client_id"));
				hmVersionHistory.put("PRO_ID", rs.getString("pro_id"));
				hmVersionHistory.put("FOLDER_NAME", rs.getString("folder_name"));
				hmVersionHistory.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmVersionHistory.put("ADDED_BY", uF.showData(hmEmpCodeName.get(rs.getString("added_by")), "-") );
				hmVersionHistory.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat()));
				hmVersionHistory.put("ENTRY_TIME", uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()));
				hmVersionHistory.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmVersionHistory.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				hmVersionHistory.put("FILE_SIZE", rs.getString("file_size"));
				hmVersionHistory.put("FILE_TYPE", rs.getString("file_type"));
				hmVersionHistory.put("ALIGN_WITH", rs.getString("align_with"));
//				hmVersionHistory.put("SHARING_TYPE", rs.getString("sharing_type"));
				hmVersionHistory.put("SIZE_IN_BYTES", rs.getString("size_in_bytes"));
				hmVersionHistory.put("PROJECT_CATEGORY", rs.getString("project_category"));
				hmVersionHistory.put("DOC_PARENT_ID", rs.getString("doc_parent_id"));
				hmVersionHistory.put("SCOPE_DOCUMENT", rs.getString("scope_document"));
				hmVersionHistory.put("DESCRIPTION", rs.getString("description"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1){
					hmVersionHistory.put("CATEGORY", "Project");
					String strAlign = null;
					if(uF.parseToInt(rs.getString("align_with")) > 0) {
						strAlign = CF.getProjectTaskNameByTaskId(con, uF, rs.getString("align_with"));
					} else {
						strAlign = "Full Project";
					}
					hmVersionHistory.put("ALIGN", uF.showData(strAlign, "-"));
				} else if(uF.parseToInt(rs.getString("project_category")) == 2){
					hmVersionHistory.put("CATEGORY", "Other");
					String strOther = null;
					if(uF.parseToInt(rs.getString("align_with")) > 0) {
						strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					}
					hmVersionHistory.put("ALIGN", uF.showData(strOther, "-"));
				} else {
					hmVersionHistory.put("CATEGORY", "-");
					hmVersionHistory.put("ALIGN", "-");
				}
				
				Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, rs.getString("pro_id"));
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmVersionHistory.put("SHARING_RESOURCES", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmVersionHistory.put("SHARING_RESOURCES", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmVersionHistory.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmVersionHistory.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				
				hmVersionHistory.put("DOC_VERSION", rs.getString("doc_version"));
				
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmVersionHistory.put("FILE_EXTENSION", extenstion);
				
				alVersion.add(hmVersionHistory);
			}
			rs.close();
			pst.close();
			request.setAttribute("alVersion",alVersion);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}



	public String getProDocumentId() {
		return proDocumentId;
	}



	public void setProDocumentId(String proDocumentId) {
		this.proDocumentId = proDocumentId;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getFilePath() {
		return filePath;
	}



	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}



	public String getFileDir() {
		return fileDir;
	}



	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}

	
}
