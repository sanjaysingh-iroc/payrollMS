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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UploadProjectDocuments;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DocumentList extends ActionSupport implements ServletRequestAware, ServletResponseAware,  IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	int proId;
	String strUserType;
	String strSessionEmpId;
	
	String strOrgId;
	String btnSave;
	String proType;
	String fromPage; 
	String taskId;
	
	CommonFunctions CF;
	
	String strId;
	String type;
	
	String []folderTRId;
	String []strFolderName;
	
	String[] docsTRId;
	File[] strDoc;
	String[] strDocFileName;
	
	String strSearchDoc;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		strOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
//			System.out.println("getBtnSave() ===>> " + getBtnSave()); 
		if(getBtnSave() != null && getBtnSave().equals("Save")) {
			insertDocumentDetails(uF);
			if(getFromPage() !=null && getFromPage().equals("COMMUNICATION")) {
				return "updateCommunication";
			} else {
				return UPDATE;
			}
		}
		getProjectDocumentDetails(uF);
		return SUCCESS;
	}
	
	
	private void insertDocumentDetails(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			con = db.makeConnection(con);
			
			MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request; 
			
			String mainPathWithOrg = CF.getProjectDocumentFolder()+strOrgId;
			File fileOrg = new File(mainPathWithOrg);
			if (!fileOrg.exists()) {
				if (fileOrg.mkdir()) {
					System.out.println("Org Directory is created!");
				}
			}
			
			String mainProPath = mainPathWithOrg+"/Projects";
			File file = new File(mainProPath);
			if (!file.exists()) {
				if (file.mkdir()) {
					System.out.println("Projects Directory is created!");
				}
			}
			
			String mainCatPath = mainPathWithOrg+"/Categories";
			File fileCat = new File(mainCatPath);
			if (!fileCat.exists()) {
				if (fileCat.mkdir()) {
					System.out.println("Category Directory is created!");
				}
			}
			
			String[] strScopeDoc = request.getParameterValues("strScopeDoc");
			String[] proCategoryTypeDoc = request.getParameterValues("proCategoryTypeDoc");
			String[] docSharingType = request.getParameterValues("docSharingType");
			String[] strDocDescription = request.getParameterValues("strDocDescription");
			
			String[] isDocEdit = request.getParameterValues("isDocEdit");
			String[] isDocDelete = request.getParameterValues("isDocDelete");
			
//			System.out.println("isDocEdit ===>> " + isDocEdit.length);
//			System.out.println("isDocDelete ===>> " + isDocDelete.length);
			
			String docWithCatORProName = null;
			String folderWithCatORProName = null;
			
			String strDomain = request.getServerName().split("\\.")[0];
			for(int j=0; getDocsTRId() != null && j<getDocsTRId().length; j++) {
				
				String strOrgCategoryDoc = request.getParameter("strOrgCategoryDoc"+getDocsTRId()[j]);
				String strOrgProjectDoc = request.getParameter("strOrgProjectDoc"+getDocsTRId()[j]);
				
				String proNameFolder = "0";
//				System.out.println("proNameFolder ==============>> " + proNameFolder);
				if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
					docWithCatORProName = CF.getProjectNameById(con, strOrgProjectDoc)+ " project";
					proNameFolder = mainProPath +"/"+(strOrgProjectDoc!=null ? strOrgProjectDoc : "0");
					File file1 = new File(proNameFolder);
					if (!file1.exists()) {
						if (file1.mkdir()) {
							System.out.println("pro_id Directory is created!");
						}
					} 
				} else if(uF.parseToInt(proCategoryTypeDoc[j]) == 2) {
					docWithCatORProName = CF.getProjectCategory(con, uF, strOrgCategoryDoc)+ " category";
					proNameFolder = mainCatPath;
					File file1 = new File(proNameFolder);
					if (!file1.exists()) {
						if (file1.mkdir()) {
							System.out.println("cat_id Directory is created!");
						}
					}
				}
				
				double lengthBytes =  0;
				if(getStrDoc() !=null && getStrDoc()[j]!=null) {
					lengthBytes =  getStrDoc()[j].length();
				}
				boolean isFileExist = false;
				String strFileName = null;
				if(getStrDocFileName()!=null && getStrDocFileName()[j] !=null) {
					String extenstion=FilenameUtils.getExtension(getStrDocFileName()[j]);	
					strFileName = FilenameUtils.getBaseName(getStrDocFileName()[j]);
					strFileName = strFileName+"v1."+extenstion;
					
					File f = new File(proNameFolder+"/"+strFileName);
					if(f.isFile()) {
	//				    System.out.println("isFile ===========================>> ");
					    if(f.exists()) {
							isFileExist = true;
	//					    System.out.println("exists");
						} else {
	//					    System.out.println("exists fail");
						}   
					} else {
	//				    System.out.println("isFile fail");
					}
				}
				
				if(lengthBytes > 0 && !isFileExist) {
					
					String[] strOrgResourcesDoc = request.getParameterValues("strOrgResourcesDoc"+getDocsTRId()[j]);
					List<String> alEmployee = null;
					if(strOrgResourcesDoc != null) {
						alEmployee = Arrays.asList(strOrgResourcesDoc);
					}
					StringBuilder sbEmps = null;
					List<String> alEmp = new ArrayList<String>();
					for(int a=0; alEmployee != null && a<alEmployee.size(); a++) {
						if(alEmployee.get(a) != null && !alEmployee.get(a).trim().equals("")) {
							if(sbEmps == null) {
								sbEmps = new StringBuilder();
								sbEmps.append(","+ alEmployee.get(a).trim() +",");
							} else {
								sbEmps.append(alEmployee.get(a).trim() +",");
							}
							if(!alEmp.contains(alEmployee.get(a).trim())) {
								alEmp.add(alEmployee.get(a).trim());
							}
						}
					}
					if(sbEmps == null) {
						sbEmps = new StringBuilder();
					}
					
					String[] strOrgPocDoc = request.getParameterValues("strOrgPocDoc"+getDocsTRId()[j]);
					List<String> alPoc = null;
					if(strOrgPocDoc != null) {
						alPoc = Arrays.asList(strOrgPocDoc);
					}
					StringBuilder sbPoc = null;
					List<String> alSharePoc = new ArrayList<String>();
					for(int a=0; alPoc != null && a<alPoc.size(); a++) {
						if(alPoc.get(a) != null && !alPoc.get(a).trim().equals("")) {
							if(sbPoc == null) {
								sbPoc = new StringBuilder();
								sbPoc.append(","+ alPoc.get(a).trim() +",");
							} else {
								sbPoc.append(alPoc.get(a).trim() +",");
							}
							if(!alSharePoc.contains(alPoc.get(a).trim())) {
								alSharePoc.add(alPoc.get(a).trim());
							}
						}
					}
					if(sbPoc == null) {
						sbPoc = new StringBuilder();
					}
					
					
					pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
					"visibility,visibility_with_id,created_by,create_time) values(?,?,?,?, ?,?,?,?, ?,?)"); //,doc_or_image
					pst.setString(1, strDocDescription[j]);
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						pst.setInt(2, 1);
					} else {
						pst.setInt(2, 0);
					}
//					pst.setInt(2, uF.parseToInt(getStrAlignWith()));
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						pst.setInt(3, uF.parseToInt(strOrgProjectDoc));
					} else {
						pst.setInt(3, uF.parseToInt(strOrgCategoryDoc));
					}
//					pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
					pst.setString(4, sbEmps.toString());
					pst.setString(5, "");
					pst.setString(6, "");
					pst.setInt(7, uF.parseToInt(docSharingType[j]));
					pst.setString(8, sbEmps.toString());
					pst.setInt(9, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//					pst.setString(11, strFileName);
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
				
					pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,added_by,entry_date,folder_file_type," +
						"pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description,doc_parent_id," +
						"is_edit,is_delete,is_cust_add,sharing_poc,doc_version,feed_id) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setInt(1, 0);
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						pst.setInt(2, uF.parseToInt(strOrgProjectDoc));
					} else {
						pst.setInt(2, 0);
					}
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(5, "file");
					pst.setInt(6, 0);
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						pst.setInt(7, 0);
					} else {
						pst.setInt(7, uF.parseToInt(strOrgCategoryDoc));
					}
					pst.setInt(8, uF.parseToInt(docSharingType[j]));
					pst.setString(9, sbEmps.toString());
					pst.setInt(10, uF.parseToInt(proCategoryTypeDoc[j]));
					pst.setString(11, strScopeDoc[j]);
					pst.setString(12, strDocDescription[j]);
					pst.setInt(13, 0);
					pst.setBoolean(14, uF.parseToBoolean(isDocEdit[j]));
					pst.setBoolean(15, uF.parseToBoolean(isDocDelete[j]));
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						pst.setBoolean(16, true);
					} else {
						pst.setBoolean(16, false);
					}
					pst.setString(17, sbPoc.toString());
					pst.setInt(18, 1);
					pst.setInt(19, uF.parseToInt(feedId));
	//				System.out.println("pst====>"+pst);
					int x = pst.executeUpdate();
					pst.close();
					
					String proDocumentId = "";
					pst = con.prepareStatement("select max(pro_document_id)as pro_document_id from project_document_details");
					rs = pst.executeQuery();
					while (rs.next()) {
						proDocumentId = rs.getString("pro_document_id");
					}
					rs.close();
					pst.close();
					
//					System.out.println("proNameFolder ======================== ===>> " + proNameFolder);
					uploadProjectDocuments(con, getStrDoc()[j], strFileName, proNameFolder, proDocumentId, feedId);
					

					/**
					 * Alerts
					 * */
					
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
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
						pst.setInt(1, uF.parseToInt(strOrgProjectDoc));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							proName = rs.getString("pro_name");
						}
						rs.close();
						pst.close();
					} else {
						pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
						pst.setInt(1, uF.parseToInt(strOrgCategoryDoc));
						rs = pst.executeQuery();
						while(rs.next()) {
							strCategory = rs.getString("project_category");
						}
						rs.close();
						pst.close();
					}
					Notifications nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrOrgId((String)session.getAttribute(ORGID));
					nF.setEmailTemplate(true);
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						nF.setStrProjectName(proName);
					} else {
						nF.setStrCategoryName(strCategory);
					}
					nF.setStrDocumentName(strDocumentName);
					
					String alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					String alertAction = "DocumentListView.action";
					for(String strEmp : alEmp) {
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
					for(String strEmp : alSharePoc) {
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
				}
			}
			
			
			String[] proCategoryTypeFolder = request.getParameterValues("proCategoryTypeFolder");
			String[] folderSharingType = request.getParameterValues("folderSharingType");
			String[] strFolderDescription = request.getParameterValues("strFolderDescription");
			
			String[] isFolderEdit = request.getParameterValues("isFolderEdit");
			String[] isFolderDelete = request.getParameterValues("isFolderDelete");
			
