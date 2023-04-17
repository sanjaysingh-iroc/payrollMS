package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddPredefinedTaskForService extends ActionSupport implements ServletRequestAware, IStatements {
	HttpServletRequest request;

	CommonFunctions CF; 
	HttpSession session;
	String strSessionEmpId;
	String strOrgId;
	
	String strTaskName;
	String strTaskDesc; 
	String operation;
	String strServiceTaskId;
	String strServiceId;

	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strOrgId = (String)session.getAttribute(ORGID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/task/AddTaskService.jsp");
		request.setAttribute(TITLE, "Add New Project");
		
		if(operation == null) {
			operation = "A";
		} else if(operation !=null && operation.equals("A")) {
			insertServiceTask();
			return "update";
		} else if(operation !=null && operation.equals("E")) {
			getServiceTask();
		} else if(operation !=null && operation.equals("U")) {
			updateServiceTask();
			return "update";
		} else if(operation !=null && operation.equals("D")) {
			deleteServiceTask();
			return "update";
		}
			
		return SUCCESS;
	}
	
	public void getServiceTask() {

				UtilityFunctions uF=new UtilityFunctions();
				Database db = new Database();
				db.setRequest(request);
				Connection con = null;
				PreparedStatement pst=null;
				ResultSet rs=null;
				try {
					setOperation("U");
					con = db.makeConnection(con);
					pst = con.prepareStatement("select * from service_tasks_details where service_task_id=? ");
					pst.setInt(1, uF.parseToInt(getStrServiceTaskId()));
					rs=pst.executeQuery();
					while(rs.next()) {
						setStrTaskName(rs.getString("task_name"));
						setStrTaskDesc(rs.getString("task_description"));
					}
					rs.close();
					pst.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					db.closeResultSet(rs);
					db.closeStatements(pst);
					db.closeConnection(con);
				}
		}
	
	
	public void updateServiceTask() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update service_tasks_details set task_name=?, task_description=?, updated_by=?, update_date=? where service_task_id=?");
			pst.setString(1, getStrTaskName());
			pst.setString(2, getStrTaskDesc());
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(4, uF.getTimeStamp(""+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(5, uF.parseToInt(getStrServiceTaskId()));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public void deleteServiceTask() {
		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from service_tasks_details where service_task_id=?");
			pst.setInt(1, uF.parseToInt(getStrServiceTaskId()));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	
	}
	
	public void insertServiceTask() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		
		try {

			con = db.makeConnection(con);
			boolean flag = false;
			pst = con.prepareStatement("select * from service_tasks_details where upper(task_name)=? and service_id=?");
			pst.setString(1, getStrTaskName().toUpperCase());
			pst.setInt(2, uF.parseToInt(getStrServiceId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			if(!flag) {
				pst = con.prepareStatement("insert into service_tasks_details(task_name,task_description,service_id,added_by,entry_date) values(?,?,?,?, ?) ");
				pst.setString(1, getStrTaskName());
				pst.setString(2, getStrTaskDesc());
				pst.setInt(3, uF.parseToInt(getStrServiceId()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(5, uF.getTimeStamp(""+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.execute();
				pst.close();
			}
//			System.out.println("Skill ==> "+getSkill());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}

	public String getStrTaskName() {
		return strTaskName;
	}

	public void setStrTaskName(String strTaskName) {
		this.strTaskName = strTaskName;
	}

	public String getStrTaskDesc() {
		return strTaskDesc;
	}

	public void setStrTaskDesc(String strTaskDesc) {
		this.strTaskDesc = strTaskDesc;
	}

	public String getStrServiceTaskId() {
		return strServiceTaskId;
	}

	public void setStrServiceTaskId(String strServiceTaskId) {
		this.strServiceTaskId = strServiceTaskId;
	}

	public String getStrServiceId() {
		return strServiceId;
	}
	
	public void setStrServiceId(String strServiceId) {
		this.strServiceId = strServiceId;
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
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
	
}
