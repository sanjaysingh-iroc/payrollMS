package com.konnect.jpms.task;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UploadProjectDocuments;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateProjectDocumentFolder extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	String msg; 
	CommonFunctions CF; 
	HttpSession session;
	String strSessionEmpId;
	String strOrgId;
	String strUserType;
	
	String proId;
	String taskId;
	String clientId;
	String folderName; 
	String strOrg;
	String prjectname;
	String operation;
	String proFolderId;
	
	String []folderDocsTRId;
	String []strFolderName;
	
	String []folderDocSharingType;
	String []proFolderDocTasks;
	
	
	String []docCountId;
	File[] strFolderDoc;
	String[] strFolderDocFileName;
	
	String proFolderTask;
	String proFolderCategory;
	String[] proFolderEmployee;
	String proFolderSharingType;

	String isFolderEdit;
	String isFolderDelete;
	
	String type;
	
	String proCategoryTypeFolder;
	String strFolderDescription;
	
	String[] strFolderDocDescription;
	String[] strFolderScopeDoc;
	String[] proCategoryTypeFolderDoc;

	String fromPage;
	String[] proFolderPoc;
	
	String pageType;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		strSessionEmpId = (String)session.getAttribute(EMPID);
		strOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String) session.getAttribute(BASEUSERTYPE);
		request.setAttribute("strUserType", strUserType);

		UtilityFunctions uF = new UtilityFunctions();
		
		if(getPageType() == null || getPageType().equals("") || getPageType().equals("null")) {
			setPageType(null);
		}
		
		System.out.println("getOperation() ===>> " + getOperation());
		getFolderAndDocuments(uF);
		
		if(getOperation() != null && getOperation().equals("U")) {
//			setOperation(getOperation());
			updateFolderSetting(uF);
			if(getFromPage() != null && getFromPage().equals("VAP")) {
				createVAPFolderForDocs(uF);
				if(getPageType() != null && getPageType().equals("MP")) {
					return MYSUCCESS;
				} else {
					return "vapsuccess";
				}
			} else if(getFromPage() != null && getFromPage().equals("MP")) {
				createMPFolderForDocs(uF);
				return "mpsuccess";
			} else {
				createFolderForDocs(uF);
				if(getPageType() != null && getPageType().equals("MP")) {
					return MYSUCCESS;
				}
			}
			return SUCCESS;
		}
		
		return LOAD;
	}

	private void createMPFolderForDocs(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
//			System.out.println("getOperation() ===>> " + getOperation());
			
//			String orgName = CF.getStrOrgName();
			String mainPathWithOrg = CF.getProjectDocumentFolder()+"/"+strOrgId;
			
			String mainPath = mainPathWithOrg+"/Projects";
			
			setPrjectname(CF.getProjectNameById(con, getProId()));
			
			String proNameFolder = mainPath +"/"+getProId();
			String proFolderNameFolder = "";
			if(getType()!=null && getType().equals("SF")) {
				pst = con.prepareStatement("select * from project_document_details where pro_document_id=(select pro_folder_id from project_document_details where pro_document_id=?)");
				pst.setInt(1, uF.parseToInt(getProFolderId()));
				rs = pst.executeQuery();
				String strMainFolder = null;
				while (rs.next()) {
					strMainFolder = rs.getString("folder_name");
				}
				rs.close();
				pst.close();
				if(strMainFolder!=null){
					proFolderNameFolder = proNameFolder+"/"+ strMainFolder +"/"+ getFolderName();
				}
			} else {
				proFolderNameFolder = proNameFolder +"/"+ getFolderName();
			}
			
			if(getType()!=null && getType().equals("F")) {
				
				String[] folderDocsTRId = request.getParameterValues("folderDocsTRId"+getProId()+"_"+getTaskId()+"_1");
				File[] strFolderDoc = mpRequest.getFiles("strFolderDoc"+getProId()+"_"+getTaskId()+"_1");    //  
				String[] strFolderDocFileName = mpRequest.getFileNames("strFolderDoc"+getProId()+"_"+getTaskId()+"_1"); 
				
//				String[] proFolderDocTasks = request.getParameterValues("proFolderDocTasks1");
				String[] folderDocDharingType = request.getParameterValues("folderDocDharingType"+getProId()+"_"+getTaskId()+"_1");
				String[] proCategoryTypeFolderDoc = request.getParameterValues("proCategoryTypeFolderDoc"+getProId()+"_"+getTaskId()+"_1");
				String[] strFolderDocDescription = request.getParameterValues("strFolderDocDescription"+getProId()+"_"+getTaskId()+"_1");
				String[] strFolderScopeDoc = request.getParameterValues("strFolderScopeDoc"+getProId()+"_"+getTaskId()+"_1");
				
				String[] isFolderDocEdit = request.getParameterValues("isFolderDocEdit" + getProId()+"_"+getTaskId()+"_1");
				String[] isFolderDocDelete = request.getParameterValues("isFolderDocDelete" + getProId()+"_"+getTaskId()+"_1");
				
				for(int j=0; folderDocsTRId != null && j<folderDocsTRId.length; j++) {
				
					String proFolderDocTasks = request.getParameter("proFolderDocTasks"+getProId()+"_"+getTaskId()+"_1_"+folderDocsTRId[j]);
					String proFolderDocCategory = request.getParameter("proFolderDocCategory"+getProId()+"_"+getTaskId()+"_1_"+folderDocsTRId[j]);
//					System.out.println("folderDocsTRId[j] ===>> " + folderDocsTRId[j]);
//					System.out.println("getFolderTRId()[i] ===>> " + getFolderTRId()[i]);
					double lengthBytes =  strFolderDoc[j].length();
					boolean isFileExist = false;
					
					String extenstion=FilenameUtils.getExtension(strFolderDocFileName[j]);	
					String strFileName = FilenameUtils.getBaseName(strFolderDocFileName[j]);
					strFileName = strFileName+"v1."+extenstion;
					File f = new File(proFolderNameFolder+"/"+strFileName);
					if(f.isFile()) {
					    System.out.println("isFile");
					    if(f.exists()) {
							isFileExist = true;
						    System.out.println("exists");
						} else {
						    System.out.println("exists fail");
						}   
					} else {
					    System.out.println("isFile fail");
					}
					if(lengthBytes > 0 && !isFileExist) {
						String[] proFolderDocEmployee = request.getParameterValues("proFolderDocEmployee"+getProId()+"_"+getTaskId()+"_1_"+folderDocsTRId[j]);
						List<String> alFDEmployee = null;
						if(proFolderDocEmployee != null) {
							alFDEmployee = Arrays.asList(proFolderDocEmployee);
						}
						StringBuilder sbFDEmps = null;
						
						for(int a=0; alFDEmployee != null && a<alFDEmployee.size(); a++) {
							if(alFDEmployee.get(a) != null && !alFDEmployee.get(a).trim().equals("")) {
								if(sbFDEmps == null) {
									sbFDEmps = new StringBuilder();
									sbFDEmps.append(","+ alFDEmployee.get(a).trim() +",");
								} else {
									sbFDEmps.append(alFDEmployee.get(a).trim() +",");
								}
							}
						}
						if(sbFDEmps == null) {
							sbFDEmps = new StringBuilder();
						}
						
						String[] proFolderDocPoc = request.getParameterValues("proFolderDocPoc"+getProId()+"_"+getTaskId()+"_1_"+folderDocsTRId[j]);
						List<String> alFDPoc = null;
						if(proFolderDocPoc != null) {
							alFDPoc = Arrays.asList(proFolderDocPoc);
						}
						StringBuilder sbFDPoc = null;
						
						for(int a=0; alFDPoc != null && a<alFDPoc.size(); a++) {
							if(alFDPoc.get(a) != null && !alFDPoc.get(a).trim().equals("")) {
								if(sbFDPoc == null) {
									sbFDPoc = new StringBuilder();
									sbFDPoc.append(","+ alFDPoc.get(a).trim() +",");
								} else {
									sbFDPoc.append(alFDPoc.get(a).trim() +",");
								}
							}
						}
						if(sbFDPoc == null) {
							sbFDPoc = new StringBuilder();
						}
						
						
						pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
						"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
						pst.setString(1, strFolderDocDescription[j]);
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(2, 1);
						} else {
							pst.setInt(2, 0);
						}
//						pst.setInt(2, uF.parseToInt(getStrAlignWith()));
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(3, uF.parseToInt(getProId()));
						} else {
							pst.setInt(3, uF.parseToInt(proFolderDocCategory));
						}
//						pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
						pst.setString(4, sbFDEmps.toString());
						pst.setString(5, "");
						pst.setString(6, "");
						pst.setInt(7, uF.parseToInt(folderDocSharingType[j]));
						pst.setString(8, sbFDEmps.toString());
						pst.setInt(9, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(11, strFileName);
						pst.executeUpdate();
						pst.close();
						
						String feedId = null;
						pst = con.prepareStatement("select max(communication_id) as communication_id from communication_1");
						rs = pst.executeQuery();
						while(rs.next()) {
							feedId = rs.getString("communication_id");
						}
						rs.close();
						pst.close();
						
						pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, " +
								"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document," +
								"description,doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc,feed_id)" +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getClientId()));
						pst.setInt(2, uF.parseToInt(getProId()));
						pst.setString(3, getFolderName());
						pst.setInt(4, uF.parseToInt(strSessionEmpId));
//						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(6, "folder");
						pst.setInt(7, uF.parseToInt(getProFolderId()));
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(8, uF.parseToInt(proFolderDocTasks));
						} else {
							pst.setInt(8, uF.parseToInt(proFolderDocCategory));
						}
						pst.setInt(9, uF.parseToInt(folderDocDharingType[j]));
						pst.setString(10, sbFDEmps.toString());
						pst.setInt(11, uF.parseToInt(proCategoryTypeFolderDoc[j]));
						pst.setString(12, strFolderScopeDoc[j]);
						pst.setString(13, strFolderDocDescription[j]);
						pst.setInt(14, 0);
						pst.setInt(15, 1);
						pst.setBoolean(16, uF.parseToBoolean(isFolderDocEdit[j]));
						pst.setBoolean(17, uF.parseToBoolean(isFolderDocDelete[j]));
						if(strUserType != null && strUserType.equals(CUSTOMER)) {
							pst.setBoolean(18, true);
						} else {
							pst.setBoolean(18, false);
						}
						pst.setString(19, sbFDPoc.toString());
						pst.setInt(20, uF.parseToInt(feedId));
	//					System.out.println("pst====>"+pst);
						pst.execute();
						pst.close();
						
						
						String proDocumentId = "";
						pst = con.prepareStatement("select max(pro_document_id) as pro_document_id from project_document_details");
						rs = pst.executeQuery();
						while (rs.next()) {
							proDocumentId = rs.getString("pro_document_id");
						}
						rs.close();
						pst.close();
						
						
						uploadProjectFolderDocuments(con, getProId(), strFolderDoc[j], strFileName, proFolderNameFolder, proDocumentId); 
						
						/**
						 * Alerts
						 * */
						
						List<String> alEmp= null;
						if(proFolderDocEmployee != null) {
							alEmp = Arrays.asList(proFolderDocEmployee);
						}
						if(alEmp == null){
							alEmp = new ArrayList<String>();
						}
						
						List<String> alSharePoc = null;
						if(proFolderDocPoc != null) {
							alSharePoc = Arrays.asList(proFolderDocPoc);
						}
						if(alSharePoc == null){
							alSharePoc = new ArrayList<String>();
						}
						
						String strDocumentName = "";
						pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
						pst.setInt(1, uF.parseToInt(proDocumentId));
						rs = pst.executeQuery();
						while (rs.next()) {
							strDocumentName = rs.getString("folder_name");
						}
						rs.close();
						pst.close();
						
						String proName1 = null;
						String strCategory = null;
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
							pst.setInt(1, uF.parseToInt(proFolderDocTasks));
							rs = pst.executeQuery();
							
							while(rs.next()) {
								proName1 = rs.getString("pro_name");
							}
							rs.close();
							pst.close();
						} else {
							pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
							pst.setInt(1, uF.parseToInt(proFolderDocCategory));
							rs = pst.executeQuery();
							while(rs.next()) {
								strCategory = rs.getString("project_category");
							}
							rs.close();
							pst.close();
						}
						Notifications nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
						nF.setDomain(strDomain);
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						
						nF.request = request;
						nF.setStrOrgId((String)session.getAttribute(ORGID));
						nF.setEmailTemplate(true);
						
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							nF.setStrProjectName(proName1);
						} else {
							nF.setStrCategoryName(strCategory);
						}
						nF.setStrDocumentName(strDocumentName);
						
						String alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						String alertAction = "DocumentListView.action";
						for(String strEmp : alEmp){
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							nF.setStrEmpId(strEmp.trim());
							nF.sendNotifications();
						}
						for(String strEmp : alSharePoc){
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrOther("other");
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							pst = con.prepareStatement("select * from client_poc where poc_id = ?");
							pst.setInt(1, uF.parseToInt(strEmp.trim()));
							rs = pst.executeQuery();
							boolean flg=false;
							while(rs.next()) {
								nF.setStrCustFName(rs.getString("contact_fname"));
								nF.setStrCustLName(rs.getString("contact_lname"));
								nF.setStrEmpMobileNo(rs.getString("contact_number"));
								if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
									nF.setStrEmpEmail(rs.getString("contact_email"));
									nF.setStrEmailTo(rs.getString("contact_email"));
								}
								flg = true;
							}
							rs.close();
							pst.close();
							
							if(flg) {
								nF.setStrEmpId(strEmp.trim());
								nF.sendNotifications();
							}
						}
						/**
						 * Alerts End
						 * */
					}
				}
				
				String[] SubFolderTR = request.getParameterValues("SubFolderTR"+getProId()+"_"+getTaskId()+"_1");
				String[] strSubFolderName = request.getParameterValues("strSubFolderName"+getProId()+"_"+getTaskId()+"_1");
				String[] proCategoryTypeSubFolder = request.getParameterValues("proCategoryTypeSubFolder"+getProId()+"_"+getTaskId()+"_1");
//				String[] proSubFolderTasks = request.getParameterValues("proSubFolderTasks1");
				String[] SubfolderSharingType = request.getParameterValues("SubfolderSharingType"+getProId()+"_"+getTaskId()+"_1");
				String[] strSubFolderDescription = request.getParameterValues("strSubFolderDescription"+getProId()+"_"+getTaskId()+"_1");
				
				String[] isSubFolderEdit = request.getParameterValues("isSubFolderEdit" + getProId()+"_"+getTaskId()+"_1");
				String[] isSubFolderDelete = request.getParameterValues("isSubFolderDelete" + getProId()+"_"+getTaskId()+"_1");
				
				for(int j = 0; SubFolderTR != null && j < SubFolderTR.length; j++) {
					
					String proSubFolderTasks = request.getParameter("proSubFolderTasks"+getProId()+"_"+getTaskId()+"_1_"+SubFolderTR[j]);
					String proSubFolderCategory = request.getParameter("proSubFolderCategory"+getProId()+"_"+getTaskId()+"_1_"+SubFolderTR[j]);

					String[] proSubFolderEmployee = request.getParameterValues("proSubFolderEmployee"+getProId()+"_"+getTaskId()+"_1_"+SubFolderTR[j]);
					List<String> alSubEmployee = null;
					if(proSubFolderEmployee != null) {
						alSubEmployee = Arrays.asList(proSubFolderEmployee);
					}
					StringBuilder sbSubEmps = null;
					for(int a=0; alSubEmployee != null && a<alSubEmployee.size(); a++) {
						if(alSubEmployee.get(a) != null && !alSubEmployee.get(a).trim().equals("")) {
							if(sbSubEmps == null) {
								sbSubEmps = new StringBuilder();
								sbSubEmps.append(","+ alSubEmployee.get(a).trim() +",");
							} else {
								sbSubEmps.append(alSubEmployee.get(a).trim() +",");
							}
						}
					}
					if(sbSubEmps == null) {
						sbSubEmps = new StringBuilder();
					}
					
					String[] proSubFolderPoc = request.getParameterValues("proSubFolderPoc"+getProId()+"_"+getTaskId()+"_1_"+SubFolderTR[j]);
					List<String> alSubPoc = null;
					if(proSubFolderPoc != null) {
						alSubPoc = Arrays.asList(proSubFolderPoc);
					}
					StringBuilder sbSubPoc = null;
					for(int a=0; alSubPoc != null && a<alSubPoc.size(); a++) {
						if(alSubPoc.get(a) != null && !alSubPoc.get(a).trim().equals("")) {
							if(sbSubPoc == null) {
								sbSubPoc = new StringBuilder();
								sbSubPoc.append(","+ alSubPoc.get(a).trim() +",");
							} else {
								sbSubPoc.append(alSubPoc.get(a).trim() +",");
							}
						}
					}
					if(sbSubPoc == null) {
						sbSubPoc = new StringBuilder();
					}
					
					String proSubFolderNameFolder = proFolderNameFolder +"/"+ strSubFolderName[j];
					
					File file3 = new File(proSubFolderNameFolder);
					if (!file3.exists()) {
						if (file3.mkdir()) {
							System.out.println("Directory is created!");
						}
					}
					
					pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, " +
							"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document," +
							"description,doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc)" +
							"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setInt(1, uF.parseToInt(getClientId()));
					pst.setInt(2, uF.parseToInt(getProId()));
					pst.setString(3, strSubFolderName[j]);
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
//					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setString(6, "folder");
					pst.setInt(7, uF.parseToInt(getProFolderId()));
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						pst.setInt(8, uF.parseToInt(proSubFolderTasks));
					} else {
						pst.setInt(8, uF.parseToInt(proSubFolderCategory));
					}
					pst.setInt(9, uF.parseToInt(SubfolderSharingType[j]));
					pst.setString(10, sbSubEmps.toString());
					pst.setInt(11, uF.parseToInt(proCategoryTypeSubFolder[j]));
					pst.setString(12, null);
					pst.setString(13, strSubFolderDescription[j]);
					pst.setInt(14, 0);
					pst.setInt(15, 0);
					pst.setBoolean(16, uF.parseToBoolean(isSubFolderEdit[j]));
					pst.setBoolean(17, uF.parseToBoolean(isSubFolderDelete[j]));
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						pst.setBoolean(18, true);
					} else {
						pst.setBoolean(18, false);
					}
					pst.setString(19, sbSubPoc.toString());
			//		System.out.println("pst====>"+pst);
					pst.execute();
					pst.close();
			
					String proSubFolderId = "";
					pst = con.prepareStatement("select max(pro_document_id)as pro_document_id from project_document_details");
					rs = pst.executeQuery();
					while (rs.next()) {
						proSubFolderId = rs.getString("pro_document_id");
					}
					rs.close();
					pst.close();
					
					
					/**
					 * Alerts
					 * */
					
					List<String> alEmp= null;
					if(proSubFolderEmployee != null) {
						alEmp = Arrays.asList(proSubFolderEmployee);
					}
					if(alEmp == null){
						alEmp = new ArrayList<String>();
					}
					
					List<String> alSharePoc = null;
					if(proSubFolderPoc != null) {
						alSharePoc = Arrays.asList(proSubFolderPoc);
					}
					if(alSharePoc == null){
						alSharePoc = new ArrayList<String>();
					}
					
					String strDocumentName = "";
					pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
					pst.setInt(1, uF.parseToInt(proSubFolderId));
					rs = pst.executeQuery();
					while (rs.next()) {
						strDocumentName = rs.getString("folder_name");
					}
					rs.close();
					pst.close();
					
					String proName1 = null;
					String strCategory = null;
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
						pst.setInt(1, uF.parseToInt(proSubFolderTasks));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							proName1 = rs.getString("pro_name");
						}
						rs.close();
						pst.close();
					} else {
						pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
						pst.setInt(1, uF.parseToInt(proSubFolderCategory));
						rs = pst.executeQuery();
						while(rs.next()) {
							strCategory = rs.getString("project_category");
						}
						rs.close();
						pst.close();
					}
					Notifications nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
					nF.setDomain(strDomain);
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					
					nF.request = request;
					nF.setStrOrgId((String)session.getAttribute(ORGID));
					nF.setEmailTemplate(true);
					
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						nF.setStrProjectName(proName1);
					} else {
						nF.setStrCategoryName(strCategory);
					}
					nF.setStrDocumentName(strDocumentName);
					
					String alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					String alertAction = "DocumentListView.action";
					for(String strEmp : alEmp){
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						//Mail
						nF.setStrEmpId(strEmp.trim());
						nF.sendNotifications();
					}
					for(String strEmp : alSharePoc){
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrOther("other");
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						//Mail
						pst = con.prepareStatement("select * from client_poc where poc_id = ?");
						pst.setInt(1, uF.parseToInt(strEmp.trim()));
						rs = pst.executeQuery();
						boolean flg=false;
						while(rs.next()) {
							nF.setStrCustFName(rs.getString("contact_fname"));
							nF.setStrCustLName(rs.getString("contact_lname"));
							nF.setStrEmpMobileNo(rs.getString("contact_number"));
							if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
								nF.setStrEmpEmail(rs.getString("contact_email"));
								nF.setStrEmailTo(rs.getString("contact_email"));
							}
							flg = true;
						}
						rs.close();
						pst.close();
						
						if(flg) {
							nF.setStrEmpId(strEmp.trim());
							nF.sendNotifications();
						}
					}
					/**
					 * Alerts End
					 * */
					
					
