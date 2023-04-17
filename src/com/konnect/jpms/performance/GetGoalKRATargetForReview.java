package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class GetGoalKRATargetForReview implements ServletRequestAware, SessionAware,
		IStatements {
	Map session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;

	private String id;
	private String callFrom;
	private String systemType;
	private String ansType;
	  
	public String execute() {
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";

		getAppraisalDetail();
//		System.out.println("ansType ===========> "+ansType);
		
		return "success";
	}

	
	private void getTargetDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, List<String>> hmTargetData = new HashMap<String, List<String>>();
				pst = con.prepareStatement("select * from goal_details gd where gd.goal_id not in (select aqd.goal_kra_target_id from " +
						"appraisal_question_details aqd where aqd.goal_kra_target_id is not null and appraisal_id = ?) and (gd.goal_type = "+INDIVIDUAL_GOAL+" " +
						"or gd.goal_type = "+INDIVIDUAL_TARGET+" or gd.goal_type = "+PERSONAL_GOAL+") and " +
						"measure_type is not null and measure_type != '' and measure_kra is not null and measure_kra != '' and is_close = false");
				pst.setInt(1, uF.parseToInt(getId()));
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("goal_id"));
					innerList.add(rs.getString("goal_title"));
					innerList.add(rs.getString("goal_objective"));
					innerList.add(rs.getString("goal_description"));
					innerList.add(rs.getString("goal_attribute"));
					innerList.add(rs.getString("weightage"));
					innerList.add(uF.showData(getAppendData(rs.getString("emp_ids"), hmEmpName), ""));
					
					hmTargetData.put(rs.getString("goal_id"), innerList);
				}
				rs.close();
				pst.close();
			request.setAttribute("hmTargetData", hmTargetData);
		
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	private void getKRADetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, List<String>> hmKRAData = new HashMap<String, List<String>>();
				StringBuilder sb = new StringBuilder();
				
		//===start parvez date: 18-12-2021===
				boolean isKRATaskShown = CF.getFeatureManagementStatus(request, uF, F_GOAL_KRA_TARGET_TASK_SHOWN_IN_REVIEW_CREATION_FORM);	
				
				if(!isKRATaskShown){
					sb.append("select k.*, g.weightage from goal_kras k, goal_details g where k.goal_id=g.goal_id ");
					sb.append(" and (g.goal_type="+INDIVIDUAL_GOAL+" or g.goal_type="+INDIVIDUAL_KRA+" or (g.goal_type="+EMPLOYEE_KRA+" and k.is_close=false)) " +
							"and (measure_type is null or measure_type='') and g.measure_kra is not null and g.measure_kra !='' " +
							"and (g.is_close is null or g.is_close=false) order by k.goal_id");
				} else{
					sb.append("select k.*, g.weightage,gkt.task_name from goal_kras k, goal_details g, goal_kra_tasks gkt where k.goal_id=g.goal_id and g.goal_id=gkt.goal_id ");
					sb.append(" and (g.goal_type="+INDIVIDUAL_GOAL+" or g.goal_type="+INDIVIDUAL_KRA+" or (g.goal_type="+EMPLOYEE_KRA+" and k.is_close=false)) " +
							"and (measure_type is null or measure_type='') and g.measure_kra is not null and g.measure_kra !='' " +
							"and (g.is_close is null or g.is_close=false) order by k.goal_id");
				}
				/*sb.append("select k.*, g.weightage from goal_kras k, goal_details g where k.goal_id=g.goal_id ");
				sb.append(" and (g.goal_type="+INDIVIDUAL_GOAL+" or g.goal_type="+INDIVIDUAL_KRA+" or (g.goal_type="+EMPLOYEE_KRA+" and k.is_close=false)) " +
						"and (measure_type is null or measure_type='') and g.measure_kra is not null and g.measure_kra !='' " +
						"and (g.is_close is null or g.is_close=false) order by k.goal_id");*/
				
				/*sb.append("select k.*, g.weightage,gkt.task_name from goal_kras k, goal_details g, goal_kra_tasks gkt where k.goal_id=g.goal_id and g.goal_id=gkt.goal_id ");
				sb.append(" and (g.goal_type="+INDIVIDUAL_GOAL+" or g.goal_type="+INDIVIDUAL_KRA+" or (g.goal_type="+EMPLOYEE_KRA+" and k.is_close=false)) " +
						"and (measure_type is null or measure_type='') and g.measure_kra is not null and g.measure_kra !='' " +
						"and (g.is_close is null or g.is_close=false) order by k.goal_id");*/
	
		//===end parvez date: 18-12-2021===
				
				pst = con.prepareStatement(sb.toString());
				System.out.println("GGKRATFR/106--pst ===>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("goal_id"));			//0
					innerList.add(rs.getString("kra_description"));	//1
					innerList.add(rs.getString("goal_kra_id"));		//2
					innerList.add(rs.getString("entry_date"));		//3
					innerList.add(rs.getString("effective_date"));	//4
					innerList.add(rs.getString("weightage"));		//5
					innerList.add(uF.showData(getAppendData(rs.getString("emp_ids"), hmEmpName), ""));	//6
					innerList.add(rs.getString("approved_by"));		//7
					innerList.add(rs.getString("kra_order"));		//8
					innerList.add(rs.getString("is_approved"));		//9
					innerList.add(rs.getString("goal_type"));		//10
			//===start parvez date: 21-12-2021===
					if(isKRATaskShown){
						innerList.add(rs.getString("task_name"));		//11
					}
			//===end parvez date: 20-12-2021===
					
					hmKRAData.put(rs.getString("goal_kra_id"), innerList);
				}
				rs.close();
				pst.close();
