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

import com.konnect.jpms.select.FillSubject;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewCourseDetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private String operation;

	CommonFunctions CF = null;

	private  String courseId;
	private  String fromPage;
	private String courseName;
	private String courseSubject;
	private String courseAuthor;
	private String courseVersion;
	private String coursePreface;
	private String subjectID = null;
	private List<FillSubject> subjectList;
	
	public String execute() {

		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		
		String strOperation = request.getParameter("operation");
		String strID = request.getParameter("ID");
		
		request.setAttribute(PAGE, "/jsp/training/ViewCourseDetails2.jsp");
		request.setAttribute(TITLE, "View Course Details");
		
		subjectList = new FillSubject(request).fillSubjectName();

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		getCourseData(uF);
		getChapterDataList(uF);
		getSubChapterDataList(uF);
		getAssessmentsList(uF);
		getContentsList(uF);
		getCourseDetails();
		return LOAD;
	}
	
	private void getCourseDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
//		List<List<String>> requestList = new ArrayList<List<String>>();
		List<String> courseList = new ArrayList<String>();
		Map<String, String> hmCourseDetails = new HashMap<String, String>();
		Map<String, List<String>> hmCoursesOnPerentId = new HashMap<String, List<String>>();
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmSubjectName = CF.getSubjectsMap(con);
			
			pst=con.prepareStatement("select learning_plan_stage_name_id from learning_plan_stage_details where learning_plan_stage_name_id > 0 and learning_type='Course'");
			rst=pst.executeQuery();
			List<String> alAssignCourse = new ArrayList<String>();
			while(rst.next()){
				if(!alAssignCourse.contains(rst.getString("learning_plan_stage_name_id"))){
					alAssignCourse.add(rst.getString("learning_plan_stage_name_id"));
				}
			}
			rst.close();
			pst.close();
			Map<String, String> hmCourseData = new HashMap<String, String>();
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from course_details where course_id =?");
			pst = con.prepareStatement(strQuery.toString());
			pst.setInt(1, uF.parseToInt(getCourseId()));
			rst = pst.executeQuery();
