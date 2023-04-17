package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * @author workrig
 *
 */
public class MyTasks extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	HttpServletResponse response;
	HttpSession session;
	String strProductType =  null;
	String strUserType;
	
	CommonFunctions CF;
	int pro_id;
	int emp_id;
	String proType; 
	String taskId;
	
	String taskSubtaskStatus;
	String assignedBy;
	String recurrOrMiles;
	String sortBy;
	String sortBy1;
	
	String btnSubmit;
	
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, "/jsp/task/MyTasks.jsp");
		request.setAttribute(TITLE, "My Tasks");
		emp_id = uF.parseToInt((String) session.getAttribute(EMPID));
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strProductType = (String)session.getAttribute(PRODUCT_TYPE);
		
		if(uF.parseToInt(strProductType) != 3) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-tasks\"></i><a href=\"MyTasks.action\" style=\"color: #3c8dbc;\"> My Work</a></li>" +
				"<li class=\"active\">My Tasks</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		getProjectDetails();
		
//		System.out.println("MyTasks getTaskId() ===>> " + getTaskId());
		
		if(getBtnSubmit() != null) {
			return SUCCESS;
		} else {
			return LOAD;
		}
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
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			getSelectedFilter(uF, hmEmpName);
			
			List<String> alAddedBy = new ArrayList<String>();
			StringBuilder sbquee = new StringBuilder();
			sbquee.append("select distinct(added_by) as added_by from activity_info ai where ai.resource_ids like '%,"+emp_id+",%' and ai.task_id not in (select " +
				"parent_task_id from activity_info where resource_ids like '%,"+emp_id+",%' and parent_task_id is not null and task_accept_status != -1) and added_by is not null ");
			if(getPro_id()!=0) {
				sbquee.append(" and ai.pro_id="+getPro_id()+" ");
			}
			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
				sbquee.append(" and approve_status='n' and task_accept_status = 1 and task_from_my_self=false ");
			} else if(getProType() == null || getProType().equals("TR")) {
				sbquee.append(" and approve_status='n' and task_accept_status = 0 and task_from_my_self=false ");
			} else if(getProType() == null || getProType().equals("MR")) {
				sbquee.append(" and approve_status='n' and task_from_my_self=false ");
			} else if(getProType() != null && getProType().equals("C")) {
				sbquee.append(" and approve_status='approved' ");
			}
