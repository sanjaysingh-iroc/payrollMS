package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class CreateAppraisalFromTemplate implements ServletRequestAware,
		IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
	private String existID;
	private String id;
	private String appsystem;
	private String myReviewStatus;
	private String appFreqId;
	private String appraisal_name;
	Map<String, List<Map<String, List<List<String>>>>> levelMp = new HashMap<String, List<Map<String, List<List<String>>>>>();

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		getSectionData();
		getSubsectionData();

		request.setAttribute("levelMp", levelMp);
		setAppsystem("appraisal");
		createAppraisalFromExistTemplate();
		insertLevelData();

		return "success";
	}

	private void insertLevelData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			List<List<String>> sectionList = (List<List<String>>) request.getAttribute("sectionList");
			Map<String, List<Map<String, List<List<String>>>>> levelMp = (Map<String, List<Map<String, List<List<String>>>>>) request.getAttribute("levelMp");
			Map<String, List<List<String>>> hmSubsectionData = (Map<String, List<List<String>>>)request.getAttribute("hmSubsectionData");
			
			for (int a = 0; sectionList != null && a < sectionList.size(); a++) {
				List<String> innerSectionList = sectionList.get(a);

				pst = con.prepareStatement("insert into appraisal_main_level_details(level_title,short_description,long_description,"
								+ "appraisal_id,attribute_id,section_weightage,hr,manager,peer,self,subordinate,grouphead,vendor," +
								"client,added_by,entry_date,ceo,hod,other_peer)values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setString(1, innerSectionList.get(1));
				pst.setString(2, innerSectionList.get(2));
				pst.setString(3, innerSectionList.get(3));
				pst.setInt(4, uF.parseToInt(id));
				pst.setInt(5, uF.parseToInt(innerSectionList.get(4)));
				pst.setString(6, innerSectionList.get(5));
				pst.setInt(7, uF.parseToInt(innerSectionList.get(6)));
				pst.setInt(8, uF.parseToInt(innerSectionList.get(7)));
				pst.setInt(9, uF.parseToInt(innerSectionList.get(8)));
				pst.setInt(10, uF.parseToInt(innerSectionList.get(9)));
				pst.setInt(11, uF.parseToInt(innerSectionList.get(10)));
				pst.setInt(12, uF.parseToInt(innerSectionList.get(11)));
				pst.setInt(13, uF.parseToInt(innerSectionList.get(12)));
				pst.setInt(14, uF.parseToInt(innerSectionList.get(13)));
				pst.setInt(15, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(16, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
				pst.setInt(17, uF.parseToInt(innerSectionList.get(14)));
				pst.setInt(18, uF.parseToInt(innerSectionList.get(15)));
				pst.setInt(19, uF.parseToInt(innerSectionList.get(16)));
				pst.execute();
				pst.close();
				
				int main_level_id = 0;
				pst = con.prepareStatement("select max(main_level_id) from appraisal_main_level_details");
				rs = pst.executeQuery();

				while (rs.next()) {
					main_level_id = rs.getInt(1);
				}
				rs.close();
				pst.close();
//				System.out.println("main_level_id ===> "+main_level_id);
				List<List<String>> subSectionList = hmSubsectionData.get(innerSectionList.get(0));
			
			for (int i = 0; subSectionList != null && i < subSectionList.size(); i++) {
				List<String> innerList1 = subSectionList.get(i);
//				System.out.println("insert innerList1.get(6) ====> "+innerList1.get(6));
				
				pst = con.prepareStatement("insert into appraisal_level_details(level_title,short_description,long_description,appraisal_id," +
					"attribute_id,scorecard_type,appraisal_system,main_level_id,subsection_weightage,added_by,entry_date)" +
					" values(?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setString(1, innerList1.get(1));
				pst.setString(2, innerList1.get(2));
				pst.setString(3, innerList1.get(3));
				pst.setInt(4, uF.parseToInt(id));
				pst.setInt(5, uF.parseToInt(innerList1.get(4)));
				pst.setInt(6, uF.parseToInt(innerList1.get(5)));
				pst.setInt(7, uF.parseToInt(innerList1.get(6)));
				pst.setInt(8, main_level_id);
				pst.setString(9, innerList1.get(16));
				pst.setInt(10, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(11, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
				pst.execute();
				pst.close();
				
				int appraisal_level_id = 0;
				pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					appraisal_level_id = rs.getInt(1);
				}
				rs.close();
				pst.close();
//				System.out.println("appraisal_level_id ===> "+appraisal_level_id);
//				System.out.println("innerList1.get(6) ===> "+innerList1.get(6));
//				System.out.println("innerList1.get(5) ===> "+innerList1.get(5));
				
				if (uF.parseToInt(innerList1.get(6)) == 1) {
					if (uF.parseToInt(innerList1.get(5)) == 1) {
						List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
						Map<String, List<List<String>>> scoreMp = list.get(0);
						Map<String, List<List<String>>> measureMp = list.get(1);
						Map<String, List<List<String>>> questionMp = list.get(2);
						Map<String, List<List<String>>> GoalMp = list.get(3);
						Map<String, List<List<String>>> objectiveMp = list.get(4);

						List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
						for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
							List<String> scoreinnerList = scoreList.get(j);

							pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
											+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
							pst.setString(1, scoreinnerList.get(1));
							pst.setString(2, scoreinnerList.get(2));
							pst.setString(3, scoreinnerList.get(3));
							pst.setInt(4, appraisal_level_id);
							pst.setInt(5, uF.parseToInt(id));
							pst.setInt(6, uF.parseToInt(scoreinnerList.get(4)));
							pst.execute();
							pst.close();
							
							int scorecard_id = 0;
							pst = con.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
							rs = pst.executeQuery();
							while (rs.next()) {
								scorecard_id = rs.getInt(1);
							}
							rs.close();
							pst.close();
							
//							System.out.println("scorecard_id ===> "+scorecard_id);
							List<List<String>> goalList = GoalMp.get(scoreinnerList.get(0));
							for (int k = 0; goalList != null && k < goalList.size(); k++) {
								List<String> goalinnerList = goalList.get(k);

								pst = con.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
												+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
								pst.setString(1, goalinnerList.get(1));
								pst.setString(2, goalinnerList.get(2));
								pst.setString(3, goalinnerList.get(3));
								pst.setInt(4, scorecard_id);
								pst.setInt(5, uF.parseToInt(id));
								pst.execute();
								pst.close();
								
								int goal_id = 0;
								pst = con.prepareStatement("select max(goal_id) from appraisal_goal_details");
								rs = pst.executeQuery();
								while (rs.next()) {
									goal_id = rs.getInt(1);
								}
								rs.close();
								pst.close();

//								System.out.println("goal_id====>" + goal_id);

								List<List<String>> objectiveList = objectiveMp.get(goalinnerList.get(0));
								for (int l = 0; objectiveList != null && l < objectiveList.size(); l++) {
									List<String> objectivelinnerList = objectiveList.get(l);

									pst = con.prepareStatement("insert into appraisal_objective_details(objective_section_name,objective_description,"
													+ "objective_weightage,goal_id,appraisal_id)values(?,?,?,?,?)");
									pst.setString(1, objectivelinnerList.get(1));
									pst.setString(2, objectivelinnerList.get(2));
									pst.setString(3, objectivelinnerList.get(3));
									pst.setInt(4, goal_id);
									pst.setInt(5, uF.parseToInt(id));
									pst.execute();
									pst.close();
									
									int objective_id = 0;
									pst = con.prepareStatement("select max(objective_id) from appraisal_objective_details");
									rs = pst.executeQuery();
									while (rs.next()) {
										objective_id = rs.getInt(1);
									}
									rs.close();
									pst.close();

									List<List<String>> measureList = measureMp.get(objectivelinnerList.get(0));
									for (int m = 0; measureList != null && m < measureList.size(); m++) {
										List<String> measureinnerList = measureList.get(m);

										pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,objective_id,appraisal_id,weightage)"
														+ "values(?,?,?,?,?)");
										pst.setString(1, measureinnerList.get(1));
										pst.setString(2, measureinnerList.get(2));
										pst.setInt(3, objective_id);
										pst.setInt(4, uF.parseToInt(id));
										pst.setString(5, measureinnerList.get(3));
										pst.execute();
										pst.close();
										
										int measure_id = 0;
										pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
										rs = pst.executeQuery();
										while (rs.next()) {
											measure_id = rs.getInt(1);
										}
										rs.close();
										pst.close();

										List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
										for (int n = 0; questionList != null && n < questionList.size(); n++) {
											List<String> question1List = questionList.get(n);

											pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,"
												+ "weightage,appraisal_id,appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
											pst.setInt(1, uF.parseToInt(question1List.get(1)));
											pst.setInt(2, measure_id);
											pst.setInt(3, uF.parseToInt(question1List.get(10)));
											pst.setDouble(4, uF.parseToDouble(question1List.get(2)));
											pst.setInt(5, uF.parseToInt(id));
											pst.setInt(6, appraisal_level_id);
											pst.setInt(7, scorecard_id);
											pst.execute();
											pst.close();
										}
									}
								}
							}
						}
					} else if (uF.parseToInt(innerList1.get(5)) == 2) {
						List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
						Map<String, List<List<String>>> scoreMp = list.get(0);
						Map<String, List<List<String>>> measureMp = list.get(1);
						Map<String, List<List<String>>> questionMp = list.get(2);
						
						List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
						for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
							List<String> scoreInnerList = scoreList.get(j);
							
							pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
									+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
							pst.setString(1, scoreInnerList.get(1));
							pst.setString(2, scoreInnerList.get(2));
							pst.setString(3, scoreInnerList.get(3));
							pst.setInt(4, appraisal_level_id);
							pst.setInt(5, uF.parseToInt(id));
							pst.setInt(6, uF.parseToInt(scoreInnerList.get(4)));
							pst.execute();
							pst.close();
							
							int scorecard_id = 0;
							pst = con.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
							rs = pst.executeQuery();
		
							while (rs.next()) {
								scorecard_id = rs.getInt(1);
							}
							rs.close();
							pst.close();
							
							List<List<String>> measureList = measureMp.get(scoreInnerList.get(0));
							for (int k = 0; measureList != null && k < measureList.size(); k++) {
								List<String> measureinnerList = measureList.get(k);
								
								pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,scorecard_id,appraisal_id,weightage)"
										+ "values(?,?,?,?,?)");
								pst.setString(1, measureinnerList.get(1));
								pst.setString(2, measureinnerList.get(2));
								pst.setInt(3, scorecard_id);
								pst.setInt(4, uF.parseToInt(id));
								pst.setString(5, measureinnerList.get(3));
								pst.execute();
								pst.close();
								
								int measure_id = 0;
								pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
								rs = pst.executeQuery();
								while (rs.next()) {
									measure_id = rs.getInt(1);
								}
								rs.close();
								pst.close();
								
								List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
								for (int l = 0; questionList != null && l < questionList.size(); l++) {
									List<String> question1List = questionList.get(l);
									
									pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,"
										+ "weightage,appraisal_id,appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
									pst.setInt(1, uF.parseToInt(question1List.get(1)));
									pst.setInt(2, measure_id);
									pst.setInt(3, uF.parseToInt(question1List.get(10)));
									pst.setDouble(4, uF.parseToDouble(question1List.get(2)));
									pst.setInt(5, uF.parseToInt(id));
									pst.setInt(6, appraisal_level_id);
									pst.setInt(7, scorecard_id);
									pst.execute();
									pst.close();
								}
							}
						}

					}else if (uF.parseToInt(innerList1.get(5)) == 3){
						List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
						Map<String, List<List<String>>> scoreMp = list.get(0);
						Map<String, List<List<String>>> measureMp = list.get(1);
						Map<String, List<List<String>>> questionMp = list.get(2);
						Map<String, List<List<String>>> GoalMp = list.get(3);
						
						List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
						for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
							List<String> scoreInnerList = scoreList.get(j);
							
							pst = con.prepareStatement("insert into appraisal_scorecard_details(scorecard_section_name,scorecard_description,"
									+ "scorecard_weightage,level_id,appraisal_id,appraisal_attribute)values(?,?,?,?,?,?)");
							pst.setString(1, scoreInnerList.get(1));
							pst.setString(2, scoreInnerList.get(2));
							pst.setString(3, scoreInnerList.get(3));
							pst.setInt(4, appraisal_level_id);
							pst.setInt(5, uF.parseToInt(id));
							pst.setInt(6, uF.parseToInt(scoreInnerList.get(4)));
							pst.execute();
							pst.close();
							
							int scorecard_id = 0;
							pst = con.prepareStatement("select max(scorecard_id) from appraisal_scorecard_details");
							rs = pst.executeQuery();
							while (rs.next()) {
								scorecard_id = rs.getInt(1);
							}
							rs.close();
							pst.close();
							
							List<List<String>> goalList = GoalMp.get(scoreInnerList.get(0));
							for (int k = 0; goalList != null && k < goalList.size(); k++) {
								List<String> goalinnerList = goalList.get(k);
								
								pst = con.prepareStatement("insert into appraisal_goal_details(goal_section_name,goal_description,"
										+ "goal_weightage,scorecard_id,appraisal_id)values(?,?,?,?,?)");
								pst.setString(1, goalinnerList.get(1));
								pst.setString(2, goalinnerList.get(2));
								pst.setString(3, goalinnerList.get(3));
								pst.setInt(4, scorecard_id);
								pst.setInt(5, uF.parseToInt(id));
								pst.execute();
								pst.close();
								
								int goal_id = 0;
								pst = con.prepareStatement("select max(goal_id) from appraisal_goal_details");
								rs = pst.executeQuery();
								while (rs.next()) {
									goal_id = rs.getInt(1);
								}
								rs.close();
								pst.close();
								
								List<List<String>> measureList = measureMp.get(goalinnerList.get(0));
								for (int l = 0; measureList != null && l < measureList.size(); l++) {
									List<String> measureinnerList = measureList.get(l);
									
									pst = con.prepareStatement("insert into appraisal_measure_details(measure_section_name,measure_description,goal_id,appraisal_id,weightage)"
											+ "values(?,?,?,?,?)");
									pst.setString(1, measureinnerList.get(1));
									pst.setString(2, measureinnerList.get(2));
									pst.setInt(3, goal_id);
									pst.setInt(4, uF.parseToInt(id));
									pst.setString(5, measureinnerList.get(3));
									pst.execute();
									pst.close();
									
									int measure_id = 0;
									pst = con.prepareStatement("select max(measure_id) from appraisal_measure_details");
									rs = pst.executeQuery();
									while (rs.next()) {
										measure_id = rs.getInt(1);
									}
									rs.close();
									pst.close();
									
									List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
									for (int m = 0; questionList != null && m < questionList.size(); m++) {
										List<String> question1List = questionList.get(m);
										
										pst = con.prepareStatement("insert into appraisal_question_details(question_id,measure_id,attribute_id,"
											+ "weightage,appraisal_id,appraisal_level_id,scorecard_id) values(?,?,?,?, ?,?,?)");
										pst.setInt(1, uF.parseToInt(question1List.get(1)));
										pst.setInt(2, measure_id);
										pst.setInt(3, uF.parseToInt(question1List.get(10)));
										pst.setDouble(4, uF.parseToDouble(question1List.get(2)));
										pst.setInt(5, uF.parseToInt(id));
										pst.setInt(6, appraisal_level_id);
										pst.setInt(7, scorecard_id);
										pst.execute();
										pst.close();
									}
								}
							}
						}
					}

				} else if (uF.parseToInt(innerList1.get(6)) == 2) {
					
					List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
					Map<String, List<List<String>>> otherMp = list.get(0);
					Map<String, List<List<String>>> questionMp = list.get(1);
					
					List<List<String>> otherList = otherMp.get(innerList1.get(0));
					for (int j = 0; otherList != null && j < otherList.size(); j++) {
						List<String> otherInnerList = otherList.get(j);
						
						pst = con.prepareStatement("insert into appraisal_other_question_type_details(other_question_type,is_weightage,"
								+ "appraisal_id,level_id)values(?,?,?,?)");
						pst.setString(1, otherInnerList.get(2));
						pst.setBoolean(2, uF.parseToBoolean(otherInnerList.get(3)));
						pst.setInt(3, uF.parseToInt(id));
						pst.setInt(4, appraisal_level_id);
						pst.execute();
						pst.close();
						
						int other_question_type_id = 0;
						pst = con.prepareStatement("select max(othe_question_type_id) from appraisal_other_question_type_details");
						rs = pst.executeQuery();
						while (rs.next()) {
							other_question_type_id = rs.getInt(1);
						}
						rs.close();
						pst.close();
						
						List<List<String>> questionList = questionMp.get(otherInnerList.get(0));
						for (int k = 0; questionList != null && k < questionList.size(); k++) {
							List<String> question1List = questionList.get(k);
							
							pst = con.prepareStatement("insert into appraisal_question_details(question_id,other_id,attribute_id,weightage," +
								"appraisal_id,other_short_description,appraisal_level_id) values(?,?,?,?, ?,?,?)");
							pst.setInt(1, uF.parseToInt(question1List.get(1)));
							pst.setInt(2, other_question_type_id);
							pst.setInt(3, uF.parseToInt(question1List.get(10)));
							pst.setDouble(4, uF.parseToDouble(question1List.get(2)));
							pst.setInt(5, uF.parseToInt(id));
							pst.setString(6, question1List.get(11));
							pst.setInt(7, appraisal_level_id);
							pst.execute();
							pst.close();
							}
						}
					} else if (uF.parseToInt(innerList1.get(6)) == 3 || uF.parseToInt(innerList1.get(6)) == 4 || uF.parseToInt(innerList1.get(6)) == 5) {
						
						List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
						Map<String, List<List<String>>> goalKRATargetMp = list.get(0);
						
//						System.out.println("goalKRATarget insert levelMp ====> "+levelMp);
//						System.out.println("goalKRATarget insert goalKRATargetMp ====> "+goalKRATargetMp);
						
							List<List<String>> goalKRATargetList = goalKRATargetMp.get(innerList1.get(0));
//							System.out.println("goalKRATarget insert goalKRATargetList ====> "+goalKRATargetList);
							for (int k = 0; goalKRATargetList != null && k < goalKRATargetList.size(); k++) {
								List<String> goalKRATarget1List = goalKRATargetList.get(k);
								
								StringBuilder sbQuery = new StringBuilder();
								sbQuery.append("insert into appraisal_question_details(question_id,attribute_id,weightage,appraisal_id," +
									"appraisal_level_id,goal_kra_target_id,app_system_type");
								if(uF.parseToInt(innerList1.get(6)) == 4) {
									sbQuery.append(",kra_id");
								}
								sbQuery.append(") values (?,?,?,?, ?,?,?");
								if(uF.parseToInt(innerList1.get(6)) == 4) {
									sbQuery.append(",?");
								}
								sbQuery.append(")");
								pst = con.prepareStatement(sbQuery.toString());
								pst.setInt(1, uF.parseToInt(goalKRATarget1List.get(1)));
								pst.setInt(2, uF.parseToInt(goalKRATarget1List.get(2)));
								pst.setDouble(3, uF.parseToDouble(goalKRATarget1List.get(3)));
								pst.setInt(4, uF.parseToInt(id));
								pst.setInt(5, appraisal_level_id);
								pst.setInt(6, uF.parseToInt(goalKRATarget1List.get(14)));
								pst.setInt(7, uF.parseToInt(goalKRATarget1List.get(15)));
								if(uF.parseToInt(innerList1.get(6)) == 4) {
									pst.setInt(8, uF.parseToInt(goalKRATarget1List.get(16)));
								}
								pst.execute();
								pst.close();
							}
						}
				}
			}
			session.setAttribute(MESSAGE, SUCCESSM+"Successfully Created Review from "+appraisal_name+" Template."+END);
			//System.out.println("(String)session.getAttribute(IConstants.MESSAGE)====>"+(String)session.getAttribute(MESSAGE));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void createAppraisalFromExistTemplate() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(getExistID()));
			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			List<String> innerList = new ArrayList<String>();
			while (rs.next()) {
				innerList.add(rs.getString("appraisal_name"));//0
				innerList.add(rs.getString("oriented_type"));//1
				innerList.add(rs.getString("self_ids"));//2
				innerList.add(rs.getString("level_id"));//3
				innerList.add(rs.getString("desig_id"));//4
				innerList.add(rs.getString("grade_id"));//5
				innerList.add(rs.getString("wlocation_id"));//6
				innerList.add(rs.getString("department_id"));//7
				innerList.add(rs.getString("supervisor_id"));//8
				innerList.add(rs.getString("peer_ids"));//9
				innerList.add(rs.getString("self_ids"));//10
				innerList.add(rs.getString("emp_status"));//11
				innerList.add(rs.getString("appraisal_type"));//12
				innerList.add(rs.getString("added_by"));//13
				innerList.add(rs.getString("entry_date"));//14
				innerList.add(rs.getString("frequency"));//15
				innerList.add(rs.getString("from_date"));//16
				innerList.add(rs.getString("to_date"));//17
				innerList.add(rs.getString("appraisal_day"));//18
				innerList.add(rs.getString("appraisal_month"));//19
				innerList.add(rs.getString("weekday"));//20
				innerList.add(rs.getString("hr_ids"));//21
				innerList.add(rs.getString("usertype_member"));//22
				innerList.add(rs.getString("appraisal_description"));//23
				innerList.add(rs.getString("my_review_status"));//24
				innerList.add(rs.getString("ceo_ids"));//25
				innerList.add(rs.getString("hod_ids"));//26
				appraisal_name=rs.getString("appraisal_name");
			}
			rs.close();
			pst.close();

			if (innerList != null && innerList.size() > 0) {

				pst = con.prepareStatement("insert into appraisal_details(appraisal_name,oriented_type,employee_id,level_id,desig_id,grade_id,wlocation_id,department_id,"
								+ "supervisor_id,peer_ids,self_ids,emp_status,appraisal_type,added_by,entry_date,frequency,from_date,to_date,"
								+ "appraisal_day,appraisal_month,weekday,hr_ids,usertype_member,appraisal_description,my_review_status,is_publish,template_id,ceo_ids,hod_ids)"
								+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pst.setString(1, innerList.get(0));
				pst.setString(2, innerList.get(1));
				pst.setString(3, innerList.get(2));
				pst.setString(4, innerList.get(3));
				pst.setString(5, innerList.get(4));
				pst.setString(6, innerList.get(5));
				pst.setString(7, innerList.get(6));
				pst.setString(8, innerList.get(7));
				pst.setString(9, innerList.get(8));
				pst.setString(10, innerList.get(9));
				pst.setString(11, innerList.get(10));
				pst.setString(12, innerList.get(11));
				pst.setString(13, innerList.get(12));
				pst.setInt(14, uF.parseToInt(strSessionEmpId));
				pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));

				pst.setString(16, innerList.get(15));

				String from = uF.getDateFormat(innerList.get(16), DBDATE, DATE_FORMAT);
				String to = uF.getDateFormat(innerList.get(17), DBDATE, DATE_FORMAT);
				pst.setDate(17, uF.getDateFormat(from, DATE_FORMAT));
				pst.setDate(18, uF.getDateFormat(to, DATE_FORMAT));

				pst.setString(19, innerList.get(18));
				pst.setString(20, innerList.get(19));
				pst.setString(21, innerList.get(20));
				pst.setString(22, innerList.get(21));
				pst.setString(23, innerList.get(22));
				pst.setString(24, innerList.get(23));
				pst.setInt(25, uF.parseToInt(innerList.get(24)));
				pst.setBoolean(26, false);
				pst.setInt(27, uF.parseToInt(getExistID()));
				pst.setString(28, innerList.get(25));
				pst.setString(29, innerList.get(26));
				pst.execute();
				pst.close();

				pst = con.prepareStatement("select max(appraisal_details_id) from appraisal_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					id = rs.getString(1);
				}
				rs.close();
				pst.close();
				
				if(from != null && !from.equals("") && to!= null && !to.equals("")) {
//					***************************** appraisal Frequency Start ************************************
					AppraisalScheduler scheduler = new AppraisalScheduler(request, session, CF, uF, strSessionEmpId);
					scheduler.updateAppraisalDetails(id);
//					***************************** appraisal Frequency End ************************************
				}
				
			}
			System.out.println("new id=====>" + id);
			
			
			pst = con.prepareStatement("select * from appraisal_reviewee_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getExistID()));
			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			List<List<String>> revieweeList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innList = new ArrayList<String>();
				innList.add(rs.getString("reviewee_id"));//0
				innList.add(rs.getString("subordinate_ids"));//1
				innList.add(rs.getString("peer_ids"));//2
				innList.add(rs.getString("other_peer_ids"));//3
				innList.add(rs.getString("supervisor_ids"));//4
				innList.add(rs.getString("grand_supervisor_ids"));//5
				innList.add(rs.getString("hod_ids"));//6
				innList.add(rs.getString("ceo_ids"));//7
				innList.add(rs.getString("hr_ids"));//8
				innList.add(rs.getString("ghr_ids"));//9
				innList.add(rs.getString("recruiter_ids"));//10
				innList.add(rs.getString("other_ids"));//11
				revieweeList.add(innList);
				
			}
			rs.close();
			pst.close();
			
			for(int i=0; revieweeList!=null && i<revieweeList.size(); i++) {
				List<String> innList = revieweeList.get(i);
				pst = con.prepareStatement("insert into appraisal_reviewee_details (appraisal_id, reviewee_id,subordinate_ids,peer_ids,other_peer_ids," +
					"supervisor_ids,grand_supervisor_ids,hod_ids,ceo_ids,hr_ids,ghr_ids,recruiter_ids,other_ids,added_by,entry_date) " +
					"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)"); 
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(innList.get(0)));
				pst.setString(3, innList.get(1));
				pst.setString(4, innList.get(2));
				pst.setString(5, innList.get(3));
				pst.setString(6, innList.get(4));
				pst.setString(7, innList.get(5));
				pst.setString(8, innList.get(6));
				pst.setString(9, innList.get(7));
				pst.setString(10, innList.get(8));
				pst.setString(11, innList.get(9));
				pst.setString(12, innList.get(10));
				pst.setString(13, innList.get(11));
				pst.setInt(14, uF.parseToInt(strSessionEmpId));
				pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));
		//		System.out.println("pst appraisal_reviewee_details set ====> " + pst);
				pst.executeUpdate();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	
	private void getSectionData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
