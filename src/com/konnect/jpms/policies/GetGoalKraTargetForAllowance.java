package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillAllowancePaymentLogic;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetGoalKraTargetForAllowance extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	

	String orgId;
	String levelId;
	String conditionType;
	List<FillAllowancePaymentLogic> goalList = new ArrayList<FillAllowancePaymentLogic>();
	List<FillAllowancePaymentLogic> kraList = new ArrayList<FillAllowancePaymentLogic>();

	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(uF.parseToInt(getLevelId()) > 0){
			getGoalKraTargetForAllowance(uF);
		}
		
		return SUCCESS;

	}

	private void getGoalKraTargetForAllowance(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			List<String> alEmpIds = new ArrayList<String>();
			pst = con.prepareStatement("select emp_id,grade_id from employee_official_details where emp_id>0 and grade_id in (select gd.grade_id from " +
				"grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id " +
				" and ld.level_id = ?)");
			pst.setInt(1, uF.parseToInt(getLevelId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				alEmpIds.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(getConditionType()) == A_GOAL_KRA_TARGET_ID) {
				List<String> goalIds = new ArrayList<String>();
				for (int i = 0; alEmpIds != null && i < alEmpIds.size(); i++) {
					pst = con.prepareStatement("select goal_id, goal_title from goal_details where goal_type in (4,5,6,7) and measure_kra != 'KRA' and is_close = false and emp_ids like '%,"+alEmpIds.get(i)+",%'");
					rs = pst.executeQuery();
					while(rs.next()) {
						if(!goalIds.contains(rs.getString("goal_id"))) {
							goalList.add(new FillAllowancePaymentLogic(rs.getString("goal_id"), rs.getString("goal_title")));
							goalIds.add(rs.getString("goal_id"));
						}
					}
					rs.close();
					pst.close();
				}
			} else {
				List<String> kraIds = new ArrayList<String>();
				for (int i = 0; alEmpIds != null && i < alEmpIds.size(); i++) {
					pst = con.prepareStatement("select goal_kra_id, kra_description from goal_kras where is_close = false and (is_assign = true or is_assign is null) and emp_ids like '%,"+alEmpIds.get(i)+",%'");
					rs = pst.executeQuery();
					while(rs.next()) {
						if(!kraIds.contains(rs.getString("goal_kra_id"))) {
							kraList.add(new FillAllowancePaymentLogic(rs.getString("goal_kra_id"), rs.getString("kra_description")));
							kraIds.add(rs.getString("goal_kra_id"));
						}
					}
					rs.close();
					pst.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public List<FillAllowancePaymentLogic> getGoalList() {
		return goalList;
	}

	public void setGoalList(List<FillAllowancePaymentLogic> goalList) {
		this.goalList = goalList;
	}

	public String getConditionType() {
		return conditionType;
	}

	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}

	public List<FillAllowancePaymentLogic> getKraList() {
		return kraList;
	}

	public void setKraList(List<FillAllowancePaymentLogic> kraList) {
		this.kraList = kraList;
	}


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
