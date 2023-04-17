package com.konnect.jpms.training;

import java.io.File;
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
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillSubject;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddNewAssessment extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private String operation;

	private String ID;

	CommonFunctions CF = null;

	private String del;
	private String quest_id;
	private String assessmentId;
	private String stepSubmit;
	private String stepSave;
	private String tab;
	private String sectionCnt;
    
    private String step;

	
	// first step variables **********

    private String assessmentName;
    private String assessmentSubject;
    private String assessmentAuthor;
    private String assessmentVersion;
    private String assessmentPreface;
    private String timestoAttemptAssessment;
    private String assessmentTimeDuration;
    private String marksGradeType;
    private String marksGradeStandard;
    private String assignToExist;
    
	private static Logger log = Logger.getLogger(AddNewAssessment.class);
	
	private String subjectID = null;
	
	private List<FillSubject> subjectList;
	
	private String marksGradeTypeDefault;
	private String marksGradeStandardDefault;
	private String fromPage;
	public String execute() {

		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		
		String strOperation = request.getParameter("operation");
		
	/*	request.setAttribute(PAGE, "/jsp/training/AddNewAssessment.jsp");*/
		if (strOperation != null && strOperation.equalsIgnoreCase("E")) {
			request.setAttribute(TITLE, "Edit Assessment");
		} else {
			request.setAttribute(TITLE, "Add Assessment");
		}
		
		subjectList = new FillSubject(request).fillSubjectName();

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		/*System.out.println(" before getStepSave==>"+getStepSave());
		System.out.println(" before getStepSubmit()==>"+getStepSubmit());*/
		if (strOperation != null && strOperation.equalsIgnoreCase("A")) {
			insertData(uF);
		} else if (strOperation != null && strOperation.equalsIgnoreCase("E")) {
			updateData(getAssessmentId(), uF);
			
		} else if (strOperation != null && strOperation.equalsIgnoreCase("D")) {
			return deleteAssessment(getAssessmentId(), uF);
		}

		if (tab == null) {
			setTab("0");
		} else if (tab != null && uF.parseToInt(getTab()) == 0) {
			setTab("1");
		} else if (uF.parseToInt(getTab()) >= 1) {
			setTab(""+(uF.parseToInt(getTab())+1));
		}
		if (sectionCnt == null) {
			setSectionCnt("1");
		}
		//System.out.println(" before getStepSave==>"+getStepSave());
		if (getStepSave() != null && getStepSave().equals("Save And Exit")) {
			//System.out.println("getStepSave==>"+getStepSave());
			return SUCCESS;
		}
		
		getAnsType();
		getAssessmentData(uF);
		getSectionDataList(uF);
		getQuestionsList(uF);
		getMarksGradeType(uF);
		return LOAD;
	}
	

	
	private void getMarksGradeType(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from training_mark_grade_type order by training_mark_grade_type_id");
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmGradeStandardwiseValue = new HashMap<String, List<List<String>>>();
			List<List<String>> gradeStandardwiseList = new ArrayList<List<String>>();
			while(rs.next()){
				
				gradeStandardwiseList = hmGradeStandardwiseValue.get(rs.getString("grade_standard"));
				if(gradeStandardwiseList == null) gradeStandardwiseList = new ArrayList<List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("numeric_grade_type"));
				innerList.add(rs.getString("alphabet_grade_type"));
				gradeStandardwiseList.add(innerList);
				hmGradeStandardwiseValue.put(rs.getString("grade_standard"), gradeStandardwiseList);
				
			}
			rs.close();
			pst.close();
//			System.out.println("hmGradeStandardwiseValue ===> " + hmGradeStandardwiseValue);
			request.setAttribute("hmGradeStandardwiseValue", hmGradeStandardwiseValue);
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
			Map<String, String> hmAssessQueTotWeight = new HashMap<String, String>();
			pst = con.prepareStatement("select * from assessment_question_details, assessment_question_bank where assessment_question_bank_id = question_bank_id");
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				double totWeightage = uF.parseToDouble(uF.showData(hmAssessQueTotWeight.get(rs.getString("assessment_section_id")), "0"));
				totWeightage += uF.parseToDouble(rs.getString("weightage"));
				hmAssessQueTotWeight.put(rs.getString("assessment_section_id"), String.valueOf(totWeightage));
				
				List<List<String>> questionList = hmAssessmentQueData.get(rs.getString("assessment_section_id"));
				if(questionList == null){
					questionList = new ArrayList<List<String>>();
				}
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_text"));//0
				innerList.add(rs.getString("weightage"));//1
				innerList.add(rs.getString("question_bank_id"));//2
				innerList.add(rs.getString("assessment_question_id"));//3
				innerList.add(rs.getString("option_a"));//4
				innerList.add(rs.getString("option_b"));//5
				innerList.add(rs.getString("option_c"));//6
				innerList.add(rs.getString("option_d"));//7
				innerList.add(rs.getString("correct_ans"));//8
				innerList.add(rs.getString("answer_type"));//9
				innerList.add(rs.getString("is_add"));//10
				innerList.add(rs.getString("assessment_section_id"));//11
				innerList.add(rs.getString("assessment_details_id"));//12
				innerList.add(rs.getString("que_matrix_heading"));//13
				
				questionList.add(innerList);
				hmAssessmentQueData.put(rs.getString("assessment_section_id"), questionList);
			}
			rs.close();
			pst.close();