//					System.out.println("SubFolderTR[j] ====>> " + SubFolderTR[j]);
					String[] SubfolderDocsTRId = request.getParameterValues("SubfolderDocsTRId"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);
//					System.out.println("SubfolderDocsTRId ====>> " + SubfolderDocsTRId.length);
					File[] files = mpRequest.getFiles("strSubFolderDoc"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);    //  
					String[] fileNames = mpRequest.getFileNames("strSubFolderDoc"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]); 
					String[] proCategoryTypeSubFolderDoc = request.getParameterValues("proCategoryTypeSubFolderDoc"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);
//					String[] proSubFolderDocTasks = request.getParameterValues("proSubFolderDocTasks" +SubFolderTR[j]);
					String[] SubfolderDocDharingType = request.getParameterValues("SubfolderDocDharingType"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);
					String[] strSubFolderDocDescription = request.getParameterValues("strSubFolderDocDescription"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);
					String[] strSubFolderScopeDoc = request.getParameterValues("strSubFolderScopeDoc"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);
					
					String[] isSubFolderDocEdit = request.getParameterValues("isSubFolderDocEdit"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);
					String[] isSubFolderDocDelete = request.getParameterValues("isSubFolderDocDelete"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);
					
					for(int k=0; SubfolderDocsTRId != null && k < SubfolderDocsTRId.length; k++) {
						
						String proSubFolderDocTasks = request.getParameter("proSubFolderDocTasks"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
						String proSubFolderDocCategory = request.getParameter("proSubFolderDocCategory"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
						
						double lengthBytes =  files[k].length();
						boolean isFileExist = false;
//						System.out.println("fileNames[k] ===>> " + fileNames[k] + " ----- " + proSubFolderNameFolder+"/"+fileNames[k]);
						String extenstion=FilenameUtils.getExtension(fileNames[k]);	
						String strFileName = FilenameUtils.getBaseName(fileNames[k]);
						strFileName = strFileName+"v1."+extenstion;
						File f = new File(proSubFolderNameFolder+"/"+strFileName);
						if(f.isFile()) {
						    System.out.println("isFile");
						    if(f.exists()) {
								isFileExist = true;
							    System.out.println("exists");
							} else {
							    System.out.println("exists fail");
							}   
						} else {
						    System.out.println("isFile fail");
						}
						
						if(lengthBytes > 0 && !isFileExist) {
							String[] proFolderDocEmployee = request.getParameterValues("proSubFolderDocEmployee"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
							List<String> alFDEmployee = null;
							if(proFolderDocEmployee != null) {
								alFDEmployee = Arrays.asList(proFolderDocEmployee);
							}
							StringBuilder sbFDEmps = null;
							
							for(int a=0; alFDEmployee != null && a<alFDEmployee.size(); a++) {
								if(alFDEmployee.get(a) != null && !alFDEmployee.get(a).trim().equals("")) {
									if(sbFDEmps == null) {
										sbFDEmps = new StringBuilder();
										sbFDEmps.append(","+ alFDEmployee.get(a).trim() +",");
									} else {
										sbFDEmps.append(alFDEmployee.get(a).trim() +",");
									}
								}
							}
							if(sbFDEmps == null) {
								sbFDEmps = new StringBuilder();
							}
							
							String[] proFolderDocPoc = request.getParameterValues("proSubFolderDocPoc"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
							List<String> alFDPoc = null;
							if(proFolderDocPoc != null) {
								alFDPoc = Arrays.asList(proFolderDocPoc);
							}
							StringBuilder sbFDPoc = null;
							
							for(int a=0; alFDPoc != null && a<alFDPoc.size(); a++) {
								if(alFDPoc.get(a) != null && !alFDPoc.get(a).trim().equals("")) {
									if(sbFDPoc == null) {
										sbFDPoc = new StringBuilder();
										sbFDPoc.append(","+ alFDPoc.get(a).trim() +",");
									} else {
										sbFDPoc.append(alFDPoc.get(a).trim() +",");
									}
								}
							}
							if(sbFDPoc == null) {
								sbFDPoc = new StringBuilder();
							}
							
							
							pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
							"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
							pst.setString(1, strSubFolderDocDescription[k]);
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(2, 1);
							} else {
								pst.setInt(2, 0);
							}
//							pst.setInt(2, uF.parseToInt(getStrAlignWith()));
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(3, uF.parseToInt(getProId()));
							} else {
								pst.setInt(3, uF.parseToInt(proSubFolderDocCategory));
							}
//							pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
							pst.setString(4, sbFDEmps.toString());
							pst.setString(5, "");
							pst.setString(6, "");
							pst.setInt(7, uF.parseToInt(SubfolderDocDharingType[k]));
							pst.setString(8, sbFDEmps.toString());
							pst.setInt(9, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
							pst.setString(11, strFileName);
							pst.executeUpdate();
							pst.close();
							
							String feedId = null;
							pst = con.prepareStatement("select max(communication_id) as communication_id from communication_1");
							rs = pst.executeQuery();
							while(rs.next()) {
								feedId = rs.getString("communication_id");
							}
							rs.close();
							pst.close();
							
							
							pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, " +
									"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document," +
									"description,doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc,feed_id)" +
									"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
							pst.setInt(1, uF.parseToInt(getClientId()));
							pst.setInt(2, uF.parseToInt(getProId()));
							pst.setString(3, strFileName);
							pst.setInt(4, uF.parseToInt(strSessionEmpId));
//							pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
							pst.setString(6, "folder");
							pst.setInt(7, uF.parseToInt(proSubFolderId));
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(8, uF.parseToInt(proSubFolderDocTasks));
							} else {
								pst.setInt(8, uF.parseToInt(proSubFolderDocCategory));
							}
							pst.setInt(9, uF.parseToInt(SubfolderDocDharingType[k]));
							pst.setString(10, sbFDEmps.toString());
							pst.setInt(11, uF.parseToInt(proCategoryTypeSubFolderDoc[k]));
							pst.setString(12, strSubFolderScopeDoc[k]);
							pst.setString(13, strSubFolderDocDescription[k]);
							pst.setInt(14, 0);
							pst.setInt(15, 1);
							pst.setBoolean(16, uF.parseToBoolean(isSubFolderDocEdit[k]));
							pst.setBoolean(17, uF.parseToBoolean(isSubFolderDocDelete[k]));
							if(strUserType != null && strUserType.equals(CUSTOMER)) {
								pst.setBoolean(18, true);
							} else {
								pst.setBoolean(18, false);
							}
							pst.setString(19, sbFDPoc.toString());
							pst.setInt(20, uF.parseToInt(feedId));
		//					System.out.println("pst====>"+pst);
							pst.execute();
							pst.close();
							
							String proDocumentId = "";
							pst = con.prepareStatement("select max(pro_document_id) as pro_document_id from project_document_details");
							rs = pst.executeQuery();
							while (rs.next()) {
								proDocumentId = rs.getString("pro_document_id");
							}
							rs.close();
							pst.close();
							
							
							uploadProjectFolderDocuments(con, getProId(), files[k], strFileName, proSubFolderNameFolder, proDocumentId);
							
							/**
							 * Alerts
							 * */
							
							List<String> alEmp1= null;
							if(proFolderDocEmployee != null) {
								alEmp1 = Arrays.asList(proFolderDocEmployee);
							}
							if(alEmp1 == null){
								alEmp1 = new ArrayList<String>();
							}
							
							List<String> alSharePoc1 = null;
							if(proFolderDocPoc != null) {
								alSharePoc1 = Arrays.asList(proFolderDocPoc);
							}
							if(alSharePoc1 == null){
								alSharePoc1 = new ArrayList<String>();
							}
							
							strDocumentName = "";
							pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
							pst.setInt(1, uF.parseToInt(proDocumentId));
							rs = pst.executeQuery();
							while (rs.next()) {
								strDocumentName = rs.getString("folder_name");
							}
							rs.close();
							pst.close();
							
							proName1 = null;
							strCategory = null;
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
								pst.setInt(1, uF.parseToInt(proSubFolderDocTasks));
								rs = pst.executeQuery();
								
								while(rs.next()) {
									proName1 = rs.getString("pro_name");
								}
								rs.close();
								pst.close();
							} else {
								pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
								pst.setInt(1, uF.parseToInt(proSubFolderDocCategory));
								rs = pst.executeQuery();
								while(rs.next()) {
									strCategory = rs.getString("project_category");
								}
								rs.close();
								pst.close();
							}
							nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
							nF.setDomain(strDomain);
							nF.setStrHostAddress(CF.getStrEmailLocalHost());
							nF.setStrHostPort(CF.getStrHostPort());
							nF.setStrContextPath(request.getContextPath());
							
							nF.request = request;
							nF.setStrOrgId((String)session.getAttribute(ORGID));
							nF.setEmailTemplate(true);
							
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								nF.setStrProjectName(proName1);
							} else {
								nF.setStrCategoryName(strCategory);
							}
							nF.setStrDocumentName(strDocumentName);
							
							alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
							alertAction = "DocumentListView.action";
							for(String strEmp : alEmp1){
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strEmp.trim());
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
//								userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
								userAlerts.setStatus(INSERT_TR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
								
								//Mail
								nF.setStrEmpId(strEmp.trim());
								nF.sendNotifications();
							}
							for(String strEmp : alSharePoc1){
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strEmp.trim());
								userAlerts.setStrOther("other");
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
//								userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
								userAlerts.setStatus(INSERT_TR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
								
								//Mail
								pst = con.prepareStatement("select * from client_poc where poc_id = ?");
								pst.setInt(1, uF.parseToInt(strEmp.trim()));
								rs = pst.executeQuery();
								boolean flg=false;
								while(rs.next()) {
									nF.setStrCustFName(rs.getString("contact_fname"));
									nF.setStrCustLName(rs.getString("contact_lname"));
									nF.setStrEmpMobileNo(rs.getString("contact_number"));
									if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
										nF.setStrEmpEmail(rs.getString("contact_email"));
										nF.setStrEmailTo(rs.getString("contact_email"));
									}
									flg = true;
								}
								rs.close();
								pst.close();
								
								if(flg) {
									nF.setStrEmpId(strEmp.trim());
									nF.sendNotifications();
								}
							}
							/**
							 * Alerts End
							 * */
							
						}
					}
				}
				
				int subFolderCnt = SubFolderTR!=null ? SubFolderTR.length : 0;
				String taskName = CF.getProjectTaskNameByTaskId(con, uF, getTaskId());
				StringBuilder sbMsg = new StringBuilder();
				
				sbMsg.append(SUCCESSM+""+getFolderName()+" folder updated ");
				if(subFolderCnt>0) {
					sbMsg.append("and some documents added ");
				}
				sbMsg.append("successfully for "+taskName+"."+END);
				session.setAttribute(MESSAGE, sbMsg.toString());
				
			} else if(getType()!=null && getType().equals("SF")) {
				
				String[] SubfolderDocsTRId = request.getParameterValues("SubfolderDocsTRId"+getProId()+"_"+getTaskId()+"_1");
//				System.out.println("SubfolderDocsTRId ====>> " + SubfolderDocsTRId.length);
				File[] files = mpRequest.getFiles("strSubFolderDoc"+getProId()+"_"+getTaskId()+"_1");    //  
				String[] fileNames = mpRequest.getFileNames("strSubFolderDoc"+getProId()+"_"+getTaskId()+"_1"); 
				String[] proCategoryTypeSubFolderDoc = request.getParameterValues("proCategoryTypeSubFolderDoc"+getProId()+"_"+getTaskId()+"_1");
//				String[] proSubFolderDocTasks = request.getParameterValues("proSubFolderDocTasks1");
				String[] SubfolderDocDharingType = request.getParameterValues("SubfolderDocDharingType"+getProId()+"_"+getTaskId()+"_1");
				String[] strSubFolderDocDescription = request.getParameterValues("strSubFolderDocDescription"+getProId()+"_"+getTaskId()+"_1");
				String[] strSubFolderScopeDoc = request.getParameterValues("strSubFolderScopeDoc"+getProId()+"_"+getTaskId()+"_1");
				
				String[] isSubFolderDocEdit = request.getParameterValues("isSubFolderDocEdit"+getProId()+"_"+getTaskId()+"_1");
				String[] isSubFolderDocDelete = request.getParameterValues("isSubFolderDocDelete"+getProId()+"_"+getTaskId()+"_1");
				
				for(int k=0; SubfolderDocsTRId != null && k < SubfolderDocsTRId.length; k++) {
					
					String proSubFolderDocTasks = request.getParameter("proSubFolderDocTasks"+getProId()+"_"+getTaskId()+"_1_"+SubfolderDocsTRId[k]);
					String proSubFolderDocCategory = request.getParameter("proSubFolderDocCategory"+getProId()+"_"+getTaskId()+"_1_"+SubfolderDocsTRId[k]);
					
					double lengthBytes =  files[k].length();
					boolean isFileExist = false;
//					System.out.println("fileNames[k] ===>> " + fileNames[k] + " ----- " + proSubFolderNameFolder+"/"+fileNames[k]);
					
					String extenstion=FilenameUtils.getExtension(fileNames[k]);	
					String strFileName = FilenameUtils.getBaseName(fileNames[k]);
					strFileName = strFileName+"v1."+extenstion;
					
					File f = new File(proFolderNameFolder+"/"+strFileName);
					if(f.isFile()) {
					    System.out.println("isFile");
					    if(f.exists()) {
							isFileExist = true;
						    System.out.println("exists");
						} else {
						    System.out.println("exists fail");
						}   
					} else {
					    System.out.println("isFile fail");
					}
					
					if(lengthBytes > 0 && !isFileExist) {
						String[] proFolderDocEmployee = request.getParameterValues("proSubFolderDocEmployee"+getProId()+"_"+getTaskId()+"_1_"+SubfolderDocsTRId[k]);
						List<String> alFDEmployee = null;
						if(proFolderDocEmployee != null) {
							alFDEmployee = Arrays.asList(proFolderDocEmployee);
						}
						StringBuilder sbFDEmps = null;
						
						for(int a=0; alFDEmployee != null && a<alFDEmployee.size(); a++) {
							if(alFDEmployee.get(a) != null && !alFDEmployee.get(a).trim().equals("")) {
								if(sbFDEmps == null) {
									sbFDEmps = new StringBuilder();
									sbFDEmps.append(","+ alFDEmployee.get(a).trim() +",");
								} else {
									sbFDEmps.append(alFDEmployee.get(a).trim() +",");
								}
							}
						}
						if(sbFDEmps == null) {
							sbFDEmps = new StringBuilder();
						}
						
						String[] proFolderDocPoc = request.getParameterValues("proSubFolderDocPoc"+getProId()+"_"+getTaskId()+"_1_"+SubfolderDocsTRId[k]);
						List<String> alFDPoc = null;
						if(proFolderDocPoc != null) {
							alFDPoc = Arrays.asList(proFolderDocPoc);
						}
						StringBuilder sbFDPoc = null;
						
						for(int a=0; alFDPoc != null && a<alFDPoc.size(); a++) {
							if(alFDPoc.get(a) != null && !alFDPoc.get(a).trim().equals("")) {
								if(sbFDPoc == null) {
									sbFDPoc = new StringBuilder();
									sbFDPoc.append(","+ alFDPoc.get(a).trim() +",");
								} else {
									sbFDPoc.append(alFDPoc.get(a).trim() +",");
								}
							}
						}
						if(sbFDPoc == null) {
							sbFDPoc = new StringBuilder();
						}
						
						pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
						"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
						pst.setString(1, strSubFolderDocDescription[k]);
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							pst.setInt(2, 1);
						} else {
							pst.setInt(2, 0);
						}
//						pst.setInt(2, uF.parseToInt(getStrAlignWith()));
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							pst.setInt(3, uF.parseToInt(getProId()));
						} else {
							pst.setInt(3, uF.parseToInt(proSubFolderDocCategory));
						}
//						pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
						pst.setString(4, sbFDEmps.toString());
						pst.setString(5, "");
						pst.setString(6, "");
						pst.setInt(7, uF.parseToInt(SubfolderDocDharingType[k]));
						pst.setString(8, sbFDEmps.toString());
						pst.setInt(9, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(11, strFileName);
						pst.executeUpdate();
						pst.close();
						
						String feedId = null;
						pst = con.prepareStatement("select max(communication_id) as communication_id from communication_1");
						rs = pst.executeQuery();
						while(rs.next()) {
							feedId = rs.getString("communication_id");
						}
						rs.close();
						pst.close();
						
						pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, " +
								"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document," +
								"description,doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc,feed_id)" +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getClientId()));
						pst.setInt(2, uF.parseToInt(getProId()));
						pst.setString(3, strFileName);
						pst.setInt(4, uF.parseToInt(strSessionEmpId));
