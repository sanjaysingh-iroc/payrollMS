package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectDocumentFact extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	CommonFunctions CF; 
	HttpSession session1;
	String strSessionEmpId;
	String strOrgId;
	
	String clientId;
	String proId;
	String folderName;
	String proFolderId;
	String type;
	String filePath;
	String fileDir;
	
	List<String> taggedRes = new ArrayList<String>();
	
	List<FillEmployee> resourceList;

	public String execute() throws Exception {
		session1 = request.getSession();
		strSessionEmpId = (String)session1.getAttribute(EMPID);
		strOrgId = (String)session1.getAttribute(ORGID);
		CF = (CommonFunctions) session1.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/task/ProjectDocumentFact.jsp");
		request.setAttribute(TITLE, "View "+folderName+" file"); 

		resourceList = new FillEmployee(request).fillEmployeeName(null, null, 0, 0, session1);
		
		UtilityFunctions uF = new UtilityFunctions();
		getFolderAndDocuments(uF);
		
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
			
			Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, getProId());
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			
			Map<String, String> hmFileIcon = CF.getFileIcon();
			request.setAttribute("hmFileIcon",hmFileIcon);
			
			pst = con.prepareStatement("select * from project_document_details where pro_document_id=?");
			pst.setInt(1, uF.parseToInt(getProFolderId()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmProDocumentDetails = new HashMap<String, String>();
			while (rs.next()) {
				hmProDocumentDetails.put("PRO_DOCUMENT_ID", rs.getString("pro_document_id"));
				hmProDocumentDetails.put("CLIENT_ID", rs.getString("client_id"));
				hmProDocumentDetails.put("PRO_ID", rs.getString("pro_id"));
				hmProDocumentDetails.put("FOLDER_NAME", rs.getString("folder_name"));
				hmProDocumentDetails.put("DOCUMENT_NAME", rs.getString("document_name"));
				hmProDocumentDetails.put("ADDED_BY", uF.showData(hmEmpCodeName.get(rs.getString("added_by")), "-") );
				hmProDocumentDetails.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat()));
				hmProDocumentDetails.put("ENTRY_TIME", uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()));
				hmProDocumentDetails.put("FOLDER_FILE_TYPE", rs.getString("folder_file_type"));
				hmProDocumentDetails.put("PRO_FOLDER_ID", rs.getString("pro_folder_id"));
				hmProDocumentDetails.put("FILE_SIZE", rs.getString("file_size"));
				hmProDocumentDetails.put("FILE_TYPE", rs.getString("file_type"));
				hmProDocumentDetails.put("ALIGN_WITH", rs.getString("align_with"));
				hmProDocumentDetails.put("SHARING_TYPE", rs.getString("sharing_type"));
				hmProDocumentDetails.put("SIZE_IN_BYTES", rs.getString("size_in_bytes"));
				hmProDocumentDetails.put("PROJECT_CATEGORY", rs.getString("project_category"));
				hmProDocumentDetails.put("DOC_PARENT_ID", rs.getString("doc_parent_id"));
				hmProDocumentDetails.put("SCOPE_DOCUMENT", rs.getString("scope_document"));
				hmProDocumentDetails.put("DESCRIPTION", rs.getString("description"));
				hmProDocumentDetails.put("FEED_ID", rs.getString("feed_id"));
				
				if(uF.parseToInt(rs.getString("project_category")) == 1){
					hmProDocumentDetails.put("CATEGORY", "Project");
					String strAlign = null;
					if(uF.parseToInt(rs.getString("align_with")) > 0) {
						strAlign = CF.getProjectTaskNameByTaskId(con, uF, rs.getString("align_with"));
					} else {
						strAlign = "Full Project";
					}
					hmProDocumentDetails.put("ALIGN", uF.showData(strAlign, "-"));
				} else if(uF.parseToInt(rs.getString("project_category")) == 2){
					hmProDocumentDetails.put("CATEGORY", "Other");
					String strOther = null;
					if(uF.parseToInt(rs.getString("align_with")) > 0) {
						strOther = CF.getProjectCategory(con, uF, rs.getString("align_with"));
					}
					hmProDocumentDetails.put("ALIGN", uF.showData(strOther, "-"));
				} else {
					hmProDocumentDetails.put("CATEGORY", "-");
					hmProDocumentDetails.put("ALIGN", "-");
				}
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmProDocumentDetails.put("SHARING_RESOURCES", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmProDocumentDetails.put("SHARING_RESOURCES", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmProDocumentDetails.put("DOC_VERSION", rs.getString("doc_version"));
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmProDocumentDetails.put("FILE_EXTENSION", extenstion);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProDocumentDetails",hmProDocumentDetails);
			
			
			pst = con.prepareStatement("select * from (select * from project_document_details where doc_parent_id in (select doc_parent_id " +
					"from project_document_details where pro_document_id=? and doc_parent_id >0)" +
					"union select * from project_document_details where pro_document_id in (select doc_parent_id from project_document_details where pro_document_id=? and doc_parent_id >0)" +
					"union select * from project_document_details where pro_document_id in (select pro_document_id from project_document_details where doc_parent_id=? and doc_parent_id >0)" +
					"union select * from project_document_details where pro_document_id =? and doc_parent_id =0) a order by pro_document_id desc");
			pst.setInt(1, uF.parseToInt(getProFolderId()));
			pst.setInt(2, uF.parseToInt(getProFolderId()));
			pst.setInt(3, uF.parseToInt(getProFolderId()));
			pst.setInt(4, uF.parseToInt(getProFolderId()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alVersion = new ArrayList<Map<String,String>>();
			String feedId = null;
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
				hmVersionHistory.put("SHARING_TYPE", rs.getString("sharing_type"));
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
				
				if(uF.parseToInt(rs.getString("sharing_type")) == 2) {
					hmVersionHistory.put("SHARING_RESOURCES", uF.showData(CF.getResourcesName(con, rs.getString("sharing_resources"), hmTeamLead), "-"));
				} else {
					hmVersionHistory.put("SHARING_RESOURCES", uF.showData(uF.getSharingType(rs.getString("sharing_type")), "-"));
				}
				hmVersionHistory.put("DOC_VERSION", rs.getString("doc_version"));
				
				String extenstion = null;
				if(rs.getString("document_name") !=null && !rs.getString("document_name").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("document_name").trim());
				}
				hmVersionHistory.put("FILE_EXTENSION", extenstion);
				
				feedId = rs.getString("feed_id");
				
				alVersion.add(hmVersionHistory);
			}
			rs.close();
			pst.close();
			request.setAttribute("alVersion",alVersion);
			
			
			//tiff|pdf|ppt|pptx|pps|doc|docx|txt|xls|xlsx
			List<String> availableExt = CF.getAvailableExtention();
			request.setAttribute("availableExt",availableExt);
			
			getPostData(con, uF, feedId);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getPostData(Connection con, UtilityFunctions uF, String feedId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			
			Map<String, List<List<String>>> hmComments = new HashMap<String, List<List<String>>>();
			Map<String, String> hmLastCommentId = new HashMap<String, String>();
			
			String MYImg = "<img class=\"lazy\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_22x22+"/"+hmResourceImage.get(strSessionEmpId)+"\" height=\"25\" width=\"25\">";
			request.setAttribute("MYImg", MYImg);
			
//			Map<String, List<String>> hmFeeds = new LinkedHashMap<String, List<String>>();
			
			Map<String, String> hmProjectName = CF.getProjectNameMap(con);
			Map<String, String> hmProDocName = CF.getProDocsNameMap(con);
			
			pst = con.prepareStatement("select * from communication_1 where communication_id = ?");
			pst.setInt(1, uF.parseToInt(feedId));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			List<String> innerList = new ArrayList<String>();
			while(rs.next()) {
				List<String> alLikeIds = new ArrayList<String>();
				String yourLike = null;
				if(rs.getString("like_ids") != null && !rs.getString("like_ids").equals("")) {
					alLikeIds = Arrays.asList(rs.getString("like_ids").split(","));
					if(alLikeIds.contains(strSessionEmpId)) {
						yourLike = "Y";
					}
				}
				
				List<String> empvalue1 = new ArrayList<String>();
				if (rs.getString("tagged_with") == null) {
					empvalue1.add("0");
				} else {
				empvalue1 = Arrays.asList(rs.getString("tagged_with").split(","));
				}				
				if (empvalue1 != null) {
					for (int i = 1; i < empvalue1.size(); i++) {
						taggedRes.add(empvalue1.get(i).trim());
					}
				} else {
					taggedRes.add("0");
				}
				
				String createdByImage = hmResourceImage.get(rs.getString("created_by"));
				String addedImg = "<img class='lazy' src='userImages/avatar_photo.png' data-original='"+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_60x60+"/"+createdByImage+"' height='30' width='30'>";
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
				} else {
					innerList.add(""); //3
				}
				innerList.add(getResourceName(hmResourceName, rs.getString("tagged_with"))); //4
				innerList.add(uF.showData(uF.getPostSharedWith(rs.getInt("visibility")), "")); //5
				innerList.add(getResourceName(hmResourceName, rs.getString("visibility_with_id"))); //6
				innerList.add(uF.showData(rs.getString("likes"), "0")); //7
				innerList.add(getResourceName(hmResourceName, rs.getString("like_ids"))); //8
				innerList.add(addedImg); //9
				innerList.add(hmResourceName.get(rs.getString("created_by"))); //10
//				System.out.println(" =====>> " + uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));
				if(rs.getString("update_time") != null) {
					innerList.add(uF.getDateFormat(rs.getString("update_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM)); //11
				} else { 
					innerList.add(uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM)); //11
				}
				innerList.add(uF.showData(yourLike, "N")); //12
				innerList.add(rs.getString("created_by")); //13
				innerList.add(rs.getString("doc_or_image")); //14
				String extenstion = null;
				if(rs.getString("doc_or_image") !=null && !rs.getString("doc_or_image").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("doc_or_image").trim());
				}
				innerList.add(extenstion); //15
				String strTaggedResources = getAllResourceName(hmResourceName, rs.getString("tagged_with"));
				innerList.add(strTaggedResources); //16
				innerList.add(rs.getString("like_ids")); //17
				innerList.add(rs.getString("client_like_ids")); //18
//				hmFeeds.put(rs.getString("communication_id"), innerList);
				
				List<List<String>> alComments = getPostCommentData(con, uF, rs.getString("communication_id"), hmLastCommentId);
				hmComments.put(rs.getString("communication_id"), alComments);
			}
			rs.close();
			pst.close();
			
//			System.out.println("innerList ===>> " + innerList);
			request.setAttribute("innerList", innerList);
			
			request.setAttribute("hmLastCommentId", hmLastCommentId);
			request.setAttribute("hmComments", hmComments);
			
//			session.setAttribute(MESSAGE, SUCCESSM+"Disable access for "+uF.showData(strCustName, "")+" successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
//			session.setAttribute(MESSAGE, ERRORM+"Error while updating "+uF.showData(strCustName, "")+". Please try again."+END);
		}
	}


	private List<List<String>> getPostCommentData(Connection con, UtilityFunctions uF, String postId, Map<String, String> hmLastCommentId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alComments = new ArrayList<List<String>>();
		try {
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			
			pst = con.prepareStatement("select communication_id from communication_1 where parent_id = ? order by communication_id desc");
			pst.setInt(1, uF.parseToInt(postId));
			rs = pst.executeQuery();
			StringBuilder sbcommentIds = null;
			int allCommentCnt = 0;
			int remainCommentCnt = 0;
			String lastCommentId = null;
			while(rs.next()) {
				if(allCommentCnt < 5) {
					if(sbcommentIds == null) {
						sbcommentIds = new StringBuilder();
						sbcommentIds.append(rs.getString("communication_id"));
						allCommentCnt++;
					} else {
						sbcommentIds.append(","+rs.getString("communication_id"));
						allCommentCnt++;
						if(allCommentCnt == 5) {
							lastCommentId = rs.getString("communication_id");
							
						}
					}
				} else {
					remainCommentCnt++;
				}
//				allCommentCnt = rs.getInt("count");
			}
			rs.close();
			pst.close();
			
			hmLastCommentId.put(postId+"_LAST_COMEENT_ID", lastCommentId);
			hmLastCommentId.put(postId+"_REMAIN_COMEENT_COUNT", remainCommentCnt+"");
			
//			allCommentCnt = allCommentCnt - 5;
			
			if(sbcommentIds != null) {
				//System.out.println("sbcommentIds ===>> " + sbcommentIds.toString());
				pst = con.prepareStatement("select * from communication_1 where parent_id = ? and communication_id in ("+sbcommentIds.toString()+") order by communication_id");
				pst.setInt(1, uF.parseToInt(postId));
				//System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					List<String> alLikeIds = new ArrayList<String>();
					String yourLike = null;
					if(rs.getString("like_ids") != null && !rs.getString("like_ids").equals("")) {
						alLikeIds = Arrays.asList(rs.getString("like_ids").split(","));
						if(alLikeIds.contains(strSessionEmpId)) {
							yourLike = "Y";
						}
					}
					String createdByImage = hmResourceImage.get(rs.getString("created_by"));
					String addedImg = "<img class=\"lazy\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_22x22+"/"+createdByImage+"\" height=\"25\" width=\"25\">";
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("communication_id")); //0
					innerList.add(rs.getString("communication")); //1
					innerList.add(uF.showData(rs.getString("likes"), "0")); //2
					innerList.add(getResourceName(hmResourceName, rs.getString("like_ids"))); //3
					innerList.add(addedImg); //4
					innerList.add(hmResourceName.get(rs.getString("created_by"))); //5
	//				System.out.println(" =====>> " + uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));
					innerList.add(uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM)); //6
					innerList.add(uF.showData(yourLike, "N")); //7
					innerList.add(rs.getString("created_by")); //8
					innerList.add(rs.getString("like_ids")); //9
					innerList.add(rs.getString("client_like_ids")); //10
					
					alComments.add(innerList);
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
//			session.setAttribute(MESSAGE, ERRORM+"Error while updating "+uF.showData(strCustName, "")+". Please try again."+END);
		}finally {
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
		return alComments;
	}


	private String getAllResourceName(Map<String, String> hmResourceName, String resourceIds) {
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
						resouceName.append(","+hmResourceName.get(alResIds.get(i)));
					}
				}
			}
//			if(cnt>0) {
//				resouceName.append(" <span style=\"color: gray;\">and</span> "+cnt+" others");
//			}
		}
		if(resouceName == null) {
			resouceName = new StringBuilder();
		}
		return resouceName.toString();
	}
	
	private String getResourceName(Map<String, String> hmResourceName, String resourceIds) {
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

	public List<FillEmployee> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<FillEmployee> resourceList) {
		this.resourceList = resourceList;
	}

	public List<String> getTaggedRes() {
		return taggedRes;
	}

	public void setTaggedRes(List<String> taggedRes) {
		this.taggedRes = taggedRes;
	}

	
	
}
