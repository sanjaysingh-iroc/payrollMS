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

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserActivities;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class EmpViewProject extends ActionSupport implements ServletRequestAware, ServletResponseAware, SessionAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	HttpServletResponse response;
	int id; 
	int emp_id;
	String strEmpOrgId;
	Map session;
	String strUserType; 
	
	int pro_id;
	int task_id;
	String[] isFinish;
	boolean isSingle = false;
	CommonFunctions CF;
	
	List<Integer> projectidlist = new ArrayList<Integer>();
	List<String> projectlist = new ArrayList<String>();
	List<Integer> activity_index = new ArrayList<Integer>();
	List<Integer> index = new ArrayList<Integer>();
	Map<Integer, List<String>> al = new HashMap<Integer, List<String>>();
	List<String> time1 = new ArrayList<String>();
	List<String> taskStatus1 = new ArrayList<String>();
	List<String> totalhrz1 = new ArrayList<String>();
	List<FillProjectList> projectdetailslist;
	String projectID;
	String projectName;
	double actual_days;
	double ideal_days;
	boolean flag;
	String proType; 
	
	List<GetPriorityList> priorityList;
	List<FillDependentTaskList> dependencyList;
	List<GetDependancyTypeList> dependancyTypeList;
	List<FillTaskEmpList> TaskEmpNamesList;
	List<FillSkills> empSkillList;
	
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
	
	String completeTask;
	
	public String execute() { 
			
		UtilityFunctions uF = new UtilityFunctions();
		
		session = ActionContext.getContext().getSession();
		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return LOGIN;
		request.setAttribute(PAGE, "/jsp/task/EmpViewProject.jsp");
		request.setAttribute(TITLE, "My Tasks");
		emp_id = uF.parseToInt((String) session.get(EMPID));
		strEmpOrgId = (String) session.get(ORGID);
		strUserType = (String)session.get(BASEUSERTYPE);
//		if ((Integer) session.get("pro_id") != null) {
//			pro_id = (Integer) session.get("pro_id"); 
//		}
		
//		if(getAlertStatus()!=null && getAlert_type()!=null){
//		if(uF.parseToInt(getAlertID()) > 0) {
//			updateUserAlerts();
//		}
		
		//System.out.println("EVP");
		
		if(getCompleteTask() != null && getCompleteTask().equals("C")) {
			completeThisTask(uF);
			return VIEW;
		}
		
		if(getAddTask() != null && getAddTask().equals("Add")) {
			insertActivityDetails(uF);
			return VIEW;
		}
		
		if(getAddSubTask() != null && getAddSubTask().equals("Add")) {
			insertSubTaskDetails(uF);
			return VIEW;
		}
		
		if(getAddTask() != null && getAddTask().equals("Update")) {
			updateActivityDetails(uF);
			return VIEW;
		}
		
		if(getAddTask() != null && getAddTask().equals("Delete")) {
			deleteTask(uF);
			return VIEW;
		}
		
		if(getAddTask() != null && getAddTask().equals("Reassign")) {
			sendRequestForTaskReassign(uF);
			return VIEW;
		}
		
		if(getAddTask() != null && getAddTask().equals("Reschedule")) {
			sendRequestForTaskReschedule(uF);
			return VIEW;
		}
		
		if(getTaskAcceptStatus() != null && getTaskAcceptStatus().equals("1")) {
			acceptTaskOrReassignTaskRequest(uF);
			return VIEW;
		}
		
		if(getProType() == null || getProType().equalsIgnoreCase("null") || getProType().equals("") || getProType().equals("L")) {
			projectdetailslist = new FillProjectList(request).fillProjectDetailsByEmp(emp_id, false, 0);
			flag = checkTaskStatus();
//			getProjectDetails();
		} else if(getProType() != null && getProType().equals("TR")) {
			projectdetailslist = new FillProjectList(request).fillProjectDetailsByEmp(emp_id, false, 0);
			flag = checkTaskStatus();
//			getProjectDetails();
		} else if(getProType() != null && getProType().equals("MR")) {
			projectdetailslist = new FillProjectList(request).fillProjectDetailsByEmp(emp_id, false, 0);
			flag = checkTaskStatus();
//			getProjectDetails();
		} else if(getProType() == null || getProType().equals("C")) {
			projectdetailslist = new FillProjectList(request).fillProjectDetailsByEmp(emp_id, true, 0);
//			getPastProjectDetails();
		} 
		
		getProjectDetails();
		return SUCCESS;
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


	private void sendRequestForTaskReschedule(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update activity_info set task_accept_status=?, task_reassign_reschedule_comment=?, r_start_date=?, " +
				"r_deadline=?, requested_by=? ,reschedule_reassign_request_status=0 where task_id = ?");
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
				
				alertAction = "MyWork.action";
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
			pst = con.prepareStatement("update activity_info set task_accept_status = ?, task_reassign_reschedule_comment=?, requested_by=?,reschedule_reassign_request_status=0,r_start_date=?,r_deadline=? where task_id = ?");
			pst.setInt(1, 3);
			pst.setString(2, getTaskreassignComment());
			pst.setInt(3, emp_id);
			pst.setDate(4, null);
			pst.setDate(5, null);
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

	
	
	/*public void insertActivityDetails(UtilityFunctions uF) {
		
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
				} else {
					pst = con.prepareStatement("insert into activity_info (activity_name,priority,resource_ids,deadline,idealtime,start_date," +
						"dependency_task,dependency_type,color_code,taskstatus,pro_id,task_skill_id,task_description,added_by,task_freq_name," +
						"recurring_task,parent_task_id,task_from_my_self)" +
						" values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?) ");
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
					pst.setInt(11, uF.parseToInt(getProId()));
//					pst.setInt(12, uF.parseToInt(empSkills[i]));
					pst.setInt(12, 0);
					pst.setString(13, taskDescription[i]);
					pst.setInt(14, emp_id);
					pst.setString(15, freqTaskName);
					pst.setInt(16, uF.parseToInt(isRecurringTask[i]));
					pst.setInt(17, uF.parseToInt(getParentTaskId()));
					pst.setBoolean(18, true);
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
				pst = con.prepareStatement("select sum(completed) as completed, count(task_id) as count from activity_info where parent_task_id = ?");
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

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}*/


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
			
				String taskRequestAutoApproved = null;
				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, strProjects[i]);
				if(hmProjectData != null) {
					taskRequestAutoApproved = CF.getProjectTaskRequestAutoApproved(con, hmProjectData.get("PRO_ORG_ID"));
				}
//				System.out.println("taskRequestAutoApproved ===>> " + taskRequestAutoApproved +" -- strProjects[i] ===>> " + strProjects[i]);
				
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
					if(uF.parseToInt(strProjects[i]) == 0 || (uF.parseToBoolean(taskRequestAutoApproved) && uF.parseToInt(strProjects[i])>0)) {
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
					if(uF.parseToInt(strProjects[i]) == 0 || (uF.parseToBoolean(taskRequestAutoApproved) && uF.parseToInt(strProjects[i])>0)) {
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
			StringBuilder hiddenMyProjectIdName = new StringBuilder();
			for(int a=0; projectdetailslist!=null && a<projectdetailslist.size(); a++) {
				sbMyProjectList.append("<option value='"+projectdetailslist.get(a).getProjectID()+"'>"+projectdetailslist.get(a).getProjectName()+"</option>");
				hiddenMyProjectIdName.append("<input type=hidden name="+projectdetailslist.get(a).getProjectID()+" id="+projectdetailslist.get(a).getProjectID()+" value='"+projectdetailslist.get(a).getProjectName()+"'/>");
			}
			request.setAttribute("sbMyProjectList", sbMyProjectList.toString());
			request.setAttribute("hiddenMyProjectIdName", hiddenMyProjectIdName.toString());
			
//			System.out.println("sbMyProjectList ===>> " + sbMyProjectList.toString());
			
			getMyAssignedTasks(con, uF);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmTaskActRunId = checkTaskActivityRunningId(con, uF);
			StringBuilder sbque = new StringBuilder();
			sbque.append("select * from activity_info ai where ai.resource_ids like '%,"+emp_id+",%' and ai.task_id=?");
			pst = con.prepareStatement(sbque.toString());
			pst.setInt(1, uF.parseToInt(getTaskId()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			
			GetPriorityList objGP = new GetPriorityList();
			List<List<String>> proTaskList = new ArrayList<List<String>>();
			
			Map<String, String> hmProTaskDependency = new HashMap<String, String>();
			while(rs.next()) {
				dependencyList = new FillDependentTaskList(request).fillDependentTaskList(uF.parseToInt(rs.getString("pro_id")));
				dependancyTypeList = new GetDependancyTypeList().fillDependancyTypeList();
				priorityList = new GetPriorityList().fillPriorityList();
				if(rs.getInt("parent_task_id") == 0) {
					StringBuilder sbTaskDependencyList = new StringBuilder();
					for(int i=0; dependencyList!=null && i<dependencyList.size(); i++) {
						sbTaskDependencyList.append("<option value='"+dependencyList.get(i).getDependencyId()+"'>"+dependencyList.get(i).getDependencyName()+"</option>");
					}
					hmProTaskDependency.put(rs.getString("task_id"), sbTaskDependencyList.toString());
				} else if(rs.getInt("parent_task_id") > 0) {
//					Map<String, String> hmProSubTaskDependency = new HashMap<String, String>();
					List<FillDependentTaskList> subDependencyList = new FillDependentTaskList(request).fillDependentSubTaskList(uF.parseToInt(rs.getString("pro_id")), uF.parseToInt(rs.getString("parent_task_id")));
					String subDependencyTask = getDependencyTaskOptions("0", "0", subDependencyList);
					hmProTaskDependency.put(rs.getString("task_id"), subDependencyTask);
				}
				String strColour = null;
				String strdaysColour = null;
				String strworkingColour = null;
				double no_of_hrs=0.0;
				double no_of_days=0.0;
				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
				
				if(hmProjectData==null || hmProjectData.isEmpty()){
					
					hmProjectData.put("PRO_BILLING_ACTUAL_TYPE", "H");
					hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
					hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
				}
				
				List<String> alInner = new ArrayList<String>();	
				
				String taskid = rs.getString("task_id");
				alInner.add(taskid);  //0
				
				double dblCompleted = 0;
				double dblIdealTime = 0;
				double dblAlreadyWorked = 0;
				
				String strTimeType = "";
				if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("D")) {
					strTimeType = "d";
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
//					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work_days"));
					String strAlreadyWorked = CF.getProjectTaskTotalDaysOrHoursEmpwise(con, taskid, emp_id+"", hmProjectData);
					dblAlreadyWorked = uF.parseToDouble(strAlreadyWorked);
				} else if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("M")) {
					strTimeType = "m";
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
//					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work_days"));
					Map<String, String> hmAlreadyWorked = CF.getProjectTaskTotalMonthsEmpwise(con,request, CF, taskid, emp_id+"", hmProjectData);
					dblAlreadyWorked = uF.parseToDouble(hmAlreadyWorked.get("ACTUAL_WORKING"));
					
				} else if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("H")) {
					strTimeType = "h";
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
//					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work"));
					String strAlreadyWorked = CF.getProjectTaskTotalDaysOrHoursEmpwise(con, taskid, emp_id+"", hmProjectData);
					
					dblAlreadyWorked = uF.parseToDouble(strAlreadyWorked);
					
				} else {
					strTimeType = "h";
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
//					String strAlreadyWorked = CF.getProjectTaskTotalDaysOrHoursEmpwise(con, taskid, emp_id+"", hmProjectData);
//					dblAlreadyWorked = uF.parseToDouble(strAlreadyWorked);
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
				
				double taskDealineCompletePercent = getTaskDeadlineCompletePercent(uF.parseToInt(taskid));
				String tdcpSpan = "";
				if(taskDealineCompletePercent <= 50) {
//					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/approved.png\"></span>";
					/*tdcpSpan = "<img title=\"\" src=\"images1/icons/approved.png\">"; */
					tdcpSpan = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>"; 
				} else if(taskDealineCompletePercent > 50 && taskDealineCompletePercent <= 75) {
//					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/re_submit.png\"></span>";
					/*tdcpSpan = "<img title=\"\" src=\"images1/icons/re_submit.png\">";*/
					tdcpSpan = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>";
					
				} else {
//					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/denied.png\"></span>";
					/*tdcpSpan = "<img title=\"\" src=\"images1/icons/denied.png\">";*/
					tdcpSpan = "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>";
				}
				
				if(dblCompleted >= 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
					no_of_days=getNoOfDays(uF.parseToInt(taskid));
				}
				if(dblCompleted >= 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
					no_of_hrs = dblAlreadyWorked - dblIdealTime;
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
				String daySpanLbl =" d";
				String timeSpan = "none";
				String timeSpanLbl = " h";
				String strTime = uF.getTotalTimeMinutes100To60(""+no_of_hrs);
				if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("D")) {
					daySpan="inline";
				} else {
					if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("M")) {
						timeSpanLbl = " m";
						strTime = uF.formatIntoTwoDecimal(no_of_hrs);
					}
					timeSpan="inline";
				}
				
				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
				
				
					if(rs.getString("taskstatus") != null && (rs.getString("taskstatus").equalsIgnoreCase("New Task") || rs.getString("taskstatus").equalsIgnoreCase("Sub New Task"))) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Confirmed\" ></i>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<img title=\"Completed\" src=\"images1/icons/act_now.png\">");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b> "+no_of_days+"</b>"+daySpanLbl+"</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("");
					}
					
				} else if(dtCurrentDate!=null && dtDeadlineDate!=null && dtCurrentDate.after(dtDeadlineDate)) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Confirmed\" ></i>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>days</span>)");
					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<img title=\"Completed\" src=\"images1/icons/act_now.png\">");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
					}
					
				} else if(rs.getInt("completed")>=100) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status") != null && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\" title=\"Confirmed\" ></i>");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<img title=\"Completed\" src=\"images1/icons/act_now.png\">");
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
					} else {
						alInner.add(tdcpSpan);
						alInner.add("(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
					}
				} else {
						if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<img title=\"\" src=\"images1/icons/act_now.png\">");
						alInner.add("");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan);
						alInner.add("");
					} else {
						alInner.add(tdcpSpan);//1
						alInner.add("");//2
					}
				}
				
				alInner.add(rs.getString("activity_name")); //3
				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), "")); //4
