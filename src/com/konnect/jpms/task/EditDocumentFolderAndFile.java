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

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UploadProjectDocuments;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditDocumentFolderAndFile extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;

	String msg;
	CommonFunctions CF; 
	HttpSession session1;
	String strSessionEmpId;
	String strOrgId;
	String strUserType; 
	
	String folderName;
	String strOrg;
	String operation;
	String strId;
	String type;
	
	String proCategoryTypeFolder;
	String strFolderDescription;
	String folderSharingType;
	
	String strOrgCategory;
	String strOrgProject;
	String[] strOrgResources;

	List<FillEmployee> resourceList;
	
	String filePath;
	String fileDir;
	File strFolderDoc;
	String strFolderDocFileName;
	String strFolderScopeDoc;
	String isFolderDocEdit;
	String isFolderDocDelete;
	String[] strOrgPocFolderDoc;
	
	String tableId;
	
	String fromPage;
	
	public String execute() throws Exception {
		session1 = request.getSession();
		strSessionEmpId = (String)session1.getAttribute(EMPID);
		strOrgId = (String)session1.getAttribute(ORGID);
		CF = (CommonFunctions) session1.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		strUserType = (String) session1.getAttribute(BASEUSERTYPE);
		request.setAttribute("strUserType", strUserType);
		
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("getOperation() ===>> " + getOperation());
//		System.out.println("getType() ===>> " + getType());
		getFolderAndDocuments(uF);
		
		if(getOperation() != null && getOperation().equals("U")) {
			if(getType() != null && (getType().equals("F") || getType().equals("SF"))) {
				updateFolderSetting(uF);
				createFolderForDocs(uF);
			} else {
				insertUpdatedDocs(uF);
			}
			if(getFromPage()!=null && getFromPage().equals("COMMUNICATION")) {
				return "updateCommunication";
			} else {
				return SUCCESS;
			}
		} 
		return LOAD;
	}

	public void getFolderAndDocuments(UtilityFunctions uF) {

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
			
			Map<String, String> hmFileIcon = CF.getFileIcon();
			request.setAttribute("hmFileIcon",hmFileIcon);
			

			pst = con.prepareStatement("select * from project_document_details where pro_document_id=?");
			pst.setInt(1, uF.parseToInt(getStrId()));
			rs = pst.executeQuery();
			Map<String, String> hmProDocumentDetails = new HashMap<String, String>();
			
			while (rs.next()) {
				setFolderName(rs.getString("folder_name"));
				hmProDocumentDetails.put("PRO_ID", rs.getString("pro_id"));
				hmProDocumentDetails.put("FOLDER_NAME", rs.getString("folder_name"));
				hmProDocumentDetails.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmProDocumentDetails.put("DOCUMENT_SCOPE", rs.getString("scope_document"));
				hmProDocumentDetails.put("ALIGN_WITH", rs.getString("align_with"));
				hmProDocumentDetails.put("SHARING_TYPE", rs.getString("sharing_type"));
				hmProDocumentDetails.put("SHARING_RESOURCES", rs.getString("sharing_resources"));
				
				hmProDocumentDetails.put("CATEGORY", rs.getString("project_category"));
				hmProDocumentDetails.put("DESCRIPTION", rs.getString("description"));
				
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
			request.setAttribute("hmProDocumentDetails", hmProDocumentDetails);
//			System.out.println("hmProDocumentDetails ======>> " + hmProDocumentDetails);
			
			int categoryId = 0;
			String strProjectSelect = "";
			String strOtherSelect = "";
			if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 1) {
				strProjectSelect = "selected";
				strOtherSelect = "";
			} else if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 2) {
				strProjectSelect = "";
				strOtherSelect = "selected";
				categoryId = uF.parseToInt(hmProDocumentDetails.get("ALIGN_WITH"));
			}
			
			StringBuilder sbProCategoryTypeFolder = new StringBuilder("<option value=\"1\" "+strProjectSelect+">Project</option><option value=\"2\" "+strOtherSelect+">Category</option>");
			
			request.setAttribute("sbProCategoryTypeFolder", sbProCategoryTypeFolder.toString());
			
			
			
			StringBuilder sbOrgProjects = new StringBuilder();
			pst = con.prepareStatement("select * from projectmntnc where org_id=? order by pro_name");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbOrgProjects.append("<option value='"+rs.getString("pro_id")+"'");
				if(uF.parseToInt(hmProDocumentDetails.get("PRO_ID")) == rs.getInt("pro_id")) {
					sbOrgProjects.append(" selected");
				}
				sbOrgProjects.append(">"+rs.getString("pro_name")+"</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbOrgProjects", sbOrgProjects.toString());
//			System.out.println("sbOrgProjects ======>> " + sbOrgProjects);

			StringBuilder sbOrgCategory = new StringBuilder();
			pst = con.prepareStatement("select * from project_category_details where org_id=? and project_category_id>1 order by project_category");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbOrgCategory.append("<option value='"+rs.getString("project_category_id")+"'");
				if(categoryId == rs.getInt("project_category_id")) {
					sbOrgCategory.append(" selected");
				}
				sbOrgCategory.append(">"+rs.getString("project_category")+"</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbOrgCategory", sbOrgCategory.toString());
