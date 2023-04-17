package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.reports.EventPopup;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class FeedsImageViewPopUp extends ActionSupport implements ServletRequestAware, IStatements{
	String strUserType=null;
	HttpSession session; 
	CommonFunctions CF;
	
	String strSessionEmpId;
	String strSessionOrgId;
	String communicationId;
	String pageFrom;
	List<FillEmployee> resourceList;
	private static Logger log = Logger.getLogger(FeedsImageViewPopUp.class);
	
	private HttpServletRequest request;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, "/jsp/task/FeedsImageViewPopUp.jsp");
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		resourceList = new FillEmployee(request).fillEmployeeName(null, null, 0, 0, session);
		getFeedsById(uF,CF);
		return SUCCESS;
	}
	
	private void getFeedsById(UtilityFunctions uF,CommonFunctions CF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);

			Map<String, String> hmProjectName = CF.getProjectNameMap(con);
			Map<String, String> hmProDocName = CF.getProDocsNameMap(con);
			Map<String, String> hmProInvoiceName = CF.getProInvoiceNameMap(con);
			List<String> innerList = new ArrayList<String>();
			Map<String, List<List<String>>> hmComments = new HashMap<String, List<List<String>>>();
			Map<String, String> hmLastCommentId = new HashMap<String, String>();
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				String createdByImage = hmCustImage.get(strSessionEmpId);
				String strClientId = CF.getClientIdBySPOCId(con, uF, strSessionEmpId);