//			System.out.println("isFolderDelete ===>>>> " + isFolderDelete.length);
//			System.out.println("isFolderEdit ===>>>> " + isFolderEdit.length);
			int docCnt = 0;
			for (int i=0; getFolderTRId() != null && i<getFolderTRId().length; i++) {
				
				String strOrgCategory = request.getParameter("strOrgCategory"+getFolderTRId()[i]);
				String strOrgProject = request.getParameter("strOrgProject"+getFolderTRId()[i]);
				
				String proNameFolder = null;
				if(uF.parseToInt(proCategoryTypeFolder[i]) == 1) {
					folderWithCatORProName = CF.getProjectNameById(con, strOrgProject)+ " project";
					proNameFolder = mainProPath +"/"+(strOrgProject!=null ? strOrgProject : "0");
					File file1 = new File(proNameFolder);
					if (!file1.exists()) {
						if (file1.mkdir()) {
							System.out.println("pro_id Directory is created!");
						}
					} 
				} else if(uF.parseToInt(proCategoryTypeFolder[i]) == 2) {
					folderWithCatORProName = CF.getProjectCategory(con, uF, strOrgCategory)+ " category";
					proNameFolder = mainCatPath;
					File file1 = new File(proNameFolder);
					if (!file1.exists()) {
						if (file1.mkdir()) {
							System.out.println("cat_id Directory is created!");
						}
					}
				}
				
				String[] strOrgResources = request.getParameterValues("strOrgResources" + getFolderTRId()[i]);
				List<String> alEmployee = null;
				if(strOrgResources != null) {
					alEmployee = Arrays.asList(strOrgResources);
				}
				StringBuilder sbEmps = null;
				List<String> alEmp = new ArrayList<String>();
				for(int a=0; alEmployee != null && a<alEmployee.size(); a++) {
					if(alEmployee.get(a) != null && !alEmployee.get(a).trim().equals("")) {
						if(sbEmps == null) {
							sbEmps = new StringBuilder();
							sbEmps.append(","+ alEmployee.get(a).trim() +",");
						} else {
							sbEmps.append(alEmployee.get(a).trim() +",");
						}
						if(!alEmp.contains(alEmployee.get(a).trim())) {
							alEmp.add(alEmployee.get(a).trim());
						}
					}
				}
				if(sbEmps == null) {
					sbEmps = new StringBuilder();
				}
				
				String[] strOrgPoc = request.getParameterValues("strOrgPoc" + getFolderTRId()[i]);
				List<String> alPoc = null;
				if(strOrgPoc != null) {
					alPoc = Arrays.asList(strOrgPoc);
				}
				StringBuilder sbPoc = null;
				List<String> alSharePoc = new ArrayList<String>();
				for(int a=0; alPoc != null && a<alPoc.size(); a++) {
					if(alPoc.get(a) != null && !alPoc.get(a).trim().equals("")) {
						if(sbPoc == null) {
							sbPoc = new StringBuilder();
							sbPoc.append(","+ alPoc.get(a).trim() +",");
						} else {
							sbPoc.append(alPoc.get(a).trim() +",");
						}
						if(!alSharePoc.contains(alPoc.get(a).trim())) {
							alSharePoc.add(alPoc.get(a).trim());
						}
					}
				}
				if(sbPoc == null) {
					sbPoc = new StringBuilder();
				}
				
				String proFolderNameFolder = proNameFolder +"/"+ getStrFolderName()[i];
				
				File file2 = new File(proFolderNameFolder);
				if (!file2.exists()) {
					if (file2.mkdir()) {
						System.out.println("Directory is created!");
					}
				}
				
				pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by,entry_date,folder_file_type," +
					"pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description,doc_parent_id,is_edit," +
					"is_delete,is_cust_add,sharing_poc,doc_version) " +
					"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setInt(1, 0);
				if(uF.parseToInt(proCategoryTypeFolder[i]) == 1) {
					pst.setInt(2, uF.parseToInt(strOrgProject));
				} else {
					pst.setInt(2, 0);
				}
				pst.setString(3, getStrFolderName()[i]);
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(6, "folder");
				pst.setInt(7, 0);
				if(uF.parseToInt(proCategoryTypeFolder[i]) == 1) {
					pst.setInt(8, 0);
				} else {
					pst.setInt(8, uF.parseToInt(strOrgCategory));
				}
				pst.setInt(9, uF.parseToInt(folderSharingType[i]));
				pst.setString(10, sbEmps.toString());
				pst.setInt(11, uF.parseToInt(proCategoryTypeFolder[i]));
				pst.setString(12, null);
				pst.setString(13, strFolderDescription[i]);
				pst.setInt(14, 0);
				pst.setBoolean(15, uF.parseToBoolean(isFolderEdit[i]));
				pst.setBoolean(16, uF.parseToBoolean(isFolderDelete[i]));
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					pst.setBoolean(17, true);
				} else {
					pst.setBoolean(17, false);
				}
				pst.setString(18, sbPoc.toString());
				pst.setInt(19, 0);
//				System.out.println("pst====>"+pst);
				pst.execute();
				pst.close();
		
				String proFolderId = "";
				pst = con.prepareStatement("select max(pro_document_id)as pro_document_id from project_document_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					proFolderId = rs.getString("pro_document_id");
				}
				rs.close();
				pst.close();
				
				
				/**
				 * Alerts
				 * */
				
				String strDocumentName = "";
				pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
				pst.setInt(1, uF.parseToInt(proFolderId));
				rs = pst.executeQuery();
				while (rs.next()) {
					strDocumentName = rs.getString("folder_name");
				}
				rs.close();
				pst.close();
				
				String proName = null;
				String strCategory = null;
				if(uF.parseToInt(proCategoryTypeFolder[i]) == 1) {
					pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
					pst.setInt(1, uF.parseToInt(strOrgProject));
					rs = pst.executeQuery();
					
					while(rs.next()) {
						proName = rs.getString("pro_name");
					}
					rs.close();
					pst.close();
				} else {
					pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
					pst.setInt(1, uF.parseToInt(strOrgCategory));
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
				
				if(uF.parseToInt(proCategoryTypeFolder[i]) == 1) {
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
//					userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
					//Mail
					nF.setStrEmpId(strEmp.trim());
					nF.sendNotifications();
				}
				for(String strEmp : alSharePoc) {
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmp.trim());
					userAlerts.setStrOther("other");
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
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
				
				
				String[] folderDocsTRId = request.getParameterValues("folderDocsTRId" + getFolderTRId()[i]);
				
				File[] strFolderDoc = mpRequest.getFiles("strFolderDoc"+getFolderTRId()[i]);    //  
				String[] strFolderDocFileNames = mpRequest.getFileNames("strFolderDoc"+getFolderTRId()[i]);
				
				String[] strFolderScopeDoc = request.getParameterValues("strFolderScopeDoc" + getFolderTRId()[i]);
				String[] proCategoryTypeFolderDoc = request.getParameterValues("proCategoryTypeFolderDoc" + getFolderTRId()[i]);
				String[] folderDocSharingType = request.getParameterValues("folderDocSharingType" + getFolderTRId()[i]);
				String[] strFolderDocDescription = request.getParameterValues("strFolderDocDescription" + getFolderTRId()[i]);

				String[] isFolderDocEdit = request.getParameterValues("isFolderDocEdit" + getFolderTRId()[i]);
				String[] isFolderDocDelete = request.getParameterValues("isFolderDocDelete" + getFolderTRId()[i]);
				
				for(int j=0; folderDocsTRId != null && j<folderDocsTRId.length; j++) {
				
					String strOrgCategoryFolderDoc = request.getParameter("strOrgCategoryFolderDoc"+getFolderTRId()[i]+"_"+folderDocsTRId[j]);
					String strOrgProjectFolderDoc = request.getParameter("strOrgProjectFolderDoc"+getFolderTRId()[i]+"_"+folderDocsTRId[j]);
					
//					System.out.println("folderDocsTRId[j] ===>> " + folderDocsTRId[j]);
//					System.out.println("getFolderTRId()[i] ===>> " + getFolderTRId()[i]);
					double lengthBytes =  0;
					if(strFolderDoc!=null && strFolderDoc[j]!=null) {
						lengthBytes =  strFolderDoc[j].length();
					}
					boolean isFileExist = false;
					String strFileName = null;
					if(strFolderDocFileNames!=null && strFolderDocFileNames[j] !=null) {
						String extenstion=FilenameUtils.getExtension(strFolderDocFileNames[j]);	
						strFileName = FilenameUtils.getBaseName(strFolderDocFileNames[j]);
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
					}
					
					if(lengthBytes > 0 && !isFileExist) {
						String[] strOrgResourcesFolderDoc = request.getParameterValues("strOrgResourcesFolderDoc" + getFolderTRId()[i]+"_"+folderDocsTRId[j]);
						List<String> alFDEmployee = null;
						if(strOrgResourcesFolderDoc != null) {
							alFDEmployee = Arrays.asList(strOrgResourcesFolderDoc);
						}
						StringBuilder sbFDEmps = null;
						List<String> alFDEmp = new ArrayList<String>();
						for(int a=0; alFDEmployee != null && a<alFDEmployee.size(); a++) {
							if(alFDEmployee.get(a) != null && !alFDEmployee.get(a).trim().equals("")) {
								if(sbFDEmps == null) {
									sbFDEmps = new StringBuilder();
									sbFDEmps.append(","+ alFDEmployee.get(a).trim() +",");
								} else {
									sbFDEmps.append(alFDEmployee.get(a).trim() +",");
								}
								if(!alFDEmp.contains(alFDEmployee.get(a).trim())){
									alFDEmp.add(alFDEmployee.get(a).trim());
								}
							}
						}
						if(sbFDEmps == null) {
							sbFDEmps = new StringBuilder();
						}
						
						String[] strOrgPocFolderDoc = request.getParameterValues("strOrgPocFolderDoc" + getFolderTRId()[i]+"_"+folderDocsTRId[j]);
						List<String> alFDPoc = null;
						if(strOrgPocFolderDoc != null) {
							alFDPoc = Arrays.asList(strOrgPocFolderDoc);
						}
						StringBuilder sbFDPoc = null;
						List<String> alFDSharePoc = new ArrayList<String>();
						for(int a=0; alFDPoc != null && a<alFDPoc.size(); a++) {
							if(alFDPoc.get(a) != null && !alFDPoc.get(a).trim().equals("")) {
								if(sbFDPoc == null) {
									sbFDPoc = new StringBuilder();
									sbFDPoc.append(","+ alFDPoc.get(a).trim() +",");
								} else {
									sbFDPoc.append(alFDPoc.get(a).trim() +",");
								}
								if(!alFDSharePoc.contains(alFDPoc.get(a).trim())){
									alFDSharePoc.add(alFDPoc.get(a).trim());
								}
							}
						}
						if(sbFDPoc == null) {
							sbFDPoc = new StringBuilder();
						}
						
						pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
						"visibility,visibility_with_id,created_by,create_time) values(?,?,?,?, ?,?,?,?, ?,?)"); //,doc_or_image
						pst.setString(1, strFolderDocDescription[j]);
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(2, 1);
						} else {
							pst.setInt(2, 0);
						}
//						pst.setInt(2, uF.parseToInt(getStrAlignWith()));
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(3, uF.parseToInt(strOrgProjectFolderDoc));
						} else {
							pst.setInt(3, uF.parseToInt(strOrgCategoryFolderDoc));
						}
