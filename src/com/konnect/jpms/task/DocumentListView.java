package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DocumentListView extends ActionSupport implements ServletRequestAware, ServletResponseAware,  IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	int proId;
	String strUserType;
	String strProductType =  null;
	String strSessionEmpId;
	
	String type;
	String strOrgId; 
	String btnSave;
	String proType;
	String fromPage;
	String taskId;
	
	List<FillEmployee> resourceList;
	String strSearchDoc;
	
	String alertStatus;
	String alert_type;
	String alertID;
	
	String docType;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		strOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strProductType = (String)session.getAttribute(PRODUCT_TYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		if(uF.parseToInt(strProductType) != 3) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		request.setAttribute(PAGE, "/jsp/task/DocumentListView.jsp");
		request.setAttribute(TITLE, "Documents");
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-folder-open\"></i><a href=\"DocumentListView.action\" style=\"color: #3c8dbc;\"> Document</a></li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
//		if(getAlertStatus()!=null && getAlert_type()!=null){
		if(uF.parseToInt(getAlertID()) > 0) {
			updateUserAlerts();
		}
		
		getData(uF);
		
		if(getStrSearchDoc() !=null && !getStrSearchDoc().trim().equals("") && !getStrSearchDoc().trim().equalsIgnoreCase("NULL")) {
			getSearchData(uF);
		} else {
			getDocumentDetails(uF);
		}
		return SUCCESS;
	}
	
	
	private void updateUserAlerts() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setAlertID(getAlertID()); 
			if(strUserType!=null && strUserType.equals(CUSTOMER)) {
				userAlerts.setStrOther("other");
			}
			userAlerts.setStatus(DELETE_TR_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	
	/*private void updateUserAlerts() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		try {
			con = db.makeConnection(con);
			String strType = null;
			if(getAlert_type().equals(SHARE_DOCUMENTS_ALERT)){
				strType = SHARE_DOCUMENTS_ALERT;
			}
			
			if(strType!=null && !strType.trim().equals("")){
				String strDomain = request.getServerName().split("\\.")[0];
				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(""+nEmpId);
				userAlerts.set_type(strType);
				if(strUserType!=null && strUserType.equals(CUSTOMER)){
					userAlerts.setStrOther("other");
				}
				userAlerts.setStatus(UPDATE_ALERT);
//				//Thread t = new Thread(userAlerts);
//				//t.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}*/
	
private void getSearchData(UtilityFunctions uF) {
//	System.out.println("in getSearchData====>");
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
	
			resourceList = new FillEmployee(request).fillEmployeeName(null, null, uF.parseToInt(strOrgId), 0, session);
			
			StringBuilder sbOrgResources= new StringBuilder();
			for(int i=0; resourceList!=null && i<resourceList.size(); i++) {
				sbOrgResources.append("<option value='"+resourceList.get(i).getEmployeeId()+"'>"+resourceList.get(i).getEmployeeName()+"</option>");
			}
			request.setAttribute("sbOrgResources", sbOrgResources.toString());
			
			
			StringBuilder sbOrgProjects = new StringBuilder();
			pst = con.prepareStatement("select * from projectmntnc where org_id=? order by pro_name");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbOrgProjects.append("<option value='"+rs.getString("pro_id")+"'>"+rs.getString("pro_name")+"</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbOrgProjects", sbOrgProjects.toString());
			
	
			StringBuilder sbOrgCategory = new StringBuilder();
			pst = con.prepareStatement("select * from project_category_details where org_id=? and project_category_id>1 order by project_category");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbOrgCategory.append("<option value='"+rs.getString("project_category_id")+"'>"+rs.getString("project_category")+"</option>");
			}
			rs.close();   
			pst.close();
			request.setAttribute("sbOrgCategory", sbOrgCategory.toString());
			
			StringBuilder sbOrgSPOC = new StringBuilder();
			pst = con.prepareStatement("select poc_id, contact_fname, contact_lname from client_poc where poc_id in (select poc from projectmntnc " +
					"where org_id=?) order by contact_fname,contact_lname");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbOrgSPOC.append("<option value='" + rs.getString("poc_id") + "'>" + uF.showData(rs.getString("contact_fname"), "").trim()+" "+uF.showData(rs.getString("contact_lname"), "").trim()+ "</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbOrgSPOC", sbOrgSPOC.toString());
			
			/*Map<String, List<List<String>>> hmFolderData = new HashMap<String, List<List<String>>>();
			Map<String, List<List<String>>> hmSubFolderData = new HashMap<String, List<List<String>>>();			
			Map<String, List<List<String>>> hmProFolderData = new HashMap<String, List<List<String>>>();*/
			Map<String, List<List<String>>> hmFolderData = new LinkedHashMap<String, List<List<String>>>();
			Map<String, List<List<String>>> hmSubFolderData = new LinkedHashMap<String, List<List<String>>>();			
			Map<String, List<List<String>>> hmProFolderData = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> allFolderList = new ArrayList<List<String>>();
			
			StringBuilder sbOrgCategoryId = null;
			StringBuilder sbOrgProjectId = null;
			StringBuilder sbProFoldetId = null;
			StringBuilder sbMainDocId = null;
			/**
			 * Category
			 * */
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from project_category_details where org_id=? and upper(project_category) like '%"+getStrSearchDoc().trim().toUpperCase()+"%' " +
					" and project_category_id in (select align_with from project_document_details where pro_folder_id=0 and (project_category=2 or project_category=0)");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbQuery.append(" ) order by project_category");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strOrgId));