//						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(6, "folder");
						pst.setInt(7, uF.parseToInt(getProFolderId()));
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							pst.setInt(8, uF.parseToInt(proSubFolderDocTasks));
						} else {
							pst.setInt(8, uF.parseToInt(proSubFolderDocCategory));
						}
						pst.setInt(9, uF.parseToInt(SubfolderDocDharingType[k]));
						pst.setString(10, sbFDEmps.toString());
						pst.setInt(11, uF.parseToInt(proCategoryTypeSubFolderDoc[k]));
						pst.setString(12, strSubFolderScopeDoc[k]);
						pst.setString(13, strSubFolderDocDescription[k]);
						pst.setInt(14, 0);
						pst.setInt(15, 1);
						pst.setBoolean(16, uF.parseToBoolean(isSubFolderDocEdit[k]));
						pst.setBoolean(17, uF.parseToBoolean(isSubFolderDocDelete[k]));
						if(strUserType != null && strUserType.equals(CUSTOMER)) {
							pst.setBoolean(18, true);
						} else {
							pst.setBoolean(18, false);
						}
						pst.setString(19, sbFDPoc.toString());
						pst.setInt(20, uF.parseToInt(feedId));
	//					System.out.println("pst====>"+pst);
						pst.execute();
						pst.close();
						
						String proDocumentId = "";
						pst = con.prepareStatement("select max(pro_document_id) as pro_document_id from project_document_details");
						rs = pst.executeQuery();
						while (rs.next()) {
							proDocumentId = rs.getString("pro_document_id");
						}
						rs.close();
						pst.close();
						
						
						uploadProjectFolderDocuments(con, getProId(), files[k], strFileName, proFolderNameFolder, proDocumentId);
						
						/**
						 * Alerts
						 * */
						
						List<String> alEmp= null;
						if(proFolderDocEmployee != null) {
							alEmp = Arrays.asList(proFolderDocEmployee);
						}
						if(alEmp == null){
							alEmp = new ArrayList<String>();
						}
						
						List<String> alSharePoc = null;
						if(proFolderDocPoc != null) {
							alSharePoc = Arrays.asList(proFolderDocPoc);
						}
						if(alSharePoc == null){
							alSharePoc = new ArrayList<String>();
						}
						
						String strDocumentName = "";
						pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
						pst.setInt(1, uF.parseToInt(proDocumentId));
						rs = pst.executeQuery();
						while (rs.next()) {
							strDocumentName = rs.getString("folder_name");
						}
						rs.close();
						pst.close();
						
						String proName1 = null;
						String strCategory = null;
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
							pst.setInt(1, uF.parseToInt(proSubFolderDocTasks));
							rs = pst.executeQuery();
							
							while(rs.next()) {
								proName1 = rs.getString("pro_name");
							}
							rs.close();
							pst.close();
						} else {
							pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
							pst.setInt(1, uF.parseToInt(proSubFolderDocCategory));
							rs = pst.executeQuery();
							while(rs.next()) {
								strCategory = rs.getString("project_category");
							}
							rs.close();
							pst.close();
						}
						Notifications nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
						nF.setDomain(strDomain);
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						
						nF.request = request;
						nF.setStrOrgId((String)session.getAttribute(ORGID));
						nF.setEmailTemplate(true);
						
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							nF.setStrProjectName(proName1);
						} else {
							nF.setStrCategoryName(strCategory);
						}
						nF.setStrDocumentName(strDocumentName);
						
						String alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						String alertAction = "DocumentListView.action";
						for(String strEmp : alEmp){
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							nF.setStrEmpId(strEmp.trim());
							nF.sendNotifications();
						}
						for(String strEmp : alSharePoc){
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrOther("other");
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							pst = con.prepareStatement("select * from client_poc where poc_id = ?");
							pst.setInt(1, uF.parseToInt(strEmp.trim()));
							rs = pst.executeQuery();
							boolean flg=false;
							while(rs.next()) {
								nF.setStrCustFName(rs.getString("contact_fname"));
								nF.setStrCustLName(rs.getString("contact_lname"));
								nF.setStrEmpMobileNo(rs.getString("contact_number"));
								if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
									nF.setStrEmpEmail(rs.getString("contact_email"));
									nF.setStrEmailTo(rs.getString("contact_email"));
								}
								flg = true;
							}
							rs.close();
							pst.close();
							
							if(flg) {
								nF.setStrEmpId(strEmp.trim());
								nF.sendNotifications();
							}
						}
						/**
						 * Alerts End
						 * */
					}
				}
				
				int subFolderDocsCnt = SubfolderDocsTRId!=null ? SubfolderDocsTRId.length : 0; 
				String taskName = CF.getProjectTaskNameByTaskId(con, uF, getTaskId());
				StringBuilder sbMsg = new StringBuilder();
				sbMsg.append(SUCCESSM+""+getFolderName()+" folder updated ");
				if(subFolderDocsCnt>0) {
					sbMsg.append("and some documents added ");
				}
				sbMsg.append("successfully for "+taskName+"."+END);
				session.setAttribute(MESSAGE, sbMsg.toString());
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void createVAPFolderForDocs(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
//			System.out.println("getOperation() ===>> " + getOperation());
			
//			String orgName = CF.getStrOrgName();
			String mainPathWithOrg = CF.getProjectDocumentFolder()+"/"+strOrgId;
			
			String mainPath = mainPathWithOrg+"/Projects";
			
			setPrjectname(CF.getProjectNameById(con, getProId()));
			
			String proNameFolder = mainPath +"/"+getProId();
			String proFolderNameFolder = "";
			if(getType()!=null && getType().equals("SF")) {
				pst = con.prepareStatement("select * from project_document_details where pro_document_id=(select pro_folder_id from project_document_details where pro_document_id=?)");
				pst.setInt(1, uF.parseToInt(getProFolderId()));
				rs = pst.executeQuery();
				String strMainFolder = null;
				while (rs.next()) {
					strMainFolder = rs.getString("folder_name");
				}
				rs.close();
				pst.close();
				if(strMainFolder!=null){
					proFolderNameFolder = proNameFolder+"/"+ strMainFolder +"/"+ getFolderName();
				}
			} else {
				proFolderNameFolder = proNameFolder +"/"+ getFolderName();
			}
			
			if(getType()!=null && getType().equals("F")) {
				
				String[] folderDocsTRId = request.getParameterValues("folderDocsTRId"+getProId()+"_1");
				File[] strFolderDoc = mpRequest.getFiles("strFolderDoc"+getProId()+"_1");    //  
				String[] strFolderDocFileName = mpRequest.getFileNames("strFolderDoc"+getProId()+"_1"); 
				
//				String[] proFolderDocTasks = request.getParameterValues("proFolderDocTasks1");
				String[] folderDocDharingType = request.getParameterValues("folderDocDharingType"+getProId()+"_1");
				String[] proCategoryTypeFolderDoc = request.getParameterValues("proCategoryTypeFolderDoc"+getProId()+"_1");
				String[] strFolderDocDescription = request.getParameterValues("strFolderDocDescription"+getProId()+"_1");
				String[] strFolderScopeDoc = request.getParameterValues("strFolderScopeDoc"+getProId()+"_1");
				
				String[] isFolderDocEdit = request.getParameterValues("isFolderDocEdit" + getProId()+"_1");
				String[] isFolderDocDelete = request.getParameterValues("isFolderDocDelete" + getProId()+"_1");
				
				for(int j=0; folderDocsTRId != null && j<folderDocsTRId.length; j++) {
				
					String proFolderDocTasks = request.getParameter("proFolderDocTasks"+getProId()+"_1_"+folderDocsTRId[j]);
					String proFolderDocCategory = request.getParameter("proFolderDocCategory"+getProId()+"_1_"+folderDocsTRId[j]);
//					System.out.println("folderDocsTRId[j] ===>> " + folderDocsTRId[j]);
//					System.out.println("getFolderTRId()[i] ===>> " + getFolderTRId()[i]);
					double lengthBytes =  strFolderDoc[j].length();
					boolean isFileExist = false;
					
					String extenstion=FilenameUtils.getExtension(strFolderDocFileName[j]);	
					String strFileName = FilenameUtils.getBaseName(strFolderDocFileName[j]);
					strFileName = strFileName+"v1."+extenstion;
					File f = new File(proFolderNameFolder+"/"+strFileName);
					if(f.isFile()) {
					    System.out.println("isFile");
					    if(f.exists()) {
							isFileExist = true;
						    System.out.println("exists");
						} else {
						    System.out.println("exists fail");
						}   
					} else {
					    System.out.println("isFile fail");
					}
					if(lengthBytes > 0 && !isFileExist) {
						String[] proFolderDocEmployee = request.getParameterValues("proFolderDocEmployee"+getProId()+"_1_"+folderDocsTRId[j]);
						List<String> alFDEmployee = null;
						if(proFolderDocEmployee != null) {
							alFDEmployee = Arrays.asList(proFolderDocEmployee);
						}
						StringBuilder sbFDEmps = null;
						
						for(int a=0; alFDEmployee != null && a<alFDEmployee.size(); a++) {
							if(alFDEmployee.get(a) != null && !alFDEmployee.get(a).trim().equals("")) {
								if(sbFDEmps == null) {
									sbFDEmps = new StringBuilder();
									sbFDEmps.append(","+ alFDEmployee.get(a).trim() +",");
								} else {
									sbFDEmps.append(alFDEmployee.get(a).trim() +",");
								}
							}
						}
						if(sbFDEmps == null) {
							sbFDEmps = new StringBuilder();
						}
						
						String[] proFolderDocPoc = request.getParameterValues("proFolderDocPoc"+getProId()+"_1_"+folderDocsTRId[j]);
						List<String> alFDPoc = null;
						if(proFolderDocPoc != null) {
							alFDPoc = Arrays.asList(proFolderDocPoc);
						}
						StringBuilder sbFDPoc = null;
						
						for(int a=0; alFDPoc != null && a<alFDPoc.size(); a++) {
							if(alFDPoc.get(a) != null && !alFDPoc.get(a).trim().equals("")) {
								if(sbFDPoc == null) {
									sbFDPoc = new StringBuilder();
									sbFDPoc.append(","+ alFDPoc.get(a).trim() +",");
								} else {
									sbFDPoc.append(alFDPoc.get(a).trim() +",");
								}
							}
						}
						if(sbFDPoc == null) {
							sbFDPoc = new StringBuilder();
						}
						
						
						pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
						"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
						pst.setString(1, strFolderDocDescription[j]);
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(2, 1);
						} else {
							pst.setInt(2, 0);
						}
//						pst.setInt(2, uF.parseToInt(getStrAlignWith()));
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(3, uF.parseToInt(getProId()));
						} else {
							pst.setInt(3, uF.parseToInt(proFolderDocCategory));
						}
//						pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
						pst.setString(4, sbFDEmps.toString());
						pst.setString(5, "");
						pst.setString(6, "");
						pst.setInt(7, uF.parseToInt(folderDocSharingType[j]));
						pst.setString(8, sbFDEmps.toString());
						pst.setInt(9, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(11, strFileName);
						pst.executeUpdate();
						pst.close();
						
						String feedId = null;
						pst = con.prepareStatement("select max(communication_id) as communication_id from communication_1");
						rs = pst.executeQuery();
						while(rs.next()) {
							feedId = rs.getString("communication_id");
						}
						rs.close();
						pst.close();
						
						
						pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, " +
								"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document," +
								"description,doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc,feed_id)" +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getClientId()));
						pst.setInt(2, uF.parseToInt(getProId()));
						pst.setString(3, getFolderName());
						pst.setInt(4, uF.parseToInt(strSessionEmpId));
//						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(6, "folder");
						pst.setInt(7, uF.parseToInt(getProFolderId()));
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(8, uF.parseToInt(proFolderDocTasks));
						} else {
							pst.setInt(8, uF.parseToInt(proFolderDocCategory));
						}
						pst.setInt(9, uF.parseToInt(folderDocDharingType[j]));
						pst.setString(10, sbFDEmps.toString());
						pst.setInt(11, uF.parseToInt(proCategoryTypeFolderDoc[j]));
						pst.setString(12, strFolderScopeDoc[j]);
						pst.setString(13, strFolderDocDescription[j]);
						pst.setInt(14, 0);
						pst.setInt(15, 1);
						pst.setBoolean(16, uF.parseToBoolean(isFolderDocEdit[j]));
						pst.setBoolean(17, uF.parseToBoolean(isFolderDocDelete[j]));
						if(strUserType != null && strUserType.equals(CUSTOMER)) {
							pst.setBoolean(18, true);
						} else {
							pst.setBoolean(18, false);
						}
						pst.setString(19, sbFDPoc.toString());
						pst.setInt(20, uF.parseToInt(feedId));
	//					System.out.println("pst====>"+pst);
						pst.execute();
						pst.close();
						
						
						String proDocumentId = "";
						pst = con.prepareStatement("select max(pro_document_id) as pro_document_id from project_document_details");
						rs = pst.executeQuery();
						while (rs.next()) {
							proDocumentId = rs.getString("pro_document_id");
						}
						rs.close();
						pst.close();
						
						
						uploadProjectFolderDocuments(con, getProId(), strFolderDoc[j], strFileName, proFolderNameFolder, proDocumentId);
						
						/**
						 * Alerts
						 * */
						List<String> alEmp= null;
						if(proFolderDocEmployee != null) {
							alEmp = Arrays.asList(proFolderDocEmployee);
						}
						if(alEmp == null){
							alEmp = new ArrayList<String>();
						}
						
						List<String> alSharePoc = null;
						if(proFolderDocPoc != null) {
							alSharePoc = Arrays.asList(proFolderDocPoc);
						}
						if(alSharePoc == null){
							alSharePoc = new ArrayList<String>();
						}
						
						String strDocumentName = "";
						pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
						pst.setInt(1, uF.parseToInt(proDocumentId));
						rs = pst.executeQuery();
						while (rs.next()) {
							strDocumentName = rs.getString("folder_name");
						}
						rs.close();
						pst.close();
						
						String proName1 = null;
						String strCategory = null;
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
							pst.setInt(1, uF.parseToInt(proFolderDocTasks));
							rs = pst.executeQuery();
							
							while(rs.next()) {
								proName1 = rs.getString("pro_name");
							}
							rs.close();
							pst.close();
						} else {
							pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
							pst.setInt(1, uF.parseToInt(proFolderDocCategory));
							rs = pst.executeQuery();
							while(rs.next()) {
								strCategory = rs.getString("project_category");
							}
							rs.close();
							pst.close();
						}
						Notifications nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
						nF.setDomain(strDomain);
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						
						nF.request = request;
						nF.setStrOrgId((String)session.getAttribute(ORGID));
						nF.setEmailTemplate(true);
						
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							nF.setStrProjectName(proName1);
						} else {
							nF.setStrCategoryName(strCategory);
						}
						nF.setStrDocumentName(strDocumentName);
						
						String alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						String alertAction = "DocumentListView.action";
						for(String strEmp : alEmp){
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							nF.setStrEmpId(strEmp.trim());
							nF.sendNotifications();
						}
						for(String strEmp : alSharePoc){
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrOther("other");
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							pst = con.prepareStatement("select * from client_poc where poc_id = ?");
							pst.setInt(1, uF.parseToInt(strEmp.trim()));
							rs = pst.executeQuery();
							boolean flg=false;
							while(rs.next()) {
								nF.setStrCustFName(rs.getString("contact_fname"));
								nF.setStrCustLName(rs.getString("contact_lname"));
								nF.setStrEmpMobileNo(rs.getString("contact_number"));
								if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
									nF.setStrEmpEmail(rs.getString("contact_email"));
									nF.setStrEmailTo(rs.getString("contact_email"));
								}
								flg = true;
							}
							rs.close();
							pst.close();
							
							if(flg) {
								nF.setStrEmpId(strEmp.trim());
								nF.sendNotifications();
							}
						}
						/**
						 * Alerts End
						 * */
						
					}
				}
				
				String[] SubFolderTR = request.getParameterValues("SubFolderTR"+getProId()+"_1");
				String[] strSubFolderName = request.getParameterValues("strSubFolderName"+getProId()+"_1");
				String[] proCategoryTypeSubFolder = request.getParameterValues("proCategoryTypeSubFolder"+getProId()+"_1");
