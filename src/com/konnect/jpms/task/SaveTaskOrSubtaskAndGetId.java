package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SaveTaskOrSubtaskAndGetId extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;

	CommonFunctions CF; 
	HttpSession session;
	String strSessionUserId;
	String strSessionEmpId;
	String strOrgId;
	String strUserType;
	 
	String proId;
	String taskName;
	String taskId;
	String taskTRId;
	String subTaskName;
	String subTaskId;
	String type;
	String count;
	String taskSubTaskId;
	String strTaskId;
	String operation;
	String fieldType;
	String fieldValue;
	
	List<FillDependentTaskList> dependencyList;
	List<GetDependancyTypeList> dependancyTypeList;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		strSessionUserId = (String)session.getAttribute(USERID);
		strSessionEmpId = (String)session.getAttribute(EMPID);
//		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strUserType = (String)session.getAttribute(USERTYPE);
		strOrgId = (String)session.getAttribute(ORGID);
		
		
//		System.out.println("getType() =====>> " + getType()+" -- getOperation() ===>> " + getOperation());
		UtilityFunctions uF = new UtilityFunctions();
		if(getType() != null && (getType().equals("Task") || getType().equals("VA_Task") || getType().equals("MP_Task")) && getOperation() !=null && getOperation().equals("OTHER_FIELDS")) {
			addTaskOtherDetails(uF);
		} else if(getType() != null && (getType().equals("Task") || getType().equals("VA_Task") || getType().equals("MP_Task"))) {
			addTaskName(uF);
		} else if(getType() != null && (getType().equals("SubTask") || getType().equals("VA_SubTask"))) {
			addSubTaskName(uF);
		} else if(getType() != null && getType().equals("MP_SubTask")) {
			addMySubTaskName(uF);
		} else if(getType() != null && (getType().equals("DelTask") || getType().equals("DelSubTask"))) {
			deleteTaskOrSubTask(uF);
		} else if(getType() != null && (getType().equals("GetTasks") || getType().equals("VA_GetTasks") || getType().equals("MP_GetTasks"))) {
			checkProjectFrequency(getProId());
			dependencyList = new FillDependentTaskList(request).fillDependentTaskList(uF.parseToInt(getProId()));
			getProTasks(uF, dependencyList);
		} else if(getType() != null && (getType().equals("GetSubTasks") || getType().equals("VA_GetSubTasks"))) {
			dependencyList = new FillDependentTaskList(request).fillDependentSubTaskList(uF.parseToInt(getProId()), uF.parseToInt(getTaskId()));
			getProSubTasks(uF, dependencyList);
		} else if(getType() != null && getType().equals("MP_GetSubTasks")) {
			String proId = getProjectIdByTaskId(getTaskId());
			checkProjectFrequency(proId);
			dependencyList = new FillDependentTaskList(request).fillDependentSubTaskList(uF.parseToInt(proId), uF.parseToInt(getTaskId()));
			getProSubTasks(uF, dependencyList);
		}
		
		return SUCCESS;

	}

	
	
	private void addTaskOtherDetails(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			StringBuilder sbque = new StringBuilder();
			sbque.append("update activity_info set task_id =?");
			if(getFieldType() !=null && getFieldType().equals("PROJECT")) {
				sbque.append(", pro_id="+uF.parseToInt(getFieldValue())+" ");
			} else if(getFieldType() !=null && getFieldType().equals("DEPENDTASK")) {
				sbque.append(", dependency_task="+uF.parseToInt(getFieldValue())+" ");
			} else if(getFieldType() !=null && getFieldType().equals("DEPENDTYPE")) {
				sbque.append(", dependency_type= '"+getFieldValue()+"' ");
			} else if(getFieldType() !=null && getFieldType().equals("PRIORITY")) {
				sbque.append(", priority= '"+getFieldValue()+"' ");
			} else if(getFieldType() !=null && getFieldType().equals("STARTDATE") && getFieldValue()!=null) {
				sbque.append(", start_date=? ");
			} else if(getFieldType() !=null && getFieldType().equals("DEADLINE") && getFieldValue()!=null) {
				sbque.append(", deadline=? ");
			} else if(getFieldType() !=null && getFieldType().equals("IDEALTIME")) {
				sbque.append(", idealtime= "+uF.parseToInt(getFieldValue())+" ");
			}
			sbque.append(" where task_id=?");
			pst = con.prepareStatement(sbque.toString());
			pst.setInt(1, uF.parseToInt(getTaskId()));
			if(getFieldType() !=null && (getFieldType().equals("STARTDATE") || getFieldType().equals("DEADLINE")) && getFieldValue()!=null) {
				pst.setDate(2, uF.getDateFormat(getFieldValue(), DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getTaskId()));
			} else {
				pst.setInt(2, uF.parseToInt(getTaskId()));
			}
//			System.out.println("pst ===>> " + pst);
			int x = pst.executeUpdate();
			pst.close();
//			System.out.println("X ===>> " + x);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	private void checkProjectFrequency(String proId) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			String freqVal = "0";
			pst = con.prepareStatement("select billing_kind from projectmntnc where pro_id=?"); //pro_id=? and 
			pst.setInt(1, uF.parseToInt(proId));
			rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("billing_kind") != null && !rs.getString("billing_kind").equalsIgnoreCase("O")) {
					freqVal = "1";
				}
			}
			rs.close();
			pst.close();