//			System.out.println("1 pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(sbOrgCategoryId == null){
					sbOrgCategoryId = new StringBuilder();
					sbOrgCategoryId.append(rs.getString("project_category_id"));
				} else {
					sbOrgCategoryId.append(","+rs.getString("project_category_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbOrgCategoryId != null){
				sbQuery = new StringBuilder();
				sbQuery.append("select * from project_document_details where pro_folder_id=0 and (project_category=2 or project_category=0)" +
						"and (file_size is null or file_size = '') and align_with in ("+sbOrgCategoryId+") ");
				if(strUserType != null && strUserType.equals(EMPLOYEE)){
					sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
				} else if(strUserType != null && strUserType.equals(MANAGER)){
					sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
				} else if(strUserType != null && strUserType.equals(CUSTOMER)){
					sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
				}
				sbQuery.append(" order by pro_document_id");
				pst = con.prepareStatement(sbQuery.toString());
//					System.out.println("2 pst======>"+pst);
				rs = pst.executeQuery();
				StringBuilder sbMainCatFolderId = null;
				while (rs.next()) {
					List<List<String>> folderList = hmFolderData.get(rs.getString("align_with"));
					if(folderList == null) folderList = new ArrayList<List<String>>();
	
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("pro_document_id"));
					innerList.add(rs.getString("folder_name"));
					folderList.add(innerList);
					hmFolderData.put(rs.getString("align_with"), folderList);
					
					List<String> innerFolderList = new ArrayList<String>();
					innerFolderList.add(rs.getString("pro_document_id"));
					innerFolderList.add(rs.getString("folder_name"));
					allFolderList.add(innerFolderList);
					
					if(sbMainCatFolderId == null){
						sbMainCatFolderId = new StringBuilder();
						sbMainCatFolderId.append(rs.getString("pro_document_id"));
					} else {
						sbMainCatFolderId.append(","+rs.getString("pro_document_id"));
					}
					
					if(sbMainDocId == null){
						sbMainDocId = new StringBuilder();
						sbMainDocId.append(rs.getString("pro_document_id"));
					} else {
						sbMainDocId.append(","+rs.getString("pro_document_id"));
					}
					
				}
				rs.close();
				pst.close();
				
				if(sbMainCatFolderId !=null){
					sbQuery = new StringBuilder();
					sbQuery.append("select * from project_document_details where pro_folder_id in ("+sbMainCatFolderId.toString()+") and (file_size is null or file_size = '') ");
					if(strUserType != null && strUserType.equals(EMPLOYEE)){
						sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
					} else if(strUserType != null && strUserType.equals(MANAGER)){
						sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
					} else if(strUserType != null && strUserType.equals(CUSTOMER)){
						sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
					}
					sbQuery.append(" order by pro_document_id");
					pst = con.prepareStatement(sbQuery.toString());
//						System.out.println("3 pst======>"+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						List<List<String>> subFolderList = hmSubFolderData.get(rs.getString("pro_folder_id"));
						if(subFolderList == null) subFolderList = new ArrayList<List<String>>();
	
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("pro_document_id"));
						innerList.add(rs.getString("folder_name"));
						subFolderList.add(innerList);
						hmSubFolderData.put(rs.getString("pro_folder_id"), subFolderList);
						
						if(sbMainDocId == null){
							sbMainDocId = new StringBuilder();
							sbMainDocId.append(rs.getString("pro_document_id"));
						} else {
							sbMainDocId.append(","+rs.getString("pro_document_id"));
						}
					}
					rs.close();
					pst.close();
				}
			}
			
			/**
			 * Project
			 * */
			sbQuery = new StringBuilder();
			sbQuery.append("select * from projectmntnc where org_id = ? and upper(pro_name) like '%"+getStrSearchDoc().trim().toUpperCase()+"%'" +
					" and pro_id in (select pro_id from project_document_details where pro_folder_id = 0 and project_category = 1 ");
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
			sbQuery.append(") order by pro_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strOrgId));
//			System.out.println("4 pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(sbOrgProjectId == null){
					sbOrgProjectId = new StringBuilder();
					sbOrgProjectId.append(rs.getString("pro_id"));
				} else {
					sbOrgProjectId.append(","+rs.getString("pro_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbOrgProjectId != null){
				sbQuery = new StringBuilder();
				sbQuery.append("select * from project_document_details where pro_folder_id=0 and project_category=1 " +
						"and (file_size is null or file_size = '') and pro_id in ("+sbOrgProjectId+")");
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
//					System.out.println("5 pst======>"+pst);
				rs = pst.executeQuery();
				StringBuilder sbMainProFolderId = null;
				while (rs.next()) {
					List<List<String>> folderList = hmProFolderData.get(rs.getString("pro_id"));
					if(folderList == null) folderList = new ArrayList<List<String>>();
	
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("pro_document_id"));
					innerList.add(rs.getString("folder_name"));
					folderList.add(innerList);
					hmProFolderData.put(rs.getString("pro_id"), folderList);
					
					List<String> innerFolderList = new ArrayList<String>();
					innerFolderList.add(rs.getString("pro_document_id"));
					innerFolderList.add(rs.getString("folder_name"));
					allFolderList.add(innerFolderList);
					
					if(sbMainProFolderId == null){
						sbMainProFolderId = new StringBuilder();
						sbMainProFolderId.append(rs.getString("pro_document_id"));
					} else {
						sbMainProFolderId.append(","+rs.getString("pro_document_id"));
					}
					
					if(sbMainDocId == null){
						sbMainDocId = new StringBuilder();
						sbMainDocId.append(rs.getString("pro_document_id"));
					} else {
						sbMainDocId.append(","+rs.getString("pro_document_id"));
					}
				}
				rs.close();
				pst.close();
				
				if(sbMainProFolderId !=null){
					sbQuery = new StringBuilder();
					sbQuery.append("select * from project_document_details where pro_folder_id in("+sbMainProFolderId.toString()+") and (file_size is null or file_size = '') ");
					if(strUserType != null && strUserType.equals(EMPLOYEE)){
						sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
								"or (sharing_type = 1 and pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+")) ) ");
					} else if(strUserType != null && strUserType.equals(MANAGER)){
					//===start parvez date: 13-10-2022===	
						sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0 " +
								"or (sharing_type = 1 and (pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+") " +
								"or pro_id in (select pro_id from project_emp_details where emp_id="+uF.parseToInt(strSessionEmpId)+" and _isteamlead=true) " +