//				String[] proSubFolderTasks = request.getParameterValues("proSubFolderTasks1");
				String[] SubfolderSharingType = request.getParameterValues("SubfolderSharingType"+getProId()+"_1");
				String[] strSubFolderDescription = request.getParameterValues("strSubFolderDescription"+getProId()+"_1");
				
				String[] isSubFolderEdit = request.getParameterValues("isSubFolderEdit" + getProId()+"_1");
				String[] isSubFolderDelete = request.getParameterValues("isSubFolderDelete" + getProId()+"_1");
				
				for(int j = 0; SubFolderTR != null && j < SubFolderTR.length; j++) {
					
					String proSubFolderTasks = request.getParameter("proSubFolderTasks"+getProId()+"_1_"+SubFolderTR[j]);
					String proSubFolderCategory = request.getParameter("proSubFolderCategory"+getProId()+"_1_"+SubFolderTR[j]);

					String[] proSubFolderEmployee = request.getParameterValues("proSubFolderEmployee"+getProId()+"_1_"+SubFolderTR[j]);
					List<String> alSubEmployee = null;
					if(proSubFolderEmployee != null) {
						alSubEmployee = Arrays.asList(proSubFolderEmployee);
					}
					StringBuilder sbSubEmps = null;
					
					for(int a=0; alSubEmployee != null && a<alSubEmployee.size(); a++) {
						if(alSubEmployee.get(a) != null && !alSubEmployee.get(a).trim().equals("")) {
							if(sbSubEmps == null) {
								sbSubEmps = new StringBuilder();
								sbSubEmps.append(","+ alSubEmployee.get(a).trim() +",");
							} else {
								sbSubEmps.append(alSubEmployee.get(a).trim() +",");
							}
						}
					}
					if(sbSubEmps == null) {
						sbSubEmps = new StringBuilder();
					}
					
					String[] proSubFolderPoc = request.getParameterValues("proSubFolderPoc"+getProId()+"_1_"+SubFolderTR[j]);
					List<String> alSubPoc = null;
					if(proSubFolderPoc != null) {
						alSubPoc = Arrays.asList(proSubFolderPoc);
					}
					StringBuilder sbSubPoc = null;
					
					for(int a=0; alSubPoc != null && a<alSubPoc.size(); a++) {
						if(alSubPoc.get(a) != null && !alSubPoc.get(a).trim().equals("")) {
							if(sbSubPoc == null) {
								sbSubPoc = new StringBuilder();
								sbSubPoc.append(","+ alSubPoc.get(a).trim() +",");
							} else {
								sbSubPoc.append(alSubPoc.get(a).trim() +",");
							}
						}
					}
					if(sbSubPoc == null) {
						sbSubPoc = new StringBuilder();
					}
					
					String proSubFolderNameFolder = proFolderNameFolder +"/"+ strSubFolderName[j];
					
					File file3 = new File(proSubFolderNameFolder);
					if (!file3.exists()) {
						if (file3.mkdir()) {
							System.out.println("Directory is created!");
						}
					}
					
					pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, " +
							"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document," +
							"description,doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc)" +
							"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(getClientId()));
					pst.setInt(2, uF.parseToInt(getProId()));
					pst.setString(3, strSubFolderName[j]);
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
//					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setString(6, "folder");
					pst.setInt(7, uF.parseToInt(getProFolderId()));
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						pst.setInt(8, uF.parseToInt(proSubFolderTasks));
					} else {
						pst.setInt(8, uF.parseToInt(proSubFolderCategory));
					}
					pst.setInt(9, uF.parseToInt(SubfolderSharingType[j]));
					pst.setString(10, sbSubEmps.toString());
					pst.setInt(11, uF.parseToInt(proCategoryTypeSubFolder[j]));
					pst.setString(12, null);
					pst.setString(13, strSubFolderDescription[j]);
					pst.setInt(14, 0);
					pst.setInt(15, 0);
					pst.setBoolean(16, uF.parseToBoolean(isSubFolderEdit[j]));
					pst.setBoolean(17, uF.parseToBoolean(isSubFolderDelete[j]));
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						pst.setBoolean(18, true);
					} else {
						pst.setBoolean(18, false);
					}
					pst.setString(19, sbSubPoc.toString());
			//		System.out.println("pst====>"+pst);
					pst.execute();
					pst.close();
			
					String proSubFolderId = "";
					pst = con.prepareStatement("select max(pro_document_id)as pro_document_id from project_document_details");
					rs = pst.executeQuery();
					while (rs.next()) {
						proSubFolderId = rs.getString("pro_document_id");
					}
					rs.close();
					pst.close();
					
					/**
					 * Alerts
					 * */
					List<String> alEmp= null;
					if(proSubFolderEmployee != null) {
						alEmp = Arrays.asList(proSubFolderEmployee);
					}
					if(alEmp == null){
						alEmp = new ArrayList<String>();
					}
					
					List<String> alSharePoc = null;
					if(proSubFolderPoc != null) {
						alSharePoc = Arrays.asList(proSubFolderPoc);
					}
					if(alSharePoc == null){
						alSharePoc = new ArrayList<String>();
					}
					
					String strDocumentName = "";
					pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
					pst.setInt(1, uF.parseToInt(proSubFolderId));
					rs = pst.executeQuery();
					while (rs.next()) {
						strDocumentName = rs.getString("folder_name");
					}
					rs.close();
					pst.close();
					
					String proName1 = null;
					String strCategory = null;
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
						pst.setInt(1, uF.parseToInt(proSubFolderTasks));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							proName1 = rs.getString("pro_name");
						}
						rs.close();
						pst.close();
					} else {
						pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
						pst.setInt(1, uF.parseToInt(proSubFolderCategory));
						rs = pst.executeQuery();
						while(rs.next()) {
							strCategory = rs.getString("project_category");
						}
						rs.close();
						pst.close();
					}
					Notifications nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
					nF.setDomain(strDomain);
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					
					nF.request = request;
					nF.setStrOrgId((String)session.getAttribute(ORGID));
					nF.setEmailTemplate(true);
					
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						nF.setStrProjectName(proName1);
					} else {
						nF.setStrCategoryName(strCategory);
					}
					nF.setStrDocumentName(strDocumentName);
					
					String alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					String alertAction = "DocumentListView.action";
					for(String strEmp : alEmp){
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						//Mail
						nF.setStrEmpId(strEmp.trim());
						nF.sendNotifications();
					}
					for(String strEmp : alSharePoc){
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrOther("other");
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						//Mail
						pst = con.prepareStatement("select * from client_poc where poc_id = ?");
						pst.setInt(1, uF.parseToInt(strEmp.trim()));
						rs = pst.executeQuery();
						boolean flg=false;
						while(rs.next()) {
							nF.setStrCustFName(rs.getString("contact_fname"));
							nF.setStrCustLName(rs.getString("contact_lname"));
							nF.setStrEmpMobileNo(rs.getString("contact_number"));
							if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
								nF.setStrEmpEmail(rs.getString("contact_email"));
								nF.setStrEmailTo(rs.getString("contact_email"));
							}
							flg = true;
						}
						rs.close();
						pst.close();
						
						if(flg) {
							nF.setStrEmpId(strEmp.trim());
							nF.sendNotifications();
						}
					}
					/**
					 * Alerts End
					 * */
					
					
//					System.out.println("SubFolderTR[j] ====>> " + SubFolderTR[j]);
					String[] SubfolderDocsTRId = request.getParameterValues("SubfolderDocsTRId"+getProId()+"_"+SubFolderTR[j]);
//					System.out.println("SubfolderDocsTRId ====>> " + SubfolderDocsTRId.length);
					File[] files = mpRequest.getFiles("strSubFolderDoc"+getProId()+"_"+SubFolderTR[j]);    //  
					String[] fileNames = mpRequest.getFileNames("strSubFolderDoc"+getProId()+"_"+SubFolderTR[j]); 
					String[] proCategoryTypeSubFolderDoc = request.getParameterValues("proCategoryTypeSubFolderDoc"+getProId()+"_"+SubFolderTR[j]);
//					String[] proSubFolderDocTasks = request.getParameterValues("proSubFolderDocTasks" +SubFolderTR[j]);
					String[] SubfolderDocDharingType = request.getParameterValues("SubfolderDocDharingType"+getProId()+"_"+SubFolderTR[j]);
					String[] strSubFolderDocDescription = request.getParameterValues("strSubFolderDocDescription"+getProId()+"_"+SubFolderTR[j]);
					String[] strSubFolderScopeDoc = request.getParameterValues("strSubFolderScopeDoc"+getProId()+"_"+SubFolderTR[j]);
					
					String[] isSubFolderDocEdit = request.getParameterValues("isSubFolderDocEdit"+getProId()+"_"+SubFolderTR[j]);
					String[] isSubFolderDocDelete = request.getParameterValues("isSubFolderDocDelete"+getProId()+"_"+SubFolderTR[j]);
					
					for(int k=0; SubfolderDocsTRId != null && k < SubfolderDocsTRId.length; k++) {
						
						String proSubFolderDocTasks = request.getParameter("proSubFolderDocTasks"+getProId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
						String proSubFolderDocCategory = request.getParameter("proSubFolderDocCategory"+getProId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
						
						double lengthBytes =  files[k].length();
						boolean isFileExist = false;
//						System.out.println("fileNames[k] ===>> " + fileNames[k] + " ----- " + proSubFolderNameFolder+"/"+fileNames[k]);
						String extenstion=FilenameUtils.getExtension(fileNames[k]);	
						String strFileName = FilenameUtils.getBaseName(fileNames[k]);
						strFileName = strFileName+"v1."+extenstion;
						File f = new File(proSubFolderNameFolder+"/"+strFileName);
						if(f.isFile()) {
						    System.out.println("isFile");
						    if(f.exists()) {
								isFileExist = true;
							    System.out.println("exists");
							} else {
							    System.out.println("exists fail");
							}   
						} else {
						    System.out.println("isFile fail");
						}
						
						if(lengthBytes > 0 && !isFileExist) {
							String[] proFolderDocEmployee = request.getParameterValues("proSubFolderDocEmployee"+getProId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
							List<String> alFDEmployee = null;
							if(proFolderDocEmployee != null) {
								alFDEmployee = Arrays.asList(proFolderDocEmployee);
							}
							StringBuilder sbFDEmps = null;
							
							for(int a=0; alFDEmployee != null && a<alFDEmployee.size(); a++) {
								if(alFDEmployee.get(a) != null && !alFDEmployee.get(a).trim().equals("")) {
									if(sbFDEmps == null) {
										sbFDEmps = new StringBuilder();
										sbFDEmps.append(","+ alFDEmployee.get(a).trim() +",");
									} else {
										sbFDEmps.append(alFDEmployee.get(a).trim() +",");
									}
								}
							}
							if(sbFDEmps == null) {
								sbFDEmps = new StringBuilder();
							}
							
							String[] proFolderDocPoc = request.getParameterValues("proSubFolderDocPoc"+getProId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
							List<String> alFDPoc = null;
							if(proFolderDocPoc != null) {
								alFDPoc = Arrays.asList(proFolderDocPoc);
							}
							StringBuilder sbFDPoc = null;
							
							for(int a=0; alFDPoc != null && a<alFDPoc.size(); a++) {
								if(alFDPoc.get(a) != null && !alFDPoc.get(a).trim().equals("")) {
									if(sbFDPoc == null) {
										sbFDPoc = new StringBuilder();
										sbFDPoc.append(","+ alFDPoc.get(a).trim() +",");
									} else {
										sbFDPoc.append(alFDPoc.get(a).trim() +",");
									}
								}
							}
							if(sbFDPoc == null) {
								sbFDPoc = new StringBuilder();
							}
							
							
							pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
							"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
							pst.setString(1, strSubFolderDocDescription[k]);
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(2, 1);
							} else {
								pst.setInt(2, 0);
							}
//							pst.setInt(2, uF.parseToInt(getStrAlignWith()));
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(3, uF.parseToInt(getProId()));
							} else {
								pst.setInt(3, uF.parseToInt(proSubFolderDocCategory));
							}
//							pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
							pst.setString(4, sbFDEmps.toString());
							pst.setString(5, "");
							pst.setString(6, "");
							pst.setInt(7, uF.parseToInt(SubfolderDocDharingType[k]));
							pst.setString(8, sbFDEmps.toString());
							pst.setInt(9, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
							pst.setString(11, strFileName);
							pst.executeUpdate();
							pst.close();
							
							String feedId = null;
							pst = con.prepareStatement("select max(communication_id) as communication_id from communication_1");
							rs = pst.executeQuery();
							while(rs.next()) {
								feedId = rs.getString("communication_id");
							}
							rs.close();
							pst.close();
							
							
							pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, " +
									"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document," +
									"description,doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc,feed_id)" +
									"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
							pst.setInt(1, uF.parseToInt(getClientId()));
							pst.setInt(2, uF.parseToInt(getProId()));
							pst.setString(3, strFileName);
							pst.setInt(4, uF.parseToInt(strSessionEmpId));
//							pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
							pst.setString(6, "folder");
							pst.setInt(7, uF.parseToInt(proSubFolderId));
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(8, uF.parseToInt(proSubFolderDocTasks));
							} else {
								pst.setInt(8, uF.parseToInt(proSubFolderDocCategory));
							}
							pst.setInt(9, uF.parseToInt(SubfolderDocDharingType[k]));
							pst.setString(10, sbFDEmps.toString());
							pst.setInt(11, uF.parseToInt(proCategoryTypeSubFolderDoc[k]));
							pst.setString(12, strSubFolderScopeDoc[k]);
							pst.setString(13, strSubFolderDocDescription[k]);
							pst.setInt(14, 0);
							pst.setInt(15, 1);
							pst.setBoolean(16, uF.parseToBoolean(isSubFolderDocEdit[k]));
							pst.setBoolean(17, uF.parseToBoolean(isSubFolderDocDelete[k]));
							if(strUserType != null && strUserType.equals(CUSTOMER)) {
								pst.setBoolean(18, true);
							} else {
								pst.setBoolean(18, false);
							}
							pst.setString(19, sbFDPoc.toString());
							pst.setInt(20, uF.parseToInt(feedId));
		//					System.out.println("pst====>"+pst);
							pst.execute();
							pst.close();
							
							String proDocumentId = "";
							pst = con.prepareStatement("select max(pro_document_id) as pro_document_id from project_document_details");
							rs = pst.executeQuery();
							while (rs.next()) {
								proDocumentId = rs.getString("pro_document_id");
							}
							rs.close();
							pst.close();
							
							
							uploadProjectFolderDocuments(con, getProId(), files[k], strFileName, proSubFolderNameFolder, proDocumentId);
							
							
							/**
							 * Alerts
							 * */
							List<String> alEmp1= null;
							if(proFolderDocEmployee != null) {
								alEmp1 = Arrays.asList(proFolderDocEmployee);
							}
							if(alEmp1 == null){
								alEmp1 = new ArrayList<String>();
							}
							
							List<String> alSharePoc1 = null;
							if(proFolderDocPoc != null) {
								alSharePoc1 = Arrays.asList(proFolderDocPoc);
							}
							if(alSharePoc == null){
								alSharePoc1 = new ArrayList<String>();
							}
							
							strDocumentName = "";
							pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
							pst.setInt(1, uF.parseToInt(proDocumentId));
							rs = pst.executeQuery();
							while (rs.next()) {
								strDocumentName = rs.getString("folder_name");
							}
							rs.close();
							pst.close();
							
							proName1 = null;
							strCategory = null;
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
								pst.setInt(1, uF.parseToInt(proSubFolderDocTasks));
								rs = pst.executeQuery();
								
								while(rs.next()) {
									proName1 = rs.getString("pro_name");
								}
								rs.close();
								pst.close();
							} else {
								pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
								pst.setInt(1, uF.parseToInt(proSubFolderDocCategory));
								rs = pst.executeQuery();
								while(rs.next()) {
									strCategory = rs.getString("project_category");
								}
								rs.close();
								pst.close();
							}
							nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
							nF.setDomain(strDomain);
							nF.setStrHostAddress(CF.getStrEmailLocalHost());
							nF.setStrHostPort(CF.getStrHostPort());
							nF.setStrContextPath(request.getContextPath());
							
							nF.request = request;
							nF.setStrOrgId((String)session.getAttribute(ORGID));
							nF.setEmailTemplate(true);
							
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								nF.setStrProjectName(proName1);
							} else {
								nF.setStrCategoryName(strCategory);
							}
							nF.setStrDocumentName(strDocumentName);
							
							alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
							alertAction = "DocumentListView.action";
							for(String strEmp : alEmp1){
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strEmp.trim());
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
//								userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
								userAlerts.setStatus(INSERT_TR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
								
								//Mail
								nF.setStrEmpId(strEmp.trim());
								nF.sendNotifications();
							}
							for(String strEmp : alSharePoc1){
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strEmp.trim());
								userAlerts.setStrOther("other");
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
//								userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
								userAlerts.setStatus(INSERT_TR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
								
								//Mail
								pst = con.prepareStatement("select * from client_poc where poc_id = ?");
								pst.setInt(1, uF.parseToInt(strEmp.trim()));
								rs = pst.executeQuery();
								boolean flg=false;
								while(rs.next()) {
									nF.setStrCustFName(rs.getString("contact_fname"));
									nF.setStrCustLName(rs.getString("contact_lname"));
									nF.setStrEmpMobileNo(rs.getString("contact_number"));
									if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
										nF.setStrEmpEmail(rs.getString("contact_email"));
										nF.setStrEmailTo(rs.getString("contact_email"));
									}
									flg = true;
								}
								rs.close();
								pst.close();
								
								if(flg) {
									nF.setStrEmpId(strEmp.trim());
									nF.sendNotifications();
								}
							}
							/**
							 * Alerts End
							 * */
							
						}
					}
				}
				
					int subFolderCnt = SubFolderTR!=null ? SubFolderTR.length : 0;
					String proName = CF.getProjectNameById(con, getProId());
					StringBuilder sbMsg = new StringBuilder();
					
					sbMsg.append(SUCCESSM+""+getFolderName()+" folder updated ");
					if(subFolderCnt>0) {
						sbMsg.append("and some documents added ");
					}
					sbMsg.append("successfully for "+proName+"."+END);
					session.setAttribute(MESSAGE, sbMsg.toString());
					
			} else if(getType()!=null && getType().equals("SF")) {
				
				String[] SubfolderDocsTRId = request.getParameterValues("SubfolderDocsTRId"+getProId()+"_1");
//				System.out.println("SubfolderDocsTRId ====>> " + SubfolderDocsTRId.length);
				File[] files = mpRequest.getFiles("strSubFolderDoc"+getProId()+"_1");    //  
				String[] fileNames = mpRequest.getFileNames("strSubFolderDoc"+getProId()+"_1"); 
				String[] proCategoryTypeSubFolderDoc = request.getParameterValues("proCategoryTypeSubFolderDoc"+getProId()+"_1");
//				String[] proSubFolderDocTasks = request.getParameterValues("proSubFolderDocTasks1");
				String[] SubfolderDocDharingType = request.getParameterValues("SubfolderDocDharingType"+getProId()+"_1");
				String[] strSubFolderDocDescription = request.getParameterValues("strSubFolderDocDescription"+getProId()+"_1");
				String[] strSubFolderScopeDoc = request.getParameterValues("strSubFolderScopeDoc"+getProId()+"_1");
				
				String[] isSubFolderDocEdit = request.getParameterValues("isSubFolderDocEdit"+getProId()+"_1");
				String[] isSubFolderDocDelete = request.getParameterValues("isSubFolderDocDelete"+getProId()+"_1");
				
				for(int k=0; SubfolderDocsTRId != null && k < SubfolderDocsTRId.length; k++) {
					
					String proSubFolderDocTasks = request.getParameter("proSubFolderDocTasks"+getProId()+"_1_"+SubfolderDocsTRId[k]);
					String proSubFolderDocCategory = request.getParameter("proSubFolderDocCategory"+getProId()+"_1_"+SubfolderDocsTRId[k]);
					
					double lengthBytes =  files[k].length();
					boolean isFileExist = false;
//					System.out.println("fileNames[k] ===>> " + fileNames[k] + " ----- " + proSubFolderNameFolder+"/"+fileNames[k]);
					String extenstion=FilenameUtils.getExtension(fileNames[k]);	
					String strFileName = FilenameUtils.getBaseName(fileNames[k]);
					strFileName = strFileName+"v1."+extenstion;
					File f = new File(proFolderNameFolder+"/"+strFileName);
					if(f.isFile()) {
					    System.out.println("isFile");
					    if(f.exists()) {
							isFileExist = true;
						    System.out.println("exists");
						} else {
						    System.out.println("exists fail");
						}   
					} else {
					    System.out.println("isFile fail");
					}
					
					if(lengthBytes > 0 && !isFileExist) {
						String[] proFolderDocEmployee = request.getParameterValues("proSubFolderDocEmployee"+getProId()+"_1_"+SubfolderDocsTRId[k]);
						List<String> alFDEmployee = null;
						if(proFolderDocEmployee != null) {
							alFDEmployee = Arrays.asList(proFolderDocEmployee);
						}
						StringBuilder sbFDEmps = null;
						
						for(int a=0; alFDEmployee != null && a<alFDEmployee.size(); a++) {
							if(alFDEmployee.get(a) != null && !alFDEmployee.get(a).trim().equals("")) {
								if(sbFDEmps == null) {
									sbFDEmps = new StringBuilder();
									sbFDEmps.append(","+ alFDEmployee.get(a).trim() +",");
								} else {
									sbFDEmps.append(alFDEmployee.get(a).trim() +",");
								}
							}
						}
						if(sbFDEmps == null) {
							sbFDEmps = new StringBuilder();
						}
						
						String[] proFolderDocPoc = request.getParameterValues("proSubFolderDocPoc"+getProId()+"_1_"+SubfolderDocsTRId[k]);
						List<String> alFDPoc = null;
						if(proFolderDocPoc != null) {
							alFDPoc = Arrays.asList(proFolderDocPoc);
						}
						StringBuilder sbFDPoc = null;
						
						for(int a=0; alFDPoc != null && a<alFDPoc.size(); a++) {
							if(alFDPoc.get(a) != null && !alFDPoc.get(a).trim().equals("")) {
								if(sbFDPoc == null) {
									sbFDPoc = new StringBuilder();
									sbFDPoc.append(","+ alFDPoc.get(a).trim() +",");
								} else {
									sbFDPoc.append(alFDPoc.get(a).trim() +",");
								}
							}
						}
						if(sbFDPoc == null) {
							sbFDPoc = new StringBuilder();
						}
						
						
						pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
						"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
						pst.setString(1, strSubFolderDocDescription[k]);
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							pst.setInt(2, 1);
						} else {
							pst.setInt(2, 0);
						}
//						pst.setInt(2, uF.parseToInt(getStrAlignWith()));
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							pst.setInt(3, uF.parseToInt(getProId()));
						} else {
							pst.setInt(3, uF.parseToInt(proSubFolderDocCategory));
						}
//						pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
						pst.setString(4, sbFDEmps.toString());
						pst.setString(5, "");
						pst.setString(6, "");
						pst.setInt(7, uF.parseToInt(SubfolderDocDharingType[k]));
						pst.setString(8, sbFDEmps.toString());
						pst.setInt(9, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(11, strFileName);
						pst.executeUpdate();
						pst.close();
						
						String feedId = null;
						pst = con.prepareStatement("select max(communication_id) as communication_id from communication_1");
						rs = pst.executeQuery();
						while(rs.next()) {
							feedId = rs.getString("communication_id");
						}
						rs.close();
						pst.close();
						
						
						pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, " +
								"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document," +
								"description,doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc,feed_id)" +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getClientId()));
						pst.setInt(2, uF.parseToInt(getProId()));
						pst.setString(3, strFileName);
						pst.setInt(4, uF.parseToInt(strSessionEmpId));
