package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;

public class LearningPlanAssessmentStatus implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
	private String lPlanId;

	private String alertStatus;
	private String alert_type;
	
	private String alertID;
	
	
	public String execute() {
		
		session = request.getSession(); 
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/training/LearningPlanAssessmentStatus.jsp");
		request.setAttribute(TITLE, "Learning Plan Status");

		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
//		if(strSessionUserType != null && strSessionUserType.equals(HRMANAGER) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(HR_LEARNING_FINALIZATION_ALERT)){
//			updateUserAlerts();
//		}
		
		getLearningPlanAssessmentStatus(uF);
		getRemarks();
		getSingleOpenWithoutMarksQueReadUnreadCount(uF);
		getAssessmentTakeAttemptPercent(uF);
		
		getAssessmentIdsData(uF);
		getLearningPlanAssessStatus(uF);
		
		getTrainingIdsData(uF);
		getSystemCompletedStatus(uF);
		getLearningPlanTrainingStatus(uF);
		
		getTrainingUpdateStatus(uF);
		
		getCourseIdsData(uF);
		getChapterCountAndReadCount(uF);
		
		return "success";
	}
	
	
//	private void updateUserAlerts() {
//		Connection con = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
//		try {
//			con = db.makeConnection(con);
//			String strDomain = request.getServerName().split("\\.")[0];
//			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//			userAlerts.setStrDomain(strDomain);
//			userAlerts.setStrEmpId(""+nEmpId);
//			userAlerts.set_type(HR_LEARNING_FINALIZATION_ALERT);
//			userAlerts.setStatus(UPDATE_ALERT);
//			Thread t = new Thread(userAlerts);
//			t.run();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//		}
//	}
	
	
	private void getTrainingUpdateStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;

		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select max(status) as status, training_id from training_status where learning_plan_id=? and emp_id = ? group by training_id");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			Map<String, String> hmTrainingStatus = new HashMap<String, String>();
			while (rst.next()) {
				hmTrainingStatus.put(rst.getString("training_id"), rst.getString("status"));
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select is_completed, training_id from training_status where learning_plan_id=? and emp_id = ? and is_completed = 1");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			Map<String, String> hmTrainingCompletedStatus = new HashMap<String, String>();
			while (rst.next()) {
				hmTrainingCompletedStatus.put(rst.getString("training_id"), rst.getString("is_completed"));
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmTrainingStatus", hmTrainingStatus);
			request.setAttribute("hmTrainingCompletedStatus", hmTrainingCompletedStatus);
//			System.out.println("hmReadUnreadCount =====> "+ hmReadUnreadCount);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}

	
	private void getLearningPlanAssessStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {
//			pst = con.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks) as marks ,sum(weightage) as weightage," +
//					"emp_id,plan_id from training_question_answer where learning_plan_id=? group by emp_id,plan_id)as a");
			pst = con.prepareStatement("select * from(select sum(marks) as marks ,sum(weightage) as weightage," +
					"assessment_details_id,user_type_id,emp_id from assessment_question_answer where learning_plan_id= ? group by user_type_id," +
					"emp_id,assessment_details_id)as a");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
			Map<String, String> hmAssessRateEmpAndAssessIdWise = new HashMap<String, String>();
			while (rs.next()) {
				double dblMarks = uF.parseToDouble(rs.getString("marks"));
//				System.out.println("dblTotalMarks"+dblTotalMarks);
				double dblWeightage = uF.parseToDouble(rs.getString("weightage"));
				String aggregate = "0";
				if(dblWeightage>0){
					aggregate = uF.formatIntoTwoDecimal((dblMarks * 100)/dblWeightage);
				}
				hmAssessRateEmpAndAssessIdWise.put(rs.getString("emp_id")+"_"+rs.getString("assessment_details_id"), aggregate);
			}
			rs.close();
			pst.close();
			
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
			request.setAttribute("hmAssessRateEmpAndAssessIdWise", hmAssessRateEmpAndAssessIdWise);

			Map<String, String> hmMarksGrade = new HashMap<String, String>();
			pst = con.prepareStatement("select assessment_details_id,marks_grade_type,marks_grade_standard from assessment_details ad, " +
					"learning_plan_stage_details lpsd where lpsd.learning_plan_stage_name_id = ad.assessment_details_id and " +
					"lpsd.learning_type ='Assessment' and lpsd.learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmMarksGrade.put("MARKS_GRADE_TYPE", rs.getString("marks_grade_type"));
				hmMarksGrade.put("MARKS_GRADE_STANDARD", rs.getString("marks_grade_standard"));
			}
			rs.close();
			pst.close();

			Map<String, String> hmEmpIdwiseResult = new HashMap<String, String>();
			Iterator<String> it = hmAssessRateEmpAndAssessIdWise.keySet().iterator();
			while(it.hasNext()){
				String key=(String)it.next();
				String value =hmAssessRateEmpAndAssessIdWise.get(key);
//				System.out.println("KEY ===> " + key + "  VALUE ===> " + value);
				
					pst = con.prepareStatement("select * from training_mark_grade_type where grade_standard=? and max_value >= ? and min_value <= ?");
					pst.setInt(1, uF.parseToInt(hmMarksGrade.get("MARKS_GRADE_STANDARD")));
					pst.setDouble(2, uF.parseToDouble(value));
					pst.setDouble(3, uF.parseToDouble(value));
//					System.out.println("pst ===>> " + pst);
					rs = pst.executeQuery();
					
					while (rs.next()) {
						String grade = "";
						if(hmMarksGrade.get("MARKS_GRADE_TYPE").equals("1")){
							grade = rs.getString("numeric_grade_type");
						} else {
							grade = rs.getString("alphabet_grade_type");
						}
						hmEmpIdwiseResult.put(key, grade);
					}
					rs.close();
					pst.close();
			}
//			System.out.println("hmEmpIdwiseResult ===> " + hmEmpIdwiseResult);
			request.setAttribute("hmEmpIdwiseResult", hmEmpIdwiseResult);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void getLearningPlanTrainingStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {

//			pst = con.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks) as marks ,sum(weightage) as weightage," +
//					"emp_id,plan_id from training_question_answer where learning_plan_id=? group by emp_id,plan_id)as a");
			pst = con.prepareStatement("select *,(marks*100/weightage) as average from (select sum(tqa.marks) as marks ,sum(tqa.weightage) as weightage," +
				"tqa.user_type_id,tqa.emp_id,tqa.plan_id from training_question_answer tqa, training_question_details tqd where tqa.learning_plan_id=? " +
				"and tqa.user_id=? and tqa.training_question_id = tqd.training_question_id and tqd.question_for=1 and tqa.weightage>0 " +
				"group by tqa.user_type_id,tqa.emp_id,tqa.plan_id) as a");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
//			System.out.println("hmTrainerRateEmpAndTrainingIdWise PST==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmTrainerRateEmpAndTrainingIdWise = new HashMap<String, String>();
			
			while (rs.next()) {
				
				double dblMarks = uF.parseToDouble(rs.getString("marks"));
//				System.out.println("dblTotalMarks"+dblTotalMarks);
				double dblWeightage = uF.parseToDouble(rs.getString("weightage"));
				String aggregate = "0";
				if(dblWeightage>0){
					aggregate = uF.formatIntoTwoDecimal((dblMarks * 100)/dblWeightage);
				}
				hmTrainerRateEmpAndTrainingIdWise.put(rs.getString("emp_id")+"_"+rs.getString("plan_id"), aggregate);
			}
			rs.close();
			pst.close();
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
			request.setAttribute("hmTrainerRateEmpAndTrainingIdWise", hmTrainerRateEmpAndTrainingIdWise);
			
 			
//			pst = con.prepareStatement("select * from(select sum(tqa.marks) as marks ,sum(tqa.weightage) as weightage," +
//					"tqa.user_type_id,tqa.user_id,tqa.plan_id from training_question_answer tqa, training_question_details tqd " +
//					"where tqa.learning_plan_id=? and tqa.emp_id=? and tqa.training_question_id = tqd.training_question_id and tqd.question_for = 1 " +
//					"group by tqa.user_type_id,tqa.user_id,tqa.plan_id)as a");
//			pst.setInt(1, uF.parseToInt(getlPlanId()));
//			pst.setInt(2, uF.parseToInt(strSessionEmpId));
//			rs = pst.executeQuery();
			pst = con.prepareStatement("select * from(select sum(tqa.marks) as marks ,sum(tqa.weightage) as weightage," +
					"tqa.user_type_id,tqa.user_id,tqa.plan_id from training_question_answer tqa, training_question_details tqd " +
					"where tqa.learning_plan_id=? and tqa.training_question_id = tqd.training_question_id and tqd.question_for = 2 " +
					"and tqa.emp_id in (select trainer_id from training_trainer where trainer_id>0 or emp_id=?)" +
					"group by tqa.user_type_id,tqa.user_id,tqa.plan_id)as a");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();			
//			System.out.println("learner pst =====> " + pst);
			Map<String, String> hmLearnerRateTrainerAndTrainingIdWise = new HashMap<String, String>();
			while (rs.next()) {
				double dblMarks = uF.parseToDouble(rs.getString("marks"));
				double dblWeightage = uF.parseToDouble(rs.getString("weightage"));
				String aggregate = "0";
				if(dblWeightage>0){
					aggregate = uF.formatIntoTwoDecimal((dblMarks * 100)/dblWeightage);
				}
				hmLearnerRateTrainerAndTrainingIdWise.put(rs.getString("user_id")+"_"+rs.getString("plan_id"), aggregate);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmLearnerRateTrainerAndTrainingIdWise", hmLearnerRateTrainerAndTrainingIdWise);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getAssessmentIdsData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_plan_id = ? and learning_type = 'Assessment'");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
			List<List<String>> assessmentIdList = new ArrayList<List<String>>();
//			System.out.println("pst ============ > "+ pst);
			while (rs.next()) {
				String assessName = CF.getAssessmentNameByAssessId(con, uF, rs.getString("learning_plan_stage_name_id"));
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("learning_plan_stage_name_id"));
				innerList.add(assessName);
				assessmentIdList.add(innerList);
			}	
			rs.close();
			pst.close();
			request.setAttribute("assessmentIdList", assessmentIdList);
//			System.out.println("hmReadUnreadCount =====> "+ hmReadUnreadCount);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}

	
	private void getTrainingIdsData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_plan_id = ? and learning_type = 'Training'");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
			List<List<String>> trainingIdList = new ArrayList<List<String>>();
//			System.out.println("pst ============ > "+ pst);
			while (rs.next()) {
				String trainingName = CF.getTrainingNameByTrainingId(con, uF, rs.getString("learning_plan_stage_name_id"));
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("learning_plan_stage_name_id"));
				innerList.add(trainingName);
				trainingIdList.add(innerList);
			}	
			rs.close();
			pst.close();
			request.setAttribute("trainingIdList", trainingIdList);
//			System.out.println("hmReadUnreadCount =====> "+ hmReadUnreadCount);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}
	
//===created by parvez date: 14-02-2023===
	//===start===
	private void getCourseIdsData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_plan_id = ? and learning_type = 'Course'");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
			List<List<String>> courseIdList = new ArrayList<List<String>>();
//			System.out.println("pst ============ > "+ pst);
			while (rs.next()) {
				String assessName = getCourseNameByID(con, uF, rs.getString("learning_plan_stage_name_id"));
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("learning_plan_stage_name_id"));
				innerList.add(assessName);
				courseIdList.add(innerList);
			}	
			rs.close();
			pst.close();
			request.setAttribute("courseIdList", courseIdList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}
	//===end===
	
	private void getSystemCompletedStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmTrainingTotDays = new HashMap<String, String>();
			pst = con.prepareStatement("select count(*) as count, training_id from training_schedule_details group by training_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmTrainingTotDays.put(rs.getString("training_id"), rs.getString("count"));
			}	
			rs.close();
			pst.close();
			
			Map<String, String> hmTrainingCompletedDays = new HashMap<String, String>();
			pst = con.prepareStatement("select count(*) as count, training_id from training_schedule_details where day_date <= ? group by training_id");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmTrainingCompletedDays.put(rs.getString("training_id"), rs.getString("count"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmTrainingTotDays", hmTrainingTotDays);
			request.setAttribute("hmTrainingCompletedDays", hmTrainingCompletedDays);
//			System.out.println("hmReadUnreadCount =====> "+ hmReadUnreadCount);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}
	

	private void getAssessmentTakeAttemptPercent(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			int takeAttemptSum = 0;
			pst = con.prepareStatement("select sum(assessment_take_attempt) as allsum from learning_plan_stage_details lpsd, assessment_details ad " +
					"where lpsd.learning_plan_id = ? and lpsd.learning_plan_stage_name_id = ad.assessment_details_id");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				takeAttemptSum = rs.getInt("allsum");
			}
			rs.close();
			pst.close();
			
			String learnersId = "";
			pst = con.prepareStatement("select learner_ids from learning_plan_details where learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				learnersId = rs.getString("learner_ids");
			}
			rs.close();
			pst.close();
			List<String> learnersList = getAppendData(learnersId);
			Map<String, String> hmAssessmentTakePercent = new HashMap<String, String>();
			for(int j=0; learnersList != null && !learnersList.isEmpty() && j< learnersList.size(); j++) {
				int takeAttemptCount = 0;
				pst = con.prepareStatement("select count(*) as cnt from assessment_take_attempt_details where learning_plan_id = ? and emp_id = ?");
				pst.setInt(1, uF.parseToInt(getlPlanId()));
				pst.setInt(2, uF.parseToInt(learnersList.get(j)));
				rs = pst.executeQuery();
				while (rs.next()) {
					takeAttemptCount = rs.getInt("cnt");
				}
				rs.close();
				pst.close();
				
				double takeAttemptPercent = 0;
				if(takeAttemptCount > 0 && takeAttemptSum > 0) { 
					takeAttemptPercent = (takeAttemptCount*100) / takeAttemptSum;
				}
				hmAssessmentTakePercent.put(learnersList.get(j), ""+takeAttemptPercent);
			}
			
			request.setAttribute("hmAssessmentTakePercent", hmAssessmentTakePercent);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}


	private void getSingleOpenWithoutMarksQueReadUnreadCount(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmReadUnreadCount = new HashMap<String, String>();
			pst = con.prepareStatement("select count(*) as count,aqa.user_type_id,aqa.emp_id,aqa.read_status from assessment_question_answer aqa, " +
				"assessment_question_details aqd where learning_plan_id = ? and aqa.assessment_details_id = aqd.assessment_details_id " +
				"and aqa.assessment_question_bank_id = aqd.question_bank_id and aqd.answer_type = 12 group by aqa.read_status,aqa.user_type_id,aqa.emp_id");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
//			System.out.println("pst ============ > "+ pst);
			while (rs.next()) {
				hmReadUnreadCount.put(rs.getString("user_type_id")+"_"+rs.getString("emp_id")+"_"+rs.getString("read_status"), rs.getString("count"));
			}		
			rs.close();
			pst.close();
			
			request.setAttribute("hmReadUnreadCount", hmReadUnreadCount);
//			System.out.println("hmReadUnreadCount =====> "+ hmReadUnreadCount);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}

	
	private void getRemarks() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		String remark = null;
		String strApprovedBy = null;
		Map<String,String> hmRemark=new HashMap<String, String>();
		boolean flag= false;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
		//===start parvez date: 14-02-2023===	
			pst = con.prepareStatement("select finalize_remark,lpfd.emp_id,lpfd.added_by,emp_fname,emp_mname,emp_lname,learning_plan_id,training_id,assessment_id,course_id" +
				",entry_date from learning_plan_finalize_details lpfd, employee_personal_details epd where learning_plan_id=? and lpfd.added_by = epd.emp_per_id ");
		//===end parvez date: 14-02-2023===	
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				remark = rs.getString("finalize_remark");
				if (remark != null) {
					remark = remark.replace("\n", "<br/>");
				}
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				strApprovedBy = rs.getString("emp_fname") +strEmpMName+ " "
						+ rs.getString("emp_lname");
				String trainingOrAssessIdOrCourseId = "";
				if(rs.getString("training_id") != null && uF.parseToInt(rs.getString("training_id")) > 0) {
					trainingOrAssessIdOrCourseId = rs.getString("training_id");
				} else if(rs.getString("assessment_id") != null && uF.parseToInt(rs.getString("assessment_id")) > 0) {
					trainingOrAssessIdOrCourseId = rs.getString("assessment_id");
			//===start parvez date: 14-02-2023===	
				} else if(rs.getString("course_id") != null && uF.parseToInt(rs.getString("course_id")) > 0) {
					trainingOrAssessIdOrCourseId = rs.getString("course_id");
				}
			//===end parvez date: 14-02-2023===	
				
				hmRemark.put(rs.getString("learning_plan_id")+rs.getString("emp_id")+trainingOrAssessIdOrCourseId, strApprovedBy+" on "+ uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			
			/*request.setAttribute("hrremark", remark);
			request.setAttribute("flag", flag);
			request.setAttribute("strApprovedBy", strApprovedBy);*/
			
			request.setAttribute("hmRemark", hmRemark);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}

	}

	private void getLearningPlanAssessmentStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		String self_ids = null;
		try {

			Map<String, String> hmAttribute = new HashMap<String, String>();
			pst = con.prepareStatement("select arribute_id,attribute_name from appraisal_attribute");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select min(from_date) as minDate, max(to_date) as maxDate from learning_plan_stage_details where learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
//			System.out.println("pst1 =====> " + pst1);
			String minFromDate = null, maxToDate = null; 
			while (rs.next()) {
				minFromDate = rs.getString("minDate");
				maxToDate = rs.getString("maxDate");
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			pst = con.prepareStatement("select * from learning_plan_details where learning_plan_id =?");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
			int memberCount=1;
			Map<String, String> learningPlanMp = new HashMap<String, String>();
			while (rs.next()) {

//				List<String> memberList = new ArrayList<String>();

				learningPlanMp.put("ID", rs.getString("learning_plan_id"));
				learningPlanMp.put("LEARNING_PLAN_NAME", rs.getString("learning_plan_name"));
				learningPlanMp.put("OBJECTIVE", uF.showData(rs.getString("learning_plan_objective"), ""));
				String alignedWith = "";
				if(rs.getString("group_or_condition") != null && rs.getString("group_or_condition").equals("3")) {
					alignedWith = "General";
				} else if(rs.getString("group_or_condition") != null && rs.getString("group_or_condition").equals("2")) {
					alignedWith = "Gap";
				} else if(rs.getString("group_or_condition") != null && rs.getString("group_or_condition").equals("1")) {
					alignedWith = "Induction";
				} 
				learningPlanMp.put("ALIGNED_WITH", uF.showData(alignedWith, ""));
				learningPlanMp.put("CERTIFICATE", uF.showData(CF.getCertificateName(con, rs.getString("certificate_id")), ""));
				learningPlanMp.put("ATTRIBUTE", uF.showData(getAppendData(rs.getString("attribute_id"), hmAttribute),""));
				/*String skills = "";
				if(rs.getString("skills") != null && rs.getString("skills").length() > 1){
					skills = rs.getString("skills").substring(1, rs.getString("skills").length()-1);
				}*/
				learningPlanMp.put("SKILLS", uF.showData(getAppendData(rs.getString("skills"), hmSkillName), ""));
				learningPlanMp.put("FROM", uF.getDateFormat(minFromDate, DBDATE, CF.getStrReportDateFormat()));
				learningPlanMp.put("TO", uF.getDateFormat(maxToDate, DBDATE, CF.getStrReportDateFormat()));
				learningPlanMp.put("LEARNERS_ID", rs.getString("learner_ids"));
				
				learningPlanMp.put("IS_CLOSE", ""+uF.parseToBoolean(rs.getString("is_close")));
//				learner_ids
//				request.setAttribute("memberList", memberList);
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("memberCount", memberCount);
			getEmpWlocation(learningPlanMp.get("LEARNERS_ID"));
			
			String empids=learningPlanMp.get("LEARNERS_ID")!=null && !learningPlanMp.get("LEARNERS_ID").equals("") ? learningPlanMp.get("LEARNERS_ID").substring(1, learningPlanMp.get("LEARNERS_ID").length()-1) : "";
			Map<String, String> empImageMap = new HashMap<String, String>();
			if(empids.length()>0) {
				pst = con.prepareStatement("select emp_image,emp_per_id from employee_personal_details where emp_per_id in(" + empids + ")");
				rs = pst.executeQuery();
				while (rs.next()) {
					empImageMap.put(rs.getString("emp_per_id"), rs.getString("emp_image"));
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("empImageMap", empImageMap);

			request.setAttribute("learningPlanMp", learningPlanMp);

			pst = con.prepareStatement("select learner_ids,learning_plan_id from learning_plan_details where learning_plan_id=?");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				self_ids = rs.getString("learner_ids");
			}
			rs.close();
			pst.close();
			
			self_ids=self_ids!=null && !self_ids.equals("") ? self_ids.substring(1, self_ids.length()-1) :"";
			List<String> empList = Arrays.asList(self_ids.split(","));

			
			/*pst = con.prepareStatement("select *,(marks*100/weightage) as average from(select sum(marks) as marks ,sum(weightage) as weightage," +
					"user_type_id,emp_id from assessment_question_answer where learning_plan_id=?  group by user_type_id,emp_id)as a");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> outerMp = new HashMap<String, Map<String, String>>();
			
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			
			double dblTotalMarks = 0; 
			double dblTotalWeightage = 0;
			
			while (rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					dblTotalMarks = 0;
					dblTotalWeightage = 0;
				}
				
				dblTotalMarks += uF.parseToDouble(rs.getString("marks"));
		//		System.out.println("dblTotalMarks"+dblTotalMarks);
				
				dblTotalWeightage += uF.parseToDouble(rs.getString("weightage"));
				Map<String, String> value = outerMp.get(rs.getString("emp_id"));
				if (value == null)
					value = new HashMap<String, String>();
				value.put(rs.getString("user_type_id"), uF.formatIntoTwoDecimal(rs.getDouble("average")));
				if(dblTotalWeightage>0) {
					value.put("AGGREGATE", uF.formatIntoTwoDecimal((dblTotalMarks * 100)/dblTotalWeightage));
				}
				outerMp.put(rs.getString("emp_id"), value);
				
				strEmpIdOld = strEmpIdNew;
			}*/
			// request.setAttribute("hmUserTypeID", hmUserTypeID);
//			request.setAttribute("outerMp", outerMp);
			request.setAttribute("empList", empList);
			
			
			pst = con.prepareStatement("select count(*)as count,emp_id,assessment_details_id from (select emp_id,assessment_details_id,user_type_id from assessment_question_answer "
					+ "where learning_plan_id=? group by emp_id,user_type_id,assessment_details_id)as a group by emp_id,assessment_details_id");
			pst.setInt(1, uF.parseToInt(getlPlanId()));			
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmEmpCount=new HashMap<String, String>();
			while (rs.next()) {
				hmEmpCount.put(rs.getString("emp_id"), rs.getString("count"));				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpCount", hmEmpCount);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
	}
	
	public void getChapterCountAndReadCount( UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		
		try {
			
			String learnersId = "";
			pst = con.prepareStatement("select learner_ids from learning_plan_details where learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				learnersId = rs.getString("learner_ids");
			}
			rs.close();
			pst.close();
			List<String> learnersList = getAppendData(learnersId);
			StringBuilder sbEmpIds = null;
			for(int i=0; learnersList!=null && i<learnersList.size(); i++){
				if(sbEmpIds == null){
					sbEmpIds = new StringBuilder();
					sbEmpIds.append(learnersList.get(i));
				} else{
					sbEmpIds.append(","+learnersList.get(i));
				}
			}
			
			Map<String, String> hmChapterCount = new HashMap<String, String>();
			pst = con.prepareStatement("select count(*) as count, course_id from course_chapter_details group by course_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmChapterCount.put(rs.getString("course_id"), rs.getString("count"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmChapterCount", hmChapterCount);

			Map<String, String> hmChapterReadCount = new HashMap<String, String>();
			if(sbEmpIds!=null){
				pst = con.prepareStatement("select count(*) as count, course_id, emp_id from course_read_update_details where " +
						" emp_id in ("+sbEmpIds.toString()+") and learning_plan_id = ? group by course_id,emp_id");
				pst.setInt(1, uF.parseToInt(getlPlanId()));
				System.out.println("LPA/793---pst===>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					hmChapterReadCount.put(rs.getString("course_id")+"_"+rs.getString("emp_id"), rs.getString("count"));
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("hmChapterReadCount", hmChapterReadCount);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String getCourseNameByID(Connection con, UtilityFunctions uF, String courseId) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		String courseName = null;
		
		try {
			pst = con.prepareStatement("select course_id,course_name from course_details where course_id=?");
			pst.setInt(1, uF.parseToInt(courseId));
			rst = pst.executeQuery();
			while(rst.next()) {
				courseName = rst.getString("course_name");
			}
			rst.close();
			pst.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return courseName;
	}

	private void getEmpWlocation(String empIds) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			Map<String, String> locationMp = new HashMap<String, String>();
			con = db.makeConnection(con);
//			System.out.println("empIds==>"+empIds);
			if(empIds != null && !empIds.equals("") && !empIds.equalsIgnoreCase("null" )){
				empIds = empIds !=null && !empIds.equals("") ? empIds.substring(1, empIds.length()-1) : "";
				if(empIds.length()>0){
					pst = con.prepareStatement("select eod.wlocation_id,emp_id,wlocation_name from employee_official_details eod,work_location_info wli where eod.wlocation_id=wli.wlocation_id and emp_id in("
									+ empIds + ")");
//					System.out.println("pst==>"+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						locationMp.put(rs.getString("emp_id"), rs.getString("wlocation_name"));
					}
					rs.close();
					pst.close();
				}
			}
			request.setAttribute("locationMp", locationMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}

	
	private List<String> getAppendData(String strID) {
		
		List<String> learnersList = new ArrayList<String>();
		if (strID != null && !strID.equals("") && !strID.isEmpty() && strID.length() >1) {
			if (strID.substring(0, 1).equals(",") && strID.substring(strID.length()-1, strID.length()).equals(",")) {
				strID = strID.substring(1, strID.length()-1);
			}
			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					learnersList.add(temp[i]);
				}
			} else {
				learnersList.add(strID.trim());
				return learnersList;
			}
		}
		return learnersList;
	}
	
	
	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();

		if (strID != null && !strID.equals("")) {
			strID=strID.substring(1, strID.length()-1);
			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						sb.append(", " + mp.get(temp[i].trim()));
					}
				}
			} else {
				return mp.get(strID);
			}

		} else {
			return null;
		}

		return sb.toString();
	}

	public String getlPlanId() {
		return lPlanId;
	}

	public void setlPlanId(String lPlanId) {
		this.lPlanId = lPlanId;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

}