//								"or pro_id in (select pro_id from projectmntnc where project_owner="+uF.parseToInt(strSessionEmpId)+")))) ");
								"or pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')))) ");
					//===end parvez date: 13-10-2022===	
					} else if(strUserType != null && strUserType.equals(CUSTOMER)){
						sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
					}
					sbQuery.append(" order by pro_document_id");
					pst = con.prepareStatement(sbQuery.toString());
//						System.out.println("6 pst======>"+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						List<List<String>> subFolderList = hmSubFolderData.get(rs.getString("pro_folder_id"));
						if(subFolderList == null) subFolderList = new ArrayList<List<String>>();
	
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("pro_document_id"));
						innerList.add(rs.getString("folder_name"));
						subFolderList.add(innerList);
						hmSubFolderData.put(rs.getString("pro_folder_id"), subFolderList);
						
						if(sbMainDocId == null){
							sbMainDocId = new StringBuilder();
							sbMainDocId.append(rs.getString("pro_document_id"));
						} else {
							sbMainDocId.append(","+rs.getString("pro_document_id"));
						}
					}
					rs.close();
					pst.close();
				}
			}
			
			/**
			 * Folder
			 * */
			
			StringBuilder sbMainFolderId = null;
			StringBuilder sbDocId = null;
			sbQuery = new StringBuilder();
			sbQuery.append("select * from project_document_details where (file_size is null or file_size = '') and upper(folder_name)  like '%"+getStrSearchDoc().trim().toUpperCase()+"%' ");
			if(sbMainDocId !=null){
				sbQuery.append(" and pro_document_id not in("+sbMainDocId.toString()+")");
			}
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
//			System.out.println("7 pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(uF.parseToInt(rs.getString("pro_folder_id")) == 0){
					if(uF.parseToInt(rs.getString("project_category")) == 1){
						List<List<String>> proFolderList = hmProFolderData.get(rs.getString("pro_id"));
						if(proFolderList == null) proFolderList = new ArrayList<List<String>>();
	
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("pro_document_id"));
						innerList.add(rs.getString("folder_name"));
						proFolderList.add(innerList);
						hmProFolderData.put(rs.getString("pro_id"), proFolderList);
						
						List<String> innerAllFoderList = new ArrayList<String>();
						innerAllFoderList.add(rs.getString("pro_document_id"));
						innerAllFoderList.add(rs.getString("folder_name"));
						allFolderList.add(innerAllFoderList);
						
						if(sbOrgProjectId == null){
							sbOrgProjectId = new StringBuilder();
							sbOrgProjectId.append(rs.getString("pro_id"));
						} else {
							sbOrgProjectId.append(","+rs.getString("pro_id"));
						}
						
						if(sbMainFolderId == null){
							sbMainFolderId = new StringBuilder();
							sbMainFolderId.append(rs.getString("pro_document_id"));
						} else {
							sbMainFolderId.append(","+rs.getString("pro_document_id"));
						}
						
					} else if(uF.parseToInt(rs.getString("project_category")) == 2){
						List<List<String>> folderList = hmFolderData.get(rs.getString("align_with"));
						if(folderList == null) folderList = new ArrayList<List<String>>();
	
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("pro_document_id"));
						innerList.add(rs.getString("folder_name"));
						folderList.add(innerList);
						hmFolderData.put(rs.getString("align_with"), folderList);
						
						List<String> innerAllFoderList = new ArrayList<String>();
						innerAllFoderList.add(rs.getString("pro_document_id"));
						innerAllFoderList.add(rs.getString("folder_name"));
						allFolderList.add(innerAllFoderList);
						
						if(sbOrgCategoryId == null){
							sbOrgCategoryId = new StringBuilder();
							sbOrgCategoryId.append(rs.getString("align_with"));
						} else {
							sbOrgCategoryId.append(","+rs.getString("align_with"));
						}
						
						if(sbMainFolderId == null){
							sbMainFolderId = new StringBuilder();
							sbMainFolderId.append(rs.getString("pro_document_id"));
						} else {
							sbMainFolderId.append(","+rs.getString("pro_document_id"));
						}						
					}
				} else {
					List<List<String>> subFolderList = hmSubFolderData.get(rs.getString("pro_folder_id"));
					if(subFolderList == null) subFolderList = new ArrayList<List<String>>();
	
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("pro_document_id"));
					innerList.add(rs.getString("folder_name"));
					subFolderList.add(innerList);
					hmSubFolderData.put(rs.getString("pro_folder_id"), subFolderList);
					
					if(sbProFoldetId == null){
						sbProFoldetId = new StringBuilder();
						sbProFoldetId.append(rs.getString("pro_folder_id"));
					} else {
						sbProFoldetId.append(","+rs.getString("pro_folder_id"));
					}
					
					if(sbMainFolderId == null){
						sbMainFolderId = new StringBuilder();
						sbMainFolderId.append(rs.getString("pro_folder_id"));
					} else {
						sbMainFolderId.append(","+rs.getString("pro_folder_id"));
					}
					
					if(sbDocId == null){
						sbDocId = new StringBuilder();
						sbDocId.append(rs.getString("pro_document_id"));
					} else {
						sbDocId.append(","+rs.getString("pro_document_id"));
					}
					
				}
			}
			rs.close();
			pst.close();
			
			if(sbProFoldetId !=null){
				sbQuery = new StringBuilder();
				sbQuery.append("select * from project_document_details where (file_size is null or file_size = '') and pro_document_id in ("+sbProFoldetId.toString()+") ");
				if(sbMainDocId !=null){
					sbQuery.append(" and pro_document_id not in("+sbMainDocId.toString()+")");
				}
				if(sbMainFolderId !=null){
					sbQuery.append(" and pro_document_id not in("+sbMainFolderId.toString()+")");
				}
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
//					System.out.println("8 pst======>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					if(uF.parseToInt(rs.getString("pro_folder_id")) == 0){
						if(uF.parseToInt(rs.getString("project_category")) == 1){
							List<List<String>> proFolderList = hmProFolderData.get(rs.getString("pro_id"));
							if(proFolderList == null) proFolderList = new ArrayList<List<String>>();
	
							List<String> innerList = new ArrayList<String>();
							innerList.add(rs.getString("pro_document_id"));
							innerList.add(rs.getString("folder_name"));
							proFolderList.add(innerList);
							hmProFolderData.put(rs.getString("pro_id"), proFolderList);
							
							List<String> innerAllFoderList = new ArrayList<String>();
							innerAllFoderList.add(rs.getString("pro_document_id"));
							innerAllFoderList.add(rs.getString("folder_name"));
							allFolderList.add(innerAllFoderList);
							
							if(sbOrgProjectId == null){
								sbOrgProjectId = new StringBuilder();
								sbOrgProjectId.append(rs.getString("pro_id"));
							} else {
								sbOrgProjectId.append(","+rs.getString("pro_id"));
							}
							
						} else if(uF.parseToInt(rs.getString("project_category")) == 2){
							List<List<String>> folderList = hmFolderData.get(rs.getString("align_with"));
							if(folderList == null) folderList = new ArrayList<List<String>>();
	
							List<String> innerList = new ArrayList<String>();
							innerList.add(rs.getString("pro_document_id"));
							innerList.add(rs.getString("folder_name"));
							folderList.add(innerList);
							hmFolderData.put(rs.getString("align_with"), folderList);
							
							List<String> innerAllFoderList = new ArrayList<String>();
							innerAllFoderList.add(rs.getString("pro_document_id"));
							innerAllFoderList.add(rs.getString("folder_name"));
							allFolderList.add(innerAllFoderList);
							
							if(sbOrgCategoryId == null){
								sbOrgCategoryId = new StringBuilder();
								sbOrgCategoryId.append(rs.getString("align_with"));
							} else {
								sbOrgCategoryId.append(","+rs.getString("align_with"));
							}
						}
					} 
				}
				rs.close();
				pst.close();
			}
			
			if(sbMainFolderId !=null){
				sbQuery = new StringBuilder();
				sbQuery.append("select * from project_document_details where pro_folder_id in("+sbMainFolderId.toString()+") and (file_size is null or file_size = '') ");
				if(sbDocId != null){
					sbQuery.append(" and pro_document_id not in ("+sbDocId.toString()+")");
				}
				if(sbMainDocId !=null){
					sbQuery.append(" and pro_document_id not in("+sbMainDocId.toString()+")");
				}
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
//					System.out.println("9 pst======>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<List<String>> subFolderList = hmSubFolderData.get(rs.getString("pro_folder_id"));
					if(subFolderList == null) subFolderList = new ArrayList<List<String>>();
	
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("pro_document_id"));
					innerList.add(rs.getString("folder_name"));
					subFolderList.add(innerList);
					hmSubFolderData.put(rs.getString("pro_folder_id"), subFolderList);
				}
				rs.close();
				pst.close();
			}
			
			
			request.setAttribute("hmProFolderData", hmProFolderData);
			request.setAttribute("hmFolderData", hmFolderData);
			request.setAttribute("allFolderList", allFolderList);
			request.setAttribute("hmSubFolderData", hmSubFolderData);
			
