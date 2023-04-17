package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.Date;
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

import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class CandidateTakeAssessment implements ServletRequestAware, IStatements, Runnable {

	HttpSession session;
//	String strSessionEmpId;
//	String strSessionUserType;
//	String strSessionUserTypeID;
//	String strUserType =  null;
	CommonFunctions CF;
	String assessmentId;
//    String lPlanId;
	
	String candidateId;
	String recruitId;
	String roundId;
	String currentLevel;
	String userType;
	String levelCount;
	
	public String execute() {
//		session = request.getSession();
//		strSessionEmpId = (String) session.getAttribute(EMPID);
//		strSessionUserType = (String) session.getAttribute(USERTYPE);
//		strSessionUserTypeID = (String) session.getAttribute(USERTYPEID);
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null){ 
			CF = new CommonFunctions();
			CF.setRequest(request);
		}  
		
//		strUserType = (String)session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, "/jsp/recruitment/CandidateTakeAssessment.jsp");
		request.setAttribute(TITLE, "Take Assessment");
		
//		session.setAttribute(MENU, "/jsp/common/PreMenu.jsp");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		
		boolean attemptFlag = checkAttemptLimit(uF);
		if(attemptFlag) {
			return "attemptAlert";
		}
		
		boolean interviewDateFlag = checkInterviewDate(uF);
		if(interviewDateFlag) {
			return "attemptAlert";
		}
		
		if(uF.parseToInt(getCandidateId()) > 0 && uF.parseToInt(getAssessmentId()) > 0 && uF.parseToInt(getRoundId()) > 0 && uF.parseToInt(getRecruitId()) > 0) {
			getanswerTypeMap(uF);
			getSectionStep(uF);
		
			getQuestionSubType(uF);
			getAssessmentData(uF);
			
			if(levelCount == null) {
				levelCount = "1";
			} else {
				int cnt = uF.parseToInt(levelCount);
				cnt++;
				setLevelCount(""+cnt);
			}
	//		System.out.println("assessmentId ===> " + assessmentId);
	//		System.out.println("recruitId ===> " + recruitId);
	//		System.out.println("roundId ===> " + roundId);
	//		System.out.println("levelCount ===> " + levelCount);
	//		System.out.println("getCurrentLevel() ===> "+getCurrentLevel());
			String submit = request.getParameter("submit");
			String btnfinish = request.getParameter("btnfinish");
			String finishType = request.getParameter("finishType");
	//		String levelAppSystem = request.getParameter("levelAppSystem");
	//		System.out.println("finishType ===> "+finishType);
	//		System.out.println("submit ========= > " + submit);
	//		System.out.println("getCurrentLevel ========= > " + getCurrentLevel());
			if ((submit == null || submit.trim().equals("")) && (btnfinish == null || btnfinish.trim().equals("")) && (finishType == null || finishType.trim().equals(""))) {
				getSctionStatus(uF);
				checkCurrentLevelExistForCurrentEmp(uF);
				return getSectionDetails(uF);
	
			} else {
	//			System.out.println("levelCount ========== > " + levelCount+ " -- getCurrentLevel ======== > " + getCurrentLevel());
				getPreviousSectionData(uF);	
				//getAssessmentQuestionAnswer(uF);
				if(getCurrentLevel() !=null && uF.parseToInt(levelCount) > 2) {
					insertMarks(uF);
				}
				if(uF.parseToInt(levelCount)>2) {
					if(swapLevelId(uF)) {
						insertTakeAssessmentAttempt(uF);
		//				String btnSubmit = request.getParameter("btnfinish");
						if(btnfinish != null && btnfinish.equals("Finish")) {
							return "finish";
						} else if(finishType != null && finishType.trim().equalsIgnoreCase("finish")) {
							return "finish";
						} else {
							return "update";
						}
					}
				}
	//			System.out.println("levelCount ===> "+levelCount);
	//			System.out.println("getCurrentLevel() ===> "+getCurrentLevel());
				getSctionStatus(uF);
				checkCurrentLevelExistForCurrentEmp(uF);
				return getSectionDetails(uF);
			}
		}
		return "success";
	}

	
	private boolean checkInterviewDate(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select interview_date from candidate_interview_panel where assessment_id=? and panel_round_id=? and candidate_id=? and " +
					"recruitment_id=?"); // and interview_date = ?
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.setInt(2, uF.parseToInt(getRoundId()));
			pst.setInt(3, uF.parseToInt(getCandidateId()));
			pst.setInt(4, uF.parseToInt(getRecruitId()));
