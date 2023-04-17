package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TaskUpdateTime extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	private HttpServletRequest request;

	String type;
	String id;
	String taskId;
	String per;
	String activity;
	String strt_time;
	String end_time;
	String strTime;
	String workStatus;
	String strt_date;
	String strBillableYesNoT;
	String strBillableTime;
	String strTaskOnOffSiteT;
	String taskDescription;
	String fromPage;
	String btncomplete;
	 
	String time;
	HttpSession session;

	int proid;
	int pro_id;
	int task_id;
	int emp_id;
	int time_id;
	int timestatus;
	int extraActivity;

	CommonFunctions CF;
	UtilityFunctions uF = new UtilityFunctions();
	

	public String execute() throws Exception {
			
//			System.out.println("pro_id in time ===>> " + pro_id);
//			System.out.println("id in time ===>> " + id);
			
//			System.out.println("getBtncomplete ===>> " + getBtncomplete());
			session = request.getSession();
//			session.setAttribute("pro_id", pro_id);
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if (CF == null) return LOGIN;
			
			emp_id = uF.parseToInt((String) session.getAttribute(EMPID));

			task_id = uF.parseToInt(id);
//			System.out.println("type ===>> " + type);
			if (type.equals("start")) {
//				if ((String) session.getAttribute("MESSAGE") != null || (String) session.getAttribute("MSG") != null) {
				endTaskStarted();
//				}
				startTask();
				session.setAttribute("MSG", "Please end the exiting activity to start the new activity.");
				
			} else if (type.equals("end")) {
				endTask();
//				session.setAttribute("MESSAGE", null);
//				session.setAttribute("MSG", null);
			}

//			System.out.println("getFromPage() ===>> " + getFromPage());
			
			if(getFromPage() != null && getFromPage().equals("MyActivity")) {
				return "MASUCCESS";
			} else {
				return SUCCESS;
			}
	}
	

	public void endTaskStarted() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		int taskActivityId = 0;
//		int activityId = 0;
		int taskId = 0;
		String time = "";
		String per = "";
		try {
			con = db.makeConnection(con);
//			System.out.println("endTaskStarted =====>>>>>>>> inn ");
			
			
			pst = con.prepareStatement("select activity_id, task_id, start_time, end_time from task_activity where emp_id =? and end_time is null");
			pst.setInt(1, emp_id);
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				taskId = rs.getInt("activity_id");
				taskActivityId = rs.getInt("task_id");
			}
			rs.close();
			pst.close();
			