//			System.out.println(proId + "  -- freqVal ===>> " + freqVal);
			request.setAttribute("freqVal", freqVal);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	private String getProjectIdByTaskId(String taskId) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String proId = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select pro_id from activity_info where task_id=?"); //pro_id=? and 
			pst.setInt(1, uF.parseToInt(taskId));
			rs = pst.executeQuery();
			while(rs.next()) {
				proId = rs.getString("pro_id");
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
		return proId;
	}



	private void getProTasks(UtilityFunctions uF, List<FillDependentTaskList> dependencyList) {
		StringBuilder sbTaskOptions = new StringBuilder();
		StringBuilder hiddenTaskIdName = new StringBuilder();
		for(int i=0; dependencyList != null && i<dependencyList.size(); i++) {
			sbTaskOptions.append("<option value='"+dependencyList.get(i).getDependencyId()+"'>"+dependencyList.get(i).getDependencyName()+"</option>");
			hiddenTaskIdName.append("<input type=hidden name="+dependencyList.get(i).getDependencyId()+" id="+dependencyList.get(i).getDependencyId()+" value='"+dependencyList.get(i).getDependencyName()+"'/>");
		}
//		System.out.println("sbTaskOptions ====>>> " + sbTaskOptions.toString());
		request.setAttribute("hiddenTaskIdName", hiddenTaskIdName.toString());
		request.setAttribute("sbTaskOptions", sbTaskOptions.toString());
	}

	
	private void getProSubTasks(UtilityFunctions uF, List<FillDependentTaskList> dependencyList) {
		StringBuilder sbSubTaskOptions = new StringBuilder();
		for(int i=0; dependencyList != null && i<dependencyList.size(); i++) {
			sbSubTaskOptions.append("<option value='"+dependencyList.get(i).getDependencyId()+"'>"+dependencyList.get(i).getDependencyName()+"</option>");
		}
//		System.out.println("sbSubTaskOptions ====>>> " + sbSubTaskOptions.toString());
		request.setAttribute("sbSubTaskOptions", sbSubTaskOptions.toString());
	}

	
	private void deleteTaskOrSubTask(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from activity_info where task_id=?"); //pro_id=? and 
//			pst.setInt(1, uF.parseToInt(getProId()));
			pst.setInt(1, uF.parseToInt(getTaskId()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void addTaskName(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String taskRequestAutoApproved = null;
			Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, getProId());
			if(hmProjectData != null) {
				taskRequestAutoApproved = CF.getProjectTaskRequestAutoApproved(con, hmProjectData.get("PRO_ORG_ID"));
			}
			
			if(getTaskName() != null && !getTaskName().equals("")) {
				StringBuilder sbque = new StringBuilder();
				sbque.append("update activity_info set activity_name =?, pro_id=?, is_cust_add=?,task_accept_status=? ");
				if(strUserType !=null && (strUserType.equals(CUSTOMER) || strUserType.equals(EMPLOYEE))){
					sbque.append(", requested_by="+uF.parseToInt(strSessionEmpId)+" ");
					if(strUserType !=null && strUserType.equals(EMPLOYEE)) {
						sbque.append(", resource_ids= ',"+strSessionEmpId+",' ");
					}
				} else {
					sbque.append(",added_by="+uF.parseToInt(strSessionEmpId)+" ");
				}
				sbque.append(" where task_id=?");
//			pst = con.prepareStatement("update activity_info set activity_name =?, pro_id=?, is_cust_add=?,task_accept_status=? where task_id=?");
				pst = con.prepareStatement(sbque.toString());
				pst.setString(1, getTaskName());
				pst.setInt(2, uF.parseToInt(getProId()));
				if(strUserType !=null && strUserType.equals(CUSTOMER)) {
					pst.setInt(3, 1);
					pst.setInt(4, -2);
				} else {
					pst.setInt(3, 0);
					pst.setInt(4, (uF.parseToBoolean(taskRequestAutoApproved) || uF.parseToInt(getProId())==0) ? 1 : 0);
				}
				pst.setInt(5, uF.parseToInt(getTaskId()));
//				System.out.println("pst ===>> " + pst);
				int x = pst.executeUpdate();
				pst.close();
	//			System.out.println("X ===>> " + x);
				if(x == 0) {
					StringBuilder sbque1 = new StringBuilder();
					sbque1.append("insert into activity_info(pro_id,activity_name,is_cust_add,task_accept_status");
					if(strUserType !=null && (strUserType.equals(CUSTOMER) || strUserType.equals(EMPLOYEE))){
						sbque1.append(",requested_by");
						if(strUserType !=null && strUserType.equals(EMPLOYEE)) {
							sbque1.append(",resource_ids");
						}
					} else {
						sbque1.append(",added_by");
					}
					sbque1.append(") values (?,?,?,?, ?");
					if(strUserType !=null && strUserType.equals(EMPLOYEE)) {
						sbque1.append(",?");
					}
					sbque1.append(")");
//					pst = con.prepareStatement("insert into activity_info(pro_id,activity_name,is_cust_add,task_accept_status) values (?,?,?,?)");
					pst = con.prepareStatement(sbque1.toString());
					pst.setInt(1, uF.parseToInt(getProId()));
					pst.setString(2, getTaskName());
					if(strUserType !=null && strUserType.equals(CUSTOMER)) {
						pst.setInt(3, 1);
						pst.setInt(4, -2);
					} else {
						pst.setInt(3, 0);
						pst.setInt(4, (uF.parseToBoolean(taskRequestAutoApproved) || uF.parseToInt(getProId())==0) ? 1 : 0);
					}
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					if(strUserType !=null && strUserType.equals(EMPLOYEE)) {
						pst.setString(6, ","+strSessionEmpId+",");
					}
//					System.out.println("pst ===>> " + pst);
					pst.executeUpdate();
					pst.close();
					
					
					pst = con.prepareStatement("select max(task_id) as task_id from activity_info");
					rs = pst.executeQuery();
					while(rs.next()) {
						setTaskSubTaskId(rs.getString("task_id"));
					}
					rs.close();
					pst.close();
				} else {
					setTaskSubTaskId(getTaskId());
				}
		} else {
			if(uF.parseToInt(getTaskId()) > 0) {
				setTaskSubTaskId(getTaskId());
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
	
	
	private void addSubTaskName(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			if(getSubTaskName() != null && !getSubTaskName().equals("")) {
				StringBuilder sbque = new StringBuilder();
				sbque.append("update activity_info set activity_name =?, pro_id=?, parent_task_id=?,is_cust_add=?,task_accept_status=?");
				if(strUserType !=null && (strUserType.equals(CUSTOMER) || strUserType.equals(EMPLOYEE))){
					sbque.append(",requested_by="+uF.parseToInt(strSessionEmpId)+" ");
					if(strUserType !=null && strUserType.equals(EMPLOYEE)) {
						sbque.append(" ,resource_ids= ',"+strSessionEmpId+",' ");
					}
				} else {
					sbque.append(",added_by="+uF.parseToInt(strSessionEmpId)+" ");
				}
				sbque.append(" where task_id=?");
				pst = con.prepareStatement(sbque.toString());
//				pst = con.prepareStatement("update activity_info set activity_name =?, pro_id=?, parent_task_id=?,is_cust_add=?,task_accept_status=? where task_id=?");
				pst.setString(1, getSubTaskName());
				pst.setInt(2, uF.parseToInt(getProId()));
				pst.setInt(3, uF.parseToInt(getTaskId()));
				if(strUserType !=null && strUserType.equals(CUSTOMER)) {
					pst.setInt(4, 1);
					pst.setInt(5, -2);
				} else {
					pst.setInt(4, 0);
					pst.setInt(5, 0);
				}
				pst.setInt(6, uF.parseToInt(getSubTaskId()));
//				System.out.println("pst ===>> " + pst);
				int x = pst.executeUpdate();
				pst.close();
//				System.out.println("X ===>> " + x);
				if(x == 0) {
					StringBuilder sbque1 = new StringBuilder();
					sbque1.append("insert into activity_info(pro_id,parent_task_id,activity_name,is_cust_add,task_accept_status");
					if(strUserType !=null && (strUserType.equals(CUSTOMER) || strUserType.equals(EMPLOYEE))){
						sbque1.append(",requested_by");
						if(strUserType !=null && strUserType.equals(EMPLOYEE)) {
							sbque1.append(",resource_ids");
						}
					} else {
						sbque1.append(",added_by");
					}
					sbque1.append(") values(?,?,?,?, ?,?");
					if(strUserType !=null && strUserType.equals(EMPLOYEE)) {
						sbque1.append(",?");
					}
					sbque1.append(")");
					pst = con.prepareStatement(sbque1.toString());
//					pst = con.prepareStatement("insert into activity_info(pro_id,parent_task_id,activity_name,is_cust_add,task_accept_status) values (?,?,?,?, ?)");
					pst.setInt(1, uF.parseToInt(getProId()));
					pst.setInt(2, uF.parseToInt(getTaskId()));
					pst.setString(3, getSubTaskName());
					if(strUserType !=null && strUserType.equals(CUSTOMER)) {
						pst.setInt(4, 1);
						pst.setInt(5, -2);
					} else {
						pst.setInt(4, 0);
						pst.setInt(5, 0);
					}
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					if(strUserType !=null && strUserType.equals(EMPLOYEE)) {
						pst.setString(7, ","+strSessionEmpId+",");
					}
//					System.out.println("pst ====>>> " + pst);
					pst.executeUpdate();
					pst.close();

					pst = con.prepareStatement("select max(task_id) as task_id from activity_info");
					rs = pst.executeQuery();
					while(rs.next()) {
						setTaskSubTaskId(rs.getString("task_id"));
					}
					rs.close();
					pst.close();
				} else {
					setTaskSubTaskId(getSubTaskId());
				}
			} else {
				if(uF.parseToInt(getSubTaskId()) > 0) {
					setTaskSubTaskId(getSubTaskId());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void addMySubTaskName(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			String proId = getProjectIdByTaskId(getTaskId());
			if(getSubTaskName() != null && !getSubTaskName().equals("")) {
				pst = con.prepareStatement("update activity_info set activity_name =?, pro_id=?, parent_task_id=?,is_cust_add=?,task_accept_status=?" +
					",requested_by=? where task_id=?");
				pst.setString(1, getSubTaskName());
				pst.setInt(2, uF.parseToInt(proId));
				pst.setInt(3, uF.parseToInt(getTaskId()));
				if(strUserType !=null && strUserType.equals(CUSTOMER)) {
					pst.setInt(4, 1);
					pst.setInt(5, -2);
				} else {
					pst.setInt(4, 0);
					pst.setInt(5, 0);
				}
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
				pst.setInt(7, uF.parseToInt(getSubTaskId()));
//				System.out.println("pst ===>> " + pst);
				int x = pst.executeUpdate();
				pst.close();
//				System.out.println("X ===>> " + x);
				if(x == 0) {
					pst = con.prepareStatement("insert into activity_info(pro_id,parent_task_id,activity_name,is_cust_add,task_accept_status," +
						"requested_by) values (?,?,?,?, ?,?)");
					pst.setInt(1, uF.parseToInt(proId));
					pst.setInt(2, uF.parseToInt(getTaskId()));
					pst.setString(3, getSubTaskName());
					if(strUserType !=null && strUserType.equals(CUSTOMER)) {
						pst.setInt(4, 1);
						pst.setInt(5, -2);
					} else {
						pst.setInt(4, 0);
						pst.setInt(5, 0);
					}
					pst.setInt(6, uF.parseToInt(strSessionEmpId));
					pst.executeUpdate();
					pst.close();

					pst = con.prepareStatement("select max(task_id) as task_id from activity_info");
					rs = pst.executeQuery();
					while(rs.next()) {
						setTaskSubTaskId(rs.getString("task_id"));
					}
					rs.close();
					pst.close();
				} else {
					setTaskSubTaskId(getSubTaskId());
				}
			} else {
				if(uF.parseToInt(getSubTaskId()) > 0) {
					setTaskSubTaskId(getSubTaskId());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getSubTaskName() {
		return subTaskName;
	}

	public void setSubTaskName(String subTaskName) {
		this.subTaskName = subTaskName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getTaskSubTaskId() {
		return taskSubTaskId;
	}

	public void setTaskSubTaskId(String taskSubTaskId) {
		this.taskSubTaskId = taskSubTaskId;
	}

	public String getSubTaskId() {
		return subTaskId;
	}

	public void setSubTaskId(String subTaskId) {
		this.subTaskId = subTaskId;
	}

	public List<FillDependentTaskList> getDependencyList() {
		return dependencyList;
	}

	public void setDependencyList(List<FillDependentTaskList> dependencyList) {
		this.dependencyList = dependencyList;
	}

	public String getTaskTRId() {
		return taskTRId;
	}

	public void setTaskTRId(String taskTRId) {
		this.taskTRId = taskTRId;
	}

	public String getStrTaskId() {
		return strTaskId;
	}

	public void setStrTaskId(String strTaskId) {
		this.strTaskId = strTaskId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
}

