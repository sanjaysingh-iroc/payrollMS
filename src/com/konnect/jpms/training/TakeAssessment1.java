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

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class TakeAssessment1 implements ServletRequestAware, IStatements, Runnable {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeID;
	String strUserType =  null;
	CommonFunctions CF;
	private String assessmentId;
	private String lPlanId;
	
	private String empID;
	private String currentLevel;
	private String userType;
	private String levelCount;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strSessionUserTypeID = (String) session.getAttribute(USERTYPEID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return "login";
		strUserType = (String)session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, "/jsp/training/TakeAssessment1.jsp");
		request.setAttribute(TITLE, "Take Assessment");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		getanswerTypeMap(uF);
		getSectionStep(uF);
		getQuestionSubType(uF);
		getAssessmentData(uF);

		
		if(levelCount == null){
			levelCount = "1";
		}else{
			int cnt = uF.parseToInt(levelCount);
			cnt++;
			setLevelCount(""+cnt);
		}
		/*System.out.println("assessmentId ===> " + assessmentId);
		System.out.println("lPlanId ===> " + lPlanId);
		
		System.out.println("getCurrentLevel() ===> "+getCurrentLevel());*/
		
		String submit = request.getParameter("submit");
		String btnfinish = request.getParameter("btnfinish");
		String finishType = request.getParameter("finishType");
		String levelAppSystem = request.getParameter("levelAppSystem");
		/*System.out.println("finishType ===> "+finishType);
		System.out.println("btnfinish ========= > " + btnfinish);*/
		/*System.out.println("levelCount ===> " + levelCount);
		
		System.out.println("submit ========= > " + submit);
		
		System.out.println("getCurrentLevel ========= > " + getCurrentLevel());*/
		
		if ((submit == null || submit.trim().equals("")) && (btnfinish == null || btnfinish.trim().equals("")) && (finishType == null || finishType.trim().equals(""))) {
			getSctionStatus(uF);
			checkCurrentLevelExistForCurrentEmp(uF);
			return getSectionDetails(uF);

		} else {
//			System.out.println("levelCount ========== > " + levelCount);
			getPreviousSectionData(uF);	
			//getAssessmentQuestionAnswer(uF);
			if(getCurrentLevel() !=null && uF.parseToInt(levelCount) > 2){

				insertMarks(uF);
			}
			if(uF.parseToInt(levelCount)>2){
			if (swapLevelId(uF)) {
				insertTakeAssessmentAttempt(uF);
//				System.out.println("swapLevelId(uF) ===> " + levelCount);
//				String btnSubmit = request.getParameter("btnfinish");
				if(btnfinish != null && btnfinish.equals("Finish")){
//					String strDomain = request.getServerName().split("\\.")[0];
//					setDomain(strDomain);
//					Thread th = new Thread(this);
//					th.start();
					return "finish";
				} else if(finishType != null && finishType.trim().equalsIgnoreCase("finish")){
					return "finish";
				}else{
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

	
	private void insertTakeAssessmentAttempt(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("insert into assessment_take_attempt_details(assessment_details_id,take_attempt_count,assessment_section_ids," +
					"added_by,entry_date,learning_plan_id,emp_id)values(?,?,?,?, ?,?,?)");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.setInt(2, uF.parseToInt("1"));
			pst.setString(3, "");
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(getlPlanId()));
			pst.setInt(7, uF.parseToInt(strSessionEmpId));
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
				// request.setAttribute( rs.getString("appraisal_level_id"),
				// rs.getString("appraisal_level_id"));
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

			pst = con.prepareStatement("select * from assessment_question_answer where assessment_details_id=? and learning_plan_id=? and emp_id=? and user_id=? and assessment_section_id = ?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.setInt(2, uF.parseToInt(getlPlanId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, sectionId);
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

			pst = con.prepareStatement("select assessment_section_id from assessment_question_answer where assessment_details_id = ? and emp_id = ? " +
					"and user_id = ? and user_type_id = ? group by assessment_section_id order by assessment_section_id");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt("0"));
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
//			mainLevelList.add("0");
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
	
	
//	public void getLevelStep(UtilityFunctions uF) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//
//		try {
//			con = db.makeConnection(con);
////			List<String> levelList = new ArrayList<String>();
//			pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
//			pst.setInt(1, uF.parseToInt(getId()));
//			rs = pst.executeQuery();
//			Map<String,Map<String,String>> hmSubsection=new HashMap<String,Map<String,String>>();
//			while (rs.next()) {
//				Map<String,String> hmSubsectionDetails =new HashMap<String,String>();
//				hmSubsectionDetails.put("LEVEL_NAME", rs.getString("level_title"));
//				hmSubsectionDetails.put("LEVEL_SDESC", rs.getString("short_description"));
//				hmSubsectionDetails.put("LEVEL_LDESC", rs.getString("long_description"));
//				hmSubsectionDetails.put("LEVEL_APPSYSTEM", rs.getString("appraisal_system"));
//				hmSubsectionDetails.put("APP_LEVEL_ID", rs.getString("appraisal_level_id"));
//				// request.setAttribute( rs.getString("appraisal_level_id"),
//				// rs.getString("appraisal_level_id"));
//				hmSubsection.put(rs.getString("appraisal_level_id"), hmSubsectionDetails);
//			}
//			request.setAttribute("hmSubsection", hmSubsection);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}

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
				/*List<List<String>> questionList = hmAssessmentQueData.get(rs.getString("assessment_section_id"));
				if(questionList == null){
					questionList = new ArrayList<List<String>>();
				}*/
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_bank_id"));
				innerList.add(rs.getString("question_text"));
				innerList.add(rs.getString("option_a")); //2
				innerList.add(rs.getString("option_b")); //3
				innerList.add(rs.getString("option_c")); //4
				innerList.add(rs.getString("option_d")); //5
				innerList.add(rs.getString("correct_ans")); //6
				innerList.add(rs.getString("weightage")); //7
				innerList.add(rs.getString("answer_type")); //8
				innerList.add(rs.getString("assessment_question_id")); //9
				innerList.add(rs.getString("is_add")); //10
				innerList.add(rs.getString("assessment_section_id")); //11
				innerList.add(rs.getString("assessment_details_id")); //12
				innerList.add(rs.getString("que_matrix_heading")); //13
				innerList.add(rs.getString("que_attached_file")); //14
				
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
		con = db.makeConnection(con);
		try {
			
			pst = con.prepareStatement("delete from assessment_question_answer where emp_id=? and user_id =? and assessment_details_id=? and learning_plan_id = ? and assessment_section_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setInt(3, uF.parseToInt(getAssessmentId()));
			pst.setInt(4, uF.parseToInt(getlPlanId()));
			pst.setInt(5, uF.parseToInt(currentLevel));
			pst.execute();
			pst.close();
			
			List<List<String>> questionDetailsList = (List<List<String>>) request.getAttribute("questionList");
			
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
					
//					System.out.println("correctanswer ===> " + correctanswer +" givenAnswer ===> "+givenAnswer);
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
						//	System.out.println("alCorrectAns.size() ===> " + alCorrectAns.size() +" alGivenAns.size() ===> "+alGivenAns.size());
						//	System.out.println("nCorrectAns ===> " + nCorrectAns +" nGivenAns ===> "+nGivenAns);
							
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

				} else if (uF.parseToInt(innerlist.get(8)) == 12) {
					ansComment = request.getParameter("anscomment" + innerlist.get(0)+"_"+innerlist.get(9));
					givenAnswer = request.getParameter("" + innerlist.get(0)+"_"+innerlist.get(9));
					
				} else if (uF.parseToInt(innerlist.get(8)) == 14) {
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
					}
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
				pst.setInt(14, uF.parseToInt(currentLevel));
				
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

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
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
//			System.out.println("assessmentList ===> " + assessmentList.toString());
//			System.out.println("nTimeDuration ===> " + nTimeDuration);
			
			pst = con.prepareStatement("select * from assessment_emp_remain_time where emp_id=? and learning_plan_id=? and assessment_id=?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(getlPlanId()));
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
			//Map<String, String> hmOrientMemberID = getOrientMemberID();
			int sectionCnt=0;
//			System.out.println("currentLevel ====> " + currentLevel);
			if (currentLevel == null || currentLevel.equals("null")) {
//				pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
				pst = con.prepareStatement("select * from assessment_section_details where assessment_details_id = ? order by assessment_section_id");
			} else {
//				pst = con.prepareStatement("select * from appraisal_level_details where appraisal_id=?  and appraisal_level_id=? order by appraisal_level_id");
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
//			Map<String, List<List<String>>> hmAssessmentQueData = new HashMap<String, List<List<String>>>();
//			List<List<String>> outerList = new ArrayList<List<String>>();
			List<List<String>> questionList = new ArrayList<List<String>>();
			while (rs.next()) {
				/*List<List<String>> questionList = hmAssessmentQueData.get(rs.getString("assessment_section_id"));
				if(questionList == null){
					questionList = new ArrayList<List<String>>();
				}*/
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
				innerList.add(rs.getString("que_matrix_heading")); //13
				innerList.add(rs.getString("que_attached_file")); //14
				
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

//	public Map<String, String> getAppraisalQuestionMap(UtilityFunctions uF) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rs = null;
//		Map<String, String> AppraisalQuestion = new HashMap<String, String>();
//		Map<String, List<String>> hmQuestion = new HashMap<String, List<String>>();
//		try {
//
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from question_bank qb, appraisal_question_details aqd where qb.question_bank_id=aqd.question_id and appraisal_id=?");
//			pst.setInt(1, uF.parseToInt(getId()));
//			rs = pst.executeQuery();
//			List<List<String>> outerList = new ArrayList<List<String>>();
//
//			while (rs.next()) {
//				List<String> innerList = new ArrayList<String>();
//				innerList.add(rs.getString("question_bank_id"));
//				innerList.add(rs.getString("question_text"));
//				innerList.add(rs.getString("option_a"));
//				innerList.add(rs.getString("option_b"));
//				innerList.add(rs.getString("option_c"));
//				innerList.add(rs.getString("option_d"));
//				innerList.add(rs.getString("correct_ans"));
//				innerList.add(rs.getString("is_add"));
//				innerList.add(rs.getString("question_type"));
//				innerList.add(rs.getString("appraisal_question_details_id"));
//				
//				outerList.add(innerList);
//				hmQuestion.put(rs.getString("question_bank_id"), innerList);
//
//				AppraisalQuestion.put(rs.getString("question_bank_id"), rs.getString("question_text"));
//
//			}
//			request.setAttribute("hmQuestion", hmQuestion);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return AppraisalQuestion;
//	}

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

//	public Map<String, String> getLevelMap(UtilityFunctions uF) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map<String, String> hmLevelMap = new HashMap<String, String>();
//		Database db = new Database();
//		db.setRequest(request);
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement(selectLevel);
//			rs = pst.executeQuery();
//
//			while (rs.next()) {
//				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//		return hmLevelMap;
//	}

	private void getQuestionSubType(UtilityFunctions uF) {
		Map<String, List<List<String>>> answertypeSub = new HashMap<String, List<List<String>>>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		con = db.makeConnection(con);

		try {

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
//		boolean flag = false; 
		try {
			/*pst = con.prepareStatement("select * from appraisal_question_answer where emp_id = ? and appraisal_id =? and user_type_id = ?" +
					" and section_id = ? and user_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getUserType()));
			pst.setInt(4, uF.parseToInt(getCurrentLevel()));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
//			System.out.println("pst === > "+pst);
			while (rst.next()) {
				flag = true;
			}*/
			String sectionCount="0";
			pst = con.prepareStatement("select count(distinct(assessment_section_id)) as sectionCnt from assessment_question_answer where emp_id = ? and assessment_details_id =? and user_type_id = ?" +
			" and user_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(getAssessmentId()));
			pst.setInt(3, uF.parseToInt("0"));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
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
	
	
//	private void getFinalResult(){
//		UtilityFunctions uF = new UtilityFunctions();
//		Map<String, List<String>> hmRemainOrientDetails = getSecondMaxOrientNameAndIDs(uF);
//		List<String> listRemainOrientName = hmRemainOrientDetails.get("NAME");
//		List<String> listRemainOrientID = hmRemainOrientDetails.get("ID");
//		
//		Map<String, List<String>> hmExistOrientTypeAQA = getExistOrientTypeInAQA(uF);
//		List<String> listExistOrientTypeInAQA = hmExistOrientTypeAQA.get(getCurrentLevel()+"_"+getEmpID());
//		
//		List<String> listRemainOrientType = new ArrayList<String>();
//		
//		Map<String,List<String>> hmExistUsersAQA = getExistUsersInAQA();
//		Map<String,List<String>> hmOrientTypewiseID = getOrientTypeWiseIds();
//		
//		for(int b = 0; listRemainOrientID != null && b<listRemainOrientID.size();b++){
//			if(listExistOrientTypeInAQA != null){
//			if(!listExistOrientTypeInAQA.contains(listRemainOrientID.get(b))){
//				listRemainOrientType.add(listRemainOrientName.get(b));
//				//sbRemainOrientTypeID.append(listRemainOrientID.get(b)+",");
//			}else{
//				List<String> listExistUserInAQA = hmExistUsersAQA.get(getCurrentLevel()+"_"+listRemainOrientID.get(b));
//				List<String> listIds = hmOrientTypewiseID.get(getId()+"_"+listRemainOrientID.get(b));
//				boolean flag = false;
//				for(int a = 0; listIds != null && a<listIds.size();a++){
//					if(listExistUserInAQA != null){
//					if(!listExistUserInAQA.contains(listIds.get(a))){
//						flag = true;
//					}
//					}
//				}
//					if(flag == true){
//						listRemainOrientType.add(listRemainOrientName.get(b));
//						//sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b)+",");
//					}
//			}
//			}else{
//				listRemainOrientType.add(listRemainOrientName.get(b));
//				//sbRemainOrientTypeIDForSelf.append(listRemainOrientIDForSelf.get(b)+",");
//			}
//		}
//		request.setAttribute("listRemainOrientType", listRemainOrientType);
//	}
	
	
//	private Map<String,List<String>> getExistUsersInAQA() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rs = null;
//		
//		Map<String,List<String>> hmExistUsersAQA = new HashMap<String, List<String>>();
//		
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select distinct(user_type_id),appraisal_id,section_id,user_id from appraisal_question_answer");
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				List<String> existUsersAQAList = hmExistUsersAQA.get(rs.getString("section_id")+"_"+rs.getString("user_type_id"));
//				if(existUsersAQAList==null) existUsersAQAList = new ArrayList<String>();				
//				existUsersAQAList.add(rs.getString("user_id"));
//				hmExistUsersAQA.put(rs.getString("section_id")+"_"+rs.getString("user_type_id"), existUsersAQAList);
//			}
////			System.out.println("hmExistUsersAQA ========== > "+hmExistUsersAQA);
//			request.setAttribute("hmExistUsersAQA", hmExistUsersAQA);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return hmExistUsersAQA;
//	}
	
	
//	private Map<String,List<String>> getExistOrientTypeInAQA(UtilityFunctions uF) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rs = null;
//		
//		Map<String,List<String>> hmExistOrientTypeAQA = new HashMap<String, List<String>>();
//		
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select distinct(user_type_id),appraisal_id,section_id,emp_id from appraisal_question_answer where appraisal_id = ?");
//			pst.setInt(1, uF.parseToInt(id));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				List<String> existOrientTypeAQAList = hmExistOrientTypeAQA.get(rs.getString("section_id")+"_"+rs.getString("emp_id"));
//				if(existOrientTypeAQAList==null) existOrientTypeAQAList = new ArrayList<String>();				
//				existOrientTypeAQAList.add(rs.getString("user_type_id"));
//				hmExistOrientTypeAQA.put(rs.getString("section_id")+"_"+rs.getString("emp_id"), existOrientTypeAQAList);
//			}
////			System.out.println("hmExistOrientTypeAQA ========== > "+hmExistOrientTypeAQA);
//			request.setAttribute("hmExistOrientTypeAQA", hmExistOrientTypeAQA);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return hmExistOrientTypeAQA;
//	}
	
	
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

	public String getlPlanId() {
		return lPlanId;
	}

	public void setlPlanId(String lPlanId) {
		this.lPlanId = lPlanId;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}


//	private Map<String, String> getReviewDetails(Connection con) {
//		PreparedStatement pst = null;
//		ResultSet rst = null;
//		UtilityFunctions uF = new UtilityFunctions();
//		Map<String, String> hmReviewData = new HashMap<String, String>();
//		try {
//			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
//			pst.setInt(1, uF.parseToInt(id));
//			rst = pst.executeQuery();
//			while (rst.next()) {
//				hmReviewData.put("REVIEW_NAME", rst.getString("appraisal_name"));
//				hmReviewData.put("REVIEW_SELFID", rst.getString("self_ids"));
//				hmReviewData.put("REVIEW_PEERID", rst.getString("peer_ids"));
//				hmReviewData.put("REVIEW_MANAGERID", rst.getString("supervisor_id"));
//				hmReviewData.put("REVIEW_HRID", rst.getString("hr_ids"));
//				hmReviewData.put("REVIEW_OTHERID", rst.getString("other_ids"));
//				hmReviewData.put("REVIEW_STARTDATE", rst.getString("from_date"));
//				hmReviewData.put("REVIEW_ENDDATE", rst.getString("to_date"));
////				hmReviewData.put("REVIEW_ENDDATE", rst.getString(""));
//			}
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return hmReviewData;
//	}
	
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
				
				nF.sendNotifications();
//			}
//		}
		db.closeConnection(con);
	*/}
}
