package com.konnect.jpms.training;

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
import com.opensymphony.xwork2.ActionSupport;

public class TrainingFeedbackSummary extends ActionSupport implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeID;
	CommonFunctions CF;
	
	private String trainingId;
	private String lPlanId;
	private String empID;
	private String[] trainerId;
	private String type;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return "login";
		request.setAttribute(PAGE, "/jsp/training/TrainingFeedbackSummary.jsp");
		request.setAttribute(TITLE, "Training Feedback Summary");
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("trainingId ====> " + trainingId);
//		System.out.println("lPLanId ====> " + lPlanId);
		getTrainingFeedbackQuestion(uF);
		getFeedbackQuestionAnswer(uF);
		getQuestionSubType(uF);
		
//		String submit = request.getParameter("submit");
		
		return LOAD;
	}
	
	
	private void getTrainingFeedbackQuestion(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			request.setAttribute("hmEmpName", hmEmpName);
			if(getType() != null && getType().equals("T")) {
				pst = con.prepareStatement("select * from training_question_details tqd, training_question_bank tqb where tqd.plan_id = ? and question_for = 1 and tqd.question_id = tqb.training_question_bank_id");
			} else {
				pst = con.prepareStatement("select * from training_question_details tqd, training_question_bank tqb where tqd.plan_id = ? and question_for = 2 and tqd.question_id = tqb.training_question_bank_id");
			}
			pst.setInt(1, uF.parseToInt(trainingId));
			rs = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			List<List<String>> questionDetailsList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("plan_id"));
				innerList.add(rs.getString("training_question_id"));
				innerList.add(rs.getString("training_question_bank_id"));
				innerList.add(rs.getString("question_text"));
				innerList.add(rs.getString("option_a"));
				innerList.add(rs.getString("option_b"));
				innerList.add(rs.getString("option_c"));
				innerList.add(rs.getString("option_d"));
				innerList.add(rs.getString("question_type"));
				innerList.add(rs.getString("correct_ans"));
				innerList.add(rs.getString("weightage"));
//				innerList.add(rs.getString(""));
				questionDetailsList.add(innerList);
			}
			rs.close();
			pst.close();
//			System.out.println("questionDetailsList ===> " + questionDetailsList);
			request.setAttribute("questionDetailsList", questionDetailsList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public void getFeedbackQuestionAnswer(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
//			System.out.println("TYPE ====> " +getType());
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from training_question_answer where learning_plan_id=? and plan_id=? ");
//			if(getType() != null && getType().equals("T")) {
//				sbQuery.append(" and emp_id=? and user_id=?");
//			} else {
//				sbQuery.append(" and user_id=? and emp_id=?");
//			}
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getlPlanId()));
//			pst.setInt(2, uF.parseToInt(getTrainingId()));
//			pst.setInt(3, uF.parseToInt(getEmpID()));
//			pst.setInt(4, uF.parseToInt(strSessionEmpId));
//			System.out.println("pst =========> " + pst);
//			rs = pst.executeQuery();
			
			sbQuery.append("select * from training_question_answer where learning_plan_id=? and plan_id=? ");
			if(getType() != null && getType().equals("T")) {
				sbQuery.append(" and emp_id=? and user_id=?");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getlPlanId()));
				pst.setInt(2, uF.parseToInt(getTrainingId()));
				pst.setInt(3, uF.parseToInt(getEmpID()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
//				System.out.println("pst =========> " + pst);
				rs = pst.executeQuery();
			} else {
				sbQuery.append(" and user_id=? and user_type_id=3");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getlPlanId()));
				pst.setInt(2, uF.parseToInt(getTrainingId()));
				pst.setInt(3, uF.parseToInt(getEmpID()));
//				System.out.println("pst =========> " + pst);
				rs = pst.executeQuery();
			}
			
			Map<String, Map<String, String>> questionanswerMp = new HashMap<String, Map<String, String>>();

			while (rs.next()) {
				Map<String, String> innerMp = new HashMap<String, String>();
				innerMp.put("ANSWER", rs.getString("answer"));
				innerMp.put("REMARK", rs.getString("remark"));
				innerMp.put("MARKS", rs.getString("marks"));
				innerMp.put("TRAINING_QUE_ANS_ID", rs.getString("training_question_answer_id"));
				questionanswerMp.put(rs.getString("training_question_id") + "question" + rs.getString("question_id"), innerMp);
			}
			rs.close();
			pst.close();
//			System.out.println("questionanswerMp ===> " + questionanswerMp);
			request.setAttribute("questionanswerMp", questionanswerMp);

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


	private void getQuestionSubType(UtilityFunctions uF) {
		Map<String, List<List<String>>> answertypeSub = new HashMap<String, List<List<String>>>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {
			pst = con.prepareStatement("select * from training_answer_type_sub order by training_answer_type_sub_id"); //select * from appraisal_answer_type_sub order by appraisal_answer_type_sub_id
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

	
	public String[] getTrainerId() {
		return trainerId;
	}

	public void setTrainerId(String[] trainerId) {
		this.trainerId = trainerId;
	}

	public String getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(String trainingId) {
		this.trainingId = trainingId;
	}

	public String getlPlanId() {
		return lPlanId;
	}

	public void setlPlanId(String lPlanId) {
		this.lPlanId = lPlanId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}

	public String getStrDomain() {
		return strDomain;
	}
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}


}
