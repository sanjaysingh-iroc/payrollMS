package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

public class EditStaffAppraisalQueAnswer extends ActionSupport  implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1791355068993319712L;
	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeID;
	CommonFunctions CF;
	
	private String id;
	private String empID;
	private String userType;
	private String queID;
	private String queCnt;
	private String appFreqId;
	private String strQueOrSec;
	private String sectionId;
	
	private String areasOfStrength;
	private String areasOfImprovement;
	private String dataType;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strSessionUserTypeID = (String) session.getAttribute(USERTYPEID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
		request.setAttribute("hmFeatureStatus", hmFeatureStatus);
		String submit = request.getParameter("submit");
		if (submit == null) {
			if(getStrQueOrSec() != null && getStrQueOrSec().equalsIgnoreCase("AREAOFSI")) {
				getAreaOfStrengthAndImprovement(uF);
			} else if(getStrQueOrSec() != null && getStrQueOrSec().equalsIgnoreCase("SECTION")) {
				getLevelComment(uF);
			} else {
				getanswerTypeMap(uF);
				getAppraisalQuestionMap(uF);
				getQuestionsAnswer(uF);
		//===start parvez date: 18-07-2022===		
				if(getDataType()!=null && getDataType().equals("Reviewer Feedback")){
					getFeedBackDetails(uF);
				}
		//===end parvez date: 18-07-2022===		
			}
		return LOAD;
		
		} else {
			if(getStrQueOrSec() != null && getStrQueOrSec().equalsIgnoreCase("AREAOFSI")) {
				updateAreaOfStrengthAndImprovement(uF);
			} else if(getStrQueOrSec() != null && getStrQueOrSec().equalsIgnoreCase("SECTION")) {
				updateLevelComment(uF);
			} else {
				if(getDataType()!=null && getDataType().equals("Reviewer Feedback")){
					updateReviewerFeedBackDetails(uF);
				}else{
					getanswerTypeMap(uF);
					getAppraisalQuestionMap(uF);
					getQuestionsAnswer(uF);
					updateQuestionsAnswer(uF);
				}
//				getanswerTypeMap(uF);
//				getAppraisalQuestionMap(uF);
//				getQuestionsAnswer(uF);
//				updateQuestionsAnswer(uF);
			}
			return SUCCESS;
		}
	}
	
	
	private void updateAreaOfStrengthAndImprovement(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
			
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update reviewee_strength_improvements set areas_of_strength=?,areas_of_improvement=? where emp_id=? and " +
				"review_id=? and review_freq_id=? and user_id=? and user_type_id=?");
			pst.setString(1, getAreasOfStrength());
			pst.setString(2, getAreasOfImprovement());
			pst.setInt(3, uF.parseToInt(empID));
			pst.setInt(4, uF.parseToInt(id));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			pst.setInt(6, uF.parseToInt(strSessionEmpId));
			pst.setInt(7, uF.parseToInt(getUserType()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void updateLevelComment(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String levelComment = (String)request.getParameter("levelcomment"+getSectionId());
			
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update appraisal_question_answer set section_comment=? where section_id=? and emp_id=? and user_type_id=? and user_id=?");
			pst.setString(1, uF.showData(levelComment, "-"));
			pst.setInt(2, uF.parseToInt(getSectionId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	
	public String getAreaOfStrengthAndImprovement(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		// boolean flag = false;
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from reviewee_strength_improvements where emp_id=? and review_id=? and review_freq_id=? and user_id=? and user_type_id=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getUserType()));
			rs = pst.executeQuery();
			while (rs.next()) {
				request.setAttribute("areasOfStrength", uF.showData(rs.getString("areas_of_strength"), ""));
				request.setAttribute("areasOfImprovement", uF.showData(rs.getString("areas_of_improvement"), ""));
			}
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "success";
	}
	
	
	public String getLevelComment(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		// boolean flag = false;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmLevelDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_main_level_details where main_level_id in ("+getSectionId()+") order by main_level_id");
			rs = pst.executeQuery();
//			System.out.println(" pst : "+ pst);
			while(rs.next()) {
				hmLevelDetails.put(rs.getString("main_level_id")+"_TITLE", rs.getString("level_title"));
				hmLevelDetails.put(rs.getString("main_level_id")+"_SDESC", rs.getString("short_description"));
				hmLevelDetails.put(rs.getString("main_level_id")+"_LDESC", rs.getString("long_description"));
			} 
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_question_answer_id=?");
			pst.setInt(1, uF.parseToInt(getQueID()));
			rs = pst.executeQuery();
//			System.out.println(" pst : "+ pst);
			while(rs.next()) {
				hmLevelDetails.put(rs.getString("section_id")+"_COMMENT", rs.getString("section_comment"));
			}
			rs.close();
			pst.close();
				
			request.setAttribute("hmLevelDetails", hmLevelDetails);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "success";
	}
	

	private void updateQuestionsAnswer(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
		Map<String, String> innerMp = (Map<String, String>) request.getAttribute("innerMp");
		List<String> questioninnerList = hmQuestion.get(innerMp.get("QUESTION_ID"));
	//===start parvez date: 09-03-2023===
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
	//===end parvez date: 09-03-2023===	
		
		String givenAnswer = null;
		double marks = 0;
		String remark = null;
		String ansComment = null;
		if (uF.parseToInt(questioninnerList.get(8)) == 1) {
			ansComment = request.getParameter("anscomment"+questioninnerList.get(9));
			String[] correct = request.getParameterValues("correct"+questioninnerList.get(9));
			remark = request.getParameter("remark"+questioninnerList.get(9));
			String correctanswer = questioninnerList.get(6);
			for (int k = 0; correct != null && k < correct.length; k++) {
				if (k == 0) {
					givenAnswer = correct[k] + ",";
				} else {
					givenAnswer += correct[k] + ",";
				}
			}
			if (correctanswer != null && givenAnswer != null && givenAnswer.equals(correctanswer)) {
				marks = uF.parseToDouble(innerMp.get("WEIGHTAGE"));
			}

		} else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
			ansComment = request.getParameter("anscomment"+questioninnerList.get(9));
			String[] correct = request.getParameterValues("correct"+questioninnerList.get(9));
			for (int k = 0; correct != null && k < correct.length; k++) {
				if (k == 0) {
					givenAnswer = correct[k] + ",";
				} else {
					givenAnswer += correct[k] + ",";
				}
			}
			String correctanswer = questioninnerList.get(6);

			if (correctanswer != null && givenAnswer != null && givenAnswer.equals(correctanswer)) {
				marks = uF.parseToDouble(innerMp.get("WEIGHTAGE"));
			} else {
				marks = 0;
			}

		} else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
			ansComment = request.getParameter("anscomment" +questioninnerList.get(9));
			marks = uF.parseToDouble(request.getParameter("marks" +questioninnerList.get(9)));
			
		} else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
			ansComment = request.getParameter("anscomment"+questioninnerList.get(9));
			givenAnswer = request.getParameter("" + questioninnerList.get(9));
			marks = (uF.parseToDouble(givenAnswer) * uF.parseToDouble(innerMp.get("WEIGHTAGE"))) / 100;

		} else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
			ansComment = request.getParameter("anscomment" +questioninnerList.get(9));
			givenAnswer = request.getParameter(""+questioninnerList.get(9)) + ",";
			String answer = questioninnerList.get(6);
			if (givenAnswer != null && answer != null && answer.equals(givenAnswer)) {
				marks = uF.parseToDouble(innerMp.get("WEIGHTAGE"));
			}
			
		} else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
			ansComment = request.getParameter("anscomment"+questioninnerList.get(9));
			givenAnswer = request.getParameter(""+questioninnerList.get(9)) + ",";
			String answer = questioninnerList.get(6);
			if (givenAnswer != null && answer != null && answer.equals(givenAnswer)) {
				marks = uF.parseToDouble(innerMp.get("WEIGHTAGE"));
			}

		} else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
			ansComment = request.getParameter("anscomment"+questioninnerList.get(9));
			givenAnswer = request.getParameter(""+questioninnerList.get(9));
			marks = uF.parseToDouble(request.getParameter("marks"+questioninnerList.get(9)));

		} else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
			ansComment = request.getParameter("anscomment"+questioninnerList.get(9));
			givenAnswer = request.getParameter("correct"+questioninnerList.get(9)) + ",";
			String correctanswer = questioninnerList.get(6);
			if (givenAnswer != null && correctanswer != null && correctanswer.equals(givenAnswer)) {
				marks = uF.parseToDouble(innerMp.get("WEIGHTAGE"));
			}

		} else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
			ansComment = request.getParameter("anscomment"+questioninnerList.get(9));
			String[] correct = request.getParameterValues("correct"+questioninnerList.get(9));
			for (int k = 0; correct != null && k < correct.length; k++) {
				if (k == 0) {
					givenAnswer = correct[k] + ",";
				} else {
					givenAnswer += correct[k] + ",";
				}
			}
			String correctanswer = questioninnerList.get(6);
			if (correctanswer != null && givenAnswer != null && givenAnswer.equals(correctanswer)) {
				marks = uF.parseToDouble(innerMp.get("WEIGHTAGE"));
			}
		} else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
			ansComment = request.getParameter("anscomment"+questioninnerList.get(9));
			marks = uF.parseToDouble(request.getParameter("marks"+questioninnerList.get(9)));
			String a = request.getParameter("a"+questioninnerList.get(9));
			String b = request.getParameter("b"+questioninnerList.get(9));
			String c = request.getParameter("c"+questioninnerList.get(9));
			String d = request.getParameter("d"+questioninnerList.get(9));

			givenAnswer = uF.showData(a, "") + " :_:" + uF.showData(b, "") + " :_:" + uF.showData(c, "") + " :_: " + uF.showData(d, "");

		} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
			ansComment = request.getParameter("anscomment"+questioninnerList.get(9));
			String rating = request.getParameter("gradewithrating"+questioninnerList.get(9));
		//===start parvez date: 09-03-2023===	