//			if (activity == null || activity.equals("null") || activity.equals("")) {
			if (taskId > 0) {
				pst = con.prepareStatement("UPDATE task_activity SET end_time =? WHERE task_id =?");
				pst.setTime(1, uF.getCurrentTime(CF.getStrTimeZone()));
				pst.setInt(2, taskActivityId);
//				System.out.println("pst ===>> " + pst);
				pst.executeUpdate();
				pst.close();
				
//				pst = con.prepareStatement("select task_id from activity_info where emp_id = ? and timestatus='y'");
//				pst.setInt(1, emp_id);
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					taskId = rs.getInt("task_id");
//				}
//				rs.close();
//				pst.close();
				// ======================================================

				pst = con.prepareStatement("UPDATE activity_info SET end_time =?, timestatus='n' WHERE task_id =? ");
				pst.setTime(1, uF.getCurrentTime(CF.getStrTimeZone()));
				pst.setInt(2, taskId);
				pst.executeUpdate();
				pst.close();

				// ======================================================
				pst = con.prepareStatement("select start_time, end_time from task_activity where task_id=?");
				pst.setInt(1, taskActivityId);
				rs = pst.executeQuery();
				while (rs.next()) {
					time = uF.getTimeDiffInHoursMins(rs.getTime("start_time").getTime(), rs.getTime("end_time").getTime());
				}
				rs.close();
				pst.close();

				// ======================================================
				pst = con.prepareStatement("select completed from activity_info where task_id=?");
				pst.setInt(1, taskId);
				rs = pst.executeQuery();
				while (rs.next()) {
					per = rs.getString("completed");
				}
				rs.close();
				pst.close();

				// ======================================================
//				pst = con.prepareStatement("UPDATE task_activity SET end_time=?,actual_hrs=?,task_status=?,activity=?,is_billable=true WHERE activity_id =? and emp_id=? and task_id=(select max(task_id) from task_activity where emp_id=? and activity_id=? )");
				pst = con.prepareStatement("UPDATE task_activity SET actual_hrs=?,task_status=?,activity=?,is_billable=true,billable_hrs=? WHERE task_id = ?");
//				pst.setTime(1, uF.getCurrentTime(CF.getStrTimeZone()));
				pst.setDouble(1, uF.parseToDouble(uF.getTotalTimeMinutes60To100(time)));
				pst.setDouble(2, uF.parseToDouble(per));
				pst.setString(3, "");
				pst.setDouble(4, uF.parseToDouble(uF.getTotalTimeMinutes60To100(time)));
//				pst.setInt(5, taskId);
//				pst.setInt(6, emp_id);
//				pst.setInt(7, emp_id);
				pst.setInt(5, taskActivityId);
				pst.executeUpdate();
				pst.close();

				// ======================================================
				insertintoActivityandProject(taskId+"", per, con);
//				pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity where activity_id=? group by emp_id");
//				pst.setInt(1, taskId);
////				pst.setInt(2, emp_id);
//				double stime = 0.0;
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					stime += rs.getDouble("actual_hrs");
//				}
//				rs.close();
//				pst.close();
//
//				// ======================================================
//				pst = con.prepareStatement("select count(distinct task_date) as actual_days from task_activity where activity_id=? group by emp_id");
//				pst.setInt(1, taskId);
//				pst.setInt(2, emp_id);
//				double dblActualDays = 0.0;
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					dblActualDays += rs.getDouble("actual_days");
//				}
//				rs.close();
//				pst.close();
//
//				// ======================================================
//				pst = con.prepareStatement("UPDATE activity_info SET already_work =?, already_work_days =?, completed=? WHERE task_id =?");
//				pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(stime)));
//				pst.setDouble(2, dblActualDays);
//				pst.setInt(3, uF.parseToInt(per));
//				pst.setInt(4, taskId);
//				pst.executeUpdate();
//				pst.close();

//				pst = con.prepareStatement("select sum(ta.actual_hrs) as actual_hrs, count(distinct ta.task_date) as actual_days, pmc.pro_id from task_activity ta, activity_info ai, projectmntnc pmc where pmc.pro_id = ai.pro_id and ai.task_id = ta.activity_id and pmc.approve_status = 'n' group by pmc.pro_id");
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					pst1 = con.prepareStatement("update projectmntnc SET already_work=? WHERE pro_id=?");
//					pst1.setDouble(1, uF.parseToDouble(uF.roundOffInTimeInHoursMins(uF.parseToDouble(rs.getString("actual_hrs")))));
//					pst1.setDouble(2, uF.parseToDouble(rs.getString("actual_days")));
//					pst1.setInt(2, uF.parseToInt(rs.getString("pro_id")));
//					pst1.executeUpdate();
//				}
//				rs.close();
//				pst.close();

			} else {
				pst = con.prepareStatement("UPDATE task_activity SET end_time =? WHERE task_id =?");
				pst.setTime(1, uF.getCurrentTime(CF.getStrTimeZone()));
				pst.setInt(2, taskActivityId);
				pst.executeUpdate();
				pst.close();

				// ======================================================
				String total_time = "";
				pst = con.prepareStatement("select start_time,end_time,is_billable from task_activity where task_id =?");
				pst.setInt(1, taskActivityId);
				rs = pst.executeQuery();
				while (rs.next()) {
//					if (rs.getBoolean("is_billable")) {
						total_time = uF.getTimeDiffInHoursMins(rs.getTime("start_time").getTime(), rs.getTime("end_time").getTime());
//					}
				}
				rs.close();
				pst.close();

				// ======================================================
				pst = con.prepareStatement("UPDATE task_activity SET actual_hrs =? WHERE task_id =?");
				pst.setDouble(1, uF.parseToDouble(uF.getTotalTimeMinutes60To100(total_time)));
				pst.setInt(2, taskActivityId);
				pst.executeUpdate();
				pst.close();

			}
			// ======================================================
//			session.setAttribute("MESSAGE", null);
//			session.setAttribute("MSG", null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

