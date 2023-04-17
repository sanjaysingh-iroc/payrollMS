package com.konnect.jpms.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddNewActivity extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	private String activity_name;
	private String dependency;
	private String dependencyType;
	private String priority;
	private String empSkills;
	private String[] emp_id;
	private String comment;
	private String startDate;
	private String deadline;
	private String idealtime;
	private String colourCode;
	private int totalidealtime;
	private String task_id;
	private String sub_task_id;
	private String pro_id;
	private String type; 
	
	private String fromPage;
	String strSessionEmpId;
	
//	private String comment;
	 
//	private String startDate;
//	private int idealtime;
//	private String alreadyworked;
//	private String completed;
//	private File document;
//	private String documentContentType;
//	private String documentFileName;
	private String pro_deadline;
	private String pro_startDate;
	private String proBillingType;
//	private File[] document1;
//	private String[] document1FileName;
//	private String[] document1ContentType;
//	String colourCode;
//	String[] docid; 
//	String[] update_doc_title;
//	String[] doc_name1;
//	private int totalidealtime;
//	private String filename;
//	String filePath;
	private String start;
	private String TaskEmployeeId;
	private String TaskEmployeeName;
//	private String doc_name;
	String operation;
	
	HttpSession session;
	List<GetPriorityList> priorityList;
	private String priId;
	private String proName;
	List<GetDependancyTypeList> dependancyTypeList;
	
	CommonFunctions CF;
	UtilityFunctions uF = new UtilityFunctions();
	List<FillTaskEmpList> TaskEmpNamesList;
	List<FillDependentTaskList> dependencyList;
	List<FillSkills> empSkillList;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		if(getType() != null && getType().equals("SubTask")) {
			dependencyList = new FillDependentTaskList(request).fillDependentSubTaskList(uF.parseToInt(getPro_id()), uF.parseToInt(getTask_id()));
		} else {
			dependencyList = new FillDependentTaskList(request).fillDependentTaskList(uF.parseToInt(pro_id));
		}
		priorityList = new GetPriorityList().fillPriorityList();
		dependancyTypeList = new GetDependancyTypeList().fillDependancyTypeList();
		if(getFromPage() != null && getFromPage().equals("MP")) {
			TaskEmpNamesList = new ArrayList<FillTaskEmpList>();
			String strEmpName = getSessionEmpName(strSessionEmpId);
			TaskEmpNamesList.add(new FillTaskEmpList(strSessionEmpId, strEmpName));
		} else {
			TaskEmpNamesList = new FillTaskEmpList(request).fillEmployeeName(uF.parseToInt(pro_id));
		}
		
		String skillIds = getProjectEmpSkillIds(uF, uF.parseToInt(pro_id));
		empSkillList = new FillSkills(request).fillSkillNameByIds(skillIds);
		getProjectsDate();
		
