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

public class LearningPlanAssessmentScoreSummary extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8212571017913212852L;


	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;
//	Map<String, List<Map<String, List<List<String>>>>> levelMp = new HashMap<String, List<Map<String, List<List<String>>>>>();

	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(LearningPlanAssessmentScoreSummary.class);

	private String lPlanId;
//	String assessmentId; 
	private String empId;

	private String type;
	private String UID;
	private String newID;
	public  String execute() {

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);

		UtilityFunctions uF = new UtilityFunctions();

		if (CF == null) {
			return LOGIN;
		}

		request.setAttribute(TITLE, "Learning Plan Summary ");
		request.setAttribute(PAGE, "/jsp/training/LearningPlanAssessmentScoreSummary.jsp");
		
		if(getEmpId() != null) {
			getScoreMarks();
		}

		getLearningPlanAssessmentStatus(uF);
		getReport();
		getAssessmentQuestionData(uF);

//		request.setAttribute("levelMp", levelMp);

		return SUCCESS;
	}
	
	
	private String getAppendData( String strID,  Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
//		System.out.println("strID :: "+strID);
		if (strID != null && !strID.equals("") && !strID.isEmpty()) {
			if(strID.length()>0 && strID.substring(0, 1).equals(",") && strID.substring(strID.length()-1, strID.length()).equals(",")){
			strID = strID.substring(1, strID.length()-1);
			}
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

	
	private Map getScoreMarks() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		Map hmScoreDetailsMap = new HashMap();
		Map hmScoreAggregateMap = new HashMap();
		try {

			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from assessment_question_answer where learning_plan_id=? and emp_id= ? order by user_type_id");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
//			System.out.println("hmScoreDetailsMap pst==>"+pst);
			rs = pst.executeQuery();

			String strUserTypeNew = null;
			String strUserTypeOld = null;
			
			Map hmAnswerScore = new HashMap();
			
			while (rs.next()) {
				
				strUserTypeNew = rs.getString("user_type_id");
				if(strUserTypeNew!=null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)){
					hmAnswerScore = new HashMap();
				}
				
				double dblMarks = uF.parseToDouble(rs.getString("marks"));
				double dblWeightage = uF.parseToDouble(rs.getString("weightage"));
				double dblPercent = 0;
				if(dblWeightage>0){
					dblPercent = dblMarks * 100 / dblWeightage;  
				}
				//System.out.println("dblPercent :::::: "+dblPercent);
				hmAnswerScore.put(rs.getString("assessment_question_id"), uF.formatIntoComma(dblPercent)+"%");
//				System.out.println("hmAnswerScore :::::: "+hmAnswerScore);
				hmScoreDetailsMap.put(rs.getString("user_type_id"), hmAnswerScore);
//				System.out.println("hmScoreDetailsMap :::::: "+hmScoreDetailsMap);
				strUserTypeOld = strUserTypeNew;
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmScoreDetailsMap :::::: "+hmScoreDetailsMap);
			request.setAttribute("hmScoreDetailsMap", hmScoreDetailsMap);
			
			
			pst = con.prepareStatement("select user_type_id, sum(marks) as marks, sum(weightage) as weightage, aqw.assessment_section_id from " +
					"assessment_question_answer aqw,assessment_section_details ald where ald.assessment_details_id = aqw.assessment_details_id " +
					"and aqw.assessment_section_id = ald.assessment_section_id and aqw.learning_plan_id= ? and emp_id= ? " +
					"group by aqw.assessment_section_id, user_type_id order by user_type_id");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
//			System.out.println("hmScoreAggregateMap pst==>"+pst);
			rs = pst.executeQuery();
			
			double dblTotalMarks=0;
			double dblTotalWeightage=0;
			Map hmTemp = new HashMap();
			
			while (rs.next()) {
				
				strUserTypeNew = rs.getString("user_type_id");
				if(strUserTypeNew!=null && !strUserTypeNew.equalsIgnoreCase(strUserTypeOld)){
					hmTemp = new HashMap();
				}
				
				dblTotalMarks = uF.parseToDouble(rs.getString("marks"));
				dblTotalWeightage = uF.parseToDouble(rs.getString("weightage"));
				
				
				if(dblTotalWeightage>0 && rs.getString("marks")!=null){
					hmTemp.put(rs.getString("assessment_section_id"), uF.formatIntoTwoDecimal(((dblTotalMarks / dblTotalWeightage) * 100))+"%");
				}
				
				hmScoreAggregateMap.put(rs.getString("user_type_id"), hmTemp);
				
				
				strUserTypeOld = strUserTypeNew;
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmScoreAggregateMap === > " + hmScoreAggregateMap);
			request.setAttribute("hmScoreAggregateMap", hmScoreAggregateMap);
			
			
			/*pst = con.prepareStatement("Select sattlement_comment,if_approved,user_id, emp_fname, emp_lname, _date from appraisal__sattlement afs,employee_personal_details epd where afs.user_id = epd.emp_per_id and appraisal_id=? and emp_id=? ");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			
//			System.out.println("pst===>"+pst);
			
			String strComments = null;
			String strAppraisedBy = null;
			String strAppraisedOn = null;
			while(rs.next()){
				strComments = rs.getString("sattlement_comment");
				if(strComments!=null){
					strComments = strComments.replace("\n", "<br/>");
				}
				strAppraisedBy = rs.getString("emp_fname")+" "+rs.getString("emp_lname");
				strAppraisedOn = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());
			}*/
//			request.setAttribute("strComments", strComments);
//			request.setAttribute("strAppraisedBy", strAppraisedBy);
//			request.setAttribute("strAppraisedOn", strAppraisedOn);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmScoreDetailsMap;
	}

	public  void getReport() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmUserName = CF.getEmpNameMap(con, null, null);
			
			Map<String, String> hmCourseSubject = new HashMap<String, String>();
			pst = con.prepareStatement("select * from course_subject_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmCourseSubject.put(rs.getString("course_subject_id"), rs.getString("course_subject_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from learning_plan_stage_details lpsd, assessment_details ad where learning_plan_id = ? and lpsd.learning_plan_stage_name_id = ad.assessment_details_id ");
			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
			List<List<String>> assessmentList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("assessment_details_id"));
				innerList.add(rs.getString("assessment_name"));
				innerList.add(hmCourseSubject.get(rs.getString("assessment_subject")));
				innerList.add(rs.getString("assessment_author"));
				innerList.add(rs.getString("assessment_version"));
				innerList.add(rs.getString("assessment_description"));
				
				assessmentList.add(innerList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("assessmentList =====> "+ assessmentList);
			request.setAttribute("assessmentList", assessmentList);
			
			Map<String, List<List<String>>> hmSectionData = new HashMap<String, List<List<String>>>();
			pst = con.prepareStatement("select * from assessment_section_details");
//			pst.setInt(1, uF.parseToInt(getlPlanId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> assessSectionList = hmSectionData.get(rs.getString("assessment_details_id"));
				if(assessSectionList == null){
					assessSectionList = new ArrayList<List<String>>();
				}
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("assessment_section_id"));
				innerList.add(rs.getString("assessment_section_name"));
				innerList.add(rs.getString("assessment_section_description"));
				innerList.add(rs.getString("marks_of_section"));
				innerList.add(rs.getString("assessment_details_id"));
				innerList.add(rs.getString("attempt_questions"));
				innerList.add(hmUserName.get(rs.getString("added_by")));
				innerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				assessSectionList.add(innerList);
				hmSectionData.put(rs.getString("assessment_details_id"), assessSectionList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmSectionData", hmSectionData);
			
//			System.out.println("hmSectionData =====> "+hmSectionData);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public  void getAssessmentQuestionData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
//			List<List<String>> assessmentList = new ArrayList<List<String>>();
			Map<String, List<List<String>>> hmAssessmentQueData = new HashMap<String, List<List<String>>>();
			pst = con.prepareStatement("select * from assessment_question_details, assessment_question_bank where assessment_question_bank_id = question_bank_id");
//			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			rs = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
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

//			System.out.println("questionMp =====> "+questionMp);
//			List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
//			// list.add(otherMp);
//			list.add(questionMp);
//			levelMp.put(id + "", list);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	

//	Connection con = null;
//	PreparedStatement pst = null;
//	ResultSet rs = null;
//	Database db = new Database();
//	db.setRequest(request);
////	UtilityFunctions uF = new UtilityFunctions();
//	
//	try {
//		con = db.makeConnection(con);
////		List<List<String>> assessmentList = new ArrayList<List<String>>();
//		Map<String, Map<String, List<List<String>>>> hmAssessmentwiseQueData = new HashMap<String, Map<String, List<List<String>>>>();
////		Map<String, List<List<String>>> hmAssessmentQueData = new HashMap<String, List<List<String>>>();
//		pst = con.prepareStatement("select * from assessment_question_details, assessment_question_bank where assessment_question_bank_id = question_bank_id and assessment_details_id = ?");
////		pst.setInt(1, uF.parseToInt(getAssessmentId()));
//		rs = pst.executeQuery();
//		System.out.println("pst ===> " + pst);
//		while (rs.next()) {
//			Map<String, List<List<String>>> hmAssessmentQueData = hmAssessmentwiseQueData.get(rs.getString("assessment_details_id"));
//			if(hmAssessmentQueData == null){
//				hmAssessmentQueData = new HashMap<String, List<List<String>>>();
//			}
//			List<List<String>> questionList = hmAssessmentQueData.get(rs.getString("assessment_section_id"));
//			if(questionList == null){
//				questionList = new ArrayList<List<String>>();
//			}
//			List<String> innerList = new ArrayList<String>();
//			innerList.add(rs.getString("question_bank_id"));
//			innerList.add(rs.getString("question_text"));
//			innerList.add(rs.getString("option_a"));
//			innerList.add(rs.getString("option_b"));
//			innerList.add(rs.getString("option_c"));
//			innerList.add(rs.getString("option_d"));
//			innerList.add(rs.getString("correct_ans"));
//			innerList.add(rs.getString("weightage"));
//			innerList.add(rs.getString("answer_type"));
//			innerList.add(rs.getString("assessment_question_id"));
//			innerList.add(rs.getString("is_add"));
//			innerList.add(rs.getString("assessment_section_id"));
//			innerList.add(rs.getString("assessment_details_id"));
//			
//			questionList.add(innerList);
//			hmAssessmentQueData.put(rs.getString("assessment_section_id"), questionList);
//			hmAssessmentwiseQueData.put(rs.getString("assessment_details_id"), hmAssessmentQueData);
//		}
//		System.out.println("hmAssessmentwiseQueData === > " +hmAssessmentwiseQueData);
//		request.setAttribute("hmAssessmentwiseQueData", hmAssessmentwiseQueData);
//
////		System.out.println("questionMp =====> "+questionMp);
////		List<Map<String, List<List<String>>>> list = new ArrayList<Map<String, List<List<String>>>>();
////		// list.add(otherMp);
////		list.add(questionMp);
////		levelMp.put(id + "", list);
//
//	} catch (SQLException e) {
//		e.printStackTrace();
//	} finally {
//		
//		db.closeResultSet(rs);
//		db.closeStatements(pst);
//		db.closeConnection(con);
//	}

	
	
	
	

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
			Map<String, String> learningPlanMp = new HashMap<String, String>();
			while (rs.next()) {
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
				learningPlanMp.put("ASSIGNEE", uF.showData(getAppendData(rs.getString("learner_ids"), hmEmpName),""));
				/*String skills = "";
				if(rs.getString("skills") != null && rs.getString("skills").length() > 1){
					skills = rs.getString("skills").substring(1, rs.getString("skills").length()-1);
				}*/
				learningPlanMp.put("SKILLS", uF.showData(getAppendData(rs.getString("skills"), hmSkillName), ""));
				learningPlanMp.put("FROM", uF.getDateFormat(minFromDate, DBDATE, CF.getStrReportDateFormat()));
				learningPlanMp.put("TO", uF.getDateFormat(maxToDate, DBDATE, CF.getStrReportDateFormat()));
				learningPlanMp.put("LEARNERS_ID", rs.getString("learner_ids"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("learningPlanMp", learningPlanMp);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	@Override
	public  void setServletRequest( HttpServletRequest request) {
		this.request = request;
	}
	public  String getType() {
		return type;
	}
	public  void setType( String type) {
		this.type = type;
	}
	public  String getEmpId() {
		return empId;
	}
	public  void setEmpId( String empId) {
		this.empId = empId;
	}

	public String getlPlanId() {
		return lPlanId;
	}

	public void setlPlanId(String lPlanId) {
		this.lPlanId = lPlanId;
	}

}
