package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class GetMoreProjects extends ActionSupport implements ServletRequestAware, ServletResponseAware, SessionAware, IStatements {

	private static final long serialVersionUID = 1L;
	private HttpServletRequest request;
	String approve;
	String blocked;
	String operation;
	int ID;
	int singleProid;
	int emp_id;
	String[] pro_id;
	Map session;
	int task_id;
	boolean isSingle = false;
	private String[] cb;
	private String[] approvePr;
	
	CommonFunctions CF;
	UtilityFunctions uF = new UtilityFunctions();
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
	String strUserType;
	List<FillProjectList> projectdetailslist;
	List<FillServices> serviceList;
	List<FillClients> clientList;
	List<FillEmployee> managerList;
	List<FillSkills> skillList;
	String[] skill;
	String[] managerId;
	String[] client;
	String projectID;
	String projectName;
	String[] f_service;
	double actual_days;
	double ideal_days;
	String isReassign;
	String proType;
	String strLimit;
	String strDivCount;
	
	String service_id;
	String project_id;
	String manager_id;
	String client_id;
	
	public String execute() {
		try {
			
			session = ActionContext.getContext().getSession();
			CF = (CommonFunctions) session.get(CommonFunctions);
			if (CF == null)
				return LOGIN;
			
			strUserType = (String)session.get(BASEUSERTYPE);
			
			getProjectDetails();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public void getProjectDetails() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
//			System.out.println("getProType ===>> " + getProType()+" -- getService_id ===>> " + getService_id() + " -- getClient_id ===>> " + getClient_id() + " -- getProject_id ===>> " + getProject_id() + " -- getManager_id ===>> " + getManager_id());
			Map<String, List<String>> hmProject = new LinkedHashMap<String, List<String>>();
			Map<String, List<List<String>>> hmTasks = new LinkedHashMap<String, List<List<String>>>();
			Map<String, List<List<String>>> hmSubTasks = new LinkedHashMap<String, List<List<String>>>();
			
//			Map<String, String> hmEmployee = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmServicemap = CF.getProjectServicesMap(con, true);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from projectmntnc where pro_id > 0 ");
			
			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
				sbQuery.append(" and approve_status='n' ");
			} else if(getProType() != null && getProType().equals("C")) {
				sbQuery.append(" and approve_status='approved' ");
			} else if(getProType() != null && getProType().equals("B")) {
				sbQuery.append(" and approve_status='blocked' ");
			}
			
			if(getService_id()!=null && getService_id().length() > 0) {
				StringBuilder service = getConcateData(getService_id());
				sbQuery.append(" and service in ("+service+") ");
			}
			if(getClient_id()!=null && getClient_id().length() > 0) {
				sbQuery.append(" and client_id in ("+getClient_id()+") ");
			}
			/*if(getSkill()!=null && getSkill().length > 0) {
				StringBuilder skill = getConcateData1(getSkill());
				sbQuery.append(" and pro_id in (select pro_id from project_skill_details where skill_id in ("+skill.toString()+"))");
			}*/
			if(getProject_id()!=null && getProject_id().length() > 0) {
				sbQuery.append(" and pro_id in ("+getProject_id()+") ");
			}
			if(getManager_id()!=null && getManager_id().length() > 0) {
				sbQuery.append(" and added_by in ("+getManager_id()+") ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "+uF.parseToInt((String)session.get(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.get(EMPID))+" ) ");
			}
			sbQuery.append(" order by pro_id limit 10 offset "+getStrLimit()+" ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
//			System.out.println("pst date ==========>> " + new Date());
			rs = pst.executeQuery();
			
			setStrLimit(""+(uF.parseToInt(getStrLimit())+10));
			setStrDivCount(""+(uF.parseToInt(getStrDivCount())+1));
			request.setAttribute("strLimit", getStrLimit());
			request.setAttribute("strDivCount", getStrDivCount());
			
			GetPriorityList objGP = new GetPriorityList();
			
			StringBuilder sbProIds = null;
			while(rs.next()) {
				if(sbProIds == null) {
					sbProIds = new StringBuilder();
					sbProIds.append(rs.getString("pro_id"));
				} else {
					sbProIds.append(","+rs.getString("pro_id"));
				}
				List<String> alInner = new ArrayList<String>();
				
				Map<String, String> hmProjectData = new HashMap<String, String>();
				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
					
				Map<String, String> hmProAWDaysAndHrs = CF.getProjectActualAndBillableEfforts(con, rs.getString("pro_id"), hmProjectData);
				
				alInner.add(rs.getString("pro_id"));
				alInner.add(rs.getString("pro_name"));
				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), ""));
				
				alInner.add(uF.showData(hmServicemap.get(rs.getString("service")), ""));
				alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				
				
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
//					String actualTime = CF.getProjectActualTime(con, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
					String actualTime = hmProAWDaysAndHrs.get("ACTUAL_DAYS");
					alInner.add(actualTime+" days");
				} else {
					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" hrs");
//					String actualTime = CF.getProjectActualTime(con, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
					String actualTime = hmProAWDaysAndHrs.get("ACTUAL_HRS");
					alInner.add(actualTime+" hrs");
				}
				
				
				alInner.add(rs.getString("completed"));
				
				java.util.Date dtStartDate = uF.getDateFormat(rs.getString("start_date"), DBDATE);
				java.util.Date dtDeadLine = uF.getDateFormat(rs.getString("deadline"), DBDATE);
				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
				java.util.Date dtEntryDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE);
				java.util.Date dtPrevDate = uF.getPrevDate(CF.getStrTimeZone(), 7);
				
