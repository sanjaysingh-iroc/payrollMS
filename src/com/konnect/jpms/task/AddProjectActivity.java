package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillTask;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class AddProjectActivity extends ActionSupport implements
		ServletRequestAware, ServletResponseAware, SessionAware, IStatements {
	private static final long serialVersionUID = 1L;

	HttpSession session;
	String extraTask;
	String etime;
	String stime;
	String task_id;
	String comment;
	String submit;
	String task_date;
	String[] cb;
	String ExtraActivityID;
	String ExtraActivityName;
	String p_id;
	String t_id;
	String projectID;
	String projectName;
	String activityId;
	String service_id;
	String strStartTime;
	String strEndTime;

	int emp_id;
	int timestatus;
	int extraActivity;

	List<FillProjectList> projectdetailslist;
	List<FillActivityList> activitydetailslist;
	List<ExtraActivityList> extraActivityList;
	List<FillServices> serviceList;
	List<FillClients> clientlist;
	List<FillEmployee> empNamesList;
	List<FillTask> tasklist;
	String clientId;
	String clientName;
	String[] empId;
	CommonFunctions CF;
	private HttpServletRequest request;
	UtilityFunctions uF = new UtilityFunctions();

	String frmDate;
	String toDate;
	String strClient;
	String strProject;
	String strTask;
	String frmTime;
	String toTime;
	String totalHours;

	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		extraActivityList = new ExtraActivityList().fillExtraActivity();
		emp_id = uF.parseToInt((String) session.getAttribute(EMPID));
		projectdetailslist = new FillProjectList(request)
				.fillProjectDetailsByEmp(emp_id, false, 0);
		// activitydetailslist=new
		// FillActivityList(request).fillActivityDetailsByProject(uF.parseToInt(p_id));
		serviceList = new FillServices(request).fillServices((String) session.getAttribute(EMPID));
		clientlist = new FillClients(request).fillClients(uF.parseToInt((String) session.getAttribute(EMPID)));
		empNamesList = new FillEmployee(request).fillEmployeeName(null, null, session);
		tasklist = new FillTask(request).fillTask(emp_id);

		if (getSubmit() != null && getSubmit().equalsIgnoreCase(("Start"))) {

			if ((String) session.getAttribute("MSG") != null || (String) session.getAttribute("MESSAGE") != null) {
				endTaskStarted();
			}
			startNewActivity();
			session.setAttribute("MESSAGE", "Please end the exiting activity to start the new activity.");

		}
		if (getTask_id() != null) {
			endExtraActivity();
			session.setAttribute("MESSAGE", null);
			session.setAttribute("MSG", null);
		}
		if (cb != null) {
			sendToApprove();
		}

		if (getTotalHours() != null) {
			insertProjectData();
		}
		getData();

		return SUCCESS;
	}

	public void getData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);

			setFrmDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())
					+ "", DBDATE, DATE_FORMAT));
			setToDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())
					+ "", DBDATE, DATE_FORMAT));

			pst = con
					.prepareStatement("select * from activity_info ai, projectmntnc pmc where pmc.pro_id = ai.pro_id and ai.start_date<=? and ai.deadline>=? order by ai.start_date");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			if (rs.next()) {
				setStrClient(rs.getString("client_id"));
				setStrProject(rs.getString("pro_id"));
				setStrTask(rs.getString("task_id"));
			}
			rs.close();
			pst.close();

			pst = con
					.prepareStatement("select * from attendance_details where emp_id = ? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') =?");
			pst.setInt(1, getEmp_id());
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();

			while (rs.next()) {
				if (rs.getString("in_out").equalsIgnoreCase("IN")) {
					setFrmTime(uF.getDateFormat(
							rs.getString("in_out_timestamp"), DBTIMESTAMP,
							CF.getStrReportTimeFormat()));
				} else if (rs.getString("in_out").equalsIgnoreCase("OUT")) {
					setToTime(uF.getDateFormat(
							rs.getString("in_out_timestamp"), DBTIMESTAMP,
							CF.getStrReportTimeFormat()));
				}
			}
			rs.close();
			pst.close();

			if (getFrmTime() == null || getToTime() == null) {
				pst = con
						.prepareStatement("select * from roster_details where emp_id=? and _date=? ");
				pst.setInt(1, getEmp_id());
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				rs = pst.executeQuery();

				System.out.println("pst==>" + pst);

				while (rs.next()) {
					if (getFrmTime() == null) {
						setFrmTime(uF.getDateFormat(rs.getString("_from"),
								DBTIME, CF.getStrReportTimeFormat()));
					}
					if (getToTime() == null) {
						setToTime(uF.getDateFormat(rs.getString("_to"), DBTIME,
								CF.getStrReportTimeFormat()));
					}
				}
				rs.close();
				pst.close();
			}

			setTotalHours(uF.getTimeDiffInHoursMins(
					uF.getTimeFormat(getFrmTime(), DBTIME).getTime(), uF
							.getTimeFormat(getToTime(), DBTIME).getTime()));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void insertProjectData() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);

			int nDays = uF.parseToInt(uF.dateDifference(getFrmDate(),
					DATE_FORMAT, getToDate(), DATE_FORMAT));
			java.util.Date dt = null;
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(getFrmDate(),
					DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(
					getFrmDate(), DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getFrmDate(),
					DATE_FORMAT, "yyyy")));

			for (int i = 0; i < nDays; i++) {

				dt = new java.util.Date(cal.get(Calendar.YEAR) - 1900,
						cal.get(Calendar.MONTH), cal.get(Calendar.DATE),
						cal.get(Calendar.HOUR_OF_DAY),
						cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
				long currDate = dt.getTime();

				pst = con
						.prepareStatement("insert into task_activity (activity_id, task_date, emp_id, actual_hrs, start_time, end_time, total_time, is_billable) values (?,?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getStrTask()));
				pst.setDate(2, new java.sql.Date(currDate));
				pst.setInt(3, emp_id);
				pst.setDouble(4, uF.parseToDouble(uF.getTimeDiffInHoursMins(uF
						.getTimeFormat(getFrmTime(), DBTIME).getTime(), uF
						.getTimeFormat(getToTime(), DBTIME).getTime())));
				pst.setTime(5, uF.getTimeFormat(getFrmTime(), DBTIME));
				pst.setTime(6, uF.getTimeFormat(getToTime(), DBTIME));
				pst.setDouble(7, uF.parseToDouble(uF.getTimeDiffInHoursMins(uF
						.getTimeFormat(getFrmTime(), DBTIME).getTime(), uF
						.getTimeFormat(getToTime(), DBTIME).getTime())));
				pst.setBoolean(8, true);
				pst.execute();
				pst.close();

				cal.add(Calendar.DATE, 1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void sendToApprove() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			for (int i = 0; i < cb.length; i++) {
				pst = con
						.prepareStatement("UPDATE task_activity SET sent ='y',task_date=? WHERE emp_id =? and sent='n' and task_id=?");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(2, emp_id);
				pst.setInt(3, uF.parseToInt(cb[i]));
				pst.executeUpdate();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void endTaskStarted() {
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		int t_id = 0;
		int a_id = 0;
		String time = "";
		String per = "";
		boolean is_billable = false;
		String activity = "";
		try {
			con = db.makeConnection(con);
			pst = con
					.prepareStatement("select activity,task_id from task_activity where emp_id =? and end_time is null");
			pst.setInt(1, emp_id);
			rs = pst.executeQuery();
			System.out.println("pst in automatic stop 1===>" + pst);
			while (rs.next()) {
				activity = rs.getString("activity");
				t_id = rs.getInt("task_id");
			}
			rs.close();
			pst.close();

			if (activity == null)

			{
				// ======================================================
				/*
				 * pst = con.prepareStatement(
				 * "select task_id from task_activity where emp_id =? and end_time is null"
				 * ); pst.setInt(1, emp_id); rs = pst.executeQuery();
				 * while(rs.next()) { t_id=rs.getInt("task_id"); }
				 */
				// ======================================================
				pst = con
						.prepareStatement("select task_id from activity_info where emp_id = ? and timestatus='y'");
				pst.setInt(1, emp_id);
				System.out.println("pst in automatic stop===> 2" + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					a_id = rs.getInt("task_id");
				}
				rs.close();
				pst.close();
				// ======================================================

				pst = con
						.prepareStatement("UPDATE activity_info SET end_time =?,timestatus='n' WHERE task_id =? ");
				pst.setTime(1, uF.getCurrentTime(CF.getStrTimeZone()));
				pst.setInt(2, a_id);
				System.out.println("pst in automatic stop===> 3" + pst);
				pst.executeUpdate();
				pst.close();

				// ======================================================
				pst = con
						.prepareStatement("select start_time,end_time from activity_info where task_id=?");
				pst.setInt(1, a_id);
				System.out.println("pst in automatic stop===> 4" + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					System.out.println("rs.getTime()===> 4"
							+ rs.getTime("start_time"));
					System.out.println("rs.getTime(end_time)===> 4"
							+ rs.getTime("end_time").getTime());
					time = uF.getTimeDiffInHoursMins(rs.getTime("start_time")
							.getTime(), rs.getTime("end_time").getTime());
				}
				rs.close();
				pst.close();
				System.out.println("time in automatic stop===> 5" + time);
				// ======================================================
				pst = con
						.prepareStatement("select completed from activity_info where task_id=?");
				pst.setInt(1, a_id);
				rs = pst.executeQuery();
				while (rs.next()) {
					per = rs.getString("completed");
				}
				rs.close();
				pst.close();

				// ======================================================
				pst = con
						.prepareStatement("UPDATE task_activity SET end_time=?,actual_hrs=?,task_status=?,activity=?,is_billable=true WHERE activity_id =? and emp_id=? and task_id=(select max(task_id) from task_activity where emp_id=? and activity_id=? )");
				pst.setTime(1, uF.getCurrentTime(CF.getStrTimeZone()));
				pst.setDouble(2, uF.parseToDouble(time));
				pst.setDouble(3, uF.parseToDouble(per));
				pst.setString(4, null);
				pst.setInt(5, a_id);
				pst.setInt(6, emp_id);
				pst.setInt(7, emp_id);
				pst.setInt(8, a_id);
				pst.executeUpdate();
				pst.close();

				// ======================================================
				pst = con
						.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity where activity_id=? and emp_id=?");
				pst.setInt(1, a_id);
				pst.setInt(2, emp_id);
				double stime = 0.0;
				rs = pst.executeQuery();

				while (rs.next()) {
					stime = rs.getDouble("actual_hrs");
				}
				rs.close();
				pst.close();

				// ======================================================
				pst = con
						.prepareStatement("UPDATE activity_info SET already_work =?,completed=? WHERE task_id =?");
				pst.setDouble(1, stime);
				pst.setInt(2, uF.parseToInt(per));
				pst.setInt(3, a_id);
				pst.executeUpdate();
				pst.close();

				// ======================================================
			} else {
				pst = con
						.prepareStatement("UPDATE task_activity SET end_time =? WHERE task_id =?");
				pst.setTime(1, uF.getCurrentTime(CF.getStrTimeZone()));
				pst.setInt(2, t_id);
				pst.executeUpdate();
				pst.close();

				// ======================================================
				String total_time = "";
				pst = con
						.prepareStatement("select start_time,end_time,is_billable from task_activity where task_id =?");
				pst.setInt(1, t_id);
				rs = pst.executeQuery();
				while (rs.next()) {
					if (rs.getBoolean("is_billable")) {
						total_time = uF.getTimeDiffInHoursMins(
								rs.getTime("start_time").getTime(),
								rs.getTime("end_time").getTime());
					}
				}
				rs.close();
				pst.close();

				// ======================================================
				pst = con
						.prepareStatement("UPDATE task_activity SET actual_hrs =? WHERE task_id =?");
				pst.setDouble(1, uF.parseToDouble(total_time));
				pst.setInt(2, t_id);
				pst.executeUpdate();
				pst.close();

			}
			session.setAttribute("MESSAGE", null);
			session.setAttribute("MSG", null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void checkTaskStatus() {
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		try {
			con = db.makeConnection(con);
			pst = con
					.prepareStatement("select count(task_id) from task_activity where emp_id =? and end_time is null");
			pst.setInt(1, emp_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				extraActivity = rs.getInt(1);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select count(task_id) from activity_info where emp_id = ? and timestatus='y'");
			pst.setInt(1, emp_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				timestatus = rs.getInt(1);
			}
			rs.close();
			pst.close();
			System.out.println("time status" + timestatus);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void endExtraActivity() {
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		try {
			con = db.makeConnection(con);
			pst = con
					.prepareStatement("UPDATE task_activity SET end_time =? WHERE task_id =?");
			pst.setTime(1, uF.getCurrentTime(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt(task_id));
			pst.executeUpdate();
			pst.close();

			String total_time = "";
			pst = con
					.prepareStatement("select start_time,end_time,is_billable from task_activity where task_id =?");
			pst.setInt(1, uF.parseToInt(task_id));
			rs = pst.executeQuery();
			while (rs.next()) {

				total_time = uF.getTimeDiffInHoursMins(rs.getTime("start_time")
						.getTime(), rs.getTime("end_time").getTime());

			}
			rs.close();
			pst.close();

			pst = con
					.prepareStatement("UPDATE task_activity SET actual_hrs =? WHERE task_id =?");
			pst.setDouble(1, uF.parseToDouble(total_time));
			pst.setInt(2, uF.parseToInt(task_id));
			pst.executeUpdate();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void startNewActivity() {
		boolean isBillable = false;
		if (uF.parseToInt(getActivityId()) != 0)
			isBillable = true;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		String teamIds = "";
		try {
			con = db.makeConnection(con);

			for (int i = 0; getEmpId() != null && i < getEmpId().length; i++) {

				teamIds += getEmpId()[i] + ",";

			}

			if (getStrStartTime() != null && getStrEndTime() != null
					&& getStrStartTime().length() > 0
					&& getStrEndTime().length() > 0) {

				String total_time = uF.getTimeDiffInHoursMins(
						uF.getTimeFormat(getStrStartTime(), TIME_FORMAT)
								.getTime(),
						uF.getTimeFormat(getStrEndTime(), TIME_FORMAT)
								.getTime());

				pst = con
						.prepareStatement("insert into task_activity (task_date, start_time, end_time, emp_id,activity,_comment,sent,is_billable, service_id, actual_hrs,client_id,team_ids,is_manual) values(?,?,?,?,?,?,'n',?,?,?,?,?,?)");

				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setTime(2, uF.getTimeFormat(getStrStartTime(), TIME_FORMAT));
				pst.setTime(3, uF.getTimeFormat(getStrEndTime(), TIME_FORMAT));
				pst.setInt(4, getEmp_id());
				pst.setString(5, getExtraTask());
				pst.setString(6, getComment());
				pst.setBoolean(7, isBillable);
				pst.setInt(8, uF.parseToInt(getService_id()));
				pst.setDouble(9, uF.parseToDouble(total_time));
				pst.setInt(10, uF.parseToInt(getClientId()));
				pst.setString(11, teamIds);
				pst.setBoolean(12, true);
				int a = pst.executeUpdate();
				pst.close();
			} else {
				pst = con
						.prepareStatement("insert into task_activity(task_date, start_time,emp_id,activity,_comment,sent,is_billable, service_id,client_id,team_ids) values(?,?,?,?,?,'n',?,?,?,?)");

				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setTime(2, uF.getCurrentTime(CF.getStrTimeZone()));

				pst.setInt(3, getEmp_id());
				pst.setString(4, getExtraTask());
				pst.setString(5, getComment());
				pst.setBoolean(6, isBillable);
				pst.setInt(7, uF.parseToInt(getService_id()));
				pst.setInt(8, uF.parseToInt(getClientId()));
				pst.setString(9, teamIds);
				pst.executeUpdate();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public int getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(int emp_id) {
		this.emp_id = emp_id;
	}

	public String[] getCb() {
		return cb;
	}

	public void setCb(String[] cb) {
		this.cb = cb;
	}

	public String getStime() {
		return stime;
	}

	public void setStime(String stime) {
		this.stime = stime;
	}

	public String getEtime() {
		return etime;
	}

	public void setEtime(String etime) {
		this.etime = etime;
	}

	public List<FillProjectList> getProjectdetailslist() {
		return projectdetailslist;
	}

	public void setProjectdetailslist(List<FillProjectList> projectdetailslist) {
		this.projectdetailslist = projectdetailslist;
	}

	public List<FillActivityList> getActivitydetailslist() {
		return activitydetailslist;
	}

	public void setActivitydetailslist(
			List<FillActivityList> activitydetailslist) {
		this.activitydetailslist = activitydetailslist;
	}

	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}

	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}

	public String[] getEmpId() {
		return empId;
	}

	public void setEmpId(String[] empId) {
		this.empId = empId;
	}

	public List<FillClients> getClientlist() {
		return clientlist;
	}

	public void setClientlist(List<FillClients> clientlist) {
		this.clientlist = clientlist;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getP_id() {
		return p_id;
	}

	public void setP_id(String p_id) {
		this.p_id = p_id;
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

	public String getExtraTask() {
		return extraTask;
	}

	public void setExtraTask(String extraTask) {
		this.extraTask = extraTask;
	}

	public List<ExtraActivityList> getExtraActivityList() {
		return extraActivityList;
	}

	public void setExtraActivityList(List<ExtraActivityList> extraActivityList) {
		this.extraActivityList = extraActivityList;
	}

	public String getExtraActivityID() {
		return ExtraActivityID;
	}

	public void setExtraActivityID(String extraActivityID) {
		ExtraActivityID = extraActivityID;
	}

	public String getExtraActivityName() {
		return ExtraActivityName;
	}

	public void setExtraActivityName(String extraActivityName) {
		ExtraActivityName = extraActivityName;
	}

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTask_date() {
		return task_date;
	}

	public void setTask_date(String task_date) {
		this.task_date = task_date;
	}

	@Override
	public void setSession(Map arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getStrStartTime() {
		return strStartTime;
	}

	public void setStrStartTime(String strStartTime) {
		this.strStartTime = strStartTime;
	}

	public String getStrEndTime() {
		return strEndTime;
	}

	public void setStrEndTime(String strEndTime) {
		this.strEndTime = strEndTime;
	}

	public List<FillTask> getTasklist() {
		return tasklist;
	}

	public void setTasklist(List<FillTask> tasklist) {
		this.tasklist = tasklist;
	}

	public String getFrmDate() {
		return frmDate;
	}

	public void setFrmDate(String frmDate) {
		this.frmDate = frmDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getStrClient() {
		return strClient;
	}

	public void setStrClient(String strClient) {
		this.strClient = strClient;
	}

	public String getStrProject() {
		return strProject;
	}

	public void setStrProject(String strProject) {
		this.strProject = strProject;
	}

	public String getStrTask() {
		return strTask;
	}

	public void setStrTask(String strTask) {
		this.strTask = strTask;
	}

	public String getFrmTime() {
		return frmTime;
	}

	public void setFrmTime(String frmTime) {
		this.frmTime = frmTime;
	}

	public String getToTime() {
		return toTime;
	}

	public void setToTime(String toTime) {
		this.toTime = toTime;
	}

	public String getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(String totalHours) {
		this.totalHours = totalHours;
	}
}
