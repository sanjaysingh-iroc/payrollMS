package com.konnect.jpms.task;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.mail.Flags.Flag;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.config.Results;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillTask;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadProjectDocuments;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditAndDeleteFeed extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	
	private String postId; 
	private String type;
	
	private List<FillEmployee> resourceList;
	private List<FillAlignedType> alignTypeList;
	
	private String strCommunication;
	private String strAlignWith;
	private String strAlignWithIds;
	private String[] strTaggedWith;
	private String[] strVisibilityWith;
	private String strVisibility;
	
	private List<FillProjectList> projectList;
	private List<FillProjectList> projectFreqList;
	private List<FillTask> taskList;
	private List<FillProjectDocument> documentList;
	private List<FillProjectInvoice> invoiceList;
	
	private String strCommunication1;
	private String strAlignWith1;
	private String strAlignWithIds1;
	private String strTaggedWith1;
	private String strTaggedIds;
	private String strVisibilityWith1;
	private String strVisibility1;
	
	private String pageFrom;
	private String proId;
	private String taskId;
	private String proFreqId;
	private String invoiceId;
	
	
	private File strFeedDoc;
	private String strFeedDocFileName;
	private String submit;
	
	public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if (CF == null) {
//			CF = new CommonFunctions();
//		}
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getPageFrom()!=null && (getPageFrom().trim().equalsIgnoreCase("Project") || getPageFrom().trim().equalsIgnoreCase("VAPProject") || getPageFrom().trim().equalsIgnoreCase("Task") || getPageFrom().trim().equalsIgnoreCase("Timesheet") || 
			getPageFrom().trim().equalsIgnoreCase("Invoice") || getPageFrom().trim().equalsIgnoreCase("VAPTask"))) {
			String strProEmpId = getProEmpIds(uF);
			resourceList = new FillEmployee(request).fillProjectEmployeeName(strProEmpId);
			
		} else if(strUserType != null && strUserType.equals(CUSTOMER)) {
			String strCustProEmpId = getCustomerProEmpIds(uF);
			resourceList = new FillEmployee(request).fillProjectEmployeeName(strCustProEmpId);
			
		} else {
			resourceList = new FillEmployee(request).fillEmployeeName(null, null, 0, 0, session);
			
		}
		alignTypeList = new FillAlignedType(request).fillAlignedTypeList(getPageFrom());
		
		if(getType() != null && getType().equals("P_E")) {
			getFeedData(uF);
		} else if(getType() != null && getType().equals("P_D")) {
			deleteFeed(uF);
		} else if(getType() != null && (getType().equals("U") || getType().equals("C"))) {
			updateFeedData(uF);
		}
		
		if(getSubmit()!=null && !getSubmit().equals("")){
			
			setType("U");
			updateFeedData1(uF);
			return SUCCESS;
		}else{
			return LOAD;
		}
	}
	
	private void editProjectDocuments(String postId,UtilityFunctions uF ,int visibility,StringBuilder visibilityWithId){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			con = db.makeConnection(con);
			String proId = null;
			if(uF.parseToInt(getStrAlignWith()) == PROJECT) {
				proId = getStrAlignWithIds();
				
			} else if(uF.parseToInt(getStrAlignWith()) == TASK) {
				proId = CF.getProjectIdByTaskId(con, getStrAlignWithIds());
				
			} else if(uF.parseToInt(getStrAlignWith()) == PRO_TIMESHEET) {
				proId = CF.getProjectIdByProFreqId(con, getStrAlignWithIds());
				
			} else if(uF.parseToInt(getStrAlignWith()) == DOCUMENT) {
				
			} else if(uF.parseToInt(getStrAlignWith()) == INVOICE) {
				proId = CF.getProjectIdByInvoiceId(con, getStrAlignWithIds());
				
			} else if(getPageFrom()!=null && (getPageFrom().trim().equalsIgnoreCase("Project") || getPageFrom().trim().equalsIgnoreCase("VAPProject") || getPageFrom().trim().equalsIgnoreCase("Task") || 
				getPageFrom().trim().equalsIgnoreCase("Timesheet") || getPageFrom().trim().equalsIgnoreCase("Invoice") || getPageFrom().trim().equalsIgnoreCase("VAPTask")) 
				&& uF.parseToInt(getStrAlignWith()) == 0) {
				proId = getProId();
			}
			
			String mainPathWithOrg = CF.getProjectDocumentFolder()+strSessionOrgId;

			File fileOrg = new File(mainPathWithOrg);
			if (!fileOrg.exists()) {
				if (fileOrg.mkdir()) {
					System.out.println("Org Directory is created!");
				}
			}
			
			String mainProPath = mainPathWithOrg;
			
			if(uF.parseToInt(proId) > 0) {
				mainProPath = mainPathWithOrg+"/Projects";
			} else {
				mainProPath = mainPathWithOrg+"/Uncategorised";
			}
			File file = new File(mainProPath);
			if (!file.exists()) {
				if (file.mkdir()) {
					System.out.println("Projects Directory is created!");
				}
			}
			
			String proNameFolder = null;
			if(uF.parseToInt(proId) > 0) {
				proNameFolder = mainProPath +"/"+proId;
			} else {
				proNameFolder = mainProPath;
			}
			File file1 = new File(proNameFolder);
			if (!file1.exists()) {
				if (file1.mkdir()) {
					System.out.println("pro_id Directory is created!");
				}
			}
			
			double lengthBytes = 0;
			
			if(getStrFeedDoc() != null) {
				lengthBytes = getStrFeedDoc().length();
			}
	//			boolean isFileExist = false;
			
			String strCheckFile = proNameFolder+"/"+getStrFeedDocFileName();
			String strFileName = getStrFeedDocFileName();
			File f = new File(strCheckFile);
		    if(f.isFile() && f.exists()) {
		    	String strCopyFile = uF.getCreateNewFileName("Copy_"+getStrFeedDocFileName(), proNameFolder, getStrFeedDocFileName(), 0);
		    	strFileName = strCopyFile;
		    }
	
			
			if(lengthBytes > 0) { // && !isFileExist
				
				pst = con.prepareStatement("update project_document_details set client_id =?,pro_id=?,added_by=?,entry_date=?,folder_file_type=?," +
						"pro_folder_id=?,align_with=?,sharing_type=?,sharing_resources=?,project_category=?,scope_document=?,description=?,doc_parent_id=?,"+
						"is_edit=?,is_delete=?,is_cust_add=?,sharing_poc=?,doc_version=? where feed_id=?");
						
				pst.setInt(1, 0);
				pst.setInt(2, uF.parseToInt(proId));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
	//					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(5, "file");
				pst.setInt(6, 0);
				pst.setInt(7, 0);
				pst.setInt(8, visibility);
				pst.setString(9, visibilityWithId.toString());
				if(uF.parseToInt(proId) > 0) {
					pst.setInt(10, 1);
				} else {
					pst.setInt(10, 0);
				}
				pst.setString(11, "");
				pst.setString(12, "");
				pst.setInt(13, 0);
				pst.setBoolean(14, false);
				pst.setBoolean(15, false);
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					pst.setBoolean(16, true);
				} else {
					pst.setBoolean(16, false);
				}
				pst.setString(17, "");
				pst.setInt(18, 1);
				pst.setInt(19, uF.parseToInt(getPostId()));
	//				System.out.println("pst====>"+pst);
				int x = pst.executeUpdate();
				pst.close();
				
				String proDocumentId = "";
				pst = con.prepareStatement("select max(pro_document_id)as pro_document_id from project_document_details where feed_id = ?");
				pst.setInt(1, uF.parseToInt(getPostId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					proDocumentId = rs.getString("pro_document_id");
				}
				rs.close();
				pst.close();
				
				uploadProjectDocuments(con, getStrFeedDoc(), strFileName, proNameFolder, proDocumentId, getPostId());
			}	
		}catch (Exception e) {
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
//				System.out.println("empId2 ===> "+empId2+" getEmpImage() ===> "+getEmpImage());
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
		
	private String getProEmpIds(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		StringBuilder proEmpIds = null; 
		try {
			con = db.makeConnection(con);
			
			List<String> alEmpId = new ArrayList<String>();
		//===start parvez date: 13-10-2022===	
//			pst = con.prepareStatement("select project_owner,added_by from projectmntnc where pro_id = ?");
			pst = con.prepareStatement("select project_owners,added_by from projectmntnc where pro_id = ?");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				/*if(proEmpIds == null) {
					proEmpIds = new StringBuilder();
					proEmpIds.append(rs.getString("project_owner"));
					alEmpId.add(rs.getString("project_owner"));
					if(!alEmpId.contains(rs.getString("added_by"))) {
						proEmpIds.append(","+rs.getString("added_by"));
						alEmpId.add(rs.getString("added_by"));
					}
				} else {
					proEmpIds.append(","+rs.getString("project_owner"));
					alEmpId.add(rs.getString("project_owner"));
					if(!alEmpId.contains(rs.getString("added_by"))) {
						proEmpIds.append(","+rs.getString("added_by"));
						alEmpId.add(rs.getString("added_by"));
					}
				}*/
				List<String> tempList = new ArrayList<String>();
				if(rs.getString("project_owners")!=null){
					tempList = Arrays.asList(rs.getString("project_owners").split(","));
				}
				for(int j=1; j<tempList.size();j++){
					if(proEmpIds == null) {
						proEmpIds = new StringBuilder();
						proEmpIds.append(tempList.get(j));
						alEmpId.add(tempList.get(j));
						if(!alEmpId.contains(rs.getString("added_by"))) {
							proEmpIds.append(","+rs.getString("added_by"));
							alEmpId.add(rs.getString("added_by"));
						}
					} else {
						proEmpIds.append(","+tempList.get(j));
						alEmpId.add(tempList.get(j));
						if(!alEmpId.contains(rs.getString("added_by"))) {
							proEmpIds.append(","+rs.getString("added_by"));
							alEmpId.add(rs.getString("added_by"));
						}
					}
				}
				
		//===end parvez date: 13-10-2022===		
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select emp_id from project_emp_details where pro_id = ?");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				if(proEmpIds == null) {
					proEmpIds = new StringBuilder();
					if(!alEmpId.contains(rs.getString("emp_id"))) {
						proEmpIds.append(rs.getString("emp_id"));
						alEmpId.add(rs.getString("emp_id"));
					}
				} else {
					if(!alEmpId.contains(rs.getString("emp_id"))) {
						proEmpIds.append(","+rs.getString("emp_id"));
						alEmpId.add(rs.getString("emp_id"));
					}
				}
			}
			rs.close();
			pst.close();
			if(proEmpIds == null) {
				proEmpIds = new StringBuilder();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return proEmpIds.toString();
	}


	private String getCustomerProEmpIds(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		StringBuilder proEmpIds = null; 
		try {
			con = db.makeConnection(con);
	//===start parvez date: 13-10-2022===		
			StringBuilder sbProIds = null;
			pst = con.prepareStatement("select pro_id from projectmntnc where poc = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while(rs.next()) {
				if(sbProIds == null) {
					sbProIds = new StringBuilder();
//					sbProIds.append(rs.getString("project_owner"));
					sbProIds.append(rs.getString("pro_id"));
				} else {
//					sbProIds.append(","+rs.getString("project_owner"));
					sbProIds.append(","+rs.getString("pro_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbProIds == null) {
				sbProIds = new StringBuilder();
			}
			
			if(sbProIds != null && sbProIds.length() > 0) {
				List<String> alEmpId = new ArrayList<String>();
				/*pst = con.prepareStatement("select project_owner,added_by from projectmntnc where pro_id in ("+sbProIds.toString()+")");
				pst.setInt(1, uF.parseToInt(getProId()));
				rs = pst.executeQuery();
				while(rs.next()) {
					if(proEmpIds == null) {
						proEmpIds = new StringBuilder();
						proEmpIds.append(rs.getString("project_owner"));
						alEmpId.add(rs.getString("project_owner"));
						if(!alEmpId.contains(rs.getString("added_by"))) {
							proEmpIds.append(","+rs.getString("added_by"));
							alEmpId.add(rs.getString("added_by"));
						}
					} else {
						proEmpIds.append(","+rs.getString("project_owner"));
						alEmpId.add(rs.getString("project_owner"));
						if(!alEmpId.contains(rs.getString("added_by"))) {
							proEmpIds.append(","+rs.getString("added_by"));
							alEmpId.add(rs.getString("added_by"));
						}
					}
				}
				rs.close();
				pst.close();*/
				
				pst = con.prepareStatement("select project_owners,added_by from projectmntnc where pro_id in ("+sbProIds.toString()+")");
				pst.setInt(1, uF.parseToInt(getProId()));
				rs = pst.executeQuery();
				while(rs.next()) {
					
					List<String> tempList = new ArrayList<String>();
					if(rs.getString("project_owners")!=null){
						tempList = Arrays.asList(rs.getString("project_owners").split(","));
					}
					for(int j=1; j<tempList.size();j++){
						if(proEmpIds == null) {
							proEmpIds = new StringBuilder();
							proEmpIds.append(tempList.get(j));
							alEmpId.add(tempList.get(j));
							if(!alEmpId.contains(rs.getString("added_by"))) {
								proEmpIds.append(","+rs.getString("added_by"));
								alEmpId.add(rs.getString("added_by"));
							}
						} else {
							proEmpIds.append(","+tempList.get(j));
							alEmpId.add(tempList.get(j));
							if(!alEmpId.contains(rs.getString("added_by"))) {
								proEmpIds.append(","+rs.getString("added_by"));
								alEmpId.add(rs.getString("added_by"));
							}
						}
					}
					
				}
				rs.close();
				pst.close();
		//===end parvez date: 13-10-2022===		
				
				pst = con.prepareStatement("select emp_id from project_emp_details where pro_id in ("+sbProIds.toString()+")");
				pst.setInt(1, uF.parseToInt(getProId()));
				rs = pst.executeQuery();
				while(rs.next()) {
					if(proEmpIds == null) {
						proEmpIds = new StringBuilder();
						if(!alEmpId.contains(rs.getString("emp_id"))) {
							proEmpIds.append(rs.getString("emp_id"));
							alEmpId.add(rs.getString("emp_id"));
						}
					} else {
						if(!alEmpId.contains(rs.getString("emp_id"))) {
							proEmpIds.append(","+rs.getString("emp_id"));
							alEmpId.add(rs.getString("emp_id"));
						}
					}
				}
				rs.close();
				pst.close();
			}
			
			if(proEmpIds == null) {
				proEmpIds = new StringBuilder();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return proEmpIds.toString();
	}
	
	private void updateFeedData1(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String strCustName = null;
		try {
			con = db.makeConnection(con);
			
			
			
			if(getType() != null && getType().equals("U")) {
				StringBuilder sbTaggedWith = null;
				for(int i=0; getStrTaggedWith() != null && i<getStrTaggedWith().length; i++) {
					
					if(sbTaggedWith == null) {
						sbTaggedWith = new StringBuilder();
						sbTaggedWith.append("," + getStrTaggedWith()[i] + ",");
					} else {
						sbTaggedWith.append(getStrTaggedWith()[i] + ",");
					}
				}
				if(sbTaggedWith == null) {
					sbTaggedWith = new StringBuilder();
				}
				
				
				int sbVisibility = 0;
				StringBuilder sbVisibilityWith = null;
				for(int i=0; getStrVisibilityWith() != null && i<getStrVisibilityWith().length; i++) {
					if(sbVisibilityWith == null) {
						sbVisibilityWith = new StringBuilder();
						sbVisibilityWith.append("," + getStrVisibilityWith()[i] + ",");
						sbVisibility = 1;
						
					} else {
						sbVisibilityWith.append(getStrVisibilityWith()[i] + ",");
						sbVisibility++;
					}
				}
				if(sbVisibilityWith == null) {
					sbVisibilityWith = new StringBuilder();
				}
				
				String strProTeamId = getProjectteamIdsByTaskProFreqInvoice(con, uF, getPageFrom(), getStrAlignWith(), getStrAlignWithIds(), getTaskId(), getProFreqId(), getInvoiceId());
				String strProSPOCId = getProjectSPOCIdByTaskProFreqInvoice(con, uF, getPageFrom(), getStrAlignWith(), getStrAlignWithIds(), getTaskId(), getProFreqId(), getInvoiceId());
				
				pst = con.prepareStatement("update communication_1 set communication=?,align_with=?,align_with_id=?,tagged_with=?,doc_shared_with_id=?," +
					"doc_id=?,visibility=?,visibility_with_id=?,update_time=?,client_tagged_with=?,client_visibility_with_id=? where communication_id=?");
				pst.setString(1, getStrCommunication());
				if(getPageFrom()!=null && (getPageFrom().trim().equalsIgnoreCase("Project") || getPageFrom().trim().equalsIgnoreCase("VAPProject")) && uF.parseToInt(getStrAlignWith()) == 0) {
					pst.setInt(2, PROJECT);
					pst.setInt(3, uF.parseToInt(getProId()));
				} else if(getPageFrom()!=null && (getPageFrom().trim().equalsIgnoreCase("Task") || getPageFrom().trim().equalsIgnoreCase("VAPTask")) && uF.parseToInt(getStrAlignWith()) == 0) {
					pst.setInt(2, TASK);
					pst.setInt(3, uF.parseToInt(getTaskId()));
				} else if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Timesheet") && uF.parseToInt(getStrAlignWith()) == 0) {
					pst.setInt(2, PRO_TIMESHEET);
					pst.setInt(3, uF.parseToInt(getProFreqId()));
				} else if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Invoice") && uF.parseToInt(getStrAlignWith()) == 0) {
					pst.setInt(2, INVOICE);
					pst.setInt(3, uF.parseToInt(getInvoiceId()));
				} else {
					pst.setInt(2, uF.parseToInt(getStrAlignWith()));
					pst.setInt(3, uF.parseToInt(getStrAlignWithIds()));
				}
				pst.setString(4, sbTaggedWith.toString());
				pst.setString(5, "");
				pst.setString(6, "");
				pst.setInt(7, sbVisibility);
				if(uF.parseToInt(getStrVisibility()) == S_TEAM) {
					pst.setString(8, strProTeamId);
				} else if(uF.parseToInt(getStrVisibility()) == S_RESOURCE) {
					pst.setString(8, sbVisibilityWith.toString());
				} else {
					pst.setString(8, null);
				}
	//			pst.setInt(9, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(9, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				if(uF.parseToInt(getStrVisibility()) == S_CUSTOMER) {
					pst.setString(10, strProSPOCId);
					pst.setString(11, strProSPOCId);
				} else {
					pst.setString(10, null);
					pst.setString(11, null);
				}
				pst.setInt(12, uF.parseToInt(getPostId()));
//				System.out.println("pst ====>> " + pst);
				pst.executeUpdate();
				pst.close();
				
				if(getStrFeedDocFileName()!=null && !getStrFeedDocFileName().equals("") && getStrFeedDoc()!=null && getStrFeedDoc().length()>0){
					editProjectDocuments(getPostId(),uF,sbVisibility,sbVisibilityWith);
				}
			}
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			
			Map<String, String> hmProjectName = CF.getProjectNameMap(con);
			Map<String, String> hmProDocName = CF.getProDocsNameMap(con);
			Map<String, String> hmProInvoiceName = CF.getProInvoiceNameMap(con);
			
			pst = con.prepareStatement("select * from communication_1 where communication_id = ?");
			pst.setInt(1, uF.parseToInt(getPostId()));
			rs = pst.executeQuery();
			List<String> innerList = new ArrayList<String>();
			while(rs.next()) {
				
				List<String> alLikeIds = new ArrayList<String>();
				String yourLike = null;
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					if(rs.getString("client_like_ids") != null && !rs.getString("client_like_ids").equals("")) {
						alLikeIds = Arrays.asList(rs.getString("client_like_ids").split(","));
						if(alLikeIds.contains(strSessionEmpId)) {
							yourLike = "Y";
						}
					}
				} else {
					if(rs.getString("like_ids") != null && !rs.getString("like_ids").equals("")) {
						alLikeIds = Arrays.asList(rs.getString("like_ids").split(","));
						if(alLikeIds.contains(strSessionEmpId)) {
							yourLike = "Y";
						}
					}
				}
				
				innerList.add(rs.getString("communication_id")); //0
				innerList.add(rs.getString("communication")); //1
				innerList.add(uF.showData(uF.getPostAlignedWith(rs.getInt("align_with")), "")); //2
				if(rs.getInt("align_with") == PROJECT) {
					innerList.add(hmProjectName.get(rs.getString("align_with_id"))); //3
				} else if(rs.getInt("align_with") == TASK) {
					innerList.add(CF.getProjectTaskNameByTaskId(con, uF, rs.getString("align_with_id"))); //3
				} else if(rs.getInt("align_with") == PRO_TIMESHEET) {
					innerList.add(CF.getProNamePlusFreqNameById(con, rs.getString("align_with_id"))); //3
				} else if(rs.getInt("align_with") == DOCUMENT) {
					innerList.add(hmProDocName.get(rs.getString("align_with_id"))); //3
				} else if(rs.getInt("align_with") == INVOICE) {
					innerList.add(hmProInvoiceName.get(rs.getString("align_with_id"))); //3
				} else {
					innerList.add(""); //3
				}
				innerList.add(getResourceName(hmResourceName, rs.getString("tagged_with"), hmCustName, rs.getString("client_tagged_with"))); //4
				innerList.add(uF.showData(uF.getPostSharedWith(rs.getInt("visibility")), "")); //5
				innerList.add(getResourceName(hmResourceName, rs.getString("visibility_with_id"), hmCustName, rs.getString("client_visibility_with_id"))); //6
				innerList.add(uF.showData(rs.getString("likes"), "0")); //7
				innerList.add(getResourceName(hmResourceName, rs.getString("like_ids"), hmCustName, rs.getString("client_like_ids"))); //8
				if(uF.parseToInt(rs.getString("client_created_by")) > 0) {
					String createdByImage = hmCustImage.get(rs.getString("client_created_by"));
					String strClientId = CF.getClientIdBySPOCId(con, uF, rs.getString("client_created_by"));
					String addedImage = CF.getStrDocRetriveLocation()+I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
					String addedImg = "<img class=\"lazy\" src=\"userImages/avatar_photo.png\" data-original=\""+ addedImage +"\" height=\"30\" width=\"30\">";
					innerList.add(addedImg); //9
					innerList.add(hmResourceName.get(rs.getString("client_created_by"))); //10
				} else {
					String createdByImage = hmResourceImage.get(rs.getString("created_by"));
					String addedImage = CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_60x60+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
					String addedImg = "<img class='lazy' src='userImages/avatar_photo.png' data-original='" + addedImage + "' height='30' width='30'>";
					innerList.add(addedImg); //9
					innerList.add(hmResourceName.get(rs.getString("created_by"))); //10
				}
//				System.out.println(" =====>> " + uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));
				innerList.add(uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM)); //11
				innerList.add(uF.showData(yourLike, "N")); //12
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					innerList.add(rs.getString("client_created_by")); //13
				} else {
					innerList.add(rs.getString("created_by")); //13
				}
				
				
			}
			rs.close();
			pst.close();
			
//			System.out.println("innerList ===>> " + innerList);
			
			request.setAttribute("innerList", innerList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	private void updateFeedData(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String strCustName = null;
		try {
			con = db.makeConnection(con);
			
			if(getType() != null && getType().equals("U")) {
				
				String strProTeamId = getProjectteamIdsByTaskProFreqInvoice(con, uF, getPageFrom(), getStrAlignWith1(), getStrAlignWithIds1(), getTaskId(), getProFreqId(), getInvoiceId());
				String strProSPOCId = getProjectSPOCIdByTaskProFreqInvoice(con, uF, getPageFrom(), getStrAlignWith1(), getStrAlignWithIds1(), getTaskId(), getProFreqId(), getInvoiceId());
				
				pst = con.prepareStatement("update communication_1 set communication=?,align_with=?,align_with_id=?,tagged_with=?,doc_shared_with_id=?," +
					"doc_id=?,visibility=?,visibility_with_id=?,update_time=?,client_tagged_with=?,client_visibility_with_id=? where communication_id=?");
				pst.setString(1, getStrCommunication1());
				if(getPageFrom()!=null && (getPageFrom().trim().equalsIgnoreCase("Project") || getPageFrom().trim().equalsIgnoreCase("VAPProject")) && uF.parseToInt(getStrAlignWith1()) == 0) {
					pst.setInt(2, PROJECT);
					pst.setInt(3, uF.parseToInt(getProId()));
				} else if(getPageFrom()!=null && (getPageFrom().trim().equalsIgnoreCase("Task") || getPageFrom().trim().equalsIgnoreCase("VAPTask")) && uF.parseToInt(getStrAlignWith1()) == 0) {
					pst.setInt(2, TASK);
					pst.setInt(3, uF.parseToInt(getTaskId()));
				} else if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Timesheet") && uF.parseToInt(getStrAlignWith()) == 0) {
					pst.setInt(2, PRO_TIMESHEET);
					pst.setInt(3, uF.parseToInt(getProFreqId()));
				} else if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Invoice") && uF.parseToInt(getStrAlignWith()) == 0) {
					pst.setInt(2, INVOICE);
					pst.setInt(3, uF.parseToInt(getInvoiceId()));
				} else {
					pst.setInt(2, uF.parseToInt(getStrAlignWith1()));
					pst.setInt(3, uF.parseToInt(getStrAlignWithIds1()));
				}
				pst.setString(4, getStrTaggedWith1());
				pst.setString(5, "");
				pst.setString(6, "");
				pst.setInt(7, uF.parseToInt(getStrVisibility1()));
				if(uF.parseToInt(getStrVisibility1()) == S_TEAM) {
					pst.setString(8, strProTeamId);
				} else if(uF.parseToInt(getStrVisibility1()) == S_RESOURCE) {
					pst.setString(8, getStrVisibilityWith1());
				} else {
					pst.setString(8, null);
				}
	//			pst.setInt(9, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(9, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				if(uF.parseToInt(getStrVisibility()) == S_CUSTOMER) {
					pst.setString(10, strProSPOCId);
					pst.setString(11, strProSPOCId);
				} else {
					pst.setString(10, null);
					pst.setString(11, null);
				}
				pst.setInt(12, uF.parseToInt(getPostId()));
//				System.out.println("pst ====>> " + pst);
				pst.executeUpdate();
				pst.close();
			}
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			
			Map<String, String> hmProjectName = CF.getProjectNameMap(con);
			Map<String, String> hmProDocName = CF.getProDocsNameMap(con);
			Map<String, String> hmProInvoiceName = CF.getProInvoiceNameMap(con);
			
			pst = con.prepareStatement("select * from communication_1 where communication_id = ?");
			pst.setInt(1, uF.parseToInt(getPostId()));
			rs = pst.executeQuery();
			List<String> innerList = new ArrayList<String>();
			while(rs.next()) {
				
				List<String> alLikeIds = new ArrayList<String>();
				String yourLike = null;
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					if(rs.getString("client_like_ids") != null && !rs.getString("client_like_ids").equals("")) {
						alLikeIds = Arrays.asList(rs.getString("client_like_ids").split(","));
						if(alLikeIds.contains(strSessionEmpId)) {
							yourLike = "Y";
						}
					}
				} else {
					if(rs.getString("like_ids") != null && !rs.getString("like_ids").equals("")) {
						alLikeIds = Arrays.asList(rs.getString("like_ids").split(","));
						if(alLikeIds.contains(strSessionEmpId)) {
							yourLike = "Y";
						}
					}
				}
				
				innerList.add(rs.getString("communication_id")); //0
				innerList.add(rs.getString("communication")); //1
				innerList.add(uF.showData(uF.getPostAlignedWith(rs.getInt("align_with")), "")); //2
				if(rs.getInt("align_with") == PROJECT) {
					innerList.add(hmProjectName.get(rs.getString("align_with_id"))); //3
				} else if(rs.getInt("align_with") == TASK) {
					innerList.add(CF.getProjectTaskNameByTaskId(con, uF, rs.getString("align_with_id"))); //3
				} else if(rs.getInt("align_with") == PRO_TIMESHEET) {
					innerList.add(CF.getProNamePlusFreqNameById(con, rs.getString("align_with_id"))); //3
				} else if(rs.getInt("align_with") == DOCUMENT) {
					innerList.add(hmProDocName.get(rs.getString("align_with_id"))); //3
				} else if(rs.getInt("align_with") == INVOICE) {
					innerList.add(hmProInvoiceName.get(rs.getString("align_with_id"))); //3
				} else {
					innerList.add(""); //3
				}
				innerList.add(getResourceName(hmResourceName, rs.getString("tagged_with"), hmCustName, rs.getString("client_tagged_with"))); //4
				innerList.add(uF.showData(uF.getPostSharedWith(rs.getInt("visibility")), "")); //5
				innerList.add(getResourceName(hmResourceName, rs.getString("visibility_with_id"), hmCustName, rs.getString("client_visibility_with_id"))); //6
				innerList.add(uF.showData(rs.getString("likes"), "0")); //7
				innerList.add(getResourceName(hmResourceName, rs.getString("like_ids"), hmCustName, rs.getString("client_like_ids"))); //8
				if(uF.parseToInt(rs.getString("client_created_by")) > 0) {
					String createdByImage = hmCustImage.get(rs.getString("client_created_by"));
					String strClientId = CF.getClientIdBySPOCId(con, uF, rs.getString("client_created_by"));
					String addedImage = CF.getStrDocRetriveLocation()+I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
					String addedImg = "<img class=\"lazy\" src=\"userImages/avatar_photo.png\" data-original=\""+ addedImage +"\" height=\"30\" width=\"30\">";
					innerList.add(addedImg); //9
					innerList.add(hmResourceName.get(rs.getString("client_created_by"))); //10
				} else {
					String createdByImage = hmResourceImage.get(rs.getString("created_by"));
					String addedImage = CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_60x60+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
					String addedImg = "<img class='lazy' src='userImages/avatar_photo.png' data-original='" + addedImage + "' height='30' width='30'>";
					innerList.add(addedImg); //9
					innerList.add(hmResourceName.get(rs.getString("created_by"))); //10
				}
//				System.out.println(" =====>> " + uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));
				innerList.add(uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM)); //11
				innerList.add(uF.showData(yourLike, "N")); //12
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					innerList.add(rs.getString("client_created_by")); //13
				} else {
					innerList.add(rs.getString("created_by")); //13
				}
				
				
			}
			rs.close();
			pst.close();
			