//		System.out.println("get ====> " + getOperation());
//		System.out.println("getPro_id() ====> " + getPro_id());
//		System.out.println("getTask_id() ====> " + getTask_id());
//		System.out.println("getStart ====> " + getStart());
//		System.out.println("getFromPage ====> " + getFromPage());
		
		if (getOperation() != null && getOperation().equalsIgnoreCase("D")) {
			deleteTask();
//			System.out.println("get====> 1 ");
			if(getFromPage() != null && getFromPage().equals("MP")) {
				return "mpsuccess";
			} else {
				return SUCCESS;
			}
		} else if (getOperation() != null && getOperation().equalsIgnoreCase("E")) {
			if(getStart() == null) {
//				System.out.println("get====> 2 ");
				getTaskDetails(uF);
				if(getFromPage() != null && getFromPage().equals("MP")) {
					return "mpsuccess";
				} else {
					return SUCCESS;
				}
			} else {
//				System.out.println("get====> 3 ");
				updateActivityDetails();
				if(getFromPage() != null && getFromPage().equals("MP")) {
					return "mpsuccess";
				} else {
					return SUCCESS;
				}
			}
		} else if (getOperation() == null) {
			setOperation("I");
//			System.out.println("get====> 4 ");
			if(getFromPage() != null && getFromPage().equals("MP")) {
				return "mpsuccess";
			} else {
				return SUCCESS;
			}
		} else if (getOperation() != null && getOperation().equalsIgnoreCase("I")) {
//			System.out.println("get====> 5 ");
			insertActivityDetails();
			if(getFromPage() != null && getFromPage().equals("MP")) {
				return "mpsuccess";
			} else {
				return SUCCESS;
			}
		} else {
			if(getFromPage() != null && getFromPage().equals("MP")) {
				return "mpsuccess";
			} else {
				return SUCCESS;
			}
		}

	}
	
	
	private String getSessionEmpName(String strSessionEmpId) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		String empName = null;
		try {
			con = db.makeConnection(con);
			empName =CF.getEmpNameMapByEmpId(con, strSessionEmpId);
//			System.out.println("sbSkillIds ===>>> " + sbSkillIds);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		return empName;
	}


	private String getProjectEmpSkillIds(UtilityFunctions uF, int proId) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sbSkillIds = null;
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbEmpIds = null;
			List<String> alEmpID = new ArrayList<String>();
			pst = con.prepareStatement("select emp_id from project_emp_details where pro_id=?");
			pst.setInt(1, proId);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(!alEmpID.contains(rs.getString("emp_id"))) {
					if(sbEmpIds == null) {
						sbEmpIds = new StringBuilder();
						sbEmpIds.append(rs.getString("emp_id"));
					} else {
						sbEmpIds.append(","+rs.getString("emp_id"));
					}
					alEmpID.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbEmpIds == null) {
				sbEmpIds = new StringBuilder();
			}
			
//			System.out.println("sbEmpIds ===>>> " + sbEmpIds);
			if(sbEmpIds.length()>0) {
				List<String> alSkillIds = new ArrayList<String>();
				pst = con.prepareStatement("select skill_id from skills_description where emp_id in ("+sbEmpIds.toString()+")");
				rs = pst.executeQuery();
				while(rs.next()) {
					if(!alSkillIds.contains(rs.getString("skill_id"))) {
						if(sbSkillIds == null) {
							sbSkillIds = new StringBuilder();
							sbSkillIds.append(rs.getString("skill_id"));
						} else {
							sbSkillIds.append(","+rs.getString("skill_id"));
						}
						alSkillIds.add(rs.getString("skill_id"));
					}
				}
				rs.close();
				pst.close();
			}
			if(sbSkillIds == null) {
				sbSkillIds = new StringBuilder();
			}