//			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			Date inerviewDt = null;
			Date currDt = uF.getCurrentDate(CF.getStrTimeZone());
			while (rs.next()) {
				if(rs.getString("interview_date") != null) {
					inerviewDt = uF.getDateFormat(rs.getString("interview_date"), DBDATE);
				}
				if(inerviewDt != null) {
					if(inerviewDt.after(currDt)) {
						flag = true;
						request.setAttribute("STATUS_MSG", "You can not take assessment, your assessment date is "+uF.getDateFormat(rs.getString("interview_date"), DBDATE, DATE_FORMAT_STR)+". You are trying to take assessment before time, Please try on time.");
					} else if(inerviewDt.before(currDt)) {
						flag = true;
						request.setAttribute("STATUS_MSG", "You can not take assessment, your assessment date was "+uF.getDateFormat(rs.getString("interview_date"), DBDATE, DATE_FORMAT_STR)+". You are trying to take assessment after time.");
					}
				}
				
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
		return flag;
	}


	private boolean checkAttemptLimit(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select count(assessment_take_attempt_id) as attemptCount from assessment_take_attempt_details where assessment_details_id=? " +
					" and round_id=? and candidate_id=? and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.setInt(2, uF.parseToInt(getRoundId()));
			pst.setInt(3, uF.parseToInt(getCandidateId()));
			pst.setInt(4, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
			int attemptCount = 0;
			while (rs.next()) {
				attemptCount = rs.getInt("attemptCount");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select assessment_take_attempt from assessment_details where assessment_details_id=?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			rs = pst.executeQuery();
			int assessAttemptCount = 0;
			while (rs.next()) {
				assessAttemptCount = rs.getInt("assessment_take_attempt");
			}
			rs.close();
			pst.close();
			
//			System.out.println("attemptCount ===>> " + attemptCount + " -- assessAttemptCount ===>> " + assessAttemptCount);
			
			if(attemptCount >= assessAttemptCount) {
				flag = true;
				request.setAttribute("STATUS_MSG", "You can not take assessment, you cross the assessment attempt limit.");
			}
	
//			System.out.println("flag ====> "+flag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}


	private void insertTakeAssessmentAttempt(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("insert into assessment_take_attempt_details(assessment_details_id,take_attempt_count,assessment_section_ids," +
					"added_by,entry_date,round_id,candidate_id,recruitment_id)values(?,?,?,?, ?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.setInt(2, uF.parseToInt("1"));
			pst.setString(3, "");
			pst.setInt(4, uF.parseToInt(getCandidateId()));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(getRoundId()));
			pst.setInt(7, uF.parseToInt(getCandidateId()));
			pst.setInt(8, uF.parseToInt(getRecruitId()));
			pst.executeUpdate();
			pst.close();
			
			pst=con.prepareStatement("update candidate_interview_panel set status=? where recruitment_id=? and candidate_id=? and panel_round_id=? and " +
					" assessment_id=? ");
			pst.setInt(1, uF.parseToInt("1"));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			pst.setInt(3, uF.parseToInt(getCandidateId()));
			pst.setInt(4, uF.parseToInt(getRoundId()));
			pst.setInt(5, uF.parseToInt(getAssessmentId()));
			pst.executeUpdate();
			pst.close();
			
//			System.out.println("flag ====> "+flag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
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
			pst = con.prepareStatement("select assessment_section_id from assessment_section_details where assessment_details_id = ? and assessment_section_id>? order by assessment_section_id");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.setInt(2, uF.parseToInt(getCurrentLevel()));
			rs = pst.executeQuery();
//			System.out.println("pst ====> "+pst);
			while (rs.next()) {
				flag = false;
				setCurrentLevel(rs.getString("assessment_section_id"));
				break;
			}
			
			rs.close();
			pst.close();
//			System.out.println("flag ====> "+flag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}
	
	
	
	public void getAssessmentQuestionAnswer(UtilityFunctions uF, int sectionId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from assessment_question_answer where assessment_details_id=? and candidate_id=? and user_id=? and assessment_section_id=? and round_id=? and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setInt(3, uF.parseToInt(getCandidateId()));
			pst.setInt(4, sectionId);
			pst.setInt(5, uF.parseToInt(getRoundId()));
			pst.setInt(6, uF.parseToInt(getRecruitId()));
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

	

	public void getSctionStatus(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select assessment_section_id from assessment_question_answer where assessment_details_id=? and candidate_id=? " +
				"and user_id=? and user_type_id=? and round_id=? and recruitment_id=? group by assessment_section_id order by assessment_section_id");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setInt(3, uF.parseToInt(getCandidateId()));
			pst.setInt(4, uF.parseToInt("0"));
			pst.setInt(5, uF.parseToInt(getRoundId()));
			pst.setInt(6, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
			Map<String, String> innerMp = new HashMap<String, String>();
			while (rs.next()) {
				innerMp.put(rs.getString("assessment_section_id"), rs.getString("assessment_section_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("SECTION_STATUS", innerMp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void getSectionStep(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			List<String> sectionsList = new ArrayList<String>();
			pst = con.prepareStatement("select assessment_section_id from assessment_section_details where assessment_details_id = ? order by assessment_section_id");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				sectionsList.add(rs.getString("assessment_section_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("sectionsList", sectionsList);
//			request.setAttribute("mainLevelList", sectinsList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public boolean getPreviousSectionData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);
//			StringBuilder sb = new StringBuilder("select * from appraisal_question_details where appraisal_id=? and appraisal_level_id in(select appraisal_level_id from  appraisal_level_details where  main_level_id=?)");
			StringBuilder sb = new StringBuilder("select * from assessment_question_details, assessment_question_bank where assessment_question_bank_id = question_bank_id and assessment_details_id = ? and assessment_section_id = ?");
		
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.setInt(2, uF.parseToInt(getCurrentLevel()));
			rs = pst.executeQuery();
//			Map<String, List<List<String>>> hmAssessmentQueData = new HashMap<String, List<List<String>>>();
//			List<List<String>> outerList = new ArrayList<List<String>>();
			List<List<String>> questionList = new ArrayList<List<String>>();
			while (rs.next()) {
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
//				hmAssessmentQueData.put(rs.getString("assessment_section_id"), questionList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("questionList ===> " + questionList);
			
			request.setAttribute("questionList", questionList);
			
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
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
//			System.out.println("insertMarks =======>> ");
			pst = con.prepareStatement("delete from assessment_question_answer where candidate_id=? and user_id=? and assessment_details_id=? and round_id=? and assessment_section_id=? and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.setInt(2, uF.parseToInt(getCandidateId()));
			pst.setInt(3, uF.parseToInt(getAssessmentId()));
			pst.setInt(4, uF.parseToInt(getRoundId()));
			pst.setInt(5, uF.parseToInt(currentLevel));
			pst.setInt(6, uF.parseToInt(getRecruitId()));
			pst.execute();
			pst.close();
			
			List<List<String>> questionDetailsList = (List<List<String>>) request.getAttribute("questionList");
//			System.out.println("questionDetailsList =======>> " + questionDetailsList);
			
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
//					System.out.println("MArks OF ANSTYPE 3 ===> " + request.getParameter("marks" + innerlist.get(0)+"_"+innerlist.get(9)));
//					System.out.println("ANSTYPE 3 ID ===> " + "marks" + innerlist.get(0)+"_"+innerlist.get(9));
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
//					System.out.println("MArks OF ANSTYPE 7 ===> " + request.getParameter("marks" + innerlist.get(0)+"_"+innerlist.get(9)));
//					System.out.println("ANSTYPE 7 ID ===> " + "marks" + innerlist.get(0)+"_"+innerlist.get(9));
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
					List<String> alGivenAns = new ArrayList<String>();
					for (int k = 0; correct != null && k < correct.length; k++) {
						alGivenAns.add(correct[k].trim());
						if (k == 0) {
							givenAnswer = correct[k] + ",";
						} else {
							givenAnswer += correct[k] + ",";
						}
					}
					String correctanswer = innerlist.get(6);
					
					System.out.println("correctanswer ===> " + correctanswer +" givenAnswer ===> "+givenAnswer);
//					if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
//						System.out.println("in if correctanswer ===> " + correctanswer +" givenAnswer ===> "+givenAnswer);
//						marks = uF.parseToDouble(weightage);
//					}
					if((correctanswer !=null && !correctanswer.equals("")) && alGivenAns!=null ){
						List<String> alCorrect = Arrays.asList(correctanswer.trim().split(","));
						List<String> alCorrectAns = new ArrayList<String>();
						if(alCorrect!=null){
							for(int x = 0; alCorrect!=null && x<alCorrect.size(); x++){
								alCorrectAns.add(alCorrect.get(x).trim());
							}
							int nCorrectAns = 0;
							for(int ii = 0; ii < alGivenAns.size(); ii++){
								if(alCorrectAns.contains(alGivenAns.get(ii))){
									nCorrectAns++;
								}
							}
							
							int nGivenAns = alGivenAns.size();
							if(nCorrectAns == nGivenAns && nCorrectAns == alCorrectAns.size()){
								marks = uF.parseToDouble(weightage);
							} else {
								marks = 0.0d;
							}
//							System.out.println("alCorrectAns.size() ===> " + alCorrectAns.size() +" alGivenAns.size() ===> "+alGivenAns.size());
//							System.out.println("nCorrectAns ===> " + nCorrectAns +" nGivenAns ===> "+nGivenAns);
							
						} else {
							marks = 0.0d;
						}
						
						
					} else {
						marks = 0.0d;
					}
					
				} else if (uF.parseToInt(innerlist.get(8)) == 10) {
					ansComment = request.getParameter("anscomment" + innerlist.get(0)+"_"+innerlist.get(9));
//					System.out.println("MArks OF ANSTYPE 10 ===> " + request.getParameter("marks" + innerlist.get(0)+"_"+innerlist.get(9)));
//					System.out.println("ANSTYPE 10 ID ===> " + "marks" + innerlist.get(0)+"_"+innerlist.get(9));
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
				
				pst = con.prepareStatement("insert into assessment_question_answer(candidate_id,answer,assessment_details_id,assessment_question_id," +
						"user_id,user_type_id,entry_date,weightage,marks,remark,assessment_question_bank_id,answers_comment," +
						"assessment_section_id,round_id,recruitment_id)values(?,?,?,? ,?,?,?,? ,?,?,?,? ,?,?,?)");
				pst.setInt(1, uF.parseToInt(getCandidateId()));
				pst.setString(2, givenAnswer);
				pst.setInt(3, uF.parseToInt(getAssessmentId()));
				pst.setInt(4, uF.parseToInt(innerlist.get(9)));
				pst.setInt(5, uF.parseToInt(getCandidateId()));
				pst.setInt(6, uF.parseToInt(""));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDouble(8, uF.parseToDouble(weightage));
				pst.setDouble(9, marks);
				pst.setString(10, remark);
				pst.setInt(11, uF.parseToInt(innerlist.get(0)));
				pst.setString(12, ansComment);
				pst.setInt(13, uF.parseToInt(currentLevel));
				pst.setInt(14, uF.parseToInt(getRoundId()));
				pst.setInt(15, uF.parseToInt(getRecruitId()));
				pst.execute();
				pst.close();
//				System.out.println("pst insert ===> " + pst);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
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
			String strTimeDuration = "0";
			
			while (rs.next()) {
				assessmentList.add(rs.getString("assessment_details_id"));
				assessmentList.add(rs.getString("assessment_name"));
				assessmentList.add(hmCourseSubject.get(rs.getString("assessment_subject")));
				assessmentList.add(rs.getString("assessment_author"));
				assessmentList.add(rs.getString("assessment_version"));
				assessmentList.add(rs.getString("assessment_description"));
				
				strTimeDuration = rs.getString("assessment_time_duration"); 				
				assessmentList.add(""+strTimeDuration);
				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from assessment_emp_remain_time where candidate_id=? and learning_plan_id=? and assessment_id=?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.setInt(2, uF.parseToInt("0"));
			pst.setInt(3, uF.parseToInt(getAssessmentId()));
			rs = pst.executeQuery();
			String strTimeSpendDuration = null;
			boolean flag = false;
			while (rs.next()) {
				flag = true;
				strTimeSpendDuration = rs.getString("remaining_time");
			}
			rs.close();
			pst.close();
			
			String remainTime ="0.00";
			if(flag){ 
				strTimeSpendDuration = strTimeSpendDuration.replace(".", ",");
				String strTemp[]=strTimeSpendDuration.split(",");
				int seconds = uF.parseToInt(strTemp[1]);
				int seconds100 = (seconds*100)/60;
				
				remainTime = strTemp[0]+"."+seconds100;
			} else {
				strTimeDuration = uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(strTimeDuration));
				strTimeDuration = strTimeDuration.replace(".", ",");
				String strTemp[]=strTimeDuration.split(",");
				int seconds = uF.parseToInt(strTemp[1]);
				int seconds100 = (seconds*100)/60;
				remainTime = strTemp[0]+"."+seconds100;
			}
//			System.out.println("remainTime ===> " + remainTime);
			request.setAttribute("TIME_DURATION", ""+remainTime);
			request.setAttribute("assessmentList", assessmentList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public String getSectionDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		// boolean flag = false;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmSectionDetails = new HashMap<String, String>();
			int sectionCnt=0;
			if (currentLevel == null || currentLevel.equals("null")) {
				pst = con.prepareStatement("select * from assessment_section_details where assessment_details_id = ? order by assessment_section_id");
			} else {
				pst = con.prepareStatement("select * from assessment_section_details where assessment_details_id = ?  and assessment_section_id = ? order by assessment_section_id");
				pst.setInt(2, uF.parseToInt(currentLevel));
			}
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			rs = pst.executeQuery();
//			System.out.println("pst for section id ====> " +pst);
			String assessSectionId = null;
			while (rs.next()) {
				sectionCnt++;
				assessSectionId = rs.getString("assessment_section_id");
				hmSectionDetails.put("SECTION_NAME", rs.getString("assessment_section_name"));
				hmSectionDetails.put("SECTION_DESC", rs.getString("assessment_section_description"));
				hmSectionDetails.put("MARKS_FOR_SECTION", rs.getString("marks_of_section"));
				hmSectionDetails.put("ATTEMPT_QUE", rs.getString("attempt_questions"));
				hmSectionDetails.put("SECTION_COUNT", sectionCnt+"");
				hmSectionDetails.put("ASSESS_SECTION_ID", rs.getString("assessment_section_id"));
				break;
			}
			rs.close();
			pst.close();
			
//			System.out.println("assessSectionId ====> " +assessSectionId);
			if (assessSectionId == null) {
				return "update";
			}
			request.setAttribute("hmSectionDetails", hmSectionDetails);
			
			getAssessmentQuestionAnswer(uF, uF.parseToInt(currentLevel));

			StringBuilder sb = new StringBuilder("select * from assessment_question_details, assessment_question_bank where assessment_question_bank_id = question_bank_id and assessment_details_id = ? and assessment_section_id = ?");
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.setInt(2, uF.parseToInt(getCurrentLevel()));
			rs = pst.executeQuery();
			List<List<String>> questionList = new ArrayList<List<String>>();
			while (rs.next()) {
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
//				hmAssessmentQueData.put(rs.getString("assessment_section_id"), questionList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("questionList ===> " + questionList);
			
			request.setAttribute("questionList", questionList);
			setCurrentLevel(assessSectionId);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "success";
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

	
	
	private void checkCurrentLevelExistForCurrentEmp(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {
			String sectionCount="0";
			pst = con.prepareStatement("select count(distinct(assessment_section_id)) as sectionCnt from assessment_question_answer where candidate_id = ? and assessment_details_id =? and user_type_id = ?" +
			" and user_id=? and round_id=? and recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getCandidateId()));
			pst.setInt(2, uF.parseToInt(getAssessmentId()));
			pst.setInt(3, uF.parseToInt("0"));
			pst.setInt(4, uF.parseToInt(getCandidateId()));
			pst.setInt(5, uF.parseToInt(getRoundId()));
			pst.setInt(6, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
//			System.out.println("pst === > "+pst);
			while (rs.next()) {
				sectionCount = rs.getString("sectionCnt");
			}
			rs.close();
			pst.close();
			
//			System.out.println("sectionCount ===> " + sectionCount);
			request.setAttribute("sectionCount", sectionCount);
//			request.setAttribute("existLevelFlag", flag);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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

	public String getLevelCount() {
		return levelCount;
	}

	public void setLevelCount(String levelCount) {
		this.levelCount = levelCount;
	}

	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}

	public String getStrDomain() {
		return strDomain;
	}
	
	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}

	public String getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getRoundId() {
		return roundId;
	}

	public void setRoundId(String roundId) {
		this.roundId = roundId;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	
	@Override
	public void run() {/*
	
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		Map<String, String> hmReviewData = getReviewDetails(con);
		
		Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);

//		 System.out.println("allIdList ==> "+ allIdList);
		// String strDomain = request.getServerName().split("\\.")[0];
//		for (int i = 0; allIdList != null && !allIdList.isEmpty() && i < allIdList.size(); i++) {
//			if(allIdList.get(i) != null && !allIdList.get(i).equals("")){
				Map<String, String> hmEmpInner = hmEmpInfo.get(strSessionEmpId);
				Map<String, String> hmRevieweeInner = hmEmpInfo.get(empID);
//				 System.out.println(i+" allIdList "+allIdList.get(i));
	//			String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_EMP_REVIW_SUBMITED, CF);
				nF.setDomain(getStrDomain());
				nF.setStrEmpId(strSessionEmpId);
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrContextPath(request.getContextPath());
	
				if(empID != null && strSessionEmpId != null && empID.equals(strSessionEmpId)){
					nF.setStrRevieweeName("SELF");
				}else{
					nF.setStrRevieweeName(hmRevieweeInner.get("FNAME")+" "+hmRevieweeInner.get("LNAME"));
				}
				nF.setStrReviewName(hmReviewData.get("REVIEW_NAME"));
				nF.setStrReviewStartdate(uF.getDateFormat(hmReviewData.get("REVIEW_STARTDATE"), DBDATE, CF.getStrReportDateFormat()));
				nF.setStrReviewEnddate(uF.getDateFormat(hmReviewData.get("REVIEW_ENDDATE"), DBDATE, CF.getStrReportDateFormat()));
	
				nF.setStrEmpFname(hmEmpInner.get("FNAME"));
				nF.setStrEmpLname(hmEmpInner.get("LNAME"));
				nF.setEmailTemplate(true);
				
				nF.sendNotifications();
//			}
//		}
		db.closeConnection(con);
	*/}
}
