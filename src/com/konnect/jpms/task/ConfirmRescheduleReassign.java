package com.konnect.jpms.task;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ConfirmRescheduleReassign extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements{
	
	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	String strSessionEmpId;
	String startDate;
	String DeadlineDate;
	String comment;
	String pro_id;
	String parentId;
	String resourceId;
	String taskId;
	String activityName;
	
	public String execute() {
	
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		insertRequestData(uF);
		
		return LOAD;
	}
	
	public void insertRequestData(UtilityFunctions uF){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
	/*	System.out.println("proid===>"+getPro_id());
		System.out.println("parentid===>"+getParentId());
		System.out.println("resourceid===>"+getResourceId());
		System.out.println("start date===>"+getStartDate());
		System.out.println("deadline===>"+getDeadlineDate());
		System.out.println("comment===>"+getComment());
		System.out.println("activity===>"+getActivityName());*/
		
		try{
			con = db.makeConnection(con);

			     /* Check Reschedule Request */		
			List<String> resourceIdList1 = new LinkedList<String>();
			if(getStartDate()!=null && getDeadlineDate()!=null){
				pst = con.prepareStatement("insert into activity_info(pro_id,r_start_date,r_deadline,reschedule_reassign_by_comment,resource_ids,reschedule_reassign_align_by,parent_task_id,reschedule_reassign_request_status,activity_name)values(?,?,?,?,?,?,?,?,?)");
				pst.setInt(1,uF.parseToInt(getPro_id()));
				pst.setDate(2, uF.getDateFormat(getStartDate(), DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(getDeadlineDate(), DATE_FORMAT));
				pst.setString(4,uF.showData(getComment(),""));
				pst.setString(5,","+getResourceId()+",");
				pst.setInt(6,uF.parseToInt(strSessionEmpId));
				pst.setInt(7,uF.parseToInt(getParentId()));
				pst.setInt(8,1);
				pst.setString(9,getActivityName());
//				System.out.println("reschedule pst====="+pst);
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select resource_ids from activity_info where pro_id=? and task_id=? and reschedule_reassign_request_status=?");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				pst.setInt(2, uF.parseToInt(getTaskId()));
				pst.setInt(3, 0);
//				System.out.println("Main pst====>"+pst);
				rs = pst.executeQuery();
				String strResourceIds = null; 
				
				while(rs.next()){
					strResourceIds = rs.getString("resource_ids");
				}
				
//				System.out.println("strResourceIds====>"+strResourceIds);
				List<String> resourceIdList = new ArrayList<String>(Arrays.asList(strResourceIds.split(",")));
//			    System.out.println("resourceIdList===>"+resourceIdList);
			    StringBuilder empids = null;
				resourceIdList.remove(getResourceId());
				for (String empId : resourceIdList) {
				if(uF.parseToInt(empId) > 0){
					if(empids == null) {
						empids = new StringBuilder();
						empids.append(","+empId+","); 
					} else {
						empids.append(empId+",");
					}
				 }
				}
				if(empids == null) {
					empids = new StringBuilder();
				}
//				System.out.println("empids ===>"+empids);
				rs.close();
				pst.close();
				
     			pst = con.prepareStatement("update activity_info set resource_ids=?,reschedule_reassign_request_status=? where pro_id=? and task_id=?");	
				pst.setString(1, empids.toString());
				pst.setInt(2, 1);
				pst.setInt(3, uF.parseToInt(getPro_id()));
				pst.setInt(4, uF.parseToInt(getTaskId()));
//				System.out.println("update pst=====>"+pst);
				pst.executeUpdate();
				pst.close();
	
			}else{      /* Check Reassign Request */		
				
				pst = con.prepareStatement("select resource_ids from activity_info where pro_id=? and task_id=? and reschedule_reassign_request_status=?");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				pst.setInt(2, uF.parseToInt(getTaskId()));
				pst.setInt(3,0);
				rs = pst.executeQuery();
				String strResourceIds1 = null;
				while(rs.next()){
//				resourceIdList1.add(rs.getString("resource_ids"));
					strResourceIds1 = rs.getString("resource_ids");
				}
				
				List<String> resourceIdList = new ArrayList<String>(Arrays.asList(strResourceIds1.split(",")));
//			    System.out.println("resourceIdList1===>"+resourceIdList);
			    StringBuilder empids = null;
				resourceIdList.remove(getResourceId());
				for (String empId : resourceIdList) {
				if(uF.parseToInt(empId) > 0){
					if(empids == null) {
						empids = new StringBuilder();
						empids.append(","+empId+","); 
					} else {
						empids.append(empId+",");
					}
				 }
				}
				if(empids == null) {
					empids = new StringBuilder();
				}
//				System.out.println("resourceIdList1 after removal===>"+resourceIdList1);
				rs.close();
				pst.close();
     			pst = con.prepareStatement("update activity_info set resource_ids=?,reschedule_reassign_request_status=? where pro_id=? and task_id=?");	
				pst.setString(1, empids.toString());
				pst.setInt(2,1);
				pst.setInt(3, uF.parseToInt(getPro_id()));
				pst.setInt(4, uF.parseToInt(getTaskId()));
//				System.out.println("update pst=====>"+pst);
				pst.executeUpdate();
				pst.close();
		}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getDeadlineDate() {
		return DeadlineDate;
	}
	public void setDeadlineDate(String deadlineDate) {
		DeadlineDate = deadlineDate;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getPro_id() {
		return pro_id;
	}
	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
}