//			System.out.println("getExistID() =====>" + getExistID());
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getExistID()));
			rs = pst.executeQuery();
			List<List<String>> sectionList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("main_level_id"));//0
				innerList.add(rs.getString("level_title"));//1
				innerList.add(rs.getString("short_description"));//2
				innerList.add(rs.getString("long_description"));//3
				innerList.add(rs.getString("attribute_id"));//4
				innerList.add(rs.getString("section_weightage"));//5
				innerList.add(rs.getString("hr"));//6
				innerList.add(rs.getString("manager"));//7
				innerList.add(rs.getString("peer"));//8
				innerList.add(rs.getString("self"));//9
				innerList.add(rs.getString("subordinate"));//10
				innerList.add(rs.getString("grouphead"));//11
				innerList.add(rs.getString("vendor"));//12
				innerList.add(rs.getString("client"));//13
				innerList.add(rs.getString("ceo"));//14
				innerList.add(rs.getString("hod"));//15
				innerList.add(rs.getString("other_peer"));//16
				sectionList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("sectionList", sectionList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getSubsectionData() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
//			System.out.println("getExistID() =====>" + getExistID());
			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getExistID()));
			rs = pst.executeQuery();
//			List<List<String>> outerList1 = new ArrayList<List<String>>();
			Map<String, List<List<String>>> hmSubsectionData = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_level_id"));//0
				innerList.add(rs.getString("level_title"));//1
				innerList.add(rs.getString("short_description"));//2
				innerList.add(rs.getString("long_description"));//3
				innerList.add(rs.getString("attribute_id"));//4
				innerList.add(rs.getString("scorecard_type"));//5
				innerList.add(rs.getString("appraisal_system"));//6
				innerList.add(rs.getString("main_level_id"));//7
				innerList.add("");//8
				innerList.add("");//9
				innerList.add("");//10
				innerList.add("");//11
				innerList.add("");//12
				innerList.add("");//13
				innerList.add("");//14
				innerList.add("");//15
				innerList.add(rs.getString("subsection_weightage"));//16
				innerList.add("");//17
				innerList.add("");//18
				if (uF.parseToInt(rs.getString("appraisal_system")) == 1) {

					if (uF.parseToInt(rs.getString("scorecard_type")) == 1) {
						getDataObjectiveMeasureGoal(con, uF.parseToInt(rs.getString("appraisal_level_id")));
					} else if (uF.parseToInt(rs.getString("scorecard_type")) == 2) {
						getDataMeasure(con, uF.parseToInt(rs.getString("appraisal_level_id")));
					}else if (uF.parseToInt(rs.getString("scorecard_type"))== 3) {
					 getDataMeasureGoal(con, uF.parseToInt(rs.getString("appraisal_level_id")));
					 }
				}else if (uF.parseToInt(rs.getString("appraisal_system")) ==2) {
					getOtherData(con, uF, uF.parseToInt(rs.getString("appraisal_level_id")));
				 } else if (uF.parseToInt(rs.getString("appraisal_system")) ==3 || uF.parseToInt(rs.getString("appraisal_system")) == 4 || uF.parseToInt(rs.getString("appraisal_system")) == 5) {
					 getGoalKRATargetData(con, uF, uF.parseToInt(rs.getString("appraisal_level_id")), rs.getString("appraisal_system"));
				 }

				List<List<String>> outerList1 = hmSubsectionData.get(rs.getString("main_level_id"));
				if (outerList1 == null)outerList1 = new ArrayList<List<String>>();
				outerList1.add(innerList);
				hmSubsectionData.put(rs.getString("main_level_id"), outerList1);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSubsectionData", hmSubsectionData);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getGoalKRATargetData(Connection con, UtilityFunctions uF, int id, String appSystemType) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			pst = con.prepareStatement("select * from appraisal_question_details where appraisal_level_id=? and app_system_type=?");
			pst.setInt(1, id);
			pst.setInt(2, uF.parseToInt(appSystemType));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> goalKRATargetMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));//0
				innerList.add(rs.getString("question_id"));//1
				innerList.add(rs.getString("attribute_id"));//2
				innerList.add(rs.getString("weightage"));//3
				innerList.add(rs.getString("appraisal_id"));//4