//			System.out.println("pst ====> "+pst);
			
			StringBuilder sbCourses = new StringBuilder();
			StringBuilder sbStauts = new StringBuilder();
			StringBuilder sbCourseData = new StringBuilder();
			while (rst.next()) {
				String updateDate = null;
 
				if(rst.getString("update_date") != null) {
					updateDate = uF.getDateFormat(rst.getString("update_date"), DBDATE, CF.getStrReportDateFormat());
				} else {
					updateDate = uF.getDateFormat(rst.getString("entry_date"), DBDATE, CF.getStrReportDateFormat());
				}
				sbCourses.replace(0, sbCourses.length(), "");
				sbStauts.replace(0, sbStauts.length(), "");
				sbCourseData.replace(0, sbCourseData.length(), "");
				boolean statusFlag = checkCourseStatus(con, rst.getString("course_id"));

				if(statusFlag == false) {
					sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus\" > ");
					/*sbStauts.append("<img src=\"images1/icons/pullout.png\" title=\"Waiting for live\" />");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Waiting for live\"></i>");
					
					sbStauts.append("</div>");
				} else {
					sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatus\" > ");
					 /*sbStauts.append("<img src=\"images1/icons/approved.png\" title=\"Live\" />");*/
					sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Live\" ></i>");
					
					sbStauts.append("</div>");
				}
				sbCourseData.append("<div style=\"float:right;\" id=\"myDivM\" > ");

				boolean statusFlag1 = checkCourseStatus1(con, rst.getString("course_id"));
				if(statusFlag1 == false) {
					sbCourseData.append("<a onclick=\"editCourse('"+rst.getString("course_id")+"')\" href=\"javascript:void(0)\"> <i class=\"fa fa-pencil-square-o\" title=\"Edit Course\"></i></a> ");
				} else {
					sbCourseData.append("<a href=\"javascript:void(0);\" onclick=\"createNewVersionOfCourse("+ rst.getString("course_id") +");\"> <i class=\"fa fa-pencil-square-o\" title=\"Edit Assessment\"></i> </a> ");
				}
				
				if(!alAssignCourse.contains(rst.getString("course_id"))){
					sbCourseData.append("<a class=\"del\" title=\"Delete Course\" onclick=\"deleteCourse('"+rst.getString("course_id")+"')\" href=\"javascript:void(0)\" ><i class=\"fa fa-trash\"></i></a>");
				}
				
				
				
				sbCourseData.append("</div>");
				sbCourses.append("<div>" + sbStauts + " <strong>" + rst.getString("course_name") + "</strong> author is <strong>" + uF.showData(rst.getString("author"), "-") + "</strong>, subject is <strong>"+ uF.showData(hmSubjectName.get(rst.getString("course_subject")), "-") + "</strong>, version <strong>"+ uF.showData(rst.getString("course_version"), "-") +"</strong>." + sbCourseData+"</div><div style=\"margin-left: 20px;\"><i> Last updated on "+updateDate+"</i></div>");
				
			}
			rst.close();
			pst.close();
			
			request.setAttribute("sbCourses", sbCourses.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		
		
	}
	
	private boolean checkCourseStatus(Connection con, String courseId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean courseStatusFlag = false;
		try {
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_type = 'Course' and learning_plan_stage_name_id = ? and ? between from_date and to_date");
			pst.setInt(1, uF.parseToInt(courseId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rst = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			while (rst.next()) {
				courseStatusFlag = true;
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
		
		return courseStatusFlag;
	}
	
	private boolean checkCourseStatus1(Connection con, String courseId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean courseStatusFlag = false;
		try {
			
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_type = 'Course' and learning_plan_stage_name_id = ?");
			pst.setInt(1, uF.parseToInt(courseId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rst = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			while (rst.next()) {
				courseStatusFlag = true;
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
		
		return courseStatusFlag;
	}
	private void getSubChapterDataList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, List<List<String>>> hmSubchapterData = new HashMap<String, List<List<String>>>();
//			List<List<String>> subchapterList = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from course_subchapter_details where course_id = ? order by course_subchapter_id");
			pst.setInt(1, uF.parseToInt(getCourseId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> subchapterList = hmSubchapterData.get(rs.getString("course_chapter_id"));
				if(subchapterList == null ) subchapterList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("course_subchapter_id"));
				innerList.add(rs.getString("course_subchapter_name"));
				innerList.add(rs.getString("subchapter_description"));
				subchapterList.add(innerList);
				hmSubchapterData.put(rs.getString("course_chapter_id"), subchapterList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmSubchapterData", hmSubchapterData);
//			System.out.println("hmSubchapterData ===> " + hmSubchapterData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getAssessmentsList(UtilityFunctions uF) {
		
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
			
			Map<String, List<List<String>>> hmAssessmentData = new HashMap<String, List<List<String>>>();
			Map<String, String> hmAssessTotWeight = new HashMap<String, String>();
			pst = con.prepareStatement("select * from course_assessment_details cad, course_question_bank qb where course_question_bank_id = assessment_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				double totWeightage = uF.parseToDouble(uF.showData(hmAssessTotWeight.get(rs.getString("course_subchapter_id")), "0"));
				totWeightage += uF.parseToDouble(rs.getString("weightage"));
				hmAssessTotWeight.put(rs.getString("course_subchapter_id"), String.valueOf(totWeightage));
				
				List<List<String>> assessmentList = hmAssessmentData.get(rs.getString("course_subchapter_id"));
				if(assessmentList == null){
					assessmentList = new ArrayList<List<String>>();
				}
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("course_question_text"));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("assessment_id"));
				innerList.add(rs.getString("course_assessment_id"));
				innerList.add(rs.getString("option_a"));
				innerList.add(rs.getString("option_b"));
				innerList.add(rs.getString("option_c"));
				innerList.add(rs.getString("option_d"));
				innerList.add(rs.getString("correct_ans"));
				innerList.add(rs.getString("answer_type"));
				innerList.add(rs.getString("is_add"));
				innerList.add(rs.getString("course_subchapter_id"));
				innerList.add(rs.getString("course_chapter_id"));
				innerList.add(rs.getString("course_id"));
				
				assessmentList.add(innerList);
				hmAssessmentData.put(rs.getString("course_subchapter_id"), assessmentList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAssessmentData", hmAssessmentData);
			request.setAttribute("hmAssessTotWeight", hmAssessTotWeight);
			request.setAttribute("hmAnstypeName", hmAnstypeName);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void getContentsList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, List<List<String>>> hmContentData = new HashMap<String, List<List<String>>>();
			Map<String, String> hmContentImg = new HashMap<String, String>();
			pst = con.prepareStatement("select * from course_content_details where course_id = ? order by course_content_id");
			pst.setInt(1, uF.parseToInt(getCourseId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> contentList = hmContentData.get(rs.getString("course_subchapter_id"));
				if(contentList == null){
					contentList = new ArrayList<List<String>>();
				}
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("course_content_id"));
				innerList.add(rs.getString("course_content_name"));
				innerList.add(rs.getString("content_type"));
				innerList.add(rs.getString("course_subchapter_id"));
				innerList.add(rs.getString("course_chapter_id"));
				innerList.add(rs.getString("course_id"));
				if(rs.getString("content_type") != null &&(rs.getString("content_type").equals("IMAGE") || rs.getString("content_type").equals("PDF") || rs.getString("content_type").equals("ATTACH") || rs.getString("content_type").equals("PPT"))) {
					String filePath = null;
//					System.out.println("CF.getIsRemoteLocation() ===> " + CF.getIsRemoteLocation());
					if(CF.getIsRemoteLocation()){
						filePath = CF.getStrDocRetriveLocation() + rs.getString("course_content_name");
					} else {
						filePath = request.getContextPath()+"/userImages/" + rs.getString("course_content_name") + "";
					}
					hmContentImg.put(rs.getString("course_content_id"), filePath);
					innerList.add(filePath);
				} else {
					innerList.add("");
				}
				innerList.add(rs.getString("content_title"));
				innerList.add(rs.getString("content_url"));
				contentList.add(innerList);
				hmContentData.put(rs.getString("course_subchapter_id"), contentList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmContentImg ====> " + hmContentImg);
			request.setAttribute("hmContentData", hmContentData);
			request.setAttribute("hmContentImg", hmContentImg);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public void getCourseData(UtilityFunctions uF) {

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
			
			pst = con.prepareStatement("select * from course_details where course_id = ? ");
			pst.setInt(1, uF.parseToInt(getCourseId()));
			rs = pst.executeQuery();
			 String crsPreface = null;
			while (rs.next()) {
				setCourseName(rs.getString("course_name"));
				setSubjectID(rs.getString("course_subject"));
				setCourseSubject(hmCourseSubject.get(rs.getString("course_subject")));
				setCourseAuthor(rs.getString("author"));
				setCourseVersion(rs.getString("course_version"));
				crsPreface = rs.getString("preface");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("crsPreface", crsPreface);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getChapterDataList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			List<List<String>> chapterList = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from course_chapter_details where course_id = ? order by course_chapter_id");
			pst.setInt(1, uF.parseToInt(getCourseId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("course_chapter_id"));
				innerList.add(rs.getString("course_chapter_name"));
				innerList.add(rs.getString("chapter_description"));
				chapterList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("chapterList", chapterList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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

	

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseSubject() {
		return courseSubject;
	}

	public void setCourseSubject(String courseSubject) {
		this.courseSubject = courseSubject;
	}

	public String getCourseAuthor() {
		return courseAuthor;
	}

	public void setCourseAuthor(String courseAuthor) {
		this.courseAuthor = courseAuthor;
	}

	public String getCourseVersion() {
		return courseVersion;
	}

	public void setCourseVersion(String courseVersion) {
		this.courseVersion = courseVersion;
	}

	public String getCoursePreface() {
		return coursePreface;
	}

	public void setCoursePreface(String coursePreface) {
		this.coursePreface = coursePreface;
	}

		// fields for 2nd screen Stage info **************
	
	
	// Fields for 3rd Screen ****************

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
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


	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	
}