//			System.out.println("sbSkillIds ===>>> " + sbSkillIds);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return sbSkillIds.toString();
	}
	
	
	public void getProjectsDate() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select deadline,start_date,actual_calculation_type from projectmntnc where pro_id=?");
			pst.setInt(1,uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				pro_deadline = uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT);
				pro_startDate = uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT);
				proBillingType = rs.getString("actual_calculation_type");
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
	public void deleteTask() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			int parentTaskId = 0; 
			pst = con.prepareStatement("select parent_task_id from activity_info where task_id = ? and parent_task_id != 0");
			pst.setInt(1, uF.parseToInt(task_id));
			rs = pst.executeQuery();
			while(rs.next()) {
				parentTaskId = rs.getInt("parent_task_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("DELETE FROM activity_info WHERE task_id=?");
			pst.setInt(1, uF.parseToInt(task_id));
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
			
			String pro_id = CF.getProjectIdByTaskId(con, task_id);
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

	
	
	
	public void getTaskDetails(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from activity_info where task_id = ? ");
			if(uF.parseToInt(getSub_task_id()) > 0) {
				pst.setInt(1, uF.parseToInt(getSub_task_id()));
			} else {
				pst.setInt(1, uF.parseToInt(getTask_id()));
			}
//			System.out.println("pst======main==="+pst);
			rs = pst.executeQuery();
			List<String> alTaskData = new ArrayList<String>();
			while (rs.next()) {
				alTaskData.add(rs.getString("task_id")); //0
				alTaskData.add(rs.getString("parent_task_id")); //1
				alTaskData.add(rs.getString("pro_id")); //2
				alTaskData.add(uF.showData(rs.getString("activity_name"), "")); //3
				if(uF.parseToInt(getSub_task_id()) > 0) {
					List<FillDependentTaskList> subDependencyList = new FillDependentTaskList(request).fillDependentSubTaskList(uF.parseToInt(getPro_id()), uF.parseToInt(rs.getString("parent_task_id")));
					String subDependencyTask = getDependencyTaskOptions(rs.getString("task_id"), rs.getString("dependency_task"), subDependencyList);
					alTaskData.add(uF.showData(subDependencyTask, "")); //4
				} else {
					String dependencyTask = getDependencyTaskOptions(rs.getString("task_id"), rs.getString("dependency_task"), dependencyList);
					alTaskData.add(uF.showData(dependencyTask, "")); //4
				}
				alTaskData.add(uF.showData(rs.getString("dependency_type"), "")); //5
				alTaskData.add(uF.showData(rs.getString("priority"), ""));  //6
				alTaskData.add(uF.showData(rs.getString("task_skill_id"), "")); //7
				String taskEmps = getTaskEmployee(rs.getString("resource_ids"), TaskEmpNamesList);
				alTaskData.add(uF.showData(taskEmps, "")); //8
				alTaskData.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));  //9
				alTaskData.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT)); //10
				alTaskData.add(uF.showData(rs.getString("idealtime"), "")); //11
				alTaskData.add(uF.showData(rs.getString("color_code"), "")); //12
				String timeFilledEmp = getTimesheetFilledEmp(con, rs.getString("resource_ids"), rs.getString("task_id"));
				alTaskData.add(uF.showData(timeFilledEmp, "")); //13
			}
			rs.close();
			pst.close();
			
