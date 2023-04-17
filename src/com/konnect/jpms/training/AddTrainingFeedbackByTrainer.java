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
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddTrainingFeedbackByTrainer extends ActionSupport implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeID;
	CommonFunctions CF;
	
	private String trainingId;
	private String lPlanId;
	private String empID;
	private String[] trainerId;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserTypeID = (String) session.getAttribute(USERTYPEID);
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return "login";
		request.setAttribute(PAGE, "/jsp/training/AddTrainingFeedbackByTrainer.jsp");
		request.setAttribute(TITLE, "Training Feedback Form");
		UtilityFunctions uF = new UtilityFunctions();
		System.out.println("trainingId ====> " + trainingId);
		System.out.println("lPLanId ====> " + lPlanId);
		System.out.println("empID ====> " + empID);
		System.out.println("trainerId ====> " + trainerId);
		getTrainingFeedbackQuestion(uF);
		getFeedbackQuestionAnswer(uF);
		getQuestionSubType(uF);
		
		String btnfinish = request.getParameter("btnfinish");
		if (btnfinish != null) {
			insertMarks(uF);
			return SUCCESS;
		} 
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
			
			pst = con.prepareStatement("select * from training_question_details tqd, training_question_bank tqb where tqd.plan_id = ? and question_for = 1 and tqd.question_id = tqb.training_question_bank_id");
			pst.setInt(1, uF.parseToInt(trainingId));
			rs = pst.executeQuery();
			System.out.println("pst ===> " + pst);
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
			
			System.out.println("questionDetailsList ===> " + questionDetailsList);
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

			pst = con.prepareStatement("select * from training_question_answer  where learning_plan_id=? and plan_id=? and emp_id=? and user_id=?");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			pst.setInt(2, uF.parseToInt(getTrainingId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
//			System.out.println("pst2 ===> " + pst);
			rs = pst.executeQuery();
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


	private void insertMarks(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		List<List<String>> questionDetailsList = (List<List<String>>) request.getAttribute("questionDetailsList");
		
		try {
		
					pst = con.prepareStatement("delete from training_question_answer where emp_id=? and user_id =? and plan_id=? and learning_plan_id = ? and user_type_id =?");
					pst.setInt(1, uF.parseToInt(getEmpID()));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setInt(3, uF.parseToInt(getTrainingId()));
					pst.setInt(4, uF.parseToInt(getlPlanId()));
					pst.setInt(5, uF.parseToInt(strSessionUserTypeID));
					pst.execute();
					pst.close();
					
//					System.out.println("pst delete ===> " + pst);
					for (int i = 0; questionDetailsList != null && i < questionDetailsList.size(); i++) {
						List<String> innerlist = (List<String>) questionDetailsList.get(i);
		
						String weightage = innerlist.get(10);
						String givenAnswer = null;
						double marks = 0;
						String remark = null;
		
						if (uF.parseToInt(innerlist.get(8)) == 1) {
							String[] correct = request.getParameterValues("correct" + innerlist.get(1)+"_"+ innerlist.get(2));
							remark = request.getParameter("" + innerlist.get(1)+"_"+ innerlist.get(2));
							String correctanswer = innerlist.get(9);
							for (int k = 0; correct != null && k < correct.length; k++) {
								if (k == 0) {
									givenAnswer = correct[k];
								} else {
									givenAnswer += "," + correct[k];
								}
							}
							if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
								marks = uF.parseToDouble(weightage);
							}
		
						} else if (uF.parseToInt(innerlist.get(8)) == 2) {
							String[] correct = request.getParameterValues("correct" + innerlist.get(1)+"_"+ innerlist.get(2));
							for (int k = 0; correct != null && k < correct.length; k++) {
								if (k == 0) {
									givenAnswer = correct[k];
								} else {
									givenAnswer += "," + correct[k];
								}
							}
							String correctanswer = innerlist.get(9);
		
							if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
								marks = uF.parseToDouble(weightage);
							} else {
								marks = 0;
							} 
		
						} else if (uF.parseToInt(innerlist.get(8)) == 3) {
		
							marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(1)+"_"+ innerlist.get(2)));
						} else if (uF.parseToInt(innerlist.get(8)) == 4) {
		
							givenAnswer = request.getParameter("" + innerlist.get(1)+"_"+ innerlist.get(2));
							marks = (uF.parseToDouble(givenAnswer) * uF.parseToDouble(weightage)) / 100;
		
						} else if (uF.parseToInt(innerlist.get(8)) == 5) {
							givenAnswer = request.getParameter("" + innerlist.get(1)+"_"+ innerlist.get(2));
							String answer = innerlist.get(9);
							if (givenAnswer != null && answer != null && answer.contains(givenAnswer)) {
								marks = uF.parseToDouble(weightage);
							}
						} else if (uF.parseToInt(innerlist.get(8)) == 6) {
							givenAnswer = request.getParameter("" + innerlist.get(1)+"_"+ innerlist.get(2));
							String answer = innerlist.get(9);
							if (givenAnswer != null && answer != null && answer.contains(givenAnswer)) {
								marks = uF.parseToDouble(weightage);
							}
		
						} else if (uF.parseToInt(innerlist.get(8)) == 7) {
		
							givenAnswer = request.getParameter("" + innerlist.get(1)+"_"+ innerlist.get(2));
							marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(1)+"_"+ innerlist.get(2)));
							weightage = request.getParameter("outofmarks" + innerlist.get(1)+"_"+ innerlist.get(2));
		
						} else if (uF.parseToInt(innerlist.get(8)) == 8) {
		
							givenAnswer = request.getParameter("correct" + innerlist.get(1)+"_"+ innerlist.get(2));
							String correctanswer = innerlist.get(9);
							if (givenAnswer != null && correctanswer != null && correctanswer.contains(givenAnswer)) {
								marks = uF.parseToDouble(weightage);
							}
		
						} else if (uF.parseToInt(innerlist.get(8)) == 9) {
		
							String[] correct = request.getParameterValues("correct" + innerlist.get(1)+"_"+ innerlist.get(2));
		
							for (int k = 0; correct != null && k < correct.length; k++) {
								if (k == 0) {
									givenAnswer = correct[k];
								} else {
									givenAnswer += "," + correct[k];
								}
							}
		
							String correctanswer = innerlist.get(9);
		
							if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
								marks = uF.parseToDouble(weightage);
							}
		
						} else if (uF.parseToInt(innerlist.get(8)) == 10) {
		
							marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(1)+"_"+ innerlist.get(2)));
							String a = request.getParameter("a" + innerlist.get(1)+"_"+ innerlist.get(2));
							String b = request.getParameter("b" + innerlist.get(1)+"_"+ innerlist.get(2));
							String c = request.getParameter("c" + innerlist.get(1)+"_"+ innerlist.get(2));
							String d = request.getParameter("d" + innerlist.get(1)+"_"+ innerlist.get(2));
		
							givenAnswer = uF.showData(a, "") + " :_:" + uF.showData(b, "") + " :_:" + uF.showData(c, "") + " :_: " + uF.showData(d, "");
		
						} else if (uF.parseToInt(innerlist.get(8)) == 11) {
							String rating = request.getParameter("gradewithrating" + innerlist.get(1)+"_"+ innerlist.get(2));
		
							marks = uF.parseToDouble(rating) * uF.parseToDouble(weightage) / 5;
		
						}  else if (uF.parseToInt(innerlist.get(8)) == 12) {
							givenAnswer = request.getParameter("" + innerlist.get(1)+"_"+ innerlist.get(2));
						}
						
												
						pst = con.prepareStatement("insert into training_question_answer(emp_id,answer,plan_id,question_id,user_id,user_type_id," +
										"attempted_on,weightage,marks,remark,training_question_id,learning_plan_id)values(?,?,?,?,?,?,?,?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getEmpID()));
						pst.setString(2, givenAnswer);
						pst.setInt(3, uF.parseToInt(getTrainingId()));
						pst.setInt(4, uF.parseToInt(innerlist.get(2)));
						pst.setInt(5, uF.parseToInt(strSessionEmpId));
						pst.setInt(6, uF.parseToInt(strSessionUserTypeID));
						pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDouble(8, uF.parseToDouble(weightage));
						pst.setDouble(9, marks);
						pst.setString(10, remark);
						pst.setInt(11, uF.parseToInt(innerlist.get(1)));
						pst.setInt(12, uF.parseToInt(getlPlanId()));
						pst.execute();
						pst.close();
		//				System.out.println("pst insert ===> " + pst);
					}
					sendMail(con, getlPlanId(), getTrainingId(), getEmpID());
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void sendMail(Connection con, String learningPlanId, String trainingId, String empId) {

		ResultSet rs = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
//			System.out.println("Req id is ========= "+getStrId());
			String lPlanName = "";
			pst = con.prepareStatement("select learning_plan_name from learning_plan_details where learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(learningPlanId));
			rs = pst.executeQuery();
			while (rs.next()) {
				lPlanName = rs.getString("learning_plan_name");
			}
			rs.close();
			pst.close();
			
			String trainingName = CF.getTrainingNameByTrainingId(con, uF, trainingId);
			if(lPlanName != null && !lPlanName.equals("")) {
				Map<String, String> hmEmpHrId = CF.getEmpHRIdMap(con, uF);
				String empHr = hmEmpHrId.get(empId);
				Map<String, String> hmEmpInner1 = hmEmpInfo.get(empId);
				StringBuilder sbLearnerName = new StringBuilder();
				sbLearnerName.append(hmEmpInner1.get("FNAME")+" "+hmEmpInner1.get("LNAME"));
				
				Map<String, String> hmEmpInner = hmEmpInfo.get(strSessionEmpId);
				StringBuilder sbTrainerName = new StringBuilder();
				sbTrainerName.append(hmEmpInner.get("FNAME")+" "+hmEmpInner.get("LNAME"));
				
				String strDomain = request.getServerName().split("\\.")[0];
				if(uF.parseToInt(empHr)>0) {
					Notifications nF = new Notifications(N_L_TRAINING_FEEDBACK_FROM_TRAINER_TO_HR, CF); 
		//			System.out.println("Emp ID is ========= "+rst1.getString("panel_emp_id"));
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrEmpId(empHr);
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					if(trainingName != null && !trainingName.equals("")) {
						nF.setStrTrainingName(trainingName);
					}
					nF.setStrLearnersName(sbLearnerName.toString());
					nF.setStrTrainerName(sbTrainerName.toString());
					nF.setStrLearningPlanName(lPlanName);
					nF.setEmailTemplate(true);
					nF.sendNotifications();
				}
				
				Notifications nF1 = new Notifications(N_L_TRAINING_FEEDBACK_FROM_TRAINER_EMP, CF); 
	//			System.out.println("Emp ID is ========= "+rst1.getString("panel_emp_id"));
				nF1.setDomain(strDomain);
				nF1.request = request;
				nF1.setStrEmpId(empId);
				nF1.setStrHostAddress(CF.getStrEmailLocalHost());
				nF1.setStrHostPort(CF.getStrHostPort());
				nF1.setStrContextPath(request.getContextPath());
				if(trainingName != null && !trainingName.equals("")) {
					nF1.setStrTrainingName(trainingName);
				}
				nF1.setStrTrainerName(sbTrainerName.toString());
				nF1.setStrLearningPlanName(lPlanName);
				nF1.setStrEmpFname(hmEmpInner1.get("FNAME"));
				nF1.setStrEmpLname(hmEmpInner1.get("LNAME"));
				nF1.setEmailTemplate(true);
				nF1.sendNotifications();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
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
		
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from training_answer_type_sub order by training_answer_type_sub_id"); //select * from appraisal_answer_type_sub order by appraisal_answer_type_sub_id
//			System.out.println("pst 3 ===> " + pst);
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
