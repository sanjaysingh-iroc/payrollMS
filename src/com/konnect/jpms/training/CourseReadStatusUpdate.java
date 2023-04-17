package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.performance.GetParameterList;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CourseReadStatusUpdate extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF=null;
	
	private static Logger log = Logger.getLogger(CourseReadStatusUpdate.class);
	
	private String courseId;
	private String lPlanId;
	private String fromPage;
	private String empId;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/training/CourseReadStatusUpdate.jsp");
		request.setAttribute(TITLE, "Course Read Status Update");
		UtilityFunctions uF = new UtilityFunctions();
		String updateStatus = request.getParameter("updateStatus");
		if(updateStatus != null){
			insertCourseReadStatus(uF);
//			return SUCCESS;
		}
//		System.out.println("fromPage=="+fromPage);
		getCourseData(uF);
		getChapterDataList(uF);
		getSubChapterDataList(uF);
		getReadChapters(uF);
//		trainingAttend();
		
		return LOAD;

	}

	
	private void getReadChapters(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmCourseReadStatus = new HashMap<String, String>();
			int count = 0;
			pst = con.prepareStatement("select course_chapter_id,read_status from course_read_update_details where emp_id = ? and learning_plan_id = ? and course_id = ?");
			if(getEmpId()!=null && !getEmpId().equals("") && getEmpId().length()>0){
				pst.setInt(1, uF.parseToInt(getEmpId()));
			} else{
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
			}
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(lPlanId));
			pst.setInt(3, uF.parseToInt(courseId));
			rst = pst.executeQuery();
			while(rst.next()) {
				count++;
				hmCourseReadStatus.put(rst.getString("course_chapter_id"), rst.getString("read_status"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmCourseReadStatus =====> " + hmCourseReadStatus);
			request.setAttribute("hmCourseReadStatus", hmCourseReadStatus);
			request.setAttribute("readChapterCount", count);
			
		}catch(Exception e){
			e.printStackTrace();
		}  finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void insertCourseReadStatus(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String chaters[] = request.getParameterValues("chaters");
//			System.out.println("chaters Length ===> " + chaters.length);
			for (int i = 0; chaters != null && i < chaters.length; i++) {
				pst = con.prepareStatement("insert into course_read_update_details(emp_id,learning_plan_id,course_id,course_chapter_id,read_status," +
							"added_by,entry_date) values(?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, uF.parseToInt(lPlanId));
				pst.setInt(3, uF.parseToInt(courseId));
				pst.setInt(4, uF.parseToInt(chaters[i]));
				pst.setInt(5, uF.parseToInt("1"));
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.executeUpdate();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public void getCourseData(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from course_details where course_id = ? ");
			pst.setInt(1, uF.parseToInt(getCourseId()));
			rst = pst.executeQuery();
			String crsPreface = null;
			while (rst.next()) {
				setCourseName(rst.getString("course_name"));
//				setSubjectID(rs.getString("course_subject"));
				setCourseAuthor(rst.getString("author"));
				setCourseSubject(rst.getString("course_subject"));
				setCourseName(rst.getString("course_name"));
				setCourseVersion(rst.getString("course_version"));
				crsPreface = rst.getString("preface");
			}
			rst.close();
			pst.close();
			request.setAttribute("crsPreface", crsPreface);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getChapterDataList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			con = db.makeConnection(con);
			List<List<String>> chapterList = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from course_chapter_details where course_id = ?");
			pst.setInt(1, uF.parseToInt(getCourseId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("course_chapter_id"));
				innerList.add(rst.getString("course_chapter_name"));
				innerList.add(rst.getString("chapter_description"));
				chapterList.add(innerList);
			}
			rst.close();
			pst.close();
			request.setAttribute("chapterList", chapterList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getSubChapterDataList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			con = db.makeConnection(con);
			Map<String, List<List<String>>> hmSubchapterData = new HashMap<String, List<List<String>>>();
//			List<List<String>> subchapterList = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from course_subchapter_details where course_id = ?");
			pst.setInt(1, uF.parseToInt(getCourseId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				List<List<String>> subchapterList = hmSubchapterData.get(rst.getString("course_chapter_id"));
				if(subchapterList == null ) subchapterList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("course_subchapter_id"));
				innerList.add(rst.getString("course_subchapter_name"));
				innerList.add(rst.getString("subchapter_description"));
				subchapterList.add(innerList);
				hmSubchapterData.put(rst.getString("course_chapter_id"), subchapterList);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmSubchapterData", hmSubchapterData);
//			System.out.println("hmSubchapterData ===> " + hmSubchapterData);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
//	private void trainingAttend() {
//		
//		Database db = new Database();
//		db.setRequest(request);
//		PreparedStatement pst = null;
//		Connection con = null;
//		UtilityFunctions uF = new UtilityFunctions();
//		ResultSet rst=null;
//		try {
//			con=db.makeConnection(con);
//			
//			pst = con.prepareStatement("insert into course_read_details(emp_id,learning_plan_id,course_id,course_read_status,added_by,entry_date) values(?,?,?,?, ?,?)");
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			pst.setInt(2, uF.parseToInt(lPlanId));
//			pst.setInt(3, uF.parseToInt(courseId));
//			pst.setInt(4, uF.parseToInt("1"));
//			pst.setInt(5, uF.parseToInt(strSessionEmpId));
//			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.executeUpdate();
//			
////			request.setAttribute("alLiveLearnings", alLiveLearnings);
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			db.closeResultSet(rst);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//			
//		}
//	}
	
	String courseName;
	String courseSubject;
	String courseAuthor;
	String courseVersion;
	String coursePreface;
	
	
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

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getlPlanId() {
		return lPlanId;
	}

	public void setlPlanId(String lPlanId) {
		this.lPlanId = lPlanId;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}


	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}


	public String getEmpId() {
		return empId;
	}


	public void setEmpId(String empId) {
		this.empId = empId;
	}
	
	

}