//			System.out.println("alTaskData ===>> " + alTaskData);
			request.setAttribute("alTaskData", alTaskData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private String getTimesheetFilledEmp(Connection con, String resourceIds, String taskId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder timeFilledEmp = null;
		try {
			if(resourceIds != null && resourceIds.length() > 1) {
				resourceIds = resourceIds.substring(1, resourceIds.length()-1);
				if(!resourceIds.trim().equals("") && resourceIds.length() > 1) {
					pst = con.prepareStatement("select * from task_activity where emp_id in("+resourceIds+") and activity_id=?");
					pst.setInt(1, uF.parseToInt(taskId));
	//				System.out.println("pst======main===" + pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						if(timeFilledEmp == null) {
							timeFilledEmp = new StringBuilder();
							timeFilledEmp.append(rs.getString("emp_id"));
						} else {
							timeFilledEmp.append(","+rs.getString("emp_id"));
						}
					}
					rs.close();
					pst.close();
					
					if(timeFilledEmp == null) {
						timeFilledEmp = new StringBuilder();
					}
	//				System.out.println("timeFilledEmp ===>> " + timeFilledEmp.toString());
				}
			}
			if(timeFilledEmp == null) {
				timeFilledEmp = new StringBuilder();
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
		return timeFilledEmp.toString();
		
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


//	public void getTaskDetails() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from activity_info where task_id=?");
//			pst.setInt(1, uF.parseToInt(getTask_id()));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				setActivity_name(rs.getString("activity_name"));
//				setActivity_priority(rs.getString("priority"));
//				setEmp_id(rs.getString("emp_id"));
//				setComment(rs.getString("_comment"));
//				// setDeadline(rs.getString("deadline"));
//				setDeadline(uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
//				setIdealtime(uF.parseToInt(rs.getString("idealtime")));
//				setStartDate(uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
//				setTask_dependency(rs.getString("dependency_task"));
//				setDependency_type(rs.getString("dependency_type"));
////				System.out.println("Color Code===>"+rs.getString("color_code"));
//				colourCode = rs.getString("color_code");
//			}
//			
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from project_documents_details where task_id=?");
//			pst.setInt(1, uF.parseToInt(getTask_id()));
//			rs = pst.executeQuery();
//			StringBuilder sb = new StringBuilder();
//			StringBuilder sb1 = new StringBuilder();
//			StringBuilder sb2 = new StringBuilder();
//						
//			int i=0;
//			while(rs.next()){	
//				if(i==0){
//					sb.append(rs.getString("doc_id"));		
//					sb1.append(rs.getString("doc_path"));
//					sb2.append(rs.getString("doc_name"));
//					
//				}else{
//					sb.append(","+rs.getString("doc_id"));		
//					sb1.append(","+rs.getString("doc_path"));
//					sb2.append(","+rs.getString("doc_name"));
//				}
//				i++;
//			}
//			
//			request.setAttribute("docSrNo", sb.toString());
//			request.setAttribute("docName", sb1.toString());
//			request.setAttribute("docTitle", sb2.toString());
//			
//			
//			operation = "U";
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}

	public void updateActivityDetails() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			StringBuilder taskResourceIds = new StringBuilder();
			pst = con.prepareStatement("select resource_ids from activity_info where parent_task_id=? and task_id != ?");
			pst.setInt(1, uF.parseToInt(getTask_id()));
			pst.setInt(2, uF.parseToInt(getSub_task_id()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				taskResourceIds.append(rs.getString("resource_ids"));
			}
			rs.close();
			pst.close();
			
			List<String> alTaskResources = Arrays.asList(taskResourceIds.toString().split(","));
			List<String> taskResources = new ArrayList<String>();
			for (int aa = 0; alTaskResources != null && aa < alTaskResources.size(); aa++) {
				if(!taskResources.contains(alTaskResources.get(aa)) && !alTaskResources.get(aa).equals("") && !alTaskResources.get(aa).equals("null")) {
					taskResources.add(alTaskResources.get(aa));
				}
			}
//			System.out.println("alTaskResources ===>> " + alTaskResources);
			
			StringBuilder sbEmpIds = null;
//			System.out.println("sub_emp_id ===>> " + sub_emp_id);
			if (getEmp_id() != null && getEmp_id().length > 0) {
				List<String> empIdList = Arrays.asList(getEmp_id());
				for (int a = 0; empIdList != null && a < empIdList.size(); a++) {
					if(uF.parseToInt(getSub_task_id()) > 0) {
						if(!taskResources.contains(empIdList.get(a).trim())) {
							if(!empIdList.get(a).trim().equals("") && !empIdList.get(a).trim().equals("null")) {
								taskResources.add(empIdList.get(a).trim());
							}
						}
					}
					if (sbEmpIds == null) {
						sbEmpIds = new StringBuilder();
						sbEmpIds.append("," + empIdList.get(a).trim()+",");
					} else {
						sbEmpIds.append(empIdList.get(a).trim()+",");
					}
				}
			}
			if (sbEmpIds == null) {
				sbEmpIds = new StringBuilder();
			}
			
			pst = con.prepareStatement("UPDATE activity_info SET activity_name=?,priority=?,resource_ids=?,_comment=?,start_date=?,deadline=?," +
				"idealtime=?,dependency_task=?,dependency_type=?,color_code=?, task_skill_id=? WHERE task_id=?");
			pst.setString(1, getActivity_name());
			pst.setString(2, getPriority());
			pst.setString(3, sbEmpIds.toString());
			pst.setString(4, getComment());
			pst.setDate(5, uF.getDateFormat(getStartDate(), DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(getDeadline(), DATE_FORMAT));
			pst.setInt(7, uF.parseToInt(getIdealtime()));
			pst.setInt(8, uF.parseToInt(getDependency()));
			pst.setString(9, getDependencyType());
			pst.setString(10, getColourCode());
			pst.setInt(11, uF.parseToInt(getEmpSkills()));
			if(uF.parseToInt(getSub_task_id()) > 0) {
				pst.setInt(12, uF.parseToInt(getSub_task_id()));
			} else {
				pst.setInt(12, uF.parseToInt(getTask_id()));
			}
//			System.out.println("pst ===>> " + pst);
			pst.executeUpdate();
			pst.close();

			
			if(uF.parseToInt(getSub_task_id()) > 0) {
				StringBuilder sbTEmpIds = null;
				for (int a = 0; taskResources != null && a < taskResources.size(); a++) {
					if(!taskResources.get(a).equals("")) {							
						if (sbTEmpIds == null) {
							sbTEmpIds = new StringBuilder();
							sbTEmpIds.append("," + taskResources.get(a)+",");
						} else {
							sbTEmpIds.append(taskResources.get(a)+",");
						}
					}
				}
				if (sbTEmpIds == null) {
					sbTEmpIds = new StringBuilder();
				}
	//			System.out.println("sbTEmpIds ===>> " + sbTEmpIds.toString());
				
				pst = con.prepareStatement("update activity_info set resource_ids=? where task_id=?");
				pst.setString(1, sbTEmpIds.toString());
				pst.setInt(2, uF.parseToInt(getTask_id()));
				pst.executeUpdate();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void insertActivityDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);			
			StringBuilder taskResourceIds = new StringBuilder();
			pst = con.prepareStatement("select resource_ids from activity_info where parent_task_id=?");
			pst.setInt(1, uF.parseToInt(getTask_id()));
//			pst.setInt(2, uF.parseToInt(subTaskID[k]));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				taskResourceIds.append(rs.getString("resource_ids"));
			}
			rs.close();
			pst.close();
			
			List<String> alTaskResources = Arrays.asList(taskResourceIds.toString().split(","));
			List<String> taskResources = new ArrayList<String>();
			for (int aa = 0; alTaskResources != null && aa < alTaskResources.size(); aa++) {
				if(!taskResources.contains(alTaskResources.get(aa)) && !alTaskResources.get(aa).equals("") && !alTaskResources.get(aa).equals("null")) {
					taskResources.add(alTaskResources.get(aa));
				}
			}
//			System.out.println("alTaskResources ===>> " + alTaskResources);
			
			StringBuilder sbEmpIds = null;
//			System.out.println("sub_emp_id ===>> " + sub_emp_id);
			if (getEmp_id() != null && getEmp_id().length > 0) {
				List<String> empIdList = Arrays.asList(getEmp_id());
				for (int a = 0; empIdList != null && a < empIdList.size(); a++) {
					if(!taskResources.contains(empIdList.get(a).trim())) {
						if(!empIdList.get(a).trim().equals("") && !empIdList.get(a).trim().equals("null")) {
							taskResources.add(empIdList.get(a).trim());
						}
					}
					
					if (sbEmpIds == null) {
						sbEmpIds = new StringBuilder();
						sbEmpIds.append("," + empIdList.get(a).trim()+",");
					} else {
						sbEmpIds.append(empIdList.get(a).trim()+",");
					}
				}
			}
			if (sbEmpIds == null) {
				sbEmpIds = new StringBuilder();
			}
			
			pst = con.prepareStatement("insert into activity_info(pro_id,activity_name,priority,resource_ids,_comment,deadline,idealtime,already_work," +
				"completed,timestatus,approve_status,taskstatus,start_date,dependency_task,dependency_type,color_code,task_skill_id,parent_task_id," +
				"task_from_my_self) values (?,?,?,?, ?,?,?,'0', '0','n','n',?, ?,?,?,?, ?,?,?)");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			pst.setString(2, getActivity_name());
			pst.setString(3, getPriority());
			pst.setString(4, sbEmpIds.toString());
			pst.setString(5, getComment());
			pst.setDate(6, uF.getDateFormat(getDeadline(), DATE_FORMAT));
			pst.setInt(7, uF.parseToInt(getIdealtime()));
			if(getType() != null && getType().equals("SubTask")) {
				pst.setString(8, "New Sub Task");
			} else {
				pst.setString(8, "New Task");
			}
			pst.setDate(9, uF.getDateFormat(getStartDate(), DATE_FORMAT));
			pst.setInt(10, uF.parseToInt(getDependency()));
			pst.setString(11, getDependencyType());
			pst.setString(12, getColourCode());
			pst.setInt(13, uF.parseToInt(getEmpSkills()));
			if(getType() != null && getType().equals("SubTask")) {
				pst.setInt(14, uF.parseToInt(getTask_id()));
			} else {
				pst.setInt(14, 0);
			}
			if(getFromPage() != null && getFromPage().equals("MP")) {
				pst.setBoolean(15, true);
			} else {
				pst.setBoolean(15, false);
			}
			pst.executeUpdate();
			pst.close();

			
			if(getType() != null && getType().equals("SubTask")) {
				StringBuilder sbTEmpIds = null;
				for (int a = 0; taskResources != null && a < taskResources.size(); a++) {
					if(!taskResources.get(a).equals("")) {							
						if (sbTEmpIds == null) {
							sbTEmpIds = new StringBuilder();
							sbTEmpIds.append("," + taskResources.get(a)+",");
						} else {
							sbTEmpIds.append(taskResources.get(a)+",");
						}
					}
				}
				if (sbTEmpIds == null) {
					sbTEmpIds = new StringBuilder();
				}
	//			System.out.println("sbTEmpIds ===>> " + sbTEmpIds.toString());
				
				pst = con.prepareStatement("update activity_info set resource_ids=? where task_id=?");
				pst.setString(1, sbTEmpIds.toString());
				pst.setInt(2, uF.parseToInt(getTask_id()));
				pst.executeUpdate();
				pst.close();
				
				
				double dblAllCompleted = 0.0d;
				int subTaskCnt = 0;
				pst = con.prepareStatement("select sum(completed) as completed, count(task_id) as count from activity_info where parent_task_id = ? and task_accept_status = 1");
				pst.setInt(1, uF.parseToInt(getTask_id()));
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
					pst.setInt(2, uF.parseToInt(getTask_id()));
					pst.execute();
					pst.close();
				}
				
			}
			
			pst = con.prepareStatement("select sum(completed)/count(task_id) as avrg from activity_info where pro_id=? and parent_task_id = 0 and task_accept_status = 1");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			String projectCompletePercent = null;
			while (rs.next()) {
				projectCompletePercent = uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rs.getString("avrg")));
			}
			rs.close();
			pst.close();
	
			pst = con.prepareStatement("update projectmntnc set completed=? where pro_id=? ");
			pst.setDouble(1, uF.parseToDouble(projectCompletePercent));
			pst.setInt(2, uF.parseToInt(getPro_id()));
			pst.execute();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		setOperation(null);
		selectTotalTime();