//						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(6, "folder");
						pst.setInt(7, uF.parseToInt(getProFolderId()));
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							pst.setInt(8, uF.parseToInt(proSubFolderDocTasks));
						} else {
							pst.setInt(8, uF.parseToInt(proSubFolderDocCategory));
						}
						pst.setInt(9, uF.parseToInt(SubfolderDocDharingType[k]));
						pst.setString(10, sbFDEmps.toString());
						pst.setInt(11, uF.parseToInt(proCategoryTypeSubFolderDoc[k]));
						pst.setString(12, strSubFolderScopeDoc[k]);
						pst.setString(13, strSubFolderDocDescription[k]);
						pst.setInt(14, 0);
						pst.setInt(15, 1);
						pst.setBoolean(16, uF.parseToBoolean(isSubFolderDocEdit[k]));
						pst.setBoolean(17, uF.parseToBoolean(isSubFolderDocDelete[k]));
						if(strUserType != null && strUserType.equals(CUSTOMER)) {
							pst.setBoolean(18, true);
						} else {
							pst.setBoolean(18, false);
						}
						pst.setString(19, sbFDPoc.toString());
						pst.setInt(20, uF.parseToInt(feedId));
	//					System.out.println("pst====>"+pst);
						pst.execute();
						pst.close();
						
						String proDocumentId = "";
						pst = con.prepareStatement("select max(pro_document_id) as pro_document_id from project_document_details");
						rs = pst.executeQuery();
						while (rs.next()) {
							proDocumentId = rs.getString("pro_document_id");
						}
						rs.close();
						pst.close();
						
						
						uploadProjectFolderDocuments(con, getProId(), files[k], strFileName, proFolderNameFolder, proDocumentId);
						
						/**
						 * Alerts
						 * */
						List<String> alEmp= null;
						if(proFolderDocEmployee != null) {
							alEmp = Arrays.asList(proFolderDocEmployee);
						}
						if(alEmp == null){
							alEmp = new ArrayList<String>();
						}
						
						List<String> alSharePoc = null;
						if(proFolderDocPoc != null) {
							alSharePoc = Arrays.asList(proFolderDocPoc);
						}
						if(alSharePoc == null){
							alSharePoc = new ArrayList<String>();
						}
						
						String strDocumentName = "";
						pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
						pst.setInt(1, uF.parseToInt(proDocumentId));
						rs = pst.executeQuery();
						while (rs.next()) {
							strDocumentName = rs.getString("folder_name");
						}
						rs.close();
						pst.close();
						
						String proName1 = null;
						String strCategory = null;
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
							pst.setInt(1, uF.parseToInt(proSubFolderDocTasks));
							rs = pst.executeQuery();
							
							while(rs.next()) {
								proName1 = rs.getString("pro_name");
							}
							rs.close();
							pst.close();
						} else {
							pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
							pst.setInt(1, uF.parseToInt(proSubFolderDocCategory));
							rs = pst.executeQuery();
							while(rs.next()) {
								strCategory = rs.getString("project_category");
							}
							rs.close();
							pst.close();
						}
						Notifications nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
						nF.setDomain(strDomain);
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						
						nF.request = request;
						nF.setStrOrgId((String)session.getAttribute(ORGID));
						nF.setEmailTemplate(true);
						
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							nF.setStrProjectName(proName1);
						} else {
							nF.setStrCategoryName(strCategory);
						}
						nF.setStrDocumentName(strDocumentName);
						
						String alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						String alertAction = "DocumentListView.action";
						for(String strEmp : alEmp){
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							nF.setStrEmpId(strEmp.trim());
							nF.sendNotifications();
						}
						for(String strEmp : alSharePoc){
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrOther("other");
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							pst = con.prepareStatement("select * from client_poc where poc_id = ?");
							pst.setInt(1, uF.parseToInt(strEmp.trim()));
							rs = pst.executeQuery();
							boolean flg=false;
							while(rs.next()) {
								nF.setStrCustFName(rs.getString("contact_fname"));
								nF.setStrCustLName(rs.getString("contact_lname"));
								nF.setStrEmpMobileNo(rs.getString("contact_number"));
								if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
									nF.setStrEmpEmail(rs.getString("contact_email"));
									nF.setStrEmailTo(rs.getString("contact_email"));
								}
								flg = true;
							}
							rs.close();
							pst.close();
							
							if(flg) {
								nF.setStrEmpId(strEmp.trim());
								nF.sendNotifications();
							}
						}
						/**
						 * Alerts End
						 * */
						
					}
				}
				
				int subFolderDocsCnt = SubfolderDocsTRId!=null ? SubfolderDocsTRId.length : 0; 
				String proName = CF.getProjectNameById(con, getProId());
				StringBuilder sbMsg = new StringBuilder();
				sbMsg.append(SUCCESSM+""+getFolderName()+" folder updated ");
				if(subFolderDocsCnt>0) {
					sbMsg.append("and some documents added ");
				}
				sbMsg.append("successfully for "+proName+"."+END);
				session.setAttribute(MESSAGE, sbMsg.toString());
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getFolderAndDocuments(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String orgName = CF.getStrOrgName();
			request.setAttribute("orgName", orgName);
			
			String proDocMainPath = CF.getProjectDocumentFolder();
			request.setAttribute("proDocMainPath", proDocMainPath);
			
			String proDocRetrivePath = CF.getRetriveProjectDocumentFolder();
			request.setAttribute("proDocRetrivePath", proDocRetrivePath);
			request.setAttribute("strOrgId", strOrgId);
			

			pst = con.prepareStatement("select * from project_document_details where pro_document_id=?");
			pst.setInt(1, uF.parseToInt(getProFolderId()));
			rs = pst.executeQuery();
			Map<String, String> hmProDocumentDetails = new HashMap<String, String>();
			while (rs.next()) {
				hmProDocumentDetails.put("FOLDER_NAME", rs.getString("folder_name"));
				hmProDocumentDetails.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmProDocumentDetails.put("ALIGN_WITH", rs.getString("align_with"));
				hmProDocumentDetails.put("SHARING_TYPE", rs.getString("sharing_type"));
				hmProDocumentDetails.put("SHARING_RESOURCES", rs.getString("sharing_resources"));
				
				hmProDocumentDetails.put("CATEGORY", rs.getString("project_category"));
				hmProDocumentDetails.put("DESCRIPTION", rs.getString("description"));
				
				hmProDocumentDetails.put("EDIT_STATUS", uF.parseToBoolean(rs.getString("is_edit")) == true ? "checked" : "");
				hmProDocumentDetails.put("DELETE_STATUS", uF.parseToBoolean(rs.getString("is_delete")) == true ? "checked" : "");
				
				hmProDocumentDetails.put("EDIT_STATUS_VAL", uF.parseToBoolean(rs.getString("is_edit")) == true ? "1" : "0");
				hmProDocumentDetails.put("DELETE_STATUS_VAL", uF.parseToBoolean(rs.getString("is_delete")) == true ? "1" : "0");
				
				hmProDocumentDetails.put("SHARING_POC", rs.getString("sharing_poc"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProDocumentDetails", hmProDocumentDetails);
			
			
			String strProjectSelect = "";
			String strOtherSelect = "";
			if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 1){
				strProjectSelect = "selected";
				strOtherSelect = "";
			} else if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 2){
				strProjectSelect = "";
				strOtherSelect = "selected";
			}
			
			StringBuilder sbProCategoryTypeFolder = new StringBuilder("<option value=\"1\" "+strProjectSelect+">Project</option><option value=\"2\" "+strOtherSelect+">Category</option>");
			
			request.setAttribute("sbProCategoryTypeFolder", sbProCategoryTypeFolder.toString());
			
			
			StringBuilder sbProFolderTasks = new StringBuilder();
//			if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 1) {
				if(getFromPage() == null || !getFromPage().equalsIgnoreCase("MP")) {
					sbProFolderTasks.append("<option value=\"0\">Full Project</option>");
				}
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select task_id,activity_name,parent_task_id from activity_info where pro_id=? and task_id not in " +
				"(select parent_task_id from activity_info where pro_id=? and parent_task_id is not null) ");
				if(getFromPage() != null && getFromPage().equalsIgnoreCase("MP")) {
					sbQuery.append(" and task_id = "+uF.parseToInt(getTaskId())+"");
				}
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getProId()));
				pst.setInt(2, uF.parseToInt(getProId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(uF.parseToInt(rs.getString("parent_task_id")) > 0) {
						if(uF.parseToInt(hmProDocumentDetails.get("ALIGN_WITH")) == uF.parseToInt(rs.getString("task_id"))) {
							if(rs.getString("activity_name") != null && !rs.getString("activity_name").equals("")) {
								String activityName = rs.getString("activity_name").replace(".", ". ");
								activityName = activityName.replace(",", ", ");
								String strTemp[] = activityName.split(" ");
								StringBuilder sbTaskName = new StringBuilder();
								for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
									if(strTemp[i].trim().length()>0) {
										sbTaskName.append(strTemp[i].trim()+" ");
									}
								}
								sbProFolderTasks.append("<option value=\"" + rs.getString("task_id") + "\" selected>" + sbTaskName.toString().trim() + " [ST]"+"</option>");
							}
						} else {
							if(rs.getString("activity_name") != null && !rs.getString("activity_name").equals("")) {
								String activityName = rs.getString("activity_name").replace(".", ". ");
								activityName = activityName.replace(",", ", ");
								String strTemp[] = activityName.split(" ");
								StringBuilder sbTaskName = new StringBuilder();
								for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
									if(strTemp[i].trim().length()>0) {
										sbTaskName.append(strTemp[i].trim()+" ");
									}
								}
								sbProFolderTasks.append("<option value=\"" + rs.getString("task_id") + "\">" + sbTaskName.toString().trim() + " [ST]"+"</option>");
							}
						}
					} else {
						if(uF.parseToInt(hmProDocumentDetails.get("ALIGN_WITH")) == uF.parseToInt(rs.getString("task_id"))) {
							if(rs.getString("activity_name") != null && !rs.getString("activity_name").equals("")) {
								String activityName = rs.getString("activity_name").replace(".", ". ");
								activityName = activityName.replace(",", ", ");
								String strTemp[] = activityName.split(" ");
								StringBuilder sbTaskName = new StringBuilder();
								for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
									if(strTemp[i].trim().length()>0) {
										sbTaskName.append(strTemp[i].trim()+" ");
									}
								}
								sbProFolderTasks.append("<option value=\"" + rs.getString("task_id") + "\" selected>" + sbTaskName.toString().trim() +"</option>");
							}
						} else {
							if(rs.getString("activity_name") != null && !rs.getString("activity_name").equals("")) {
								String activityName = rs.getString("activity_name").replace(".", ". ");
								activityName = activityName.replace(",", ", ");
								String strTemp[] = activityName.split(" ");
								StringBuilder sbTaskName = new StringBuilder();
								for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
									if(strTemp[i].trim().length()>0) {
										sbTaskName.append(strTemp[i].trim()+" ");
									}
								}
								sbProFolderTasks.append("<option value=\"" + rs.getString("task_id") + "\">" + sbTaskName.toString().trim() +"</option>");
							}
						}
					}
				}
				rs.close();
				pst.close();
				request.setAttribute("sbProFolderTasks", sbProFolderTasks.toString());
//			} else if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 2) {
				
				StringBuilder sbProFolderCategory = new StringBuilder();
				pst = con.prepareStatement("select project_category_id,project_category from project_category_details where project_category_id>1");
				rs = pst.executeQuery();
				while (rs.next()) {
					if(uF.parseToInt(hmProDocumentDetails.get("ALIGN_WITH")) == uF.parseToInt(rs.getString("project_category_id"))) {
						sbProFolderCategory.append("<option value=\"" + rs.getString("project_category_id") + "\" selected>" + rs.getString("project_category") + "</option>");
					} else {
						sbProFolderCategory.append("<option value=\"" + rs.getString("project_category_id") + "\">" + rs.getString("project_category") + "</option>");
					}
				}
				rs.close();
				pst.close();
//			}
//			sbProFolderTasks.append("</select>");
			request.setAttribute("sbProFolderCategory", sbProFolderCategory.toString());
			
			
			StringBuilder sbProFolderEmp = new StringBuilder("<select name=\"proFolderEmployee\" id=\"proFolderEmployee0\" style=\"width:100px !important;\" multiple size=\"3\">");
			pst = con.prepareStatement("select * from project_emp_details ped, employee_personal_details epd where ped.emp_id = epd.emp_per_id and pro_id=?");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> alResources = new ArrayList<String>();
				if(hmProDocumentDetails.get("SHARING_RESOURCES") != null) {
					alResources = Arrays.asList(hmProDocumentDetails.get("SHARING_RESOURCES").split(","));
				}
				boolean flag = false;
					for(int a=0; alResources != null && a<alResources.size(); a++) {
						if(rs.getString("emp_id").equals(alResources.get(a))) {
							
							String strEmpMName = "";
							if(flagMiddleName) {
								if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
									strEmpMName = " "+rs.getString("emp_mname");
								}
							}
							
							sbProFolderEmp.append("<option value=\"" + rs.getString("emp_id") + "\" selected>" + rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname") + "</option>");
							flag = true;
						}
					}
					if(!flag) {
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						sbProFolderEmp.append("<option value=\"" + rs.getString("emp_id") + "\">" + rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname") + "</option>");
					}
			}
			rs.close();
			pst.close();
			sbProFolderEmp.append("</select>");
			request.setAttribute("sbProFolderEmp", sbProFolderEmp.toString());
			
			StringBuilder sbProSharingType = new StringBuilder("<select name=\"proFolderSharingType\" id=\"proFolderSharingType0\" style=\"width:100px !important;\"");
			if(getFromPage() != null && getFromPage().equals("VAP")) {
				sbProSharingType.append("onchange=\"showHideResources('"+getProId()+"', this.value, '1')\">");
			} else if(getFromPage() != null && getFromPage().equals("MP")) {
				sbProSharingType.append("onchange=\"showHideResources('"+getProId()+"', '"+getTaskId()+"', this.value, '1')\">");
			} else {
				sbProSharingType.append("onchange=\"showHideResources(this.value, '1')\">");
			}
			sbProSharingType.append("<option value=\"0\"");
			if(hmProDocumentDetails.get("SHARING_TYPE") != null && hmProDocumentDetails.get("SHARING_TYPE").equals("0")){ 
				sbProSharingType.append(" selected"); 
			}
			sbProSharingType.append(">Public</option>");
			sbProSharingType.append("<option value=\"1\"");
			if(hmProDocumentDetails.get("SHARING_TYPE") != null && hmProDocumentDetails.get("SHARING_TYPE").equals("1")){ 
				sbProSharingType.append(" selected"); 
			}
			sbProSharingType.append(">Private Team</option>");
			sbProSharingType.append("<option value=\"2\"");
			if(hmProDocumentDetails.get("SHARING_TYPE") != null && hmProDocumentDetails.get("SHARING_TYPE").equals("2")){ 
				sbProSharingType.append(" selected"); 
			}
			sbProSharingType.append(">Individual Resource</option>");
			sbProSharingType.append("</select>");
			request.setAttribute("sbProSharingType", sbProSharingType.toString());
			
			
			StringBuilder sbProTasks = new StringBuilder("<select name=\"proTasks\" id=\"proTasks\" style=\"width:140px !important;\"><option value=\"0\">Full Project</option>");