//						pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
						pst.setString(4, sbFDEmps.toString());
						pst.setString(5, "");
						pst.setString(6, "");
						pst.setInt(7, uF.parseToInt(folderDocSharingType[j]));
						pst.setString(8, sbFDEmps.toString());
						pst.setInt(9, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//						pst.setString(11, strFileName);
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
								"description,doc_parent_id,is_edit,is_delete,is_cust_add,sharing_poc,doc_version,feed_id)" +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setInt(1, 0);
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(2, uF.parseToInt(strOrgProjectFolderDoc));
						} else {
							pst.setInt(2, 0);
						}
						pst.setString(3, getStrFolderName()[i]);
						pst.setInt(4, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(6, "folder");
						pst.setInt(7, uF.parseToInt(proFolderId));
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(8, 0);
						} else {
							pst.setInt(8, uF.parseToInt(strOrgCategoryFolderDoc));
						}
						pst.setInt(9, uF.parseToInt(folderDocSharingType[j]));
						pst.setString(10, sbFDEmps.toString());
						pst.setInt(11, uF.parseToInt(proCategoryTypeFolderDoc[j]));
						pst.setString(12, strFolderScopeDoc[j]);
						pst.setString(13, strFolderDocDescription[j]);
						pst.setInt(14, 0);
						pst.setBoolean(15, uF.parseToBoolean(isFolderDocEdit[j]));
						pst.setBoolean(16, uF.parseToBoolean(isFolderDocDelete[j]));
						if(strUserType != null && strUserType.equals(CUSTOMER)) {
							pst.setBoolean(17, true);
						} else {
							pst.setBoolean(17, false);
						}
						pst.setString(18, sbFDPoc.toString());
						pst.setInt(19, 1);
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
						
						uploadProjectFolderDocuments(con, strFolderDoc[j], strFileName, proFolderNameFolder, proDocumentId, feedId);
						docCnt++;
						
						
						/**
						 * Alerts
						 * */
						
						String strDocumentName1 = "";
						pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
						pst.setInt(1, uF.parseToInt(proFolderId));
						rs = pst.executeQuery();
						while (rs.next()) {
							strDocumentName1 = rs.getString("folder_name");
						}
						rs.close();
						pst.close();
						
						String proName1 = null;
						String strCategory1 = null;
						if(uF.parseToInt(proCategoryTypeFolder[i]) == 1) {
							pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
							pst.setInt(1, uF.parseToInt(strOrgProject));
							rs = pst.executeQuery();
							
							while(rs.next()) {
								proName1 = rs.getString("pro_name");
							}
							rs.close();
							pst.close();
						} else {
							pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
							pst.setInt(1, uF.parseToInt(strOrgCategory));
							rs = pst.executeQuery();
							while(rs.next()) {
								strCategory1 = rs.getString("project_category");
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
						
						if(uF.parseToInt(proCategoryTypeFolder[i]) == 1) {
							nF.setStrProjectName(proName1);
						} else {
							nF.setStrCategoryName(strCategory1);
						}
						nF.setStrDocumentName(strDocumentName1);
						
						String alertData1 = "<div style=\"float: left;\"> <b>"+strDocumentName1+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						String alertAction1 = "DocumentListView.action";
						for(String strEmp : alFDEmp){
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrData(alertData1);
							userAlerts.setStrAction(alertAction1);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							//Mail
							nF.setStrEmpId(strEmp.trim());
							nF.sendNotifications();
						}
						for(String strEmp : alFDSharePoc){
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp.trim());
							userAlerts.setStrOther("other");
							userAlerts.setStrData(alertData1);
							userAlerts.setStrAction(alertAction1);
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
							userAlerts.setStatus(INSERT_ALERT);
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
				
				
				String[] subFolderTRId = request.getParameterValues("subFolderTRId" + getFolderTRId()[i]);
				String[] strSubFolderName = request.getParameterValues("strSubFolderName" + getFolderTRId()[i]);
				String[] proCategoryTypeSubFolder = request.getParameterValues("proCategoryTypeSubFolder" + getFolderTRId()[i]);
				String[] SubFolderSharingType = request.getParameterValues("SubFolderSharingType" + getFolderTRId()[i]);
				String[] strSubFolderDescription = request.getParameterValues("strSubFolderDescription" + getFolderTRId()[i]);
				
				String[] isSubFolderEdit = request.getParameterValues("isSubFolderEdit" + getFolderTRId()[i]);
				String[] isSubFolderDelete = request.getParameterValues("isSubFolderDelete" + getFolderTRId()[i]);
				
				for(int j = 0; subFolderTRId != null && j < subFolderTRId.length; j++) {
					
					String strOrgCategorySubFolder = request.getParameter("strOrgCategorySubFolder" + getFolderTRId()[i]+"_"+subFolderTRId[j]);
					String strOrgProjectSubFolder = request.getParameter("strOrgProjectSubFolder" + getFolderTRId()[i]+"_"+subFolderTRId[j]);
					
					String[] strOrgResourcesSubFolder = request.getParameterValues("strOrgResourcesSubFolder" + getFolderTRId()[i]+"_"+subFolderTRId[j]);
					List<String> alSubEmployee = null;
					if(strOrgResourcesSubFolder != null) {
						alSubEmployee = Arrays.asList(strOrgResourcesSubFolder);
					}
					StringBuilder sbSubEmps = null;
					List<String> alSubEmp = new ArrayList<String>();
					for(int a=0; alSubEmployee != null && a<alSubEmployee.size(); a++) {
						if(alSubEmployee.get(a) != null && !alSubEmployee.get(a).trim().equals("")) {
							if(sbSubEmps == null) {
								sbSubEmps = new StringBuilder();
								sbSubEmps.append(","+ alSubEmployee.get(a).trim() +",");
							} else {
								sbSubEmps.append(alSubEmployee.get(a).trim() +",");
							}
							if(!alSubEmp.contains(alSubEmployee.get(a).trim())){
								alSubEmp.add(alSubEmployee.get(a).trim());
							}
						}
					}
					if(sbSubEmps == null) {
						sbSubEmps = new StringBuilder();
					}
					
					String[] strOrgPocSubFolder = request.getParameterValues("strOrgPocSubFolder" + getFolderTRId()[i]+"_"+subFolderTRId[j]);
					List<String> alSubPoc = null;
					if(strOrgPocSubFolder != null) {
						alSubPoc = Arrays.asList(strOrgPocSubFolder);
					}
					StringBuilder sbSubPoc = null;
					List<String> alSubSharePoc = new ArrayList<String>();
					for(int a=0; alSubPoc != null && a<alSubPoc.size(); a++) {
						if(alSubPoc.get(a) != null && !alSubPoc.get(a).trim().equals("")) {
							if(sbSubPoc == null) {
								sbSubPoc = new StringBuilder();
								sbSubPoc.append(","+ alSubPoc.get(a).trim() +",");
							} else {
								sbSubPoc.append(alSubPoc.get(a).trim() +",");
							}
							if(!alSubSharePoc.contains(alSubPoc.get(a).trim())){
								alSubSharePoc.add(alSubPoc.get(a).trim());
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
							"description,doc_parent_id,is_edit,is_delete,is_cust_add,sharing_poc,doc_version)" +
							"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setInt(1, 0);
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						pst.setInt(2, uF.parseToInt(strOrgProjectSubFolder));
					} else {
						pst.setInt(2, 0);
					}
					pst.setString(3, strSubFolderName[j]);
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(6, "folder");
					pst.setInt(7, uF.parseToInt(proFolderId));
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						pst.setInt(8, 0);
					} else {
						pst.setInt(8, uF.parseToInt(strOrgCategorySubFolder));
					}
					pst.setInt(9, uF.parseToInt(SubFolderSharingType[j]));
					pst.setString(10, sbSubEmps.toString());
					pst.setInt(11, uF.parseToInt(proCategoryTypeSubFolder[j]));
					pst.setString(12, null);
					pst.setString(13, strSubFolderDescription[j]);
					pst.setInt(14, 0);
					pst.setBoolean(15, uF.parseToBoolean(isSubFolderEdit[j]));
					pst.setBoolean(16, uF.parseToBoolean(isSubFolderDelete[j]));
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						pst.setBoolean(17, true);
					} else {
						pst.setBoolean(17, false);
					}
					pst.setString(18, sbSubPoc.toString());
					pst.setInt(19, 0);
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
					
					String strDocumentName1 = "";
					pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
					pst.setInt(1, uF.parseToInt(proFolderId));
					rs = pst.executeQuery();
					while (rs.next()) {
						strDocumentName1 = rs.getString("folder_name");
					}
					rs.close();
					pst.close();
					
					String proName1 = null;
					String strCategory1 = null;
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
						pst.setInt(1, uF.parseToInt(strOrgProjectSubFolder));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							proName1 = rs.getString("pro_name");
						}
						rs.close();
						pst.close();
					} else {
						pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
						pst.setInt(1, uF.parseToInt(strOrgCategorySubFolder));
						rs = pst.executeQuery();
						while(rs.next()) {
							strCategory1 = rs.getString("project_category");
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
					
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						nF.setStrProjectName(proName1);
					} else {
						nF.setStrCategoryName(strCategory1);
					}
					nF.setStrDocumentName(strDocumentName1);
					
					String alertData1 = "<div style=\"float: left;\"> <b>"+strDocumentName1+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					String alertAction1 = "DocumentListView.action";
					for(String strEmp : alSubEmp){
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrData(alertData1);
						userAlerts.setStrAction(alertAction1);
//						userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						//Mail
						nF.setStrEmpId(strEmp.trim());
						nF.sendNotifications();
					}
					for(String strEmp : alSubSharePoc) {
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrOther("other");
						userAlerts.setStrData(alertData1);
						userAlerts.setStrAction(alertAction1);
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
					String[] subFolderDocsTRId = request.getParameterValues("subFolderDocsTRId"+subFolderTRId[j]);
					
					File[] strSubFolderDoc = mpRequest.getFiles("strSubFolderDoc"+subFolderTRId[j]);    //  
					String[] strSubFolderDocFileNames = mpRequest.getFileNames("strSubFolderDoc"+subFolderTRId[j]);
					String[] strSubFolderScopeDoc = request.getParameterValues("strSubFolderScopeDoc" + subFolderTRId[j]);
					
					String[] proCategoryTypeSubFolderDoc = request.getParameterValues("proCategoryTypeSubFolderDoc" +subFolderTRId[j]);
					String[] SubFolderDocSharingType = request.getParameterValues("SubFolderDocSharingType" + subFolderTRId[j]);
					String[] strSubFolderDocDescription = request.getParameterValues("strSubFolderDocDescription" +subFolderTRId[j]);
					
					String[] isSubFolderDocEdit = request.getParameterValues("isSubFolderDocEdit" + subFolderTRId[j]);
					String[] isSubFolderDocDelete = request.getParameterValues("isSubFolderDocDelete" + subFolderTRId[j]);
					
					for(int k=0; subFolderDocsTRId != null && k < subFolderDocsTRId.length; k++) {
						String strOrgCategorySubFolderDoc = request.getParameter("strOrgCategorySubFolderDoc" + subFolderTRId[j]+"_"+subFolderDocsTRId[k]);
						String strOrgProjectSubFolderDoc = request.getParameter("strOrgProjectSubFolderDoc" + subFolderTRId[j]+"_"+subFolderDocsTRId[k]);
						
						double lengthBytes =  0;
						if(strSubFolderDoc!=null && strSubFolderDoc[k]!=null) {
							lengthBytes =  strSubFolderDoc[k].length();
						}
						boolean isFileExist = false;
//						System.out.println("fileNames[k] ===>> " + fileNames[k] + " ----- " + proSubFolderNameFolder+"/"+fileNames[k]);
						String strFileName = null;
						if(strSubFolderDocFileNames!=null && strSubFolderDocFileNames[k]!=null) {
							String extenstion=FilenameUtils.getExtension(strSubFolderDocFileNames[k]);	
							strFileName = FilenameUtils.getBaseName(strSubFolderDocFileNames[k]);
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
						}
						
						if(lengthBytes > 0 && !isFileExist) {
							String[] proFolderDocEmployee = request.getParameterValues("strOrgResourcesSubFolderDoc" + subFolderTRId[j]+"_"+subFolderDocsTRId[k]);
							List<String> alFDEmployee = null;
							if(proFolderDocEmployee != null) {
								alFDEmployee = Arrays.asList(proFolderDocEmployee);
							}
							StringBuilder sbFDEmps = null;
							List<String> alFDEmp = new ArrayList<String>();
							for(int a=0; alFDEmployee != null && a<alFDEmployee.size(); a++) {
								if(alFDEmployee.get(a) != null && !alFDEmployee.get(a).trim().equals("")) {
									if(sbFDEmps == null) {
										sbFDEmps = new StringBuilder();
										sbFDEmps.append(","+ alFDEmployee.get(a).trim() +",");
									} else {
										sbFDEmps.append(alFDEmployee.get(a).trim() +",");
									}
									if(!alFDEmp.contains(alFDEmployee.get(a).trim())){
										alFDEmp.add(alFDEmployee.get(a).trim());
									}
								}
							}
							if(sbFDEmps == null) {
								sbFDEmps = new StringBuilder();
							}
							
							String[] proFolderDocPoc = request.getParameterValues("strOrgPocSubFolderDoc" + subFolderTRId[j]+"_"+subFolderDocsTRId[k]);
							List<String> alFDPoc = null;
							if(proFolderDocPoc != null) {
								alFDPoc = Arrays.asList(proFolderDocPoc);
							}
							StringBuilder sbFDPoc = null;
							List<String> alFDSharePoc = new ArrayList<String>();
							for(int a=0; alFDPoc != null && a<alFDPoc.size(); a++) {
								if(alFDPoc.get(a) != null && !alFDPoc.get(a).trim().equals("")) {
									if(sbFDPoc == null) {
										sbFDPoc = new StringBuilder();
										sbFDPoc.append(","+ alFDPoc.get(a).trim() +",");
									} else {
										sbFDPoc.append(alFDPoc.get(a).trim() +",");
									}
									if(!alFDSharePoc.contains(alFDPoc.get(a).trim())){
										alFDSharePoc.add(alFDPoc.get(a).trim());
									}
								}
							}
							if(sbFDPoc == null) {
								sbFDPoc = new StringBuilder();
							}
							
							
							pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
							"visibility,visibility_with_id,created_by,create_time) values(?,?,?,?, ?,?,?,?, ?,?)"); //,doc_or_image
							pst.setString(1, strSubFolderDocDescription[k]);
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(2, 1);
							} else {
								pst.setInt(2, 0);
							}
//							pst.setInt(2, uF.parseToInt(getStrAlignWith()));
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(3, uF.parseToInt(strOrgProjectSubFolderDoc));
							} else {
								pst.setInt(3, uF.parseToInt(strOrgCategorySubFolderDoc));
							}
//							pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
							pst.setString(4, sbFDEmps.toString());
							pst.setString(5, "");
							pst.setString(6, "");
							pst.setInt(7, uF.parseToInt(SubFolderDocSharingType[k]));
							pst.setString(8, sbFDEmps.toString());
							pst.setInt(9, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//							pst.setString(11, strFileName);
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
									"description,doc_parent_id,is_edit,is_delete,is_cust_add,sharing_poc,doc_version,feed_id)" +
									"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
							pst.setInt(1, 0);
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k])== 1) {
								pst.setInt(2, uF.parseToInt(strOrgProjectSubFolderDoc));
							} else {
								pst.setInt(2, 0);
							}
							pst.setString(3, strSubFolderName[j]);
							pst.setInt(4, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//							pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setString(6, "folder");
							pst.setInt(7, uF.parseToInt(proSubFolderId));
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k])== 1) {
								pst.setInt(8, 0);
							} else {
								pst.setInt(8, uF.parseToInt(strOrgCategorySubFolderDoc));
							}
							pst.setInt(9, uF.parseToInt(SubFolderDocSharingType[k]));
							pst.setString(10, sbFDEmps.toString());
							pst.setInt(11, uF.parseToInt(proCategoryTypeSubFolderDoc[k]));
							pst.setString(12, strSubFolderScopeDoc[k]);
							pst.setString(13, strSubFolderDocDescription[k]);
							pst.setInt(14, 0);
							pst.setBoolean(15, uF.parseToBoolean(isSubFolderDocEdit[k]));
							pst.setBoolean(16, uF.parseToBoolean(isSubFolderDocDelete[k]));
							if(strUserType != null && strUserType.equals(CUSTOMER)) {
								pst.setBoolean(17, true);
							} else {
								pst.setBoolean(17, false);
							}
							pst.setString(18, sbFDPoc.toString());
							pst.setInt(19, 1);
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
							
							uploadProjectFolderDocuments(con, strSubFolderDoc[k], strFileName, proSubFolderNameFolder, proDocumentId, feedId);
							
							/**
							 * Alerts
							 * */
							
							strDocumentName = "";
							pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
							pst.setInt(1, uF.parseToInt(proDocumentId));
							rs = pst.executeQuery();
							while (rs.next()) {
								strDocumentName = rs.getString("folder_name");
							}
							rs.close();
							pst.close();
							
							proName = null;
							strCategory = null;
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
								pst.setInt(1, uF.parseToInt(strOrgProjectSubFolderDoc));
								rs = pst.executeQuery();
								
								while(rs.next()) {
									proName = rs.getString("pro_name");
								}
								rs.close();
								pst.close();
							} else {
								pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
								pst.setInt(1, uF.parseToInt(strOrgCategorySubFolderDoc));
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
								nF.setStrProjectName(proName);
							} else {
								nF.setStrCategoryName(strCategory);
							}
							nF.setStrDocumentName(strDocumentName);
							
							String alertData2 = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
							String alertAction2 = "DocumentListView.action";
							for(String strEmp : alFDEmp) {
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strEmp.trim());
								userAlerts.setStrData(alertData2);
								userAlerts.setStrAction(alertAction2);
