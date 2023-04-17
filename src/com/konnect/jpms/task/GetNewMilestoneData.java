package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetNewMilestoneData extends ActionSupport implements
		ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	
	
	HttpSession session;
	HttpServletRequest request;
	CommonFunctions CF;
	String proId;
	String proFreqId;
	String milestoneId;
	String mileCnt;
	String srNoCnt;
	String partiCnt;
	String type;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		getNewMilestoneData();
		
		return SUCCESS;
	}


	private void getNewMilestoneData() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from project_milestone_details where project_milestone_id = ?");
			pst.setInt(1, uF.parseToInt(getMilestoneId()));
			rs = pst.executeQuery();
			String taskId = null;
			String milePercent = null;
			while(rs.next()) {
				taskId = rs.getString("pro_task_id");
				milePercent = rs.getString("pro_completion_percent");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(taskId)>0) {
				pst = con.prepareStatement("select * from project_milestone_details where project_milestone_id > "+uF.parseToInt(taskId)+" and " +
					"pro_id = "+uF.parseToInt(getProId())+" limit 1");
			} else {
				pst = con.prepareStatement("select * from project_milestone_details where pro_completion_percent > "+uF.parseToDouble(milePercent)+" " +
					"and pro_id = "+uF.parseToInt(getProId())+" limit 1");
			}
			List<String> alMilestoneData = new ArrayList<String>();
			rs = pst.executeQuery();
			while(rs.next()) {
				alMilestoneData.add(rs.getString("project_milestone_id")); //0
				alMilestoneData.add(rs.getString("pro_milestone_name")); //1
				alMilestoneData.add(rs.getString("pro_milestone_description")); //2
				alMilestoneData.add(rs.getString("pro_completion_percent")); //3
				alMilestoneData.add(rs.getString("pro_task_id")); //4
				alMilestoneData.add(CF.getProjectTaskNameByTaskId(con, uF, rs.getString("pro_task_id"))); //5
				alMilestoneData.add(rs.getString("pro_milestone_amount")); //6
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alMilestoneData", alMilestoneData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}


	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getProFreqId() {
		return proFreqId;
	}

	public void setProFreqId(String proFreqId) {
		this.proFreqId = proFreqId;
	}

	public String getMilestoneId() {
		return milestoneId;
	}

	public void setMilestoneId(String milestoneId) {
		this.milestoneId = milestoneId;
	}

	public String getMileCnt() {
		return mileCnt;
	}

	public void setMileCnt(String mileCnt) {
		this.mileCnt = mileCnt;
	}

	public String getSrNoCnt() {
		return srNoCnt;
	}

	public void setSrNoCnt(String srNoCnt) {
		this.srNoCnt = srNoCnt;
	}

	public String getPartiCnt() {
		return partiCnt;
	}

	public void setPartiCnt(String partiCnt) {
		this.partiCnt = partiCnt;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
