package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author workrig
 *
 */
public class ViewAllProjects extends ActionSupport implements ServletRequestAware, ServletResponseAware, SessionAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	String strSessionEmpId;
	String strSessionOrgId;
	String strProductType =  null;
	
	String approve;
	String blocked;
	String operation;
	int ID;
	int singleProid; 
	int emp_id;
	String[] pro_id;
	HttpSession session;
	int task_id;
	boolean isSingle = false;
	String taskId;
	String reassign; 
	String reschedule;
	String align;
	String allowDeny;
	
	CommonFunctions CF;
	
	List<Integer> projectidlist = new ArrayList<Integer>();
	List<String> projectlist = new ArrayList<String>();
	List<String> alInner = new ArrayList<String>();
	Map<Integer, List<String>> activity_al = new HashMap<Integer, List<String>>();

	// Comes from ViewAllProject.action to view all project
	List<Integer> index = new ArrayList<Integer>();
	List<String> time1 = new ArrayList<String>();
	List<String> taskStatus1 = new ArrayList<String>();
	List<String> totalhrz1 = new ArrayList<String>();
	Map<Integer,String> EmpNameMap=new HashMap<Integer,String>();
	Map<Integer, List<String>> al = new HashMap<Integer, List<String>>();
//	Map<String,String> 	hmServiceDesc=new HashMap<String, String>();
	
	private String strSBU;
	private String strService;
	private String strProjectId;
	private String strManagerId;
	private String strClient;
	
	String strUserType;
	List<FillProjectList> projectdetailslist;
	List<FillServices> serviceList;
	List<FillServices> sbuList;
	List<FillClients> clientList;
	List<FillProjectOwnerList> proOwnerList;
	List<FillSkills> skillList;
	String[] skill;
	String[] managerId;
	String[] client;
	String projectID;
	String projectName;
	String[] f_service;
	String[] f_sbu;
	String[] location;
	String service_id;
	double actual_days;
	double ideal_days;
	String isReassign;
	String proType;
//	String strLimit;
	String strDivCount;
//	float no_of_days;
	
	List<GetPriorityList> priorityList;
	List<FillDependentTaskList> dependencyList;
	List<GetDependancyTypeList> dependancyTypeList;
	List<FillTaskEmpList> TaskEmpNamesList;
	List<FillSkills> empSkillList;
	
	String addTask;
	String proId;
	
	String proPage;
	String minLimit;
	
	String proStatus;
	String assignedBy;
	String recurrOrMiles;
	String sortBy;
	
	String alertStatus;
	String alert_type;
	String alertID;
	
	String pageType;
	
	String strSearchJob;
	
	List<FillWLocation> workLocationList;
	
	private String btnSubmit;
	private String submitType;
	
	public String execute() {
	
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strProductType = (String)session.getAttribute(PRODUCT_TYPE);
		
		if(uF.parseToInt(strProductType) != 3) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
//		boolean isView = CF.getAccess(session, request, uF);
		
//		System.out.println("getPageType ====>> " + getPageType());
//		System.out.println("getProId() ====>> " + getProId());
//		System.out.println("getStrSearchJob() ====>> " + getStrSearchJob());
		
//		if(!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied); 
//			return ACCESS_DENIED;
//		}
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-list-alt\"></i><a href=\"ViewAllProjects.action\" style=\"color: #3c8dbc;\"> Projects</a></li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		request.setAttribute(PAGE, "/jsp/task/ViewAllProjects.jsp");
		request.setAttribute(TITLE, "Working Projects");
		
		System.out.println("getPageType() ===>> " + getPageType());
		if(getPageType() == null || getPageType().equals("") || getPageType().equals("null")) {
			setPageType(null);
		}
		
//			hmServiceDesc=CF.getServiceDesc();
		loadProjectFilters();
//			updateTotalHrs();
		
//			System.out.println("getPageType ===>> " + getPageType());
		
		/*if(getAlertStatus()!=null && getAlert_type()!=null) {
			updateUserAlerts();
		}*/
		
		if(uF.parseToInt(getAlertID()) > 0) {
			updateUserAlerts();
		}
		
		if(getProPage() == null || getProPage().equals("") || getProPage().equals("null")) {
			setProPage("1");
		}
		
		if(getStrSBU() != null && !getStrSBU().equals("")) {
			setF_sbu(getStrSBU().split(","));
		} else {
			setF_sbu(null);
		}
		if(getStrService() != null && !getStrService().equals("")) {
			setF_service(getStrService().split(","));
		} else {
			setF_service(null);
		}
		if(getStrProjectId() != null && !getStrProjectId().equals("")) {
			setPro_id(getStrProjectId().split(","));
		} else {
			setPro_id(null);
		}
		if(getStrManagerId() != null && !getStrManagerId().equals("")) {
			setManagerId(getStrManagerId().split(","));
		} else {
			setManagerId(null);
		}
		if(getStrClient() != null && !getStrClient().equals("")) {
			setClient(getStrClient().split(","));
		} else {
			setClient(null);
		}
		
//			System.out.println("getAddTask ===>> " + getAddTask());
		if(getAddTask() != null && getAddTask().equals("Save")) {
			//System.out.println("getProId ===>> " + getProId());
			insertActivityDetails(uF);
		}
		
		if(getProType() == null || getProType().equals("") || getProType().equalsIgnoreCase("null") || getProType().equals("L")) {
			if(operation!=null && operation.equals("D")) {
				deleteProject();
			}
			
			if (getReassign() != null && !getReassign().equals("")) {
				reassignTaskSubTask();
			}
			
			if (getReschedule() != null && !getReschedule().equals("")) {
				rescheduleTaskSubTask();
			}
			
			if (getAlign() != null && !getAlign().equals("")) {
				alignTaskSubTask();
			}
			
			if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(CEO))) {
				projectdetailslist = new FillProjectList(request).fillAllProjectDetails(false, false);
			} else if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
				projectdetailslist = new FillProjectList(request).fillProjectDetailsByCustomer(uF.parseToInt(strSessionEmpId), true, false, false);
			} else {
				projectdetailslist = new FillProjectList(request).fillProjectDetailsByManager(uF.parseToInt(strSessionEmpId), false, false);
			}
			
			request.setAttribute(TITLE, "Working Projects");
		} else if(getProType() != null && getProType().equals("C")) {
			
			if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(CEO))) {
				projectdetailslist = new FillProjectList(request).fillAllProjectDetails(true, false);
			} else if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
				projectdetailslist = new FillProjectList(request).fillProjectDetailsByCustomer(uF.parseToInt(strSessionEmpId), false, true, false);
			} else {
				projectdetailslist = new FillProjectList(request).fillProjectDetailsByManager(uF.parseToInt(strSessionEmpId), true, false);
			}
//				getCompletedProjectDetails();
			request.setAttribute(TITLE, "Completed Projects");
		} else if(getProType() != null && getProType().equals("B")) {
			
			if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(CEO))) {
				projectdetailslist = new FillProjectList(request).fillAllProjectDetails(false, true);
			} else if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
				projectdetailslist = new FillProjectList(request).fillProjectDetailsByCustomer(uF.parseToInt(strSessionEmpId), false, false, true);
			} else {
				projectdetailslist = new FillProjectList(request).fillProjectDetailsByManager(uF.parseToInt(strSessionEmpId), false, true);
			}
//				getBlockedProjectDetails();
			request.setAttribute(TITLE, "Blocked Projects");
		}
		
		if(getSortBy() == null || getSortBy().equals("")) {
			setSortBy("1");
		}