//		insertDocuments();
	}

	

	public String upload(File file, String fileFileName) throws Exception {
		
		double randomname = Math.random();
        String random = randomname + "";
        random = random.replace("0.", "");
        if (fileFileName.contains(" ")) {
            fileFileName = fileFileName.replace(" ", "");
        }
        // the directory to upload to
        String uploadDir = ServletActionContext.getServletContext()
                .getRealPath("/taskuploads") + "/";

        // write the file to the file specified
        File dirPath = new File(uploadDir);

        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }

        // retrieve the file data
        InputStream stream = new FileInputStream(file);

        // write the file to the file specified
        OutputStream bos = new FileOutputStream(uploadDir + random
                + fileFileName);
        int bytesRead;
        byte[] buffer = new byte[8192];

        while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }

        bos.close();
        stream.close();
        // place the data into the request for retrieval on next page
        request.setAttribute("location", dirPath.getAbsolutePath() + "/"
                + fileFileName);
        
        return "taskuploads/"+random + fileFileName;

	}
	
	
	
	public void selectTotalTime() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select sum(idealtime) as totalidealtime from activity_info where pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				totalidealtime = rs.getInt("totalidealtime");
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
		updateTimeOfProject();
	}

	
	
	public void updateTimeOfProject() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE projectmntnc SET idealtime=? WHERE pro_id =?");
			pst.setInt(1, totalidealtime);
			pst.setInt(2, uF.parseToInt(getPro_id()));
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

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getColourCode() {
		return colourCode;
	}
	public void setColourCode(String colourCode) {
		this.colourCode = colourCode;
	}
	public List<GetDependancyTypeList> getDependancyTypeList() {
		return dependancyTypeList;
	}
	public void setDependancyTypeList(List<GetDependancyTypeList> dependancyTypeList) {
		this.dependancyTypeList = dependancyTypeList;
	}
	
	public String getPro_startDate() {
		return pro_startDate;
	}
	
	public void setPro_startDate(String pro_startDate) {
		this.pro_startDate = pro_startDate;
	}
	
	public List<FillTaskEmpList> getTaskEmpNamesList() {
		return TaskEmpNamesList;
	}

	public void setTaskEmpNamesList(List<FillTaskEmpList> taskEmpNamesList) {
		TaskEmpNamesList = taskEmpNamesList;
	}

	public String getTaskEmployeeId() {
		return TaskEmployeeId;
	}

	public void setTaskEmployeeId(String taskEmployeeId) {
		TaskEmployeeId = taskEmployeeId;
	}

	public String getTaskEmployeeName() {
		return TaskEmployeeName;
	}

	public List<GetPriorityList> getPriorityList() {
		return priorityList;
	}

	public void setPriorityList(List<GetPriorityList> priorityList) {
		this.priorityList = priorityList;
	}

	public String getPriId() {
		return priId;
	}

	public void setPriId(String priId) {
		this.priId = priId;
	}

	public List<FillDependentTaskList> getDependencyList() {
		return dependencyList;
	}

	public void setDependencyList(List<FillDependentTaskList> dependencyList) {
		this.dependencyList = dependencyList;
	}

	public String getPro_deadline() {
		return pro_deadline;
	}
	public void setPro_deadline(String pro_deadline) {
		this.pro_deadline = pro_deadline;
	}
	
	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public void setTaskEmployeeName(String taskEmployeeName) {
		TaskEmployeeName = taskEmployeeName;
	}

	private HttpServletRequest request;

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getActivity_name() {
		return activity_name;
	}

	public void setActivity_name(String activity_name) {
		this.activity_name = activity_name;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public List<FillSkills> getEmpSkillList() {
		return empSkillList;
	}

	public void setEmpSkillList(List<FillSkills> empSkillList) {
		this.empSkillList = empSkillList;
	}

	public String getSub_task_id() {
		return sub_task_id;
	}

	public void setSub_task_id(String sub_task_id) {
		this.sub_task_id = sub_task_id;
	}

	public String getPro_id() {
		return pro_id;
	}

	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}


	public String getDependency() {
		return dependency;
	}


	public void setDependency(String dependency) {
		this.dependency = dependency;
	}


	public String getDependencyType() {
		return dependencyType;
	}

	public void setDependencyType(String dependencyType) {
		this.dependencyType = dependencyType;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getEmpSkills() {
		return empSkills;
	}

	public void setEmpSkills(String empSkills) {
		this.empSkills = empSkills;
	}

	public String[] getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String[] emp_id) {
		this.emp_id = emp_id;
	}

	public String getIdealtime() {
		return idealtime;
	}

	public void setIdealtime(String idealtime) {
		this.idealtime = idealtime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getTotalidealtime() {
		return totalidealtime;
	}

	public void setTotalidealtime(int totalidealtime) {
		this.totalidealtime = totalidealtime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProBillingType() {
		return proBillingType;
	}

	public void setProBillingType(String proBillingType) {
		this.proBillingType = proBillingType;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	
}
