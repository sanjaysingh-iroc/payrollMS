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

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MyLearningPlan extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	String strOrgId = null;		//===added by parvez date: 01-10-2021
	CommonFunctions CF=null;
	
	private String alertStatus;
	private String alert_type;
	
	private static Logger log = Logger.getLogger(MyLearningPlan.class);
	
	private String dataType;	//added by parvez date: 27-09-2021
	private String alertID;
	private String fromPage;
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
		return LOGIN;
		}
		/* created by seema */
		if(request.getParameter("download")!=null && !request.getParameter("download").isEmpty()){
			request.setAttribute("download", request.getParameter("download"));			
		}
		/* created by seema */
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		//===start parvez date: 01-10-2021===
		strOrgId = (String)session.getAttribute(ORGID);
		//===end parvez date: 01-10-2021===
		
		request.setAttribute(PAGE, "/jsp/training/MyLearningPlan.jsp");
		request.setAttribute(TITLE, "My Learning Plan");
		
		//===start parvez date: 27-09-2021===
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("L");
		}
		//===end parvez date: 27-09-2021===
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		prepareLearning();
		
		if(getDataType() != null && getDataType().equalsIgnoreCase("LC")){
			getAlLearning();
		}
		//System.out.println("MLP/68--getFromPage learning==>"+getFromPage());
		if(getFromPage() != null && getFromPage().equalsIgnoreCase("MyHR") ) {
			return VIEW;
		}
		
		return SUCCESS;
	
	}


	private void getCertificatesThumbsups(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> hmMyLearnCertiAndThumbsups = new HashMap<String, String>();
			pst = con.prepareStatement("select * from learning_plan_finalize_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
//				takeAttemptSum = rs.getInt("allsum");
				String trainingOrAssessmentId = null;
				
				if(rs.getString("training_id") != null && uF.parseToInt(rs.getString("training_id"))>0) {
					trainingOrAssessmentId = rs.getString("training_id");
				} else if(rs.getString("assessment_id") != null && uF.parseToInt(rs.getString("assessment_id"))>0) {
					trainingOrAssessmentId = rs.getString("assessment_id");
				} else if(rs.getString("course_id") != null && uF.parseToInt(rs.getString("course_id"))>0) {
					trainingOrAssessmentId = rs.getString("course_id");
				}
				hmMyLearnCertiAndThumbsups.put(rs.getString("learning_plan_id")+"_"+trainingOrAssessmentId+"_CERTI", rs.getString("certificate_status"));
				hmMyLearnCertiAndThumbsups.put(rs.getString("learning_plan_id")+"_"+trainingOrAssessmentId+"_THUMBSUP", rs.getString("thumbsup_status"));
				hmMyLearnCertiAndThumbsups.put(rs.getString("learning_plan_id")+"_"+trainingOrAssessmentId+"_THUMBSDOWN", rs.getString("send_to_gap_status"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmMyLearnCertiAndThumbsups", hmMyLearnCertiAndThumbsups);
//			System.out.println("hmMyLearnCertiAndThumbsups =====> "  +hmMyLearnCertiAndThumbsups);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	private String checkLearningIsNew(String lPlanID) {
		
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		String learningStatus = "0";
		try {
			con=db.makeConnection(con);
			
			pst = con.prepareStatement("select * from training_attend_details where emp_id = ? and added_by = ? and learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setInt(3, uF.parseToInt(lPlanID));
//			System.out.println("pst =====>> " +pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				learningStatus = "1";
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from course_read_details where emp_id = ? and added_by = ? and learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setInt(3, uF.parseToInt(lPlanID));
//			System.out.println("pst =====>> " +pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				learningStatus = "1";
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from assessment_question_answer where emp_id=? and user_id=? and learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setInt(3, uF.parseToInt(lPlanID));
//			System.out.println("pst =====>> " +pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				learningStatus = "1";
			}
			rs.close();
			pst.close();
			
			//===start parvez date:23-09-2021===
			pst = con.prepareStatement("select * from learning_video_seen_details where emp_id=? and added_by=? and learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setInt(3, uF.parseToInt(lPlanID));
//			System.out.println("pst =====>> " +pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				learningStatus = "1";
			}
			
			rs.close();
			pst.close();
			//===end parvez date: 23-09-2021===
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return learningStatus;
	}
	
	
	
	private String getAssessmentTakeAttemptPercent(UtilityFunctions uF, String assessmentId, String lPlanId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		String strTakeAttemptPercent ="";
		try {
			con = db.makeConnection(con);
			int takeAttemptSum = 0;
			pst = con.prepareStatement("select sum(assessment_take_attempt) as allsum from assessment_details ad where ad.assessment_details_id = ?");
			pst.setInt(1, uF.parseToInt(assessmentId));
			rs = pst.executeQuery();
			while (rs.next()) {
				takeAttemptSum = rs.getInt("allsum");
			}
			rs.close();
			pst.close();
			
			//System.out.println("takeAttemptSum ===> " + takeAttemptSum);
			int takeAttemptCount = 0;
			pst = con.prepareStatement("select count(*) as cnt from assessment_take_attempt_details where assessment_details_id=? and emp_id=? and learning_plan_id=?");
			pst.setInt(1, uF.parseToInt(assessmentId));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setInt(3, uF.parseToInt(lPlanId));
			rs = pst.executeQuery();
			//System.out.println("pst ===>  " + pst);
			while (rs.next()) {
				takeAttemptCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			double takeAttemptPercent = 0;
			if(takeAttemptCount > 0 && takeAttemptSum > 0) { 
				takeAttemptPercent = (takeAttemptCount*100) / takeAttemptSum;
			}
//			request.setAttribute("takeAttemptPercent", takeAttemptPercent);
			//System.out.println("takeAttemptPercent =====> "+ takeAttemptPercent);
			if(takeAttemptPercent > 100) {
				takeAttemptPercent = 100;
			}
			strTakeAttemptPercent = ""+takeAttemptPercent;	
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
		return strTakeAttemptPercent;
	}
	
	
	private void prepareLearning() {
		
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null, pst1 = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rst=null, rst1 = null;
		try {
			con=db.makeConnection(con);
			
			Map<String,String> hmwlocation = CF.getWLocationMap(con, null, null);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			List<List<String>> alFutureLearnings=new ArrayList<List<String>>();
			List<List<String>> alAbsentLearnings=new ArrayList<List<String>>();
			List<List<String>> alPreviousLearnings=new ArrayList<List<String>>();
			List<List<String>> alLiveLearnings=new ArrayList<List<String>>();
			getCourseNameByID(con, uF);
			//===start parvez date: 23-09-2021===
			getVideoNameByID(con,uF);
			//===end parvez date: 23-09-2021===
			getCertificatesThumbsups(con, uF);
			
			Map<String, String> hmLearningPlanAttend = new HashMap<String, String>();
//			pst=con.prepareStatement("select * from learning_plan_details lpd where lpd.learner_ids LIKE '%,"+strSessionEmpId+",%' and lpd.learning_plan_id in" +
//					"(select distinct learning_plan_id from learning_plan_stage_details lpsd where ? between from_date and to_date) ");
			pst = con.prepareStatement("select * from learning_plan_details lpd where lpd.learner_ids LIKE '%,"+strSessionEmpId+",%' and is_publish = true");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rst = pst.executeQuery();
			while(rst.next()) {
			
				pst1 = con.prepareStatement("select min(from_date) as minDate, max(to_date) as maxDate from learning_plan_stage_details where learning_plan_id = ?");
//				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst1.setInt(1, rst.getInt("learning_plan_id"));
				rst1 = pst1.executeQuery();
//				System.out.println("pst1 =====> " + pst1);
				String minFromDate = null, maxToDate = null; 
				while (rst1.next()) {
					minFromDate = rst1.getString("minDate");
					maxToDate = rst1.getString("maxDate");
				}
				rst1.close();
				pst1.close();
//				System.out.println("minFromDate ===> " + minFromDate + " maxToDate ===> " + maxToDate);
//				System.out.println("uF.getCurrentDate(CF.getStrTimeZone()) ===> " + uF.getCurrentDate(CF.getStrTimeZone()));
				String fromDateDiff = "0";
				String toDateDiff = "0";
				
				if(minFromDate != null && maxToDate != null && !minFromDate.equals("") && !maxToDate.equals("")){
					fromDateDiff = uF.dateDifference(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, minFromDate, DBDATE);
					toDateDiff = uF.dateDifference(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, maxToDate, DBDATE);
				}
//				System.out.println("fromDateDiff ===> " + fromDateDiff + " toDateDiff ===> " + toDateDiff);
				List<String> lTypeAndIdList = getLearningStageType(con, uF, rst.getInt("learning_plan_id"));
				if(uF.parseToInt(fromDateDiff) <= 1 && uF.parseToInt(toDateDiff) >= 1) {
//					System.out.println("Live Plan IDS ===> " + rst.getInt("learning_plan_id"));
					List<String> innerList = new ArrayList<String>();
					innerList.add(rst.getString("learning_plan_id"));
					innerList.add(rst.getString("learning_plan_name"));
					innerList.add(lTypeAndIdList.get(0)); // L Type
					innerList.add(uF.showData(CF.getCertificateName(con, rst.getString("certificate_id")),"No Certificate")); // Certificate
					innerList.add(uF.getDateFormat(minFromDate, DBDATE, CF.getStrReportDateFormat())); //S Date
					innerList.add(uF.getDateFormat(maxToDate, DBDATE, CF.getStrReportDateFormat())); //E Date
					innerList.add(hmEmpName.get(rst.getString("added_by"))); //Created By
					innerList.add(rst.getString("learning_plan_objective")); //Reason For
					innerList.add(lTypeAndIdList.get(1)); // L ID
					alLiveLearnings.add(innerList);
					
				} else if(uF.parseToInt(fromDateDiff) < 1 && uF.parseToInt(toDateDiff) < 1) {
//					System.out.println("Previous Plan IDS ===> " + rst.getInt("learning_plan_id"));
					List<String> innerList = new ArrayList<String>();
					innerList.add(rst.getString("learning_plan_id"));
					innerList.add(rst.getString("learning_plan_name"));
					innerList.add(lTypeAndIdList.get(0)); // L Type
					innerList.add(uF.showData(CF.getCertificateName(con, rst.getString("certificate_id")),"No Certificate")); // Certificate
					innerList.add(uF.getDateFormat(minFromDate, DBDATE, CF.getStrReportDateFormat())); //S Date
					innerList.add(uF.getDateFormat(maxToDate, DBDATE, CF.getStrReportDateFormat())); //E Date
					innerList.add(hmEmpName.get(rst.getString("added_by"))); //Created By
					innerList.add(rst.getString("learning_plan_objective")); //Reason For
					innerList.add(lTypeAndIdList.get(1)); // L ID
					alPreviousLearnings.add(innerList);
					
				} else if(uF.parseToInt(fromDateDiff) > 1 && uF.parseToInt(toDateDiff) > 1) {
//					System.out.println("Future Plan IDS ===> " + rst.getInt("learning_plan_id"));
					List<String> innerList = new ArrayList<String>();
					innerList.add(rst.getString("learning_plan_id"));
					innerList.add(rst.getString("learning_plan_name"));
					innerList.add(lTypeAndIdList.get(0)); // L Type
					innerList.add(uF.showData(CF.getCertificateName(con, rst.getString("certificate_id")),"No Certificate")); // Certificate
					innerList.add(uF.getDateFormat(minFromDate, DBDATE, CF.getStrReportDateFormat())); //S Date
					innerList.add(uF.getDateFormat(maxToDate, DBDATE, CF.getStrReportDateFormat())); //E Date
					innerList.add(hmEmpName.get(rst.getString("added_by"))); //Created By
					innerList.add(rst.getString("learning_plan_objective")); //Reason For
					innerList.add(lTypeAndIdList.get(1)); // L ID
			//===start parvez date: 30-09-2021===
//					innerList.add(rst.getString("learning_request_status")); 		//9
			//===end parvez date: 30-09-2021===
					alFutureLearnings.add(innerList);
				}
				
			
				String learningStatus = checkLearningIsNew(rst.getString("learning_plan_id"));
				hmLearningPlanAttend.put(rst.getString("learning_plan_id"), learningStatus);
	//			planIdList.add(rst.getString("plan_id"));
	//			
	//			alInner.add(rst.getString("plan_id"));
	//			alInner.add(rst.getString("training_title"));
	//			alInner.add((String)hmwlocation.get(rst.getString("wlocation_id")));
	//			alInner.add(uF.getDateFormat(rst.getString("start_date"), DBDATE, DATE_FORMAT));  
	//			alInner.add(uF.getDateFormat(rst.getString("end_date"), DBDATE, DATE_FORMAT));  
	// 
	//			String datediff=uF.dateDifference(uF.getCurrentDate(CF.getStrTimeZone()).toString(), DBDATE,rst.getString("end_date") , DBDATE);
	//			if(uF.parseToInt(datediff)<0)
	//			alAbsentLearnings.add(alInner);
	//				else
	//			alFutureLearnings.add(alInner);
			
			}
			rst.close();
			pst.close();
			
			getLearningStageTypeDetails(con, uF);
			getTrainingAttendStatus(con, uF);
			getCourseReadStatus(con, uF);
			getTrainingCompletedStatus(con, uF);
			getChapterCountAndReadCount(con, uF);
			
			getAssessmentRating(con, uF);
			//===start parvez date: 23-09-2021===
			getVideoSeenStatus(con, uF);
			//===end parvez date: 23-09-2021===
			
			//===start parvez date: 22-10-2021===
			getVideoCountAndVideoViewedCount(con, uF);
			//===end parvez date: 22-10-2021===

//			System.out.println("hmLearningPlanAttend === >>> " + hmLearningPlanAttend);
			request.setAttribute("alPreviousLearnings", alPreviousLearnings);
			request.setAttribute("alFutureLearnings", alFutureLearnings);
			request.setAttribute("alAbsentLearnings", alAbsentLearnings);
			request.setAttribute("alLiveLearnings", alLiveLearnings);
			request.setAttribute("hmLearningPlanAttend", hmLearningPlanAttend);
			
			List<String> alCloseLearnPlan = new ArrayList<String>();
			pst = con.prepareStatement("select learning_plan_id from learning_plan_details where is_close = true");
			rst = pst.executeQuery();
			while (rst.next()) {
				if(!alCloseLearnPlan.contains(rst.getString("learning_plan_id"))){
					alCloseLearnPlan.add(rst.getString("learning_plan_id"));
				}
			}
			rst.close();
			pst.close();
			request.setAttribute("alCloseLearnPlan", alCloseLearnPlan);
			
				
			pst = con.prepareStatement("select * from assessment_details");
			rst = pst.executeQuery();
			Map<String, String> hmAssessmentAttempt = new HashMap<String, String>();
			while (rst.next()) {
				hmAssessmentAttempt.put(rst.getString("assessment_details_id"), ""+uF.parseToInt(rst.getString("assessment_take_attempt")));
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select count(assessment_details_id) as cnt,learning_plan_id,assessment_details_id " +
					"from assessment_take_attempt_details where emp_id=? group by learning_plan_id,assessment_details_id");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			Map<String, String> hmAssessmentTaken = new HashMap<String, String>();
			while (rst.next()) {
				hmAssessmentTaken.put(rst.getString("learning_plan_id")+"_"+rst.getString("assessment_details_id"), ""+uF.parseToInt(rst.getString("cnt")));
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from assessment_emp_remain_time where emp_id=? and remaining_time='00.00'");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			List<String> alAssessmentFinish = new ArrayList<String>();
			while (rst.next()) {
				alAssessmentFinish.add(rst.getString("learning_plan_id")+"_"+rst.getString("assessment_id"));
			}
			rst.close();
			pst.close();
			
			
//			System.out.println("alAssessmentFinish======>"+alAssessmentFinish.toString()); 
			
			request.setAttribute("hmAssessmentAttempt", hmAssessmentAttempt);
			request.setAttribute("hmAssessmentTaken", hmAssessmentTaken);
			request.setAttribute("alAssessmentFinish", alAssessmentFinish);	
			
			
				
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst1);
			db.closeResultSet(rst);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
			
		}
	}
	
	
	private void getAssessmentRating(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select *,(marks*100/weightage) as average from (select sum(marks) as marks ,sum(weightage) as weightage," + //
					"user_type_id,emp_id,learning_plan_id,assessment_details_id from assessment_question_answer where emp_id = ? and weightage>0" +
					"group by user_type_id,emp_id,learning_plan_id,assessment_details_id) as a"); 
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			Map<String, String> hmAssessmentRating = new HashMap<String, String>();
			
			while (rs.next()) {
//				double dblTotalMarks = uF.parseToDouble(rs.getString("marks"));
//				double dblTotalWeightage = uF.parseToDouble(rs.getString("average"));
				hmAssessmentRating.put(rs.getString("learning_plan_id")+"_"+rs.getString("assessment_details_id"), uF.formatIntoTwoDecimal(rs.getDouble("average")));
//				outerMp.put(rs.getString("emp_id"), outerMp);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAssessmentRating", hmAssessmentRating);
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


	public void getChapterCountAndReadCount(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
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
			pst = con.prepareStatement("select count(*) as count, course_id, learning_plan_id from course_read_update_details where emp_id = ? group by course_id,learning_plan_id");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmChapterReadCount.put(rs.getString("course_id")+"_"+rs.getString("learning_plan_id"), rs.getString("count"));
			}
			rs.close();
			pst.close();
			
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
	
	
	
private void getCourseReadStatus(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rst=null;
		try {
			Map<String, String> hmcourseReadStatus = new HashMap<String, String>();
			pst = con.prepareStatement("select course_read_status,course_id,learning_plan_id from course_read_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			while(rst.next()) {
				hmcourseReadStatus.put(rst.getString("course_id")+"_"+rst.getString("learning_plan_id"), rst.getString("course_read_status"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmcourseReadStatus =====> " + hmcourseReadStatus);
			request.setAttribute("hmcourseReadStatus", hmcourseReadStatus);
			
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
	}


private void getCourseNameByID(Connection con, UtilityFunctions uF) {
	
	PreparedStatement pst = null;
	ResultSet rst = null;
	try {
		Map<String, String> hmCourseName = new HashMap<String, String>();
		pst = con.prepareStatement("select course_id,course_name from course_details");
		rst = pst.executeQuery();
		while(rst.next()) {
			hmCourseName.put(rst.getString("course_id"), rst.getString("course_name"));
		}
		rst.close();
		pst.close();
//		System.out.println("hmTrainingAttendStatus =====> " + hmTrainingAttendStatus);
		request.setAttribute("hmCourseName", hmCourseName);
		
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
}

	
private void getTrainingAttendStatus(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rst=null;
		try {
			Map<String, String> hmTrainingAttendStatus = new HashMap<String, String>();
			pst = con.prepareStatement("select attend_status,training_id,learning_plan_id from training_attend_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			while(rst.next()) {
				hmTrainingAttendStatus.put(rst.getString("training_id")+"_"+rst.getString("learning_plan_id"), rst.getString("attend_status"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmTrainingAttendStatus =====> " + hmTrainingAttendStatus);
			request.setAttribute("hmTrainingAttendStatus", hmTrainingAttendStatus);
			
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
	}


private void getTrainingCompletedStatus(Connection con, UtilityFunctions uF) {
	
	PreparedStatement pst = null;
	ResultSet rst=null;
	try {
		Map<String, String> hmTrainingStatus = new HashMap<String, String>();
//		pst = con.prepareStatement("select max(status) as status,training_id,learning_plan_id from training_status where emp_id = ? group by training_id,learning_plan_id");
//		pst.setInt(1, uF.parseToInt(strSessionEmpId));
		pst = con.prepareStatement("select max(status) as status,training_id,learning_plan_id from training_status where " +
				"learning_plan_id in (select learning_plan_id from learning_plan_details where learner_ids like'%,"+strSessionEmpId+",%') group by training_id,learning_plan_id");
		rst = pst.executeQuery();
		while(rst.next()) {
			hmTrainingStatus.put(rst.getString("training_id")+"_"+rst.getString("learning_plan_id"), rst.getString("status"));
		}
		rst.close();
		pst.close();
		
		Map<String, String> hmTrainingCompleteStatus = new HashMap<String, String>();
//		pst = con.prepareStatement("select is_completed,training_id,learning_plan_id from training_status where emp_id = ? and is_completed = 1");
//		pst.setInt(1, uF.parseToInt(strSessionEmpId));
		pst = con.prepareStatement("select is_completed,training_id,learning_plan_id from training_status where is_completed = 1 " +
				"and learning_plan_id in (select learning_plan_id from learning_plan_details where learner_ids like'%,"+strSessionEmpId+",%')");
		
		rst = pst.executeQuery();
		while(rst.next()) {
			hmTrainingCompleteStatus.put(rst.getString("training_id")+"_"+rst.getString("learning_plan_id"), rst.getString("is_completed"));
		}
		rst.close();
		pst.close();
		
//		System.out.println("hmTrainingCompleteStatus =====> " + hmTrainingCompleteStatus);
//		System.out.println("hmTrainingStatus =====> " + hmTrainingStatus);
		request.setAttribute("hmTrainingCompleteStatus", hmTrainingCompleteStatus);
		request.setAttribute("hmTrainingStatus", hmTrainingStatus);
		
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
}

	
	private void getLearningStageTypeDetails(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rst=null;
		StringBuilder learningType = new StringBuilder();
		
		try {
			Map<String,String> hmwlocation = CF.getWLocationMap(con, null, null);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmAssessmentTakePercent = new HashMap<String, String>();
			
			Map<String, List<List<String>>> hmStageType = new HashMap<String, List<List<String>>>();
			Map<String, String> hmStageTypeId = new HashMap<String, String>();
			pst = con.prepareStatement("select * from learning_plan_stage_details");
			rst = pst.executeQuery();
			 List<List<String>> learningTypeList = new ArrayList<List<String>>();
			while(rst.next()) {
				learningTypeList = hmStageType.get(rst.getString("learning_plan_id"));
				if(learningTypeList == null ) learningTypeList = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("learning_plan_stage_id"));//0
				innerList.add(rst.getString("learning_plan_stage_name"));//1
				innerList.add(uF.getDateFormat(rst.getString("from_date"), DBDATE, CF.getStrReportDateFormat())); //S Date//2
				innerList.add(uF.getDateFormat(rst.getString("to_date"), DBDATE, CF.getStrReportDateFormat())); //E Date//3
				innerList.add(rst.getString("learning_type"));//4
				innerList.add(rst.getString("learning_plan_id"));//5
				innerList.add(hmEmpName.get(rst.getString("added_by")));//6
				innerList.add(rst.getString("learning_plan_stage_name_id"));//7
				
				learningTypeList.add(innerList);
				hmStageType.put(rst.getString("learning_plan_id"), learningTypeList);
				
				hmStageTypeId.put(rst.getString("learning_plan_id")+"_"+rst.getString("learning_type"), rst.getString("learning_plan_stage_name_id"));
				if(rst.getString("learning_type") != null && rst.getString("learning_type").equals("Assessment")){
					String strTakeAttemptPercent = getAssessmentTakeAttemptPercent(uF, rst.getString("learning_plan_stage_name_id"), rst.getString("learning_plan_id"));
					hmAssessmentTakePercent.put(rst.getString("learning_plan_stage_name_id"), strTakeAttemptPercent);
				}
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmAssessmentTakePercent", hmAssessmentTakePercent);
			//System.out.println("hmAssessmentTakePercent =====> " + hmAssessmentTakePercent);
			request.setAttribute("hmStageType", hmStageType);
//			System.out.println("hmStageTypeId =====> " + hmStageTypeId);
			request.setAttribute("hmStageTypeId", hmStageTypeId);
			
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
	}

	
	private List<String> getLearningStageType(Connection con, UtilityFunctions uF, int learningPlanId) {
		
		PreparedStatement pst = null;
		ResultSet rst=null;
//		StringBuilder learningType = new StringBuilder();
		List<String> lTypeAndId = new ArrayList<String>();
		try {
//			Map<String,String> hmwlocation = CF.getWLocationMap(con, null, null);
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_plan_id = ?");
			pst.setInt(1, learningPlanId);
			rst = pst.executeQuery();
			 List<List<String>> learningTypeList = new ArrayList<List<String>>();
			while(rst.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("learning_type"));
				innerList.add(rst.getString("learning_plan_stage_name_id"));
				learningTypeList.add(innerList);
			}
			rst.close();
			pst.close();
			
			int a=0,b=0,c=0,d=0;
			String learningStageID = "";
			//System.out.println("MLP/788--learningTypeList="+learningTypeList);
			for (int i = 0; learningTypeList != null && !learningTypeList.isEmpty() && i < learningTypeList.size(); i++) {
				List<String> innerList = learningTypeList.get(i);
				if(innerList.get(0).equals("Training")){
					learningStageID = innerList.get(1);
					a++;
				} else if(innerList.get(0).equals("Course")){
					learningStageID = innerList.get(1);
					b++;
				}  else if(innerList.get(0).equals("Assessment")){
					learningStageID = innerList.get(1);
					c++;
				//===start parvez date: 23-09-2021===
				} else if(innerList.get(0).equals("Video")){
					learningStageID = innerList.get(1);
					d++;
				}
				//===end parvez date: 23-09-2021===
			}
			
				/*if(a == 1 && b == 0 && c == 0) {
//					learningType.append("Training");
					lTypeAndId.add("Training");
					lTypeAndId.add(learningStageID);
				} else if(a > 1 && b == 0 && c == 0) {
//					learningType.append(a+" Trainings");
					lTypeAndId.add("Trainings");
					lTypeAndId.add("0");
				} else if(a == 0 && b == 1 && c == 0) {
//					learningType.append("Course");
					lTypeAndId.add("Course");
					lTypeAndId.add(learningStageID);
				} else if(a == 0 && b > 1 && c == 0) {
//					learningType.append(b+" Courses");
					lTypeAndId.add("Courses");
					lTypeAndId.add("0");
				} else if(a == 0 && b == 0 && c == 1) {
//					learningType.append("Assessment");
					lTypeAndId.add("Assessment");
					lTypeAndId.add(learningStageID);
				} else if(a == 0 && b == 0 && c > 1) {
//					learningType.append(c+" Assessments");
					lTypeAndId.add("Assessments");
					lTypeAndId.add("0");
				} else if((a >= 1 && b >= 1 && c >= 1) || (a >= 1 && b >= 1 && c == 0) || (a >= 1 && b == 0 && c >= 1) || (a == 0 && b >= 1 && c >= 1)) {
//					learningType.append("Hybrid");
					lTypeAndId.add("Hybrid");
					lTypeAndId.add("0");
				} else {
					lTypeAndId.add("");
					lTypeAndId.add("0");
				}*/
			
			//===start parvez date: 23-09-2021===
				if(a == 1 && b == 0 && c == 0 && d == 0) {
	//				learningType.append("Training");
					lTypeAndId.add("Training");
					lTypeAndId.add(learningStageID);
				} else if(a > 1 && b == 0 && c == 0 && d == 0) {
	//				learningType.append(a+" Trainings");
					lTypeAndId.add("Trainings");
					lTypeAndId.add("0");
				} else if(a == 0 && b == 1 && c == 0 && d == 0) {
	//				learningType.append("Course");
					lTypeAndId.add("Course");
					lTypeAndId.add(learningStageID);
				} else if(a == 0 && b > 1 && c == 0 && d == 0) {
	//				learningType.append(b+" Courses");
					lTypeAndId.add("Courses");
					lTypeAndId.add("0");
				} else if(a == 0 && b == 0 && c == 1 && d == 0) {
	//				learningType.append("Assessment");
					lTypeAndId.add("Assessment");
					lTypeAndId.add(learningStageID);
				} else if(a == 0 && b == 0 && c > 1 && d == 0) {
	//				learningType.append(c+" Assessments");
					lTypeAndId.add("Assessments");
					lTypeAndId.add("0");
				} else if(a == 0 && b == 0 && c == 0 && d== 1) {
	//				learningType.append("Video");
					lTypeAndId.add("Video");
					lTypeAndId.add(learningStageID);
				} else if(a == 0 && b == 0 && c == 0 && d > 1) {
	//				learningType.append(b+" Video");
					lTypeAndId.add("Video");
					lTypeAndId.add("0");
				} else if((a >= 1 && b >= 1 && c >= 1) || (a >= 1 && b >= 1 && c == 0) || (a >= 1 && b == 0 && c >= 1) || (a == 0 && b >= 1 && c >= 1)) {
	//				learningType.append("Hybrid");
					lTypeAndId.add("Hybrid");
					lTypeAndId.add("0");
				} else {
					lTypeAndId.add("");
					lTypeAndId.add("0");
				}
			//===end parvez date: 23-09-2021===
//				System.out.println("MLP/912--lTypeAndId == > " + lTypeAndId);
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
		return lTypeAndId;
	}
	
	
	/*private void prepareLearning() {
	
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rst=null;
		try {
			con=db.makeConnection(con);
			
			Map<String,String> hmwlocation = CF.getWLocationMap(con, null, null);
			
			List<List<String>> alFutureLearnings=new ArrayList<List<String>>();
			List<List<String>> alAbsentLearnings=new ArrayList<List<String>>();
			List<List<String>> alPreviousLearnings=new ArrayList<List<String>>();
			List<List<String>> alLiveLearnings=new ArrayList<List<String>>();
			
			List<String> planIdList=new ArrayList<String>();
			
			pst=con.prepareStatement(" select * from training_schedule join training_plan using(plan_id) " +
					"where emp_ids LIKE '%,"+strSessionEmpId.trim()+",%' and plan_id not in(select plan_id from training_learnings where emp_id="+strSessionEmpId.trim()+") ");
			rst=pst.executeQuery();
			 
			while(rst.next()) {
			
			List<String> alInner=new ArrayList<String>();
			
			planIdList.add(rst.getString("plan_id"));
			
			alInner.add(rst.getString("plan_id"));
			alInner.add(rst.getString("training_title"));
			alInner.add((String)hmwlocation.get(rst.getString("wlocation_id")));
			alInner.add(uF.getDateFormat(rst.getString("start_date"), DBDATE, DATE_FORMAT));  
			alInner.add(uF.getDateFormat(rst.getString("end_date"), DBDATE, DATE_FORMAT));  
 
			String datediff=uF.dateDifference(uF.getCurrentDate(CF.getStrTimeZone()).toString(), DBDATE,rst.getString("end_date") , DBDATE);
			if(uF.parseToInt(datediff)<0)
			alAbsentLearnings.add(alInner);
				else
			alFutureLearnings.add(alInner);
			
			
		}
			
			//prepare past learnings ***********
			pst=con.prepareStatement(" select ts.plan_id,tp.training_title,ts.wlocation_id,ts.start_date,ts.end_date,tl.status,tl.is_completed " +
					"from training_schedule ts join training_plan tp using(plan_id)" +
					"join training_learnings tl using(plan_id) " +
					"where emp_ids LIKE '%,"+strSessionEmpId.trim()+",%' " +
							"and plan_id in(select plan_id from training_learnings " +
							"where emp_id="+strSessionEmpId.trim()+ ")  ");
			
			rst=pst.executeQuery();
			
			while(rst.next()){
			
			List<String> alInner=new ArrayList<String>();
			
			alInner.add(rst.getString("plan_id"));
			alInner.add(rst.getString("training_title"));
			alInner.add((String)hmwlocation.get(rst.getString("wlocation_id")));
			alInner.add(uF.getDateFormat(rst.getString("start_date"), DBDATE, DATE_FORMAT));  
			alInner.add(uF.getDateFormat(rst.getString("end_date"), DBDATE, DATE_FORMAT));
			alInner.add(rst.getString("status"));
		
			if(rst.getInt("is_completed")==1)
			alPreviousLearnings.add(alInner);
			else
			alLiveLearnings.add(alInner);

			}
			
			Map<String,String> hmLearnerTotal=new HashMap<String,String>();
			pst=con.prepareStatement("select ((marks/weightage)*100) as total,plan_id " +
					" from (select sum(marks)as marks,sum(weightage)as weightage,plan_id from training_question_answer " +
					" where emp_id=? and emp_id=user_id group by plan_id order by plan_id) a");
			pst.setInt(1,uF.parseToInt(strSessionEmpId.trim()));
				rst=pst.executeQuery();
			while(rst.next()){
				 hmLearnerTotal.put(rst.getString("plan_id"), rst.getString("total"));
			 }			
			request.setAttribute("hmLearnerTotal", hmLearnerTotal);
			
			request.setAttribute("alPreviousLearnings", alPreviousLearnings);
			request.setAttribute("alFutureLearnings", alFutureLearnings);
			request.setAttribute("alAbsentLearnings", alAbsentLearnings);
			request.setAttribute("alLiveLearnings", alLiveLearnings);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}*/
	
//===start parvez date: 23-09-2021===	
	private void getVideoSeenStatus(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rst=null;
		try {
			Map<String, String> hmVideoSeenStatus = new HashMap<String, String>();
			pst = con.prepareStatement("select learning_video_seen_status,learning_video_id,learning_plan_id from learning_video_seen_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			while(rst.next()) {
				hmVideoSeenStatus.put(rst.getString("learning_video_id")+"_"+rst.getString("learning_plan_id"), rst.getString("learning_video_seen_status"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmVideoSeenStatus =====> " + hmVideoSeenStatus);
			request.setAttribute("hmVideoSeenStatus", hmVideoSeenStatus);
			
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
	}

	private void getVideoNameByID(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			Map<String, String> hmVideoName = new HashMap<String, String>();
			pst = con.prepareStatement("select learning_video_id,learning_video_title from learning_video_details");
			rst = pst.executeQuery();
			while(rst.next()) {
				hmVideoName.put(rst.getString("learning_video_id"), rst.getString("learning_video_title"));
			}
			rst.close();
			pst.close();
	//		System.out.println("hmTrainingAttendStatus =====> " + hmTrainingAttendStatus);
			request.setAttribute("hmVideoName", hmVideoName);
			
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
	}

//===end parvez date: 23-09-2021===
	
	//===start parvez date: 01-10-2021===
	private void getAlLearning() {
		
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null, pst1 = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rst=null, rst1 = null;
		
		try {
//			System.out.println("MLP/1124--empId="+strSessionEmpId);
			con=db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			List<List<String>> alOrgLearnings=new ArrayList<List<String>>();
			
			pst = con.prepareStatement("select * from learning_plan_details lpd where lpd.learner_ids NOT LIKE '%,"+strSessionEmpId+",%' and is_publish = true");
			rst = pst.executeQuery();
			while(rst.next()) {
				pst1 = con.prepareStatement("select min(from_date) as minDate, max(to_date) as maxDate from learning_plan_stage_details where learning_plan_id = ?");
				pst1.setInt(1, rst.getInt("learning_plan_id"));
				rst1 = pst1.executeQuery();
				String minFromDate = null, maxToDate = null;
				while (rst1.next()) {
					minFromDate = rst1.getString("minDate");
					maxToDate = rst1.getString("maxDate");
				}
				rst1.close();
				pst1.close();
				
				String fromDateDiff = "0";
				String toDateDiff = "0";
				
				if(minFromDate != null && maxToDate != null && !minFromDate.equals("") && !maxToDate.equals("")){
					fromDateDiff = uF.dateDifference(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, minFromDate, DBDATE);
					toDateDiff = uF.dateDifference(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, maxToDate, DBDATE);
				}
				
				List<String> lTypeAndIdList = getLearningStageType(con, uF, rst.getInt("learning_plan_id"));
				if(uF.parseToInt(fromDateDiff) > 1 && uF.parseToInt(toDateDiff) > 1) {
//					System.out.println("Future Plan IDS ===> " + rst.getInt("learning_plan_id"));
					/*if(){
						
					}*/
					List<String> innerList = new ArrayList<String>();
					innerList.add(rst.getString("learning_plan_id"));
					innerList.add(rst.getString("learning_plan_name"));
					innerList.add(lTypeAndIdList.get(0)); // L Type
					innerList.add(uF.showData(CF.getCertificateName(con, rst.getString("certificate_id")),"No Certificate")); // Certificate
					innerList.add(uF.getDateFormat(minFromDate, DBDATE, CF.getStrReportDateFormat())); //S Date
					innerList.add(uF.getDateFormat(maxToDate, DBDATE, CF.getStrReportDateFormat())); //E Date
					innerList.add(hmEmpName.get(rst.getString("added_by"))); //Created By
					innerList.add(rst.getString("learning_plan_objective")); //Reason For
					innerList.add(lTypeAndIdList.get(1)); // L ID
					
					pst1 = con.prepareStatement("select * from learning_nominee_details where learning_plan_id=? and requested_by=?");
					pst1.setInt(1, uF.parseToInt(rst.getString("learning_plan_id")));
					pst1.setInt(2, uF.parseToInt(strSessionEmpId));
					rst1 = pst1.executeQuery();
					StringBuilder tempStatus = new StringBuilder();
					while (rst1.next()) {
						tempStatus.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rst1.getString("nominated_details_id")+"');\" style=\"margin-left: 10px;\">View</a>");
					}
					innerList.add(tempStatus.toString());
					rst1.close();
					pst1.close();
					alOrgLearnings.add(innerList);
				}
				
				request.setAttribute("alOrgLearnings", alOrgLearnings);
				
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst1);
			db.closeResultSet(rst);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	//===end parvez date: 01-10-2021===
	
	//===added by parvez date: 22-10-2021===
	//===start===
	public void getVideoCountAndVideoViewedCount(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> hmVideoCount = new HashMap<String, String>();
			
			int alCount = 0;
			pst = con.prepareStatement("select learning_video_id from learning_video_details where learning_video_id > 0");
			rs = pst.executeQuery();
			while (rs.next()) {
//				alCount++;
				hmVideoCount.put(rs.getString("learning_video_id"), "1");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as count, learning_video_id from learning_subvideo_details group by learning_video_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				int subCount = uF.parseToInt(hmVideoCount.get(rs.getString("learning_video_id")))+uF.parseToInt(rs.getString("count"));
				
				hmVideoCount.put(rs.getString("learning_video_id"), subCount+"");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmVideoCount", hmVideoCount);

			Map<String, String> hmVideoViewedCount = new HashMap<String, String>();
			pst = con.prepareStatement("select learning_video_seen_count, learning_video_id, learning_plan_id from learning_video_seen_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getString("learning_video_seen_count")!=null){
					String strCount1 = rs.getString("learning_video_seen_count");
					String[] count = strCount1.split(",");
					
					hmVideoViewedCount.put(rs.getString("learning_video_id")+"_"+rs.getString("learning_plan_id"), count.length+"");
				}
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmVideoViewedCount", hmVideoViewedCount);
			
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
	//===end parvez date: 22-10-2021===


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

	 

	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getDataType() {
		return dataType;
	}


	public void setDataType(String dataType) {
		this.dataType = dataType;
	}



	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}
	
}