//			pst = con.prepareStatement("select task_id,activity_name,parent_task_id from activity_info where pro_id=?");
			pst = con.prepareStatement("select task_id,activity_name,parent_task_id from activity_info where pro_id=? and task_id not in " +
			"(select parent_task_id from activity_info where pro_id=? and parent_task_id is not null)");
			pst.setInt(1, uF.parseToInt(getProId()));
			pst.setInt(2, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				if(uF.parseToInt(rs.getString("parent_task_id")) > 0) {
					if(rs.getString("activity_name") != null && !rs.getString("activity_name").equals("")) {
						String activityName = rs.getString("activity_name").replace(".", ". ");
						activityName = activityName.replace(",", ", ");
						String strTemp[] = activityName.split(" ");
						StringBuilder sbTaskName = new StringBuilder();
						for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
							if(strTemp[i].trim().length()>0) {
								sbTaskName.append(strTemp[i].trim()+" ");
							}
						}
						sbProTasks.append("<option value=\"" + rs.getString("task_id") + "\">" + sbTaskName.toString().trim() + " [ST]"+"</option>");
					}
				} else {
					if(rs.getString("activity_name") != null && !rs.getString("activity_name").equals("")) {
						String activityName = rs.getString("activity_name").replace(".", ". ");
						activityName = activityName.replace(",", ", ");
						String strTemp[] = activityName.split(" ");
						StringBuilder sbTaskName = new StringBuilder();
						for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
							if(strTemp[i].trim().length()>0) {
								sbTaskName.append(strTemp[i].trim()+" ");
							}
						}
						sbProTasks.append("<option value=\"" + rs.getString("task_id") + "\">" + sbTaskName.toString().trim() +"</option>");
					}
				}
			}
			rs.close();
			pst.close();
			sbProTasks.append("</select>");
			request.setAttribute("sbProTasks", sbProTasks.toString());
			
			
			StringBuilder sbProEmp = new StringBuilder("<select name=\"proEmployee\" id=\"proEmployee\" style=\"width:160px !important\" multiple size=\"3\">");
			pst = con.prepareStatement("select * from project_emp_details ped, employee_personal_details epd where ped.emp_id = epd.emp_per_id and pro_id=?");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				sbProEmp.append("<option value=\"" + rs.getString("emp_id") + "\">" + rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + "</option>");
			}
			rs.close();
			pst.close();
			sbProEmp.append("</select>");
			request.setAttribute("sbProEmp", sbProEmp.toString());
			
			StringBuilder sbProCategory = new StringBuilder("<select name=\"proTasks\" id=\"proTasks\" style=\"width:100px !important;\">");
			pst = con.prepareStatement("select * from project_category_details where org_id=? and project_category_id>1 order by project_category");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbProCategory.append("<option value=\"" + rs.getString("project_category_id") + "\">" + rs.getString("project_category").trim()+ "</option>");
			}
			rs.close();
			pst.close();
			sbProEmp.append("</select>");
			request.setAttribute("sbProCategory", sbProCategory.toString());
			
			List<String> existPocList = new ArrayList<String>();
			if(hmProDocumentDetails.get("SHARING_POC") != null && !hmProDocumentDetails.get("SHARING_POC").trim().equals("")) {
				existPocList = Arrays.asList(hmProDocumentDetails.get("SHARING_POC").split(","));
			}
			StringBuilder sbOrgSPOC = new StringBuilder();
			pst = con.prepareStatement("select poc_id, contact_fname, contact_lname from client_poc where poc_id in (select poc from projectmntnc " +
					"where org_id=? and client_id in (select client_id from projectmntnc where pro_id=?)) order by contact_fname,contact_lname");
			pst.setInt(1, uF.parseToInt(strOrgId));
			pst.setInt(2, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			boolean isSpoc = false;
			while (rs.next()) {
				String strSeletcted = "";
				if(existPocList.contains(rs.getString("poc_id"))){
					strSeletcted = "selected";
					isSpoc = true;
				}
				sbOrgSPOC.append("<option value='" + rs.getString("poc_id") + "' "+strSeletcted+">" + uF.showData(rs.getString("contact_fname"), "").trim()+" "+uF.showData(rs.getString("contact_lname"), "").trim()+ "</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbOrgSPOC", sbOrgSPOC.toString());
			request.setAttribute("isSpoc", isSpoc);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void updateFolderSetting(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			List<String> alEmployee = null;
			if(getProFolderEmployee() != null) {
				alEmployee = Arrays.asList(getProFolderEmployee());
			}
			StringBuilder sbEmps = null;
			
			for(int a=0; alEmployee != null && a<alEmployee.size(); a++) {
				if(alEmployee.get(a) != null && !alEmployee.get(a).trim().equals("")) {
					if(sbEmps == null) {
						sbEmps = new StringBuilder();
						sbEmps.append(","+ alEmployee.get(a).trim() +",");
					} else {
						sbEmps.append(alEmployee.get(a).trim() +",");
					}
				}
			}
			if(sbEmps == null) {
				sbEmps = new StringBuilder();
			}
			
			List<String> alPoc = null;
			if(getProFolderPoc() != null) {
				alPoc = Arrays.asList(getProFolderPoc());
			}
			StringBuilder sbPoc = null;
			
			for(int a=0; alPoc != null && a<alPoc.size(); a++) {
				if(alPoc.get(a) != null && !alPoc.get(a).trim().equals("")) {
					if(sbPoc == null) {
						sbPoc = new StringBuilder();
						sbPoc.append(","+ alPoc.get(a).trim() +",");
					} else {
						sbPoc.append(alPoc.get(a).trim() +",");
					}
				}
			}
			if(sbPoc == null) {
				sbPoc = new StringBuilder();
			}
			
			pst = con.prepareStatement("update project_document_details set align_with=?,sharing_type=?,sharing_resources=?,project_category=?," +
				"description=?,is_edit=?,is_delete=?,is_cust_add=?,sharing_poc=? where pro_document_id=?");
			if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
				pst.setInt(1, uF.parseToInt(getProFolderTask()));
			} else {
				pst.setInt(1, uF.parseToInt(getProFolderCategory()));
			}
			pst.setInt(2, uF.parseToInt(getProFolderSharingType()));
			pst.setString(3, sbEmps.toString());
			pst.setInt(4, uF.parseToInt(getProCategoryTypeFolder()));
			pst.setString(5, getStrFolderDescription());
			pst.setBoolean(6, uF.parseToBoolean(getIsFolderEdit()));
			pst.setBoolean(7, uF.parseToBoolean(getIsFolderDelete()));
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				pst.setBoolean(8, true);
			} else {
				pst.setBoolean(8, false);
			}
			pst.setString(9, sbPoc.toString());
			pst.setInt(10, uF.parseToInt(getProFolderId()));
			System.out.println("pst====>"+pst);
			pst.executeUpdate();
			pst.close();
			
			
			/**
			 * Alerts
			 * */
			List<String> alEmp= null;
			if(getProFolderEmployee() != null) {
				alEmp = Arrays.asList(getProFolderEmployee());
			}
			if(alEmp == null){
				alEmp = new ArrayList<String>();
			}
			
			List<String> alSharePoc = null;
			if(getProFolderPoc() != null) {
				alSharePoc = Arrays.asList(getProFolderPoc());
			}
			if(alSharePoc == null){
				alSharePoc = new ArrayList<String>();
			}
			
			String strDocumentName = "";
			pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
			pst.setInt(1, uF.parseToInt(getProFolderId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				strDocumentName = rs.getString("folder_name");
			}
			rs.close();
			pst.close();
			
			String proName1 = null;
			String strCategory = null;
			if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
				pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
				pst.setInt(1, uF.parseToInt(getProFolderTask()));
				rs = pst.executeQuery();
				
				while(rs.next()) {
					proName1 = rs.getString("pro_name");
				}
				rs.close();
				pst.close();
			} else {
				pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
				pst.setInt(1, uF.parseToInt(getProFolderCategory()));
				rs = pst.executeQuery();
				while(rs.next()) {
					strCategory = rs.getString("project_category");
				}
				rs.close();
				pst.close();
			}
			Notifications nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
			nF.setDomain(strDomain);
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			
			nF.request = request;
			nF.setStrOrgId((String)session.getAttribute(ORGID));
			nF.setEmailTemplate(true);
			
			if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
				nF.setStrProjectName(proName1);
			} else {
				nF.setStrCategoryName(strCategory);
			}
			nF.setStrDocumentName(strDocumentName);
			
			String alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
			String alertAction = "DocumentListView.action";
			for(String strEmp : alEmp){
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(strEmp.trim());
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
//				userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
				userAlerts.setStatus(INSERT_TR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
				
				//Mail
				nF.setStrEmpId(strEmp.trim());
				nF.sendNotifications();
			}
			for(String strEmp : alSharePoc){
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(strEmp.trim());
				userAlerts.setStrOther("other");
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
//				userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
				userAlerts.setStatus(INSERT_TR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
				
				//Mail
				pst = con.prepareStatement("select * from client_poc where poc_id = ?");
				pst.setInt(1, uF.parseToInt(strEmp.trim()));
				rs = pst.executeQuery();
				boolean flg=false;
				while(rs.next()) {
					nF.setStrCustFName(rs.getString("contact_fname"));
					nF.setStrCustLName(rs.getString("contact_lname"));
					nF.setStrEmpMobileNo(rs.getString("contact_number"));
					if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
						nF.setStrEmpEmail(rs.getString("contact_email"));
						nF.setStrEmailTo(rs.getString("contact_email"));
					}
					flg = true;
				}
				rs.close();
				pst.close();
				
				if(flg) {
					nF.setStrEmpId(strEmp.trim());
					nF.sendNotifications();
				}
			}
			/**
			 * Alerts End
			 * */
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void createFolderForDocs(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
//			System.out.println("getOperation() ===>> " + getOperation());
			
//			String orgName = CF.getStrOrgName();
			String mainPathWithOrg = CF.getProjectDocumentFolder()+"/"+strOrgId;
			
			String mainPath = mainPathWithOrg+"/Projects";
			
			setPrjectname(CF.getProjectNameById(con, getProId()));
			
			String proNameFolder = mainPath +"/"+getProId();
			String proFolderNameFolder = "";
			if(getType()!=null && getType().equals("SF")) {
				pst = con.prepareStatement("select * from project_document_details where pro_document_id=(select pro_folder_id from project_document_details where pro_document_id=?)");
				pst.setInt(1, uF.parseToInt(getProFolderId()));
				rs = pst.executeQuery();
				String strMainFolder = null;
				while (rs.next()) {
					strMainFolder = rs.getString("folder_name");
				}
				rs.close();
				pst.close();
				if(strMainFolder!=null){
					proFolderNameFolder = proNameFolder+"/"+ strMainFolder +"/"+ getFolderName();
				}
			} else {
				proFolderNameFolder = proNameFolder +"/"+ getFolderName();
			}
			
			if(getType()!=null && getType().equals("F")) {
				
				String[] folderDocsTRId = request.getParameterValues("folderDocsTRId1");
				File[] strFolderDoc = mpRequest.getFiles("strFolderDoc");    //  
				String[] strFolderDocFileName = mpRequest.getFileNames("strFolderDoc"); 
				
//				String[] proFolderDocTasks = request.getParameterValues("proFolderDocTasks1");
				String[] folderDocDharingType = request.getParameterValues("folderDocDharingType1");
				String[] proCategoryTypeFolderDoc = request.getParameterValues("proCategoryTypeFolderDoc1");
				String[] strFolderDocDescription = request.getParameterValues("strFolderDocDescription1");
				String[] strFolderScopeDoc = request.getParameterValues("strFolderScopeDoc1");
				
				String[] isFolderDocEdit = request.getParameterValues("isFolderDocEdit1");
				String[] isFolderDocDelete = request.getParameterValues("isFolderDocDelete1");
				
				for(int j=0; folderDocsTRId != null && j<folderDocsTRId.length; j++) {
				
					String proFolderDocTasks = request.getParameter("proFolderDocTasks1_"+folderDocsTRId[j]);
					String proFolderDocCategory = request.getParameter("proFolderDocCategory1_"+folderDocsTRId[j]);
//					System.out.println("folderDocsTRId[j] ===>> " + folderDocsTRId[j]);
//					System.out.println("getFolderTRId()[i] ===>> " + getFolderTRId()[i]);
					double lengthBytes =  strFolderDoc[j].length();
					boolean isFileExist = false;
					String extenstion=FilenameUtils.getExtension(strFolderDocFileName[j]);	
					String strFileName = FilenameUtils.getBaseName(strFolderDocFileName[j]);
					strFileName = strFileName+"v1."+extenstion;
					File f = new File(proFolderNameFolder+"/"+strFileName);
					if(f.isFile()) {
					    System.out.println("isFile");
					    if(f.exists()) {
							isFileExist = true;
						    System.out.println("exists");
						} else {
						    System.out.println("exists fail");
						}   
					} else {
					    System.out.println("isFile fail");
					}
//					if(f.isFile() && f.exists()){
//						isFileExist = true;
//					    System.out.println("success");
//					}
//					else{
//					    System.out.println("fail");
//					}
					if(lengthBytes > 0 && !isFileExist) {
						String[] proFolderDocEmployee = request.getParameterValues("proFolderDocEmployee1_"+folderDocsTRId[j]);
						List<String> alFDEmployee = null;
						if(proFolderDocEmployee != null) {
							alFDEmployee = Arrays.asList(proFolderDocEmployee);
						}
						StringBuilder sbFDEmps = null;
						
						for(int a=0; alFDEmployee != null && a<alFDEmployee.size(); a++) {
							if(alFDEmployee.get(a) != null && !alFDEmployee.get(a).trim().equals("")) {
								if(sbFDEmps == null) {
									sbFDEmps = new StringBuilder();
									sbFDEmps.append(","+ alFDEmployee.get(a).trim() +",");
								} else {
									sbFDEmps.append(alFDEmployee.get(a).trim() +",");
								}
							}
						}
						if(sbFDEmps == null) {
							sbFDEmps = new StringBuilder();
						}
						
						String[] proFolderDocPoc = request.getParameterValues("proFolderDocPoc1_"+folderDocsTRId[j]);
						List<String> alFDPoc = null;
						if(proFolderDocPoc != null) {
							alFDPoc = Arrays.asList(proFolderDocPoc);
						}
						StringBuilder sbFDPoc = null;
						
						for(int a=0; alFDPoc != null && a<alFDPoc.size(); a++) {
							if(alFDPoc.get(a) != null && !alFDPoc.get(a).trim().equals("")) {
								if(sbFDPoc == null) {
									sbFDPoc = new StringBuilder();
									sbFDPoc.append(","+ alFDPoc.get(a).trim() +",");
								} else {
									sbFDPoc.append(alFDPoc.get(a).trim() +",");
								}
							}
						}
						if(sbFDPoc == null) {
							sbFDPoc = new StringBuilder();
						}
						
						
						pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
						"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
						pst.setString(1, strFolderDocDescription[j]);
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(2, 1);
						} else {
							pst.setInt(2, 0);
						}
//						pst.setInt(2, uF.parseToInt(getStrAlignWith()));
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(3, uF.parseToInt(getProId()));
						} else {
							pst.setInt(3, uF.parseToInt(proFolderDocCategory));
						}
//						pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
						pst.setString(4, sbFDEmps.toString());
						pst.setString(5, "");
						pst.setString(6, "");
						pst.setInt(7, uF.parseToInt(folderDocSharingType[j]));
						pst.setString(8, sbFDEmps.toString());
						pst.setInt(9, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(11, strFileName);
						pst.executeUpdate();
						pst.close();
						
						String feedId = null;
						pst = con.prepareStatement("select max(communication_id) as communication_id from communication_1");
						rs = pst.executeQuery();
						while(rs.next()) {
							feedId = rs.getString("communication_id");
						}
						rs.close();
						pst.close();
						
						pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, " +
								"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document," +
								"description,doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc,feed_id)" +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getClientId()));
						pst.setInt(2, uF.parseToInt(getProId()));
						pst.setString(3, getFolderName());
						pst.setInt(4, uF.parseToInt(strSessionEmpId));
//						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(6, "folder");
						pst.setInt(7, uF.parseToInt(getProFolderId()));
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(8, uF.parseToInt(proFolderDocTasks));
						} else {
							pst.setInt(8, uF.parseToInt(proFolderDocCategory));
						}
						pst.setInt(9, uF.parseToInt(folderDocDharingType[j]));
						pst.setString(10, sbFDEmps.toString());
						pst.setInt(11, uF.parseToInt(proCategoryTypeFolderDoc[j]));
						pst.setString(12, strFolderScopeDoc[j]);
						pst.setString(13, strFolderDocDescription[j]);
						pst.setInt(14, 0);
						pst.setInt(15, 1);
						pst.setBoolean(16, uF.parseToBoolean(isFolderDocEdit[j]));
						pst.setBoolean(17, uF.parseToBoolean(isFolderDocDelete[j]));
						if(strUserType != null && strUserType.equals(CUSTOMER)) {
							pst.setBoolean(18, true);
						} else {
							pst.setBoolean(18, false);
						}
						pst.setString(19, sbFDPoc.toString());
						pst.setInt(20, uF.parseToInt(feedId));
	//					System.out.println("pst====>"+pst);
						pst.execute();
						pst.close();
						
						
						String proDocumentId = "";
						pst = con.prepareStatement("select max(pro_document_id) as pro_document_id from project_document_details");
						rs = pst.executeQuery();
						while (rs.next()) {
							proDocumentId = rs.getString("pro_document_id");
						}
						rs.close();
						pst.close();
						
						
						uploadProjectFolderDocuments(con, getProId(), strFolderDoc[j], strFileName, proFolderNameFolder, proDocumentId);
						
						/**
						 * Alerts
						 * */
						List<String> alEmp= null;
						if(proFolderDocEmployee != null) {
							alEmp = Arrays.asList(proFolderDocEmployee);
						}
						if(alEmp == null){
							alEmp = new ArrayList<String>();
						}
						
						List<String> alSharePoc = null;
						if(proFolderDocPoc != null) {
							alSharePoc = Arrays.asList(proFolderDocPoc);
						}
						if(alSharePoc == null){
							alSharePoc = new ArrayList<String>();
						}
						
						String strDocumentName = "";
						pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
						pst.setInt(1, uF.parseToInt(proDocumentId));
						rs = pst.executeQuery();
						while (rs.next()) {
							strDocumentName = rs.getString("folder_name");
						}
						rs.close();
						pst.close();
						
						String proName1 = null;
						String strCategory = null;
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
							pst.setInt(1, uF.parseToInt(proFolderDocTasks));
							rs = pst.executeQuery();
							
							while(rs.next()) {
								proName1 = rs.getString("pro_name");
							}
							rs.close();
							pst.close();
						} else {
							pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
							pst.setInt(1, uF.parseToInt(proFolderDocCategory));
							rs = pst.executeQuery();
							while(rs.next()) {
								strCategory = rs.getString("project_category");
							}
							rs.close();
							pst.close();
						}
						Notifications nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
						nF.setDomain(strDomain);
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						
						nF.request = request;
						nF.setStrOrgId((String)session.getAttribute(ORGID));
						nF.setEmailTemplate(true);
						
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							nF.setStrProjectName(proName1);
						} else {
							nF.setStrCategoryName(strCategory);
						}
						nF.setStrDocumentName(strDocumentName);
						
						String alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						String alertAction = "DocumentListView.action";
						for(String strEmp : alEmp){
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							nF.setStrEmpId(strEmp.trim());
							nF.sendNotifications();
						}
						for(String strEmp : alSharePoc){
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrOther("other");
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							pst = con.prepareStatement("select * from client_poc where poc_id = ?");
							pst.setInt(1, uF.parseToInt(strEmp.trim()));
							rs = pst.executeQuery();
							boolean flg=false;
							while(rs.next()) {
								nF.setStrCustFName(rs.getString("contact_fname"));
								nF.setStrCustLName(rs.getString("contact_lname"));
								nF.setStrEmpMobileNo(rs.getString("contact_number"));
								if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
									nF.setStrEmpEmail(rs.getString("contact_email"));
									nF.setStrEmailTo(rs.getString("contact_email"));
								}
								flg = true;
							}
							rs.close();
							pst.close();
							
							if(flg) {
								nF.setStrEmpId(strEmp.trim());
								nF.sendNotifications();
							}
						}
						/**
						 * Alerts End
						 * */
					}
				}
				
				String[] SubFolderTR = request.getParameterValues("SubFolderTR1");
				String[] strSubFolderName = request.getParameterValues("strSubFolderName1");
				String[] proCategoryTypeSubFolder = request.getParameterValues("proCategoryTypeSubFolder1");
