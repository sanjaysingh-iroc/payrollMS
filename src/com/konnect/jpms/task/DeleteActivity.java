package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DeleteActivity extends ActionSupport implements ServletRequestAware, IStatements {
	
	HttpServletRequest request;
	CommonFunctions CF; 
	HttpSession session1;
	String strSessionEmpId;
	
	String activity_id;
	
	public String execute() {
		session1 = request.getSession();
		strSessionEmpId = (String)session1.getAttribute(EMPID);
		CF = (CommonFunctions) session1.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN; 
		
		deleteActivity();
		
		return SUCCESS;
	}
	
	
	public void deleteActivity(){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
			UtilityFunctions uF = new UtilityFunctions();
			con = db.makeConnection(con);
			String taskId = null;
			pst = con.prepareStatement("select activity_id from task_activity where task_id = ?");
			pst.setInt(1, uF.parseToInt(getActivity_id()));
			rs = pst.executeQuery();
			while (rs.next()) {
				taskId = rs.getString("activity_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("delete from  work_flow_details where effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id= ?");
			pst.setInt(1, uF.parseToInt(getActivity_id()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from task_activity where task_id=?");
			pst.setInt(1, uF.parseToInt(getActivity_id()));
			pst.execute();
			pst.close();
			
			insertintoActivityandProject(taskId, con);
			
			request.setAttribute("STATUS_MSG", "<span style=\"color: green; font-size: 10px; float: right; width: 200px;\">Activity deleted successfully!!!</span>");
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("STATUS_MSG", "<span style=\"color: red; font-size: 10px; float: right; width: 200px;\">Activity could not bedeleted!!!</span>");
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	private void insertintoActivityandProject(String activityID, Connection con) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			if (uF.parseToInt(activityID) > 0) {
				StringBuilder sumActivityQuery = new StringBuilder();

				sumActivityQuery.append("select sum(actual_hrs)as actual_hrs, count (distinct(task_date)) as actual_days from task_activity where emp_id = ? "
								+ " and activity_id=?");
				pst = con.prepareStatement(sumActivityQuery.toString());
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
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

				pst = con.prepareStatement("update activity_info set already_work=?, already_work_days=? where task_id=?");
				pst.setDouble(1, uF.parseToDouble(actual_hrs));
				pst.setDouble(2, uF.parseToDouble(actual_days));
				pst.setInt(3, uF.parseToInt(activityID));
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
				System.out.println("pst ==>> " + pst);
				rst = pst.executeQuery();
				while(rst.next()) {
					dblAllCompleted = rst.getDouble("completed");
					subTaskCnt = rst.getInt("count");
					taskActualHrs = rst.getString("already_work");
					taskActualDays = rst.getString("already_work_days");
				}
				rst.close();
				pst.close();
				
				System.out.println("taskActualHrs ==>> " + taskActualHrs);
				System.out.println("taskActualDays ==>> " + taskActualDays);
				
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
	
	
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}
	public String getActivity_id() {
		return activity_id;
	}
	public void setActivity_id(String activity_id) {
		this.activity_id = activity_id;
	}

}