//			System.out.println("innerList ===>> " + innerList);
			
			request.setAttribute("innerList", innerList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private String getProjectSPOCIdByTaskProFreqInvoice(Connection con, UtilityFunctions uF, String pageFrom, String strAlignWith, String strAlignWithIds,
			String taskId, String proFreqId, String invoiceId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder proSPOCId = null;
		try {
			
			StringBuilder sbQuery = null;
			if(pageFrom == null || pageFrom.trim().equals("") || pageFrom.trim().equals("null")) {
				if(uF.parseToInt(strAlignWith) == PROJECT) {
					sbQuery = new StringBuilder();
					sbQuery.append("select poc from projectmntnc where pro_id = "+uF.parseToInt(strAlignWithIds)+" ");
//					pst = con.prepareStatement(" ?");
//					pst.setInt(1, uF.parseToInt(strAlignWithIds));
					
				} else if(uF.parseToInt(strAlignWith) == TASK) {
					sbQuery = new StringBuilder();
					sbQuery.append("select poc from projectmntnc where pro_id = (select pro_id from activity_info where task_id = "+uF.parseToInt(strAlignWithIds)+") ");
				} else if(uF.parseToInt(strAlignWith) == PRO_TIMESHEET) {
					sbQuery = new StringBuilder();
					sbQuery.append("select poc from projectmntnc where pro_id = (select pro_id from projectmntnc_frequency where pro_freq_id = "+uF.parseToInt(strAlignWithIds)+") ");
				} else if(uF.parseToInt(strAlignWith) == INVOICE) {
					sbQuery = new StringBuilder();
					sbQuery.append("select poc from projectmntnc where pro_id = (select pro_id from promntc_invoice_details where promntc_invoice_id = "+uF.parseToInt(strAlignWithIds)+") ");
				} 
			} else {
				if(pageFrom != null && (pageFrom.trim().equals("Project") || pageFrom.trim().equals("VAPProject"))) {
					if(uF.parseToInt(strAlignWith) == TASK) {
						sbQuery = new StringBuilder();
						sbQuery.append("select poc from projectmntnc where pro_id = (select pro_id from activity_info where task_id = "+uF.parseToInt(strAlignWithIds)+") ");
					} else {
						sbQuery = new StringBuilder();
						sbQuery.append("select poc from projectmntnc where pro_id = "+uF.parseToInt(proId)+" ");
					}
				} else if(pageFrom != null && (pageFrom.trim().equals("Task") || pageFrom.trim().equals("VAPTask"))) {
					sbQuery = new StringBuilder();
					sbQuery.append("select poc from projectmntnc where pro_id = (select pro_id from activity_info where task_id = "+uF.parseToInt(taskId)+") ");

				} else if(pageFrom != null && pageFrom.trim().equals("Timesheet")) {
					sbQuery = new StringBuilder();
					sbQuery.append("select poc from projectmntnc where pro_id = (select pro_id from projectmntnc_frequency where pro_freq_id = "+uF.parseToInt(proFreqId)+") ");

				} else if(pageFrom != null && pageFrom.trim().equals("Invoice")) {
					sbQuery = new StringBuilder();
					sbQuery.append("select poc from projectmntnc where pro_id = (select pro_id from promntc_invoice_details where promntc_invoice_id = "+uF.parseToInt(invoiceId)+") ");

				}
			}
			
			if(sbQuery != null) {
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					if(proSPOCId == null) {
						proSPOCId = new StringBuilder();
						proSPOCId.append(","+rs.getString("poc")+",");
					}
				}
				rs.close();
				pst.close();
			}
			
			if(proSPOCId == null) {
				proSPOCId = new StringBuilder();
			}
			
//			System.out.println("proTeamIds ===>> " + proTeamIds.toString());
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return proSPOCId.toString();
	}
	
	
	private String getProjectteamIdsByTaskProFreqInvoice(Connection con, UtilityFunctions uF, String pageFrom, String strAlignWith, String strAlignWithIds,
			String taskId, String proFreqId, String invoiceId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder proTeamIds = null;
		try {
			
			StringBuilder sbQuery = null;
			if(pageFrom == null || pageFrom.trim().equals("") || pageFrom.trim().equals("null")) {
				if(uF.parseToInt(strAlignWith) == PROJECT) {
					sbQuery = new StringBuilder();
					sbQuery.append("select emp_id from project_emp_details where pro_id = "+uF.parseToInt(strAlignWithIds)+" ");
				} else if(uF.parseToInt(strAlignWith) == TASK) {
					sbQuery = new StringBuilder();
					sbQuery.append("select emp_id from project_emp_details where pro_id = (select pro_id from activity_info where task_id = "+uF.parseToInt(strAlignWithIds)+") ");
				} else if(uF.parseToInt(strAlignWith) == PRO_TIMESHEET) {
					sbQuery = new StringBuilder();
					sbQuery.append("select emp_id from project_emp_details where pro_id = (select pro_id from projectmntnc_frequency where pro_freq_id = "+uF.parseToInt(strAlignWithIds)+") ");
				} else if(uF.parseToInt(strAlignWith) == INVOICE) {
					sbQuery = new StringBuilder();
					sbQuery.append("select emp_id from project_emp_details where pro_id = (select pro_id from promntc_invoice_details where promntc_invoice_id = "+uF.parseToInt(strAlignWithIds)+") ");
				} 
			} else {
				if(pageFrom != null && (pageFrom.trim().equals("Project") || pageFrom.trim().equals("VAPProject"))) {
					if(uF.parseToInt(strAlignWith) == TASK) {
						sbQuery = new StringBuilder();
						sbQuery.append("select emp_id from project_emp_details where pro_id = (select pro_id from activity_info where task_id = "+uF.parseToInt(strAlignWithIds)+") ");
					} else {
						sbQuery = new StringBuilder();
						sbQuery.append("select emp_id from project_emp_details where pro_id = "+uF.parseToInt(proId)+" ");
					}
				} else if(pageFrom != null && (pageFrom.trim().equals("Task") || pageFrom.trim().equals("VAPTask"))) {
					sbQuery = new StringBuilder();
					sbQuery.append("select emp_id from project_emp_details where pro_id = (select pro_id from activity_info where task_id = "+uF.parseToInt(taskId)+") ");
				} else if(pageFrom != null && pageFrom.trim().equals("Timesheet")) {
					sbQuery = new StringBuilder();
					sbQuery.append("select emp_id from project_emp_details where pro_id = (select pro_id from projectmntnc_frequency where pro_freq_id = "+uF.parseToInt(proFreqId)+") ");
				} else if(pageFrom != null && pageFrom.trim().equals("Invoice")) {
					sbQuery = new StringBuilder();
					sbQuery.append("select emp_id from project_emp_details where pro_id = (select pro_id from promntc_invoice_details where promntc_invoice_id = "+uF.parseToInt(invoiceId)+") ");
				}
			}
			
			if(sbQuery != null) {
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					if(proTeamIds == null) {
						proTeamIds = new StringBuilder();
						proTeamIds.append(","+rs.getString("emp_id")+",");
					} else {
						proTeamIds.append(rs.getString("emp_id")+",");
					}
				}
				rs.close();
				pst.close();
			}
			if(proTeamIds == null) {
				proTeamIds = new StringBuilder();
			}
			
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return proTeamIds.toString();
	}
	
	
	private String getResourceName(Map<String, String> hmResourceName, String resourceIds, Map<String, String> hmCustName, String spocId) {
		StringBuilder resouceName = null;
		UtilityFunctions uF = new UtilityFunctions();
		List<String> alResIds = new ArrayList<String>();
		
		if(resourceIds != null && resourceIds.length()>0) {
			int cnt = 0;
			alResIds = Arrays.asList(resourceIds.split(","));
			for(int i=0; alResIds != null && !alResIds.isEmpty() && i<alResIds.size(); i++) {
				if(uF.parseToInt(alResIds.get(i)) > 0) {
					if(resouceName == null) {
						resouceName = new StringBuilder();
						resouceName.append(hmResourceName.get(alResIds.get(i)));
					} else {
						cnt++;
//						resouceName.append(", "+hmResourceName.get(alResIds.get(i)));
					}
				}
			}
			if(cnt>0) {
				resouceName.append(" <span style=\"color: gray;\">and</span> "+cnt+" others");
			}
		}
		if(resouceName == null) {
			resouceName = new StringBuilder();
		}
		return resouceName.toString();
	}
	
	
	private void deleteFeed(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from communication_1 where communication_id=?");
			pst.setInt(1, uF.parseToInt(getPostId()));
			rs = pst.executeQuery();
			String doc_or_image = null;
			String proId = null;
			while(rs.next()) {
				doc_or_image = rs.getString("doc_or_image");
				
				if(rs.getInt("align_with") == PROJECT) {
					proId = rs.getString("align_with_id");
					
				} else if(rs.getInt("align_with") == TASK) {
					proId = CF.getProjectIdByTaskId(con, rs.getString("align_with_id"));
					
				} else if(rs.getInt("align_with") == PRO_TIMESHEET) {
					proId = CF.getProjectIdByProFreqId(con, rs.getString("align_with_id"));
					
				} else if(rs.getInt("align_with") == DOCUMENT) {
					
				} else if(rs.getInt("align_with") == INVOICE) {
					proId = CF.getProjectIdByInvoiceId(con, rs.getString("align_with_id"));
				}
			}
			rs.close();
			pst.close();
			
			
			boolean flag = false;
			
			String mainPathWithOrg = CF.getProjectDocumentFolder()+strSessionOrgId;
			String mainProPath = mainPathWithOrg;
			if(uF.parseToInt(proId) > 0) {
				mainProPath = mainPathWithOrg+"/Projects";
			} else {
				mainProPath = mainPathWithOrg+"/Uncategorised";
			}
			
			String proNameFolder = null;
			if(uF.parseToInt(proId) > 0) {
				proNameFolder = mainProPath +"/"+proId;
			} else {
				proNameFolder = mainProPath;
			}
				
				String strFilePath = null;
				if(CF.getStrDocSaveLocation()==null) {
					strFilePath = DOCUMENT_LOCATION +"/"+doc_or_image;
				} else {
					strFilePath = proNameFolder +"/"+doc_or_image;
				}
				File file = new File(strFilePath);
				file.delete();
				flag = true;
			
//			if((doc_or_image != null && !doc_or_image.equals("") && flag) || ((doc_or_image == null || doc_or_image.equals("")) && !flag)) {
				pst = con.prepareStatement("delete from communication_1 where communication_id=?");
				pst.setInt(1, uF.parseToInt(getPostId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from communication_1 where parent_id=?");
				pst.setInt(1, uF.parseToInt(getPostId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from project_document_details where feed_id=?");
				pst.setInt(1, uF.parseToInt(getPostId()));
				pst.executeUpdate();
				pst.close();
				
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getFeedData(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
//			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			
			pst = con.prepareStatement("select * from communication_1 where communication_id = ?");
			pst.setInt(1, uF.parseToInt(getPostId()));
			rs = pst.executeQuery();
//			List<String> innerList = new ArrayList<String>();
			
			String strTaggedId = null;
			String strVisibilityId = null;
			while(rs.next()) {
				if(uF.parseToInt(rs.getString("client_created_by")) > 0) {
					String createdByImage = hmCustImage.get(rs.getString("client_created_by"));
					String strClientId = CF.getClientIdBySPOCId(con, uF, rs.getString("client_created_by"));
					String addedImage = CF.getStrDocRetriveLocation()+I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
					String addedImg = "<img class=\"lazy\" src=\"userImages/avatar_photo.png\" data-original=\""+ addedImage +"\" height=\"30\" width=\"30\">";
					request.setAttribute("addedImg", addedImg);
				} else {
					String createdByImage = hmResourceImage.get(rs.getString("created_by"));
					String addedImage = CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_60x60+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
					String addedImg = "<img class='lazy' src='userImages/avatar_photo.png' data-original='" + addedImage + "' height='30' width='30'>";
					request.setAttribute("addedImg", addedImg);
				}
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					String createdByImage = hmCustImage.get(strSessionEmpId);
					String strClientId = CF.getClientIdBySPOCId(con, uF, strSessionEmpId);
					String MYLargeImage = "<img class='lazy' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
					if(CF.getStrDocRetriveLocation()==null) { 
						MYLargeImage = "<img class='lazy' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
				  	} else if(createdByImage != null && !createdByImage.equals("")) {
				  		MYLargeImage = "<img class='lazy' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_100x100+"/"+createdByImage+"\" />";
		            }
					request.setAttribute("MYLargeImg", MYLargeImage);
				} else {
					String createdByImage = hmResourceImage.get(strSessionEmpId);
					String MYLargeImage = "<img class='lazy' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
					if(CF.getStrDocRetriveLocation()==null) { 
						MYLargeImage = "<img class='lazy' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
				  	} else if(createdByImage != null && !createdByImage.equals("")) {
				  		MYLargeImage = "<img class='lazy' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_100x100+"/"+createdByImage+"\" />";
		            }
					request.setAttribute("MYLargeImg", MYLargeImage);
				}
				
//				innerList.add(rs.getString("communication_id")); //0
				setStrCommunication(rs.getString("communication")); //1
				setStrAlignWith(rs.getString("align_with")); //2
				setStrAlignWithIds(rs.getString("align_with_id")); //3
				
				if(rs.getInt("align_with") == PROJECT) {
					projectList = new FillProjectList(request).fillProjectAllDetails();
				} else if(rs.getInt("align_with") == TASK) {
					taskList = new FillTask(request).fillAllTasks();
				} else if(rs.getInt("align_with") == PRO_TIMESHEET) {
					documentList = new FillProjectDocument(request).fillProjectDocument();
				} else if(rs.getInt("align_with") == DOCUMENT) {
					projectFreqList = new FillProjectList(request).fillProjectFrequencyList(null, null, null);
				} else if(rs.getInt("align_with") == INVOICE) {
					invoiceList = new FillProjectInvoice(request).fillProjectInvoices();
				}
				strTaggedId = rs.getString("tagged_with");
//				setStrTaggedWith(rs.getString("tagged_with")); //4
				setStrVisibility(rs.getString("visibility")); //5
//				setStrVisibilityWith(rs.getString("visibility_with_id")); //6
				strVisibilityId = rs.getString("visibility_with_id");
				
				List<String> availableExt = CF.getAvailableExtention();
				request.setAttribute("availableExt", availableExt);
				
				String extenstion = null;
				if(rs.getString("doc_or_image") !=null && !rs.getString("doc_or_image").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("doc_or_image").trim());
				}
				
				String feedMainPath = getFeedMainPath(con, uF, rs.getString("communication_id"));
				
				request.setAttribute("extenstion", extenstion);
				request.setAttribute("docOrImage", (String)rs.getString("doc_or_image"));
				
				request.setAttribute("feedImagePath", feedMainPath);
				
				
			}
			
			
			StringBuilder sbAlignTypeOption = new StringBuilder();
			for(int i=0; alignTypeList!=null && i<alignTypeList.size(); i++) {
				if(uF.parseToInt(alignTypeList.get(i).getAlignTypeId()) == uF.parseToInt(getStrAlignWith())) {
					sbAlignTypeOption.append("<option value='"+alignTypeList.get(i).getAlignTypeId()+"' selected>"+alignTypeList.get(i).getAlignTypeName()+"</option>");
				} else {
					sbAlignTypeOption.append("<option value='"+alignTypeList.get(i).getAlignTypeId()+"'>"+alignTypeList.get(i).getAlignTypeName()+"</option>");
				}
			}
//			System.out.println("sbAlignTypeOption==>"+sbAlignTypeOption.toString());
			request.setAttribute("sbAlignTypeOption", sbAlignTypeOption.toString());
			
			
			List<String> alTaggedId = new ArrayList<String>();
			if(strTaggedId != null && !strTaggedId.equals("")) {
				alTaggedId = Arrays.asList(strTaggedId.split(","));
			}
			StringBuilder sbTaggedWithOption = new StringBuilder();
			for(int i=0; resourceList!=null && i<resourceList.size(); i++) {
				if(alTaggedId.contains(resourceList.get(i).getEmployeeId())) {
					sbTaggedWithOption.append("<option value='"+resourceList.get(i).getEmployeeId()+"' selected>"+resourceList.get(i).getEmployeeName()+"</option>");
				} else {
					sbTaggedWithOption.append("<option value='"+resourceList.get(i).getEmployeeId()+"'>"+resourceList.get(i).getEmployeeName()+"</option>");
				}
			}
			request.setAttribute("sbTaggedWithOption", sbTaggedWithOption.toString());
			
			
			
			List<String> alVisibilityId = new ArrayList<String>();
			if(strVisibilityId != null && !strVisibilityId.equals("")) {
				alVisibilityId = Arrays.asList(strVisibilityId.split(","));
			}
			StringBuilder sbVisibilityWithOption = new StringBuilder();
			for(int i=0; resourceList!=null && i<resourceList.size(); i++) {
				if(alVisibilityId.contains(resourceList.get(i).getEmployeeId())) {
					sbVisibilityWithOption.append("<option value='"+resourceList.get(i).getEmployeeId()+"' selected>"+resourceList.get(i).getEmployeeName()+"</option>");
				} else {
					sbVisibilityWithOption.append("<option value='"+resourceList.get(i).getEmployeeId()+"'>"+resourceList.get(i).getEmployeeName()+"</option>");
				}
			}
			request.setAttribute("sbVisibilityWithOption", sbVisibilityWithOption.toString());
			
			
			StringBuilder sbVisibilityOption = new StringBuilder();
			sbVisibilityOption.append("<option value='"+S_PUBLIC+"'");
			if(uF.parseToInt(getStrVisibility()) == S_PUBLIC) {
				sbVisibilityOption.append(" selected");
			}
			sbVisibilityOption.append(">Public</option>");
			
			if(uF.parseToInt(getStrAlignWith()) > 0 || (getPageFrom() != null && !getPageFrom().trim().equals("") && !getPageFrom().trim().equals("null"))) {
				sbVisibilityOption.append("<option value='"+S_TEAM+"'");
				if(uF.parseToInt(getStrVisibility()) == S_TEAM) {
					sbVisibilityOption.append(" selected");
				}
				sbVisibilityOption.append(">Team</option>");
			}
			
			sbVisibilityOption.append("<option value='"+S_RESOURCE+"'");
			if(uF.parseToInt(getStrVisibility()) == S_RESOURCE) {
				sbVisibilityOption.append(" selected");
			}
			sbVisibilityOption.append(">Resources</option>");
			
			if(uF.parseToInt(getStrAlignWith()) > 0 || (getPageFrom() != null && !getPageFrom().trim().equals("") && !getPageFrom().trim().equals("null"))) {
				sbVisibilityOption.append("<option value='"+S_CUSTOMER+"'");
				if(uF.parseToInt(getStrVisibility()) == S_CUSTOMER) {
					sbVisibilityOption.append(" selected");
				}
				sbVisibilityOption.append(">Customer</option>");
			}
			
			request.setAttribute("sbVisibilityOption", sbVisibilityOption.toString());
			
			
			StringBuilder sbProjectsOption = new StringBuilder();
			for(int i=0; projectList!=null && i<projectList.size(); i++) {
				if(uF.parseToInt(projectList.get(i).getProjectID()) == uF.parseToInt(getStrAlignWithIds())) {
					sbProjectsOption.append("<option value='"+projectList.get(i).getProjectID()+"' selected>"+projectList.get(i).getProjectName()+"</option>");
				} else {
					sbProjectsOption.append("<option value='"+projectList.get(i).getProjectID()+"'>"+projectList.get(i).getProjectName()+"</option>");
				}
			}
			request.setAttribute("sbProjectsOption", sbProjectsOption.toString());
			
			
			StringBuilder sbTaskOption = new StringBuilder();
			for(int i=0; taskList!=null && i<taskList.size(); i++) {
				if(uF.parseToInt(taskList.get(i).getTaskId()) == uF.parseToInt(getStrAlignWithIds())) {
					sbTaskOption.append("<option value='"+taskList.get(i).getTaskId()+"' selected>"+taskList.get(i).getTaskName()+"</option>");
				} else {
					sbTaskOption.append("<option value='"+taskList.get(i).getTaskId()+"'>"+taskList.get(i).getTaskName()+"</option>");
				}
			}
			request.setAttribute("sbTaskOption", sbTaskOption.toString());
			
			
			StringBuilder sbDocumentsOption = new StringBuilder();
			for(int i=0; documentList!=null && i<documentList.size(); i++) {
				if(uF.parseToInt(documentList.get(i).getDocumentId()) == uF.parseToInt(getStrAlignWithIds())) {
					sbDocumentsOption.append("<option value='"+documentList.get(i).getDocumentId()+"' selected>"+documentList.get(i).getDocumentName()+"</option>");
				} else {
					sbDocumentsOption.append("<option value='"+documentList.get(i).getDocumentId()+"'>"+documentList.get(i).getDocumentName()+"</option>");
				}
			}
			request.setAttribute("sbDocumentsOption", sbDocumentsOption.toString());
			
			
			StringBuilder sbProTimesheetOption = new StringBuilder();
			for(int i=0; projectFreqList!=null && i<projectFreqList.size(); i++) {
				if(uF.parseToInt(projectFreqList.get(i).getProjectID()) == uF.parseToInt(getStrAlignWithIds())) {
					sbProTimesheetOption.append("<option value='"+projectFreqList.get(i).getProjectID()+"' selected>"+projectFreqList.get(i).getProjectName()+"</option>");
				} else {
					sbProTimesheetOption.append("<option value='"+projectFreqList.get(i).getProjectID()+"'>"+projectFreqList.get(i).getProjectName()+"</option>");
				}
			}
			request.setAttribute("sbProTimesheetOption", sbProTimesheetOption.toString());
			
			
			StringBuilder sbProInvoiceOption = new StringBuilder();
			for(int i=0; invoiceList!=null && i<invoiceList.size(); i++) {
				if(uF.parseToInt(invoiceList.get(i).getInvoiceId()) == uF.parseToInt(getStrAlignWithIds())) {
					sbProInvoiceOption.append("<option value='"+invoiceList.get(i).getInvoiceId()+"' selected>"+invoiceList.get(i).getInvoiceCode()+"</option>");
				} else {
					sbProInvoiceOption.append("<option value='"+invoiceList.get(i).getInvoiceId()+"'>"+invoiceList.get(i).getInvoiceCode()+"</option>");
				}
			}
			
			rs.close();
			pst.close();
			request.setAttribute("sbProInvoiceOption", sbProInvoiceOption.toString());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String getFeedMainPath(Connection con, UtilityFunctions uF, String postId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		String feedPath = null;
		try {
			
			pst = con.prepareStatement("select * from project_document_details where feed_id = ?");
			pst.setInt(1, uF.parseToInt(postId));
			rs = pst.executeQuery();
			String docFolderId = null;
			String docMainFolderId = null;
			String docFolderName = null;
			String docMainFolderName = null;
			String proOrCat = null;
			String proId = null;
			while(rs.next()) {
				docFolderId = rs.getString("pro_folder_id");
				proOrCat = rs.getString("project_category");
				proId = rs.getString("pro_id");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(proOrCat) == 0) {
				feedPath = CF.getRetriveProjectDocumentFolder() +strSessionOrgId+"/Uncategorised";
			} else if(uF.parseToInt(proOrCat) == 1) {
				feedPath = CF.getRetriveProjectDocumentFolder() +strSessionOrgId+"/Projects";
			} else if(uF.parseToInt(proOrCat) == 2) {
				feedPath = CF.getRetriveProjectDocumentFolder() +strSessionOrgId+"/Categories";
			}
			
			String folderPath = null;
			if(uF.parseToInt(docFolderId) > 0) {
				pst = con.prepareStatement("select * from project_document_details where pro_document_id = ?");
				pst.setInt(1, uF.parseToInt(docFolderId));
				rs = pst.executeQuery();
				while(rs.next()) {
					docFolderName = rs.getString("folder_name");
					docMainFolderId = rs.getString("pro_folder_id");
					proId = rs.getString("pro_id");
				}
				rs.close();
				pst.close();
				
				folderPath = "/"+docFolderName;
			}
			
			if(uF.parseToInt(docMainFolderId) > 0) {
				pst = con.prepareStatement("select * from project_document_details where pro_document_id = ?");
				pst.setInt(1, uF.parseToInt(docMainFolderId));
				rs = pst.executeQuery();
				while(rs.next()) {
					docMainFolderName = rs.getString("folder_name");
					proId = rs.getString("pro_id");
				}
				rs.close();
				pst.close();
				
				folderPath = "/"+docMainFolderName+"/"+docFolderName;
			}
			if(uF.parseToInt(proOrCat) == 1) {
				feedPath = feedPath +"/"+proId;
			}
			if(folderPath != null && !folderPath.equals("")) {
				feedPath = feedPath + folderPath;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return feedPath;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<FillEmployee> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<FillEmployee> resourceList) {
		this.resourceList = resourceList;
	}

	public List<FillAlignedType> getAlignTypeList() {
		return alignTypeList;
	}

	public void setAlignTypeList(List<FillAlignedType> alignTypeList) {
		this.alignTypeList = alignTypeList;
	}

	public String getStrCommunication() {
		return strCommunication;
	}

	public void setStrCommunication(String strCommunication) {
		this.strCommunication = strCommunication;
	}

	public String getStrAlignWith() {
		return strAlignWith;
	}

	public void setStrAlignWith(String strAlignWith) {
		this.strAlignWith = strAlignWith;
	}

	public String getStrAlignWithIds() {
		return strAlignWithIds;
	}

	public void setStrAlignWithIds(String strAlignWithIds) {
		this.strAlignWithIds = strAlignWithIds;
	}

	public String[] getStrTaggedWith() {
		return strTaggedWith;
	}

	public void setStrTaggedWith(String[] strTaggedWith) {
		this.strTaggedWith = strTaggedWith;
	}

	public String[] getStrVisibilityWith() {
		return strVisibilityWith;
	}

	public void setStrVisibilityWith(String[] strVisibilityWith) {
		this.strVisibilityWith = strVisibilityWith;
	}

	public String getStrVisibility() {
		return strVisibility;
	}

	public void setStrVisibility(String strVisibility) {
		this.strVisibility = strVisibility;
	}

	public List<FillProjectList> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<FillProjectList> projectList) {
		this.projectList = projectList;
	}

	public List<FillProjectList> getProjectFreqList() {
		return projectFreqList;
	}

	public void setProjectFreqList(List<FillProjectList> projectFreqList) {
		this.projectFreqList = projectFreqList;
	}

	public List<FillTask> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<FillTask> taskList) {
		this.taskList = taskList;
	}

	public List<FillProjectDocument> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(List<FillProjectDocument> documentList) {
		this.documentList = documentList;
	}

	public String getStrCommunication1() {
		return strCommunication1;
	}

	public void setStrCommunication1(String strCommunication1) {
		this.strCommunication1 = strCommunication1;
	}

	public String getStrAlignWith1() {
		return strAlignWith1;
	}

	public void setStrAlignWith1(String strAlignWith1) {
		this.strAlignWith1 = strAlignWith1;
	}

	public String getStrAlignWithIds1() {
		return strAlignWithIds1;
	}

	public void setStrAlignWithIds1(String strAlignWithIds1) {
		this.strAlignWithIds1 = strAlignWithIds1;
	}

	public String getStrTaggedWith1() {
		return strTaggedWith1;
	}

	public void setStrTaggedWith1(String strTaggedWith1) {
		this.strTaggedWith1 = strTaggedWith1;
	}

	public String getStrVisibilityWith1() {
		return strVisibilityWith1;
	}

	public void setStrVisibilityWith1(String strVisibilityWith1) {
		this.strVisibilityWith1 = strVisibilityWith1;
	}

	public String getStrVisibility1() {
		return strVisibility1;
	}

	public void setStrVisibility1(String strVisibility1) {
		this.strVisibility1 = strVisibility1;
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getProFreqId() {
		return proFreqId;
	}

	public void setProFreqId(String proFreqId) {
		this.proFreqId = proFreqId;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public List<FillProjectInvoice> getInvoiceList() {
		return invoiceList;
	}

	public void setInvoiceList(List<FillProjectInvoice> invoiceList) {
		this.invoiceList = invoiceList;
	}


	public File getStrFeedDoc() {
		return strFeedDoc;
	}


	public void setStrFeedDoc(File strFeedDoc) {
		this.strFeedDoc = strFeedDoc;
	}


	public String getStrFeedDocFileName() {
		return strFeedDocFileName;
	}


	public void setStrFeedDocFileName(String strFeedDocFileName) {
		this.strFeedDocFileName = strFeedDocFileName;
	}


	public String getSubmit() {
		return submit;
	}


	public void setSubmit(String submit) {
		this.submit = submit;
	}


	public String getStrTaggedIds() {
		return strTaggedIds;
	}


	public void setStrTaggedIds(String strTaggedIds) {
		this.strTaggedIds = strTaggedIds;
	}

	
	
}