//				alInner.add(rs.getString("priority")); //4
//				boolean isTeamLead = CF.getProjectTLByEmpId(con, rs.getString("pro_id"), rs.getString("emp_id")); 
//				alInner.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")); //5
				alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat())); //5
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat())); //6
				
				if(hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && !hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("H")) {
					alInner.add(uF.formatIntoTwoDecimal(dblIdealTime)); //7
					
					alInner.add(uF.formatIntoTwoDecimal(dblAlreadyWorked)); //8
				} else {
					alInner.add(uF.getTotalTimeMinutes100To60(""+dblIdealTime)); //7
					alInner.add(uF.getTotalTimeMinutes100To60(""+dblAlreadyWorked)); //8
				}
				
				alInner.add(uF.showData(rs.getString("completed"), "0")); //9
				alInner.add(uF.showData(rs.getString("approve_status"), "")); //10
				alInner.add(uF.showData(rs.getString("taskstatus"), "")); //11
				alInner.add(dblAlreadyWorked+""); //12
				alInner.add(uF.showData(rs.getString("task_from_my_self"), "")); //13
				if(uF.parseToInt(rs.getString("pro_id"))>0) {
					if(proType != null && proType.equals("MR")) {
						alInner.add("aligned with: "+uF.showData(hmProjectData.get("PRO_NAME"), "-")); //14
					} else {
						alInner.add(uF.showData(hmProjectData.get("PRO_NAME"), "-")); //14
					}
				} else {
					alInner.add("Not aligned"); //14
				}
				if(rs.getString("added_by") != null && rs.getString("added_by").equals(emp_id+"")) {
					alInner.add("Myself"); //15
				} else {
					alInner.add(uF.showData(hmEmpName.get(rs.getString("added_by")), "-")); //15
				}
				String taskDocCnt = getTaskDocumentsCount(con, uF, rs.getString("pro_id"), rs.getString("task_id"));
				alInner.add(taskDocCnt); //16
				alInner.add(strTimeType); //17
				alInner.add(uF.showData(rs.getString("pro_id"), "0")); //18
//				boolean taskrunningFlag = checkTaskStatus();
				String taskActivityRunningId = hmTaskActRunId.get(rs.getString("task_id")); //checkTaskActivityRunningId(con, uF, rs.getString("task_id"));
//				alInner.add(taskrunningFlag+""); //
				alInner.add(taskActivityRunningId); //19
				alInner.add(uF.showData(CF.getProjectTaskNameByTaskId(con, uF, rs.getString("parent_task_id")), "")); //20 
				alInner.add(rs.getString("parent_task_id")); //21
				alInner.add(hmProjectData.get("PRO_START_DATE")); //22
				alInner.add(hmProjectData.get("PRO_END_DATE")); //23
				alInner.add(uF.showData(rs.getString("task_description"), "")); //24
				alInner.add(uF.showData(rs.getString("task_freq_name"), "-")); //25
				alInner.add(rs.getString("recurring_task")); //26
				if(uF.parseToInt(rs.getString("parent_task_id")) == 0) {
					dependencyList = new FillDependentTaskList(request).fillDependentTaskList(uF.parseToInt(rs.getString("pro_id")));
					String dependencyTask = getDependencyTaskOptions(rs.getString("task_id"), rs.getString("dependency_task"), dependencyList);
					alInner.add(uF.showData(dependencyTask, "")); //27
				} else {
					List<FillDependentTaskList> subDependencyList = new FillDependentTaskList(request).fillDependentSubTaskList(uF.parseToInt(rs.getString("pro_id")), uF.parseToInt(rs.getString("parent_task_id")));
					String subDependencyTask = getDependencyTaskOptions(rs.getString("task_id"), rs.getString("dependency_task"), subDependencyList);
					alInner.add(uF.showData(subDependencyTask, "")); //27
				}
				alInner.add(uF.showData(rs.getString("dependency_type"), "")); //28
				alInner.add(uF.showData(rs.getString("priority"), "")); //29
				
				TaskEmpNamesList = new FillTaskEmpList(request).fillEmployeeName(uF.parseToInt(rs.getString("pro_id")));
				String taskEmps = getTaskEmployee(rs.getString("resource_ids"), TaskEmpNamesList); 
				alInner.add(uF.showData(taskEmps, "")); //30
				alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT)); //31
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT)); //32
				alInner.add(rs.getString("idealtime")); //33
				alInner.add(rs.getString("color_code")); //34
				alInner.add(hmProjectData.get("PRO_BILL_FREQUENCY")); //35
				String freqEndDays = getProFreqEndDayCount(con, uF, rs.getString("pro_id"));
				alInner.add(uF.showData(freqEndDays, "")); //36
				alInner.add(rs.getString("task_accept_status")); //37
				alInner.add(uF.showData(rs.getString("task_reassign_reschedule_comment"), "-")); //38
				
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
				alInner.add(proDeadlinePercentColor); //39
				
//				String[] arr = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, strEmpOrgId);
				proTaskList.add(alInner);
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("proTaskList", proTaskList);
			request.setAttribute("hmProTaskDependency", hmProTaskDependency);
			