//	public void checkTaskStatus() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rs = null;
//		try {
//
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select count(task_id) as count from task_activity where emp_id =? and end_time is null");
//			pst.setInt(1, emp_id);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				extraActivity = rs.getInt("count");
//			}
//			rs.close();
//			pst.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}

	public void startTask() {
		Connection con = null;
		PreparedStatement pst = null, pst1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if (getStrt_time() != null && getEnd_time() != null) {

				String orgID = CF.getEmpOrgId(con, uF, emp_id+"");
				String []arr = CF.getPayCycleFromDate(con, getStrt_date(), CF.getStrTimeZone(), CF, orgID);
//				System.out.println("else===>");
				String clentID = CF.getClientIdByProjectTaskId(con, uF, task_id+"", emp_id+"");
				String taskName = CF.getProjectTaskNameByTaskId(con, uF, task_id+"");
				String time = uF.getTimeDiffInHoursMins(uF.getTimeFormat(getStrt_time(), TIME_FORMAT).getTime(), uF.getTimeFormat(getEnd_time(), TIME_FORMAT).getTime());
				
//				long ST_TIME = uF.getTimeFormat(uF.getDateFormat(getStrt_date(), DATE_FORMAT, DBDATE)+" "+getStrt_time().trim()+":00", DBTIMESTAMP).getTime();
//				long END_TIME = uF.getTimeFormat(uF.getDateFormat(getStrt_date(), DATE_FORMAT, DBDATE)+" "+getEnd_time().trim()+":00", DBTIMESTAMP).getTime();
//				time = uF.getTimeDiffInHoursMins(ST_TIME, END_TIME);
				
				pst = con.prepareStatement("insert into task_activity (activity_id, activity, task_date, emp_id, actual_hrs, start_time, end_time, " +
					"total_time, is_billable, activity_description, task_location,generated_date,timesheet_paycycle,is_approved, client_id, " +
					"billable_hrs,task_status) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
				pst.setInt(1, task_id);
				if(pro_id == 0) {
					pst.setString(2, taskName);
				} else {
					pst.setString(2, "");
				}
				pst.setDate(3, uF.getDateFormat(getStrt_date(), DATE_FORMAT));
				pst.setInt(4, emp_id);
				if(uF.parseToDouble(getStrTime()) > 0) {
					pst.setDouble(5, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrTime())));
				} else {
					pst.setDouble(5, uF.parseToDouble(uF.getTotalTimeMinutes60To100(time)));
				}
				pst.setTime(6, uF.getTimeFormat(getStrt_time(), TIME_FORMAT));
				pst.setTime(7, uF.getTimeFormat(getEnd_time(), TIME_FORMAT));
				if(uF.parseToDouble(getStrTime()) > 0) {
					pst.setDouble(8, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrTime())));
				} else {
					pst.setDouble(8, uF.parseToDouble(uF.getTotalTimeMinutes60To100(time)));
				}
				pst.setBoolean(9, uF.parseToBoolean(getStrBillableYesNoT()));
				pst.setString(10, ((getTaskDescription() != null) ? getTaskDescription() : ""));
				pst.setString(11,((getStrTaskOnOffSiteT().equalsIgnoreCase("1")) ? "ONS": "OFS"));
//				pst.setString(11, ((ArrayUtils.contains(getStrTaskOnOffSite(), getStrTaskOnOffSiteT()[i]) >= 0) ? "ONS" : "OFS"));
				pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(13, uF.parseToInt(arr[2]));
				pst.setInt(14, 0);
				pst.setInt(15, uF.parseToInt(clentID));
				if(uF.parseToDouble(getStrBillableTime()) > 0) {
					pst.setDouble(16, uF.parseToBoolean(getStrBillableYesNoT()) ? uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrBillableTime())) : 0.0d);
				} else {
					pst.setDouble(16, uF.parseToBoolean(getStrBillableYesNoT()) ? uF.parseToDouble(uF.getTotalTimeMinutes60To100(time)) : 0.0d);
				}
				pst.setDouble(17, uF.parseToDouble(getWorkStatus()));
//				System.out.println("getStrt_time() - getEnd_time() - pst===>"+pst);
				pst.execute();
				pst.close();

				session.setAttribute(MESSAGE, SUCCESSM + "Your task has been saved successfully." + END);

				insertintoActivityandProject(task_id+"", getWorkStatus(), con);