//								userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
								userAlerts.setStatus(INSERT_TR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
								
								//Mail
								nF.setStrEmpId(strEmp.trim());
								nF.sendNotifications();
							}
							
							for(String strEmp : alFDSharePoc) {
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strEmp.trim());
								userAlerts.setStrOther("other");
								userAlerts.setStrData(alertData2);
								userAlerts.setStrAction(alertAction2);
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
							
							
							docCnt++;
						}
					}
				}
			}
			
			int docuCnt = getDocsTRId()!=null ? getDocsTRId().length : 0;
			int foldCnt = getFolderTRId()!=null ? getFolderTRId().length : 0;
			if(docuCnt>0 && foldCnt>0) {
				session.setAttribute(MESSAGE, SUCCESSM+docuCnt+" document added for "+docWithCatORProName+" and "+foldCnt+" folder created for "+folderWithCatORProName+" successfully."+END);
			} else if(foldCnt>0) {
				session.setAttribute(MESSAGE, SUCCESSM+foldCnt+" folder created for "+folderWithCatORProName+" successfully."+END);
			} else if(docuCnt>0) {
				session.setAttribute(MESSAGE, SUCCESSM+docuCnt+" document added for "+docWithCatORProName+" successfully."+END);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void uploadProjectFolderDocuments(Connection con, File contentFile, String contentFileName, String realFolderPath, String proDocumentId, String feedId) {

		PreparedStatement pst = null;
		try {
			
			double lengthBytes =  0;
			if(contentFile!=null) {
				lengthBytes =  contentFile.length();
			}
			
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
	
	
	private void uploadProjectDocuments(Connection con, File contentFile, String contentFileName, String realFolderPath, String proDocumentId, String feedId) {

		PreparedStatement pst = null;
		try {
			
			double lengthBytes =  0;
			if(contentFile!=null) {
				lengthBytes =  contentFile.length();
			}
			
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

	public void getProjectDocumentDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
//			System.out.println("getType() ===>> " + getType());
//			System.out.println("getStrId() ===>> " + getStrId());
			
			Map<String, String> hmFileIcon = CF.getFileIcon();
			request.setAttribute("hmFileIcon", hmFileIcon);
			
//			System.out.println("getStrSearchDoc() ===>> " + getStrSearchDoc());
			
			if(getType() != null && (getType().equals("C") || getType().equals("P"))) {
				if(getType().equals("C") && uF.parseToInt(getStrId()) == 0 && (getStrSearchDoc() !=null && !getStrSearchDoc().trim().equals("") && !getStrSearchDoc().trim().equalsIgnoreCase("NULL"))) { 
					getAllSearchFolderSubFolderAndFiles(uF);
				} else if(getType().equals("C") && uF.parseToInt(getStrId()) == 0) {
					getAllFolderSubFolderAndFiles(uF);
				} else {
					getCatagoryORProjectFolderFiles(uF);
				}
			} else if(getType() != null && (getType().equals("CF") || getType().equals("PF") || getType().equals("AF"))) {
				getFolderSubFolderAndFiles(uF);
			} else if(getType()!=null && (getType().equals("CSF") || getType().equals("PSF") || getType().equals("ASF"))) {
				getSubFolderFiles(uF);
			}
			
			StringBuilder sbProTasks = new StringBuilder();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select task_id,activity_name,parent_task_id from activity_info where pro_id=? and task_id not in " +
					"(select parent_task_id from activity_info where pro_id=? and parent_task_id is not null)");
			if(getFromPage() != null && getFromPage().equalsIgnoreCase("MyProject")) {
				sbQuery.append(" and task_id = "+uF.parseToInt(taskId)+"");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, getProId());
			pst.setInt(2, getProId());
			rs = pst.executeQuery();
			while(rs.next()) {
				if(uF.parseToInt(rs.getString("parent_task_id")) > 0) {
					sbProTasks.append("<option value='" + rs.getString("task_id") + "'>" + rs.getString("activity_name") + " [ST]"+"</option>");
				} else {
					sbProTasks.append("<option value='" + rs.getString("task_id") + "'>" + rs.getString("activity_name") + "</option>");
				}
			}
			rs.close();
			pst.close();
//			sbProTasks.append("</select>");
			request.setAttribute("sbProTasks", sbProTasks.toString());
			
//			StringBuilder sbProEmp = new StringBuilder("<select name=\"proEmployee\" id=\"proEmployee\" style=\"width:160px\" multiple size=\"3\"><option >Select Resource</option>");
			StringBuilder sbProEmp = new StringBuilder();
			pst = con.prepareStatement("select * from project_emp_details ped, employee_personal_details epd where ped.emp_id = epd.emp_per_id and pro_id=?");
			pst.setInt(1, getProId());
			rs = pst.executeQuery();
			while(rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				sbProEmp.append("<option value='" + rs.getString("emp_id") + "'>" + rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + "</option>");
			}
			rs.close();
			pst.close();
//			sbProEmp.append("</select>");
			request.setAttribute("sbProEmp", sbProEmp.toString());
			
//			StringBuilder sbProCategory = new StringBuilder("<select name=\"proTasks\" id=\"proTasks\" style=\"width:100px\"><option >Select Category</option>");
			StringBuilder sbProCategory = new StringBuilder();
			pst = con.prepareStatement("select * from project_category_details where org_id=? and project_category_id>1 order by project_category");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while(rs.next()) {
				
				sbProCategory.append("<option value='" + rs.getString("project_category_id") + "'>" + rs.getString("project_category").trim()+ "</option>");
			}
			rs.close();
			pst.close();
//			sbProEmp.append("</select>");
			request.setAttribute("sbProCategory", sbProCategory.toString());
			
			StringBuilder sbProSPOC = new StringBuilder();
			pst = con.prepareStatement("select poc_id, contact_fname, contact_lname from client_poc where poc_id in (select poc from projectmntnc " +
					"where org_id=?) order by contact_fname,contact_lname");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while(rs.next()) {
				sbProSPOC.append("<option value='" + rs.getString("poc_id") + "'>" + uF.showData(rs.getString("contact_fname"), "").trim()+" "+uF.showData(rs.getString("contact_lname"), "").trim()+ "</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbProSPOC", sbProSPOC.toString());
			
//			System.out.println("sbProTasks======>"+sbProTasks.toString());
//			System.out.println("sbProEmp======>"+sbProEmp.toString());
//			System.out.println("sbProCategory======>"+sbProCategory.toString());
			
		} catch (Exception e) {
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	  
	private void getAllSearchFolderSubFolderAndFiles(UtilityFunctions uF) {
//		System.out.println("getAllSearchFolderSubFolderAndFiles====>>>>> ");
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String orgName = CF.getStrOrgName();
			request.setAttribute("orgName", orgName);
			
			String proDocMainPath = CF.getProjectDocumentFolder();
			request.setAttribute("proDocMainPath", proDocMainPath);
			
			String proDocRetrivePath = CF.getRetriveProjectDocumentFolder();
			request.setAttribute("proDocRetrivePath", proDocRetrivePath);
			request.setAttribute("strOrgId", strOrgId);
			Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, getProId()+"");
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from project_document_details where pro_folder_id = 0 and (file_size is null or file_size ='') " +
					"and align_with in(select project_category_id from project_category_details where org_id=? " +
					"and upper(project_category) like'%"+getStrSearchDoc().trim().toUpperCase()+"%') and (project_category=2 or project_category=0) ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbQuery.append(" union ");
			sbQuery.append("select * from project_document_details where pro_folder_id = 0 and (file_size is null or file_size ='') " +
					"and pro_id in (select pro_id from projectmntnc where org_id=? and upper(pro_name)  like '%"+getStrSearchDoc().trim().toUpperCase()+"%') and project_category=1 ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 or " +
						"(sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+"))) ");
			}else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 or " +
						"(sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%' )))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbQuery.append(" union ");
			sbQuery.append("select * from project_document_details where (file_size is null or file_size ='') and upper(folder_name) like '%"+getStrSearchDoc().trim().toUpperCase()+"%'");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			}else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbQuery.append("and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%' )))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strOrgId));
			pst.setInt(2, uF.parseToInt(strOrgId));
//			System.out.println("pst 1 ====>>>>> " + pst);
			rs = pst.executeQuery();
			StringBuilder sbMainProDocId = null;
			while (rs.next()) {
				String strProDocId = uF.parseToInt(rs.getString("pro_folder_id")) == 0 ?  rs.getString("pro_document_id") : rs.getString("pro_folder_id");
				if(sbMainProDocId == null){
					sbMainProDocId = new StringBuilder();
					sbMainProDocId.append(strProDocId);
				} else {
					sbMainProDocId.append(","+strProDocId);
				}
			}
			rs.close();
			pst.close();
			
			if(sbMainProDocId !=null){
				sbQuery = new StringBuilder();
				sbQuery.append("select * from project_document_details where pro_folder_id = 0 and (file_size is null or file_size ='') " +
						"and pro_document_id in ("+sbMainProDocId.toString()+") ");
				if(strUserType != null && strUserType.equals(EMPLOYEE)){
					sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
				}else if(strUserType != null && strUserType.equals(MANAGER)){
				//===start parvez date: 13-10-2022===	
					sbQuery.append("and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
							"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//							"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
							"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
				//===end parvez date: 13-10-2022===	
				} else if(strUserType != null && strUserType.equals(CUSTOMER)){
					sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
				}
				sbQuery.append(" order by pro_document_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst 2 ====>>>>> " + pst);
				rs = pst.executeQuery();
				List<Map<String, String>> alProFolder = new ArrayList<Map<String,String>>();
				StringBuilder sbProjectDocId = null;
				while (rs.next()) {
					
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
					hmInner.put("FOLDER_NAME", rs.getString("folder_name"));
					hmInner.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
					hmInner.put("DOCUMENT_NAME", rs.getString("document_name"));
					hmInner.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
					hmInner.put("PRO_ID", rs.getString("pro_id"));
					hmInner.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
	//				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
					hmInner.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
							+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
					hmInner.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
					hmInner.put("CLIENT_ID", rs.getString("client_id"));
					hmInner.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
					hmInner.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
					hmInner.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
					
					if(uF.parseToInt(rs.getString("project_category")) == 1) {
						hmInner.put("CATEGORY", "Project");
						String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
						hmInner.put("ALIGN", uF.showData(strAlign, "-")+", Project");
					} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
						hmInner.put("CATEGORY", "Category");
						String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
						hmInner.put("ALIGN", uF.showData(strOther, "-")+", Category");
					} else {
						hmInner.put("CATEGORY", "Uncategorised");
						hmInner.put("ALIGN", "Uncategorised");
					}
					
					hmInner.put("DOC_VERSION", rs.getString("doc_version"));
					
					if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
						hmInner.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
					} else {
						hmInner.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
					}
					hmInner.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
					alProFolder.add(hmInner);
					
					if(sbProjectDocId == null){
						sbProjectDocId = new StringBuilder();
						sbProjectDocId.append(rs.getString("pro_document_id"));
					} else {
						sbProjectDocId.append(","+rs.getString("pro_document_id"));
					}
				}
				rs.close();
				pst.close(); 
	//			System.out.println("hmFolder ====>>>>> " + hmFolder);
				request.setAttribute("alProFolder", alProFolder);
				
				
				Map<String, List<Map<String,String>>> hmSubFolder = new HashMap<String, List<Map<String,String>>>();
				Map<String, List<Map<String,String>>> hmSubDoc = new HashMap<String, List<Map<String,String>>>();
				StringBuilder sbSFQue = new StringBuilder();
				sbSFQue.append("select * from project_document_details where pro_folder_id > 0 ");
				if(sbProjectDocId != null) {
					sbSFQue.append(" and pro_folder_id in ("+sbProjectDocId.toString()+")");
				}
				if(strUserType != null && strUserType.equals(EMPLOYEE)){
					sbSFQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
				}else if(strUserType != null && strUserType.equals(MANAGER)){
				//===start parvez date: 13-10-2022===	
					sbSFQue.append("and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
							"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//							"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
							"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
				//===end parvez date: 13-10-2022===	
				} else if(strUserType != null && strUserType.equals(CUSTOMER)){
					sbSFQue.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
				}
				sbSFQue.append(" and (file_size is null or file_size ='') order by pro_document_id");
				pst = con.prepareStatement(sbSFQue.toString());
//				System.out.println("pst 4 ====>>>>> " + pst);
				rs = pst.executeQuery();
				StringBuilder sbSubFolderIds = null;
				while (rs.next()) {
					List<Map<String, String>> alFolder = (List<Map<String, String>>) hmSubFolder.get(rs.getString("pro_folder_id")); 
					if(alFolder == null) alFolder = new ArrayList<Map<String,String>>();
					
					if(sbSubFolderIds == null) {
						sbSubFolderIds = new StringBuilder();
						sbSubFolderIds.append(rs.getString("pro_document_id"));
					} else {
						sbSubFolderIds.append(","+rs.getString("pro_document_id"));
					}
					
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
					hmInner.put("FOLDER_NAME", rs.getString("folder_name"));
					hmInner.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
					hmInner.put("DOCUMENT_NAME", rs.getString("document_name"));
					hmInner.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
					hmInner.put("PRO_ID", rs.getString("pro_id"));
					hmInner.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
	//				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
					hmInner.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
							+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
					hmInner.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
					hmInner.put("CLIENT_ID", rs.getString("client_id"));
					hmInner.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
					hmInner.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
					hmInner.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
					
					if(uF.parseToInt(rs.getString("project_category")) == 1) {
						hmInner.put("CATEGORY", "Project");
						String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
						hmInner.put("ALIGN", uF.showData(strAlign, "-")+", Project");
					} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
						hmInner.put("CATEGORY", "Category");
						String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
						hmInner.put("ALIGN", uF.showData(strOther, "-")+", Category");
					} else {
						hmInner.put("CATEGORY", "Uncategorised");
						hmInner.put("ALIGN", "Uncategorised");
					}
					hmInner.put("DOC_VERSION", rs.getString("doc_version"));
					if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
						hmInner.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
					} else {
						hmInner.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
					}
					hmInner.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
					alFolder.add(hmInner);
					hmSubFolder.put(rs.getString("pro_folder_id"), alFolder);
					
				}
				rs.close();
				pst.close();
	//			System.out.println("hmSubFolder ====>>>>> " + hmSubFolder);
				request.setAttribute("hmSubFolder", hmSubFolder);
				
				StringBuilder sbSFDocQue = new StringBuilder();
				sbSFDocQue.append("select * from project_document_details where pro_folder_id > 0 ");
				if(sbProjectDocId != null && sbSubFolderIds != null) {
					sbSFDocQue.append(" and (pro_folder_id in ("+sbProjectDocId.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
				} else if(sbProjectDocId != null && sbSubFolderIds == null) {
					sbSFDocQue.append(" and pro_folder_id in ("+sbProjectDocId.toString()+") ");
				} else if(sbSubFolderIds != null) {
					sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
				}
				sbSFDocQue.append(" and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from " +
					"project_document_details where pro_folder_id > 0 ");
				if(sbProjectDocId != null && sbSubFolderIds != null) {
					sbSFDocQue.append(" and (pro_folder_id in ("+sbProjectDocId.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
				} else if(sbProjectDocId != null && sbSubFolderIds == null) {
					sbSFDocQue.append(" and pro_folder_id in ("+sbProjectDocId.toString()+") ");
				} else if(sbSubFolderIds != null) {
					sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
				}
				sbSFDocQue.append(" and (file_size is not null or file_size !='')) and doc_parent_id = 0 ");
				if(strUserType != null && strUserType.equals(EMPLOYEE)){
					sbSFDocQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
				}else if(strUserType != null && strUserType.equals(MANAGER)){
				//===start parvez date: 13-10-2022===	
					sbSFDocQue.append("and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
							"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//							"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
							"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%' )))) ");
				//===end parvez date: 13-10-2022==	
				} else if(strUserType != null && strUserType.equals(CUSTOMER)){
					sbSFDocQue.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
				}
				sbSFDocQue.append(" order by pro_document_id ");
				pst = con.prepareStatement(sbSFDocQue.toString());
//				System.out.println("pst 5 ====>>>>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(rs.getString("pro_folder_id")); 
					if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
					
					Map<String, String> hmInnerDoc = new HashMap<String, String>();
					hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
					hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
					hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
					hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
					hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
					hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
					hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
	//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
					hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
							+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
					hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
					hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
					hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
					hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
					hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
					
					if(uF.parseToInt(rs.getString("project_category")) == 1) {
						hmInnerDoc.put("CATEGORY", "Project");
						String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
						hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
					} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
						hmInnerDoc.put("CATEGORY", "Category");
						String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
						hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
					} else {
						hmInnerDoc.put("CATEGORY", "-");
						hmInnerDoc.put("ALIGN", "-");
					}
					
					if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
						hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
					} else {
						hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
					}
					hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
					
					hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
					String extenstion = null;
					if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
						extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
					}
					hmInnerDoc.put("FILE_EXTENSION", extenstion);
					
					alDoc.add(hmInnerDoc);
					hmSubDoc.put(rs.getString("pro_folder_id"), alDoc);
				}
				rs.close();
				pst.close();
	//			System.out.println("1 hmSubDoc ====>>>>> " + hmSubDoc);
				
				sbSFDocQue = new StringBuilder();
				sbSFDocQue.append("select * from (select * from (select * from project_document_details where pro_folder_id > 0 ");
				if(sbProjectDocId != null && sbSubFolderIds != null) {
					sbSFDocQue.append(" and (pro_folder_id in ("+sbProjectDocId.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
				} else if(sbProjectDocId != null && sbSubFolderIds == null) {
					sbSFDocQue.append(" and pro_folder_id in ("+sbProjectDocId.toString()+") ");
				} else if(sbSubFolderIds != null) {
					sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
				}
				sbSFDocQue.append(" and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from " +
						"project_document_details where pro_folder_id > 0  ");
				if(sbProjectDocId != null && sbSubFolderIds != null) {
					sbSFDocQue.append(" and (pro_folder_id in ("+sbProjectDocId.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
				} else if(sbProjectDocId != null && sbSubFolderIds == null) {
					sbSFDocQue.append(" and pro_folder_id in ("+sbProjectDocId.toString()+") ");
				} else if(sbSubFolderIds != null) {
					sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
				}
				sbSFDocQue.append(" and (file_size is not null or file_size !='')) and doc_parent_id > 0 ) a, (select max(pro_document_id) as " +
						"pro_document_id from project_document_details where pro_folder_id > 0 ");
				if(sbProjectDocId != null && sbSubFolderIds != null) {
					sbSFDocQue.append(" and (pro_folder_id in ("+sbProjectDocId.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
				} else if(sbProjectDocId != null && sbSubFolderIds == null) {
					sbSFDocQue.append(" and pro_folder_id in ("+sbProjectDocId.toString()+") ");
				} else if(sbSubFolderIds != null) {
					sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
				}
				sbSFDocQue.append(" and (file_size is not null or file_size !='') and doc_parent_id > 0 group by doc_parent_id) b where a.pro_document_id=b.pro_document_id) ab ");
				if(strUserType != null && strUserType.equals(EMPLOYEE)){
					sbSFDocQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
				}else if(strUserType != null && strUserType.equals(MANAGER)){
				//===start parvez date: 13-10-2022===	
					sbSFDocQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
							"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//							"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
							"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%' )))) ");
				//===end parvez date: 13-10-2022===	
				} else if(strUserType != null && strUserType.equals(CUSTOMER)){
					sbSFDocQue.append(" where sharing_poc like '%,"+strSessionEmpId+",%'");
				}
				pst = con.prepareStatement(sbSFDocQue.toString());
//				System.out.println("pst 6 ====>>>>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(rs.getString("pro_folder_id")); 
					if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
					
					Map<String, String> hmInnerDoc = new HashMap<String, String>();
					hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
					hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
					hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
					hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
					hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
					hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
					hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
	//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
					hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
					hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
					hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
					hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
					hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
					hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
					
					if(uF.parseToInt(rs.getString("project_category")) == 1) {
						hmInnerDoc.put("CATEGORY", "Project");
						String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
						hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
					} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
						hmInnerDoc.put("CATEGORY", "Category");
						String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
						hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
					} else {
						hmInnerDoc.put("CATEGORY", "-");
						hmInnerDoc.put("ALIGN", "-");
					}
					
					if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
						hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
					} else {
						hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
					}
					hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
					
					hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
					String extenstion = null;
					if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
						extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
					}
					hmInnerDoc.put("FILE_EXTENSION", extenstion);
					
					alDoc.add(hmInnerDoc);
					hmSubDoc.put(rs.getString("pro_folder_id"), alDoc);
				}
				rs.close();
				pst.close();
	//			System.out.println("2 hmSubDoc ====>>>>> " + hmSubDoc);
				
				request.setAttribute("hmSubDoc", hmSubDoc);
			}
			
			StringBuilder sbFDQue = new StringBuilder();
			sbFDQue.append("select * from project_document_details where pro_folder_id = 0 and (file_size is not null or file_size !='') " +
					"and align_with in(select project_category_id from project_category_details where org_id=? and upper(project_category) like'%"+getStrSearchDoc().trim().toUpperCase()+"%') " +
					"and (project_category=2 or project_category=0) and doc_parent_id = 0 and pro_document_id not in (select doc_parent_id from project_document_details" +
					" where pro_folder_id = 0 and (file_size is not null or file_size !='')) ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbFDQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
				sbFDQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbFDQue.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbFDQue.append(" union ");
			sbFDQue.append("select * from project_document_details where pro_folder_id = 0 and (file_size is not null or file_size !='') " +
					"and pro_id in (select pro_id from projectmntnc where org_id=? and upper(pro_name)  like '%"+getStrSearchDoc().trim().toUpperCase()+"%') and project_category=1  " +
					"and doc_parent_id = 0 and pro_document_id not in (select doc_parent_id from project_document_details where pro_folder_id = 0 " +
					"and (file_size is not null or file_size !='')) ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbFDQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbFDQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%' )))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbFDQue.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbFDQue.append(" union ");
			sbFDQue.append(" select * from (");
			sbFDQue.append("select aa.* from (select * from project_document_details where pro_folder_id = 0 and (file_size is not null or file_size !='') " +
					"and align_with in(select project_category_id from project_category_details where org_id=? and upper(project_category) like'%"+getStrSearchDoc().trim().toUpperCase()+"%') " +
					"and (project_category=2 or project_category=0)" +
					" union " +
					"select * from project_document_details where pro_folder_id = 0 and (file_size is not null or file_size !='') " +
					"and pro_id in (select pro_id from projectmntnc where org_id=? and upper(pro_name)  like '%"+getStrSearchDoc().trim().toUpperCase()+"%') and project_category=1 ) aa," +
					"(select a.* from (select * from project_document_details where pro_folder_id = 0 and (file_size is not null or file_size !='') " +
					"and pro_document_id not in (select doc_parent_id from project_document_details where pro_folder_id = 0 " +
					"and (file_size is not null or file_size !='')) and doc_parent_id > 0) a, (select max(pro_document_id) as pro_document_id1 " +
					"from project_document_details where pro_folder_id = 0 and (file_size is not null or file_size !='') and doc_parent_id > 0 " +
					"group by doc_parent_id) b where a.pro_document_id=b.pro_document_id1) cc where aa.pro_document_id=cc.pro_document_id) ab");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbFDQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbFDQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%' )))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbFDQue.append(" where sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			pst = con.prepareStatement(sbFDQue.toString());
			pst.setInt(1, uF.parseToInt(strOrgId));
			pst.setInt(2, uF.parseToInt(strOrgId));
			pst.setInt(3, uF.parseToInt(strOrgId));
			pst.setInt(4, uF.parseToInt(strOrgId));
//			System.out.println("pst doc search ====>>>>> " + pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alMainDoc = new ArrayList<Map<String,String>>();
			while (rs.next()) {
				Map<String, String> hmInnerDoc = new HashMap<String, String>();
				hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
				hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
				hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInnerDoc.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInnerDoc.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInnerDoc.put("CATEGORY", "-");
					hmInnerDoc.put("ALIGN", "-");
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				
				hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmInnerDoc.put("FILE_EXTENSION", extenstion);
				
				alMainDoc.add(hmInnerDoc);
			}
			rs.close();
			pst.close();
			request.setAttribute("alMainDoc", alMainDoc);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	/*sbFDQue.append("select * from project_document_details where pro_folder_id = 0 and (file_size is not null or file_size !='') " +
			"and align_with in(select project_category_id from project_category_details where org_id=? and upper(project_category) like'%"+getStrSearchDoc().trim().toUpperCase()+"%') " +
			"and project_category=2 and doc_parent_id = 0 and pro_document_id not in (select doc_parent_id from project_document_details" +
			" where pro_folder_id = 0 and (file_size is not null or file_size !='')) " +
			" union " +
			" select * from project_document_details where pro_folder_id = 0 and (file_size is not null or file_size !='') " +
			"and pro_id in (select pro_id from projectmntnc where org_id=? and upper(pro_name)  like '%"+getStrSearchDoc().trim().toUpperCase()+"%') and project_category=1  " +
			"and doc_parent_id = 0 and pro_document_id not in (select doc_parent_id from project_document_details where pro_folder_id = 0 " +
			"and (file_size is not null or file_size !=''))" +
			" union " +
			"select aa.* from (select * from project_document_details where pro_folder_id = 0 and (file_size is not null or file_size !='') " +
			"and align_with in(select project_category_id from project_category_details where org_id=? and upper(project_category) like'%"+getStrSearchDoc().trim().toUpperCase()+"%') " +
			"and project_category=2" +
			" union " +
			"select * from project_document_details where pro_folder_id = 0 and (file_size is not null or file_size !='') " +
			"and pro_id in (select pro_id from projectmntnc where org_id=? and upper(pro_name)  like '%"+getStrSearchDoc().trim().toUpperCase()+"%') and project_category=1 ) aa," +
			"(select a.* from (select * from project_document_details where pro_folder_id = 0 and (file_size is not null or file_size !='') " +
			"and pro_document_id not in (select doc_parent_id from project_document_details where pro_folder_id = 0 " +
			"and (file_size is not null or file_size !='')) and doc_parent_id > 0) a, (select max(pro_document_id) as pro_document_id1 " +
			"from project_document_details where pro_folder_id = 0 and (file_size is not null or file_size !='') and doc_parent_id > 0 " +
			"group by doc_parent_id) b where a.pro_document_id=b.pro_document_id1) cc where aa.pro_document_id=cc.pro_document_id");*/
	

	private void getAllFolderSubFolderAndFiles(UtilityFunctions uF) {
//		System.out.println("getAllFolderSubFolderAndFiles====>>>>> ");
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String orgName = CF.getStrOrgName();
			request.setAttribute("orgName", orgName);
			
			String proDocMainPath = CF.getProjectDocumentFolder();
			request.setAttribute("proDocMainPath", proDocMainPath);
			
			String proDocRetrivePath = CF.getRetriveProjectDocumentFolder();
			request.setAttribute("proDocRetrivePath", proDocRetrivePath);
			request.setAttribute("strOrgId", strOrgId);
			Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, getProId()+"");

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from project_document_details where pro_folder_id = 0 and (file_size is null or file_size ='') ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%' )))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbQuery.append(" order by pro_document_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst 1 ====>>>>> " + pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alProFolder = new ArrayList<Map<String,String>>();
			StringBuilder sbProjectDocId = null;
			while (rs.next()) {
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInner.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInner.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInner.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInner.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInner.put("PRO_ID", rs.getString("pro_id"));
				hmInner.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInner.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInner.put("CLIENT_ID", rs.getString("client_id"));
				hmInner.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInner.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInner.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInner.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInner.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInner.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInner.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInner.put("CATEGORY", "Uncategorised");
					hmInner.put("ALIGN", "Uncategorised");
				}
				hmInner.put("DOC_VERSION", rs.getString("doc_version"));
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInner.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInner.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInner.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				alProFolder.add(hmInner);
				
				if(sbProjectDocId == null){
					sbProjectDocId = new StringBuilder();
					sbProjectDocId.append(rs.getString("pro_document_id"));
				} else {
					sbProjectDocId.append(","+rs.getString("pro_document_id"));
				}
			}
			rs.close();
			pst.close(); 
//			System.out.println("hmFolder ====>>>>> " + hmFolder);
			request.setAttribute("alProFolder", alProFolder);
			
			
			Map<String, List<Map<String,String>>> hmSubFolder = new HashMap<String, List<Map<String,String>>>();
			Map<String, List<Map<String,String>>> hmSubDoc = new HashMap<String, List<Map<String,String>>>();
			StringBuilder sbSFQue = new StringBuilder();
			sbSFQue.append("select * from project_document_details where pro_folder_id > 0 ");
			if(sbProjectDocId != null) {
				sbSFQue.append(" and pro_folder_id in ("+sbProjectDocId.toString()+")");
			}
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbSFQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbSFQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbSFQue.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbSFQue.append(" and (file_size is null or file_size ='') order by pro_document_id");
			pst = con.prepareStatement(sbSFQue.toString());
//			System.out.println("pst 4 ====>>>>> " + pst);
			rs = pst.executeQuery();
			StringBuilder sbSubFolderIds = null;
			while (rs.next()) {
				List<Map<String, String>> alFolder = (List<Map<String, String>>) hmSubFolder.get(rs.getString("pro_folder_id")); 
				if(alFolder == null) alFolder = new ArrayList<Map<String,String>>();
				
				if(sbSubFolderIds == null) {
					sbSubFolderIds = new StringBuilder();
					sbSubFolderIds.append(rs.getString("pro_document_id"));
				} else {
					sbSubFolderIds.append(","+rs.getString("pro_document_id"));
				}
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInner.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInner.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInner.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInner.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInner.put("PRO_ID", rs.getString("pro_id"));
				hmInner.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInner.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInner.put("CLIENT_ID", rs.getString("client_id"));
				hmInner.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInner.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInner.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInner.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInner.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInner.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInner.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInner.put("CATEGORY", "Uncategorised");
					hmInner.put("ALIGN", "Uncategorised");
				}
				hmInner.put("DOC_VERSION", rs.getString("doc_version"));
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInner.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInner.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInner.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				alFolder.add(hmInner);
				hmSubFolder.put(rs.getString("pro_folder_id"), alFolder);
				
			}
			rs.close();
			pst.close();