//		getProjectDetails();
//			getActivity(uF);
		getProjectFilters();
		if(getBtnSubmit() != null) {
//			if(getPageType() != null && getPageType().equals("MP")) {
//				return MYSUCCESS;
//			} else {
				return SUCCESS;
//			}
		} else {
			if(getPageType() != null && getPageType().equals("MP")) {
				return MYLOAD;
			} else {
				return LOAD;
			}
		}
	}
	
	private void updateUserAlerts() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		try {
			con = db.makeConnection(con);
//			String strType = null;
//			if(getAlert_type().equals(PRO_CREATED_ALERT)) {
//				strType = PRO_CREATED_ALERT;
//			} else if(getAlert_type().equals(PRO_COMPLETED_ALERT)) {
//				strType = PRO_COMPLETED_ALERT;
//			} else if(getAlert_type().equals(PRO_NEW_RESOURCE_ALERT)) {
//				strType = PRO_NEW_RESOURCE_ALERT;
//			} else if(getAlert_type().equals(TASK_ALLOCATE_ALERT)) {
//				strType = TASK_ALLOCATE_ALERT;
//			} else if(getAlert_type().equals(TASK_NEW_REQUEST_ALERT)) {
//				strType = TASK_NEW_REQUEST_ALERT;
//			} else if(getAlert_type().equals(TASK_REQUEST_RESCHEDULE_ALERT)) {
//				strType = TASK_REQUEST_RESCHEDULE_ALERT;
//			} else if(getAlert_type().equals(TASK_REQUEST_REASSIGN_ALERT)) {
//				strType = TASK_REQUEST_REASSIGN_ALERT;
//			} else if(getAlert_type().equals(TASK_ACCEPT_ALERT)) {
//				strType = TASK_ACCEPT_ALERT;
//			} else if(getAlert_type().equals(TASK_COMPLETED_ALERT)) {
//				strType = TASK_COMPLETED_ALERT;
//			}
			
//			if(strType!=null && !strType.trim().equals("")){
//				String strDomain = request.getServerName().split("\\.")[0];
//				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//				userAlerts.setStrDomain(strDomain);
//				userAlerts.setStrEmpId(""+nEmpId); 
//				if(strUserType!=null && strUserType.equals(CUSTOMER)){
//					userAlerts.setStrOther("other");
//				}
//				userAlerts.set_type(strType);
//				userAlerts.setStatus(UPDATE_ALERT);
//				Thread t = new Thread(userAlerts);
//				t.run(); 
//			}
			
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
	
	private void alignTaskSubTask() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		UtilityFunctions uF = new UtilityFunctions();
		
		if (getTaskId() != null && !getTaskId().equals("")) {
			try {
				
				if(uF.parseToInt(getAllowDeny())==1) {
//					pst = con.prepareStatement("UPDATE activity_info SET task_accept_status=1, reschedule_reassign_align_by=? WHERE task_id=?");
					pst = con.prepareStatement("UPDATE activity_info SET task_accept_status=1, added_by=? WHERE task_id=?");
					pst.setInt(1, uF.parseToInt(strSessionEmpId));
					pst.setInt(2, uF.parseToInt(getTaskId()));
					pst.executeUpdate();
					pst.close();
					
					String parentTaskId = null;
					pst = con.prepareStatement("select parent_task_id from activity_info WHERE task_id=? and parent_task_id > 0");
					pst.setInt(1, uF.parseToInt(getTaskId()));
					rs = pst.executeQuery();
					while(rs.next()) {
						parentTaskId = rs.getString("parent_task_id");
					}
					rs.close();
					pst.close();
					
					if(uF.parseToInt(parentTaskId)>0) {
//						pst = con.prepareStatement("UPDATE activity_info SET task_accept_status=1, reschedule_reassign_align_by=? WHERE task_id=?");
						pst = con.prepareStatement("UPDATE activity_info SET task_accept_status=1, added_by=? WHERE task_id=?");
						pst.setInt(1, uF.parseToInt(strSessionEmpId));
						pst.setInt(2, uF.parseToInt(parentTaskId));
						pst.executeUpdate();
						pst.close();
					}
				} else if(uF.parseToInt(getAllowDeny())==2) {
//					pst = con.prepareStatement("UPDATE activity_info SET task_accept_status=-1, reschedule_reassign_align_by=? WHERE task_id=?");
					pst = con.prepareStatement("UPDATE activity_info SET task_accept_status=-1, added_by=? WHERE task_id=?");
					pst.setInt(1, uF.parseToInt(strSessionEmpId));
					pst.setInt(2, uF.parseToInt(getTaskId()));
					pst.executeUpdate();
					pst.close();
					
//					pst = con.prepareStatement("UPDATE activity_info SET task_accept_status=-1, reschedule_reassign_align_by=? WHERE parent_task_id=?");
					pst = con.prepareStatement("UPDATE activity_info SET task_accept_status=-1, added_by=? WHERE parent_task_id=?");
					pst.setInt(1, uF.parseToInt(strSessionEmpId));
					pst.setInt(2, uF.parseToInt(getTaskId()));
					pst.executeUpdate();
					pst.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}
	}


	private void rescheduleTaskSubTask() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		UtilityFunctions uF = new UtilityFunctions();
		
		if (getTaskId() != null && !getTaskId().equals("")) {
			try {
				
				String rStartDate = null;
				String rDeadline = null;
				pst = con.prepareStatement("select r_start_date,r_deadline from activity_info WHERE task_id=?");
				pst.setInt(1, uF.parseToInt(getTaskId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(rs.getString("r_start_date") != null)
						rStartDate = uF.getDateFormat(rs.getString("r_start_date"), DBDATE, DATE_FORMAT);
					if(rs.getString("r_deadline") != null)
						rDeadline = uF.getDateFormat(rs.getString("r_deadline"), DBDATE, DATE_FORMAT);
				}
				rs.close();
				pst.close();
				
				int x = 0;
				if(rStartDate != null && rDeadline != null && uF.parseToInt(getAllowDeny())==1) {
//					pst = con.prepareStatement("UPDATE activity_info SET task_accept_status=0, start_date=?, deadline=?, reschedule_reassign_align_by=? WHERE task_id=?");
					pst = con.prepareStatement("UPDATE activity_info SET task_accept_status=0, start_date=?, deadline=?, added_by=? WHERE task_id=?");
					pst.setDate(1, uF.getDateFormat(rStartDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(rDeadline, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setInt(4, uF.parseToInt(getTaskId()));
					x = pst.executeUpdate();
	//				System.out.println("pst ===>> " + pst);
					pst.close();
				} else if(rStartDate != null && rDeadline != null && uF.parseToInt(getAllowDeny())==2) {
//					pst = con.prepareStatement("UPDATE activity_info SET task_accept_status=0, reschedule_reassign_align_by=? WHERE task_id=?");
					pst = con.prepareStatement("UPDATE activity_info SET task_accept_status=0, added_by=? WHERE task_id=?");
					pst.setInt(1, uF.parseToInt(strSessionEmpId));
					pst.setInt(2, uF.parseToInt(getTaskId()));
					x = pst.executeUpdate();
	//				System.out.println("pst ===>> " + pst);
					pst.close();
				}
				
				/**
				 * Alerts
				 * */
				int nProId = 0;
				String taskName = null;
				pst = con.prepareStatement("select pro_id,resource_ids,activity_name from activity_info where task_id=?");
				pst.setInt(1, uF.parseToInt(getTaskId()));
				rs = pst.executeQuery();
				String strResourceIds = null;
				while(rs.next()){
					nProId = rs.getInt("pro_id");
					strResourceIds = rs.getString("resource_ids");
					taskName = rs.getString("activity_name");
				}
				rs.close();
				pst.close();
				if(nProId > 0 && x > 0){			
					List<String> alEmp = new ArrayList<String>();
									
					if(strResourceIds !=null && !strResourceIds.trim().equals("") && strResourceIds.contains(",")){
						strResourceIds = strResourceIds.substring(1, strResourceIds.length()-1);
						List<String> alResource = Arrays.asList(strResourceIds.split(","));
						for(String strEmp : alResource){
							if(!alEmp.contains(strEmp.trim())){
								alEmp.add(strEmp.trim());
							}
						}
					}
					
					String strDomain = request.getServerName().split("\\.")[0];
					
					String proName = CF.getProjectNameById(con, nProId+"");
					String alertData = "<div style=\"float: left;\"> <b>"+taskName+"</b> in <b>"+proName+"</b> project has been reschedule to you by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					String alertAction = "MyWork.action";
					for(String strEmp : alEmp) {
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(TASK_RESCHEDULE_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}
	}


	private void reassignTaskSubTask() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		UtilityFunctions uF = new UtilityFunctions();
		
//		System.out.println("getTaskId() ===>> " + getTaskId());
		
		if (getTaskId() != null && !getTaskId().equals("")) {
			try {
				pst = con.prepareStatement("UPDATE activity_info SET approve_status='n', completed=0, finish_task='n', reassign_by=? WHERE task_id=?");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, uF.parseToInt(getTaskId()));
				int x = pst.executeUpdate();
//				System.out.println("pst ===>> " + pst);
				pst.close();
				
				StringBuilder sbSubTask = null;
				pst = con.prepareStatement("select task_id from activity_info WHERE parent_task_id=?");
				pst.setInt(1, uF.parseToInt(getTaskId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbSubTask == null) {
						sbSubTask = new StringBuilder();
						sbSubTask.append(rs.getString("task_id"));
					} else {
						sbSubTask.append(","+rs.getString("task_id"));
					}
				}
				rs.close();
				pst.close();
				
				if(sbSubTask != null && !sbSubTask.toString().equals("")) {
					pst = con.prepareStatement("UPDATE activity_info SET approve_status='n', completed=0, finish_task='n', reassign_by=? WHERE task_id in ("+sbSubTask.toString()+")");
					pst.setInt(1, uF.parseToInt(strSessionEmpId));
					pst.executeUpdate();
	//				System.out.println("pst ===>> " + pst);
					pst.close();
				}
				
				/**
				 * Alerts
				 * */
				int nProId = 0;
				String taskName = null;
				pst = con.prepareStatement("select pro_id,resource_ids,activity_name from activity_info where task_id=?");
				pst.setInt(1, uF.parseToInt(getTaskId()));
				rs = pst.executeQuery();
				String strResourceIds = null;
				while(rs.next()){
					nProId = rs.getInt("pro_id");
					strResourceIds = rs.getString("resource_ids");
					taskName = rs.getString("activity_name");
				}
				rs.close();
				pst.close();
				if(nProId > 0 && x > 0){			
					List<String> alEmp = new ArrayList<String>();
									
					if(strResourceIds !=null && !strResourceIds.trim().equals("") && strResourceIds.contains(",")){
						strResourceIds = strResourceIds.substring(1, strResourceIds.length()-1);
						List<String> alResource = Arrays.asList(strResourceIds.split(","));
						for(String strEmp : alResource){
							if(!alEmp.contains(strEmp.trim())){
								alEmp.add(strEmp.trim());
							}
						}
					}
					
					String strDomain = request.getServerName().split("\\.")[0];
					
					String proName = CF.getProjectNameById(con, nProId+"");
					String alertData = "<div style=\"float: left;\"> <b>"+taskName+"</b> in <b>"+proName+"</b> project has been reassign to you by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
					String alertAction = "MyWork.action";
					for(String strEmp : alEmp){
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(strEmp.trim());
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
//						userAlerts.set_type(TASK_REASSIGN_ALERT);
						userAlerts.setStatus(INSERT_TR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}
	}


	public void loadProjectFilters() {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			workLocationList = new FillWLocation(request).fillWLocationOrgIdAndWLocationIds((String)session.getAttribute(ORG_ACCESS), (String)session.getAttribute(WLOCATION_ACCESS));
			if((String)session.getAttribute(ORG_ACCESS)!=null) {
			sbuList = new FillServices(request).fillServicesByOrgIds((String)session.getAttribute(ORG_ACCESS));
			} else {
				sbuList = new FillServices(request).fillServicesByOrgIds(strSessionOrgId);
			}
		} else {
			workLocationList = new FillWLocation(request).fillWLocationOrgIdAndWLocationIds((String)session.getAttribute(ORG_ACCESS), null);
			sbuList = new FillServices(request).fillServicesByOrgIds(strSessionOrgId);
		}
		serviceList = new FillServices(request).fillProjectServices();
		clientList = new FillClients(request).fillClients(false);
		proOwnerList = new FillProjectOwnerList(request).fillProjectOwner();
		skillList = new FillSkills(request).fillSkillsWithId();
	} 
	
	
	/*public void reassignProject()
	{try {
		
		pst = con.prepareStatement("UPDATE activity_info SET completed=?,approve_status='n',finish_task='n' WHERE task_id =?");
		pst.setInt(1,0);
		pst.setInt(2, task_id); 
		pst.executeUpdate();
	} catch (Exception e) {
		e.printStackTrace();
	} 
	}*/
	
	
	
	public void insertActivityDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String[] taskTRId = request.getParameterValues("taskTRId"+getProId());
			String[] taskByCust = request.getParameterValues("taskByCust"+getProId());
			String[] taskname = request.getParameterValues("taskname"+getProId());
			String[] taskDescription = request.getParameterValues("taskDescription"+getProId());
			String[] isRecurringTask = request.getParameterValues("isRecurringTask"+getProId());
			String[] taskID = request.getParameterValues("taskID"+getProId());
			String[] dependency = request.getParameterValues("dependency"+getProId());
			String[] dependencyType = request.getParameterValues("dependencyType"+getProId());
			
//			String task_dependency = request.getParameter("task_dependency");
//			String dependency_type = request.getParameter("dependency_type");

			String[] priority = request.getParameterValues("priority"+getProId());
//			String[] empSkills = request.getParameterValues("empSkills"+getProId());
			
			String[] startDate = request.getParameterValues("startDate"+getProId());
			String[] deadline1 = request.getParameterValues("deadline1"+getProId());
			String[] idealTime = request.getParameterValues("idealTime"+getProId());
			String[] colourCode = request.getParameterValues("colourCode"+getProId());

			con = db.makeConnection(con);

			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			String forcedTask = null;
			Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, getProId());
			if(hmProjectData != null) {
				forcedTask = CF.getProjectForcedTask(con, hmProjectData.get("PRO_ORG_ID"));
			}
			
			String freqTaskName = null;
			pst = con.prepareStatement("select freq_start_date,freq_end_date from projectmntnc_frequency where pro_id=?");
			pst.setInt(1, uF.parseToInt(getProId()));
			
			rs = pst.executeQuery();
			while (rs.next()) {
				freqTaskName = uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT_STR)+" to "+uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT_STR);
			}
			rs.close();
			pst.close();
			
//			System.out.println("freqTaskName ===>>> " + freqTaskName);
			
			for (int i = 0; taskID!=null && i<taskID.length; i++) {
			
				if(startDate[i] != null && !startDate[i].equals("") && !startDate[i].equals("-") && deadline1[i] !=null && !deadline1[i].equals("") && !deadline1[i].equals("-")) {
					freqTaskName = uF.getDateFormat(startDate[i], DATE_FORMAT, DATE_FORMAT_STR)+" to "+uF.getDateFormat(deadline1[i], DATE_FORMAT, DATE_FORMAT_STR);
				}
//				System.out.println("taskID ===>>> "+ i +" --- "+ taskID[i]);
				
				String[] emp_id = request.getParameterValues("emp_id"+getProId()+"_"+taskTRId[i]);
				
//				System.out.println("getTaskTRId().length ====>> " + getTaskTRId().length);
//				System.out.println("getTaskTRId()[i] ====>> " + getTaskTRId()[i]);
				
				String[] subtaskname = request.getParameterValues("subtaskname"+getProId()+"_"+taskTRId[i]);
				String[] subtaskDescription = request.getParameterValues("subTaskDescription"+getProId()+"_"+taskTRId[i]);
				String[] isRecurringSubTask = request.getParameterValues("isRecurringSubTask"+getProId()+"_"+taskTRId[i]);
				
				String[] subTaskID = request.getParameterValues("subTaskID"+getProId()+"_"+taskTRId[i]);
				String[] subTaskTRId = request.getParameterValues("subTaskTRId"+getProId()+"_"+taskTRId[i]);
				String[] subTaskByCust = request.getParameterValues("subTaskByCust"+getProId()+"_"+taskTRId[i]);
				
//				System.out.println("subTaskTRId.length ====>> " + subTaskTRId.length);
				
				String[] subDependency = request.getParameterValues("subDependency"+getProId()+"_"+taskTRId[i]);
				String[] subDependencyType = request.getParameterValues("subDependencyType"+getProId()+"_"+taskTRId[i]);
				 
				String[] subpriority = request.getParameterValues("subpriority"+getProId()+"_"+taskTRId[i]);
//				String[] empSubSkills = request.getParameterValues("empSubSkills"+getProId()+"_"+taskTRId[i]);
				
				
				String[] substartDate = request.getParameterValues("substartDate"+getProId()+"_"+taskTRId[i]);
				String[] subdeadline1 = request.getParameterValues("subdeadline1"+getProId()+"_"+taskTRId[i]);
				String[] subidealTime = request.getParameterValues("subidealTime"+getProId()+"_"+taskTRId[i]);
				String[] subcolourCode = request.getParameterValues("subcolourCode"+getProId()+"_"+taskTRId[i]);
				
				StringBuilder sbEmpIds = null;
//				System.out.println("emp_id ===>> " + emp_id);
				if (emp_id != null && emp_id.length > 0) {
					List<String> empIdList = Arrays.asList(emp_id);
					
					for (int a = 0; empIdList != null && a < empIdList.size(); a++) {
						if (sbEmpIds == null) {
							sbEmpIds = new StringBuilder();
							sbEmpIds.append("," + empIdList.get(a).trim()+",");
						} else {
							sbEmpIds.append(empIdList.get(a).trim()+",");
						}
					}
				}
				if (sbEmpIds == null) {
					sbEmpIds = new StringBuilder();
				}
				
				if((strUserType!=null && strUserType.equals(CUSTOMER) && uF.parseToInt(taskByCust[i]) == 1) || (strUserType!=null && !strUserType.equals(CUSTOMER))) {
					if(uF.parseToInt(taskID[i]) > 0) {
						
						int nProId = 0;
						pst = con.prepareStatement("select pro_id,resource_ids from activity_info where task_id=?");
						pst.setInt(1, uF.parseToInt(taskID[i]));
						rs = pst.executeQuery();
						String strResourceIds = null;
						while(rs.next()){
							nProId = rs.getInt("pro_id");
							strResourceIds = rs.getString("resource_ids");
						}
						rs.close();
						pst.close();
						
						List<String> alEmp = new ArrayList<String>();
						if(strResourceIds !=null && !strResourceIds.trim().equals("") && strResourceIds.contains(",")) {
							strResourceIds = strResourceIds.substring(1, strResourceIds.length()-1);
							List<String> alTemp = new ArrayList<String>();
							if (emp_id != null && emp_id.length > 0) {
								alTemp = Arrays.asList(emp_id);
							}
							List<String> alResource = Arrays.asList(strResourceIds.split(","));
							for(String strEmp : alTemp) {
								if(!alResource.contains(strEmp.trim())) {
									alEmp.add(strEmp.trim());
								}
							}
						} else {
							if (emp_id != null && emp_id.length > 0) {
								alEmp = Arrays.asList(emp_id);
							}
						}
						
						String strReassign = request.getParameter("taskActions"+getProId()+"_"+taskID[i]);
						if(strReassign == null || strReassign.equals("1")) {
							StringBuilder sbQuery = new StringBuilder();
							sbQuery.append("update activity_info set activity_name=?, priority=?, resource_ids=?, deadline=?, idealtime=?, start_date=?," +
								" dependency_task=?, dependency_type=?, color_code=?, taskstatus=?, task_skill_id=?, task_description=?," +
								"task_freq_name=?,recurring_task=? ");
							if(uF.parseToBoolean(forcedTask) && strUserType != null && !strUserType.equals(CUSTOMER) && (strReassign == null || strReassign.equals("1"))) {
								sbQuery.append(", task_accept_status=1 ");
							}
							if(strReassign != null && strReassign.equals("1")) {
								sbQuery.append(", task_accept_status=0, added_by="+uF.parseToInt(strSessionEmpId)+" ");
							}
							sbQuery.append(" where task_id =?");
							pst = con.prepareStatement(sbQuery.toString());
							pst.setString(1, taskname[i]);
							pst.setString(2, priority[i]);
		//					pst.setInt(3, uF.parseToInt(emp_id[i]));
							pst.setString(3, sbEmpIds.toString());
							pst.setDate(4, uF.getDateFormat(deadline1[i], DATE_FORMAT));
							pst.setDouble(5, uF.parseToDouble(idealTime[i]));
							pst.setDate(6, uF.getDateFormat(startDate[i], DATE_FORMAT));
							pst.setInt(7, uF.parseToInt(dependency[i]));
							pst.setString(8, dependencyType[i]);
							pst.setString(9, colourCode[i]);
							pst.setString(10, "New Task");
		//					pst.setInt(11, uF.parseToInt(empSkills[i]));
							pst.setInt(11, 0);
							pst.setString(12, taskDescription[i]);
	//						pst.setInt(13, uF.parseToInt(strSessionEmpId));
							pst.setString(13, freqTaskName);
							pst.setInt(14, uF.parseToInt(isRecurringTask[i]));
							pst.setInt(15, uF.parseToInt(taskID[i]));
							int x = pst.executeUpdate();
							pst.close();
							
							if(x > 0) {
								
								Map<String, String> hmTaskProData = CF.getTaskProInfo(con, taskID[i], null);
								
								for(int a=0; alEmp!=null && !alEmp.isEmpty() && a<alEmp.size(); a++) {
									Map<String, String> hmEmpData = hmEmpInfo.get(alEmp.get(a));
									String strDomain = request.getServerName().split("\\.")[0];
									Notifications nF = new Notifications(N_NEW_TASK_ASSIGN, CF); 
									nF.setDomain(strDomain);
									
									nF.request = request;
									nF.setStrOrgId(strSessionOrgId);
									nF.setEmailTemplate(true);
									
									nF.setStrEmpId(alEmp.get(a));
									nF.setStrResourceFName(hmEmpData.get("FNAME"));
									nF.setStrResourceLName(hmEmpData.get("LNAME"));
									nF.setStrTaskName(taskname[i]);
									nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
									nF.setStrProjectOwnerName(hmEmpName.get(hmTaskProData.get("PROJECT_OWNER_ID")));
									nF.setStrTeamLeader(hmTaskProData.get("TEAM_LEADER"));
									nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
									
									nF.setStrHostAddress(CF.getStrEmailLocalHost());
									nF.setStrHostPort(CF.getStrHostPort());
									nF.setStrContextPath(request.getContextPath());
									nF.sendNotifications();
								}
								
								String strDomain = request.getServerName().split("\\.")[0];
								String alertData = "<div style=\"float: left;\"> <b>"+taskname[i]+"</b> in <b>"+hmTaskProData.get("PRO_NAME")+"</b> project has been allocated to you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
								String alertAction = "MyWork.action";
								for(String strEmp : alEmp) {
									UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
									userAlerts.setStrDomain(strDomain);
									userAlerts.setStrEmpId(strEmp.trim());
									userAlerts.setStrData(alertData);
									userAlerts.setStrAction(alertAction);
//									userAlerts.set_type(TASK_ALLOCATE_ALERT);
									userAlerts.setStatus(INSERT_TR_ALERT);
									Thread t = new Thread(userAlerts);
									t.run();
								}
								
								alEmp = new ArrayList<String>();
								pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=? and _isteamlead = true");
								pst.setInt(1, nProId);
								rs = pst.executeQuery();
								while(rs.next()){
									if(uF.parseToInt(rs.getString("emp_id")) > 0 && !alEmp.contains(rs.getString("emp_id"))) {
										alEmp.add(rs.getString("emp_id"));
									}
								}
								
								alertData = "<div style=\"float: left;\"> <b>"+taskname[i]+"</b> in <b>"+hmTaskProData.get("PRO_NAME")+"</b> project has been allocated to your team by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
								alertAction = "ViewMyProjects.action";
								for(String strEmp : alEmp) {
									UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
									userAlerts.setStrDomain(strDomain);
									userAlerts.setStrEmpId(strEmp.trim());
									userAlerts.setStrData(alertData);
									userAlerts.setStrAction(alertAction);
//									userAlerts.set_type(TASK_ALLOCATE_ALERT);
									userAlerts.setStatus(INSERT_TR_ALERT);
									Thread t = new Thread(userAlerts);
									t.run();
								}
							}
							
						} else if(strReassign != null && strReassign.equals("2")) {
							pst = con.prepareStatement("UPDATE activity_info SET task_accept_status=0, reschedule_reassign_align_by=? WHERE task_id=?");
							pst.setInt(1, uF.parseToInt(strSessionEmpId));
							pst.setInt(2, uF.parseToInt(taskID[i]));
							pst.executeUpdate();
							pst.close();
						}
					} else {
						String strAddOrReq = "added_by";
						int isCustAdd = 0;
						if(strUserType!=null && strUserType.equals(CUSTOMER)) {
							strAddOrReq = "requested_by";
							isCustAdd = 1;
						}
						pst = con.prepareStatement("insert into activity_info (activity_name,priority,resource_ids,deadline,idealtime,start_date," +
							"dependency_task,dependency_type,color_code,taskstatus,pro_id,task_skill_id,task_description,"+strAddOrReq+",task_freq_name," +
							"recurring_task,task_accept_status,is_cust_add)" +
							" values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?) ");
						pst.setString(1, taskname[i]);
						pst.setString(2, priority[i]);
	//					pst.setInt(3, uF.parseToInt(emp_id[i]));
						pst.setString(3, sbEmpIds.toString());
						pst.setDate(4, uF.getDateFormat(deadline1[i], DATE_FORMAT));
						pst.setDouble(5, uF.parseToDouble(idealTime[i]));
						pst.setDate(6, uF.getDateFormat(startDate[i], DATE_FORMAT));
						pst.setInt(7, uF.parseToInt(dependency[i]));
						pst.setString(8, dependencyType[i]);
						pst.setString(9, colourCode[i]); 
						pst.setString(10, "New Task");
						pst.setInt(11, uF.parseToInt(getProId()));
	//					pst.setInt(12, uF.parseToInt(empSkills[i]));
						pst.setInt(12, 0);
						pst.setString(13, taskDescription[i]);
						pst.setInt(14, uF.parseToInt(strSessionEmpId));
						pst.setString(15, freqTaskName);
						pst.setInt(16, uF.parseToInt(isRecurringTask[i]));
						if(uF.parseToBoolean(forcedTask) && strUserType != null && !strUserType.equals(CUSTOMER)) {
							pst.setInt(17, 1);
						} else if(!uF.parseToBoolean(forcedTask) && strUserType != null && !strUserType.equals(CUSTOMER)) {
							pst.setInt(17, 0);
						} else {
							pst.setInt(17, -2);
						}
						pst.setInt(18, isCustAdd);
						pst.executeUpdate();
						pst.close();
					}
				}
				String strTaskId = taskID[i];
				if(uF.parseToInt(strTaskId) == 0) {
					pst = con.prepareStatement("select max(task_id) as task_id from activity_info");
					rs = pst.executeQuery();
					while (rs.next()) {
						strTaskId =	rs.getString("task_id");
					}
					rs.close();
					pst.close();
				}
				
				
				
				for (int k = 0; subTaskID != null && k < subTaskID.length; k++) {
					
					if(substartDate[k] != null && !substartDate[k].equals("") && !substartDate[k].equals("-") && subdeadline1[k] !=null && !subdeadline1[k].equals("") && !subdeadline1[k].equals("-")) {
						freqTaskName = uF.getDateFormat(substartDate[k], DATE_FORMAT, DATE_FORMAT_STR)+" to "+uF.getDateFormat(subdeadline1[k], DATE_FORMAT, DATE_FORMAT_STR);
					}
					
					StringBuilder taskResourceIds = new StringBuilder();
					pst = con.prepareStatement("select resource_ids from activity_info where parent_task_id=? and task_id != ?");
					pst.setInt(1, uF.parseToInt(strTaskId));
					pst.setInt(2, uF.parseToInt(subTaskID[k]));
//					System.out.println("pst ===>> " + pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						taskResourceIds.append(rs.getString("resource_ids"));
					}
					rs.close();
					pst.close();
					
					List<String> alTaskResources = Arrays.asList(taskResourceIds.toString().split(","));
					List<String> taskResources = new ArrayList<String>();
					for (int aa = 0; alTaskResources != null && aa < alTaskResources.size(); aa++) {
						if(!taskResources.contains(alTaskResources.get(aa)) && !alTaskResources.get(aa).equals("") && !alTaskResources.get(aa).equals("null")) {
							taskResources.add(alTaskResources.get(aa));
						}
					}
//					System.out.println("alTaskResources ===>> " + alTaskResources);
					
//					System.out.println("subTaskTRId[k] ===>> " + getTaskTRId()[i]+"_"+subTaskTRId[k]);
					String[] sub_emp_id = request.getParameterValues("sub_emp_id"+getProId()+"_"+taskTRId[i]+"_"+subTaskTRId[k]);
					
					StringBuilder sbSTEmpIds = null;
//					System.out.println("sub_emp_id ===>> " + sub_emp_id);
					if (sub_emp_id != null && sub_emp_id.length > 0) {
						List<String> empIdSTList = Arrays.asList(sub_emp_id);
						for (int a = 0; empIdSTList != null && a < empIdSTList.size(); a++) {
							if(!taskResources.contains(empIdSTList.get(a).trim())) {
								if(!empIdSTList.get(a).trim().equals("") && !empIdSTList.get(a).trim().equals("null")) {
									taskResources.add(empIdSTList.get(a).trim());
								}
							}
							
							if (sbSTEmpIds == null) {
								sbSTEmpIds = new StringBuilder();
								sbSTEmpIds.append("," + empIdSTList.get(a).trim()+",");
							} else {
								sbSTEmpIds.append(empIdSTList.get(a).trim()+",");
							}
						}
					}
					if (sbSTEmpIds == null) {
						sbSTEmpIds = new StringBuilder();
					}
					
					if((strUserType!=null && strUserType.equals(CUSTOMER) && uF.parseToInt(subTaskByCust[k]) == 1) || (strUserType!=null && !strUserType.equals(CUSTOMER))) {
						if(uF.parseToInt(subTaskID[k])>0) {
							
							int nProId = 0;
							pst = con.prepareStatement("select pro_id,resource_ids from activity_info where task_id=?");
							pst.setInt(1, uF.parseToInt(subTaskID[k]));
							rs = pst.executeQuery();
							String strResourceIds = null;
							while(rs.next()){
								nProId = rs.getInt("pro_id");
								strResourceIds = rs.getString("resource_ids");
							}
							rs.close();
							pst.close();
							
							List<String> alEmp = new ArrayList<String>();
							if(strResourceIds !=null && !strResourceIds.trim().equals("") && strResourceIds.contains(",")) {
								strResourceIds = strResourceIds.substring(1, strResourceIds.length()-1);
								List<String> alTemp = new ArrayList<String>();
								if (sub_emp_id != null && sub_emp_id.length > 0) {
									alTemp = Arrays.asList(sub_emp_id);
								}
								List<String> alResource = Arrays.asList(strResourceIds.split(","));
								for(String strEmp : alTemp) {
									if(!alResource.contains(strEmp.trim())) {
										alEmp.add(strEmp.trim());
									}
								}
							} else {
								if (emp_id != null && emp_id.length > 0) {
									alEmp = Arrays.asList(sub_emp_id);
								}
							}
							
							String strReassign = request.getParameter("subtaskActions"+getProId()+"_"+subTaskID[k]);
							if(strReassign == null || strReassign.equals("1")) {
								StringBuilder sbQuery = new StringBuilder();
								sbQuery.append("update activity_info set activity_name=?, priority=?, resource_ids=?, deadline=?, idealtime=?, start_date=?," +
									"dependency_task=?,dependency_type=?,color_code=?,taskstatus=?,task_skill_id=?,task_description=?," +
									"task_freq_name=?,recurring_task=? ");
								if(uF.parseToBoolean(forcedTask) && strUserType != null && !strUserType.equals(CUSTOMER) && (strReassign == null || strReassign.equals("1"))) {
									sbQuery.append(", task_accept_status=1 ");
								}
								if(strReassign != null && strReassign.equals("1")) {
									sbQuery.append(", task_accept_status=0, added_by="+uF.parseToInt(strSessionEmpId)+" ");
								}
								sbQuery.append(" where task_id =?");
								
								pst = con.prepareStatement(sbQuery.toString());
								pst.setString(1, subtaskname[k]);
								pst.setString(2, subpriority[k]);
		//						pst.setInt(3, uF.parseToInt(sub_emp_id[i]));
								pst.setString(3, sbSTEmpIds.toString());
								pst.setDate(4, uF.getDateFormat(subdeadline1[k], DATE_FORMAT));
								pst.setDouble(5, uF.parseToDouble(subidealTime[k]));
								pst.setDate(6, uF.getDateFormat(substartDate[k], DATE_FORMAT));
								pst.setInt(7, uF.parseToInt(subDependency[k]));
								pst.setString(8, subDependencyType[k]);
								pst.setString(9, subcolourCode[k]);
								pst.setString(10, "New Sub Task");
		//						pst.setInt(11, uF.parseToInt(empSubSkills[k]));
								pst.setInt(11, 0);
								pst.setString(12, subtaskDescription[k]);
	//							pst.setInt(13, uF.parseToInt(strSessionEmpId));
								pst.setString(13, freqTaskName);
								pst.setInt(14, uF.parseToInt(isRecurringSubTask[k]));
								pst.setInt(15, uF.parseToInt(subTaskID[k]));
								int x = pst.executeUpdate();
								pst.close();
								
								if(x > 0) {
									
									Map<String, String> hmTaskProData = CF.getTaskProInfo(con, subTaskID[k], null);
									
									for(int a=0; alEmp!=null && !alEmp.isEmpty() && a<alEmp.size(); a++) {
										Map<String, String> hmEmpData = hmEmpInfo.get(alEmp.get(a));
										String strDomain = request.getServerName().split("\\.")[0];
										Notifications nF = new Notifications(N_NEW_TASK_ASSIGN, CF); 
										nF.setDomain(strDomain);
										
										nF.request = request;
										nF.setStrOrgId(strSessionOrgId);
										nF.setEmailTemplate(true);
										
										nF.setStrEmpId(alEmp.get(a));
										nF.setStrResourceFName(hmEmpData.get("FNAME"));
										nF.setStrResourceLName(hmEmpData.get("LNAME"));
										nF.setStrTaskName(subtaskname[k]+" [ST]");
										nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
										nF.setStrProjectOwnerName(hmEmpName.get(hmTaskProData.get("PROJECT_OWNER_ID")));
										nF.setStrTeamLeader(hmTaskProData.get("TEAM_LEADER"));
										nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
										
										nF.setStrHostAddress(CF.getStrEmailLocalHost());
										nF.setStrHostPort(CF.getStrHostPort());
										nF.setStrContextPath(request.getContextPath());
										nF.sendNotifications();
									}
									
									String strDomain = request.getServerName().split("\\.")[0];
									String alertData = "<div style=\"float: left;\"> <b>"+subtaskname[k]+" [ST]</b> in <b>"+hmTaskProData.get("PRO_NAME")+"</b> project has been allocated to you by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
									String alertAction = "MyWork.action";
									for(String strEmp : alEmp) {
										UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
										userAlerts.setStrDomain(strDomain);
										userAlerts.setStrEmpId(strEmp.trim());
										userAlerts.setStrData(alertData);
										userAlerts.setStrAction(alertAction);
//										userAlerts.set_type(TASK_ALLOCATE_ALERT);
										userAlerts.setStatus(INSERT_TR_ALERT);
										Thread t = new Thread(userAlerts);
										t.run();
									}
									
									alEmp = new ArrayList<String>();
									pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=? and _isteamlead = true");
									pst.setInt(1, nProId);
									rs = pst.executeQuery();
									while(rs.next()){
										if(uF.parseToInt(rs.getString("emp_id")) > 0 && !alEmp.contains(rs.getString("emp_id"))) {
											alEmp.add(rs.getString("emp_id"));
										}
									}
									rs.close();
									pst.close();
									
									alertData = "<div style=\"float: left;\"> <b>"+subtaskname[k]+" [ST]</b> in <b>"+hmTaskProData.get("PRO_NAME")+"</b> project has been allocated to your team by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
									alertAction = "ViewMyProjects.action";
									for(String strEmp : alEmp) {
										UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
										userAlerts.setStrDomain(strDomain);
										userAlerts.setStrEmpId(strEmp.trim());
										userAlerts.setStrData(alertData);
										userAlerts.setStrAction(alertAction);
//										userAlerts.set_type(TASK_ALLOCATE_ALERT);
										userAlerts.setStatus(INSERT_TR_ALERT);
										Thread t = new Thread(userAlerts);
										t.run();
									}
								}
								
							} else if(strReassign != null && strReassign.equals("2")) {
								pst = con.prepareStatement("UPDATE activity_info SET task_accept_status=0, reschedule_reassign_align_by=? WHERE task_id=?");
								pst.setInt(1, uF.parseToInt(strSessionEmpId));
								pst.setInt(2, uF.parseToInt(subTaskID[k]));
								pst.executeUpdate();
								pst.close();
							}
						} else {
							String strAddOrReq = "added_by";
							int isCustAdd = 0;
							if(strUserType!=null && strUserType.equals(CUSTOMER)) {
								strAddOrReq = "requested_by";
								isCustAdd = 1;
							}
							pst = con.prepareStatement("insert into activity_info (activity_name,priority,resource_ids,deadline,idealtime,start_date," +
								"dependency_task,dependency_type,color_code,taskstatus,pro_id,parent_task_id,task_skill_id,task_description,"+strAddOrReq+"," +
								"task_freq_name,recurring_task,task_accept_status,is_cust_add) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?) ");
							pst.setString(1, subtaskname[k]);
							pst.setString(2, subpriority[k]);
	//						pst.setInt(3, uF.parseToInt(sub_emp_id[i]));
							pst.setString(3, sbSTEmpIds.toString());
							pst.setDate(4, uF.getDateFormat(subdeadline1[k], DATE_FORMAT));
							pst.setDouble(5, uF.parseToDouble(subidealTime[k]));
							pst.setDate(6, uF.getDateFormat(substartDate[k], DATE_FORMAT));
							pst.setInt(7, uF.parseToInt(subDependency[k]));
							pst.setString(8, subDependencyType[k]);
							pst.setString(9, subcolourCode[k]);
							pst.setString(10, "New Sub Task");
							pst.setInt(11, uF.parseToInt(getProId()));
							pst.setInt(12, uF.parseToInt(strTaskId));
	//						pst.setInt(13, uF.parseToInt(empSubSkills[k]));
							pst.setInt(13, 0);
							pst.setString(14, subtaskDescription[k]);
							pst.setInt(15, uF.parseToInt(strSessionEmpId));
							pst.setString(16, freqTaskName);
							pst.setInt(17, uF.parseToInt(isRecurringSubTask[k]));
							if(uF.parseToBoolean(forcedTask) && strUserType != null && !strUserType.equals(CUSTOMER)) {
								pst.setInt(18, 1);
							} else if(!uF.parseToBoolean(forcedTask) && strUserType != null && !strUserType.equals(CUSTOMER)) {
								pst.setInt(18, 0);
							} else {
								pst.setInt(18, -2);
							}
							pst.setInt(19, isCustAdd);
							pst.executeUpdate();
							pst.close();
						}
					}
					
					
					StringBuilder sbTEmpIds = null;
//					System.out.println("sub_emp_id ===>> " + sub_emp_id);
					for (int a = 0; taskResources != null && a < taskResources.size(); a++) {
						if(!taskResources.get(a).equals("")) {							
							if (sbTEmpIds == null) {
								sbTEmpIds = new StringBuilder();
								sbTEmpIds.append("," + taskResources.get(a)+",");
							} else {
								sbTEmpIds.append(taskResources.get(a)+",");
							}
						}
					}
					if (sbTEmpIds == null) {
						sbTEmpIds = new StringBuilder();
					}
//					System.out.println("sbTEmpIds ===>> " + sbTEmpIds.toString());
					
					pst = con.prepareStatement("update activity_info set resource_ids=? where task_id=?");
					pst.setString(1, sbTEmpIds.toString());
					pst.setInt(2, uF.parseToInt(strTaskId));
					pst.executeUpdate();
					
//					System.out.println("ASDFG ====>> "+ i + " " + taskID[i] + " " + taskname[i] + " " + subTaskID[k] + " " + subtaskname[k] + " " + subDependency[k]);
//					System.out.println( i + " " + taskID[i] + " " + taskname[i] + " " + subTaskID[k]+ " " + subtaskname[k]+ " " + subDependency[k] + " " + subDependencyType[k] + " " + subpriority[k] 
//					     + " " + sub_emp_id[k] + " " + substartDate[k] + " " + subdeadline1[k] + " " + subidealTime[k] + " " + subcolourCode[k]);
				}
//				
				double dblAllCompleted = 0.0d;
				int subTaskCnt = 0;
				pst = con.prepareStatement("select sum(completed) as completed, count(task_id) as count from activity_info where parent_task_id = ? and task_accept_status = 1");
				pst.setInt(1, uF.parseToInt(strTaskId));
//				pst.setInt(2, uF.parseToInt(taskId));
				rs = pst.executeQuery();
				while(rs.next()) {
					dblAllCompleted = rs.getDouble("completed");
					subTaskCnt = rs.getInt("count");
				}
				rs.close();
				pst.close();
				
				double avgComplted = 0.0d;
				if(dblAllCompleted > 0 && subTaskCnt > 0) {
					avgComplted = dblAllCompleted / subTaskCnt;
				}
				
				if(avgComplted > 0) {
					pst = con.prepareStatement("update activity_info set completed = ? where task_id = ?");
					pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(avgComplted)));
					pst.setInt(2, uF.parseToInt(strTaskId));
					pst.execute();
					pst.close();
				}
			}
			
			pst = con.prepareStatement("select sum(completed)/count(task_id) as avrg from activity_info where pro_id=? and parent_task_id = 0 and task_accept_status = 1");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			String projectCompletePercent = null;
			while (rs.next()) {
				projectCompletePercent = uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rs.getString("avrg")));
			}
			rs.close();
			pst.close();
	
			pst = con.prepareStatement("update projectmntnc set completed=? where pro_id=? ");
			pst.setDouble(1, uF.parseToDouble(projectCompletePercent));
			pst.setInt(2, uF.parseToInt(getProId()));
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void deleteProject() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("DELETE FROM work_flow_details where effective_id in (select task_id FROM task_activity WHERE activity_id in (select task_id from activity_info WHERE pro_id=?))");
			pst.setInt(1,ID);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM task_activity WHERE activity_id in (select task_id from activity_info WHERE pro_id=?)");
			pst.setInt(1,ID);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM projectmntnc WHERE pro_id=?");
			pst.setInt(1,ID);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM activity_info WHERE pro_id=?");
			pst.setInt(1,ID);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM project_skill_details WHERE pro_id=?");
			pst.setInt(1,ID);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM project_emp_details WHERE pro_id=?");
			pst.setInt(1,ID);
			pst.executeUpdate(); 
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM project_document_details WHERE pro_id=?");
			pst.setInt(1,ID);
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM project_milestone_details WHERE pro_id=?");
			pst.setInt(1,ID);
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
//	public void monthlyProjects() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		try {
//			con = db.makeConnection(con);
//			Map<String, List<String>> hmMonthlyProject = new HashMap<String, List<String>>();
//			
//			Map<String, String> hmServicemap = CF.getProjectServicesMap(con, true);
//			
//			Map<String, String> hmClientMap = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from client_details");
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmClientMap.put(rs.getString("client_id"), rs.getString("client_name"));
//			}
//			rs.close();
//			pst.close();
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
//				pst = con.prepareStatement("select * from projectmntnc where ismonthly=? and added_by = ?");
//				pst.setBoolean(1,true);
//				pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
//			} else {
//				pst = con.prepareStatement("select * from projectmntnc where ismonthly=?");
//				pst.setBoolean(1,true);
//			}
//			
//			rs = pst.executeQuery();
//			
//			GetPriorityList objGP = new GetPriorityList();
//			
//			while(rs.next()) {
//				
//				List<String> alInner = new ArrayList<String>();
//				
//				alInner.add(rs.getString("pro_id"));
//				alInner.add(rs.getString("pro_name"));
//				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), ""));
//				
//				alInner.add(uF.showData(hmServicemap.get(rs.getString("service")), ""));
//				alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
//				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
//				alInner.add(uF.showData(rs.getString("idealtime"), ""));
//
//				alInner.add(uF.showData(hmClientMap.get(rs.getString("client_id")), ""));
//				
//				hmMonthlyProject.put(rs.getString("pro_id"), alInner);
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("hmMonthlyProject", hmMonthlyProject);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
	
	public void getActivity(Connection con, UtilityFunctions uF, String proIds, int proId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmCustName = CF.getCustomerNameMap(con);
			Map<String, String> hmSubTaskCountOfTask = getSubTaskCountOfTask(con, uF);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select ai.*, pmc.start_date as pmc_start_date, pmc.deadline as pmc_deadline, pmc.bill_days_type, pmc.hours_for_bill_day, " +
				"pmc.actual_calculation_type, pmc.billing_type from activity_info ai, projectmntnc pmc where ai.pro_id=pmc.pro_id and " +
				"ai.parent_task_id = 0 ");
			if(strUserType != null && !strUserType.equals(CUSTOMER)) {
				sbQuery.append(" and (ai.task_accept_status >=0 or ai.task_accept_status = -2) ");
			} else {
				sbQuery.append(" and (ai.task_accept_status =1 or ai.task_accept_status = -2) ");
			}
			if(proIds != null && !proIds.equals("")) {
				sbQuery.append(" and pmc.pro_id in ("+proIds+")");
			} else if(proId > 0) {
				sbQuery.append(" and pmc.pro_id in ("+proId+")");
			}
			sbQuery.append(" order by ai.start_date, ai.task_id ");
			pst = con.prepareStatement(sbQuery.toString()); //and ai.pro_id=?
//			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======main==="+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, List<String>>> hmProWiseTasks = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, List<String>> hmProTasks = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmProTaskCount = new HashMap<String, String>();
//			Map<String, String> hmProTaskCompletePercent = new HashMap<String, String>();
//			Map<String, String> hmProTakenTime = new HashMap<String, String>();
			int taskAndSubtaskCount = 0;
			
			while (rs.next()) {
				dependencyList = new FillDependentTaskList(request).fillDependentTaskList(uF.parseToInt(rs.getString("pro_id")));
				dependancyTypeList = new GetDependancyTypeList().fillDependancyTypeList();
				priorityList = new GetPriorityList().fillPriorityList();
				TaskEmpNamesList = new FillTaskEmpList(request).fillEmployeeName(uF.parseToInt(rs.getString("pro_id")));
				String skillIds = getProjectEmpSkillIds(uF, uF.parseToInt(rs.getString("pro_id"))); 
				empSkillList = new FillSkills(request).fillSkillNameByIds(skillIds);
			
//				double taskCompPercent = uF.parseToDouble(hmProTaskCompletePercent.get(rs.getString("pro_id")));
//				taskCompPercent = taskCompPercent + uF.parseToDouble(rs.getString("completed"));
//				hmProTaskCompletePercent.put(rs.getString("pro_id"), taskCompPercent+"");
				
				int taskCount = uF.parseToInt(hmProTaskCount.get(rs.getString("pro_id")));
					taskCount++;
				hmProTaskCount.put(rs.getString("pro_id"), taskCount+"");
				
//				double proTakenTime = uF.parseToDouble(hmProTakenTime.get(rs.getString("pro_id")+"_TIME"));
				
				hmProTasks = hmProWiseTasks.get(rs.getString("pro_id"));
				if(hmProTasks == null) hmProTasks = new LinkedHashMap<String, List<String>>();
				
				String taskActivityCnt = CF.getTaskActivityTaskCount(con, uF, rs.getString("task_id"));
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("task_id")); //0
				alInner.add(rs.getString("parent_task_id"));
				alInner.add(rs.getString("pro_id")); //2
				alInner.add(uF.showData(rs.getString("activity_name"), "")); //3
				String dependencyTask = getDependencyTaskOptions(rs.getString("task_id"), rs.getString("dependency_task"), dependencyList);
				alInner.add(uF.showData(dependencyTask, "")); //4
				alInner.add(uF.showData(rs.getString("dependency_type"), "")); //5
				alInner.add(uF.showData(rs.getString("priority"), "")); //6
				String empSkills = getProEmpSkills(rs.getString("task_skill_id"), empSkillList);
				alInner.add(uF.showData(empSkills, "")); //7
				if(uF.parseToBoolean(rs.getString("task_from_my_self"))) {
					String strRescId = null;
					if(rs.getString("resource_ids") != null && rs.getString("resource_ids").length()>1) {
						strRescId = rs.getString("resource_ids").substring(1, rs.getString("resource_ids").length()-1);
					}
					alInner.add(uF.showData(hmEmpName.get(strRescId), "-")+"::::"+uF.showData(strRescId, "0"));//8
				} else {
					String taskEmps = getTaskEmployee(rs.getString("resource_ids"), TaskEmpNamesList);
					alInner.add(uF.showData(taskEmps, ""));//8
				}
//				String taskEmps = getTaskEmployee(rs.getString("resource_ids"), TaskEmpNamesList); 
//				alInner.add(uF.showData(taskEmps, "")); //8
				alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT)); //9
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT)); //10
				alInner.add(uF.showData(rs.getString("idealtime"), "")); //11
				alInner.add(uF.showData(rs.getString("color_code"), "")); //12
				alInner.add(uF.showData(taskActivityCnt, "")); //13
				String timeFilledEmp = getTimesheetFilledEmp(con, rs.getString("resource_ids"), rs.getString("task_id"));
				alInner.add(uF.showData(timeFilledEmp, "")); //14
				String subTaskCnt = hmSubTaskCountOfTask.get(rs.getString("task_id"));
				alInner.add(uF.showData(subTaskCnt, "")); //15
				alInner.add(uF.showData(rs.getString("completed"), "0")); //16
				
				String strColour = null;
				String strdaysColour = null;
				String strworkingColour = null;
				double no_of_hrs=0.0;
				double no_of_days=0.0;
				Map<String, String> hmProjectData = new HashMap<String, String>();
				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("pmc_start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("pmc_deadline"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
				
				Map<String, String> hmTaskAWDaysAndHrs = CF.getProjectTaskActualWorkedDaysAndHrs(con, rs.getString("task_id"), hmProjectData);
				
				double dblCompleted = 0;
				double dblIdealTime = 0;
				double dblAlreadyWorked = 0;

				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_DAYS"));
				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_DAYS"));
				} else {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_HRS"));
				}
				double idealPercent = 0;
				if(dblIdealTime > 0) {
					idealPercent = (dblAlreadyWorked / dblIdealTime) * 100;
				}
				
				if(idealPercent == dblCompleted) {
					strworkingColour = "yellow";
				} else if(idealPercent > dblCompleted) {
					strworkingColour = "red";
				} else {
					strworkingColour = "green";
				}
				
				double taskDealineCompletePercent = getTaskDeadlineCompletePercent(uF.parseToInt(rs.getString("task_id")));
				String tdcpSpan = "";
				if(taskDealineCompletePercent <= 50) {
					 /*tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/approved.png\"></span>";*/
					tdcpSpan = "<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Working\"></i></span>";
					
				} else if(taskDealineCompletePercent > 50 && taskDealineCompletePercent <= 75) {
					/*tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/re_submit.png\"></span>";*/
					tdcpSpan = "<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i></span>";
					
				} else if(taskDealineCompletePercent > 75) {
					/*tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/denied.png\"></span>";*/
					tdcpSpan = "<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Working\"></i></span>";
					
				} else {
					/*tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/pending.png\"></span>";*/
					tdcpSpan = "<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i></span>";
					
				}
				
				if(dblCompleted > 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
					no_of_days=getNoOfDays(uF.parseToInt(rs.getString("task_id")));
				}
