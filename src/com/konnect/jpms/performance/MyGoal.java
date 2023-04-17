package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class MyGoal implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	String strEmpOrgId;
	CommonFunctions CF; 

	private String dataType;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/MyGoal.jsp");
		request.setAttribute(TITLE, "My Goal");
		
		getIndividualDetails();
		getGoalTypeDetails();
		getTargetDetails();
		getGoalKRADetails();
		getPersonalGoalAndTarget();
		getGoalRating();
		getKRARating();
		getTeamGoalAverageDetails();
		getManagerGoalAverageDetails();
		return "success";

	}
	
	void getGoalTypeDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> hmGoalType = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpName", hmEmpName);
			
			pst = con.prepareStatement("select * from goal_type_details order by goal_type_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmGoalType.put(rs.getString("goal_type_id"),rs.getString("goal_type_name"));				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmGoalType",hmGoalType);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	public void getManagerGoalAverageDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
//				pst=con.prepareStatement("select goal_parent_id, goal_id from goal_details where (goal_type= 4 or (goal_type= 5 and goalalign_with_teamgoal = true)) " +
//						" and org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strSessionEmpId+",%' order by goal_id desc");
			pst=con.prepareStatement("select goal_parent_id,goal_id from goal_details where goal_type = ? and goal_id in(select goal_parent_id from " +
					"goal_details where (goal_type= ? or (goal_type= ? and goalalign_with_teamgoal = true)) and " +
					"org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strSessionEmpId+",%') order by goal_id desc ");
			pst.setInt(1, TEAM_GOAL);
			pst.setInt(2, INDIVIDUAL_GOAL);
			pst.setInt(3, PERSONAL_GOAL);
//			System.out.println("pst ======> " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmTeamGoalIdManagerwise = new HashMap<String, List<String>>();
			List<String> teanGoalIdList = new ArrayList<String>();
			while (rs.next()) {
				teanGoalIdList = hmTeamGoalIdManagerwise.get(rs.getString("goal_parent_id"));
				if(teanGoalIdList == null) teanGoalIdList = new ArrayList<String>();
				teanGoalIdList.add(rs.getString("goal_id"));
				hmTeamGoalIdManagerwise.put(rs.getString("goal_parent_id"), teanGoalIdList);
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmTeamGoalAverage = (Map<String, String>) request.getAttribute("hmTeamGoalAverage");
			
			Map<String, String> hmManagerGoalAverage = new HashMap<String, String>();
//			System.out.println("hmTeamGoalIdManagerwise =====> " + hmTeamGoalIdManagerwise);
			
			Iterator<String> it = hmTeamGoalIdManagerwise.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				List<String> teanGoalIdList1 = hmTeamGoalIdManagerwise.get(key);
			double managerAllGoalAverage = 0.0d;
			double mangerGoalAverage = 0.0d;
			int teamGoalCnt = 0;
			for(int i=0; teanGoalIdList1 != null && !teanGoalIdList1.isEmpty() && i< teanGoalIdList1.size(); i++) {
				if(hmManagerGoalAverage != null && !hmManagerGoalAverage.isEmpty()){
					if(hmManagerGoalAverage.get(teanGoalIdList1.get(i)) != null) {
						managerAllGoalAverage += uF.parseToDouble(hmManagerGoalAverage.get(teanGoalIdList1.get(i)));
						teamGoalCnt++;
					}
				}
			}
			if(teamGoalCnt > 0){
				mangerGoalAverage = managerAllGoalAverage / teamGoalCnt;
			}
			hmManagerGoalAverage.put(key, ""+mangerGoalAverage);
			}
//			System.out.println("hmManagerGoalAverage =====> " + hmManagerGoalAverage);
			request.setAttribute("hmManagerGoalAverage", hmManagerGoalAverage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getTeamGoalAverageDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			pst=con.prepareStatement("select goal_parent_id, goal_id from goal_details where (goal_type= ? or (goal_type= ? and goalalign_with_teamgoal = true)) " +
					" and org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strSessionEmpId+",%' order by goal_id desc");
			pst.setInt(1, INDIVIDUAL_GOAL);
			pst.setInt(2, PERSONAL_GOAL);
//			System.out.println("pst ======> " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmGoalIdTeamwise = new HashMap<String, List<String>>();
			List<String> goalIdList = new ArrayList<String>();
			while (rs.next()) {
				goalIdList = hmGoalIdTeamwise.get(rs.getString("goal_parent_id"));
				if(goalIdList == null) goalIdList = new ArrayList<String>();
				goalIdList.add(rs.getString("goal_id"));
				hmGoalIdTeamwise.put(rs.getString("goal_parent_id"), goalIdList);
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmGoalAverage = (Map<String, String>) request.getAttribute("hmGoalAverage");
			
			Map<String, String> hmTeamGoalAverage = new HashMap<String, String>();
			//System.out.println("hmGoalIdTeamwise =====> " + hmGoalIdTeamwise);
			
			Iterator<String> it = hmGoalIdTeamwise.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				List<String> goalIdList1 = hmGoalIdTeamwise.get(key);
			double teamAllGoalAverage = 0.0d;
			double teamGoalAverage = 0.0d;
			int teamGoalCnt = 0;
			for(int i=0; goalIdList1 != null && !goalIdList1.isEmpty() && i< goalIdList1.size(); i++) {
				if(hmGoalAverage != null && !hmGoalAverage.isEmpty()){
					if(hmGoalAverage.get(goalIdList1.get(i)) != null) {
						teamAllGoalAverage += uF.parseToDouble(hmGoalAverage.get(goalIdList1.get(i)));
						teamGoalCnt++;
					}
				}
			}
			if(teamGoalCnt > 0){
				teamGoalAverage = teamAllGoalAverage / teamGoalCnt;
			}
			hmTeamGoalAverage.put(key, ""+teamGoalAverage);
			}
//			System.out.println("hmTeamGoalAverage =====> " + hmTeamGoalAverage);
			request.setAttribute("hmTeamGoalAverage", hmTeamGoalAverage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	public void getKRARating() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try { 
			con = db.makeConnection(con);
			pst=con.prepareStatement("select question_bank_id,aaa.kra_id,average,kra_description from (select question_bank_id,question_text,kra_id," +
				"(marks*100/weightage) as average from (select question_bank_id,question_text,kra_id from question_bank where goal_kra_target_id in " +
				"(select goal_id from goal_details where measure_kra = 'KRA' and emp_ids like '%,"+strSessionEmpId+",%' and (goal_type = ? or goal_type = ? " +
				"or goal_type = ?))) as a,appraisal_question_answer aqa where a.question_bank_id=aqa.question_id and weightage>0) as aaa, goal_kras gk where aaa.kra_id = gk.goal_kra_id");
			pst.setInt(1, INDIVIDUAL_GOAL); 
			pst.setInt(2, INDIVIDUAL_KRA);
			pst.setInt(3, EMPLOYEE_KRA);
			//System.out.println("pst ===>> " + pst);
			rs=pst.executeQuery();
			Map<String, String> hmKraAverage = new HashMap<String, String>();
			while (rs.next()) {
				hmKraAverage.put(rs.getString("kra_id"), rs.getString("average"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmKraAverage ====>>> " + hmKraAverage);
			request.setAttribute("hmKraAverage", hmKraAverage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	public void getGoalRating() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try { 
			con = db.makeConnection(con);
//			pst=con.prepareStatement("select question_bank_id,aaa.goal_kra_target_id,goal_title,average from (select question_bank_id,question_text," +
//					"goal_kra_target_id,(marks*100/weightage) as average from (select question_bank_id,question_text,goal_kra_target_id from " +
//					"question_bank where goal_kra_target_id in(select goal_id from goal_details where is_measure_kra = false and emp_ids like " +
//					"'%,"+strSessionEmpId+",%' and (goal_type = ? or goal_type = ?))) as a, appraisal_question_answer aqa where " +
//					"a.question_bank_id=aqa.question_id) as aaa, goal_details gd where aaa.goal_kra_target_id = gd.goal_id");
			pst=con.prepareStatement("select goal_kra_target_id,emp_id,sum(average)/count(goal_kra_target_id) as avg from(select goal_kra_target_id," +
					" emp_id,(marks*100/weightage) as average from (select question_bank_id,question_text,goal_kra_target_id from question_bank " +
					" where goal_kra_target_id in(select goal_id from goal_details where  (goal_type = ? or goal_type = ? or goal_type = ?) and " +
					" emp_ids like '%,"+strSessionEmpId+",%')) as a, appraisal_question_answer aqa where a.question_bank_id=aqa.question_id and weightage>0) b " +
					" where emp_id = ? group by  goal_kra_target_id,emp_id");
			pst.setInt(1, INDIVIDUAL_GOAL); 
			pst.setInt(2, PERSONAL_GOAL);
			pst.setInt(3, INDIVIDUAL_TARGET);
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
//			System.out.println("pst ===>> " + pst);
			rs=pst.executeQuery();
			Map<String, String> hmGoalAverage = new HashMap<String, String>();
			while (rs.next()) {
				hmGoalAverage.put(rs.getString("goal_kra_target_id"), rs.getString("avg"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmGoalAverage =====>> "  +hmGoalAverage);
			request.setAttribute("hmGoalAverage", hmGoalAverage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	void getGoalKRADetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			Map<String, List<List<String>>> hmGoalKra= new HashMap<String, List<List<String>>>(); 
			con = db.makeConnection(con);
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			
//			for(int i=0;empList!=null && !empList.isEmpty() && i<empList.size();i++){
				/*pst = con.prepareStatement("select * from goal_details  where emp_ids like '%,"+empList.get(i)+ ",%' and goal_type=4 and (measure_type='' or measure_type is null) order by goal_id");*/
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from goal_details gd,goal_kras gk  where gd.emp_ids like '%,"+strSessionEmpId+ ",%' and " +
						" gd.goal_type= ? and (gd.measure_type='' or gd.measure_type is null) and gd.goal_id=gk.goal_id ");
				if(getDataType() != null && getDataType().equals("L")) {
					sbQuery.append(" and gd.is_close = false ");
				} else if(getDataType() != null && getDataType().equals("C")) {
					sbQuery.append(" and gd.is_close = true");
				}
				sbQuery.append(" order by gd.goal_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, INDIVIDUAL_GOAL);
//				System.out.println("pst ========> "  + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<List<String>> outerList=hmGoalKra.get(rs.getString("goal_id"));
					if(outerList==null) outerList=new ArrayList<List<String>>();
					
					List<String> innerList=new ArrayList<String>();
					innerList.add(rs.getString("goal_type"));
					innerList.add(rs.getString("goal_id"));
					innerList.add(rs.getString("kra_description"));
					innerList.add(rs.getString("goal_title"));
					if(uF.parseToInt(strSessionEmpId)>0 && uF.parseToInt(strSessionEmpId)==uF.parseToInt(rs.getString("user_id"))){
						innerList.add("Self");
					}else{
					innerList.add(uF.showData(hmEmpCodeName.get(rs.getString("user_id")), "-"));
					}
					innerList.add(uF.showData(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()),"-"));
					
					outerList.add(innerList);
					
					hmGoalKra.put(rs.getString("goal_id"), outerList);
				}
				rs.close();
				pst.close();
//			}
//				System.out.println("hmGoalKra ======>   "+ hmGoalKra);
				
			request.setAttribute("hmGoalKra1", hmGoalKra);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	void getTargetDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpCodeName", hmEmpCodeName);
			Map<String,String> hmGoalName = getGoalsName(con);
			Map<String, List<String>> hmGoalTarget = new HashMap<String, List<String>>();
			Map<String, List<String>> hmIndividualTarget= new HashMap<String, List<String>>();
			
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from goal_details gd where gd.emp_ids like '%,"+strSessionEmpId+ ",%' and (gd.goal_type=? or " +
						"gd.goal_type= ?) and org_id ="+uF.parseToInt(strEmpOrgId)+" and is_measure_kra = true and gd.measure_type !='' ");
				if(getDataType() != null && getDataType().equals("L")) {
					sbQuery.append(" and is_close = false ");
				} else if(getDataType() != null && getDataType().equals("C")) {
					sbQuery.append("and is_close = true ");
				}
				sbQuery.append(" order by gd.goal_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, INDIVIDUAL_GOAL);
				pst.setInt(2, PERSONAL_GOAL);
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
				while (rs.next()) {
					List<String> innerList=new ArrayList<String>();
					innerList.add(rs.getString("goal_type"));
					innerList.add(rs.getString("goal_id"));
					innerList.add(rs.getString("goal_objective"));
					innerList.add(rs.getString("goal_title"));
					
					innerList.add(rs.getString("measure_type"));
					String val="",daysHRVal="";
					if(rs.getString("measure_type")!=null && rs.getString("measure_type").equals("Effort")){
						val=" "+rs.getString("measure_effort_days")+" Days and "+rs.getString("measure_effort_hrs")+" HRs.";
						daysHRVal = (rs.getString("measure_effort_days") != null && !rs.getString("measure_effort_days").equals("") ? rs.getString("measure_effort_days") : "0")+"."
						+(rs.getString("measure_effort_hrs") != null && !rs.getString("measure_effort_hrs").equals("") ? rs.getString("measure_effort_hrs") : "0");
					} else {
//						val= CF.getAmountInCrAndLksFormat(rs.getDouble("measure_currency_value"));
						val= uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("measure_currency_value")));
					}
					
					innerList.add(val);
					if(uF.parseToInt(strSessionEmpId)>0 && uF.parseToInt(strSessionEmpId)==uF.parseToInt(rs.getString("user_id"))){
						innerList.add("Self"); //6
					}else{
						innerList.add(hmEmpCodeName.get(rs.getString("user_id"))); //6
					}
					innerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat())); //7
					innerList.add(daysHRVal); //8
					innerList.add(""+uF.parseToBoolean(rs.getString("goalalign_with_teamgoal"))); //9
					innerList.add(uF.showData(hmGoalName.get(rs.getString("goal_parent_id")), "")); //10
					innerList.add(rs.getString("is_close")); //11
					String tGoalId = getPerentGoalId(con, uF, rs.getString("goal_id"));
					innerList.add(tGoalId); // team goal id 12
					String mGoalId = getPerentGoalId(con, uF, tGoalId);
					innerList.add(mGoalId); // manager goal id 13
					String cGoalId = getPerentGoalId(con, uF, mGoalId);
					innerList.add(cGoalId); // corporate goal id 14
					
					String priority="";
					String pClass="";
					if(rs.getString("priority")!=null && !rs.getString("priority").equals("")){
						if(rs.getString("priority").equals("1")){
							pClass="high";
							priority="High";
						}else if(rs.getString("priority").equals("2")){
							pClass="medium";
							priority="Medium";
						}else if(rs.getString("priority").equals("3")){
							pClass="low";
							priority="Low";
						}
					}
					innerList.add(priority); // Priority 15
					innerList.add(pClass); // Priority class 16
					
					hmGoalTarget.put(rs.getString("goal_id"), innerList);
					
				}
				rs.close();
				pst.close();
				
				StringBuilder sbQuery1 = new StringBuilder();
				sbQuery1.append("select * from goal_details where goal_type = ? and org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strSessionEmpId+ ",%' ");
				if(getDataType() != null && getDataType().equals("L")) {
					sbQuery1.append(" and is_close = false ");
				} else if(getDataType() != null && getDataType().equals("C")) {
					sbQuery1.append("and is_close = true ");
				}
				pst=con.prepareStatement(sbQuery1.toString());
				pst.setInt(1, INDIVIDUAL_TARGET);
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
				while (rs.next()) {
					List<String> innerList=new ArrayList<String>();
					innerList.add(rs.getString("goal_type"));
					innerList.add(rs.getString("goal_id"));
					innerList.add(rs.getString("goal_objective"));
					innerList.add(rs.getString("goal_title"));
					innerList.add(rs.getString("measure_type"));
					String val="",daysHRVal="";
					if(rs.getString("measure_type")!=null && rs.getString("measure_type").equals("Effort")){
						val=" "+rs.getString("measure_effort_days")+" Days and "+rs.getString("measure_effort_hrs")+" HRs.";
						daysHRVal = (rs.getString("measure_effort_days") != null && !rs.getString("measure_effort_days").equals("") ? rs.getString("measure_effort_days") : "0")+"."
						+(rs.getString("measure_effort_hrs") != null && !rs.getString("measure_effort_hrs").equals("") ? rs.getString("measure_effort_hrs") : "0");
					}else{
						val= uF.formatIntoComma(uF.parseToDouble(rs.getString("measure_currency_value")));
					}
					
					innerList.add(val); //5
					if(uF.parseToInt(strSessionEmpId)>0 && uF.parseToInt(strSessionEmpId)==uF.parseToInt(rs.getString("user_id"))){
						innerList.add("Self"); //6
					}else{
						innerList.add(hmEmpCodeName.get(rs.getString("user_id")));//6
					}
					innerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat())); //7
					innerList.add(daysHRVal); //8
					innerList.add(rs.getString("is_close")); //9
					
					String priority="";
					String pClass="";
					if(rs.getString("priority")!=null && !rs.getString("priority").equals("")){
						if(rs.getString("priority").equals("1")){
							pClass="high";
							priority="High";
						}else if(rs.getString("priority").equals("2")){
							pClass="medium";
							priority="Medium";
						}else if(rs.getString("priority").equals("3")){
							pClass="low";
							priority="Low";
						}
					}
					innerList.add(priority); // Priority 10
					innerList.add(pClass); // Priority class 11
					
					hmIndividualTarget.put(rs.getString("goal_id"), innerList);
				}
				rs.close();
				pst.close();
				
			
			Map<String, String> hmTargetValue= new HashMap<String,String>();
			Map<String, String> hmTargetID= new HashMap<String,String>();
			Map<String, String> hmTargetRemark= new HashMap<String,String>();
			Map<String, String> hmTargetTmpValue= new HashMap<String,String>();
			Map<String, String> hmUpdateBy= new HashMap<String,String>();
			pst = con.prepareStatement("select * from target_details where target_id in (select max(target_id) from target_details where emp_id = ? group by goal_id)");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs= pst.executeQuery();
			while(rs.next()){
				hmTargetValue.put(rs.getString("goal_id"), rs.getString("amt_percentage"));
				hmTargetID.put(rs.getString("goal_id"), rs.getString("target_id"));
				hmTargetRemark.put(rs.getString("goal_id"), rs.getString("target_remark"));
				hmTargetTmpValue.put(rs.getString("goal_id"), rs.getString("emp_amt_percentage"));
				hmUpdateBy.put(rs.getString("goal_id"), hmEmpCodeName.get(rs.getString("added_by"))+" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
			rs=pst.executeQuery();
			Map<String,String> empImageMap=new HashMap<String,String>();
			while(rs.next()){
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmTargetValue =====> "  + hmTargetValue);
			request.setAttribute("empImageMap", empImageMap);
			request.setAttribute("hmUpdateBy1", hmUpdateBy);
			request.setAttribute("hmTargetValue1", hmTargetValue);
			request.setAttribute("hmTargetID1", hmTargetID);
			request.setAttribute("hmTargetRemark1", hmTargetRemark);
			
			request.setAttribute("hmTargetTmpValue1", hmTargetTmpValue);
			request.setAttribute("hmGoalTarget1", hmGoalTarget);
//			System.out.println("hmIndividualTarget =====> "  + hmIndividualTarget);
			request.setAttribute("hmIndividualTarget1", hmIndividualTarget);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	void getPersonalGoalAndTarget() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			UtilityFunctions uF=new UtilityFunctions();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from goal_details where (goal_type=? and goalalign_with_teamgoal=false) and org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strSessionEmpId+",%' ");
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append("and is_close = true ");
			}
			sbQuery.append(" order by goal_id desc");
			pst = con.prepareStatement(sbQuery.toString());//(goal_type=4 or goal_type=5)
			pst.setInt(1, PERSONAL_GOAL);
//			pst.setInt(2, INDIVIDUAL_GOAL);
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
//			Map<String,List<List<String>>> hmIndGoalIdWithType = new HashMap<String, List<List<String>>>();
			List<String> personalGoalIDs = new ArrayList<String>();
			List<String> personalTargetIDs = new ArrayList<String>();
			while (rs.next()) {
				if(uF.parseToBoolean(rs.getString("is_measure_kra")) == false) {
					personalGoalIDs.add(rs.getString("goal_id"));
				} else if(uF.parseToBoolean(rs.getString("is_measure_kra")) == true && rs.getString("measure_type")!=null && !rs.getString("measure_type").equals("")) { //is_measure_kra = true and gd.measure_type !=''
					personalTargetIDs.add(rs.getString("goal_id"));
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("personalTargetIDs ===> " + personalTargetIDs);
//			System.out.println("personalGoalIDs ===> " + personalGoalIDs);
			
			request.setAttribute("personalTargetIDs", personalTargetIDs);
			request.setAttribute("personalGoalIDs", personalGoalIDs);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	void getIndividualDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			UtilityFunctions uF=new UtilityFunctions();
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<List<String>>> hmIndividual = new HashMap<String, List<List<String>>>();
			Map<String,List<String>> hmIndividualGoal = new HashMap<String, List<String>>();
			Map<String,String> hmAttribute=getAttributeMap(con);
			Map<String,String> hmGoalName = getGoalsName(con);
			request.setAttribute("hmGoalName", hmGoalName);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from goal_details where (goal_type=? or (goal_type=? and is_measure_kra = false)) and org_id ="+uF.parseToInt(strEmpOrgId)+" and emp_ids like '%,"+strSessionEmpId+",%' ");
			if(getDataType() != null && getDataType().equals("L")) {
				sbQuery.append(" and is_close = false ");
			} else if(getDataType() != null && getDataType().equals("C")) {
				sbQuery.append(" and is_close = true ");
			}
			sbQuery.append(" order by goal_id desc");
			pst = con.prepareStatement(sbQuery.toString());//(goal_type=4 or goal_type=5)
			pst.setInt(1, PERSONAL_GOAL);
			pst.setInt(2, INDIVIDUAL_GOAL);
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
				String val="",daysHRVal="";
				if(rs.getString("measure_type")!=null && rs.getString("measure_type").equals("Effort")){
					val=" "+rs.getString("measure_effort_days")+" Days and "+rs.getString("measure_effort_hrs")+" HRs.";
					daysHRVal = (rs.getString("measure_effort_days") != null && !rs.getString("measure_effort_days").equals("") ? rs.getString("measure_effort_days") : "0")+"."
					+(rs.getString("measure_effort_hrs") != null && !rs.getString("measure_effort_hrs").equals("") ? rs.getString("measure_effort_hrs") : "0");
				}else{
//					val= CF.getAmountInCrAndLksFormat(rs.getDouble("measure_currency_value"));
					val= uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("measure_currency_value")));
				}
				
				innerList.add(val);
//				innerList.add(rs.getString("measure_currency_value"));
				innerList.add(rs.getString("measure_currency_id"));
				innerList.add(rs.getString("measure_effort_days"));
				innerList.add(rs.getString("measure_effort_hrs"));
				innerList.add(rs.getString("measure_type1"));
				innerList.add(rs.getString("measure_kra"));
				innerList.add(rs.getString("measure_currency_value1"));
				innerList.add(rs.getString("measure_currency1_id"));
				innerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()));
				innerList.add(rs.getString("is_feedback"));
				innerList.add(rs.getString("orientation_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add(uF.showData(getAppendData(con, rs.getString("emp_ids"), hmEmpName), "")); //20
				//innerList.add(uf.showData(getAppendData(strSessionEmpId, hmEmpName), ""));
				innerList.add(rs.getString("entry_date")); //21
				if(uF.parseToInt(strSessionEmpId)>0 && uF.parseToInt(strSessionEmpId)==uF.parseToInt(rs.getString("user_id"))){
					innerList.add("Self"); //22
				}else{
				innerList.add(uF.showData(hmEmpName.get(rs.getString("user_id")), "-")); //22
				}
//				innerList.add(rs.getString("user_id")); //22
				innerList.add(rs.getString("is_measure_kra")); 
				innerList.add(rs.getString("measure_kra_days"));
				innerList.add(rs.getString("measure_kra_hrs"));
				innerList.add(rs.getString("grade_id"));
				innerList.add(rs.getString("level_id"));
				innerList.add(rs.getString("kra"));
				
				String priority="";
				String pClass="";
				if(rs.getString("priority")!=null && !rs.getString("priority").equals("")){
					if(rs.getString("priority").equals("1")){
						pClass="high";
						priority="High";
					}else if(rs.getString("priority").equals("2")){
						pClass="medium";
						priority="Medium";
					}else if(rs.getString("priority").equals("3")){
						pClass="low";
						priority="Low";
					}
				}
				innerList.add(priority);
				innerList.add(daysHRVal); //30
				innerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat())); //31
				innerList.add(pClass); //32
				innerList.add(""+uF.parseToBoolean(rs.getString("goalalign_with_teamgoal"))); //33
				innerList.add(uF.showData(hmGoalName.get(rs.getString("goal_parent_id")), "")); //34
				innerList.add(rs.getString("is_close")); //35
				
				String tGoalId = getPerentGoalId(con, uF, rs.getString("goal_id"));
				innerList.add(tGoalId); // team goal id 36
				String mGoalId = getPerentGoalId(con, uF, tGoalId);
				innerList.add(mGoalId); // manager goal id 37
				String cGoalId = getPerentGoalId(con, uF, mGoalId);
				innerList.add(cGoalId); // Corporate goal id 38
				
				outerList.add(innerList);
				hmIndividualGoal.put(rs.getString("goal_id"), innerList);
				hmIndividual.put(rs.getString("goal_parent_id"), outerList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmIndividualGoal =====> " + hmIndividualGoal);
			request.setAttribute("hmIndividualGoal", hmIndividualGoal);
			request.setAttribute("hmIndividual", hmIndividual);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	private String getPerentGoalId(Connection con, UtilityFunctions uF, String goalID) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String goalId = null;
		try {
			String query1 = "select goal_parent_id from goal_details where goal_id = ?";
			pst = con.prepareStatement(query1);
			pst.setInt(1, uF.parseToInt(goalID));
			rs = pst.executeQuery();
			while (rs.next()) {
				goalId = rs.getString("goal_parent_id");
			}
			rs.close();
			pst.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goalId;
	}
	
	
	private Map<String, String> getGoalsName(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmGoalName=new HashMap<String, String>();
		try {
			
			pst = con.prepareStatement("select * from goal_details");
			rs=pst.executeQuery();
			
			while(rs.next()){
				hmGoalName.put(rs.getString("goal_id"), rs.getString("goal_title"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmGoalName;
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
	
	private String getAppendData(Connection con, String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> hmDesignation = CF.getEmpDesigMap(con);

		if (strID != null && !strID.equals("")) {

			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 1; i < temp.length; i++) {
					if (i == 1) {
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
	
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}
}