//			System.out.println("hmSubFolder ====>>>>> " + hmSubFolder);
			request.setAttribute("hmSubFolder", hmSubFolder);
			
			StringBuilder sbSFDocQue = new StringBuilder();
			sbSFDocQue.append("select * from project_document_details where pro_folder_id > 0 ");
			if(sbProjectDocId != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+sbProjectDocId.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(sbProjectDocId != null && sbSubFolderIds == null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbProjectDocId.toString()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from " +
				"project_document_details where pro_folder_id > 0 ");
			if(sbProjectDocId != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+sbProjectDocId.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(sbProjectDocId != null && sbSubFolderIds == null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbProjectDocId.toString()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='')) and doc_parent_id = 0 ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbSFDocQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbSFDocQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbSFDocQue.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbSFDocQue.append(" order by pro_document_id ");
			pst = con.prepareStatement(sbSFDocQue.toString());
//			System.out.println("pst 5 ====>>>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(rs.getString("pro_folder_id")); 
				if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmInnerDoc = new HashMap<String, String>();
				hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
				hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
				hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInnerDoc.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInnerDoc.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInnerDoc.put("CATEGORY", "-");
					hmInnerDoc.put("ALIGN", "-");
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				
				hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmInnerDoc.put("FILE_EXTENSION", extenstion);
				
				alDoc.add(hmInnerDoc);
				hmSubDoc.put(rs.getString("pro_folder_id"), alDoc);
			}
			rs.close();
			pst.close();