//				System.out.println("dateDiff  ==>>> " + uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE)));
				if(dblCompleted >= 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
					
					if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("H")) {
						no_of_hrs = dblAlreadyWorked - dblIdealTime;
					} else {
						no_of_hrs = (dblAlreadyWorked/30) - dblIdealTime;
					}
//					System.out.println("no_of_hrs ===>> " + no_of_hrs);
				}
				
				if(uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"), DBDATE))>=0.0 && uF.parseToDouble(uF.dateDifference(rs.getString("end_date"), DBDATE, rs.getString("deadline"),DBDATE))>=0.0) {
					strdaysColour="green";
				} else {
					strdaysColour="red";
				}
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
					if((dblAlreadyWorked/30)>dblIdealTime) {
						strColour="red";
					} else {
						strColour="green";
					}
				} else {
					if(dblAlreadyWorked>dblIdealTime) {
						strColour="red";
					} else {
						strColour="green";
					}
				}
				String daySpan="none";
				String timeSpan = "none";
				String timeSpanLbl = " hrs";
				String strTime = uF.getTotalTimeMinutes100To60(""+no_of_hrs);
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					daySpan="inline";
				} else {
					if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
						timeSpanLbl = " months";
						strTime = uF.formatIntoTwoDecimal(no_of_hrs);
					}
					timeSpan="inline";
				}
				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
				
//				System.out.println(rs.getString("activity_name")+" --- taskstatus ===>> " + rs.getString("taskstatus") +" --- completed ===>> " + rs.getInt("completed") +" --- approve_status ===>> " +rs.getString("approve_status"));
				
				if(rs.getString("taskstatus") != null && rs.getString("taskstatus").equalsIgnoreCase("New Task")) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"margin-right: 5px;\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Confirmed\" ></i></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b> "+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
