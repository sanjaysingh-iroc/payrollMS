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

public class ProjectDocuments extends ActionSupport implements ServletRequestAware, ServletResponseAware,  IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	int proId;
	String strUserType;
	String strSessionEmpId;
	String type;
	String strOrgId;
	String btnSave;
	String proType;
	String fromPage;
	String taskId;
	
	String pageType;
	
	CommonFunctions CF;
	

	public String execute() {
			
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		System.out.println("getProId() ===>> "+ getProId());
		System.out.println("getTaskId() ===>> "+ getTaskId());
		
		if(getPageType() == null || getPageType().equals("") || getPageType().equals("null")) {
			setPageType(null);
		}
		
		if(getBtnSave() != null && getBtnSave().equals("Save")) {
			if(getFromPage() != null && getFromPage().equalsIgnoreCase("MyProject")) {
				createEmpFolderForDocs(uF);
				return "mpupdate";
			} else {
				createFolderForDocs(uF);
				if(getPageType() != null && getPageType().equals("MP")) {
					return MYSUCCESS;
				} else {
					return UPDATE;
				}
			}
		}
		System.out.println("getProId() -- ===>> "+ getProId());
		getProjectDocumentDetails(uF);
		return SUCCESS;
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
			String clientId = CF.getClientIdByProjectId(con, uF, getProId()+"");
//			System.out.println("clientId ===>> " + clientId);
			
			String mainPathWithOrg = CF.getProjectDocumentFolder()+"/"+strOrgId;
			File fileOrg = new File(mainPathWithOrg);
			if (!fileOrg.exists()) {
				if (fileOrg.mkdir()) {
					System.out.println("Directory is created!");
				}
			}
			
			String mainPath = mainPathWithOrg+"/Projects";
			File file = new File(mainPath);
			if (!file.exists()) {
				if (file.mkdir()) {
					System.out.println("Directory is created!");
				}
			}
			
			String proNameFolder = mainPath +"/"+getProId();
			File file1 = new File(proNameFolder);
			if (!file1.exists()) {
				if (file1.mkdir()) {
					System.out.println("Directory is created!");
				}
			} 
			
			MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;   
			  
			File[] strDoc = mpRequest.getFiles("strDoc"+getProId());      
			String[] strDocFileName = mpRequest.getFileNames("strDoc"+getProId());
			
			String[] docsTRId = request.getParameterValues("docsTRId"+getProId());
//			String[] proDocTasks = request.getParameterValues("proDocTasks"+getProId());
			String[] docSharingType = request.getParameterValues("docSharingType"+getProId());
			String[] proCategoryTypeDoc = request.getParameterValues("proCategoryTypeDoc"+getProId());
			String[] strDocDescription = request.getParameterValues("strDocDescription"+getProId());
			String[] strScopeDoc = request.getParameterValues("strScopeDoc"+getProId());
			
			String[] isDocEdit = request.getParameterValues("isDocEdit"+getProId());
			String[] isDocDelete = request.getParameterValues("isDocDelete"+getProId());
			
			for(int j=0; docsTRId != null && j<docsTRId.length; j++) {
				String proDocTasks = request.getParameter("proDocTasks"+getProId()+"_"+docsTRId[j]);
				String proDocCategory = request.getParameter("proDocCategory"+getProId()+"_"+docsTRId[j]);
				
				double lengthBytes=0;
				boolean isFileExist = false;
				String strFileName = "";
				if(strDoc !=null && strDoc[j]!=null) {
					lengthBytes =  strDoc[j].length();
					
					String extenstion=FilenameUtils.getExtension(strDocFileName[j]);	
					strFileName = FilenameUtils.getBaseName(strDocFileName[j]);
					strFileName = strFileName+"v1."+extenstion;
					
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
				}
				
				if(lengthBytes > 0 && !isFileExist) {
//					 System.out.println("1 file save path=====>"+proNameFolder+"/"+strDocFileName[j]);
					String[] proDocEmployee = request.getParameterValues("proDocEmployee"+getProId()+"_"+docsTRId[j]);
					List<String> alEmployee = null;
					if(proDocEmployee != null) {
						alEmployee = Arrays.asList(proDocEmployee);
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
					
					String[] proDocPoc = request.getParameterValues("proDocPoc"+getProId()+"_"+docsTRId[j]);
					List<String> alPoc = null;
					if(proDocPoc != null) {
						alPoc = Arrays.asList(proDocPoc);
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
						pst.setInt(3, getProId());
					} else {
						pst.setInt(3, uF.parseToInt(proDocCategory));
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
					
					
					pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,added_by, entry_date,folder_file_type," +
						"pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description,doc_parent_id," +
						"is_edit,is_delete,is_cust_add,sharing_poc,doc_version,feed_id) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(clientId));
					pst.setInt(2, getProId());
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
//					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setString(5, "file");
					pst.setInt(6, 0);
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						pst.setInt(7, uF.parseToInt(proDocTasks));
					} else {
						pst.setInt(7, uF.parseToInt(proDocCategory));
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
					
					
					uploadProjectDocuments(con, getProId()+"", strDoc[j], strFileName, proNameFolder, proDocumentId, feedId);
					
					/**
					 * Alerts
					 * */
					
					List<String> alEmp= null;
					if(proDocEmployee != null) {
						alEmp = Arrays.asList(proDocEmployee);
					}
					if(alEmp == null){
						alEmp = new ArrayList<String>();
					}
					
					List<String> alSharePoc = null;
					if(proDocPoc != null) {
						alSharePoc = Arrays.asList(proDocPoc);
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
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
						pst.setInt(1, uF.parseToInt(proDocTasks));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							proName = rs.getString("pro_name");
						}
						rs.close();
						pst.close();
					} else {
						pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
						pst.setInt(1, uF.parseToInt(proDocCategory));
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
			

			String[] folderTRId = request.getParameterValues("folderTRId"+getProId());
			String[] strFolderName = request.getParameterValues("strFolderName"+getProId());
			String[] strFolderDescription = request.getParameterValues("strFolderDescription"+getProId());
			String[] proCategoryTypeFolder = request.getParameterValues("proCategoryTypeFolder"+getProId());
//			String[] proFolderTasks = request.getParameterValues("proFolderTasks"+getProId());
			String[] folderSharingType = request.getParameterValues("folderSharingType"+getProId());
			
			String[] isFolderEdit = request.getParameterValues("isFolderEdit"+getProId());
			String[] isFolderDelete = request.getParameterValues("isFolderDelete"+getProId());
//			System.out.println("getStrClient 111 ===>>>> " + getStrClient());
			int docCnt = 0;
			for (int i=0; folderTRId != null && i<folderTRId.length; i++) {
				
				String proFolderTasks = request.getParameter("proFolderTasks"+getProId()+"_"+folderTRId[i]);
				String proFolderCategory = request.getParameter("proFolderCategory"+getProId()+"_"+folderTRId[i]);
				
				String[] proFolderEmployee = request.getParameterValues("proFolderEmployee"+getProId()+"_"+folderTRId[i]);
				List<String> alEmployee = null;
				if(proFolderEmployee != null) {
					alEmployee = Arrays.asList(proFolderEmployee);
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
				
				String[] proFolderPoc = request.getParameterValues("proFolderPoc"+getProId()+"_"+folderTRId[i]);
				List<String> alPoc = null;
				if(proFolderPoc != null) {
					alPoc = Arrays.asList(proFolderPoc);
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
				
				String proFolderNameFolder = proNameFolder +"/"+ strFolderName[i];
				
				File file2 = new File(proFolderNameFolder);
				if (!file2.exists()) {
					if (file2.mkdir()) {
						System.out.println("Directory is created!");
					}
				}
				
				pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date,folder_file_type," +
					"pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description,doc_parent_id,is_edit,is_delete," +
					"is_cust_add,sharing_poc,doc_version) " +
					"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(clientId));
				pst.setInt(2, getProId());
				pst.setString(3, strFolderName[i]);
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
//				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				pst.setString(6, "folder");
				pst.setInt(7, 0);
				if(uF.parseToInt(proCategoryTypeFolder[i]) == 1) {
					pst.setInt(8, uF.parseToInt(proFolderTasks));
				} else {
					pst.setInt(8, uF.parseToInt(proFolderCategory));
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
		//		System.out.println("pst====>"+pst);
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
				
				List<String> alEmp= null;
				if(proFolderEmployee != null) {
					alEmp = Arrays.asList(proFolderEmployee);
				}
				if(alEmp == null){
					alEmp = new ArrayList<String>();
				}
				
				List<String> alSharePoc = null;
				if(proFolderPoc != null) {
					alSharePoc = Arrays.asList(proFolderPoc);
				}
				if(alSharePoc == null){
					alSharePoc = new ArrayList<String>();
				}
				
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
					pst.setInt(1, uF.parseToInt(proFolderTasks));
					rs = pst.executeQuery();
					
					while(rs.next()) {
						proName = rs.getString("pro_name");
					}
					rs.close();
					pst.close();
				} else {
					pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
					pst.setInt(1, uF.parseToInt(proFolderCategory));
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
				for(String strEmp : alEmp) {
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
				
				
				String[] folderDocsTRId = request.getParameterValues("folderDocsTRId" + getProId()+"_"+folderTRId[i]);
				File[] strFolderDoc = mpRequest.getFiles("strFolderDoc" + getProId()+"_"+folderTRId[i]);      
				String[] strFolderDocFileName = mpRequest.getFileNames("strFolderDoc" + getProId()+"_"+folderTRId[i]);
//				String[] proFolderDocTasks = request.getParameterValues("proFolderDocTasks" + getProId()+"_"+folderTRId[i]);
				String[] folderDocDharingType = request.getParameterValues("folderDocDharingType" + getProId()+"_"+folderTRId[i]);
				String[] proCategoryTypeFolderDoc = request.getParameterValues("proCategoryTypeFolderDoc" + getProId()+"_"+folderTRId[i]);
				String[] strFolderDocDescription = request.getParameterValues("strFolderDocDescription" + getProId()+"_"+folderTRId[i]);
				String[] strFolderScopeDoc = request.getParameterValues("strFolderScopeDoc" + getProId()+"_"+folderTRId[i]);
				
				String[] isFolderDocEdit = request.getParameterValues("isFolderDocEdit" + getProId()+"_"+folderTRId[i]);
				String[] isFolderDocDelete = request.getParameterValues("isFolderDocDelete" + getProId()+"_"+folderTRId[i]);
				
				for(int j=0; folderDocsTRId != null && j<folderDocsTRId.length; j++) {
				
					String proFolderDocTasks = request.getParameter("proFolderDocTasks"+getProId()+"_"+folderTRId[i]+"_"+folderDocsTRId[j]);
					String proFolderDocCategory = request.getParameter("proFolderDocCategory"+getProId()+"_"+folderTRId[i]+"_"+folderDocsTRId[j]);
					
					double lengthBytes=0;
					if(strFolderDoc !=null && strFolderDoc[j]!=null) {
						lengthBytes =  strFolderDoc[j].length();
					} else {
						continue;
					}
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
						String[] proFolderDocEmployee = request.getParameterValues("proFolderDocEmployee"+getProId()+"_"+folderTRId[i]+"_"+folderDocsTRId[j]);
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
						
						String[] proFolderDocPoc = request.getParameterValues("proFolderDocPoc"+getProId()+"_"+folderTRId[i]+"_"+folderDocsTRId[j]);
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
						"visibility,visibility_with_id,created_by,create_time) values(?,?,?,?, ?,?,?,?, ?,?)"); //,doc_or_image
						pst.setString(1, strFolderDocDescription[j]);
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(2, 1);
						} else {
							pst.setInt(2, 0);
						}
//						pst.setInt(2, uF.parseToInt(getStrAlignWith()));
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(3, getProId());
						} else {
							pst.setInt(3, uF.parseToInt(proFolderDocCategory));
						}
//						pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
						pst.setString(4, sbFDEmps.toString());
						pst.setString(5, "");
						pst.setString(6, "");
						pst.setInt(7, uF.parseToInt(folderDocDharingType[j]));
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
							"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description," +
							"doc_parent_id,is_edit,is_delete,is_cust_add,sharing_poc,doc_version,feed_id)" +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setInt(1, uF.parseToInt(clientId));
						pst.setInt(2, getProId());
						pst.setString(3, strFolderName[i]);
						pst.setInt(4, uF.parseToInt(strSessionEmpId));
//						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(6, "folder");
						pst.setInt(7, uF.parseToInt(proFolderId));
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
						
						
						uploadProjectFolderDocuments(con, ""+getProId(), strFolderDoc[j], strFileName, proFolderNameFolder, proDocumentId, feedId);
						
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
						
						proName = null;
						strCategory = null;
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
							pst.setInt(1, uF.parseToInt(proFolderDocTasks));
							rs = pst.executeQuery();
							
							while(rs.next()) {
								proName = rs.getString("pro_name");
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
						nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
						nF.setDomain(strDomain);
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						
						nF.request = request;
						nF.setStrOrgId((String)session.getAttribute(ORGID));
						nF.setEmailTemplate(true);
						
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							nF.setStrProjectName(proName);
						} else {
							nF.setStrCategoryName(strCategory);
						}
						nF.setStrDocumentName(strDocumentName);
						
						alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						alertAction = "DocumentListView.action";
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
						
						docCnt++;
					}
				}
				
				String[] SubFolderTR = request.getParameterValues("SubFolderTR" + getProId()+"_"+folderTRId[i]);
				String[] strSubFolderName = request.getParameterValues("strSubFolderName" + getProId()+"_"+folderTRId[i]);
				String[] proCategoryTypeSubFolder = request.getParameterValues("proCategoryTypeSubFolder" + getProId()+"_"+folderTRId[i]);
//				String[] proSubFolderTasks = request.getParameterValues("proSubFolderTasks" + getProId()+"_"+folderTRId[i]);
				String[] SubfolderSharingType = request.getParameterValues("SubfolderSharingType" + getProId()+"_"+folderTRId[i]);
				String[] strSubFolderDescription = request.getParameterValues("strSubFolderDescription" + getProId()+"_"+folderTRId[i]);
				
				String[] isSubFolderEdit = request.getParameterValues("isSubFolderEdit" + getProId()+"_"+folderTRId[i]);
				String[] isSubFolderDelete = request.getParameterValues("isSubFolderDelete" + getProId()+"_"+folderTRId[i]);
				
				for(int j = 0; SubFolderTR != null && j < SubFolderTR.length; j++) {
					
					String proSubFolderTasks = request.getParameter("proSubFolderTasks"+getProId()+"_"+folderTRId[i]+"_"+SubFolderTR[j]);
					String proSubFolderCategory = request.getParameter("proSubFolderCategory"+getProId()+"_"+folderTRId[i]+"_"+SubFolderTR[j]);
					
					String[] proSubFolderEmployee = request.getParameterValues("proSubFolderEmployee"+getProId()+"_"+folderTRId[i]+"_"+SubFolderTR[j]);
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
					
					String[] proSubFolderPoc = request.getParameterValues("proSubFolderPoc"+getProId()+"_"+folderTRId[i]+"_"+SubFolderTR[j]);
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
						"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description," +
						"doc_parent_id,is_edit,is_delete,is_cust_add,sharing_poc,doc_version)" +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(clientId));
					pst.setInt(2, getProId());
					pst.setString(3, strSubFolderName[j]);
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
//					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setString(6, "folder");
					pst.setInt(7, uF.parseToInt(proFolderId));
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
					
					List<String> alEmp1= null;
					if(proSubFolderEmployee != null) {
						alEmp1 = Arrays.asList(proSubFolderEmployee);
					}
					if(alEmp1 == null){
						alEmp1 = new ArrayList<String>();
					}
					
					List<String> alSharePoc1 = null;
					if(proSubFolderPoc != null) {
						alSharePoc1 = Arrays.asList(proSubFolderPoc);
					}
					if(alSharePoc1 == null){
						alSharePoc1 = new ArrayList<String>();
					}
					
					strDocumentName = "";
					pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
					pst.setInt(1, uF.parseToInt(proSubFolderId));
					rs = pst.executeQuery();
					while (rs.next()) {
						strDocumentName = rs.getString("folder_name");
					}
					rs.close();
					pst.close();
					
					proName = null;
					strCategory = null;
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
						pst.setInt(1, uF.parseToInt(proSubFolderTasks));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							proName = rs.getString("pro_name");
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
					nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
					nF.setDomain(strDomain);
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					
					nF.request = request;
					nF.setStrOrgId((String)session.getAttribute(ORGID));
					nF.setEmailTemplate(true);
					
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						nF.setStrProjectName(proName);
					} else {
						nF.setStrCategoryName(strCategory);
					}
					nF.setStrDocumentName(strDocumentName);
					
					alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					alertAction = "DocumentListView.action";
					for(String strEmp : alEmp1) {
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
					for(String strEmp : alSharePoc1) {
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
					File[] strSubFolderDoc = mpRequest.getFiles("strSubFolderDoc"+getProId()+"_"+SubFolderTR[j]);    //  
					String[] strSubFolderDocFileName = mpRequest.getFileNames("strSubFolderDoc"+getProId()+"_"+SubFolderTR[j]); 
					String[] proCategoryTypeSubFolderDoc = request.getParameterValues("proCategoryTypeSubFolderDoc"+getProId()+"_"+SubFolderTR[j]);
//					String[] proSubFolderDocTasks = request.getParameterValues("proSubFolderDocTasks"+getProId()+"_"+SubFolderTR[j]);
					String[] SubfolderDocDharingType = request.getParameterValues("SubfolderDocDharingType"+getProId()+"_"+SubFolderTR[j]);
					String[] strSubFolderDocDescription = request.getParameterValues("strSubFolderDocDescription"+getProId()+"_"+SubFolderTR[j]);
					String[] strSubFolderScopeDoc = request.getParameterValues("strSubFolderScopeDoc"+getProId()+"_"+SubFolderTR[j]);
					
					String[] isSubFolderDocEdit = request.getParameterValues("isSubFolderDocEdit"+getProId()+"_"+SubFolderTR[j]);
					String[] isSubFolderDocDelete = request.getParameterValues("isSubFolderDocDelete"+getProId()+"_"+SubFolderTR[j]);
					
					for(int k=0; SubfolderDocsTRId != null && k < SubfolderDocsTRId.length; k++) {
						
						String proSubFolderDocTasks = request.getParameter("proSubFolderDocTasks"+getProId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
						String proSubFolderDocCategory = request.getParameter("proSubFolderDocCategory"+getProId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
						
						double lengthBytes=0;
						if(strSubFolderDoc !=null && strSubFolderDoc[k]!=null) {
							lengthBytes =  strSubFolderDoc[k].length();
						} else {
							continue;
						}
						boolean isFileExist = false;
//						System.out.println("fileNames[k] ===>> " + fileNames[k] + " ----- " + proSubFolderNameFolder+"/"+fileNames[k]);
						String extenstion=FilenameUtils.getExtension(strSubFolderDocFileName[k]);	
						String strFileName = FilenameUtils.getBaseName(strSubFolderDocFileName[k]);
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
							String[] proSubFolderDocEmployee = request.getParameterValues("proSubFolderDocEmployee"+getProId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
							List<String> alFDEmployee = null;
							if(proSubFolderDocEmployee != null) {
								alFDEmployee = Arrays.asList(proSubFolderDocEmployee);
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
							
							String[] proSubFolderDocPoc = request.getParameterValues("proSubFolderDocPoc"+getProId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
							List<String> alFDPoc = null;
							if(proSubFolderDocPoc != null) {
								alFDPoc = Arrays.asList(proSubFolderDocPoc);
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
							"visibility,visibility_with_id,created_by,create_time) values(?,?,?,?, ?,?,?,?, ?,?)"); //,doc_or_image
							pst.setString(1, strSubFolderDocDescription[k]);
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(2, 1);
							} else {
								pst.setInt(2, 0);
							}
//							pst.setInt(2, uF.parseToInt(getStrAlignWith()));
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(3, getProId());
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
							pst.setInt(1, uF.parseToInt(clientId));
							pst.setInt(2, getProId());
							pst.setString(3, strSubFolderName[j]);
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
							
							
							uploadProjectFolderDocuments(con, ""+getProId(), strSubFolderDoc[k], strFileName, proSubFolderNameFolder, proDocumentId, feedId);
							
							/**
							 * Alerts
							 * */
							
							List<String> alEmp11= null;
							if(proSubFolderDocEmployee != null) {
								alEmp11 = Arrays.asList(proSubFolderDocEmployee);
							}
							if(alEmp11 == null){
								alEmp11 = new ArrayList<String>();
							}
							
							List<String> alSharePoc11 = null;
							if(proSubFolderDocPoc != null) {
								alSharePoc11 = Arrays.asList(proSubFolderDocPoc);
							}
							if(alSharePoc11 == null){
								alSharePoc11 = new ArrayList<String>();
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
							
							proName = null;
							strCategory = null;
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
								pst.setInt(1, uF.parseToInt(proSubFolderDocTasks));
								rs = pst.executeQuery();
								
								while(rs.next()) {
									proName = rs.getString("pro_name");
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
								nF.setStrProjectName(proName);
							} else {
								nF.setStrCategoryName(strCategory);
							}
							nF.setStrDocumentName(strDocumentName);
							
							alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
							alertAction = "DocumentListView.action";
							for(String strEmp : alEmp11) {
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
							for(String strEmp : alSharePoc11) {
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
							
							docCnt++;
						}
					}
				}
			}
			int docuCnt = docsTRId!=null ? docsTRId.length : 0;
			int foldCnt = folderTRId!=null ? folderTRId.length : 0;
			if(docuCnt>0 && foldCnt>0) {
				String proName = CF.getProjectNameById(con, getProId()+"");
				session.setAttribute(MESSAGE, SUCCESSM+docuCnt+" document added and "+foldCnt+" folder created successfully for "+proName+"."+END);
			} else if(foldCnt>0) {
				String proName = CF.getProjectNameById(con, getProId()+"");
				session.setAttribute(MESSAGE, SUCCESSM+foldCnt+" folder created successfully for "+proName+"."+END);
			} else if(docuCnt>0) {
				String proName = CF.getProjectNameById(con, getProId()+"");
				session.setAttribute(MESSAGE, SUCCESSM+docuCnt+" document added successfully for "+proName+"."+END);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void createEmpFolderForDocs(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			String clientId = CF.getClientIdByProjectId(con, uF, getProId()+"");
//			System.out.println("clientId ===>> " + clientId);
			
			String mainPathWithOrg = CF.getProjectDocumentFolder()+"/"+strOrgId;
			File fileOrg = new File(mainPathWithOrg);
			if (!fileOrg.exists()) {
				if (fileOrg.mkdir()) {
					System.out.println("Directory is created!");
				}
			}
			
			String mainPath = mainPathWithOrg+"/Projects";
			File file = new File(mainPath);
			if (!file.exists()) {
				if (file.mkdir()) {
					System.out.println("Directory is created!");
				}
			}
			
			String proNameFolder = mainPath +"/"+getProId();
			File file1 = new File(proNameFolder);
			if (!file1.exists()) {
				if (file1.mkdir()) {
					System.out.println("Directory is created!");
				}
			} 
			
			MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;   
			  
			File[] strDoc = mpRequest.getFiles("strDoc"+getProId()+"_"+getTaskId());      
			String[] strDocFileName = mpRequest.getFileNames("strDoc"+getProId()+"_"+getTaskId());
			
			String[] docsTRId = request.getParameterValues("docsTRId"+getProId()+"_"+getTaskId());
//			String[] proDocTasks = request.getParameterValues("proDocTasks"+getProId()+"_"+getTaskId());
			String[] docSharingType = request.getParameterValues("docSharingType"+getProId()+"_"+getTaskId());
			String[] proCategoryTypeDoc = request.getParameterValues("proCategoryTypeDoc"+getProId()+"_"+getTaskId());
			String[] strDocDescription = request.getParameterValues("strDocDescription"+getProId()+"_"+getTaskId());
			String[] strScopeDoc = request.getParameterValues("strScopeDoc"+getProId()+"_"+getTaskId());
			
			String[] isDocEdit = request.getParameterValues("isDocEdit"+getProId()+"_"+getTaskId());
			String[] isDocDelete = request.getParameterValues("isDocDelete"+getProId()+"_"+getTaskId());
			
			for(int j=0; docsTRId != null && j<docsTRId.length; j++) {
				String proDocTasks = request.getParameter("proDocTasks"+getProId()+"_"+getTaskId()+"_"+docsTRId[j]);
				String proDocCategory = request.getParameter("proDocCategory"+getProId()+"_"+getTaskId()+"_"+docsTRId[j]);
				
				double lengthBytes = 0;
				if(strDoc !=null && strDoc[j]!=null) {
					lengthBytes =  strDoc[j].length();
				} else {
					continue;
				}
				
				boolean isFileExist = false;
				
				String extenstion=FilenameUtils.getExtension(strDocFileName[j]);	
				String strFileName = FilenameUtils.getBaseName(strDocFileName[j]);
				strFileName = strFileName+"v1."+extenstion;
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
//					System.out.println("4 file save path=====>"+proNameFolder+"/"+strDocFileName[j]);
					String[] proDocEmployee = request.getParameterValues("proDocEmployee"+getProId()+"_"+getTaskId()+"_"+docsTRId[j]);
					List<String> alEmployee = null;
					if(proDocEmployee != null) {
						alEmployee = Arrays.asList(proDocEmployee);
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
					
					String[] proDocPoc = request.getParameterValues("proDocPoc"+getProId()+"_"+getTaskId()+"_"+docsTRId[j]);
					List<String> alPoc = null;
					if(proDocPoc != null) {
						alPoc = Arrays.asList(proDocPoc);
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
						pst.setInt(3, getProId());
					} else {
						pst.setInt(3, uF.parseToInt(proDocCategory));
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
					
					pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,added_by, entry_date,folder_file_type," +
						"pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description,doc_parent_id," +
						"is_edit,is_delete,is_cust_add,sharing_poc,doc_version,feed_id) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(clientId));
					pst.setInt(2, getProId());
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
//					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setString(5, "file");
					pst.setInt(6, 0);
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						pst.setInt(7, uF.parseToInt(proDocTasks));
					} else {
						pst.setInt(7, uF.parseToInt(proDocCategory));
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
					
					uploadProjectDocuments(con, getProId()+"", strDoc[j], strFileName, proNameFolder, proDocumentId, feedId);
					
					/**
					 * Alerts
					 * */
					
					List<String> alEmp= null;
					if(proDocEmployee != null) {
						alEmp = Arrays.asList(proDocEmployee);
					}
					if(alEmp == null){
						alEmp = new ArrayList<String>();
					}
					
					List<String> alSharePoc = null;
					if(proDocPoc != null) {
						alSharePoc = Arrays.asList(proDocPoc);
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
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
						pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
						pst.setInt(1, uF.parseToInt(proDocTasks));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							proName = rs.getString("pro_name");
						}
						rs.close();
						pst.close();
					} else {
						pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
						pst.setInt(1, uF.parseToInt(proDocCategory));
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
					
					if(uF.parseToInt(proCategoryTypeDoc[j]) == 1) {
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
				}
			}
			

			String[] folderTRId = request.getParameterValues("folderTRId"+getProId()+"_"+getTaskId());
			String[] strFolderName = request.getParameterValues("strFolderName"+getProId()+"_"+getTaskId());
			String[] strFolderDescription = request.getParameterValues("strFolderDescription"+getProId()+"_"+getTaskId());
			String[] proCategoryTypeFolder = request.getParameterValues("proCategoryTypeFolder"+getProId()+"_"+getTaskId());
//			String[] proFolderTasks = request.getParameterValues("proFolderTasks"+getProId()+"_"+getTaskId());
			String[] folderSharingType = request.getParameterValues("folderSharingType"+getProId()+"_"+getTaskId());
			
			String[] isFolderEdit = request.getParameterValues("isFolderEdit"+getProId()+"_"+getTaskId());
			String[] isFolderDelete = request.getParameterValues("isFolderDelete"+getProId()+"_"+getTaskId());
			
//			System.out.println("getStrClient 111 ===>>>> " + getStrClient());
			int docCnt = 0;
			for (int i=0; folderTRId != null && i<folderTRId.length; i++) {
				
				String proFolderTasks = request.getParameter("proFolderTasks"+getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				String proFolderCategory = request.getParameter("proFolderCategory"+getProId()+"_"+getTaskId()+"_"+folderTRId[i]);

				String[] proFolderEmployee = request.getParameterValues("proFolderEmployee"+getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				List<String> alEmployee = null;
				if(proFolderEmployee != null) {
					alEmployee = Arrays.asList(proFolderEmployee);
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
				
				String[] proFolderPoc = request.getParameterValues("proFolderPoc"+getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				List<String> alPoc = null;
				if(proFolderPoc != null) {
					alPoc = Arrays.asList(proFolderPoc);
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
				
				String proFolderNameFolder = proNameFolder +"/"+ strFolderName[i];
				
				File file2 = new File(proFolderNameFolder);
				if (!file2.exists()) {
					if (file2.mkdir()) {
						System.out.println("Directory is created!");
					}
				}
				
				pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date,folder_file_type," +
					"pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description,doc_parent_id,is_edit," +
					"is_delete,is_cust_add,sharing_poc,doc_version) " +
					"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setInt(1, uF.parseToInt(clientId));
				pst.setInt(2, getProId());
				pst.setString(3, strFolderName[i]);
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
//				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				pst.setString(6, "folder");
				pst.setInt(7, 0);
				if(uF.parseToInt(proCategoryTypeFolder[i]) == 1) {
					pst.setInt(8, uF.parseToInt(proFolderTasks));
				} else {
					pst.setInt(8, uF.parseToInt(proFolderCategory));
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
		//		System.out.println("pst====>"+pst);
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
				
				List<String> alEmp= null;
				if(proFolderEmployee != null) {
					alEmp = Arrays.asList(proFolderEmployee);
				}
				if(alEmp == null){
					alEmp = new ArrayList<String>();
				}
				
				List<String> alSharePoc = null;
				if(proFolderPoc != null) {
					alSharePoc = Arrays.asList(proFolderPoc);
				}
				if(alSharePoc == null){
					alSharePoc = new ArrayList<String>();
				}
				
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
					pst.setInt(1, uF.parseToInt(proFolderTasks));
					rs = pst.executeQuery();
					
					while(rs.next()) {
						proName = rs.getString("pro_name");
					}
					rs.close();
					pst.close();
				} else {
					pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
					pst.setInt(1, uF.parseToInt(proFolderCategory));
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
				for(String strEmp : alSharePoc){
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
				
				
				String[] folderDocsTRId = request.getParameterValues("folderDocsTRId" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				File[] strFolderDoc = mpRequest.getFiles("strFolderDoc" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);  
				
				String[] strFolderDocFileName = mpRequest.getFileNames("strFolderDoc" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
//				String[] proFolderDocTasks = request.getParameterValues("proFolderDocTasks" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				String[] folderDocDharingType = request.getParameterValues("folderDocDharingType" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				String[] proCategoryTypeFolderDoc = request.getParameterValues("proCategoryTypeFolderDoc" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				String[] strFolderDocDescription = request.getParameterValues("strFolderDocDescription" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				String[] strFolderScopeDoc = request.getParameterValues("strFolderScopeDoc" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				
				String[] isFolderDocEdit = request.getParameterValues("isFolderDocEdit" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				String[] isFolderDocDelete = request.getParameterValues("isFolderDocDelete" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				
				for(int j=0; folderDocsTRId != null && j<folderDocsTRId.length; j++) {
				
					String proFolderDocTasks = request.getParameter("proFolderDocTasks" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]+"_"+folderDocsTRId[j]);
					String proFolderDocCategory = request.getParameter("proFolderDocCategory" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]+"_"+folderDocsTRId[j]);
					
					double lengthBytes = 0;
					if(strFolderDoc !=null && strFolderDoc[j]!=null) {
						lengthBytes = strFolderDoc[j].length();
					} else {
						continue;
					}
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
//						System.out.println("5 file save path=====>"+proFolderNameFolder+"/"+strFolderDocFileName[j]);
						
						String[] proFolderDocEmployee = request.getParameterValues("proFolderDocEmployee" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]+"_"+folderDocsTRId[j]);
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
						
						String[] proFolderDocPoc = request.getParameterValues("proFolderDocPoc" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]+"_"+folderDocsTRId[j]);
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
						"visibility,visibility_with_id,created_by,create_time) values(?,?,?,?, ?,?,?,?, ?,?)"); //,doc_or_image
						pst.setString(1, strFolderDocDescription[j]);
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(2, 1);
						} else {
							pst.setInt(2, 0);
						}
//						pst.setInt(2, uF.parseToInt(getStrAlignWith()));
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst.setInt(3, getProId());
						} else {
							pst.setInt(3, uF.parseToInt(proFolderDocCategory));
						}
//						pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
						pst.setString(4, sbFDEmps.toString());
						pst.setString(5, "");
						pst.setString(6, "");
						pst.setInt(7, uF.parseToInt(folderDocDharingType[j]));
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
						
						pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,folder_name,added_by, entry_date,folder_file_type," +
							"pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description,doc_parent_id," +
							"is_edit,is_delete,is_cust_add,sharing_poc,doc_version,feed_id)" +
							"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setInt(1, uF.parseToInt(clientId));
						pst.setInt(2, getProId());
						pst.setString(3, strFolderName[i]);
						pst.setInt(4, uF.parseToInt(strSessionEmpId));
//						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
						pst.setString(6, "folder");
						pst.setInt(7, uF.parseToInt(proFolderId));
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
						
						
						uploadProjectFolderDocuments(con, ""+getProId(), strFolderDoc[j], strFileName, proFolderNameFolder, proDocumentId, feedId);
						
						
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
						pst.setInt(1, uF.parseToInt(proFolderId));
						rs = pst.executeQuery();
						while (rs.next()) {
							strDocumentName = rs.getString("folder_name");
						}
						rs.close();
						pst.close();
						
						proName = null;
						strCategory = null;
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
							pst.setInt(1, uF.parseToInt(proFolderDocTasks));
							rs = pst.executeQuery();
							
							while(rs.next()) {
								proName = rs.getString("pro_name");
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
						nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
						nF.setDomain(strDomain);
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						
						nF.request = request;
						nF.setStrOrgId((String)session.getAttribute(ORGID));
						nF.setEmailTemplate(true);
						
						if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
							nF.setStrProjectName(proName);
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
//							userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
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
						
						
						docCnt++;
					}
				}
				
				String[] SubFolderTR = request.getParameterValues("SubFolderTR" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				String[] strSubFolderName = request.getParameterValues("strSubFolderName" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				String[] proCategoryTypeSubFolder = request.getParameterValues("proCategoryTypeSubFolder" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
//				String[] proSubFolderTasks = request.getParameterValues("proSubFolderTasks" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				String[] SubfolderSharingType = request.getParameterValues("SubfolderSharingType" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				String[] strSubFolderDescription = request.getParameterValues("strSubFolderDescription" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				
				String[] isSubFolderEdit = request.getParameterValues("isSubFolderEdit" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				String[] isSubFolderDelete = request.getParameterValues("isSubFolderDelete" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]);
				
				for(int j = 0; SubFolderTR != null && j < SubFolderTR.length; j++) {
					
					String proSubFolderTasks = request.getParameter("proSubFolderTasks" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]+"_"+SubFolderTR[j]);
					String proSubFolderCategory = request.getParameter("proSubFolderCategory" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]+"_"+SubFolderTR[j]);
					
					String[] proSubFolderEmployee = request.getParameterValues("proSubFolderEmployee" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]+"_"+SubFolderTR[j]);
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
					
					String[] proSubFolderPoc = request.getParameterValues("proSubFolderPoc" + getProId()+"_"+getTaskId()+"_"+folderTRId[i]+"_"+SubFolderTR[j]);
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
						"folder_file_type,pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description," +
						"doc_parent_id,is_edit,is_delete,is_cust_add,sharing_poc,doc_version)" +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(clientId));
					pst.setInt(2, getProId());
					pst.setString(3, strSubFolderName[j]);
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
//					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setString(6, "folder");
					pst.setInt(7, uF.parseToInt(proFolderId));
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
					
					List<String> alEmp1= null;
					if(proSubFolderEmployee != null) {
						alEmp1 = Arrays.asList(proSubFolderEmployee);
					}
					if(alEmp1 == null){
						alEmp1 = new ArrayList<String>();
					}
					
					List<String> alSharePoc1 = null;
					if(proSubFolderPoc != null) {
						alSharePoc1 = Arrays.asList(proSubFolderPoc);
					}
					if(alSharePoc1 == null){
						alSharePoc1 = new ArrayList<String>();
					}
					
					strDocumentName = "";
					pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
					pst.setInt(1, uF.parseToInt(proFolderId));
					rs = pst.executeQuery();
					while (rs.next()) {
						strDocumentName = rs.getString("folder_name");
					}
					rs.close();
					pst.close();
					
					proName = null;
					strCategory = null;
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
						pst.setInt(1, uF.parseToInt(proSubFolderTasks));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							proName = rs.getString("pro_name");
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
					nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
					nF.setDomain(strDomain);
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					
					nF.request = request;
					nF.setStrOrgId((String)session.getAttribute(ORGID));
					nF.setEmailTemplate(true);
					
					if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
						nF.setStrProjectName(proName);
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
//						userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
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
					File[] strSubFolderDoc = mpRequest.getFiles("strSubFolderDoc"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);    //  
					String[] strSubFolderDocFileName = mpRequest.getFileNames("strSubFolderDoc"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]); 
					
					String[] proCategoryTypeSubFolderDoc = request.getParameterValues("proCategoryTypeSubFolderDoc"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);
//					String[] proSubFolderDocTasks = request.getParameterValues("proSubFolderDocTasks"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);
					String[] SubfolderDocDharingType = request.getParameterValues("SubfolderDocDharingType"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);
					String[] strSubFolderDocDescription = request.getParameterValues("strSubFolderDocDescription"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);
					String[] strSubFolderScopeDoc = request.getParameterValues("strSubFolderScopeDoc"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);
					
					String[] isSubFolderDocEdit = request.getParameterValues("isSubFolderDocEdit"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);
					String[] isSubFolderDocDelete = request.getParameterValues("isSubFolderDocDelete"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]);
					
					for(int k=0; SubfolderDocsTRId != null && k < SubfolderDocsTRId.length; k++) {
						
						String proSubFolderDocTasks = request.getParameter("proSubFolderDocTasks" +getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
						String proSubFolderDocCategory = request.getParameter("proSubFolderDocCategory" +getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
						
						double lengthBytes=0;
						if(strSubFolderDoc !=null && strSubFolderDoc[k]!=null) {
							lengthBytes =  strSubFolderDoc[k].length();
						} else {
							continue;
						}
						boolean isFileExist = false;
//						System.out.println("fileNames[k] ===>> " + fileNames[k] + " ----- " + proSubFolderNameFolder+"/"+fileNames[k]);
						
						String extenstion=FilenameUtils.getExtension(strSubFolderDocFileName[k]);	
						String strFileName = FilenameUtils.getBaseName(strSubFolderDocFileName[k]);
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
//							System.out.println("6 file save path=====>"+proSubFolderNameFolder+"/"+strSubFolderDocFileName[k]);
							String[] proSubFolderDocEmployee = request.getParameterValues("proSubFolderDocEmployee"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
							List<String> alFDEmployee = null;
							if(proSubFolderDocEmployee != null) {
								alFDEmployee = Arrays.asList(proSubFolderDocEmployee);
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
							
							String[] proSubFolderDocPoc = request.getParameterValues("proSubFolderDocPoc"+getProId()+"_"+getTaskId()+"_"+SubFolderTR[j]+"_"+SubfolderDocsTRId[k]);
							List<String> alFDPoc = null;
							if(proSubFolderDocPoc != null) {
								alFDPoc = Arrays.asList(proSubFolderDocPoc);
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
							"visibility,visibility_with_id,created_by,create_time) values(?,?,?,?, ?,?,?,?, ?,?)"); //,doc_or_image
							pst.setString(1, strSubFolderDocDescription[k]);
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(2, 1);
							} else {
								pst.setInt(2, 0);
							}
//							pst.setInt(2, uF.parseToInt(getStrAlignWith()));
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst.setInt(3, getProId());
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
							pst.setInt(1, uF.parseToInt(clientId));
							pst.setInt(2, getProId());
							pst.setString(3, strSubFolderName[j]);
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
							
							
							uploadProjectFolderDocuments(con, ""+getProId(), strSubFolderDoc[k], strFileName, proSubFolderNameFolder, proDocumentId, feedId);
							
							/**
							 * Alerts
							 * */
							
							List<String> alEmp11= null;
							if(proSubFolderDocEmployee != null) {
								alEmp11 = Arrays.asList(proSubFolderDocEmployee);
							}
							if(alEmp11 == null){
								alEmp11 = new ArrayList<String>();
							}
							
							List<String> alSharePoc11 = null;
							if(proSubFolderDocPoc != null) {
								alSharePoc11 = Arrays.asList(proSubFolderDocPoc);
							}
							if(alSharePoc11 == null){
								alSharePoc11 = new ArrayList<String>();
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
							
							proName = null;
							strCategory = null;
							if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
								pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
								pst.setInt(1, uF.parseToInt(proSubFolderDocTasks));
								rs = pst.executeQuery();
								
								while(rs.next()) {
									proName = rs.getString("pro_name");
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
								nF.setStrProjectName(proName);
							} else {
								nF.setStrCategoryName(strCategory);
							}
							nF.setStrDocumentName(strDocumentName);
							
							alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
							alertAction = "DocumentListView.action";
							for(String strEmp : alEmp11) {
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strEmp.trim());
//								userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
								userAlerts.setStatus(INSERT_TR_ALERT);
								Thread t = new Thread(userAlerts);
								t.run();
								
								//Mail
								nF.setStrEmpId(strEmp.trim());
								nF.sendNotifications();
							}
							for(String strEmp : alSharePoc11) {
								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
								userAlerts.setStrDomain(strDomain);
								userAlerts.setStrEmpId(strEmp.trim());
								userAlerts.setStrOther("other");
								alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
								alertAction = "DocumentListView.action";
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
			
			int docuCnt = docsTRId!=null ? docsTRId.length : 0;
			int foldCnt = folderTRId!=null ? folderTRId.length : 0;
			if(docuCnt>0 && foldCnt>0) {
				String taskName = CF.getProjectTaskNameByTaskId(con, uF, getTaskId());
				session.setAttribute(MESSAGE, SUCCESSM+docuCnt+ " document added and "+foldCnt+" folder created successfully for "+taskName+"."+END);
			} else if(foldCnt>0) {
				String taskName = CF.getProjectTaskNameByTaskId(con, uF, getTaskId());
				session.setAttribute(MESSAGE, SUCCESSM+foldCnt+" folder created successfully for "+taskName+"."+END);
			} else if(docuCnt>0) {
				String taskName = CF.getProjectTaskNameByTaskId(con, uF, getTaskId());
				session.setAttribute(MESSAGE, SUCCESSM+docuCnt+" document added successfully for "+taskName+"."+END);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getProjectDocumentDetails(UtilityFunctions uF) {
		try {
			System.out.println("getProId() ===>> " + getProId());
			PreAddNewProject1 project1 = new PreAddNewProject1();
			project1.session = session;
			project1.request = request;
			project1.CF = CF;
			project1.strOrgId = strOrgId;
			project1.setPro_id(""+getProId());
			project1.setFromPage(getFromPage());
			project1.setStrTaskId(getTaskId());
			project1.strSessionEmpId = strSessionEmpId;
			project1.strUserType = strUserType;
			project1.getProjectAndTaskDocuments(uF);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbProTasks = new StringBuilder();
			if(getFromPage() == null || !getFromPage().equalsIgnoreCase("MyProject")) {
				sbProTasks.append("<option value='0'>Full Project</option>");
			}
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
						sbProTasks.append("<option value='"+rs.getString("task_id")+"'>" + sbTaskName.toString().trim() + " [ST]" +"</option>");
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
						sbProTasks.append("<option value='"+rs.getString("task_id")+"'>" + sbTaskName.toString().trim() +"</option>");
					}
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
			while (rs.next()) {
				
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
			while (rs.next()) {
				sbProCategory.append("<option value='" + rs.getString("project_category_id") + "'>" + rs.getString("project_category").trim()+ "</option>");
			}
			rs.close();
			pst.close();
//			sbProEmp.append("</select>");
			request.setAttribute("sbProCategory", sbProCategory.toString());
			
//			System.out.println("sbProTasks======>"+sbProTasks.toString());
//			System.out.println("sbProEmp======>"+sbProEmp.toString());
//			System.out.println("sbProCategory======>"+sbProCategory.toString());
			StringBuilder sbProSPOC = new StringBuilder();
			pst = con.prepareStatement("select poc_id, contact_fname, contact_lname from client_poc where poc_id in (select poc from projectmntnc " +
					"where org_id=? and client_id in (select client_id from projectmntnc where pro_id=?)) order by contact_fname,contact_lname");
			pst.setInt(1, uF.parseToInt(strOrgId));
			pst.setInt(2, getProId());
			rs = pst.executeQuery();
			while (rs.next()) {
				sbProSPOC.append("<option value='" + rs.getString("poc_id") + "'>" + uF.showData(rs.getString("contact_fname"), "").trim()+" "+uF.showData(rs.getString("contact_lname"), "").trim()+ "</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbProSPOC", sbProSPOC.toString());
//			System.out.println("sbProSPOC======>"+sbProSPOC.toString());
			
		} catch (Exception e) {
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	private void uploadProjectFolderDocuments(Connection con, String proId, File contentFile, String contentFileName, String realFolderPath, String proDocumentId, String feedId) {

		PreparedStatement pst = null;
		try {
			
			double lengthBytes=0;
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
			
			double lengthBytes=0;
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
	
	private void getMyProjectDocumentDetails(UtilityFunctions uF) {

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

			Map<String, List<List<String>>> hmFolderDocs = new HashMap<String, List<List<String>>>();
			List<List<String>> listDocs = new ArrayList<List<String>>();
//			pst = con.prepareStatement("select * from project_document_details where pro_id=? and pro_folder_id > 0 order by pro_document_id");
			pst = con.prepareStatement("select * from project_document_details where file_size is not null and (sharing_type =0 or sharing_type = 1 " +
					"or sharing_resources like '%,"+strSessionEmpId+",%') and (align_with = 0 or align_with = ?) and pro_id=? and " +
					" pro_folder_id > 0 order by pro_document_id ");
			pst.setInt(1, uF.parseToInt(getTaskId()));
			pst.setInt(2, getProId());
//			System.out.println("pst ====>> "+pst);
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
//			pst = con.prepareStatement("select * from project_document_details where pro_id=? and pro_folder_id = 0 order by pro_document_id desc");
			pst = con.prepareStatement("select * from project_document_details where ((file_size is not null and folder_file_type = 'file') or " +
					"(file_size is null and folder_file_type = 'folder')) and (sharing_type =0 or sharing_type = 1 " +
					"or sharing_resources like '%,"+strSessionEmpId+",%') and (align_with = 0 or align_with = ?) and pro_id=? and " +
					" pro_folder_id = 0 order by pro_document_id ");
			pst.setInt(1, uF.parseToInt(getTaskId()));
			pst.setInt(2, getProId());
//			System.out.println("pst ====>> "+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
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
				hmFolderDocsData.put(rs.getString("pro_document_id"), innerList);
			}
			rs.close();
			pst.close();
//			System.out.println("hmFolderDocsData ===>>> " + hmFolderDocsData);
			request.setAttribute("hmFolderDocsData", hmFolderDocsData);
			
			
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

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}
	
}
