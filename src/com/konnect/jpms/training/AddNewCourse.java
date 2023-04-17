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
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddNewCourse extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private String operation;

	private String ID;

	CommonFunctions CF = null;

	private String del;
	private  String quest_id;
	private  String courseId;
	private String stepSubmit;
	private  String stepSave;
	private String tab;
	private  String chapterCnt;
	
    
	private static Logger log = Logger.getLogger(AddNewCourse.class);
	
	String subjectID = null;
	
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
		
		request.setAttribute(PAGE, "/jsp/training/AddNewCourse.jsp");
		if (strOperation != null && strOperation.equalsIgnoreCase("E")) {
			request.setAttribute(TITLE, "Edit Course");
		} else {
			request.setAttribute(TITLE, "Add Course");
		}
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/

		subjectList = new FillSubject(request).fillSubjectName();

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
//		System.out.println("Plan ID ===> "+getPlanId());
		/*if(getPlanId() == null) {
			setPlanId(strID);
		}*/
//		System.out.println("strOperation ===> " + strOperation);
//		System.out.println("getCourseId() ===> " + getCourseId());
//		System.out.println("strID ===> " + strID);
		
		if (strOperation != null && strOperation.equalsIgnoreCase("A")) {
			insertData(uF);
		} else if (strOperation != null && strOperation.equalsIgnoreCase("E")) {
			updateData(getCourseId(), uF);
		} else if (strOperation != null && strOperation.equalsIgnoreCase("D")) {
			return deleteCourse(getCourseId(), uF);
		}

		if (tab == null) {
			setTab("0");
		} else if (tab != null && uF.parseToInt(getTab()) == 0) {
			setTab("1");
		} else if (uF.parseToInt(getTab()) >= 1) {
			setTab(""+(uF.parseToInt(getTab())+1));
		}
		if (chapterCnt == null) {
			setChapterCnt("1");
		}