//			System.out.println("sbOrgCategory ======>> " + sbOrgCategory);
			
			
			StringBuilder sbProSharingType = new StringBuilder();
			sbProSharingType.append("<option value=\"0\"");
			if(hmProDocumentDetails.get("SHARING_TYPE") != null && hmProDocumentDetails.get("SHARING_TYPE").equals("0")) { 
				sbProSharingType.append(" selected"); 
			}
			sbProSharingType.append(">Public</option>");
			sbProSharingType.append("<option value=\"1\"");
			if(hmProDocumentDetails.get("SHARING_TYPE") != null && hmProDocumentDetails.get("SHARING_TYPE").equals("1")) { 
				sbProSharingType.append(" selected"); 
			}
			sbProSharingType.append(">Private Team</option>");
			sbProSharingType.append("<option value=\"2\"");
			if(hmProDocumentDetails.get("SHARING_TYPE") != null && hmProDocumentDetails.get("SHARING_TYPE").equals("2")) { 
				sbProSharingType.append(" selected"); 
			}
			sbProSharingType.append(">Individual Resource</option>");
			request.setAttribute("sbProSharingType", sbProSharingType.toString());
			
			
			List<String> existResourceList = new ArrayList<String>();
			
			if(hmProDocumentDetails.get("SHARING_RESOURCES") != null && !hmProDocumentDetails.get("SHARING_RESOURCES").trim().equals("")) {
				existResourceList = Arrays.asList(hmProDocumentDetails.get("SHARING_RESOURCES").split(","));
			}
			resourceList = new FillEmployee(request).fillEmployeeName(null, null, uF.parseToInt(strOrgId), 0, session1);
			StringBuilder sbOrgResources= new StringBuilder();
			for(int i=0; resourceList!=null && i<resourceList.size(); i++) {
				sbOrgResources.append("<option value='"+resourceList.get(i).getEmployeeId()+"'");
				if(existResourceList.contains(resourceList.get(i).getEmployeeId())) {
					sbOrgResources.append(" selected");
				}
				sbOrgResources.append(">"+resourceList.get(i).getEmployeeName()+"</option>");
			}
			request.setAttribute("sbOrgResources", sbOrgResources.toString());
			
			List<String> existPocList = new ArrayList<String>();
			if(hmProDocumentDetails.get("SHARING_POC") != null && !hmProDocumentDetails.get("SHARING_POC").trim().equals("")) {
				existPocList = Arrays.asList(hmProDocumentDetails.get("SHARING_POC").split(","));
			}
			
			StringBuilder sbOrgSPOC = new StringBuilder();