//				String[] proSubFolderTasks = request.getParameterValues("proSubFolderTasks1");
				String[] SubfolderSharingType = request.getParameterValues("SubfolderSharingType1");
				String[] strSubFolderDescription = request.getParameterValues("strSubFolderDescription1");
				
				String[] isSubFolderEdit = request.getParameterValues("isSubFolderEdit1");
				String[] isSubFolderDelete = request.getParameterValues("isSubFolderDelete1");
				
				for(int j = 0; SubFolderTR != null && j < SubFolderTR.length; j++) {
					
					String proSubFolderTasks = request.getParameter("proSubFolderTasks1_"+SubFolderTR[j]);
					String proSubFolderCategory = request.getParameter("proSubFolderCategory1_"+SubFolderTR[j]);
					
					String[] proSubFolderEmployee = request.getParameterValues("proSubFolderEmployee1_"+SubFolderTR[j]);
					List<String> alSubEmployee = null;
					if(proSubFolderEmployee != null) {
						alSubEmployee = Arrays.asList(proSubFolderEmployee);
					}
					StringBuilder sbSubEmps = null;
					for(int a=0; alSubEmployee != null && a<alSubEmployee.size(); a++) {
						if(alSubEmployee.get(a) != null && !alSubEmployee.get(a).trim().equals("")) {
							if(sbSubEmps == null) {
								sbSubEmps = new StringBuilder();
								sbSubEmps.append(","+ alSubEmployee.get(a).trim() +",");
							} else {
								sbSubEmps.append(alSubEmployee.get(a).trim() +",");
							}
						}
					}
					if(sbSubEmps == null) {
						sbSubEmps = new StringBuilder();
					}
					
					String[] proSubFolderPoc = request.getParameterValues("proSubFolderPoc1_"+SubFolderTR[j]);
					List<String> alSubPoc = null;
					if(proSubFolderPoc != null) {
						alSubPoc = Arrays.asList(proSubFolderPoc);
					}
					StringBuilder sbSubPoc = null;
					for(int a=0; alSubPoc != null && a<alSubPoc.size(); a++) {
						if(alSubPoc.get(a) != null && !alSubPoc.get(a).trim().equals("")) {
							if(sbSubPoc == null) {
								sbSubPoc = new StringBuilder();
								sbSubPoc.append(","+ alSubPoc.get(a).trim() +",");
							} else {
								sbSubPoc.append(alSubPoc.get(a).trim() +",");
							}
						}
					}
					if(sbSubPoc == null) {
						sbSubPoc = new StringBuilder();
					}
					
					String proSubFolderNameFolder = proFolderNameFolder +"/"+ strSubFolderName[j];
					
					File file3 = new File(proSubFolderNameFolder);
					if (!file3.exists()) {
						if (file3.mkdir()) {
							System.out.println("Directory is created!");
						}
					}
					
					pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, " +
							"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document," +
							"description,doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc)" +
							"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(getClientId()));
					pst.setInt(2, uF.parseToInt(getProId()));
					pst.setString(3, strSubFolderName[j]);
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
//					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setString(6, "folder");
					pst.setInt(7, uF.parseToInt(getProFolderId()));
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						pst.setInt(8, uF.parseToInt(proSubFolderTasks));
					} else {
						pst.setInt(8, uF.parseToInt(proSubFolderCategory));
					}
					pst.setInt(9, uF.parseToInt(SubfolderSharingType[j]));
					pst.setString(10, sbSubEmps.toString());
					pst.setInt(11, uF.parseToInt(proCategoryTypeSubFolder[j]));
					pst.setString(12, null);
					pst.setString(13, strSubFolderDescription[j]);
					pst.setInt(14, 0);
					pst.setInt(15, 0);
					pst.setBoolean(16, uF.parseToBoolean(isSubFolderEdit[j]));
					pst.setBoolean(17, uF.parseToBoolean(isSubFolderDelete[j]));
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						pst.setBoolean(18, true);
					} else {
						pst.setBoolean(18, false);
					}
					pst.setString(19, sbSubPoc.toString());
			//		System.out.println("pst====>"+pst);
					pst.execute();
					pst.close();
			
					String proSubFolderId = "";
					pst = con.prepareStatement("select max(pro_document_id)as pro_document_id from project_document_details");
					rs = pst.executeQuery();
					while (rs.next()) {
						proSubFolderId = rs.getString("pro_document_id");
					}
					rs.close();
					pst.close();
					
					
					/**
					 * Alerts
					 * */
					List<String> alEmp= null;
					if(proSubFolderEmployee != null) {
						alEmp = Arrays.asList(proSubFolderEmployee);
					}
					if(alEmp == null){
						alEmp = new ArrayList<String>();
					}
					
					List<String> alSharePoc = null;
					if(proSubFolderPoc != null) {
						alSharePoc = Arrays.asList(proSubFolderPoc);
					}
					if(alSharePoc == null){
						alSharePoc = new ArrayList<String>();
					}
					
					String strDocumentName = "";
					pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
					pst.setInt(1, uF.parseToInt(proSubFolderId));
					rs = pst.executeQuery();
					while (rs.next()) {
						strDocumentName = rs.getString("folder_name");
					}
					rs.close();
					pst.close();
					
					String proName1 = null;
					String strCategory = null;
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
						pst.setInt(1, uF.parseToInt(proSubFolderTasks));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							proName1 = rs.getString("pro_name");
						}
						rs.close();
						pst.close();
					} else {
						pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
						pst.setInt(1, uF.parseToInt(proSubFolderCategory));
						rs = pst.executeQuery();
						while(rs.next()) {
							strCategory = rs.getString("project_category");
						}
						rs.close();
						pst.close();
					}
					Notifications nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
					nF.setDomain(strDomain);
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					
					nF.request = request;
					nF.setStrOrgId((String)session.getAttribute(ORGID));
					nF.setEmailTemplate(true);
					
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						nF.setStrProjectName(proName1);
					} else {
						nF.setStrCategoryName(strCategory);
					}
					nF.setStrDocumentName(strDocumentName);
					
					String alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					String alertAction = "DocumentListView.action";
					for(String strEmp : alEmp){
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						//Mail
						nF.setStrEmpId(strEmp.trim());
						nF.sendNotifications();
					}
					for(String strEmp : alSharePoc){
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrOther("other");
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						//Mail
						pst = con.prepareStatement("select * from client_poc where poc_id = ?");
						pst.setInt(1, uF.parseToInt(strEmp.trim()));
						rs = pst.executeQuery();
						boolean flg=false;
						while(rs.next()) {
							nF.setStrCustFName(rs.getString("contact_fname"));
							nF.setStrCustLName(rs.getString("contact_lname"));
							nF.setStrEmpMobileNo(rs.getString("contact_number"));
							if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
								nF.setStrEmpEmail(rs.getString("contact_email"));
								nF.setStrEmailTo(rs.getString("contact_email"));
							}
							flg = true;
						}
						rs.close();
						pst.close();
						
						if(flg) {
							nF.setStrEmpId(strEmp.trim());
							nF.sendNotifications();
						}
					}
					/**
					 * Alerts End
					 * */
					
					
//					System.out.println("SubFolderTR[j] ====>> " + SubFolderTR[j]);
					String[] SubfolderDocsTRId = request.getParameterValues("SubfolderDocsTRId"+SubFolderTR[j]);
//					System.out.println("SubfolderDocsTRId ====>> " + SubfolderDocsTRId.length);
					File[] files = mpRequest.getFiles("strSubFolderDoc"+SubFolderTR[j]);    //  
					String[] fileNames = mpRequest.getFileNames("strSubFolderDoc"+SubFolderTR[j]); 
					String[] proCategoryTypeSubFolderDoc = request.getParameterValues("proCategoryTypeSubFolderDoc" +SubFolderTR[j]);
//					String[] proSubFolderDocTasks = request.getParameterValues("proSubFolderDocTasks" +SubFolderTR[j]);
					String[] SubfolderDocDharingType = request.getParameterValues("SubfolderDocDharingType" + SubFolderTR[j]);
					String[] strSubFolderDocDescription = request.getParameterValues("strSubFolderDocDescription" +SubFolderTR[j]);
					String[] strSubFolderScopeDoc = request.getParameterValues("strSubFolderScopeDoc" + SubFolderTR[j]);
					
					String[] isSubFolderDocEdit = request.getParameterValues("isSubFolderDocEdit"+SubFolderTR[j]);
					String[] isSubFolderDocDelete = request.getParameterValues("isSubFolderDocDelete"+SubFolderTR[j]);
					
					for(int k=0; SubfolderDocsTRId != null && k < SubfolderDocsTRId.length; k++) {
						
						String proSubFolderDocTasks = request.getParameter("proSubFolderDocTasks" +SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
						String proSubFolderDocCategory = request.getParameter("proSubFolderDocCategory" +SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
						
						double lengthBytes =  files[k].length();
						boolean isFileExist = false;
//						System.out.println("fileNames[k] ===>> " + fileNames[k] + " ----- " + proSubFolderNameFolder+"/"+fileNames[k]);
						String extenstion=FilenameUtils.getExtension(fileNames[k]);	
						String strFileName = FilenameUtils.getBaseName(fileNames[k]);
						strFileName = strFileName+"v1."+extenstion;
						File f = new File(proSubFolderNameFolder+"/"+strFileName);
						if(f.isFile()) {
						    System.out.println("isFile");
						    if(f.exists()) {
								isFileExist = true;
							    System.out.println("exists");
							} else {
							    System.out.println("exists fail");
							}   
						} else {
						    System.out.println("isFile fail");
						}
						
						if(lengthBytes > 0 && !isFileExist) {
							String[] proFolderDocEmployee = request.getParameterValues("proSubFolderDocEmployee" + SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
							List<String> alFDEmployee = null;
							if(proFolderDocEmployee != null) {
								alFDEmployee = Arrays.asList(proFolderDocEmployee);
							}
							StringBuilder sbFDEmps = null;
							
							for(int a=0; alFDEmployee != null && a<alFDEmployee.size(); a++) {
								if(alFDEmployee.get(a) != null && !alFDEmployee.get(a).trim().equals("")) {
									if(sbFDEmps == null) {
										sbFDEmps = new StringBuilder();
										sbFDEmps.append(","+ alFDEmployee.get(a).trim() +",");
									} else {
										sbFDEmps.append(alFDEmployee.get(a).trim() +",");
									}
								}
							}
							if(sbFDEmps == null) {
								sbFDEmps = new StringBuilder();
							}
							
							String[] proFolderDocPoc = request.getParameterValues("proSubFolderDocPoc" + SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
							List<String> alFDPoc = null;
							if(proFolderDocPoc != null) {
								alFDPoc = Arrays.asList(proFolderDocPoc);
							}
							StringBuilder sbFDPoc = null;
							
							for(int a=0; alFDPoc != null && a<alFDPoc.size(); a++) {
								if(alFDPoc.get(a) != null && !alFDPoc.get(a).trim().equals("")) {
									if(sbFDPoc == null) {
										sbFDPoc = new StringBuilder();
										sbFDPoc.append(","+ alFDPoc.get(a).trim() +",");
									} else {
										sbFDPoc.append(alFDPoc.get(a).trim() +",");
									}
								}
							}
							if(sbFDPoc == null) {
								sbFDPoc = new StringBuilder();
							}
							
							
							pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
							"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
							pst.setString(1, strSubFolderDocDescription[k]);
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(2, 1);
							} else {
								pst.setInt(2, 0);
							}
//							pst.setInt(2, uF.parseToInt(getStrAlignWith()));
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(3, uF.parseToInt(getProId()));
							} else {
								pst.setInt(3, uF.parseToInt(proSubFolderDocCategory));
							}
//							pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
							pst.setString(4, sbFDEmps.toString());
							pst.setString(5, "");
							pst.setString(6, "");
							pst.setInt(7, uF.parseToInt(SubfolderDocDharingType[k]));
							pst.setString(8, sbFDEmps.toString());
							pst.setInt(9, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
							pst.setString(11, strFileName);
							pst.executeUpdate();
							pst.close();
							
							String feedId = null;
							pst = con.prepareStatement("select max(communication_id) as communication_id from communication_1");
							rs = pst.executeQuery();
							while(rs.next()) {
								feedId = rs.getString("communication_id");
							}
							rs.close();
							pst.close();
							
							pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, " +
									"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document," +
									"description,doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc,feed_id)" +
									"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
							pst.setInt(1, uF.parseToInt(getClientId()));
							pst.setInt(2, uF.parseToInt(getProId()));
							pst.setString(3, strFileName);
							pst.setInt(4, uF.parseToInt(strSessionEmpId));
//							pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
							pst.setString(6, "folder");
							pst.setInt(7, uF.parseToInt(proSubFolderId));
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(8, uF.parseToInt(proSubFolderDocTasks));
							} else {
								pst.setInt(8, uF.parseToInt(proSubFolderDocCategory));
							}
							pst.setInt(9, uF.parseToInt(SubfolderDocDharingType[k]));
							pst.setString(10, sbFDEmps.toString());
							pst.setInt(11, uF.parseToInt(proCategoryTypeSubFolderDoc[k]));
							pst.setString(12, strSubFolderScopeDoc[k]);
							pst.setString(13, strSubFolderDocDescription[k]);
							pst.setInt(14, 0);
							pst.setInt(15, 1);
							pst.setBoolean(16, uF.parseToBoolean(isSubFolderDocEdit[k]));
							pst.setBoolean(17, uF.parseToBoolean(isSubFolderDocDelete[k]));
							if(strUserType != null && strUserType.equals(CUSTOMER)) {
								pst.setBoolean(18, true);
							} else {
								pst.setBoolean(18, false);
							}
							pst.setString(19, sbFDPoc.toString());
							pst.setInt(20, uF.parseToInt(feedId));
		//					System.out.println("pst====>"+pst);
							pst.execute();
							pst.close();
							
							String proDocumentId = "";
							pst = con.prepareStatement("select max(pro_document_id) as pro_document_id from project_document_details");
							rs = pst.executeQuery();
							while (rs.next()) {
								proDocumentId = rs.getString("pro_document_id");
							}
							rs.close();
							pst.close();
							
							uploadProjectFolderDocuments(con, getProId(), files[k], strFileName, proSubFolderNameFolder, proDocumentId);
							
							/**
							 * Alerts
							 * */
							List<String> alEmp1= null;
							if(proFolderDocEmployee != null) {
								alEmp1 = Arrays.asList(proFolderDocEmployee);
							}
							if(alEmp1 == null){
								alEmp1 = new ArrayList<String>();
							}
							
							List<String> alSharePoc1 = null;
							if(proFolderDocPoc != null) {
								alSharePoc1 = Arrays.asList(proFolderDocPoc);
							}
							if(alSharePoc1 == null){
								alSharePoc1 = new ArrayList<String>();
							}
							
							strDocumentName = "";
							pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
							pst.setInt(1, uF.parseToInt(proDocumentId));
							rs = pst.executeQuery();
							while (rs.next()) {
								strDocumentName = rs.getString("folder_name");
							}
							rs.close();
							pst.close();
							
							proName1 = null;
							strCategory = null;
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
								pst.setInt(1, uF.parseToInt(proSubFolderDocTasks));
								rs = pst.executeQuery();
								
								while(rs.next()) {
									proName1 = rs.getString("pro_name");
								}
								rs.close();
								pst.close();
							} else {
								pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
								pst.setInt(1, uF.parseToInt(proSubFolderDocCategory));
								rs = pst.executeQuery();
								while(rs.next()) {
									strCategory = rs.getString("project_category");
								}
								rs.close();
								pst.close();
							}
							nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
							nF.setDomain(strDomain);
							nF.setStrHostAddress(CF.getStrEmailLocalHost());
							nF.setStrHostPort(CF.getStrHostPort());
							nF.setStrContextPath(request.getContextPath());
							
							nF.request = request;
							nF.setStrOrgId((String)session.getAttribute(ORGID));
							nF.setEmailTemplate(true);
							
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								nF.setStrProjectName(proName1);
							} else {
								nF.setStrCategoryName(strCategory);
							}
							nF.setStrDocumentName(strDocumentName);
							
							alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
							alertAction = "DocumentListView.action";
							for(String strEmp : alEmp1){
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strEmp.trim());
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
//								userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
								userAlerts.setStatus(INSERT_TR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
								
								//Mail
								nF.setStrEmpId(strEmp.trim());
								nF.sendNotifications();
							}
							for(String strEmp : alSharePoc1){
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strEmp.trim());
								userAlerts.setStrOther("other");
								userAlerts.setStrData(alertData);
								userAlerts.setStrAction(alertAction);
//								userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
								userAlerts.setStatus(INSERT_TR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
								
								//Mail
								pst = con.prepareStatement("select * from client_poc where poc_id = ?");
								pst.setInt(1, uF.parseToInt(strEmp.trim()));
								rs = pst.executeQuery();
								boolean flg=false;
								while(rs.next()) {
									nF.setStrCustFName(rs.getString("contact_fname"));
									nF.setStrCustLName(rs.getString("contact_lname"));
									nF.setStrEmpMobileNo(rs.getString("contact_number"));
									if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
										nF.setStrEmpEmail(rs.getString("contact_email"));
										nF.setStrEmailTo(rs.getString("contact_email"));
									}
									flg = true;
								}
								rs.close();
								pst.close();
								
								if(flg) {
									nF.setStrEmpId(strEmp.trim());
									nF.sendNotifications();
								}
							}
							/**
							 * Alerts End
							 * */
						}
					}
				}
				
				int subFolderCnt = SubFolderTR!=null ? SubFolderTR.length : 0;
				String proName = CF.getProjectNameById(con, getProId());
				StringBuilder sbMsg = new StringBuilder();
				
				sbMsg.append(SUCCESSM+""+getFolderName()+" folder updated ");
				if(subFolderCnt>0) {
					sbMsg.append("and some documents added ");
				}
				sbMsg.append("successfully for "+proName+"."+END);
				session.setAttribute(MESSAGE, sbMsg.toString());
				
			} else if(getType()!=null && getType().equals("SF")) {
				
				String[] SubfolderDocsTRId = request.getParameterValues("SubfolderDocsTRId1");
//				System.out.println("SubfolderDocsTRId ====>> " + SubfolderDocsTRId.length);
				File[] files = mpRequest.getFiles("strSubFolderDoc1");    //  
				String[] fileNames = mpRequest.getFileNames("strSubFolderDoc1"); 
				String[] proCategoryTypeSubFolderDoc = request.getParameterValues("proCategoryTypeSubFolderDoc1");
//				String[] proSubFolderDocTasks = request.getParameterValues("proSubFolderDocTasks1");
				String[] SubfolderDocDharingType = request.getParameterValues("SubfolderDocDharingType1");
				String[] strSubFolderDocDescription = request.getParameterValues("strSubFolderDocDescription1");
				String[] strSubFolderScopeDoc = request.getParameterValues("strSubFolderScopeDoc1");
				
				String[] isSubFolderDocEdit = request.getParameterValues("isSubFolderDocEdit1");
				String[] isSubFolderDocDelete = request.getParameterValues("isSubFolderDocDelete1");

				for(int k=0; SubfolderDocsTRId != null && k < SubfolderDocsTRId.length; k++) {
					
					String proSubFolderDocTasks = request.getParameter("proSubFolderDocTasks1_"+SubfolderDocsTRId[k]);
					String proSubFolderDocCategory = request.getParameter("proSubFolderDocCategory1_"+SubfolderDocsTRId[k]);
					
					double lengthBytes =  files[k].length();
					boolean isFileExist = false;
//					System.out.println("fileNames[k] ===>> " + fileNames[k] + " ----- " + proSubFolderNameFolder+"/"+fileNames[k]);
					String extenstion=FilenameUtils.getExtension(fileNames[k]);	
					String strFileName = FilenameUtils.getBaseName(fileNames[k]);
					strFileName = strFileName+"v1."+extenstion;
					File f = new File(proFolderNameFolder+"/"+strFileName);
					if(f.isFile()) {
					    System.out.println("isFile");
					    if(f.exists()) {
							isFileExist = true;
						    System.out.println("exists");
						} else {
						    System.out.println("exists fail");
						}   
					} else {
					    System.out.println("isFile fail");
					}
					
					if(lengthBytes > 0 && !isFileExist) {
						String[] proFolderDocEmployee = request.getParameterValues("proSubFolderDocEmployee1_"+SubfolderDocsTRId[k]);
						List<String> alFDEmployee = null;
						if(proFolderDocEmployee != null) {
							alFDEmployee = Arrays.asList(proFolderDocEmployee);
						}
						StringBuilder sbFDEmps = null;
						
						for(int a=0; alFDEmployee != null && a<alFDEmployee.size(); a++) {
							if(alFDEmployee.get(a) != null && !alFDEmployee.get(a).trim().equals("")) {
								if(sbFDEmps == null) {
									sbFDEmps = new StringBuilder();
									sbFDEmps.append(","+ alFDEmployee.get(a).trim() +",");
								} else {
									sbFDEmps.append(alFDEmployee.get(a).trim() +",");
								}
							}
						}
						if(sbFDEmps == null) {
							sbFDEmps = new StringBuilder();
						}
						
						String[] proFolderDocPoc = request.getParameterValues("proSubFolderDocPoc1_"+SubfolderDocsTRId[k]);
						List<String> alFDPoc = null;
						if(proFolderDocPoc != null) {
							alFDPoc = Arrays.asList(proFolderDocPoc);
						}
						StringBuilder sbFDPoc = null;
						
						for(int a=0; alFDPoc != null && a<alFDPoc.size(); a++) {
							if(alFDPoc.get(a) != null && !alFDPoc.get(a).trim().equals("")) {
								if(sbFDPoc == null) {
									sbFDPoc = new StringBuilder();
									sbFDPoc.append(","+ alFDPoc.get(a).trim() +",");
								} else {
									sbFDPoc.append(alFDPoc.get(a).trim() +",");
								}
							}
						}
						if(sbFDPoc == null) {
							sbFDPoc = new StringBuilder();
						}
						
						
						pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
						"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
						pst.setString(1, strSubFolderDocDescription[k]);
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							pst.setInt(2, 1);
						} else {
							pst.setInt(2, 0);
						}
//						pst.setInt(2, uF.parseToInt(getStrAlignWith()));
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							pst.setInt(3, uF.parseToInt(getProId()));
						} else {
							pst.setInt(3, uF.parseToInt(proSubFolderDocCategory));
						}
//						pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
						pst.setString(4, sbFDEmps.toString());
						pst.setString(5, "");
						pst.setString(6, "");
						pst.setInt(7, uF.parseToInt(SubfolderDocDharingType[k]));
						pst.setString(8, sbFDEmps.toString());
						pst.setInt(9, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(11, strFileName);
						pst.executeUpdate();
						pst.close();
						
						String feedId = null;
						pst = con.prepareStatement("select max(communication_id) as communication_id from communication_1");
						rs = pst.executeQuery();
						while(rs.next()) {
							feedId = rs.getString("communication_id");
						}
						rs.close();
						pst.close();
						
						pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, " +
								"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document," +
								"description,doc_parent_id,doc_version,is_edit,is_delete,is_cust_add,sharing_poc,feed_id)" +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getClientId()));
						pst.setInt(2, uF.parseToInt(getProId()));
						pst.setString(3, strFileName);
						pst.setInt(4, uF.parseToInt(strSessionEmpId));
