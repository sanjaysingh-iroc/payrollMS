package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateTaskPercentage extends ActionSupport implements IStatements, ServletRequestAware,ServletResponseAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	HttpSession session; 
	CommonFunctions CF;
	String divId;
	String proId;
	String strSessionEmpId;
	String strSessionOrgId;
	
	String complete;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		String taskId = request.getParameter("ID");
//		String proId = request.getParameter("proId");
		String percent = request.getParameter("percent");
		String strTaskId = request.getParameter("taskId");
		
		System.out.println("getComplete ==>> " + getComplete());
		if(percent!=null && taskId!=null) {
			updateCompletionPercent(taskId, percent);
			//request.setAttribute("STATUS_MSG", percent);
			if(getComplete() != null && getComplete().equals("complete")) {
				return "mysuccess";
			} else {
				return SUCCESS;
			}
		} else {
			getTaskStatus(strTaskId, proId);
			return LOAD;
		}
	}
	
	
	public void updateCompletionPercent(String taskId, String percent) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			int parentTaskId = 0; 
			pst = con.prepareStatement("select parent_task_id from activity_info where task_id = ? and parent_task_id != 0");
			pst.setInt(1, uF.parseToInt(taskId));
			rs = pst.executeQuery();
			while(rs.next()) {
				parentTaskId = rs.getInt("parent_task_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("update activity_info set completed=?, approve_status=?, end_date=? where task_id = ?");
			if(getComplete() != null && getComplete().equals("complete")) {
				pst.setDouble(1, 100);
				pst.setString(2, "approved");
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			} else {
				pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(percent))));
				pst.setString(2, "n");
				pst.setDate(3, null);
			}
//			pst.setDouble(1, uF.parseToDouble(percent));
			pst.setInt(4, uF.parseToInt(taskId));
			pst.execute();
			pst.close();
			
			
			if(uF.parseToDouble(percent)>= 100) {
				Map<String, String> hmTaskProData = CF.getTaskProInfo(con, taskId, null);
				
				Map<String, String> hmEmpData = hmEmpInfo.get(strSessionEmpId);
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_TASK_COMPLETED, CF); 
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId(strSessionOrgId);
				nF.setEmailTemplate(true);
				
				nF.setStrEmpId(strSessionEmpId);
				nF.setStrResourceFName(hmEmpData.get("FNAME"));
				nF.setStrResourceLName(hmEmpData.get("LNAME"));
				nF.setStrTaskName(hmTaskProData.get("TASK_NAME"));
				nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
				nF.setStrTeamLeader(hmTaskProData.get("TEAM_LEADER"));
				nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
				
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.sendNotifications();
			}
			
			double dblAllCompleted = 0.0d;
			int subTaskCnt = 0;
			pst = con.prepareStatement("select sum(completed) as completed, count(task_id) as count from activity_info where parent_task_id = ? and task_accept_status = 1");
			pst.setInt(1, parentTaskId);
//			pst.setInt(2, uF.parseToInt(taskId));
			rs = pst.executeQuery();
			while(rs.next()) {
				dblAllCompleted = rs.getDouble("completed");
				subTaskCnt = rs.getInt("count");
			}
			rs.close();
			pst.close();
			
//			subTaskCnt = subTaskCnt + 1; 
//			dblAllCompleted += uF.parseToDouble(percent);
			
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
			
			String pro_id = CF.getProjectIdByTaskId(con, taskId);
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
			
			String data =  percent+"::::"+"<div class=\"greenbox\" style=\"width:"+percent+"%;\"></div>";
			request.setAttribute("STATUS_MSG", data);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getTaskStatus(String taskId, String proId) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> hmTaskDetails = new HashMap<String, String>();
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, proId);

			pst = con.prepareStatement("select task_id, completed, resource_ids, activity_name from activity_info where task_id = ?");
			pst.setInt(1, uF.parseToInt(taskId));
			rs = pst.executeQuery();
//			int nEmpId = 0;
			while(rs.next()) {
				hmTaskDetails.put("TASK_ID", rs.getString("task_id"));
				hmTaskDetails.put("COMPLETED", uF.showData(rs.getString("completed"), "0"));
				hmTaskDetails.put("TASK_NAME", uF.showData(rs.getString("activity_name"), "-"));
				hmTaskDetails.put("EMP_NAME", uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-"));
//				nEmpId = uF.parseToInt(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			
//			pst = con.prepareStatement("select emp_fname, emp_lname from employee_personal_details where emp_per_id=?");
//			pst.setInt(1, nEmpId);
//			rs = pst.executeQuery();
//			
//			while(rs.next()) {
//				hmTaskDetails.put("EMP_NAME", rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
//			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		request.setAttribute("hmTaskDetails", hmTaskDetails);
	}
	
	
	public String getDivId() {
		return divId;
	}

	public void setDivId(String divId) {
		this.divId = divId;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getComplete() {
		return complete;
	}

	public void setComplete(String complete) {
		this.complete = complete;
	}

	HttpServletRequest request;
	
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
		
	}
}
