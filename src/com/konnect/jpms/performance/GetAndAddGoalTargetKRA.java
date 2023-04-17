package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetAndAddGoalTargetKRA extends ActionSupport implements ServletRequestAware, SessionAware,
		IStatements {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Map session;
	CommonFunctions CF;

	String strUserType = null; 
	String strSessionEmpId = null;

	private String id;
	private String callFrom;
	private String systemType;
	private String ansType;
	private String subSectionId;
	private String attributeId;
	private String sysType;
	private String divCount;
	private String type;
	private String queID;
	private String appFreqId;
	public String execute() {
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";
		
		String submit = request.getParameter("submit");
		if(submit == null && type == null){
			if(sysType != null && sysType.equals("3")){
				setSystemType("goal");
			} else if(sysType != null && sysType.equals("4")){
				setSystemType("KRA");
			} else if(sysType != null && sysType.equals("5")){
				setSystemType("target");
			}
		getAppraisalDetail();
			return LOAD;
		}
		if(submit != null){
			//System.out.println("sysType ===> "+sysType);
			if(sysType != null && sysType.equals("4")){
				addKRA();
			} else if(sysType != null && (sysType.equals("3") || sysType.equals("5"))){
				addGoalTarget();
			}
			return SUCCESS;
		}
		
		if(type != null && type.equals("delete")){
			deleteGoalTargetKRA();
			return SUCCESS;
		}
		return LOAD;
	}

	
	private void deleteGoalTargetKRA() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if (queID != null && !queID.equals("")) {
				pst = con.prepareStatement("delete from appraisal_question_details where question_id in (" + queID + ")");
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("delete from question_bank where question_bank_id in (" + queID + ")");
				pst.executeUpdate();
				pst.close();
//				System.out.println("PST =====> "+pst);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
  	}

	
	private void addKRA() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		
		String[] goalId = request.getParameterValues("goalId");
		try {
			
			Map<String, String> hmKRAName = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from goal_kras where goal_type = 4");
			String kraTypes = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+EMPLOYEE_KRA;
			pst = con.prepareStatement("select * from goal_kras where goal_type in ("+kraTypes+")");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmKRAName.put(rst.getString("goal_kra_id"), rst.getString("kra_description"));
			}
			rst.close();
			pst.close();
			
		//===start parvez date: 17-03-2022===	
			Map<String, String> hmKRADescription = new HashMap<String, String>();
			pst = con.prepareStatement("select * from goal_details where goal_type in ("+kraTypes+")");
			rst = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rst.next()) {
				hmKRADescription.put(rst.getString("goal_id"), rst.getString("goal_description"));
			}
			rst.close();
			pst.close();
		//===end parvez date: 17-03-2022===
			
			for (int i = 0; goalId != null && i < goalId.length; i++) {
				String ansType = request.getParameter("ansType");
				/*int question_id = uF.parseToInt(hidequeid[i]);*/
				int question_id = 0;
//				System.out.println("goalId ====> "+goalId[i]);
				String optiona = request.getParameter("optiona"+ goalId[i]);
				String optionb = request.getParameter("optionb"+ goalId[i]);
				String optionc = request.getParameter("optionc"+ goalId[i]);
				String optiond = request.getParameter("optiond"+ goalId[i]);
				String optione = request.getParameter("optione"+ goalId[i]);
				String rateoptiona = request.getParameter("rateoptiona"+ goalId[i]);
				String rateoptionb = request.getParameter("rateoptionb"+ goalId[i]);
				String rateoptionc = request.getParameter("rateoptionc"+ goalId[i]);
				String rateoptiond = request.getParameter("rateoptiond"+ goalId[i]);
				String rateoptione = request.getParameter("rateoptione"+ goalId[i]);
				String goalWeightage = request.getParameter("goalWeightage"+ goalId[i]);
				String goalID = request.getParameter("goalID"+ goalId[i]);
//				System.out.println("GOal ID ===> "+goalId[i]+ "  optiona === "+optiona + "  optiona=b === "+optionb + "  optionc === "+optionc + "  optiond === "+optiond);
				
					String[] correct = request.getParameterValues("correct"+ goalId[i]);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}
					
					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans," +
						"is_add,question_type,goal_kra_target_id,app_system_type,kra_id)values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
					pst.setString(1, hmKRAName.get(goalId[i]));
					pst.setString(2, uF.showData(optiona, ""));
					pst.setString(3, uF.showData(optionb, ""));
					pst.setString(4, uF.showData(optionc, ""));
					pst.setString(5, uF.showData(optiond, ""));
					pst.setString(6, uF.showData(optione, ""));
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, false);
					pst.setInt(14, uF.parseToInt(ansType));
					pst.setInt(15, uF.parseToInt(goalID));
					pst.setInt(16, uF.parseToInt(sysType));
					pst.setInt(17, uF.parseToInt(goalId[i]));
					pst.executeUpdate();
					pst.close();
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}