//			System.out.println("1 hmSubDoc ====>>>>> " + hmSubDoc);
			
			sbSFDocQue = new StringBuilder();
			sbSFDocQue.append("select * from (select * from (select * from project_document_details where pro_folder_id > 0 ");
			if(sbProjectDocId != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+sbProjectDocId.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(sbProjectDocId != null && sbSubFolderIds == null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbProjectDocId.toString()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from " +
					"project_document_details where pro_folder_id > 0  ");
			if(sbProjectDocId != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+sbProjectDocId.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(sbProjectDocId != null && sbSubFolderIds == null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbProjectDocId.toString()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='')) and doc_parent_id > 0 ) a, (select max(pro_document_id) as " +
					"pro_document_id from project_document_details where pro_folder_id > 0 ");
			if(sbProjectDocId != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+sbProjectDocId.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(sbProjectDocId != null && sbSubFolderIds == null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbProjectDocId.toString()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='') and doc_parent_id > 0 group by doc_parent_id) b where a.pro_document_id=b.pro_document_id) ab");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbSFDocQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbSFDocQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%' )))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbSFDocQue.append(" where sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			pst = con.prepareStatement(sbSFDocQue.toString());
//			System.out.println("pst 6 ====>>>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(rs.getString("pro_folder_id")); 
				if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmInnerDoc = new HashMap<String, String>();
				hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
				hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
					+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
				hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInnerDoc.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInnerDoc.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInnerDoc.put("CATEGORY", "-");
					hmInnerDoc.put("ALIGN", "-");
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				
				hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmInnerDoc.put("FILE_EXTENSION", extenstion);
				
				alDoc.add(hmInnerDoc);
				hmSubDoc.put(rs.getString("pro_folder_id"), alDoc);
			}
			rs.close();
			pst.close();
//			System.out.println("2 hmSubDoc ====>>>>> " + hmSubDoc);
			request.setAttribute("hmSubDoc", hmSubDoc);
			
			StringBuilder sbFDQue = new StringBuilder();
			sbFDQue.append("select * from project_document_details where pro_folder_id = 0 and (file_size is not null or file_size !='') " +
					"and pro_document_id not in (select doc_parent_id from project_document_details where pro_folder_id = 0 " +
					"and (file_size is not null or file_size !='')) and doc_parent_id = 0 ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbFDQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbFDQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbFDQue.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			pst = con.prepareStatement(sbFDQue.toString());
//			System.out.println("pst 2 ====>>>>> " + pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alMainDoc = new ArrayList<Map<String,String>>();
			while (rs.next()) {
				Map<String, String> hmInnerDoc = new HashMap<String, String>();
				hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
				hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
				hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInnerDoc.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInnerDoc.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInnerDoc.put("CATEGORY", "-");
					hmInnerDoc.put("ALIGN", "-");
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				
				hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmInnerDoc.put("FILE_EXTENSION", extenstion);
				
				alMainDoc.add(hmInnerDoc);
//				hmDoc.put(rs.getString("pro_document_id"), alDoc);
			}
			rs.close();
			pst.close();
//			System.out.println("1 hmDoc ====>>>>> " + hmDoc);
			
			sbFDQue = new StringBuilder();
			sbFDQue.append("select * from (select * from (select * from project_document_details where pro_folder_id = 0 and (file_size is not null or file_size !='') " +
					"and pro_document_id not in (select doc_parent_id from project_document_details where pro_folder_id = 0 " +
					"and (file_size is not null or file_size !='')) and doc_parent_id > 0) a, " +
					"(select max(pro_document_id) as pro_document_id from project_document_details where pro_folder_id = 0 " +
					"and (file_size is not null or file_size !='') and doc_parent_id > 0 group by doc_parent_id) b where a.pro_document_id=b.pro_document_id) ab ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbFDQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbFDQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbFDQue.append(" where sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			pst = con.prepareStatement(sbFDQue.toString());
//			System.out.println("pst 3 ====>>>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				Map<String, String> hmInnerDoc = new HashMap<String, String>();
				hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
				hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
				hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInnerDoc.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInnerDoc.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInnerDoc.put("CATEGORY", "-");
					hmInnerDoc.put("ALIGN", "-");
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				
				hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmInnerDoc.put("FILE_EXTENSION", extenstion);				
				
				alMainDoc.add(hmInnerDoc);
//				hmDoc.put(rs.getString("pro_document_id"), alDoc);
			}
			rs.close();
			pst.close(); 
//			System.out.println("2 hmDoc ====>>>>> " + hmDoc);
			request.setAttribute("alMainDoc", alMainDoc);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getSubFolderFiles(UtilityFunctions uF) {
//		System.out.println("getSubFolderFiles ====>>>>> ");
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String orgName = CF.getStrOrgName();
			request.setAttribute("orgName", orgName);
			
			String proDocMainPath = CF.getProjectDocumentFolder();
			request.setAttribute("proDocMainPath", proDocMainPath);
			
			String proDocRetrivePath = CF.getRetriveProjectDocumentFolder();
			request.setAttribute("proDocRetrivePath", proDocRetrivePath);
			request.setAttribute("strOrgId", strOrgId);
			Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, getProId()+"");

			Map<String, List<Map<String,String>>> hmSubFolder = new HashMap<String, List<Map<String,String>>>();
			Map<String, List<Map<String,String>>> hmSubDoc = new HashMap<String, List<Map<String,String>>>();
			
			
			String folderName = null;
			String categoryType = null;
			String strProId = null;
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select folder_name, project_category, pro_id from project_document_details where pro_document_id in (select pro_folder_id from project_document_details where pro_document_id = ?)");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrId()));
//			System.out.println("pst 0 ====>>>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				folderName = rs.getString("folder_name");
				categoryType = rs.getString("project_category");
				strProId = rs.getString("pro_id");
			}
			rs.close();
			pst.close();
//			System.out.println("folderName ====>>>>> " + folderName);
			request.setAttribute("folderName", folderName);
			request.setAttribute("categoryType", categoryType);
			request.setAttribute("strProId", strProId);
			
			
			StringBuilder sbSFQue = new StringBuilder();
			sbSFQue.append("select * from project_document_details where pro_folder_id > 0 ");
			if(getStrId() != null) {
				sbSFQue.append(" and pro_document_id in ("+getStrId()+")");
			}
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbSFQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbSFQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbSFQue.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbSFQue.append(" and (file_size is null or file_size ='') order by pro_document_id");
			pst = con.prepareStatement(sbSFQue.toString());