//					} else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
//						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("");
//						alInner.add("<span style=\"color:orange\">Planned</span>");
					}
					
				} else if(dtCurrentDate!=null && dtDeadlineDate!=null && dtCurrentDate.after(dtDeadlineDate)) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"margin-right: 5px;\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Confirmed\" ></i></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>days</span>)");
					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
//					} else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
//						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
//						alInner.add("<span style=\"color:"+strColour+"\">Working</span> (<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					}
					
				} else if(rs.getInt("completed")>=100) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status") != null && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"color:#5D862B\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Confirmed\" ></i></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					}
				} else {
//					if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
//						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
//					} else 
						if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("");
					}
				}
				
//				String timeType = "hours";
//				double dblTaskTakenTme = 0;
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					timeType = "days";
					alInner.add(uF.formatIntoTwoDecimal(dblAlreadyWorked)+" days"); //19
//					dblTaskTakenTme = dblAlreadyWorked;
				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
//					timeType = "months";
//					System.out.println("activity_name ===>> " + rs.getString("activity_name") + " dblAlreadyWorked ===>> " + dblAlreadyWorked);
					double alreadyWorkedMnths = dblAlreadyWorked / 30;
					if(alreadyWorkedMnths < 0) 
						alreadyWorkedMnths = 0;
//					dblTaskTakenTme = alreadyWorkedMnths;
					alInner.add(uF.formatIntoTwoDecimal(alreadyWorkedMnths)+" months"); //19
				} else {
//					timeType = "hours";
//					dblTaskTakenTme = dblAlreadyWorked;
//					System.out.println(rs.getString("activity_name")+" --- dblAlreadyWorked ===>> " + dblAlreadyWorked);
//					System.out.println("getTotalTimeMinutes100To60(dblAlreadyWorked) ===>> " + uF.getTotalTimeMinutes100To60(""+dblAlreadyWorked));
					alInner.add(uF.getTotalTimeMinutes100To60(""+dblAlreadyWorked)+" hrs"); //19
				}
				alInner.add(uF.showData(rs.getString("approve_status"), "")); //20
				alInner.add(uF.showData(rs.getString("task_description"), "")); //21
				alInner.add(uF.showData(hmEmpName.get(rs.getString("added_by")), "-")); //22
				alInner.add(uF.showData(rs.getString("task_freq_name"), "-")); //23
				alInner.add(uF.showData(rs.getString("recurring_task"), "0")); //24
				alInner.add(uF.showData(rs.getString("task_accept_status"), "0")); //25
				alInner.add(uF.showData(rs.getString("task_reassign_reschedule_comment"), "-")); //26
				alInner.add(rs.getString("task_from_my_self")); //27
				if(uF.parseToInt(rs.getString("is_cust_add")) == 1) {
					alInner.add(uF.showData(hmCustName.get(rs.getString("requested_by")), "")); //28
				} else {
					alInner.add(uF.showData(hmEmpName.get(rs.getString("requested_by")), "")); //28
				}
				alInner.add(uF.showData(rs.getString("is_cust_add"), "-")); //29
				
//				proTaskList.add(alInner);
				
//				proTakenTime = proTakenTime + dblTaskTakenTme;
//				hmProTakenTime.put(rs.getString("pro_id")+"_TIME", proTakenTime+"");
//				hmProTakenTime.put(rs.getString("pro_id")+"_TIME_TYPE", timeType);
				
				hmProTasks.put(rs.getString("task_id"), alInner);
				hmProWiseTasks.put(rs.getString("pro_id"), hmProTasks);
				taskAndSubtaskCount++;
				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProWiseTasks", hmProWiseTasks);
			request.setAttribute("hmProTaskCount", hmProTaskCount);
//			request.setAttribute("hmProTaskCompletePercent", hmProTaskCompletePercent);
//			request.setAttribute("hmProTakenTime", hmProTakenTime);
			
			
			StringBuilder sbSQuery = new StringBuilder();
			sbSQuery.append("select pro_id from projectmntnc pmc where pmc.pro_id > 0 ");
			if(proIds != null && !proIds.equals("")) {
				sbSQuery.append(" and pmc.pro_id in ("+proIds+")");
			} else if(proId > 0) {
				sbSQuery.append(" and pmc.pro_id in ("+proId+")");
			}
			pst = con.prepareStatement(sbSQuery.toString());
			//	System.out.println("pst======main==="+pst);
			rs = pst.executeQuery();
			Map<String, String> hmProTaskDependency = new HashMap<String, String>();
			Map<String, String> hmProEmpSkills = new HashMap<String, String>();
			Map<String, String> hmProTeamEmp = new HashMap<String, String>();
			while (rs.next()) {
				dependencyList = new FillDependentTaskList(request).fillDependentTaskList(uF.parseToInt(rs.getString("pro_id")));
				dependancyTypeList = new GetDependancyTypeList().fillDependancyTypeList();
				priorityList = new GetPriorityList().fillPriorityList();
				TaskEmpNamesList = new FillTaskEmpList(request).fillEmployeeName(uF.parseToInt(rs.getString("pro_id")));
				String skillIds = getProjectEmpSkillIds(uF, uF.parseToInt(rs.getString("pro_id"))); 
				empSkillList = new FillSkills(request).fillSkillNameByIds(skillIds);
				
				StringBuilder sbTaskDependencyList = new StringBuilder();
				for(int i=0; dependencyList!=null && i<dependencyList.size(); i++) {
					sbTaskDependencyList.append("<option value='"+dependencyList.get(i).getDependencyId()+"'>"+dependencyList.get(i).getDependencyName()+"</option>");
				}
				hmProTaskDependency.put(rs.getString("pro_id"), sbTaskDependencyList.toString());
				
				StringBuilder sbTaskEmpList = new StringBuilder();
				for(int i=0; TaskEmpNamesList!=null && i<TaskEmpNamesList.size(); i++) {
					sbTaskEmpList.append("<option value='"+TaskEmpNamesList.get(i).getTaskEmployeeId()+"'>"+TaskEmpNamesList.get(i).getTaskEmployeeName()+"</option>");
				}
				hmProTeamEmp.put(rs.getString("pro_id"), sbTaskEmpList.toString());
				
				StringBuilder sbEmpSkillList = new StringBuilder();
				for(int i=0; empSkillList!=null && i<empSkillList.size(); i++) {
					sbEmpSkillList.append("<option value='"+empSkillList.get(i).getSkillsId()+"'>"+empSkillList.get(i).getSkillsName()+"</option>");
				}
				hmProEmpSkills.put(rs.getString("pro_id"), sbEmpSkillList.toString());
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProTaskDependency", hmProTaskDependency);
			request.setAttribute("hmProTeamEmp", hmProTeamEmp);
			request.setAttribute("hmProEmpSkills", hmProEmpSkills);
			
			
			StringBuilder sbStQuery = new StringBuilder();
			sbStQuery.append("select ai.* from activity_info ai, projectmntnc pmc where ai.pro_id=pmc.pro_id and ai.parent_task_id = 0 and ai.task_accept_status != -1");
			if(proIds != null && !proIds.equals("")) {
				sbStQuery.append(" and pmc.pro_id in ("+proIds+")");
			} else if(proId > 0) {
				sbStQuery.append(" and pmc.pro_id in ("+proId+")");
			}
			pst = con.prepareStatement(sbStQuery.toString());
			rs = pst.executeQuery();
			Map<String, String> hmProSubTaskDependency = new HashMap<String, String>();
			while (rs.next()) {
				List<FillDependentTaskList> subDependencyList = new FillDependentTaskList(request).fillDependentSubTaskList(uF.parseToInt(rs.getString("pro_id")), uF.parseToInt(rs.getString("task_id")));
				String subDependencyTask = getDependencyTaskOptions("0", "0", subDependencyList);

				hmProSubTaskDependency.put(rs.getString("pro_id")+"_"+rs.getString("task_id"), subDependencyTask);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProSubTaskDependency", hmProSubTaskDependency);
			
			
			StringBuilder sbSTQuery = new StringBuilder();
			sbSTQuery.append("select ai.*, pmc.start_date as pmc_start_date, pmc.deadline as pmc_deadline, pmc.bill_days_type, pmc.hours_for_bill_day, " +
					"pmc.actual_calculation_type, pmc.billing_type from activity_info ai, projectmntnc pmc where ai.pro_id=pmc.pro_id and " +
					"ai.parent_task_id != 0 ");
			if(strUserType != null && !strUserType.equals(CUSTOMER)) {
				sbQuery.append(" and (ai.task_accept_status >=0 or ai.task_accept_status = -2) ");
			} else {
				sbQuery.append(" and (ai.task_accept_status =1 or ai.task_accept_status = -2) ");
			}
			if(proIds != null && !proIds.equals("")) {
				sbSTQuery.append(" and pmc.pro_id in ("+proIds+")");
			} else if(proId > 0) {
				sbSTQuery.append(" and pmc.pro_id in ("+proId+")");
			}
			sbSTQuery.append(" order by ai.start_date, ai.task_id ");
			pst = con.prepareStatement(sbSTQuery.toString()); //and ai.pro_id=?
//			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======main==="+pst);
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmProSubTasks = new LinkedHashMap<String, List<List<String>>>();
			
			List<List<String>> proSubTaskList = new ArrayList<List<String>>();
			while (rs.next()) {
				proSubTaskList = hmProSubTasks.get(rs.getString("pro_id")+"_"+rs.getString("parent_task_id"));
				if(proSubTaskList == null) proSubTaskList = new ArrayList<List<String>>();
				
				dependencyList = new FillDependentTaskList(request).fillDependentTaskList(uF.parseToInt(rs.getString("pro_id")));
				dependancyTypeList = new GetDependancyTypeList().fillDependancyTypeList();
				priorityList = new GetPriorityList().fillPriorityList();
				TaskEmpNamesList = new FillTaskEmpList(request).fillEmployeeName(uF.parseToInt(rs.getString("pro_id")));
				String skillIds = getProjectEmpSkillIds(uF, uF.parseToInt(rs.getString("pro_id"))); 
				empSkillList = new FillSkills(request).fillSkillNameByIds(skillIds);
				
				List<String> alInner = new ArrayList<String>();
				List<FillDependentTaskList> subDependencyList = new FillDependentTaskList(request).fillDependentSubTaskList(uF.parseToInt(rs.getString("pro_id")), uF.parseToInt(rs.getString("parent_task_id")));
				String subDependencyTask = getDependencyTaskOptions(rs.getString("task_id"), rs.getString("dependency_task"), subDependencyList);
				
				String taskActivityCnt = CF.getTaskActivityTaskCount(con, uF, rs.getString("task_id"));
				alInner.add(rs.getString("task_id")); //0
				alInner.add(rs.getString("parent_task_id")); //1
				alInner.add(rs.getString("pro_id")); //2
				alInner.add(uF.showData(rs.getString("activity_name"), "")); //3
				alInner.add(uF.showData(subDependencyTask, "")); //4
				alInner.add(uF.showData(rs.getString("dependency_type"), "")); //5
				alInner.add(uF.showData(rs.getString("priority"), "")); //6
//				alInner.add(uF.showData(rs.getString("task_skill_id"), "")); //7
				String empSkills = getProEmpSkills(rs.getString("task_skill_id"), empSkillList);
				alInner.add(uF.showData(empSkills, "")); //7
				if(uF.parseToBoolean(rs.getString("task_from_my_self"))) {
					String strRescId = null;
					if(rs.getString("resource_ids") != null && rs.getString("resource_ids").length()>1) {
						strRescId = rs.getString("resource_ids").substring(1, rs.getString("resource_ids").length()-1);
					}
					alInner.add(uF.showData(hmEmpName.get(strRescId), "-")+"::::"+uF.showData(strRescId, "0"));//8
				} else {
					String taskEmps = getTaskEmployee(rs.getString("resource_ids"), TaskEmpNamesList);
					alInner.add(uF.showData(taskEmps, ""));//8
				}
				alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));//9
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT)); //10
				alInner.add(uF.showData(rs.getString("idealtime"), "")); //11
				alInner.add(uF.showData(rs.getString("color_code"), "")); //12
				alInner.add(uF.showData(taskActivityCnt, "")); //13
				String timeFilledEmp = getTimesheetFilledEmp(con, rs.getString("resource_ids"), rs.getString("task_id"));
				alInner.add(uF.showData(timeFilledEmp, "")); //14
				alInner.add(uF.showData(rs.getString("completed"), "0")); //15
				
				
				String strColour = null;
				String strdaysColour = null;
				String strworkingColour = null;
				double no_of_hrs=0.0;
				double no_of_days=0.0;
				Map<String, String> hmProjectData = new HashMap<String, String>();
				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("pmc_start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("pmc_deadline"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
				
				Map<String, String> hmTaskAWDaysAndHrs = CF.getProjectTaskActualWorkedDaysAndHrs(con, rs.getString("task_id"), hmProjectData);
				
				double dblCompleted = 0;
				double dblIdealTime = 0;
				double dblAlreadyWorked = 0;

				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_DAYS"));
				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_DAYS"));
				} else {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_HRS"));
				}
				double idealPercent = 0;
				if(dblIdealTime > 0) {
					idealPercent = (dblAlreadyWorked / dblIdealTime) * 100;
				}
				
				if(idealPercent == dblCompleted) {
					strworkingColour = "yellow";
				} else if(idealPercent > dblCompleted) {
					strworkingColour = "red";
				} else {
					strworkingColour = "green";
				}
				
				double taskDealineCompletePercent = getTaskDeadlineCompletePercent(uF.parseToInt(rs.getString("task_id")));
				String tdcpSpan = "";
				if(taskDealineCompletePercent <= 50) {
					 //tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/approved.png\"></span>";
					tdcpSpan = "<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Working\"></i></span>";
					
				} else if(taskDealineCompletePercent > 50 && taskDealineCompletePercent <= 75) {
					/*tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/re_submit.png\"></span>";*/
					tdcpSpan = "<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i></span>";
					
				} else if(taskDealineCompletePercent > 75) {
					/*tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/denied.png\"></span>";*/
					tdcpSpan = "<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Working\"></i></span>";
					
				} else {
					/*tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"Working\" src=\"images1/icons/pending.png\"></span>";*/
					tdcpSpan = "<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Working\"></i></span>";
					
				}
				
				if(dblCompleted > 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
					no_of_days=getNoOfDays(uF.parseToInt(rs.getString("task_id")));
				}
				if(dblCompleted >= 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
					if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("H")) {
						no_of_hrs = dblAlreadyWorked - dblIdealTime;
					} else {
						no_of_hrs = (dblAlreadyWorked/30) - dblIdealTime;
					}
				}
				
				if(uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))>=0.0 && uF.parseToDouble(uF.dateDifference(rs.getString("end_date"), DBDATE, rs.getString("deadline"),DBDATE))>=0.0) {
					strdaysColour="green";
				} else {
					strdaysColour="red";
				}
				if(dblAlreadyWorked>dblIdealTime) {
					strColour="red";
				} else {
					strColour="green";
				}
				
				String daySpan="none";
				String timeSpan = "none";
				String timeSpanLbl = " hrs";
				String strTime = uF.getTotalTimeMinutes100To60(""+no_of_hrs);
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					daySpan="inline";
				} else {
					if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
						timeSpanLbl = " months";
						strTime = uF.formatIntoTwoDecimal(no_of_hrs);
					}
					timeSpan="inline";
				}
				
				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
				
				if(rs.getString("taskstatus") != null && rs.getString("taskstatus").equalsIgnoreCase("New Task")) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"margin-right: 5px;\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Confirmed\" ></i></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b> "+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
//					} else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
//						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("");
//						alInner.add("<span style=\"color:orange\">Planned</span>");
					}
					
				} else if(dtCurrentDate!=null && dtDeadlineDate!=null && dtCurrentDate.after(dtDeadlineDate)) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"margin-right: 5px;\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Confirmed\" ></i></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>days</span>)");
					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
//					} else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
//						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
//						alInner.add("<span style=\"color:"+strColour+"\">Working</span> (<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					}
					
				} else if(rs.getInt("completed")>=100) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status") != null && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"color:#5D862B\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Confirmed\" ></i></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					}
				} else {
//					if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
//						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
//					} else 
						if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/act_now.png\"></span>");
						alInner.add("");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("");
					}
				}
				
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					alInner.add(uF.formatIntoOneDecimal(dblAlreadyWorked)+" days"); //18
				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
					double alreadyWorkedMnths = dblAlreadyWorked / 30;
					if(alreadyWorkedMnths < 0) 
						alreadyWorkedMnths = 0;
					alInner.add(uF.formatIntoOneDecimal(alreadyWorkedMnths)+" months"); //18
				} else {
					alInner.add(uF.getTotalTimeMinutes100To60(""+dblAlreadyWorked)+" hrs"); //18
				}
				alInner.add(uF.showData(rs.getString("approve_status"), "")); //19
				alInner.add(uF.showData(rs.getString("task_description"), "")); //20
				alInner.add(uF.showData(hmEmpName.get(rs.getString("added_by")), "-")); //21
				alInner.add(uF.showData(rs.getString("task_freq_name"), "-")); //22
				alInner.add(uF.showData(rs.getString("recurring_task"), "0")); //23
				alInner.add(uF.showData(rs.getString("task_accept_status"), "0")); //24
				alInner.add(uF.showData(rs.getString("task_reassign_reschedule_comment"), "-")); //25
				alInner.add(rs.getString("task_from_my_self")); //26
				if(uF.parseToInt(rs.getString("is_cust_add")) == 1) {
					alInner.add(uF.showData(hmCustName.get(rs.getString("requested_by")), "")); //27
				} else {
					alInner.add(uF.showData(hmEmpName.get(rs.getString("requested_by")), "")); //27
				}
				alInner.add(uF.showData(rs.getString("is_cust_add"), "-")); //28
				
				proSubTaskList.add(alInner);
				
				hmProSubTasks.put(rs.getString("pro_id")+"_"+rs.getString("parent_task_id"), proSubTaskList);
				taskAndSubtaskCount++;
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProSubTasks", hmProSubTasks);
			request.setAttribute("taskAndSubtaskCount", taskAndSubtaskCount);
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
	}

	
	private String getProEmpSkills(String taskSkillId, List<FillSkills> empSkills) {
		StringBuilder sbTaskOptions = new StringBuilder();
		
		for(int i=0; empSkills != null && i<empSkills.size(); i++) {
			if(empSkills.get(i).getSkillsId().equals(taskSkillId)) {
				sbTaskOptions.append("<option value='"+empSkills.get(i).getSkillsId()+"' selected>"+empSkills.get(i).getSkillsName()+"</option>");
			} else {
				sbTaskOptions.append("<option value='"+empSkills.get(i).getSkillsId()+"'>"+empSkills.get(i).getSkillsName()+"</option>");
			}
		}
//		System.out.println("sbTaskOptions ====>>> " + sbTaskOptions.toString());
		return sbTaskOptions.toString();
	}


	private String getTaskEmployee(String resourceIds, List<FillTaskEmpList> taskEmpNamesList) {
		StringBuilder sbTaskEmps = new StringBuilder();
		
		List<String> addedEmpIds = new ArrayList<String>();
		for(int i=0; taskEmpNamesList != null && i<taskEmpNamesList.size(); i++) {
			List<String> alResources = new ArrayList<String>();
			if(resourceIds != null) {
				alResources = Arrays.asList(resourceIds.split(","));
			}
			boolean flag = false;
				for(int a=0; alResources != null && a<alResources.size(); a++) {
					if(!addedEmpIds.contains(taskEmpNamesList.get(i).getTaskEmployeeId()) && taskEmpNamesList.get(i).getTaskEmployeeId().equals(alResources.get(a))) {
						addedEmpIds.add(taskEmpNamesList.get(i).getTaskEmployeeId());
						sbTaskEmps.append("<option value='"+taskEmpNamesList.get(i).getTaskEmployeeId()+"' selected>"+taskEmpNamesList.get(i).getTaskEmployeeName()+"</option>");
						flag = true;
					}
				}
				if(!flag) {
					addedEmpIds.add(taskEmpNamesList.get(i).getTaskEmployeeId());
					sbTaskEmps.append("<option value='"+taskEmpNamesList.get(i).getTaskEmployeeId()+"'>"+taskEmpNamesList.get(i).getTaskEmployeeName()+"</option>");
				}
		}
//		System.out.println("sbTaskEmps ====>>> " + sbTaskEmps.toString());
		return sbTaskEmps.toString();
	}
	
	
	private Map<String, String> getSubTaskCountOfTask(Connection con, UtilityFunctions uF) { //, String taskId
//		String subTaskCnt = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmSubTaskCountOfTask = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select count(task_id) as stCnt, parent_task_id from activity_info where task_accept_status != -1 group by parent_task_id"); //where parent_task_id = ?
//			pst.setInt(1, uF.parseToInt(taskId));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmSubTaskCountOfTask.put(rs.getString("parent_task_id"), rs.getString("stCnt"));
//				subTaskCnt = rs.getString("stCnt");
			}
			rs.close();
			pst.close();
