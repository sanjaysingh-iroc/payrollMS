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

public class ViewAssessmentDetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private String operation;

	private String ID;

	CommonFunctions CF = null;

	private String assessmentName;
	private String assessmentSubject;
	private String assessmentAuthor;
	private String assessmentVersion;
	private String assessmentPreface;
	private String timesToAttempt;
	private String timeDuration;
	private String gradingType;
	private String gradingStandard;
	
	private  String del;
	private String quest_id;
	private  String assessmentId;
	private String stepSubmit;
	private  String stepSave;
	private String tab;
	private String sectionCnt;
	private String fromPage;
	private static Logger log = Logger.getLogger(AddNewAssessment.class);
	
	private String subjectID = null;
	
	public String execute() {

		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		
		
		request.setAttribute(PAGE, "/jsp/training/ViewAssessmentDetails.jsp");
		request.setAttribute(TITLE, "View Assessment Details");
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		
//		System.out.println("getAssessmentId() ===> " + getAssessmentId());
		
		getAssessmentData(uF);
		getSectionDataList(uF);
		getQuestionsList(uF);
		getAssessmentDetails();
		return LOAD;
	}
	
	private void getAssessmentDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmSubjectName =  CF.getSubjectsMap(con);
			
			pst=con.prepareStatement("select learning_plan_stage_name_id from learning_plan_stage_details where learning_plan_stage_name_id > 0 and learning_type='Assessment'");
			rst=pst.executeQuery();
			List<String> alAssignAssessment = new ArrayList<String>();
			while(rst.next()){
				if(!alAssignAssessment.contains(rst.getString("learning_plan_stage_name_id"))){
					alAssignAssessment.add(rst.getString("learning_plan_stage_name_id"));
				}
			}
			rst.close();
			pst.close();
			
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from assessment_details where assessment_details_id=?");
			pst = con.prepareStatement(strQuery.toString());
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			rst = pst.executeQuery();
			
			StringBuilder sbAssessments = new StringBuilder();
			StringBuilder sbStauts = new StringBuilder();
			StringBuilder sbAssessmentData = new StringBuilder();
			while (rst.next()) {
				String updateDate = null;
 
				if(rst.getString("update_date") != null) {
					updateDate = uF.getDateFormat(rst.getString("update_date"), DBDATE, CF.getStrReportDateFormat());
				} else {
					updateDate = uF.getDateFormat(rst.getString("entry_date"), DBDATE, CF.getStrReportDateFormat());
				}
				sbAssessments.replace(0, sbAssessments.length(), "");
				sbStauts.replace(0, sbStauts.length(), "");
				sbAssessmentData.replace(0, sbAssessmentData.length(), "");
				boolean statusFlag = checkAssessmentStatus(con, rst.getString("assessment_details_id"));
				
				if(statusFlag == false) {
					sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus\" > ");
					/*sbStauts.append("<img src=\"images1/icons/pullout.png\" title=\"Waiting for live\" />");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Waiting for live\" ></i>");
					
					sbStauts.append("</div>");
				} else {
					sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus\" > ");
					 /*sbStauts.append("<img src=\"images1/icons/approved.png\" title=\"Live\" />");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Live\"></i>");
					
					sbStauts.append("</div>");
				}
				sbAssessmentData.append("<div style=\"float:right;\" id=\"myDivM\" > ");
				
				boolean statusFlag1 = checkAssessmentStatus1(con, rst.getString("assessment_details_id"));
				if(statusFlag1 == false) {	
					sbAssessmentData.append("<a href=\"javascript:void(0);\" onclick=\"editAssessment("+ rst.getString("assessment_details_id") +");\"> <i class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></i> </a>");
				} else {
					sbAssessmentData.append("<a href=\"javascript:void(0);\" onclick=\"createNewVersionOfAssessment("+ rst.getString("assessment_details_id") +");\"> <i class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></i> </a>");
				}
				
				if(!alAssignAssessment.contains(rst.getString("assessment_details_id"))){
					sbAssessmentData.append("<a class=\"del\" title=\"Delete Assessment\" href=\"javascript:void(0)\" onclick=\"deleteAssessment('"+rst.getString("assessment_details_id")+"')\" ><i class=\"fa fa-trash\" aria-hidden=\"true\"></i></a>");
				}
				
				sbAssessmentData.append("</div>");
				sbAssessments.append("<div>" + sbStauts + " <strong>" + rst.getString("assessment_name") + "</strong> author is <strong>" + uF.showData(rst.getString("assessment_author"),"-") + "</strong>, subject is <strong>"+ uF.showData(hmSubjectName.get(rst.getString("assessment_subject")), "-") + "</strong>, version <strong>"+ uF.showData(rst.getString("assessment_version"), "-") +"</strong>." + sbAssessmentData+"</div><div style=\"margin-left: 20px;\"><i> Last updated on "+updateDate+"</i></div>");
				
			}
			rst.close();
			pst.close();
			
			request.setAttribute("sbAssessments", sbAssessments.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private boolean checkAssessmentStatus(Connection con, String assessmentId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean assessmentStatusFlag = false;
		try {
			
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_type = 'Assessment' and learning_plan_stage_name_id = ? and ? between from_date and to_date");
			pst.setInt(1, uF.parseToInt(assessmentId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rst = pst.executeQuery();
			while (rst.next()) {
				assessmentStatusFlag = true;
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
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
		return assessmentStatusFlag;
	}
	
	
	
	private boolean checkAssessmentStatus1(Connection con, String assessmentId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean assessmentStatusFlag = false;
		try {
			
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_type = 'Assessment' and learning_plan_stage_name_id = ?");
			pst.setInt(1, uF.parseToInt(assessmentId));
			rst = pst.executeQuery();
			while (rst.next()) {
				assessmentStatusFlag = true;
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return assessmentStatusFlag;
	}

	private void getQuestionsList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmAnstypeName = new HashMap<String, String>();
			pst = con.prepareStatement("select * from training_answer_type ");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAnstypeName.put(rs.getString("answer_type_id"), rs.getString("answer_type_name"));
			}
			rs.close();
			pst.close();
			
//			List<List<String>> assessmentList = new ArrayList<List<String>>();
			Map<String, List<List<String>>> hmAssessmentQueData = new HashMap<String, List<List<String>>>();
			pst = con.prepareStatement("select * from assessment_question_details, assessment_question_bank where assessment_question_bank_id = question_bank_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> questionList = hmAssessmentQueData.get(rs.getString("assessment_section_id"));
				if(questionList == null){
					questionList = new ArrayList<List<String>>();
				}
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_text"));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("question_bank_id"));
				innerList.add(rs.getString("assessment_question_id"));
				innerList.add(rs.getString("option_a"));//4
				innerList.add(rs.getString("option_b"));
				innerList.add(rs.getString("option_c"));
				innerList.add(rs.getString("option_d"));
				innerList.add(rs.getString("correct_ans")); //8
				innerList.add(rs.getString("answer_type"));
				innerList.add(rs.getString("is_add"));
				innerList.add(rs.getString("assessment_section_id"));
				innerList.add(rs.getString("assessment_details_id"));
				innerList.add(rs.getString("que_matrix_heading")); //13
				
				questionList.add(innerList);
				hmAssessmentQueData.put(rs.getString("assessment_section_id"), questionList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmAssessmentQueData === > " +hmAssessmentQueData);
			request.setAttribute("hmAssessmentQueData", hmAssessmentQueData);
			request.setAttribute("hmAnstypeName", hmAnstypeName);

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
			
			pst = con.prepareStatement("select * from assessment_details where assessment_details_id = ? ");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			rs = pst.executeQuery();
			 String assessPreface = null;
			while (rs.next()) {
				setAssessmentName(rs.getString("assessment_name"));
				setAssessmentSubject(hmCourseSubject.get(rs.getString("assessment_subject")));
				setSubjectID(uF.showData(rs.getString("assessment_subject"), "-"));
				setAssessmentAuthor(uF.showData(rs.getString("assessment_author"), "-"));
				setAssessmentVersion(uF.showData(rs.getString("assessment_version"), "-"));
				assessPreface = rs.getString("assessment_description");
				setTimesToAttempt(uF.showData(uF.getTimesToAttempt(uF.parseToInt(rs.getString("assessment_take_attempt"))), ""));
				setTimeDuration(rs.getString("assessment_time_duration")+" mins");
				setGradingType(uF.showData(uF.getGradeType(uF, rs.getString("marks_grade_type")), ""));
				setGradingStandard(uF.showData(uF.getGradeStandard(uF, rs.getString("marks_grade_standard")), ""));
			}
			rs.close();
			pst.close();
			request.setAttribute("assessPreface", assessPreface);

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


	public String getAppendData(String strIds) {
		
		StringBuilder sb = new StringBuilder();
		if(strIds != null && !strIds.equals("")) {
			
			List<String> idsList = Arrays.asList(strIds.split(","));
			if (idsList != null && !idsList.isEmpty()) {
				
				for (int i = 0; i < idsList.size(); i++) {
					if (i == 0) {
						sb.append("," + idsList.get(i).trim() + ",");
					} else {
						sb.append(idsList.get(i).trim() + ",");
					}
				}
			} else {
				return null;
			}
		}
		return sb.toString();
	}
	
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;

	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	private String step;

	
	// first step variables **********

	
	
		public String getAssessmentName() {
		return assessmentName;
	}

	public void setAssessmentName(String assessmentName) {
		this.assessmentName = assessmentName;
	}

	public String getAssessmentSubject() {
		return assessmentSubject;
	}

	public void setAssessmentSubject(String assessmentSubject) {
		this.assessmentSubject = assessmentSubject;
	}

	public String getAssessmentAuthor() {
		return assessmentAuthor;
	}

	public void setAssessmentAuthor(String assessmentAuthor) {
		this.assessmentAuthor = assessmentAuthor;
	}

	public String getAssessmentVersion() {
		return assessmentVersion;
	}

	public void setAssessmentVersion(String assessmentVersion) {
		this.assessmentVersion = assessmentVersion;
	}

	public String getAssessmentPreface() {
		return assessmentPreface;
	}

	public void setAssessmentPreface(String assessmentPreface) {
		this.assessmentPreface = assessmentPreface;
	}


	// fields for 2nd screen Stage info **************
	
	
	// Fields for 3rd Screen ****************

	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}

	public String getStepSubmit() {
		return stepSubmit;
	}

	public void setStepSubmit(String stepSubmit) {
		this.stepSubmit = stepSubmit;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public String getSectionCnt() {
		return sectionCnt;
	}

	public void setSectionCnt(String sectionCnt) {
		this.sectionCnt = sectionCnt;
	}

	public String getStepSave() {
		return stepSave;
	}

	public void setStepSave(String stepSave) {
		this.stepSave = stepSave;
	}

	public String getTimesToAttempt() {
		return timesToAttempt;
	}

	public void setTimesToAttempt(String timesToAttempt) {
		this.timesToAttempt = timesToAttempt;
	}

	public String getTimeDuration() {
		return timeDuration;
	}

	public void setTimeDuration(String timeDuration) {
		this.timeDuration = timeDuration;
	}

	public String getGradingType() {
		return gradingType;
	}

	public void setGradingType(String gradingType) {
		this.gradingType = gradingType;
	}

	public String getGradingStandard() {
		return gradingStandard;
	}

	public void setGradingStandard(String gradingStandard) {
		this.gradingStandard = gradingStandard;
	}


	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	
}