//			System.out.println("pst 4 ====>>>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<Map<String, String>> alFolder = (List<Map<String, String>>) hmSubFolder.get(rs.getString("pro_document_id")); 
				if(alFolder == null) alFolder = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInner.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInner.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInner.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInner.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInner.put("PRO_ID", rs.getString("pro_id"));
				hmInner.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInner.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInner.put("CLIENT_ID", rs.getString("client_id"));
				hmInner.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInner.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInner.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInner.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInner.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInner.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInner.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInner.put("CATEGORY", "Uncategorised");
					hmInner.put("ALIGN", "Uncategorised");
				}
				hmInner.put("DOC_VERSION", rs.getString("doc_version"));
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInner.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInner.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInner.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				alFolder.add(hmInner);
				hmSubFolder.put(rs.getString("pro_document_id"), alFolder);
			}
			rs.close();
			pst.close();
//			System.out.println("hmSubFolder ====>>>>> " + hmSubFolder);
			request.setAttribute("hmSubFolder", hmSubFolder);
			
			StringBuilder sbSFDocQue = new StringBuilder();
			sbSFDocQue.append("select * from project_document_details where pro_folder_id > 0 ");
			if(getStrId() != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+getStrId()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from " +
				"project_document_details where pro_folder_id > 0 ");
			if(getStrId() != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+getStrId()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='')) and doc_parent_id = 0 ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbSFDocQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbSFDocQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbSFDocQue.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbSFDocQue.append(" order by pro_document_id ");
			pst = con.prepareStatement(sbSFDocQue.toString());
//			System.out.println("pst 5 ====>>>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(rs.getString("pro_folder_id")); 
				if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmInnerDoc = new HashMap<String, String>();
				hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
				hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
				hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInnerDoc.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInnerDoc.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInnerDoc.put("CATEGORY", "-");
					hmInnerDoc.put("ALIGN", "-");
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				
				hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmInnerDoc.put("FILE_EXTENSION", extenstion);
				
				alDoc.add(hmInnerDoc);
				hmSubDoc.put(rs.getString("pro_folder_id"), alDoc);
			}
			rs.close();
			pst.close();
//			System.out.println("1 hmSubDoc ====>>>>> " + hmSubDoc);
			
			sbSFDocQue = new StringBuilder();
			sbSFDocQue.append(" select * from (select * from (select * from project_document_details where pro_folder_id > 0 ");
			if(getStrId() != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+getStrId()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from " +
					"project_document_details where pro_folder_id > 0  ");
			if(getStrId() != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+getStrId()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='')) and doc_parent_id > 0 ) a, (select max(pro_document_id) as " +
					"pro_document_id from project_document_details where pro_folder_id > 0 ");
			if(getStrId() != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+getStrId()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='') and doc_parent_id > 0 group by doc_parent_id) b where a.pro_document_id=b.pro_document_id) ab");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbSFDocQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbSFDocQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbSFDocQue.append(" where sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			pst = con.prepareStatement(sbSFDocQue.toString());
//			System.out.println("pst 6 ====>>>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(rs.getString("pro_folder_id")); 
				if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmInnerDoc = new HashMap<String, String>();
				hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
				hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
					+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
				hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInnerDoc.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInnerDoc.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInnerDoc.put("CATEGORY", "-");
					hmInnerDoc.put("ALIGN", "-");
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				
				hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmInnerDoc.put("FILE_EXTENSION", extenstion);
				
				alDoc.add(hmInnerDoc);
				hmSubDoc.put(rs.getString("pro_folder_id"), alDoc);
			}
			rs.close();
			pst.close();
//			System.out.println("2 hmSubDoc ====>>>>> " + hmSubDoc);
			
			request.setAttribute("hmSubDoc", hmSubDoc);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getFolderSubFolderAndFiles(UtilityFunctions uF) {
//		System.out.println("getFolderSubFolderAndFiles====>>>>> ");
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String orgName = CF.getStrOrgName();
			request.setAttribute("orgName", orgName);
			
			String proDocMainPath = CF.getProjectDocumentFolder();
			request.setAttribute("proDocMainPath", proDocMainPath);
			
			String proDocRetrivePath = CF.getRetriveProjectDocumentFolder();
			request.setAttribute("proDocRetrivePath", proDocRetrivePath);
			request.setAttribute("strOrgId", strOrgId);
			Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, getProId()+"");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from project_document_details where pro_document_id=? and pro_folder_id = 0 and (file_size is null or file_size ='') ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbQuery.append(" order by pro_document_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrId()));
//			System.out.println("pst 1 ====>>>>> " + pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alProFolder = new ArrayList<Map<String,String>>();
			while (rs.next()) {
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInner.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInner.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInner.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInner.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInner.put("PRO_ID", rs.getString("pro_id"));
				hmInner.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInner.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInner.put("CLIENT_ID", rs.getString("client_id"));
				hmInner.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInner.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInner.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInner.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInner.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInner.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInner.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInner.put("CATEGORY", "Uncategorised");
					hmInner.put("ALIGN", "Uncategorised");
				}
				hmInner.put("DOC_VERSION", rs.getString("doc_version"));
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInner.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInner.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInner.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				alProFolder.add(hmInner);
			}
			rs.close();
			pst.close(); 
//			System.out.println("hmFolder ====>>>>> " + hmFolder);
			request.setAttribute("alProFolder", alProFolder);
			
			
			Map<String, List<Map<String,String>>> hmSubFolder = new HashMap<String, List<Map<String,String>>>();
			Map<String, List<Map<String,String>>> hmSubDoc = new HashMap<String, List<Map<String,String>>>();
			StringBuilder sbSFQue = new StringBuilder();
			sbSFQue.append("select * from project_document_details where pro_folder_id > 0 ");
			if(getStrId() != null) {
				sbSFQue.append(" and pro_folder_id in ("+getStrId()+")");
			}
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbSFQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbSFQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbSFQue.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbSFQue.append(" and (file_size is null or file_size ='') order by pro_document_id");
			pst = con.prepareStatement(sbSFQue.toString());
//			System.out.println("pst 4 ====>>>>> " + pst);
			rs = pst.executeQuery();
			StringBuilder sbSubFolderIds = null;
			while (rs.next()) {
				List<Map<String, String>> alFolder = (List<Map<String, String>>) hmSubFolder.get(rs.getString("pro_folder_id")); 
				if(alFolder == null) alFolder = new ArrayList<Map<String,String>>();
				
				if(sbSubFolderIds == null) {
					sbSubFolderIds = new StringBuilder();
					sbSubFolderIds.append(rs.getString("pro_document_id"));
				} else {
					sbSubFolderIds.append(","+rs.getString("pro_document_id"));
				}
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInner.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInner.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInner.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInner.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInner.put("PRO_ID", rs.getString("pro_id"));
				hmInner.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInner.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInner.put("CLIENT_ID", rs.getString("client_id"));
				hmInner.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInner.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInner.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInner.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInner.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInner.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInner.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInner.put("CATEGORY", "Uncategorised");
					hmInner.put("ALIGN", "Uncategorised");
				}
				hmInner.put("DOC_VERSION", rs.getString("doc_version"));
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInner.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInner.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInner.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				alFolder.add(hmInner);
				hmSubFolder.put(rs.getString("pro_folder_id"), alFolder);
				
			}
			rs.close();
			pst.close();
//			System.out.println("hmSubFolder ====>>>>> " + hmSubFolder);
			request.setAttribute("hmSubFolder", hmSubFolder);
			
			StringBuilder sbSFDocQue = new StringBuilder();
			sbSFDocQue.append("select * from project_document_details where pro_folder_id > 0 ");
			if(getStrId() != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+getStrId()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(getStrId() != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+getStrId()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from " +
				"project_document_details where pro_folder_id > 0 ");
			if(getStrId() != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+getStrId()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(getStrId() != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+getStrId()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='')) and doc_parent_id = 0 ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbSFDocQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbSFDocQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbSFDocQue.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbSFDocQue.append(" order by pro_document_id ");
			pst = con.prepareStatement(sbSFDocQue.toString());
//			System.out.println("pst 5 ====>>>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(rs.getString("pro_folder_id")); 
				if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmInnerDoc = new HashMap<String, String>();
				hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
				hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
				hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInnerDoc.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInnerDoc.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInnerDoc.put("CATEGORY", "-");
					hmInnerDoc.put("ALIGN", "-");
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				
				hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmInnerDoc.put("FILE_EXTENSION", extenstion);
				
				alDoc.add(hmInnerDoc);
				hmSubDoc.put(rs.getString("pro_folder_id"), alDoc);
			}
			rs.close();
			pst.close();
//			System.out.println("1 hmSubDoc ====>>>>> " + hmSubDoc);
			
			sbSFDocQue = new StringBuilder();
			sbSFDocQue.append("select * from (select * from (select * from project_document_details where pro_folder_id > 0 ");
			if(getStrId() != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+getStrId()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(getStrId() != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+getStrId()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from " +
					"project_document_details where pro_folder_id > 0  ");
			if(getStrId() != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+getStrId()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(getStrId() != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+getStrId()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='')) and doc_parent_id > 0 ) a, (select max(pro_document_id) as " +
					"pro_document_id from project_document_details where pro_folder_id > 0 ");
			if(getStrId() != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+getStrId()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(getStrId() != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+getStrId()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='') and doc_parent_id > 0 group by doc_parent_id) b where a.pro_document_id=b.pro_document_id) ab ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbSFDocQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbSFDocQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbSFDocQue.append(" where sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			pst = con.prepareStatement(sbSFDocQue.toString());
//			System.out.println("pst 6 ====>>>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(rs.getString("pro_folder_id")); 
				if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmInnerDoc = new HashMap<String, String>();
				hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
				hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
					+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
				hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInnerDoc.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInnerDoc.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInnerDoc.put("CATEGORY", "-");
					hmInnerDoc.put("ALIGN", "-");
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				
				hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmInnerDoc.put("FILE_EXTENSION", extenstion);
				
				alDoc.add(hmInnerDoc);
				hmSubDoc.put(rs.getString("pro_folder_id"), alDoc);
			}
			rs.close();
			pst.close();
//			System.out.println("2 hmSubDoc ====>>>>> " + hmSubDoc);
			
			request.setAttribute("hmSubDoc", hmSubDoc);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public void getCatagoryORProjectFolderFiles(UtilityFunctions uF) {
//		System.out.println("getCatagoryORProjectFolderFiles ====>>>>> ");
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String orgName = CF.getStrOrgName();
			request.setAttribute("orgName", orgName);
			
			String proDocMainPath = CF.getProjectDocumentFolder();
			request.setAttribute("proDocMainPath", proDocMainPath);
			
			String proDocRetrivePath = CF.getRetriveProjectDocumentFolder();
			request.setAttribute("proDocRetrivePath", proDocRetrivePath);
			request.setAttribute("strOrgId", strOrgId);
			Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, getProId()+"");

			if(getType() != null && getType().equals("C")) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from project_document_details where align_with=? and (project_category=2 or project_category=0) and pro_folder_id = 0 and (file_size is null or file_size ='') ");
				if(strUserType != null && strUserType.equals(EMPLOYEE)){
					sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
				} else if(strUserType != null && strUserType.equals(MANAGER)){
					sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
				} else if(strUserType != null && strUserType.equals(CUSTOMER)){
					sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
				}
				sbQuery.append(" order by pro_document_id");
				pst = con.prepareStatement(sbQuery.toString());
			} else if(getType() != null && getType().equals("P")) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from project_document_details where pro_id=? and project_category=1 and pro_folder_id = 0 and (file_size is null or file_size ='') ");
				if(strUserType != null && strUserType.equals(EMPLOYEE)){
					sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
				} else if(strUserType != null && strUserType.equals(MANAGER)){
				//===start parvez date: 13-10-2022===	
					sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
							"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
							"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//							"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
							"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
				//===end parvez date: 13-10-2022===	
				} else if(strUserType != null && strUserType.equals(CUSTOMER)){
					sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
				}
				sbQuery.append(" order by pro_document_id");
				pst = con.prepareStatement(sbQuery.toString());
			}
			pst.setInt(1, uF.parseToInt(getStrId()));
//			System.out.println("pst 1 ====>>>>> " + pst);
			rs = pst.executeQuery();
			StringBuilder sbFolderIds = null;
			List<Map<String, String>> alProFolder = new ArrayList<Map<String,String>>();
			while (rs.next()) {
				
				if(sbFolderIds == null) {
					sbFolderIds = new StringBuilder();
					sbFolderIds.append(rs.getString("pro_document_id"));
				} else {
					sbFolderIds.append(","+rs.getString("pro_document_id"));
				}
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInner.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInner.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInner.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInner.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInner.put("PRO_ID", rs.getString("pro_id"));
				hmInner.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInner.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInner.put("CLIENT_ID", rs.getString("client_id"));
				hmInner.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInner.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInner.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInner.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInner.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInner.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInner.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInner.put("CATEGORY", "Uncategorised");
					hmInner.put("ALIGN", "Uncategorised");
				}
				hmInner.put("DOC_VERSION", rs.getString("doc_version"));
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInner.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInner.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInner.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				alProFolder.add(hmInner);
			}
			rs.close();
			pst.close(); 
