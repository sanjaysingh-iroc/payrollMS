package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class StaffAppraisalCorrection implements ServletRequestAware, IStatements, Runnable {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strBaseUserTypeID;
	String strUserType = null;
	CommonFunctions CF;
	private String id;
	private String empID;
	private String currentLevel;
	private String role;
	private String userType;
	private String levelCount;
	private String appFreqId;
	private String userId;

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserTypeID = (String) session.getAttribute(BASEUSERTYPEID);
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		strUserType = (String) session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, "/jsp/performance/StaffAppraisalCorrection.jsp");
		request.setAttribute(TITLE, "Review Form");

		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		UtilityFunctions uF = new UtilityFunctions();
		getanswerTypeMap(uF);
		getLevelStep(uF);
		getMainLevelStep(uF);
		getQuestionSubType(uF);
		getAppraisalDetail(uF);
		getEmployyDetailsList(uF);
		getAppraisalQuestionMap(uF);

//		System.out.println("levelCount ===>> " + levelCount);
		if (levelCount == null || levelCount.equalsIgnoreCase("null")) {
			levelCount = "1";
		} else {
			int cnt = uF.parseToInt(levelCount);
			cnt++;
			setLevelCount("" + cnt);
		}
//		 System.out.println("getUserId() ===> " + getUserId());
//		 System.out.println("levelCount ===>> " + levelCount);
//		 System.out.println("getCurrentLevel() ===> "+getCurrentLevel());
		String submit = request.getParameter("submit");
		String btnfinish = request.getParameter("btnfinish");
		String levelAppSystem = request.getParameter("levelAppSystem");
//		 System.out.println("submit ========= >> "+submit);
//		 System.out.println("btnfinish ========= >> "+btnfinish);
		if (submit == null && btnfinish == null) {
			getLevelStatus(uF);
			checkCurrentLevelExistForCurrentEmp(uF);
//			getFinalResult();
			return getLevelQuestion(uF);

		} else {
			getPreviousLevelData(uF);
			if (levelAppSystem != null) {
				insertMarks(uF);
			}
			if (uF.parseToInt(levelCount) > 2) {
//				System.out.println("btnfinish =========>> " + btnfinish);
				if (swapLevelId(uF)) {
					// String btnSubmit = request.getParameter("btnfinish");
//					System.out.println("btnfinish 1 =========>> " + btnfinish);
					if (btnfinish != null && btnfinish.equals("Finish")) {
						String strDomain = request.getServerName().split("\\.")[0];
						setDomain(strDomain);
						Thread th = new Thread(this);
						th.start();
//						System.out.println("btnfinish 2 =========>> " + btnfinish);
						return "finish";
					} else {
						return "update";
					}
				}
			}
//			 System.out.println("levelCount ===> "+levelCount);
//			 System.out.println("getCurrentLevel() ===> "+getCurrentLevel());
			getLevelStatus(uF);
			checkCurrentLevelExistForCurrentEmp(uF);
//			getFinalResult();
			return getLevelQuestion(uF);
		}
	}
	


	public boolean swapLevelId(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = true;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select main_level_id from appraisal_main_level_details where appraisal_id=? and main_level_id>? order by main_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getCurrentLevel()));
			rs = pst.executeQuery();
			// System.out.println("pst ====> "+pst);
			while (rs.next()) {
				flag = false;
				setCurrentLevel(rs.getString("main_level_id"));
				break;
			}
			rs.close();
			pst.close();
			// System.out.println("flag ====> "+flag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}

	
	public void getCurrentLevelAnswer(UtilityFunctions uF, int level) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_question_answer where section_id=? and appraisal_id=? and emp_id=? and " +
				"user_id=? and user_type_id=? and appraisal_freq_id=? and reviewer_or_appraiser=?");
			pst.setInt(1, level);
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			pst.setInt(4, uF.parseToInt(getUserId()));
			pst.setInt(5, uF.parseToInt(getUserType()));
			pst.setInt(6, uF.parseToInt(getAppFreqId()));
			pst.setInt(7, 0);
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> questionanswerMp = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				Map<String, String> innerMp = new HashMap<String, String>();
				innerMp.put("ANSWER", rs.getString("answer"));
				innerMp.put("REMARK", rs.getString("remark"));
				innerMp.put("MARKS", rs.getString("marks"));
				innerMp.put("APP_QUE_ANS_ID", rs.getString("appraisal_question_answer_id"));
				innerMp.put("WEIGHTAGE", rs.getString("weightage"));
				innerMp.put("ANSWERCOMMENT", rs.getString("answers_comment"));
				if (uF.parseToInt(rs.getString("scorecard_id")) != 0) {
					questionanswerMp.put(rs.getString("scorecard_id") + "question" + rs.getString("question_id"), innerMp);
				} else {
					questionanswerMp.put(rs.getString("other_id") + "question" + rs.getString("question_id"), innerMp);
				}
			}
			rs.close();
			pst.close();
