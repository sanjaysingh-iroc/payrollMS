package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

public class ViewManagerAndHRComentsOfGoalKRATarget extends ActionSupport implements IStatements, ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String strSessionEmpId;
	String strSessionOrgId;
	HttpSession session;
	CommonFunctions CF;
	
	private String empId;
	private String kraId;
	private String kraTaskId;
	private String goalId;
	private String goalType;
	private String managerName;
	private String managerRating;
	private String managerComment;
	private String hrName;
	private String hrRating;
	private String hrComment;
	private String goalFreqId;
	
	public String execute() throws Exception {
		 
		session = request.getSession();
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		viewManagerAndHRComentsOfGoalKRATarget();
		
		viewUserComentsOfGoalKRATarget();
		
		return SUCCESS;
	
	}
	
	
	private void viewUserComentsOfGoalKRATarget() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			StringBuilder sbQuery = new StringBuilder();
			if(getEmpId() == null || getEmpId().equals("0") ||  getEmpId() == "0" ){
				sbQuery.append("select user_id,user_type,user_rating, user_comment from goal_kra_emp_status_rating_details where  ");
				if(getGoalType() != null && getGoalType().equals("KRA")) {
					sbQuery.append("  kra_id="+uF.parseToInt(getKraId())+" and kra_task_id= "+uF.parseToInt(getKraTaskId())+" ");
				}
				sbQuery.append(" and goal_id= "+uF.parseToInt(getGoalId())+" and goal_freq_id= "+uF.parseToInt(getGoalFreqId())+" and user_type != '-' ");
				pst = con.prepareStatement(sbQuery.toString());
			}else{
			
				sbQuery.append("select user_id,user_type,user_rating, user_comment from goal_kra_emp_status_rating_details where emp_id=? ");
				if(getGoalType() != null && getGoalType().equals("KRA")) {
					sbQuery.append(" and kra_id="+uF.parseToInt(getKraId())+" and kra_task_id= "+uF.parseToInt(getKraTaskId())+" ");
				}
				sbQuery.append(" and goal_id= "+uF.parseToInt(getGoalId())+" and goal_freq_id= "+uF.parseToInt(getGoalFreqId())+" and user_type != '-' ");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getEmpId()));
				
			}
			
			
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmUserComments = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alUserComments = new ArrayList<List<String>>();
			while(rs.next()) {
				alUserComments = hmUserComments.get(rs.getString("user_type"));
				if(alUserComments == null) alUserComments = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(hmEmpName.get(rs.getString("user_id")));
				innerList.add(rs.getString("user_comment"));
				innerList.add(rs.getString("user_rating"));
				innerList.add(rs.getString("user_type"));
				innerList.add(rs.getString("user_id"));
				alUserComments.add(innerList);
				
				hmUserComments.put(rs.getString("user_type"), alUserComments);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmUserComments", hmUserComments);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public void viewManagerAndHRComentsOfGoalKRATarget() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			if(getEmpId() == null || getEmpId().equals("0") ||  getEmpId() == "0" ){
				sbQuery.append("select manager_id, manager_rating, manager_comment, hr_id, hr_rating, hr_comment from goal_kra_status_rating_details where  ");

				if(getGoalType() != null && getGoalType().equals("KRA")) {
					sbQuery.append("  kra_id="+uF.parseToInt(getKraId())+" and kra_task_id= "+uF.parseToInt(getKraTaskId())+" ");
				}
				sbQuery.append(" and goal_id= "+uF.parseToInt(getGoalId())+" and goal_freq_id= "+uF.parseToInt(getGoalFreqId())+" ");
				
				pst = con.prepareStatement(sbQuery.toString());
			//	pst.setInt(1, uF.parseToInt(getEmpId()));
			}else{
					sbQuery.append("select manager_id, manager_rating, manager_comment, hr_id, hr_rating, hr_comment from goal_kra_status_rating_details where emp_id=? ");
					if(getGoalType() != null && getGoalType().equals("KRA")) {
						sbQuery.append(" and kra_id="+uF.parseToInt(getKraId())+" and kra_task_id= "+uF.parseToInt(getKraTaskId())+" ");
					}
					sbQuery.append(" and goal_id= "+uF.parseToInt(getGoalId())+" and goal_freq_id= "+uF.parseToInt(getGoalFreqId())+" ");
					
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getEmpId()));
			}
			rs = pst.executeQuery();
			while(rs.next()) {
				setManagerName(CF.getEmpNameMapByEmpId(con, rs.getString("manager_id")));
				setManagerRating(rs.getString("manager_rating"));
				setManagerComment(rs.getString("manager_comment"));
				setHrName(CF.getEmpNameMapByEmpId(con, rs.getString("hr_id")));
				setHrRating(rs.getString("hr_rating"));
				setHrComment(rs.getString("hr_comment"));
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
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getKraId() {
		return kraId;
	}

	public void setKraId(String kraId) {
		this.kraId = kraId;
	}

	public String getKraTaskId() {
		return kraTaskId;
	}

	public void setKraTaskId(String kraTaskId) {
		this.kraTaskId = kraTaskId;
	}

	public String getGoalId() {
		return goalId;
	}

	public void setGoalId(String goalId) {
		this.goalId = goalId;
	}

	public String getGoalType() {
		return goalType;
	}

	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}

	public String getManagerComment() {
		return managerComment;
	}

	public void setManagerComment(String managerComment) {
		this.managerComment = managerComment;
	}

	public String getHrComment() {
		return hrComment;
	}

	public void setHrComment(String hrComment) {
		this.hrComment = hrComment;
	}

	public String getGoalFreqId() {
		return goalFreqId;
	}

	public void setGoalFreqId(String goalFreqId) {
		this.goalFreqId = goalFreqId;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getManagerRating() {
		return managerRating;
	}

	public void setManagerRating(String managerRating) {
		this.managerRating = managerRating;
	}

	public String getHrName() {
		return hrName;
	}

	public void setHrName(String hrName) {
		this.hrName = hrName;
	}

	public String getHrRating() {
		return hrRating;
	}

	public void setHrRating(String hrRating) {
		this.hrRating = hrRating;
	}

}
