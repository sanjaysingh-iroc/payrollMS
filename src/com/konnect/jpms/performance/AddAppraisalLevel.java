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

public class AddAppraisalLevel implements ServletRequestAware, SessionAware,
		IStatements {
	Map session;
	CommonFunctions CF;

	String strUserType = null;
	String strSessionEmpId = null;

	private List<FillAttribute> attributeList;
	private List<FillAnswerType> ansTypeList;
	private List<FillFrequency> frequencyList;

	private String id;

	private String oreinted;

	private List<FillOrientation> orientationList;
	private Map<String, List<Map<String, List<List<String>>>>> levelMp = new HashMap<String, List<Map<String, List<List<String>>>>>();

	private String main_level_id;
	private String MLID;
	private String type;
	private String appFreqId;
	public String execute() {
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/performance/AddAppraisalLevel.jsp");
		request.setAttribute(TITLE, "Add New Appraisal Level");

		String levelID = getSelfIDs(getId());
		// System.out.println("levelID====>"+levelID);
		if (levelID != null && levelID.length() > 0) {
			attributeList = new FillAttribute(request).fillElementAttribute(levelID);
		} else {
			attributeList = new FillAttribute(request).fillElementAttribute(null);
		}
		ansTypeList = new FillAnswerType(request).fillAnswerType();

		getAppraisalDetail();
		getOrientationValue(uF.parseToInt(getOreinted()));
		getattribute();
		getOtherAnsType();
		getAppraisalQuestionList();
		getKRADetails();
		getKRATargetDetails();
		getMainLevelData1();
		getReport();
		// System.out.println("getType ===========> "+getType());
		String submit = request.getParameter("submit");

		if (getType() != null && getType().equals("system")) {
			getMainLevelData();
		} else if (submit != null && submit.equals("Save")) {

			String appraisalSystem = request.getParameter("appraisalSystem");

			// System.out.println("id=====> "+id);
			if (appraisalSystem != null) {
				insertInMainLevelDetails();
				if (appraisalSystem.equals("1")) {
					selectFunction();
				} else if (appraisalSystem.equals("2")) {
					addOtherQuestions();
				} else if (appraisalSystem.equals("3") || appraisalSystem.equals("4") || appraisalSystem.equals("5")) {
					insertGoalKRATarget();
				}
			}
			return "success";
		}
		return LOAD;
	}

	public void getReport() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);

			//
			// pst =
			// con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=?");
			// pst.setInt(1, uF.parseToInt(getId()));
			// rs = pst.executeQuery();
			// List<List<String>> mainLevelList = new ArrayList<List<String>>();
			// while (rs.next()) {
			//
			// List<String> innerList = new ArrayList<String>();
			// innerList.add(rs.getString("main_level_id"));
			// innerList.add(rs.getString("level_title"));
			// innerList.add(rs.getString("short_description"));
			// innerList.add(rs.getString("long_description"));
			// innerList.add(rs.getString("appraisal_id"));
			//
			// mainLevelList.add(innerList);
			//
			// }
			// request.setAttribute("mainLevelList", mainLevelList);

			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			// List<List<String>> outerList1 = new ArrayList<List<String>>();
			Map<String, List<List<String>>> hmSystemLevelMp = new HashMap<String, List<List<String>>>();
			Map<String, String> attributeMp = getAttributeMap(con);
			while (rs.next()) {

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("level_title"));
				innerList.add(rs.getString("scorecard_type"));
				innerList.add(rs.getString("appraisal_system"));
				innerList.add(rs.getString("short_description"));
				innerList.add(rs.getString("long_description"));
				innerList.add(attributeMp.get(rs.getString("attribute_id")));

				if (uF.parseToInt(rs.getString("appraisal_system")) == 1) {

					if (uF.parseToInt(rs.getString("scorecard_type")) == 1)
						getDataObjectiveMeasureGoal(uF.parseToInt(rs
								.getString("appraisal_level_id")));
					if (uF.parseToInt(rs.getString("scorecard_type")) == 2)
						getDataMeasure(uF.parseToInt(rs
								.getString("appraisal_level_id")));
					else if (uF.parseToInt(rs.getString("scorecard_type")) == 3) {
						getDataMeasureGoal(uF.parseToInt(rs
								.getString("appraisal_level_id")));
					}
				} else if (uF.parseToInt(rs.getString("appraisal_system")) == 2) {
					// code for other option
					getOtherData(uF.parseToInt(rs
							.getString("appraisal_level_id")));
				}
				innerList.add(rs.getString("main_level_id"));

				List<List<String>> outerList1 = hmSystemLevelMp.get(rs
						.getString("main_level_id"));
				if (outerList1 == null)
					outerList1 = new ArrayList<List<String>>();
				outerList1.add(innerList);
				hmSystemLevelMp.put(rs.getString("main_level_id"), outerList1);

			}
			rs.close();
			pst.close();
			request.setAttribute("hmSystemLevelMp", hmSystemLevelMp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getOtherData(int id) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);
			pst = con.prepareStatement("select * from appraisal_other_question_type_details where level_id =?");
			pst.setInt(1, id);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String othe_question_type_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					othe_question_type_id = rs
							.getString("othe_question_type_id");
				} else {
					othe_question_type_id += ","
							+ rs.getString("othe_question_type_id");
				}
				i++;
			}
			rs.close();
			pst.close();

			pst = con
					.prepareStatement("select * from appraisal_question_details where other_id in("
							+ othe_question_type_id + ")");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList
						.add(uF.showData(AppraisalQuestion.get(rs
								.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("other_short_description"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));

				List<List<String>> outerList = questionMp.get(id + "");
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(id + "", outerList);

			}
			rs.close();
			pst.close();
			
			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			// list.add(otherMp);
			list.add(questionMp);
			levelMp.put(id + "", list);
			request.setAttribute("levelMp", levelMp);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getDataMeasureGoal(int id) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);
			Map<String, String> attributeMp = getAttributeMap(con);

			pst = con
					.prepareStatement("select * from appraisal_scorecard_details where level_id =? and appraisal_id=?");
			pst.setInt(1, id);
			pst.setInt(2, uF.parseToInt(getId()));

			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, List<List<String>>> scoreMp = new HashMap<String, List<List<String>>>();
			String scorecard_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					scorecard_id = rs.getString("scorecard_id");
				} else {
					scorecard_id += "," + rs.getString("scorecard_id");
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("scorecard_section_name"));
				innerList.add(rs.getString("scorecard_weightage"));
				innerList.add(uF.showData(
						attributeMp.get(rs.getString("appraisal_attribute")),
						""));
				List<List<String>> outerList = scoreMp.get(rs
						.getString("level_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select * from appraisal_goal_details where scorecard_id in("
							+ scorecard_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));

			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, List<List<String>>> GoalMp = new HashMap<String, List<List<String>>>();
			String goal_id = null;
			i = 0;
			while (rs.next()) {
				if (i == 0) {
					goal_id = rs.getString("goal_id");
				} else {
					goal_id += "," + rs.getString("goal_id");
				}
				i++;
				List<String> innerGoalList = new ArrayList<String>();
				innerGoalList.add(rs.getString("goal_id"));
				// innerGoalList.add(rs.getString("scorecard_id"));
				innerGoalList.add(rs.getString("goal_section_name"));
				// innerGoalList.add(rs.getString("goal_description"));
				innerGoalList.add(rs.getString("goal_weightage"));

				List<List<String>> outerGoalList = GoalMp.get(rs
						.getString("scorecard_id"));
				if (outerGoalList == null)
					outerGoalList = new ArrayList<List<String>>();
				outerGoalList.add(innerGoalList);
				// outerGoalList.add(innerGoalList);

				GoalMp.put(rs.getString("scorecard_id"), outerGoalList);

			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select * from appraisal_measure_details where goal_id in("
							+ goal_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));

			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, List<List<String>>> measureMp = new HashMap<String, List<List<String>>>();
			String measure_id = null;
			i = 0;
			while (rs.next()) {
				if (i == 0) {
					measure_id = rs.getString("measure_id");
				} else {
					measure_id += "," + rs.getString("measure_id");
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("weightage"));
				List<List<String>> outerList = measureMp.get(rs
						.getString("goal_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				measureMp.put(rs.getString("goal_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select * from appraisal_question_details where measure_id in ("
							+ measure_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));

			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList
						.add(uF.showData(AppraisalQuestion.get(rs
								.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("appraisal_question_details_id"));

				List<List<String>> outerList = questionMp.get(rs
						.getString("measure_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(rs.getString("measure_id"), outerList);

			}
			rs.close();
			pst.close();
			
			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(scoreMp);
			list.add(measureMp);
			list.add(questionMp);
			list.add(GoalMp);
			levelMp.put(id + "", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getDataObjectiveMeasureGoal(int id) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con,uF);
			Map<String, String> attributeMp = getAttributeMap(con);
			pst = con
					.prepareStatement("select * from appraisal_scorecard_details where level_id =? and appraisal_id=?");
			pst.setInt(1, id);
			pst.setInt(2, uF.parseToInt(getId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, List<List<String>>> scoreMp = new HashMap<String, List<List<String>>>();
			String scorecard_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					scorecard_id = rs.getString("scorecard_id");
				} else {
					scorecard_id += "," + rs.getString("scorecard_id");
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("scorecard_section_name"));
				innerList.add(rs.getString("scorecard_weightage"));
				innerList.add(uF.showData(
						attributeMp.get(rs.getString("appraisal_attribute")),
						""));

				List<List<String>> outerList = scoreMp.get(rs
						.getString("level_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select * from appraisal_goal_details where scorecard_id in("
							+ scorecard_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, List<List<String>>> GoalMp = new HashMap<String, List<List<String>>>();
			String goal_id = null;
			i = 0;
			while (rs.next()) {
				if (i == 0) {
					goal_id = rs.getString("goal_id");
				} else {
					goal_id += "," + rs.getString("goal_id");
				}
				i++;
				List<String> innerGoalList = new ArrayList<String>();
				innerGoalList.add(rs.getString("goal_id"));
				// innerGoalList.add(rs.getString("scorecard_id"));
				innerGoalList.add(rs.getString("goal_section_name"));
				// innerGoalList.add(rs.getString("goal_description"));
				innerGoalList.add(rs.getString("goal_weightage"));

				List<List<String>> outerGoalList = GoalMp.get(rs
						.getString("scorecard_id"));
				if (outerGoalList == null)
					outerGoalList = new ArrayList<List<String>>();
				outerGoalList.add(innerGoalList);
				// outerGoalList.add(innerGoalList);

				GoalMp.put(rs.getString("scorecard_id"), outerGoalList);

			}
			rs.close();
			pst.close();

			pst = con
					.prepareStatement("select * from appraisal_objective_details where goal_id in("
							+ goal_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, List<List<String>>> objectiveMp = new HashMap<String, List<List<String>>>();
			String objective_id = null;
			i = 0;
			while (rs.next()) {
				if (i == 0) {
					objective_id = rs.getString("objective_id");
				} else {
					objective_id += "," + rs.getString("objective_id");
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("objective_id"));
				innerList.add(rs.getString("objective_section_name"));
				innerList.add(rs.getString("objective_weightage"));

				List<List<String>> outerList = objectiveMp.get(rs
						.getString("goal_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				objectiveMp.put(rs.getString("goal_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select * from appraisal_measure_details where objective_id in("
							+ objective_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, List<List<String>>> measureMp = new HashMap<String, List<List<String>>>();
			String measure_id = null;
			i = 0;
			while (rs.next()) {
				if (i == 0) {
					measure_id = rs.getString("measure_id");
				} else {
					measure_id += "," + rs.getString("measure_id");
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("weightage"));
				List<List<String>> outerList = measureMp.get(rs
						.getString("objective_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				measureMp.put(rs.getString("objective_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select * from appraisal_question_details where measure_id in("
							+ measure_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));

			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList
						.add(uF.showData(AppraisalQuestion.get(rs
								.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("appraisal_question_details_id"));

				List<List<String>> outerList = questionMp.get(rs
						.getString("measure_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(rs.getString("measure_id"), outerList);

			}
			rs.close();
			pst.close();
			
			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(scoreMp);
			list.add(measureMp);
			list.add(questionMp);
			list.add(GoalMp);
			list.add(objectiveMp);

			levelMp.put(id + "", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getDataMeasure(int id) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);
			Map<String, String> attributeMp = getAttributeMap(con);

			pst = con
					.prepareStatement("select * from appraisal_scorecard_details where level_id =? and appraisal_id=?");
			pst.setInt(1, id);
			pst.setInt(2, uF.parseToInt(getId()));

			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, List<List<String>>> scoreMp = new HashMap<String, List<List<String>>>();
			String scorecard_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					scorecard_id = rs.getString("scorecard_id");
				} else {
					scorecard_id += "," + rs.getString("scorecard_id");
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("scorecard_section_name"));
				innerList.add(rs.getString("scorecard_weightage"));
				innerList.add(uF.showData(
						attributeMp.get(rs.getString("appraisal_attribute")),
						""));

				List<List<String>> outerList = scoreMp.get(rs
						.getString("level_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select * from appraisal_measure_details where scorecard_id in("
							+ scorecard_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));

			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, List<List<String>>> measureMp = new HashMap<String, List<List<String>>>();
			String measure_id = null;
			i = 0;
			while (rs.next()) {
				if (i == 0) {
					measure_id = rs.getString("measure_id");
				} else {
					measure_id += "," + rs.getString("measure_id");
				}
				i++;
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("measure_section_name"));
				innerList.add(rs.getString("weightage"));
				List<List<String>> outerList = measureMp.get(rs
						.getString("scorecard_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				measureMp.put(rs.getString("scorecard_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select * from appraisal_question_details where measure_id in("
							+ measure_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));

			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList
						.add(uF.showData(AppraisalQuestion.get(rs
								.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("appraisal_question_details_id"));

				List<List<String>> outerList = questionMp.get(rs
						.getString("measure_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(rs.getString("measure_id"), outerList);

			}
			rs.close();
			pst.close();
			
			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(scoreMp);
			list.add(measureMp);
			list.add(questionMp);
			list.add(questionMp);
			levelMp.put(id + "", list);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getKRATargetDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		UtilityFunctions uf = new UtilityFunctions();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmGoalOrientation = new HashMap<String, String>();
			Map<String, String> hmMesures = new HashMap<String, String>();
			Map<String, String> hmMesuresType = new HashMap<String, String>();
			Map<String, String> hmGoalTitle = new HashMap<String, String>();
			Map<String, String> orientationMemberMp = getOrientationMember(con);

			Map<String, List<List<String>>> hmKRA = new LinkedHashMap<String, List<List<String>>>();
			
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uf.parseToInt(getId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			List<String> memberList = new ArrayList<String>();
			while (rs.next()) {
				memberList = Arrays.asList(rs.getString("usertype_member")
						.split(","));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("memberList", memberList);

			StringBuilder sb = new StringBuilder();

			sb.append("select * from goal_kras k join goal_details g on k.goal_id=g.goal_id ");
			sb.append(" and g.goal_type=4 and measure_type !=''  order by k.goal_id");

			pst = con.prepareStatement(sb.toString());
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				List<List<String>> outerList = hmKRA.get(rs
						.getString("goal_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("goal_kra_id"));
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("entry_date"));
				innerList.add(rs.getString("effective_date"));
				innerList.add(rs.getString("is_approved"));
				innerList.add(rs.getString("approved_by"));
				innerList.add(rs.getString("kra_order"));
				innerList.add(rs.getString("kra_description"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("weightage"));

				hmGoalOrientation.put(rs.getString("goal_id"),
						rs.getString("orientation_id"));

				hmGoalTitle.put(rs.getString("goal_id"),
						rs.getString("goal_title"));

				String measures = "";
				if (rs.getString("measure_type").equals("$")) {
					measures = rs.getString("measure_currency_value");
				} else if (rs.getString("measure_type").equals("Effort")) {
					measures = rs.getString("measure_effort_days")
							+ " Days and " + rs.getString("measure_effort_hrs")
							+ " Hrs.";
				}
				hmMesures.put(rs.getString("goal_id"), measures);
				hmMesuresType.put(rs.getString("goal_id"),
						rs.getString("measure_type"));

				outerList.add(innerList);
				hmKRA.put(rs.getString("goal_id"), outerList);
			}
			rs.close();
			pst.close();
			// }
			request.setAttribute("hmKRA", hmKRA);
			// System.out.println("hmKRA"+hmKRA);
			// System.out.println("hmMesuresType"+hmMesuresType);

			request.setAttribute("hmMesures", hmMesures);
			request.setAttribute("hmMesuresType", hmMesuresType);
			request.setAttribute("hmGoalOrientation", hmGoalOrientation);
			request.setAttribute("hmGoalTitle", hmGoalTitle);

			request.setAttribute("orientationMemberMp", orientationMemberMp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getKRADetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		UtilityFunctions uf = new UtilityFunctions();
		db.setRequest(request);
		
		Map<String, List<List<String>>> hmKRA = new LinkedHashMap<String, List<List<String>>>();
		boolean levelFlag = false;

		try {
			con = db.makeConnection(con);

			Map<String, String> hmGoalOrientation = new HashMap<String, String>();
			Map<String, String> hmMesures = new HashMap<String, String>();
			Map<String, String> hmMesuresType = new HashMap<String, String>();
			Map<String, String> hmGoalTitle = new HashMap<String, String>();
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id =?");
			pst.setInt(1, uf.parseToInt(getId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			List<String> memberList = new ArrayList<String>();
			while (rs.next()) {
				memberList = Arrays.asList(rs.getString("usertype_member")
						.split(","));
			}
			rs.close();
			pst.close();
			request.setAttribute("memberList1", memberList);

			StringBuilder sb = new StringBuilder();
			sb.append("select * from goal_kras k join goal_details g on k.goal_id=g.goal_id ");
			sb.append(" and g.goal_type=4 and (measure_type='' or measure_type is null) order by k.goal_id");
			pst = con.prepareStatement(sb.toString());
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				List<List<String>> outerList = hmKRA.get(rs
						.getString("goal_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("goal_kra_id"));
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("entry_date"));
				innerList.add(rs.getString("effective_date"));
				innerList.add(rs.getString("is_approved"));
				innerList.add(rs.getString("approved_by"));
				innerList.add(rs.getString("kra_order"));
				innerList.add(rs.getString("kra_description"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("weightage"));

				hmGoalOrientation.put(rs.getString("goal_id"),
						rs.getString("orientation_id"));

				hmGoalTitle.put(rs.getString("goal_id"),
						rs.getString("goal_title"));

				String measures = "";
				// if (rs.getString("measure_type").equals("$")) {
				// measures = rs.getString("measure_currency_value");
				// } else if (rs.getString("measure_type").equals("Effort")) {
				// measures = rs.getString("measure_effort_days")
				// + " Days and "
				// + rs.getString("measure_effort_hrs") + " Hrs.";
				// }
				hmMesures.put(rs.getString("goal_id"), measures);
				hmMesuresType.put(rs.getString("goal_id"),
						rs.getString("measure_type"));

				outerList.add(innerList);
				hmKRA.put(rs.getString("goal_id"), outerList);

				levelFlag = true;
			}
			rs.close();
			pst.close();
			// }
			// System.out.println("hmKRA"+hmKRA);
			request.setAttribute("hmKRA1", hmKRA);
			request.setAttribute("hmMesures1", hmMesures);
			request.setAttribute("hmMesuresType1", hmMesuresType);
			request.setAttribute("hmGoalOrientation1", hmGoalOrientation);
			request.setAttribute("hmGoalTitle1", hmGoalTitle);

			request.setAttribute("orientationMemberMp1", orientationMemberMp);
			request.setAttribute("levelFlag", levelFlag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getMainLevelData1() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);

			pst = con
					.prepareStatement("select * from appraisal_main_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			// System.out.println("pst ============ > "+pst);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			List<List<String>> mainLevelList1 = new ArrayList<List<String>>();
			while (rs.next()) {

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("main_level_id"));
				innerList.add(rs.getString("level_title"));
				innerList.add(rs.getString("short_description"));
				innerList.add(rs.getString("long_description"));
				innerList.add(rs.getString("appraisal_id"));

				mainLevelList1.add(innerList);
//				 System.out.println("mainLevelList1 in java ============ > "+mainLevelList1);
			}
			rs.close();
			pst.close();
			request.setAttribute("mainLevelList1", mainLevelList1);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getMainLevelData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);

			pst = con
					.prepareStatement("select * from appraisal_main_level_details where appraisal_id=? and main_level_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getMLID()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			List<String> mainLevelList = new ArrayList<String>();
			while (rs.next()) {
				mainLevelList.add(rs.getString("main_level_id"));
				mainLevelList.add(rs.getString("level_title"));
				mainLevelList.add(rs.getString("short_description"));
				mainLevelList.add(rs.getString("long_description"));
				mainLevelList.add(rs.getString("appraisal_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("mainLevelList", mainLevelList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void insertInMainLevelDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		String levelTitle = request.getParameter("levelTitle");
		String shortDesrciption = request.getParameter("shortDesrciption");
		String longDesrciption = request.getParameter("longDesrciption");

		try {
			con = db.makeConnection(con);
			// System.out.println("getMain_level_id()======>"+getMain_level_id());
			if (getMain_level_id() == null || getMain_level_id().equals("")
					|| getMain_level_id().equals("null")) {
				pst = con
						.prepareStatement("insert into appraisal_main_level_details(level_title,short_description,long_description,"
								+ "appraisal_id)values(?,?,?,?)");

				pst.setString(1, levelTitle);
				pst.setString(2, shortDesrciption);
				pst.setString(3, longDesrciption);
				pst.setInt(4, uF.parseToInt(id));
				pst.execute();
				pst.close();
				
				int main_level_id = 0;
				pst = con
						.prepareStatement("select max(main_level_id) from appraisal_main_level_details");
				rs = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				while (rs.next()) {
					main_level_id = rs.getInt(1);
				}
				rs.close();
				pst.close();
				
				setMain_level_id("" + main_level_id);
				request.setAttribute("mainlevelTitle", levelTitle);
				request.setAttribute("mainshortDesrciption", shortDesrciption);
				request.setAttribute("mainlongDesrciption", longDesrciption);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String getSelfIDs(String id2) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		String levelID = null;

		try {
			con = db.makeConnection(con);
			String empID = null;
			pst = con
					.prepareStatement("select self_ids from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				empID = rs.getString("self_ids");
			}
			rs.close();
			pst.close();

			if (empID != null && !empID.equals("")) {
				empID = empID != null && !empID.equals("") ? empID.substring(1,
						empID.length() - 1) : "";

				List<String> levellistID = new ArrayList<String>();
				pst = con
						.prepareStatement("select ld.level_id from level_details ld right join (select * from designation_details dd "
								+ "right join (select *, gd.designation_id as designationid from employee_official_details eod, grades_details gd "
								+ "where gd.grade_id=eod.grade_id and eod.emp_id in ("
								+ empID
								+ ")) a on a.designationid=dd.designation_id)"
								+ " a on a.level_id=ld.level_id");
				rs = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				while (rs.next()) {
					levellistID.add(rs.getString(1));
				}
				rs.close();
				pst.close();

				Set<String> levelIdSet = new HashSet<String>(levellistID);
				Iterator<String> itr = levelIdSet.iterator();
				int i = 0;
				while (itr.hasNext()) {
					String levelid = (String) itr.next();
					if (i == 0) {
						levelID = levelid;
					} else {
						levelID += "," + levelid;
					}
					i++;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return levelID;
	}

	private void insertGoalKRATarget() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String levelTitle = request.getParameter("levelTitle");
		String shortDesrciption = request.getParameter("shortDesrciption");
		String longDesrciption = request.getParameter("longDesrciption");
		String appraisalSystem = request.getParameter("appraisalSystem");
		String scoreCard = request.getParameter("scoreCard");

		try {
			con = db.makeConnection(con);

			pst = con
					.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,"
							+ "appraisal_system,scorecard_type,appraisal_id,main_level_id)values(?,?,?,?,?,?,?)");

			pst.setString(1, levelTitle);
			pst.setString(2, shortDesrciption);
			pst.setString(3, longDesrciption);
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getAppraisalDetail() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		con = db.makeConnection(con);
		Map<String, String> hmDesignation = CF.getDesigMap(con);
		Map<String, String> hmGradeMap = CF.getGradeMap(con);
		Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
		Map<String, String> hmLevelMap = getLevelMap(con);
		Map<String, String> hmLocation = getLocationMap(con);
		Map<String, String> mpdepart = CF.getDeptMap(con);
		Map<String, String> orientationMp = getOrientationValue(con);
		Map<String, String> orientationMemberMp = getOrientationMember(con);
		try {
			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"),
						rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
			List<String> appraisalList = new ArrayList<String>();
			pst = con
					.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				List<String> memberList = Arrays.asList(rs.getString("usertype_member").split(","));
				String memberName = "";

				for (int i = 0; i < memberList.size(); i++) {
					if (i == 0)
						memberName += orientationMemberMp.get(memberList.get(i));
					else
						memberName += "," + orientationMemberMp.get(memberList.get(i));
				}

				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_name"), ""));
				oreinted = rs.getString("oriented_type");
				appraisalList.add(uF.showData(orientationMp.get(rs.getString("oriented_type")) + "&deg( " + memberName + " )", ""));
				appraisalList.add(uF.showData(rs.getString("self_ids"), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("level_id"), hmLevelMap), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("desig_id"), hmDesignation), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("grade_id"), hmGradeMap), ""));
				appraisalList.add(uF.showData(hmFrequency.get(rs.getString("frequency")), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("wlocation_id"), hmLocation), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("department_id"), mpdepart), ""));
				appraisalList.add(uF.showData(rs.getString("supervisor_id"), ""));
				appraisalList.add(uF.showData(rs.getString("peer_ids"), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));
				appraisalList.add(uF.showData(rs.getString("emp_status"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_type"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_description"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_instruction"), ""));
				appraisalList.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalList.add(uF.showData(rs.getString("ceo_ids"), ""));
				appraisalList.add(uF.showData(rs.getString("hod_ids"), ""));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("appraisalList", appraisalList);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private Map<String, String> getOrientMemberID(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();

		try {

			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_name"),
						rs.getString("member_id"));
			}
			rs.close();
			pst.close();

			// System.out.println("memberid=====>"+orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}

	
	public void initialize(UtilityFunctions uF) {

		orientationList = new FillOrientation(request).fillOrientation();
		frequencyList = new FillFrequency(request).fillFrequency();

		String levelID = getSelfIDs(getId());
		if (levelID != null && levelID.length() > 0) {
			attributeList = new FillAttribute(request).fillElementAttribute(levelID);
		} else {
			attributeList = new FillAttribute(request).fillElementAttribute(null);
		}

		getattribute();
		ansTypeList = new FillAnswerType(request).fillAnswerType();

	}

	
	private void getOrientationValue(int id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rs = null;
		db.setRequest(request);
		try {
			StringBuilder sb = new StringBuilder();
			con = db.makeConnection(con);

			pst = con
					.prepareStatement("select member_name from orientation_details od,orientation_member om  where orientation_id=? and od.member_id=orientation_member_id");
			pst.setInt(1, id);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			int i = 0;
			while (rs.next()) {
				if (i == 0)
					sb.append(rs.getString("member_name"));
				else
					sb.append("," + rs.getString("member_name"));
				i++;
			}
			rs.close();
			pst.close();
			request.setAttribute("member", sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public List<FillFrequency> getFrequencyList() {
		return frequencyList;
	}

	public void setFrequencyList(List<FillFrequency> frequencyList) {
		this.frequencyList = frequencyList;
	}

	public List<FillOrientation> getOrientationList() {
		return orientationList;
	}

	public void setOrientationList(List<FillOrientation> orientationList) {
		this.orientationList = orientationList;
	}

	private void getOtherAnsType() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		try {
			StringBuilder sb = new StringBuilder("");

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("appraisal_answer_type_id")) == 9) {
					sb.append("<option value=\""
							+ rs.getString("appraisal_answer_type_id")
							+ "\" selected>"
							+ rs.getString("appraisal_answer_type_name")
							+ "</option>");
				} else {
					sb.append("<option value=\""
							+ rs.getString("appraisal_answer_type_id") + "\">"
							+ rs.getString("appraisal_answer_type_name")
							+ "</option>");
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("anstype", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void addOtherQuestions() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
		
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
	
			String levelTitle = request.getParameter("levelTitle");
			String shortDesrciption = request.getParameter("shortDesrciption");
			String longDesrciption = request.getParameter("longDesrciption");
			String attribute = request.getParameter("attribute");
			String appraisalSystem = request.getParameter("appraisalSystem");
			String scoreCard = request.getParameter("scoreCard");
	
			String otherQuestionType = request.getParameter("otherQuestionType");
			String checkWeightage = request.getParameter("checkWeightage");
	
			String[] otherSDescription = request.getParameterValues("otherSDescription");
			String[] orientt = request.getParameterValues("orientt");
	
			String[] questionSelect = request.getParameterValues("questionSelect");
			String[] weightage = request.getParameterValues("weightage");
			String[] question = request.getParameterValues("question");
			String[] addFlag = request.getParameterValues("status");
			String[] optiona = request.getParameterValues("optiona");
			String[] optionb = request.getParameterValues("optionb");
			String[] optionc = request.getParameterValues("optionc");
			String[] optiond = request.getParameterValues("optiond");
			String[] optione = request.getParameterValues("optione");
			String[] rateoptiona = request.getParameterValues("rateoptiona");
			String[] rateoptionb = request.getParameterValues("rateoptionb");
			String[] rateoptionc = request.getParameterValues("rateoptionc");
			String[] rateoptiond = request.getParameterValues("rateoptiond");
			String[] rateoptione = request.getParameterValues("rateoptione");
	
			Map<String, String> orientationMemberMp = getOrientationMember(con);

			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,"
							+ "appraisal_system,scorecard_type,appraisal_id,main_level_id,attribute_id)values(?,?,?,?,?,?,?,?)");
			pst.setString(1, levelTitle);
			pst.setString(2, shortDesrciption);
			pst.setString(3, longDesrciption);
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.setInt(8, uF.parseToInt(attribute));
			pst.execute();
			pst.close();

			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			pst = con.prepareStatement("insert into appraisal_other_question_type_details(other_question_type,is_weightage,"
				+ "appraisal_id,level_id)values(?,?,?,?)");
			pst.setString(1, otherQuestionType);
			pst.setBoolean(2, uF.parseToBoolean(checkWeightage));
			pst.setInt(3, uF.parseToInt(id));
			pst.setInt(4, appraisal_level_id);
			pst.execute();
			pst.close();
			
			int other_question_type_id = 0;
			pst = con
					.prepareStatement("select max(othe_question_type_id) from appraisal_other_question_type_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				other_question_type_id = rst.getInt(1);
			}
			rst.close();
			pst.close();

			for (int i = 0; i < questionSelect.length; i++) {

				int question_id = uF.parseToInt(questionSelect[i]);
				if (uF.parseToInt(questionSelect[i]) == 0) {

					String[] correct = request.getParameterValues("correct"+ orientt[i]);
					String ansType = request.getParameter("ansType"+ orientt[i]);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}

					pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
						"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setString(1, question[i]);
					pst.setString(2, (optiona != null && optiona.length > i ? optiona[i]: ""));
					pst.setString(3, (optionb != null && optionb.length > i ? optionb[i]: ""));
					pst.setString(4, (optionc != null && optionc.length > i ? optionc[i]: ""));
					pst.setString(5, (optiond != null && optiond.length > i ? optiond[i]: ""));
					pst.setString(6, (optione != null && optione.length > i ? optione[i]: ""));
					pst.setInt(7, (rateoptiona != null && rateoptiona.length > i ? uF.parseToInt(rateoptiona[i]): 0));
					pst.setInt(8, (rateoptionb != null && rateoptionb.length > i ? uF.parseToInt(rateoptionb[i]): 0));
					pst.setInt(9, (rateoptionc != null && rateoptionc.length > i ? uF.parseToInt(rateoptionc[i]): 0));
					pst.setInt(10, (rateoptiond != null && rateoptiond.length > i ? uF.parseToInt(rateoptiond[i]): 0));
					pst.setInt(11, (rateoptione != null && rateoptione.length > i ? uF.parseToInt(rateoptione[i]): 0));
					pst.setString(12, option.toString());
					pst.setBoolean(13, uF.parseToBoolean(addFlag[i]));
					pst.setInt(14, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();
					
					/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
					pst.setString(1, question[i]);
					pst.setString(2, optiona[i]);
					pst.setString(3, optionb[i]);
					pst.setString(4, optionc[i]);
					pst.setString(5, optiond[i]);
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag[i]));
					pst.setInt(8, uF.parseToInt(ansType));
					pst.executeUpdate();
					pst.close();*/
					
					pst = con.prepareStatement("select max(question_bank_id) from question_bank");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();

				}

				pst = con.prepareStatement("insert into appraisal_question_details(question_id,other_id,attribute_id,"
					+ "weightage,appraisal_id,other_short_description,appraisal_level_id)values(?,?,?,?, ?,?,?)");
				pst.setInt(1, question_id);
				pst.setInt(2, other_question_type_id);
				pst.setInt(3, uF.parseToInt(attribute));
				pst.setDouble(4, uF.parseToDouble(weightage[i]));
				pst.setInt(5, uF.parseToInt(id));
				pst.setString(6, otherSDescription[i]);
				pst.setInt(7, appraisal_level_id);
				pst.executeUpdate();
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

	public void selectFunction() {
		UtilityFunctions uF = new UtilityFunctions();

		int scoreCard = uF.parseToInt((String) request
				.getParameter("scoreCard"));
		if (scoreCard == 1) {
			insertDatawithGoalObjective();
		} else if (scoreCard == 2) {
			insertData();
		} else if (scoreCard == 3) {
			insertDatawithGoal();
		}

	}

	public void insertDatawithGoal() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		
		try {
				String[] scoreSectionName = request
					.getParameterValues("scoreSectionName");
			String[] scoreCardDescription = request
					.getParameterValues("scoreCardDescription");
			String[] scoreCardWeightage = request
					.getParameterValues("scoreCardWeightage");
	
			String[] goalSectionName = request
					.getParameterValues("goalSectionName");
			String[] goalDescription = request
					.getParameterValues("goalDescription");
			String[] goalWeightage = request.getParameterValues("goalWeightage");
	
			String[] measuresSectionName = request
					.getParameterValues("measuresSectionName");
			String[] measuresDescription = request
					.getParameterValues("measuresDescription");
			String[] measureWeightage = request
					.getParameterValues("measureWeightage");
	
			String[] questionSelect = request.getParameterValues("questionSelect");
			String[] weightage = request.getParameterValues("weightage");
			String[] measurecount = request.getParameterValues("measurecount");
			String[] questioncount = request.getParameterValues("questioncount");
			String[] goalcount = request.getParameterValues("goalcount");
			String[] question = request.getParameterValues("question");
	
			String levelTitle = request.getParameter("levelTitle");
			String shortDesrciption = request.getParameter("shortDesrciption");
			String longDesrciption = request.getParameter("longDesrciption");
			String[] attribute = request.getParameterValues("attribute");
			String appraisalSystem = request.getParameter("appraisalSystem");
			String scoreCard = request.getParameter("scoreCard");
	
			String[] addFlag = request.getParameterValues("status");
			String[] optiona = request.getParameterValues("optiona");
			String[] optionb = request.getParameterValues("optionb");
			String[] optionc = request.getParameterValues("optionc");
			String[] optiond = request.getParameterValues("optiond");
			String[] optione = request.getParameterValues("optione");
			String[] rateoptiona = request.getParameterValues("rateoptiona");
			String[] rateoptionb = request.getParameterValues("rateoptionb");
			String[] rateoptionc = request.getParameterValues("rateoptionc");
			String[] rateoptiond = request.getParameterValues("rateoptiond");
			String[] rateoptione = request.getParameterValues("rateoptione");
			String[] orientt = request.getParameterValues("orientt");
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
		
	

			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,"
				+ "appraisal_system,scorecard_type,appraisal_id,main_level_id)values(?,?,?,?,?,?,?)");
			pst.setString(1, levelTitle);
			pst.setString(2, shortDesrciption);
			pst.setString(3, longDesrciption);
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.execute();
			pst.close();
			
			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int questionserial = 0;
			int measureserial = 0;
			int goalserial = 0;
			for (int i = 0; i < scoreSectionName.length; i++) {

				pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
					+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
				pst.setString(1, scoreSectionName[i]);
				pst.setString(2, scoreCardDescription[i]);
				pst.setString(3, scoreCardWeightage[i]);
				pst.setInt(4, appraisal_level_id);
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(attribute[i]));
				pst.execute();
				pst.close();
				
				int scorecard_id = 0;
				pst = con.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
				rst = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				while (rst.next()) {
					scorecard_id = rst.getInt(1);
				}
				rst.close();
				pst.close();

				int goal = uF.parseToInt(goalcount[i]);
				for (int j = 0; j < goal; j++) {
					pst = con.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
						+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
					pst.setString(1, goalSectionName[goalserial]);
					pst.setString(2, goalDescription[goalserial]);
					pst.setString(3, goalWeightage[goalserial]);
					pst.setInt(4, scorecard_id);
					pst.setInt(5, uF.parseToInt(id));
					pst.execute();
					pst.close();
					
					int goal_id = 0;
					pst = con.prepareStatement("select max(goal_id) from appraisal_goal_details");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						goal_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
					
					int measure = uF.parseToInt(measurecount[goalserial]);
					goalserial++;
					for (int k = 0; k < measure; k++) {
						pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,goal_id,appraisal_id,weightage)"
										+ "values(?,?,?,?,?)");
						pst.setString(1, measuresSectionName[measureserial]);
						pst.setString(2, measuresDescription[measureserial]);
						pst.setInt(3, goal_id);
						pst.setInt(4, uF.parseToInt(id));
						pst.setString(5, measureWeightage[measureserial]);
						pst.execute();
						pst.close();
						
						int measure_id = 0;
						pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
						rst = pst.executeQuery();
//						System.out.println("new Date ===> " + new Date());
						while (rst.next()) {
							measure_id = rst.getInt(1);
						}
						rst.close();
						pst.close();

						int questioncnt = uF.parseToInt(questioncount[measureserial]);
						measureserial++;
						for (int l = 0; l < questioncnt; l++) {

							int question_id = uF.parseToInt(questionSelect[questionserial]);
							if (questionSelect[questionserial].length() > 0) {
								if (uF.parseToInt(questionSelect[questionserial]) == 0) {

									String[] correct = request.getParameterValues("correct"+ orientt[questionserial]);
									String ansType = request.getParameter("ansType"+ orientt[questionserial]);
									StringBuilder option = new StringBuilder();

									for (int ab = 0; correct != null && ab < correct.length; ab++) {
										option.append(correct[ab] + ",");
									}

									pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
										"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
										"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
									pst.setString(1, question[questionserial]);
									pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
									pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
									pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
									pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
									pst.setString(6, (optione != null && optione.length > questionserial ? optione[questionserial]: ""));
									pst.setInt(7, (rateoptiona != null && rateoptiona.length > questionserial ? uF.parseToInt(rateoptiona[questionserial]): 0));
									pst.setInt(8, (rateoptionb != null && rateoptionb.length > questionserial ? uF.parseToInt(rateoptionb[questionserial]): 0));
									pst.setInt(9, (rateoptionc != null && rateoptionc.length > questionserial ? uF.parseToInt(rateoptionc[questionserial]): 0));
									pst.setInt(10, (rateoptiond != null && rateoptiond.length > questionserial ? uF.parseToInt(rateoptiond[questionserial]): 0));
									pst.setInt(11, (rateoptione != null && rateoptione.length > questionserial ? uF.parseToInt(rateoptione[questionserial]): 0));
									pst.setString(12, option.toString());
									pst.setBoolean(13, uF.parseToBoolean(addFlag[i]));
									pst.setInt(14, uF.parseToInt(ansType));
									pst.executeUpdate();
									pst.close();
										
									/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
									pst.setString(1, question[questionserial]);
									pst.setString(2, optiona[questionserial]);
									pst.setString(3, optionb[questionserial]);
									pst.setString(4, optionc[questionserial]);
									pst.setString(5, optiond[questionserial]);
									pst.setString(6, option.toString());
									pst.setBoolean(7,
											uF.parseToBoolean(addFlag[i]));
									pst.setInt(8, uF.parseToInt(ansType));
									pst.execute();
									pst.close();*/
									
									pst = con
											.prepareStatement("select max(question_bank_id) from question_bank");
									rst = pst.executeQuery();
//									System.out.println("new Date ===> " + new Date());
									while (rst.next()) {
										question_id = rst.getInt(1);
									}
									rst.close();
									pst.close();

								}

								pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,"
									+ "weightage,appraisal_id,appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
								pst.setInt(1, question_id);
								pst.setInt(2, measure_id);
								pst.setInt(3, uF.parseToInt(attribute[i]));
								pst.setDouble(4,uF.parseToDouble(weightage[questionserial]));
								pst.setInt(5, uF.parseToInt(id));
								pst.setInt(6, appraisal_level_id);
								pst.setInt(7, scorecard_id);
								pst.executeUpdate();
								pst.close();
							}
							questionserial++;
						}
					}

				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private Map<String, String> getOrientationMember(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();

		try {

			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				// orientationMemberMp.put(rs.getString("orientation_member_id"),rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"),
						rs.getString("member_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		return orientationMemberMp;
	}

	
	public void insertDatawithGoalObjective() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		
		try {
				
			String[] scoreSectionName = request
					.getParameterValues("scoreSectionName");
			String[] scoreCardDescription = request
					.getParameterValues("scoreCardDescription");
			String[] scoreCardWeightage = request
					.getParameterValues("scoreCardWeightage");
	
			String[] goalSectionName = request
					.getParameterValues("goalSectionName");
			String[] goalDescription = request
					.getParameterValues("goalDescription");
			String[] goalWeightage = request.getParameterValues("goalWeightage");
	
			String[] objectiveSectionName = request
					.getParameterValues("objectiveSectionName");
			String[] objectiveDescription = request
					.getParameterValues("objectiveDescription");
			String[] objectiveWeightage = request
					.getParameterValues("objectiveWeightage");
	
			String[] measuresSectionName = request
					.getParameterValues("measuresSectionName");
			String[] measuresDescription = request
					.getParameterValues("measuresDescription");
			String[] measureWeightage = request
					.getParameterValues("measureWeightage");
	
			String[] questionSelect = request.getParameterValues("questionSelect");
			String[] weightage = request.getParameterValues("weightage");
			String[] measurecount = request.getParameterValues("measurecount");
			String[] questioncount = request.getParameterValues("questioncount");
			String[] goalcount = request.getParameterValues("goalcount");
			String[] objectivecount = request.getParameterValues("objectivecount");
			String[] question = request.getParameterValues("question");
	
			String levelTitle = request.getParameter("levelTitle");
			String shortDesrciption = request.getParameter("shortDesrciption");
			String longDesrciption = request.getParameter("longDesrciption");
			String[] attribute = request.getParameterValues("attribute");
			String appraisalSystem = request.getParameter("appraisalSystem");
			String scoreCard = request.getParameter("scoreCard");
	
			String[] addFlag = request.getParameterValues("status");
			String[] optiona = request.getParameterValues("optiona");
			String[] optionb = request.getParameterValues("optionb");
			String[] optionc = request.getParameterValues("optionc");
			String[] optiond = request.getParameterValues("optiond");
			String[] optione = request.getParameterValues("optione");
			String[] rateoptiona = request.getParameterValues("rateoptiona");
			String[] rateoptionb = request.getParameterValues("rateoptionb");
			String[] rateoptionc = request.getParameterValues("rateoptionc");
			String[] rateoptiond = request.getParameterValues("rateoptiond");
			String[] rateoptione = request.getParameterValues("rateoptione");
			String[] orientt = request.getParameterValues("orientt");
	
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);

			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,"
							+ "appraisal_system,scorecard_type,appraisal_id,main_level_id)values(?,?,?,?,?,?,?)");

			pst.setString(1, levelTitle);
			pst.setString(2, shortDesrciption);
			pst.setString(3, longDesrciption);
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.execute();
			pst.close();
			
			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int questionserial = 0;
			int measureserial = 0;
			int goalserial = 0;
			int objectiveserial = 0;
			for (int i = 0; i < scoreSectionName.length; i++) {

				pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
								+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
				pst.setString(1, scoreSectionName[i]);
				pst.setString(2, scoreCardDescription[i]);
				pst.setString(3, scoreCardWeightage[i]);
				pst.setInt(4, appraisal_level_id);
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(attribute[i]));
				pst.execute();
				pst.close();
				
				int scorecard_id = 0;
				pst = con
						.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
				rst = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				while (rst.next()) {
					scorecard_id = rst.getInt(1);
				}
				rst.close();
				pst.close();

				int goal = uF.parseToInt(goalcount[i]);
				for (int j = 0; j < goal; j++) {

					pst = con.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
						+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
					pst.setString(1, goalSectionName[goalserial]);
					pst.setString(2, goalDescription[goalserial]);
					pst.setString(3, goalWeightage[goalserial]);
					pst.setInt(4, scorecard_id);
					pst.setInt(5, uF.parseToInt(id));
					pst.execute();
					pst.close();
					
					int goal_id = 0;
					pst = con.prepareStatement("select max(goal_id) from appraisal_goal_details");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						goal_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
					
					int objective = uF.parseToInt(objectivecount[goalserial]);
					goalserial++;
					for (int m = 0; m < objective; m++) {

						pst = con.prepareStatement("insert into appraisal_objective_details(objective_section_name,objective_description,"
							+ "objective_weightage,goal_id,appraisal_id)values(?,?,?,?,?)");
						pst.setString(1, objectiveSectionName[objectiveserial]);
						pst.setString(2, objectiveDescription[objectiveserial]);
						pst.setString(3, objectiveWeightage[objectiveserial]);
						pst.setInt(4, goal_id);
						pst.setInt(5, uF.parseToInt(id));
						pst.execute();
						pst.close();
						
						int objective_id = 0;
						pst = con.prepareStatement("select max(objective_id) from appraisal_objective_details");
						rst = pst.executeQuery();
//						System.out.println("new Date ===> " + new Date());
						while (rst.next()) {
							objective_id = rst.getInt(1);
						}
						rst.close();
						pst.close();

						int measure = uF.parseToInt(measurecount[objectiveserial]);
						objectiveserial++;
						for (int k = 0; k < measure; k++) {
							pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,objective_id,appraisal_id,weightage)"
								+ "values(?,?,?,?,?)");
							pst.setString(1, measuresSectionName[measureserial]);
							pst.setString(2, measuresDescription[measureserial]);
							pst.setInt(3, objective_id);
							pst.setInt(4, uF.parseToInt(id));
							pst.setString(5, measureWeightage[measureserial]);
							pst.execute();
							pst.close();
							
							int measure_id = 0;
							pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
							rst = pst.executeQuery();
//							System.out.println("new Date ===> " + new Date());
							while (rst.next()) {
								measure_id = rst.getInt(1);
							}
							rst.close();
							pst.close();

							int questioncnt = uF.parseToInt(questioncount[measureserial]);
							measureserial++;
							for (int l = 0; l < questioncnt; l++) {

								int question_id = uF.parseToInt(questionSelect[questionserial]);
								if (questionSelect[questionserial].length() > 0) {
									if (uF.parseToInt(questionSelect[questionserial]) == 0) {

										String[] correct = request.getParameterValues("correct"+ orientt[questionserial]);
										String ansType = request.getParameter("ansType"+ orientt[questionserial]);
										StringBuilder option = new StringBuilder();

										for (int ab = 0; correct != null && ab < correct.length; ab++) {
											option.append(correct[ab] + ",");
										}
										
										pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
											"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
											"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
										pst.setString(1, question[questionserial]);
										pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
										pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
										pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
										pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
										pst.setString(6, (optione != null && optione.length > questionserial ? optione[questionserial]: ""));
										pst.setInt(7, (rateoptiona != null && rateoptiona.length > questionserial ? uF.parseToInt(rateoptiona[questionserial]): 0));
										pst.setInt(8, (rateoptionb != null && rateoptionb.length > questionserial ? uF.parseToInt(rateoptionb[questionserial]): 0));
										pst.setInt(9, (rateoptionc != null && rateoptionc.length > questionserial ? uF.parseToInt(rateoptionc[questionserial]): 0));
										pst.setInt(10, (rateoptiond != null && rateoptiond.length > questionserial ? uF.parseToInt(rateoptiond[questionserial]): 0));
										pst.setInt(11, (rateoptione != null && rateoptione.length > questionserial ? uF.parseToInt(rateoptione[questionserial]): 0));
										pst.setString(12, option.toString());
										pst.setBoolean(13, uF.parseToBoolean(addFlag[i]));
										pst.setInt(14, uF.parseToInt(ansType));
										pst.executeUpdate();
										pst.close();
											
										pst = con.prepareStatement("select max(question_bank_id) from question_bank");
										rst = pst.executeQuery();
//										System.out.println("new Date ===> " + new Date());
										while (rst.next()) {
											question_id = rst.getInt(1);
										}
										rst.close();
										pst.close();
									}

									pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,"
										+ "weightage,appraisal_id,appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
									pst.setInt(1, question_id);
									pst.setInt(2, measure_id);
									pst.setInt(3, uF.parseToInt(attribute[i]));
									pst.setDouble(4,uF.parseToDouble(weightage[questionserial]));
									pst.setInt(5, uF.parseToInt(id));
									pst.setInt(6, appraisal_level_id);
									pst.setInt(7, scorecard_id);
									pst.executeUpdate();
									pst.close();
								}
								questionserial++;
							}
						}
					}
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void insertData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		UtilityFunctions uF = new UtilityFunctions();
		db.setRequest(request);
		
		try {
			
			String levelTitle = request.getParameter("levelTitle");
			String shortDesrciption = request.getParameter("shortDesrciption");
			String longDesrciption = request.getParameter("longDesrciption");
			String[] attribute = request.getParameterValues("attribute");
			String appraisalSystem = request.getParameter("appraisalSystem");
			String scoreCard = request.getParameter("scoreCard");
	
			String[] scoreSectionName = request.getParameterValues("scoreSectionName");
			String[] scoreCardDescription = request.getParameterValues("scoreCardDescription");
			String[] scoreCardWeightage = request.getParameterValues("scoreCardWeightage");
	
			String[] measuresSectionName = request.getParameterValues("measuresSectionName");
			String[] measuresDescription = request.getParameterValues("measuresDescription");
			String[] measureWeightage = request.getParameterValues("measureWeightage");
	
			String[] questionSelect = request.getParameterValues("questionSelect");
			String[] weightage = request.getParameterValues("weightage");
			String[] measurecount = request.getParameterValues("measurecount");
			String[] questioncount = request.getParameterValues("questioncount");
			String[] question = request.getParameterValues("question");
	
			String[] addFlag = request.getParameterValues("status");
			String[] optiona = request.getParameterValues("optiona");
			String[] optionb = request.getParameterValues("optionb");
			String[] optionc = request.getParameterValues("optionc");
			String[] optiond = request.getParameterValues("optiond");
			String[] optione = request.getParameterValues("optione");
			String[] rateoptiona = request.getParameterValues("rateoptiona");
			String[] rateoptionb = request.getParameterValues("rateoptionb");
			String[] rateoptionc = request.getParameterValues("rateoptionc");
			String[] rateoptiond = request.getParameterValues("rateoptiond");
			String[] rateoptione = request.getParameterValues("rateoptione");
			String[] orientt = request.getParameterValues("orientt");
			
			con = db.makeConnection(con);
			Map<String, String> orientationMemberMp = getOrientationMember(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);

			pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,"
				+ "appraisal_system,scorecard_type,appraisal_id,main_level_id)values(?,?,?,?,?,?,?)");
			pst.setString(1, levelTitle);
			pst.setString(2, shortDesrciption);
			pst.setString(3, longDesrciption);
			pst.setInt(4, uF.parseToInt(appraisalSystem));
			pst.setInt(5, uF.parseToInt(scoreCard));
			pst.setInt(6, uF.parseToInt(id));
			pst.setInt(7, uF.parseToInt(getMain_level_id()));
			pst.execute();
			pst.close();
			
			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			int questionserial = 0;
			int measureserial = 0;

			for (int i = 0; i < scoreSectionName.length; i++) {

				pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
					+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
				pst.setString(1, scoreSectionName[i]);
				pst.setString(2, scoreCardDescription[i]);
				pst.setString(3, scoreCardWeightage[i]);
				pst.setInt(4, appraisal_level_id);
				pst.setInt(5, uF.parseToInt(id));
				pst.setInt(6, uF.parseToInt(attribute[i]));
				pst.execute();
				pst.close();
				
				int scorecard_id = 0;
				pst = con.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
				rst = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				while (rst.next()) {
					scorecard_id = rst.getInt(1);
				}
				rst.close();
				pst.close();
				
				int measure = uF.parseToInt(measurecount[i]);
				for (int k = 0; k < measure; k++) {
					pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,scorecard_id,appraisal_id,weightage)"
									+ "values(?,?,?,?,?)");
					pst.setString(1, measuresSectionName[measureserial]);
					pst.setString(2, measuresDescription[measureserial]);
					pst.setInt(3, scorecard_id);
					pst.setInt(4, uF.parseToInt(id));
					pst.setString(5, measureWeightage[measureserial]);
					pst.execute();
					pst.close();
					
					int measure_id = 0;
					pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
					rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while (rst.next()) {
						measure_id = rst.getInt(1);
					}
					rst.close();
					pst.close();

					int questioncnt = uF.parseToInt(questioncount[measureserial]);
					measureserial++;
					for (int l = 0; l < questioncnt; l++) {

						int question_id = uF.parseToInt(questionSelect[questionserial]);

						if (questionSelect[questionserial].length() > 0) {

							if (uF.parseToInt(questionSelect[questionserial]) == 0) {

								String[] correct = request.getParameterValues("correct"+ orientt[questionserial]);
								String ansType = request.getParameter("ansType"+ orientt[questionserial]);
								StringBuilder option = new StringBuilder();

								for (int ab = 0; correct != null
										&& ab < correct.length; ab++) {
									option.append(correct[ab] + ",");
								}

								pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
									"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type) " +
									"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
								pst.setString(1, question[questionserial]);
								pst.setString(2, (optiona != null && optiona.length > questionserial ? optiona[questionserial]: ""));
								pst.setString(3, (optionb != null && optionb.length > questionserial ? optionb[questionserial]: ""));
								pst.setString(4, (optionc != null && optionc.length > questionserial ? optionc[questionserial]: ""));
								pst.setString(5, (optiond != null && optiond.length > questionserial ? optiond[questionserial]: ""));
								pst.setString(6, (optione != null && optione.length > questionserial ? optione[questionserial]: ""));
								pst.setInt(7, (rateoptiona != null && rateoptiona.length > questionserial ? uF.parseToInt(rateoptiona[questionserial]): 0));
								pst.setInt(8, (rateoptionb != null && rateoptionb.length > questionserial ? uF.parseToInt(rateoptionb[questionserial]): 0));
								pst.setInt(9, (rateoptionc != null && rateoptionc.length > questionserial ? uF.parseToInt(rateoptionc[questionserial]): 0));
								pst.setInt(10, (rateoptiond != null && rateoptiond.length > questionserial ? uF.parseToInt(rateoptiond[questionserial]): 0));
								pst.setInt(11, (rateoptione != null && rateoptione.length > questionserial ? uF.parseToInt(rateoptione[questionserial]): 0));
								pst.setString(12, option.toString());
								pst.setBoolean(13, uF.parseToBoolean(addFlag[i]));
								pst.setInt(14, uF.parseToInt(ansType));
								pst.executeUpdate();
								pst.close();
									
									
								/*pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,question_type)values(?,?,?,?,?,?,?,?)");
								pst.setString(1, question[questionserial]);
								pst.setString(2, optiona[questionserial]);
								pst.setString(3, optionb[questionserial]);
								pst.setString(4, optionc[questionserial]);
								pst.setString(5, optiond[questionserial]);
								pst.setString(6, option.toString());
								pst.setBoolean(7, uF.parseToBoolean(addFlag[i]));
								pst.setInt(8, uF.parseToInt(ansType));
								pst.execute();
								pst.close();*/
								
								pst = con.prepareStatement("select max(question_bank_id) from question_bank");
								rst = pst.executeQuery();
//								System.out.println("new Date ===> " + new Date());
								while (rst.next()) {
									question_id = rst.getInt(1);
								}
								rst.close();
								pst.close();
							}

							pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,"
								+ "weightage,appraisal_id,appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
							pst.setInt(1, question_id);
							pst.setInt(2, measure_id);
							pst.setInt(3, uF.parseToInt(attribute[i]));
							pst.setDouble(4, uF.parseToDouble(weightage[questionserial]));
							pst.setInt(5, uF.parseToInt(id));
							pst.setInt(6, appraisal_level_id);
							pst.setInt(7, scorecard_id);
							pst.executeUpdate();
							pst.close();
						}
						questionserial++;
					}
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

//	public String getEmployeeList(String self, int type) {
//		StringBuilder sb = new StringBuilder();
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		con = db.makeConnection(con);
//		try {
//
//			if (type == 2) {
//
//				pst = con
//						.prepareStatement("select supervisor_emp_id from employee_official_details where emp_id in ("
//								+ self + ") and supervisor_emp_id!=0");
//				rs = pst.executeQuery();
////				System.out.println("new Date ===> " + new Date());
//				int cnt = 0;
//				while (rs.next()) {
//					if (cnt == 0) {
//						sb.append(rs.getString("supervisor_emp_id"));
//					} else {
//						sb.append("," + rs.getString("supervisor_emp_id"));
//
//					}
//					cnt++;
//				}
//				return sb.toString();
//
//			} else if (type == 3) {
//
//			} else if (type == 4) {
//
//				pst = con
//						.prepareStatement("select grade_id from employee_official_details  where emp_id in("
//								+ self + ") group by grade_id");
//				rs = pst.executeQuery();
////				System.out.println("new Date ===> " + new Date());
//				StringBuilder sb4 = new StringBuilder();
//				int cnt = 0;
//				while (rs.next()) {
//					if (cnt == 0) {
//						sb4.append(rs.getString("grade_id"));
//					} else {
//						sb4.append("," + rs.getString("grade_id"));
//
//					}
//					cnt++;
//				}
//				pst = con
//						.prepareStatement("select wlocation_id from employee_official_details  where emp_id in("
//								+ self + ") group by wlocation_id");
//				rs = pst.executeQuery();
////				System.out.println("new Date ===> " + new Date());
//				StringBuilder sb5 = new StringBuilder();
//				cnt = 0;
//				while (rs.next()) {
//					if (cnt == 0) {
//						sb5.append(rs.getString("wlocation_id"));
//					} else {
//						sb5.append("," + rs.getString("wlocation_id"));
//
//					}
//					cnt++;
//				}
//				pst = con
//						.prepareStatement("select emp_id from employee_official_details  where wlocation_id in("
//								+ sb5.toString()
//								+ ") and grade_id in("
//								+ sb4.toString() + ") group by emp_id");
//
//				rs = pst.executeQuery();
////				System.out.println("new Date ===> " + new Date());
//				cnt = 0;
//				while (rs.next()) {
//					if (cnt == 0) {
//						sb.append(rs.getString("emp_id"));
//					} else {
//						sb.append("," + rs.getString("emp_id"));
//
//					}
//					cnt++;
//				}
//				return sb.toString();
//
//			} else if (type == 5) {
//
//			} else if (type == 6) {
//
//			} else if (type == 7) {
//
//				pst = con
//						.prepareStatement("select wlocation_id from employee_official_details  where emp_id in("
//								+ self + ") group by wlocation_id");
//				rs = pst.executeQuery();
////				System.out.println("new Date ===> " + new Date());
//				StringBuilder sb5 = new StringBuilder();
//				int cnt = 0;
//				while (rs.next()) {
//					if (cnt == 0) {
//						sb5.append(rs.getString("wlocation_id"));
//					} else {
//						sb5.append("," + rs.getString("wlocation_id"));
//
//					}
//					cnt++;
//				}
//
//				pst = con
//						.prepareStatement("select * from employee_official_details eod,user_details ud where ud.emp_id=eod.emp_id and ud.usertype_id=? and wlocation_id in("
//								+ sb5.toString() + ")");
//				pst.setInt(1, 7);
//				rs = pst.executeQuery();
////				System.out.println("new Date ===> " + new Date());
//				cnt = 0;
//				while (rs.next()) {
//					if (cnt == 0) {
//						sb.append(rs.getString("emp_id"));
//					} else {
//						sb.append("," + rs.getString("emp_id"));
//
//					}
//					cnt++;
//				}
//				return sb.toString();
//			} else if (type == 8) {
//
//			} else if (type == 9) {
//
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//
//		return null;
//	}

//	private String getManagerLocation() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//		Database db = new Database();
//		UtilityFunctions uF = new UtilityFunctions();
//		String location = "";
//
//		con = db.makeConnection(con);
//		try {
//			pst = con
//					.prepareStatement("select e.wlocation_id from employee_official_details e where e.emp_id=?");
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			rst = pst.executeQuery();
//			while (rst.next()) {
//				location = rst.getString(1);
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rst);
//		}
//
//		return location;
//	}

	

	public List<FillAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	public void getattribute() {

		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < attributeList.size(); i++) {
			FillAttribute fillAttribute = attributeList.get(i);

			sb.append("<option value=\"" + fillAttribute.getId() + "\">"
					+ fillAttribute.getName() + "</option>");

		}
		request.setAttribute("attribute", sb.toString());

	}

	public void getAppraisalQuestionList() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			StringBuilder sb = new StringBuilder("");

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from question_bank where is_add=true");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				sb.append("<option value=\"" + rs.getString("question_bank_id") + "\">" + rs.getString("question_text") + "</option>");
			}
			rs.close();
			pst.close();
			
			sb.append("<option value=\"0\">Add new Question</option>");

			request.setAttribute("option", sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();

		if (strID != null && !strID.equals("")) {
			strID = strID.substring(1, strID.length() - 1);
			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						sb.append("," + mp.get(temp[i].trim()));
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

	public Map<String, String> getLevelMap(Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		try {
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmLevelMap;
	}

	
	private Map<String, String> getLocationMap(Connection con) {
		Map<String, String> mplocation = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {

			pst = con.prepareStatement("select * from work_location_info");
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				mplocation.put(rst.getString("wlocation_id"), rst.getString("wlocation_name"));
			}
			rst.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

		return mplocation;
	}

	public Map<String, String> getAppraisalQuestionMap(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();

		try {
			pst = con.prepareStatement("select * from question_bank qb, appraisal_question_details aqd where qb.question_bank_id=aqd.question_id and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				AppraisalQuestion.put(rs.getString("question_bank_id"), rs.getString("question_text"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return AppraisalQuestion;
	}

	
	public Map<String, String> getAttributeMap(Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from appraisal_attribute ");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				AppraisalQuestion.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return AppraisalQuestion;
	}

	
	private Map<String, String> getOrientationValue(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMp = new HashMap<String, String>();
		Map<String, String> hmorientationMembers = new HashMap<String, String>();
		try {

			pst = con.prepareStatement("select * from apparisal_orientation");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				orientationMp.put(rs.getString("apparisal_orientation_id"), rs.getString("orientation_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("orientationMp", orientationMp);

			pst = con.prepareStatement("select * from orientation_member");
			rs = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rs.next()) {
				hmorientationMembers.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmorientationMembers", hmorientationMembers);

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return orientationMp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<FillAnswerType> getAnsTypeList() {
		return ansTypeList;
	}

	public void setAnsTypeList(List<FillAnswerType> ansTypeList) {
		this.ansTypeList = ansTypeList;
	}

	public String getOreinted() {
		return oreinted;
	}

	public void setOreinted(String oreinted) {
		this.oreinted = oreinted;
	}

	public String getMain_level_id() {
		return main_level_id;
	}

	public void setMain_level_id(String main_level_id) {
		this.main_level_id = main_level_id;
	}

	public String getMLID() {
		return MLID;
	}

	public void setMLID(String mLID) {
		MLID = mLID;
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