//			System.out.println("questionanswerMp ===>> " + questionanswerMp);
			request.setAttribute("questionanswerMp", questionanswerMp);
			
			
			pst = con.prepareStatement("select * from appraisal_question_answer where section_id=? and appraisal_id=? and emp_id=? and " +
				"reviewer_id=? and reviewer_user_type_id=? and appraisal_freq_id=? and reviewer_or_appraiser=? and user_type_id=?");
			pst.setInt(1, level);
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(strBaseUserTypeID));
			pst.setInt(6, uF.parseToInt(getAppFreqId()));
			pst.setInt(7, 0);
			pst.setInt(8, uF.parseToInt(getUserType()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> questionanswerMpReviewer = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				Map<String, String> innerMp = new HashMap<String, String>();
				innerMp.put("ANSWER", rs.getString("reviewer_answer"));
				innerMp.put("REMARK", rs.getString("reviewer_remark"));
				innerMp.put("MARKS", rs.getString("reviewer_marks"));
				innerMp.put("APP_QUE_ANS_ID", rs.getString("appraisal_question_answer_id"));
				innerMp.put("WEIGHTAGE", rs.getString("weightage"));
				innerMp.put("ANSWERCOMMENT", rs.getString("reviewer_answers_comment"));
				if (uF.parseToInt(rs.getString("scorecard_id")) != 0) {
					questionanswerMpReviewer.put(rs.getString("scorecard_id") + "question" + rs.getString("question_id"), innerMp);
				} else {
					questionanswerMpReviewer.put(rs.getString("other_id") + "question" + rs.getString("question_id"), innerMp);
				}
			}
			rs.close();
			pst.close();
//			System.out.println("questionanswerMpReviewer ===>> " + questionanswerMpReviewer);
			request.setAttribute("questionanswerMpReviewer", questionanswerMpReviewer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	

	public void getLevelStatus(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select section_id from appraisal_question_answer where appraisal_id=? and emp_id=? and user_id=? " +
				"and user_type_id=? and appraisal_freq_id=? and reviewer_or_appraiser=? group by section_id order by section_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(getUserId()));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			pst.setInt(6, 0);
			rs = pst.executeQuery();
			Map<String, String> innerMp = new HashMap<String, String>();
			while (rs.next()) {
				innerMp.put(rs.getString("section_id"), rs.getString("section_id"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select appraisal_level_id from kra_rating_details where appraisal_id=? and emp_id=? and added_by=? and user_type_id=? and appraisal_freq_id=?  group by appraisal_level_id order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(getUserId()));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				innerMp.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select appraisal_level_id from target_details where appraisal_id=? and emp_id=? and added_by=? and user_type_id=? and appraisal_freq_id = ? group by appraisal_level_id order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(getUserId()));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				innerMp.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("LEVEL_STATUS", innerMp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void getMainLevelStep(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			List<String> mainLevelList = new ArrayList<String>();
			pst = con.prepareStatement("select main_level_id from appraisal_main_level_details where appraisal_id=? order by main_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				mainLevelList.add(rs.getString("main_level_id"));
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
	

	public void getLevelStep(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmSubsection = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				Map<String, String> hmSubsectionDetails = new HashMap<String, String>();
				hmSubsectionDetails.put("LEVEL_NAME", rs.getString("level_title"));
				hmSubsectionDetails.put("LEVEL_SDESC", rs.getString("short_description"));
				hmSubsectionDetails.put("LEVEL_LDESC", rs.getString("long_description"));
				hmSubsectionDetails.put("LEVEL_APPSYSTEM", rs.getString("appraisal_system"));
				hmSubsectionDetails.put("APP_LEVEL_ID", rs.getString("appraisal_level_id"));
				hmSubsection.put(rs.getString("appraisal_level_id"), hmSubsectionDetails);
			}
			rs.close();
			pst.close();

			request.setAttribute("hmSubsection", hmSubsection);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public boolean getPreviousLevelData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);
//			System.out.println("in getPreviousLevelData ... ");
			StringBuilder sb = new StringBuilder("select * from appraisal_question_details where appraisal_id=? ");
			/*if(getRole() == null || !getRole().equalsIgnoreCase("Reviewer")) {
				if (uF.parseToInt(getUserType()) == 2) {
					sb.append(" and manager !=0 ");
				} else if (uF.parseToInt(getUserType()) == 7) {
					sb.append(" and hr !=0 ");
				} else if (uF.parseToInt(getUserType()) == 3) {
					sb.append(" and self !=0 ");
				} else if (uF.parseToInt(getUserType()) == 4) {
					sb.append(" and peer !=0 ");
				} else if (uF.parseToInt(getUserType()) == 5) {
					sb.append(" and ceo !=0 ");
				} else if (uF.parseToInt(getUserType()) == 13) {
					sb.append(" and hod !=0 ");
				}
			}*/
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(getId()));
//			pst.setInt(2, uF.parseToInt(getCurrentLevel()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmAppraisalQuestion = new HashMap<String, List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add("");
				innerList.add(rs.getString("appraisal_id"));
				innerList.add(rs.getString("plan_id"));
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("other_short_description"));
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("other_id"));
				innerList.add("");
				innerList.add("");
				hmAppraisalQuestion.put(rs.getString("appraisal_question_details_id"), innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAppraisalQuestion", hmAppraisalQuestion);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}
	

	private void insertMarks(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		Map<String, List<String>> hmAppraisalQuestion = (Map<String, List<String>>) request.getAttribute("hmAppraisalQuestion");
		Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
		// System.out.println("hmLevelQuestion in insert =============>> " + hmLevelQuestion);
		try {
			con = db.makeConnection(con);
			
			List<String> alAppQueAnsId = new ArrayList<String>();
			String appQueAnsIds = request.getParameter("appQueAnsIds");
			if(appQueAnsIds != null && appQueAnsIds.length()> 0) {
				alAppQueAnsId = Arrays.asList(appQueAnsIds.split("::::"));
			}
//			System.out.println("appQueAnsIds ===>> " + appQueAnsIds);
//			System.out.println("alAppQueAnsId ===>> " + alAppQueAnsId);

			for (int i = 0; alAppQueAnsId != null && i < alAppQueAnsId.size(); i++) {
				String queAnsId = alAppQueAnsId.get(i);
				List<String> alAppraisalData = getQuestionIdFromAppraisal(con, uF, queAnsId);
				String queBankQueId = alAppraisalData.get(0);
				String appraisalQueId = alAppraisalData.get(1);
				
				List<String> questioninnerList = hmQuestion.get(queBankQueId);
				List<String> innerList = hmAppraisalQuestion.get(appraisalQueId);
				
				String weightage = innerList.get(2);
//				String appraisal_level_id = innerList.get(13);
//				String scorecard_id = innerList.get(14);
//				String attribute = innerList.get(11);
				String givenAnswer = null;
				double marks = 0;
				String remark = null;
				String ansComment = null;
				if (uF.parseToInt(questioninnerList.get(8)) == 1) {
					ansComment = request.getParameter("anscomment" + queAnsId);
					String[] correct = request.getParameterValues("correct" + queAnsId);
					remark = request.getParameter("" + queAnsId);
					String correctanswer = questioninnerList.get(6);
					for (int k = 0; correct != null && k < correct.length; k++) {
						if (k == 0) {
							givenAnswer = correct[k] + ",";
						} else {
							givenAnswer += correct[k] + ",";
						}
					}
					if (correctanswer != null && givenAnswer != null && givenAnswer.equals(correctanswer)) {
						marks = uF.parseToDouble(weightage);
					}

				} else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
					ansComment = request.getParameter("anscomment" + queAnsId);
					String[] correct = request.getParameterValues("correct" + queAnsId);
					for (int k = 0; correct != null && k < correct.length; k++) {
						if (k == 0) {
							givenAnswer = correct[k] + ",";
						} else {
							givenAnswer += correct[k] + ",";
						}
					}
					String correctanswer = questioninnerList.get(6);

					if (correctanswer != null && givenAnswer != null && givenAnswer.equals(correctanswer)) {
						marks = uF.parseToDouble(weightage);
					} else {
						marks = 0;
					}

				} else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
					ansComment = request.getParameter("anscomment" + queAnsId);
					marks = uF.parseToDouble(request.getParameter("marks" + queAnsId));

				} else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
					ansComment = request.getParameter("anscomment" + queAnsId);
					givenAnswer = request.getParameter("" + queAnsId);
					marks = (uF.parseToDouble(givenAnswer) * uF.parseToDouble(weightage)) / 100;

				} else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
					ansComment = request.getParameter("anscomment" + queAnsId);
					givenAnswer = request.getParameter("" + queAnsId) + ",";
					String answer = questioninnerList.get(6);
					if (givenAnswer != null && answer != null && givenAnswer.equals(answer)) {
						marks = uF.parseToDouble(weightage);
					}
				} else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
					ansComment = request.getParameter("anscomment" + queAnsId);
					givenAnswer = request.getParameter("" + queAnsId) + ",";
					String answer = questioninnerList.get(6);
					if (givenAnswer != null && answer != null && givenAnswer.equals(answer)) {
						marks = uF.parseToDouble(weightage);
					}
				} else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
					ansComment = request.getParameter("anscomment" + queAnsId);
					givenAnswer = request.getParameter("" + queAnsId);
					marks = uF.parseToDouble(request.getParameter("marks" + queAnsId));
					weightage = request.getParameter("outofmarks" + queAnsId);
				} else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
					ansComment = request.getParameter("anscomment" + queAnsId);
					givenAnswer = request.getParameter("correct" + queAnsId) + ",";
					String correctanswer = questioninnerList.get(6);
					if (givenAnswer != null && correctanswer != null && givenAnswer.equals(correctanswer)) {
						marks = uF.parseToDouble(weightage);
					}
				} else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
					ansComment = request.getParameter("anscomment" + queAnsId);
					String[] correct = request.getParameterValues("correct" + queAnsId);
					for (int k = 0; correct != null && k < correct.length; k++) {
						if (k == 0) {
							givenAnswer = correct[k] + ",";
						} else {
							givenAnswer += correct[k] + ",";
						}
					}
					String correctanswer = questioninnerList.get(6);
					if (correctanswer != null && givenAnswer != null && givenAnswer.equals(correctanswer)) {
						marks = uF.parseToDouble(weightage);
					}
				} else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
					ansComment = request.getParameter("anscomment" + queAnsId);
					marks = uF.parseToDouble(request.getParameter("marks" + queAnsId));
					String a = request.getParameter("a" + queAnsId);
					String b = request.getParameter("b" + queAnsId);
					String c = request.getParameter("c" + queAnsId);
					String d = request.getParameter("d" + queAnsId);
					givenAnswer = uF.showData(a, "") + ":_:" + uF.showData(b, "") + ":_:" + uF.showData(c, "") + ":_:" + uF.showData(d, "");
				} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
					ansComment = request.getParameter("anscomment" + queAnsId);
					String rating = request.getParameter("gradewithrating" + queAnsId);
					marks = uF.parseToDouble(rating) * uF.parseToDouble(weightage) / 5;
				} else if (uF.parseToInt(questioninnerList.get(8)) == 12) {
					ansComment = request.getParameter("anscomment" + queAnsId);
					givenAnswer = request.getParameter("" + queAnsId);
				} else if (uF.parseToInt(questioninnerList.get(8)) == 13) {
					ansComment = request.getParameter("anscomment" + queAnsId);
					givenAnswer = request.getParameter("correct" + queAnsId) + ",";
					String gvnAnswer = request.getParameter("correct" + queAnsId);
//					String correctanswer = questioninnerList.get(6);
					String correctAnsVal = null;
					if(gvnAnswer != null && gvnAnswer.trim().equalsIgnoreCase("a")) {
						correctAnsVal = questioninnerList.get(11);
					} else if(gvnAnswer != null && gvnAnswer.trim().equalsIgnoreCase("b")) {
						correctAnsVal = questioninnerList.get(12);
					} else if(gvnAnswer != null && gvnAnswer.trim().equalsIgnoreCase("c")) {
						correctAnsVal = questioninnerList.get(13);
					} else if(gvnAnswer != null && gvnAnswer.trim().equalsIgnoreCase("d")) {
						correctAnsVal = questioninnerList.get(14);
					} else if(gvnAnswer != null && gvnAnswer.trim().equalsIgnoreCase("e")) {
						correctAnsVal = questioninnerList.get(15);
					}
//					if (givenAnswer != null && correctanswer != null && givenAnswer.equals(correctanswer)) {
						marks = uF.parseToDouble(correctAnsVal) * uF.parseToDouble(weightage) / 5;
//					}
				}