//			System.out.println("sbOrgCategoryId ==>> " + sbOrgCategoryId);
			if(sbOrgCategoryId !=null){
				List<List<String>> categoryList = new ArrayList<List<String>>();
				pst = con.prepareStatement("select * from project_category_details where project_category_id in ("+sbOrgCategoryId.toString()+") order by project_category");
//					System.out.println("10 pst======>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("project_category_id"));
					innerList.add(rs.getString("project_category"));
					categoryList.add(innerList);
				}
				rs.close();
				pst.close();
				
				request.setAttribute("categoryList", categoryList);
//				System.out.println("categoryList Java ==>> " + categoryList);
			}
			
			
			if(sbOrgProjectId !=null){
				List<List<String>> projectList = new ArrayList<List<String>>();
				pst = con.prepareStatement("select pro_id, pro_name from projectmntnc where pro_id in ("+sbOrgProjectId.toString()+") order by pro_name");
//				System.out.println("11 pst======>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("pro_id"));
					innerList.add(rs.getString("pro_name"));
					projectList.add(innerList);
				}
				rs.close();
				pst.close();
				request.setAttribute("projectList", projectList);
			}
			
		} catch (Exception e) {
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}                                                                                                                                                                                                                                                                                                                                                                                            
 
	private void getData(UtilityFunctions uF) {
//		System.out.println("in getData====>");
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);

			SortedSet<String> setDocList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select pro_name from projectmntnc where pro_id in (select pro_id from project_document_details where " +
					"project_category=1 and pro_folder_id=0 ");
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
			sbQuery.append(" group by pro_id) and org_id=? order by pro_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strOrgId));
//			System.out.println("1 pst====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				setDocList.add(rs.getString("pro_name"));
			}
			rs.close();
			pst.close();

			sbQuery = new StringBuilder();
			sbQuery.append("select * from project_category_details where project_category_id in (select align_with from project_document_details where " +
					"(project_category=2 or project_category=0) and pro_folder_id=0 ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbQuery.append(" group by align_with) and org_id=? order by project_category");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strOrgId));