//			pst = con.prepareStatement("select poc_id, contact_fname, contact_lname from client_poc where poc_id in (select poc from projectmntnc " +
//					"where org_id=? and client_id in (select client_id from projectmntnc where pro_id=?)) order by contact_fname,contact_lname");
//			pst.setInt(1, uF.parseToInt(strOrgId));
//			pst.setInt(2, uF.parseToInt(hmProDocumentDetails.get("PRO_ID")));
			pst = con.prepareStatement("select poc_id, contact_fname, contact_lname from client_poc where poc_id in (select poc from projectmntnc " +
			"where org_id=?) order by contact_fname,contact_lname");
			pst.setInt(1, uF.parseToInt(strOrgId));
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
			if(getStrOrgResources() != null) {
				alEmployee = Arrays.asList(getStrOrgResources());
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
			
			List<String> alPoc = null;
			if(getStrOrgPocFolderDoc() != null) {
				alPoc = Arrays.asList(getStrOrgPocFolderDoc());
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
			
//			pst = con.prepareStatement("update project_document_details set align_with=?,sharing_type=?,sharing_resources=?,project_category=?," +
//					"description=?,pro_id=? where pro_document_id=?");
			pst = con.prepareStatement("update project_document_details set sharing_type=?,sharing_resources=?,description=?,is_edit=?,is_delete=?," +
					"is_cust_add=?,sharing_poc=? where pro_document_id=?");
			pst.setInt(1, uF.parseToInt(getFolderSharingType()));
			pst.setString(2, sbEmps.toString());
			pst.setString(3, getStrFolderDescription());
			pst.setBoolean(4, uF.parseToBoolean(getIsFolderDocEdit()));
			pst.setBoolean(5, uF.parseToBoolean(getIsFolderDocDelete()));
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				pst.setBoolean(6, true);
			} else {
				pst.setBoolean(6, false);
			}
			pst.setString(7, sbPoc.toString());
			pst.setInt(8, uF.parseToInt(getStrId()));
			
//			System.out.println("pst ====> " + pst);
			pst.executeUpdate();
			pst.close();
			/*if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
				pst.setInt(1, 0);
			} else {
				pst.setInt(1, uF.parseToInt(getStrOrgCategory()));
			}
			pst.setInt(2, uF.parseToInt(getFolderSharingType()));
			pst.setString(3, sbEmps.toString());
			pst.setInt(4, uF.parseToInt(getProCategoryTypeFolder()));
			pst.setString(5, getStrFolderDescription());
			if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
				pst.setInt(6, uF.parseToInt(getStrOrgProject()));
			} else {
				pst.setInt(6, 0);
			}
			pst.setInt(7, uF.parseToInt(getStrId()));*/
			
			
			
			
			String strDocumentName = "";
			String strProCategoryType = "";
			String strProId = "";
			String strCategoryId = "";
			pst = con.prepareStatement("select * from project_document_details where pro_document_id=?");
			pst.setInt(1, uF.parseToInt(getStrId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				strDocumentName = rs.getString("folder_name");
				strProCategoryType = rs.getString("project_category");
				strProId = rs.getString("pro_id");
				strCategoryId = rs.getString("align_with");
			}
			rs.close();
			pst.close();
			
			
			String alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
			String alertAction = "DocumentListView.action";
			
			/**
			 * Alerts
			 * */
			
			String proName1 = null;
			String strCategory = null;
			if(uF.parseToInt(strProCategoryType) == 1) {
				pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
				pst.setInt(1, uF.parseToInt(strProId));
				rs = pst.executeQuery();
				
				while(rs.next()) {
					proName1 = rs.getString("pro_name");
				}
				rs.close();
				pst.close();
			} else {
				pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
				pst.setInt(1, uF.parseToInt(strCategoryId));
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
			nF.setStrOrgId((String)session1.getAttribute(ORGID));
			nF.setEmailTemplate(true);
			
			if(uF.parseToInt(strProCategoryType) == 1) {
				nF.setStrProjectName(proName1);
			} else {
				nF.setStrCategoryName(strCategory);
			}
			nF.setStrDocumentName(strDocumentName);
			
			for(String strEmp : alEmp) {
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
			for(String strEmp : alSharePoc) {
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
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
			pst = con.prepareStatement("select pro_document_id,doc_parent_id,pro_folder_id from project_document_details where pro_document_id=?");
			pst.setInt(1, uF.parseToInt(getStrId()));
//			System.out.println("pst ==>> " + pst);
			rs = pst.executeQuery();
			if(rs.next()) {
				flag = true;
				nProParentDocId = uF.parseToInt(rs.getString("doc_parent_id"));
				nProParentFolderId = uF.parseToInt(rs.getString("pro_folder_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("nProParentFolderId ===>> " + nProParentFolderId);
			if (flag) {
				int nVersion = 0;
				if(nProParentDocId == 0) {
					nProParentDocId = uF.parseToInt(getStrId());
				}
				
				pst = con.prepareStatement("select count(pro_document_id) as cnt from project_document_details where pro_document_id =? and pro_document_id > 0");
				pst.setInt(1, uF.parseToInt(getStrId()));
				rs = pst.executeQuery();
				if(rs.next()) {
					nVersion = uF.parseToInt(rs.getString("cnt"));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select count(pro_document_id) as cnt from project_document_details where doc_parent_id in ("+nProParentDocId+") and doc_parent_id > 0");
				rs = pst.executeQuery();
				if(rs.next()) {
					nVersion += uF.parseToInt(rs.getString("cnt"));
				}
				rs.close();
				pst.close();
				
				nVersion++;
				
				String mainPathWithOrg = CF.getProjectDocumentFolder()+strOrgId;
				String proNameFolder = mainPathWithOrg;
//				String mainPath = mainPathWithOrg+"/Projects";
				
//				String proNameFolder = mainPath +"/"+getProId();
				int nDocPosition = 1;
				String strFolderName = null;
				
				if(nProParentFolderId > 0) {
					pst = con.prepareStatement("select pro_document_id,pro_folder_id,folder_name,pro_id from project_document_details where pro_document_id=?");
					pst.setInt(1, nProParentFolderId);
					rs = pst.executeQuery();
					int nProFId = 0;
					String strProFolder = "0";
					int strProId = 0;
					if(rs.next()) {
						flag = true;
						nProFId = uF.parseToInt(rs.getString("pro_folder_id"));
						strProFolder = rs.getString("folder_name");
						strProId = rs.getInt("pro_id");
					}
					rs.close();
					pst.close();
					
					if(nProFId > 0) {
						pst = con.prepareStatement("select pro_document_id,pro_folder_id,folder_name,pro_id from project_document_details where pro_document_id=?");
						pst.setInt(1, nProFId);
						rs = pst.executeQuery();
						String strProFolder1 = "0";
						int strProId1 = 0;
						if(rs.next()) {
							flag = true;
							strProFolder1 = rs.getString("folder_name");
							strProId1 = rs.getInt("pro_id");
						}
						rs.close();
						pst.close();
						
						if(strProId1 > 0) {
							proNameFolder = proNameFolder+"/Projects/"+strProId +"/"+strProFolder1+"/"+strProFolder;
						} else {
							proNameFolder = proNameFolder +"/Categories/"+ strProFolder1+"/"+strProFolder;
						}
//						proNameFolder += "/"+strProFolder1+"/"+strProFolder;
						
					} else {
						if(strProId > 0) {
							proNameFolder = proNameFolder+"/Projects/"+strProId +"/"+strProFolder;
						} else {
							proNameFolder = proNameFolder +"/Categories/"+strProFolder;
						}
//						proNameFolder += "/"+strProFolder;
					}
					strFolderName = strProFolder;
					nDocPosition = 2;
				}
				
//				System.out.println("nDocPosition ===>> " + nDocPosition);
				
				if(getStrFolderDoc() != null) {
					double lengthBytes =  getStrFolderDoc().length();
					
					String extenstion=FilenameUtils.getExtension(getStrFolderDocFileName());	
					String strFileName = FilenameUtils.getBaseName(getStrFolderDocFileName());
					strFileName = strFileName+"v"+nVersion+"."+extenstion;
					
					boolean isFileExist = false;
					File f = new File(proNameFolder+"/"+strFileName);
					if(f.isFile()) {
	//				    System.out.println("isFile");
					    if(f.exists()) {
							isFileExist = true;
	//					    System.out.println("exists");
						} else {
	//					    System.out.println("exists fail");
						}   
					} else {
	//				    System.out.println("isFile fail");
					}
				
					if(lengthBytes > 0 && !isFileExist) {
						List<String> alEmployee = null;
						if(getStrOrgResources() != null) {
							alEmployee = Arrays.asList(getStrOrgResources());
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
						
						List<String> alPoc = null;
						if(getStrOrgPocFolderDoc() != null) {
							alPoc = Arrays.asList(getStrOrgPocFolderDoc());
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
						
						if(nDocPosition == 1) {
							
							pst = con.prepareStatement("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
							"visibility,visibility_with_id,created_by,create_time,doc_or_image) values(?,?,?,?, ?,?,?,?, ?,?,?)");
							pst.setString(1, getStrFolderDescription());
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								pst.setInt(2, 1);
							} else {
								pst.setInt(2, 0);
							}
//							pst.setInt(2, uF.parseToInt(getStrAlignWith()));
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								pst.setInt(3, uF.parseToInt(getStrOrgProject()));
							} else {
								pst.setInt(3, uF.parseToInt(getStrOrgCategory()));
							}
//							pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
							pst.setString(4, sbEmps.toString());
							pst.setString(5, "");
							pst.setString(6, "");
							pst.setInt(7, uF.parseToInt(getFolderSharingType()));
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
								"is_edit,is_delete,is_cust_add,sharing_poc,doc_version,feed_id) " +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
							pst.setInt(1, 0);
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								pst.setInt(2, uF.parseToInt(getStrOrgProject()));
							} else {
								pst.setInt(2, 0);
							}
							pst.setInt(3, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//							pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setString(5, "file");
							pst.setInt(6, nProParentFolderId);
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								pst.setInt(7, 0);
							} else {
								pst.setInt(7, uF.parseToInt(getStrOrgCategory()));
							}
							pst.setInt(8, uF.parseToInt(getFolderSharingType()));
							pst.setString(9, sbEmps.toString());
							pst.setInt(10, uF.parseToInt(getProCategoryTypeFolder()));
							pst.setString(11, getStrFolderScopeDoc());
							pst.setString(12, getStrFolderDescription());
							pst.setInt(13, nProParentDocId);
							pst.setBoolean(14, uF.parseToBoolean(getIsFolderDocEdit()));
							pst.setBoolean(15, uF.parseToBoolean(getIsFolderDocDelete()));
							if(strUserType != null && strUserType.equals(CUSTOMER)) {
								pst.setBoolean(16, true);
							} else {
								pst.setBoolean(16, false);
							}
							pst.setString(17, sbPoc.toString());
							pst.setInt(18, nVersion);
							pst.setInt(19, uF.parseToInt(feedId));
//							System.out.println("pst====>"+pst);
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
							
							if(uF.parseToInt(proDocumentId) > 0) {
								uploadProjectDocuments(con, getStrFolderDoc(), strFileName, proNameFolder, proDocumentId, feedId);
								
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
								
								String proName1 = null;
								String strCategory = null;
								if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
									pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
									pst.setInt(1, uF.parseToInt(getStrOrgProject()));
									rs = pst.executeQuery();
									
									while(rs.next()) {
										proName1 = rs.getString("pro_name");
									}
									rs.close();
									pst.close();
								} else {
									pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
									pst.setInt(1, uF.parseToInt(getStrOrgCategory()));
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
								nF.setStrOrgId((String)session1.getAttribute(ORGID));
								nF.setEmailTemplate(true);
								
								if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
									nF.setStrProjectName(proName1);
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
//									userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
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
							pst.setString(1, getStrFolderDescription());
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								pst.setInt(2, 1);
							} else {
								pst.setInt(2, 0);
							}
//							pst.setInt(2, uF.parseToInt(getStrAlignWith()));
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								pst.setInt(3, uF.parseToInt(getStrOrgProject()));
							} else {
								pst.setInt(3, uF.parseToInt(getStrOrgCategory()));
							}
//							pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
							pst.setString(4, sbEmps.toString());
							pst.setString(5, "");
							pst.setString(6, "");
							pst.setInt(7, uF.parseToInt(getFolderSharingType()));
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
									"description,doc_parent_id,is_edit,is_delete,is_cust_add,sharing_poc,doc_version,feed_id)" +
									"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
							pst.setInt(1, 0);
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								pst.setInt(2, uF.parseToInt(getStrOrgProject()));
							} else {
								pst.setInt(2, 0);
							}
							pst.setString(3, strFolderName);
							pst.setInt(4, uF.parseToInt(strSessionEmpId));
							pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//							pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setString(6, "folder");
							pst.setInt(7, nProParentFolderId);
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								pst.setInt(8, 0);
							} else {
								pst.setInt(8, uF.parseToInt(getStrOrgCategory()));
							}
							pst.setInt(9, uF.parseToInt(getFolderSharingType()));
							pst.setString(10, sbEmps.toString());
							pst.setInt(11, uF.parseToInt(getProCategoryTypeFolder()));
							pst.setString(12, getStrFolderScopeDoc());
							pst.setString(13, getStrFolderDescription());
							pst.setInt(14, nProParentDocId);
							pst.setBoolean(15, uF.parseToBoolean(getIsFolderDocEdit()));
							pst.setBoolean(16, uF.parseToBoolean(getIsFolderDocDelete()));
							if(strUserType != null && strUserType.equals(CUSTOMER)) {
								pst.setBoolean(17, true);
							} else {
								pst.setBoolean(17, false);
							}
							pst.setString(18, sbPoc.toString());
							pst.setInt(19, nVersion);
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
							
							uploadProjectFolderDocuments(con, getStrFolderDoc(), strFileName, proNameFolder, proDocumentId, feedId);
							

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
							
							String proName1 = null;
							String strCategory = null;
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
								pst.setInt(1, uF.parseToInt(getStrOrgProject()));
								rs = pst.executeQuery();
								
								while(rs.next()) {
									proName1 = rs.getString("pro_name");
								}
								rs.close();
								pst.close();
							} else {
								pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
								pst.setInt(1, uF.parseToInt(getStrOrgCategory()));
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
							nF.setStrOrgId((String)session1.getAttribute(ORGID));
							nF.setEmailTemplate(true);
							
							if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
								nF.setStrProjectName(proName1);
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
//								userAlerts.set_type(SHARE_DOCUMENTS_ALERT);
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
					
					Map<String, String> hmProDocumentDetails = (Map<String, String>) request.getAttribute("hmProDocumentDetails");
					String proName = null;
					if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 1) {
						proName = CF.getProjectNameById(con, hmProDocumentDetails.get("PRO_ID"));
					} else if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 2) {
						proName = CF.getProjectCategory(con, uF, hmProDocumentDetails.get("ALIGN_WITH"));
					}
//					String proName = CF.getProjectNameById(con, getProId());
					session1.setAttribute(MESSAGE, SUCCESSM+" Document updated and create new version for "+proName+" successfully."+END);
					
				} else {
					List<String> alEmployee = null;
					if(getStrOrgResources() != null) {
						alEmployee = Arrays.asList(getStrOrgResources());
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
					
					List<String> alPoc = null;
					if(getStrOrgPocFolderDoc() != null) {
						alPoc = Arrays.asList(getStrOrgPocFolderDoc());
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
					
					pst = con.prepareStatement("update project_document_details set pro_id=?, added_by=?, entry_date=?, align_with=?, sharing_type=?, " +
						"sharing_resources=?, project_category=?, scope_document=?, description=?, is_edit=?, is_delete=?,is_cust_add=?,sharing_poc=? where pro_document_id=?");
					if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
						pst.setInt(1, uF.parseToInt(getStrOrgProject()));
					} else {
						pst.setInt(1, 0);
					}
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(3, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
						pst.setInt(4, 0);
					} else {
						pst.setInt(4, uF.parseToInt(getStrOrgCategory()));
					}
					pst.setInt(5, uF.parseToInt(getFolderSharingType()));
					pst.setString(6, sbEmps.toString());
					pst.setInt(7, uF.parseToInt(getProCategoryTypeFolder()));
					pst.setString(8, getStrFolderScopeDoc());
					pst.setString(9, getStrFolderDescription());
					pst.setBoolean(10, uF.parseToBoolean(getIsFolderDocEdit()));
					pst.setBoolean(11, uF.parseToBoolean(getIsFolderDocDelete()));
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						pst.setBoolean(12, true);
					} else {
						pst.setBoolean(12, false);
					}
					pst.setString(13, sbPoc.toString());
					pst.setInt(14, uF.parseToInt(getStrId()));
	//				System.out.println("pst====>"+pst);
					pst.execute();
					pst.close();
					
					
					String docFeedId = "";
					pst = con.prepareStatement("select feed_id from project_document_details where pro_document_id=?");
					pst.setInt(1, uF.parseToInt(getStrId()));
					rs = pst.executeQuery();
					while (rs.next()) {
						docFeedId = rs.getString("feed_id");
					}
					rs.close();
					pst.close();
					
					
					pst = con.prepareStatement("update communication_1 set communication=?,align_with=?,align_with_id=?,tagged_with=?," +
					"visibility=?,visibility_with_id=?,update_time=? where communication_id=?");
					pst.setString(1, getStrFolderDescription());
					if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
						pst.setInt(2, 1);
					} else {
						pst.setInt(2, 0);
					}
//					pst.setInt(2, uF.parseToInt(getStrAlignWith()));
					if(uF.parseToInt(getProCategoryTypeFolder()) == 1) {
						pst.setInt(3, uF.parseToInt(getStrOrgProject()));
					} else {
						pst.setInt(3, uF.parseToInt(getStrOrgCategory()));
					}
//					pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
					pst.setString(4, sbEmps.toString());
					pst.setInt(5, uF.parseToInt(getFolderSharingType()));
					pst.setString(6, sbEmps.toString());
					pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setInt(8, uF.parseToInt(docFeedId));
					pst.executeUpdate();
					pst.close();
					
					
					
					/**
					 * Alerts
					 * */
					
					String strDocumentName = "";
					pst = con.prepareStatement("select folder_name from project_document_details where pro_document_id=?");
					pst.setInt(1, uF.parseToInt(getStrId()));
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
						pst.setInt(1, uF.parseToInt(getStrOrgProject()));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							proName1 = rs.getString("pro_name");
						}
						rs.close();
						pst.close();
					} else {
						pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
						pst.setInt(1, uF.parseToInt(getStrOrgCategory()));
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
					nF.setStrOrgId((String)session1.getAttribute(ORGID));
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
					
					
					
					
					Map<String, String> hmProDocumentDetails = (Map<String, String>) request.getAttribute("hmProDocumentDetails");
					String proName = null;
					if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 1) {
						proName = CF.getProjectNameById(con, hmProDocumentDetails.get("PRO_ID"));
					} else if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 2) {
						proName = CF.getProjectCategory(con, uF, hmProDocumentDetails.get("ALIGN_WITH"));
					}
					session1.setAttribute(MESSAGE, SUCCESSM+" Document updated for "+proName+" successfully."+END);
					
				}
			} else {
				session1.setAttribute(MESSAGE, ERRORM+" Document updated failed."+END);
			}
			
		} catch(Exception e) {
			session1.setAttribute(MESSAGE, ERRORM+" Document updated failed."+END);
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
			MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
//			System.out.println("getOperation() ===>> " + getOperation());
			
//			String orgName = CF.getStrOrgName();
			String mainPathWithOrg = CF.getProjectDocumentFolder()+"/"+strOrgId;
			
//			String mainPath = mainPathWithOrg+"/Projects";
			String strDomain = request.getServerName().split("\\.")[0];
			String proNameFolder = mainPathWithOrg;
			String proFolderNameFolder = "";
			if(getType() != null && getType().equals("SF")) {
				pst = con.prepareStatement("select * from project_document_details where pro_document_id=(select pro_folder_id from project_document_details where pro_document_id=?)");
				pst.setInt(1, uF.parseToInt(getStrId()));
				rs = pst.executeQuery();
				String strMainFolder = "0";
				String strProId = null;
				while (rs.next()) {
					strMainFolder = rs.getString("folder_name");
					strProId = rs.getString("pro_id");
				}
				if(uF.parseToInt(strProId) > 0) {
					proFolderNameFolder = proNameFolder+"/Projects/"+strProId+"/"+ strMainFolder +"/"+ getFolderName();
				} else {
					proFolderNameFolder = proNameFolder +"/Categories/"+ strMainFolder +"/"+ getFolderName();
				}
			} else if(getType() != null && getType().equals("F")) {
				pst = con.prepareStatement("select * from project_document_details where pro_document_id=?");
				pst.setInt(1, uF.parseToInt(getStrId()));
				rs = pst.executeQuery();
				String strProId = null;
				while (rs.next()) {
					strProId = rs.getString("pro_id");
				}
				rs.close();
				pst.close();
				if(uF.parseToInt(strProId) > 0) {
					proFolderNameFolder = proNameFolder+"/Projects/"+strProId +"/"+ getFolderName();
				} else {
					proFolderNameFolder = proNameFolder +"/Categories/"+ getFolderName();
				}
			}
			
			
			if(getType() != null && getType().equals("F")) {
			String[] folderDocsTRId = request.getParameterValues("folderDocsTRId1");
			
			File[] strFolderDoc = mpRequest.getFiles("strFolderDoc1");
			String[] strFolderDocFileNames = mpRequest.getFileNames("strFolderDoc1");
			
			String[] strFolderScopeDoc = request.getParameterValues("strFolderScopeDoc1");
			String[] proCategoryTypeFolderDoc = request.getParameterValues("proCategoryTypeFolderDoc1");
			String[] folderDocSharingType = request.getParameterValues("folderDocSharingType1");
			String[] strFolderDocDescription = request.getParameterValues("strFolderDocDescription1");

			String[] isFolderDocEdit = request.getParameterValues("isFolderDocEdit1");
			String[] isFolderDocDelete = request.getParameterValues("isFolderDocDelete1");
			
			for(int j=0; folderDocsTRId != null && j<folderDocsTRId.length; j++) {
			
				String strOrgCategoryFolderDoc = request.getParameter("strOrgCategoryFolderDoc1_"+folderDocsTRId[j]);
				String strOrgProjectFolderDoc = request.getParameter("strOrgProjectFolderDoc1_"+folderDocsTRId[j]);
				
//				System.out.println("folderDocsTRId[j] ===>> " + folderDocsTRId[j]);
//				System.out.println("getFolderTRId()[i] ===>> " + getFolderTRId()[i]);
				double lengthBytes =  strFolderDoc[j].length();
				boolean isFileExist = false;
				
				String extenstion=FilenameUtils.getExtension(strFolderDocFileNames[j]);	
				String strFileName = FilenameUtils.getBaseName(strFolderDocFileNames[j]);
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
					String[] strOrgResourcesFolderDoc = request.getParameterValues("strOrgResourcesFolderDoc1_"+folderDocsTRId[j]);
					List<String> alFDEmployee = null;
					if(strOrgResourcesFolderDoc != null) {
						alFDEmployee = Arrays.asList(strOrgResourcesFolderDoc);
					}
					StringBuilder sbFDEmps = null;
					List<String> alEmp = new ArrayList<String>();
					for(int a=0; alFDEmployee != null && a<alFDEmployee.size(); a++) {
						if(alFDEmployee.get(a) != null && !alFDEmployee.get(a).trim().equals("")) {
							if(sbFDEmps == null) {
								sbFDEmps = new StringBuilder();
								sbFDEmps.append(","+ alFDEmployee.get(a).trim() +",");
							} else {
								sbFDEmps.append(alFDEmployee.get(a).trim() +",");
							}
							if(!alEmp.contains(alFDEmployee.get(a).trim())) {
								alEmp.add(alFDEmployee.get(a).trim());
							}
						}
					}
					if(sbFDEmps == null) {
						sbFDEmps = new StringBuilder();
					}
					
					String[] strOrgPocFolderDoc = request.getParameterValues("strOrgPocFolderDoc1_"+folderDocsTRId[j]);
					List<String> alFDPoc = null;
					if(strOrgPocFolderDoc != null) {
						alFDPoc = Arrays.asList(strOrgPocFolderDoc);
					}
					StringBuilder sbFDPoc = null;
					List<String> alSharePoc = new ArrayList<String>();
					for(int a=0; alFDPoc != null && a<alFDPoc.size(); a++) {
						if(alFDPoc.get(a) != null && !alFDPoc.get(a).trim().equals("")) {
							if(sbFDPoc == null) {
								sbFDPoc = new StringBuilder();
								sbFDPoc.append(","+ alFDPoc.get(a).trim() +",");
							} else {
								sbFDPoc.append(alFDPoc.get(a).trim() +",");
							}
							if(!alSharePoc.contains(alFDPoc.get(a).trim())) {
								alSharePoc.add(alFDPoc.get(a).trim());
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
//					pst.setInt(2, uF.parseToInt(getStrAlignWith()));
					if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
						pst.setInt(3, uF.parseToInt(strOrgProjectFolderDoc));
					} else {
						pst.setInt(3, uF.parseToInt(strOrgCategoryFolderDoc));
					}
//					pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
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
							"description,doc_parent_id,is_edit,is_delete,is_cust_add,sharing_poc,doc_version,feed_id)" +
							"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst.setInt(1, 0);
					if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
						pst.setInt(2, uF.parseToInt(strOrgProjectFolderDoc));
					} else {
						pst.setInt(2, 0);
					}
					pst.setString(3, getFolderName());
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(6, "folder");
					pst.setInt(7, uF.parseToInt(getStrId()));
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
					
					String proName1 = null;
					String strCategory = null;
					if(uF.parseToInt(proCategoryTypeFolderDoc[j]) == 1) {
						pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
						pst.setInt(1, uF.parseToInt(strOrgProjectFolderDoc));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							proName1 = rs.getString("pro_name");
						}
						rs.close();
						pst.close();
					} else {
						pst = con.prepareStatement("select * from project_category_details where project_category_id=? ");
						pst.setInt(1, uF.parseToInt(strOrgCategoryFolderDoc));
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
					nF.setStrOrgId((String)session1.getAttribute(ORGID));
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
			
			
			String[] subFolderTRId = request.getParameterValues("subFolderTRId1");
			String[] strSubFolderName = request.getParameterValues("strSubFolderName1");
			String[] proCategoryTypeSubFolder = request.getParameterValues("proCategoryTypeSubFolder1");
			String[] SubFolderSharingType = request.getParameterValues("SubFolderSharingType1");
			String[] strSubFolderDescription = request.getParameterValues("strSubFolderDescription1");
			
			String[] isSubFolderEdit = request.getParameterValues("isSubFolderEdit1");
			String[] isSubFolderDelete = request.getParameterValues("isSubFolderDelete1");
			
			for(int j = 0; subFolderTRId != null && j < subFolderTRId.length; j++) {
				
				String strOrgCategorySubFolder = request.getParameter("strOrgCategorySubFolder1_"+subFolderTRId[j]);
				String strOrgProjectSubFolder = request.getParameter("strOrgProjectSubFolder1_"+subFolderTRId[j]);
				
				String[] strOrgResourcesSubFolder = request.getParameterValues("strOrgResourcesSubFolder1_"+subFolderTRId[j]);
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
						if(!alSubEmp.contains(alSubEmployee.get(a))){
							alSubEmp.add(alSubEmployee.get(a));
						}
					}
				}
				if(sbSubEmps == null) {
					sbSubEmps = new StringBuilder();
				}
				
				String[] strOrgPocSubFolder = request.getParameterValues("strOrgPocSubFolder1_"+subFolderTRId[j]);
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
						if(!alSubSharePoc.contains(alSubPoc.get(a))){
							alSubSharePoc.add(alSubPoc.get(a));
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
						"description,doc_parent_id,is_edit,is_delete,is_cust_add,sharing_poc)" +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
				pst.setInt(1, 0);
				if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
					pst.setInt(2, uF.parseToInt(strOrgProjectSubFolder));
				} else {
					pst.setInt(2, 0);
				}
				pst.setString(3, strSubFolderName[j]);
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(6, "folder");
				pst.setInt(7, uF.parseToInt(getStrId()));
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
				nF.setStrOrgId((String)session1.getAttribute(ORGID));
				nF.setEmailTemplate(true);
				
				if(uF.parseToInt(proCategoryTypeSubFolder[j]) == 1) {
					nF.setStrProjectName(proName1);
				} else {
					nF.setStrCategoryName(strCategory);
				}
				nF.setStrDocumentName(strDocumentName);
				
				String alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
				String alertAction = "DocumentListView.action";
				for(String strEmp : alSubEmp){
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
				for(String strEmp : alSubSharePoc) {
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
				
//				System.out.println("SubFolderTR[j] ====>> " + SubFolderTR[j]);
				String[] subFolderDocsTRId = request.getParameterValues("subFolderDocsTRId"+subFolderTRId[j]);
				
				File[] strSubFolderDoc = mpRequest.getFiles("strSubFolderDoc"+subFolderTRId[j]);    //  
				String[] strSubFolderDocFileNames = mpRequest.getFileNames("strSubFolderDoc"+subFolderTRId[j]);
				String[] strSubFolderScopeDoc = request.getParameterValues("strSubFolderScopeDoc" + subFolderTRId[j]);
				
				String[] proCategoryTypeSubFolderDoc = request.getParameterValues("proCategoryTypeSubFolderDoc" +subFolderTRId[j]);
				String[] SubFolderDocSharingType = request.getParameterValues("SubFolderDocSharingType" + subFolderTRId[j]);
				String[] strSubFolderDocDescription = request.getParameterValues("strSubFolderDocDescription" +subFolderTRId[j]);
				
				String[] isSubFolderDocEdit = request.getParameterValues("isSubFolderDocEdit"+subFolderTRId[j]);
				String[] isSubFolderDocDelete = request.getParameterValues("isSubFolderDocDelete"+subFolderTRId[j]);
				
				for(int k=0; subFolderDocsTRId != null && k < subFolderDocsTRId.length; k++) {
					String strOrgCategorySubFolderDoc = request.getParameter("strOrgCategorySubFolderDoc" + subFolderTRId[j]+"_"+subFolderDocsTRId[k]);
					String strOrgProjectSubFolderDoc = request.getParameter("strOrgProjectSubFolderDoc" + subFolderTRId[j]+"_"+subFolderDocsTRId[k]);
					
					double lengthBytes =  strSubFolderDoc[k].length();
					boolean isFileExist = false;
//					System.out.println("fileNames[k] ===>> " + fileNames[k] + " ----- " + proSubFolderNameFolder+"/"+fileNames[k]);
					
					String extenstion=FilenameUtils.getExtension(strSubFolderDocFileNames[k]);	
					String strFileName = FilenameUtils.getBaseName(strSubFolderDocFileNames[k]);
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
						String[] proFolderDocEmployee = request.getParameterValues("strOrgResourcesSubFolderDoc" + subFolderTRId[j]+"_"+subFolderDocsTRId[k]);
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
						
						String[] proFolderDocPoc = request.getParameterValues("strOrgPocSubFolderDoc" + subFolderTRId[j]+"_"+subFolderDocsTRId[k]);
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
							pst.setInt(3, uF.parseToInt(strOrgProjectSubFolderDoc));
						} else {
							pst.setInt(3, uF.parseToInt(strOrgCategorySubFolderDoc));
						}
//						pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
						pst.setString(4, sbFDEmps.toString());
						pst.setString(5, "");
						pst.setString(6, "");
						pst.setInt(7, uF.parseToInt(SubFolderDocSharingType[k]));
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
								"description,doc_parent_id,is_edit,is_delete,is_cust_add,sharing_poc,doc_version,feed_id)" +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst.setInt(1, 0);
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							pst.setInt(2, uF.parseToInt(strOrgProjectSubFolderDoc));
						} else {
							pst.setInt(2, 0);
						}
						pst.setString(3, strSubFolderName[j]);
						pst.setInt(4, uF.parseToInt(strSessionEmpId));
						pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//						pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
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
	//					System.out.println("pst ====> " + pst);
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
						List<String> alFDEmp= null;
						if(proFolderDocEmployee != null) {
							alFDEmp = Arrays.asList(proFolderDocEmployee);
						}
						if(alFDEmp == null){
							alFDEmp = new ArrayList<String>();
						}
						List<String> alFDSharePoc = null;
						if(proFolderDocPoc != null) {
							alFDSharePoc = Arrays.asList(proFolderDocPoc);
						}
						if(alFDSharePoc == null){
							alFDSharePoc = new ArrayList<String>();
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
							pst.setInt(1, uF.parseToInt(strOrgProjectSubFolderDoc));
							rs = pst.executeQuery();
							while(rs.next()) {
								proName1 = rs.getString("pro_name");
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
						nF.setStrOrgId((String)session1.getAttribute(ORGID));
						nF.setEmailTemplate(true);
						
						if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
							nF.setStrProjectName(proName1);
						} else {
							nF.setStrCategoryName(strCategory);
						}
						nF.setStrDocumentName(strDocumentName);
						
						alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						alertAction = "DocumentListView.action";
						for(String strEmp : alFDEmp) {
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
						for(String strEmp : alFDSharePoc) {
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
			}
			
			Map<String, String> hmProDocumentDetails = (Map<String, String>) request.getAttribute("hmProDocumentDetails");
			String proName = null;
			if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 1) {
				proName = CF.getProjectNameById(con, hmProDocumentDetails.get("PRO_ID"));
			} else if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 2) {
				proName = CF.getProjectCategory(con, uF, hmProDocumentDetails.get("ALIGN_WITH"));
			}
			int subFolderCnt = subFolderTRId!=null ? subFolderTRId.length : 0;
			 
			StringBuilder sbMsg = new StringBuilder();
			
			sbMsg.append(SUCCESSM+""+getFolderName()+" folder updated ");
			if(subFolderCnt>0) {
				sbMsg.append("and some documents added ");
			}
			sbMsg.append("successfully for "+proName+"."+END);
			session1.setAttribute(MESSAGE, sbMsg.toString());
			
			} else if(getType() != null && getType().equals("SF")) {
			
//			System.out.println("SubFolderTR[j] ====>> " + SubFolderTR[j]);
			String[] subFolderDocsTRId = request.getParameterValues("subFolderDocsTRId1");
			
			File[] strSubFolderDoc = mpRequest.getFiles("strSubFolderDoc1");
			String[] strSubFolderDocFileNames = mpRequest.getFileNames("strSubFolderDoc1");
			String[] strSubFolderScopeDoc = request.getParameterValues("strSubFolderScopeDoc1");
			
			String[] proCategoryTypeSubFolderDoc = request.getParameterValues("proCategoryTypeSubFolderDoc1" );
			String[] SubFolderDocSharingType = request.getParameterValues("SubFolderDocSharingType1");
			String[] strSubFolderDocDescription = request.getParameterValues("strSubFolderDocDescription1");
			
			String[] isSubFolderDocEdit = request.getParameterValues("isSubFolderDocEdit1");
			String[] isSubFolderDocDelete = request.getParameterValues("isSubFolderDocDelete1");
			
			for(int k=0; subFolderDocsTRId != null && k < subFolderDocsTRId.length; k++) {
				String strOrgCategorySubFolderDoc = request.getParameter("strOrgCategorySubFolderDoc1_"+subFolderDocsTRId[k]);
				String strOrgProjectSubFolderDoc = request.getParameter("strOrgProjectSubFolderDoc1_"+subFolderDocsTRId[k]);
				
				double lengthBytes =  strSubFolderDoc[k].length();
				boolean isFileExist = false;
//				System.out.println("fileNames[k] ===>> " + fileNames[k] + " ----- " + proSubFolderNameFolder+"/"+fileNames[k]);
				String extenstion=FilenameUtils.getExtension(strSubFolderDocFileNames[k]);	
				String strFileName = FilenameUtils.getBaseName(strSubFolderDocFileNames[k]);
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
					String[] proFolderDocEmployee = request.getParameterValues("strOrgResourcesSubFolderDoc1_"+subFolderDocsTRId[k]);
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
					
					String[] proFolderDocPoc = request.getParameterValues("strOrgPocSubFolderDoc1_"+subFolderDocsTRId[k]);
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
//					pst.setInt(2, uF.parseToInt(getStrAlignWith()));
					if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
						pst.setInt(3, uF.parseToInt(strOrgProjectSubFolderDoc));
					} else {
						pst.setInt(3, uF.parseToInt(strOrgCategorySubFolderDoc));
					}
//					pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
					pst.setString(4, sbFDEmps.toString());
					pst.setString(5, "");
					pst.setString(6, "");
					pst.setInt(7, uF.parseToInt(SubFolderDocSharingType[k]));
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
							"description,doc_parent_id,is_edit,is_delete,is_cust_add,sharing_poc,doc_version,feed_id)" +
							"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst.setInt(1, 0);
					if(uF.parseToInt(proCategoryTypeSubFolderDoc[k])== 1) {
						pst.setInt(2, uF.parseToInt(strOrgProjectSubFolderDoc));
					} else {
						pst.setInt(2, 0);
					}
					pst.setString(3, getFolderName());
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(6, "folder");
					pst.setInt(7, uF.parseToInt(getStrId()));
					if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
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
//					System.out.println("pst ====> " + pst);
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
					
					uploadProjectFolderDocuments(con, strSubFolderDoc[k], strFileName, proFolderNameFolder, proDocumentId, feedId);
					
					/**
					 * Alerts
					 * */
					List<String> alFDEmp= null;
					if(proFolderDocEmployee != null) {
						alFDEmp = Arrays.asList(proFolderDocEmployee);
					}
					if(alFDEmp == null){
						alFDEmp = new ArrayList<String>();
					}
					List<String> alFDSharePoc = null;
					if(proFolderDocPoc != null) {
						alFDSharePoc = Arrays.asList(proFolderDocPoc);
					}
					if(alFDSharePoc == null){
						alFDSharePoc = new ArrayList<String>();
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
						pst.setInt(1, uF.parseToInt(strOrgProjectSubFolderDoc));
						rs = pst.executeQuery();
						while(rs.next()) {
							proName1 = rs.getString("pro_name");
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
					Notifications nF = new Notifications(N_NEW_DOCUMENT_SHARED, CF); 
					nF.setDomain(strDomain);
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					
					nF.request = request;
					nF.setStrOrgId((String)session1.getAttribute(ORGID));
					nF.setEmailTemplate(true);
					
					if(uF.parseToInt(proCategoryTypeSubFolderDoc[k]) == 1) {
						nF.setStrProjectName(proName1);
					} else {
						nF.setStrCategoryName(strCategory);
					}
					nF.setStrDocumentName(strDocumentName);
					
					String alertData = "<div style=\"float: left;\"> <b>"+strDocumentName+"</b> document has been shared with you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					String alertAction = "DocumentListView.action";
					for(String strEmp : alFDEmp){
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
					for(String strEmp : alFDSharePoc){
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
			
			
			Map<String, String> hmProDocumentDetails = (Map<String, String>) request.getAttribute("hmProDocumentDetails");
			String proName = null;
			if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 1) {
				proName = CF.getProjectNameById(con, hmProDocumentDetails.get("PRO_ID"));
			} else if(uF.parseToInt(hmProDocumentDetails.get("CATEGORY")) == 2) {
				proName = CF.getProjectCategory(con, uF, hmProDocumentDetails.get("ALIGN_WITH"));
			}
			int subFolderDocsCnt = subFolderDocsTRId!=null ? subFolderDocsTRId.length : 0; 
//			String proName = CF.getProjectNameById(con, getProId());
			StringBuilder sbMsg = new StringBuilder();
			sbMsg.append(SUCCESSM+""+getFolderName()+" folder updated ");
			if(subFolderDocsCnt>0) {
				sbMsg.append("and some documents added ");
			}
			sbMsg.append("successfully for "+proName+"."+END);
			session1.setAttribute(MESSAGE, sbMsg.toString());
			
			}
		
		} catch(Exception e) {
			session1.setAttribute(MESSAGE, ERRORM+" Document added failed."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void uploadProjectDocuments(Connection con, File contentFile, String contentFileName, String realFolderPath, String proDocumentId, String feedId) {

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
	
	
	private void uploadProjectFolderDocuments(Connection con, File contentFile, String contentFileName, String realFolderPath, String proDocumentId, String feedId) {

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
			upd.setProDocumentId(proDocumentId);
			upd.setFeedId(feedId);
			upd.setCF(CF);
//			System.out.println("empId2 ===> "+empId2+" getEmpImage() ===> "+getEmpImage());
			upd.uploadProjectDocuments();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
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

	public String getFolderSharingType() {
		return folderSharingType;
	}

	public void setFolderSharingType(String folderSharingType) {
		this.folderSharingType = folderSharingType;
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

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
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

	public List<FillEmployee> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<FillEmployee> resourceList) {
		this.resourceList = resourceList;
	}

	public String getStrOrgCategory() {
		return strOrgCategory;
	}

	public void setStrOrgCategory(String strOrgCategory) {
		this.strOrgCategory = strOrgCategory;
	}

	public String getStrOrgProject() {
		return strOrgProject;
	}

	public void setStrOrgProject(String strOrgProject) {
		this.strOrgProject = strOrgProject;
	}

	public String[] getStrOrgResources() {
		return strOrgResources;
	}

	public void setStrOrgResources(String[] strOrgResources) {
		this.strOrgResources = strOrgResources;
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

	public String getStrFolderScopeDoc() {
		return strFolderScopeDoc;
	}

	public void setStrFolderScopeDoc(String strFolderScopeDoc) {
		this.strFolderScopeDoc = strFolderScopeDoc;
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

	public String[] getStrOrgPocFolderDoc() {
		return strOrgPocFolderDoc;
	}

	public void setStrOrgPocFolderDoc(String[] strOrgPocFolderDoc) {
		this.strOrgPocFolderDoc = strOrgPocFolderDoc;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}
