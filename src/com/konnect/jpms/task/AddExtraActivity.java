	package com.konnect.jpms.task;

	import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

	public class AddExtraActivity extends ActionSupport implements ServletRequestAware, ServletResponseAware,SessionAware,IStatements {
		private static final long serialVersionUID = 1L;
		
		HttpSession session;
		String extraTask;
		String etime;
		String stime;
		String task_id;
		String comment;
		String submit;
		String start;
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
		String strTaskOnOffSiteT;
		String strBillableYesNoT;
		String workStatus;
		String strTime;
		String strBillableTime;
		
		int emp_id;
		int timestatus;
		int extraActivity;
		
		List<FillProjectList> projectdetailslist;
		List<FillActivityList> activitydetailslist;
		List<FillActivityList> extraActivityList; 
		List<FillServices> serviceList;
		List<FillClients> clientlist;
		List<FillEmployee> empNamesList;
		String clientId;
		String clientName;
		String[] empId;
		
		CommonFunctions CF;
		private HttpServletRequest request;
		UtilityFunctions uF=new UtilityFunctions();
		
		
		public String execute(){
			session = request.getSession();
			CF = (CommonFunctions)session.getAttribute(CommonFunctions);
			if(CF==null) return LOGIN;
			
			emp_id = uF.parseToInt((String)session.getAttribute(EMPID));
			
			String proIds = getEmpProjectIds();
			extraActivityList =  new FillActivityList(request).fillActivityOfProjectsAndOther(proIds, emp_id+"");
			
			projectdetailslist = new FillProjectList(request).fillProjectDetailsByEmp(emp_id, false, uF.parseToInt(getClientId()));
//			activitydetailslist = new FillActivityList(request).fillActivityDetailsByProject(uF.parseToInt(p_id));
			serviceList = new FillServices(request).fillServices((String)session.getAttribute(EMPID));
			clientlist = new FillClients(request).fillClients(false);
			empNamesList = new FillEmployee(request).fillEmployeeName(null, null, session);
			
//			System.out.println("MSG ==>>> " + (String)session.get("MSG") + " MESSAGE ==>>> " + (String)session.get("MESSAGE"));
			
//			System.out.println("getStart() ==>>> " + getStart());
	    	if(getStart()!=null && getStart().equalsIgnoreCase(("Submit"))) {
		    	if((String)session.getAttribute("MSG") != null || (String)session.getAttribute("MESSAGE") != null) {
//		    			endTaskStarted();
		    	}
	    		startNewActivity();
	    	  	session.setAttribute("MESSAGE", "Please end the exiting activity to start the new activity.");
	    	}
	    	
//	    	System.out.println("getTask_id() ==>>> " + getTask_id());
	    	
	    	if(getTask_id() != null) {
	    		endExtraActivity();	
	    		session.setAttribute("MESSAGE", null);
	    		session.setAttribute("MSG", null);
	    	}
	    	
	    	if(cb!=null) {
	    		sendToApprove();
	    	}
		
		return SUCCESS;
		}
		
		
	private String getEmpProjectIds() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder proIds = null;
		Database db = new Database();
		db.setRequest(request);
		
			try {
				con = db.makeConnection(con);
				getOneDaySingleEmpTaskHours(con);
					pst = con.prepareStatement("select distinct(pro_id) as pro_id from project_emp_details WHERE emp_id =? ");
					pst.setInt(1, emp_id);
//					System.out.println("pst ==>>> " + pst);
					
					rs = pst.executeQuery();
					while (rs.next()) {
						if(proIds == null) {
							proIds = new StringBuilder();
							proIds.append(rs.getString("pro_id"));
						} else {
							proIds.append(","+rs.getString("pro_id"));
						}
					}
					rs.close();
					pst.close();
					if(proIds == null) {
						proIds = new StringBuilder();
					}
//					System.out.println("proIds ===>> " + proIds);
					
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
			return proIds.toString();
		}


	private void getOneDaySingleEmpTaskHours(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
			try {
				pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity WHERE emp_id =? and task_date = ?");
				pst.setInt(1, emp_id);
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("pst ==>>> " + pst);
				String totHrs = "0";
				rs = pst.executeQuery();
				while (rs.next()) {
					totHrs = rs.getString("actual_hrs");
				}
				double dblTotHrs = uF.parseToDouble(getStrTime()) + uF.parseToDouble(totHrs);
				request.setAttribute("TotalHrs", dblTotHrs);
				rs.close();
				pst.close();
//				System.out.println("dblTotHrs ===>> " + dblTotHrs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void sendToApprove() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
			try {
				con = db.makeConnection(con);
				for(int i=0;i<cb.length;i++) {
					pst = con.prepareStatement("UPDATE task_activity SET sent ='y',task_date=? WHERE emp_id =? and sent='n' and task_id=?");
					pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(2,emp_id);
					pst.setInt(3,uF.parseToInt(cb[i]));
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
			ResultSet rs = null;
			Connection con = null;
			PreparedStatement pst = null;
			Database db = new Database();
			db.setRequest(request);
			int t_id=0;
			int a_id=0;
			String time="";
			String per="";
			boolean is_billable=false;
			String activity="";
			try {
				con = db.makeConnection(con);	
				pst = con.prepareStatement("select activity,task_id from task_activity where emp_id =? and end_time is null");
				pst.setInt(1, emp_id);
				rs = pst.executeQuery();
//				System.out.println("pst in automatic stop 1===>"+pst);
				while(rs.next()) {
					activity=rs.getString("activity");
					t_id=rs.getInt("task_id");
				}
				rs.close();
				pst.close();	
				
				if(activity==null) {
					//======================================================
					/*pst = con.prepareStatement("select task_id from task_activity where emp_id =? and end_time is null");
					pst.setInt(1, emp_id);
					rs = pst.executeQuery();
					while(rs.next())
					{
						t_id=rs.getInt("task_id");
					}*/
					//======================================================
					pst = con.prepareStatement("select task_id from activity_info where emp_id = ? and timestatus='y'");
					pst.setInt(1, emp_id);
//					System.out.println("pst in automatic stop===> 2"+pst);
					rs = pst.executeQuery();
					while(rs.next()) {
						a_id=rs.getInt("task_id");
					}
					rs.close();
					pst.close();
					//======================================================
		
					pst = con.prepareStatement("UPDATE activity_info SET end_time =?,timestatus='n' WHERE task_id =? ");
					pst.setTime(1, uF.getCurrentTime(CF.getStrTimeZone()));
					pst.setInt(2, a_id);
//					System.out.println("pst in automatic stop===> 3"+pst);
					pst.executeUpdate();
					pst.close();
					
					//======================================================			
					pst = con.prepareStatement("select start_time,end_time from activity_info where task_id=?");
					pst.setInt(1,a_id );
//					System.out.println("pst in automatic stop===> 4"+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
//						System.out.println("rs.getTime()===> 4"+rs.getTime("start_time"));
//						System.out.println("rs.getTime(end_time)===> 4"+rs.getTime("end_time").getTime());
						time = uF.getTimeDiffInHoursMins(rs.getTime("start_time").getTime(), rs.getTime("end_time").getTime());
					} 
					rs.close();
					pst.close();
//					System.out.println("time in automatic stop===> 5"+time);
					//======================================================			
					pst = con.prepareStatement("select completed from activity_info where task_id=?");
					pst.setInt(1,a_id );
					rs = pst.executeQuery();
					while (rs.next()) {
						per = rs.getString("completed");
					} 
					rs.close();
					pst.close();
					
					//======================================================			
					pst = con.prepareStatement("UPDATE task_activity SET end_time=?,actual_hrs=?,task_status=?,activity=?,is_billable=true WHERE activity_id =? and emp_id=? and task_id=(select max(task_id) from task_activity where emp_id=? and activity_id=? )");
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
					
					//======================================================			
					pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs from task_activity where activity_id=? and emp_id=?");
					pst.setInt(1, a_id);
					pst.setInt(2, emp_id);
					double stime = 0.0;
					rs = pst.executeQuery();
					
					while (rs.next()) {
						stime = rs.getDouble("actual_hrs");
					}
					rs.close();
					pst.close();
					
					//======================================================
					pst = con.prepareStatement("UPDATE activity_info SET already_work =?,completed=? WHERE task_id =?");
					pst.setDouble(1, stime);
					pst.setInt(2, uF.parseToInt(per));
					pst.setInt(3, a_id);
					pst.executeUpdate();
					pst.close();
					
					
					
					int parentTaskId = 0;
					pst = con.prepareStatement("select parent_task_id from activity_info where task_id = ? and parent_task_id != 0");
					pst.setInt(1, a_id);
					rs = pst.executeQuery();
					while(rs.next()) {
						parentTaskId = rs.getInt("parent_task_id");
					}
					rs.close();
					pst.close();
					
					double dblAllCompleted = 0.0d;
					int subTaskCnt = 0;
					String taskActualHrs = null;
					String taskActualDays = null;
					pst = con.prepareStatement("select sum(completed) as completed, count(task_id) as count, sum(already_work) as already_work, " +
						"sum(already_work_days) as already_work_days from activity_info where parent_task_id = ?");
					pst.setInt(1, parentTaskId);
//					System.out.println("pst ==>> " + pst);
					rs = pst.executeQuery();
					while(rs.next()) {
						dblAllCompleted = rs.getDouble("completed");
						subTaskCnt = rs.getInt("count");
						taskActualHrs = rs.getString("already_work");
						taskActualDays = rs.getString("already_work_days");
					}
					rs.close();
					pst.close();
					
					double avgComplted = 0.0d;
					if(dblAllCompleted > 0 && subTaskCnt > 0) {
						avgComplted = dblAllCompleted / subTaskCnt;
					}
					
//					if(avgComplted > 0) {
						pst = con.prepareStatement("update activity_info set completed=?, already_work=?, already_work_days=? where task_id=?");
						pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(avgComplted)));
						pst.setDouble(2, uF.parseToDouble(taskActualHrs));
						pst.setDouble(3, uF.parseToDouble(taskActualDays));
						pst.setInt(4, parentTaskId);
						pst.execute();
						pst.close();
//					}
					
					String pro_id = CF.getProjectIdByTaskId(con, a_id+"");	
					pst = con.prepareStatement("select sum(already_work)as already_work, sum(already_work_days)as already_work_day, " +
						"sum(completed)/count(task_id) as avrg from activity_info where pro_id=? and parent_task_id = 0");
					pst.setInt(1, uF.parseToInt(pro_id));
					rs = pst.executeQuery();
					String project_hrs = null;
					String project_days = null;
					String projectCompletePercent = null;
					while (rs.next()) {
						project_hrs = rs.getString("already_work");
						project_days = rs.getString("already_work_day");
						projectCompletePercent = uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rs.getString("avrg")));
					}
					rs.close();
					pst.close();

					pst = con.prepareStatement("update projectmntnc set already_work=?, already_work_days=?, completed=? where pro_id=? ");
					pst.setDouble(1, uF.parseToDouble(project_hrs));
					pst.setDouble(2, uF.parseToDouble(project_days));
					pst.setDouble(3, uF.parseToDouble(projectCompletePercent));
					pst.setInt(4, uF.parseToInt(pro_id));
					pst.execute();
					pst.close();
					
					//======================================================
				} else {
					pst = con.prepareStatement("UPDATE task_activity SET end_time =? WHERE task_id =?");
					pst.setTime(1, uF.getCurrentTime(CF.getStrTimeZone()));
					pst.setInt(2,t_id);
					pst.executeUpdate();
					pst.close();
						
					//======================================================
					String total_time="";
					pst = con.prepareStatement("select start_time,end_time,is_billable from task_activity where task_id =?");
					pst.setInt(1,t_id);
					rs = pst.executeQuery();
					while(rs.next()) {
						if(rs.getBoolean("is_billable")) {
							total_time=uF.getTimeDiffInHoursMins(rs.getTime("start_time").getTime(), rs.getTime("end_time").getTime());
						}
					}
					rs.close();
					pst.close();
					
					//======================================================
					pst = con.prepareStatement("UPDATE task_activity SET actual_hrs =? WHERE task_id =?");
					pst.setDouble(1,uF.parseToDouble(total_time));
					pst.setInt(2,t_id);
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
			ResultSet rs = null;
			Connection con = null;
			PreparedStatement pst = null;
			Database db = new Database();
			db.setRequest(request);
			try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select count(task_id) from task_activity where emp_id =? and end_time is null");
			pst.setInt(1, emp_id);
			rs = pst.executeQuery();
			while(rs.next()) {
				extraActivity=rs.getInt(1);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(task_id) from activity_info where emp_id = ? and timestatus='y'");
			pst.setInt(1, emp_id);
			rs = pst.executeQuery();
			while(rs.next()) {
				timestatus=rs.getInt(1);
			}
			rs.close();
			pst.close();
//			System.out.println("time status"+timestatus);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}
	
	
	public void endExtraActivity() {
			ResultSet rs = null;
			Connection con = null;
			PreparedStatement pst = null;
			Database db = new Database();
			db.setRequest(request);
			try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE task_activity SET end_time =? WHERE task_id =?");
			pst.setTime(1, uF.getCurrentTime(CF.getStrTimeZone()));
			pst.setInt(2,uF.parseToInt(task_id));
			pst.executeUpdate();
			pst.close();
			
			String total_time="";
			pst = con.prepareStatement("select start_time,end_time,is_billable from task_activity where task_id =?");
			pst.setInt(1,uF.parseToInt(task_id));
			rs = pst.executeQuery();
			while(rs.next()) {
				total_time = uF.getTimeDiffInHoursMins(rs.getTime("start_time").getTime(), rs.getTime("end_time").getTime());
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("UPDATE task_activity SET actual_hrs =? WHERE task_id =?");
			pst.setDouble(1,uF.parseToDouble(total_time));
			pst.setInt(2,uF.parseToInt(task_id));
			pst.executeUpdate();
			pst.close();
				
			} catch(Exception e) {
				e.printStackTrace();
			}finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}
		

	public void startNewActivity() {
			boolean isBillable=false;
			if(uF.parseToInt(getActivityId())!=0)
				isBillable=true;
			ResultSet rs = null;
			Connection con = null;
			PreparedStatement pst = null;
			Database db = new Database();
			db.setRequest(request);
			
			String teamIds="";
			try {
				con = db.makeConnection(con);
				
				for(int i=0;getEmpId()!=null && i<getEmpId().length;i++) {
					teamIds+=getEmpId()[i]+",";
				}
				
				if(getStrStartTime()!=null && getStrEndTime()!=null && getStrStartTime().length()>0 && getStrEndTime().length()>0) {
					
					String total_time = uF.getTimeDiffInHoursMins(uF.getTimeFormat(getStrStartTime(), TIME_FORMAT).getTime(), uF.getTimeFormat(getStrEndTime(), TIME_FORMAT).getTime());
					
					if(uF.parseToInt(getExtraTask()) > 0) {
						String orgID = CF.getEmpOrgId(con, uF, getEmp_id()+"");
						String []arr = CF.getPayCycleFromDate(con, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), CF.getStrTimeZone(), CF, orgID);
						String clentID = CF.getClientIdByProjectTaskId(con, uF, getExtraTask(), getEmp_id()+"");
						
						pst = con.prepareStatement("insert into task_activity (task_date,start_time,end_time,emp_id,activity_id,activity_description,sent," +
							"is_billable,actual_hrs,client_id,team_ids,is_manual, total_time,task_status,timesheet_paycycle,task_location," +
							"billable_hrs,is_approved,generated_date)" +
							"values(?,?,?,?, ?,?,'n',?, ?,?,?,?, ?,?,?,?, ?,?,?)");
						pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTime(2, uF.getTimeFormat(getStrStartTime(), TIME_FORMAT));
						pst.setTime(3, uF.getTimeFormat(getStrEndTime(), TIME_FORMAT));
						pst.setInt(4, getEmp_id());
						pst.setInt(5, uF.parseToInt(getExtraTask()));
						pst.setString(6, getComment());
						pst.setBoolean(7, isBillable);
						if(uF.parseToDouble(getStrTime()) > 0) {
							pst.setDouble(8, uF.parseToDouble(getStrTime()));
						} else {
							pst.setDouble(8, uF.parseToDouble(total_time));
						}
//						pst.setDouble(9, uF.parseToDouble(total_time));
						pst.setInt(9, uF.parseToInt(clentID));
						pst.setString(10, teamIds);
						pst.setBoolean(11, true);
						if(uF.parseToDouble(getStrTime()) > 0) {
							pst.setDouble(12, uF.parseToDouble(getStrTime()));
						} else {
							pst.setDouble(12, uF.parseToDouble(total_time));
						}
//						pst.setDouble(13, uF.parseToDouble(total_time));
						pst.setDouble(13, uF.parseToDouble(getWorkStatus()));
						pst.setInt(14, uF.parseToInt(arr[2]));
						pst.setString(15, ((getStrTaskOnOffSiteT().equalsIgnoreCase("1")) ? "ONS": "OFS"));
						if(uF.parseToDouble(getStrBillableTime()) > 0) {
							pst.setDouble(16, uF.parseToBoolean(getStrBillableYesNoT()) ? uF.parseToDouble(getStrBillableTime()) : 0.0d);
						} else {
							pst.setDouble(16, uF.parseToBoolean(getStrBillableYesNoT()) ? uF.parseToDouble(total_time) : 0.0d);
						}
						pst.setInt(17, 0);
						pst.setDate(18, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.executeUpdate();
						pst.close();
						
//						********************  insert in activity_info and projectmntc ***************
						
						insertintoActivityandProject(getExtraTask(), getWorkStatus(), con); 
						
					} else {
						String orgID = CF.getEmpOrgId(con, uF, getEmp_id()+"");
						String []arr = CF.getPayCycleFromDate(con, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), CF.getStrTimeZone(), CF, orgID);
						
						String clntID = CF.getClientIdByProjectId(con, uF, getP_id());
						pst = con.prepareStatement("insert into task_activity (task_date,start_time,end_time,emp_id,activity,activity_description,sent," +
							"is_billable,actual_hrs,client_id,team_ids,is_manual, total_time,task_status,timesheet_paycycle," +
							"task_location,billable_hrs,is_approved,generated_date) values(?,?,?,?, ?,?,'n',?, ?,?,?,?, ?,?,?,?, ?,?,?)");
						pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTime(2, uF.getTimeFormat(getStrStartTime(), TIME_FORMAT));
						pst.setTime(3, uF.getTimeFormat(getStrEndTime(), TIME_FORMAT));
						pst.setInt(4, getEmp_id());
						pst.setString(5, getExtraTask());
						pst.setString(6, getComment());
						pst.setBoolean(7, isBillable);
//						pst.setInt(8, uF.parseToInt(getService_id()));
//						pst.setDouble(9, uF.parseToDouble(total_time));
						if(uF.parseToDouble(getStrTime()) > 0) {
							pst.setDouble(8, uF.parseToDouble(getStrTime()));
						} else {
							pst.setDouble(8, uF.parseToDouble(total_time));
						}
						if(uF.parseToInt(getClientId()) > 0) {
							pst.setInt(9, uF.parseToInt(getClientId()));
						} else {
							pst.setInt(9, uF.parseToInt(clntID));
						}
						pst.setString(10, teamIds);
						pst.setBoolean(11, true);
						if(uF.parseToDouble(getStrTime()) > 0) {
							pst.setDouble(12, uF.parseToDouble(getStrTime()));
						} else {
							pst.setDouble(12, uF.parseToDouble(total_time));
						}
//						pst.setDouble(13, uF.parseToDouble(total_time));
						pst.setDouble(13, uF.parseToDouble(getWorkStatus()));
						pst.setInt(14, uF.parseToInt(arr[2]));
						pst.setString(15, ((getStrTaskOnOffSiteT().equalsIgnoreCase("1")) ? "ONS": "OFS"));
						if(uF.parseToDouble(getStrBillableTime()) > 0) {
							pst.setDouble(16, uF.parseToBoolean(getStrBillableYesNoT()) ? uF.parseToDouble(getStrBillableTime()) : 0.0d);
						} else {
							pst.setDouble(16, uF.parseToBoolean(getStrBillableYesNoT()) ? uF.parseToDouble(total_time) : 0.0d);
						}
						pst.setInt(17, 0);
						pst.setDate(18, uF.getCurrentDate(CF.getStrTimeZone()));
						int a = pst.executeUpdate();
						pst.close();
					}
				} else {
					
					if(uF.parseToInt(getExtraTask()) > 0) {
						String orgID = CF.getEmpOrgId(con, uF, getEmp_id()+"");
						String []arr = CF.getPayCycleFromDate(con, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), CF.getStrTimeZone(), CF, orgID);
						
//						System.out.println("getExtraTask() ===>> " + getExtraTask() + "getEmp_id() ===>> " + getEmp_id());
						
						String clentID = CF.getClientIdByProjectTaskId(con, uF, getExtraTask(), getEmp_id()+"");
//						System.out.println("clentID ===>> " + clentID);
						
						pst = con.prepareStatement("insert into task_activity (activity_id,task_date,start_time,total_time,emp_id,task_status," +
							"actual_hrs,timesheet_paycycle,client_id,activity_description,task_location,is_billable) values (?,?,?,?,?,?,0,?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getExtraTask()));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
						pst.setDouble(4, 0);
						pst.setInt(5, getEmp_id());
						pst.setDouble(6, 0);
						pst.setInt(7, uF.parseToInt(arr[2]));
						pst.setInt(8, uF.parseToInt(clentID));
						pst.setString(9, getComment());
						pst.setString(10, ((getStrTaskOnOffSiteT().equalsIgnoreCase("1")) ? "ONS": "OFS"));
						pst.setBoolean(11, uF.parseToBoolean(getStrBillableYesNoT()));
						pst.executeUpdate();
						pst.close();
						
					} else {
						String clntID = CF.getClientIdByProjectId(con, uF, getP_id());
						pst = con.prepareStatement("insert into task_activity(task_date,start_time,emp_id,activity,activity_description,sent,is_billable,client_id,team_ids) values(?,?,?,?, ?,'n',?,?, ?)");
						pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTime(2, uF.getCurrentTime(CF.getStrTimeZone()));
						pst.setInt(3, getEmp_id());
						pst.setString(4, getExtraTask());
						pst.setString(5, getComment());
						pst.setBoolean(6, uF.parseToBoolean(getStrBillableYesNoT()));
	//					pst.setInt(7, uF.parseToInt(getService_id()));
						if(uF.parseToInt(getClientId()) > 0) {
							pst.setInt(7, uF.parseToInt(getClientId()));
						} else {
							pst.setInt(7, uF.parseToInt(clntID));
						}
						pst.setString(8, teamIds);
						pst.executeUpdate();
						pst.close();
					}
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}
	
	
	private void insertintoActivityandProject(String activityID, String completedPercent, Connection con) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			if (uF.parseToInt(activityID) > 0) {
				StringBuilder sumActivityQuery = new StringBuilder();

				sumActivityQuery.append("select sum(actual_hrs)as actual_hrs, count (distinct(task_date)) as actual_days from task_activity where emp_id = ? "
								+ " and activity_id=?");
				pst = con.prepareStatement(sumActivityQuery.toString());
				pst.setInt(1, emp_id);
				pst.setInt(2, uF.parseToInt(activityID));
				rst = pst.executeQuery();
				String actual_hrs = null;
				String actual_days = null;
				while (rst.next()) {
					actual_hrs = rst.getString("actual_hrs");
					actual_days = rst.getString("actual_days");
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

				pst = con.prepareStatement("update activity_info set already_work=?, already_work_days=?, completed=? where task_id=?");
				pst.setDouble(1, uF.parseToDouble(actual_hrs));
				pst.setDouble(2, uF.parseToDouble(actual_days));
				pst.setInt(3, uF.parseToInt(completedPercent));
				pst.setInt(4, uF.parseToInt(activityID));
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
				pst = con.prepareStatement("select sum(completed) as completed, count(task_id) as count, sum(already_work)as already_work, " +
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
				
//				System.out.println("taskActualHrs ==>> " + taskActualHrs);
//				System.out.println("taskActualDays ==>> " + taskActualDays);
				
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
		
		public void setActivitydetailslist(List<FillActivityList> activitydetailslist) {
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
		
		public List<FillActivityList> getExtraActivityList() {
			return extraActivityList;
		}

		public void setExtraActivityList(List<FillActivityList> extraActivityList) {
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

		public String getStrTaskOnOffSiteT() {
			return strTaskOnOffSiteT;
		}

		public void setStrTaskOnOffSiteT(String strTaskOnOffSiteT) {
			this.strTaskOnOffSiteT = strTaskOnOffSiteT;
		}

		public String getStrBillableYesNoT() {
			return strBillableYesNoT;
		}

		public void setStrBillableYesNoT(String strBillableYesNoT) {
			this.strBillableYesNoT = strBillableYesNoT;
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

		public String getStart() {
			return start;
		}

		public void setStart(String start) {
			this.start = start;
		}


		@Override
		public void setSession(Map arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