//			pst = con.prepareStatement("select * from task_activity where activity_id in (select task_id from activity_info where resource_ids like '%,"+emp_id+",%') order by activity_id, task_date desc");
//			pst = con.prepareStatement("select * from task_activity where emp_id = ? order by activity_id, task_date desc");
//			pst.setInt(1, emp_id);
//			rs = pst.executeQuery();
//			
//			Map<String, List<List<String>>> hmActivities = new HashMap<String, List<List<String>>>();
//			List<List<String>> alTaskActivity = new ArrayList<List<String>>();
//			
//			while(rs.next()) {
//				
//				alTaskActivity = hmActivities.get(rs.getString("activity_id"));
//				if(alTaskActivity == null) alTaskActivity = new ArrayList<List<String>>();
//				
//				List<String> alInner = new ArrayList<String>();
//				alInner.add(rs.getString("task_id"));
//				alInner.add(uF.getDateFormat(rs.getString("task_date"), DBDATE, CF.getStrReportDateFormat()));
//				alInner.add(uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
//				if(rs.getString("end_time")!=null) {
//					alInner.add(uF.getDateFormat(rs.getString("end_time"), DBTIME, CF.getStrReportTimeFormat()));
//				} else {
//					alInner.add(null);
//				}
//				alInner.add(uF.getTotalTimeMinutes100To60(rs.getString("actual_hrs")));
//				alInner.add(uF.showData(rs.getString("activity_description"), "-"));
//				alInner.add(uF.getTotalTimeMinutes100To60(rs.getString("billable_hrs")));
//				alInner.add((rs.getString("task_location")!= null && rs.getString("task_location").equals("ONS")) ? "Onsite" : "Offsite");
//				
//				alTaskActivity.add(alInner);
//				
//				hmActivities.put(rs.getString("activity_id"), alTaskActivity);
//			}
//			rs.close();
//			pst.close();
////			System.out.println("hmActivities ===>> " + hmActivities);
//			request.setAttribute("hmActivities", hmActivities);
			
			boolean timeApproveFlag = false;
			pst = con.prepareStatement("select is_approved from task_activity where emp_id=? and task_date=? and is_approved=2 ");
			pst.setInt(1, emp_id);
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()) {
				timeApproveFlag = true;
			}
			rs.close();
			pst.close();
			request.setAttribute("timeApproveFlag", timeApproveFlag);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private String getProFreqEndDayCount(Connection con, UtilityFunctions uF, String proId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String remainDays = "0";
		try {
			pst = con.prepareStatement("select freq_end_date from projectmntnc_frequency where freq_end_date >=? and pro_id=? limit 1"); //pro_id=? and
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt(proId));
			rs = pst.executeQuery();
			while (rs.next()) {
				remainDays = uF.dateDifference(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, rs.getString("freq_end_date"), DBDATE);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return remainDays;
	}



	private void getMyAssignedTasks(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> hmProName = CF.getProjectNameMap(con);
			StringBuilder sbMyAssignedTasks = new StringBuilder();
//			pst = con.prepareStatement("select ai.task_id, ai.activity_name, p.pro_name from activity_info ai, projectmntnc p where " +
//				"ai.parent_task_id = 0 and ai.pro_id = p.pro_id and (ai.completed < 100 or ai.completed is null) and ai.approve_status = 'n' " +
//				"and ai.resource_ids like '%,"+emp_id+",%' and ai.task_id not in (select distinct(activity_id) from task_activity where " +
//				"activity_id > 0) and task_accept_status != -1 order by ai.activity_name"); //pro_id=? and 
			pst = con.prepareStatement("select ai.task_id, ai.activity_name, ai.pro_id from activity_info ai where ai.parent_task_id = 0 and " +
				" (ai.kra_id = 0 or ai.kra_id is null) and (ai.completed < 100 or ai.completed is null) and ai.approve_status = 'n' " +
				" and ai.resource_ids like '%,"+emp_id+",%' and ai.task_id not in (select distinct(activity_id) from task_activity " +
				" where activity_id > 0)and task_accept_status != -1 order by ai.activity_name"); 
			rs = pst.executeQuery();
			while (rs.next()) {
				sbMyAssignedTasks.append("<option value='"+rs.getString("task_id")+"'>"+rs.getString("activity_name")+" ("+uF.showData(hmProName.get(rs.getString("pro_id")), "Not Aligned")+")"+"</option>");
			}
			rs.close();
			pst.close();
			request.setAttribute("sbMyAssignedTasks", sbMyAssignedTasks.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	//	System.out.println("sbTaskEmps ====>>> " + sbTaskEmps.toString());
		return sbTaskEmps.toString();
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
	//	System.out.println("sbTaskOptions ====>>> " + sbTaskOptions.toString());
		return sbTaskOptions.toString();
	}
//public void getProjectDetails() {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		try {
//			con = db.makeConnection(con);
//			Map<String, List<String>> hmProject = new HashMap<String, List<String>>();
//			Map<String, List<List<String>>> hmTasks = new HashMap<String, List<List<String>>>();
//			
//			Map<String, String> hmEmployee = CF.getEmpNameMap(con, null, null);
//			Map<String, String> hmServicemap = CF.getProjectServicesMap(con, true);
//			
//			
//			if(getPro_id() != 0) {
//				StringBuilder sbQuery = new StringBuilder();
//				sbQuery.append("select * from projectmntnc where pro_id > 0 ");
//				if(getProType() == null || getProType().equals("L")) {
//					sbQuery.append(" and approve_status='n' ");
//				} else if(getProType() != null && getProType().equals("C")) {
//					sbQuery.append(" and approve_status='approved' ");
//				}
//				sbQuery.append(" and pro_id in (select pro_id from project_emp_details where pro_id=? and emp_id=?) order by deadline asc");
////				pst = con.prepareStatement("select * from projectmntnc where approve_status = 'n' and pro_id in (select pro_id from project_emp_details where pro_id =? and emp_id = ?) order by deadline asc");
//				pst = con.prepareStatement(sbQuery.toString());
//				pst.setInt(1, getPro_id());
//				pst.setInt(2, emp_id);
//			} else {
//				StringBuilder sbQuery = new StringBuilder();
//				sbQuery.append("select * from projectmntnc where pro_id > 0 ");
//				if(getProType() == null || getProType().equals("L")) {
//					sbQuery.append(" and approve_status='n' ");
//				} else if(getProType() != null && getProType().equals("C")) {
//					sbQuery.append(" and approve_status='approved' ");
//				}
//				sbQuery.append(" and pro_id in (select pro_id from project_emp_details where emp_id = ?) order by deadline asc");
//				pst = con.prepareStatement(sbQuery.toString());
//				pst.setInt(1, emp_id);
//			}
//			rs = pst.executeQuery();
//			
//			GetPriorityList objGP = new GetPriorityList();
//			
//			StringBuilder sbProIds = null;
//			while(rs.next()) {
//				
//				if(sbProIds == null) {
//					sbProIds = new StringBuilder();
//					sbProIds.append(rs.getString("pro_id"));
//				} else {
//					sbProIds.append(","+rs.getString("pro_id"));
//				}
////				List<String> alInner = new ArrayList<String>();
////				alInner.add(rs.getString("pro_id"));
////				alInner.add(rs.getString("pro_name"));
////				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), ""));
////				
////				alInner.add(uF.showData(hmServicemap.get(rs.getString("service")), ""));
////				alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
////				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
////				
////				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
////					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
////					String actualTime = CF.getProjectActualTime(con, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
////					alInner.add(actualTime+" days");
////				} else {
////					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" hrs");
////					String actualTime = CF.getProjectActualTime(con, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
////					alInner.add(actualTime+" hrs");
////				}
////				alInner.add(rs.getString("completed"));
////				
////				java.util.Date dtStartDate = uF.getDateFormat(rs.getString("start_date"), DBDATE);
////				java.util.Date dtDeadLine = uF.getDateFormat(rs.getString("deadline"), DBDATE);
////				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
////				java.util.Date dtEntryDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE);
////				java.util.Date dtPrevDate = uF.getPrevDate(CF.getStrTimeZone(), 7);
////				
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
////				alInner.add(uF.showData(CF.getClientNameById(con, rs.getString("client_id")), ""));
////				if(dtEntryDate!=null && dtEntryDate.after(dtPrevDate)) {
////					alInner.add("1"); // show new icon on new projects	
////				} else {
////					alInner.add("0");
////				}
////				hmProject.put(rs.getString("pro_id"), alInner);
//			}
//			rs.close();
//			pst.close();
//			if(sbProIds == null) {
//				sbProIds = new StringBuilder();
//			}
//			request.setAttribute("hmProject", hmProject);
//
////			System.out.println("hmProject ===>> " + hmProject);
//			
//			Map<String, String> hmPMilestoneSize = new HashMap<String, String>();
//			pst = con.prepareStatement("select count(project_milestone_id) as count, pro_id from project_milestone_details group by pro_id");
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmPMilestoneSize.put(rs.getString("pro_id"), rs.getString("count"));
//			}
//			rs.close();
//			pst.close();
//			request.setAttribute("hmPMilestoneSize", hmPMilestoneSize);
//			
//			
//			Map<String, String> hmPDocumentCounter = new HashMap<String, String>();
//			pst = con.prepareStatement("select count(pro_document_id) as count, pro_id from project_document_details where file_size is not null and " +
//				"(sharing_type =0 or sharing_type = 1 or sharing_resources like '%,"+emp_id+",%') group by pro_id"); //pro_folder_id > 0 and 
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmPDocumentCounter.put(rs.getString("pro_id"), rs.getString("count"));
//			}
//			rs.close();
//			pst.close();
////			System.out.println("hmPDocumentCounter ====>>> " + hmPDocumentCounter);
//			request.setAttribute("hmPDocumentCounter", hmPDocumentCounter);
//			
//			if(getPro_id()!=0) {
//				StringBuilder sbque = new StringBuilder();
//				sbque.append("select pmc.actual_calculation_type, ai.reassign_by, ai.approve_status, ai.emp_id, pmc.billing_type, ai.task_id, " +
//					"ai.completed, ai.idealtime, ai.already_work_days, ai.start_date, ai.deadline, ai.end_date, ai.taskstatus, ai.activity_name, " +
//					"ai.finish_task, ai.priority, ai.already_work, ai.pro_id, ai.entry_date, pmc.client_id, ai.resource_ids, ai.parent_task_id, " +
//					"ai.task_from_my_self, ai.added_by from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id and ai.resource_ids " +
//					"like '%,"+emp_id+",%' and ai.pro_id = ? and ai.parent_task_id = 0 ");
//				if(getProType() == null || getProType().equals("L")) {
//					sbque.append(" and pmc.approve_status='n' ");
//				} else if(getProType() != null && getProType().equals("C")) {
//					sbque.append(" and pmc.approve_status='approved' ");
//				}
//				sbque.append("order by ai.deadline desc, priority desc");
//				pst = con.prepareStatement(sbque.toString());
////				pst.setInt(1, emp_id);
//				pst.setInt(1, getPro_id());
//			} else {
//				StringBuilder sbque = new StringBuilder();
//				sbque.append("select pmc.actual_calculation_type, ai.reassign_by, ai.approve_status, ai.emp_id, pmc.billing_type, ai.task_id, " +
//					"ai.completed, ai.idealtime, ai.already_work_days, ai.start_date, ai.deadline, ai.end_date, ai.taskstatus, ai.activity_name, " +
//					"ai.finish_task, ai.priority, ai.already_work, ai.pro_id, ai.entry_date, pmc.client_id, ai.resource_ids, ai.parent_task_id, " +
//					"ai.task_from_my_self, ai.added_by from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id and ai.resource_ids " +
//					"like '%,"+emp_id+",%' and  ai.parent_task_id = 0 ");
//				if(getProType() == null || getProType().equals("L")) {
//					sbque.append(" and pmc.approve_status='n' ");
//				} else if(getProType() != null && getProType().equals("C")) {
//					sbque.append(" and pmc.approve_status='approved' ");
//				}
//				sbque.append("order by ai.deadline desc, priority desc");
//				pst = con.prepareStatement(sbque.toString());
////				pst.setInt(1, emp_id);
//			}
//			
//			System.out.println("pst ===>> " + pst);
//			rs = pst.executeQuery();
//
//			List<List<String>> proTaskList = new ArrayList<List<String>>();
//			while(rs.next()) {
//				String strColour = null;
//				String strdaysColour = null;
//				String strworkingColour = null;
//				double no_of_hrs=0.0;
//				double no_of_days=0.0;
////				Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, rs.getString("pro_id"));
////				proTaskList = hmTasks.get(rs.getString("pro_id"));
////				if(proTaskList == null) proTaskList = new ArrayList<List<String>>();
//				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
//				
//				List<String> alInner = new ArrayList<String>();	
//				
//				String taskid = rs.getString("task_id");
//				alInner.add(taskid);  //0
//				
//				double dblCompleted = 0;
//				double dblIdealTime = 0;
//				double dblAlreadyWorked = 0;
//
//				String strTimeType = "";
//				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					strTimeType = "d";
//					dblCompleted = uF.parseToDouble(rs.getString("completed"));
//					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
////					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work_days"));
//					String strAlreadyWorked = CF.getProjectTaskTotalDaysOrHoursEmpwise(con, taskid, emp_id+"", hmProjectData);
//					dblAlreadyWorked = uF.parseToDouble(strAlreadyWorked);
//				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
//					strTimeType = "m";
//					dblCompleted = uF.parseToDouble(rs.getString("completed"));
//					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
////					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work_days"));
//					Map<String, String> hmAlreadyWorked = CF.getProjectTaskTotalMonthsEmpwise(con, CF, taskid, emp_id+"", hmProjectData);
//					dblAlreadyWorked = uF.parseToDouble(hmAlreadyWorked.get("ACTUAL_WORKING"));
//				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("H")) {
//					strTimeType = "h";
//					dblCompleted = uF.parseToDouble(rs.getString("completed"));
//					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
////					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work"));
//					String strAlreadyWorked = CF.getProjectTaskTotalDaysOrHoursEmpwise(con, taskid, emp_id+"", hmProjectData);
//					dblAlreadyWorked = uF.parseToDouble(strAlreadyWorked);
//				}
//				
//				
//				double idealPercent = 0;
//				
//				if(dblIdealTime > 0) {
//					idealPercent = (dblAlreadyWorked / dblIdealTime) * 100;
//				}
//				
//				if(idealPercent == dblCompleted) {
//					strworkingColour = "yellow";
//				} else if(idealPercent > dblCompleted) {
//					strworkingColour = "red";
//				} else {
//					strworkingColour = "green";
//				}
//				
//				double taskDealineCompletePercent = getTaskDeadlineCompletePercent(uF.parseToInt(taskid));
//				String tdcpSpan = "";
//				if(taskDealineCompletePercent <= 50) {
//					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/approved.png\"></span>";
//				} else if(taskDealineCompletePercent > 50 && taskDealineCompletePercent <= 75) {
//					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/re_submit.png\"></span>";
//				} else {
//					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/denied.png\"></span>";
//				}
//				
//				if(dblCompleted > 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
//					no_of_days=getNoOfDays(uF.parseToInt(taskid));
//				}
//				if(dblCompleted >= 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
//					no_of_hrs = dblAlreadyWorked - dblIdealTime;
//				}
//				
//				if(uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))>=0.0 && uF.parseToDouble(uF.dateDifference(rs.getString("end_date"), DBDATE, rs.getString("deadline"),DBDATE))>=0.0) {
//					strdaysColour="green";
//				} else {
//					strdaysColour="red";
//				}
//				if(dblAlreadyWorked>dblIdealTime) {
//					strColour="red";
//				} else {
//					strColour="green";
//				}
//				
////				String daySpan="none";
////				String timeSpan = "none";
////				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
////					daySpan="inline";
////				} else {
////					timeSpan="inline";
////				}
//				
//				String daySpan="none";
//				String daySpanLbl =" d";
//				String timeSpan = "none";
//				String timeSpanLbl = " h";
//				String strTime = uF.getTotalTimeMinutes100To60(""+no_of_hrs);
//				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					daySpan="inline";
//				} else {
//					if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
//						timeSpanLbl = " m";
//						strTime = uF.formatIntoTwoDecimal(no_of_hrs);
//					}
//					timeSpan="inline";
//				}
//				
//				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
//				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
//				
////				if(rs.getString("taskstatus") != null && rs.getString("taskstatus").equalsIgnoreCase("New Task")) {
////					
////					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
////						alInner.add("<span style=\"color:#5D862B\">Confirmed</span> (<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b> "+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
////						alInner.add("<span style=\"color:red\">Overdue </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
////						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
////					} else {
////						alInner.add("<span style=\"color:orange\">Planned</span>");
////					}
////					
////				} else if(dtCurrentDate!=null && dtDeadlineDate!=null && dtCurrentDate.after(dtDeadlineDate)) {
////					
////					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
////						alInner.add("<span style=\"color:#5D862B\">Confirmed</span> (<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>days</span>)");
////					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
////						alInner.add("<span style=\"color:red\">Overdue </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
////						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
////					} else {
////						alInner.add("<span style=\"color:"+strColour+"\">Working</span> (<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					}
////					
////				} else if(rs.getInt("completed")>=100) {
////					
////					if(rs.getInt("completed")>=100 && rs.getString("approve_status") != null && rs.getString("approve_status").equalsIgnoreCase("approved")) {
////						alInner.add("<span style=\"color:#5D862B\">Confirmed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
////						alInner.add("<span style=\"color:red\">Overdue </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else {
////						alInner.add("<span style=\"color:"+strColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					}
////				} else {
////					if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
////						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
////					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>");
////					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>");
////					} else {
////						alInner.add("<span style=\"color:"+strColour+"\">Working </span>");
////					}
////				}
//				
//					if(rs.getString("taskstatus") != null && rs.getString("taskstatus").equalsIgnoreCase("New Task")) {
//					
//					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
//						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Confirmed\" src=\"images1/icons/icons/approve_icon.png\"></span>" +
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>" +
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b> "+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else {
//						alInner.add(tdcpSpan+"");
//					}
//					
//				} else if(dtCurrentDate!=null && dtDeadlineDate!=null && dtCurrentDate.after(dtDeadlineDate)) {
//					
//					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
//						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Confirmed\" src=\"images1/icons/icons/approve_icon.png\"></span>" +
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>days</span>)");
//					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>" +
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					}
//					
//				} else if(rs.getInt("completed")>=100) {
//					
//					if(rs.getInt("completed")>=100 && rs.getString("approve_status") != null && rs.getString("approve_status").equalsIgnoreCase("approved")) {
//						alInner.add("<span style=\"color:#5D862B\"><img title=\"Confirmed\" src=\"images1/icons/icons/approve_icon.png\"></span>"+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>" +
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					}
//				} else {
//						if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/act_now.png\"></span>" +
//								"");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add(tdcpSpan+
//								"");
//					} else {
//						alInner.add(tdcpSpan+
//								"");
//					}
//				}
//				
//				alInner.add(rs.getString("activity_name")); //2
//				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), "")); //3
//				alInner.add(rs.getString("priority")); //4
////				boolean isTeamLead = CF.getProjectTLByEmpId(con, rs.getString("pro_id"), rs.getString("emp_id")); 
////				alInner.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")); //5
//				alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat())); //5
//				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat())); //6
//				
//				if(rs.getString("actual_calculation_type")!=null && !rs.getString("actual_calculation_type").equalsIgnoreCase("H")) {
//					alInner.add(uF.formatIntoTwoDecimal(dblIdealTime)); //7
//					alInner.add(uF.formatIntoTwoDecimal(dblAlreadyWorked)); //8
//				} else {
//					alInner.add(uF.getTotalTimeMinutes100To60(""+dblIdealTime)); //7
//					alInner.add(uF.getTotalTimeMinutes100To60(""+dblAlreadyWorked)); //8
//				}
//				
//				alInner.add(uF.showData(rs.getString("completed"), "0")); //9
//				alInner.add(uF.showData(rs.getString("approve_status"), "")); //10
//				alInner.add(uF.showData(rs.getString("taskstatus"), "")); //11
//				alInner.add(dblAlreadyWorked+""); //12
//				alInner.add(uF.showData(rs.getString("task_from_my_self"), "")); //13
//				alInner.add(uF.showData(CF.getProjectNameById(con, rs.getString("pro_id")), "")); //14
//				alInner.add(uF.showData(CF.getEmpNameMapByEmpId(con, rs.getString("added_by")), "-")); //15
//				String taskDocCnt = getTaskDocumentsCount(con, uF, rs.getString("pro_id"), rs.getString("task_id"));
//				alInner.add(taskDocCnt); //16
//				alInner.add(strTimeType); //17
//				alInner.add(uF.showData(rs.getString("pro_id"), "")); //18
////				boolean taskrunningFlag = checkTaskStatus();
//				String taskActivityRunningId = checkTaskActivityRunningId(con, uF, rs.getString("task_id"));
////				alInner.add(taskrunningFlag+""); //
//				alInner.add(taskActivityRunningId); //19
//				
//				proTaskList.add(alInner);
//				
//				hmTasks.put(rs.getString("pro_id"), proTaskList);
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("proTaskList", proTaskList);
//			
//			if(getPro_id()!=0) {
//				StringBuilder sbque = new StringBuilder();
//				sbque.append("select pmc.actual_calculation_type, ai.reassign_by, ai.approve_status, ai.emp_id, pmc.billing_type, ai.task_id, " +
//					"ai.completed, ai.idealtime, ai.already_work_days, ai.start_date, ai.deadline, ai.end_date, ai.taskstatus, ai.activity_name, " +
//					"ai.finish_task, ai.priority, ai.already_work, ai.pro_id, ai.entry_date, pmc.client_id, ai.resource_ids, ai.parent_task_id, " +
//					"ai.task_from_my_self, ai.added_by from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id and ai.resource_ids " +
//					"like '%,"+emp_id+",%' and ai.pro_id = ? and ai.parent_task_id != 0 ");
//				if(getProType() == null || getProType().equals("L")) {
//					sbque.append(" and pmc.approve_status='n' ");
//				} else if(getProType() != null && getProType().equals("C")) {
//					sbque.append(" and pmc.approve_status='approved' ");
//				}
//				sbque.append("order by ai.deadline desc, priority desc");
//				pst = con.prepareStatement(sbque.toString());
//				pst.setInt(1, getPro_id());
//			} else {
//				StringBuilder sbque = new StringBuilder();
//				sbque.append("select pmc.actual_calculation_type, ai.reassign_by, ai.approve_status, ai.emp_id, pmc.billing_type, ai.task_id, " +
//					"ai.completed, ai.idealtime, ai.already_work_days, ai.start_date, ai.deadline, ai.end_date, ai.taskstatus, ai.activity_name, " +
//					"ai.finish_task, ai.priority, ai.already_work, ai.pro_id, ai.entry_date, pmc.client_id, ai.resource_ids, ai.parent_task_id, " +
//					"ai.task_from_my_self, ai.added_by from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id and ai.resource_ids " +
//					"like '%,"+emp_id+",%' and ai.parent_task_id != 0 ");
//				if(getProType() == null || getProType().equals("L")) {
//					sbque.append(" and pmc.approve_status='n' ");
//				} else if(getProType() != null && getProType().equals("C")) {
//					sbque.append(" and pmc.approve_status='approved' ");
//				}
//				sbque.append("order by ai.deadline desc, priority desc");
//				pst = con.prepareStatement(sbque.toString());
//			}
//				
//			rs = pst.executeQuery();
//			Map<String, List<List<String>>> hmSubTasks = new HashMap<String, List<List<String>>>();
//			List<List<String>> proSubTaskList = new ArrayList<List<String>>();
//			while(rs.next()) {
//				String strColour = null;
//				String strdaysColour = null;
//				String strworkingColour = null;
//				double no_of_hrs = 0.0;
//				double no_of_days = 0.0;
////				Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, rs.getString("pro_id"));
//				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
//				proSubTaskList = hmSubTasks.get(rs.getString("parent_task_id"));
//				if(proSubTaskList == null) proSubTaskList = new ArrayList<List<String>>();
//				
//				List<String> alInner = new ArrayList<String>();	
//				
//				String taskid = rs.getString("task_id");
//				alInner.add(taskid); //0
//				
//				double dblCompleted = 0;
//				double dblIdealTime = 0;
//				double dblAlreadyWorked = 0;
//				
//				String strTimeType = "";
//				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					strTimeType = "d";
//					dblCompleted = uF.parseToDouble(rs.getString("completed"));
//					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
////					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work_days"));
//					String strAlreadyWorked = CF.getProjectTaskTotalDaysOrHoursEmpwise(con, taskid, emp_id+"", hmProjectData);
//					dblAlreadyWorked = uF.parseToDouble(strAlreadyWorked);
//				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
//					strTimeType = "m";
//					dblCompleted = uF.parseToDouble(rs.getString("completed"));
//					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
////					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work_days"));
//					Map<String, String> hmAlreadyWorked = CF.getProjectTaskTotalMonthsEmpwise(con, CF, taskid, emp_id+"", hmProjectData);
//					dblAlreadyWorked = uF.parseToDouble(hmAlreadyWorked.get("ACTUAL_WORKING"));
//				} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("H")) {
//					strTimeType = "h";
//					dblCompleted = uF.parseToDouble(rs.getString("completed"));
//					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
////					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work"));
//					String strAlreadyWorked = CF.getProjectTaskTotalDaysOrHoursEmpwise(con, taskid, emp_id+"", hmProjectData);
//					dblAlreadyWorked = uF.parseToDouble(strAlreadyWorked);
//				}
//				
//				double idealPercent = 0;
//				
//				if(dblIdealTime > 0) {
//					idealPercent = (dblAlreadyWorked / dblIdealTime) * 100;
//				}
//				
//				if(idealPercent == dblCompleted) {
//					strworkingColour = "yellow";
//				} else if(idealPercent > dblCompleted) {
//					strworkingColour = "red";
//				} else {
//					strworkingColour = "green";
//				}
//				
//				double taskDealineCompletePercent = getTaskDeadlineCompletePercent(uF.parseToInt(taskid));
//				String tdcpSpan = "";
//				if(taskDealineCompletePercent <= 50) {
//					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/approved.png\"></span>";
//				} else if(taskDealineCompletePercent > 50 && taskDealineCompletePercent <= 75) {
//					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/re_submit.png\"></span>";
//				} else {
//					tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/denied.png\"></span>";
//				}
//				
//				if(dblCompleted > 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
//					no_of_days=getNoOfDays(uF.parseToInt(taskid));
//				}
//				if(dblCompleted >= 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
//					no_of_hrs = dblAlreadyWorked - dblIdealTime;
//				}
//				
//				if(uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))>=0.0 && uF.parseToDouble(uF.dateDifference(rs.getString("end_date"), DBDATE, rs.getString("deadline"),DBDATE))>=0.0) {
//					strdaysColour="green";
//				} else {
//					strdaysColour="red";
//				}
//				if(dblAlreadyWorked>dblIdealTime) {
//					strColour="red";
//				} else {
//					strColour="green";
//				}
//				
////				String daySpan="none";
////				String timeSpan = "none";
////				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
////					daySpan="inline";
////				} else {
////					timeSpan="inline";
////				}
//				
//				String daySpan="none";
//				String daySpanLbl = " d";
//				String timeSpan = "none";
//				String timeSpanLbl = " h";
//				String strTime = uF.getTotalTimeMinutes100To60(""+no_of_hrs);
//				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					daySpan="inline";
//				} else {
//					if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
//						timeSpanLbl = " m";
//						strTime = uF.formatIntoTwoDecimal(no_of_hrs);
//					}
//					timeSpan="inline";
//				}
//				
//				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
//				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
//				
////				if(rs.getString("taskstatus") != null && rs.getString("taskstatus").equalsIgnoreCase("New Task")) {
////					
////					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
////						alInner.add("<span style=\"color:#5D862B\">Confirmed</span> (<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b> "+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"), DBDATE))<0.0) {
////						alInner.add("<span style=\"color:red\">Overdue </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
////						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
////					} else {
////						alInner.add("<span style=\"color:orange\">Planned</span>");
////					}
////					
////				} else if(dtCurrentDate!=null && dtDeadlineDate!=null && dtCurrentDate.after(dtDeadlineDate)) {
////					
////					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
////						alInner.add("<span style=\"color:#5D862B\">Confirmed</span> (<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>days</span>)");
////					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
////						alInner.add("<span style=\"color:red\">Overdue </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
////						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
////					} else {
////						alInner.add("<span style=\"color:"+strColour+"\">Working</span> (<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					}
////					
////				} else if(rs.getInt("completed")>=100) {
////					
////					if(rs.getInt("completed")>=100 && rs.getString("approve_status") != null && rs.getString("approve_status").equalsIgnoreCase("approved")) {
////						alInner.add("<span style=\"color:#5D862B\">Confirmed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
////						alInner.add("<span style=\"color:red\">Overdue </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					} else {
////						alInner.add("<span style=\"color:"+strColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
////					}
////				} else {
////					if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
////						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
////					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>");
////					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
////						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>");
////					} else {
////						alInner.add("<span style=\"color:"+strColour+"\">Working </span>");
////					}
////				}
//				
//				
//				if(rs.getString("taskstatus") != null && rs.getString("taskstatus").equalsIgnoreCase("New Task")) {
//					
//					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
//						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Confirmed\" src=\"images1/icons/icons/approve_icon.png\"></span>" +
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>" +
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b> "+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else {
//						alInner.add(tdcpSpan+"");
//					}
//					
//				} else if(dtCurrentDate!=null && dtDeadlineDate!=null && dtCurrentDate.after(dtDeadlineDate)) {
//					
//					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
//						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Confirmed\" src=\"images1/icons/icons/approve_icon.png\"></span>" +
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>days</span>)");
//					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>" +
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					}
//					
//				} else if(rs.getInt("completed")>=100) {
//					
//					if(rs.getInt("completed")>=100 && rs.getString("approve_status") != null && rs.getString("approve_status").equalsIgnoreCase("approved")) {
//						alInner.add("<span style=\"color:#5D862B\"><img title=\"Confirmed\" src=\"images1/icons/icons/approve_icon.png\"></span>"+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"Completed\" src=\"images1/icons/act_now.png\"></span>" +
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					} else {
//						alInner.add(tdcpSpan+
//								"(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+strTime+"</b>"+timeSpanLbl+"</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>"+daySpanLbl+"</span>)");
//					}
//				} else {
//						if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/act_now.png\"></span>" +
//								"");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add(tdcpSpan+
//								"");
//					} else {
//						alInner.add(tdcpSpan+
//								"");
//					}
//				}
//				
//				alInner.add(rs.getString("activity_name")); //2
//				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), "")); //3
//				alInner.add(rs.getString("priority")); //4
////				boolean isTeamLead = CF.getProjectTLByEmpId(con, rs.getString("pro_id"), rs.getString("emp_id"));
////				alInner.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")); //5
//				alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat())); //5
//				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat())); //6
//				
//				if(rs.getString("actual_calculation_type")!=null && !rs.getString("actual_calculation_type").equalsIgnoreCase("H")) {
//					alInner.add(uF.formatIntoOneDecimal(dblIdealTime)); //7
//					alInner.add(uF.formatIntoOneDecimal(dblAlreadyWorked)); //8
//				} else {
//					alInner.add(uF.getTotalTimeMinutes100To60(""+dblIdealTime)); //7
//					alInner.add(uF.getTotalTimeMinutes100To60(""+dblAlreadyWorked)); //8
//				}
//				
//				alInner.add(uF.showData(rs.getString("completed"), "0")); //9
//				alInner.add(uF.showData(rs.getString("approve_status"), "")); //10
//				alInner.add(uF.showData(rs.getString("taskstatus"), "")); //11
//				alInner.add(dblAlreadyWorked+""); //12
//				alInner.add(uF.showData(rs.getString("task_from_my_self"), "")); //13
//				alInner.add(uF.showData(CF.getProjectNameById(con, rs.getString("pro_id")), "")); //14
//				alInner.add(uF.showData(CF.getEmpNameMapByEmpId(con, rs.getString("added_by")), "-")); //15
//				String taskDocCnt = getTaskDocumentsCount(con, uF, rs.getString("pro_id"), rs.getString("task_id"));
//				alInner.add(taskDocCnt); //16
//				alInner.add(strTimeType); //17
//				alInner.add(uF.showData(rs.getString("pro_id"), "")); //18
////				boolean taskrunningFlag = checkTaskStatus();
//				String taskActivityRunningId = checkTaskActivityRunningId(con, uF, rs.getString("task_id"));
////				alInner.add(taskrunningFlag+""); //
//				alInner.add(taskActivityRunningId); //19
//				
//				proSubTaskList.add(alInner);
//				
//				hmSubTasks.put(rs.getString("parent_task_id"), proSubTaskList);
//			}
//			rs.close();
//			pst.close();
//			
////			System.out.println("hmSubTasks ===>> " + hmSubTasks);
////			System.out.println("hmTasks ===>> " + hmTasks);
//			
//			request.setAttribute("hmSubTasks", hmSubTasks);
//			request.setAttribute("hmTasks", hmTasks);
//			
//			
//			
////			pst = con.prepareStatement("select * from task_activity where activity_id in (select task_id from activity_info where resource_ids like '%,"+emp_id+",%') order by activity_id, task_date desc");
//			pst = con.prepareStatement("select * from task_activity where emp_id = ? order by activity_id, task_date desc");
//			pst.setInt(1, emp_id);
//			rs = pst.executeQuery();
//			
//			Map<String, List<List<String>>> hmActivities = new HashMap<String, List<List<String>>>();
//			List<List<String>> alTaskActivity = new ArrayList<List<String>>();
//			
//			while(rs.next()) {
//				
//				alTaskActivity = hmActivities.get(rs.getString("activity_id"));
//				if(alTaskActivity == null) alTaskActivity = new ArrayList<List<String>>();
//				
//				List<String> alInner = new ArrayList<String>();
//				alInner.add(rs.getString("task_id"));
//				alInner.add(uF.getDateFormat(rs.getString("task_date"), DBDATE, CF.getStrReportDateFormat()));
//				alInner.add(uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
//				if(rs.getString("end_time")!=null) {
//					alInner.add(uF.getDateFormat(rs.getString("end_time"), DBTIME, CF.getStrReportTimeFormat()));
//				} else {
//					alInner.add(null);
//				}
//				alInner.add(uF.getTotalTimeMinutes100To60(rs.getString("actual_hrs")));
//				alInner.add(uF.showData(rs.getString("activity_description"), "-"));
//				alInner.add(uF.getTotalTimeMinutes100To60(rs.getString("billable_hrs")));
//				alInner.add((rs.getString("task_location")!= null && rs.getString("task_location").equals("ONS")) ? "Onsite" : "Offsite");
//				
//				alTaskActivity.add(alInner);
//				
//				hmActivities.put(rs.getString("activity_id"), alTaskActivity);
//			}
//			rs.close();
//			pst.close();
////			System.out.println("hmActivities ===>> " + hmActivities);
//			request.setAttribute("hmActivities", hmActivities);
//			
//			boolean timeApproveFlag = false;
//			pst = con.prepareStatement("select is_approved from task_activity where emp_id=? and task_date=? and is_approved=2 ");
//			pst.setInt(1, emp_id);
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				timeApproveFlag = true;
//			}
//			rs.close();
//			pst.close();
//			request.setAttribute("timeApproveFlag", timeApproveFlag);
////			System.out.println("timeApproveFlag ===>> " + timeApproveFlag);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}



private Map<String, String> checkTaskActivityRunningId(Connection con, UtilityFunctions uF) { //, String taskId
	ResultSet rs = null;
	PreparedStatement pst = null;
//	String strTaskId = null;
	Map<String, String> hmTaskActRunId = new HashMap<String, String>();
	try {
		pst = con.prepareStatement("select activity_id,task_id from task_activity where emp_id =? and end_time is null");
		pst.setInt(1, emp_id);
//		pst.setInt(2, uF.parseToInt(taskId));
		rs = pst.executeQuery();
		while (rs.next()) {
			hmTaskActRunId.put(rs.getString("activity_id"), rs.getString("task_id"));
//			strTaskId = rs.getString("task_id");
		}
//		System.out.println("hmTaskActRunId task_id ===>> " + hmTaskActRunId);
		rs.close();
		pst.close();

	} catch (Exception e) {
		e.printStackTrace();
	} 
	return hmTaskActRunId;
}


private String getTaskDocumentsCount(Connection con, UtilityFunctions uF, String proId, String taskId) {
	PreparedStatement pst = null;
	ResultSet rs = null;
	
	String strTaskDocCnt = null;
	try {
//		Map<String, String> hmPDocumentCounter = new HashMap<String, String>();
		pst = con.prepareStatement("select count(pro_document_id) as count from project_document_details where file_size is not null and " +
			"(sharing_type =0 or sharing_type = 1 or sharing_resources like '%,"+emp_id+",%') and ((align_with=0 and project_category=1 and pro_id>0 and pro_id=?) " +
			"or (align_with>0 and project_category=2 and pro_id>0 and pro_id=?) or (align_with = ? and pro_id=?))"); // (align_with = 0 or align_with = ?) and pro_id=? 
		pst.setInt(1, uF.parseToInt(proId));
		pst.setInt(2, uF.parseToInt(proId));
		pst.setInt(3, uF.parseToInt(taskId));
		pst.setInt(4, uF.parseToInt(proId));
		rs = pst.executeQuery();
//		System.out.println("pst ===>> " + pst);
		while (rs.next()) {
			strTaskDocCnt = rs.getString("count");
//			hmPDocumentCounter.put(rs.getString("pro_id"), rs.getString("count"));
		}
		rs.close();
		pst.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	} 
	return strTaskDocCnt;
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
		
		ideal_days = uF.parseToDouble(uF.dateDifference(start_date, DBDATE, dl_date, DBDATE));
		
		actual_days = uF.parseToDouble(uF.dateDifference(start_date, DBDATE, uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE));
		
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
	
//	public void getProjectDetails() {
//		ResultSet rs = null;
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		try {
//
//			con = db.makeConnection(con);
//			
//			Map<String, List<String>> hmProject = new HashMap<String, List<String>>();
//			Map<String, List<String>> hmTasks = new LinkedHashMap<String, List<String>>();
//			Map<String, List<String>> hmActivities = new HashMap<String, List<String>>();
//			
//			Map<String, String> hmEmployee = CF.getEmpNameMap(con, null, null);
//			
//			if(getPro_id() != 0) {
//				pst = con.prepareStatement("select * from projectmntnc where approve_status = 'n' and pro_id in (select pro_id from project_emp_details where pro_id =? and emp_id = ?) order by deadline asc");
//				pst.setInt(1, getPro_id());
//				pst.setInt(2, emp_id);
////				pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
////				pst.setInt(1, getPro_id());
//			} else {
//				pst = con.prepareStatement("select * from projectmntnc where approve_status = 'n' and pro_id in (select pro_id from project_emp_details where emp_id = ?) order by deadline asc");
//				pst.setInt(1, emp_id);
//			}
//			rs = pst.executeQuery();
//			
//			while(rs.next()) {
//				
//				List<String> alInner = new ArrayList<String>();
//				alInner.add(rs.getString("pro_id"));
//				alInner.add(rs.getString("pro_name"));
//				alInner.add(rs.getString("priority"));
//				
//				alInner.add(rs.getString("service"));
//				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
//				
//				if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("D")) {
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("already_work_days")))+" days");
//				} else {
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" hrs");
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("already_work")))+" hrs");
//				}
//				
//				alInner.add(rs.getString("completed"));
//				alInner.add((("n".equalsIgnoreCase(rs.getString("approve_status")))?"Pending":"Completed"));
//				
//				hmProject.put(rs.getString("pro_id"), alInner);
//			} 
//
//			request.setAttribute("hmProject", hmProject);
//			
//			System.out.println("hmProject ===>> " + hmProject);
//			
//			
//			Map<String, String> hmProjectClientMap = CF.getProjectClientMap(con, uF);
//			Map<String, String> hmProjectMap = CF.getProjectNameMap(con);
//			
//			if(getPro_id()!=0)
//			{
//				pst = con.prepareStatement("select ai.reassign_by, ai.approve_status, ai.emp_id, pmc.billing_type,ai.task_id, ai.completed, ai.idealtime, ai.already_work_days,ai.deadline, ai.end_date,ai.taskstatus,ai.activity_name, ai.finish_task, ai.completed, ai.priority, ai.already_work, ai.pro_id, ai.entry_date, pmc.client_id from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id and ai.emp_id = ? and ai.approve_status='n' and pmc.approve_status='n' and ai.pro_id = ? order by ai.deadline desc, priority desc");
//				pst.setInt(1, emp_id);
//				pst.setInt(2, getPro_id());
//			} else {
//				pst = con.prepareStatement("select ai.reassign_by, ai.approve_status, ai.emp_id, pmc.billing_type,ai.task_id, ai.completed, ai.idealtime, ai.already_work_days,ai.deadline, ai.end_date,ai.taskstatus,ai.activity_name, ai.finish_task, ai.completed, ai.priority, ai.already_work, ai.pro_id, ai.entry_date, pmc.client_id from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id and ai.emp_id = ? and ai.approve_status='n' and pmc.approve_status='n'  order by ai.deadline desc, priority desc");
//				pst.setInt(1, emp_id);
//				
//			}
//			
//			rs = pst.executeQuery();
//			GetPriorityList objGP = new GetPriorityList();
//			
//			String strProjectIdNew = null;
//			String strProjectIdOld = null;
//			List<String> alInner = new ArrayList<String>();
//			while(rs.next()) {
//				double no_of_days=0.0;
//				String strColour = null;
//				String strdaysColour = null;
//				double no_of_hrs =0.0;
//				strProjectIdNew = rs.getString("pro_id"); 
//				/*if(strProjectIdNew!=null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)) {
//					alInner = new ArrayList<String>();
//				}*/
//				
//				alInner = hmTasks.get(strProjectIdNew);
//				if(alInner==null)alInner = new ArrayList<String>();
//				
//				String taskid=rs.getString("task_id");
//				alInner.add(taskid);
//				alInner.add(strProjectIdNew);
//				// days and time calculations
//				
//				double dblCompleted = 0;
//				double dblIdealTime  = 0;
//				double dblAlreadyWorked = 0;
//				
//				if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("D")) {
//					dblCompleted = uF.parseToDouble(rs.getString("completed"));
//					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
//					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work_days"));
//				} else {
//					dblCompleted = uF.parseToDouble(rs.getString("completed"));
//					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
//					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work"));
//				}
//				
//				if(dblCompleted>=100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
//					no_of_days=getNoOfDays(uF.parseToInt(taskid));
//				}
//				if(dblCompleted>=100) {
//					no_of_hrs=dblAlreadyWorked - dblIdealTime;
//				}
//				
//				if(uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))>=0.0 && uF.parseToDouble(uF.dateDifference(rs.getString("end_date"), DBDATE, rs.getString("deadline"),DBDATE))>=0.0) {
//					strdaysColour="green";
//				} else {
//					strdaysColour="red";
//				}
//				if(dblAlreadyWorked > dblIdealTime) {
//					strColour="red";
//				} else {
//					strColour="green";
//				}
//				
//				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
//				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
//				
//				if(rs.getString("taskstatus").equalsIgnoreCase("New Task")) {
//					alInner.add("<span style=\"color:orange\">Planned</span>"); 
//				} else if(dtCurrentDate!=null && dtDeadlineDate!=null && dtCurrentDate.after(dtDeadlineDate)) {
//					
//					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
//						alInner.add("<span style=\"color:#5D862B\">Confirmed</span> (<b><span style=\"color:"+strColour+"\">" +uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b>days )");
//					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/<b> <span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days )");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
//						alInner.add("<span style=\"color:red\">Overdue </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days )");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"color:"+strColour+"\">Working </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days )");
//					} else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
//						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
//					} else {
//						alInner.add("<span style=\"color:"+strColour+"\">Working</span> (<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/<b> <span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days )");
//					}
//					
//				} else if(rs.getInt("completed")>=100) {
//					
//					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
//						alInner.add("<span style=\"color:#5D862B\">Confirmed </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/<b><span style=\"color:"+strdaysColour+"\"> "+no_of_days+"</span></b> days )");
//					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/<b> <span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days )");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
//						alInner.add("<span style=\"color:red\">Overdue </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days )");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"color:"+strColour+"\">Working </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days)");
//					} else {
//						alInner.add("<span style=\"color:"+strColour+"\">Working </span>( <b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days )");
//					}
//				} else {
//					if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
//						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
//					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"color:"+strColour+"\">Working </span>");
//					} else {
//						alInner.add("<span style=\"color:"+strColour+"\">Working </span>");
//					}
//				}
//				
//				String isFinish = rs.getString("finish_task");
//				if (rs.getDouble("completed") >= 100 && isFinish.equals("n")) {
//					alInner.add("<input type='checkbox' value='"+ taskid + "' name='isFinish' />");
//				} else {
//					alInner.add("");
//				}
//				
//				alInner.add(rs.getString("activity_name"));
//				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), "")); 
//				alInner.add(rs.getString("priority"));
//				alInner.add(uF.showData(hmEmployee.get(rs.getString("emp_id")), ""));
//				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
//				
//				
//				if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("D")) {
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
//					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("already_work_days")))+" days");
//				} else {
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" hrs");
//					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("already_work")))+" hrs");
//				}
//				
//				alInner.add(rs.getString("completed"));
//				
//				java.util.Date dtEntryDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE);
//				java.util.Date dtPrevDate = uF.getPrevDate(CF.getStrTimeZone(), 7);
//				
//				if(dtEntryDate!=null && dtEntryDate.after(dtPrevDate)) {
//					alInner.add("1"); // show new icon on new projects	
//				} else {
//					alInner.add("0");
//				}
//				
//				alInner.add(uF.showData(hmProjectMap.get(strProjectIdNew), ""));
//				alInner.add(uF.showData(hmProjectClientMap.get(rs.getString("client_id")), ""));
//				
//				hmTasks.put(rs.getString("pro_id"), alInner);
//				
//				strProjectIdOld = strProjectIdNew;
//			}
//
//			request.setAttribute("hmTasks", hmTasks);
//			
//			
//			pst = con.prepareStatement("select * from task_activity where activity_id in (select task_id from activity_info where emp_id = ?) order by activity_id, task_date desc");
//			pst.setInt(1, emp_id);
//			rs = pst.executeQuery();
//			
//			alInner = new ArrayList<String>();
//			
//			String strTaskIdNew = null;
//			String strTaskIdOld = null;
//			
//			while(rs.next()) {
//				strTaskIdNew = rs.getString("activity_id"); 
//				if(strTaskIdNew!=null && !strTaskIdNew.equalsIgnoreCase(strTaskIdOld)) {
//					alInner = new ArrayList<String>();
//				}
//				
//				alInner.add(rs.getString("task_id"));
//				alInner.add(uF.getDateFormat(rs.getString("task_date"), DBDATE, CF.getStrReportDateFormat()));
//				alInner.add(uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
//				
//				if(rs.getString("end_time")!=null) {
//					alInner.add(uF.getDateFormat(rs.getString("end_time"), DBTIME, CF.getStrReportTimeFormat()));
//				} else {
//					alInner.add(null);
//				}
//				
//				alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("actual_hrs"))));
//				
//				hmActivities.put(rs.getString("activity_id"), alInner);
//				strTaskIdOld = strTaskIdNew;
//			}
//
//			request.setAttribute("hmActivities", hmActivities);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
	public double getNoOfDays(int t_id) {
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		double no_of_days=0.0f;
		try {
			
			con = db.makeConnection(con);
			String dl_date = "";
			String start_date = "";
			String end_date = "";
			
			pst = con.prepareStatement("select deadline, start_date, end_date from activity_info where task_id=?");
			pst.setInt(1, t_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				dl_date = rs.getString(1);
				start_date = rs.getString(2);
				end_date = rs.getString(3);
			}
			rs.close();
			pst.close();
						
			ideal_days = uF.parseToDouble(uF.dateDifference(start_date, DBDATE, dl_date, DBDATE));
			if(end_date!=null) {
				actual_days = uF.parseToDouble(uF.dateDifference(start_date, DBDATE, end_date, DBDATE));
			} else {
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
	
	
//	public void FinishTask() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		if (isFinish != null) {
//			for (int i = 0; i < isFinish.length; i++) {
//				try {
//					con = db.makeConnection(con);
//					pst = con.prepareStatement("UPDATE activity_info SET finish_task='y',end_date=? WHERE task_id =?");
//					pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setInt(2, uF.parseToInt(isFinish[i]));
//					pst.executeUpdate();
//				} catch (Exception e) {
//					e.printStackTrace();
//				} finally {
//					db.closeStatements(pst);
//					db.closeConnection(con);
//				}
//			}
//		}
//	}
	
	
//	public String getTime(String sdate, String stdate) {
//		String dateStart = sdate;
//		String dateStop = stdate;
//		int a = dateStart.indexOf(".");
//		dateStart = dateStart.substring(0, a);
//		int b = dateStop.indexOf(".");
//		dateStop = dateStop.substring(0, b);
//
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date d1 = null;
//		Date d2 = null;
//		try {
//			d1 = format.parse(dateStart);
//			d2 = format.parse(dateStop);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		long diff = d2.getTime() - d1.getTime();
//		String tt1 = "";
//		long hours = diff / 3600000;
//		long min = (diff % 3600000) / 60000;
//		tt1 = hours + ".";
//		if (min > 9) {
//			tt1 += min;
//		} else {
//			tt1 += "0" + min;
//		}
//		System.out.println("Time in hours: " + tt1);
//		return tt1;
//	}
	
	
//	public void getProjectList() {
//		ResultSet rs = null;
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		try {
//			con = db.makeConnection(con);
//			
//			pst = con.prepareStatement("select distinct(pro.pro_id),pro.pro_name from activity_info ac,projectmntnc pro where ac.emp_id=? and pro.start_date<=? and pro.pro_id= ac.pro_id and pro.approve_status='n' order by pro_id");
//			pst.setInt(1, emp_id);
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			
//			
//			System.out.println("pst=>"+pst);
//			while (rs.next()) {
//				projectidlist.add(rs.getInt("pro_id"));
//				projectlist.add(rs.getString("pro_name"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}

	
//	public List<Integer> getProjectIds() {
//		ResultSet rs = null;
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		List<Integer> alCurrentProjects = new ArrayList<Integer>();
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select distinct(pmc.pro_id) as pro_id from activity_info ai, projectmntnc pmc where ai.pro_id = pmc.pro_id and pmc.approve_status = 'n' and ai.emp_id=? order by pro_id");
//			pst.setInt(1, emp_id);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				alCurrentProjects.add(rs.getInt("pro_id"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return alCurrentProjects;
//	}

	/*public List<Integer> getTaskIds(List<Integer> p_id) {
		List<Integer> ac_index = new ArrayList<Integer>();
		try {
			for (int k = 0; k < p_id.size(); k++) {
				pst = con.prepareStatement("select task_id from activity_info where emp_id=? and pro_id=?");
				pst.setInt(1, emp_id);
				pst.setInt(2, p_id.get(k));
				rs = pst.executeQuery();
				while (rs.next()) {
					ac_index.add(rs.getInt(1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ac_index;
	}*/

	
//	public void getAllProjects(List<Integer> p_id) {
//		ResultSet rs = null;
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		try {
//			con = db.makeConnection(con);
//			
//			for (int a = 0; a < p_id.size(); a++) {
//				pst = con.prepareStatement("select pro_name,priority,document_name,service,emp_id,completed,taskstatus,_comment,deadline,idealtime,already_work,completed,timestatus,teamlead_name from projectmntnc where pro_id=? and approve_status='n' order by pro_id");
//				pst.setInt(1, p_id.get(a));
//				rs = pst.executeQuery();
////				System.out.println("pst===>"+pst);
//				while (rs.next()) {
//					List<String> alInner = new ArrayList<String>();
//					alInner.add("<b>" + rs.getString("pro_name") + "</b>");
//					alInner.add("");
//					alInner.add(rs.getString("priority"));
//					String file = rs.getString("document_name");
//					if (file != null) {
//						alInner.add("<a href='taskuploads/" + file + "' target='blank'>" + file + "</a>");
//					} else {
//						alInner.add("No Attachments");
//					}
//					alInner.add(rs.getString("service"));
//					if (uF.parseToInt(rs.getString("completed")) < 100) {
//						alInner.add("");
//					} else {
//						alInner.add("Finished");
//					}
//					alInner.add(rs.getString("_comment"));
//					alInner.add(uF.getDateFormat(rs.getString("deadline"),DBDATE, CF.getStrReportDateFormat()));
//					alInner.add(rs.getString("idealtime") + "hrs");
//					alInner.add(rs.getDouble("already_work") + "hrs");
//					if (isSingle) {
//						alInner.add("");
//						alInner.add("");
//						alInner.add("");
//					} else {
//						alInner.add("");
//						alInner.add("<a href='EmpViewProject.action?pro_id="+ p_id.get(a) + "'>View More</a>");
//					}
//					al.put(p_id.get(a), alInner);
//					index.add(p_id.get(a));
//				}
//				getActivityDetailForAll(p_id);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}

	
//	public void getTimeDetail(int t_id) {
//		ResultSet rs = null;
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		try {
//			con = db.makeConnection(con);
//			
//			pst = con.prepareStatement("select * from task_activity where activity_id=? and emp_id=? order by task_id");
//			pst.setInt(1, t_id);
//			pst.setInt(2, emp_id);
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
	

//	public void getActivityDetailForAll(List<Integer> index) {
//		ResultSet rs = null;
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		try {
//			con = db.makeConnection(con);
//			
//			Map<Integer, List<List<String>>> activityDetailMap = new HashMap<Integer, List<List<String>>>();
//
//			for (int k = 0; k < index.size(); k++) {
//				List<List<String>> outInner = new ArrayList<List<String>>();
//				pst = con.prepareStatement("select * from activity_info where pro_id=? and emp_id=? and approve_status='n' order by task_id");
//				pst.setInt(1, index.get(k));
//				pst.setInt(2, emp_id);
//				rs = pst.executeQuery();
//				
//				while (rs.next()) {
//					List<String> alInner = new ArrayList<String>();
//					task_id = rs.getInt("task_id");
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
//					alInner.add(rs.getString("_comment"));
//					alInner.add(uF.getDateFormat(rs.getString("deadline"),DBDATE, CF.getStrReportDateFormat()));
//					alInner.add(rs.getString("idealtime") + "hrs");
//					int per = uF.parseToInt(rs.getString("completed"));
//					if (per > 0)
//						alInner.add(5, "Pending");
//					else
//						alInner.add(5, "New Task");
//					String isFinish = rs.getString("finish_task");
//					if (per >= 100 && isFinish.equals("n")) {
//						alInner.add(0, "<input type='checkbox' value='"	+ task_id + "' name='isFinish' />");
//					} else {
//						alInner.add(0, "");
//					}
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
//							alInner.add("");
//							alInner.add("");
//							alInner.add("Not Started");
//						}
//						totalhrz1 = new ArrayList<String>();
//						taskStatus1 = new ArrayList<String>();
//						time1 = new ArrayList<String>();
//						if (per < 100) {
//							if (rs.getString("timestatus").equals("n")) {
//								alInner.add("<input type=\"button\" name=\"start\" class=\"input_button\" value=\"start\" onclick=\"start123("+ task_id + ");\"/>");
//							} else if (rs.getString("timestatus").equals("y")) {
//								alInner.add("<a href=\"EndTaskPopup.action?id="+ task_id+"&pro_id="+index.get(k)+"\" onclick=\"return hs.htmlExpand(this, { objectType: 'ajax',width:700 })\"><input type=\"button\" name=\"start\" class=\"input_button\" value=\"End\"/></a>");
//							}
//						} else {
//							if (isFinish.equals("n")) {
//								alInner.add("Testing");
//							} else {
//								alInner.add("Finished");
//							}
//						}
//					} else {
//						alInner.add(rs.getDouble("already_work") + "hrs");
//						alInner.add(per + "%");
//						alInner.add("");
//					}
//					outInner.add(alInner);
//					activity_index.add(rs.getInt(1));
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

	
//	public void getPastProjectDetails() {
//			
//		ResultSet rs = null;
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		GetPriorityList objGP = new GetPriorityList();
//		
//		try {
//			
//			con =db.makeConnection(con);
//			
//			Map<String, List<String>> hmProject = new HashMap<String, List<String>>();
//			Map<String, List<String>> hmTasks = new HashMap<String, List<String>>();
//			Map<String, List<String>> hmActivities = new HashMap<String, List<String>>();
//			
//			Map<String, String> hmEmployee = CF.getEmpNameMap(con, null, null);
//			
//			if(getPro_id()!=0) {
//				pst = con.prepareStatement("select * from projectmntnc where pro_id=?");
//				pst.setInt(1, getPro_id());
//			} else {
//				pst = con.prepareStatement("select * from projectmntnc where pro_id in (select pro_id from project_emp_details where emp_id = ?) order by deadline asc");
//				pst.setInt(1, emp_id);
//			}
//			rs = pst.executeQuery();
//			
//			while(rs.next()) {
//				List<String> alInner = new ArrayList<String>();
//				
//				alInner.add(rs.getString("pro_id"));
//				alInner.add(rs.getString("pro_name"));
//				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), ""));
//				alInner.add(rs.getString("priority"));
//				
//				alInner.add(rs.getString("service"));
//				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
//				alInner.add(rs.getString("idealtime"));
//				alInner.add(rs.getString("already_work"));
//				alInner.add(rs.getString("completed"));
//				alInner.add((("n".equalsIgnoreCase(rs.getString("approve_status")))?"Pending":"Completed"));
//				
//				
//				hmProject.put(rs.getString("pro_id"), alInner);
//			}
//			rs.close();
//			pst.close();
//
//			request.setAttribute("hmProject", hmProject);
//			
//			pst = con.prepareStatement("select * from activity_info where emp_id = ? and approve_status='approved' order by deadline asc");
//			pst.setInt(1, emp_id);
//			rs = pst.executeQuery();
//			String strProjectIdNew = null;
//			String strProjectIdOld = null;
//			List<String> alInner = new ArrayList<String>();
//			while(rs.next()) {
//				double no_of_days=0.0;
//				double no_of_hrs=0.0;
//				String strColour = null;
//				String strdaysColour = null;
//				strProjectIdNew = rs.getString("pro_id"); 
//				if(strProjectIdNew!=null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)) {
//					alInner = new ArrayList<String>();
//				}
//				String taskid=rs.getString("task_id");
//				alInner.add(taskid);
//				// days and time calculations
//				if(rs.getInt("completed")>100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))>0.0 ) {
//					no_of_days = getNoOfDays(uF.parseToInt(taskid));
//				}
//				if(rs.getInt("completed")>=100) {
//					no_of_hrs=uF.parseToDouble(rs.getString("already_work"))-uF.parseToDouble(rs.getString("idealtime"));
//				}
//				if(uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))>=0.0 && uF.parseToDouble(uF.dateDifference(rs.getString("end_date"), DBDATE, rs.getString("deadline"),DBDATE))>=0.0)
//				{
//					
//					strdaysColour="green";
//					
//				} else {
//					strdaysColour="red";
//					
//				}
//				if(uF.parseToDouble(rs.getString("already_work"))>uF.parseToDouble(rs.getString("idealtime")))
//				{
//					strColour="red";
//				} else {
//					strColour="green";
//					
//				}
//				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
//				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
//				
//				String daySpanLbl =" d";
//				
//				if(rs.getString("taskstatus").equalsIgnoreCase("New Task")) {
//					alInner.add("<span style=\"color:red\">Planned</span>");
//				} else if(dtCurrentDate!=null && dtDeadlineDate!=null && dtCurrentDate.after(dtDeadlineDate)) {
//					
//					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
//						alInner.add("<span style=\"color:#5D862B\">Confirmed</span> (<b><span style=\"color:"+strColour+"\">" +uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b>"+daySpanLbl+")");
//					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/<b> <span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b>"+daySpanLbl+")");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
//						alInner.add("<span style=\"color:red\">Overdue </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b>"+daySpanLbl+")");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"color:"+strColour+"\">Working </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b>"+daySpanLbl+")");
//					} else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
//						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
//					} else {
//						alInner.add("<span style=\"color:"+strColour+"\">Working</span> (<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/<b> <span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b>"+daySpanLbl+")");
//					}
//					
//				} else if(rs.getInt("completed")>=100) {
//					
//					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
//						alInner.add("<span style=\"color:#5D862B\">Confirmed </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/<b><span style=\"color:"+strdaysColour+"\"> "+no_of_days+"</span></b>"+daySpanLbl+")");
//					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/<b> <span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b>"+daySpanLbl+")");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
//						alInner.add("<span style=\"color:red\">Overdue </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b>"+daySpanLbl+")");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"color:"+strColour+"\">Working </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b>"+daySpanLbl+")");
//					} else {
//						alInner.add("<span style=\"color:"+strColour+"\">Working </span>( <b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b>"+daySpanLbl+")");
//					}
//				} else {
//					if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
//						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
//					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>");
//					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
//						alInner.add("<span style=\"color:"+strColour+"\">Working </span>");
//					} else {
//						alInner.add("<span style=\"color:"+strColour+"\">Working </span>");
//					}
//				}
//				
//				
//				String isFinish = rs.getString("finish_task");
//				if (rs.getDouble("completed") >= 100 && isFinish.equals("n")) {
//					alInner.add("<input type='checkbox' value='"+ taskid + "' name='isFinish' />");
//				} else {
//					alInner.add("");
//				}
//				
//				
//				alInner.add(rs.getString("activity_name"));
//				alInner.add(rs.getString("priority"));
//				alInner.add(uF.showData(hmEmployee.get(rs.getString("emp_id")), ""));
//				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
//				alInner.add(rs.getString("idealtime"));
//				alInner.add(rs.getString("already_work"));
//				alInner.add(rs.getString("completed"));
//				
//				hmTasks.put(rs.getString("pro_id"), alInner);
//				
//				
//				strProjectIdOld = strProjectIdNew;
//			}
//			rs.close();
//			pst.close();
//
//			request.setAttribute("hmTasks", hmTasks);
//			
//			
//			pst = con.prepareStatement("select * from task_activity where activity_id in (select task_id from activity_info where emp_id = ?) order by activity_id, task_date desc, start_time desc");
//			pst.setInt(1, emp_id);
//			rs = pst.executeQuery();
//			
//			alInner = new ArrayList<String>();
//			
//			String strTaskIdNew = null;
//			String strTaskIdOld = null;
//			while(rs.next()) {
//				strTaskIdNew = rs.getString("activity_id"); 
//				if(strTaskIdNew!=null && !strTaskIdNew.equalsIgnoreCase(strTaskIdOld)) {
//					alInner = new ArrayList<String>();
//				}
//				
//				alInner.add(rs.getString("task_id"));
//				alInner.add(rs.getString("task_date"));
//				alInner.add(rs.getString("start_time"));
//				alInner.add(rs.getString("end_time"));
//				alInner.add(rs.getString("actual_hrs"));
//				
//				hmActivities.put(rs.getString("activity_id"), alInner);
//				
//				
//				strTaskIdOld = strTaskIdNew;
//			}
//			rs.close();
//			pst.close();
//
//			request.setAttribute("hmActivities", hmActivities);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}


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

	public int getTask_id() {
		return task_id;
	}

	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}

	public List<Integer> getActivity_index() {
		return activity_index;
	}

	public void setActivity_index(List<Integer> activity_index) {
		this.activity_index = activity_index;
	}

	public Map<Integer, List<String>> getAl() {
		return al;
	}

	public void setAl(Map<Integer, List<String>> al) {
		this.al = al;
	}

	public List<Integer> getIndex() {
		return index;
	}

	public void setIndex(List<Integer> index) {
		this.index = index;
	}

	public List<String> getProjectlist() {
		return projectlist;
	}

	public void setProjectlist(List<String> projectlist) {
		this.projectlist = projectlist;
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

	public List<Integer> getProjectidlist() {
		return projectidlist;
	}

	public void setProjectidlist(List<Integer> projectidlist) {
		this.projectidlist = projectidlist;
	}

	public Map getSession() {
		return session;
	}

	@Override
	public void setSession(Map arg0) {
		// TODO Auto-generated method stub
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

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public List<GetPriorityList> getPriorityList() {
		return priorityList;
	}

	public void setPriorityList(List<GetPriorityList> priorityList) {
		this.priorityList = priorityList;
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

}