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
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UploadProjectDocuments;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateProjectDocumentFile extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	CommonFunctions CF; 
	HttpSession session;
	String strSessionEmpId;
	String strOrgId;
	String strUserType;
	
	String clientId;
	String proId;
	String taskId;
	String folderName;
	String proFolderId;
	String type;
	String filePath; 
	String fileDir;	
	String operation;
	
	File strFolderDoc;
	String strFolderDocFileName;
	
	String strFolderDocDescription;
	String strFolderScopeDoc;
	String proCategoryTypeFolder;
	String proFolderTasks;
	String proFolderSharingType;
	String[] proFolderEmployee;
	
	String isFolderDocEdit;
	String isFolderDocDelete;

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
		
		getFolderAndDocuments(uF);
		
		if(getOperation() != null && getOperation().equals("U")) {
			if(getFromPage() != null && getFromPage().equals("VAP")) {
				insertUpdatedDocs(uF);
				if(getPageType() != null && getPageType().equals("MP")) {
					return MYSUCCESS;
				} else {
					return "vapsuccess";
				}
			} else if(getFromPage() != null && getFromPage().equals("MP")) {
				insertUpdatedDocs(uF);
				return "mpsuccess";
			} else {
				insertUpdatedDocs(uF);
				if(getPageType() != null && getPageType().equals("MP")) {
					return MYSUCCESS;
				} else {
					return SUCCESS;
				}
			}
		}
		
		return LOAD;
	}

	private void insertUpdatedDocs(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			boolean flag = false;
			int nProParentDocId = 0;
			int nProParentFolderId = 0;
			String docuName = null;
			pst = con.prepareStatement("select pro_document_id,doc_parent_id,pro_folder_id,document_name,align_with from project_document_details where pro_document_id=?");
			pst.setInt(1, uF.parseToInt(getProFolderId()));
			rs = pst.executeQuery();
			if(rs.next()){
				flag = true;
				docuName = rs.getString("document_name");
				nProParentDocId = uF.parseToInt(rs.getString("doc_parent_id"));
				nProParentFolderId = uF.parseToInt(rs.getString("pro_folder_id"));
			}
			
			if (flag) {
				int nVersion = 0;
				if(nProParentDocId == 0) {
					nProParentDocId = uF.parseToInt(getProFolderId());
				}
				
				pst = con.prepareStatement("select count(pro_document_id) as cnt from project_document_details where pro_document_id =? and pro_document_id > 0");
				pst.setInt(1, uF.parseToInt(getProFolderId()));
				rs = pst.executeQuery();
				if(rs.next()){
					nVersion = uF.parseToInt(rs.getString("cnt"));
				}
				
				pst = con.prepareStatement("select count(pro_document_id) as cnt from project_document_details where doc_parent_id in ("+nProParentDocId+") and doc_parent_id > 0");
				rs = pst.executeQuery();
				if(rs.next()){
					nVersion += uF.parseToInt(rs.getString("cnt"));
				}
				
				nVersion++;
				
				String mainPathWithOrg = CF.getProjectDocumentFolder()+strOrgId;
				File fileOrg = new File(mainPathWithOrg);
				if (!fileOrg.exists()) {
					if (fileOrg.mkdir()) {
						System.out.println("Org Directory is created!");
					}
				}
				
				String mainPath = mainPathWithOrg+"/Projects";
				File file = new File(mainPath);
				if (!file.exists()) {
					if (file.mkdir()) {
						System.out.println("Projects Directory is created!");
					}
				}
				
				String proNameFolder = mainPath +"/"+getProId();
				File file1 = new File(proNameFolder);
				if (!file1.exists()) {
					if (file1.mkdir()) {
						System.out.println("pro_id Directory is created!");
					}
				} 
				int nDocPosition = 1;
				String strFolderName = null;
				
				if(nProParentFolderId > 0){
					pst = con.prepareStatement("select pro_document_id,pro_folder_id,folder_name from project_document_details where pro_document_id=?");
					pst.setInt(1, nProParentFolderId);
					rs = pst.executeQuery();
					int nProFId = 0;
					int nProDocId = 0;
					String strProFolder = null;
					if(rs.next()){
						flag = true;
						nProDocId = uF.parseToInt(rs.getString("pro_document_id"));
						nProFId = uF.parseToInt(rs.getString("pro_folder_id"));
						strProFolder = rs.getString("folder_name");
					}
					rs.close();
					pst.close();
					
					if(nProFId > 0){
						pst = con.prepareStatement("select pro_document_id,pro_folder_id,folder_name from project_document_details where pro_document_id=?");
						pst.setInt(1, nProFId);
						rs = pst.executeQuery();
						int nProFId1 = 0;
						int nProDocId1 = 0;
						String strProFolder1 = null;
						if(rs.next()){
							flag = true;
							nProDocId1 = uF.parseToInt(rs.getString("pro_document_id"));
							nProFId1 = uF.parseToInt(rs.getString("pro_folder_id"));
							strProFolder1 = rs.getString("folder_name");
						}
						rs.close();
						pst.close();
						
						proNameFolder += "/"+strProFolder1+"/"+strProFolder;
						
					} else {
						proNameFolder += "/"+strProFolder;
					}
					strFolderName = strProFolder;
					nDocPosition = 2;
				}
				
				
				if(getStrFolderDoc() != null) {
					double lengthBytes = getStrFolderDoc().length();
					
					String extenstion=FilenameUtils.getExtension(getStrFolderDocFileName());	
					String strFileName = FilenameUtils.getBaseName(getStrFolderDocFileName());
					strFileName = strFileName+"V"+nVersion+"."+extenstion;
					
					boolean isFileExist = false;
					File f = new File(proNameFolder+"/"+strFileName);
					if(f.isFile()) {
	//				    System.out.println("isFile");
					    if(f.exists()){
							isFileExist = true;
	//					    System.out.println("exists");
						}else{
	//					    System.out.println("exists fail");
						}   
					}else{
	//				    System.out.println("isFile fail");
					}
				
					if(lengthBytes > 0 && !isFileExist) {
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
						
						if(nDocPosition == 1){
							
							pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
							"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
							pst.setString(1, getStrFolderDocDescription());
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								pst.setInt(2, 1);
							} else {
								pst.setInt(2, 0);
							}
//							pst.setInt(2, uF.parseToInt(getStrAlignWith()));
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								pst.setInt(3, uF.parseToInt(getProId()));
							} else {
								pst.setInt(3, uF.parseToInt(getProFolderTasks()));
							}
//							pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
							pst.setString(4, sbEmps.toString());
							pst.setString(5, "");
							pst.setString(6, "");
							pst.setInt(7, uF.parseToInt(getProFolderSharingType()));
							pst.setString(8, sbEmps.toString());
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
							
							
							pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,added_by, entry_date,folder_file_type," +
								"pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description,doc_parent_id," +
								"doc_version, is_edit, is_delete,is_cust_add,sharing_poc,feed_id) " +
									"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
							pst.setInt(1, uF.parseToInt(getClientId()));
							pst.setInt(2, uF.parseToInt(getProId()));
							pst.setInt(3, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//							pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setString(5, "file");
							pst.setInt(6, nProParentFolderId);
							pst.setInt(7, uF.parseToInt(getProFolderTasks()));
							pst.setInt(8, uF.parseToInt(getProFolderSharingType()));
							pst.setString(9, sbEmps.toString());
							pst.setInt(10, uF.parseToInt(getProCategoryTypeFolder()));
							pst.setString(11, getStrFolderScopeDoc());
							pst.setString(12, getStrFolderDocDescription());
							pst.setInt(13, nProParentDocId);
							pst.setInt(14, nVersion);
							pst.setBoolean(15, uF.parseToBoolean(getIsFolderDocEdit()));
							pst.setBoolean(16, uF.parseToBoolean(getIsFolderDocDelete()));
							if(strUserType != null && strUserType.equals(CUSTOMER)) {
								pst.setBoolean(17, true);
							} else {
								pst.setBoolean(17, false);
							}
							pst.setString(18, sbPoc.toString());
							pst.setInt(19, uF.parseToInt(feedId));
			//				System.out.println("pst====>"+pst);
							pst.execute();
							pst.close();
							
							String proDocumentId = "";
							pst = con.prepareStatement("select max(pro_document_id)as pro_document_id from project_document_details");
							rs = pst.executeQuery();
							while (rs.next()) {
								proDocumentId = rs.getString("pro_document_id");
							}
							rs.close();
							pst.close();
							
							
							if(uF.parseToInt(proDocumentId) > 0){
								uploadProjectDocuments(con, getProId(), getStrFolderDoc(), strFileName, proNameFolder, proDocumentId, feedId);
								
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
								pst.setInt(1, uF.parseToInt(proDocumentId));
								rs = pst.executeQuery();
								while (rs.next()) {
									strDocumentName = rs.getString("folder_name");
								}
								rs.close();
								pst.close();
								
								String proName = null;
								String strCategory = null;
								if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
									pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
									pst.setInt(1, uF.parseToInt(getProFolderTasks()));
									rs = pst.executeQuery();
									
									while(rs.next()) {
										proName = rs.getString("pro_name");
									}
									rs.close();
									pst.close();
								} else {
									pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
									pst.setInt(1, uF.parseToInt(getProFolderTasks()));
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
									nF.setStrProjectName(proName);
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
//									userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
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
//									userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
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
							
						} else {
							
							pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
							"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
							pst.setString(1, getStrFolderDocDescription());
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								pst.setInt(2, 1);
							} else {
								pst.setInt(2, 0);
							}
//							pst.setInt(2, uF.parseToInt(getStrAlignWith()));
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								pst.setInt(3, uF.parseToInt(getProId()));
							} else {
								pst.setInt(3, uF.parseToInt(getProFolderTasks()));
							}
//							pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
							pst.setString(4, sbEmps.toString());
							pst.setString(5, "");
							pst.setString(6, "");
							pst.setInt(7, uF.parseToInt(getProFolderSharingType()));
							pst.setString(8, sbEmps.toString());
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
								"description,doc_parent_id,doc_version, is_edit, is_delete, is_cust_add,sharing_poc,feed_id)" +
									"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
							pst.setInt(1, uF.parseToInt(getClientId()));
							pst.setInt(2, uF.parseToInt(getProId()));
							pst.setString(3, strFolderName);
							pst.setInt(4, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//							pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setString(6, "folder");
							pst.setInt(7, nProParentFolderId);
							pst.setInt(8, uF.parseToInt(getProFolderTasks()));
							pst.setInt(9, uF.parseToInt(getProFolderSharingType()));
							pst.setString(10, sbEmps.toString());
							pst.setInt(11, uF.parseToInt(getProCategoryTypeFolder()));
							pst.setString(12, getStrFolderScopeDoc());
							pst.setString(13, getStrFolderDocDescription());
							pst.setInt(14, nProParentDocId);
							pst.setInt(15, nVersion);
							pst.setBoolean(16, uF.parseToBoolean(getIsFolderDocEdit()));
							pst.setBoolean(17, uF.parseToBoolean(getIsFolderDocDelete()));
							if(strUserType != null && strUserType.equals(CUSTOMER)) {
								pst.setBoolean(18, true);
							} else {
								pst.setBoolean(18, false);
							}
							pst.setString(19, sbPoc.toString());
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
							
							
							uploadProjectFolderDocuments(con, getProId(), getStrFolderDoc(), strFileName, proNameFolder, proDocumentId, feedId);
							
							
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
							pst.setInt(1, uF.parseToInt(proDocumentId));
							rs = pst.executeQuery();
							while (rs.next()) {
								strDocumentName = rs.getString("folder_name");
							}
							rs.close();
							pst.close();
							
							String proName = null;
							String strCategory = null;
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
								pst.setInt(1, uF.parseToInt(getProFolderTasks()));
								rs = pst.executeQuery();
								
								while(rs.next()) {
									proName = rs.getString("pro_name");
								}
								rs.close();
								pst.close();
							} else {
								pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
								pst.setInt(1, uF.parseToInt(getProFolderTasks()));
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
								nF.setStrProjectName(proName);
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
//								userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
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
					
					if(getFromPage() != null && getFromPage().equals("MP")) {
						String taskName = CF.getProjectTaskNameByTaskId(con, uF, getTaskId());
						session.setAttribute(MESSAGE, SUCCESSM+" Document updated and create new version for "+taskName+" successfully."+END);
					} else if(getFromPage() != null && getFromPage().equals("VAP")) {
						String proName = CF.getProjectNameById(con, getProId());
						session.setAttribute(MESSAGE, SUCCESSM+" Document updated and create new version for "+proName+" successfully."+END);
					} else {
						String proName = CF.getProjectNameById(con, getProId());
						session.setAttribute(MESSAGE, SUCCESSM+" Document updated and create new version for "+proName+" successfully."+END);
					}
				} else {
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
					
					pst = con.prepareStatement("update project_document_details set pro_id=?, added_by=?, entry_date=?, align_with=?, sharing_type=?," +
						"sharing_resources=?, project_category=?, scope_document=?, description=?, is_edit=?, is_delete=?, is_cust_add=?,sharing_poc=? where pro_document_id=?");
					pst.setInt(1, uF.parseToInt(getProId()));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(getProFolderTasks()));
					pst.setInt(5, uF.parseToInt(getProFolderSharingType()));
					pst.setString(6, sbEmps.toString());
					pst.setInt(7, uF.parseToInt(getProCategoryTypeFolder()));
					pst.setString(8, getStrFolderScopeDoc());
					pst.setString(9, getStrFolderDocDescription());
					pst.setBoolean(10, uF.parseToBoolean(getIsFolderDocEdit()));
					pst.setBoolean(11, uF.parseToBoolean(getIsFolderDocDelete()));
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						pst.setBoolean(12, true);
					} else {
						pst.setBoolean(12, false);
					}
					pst.setString(13, sbPoc.toString());
					pst.setInt(14, uF.parseToInt(getProFolderId()));
	//				System.out.println("pst====>"+pst);
					pst.execute();
					pst.close();
					
					
					String docFeedId = "";
					pst = con.prepareStatement("select feed_id from project_document_details where pro_document_id=?");
					pst.setInt(1, uF.parseToInt(getProFolderId()));
					rs = pst.executeQuery();
					while (rs.next()) {
						docFeedId = rs.getString("feed_id");
					}
					rs.close();
					pst.close();
					
					
					pst = con.prepareStatement("update communication_1 set communication=?,align_with=?,align_with_id=?,tagged_with=?," +
					"visibility=?,visibility_with_id=?,update_time=? where communication_id=?");
					pst.setString(1, getStrFolderDocDescription());
					if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
						pst.setInt(2, 1);
					} else {
						pst.setInt(2, 0);
					}
//					pst.setInt(2, uF.parseToInt(getStrAlignWith()));
					if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
						pst.setInt(3, uF.parseToInt(getProId()));
					} else {
						pst.setInt(3, uF.parseToInt(getProFolderTasks()));
					}