//			System.out.println("hmAssessmentQueData === > " +hmAssessmentQueData);
			request.setAttribute("hmAssessmentQueData", hmAssessmentQueData);
			request.setAttribute("hmAssessQueTotWeight", hmAssessQueTotWeight);

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
			pst = con.prepareStatement("select * from assessment_details where assessment_details_id = ?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			rs = pst.executeQuery();
			 String assessPreface = null;
			while (rs.next()) {
				setAssessmentName(rs.getString("assessment_name"));
				setSubjectID(rs.getString("assessment_subject"));
				setAssessmentAuthor(rs.getString("assessment_author"));
				
				String dblVersion = "1.0";
				if(rs.getString("assessment_version") != null) {
					dblVersion = rs.getString("assessment_version").replaceAll("[^.0-9]", "");
				}
				String newVersion = "";
				if(dblVersion.contains(".9")) {
					double dblver = uF.parseToDouble(dblVersion);
					int intVersion = (int)dblver + 1;
					newVersion = intVersion+".0";
				} else {
					double dblVers = uF.parseToDouble(dblVersion) + 0.1;
					newVersion = uF.formatIntoOneDecimalWithOutComma(dblVers);
				}
				setAssessmentVersion(newVersion);
				
				assessPreface = rs.getString("assessment_description");
				setTimestoAttemptAssessment(rs.getString("time_to_attempt_assessment"));
				setAssessmentTimeDuration(rs.getString("assessment_time_duration"));
//				setMarksGradeType(rs.getString("marks_grade_type"));
				setMarksGradeTypeDefault(rs.getString("marks_grade_type"));
				
				
//				setMarksGradeStandard(rs.getString("marks_grade_standard"));
				setMarksGradeStandardDefault(rs.getString("marks_grade_standard"));
			}
			rs.close();
			pst.close();
			request.setAttribute("assessPreface", assessPreface);
			
			if(uF.parseToInt(getMarksGradeTypeDefault()) == 0){
				setMarksGradeTypeDefault("1");
			}
			if(uF.parseToInt(getMarksGradeStandardDefault()) == 0){
				setMarksGradeStandardDefault("1");
			}

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
			pst = con.prepareStatement("select * from assessment_section_details where assessment_details_id = ? order by assessment_section_id");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("assessment_section_id"));//0
				innerList.add(rs.getString("assessment_section_name"));//1
				innerList.add(rs.getString("assessment_section_description"));//2
				innerList.add(rs.getString("marks_of_section"));//3
				innerList.add(rs.getString("attempt_questions"));//4
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
					sb.append("<option value=\"" + rs.getString("answer_type") + "\" selected>" + rs.getString("answer_type_name") + "</option>");
				} else {
					sb.append("<option value=\"" + rs.getString("answer_type") + "\">" + rs.getString("answer_type_name") + "</option>");
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


	private String deleteAssessment(String assessmentID, UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		try {

			con = db.makeConnection(con);

			String assessName = getAssessmentNameById(con, uF, assessmentID);
			
			pst = con.prepareStatement("delete from assessment_details where assessment_details_id = ? ");
			pst.setInt(1, uF.parseToInt(assessmentID));
			pst.execute();
			pst.close();

			pst = con.prepareStatement("delete from assessment_section_details where assessment_details_id = ? ");
			pst.setInt(1, uF.parseToInt(assessmentID));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from assessment_question_details where assessment_details_id = ? ");
			pst.setInt(1, uF.parseToInt(assessmentID));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+""+assessName+" assessment has been deleted successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	private void updateData(String assessmentID, UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		boolean assessmentStatusFlag = false;
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_type = 'Assessment' and learning_plan_stage_name_id = ?");
			pst.setInt(1, uF.parseToInt(assessmentID));
			rst = pst.executeQuery();
//			System.out.println("pst assessmentStatusFlag ===> " + pst);
			while (rst.next()) {
				assessmentStatusFlag = true;
			}
			rst.close();
			pst.close();
//			System.out.println("assessmentStatusFlag ===> " + assessmentStatusFlag+"==>getStepSave==>"+getStepSave()+"==>getStepSubmit==>"+getStepSubmit());
			if(assessmentStatusFlag == true && (getStepSubmit() != null || getStepSave() != null)) {
				
				String assessName = getAssessmentNameById(con, uF, assessmentID);
				createNewVersionOfAssessment(con, uF, assessmentID);
				updateStep1Data(con, uF, assessmentID);
				
				String newAssessName = getAssessmentNameById(con, uF, assessmentID);
//				session.setAttribute(MESSAGE, SUCCESSM+""+newAssessName+" assessment has been created new version of "+assessName+" assessment successfully."+END);
			} else {
				
				if (getStepSubmit() != null || getStepSave() != null) {
					insertUpdatedStepData(con, assessmentID, uF);
				}
				
				String assessName = getAssessmentNameById(con, uF, getAssessmentId());
//				session.setAttribute(MESSAGE, SUCCESSM+""+assessName+" assessment has been updated successfully."+END);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	
	private String getAssessmentNameById(Connection con, UtilityFunctions uF, String assessmentID) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		String assessName = null;
		try {
				pst = con.prepareStatement("select assessment_name from assessment_details where assessment_details_id=?");
				pst.setInt(1, uF.parseToInt(assessmentID));
//				System.out.println("pst getAssessmentNameById==>"+pst);
				rst = pst.executeQuery();
				while (rst.next()) {
					assessName = rst.getString("assessment_name");
				}
				rst.close();
				pst.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return assessName;
	}
	
	
	private void updateAssessmentData(Connection con, String assessmentID, UtilityFunctions uF) {

//		System.out.println("updateCourseDat assessmentID ===> " + assessmentID); 
		PreparedStatement pst = null;

		try {
//			update assessment_details set assessment_name=?,assessment_subject=?,assessment_author=?,date_of_creation=?," +
//					"assessment_version=?,assessment_description=?,updated_by=?,update_date=?,ref_assessment_id = 0 where ref_assessment_id = ?
			pst = con.prepareStatement("update assessment_details set assessment_name=?,assessment_subject=?,assessment_author=?,date_of_creation=?," +
					"assessment_version=?,assessment_description=?,updated_by=?,update_date=?,time_to_attempt_assessment=?,assessment_time_duration=?, " +
					"marks_grade_type=?,marks_grade_standard=? where assessment_details_id = ?");
			pst.setString(1, getAssessmentName());
			pst.setString(2, getAssessmentSubject());
			pst.setString(3, getAssessmentAuthor());
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(5, getAssessmentVersion());
			pst.setString(6, getAssessmentPreface());
			pst.setInt(7, uF.parseToInt(strSessionEmpId));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(9, uF.parseToInt(getTimestoAttemptAssessment()));
			pst.setString(10, getAssessmentTimeDuration());
			pst.setInt(11, uF.parseToInt(getMarksGradeType()));
			pst.setInt(12, uF.parseToInt(getMarksGradeStandard()));
			pst.setInt(13, uF.parseToInt(assessmentID));
			pst.execute();
			pst.close();
 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}


	private void createNewVersionOfAssessment(Connection con, UtilityFunctions uF, String assessmentID) {

//		System.out.println("createNewVersionOfAssessment assessmentID ===> " + assessmentID);
		PreparedStatement pst = null, pst1 = null, pst2 = null, pst6 = null, pstt6 = null;
		ResultSet rst = null, rst2 = null, rst6 = null;

		try {
			
			pst = con.prepareStatement("select * from assessment_details where assessment_details_id = ?");
			pst.setInt(1, uF.parseToInt(assessmentID));
			rst = pst.executeQuery();
//			System.out.println("pst course_details ===> " + pst);
			while (rst.next()) {
//				insert into assessment_details(assessment_name,assessment_subject,assessment_author,date_of_creation," +
//						"assessment_version,assessment_description,added_by,entry_date)values(?,?,?,?, ?,?,?,?)
				pst1 = con.prepareStatement("insert into assessment_details(assessment_name,assessment_subject,assessment_author,date_of_creation," +
						"assessment_version,assessment_description,added_by,entry_date,time_to_attempt_assessment,assessment_time_duration," +
						"parent_assessment_id,ref_assessment_id,root_assessment_id)values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
				pst1.setString(1, rst.getString("assessment_name"));
				pst1.setString(2, rst.getString("assessment_subject"));
				pst1.setString(3, rst.getString("assessment_author"));
				pst1.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst1.setString(5, getAssessmentVersion());
				pst1.setString(6, getAssessmentPreface());
				pst1.setInt(7, uF.parseToInt(strSessionEmpId));
				pst1.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
				pst1.setInt(9, uF.parseToInt(rst.getString("time_to_attempt_assessment")));
				pst1.setString(10, rst.getString("assessment_time_duration"));
				pst1.setInt(11, uF.parseToInt(assessmentID));
				pst1.setInt(12, uF.parseToInt(assessmentID));
				if(rst.getString("root_assessment_id") != null){
					pst1.setInt(13, uF.parseToInt(rst.getString("root_assessment_id")));	
				} else {
					pst1.setInt(13, uF.parseToInt(assessmentID));
				}
				pst1.execute();
				pst1.close();
	
				pst2 = con.prepareStatement(" select max(assessment_details_id) as assessment_details_id from assessment_details");
				rst2 = pst2.executeQuery();
				while (rst2.next()) {
					setAssessmentId(rst2.getString("assessment_details_id"));
				}
				rst2.close();
				pst2.close();
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from assessment_section_details where assessment_details_id = ?");
			pst.setInt(1, uF.parseToInt(assessmentID));
			rst = pst.executeQuery();
//			System.out.println("pst assessment_section_detail ===> " + pst);
			while (rst.next()) {
				pst1 = con.prepareStatement("insert into assessment_section_details(assessment_section_name,assessment_section_description,assessment_details_id," +
					"marks_of_section,attempt_questions,added_by,entry_date,ref_assessment_section_id)values(?,?,?, ?,?,?, ?,?)");
				pst1.setString(1, rst.getString("assessment_section_name"));
				pst1.setString(2, rst.getString("assessment_section_description"));
				pst1.setInt(3, uF.parseToInt(getAssessmentId()));
				pst1.setInt(4, uF.parseToInt(rst.getString("marks_of_section")));
				pst1.setInt(5, uF.parseToInt(rst.getString("attempt_questions")));
				pst1.setInt(6, uF.parseToInt(strSessionEmpId));
				pst1.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst1.setInt(8, uF.parseToInt(rst.getString("assessment_section_id")));
				pst1.execute();
				pst1.close();
				
				int newSectionId = 0;
				pst2 = con.prepareStatement(" select max(assessment_section_id) as assessment_section_id from assessment_section_details");
				rst2 = pst2.executeQuery();
				while (rst2.next()) {
					newSectionId = rst2.getInt("assessment_section_id");
				
					pst6 = con.prepareStatement("select * from assessment_question_details where assessment_details_id = ? and assessment_section_id = ?");
					pst6.setInt(1, uF.parseToInt(assessmentID));
					pst6.setInt(2, uF.parseToInt(rst.getString("assessment_section_id")));
					rst6 = pst6.executeQuery();
//					System.out.println("pst assessment_question_detail ===> " + pst);
					while (rst6.next()) {
						pstt6 = con.prepareStatement("insert into assessment_question_details(question_bank_id,weightage,assessment_details_id," +
								"assessment_section_id,added_by,entry_date,ref_assessment_question_id,answer_type)values(?,?,?,?, ?,?,?,?)");
						pstt6.setInt(1, uF.parseToInt(rst6.getString("question_bank_id")));
						pstt6.setDouble(2, uF.parseToDouble(rst6.getString("weightage")));
						pstt6.setInt(3, uF.parseToInt(getAssessmentId()));
						pstt6.setInt(4, newSectionId);
						pstt6.setInt(5, uF.parseToInt(strSessionEmpId));
						pstt6.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
						pstt6.setInt(7, uF.parseToInt(rst6.getString("assessment_question_id")));
						pstt6.setInt(8, uF.parseToInt(rst6.getString("answer_type")));
						pstt6.execute();
						pstt6.close();
					}
					rst6.close();
					pst6.close();
					
				}
				rst2.close();
				pst2.close();
				
			}
			rst.close();
			pst.close();
			
			if(getAssignToExist() != null && getAssignToExist().equals("Yes")) {
				
				pst = con.prepareStatement("update learning_plan_stage_details set learning_plan_stage_name_id = ? where learning_plan_stage_name_id = ? and learning_type = 'Assessment'");
				pst.setInt(1, uF.parseToInt(getAssessmentId()));
				pst.setInt(2, uF.parseToInt(assessmentID));
				pst.executeUpdate();
				pst.close();
				
				getAssessmentNameFromID(con, getAssessmentId(), uF);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rst2 !=null){
				try {
					rst2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rst6 !=null){
				try {
					rst6.close();
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
			if(pst1 !=null){
				try {
					pst1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst2 !=null){
				try {
					pst2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst6 !=null){
				try {
					pst6.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstt6 !=null){
				try {
					pstt6.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}


	private String getAssessmentNameFromID(Connection con, String assessmentID, UtilityFunctions uF) {
		
		PreparedStatement pst = null, pstt = null;
		ResultSet rst = null;
		
		String assessmentName1 = "";
		try {
			
			pst = con.prepareStatement("select assessment_name from assessment_details where assessment_details_id = ?");
			pst.setInt(1, uF.parseToInt(assessmentID));
			rst = pst.executeQuery();
			while (rst.next()) {
				assessmentName1 = rst.getString("assessment_name");
			}
			rst.close();
			pst.close();
			
			pstt = con.prepareStatement("update learning_plan_stage_details set learning_plan_stage_name = ? where learning_plan_stage_name_id = ? and learning_type = 'Assessment'");
			pstt.setString(1, assessmentName1);
			pstt.setInt(2, uF.parseToInt(assessmentID));
			pstt.executeUpdate();
			pstt.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return assessmentName1;
	}

	
	private void insertUpdatedStepData(Connection con, String assessmentID, UtilityFunctions uF) {

//		System.out.println("insertUpdatedStepData assessmentID ===> " + assessmentID);
//		System.out.println("insertUpdatedStepData tab ===> " + tab);
		try {
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			if(getAssessmentAuthor() == null || getAssessmentAuthor().equals("")){
				setAssessmentAuthor(hmEmpName.get(strSessionEmpId));
			}
			if(getAssessmentVersion() == null || getAssessmentVersion().equals("")){
				setAssessmentVersion("1.0");
			}
		if (tab != null && uF.parseToInt(tab) == 0) {

			updateAssessmentData(con, assessmentID, uF);
//			request.setAttribute("CourseID", getAssessmentId());

		} else if (tab != null && uF.parseToInt(tab) > 1) {

			insertChapterData(con, uF);
//			request.setAttribute("CourseID", getAssessmentId());
		} 
		
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

	

	private void updateStep1Data(Connection con, UtilityFunctions uF, String assessmentID) {

		PreparedStatement pst = null;
		ResultSet rst = null;
//		System.out.println("updateStep1Dat getTab() ==== > " + getTab());
		try {
			if(uF.parseToInt(getTab()) == 0) {
			pst = con.prepareStatement("update assessment_details set assessment_name=?,assessment_subject=?,assessment_author=?,date_of_creation=?," +
					"assessment_version=?,assessment_description=?,updated_by=?,update_date=?,time_to_attempt_assessment=?,assessment_time_duration=?," +
					"marks_grade_type=?,marks_grade_standard=?,ref_assessment_id = 0 where ref_assessment_id = ?");
			pst.setString(1, getAssessmentName());
			pst.setString(2, getAssessmentSubject());
			pst.setString(3, getAssessmentAuthor());
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(5, getAssessmentVersion());
			pst.setString(6, getAssessmentPreface());
			pst.setInt(7, uF.parseToInt(strSessionEmpId));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(9, uF.parseToInt(getTimestoAttemptAssessment()));
			pst.setString(10, getAssessmentTimeDuration());
			pst.setInt(11, uF.parseToInt(getMarksGradeType()));
			pst.setInt(12, uF.parseToInt(getMarksGradeStandard()));
			pst.setInt(13, uF.parseToInt(assessmentID));
			pst.execute();
			pst.close();
			
			if(getAssignToExist() != null && getAssignToExist().equals("Yes")) {
				getAssessmentNameFromID(con, getAssessmentId(), uF);
			}
//			System.out.println("assessment_detail update ==== > " + pst);
			} else if (uF.parseToInt(getTab()) > 0) {
			
			String sectionName = request.getParameter("sectionName");
			String sectionDescription = request.getParameter("sectionDescription");
			String marksForSection = request.getParameter("marksForSection");
			String questionAttempt = request.getParameter("questionAttempt");
			String hidesectionid = request.getParameter("hidesectionid");
			/*System.out.println("sectionName ====> " + sectionName);
			System.out.println("sectionDescription ====> " + sectionDescription);
			System.out.println("marksForSection ====> " + marksForSection);
			System.out.println("questionAttempt ====> " + questionAttempt);
			System.out.println("hidesectionid ====> " + hidesectionid);*/
			boolean sectionFlag = false;
			int newSectionId = 0;
			pst = con.prepareStatement("select assessment_section_id from assessment_section_details where ref_assessment_section_id = ?");
			pst.setInt(1, uF.parseToInt(hidesectionid));
			rst = pst.executeQuery();
//			System.out.println("assessment_section_detail pst === > " + pst);
			while (rst.next()) {
				sectionFlag = true;
				newSectionId = rst.getInt("assessment_section_id");
			}
			rst.close();
			pst.close();
			
			if(sectionFlag == true) {
				pst = con.prepareStatement("update assessment_section_details set assessment_section_name=?,assessment_section_description=?," +
						"assessment_details_id=?,marks_of_section=?,attempt_questions=?,updated_by=?,update_date=?,ref_assessment_section_id=0 " +
						"where ref_assessment_section_id = ?");
				pst.setString(1, sectionName);
				pst.setString(2, sectionDescription);
				pst.setInt(3, uF.parseToInt(getAssessmentId()));
				pst.setInt(4, uF.parseToInt(marksForSection));
				pst.setInt(5, uF.parseToInt(questionAttempt));
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(8, uF.parseToInt(hidesectionid));
				pst.execute();
				pst.close();
				
//				newSectionId = uF.parseToInt(hidesectionid);
				
			} else {
			
				pst = con.prepareStatement("insert into assessment_section_details(assessment_section_name,assessment_section_description,assessment_details_id" +
						",marks_of_section,attempt_questions,added_by,entry_date)values(?,?,?, ?,?,?,?)");
				pst.setString(1, sectionName);
				pst.setString(2, sectionDescription);
				pst.setInt(3, uF.parseToInt(getAssessmentId()));
				pst.setInt(4, uF.parseToInt(marksForSection));
				pst.setInt(5, uF.parseToInt(questionAttempt));
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("select max(assessment_section_id) as assessment_section_id from assessment_section_details");
				rst = pst.executeQuery();
				while (rst.next()) {
					newSectionId = rst.getInt("assessment_section_id");
				}
				rst.close();
				pst.close();
			}
			

			double marksPerQue = 0;
			if(marksForSection != null && !marksForSection.equals("") && questionAttempt != null && !questionAttempt.equals("")) {
				marksPerQue = uF.parseToDouble(marksForSection) / uF.parseToDouble(questionAttempt);
			}
//			System.out.println("marksPerQue ===> " + marksPerQue);
				String question[] = request.getParameterValues("question"+sectionCnt);
				String hideassessmentid[] = request.getParameterValues("hideassessmentid"+sectionCnt);
				
				String addFlag[] = request.getParameterValues("status"+sectionCnt);
				String matrixHeading[] = request.getParameterValues("matrixHeading"+sectionCnt);
				String optiona[] = request.getParameterValues("optiona"+sectionCnt);
				String optionb[] = request.getParameterValues("optionb"+sectionCnt);
				String optionc[] = request.getParameterValues("optionc"+sectionCnt);
				String optiond[] = request.getParameterValues("optiond"+sectionCnt);
				String ansType[] = request.getParameterValues("ansType"+sectionCnt);
				String orientt[] = request.getParameterValues("orientt"+sectionCnt);
//				System.out.println("hideassessmentid ====> " + hideassessmentid.length);
				for (int i = 0; question != null && i < question.length; i++) {
					String[] correct = request.getParameterValues("correct"+sectionCnt+"_"+ orientt[i]);
					StringBuilder option = new StringBuilder();

					for (int ab = 0; correct != null && ab < correct.length; ab++) {
						option.append(correct[ab] + ",");
					}
//					System.out.println("hideassessmentid[i] ====> " + hideassessmentid[i]);
					int question_id = 0;
					pst = con.prepareStatement("insert into assessment_question_bank(question_text, option_a, option_b, option_c, option_d, correct_ans, is_add, answer_type, que_matrix_heading)values(?,?,?,?, ?,?,?,?, ?)");
					pst.setString(1, question[i]);
					pst.setString(2, (optiona != null && optiona.length > i ? optiona[i]: ""));
					pst.setString(3, (optionb != null && optionb.length > i ? optionb[i]: ""));
					pst.setString(4, (optionc != null && optionc.length > i ? optionc[i]: ""));
					pst.setString(5, (optiond != null && optiond.length > i ? optiond[i]: ""));
					pst.setString(6, option.toString());
					pst.setBoolean(7, uF.parseToBoolean(addFlag[i]));
					pst.setInt(8, uF.parseToInt(ansType[i]));
					pst.setString(9, (matrixHeading != null && matrixHeading.length > i ? matrixHeading[i]: ""));
					pst.execute();
					pst.close();

					pst = con.prepareStatement("select max(assessment_question_bank_id) from assessment_question_bank");
					rst = pst.executeQuery();
					while (rst.next()) {
						question_id = rst.getInt(1);
					}
					rst.close();
					pst.close();
					
					boolean assessFlag = false;
					if(hideassessmentid != null && hideassessmentid.length > i){
						pst = con.prepareStatement("select assessment_question_id from assessment_question_details where ref_assessment_question_id = ?");
						pst.setInt(1, uF.parseToInt(hideassessmentid[i]));
						rst = pst.executeQuery();
						//System.out.println("assessment_question_detail pst === > " + pst);
						while (rst.next()) {
							assessFlag = true;
						}
						rst.close();
						pst.close();
					}
//					System.out.println("assessFlag ===> " + assessFlag);
					if(assessFlag == true) {
						pst = con.prepareStatement("update assessment_question_details set question_bank_id=?,weightage=?,assessment_details_id=?," +
								"assessment_section_id=?,updated_by=?,update_date=?,answer_type=?,ref_assessment_question_id=0 where ref_assessment_question_id = ?");
						pst.setInt(1, question_id);
						pst.setDouble(2, marksPerQue);
						pst.setInt(3, uF.parseToInt(getAssessmentId()));
						pst.setInt(4, newSectionId);
						pst.setInt(5, uF.parseToInt(strSessionEmpId));
						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(7, uF.parseToInt(ansType[i]));
						pst.setInt(8, uF.parseToInt(hideassessmentid[i]));
						pst.execute();
						pst.close();
					} else{
						pst = con.prepareStatement("insert into assessment_question_details(question_bank_id,weightage,assessment_details_id," +
								"assessment_section_id,added_by,entry_date,answer_type)values(?,?,?,?, ?,?,?)");
						pst.setInt(1, question_id);
						pst.setDouble(2, marksPerQue);
						pst.setInt(3, uF.parseToInt(getAssessmentId()));
						pst.setInt(4, newSectionId);
						pst.setInt(5, uF.parseToInt(strSessionEmpId));
						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(7, uF.parseToInt(ansType[i]));
						pst.execute();
						pst.close();
					}
				}
				
				if(getAssignToExist() != null && getAssignToExist().equals("Yes")) {
					getAssessmentNameFromID(con, getAssessmentId(), uF);
				}
				
			}
			
			setSectionCnt(""+(uF.parseToInt(getSectionCnt())+1));
 
			pst = con.prepareStatement("update assessment_details set ref_assessment_id = 0 where assessment_details_id = ?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("update assessment_section_details set ref_assessment_section_id = 0 where assessment_details_id = ?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.executeUpdate();
			pst.close();
			
			pst = con.prepareStatement("update assessment_question_details set ref_assessment_question_id = 0 where assessment_details_id = ?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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


	private void insertData(UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			if(getAssessmentAuthor() == null || getAssessmentAuthor().equals("")){
				setAssessmentAuthor(hmEmpName.get(strSessionEmpId));
			}
			if(getAssessmentVersion() == null || getAssessmentVersion().equals("")){
				setAssessmentVersion("1.0");
			}
		if (tab != null && uF.parseToInt(tab) == 0) {

			setAssessmentId(insertStep1Data(con, uF));
//			request.setAttribute("CourseID", getAssessmentId());

		} else if (tab != null && uF.parseToInt(tab) > 1) {

			insertChapterData(con, uF);
//			request.setAttribute("CourseID", getAssessmentId());
		} 
		
		
		String assessName ="";
		if(getAssessmentId() != null && !getAssessmentId().equals("")) {
			assessName = getAssessmentNameById(con, uF, getAssessmentId());
		}
		
		session.setAttribute(MESSAGE, SUCCESSM+""+assessName+" assessment has been created successfully."+END);
		
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}

	}

	private void insertChapterData(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			String sectionName = request.getParameter("sectionName");
			String sectionDescription = request.getParameter("sectionDescription");
			String marksForSection = request.getParameter("marksForSection");
			String questionAttempt = request.getParameter("questionAttempt");
			String hidesectionid = request.getParameter("hidesectionid");
			/*System.out.println("sectionDescription ====> " + sectionDescription);
			System.out.println("sectionCnt ====> " + sectionCnt);*/
			boolean sectionFlag = false;
			int newSectionId = 0;
			pst = con.prepareStatement("select assessment_section_id from assessment_section_details where assessment_section_id = ?");
			pst.setInt(1, uF.parseToInt(hidesectionid));
			rst = pst.executeQuery();
			while (rst.next()) {
				sectionFlag = true;
//				newChapterId = rst.getInt("course_chapter_id");
			}
			rst.close();
			pst.close();
			
			if(sectionFlag == true) {
				pst = con.prepareStatement("update assessment_section_details set assessment_section_name=?,assessment_section_description=?," +
						"assessment_details_id=?,marks_of_section=?,attempt_questions=?,updated_by=?,update_date=? where assessment_section_id = ?");
				pst.setString(1, sectionName);
				pst.setString(2, sectionDescription);
				pst.setInt(3, uF.parseToInt(getAssessmentId()));
				pst.setInt(4, uF.parseToInt(marksForSection));
				pst.setInt(5, uF.parseToInt(questionAttempt));
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(8, uF.parseToInt(hidesectionid));
				pst.execute();
				pst.close();
				
				newSectionId = uF.parseToInt(hidesectionid);
				
			} else {
			
				pst = con.prepareStatement("insert into assessment_section_details(assessment_section_name,assessment_section_description,assessment_details_id" +
						",marks_of_section,attempt_questions,added_by,entry_date)values(?,?,?, ?,?,?,?)");
				pst.setString(1, sectionName);
				pst.setString(2, sectionDescription);
				pst.setInt(3, uF.parseToInt(getAssessmentId()));
				pst.setInt(4, uF.parseToInt(marksForSection));
				pst.setInt(5, uF.parseToInt(questionAttempt));
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("select max(assessment_section_id) as assessment_section_id from assessment_section_details");
				rst = pst.executeQuery();
				while (rst.next()) {
					newSectionId = rst.getInt("assessment_section_id");
				}
				rst.close();
				pst.close();
			}	
				
//						boolean subchapterFlag = false;
//						if(subchapterFlag == true){
			double marksPerQue = 0;
			if(marksForSection != null && !marksForSection.equals("") && questionAttempt != null && !questionAttempt.equals("")){
				marksPerQue = uF.parseToDouble(marksForSection) / uF.parseToDouble(questionAttempt);
				
			}
			
//			MultiPartRequestWrapper mpRequest = (MultiPartRequestWrapper)request;
			
//			System.out.println("marksPerQue ===> " + marksPerQue);
			String question[] = request.getParameterValues("question"+sectionCnt);
			String hideassessmentid[] = request.getParameterValues("hideassessmentid"+sectionCnt);
			
			String addFlag[] = request.getParameterValues("status"+sectionCnt);
			String matrixHeading[] = request.getParameterValues("matrixHeading"+sectionCnt);
			String optiona[] = request.getParameterValues("optiona"+sectionCnt);
			String optionb[] = request.getParameterValues("optionb"+sectionCnt);
			String optionc[] = request.getParameterValues("optionc"+sectionCnt);
			String optiond[] = request.getParameterValues("optiond"+sectionCnt);
			String ansType[] = request.getParameterValues("ansType"+sectionCnt);
			String orientt[] = request.getParameterValues("orientt"+sectionCnt);
			
//			File[] strFolderDoc = mpRequest.getFiles("questionImage"+sectionCnt);    //  
//			String[] strFolderDocFileNames = mpRequest.getFileNames("questionImage"+sectionCnt);
			
//			System.out.println("hideassessmentid ====> " + hideassessmentid.length);
//			System.out.println("matrixHeading ====> " + matrixHeading.length);
//			System.out.println("optiona ====> " + optiona.length);

			for (int i = 0; question != null && i < question.length; i++) {
				String[] correct = request.getParameterValues("correct"+sectionCnt+"_"+ orientt[i]);
				StringBuilder option = new StringBuilder();
 				for (int ab = 0; correct != null && ab < correct.length; ab++) {
					option.append(correct[ab] + ",");
				}
//				System.out.println("hideassessmentid[i] ====> " + hideassessmentid[i]);
				int question_id = 0;
				pst = con.prepareStatement("insert into assessment_question_bank(question_text, option_a, option_b, option_c, option_d, correct_ans, is_add, answer_type, que_matrix_heading)values(?,?,?,?, ?,?,?,?, ?)");
				pst.setString(1, question[i]);
				pst.setString(2, (optiona != null && optiona.length > i ? optiona[i]: ""));
				pst.setString(3, (optionb != null && optionb.length > i ? optionb[i]: ""));
				pst.setString(4, (optionc != null && optionc.length > i ? optionc[i]: ""));
				pst.setString(5, (optiond != null && optiond.length > i ? optiond[i]: ""));
				pst.setString(6, option.toString());
				pst.setBoolean(7, uF.parseToBoolean(addFlag[i]));
				pst.setInt(8, uF.parseToInt(ansType[i]));
				pst.setString(9, (matrixHeading != null && matrixHeading.length > i ? matrixHeading[i]: ""));
				pst.execute();
				pst.close();

				pst = con.prepareStatement("select max(assessment_question_bank_id) from assessment_question_bank");
				rst = pst.executeQuery();
				while (rst.next()) {
					question_id = rst.getInt(1);
				}
				rst.close();
				pst.close();
				
				boolean assessFlag = false;
				if(hideassessmentid != null && hideassessmentid.length > i){
					pst = con.prepareStatement("select assessment_question_id from assessment_question_details where assessment_question_id = ?");
					pst.setInt(1, uF.parseToInt(hideassessmentid[i]));
					rst = pst.executeQuery();
					while (rst.next()) {
						assessFlag = true;
					}
					rst.close();
					pst.close();
				}
//				System.out.println("assessFlag ===> " + assessFlag);
				if(assessFlag == true) {
					pst = con.prepareStatement("update assessment_question_details set question_bank_id=?,weightage=?,assessment_details_id=?," +
						"assessment_section_id=?,updated_by=?,update_date=?,answer_type=? where assessment_question_id = ?");
					pst.setInt(1, question_id);
					pst.setDouble(2, marksPerQue);
					pst.setInt(3, uF.parseToInt(getAssessmentId()));
					pst.setInt(4, newSectionId);
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(7, uF.parseToInt(ansType[i]));
					pst.setInt(8, uF.parseToInt(hideassessmentid[i]));
					pst.execute();
					pst.close();
				} else {
					pst = con.prepareStatement("insert into assessment_question_details(question_bank_id,weightage,assessment_details_id," +
						"assessment_section_id,added_by,entry_date,answer_type)values(?,?,?,?, ?,?,?)");
					pst.setInt(1, question_id);
					pst.setDouble(2, marksPerQue);
					pst.setInt(3, uF.parseToInt(getAssessmentId()));
					pst.setInt(4, newSectionId);
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(7, uF.parseToInt(ansType[i]));
					pst.execute();
					pst.close();
				}
			}
			setSectionCnt(""+(uF.parseToInt(getSectionCnt())+1));
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

	
	private String insertStep1Data(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;

		String assessmentIDNew = null;

		try {
			pst = con.prepareStatement("insert into assessment_details(assessment_name,assessment_subject,assessment_author,date_of_creation," +
					"assessment_version,assessment_description,added_by,entry_date,time_to_attempt_assessment,assessment_time_duration," +
					"marks_grade_type,marks_grade_standard)values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
			pst.setString(1, getAssessmentName());
			pst.setString(2, getAssessmentSubject());
			pst.setString(3, getAssessmentAuthor());
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(5, getAssessmentVersion());
			pst.setString(6, getAssessmentPreface());
			pst.setInt(7, uF.parseToInt(strSessionEmpId));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(9, uF.parseToInt(getTimestoAttemptAssessment()));
			pst.setString(10, getAssessmentTimeDuration());
			pst.setInt(11, uF.parseToInt(getMarksGradeType()));
			pst.setInt(12, uF.parseToInt(getMarksGradeStandard()));
			pst.execute();
			pst.close();

			pst = con.prepareStatement(" select max(assessment_details_id) as assessment_details_id from assessment_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				assessmentIDNew = rst.getString("assessment_details_id");
			}
			rst.close();
			pst.close();
 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		
		return assessmentIDNew;
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

	public String getAssignToExist() {
		return assignToExist;
	}

	public void setAssignToExist(String assignToExist) {
		this.assignToExist = assignToExist;
	}

	public String getMarksGradeType() {
		return marksGradeType;
	}

	public void setMarksGradeType(String marksGradeType) {
		this.marksGradeType = marksGradeType;
	}

	public String getMarksGradeStandard() {
		return marksGradeStandard;
	}

	public void setMarksGradeStandard(String marksGradeStandard) {
		this.marksGradeStandard = marksGradeStandard;
	}

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

	public String getTimestoAttemptAssessment() {
		return timestoAttemptAssessment;
	}

	public void setTimestoAttemptAssessment(String timestoAttemptAssessment) {
		this.timestoAttemptAssessment = timestoAttemptAssessment;
	}

	public String getAssessmentTimeDuration() {
		return assessmentTimeDuration;
	}

	public void setAssessmentTimeDuration(String assessmentTimeDuration) {
		this.assessmentTimeDuration = assessmentTimeDuration;
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

	public List<FillSubject> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(List<FillSubject> subjectList) {
		this.subjectList = subjectList;
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

	public String getMarksGradeTypeDefault() {
		return marksGradeTypeDefault;
	}

	public void setMarksGradeTypeDefault(String marksGradeTypeDefault) {
		this.marksGradeTypeDefault = marksGradeTypeDefault;
	}

	public String getMarksGradeStandardDefault() {
		return marksGradeStandardDefault;
	}

	public void setMarksGradeStandardDefault(String marksGradeStandardDefault) {
		this.marksGradeStandardDefault = marksGradeStandardDefault;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}