//				sbque.append("order by ai.deadline desc, priority desc, ai.task_id desc limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbquee.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				alAddedBy.add(rs.getString("added_by"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbAddedbyOption = new StringBuilder();
			if(uF.parseToInt(getAssignedBy()) == -1) {
				sbAddedbyOption.append("<option value='-1' selected>Myself</option>");
			} else {
				sbAddedbyOption.append("<option value='-1'>Myself</option>");
			}
			for(int i=0; alAddedBy != null && !alAddedBy.isEmpty() && i<alAddedBy.size(); i++) {
				if(uF.parseToInt(alAddedBy.get(i)) > 0) {
					if(uF.parseToInt(getAssignedBy()) == uF.parseToInt(alAddedBy.get(i))) {
						sbAddedbyOption.append("<option value='"+alAddedBy.get(i)+"' selected>"+hmEmpName.get(alAddedBy.get(i))+"</option>");
					} else {
						sbAddedbyOption.append("<option value='"+alAddedBy.get(i)+"'>"+hmEmpName.get(alAddedBy.get(i))+"</option>");
					}
				}
			}
			request.setAttribute("sbAddedbyOption", sbAddedbyOption.toString());
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getSelectedFilter(UtilityFunctions uF, Map<String, String> hmEmpName) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("TASKSTATUS");
		if(getTaskSubtaskStatus()!=null ) {
			String strTaskSubtaskStatus="";
			if(uF.parseToInt(getTaskSubtaskStatus()) == 1) {
				strTaskSubtaskStatus = "On-Track";
			} else if(uF.parseToInt(getTaskSubtaskStatus()) == 2) {
				strTaskSubtaskStatus = "Not Started";
			} else if(uF.parseToInt(getTaskSubtaskStatus()) == 3) {
				strTaskSubtaskStatus = "Pending";
			}
			if(strTaskSubtaskStatus!=null && !strTaskSubtaskStatus.equals("")) {
				hmFilter.put("TASKSTATUS", strTaskSubtaskStatus);
			} else {
				hmFilter.put("TASKSTATUS", "All Tasks");
			}
		} else {
			hmFilter.put("TASKSTATUS", "All Tasks");
		}
		
		
		alFilter.add("ASSIGNEDBY");
		if(getAssignedBy()!=null) {
			String strAssignedBy="";
			if(uF.parseToInt(getAssignedBy()) == -1) {
				strAssignedBy = "Myself";
			} else if(uF.parseToInt(getAssignedBy()) >0) {
				strAssignedBy = hmEmpName.get(getAssignedBy());
			}
			if(strAssignedBy!=null && !strAssignedBy.equals("")) {
				hmFilter.put("ASSIGNEDBY", strAssignedBy);
			} else {
				hmFilter.put("ASSIGNEDBY", "All Assigner");
			}
		} else {
			hmFilter.put("ASSIGNEDBY", "All Assigner");
		}
		
		alFilter.add("RECURRORMILES");
		if(getRecurrOrMiles()!=null ) {
			String strRecurrOrMiles="";
			if(uF.parseToInt(getRecurrOrMiles()) == 1) {
				strRecurrOrMiles = "Recurring";
			} else if(uF.parseToInt(getRecurrOrMiles()) == 2) {
				strRecurrOrMiles = "Milestone";
			}
			if(strRecurrOrMiles!=null && !strRecurrOrMiles.equals("")) {
				hmFilter.put("RECURRORMILES", strRecurrOrMiles);
			} else {
				hmFilter.put("RECURRORMILES", "All Project Type");
			}
		} else {
			hmFilter.put("RECURRORMILES", "All Project Type");
		}
		
		
		String selectedFilter=getSelectedFilter(uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	public String getSelectedFilter(UtilityFunctions uF, List<String> alFilter, Map<String, String> hmFilter) {
		StringBuilder sbFilter=new StringBuilder("<strong>Filter Summary: </strong>");
		
		for(int i=0;alFilter!=null && i<alFilter.size();i++) {
			if(i>0) {
					sbFilter.append(", ");
			} 
			if(alFilter.get(i).equals("TASKSTATUS")) {
				sbFilter.append("<strong>Task Status:</strong> ");
				sbFilter.append(hmFilter.get("TASKSTATUS"));
			
			} else if(alFilter.get(i).equals("ASSIGNEDBY")) {
				sbFilter.append("<strong>Assigned By:</strong> ");
				sbFilter.append(hmFilter.get("ASSIGNEDBY"));
			
			} else if(alFilter.get(i).equals("RECURRORMILES")) {
				sbFilter.append("<strong>Project Type:</strong> ");
				sbFilter.append(hmFilter.get("RECURRORMILES"));
			
			}/* else if(alFilter.get(i).equals("SORTBY")) {
				sbFilter.append("<strong>Sort By:</strong> ");
				sbFilter.append(hmFilter.get("SORTBY"));
			} */
		}
		return sbFilter.toString();
	}
	
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getTaskSubtaskStatus() {
		return taskSubtaskStatus;
	}

	public void setTaskSubtaskStatus(String taskSubtaskStatus) {
		this.taskSubtaskStatus = taskSubtaskStatus;
	}

	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(String assignedBy) {
		this.assignedBy = assignedBy;
	}

	public String getRecurrOrMiles() {
		return recurrOrMiles;
	}

	public void setRecurrOrMiles(String recurrOrMiles) {
		this.recurrOrMiles = recurrOrMiles;
	}

	public int getPro_id() {
		return pro_id;
	}

	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
}