//			System.out.println("2 pst====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				setDocList.add(rs.getString("project_category"));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select pro_document_id,folder_name from (select pro_document_id,folder_name from project_document_details " +
					"where project_category=1 and (file_size is null or file_size = '') ");
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
			} 
			sbQuery.append(" and pro_id in (select pro_id from projectmntnc where org_id=?)");
			sbQuery.append(" union ");
			sbQuery.append("select pro_document_id,folder_name from project_document_details where project_category=2 and (file_size is null or file_size = '')");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbQuery.append("and align_with in (select project_category_id from project_category_details where org_id=?)) a order by pro_document_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strOrgId));
			pst.setInt(2, uF.parseToInt(strOrgId));
//			System.out.println("3 pst====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				setDocList.add(rs.getString("folder_name"));
			}
			rs.close();
			pst.close();
//			System.out.println("setDocList ===>> " + setDocList);
			
			StringBuilder sbData = null;
			Iterator<String> it = setDocList.iterator();
			while (it.hasNext()){
				String strData = it.next();
				if(sbData == null){
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null){
				sbData = new StringBuilder();
			}
			
			request.setAttribute("sbData", sbData.toString());
//			System.out.println("sbData======>"+sbData.toString());
			
		} catch (Exception e) {
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}


	public void getDocumentDetails(UtilityFunctions uF) {
//		System.out.println("in getDocumentDetails====>");
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);

			resourceList = new FillEmployee(request).fillEmployeeName(null, null, uF.parseToInt(strOrgId), 0, session);
			
			StringBuilder sbOrgResources= new StringBuilder();
			for(int i=0; resourceList!=null && i<resourceList.size(); i++) {
				sbOrgResources.append("<option value='"+resourceList.get(i).getEmployeeId()+"'>"+resourceList.get(i).getEmployeeName()+"</option>");
			}
			request.setAttribute("sbOrgResources", sbOrgResources.toString());
			
			
			StringBuilder sbOrgProjects = new StringBuilder();
			pst = con.prepareStatement("select * from projectmntnc where org_id=? order by pro_name");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbOrgProjects.append("<option value='"+rs.getString("pro_id")+"'>"+rs.getString("pro_name")+"</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbOrgProjects", sbOrgProjects.toString());
			

			StringBuilder sbOrgCategory = new StringBuilder();
			pst = con.prepareStatement("select * from project_category_details where org_id=? and project_category_id>1 order by project_category");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbOrgCategory.append("<option value='"+rs.getString("project_category_id")+"'>"+rs.getString("project_category")+"</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbOrgCategory", sbOrgCategory.toString());
			
			StringBuilder sbOrgSPOC = new StringBuilder();
			pst = con.prepareStatement("select poc_id, contact_fname, contact_lname from client_poc where poc_id in (select poc from projectmntnc " +
					"where org_id=?) order by contact_fname,contact_lname");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				sbOrgSPOC.append("<option value='" + rs.getString("poc_id") + "'>" + uF.showData(rs.getString("contact_fname"), "").trim()+" "+uF.showData(rs.getString("contact_lname"), "").trim()+ "</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbOrgSPOC", sbOrgSPOC.toString());
			
			List<List<String>> categoryList = new ArrayList<List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from project_category_details where org_id=? and project_category_id in (select align_with from project_document_details where " +
					"(project_category=2 or project_category=0) and pro_folder_id=0 ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)) {
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
			} else if(strUserType != null && strUserType.equals(CUSTOMER)) {
				sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbQuery.append(" group by align_with) order by project_category");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strOrgId));
			System.out.println("1 pst=======> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("project_category_id"));
				innerList.add(rs.getString("project_category"));
				categoryList.add(innerList);
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("categoryList", categoryList);
			
//			System.out.println("categoryList ======>> " + categoryList);
			
			
			Map<String, List<List<String>>> hmFolderData = new HashMap<String, List<List<String>>>();
			List<List<String>> folderList = new ArrayList<List<String>>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from project_document_details where pro_folder_id=0 and project_category=2 and (file_size is null or file_size = '') ");
			if(strUserType != null && strUserType.equals(EMPLOYEE)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
			} else if(strUserType != null && strUserType.equals(MANAGER)){
				sbQuery.append(" and ((sharing_type = 2 and sharing_resources like '%,"+strSessionEmpId+",%') or sharing_type = 0) ");
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbQuery.append("order by pro_document_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("2 pst=======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				folderList = hmFolderData.get(rs.getString("align_with"));
				if(folderList == null) folderList = new ArrayList<List<String>>();

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("pro_document_id"));
				innerList.add(rs.getString("folder_name"));
				folderList.add(innerList);
				hmFolderData.put(rs.getString("align_with"), folderList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmFolderData", hmFolderData);
//			System.out.println("hmFolderData ======>> " + hmFolderData);
			
			
			List<List<String>> projectList = new ArrayList<List<String>>();
			sbQuery = new StringBuilder();
			sbQuery.append("select pro_id, pro_name from projectmntnc where org_id=? and pro_id in (select pro_id from project_document_details where " +
					"project_category=1 and pro_folder_id=0 ");
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
			//===start parvez date: 13-10-2022===	
			} else if(strUserType != null && strUserType.equals(CUSTOMER)){
				sbQuery.append(" and sharing_poc like '%,"+strSessionEmpId+",%'");
			}
			sbQuery.append(" group by pro_id) order by pro_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strOrgId));
//			System.out.println("3 pst=======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("pro_id"));
				innerList.add(rs.getString("pro_name"));
				projectList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("projectList", projectList);
			
			
			Map<String, List<List<String>>> hmProFolderData = new HashMap<String, List<List<String>>>();
			List<List<String>> proFolderList = new ArrayList<List<String>>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from project_document_details where pro_folder_id=0 and project_category=1 and (file_size is null or file_size = '') ");
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
			sbQuery.append("order by pro_document_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("4 pst=======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				proFolderList = hmProFolderData.get(rs.getString("pro_id"));
				if(proFolderList == null) proFolderList = new ArrayList<List<String>>();

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("pro_document_id"));
				innerList.add(rs.getString("folder_name"));
				proFolderList.add(innerList);
				hmProFolderData.put(rs.getString("pro_id"), proFolderList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProFolderData", hmProFolderData);
			
			
			List<List<String>> allFolderList = new ArrayList<List<String>>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from project_document_details where pro_folder_id=0 and (file_size is null or file_size = '') ");
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
			sbQuery.append(" and ((project_category = 1 and pro_id in (select pro_id from projectmntnc where org_id=? )) " +
					"or (project_category = 2 and align_with in (select project_category_id from project_category_details where org_id=?))) ");
			sbQuery.append(" order by pro_document_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strOrgId));
			pst.setInt(2, uF.parseToInt(strOrgId));
//			System.out.println("5 pst=======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("pro_document_id"));
				innerList.add(rs.getString("folder_name"));
				allFolderList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("allFolderList", allFolderList);
			
			
			Map<String, List<List<String>>> hmSubFolderData = new HashMap<String, List<List<String>>>();
			List<List<String>> subFolderList = new ArrayList<List<String>>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from project_document_details where pro_folder_id > 0 and (file_size is null or file_size = '') ");
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
//			System.out.println("6 pst=======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				subFolderList = hmSubFolderData.get(rs.getString("pro_folder_id"));
				if(subFolderList == null) subFolderList = new ArrayList<List<String>>();

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("pro_document_id"));
				innerList.add(rs.getString("folder_name"));
				subFolderList.add(innerList);
				hmSubFolderData.put(rs.getString("pro_folder_id"), subFolderList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSubFolderData", hmSubFolderData);
//			System.out.println("hmSubFolderData ======>> " + hmSubFolderData);
			
//			System.out.println("sbProCategory======>"+sbProCategory.toString());
			
		} catch (Exception e) {
			
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

	public List<FillEmployee> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<FillEmployee> resourceList) {
		this.resourceList = resourceList;
	}

	public String getStrSearchDoc() {
		return strSearchDoc;
	}

	public void setStrSearchDoc(String strSearchDoc) {
		this.strSearchDoc = strSearchDoc;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}
	
}
