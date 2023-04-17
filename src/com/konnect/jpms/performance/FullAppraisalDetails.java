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
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.recruitment.UpdateADRRequest;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class FullAppraisalDetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;
	Map<String, List<Map<String, List<List<String>>>>> levelMp = new HashMap<String, List<Map<String, List<List<String>>>>>();

	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(UpdateADRRequest.class);

	private String id;
	private String empId;
	private String appFreqId;
	
	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	String type;

	public String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/performance/AppraisalSummary.jsp");
		request.setAttribute(TITLE, "Appraisal Summary");
		getAppraisalDetail();
		getReport();
		getAttributeDetails();
		getCurrentLevelAnswer();
		request.setAttribute("levelMp", levelMp);
		return SUCCESS;

	}

	private Map<String,String>  getOrientationMember(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Map<String,String> orientationMemberMp=new HashMap<String,String>();
		try {
			
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs=pst.executeQuery();
			while(rs.next()){
				orientationMemberMp.put(rs.getString("orientation_member_id"),rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("orientationMemberMp", orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
			return orientationMemberMp;
	}
	
//	private String getOrientationMemberDetails(int id) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		ResultSet rs = null;
//		Map<String,String> orientationMp=(Map<String,String>)request.getAttribute("orientationMemberMp");
//		StringBuilder sb=new StringBuilder();
//		try {
//			List<String> memberList=new ArrayList<String>();
//			con = db.makeConnection(con);
//			
//			pst = con.prepareStatement("select * from orientation_details where orientation_id=?");
//			pst.setInt(1,id);
//			rs=pst.executeQuery();
//			
//			while(rs.next()){
//				sb.append(orientationMp.get(rs.getString("member_id"))+",");
//				memberList.add(rs.getString("member_id"));
//			}
//			
//				request.setAttribute("memberList", memberList);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//		}
//		return sb.toString();
//		}
	
	private Map<String,String> getOrientationValue(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Map<String,String> orientationMp=new HashMap<String,String>();
		try {
			
			pst = con.prepareStatement("select * from apparisal_orientation");
			rs=pst.executeQuery();
			while(rs.next()){
				orientationMp.put(rs.getString("apparisal_orientation_id"),rs.getString("orientation_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("orientationMp", orientationMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMp;
	}
	
	
	private void getAttributeDetails() {
		Map<String, String> mpAttribute = new HashMap<String, String>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_attribute where status=true");
			rs = pst.executeQuery();
			while (rs.next()) {
				mpAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
			request.setAttribute("mpAttribute", mpAttribute);

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
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);
			Map<String, String> attributeMp = getAttributeMap(con);

			pst = con.prepareStatement("select * from appraisal_scorecard_details where level_id =? and appraisal_id=?");
			pst.setInt(1, id);
			pst.setInt(2, uF.parseToInt(getId()));

			rs = pst.executeQuery();
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
				innerList.add(uF.showData(attributeMp.get(rs.getString("appraisal_attribute")), ""));

				List<List<String>> outerList = scoreMp.get(rs.getString("level_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_measure_details where scorecard_id in(" + scorecard_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));

			rs = pst.executeQuery();
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
				List<List<String>> outerList = measureMp.get(rs.getString("scorecard_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				measureMp.put(rs.getString("scorecard_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_question_details where measure_id in(" + measure_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));

			rs = pst.executeQuery();

			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_id"));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));

				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
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
			levelMp.put(id + "", list);

		} catch (Exception e) {
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
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);
			Map<String, String> attributeMp = getAttributeMap(con);

			pst = con.prepareStatement("select * from appraisal_scorecard_details where level_id =? and appraisal_id=?");
			pst.setInt(1, id);
			pst.setInt(2, uF.parseToInt(getId()));

			rs = pst.executeQuery();
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
				innerList.add(uF.showData(attributeMp.get(rs.getString("appraisal_attribute")), ""));
				List<List<String>> outerList = scoreMp.get(rs.getString("level_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_goal_details where scorecard_id in(" + scorecard_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));

			rs = pst.executeQuery();
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

				List<List<String>> outerGoalList = GoalMp.get(rs.getString("scorecard_id"));
				if (outerGoalList == null)
					outerGoalList = new ArrayList<List<String>>();
				outerGoalList.add(innerGoalList);
				// outerGoalList.add(innerGoalList);

				GoalMp.put(rs.getString("scorecard_id"), outerGoalList);

			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_measure_details where goal_id in(" + goal_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));

			rs = pst.executeQuery();
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
				List<List<String>> outerList = measureMp.get(rs.getString("goal_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				measureMp.put(rs.getString("goal_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_question_details where measure_id in (" + measure_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));

			rs = pst.executeQuery();

			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_id"));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));

				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
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
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		

		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);
			Map<String, String> attributeMp = getAttributeMap(con);
			pst = con.prepareStatement("select * from appraisal_scorecard_details where level_id =? and appraisal_id=?");
			pst.setInt(1, id);
			pst.setInt(2, uF.parseToInt(getId()));
			rs = pst.executeQuery();
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
				innerList.add(uF.showData(attributeMp.get(rs.getString("appraisal_attribute")), ""));

				List<List<String>> outerList = scoreMp.get(rs.getString("level_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_goal_details where scorecard_id in(" + scorecard_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
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

				List<List<String>> outerGoalList = GoalMp.get(rs.getString("scorecard_id"));
				if (outerGoalList == null)
					outerGoalList = new ArrayList<List<String>>();
				outerGoalList.add(innerGoalList);
				// outerGoalList.add(innerGoalList);

				GoalMp.put(rs.getString("scorecard_id"), outerGoalList);

			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_objective_details where goal_id in(" + goal_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
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

				List<List<String>> outerList = objectiveMp.get(rs.getString("goal_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				objectiveMp.put(rs.getString("goal_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_measure_details where objective_id in(" + objective_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
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
				List<List<String>> outerList = measureMp.get(rs.getString("objective_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				measureMp.put(rs.getString("objective_id"), outerList);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_question_details where measure_id in(" + measure_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));

			rs = pst.executeQuery();

			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_id"));
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));

				List<List<String>> outerList = questionMp.get(rs.getString("measure_id"));
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

	public void getReport() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			List<List<String>> outerList1 = new ArrayList<List<String>>();
			Map<String, String> attributeMp = getAttributeMap(con);
			// String appraisal_level_id = null;
			// int i = 0;
			while (rs.next()) {

				// if (i == 0) {
				// appraisal_level_id = rs.getString("appraisal_level_id");
				// } else {
				// appraisal_level_id += ","+
				// rs.getString("appraisal_level_id");
				// }
				// i++;
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
						getDataObjectiveMeasureGoal(uF.parseToInt(rs.getString("appraisal_level_id")));
					if (uF.parseToInt(rs.getString("scorecard_type")) == 2)
						getDataMeasure(uF.parseToInt(rs.getString("appraisal_level_id")));
					else if (uF.parseToInt(rs.getString("scorecard_type")) == 3) {
						getDataMeasureGoal(uF.parseToInt(rs.getString("appraisal_level_id")));
					}
				} else if (uF.parseToInt(rs.getString("appraisal_system")) == 2) {
					// code for other option
					getOtherData(uF.parseToInt(rs.getString("appraisal_level_id")));
				}

				outerList1.add(innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("outerList1", outerList1);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
public void getCurrentLevelAnswer(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from appraisal_question_answer  where appraisal_id=? and emp_id=? and appraisal_freq_id=? ");
			
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2,uF.parseToInt(getEmpId()));
			pst.setInt(3,uF.parseToInt(getAppFreqId()));

			rs = pst.executeQuery();
			Map<String,Map<String,Map<String,String>>> questionanswerMp=new HashMap<String,Map<String,Map<String,String>>>();
			
			while (rs.next()) {
				
				Map<String,Map<String,String>> outerMp=questionanswerMp.get(rs.getString("user_type_id"));
				if(outerMp==null)outerMp= new HashMap<String,Map<String,String>>();
				
				Map<String,String> innerMp=new HashMap<String,String>();
				innerMp.put("ANSWER", rs.getString("answer"));
				innerMp.put("REMARK", rs.getString("remark"));
				innerMp.put("MARKS", rs.getString("marks"));
				if(uF.parseToInt(rs.getString("scorecard_id"))!=0)
					outerMp.put(rs.getString("scorecard_id")+"question"+rs.getString("question_id"),innerMp);
				else{
					outerMp.put(rs.getString("other_id")+"question"+rs.getString("question_id"),innerMp);

				}
				questionanswerMp.put(rs.getString("user_type_id"),outerMp);
				
			}
			rs.close();
			pst.close();
			request.setAttribute("questionanswerMp", questionanswerMp);

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
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			Map<String, String> AppraisalQuestion = getAppraisalQuestionMap(con, uF);

			pst = con.prepareStatement("select * from appraisal_other_question_type_details where level_id =?");
			pst.setInt(1, id);
			rs = pst.executeQuery();
			String othe_question_type_id = null;
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					othe_question_type_id = rs.getString("othe_question_type_id");
				} else {
					othe_question_type_id += "," + rs.getString("othe_question_type_id");
				}
				i++;

			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_question_details where other_id in(" + othe_question_type_id + ")");
			rs = pst.executeQuery();
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(AppraisalQuestion.get(rs.getString("question_id")), ""));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("other_short_description"));

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

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getAppraisalDetail() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		con = db.makeConnection(con);
		Map<String, String> hmDesignation = CF.getDesigMap(con);
		Map<String, String> hmGradeMap = CF.getGradeMap(con);
		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
		Map<String, String> hmLevelMap = getLevelMap(con);
		Map<String, String> hmLocation = getLocationMap(con);
		Map<String, String> mpdepart = CF.getDeptMap(con);
		Map<String,String> orientationMp=getOrientationValue(con);
		Map<String,String> orientationMemberMp=getOrientationMember(con);
		try {
			Map<String, String> hmFrequency = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFrequency.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}		
			rs.close();
			pst.close();
			
			List<String> appraisalList = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> memberList=Arrays.asList(rs.getString("usertype_member").split(","));
				String memberName="";
				
				for(int i=0;i<memberList.size();i++){
					if(i==0)
					memberName+=orientationMemberMp.get(memberList.get(i));
					else
						memberName+=","+orientationMemberMp.get(memberList.get(i));
				}
				
				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_name"), ""));
				appraisalList.add(uF.showData(orientationMp.get(rs.getString("oriented_type"))+"&deg( "+memberName+" )", ""));
				appraisalList.add(uF.showData(rs.getString("self_ids"), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("level_id"), hmLevelMap), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("desig_id"), hmDesignation), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("grade_id"), hmGradeMap), ""));
				appraisalList.add( uF.showData(hmFrequency.get(rs.getString("frequency")),""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("wlocation_id"), hmLocation), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("department_id"), mpdepart), ""));
				appraisalList.add(uF.showData(rs.getString("supervisor_id"), ""));
				appraisalList.add(uF.showData(rs.getString("peer_ids"), ""));
				appraisalList.add(uF.showData(getAppendData(rs.getString("self_ids"), hmEmpName), ""));
				appraisalList.add(uF.showData(rs.getString("emp_status"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_type"), ""));
				appraisalList.add(uF.showData(rs.getString("ceo_ids"), ""));
				appraisalList.add(uF.showData(rs.getString("ceo_ids"), ""));
				request.setAttribute("memberList", memberList);
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

	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();

		if (strID != null && !strID.equals("")) {

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
			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
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
			while (rst.next()) {
				mplocation.put(rst.getString("wlocation_id"), rst.getString("wlocation_name"));
			}
			rst.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mplocation;
	}

	public Map<String, String> getAppraisalQuestionMap(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();
		Map<String, List<String>> hmQuestion = new HashMap<String, List<String>>();
		try {

			pst = con.prepareStatement("select * from question_bank qb, appraisal_question_details aqd where qb.question_bank_id=aqd.question_id and appraisal_id=?");
			pst.setInt(1,uF.parseToInt(getId()));
			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();

			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_bank_id"));
				innerList.add(rs.getString("question_text"));
				innerList.add(rs.getString("option_a"));
				innerList.add(rs.getString("option_b"));
				innerList.add(rs.getString("option_c"));
				innerList.add(rs.getString("option_d"));
				innerList.add(rs.getString("correct_ans"));
				innerList.add(rs.getString("is_add"));
				innerList.add(rs.getString("question_type"));

				outerList.add(innerList);
				hmQuestion.put(rs.getString("question_bank_id"), innerList);

				AppraisalQuestion.put(rs.getString("question_bank_id"),
						rs.getString("question_text"));

			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmQuestion", hmQuestion);

		} catch (Exception e) {
			e.printStackTrace();
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
			while (rs.next()) {
				AppraisalQuestion.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return AppraisalQuestion;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

}
