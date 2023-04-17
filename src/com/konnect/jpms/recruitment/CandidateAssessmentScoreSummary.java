package com.konnect.jpms.recruitment;

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

import com.konnect.jpms.training.LearningPlanAssessmentScoreSummary;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CandidateAssessmentScoreSummary extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8212571017913212852L;


	HttpSession session;
	private HttpServletRequest request;
	String strUserType = null;
	String strSessionEmpId = null;

	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(LearningPlanAssessmentScoreSummary.class);

//	String lPlanId;
	String assessmentId;
	String candidateId;
	String recruitId;
	String roundId;

	String type;
	String UID;
	String newID;
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
		
		if(getCandidateId() != null) {
			getScoreMarks();
			}

		getAssessmentDetails(uF);
		getReport();
		getAssessmentQuestionData(uF);

//		request.setAttribute("levelMp", levelMp);

		return SUCCESS;
	}
	
	
	private String getAppendData(String strID,  Map<String, String> mp) {
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

			pst = con.prepareStatement("select * from assessment_question_answer where recruitment_id=? and round_id=? and candidate_id=? order by user_type_id");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getRoundId()));
			pst.setInt(3, uF.parseToInt(getCandidateId()));
			rs = pst.executeQuery();
//			System.out.println("getScoreMarks pst ===> "+pst);
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
					"and aqw.assessment_section_id = ald.assessment_section_id and aqw.recruitment_id= ? and round_id= ? and candidate_id=?" +
					"group by aqw.assessment_section_id, user_type_id order by user_type_id");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			pst.setInt(2, uF.parseToInt(getRoundId()));
			pst.setInt(3, uF.parseToInt(getCandidateId()));
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
			
			Map<String, List<List<String>>> hmSectionData = new HashMap<String, List<List<String>>>();
			pst = con.prepareStatement("select * from assessment_section_details where assessment_details_id=?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
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
			pst = con.prepareStatement("select * from assessment_question_details, assessment_question_bank where assessment_question_bank_id = question_bank_id" +
					" and assessment_details_id=?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
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

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void getAssessmentDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {

			Map<String, String> hmAttribute = new HashMap<String, String>();
			pst = con.prepareStatement("select arribute_id,attribute_name from appraisal_attribute");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			pst = con.prepareStatement("select * from assessment_details where assessment_details_id =?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			rs = pst.executeQuery();
			Map<String, String> hmAssessmentData = new HashMap<String, String>();
			while (rs.next()) {
				hmAssessmentData.put("ID", rs.getString("assessment_details_id"));
				hmAssessmentData.put("ASSESSMENT_NAME", rs.getString("assessment_name"));
				hmAssessmentData.put("SUBJECT", uF.showData(CF.getAssessmentSubjectNameById(con, rs.getString("assessment_subject")), ""));
				hmAssessmentData.put("AUTHOR", uF.showData(rs.getString("assessment_author"), ""));
				hmAssessmentData.put("VERSION", uF.showData(rs.getString("assessment_version"), ""));
				hmAssessmentData.put("DESCRIPTION", uF.showData(rs.getString("assessment_description"), ""));
				hmAssessmentData.put("ATTEMPT_COUNT", uF.showData(rs.getString("assessment_take_attempt"), ""));
				hmAssessmentData.put("TIME_DURATION", uF.showData(rs.getString("assessment_time_duration"), ""));
				hmAssessmentData.put("MARK_GRADE_STANDARD", uF.showData(uF.getGradeStandard(uF, rs.getString("marks_grade_standard")), ""));
				hmAssessmentData.put("MARK_GRADE_TYPE", uF.showData(uF.getGradeType(uF, rs.getString("marks_grade_type")), ""));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAssessmentData", hmAssessmentData);
			
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

}
