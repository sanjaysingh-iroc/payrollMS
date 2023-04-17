package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetGoalsList extends ActionSupport implements ServletRequestAware {

	private String empId;
	private String goalCnt;
	HttpServletRequest request;

	private List<FillGoals> goalsList;
	public String execute() {
		goalsList = getGoalsListEmpwise();
		return SUCCESS;
	}
	public List<FillGoals> getGoalsListEmpwise() {
		List<FillGoals> al = new ArrayList<FillGoals>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			StringBuilder sbgoalids = new StringBuilder();
			
			pst = con.prepareStatement("select goal_id from goal_details where emp_ids = ?");
			pst.setString(1,","+getEmpId()+",");
			rs = pst.executeQuery();
			System.out.println("pst===>"+pst);
			int cnt=0;
			while(rs.next()){
				if(cnt==0){	
					sbgoalids.append(rs.getString("goal_id"));
					cnt++;
				}else{
					sbgoalids.append(","+rs.getString("goal_id"));
				}
			}
			rs.close();
			pst.close();
			
			al = new FillGoals(request).fillGoalsEmpWise(sbgoalids.toString());

			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getGoalCnt() {
		return goalCnt;
	}
	public void setGoalCnt(String goalCnt) {
		this.goalCnt = goalCnt;
	}
	public List<FillGoals> getGoalsList() {
		return goalsList;
	}
	public void setGoalsList(List<FillGoals> goalsList) {
		this.goalsList = goalsList;
	}
	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		// TODO Auto-generated method stub
		
	}

}
