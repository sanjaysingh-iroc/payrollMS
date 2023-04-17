package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.itextpdf.text.xml.simpleparser.NewLineHandler;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class ExtraActivityView extends ActionSupport implements ServletRequestAware, ServletResponseAware, SessionAware, IStatements {
	private static final long serialVersionUID = 1L;
	int emp_id;
	Map session;
	String task_date;
	 
	CommonFunctions CF; 
	UtilityFunctions uF = new UtilityFunctions();
	private HttpServletRequest request;
	List<Integer> taskindex = new ArrayList<Integer>();
	Map<Integer, List<String>> taskmap = new HashMap<Integer, List<String>>();
	List<String> taskdatelist = new ArrayList<String>();
	List<Integer> taskiddatelist = new ArrayList<Integer>();
	List<Integer> datewiseindex = new ArrayList<Integer>();
	Map<Integer, List<String>> datewisemap = new HashMap<Integer, List<String>>();
	List<FillTaskDates> taskdateslist;
	String taskDateEmpId;
	String taskDate;
	boolean flag;

	public String execute() {
			session = ActionContext.getContext().getSession();
			CF = (CommonFunctions) session.get(CommonFunctions);
			if (CF == null)
				return LOGIN;
			request.setAttribute(PAGE, "/jsp/task/ExtraActivityView.jsp");
			request.setAttribute(TITLE, "My Activities");
			emp_id = uF.parseToInt((String) session.get(EMPID));
			taskdateslist = new FillTaskDates(request).fillTaskDate(emp_id);

			if (getTask_date() == null) {
				setTask_date(uF.getDateFormat(uF.getCurrentDate(CF.getStrReportDateFormat()) + "", DBDATE, DATE_FORMAT));
			}

			flag = checkTaskStatus();

			// if(task_date!=null)
			// {
			// getDateWiseExtraActivity();
			// }else{
			//
			getAllActivities();
			// }
			viewDate();
			// getExtraActivity();

			// getAllActivities();
		return SUCCESS;
	}

	
	public boolean checkTaskStatus() {
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		boolean flag = false;
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select count(task_id) as count from activity_info where emp_id = ? and timestatus='y'");
			pst.setInt(1, emp_id);
			rs = pst.executeQuery();
			while (rs.next()) {

				rs.getInt("count");
				if (rs.getInt("count") > 0) {
					flag = true;
				}
			}
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			pst = con.prepareStatement("select count(task_id) as count from task_activity where emp_id =? and end_time is null");
			pst.setInt(1, emp_id);
			rs = pst.executeQuery();
			while (rs.next()) {

				rs.getInt("count");
				if (rs.getInt("count") > 0) {
					flag = true;
				}
			}
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
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
		return flag;
	}

	
	
	public void getAllActivities() {
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		try {
			con = db.makeConnection(con);
			
			List alActivitiesList = new ArrayList();
			Map<String, String> hmClientName = new FillTaskRelatedMap(request).getClientNameMap();
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);

			Map<String, String> hmTimesheetSubmittedDate = new HashMap<String, String>();
			pst = con.prepareStatement("select * from project_timesheet where timesheet_from<=? and timesheet_to>=?");
			pst.setDate(1, uF.getDateFormat(getTask_date(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getTask_date(), DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				hmTimesheetSubmittedDate.put(uF.getDateFormat(getTask_date(), DATE_FORMAT, DBDATE), uF.getDateFormat(rs.getString("timesheet_generated_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			
			if (getTask_date() != null) {
				// pst =
				// con.prepareStatement("select * from task_activity where emp_id=? and task_date=? and sent='y'");
				pst = con.prepareStatement("select * from task_activity where emp_id=? and task_date=? order by start_time desc");
				pst.setInt(1, emp_id);
				pst.setDate(2, uF.getDateFormat(getTask_date(), DATE_FORMAT));
			} else {
				pst = con.prepareStatement("select * from task_activity where emp_id=? and sent='n' order by start_time desc");
				pst.setInt(1, emp_id);
			}

			rs = pst.executeQuery();
			List<String> newInner = new ArrayList<String>();
			int ncount=0;
			while (rs.next()) {
				StringBuilder sbWithPerson = new StringBuilder();
				newInner = new ArrayList<String>();
				String task_id = rs.getString("task_id");
				StringBuilder sb = new StringBuilder();
				StringBuilder time = new StringBuilder();
				String isManual = null;

				if (rs.getInt("client_id") > 0) {
					sbWithPerson .append(" with Client: " + hmClientName.get(rs.getString("client_id")));
				}else if(rs.getString("team_ids")!=null){
					String []arr = rs.getString("team_ids").split(",");
					
					for(int i=0; i<arr.length && hmEmpNames.get(arr[i])!=null; i++){
						sbWithPerson .append(" "+hmEmpNames.get(arr[i]));
						
						if(i<arr.length-2){
							sbWithPerson .append(",");
						}else if(i<arr.length-1){
							sbWithPerson .append(" and");
						}
					}
				}
				
				if (rs.getString("end_time") != null) {
//					time.append(uF.getTimeDiffInHoursMins(rs.getTime("start_time").getTime(), rs.getTime("end_time").getTime()));
					time.append(uF.getTotalTimeMinutes100To60(rs.getString("actual_hrs")));
				}
				/*
				 * if(rs.getBoolean("is_billable") &&
				 * rs.getString("end_time")!=null) {
				 * time.append(uF.getTimeDiffInHoursMins
				 * (rs.getTime("start_time").getTime(),
				 * rs.getTime("end_time").getTime())); }else{
				 * 
				 * time.append(""); }
				 */

				int a_id = uF.parseToInt(rs.getString("activity_id"));
				int pid = new FillProjectList(request).getProjectId(a_id);
				String ac_name = new FillActivityDetails(request).getActivitName(a_id);
				if (a_id > 0) {

					sb.append("<img src=\"images1/away.png\" border=\"0\"  width=\"16px\" > ");

					// <a
					// onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })"
					// href="EndTaskPopup.action?id=<%=strTaskId%>&pro_id=<%=strProjectId%>">
					if (rs.getString("end_time") == null) {
						// sb.append(" <a href=\"EndTaskPopup.action?id="+rs.getString("activity_id")+"&pro_id="+pid+"\">End Task</a>");
						sb.append("I am working on " + ac_name + " from " + uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
						sb.append(" <a onclick=\"endTask('"+rs.getString("activity_id")+"', '" + pid + "', '"+rs.getString("task_id")+"')\" href=\"javascript:void(0);\">End this task</a>");
//						sb.append(" <a onclick=\"return hs.htmlExpand(this, { objectType: 'ajax',width:700 })\" href=\"EndTaskPopup.action?id=" + rs.getString("activity_id")
//							+ "&pro_id=" + pid + "\">End Task</a>");
					} else {
//						sb.append("I was working on " + ac_name + " from " + uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
						sb.append("I was working on " + ac_name);
//						sb.append(" till " + uF.getDateFormat(rs.getString("end_time"), DBTIME, CF.getStrReportTimeFormat()));
					}

				} else {

					// if("y".equalsIgnoreCase(rs.getString("sent"))){
					// sb.append("");
					// }else{
					// sb.append("<input type='checkbox' value='"+task_id+"' name='cb' />");
					// }
					if (rs.getBoolean("issent_report")) {
						sb.append("<img src=\"images1/working.png\" border=\"0\"  width=\"16px\" > ");

						sb.append("" + rs.getString("activity") + " at " + uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
						// sb.append("<a href=\"GenerateTimeSheet.action?task_date="+rs.getString("task_date")+"&isOld=true\" class=\"pdf\" >Pdf </a><a href=\"ExportToExcelTimeSheet.action?task_date="+rs.getString("task_date")+"&isOld=true&emp_id="+emp_id+"&taskId="+rs.getString("task_id")+"\" class=\"xls\">Excel </a>");

//						sb.append("<a href=\"GenerateTimeSheet.action?task_date=" + rs.getString("task_date") + "&isOld=true&emp_id=" + emp_id + "&taskId="
//								+ rs.getString("task_id") + "\" class=\"pdf\" >Pdf </a>");
						sb.append("<a href=\"ExportToExcelTimeSheet.action?task_date=" + rs.getString("task_date")
								+ "&isOld=true&emp_id=" + emp_id + "&taskId=" + rs.getString("task_id") + "\" class=\"xls\">Excel </a>");
						sb.append("<br/><div style=\"font-size:10px;margin-left:20px;font-style:italic\">" + uF.showData(rs.getString("_comment"), " ") + getSupervisorName()
								+ "</div>");

					} else if (rs.getBoolean("issent_timesheet")) {
						sb.append("<img src=\"images1/working.png\" border=\"0\"  width=\"16px\" > ");

						sb.append("" + rs.getString("activity") + " at " + uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
						// sb.append("<a href=\"GenerateTimeSheet.action?task_date="+rs.getString("task_date")+"&isOld=true\" class=\"pdf\" >Pdf </a><a href=\"ExportToExcelTimeSheet.action?task_date="+rs.getString("task_date")+"&isOld=true&emp_id="+emp_id+"&taskId="+rs.getString("task_id")+"\" class=\"xls\">Excel </a>");

						//sb.append("<a href=\"javascript:void(0)\" onclick=\"sendTimesheet1("+rs.getString("emp_id")+", '"+uF.getDateFormat(rs.getString("timesheet_start_date"), DBDATE, DATE_FORMAT)+"','"+uF.getDateFormat(rs.getString("timesheet_end_date"), DBDATE, DATE_FORMAT)+"', 0)\" class=\"pdf\">Donwload</a>");
						
						sb.append("<br/><div style=\"font-size:10px;margin-left:20px;font-style:italic\">" + uF.showData(rs.getString("_comment"), " ") + getSupervisorName()
								+ "</div>");

					} else if(a_id < 0) {
						sb.append("<img src=\"images1/working.png\" border=\"0\"  width=\"16px\" > ");
						sb.append(rs.getString("activity") + " at "	+ uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
						sb.append("<br/><div style=\"font-size:10px;margin-left:20px;font-style:italic\">" + uF.showData(rs.getString("_comment"), "") + "</div>");
					} else {
						sb.append("<img src=\"images1/working.png\" border=\"0\"  width=\"16px\" > ");
						sb.append("In " + rs.getString("activity") + uF.showData(sbWithPerson.toString(), "") + " at "
								+ uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));

						if (rs.getString("end_time") == null) {
							sb.append(" <a href=\"AddExtraActivity.action?task_id=" + task_id + "\">End Activity</a>");
							session.put("ActivitID", task_id);

						} else {
							sb.append(" till " + uF.getDateFormat(rs.getString("end_time"), DBTIME, CF.getStrReportTimeFormat()) + " "
									+ ((rs.getBoolean("is_manual")) ? "<span style=\"float:right\">M</span>" : ""));
						}

						sb.append("<br/><div style=\"font-size:10px;margin-left:20px;font-style:italic\">" + uF.showData(rs.getString("_comment"), "") + "</div>");
					}

				}

				newInner.add(sb.toString());
				boolean flag = checkTaskApprovedStatus(con, emp_id+"", rs.getString("task_id"));

				if(flag) {
					newInner.add(time.toString()+ ((hmTimesheetSubmittedDate.containsKey(rs.getString("task_date")))?"":"<div id=\"myDiv_"+ncount+"\" style=\"float:right;\"><a class=\"del\" onclick=\"(alert('Timesheet for this activity is already approved.'))\">Delete</a></div>"));
				} else {
					newInner.add(time.toString()+ ((hmTimesheetSubmittedDate.containsKey(rs.getString("task_date")))?"":"<div id=\"myDiv_"+ncount+"\" style=\"float:right;\"><a class=\"del\" onclick=\"(confirm('Are you sure you want to delete this entry?')?getContent('myDiv_"+ncount+"','DeleteActivity.action?activity_id="+rs.getString("task_id")+"'):'')\">Delete</a></div>"));
				}
				alActivitiesList.add(newInner);
				ncount++;
			}
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			double totalHrs = 0.0;
			
			// pst =
			// con.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity where emp_id=? and is_billable=true and task_date=? ");
			pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity where emp_id=? and task_date=? ");
			pst.setInt(1, getEmp_id());
			pst.setDate(2, uF.getDateFormat(getTask_date(), DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				totalHrs = rs.getDouble("actual_hrs");
			}
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			request.setAttribute("totalHrs", uF.getTotalTimeMinutes100To60(totalHrs+""));
			request.setAttribute("alActivitiesList", alActivitiesList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	public boolean checkTaskApprovedStatus(Connection con, String strSessionEmpId, String taskId) {
		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean flag = false;
		try {
			StringBuilder sbquery = new StringBuilder();
			sbquery.append("select activity_id, is_approved from task_activity where task_id =? and is_approved=2 ");
			pst = con.prepareStatement(sbquery.toString());
			pst.setInt(1, uF.parseToInt(taskId));
			rs = pst.executeQuery();
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("flag", flag);
//			System.out.println("flag===> " + flag);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	return flag;	
	}



	public String getSupervisorName() {
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
			pst.setInt(1, getEmp_id());
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

	public void getDateWiseExtraActivity() {
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		List alActivitiesList = new ArrayList();
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from task_activity where emp_id=? and task_date=?");
			pst.setInt(1, getEmp_id());
			pst.setDate(2, uF.getDateFormat(getTask_date(), DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> newInner = new ArrayList<String>();
				newInner.add(rs.getString("activity"));
				newInner.add(rs.getString("start_time"));
				newInner.add(rs.getString("end_time"));
				newInner.add(rs.getString("_comment"));
				alActivitiesList.add(newInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("alActivitiesList", alActivitiesList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getExtraActivity() {
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from task_extraactivity where emp_id=? and task_status='n'");
			pst.setInt(1, emp_id);
			rs = pst.executeQuery();
			int a = 1;
			while (rs.next()) {

				List<String> newInner = new ArrayList<String>();
				String t_id = rs.getString("task_id");
				newInner.add("<input type='checkbox' value='" + t_id + "' name='cb' />");
				newInner.add(new Integer(a).toString());
				newInner.add(rs.getString("task_name"));
				newInner.add(rs.getString("status"));
				newInner.add(rs.getString("_comment"));
				newInner.add(rs.getString("stime"));
				newInner.add(uF.removeNull(rs.getString("etime")));
				a++;
				taskmap.put(rs.getInt(1), newInner);
				taskindex.add(rs.getInt(1));
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

	public void viewDate() {
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select DISTINCT ON (task_date) task_date,emp_id  from task_activity where emp_id=? and sent!='n' ORDER BY task_date DESC");
			pst.setInt(1, getEmp_id());
			rs = pst.executeQuery();
			while (rs.next()) {
				taskiddatelist.add(rs.getInt("emp_id"));
				taskdatelist.add(rs.getString("task_date"));
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

	public String getTask_date() {
		return task_date;
	}

	public void setTask_date(String task_date) {
		this.task_date = task_date;
	}

	public List<Integer> getDatewiseindex() {
		return datewiseindex;
	}

	public void setDatewiseindex(List<Integer> datewiseindex) {
		this.datewiseindex = datewiseindex;
	}

	public Map<Integer, List<String>> getDatewisemap() {
		return datewisemap;
	}

	public void setDatewisemap(Map<Integer, List<String>> datewisemap) {
		this.datewisemap = datewisemap;
	}

	public int getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(int emp_id) {
		this.emp_id = emp_id;
	}

	public List<Integer> getTaskindex() {
		return taskindex;
	}

	public void setTaskindex(List<Integer> taskindex) {
		this.taskindex = taskindex;
	}

	public Map<Integer, List<String>> getTaskmap() {
		return taskmap;
	}

	public void setTaskmap(Map<Integer, List<String>> taskmap) {
		this.taskmap = taskmap;
	}

	public List<String> getTaskdatelist() {
		return taskdatelist;
	}

	public void setTaskdatelist(List<String> taskdatelist) {
		this.taskdatelist = taskdatelist;
	}

	public List<Integer> getTaskiddatelist() {
		return taskiddatelist;
	}

	public void setTaskiddatelist(List<Integer> taskiddatelist) {
		this.taskiddatelist = taskiddatelist;
	}

	public List<FillTaskDates> getTaskdateslist() {
		return taskdateslist;
	}

	public void setTaskdateslist(List<FillTaskDates> taskdateslist) {
		this.taskdateslist = taskdateslist;
	}

	public String getTaskDateEmpId() {
		return taskDateEmpId;
	}

	public void setTaskDateEmpId(String taskDateEmpId) {
		this.taskDateEmpId = taskDateEmpId;
	}

	public String getTaskDate() {
		return taskDate;
	}

	public void setTaskDate(String taskDate) {
		this.taskDate = taskDate;
	}

	public Map getSession() {
		return session;
	}

	@Override
	public void setSession(Map arg0) {
		// TODO Auto-generated method stub

	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