//			System.out.println("hmFolder ====>>>>> " + hmFolder);
			request.setAttribute("alProFolder", alProFolder);
			StringBuilder sbFDQue = new StringBuilder();
			sbFDQue.append("select * from project_document_details where pro_folder_id = 0 ");
			if(getType() != null && getType().equals("C")) {
				sbFDQue.append(" and align_with=? and (project_category=2 or project_category=0) ");
			} else if(getType() != null && getType().equals("P")) {
				sbFDQue.append(" and pro_id=? and project_category=1 ");
			}
			sbFDQue.append(" and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from " +
				"project_document_details where pro_folder_id = 0 ");
			if(getType() != null && getType().equals("C")) {
				sbFDQue.append(" and align_with=? and (project_category=2 or project_category=0) ");
			} else if(getType() != null && getType().equals("P")) {
				sbFDQue.append(" and pro_id=? and project_category=1 ");
			}
			sbFDQue.append(" and (file_size is not null or file_size !='')) and doc_parent_id = 0");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbFDQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbFDQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbFDQue.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			pst = con.prepareStatement(sbFDQue.toString());
			pst.setInt(1, uF.parseToInt(getStrId()));
			pst.setInt(2, uF.parseToInt(getStrId()));
//			System.out.println("pst 2 ====>>>>> " + pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alMainDoc = new ArrayList<Map<String,String>>();
			while (rs.next()) {
				
				Map<String, String> hmInnerDoc = new HashMap<String, String>();
				hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
				hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
				hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInnerDoc.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInnerDoc.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInnerDoc.put("CATEGORY", "-");
					hmInnerDoc.put("ALIGN", "-");
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				
				hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmInnerDoc.put("FILE_EXTENSION", extenstion);
				
				alMainDoc.add(hmInnerDoc);
			}
			rs.close();
			pst.close();
//			System.out.println("1 hmDoc ====>>>>> " + hmDoc);
			
			sbFDQue = new StringBuilder();
			sbFDQue.append("select * from (select * from (select * from project_document_details where pro_folder_id = 0 ");
			if(getType() != null && getType().equals("C")) {
				sbFDQue.append(" and align_with=? and (project_category=2 or project_category=0) ");
			} else if(getType() != null && getType().equals("P")) {
				sbFDQue.append(" and pro_id=? and project_category=1 ");
			}
			sbFDQue.append(" and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from project_document_details " +
					"where pro_folder_id = 0 ");
			if(getType() != null && getType().equals("C")) {
				sbFDQue.append(" and align_with=? and (project_category=2 or project_category=0) ");
			} else if(getType() != null && getType().equals("P")) {
				sbFDQue.append(" and pro_id=? and project_category=1 ");
			}
			sbFDQue.append(" and (file_size is not null or file_size !='')) and doc_parent_id > 0) a," +
					" (select max(pro_document_id) as pro_document_id from project_document_details where pro_folder_id = 0 ");
			if(getType() != null && getType().equals("C")) {
				sbFDQue.append(" and align_with=? and (project_category=2 or project_category=0) ");
			} else if(getType() != null && getType().equals("P")) {
				sbFDQue.append(" and pro_id=? and project_category=1 ");
			}
			sbFDQue.append(" and (file_size is not null or file_size !='') and doc_parent_id > 0 group by doc_parent_id) b where a.pro_document_id=b.pro_document_id) ab");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbFDQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbFDQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbFDQue.append(" where sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			pst = con.prepareStatement(sbFDQue.toString());
			pst.setInt(1, uF.parseToInt(getStrId()));
			pst.setInt(2, uF.parseToInt(getStrId()));
			pst.setInt(3, uF.parseToInt(getStrId()));
//			System.out.println("pst 3 ====>>>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				Map<String, String> hmInnerDoc = new HashMap<String, String>();
				hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
				hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
				hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInnerDoc.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInnerDoc.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInnerDoc.put("CATEGORY", "-");
					hmInnerDoc.put("ALIGN", "-");
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				
				hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmInnerDoc.put("FILE_EXTENSION", extenstion);				
				
				alMainDoc.add(hmInnerDoc);
			}
			rs.close();
			pst.close(); 
//			System.out.println("2 hmDoc ====>>>>> " + hmDoc);
			request.setAttribute("alMainDoc", alMainDoc);
			
			
			Map<String, List<Map<String,String>>> hmSubFolder = new HashMap<String, List<Map<String,String>>>();
			Map<String, List<Map<String,String>>> hmSubDoc = new HashMap<String, List<Map<String,String>>>();
			StringBuilder sbSFQue = new StringBuilder();
			sbSFQue.append("select * from project_document_details where pro_folder_id > 0 ");
			if(sbFolderIds != null) {
				sbSFQue.append(" and pro_folder_id in ("+sbFolderIds.toString()+")");
			}
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbSFQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbSFQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbSFQue.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbSFQue.append(" and (file_size is null or file_size ='') order by pro_document_id");
			pst = con.prepareStatement(sbSFQue.toString());
//			System.out.println("pst 4 ====>>>>> " + pst);
			rs = pst.executeQuery();
			StringBuilder sbSubFolderIds = null;
			while (rs.next()) {
				List<Map<String, String>> alFolder = (List<Map<String, String>>) hmSubFolder.get(rs.getString("pro_folder_id")); 
				if(alFolder == null) alFolder = new ArrayList<Map<String,String>>();
				
				if(sbSubFolderIds == null) {
					sbSubFolderIds = new StringBuilder();
					sbSubFolderIds.append(rs.getString("pro_document_id"));
				} else {
					sbSubFolderIds.append(","+rs.getString("pro_document_id"));
				}
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInner.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInner.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInner.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInner.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInner.put("PRO_ID", rs.getString("pro_id"));
				hmInner.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInner.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInner.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInner.put("CLIENT_ID", rs.getString("client_id"));
				hmInner.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInner.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInner.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInner.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInner.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInner.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInner.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInner.put("CATEGORY", "Uncategorised");
					hmInner.put("ALIGN", "Uncategorised");
				}
				hmInner.put("DOC_VERSION", rs.getString("doc_version"));
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInner.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInner.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInner.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				alFolder.add(hmInner);
				hmSubFolder.put(rs.getString("pro_folder_id"), alFolder);
				
			}
			rs.close();
			pst.close();
//			System.out.println("hmSubFolder ====>>>>> " + hmSubFolder);
			request.setAttribute("hmSubFolder", hmSubFolder);
			
			StringBuilder sbSFDocQue = new StringBuilder();
			sbSFDocQue.append("select * from project_document_details where pro_folder_id > 0 ");
			if(sbFolderIds != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+sbFolderIds.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(sbFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbFolderIds.toString()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from " +
				"project_document_details where pro_folder_id > 0 ");
			if(sbFolderIds != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+sbFolderIds.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(sbFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbFolderIds.toString()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='')) and doc_parent_id = 0 ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbSFDocQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbSFDocQue.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbSFDocQue.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbSFDocQue.append(" order by pro_document_id ");
			pst = con.prepareStatement(sbSFDocQue.toString());
//			System.out.println("pst 5 ====>>>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(rs.getString("pro_folder_id")); 
				if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmInnerDoc = new HashMap<String, String>();
				hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
				hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
						+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
				hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInnerDoc.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInnerDoc.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInnerDoc.put("CATEGORY", "-");
					hmInnerDoc.put("ALIGN", "-");
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				
				hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmInnerDoc.put("FILE_EXTENSION", extenstion);
				
				alDoc.add(hmInnerDoc);
				hmSubDoc.put(rs.getString("pro_folder_id"), alDoc);
			}
			rs.close();
			pst.close();
//			System.out.println("1 hmSubDoc ====>>>>> " + hmSubDoc);
			
			sbSFDocQue = new StringBuilder();
			sbSFDocQue.append("select * from (select * from (select * from project_document_details where pro_folder_id > 0 ");
			if(sbFolderIds != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+sbFolderIds.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(sbFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbFolderIds.toString()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='') and pro_document_id not in (select doc_parent_id from " +
					"project_document_details where pro_folder_id > 0  ");
			if(sbFolderIds != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+sbFolderIds.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(sbFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbFolderIds.toString()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='')) and doc_parent_id > 0 ) a, (select max(pro_document_id) as " +
					"pro_document_id from project_document_details where pro_folder_id > 0 ");
			if(sbFolderIds != null && sbSubFolderIds != null) {
				sbSFDocQue.append(" and (pro_folder_id in ("+sbFolderIds.toString()+") or pro_folder_id in ("+sbSubFolderIds.toString()+")) ");
			} else if(sbFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbFolderIds.toString()+") ");
			} else if(sbSubFolderIds != null) {
				sbSFDocQue.append(" and pro_folder_id in ("+sbSubFolderIds.toString()+") ");
			}
			sbSFDocQue.append(" and (file_size is not null or file_size !='') and doc_parent_id > 0 group by doc_parent_id) b where a.pro_document_id=b.pro_document_id) ab ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbSFDocQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
			//===start parvez date: 13-10-2022===	
				sbSFDocQue.append(" where ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
						"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
						"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//						"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
						"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
			//===end parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbSFDocQue.append(" where sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			pst = con.prepareStatement(sbSFDocQue.toString());
//			System.out.println("pst 6 ====>>>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(rs.getString("pro_folder_id")); 
				if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
				
				Map<String, String> hmInnerDoc = new HashMap<String, String>();
				hmInnerDoc.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmInnerDoc.put("FOLDER_NAME", rs.getString("folder_name"));
				hmInnerDoc.put("DESCRIPTION", uF.showData(rs.getString("description"), ""));
				hmInnerDoc.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmInnerDoc.put("PROJECT_NAME", CF.getProjectNameById(con, rs.getString("pro_id")));
				hmInnerDoc.put("PRO_ID", rs.getString("pro_id"));
				hmInnerDoc.put("ADDED_BY", CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
//				hmInnerDoc.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInnerDoc.put("ENTRY_DATE", "Last update at "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()) 
					+" on "+uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat())+" by " + CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				hmInnerDoc.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmInnerDoc.put("CLIENT_ID", rs.getString("client_id"));
				hmInnerDoc.put("FILE_SIZE", uF.showData(rs.getString("file_size"), "-"));
				hmInnerDoc.put("FILE_TYPE", uF.showData(rs.getString("file_type"), "-"));
				hmInnerDoc.put("CATEGORY_ID", uF.showData(rs.getString("project_category"), "-"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1) {
					hmInnerDoc.put("CATEGORY", "Project");
					String strAlign = CF.getProjectNameById(con, rs.getString("pro_id"));
					hmInnerDoc.put("ALIGN", uF.showData(strAlign, "-")+", Project");
				} else if(uF.parseToInt(rs.getString("project_category")) == 2) {
					hmInnerDoc.put("CATEGORY", "Category");
					String strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					hmInnerDoc.put("ALIGN", uF.showData(strOther, "-")+", Category");
				} else {
					hmInnerDoc.put("CATEGORY", "-");
					hmInnerDoc.put("ALIGN", "-");
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmInnerDoc.put("SHARING_TYPE", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmInnerDoc.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				
				hmInnerDoc.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmInnerDoc.put("FILE_EXTENSION", extenstion);
				
				alDoc.add(hmInnerDoc);
				hmSubDoc.put(rs.getString("pro_folder_id"), alDoc);
			}
			rs.close();
			pst.close();
//			System.out.println("2 hmSubDoc ====>>>>> " + hmSubDoc);
			
			request.setAttribute("hmSubDoc", hmSubDoc);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public int getProId() {
		return proId;
	}

	public void setProId(int proId) {
		this.proId = proId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBtnSave() {
		return btnSave;
	}

	public void setBtnSave(String btnSave) {
		this.btnSave = btnSave;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
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

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}


	public String[] getFolderTRId() {
		return folderTRId;
	}


	public void setFolderTRId(String[] folderTRId) {
		this.folderTRId = folderTRId;
	}

	public String[] getStrFolderName() {
		return strFolderName;
	}

	public void setStrFolderName(String[] strFolderName) {
		this.strFolderName = strFolderName;
	}

	public File[] getStrDoc() {
		return strDoc;
	}

	public void setStrDoc(File[] strDoc) {
		this.strDoc = strDoc;
	}

	public String[] getStrDocFileName() {
		return strDocFileName;
	}

	public void setStrDocFileName(String[] strDocFileName) {
		this.strDocFileName = strDocFileName;
	}

	public String[] getDocsTRId() {
		return docsTRId;
	}

	public void setDocsTRId(String[] docsTRId) {
		this.docsTRId = docsTRId;
	}
	
	public String getStrSearchDoc() {
		return strSearchDoc;
	}


	public void setStrSearchDoc(String strSearchDoc) {
		this.strSearchDoc = strSearchDoc;
	}
	
}
