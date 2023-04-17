package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.EmpDashboard;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddTrainingFeedback extends ActionSupport implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeID;
	CommonFunctions CF;
	
	private String trainingId;
	private String lPlanId;
	private String empID;
	private String[] trainerId;
	
	private String fromPage;
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserTypeID = (String) session.getAttribute(USERTYPEID);
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return "login";
		request.setAttribute(PAGE, "/jsp/training/AddTrainingFeedback.jsp");
		request.setAttribute(TITLE, "Training Feedback Form");
		UtilityFunctions uF = new UtilityFunctions();
		getTrainingFeedbackQuestion(uF);
		getFeedbackQuestionAnswer(uF);
		getQuestionSubType(uF);
		
//		String submit = request.getParameter("submit");
		String btnfinish = request.getParameter("btnfinish");
//		System.out.println("btnfinish==>"+btnfinish);
		if (btnfinish != null) {
			insertMarks(uF);
			if(getFromPage()!= null && getFromPage().equals("MLP")) {
				return SUCCESS;
			} else {
				return VIEW;
			}
		} 
		return LOAD;
	}
	
	private List<String> getTrainersList(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> trainerList = new ArrayList<String>();
		try {
			pst = con.prepareStatement("select * from training_schedule where plan_id = ?");
			pst.setInt(1, uF.parseToInt(trainingId));
			rs = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			String trainerIds = "";
			while (rs.next()) {
				trainerIds = rs.getString("trainer_ids");
			}
			rs.close();
			pst.close();
			
			trainerIds=trainerIds!=null && !trainerIds.equals("") ? trainerIds.substring(1, trainerIds.length()-1) :"";
			trainerList = Arrays.asList(trainerIds.split(","));

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
		return trainerList;
	}
	
	
	private void getTrainingFeedbackQuestion(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			Map<String, String> hmTrainerName = CF.getTrainingTrainerNameMap(con);
			
			request.setAttribute("hmTrainerName", hmTrainerName);
			
			List<String> trainerList = getTrainersList(con, uF);
//			System.out.println("trainerList ===>> " +trainerList);
			
			List<String> existTrainerList = new ArrayList<String>();
			pst = con.prepareStatement("select * from training_question_answer where learning_plan_id=? and plan_id=? and user_id=? and user_type_id=?");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			pst.setInt(2, uF.parseToInt(getTrainingId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(strSessionUserTypeID));
//			System.out.println("existTrainerList pst ===>> " +pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!existTrainerList.contains(rs.getString("emp_id"))) {
					existTrainerList.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("existTrainerList ===>> " + existTrainerList);
			
			List<String> remainingTarinerList = new ArrayList<String>();
			for (int i = 0; trainerList != null && i < trainerList.size(); i++) {
				if(!existTrainerList.contains(trainerList.get(i))) {
					remainingTarinerList.add(trainerList.get(i));
				}
			}
			
//			System.out.println("remainingTarinerList ===>> " + remainingTarinerList);
			
			pst = con.prepareStatement("select * from training_question_details tqd, training_question_bank tqb where tqd.plan_id = ? and question_for = 2 and tqd.question_id = tqb.training_question_bank_id");
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
			request.setAttribute("remainingTarinerList", remainingTarinerList);
			request.setAttribute("existTrainerList", existTrainerList);

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
			List<String> trainerList = getTrainersList(con, uF);
			Map<String, Map<String, Map<String, String>>> trainerQueAnsMp = new HashMap<String, Map<String, Map<String, String>>>();
			for(int i =0; trainerList != null && !trainerList.isEmpty() && i<trainerList.size(); i++) {
				pst = con.prepareStatement("select * from training_question_answer  where learning_plan_id=? and plan_id=? and emp_id=? and user_id=?");
				pst.setInt(1, uF.parseToInt(getlPlanId()));
				pst.setInt(2, uF.parseToInt(getTrainingId()));
				pst.setInt(3, uF.parseToInt(trainerList.get(i)));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				rs = pst.executeQuery();
				Map<String, Map<String, String>> queAnswerMp = new HashMap<String, Map<String, String>>();
	
				while (rs.next()) {
					Map<String, String> innerMp = new HashMap<String, String>();
					innerMp.put("ANSWER", rs.getString("answer"));
					innerMp.put("REMARK", rs.getString("remark"));
					innerMp.put("MARKS", rs.getString("marks"));
					innerMp.put("TRAINING_QUE_ANS_ID", rs.getString("training_question_answer_id"));
					queAnswerMp.put(rs.getString("training_question_id") + "question" + rs.getString("question_id"), innerMp);
				}
				rs.close();
				pst.close();
				
				trainerQueAnsMp.put(trainerList.get(i), queAnswerMp);
			}
//			System.out.println("questionanswerMp ===> " + questionanswerMp);
			request.setAttribute("trainerQueAnsMp", trainerQueAnsMp);

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
//		Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
//		trainerId = request.getParameterValues("status");
		
		try {
//			System.out.println("TrainerId Length ===> " + trainerId.length);
			for(int j=0; trainerId != null && j<trainerId.length; j++) {
				if(!trainerId[j].trim().equals("")) {
//					System.out.println("TrainerId ===> " + trainerId[j]);
					pst = con.prepareStatement("delete from training_question_answer where emp_id=? and user_id =? and plan_id=? and learning_plan_id = ? and user_type_id =?");
					pst.setInt(1, uF.parseToInt(trainerId[j]));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setInt(3, uF.parseToInt(getTrainingId()));
					pst.setInt(4, uF.parseToInt(getlPlanId()));
					pst.setInt(5, uF.parseToInt(strSessionUserTypeID));
					pst.execute();
					pst.close();
					
		//			System.out.println("pst delete ===> " + pst);
					for (int i = 0; questionDetailsList != null && i < questionDetailsList.size(); i++) {
						List<String> innerlist = (List<String>) questionDetailsList.get(i);
		//				System.out.println("1 innerlist.get(0)=====>"+innerlist.get(0));
		//				List<String> questioninnerList = hmQuestion.get(innerlist.get(1));
		
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
						
						  //training_question_answer_id serial NOT NULL,training_question_id
						  //emp_id,answer,plan_id,question_id,user_id,user_type_id,attempted_on,weightage,marks,remark,training_question_id
						
						pst = con.prepareStatement("insert into training_question_answer(emp_id,answer,plan_id,question_id,user_id,user_type_id," +
										"attempted_on,weightage,marks,remark,training_question_id,learning_plan_id)values(?,?,?,?,?,?,?,?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(trainerId[j]));
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
//						System.out.println("pst==>"+pst);
						pst.execute();
						pst.close();
						
		//				System.out.println("pst insert ===> " + pst);
					}
				}
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

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	
}