//						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(6, "folder");
						pst.setInt(7, uF.parseToInt(getProFolderId()));
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							pst.setInt(8, uF.parseToInt(proSubFolderDocTasks));
						} else {
							pst.setInt(8, uF.parseToInt(proSubFolderDocCategory));
						}
						pst.setInt(9, uF.parseToInt(SubfolderDocDharingType[k]));
						pst.setString(10, sbFDEmps.toString());
						pst.setInt(11, uF.parseToInt(proCategoryTypeSubFolderDoc[k]));
						pst.setString(12, strSubFolderScopeDoc[k]);
						pst.setString(13, strSubFolderDocDescription[k]);
						pst.setInt(14, 0);
						pst.setInt(15, 1);
						pst.setBoolean(16, uF.parseToBoolean(isSubFolderDocEdit[k]));
						pst.setBoolean(17, uF.parseToBoolean(isSubFolderDocDelete[k]));
						if(strUserType != null && strUserType.equals(CUSTOMER)) {
							pst.setBoolean(18, true);
						} else {
							pst.setBoolean(18, false);
						}
						pst.setString(19, sbFDPoc.toString());
						pst.setInt(20, uF.parseToInt(feedId));
	//					System.out.println("pst====>"+pst);
						pst.execute();
						pst.close();
						
						String proDocumentId = "";
						pst = con.prepareStatement("select max(pro_document_id) as pro_document_id from project_document_details");
						rs = pst.executeQuery();
						while(rs.next()) {
							proDocumentId = rs.getString("pro_document_id");
						}
						rs.close();
						pst.close();
						
						
						uploadProjectFolderDocuments(con, getProId(), files[k], strFileName, proFolderNameFolder, proDocumentId);
						
						/**
						 * Alerts
						 * */
						List<String> alEmp1= null;
						if(proFolderDocEmployee != null) {
							alEmp1 = Arrays.asList(proFolderDocEmployee);
						}
						if(alEmp1 == null) {
							alEmp1 = new ArrayList<String>();
						}
						
						List<String> alSharePoc1 = null;
						if(proFolderDocPoc != null) {
							alSharePoc1 = Arrays.asList(proFolderDocPoc);
						}
						if(alSharePoc1 == null) {
							alSharePoc1 = new ArrayList<String>();
						}
						
						String strDocumentName = "";
						pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
						pst.setInt(1, uF.parseToInt(proDocumentId));
						rs = pst.executeQuery();
						while (rs.next()) {
							strDocumentName = rs.getString("folder_name");
						}
						rs.close();
						pst.close();
						
						String proName1 = null;
						String strCategory = null;
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
							pst.setInt(1, uF.parseToInt(proSubFolderDocTasks));
							rs = pst.executeQuery();
							
							while(rs.next()) {
								proName1 = rs.getString("pro_name");
							}
							rs.close();
							pst.close();
						} else {
							pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
							pst.setInt(1, uF.parseToInt(proSubFolderDocCategory));
							rs = pst.executeQuery();
							while(rs.next()) {
								strCategory = rs.getString("project_category");
							}
							rs.close();
							pst.close();
						}
						Notifications nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
						nF.setDomain(strDomain);
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						
						nF.request = request;
						nF.setStrOrgId((String)session.getAttribute(ORGID));
						nF.setEmailTemplate(true);
						
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							nF.setStrProjectName(proName1);
						} else {
							nF.setStrCategoryName(strCategory);
						}
						nF.setStrDocumentName(strDocumentName);
						
						String alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						String alertAction = "DocumentListView.action";
						for(String strEmp : alEmp1) {
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							nF.setStrEmpId(strEmp.trim());
							nF.sendNotifications();
						}
						for(String strEmp : alSharePoc1) {
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrOther("other");
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							pst = con.prepareStatement("select * from client_poc where poc_id = ?");
							pst.setInt(1, uF.parseToInt(strEmp.trim()));
							rs = pst.executeQuery();
							boolean flg=false;
							while(rs.next()) {
								nF.setStrCustFName(rs.getString("contact_fname"));
								nF.setStrCustLName(rs.getString("contact_lname"));
								nF.setStrEmpMobileNo(rs.getString("contact_number"));
								if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
									nF.setStrEmpEmail(rs.getString("contact_email"));
									nF.setStrEmailTo(rs.getString("contact_email"));
								}
								flg = true;
							}
							rs.close();
							pst.close();
							
							if(flg) {
								nF.setStrEmpId(strEmp.trim());
								nF.sendNotifications();
							}
						}
						/**
						 * Alerts End
						 * */
						
					}
				}
				
				int subFolderDocsCnt = SubfolderDocsTRId!=null ? SubfolderDocsTRId.length : 0; 
				String proName = CF.getProjectNameById(con, getProId());
				StringBuilder sbMsg = new StringBuilder();
				sbMsg.append(SUCCESSM+""+getFolderName()+" folder updated ");
				if(subFolderDocsCnt>0) {
					sbMsg.append("and some documents added ");
				}
				sbMsg.append("successfully for "+proName+"."+END);
				session.setAttribute(MESSAGE, sbMsg.toString());
			}
			
//			for(int j=0; getFolderDocsTRId() != null && j<getFolderDocsTRId().length; j++) {
//				
//				double lengthBytes =  getStrFolderDoc()[j].length();
//				boolean isFileExist = false;
//				File f = new File(proFolderNameFolder+"/"+getStrFolderDocFileName()[j]);
//				if(f.isFile()) {
////				    System.out.println("isFile");
//				    if(f.exists()) {
//						isFileExist = true;
////					    System.out.println("exists");
//					} else {
////					    System.out.println("exists fail");
//					}   
//				} else {
////				    System.out.println("isFile fail");
//				}
//				if(lengthBytes > 0 && !isFileExist) {
//					
//					String []proFolderDocEmployee = request.getParameterValues("proFolderDocEmployee"+getFolderDocsTRId()[j]);
//					
//					List<String> alEmployee = null;
//					if(proFolderDocEmployee != null) {
//						alEmployee = Arrays.asList(proFolderDocEmployee);
//					}
//					StringBuilder sbEmps = null;
//					
//					for(int a=0; alEmployee != null && a<alEmployee.size(); a++) {
//						if(alEmployee.get(a) != null && !alEmployee.get(a).trim().equals("")) {
//							if(sbEmps == null) {
//								sbEmps = new StringBuilder();
//								sbEmps.append(","+ alEmployee.get(a).trim() +",");
//							} else {
//								sbEmps.append(alEmployee.get(a).trim() +",");
//							}
//						}
//					}
//					if(sbEmps == null) {
//						sbEmps = new StringBuilder();
//					}
//						pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date, " +
//								"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description,doc_parent_id) " +
//								"values(?,?,?,?, ?,?,?,?, ?,?,?,?,?,?)");
//						pst.setInt(1, uF.parseToInt(getClientId()));
//						pst.setInt(2, uF.parseToInt(getProId()));
//						pst.setString(3, getFolderName());
//						pst.setInt(4, uF.parseToInt(strSessionEmpId));
//						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setString(6, "folder");
//						pst.setInt(7, uF.parseToInt(getProFolderId()));
//						pst.setInt(8, uF.parseToInt(getProFolderDocTasks()[j]));
//						pst.setInt(9, uF.parseToInt(getFolderDocSharingType()[j]));
//						pst.setString(10, sbEmps.toString());
//						pst.setInt(11, uF.parseToInt(getProCategoryTypeFolderDoc()[j]));
//						pst.setString(12, getStrFolderScopeDoc()[j]);
//						pst.setString(13, getStrFolderDocDescription()[j]);
//						pst.setInt(14, 0);
//	//					System.out.println("pst====>"+pst);
//						pst.execute();
//						pst.close();
//						
//						String proDocumentId = "";
//						pst = con.prepareStatement("select max(pro_document_id)as pro_document_id from project_document_details");
//						rs = pst.executeQuery();
//						while (rs.next()) {
//							proDocumentId = rs.getString("pro_document_id");
//						}
//						rs.close();
//						pst.close();
//						
//						uploadProjectFolderDocuments(con, getProId(), getStrFolderDoc()[j], getStrFolderDocFileName()[j], proFolderNameFolder, proDocumentId);
//				}
//			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	private void uploadProjectFolderDocuments(Connection con, String proId, File contentFile, String contentFileName, String realFolderPath, String proDocumentId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			double lengthBytes =  contentFile.length();
			
			String ext = FilenameUtils.getExtension(realFolderPath+"/"+contentFileName);
			UtilityFunctions uF = new UtilityFunctions();
			
			String fileType = uF.getFileTypeOnExtension(ext);
			String fileSize = uF.getFileTypeSize(lengthBytes);
			
			pst = con.prepareStatement("update project_document_details set file_size=?, file_type=?, size_in_bytes=? where pro_document_id=?");
			pst.setString(1, fileSize);
			pst.setString(2, fileType);
			pst.setString(3, lengthBytes+"");
			pst.setInt(4, uF.parseToInt(proDocumentId));
			pst.execute();
			pst.close();
			
			UploadProjectDocuments upd = new UploadProjectDocuments();
			upd.setServletRequest(request);
			upd.setDocType("PROJECT_FOLDER_DOCUMENTS");
			upd.setDocumentFile(contentFile);
			upd.setDocumentFileFileName(contentFileName);
			upd.setRealFolderPath(realFolderPath);
			upd.setProId(proId);
			upd.setProDocumentId(proDocumentId);
			upd.setCF(CF);
//			System.out.println("empId2 ===> "+empId2+" getEmpImage() ===> "+getEmpImage());
			upd.uploadProjectDocuments();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String[] getFolderDocsTRId() {
		return folderDocsTRId;
	}

	public void setFolderDocsTRId(String[] folderDocsTRId) {
		this.folderDocsTRId = folderDocsTRId;
	}

	public String[] getFolderDocSharingType() {
		return folderDocSharingType;
	}

	public void setFolderDocSharingType(String[] folderDocSharingType) {
		this.folderDocSharingType = folderDocSharingType;
	}

	public String[] getProFolderDocTasks() {
		return proFolderDocTasks;
	}

	public void setProFolderDocTasks(String[] proFolderDocTasks) {
		this.proFolderDocTasks = proFolderDocTasks;
	}

	public String[] getStrFolderName() {
		return strFolderName;
	}

	public void setStrFolderName(String[] strFolderName) {
		this.strFolderName = strFolderName;
	}

	public String[] getDocCountId() {
		return docCountId;
	}

	public void setDocCountId(String[] docCountId) {
		this.docCountId = docCountId;
	}

	public File[] getStrFolderDoc() {
		return strFolderDoc;
	}

	public void setStrFolderDoc(File[] strFolderDoc) {
		this.strFolderDoc = strFolderDoc;
	}

	public String[] getStrFolderDocFileName() {
		return strFolderDocFileName;
	}

	public void setStrFolderDocFileName(String[] strFolderDocFileName) {
		this.strFolderDocFileName = strFolderDocFileName;
	}

	public String[] getProFolderEmployee() {
		return proFolderEmployee;
	}

	public void setProFolderEmployee(String[] proFolderEmployee) {
		this.proFolderEmployee = proFolderEmployee;
	}

	public String getProFolderSharingType() {
		return proFolderSharingType;
	}

	public void setProFolderSharingType(String proFolderSharingType) {
		this.proFolderSharingType = proFolderSharingType;
	}

	public String getPrjectname() {
		return prjectname;
	}

	public void setPrjectname(String prjectname) {
		this.prjectname = prjectname;
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

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getProFolderId() {
		return proFolderId;
	}

	public void setProFolderId(String proFolderId) {
		this.proFolderId = proFolderId;
	}
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProCategoryTypeFolder() {
		return proCategoryTypeFolder;
	}

	public void setProCategoryTypeFolder(String proCategoryTypeFolder) {
		this.proCategoryTypeFolder = proCategoryTypeFolder;
	}

	public String getStrFolderDescription() {
		return strFolderDescription;
	}

	public void setStrFolderDescription(String strFolderDescription) {
		this.strFolderDescription = strFolderDescription;
	}

	public String[] getStrFolderDocDescription() {
		return strFolderDocDescription;
	}

	public void setStrFolderDocDescription(String[] strFolderDocDescription) {
		this.strFolderDocDescription = strFolderDocDescription;
	}

	public String[] getStrFolderScopeDoc() {
		return strFolderScopeDoc;
	}

	public void setStrFolderScopeDoc(String[] strFolderScopeDoc) {
		this.strFolderScopeDoc = strFolderScopeDoc;
	}

	public String[] getProCategoryTypeFolderDoc() {
		return proCategoryTypeFolderDoc;
	}

	public void setProCategoryTypeFolderDoc(String[] proCategoryTypeFolderDoc) {
		this.proCategoryTypeFolderDoc = proCategoryTypeFolderDoc;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getProFolderTask() {
		return proFolderTask;
	}

	public void setProFolderTask(String proFolderTask) {
		this.proFolderTask = proFolderTask;
	}

	public String getProFolderCategory() {
		return proFolderCategory;
	}

	public void setProFolderCategory(String proFolderCategory) {
		this.proFolderCategory = proFolderCategory;
	}

	public String getIsFolderEdit() {
		return isFolderEdit;
	}

	public void setIsFolderEdit(String isFolderEdit) {
		this.isFolderEdit = isFolderEdit;
	}

	public String getIsFolderDelete() {
		return isFolderDelete;
	}

	public void setIsFolderDelete(String isFolderDelete) {
		this.isFolderDelete = isFolderDelete;
	}

	public String[] getProFolderPoc() {
		return proFolderPoc;
	}

	public void setProFolderPoc(String[] proFolderPoc) {
		this.proFolderPoc = proFolderPoc;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}
	
}