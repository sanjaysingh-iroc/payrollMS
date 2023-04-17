package com.konnect.jpms.training;

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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TakeAssessment extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	CommonFunctions CF = null;

	private String assessmentId;
	private String lPlanId;
	private  String submit;
    
	private static Logger log = Logger.getLogger(AddNewAssessment.class);
	
	public String execute() {

		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		
		String strOperation = request.getParameter("operation");
		
		request.setAttribute(PAGE, "/jsp/training/AddNewAssessment.jsp");
		if (strOperation != null && strOperation.equalsIgnoreCase("E")) {
			request.setAttribute(TITLE, "Edit Assessment");
		} else {
			request.setAttribute(TITLE, "Add Assessment");
		}
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
//		System.out.println("getlPlanId() ===> " + getlPlanId());
//		System.out.println("getAssessmentId() ===> " + getAssessmentId());
		
		String submit = request.getParameter("submit");
//		System.out.println("submit ===> " + submit);
		

		getAnsType();
		getAnswerTypeMap(uF);
		getQuestionSubType(uF);
		getAssessmentData(uF);
		getSectionDataList(uF);
		getAssessmentQuestionAnswer(uF);
		getQuestionsList(uF);
		
		if(submit != null && submit.equals("Submit")) { 
			insertTakeAssessment(uF);
			return SUCCESS;
		}
		
		return LOAD;
	}
	

	public void getAssessmentQuestionAnswer(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from assessment_question_answer where assessment_details_id=? and learning_plan_id=? and emp_id=? and user_id=?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.setInt(2, uF.parseToInt(getlPlanId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> questionAnswerMp = new HashMap<String, Map<String, String>>();

			while (rs.next()) {

				Map<String, String> innerMp = new HashMap<String, String>();
				innerMp.put("ANSWER", rs.getString("answer"));
				innerMp.put("REMARK", rs.getString("remark"));
				innerMp.put("MARKS", rs.getString("marks"));
				innerMp.put("WEIGHTAGE", rs.getString("weightage"));
				innerMp.put("ASSESS_QUE_ANS_ID", rs.getString("assess_question_answer_id"));
				questionAnswerMp.put(rs.getString("assessment_question_id") + "question" + rs.getString("assessment_question_bank_id"), innerMp);
			}
			rs.close();
			pst.close();
			
//			System.out.println("questionAnswerMp ===> " + questionAnswerMp);
			request.setAttribute("questionAnswerMp", questionAnswerMp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getQuestionSubType(UtilityFunctions uF) {
		Map<String, List<List<String>>> answertypeSub = new HashMap<String, List<List<String>>>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from appraisal_answer_type_sub order by appraisal_answer_type_sub_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> outerList = answertypeSub.get(rs.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("score"));
				innerList.add(rs.getString("score_label"));
				outerList.add(innerList);
				answertypeSub.put(rs.getString("answer_type_id"), outerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("answertypeSub", answertypeSub);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getAnswerTypeMap(UtilityFunctions uF) {

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
	
	private void getQuestionsList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
//			List<List<String>> assessmentList = new ArrayList<List<String>>();
			Map<String, List<List<String>>> hmAssessmentQueData = new HashMap<String, List<List<String>>>();
			pst = con.prepareStatement("select * from assessment_question_details, assessment_question_bank where assessment_question_bank_id = question_bank_id and assessment_details_id = ?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> questionList = hmAssessmentQueData.get(rs.getString("assessment_section_id"));
				if(questionList == null){
					questionList = new ArrayList<List<String>>();
				}
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_bank_id"));
				innerList.add(rs.getString("question_text"));
				innerList.add(rs.getString("option_a"));
				innerList.add(rs.getString("option_b"));
				innerList.add(rs.getString("option_c"));
				innerList.add(rs.getString("option_d"));
				innerList.add(rs.getString("correct_ans"));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("answer_type"));
				innerList.add(rs.getString("assessment_question_id"));
				innerList.add(rs.getString("is_add"));
				innerList.add(rs.getString("assessment_section_id"));
				innerList.add(rs.getString("assessment_details_id"));
				
				questionList.add(innerList);
				hmAssessmentQueData.put(rs.getString("assessment_section_id"), questionList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmAssessmentQueData === > " +hmAssessmentQueData);
			request.setAttribute("hmAssessmentQueData", hmAssessmentQueData);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void getAssessmentData(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmCourseSubject = new HashMap<String, String>();
			pst = con.prepareStatement("select * from course_subject_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmCourseSubject.put(rs.getString("course_subject_id"), rs.getString("course_subject_name"));
			}
			rs.close();
			pst.close();
			
			List<String> assessmentList = new ArrayList<String>();
			pst = con.prepareStatement("select * from assessment_details where assessment_details_id = ? ");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			rs = pst.executeQuery();
//			 String assessPreface = null;
			while (rs.next()) {
				assessmentList.add(rs.getString("assessment_details_id"));
				assessmentList.add(rs.getString("assessment_name"));
				assessmentList.add(hmCourseSubject.get(rs.getString("assessment_subject")));
				assessmentList.add(rs.getString("assessment_author"));
				assessmentList.add(rs.getString("assessment_version"));
				assessmentList.add(rs.getString("assessment_description"));
//				assessPreface = rs.getString("assessment_description");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("assessmentList", assessmentList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getSectionDataList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			List<List<String>> sectionList = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from assessment_section_details where assessment_details_id = ?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("assessment_section_id"));
				innerList.add(rs.getString("assessment_section_name"));
				innerList.add(rs.getString("assessment_section_description"));
				innerList.add(rs.getString("marks_of_section"));
				innerList.add(rs.getString("attempt_questions"));
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

	
	private void getAnsType() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
			Map<String, String> hmWLocation = CF.getWLocationMap(con, null, null);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
//			System.out.println("hmWLocation ===> "+hmWLocation);
			request.setAttribute("hmEmpLocation", hmEmpLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			StringBuilder sb = new StringBuilder("");
			List<List<String>> ansTypeList = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from training_answer_type");
			rs = pst.executeQuery();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("answer_type")) == 9) {
					sb.append("<option value=\"" + rs.getString("answer_type") + "\" selected>"
							+ rs.getString("answer_type_name") + "</option>");
				} else {
					sb.append("<option value=\"" + rs.getString("answer_type") + "\">"
							+ rs.getString("answer_type_name") + "</option>");
				}
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("answer_type"));
				innerList.add(rs.getString("answer_type_name"));
				ansTypeList.add(innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("anstype", sb.toString());
			request.setAttribute("ansTypeList", ansTypeList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void insertTakeAssessment(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
//		List<List<String>> questionDetailsList = (List<List<String>>) request.getAttribute("questionDetailsList");
		List<List<String>> sectionList = (List<List<String>>) request.getAttribute("sectionList");
		Map<String, List<List<String>>> hmAssessmentQueData = (Map<String, List<List<String>>>) request.getAttribute("hmAssessmentQueData");
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from assessment_question_answer where emp_id=? and user_id =? and assessment_details_id=? and learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setInt(3, uF.parseToInt(getAssessmentId()));
			pst.setInt(4, uF.parseToInt(getlPlanId()));
			pst.execute();
			pst.close();
			
//			System.out.println("pst delete ===> " + pst);
			for (int j = 0; sectionList != null && j < sectionList.size(); j++) {
				List<String> sectionInnerList = sectionList.get(j);
				List<List<String>> questionDetailsList = hmAssessmentQueData.get(sectionInnerList.get(0));
				
				for (int i = 0; questionDetailsList != null && i < questionDetailsList.size(); i++) {
					List<String> innerlist = (List<String>) questionDetailsList.get(i);
	
					String weightage = innerlist.get(7);
					String givenAnswer = null;
					double marks = 0;
					String remark = null;
					String ansComment = null;
					
					if (uF.parseToInt(innerlist.get(8)) == 1) {
						ansComment = request.getParameter("anscomment" + innerlist.get(0)+"_"+innerlist.get(9));
						String[] correct = request.getParameterValues("correct" + innerlist.get(0)+"_"+innerlist.get(9));
						remark = request.getParameter("" + innerlist.get(0)+"_"+innerlist.get(9));
						String correctanswer = innerlist.get(6);
						for (int k = 0; correct != null && k < correct.length; k++) {
							if (k == 0) {
								givenAnswer = correct[k] + ",";
							} else {
								givenAnswer += correct[k] + ",";
							}
						}
						if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
							marks = uF.parseToDouble(weightage);
						}

					} else if (uF.parseToInt(innerlist.get(8)) == 2) {
						ansComment = request.getParameter("anscomment" + innerlist.get(0)+"_"+innerlist.get(9));
						String[] correct = request.getParameterValues("correct" + innerlist.get(0)+"_"+innerlist.get(9));
						for (int k = 0; correct != null && k < correct.length; k++) {
							if (k == 0) {
								givenAnswer = correct[k] + ",";
							} else {
								givenAnswer += correct[k] + ",";
							}
						}
						String correctanswer = innerlist.get(6);

						if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
							marks = uF.parseToDouble(weightage);
						} else {
							marks = 0;
						}

					} else if (uF.parseToInt(innerlist.get(8)) == 3) { 
						ansComment = request.getParameter("anscomment" + innerlist.get(0)+"_"+innerlist.get(9));
//						System.out.println("MArks OF ANSTYPE 3 ===> " + request.getParameter("marks" + innerlist.get(0)+"_"+innerlist.get(9)));
//						System.out.println("ANSTYPE 3 ID ===> " + "marks" + innerlist.get(0)+"_"+innerlist.get(9));
						marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(0)+"_"+innerlist.get(9)));
						
					} else if (uF.parseToInt(innerlist.get(8)) == 4) {
						ansComment = request.getParameter("anscomment" + innerlist.get(0)+"_"+innerlist.get(9));
						givenAnswer = request.getParameter("" + innerlist.get(0)+"_"+innerlist.get(9));
						marks = (uF.parseToDouble(givenAnswer) * uF.parseToDouble(weightage)) / 100;

					} else if (uF.parseToInt(innerlist.get(8)) == 5) {
						ansComment = request.getParameter("anscomment" + innerlist.get(0)+"_"+innerlist.get(9));
						givenAnswer = request.getParameter("" + innerlist.get(0)+"_"+innerlist.get(9)) + ",";
						String answer = innerlist.get(6);
						if (givenAnswer != null && answer != null && answer.contains(givenAnswer)) {
							marks = uF.parseToDouble(weightage);
						}
						
					} else if (uF.parseToInt(innerlist.get(8)) == 6) {
						ansComment = request.getParameter("anscomment" + innerlist.get(0)+"_"+innerlist.get(9));
						givenAnswer = request.getParameter("" + innerlist.get(0)+"_"+innerlist.get(9)) + ",";
						String answer = innerlist.get(6);
						if (givenAnswer != null && answer != null && answer.contains(givenAnswer)) {
							marks = uF.parseToDouble(weightage);
						}

					} else if (uF.parseToInt(innerlist.get(8)) == 7) {
						ansComment = request.getParameter("anscomment" + innerlist.get(0)+"_"+innerlist.get(9));
						givenAnswer = request.getParameter("" + innerlist.get(0)+"_"+innerlist.get(9));
//						System.out.println("MArks OF ANSTYPE 7 ===> " + request.getParameter("marks" + innerlist.get(0)+"_"+innerlist.get(9)));
//						System.out.println("ANSTYPE 7 ID ===> " + "marks" + innerlist.get(0)+"_"+innerlist.get(9));
						marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(0)+"_"+innerlist.get(9)));
						weightage = request.getParameter("outofmarks" + innerlist.get(0)+"_"+innerlist.get(9));

					} else if (uF.parseToInt(innerlist.get(8)) == 8) {
						ansComment = request.getParameter("anscomment" + innerlist.get(0)+"_"+innerlist.get(9));
						givenAnswer = request.getParameter("correct" + innerlist.get(0)+"_"+innerlist.get(9)) + ",";
						String correctanswer = innerlist.get(6);
						if (givenAnswer != null && correctanswer != null && correctanswer.contains(givenAnswer)) {
							marks = uF.parseToDouble(weightage);
						}

					} else if (uF.parseToInt(innerlist.get(8)) == 9) {
						ansComment = request.getParameter("anscomment" + innerlist.get(0)+"_"+innerlist.get(9));
						String[] correct = request.getParameterValues("correct" + innerlist.get(0)+"_"+innerlist.get(9));
						for (int k = 0; correct != null && k < correct.length; k++) {
							if (k == 0) {
								givenAnswer = correct[k] + ",";
							} else {
								givenAnswer += correct[k] + ",";
							}
						}
						String correctanswer = innerlist.get(6);
//						System.out.println("correctanswer ===> " + correctanswer +" givenAnswer ===> "+givenAnswer);
						if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
//							System.out.println("in if correctanswer ===> " + correctanswer +" givenAnswer ===> "+givenAnswer);
							marks = uF.parseToDouble(weightage);
						}
					} else if (uF.parseToInt(innerlist.get(8)) == 10) {
						ansComment = request.getParameter("anscomment" + innerlist.get(0)+"_"+innerlist.get(9));
//						System.out.println("MArks OF ANSTYPE 10 ===> " + request.getParameter("marks" + innerlist.get(0)+"_"+innerlist.get(9)));
//						System.out.println("ANSTYPE 10 ID ===> " + "marks" + innerlist.get(0)+"_"+innerlist.get(9));
						marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(0)+"_"+innerlist.get(9)));
						String a = request.getParameter("a" + innerlist.get(0)+"_"+innerlist.get(9));
						String b = request.getParameter("b" + innerlist.get(0)+"_"+innerlist.get(9));
						String c = request.getParameter("c" + innerlist.get(0)+"_"+innerlist.get(9));
						String d = request.getParameter("d" + innerlist.get(0)+"_"+innerlist.get(9));

						givenAnswer = uF.showData(a, "") + " :_:" + uF.showData(b, "") + " :_:" + uF.showData(c, "") + " :_: " + uF.showData(d, "");

					} else if (uF.parseToInt(innerlist.get(8)) == 11) {
						ansComment = request.getParameter("anscomment" + innerlist.get(0)+"_"+innerlist.get(9));
						String rating = request.getParameter("gradewithrating" + innerlist.get(0)+"_"+innerlist.get(9));
						marks = uF.parseToDouble(rating) * uF.parseToDouble(weightage) / 5;

					}else if (uF.parseToInt(innerlist.get(8)) == 12) {
						ansComment = request.getParameter("anscomment" + innerlist.get(0)+"_"+innerlist.get(9));
						givenAnswer = request.getParameter("" + innerlist.get(0)+"_"+innerlist.get(9));
					}
					
					  //training_question_answer_id serial NOT NULL,training_question_id
					  //emp_id,answer,plan_id,question_id,user_id,user_type_id,attempted_on,weightage,marks,remark,training_question_id
					
					pst = con.prepareStatement("insert into assessment_question_answer(emp_id,answer,assessment_details_id,assessment_question_id," +
							"user_id,user_type_id,entry_date,weightage,marks,remark,assessment_question_bank_id,learning_plan_id,answers_comment," +
							"assessment_section_id)values(?,?,?,? ,?,?,?,? ,?,?,?,? ,?,?)");
					pst.setInt(1, uF.parseToInt(strSessionEmpId));
					pst.setString(2, givenAnswer);
					pst.setInt(3, uF.parseToInt(getAssessmentId()));
					pst.setInt(4, uF.parseToInt(innerlist.get(9)));
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					pst.setInt(6, uF.parseToInt(""));
					pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDouble(8, uF.parseToDouble(weightage));
					pst.setDouble(9, marks);
					pst.setString(10, remark);
					pst.setInt(11, uF.parseToInt(innerlist.get(0)));
					pst.setInt(12, uF.parseToInt(getlPlanId()));
					pst.setString(13, ansComment);
					pst.setInt(14, uF.parseToInt(sectionInnerList.get(0)));
					pst.execute();
					pst.close();
	//				System.out.println("pst insert ===> " + pst);
				}
		}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
//	public String getAppendData(String strIds) {
//		
//		StringBuilder sb = new StringBuilder();
//		if(strIds != null && !strIds.equals("")) {
//			
//			List<String> idsList = Arrays.asList(strIds.split(","));
//			if (idsList != null && !idsList.isEmpty()) {
//				
//				for (int i = 0; i < idsList.size(); i++) {
//					if (i == 0) {
//						sb.append("," + idsList.get(i).trim() + ",");
//					} else {
//						sb.append(idsList.get(i).trim() + ",");
//					}
//				}
//			} else {
//				return null;
//			}
//		}
//		return sb.toString();
//	}
	
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;

	}

	public String getAssessmentId() {
		return assessmentId;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
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

}
