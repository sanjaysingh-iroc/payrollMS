package com.konnect.jpms.task.tax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SetTaskSubtaskType extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strSessionEmpId;
	
	String strOrg;
	String strOrgName;
	String taskTypeId;
	String forcedTask;
	String taskRequestAutoApproved;
	String operation;
	
	List<FillOrganisation> orgList;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		String existOrgId = getExistOrgId();
		orgList = new FillOrganisation(request).fillOrganisationWithoutCurrentOrgId(existOrgId);
		
		if (operation!=null && operation.equals("D")) {
			return deleteForcedTaskSetting(getTaskTypeId(), uF); 
		} 
		if (operation!=null && operation.equals("E")) { 
			return viewForcedTaskSetting(getTaskTypeId(), uF);
		}
		if (getTaskTypeId() != null && getTaskTypeId().length()>0) { 
			return updateForcedTaskSetting(uF);
		}
		
		if (getStrOrg() != null && getStrOrg().length()>0) {
			return insertForcedTaskSetting(uF);
		}
		
		return LOAD;
	}

	
	private String getExistOrgId() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		StringBuilder sbOrgIds = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select org_id from task_type_setting ");
			rs = pst.executeQuery();
			while(rs.next()) {
				if(sbOrgIds == null) {
					sbOrgIds = new StringBuilder();
					sbOrgIds.append(rs.getString("org_id"));
				} else {
					sbOrgIds.append(","+rs.getString("org_id"));
				}
			}
			rs.close();
			pst.close();
			if(sbOrgIds == null) {
				sbOrgIds = new StringBuilder();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return sbOrgIds.toString();

	}

	public String insertForcedTaskSetting(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("INSERT INTO task_type_setting(org_id,forced_task,task_request_autoapproved,added_by,entry_date) VALUES (?,?,?,?, ?)");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			pst.setBoolean(2, uF.parseToBoolean(getForcedTask()));
			pst.setBoolean(3, uF.parseToBoolean(getTaskRequestAutoApproved()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+CF.getOrgNameById(con, getStrOrg())+" task approval setting saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	
	public String viewForcedTaskSetting(String strId, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from task_type_setting where task_type_setting_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()) {
				setForcedTask(rs.getString("forced_task"));
				setTaskRequestAutoApproved(rs.getString("task_request_autoapproved"));
				setStrOrg(rs.getString("org_id"));
				setStrOrgName(CF.getOrgNameById(con, rs.getString("org_id")));
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
		return LOAD;

	}
	
	

	public String updateForcedTaskSetting(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update task_type_setting set forced_task=?, task_request_autoapproved=?,updated_by=?,update_date=? where task_type_setting_id=?");
			pst.setBoolean(1, uF.parseToBoolean(getForcedTask()));
			pst.setBoolean(2, uF.parseToBoolean(getTaskRequestAutoApproved()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt(getTaskTypeId()));
			pst.executeUpdate();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+CF.getOrgNameById(con, getStrOrg())+" task approval setting updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
//		if("LeaveTypeReport.action".equalsIgnoreCase(request.getParameter("URI"))){
//			return "success_redirect";
//		}
		return SUCCESS;

	}
	
	public String deleteForcedTaskSetting(String strId,UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from task_type_setting where task_type_setting_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Task approval setting deleted successfully."+END);
			
			//Delete Salary Heads related to the level.
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
//		if("LeaveTypeReport.action".equalsIgnoreCase(request.getParameter("URI"))){
//			return "success_redirect";
//		}
		return SUCCESS;

	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStrOrgName() {
		return strOrgName;
	}

	public void setStrOrgName(String strOrgName) {
		this.strOrgName = strOrgName;
	}

	public String getTaskTypeId() {
		return taskTypeId;
	}

	public void setTaskTypeId(String taskTypeId) {
		this.taskTypeId = taskTypeId;
	}

	public String getForcedTask() {
		return forcedTask;
	}

	public void setForcedTask(String forcedTask) {
		this.forcedTask = forcedTask;
	}


	public String getUserscreen() {
		return userscreen;
	}


	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}


	public String getNavigationId() {
		return navigationId;
	}


	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}


	public String getToPage() {
		return toPage;
	}


	public void setToPage(String toPage) {
		this.toPage = toPage;
	}


	public String getTaskRequestAutoApproved() {
		return taskRequestAutoApproved;
	}


	public void setTaskRequestAutoApproved(String taskRequestAutoApproved) {
		this.taskRequestAutoApproved = taskRequestAutoApproved;
	}
	
}