//				pst = con.prepareStatement("insert into appraisal_question_details(question_id,attribute_id,appraisal_id,appraisal_level_id," +
//					"answer_type,goal_kra_target_id,app_system_type,weightage,kra_id) values(?,?,?,?, ?,?,?,?, ?)");
					
				pst = con.prepareStatement("insert into appraisal_question_details(question_id,attribute_id,appraisal_id,appraisal_level_id," +
					"answer_type,goal_kra_target_id,app_system_type,weightage,kra_id,other_short_description) values(?,?,?,?, ?,?,?,?, ?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, uF.parseToInt(attributeId));
				pst.setInt(3, uF.parseToInt(id));
				pst.setInt(4, uF.parseToInt(subSectionId));
				pst.setInt(5, uF.parseToInt(ansType));
				pst.setInt(6, uF.parseToInt(goalID));
				pst.setInt(7, uF.parseToInt(sysType));
				pst.setDouble(8, uF.parseToDouble(goalWeightage));
				pst.setInt(9, uF.parseToInt(goalId[i]));
				
			//===start parvez date: 17-03-2022===	
				pst.setString(10, hmKRADescription.get(goalID));
			//===end parvez date: 17-03-2022===
				pst.execute();
				pst.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void addGoalTarget() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		
		String[] goalId = request.getParameterValues("goalId");
		try {
			
			Map<String, String> hmGoalName = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from goal_details where goal_type = 4");
			String goalTypes =  INDIVIDUAL_GOAL+","+INDIVIDUAL_TARGET+","+PERSONAL_GOAL;
			pst = con.prepareStatement("select * from goal_details where goal_type in ("+goalTypes+")");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmGoalName.put(rst.getString("goal_id"), rst.getString("goal_title"));
			//===start parvez date: 17-03-2022===	
				hmGoalName.put(rst.getString("goal_id")+"_DESCRIPTION", rst.getString("goal_description"));
			//===end parvez date: 17-03-2022===
			}
			rst.close();
			pst.close();
			
			for (int i = 0; goalId != null && i < goalId.length; i++) {
				String ansType = request.getParameter("ansType");
				/*int question_id = uF.parseToInt(hidequeid[i]);*/
				int question_id = 0;
				
				String optiona = request.getParameter("optiona"+ goalId[i]);
				String optionb = request.getParameter("optionb"+ goalId[i]);
				String optionc = request.getParameter("optionc"+ goalId[i]);
				String optiond = request.getParameter("optiond"+ goalId[i]);
				String optione = request.getParameter("optione"+ goalId[i]);
				String rateoptiona = request.getParameter("rateoptiona"+ goalId[i]);
				String rateoptionb = request.getParameter("rateoptionb"+ goalId[i]);
				String rateoptionc = request.getParameter("rateoptionc"+ goalId[i]);
				String rateoptiond = request.getParameter("rateoptiond"+ goalId[i]);
				String rateoptione = request.getParameter("rateoptione"+ goalId[i]);
				String goalWeightage = request.getParameter("goalWeightage"+ goalId[i]);
//				System.out.println("GOal ID ===> "+goalId[i]+ "  optiona === "+optiona + "  optiona=b === "+optionb + "  optionc === "+optionc + "  optiond === "+optiond);
				
					String[] correct = request.getParameterValues("correct"+ goalId[i]);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans," +
						"is_add,question_type,goal_kra_target_id,app_system_type)values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst.setString(1, hmGoalName.get(goalId[i]));
					pst.setString(2, uF.showData(optiona, ""));
					pst.setString(3, uF.showData(optionb, ""));
					pst.setString(4, uF.showData(optionc, ""));
					pst.setString(5, uF.showData(optiond, ""));
					pst.setString(6, uF.showData(optione, ""));
					pst.setInt(7, uF.parseToInt(rateoptiona));
					pst.setInt(8, uF.parseToInt(rateoptionb));
					pst.setInt(9, uF.parseToInt(rateoptionc));
					pst.setInt(10, uF.parseToInt(rateoptiond));
					pst.setInt(11, uF.parseToInt(rateoptione));
					pst.setString(12, option.toString());
					pst.setBoolean(13, false);
					pst.setInt(14, uF.parseToInt(ansType));
					pst.setInt(15, uF.parseToInt(goalId[i]));
					pst.setInt(16, uF.parseToInt(sysType));
					pst.execute();
					pst.close();
					
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans," +
						"is_add,question_type,goal_kra_target_id,app_system_type)values(?,?,?,?,?,?,?,?,?,?)");
					pst.setString(1, hmGoalName.get(goalId[i]));
					pst.setString(2, uF.showData(optiona, ""));
					pst.setString(3, uF.showData(optionb, ""));
					pst.setString(4, uF.showData(optionc, ""));
					pst.setString(5, uF.showData(optiond, ""));
					pst.setString(6, option.toString());
					pst.setBoolean(7, false);
					pst.setInt(8, uF.parseToInt(ansType));
					pst.setInt(9, uF.parseToInt(goalId[i]));
					pst.setInt(10, uF.parseToInt(sysType));
					pst.execute();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
//				}

			//===start parvez date: 17-03-2022===	
					
//				pst = con.prepareStatement("insert into appraisal_question_details(question_id,attribute_id,appraisal_id,appraisal_level_id," +
//					"answer_type,goal_kra_target_id,app_system_type,weightage) values(?,?,?,?, ?,?,?,?)");
					
				pst = con.prepareStatement("insert into appraisal_question_details(question_id,attribute_id,appraisal_id,appraisal_level_id," +
					"answer_type,goal_kra_target_id,app_system_type,weightage,other_short_description) values(?,?,?,?, ?,?,?,?, ?)");
			//===end parvez date: 17-03-2022===	
				
				pst.setInt(1, question_id);
				pst.setInt(2, uF.parseToInt(attributeId));
				pst.setInt(3, uF.parseToInt(id));
				pst.setInt(4, uF.parseToInt(subSectionId));
				pst.setInt(5, uF.parseToInt(ansType));
				pst.setInt(6, uF.parseToInt(goalId[i]));
				pst.setInt(7, uF.parseToInt(sysType));
				pst.setDouble(8, uF.parseToDouble(goalWeightage));
		//===start parvez date: 17-03-2022===		
				pst.setString(9, hmGoalName.get(goalId[i]+"_DESCRIPTION"));
		//===end parvez date: 17-03-2022===	
				
				pst.execute();
				pst.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void getTargetDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, List<String>> hmTargetData = new HashMap<String, List<String>>();
				String goalType = INDIVIDUAL_GOAL+","+INDIVIDUAL_TARGET;
				pst = con.prepareStatement("select * from goal_details gd where gd.goal_id not in (select aqd.goal_kra_target_id from " +
					"appraisal_question_details aqd where aqd.goal_kra_target_id is not null and appraisal_id=?) and gd.goal_type in ("+goalType+") and " +
					"measure_type is not null and measure_type != '' and measure_kra is not null and measure_kra != '' and is_close = false");
				pst.setInt(1, uF.parseToInt(getId()));
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
			String goalType = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+EMPLOYEE_KRA;
				StringBuilder sb = new StringBuilder();
				sb.append("select k.*, g.weightage from goal_kras k, goal_details g where k.goal_id=g.goal_id and g.goal_type in ("+goalType+") " +
					"and k.is_close = false and (measure_type is null or measure_type='') ");
				sb.append(" and g.measure_kra is not null and g.measure_kra !='' and g.is_close = false order by k.goal_id");
				pst = con.prepareStatement(sb.toString());
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("goal_id"));
					innerList.add(rs.getString("kra_description"));
					innerList.add(rs.getString("goal_kra_id"));
					innerList.add(rs.getString("entry_date"));
					innerList.add(rs.getString("effective_date"));
					innerList.add(rs.getString("weightage"));
					innerList.add(uF.showData(getAppendData(rs.getString("emp_ids"), hmEmpName), ""));
					innerList.add(rs.getString("approved_by"));
					innerList.add(rs.getString("kra_order"));
					innerList.add(rs.getString("is_approved"));
					innerList.add(rs.getString("goal_type"));
					hmKRAData.put(rs.getString("goal_kra_id"), innerList);
			
			request.setAttribute("hmKRAData", hmKRAData);
			
			
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
//			Map<String, List<String>> hmKRAData = new HashMap<String, List<String>>();
//				pst = con.prepareStatement("select * from goal_details gd where gd.goal_id not in (select aqd.goal_kra_target_id from " +
//						"appraisal_question_details aqd where aqd.goal_kra_target_id is not null) and gd.goal_type = 4 and " +
//						"(measure_type is null or measure_type = '') and measure_kra is not null and measure_kra != ''");
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					List<String> innerList = new ArrayList<String>();
//					innerList.add(rs.getString("goal_id"));
//					innerList.add(rs.getString("goal_title"));
//					innerList.add(rs.getString("goal_objective"));
//					innerList.add(rs.getString("goal_description"));
//					innerList.add(rs.getString("goal_attribute"));
//					innerList.add(rs.getString("weightage"));
//					innerList.add(uF.showData(getAppendData(rs.getString("emp_ids"), hmEmpName), ""));
//					
//					hmKRAData.put(rs.getString("goal_id"), innerList);
//				}
//			request.setAttribute("hmKRAData", hmKRAData);
				}
				rs.close();
				pst.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void getKRAIDsGoalwise(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			Map<String, List<String>> hmKraIDs = new HashMap<String, List<String>>();
			String goalType = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+EMPLOYEE_KRA;
			pst = con.prepareStatement("select * from goal_kras where goal_type in ("+goalType+") and is_close = false and goal_kra_id not in (select aqd.kra_id from " +
				"appraisal_question_details aqd where aqd.kra_id is not null and appraisal_id=?)");
			pst.setInt(1, uF.parseToInt(getId()));
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
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			if(getAnsType() == null || getAnsType().equals("") || getAnsType().equals("0")){
				setAnsType("11");
			}
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
			String goalType = INDIVIDUAL_GOAL+","+PERSONAL_GOAL;
			pst = con.prepareStatement("select * from goal_details gd where gd.goal_id not in (select aqd.goal_kra_target_id from " +
				"appraisal_question_details aqd where aqd.goal_kra_target_id is not null and appraisal_id=?) and gd.goal_type in ("+goalType+") and " +
				"(measure_type is null or measure_type = '') and (measure_kra is null or measure_kra = '') and is_close = false");
			pst.setInt(1, uF.parseToInt(getId()));
//				pst = con.prepareStatement("select * from goal_details gd, appraisal_question_details aqd where gd.goal_id != aqd.goal_kra_target_id and gd.goal_type = 4 and (measure_type is null or measure_type = '') and (measure_kra is null or measure_kra = '')");
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
			while (rs.next()) {
				employeeIds = uF.showData(rs.getString("self_ids"), "");
			}
			rs.close();
			pst.close();
			
			List<String> empIdsList = Arrays.asList(employeeIds.split(","));
			List<String> goalIdsList = new ArrayList<String>();
			String goaltype = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+INDIVIDUAL_TARGET+","+PERSONAL_GOAL+","+EMPLOYEE_KRA;
			for (int i = 0; empIdsList != null && !empIdsList.isEmpty() && i < empIdsList.size(); i++) {
				StringBuilder sbquery = new StringBuilder();
				sbquery.append("select goal_id from goal_details where goal_type in ("+goaltype+") and is_close = false and emp_ids like '%,"+empIdsList.get(i)+",%' ");
				if(systemType != null && systemType.equals("goal")) {
					sbquery.append(" and (measure_type is null or measure_type = '') and (measure_kra is null or measure_kra = '')");
				} else if(systemType != null && systemType.equals("KRA")) {
					sbquery.append(" and (measure_type is null or measure_type = '') and measure_kra is not null and measure_kra != ''");
				} else if(systemType != null && systemType.equals("target")) {
					sbquery.append(" and measure_type is not null and measure_type != '' and measure_kra is not null and measure_kra != ''");
				}
//				pst = con.prepareStatement("select goal_id from goal_details where goal_type = 4 and emp_ids like '%,"+empIdsList.get(i)+",%' ");
				pst = con.prepareStatement(sbquery.toString());
				rs = pst.executeQuery();
				while (rs.next()) {
					if(!goalIdsList.contains(rs.getString("goal_id"))) {
						goalIdsList.add(rs.getString("goal_id"));
					}
				}
				rs.close();
				pst.close();
			}
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

	public String getSubSectionId() {
		return subSectionId;
	}

	public void setSubSectionId(String subSectionId) {
		this.subSectionId = subSectionId;
	}

	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public String getSysType() {
		return sysType;
	}

	public void setSysType(String sysType) {
		this.sysType = sysType;
	}

	public String getDivCount() {
		return divCount;
	}

	public void setDivCount(String divCount) {
		this.divCount = divCount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getQueID() {
		return queID;
	}

	public void setQueID(String queID) {
		this.queID = queID;
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


	public String getAppFreqId() {
		return appFreqId;
	}


	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	
}