//				System.out.println("dtDeadLine=="+dtDeadLine+" dtCurrentDate=="+dtCurrentDate+" "+dtDeadLine.after(dtCurrentDate));
				
				
				if("n".equalsIgnoreCase(rs.getString("approve_status"))) {
					if(uF.parseToDouble(rs.getString("completed"))>=100) {
						alInner.add("<span style=\"color:green\">Completed</span>");
					} else if(dtCurrentDate!=null && dtDeadLine!=null && dtCurrentDate.after(dtDeadLine) && uF.parseToDouble(rs.getString("already_work"))>0) {
						alInner.add("<span style=\"color:red\">Overdue</span>");
					} else if(dtCurrentDate!=null && dtStartDate!=null && dtCurrentDate.before(dtStartDate) && uF.parseToDouble(rs.getString("already_work"))==0) {
						alInner.add("<span style=\"color:orange\">Planned</span>");
					} else if(dtCurrentDate!=null && dtDeadLine!=null && uF.parseToDouble(rs.getString("already_work"))>0) {
						alInner.add("<span style=\"color:green\">Working</span>");
					} else {
						alInner.add("<span style=\"color:orange\">Planned</span>");
					}
				} else {
					alInner.add("<span style=\"color:green\">Completed</span>");
				}
				
				alInner.add(uF.showData(CF.getClientNameById(con, rs.getString("client_id")), ""));
				if(dtEntryDate!=null && dtEntryDate.after(dtPrevDate)) {
					alInner.add("1"); // show new icon on new projects	
				} else {
					alInner.add("0");
				}
				