//		System.out.println("getStepSave=="+getStepSave());
		if (getStepSave() != null && getStepSave().equals("Save And Exit")) {
			return SUCCESS;
		}
		
		getAnsType();
		getCourseData(uF);
		getChapterDataList(uF);
		getSubChapterDataList(uF);
		getAssessmentsList(uF);
		getContentsList(uF);
		
		return LOAD;
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
//			List<List<String>> assessmentList = new ArrayList<List<String>>();
			Map<String, List<List<String>>> hmAssessmentData = new HashMap<String, List<List<String>>>();
			Map<String, String> hmAssessTotWeight = new HashMap<String, String>();
			pst = con.prepareStatement("select * from course_assessment_details cad, course_question_bank qb where course_question_bank_id = assessment_id order by course_assessment_id");
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
				innerList.add(rs.getString("course_question_text"));//0
				innerList.add(rs.getString("weightage"));//1
				innerList.add(rs.getString("assessment_id"));//2
				innerList.add(rs.getString("course_assessment_id"));//3
				innerList.add(rs.getString("option_a"));//4
				innerList.add(rs.getString("option_b"));//5
				innerList.add(rs.getString("option_c"));//6
				innerList.add(rs.getString("option_d"));//7
				innerList.add(rs.getString("correct_ans"));//8
				innerList.add(rs.getString("answer_type"));//9
				
				innerList.add(rs.getString("is_add"));//10
				innerList.add(rs.getString("course_subchapter_id"));//11
				innerList.add(rs.getString("course_chapter_id"));//12
				innerList.add(rs.getString("course_id"));//13
				
				assessmentList.add(innerList);
				hmAssessmentData.put(rs.getString("course_subchapter_id"), assessmentList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAssessmentData", hmAssessmentData);
			request.setAttribute("hmAssessTotWeight", hmAssessTotWeight);

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
				innerList.add(rs.getString("course_content_id"));//0
				innerList.add(rs.getString("course_content_name"));//1
				innerList.add(rs.getString("content_type"));//2
				innerList.add(rs.getString("course_subchapter_id"));//3
				innerList.add(rs.getString("course_chapter_id"));//4
				innerList.add(rs.getString("course_id"));//5
				if(rs.getString("content_type") != null &&(rs.getString("content_type").equals("IMAGE") || rs.getString("content_type").equals("PDF") || rs.getString("content_type").equals("ATTACH") || rs.getString("content_type").equals("PPT"))) {
					String filePath = null;
					if(CF.getIsRemoteLocation()){
						filePath = CF.getStrDocRetriveLocation() + rs.getString("course_content_name");
					}else{
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
			pst = con.prepareStatement("select * from course_details where course_id = ? ");
			pst.setInt(1, uF.parseToInt(getCourseId()));
			rs = pst.executeQuery();
			String crsPreface = null;
			while (rs.next()) {
				setCourseName(rs.getString("course_name"));
				setSubjectID(rs.getString("course_subject"));
				setCourseAuthor(rs.getString("author"));
				
				String dblVersion = "1.0";
				if(rs.getString("course_version") != null) {
					dblVersion = rs.getString("course_version").replaceAll("[^.0-9]", "");
				}
//				System.out.println("dblVersion ===> " + dblVersion);
//				int intVersion = 0;
				String newVersion = "";
				if(dblVersion.contains(".9")) {
					double dblver = uF.parseToDouble(dblVersion);
					int intVersion = (int)dblver + 1;
					newVersion = intVersion+".0";
				} else {
					double dblVers = uF.parseToDouble(dblVersion) + 0.1;
					newVersion = uF.formatIntoOneDecimalWithOutComma(dblVers);
				}
//				System.out.println("newVersion ===> " + newVersion);
				
				setCourseVersion(newVersion);
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
					sb.append("<option value=\""
							+ rs.getString("answer_type")
							+ "\" selected>"
							+ rs.getString("answer_type_name")
							+ "</option>");
				} else {
					sb.append("<option value=\""
							+ rs.getString("answer_type") + "\">"
							+ rs.getString("answer_type_name")
							+ "</option>");
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


	private String deleteCourse(String courseID, UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		try {

			con = db.makeConnection(con);

			String crsName = getCourseNameById(con, uF, courseID);
			
			pst = con.prepareStatement("delete from course_details where course_id = ? ");
			pst.setInt(1, uF.parseToInt(courseID));
			pst.execute();
			pst.close();

			pst = con.prepareStatement("delete from course_assessment_details where course_id = ? ");
			pst.setInt(1, uF.parseToInt(courseID));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from course_chapter_details where course_id = ? ");
			pst.setInt(1, uF.parseToInt(courseID));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from course_content_details where course_id = ? ");
			pst.setInt(1, uF.parseToInt(courseID));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from course_subchapter_details where course_id = ? ");
			pst.setInt(1, uF.parseToInt(courseID));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+""+crsName+" course has been deleted successfully."+END);
//			session.setAttribute(MESSAGE, SUCCESSM+"You have deleted course successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	private void updateData(String courseID, UtilityFunctions uF) {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		boolean courseStatusFlag = false;
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_type = 'Course' and learning_plan_stage_name_id = ?");
			pst.setInt(1, uF.parseToInt(courseID));
			rst = pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			while (rst.next()) {
				courseStatusFlag = true;
			}
			rst.close();
			pst.close();
			
			if(courseStatusFlag == true && (getStepSubmit() != null || getStepSave() != null)){
				String crsName = getCourseNameById(con, uF, courseID);
				
				createNewVersionOfCourse(con, uF, courseID);
				updateStep1Data(con, uF, courseID);
				
				String newCrsName = getCourseNameById(con, uF, getCourseId());
				session.setAttribute(MESSAGE, SUCCESSM+""+newCrsName+" course has been created new version of "+crsName+" course successfully."+END);
			} else {
				if (getStepSubmit() != null || getStepSave() != null){
//					System.out.println("courseID ===> " + courseID);
					insertUpdatedStepData(con, courseID, uF);
				}
			
				String crsName = getCourseNameById(con, uF, courseID);
				session.setAttribute(MESSAGE, SUCCESSM+""+crsName+" course has been updated successfully."+END);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private String getCourseNameById(Connection con, UtilityFunctions uF, String courseID) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		String crsName = null;
		try {
				pst = con.prepareStatement("select course_name from course_details where course_id = ?");
				pst.setInt(1, uF.parseToInt(courseID));
				rst = pst.executeQuery();
				while (rst.next()) {
					crsName = rst.getString("course_name");
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
		return crsName;
	}


	private void updateCourseData(Connection con, String courseID, UtilityFunctions uF) {

//		System.out.println("updatedStepData courseID ===> " + courseID); 
		PreparedStatement pst = null;

		try {
			pst = con.prepareStatement("update course_details set course_name=?,course_subject=?,author=?,date_of_creation=?," +
					"course_version=?,preface=?,updated_by=?,update_date=? where course_id = ?");
			pst.setString(1, getCourseName());
			pst.setString(2, getCourseSubject());
			pst.setString(3, getCourseAuthor());
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(5, getCourseVersion());
			pst.setString(6, getCoursePreface());
			pst.setInt(7, uF.parseToInt(strSessionEmpId));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(9, uF.parseToInt(courseID));
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


	private void createNewVersionOfCourse(Connection con, UtilityFunctions uF, String courseID) {

//		System.out.println("createNewVersionOfCourse courseID ===> " + courseID);
		PreparedStatement pst = null, pstt = null, psttt = null, pst1 = null, pstt1 = null, pst2 = null, pst3 = null, pstt3 = null, pst4 = null, pst5 = null, pstt5 = null, pst6 = null, pstt6 = null, pst7 = null, pstt7 = null;
		ResultSet rst = null, rst1 = null, rstt1 = null, rst2 = null, rst3 = null, rst4 = null, rst5 = null, rst6 = null, rst7 = null;

		try {
			
			pst = con.prepareStatement("select * from course_details where course_id = ?");
			pst.setInt(1, uF.parseToInt(courseID));
			rst = pst.executeQuery();
//			System.out.println("pst course_details ===> " + pst);
			String newVerCourseName = null;
//			String courseId11 = null;
			while (rst.next()) {
				pstt = con.prepareStatement("insert into course_details(course_name,course_subject,author,date_of_creation," +
						"course_version,preface,added_by,entry_date,parent_course_id,ref_course_id,root_course_id)values(?,?,?,?, ?,?,?,?, ?,?,?)");
				pstt.setString(1, rst.getString("course_name"));
				pstt.setString(2, rst.getString("course_subject"));
				pstt.setString(3, rst.getString("author"));
				pstt.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pstt.setString(5, getCourseVersion());
				pstt.setString(6, getCoursePreface());
				pstt.setInt(7, uF.parseToInt(strSessionEmpId));
				pstt.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
				pstt.setInt(9, uF.parseToInt(courseID));
				pstt.setInt(10, uF.parseToInt(courseID));
				if(rst.getString("root_course_id") != null) {
					pstt.setInt(11, uF.parseToInt(rst.getString("root_course_id")));
				} else {
					pstt.setInt(11, uF.parseToInt(courseID));
				}
				pstt.executeUpdate();
				pstt.close();
	
				pstt1 = con.prepareStatement(" select course_id,course_name from course_details where course_id in(select max(course_id) as course_id from course_details)");
				rstt1 = pstt1.executeQuery();
				while (rstt1.next()) {
					setCourseId(rstt1.getString("course_id"));
//					courseId11=rstt1.getString("course_id");
					newVerCourseName = rstt1.getString("course_name");
				}
				rstt1.close();
				pstt1.close();
			}
			rst.close();
			pst.close();
			
			psttt = con.prepareStatement("select * from course_chapter_details where course_id = ?");
			psttt.setInt(1, uF.parseToInt(courseID));
			rst1 = psttt.executeQuery();
//			System.out.println("pst course_chapter_details ===> " + pst);
			while (rst1.next()) {
				pst1 = con.prepareStatement("insert into course_chapter_details(course_chapter_name,chapter_description,course_id" +
					",added_by,entry_date,ref_course_chapter_id)values(?,?,?, ?,?,?)");
				pst1.setString(1, rst1.getString("course_chapter_name"));
				pst1.setString(2, rst1.getString("chapter_description"));
				pst1.setInt(3, uF.parseToInt(getCourseId()));
				pst1.setInt(4, uF.parseToInt(strSessionEmpId));
				pst1.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst1.setInt(6, uF.parseToInt(rst1.getString("course_chapter_id")));
				pst1.executeUpdate();
				pst1.close();
				
				int newChapterId = 0;
				pst2 = con.prepareStatement(" select max(course_chapter_id) as course_chapter_id from course_chapter_details");
				rst2 = pst2.executeQuery();
				while (rst2.next()) {
					newChapterId = rst2.getInt("course_chapter_id");
				}
				rst2.close();
				pst2.close();
				
				pst3 = con.prepareStatement("select * from course_subchapter_details where course_id = ? and course_chapter_id = ?");
				pst3.setInt(1, uF.parseToInt(courseID));
				pst3.setInt(2, uF.parseToInt(rst1.getString("course_chapter_id")));
				rst3 = pst3.executeQuery();
//				System.out.println("pst course_subchapter_details ===> " + pst);
				while (rst3.next()) {
					pstt3 = con.prepareStatement("insert into course_subchapter_details(course_subchapter_name,subchapter_description,course_id" +
						",course_chapter_id,added_by,entry_date,ref_course_subchapter_id)values(?,?,?, ?,?,?, ?)");
					pstt3.setString(1, rst3.getString("course_subchapter_name"));
					pstt3.setString(2, rst3.getString("subchapter_description"));
					pstt3.setInt(3, uF.parseToInt(getCourseId()));
					pstt3.setInt(4, newChapterId);
					pstt3.setInt(5, uF.parseToInt(strSessionEmpId));
					pstt3.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pstt3.setInt(7, uF.parseToInt(rst3.getString("course_subchapter_id")));
					pstt3.executeUpdate();
					pstt3.close();
					
					int newSubchapterId = 0;
					pst4 = con.prepareStatement("select max(course_subchapter_id) as course_subchapter_id from course_subchapter_details");
					rst4 = pst4.executeQuery();
					while (rst4.next()) {
						newSubchapterId = rst4.getInt("course_subchapter_id");
					}
					rst4.close();
					pst4.close();
					
					pst5 = con.prepareStatement("select * from course_content_details where course_id = ? and course_chapter_id = ? and course_subchapter_id = ?");
					pst5.setInt(1, uF.parseToInt(courseID));
					pst5.setInt(2, uF.parseToInt(rst1.getString("course_chapter_id")));
					pst5.setInt(3, uF.parseToInt(rst3.getString("course_subchapter_id")));
					rst5 = pst5.executeQuery();
//					System.out.println("pst ===> " + pst);
					while (rst5.next()) {
						pstt5 = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
							"course_subchapter_id,added_by,entry_date,ref_course_content_id)values(?,?,?,?, ?,?,?,?)");
						pstt5.setString(1, rst5.getString("course_content_name"));
						pstt5.setString(2, rst5.getString("content_type"));
						pstt5.setInt(3, uF.parseToInt(getCourseId()));
						pstt5.setInt(4, newChapterId);
						pstt5.setInt(5, newSubchapterId);
						pstt5.setInt(6, uF.parseToInt(strSessionEmpId));
						pstt5.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
						pstt5.setInt(8, uF.parseToInt(rst5.getString("course_content_id")));
						pstt5.executeUpdate();
						pstt5.close();
					}
					rst5.close();
					pst5.close();
					
					pst6 = con.prepareStatement("select * from course_assessment_details where course_id = ? and course_chapter_id = ? and course_subchapter_id = ?");
					pst6.setInt(1, uF.parseToInt(courseID));
					pst6.setInt(2, uF.parseToInt(rst1.getString("course_chapter_id")));
					pst6.setInt(3, uF.parseToInt(rst3.getString("course_subchapter_id")));
					rst6 = pst6.executeQuery();
//					System.out.println("pst ===> " + pst);
					while (rst6.next()) {
						pstt6 = con.prepareStatement("insert into course_assessment_details(assessment_id,weightage,course_id,course_chapter_id," +
							"course_subchapter_id,added_by,entry_date,ref_course_assessment_id)values(?,?,?,?, ?,?,?,?)");
						pstt6.setInt(1, uF.parseToInt(rst6.getString("assessment_id")));
						pstt6.setDouble(2, uF.parseToDouble(rst6.getString("weightage")));
						pstt6.setInt(3, uF.parseToInt(getCourseId()));
						pstt6.setInt(4, newChapterId);
						pstt6.setInt(5, newSubchapterId);
						pstt6.setInt(6, uF.parseToInt(strSessionEmpId));
						pstt6.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
						pstt6.setInt(8, uF.parseToInt(rst6.getString("course_assessment_id")));
						pstt6.executeUpdate();
						pstt6.close();
					}
					rst6.close();
					pst6.close();
				}
				rst3.close();
				pst3.close();
			}
			rst1.close();
			psttt.close();
			
//			System.out.println("getAssignToExist() ===> " + getAssignToExist());
			if(getAssignToExist() != null && getAssignToExist().equals("Yes")) {
				
				pstt7 = con.prepareStatement("update learning_plan_stage_details set learning_plan_stage_name_id = ?, learning_plan_stage_name = ? where learning_plan_stage_name_id = ? and learning_type = 'Course'");
				pstt7.setInt(1, uF.parseToInt(getCourseId()));
				pstt7.setString(2, newVerCourseName);
				pstt7.setInt(3, uF.parseToInt(courseID));
				pstt7.executeUpdate();
				pstt7.close();
//				System.out.println("pstt7 =====> " + pstt7);
				
				getCourseNameFromID(con, getCourseId(), uF);
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
			if(rst1 !=null){
				try {
					rst1.close();
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
			if(rst3 !=null){
				try {
					rst3.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rst4 !=null){
				try {
					rst4.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rst5 !=null){
				try {
					rst5.close();
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
			if(rst7 !=null){
				try {
					rst7.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rstt1 !=null){
				try {
					rstt1.close();
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
			if(pst3 !=null){
				try {
					pst3.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst4 !=null){
				try {
					pst4.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst5 !=null){
				try {
					pst5.close();
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
			if(pst7 !=null){
				try {
					pst7.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstt !=null){
				try {
					pstt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstt1 !=null){
				try {
					pstt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstt3 !=null){
				try {
					pstt3.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstt5 !=null){
				try {
					pstt5.close();
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
			if(pstt7 !=null){
				try {
					pstt7.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(psttt !=null){
				try {
					psttt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	
	private String getCourseNameFromID(Connection con, String courseID, UtilityFunctions uF) {
		
		PreparedStatement pst = null, pstt = null;
		ResultSet rst = null;
		
		String courseName1 = "";
		try {
			
			pst = con.prepareStatement(" select course_name from course_details where course_id = ?");
			pst.setInt(1, uF.parseToInt(courseID));
			rst = pst.executeQuery();
//			System.out.println("pst7 select =====> " + pst);
			while (rst.next()) {
				courseName1 = rst.getString("course_name");
			}
			rst.close();
			pst.close();
//			System.out.println("courseName1 =====> " + courseName1);
			
			pstt = con.prepareStatement("update learning_plan_stage_details set learning_plan_stage_name = ? where learning_plan_stage_name_id = ? and learning_type = 'Course'");
			pstt.setString(1, courseName1);
			pstt.setInt(2, uF.parseToInt(courseID));
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
			if(pstt !=null){
				try {
					pstt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return courseName1;
	}

	
	private void insertUpdatedStepData(Connection con, String courseID, UtilityFunctions uF) {

//		System.out.println("insertUpdatedStepData courseID ===> " + courseID);
//		System.out.println("insertUpdatedStepData tab ===> " + tab);
		try {
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			if(getCourseAuthor() == null || getCourseAuthor().equals("")){
				setCourseAuthor(hmEmpName.get(strSessionEmpId));
			}
			if(getCourseVersion() == null || getCourseVersion().equals("")){
				setCourseVersion("1.0");
			}
		if (tab != null && uF.parseToInt(tab) == 0) {

			updateCourseData(con, courseID, uF);
//			request.setAttribute("CourseID", getCourseId());

		} else if (tab != null && uF.parseToInt(tab) > 1) {

			insertChapterData(con, uF);
//			request.setAttribute("CourseID", getCourseId());
		} 
		
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

	

	private void updateStep1Data(Connection con, UtilityFunctions uF, String courseID) {

		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			
			if(uF.parseToInt(getTab()) == 0) {
			pst = con.prepareStatement("update course_details set course_name=?,course_subject=?,author=?,date_of_creation=?," +
					"course_version=?,preface=?,updated_by=?,update_date=?,ref_course_id = 0 where ref_course_id = ?");
			pst.setString(1, getCourseName());
			pst.setString(2, getCourseSubject());
			pst.setString(3, getCourseAuthor());
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(5, getCourseVersion());
			pst.setString(6, getCoursePreface());
			pst.setInt(7, uF.parseToInt(strSessionEmpId));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(9, uF.parseToInt(courseID));
			pst.execute();
			pst.close();
			
			if(getAssignToExist() != null && getAssignToExist().equals("Yes")) {
				getCourseNameFromID(con, getCourseId(), uF);
			}
			
			} else if (uF.parseToInt(getTab()) > 0) {
			
			String chapterName = request.getParameter("chapterName");
			String chapterDescription = request.getParameter("chapterDescription");
			String subchapterName[] = request.getParameterValues("subchapterName");
			String subchapterDescription[] = request.getParameterValues("subchapterDescription");
			String descStatus[] = request.getParameterValues("descStatus");
			String subchapterCount[] = request.getParameterValues("subchapterCount"+chapterCnt);
			String hidesubchapterid[] = request.getParameterValues("hidesubchapterid"+chapterCnt);
			String hidechapterid = request.getParameter("hidechapterid");
//			System.out.println("hidechapterid ====> " + hidechapterid);
//			System.out.println("chapterCnt ====> " + chapterCnt);
			boolean chapterFlag = false;
			int newChapterId = 0;
			pst = con.prepareStatement("select course_chapter_id from course_chapter_details where ref_course_chapter_id = ?");
			pst.setInt(1, uF.parseToInt(hidechapterid));
			rst = pst.executeQuery();
			while (rst.next()) {
				chapterFlag = true;
				newChapterId = rst.getInt("course_chapter_id");
			}
			rst.close();
			pst.close();
			
			if(chapterFlag == true) {
				pst = con.prepareStatement("update course_chapter_details set course_chapter_name=?,chapter_description=?,course_id=?" +
						",updated_by=?,update_date=?,ref_course_chapter_id = 0 where ref_course_chapter_id = ?");
				pst.setString(1, chapterName);
				pst.setString(2, chapterDescription);
				pst.setInt(3, uF.parseToInt(getCourseId()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, uF.parseToInt(hidechapterid));
				pst.execute();
				pst.close();
//					newChapterId = uF.parseToInt(hidechapterid);
			} else {
			
				pst = con.prepareStatement("insert into course_chapter_details(course_chapter_name,chapter_description,course_id" +
						",added_by,entry_date)values(?,?,?, ?,?)");
				pst.setString(1, chapterName);
				pst.setString(2, chapterDescription);
				pst.setInt(3, uF.parseToInt(getCourseId()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("select max(course_chapter_id) as course_chapter_id from course_chapter_details");
				rst = pst.executeQuery();
				while (rst.next()) {
					newChapterId = rst.getInt("course_chapter_id");
				}
				rst.close();
				pst.close();
			}	
				setTextSubchapterId(request.getParameterValues("textSubchapterId"+chapterCnt));
				setImageSubchapterId(request.getParameterValues("imageSubchapterId"+chapterCnt));
				setVideoSubchapterId(request.getParameterValues("videoSubchapterId"+chapterCnt));
				setPdfSubchapterId(request.getParameterValues("pdfSubchapterId"+chapterCnt));
				setAttachSubchapterId(request.getParameterValues("attachSubchapterId"+chapterCnt));
				setPptSubchapterId(request.getParameterValues("pptSubchapterId"+chapterCnt));
				
				if(subchapterCount != null && !subchapterCount.equals("") && subchapterCount.length > 0) {
					for(int a=0; a<subchapterCount.length; a++) {
						boolean subchapterFlag = false;
						int newSubchapterId = 0;
						if(hidesubchapterid != null && hidesubchapterid.length > a){
							pst = con.prepareStatement("select course_subchapter_id from course_subchapter_details where ref_course_subchapter_id = ?");
							pst.setInt(1, uF.parseToInt(hidesubchapterid[a]));
							rst = pst.executeQuery();
							while (rst.next()) {
								subchapterFlag = true;
								newSubchapterId = rst.getInt("course_subchapter_id");
							}
							rst.close();
							pst.close();
						}
						if(subchapterFlag == true){
							pst = con.prepareStatement("update course_subchapter_details set course_subchapter_name=?,subchapter_description=?,course_id=?" +
								",course_chapter_id=?,updated_by=?,update_date=?,ref_course_subchapter_id=0 where course_subchapter_id = ?");
							pst.setString(1, subchapterName[a]);
							if(descStatus[a].equals("1")) {
								pst.setString(2, subchapterDescription[a]);
							} else {
								pst.setString(2, "");
							}
							pst.setInt(3, uF.parseToInt(getCourseId()));
							pst.setInt(4, newChapterId);
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(7, newSubchapterId);
							pst.execute();
							rst.close();
							pst.close();
							
							String hideimageid[] = request.getParameterValues("hideimageid"+chapterCnt);
							String hidetextid[] = request.getParameterValues("hideimageid"+chapterCnt);
							String hidevideoid[] = request.getParameterValues("hideimageid"+chapterCnt);
							String hidepdfid[] = request.getParameterValues("hideimageid"+chapterCnt);
							String hidedocsid[] = request.getParameterValues("hideimageid"+chapterCnt);
							String hidepptid[] = request.getParameterValues("hideimageid"+chapterCnt);
							
							for (int i = 0; getTextSubchapterId() != null && i < getTextSubchapterId().length; i++) {
								if(getTextSubchapterId()[i].equals(subchapterCount[a])) {
									boolean contentTextFlag = false;
									if(hidetextid != null && hidetextid.length > a){
										pst = con.prepareStatement("select course_content_id from course_content_details where ref_course_content_id = ?");
										pst.setInt(1, uF.parseToInt(hidetextid[i]));
										rst = pst.executeQuery();
										while (rst.next()) {
											contentTextFlag = true;
	//										newSubchapterId = rst.getInt("course_content_id");
										}
										rst.close();
										pst.close();
									}
									if(contentTextFlag == true) {
										pst = con.prepareStatement("update course_content_details set course_content_name=?,content_type=?,course_id=?" +
											",course_chapter_id=?,course_subchapter_id=?,updated_by=?,update_date=?,ref_course_content_id=0 where ref_course_content_id = ?");
										pst.setString(1, contentTextarea[i]);
										pst.setString(2, "TEXT");
										pst.setInt(3, uF.parseToInt(getCourseId()));
										pst.setInt(4, newChapterId);
										pst.setInt(5, newSubchapterId);
										pst.setInt(6, uF.parseToInt(strSessionEmpId));
										pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
										pst.setInt(8, uF.parseToInt(hidetextid[i]));
										pst.execute();
										pst.close();
									} else {
										pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
												"course_subchapter_id,added_by,entry_date)values(?,?,?,?, ?,?,?)");
										pst.setString(1, contentTextarea[i]);
										pst.setString(2, "TEXT");
										pst.setInt(3, uF.parseToInt(getCourseId()));
										pst.setInt(4, newChapterId);
										pst.setInt(5, newSubchapterId);
										pst.setInt(6, uF.parseToInt(strSessionEmpId));
										pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
										pst.execute();
										pst.close();
									}
								}
							}
							
							for (int i = 0; getImageSubchapterId() != null && i < getImageSubchapterId().length; i++) {
								if(getImageSubchapterId()[i].equals(subchapterCount[a])) {
									boolean contentImageFlag = false;
									int newImageId = 0;
									if(hideimageid != null && hideimageid.length > a){
										pst = con.prepareStatement("select course_content_id from course_content_details where ref_course_content_id = ?");
										pst.setInt(1, uF.parseToInt(hideimageid[i]));
										rst = pst.executeQuery();
										while (rst.next()) {
											contentImageFlag = true;
											newImageId = rst.getInt("course_content_id");
										}
										rst.close();
										pst.close();
									}
									if(contentImageFlag == true) {
										if(getContentImage()[i] != null) {
											pst = con.prepareStatement("update course_content_details set course_content_name=?,content_type=?,course_id=?" +
												",course_chapter_id=?,course_subchapter_id=?,updated_by=?,update_date=?,content_title=?,ref_course_content_id=0 where ref_course_content_id = ?");
											pst.setString(1, "");
											pst.setString(2, "IMAGE");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, imageTitle[i]);
											pst.setInt(9, uF.parseToInt(hideimageid[i]));
											pst.execute();
											pst.close();
										
										
											uploadImage(newImageId,getContentImage()[i],getContentImageFileName()[i]);
										}
										
									} else {
										if(getContentImage()[i] != null) {
											pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
													"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
											pst.setString(1, "");
											pst.setString(2, "IMAGE");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, imageTitle[i]);
											pst.execute();
											pst.close();
											
											pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
											rst = pst.executeQuery();
											while (rst.next()) {
												newImageId = rst.getInt("course_content_id");
											}
											rst.close();
											pst.close();
										
											uploadImage(newImageId,getContentImage()[i],getContentImageFileName()[i]);
										}
									}
								
								}
							}
							
							
							for (int i = 0; getVideoSubchapterId() != null && i < getVideoSubchapterId().length; i++) {
								if(getVideoSubchapterId()[i].equals(subchapterCount[a])) {
									boolean contentVideoFlag = false;
									int newVideoId = 0;
									if(hidevideoid != null && hidevideoid.length > a){
										pst = con.prepareStatement("select course_content_id from course_content_details where ref_course_content_id = ?");
										pst.setInt(1, uF.parseToInt(hidevideoid[i]));
										rst = pst.executeQuery();
										while (rst.next()) {
											contentVideoFlag = true;
											newVideoId = rst.getInt("course_content_id");
	//										newSubchapterId = rst.getInt("course_content_id");
										}
										rst.close();
										pst.close();
									}
									if(contentVideoFlag == true) {
										if(getContentVideo()[i] != null) {
											pst = con.prepareStatement("update course_content_details set course_content_name=?,content_type=?,course_id=?" +
												",course_chapter_id=?,course_subchapter_id=?,updated_by=?,update_date=?,content_title=?,content_url=?,ref_course_content_id=0 where ref_course_content_id = ?");
											pst.setString(1, "");
											pst.setString(2, "VIDEO");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, videoTitle[i]);
											pst.setString(9, videoUrl[i]);
											pst.setInt(10, uF.parseToInt(hidevideoid[i]));
											pst.execute();
											rst.close();
											pst.close();

										
//											uploadImage(newVideoId,getContentVideo()[i],getContentVideoFileName()[i]);
										}
									} else {
										if(getContentVideo()[i] != null) {
											pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
													"course_subchapter_id,added_by,entry_date,content_title,content_url)values(?,?,?,?, ?,?,?,?, ?)");
											pst.setString(1, "");
											pst.setString(2, "VIDEO");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, videoTitle[i]);
											pst.setString(9, videoUrl[i]);
											pst.execute();
											pst.close();
											
											pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
											rst = pst.executeQuery();
											while (rst.next()) {
												newVideoId = rst.getInt("course_content_id");
											}
											rst.close();
											pst.close();
										
										
//											uploadImage(newVideoId,getContentVideo()[i],getContentVideoFileName()[i]);
										}
									}
								
								}
							}
							
							for (int i = 0; getPdfSubchapterId() != null && i < getPdfSubchapterId().length; i++) {
								if(getPdfSubchapterId()[i].equals(subchapterCount[a])) {
									boolean contentPdfFlag = false;
									int newPDFId = 0;
									if(hidepdfid != null && hidepdfid.length > a){
										pst = con.prepareStatement("select course_content_id from course_content_details where ref_course_content_id = ?");
										pst.setInt(1, uF.parseToInt(hidepdfid[i]));
										rst = pst.executeQuery();
										while (rst.next()) {
											contentPdfFlag = true;
											newPDFId = rst.getInt("course_content_id");
	//										newSubchapterId = rst.getInt("course_content_id");
										}
										rst.close();
										pst.close();
									}
									if(contentPdfFlag == true) {
										System.out.println("ANC/1344---getContentPdf()=="+getContentPdf());
										if(getContentPdf() != null && getContentPdf()[i] != null) {
											pst = con.prepareStatement("update course_content_details set course_content_name=?,content_type=?,course_id=?" +
												",course_chapter_id=?,course_subchapter_id=?,updated_by=?,update_date=?,content_title=?,ref_course_content_id=0 where ref_course_content_id = ?");
											pst.setString(1, "");
											pst.setString(2, "PDF");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, pdfTitle[i]);
											pst.setInt(9, uF.parseToInt(hidepdfid[i]));
											pst.execute();
											pst.close();
										
										
											uploadImage(newPDFId,getContentPdf()[i],getContentPdfFileName()[i]);
										}
									} else {
										System.out.println("ANC/1364---getContentPdf()=="+getContentPdf());
										if(getContentPdf() != null && getContentPdf()[i] != null) {
											pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
													"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
											pst.setString(1, "");
											pst.setString(2, "PDF");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, pdfTitle[i]);
											pst.execute();
											pst.close();
											
											pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
											rst = pst.executeQuery();
											while (rst.next()) {
												newPDFId = rst.getInt("course_content_id");
											}
											rst.close();
											pst.close();
										
										
											uploadImage(newPDFId,getContentPdf()[i],getContentPdfFileName()[i]);
										}
									}
									
								}
							}
							
							
							for (int i = 0; getPptSubchapterId() != null && i < getPptSubchapterId().length; i++) {
								if(getPptSubchapterId()[i].equals(subchapterCount[a])) {
									boolean contentPPTFlag = false;
									int newPPTId = 0;
									if(hidepptid != null && hidepptid.length > a){
										pst = con.prepareStatement("select course_content_id from course_content_details where ref_course_content_id = ?");
										pst.setInt(1, uF.parseToInt(hidepptid[i]));
										rst = pst.executeQuery();
										while (rst.next()) {
											contentPPTFlag = true;
											newPPTId = rst.getInt("course_content_id");
	//										newSubchapterId = rst.getInt("course_content_id");
										}
										rst.close();
										pst.close();
									}
									if(contentPPTFlag == true) {
										if(getContentPPT() != null && getContentPPT()[i] != null) {
											pst = con.prepareStatement("update course_content_details set course_content_name=?,content_type=?,course_id=?" +
												",course_chapter_id=?,course_subchapter_id=?,updated_by=?,update_date=?,content_title=?,ref_course_content_id=0 where ref_course_content_id = ?");
											pst.setString(1, "");
											pst.setString(2, "PPT");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, pptTitle[i]);
											pst.setInt(9, uF.parseToInt(hidepptid[i]));
											pst.execute();
											pst.close();
											
											uploadImage(newPPTId,getContentPPT()[i],getContentPPTFileName()[i]);
										}
									} else {
										System.out.println("ANC/1364---getContentPPT()=="+getContentPPT());
										if(getContentPPT() != null && getContentPPT()[i] != null) {
											pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
													"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
											pst.setString(1, "");
											pst.setString(2, "PPT");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, pptTitle[i]);
											pst.execute();
											pst.close();
											
											pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
											rst = pst.executeQuery();
											while (rst.next()) {
												newPPTId = rst.getInt("course_content_id");
											}
											rst.close();
											pst.close();
										
										
											uploadImage(newPPTId,getContentPPT()[i],getContentPPTFileName()[i]);
										}
									}
									
								}
							}
							
							for (int i = 0; getAttachSubchapterId() != null && i < getAttachSubchapterId().length; i++) {
								if(getAttachSubchapterId()[i].equals(subchapterCount[a])) {
									boolean contentAttechFlag = false;
									int newAttachId = uF.parseToInt(hidedocsid[i]);
									if(hidedocsid != null && hidedocsid.length > a){
										pst = con.prepareStatement("select course_content_id from course_content_details where ref_course_content_id = ?");
										pst.setInt(1, uF.parseToInt(hidedocsid[i]));
										rst = pst.executeQuery();
										while (rst.next()) {
											contentAttechFlag = true;
											newAttachId = rst.getInt("course_content_id");
										}
										rst.close();
										pst.close();
									}
									if(contentAttechFlag == true) {
										if(getContentAttach()[i] != null) {
											pst = con.prepareStatement("update course_content_details set course_content_name=?,content_type=?,course_id=?" +
												",course_chapter_id=?,course_subchapter_id=?,updated_by=?,update_date=?,content_title=?,ref_course_content_id=0 where ref_course_content_id = ?");
											pst.setString(1, "");
											pst.setString(2, "ATTACH");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, attachTitle[i]);
											pst.setInt(9, uF.parseToInt(hidedocsid[i]));
											pst.execute();
											pst.close();
										
											uploadImage(newAttachId,getContentAttach()[i],getContentAttachFileName()[i]);
										}
									} else {
										if(getContentAttach()[i] != null) {
										pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
													"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
											pst.setString(1, "");
											pst.setString(2, "ATTACH");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, attachTitle[i]);
											pst.execute();
											pst.close();
											
											pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
											rst = pst.executeQuery();
											while (rst.next()) {
												newAttachId = rst.getInt("course_content_id");
											}
											rst.close();
											pst.close();
										
										
											uploadImage(newAttachId,getContentAttach()[i],getContentAttachFileName()[i]);
										}
									}
									
								}
							}
							 
							String weightage[] = request.getParameterValues("weightage"+chapterCnt+"_"+subchapterCount[a]);
							String question[] = request.getParameterValues("question"+chapterCnt+"_"+subchapterCount[a]);
							String hideassessmentid[] = request.getParameterValues("hideassessmentid"+chapterCnt+"_"+subchapterCount[a]);
							
							String addFlag[] = request.getParameterValues("status"+chapterCnt+"_"+subchapterCount[a]);
							String optiona[] = request.getParameterValues("optiona"+chapterCnt+"_"+subchapterCount[a]);
							String optionb[] = request.getParameterValues("optionb"+chapterCnt+"_"+subchapterCount[a]);
							String optionc[] = request.getParameterValues("optionc"+chapterCnt+"_"+subchapterCount[a]);
							String optiond[] = request.getParameterValues("optiond"+chapterCnt+"_"+subchapterCount[a]);
							String ansType[] = request.getParameterValues("ansType"+chapterCnt+"_"+subchapterCount[a]);
							String orientt[] = request.getParameterValues("orientt"+chapterCnt+"_"+subchapterCount[a]);
//							System.out.println("hideassessmentid ====> " + hideassessmentid.length);
							for (int i = 0; question != null && i < question.length; i++) {
								String[] correct = request.getParameterValues("correct"+chapterCnt+"_"+subchapterCount[a]+"_"+ orientt[i]);
								StringBuilder option = new StringBuilder();
	 
								for (int ab = 0; correct != null && ab < correct.length; ab++) {
									option.append(correct[ab] + ",");
								}
//								System.out.println("hideassessmentid[i] ====> " + hideassessmentid[i]);
								int question_id = 0;
								pst = con.prepareStatement("insert into course_question_bank(course_question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,answer_type)values(?,?,?,?,?,?,?,?)");
								pst.setString(1, question[i]);
								pst.setString(2, (optiona != null && optiona.length > i ? optiona[i]: ""));
								pst.setString(3, (optionb != null && optionb.length > i ? optionb[i]: ""));
								pst.setString(4, (optionc != null && optionc.length > i ? optionc[i]: ""));
								pst.setString(5, (optiond != null && optiond.length > i ? optiond[i]: ""));
								pst.setString(6, option.toString());
								pst.setBoolean(7, uF.parseToBoolean(addFlag[i]));
								pst.setInt(8, uF.parseToInt(ansType[i]));
//								System.out.println("pst==>"+pst);
								pst.execute();
								pst.close();

								pst = con.prepareStatement("select max(course_question_bank_id) from course_question_bank");
								rst = pst.executeQuery();
								while (rst.next()) {
									question_id = rst.getInt(1);
								}
								rst.close();
								pst.close();
								
								boolean assessFlag = false;
								if(hideassessmentid != null && hideassessmentid.length > i){
									pst = con.prepareStatement("select course_assessment_id from course_assessment_details where ref_course_assessment_id = ?");
									pst.setInt(1, uF.parseToInt(hideassessmentid[i]));
									rst = pst.executeQuery();
									while (rst.next()) {
										assessFlag = true;
									}
									rst.close();
									pst.close();
								}
//								System.out.println("assessFlag ===> " + assessFlag);
								if(assessFlag == true) {
									pst = con.prepareStatement("update course_assessment_details set assessment_id=?,weightage=?,course_id=?,course_chapter_id=?," +
											"course_subchapter_id=?,updated_by=?,update_date=?,ref_course_assessment_id=0 where ref_course_assessment_id = ?");
									pst.setInt(1, question_id);
									pst.setDouble(2, uF.parseToDouble(weightage[i]));
									pst.setInt(3, uF.parseToInt(getCourseId()));
									pst.setInt(4, newChapterId);
									pst.setInt(5, newSubchapterId);
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(8, uF.parseToInt(hideassessmentid[i]));
									pst.execute();
									pst.close();
								} else {
									pst = con.prepareStatement("insert into course_assessment_details(assessment_id,weightage,course_id,course_chapter_id," +
											"course_subchapter_id,added_by,entry_date)values(?,?,?,?, ?,?,?)");
									pst.setInt(1, question_id);
									pst.setDouble(2, uF.parseToDouble(weightage[i]));
									pst.setInt(3, uF.parseToInt(getCourseId()));
									pst.setInt(4, newChapterId);
									pst.setInt(5, newSubchapterId);
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.execute();
									pst.close();
								}
							}
							
							
							
						} else {
							pst = con.prepareStatement("insert into course_subchapter_details(course_subchapter_name,subchapter_description,course_id" +
								",course_chapter_id,added_by,entry_date)values(?,?,?, ?,?,?)");
							pst.setString(1, subchapterName[a]);
							if(descStatus[a].equals("1")) {
								pst.setString(2, subchapterDescription[a]);
							} else {
								pst.setString(2, "");
							}
							pst.setInt(3, uF.parseToInt(getCourseId()));
							pst.setInt(4, newChapterId);
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.execute();
							
							pst = con.prepareStatement("select max(course_subchapter_id) as course_subchapter_id from course_subchapter_details");
							rst = pst.executeQuery();
							while (rst.next()) {
								newSubchapterId = rst.getInt("course_subchapter_id");
							}
							rst.close();
							pst.close();
						
						for (int i = 0; getTextSubchapterId() != null && i < getTextSubchapterId().length; i++) {
							if(getTextSubchapterId()[i].equals(subchapterCount[a])) {
								pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
										"course_subchapter_id,added_by,entry_date)values(?,?,?,?, ?,?,?)");
								pst.setString(1, contentTextarea[i]);
								pst.setString(2, "TEXT");
								pst.setInt(3, uF.parseToInt(getCourseId()));
								pst.setInt(4, newChapterId);
								pst.setInt(5, newSubchapterId);
								pst.setInt(6, uF.parseToInt(strSessionEmpId));
								pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.execute();
								pst.close();
							}
						}
						
						for (int i = 0; getImageSubchapterId() != null && i < getImageSubchapterId().length; i++) {
							if(getImageSubchapterId()[i].equals(subchapterCount[a])) {
								if(getContentImage()[i] != null) {
									pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
											"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
									pst.setString(1, "");
									pst.setString(2, "IMAGE");
									pst.setInt(3, uF.parseToInt(getCourseId()));
									pst.setInt(4, newChapterId);
									pst.setInt(5, newSubchapterId);
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setString(8, imageTitle[i]);
									pst.execute();
									pst.close();
									
									int newImageId = 0;
									pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
									rst = pst.executeQuery();
									while (rst.next()) {
										newImageId = rst.getInt("course_content_id");
									}
									rst.close();
									pst.close();
								
									uploadImage(newImageId,getContentImage()[i],getContentImageFileName()[i]);
								}
							}
						}
						
						
						for (int i = 0; getVideoSubchapterId() != null && i < getVideoSubchapterId().length; i++) {
							if(getVideoSubchapterId()[i].equals(subchapterCount[a])) {
								if(getContentVideo()[i] != null) {
									pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
											"course_subchapter_id,added_by,entry_date,content_title,content_url)values(?,?,?,?, ?,?,?,?, ?)");
									pst.setString(1, "");
									pst.setString(2, "VIDEO");
									pst.setInt(3, uF.parseToInt(getCourseId()));
									pst.setInt(4, newChapterId);
									pst.setInt(5, newSubchapterId);
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setString(8, videoTitle[i]);
									pst.setString(9, videoUrl[i]);
									pst.execute();
									pst.close();
									
									int newVideoId = 0;
									pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
									rst = pst.executeQuery();
									while (rst.next()) {
										newVideoId = rst.getInt("course_content_id");
									}
									rst.close();
									pst.close();
								
								
//									uploadImage(newVideoId,getContentVideo()[i],getContentVideoFileName()[i]);
								}
							}
						}
						
						for (int i = 0; getPdfSubchapterId() != null && i < getPdfSubchapterId().length; i++) {
							if(getPdfSubchapterId()[i].equals(subchapterCount[a])) {
								System.out.println("ANC/1645---getContentPdf()==="+getContentPdf());
								if(getContentPdf() != null && getContentPdf()[i] != null) {
									pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
												"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
									pst.setString(1, "");
									pst.setString(2, "PDF");
									pst.setInt(3, uF.parseToInt(getCourseId()));
									pst.setInt(4, newChapterId);
									pst.setInt(5, newSubchapterId);
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setString(8, pdfTitle[i]);
									pst.execute();
									pst.close();
									
									int newPDFId = 0;
									pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
									rst = pst.executeQuery();
									while (rst.next()) {
										newPDFId = rst.getInt("course_content_id");
									}
									rst.close();
									pst.close();
								
								
									uploadImage(newPDFId,getContentPdf()[i],getContentPdfFileName()[i]);
								}
							}
						}
				//===start parvez date: 12-01-2023===		
						for (int i = 0; getPptSubchapterId() != null && i < getPptSubchapterId().length; i++) {
							if(getPptSubchapterId()[i].equals(subchapterCount[a])) {
								if(getContentPPT() != null && getContentPPT()[i] != null) {
									pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
												"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
									pst.setString(1, "");
									pst.setString(2, "PPT");
									pst.setInt(3, uF.parseToInt(getCourseId()));
									pst.setInt(4, newChapterId);
									pst.setInt(5, newSubchapterId);
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setString(8, pptTitle[i]);
									pst.execute();
									pst.close();
									
									int newPPTId = 0;
									pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
									rst = pst.executeQuery();
									while (rst.next()) {
										newPPTId = rst.getInt("course_content_id");
									}
									rst.close();
									pst.close();
								
								
									uploadImage(newPPTId,getContentPPT()[i],getContentPPTFileName()[i]);
								}
							}
						}
				//===end parvez date: 13-01-2023===		
						
						for (int i = 0; getAttachSubchapterId() != null && i < getAttachSubchapterId().length; i++) {
							if(getAttachSubchapterId()[i].equals(subchapterCount[a])) {
								if(getContentAttach()[i] != null) {
									pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
											"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
									pst.setString(1, "");
									pst.setString(2, "ATTACH");
									pst.setInt(3, uF.parseToInt(getCourseId()));
									pst.setInt(4, newChapterId);
									pst.setInt(5, newSubchapterId);
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setString(8, attachTitle[i]);
									pst.execute();
									pst.close();
									
									int newAttachId = 0;
									pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
									rst = pst.executeQuery();
									while (rst.next()) {
										newAttachId = rst.getInt("course_content_id");
									}
									rst.close();
									pst.close();
								
								
									uploadImage(newAttachId,getContentAttach()[i],getContentAttachFileName()[i]);
								}
							}
						}
						 
						String weightage[] = request.getParameterValues("weightage"+chapterCnt+"_"+subchapterCount[a]);
						String question[] = request.getParameterValues("question"+chapterCnt+"_"+subchapterCount[a]);

						String addFlag[] = request.getParameterValues("status"+chapterCnt+"_"+subchapterCount[a]);
						String optiona[] = request.getParameterValues("optiona"+chapterCnt+"_"+subchapterCount[a]);
						String optionb[] = request.getParameterValues("optionb"+chapterCnt+"_"+subchapterCount[a]);
						String optionc[] = request.getParameterValues("optionc"+chapterCnt+"_"+subchapterCount[a]);
						String optiond[] = request.getParameterValues("optiond"+chapterCnt+"_"+subchapterCount[a]);
						String ansType[] = request.getParameterValues("ansType"+chapterCnt+"_"+subchapterCount[a]);
						String orientt[] = request.getParameterValues("orientt"+chapterCnt+"_"+subchapterCount[a]);
						
						for (int i = 0; question != null && i < question.length; i++) {
							String[] correct = request.getParameterValues("correct"+chapterCnt+"_"+subchapterCount[a]+"_"+ orientt[i]);
							StringBuilder option = new StringBuilder();
 
							for (int ab = 0; correct != null && ab < correct.length; ab++) {
								option.append(correct[ab] + ",");
							}
							int question_id = 0;
							pst = con.prepareStatement("insert into course_question_bank(course_question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,answer_type)values(?,?,?,?,?,?,?,?)");
							pst.setString(1, question[i]);
							pst.setString(2, (optiona != null && optiona.length > i ? optiona[i]: ""));
							pst.setString(3, (optionb != null && optionb.length > i ? optionb[i]: ""));
							pst.setString(4, (optionc != null && optionc.length > i ? optionc[i]: ""));
							pst.setString(5, (optiond != null && optiond.length > i ? optiond[i]: ""));
							pst.setString(6, option.toString());
							pst.setBoolean(7, uF.parseToBoolean(addFlag[i]));
							pst.setInt(8, uF.parseToInt(ansType[i]));
							pst.execute();
							pst.close();

							pst = con.prepareStatement("select max(course_question_bank_id) from course_question_bank");
							rst = pst.executeQuery();
							while (rst.next()) {
								question_id = rst.getInt(1);
							}
							rst.close();
							pst.close();
							
							pst = con.prepareStatement("insert into course_assessment_details(assessment_id,weightage,course_id,course_chapter_id," +
									"course_subchapter_id,added_by,entry_date)values(?,?,?,?, ?,?,?)");
							pst.setInt(1, question_id);
							pst.setDouble(2, uF.parseToDouble(weightage[i]));
							pst.setInt(3, uF.parseToInt(getCourseId()));
							pst.setInt(4, newChapterId);
							pst.setInt(5, newSubchapterId);
							pst.setInt(6, uF.parseToInt(strSessionEmpId));
							pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.execute();
							pst.close();
						}
					}
				}
				}	
				
				if(getAssignToExist() != null && getAssignToExist().equals("Yes")) {
					getCourseNameFromID(con, getCourseId(), uF);
				}
				
			setChapterCnt(""+(uF.parseToInt(getChapterCnt())+1));
				
			}
			
//			if (getStepSave() != null && getStepSave().equals("Save & Exit")) {
//				int newCourseId = 0;
//				pst = con.prepareStatement("select course_id from course_details where ref_course_id = ?");
//				pst.setInt(1,  uF.parseToInt(courseID));
//				rst = pst.executeQuery();
//				while (rst.next()) {
//					newCourseId = rst.getInt(1);
//				}
				
				pst = con.prepareStatement("update course_details set ref_course_id = 0 where course_id = ?");
				pst.setInt(1, uF.parseToInt(getCourseId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("update course_chapter_details set ref_course_chapter_id = 0 where course_id = ?");
				pst.setInt(1, uF.parseToInt(getCourseId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("update course_subchapter_details set ref_course_subchapter_id = 0 where course_id = ?");
				pst.setInt(1, uF.parseToInt(getCourseId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("update course_content_details set ref_course_content_id = 0 where course_id = ?");
				pst.setInt(1, uF.parseToInt(getCourseId()));
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("update course_assessment_details set ref_course_assessment_id = 0 where course_id = ?");
				pst.setInt(1, uF.parseToInt(getCourseId()));
				pst.executeUpdate();
				pst.close();
//			}
 
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
			if(getCourseAuthor() == null || getCourseAuthor().equals("")){
				setCourseAuthor(hmEmpName.get(strSessionEmpId));
			}
			if(getCourseVersion() == null || getCourseVersion().equals("")){
				setCourseVersion("1.0");
			}
		if (tab != null && uF.parseToInt(tab) == 0) {

			setCourseId(insertStep1Data(con, uF));
//			request.setAttribute("CourseID", getCourseId());

		} else if (tab != null && uF.parseToInt(tab) > 1) {

			insertChapterData(con, uF);
//			request.setAttribute("CourseID", getCourseId());
		} 
		String crsName = getCourseNameById(con, uF, getCourseId());
		session.setAttribute(MESSAGE, SUCCESSM+""+crsName+" course has been created successfully."+END);
//		session.setAttribute(MESSAGE, SUCCESSM+"You have added course successfully."+END);
		
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}

	}

	
	private void uploadImage(int contentID, File contentFile, String contentFileName) {

		try {
			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("CONTENT_IMAGE");
			uI.setEmpImage(contentFile);
			uI.setEmpImageFileName(contentFileName);
			uI.setContentID(contentID + "");
			uI.setCF(CF);
//			System.out.println("empId2 ===> "+empId2+" getEmpImage() ===> "+getEmpImage());
			uI.upoadImage();
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private void insertChapterData(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			String chapterName = request.getParameter("chapterName");
			String chapterDescription = request.getParameter("chapterDescription");
			String subchapterName[] = request.getParameterValues("subchapterName");
			String subchapterDescription[] = request.getParameterValues("subchapterDescription");
			String descStatus[] = request.getParameterValues("descStatus");
			String subchapterCount[] = request.getParameterValues("subchapterCount"+chapterCnt);
			String hidesubchapterid[] = request.getParameterValues("hidesubchapterid"+chapterCnt);
			String hidechapterid = request.getParameter("hidechapterid");
//			System.out.println("hidechapterid ====> " + hidechapterid);
//			System.out.println("chapterCnt ====> " + chapterCnt);
			boolean chapterFlag = false;
			int newChapterId = 0;
			pst = con.prepareStatement("select course_chapter_id from course_chapter_details where course_chapter_id = ?");
			pst.setInt(1, uF.parseToInt(hidechapterid));
			rst = pst.executeQuery();
			while (rst.next()) {
				chapterFlag = true;
//				newChapterId = rst.getInt("course_chapter_id");
			}
			rst.close();
			pst.close();
			if(chapterFlag) {
				pst = con.prepareStatement("update course_chapter_details set course_chapter_name=?,chapter_description=?,course_id=?" +
						",updated_by=?,update_date=? where course_chapter_id = ?");
				pst.setString(1, chapterName);
				pst.setString(2, chapterDescription);
				pst.setInt(3, uF.parseToInt(getCourseId()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, uF.parseToInt(hidechapterid));
				pst.execute();
				pst.close();
				
					newChapterId = uF.parseToInt(hidechapterid);
				
			} else {
			
				pst = con.prepareStatement("insert into course_chapter_details(course_chapter_name,chapter_description,course_id" +
						",added_by,entry_date)values(?,?,?, ?,?)");
				pst.setString(1, chapterName);
				pst.setString(2, chapterDescription);
				pst.setInt(3, uF.parseToInt(getCourseId()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("select max(course_chapter_id) as course_chapter_id from course_chapter_details");
				rst = pst.executeQuery();
				while (rst.next()) {
					newChapterId = rst.getInt("course_chapter_id");
				}
				rst.close();
				pst.close();
			}	
				setTextSubchapterId(request.getParameterValues("textSubchapterId"+chapterCnt));
				setImageSubchapterId(request.getParameterValues("imageSubchapterId"+chapterCnt));
				setVideoSubchapterId(request.getParameterValues("videoSubchapterId"+chapterCnt));
				setPdfSubchapterId(request.getParameterValues("pdfSubchapterId"+chapterCnt));
				setAttachSubchapterId(request.getParameterValues("attachSubchapterId"+chapterCnt));
				setPptSubchapterId(request.getParameterValues("pptSubchapterId"+chapterCnt));
				
//				System.out.println("getTextSubchapterId().length =====> " + getTextSubchapterId() != null ? getTextSubchapterId().length : "");
//				System.out.println("getImageSubchapterId().length =====> " + getImageSubchapterId() != null ? getImageSubchapterId().length : "");
//				System.out.println("getVideoSubchapterId().length =====> " + getVideoSubchapterId() != null ? getVideoSubchapterId().length : "");
//				System.out.println("getPdfSubchapterId().length =====> " + getPdfSubchapterId() != null ? getPdfSubchapterId().length : "");
				
//				System.out.println("subchapterCount.length =====> " + subchapterCount != null ? subchapterCount.length : "");
				
				if(subchapterCount != null && !subchapterCount.equals("") && subchapterCount.length > 0) {
					for(int a=0; a<subchapterCount.length; a++) {
						boolean subchapterFlag = false;
						int newSubchapterId = 0;
						if(hidesubchapterid != null && hidesubchapterid.length > a){
							pst = con.prepareStatement("select course_subchapter_id from course_subchapter_details where course_subchapter_id = ?");
							pst.setInt(1, uF.parseToInt(hidesubchapterid[a]));
							rst = pst.executeQuery();
//							System.out.println("pst hidesubchapterid ====> " + pst);
							while (rst.next()) {
								subchapterFlag = true;
								newSubchapterId = rst.getInt("course_subchapter_id");
							}
							rst.close();
							pst.close();
						}
						if(subchapterFlag == true) {
							pst = con.prepareStatement("update course_subchapter_details set course_subchapter_name=?,subchapter_description=?,course_id=?" +
								",course_chapter_id=?,updated_by=?,update_date=? where course_subchapter_id = ?");
							pst.setString(1, subchapterName[a]);
							if(descStatus[a].equals("1")) {
								pst.setString(2, subchapterDescription[a]);
							} else {
								pst.setString(2, "");
							}
							pst.setInt(3, uF.parseToInt(getCourseId()));
							pst.setInt(4, newChapterId);
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(7, newSubchapterId);
//							System.out.println("pst course_subchapter_details ===> " + pst);
							
							pst.execute();
							pst.close();
							
							String hideimageid[] = request.getParameterValues("hideimageid"+chapterCnt);
							String hidetextid[] = request.getParameterValues("hidetextid"+chapterCnt);
							String hidevideoid[] = request.getParameterValues("hidevideoid"+chapterCnt);
							String hidepdfid[] = request.getParameterValues("hidepdfid"+chapterCnt);
							String hidedocsid[] = request.getParameterValues("hidedocsid"+chapterCnt);
							String hidepptid[] = request.getParameterValues("hidepptid"+chapterCnt);
							
							for (int i = 0; getTextSubchapterId() != null && i < getTextSubchapterId().length; i++) {
								if(getTextSubchapterId()[i].equals(subchapterCount[a])) {
									boolean contentTextFlag = false;
									if(hidetextid != null && hidetextid.length > i){
										pst = con.prepareStatement("select course_content_id from course_content_details where course_content_id = ?");
										pst.setInt(1, uF.parseToInt(hidetextid[i]));
//										System.out.println("pst text select ========> " + pst);
										rst = pst.executeQuery();
										while (rst.next()) {
											contentTextFlag = true;
	//										newSubchapterId = rst.getInt("course_content_id");
										}
										rst.close();
										pst.close();
									}
									if(contentTextFlag == true) {
										pst = con.prepareStatement("update course_content_details set course_content_name=?,content_type=?,course_id=?" +
											",course_chapter_id=?,course_subchapter_id=?,updated_by=?,update_date=? where course_content_id = ?");
										pst.setString(1, contentTextarea[i]);
										pst.setString(2, "TEXT");
										pst.setInt(3, uF.parseToInt(getCourseId()));
										pst.setInt(4, newChapterId);
										pst.setInt(5, newSubchapterId);
										pst.setInt(6, uF.parseToInt(strSessionEmpId));
										pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
										pst.setInt(8, uF.parseToInt(hidetextid[i]));
//										System.out.println("pst text course_content_details ===> " + pst);
										pst.execute();
										pst.close();
									} else {
										pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
												"course_subchapter_id,added_by,entry_date)values(?,?,?,?, ?,?,?)");
										pst.setString(1, contentTextarea[i]);
										pst.setString(2, "TEXT");
										pst.setInt(3, uF.parseToInt(getCourseId()));
										pst.setInt(4, newChapterId);
										pst.setInt(5, newSubchapterId);
										pst.setInt(6, uF.parseToInt(strSessionEmpId));
										pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
//										System.out.println("pst text course_content_details ===> " + pst);
										pst.execute();
										pst.close();
									}
								}
							}
							
//							System.out.println("getImageSubchapterId length ----------> " + getImageSubchapterId().length);
							for (int i = 0; getImageSubchapterId() != null && i < getImageSubchapterId().length; i++) {
//								System.out.println("getImageSubchapterId [i] ----------> " + getImageSubchapterId()[i]+"  subchapterCount[a] ----------> " + subchapterCount[a]);
								if(getImageSubchapterId()[i].equals(subchapterCount[a])) {
//									System.out.println("in if getImageSubchapterId [i] ----------> " + getImageSubchapterId()[i]+"  subchapterCount[a] ----------> " + subchapterCount[a]);
									boolean contentImageFlag = false;
									if(hideimageid != null && hideimageid.length > i){
										pst = con.prepareStatement("select course_content_id from course_content_details where course_content_id = ?");
										pst.setInt(1, uF.parseToInt(hideimageid[i]));
//										System.out.println("pst image select ========> " + pst);
										rst = pst.executeQuery();
										while (rst.next()) {
											contentImageFlag = true;
	//										newSubchapterId = rst.getInt("course_content_id");
										}
										rst.close();
										pst.close();
									}
//									System.out.println("contentImageFlag ----------> " + contentImageFlag);
									
									if(contentImageFlag == true) {
										if(getContentImage()[i] != null) {
											pst = con.prepareStatement("update course_content_details set course_content_name=?,content_type=?,course_id=?" +
												",course_chapter_id=?,course_subchapter_id=?,updated_by=?,update_date=?,content_title=? where course_content_id = ?");
											pst.setString(1, "");
											pst.setString(2, "IMAGE");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, imageTitle[i]);
											pst.setInt(9, uF.parseToInt(hideimageid[i]));
//											System.out.println("pst image course_content_details ===> " + pst);
											pst.execute();
											pst.close();
											
											int newImageId = uF.parseToInt(hideimageid[i]);
										
										
											uploadImage(newImageId,getContentImage()[i],getContentImageFileName()[i]);
										}
										
									} else {
										if(getContentImage()[i] != null) {
											pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
													"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
											pst.setString(1, "");
											pst.setString(2, "IMAGE");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, imageTitle[i]);
//											System.out.println("pst image course_content_details ===> " + pst);
											pst.execute();
											pst.close();
											
											int newImageId = 0;
											pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
											rst = pst.executeQuery();
											while (rst.next()) {
												newImageId = rst.getInt("course_content_id");
											}
											rst.close();
											pst.close();
										
										
											uploadImage(newImageId,getContentImage()[i],getContentImageFileName()[i]);
										}
									}
								
								}
							}
							
							
							for (int i = 0; getVideoSubchapterId() != null && i < getVideoSubchapterId().length; i++) {
								if(getVideoSubchapterId()[i].equals(subchapterCount[a])) {
									boolean contentVideoFlag = false;
									if(hidevideoid != null && hidevideoid.length > i){
										pst = con.prepareStatement("select course_content_id from course_content_details where course_content_id = ?");
										pst.setInt(1, uF.parseToInt(hidevideoid[i]));
//										System.out.println("pst video select ========> " + pst);
										rst = pst.executeQuery();
										while (rst.next()) {
											contentVideoFlag = true;
	//										newSubchapterId = rst.getInt("course_content_id");
										}
										rst.close();
										pst.close();
									}
									if(contentVideoFlag == true) {
										if(getContentVideo()[i] != null) {
											pst = con.prepareStatement("update course_content_details set course_content_name=?,content_type=?,course_id=?" +
												",course_chapter_id=?,course_subchapter_id=?,updated_by=?,update_date=?,content_title=?,content_url=? where course_content_id = ?");
											pst.setString(1, "");
											pst.setString(2, "VIDEO");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, videoTitle[i]);
											pst.setString(9, videoUrl[i]);
											pst.setInt(10, uF.parseToInt(hidevideoid[i]));
//											System.out.println("pst video course_content_details ===> " + pst);
											pst.execute();
											rst.close();
											pst.close();
											
											int newVideoId = uF.parseToInt(hidevideoid[i]);
										
										
//											uploadImage(newVideoId,getContentVideo()[i],getContentVideoFileName()[i]);
										}
									} else {
										if(getContentVideo()[i] != null) {
											pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
													"course_subchapter_id,added_by,entry_date,content_title,content_url)values(?,?,?,?, ?,?,?,?, ?)");
											pst.setString(1, "");
											pst.setString(2, "VIDEO");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, videoTitle[i]);
											pst.setString(9, videoUrl[i]);
//											System.out.println("pst video course_content_details ===> " + pst);
											pst.execute();
											pst.close();
											
											int newVideoId = 0;
											pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
											rst = pst.executeQuery();
											while (rst.next()) {
												newVideoId = rst.getInt("course_content_id");
											}
											rst.close();
											pst.close();
										
										
//											uploadImage(newVideoId,getContentVideo()[i],getContentVideoFileName()[i]);
										}
									}
								
								}
							}
							
							for (int i = 0; getPdfSubchapterId() != null && i < getPdfSubchapterId().length; i++) {
								if(getPdfSubchapterId()[i].equals(subchapterCount[a])) {
									boolean contentPdfFlag = false;
									if(hidepdfid != null && hidepdfid.length > i){
										pst = con.prepareStatement("select course_content_id from course_content_details where course_content_id = ?");
										pst.setInt(1, uF.parseToInt(hidepdfid[i]));
//										System.out.println("pst pdf select ========> " + pst);
										rst = pst.executeQuery();
										while (rst.next()) {
											contentPdfFlag = true;
	//										newSubchapterId = rst.getInt("course_content_id");
										}
										rst.close();
										pst.close();
									}
									if(contentPdfFlag == true) {
										System.out.println("ANC/2211---getContentPdf()=="+getContentPdf());
										if(getContentPdf() != null && getContentPdf()[i] != null) {
											pst = con.prepareStatement("update course_content_details set course_content_name=?,content_type=?,course_id=?" +
												",course_chapter_id=?,course_subchapter_id=?,updated_by=?,update_date=?,content_title=? where course_content_id = ?");
											pst.setString(1, "");
											pst.setString(2, "PDF");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, pdfTitle[i]);
											pst.setInt(9, uF.parseToInt(hidepdfid[i]));
//											System.out.println("pst pdf course_content_details ===> " + pst);
											pst.execute();
											rst.close();
											pst.close();
											
											int newPDFId = uF.parseToInt(hidepdfid[i]);
										
										
											uploadImage(newPDFId,getContentPdf()[i],getContentPdfFileName()[i]);
										}
									} else {
										System.out.println("ANC/2236---getContentPdf()=="+getContentPdf());
										if(getContentPdf() != null && getContentPdf()[i] != null) {
											pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
													"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
											pst.setString(1, "");
											pst.setString(2, "PDF");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, pdfTitle[i]);
//											System.out.println("pst pdf course_content_details ===> " + pst);
											pst.execute();
											rst.close();
											pst.close();
											
											int newPDFId = 0;
											pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
											rst = pst.executeQuery();
											while (rst.next()) {
												newPDFId = rst.getInt("course_content_id");
											}
											rst.close();
											pst.close();
										
										
											uploadImage(newPDFId,getContentPdf()[i],getContentPdfFileName()[i]);
										}
									}
									
								}
							}
							
						//===start parvez date: 13-01-2023===	
							for (int i = 0; getPptSubchapterId() != null && i < getPptSubchapterId().length; i++) {
								if(getPptSubchapterId()[i].equals(subchapterCount[a])) {
									boolean contentPPTFlag = false;
									if(hidepptid != null && hidepptid.length > i){
										pst = con.prepareStatement("select course_content_id from course_content_details where course_content_id = ?");
										pst.setInt(1, uF.parseToInt(hidepptid[i]));
//										System.out.println("pst pdf select ========> " + pst);
										rst = pst.executeQuery();
										while (rst.next()) {
											contentPPTFlag = true;
	//										newSubchapterId = rst.getInt("course_content_id");
										}
										rst.close();
										pst.close();
									}
									if(contentPPTFlag == true) {
										if(getContentPPT() != null && getContentPPT()[i] != null) {
											pst = con.prepareStatement("update course_content_details set course_content_name=?,content_type=?,course_id=?" +
												",course_chapter_id=?,course_subchapter_id=?,updated_by=?,update_date=?,content_title=? where course_content_id = ?");
											pst.setString(1, "");
											pst.setString(2, "PPT");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, pptTitle[i]);
											pst.setInt(9, uF.parseToInt(hidepptid[i]));
//											System.out.println("pst pdf course_content_details ===> " + pst);
											pst.execute();
											rst.close();
											pst.close();
											
											int newPPTId = uF.parseToInt(hidepptid[i]);
										
										
											uploadImage(newPPTId,getContentPPT()[i],getContentPPTFileName()[i]);
										}
									} else {
										if(getContentPPT() != null && getContentPPT()[i] != null) {
											pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
													"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
											pst.setString(1, "");
											pst.setString(2, "PPT");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, pptTitle[i]);
//											System.out.println("pst pdf course_content_details ===> " + pst);
											pst.execute();
											rst.close();
											pst.close();
											
											int newPPTId = 0;
											pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
											rst = pst.executeQuery();
											while (rst.next()) {
												newPPTId = rst.getInt("course_content_id");
											}
											rst.close();
											pst.close();
										
										
											uploadImage(newPPTId,getContentPPT()[i],getContentPPTFileName()[i]);
										}
									}
									
								}
							}
					//===end parvez date: 13-01-2023===		
							
							for (int i = 0; getAttachSubchapterId() != null && i < getAttachSubchapterId().length; i++) {
								if(getAttachSubchapterId()[i].equals(subchapterCount[a])) {
									boolean contentAttechFlag = false;
									if(hidedocsid != null && hidedocsid.length > i){
										pst = con.prepareStatement("select course_content_id from course_content_details where course_content_id = ?");
										pst.setInt(1, uF.parseToInt(hidedocsid[i]));
//										System.out.println("pst doc select ========> " + pst);
										rst = pst.executeQuery();
										while (rst.next()) {
											contentAttechFlag = true;
	//										newSubchapterId = rst.getInt("course_content_id");
										}
										rst.close();
										pst.close();
									}
									if(contentAttechFlag == true) {
										if(getContentAttach()[i] != null) {
											pst = con.prepareStatement("update course_content_details set course_content_name=?,content_type=?,course_id=?" +
												",course_chapter_id=?,course_subchapter_id=?,updated_by=?,update_date=?,content_title=? where course_content_id = ?");
											pst.setString(1, "");
											pst.setString(2, "ATTACH");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, attachTitle[i]);
											pst.setInt(9, uF.parseToInt(hidedocsid[i]));
//											System.out.println("pst attach course_content_details ===> " + pst);
											pst.execute();
											pst.close();
											
											int newAttachId = uF.parseToInt(hidedocsid[i]);
										
										
											uploadImage(newAttachId,getContentAttach()[i],getContentAttachFileName()[i]);
										}
									} else {
										if(getContentAttach()[i] != null) {
											pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
													"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
											pst.setString(1, "");
											pst.setString(2, "ATTACH");
											pst.setInt(3, uF.parseToInt(getCourseId()));
											pst.setInt(4, newChapterId);
											pst.setInt(5, newSubchapterId);
											pst.setInt(6, uF.parseToInt(strSessionEmpId));
											pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setString(8, attachTitle[i]);
//											System.out.println("pst attach course_content_details ===> " + pst);
											pst.execute();
											pst.close();
											
											int newAttachId = 0;
											pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
											rst = pst.executeQuery();
											while (rst.next()) {
												newAttachId = rst.getInt("course_content_id");
											}
											rst.close();
											pst.close();
										
										
											uploadImage(newAttachId,getContentAttach()[i],getContentAttachFileName()[i]);
										}
									}
									
								}
							}
							 
							String weightage[] = request.getParameterValues("weightage"+chapterCnt+"_"+subchapterCount[a]);
							String question[] = request.getParameterValues("question"+chapterCnt+"_"+subchapterCount[a]);
							String hideassessmentid[] = request.getParameterValues("hideassessmentid"+chapterCnt+"_"+subchapterCount[a]);
							
							String addFlag[] = request.getParameterValues("status"+chapterCnt+"_"+subchapterCount[a]);
							String optiona[] = request.getParameterValues("optiona"+chapterCnt+"_"+subchapterCount[a]);
							String optionb[] = request.getParameterValues("optionb"+chapterCnt+"_"+subchapterCount[a]);
							String optionc[] = request.getParameterValues("optionc"+chapterCnt+"_"+subchapterCount[a]);
							String optiond[] = request.getParameterValues("optiond"+chapterCnt+"_"+subchapterCount[a]);
							String ansType[] = request.getParameterValues("ansType"+chapterCnt+"_"+subchapterCount[a]);
							String orientt[] = request.getParameterValues("orientt"+chapterCnt+"_"+subchapterCount[a]);
//							System.out.println("hideassessmentid ====> " + hideassessmentid.length);
							for (int i = 0; question != null && i < question.length; i++) {
								String[] correct = request.getParameterValues("correct"+chapterCnt+"_"+subchapterCount[a]+"_"+ orientt[i]);
								StringBuilder option = new StringBuilder();
	 
								for (int ab = 0; correct != null && ab < correct.length; ab++) {
									option.append(correct[ab] + ",");
								}
//								System.out.println("hideassessmentid[i] ====> " + hideassessmentid[i]);
								int question_id = 0;
								pst = con.prepareStatement("insert into course_question_bank(course_question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,answer_type)values(?,?,?,?,?,?,?,?)");
								pst.setString(1, question[i]);
								pst.setString(2, (optiona != null && optiona.length > i ? optiona[i]: ""));
								pst.setString(3, (optionb != null && optionb.length > i ? optionb[i]: ""));
								pst.setString(4, (optionc != null && optionc.length > i ? optionc[i]: ""));
								pst.setString(5, (optiond != null && optiond.length > i ? optiond[i]: ""));
								pst.setString(6, option.toString());
								
								if(addFlag !=null && addFlag.length > 0 && addFlag[i] != null && !addFlag[i].equals("")) {
									pst.setBoolean(7, uF.parseToBoolean(addFlag[i]));
								}else {
									pst.setBoolean(7, false);
								}
								
								pst.setInt(8, uF.parseToInt(ansType[i]));
								pst.execute();
								pst.close();

								pst = con.prepareStatement("select max(course_question_bank_id) from course_question_bank");
								rst = pst.executeQuery();
								while (rst.next()) {
									question_id = rst.getInt(1);
								}
								rst.close();
								pst.close();
								boolean assessFlag = false;
								if(hideassessmentid != null && hideassessmentid.length > i){
									pst = con.prepareStatement("select course_assessment_id from course_assessment_details where course_assessment_id = ?");
									pst.setInt(1, uF.parseToInt(hideassessmentid[i]));
									rst = pst.executeQuery();
									while (rst.next()) {
										assessFlag = true;
									}
									rst.close();
									pst.close();
								}
//								System.out.println("assessFlag ===> " + assessFlag);
								if(assessFlag == true) {
									pst = con.prepareStatement("update course_assessment_details set assessment_id=?,weightage=?,course_id=?,course_chapter_id=?," +
											"course_subchapter_id=?,updated_by=?,update_date=? where course_assessment_id = ?");
									pst.setInt(1, question_id);
									pst.setDouble(2, uF.parseToDouble(weightage[i]));
									pst.setInt(3, uF.parseToInt(getCourseId()));
									pst.setInt(4, newChapterId);
									pst.setInt(5, newSubchapterId);
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(8, uF.parseToInt(hideassessmentid[i]));
									pst.execute();
									pst.close();
								} else{
									pst = con.prepareStatement("insert into course_assessment_details(assessment_id,weightage,course_id,course_chapter_id," +
											"course_subchapter_id,added_by,entry_date)values(?,?,?,?, ?,?,?)");
									pst.setInt(1, question_id);
									pst.setDouble(2, uF.parseToDouble(weightage[i]));
									pst.setInt(3, uF.parseToInt(getCourseId()));
									pst.setInt(4, newChapterId);
									pst.setInt(5, newSubchapterId);
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.execute();
									pst.close();
								}
							}
							
							
							
						} else {
							pst = con.prepareStatement("insert into course_subchapter_details(course_subchapter_name,subchapter_description,course_id" +
								",course_chapter_id,added_by,entry_date)values(?,?,?, ?,?,?)");
							pst.setString(1, subchapterName[a]);
							if(descStatus[a].equals("1")) {
								pst.setString(2, subchapterDescription[a]);
							} else {
								pst.setString(2, "");
							}
							pst.setInt(3, uF.parseToInt(getCourseId()));
							pst.setInt(4, newChapterId);
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
//							System.out.println("pst course_subchapter_details ===> " + pst);
							pst.execute();
							pst.close();
							
							pst = con.prepareStatement("select max(course_subchapter_id) as course_subchapter_id from course_subchapter_details");
							rst = pst.executeQuery();
							while (rst.next()) {
								newSubchapterId = rst.getInt("course_subchapter_id");
							}
							rst.close();
							pst.close();
						
//						String contentTextarea[] = request.getParameterValues("contentTextarea"+subchapterCount[a]);
//						String contentImage[] = request.getParameterValues("contentImage"+subchapterCount[a]);
//						String contentVideo[] = request.getParameterValues("contentVideo"+subchapterCount[a]);
//						String contentPdf[] = request.getParameterValues("contentPdf"+subchapterCount[a]);
//						String contentAttach[] = request.getParameterValues("contentAttach"+subchapterCount[a]);
						
						for (int i = 0; getTextSubchapterId() != null && i < getTextSubchapterId().length; i++) {
							if(getTextSubchapterId()[i].equals(subchapterCount[a])) {
								pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
										"course_subchapter_id,added_by,entry_date)values(?,?,?,?, ?,?,?)");
								pst.setString(1, contentTextarea[i]);
								pst.setString(2, "TEXT");
								pst.setInt(3, uF.parseToInt(getCourseId()));
								pst.setInt(4, newChapterId);
								pst.setInt(5, newSubchapterId);
								pst.setInt(6, uF.parseToInt(strSessionEmpId));
								pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
//								System.out.println("pst else text course_content_details ===> " + pst);
								pst.execute();
								pst.close();
							}
						}
						
						for (int i = 0; getImageSubchapterId() != null && i < getImageSubchapterId().length; i++) {
							if(getImageSubchapterId()[i].equals(subchapterCount[a])) {
								if(getContentImage()[i] != null) {
									pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
											"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
									pst.setString(1, "");
									pst.setString(2, "IMAGE");
									pst.setInt(3, uF.parseToInt(getCourseId()));
									pst.setInt(4, newChapterId);
									pst.setInt(5, newSubchapterId);
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setString(8, imageTitle[i]);
//									System.out.println("pst else image course_content_details ===> " + pst);
									pst.execute();
									pst.close();
									
									int newImageId = 0;
									pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
									rst = pst.executeQuery();
									while (rst.next()) {
										newImageId = rst.getInt("course_content_id");
									}
									pst.close();
								
//									System.out.println("getContentImageFileName()[i] ===> "+ getContentImageFileName()[i]);
//									System.out.println("getContentImage()[i] ===> "+ getContentImage()[i]);
									uploadImage(newImageId,getContentImage()[i],getContentImageFileName()[i]);
								}
							}
						}
						
						
						for (int i = 0; getVideoSubchapterId() != null && i < getVideoSubchapterId().length; i++) {
							if(getVideoSubchapterId()[i].equals(subchapterCount[a])) {
								if(getContentVideo()[i] != null) {
									pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
											"course_subchapter_id,added_by,entry_date,content_title,content_url)values(?,?,?,?, ?,?,?,?, ?)");
									pst.setString(1, "");
									pst.setString(2, "VIDEO");
									pst.setInt(3, uF.parseToInt(getCourseId()));
									pst.setInt(4, newChapterId);
									pst.setInt(5, newSubchapterId);
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setString(8, videoTitle[i]);
									pst.setString(9, videoUrl[i]);
//									System.out.println("pst else video course_content_details ===> " + pst);
									pst.execute();
									pst.close();
									
									int newVideoId = 0;
									pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
									rst = pst.executeQuery();
									while (rst.next()) {
										newVideoId = rst.getInt("course_content_id");
									}
									pst.close();
								
//									System.out.println("getContentImageFileName()[i] ===> "+ getContentImageFileName()[i]);
//									System.out.println("getContentImage()[i] ===> "+ getContentImage()[i]);
//									uploadImage(newVideoId,getContentVideo()[i],getContentVideoFileName()[i]);
								}
							}
						}
						
						for (int i = 0; getPdfSubchapterId() != null && i < getPdfSubchapterId().length; i++) {
							if(getPdfSubchapterId()[i].equals(subchapterCount[a])) {
								System.out.println("ANC/2544---getContentPdf()=="+getContentPdf());
								if(getContentPdf() != null && getContentPdf()[i] != null) {
									pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
											"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
									pst.setString(1, "");
									pst.setString(2, "PDF");
									pst.setInt(3, uF.parseToInt(getCourseId()));
									pst.setInt(4, newChapterId);
									pst.setInt(5, newSubchapterId);
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setString(8, pdfTitle[i]);
//									System.out.println("pst else pdf course_content_details ===> " + pst);
									pst.execute();
									pst.close();
									
									int newPDFId = 0;
									pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
									rst = pst.executeQuery();
									while (rst.next()) {
										newPDFId = rst.getInt("course_content_id");
									}
									rst.close();
									pst.close();
								
//									System.out.println("getContentImageFileName()[i] ===> "+ getContentImageFileName()[i]);
//									System.out.println("getContentImage()[i] ===> "+ getContentImage()[i]);
									uploadImage(newPDFId,getContentPdf()[i],getContentPdfFileName()[i]);
								}
							}
						}
						
					//===start parvez date: 13-01-2023===	
						for (int i = 0; getPptSubchapterId() != null && i < getPptSubchapterId().length; i++) {
							if(getPptSubchapterId()[i].equals(subchapterCount[a])) {
								if(getContentPPT() != null && getContentPPT()[i] != null) {
									pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
											"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
									pst.setString(1, "");
									pst.setString(2, "PPT");
									pst.setInt(3, uF.parseToInt(getCourseId()));
									pst.setInt(4, newChapterId);
									pst.setInt(5, newSubchapterId);
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setString(8, pptTitle[i]);
//									System.out.println("pst else pdf course_content_details ===> " + pst);
									pst.execute();
									pst.close();
									
									int newPPTId = 0;
									pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
									rst = pst.executeQuery();
									while (rst.next()) {
										newPPTId = rst.getInt("course_content_id");
									}
									rst.close();
									pst.close();
								
//									System.out.println("getContentImageFileName()[i] ===> "+ getContentImageFileName()[i]);
//									System.out.println("getContentImage()[i] ===> "+ getContentImage()[i]);
									uploadImage(newPPTId,getContentPPT()[i],getContentPPTFileName()[i]);
								}
							}
						}
					//===end parvez date: 13-01-2023===	
						
						for (int i = 0; getAttachSubchapterId() != null && i < getAttachSubchapterId().length; i++) {
							if(getAttachSubchapterId()[i].equals(subchapterCount[a])) {
								if(getContentAttach()[i] != null) {
									pst = con.prepareStatement("insert into course_content_details(course_content_name,content_type,course_id,course_chapter_id," +
											"course_subchapter_id,added_by,entry_date,content_title)values(?,?,?,?, ?,?,?,?)");
									pst.setString(1, "");
									pst.setString(2, "ATTACH");
									pst.setInt(3, uF.parseToInt(getCourseId()));
									pst.setInt(4, newChapterId);
									pst.setInt(5, newSubchapterId);
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setString(8, attachTitle[i]);
//									System.out.println("pst else attach course_content_details ===> " + pst);
									pst.execute();
									pst.close();
									
									int newAttachId = 0;
									pst = con.prepareStatement("select max(course_content_id) as course_content_id from course_content_details");
									rst = pst.executeQuery();
									while (rst.next()) {
										newAttachId = rst.getInt("course_content_id");
									}
									rst.close();
									pst.close();
								
//									System.out.println("getContentImageFileName()[i] ===> "+ getContentImageFileName()[i]);
//									System.out.println("getContentImage()[i] ===> "+ getContentImage()[i]);
									uploadImage(newAttachId,getContentAttach()[i],getContentAttachFileName()[i]);
								}
							}
						}
						 
						String weightage[] = request.getParameterValues("weightage"+chapterCnt+"_"+subchapterCount[a]);
						String question[] = request.getParameterValues("question"+chapterCnt+"_"+subchapterCount[a]);

						String addFlag[] = request.getParameterValues("status"+chapterCnt+"_"+subchapterCount[a]);
						String optiona[] = request.getParameterValues("optiona"+chapterCnt+"_"+subchapterCount[a]);
						String optionb[] = request.getParameterValues("optionb"+chapterCnt+"_"+subchapterCount[a]);
						String optionc[] = request.getParameterValues("optionc"+chapterCnt+"_"+subchapterCount[a]);
						String optiond[] = request.getParameterValues("optiond"+chapterCnt+"_"+subchapterCount[a]);
						String ansType[] = request.getParameterValues("ansType"+chapterCnt+"_"+subchapterCount[a]);
						String orientt[] = request.getParameterValues("orientt"+chapterCnt+"_"+subchapterCount[a]);
						
						for (int i = 0; question != null && i < question.length; i++) {
							String[] correct = request.getParameterValues("correct"+chapterCnt+"_"+subchapterCount[a]+"_"+ orientt[i]);
							StringBuilder option = new StringBuilder();
 
							for (int ab = 0; correct != null && ab < correct.length; ab++) {
								option.append(correct[ab] + ",");
							}
							int question_id = 0;
							pst = con.prepareStatement("insert into course_question_bank(course_question_text,option_a,option_b,option_c,option_d,correct_ans,is_add,answer_type)values(?,?,?,?,?,?,?,?)");
							pst.setString(1, question[i]);
							pst.setString(2, (optiona != null && optiona.length > i ? optiona[i]: ""));
							pst.setString(3, (optionb != null && optionb.length > i ? optionb[i]: ""));
							pst.setString(4, (optionc != null && optionc.length > i ? optionc[i]: ""));
							pst.setString(5, (optiond != null && optiond.length > i ? optiond[i]: ""));
							pst.setString(6, option.toString());
							
							if(addFlag!= null && addFlag.length > 0 && addFlag[i]!=null && addFlag[i]!="") {
								pst.setBoolean(7, uF.parseToBoolean(addFlag[i]));
							} else {
								pst.setBoolean(7, false);
							}
							
							pst.setInt(8, uF.parseToInt(ansType[i]));
							pst.execute();
							pst.close();

							pst = con.prepareStatement("select max(course_question_bank_id) from course_question_bank");
							rst = pst.executeQuery();
							while (rst.next()) {
								question_id = rst.getInt(1);
							}
							rst.close();
							pst.close();
							
							pst = con.prepareStatement("insert into course_assessment_details(assessment_id,weightage,course_id,course_chapter_id," +
									"course_subchapter_id,added_by,entry_date)values(?,?,?,?, ?,?,?)");
							pst.setInt(1, question_id);
							pst.setDouble(2, uF.parseToDouble(weightage[i]));
							pst.setInt(3, uF.parseToInt(getCourseId()));
							pst.setInt(4, newChapterId);
							pst.setInt(5, newSubchapterId);
							pst.setInt(6, uF.parseToInt(strSessionEmpId));
							pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.execute();
							pst.close();
						}
					}
				}
				}	
			setChapterCnt(""+(uF.parseToInt(getChapterCnt())+1));
				
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

		String courseIDNew = null;

		try {
			pst = con.prepareStatement("insert into course_details(course_name,course_subject,author,date_of_creation," +
					"course_version,preface,added_by,entry_date)values(?,?,?,?, ?,?,?,?)");
			pst.setString(1, getCourseName());
			pst.setString(2, getCourseSubject());
			pst.setString(3, getCourseAuthor());
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(5, getCourseVersion());
			pst.setString(6, getCoursePreface());
			pst.setInt(7, uF.parseToInt(strSessionEmpId));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();

			pst = con.prepareStatement(" select max(course_id) as course_id from course_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				courseIDNew = rst.getString("course_id");
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
		
		return courseIDNew;
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

	String courseName;
	String courseSubject;
	String courseAuthor;
	String courseVersion;
	String coursePreface;
	String assignToExist;
	
	public String getAssignToExist() {
		return assignToExist;
	}

	public void setAssignToExist(String assignToExist) {
		this.assignToExist = assignToExist;
	}

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
	String[] textSubchapterId;
	String[] imageSubchapterId;
	String[] pdfSubchapterId;
	String[] pptSubchapterId;
	String[] videoSubchapterId;
	String[] attachSubchapterId;
	
	String[] contentTextarea;
	File[] contentImage;
	String[] contentImageFileName;
	String[] imageTitle;
	File[] contentVideo;
	String[] contentVideoFileName;
	String[] videoTitle;
	String[] videoUrl;
	File[] contentPdf;
	File[] contentPPT;
	String[] contentPdfFileName;
	String[] contentPPTFileName;
	String[] pdfTitle;
	String[] pptTitle;
	File[] contentAttach;
	String[] contentAttachFileName;
	String[] attachTitle;
	
	// Fields for 3rd Screen ****************

	public String[] getContentImageFileName() {
		return contentImageFileName;
	}

	public void setContentImageFileName(String[] contentImageFileName) {
		this.contentImageFileName = contentImageFileName;
	}

	public String[] getContentVideoFileName() {
		return contentVideoFileName;
	}

	public void setContentVideoFileName(String[] contentVideoFileName) {
		this.contentVideoFileName = contentVideoFileName;
	}

	public String[] getContentPdfFileName() {
		return contentPdfFileName;
	}

	public void setContentPdfFileName(String[] contentPdfFileName) {
		this.contentPdfFileName = contentPdfFileName;
	}

	public String[] getContentAttachFileName() {
		return contentAttachFileName;
	}

	public void setContentAttachFileName(String[] contentAttachFileName) {
		this.contentAttachFileName = contentAttachFileName;
	}

	public String[] getTextSubchapterId() {
		return textSubchapterId;
	}

	public void setTextSubchapterId(String[] textSubchapterId) {
		this.textSubchapterId = textSubchapterId;
	}

	public String[] getImageSubchapterId() {
		return imageSubchapterId;
	}

	public void setImageSubchapterId(String[] imageSubchapterId) {
		this.imageSubchapterId = imageSubchapterId;
	}

	public String[] getPdfSubchapterId() {
		return pdfSubchapterId;
	}

	public void setPdfSubchapterId(String[] pdfSubchapterId) {
		this.pdfSubchapterId = pdfSubchapterId;
	}

	public String[] getVideoSubchapterId() {
		return videoSubchapterId;
	}

	public void setVideoSubchapterId(String[] videoSubchapterId) {
		this.videoSubchapterId = videoSubchapterId;
	}

	public String[] getAttachSubchapterId() {
		return attachSubchapterId;
	}

	public void setAttachSubchapterId(String[] attachSubchapterId) {
		this.attachSubchapterId = attachSubchapterId;
	}

	public String[] getContentTextarea() {
		return contentTextarea;
	}

	public void setContentTextarea(String[] contentTextarea) {
		this.contentTextarea = contentTextarea;
	}

	public File[] getContentImage() {
		return contentImage;
	}

	public void setContentImage(File[] contentImage) {
		this.contentImage = contentImage;
	}

	public File[] getContentVideo() {
		return contentVideo;
	}

	public void setContentVideo(File[] contentVideo) {
		this.contentVideo = contentVideo;
	}

	public File[] getContentPdf() {
		return contentPdf;
	}

	public void setContentPdf(File[] contentPdf) {
		this.contentPdf = contentPdf;
	}

	public File[] getContentAttach() {
		return contentAttach;
	}

	public void setContentAttach(File[] contentAttach) {
		this.contentAttach = contentAttach;
	}

	public String[] getImageTitle() {
		return imageTitle;
	}

	public void setImageTitle(String[] imageTitle) {
		this.imageTitle = imageTitle;
	}

	public String[] getVideoTitle() {
		return videoTitle;
	}


	public void setVideoTitle(String[] videoTitle) {
		this.videoTitle = videoTitle;
	}


	public String[] getVideoUrl() {
		return videoUrl;
	}


	public void setVideoUrl(String[] videoUrl) {
		this.videoUrl = videoUrl;
	}


	public String[] getPdfTitle() {
		return pdfTitle;
	}


	public void setPdfTitle(String[] pdfTitle) {
		this.pdfTitle = pdfTitle;
	}

	public String[] getAttachTitle() {
		return attachTitle;
	}

	public void setAttachTitle(String[] attachTitle) {
		this.attachTitle = attachTitle;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
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

	public String getChapterCnt() {
		return chapterCnt;
	}

	public void setChapterCnt(String chapterCnt) {
		this.chapterCnt = chapterCnt;
	}

	public String getStepSave() {
		return stepSave;
	}

	public void setStepSave(String stepSave) {
		this.stepSave = stepSave;
	}

	public String[] getPptSubchapterId() {
		return pptSubchapterId;
	}

	public void setPptSubchapterId(String[] pptSubchapterId) {
		this.pptSubchapterId = pptSubchapterId;
	}

	public File[] getContentPPT() {
		return contentPPT;
	}

	public void setContentPPT(File[] contentPPT) {
		this.contentPPT = contentPPT;
	}

	public String[] getPptTitle() {
		return pptTitle;
	}

	public void setPptTitle(String[] pptTitle) {
		this.pptTitle = pptTitle;
	}

	public String[] getContentPPTFileName() {
		return contentPPTFileName;
	}

	public void setContentPPTFileName(String[] contentPPTFileName) {
		this.contentPPTFileName = contentPPTFileName;
	}

}