//					pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
					pst.setString(4, sbEmps.toString());
					pst.setInt(5, uF.parseToInt(getProFolderSharingType()));
					pst.setString(6, sbEmps.toString());
					pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setInt(8, uF.parseToInt(docFeedId));
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
						pst.setInt(1, uF.parseToInt(getProFolderTasks()));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							proName1 = rs.getString("pro_name");
						}
						rs.close();
						pst.close();
					} else {
						pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
						pst.setInt(1, uF.parseToInt(getProFolderTasks()));
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
					
					if(getFromPage() != null && getFromPage().equals("MP")) {
						String taskName = CF.getProjectTaskNameByTaskId(con, uF, getTaskId());
						session.setAttribute(MESSAGE, SUCCESSM+" Document updated for "+taskName+" successfully."+END);
					} else if(getFromPage() != null && getFromPage().equals("VAP")) {
						String proName = CF.getProjectNameById(con, getProId());
						session.setAttribute(MESSAGE, SUCCESSM+" Document updated for "+proName+" successfully."+END);
					} else {
						String proName = CF.getProjectNameById(con, getProId());
						session.setAttribute(MESSAGE, SUCCESSM+" Document updated for "+proName+" successfully."+END);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void uploadProjectFolderDocuments(Connection con, String proId, File contentFile, String contentFileName, String realFolderPath, String proDocumentId, String feedId) {

		PreparedStatement pst = null;
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
			upd.setFeedId(feedId);
			upd.setCF(CF);
//			System.out.println("empId2 ===> "+empId2+" getEmpImage() ===> "+getEmpImage());
			upd.uploadProjectDocuments();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void uploadProjectDocuments(Connection con, String proId, File contentFile, String contentFileName, String realFolderPath, String proDocumentId, String feedId) {

		PreparedStatement pst = null;
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
			upd.setDocType("PROJECT_DOCUMENTS");
			upd.setDocumentFile(contentFile);
			upd.setDocumentFileFileName(contentFileName);
			upd.setRealFolderPath(realFolderPath);
			upd.setProId(proId);
			upd.setProDocumentId(proDocumentId);
			upd.setFeedId(feedId);
			upd.setCF(CF);
			upd.uploadProjectDocuments();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
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
			
			Map<String, String> hmFileIcon = CF.getFileIcon();
			request.setAttribute("hmFileIcon",hmFileIcon);
			

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
				hmProDocumentDetails.put("SCOPE_DOCUMENT", rs.getString("scope_document"));
				
				hmProDocumentDetails.put("EDIT_STATUS", uF.parseToBoolean(rs.getString("is_edit")) == true ? "checked" : "");
				hmProDocumentDetails.put("DELETE_STATUS", uF.parseToBoolean(rs.getString("is_delete")) == true ? "checked" : "");
				
				hmProDocumentDetails.put("EDIT_STATUS_VAL", uF.parseToBoolean(rs.getString("is_edit")) == true ? "1" : "0");
				hmProDocumentDetails.put("DELETE_STATUS_VAL", uF.parseToBoolean(rs.getString("is_delete")) == true ? "1" : "0");
				
				hmProDocumentDetails.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmProDocumentDetails.put("FILE_EXTENSION", extenstion);
				
				hmProDocumentDetails.put("SHARING_POC", rs.getString("sharing_poc"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProDocumentDetails",hmProDocumentDetails);
			
			
			String strProjectSelect = "";
			String strOtherSelect = "";
			if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 1){
				strProjectSelect = "selected";
				strOtherSelect = "";
			} else if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 2){
				strProjectSelect = "";
				strOtherSelect = "selected";
			}
			
			StringBuilder sbProCategoryTypeFolder = new StringBuilder("<span style=\"float: left;\"><select name=\"proCategoryTypeFolder\" id=\"proCategoryTypeFolder1\" style=\"width:100px !important;\" onchange=\"changeCategoryType(this.value,'proFolderTaskSpan1',1,'folderTR1','Folder');\">"+
		    "<option value=\"1\" "+strProjectSelect+">Project</option><option value=\"2\" "+strOtherSelect+">Category</option></select></span>");
			
			request.setAttribute("sbProCategoryTypeFolder", sbProCategoryTypeFolder.toString());
			
			
			StringBuilder sbProFolderTasks = new StringBuilder("<select name=\"proFolderTasks\" id=\"proFolderTasks\" style=\"width:100px !important;\">");
			if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 1){
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
			} else if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 2){
				pst = con.prepareStatement("select project_category_id,project_category from project_category_details where project_category_id>1");
				rs = pst.executeQuery();
				while (rs.next()) {
					if(uF.parseToInt(hmProDocumentDetails.get("ALIGN_WITH")) == uF.parseToInt(rs.getString("project_category_id"))) {
						sbProFolderTasks.append("<option value=\"" + rs.getString("project_category_id") + "\" selected>" + rs.getString("project_category") + "</option>");
					} else {
						sbProFolderTasks.append("<option value=\"" + rs.getString("project_category_id") + "\">" + rs.getString("project_category") + "</option>");
					}
				}
				rs.close();
				pst.close();
			}
			sbProFolderTasks.append("</select>");
			request.setAttribute("sbProFolderTasks", sbProFolderTasks.toString());
			
			
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
							
							sbProFolderEmp.append("<option value=\"" + rs.getString("emp_id") + "\" selected>" + rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + "</option>");
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
						
						sbProFolderEmp.append("<option value=\"" + rs.getString("emp_id") + "\">" + rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + "</option>");
					}
			}
			rs.close();
			pst.close();
			sbProFolderEmp.append("</select>");
			request.setAttribute("sbProFolderEmp", sbProFolderEmp.toString());
			
			StringBuilder sbProSharingType = new StringBuilder("<select name=\"proFolderSharingType\" id=\"proFolderSharingType0\" style=\"width:100px !important;\" onchange=\"showHideResources(this.value, '1')\">");
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
			
			
			StringBuilder sbProEmp = new StringBuilder("<select name=\"proEmployee\" id=\"proEmployee\" style=\"width:160px !important;\" multiple size=\"3\">");
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
				sbProEmp.append("<option value=\"" + rs.getString("emp_id") + "\">" + rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname") + "</option>");
			}
			rs.close();
			pst.close();
			sbProEmp.append("</select>");
			request.setAttribute("sbProEmp", sbProEmp.toString());
			
			StringBuilder sbProCategory = new StringBuilder("<select name=\"proTasks\" id=\"proTasks\" style=\"width:135px !important;\">");
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
	
	
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getProFolderId() {
		return proFolderId;
	}

	public void setProFolderId(String proFolderId) {
		this.proFolderId = proFolderId;
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

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public File getStrFolderDoc() {
		return strFolderDoc;
	}

	public void setStrFolderDoc(File strFolderDoc) {
		this.strFolderDoc = strFolderDoc;
	}

	public String getStrFolderDocFileName() {
		return strFolderDocFileName;
	}

	public void setStrFolderDocFileName(String strFolderDocFileName) {
		this.strFolderDocFileName = strFolderDocFileName;
	}

	public String getStrFolderDocDescription() {
		return strFolderDocDescription;
	}

	public void setStrFolderDocDescription(String strFolderDocDescription) {
		this.strFolderDocDescription = strFolderDocDescription;
	}

	public String getStrFolderScopeDoc() {
		return strFolderScopeDoc;
	}

	public void setStrFolderScopeDoc(String strFolderScopeDoc) {
		this.strFolderScopeDoc = strFolderScopeDoc;
	}

	public String getProCategoryTypeFolder() {
		return proCategoryTypeFolder;
	}

	public void setProCategoryTypeFolder(String proCategoryTypeFolder) {
		this.proCategoryTypeFolder = proCategoryTypeFolder;
	}

	public String getProFolderTasks() {
		return proFolderTasks;
	}

	public void setProFolderTasks(String proFolderTasks) {
		this.proFolderTasks = proFolderTasks;
	}

	public String getProFolderSharingType() {
		return proFolderSharingType;
	}

	public void setProFolderSharingType(String proFolderSharingType) {
		this.proFolderSharingType = proFolderSharingType;
	}

	public String[] getProFolderEmployee() {
		return proFolderEmployee;
	}

	public void setProFolderEmployee(String[] proFolderEmployee) {
		this.proFolderEmployee = proFolderEmployee;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getIsFolderDocEdit() {
		return isFolderDocEdit;
	}

	public void setIsFolderDocEdit(String isFolderDocEdit) {
		this.isFolderDocEdit = isFolderDocEdit;
	}

	public String getIsFolderDocDelete() {
		return isFolderDocDelete;
	}

	public void setIsFolderDocDelete(String isFolderDocDelete) {
		this.isFolderDocDelete = isFolderDocDelete;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
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
