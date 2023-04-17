package com.konnect.jpms.training;

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

public class EditTakeAssessmentQueAnswer extends ActionSupport  implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1791355068993319712L;
	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeID;
	CommonFunctions CF;
	private String assessmentId;
	private String lPlanId;
	private String empID;
	private String userType;
	private String queID;
	private String queCnt;
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strSessionUserTypeID = (String) session.getAttribute(USERTYPEID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		UtilityFunctions uF = new UtilityFunctions();
		String submit = request.getParameter("submit");
		System.out.println("submit ===>> " + submit);
		if (submit == null) {
		getanswerTypeMap(uF);
		getAssessmentQuestionMap(uF);
		getQuestionsAnswer(uF);
		return LOAD;
		
		} else {
			getanswerTypeMap(uF);
			getAssessmentQuestionMap(uF);
			getQuestionsAnswer(uF);
			updateQuestionsAnswer(uF);
			return SUCCESS;
		}
	}
	
	

	private void updateQuestionsAnswer(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
		Map<String, String> innerMp = (Map<String, String>) request.getAttribute("innerMp");
		List<String> questioninnerList = hmQuestion.get(innerMp.get("QUESTION_ID"));
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
			if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
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
			if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
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
			if (givenAnswer != null && answer != null && answer.contains(givenAnswer)) {
				marks = uF.parseToDouble(innerMp.get("WEIGHTAGE"));
			}
			
		} else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
			ansComment = request.getParameter("anscomment"+questioninnerList.get(9));
			givenAnswer = request.getParameter(""+questioninnerList.get(9)) + ",";
			String answer = questioninnerList.get(6);
			if (givenAnswer != null && answer != null && answer.contains(givenAnswer)) {
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
			if (givenAnswer != null && correctanswer != null && correctanswer.contains(givenAnswer)) {
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
			if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
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
			marks = uF.parseToDouble(rating) * uF.parseToDouble(innerMp.get("WEIGHTAGE")) / 5;

		} else if (uF.parseToInt(questioninnerList.get(8)) == 12) {
//			System.out.println("questioninnerList.get(9) ===> "+questioninnerList.get(9));
			ansComment = request.getParameter("anscomment"+questioninnerList.get(9));
//			System.out.println("ansComment ===> "+ansComment);
			givenAnswer = request.getParameter(""+questioninnerList.get(9));
//			System.out.println("givenAnswer ===> "+givenAnswer);
		} else if (uF.parseToInt(questioninnerList.get(8)) == 14) {
			ansComment = request.getParameter("anscomment" + questioninnerList.get(9));
			String[] correct = request.getParameterValues("correct" + questioninnerList.get(9));
			for (int k = 0; correct != null && k < correct.length; k++) {
				if (k == 0) {
					givenAnswer = correct[k] + ",";
				} else {
					givenAnswer += correct[k] + ",";
				}
			}
			String correctanswer = questioninnerList.get(6);
			if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
				marks = uF.parseToDouble(innerMp.get("WEIGHTAGE"));
			}
		}
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update assessment_question_answer set answer =?, remark=?, marks=?,answers_comment=? " +
					"where assess_question_answer_id=?");
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

			pst = con.prepareStatement("select * from assessment_question_answer where assess_question_answer_id=?");
			pst.setInt(1, uF.parseToInt(getQueID()));
			rs = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			Map<String, String> innerMp = new HashMap<String, String>();
			while (rs.next()) {
				innerMp.put("ANSWER", rs.getString("answer"));
				innerMp.put("REMARK", rs.getString("remark"));
				innerMp.put("MARKS", rs.getString("marks"));
				innerMp.put("ASSESS_QUE_ANS_ID", rs.getString("assess_question_answer_id"));
				innerMp.put("WEIGHTAGE", rs.getString("weightage"));
				innerMp.put("ANSWERCOMMENT", rs.getString("answers_comment"));
				innerMp.put("QUESTION_ID", rs.getString("assessment_question_bank_id"));
				
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
	
	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}
		
	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}
	
	public String getlPlanId() {
		return lPlanId;
	}

	public void setlPlanId(String lPlanId) {
		this.lPlanId = lPlanId;
	}

	public void getAssessmentQuestionMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<String>> hmQuestion = new HashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from assessment_question_bank aqb, assessment_question_answer aqa where " +
					"aqb.assessment_question_bank_id=aqa.assessment_question_bank_id and aqa.assess_question_answer_id = ?");
			pst.setInt(1, uF.parseToInt(getQueID()));
			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("assessment_question_bank_id"));
				innerList.add(rs.getString("question_text"));
				innerList.add(rs.getString("option_a")); //2
				innerList.add(rs.getString("option_b")); //3
				innerList.add(rs.getString("option_c")); //4
				innerList.add(rs.getString("option_d")); //5
				innerList.add(rs.getString("correct_ans")); //6
				innerList.add(rs.getString("is_add")); //7
				innerList.add(rs.getString("answer_type")); //8
				innerList.add(rs.getString("assess_question_answer_id")); //9
				innerList.add(rs.getString("que_matrix_heading")); //10
				innerList.add(rs.getString("que_attached_file")); //11
				
				outerList.add(innerList);
				hmQuestion.put(rs.getString("assessment_question_bank_id"), innerList);
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

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

}