//				System.out.println("pst=====>" + pst);
			} else if (getStrt_time() != null) {
				String orgID = CF.getEmpOrgId(con, uF, emp_id+"");
				String []arr = CF.getPayCycleFromDate(con, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT) , CF.getStrTimeZone(), CF, orgID);
//				System.out.println("else===>");
				String clentID = CF.getClientIdByProjectTaskId(con, uF, task_id+"", emp_id+"");
				String taskName = CF.getProjectTaskNameByTaskId(con, uF, task_id+"");
				pst = con.prepareStatement("UPDATE activity_info SET taskstatus='Pending', start_time =?, timestatus='y' WHERE task_id =?");
				pst.setTime(1, uF.getTimeFormat(getStrt_time(), TIME_FORMAT));
				pst.setInt(2, task_id);
				pst.executeUpdate();
				pst.close();
				
				pst1 = con.prepareStatement("insert into task_activity (activity_id,activity,task_date,start_time,total_time,emp_id,task_status,actual_hrs,timesheet_paycycle,client_id) values (?,?,?,?, ?,?,?,?,0, ?,?)");
				pst1.setInt(1, task_id);
				if(pro_id == 0) {
					pst1.setString(2, taskName);
				} else {
					pst1.setString(2, "");
				}
				pst1.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst1.setTime(4, uF.getTimeFormat(getStrt_time(), TIME_FORMAT));
				pst1.setDouble(5, 0);
				pst1.setInt(6, emp_id);
				pst1.setDouble(7, 0);
				pst1.setInt(8, uF.parseToInt(arr[2]));
				pst1.setInt(9, uF.parseToInt(clentID));
				pst1.executeUpdate();
//				System.out.println("pst getStrt_time() ===>> " + pst);
				pst1.close();
				
			} else {

				String orgID = CF.getEmpOrgId(con, uF, emp_id+"");
				String []arr = CF.getPayCycleFromDate(con, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT) , CF.getStrTimeZone(), CF, orgID);
//				System.out.println("else===>");
				String clentID = CF.getClientIdByProjectTaskId(con, uF, task_id+"", emp_id+"");
				String taskName = CF.getProjectTaskNameByTaskId(con, uF, task_id+"");
				pst = con.prepareStatement("UPDATE activity_info SET taskstatus='Pending', start_time =?, timestatus='y' WHERE task_id =?");
				pst.setTime(1, uF.getCurrentTime(CF.getStrTimeZone()));
				pst.setInt(2, task_id);
				pst.executeUpdate();

				pst1 = con.prepareStatement("insert into task_activity (activity_id,activity,task_date,start_time,total_time,emp_id,task_status,actual_hrs,timesheet_paycycle,client_id) values (?,?,?,?, ?,?,?,0, ?,?)");
				pst1.setInt(1, task_id);
				if(pro_id == 0) {
					pst1.setString(2, taskName);
				} else {
					pst1.setString(2, "");
				}
				pst1.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst1.setTime(4, uF.getCurrentTime(CF.getStrTimeZone()));
				pst1.setDouble(5, 0);
				pst1.setInt(6, emp_id);
				pst1.setDouble(7, 0);
				pst1.setInt(8, uF.parseToInt(arr[2]));
				pst1.setInt(9, uF.parseToInt(clentID));
				pst1.executeUpdate();