//				alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("already_work"))));
				
				hmProject.put(rs.getString("pro_id"), alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmProject", hmProject);
			
			if(sbProIds != null || getSingleProid() != 0) {
//				System.out.println("sbProIds ===>> " + sbProIds.toString());
			Map<String, String> hmPMilestoneSize = new HashMap<String, String>();
			pst = con.prepareStatement("select count(project_milestone_id) as count, pro_id from project_milestone_details where pro_id in ("+sbProIds.toString()+") group by pro_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmPMilestoneSize.put(rs.getString("pro_id"), rs.getString("count"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmPMilestoneSize", hmPMilestoneSize);
			
			
			Map<String, String> hmPDocumentCounter = new HashMap<String, String>();
			pst = con.prepareStatement("select count(pro_document_id) as count, pro_id from project_document_details where pro_id in ("+sbProIds.toString()+") and file_size is not null group by pro_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmPDocumentCounter.put(rs.getString("pro_id"), rs.getString("count"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmPDocumentCounter ====>>> " + hmPDocumentCounter);
			request.setAttribute("hmPDocumentCounter", hmPDocumentCounter);
			
				
			if(getSingleProid() != 0) {
				pst = con.prepareStatement("select pmc.actual_calculation_type, ai.reassign_by, pmc.billing_type, ai.pro_id, ai.task_id, ai.completed, " +
					"ai.idealtime, ai.already_work_days, ai.already_work, ai.deadline, ai.end_date, ai.taskstatus, ai.approve_status, ai.finish_task, " +
					"ai.activity_name, ai.priority, ai.emp_id, ai.resource_ids, ai.parent_task_id,pmc.start_date,pmc.deadline,pmc.bill_days_type," +
					"pmc.hours_for_bill_day from activity_info ai, projectmntnc pmc where ai.pro_id = pmc.pro_id and ai.pro_id=? and " +
					" ai.parent_task_id = 0 and is_milestone=?");
				pst.setInt(1,getSingleProid());
				pst.setBoolean(2,false);
			} else {
				pst = con.prepareStatement("select pmc.actual_calculation_type, ai.reassign_by, pmc.billing_type, ai.pro_id, ai.task_id, ai.completed, " +
					"ai.idealtime, ai.already_work_days, ai.already_work, ai.deadline, ai.end_date, ai.taskstatus, ai.approve_status, ai.finish_task, " +
					"ai.activity_name, ai.priority, ai.emp_id, ai.resource_ids, ai.parent_task_id,pmc.start_date,pmc.deadline,pmc.bill_days_type," +
					"pmc.hours_for_bill_day from activity_info ai, projectmntnc pmc where ai.pro_id=pmc.pro_id and pmc.pro_id in ("+sbProIds.toString()+") " +
					"and ai.parent_task_id = 0 and is_milestone=? order by ai.pro_id");
				pst.setBoolean(1,false);
			}	
			rs = pst.executeQuery();
//			System.out.println("pst ==========>> " + pst);
//			System.out.println("pst date ==========>> " + new Date());
			List<List<String>> proTaskList = new ArrayList<List<String>>();
			while(rs.next()) {
				String strColour = null;
				String strdaysColour = null;
				String strworkingColour = null;
				double no_of_hrs=0.0;
				double no_of_days=0.0;
				Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, rs.getString("pro_id"));
				Map<String, String> hmProjectData = new HashMap<String, String>();
				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
					
				Map<String, String> hmTaskAWDaysAndHrs = CF.getProjectTaskActualWorkedDaysAndHrs(con, rs.getString("task_id"), hmProjectData);
				
				proTaskList = hmTasks.get(rs.getString("pro_id"));
				if(proTaskList == null) proTaskList = new ArrayList<List<String>>();
				
				List<String> alInner = new ArrayList<String>();	
				
				String taskActivityCnt = CF.getTaskActivityTaskCount(con, uF, rs.getString("task_id"));
				
				String taskid = rs.getString("task_id");
				alInner.add(taskid);  //0
				
				double dblCompleted = 0;
				double dblIdealTime = 0;
				double dblAlreadyWorked = 0;

				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
//					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work_days"));
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_DAYS"));
				} else {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
//					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work"));
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
				
				double taskDealineCompletePercent = getTaskDeadlineCompletePercent(uF.parseToInt(taskid));
				String tdcpSpan = "";
				if(taskDealineCompletePercent <= 50) {
					 /*tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/approved.png\"></span>";*/
					tdcpSpan = "<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i></span>";
				} else if(taskDealineCompletePercent > 50 && taskDealineCompletePercent <= 75) {
					/*tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/re_submit.png\"></span>";*/
					tdcpSpan = "<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i></span>";
					
					
				} else {
					/*tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/denied.png\"></span>";*/
					tdcpSpan = "<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i></span>";
				}
				
				if(dblCompleted > 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
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
				String timeSpan = "none";
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					daySpan="inline";
				} else {
					timeSpan="inline";
				}
				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
				
				if(rs.getString("taskstatus") != null && rs.getString("taskstatus").equalsIgnoreCase("New Task")) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"color:#5D862B\">Confirmed</span> (<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b> "+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add("<span style=\"color:red\">Overdue </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
					} else {
						alInner.add("<span style=\"color:orange\">Planned</span>");
					}
					
				} else if(dtCurrentDate!=null && dtDeadlineDate!=null && dtCurrentDate.after(dtDeadlineDate)) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"color:#5D862B\">Confirmed</span> (<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>days</span>)");
					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add("<span style=\"color:red\">Overdue </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
					} else {
						alInner.add("<span style=\"color:"+strColour+"\">Working</span> (<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					}
					
				} else if(rs.getInt("completed")>=100) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status") != null && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"color:#5D862B\">Confirmed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add("<span style=\"color:red\">Overdue </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else {
						alInner.add("<span style=\"color:"+strColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					}
				} else {
					if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>");
					} else {
						alInner.add("<span style=\"color:"+strColour+"\">Working </span>");
					}
				}
				
				
				alInner.add(rs.getString("activity_name")); //2
				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), "")); //3
				alInner.add(rs.getString("priority")); //4
