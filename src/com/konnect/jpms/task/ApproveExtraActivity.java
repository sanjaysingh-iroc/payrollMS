package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import test.datacon;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class ApproveExtraActivity extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	String[] cb;
	String emp_id;
	String task_date;
	String isBillable;
	String pro_id;
	String projectID;
	String projectName;
	String autocompleteEmpName; 

	CommonFunctions CF;
	UtilityFunctions uF;
	List<String> enamelist;
	List<Integer> eidlist;
	List<Integer> taskindex;
	List<String> isBillableList;

	Map session;
	Map<Integer, List<String>> taskmap;
	List<FillProjectList> projectdetailslist;

	public String execute() {

		session = ActionContext.getContext().getSession();
		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return LOGIN;

		request.setAttribute(PAGE, "/jsp/task/ApproveExtraActivity.jsp");
		request.setAttribute(TITLE, "Team Activities");

			projectdetailslist = new FillProjectList(request).fillProjectAllDetails();
			enamelist = new ArrayList<String>();
			eidlist = new ArrayList<Integer>();
			taskindex = new ArrayList<Integer>();
			isBillableList = getisBillableList();
			taskmap = new HashMap<Integer, List<String>>();
			uF = new UtilityFunctions();

			if (cb != null) {
				approveExtraActivity();
			}

			/*
			 * if(getTask_date()==null){
			 * setTask_date(uF.getDateFormat(uF.getCurrentDate
			 * (CF.getStrReportDateFormat())+"", DBDATE, DATE_FORMAT)); }
			 */
			getExtraActivity();

		return SUCCESS;
	}

	public List<String> getisBillableList() {
		List<String> isBillableList = new ArrayList<String>();

		isBillableList.add("Billable");
		isBillableList.add("Non Billable");

		return isBillableList;
	}

	public void approveExtraActivity() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = db.makeConnection(con);
			for (int i = 0; i < cb.length; i++) {
			pst = con.prepareStatement("UPDATE task_extraactivity SET approve_status='approved' WHERE task_id =?");
			pst.setInt(1, uF.parseToInt((cb[i])));
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

	public void getEmpId() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(emp_id) from task_extraactivity");
			rs = pst.executeQuery();
			while (rs.next()) {
				eidlist.add(rs.getInt("emp_id"));
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
	}

	public void getEmpDetails() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			for (int i = 0; i < eidlist.size(); i++) {
				pst = con.prepareStatement("select username from user_details where emp_id=?");
				pst.setInt(1, eidlist.get(i));
				rs = pst.executeQuery();
				while (rs.next()) {
					enamelist.add(rs.getString("username"));
				}
				rs.close();
				pst.close();
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
	}

	public void getExtraActivity() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement("select distinct(emp_fname),emp_mname, emp_lname from activity_info ai, employee_personal_details epd where epd.emp_per_id = ai.emp_id and epd.is_alive = true");
			rs = pst.executeQuery();
			StringBuilder sbEmp = new StringBuilder();
			while (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				sbEmp.append("\"" + rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + "\",");
			}
			rs.close();
			pst.close();

			if(sbEmp.length()>1){
				sbEmp = sbEmp.replace(0, sbEmp.length(), sbEmp.toString().substring(0, sbEmp.length() - 1));
			}

			request.setAttribute("sbEmp", sbEmp);

			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpDesig = CF.getEmpDesigMap(con);
			List alActivitiesList = new ArrayList();

			if (getPro_id() != null && !getPro_id().equals("") && getTask_date() != null) {

				pst = con.prepareStatement("select * from task_activity ta,activity_info ai where ta.activity_id=ai.task_id and pro_id=? and ta.task_date=?");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				pst.setDate(2, uF.getDateFormat(getTask_date(), DATE_FORMAT));
			} else if (getPro_id() != null && !getPro_id().equals("")) {

				pst = con.prepareStatement("select * from task_activity ta,activity_info ai where ta.activity_id=ai.task_id and pro_id=?");
				pst.setInt(1, uF.parseToInt(getPro_id()));

			} else if (isBillable != null && getIsBillable().equals("Billable")) {

				pst = con.prepareStatement("select * from task_activity where task_date=? and is_billable=true order by start_time desc");
				pst.setDate(1, uF.getDateFormat(getTask_date(), DATE_FORMAT));
			} else if (isBillable != null && getIsBillable().equals("Non Billable")) {

				pst = con.prepareStatement("select * from task_activity where task_date=? and is_billable=false order by start_time desc");
				pst.setDate(1, uF.getDateFormat(getTask_date(), DATE_FORMAT));

			} else if (getTask_date() != null && getTask_date().length() > 0) {

				pst = con.prepareStatement("select * from task_activity where task_date=? order by start_time desc");
				pst.setDate(1, uF.getDateFormat(getTask_date(), DATE_FORMAT));
			} else {

				// pst =
				// con.prepareStatement("select * from task_activity order by start_time desc");

				pst = con
						.prepareStatement("select * from task_activity where task_date in (select distinct(task_date) from task_activity where task_date<= ? order by task_date desc limit 3 ) order by task_date desc, start_time desc");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));

			}

			rs = pst.executeQuery();

			// System.out.println("pst====>"+pst);
			Map hmActivities = new LinkedHashMap();
			Map hmActivitiesTotal = new LinkedHashMap();

			String strTaskDateOld = null;
			String strTaskDateNew = null;
			double dblTotalTime = 0;

			while (rs.next()) {

				if (getAutocompleteEmpName() != null && getAutocompleteEmpName().length() > 0
						&& !getAutocompleteEmpName().equalsIgnoreCase(hmEmpNameMap.get(rs.getString("emp_id")))) {
					continue;
				}
				List<String> newInner = new ArrayList<String>();

				strTaskDateNew = rs.getString("task_date");

				if (strTaskDateNew != null && !strTaskDateNew.equalsIgnoreCase(strTaskDateOld)) {
					// hmInner = new HashMap();
					alActivitiesList = new ArrayList();
					dblTotalTime = 0.0;
				}

				String task_id = rs.getString("task_id");
				StringBuilder sb = new StringBuilder();
				StringBuilder time = new StringBuilder();

				if (rs.getString("end_time") != null) {
					time.append(uF.getTimeDiffInHoursMins(rs.getTime("start_time").getTime(), rs.getTime("end_time").getTime()));
					dblTotalTime += uF.parseToDouble(uF.getTimeDiffInHoursMins(rs.getTime("start_time").getTime(), rs.getTime("end_time").getTime()));

				}
				/*
				 * if(rs.getBoolean("is_billable") &&
				 * rs.getString("end_time")!=null) {
				 * time.append(uF.getTimeDiffInHoursMins
				 * (rs.getTime("start_time").getTime(),
				 * rs.getTime("end_time").getTime())); }else{ time.append(""); }
				 */
				int a_id = uF.parseToInt(rs.getString("activity_id"));
				int pid = new FillProjectList(request).getProjectId(a_id);
				String ac_name = new FillActivityDetails(request).getActivitName(a_id);
				if (a_id > 0) {
					sb.append("<img src=\"images1/away.png\" border=\"0\"  width=\"16px\" > ");

					if (rs.getString("end_time") == null) {
						sb.append("<strong>" + uF.showData((String) hmEmpNameMap.get(rs.getString("emp_id")), "") + "</strong> ["
								+ uF.showData(hmEmpDesig.get(rs.getString("emp_id")), "") + "]" + " is working on <strong>" + ac_name + "</strong> from "
								+ uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
					} else {
						sb.append("<strong>" + uF.showData((String) hmEmpNameMap.get(rs.getString("emp_id")), "") + "</strong> ["
								+ uF.showData(hmEmpDesig.get(rs.getString("emp_id")), "") + "]" + " was working on <strong>" + ac_name + "</strong> from "
								+ uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
						sb.append(" till " + uF.getDateFormat(rs.getString("end_time"), DBTIME, CF.getStrReportTimeFormat()));
					}

				} else if(a_id < 0){
					sb.append("<img src=\"images1/working.png\" border=\"0\"  width=\"16px\" > ");
					sb.append("<strong>" + uF.showData((String) hmEmpNameMap.get(rs.getString("emp_id")), "") + "</strong> ["
							+ uF.showData(hmEmpDesig.get(rs.getString("emp_id")), "") + "]" + " <strong>" + rs.getString("activity") + "</strong> at "
							+ uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
					sb.append("<br/><div style=\"font-size:10px;margin-left:20px;font-style:italic\">" + uF.showData(rs.getString("_comment"), "") + "</div>");
				}else {
					sb.append("<img src=\"images1/working.png\" border=\"0\"  width=\"16px\" > ");

					if (rs.getString("end_time") == null) {
						sb.append("<strong>" + uF.showData((String) hmEmpNameMap.get(rs.getString("emp_id")), "") + "</strong> ["
								+ uF.showData(hmEmpDesig.get(rs.getString("emp_id")), "") + "]" + " is in <strong>" + rs.getString("activity") + "</strong> at "
								+ uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
						sb.append("<br/><div style=\"font-size:10px;margin-left:20px;font-style:italic\">" + uF.showData(rs.getString("_comment"), "") + "</div>");
					} else if (rs.getBoolean("issent_report")) {
						sb.append("<strong>" + uF.showData((String) hmEmpNameMap.get(rs.getString("emp_id")), "") + "</strong> ["
								+ uF.showData(hmEmpDesig.get(rs.getString("emp_id")), "") + "]" + " sent report at "
								+ uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
						sb.append("<a href=\"GenerateTimeSheet.action?task_date=" + rs.getString("task_date") + "&emptype=admin&emp_id=" + rs.getString("emp_id") + "&taskId="
								+ rs.getString("task_id") + "\" class=\"pdf\" >Pdf </a><a href=\"ExportToExcelTimeSheet.action?task_date=" + rs.getString("task_date")
								+ "&emptype=admin&emp_id=" + rs.getString("emp_id") + "&taskId=" + rs.getString("task_id") + "\" class=\"xls\">Excel </a>");
						sb.append("<br/><div style=\"font-size:10px;margin-left:20px;font-style:italic\">" + uF.showData(rs.getString("_comment"), "")
								+ getSupervisorName(rs.getString("emp_id")) + "</div>");
					} else {
						sb.append("<strong>" + uF.showData((String) hmEmpNameMap.get(rs.getString("emp_id")), "") + "</strong> ["
								+ uF.showData(hmEmpDesig.get(rs.getString("emp_id")), "") + "]" + " was in <strong>" + rs.getString("activity") + "</strong> at "
								+ uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
						sb.append(" till " + uF.getDateFormat(rs.getString("end_time"), DBTIME, CF.getStrReportTimeFormat()));
						sb.append("<br/><div style=\"font-size:10px;margin-left:20px;font-style:italic\">" + uF.showData(rs.getString("_comment"), "") + "</div>");
					}

				}

				newInner.add(sb.toString());
				newInner.add(time.toString());

				alActivitiesList.add(newInner);

				hmActivities.put(uF.getDateFormat(strTaskDateNew, DBDATE, CF.getStrReportDateFormat()), alActivitiesList);
				// hmActivitiesTotal.put(uF.getDateFormat(strTaskDateNew,
				// DBDATE, CF.getStrReportDateFormat()),
				// uF.formatIntoTwoDecimal(dblTotalTime));
				hmActivitiesTotal.put(uF.getDateFormat(strTaskDateNew, DBDATE, CF.getStrReportDateFormat()), uF.roundOffInTimeInHoursMins(dblTotalTime));

				strTaskDateOld = strTaskDateNew;

			}
			rs.close();
			pst.close();

			/*
			 * double totalHrs=0.0; if(getPro_id()!=null &&
			 * !getPro_id().equals("") && getTask_date()!=null){
			 * System.out.println("from pro_id & date"); pst =
			 * con.prepareStatement(
			 * "select sum(ta.actual_hrs) as actual_hrs from task_activity ta,activity_info ai where ta.activity_id=ai.task_id and pro_id=? and ta.task_date=?"
			 * ); pst.setInt(1,uF.parseToInt(getPro_id()));
			 * pst.setDate(2,uF.getDateFormat(getTask_date(), DATE_FORMAT));
			 * }else if(getPro_id()!=null && !getPro_id().equals("")){
			 * System.out.println("from pro_id"); pst = con.prepareStatement(
			 * "select sum(ta.actual_hrs) as actual_hrs from task_activity ta,activity_info ai where ta.activity_id=ai.task_id and pro_id=?"
			 * ); pst.setInt(1,uF.parseToInt(getPro_id())); }else
			 * if(isBillable!=null && getIsBillable().equals("Billable")){
			 * System.out.println("from Billable"); pst = con.prepareStatement(
			 * "select sum(actual_hrs) as actual_hrs from task_activity where task_date=? and is_billable=true"
			 * ); pst.setDate(1,uF.getDateFormat(getTask_date(), DATE_FORMAT));
			 * }else if(isBillable!=null &&
			 * getIsBillable().equals("Non Billable")){
			 * System.out.println("from Non Billable"); pst =
			 * con.prepareStatement(
			 * "select sum(actual_hrs) as actual_hrs from task_activity where task_date=? and is_billable=false"
			 * ); pst.setDate(1,uF.getDateFormat(getTask_date(), DATE_FORMAT));
			 * }else{ System.out.println("from date"); pst =
			 * con.prepareStatement(
			 * "select sum(actual_hrs) as actual_hrs from task_activity where task_date=?"
			 * ); pst.setDate(1,uF.getDateFormat(getTask_date(), DATE_FORMAT));
			 * } rs = pst.executeQuery(); while(rs.next()){
			 * totalHrs=rs.getDouble("actual_hrs"); }
			 * request.setAttribute("totalHrs", totalHrs);
			 */

			request.setAttribute("hmActivitiesTotal", hmActivitiesTotal);
			request.setAttribute("hmActivities", hmActivities);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public String getSupervisorName(String empid) {
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		String ename = "";
		int supervisor_emp_id = 0;
		try {
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement("select supervisor_emp_id from employee_official_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(empid));
			rs = pst.executeQuery();
			while (rs.next()) {

				supervisor_emp_id = rs.getInt("supervisor_emp_id");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select emp_fname,emp_mname,emp_lname from employee_personal_details where emp_per_id=?");
			pst.setInt(1, supervisor_emp_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}

				ename = rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname");
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
		return ename;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public List<String> getEnamelist() {
		return enamelist;
	}

	public void setEnamelist(List<String> enamelist) {
		this.enamelist = enamelist;
	}

	public List<Integer> getEidlist() {
		return eidlist;
	}

	public void setEidlist(List<Integer> eidlist) {
		this.eidlist = eidlist;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String[] getCb() {
		return cb;
	}

	public void setCb(String[] cb) {
		this.cb = cb;
	}

	public Map getSession() {
		return session;
	}

	public void setSession(Map session) {
		this.session = session;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		// TODO Auto-generated method stub
	}

	public Map<Integer, List<String>> getTaskmap() {
		return taskmap;
	}

	public void setTaskmap(Map<Integer, List<String>> taskmap) {
		this.taskmap = taskmap;
	}

	public List<Integer> getTaskindex() {
		return taskindex;
	}

	public void setTaskindex(List<Integer> taskindex) {
		this.taskindex = taskindex;
	}

	public String getTask_date() {
		return task_date;
	}

	public void setTask_date(String task_date) {
		this.task_date = task_date;
	}

	public String getPro_id() {
		return pro_id;
	}

	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}

	public String getAutocompleteEmpName() {
		return autocompleteEmpName;
	}

	public void setAutocompleteEmpName(String autocompleteEmpName) {
		this.autocompleteEmpName = autocompleteEmpName;
	}

	public String getIsBillable() {
		return isBillable;
	}

	public void setIsBillable(String isBillable) {
		this.isBillable = isBillable;
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

	public List<String> getIsBillableList() {
		return isBillableList;
	}

	public void setIsBillableList(List<String> isBillableList) {
		this.isBillableList = isBillableList;
	}

}