//			System.out.println("PROJECT_NAME ===>> " + request.getAttribute("PROJECT_NAME"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return hmSubTaskCountOfTask;
	}
	
	
	private String getTimesheetFilledEmp(Connection con, String resourceIds, String taskId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder timeFilledEmp = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			if(resourceIds != null && !resourceIds.trim().equals("") && resourceIds.length() > 1) {
//				System.out.println("resourceIds ===>> " + resourceIds);
				resourceIds = resourceIds.trim().substring(1, resourceIds.length()-1);
				if(!resourceIds.trim().equals("") && resourceIds.length() > 0) {
					pst = con.prepareStatement("select * from task_activity where emp_id in("+resourceIds+") and activity_id=?");
					pst.setInt(1, uF.parseToInt(taskId));
//					System.out.println("pst======main===" + pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						if(timeFilledEmp == null) {
							timeFilledEmp = new StringBuilder();
							timeFilledEmp.append(rs.getString("emp_id"));
						} else {
							timeFilledEmp.append(","+rs.getString("emp_id"));
						}
					}
					rs.close();
					pst.close();
					if(timeFilledEmp == null) {
						timeFilledEmp = new StringBuilder();
					}
				}
//				System.out.println("timeFilledEmp ===>> " + timeFilledEmp.toString());
			}
			if(timeFilledEmp == null) {
				timeFilledEmp = new StringBuilder();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return timeFilledEmp.toString();
	}

	
	private String getDependencyTaskOptions(String taskId, String dependencyId, List<FillDependentTaskList> dependencyList) {
		StringBuilder sbTaskOptions = new StringBuilder();
		
		for(int i=0; dependencyList != null && i<dependencyList.size(); i++) {
			if(!dependencyList.get(i).getDependencyId().equals(taskId)) {
				if(dependencyList.get(i).getDependencyId().equals(dependencyId)) {
					sbTaskOptions.append("<option value='"+dependencyList.get(i).getDependencyId()+"' selected>"+dependencyList.get(i).getDependencyName()+"</option>");
				} else {
					sbTaskOptions.append("<option value='"+dependencyList.get(i).getDependencyId()+"'>"+dependencyList.get(i).getDependencyName()+"</option>");
				}
			}
		}
//		System.out.println("sbTaskOptions ====>>> " + sbTaskOptions.toString());
		return sbTaskOptions.toString();
	}
	
	
	private String getProjectEmpSkillIds(UtilityFunctions uF, int proId) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sbSkillIds = null;
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbEmpIds = null;
			List<String> alEmpID = new ArrayList<String>();
			pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=?");
			pst.setInt(1, proId);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(!alEmpID.contains(rs.getString("emp_id"))) {
					if(sbEmpIds == null) {
						sbEmpIds = new StringBuilder();
						sbEmpIds.append(rs.getString("emp_id"));
					} else {
						sbEmpIds.append(","+rs.getString("emp_id"));
					}
					alEmpID.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			if(sbEmpIds == null) {
				sbEmpIds = new StringBuilder();
			}
			
//			System.out.println("sbEmpIds ===>>> " + sbEmpIds);
			if(sbEmpIds.length()>0) {
				List<String> alSkillIds = new ArrayList<String>();
				pst = con.prepareStatement("select skill_id from skills_description where emp_id in ("+sbEmpIds.toString()+")");
				rs = pst.executeQuery();
				while(rs.next()) {
					if(!alSkillIds.contains(rs.getString("skill_id"))) {
						if(sbSkillIds == null) {
							sbSkillIds = new StringBuilder();
							sbSkillIds.append(rs.getString("skill_id"));
						} else {
							sbSkillIds.append(","+rs.getString("skill_id"));
						}
						alSkillIds.add(rs.getString("skill_id"));
					}
				}
				rs.close();
				pst.close();
			}
			if(sbSkillIds == null) {
				sbSkillIds = new StringBuilder();
			}
//			System.out.println("sbSkillIds ===>>> " + sbSkillIds);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return sbSkillIds.toString();
	}


	
	public void getProjectFilters() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