//			marks = uF.parseToDouble(rating) * uF.parseToDouble(innerMp.get("WEIGHTAGE")) / 5;
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_TEN_STAR_RATING_FOR_REVIEW))){
				marks = uF.parseToDouble(rating) * uF.parseToDouble(innerMp.get("WEIGHTAGE")) / 10;
			} else{
				marks = uF.parseToDouble(rating) * uF.parseToDouble(innerMp.get("WEIGHTAGE")) / 5;
			}
		//===end parvez date: 09-03-2023===	

		} else if (uF.parseToInt(questioninnerList.get(8)) == 12) {
//			System.out.println("questioninnerList.get(9) ===> "+questioninnerList.get(9));
			ansComment = request.getParameter("anscomment"+questioninnerList.get(9));
//			System.out.println("ansComment ===> "+ansComment);
			givenAnswer = request.getParameter(""+questioninnerList.get(9));
//			System.out.println("givenAnswer ===> "+givenAnswer);
		} else if (uF.parseToInt(questioninnerList.get(8)) == 13) {
			ansComment = request.getParameter("anscomment" + questioninnerList.get(9));
			givenAnswer = request.getParameter("correct" + questioninnerList.get(9)) + ",";
			String gvnAnswer = request.getParameter("correct" + questioninnerList.get(9));
//			String correctanswer = questioninnerList.get(6);
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
//			if (givenAnswer != null && correctanswer != null && givenAnswer.equals(correctanswer)) {
			marks = uF.parseToDouble(correctAnsVal) * uF.parseToDouble(innerMp.get("WEIGHTAGE")) / 5;
//			}
		}
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update appraisal_question_answer set answer=?, remark=?, marks=?," +
				" answers_comment=? where appraisal_question_answer_id=?");
			pst.setString(1, givenAnswer);
			pst.setString(2, remark);
			pst.setDouble(3, marks);
			pst.setString(4, ansComment);
			pst.setInt(5, uF.parseToInt(getQueID()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	public void getQuestionsAnswer(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_question_answer_id=?");
			pst.setInt(1, uF.parseToInt(getQueID()));
			rs = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			Map<String, String> innerMp = new HashMap<String, String>();
			while (rs.next()) {
				innerMp.put("ANSWER", rs.getString("answer"));
				innerMp.put("REMARK", rs.getString("remark"));
				innerMp.put("MARKS", rs.getString("marks"));
				innerMp.put("APP_QUE_ANS_ID", rs.getString("appraisal_question_answer_id"));
				innerMp.put("WEIGHTAGE", rs.getString("weightage"));
				innerMp.put("ANSWERCOMMENT", rs.getString("answers_comment"));
				innerMp.put("QUESTION_ID", rs.getString("question_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("innerMp", innerMp);
//			System.out.println("innerMp ===> " + innerMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
//===start parvez date: 18-07-2022===
	public void getFeedBackDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);

			Map<String, String> hmUsersFeedbackDetails = new HashMap<String, String>();
			
			double weightage = 0;
			pst = con.prepareStatement("select section_weightage from appraisal_main_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while(rs.next()){
				weightage += uF.parseToDouble(rs.getString("section_weightage")); 
			}
			rs.close();
			pst.close();
			hmUsersFeedbackDetails.put("WEIGHTAGE", weightage+"");
			
			pst = con.prepareStatement("select * from reviewer_feedback_details where appraisal_id=? and appraisal_freq_id=? and emp_id=? and user_id=? and user_type_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getUserType()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> innerMap = new HashMap<String, String>();
				hmUsersFeedbackDetails.put("MARKS", rs.getString("reviewer_marks"));
				hmUsersFeedbackDetails.put("COMMENT", rs.getString("reviewer_comment"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmUsersFeedbackDetails", hmUsersFeedbackDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void updateReviewerFeedBackDetails(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			String levelComment = request.getParameter("anscomment");
			String rating = request.getParameter("gradewithrating1");
			
			double weightage = 0;
			pst = con.prepareStatement("select * from appraisal_main_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while(rs.next()){
				weightage += uF.parseToDouble(rs.getString("section_weightage")); 
			}
			rs.close();
			pst.close();
			double marks = uF.parseToDouble(rating) * weightage / 5;
			
			pst = con.prepareStatement("update reviewer_feedback_details set reviewer_marks=?,reviewer_comment=? where emp_id=? and appraisal_id=? and user_id=? and user_type_id=? and appraisal_freq_id=?");
			pst.setDouble(1, marks);
			pst.setString(2, levelComment);
			pst.setInt(3, uF.parseToInt(getEmpID()));
			pst.setInt(4, uF.parseToInt(getId()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setInt(6, uF.parseToInt(getUserType()));
			pst.setInt(7, uF.parseToInt(getAppFreqId()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
//===end parvez date: 18-07-2022===	
	
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

	
	public void getAppraisalQuestionMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<String>> hmQuestion = new HashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from question_bank qb, appraisal_question_answer aqa where qb.question_bank_id=aqa.question_id and aqa.appraisal_question_answer_id=?");
			pst = con.prepareStatement("select qb.*,aqa.*,aqd.other_short_description from question_bank qb, appraisal_question_answer aqa,appraisal_question_details aqd " +
					" where qb.question_bank_id=aqa.question_id and aqa.question_id=aqd.question_id and aqa.appraisal_question_answer_id=?");
			pst.setInt(1, uF.parseToInt(getQueID()));
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
				innerList.add(rs.getString("appraisal_question_answer_id"));
				innerList.add(rs.getString("option_e")); //10
				innerList.add(rs.getString("rate_option_a")); //11
				innerList.add(rs.getString("rate_option_b")); //12
				innerList.add(rs.getString("rate_option_c")); //13
				innerList.add(rs.getString("rate_option_d")); //14
				innerList.add(rs.getString("rate_option_e")); //15
				innerList.add(rs.getString("weightage")); //16
				innerList.add(rs.getString("other_short_description")); //17
				
				outerList.add(innerList);
				hmQuestion.put(rs.getString("question_bank_id"), innerList);
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
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("score"));
				innerList.add(rs.getString("score_label"));
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


	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getQueID() {
		return queID;
	}

	public void setQueID(String queID) {
		this.queID = queID;
	}

	public String getQueCnt() {
		return queCnt;
	}

	public void setQueCnt(String queCnt) {
		this.queCnt = queCnt;
	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}

	public String getStrQueOrSec() {
		return strQueOrSec;
	}

	public void setStrQueOrSec(String strQueOrSec) {
		this.strQueOrSec = strQueOrSec;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getAreasOfStrength() {
		return areasOfStrength;
	}

	public void setAreasOfStrength(String areasOfStrength) {
		this.areasOfStrength = areasOfStrength;
	}

	public String getAreasOfImprovement() {
		return areasOfImprovement;
	}

	public void setAreasOfImprovement(String areasOfImprovement) {
		this.areasOfImprovement = areasOfImprovement;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
//===start parvez date: 18-07-2022===
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
//===end parvez date: 18-07-2022===
}
