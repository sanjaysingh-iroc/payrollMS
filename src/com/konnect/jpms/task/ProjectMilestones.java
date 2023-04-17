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
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectMilestones extends ActionSupport implements
		ServletRequestAware, ServletResponseAware, SessionAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	int proId;
	String strUsreType;
	String strSessionEmpId;
	String type;
	String strOrgId;
	String submit;
	
	CommonFunctions CF;
	private HttpServletRequest request;

	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		strOrgId = (String)session.getAttribute(ORGID);
		strUsreType = (String)session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		getProjectDocumentDetails();

		return SUCCESS;
	}

	
	public void getProjectDocumentDetails() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			
//			Map<String, List<List<String>>> hmFolderDocs = new HashMap<String, List<List<String>>>();
			List<List<String>> milestoneList = new ArrayList<List<String>>();
			pst = con.prepareStatement("select pmd.*, p.milestone_dependent_on,p.billing_curr_id from project_milestone_details pmd, projectmntnc p where pmd.pro_id=p.pro_id and pmd.pro_id=? order by project_milestone_id");
			pst.setInt(1, getProId());
//			System.out.println("pst ====>> "+pst);
			String dependentOn = "";
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hmCurr = (Map<String, String>)hmCurrency.get(rs.getString("billing_curr_id"));
//				milestoneList = hmFolderDocs.get(rs.getString("pro_folder_id"));
//				if(milestoneList == null) milestoneList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("project_milestone_id"));
				innerList.add(rs.getString("pro_milestone_name"));
				innerList.add(uF.showData(rs.getString("pro_milestone_description"), "-"));
				if(uF.parseToInt(rs.getString("milestone_dependent_on")) == 1) {
					dependentOn = "Milestone %";
					innerList.add(rs.getString("pro_completion_percent")+"%"); //3
				} else if(uF.parseToInt(rs.getString("milestone_dependent_on")) == 2) {
					dependentOn = "Task";
					innerList.add(CF.getProjectTaskNameByTaskId(con, uF, rs.getString("pro_task_id"))); //3
				}
				innerList.add((hmCurr != null ? hmCurr.get("SHORT_CURR") : "") +" "+ rs.getString("pro_milestone_amount")); //4
				boolean flag = checkMilestoneStatus(con, uF, rs.getString("project_milestone_id"), getProId()+"");
				if(flag) {
					/*innerList.add("<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/approved.png\"></span>"); *///5
					innerList.add("<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i></span>"); 
					
				} else {
					/*innerList.add("<span style=\"margin-right: 5px;\"><img title=\"\" src=\"images1/icons/pending.png\"></span>");*/ //5
					innerList.add("<span style=\"margin-right: 5px;\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i></span>");
				}
				milestoneList.add(innerList);
//				hmFolderDocs.put(rs.getString("pro_folder_id"), listDocs);
			}
			rs.close();
			pst.close();
//			System.out.println("hmFolderDocs ====>>>>> " + hmFolderDocs);
			request.setAttribute("milestoneList", milestoneList);
			request.setAttribute("dependentOn", dependentOn);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private boolean checkMilestoneStatus(Connection con, UtilityFunctions uF, String milestoneId, String proId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean flag = false;
		try {
			
			String proTaskId = "";
			String proCompletionPercent = "";
			pst = con.prepareStatement("select pro_task_id, pro_completion_percent from project_milestone_details where project_milestone_id=?");
			pst.setInt(1, uF.parseToInt(milestoneId));
			rs = pst.executeQuery();
			while(rs.next()) {
				proTaskId = rs.getString("pro_task_id");
				proCompletionPercent = rs.getString("pro_completion_percent");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(proTaskId) > 0) {
				pst = con.prepareStatement("select task_id from activity_info where task_id=? and approve_status = 'approved' and completed = 100");
				pst.setInt(1, uF.parseToInt(proTaskId));
				rs = pst.executeQuery();
				while(rs.next()) {
					flag = true;
				}
				rs.close();
				pst.close();
			} else {
				pst = con.prepareStatement("select sum(completed) as completed, count(task_id) as taskCnt from activity_info where pro_id=? and parent_task_id = 0");
				pst.setInt(1, uF.parseToInt(proId));
				rs = pst.executeQuery();
				while(rs.next()) {
					String strComplete = rs.getString("completed");
					String strTaskCnt = rs.getString("taskCnt");
					double avgPercent = uF.parseToDouble(strComplete) / uF.parseToDouble(strTaskCnt);
					if(avgPercent >= uF.parseToDouble(proCompletionPercent)) {
						flag = true;
					}
				}
				rs.close();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	
	public int getProId() {
		return proId;
	}

	public void setProId(int proId) {
		this.proId = proId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setSession(Map arg0) {
		// TODO Auto-generated method stub
	}
}
