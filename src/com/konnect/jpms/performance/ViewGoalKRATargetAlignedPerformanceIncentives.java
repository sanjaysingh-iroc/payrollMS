package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
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

public class ViewGoalKRATargetAlignedPerformanceIncentives extends ActionSupport implements ServletRequestAware,IStatements{

	HttpSession session;
	
	String strSessionEmpId;
	String strSessionUserType;
	CommonFunctions CF;
	
	
	private String goalTargetKraId;
	private String type;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
		 
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		
		UtilityFunctions uF = new UtilityFunctions();
		getAllowancePolicyDetails(uF);
		
		return SUCCESS;
	}
	
	
	private void getAllowancePolicyDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmAllowanceCondition = CF.getAllowanceCondition();
			
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmOrg = CF.getOrgName(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmAllowancePaymentLogic = CF.getAllowancePaymentLogic();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from allowance_condition_details where level_id > 0");
			if(getType() != null && getType().equals("GT")) {
				sbQuery.append(" and goal_kra_target_ids like '%,"+getGoalTargetKraId()+",%' ");
			} else {
				sbQuery.append(" and kra_ids like '%,"+getGoalTargetKraId()+",%' ");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			
			rs = pst.executeQuery();
			List<Map<String, String>> alCondition = new ArrayList<Map<String,String>>();
			Map<String, String> hmAllowanceConditionSlab = new HashMap<String, String>();
			StringBuilder sbCondiIds = null;
			while(rs.next()) {
				Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(rs.getString("level_id")));
				Map<String,String> hmCondition = new HashMap<String, String>();
				hmCondition.put("ALLOWANCE_CONDITION_ID", rs.getString("allowance_condition_id"));
				hmCondition.put("ALLOWANCE_CONDITION_SLAB", uF.showData(rs.getString("condition_slab"),""));
				hmCondition.put("ALLOWANCE_CONDITION_TYPE", rs.getString("allowance_condition"));
				hmCondition.put("ALLOWANCE_CONDITION", uF.showData(hmAllowanceCondition.get(rs.getString("allowance_condition")),""));
				hmCondition.put("MIN_CONDITION", uF.showData(rs.getString("min_condition"),"0"));
				hmCondition.put("MAX_CONDITION", uF.showData(rs.getString("max_condition"),"0"));
				hmCondition.put("SALARY_HEAD_ID", uF.showData(hmSalaryHeadsMap.get(rs.getString("salary_head_id")),""));
				hmCondition.put("LEVEL_ID", uF.showData(hmLevelMap.get(rs.getString("level_id")),""));
				hmCondition.put("ORG_ID", uF.showData(hmOrg.get(rs.getString("org_id")),""));
				hmCondition.put("ADDED_BY", uF.showData(hmEmpName.get(rs.getString("updated_by") != null ? rs.getString("updated_by") : rs.getString("added_by")),""));
				hmCondition.put("ENTRY_DATE", uF.getDateFormat(rs.getString("update_date") != null ? rs.getString("update_date") : rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				hmCondition.put("CUSTOM_FACTOR_TYPE", uF.showData(rs.getString("custom_type"),"A"));
				hmCondition.put("CUSTOM_AMT_PERCENTAGE", uF.showData(rs.getString("custom_amt_percentage"),"0"));
				
				hmCondition.put("IS_PUBLISH", rs.getString("is_publish"));
				hmCondition.put("UPDATED_BY", uF.showData(hmEmpName.get(rs.getString("updated_by")),""));
				hmCondition.put("UPDATE_DATE", uF.getDateFormat(rs.getString("update_date"), DBDATE, CF.getStrReportDateFormat()));
				
				alCondition.add(hmCondition);
				
				hmAllowanceConditionSlab.put(rs.getString("allowance_condition_id"), uF.showData(rs.getString("condition_slab"),""));
				
				if(sbCondiIds == null) {
					sbCondiIds = new StringBuilder();
					sbCondiIds.append(rs.getString("allowance_condition_id"));
				} else {
					sbCondiIds.append(","+rs.getString("allowance_condition_id"));
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alCondition", alCondition);
//			System.out.println("alCondition ===>> " + alCondition);
			
			/*
			 * Payment Logic
			 * */
			
			Map<String, List<Map<String, String>>> hmCondiLogic = new HashMap<String, List<Map<String, String>>>();
			
			if(sbCondiIds != null) {
//				System.out.println("sbCondiIds ===>> " + sbCondiIds.toString());
				pst = con.prepareStatement("select * from allowance_payment_logic where allowance_condition_id in ("+sbCondiIds.toString()+")");
//				System.out.println("pst logic ===>> " + pst);
				rs = pst.executeQuery();
				List<Map<String, String>> alLogic = new ArrayList<Map<String,String>>();
				while(rs.next()) {
					Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(rs.getString("level_id")));
					
					alLogic = hmCondiLogic.get(rs.getString("allowance_condition_id"));
					if(alLogic == null) alLogic = new ArrayList<Map<String,String>>();
					
					Map<String,String> hmLogic = new HashMap<String, String>();
					hmLogic.put("PAYMENT_LOGIC_ID", rs.getString("payment_logic_id"));
					hmLogic.put("PAYMENT_LOGIC_SLAB", uF.showData(rs.getString("payment_logic_slab"),""));
					hmLogic.put("ALLOWANCE_CONDITION_ID", rs.getString("allowance_condition_id"));
					hmLogic.put("ALLOWANCE_CONDITION", uF.showData(hmAllowanceConditionSlab.get(rs.getString("allowance_condition_id")),""));
					
					hmLogic.put("ALLOWANCE_PAYMENT_LOGIC_ID", rs.getString("payment_logic"));
					hmLogic.put("ALLOWANCE_PAYMENT_LOGIC", uF.showData(hmAllowancePaymentLogic.get(rs.getString("payment_logic")),""));
					
					hmLogic.put("ADDED_BY", uF.showData(hmEmpName.get(rs.getString("updated_by") != null ? rs.getString("updated_by") : rs.getString("added_by")),""));
					hmLogic.put("ENTRY_DATE", uF.getDateFormat(rs.getString("update_date") != null ? rs.getString("update_date") : rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
					hmLogic.put("SALARY_HEAD_ID", uF.showData(hmSalaryHeadsMap.get(rs.getString("salary_head_id")),""));
					hmLogic.put("LEVEL_ID", uF.showData(hmLevelMap.get(rs.getString("level_id")),""));
					hmLogic.put("ORG_ID", uF.showData(hmOrg.get(rs.getString("org_id")),""));
					
					hmLogic.put("FIXED_AMOUNT", uF.showData(rs.getString("fixed_amount"),"0"));
					hmLogic.put("CAL_SALARY_HEAD_ID", uF.showData(rs.getString("cal_salary_head_id"),""));
					hmLogic.put("CAL_SALARY_HEAD_NAME", uF.showData(hmSalaryHeadsMap.get(rs.getString("cal_salary_head_id")),""));
					
					hmLogic.put("IS_PUBLISH", rs.getString("is_publish"));
					hmLogic.put("UPDATED_BY", uF.showData(hmEmpName.get(rs.getString("updated_by")),""));
					hmLogic.put("UPDATE_DATE", uF.getDateFormat(rs.getString("update_date"), DBDATE, CF.getStrReportDateFormat()));
					
					alLogic.add(hmLogic);
					
					hmCondiLogic.put(rs.getString("allowance_condition_id"), alLogic);
				}
				rs.close();
				pst.close();
//				System.out.println("hmCondiLogic ===>> " + hmCondiLogic);
			}
			request.setAttribute("hmCondiLogic", hmCondiLogic);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String getGoalTargetKraId() {
		return goalTargetKraId;
	}

	public void setGoalTargetKraId(String goalTargetKraId) {
		this.goalTargetKraId = goalTargetKraId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}	

}
