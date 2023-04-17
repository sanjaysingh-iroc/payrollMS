package com.konnect.jpms.task;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

public class FeedsByAjax extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	
	String operation;
	String btnPost;
	
	List<FillEmployee> resourceList;
	List<FillAlignedType> alignTypeList;
	
	
	String strCommunication;
	String strAlignWith;
	String strAlignWithIds;
	String[] strTaggedWith;
	String[] strVisibilityWith;
	String strVisibility;
	
	File strFeedDoc;
	String strFeedDocFileName;
	
	String pageFrom;
	String proId;
	String pageType;
	
	String taskId;
	String proFreqId;
	String invoiceId;
	
	String offsetCnt;
	String lastComunicationId;
	String remainFeeds;
	
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
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getRetriveProjectDocumentFolder());
		
		UtilityFunctions uF = new UtilityFunctions();
		
//		System.out.println("getPageType() ===>> " + getPageType() + " -- getPageFrom() ===>> " + getPageFrom() + " -- getBtnPost() ===>> "  +getBtnPost());
		if(getPageFrom()!=null && (getPageFrom().trim().equalsIgnoreCase("Project") || getPageFrom().trim().equalsIgnoreCase("VAPProject") || getPageFrom().trim().equalsIgnoreCase("Task") || getPageFrom().trim().equalsIgnoreCase("Timesheet") || 
			getPageFrom().trim().equalsIgnoreCase("Invoice") || getPageFrom().trim().equalsIgnoreCase("VAPTask"))) {
			String strProEmpId = getProEmpIds(uF);
			resourceList = new FillEmployee(request).fillProjectEmployeeName(strProEmpId);
			
		} else if(strUserType != null && strUserType.equals(CUSTOMER)) {
			String strCustProEmpId = getCustomerProEmpIds(uF);
			resourceList = new FillEmployee(request).fillProjectEmployeeName(strCustProEmpId);
			
		} else {
			resourceList = new FillEmployee(request).fillEmployeeName(null, null, session);
			
		}
		alignTypeList = new FillAlignedType(request).fillAlignedTypeList(getPageFrom());
		
		getAllPosts(uF);
		return LOAD;
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
			/*pst = con.prepareStatement("select project_owner,added_by from projectmntnc where pro_id = ?");
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
			
			pst = con.prepareStatement("select project_owners,added_by from projectmntnc where pro_id = ?");
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
			
			StringBuilder sbProIds = null;
			pst = con.prepareStatement("select pro_id from projectmntnc where poc = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while(rs.next()) {
				if(sbProIds == null) {
					sbProIds = new StringBuilder();
					sbProIds.append(rs.getString("pro_id"));
				} else {
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
			
			//===start parvez date: 13-10-2022===	
				/*pst = con.prepareStatement("select project_owner,added_by from projectmntnc where pro_id in ("+sbProIds.toString()+")");
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
	
	
	private void getAllPosts(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			Map<String, String> hmResourceImage = CF.getEmpProfileImage(con);
			Map<String, String> hmCustImage = CF.getCustomerImageMap(con);
			
			Map<String, List<List<String>>> hmComments = new HashMap<String, List<List<String>>>();
			Map<String, String> hmLastCommentId = new HashMap<String, String>();
			
			if(strUserType != null && strUserType.equals(CUSTOMER)) {
				String createdByImage = hmCustImage.get(strSessionEmpId);
				String strClientId = CF.getClientIdBySPOCId(con, uF, strSessionEmpId);
//				String MYImage = CF.getStrDocRetriveLocation()+I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
//				String MYImg = "<img class=\"lazy\" src=\""+ MYImage +"\" data-original=\""+ MYImage +"\" height=\"25\" width=\"25\">";
				String MYImage = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
			  	} else if(createdByImage != null && !createdByImage.equals("")) {
			  		MYImage = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage+"\" />";
	            }
				request.setAttribute("MYImg", MYImage);
			} else {
				String createdByImage = hmResourceImage.get(strSessionEmpId);
//				String MYImage = CF.getStrDocRetriveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_22x22+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
//				String MYImg = "<img class=\"lazy\" src=\""+ MYImage +"\" data-original=\""+ MYImage +"\" height=\"25\" width=\"25\">";
				String MYImage = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
			  	} else if(createdByImage != null && !createdByImage.equals("")) {
			  		MYImage = "<img class='lazy' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_22x22+"/"+createdByImage+"\" />";
	            }
				request.setAttribute("MYImg", MYImage);
			}
			Map<String, List<String>> hmFeeds = new LinkedHashMap<String, List<String>>();
			
			Map<String, String> hmProjectName = CF.getProjectNameMap(con);
			Map<String, String> hmProDocName = CF.getProDocsNameMap(con);
			Map<String, String> hmProInvoiceName = CF.getProInvoiceNameMap(con);
			
			StringBuilder proTaskIds = null;
			pst = con.prepareStatement("select task_id from activity_info where pro_id = ?");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				if(proTaskIds == null) {
					proTaskIds = new StringBuilder();
					proTaskIds.append(rs.getString("task_id"));
				} else {
					proTaskIds.append(","+rs.getString("task_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(proTaskIds == null) {
				proTaskIds = new StringBuilder("0");
			}
			
			StringBuilder proDocIds = null;
			pst = con.prepareStatement("select pro_document_id from project_document_details where pro_id = ?");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				if(proDocIds == null) {
					proDocIds = new StringBuilder();
					proDocIds.append(rs.getString("pro_document_id"));
				} else {
					proDocIds.append(","+rs.getString("pro_document_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(proDocIds == null) {
				proDocIds = new StringBuilder("0");
			}
			
			Map<String, String> hmFeedIds = new LinkedHashMap<String, String>();
			if(getPageFrom()!=null && (getPageFrom().trim().equalsIgnoreCase("Project") || getPageFrom().trim().equalsIgnoreCase("VAPProject"))) {
				pst = con.prepareStatement("select * from communication_1 where communication_id < "+uF.parseToInt(getLastComunicationId())+" and " +
					" ((align_with = "+PROJECT+" and align_with_id= "+uF.parseToInt(getProId())+") or (align_with = "+TASK+" and align_with_id in " +
					" ("+proTaskIds.toString()+")) or (align_with = "+PRO_TIMESHEET+" and align_with_id in  ("+proDocIds.toString()+"))) and " +
					" communication_id not in (select parent_id from communication_1 where communication_id >= "+uF.parseToInt(getLastComunicationId())+" " +
					" and ((align_with = "+PROJECT+" and align_with_id= "+uF.parseToInt(getProId())+") or (align_with = "+TASK+" and align_with_id in " +
					" ("+proTaskIds.toString()+")) or (align_with = "+PRO_TIMESHEET+" and align_with_id in  ("+proDocIds.toString()+")))) order by communication_id desc ");
			} else if(getPageFrom()!=null && (getPageFrom().trim().equalsIgnoreCase("Task") || getPageFrom().trim().equalsIgnoreCase("VAPTask"))) {
				pst = con.prepareStatement("select * from communication_1 where communication_id < "+uF.parseToInt(getLastComunicationId())+" and " +
					" align_with = "+TASK+" and align_with_id = "+uF.parseToInt(getTaskId())+" and communication_id not in (select parent_id from " +
					" communication_1 where communication_id >= "+uF.parseToInt(getLastComunicationId())+" and align_with = "+TASK+" and " +
					" align_with_id = "+uF.parseToInt(getTaskId())+") order by communication_id desc ");
			} else if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Timesheet")) {
				pst = con.prepareStatement("select * from communication_1 where communication_id < "+uF.parseToInt(getLastComunicationId())+" and " +
					" align_with = "+PRO_TIMESHEET+" and align_with_id = "+uF.parseToInt(getProFreqId())+" and communication_id not in (select " +
					" parent_id from communication_1 where communication_id >= "+uF.parseToInt(getLastComunicationId())+" and align_with = "+PRO_TIMESHEET+" " +
					" and align_with_id = "+uF.parseToInt(getProFreqId())+") order by communication_id desc ");
			} else if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Invoice")) {
				pst = con.prepareStatement("select * from communication_1 where communication_id < "+uF.parseToInt(getLastComunicationId())+" and " +
					" align_with = "+INVOICE+" and align_with_id = "+uF.parseToInt(getInvoiceId())+" and communication_id not in (select " +
					" parent_id from communication_1 where communication_id >= "+uF.parseToInt(getLastComunicationId())+" and align_with = "+INVOICE+" " +
					" and align_with_id = "+uF.parseToInt(getInvoiceId())+") order by communication_id desc ");
			} else {
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					pst = con.prepareStatement("select * from communication_1 where ((parent_id = 0 and (client_visibility_with_id like '%,"+strSessionEmpId+",%' " +
						" or client_tagged_with like '%,"+strSessionEmpId+",%' or client_created_by = "+uF.parseToInt(strSessionEmpId)+")) or communication_id in " +
						"(select parent_id from communication_1 where parent_id > 0 and client_tagged_with like '%,"+strSessionEmpId+",%')) and communication_id < " +
						" "+uF.parseToInt(getLastComunicationId())+" and communication_id not in (select parent_id from communication_1 where " +
						" communication_id >= "+uF.parseToInt(getLastComunicationId())+") order by communication_id desc ");
					
				} else {
					pst = con.prepareStatement("select * from communication_1 where ((parent_id = 0 and (visibility = 0 or visibility_with_id like '%,"+strSessionEmpId+",%' " +
						" or tagged_with like '%,"+strSessionEmpId+",%' or created_by = "+uF.parseToInt(strSessionEmpId)+")) or communication_id in " +
						" (select parent_id from communication_1 where parent_id > 0 and tagged_with like '%,"+strSessionEmpId+",%')) and communication_id < " +
						" "+uF.parseToInt(getLastComunicationId())+" and communication_id not in (select parent_id from communication_1 where " +
						" communication_id >= "+uF.parseToInt(getLastComunicationId())+") order by communication_id desc ");
					
				}
//				pst = con.prepareStatement("select * from communication_1 where communication_id < "+uF.parseToInt(getLastComunicationId())+" and communication_id " +
//					" not in (select parent_id from communication_1 where communication_id >= "+uF.parseToInt(getLastComunicationId())+") order by communication_id desc "); // limit 10 offset "+uF.parseToInt(getOffsetCnt())+" 
			}
//			System.out.println("pst ========>>> " + pst);
			rs = pst.executeQuery();
			StringBuilder sbCommunicationIds = null;
			List<String> alFeedIds = new ArrayList<String>();
			setRemainFeeds("NO");
			while(rs.next()) {
				if(alFeedIds.size() == 10) {
					setRemainFeeds("YES");
					break;
				}
				if(uF.parseToInt(rs.getString("parent_id")) > 0 && !alFeedIds.contains(rs.getString("parent_id"))) {
					alFeedIds.add(rs.getString("parent_id"));
					hmFeedIds.put(rs.getString("parent_id"), rs.getString("parent_id"));
					if(sbCommunicationIds == null) {
						sbCommunicationIds = new StringBuilder();
						sbCommunicationIds.append(rs.getString("parent_id"));
					} else {
						sbCommunicationIds.append(","+rs.getString("parent_id"));
					}
					
				} else if (uF.parseToInt(rs.getString("parent_id")) ==0 && !alFeedIds.contains(rs.getString("communication_id"))) {
					alFeedIds.add(rs.getString("communication_id"));
					hmFeedIds.put(rs.getString("communication_id"), rs.getString("communication_id"));
					if(sbCommunicationIds == null) {
						sbCommunicationIds = new StringBuilder();
						sbCommunicationIds.append(rs.getString("communication_id"));
					} else {
						sbCommunicationIds.append(","+rs.getString("communication_id"));
					}
				}
				setLastComunicationId(rs.getString("communication_id"));
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmFeedIds", hmFeedIds);
//			System.out.println("sbCommunicationIds ===>> " + sbCommunicationIds);
			
			if(sbCommunicationIds != null) {
				StringBuilder sbQuery = null;
//				if(getPageFrom()!=null && (getPageFrom().trim().equalsIgnoreCase("Project") || getPageFrom().trim().equalsIgnoreCase("VAPProject"))) {
//					sbQuery = new StringBuilder();
//					sbQuery.append("select * from communication_1 where parent_id = 0 and communication_id in ("+sbCommunicationIds.toString()+") and ((align_with = "+PROJECT+" and align_with_id= "+uF.parseToInt(getProId())+") " +
//						"or (align_with = "+TASK+" and align_with_id in ("+proTaskIds.toString()+")) or (align_with = "+PRO_TIMESHEET+" and align_with_id in ("+proDocIds.toString()+"))) order by communication_id desc");
//					
//				} else if(getPageFrom()!=null && (getPageFrom().trim().equalsIgnoreCase("Task") || getPageFrom().trim().equalsIgnoreCase("VAPTask"))) {
//					sbQuery = new StringBuilder();
//					sbQuery.append("select * from communication_1 where parent_id = 0 and communication_id in ("+sbCommunicationIds.toString()+") and align_with = "+TASK+" and align_with_id = "+uF.parseToInt(getTaskId())+" " +
//						" order by communication_id desc ");
//					
//				} else if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Timesheet")) {
//					sbQuery = new StringBuilder();
//					sbQuery.append("select * from communication_1 where parent_id = 0 and communication_id in ("+sbCommunicationIds.toString()+") and align_with = "+PRO_TIMESHEET+" and align_with_id = "+uF.parseToInt(getProFreqId())+" " +
//						" order by communication_id desc ");
//					
//				} else if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Invoice")) {
//					sbQuery = new StringBuilder();
//					sbQuery.append("select * from communication_1 where parent_id = 0 and communication_id in ("+sbCommunicationIds.toString()+") and align_with = "+INVOICE+" and align_with_id = "+uF.parseToInt(getInvoiceId())+" " +
//						" order by communication_id desc ");
//					
//				} else {
//					if(strUserType != null && strUserType.equals(CUSTOMER)) {
//						sbQuery = new StringBuilder();
//						sbQuery.append("select * from communication_1 where (parent_id = 0 and communication_id in ("+sbCommunicationIds.toString()+") and (client_visibility_with_id " +
//							"like '%,"+strSessionEmpId+",%' or client_tagged_with like '%,"+strSessionEmpId+",%' or client_created_by = "+uF.parseToInt(strSessionEmpId)+") ) or communication_id in " +
//							"(select parent_id from communication_1 where parent_id > 0 and client_tagged_with like '%,"+strSessionEmpId+",%') order by communication_id desc ");
//						
//					} else {
//						sbQuery = new StringBuilder();
//						sbQuery.append("select * from communication_1 where (parent_id = 0 and communication_id in ("+sbCommunicationIds.toString()+") and (visibility = 0 or visibility_with_id " +
//							"like '%,"+strSessionEmpId+",%' or tagged_with like '%,"+strSessionEmpId+",%' or created_by = "+uF.parseToInt(strSessionEmpId)+")) or communication_id in " +
//							"(select parent_id from communication_1 where parent_id > 0 and tagged_with like '%,"+strSessionEmpId+",%') order by communication_id desc ");
//						
//					}
//				}
				sbQuery = new StringBuilder();
				sbQuery.append("select * from communication_1 where parent_id = 0 and communication_id in ("+sbCommunicationIds.toString()+") order by communication_id desc ");
				if(sbQuery != null) {
					pst = con.prepareStatement(sbQuery.toString());
//					System.out.println("pst ===>> " + pst);
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
		//					String addedImage = CF.getStrDocRetriveLocation()+I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
		//					String addedImg = "<img class=\"lazy\" src=\""+ addedImage +"\" data-original=\""+ addedImage +"\" height=\"30\" width=\"30\">";
							String addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
							if(CF.getStrDocRetriveLocation()==null) { 
								addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
						  	} else if(createdByImage != null && !createdByImage.equals("")) {
						  		addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage+"\" />";
				            }
							innerList.add(addedImage); //9
							innerList.add(hmCustName.get(rs.getString("client_created_by"))); //10
						} else {
							String createdByImage = hmResourceImage.get(rs.getString("created_by"));
							String addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
								//CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_60x60+"/"+((createdByImage!=null && !createdByImage.equals("")) ? createdByImage : "avatar_photo.png");
							if(CF.getStrDocRetriveLocation()==null) { 
								addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
						  	} else if(createdByImage != null && !createdByImage.equals("")) {
						  		addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_60x60+"/"+createdByImage+"\" />";
				            }
		//					String addedImg = "<img class='lazy' src='" + addedImage + "' data-original='" + addedImage + "' height='30' width='30'>";
							innerList.add(addedImage); //9
							innerList.add(hmResourceName.get(rs.getString("created_by"))); //10
						}
		//				System.out.println(" =====>> " + uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));
						if(rs.getString("update_time") != null) {
							innerList.add(uF.getDateFormat(rs.getString("update_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM)); //11
						} else { 
							innerList.add(uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM)); //11
						}
						innerList.add(uF.showData(yourLike, "N")); //12
						if(strUserType != null && strUserType.equals(CUSTOMER)) {
							innerList.add(rs.getString("client_created_by")); //13
						} else {
							innerList.add(rs.getString("created_by")); //13
						}
						innerList.add(rs.getString("doc_or_image")); //14
						String extenstion = null;
						if(rs.getString("doc_or_image") !=null && !rs.getString("doc_or_image").trim().equals("")){
							extenstion = FilenameUtils.getExtension(rs.getString("doc_or_image").trim());
						}
						innerList.add(extenstion); //15
		//				String proId = null;
		//				if(rs.getInt("align_with") == 1) {
		//					proId = rs.getString("align_with_id");
		//					
		//				} else if(rs.getInt("align_with") == 2) {
		//					proId = CF.getProjectIdByTaskId(con, rs.getString("align_with_id"));
		//					
		//				} else if(rs.getInt("align_with") == 3) {
		//					proId = CF.getProjectIdByProFreqId(con, rs.getString("align_with_id"));
		//					
		//				} else if(rs.getInt("align_with") == 4) {
		//					
		//				}
		//				innerList.add(proId); //16
						String feedMainPath = getFeedMainPath(con, uF, rs.getString("communication_id"));
						innerList.add(feedMainPath); //16
						innerList.add(rs.getString("feed_type")); //17
						innerList.add(rs.getString("like_ids")); //18
						innerList.add(rs.getString("client_like_ids")); //19
						String  clientLikes = rs.getString("client_like_ids");
							
						int likes = uF.parseToInt(uF.showData(rs.getString("likes"),"0"));
						
						int clikeCount = 0;
						if(clientLikes!=null && !clientLikes.equals("") && !clientLikes.equals(",")){
							String[] clikes = clientLikes.split(",");
							int length = clikes.length;
							clikeCount = length-1;
						}
						
					//	System.out.println("Feeds likes+clikes==>"+(likes+clikeCount));
						innerList.add(String.valueOf(likes+clikeCount)); //20
						hmFeeds.put(rs.getString("communication_id"), innerList);
						
						List<List<String>> alComments = getPostCommentData(con, uF, rs.getString("communication_id"), hmLastCommentId);
						hmComments.put(rs.getString("communication_id"), alComments);
					}
					rs.close();
					pst.close();
				}
			}
			
			List<String> availableExt = CF.getAvailableExtention();
			request.setAttribute("availableExt", availableExt);
			
//			System.out.println("hmFeeds ===>> " + hmFeeds);
			
			request.setAttribute("hmFeeds", hmFeeds);
			
			request.setAttribute("hmLastCommentId", hmLastCommentId);
			request.setAttribute("hmComments", hmComments);
			
			StringBuilder sbTaggedWithOption = new StringBuilder();
			for(int i=0; resourceList!=null && i<resourceList.size(); i++) {
				sbTaggedWithOption.append("<option value='"+resourceList.get(i).getEmployeeId()+"'>"+resourceList.get(i).getEmployeeName()+"</option>");
			}
			request.setAttribute("sbTaggedWithOption", sbTaggedWithOption.toString());
			
//			session.setAttribute(MESSAGE, SUCCESSM+"Disable access for "+uF.showData(strCustName, "")+" successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
//			session.setAttribute(MESSAGE, ERRORM+"Error while updating "+uF.showData(strCustName, "")+". Please try again."+END);
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


	private List<List<String>> getPostCommentData(Connection con, UtilityFunctions uF, String postId, Map<String, String> hmLastCommentId) {

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
	

	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getBtnPost() {
		return btnPost;
	}

	public void setBtnPost(String btnPost) {
		this.btnPost = btnPost;
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

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
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

	public String getOffsetCnt() {
		return offsetCnt;
	}

	public void setOffsetCnt(String offsetCnt) {
		this.offsetCnt = offsetCnt;
	}

	public String getLastComunicationId() {
		return lastComunicationId;
	}

	public void setLastComunicationId(String lastComunicationId) {
		this.lastComunicationId = lastComunicationId;
	}

	public String getRemainFeeds() {
		return remainFeeds;
	}

	public void setRemainFeeds(String remainFeeds) {
		this.remainFeeds = remainFeeds;
	}

}