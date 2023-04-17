package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.OrganisationalChart;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GoalChart  extends ActionSupport implements  ServletRequestAware, IStatements  {

	HttpSession session;
	CommonFunctions CF;
	private String strEmpId;
	String strUserType;
	private static Logger log = Logger.getLogger(OrganisationalChart.class);
	
	public String execute() throws Exception {
		
		session = request.getSession(true);
		strUserType = (String)session.getAttribute(USERTYPE);
		strEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/performance/GoalChart.jsp");
		request.setAttribute(TITLE, "Goal Chart");
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();	
		getHireracyLevels(uF);
		getEmpImage(uF);
		 		
		return SUCCESS;
	}
	
	
	private void getEmpImage(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpName", hmEmpName);
			pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
			rs=pst.executeQuery();
			Map<String,String> empImageMap=new HashMap<String,String>();
			while(rs.next()){
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("empImageMap", empImageMap);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getHireracyLevels(UtilityFunctions uF){
		getCorporateDetails(uF);
		getManagerDetails(uF);
		getTeamDetails(uF);
		getIndividualDetails(uF);	
	}
	
	private void getIndividualDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<List<String>>> hmIndividual=new HashMap<String, List<List<String>>>();
			Map<String,String> hmAttribute=getAttributeMap(con);
			
			pst = con.prepareStatement("select * from goal_details where goal_type=4");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> outerList=hmIndividual.get(rs.getString("goal_parent_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("goal_parent_id"));
				innerList.add(rs.getString("goal_title"));
				innerList.add(rs.getString("goal_objective"));
				innerList.add(rs.getString("goal_description"));
				innerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),""));
				innerList.add(rs.getString("measure_type"));
				innerList.add(rs.getString("measure_currency_value"));
				innerList.add(rs.getString("measure_currency_id"));
				innerList.add(rs.getString("measure_effort_days"));
				innerList.add(rs.getString("measure_effort_hrs"));
				innerList.add(rs.getString("measure_type1"));
				innerList.add(rs.getString("measure_kra"));
				innerList.add(rs.getString("measure_currency_value1"));
				innerList.add(rs.getString("measure_currency1_id"));
				innerList.add(rs.getString("due_date"));
				innerList.add(rs.getString("is_feedback"));
				innerList.add(rs.getString("orientation_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add(uF.showData(getAppendData(con,rs.getString("emp_ids"), hmEmpName), ""));
				innerList.add(rs.getString("entry_date"));
				innerList.add(rs.getString("user_id"));
				innerList.add(rs.getString("is_measure_kra"));
				innerList.add(rs.getString("measure_kra_days"));
				innerList.add(rs.getString("measure_kra_hrs"));
				innerList.add(rs.getString("grade_id"));
				innerList.add(rs.getString("level_id"));
				innerList.add(rs.getString("kra"));
				innerList.add(rs.getString("emp_ids"));
				
				outerList.add(innerList);
				hmIndividual.put(rs.getString("goal_parent_id"), outerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmIndividual",hmIndividual);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}


	private void getTeamDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<List<String>>> hmTeam=new HashMap<String, List<List<String>>>();
			Map<String,String> hmAttribute=getAttributeMap(con);
				
			pst = con.prepareStatement("select * from goal_details where goal_type=3");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> outerList=hmTeam.get(rs.getString("goal_parent_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("goal_parent_id"));
				innerList.add(rs.getString("goal_title"));
				innerList.add(rs.getString("goal_objective"));
				innerList.add(rs.getString("goal_description"));
				innerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),""));
				innerList.add(rs.getString("measure_type"));
				innerList.add(rs.getString("measure_currency_value"));
				innerList.add(rs.getString("measure_currency_id"));
				innerList.add(rs.getString("measure_effort_days"));
				innerList.add(rs.getString("measure_effort_hrs"));
				innerList.add(rs.getString("measure_type1"));
				innerList.add(rs.getString("measure_kra"));
				innerList.add(rs.getString("measure_currency_value1"));
				innerList.add(rs.getString("measure_currency1_id"));
				innerList.add(rs.getString("due_date"));
				innerList.add(rs.getString("is_feedback"));
				innerList.add(rs.getString("orientation_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add(uF.showData(getAppendData(con,rs.getString("emp_ids"), hmEmpName), ""));
				innerList.add(rs.getString("entry_date"));
				innerList.add(rs.getString("user_id"));
				innerList.add(rs.getString("is_measure_kra"));
				innerList.add(rs.getString("measure_kra_days"));
				innerList.add(rs.getString("measure_kra_hrs"));
				innerList.add(rs.getString("grade_id"));
				innerList.add(rs.getString("level_id"));
				innerList.add(rs.getString("kra"));
				innerList.add(rs.getString("emp_ids"));
				
				outerList.add(innerList);
				hmTeam.put(rs.getString("goal_parent_id"), outerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmTeam", hmTeam);
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getManagerDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<List<String>>> hmManager=new HashMap<String, List<List<String>>>();
			Map<String,String> hmAttribute=getAttributeMap(con);
				
			pst = con.prepareStatement("select * from goal_details where goal_type=2");
			rs = pst.executeQuery();
		
			while (rs.next()) {
				List<List<String>> outerList=hmManager.get(rs.getString("goal_parent_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("goal_parent_id"));
				innerList.add(rs.getString("goal_title"));
				innerList.add(rs.getString("goal_objective"));
				innerList.add(rs.getString("goal_description"));
				innerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),""));
				innerList.add(rs.getString("measure_type"));
				innerList.add(rs.getString("measure_currency_value"));
				innerList.add(rs.getString("measure_currency_id"));
				innerList.add(rs.getString("measure_effort_days"));
				innerList.add(rs.getString("measure_effort_hrs"));
				innerList.add(rs.getString("measure_type1"));
				innerList.add(rs.getString("measure_kra"));
				innerList.add(rs.getString("measure_currency_value1"));
				innerList.add(rs.getString("measure_currency1_id"));
				innerList.add(rs.getString("due_date"));
				innerList.add(rs.getString("is_feedback"));
				innerList.add(rs.getString("orientation_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add(uF.showData(getAppendData(con,rs.getString("emp_ids"), hmEmpName), ""));
				innerList.add(rs.getString("entry_date"));
				innerList.add(rs.getString("user_id"));
				innerList.add(rs.getString("is_measure_kra"));
				innerList.add(rs.getString("measure_kra_days"));
				innerList.add(rs.getString("measure_kra_hrs"));
				innerList.add(rs.getString("grade_id"));
				innerList.add(rs.getString("level_id"));
				innerList.add(rs.getString("kra"));
				innerList.add(rs.getString("emp_ids"));
				
				outerList.add(innerList);
				hmManager.put(rs.getString("goal_parent_id"), outerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmManager",hmManager);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	String strGoalId;
	private void getCorporateDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<String>> hmCorporate=new HashMap<String, List<String>>();
			Map<String,String> hmAttribute=getAttributeMap(con);
				
			if(getStrGoalId()!=null){
				pst = con.prepareStatement("select * from goal_details where goal_type=1 and goal_id=?");
				pst.setInt(1, uF.parseToInt(getStrGoalId()));
			}else{
				pst = con.prepareStatement("select * from goal_details where goal_type=1");
				rs = pst.executeQuery();	
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> cinnerList=new ArrayList<String>();
				cinnerList.add(rs.getString("goal_id"));
				cinnerList.add(rs.getString("goal_type"));
				cinnerList.add(rs.getString("goal_parent_id"));
				cinnerList.add(rs.getString("goal_title"));
				cinnerList.add(rs.getString("goal_objective"));
				cinnerList.add(rs.getString("goal_description"));
				cinnerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),""));
				cinnerList.add(rs.getString("measure_type"));
				cinnerList.add(rs.getString("measure_currency_value"));
				cinnerList.add(rs.getString("measure_currency_id"));
				cinnerList.add(rs.getString("measure_effort_days"));
				cinnerList.add(rs.getString("measure_effort_hrs"));
				cinnerList.add(rs.getString("measure_type1"));
				cinnerList.add(rs.getString("measure_kra"));
				cinnerList.add(rs.getString("measure_currency_value1"));
				cinnerList.add(rs.getString("measure_currency1_id"));
				cinnerList.add(rs.getString("due_date"));
				cinnerList.add(rs.getString("is_feedback"));
				cinnerList.add(rs.getString("orientation_id"));
				cinnerList.add(rs.getString("weightage"));
				cinnerList.add(uF.showData(getAppendData(con,rs.getString("emp_ids"), hmEmpName), ""));
				cinnerList.add(rs.getString("entry_date"));
				cinnerList.add(rs.getString("user_id"));
				cinnerList.add(rs.getString("is_measure_kra"));
				cinnerList.add(rs.getString("measure_kra_days"));
				cinnerList.add(rs.getString("measure_kra_hrs"));
				cinnerList.add(rs.getString("grade_id"));
				cinnerList.add(rs.getString("level_id"));
				cinnerList.add(rs.getString("kra"));
				cinnerList.add(rs.getString("emp_ids"));
				
				hmCorporate.put(rs.getString("goal_id"), cinnerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmCorporate",hmCorporate);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private Map<String, String> getAttributeMap(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmAttribute=new HashMap<String, String>();
		try {
			
			pst = con.prepareStatement("select * from appraisal_attribute where status=true");
			rs=pst.executeQuery();
			while(rs.next()){
				hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmAttribute;
	}
	
	
	private String getAppendData(Connection con,String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
		if (strID != null && !strID.equals("")) {

			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim())+"("+hmDesignation.get(temp[i].trim())+")");
					} else {
						sb.append("," + mp.get(temp[i].trim())+"("+hmDesignation.get(temp[i].trim())+")");
					}
				}
			} else {
				return mp.get(strID)+"("+hmDesignation.get(strID)+")";
			}

		} else {
			return null;
		}

		return sb.toString();
	}
	

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getStrGoalId() {
		return strGoalId;
	}


	public void setStrGoalId(String strGoalId) {
		this.strGoalId = strGoalId;
	}
}
