package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class EmpCompletedViewProject extends ActionSupport implements
		ServletRequestAware, ServletResponseAware, SessionAware, IStatements {
	private static final long serialVersionUID = 1L;
	private HttpServletRequest request;
	int id;
	int emp_id;
	Map session;
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
	float actual_days;
	float ideal_days;
	float no_of_days;
	
	public String execute() {
			
			session = ActionContext.getContext().getSession();
			CF = (CommonFunctions) session.get(CommonFunctions);
			if (CF == null)
				return LOGIN;
			request.setAttribute(PAGE, "/jsp/task/EmpCompletedViewProject.jsp");
			request.setAttribute(TITLE, "My Projects");
			UtilityFunctions uF = new UtilityFunctions();
			
			emp_id = uF.parseToInt((String) session.get(EMPID));
			if ((Integer) session.get("pro_id") != null) {
				pro_id = (Integer) session.get("pro_id");
			}
			projectdetailslist=new FillProjectList(request).fillProjectDetailsByEmp(emp_id, true, 0);
			if (isFinish != null) {
				FinishTask();
			}
			getProjectList();
			
			getProjectDetails();

			
			if (pro_id != 0) {
				isSingle = true;
				List<Integer> p_id = new ArrayList<Integer>();
				p_id.add(pro_id);
				getAllProjects(p_id);
			} else {
				List<Integer> p_id = getProjectIds();
				getAllProjects(p_id);
			}

			session.remove("pro_id");
		return SUCCESS;
	}
 
	

	public void getProjectDetails(){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		GetPriorityList objGP = new GetPriorityList();
		
		try {
			con = db.makeConnection(con);
			Map<String, List<String>> hmProject = new HashMap<String, List<String>>();
			Map<String, List<String>> hmTasks = new HashMap<String, List<String>>();
			Map<String, List<String>> hmActivities = new HashMap<String, List<String>>();
			
			Map<String, String> hmEmployee = CF.getEmpNameMap(con, null, null);
			if(getPro_id()!=0)
			{
				pst = con.prepareStatement("select * from projectmntnc where pro_id=?");
				pst.setInt(1, getPro_id());
			}else{
				pst = con.prepareStatement("select * from projectmntnc where pro_id in (select pro_id from project_emp_details where emp_id = ?) order by deadline asc");
				pst.setInt(1, emp_id);
			}
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				List<String> alInner = new ArrayList<String>();
				
				alInner.add(rs.getString("pro_id"));
				alInner.add(rs.getString("pro_name"));
				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), ""));
				alInner.add(rs.getString("priority"));
				
				alInner.add(rs.getString("service"));
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("idealtime"));
				alInner.add(rs.getString("already_work"));
				alInner.add(rs.getString("completed"));
				alInner.add((("n".equalsIgnoreCase(rs.getString("approve_status")))?"Pending":"Completed"));
				
				
				hmProject.put(rs.getString("pro_id"), alInner);
			}
			rs.close();
			pst.close();

			request.setAttribute("hmProject", hmProject);
			
			
			
			pst = con.prepareStatement("select * from activity_info where emp_id = ? and approve_status='approved' order by deadline asc");
			pst.setInt(1, emp_id);
			rs = pst.executeQuery();
			
			
			String strProjectIdNew = null;
			String strProjectIdOld = null;
			List<String> alInner = new ArrayList<String>();
			while(rs.next()){
				double no_of_hrs=0.0;
				String strColour = null;
				String strdaysColour = null;
				strProjectIdNew = rs.getString("pro_id"); 
				if(strProjectIdNew!=null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)){
					alInner = new ArrayList<String>();
				}
				String taskid=rs.getString("task_id");
				alInner.add(taskid);
				// days and time calculations
				if(rs.getInt("completed")>100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))>0.0 ){
					getNoOfDays(uF.parseToInt(taskid));
				}
				if(rs.getInt("completed")>=100){
					no_of_hrs=uF.parseToDouble(rs.getString("already_work"))-uF.parseToDouble(rs.getString("idealtime"));
				}
				if(uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))>=0.0 && uF.parseToDouble(uF.dateDifference(rs.getString("end_date"), DBDATE, rs.getString("deadline"),DBDATE))>=0.0)
				{
					
					strdaysColour="green";
					
				}else{
					strdaysColour="red";
					
				}
				if(uF.parseToDouble(rs.getString("already_work"))>uF.parseToDouble(rs.getString("idealtime")))
				{
					strColour="red";
				}else{
					strColour="green";
					
				}
				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
				
				if(rs.getString("taskstatus").equalsIgnoreCase("New Task")){
					alInner.add("<span style=\"color:red\">Planned</span>");
				}else if(dtCurrentDate!=null && dtDeadlineDate!=null && dtCurrentDate.after(dtDeadlineDate)){
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")){
						alInner.add("<span style=\"color:#5D862B\">Confirmed</span> (<b><span style=\"color:"+strColour+"\">" +uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b>days )");
					}else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")){
						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/<b> <span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days )");
					}else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0){
						alInner.add("<span style=\"color:red\">Overdue </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days )");
					}else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")){
						alInner.add("<span style=\"color:"+strColour+"\">Working </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days )");
					}else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100){
						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
					}else{
						alInner.add("<span style=\"color:"+strColour+"\">Working</span> (<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/<b> <span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days )");
					}
					
				}else if(rs.getInt("completed")>=100){
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")){
						alInner.add("<span style=\"color:#5D862B\">Confirmed </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/<b><span style=\"color:"+strdaysColour+"\"> "+no_of_days+"</span></b> days )");
					}else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")){
						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/<b> <span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days )");
					}else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0){
						alInner.add("<span style=\"color:red\">Overdue </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days )");
					}else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")){
						alInner.add("<span style=\"color:"+strColour+"\">Working </span>(<b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days)");
					}else{
						alInner.add("<span style=\"color:"+strColour+"\">Working </span>( <b><span style=\"color:"+strColour+"\">"+uF.formatIntoTwoDecimal(no_of_hrs)+"</span></b> hrs/ <b><span style=\"color:"+strdaysColour+"\">"+no_of_days+"</span></b> days )");
					}
				}else{
					if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100){
						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
					}else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")){
						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>");
					}else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")){
						alInner.add("<span style=\"color:"+strColour+"\">Working </span>");
					}else{
						alInner.add("<span style=\"color:"+strColour+"\">Working </span>");
					}
				}
				
				
				String isFinish = rs.getString("finish_task");
				if (rs.getDouble("completed") >= 100 && isFinish.equals("n")) {
					alInner.add("<input type='checkbox' value='"+ taskid + "' name='isFinish' />");
				} else {
					alInner.add("");
				}
				
				
				alInner.add(rs.getString("activity_name"));
				alInner.add(rs.getString("priority"));
				alInner.add(uF.showData(hmEmployee.get(rs.getString("emp_id")), ""));
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("idealtime"));
				alInner.add(rs.getString("already_work"));
				alInner.add(rs.getString("completed"));
				
				hmTasks.put(rs.getString("pro_id"), alInner);
				
				
				strProjectIdOld = strProjectIdNew;
			}
			rs.close();
			pst.close();

			request.setAttribute("hmTasks", hmTasks);
			
			pst = con.prepareStatement("select * from task_activity where activity_id in (select task_id from activity_info where emp_id = ?) order by activity_id, task_date desc, start_time desc");
			pst.setInt(1, emp_id);
			rs = pst.executeQuery();
			
			alInner = new ArrayList<String>();
			
			String strTaskIdNew = null;
			String strTaskIdOld = null;
			
			while(rs.next()){
				
				strTaskIdNew = rs.getString("activity_id"); 
				if(strTaskIdNew!=null && !strTaskIdNew.equalsIgnoreCase(strTaskIdOld)){
					alInner = new ArrayList<String>();
				}
				
			
				
				alInner.add(rs.getString("task_id"));
				alInner.add(rs.getString("task_date"));
				alInner.add(rs.getString("start_time"));
				alInner.add(rs.getString("end_time"));
				alInner.add(rs.getString("actual_hrs"));
				
				hmActivities.put(rs.getString("activity_id"), alInner);
				
				
				strTaskIdOld = strTaskIdNew;
			}
			rs.close();
			pst.close();

			request.setAttribute("hmActivities", hmActivities);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	public void getNoOfDays(int t_id) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs1 = null;
		PreparedStatement pst1 = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			String dl_date = "";
			String start_date = "";
			String end_date = "";
			con = db.makeConnection(con);
			
			pst1 = con.prepareStatement("select deadline,start_date,end_date from activity_info where task_id=?");
			pst1.setInt(1, t_id);
			rs1 = pst1.executeQuery();
			while (rs1.next()) {
				dl_date = rs1.getString(1);
				start_date = rs1.getString(2);
				end_date = rs1.getString(3);

			}
			rs1.close();
			pst1.close();
			System.out.println("Dates dl_date 1:"+dl_date);
			System.out.println("Dates  start_date 2:"+start_date);
			System.out.println("Dates end_date 3:"+end_date);
			ideal_days = Float.parseFloat(uF.dateDifference(start_date,"yyyy-mm-dd", dl_date, "yyyy-mm-dd"));
			if(end_date!=null)
			actual_days = Float.parseFloat(uF.dateDifference(start_date,"yyyy-mm-dd", end_date, "yyyy-mm-dd"));
			else
			actual_days = Float.parseFloat(uF.dateDifference(start_date,"yyyy-mm-dd", uF.getCurrentDate(O_TIME_ZONE).toString(), "yyyy-mm-dd"));
			
			no_of_days=actual_days-ideal_days;
			System.out.println("Dates ideal_days:" + ideal_days);
			
			System.out.println("Dates end_date:" + end_date);
			System.out.println("Dates actual_days:" + actual_days);
			System.out.println("Dates no_of_days:" + no_of_days);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}

	}
	public void FinishTask() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		if (isFinish != null) {
			System.out.println("From finish task");
			for (int i = 0; i < isFinish.length; i++) {
				try {
					con = db.makeConnection(con);
					pst = con.prepareStatement("UPDATE activity_info SET finish_task='y',end_date=? WHERE task_id =?");
					pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(2, uF.parseToInt(isFinish[i]));
					System.out.println("From finish task pst"+pst);
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
		}
	}

	public void getProjectList() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
//		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(pro.pro_id),pro.pro_name from activity_info ac,projectmntnc pro where ac.emp_id=? and pro.pro_id= ac.pro_id and pro.approve_status='n' order by pro_id");
			pst.setInt(1, emp_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				projectidlist.add(rs.getInt("pro_id"));
				projectlist.add(rs.getString("pro_name"));
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

	public List<Integer> getProjectIds() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
//		UtilityFunctions uF = new UtilityFunctions();
		List<Integer> i = new ArrayList<Integer>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(pro_id) as pro_id from activity_info where emp_id=? order by pro_id");
			pst.setInt(1, emp_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				i.add(rs.getInt("pro_id"));
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
		return i;
	}

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

	public void getAllProjects(List<Integer> p_id) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			for (int a = 0; a < p_id.size(); a++) {
				pst = con.prepareStatement("select pro_name,priority,document_name,service,emp_id,completed,taskstatus,_comment,deadline,idealtime,already_work,completed,timestatus,teamlead_name from projectmntnc where pro_id=? and approve_status='n' order by pro_id");
				pst.setInt(1, p_id.get(a));
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> alInner = new ArrayList<String>();
					alInner.add("<b>" + rs.getString("pro_name") + "</b>");
					alInner.add("");
					alInner.add(rs.getString("priority"));
					String file = rs.getString("document_name");
					if (file != null) {
						alInner.add("<a href='taskuploads/" + file
								+ "' target='blank'>" + file + "</a>");
					} else {
						alInner.add("No Attachments");
					}
					alInner.add(rs.getString("service"));
					if (uF.parseToInt(rs.getString("completed")) < 100) {
						alInner.add("");
					} else {
						alInner.add("Finished");
					}
					alInner.add(rs.getString("_comment"));
					alInner.add(uF.getDateFormat(rs.getString("deadline"),DBDATE, CF.getStrReportDateFormat()));
					alInner.add(rs.getString("idealtime") + "hrs");
					alInner.add(rs.getDouble("already_work") + "hrs");
					if (isSingle) {
						alInner.add("");
						alInner.add("");
						alInner.add("");
					} else {
						alInner.add("");
						alInner.add("<a href='EmpViewProject.action?pro_id="+ p_id.get(a) + "'>View More</a>");
					}
					al.put(p_id.get(a), alInner);
					index.add(p_id.get(a));
				}
				rs.close();
				pst.close();
				getActivityDetailForAll(p_id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getTimeDetail(int t_id) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs1 = null;
		PreparedStatement pst1 = null;
//		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst1 = con.prepareStatement("select * from task_activity where activity_id=? and emp_id=? order by task_id");
			pst1.setInt(1, t_id);
			pst1.setInt(2, emp_id);
			rs1 = pst1.executeQuery();
			while (rs1.next()) {
				time1.add(rs1.getString("start_time").substring(0, 5));
				String s = rs1.getString("end_time");
				if (s == null) {
					time1.add("-");
				} else {
					time1.add(rs1.getString("end_time").substring(0, 5));
				}
				totalhrz1.add(rs1.getString("actual_hrs"));
				taskStatus1.add(rs1.getString("task_status"));
			}
			rs1.close();
			pst1.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
	}

	public void getActivityDetailForAll(List<Integer> index) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			Map<Integer, List<List<String>>> activityDetailMap = new HashMap<Integer, List<List<String>>>();
			con = db.makeConnection(con);
			for (int k = 0; k < index.size(); k++) {
				List<List<String>> outInner = new ArrayList<List<String>>();
				pst = con.prepareStatement("select * from activity_info where pro_id=? and emp_id=? and approve_status='n' order by task_id");
				pst.setInt(1, index.get(k));
				pst.setInt(2, emp_id);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> alInner = new ArrayList<String>();
					task_id = rs.getInt("task_id");
					alInner.add("");
					alInner.add(rs.getString("activity_name"));
					alInner.add(rs.getString("priority"));
					String file = rs.getString("filename");
					if (file != null)
						alInner.add("<a href='taskuploads/" + file+ "' target='blank'>" + file + "</a>");
					else
						alInner.add("No Attachments");
					alInner.add("");

					alInner.add(rs.getString("_comment"));
					alInner.add(uF.getDateFormat(rs.getString("deadline"),DBDATE, CF.getStrReportDateFormat()));
					alInner.add(rs.getString("idealtime") + "hrs");
					int per = uF.parseToInt(rs.getString("completed"));
					if (per > 0)
						alInner.add(5, "Pending");
					else
						alInner.add(5, "New Task");
					String isFinish = rs.getString("finish_task");
					if (per >= 100 && isFinish.equals("n")) {
						alInner.add(0, "<input type='checkbox' value='"	+ task_id + "' name='isFinish' />");
					} else {
						alInner.add(0, "");
					}
					if (isSingle) {
						getTimeDetail(task_id);
						if (time1 != null && time1.size() != 0) {
							String t_hrz = "";
							String taskSta = "";
							String time = "";
							for (int x = 0; x < totalhrz1.size(); x++) {
								t_hrz += totalhrz1.get(x) + "&nbsp;hrs<br/>";
							}
							alInner.add(t_hrz);
							for (int x = 0; x < taskStatus1.size(); x++) {
								taskSta += taskStatus1.get(x) + "%<br/>";
							}
							alInner.add(taskSta);
							for (int x = 0; x < time1.size(); x++) {
								String t = time1.get(x++);
								String t2 = time1.get(x);
								time += t + "&nbsp;-&nbsp;" + t2 + "hrs <br/>";
							}
							alInner.add(time);
						} else {
							alInner.add("");
							alInner.add("");
							alInner.add("Not Started");
						}
						totalhrz1 = new ArrayList<String>();
						taskStatus1 = new ArrayList<String>();
						time1 = new ArrayList<String>();
						if (per < 100) {
							if (rs.getString("timestatus").equals("n")) {
								alInner.add("<input type=\"button\" name=\"start\" class=\"input_button\" value=\"start\" onclick=\"start123("+ task_id + ");\"/>");
							} else if (rs.getString("timestatus").equals("y")) {
								alInner.add("<a href=\"EndTaskPopup.action?id="+ task_id+"&pro_id="+index.get(k)+"\" onclick=\"return hs.htmlExpand(this, { objectType: 'ajax',width:700 })\"><input type=\"button\" name=\"start\" class=\"input_button\" value=\"End\"/></a>");
							}
						} else {
							if (isFinish.equals("n")) {
								alInner.add("Testing");
							} else {
								alInner.add("Finished");
							}
						}
					} else {
						alInner.add(rs.getDouble("already_work") + "hrs");
						alInner.add(per + "%");
						alInner.add("");
					}
					outInner.add(alInner);
					activity_index.add(rs.getInt(1));
				}
				rs.close();
				pst.close();
				activityDetailMap.put(index.get(k), outInner);
			}
			request.setAttribute("activityDetailMap", activityDetailMap);
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



	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}