//				boolean isTeamLead = CF.getProjectTLByEmpId(con, rs.getString("pro_id"), rs.getString("emp_id")); 
				alInner.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")); //5
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat())); //6
				
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					alInner.add(uF.formatIntoOneDecimal(dblIdealTime)+" days"); //7
					alInner.add(uF.formatIntoOneDecimal(dblAlreadyWorked)+" days"); //8
				} else {
					alInner.add(uF.formatIntoOneDecimal(dblIdealTime)+" hrs"); //7
					alInner.add(uF.formatIntoOneDecimal(dblAlreadyWorked)+" hrs"); //8
				}
				
				alInner.add(uF.showData(rs.getString("completed"), "0")); //9
				alInner.add(uF.showData(rs.getString("approve_status"), "")); //10
				alInner.add(uF.showData(rs.getString("taskstatus"), "")); //11
				alInner.add(taskActivityCnt+""); //12
				
				proTaskList.add(alInner);
				hmTasks.put(rs.getString("pro_id"), proTaskList);
			}
			rs.close();
			pst.close();
			
			if((getSingleProid()) != 0) {
				pst = con.prepareStatement("select pmc.actual_calculation_type, ai.reassign_by, pmc.billing_type, ai.pro_id, ai.task_id, ai.completed, " +
					"ai.idealtime, ai.already_work_days, ai.already_work, ai.deadline, ai.end_date, ai.taskstatus, ai.approve_status, ai.finish_task, " +
					"ai.activity_name, ai.priority, ai.emp_id, ai.resource_ids,ai.parent_task_id,pmc.start_date,pmc.deadline,pmc.bill_days_type," +
					"pmc.hours_for_bill_day from activity_info ai, projectmntnc pmc where ai.pro_id=pmc.pro_id and ai.pro_id=? and " +
					" ai.parent_task_id !=0 and is_milestone=?");
				pst.setInt(1,getSingleProid());
				pst.setBoolean(2,false);
			} else {
				pst = con.prepareStatement("select pmc.actual_calculation_type, ai.reassign_by, pmc.billing_type, ai.pro_id, ai.task_id, ai.completed, " +
					"ai.idealtime, ai.already_work_days, ai.already_work, ai.deadline, ai.end_date, ai.taskstatus, ai.approve_status, ai.finish_task, " +
					"ai.activity_name, ai.priority, ai.emp_id, ai.resource_ids,ai.parent_task_id,pmc.start_date,pmc.deadline,pmc.bill_days_type," +
					"pmc.hours_for_bill_day from activity_info ai, projectmntnc pmc where ai.pro_id=pmc.pro_id and pmc.pro_id in ("+sbProIds.toString()+") " +
					"and ai.parent_task_id !=0 and is_milestone=? order by ai.pro_id");
				pst.setBoolean(1,false);
			}	
			rs = pst.executeQuery();
