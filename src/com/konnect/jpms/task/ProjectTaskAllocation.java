package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectTaskAllocation extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	
	int emp_id;
	String proStartDate;
	String proEndDate;
	

	public String execute() {
		session = request.getSession();
		CommonFunctions CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		getEmpTaskAllocationSumary(uF, CF);
			
		return SUCCESS;
	}
	
	public void getEmpTaskAllocationSumary(UtilityFunctions uF, CommonFunctions CF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
			
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id = ?");
			pst.setInt(1, getEmp_id());
			rs = pst.executeQuery();
			String strEmpName = null;
			while(rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				strEmpName = rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname");
			}
			rs.close();
			pst.close();
			Map<String, String> hmClientDetails = CF.getProjectClientMap(con, uF);
			
			/*pst = con.prepareStatement("select a.*, pmc.actual_calculation_type, pmc.pro_name, pmc.client_id, pmc.billing_type from (" +
					"select task_id, activity_name, parent_task_id, pro_id, start_date, deadline, idealtime, already_work," +
					"completed from activity_info where resource_ids like '%,"+getEmp_id()+",%' and task_id not in (select parent_task_id from " +
					"activity_info where resource_ids like '%,"+getEmp_id()+",%' and parent_task_id is not null)) a, projectmntnc pmc  where " +
					"pmc.pro_id=a.pro_id and pmc.approve_status='n' and (parent_task_id in (select task_id from activity_info where resource_ids " +
					" like '%,"+getEmp_id()+",%') or parent_task_id = 0) and (a.completed < 100 or a.completed is null) and ((a.start_date >= ? and a.deadline <= ?) or " +
					" (a.start_date <= ? and a.deadline >= ?) or (a.start_date >= ? and a.start_date <= ?))");
			
			
			pst.setDate(1, uF.getDateFormat(getProStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getProEndDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getProStartDate(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getProStartDate(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getProStartDate(), DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(getProEndDate(), DATE_FORMAT));*/
			pst = con.prepareStatement("select a.*, pmc.actual_calculation_type, pmc.pro_name, pmc.client_id, pmc.billing_type from (" +
						"select task_id, activity_name, parent_task_id, pro_id, start_date, deadline, idealtime, already_work," +
						"completed from activity_info where resource_ids like '%,"+getEmp_id()+",%' and task_id not in (select parent_task_id from " +
						"activity_info where resource_ids like '%,"+getEmp_id()+",%' and parent_task_id is not null)) a, projectmntnc pmc  where " +
						"pmc.pro_id=a.pro_id and pmc.approve_status='n' and (parent_task_id in (select task_id from activity_info where resource_ids " +
						" like '%,"+getEmp_id()+",%') or parent_task_id = 0)");
			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alOuter = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			StringBuilder sbTime = new StringBuilder();
				while(rs.next())
			{
					alInner = new ArrayList<String>();
					sbTime = new StringBuilder();
					Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
					Map<String, String> hmTaskEmpwiseAWDaysAndHrs = CF.getProjectTaskEmpwiseActualWorkedDaysAndHrs(con, rs.getString("task_id"), getEmp_id()+"", hmProjectData);
					
					String strIdealTime = null;
					String strAlreadyWork = null;
					if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("H")) {
						strIdealTime = uF.getTotalTimeMinutes100To60(rs.getString("idealtime"));
						strAlreadyWork = uF.getTotalTimeMinutes100To60(hmTaskEmpwiseAWDaysAndHrs.get("ACTUAL_HRS"));
						sbTime.append(" hrs");
					} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
						strIdealTime = rs.getString("idealtime");
						strAlreadyWork = hmTaskEmpwiseAWDaysAndHrs.get("ACTUAL_DAYS");
						sbTime.append(" days");
					} else if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
						strIdealTime = rs.getString("idealtime");
						sbTime.append(" months");
					}
					
					alInner.add(rs.getString("activity_name"));
					alInner.add(rs.getString("pro_name"));
					alInner.add(uF.showData(hmClientDetails.get(rs.getString("client_id")), "N/A"));
					alInner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
					alInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
					alInner.add(uF.showData(strIdealTime, "0") +sbTime.toString());
					alInner.add(uF.showData(strAlreadyWork, "0") +sbTime.toString());
					alInner.add(uF.showData(rs.getString("completed"), "0")+"%");
					
					alOuter.add(alInner);
			}
			rs.close();
			pst.close();
			
			System.out.println("alOuter::"+alOuter);
			request.setAttribute("alOuter", alOuter);
			request.setAttribute("strEmpName", strEmpName);
			
		}catch (Exception e) { 
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	public int getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(int emp_id) {
		this.emp_id = emp_id;
	}
	public String getProStartDate() {
		return proStartDate;
	}

	public void setProStartDate(String proStartDate) {
		this.proStartDate = proStartDate;
	}

	public String getProEndDate() {
		return proEndDate;
	}

	public void setProEndDate(String proEndDate) {
		this.proEndDate = proEndDate;
	}
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}


}