//			System.out.println("getProType ===>> " + getProType());
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			getSelectedFilter(uF, hmEmpName);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}
	
	
//	public void getProjectDetails() {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		try {
//			con = db.makeConnection(con);
////			System.out.println("getProType ===>> " + getProType());
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
//			getSelectedFilter(uF, hmEmpName);
//			
//			
//			StringBuilder sbQuery11 = new StringBuilder();
//			sbQuery11.append("select count(*) as proCount, approve_status from projectmntnc where pro_id > 0 ");
//			
//			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
//				sbQuery11.append(" and (org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") ");
//				if(getPageType() != null && getPageType().equals("MP")) {
//					sbQuery11.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//				}
//				sbQuery11.append(") ");
//			}
//			
//			if(getLocation()!=null && getLocation().length>0) {
//	            sbQuery11.append(" and wlocation_id in ("+StringUtils.join(getLocation(), ",")+") ");
//	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery11.append(" and (wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
//				if(getPageType() != null && getPageType().equals("MP")) {
//					sbQuery11.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//				}
//				sbQuery11.append(") ");
//			}
//			
//			if(getF_sbu()!=null && getF_sbu().length > 0 && !getF_sbu()[0].trim().equals("")) {
//				sbQuery11.append(" and sbu_id in ("+StringUtils.join(getF_sbu(), ",")+") ");
//			}
//			
//			if(getF_service()!=null && getF_service().length > 0) {
//				String service = getConcateData(getF_service());
//				sbQuery11.append(" and service in ("+service+") ");
//			}
//			
//			if(getClient()!=null && getClient().length > 0 && !getClient()[0].trim().equals("")) {
//				sbQuery11.append(" and client_id in ("+StringUtils.join(getClient(), ",")+") ");
//			}
//			
//			if(getPro_id()!=null && getPro_id().length > 0 && !getPro_id()[0].trim().equals("")) {
//				sbQuery11.append(" and pro_id in ("+StringUtils.join(getPro_id(), ",")+") ");
//			}
//			
//			if(getManagerId()!=null && getManagerId().length > 0 && !getManagerId()[0].trim().equals("")) {
//				sbQuery11.append(" and project_owner in ("+StringUtils.join(getManagerId(), ",")+") ");
//			}
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getPageType() == null || !getPageType().equals("MP"))) {
//				sbQuery11.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
//			}
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && getPageType() != null && getPageType().equals("MP")) {
//				sbQuery11.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
//				
//			} else if(getPageType() != null && getPageType().equals("MP")) {
//				sbQuery11.append(" and (pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//			}
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
//				sbQuery11.append(" and poc="+uF.parseToInt((String)session.getAttribute(EMPID))+" ");
//			}
//			
//			if(getProStatus() != null && uF.parseToInt(getProStatus()) == 1) {
//				sbQuery11.append(" and completed>0 and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
//			} else if(getProStatus() != null && uF.parseToInt(getProStatus()) == 2) {
//				sbQuery11.append(" and (completed = 0 or completed is null) and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
//			} else if(getProStatus() != null && uF.parseToInt(getProStatus()) == 3) {
//				sbQuery11.append(" and deadline < '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
//			}
//			
//			if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 1) {
//				sbQuery11.append(" and billing_type != 'F' ");
//			} else if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 2) {
//				sbQuery11.append(" and billing_type = 'F' ");
//			}
//			
//			if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) >0) {
//				sbQuery11.append(" and added_by = "+uF.parseToInt(getAssignedBy())+" ");
//			}
//			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
//				sbQuery11.append(" and (upper(pro_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or client_id in (select client_id from client_details where upper(client_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'))");
//			}
//			sbQuery11.append(" group by approve_status");
//			pst = con.prepareStatement(sbQuery11.toString());
////			System.out.println("pst ===>> " + pst);
//			int wProCnt = 0;
//			int cProCnt = 0;
//			int bProCnt = 0;
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				if(rs.getString("approve_status") !=null && rs.getString("approve_status").equals("n")) {
//					wProCnt = rs.getInt("proCount");
//				} else if(rs.getString("approve_status") !=null && rs.getString("approve_status").equals("approved")) {
//					cProCnt = rs.getInt("proCount");
//				} else if(rs.getString("approve_status") !=null && rs.getString("approve_status").equals("blocked")) {
//					bProCnt = rs.getInt("proCount");
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("wProCnt", wProCnt+"");
//			request.setAttribute("cProCnt", cProCnt+"");
//			request.setAttribute("bProCnt", bProCnt+"");
//			
//			
//			
//			int proCount = 0;
//			StringBuilder sbQuery1 = new StringBuilder();
//			sbQuery1.append("select count(*) as proCount from projectmntnc where pro_id > 0 ");
//			
//			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
//				sbQuery1.append(" and approve_status='n' ");
//			} else if(getProType() != null && getProType().equals("C")) {
//				sbQuery1.append(" and approve_status='approved' ");
//			} else if(getProType() != null && getProType().equals("B")) {
//				sbQuery1.append(" and approve_status='blocked' ");
//			}
//			
//			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
//				sbQuery1.append(" and (org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") ");
//				if(getPageType() != null && getPageType().equals("MP")) {
//					sbQuery1.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//				}
//				sbQuery1.append(") ");
//			}
//			
//			if(getLocation()!=null && getLocation().length>0) {
//	            sbQuery1.append(" and wlocation_id in ("+StringUtils.join(getLocation(), ",")+") ");
//	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery1.append(" and (wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
//				if(getPageType() != null && getPageType().equals("MP")) {
//					sbQuery1.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//				}
//				sbQuery1.append(") ");
//			}
//			if(getF_sbu()!=null && getF_sbu().length > 0 && !getF_sbu()[0].trim().equals("")) {
//				sbQuery1.append(" and sbu_id in ("+StringUtils.join(getF_sbu(), ",")+") ");
//			}
//			
//			if(getF_service()!=null && getF_service().length > 0) {
//				String service = getConcateData(getF_service());
//				sbQuery1.append(" and service in ("+service+") ");
//			}
//			
//			if(getClient()!=null && getClient().length > 0 && !getClient()[0].trim().equals("")) {
//				sbQuery1.append(" and client_id in ("+StringUtils.join(getClient(), ",")+") ");
//			}
//			
//			if(getPro_id()!=null && getPro_id().length > 0 && !getPro_id()[0].trim().equals("")) {
//				sbQuery1.append(" and pro_id in ("+StringUtils.join(getPro_id(), ",")+") ");
//			}
//			
//			if(getManagerId()!=null && getManagerId().length > 0 && !getManagerId()[0].trim().equals("")) {
//				sbQuery1.append(" and project_owner in ("+StringUtils.join(getManagerId(), ",")+") ");
//			}
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getPageType() == null || !getPageType().equals("MP"))) {
//				sbQuery1.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
//			}
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && getPageType() != null && getPageType().equals("MP")) {
//				sbQuery1.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
//				
//			} else if(getPageType() != null && getPageType().equals("MP")) {
//				sbQuery1.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//			}
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
//				sbQuery1.append(" and poc="+uF.parseToInt((String)session.getAttribute(EMPID))+" ");
//			}
//			
//			if(getProStatus() != null && uF.parseToInt(getProStatus()) == 1) {
//				sbQuery1.append(" and completed>0 and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
//			} else if(getProStatus() != null && uF.parseToInt(getProStatus()) == 2) {
//				sbQuery1.append(" and (completed = 0 or completed is null) and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
//			} else if(getProStatus() != null && uF.parseToInt(getProStatus()) == 3) {
//				sbQuery1.append(" and deadline < '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
//			}
//			
//			if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 1) {
//				sbQuery1.append(" and billing_type != 'F' ");
//			} else if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 2) {
//				sbQuery1.append(" and billing_type = 'F' ");
//			}
//			
//			if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) >0) {
//				sbQuery1.append(" and added_by = "+uF.parseToInt(getAssignedBy())+" ");
//			}
//			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
//				sbQuery1.append(" and (upper(pro_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or client_id in (select client_id from client_details where upper(client_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'))");
//			}
////			sbQuery1.append(" order by pro_id");
//			pst = con.prepareStatement(sbQuery1.toString());
////			System.out.println("pst ===>> " + pst);
//			int proCnt = 0;
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				proCnt = rs.getInt("proCount");
//				proCount = rs.getInt("proCount")/10;
//				if(rs.getInt("proCount")%10 != 0) {
//					proCount++;
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("proCount", proCount+"");
//			request.setAttribute("proCnt", proCnt+"");
//			
//			
//			List<String> alAddedBy = new ArrayList<String>();
//			StringBuilder sbQuery2 = new StringBuilder();
//			sbQuery2.append("select distinct(added_by) as added_by from projectmntnc where pro_id > 0 ");
//			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
//				sbQuery2.append(" and approve_status='n' ");
//			} else if(getProType() != null && getProType().equals("C")) {
//				sbQuery2.append(" and approve_status='approved' ");
//			} else if(getProType() != null && getProType().equals("B")) {
//				sbQuery2.append(" and approve_status='blocked' ");
//			}
//			
//			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
//				sbQuery2.append(" and (org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") ");
//				if(getPageType() != null && getPageType().equals("MP")) {
//					sbQuery2.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//				}
//				sbQuery2.append(") ");
//			}
//			if(getLocation()!=null && getLocation().length>0) {
//	            sbQuery2.append(" and wlocation_id in ("+StringUtils.join(getLocation(), ",")+") ");
//	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery2.append(" and (wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
//				if(getPageType() != null && getPageType().equals("MP")) {
//					sbQuery2.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//				}
//				sbQuery2.append(") ");
//			}
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getPageType() == null || !getPageType().equals("MP"))) {
//				sbQuery2.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
//			}
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && getPageType() != null && getPageType().equals("MP")) {
//				sbQuery2.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
//				
//			} else if(getPageType() != null && getPageType().equals("MP")) {
//				sbQuery2.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//			}
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
//				sbQuery2.append(" and poc="+uF.parseToInt((String)session.getAttribute(EMPID))+" ");
//			}
//			pst = con.prepareStatement(sbQuery2.toString());
////			System.out.println("pst ===>> " + pst);
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				alAddedBy.add(rs.getString("added_by"));
//			}
//			rs.close();
//			pst.close();
//			
//			StringBuilder sbAddedbyOption = new StringBuilder();
//			for(int i=0; alAddedBy != null && !alAddedBy.isEmpty() && i<alAddedBy.size(); i++) {
//				if(uF.parseToInt(alAddedBy.get(i)) > 0) {
//					if(uF.parseToInt(getAssignedBy()) == uF.parseToInt(alAddedBy.get(i))) {
//						sbAddedbyOption.append("<option value='"+alAddedBy.get(i)+"' selected>"+hmEmpName.get(alAddedBy.get(i))+"</option>");
//					} else {
//						sbAddedbyOption.append("<option value='"+alAddedBy.get(i)+"'>"+hmEmpName.get(alAddedBy.get(i))+"</option>");
//					}
//				}
//			}
//			request.setAttribute("sbAddedbyOption", sbAddedbyOption.toString());
//			
//			Map<String, List<String>> hmProject = new LinkedHashMap<String, List<String>>();
//			Map<String, List<List<String>>> hmTasks = new LinkedHashMap<String, List<List<String>>>();
//			Map<String, List<List<String>>> hmSubTasks = new LinkedHashMap<String, List<List<String>>>();
//			
////			Map<String, String> hmEmployee = CF.getEmpNameMap(con, null, null);
//			Map<String, String> hmServicemap = CF.getProjectServicesMap(con, true);
//			Map<String, String> hmClientName = CF.getProjectClientMap(con, uF);
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from projectmntnc where pro_id > 0 ");
//			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
//				sbQuery.append(" and approve_status='n' ");
//			} else if(getProType() != null && getProType().equals("C")) {
//				sbQuery.append(" and approve_status='approved' ");
//			} else if(getProType() != null && getProType().equals("B")) {
//				sbQuery.append(" and approve_status='blocked' ");
//			}
//			
//			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
//				sbQuery.append(" and (org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") ");
//				if(getPageType() != null && getPageType().equals("MP")) {
//					sbQuery.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//				}
//				sbQuery.append(") ");
//			}
//			if(getLocation()!=null && getLocation().length>0) {
//	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getLocation(), ",")+") ");
//	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery.append(" and (wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+") ");
//				if(getPageType() != null && getPageType().equals("MP")) {
//					sbQuery.append(" or ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//				}
//				sbQuery.append(") ");
//			}
//			
//			if(getF_sbu()!=null && getF_sbu().length > 0 && !getF_sbu()[0].trim().equals("")) {
//				sbQuery.append(" and sbu_id in ("+StringUtils.join(getF_sbu(), ",")+") ");
//			}
//			
//			if(getF_service()!=null && getF_service().length > 0) {
//				String service = getConcateData(getF_service());
//				sbQuery.append(" and service in ("+service+") ");
//			}
//			
//			if(getClient()!=null && getClient().length > 0 && !getClient()[0].trim().equals("")) {
//				sbQuery.append(" and client_id in ("+StringUtils.join(getClient(), ",")+") ");
//			}
//			
//			if(getPro_id()!=null && getPro_id().length > 0 && !getPro_id()[0].trim().equals("")) {
//				sbQuery.append(" and pro_id in ("+StringUtils.join(getPro_id(), ",")+") ");
//			}
//			
//			if(getManagerId()!=null && getManagerId().length > 0 && !getManagerId()[0].trim().equals("")) {
//				sbQuery.append(" and project_owner in ("+StringUtils.join(getManagerId(), ",")+") ");
//			}
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && (getPageType() == null || !getPageType().equals("MP"))) {
//				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
//			}
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER) && getPageType() != null && getPageType().equals("MP")) {
//				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
//					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
//				
//			} else if(getPageType() != null && getPageType().equals("MP")) {
//				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
//					+uF.parseToInt((String)session.getAttribute(EMPID))+") or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+") ");
//			}
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(CUSTOMER)) {
//				sbQuery.append(" and poc="+uF.parseToInt((String)session.getAttribute(EMPID))+" ");
//			}
//			
//			if(getProStatus() != null && uF.parseToInt(getProStatus()) == 1) {
//				sbQuery.append(" and completed>0 and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
//			} else if(getProStatus() != null && uF.parseToInt(getProStatus()) == 2) {
//				sbQuery.append(" and (completed = 0 or completed is null) and deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
//			} else if(getProStatus() != null && uF.parseToInt(getProStatus()) == 3) {
//				sbQuery.append(" and deadline < '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
//			}
//			
//			if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 1) {
//				sbQuery.append(" and billing_type != 'F' ");
//			} else if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 2) {
//				sbQuery.append(" and billing_type = 'F' ");
//			}
//			
//			if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) >0) {
//				sbQuery.append(" and added_by = "+uF.parseToInt(getAssignedBy())+" ");
//			}
//			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
//				sbQuery.append(" and (upper(pro_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or client_id in (select client_id from client_details where upper(client_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%'))");
//			}
//			if(getSortBy() != null && uF.parseToInt(getSortBy()) == 1) {
//				sbQuery.append(" order by start_date desc ");
//			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 2) {
//				sbQuery.append(" order by start_date ");
//			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 3) {
//				sbQuery.append(" order by pro_name ");
//			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 4) {
//				sbQuery.append(" order by pro_name desc ");
//			} else {
//				sbQuery.append(" order by start_date desc ");
//			}
//			int intOffset = uF.parseToInt(minLimit);
//			sbQuery.append(" limit 10 offset "+intOffset+"");
//			pst = con.prepareStatement(sbQuery.toString());
////			System.out.println("pst View All Projects ===>> " + pst);
////			System.out.println("pst date ==========>> " + new Date());
//			rs = pst.executeQuery();
//			
////			request.setAttribute("strLimit", "10"); 
////			request.setAttribute("strDivCount", "1");
//			GetPriorityList objGP = new GetPriorityList();
//			
//			StringBuilder sbProIds = null;
//			while(rs.next()) {
//				if(sbProIds == null) {
//					sbProIds = new StringBuilder();
//					sbProIds.append(rs.getString("pro_id"));
//				} else {
//					sbProIds.append(","+rs.getString("pro_id"));
//				}
//				List<String> alInner = new ArrayList<String>();
//				
//				Map<String, String> hmProFreqStartEndDate = getProjectCurrentFreqStartEndDate(con, uF, rs.getString("pro_id"));
//				Map<String, String> hmProjectData = new HashMap<String, String>();
//				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
//				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
//				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
//				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
//					
//				Map<String, String> hmProAWDaysAndHrs = CF.getProjectActualAndBillableEfforts(con, rs.getString("pro_id"), hmProjectData);
//				
//				alInner.add(rs.getString("pro_id"));
//				alInner.add(rs.getString("pro_name")); //1
//				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), "")); //2
//				
//				alInner.add(uF.showData(hmServicemap.get(rs.getString("service")), "")); //3
//				if(hmProFreqStartEndDate != null && !hmProFreqStartEndDate.isEmpty()) {
//					alInner.add(uF.getDateFormat(hmProFreqStartEndDate.get("FREQ_START_DATE"), DBDATE, CF.getStrReportDateFormat())); //4
//					alInner.add(uF.getDateFormat(hmProFreqStartEndDate.get("FREQ_END_DATE"), DBDATE, CF.getStrReportDateFormat())); //5
//				} else {
//					alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat())); //4
//					alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat())); //5
//				}
//				double proDealineCompletePercent = 0;
//				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days"); //6
////					String actualTime = CF.getProjectActualTime(con, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
//					String actualTime = hmProAWDaysAndHrs.get("ACTUAL_DAYS");
////					proDealineCompletePercent = (uF.parseToDouble(actualTime) / uF.parseToDouble(rs.getString("idealtime"))) * 100;
//					proDealineCompletePercent = uF.parseToDouble(actualTime);
//					alInner.add(actualTime+"::::days"); //7
//				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" months"); //6
//					String actualTime = hmProAWDaysAndHrs.get("ACTUAL_DAYS");
//					double actualMonths = uF.parseToDouble(actualTime) / 30;
////					proDealineCompletePercent = (actualMonths / uF.parseToDouble(rs.getString("idealtime"))) * 100;
//					proDealineCompletePercent = actualMonths;
//					alInner.add(uF.formatIntoTwoDecimalWithOutComma(actualMonths)+"::::months"); //7
//				} else {
//					alInner.add(uF.getTotalTimeMinutes100To60(rs.getString("idealtime"))+" hrs");//6
////					String actualTime = CF.getProjectActualTime(con, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
//					String actualTime = hmProAWDaysAndHrs.get("ACTUAL_HRS");
////					proDealineCompletePercent = (uF.parseToDouble(uF.getTotalTimeMinutes100To60(actualTime)) / uF.parseToDouble(rs.getString("idealtime"))) * 100;
//					proDealineCompletePercent = uF.parseToDouble(uF.getTotalTimeMinutes100To60(actualTime));
//					alInner.add(uF.getTotalTimeMinutes100To60(actualTime)+"::::hrs"); //7
//				}
//				
//				
//				alInner.add(rs.getString("completed")); //8
//				
////				String strProIdealCompTime = getProjectIdealCompletedTime(uF, rs.getString("start_date"), rs.getString("deadline"), rs.getString("idealtime"));
//				String strProDeadlineColor = "";
//				if(proDealineCompletePercent < uF.parseToDouble(rs.getString("idealtime"))) {
//					strProDeadlineColor = "green";
//				} else if(proDealineCompletePercent == uF.parseToDouble(rs.getString("idealtime"))) {
//					strProDeadlineColor = "yellow";
//				} else if(proDealineCompletePercent > uF.parseToDouble(rs.getString("idealtime"))) {
//					strProDeadlineColor = "red";
//				} else {
//					strProDeadlineColor = "";
//				}
//				
//				java.util.Date dtStartDate = uF.getDateFormat(rs.getString("start_date"), DBDATE);
//				java.util.Date dtDeadLine = uF.getDateFormat(rs.getString("deadline"), DBDATE);
//				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
//				java.util.Date dtEntryDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE);
//				java.util.Date dtPrevDate = uF.getPrevDate(CF.getStrTimeZone(), 7);
//				
////				if("n".equalsIgnoreCase(rs.getString("approve_status"))) {
////					if(uF.parseToDouble(rs.getString("completed"))>=100) {
////						alInner.add("<span style=\"color:green\">Completed</span>");
////					} else if(dtCurrentDate!=null && dtDeadLine!=null && dtCurrentDate.after(dtDeadLine) && uF.parseToDouble(rs.getString("already_work"))>0) {
////						alInner.add("<span style=\"color:red\">Overdue</span>");
////					} else if(dtCurrentDate!=null && dtStartDate!=null && dtCurrentDate.before(dtStartDate) && uF.parseToDouble(rs.getString("already_work"))==0) {
////						alInner.add("<span style=\"color:orange\">Planned</span>");
////					} else if(dtCurrentDate!=null && dtDeadLine!=null && uF.parseToDouble(rs.getString("already_work"))>0) {
////						alInner.add("<span style=\"color:green\">Working</span>");
////					} else {
////						alInner.add("<span style=\"color:orange\">Planned</span>");
////					}
////				} else {
////					alInner.add("<span style=\"color:green\">Completed</span>");
////				}
//				alInner.add(strProDeadlineColor); //9
//				
//				alInner.add(uF.showData(hmClientName.get(rs.getString("client_id")), "")); //10
//				if(dtEntryDate!=null && dtEntryDate.after(dtPrevDate)) {
//					alInner.add("1"); //11 show new icon on new projects	
//				} else {
//					alInner.add("0"); //11
//				}
//				
//				alInner.add(rs.getString("actual_calculation_type")); //12
//				alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT)); //13
//				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT)); //14
//				Map<String, String> hmProResourcesOwnerAndTL = getProResourcesOwnerAndTL(con, uF, rs.getString("pro_id"));
//				alInner.add(hmProResourcesOwnerAndTL.get("RESOURCE_COUNT")); //15
//				alInner.add(hmProResourcesOwnerAndTL.get("COMPLETE_TASK_COUNT")); //16
//				alInner.add(hmProResourcesOwnerAndTL.get("OWNER_TL_IMAGE")); //17
////				Map<String, String> hmProMilestoneAndCompletedMilestone = getProMilestoneAndCompletedMilestone(con, uF, rs.getString("pro_id"));
////				alInner.add(hmProMilestoneAndCompletedMilestone.get("MILESTONE_COUNT")); //18
////				alInner.add(hmProMilestoneAndCompletedMilestone.get("COMPLETED_MILESTONE_COUNT")); //19
//				
//				Map<String, String> hmProProfitability = getProjectProfitability(con, uF, rs.getString("pro_id"));
//				alInner.add(hmProProfitability.get("PROFITABILITY")); //18
//				alInner.add(rs.getString("billing_kind")); //19
//				
//				double deadLinePercent = 0;
//				String days = null;
//				String currdays = null;
//				if(hmProFreqStartEndDate != null && !hmProFreqStartEndDate.isEmpty()) {
//					days = uF.dateDifference(hmProFreqStartEndDate.get("FREQ_START_DATE"), DBDATE, hmProFreqStartEndDate.get("FREQ_END_DATE"), DBDATE);
//					if(rs.getString("approve_status") != null && !rs.getString("approve_status").equals("n")) {
//						currdays = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("approve_date"), DBDATE);
//					} else {
//						currdays = uF.dateDifference(hmProFreqStartEndDate.get("FREQ_START_DATE"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
//					} 
//				} else {
//					days = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("deadline"), DBDATE);
//					if(rs.getString("approve_status") != null && !rs.getString("approve_status").equals("n")) {
//						currdays = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("approve_date"), DBDATE);
//					} else {
//						currdays = uF.dateDifference(rs.getString("start_date"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
//					}
//				}
//				if(uF.parseToDouble(days) > 0) {
//					deadLinePercent = (uF.parseToDouble(currdays) / uF.parseToDouble(days)) * 100;
//				}
//				String proDeadlinePercentColor = "";
//				if(deadLinePercent <= 75) {
//					proDeadlinePercentColor = "green";
//				} else if(deadLinePercent > 75 && deadLinePercent < 100) {
//					proDeadlinePercentColor = "yellow";
//				} else if(deadLinePercent >= 100) {
//					proDeadlinePercentColor = "red";
//				} else {
//					proDeadlinePercentColor = "";
//				}
//				alInner.add(proDeadlinePercentColor); //20
//				alInner.add(rs.getString("project_owner")); //21
//				alInner.add(rs.getString("added_by")); //22
//				
//				hmProject.put(rs.getString("pro_id"), alInner);
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("hmProject", hmProject);
//
//			if(sbProIds != null || getSingleProid() != 0) {
//				
//			Map<String, String> hmPDocumentCounter = new HashMap<String, String>();
//			pst = con.prepareStatement("select count(pro_document_id) as count, pro_id from project_document_details where pro_id in ("+sbProIds.toString()+") and file_size is not null group by pro_id");
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmPDocumentCounter.put(rs.getString("pro_id"), rs.getString("count"));
//			}
//			rs.close();
//			pst.close();
////			System.out.println("hmPDocumentCounter ====>>> " + hmPDocumentCounter);
//			request.setAttribute("hmPDocumentCounter", hmPDocumentCounter);
//			
//			
//			getActivity(con, uF, sbProIds.toString(), getSingleProid());
//			getProMilestoneAndCompletedMilestone(con, uF, sbProIds.toString());
//
//		}
//			request.setAttribute("hmSubTasks", hmSubTasks);
//			request.setAttribute("hmTasks", hmTasks);
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
	Map<String, String> getProjectCurrentFreqStartEndDate(Connection con, UtilityFunctions uF, String proId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmProFreqStartEndDate = new HashMap<String, String>();
		try {
			
			pst = con.prepareStatement("select freq_start_date, freq_end_date from projectmntnc_frequency where freq_end_date >= ? and pro_id=? order by freq_end_date limit 1");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt(proId));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmProFreqStartEndDate.put("FREQ_START_DATE", rs.getString("freq_start_date"));
				hmProFreqStartEndDate.put("FREQ_END_DATE", rs.getString("freq_end_date"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmProFreqStartEndDate;
	}


//	private String getProjectIdealCompletedTime(UtilityFunctions uF, String stDate, String endDate, String idealTime) {
//		String compTime = "0";
////		System.out.println("stDate ===>> " + stDate + "  endDate ===>> " + endDate);
//		String dayDiff = uF.dateDifference(stDate, DBDATE, endDate, DBDATE);
//		
//		double onedayIdeatTime = uF.parseToDouble(idealTime) / uF.parseToDouble(dayDiff);
//		String dayDiff1 = uF.dateDifference(stDate, DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
////		System.out.println("dayDiff ===>> " + dayDiff + "  onedayIdeatTime ==> " + onedayIdeatTime + "  dayDiff1 ===>> " +dayDiff1);
//		if(uF.parseToDouble(dayDiff) > uF.parseToDouble(dayDiff1)) {
//			double dblCompPercent = onedayIdeatTime * uF.parseToDouble(dayDiff1);
//			compTime = dblCompPercent+"";
//		} else {
//			compTime = idealTime;
//		}
//		return compTime;
//	}


	private Map<String, String> getProjectProfitability(Connection con, UtilityFunctions uF, String proId) {
		Map<String, String> hmProjectProfitability = new HashMap<String, String>();
		try {
			double dblBillableAmt = 0;
			double dblActualAmt = 0;
			Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, proId);
		
			Map<String, String> hmProActualCostTime = CF.getProjectActualCostAndTime(con,request, CF, uF, proId, hmProjectData, false, false);
			Map<String, String> hmProBillCost = CF.getProjectBillableCostAndTime(con,request, CF, uF, proId, hmProjectData, false, false);
			String reimbursementAmount = CF.getProjectReimbursementAmount(con, proId, uF);
			
//			System.out.println("PRO_BILLING_AMOUNT ===>> " + hmProjectData.get("PRO_BILLING_AMOUNT") +"   -- " + hmProjectData.get("PRO_BILLING_TYPE"));
//			System.out.println("reimbursementAmount ===>> " +reimbursementAmount+ "  dblActualAmt ===>> " + dblActualAmt);
			
			if("F".equalsIgnoreCase(hmProjectData.get("PRO_BILL_TYPE"))) {
				dblBillableAmt = uF.parseToDouble(hmProjectData.get("PRO_BILLING_AMOUNT"));
			} else {
				dblBillableAmt = uF.parseToDouble(hmProBillCost.get("proBillableCost"));
			}
			dblActualAmt = uF.parseToDouble(hmProActualCostTime.get("proActualCost"));
//			System.out.println("dblBillableAmt  ===>> " + dblBillableAmt);
			double diff = 0;
			if(dblBillableAmt > 0) {
				diff = ((dblBillableAmt-(dblActualAmt + uF.parseToDouble(reimbursementAmount)))/dblBillableAmt) * 100;
			}
//			System.out.println("diff  ===>> " + diff);
			hmProjectProfitability.put("PROFITABILITY", Math.round(diff)+"");
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmProjectProfitability;
	}


	private void getProMilestoneAndCompletedMilestone(Connection con, UtilityFunctions uF, String proId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select ai.*, pmc.start_date as pmc_start_date, pmc.deadline as pmc_deadline, pmc.bill_days_type, pmc.hours_for_bill_day, " +
				"pmc.actual_calculation_type, pmc.billing_type from activity_info ai, projectmntnc pmc where ai.pro_id=pmc.pro_id and " +
				"ai.parent_task_id = 0 and task_accept_status != -1 and pmc.pro_id in ("+proId+")");
			pst = con.prepareStatement(sbQuery.toString()); //and ai.pro_id=?
//			pst.setInt(1, uF.parseToInt(getPro_id()));
//			System.out.println("pst======main==="+pst);
			rs = pst.executeQuery();
			Map<String, String> hmProIds = new HashMap<String, String>();
			Map<String, String> hmProTaskData = new HashMap<String, String>();
			
			
			while (rs.next()) {
				
				double taskIdealTime = uF.parseToDouble(hmProTaskData.get(rs.getString("pro_id")+"_IDEAL_TIME"));
				taskIdealTime = taskIdealTime + uF.parseToDouble(rs.getString("idealtime"));
				double taskWoredTime = uF.parseToDouble(hmProTaskData.get(rs.getString("pro_id")+"_WORKED_TIME"));
				double actWorkTime = (uF.parseToDouble(rs.getString("idealtime")) * uF.parseToDouble(rs.getString("completed"))) / 100;
				taskWoredTime = taskWoredTime + actWorkTime;
				
				hmProTaskData.put(rs.getString("pro_id")+"_IDEAL_TIME", taskIdealTime+"");
				hmProTaskData.put(rs.getString("pro_id")+"_WORKED_TIME", taskWoredTime+"");
				
				hmProIds.put(rs.getString("pro_id"), rs.getString("pro_id"));
				
				/*double taskCompPercent = uF.parseToDouble(hmProTaskCompletePercent.get(rs.getString("pro_id")));
				taskCompPercent = taskCompPercent + uF.parseToDouble(rs.getString("completed"));
				hmProTaskCompletePercent.put(rs.getString("pro_id"), taskCompPercent+"");
				
				int taskCount = uF.parseToInt(hmProTaskCount.get(rs.getString("pro_id")));
					taskCount++;
				hmProTaskCount.put(rs.getString("pro_id"), taskCount+"");*/
				
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmProIds.keySet().iterator();
			
			Map<String, String> hmProCompPercent = new HashMap<String, String>();
			Map<String, Map<String, String>> hmProMileAndCMileCnt = new HashMap<String, Map<String, String>>();
			while (it.hasNext()) {
				Map<String, String> hmMilestoneAndCompletedMilestone = new HashMap<String, String>();
				String strProId = it.next();
				double dblProCompletePercent = 0.0d;
				if(uF.parseToDouble(hmProTaskData.get(strProId+"_IDEAL_TIME")) > 0) {
					dblProCompletePercent = (uF.parseToDouble(hmProTaskData.get(strProId+"_WORKED_TIME")) * 100) / uF.parseToDouble(hmProTaskData.get(strProId+"_IDEAL_TIME"));
				}
				hmProCompPercent.put(strProId, dblProCompletePercent+"");
				
				int milestoneCount = 0;
				int completedMilestoneCount = 0;
				pst = con.prepareStatement("select pmd.*, p.milestone_dependent_on from project_milestone_details pmd, projectmntnc p where pmd.pro_id= p.pro_id and pmd.pro_id=?");
				pst.setInt(1, uF.parseToInt(strProId));
				rs = pst.executeQuery();
				while (rs.next()) {
					if(uF.parseToInt(rs.getString("milestone_dependent_on")) == 2) {
						int intcomplete = getProTaskCompleted(con, uF, rs.getString("pro_task_id"));
						completedMilestoneCount += intcomplete;
					} else if(uF.parseToInt(rs.getString("milestone_dependent_on")) == 1) {
						if(rs.getDouble("pro_completion_percent") <= uF.parseToDouble(hmProCompPercent.get(strProId))) {
							completedMilestoneCount++;
						}
					}
					
					milestoneCount++;
				}
				rs.close();
				pst.close();
				
				hmMilestoneAndCompletedMilestone.put("MILESTONE_COUNT", milestoneCount+"");
				hmMilestoneAndCompletedMilestone.put("COMPLETED_MILESTONE_COUNT", completedMilestoneCount+"");
				hmProMileAndCMileCnt.put(strProId, hmMilestoneAndCompletedMilestone);
			}
			
			request.setAttribute("hmProCompPercent", hmProCompPercent);
			request.setAttribute("hmProMileAndCMileCnt", hmProMileAndCMileCnt);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	

	private int getProTaskCompleted(Connection con, UtilityFunctions uF, String taskId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int intTaskCompleted = 0;
		try {
			
			pst = con.prepareStatement("select task_id from activity_info where task_id =? and approve_status = 'approved' and task_accept_status != -1");
			pst.setInt(1, uF.parseToInt(taskId));
			rs = pst.executeQuery();
			while (rs.next()) {
				intTaskCompleted = 1;
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
		return intTaskCompleted;
	}

	

	private Map<String, String> getProResourcesOwnerAndTL(Connection con, UtilityFunctions uF, String proId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmProResourcesOwnerAndTL = new HashMap<String, String>();
		try {
			
			pst = con.prepareStatement("select count(emp_id) as empCnt from project_emp_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(proId));
			rs = pst.executeQuery();
			int empCnt = 0;
			while (rs.next()) {
				empCnt = rs.getInt("empCnt");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(task_id) as taskCnt from activity_info where pro_id=? and parent_task_id = 0 and approve_status = 'approved' and task_accept_status != -1");
			pst.setInt(1, uF.parseToInt(proId));
			rs = pst.executeQuery();
			int completeTaskCnt = 0;
			while (rs.next()) {
				completeTaskCnt = rs.getInt("taskCnt");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select project_owner from projectmntnc where pro_id=?");
			pst.setInt(1, uF.parseToInt(proId));
			rs = pst.executeQuery();
			//StringBuilder sbResources = new StringBuilder();
			String proOwnerId = "";
			while (rs.next()) {
				proOwnerId = rs.getString("project_owner");
			}
			rs.close();
			pst.close();
			
			StringBuilder sbEmpImages = new StringBuilder();
			String empImg = CF.getEmpImageByEmpId(con, uF, proOwnerId);
			String empName = CF.getEmpNameMapByEmpId(con, proOwnerId);
			String strEmpImg = CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+proOwnerId+"/"+I_22x22+"/"+empImg;
			if(uF.parseToInt(proOwnerId)>0) {
				sbEmpImages.append("<img height=\"20\" width=\"20\" title=\""+empName+" [PO]\" class=\"lazy\" src=\"userImages/avatar_photo.png\" style=\"border-bottom: 3px solid #00FF00;\" data-original=\""+strEmpImg+"\"/>");
			}
			Map<String, String> hmProTeamLeads = CF.getProjectTeamLeads(con, uF, proId);
			Iterator<String> it = hmProTeamLeads.keySet().iterator();
			while(it.hasNext()) {
				String empId = it.next();
				empImg = CF.getEmpImageByEmpId(con, uF, empId);
				empName = CF.getEmpNameMapByEmpId(con, empId);
				strEmpImg = CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+empId+"/"+I_22x22+"/"+empImg;
				sbEmpImages.append(" &nbsp;<img height=\"20\" width=\"20\" title=\""+empName+" [TL]\" class=\"lazy\" src=\"userImages/avatar_photo.png\" style=\"border-bottom: 3px solid #FFFF00;\" data-original=\""+strEmpImg+"\"/>");
			}
			
			hmProResourcesOwnerAndTL.put("OWNER_TL_IMAGE", sbEmpImages.toString());
			hmProResourcesOwnerAndTL.put("RESOURCE_COUNT", empCnt+"");
			hmProResourcesOwnerAndTL.put("COMPLETE_TASK_COUNT", completeTaskCnt+"");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
		return hmProResourcesOwnerAndTL;
	}


	private String getConcateData(String[] data) {
		StringBuilder sb = new StringBuilder();
//		StringBuilder sb1 = new StringBuilder();
		for(int i=0;i<data.length;i++) {
			if(i==0) {
//				sb1.append(data[i]);
				sb.append("'"+data[i]+"'");
			} else {
//				sb1.append(" ,"+data[i]);
				sb.append(",'"+data[i]+"'");
			}
		}
//		setService_id(sb1.toString());
		return sb.toString();
	}
	

	private StringBuilder getConcateData1(String[] data) {
		StringBuilder sb=new StringBuilder();
		
		for(int i=0;i<data.length;i++) {
			if(i==0) {
				sb.append(data[i]);
			} else {
				sb.append(","+data[i]);
			}
		}
		
		return sb;
	}
	
	
	
	public double getTaskDeadlineCompletePercent(int t_id) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		double completedPercent=0.0f;
		try {
			con = db.makeConnection(con);
			String dl_date = "";
			String start_date = "";
			String end_date = "";
			
			pst = con.prepareStatement("select deadline,start_date,end_date from activity_info where task_id=?");
			pst.setInt(1, t_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				dl_date = rs.getString(1);
				start_date = rs.getString(2);
				end_date = rs.getString(3);
			}
			rs.close();
			pst.close();
			
			if(start_date!= null && !start_date.equals("") && dl_date != null && !dl_date.equals("")) {
				ideal_days = uF.parseToDouble(uF.dateDifference(start_date, DBDATE, dl_date, DBDATE));
			}
			if(start_date!= null && !start_date.equals("")) {
				actual_days = uF.parseToDouble(uF.dateDifference(start_date, DBDATE, uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE));
			}
			if(ideal_days > 0) {
				completedPercent = (actual_days / ideal_days) * 100;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return completedPercent;
	}
	
	
	
	public double getNoOfDays(int t_id) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		double no_of_days=0.0f;
		try {
			con = db.makeConnection(con);
			String dl_date = "";
			String start_date = "";
			String end_date = "";
			
			pst = con.prepareStatement("select deadline,start_date,end_date from activity_info where task_id=?");
			pst.setInt(1, t_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				dl_date = rs.getString(1);
				start_date = rs.getString(2);
				end_date = rs.getString(3);
			}
			rs.close();
			pst.close();
			
			if(start_date!=null && !start_date.equals("") && dl_date!=null && !dl_date.equals("")) {
				ideal_days = uF.parseToDouble(uF.dateDifference(start_date, DBDATE, dl_date, DBDATE));
			}
			if(start_date!=null && !start_date.equals("") && end_date!=null && !end_date.equals("")) {
				actual_days = uF.parseToDouble(uF.dateDifference(start_date, DBDATE, end_date, DBDATE));
			} else if(start_date!=null && !start_date.equals("")) {
				actual_days = uF.parseToDouble(uF.dateDifference(start_date, DBDATE, uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE));
			}
			
			no_of_days = actual_days - ideal_days;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return no_of_days;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF, Map<String, String> hmEmpName) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
//		alFilter.add("LOCATION");
//		if(getLocation()!=null) {
//			String strLocation="";
//			int k=0;
//			for(int i=0;workLocationList!=null && i<workLocationList.size();i++) {
//				for(int j=0;j<getLocation().length;j++) {
//					if(getLocation()[j].equals(workLocationList.get(i).getwLocationId())) {
//						if(k==0) {
//							strLocation=workLocationList.get(i).getwLocationName();
//						} else {
//							strLocation+=", "+workLocationList.get(i).getwLocationName();
//						}
//						k++;
//					}
//				}
//			}
//			if(strLocation!=null && !strLocation.equals("")) {
//				hmFilter.put("LOCATION", strLocation);
//			} else {
//				hmFilter.put("LOCATION", "All Locations");
//			}
//		} else {
//			hmFilter.put("LOCATION", "All Locations");
//		}
		
		alFilter.add("SBU");
		if(getF_sbu()!=null) {
			String strSbu="";
			int k=0;
			for(int i=0;sbuList!=null && i<sbuList.size();i++) {
				for(int j=0;j<getF_sbu().length;j++) {
					if(getF_sbu()[j].equals(sbuList.get(i).getServiceId())) {
						if(k==0) {
							strSbu=sbuList.get(i).getServiceName();
						} else {
							strSbu+=", "+sbuList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strSbu!=null && !strSbu.equals("")) {
				hmFilter.put("SBU", strSbu);
			} else {
				hmFilter.put("SBU", "All SBUs");
			}
		} else {
			hmFilter.put("SBU", "All SBUs");
		}
		
		alFilter.add("SERVICE");
		if(getF_service()!=null) {
			String strService="";
			int k=0;
			for(int i=0;serviceList!=null && i<serviceList.size();i++) {
				for(int j=0;j<getF_service().length;j++) {
					if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if(k==0) {
							strService=serviceList.get(i).getServiceName();
						} else {
							strService+=", "+serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strService!=null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All Services");
			}
		} else {
			hmFilter.put("SERVICE", "All Services");
		}
		
		alFilter.add("PROJECT");
		if(getPro_id()!=null) {
			String strProjects="";
			int k=0;
			for(int i=0;projectdetailslist!=null && i<projectdetailslist.size();i++) {
				for(int j=0;j<getPro_id().length;j++) {
					if(getPro_id()[j].equals(projectdetailslist.get(i).getProjectID())) {
						if(k==0) {
							strProjects=projectdetailslist.get(i).getProjectName();
						} else {
							strProjects+=", "+projectdetailslist.get(i).getProjectName();
						}
						k++;
					}
				}
			}
			if(strProjects!=null && !strProjects.equals("")) {
				hmFilter.put("PROJECT", strProjects);
			} else {
				hmFilter.put("PROJECT", "All Projects");
			}
		} else {
			hmFilter.put("PROJECT", "All Projects");
		}
		
		
		alFilter.add("PROJECT_OWNER");
		if(getManagerId()!=null) {
			String strManager="";
			int k=0;
			for(int i=0;proOwnerList!=null && i<proOwnerList.size();i++) {
				for(int j=0;j<getManagerId().length;j++) {
					if(getManagerId()[j].equals(proOwnerList.get(i).getProOwnerId())) {
						if(k==0) {
							strManager=proOwnerList.get(i).getProOwnerName();
						} else {
							strManager+=", "+proOwnerList.get(i).getProOwnerName();
						}
						k++;
					}
				}
			}
			if(strManager!=null && !strManager.equals("")) {
				hmFilter.put("PROJECT_OWNER", strManager);
			} else {
				hmFilter.put("PROJECT_OWNER", "All Project Owners");
			}
		} else {
			hmFilter.put("PROJECT_OWNER", "All Project Owners");
		}
		
		
		if(strUserType != null && !strUserType.equals(CUSTOMER)) {
			alFilter.add("CLIENT");
			if(getClient()!=null) {
				String strClient="";
				int k=0;
				for(int i=0; clientList!=null && i<clientList.size();i++) {
					for(int j=0;j<getClient().length;j++) {
						if(getClient()[j].equals(clientList.get(i).getClientId())) {
							if(k==0) {
								strClient=clientList.get(i).getClientName();
							} else {
								strClient+=", "+clientList.get(i).getClientName();
							}
							k++;
						}
					}
				}
				if(strClient!=null && !strClient.equals("")) {
					hmFilter.put("CLIENT", strClient);
				} else {
					hmFilter.put("CLIENT", "All Clients");
				}
			} else {
				hmFilter.put("CLIENT", "All Clients");
			}
		}
		
		
		if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
			alFilter.add("PROSTATUS");
			if(getProStatus()!=null ) {
				String strProStatus="";
				if(uF.parseToInt(getProStatus()) == 1) {
					strProStatus = "On-Track";
				} else if(uF.parseToInt(getProStatus()) == 2) {
					strProStatus = "Not Started";
				} else if(uF.parseToInt(getProStatus()) == 3) {
					strProStatus = "Pending";
				}
				if(strProStatus!=null && !strProStatus.equals("")) {
					hmFilter.put("PROSTATUS", strProStatus);
				} else {
					hmFilter.put("PROSTATUS", "All Projects");
				}
			} else {
				hmFilter.put("PROSTATUS", "All Projects");
			}
		}
		
		alFilter.add("ASSIGNEDBY");
		if(getAssignedBy()!=null) {
			String strAssignedBy="";
			if(uF.parseToInt(getAssignedBy()) == -1) {
				strAssignedBy = "Myself";
			} else if(uF.parseToInt(getAssignedBy()) >0) {
				strAssignedBy = hmEmpName.get(getAssignedBy());
			}
			if(strAssignedBy!=null && !strAssignedBy.equals("")) {
				hmFilter.put("ASSIGNEDBY", strAssignedBy);
			} else {
				hmFilter.put("ASSIGNEDBY", "All Assigner");
			}
		} else {
			hmFilter.put("ASSIGNEDBY", "All Assigner");
		}
		
		alFilter.add("RECURRORMILES");
		if(getRecurrOrMiles()!=null ) {
			String strRecurrOrMiles="";
			if(uF.parseToInt(getRecurrOrMiles()) == 1) {
				strRecurrOrMiles = "Recurring";
			} else if(uF.parseToInt(getRecurrOrMiles()) == 2) {
				strRecurrOrMiles = "Milestone";
			}
			if(strRecurrOrMiles!=null && !strRecurrOrMiles.equals("")) {
				hmFilter.put("RECURRORMILES", strRecurrOrMiles);
			} else {
				hmFilter.put("RECURRORMILES", "All Project Type");
			}
		} else {
			hmFilter.put("RECURRORMILES", "All Project Type");
		}
		
		/*alFilter.add("SORTBY");
		if(getSortBy()!=null ) {
			String strSortBy="";
			if(uF.parseToInt(getSortBy()) == 1) {
				strSortBy = "Latest on top";
			} else if(uF.parseToInt(getSortBy()) == 2) {
				strSortBy = "Oldest on top";
			} else if(uF.parseToInt(getSortBy()) == 3) {
				strSortBy = "A-Z";
			} else if(uF.parseToInt(getSortBy()) == 4) {
				strSortBy = "Z-A";
			}
			if(strSortBy!=null && !strSortBy.equals("")) {
				hmFilter.put("SORTBY", strSortBy);
			} else {
				hmFilter.put("SORTBY", "Latest on top");
			}
		} else {
			hmFilter.put("SORTBY", "Latest on top");
		}*/
		
		String selectedFilter=getSelectedFilter(uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	public String getSelectedFilter(UtilityFunctions uF, List<String> alFilter, Map<String, String> hmFilter) {
		StringBuilder sbFilter=new StringBuilder("<strong>Filter Summary: </strong>");
		
		for(int i=0;alFilter!=null && i<alFilter.size();i++) {
			if(i>0) {
				sbFilter.append(", ");
			} 
			
			if(alFilter.get(i).equals("LOCATION")) {
				sbFilter.append("<strong>LOC:</strong> ");
				sbFilter.append(hmFilter.get("LOCATION"));
//			 
			} else if(alFilter.get(i).equals("SBU")) {
				sbFilter.append("<strong>SBU:</strong> ");
				sbFilter.append(hmFilter.get("SBU"));
//			 
			} else if(alFilter.get(i).equals("SERVICE")) {
				sbFilter.append("<strong>SERVICE:</strong> ");
				sbFilter.append(hmFilter.get("SERVICE"));
//			 
			} else if(alFilter.get(i).equals("PROJECT")) {
				sbFilter.append("<strong>PROJECT:</strong> ");
				sbFilter.append(hmFilter.get("PROJECT"));
			
			} else if(alFilter.get(i).equals("PROJECT_OWNER")) {
				sbFilter.append("<strong>PROJECT OWNER:</strong> ");
				sbFilter.append(hmFilter.get("PROJECT_OWNER"));
			
			} else if(alFilter.get(i).equals("CLIENT")) {
				sbFilter.append("<strong>CLIENT:</strong> ");
				sbFilter.append(hmFilter.get("CLIENT"));
			
			} else if(alFilter.get(i).equals("PROSTATUS")) {
				sbFilter.append("<strong>Task Status:</strong> ");
				sbFilter.append(hmFilter.get("PROSTATUS"));
			
			} else if(alFilter.get(i).equals("ASSIGNEDBY")) {
				sbFilter.append("<strong>Assigned By:</strong> ");
				sbFilter.append(hmFilter.get("ASSIGNEDBY"));
			
			} else if(alFilter.get(i).equals("RECURRORMILES")) {
				sbFilter.append("<strong>Project Type:</strong> ");
				sbFilter.append(hmFilter.get("RECURRORMILES"));
			
			}/* else if(alFilter.get(i).equals("SORTBY")) {
				sbFilter.append("<strong>Sort By:</strong> ");
				sbFilter.append(hmFilter.get("SORTBY"));
			} */
		}
		return sbFilter.toString();
	}
	
	
	
//	public void updateTotalHrs() {
//
//		try {
//			
//		/*List<Integer> id = getProjectIds();
//		
//			for (int i = 0; i < id.size(); i++) {
//				List<Integer> ac = getTaskIds(id.get(i));
//				double total_hrs = 0.0;
//
//				for (int j = 0; j < ac.size(); j++) {
//					pst = con.prepareStatement("select sum(actual_hrs) from task_activity where activity_id=?");
//					pst.setInt(1, ac.get(j));
//					rs = pst.executeQuery();
//					while (rs.next()) {
//						total_hrs += rs.getDouble(1);
//					}
//					total_hrs=Double.parseDouble(uF.formatIntoTwoDecimal(total_hrs));
//				}
//				pst1 = con.prepareStatement("update projectmntnc SET already_work=? WHERE pro_id=?");
//				pst1.setDouble(1,total_hrs);
//				pst1.setInt(2, id.get(i));
//				pst1.executeUpdate();
//			}
//			*/
//			
//			
//		/*pst = con.prepareStatement("select sum(ta.actual_hrs) as actual_hrs, count(distinct ta.task_date) as actual_days, pmc.pro_id from task_activity ta, activity_info ai, projectmntnc pmc where pmc.pro_id = ai.pro_id and ai.task_id = ta.activity_id and pmc.approve_status = 'n' group by pmc.pro_id");
//		rs = pst.executeQuery();
//		while (rs.next()) {
//			pst1 = con.prepareStatement("update projectmntnc SET already_work=? WHERE pro_id=?");
//			pst1.setDouble(1, uF.parseToDouble(rs.getString("actual_hrs")));
//			pst1.setDouble(2, uF.parseToDouble(rs.getString("actual_days")));
//			pst1.setInt(2, uF.parseToInt(rs.getString("pro_id")));
//			pst1.executeUpdate();
//		}*/
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public List<Integer> getPro_IdsFromProList() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		List<Integer> p_id = new ArrayList<Integer>();
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select pro_id from projectmntnc order by deadline");
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				p_id.add(rs.getInt(1));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return p_id;
//	}

	
	
//	public List<Integer> getProjectIds() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		List<Integer> i = new ArrayList<Integer>();
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select distinct(pro_id) from activity_info");
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				i.add(rs.getInt(1));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return i;
//	}

//	public List<Integer> getTaskIds(int i) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		List<Integer> ac_index = new ArrayList<Integer>();
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select task_id from activity_info where pro_id=?");
//			pst.setInt(1, i);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				ac_index.add(rs.getInt(1));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return ac_index;
//	}

//	public void getProjectNameToView() {
//
//		try {
//			pst = con.prepareStatement("select pro_id,pro_name from projectmntnc where approve_status='n' order by deadline ");
//			rs = pst.executeQuery();
//
//			while (rs.next()) {
//				projectidlist.add(rs.getInt("pro_id"));
//				projectlist.add(rs.getString("pro_name"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	
	
//	public void getAllProjectDetails(List<Integer> p_id) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//
//		Map<String, String> hmServicemap = CF.getProjectServicesMap(con, true);
//		
//		try {
//			con = db.makeConnection(con);
//			
//			getEmpName(con);
//			
//			for (int a = 0; a < p_id.size(); a++) {
//				pst = con.prepareStatement("select * from projectmntnc where pro_id=? and approve_status='n' order by pro_id");
//				pst.setInt(1, p_id.get(a));
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					List<String> alInner = new ArrayList<String>();
//					alInner.add("<input type='checkbox' value='" + p_id.get(a)+ "' name='approvePr' />"+"<b>" + rs.getString("pro_name") + "</b>");
//					alInner.add("");
//					alInner.add(rs.getString("priority"));
//					String file = rs.getString("document_name");
//					if (file != null)
//						alInner.add("<a href='taskuploads/" + file+ "' target='blank'>" + file + "</a>");
//					else
//						alInner.add("No Attachments");
//					alInner.add(hmServicemap.get(rs.getString("service")));
//					alInner.add("");
//					// emp_id=rs.getInt("emp_id");
////					if (uF.parseToInt(rs.getString("completed")) < 100) {
////						alInner.add("Pending");
////					} else {
////						alInner.add("Finished");
////					}
//					alInner.add("<a href=\"javascript:void(0)\" onclick=\"view('"+ rs.getString("_comment") + "')\" >View</a>");
//					alInner.add(uF.getDateFormat(rs.getString("deadline"),DBDATE, CF.getStrReportDateFormat()));
//					alInner.add(uF.removeNull(rs.getString("idealtime")));
//					alInner.add("" + rs.getDouble("already_work"));
////					alInner.add(0,"<input type='checkbox' value='" + p_id.get(a)+ "' name='approvePr' />");
//					if (isSingle) {
//						alInner.add("");
//						alInner.add("");
//						if (uF.parseToInt(rs.getString("completed")) < 100) {
//							alInner.add("Pending");
//						} else {
//							alInner.add("Finished");
//						}
//						alInner.add("");
//					} else {
//						if (uF.parseToInt(rs.getString("completed")) < 100) {
//							alInner.add("Pending");
//						} else {
//							alInner.add("Finished");
//						}
//						alInner.add("<a href=\"javascript:void(0)\" onclick=\"addActivity("+ p_id.get(a) + ")\">+AddNewTask</a>");
//						alInner.add("<a href='ProjectSummaryView.action?pro_id="+ p_id.get(a) + "'>View</a>");
//					}
//					al.put(rs.getInt(1), alInner);
//					index.add(rs.getInt(1));
//				}
//				getActivityDetailForAll(p_id);
//				// viewTimeData();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	

//	public void getActivityDetailForAll(List<Integer> index) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		try {
//			con = db.makeConnection(con);
//			
//			Map<Integer, List<List<String>>> activityDetailMap = new HashMap<Integer, List<List<String>>>();
//
//			for (int k = 0; k < index.size(); k++) {
//
//				List<List<String>> outInner = new ArrayList<List<String>>();
//				pst = con.prepareStatement("select * from activity_info where pro_id=? order by task_id");
//				pst.setInt(1, index.get(k));
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					List<String> alInner = new ArrayList<String>();
////					alInner.add("");
//					task_id = rs.getInt(1);
//					alInner.add("");
//					alInner.add(rs.getString("activity_name"));
//					alInner.add(rs.getString("priority"));
//					String file = rs.getString("filename");
//					if (file != null)
//						alInner.add("<a href='taskuploads/" + file+ "' target='blank'>" + file + "</a>");
//					else
//						alInner.add("No Attachments");
//					alInner.add("");
//
//					alInner.add(EmpNameMap.get(rs.getInt("emp_id")));
////					alInner.add("");
//					alInner.add("<a href=\"javascript:void(0)\" onclick=\"view('"+ rs.getString("_comment") + "')\" >View</a>");
//					alInner.add(uF.getDateFormat(rs.getString("deadline"),DBDATE, CF.getStrReportDateFormat()));
//					alInner.add(rs.getString("idealtime"));
//					double al_work = rs.getDouble("already_work");
//					int per = uF.parseToInt(rs.getString("completed"));
//					String isFinish = rs.getString("finish_task");
//					String isApprove = rs.getString("approve_status");
////					if (isFinish.equals("y") && isApprove.equals("n")) {
////						alInner.add(0, "<input type='checkbox' value='"+ task_id + "' name='cb' />");
////					} else {
////						alInner.add(0, "");
////					}
//					if (isSingle) {
//						getTimeDetail(task_id);
//						if (time1 != null && time1.size() != 0) {
//							String t_hrz = "";
//							String taskSta = "";
//							String time = "";
//							for (int x = 0; x < totalhrz1.size(); x++) {
//								t_hrz += totalhrz1.get(x) + "&nbsp;hrs<br/>";
//							}
//							alInner.add(t_hrz);
//							for (int x = 0; x < taskStatus1.size(); x++) {
//								taskSta += taskStatus1.get(x) + "%<br/>";
//							}
//							alInner.add(taskSta);
//							for (int x = 0; x < time1.size(); x++) {
//								String t = time1.get(x++);
//								String t2 = time1.get(x);
//								time += t + "&nbsp;-&nbsp;" + t2 + "hrs <br/>";
//							}
//							alInner.add(time);
//						} else {
//							alInner.add("Not Started");
//							alInner.add("Not Started");
//							alInner.add("Not Started");
//						}
//						totalhrz1 = new ArrayList<String>();
//						taskStatus1 = new ArrayList<String>();
//						time1 = new ArrayList<String>();
//						if (per < 100 && isFinish.equals("n")) {
//							alInner.add("Pending");
//						} else {
//							alInner.add("Finished");
//						}
//						if (isApprove.equals("n")) {
//							alInner.add("Pending");
//						} else {
//							alInner.add("Approved");
//						}
//					} else {
//						alInner.add(al_work + "hrs");
//						alInner.add(per + "%");
//						alInner.add("");
//						alInner.add("");
//					}
//					outInner.add(alInner);
//				}
//				activityDetailMap.put(index.get(k), outInner);
//			}
//			request.setAttribute("activityDetailMap", activityDetailMap);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}

	
//	public void getEmpName(Connection con) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from employee_personal_details");
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				EmpNameMap.put(rs.getInt("emp_per_id"), rs.getString("emp_fname"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		
//	}

	

//	public void getTimeDetail(int t_id) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from task_activity where activity_id=?");
//			pst.setInt(1, t_id);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				time1.add(rs.getString("start_time").substring(0, 5));
//				String s = rs.getString("end_time");
//				if (s == null) {
//					time1.add("-");
//				} else {
//					time1.add(rs.getString("end_time").substring(0, 5));
//				}
//				totalhrz1.add(rs.getString("actual_hrs"));
//				taskStatus1.add(rs.getString("task_status"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
//	public void getCompletedProjectDetails() {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		try {
//			
//			con = db.makeConnection(con);
//			
//			Map<String, List<String>> hmProject = new HashMap<String, List<String>>();
//			Map<String, List<String>> hmTasks = new HashMap<String, List<String>>();
//			Map<String, String> hmEmployee = CF.getEmpNameMap(con, null, null);
//				
//			Map<String, String> hmClientMap = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from client_details");
//			rs = pst.executeQuery();
//			
//			while(rs.next()){
//				hmClientMap.put(rs.getString("client_id"), rs.getString("client_name"));
//			}
//			rs.close();
//			pst.close();
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from projectmntnc where approve_status='approved' ");
//			if(getF_service()!=null && getF_service().length>0){
//				StringBuilder service=getConcateData(getF_service());
//				sbQuery.append(" and service in ("+service+") ");
//			}
//			if(getClient()!=null && getClient().length>0){
//				sbQuery.append(" and client_id in ("+StringUtils.join(getClient(), ",")+") ");
//			}
//			/*if(getSkill()!=null && getSkill().length>0){
//				StringBuilder skill=getConcateData1(getSkill());
//				sbQuery.append(" and pro_id in (select pro_id from project_skill_details where  skill_id in (" + skill.toString() + "))");
//			}*/
//			if(getPro_id()!=null && getPro_id().length>0){
//				sbQuery.append(" and pro_id in ("+StringUtils.join(getPro_id(), ",")+") ");
//			}
//			if(getManagerId()!=null && getManagerId().length>0){
//				sbQuery.append(" and added_by in ("+StringUtils.join(getManagerId(), ",")+") ");
//			}
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
//				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
//			}
//			
//			pst = con.prepareStatement(sbQuery.toString());
////			System.out.println("pst======>"+pst);
//			rs = pst.executeQuery();
//			while(rs.next()){
//				List<String> alInner = new ArrayList<String>();
//				alInner.add(rs.getString("pro_id"));
//				alInner.add(rs.getString("pro_name"));
//				alInner.add(rs.getString("priority"));
//				alInner.add(rs.getString("service"));
//				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
//				/*if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("already_work_days")))+" days");
//				} else {
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" hrs");
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("already_work")))+" hrs");
//				}*/
//				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
//					String actualTime = CF.getProjectActualTime(con, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
//					alInner.add(actualTime+" days");
//				} else {
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" hrs");
//					String actualTime = CF.getProjectActualTime(con, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
//					alInner.add(actualTime+" hrs");
//				}
//				
//				alInner.add(rs.getString("completed"));
//				alInner.add((("n".equalsIgnoreCase(rs.getString("approve_status")))?"Pending":"Completed"));
//				alInner.add(uF.showData(hmClientMap.get(rs.getString("client_id")), ""));
//				 
//				hmProject.put(rs.getString("pro_id"), alInner);
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("hmProject", hmProject);
//			
//			if(getPro_id()!=null && getPro_id().length>0){
//				pst = con.prepareStatement("select pcmc.actual_calculation_type, ai.reassign_by, pcmc.billing_type, ai.pro_id, ai.task_id, ai.completed, ai.idealtime, ai.already_work_days, ai.already_work, ai.deadline, ai.end_date, ai.taskstatus, ai.approve_status, ai.finish_task, ai.activity_name, ai.priority, ai.emp_id from activity_info ai, projectmntnc pcmc where pcmc.pro_id = ai.pro_id and pcmc.pro_id in ("+StringUtils.join(getPro_id(), ",")+")  and is_milestone=?");
//				pst.setBoolean(1,false);
//			}else{
//				pst = con.prepareStatement("select pcmc.actual_calculation_type, ai.reassign_by, pcmc.billing_type, ai.pro_id, ai.task_id, ai.completed, ai.idealtime, ai.already_work_days, ai.already_work, ai.deadline, ai.end_date, ai.taskstatus, ai.approve_status, ai.finish_task, ai.activity_name, ai.priority, ai.emp_id from activity_info ai, projectmntnc pcmc where pcmc.pro_id = ai.pro_id and is_milestone=? order by pcmc.pro_id");
//				pst.setBoolean(1,false);
//			}	
////			System.out.println("pst ===>> " + pst);
//			rs = pst.executeQuery();
//			
//			String strProjectIdNew = null;
//			String strProjectIdOld = null;
//			List<String> alInner = new ArrayList<String>();
//			while(rs.next()){
//				
//				strProjectIdNew = rs.getString("pro_id"); 
//				if(strProjectIdNew!=null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)){
//					alInner = new ArrayList<String>();
//				}
//				alInner.add(rs.getString("task_id"));
//				alInner.add(rs.getString("activity_name"));
//				alInner.add(rs.getString("priority"));
//				
//				alInner.add(uF.showData(hmEmployee.get(rs.getString("emp_id")), ""));
//				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
//				
//				// ****************************** Task wise Actual Time ******************************
//				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("already_work_days")))+" days");
//				} else {
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" hrs");
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("already_work")))+" hrs");
//				}
//				
//				
//				alInner.add(uF.showData(rs.getString("completed"), "0"));
//				
//				hmTasks.put(rs.getString("pro_id"), alInner);
//				
//				strProjectIdOld = strProjectIdNew;
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("hmTasks", hmTasks);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
//	public void getBlockedProjectDetails() {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		try {
//			
//			con = db.makeConnection(con);
//			
//			Map<String, List<String>> hmProject = new HashMap<String, List<String>>();
//			Map<String, List<String>> hmTasks = new HashMap<String, List<String>>();
//			Map<String, String> hmEmployee = CF.getEmpNameMap(con,null, null);
//				
//			Map<String, String> hmClientMap = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from client_details");
//			rs = pst.executeQuery();
//			
//			while(rs.next()){
//				hmClientMap.put(rs.getString("client_id"), rs.getString("client_name"));
//			}
//			rs.close();
//			pst.close();
//			
//			Map<String, String> hmProjectService = CF.getProjectServicesMap(con, false);
//			
//			StringBuilder sbQuery = new StringBuilder();			
//			sbQuery.append("select * from projectmntnc where approve_status='blocked' ");			
//			if(getF_service()!=null && getF_service().length>0){
//				StringBuilder service=getConcateData(getF_service());
//				sbQuery.append(" and service in ("+service+") ");
//			}
//			if(getClient()!=null && getClient().length>0){
//				sbQuery.append(" and client_id in ("+StringUtils.join(getClient(), ",")+") ");
//			}
//			/*if(getSkill()!=null && getSkill().length>0){
//				StringBuilder skill=getConcateData1(getSkill());
//				sbQuery.append(" and pro_id in (select pro_id from project_skill_details where  skill_id in ("+ skill.toString() +"))");
//			}*/
//			if(getPro_id()!=null && getPro_id().length>0){
//				sbQuery.append(" and pro_id in ("+StringUtils.join(getPro_id(), ",")+") ");
//			}
//			if(getManagerId()!=null && getManagerId().length>0){
//				sbQuery.append(" and added_by in ("+StringUtils.join(getManagerId(), ",")+") ");
//			}
//			
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
//				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");
//				
//			}
//			pst = con.prepareStatement(sbQuery.toString());
//			rs = pst.executeQuery();
//			GetPriorityList objGP = new GetPriorityList();
//			while(rs.next()){
//				List<String> alInner = new ArrayList<String>();
//				alInner.add(rs.getString("pro_id"));
//				alInner.add(rs.getString("pro_name"));
//				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), ""));
//				
//				alInner.add(hmProjectService.get(rs.getString("service")));
//				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
//				
//				/*if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("already_work_days")))+" days");
//				} else {
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" hrs");
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("already_work")))+" hrs");
//				}*/
//				
//				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
//					String actualTime = CF.getProjectActualTime(con, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
//					alInner.add(actualTime+" days");
//				} else {
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" hrs");
//					String actualTime = CF.getProjectActualTime(con, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
//					alInner.add(actualTime+" hrs");
//				}
//				
//				alInner.add(rs.getString("completed"));
//				alInner.add((("n".equalsIgnoreCase(rs.getString("approve_status")))?"Pending":"Completed"));
//				alInner.add(uF.showData(hmClientMap.get(rs.getString("client_id")), ""));
//				
//				hmProject.put(rs.getString("pro_id"), alInner);
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("hmProject", hmProject);
//			
//			if(getPro_id()!=null && getPro_id().length>0){
//				pst = con.prepareStatement("select pcmc.actual_calculation_type, ai.reassign_by, pcmc.billing_type, ai.pro_id, ai.task_id, ai.completed, ai.idealtime, ai.already_work_days, ai.already_work, ai.deadline, ai.end_date, ai.taskstatus, ai.approve_status, ai.finish_task, ai.activity_name, ai.priority, ai.emp_id from activity_info ai, projectmntnc pcmc where pcmc.pro_id = ai.pro_id and pcmc.pro_id in ("+StringUtils.join(getPro_id(), ",")+")  and is_milestone=?");
//				pst.setBoolean(1,false);
//			}else{
//				pst = con.prepareStatement("select pcmc.actual_calculation_type, ai.reassign_by, pcmc.billing_type, ai.pro_id, ai.task_id, ai.completed, ai.idealtime, ai.already_work_days, ai.already_work, ai.deadline, ai.end_date, ai.taskstatus, ai.approve_status, ai.finish_task, ai.activity_name, ai.priority, ai.emp_id from activity_info ai, projectmntnc pcmc where pcmc.pro_id = ai.pro_id and is_milestone=? order by pcmc.pro_id");
//				pst.setBoolean(1,false);
//			}	
//			rs = pst.executeQuery();
//			
//			String strProjectIdNew = null;
//			String strProjectIdOld = null;
//			List<String> alInner = new ArrayList<String>();
//			while(rs.next()){
//				
//				strProjectIdNew = rs.getString("pro_id"); 
//				if(strProjectIdNew!=null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)){
//					alInner = new ArrayList<String>();
//				}
//				alInner.add(rs.getString("task_id"));
//				alInner.add(rs.getString("activity_name"));
//				alInner.add(rs.getString("priority"));
//				
//				alInner.add(uF.showData(hmEmployee.get(rs.getString("emp_id")), ""));
//				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
//				
//				/*if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("already_work_days")))+" days");
//				} else {
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" hrs");
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("already_work")))+" hrs");
//				}*/
//				
//				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
//					String actualTime = CF.getProjectActualTime(con, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
//					alInner.add(actualTime+" days");
//				} else {
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" hrs");
//					String actualTime = CF.getProjectActualTime(con, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
//					alInner.add(actualTime+" hrs");
//				}
//				alInner.add(uF.showData(rs.getString("completed"), "0"));
//				
//				hmTasks.put(rs.getString("pro_id"), alInner);
//				
//				strProjectIdOld = strProjectIdNew;
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("hmTasks", hmTasks);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public List<String> getTime1() {
		return time1;
	}

	public void setTime1(List<String> time1) {
		this.time1 = time1;
	}

	public List<String> getTaskStatus1() {
		return taskStatus1;
	}

	public void setTaskStatus1(List<String> taskStatus1) {
		this.taskStatus1 = taskStatus1;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}
	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}
	
	public List<FillProjectOwnerList> getProOwnerList() {
		return proOwnerList;
	}

	public void setProOwnerList(List<FillProjectOwnerList> proOwnerList) {
		this.proOwnerList = proOwnerList;
	}

	public List<FillSkills> getSkillList() {
		return skillList;
	}
	
	public void setSkillList(List<FillSkills> skillList) {
		this.skillList = skillList;
	}
	
	public List<FillClients> getClientList() {
		return clientList;
	}
	
	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}
	
	public List<String> getTotalhrz1() {
		return totalhrz1;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setTotalhrz1(List<String> totalhrz1) {
		this.totalhrz1 = totalhrz1;
	}
	
	public int getTask_id() {
		return task_id;
	}

	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}

	public List<FillProjectList> getProjectdetailslist() {
		return projectdetailslist;
	}

	public void setProjectdetailslist(List<FillProjectList> projectdetailslist) {
		this.projectdetailslist = projectdetailslist;
	}

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Map<Integer, List<String>> getActivity_al() {
		return activity_al;
	}

	public void setActivity_al(Map<Integer, List<String>> activity_al) {
		this.activity_al = activity_al;
	}

	public String getApprove() {
		return approve;
	}

	public void setApprove(String approve) {
		this.approve = approve;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<Integer> getIndex() {
		return index;
	}

	public void setIndex(List<Integer> index) {
		this.index = index;
	}

	public Map<Integer, List<String>> getAl() {
		return al;
	}

	public void setAl(Map<Integer, List<String>> al) {
		this.al = al;
	}

	// public List<String> getTime() {
	// return time;
	// }
	// public void setTime(List<String> time) {
	// this.time = time;
	// }
	// public List<String> getTaskStatus() {
	// return taskStatus;
	// }
	// public void setTaskStatus(List<String> taskStatus) {
	// this.taskStatus = taskStatus;
	// }
	// public List<String> getTotalhrz() {
	// return totalhrz;
	// }
	// public void setTotalhrz(List<String> totalhrz) {
	// this.totalhrz = totalhrz;
	// }
	// public List<String> getTaskdate() {
	// return taskdate;
	// }
	// public void setTaskdate(List<String> taskdate) {
	// this.taskdate = taskdate;
	// }
	public List<String> getAlInner() {
		return alInner;
	}

	public void setAlInner(List<String> alInner) {
		this.alInner = alInner;
	}

	public List<Integer> getProjectidlist() {
		return projectidlist;
	}

	public void setProjectidlist(List<Integer> projectidlist) {
		this.projectidlist = projectidlist;
	}

	public List<String> getProjectlist() {
		return projectlist;
	}

	public void setProjectlist(List<String> projectlist) {
		this.projectlist = projectlist;
	}

	@Override
	public void setSession(Map arg0) {
		// TODO Auto-generated method stub
	}

	public int getSingleProid() {
		return singleProid;
	}

	public void setSingleProid(int singleProid) {
		this.singleProid = singleProid;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}
	
	public String getBlocked() {
		return blocked;
	}
	
	public void setBlocked(String blocked) {
		this.blocked = blocked;
	}
	
	public String[] getPro_id() {
		return pro_id;
	}
	
	public void setPro_id(String[] pro_id) {
		this.pro_id = pro_id;
	}
	
	public String[] getSkill() {
		return skill;
	}
	
	public void setSkill(String[] skill) {
		this.skill = skill;
	}
	
	public String[] getManagerId() {
		return managerId;
	}
	
	public void setManagerId(String[] managerId) {
		this.managerId = managerId;
	}
	
	public String[] getClient() {
		return client;
	}
	
	public void setClient(String[] client) {
		this.client = client;
	}
	
	public String[] getF_service() {
		return f_service;
	}
	
	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}
	
	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public String getStrDivCount() {
		return strDivCount;
	}

	public void setStrDivCount(String strDivCount) {
		this.strDivCount = strDivCount;
	}

	public List<FillDependentTaskList> getDependencyList() {
		return dependencyList;
	}

	public void setDependencyList(List<FillDependentTaskList> dependencyList) {
		this.dependencyList = dependencyList;
	}

	public List<GetDependancyTypeList> getDependancyTypeList() {
		return dependancyTypeList;
	}

	public void setDependancyTypeList(List<GetDependancyTypeList> dependancyTypeList) {
		this.dependancyTypeList = dependancyTypeList;
	}

	public List<FillTaskEmpList> getTaskEmpNamesList() {
		return TaskEmpNamesList;
	}

	public void setTaskEmpNamesList(List<FillTaskEmpList> taskEmpNamesList) {
		TaskEmpNamesList = taskEmpNamesList;
	}

	public List<FillSkills> getEmpSkillList() {
		return empSkillList;
	}

	public void setEmpSkillList(List<FillSkills> empSkillList) {
		this.empSkillList = empSkillList;
	}

	public List<GetPriorityList> getPriorityList() {
		return priorityList;
	}

	public void setPriorityList(List<GetPriorityList> priorityList) {
		this.priorityList = priorityList;
	}

	public String getAddTask() {
		return addTask;
	}

	public void setAddTask(String addTask) {
		this.addTask = addTask;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getReassign() {
		return reassign;
	}

	public void setReassign(String reassign) {
		this.reassign = reassign;
	}

	public String getReschedule() {
		return reschedule;
	}

	public void setReschedule(String reschedule) {
		this.reschedule = reschedule;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getAllowDeny() {
		return allowDeny;
	}

	public void setAllowDeny(String allowDeny) {
		this.allowDeny = allowDeny;
	}

	public String getProStatus() {
		return proStatus;
	}

	public void setProStatus(String proStatus) {
		this.proStatus = proStatus;
	}

	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(String assignedBy) {
		this.assignedBy = assignedBy;
	}

	public String getRecurrOrMiles() {
		return recurrOrMiles;
	}

	public void setRecurrOrMiles(String recurrOrMiles) {
		this.recurrOrMiles = recurrOrMiles;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
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

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public List<FillServices> getSbuList() {
		return sbuList;
	}

	public void setSbuList(List<FillServices> sbuList) {
		this.sbuList = sbuList;
	}

	public String[] getF_sbu() {
		return f_sbu;
	}

	public void setF_sbu(String[] f_sbu) {
		this.f_sbu = f_sbu;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public List<FillWLocation> getWorkLocationList() {
		return workLocationList;
	}

	public void setWorkLocationList(List<FillWLocation> workLocationList) {
		this.workLocationList = workLocationList;
	}

	public String[] getLocation() {
		return location;
	}

	public void setLocation(String[] location) {
		this.location = location;
	}

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

	public String getStrSBU() {
		return strSBU;
	}

	public void setStrSBU(String strSBU) {
		this.strSBU = strSBU;
	}

	public String getStrService() {
		return strService;
	}

	public void setStrService(String strService) {
		this.strService = strService;
	}

	public String getStrProjectId() {
		return strProjectId;
	}

	public void setStrProjectId(String strProjectId) {
		this.strProjectId = strProjectId;
	}

	public String getStrManagerId() {
		return strManagerId;
	}

	public void setStrManagerId(String strManagerId) {
		this.strManagerId = strManagerId;
	}

	public String getStrClient() {
		return strClient;
	}

	public void setStrClient(String strClient) {
		this.strClient = strClient;
	}

	public String getSubmitType() {
		return submitType;
	}

	public void setSubmitType(String submitType) {
		this.submitType = submitType;
	}

}
