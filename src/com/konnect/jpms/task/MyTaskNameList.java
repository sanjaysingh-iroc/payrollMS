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
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserActivities;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MyTaskNameList extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	HttpServletResponse response;
	int id; 
	int emp_id;
	String strEmpOrgId;
	HttpSession session;
	String strUserType;
	
	int pro_id;
	String[] isFinish; 
	boolean isSingle = false; 
	CommonFunctions CF;
	
	String projectID;
	boolean flag;
	String proType; 
	
	String addTask;
	String addSubTask;
	
	String taskId;
	String parentTaskId;
	String proId;
	String taskAcceptStatus;
	String rStartDate;
	String rDeadline;
	String taskrescheduleComment;
	String taskreassignComment;
	
	String proPage;
	String minLimit;
	String loadMore;
	
	String taskSubtaskStatus;
	String assignedBy;
	String recurrOrMiles;
	String sortBy;
	String sortBy1;
	
//	String alertStatus;
//	String alert_type;
//	String alertID;
	
	String completeTask;
	
	String strSearchJob;
	
	List<FillProjectList> projectdetailslist;	//added by parvez
	
	public String execute() { 
			
		UtilityFunctions uF = new UtilityFunctions();
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		request.setAttribute(PAGE, "/jsp/task/MyTaskNameList.jsp");
		request.setAttribute(TITLE, "My Tasks");
		emp_id = uF.parseToInt((String) session.getAttribute(EMPID));
		strEmpOrgId = (String) session.getAttribute(ORGID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);

//		if(uF.parseToInt(getAlertID()) > 0) {
//			updateUserAlerts();
//		}
		
//		System.out.println("getTaskId() ===>> " + getTaskId());
		if(getProPage() == null || getProPage().equals("") || getProPage().equals("null")) {
			setProPage("1");
		}
		
		if(getCompleteTask() != null && getCompleteTask().equals("C")) {
			completeThisTask(uF);
		}
		
		if(getAddTask() != null && getAddTask().equals("Add")) {
			insertActivityDetails(uF);
		}
		
		if(getAddSubTask() != null && getAddSubTask().equals("Add")) {
			insertSubTaskDetails(uF);
		}
		
		if(getAddTask() != null && getAddTask().equals("Update")) {
			updateActivityDetails(uF);
		}
		
		if(getAddTask() != null && getAddTask().equals("Delete")) {
			deleteTask(uF);
		}
		
		if(getAddTask() != null && getAddTask().equals("Reassign")) {
			sendRequestForTaskReassign(uF);
		}
		
		if(getAddTask() != null && getAddTask().equals("Reschedule")) {
			sendRequestForTaskReschedule(uF);
		}
		
		if(getTaskAcceptStatus() != null && getTaskAcceptStatus().equals("1")) {
			acceptTaskOrReassignTaskRequest(uF);
		}
		
		if(getProType() == null || getProType().equals("L")) {
			flag = checkTaskStatus();
//			getProjectDetails();
		} else if(getProType() != null && getProType().equals("TR")) {
			flag = checkTaskStatus();
//			getProjectDetails();
		} else if(getProType() != null && getProType().equals("MR")) {
			flag = checkTaskStatus();
//			getProjectDetails();
		} else if(getProType() == null || getProType().equals("C")) {
//			getPastProjectDetails();
		}
		if(getSortBy() == null || getSortBy().equals("")) {
			setSortBy("1");
		}
		setSortBy1(getSortBy());
		
		if(getProType() == null || getProType().equals("") || getProType().equalsIgnoreCase("null")){
			projectdetailslist = new FillProjectList(request).fillProjectDetailsByEmp(emp_id, false, 0);
			
		}else if(getProType().equals("C")){
			projectdetailslist = new FillProjectList(request).fillProjectDetailsByEmp(emp_id, true, 0);
			
		}
		
		getSearchAutoCompleteData(uF);
		getProjectDetails();
		return LOAD;
	}
	
	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmProjectClientMap = CF.getProjectClientMap(con, uF);
			if(hmProjectClientMap == null) hmProjectClientMap = new HashMap<String, String>();
			Map<String, String> hmProjectNameMap = CF.getProjectNameMap(con);
			if(hmProjectNameMap == null) hmProjectNameMap = new HashMap<String, String>();

			SortedSet<String> setSearchList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from activity_info ai where ai.resource_ids like '%,"+emp_id+",%' and ai.task_id not in (select parent_task_id " +
				" from activity_info where resource_ids like '%,"+emp_id+",%' and parent_task_id is not null and task_accept_status != -1) ");
			if(getPro_id()!=0) {
				sbQuery.append(" and ai.pro_id="+getPro_id()+" ");
			}
			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
				sbQuery.append(" and approve_status='n' and task_accept_status = 1 ");
			} else if(getProType() == null || getProType().equals("TR")) {
				sbQuery.append(" and approve_status='n' and task_accept_status = 0 and task_from_my_self=false ");
			} else if(getProType() == null || getProType().equals("MR")) {
				sbQuery.append(" and approve_status='n' and (((task_accept_status=0 or task_accept_status = -1) and task_from_my_self=true) or (task_accept_status>1 and task_from_my_self=false)) ");
			} else if(getProType() != null && getProType().equals("C")) {
				sbQuery.append(" and approve_status='approved' ");
			}
			if(getTaskSubtaskStatus() != null && uF.parseToInt(getTaskSubtaskStatus()) == 1) {
				sbQuery.append(" and ai.completed>0 and ai.deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getTaskSubtaskStatus() != null && uF.parseToInt(getTaskSubtaskStatus()) == 2) {
				sbQuery.append(" and (ai.completed = 0 or ai.completed is null) and ai.deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getTaskSubtaskStatus() != null && uF.parseToInt(getTaskSubtaskStatus()) == 3) {
				sbQuery.append(" and ai.deadline < '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			}
			
			if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 1) {
				sbQuery.append(" and ai.pro_id in (select pro_id from projectmntnc where billing_type != 'F') ");
			} else if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 2) {
				sbQuery.append(" and ai.pro_id in (select pro_id from projectmntnc where billing_type = 'F') ");
			}
			
			if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) == -1) {
				sbQuery.append(" and ai.added_by = "+emp_id+" and ai.task_from_my_self = true ");
			} else if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) >0) {
				sbQuery.append(" and ai.added_by = "+uF.parseToInt(getAssignedBy())+" and ai.task_from_my_self = false ");
			}
			if(getSortBy() != null && uF.parseToInt(getSortBy()) == 1) {
				sbQuery.append(" order by ai.start_date desc ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 2) {
				sbQuery.append(" order by ai.start_date ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 3) {
				sbQuery.append(" order by ai.activity_name ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 4) {
				sbQuery.append(" order by ai.activity_name desc ");
			} else {
				sbQuery.append(" order by ai.start_date desc ");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst===> "+ pst);
			rs = pst.executeQuery();
			StringBuilder sbProId = null;
			while (rs.next()) {
				setSearchList.add(rs.getString("activity_name"));
				String strProName = hmProjectNameMap.get(rs.getString("pro_id"));
				if(uF.parseToInt(rs.getString("pro_id")) > 0 && strProName!=null && !strProName.trim().equals("")){
					setSearchList.add(strProName.trim());
				}
				if(sbProId == null){
					sbProId = new StringBuilder();
					sbProId.append(rs.getString("pro_id"));
				} else {
					sbProId.append(","+rs.getString("pro_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbProId != null){
				List<String> alProId = Arrays.asList(sbProId.toString().trim().split(","));
				StringBuilder sbPId = null;
				for(int i=0; alProId != null && i < alProId.size(); i++){
					if(alProId.get(i)!=null && !alProId.get(i).trim().equals("") && uF.parseToInt(alProId.get(i).trim()) > 0){
						if(sbPId == null){
							sbPId = new StringBuilder();
							sbPId.append(alProId.get(i).trim());
						} else {
							sbPId.append(","+alProId.get(i).trim());
						}
					}
				}
				
				if(sbPId!=null){
					pst = con.prepareStatement("select client_name from client_details where client_id in(select client_id from projectmntnc where pro_id > 0 " +
							"and pro_id in("+sbPId.toString()+"))");
//					System.out.println("pst===> "+ pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						setSearchList.add(rs.getString("client_name"));
					}
				}
			}
			
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
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
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	private void completeThisTask(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update activity_info set completed=100, approve_status='approved', end_date=? where task_id = ?");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt(getTaskId()));
			pst.executeUpdate();
//			System.out.println("pst ====>>> " + pst);
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


//	private void updateUserAlerts() {
//		Connection con = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		try {
//			con = db.makeConnection(con);
//			String strDomain = request.getServerName().split("\\.")[0];
//			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//			userAlerts.setStrDomain(strDomain);
//			userAlerts.setAlertID(getAlertID()); 
//			if(strUserType!=null && strUserType.equals(CUSTOMER)) {
//				userAlerts.setStrOther("other");
//			}
//			userAlerts.setStatus(DELETE_TR_ALERT);
//			Thread t = new Thread(userAlerts);
//			t.run();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//		}
//	}
	


	private void sendRequestForTaskReschedule(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update activity_info set task_accept_status=?, task_reassign_reschedule_comment=?, r_start_date=?, " +
				"r_deadline=?, requested_by=? where task_id = ?");
			pst.setInt(1, 2);
			pst.setString(2, getTaskrescheduleComment());
			pst.setDate(3, uF.getDateFormat(getrStartDate(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getrDeadline(), DATE_FORMAT));
			pst.setInt(5, emp_id);
			pst.setInt(6, uF.parseToInt(getTaskId()));
			int x = pst.executeUpdate();
			pst.close();
			
			/**
			 * Alerts
			 * */
			int nProId = 0;
			pst = con.prepareStatement("select pro_id,resource_ids from activity_info where task_id=?");
			pst.setInt(1, uF.parseToInt(getTaskId()));
			rs = pst.executeQuery();
			String strResourceIds = null;
			while(rs.next()) {
				nProId = rs.getInt("pro_id");
				strResourceIds = rs.getString("resource_ids");
			}
			rs.close();
			pst.close();
			
			if(nProId > 0 && x > 0) {
				List<String> alEmp = new ArrayList<String>();
				pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=? and _isteamlead = true");
				pst.setInt(1, nProId);
				rs = pst.executeQuery();
				while(rs.next()) {
					if(uF.parseToInt(rs.getString("emp_id")) > 0 && !alEmp.contains(rs.getString("emp_id"))) {
						alEmp.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
				
				String strDomain = request.getServerName().split("\\.")[0];
				String taskName = CF.getProjectTaskNameByTaskId(con, uF, getTaskId());
				String proName = CF.getProjectNameById(con, nProId+"");
				
				String alertData = "<div style=\"float: left;\"> <b>"+taskName+"</b> in <b>"+proName+"</b> project has been sent for reschedule by <b> "+CF.getEmpNameMapByEmpId(con, emp_id+"")+"</b>.</div>";
				String alertAction = "ViewMyProjects.action";
				StringBuilder taggedWith = null;
				for(String strEmp : alEmp) {
					if(taggedWith == null) {
						taggedWith = new StringBuilder();
						taggedWith.append(","+strEmp.trim()+",");
					} else {
						taggedWith.append(strEmp.trim()+",");
					}
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmp.trim());
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(TASK_REQUEST_RESCHEDULE_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
				
				List<String> alEmp1 = new ArrayList<String>();
				if(strResourceIds !=null && !strResourceIds.trim().equals("") && strResourceIds.contains(",")) {
					strResourceIds = strResourceIds.substring(1, strResourceIds.length()-1);
					List<String> alResource = Arrays.asList(strResourceIds.split(","));
					for(String strEmp : alResource) {
						if(!alEmp1.contains(strEmp.trim())) {
							alEmp1.add(strEmp.trim());
						}
					}
				}
				
				alertAction = "EmpViewProject.action";
				for(String strEmp : alEmp1) {
					if(taggedWith == null) {
						taggedWith = new StringBuilder();
						taggedWith.append(","+strEmp.trim()+",");
					} else {
						taggedWith.append(strEmp.trim()+",");
					}
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmp.trim());
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(TASK_REQUEST_RESCHEDULE_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
				
				if(taggedWith == null) {
					taggedWith = new StringBuilder();
				}
				
				String activityData = "<div style=\"float: left;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> T </span> <b>"+taskName+"</b> in <b>"+proName+"</b> project has been sent for reschedule by <b> "+CF.getEmpNameMapByEmpId(con, emp_id+"")+"</b>.</div>";
				UserActivities userAct = new UserActivities(con, uF, CF, request);
				userAct.setStrDomain(strDomain);
				userAct.setStrAlignWith(TASK+"");
				userAct.setStrAlignWithId(getTaskId());
				userAct.setStrTaggedWith(taggedWith.toString());
				userAct.setStrVisibilityWith(taggedWith.toString());
				userAct.setStrVisibility("2");
				userAct.setStrData(activityData);
				userAct.setStrSessionEmpId(emp_id+"");
				userAct.setStatus(INSERT_TR_ACTIVITY);
				Thread tt = new Thread(userAct);
				tt.run();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	private void sendRequestForTaskReassign(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update activity_info set task_accept_status = ?, task_reassign_reschedule_comment=?, requested_by=? where task_id = ?");
			pst.setInt(1, 3);
			pst.setString(2, getTaskreassignComment());
			pst.setInt(3, emp_id);
			pst.setInt(4, uF.parseToInt(getTaskId()));
			int x = pst.executeUpdate();
			pst.close();
			
			/**
			 * Alerts
			 * */
			int nProId = 0;
			pst = con.prepareStatement("select pro_id,resource_ids from activity_info where task_id=?");
			pst.setInt(1, uF.parseToInt(getTaskId()));
			rs = pst.executeQuery();
			String strResourceIds = null;
			while(rs.next()){
				nProId = rs.getInt("pro_id");
				strResourceIds = rs.getString("resource_ids");
			}
			rs.close();
			pst.close();
			if(nProId > 0 && x > 0){			
				List<String> alEmp = new ArrayList<String>();
				pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=? and _isteamlead = true");
				pst.setInt(1, nProId);
				rs = pst.executeQuery();
				while(rs.next()){
					if(uF.parseToInt(rs.getString("emp_id")) > 0 && !alEmp.contains(rs.getString("emp_id"))){
						alEmp.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
				
				String strDomain = request.getServerName().split("\\.")[0];
				String taskName = CF.getProjectTaskNameByTaskId(con, uF, getTaskId());
				String proName = CF.getProjectNameById(con, nProId+"");
				
				String alertData = "<div style=\"float: left;\"> <b>"+taskName+"</b> in <b>"+proName+"</b> project has been sent for reassign by <b> "+CF.getEmpNameMapByEmpId(con, emp_id+"")+"</b>.</div>";
				String alertAction = "ViewMyProjects.action";
				StringBuilder taggedWith = null;
				for(String strEmp : alEmp) {
					if(taggedWith == null) {
						taggedWith = new StringBuilder();
						taggedWith.append(","+strEmp.trim()+",");
					} else {
						taggedWith.append(strEmp.trim()+",");
					}
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmp.trim());
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(TASK_REQUEST_REASSIGN_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
				
				
				List<String> alEmp1 = new ArrayList<String>();
				if(strResourceIds !=null && !strResourceIds.trim().equals("") && strResourceIds.contains(",")){
					strResourceIds = strResourceIds.substring(1, strResourceIds.length()-1);
					List<String> alResource = Arrays.asList(strResourceIds.split(","));
					for(String strEmp : alResource) {
						if(!alEmp1.contains(strEmp.trim())){
							alEmp1.add(strEmp.trim());
						}
					}
				}
				
				alertAction = "MyWork.action";
				for(String strEmp : alEmp1) {
					if(taggedWith == null) {
						taggedWith = new StringBuilder();
						taggedWith.append(","+strEmp.trim()+",");
					} else {
						taggedWith.append(strEmp.trim()+",");
					}
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmp.trim());
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(TASK_REQUEST_REASSIGN_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
				
				if(taggedWith == null) {
					taggedWith = new StringBuilder();
				}
				
				String activityData = "<div style=\"float: left;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> T </span> <b>"+taskName+"</b> in <b>"+proName+"</b> project has been sent for reassign by <b> "+CF.getEmpNameMapByEmpId(con, emp_id+"")+"</b>.</div>";
				UserActivities userAct = new UserActivities(con, uF, CF, request);
				userAct.setStrDomain(strDomain);
				userAct.setStrAlignWith(TASK+"");
				userAct.setStrAlignWithId(getTaskId());
				userAct.setStrTaggedWith(taggedWith.toString());
				userAct.setStrVisibilityWith(taggedWith.toString());
				userAct.setStrVisibility("2");
				userAct.setStrData(activityData);
				userAct.setStrSessionEmpId(emp_id+"");
				userAct.setStatus(INSERT_TR_ACTIVITY);
				Thread tt = new Thread(userAct);
				tt.run();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	private void acceptTaskOrReassignTaskRequest(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update activity_info set task_accept_status = ? where task_id = ?");
			pst.setInt(1, 1);
			pst.setInt(2, uF.parseToInt(getTaskId()));
			int x = pst.executeUpdate();
			pst.close();
			
			/**
			 * Alerts
			 * */
			int nProId = 0;
			pst = con.prepareStatement("select pro_id from activity_info where task_id=?");
			pst.setInt(1, uF.parseToInt(getTaskId()));
			rs = pst.executeQuery();
			while(rs.next()){
				nProId = rs.getInt("pro_id");
			}
			rs.close();
			pst.close();
			if(nProId > 0 && x >0){			
				List<String> alEmp = new ArrayList<String>();
				pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=? and _isteamlead = true");
				pst.setInt(1, nProId);
				rs = pst.executeQuery();
				while(rs.next()){
					if(uF.parseToInt(rs.getString("emp_id")) > 0 && !alEmp.contains(rs.getString("emp_id"))){
						alEmp.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
				
				String strDomain = request.getServerName().split("\\.")[0];
				
				String taskName = CF.getProjectTaskNameByTaskId(con, uF, getTaskId());
				String proName = CF.getProjectNameById(con, nProId+"");
				
				String alertData = "<div style=\"float: left;\"> <b>"+taskName+"</b> in <b>"+proName+"</b> project has been accepted by <b> "+CF.getEmpNameMapByEmpId(con, emp_id+"")+"</b>.</div>";
				String alertAction = "ViewMyProjects.action";
				StringBuilder taggedWith = null;
				for(String strEmp : alEmp) {
					if(taggedWith == null) {
						taggedWith = new StringBuilder();
						taggedWith.append(","+strEmp.trim()+",");
					} else {
						taggedWith.append(strEmp.trim()+",");
					}
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmp.trim());
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(TASK_ACCEPT_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
				
				if(taggedWith == null) {
					taggedWith = new StringBuilder();
				}
				
				String activityData = "<div style=\"float: left;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> T </span> <b>"+taskName+"</b> in <b>"+proName+"</b> project has been accepted by <b> "+CF.getEmpNameMapByEmpId(con, emp_id+"")+"</b>.</div>";
				UserActivities userAct = new UserActivities(con, uF, CF, request);
				userAct.setStrDomain(strDomain);
				userAct.setStrAlignWith(TASK+"");
				userAct.setStrAlignWithId(getTaskId());
				userAct.setStrTaggedWith(taggedWith.toString());
				userAct.setStrVisibilityWith(taggedWith.toString());
				userAct.setStrVisibility("2");
				userAct.setStrData(activityData);
				userAct.setStrSessionEmpId(emp_id+"");
				userAct.setStatus(INSERT_TR_ACTIVITY);
				Thread tt = new Thread(userAct);
				tt.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	public void deleteTask(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			int parentTaskId = 0; 
			pst = con.prepareStatement("select parent_task_id from activity_info where task_id = ? and parent_task_id != 0");
			pst.setInt(1, uF.parseToInt(getTaskId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				parentTaskId = rs.getInt("parent_task_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM activity_info WHERE task_id=?");
			pst.setInt(1, uF.parseToInt(getTaskId()));
			pst.executeUpdate();
			pst.close();

			double dblAllCompleted = 0.0d;
			int subTaskCnt = 0;
			pst = con.prepareStatement("select sum(completed) as completed, count(task_id) as count from activity_info where parent_task_id = ? and task_accept_status = 1");
			pst.setInt(1, parentTaskId);
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
				pst.setInt(2, parentTaskId);
				pst.execute();
				pst.close();
			}
			
			
			String pro_id = CF.getProjectIdByTaskId(con, taskId);
			pst = con.prepareStatement("select sum(completed)/count(task_id) as avrg from activity_info where pro_id=? and parent_task_id = 0 and task_accept_status = 1");
			pst.setInt(1, uF.parseToInt(pro_id));
			rs = pst.executeQuery();
			String projectCompletePercent = null;
			while (rs.next()) {
				projectCompletePercent = uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rs.getString("avrg")));
			}
			rs.close();
			pst.close();
	
			pst = con.prepareStatement("update projectmntnc set completed=? where pro_id=? ");
			pst.setDouble(1, uF.parseToDouble(projectCompletePercent));
			pst.setInt(2, uF.parseToInt(pro_id));
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
	
	
	public void updateActivityDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String[] taskname = request.getParameterValues("taskname"+getTaskId());
			String[] taskDescription = request.getParameterValues("taskDescription"+getTaskId());
			String[] isRecurringTask = request.getParameterValues("isRecurringTask"+getTaskId());
			String[] taskID = request.getParameterValues("taskID"+getTaskId());
			String[] dependency = request.getParameterValues("dependency"+getTaskId());
			String[] dependencyType = request.getParameterValues("dependencyType"+getTaskId());
			
//			String task_dependency = request.getParameter("task_dependency");
//			String dependency_type = request.getParameter("dependency_type");

			String[] priority = request.getParameterValues("priority"+getTaskId());
//			String[] empSkills = request.getParameterValues("empSkills"+getTaskId());
			
			String[] startDate = request.getParameterValues("startDate"+getTaskId());
			String[] deadline1 = request.getParameterValues("deadline1"+getTaskId());
			String[] idealTime = request.getParameterValues("idealTime"+getTaskId());
			String[] colourCode = request.getParameterValues("colourCode"+getTaskId());
//			String[] emp_id1 = request.getParameterValues("emp_id"+getTaskId());
			
//			StringBuilder sbEmpIds = null;
//			System.out.println("emp_id ===>> " + emp_id);
//			if (emp_id1 != null && emp_id1.length > 0) {
//				List<String> empIdList = Arrays.asList(emp_id1);
//				
//				for (int a = 0; empIdList != null && a < empIdList.size(); a++) {
//					if (sbEmpIds == null) {
//						sbEmpIds = new StringBuilder();
//						sbEmpIds.append("," + empIdList.get(a).trim()+",");
//					} else {
//						sbEmpIds.append(empIdList.get(a).trim()+",");
//					}
//				}
//			}
//			if (sbEmpIds == null) {
//				sbEmpIds = new StringBuilder();
//			}
			
			con = db.makeConnection(con);

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
				if(uF.parseToInt(taskID[i]) > 0) {
					
					pst = con.prepareStatement("update activity_info set activity_name=?, priority=?, resource_ids=?, deadline=?, idealtime=?, start_date=?," +
						" dependency_task=?, dependency_type=?, color_code=?, taskstatus=?, task_skill_id=?, task_description=?,added_by=?," +
						"task_freq_name=?,recurring_task=?, task_from_my_self=? where task_id =? ");
					pst.setString(1, taskname[i]);
					pst.setString(2, priority[i]);
//					pst.setInt(3, uF.parseToInt(emp_id[i]));
					pst.setString(3, ","+emp_id+",");
					pst.setDate(4, uF.getDateFormat(deadline1[i], DATE_FORMAT));
					pst.setInt(5, uF.parseToInt(idealTime[i]));
					pst.setDate(6, uF.getDateFormat(startDate[i], DATE_FORMAT));
					pst.setInt(7, uF.parseToInt(dependency[i]));
					pst.setString(8, dependencyType[i]);
					pst.setString(9, colourCode[i]);
					if(uF.parseToInt(getParentTaskId()) > 0) {
						pst.setString(10, "New Sub Task");
					} else {
						pst.setString(10, "New Task");
					}
//					pst.setInt(11, uF.parseToInt(empSkills[i]));
					pst.setInt(11, 0);
					pst.setString(12, taskDescription[i]);
					pst.setInt(13, emp_id);
					pst.setString(14, freqTaskName);
					pst.setInt(15, uF.parseToInt(isRecurringTask[i]));
					pst.setBoolean(16, true);
					pst.setInt(17, uF.parseToInt(taskID[i]));
					pst.executeUpdate();
					pst.close();
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


	public void insertSubTaskDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String[] subtaskname = request.getParameterValues("subtaskname");
			String[] subTaskDescription = request.getParameterValues("subTaskDescription");
			String[] isRecurringSubTask = request.getParameterValues("isRecurringSubTask");
			String[] subTaskID = request.getParameterValues("subTaskID");
			String[] strTasks = request.getParameterValues("strTasks");
			
			String[] subDependency = request.getParameterValues("subDependency");
			String[] subDependencyType = request.getParameterValues("subDependencyType");
			
			String[] subpriority = request.getParameterValues("subpriority");
			
			String[] substartDate = request.getParameterValues("substartDate");
			String[] subdeadline1 = request.getParameterValues("subdeadline1");
			String[] subidealTime = request.getParameterValues("subidealTime");
			String[] subcolourCode = request.getParameterValues("subcolourCode");
	
			con = db.makeConnection(con);
			
//			System.out.println("subTaskID ===>>> " + subTaskID.length);
			
			for (int i = 0; subTaskID!=null && i<subTaskID.length; i++) {
//				System.out.println("strTasks[i] ===>>> " + strTasks[i]);
				String strProId = CF.getProjectIdByTaskId(con, strTasks[i]);
				
				String freqTaskName = null;
				pst = con.prepareStatement("select freq_start_date,freq_end_date from projectmntnc_frequency where pro_id=?");
				pst.setInt(1, uF.parseToInt(strProId));
				rs = pst.executeQuery();
				while (rs.next()) {
					freqTaskName = uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT_STR)+" to "+uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT_STR);
				}
				rs.close();
				pst.close();
				
				if(substartDate[i] != null && !substartDate[i].equals("") && !substartDate[i].equals("-") && subdeadline1[i] !=null && !subdeadline1[i].equals("") && !subdeadline1[i].equals("-")) {
					freqTaskName = uF.getDateFormat(substartDate[i], DATE_FORMAT, DATE_FORMAT_STR)+" to "+uF.getDateFormat(subdeadline1[i], DATE_FORMAT, DATE_FORMAT_STR);
				}
				
				if(uF.parseToInt(subTaskID[i]) > 0) {
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("update activity_info set activity_name=?, priority=?, resource_ids=?, deadline=?, idealtime=?, start_date=?," +
						" dependency_task=?, dependency_type=?, color_code=?, taskstatus=?, task_skill_id=?, task_description=?,added_by=?," +
						"task_freq_name=?,recurring_task=?, task_from_my_self=?, pro_id=?,parent_task_id=?,requested_by=?");
					if(uF.parseToInt(strProId) == 0) {
						sbQuery.append(",task_accept_status=1");
					}
					sbQuery.append(" where task_id =? ");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setString(1, subtaskname[i]);
					pst.setString(2, subpriority[i]);
					pst.setString(3, ","+emp_id+",");
					pst.setDate(4, uF.getDateFormat(subdeadline1[i], DATE_FORMAT));
					pst.setDouble(5, uF.parseToDouble(subidealTime[i]));
					pst.setDate(6, uF.getDateFormat(substartDate[i], DATE_FORMAT));
					pst.setInt(7, uF.parseToInt(subDependency[i]));
					pst.setString(8, subDependencyType[i]);
					pst.setString(9, subcolourCode[i]);
					pst.setString(10, "New Sub Task");
					pst.setInt(11, 0);
					pst.setString(12, subTaskDescription[i]);
					pst.setInt(13, emp_id);
					pst.setString(14, freqTaskName);
					pst.setInt(15, uF.parseToInt(isRecurringSubTask[i]));
					pst.setBoolean(16, true);
					pst.setInt(17, uF.parseToInt(strProId));
					pst.setInt(18, uF.parseToInt(strTasks[i]));
					pst.setInt(19, emp_id);
					pst.setInt(20, uF.parseToInt(subTaskID[i]));
					pst.executeUpdate();
					pst.close();
				} else {
					pst = con.prepareStatement("insert into activity_info (activity_name,priority,resource_ids,deadline,idealtime,start_date," +
						"dependency_task,dependency_type,color_code,taskstatus,pro_id,task_skill_id,task_description,added_by,task_freq_name," +
						"recurring_task,parent_task_id,task_from_my_self,requested_by,task_accept_status)" +
						" values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?) ");
					pst.setString(1, subtaskname[i]);
					pst.setString(2, subpriority[i]);
	//				pst.setInt(3, uF.parseToInt(emp_id[i]));
					pst.setString(3, ","+emp_id+",");
					pst.setDate(4, uF.getDateFormat(subdeadline1[i], DATE_FORMAT));
					pst.setDouble(5, uF.parseToDouble(subidealTime[i]));
					pst.setDate(6, uF.getDateFormat(substartDate[i], DATE_FORMAT));
					pst.setInt(7, uF.parseToInt(subDependency[i]));
					pst.setString(8, subDependencyType[i]);
					pst.setString(9, subcolourCode[i]); 
					pst.setString(10, "New Sub Task");
					pst.setInt(11, uF.parseToInt(strProId));
					pst.setInt(12, 0);
					pst.setString(13, subTaskDescription[i]);
					pst.setInt(14, emp_id);
					pst.setString(15, freqTaskName);
					pst.setInt(16, uF.parseToInt(isRecurringSubTask[i]));
					pst.setInt(17, uF.parseToInt(strTasks[i]));
					pst.setBoolean(18, true);
					pst.setInt(19, emp_id);
					if(uF.parseToInt(strProId) == 0) {
						pst.setInt(20, 1);
					} else {
						pst.setInt(20, 0);
					}
					pst.executeUpdate();
					pst.close();
				}
				String strTaskId = subTaskID[i];
				if(uF.parseToInt(strTaskId) == 0) {
					pst = con.prepareStatement("select max(task_id) as task_id from activity_info");
					rs = pst.executeQuery();
					while (rs.next()) {
						strTaskId =	rs.getString("task_id");
					}
					rs.close();
					pst.close();
				}
				
				double dblAllCompleted = 0.0d;
				int subTaskCnt = 0;
				pst = con.prepareStatement("select sum(completed) as completed, count(task_id) as count from activity_info where parent_task_id = ? and task_accept_status = 1");
				pst.setInt(1, uF.parseToInt(strTaskId));
	//			pst.setInt(2, uF.parseToInt(taskId));
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
				
				pst = con.prepareStatement("select sum(completed)/count(task_id) as avrg from activity_info where pro_id=? and parent_task_id = 0 and task_accept_status = 1");
				pst.setInt(1, uF.parseToInt(strProId));
				rs = pst.executeQuery();
				String projectCompletePercent = null;
				while (rs.next()) {
					projectCompletePercent = uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rs.getString("avrg")));
				}
				rs.close();
				pst.close();
		
				pst = con.prepareStatement("update projectmntnc set completed=? where pro_id=? ");
				pst.setDouble(1, uF.parseToDouble(projectCompletePercent));
				pst.setInt(2, uF.parseToInt(strProId));
				pst.execute();
				pst.close();
				
				
				/**
				 * Alerts
				 * */
				List<String> alEmp = new ArrayList<String>();
				pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=? and _isteamlead = true");
				pst.setInt(1, uF.parseToInt(strProId));
				rs = pst.executeQuery();
				while(rs.next()){
					if(uF.parseToInt(rs.getString("emp_id")) > 0 && !alEmp.contains(rs.getString("emp_id"))) {
						alEmp.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
				
				String strDomain = request.getServerName().split("\\.")[0];
				String taskName = CF.getProjectTaskNameByTaskId(con, uF, strTaskId);
				String proName = CF.getProjectNameById(con, strProId+"");
				
				String alertData = "<div style=\"float: left;\"> <b>"+taskName+"</b> in <b>"+proName+"</b> project new task has been sent for acceptance by <b> "+CF.getEmpNameMapByEmpId(con, emp_id+"")+"</b>.</div>";
				String alertAction = "ViewMyProjects.action";
				StringBuilder taggedWith = null;
				for(String strEmp : alEmp) {
					if(taggedWith == null) {
						taggedWith = new StringBuilder();
						taggedWith.append(","+strEmp.trim()+",");
					} else {
						taggedWith.append(strEmp.trim()+",");
					}
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmp.trim());
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(TASK_NEW_REQUEST_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
				
				if(taggedWith == null) {
					taggedWith = new StringBuilder();
				}
				
				String activityData = "<div style=\"float: left;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> T </span> <b>"+taskName+"</b> in <b>"+proName+"</b> project has been sent for acceptance by <b> "+CF.getEmpNameMapByEmpId(con, emp_id+"")+"</b>.</div>";
				UserActivities userAct = new UserActivities(con, uF, CF, request);
				userAct.setStrDomain(strDomain);
				userAct.setStrAlignWith(TASK+"");
				userAct.setStrAlignWithId(strTaskId);
				userAct.setStrTaggedWith(taggedWith.toString());
				userAct.setStrVisibilityWith(taggedWith.toString());
				userAct.setStrVisibility("2");
				userAct.setStrData(activityData);
				userAct.setStrSessionEmpId(emp_id+"");
				userAct.setStatus(INSERT_TR_ACTIVITY);
				Thread tt = new Thread(userAct);
				tt.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void insertActivityDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String[] taskname = request.getParameterValues("taskname");
			String[] taskDescription = request.getParameterValues("taskDescription");
			String[] isRecurringTask = request.getParameterValues("isRecurringTask");
			String[] taskID = request.getParameterValues("taskID");
			String[] strProjects = request.getParameterValues("strProjects");
			
			String[] dependency = request.getParameterValues("dependency");
			String[] dependencyType = request.getParameterValues("dependencyType");
			
			String[] priority = request.getParameterValues("priority");
			
			String[] startDate = request.getParameterValues("startDate");
			String[] deadline1 = request.getParameterValues("deadline1");
			String[] idealTime = request.getParameterValues("idealTime");
			String[] colourCode = request.getParameterValues("colourCode");
	
			con = db.makeConnection(con);
	
			
			
	//		System.out.println("freqTaskName ===>>> " + freqTaskName);
			
			for (int i = 0; taskID!=null && i<taskID.length; i++) {
			
				String freqTaskName = null;
				pst = con.prepareStatement("select freq_start_date,freq_end_date from projectmntnc_frequency where pro_id=?");
				pst.setInt(1, uF.parseToInt(strProjects[i]));
				rs = pst.executeQuery();
				while (rs.next()) {
					freqTaskName = uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT_STR)+" to "+uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT_STR);
				}
				rs.close();
				pst.close();
				
				if(startDate[i] != null && !startDate[i].equals("") && !startDate[i].equals("-") && deadline1[i] !=null && !deadline1[i].equals("") && !deadline1[i].equals("-")) {
					freqTaskName = uF.getDateFormat(startDate[i], DATE_FORMAT, DATE_FORMAT_STR)+" to "+uF.getDateFormat(deadline1[i], DATE_FORMAT, DATE_FORMAT_STR);
				}
	//			System.out.println("taskID ===>>> "+ i +" --- "+ taskID[i]);
				
				if(uF.parseToInt(taskID[i]) > 0) {
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("update activity_info set activity_name=?, priority=?, resource_ids=?, deadline=?, idealtime=?, start_date=?," +
						" dependency_task=?, dependency_type=?, color_code=?, taskstatus=?, task_skill_id=?, task_description=?,added_by=?," +
						"task_freq_name=?,recurring_task=?, task_from_my_self=?, pro_id=?, requested_by=?");
					if(uF.parseToInt(strProjects[i]) == 0) {
						sbQuery.append(",task_accept_status=1");
					}
					sbQuery.append(" where task_id =? ");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setString(1, taskname[i]);
					pst.setString(2, priority[i]);
					pst.setString(3, ","+emp_id+",");
					pst.setDate(4, uF.getDateFormat(deadline1[i], DATE_FORMAT));
					pst.setDouble(5, uF.parseToDouble(idealTime[i]));
					pst.setDate(6, uF.getDateFormat(startDate[i], DATE_FORMAT));
					pst.setInt(7, uF.parseToInt(dependency[i]));
					pst.setString(8, dependencyType[i]);
					pst.setString(9, colourCode[i]);
					if(uF.parseToInt(getParentTaskId()) > 0) {
						pst.setString(10, "New Sub Task");
					} else {
						pst.setString(10, "New Task");
					}
	//				pst.setInt(11, uF.parseToInt(empSkills[i]));
					pst.setInt(11, 0);
					pst.setString(12, taskDescription[i]);
					pst.setInt(13, emp_id);
					pst.setString(14, freqTaskName);
					pst.setInt(15, uF.parseToInt(isRecurringTask[i]));
					pst.setBoolean(16, true);
					pst.setInt(17, uF.parseToInt(strProjects[i]));
					pst.setInt(18, emp_id);
					pst.setInt(19, uF.parseToInt(taskID[i]));
					pst.executeUpdate();
					pst.close();
				} else {
					pst = con.prepareStatement("insert into activity_info (activity_name,priority,resource_ids,deadline,idealtime,start_date," +
						"dependency_task,dependency_type,color_code,taskstatus,pro_id,task_skill_id,task_description,added_by,task_freq_name," +
						"recurring_task,parent_task_id,task_from_my_self,requested_by,task_accept_status)" +
						" values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?) ");
					pst.setString(1, taskname[i]);
					pst.setString(2, priority[i]);
	//				pst.setInt(3, uF.parseToInt(emp_id[i]));
					pst.setString(3, ","+emp_id+",");
					pst.setDate(4, uF.getDateFormat(deadline1[i], DATE_FORMAT));
					pst.setDouble(5, uF.parseToDouble(idealTime[i]));
					pst.setDate(6, uF.getDateFormat(startDate[i], DATE_FORMAT));
					pst.setInt(7, uF.parseToInt(dependency[i]));
					pst.setString(8, dependencyType[i]);
					pst.setString(9, colourCode[i]); 
					if(uF.parseToInt(getParentTaskId()) > 0) {
						pst.setString(10, "New Sub Task");
					} else {
						pst.setString(10, "New Task");
					}
					pst.setInt(11, uF.parseToInt(strProjects[i]));
	//				pst.setInt(12, uF.parseToInt(empSkills[i]));
					pst.setInt(12, 0);
					pst.setString(13, taskDescription[i]);
					pst.setInt(14, emp_id);
					pst.setString(15, freqTaskName);
					pst.setInt(16, uF.parseToInt(isRecurringTask[i]));
					pst.setInt(17, uF.parseToInt(getParentTaskId()));
					pst.setBoolean(18, true);
					pst.setInt(19, emp_id);
					if(uF.parseToInt(strProjects[i]) == 0) {
						pst.setInt(20, 1);
					} else {
						pst.setInt(20, 0);
					}
					pst.executeUpdate();
					pst.close();
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
				
				double dblAllCompleted = 0.0d;
				int subTaskCnt = 0;
				pst = con.prepareStatement("select sum(completed) as completed, count(task_id) as count from activity_info where parent_task_id = ? and task_accept_status = 1");
				pst.setInt(1, uF.parseToInt(strTaskId));
	//			pst.setInt(2, uF.parseToInt(taskId));
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
				
				pst = con.prepareStatement("select sum(completed)/count(task_id) as avrg from activity_info where pro_id=? and parent_task_id = 0 and task_accept_status = 1");
				pst.setInt(1, uF.parseToInt(strProjects[i]));
				rs = pst.executeQuery();
				String projectCompletePercent = null;
				while (rs.next()) {
					projectCompletePercent = uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rs.getString("avrg")));
				}
				rs.close();
				pst.close();
		
				pst = con.prepareStatement("update projectmntnc set completed=? where pro_id=? ");
				pst.setDouble(1, uF.parseToDouble(projectCompletePercent));
				pst.setInt(2, uF.parseToInt(strProjects[i]));
				pst.execute();
				pst.close();
				
				/**
				 * Alerts
				 * */
				List<String> alEmp = new ArrayList<String>();
				pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=? and _isteamlead = true");
				pst.setInt(1, uF.parseToInt(strProjects[i]));
				rs = pst.executeQuery();
				while(rs.next()){
					if(uF.parseToInt(rs.getString("emp_id")) > 0 && !alEmp.contains(rs.getString("emp_id"))) {
						alEmp.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
				
				String strDomain = request.getServerName().split("\\.")[0];
				
				String taskName = CF.getProjectTaskNameByTaskId(con, uF, strTaskId);
				String proName = CF.getProjectNameById(con, strProjects[i]);
				
				String alertData = "<div style=\"float: left;\"> <b>"+taskName+"</b> in <b>"+proName+"</b> project new task has been sent for acceptance by <b> "+CF.getEmpNameMapByEmpId(con, emp_id+"")+"</b>.</div>";
				String alertAction = "ViewMyProjects.action";
				StringBuilder taggedWith = null;
				for(String strEmp : alEmp) {
					if(taggedWith == null) {
						taggedWith = new StringBuilder();
						taggedWith.append(","+strEmp.trim()+",");
					} else {
						taggedWith.append(strEmp.trim()+",");
					}
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmp.trim());
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(TASK_NEW_REQUEST_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
				
				if(taggedWith == null) {
					taggedWith = new StringBuilder();
				}
				
				String activityData = "<div style=\"float: left;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> T </span> <b>"+taskName+"</b> in <b>"+proName+"</b> project has been sent for acceptance by <b> "+CF.getEmpNameMapByEmpId(con, emp_id+"")+"</b>.</div>";
				UserActivities userAct = new UserActivities(con, uF, CF, request);
				userAct.setStrDomain(strDomain);
				userAct.setStrAlignWith(TASK+"");
				userAct.setStrAlignWithId(strTaskId);
				userAct.setStrTaggedWith(taggedWith.toString());
				userAct.setStrVisibilityWith(taggedWith.toString());
				userAct.setStrVisibility("2");
				userAct.setStrData(activityData);
				userAct.setStrSessionEmpId(emp_id+"");
				userAct.setStatus(INSERT_TR_ACTIVITY);
				Thread tt = new Thread(userAct);
				tt.run();
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public boolean checkTaskStatus() {
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag=false;
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select count(task_id) as count from task_activity where emp_id =? and end_time is null");
			pst.setInt(1, emp_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				rs.getInt("count");
				if(rs.getInt("count")>0) {
					flag=true;	
				}
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}
	
	
	public void getProjectDetails() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbMyProjectList = new StringBuilder();
			for(int a=0; projectdetailslist!=null && a<projectdetailslist.size(); a++) {
				sbMyProjectList.append("<option value='"+projectdetailslist.get(a).getProjectID()+"'>"+projectdetailslist.get(a).getProjectName()+"</option>");
				
			}
			request.setAttribute("sbMyProjectList", sbMyProjectList.toString());
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			StringBuilder sbque11 = new StringBuilder(); //count(*) as taskCount 
			sbque11.append("select * from activity_info ai where ai.resource_ids like '%,"+emp_id+",%' and ai.task_id not in (select " +
				"parent_task_id from activity_info where resource_ids like '%,"+emp_id+",%' and parent_task_id is not null and task_accept_status != -1) ");
			if(getPro_id()!=0) {
				sbque11.append(" and ai.pro_id="+getPro_id()+" ");
			}
			
			if(getTaskSubtaskStatus() != null && uF.parseToInt(getTaskSubtaskStatus()) == 1) {
				sbque11.append(" and ai.completed>0 and ai.deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getTaskSubtaskStatus() != null && uF.parseToInt(getTaskSubtaskStatus()) == 2) {
				sbque11.append(" and (ai.completed = 0 or ai.completed is null) and ai.deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getTaskSubtaskStatus() != null && uF.parseToInt(getTaskSubtaskStatus()) == 3) {
				sbque11.append(" and ai.deadline < '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			}
			
			if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 1) {
				sbque11.append(" and ai.pro_id in (select pro_id from projectmntnc where billing_type != 'F') ");
			} else if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 2) {
				sbque11.append(" and ai.pro_id in (select pro_id from projectmntnc where billing_type = 'F') ");
			}
			
			if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) == -1) {
				sbque11.append(" and ai.added_by = "+emp_id+" and ai.task_from_my_self = true ");
			} else if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) >0) {
				sbque11.append(" and ai.added_by = "+uF.parseToInt(getAssignedBy())+" and ai.task_from_my_self = false ");
			}
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sbque11.append(" and (upper(activity_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or pro_id in (select pro_id from projectmntnc where upper(pro_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%') " +
						"or pro_id in (select pro_id from projectmntnc where client_id in (select client_id from client_details where upper(client_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%')))");
			}
//			sbque.append("order by ai.deadline desc, priority desc, ai.task_id desc limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbque11.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			int wTaskCnt = 0;
			int wSubTaskCnt = 0;
			int trTaskCnt = 0;
			int trSubTaskCnt = 0;
			int mrTaskCnt = 0;
			int mrSubTaskCnt = 0;
			int cTaskCnt = 0;
			int cSubTaskCnt = 0;
			while(rs.next()) {
				if(rs.getString("approve_status").equals("n") && rs.getInt("task_accept_status") == 1) {
					if(rs.getInt("parent_task_id") == 0) {
						wTaskCnt++;
					} else {
						wSubTaskCnt++;
					}
				} else if(rs.getString("approve_status").equals("approved")) {
					if(rs.getInt("parent_task_id") == 0) {
						cTaskCnt++;
					} else {
						cSubTaskCnt++;
					}
				} else if(rs.getString("approve_status").equals("n") && rs.getInt("task_accept_status") == 0 && !uF.parseToBoolean(rs.getString("task_from_my_self"))) {
					if(rs.getInt("parent_task_id") == 0) {
						trTaskCnt++;
					} else {
						trSubTaskCnt++;
					}
				} else if(rs.getString("approve_status").equals("n") && (((rs.getInt("task_accept_status") == 0 || rs.getInt("task_accept_status") == -1) 
						&& uF.parseToBoolean(rs.getString("task_from_my_self"))) || (rs.getInt("task_accept_status") > 1 
						&& !uF.parseToBoolean(rs.getString("task_from_my_self")))) ) {
					if(rs.getInt("parent_task_id") == 0) {
						mrTaskCnt++;
					} else {
						mrSubTaskCnt++;
					}
				}
			}
			rs.close();
			pst.close();
			
			int wTSTCnt = wTaskCnt + wSubTaskCnt;
			int trTSTCnt = trTaskCnt + trSubTaskCnt;
			int mrTSTCnt = mrTaskCnt + mrSubTaskCnt;
			int cTSTCnt = cTaskCnt + cSubTaskCnt;
			
			request.setAttribute("wTSTCnt", wTSTCnt+"");
			request.setAttribute("trTSTCnt", trTSTCnt+"");
			request.setAttribute("mrTSTCnt", mrTSTCnt+"");
			request.setAttribute("cTSTCnt", cTSTCnt+"");
			
			int taskCount = 0;
			StringBuilder sbque1 = new StringBuilder(); //count(*) as taskCount 
			sbque1.append("select task_id,parent_task_id from activity_info ai where ai.resource_ids like '%,"+emp_id+",%' and ai.task_id not in (select " +
				"parent_task_id from activity_info where resource_ids like '%,"+emp_id+",%' and parent_task_id is not null and task_accept_status != -1) ");
			if(getPro_id()!=0) {
				sbque1.append(" and ai.pro_id="+getPro_id()+" ");
			}
			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
				sbque1.append(" and approve_status='n' and task_accept_status = 1 ");
			} else if(getProType() == null || getProType().equals("TR")) {
				sbque1.append(" and approve_status='n' and task_accept_status = 0 and task_from_my_self=false ");
			} else if(getProType() == null || getProType().equals("MR")) {
				sbque1.append(" and approve_status='n' and (((task_accept_status=0 or task_accept_status = -1) and task_from_my_self=true) or (task_accept_status>1 and task_from_my_self=false)) ");
			} else if(getProType() != null && getProType().equals("C")) {
				sbque1.append(" and approve_status='approved' ");
			}
			
			if(getTaskSubtaskStatus() != null && uF.parseToInt(getTaskSubtaskStatus()) == 1) {
				sbque1.append(" and ai.completed>0 and ai.deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getTaskSubtaskStatus() != null && uF.parseToInt(getTaskSubtaskStatus()) == 2) {
				sbque1.append(" and (ai.completed = 0 or ai.completed is null) and ai.deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getTaskSubtaskStatus() != null && uF.parseToInt(getTaskSubtaskStatus()) == 3) {
				sbque1.append(" and ai.deadline < '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			}
			
			if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 1) {
				sbque1.append(" and ai.pro_id in (select pro_id from projectmntnc where billing_type != 'F') ");
			} else if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 2) {
				sbque1.append(" and ai.pro_id in (select pro_id from projectmntnc where billing_type = 'F') ");
			}
			
			if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) == -1) {
				sbque1.append(" and ai.added_by = "+emp_id+" and ai.task_from_my_self = true ");
			} else if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) >0) {
				sbque1.append(" and ai.added_by = "+uF.parseToInt(getAssignedBy())+" and ai.task_from_my_self = false ");
			}
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sbque1.append(" and (upper(activity_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or pro_id in (select pro_id from projectmntnc where upper(pro_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%') " +
						"or pro_id in (select pro_id from projectmntnc where client_id in (select client_id from client_details where upper(client_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%')))");
			}
//			sbque.append("order by ai.deadline desc, priority desc, ai.task_id desc limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbque1.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			int tstCount = 0;
			int tskCnt = 0;
			int subtskCnt = 0;
			while(rs.next()) {
				tstCount++;
				if(rs.getInt("parent_task_id") == 0) {
					tskCnt++;
				} else {
					subtskCnt++;
				}
			}
			rs.close();
			pst.close();
			taskCount = tstCount/10;
			if(tstCount%10 != 0) {
				taskCount++;
			}
			request.setAttribute("tskCnt", tskCnt+"");
			request.setAttribute("subtskCnt", subtskCnt+"");
			request.setAttribute("taskCount", taskCount+"");
			
			StringBuilder sbque = new StringBuilder();
			sbque.append("select * from activity_info ai where ai.resource_ids like '%,"+emp_id+",%' and ai.task_id not in (select parent_task_id " +
				" from activity_info where resource_ids like '%,"+emp_id+",%' and parent_task_id is not null and task_accept_status != -1) ");
			if(getPro_id()!=0) {
				sbque.append(" and ai.pro_id="+getPro_id()+" ");
			}
			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
				sbque.append(" and approve_status='n' and task_accept_status = 1 ");
			} else if(getProType() == null || getProType().equals("TR")) {
				sbque.append(" and approve_status='n' and task_accept_status = 0 and task_from_my_self=false ");
			} else if(getProType() == null || getProType().equals("MR")) {
				sbque.append(" and approve_status='n' and (((task_accept_status=0 or task_accept_status = -1) and task_from_my_self=true) or (task_accept_status>1 and task_from_my_self=false)) ");
			} else if(getProType() != null && getProType().equals("C")) {
				sbque.append(" and approve_status='approved' ");
			}
			if(getTaskSubtaskStatus() != null && uF.parseToInt(getTaskSubtaskStatus()) == 1) {
				sbque.append(" and ai.completed>0 and ai.deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getTaskSubtaskStatus() != null && uF.parseToInt(getTaskSubtaskStatus()) == 2) {
				sbque.append(" and (ai.completed = 0 or ai.completed is null) and ai.deadline >= '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			} else if(getTaskSubtaskStatus() != null && uF.parseToInt(getTaskSubtaskStatus()) == 3) {
				sbque.append(" and ai.deadline < '"+uF.getCurrentDate(CF.getStrTimeZone())+"' ");
			}
			
			if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 1) {
				sbque.append(" and ai.pro_id in (select pro_id from projectmntnc where billing_type != 'F') ");
			} else if(getRecurrOrMiles() != null && uF.parseToInt(getRecurrOrMiles()) == 2) {
				sbque.append(" and ai.pro_id in (select pro_id from projectmntnc where billing_type = 'F') ");
			}
			
			if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) == -1) {
				sbque.append(" and ai.added_by = "+emp_id+" and ai.task_from_my_self = true ");
			} else if(getAssignedBy() != null && uF.parseToInt(getAssignedBy()) >0) {
				sbque.append(" and ai.added_by = "+uF.parseToInt(getAssignedBy())+" and ai.task_from_my_self = false ");
			}
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sbque.append(" and (upper(activity_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' or pro_id in (select pro_id from projectmntnc where upper(pro_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%') " +
						"or pro_id in (select pro_id from projectmntnc where client_id in (select client_id from client_details where upper(client_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%')))");
			}
			
			if(getSortBy() != null && uF.parseToInt(getSortBy()) == 1) {
				sbque.append(" order by ai.start_date desc ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 2) {
				sbque.append(" order by ai.start_date ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 3) {
				sbque.append(" order by ai.activity_name ");
			} else if(getSortBy() != null && uF.parseToInt(getSortBy()) == 4) {
				sbque.append(" order by ai.activity_name desc ");
			} else {
				sbque.append(" order by ai.start_date desc ");
//				sbque.append(" order by ai.deadline desc, priority desc, ai.task_id desc ");
			}
			int intOffset = uF.parseToInt(minLimit);
			sbque.append(" limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbque.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			
			List<List<String>> proTaskList = new ArrayList<List<String>>();
			
			while(rs.next()) {
				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
				
				if(hmProjectData==null || hmProjectData.isEmpty()){
					
					hmProjectData.put("PRO_BILLING_ACTUAL_TYPE", "H");
					hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
					hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
				}
				
				List<String> alInner = new ArrayList<String>();	
				
				String taskid = rs.getString("task_id");
				alInner.add(taskid);  //0
				
				alInner.add(rs.getString("activity_name")); //1
				alInner.add(uF.showData(rs.getString("taskstatus"), "")); //2
				if(uF.parseToInt(rs.getString("pro_id"))>0) {
					if(proType != null && proType.equals("MR")) {
						alInner.add("aligned with: "+uF.showData(hmProjectData.get("PRO_NAME"), "-")); //3
					} else {
						alInner.add(uF.showData(hmProjectData.get("PRO_NAME"), "-")); //3
					}
				} else {
					alInner.add("Not aligned"); //3
				}
				if(rs.getString("added_by") != null && rs.getString("added_by").equals(emp_id+"")) {
					alInner.add("Myself"); //4
				} else {
					alInner.add(uF.showData(hmEmpName.get(rs.getString("added_by")), "-")); //4
				}
				alInner.add(uF.showData(rs.getString("pro_id"), "0")); //5
				alInner.add(uF.showData(CF.getProjectTaskNameByTaskId(con, uF, rs.getString("parent_task_id")), "")); //6 
				alInner.add(rs.getString("parent_task_id")); //7
				alInner.add(uF.showData(rs.getString("task_description"), "")); //8

				double deadLinePercent = 0;
				String currdays = null;
				String days = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("deadline"), DBDATE);
				if(rs.getString("approve_status") != null && rs.getString("approve_status").equalsIgnoreCase("approved")) {
					if(rs.getString("end_date") != null) {
						currdays = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("end_date"), DBDATE);
					}
				} else {
					currdays = uF.dateDifference(rs.getString("start_date"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
				}
				if(uF.parseToDouble(days) > 0) {
					deadLinePercent = (uF.parseToDouble(currdays) / uF.parseToDouble(days)) * 100;
				}
				String proDeadlinePercentColor = "";
				if(deadLinePercent <= 75) {
					proDeadlinePercentColor = "green";
				} else if(deadLinePercent > 75 && deadLinePercent < 100) {
					proDeadlinePercentColor = "yellow";
				} else if(deadLinePercent >= 100) {
					proDeadlinePercentColor = "red";
				} else {
					proDeadlinePercentColor = "";
				}
				alInner.add(proDeadlinePercentColor); //9
				
				if(uF.parseToInt(rs.getString("priority"))==0) {
					alInner.add("<i class=\"fa fa-exclamation\" aria-hidden=\"true\" title=\"Low\" style=\"color:#afafaf\"></i>"); //10
				} else if(uF.parseToInt(rs.getString("priority"))==1) {
					alInner.add("<i class=\"fa fa-exclamation\" aria-hidden=\"true\" title=\"Medium\" style=\"color:#ffcc00\"></i>"); //10
				} else if(uF.parseToInt(rs.getString("priority"))==2) {
					alInner.add("<i class=\"fa fa-exclamation\" aria-hidden=\"true\" title=\"High\" style=\"color:#ff0000\"></i>"); //10
				}
				alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT_STR)); //11
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT_STR)); //12
				alInner.add(uF.showData(rs.getString("idealtime"), "0")); //13
				alInner.add(uF.showData(rs.getString("completed"), "0")); //14
				alInner.add(uF.showData(rs.getString("approve_status"), "")); //15
				Map<String, String> hmTaskAWDaysAndHrs = CF.getProjectTaskActualWorkedDaysAndHrs(con, rs.getString("task_id"), hmProjectData);
				double dblAlreadyWorked = 0;
				if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE")!=null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("D")) {
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_DAYS"));
					alInner.add(uF.formatIntoTwoDecimal(dblAlreadyWorked)+" days"); //16
					alInner.add(" days"); //17
				} else if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE")!=null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("M")) {
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_DAYS"));
					double alreadyWorkedMnths = dblAlreadyWorked / 30;
					if(alreadyWorkedMnths < 0) 
						alreadyWorkedMnths = 0;
					alInner.add(uF.formatIntoTwoDecimal(alreadyWorkedMnths)+" months"); //16
					alInner.add(" months"); //17
				} else {
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_HRS"));
					alInner.add(uF.getTotalTimeMinutes100To60(""+dblAlreadyWorked)+" hrs"); //16
					alInner.add(" hrs"); //17
				}
				
				proTaskList.add(alInner);
				
			}
			rs.close();
			pst.close();
			
//			System.out.println("proTaskList ===>> " + proTaskList);
			request.setAttribute("proTaskList", proTaskList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public int getPro_id() {
		return pro_id;
	}

	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}

	public String[] getIsFinish() {
		return isFinish;
	}

	public void setIsFinish(String[] isFinish) {
		this.isFinish = isFinish;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(int emp_id) {
		this.emp_id = emp_id;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public String getAddTask() {
		return addTask;
	}

	public void setAddTask(String addTask) {
		this.addTask = addTask;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getParentTaskId() {
		return parentTaskId;
	}

	public void setParentTaskId(String parentTaskId) {
		this.parentTaskId = parentTaskId;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getAddSubTask() {
		return addSubTask;
	}

	public void setAddSubTask(String addSubTask) {
		this.addSubTask = addSubTask;
	}

	public String getTaskAcceptStatus() {
		return taskAcceptStatus;
	}

	public void setTaskAcceptStatus(String taskAcceptStatus) {
		this.taskAcceptStatus = taskAcceptStatus;
	}

	public String getrStartDate() {
		return rStartDate;
	}

	public void setrStartDate(String rStartDate) {
		this.rStartDate = rStartDate;
	}

	public String getrDeadline() {
		return rDeadline;
	}

	public void setrDeadline(String rDeadline) {
		this.rDeadline = rDeadline;
	}

	public String getTaskrescheduleComment() {
		return taskrescheduleComment;
	}

	public void setTaskrescheduleComment(String taskrescheduleComment) {
		this.taskrescheduleComment = taskrescheduleComment;
	}

	public String getTaskreassignComment() {
		return taskreassignComment;
	}

	public void setTaskreassignComment(String taskreassignComment) {
		this.taskreassignComment = taskreassignComment;
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

	public String getLoadMore() {
		return loadMore;
	}

	public void setLoadMore(String loadMore) {
		this.loadMore = loadMore;
	}

	public String getTaskSubtaskStatus() {
		return taskSubtaskStatus;
	}

	public void setTaskSubtaskStatus(String taskSubtaskStatus) {
		this.taskSubtaskStatus = taskSubtaskStatus;
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

	public String getSortBy1() {
		return sortBy1;
	}

	public void setSortBy1(String sortBy1) {
		this.sortBy1 = sortBy1;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String getCompleteTask() {
		return completeTask;
	}

	public void setCompleteTask(String completeTask) {
		this.completeTask = completeTask;
	}
	public String getStrSearchJob() {
		return strSearchJob;
	}
	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public List<FillProjectList> getProjectdetailslist() {
		return projectdetailslist;
	}

	public void setProjectdetailslist(List<FillProjectList> projectdetailslist) {
		this.projectdetailslist = projectdetailslist;
	}
}