//					System.out.println("givenAnswer=====>"+givenAnswer+"==ansComment==>"+ansComment);
				pst = con.prepareStatement("update appraisal_question_answer set reviewer_answer=?, reviewer_id=?, reviewer_user_type_id=?, reviewer_attempted_on=?, " +
					"reviewer_marks=?, reviewer_remark=?, reviewer_answers_comment=? where appraisal_question_answer_id=? ");
				pst.setString(1, givenAnswer);
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setInt(3, uF.parseToInt(strBaseUserTypeID));
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDouble(5, marks);
				pst.setString(6, remark);
				pst.setString(7, ansComment);
				pst.setInt(8, uF.parseToInt(queAnsId));
//					System.out.println("pst2 =====>"+ pst);
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



	private List<String> getQuestionIdFromAppraisal(Connection con, UtilityFunctions uF, String queAnsId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> alAppraisalData = new ArrayList<String>();
		try {
			pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_question_answer_id=?");
			pst.setInt(1, uF.parseToInt(queAnsId));
			rs = pst.executeQuery();
			while (rs.next()) {
				alAppraisalData.add(rs.getString("question_id"));
				alAppraisalData.add(rs.getString("appraisal_question_details_id"));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return alAppraisalData;
	}


	public void getAppraisalDetail(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			List<String> appraisalList = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_name"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_description"), ""));
				appraisalList.add(uF.showData(rs.getString("oriented_type"), ""));
				appraisalList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalList.add(uF.showData(rs.getString("appraisal_instruction"), ""));
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
	
	

	private void getEmployyDetailsList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			Map<String, String> hmDepartmentMap = CF.getDeptMap(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmOrientationMember = getOrientationMember(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			rs = pst.executeQuery();
			List<String> empList = new ArrayList<String>();
			Map<String, String> hmEmpDetails = new HashMap<String, String>();
			while (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpDetails.put("EMP_NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hmEmpDetails.put("EMP_CODE", rs.getString("empcode"));
				hmEmpDetails.put("EMP_ID", rs.getString("emp_per_id"));
				hmEmpDetails.put("DESIGNATION", uF.showData(hmEmpCodeDesig.get(rs.getString("emp_per_id")), ""));
				hmEmpDetails.put("LEVEL", uF.showData(hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_per_id"))), ""));

				hmEmpDetails.put("DEAPRTMENT", uF.showData(hmDepartmentMap.get(rs.getString("depart_id")), ""));
				hmEmpDetails.put("JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();

			if(getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
				hmEmpDetails.put("ORIENTATION", "Reviewer");
			} else {
				hmEmpDetails.put("ORIENTATION", hmOrientationMember.get(getUserType()));
			}
			request.setAttribute("hmEmpDetails", hmEmpDetails);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public String getLevelQuestion(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmLevelName = new HashMap<String, String>();
			Map<String, String> hmOrientMemberID = getOrientMemberID();
			int sectionCnt = 0;
			if (uF.parseToInt(currentLevel) == 0) {
				pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=? order by main_level_id");
			} else {
				pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=?  and main_level_id=? order by main_level_id");
				pst.setInt(2, uF.parseToInt(currentLevel));
			}
			pst.setInt(1, uF.parseToInt(id));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			String appraisal_level_id = null;
			while (rs.next()) {
				sectionCnt++;
				appraisal_level_id = rs.getString("main_level_id");
				hmLevelName.put("LEVEL_NAME", rs.getString("level_title"));
				hmLevelName.put("LEVEL_SDESC", rs.getString("short_description"));
				hmLevelName.put("LEVEL_LDESC", rs.getString("long_description"));
				hmLevelName.put("LEVEL_COUNT", sectionCnt + "");
				hmLevelName.put("APP_LEVEL_ID", rs.getString("main_level_id"));
				break;
			}
			rs.close();
			pst.close();
//			System.out.println("appraisal_level_id ===>> " + appraisal_level_id);
			if (appraisal_level_id == null) {
				return "update";
			}
			request.setAttribute("hmLevelName", hmLevelName);

			setCurrentLevel(appraisal_level_id);
			getCurrentLevelAnswer(uF, uF.parseToInt(currentLevel));

			StringBuilder sb = new StringBuilder("select * from appraisal_question_details where appraisal_id=? and appraisal_level_id in (" +
				"select appraisal_level_id from  appraisal_level_details where  main_level_id=?)");
			/*if(getRole() == null || !getRole().equalsIgnoreCase("Reviewer")) {
				if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get(MANAGER))) {
					sb.append(" and manager !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HR"))) {
					sb.append(" and hr !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Self"))) {
					sb.append(" and self !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Peer"))) {
					sb.append(" and peer !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("CEO"))) {
					sb.append(" and ceo !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HOD"))) {
					sb.append(" and hod !=0 ");
				}
			}*/
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(id));
			pst.setInt(2, uF.parseToInt(currentLevel));
			rs = pst.executeQuery();
	//		System.out.println("Get Questions LevelWise pst ===> "+pst);
			Map<String, List<List<String>>> hmLevelQuestion = new LinkedHashMap<String, List<List<String>>>();
			// List<List<String>> outerList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<List<String>> outerList = hmLevelQuestion.get(rs.getString("appraisal_level_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add("");
				innerList.add(rs.getString("appraisal_id"));
				innerList.add(rs.getString("plan_id"));
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("other_short_description"));
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("other_id"));
				innerList.add("");
				innerList.add("");
				outerList.add(innerList);
				hmLevelQuestion.put(rs.getString("appraisal_level_id"), outerList);
			}
			rs.close();
			pst.close();
	
			// request.setAttribute("questionList", outerList);
			// System.out.println("hmLevelQuestion ===>"+hmLevelQuestion);
			request.setAttribute("hmLevelQuestion", hmLevelQuestion);
			// System.out.println("currentLevel===>"+currentLevel);
			// System.out.println("getUserType()===>"+getUserType());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "success";
	}

	
	private Map<String, String> getOrientMemberID() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from orientation_member where status = true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_name"), rs.getString("member_id"));
			}
			rs.close();
			pst.close();
			// System.out.println("memberid=====>"+orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return orientationMemberMp;
	}

	
	public Map<String, String> getAppraisalQuestionMap(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> AppraisalQuestion = new HashMap<String, String>();
		Map<String, List<String>> hmQuestion = new HashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from question_bank qb, appraisal_question_details aqd where qb.question_bank_id = aqd.question_id and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
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
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("option_e")); //10
				innerList.add(rs.getString("rate_option_a")); //11
				innerList.add(rs.getString("rate_option_b")); //12
				innerList.add(rs.getString("rate_option_c")); //13
				innerList.add(rs.getString("rate_option_d")); //14
				innerList.add(rs.getString("rate_option_e")); //15
				outerList.add(innerList);
				hmQuestion.put(rs.getString("question_bank_id"), innerList);
				AppraisalQuestion.put(rs.getString("question_bank_id"), rs.getString("question_text"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmQuestion", hmQuestion);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return AppraisalQuestion;
	}

	
	public void getanswerTypeMap(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<List<String>>> hmQuestionanswerType = new HashMap<String, List<List<String>>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type_sub ");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> outerList = hmQuestionanswerType.get(rs.getString("answer_type_id"));
				if (outerList == null)
					outerList = new LinkedList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("score_label"));
				innerList.add(rs.getString("score"));
				outerList.add(innerList);
				hmQuestionanswerType.put(rs.getString("answer_type_id"), outerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmQuestionanswerType", hmQuestionanswerType);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public Map<String, String> getLevelMap(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmLevelMap;
	}

	
	private void getQuestionSubType(UtilityFunctions uF) {
		Map<String, List<List<String>>> answertypeSub = new HashMap<String, List<List<String>>>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type_sub order by appraisal_answer_type_sub_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				List<List<String>> outerList = answertypeSub.get(rst.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("score"));
				innerList.add(rst.getString("score_label"));
				outerList.add(innerList);
				answertypeSub.put(rst.getString("answer_type_id"), outerList);
			}
			rst.close();
			pst.close();
			request.setAttribute("answertypeSub", answertypeSub);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private void checkCurrentLevelExistForCurrentEmp(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			String sectionCount = "0";
			pst = con.prepareStatement("select count(distinct(section_id)) as section_id from appraisal_question_answer where emp_id=? and " +
				"appraisal_id=? and user_type_id=? and user_id=? and appraisal_freq_id=? and reviewer_or_appraiser=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getUserType()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			if(getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
				pst.setInt(6, 1);
			} else {
				pst.setInt(6, 0);
			}
			rst = pst.executeQuery();
			// System.out.println("pst === > "+pst);
			while (rst.next()) {
				sectionCount = rst.getString("section_id");
			}
			rst.close();
			pst.close();
			// System.out.println("sectionCount ===> " + sectionCount);
			request.setAttribute("sectionCount", sectionCount);
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
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return orientationMemberMp;
	}

	
	private Map<String, String> getReviewDetails(Connection con) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> hmReviewData = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rst = pst.executeQuery();
			while (rst.next()) {
				hmReviewData.put("REVIEW_NAME", rst.getString("appraisal_name"));
				hmReviewData.put("REVIEW_SELFID", rst.getString("self_ids"));
				hmReviewData.put("REVIEW_PEERID", rst.getString("peer_ids"));
				hmReviewData.put("REVIEW_MANAGERID", rst.getString("supervisor_id"));
				hmReviewData.put("REVIEW_HRID", rst.getString("hr_ids"));
				hmReviewData.put("REVIEW_OTHERID", rst.getString("other_ids"));
				hmReviewData.put("REVIEW_STARTDATE", rst.getString("from_date"));
				hmReviewData.put("REVIEW_ENDDATE", rst.getString("to_date"));
				hmReviewData.put("REVIEW_CEOID", rst.getString("ceo_ids"));
				hmReviewData.put("REVIEW_HODID", rst.getString("hod_ids"));
				// hmReviewData.put("REVIEW_ENDDATE", rst.getString(""));
			}
			rst.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hmReviewData;
	}
	

	@Override
	public void run() {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		Map<String, String> hmReviewData = getReviewDetails(con);
		Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);

		db.closeConnection(con);
	}

	
	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(String currentLevel) {
		this.currentLevel = currentLevel;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getLevelCount() {
		return levelCount;
	}

	public void setLevelCount(String levelCount) {
		this.levelCount = levelCount;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain = strDomain;
	}

	public String getStrDomain() {
		return strDomain;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

}