//			System.out.println("pst ==========>> " + pst);
//			System.out.println("pst date ==========>> " + new Date());
			
			List<List<String>> proSubTaskList = new ArrayList<List<String>>();
			while(rs.next()) {
				String strColour = null;
				String strdaysColour = null;
				String strworkingColour = null;
				double no_of_hrs = 0.0;
				double no_of_days = 0.0;
				Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, rs.getString("pro_id"));
				Map<String, String> hmProjectData = new HashMap<String, String>();
				hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
					
				Map<String, String> hmTaskAWDaysAndHrs = CF.getProjectTaskActualWorkedDaysAndHrs(con, rs.getString("task_id"), hmProjectData);
				
				proSubTaskList = hmSubTasks.get(rs.getString("parent_task_id"));
				if(proSubTaskList == null) proSubTaskList = new ArrayList<List<String>>();
				
				List<String> alInner = new ArrayList<String>();	
				
				String taskActivityCnt = CF.getTaskActivityTaskCount(con, uF, rs.getString("task_id"));
				
				String taskid = rs.getString("task_id");
				alInner.add(taskid); //0
				
				double dblCompleted = 0;
				double dblIdealTime = 0;
				double dblAlreadyWorked = 0;

				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
//					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work_days"));
					dblAlreadyWorked = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_DAYS"));
				} else {
					dblCompleted = uF.parseToDouble(rs.getString("completed"));
					dblIdealTime  = uF.parseToDouble(rs.getString("idealtime"));
//					dblAlreadyWorked = uF.parseToDouble(rs.getString("already_work"));
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
				
				double taskDealineCompletePercent = getTaskDeadlineCompletePercent(uF.parseToInt(taskid));
				String tdcpSpan = "";
				if(taskDealineCompletePercent <= 50) {
					/*tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/approved.png\"></span>";*/
					tdcpSpan = "<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i></span>";
					
				} else if(taskDealineCompletePercent > 50 && taskDealineCompletePercent <= 75) {
					/*tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/re_submit.png\"></span>";*/
					tdcpSpan = "<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i></span>";
					
				} else {
					/*tdcpSpan = "<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/denied.png\"></span>";*/
					tdcpSpan = "<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i></span>";
				}
				
				if(dblCompleted > 100 || uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0 ) {
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
				String timeSpan = "none";
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					daySpan="inline";
				} else {
					timeSpan="inline";
				}
				java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
				java.util.Date dtDeadlineDate = uF.getDateFormatUtil(rs.getString("deadline"), DBDATE);
				
				if(rs.getString("taskstatus") != null && rs.getString("taskstatus").equalsIgnoreCase("New Task")) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"color:#5D862B\">Confirmed</span> (<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b> "+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"), DBDATE))<0.0) {
						alInner.add("<span style=\"color:red\">Overdue </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
					} else {
						alInner.add("<span style=\"color:orange\">Planned</span>");
					}
					
				} else if(dtCurrentDate!=null && dtDeadlineDate!=null && dtCurrentDate.after(dtDeadlineDate)) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"color:#5D862B\">Confirmed</span> (<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>" +uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b>days</span>)");
					} else if(rs.getInt("completed")>=100 && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add("<span style=\"color:red\">Overdue </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task"))  && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
					} else {
						alInner.add("<span style=\"color:"+strColour+"\">Working</span> (<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					}
					
				} else if(rs.getInt("completed")>=100) {
					
					if(rs.getInt("completed")>=100 && rs.getString("approve_status") != null && rs.getString("approve_status").equalsIgnoreCase("approved")) {
						alInner.add("<span style=\"color:#5D862B\">Confirmed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n") && uF.parseToDouble(uF.dateDifference(uF.getCurrentDate(O_TIME_ZONE).toString(), DBDATE, rs.getString("deadline"),DBDATE))<0.0) {
						alInner.add("<span style=\"color:red\">Overdue </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					} else {
						alInner.add("<span style=\"color:"+strColour+"\">Working </span>(<span style=\"display: "+timeSpan+"; color:"+strColour+"\"><b>"+uF.formatIntoTwoDecimal(no_of_hrs)+"</b> hrs</span><span style=\"display: "+daySpan+"; color:"+strdaysColour+"\"><b>"+no_of_days+"</b> days</span>)");
					}
				} else {
					if(uF.parseToInt(rs.getString("reassign_by"))>0 && rs.getInt("completed")<100) {
						alInner.add("<span style=\"color:"+strColour+"\">Reassigned </span>");
					} else if(rs.getInt("completed")>=100 && "y".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add("<span style=\"color:"+strColour+"\">Completed </span>");
					} else if(rs.getInt("completed")<100 && "n".equalsIgnoreCase(rs.getString("finish_task")) && rs.getString("approve_status").equalsIgnoreCase("n")) {
						alInner.add(tdcpSpan + "<span style=\"color:"+strworkingColour+"\">Working </span>");
					} else {
						alInner.add("<span style=\"color:"+strColour+"\">Working </span>");
					}
				}
				
				alInner.add(rs.getString("activity_name")); //2
				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), "")); //3
				alInner.add(rs.getString("priority")); //4
