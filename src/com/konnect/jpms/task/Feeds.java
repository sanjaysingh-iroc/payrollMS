package com.konnect.jpms.task;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.EmpDashboardData;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadProjectDocuments;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Feeds extends ActionSupport implements ServletRequestAware, IStatements {

	
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	private HttpServletRequest request;
	CommonFunctions CF = null;
	String strSessionEmpId;
	String strSessionOrgId;
	String strUserType;
	
	private String operation;
	private String btnPost;
	private String strCommunication;
	private String strAlignWith;
	private String strAlignWithIds;
	private String strVisibility;
	private String strFeedDocFileName;
	private String pageFrom;
	private String proId;
	private String pageType;
	private String taskId;
	private String proFreqId;
	private String invoiceId;
	private String allFeedsCount;
	private String lastComunicationId;
	private File strFeedDoc;
	
	private List<FillEmployee> resourceList;
	private List<FillAlignedType> alignTypeList;
	
	private String[] strTaggedWith;
	private String[] strVisibilityWith;
	
	public Feeds(){
		
	}
	
	public Feeds(CommonFunctions CF,HttpServletRequest request){
		this.request  = request;
		this.CF = CF;
		
		
	}
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
		
		request.setAttribute(PAGE, "/jsp/task/Feeds.jsp");
		request.setAttribute(TITLE, "Feeds");
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getRetriveProjectDocumentFolder());
		
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
		
		if(getBtnPost() != null && !getBtnPost().equals("")) {
			addNewPost(uF);
		}
		
		viewProfile(uF, strSessionEmpId);
		
		getDayThought(uF);
		getResignationStatus(uF);
		getMailCount(uF);
		getMyTeam(uF);
		getAllPosts(uF);
		
