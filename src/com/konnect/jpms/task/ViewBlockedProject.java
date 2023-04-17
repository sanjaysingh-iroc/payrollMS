package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class ViewBlockedProject extends ActionSupport implements
		ServletRequestAware, ServletResponseAware, SessionAware, IStatements {

	private static final long serialVersionUID = 1L;
	private HttpServletRequest request;
	String approve;
	int emp_id;
	String[] pro_id; 
	Map session;
	int task_id;
	boolean isSingle = false;
	private String[] cb;
	private String[] approvePr;
	CommonFunctions CF; 
	List<Integer> projectidlist = new ArrayList<Integer>();
	List<FillProjectList> projectdetailslist;
	List<String> projectlist = new ArrayList<String>();
	List<String> alInner = new ArrayList<String>();
	Map<Integer, List<String>> activity_al = new HashMap<Integer, List<String>>();
	// Comes from ViewAllProject.action to view all project
	List<Integer> index = new ArrayList<Integer>();
	List<String> time1 = new ArrayList<String>();
	List<String> taskStatus1 = new ArrayList<String>();
	List<String> totalhrz1 = new ArrayList<String>();
	Map<Integer, List<String>> al = new HashMap<Integer, List<String>>();
	Map<Integer, String> EmpNameMap = new HashMap<Integer, String>();
//	Map<String,String> 	hmServiceDesc=new HashMap<String, String>();
	List<FillClients> clientList;
	List<FillServices> serviceList;
	List<FillEmployee> managerList;
	List<FillSkills> skillList;
	String[] skill;
	String[] managerId;
	String[] client;
	String projectID;
	String projectName;
	String[] f_service;
	String strUserType;
	
	public String execute() {
		
		session = ActionContext.getContext().getSession();
		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		getProjectNameToView();
		
		request.setAttribute(PAGE, "/jsp/task/ViewBlockedProjects.jsp");
		request.setAttribute(TITLE, "Blocked Projects");
		strUserType = (String)session.get(BASEUSERTYPE);
//			clientList = new FillClients().fillClients(); 
//			hmServiceDesc=CF.getServiceDesc();
		
		loadProjectFilters();
		getProjectDetails();
			
		return SUCCESS;
	}
	
	

	public void loadProjectFilters() {
			
			projectdetailslist=new FillProjectList(request).fillAllApprovedProjectDetails();
//			serviceList = new FillServices(request).fillServices();
			serviceList = new FillServices(request).fillProjectServices();
			clientList = new FillClients(request).fillClients(false);
			managerList = new FillEmployee(request).fillManagerList();
			skillList = new FillSkills(request).fillSkillsWithId();
			
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
			Map<String, List<String>> hmProject = new HashMap<String, List<String>>();
			Map<String, List<String>> hmTasks = new HashMap<String, List<String>>();
			Map<String, String> hmEmployee = CF.getEmpNameMap(con,null, null);
			
			Map<String, String> hmClientMap = new HashMap<String, String>();
			pst = con.prepareStatement("select * from client_details");
			rs = pst.executeQuery();
			
			while(rs.next()){
				hmClientMap.put(rs.getString("client_id"), rs.getString("client_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmProjectService = CF.getProjectServicesMap(con, false);
			
			/*if (pro_id != 0) {
			pst = con.prepareStatement("select * from projectmntnc where pro_id=? and approve_status!='n' order by pro_id");
			pst.setInt(1,pro_id);
		}if(getClient()!=null && uF.parseToInt(getClient())>0){
			pst = con.prepareStatement("select * from projectmntnc where approve_status!='n' and client_id =? order by pro_id");
			pst.setInt(1, uF.parseToInt(getClient()));
		}else{
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
				pst = con.prepareStatement("select * from projectmntnc where approve_status!='n' and added_by = ?");
				pst.setInt(1, uF.parseToInt((String)session.get(EMPID)));
			}else{
				pst = con.prepareStatement("select * from projectmntnc where approve_status!='n'");
			}
			
		}*/
			
			
			
			/*if(getF_service()!=null  && getF_service().length()>0 && getPro_id()==0 && getManagerId()!=null && getManagerId().length()>0 && getClient().equals("") && getSkill()!=null && getSkill().length()>0 )
			{
				pst = con.prepareStatement("select * from projectmntnc where approve_status='n' and added_by=? and service=?  " +
						"and pro_id in (select pro_id from project_skill_details where  skill_name=?)");
				pst.setInt(1,uF.parseToInt(getManagerId()));
				pst.setString(2,getF_service());
				pst.setString(3,getSkill());
				
			}else if(getF_service()!=null  && getF_service().length()>0  && getPro_id()!=0 && getManagerId()!=null && getManagerId().length()>0 && getClient()!=null && getClient().length()>0)
			{
				pst = con.prepareStatement("select * from projectmntnc where approve_status='blocked' and pro_id=? and added_by=? and service=? and client_id=?");
				pst.setInt(1,getPro_id());
				pst.setInt(2,uF.parseToInt(getManagerId()));
				pst.setString(3,getF_service());
				pst.setInt(4,uF.parseToInt(getClient()));
				
			}else if(getF_service()!=null && getF_service().length()>0  && getManagerId()!=null && getManagerId().length()>0 && getClient()!=null && getSkill()!=null && getSkill().length()>0)
			{
				pst = con.prepareStatement("select * from projectmntnc where approve_status='blocked' and added_by=? and service=? " +
						"and client_id=? and pro_id in (select pro_id from project_skill_details where  skill_name=?)");
				pst.setInt(1,uF.parseToInt(getManagerId()));
				pst.setString(2,getF_service());
				pst.setInt(3,uF.parseToInt(getClient()));
				pst.setString(4,getSkill());
				
			}else if(getManagerId()!=null && getManagerId().length()>0){
				pst = con.prepareStatement("select * from projectmntnc where added_by=? and approve_status='blocked'");
				pst.setInt(1,uF.parseToInt(getManagerId()));
				
			}else if(getPro_id()>0){
				pst = con.prepareStatement("select * from projectmntnc where pro_id=? and approve_status='blocked'");
				pst.setInt(1,getPro_id());
			}else if(getSkill()!=null && getSkill().length()>0){
				pst = con.prepareStatement("select * from projectmntnc where approve_status='blocked' and pro_id in (select pro_id from project_skill_details where  skill_name=?)");
				pst.setString(1,getSkill());
				
			}else if(getClient()!=null && getClient().length()>0){
				pst = con.prepareStatement("select * from projectmntnc where client_id=? and approve_status='blocked'");
				pst.setInt(1,uF.parseToInt(getClient()));
				
			}else if(getF_service()!=null && getF_service().length()>0){
				pst = con.prepareStatement("select * from projectmntnc where service=? and approve_status='blocked'");
				pst.setString(1,getF_service());
				
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
				pst = con.prepareStatement("select * from projectmntnc where approve_status='blocked' and added_by = ?");
				pst.setInt(1, uF.parseToInt((String)session.get(EMPID)));
			}else{
				pst = con.prepareStatement("select * from projectmntnc where approve_status='blocked'");
			}*/
			
			StringBuilder sbQuery = new StringBuilder();			
			sbQuery.append("select * from projectmntnc where approve_status='blocked' ");			
			if(getF_service()!=null && getF_service().length>0){
				StringBuilder service=getConcateData(getF_service());
				sbQuery.append(" and service in ("+service+") ");
			}
			if(getClient()!=null && getClient().length>0){
				sbQuery.append(" and client_id in ("+StringUtils.join(getClient(), ",")+") ");
			}
			if(getSkill()!=null && getSkill().length>0){
				StringBuilder skill=getConcateData1(getSkill());
				sbQuery.append(" and pro_id in (select pro_id from project_skill_details where  skill_id in ("+ skill.toString() +"))");
			}
			if(getPro_id()!=null && getPro_id().length>0){
				sbQuery.append(" and pro_id in ("+StringUtils.join(getPro_id(), ",")+") ");
			}
			if(getManagerId()!=null && getManagerId().length>0){
				sbQuery.append(" and added_by in ("+StringUtils.join(getManagerId(), ",")+") ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "+uF.parseToInt((String)session.get(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.get(EMPID))+" ) ");
				
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			GetPriorityList objGP = new GetPriorityList();
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("pro_id"));
				alInner.add(rs.getString("pro_name"));
				alInner.add(uF.showData(objGP.getPriority(uF.parseToInt(rs.getString("priority"))), ""));
				
				alInner.add(hmProjectService.get(rs.getString("service")));
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("already_work_days")))+" days");
				} else {
					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" hrs");
					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("already_work")))+" hrs");
				}
				
				alInner.add(rs.getString("completed"));
				alInner.add((("n".equalsIgnoreCase(rs.getString("approve_status")))?"Pending":"Completed"));
				alInner.add(uF.showData(hmClientMap.get(rs.getString("client_id")), ""));
				
				hmProject.put(rs.getString("pro_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmProject", hmProject);
			
//			if(getPro_id()!=null && getPro_id().length>0){
//				pst = con.prepareStatement("select * from activity_info where pro_id  in ("+StringUtils.join(getPro_id(), ",")+") and is_milestone=?");
//				pst.setBoolean(1,false);
//			}else{
//				pst = con.prepareStatement("select * from activity_info where is_milestone=? order by pro_id");
//				pst.setBoolean(1,false);
//			}	
//			
//			rs = pst.executeQuery();
//			
//			
//			
////			pst = con.prepareStatement("select * from activity_info order by pro_id");
////			rs = pst.executeQuery();
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
			
			if(getPro_id()!=null && getPro_id().length>0){
				pst = con.prepareStatement("select pcmc.actual_calculation_type, ai.reassign_by, pcmc.billing_type, ai.pro_id, ai.task_id, ai.completed, ai.idealtime, ai.already_work_days, ai.already_work, ai.deadline, ai.end_date, ai.taskstatus, ai.approve_status, ai.finish_task, ai.activity_name, ai.priority, ai.emp_id from activity_info ai, projectmntnc pcmc where pcmc.pro_id = ai.pro_id and pcmc.pro_id in ("+StringUtils.join(getPro_id(), ",")+")  and is_milestone=?");
				pst.setBoolean(1,false);
			}else{
				pst = con.prepareStatement("select pcmc.actual_calculation_type, ai.reassign_by, pcmc.billing_type, ai.pro_id, ai.task_id, ai.completed, ai.idealtime, ai.already_work_days, ai.already_work, ai.deadline, ai.end_date, ai.taskstatus, ai.approve_status, ai.finish_task, ai.activity_name, ai.priority, ai.emp_id from activity_info ai, projectmntnc pcmc where pcmc.pro_id = ai.pro_id and is_milestone=? order by pcmc.pro_id");
				pst.setBoolean(1,false);
			}	
			rs = pst.executeQuery();
			
			String strProjectIdNew = null;
			String strProjectIdOld = null;
			List<String> alInner = new ArrayList<String>();
			while(rs.next()){
				
				strProjectIdNew = rs.getString("pro_id"); 
				if(strProjectIdNew!=null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)){
					alInner = new ArrayList<String>();
				}
				alInner.add(rs.getString("task_id"));
				alInner.add(rs.getString("activity_name"));
				alInner.add(rs.getString("priority"));
				
				alInner.add(uF.showData(hmEmployee.get(rs.getString("emp_id")), ""));
				alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				
				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
					alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("already_work_days")))+" days");
				} else {
					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" hrs");
					alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("already_work")))+" hrs");
				}
				
				alInner.add(uF.showData(rs.getString("completed"), "0"));
				
				hmTasks.put(rs.getString("pro_id"), alInner);
				
				strProjectIdOld = strProjectIdNew;
			}
			rs.close();
			pst.close();

			request.setAttribute("hmTasks", hmTasks);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private StringBuilder getConcateData(String[] data) {
		StringBuilder sb=new StringBuilder();
		
		for(int i=0;i<data.length;i++){
			if(i==0){
				sb.append("'"+data[i]+"'");
			}else{
				sb.append(",'"+data[i]+"'");
			}
		}
		
		return sb;
	}


	private StringBuilder getConcateData1(String[] data) {
		StringBuilder sb=new StringBuilder();
		
		for(int i=0;i<data.length;i++) {
			if(i==0){
				sb.append(data[i]);
			}else{
				sb.append(","+data[i]);
			}
		}
		
		return sb;
	}
	

	public List<Integer> getPro_IdsFromProList() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		List<Integer> p_id = new ArrayList<Integer>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select pro_id from projectmntnc order by deadline");
			rs = pst.executeQuery();
			while (rs.next()) {
				p_id.add(rs.getInt("pro_id"));
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
		return p_id;
	}

	public void getProjectNameToView() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select pro_id,pro_name from projectmntnc where approve_status!='n' order by deadline");
			rs = pst.executeQuery();
			int a = 1;
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

	public void getAllProjectDetails(List<Integer> p_id) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmServicemap = CF.getProjectServicesMap(con, true);
			
			getEmpName();
			for (int a = 0; a < p_id.size(); a++) {
				pst = con.prepareStatement("select * from projectmntnc where pro_id=? and approve_status!='n' order by pro_id");
				pst.setInt(1, p_id.get(a));
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> alInner = new ArrayList<String>();

					alInner.add("<b>" + rs.getString("pro_name") + "</b>");
					alInner.add("");
					alInner.add(rs.getString("priority"));
					String file = rs.getString("document_name");
					if (file != null)
						alInner.add("<a href='taskuploads/" + file
								+ "' target='blank'>" + file + "</a>");
					else
						alInner.add("No Attachments");
					alInner.add(hmServicemap.get(rs.getString("service")));
					alInner.add("");
					
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"view('"
							+ rs.getString("_comment") + "')\" >View</a>");
					alInner.add(uF.getDateFormat(rs.getString("deadline"),
							DBDATE, CF.getStrReportDateFormat()));
					alInner.add(rs.getString("idealtime"));
					alInner.add("" + rs.getDouble("already_work"));
					if (isSingle) {
						alInner.add("100%");
						alInner.add("");
					} else {
						if (uF.parseToDouble(rs.getString("completed")) < 100.0) {
							alInner.add("Completed");
						} else {
							alInner.add("Finished");
						}
						alInner.add("<a href='ProjectSummaryView.action?pro_id="
								+ p_id.get(a) + "'>View</a>");
					}
					al.put(rs.getInt(1), alInner);
					index.add(rs.getInt(1));
				}
				rs.close();
				pst.close();
				getActivityDetailForAll(p_id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getActivityDetailForAll(List<Integer> index) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<Integer, List<List<String>>> activityDetailMap = new HashMap<Integer, List<List<String>>>();
			for (int k = 0; k < index.size(); k++) {
				List<List<String>> outInner = new ArrayList<List<String>>();
				pst = con.prepareStatement("select * from activity_info where pro_id=? order by task_id");
				pst.setInt(1, index.get(k));
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> alInner = new ArrayList<String>();
					task_id = rs.getInt(1);
					alInner.add(rs.getString("activity_name"));
					alInner.add(rs.getString("priority"));
					String file = rs.getString("filename");
					if (file != null)
						alInner.add("<a href='taskuploads/" + file
								+ "' target='blank'>" + file + "</a>");
					else
						alInner.add("No Attachments");
					alInner.add("");
					alInner.add(EmpNameMap.get(rs.getInt("emp_id")));
					alInner.add("<a href=\"javascript:void(0)\" onclick=\"view('"
							+ rs.getString("_comment") + "')\" >View</a>");
					alInner.add(uF.getDateFormat(rs.getString("deadline"),
							DBDATE, CF.getStrReportDateFormat()));
					alInner.add("" + rs.getInt("idealtime"));
					double al_work = rs.getDouble("already_work");
					int per = uF.parseToInt(rs.getString("completed"));
					String isFinish = rs.getString("finish_task");
					String isApprove = rs.getString("approve_status");
					if (isFinish.equals("y") && isApprove.equals("n")) {
						alInner.add(0, "");
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
							alInner.add("Not Started");
							alInner.add("Not Started");
							alInner.add("Not Started");
						}
						totalhrz1 = new ArrayList<String>();
						taskStatus1 = new ArrayList<String>();
						time1 = new ArrayList<String>();
					} else {
						alInner.add(al_work + "hrs");
						alInner.add(per + "%");
						alInner.add("");
					}
					outInner.add(alInner);
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

	public void getEmpName() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from employee_personal_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				EmpNameMap.put(rs.getInt("emp_per_id"),rs.getString("emp_fname"));
			}
			rs.close();
			pst.close();
			System.out.println("emplist"+EmpNameMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public String[] getApprovePr() {
		return approvePr;
	}

	public void setApprovePr(String[] approvePr) {
		this.approvePr = approvePr;
	}

	public List<FillProjectList> getProjectdetailslist() {
		return projectdetailslist;
	}



	public void setProjectdetailslist(List<FillProjectList> projectdetailslist) {
		this.projectdetailslist = projectdetailslist;
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

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}



	public void getTimeDetail(int t_id) {
		Connection con = null;
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst1 = con.prepareStatement("select * from task_activity where activity_id=?");
			pst1.setInt(1, t_id);
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

	public List<String> getTotalhrz1() {
		return totalhrz1;
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

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
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

}