//				boolean isTeamLead = CF.getProjectTLByEmpId(con, rs.getString("pro_id"), rs.getString("emp_id"));
				alInner.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")); //5
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat())); //6
				
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					alInner.add(uF.formatIntoOneDecimal(dblIdealTime)+" days"); //7
					alInner.add(uF.formatIntoOneDecimal(dblAlreadyWorked)+" days"); //8
				} else {
					alInner.add(uF.formatIntoOneDecimal(dblIdealTime)+" hrs"); //7
					alInner.add(uF.formatIntoOneDecimal(dblAlreadyWorked)+" hrs"); //8
				}
				
				alInner.add(uF.showData(rs.getString("completed"), "0")); //9
				alInner.add(uF.showData(rs.getString("approve_status"), "")); //10
				alInner.add(uF.showData(rs.getString("taskstatus"), "")); //11
				alInner.add(taskActivityCnt+""); //12
				
				proSubTaskList.add(alInner);
				hmSubTasks.put(rs.getString("parent_task_id"), proSubTaskList);
			}
			rs.close();
			pst.close();
		}
			request.setAttribute("hmSubTasks", hmSubTasks);
			request.setAttribute("hmTasks", hmTasks);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private StringBuilder getConcateData(String data) {
		StringBuilder sb=new StringBuilder();
		List<String> dataList = new ArrayList<String>();
		if(data != null && data.length()>0) {
			dataList = Arrays.asList(data.split(","));
		}
		for(int i=0; dataList!=null && !dataList.isEmpty() && i<dataList.size();i++) {
			if(i==0) {
				sb.append("'"+dataList.get(i)+"'");
			} else {
				sb.append(",'"+dataList.get(i)+"'");
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
	
	public String[] getApprovePr() {
		return approvePr;
	}

	public void setApprovePr(String[] approvePr) {
		this.approvePr = approvePr;
	}
	
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
	public List<FillEmployee> getManagerList() {
		return managerList;
	}
	public void setManagerList(List<FillEmployee> managerList) {
		this.managerList = managerList;
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

	public String[] getCb() {
		return cb;
	}

	public void setCb(String[] cb) {
		this.cb = cb;
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

	public Map getSession() {
		return session;
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

	public String getStrLimit() {
		return strLimit;
	}

	public void setStrLimit(String strLimit) {
		this.strLimit = strLimit;
	}

	public String getStrDivCount() {
		return strDivCount;
	}

	public void setStrDivCount(String strDivCount) {
		this.strDivCount = strDivCount;
	}

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public String getManager_id() {
		return manager_id;
	}

	public void setManager_id(String manager_id) {
		this.manager_id = manager_id;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	
}