//		if (getStrClientContactFName()!=null && getStrClientContactFName().length()>0) {
//				return insertCustomer();
//		} else if ((getStrClientContactFName()==null || getStrClientContactFName().length()==0) && (operation == null || operation.equals(""))) {
//			session.setAttribute(MESSAGE, SUCCESSM+"No customer added, please try again."+END);
//			return SUCCESS;
//		}
		
		if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Project")) {
			if(getBtnPost() != null && !getBtnPost().equals("")) {
				return "protab";
			} else {
				return "tab";
			}
		} else if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Task")) {
			if(getBtnPost() != null && !getBtnPost().equals("")) {
				return "tasktab";
			} else {
				return LOAD;
			}
		} else if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Timesheet")) {
			if(getBtnPost() != null && !getBtnPost().equals("")) {
				return "timesheettab";
			} else {
				return LOAD;
			}
		} else if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Invoice")) {
			if(getBtnPost() != null && !getBtnPost().equals("")) {
				return "invoicetab";
			} else {
				return LOAD;
			}
		} else if(getPageFrom()!=null && (getPageFrom().trim().equalsIgnoreCase("VAPTask") || getPageFrom().trim().equalsIgnoreCase("VAPProject"))) {
			if(getBtnPost() != null && !getBtnPost().equals("")) {
				return "vaptasktab";
			} else {
				return LOAD;
			}
		} else if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("MyHub")) {
			if(getBtnPost() != null && !getBtnPost().equals("")) {
				return "myhubtab";
			} else {
				return "tab";
			}
		} else {
			return LOAD;
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
				String MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
				String MYImage = "<img class='lazy img-circle' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage = "<img class='lazy img-circle' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
			  	} else if(createdByImage != null && !createdByImage.equals("")) {
			  		MYImage = "<img class='lazy img-circle' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage+"\" />";
			  		MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_100x100+"/"+createdByImage+"\" />";
	            }
				request.setAttribute("MYLargeImg", MYLargeImage);
				request.setAttribute("MYImg", MYImage);
			} else {
				String createdByImage = hmResourceImage.get(strSessionEmpId);
				String MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" />";
				String MYImage = "<img class='lazy img-circle' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" />"; 
				if(CF.getStrDocRetriveLocation()==null) { 
					MYImage = "<img class='lazy img-circle' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
			  	} else if(createdByImage != null && !createdByImage.equals("")) {
			  		MYImage = "<img class='lazy img-circle' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_22x22+"/"+createdByImage+"\" />";
			  		MYLargeImage = "<img class='lazy img-circle' border=\"1px solid #CCCCCC\" height=\"80\" width=\"80\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+strSessionEmpId+"/"+I_100x100+"/"+createdByImage+"\" />";
	            }
				request.setAttribute("MYLargeImg", MYLargeImage);
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
				pst = con.prepareStatement("select * from communication_1 where (align_with = "+PROJECT+" and align_with_id= "+uF.parseToInt(getProId())+") " +
					"or (align_with = "+TASK+" and align_with_id in ("+proTaskIds.toString()+")) or (align_with = "+PRO_TIMESHEET+" and align_with_id in  ("+proDocIds.toString()+")) " +
					"order by communication_id desc ");
			} else if(getPageFrom()!=null && (getPageFrom().trim().equalsIgnoreCase("Task") || getPageFrom().trim().equalsIgnoreCase("VAPTask"))) {
				pst = con.prepareStatement("select * from communication_1 where align_with = "+TASK+" and align_with_id = "+uF.parseToInt(getTaskId())+" " +
					"order by communication_id desc ");
			} else if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Timesheet")) {
				pst = con.prepareStatement("select * from communication_1 where align_with = "+PRO_TIMESHEET+" and align_with_id = "+uF.parseToInt(getProFreqId())+" " +
					"order by communication_id desc ");
			} else if(getPageFrom()!=null && getPageFrom().trim().equalsIgnoreCase("Invoice")) {
					pst = con.prepareStatement("select * from communication_1 where align_with = "+INVOICE+" and align_with_id = "+uF.parseToInt(getInvoiceId())+" " +
					"order by communication_id desc ");
			} else {
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					pst = con.prepareStatement("select * from communication_1 where (parent_id = 0 and (client_visibility_with_id like '%,"+strSessionEmpId+",%' " +
						" or client_tagged_with like '%,"+strSessionEmpId+",%' or client_created_by = "+uF.parseToInt(strSessionEmpId)+")) or communication_id in " +
						"(select parent_id from communication_1 where parent_id > 0 and client_tagged_with like '%,"+strSessionEmpId+",%') order by communication_id desc ");
					
				} else {
					pst = con.prepareStatement("select * from communication_1 where (parent_id = 0 and (visibility = 0 or visibility_with_id like '%,"+strSessionEmpId+",%' " +
						" or tagged_with like '%,"+strSessionEmpId+",%' or created_by = "+uF.parseToInt(strSessionEmpId)+")) or communication_id in " +
						"(select parent_id from communication_1 where parent_id > 0 and tagged_with like '%,"+strSessionEmpId+",%') order by communication_id desc ");
					
				}
			}
			rs = pst.executeQuery();
			StringBuilder sbCommunicationIds = null;
			List<String> alFeedIds = new ArrayList<String>();
			while(rs.next()) {
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
				if(alFeedIds.size() == 10) {
					break;
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmFeedIds", hmFeedIds);
			
			if(sbCommunicationIds != null) {
				StringBuilder sbQuery = null;

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
							String addedImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
							if(CF.getStrDocRetriveLocation()==null) { 
								addedImage = "<img class='lazy' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
						  	} else if(createdByImage != null && !createdByImage.equals("")) {
						  		addedImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage+"\" />";
				            }
							innerList.add(addedImage); //9
							innerList.add(hmCustName.get(rs.getString("client_created_by"))); //10
						} else {
							String createdByImage = hmResourceImage.get(rs.getString("created_by"));
							String addedImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" />"; 
							if(CF.getStrDocRetriveLocation()==null) { 
								addedImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
						  	} else if(createdByImage != null && !createdByImage.equals("")) {
						  		addedImage = "<img class='lazy img-circle' border=\"0\" height=\"30\" width=\"30\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_60x60+"/"+createdByImage+"\" />";
				            }
							innerList.add(addedImage); //9
							innerList.add(hmResourceName.get(rs.getString("created_by"))); //10
						}
						if(rs.getString("update_time") != null) {
							innerList.add(uF.getDateFormat(rs.getString("update_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM)); //11
						} else { 
							innerList.add(uF.getDateFormat(rs.getString("create_time"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM)); //11
						}
						innerList.add(uF.showData(yourLike, "N")); //12
						String strAddedby = null;
						if(strUserType != null && strUserType.equals(CUSTOMER)) {
							innerList.add(rs.getString("client_created_by")); //13
							strAddedby = rs.getString("client_created_by");
						} else {
							innerList.add(rs.getString("created_by")); //13
							strAddedby = rs.getString("created_by");
						}
						innerList.add(rs.getString("doc_or_image")); //14
						String extenstion = null;
						if(rs.getString("doc_or_image") !=null && !rs.getString("doc_or_image").trim().equals("")){
							extenstion = FilenameUtils.getExtension(rs.getString("doc_or_image").trim());
						}
						innerList.add(extenstion); //15
		
						String feedMainPath = getFeedMainPath(con, uF, rs.getString("communication_id"), strAddedby);
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
			request.setAttribute("hmFeeds", hmFeeds);
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

	private String getFeedMainPath(Connection con, UtilityFunctions uF, String postId, String strEmpId) {

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
			String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			if(uF.parseToInt(proOrCat) == 0) {
				feedPath = CF.getRetriveProjectDocumentFolder() +empOrgId+"/Uncategorised";
			} else if(uF.parseToInt(proOrCat) == 1) {
				feedPath = CF.getRetriveProjectDocumentFolder() +empOrgId+"/Projects";
			} else if(uF.parseToInt(proOrCat) == 2) {
				feedPath = CF.getRetriveProjectDocumentFolder() +empOrgId+"/Categories";
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
			}
			rs.close();
			pst.close();
			
			hmLastCommentId.put(postId+"_LAST_COMEENT_ID", lastCommentId);
			hmLastCommentId.put(postId+"_REMAIN_COMEENT_COUNT", remainCommentCnt+"");
			
			if(sbcommentIds != null) {
				pst = con.prepareStatement("select * from communication_1 where parent_id = ? and communication_id in ("+sbcommentIds.toString()+") order by communication_id");
				pst.setInt(1, uF.parseToInt(postId));
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
						String addedImage = "<img class='lazy img-circle' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" />"; 
						if(CF.getStrDocRetriveLocation()==null) { 
							addedImage = "<img class='lazy img-circle' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					  	} else if(createdByImage != null && !createdByImage.equals("")) {
					  		addedImage = "<img class='lazy img-circle' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_CUSTOMER+"/"+strClientId+"/"+I_IMAGE+"/"+I_CUSTOMER_SPOC+"/"+I_22x22+"/"+createdByImage+"\" />";
			            }
						innerList.add(addedImage); //4
						innerList.add(hmCustName.get(rs.getString("client_created_by"))); //5
					} else {
						String createdByImage = hmResourceImage.get(rs.getString("created_by"));
						String addedImage = "<img class='lazy img-circle' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" />"; 
						if(CF.getStrDocRetriveLocation()==null) { 
							addedImage = "<img class='lazy img-circle' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+DOCUMENT_LOCATION + createdByImage+"\" />";
					  	} else if(createdByImage != null && !createdByImage.equals("")) {
					  		addedImage = "<img class='lazy img-circle' border=\"0\" height=\"25\" width=\"25\" src=\"userImages/avatar_photo.png\" data-original=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("created_by")+"/"+I_22x22+"/"+createdByImage+"\" />";
			            }
						innerList.add(addedImage); //4
						innerList.add(hmResourceName.get(rs.getString("created_by"))); //5
					}
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
					innerList.add(String.valueOf(likes+clikeCount)); //12
					alComments.add(innerList);
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	private void addNewPost(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			List<String> alInner = CF.isCommAvailable(con, uF, "Feeds");
			if(alInner == null) alInner = new ArrayList<String>();
			boolean flag =false;
			int size = alInner.size();
			
			if(alInner!=null && size>0){
				if(alInner.get(1).equalsIgnoreCase(getStrCommunication()) && strSessionEmpId.equalsIgnoreCase(alInner.get(2))) {
					flag = true;
				}
			}
			
			if(flag == false){
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
				
				StringBuilder sbVisibilityWith = null;
				for(int i=0; getStrVisibilityWith() != null && i<getStrVisibilityWith().length; i++) {
					if(sbVisibilityWith == null) {
						sbVisibilityWith = new StringBuilder();
						sbVisibilityWith.append("," + getStrVisibilityWith()[i] + ",");
					} else {
						sbVisibilityWith.append(getStrVisibilityWith()[i] + ",");
					}
				}
				if(sbVisibilityWith == null) {
					sbVisibilityWith = new StringBuilder();
				}
				
				String strProTeamId = getProjectteamIdsByTaskProFreqInvoice(con, uF, getPageFrom(), getStrAlignWith(), getStrAlignWithIds(), getTaskId(), getProFreqId(), getInvoiceId());
				String strProSPOCId = getProjectSPOCIdByTaskProFreqInvoice(con, uF, getPageFrom(), getStrAlignWith(), getStrAlignWithIds(), getTaskId(), getProFreqId(), getInvoiceId());
				
				StringBuilder sbQue = new StringBuilder();
				sbQue.append("insert into communication_1(communication,align_with,align_with_id,tagged_with,doc_shared_with_id,doc_id," +
					"visibility,visibility_with_id");
				if(strUserType != null && strUserType.equals(CUSTOMER)) {
					sbQue.append(",client_created_by");
				} else {
					sbQue.append(",created_by");
				}
				sbQue.append(",create_time,client_tagged_with,client_visibility_with_id) values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
				pst = con.prepareStatement(sbQue.toString());
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
				pst.setInt(7, uF.parseToInt(getStrVisibility()));
				if(uF.parseToInt(getStrVisibility()) == S_TEAM) {
					pst.setString(8, strProTeamId);
				} else {
					pst.setString(8, sbVisibilityWith.toString());
				}
				pst.setInt(9, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(10, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				if(uF.parseToInt(getStrVisibility()) == S_CUSTOMER) {
					pst.setString(11, strProSPOCId);
					pst.setString(12, strProSPOCId);
				} else {
					pst.setString(11, null);
					pst.setString(12, null);
				}
				pst.executeUpdate();
				pst.close();
				
				session.setAttribute(MESSAGE,SUCCESSM +"<b>"+"Feed posted Successfully."+ END );
				
				
				String feedId = null;
				pst = con.prepareStatement("select max(communication_id) as communication_id from communication_1");
				rs = pst.executeQuery();
				while(rs.next()) {
					feedId = rs.getString("communication_id");
				}
				rs.close();
				pst.close();
				
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
				
				String strCheckFile = proNameFolder+"/"+getStrFeedDocFileName();
				String strFileName = getStrFeedDocFileName();
				File f = new File(strCheckFile);
			    if(f.isFile() && f.exists()) {
			    	String strCopyFile = uF.getCreateNewFileName("Copy_"+getStrFeedDocFileName(), proNameFolder, getStrFeedDocFileName(), 0);
			    	strFileName = strCopyFile;
			    }
				
				if(lengthBytes > 0) { // && !isFileExist
					
					pst = con.prepareStatement("insert into project_document_details(client_id,pro_id,added_by,entry_date,folder_file_type," +
						"pro_folder_id,align_with,sharing_type,sharing_resources,project_category,scope_document,description,doc_parent_id," +
						"is_edit,is_delete,is_cust_add,sharing_poc,doc_version,feed_id) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
					pst.setInt(1, 0);
					pst.setInt(2, uF.parseToInt(proId));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setString(5, "file");
					pst.setInt(6, 0);
					pst.setInt(7, 0);
					pst.setInt(8, uF.parseToInt(getStrVisibility()));
					pst.setString(9, sbVisibilityWith.toString());
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
					pst.setInt(19, uF.parseToInt(feedId));
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
					
					uploadProjectDocuments(con, getStrFeedDoc(), strFileName, proNameFolder, proDocumentId, feedId);
				}
				
				setStrCommunication("");
				setStrAlignWith("");
				setStrAlignWithIds("");
				setStrTaggedWith(null);
				setStrVisibilityWith(null);
				setStrVisibility("");
			}
			
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
	
	public void viewProfile(UtilityFunctions uF, String strEmpIdReq) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		List<List<String>> alEducation;
		try {
			con = db.makeConnection(con);
			
			EmpDashboardData dashboardData = new EmpDashboardData(request, session, CF, uF, con, strEmpIdReq);
			dashboardData.getPosition();
			
			Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, strUserType, strEmpIdReq);
			
			CF.getElementList(con, request);
			CF.getAttributes(con, request, strEmpIdReq);
			
			List<List<String>> alSkills = new ArrayList<List<String>>();
			alSkills = CF.selectSkills(con, uF.parseToInt(strEmpIdReq));
			
			request.setAttribute(TITLE, "Feeds");
			
			request.setAttribute("alSkills", alSkills);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getMyTeam(UtilityFunctions uF) {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmp = new HashMap<String, String>();
			Map<String,String> empImageMap=new HashMap<String,String>();
			pst=con.prepareStatement("select emp_per_id,emp_fname,emp_mname,emp_lname,emp_image from employee_personal_details epd join " +
        		"employee_official_details eod on (epd.emp_per_id = eod.emp_id) join department_info di on (depart_id=dept_id) " +
        		"join grades_details gd on eod.grade_id=gd.grade_id join designation_details dd on gd.designation_id=dd.designation_id " +
        		"join level_details ld on dd.level_id=ld.level_id join org_details od  on ld.org_id=od.org_id where  is_alive= true " +
        		" and emp_per_id >0 and supervisor_emp_id=? order by emp_id"); //(supervisor_emp_id=? or emp_per_id =?) 
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs=pst.executeQuery();
			while(rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmp.put(rs.getString("emp_per_id"), rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmp", hmEmp);
			request.setAttribute("empImageMap", empImageMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getDayThought(UtilityFunctions uF) {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			pst = con.prepareStatement(selectThought);			
			pst.setInt(1, cal.get(Calendar.DAY_OF_YEAR));
			rs = pst.executeQuery();
			
			String strThought = null;
			String strThoughtBy = null;
			while (rs.next()) {
				strThought = rs.getString("thought_text");
				strThoughtBy = rs.getString("thought_by"); 
			}
			rs.close();
			pst.close();
			request.setAttribute("DAY_THOUGHT_TEXT",strThought);
			request.setAttribute("DAY_THOUGHT_BY",strThoughtBy);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getResignationStatus(UtilityFunctions uF) {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from emp_off_board where emp_id =? order by entry_date desc limit 1");			
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			String strResignationStatus = null;
			String strResignationStatusD = null;
			String strResigDate = null;
			int nResigId = 0;
			
			while (rs.next()) {
				nResigId = rs.getInt("off_board_id");
				if(rs.getInt("approved_1")==1 && rs.getInt("approved_2")==1) {
					if(rs.getString("off_board_type") != null && rs.getString("off_board_type").trim().equalsIgnoreCase("TERMINATED")) {
						strResignationStatus = "Terminated";
						strResignationStatusD = "You are terminated from the service. Please <a href=\"ExitForm.action?id="+session.getAttribute(EMPID)+"&resignId="+nResigId+"\">click here</a> to complete your formalities.";
					}else if(rs.getString("off_board_type") != null && rs.getString("off_board_type").trim().equalsIgnoreCase("RESIGNED")) {
						strResignationStatus = "Your resignation has been accepted";
						strResignationStatusD = "Your resignation has been accepted. Please <a href=\"ExitForm.action?id="+session.getAttribute(EMPID)+"&resignId="+nResigId+"\">click here</a> to complete your formalities.";
					}
					request.setAttribute("RESIG_STATUS", "1");
				} else if(rs.getInt("approved_1")==-1 || rs.getInt("approved_2")==-1) {
					strResignationStatus = "Your resignation has been denied";
					strResignationStatusD = "Your resignation has been denied";
				} else if(rs.getInt("approved_1")==1 && rs.getInt("approved_2")==0) {
					strResignationStatus = "Your resignation has been approved by your manager and is waiting for HR's approval";
					strResignationStatusD = "Your resignation has been approved by your manager and is waiting for HR's approval";
				} else if(rs.getInt("approved_1")==0 && rs.getInt("approved_2")==1) {
					strResignationStatus = "Your resignation has been approved by your HR and is waiting for manager's approval";
					strResignationStatusD = "Your resignation has been approved by your HR and is waiting for manager's approval";
				} else if(rs.getInt("approved_1")==0 || rs.getInt("approved_2")==0) {
					strResignationStatus = "Resigned & waiting for approval";
					strResignationStatusD = "Resigned & waiting for approval";
				}else if(rs.getString("off_board_type")!=null && rs.getString("off_board_type").equalsIgnoreCase("TERMINATED")){
					strResignationStatus = "Terminated";
					strResignationStatusD = "Terminated";
				}
				if(rs.getString("entry_date")!=null) {
					strResigDate = uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, DBDATE);
				}				
			}
			rs.close();
			pst.close();
			
			int nNotice = CF.getEmpNoticePeriod(con,strSessionEmpId );
			
			String resigData = "";
			String lastDate = "";
			int nDifference = 0;
			int nRemaining = 0;
			if(strResigDate!=null){
			
				String regDate = uF.getDateFormat(strResigDate, DBDATE,DATE_FORMAT);
								
				lastDate = uF.getDateFormat(""+uF.getBiweeklyDate(regDate, nNotice),DBDATE,CF.getStrReportDateFormat());
				String ldate = uF.getDateFormat(""+uF.getBiweeklyDate(regDate, nNotice),DBDATE,DATE_FORMAT);
				
				java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
				java.util.Date lstDate = uF.getDateFormatUtil(ldate,DATE_FORMAT );
				nDifference = uF.parseToInt(uF.dateDifference(strResigDate, DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE));
				
				if(lstDate.after(currDate)) {
					nRemaining = nNotice - nDifference;
					resigData = nRemaining + " days remaining";
				} else if(lstDate.before(currDate)) {
					resigData = " last day  "+lastDate;
				} else if(lstDate.equals(currDate)) {
					resigData = " Today is last day  ";
				}
				
			}
			
			request.setAttribute("RESIGNATION_STATUS", strResignationStatus);
			request.setAttribute("RESIGNATION_REMAINING", resigData);
			request.setAttribute("RESIGNATION_STATUS_D", strResignationStatusD);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getMailCount(UtilityFunctions uF) {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst =null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(getUnreadMailCount);			
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			
			int nMailCount = 0;
			while (rs.next()) {
				nMailCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
			request.setAttribute("MAIL_COUNT",nMailCount+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

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

	public String getAllFeedsCount() {
		return allFeedsCount;
	}

	public void setAllFeedsCount(String allFeedsCount) {
		this.allFeedsCount = allFeedsCount;
	}

	public String getLastComunicationId() {
		return lastComunicationId;
	}

	public void setLastComunicationId(String lastComunicationId) {
		this.lastComunicationId = lastComunicationId;
	}

}