//				System.out.println("pst ===>> " + pst);
				pst1.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	
	private void insertintoActivityandProject(String activityID, String completedPercent, Connection con) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			if (uF.parseToInt(activityID) > 0) {
				StringBuilder sumActivityQuery = new StringBuilder();

				sumActivityQuery.append("select sum(actual_hrs) as actual_hrs, count (distinct(task_date)) as actual_days from task_activity where " +
						"activity_id=? group by emp_id ");
				pst = con.prepareStatement(sumActivityQuery.toString());
//				pst.setInt(1, emp_id);
				pst.setInt(1, uF.parseToInt(activityID));
				rst = pst.executeQuery();
				double actual_hrs = 0.0d;
				double actual_days = 0.0d;
				while (rst.next()) {
					actual_hrs += uF.parseToDouble(rst.getString("actual_hrs"));
					actual_days += uF.parseToDouble(rst.getString("actual_days"));
				}
				rst.close();
				pst.close();

				pst = con.prepareStatement("select task_id,pro_id from activity_info order by task_id");
				rst = pst.executeQuery();
				Map<String, String> hmActivityProID = new HashMap<String, String>();
				while (rst.next()) {
					hmActivityProID.put(rst.getString("task_id"), rst.getString("pro_id"));
				}
				rst.close();
				pst.close();
				
				String pro_id = hmActivityProID.get(activityID);

				pst = con.prepareStatement("update activity_info set already_work=?,already_work_days=?,completed=?,approve_status=?,end_date=? where task_id=?");
				pst.setDouble(1, actual_hrs);
				pst.setDouble(2, actual_days);
				if(getBtncomplete() != null) {
					pst.setDouble(3, 100);
					pst.setString(4, "approved");
					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				} else {
					pst.setDouble(3, uF.parseToDouble(completedPercent));
					pst.setString(4, "n");
					pst.setDate(5, null);
				}
				pst.setInt(6, uF.parseToInt(activityID));
				pst.execute();
				pst.close();

				
				int parentTaskId = 0;
				pst = con.prepareStatement("select parent_task_id from activity_info where task_id = ? and parent_task_id != 0");
				pst.setInt(1, uF.parseToInt(activityID));
				rst = pst.executeQuery();
				while(rst.next()) {
					parentTaskId = rst.getInt("parent_task_id");
				}
				rst.close();
				pst.close();
				
				double dblAllCompleted = 0.0d;
				int subTaskCnt = 0;
				String taskActualHrs = null;
				String taskActualDays = null;
				pst = con.prepareStatement("select sum(completed) as completed, count(task_id) as count, sum(already_work) as already_work, " +
					"sum(already_work_days) as already_work_days from activity_info where parent_task_id = ?");
				pst.setInt(1, parentTaskId);
//				System.out.println("pst ==>> " + pst);
				rst = pst.executeQuery();
				while(rst.next()) {
					dblAllCompleted = rst.getDouble("completed");
					subTaskCnt = rst.getInt("count");
					taskActualHrs = rst.getString("already_work");
					taskActualDays = rst.getString("already_work_days");
				}
				rst.close();
				pst.close();
				
				double avgComplted = 0.0d;
				if(dblAllCompleted > 0 && subTaskCnt > 0) {
					avgComplted = dblAllCompleted / subTaskCnt;
				}
				
//				if(avgComplted > 0) {
					pst = con.prepareStatement("update activity_info set completed=?, already_work=?, already_work_days=? where task_id=?");
					pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(avgComplted)));
					pst.setDouble(2, uF.parseToDouble(taskActualHrs));
					pst.setDouble(3, uF.parseToDouble(taskActualDays));
					pst.setInt(4, parentTaskId);
					pst.execute();
					pst.close();
