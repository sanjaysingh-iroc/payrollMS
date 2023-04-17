package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillAllowanceCondition implements IStatements {
	String conditionId;
	String conditionName;
		
	public String getConditionId() {
		return conditionId;
	}

	public void setConditionId(String conditionId) {
		this.conditionId = conditionId;
	}

	public String getConditionName() {
		return conditionName;
	}

	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}

	public FillAllowanceCondition() {
		super();
	}
	
	public FillAllowanceCondition(String conditionId, String conditionName) {
		super();
		this.conditionId = conditionId;
		this.conditionName = conditionName;
	}
	HttpServletRequest request;
	public FillAllowanceCondition(HttpServletRequest request) {
		this.request = request;
	}

	public List<FillAllowanceCondition> fillAllowanceCondition() {
		List<FillAllowanceCondition> al = new ArrayList<FillAllowanceCondition>();
		try {
			al.add(new FillAllowanceCondition(""+A_NO_OF_DAYS_ID, A_NO_OF_DAYS));
			al.add(new FillAllowanceCondition(""+A_NO_OF_HOURS_ID, A_NO_OF_HOURS));
			al.add(new FillAllowanceCondition(""+A_NO_OF_DAYS_ABSENT_ID, A_NO_OF_DAYS_ABSENT));
			al.add(new FillAllowanceCondition(""+A_CUSTOM_FACTOR_ID, A_CUSTOM_FACTOR));
			al.add(new FillAllowanceCondition(""+A_GOAL_KRA_TARGET_ID, A_GOAL_KRA_TARGET));
			al.add(new FillAllowanceCondition(""+A_KRA_ID, A_KRA));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public List<FillAllowanceCondition> fillAllowanceConditionSlab(UtilityFunctions uF, String strOrg, String strLevel, String strSalaryHeadId) {
		List<FillAllowanceCondition> al = new ArrayList<FillAllowanceCondition>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from allowance_condition_details where salary_head_id=? and level_id=? and org_id=?");
			pst.setInt(1, uF.parseToInt(strSalaryHeadId));
			pst.setInt(2, uF.parseToInt(strLevel));
			pst.setInt(3, uF.parseToInt(strOrg));
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillAllowanceCondition(rs.getString("allowance_condition_id"), rs.getString("condition_slab")));
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
		return al;
	}
}