//				System.out.println("hmKRAData ===>> " + hmKRAData);
					request.setAttribute("hmKRAData", hmKRAData);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private void getKRAIDsGoalwise(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			Map<String, List<String>> hmKraIDs = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select * from goal_kras where is_close = false and goal_kra_id not in (select aqd.kra_id from " + //(goal_type = "+INDIVIDUAL_GOAL+" or goal_type = "+INDIVIDUAL_KRA+") and
						"appraisal_question_details aqd where aqd.kra_id is not null and appraisal_id = ?) and goal_id in (select goal_id from goal_details where is_close is null or is_close=false)");
			pst.setInt(1, uF.parseToInt(getId()));
//			System.out.println("GGKRATFR/144--pst="+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				List<String> innerList = hmKraIDs.get(rs.getString("goal_id"));
				if(innerList == null)innerList = new ArrayList<String>();
				innerList.add(rs.getString("goal_kra_id"));
//					innerList.add(rs.getString("goal_id"));

				hmKraIDs.put(rs.getString("goal_id"), innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmKraIDs", hmKraIDs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void getAppraisalDetail() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF =new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			
			getGoalsOfEmployee(con);
			getGoalParentId(con);
			if(systemType != null && systemType.equals("goal")){
				getGoalsDetails(con, uF);
			}else if(systemType != null && systemType.equals("KRA")){
				getKRADetails(con, uF);
				getKRAIDsGoalwise(con, uF);
			}else if(systemType != null && systemType.equals("target")){
				getTargetDetails(con, uF);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getGoalsDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, List<String>> hmGoalData = new HashMap<String, List<String>>();
			
			pst = con.prepareStatement("select * from goal_details gd where gd.goal_id not in (select aqd.goal_kra_target_id from " +
					"appraisal_question_details aqd where aqd.goal_kra_target_id is not null and appraisal_id = ?) and (gd.goal_type = "+INDIVIDUAL_GOAL+" or gd.goal_type = "+PERSONAL_GOAL+") and " +
					"(measure_type is null or measure_type = '') and (measure_kra is null or measure_kra = '' ) and (is_close is null or is_close=false)");
//				pst = con.prepareStatement("select * from goal_details gd, appraisal_question_details aqd where gd.goal_id != aqd.goal_kra_target_id and gd.goal_type = 4 and (measure_type is null or measure_type = '') and (measure_kra is null or measure_kra = '')");
			pst.setInt(1, uF.parseToInt(getId()));
			System.out.println("GGKRAFR/211--pst="+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_title"));
				innerList.add(rs.getString("goal_objective"));
				innerList.add(rs.getString("goal_description"));
				innerList.add(rs.getString("goal_attribute"));
				innerList.add(rs.getString("weightage"));
				innerList.add(uF.showData(getAppendData(rs.getString("emp_ids"), hmEmpName), ""));
				
				hmGoalData.put(rs.getString("goal_id"), innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmGoalData", hmGoalData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
//		System.out.println("strID :: "+strID);
		if (strID != null && !strID.equals("") && !strID.isEmpty()) {
			if(strID.length()>0 && strID.substring(0, 1).equals(",") && strID.substring(strID.length()-1, strID.length()).equals(",")){
			strID = strID.substring(1, strID.length()-1);
			}
			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						sb.append(", " + mp.get(temp[i].trim()));
					}
				}
			} else {
				return mp.get(strID);
			}

		} else {
			return null;
		}

		return sb.toString();
	}
	
	
	private Map<String, String> getGoalParentId(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> hmGoalParentID = new HashMap<String, String>();
		try {
				pst = con.prepareStatement("select goal_id,goal_parent_id from goal_details ");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmGoalParentID.put(rs.getString("goal_id"), rs.getString("goal_parent_id"));
				}
				rs.close();
				pst.close();
				request.setAttribute("hmGoalParentID", hmGoalParentID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return hmGoalParentID;
	}
	
	
	
	private void getGoalsOfEmployee(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String employeeIds = null;
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("pst employeeIds ===> "+pst);
			while (rs.next()) {
				employeeIds = uF.showData(rs.getString("self_ids"), "");
//				appraisalList.add(uF.showData(getAppendData(rs.getString("self_ids"),hmEmpName), ""));
			}
			
//			Map<String, String> hm
//			System.out.println("employeeIds ===> "+employeeIds);
			List<String> empIdsList = Arrays.asList(employeeIds.split(","));
			List<String> goalIdsList = new ArrayList<String>();
			for (int i = 0; empIdsList != null && !empIdsList.isEmpty() && i < empIdsList.size(); i++) {
				StringBuilder sbquery = new StringBuilder();
				sbquery.append("select goal_id from goal_details where emp_ids like '%,"+empIdsList.get(i)+",%' and (is_close is null or is_close=false) "); //goal_type = 4 and
				if(systemType != null && systemType.equals("goal")){
					sbquery.append(" and (goal_type = "+INDIVIDUAL_GOAL+" or goal_type = "+PERSONAL_GOAL+") and (measure_type is null or measure_type = '') and (measure_kra is null or measure_kra = '')");
				}else if(systemType != null && systemType.equals("KRA")){
					sbquery.append(" and (goal_type="+INDIVIDUAL_GOAL+" or goal_type="+INDIVIDUAL_KRA+" or goal_type="+EMPLOYEE_KRA+") and (measure_type is null or measure_type = '') and measure_kra is not null and measure_kra != ''");
				}else if(systemType != null && systemType.equals("target")){
					sbquery.append(" and (goal_type = "+INDIVIDUAL_GOAL+" or goal_type = "+INDIVIDUAL_TARGET+" or goal_type = "+PERSONAL_GOAL+") and measure_type is not null and measure_type != '' and measure_kra is not null and measure_kra != ''");
				}
//				pst = con.prepareStatement("select goal_id from goal_details where goal_type = 4 and emp_ids like '%,"+empIdsList.get(i)+",%' ");
				pst = con.prepareStatement(sbquery.toString());
//				System.out.println("GGKRATFR/321---pst="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
//					System.out.println("Goal Id ===> "+rs.getString("goal_id"));
					if(!goalIdsList.contains(rs.getString("goal_id"))){
						goalIdsList.add(rs.getString("goal_id"));
					}
				}
				rs.close();
				pst.close();
			}
//			System.out.println("goalIdsList ===> "+goalIdsList);
			request.setAttribute("goalIdsList", goalIdsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCallFrom() {
		return callFrom;
	}

	public void setCallFrom(String callFrom) {
		this.callFrom = callFrom;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public String getAnsType() {
		return ansType;
	}

	public void setAnsType(String ansType) {
		this.ansType = ansType;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
	}

}