//				}
				
				pst = con.prepareStatement("select sum(already_work)as already_work, sum(already_work_days)as already_work_day, " +
					"sum(completed)/count(task_id) as avrg from activity_info where pro_id=? and parent_task_id = 0");
				pst.setInt(1, uF.parseToInt(pro_id));
				rst = pst.executeQuery();
				String project_hrs = null;
				String project_days = null;
				String projectCompletePercent = null;
				while (rst.next()) {
					project_hrs = rst.getString("already_work");
					project_days = rst.getString("already_work_day");
					projectCompletePercent = uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rst.getString("avrg")));
				}
				rst.close();
				pst.close();

				pst = con.prepareStatement("update projectmntnc set already_work=?, already_work_days=?, completed=? where pro_id=? ");
				pst.setDouble(1, uF.parseToDouble(project_hrs));
				pst.setDouble(2, uF.parseToDouble(project_days));
				pst.setDouble(3, uF.parseToDouble(projectCompletePercent));
				pst.setInt(4, uF.parseToInt(pro_id));
				pst.execute();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
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
	}
	
	
	public void endTask() {
		Connection con = null;
		PreparedStatement pst = null, pst1 = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select task_date, start_time from task_activity WHERE task_id=?");
			pst.setInt(1, uF.parseToInt(getTaskId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				String currDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
				
				long ST_TIME = uF.getTimeFormat(rs.getString("task_date")+" "+rs.getString("start_time"), DBTIMESTAMP).getTime();
				long END_TIME = uF.getTimeFormat(currDate+" "+uF.getCurrentTime(CF.getStrTimeZone()), DBTIMESTAMP).getTime();
				time = uF.getTimeDiffInHoursMins(ST_TIME, END_TIME);
//				System.out.println("time ===>> " + time);
			}
			rs.close();
			pst.close();
			
//			System.out.println("activity ===>> " + activity);
			
			pst1 = con.prepareStatement("UPDATE task_activity SET end_time=?,actual_hrs=?,task_status=?,activity=?,is_billable=?,task_location=?," +
			"activity_description=?,billable_hrs=?,generated_date=?,is_approved=?,total_time=? WHERE task_id=?");
			pst1.setTime(1, uF.getCurrentTime(CF.getStrTimeZone()));
			pst1.setDouble(2, uF.parseToDouble(uF.getTotalTimeMinutes60To100(time)));
			pst1.setDouble(3, uF.parseToDouble(per));
			pst1.setString(4, activity);
			pst1.setBoolean(5, uF.parseToBoolean(getStrBillableYesNoT()));
			pst1.setString(6,((getStrTaskOnOffSiteT().equalsIgnoreCase("1")) ? "ONS": "OFS"));
			pst1.setString(7, ((getTaskDescription() != null) ? getTaskDescription() : ""));
			pst1.setDouble(8, uF.parseToBoolean(getStrBillableYesNoT()) ? uF.parseToDouble(uF.getTotalTimeMinutes60To100(time)) : 0.0d);
			pst1.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
			pst1.setInt(10, 0);
			pst1.setDouble(11, uF.parseToDouble(uF.getTotalTimeMinutes60To100(time)));
			pst1.setInt(12, uF.parseToInt(getTaskId()));
			pst1.executeUpdate();
			pst1.close();
			
			pst = con.prepareStatement("UPDATE activity_info SET end_time =?, timestatus='n' WHERE task_id =?");
			pst.setTime(1, uF.getCurrentTime(CF.getStrTimeZone()));
			pst.setInt(2, task_id);
			pst.executeUpdate();
			pst.close();
			
			
			insertintoActivityandProject(task_id+"", per, con);

			if (uF.parseToDouble(per) >= 100) {
				pst = con.prepareStatement("UPDATE activity_info SET finish_task='y',end_date=? WHERE task_id =?");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(2, task_id);
				pst.executeUpdate();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public int getTask_id() {
		return task_id;
	}

	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	private HttpServletResponse response;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getTime_id() {
		return time_id;
	}

	public void setTime_id(int time_id) {
		this.time_id = time_id;
	}

	public int getPro_id() {
		return pro_id;
	}

	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}

	public int getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(int emp_id) {
		this.emp_id = emp_id;
	}

	public String getPer() {
		return per;
	}

	public void setPer(String per) {
		this.per = per;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStrt_time() {
		return strt_time;
	}

	public void setStrt_time(String strt_time) {
		this.strt_time = strt_time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	public String getStrTime() {
		return strTime;
	}

	public void setStrTime(String strTime) {
		this.strTime = strTime;
	}

	public String getStrBillableTime() {
		return strBillableTime;
	}

	public void setStrBillableTime(String strBillableTime) {
		this.strBillableTime = strBillableTime;
	}

	public String getStrBillableYesNoT() {
		return strBillableYesNoT;
	}

	public void setStrBillableYesNoT(String strBillableYesNoT) {
		this.strBillableYesNoT = strBillableYesNoT;
	}

	public String getStrTaskOnOffSiteT() {
		return strTaskOnOffSiteT;
	}

	public void setStrTaskOnOffSiteT(String strTaskOnOffSiteT) {
		this.strTaskOnOffSiteT = strTaskOnOffSiteT;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getStrt_date() {
		return strt_date;
	}

	public void setStrt_date(String strt_date) {
		this.strt_date = strt_date;
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


	public String getBtncomplete() {
		return btncomplete;
	}


	public void setBtncomplete(String btncomplete) {
		this.btncomplete = btncomplete;
	}
	
}