//				String MYImage = CF.getStrDocRetriveLocation()+I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
//				String MYImg = "<img class=\"lazy\" src=\""+ MYImage +"\" data-original=\""+ MYImage +"\" height=\"25\" width=\"25\">";
				String MYLargeImage = "<img class='lazy' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
				String MYImage = "<img class='lazy verticalaligntop' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage = "<img class='lazy verticalaligntop' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					MYLargeImage = "<img class='lazy' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
			  	} else if(createdByImage != null && !createdByImage.equals("")) {
			  		MYImage = "<img class='lazy verticalaligntop' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage+"\" />";
			  		MYLargeImage = "<img class='lazy' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_100x100+"/"+createdByImage+"\" />";
	            }
				request.setAttribute("MYLargeImg", MYLargeImage);
				request.setAttribute("MYImg", MYImage);
			} else {
				String createdByImage = hmResourceImage.get(strSessionEmpId);
//				String MYImage = CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_22x22+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
//				String MYImg = "<img class=\"lazy\" src=\""+ MYImage +"\" data-original=\""+ MYImage +"\" height=\"25\" width=\"25\">";
				String MYLargeImage = "<img class='lazy' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
				String MYImage = "<img class='lazy verticalaligntop' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage = "<img class='lazy verticalaligntop' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					MYLargeImage = "<img class='lazy' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
			  	} else if(createdByImage != null && !createdByImage.equals("")) {
			  		MYImage = "<img class='lazy verticalaligntop' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_22x22+"/"+createdByImage+"\" />";
			  		MYLargeImage = "<img class='lazy' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_100x100+"/"+createdByImage+"\" />";
	            }
				request.setAttribute("MYLargeImg", MYLargeImage);
				request.setAttribute("MYImg", MYImage);
			}
			
			pst = con.prepareStatement("select * from communication_1 where communication_id = ?");
			pst.setInt(1, uF.parseToInt(getCommunicationId()));
			rs = pst.executeQuery();
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
				innerList.add(rs.getString("doc_or_image")); //3
				String extenstion = null;
				if(rs.getString("doc_or_image") !=null && !rs.getString("doc_or_image").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("doc_or_image").trim());
				}
				innerList.add(extenstion); //4
				String feedMainPath = getFeedMainPath(con, uF, rs.getString("communication_id"));
				innerList.add(feedMainPath); //5
		
				if(uF.parseToInt(rs.getString("client_created_by")) > 0) {
					String createdByImage = hmCustImage.get(rs.getString("client_created_by"));
					String strClientId = CF.getClientIdBySPOCId(con, uF, rs.getString("client_created_by"));

					String addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
					if(CF.getStrDocRetriveLocation()==null) { 
						addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
				  	} else if(createdByImage != null && !createdByImage.equals("")) {
				  		addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage+"\" />";
		            }
					innerList.add(addedImage); //6
					innerList.add(hmCustName.get(rs.getString("client_created_by"))); //7
				} else {
					String createdByImage = hmResourceImage.get(rs.getString("created_by"));
					String addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
				
					if(CF.getStrDocRetriveLocation()==null) { 
						addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
				  	} else if(createdByImage != null && !createdByImage.equals("")) {
				  		addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_60x60+"/"+createdByImage+"\" />";
		            }
//					
					innerList.add(addedImage); //6
					innerList.add(hmResourceName.get(rs.getString("created_by"))); //7
					if(rs.getString("update_time") != null) {
						innerList.add(uF.getDateFormat(rs.getString("update_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM)); //8
					} else { 
						innerList.add(uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM)); //8
					}
				}
				
				innerList.add(rs.getString("like_ids")); //9
				innerList.add(rs.getString("client_like_ids")); //10
				String  clientLikes = rs.getString("client_like_ids");
				int likes = uF.parseToInt(uF.showData(rs.getString("likes"),"0"));
				
				int clikeCount = 0;
				if(clientLikes!=null && !clientLikes.equals("") && !clientLikes.equals(",")){
					String[] clikes = clientLikes.split(",");
					int length = clikes.length;
					clikeCount = length-1;
				}
				
				
				innerList.add(String.valueOf(likes+clikeCount)); //11
			
				innerList.add(uF.showData(yourLike, "N")); //12
			
				List<List<String>> alComments = getPostCommentData(con, uF, rs.getString("communication_id"), hmLastCommentId);
				hmComments.put(rs.getString("communication_id"), alComments);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("innerList", innerList);
			request.setAttribute("hmLastCommentId", hmLastCommentId);
			request.setAttribute("hmComments", hmComments);
			StringBuilder sbTaggedWithOption = new StringBuilder();
			for(int i=0; resourceList!=null && i<resourceList.size(); i++) {
				sbTaggedWithOption.append("<option value='"+resourceList.get(i).getEmployeeId()+"'>"+resourceList.get(i).getEmployeeName()+"</option>");
			}
			request.setAttribute("sbTaggedWithOption", sbTaggedWithOption.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	public List<List<String>> getPostCommentData(Connection con, UtilityFunctions uF, String postId, Map<String, String> hmLastCommentId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alComments = new ArrayList<List<String>>();
		try {
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			
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
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("communication_id")); //0
					innerList.add(rs.getString("communication")); //1
					innerList.add(uF.showData(rs.getString("likes"), "0")); //2
					innerList.add(getResourceName(hmResourceName, rs.getString("like_ids"), hmCustName, rs.getString("client_like_ids"))); //3
					if(uF.parseToInt(rs.getString("client_created_by")) > 0) {
						String createdByImage = hmCustImage.get(rs.getString("client_created_by"));
						String strClientId = CF.getClientIdBySPOCId(con, uF, rs.getString("client_created_by"));
//						String addedImage = CF.getStrDocRetriveLocation()+I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
//						String addedImg = "<img class=\"lazy\" src=\""+ addedImage +"\" data-original=\""+ addedImage +"\" height=\"25\" width=\"25\">";
						String addedImage = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" />"; 
						if(CF.getStrDocRetriveLocation()==null) { 
							addedImage = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					  	} else if(createdByImage != null && !createdByImage.equals("")) {
					  		addedImage = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage+"\" />";
			            }
						innerList.add(addedImage); //4
						innerList.add(hmCustName.get(rs.getString("client_created_by"))); //5
					} else {
						String createdByImage = hmResourceImage.get(rs.getString("created_by"));
//						String addedImage = CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_60x60+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
//						String addedImg = "<img class='lazy' src='" + addedImage + "' data-original='" + addedImage + "' height='25' width='25'>";
						String addedImage = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" />"; 
							//CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_60x60+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
						if(CF.getStrDocRetriveLocation()==null) { 
							addedImage = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					  	} else if(createdByImage != null && !createdByImage.equals("")) {
					  		addedImage = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_22x22+"/"+createdByImage+"\" />";
			            }
						innerList.add(addedImage); //4
						innerList.add(hmResourceName.get(rs.getString("created_by"))); //5
					}
	//				System.out.println(" =====>> " + uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));
					innerList.add(uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM)); //6
					//System.out.println("java yourlike postId==>"+rs.getString("communication_id")+"==y==>"+ uF.showData(yourLike, "N"));
					innerList.add(uF.showData(yourLike, "N")); //7
					if(strUserType != null && strUserType.equals(CUSTOMER)) {
						innerList.add(rs.getString("client_created_by")); //8
					} else {
						innerList.add(rs.getString("created_by")); //8
					}
					innerList.add(getAllResourceName(hmResourceName, rs.getString("tagged_with"))); //9
					innerList.add(rs.getString("like_ids")); //10
					innerList.add(rs.getString("client_like_ids")); //11
					String  clientLikes = rs.getString("client_like_ids");
					int likes = uF.parseToInt(uF.showData(rs.getString("likes"),"0"));
					
					int clikeCount = 0;
					if(clientLikes!=null && !clientLikes.equals("") && !clientLikes.equals(",")){
						String[] clikes = clientLikes.split(",");
						int length = clikes.length;
						clikeCount = length-1;
					}
					
					//System.out.println("comment likes+clikes==>"+(likes+clikeCount));
					innerList.add(String.valueOf(likes+clikeCount)); //12
					alComments.add(innerList);
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
//			session.setAttribute(MESSAGE, ERRORM+"Error while updating "+uF.showData(strCustName, "")+". Please try again."+END);
		}
		return alComments;
	}

	private String getResourceName(Map<String, String> hmResourceName, String resourceIds, Map<String, String> hmCustName, String spocId) {
		StringBuilder resouceName = null;
		UtilityFunctions uF = new UtilityFunctions();
		List<String> alResIds = new ArrayList<String>();
		int cnt = 0;
		
		if(resourceIds != null && resourceIds.length()>0) {
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
		}
		
		if(spocId != null &&spocId.length() > 0) {
			alResIds = Arrays.asList(spocId.split(","));
			for(int i=0; alResIds != null && !alResIds.isEmpty() && i<alResIds.size(); i++) {
				if(uF.parseToInt(alResIds.get(i)) > 0) {
					if(resouceName == null) {
						resouceName = new StringBuilder();
						resouceName.append(hmCustName.get(alResIds.get(i)));
					} else {
						cnt++;
					}
				}
			}
		}
		if(cnt>0) {
			resouceName.append(" <span style=\"color: gray;\">and</span> "+cnt+" others");
		}
		if(resouceName == null) {
			resouceName = new StringBuilder();
		}
		return resouceName.toString();
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
//						cnt++;
						resouceName.append(", "+hmResourceName.get(alResIds.get(i)));
					}
				}
			}
		}
		if(resouceName == null) {
			resouceName = new StringBuilder();
		}
		return resouceName.toString();
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
	
		@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getCommunicationId() {
		return communicationId;
	}


	public void setCommunicationId(String communicationId) {
		this.communicationId = communicationId;
	}


	public List<FillEmployee> getResourceList() {
		return resourceList;
	}


	public void setResourceList(List<FillEmployee> resourceList) {
		this.resourceList = resourceList;
	}


	public String getPageFrom() {
		return pageFrom;
	}


	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	
	
	
}