//				innerList.add(rs.getString("plan_id"));
				innerList.add("");//5
				innerList.add("");//6
				innerList.add("");//7
				innerList.add("");//8
				innerList.add("");//9
				innerList.add("");//10
				innerList.add("");//11
				innerList.add("");//12
				innerList.add(rs.getString("appraisal_level_id"));//13
				innerList.add(rs.getString("goal_kra_target_id"));//14
				innerList.add(rs.getString("app_system_type"));//15
				innerList.add(rs.getString("kra_id"));//16
				innerList.add("");//17
				innerList.add("");//18
				
				List<List<String>> outerList = goalKRATargetMp.get(rs.getString("appraisal_level_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				goalKRATargetMp.put(rs.getString("appraisal_level_id"), outerList);
			}
			rs.close();
			pst.close();
			
			
			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(goalKRATargetMp);
			levelMp.put(id + "", list);
			
//			System.out.println("goalKRATarget goalKRATargetMp ====> "+goalKRATargetMp);
//			System.out.println("goalKRATarget levelMp ====> "+levelMp);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	private void getOtherData(Connection con, UtilityFunctions uF, int id) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement("select * from appraisal_other_question_type_details where level_id =?");
			pst.setInt(1, id);
			rs = pst.executeQuery();
			String othe_question_type_id = null;
			int i = 0;
			Map<String, List<List<String>>> otherMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				if (i == 0) {
					othe_question_type_id = rs.getString("othe_question_type_id");
				} else {
					othe_question_type_id += "," + rs.getString("othe_question_type_id");
				}
				i++;
//				othe_question_type_id,other_answer_type,other_question_type,is_weightage,appraisal_id,level_id
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("othe_question_type_id"));
				innerList.add(rs.getString("other_answer_type"));
				innerList.add(rs.getString("other_question_type"));
				innerList.add(rs.getString("is_weightage"));
				innerList.add(rs.getString("appraisal_id"));
				innerList.add(rs.getString("level_id"));

				List<List<String>> outerList = otherMp.get(rs.getString("level_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				otherMp.put(rs.getString("level_id"), outerList);

			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_question_details where other_id in(" + othe_question_type_id + ")");
			rs = pst.executeQuery();
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));//0
				innerList.add(rs.getString("question_id"));//1
				innerList.add(rs.getString("weightage"));//2
				innerList.add(rs.getString("appraisal_id"));//3
				innerList.add(rs.getString("plan_id"));//4
				innerList.add("");//5
				innerList.add("");//6
				innerList.add("");//7
				innerList.add("");//8
				innerList.add(rs.getString("measure_id"));//9
				innerList.add(rs.getString("attribute_id"));//10
				innerList.add(rs.getString("other_short_description"));//11
				innerList.add(rs.getString("appraisal_level_id"));//12
				innerList.add(rs.getString("scorecard_id"));//13
				innerList.add(rs.getString("other_id"));//14
				innerList.add("");//15
				innerList.add("");//16
				innerList.add("");//17
				innerList.add("");//18
				innerList.add("");//19
				innerList.add("");//20
				
				List<List<String>> outerList = questionMp.get(rs.getString("other_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				questionMp.put(rs.getString("other_id"), outerList);

			}
			rs.close();
			pst.close();
			
			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
			list.add(otherMp);
			list.add(questionMp);
			levelMp.put(id + "", list);
			
//			System.out.println("other levelMp====> "+levelMp);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void getDataMeasureGoal(Connection con, int id) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {

			pst = con.prepareStatement("select * from appraisal_scorecard_details where level_id =? and appraisal_id=?");
			pst.setInt(1, id);
			pst.setInt(2, uF.parseToInt(getExistID()));
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
				innerList.add(rs.getString("scorecard_description"));
				innerList.add(rs.getString("scorecard_weightage"));
				innerList.add(rs.getString("appraisal_attribute"));
				
				List<List<String>> outerList = scoreMp.get(rs.getString("level_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_goal_details where scorecard_id in(" + scorecard_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getExistID()));

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
				innerGoalList.add(rs.getString("goal_section_name"));
				innerGoalList.add(rs.getString("goal_description"));
				innerGoalList.add(rs.getString("goal_weightage"));

				List<List<String>> outerGoalList = GoalMp.get(rs.getString("scorecard_id"));
				if (outerGoalList == null)
					outerGoalList = new ArrayList<List<String>>();
				outerGoalList.add(innerGoalList);

				GoalMp.put(rs.getString("scorecard_id"), outerGoalList);

			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from appraisal_measure_details where goal_id in(" + goal_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getExistID()));

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
				innerList.add(rs.getString("measure_description"));
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
			pst.setInt(1, uF.parseToInt(getExistID()));

			rs = pst.executeQuery();

			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));//0
				innerList.add(rs.getString("question_id"));//1
				innerList.add(rs.getString("weightage"));//2
				innerList.add(rs.getString("appraisal_id"));//3
				innerList.add(rs.getString("plan_id"));//4
				innerList.add("");//5
				innerList.add("");//6
				innerList.add("");//7
				innerList.add("");//8
				innerList.add(rs.getString("measure_id"));//9
				innerList.add(rs.getString("attribute_id"));//10
				innerList.add(rs.getString("other_short_description"));//11
				innerList.add(rs.getString("appraisal_level_id"));//12
				innerList.add(rs.getString("scorecard_id"));//13
				innerList.add(rs.getString("other_id"));//14
				innerList.add("");//15
				innerList.add("");//16
				innerList.add("");//17
				innerList.add("");//18
				innerList.add("");//19
				innerList.add("");//20
				
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
		
	}

	
	private void getDataMeasure(Connection con, int id) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {

			pst = con.prepareStatement("select * from appraisal_scorecard_details where level_id =? and appraisal_id=?");
			pst.setInt(1, id);
			pst.setInt(2, uF.parseToInt(getExistID()));
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
				innerList.add(rs.getString("scorecard_description"));
				innerList.add(rs.getString("scorecard_weightage"));
				innerList.add(rs.getString("appraisal_attribute"));

				List<List<String>> outerList = scoreMp.get(rs.getString("level_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_measure_details where scorecard_id in(" + scorecard_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getExistID()));

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
				innerList.add(rs.getString("measure_description"));
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
			pst.setInt(1, uF.parseToInt(getExistID()));

			rs = pst.executeQuery();

			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));//0
				innerList.add(rs.getString("question_id"));//1
				innerList.add(rs.getString("weightage"));//2
				innerList.add(rs.getString("appraisal_id"));//3
				innerList.add(rs.getString("plan_id"));//4
				innerList.add("");//5
				innerList.add("");//6
				innerList.add("");//7
				innerList.add("");//8
				innerList.add(rs.getString("measure_id"));//9
				innerList.add(rs.getString("attribute_id"));//10
				innerList.add(rs.getString("other_short_description"));//11
				innerList.add(rs.getString("appraisal_level_id"));//12
				innerList.add(rs.getString("scorecard_id"));//13
				innerList.add(rs.getString("other_id"));//14
				innerList.add("");//15
				innerList.add("");//16
				innerList.add("");//17
				innerList.add("");//18
				innerList.add("");//19
				innerList.add("");//20
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
	}

	private void getDataObjectiveMeasureGoal(Connection con, int id) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			pst = con.prepareStatement("select * from appraisal_scorecard_details where level_id =? and appraisal_id=?");
			pst.setInt(1, id);
			pst.setInt(2, uF.parseToInt(getExistID()));
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
				innerList.add(rs.getString("scorecard_description"));
				innerList.add(rs.getString("scorecard_weightage"));
				innerList.add(rs.getString("appraisal_attribute"));

				List<List<String>> outerList = scoreMp.get(rs
						.getString("level_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				outerList.add(innerList);
				scoreMp.put(rs.getString("level_id"), outerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_goal_details where scorecard_id in(" + scorecard_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getExistID()));
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
				innerGoalList.add(rs.getString("goal_section_name"));
				innerGoalList.add(rs.getString("goal_description"));
				innerGoalList.add(rs.getString("goal_weightage"));

				List<List<String>> outerGoalList = GoalMp.get(rs
						.getString("scorecard_id"));
				if (outerGoalList == null)
					outerGoalList = new ArrayList<List<String>>();
				outerGoalList.add(innerGoalList);

				GoalMp.put(rs.getString("scorecard_id"), outerGoalList);

			}
			rs.close();
			pst.close();

			pst = con
					.prepareStatement("select * from appraisal_objective_details where goal_id in("
							+ goal_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getExistID()));
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
				innerList.add(rs.getString("objective_description"));
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
			pst.setInt(1, uF.parseToInt(getExistID()));
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
				innerList.add(rs.getString("measure_description"));
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
			
			pst = con.prepareStatement("select * from appraisal_question_details where measure_id in(" + measure_id + ") and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getExistID()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> questionMp = new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();

				innerList.add(rs.getString("appraisal_question_details_id"));//0
				innerList.add(rs.getString("question_id"));//1
				innerList.add(rs.getString("weightage"));//2
				innerList.add(rs.getString("appraisal_id"));//3
				innerList.add(rs.getString("plan_id"));//4
				innerList.add("");//5
				innerList.add("");//6
				innerList.add("");//7
				innerList.add("");//8
				innerList.add(rs.getString("measure_id"));//9
				innerList.add(rs.getString("attribute_id"));//10
				innerList.add(rs.getString("other_short_description"));//11
				innerList.add(rs.getString("appraisal_level_id"));//12
				innerList.add(rs.getString("scorecard_id"));//13
				innerList.add(rs.getString("other_id"));//14
				innerList.add("");//15
				innerList.add("");//16
				innerList.add("");//17
				innerList.add("");//18
				innerList.add("");//19
				innerList.add("");//20
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
//			System.out.println("levelMp====>" + levelMp);

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
	}

	
	public String getExistID() {
		return existID;
	}

	public void setExistID(String existID) {
		this.existID = existID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAppsystem() {
		return appsystem;
	}

	public void setAppsystem(String appsystem) {
		this.appsystem = appsystem;
	}

	public String getAppraisal_name() {
		return appraisal_name;
	}

	public void setAppraisal_name(String appraisal_name) {
		this.appraisal_name = appraisal_name;
	}

	public String getMyReviewStatus() {
		return myReviewStatus;
	}

	public void setMyReviewStatus(String myReviewStatus) {
		this.myReviewStatus = myReviewStatus;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}
}
