package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
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

public class CourseRead extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF=null;
	
	private static Logger log = Logger.getLogger(CourseRead.class);
	
	private String courseId;
	private String lPlanId;
	private String courseName;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
		return LOGIN;
		}
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/training/CourseRead.jsp");
		request.setAttribute(TITLE, "Course Read");
		
		courseRead();
		
		return SUCCESS;

	}

	
	private void getCourseNameByID(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rst=null;
		try {
			pst = con.prepareStatement("select course_id,course_name from course_details where course_id = ?");
			pst.setInt(1, uF.parseToInt(courseId));
			rst = pst.executeQuery();
			while(rst.next()) {
				setCourseName(rst.getString("course_name"));
			}
			rst.close();
			pst.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private void courseRead() {
		
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con=db.makeConnection(con);
			getCourseNameByID(con, uF);
			pst = con.prepareStatement("insert into course_read_details(emp_id,learning_plan_id,course_id,course_read_status,added_by,entry_date) values(?,?,?,?, ?,?)");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(lPlanId));
			pst.setInt(3, uF.parseToInt(courseId));
			pst.setInt(4, uF.parseToInt("1"));
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.executeUpdate();
			pst.close();
			
//			request.setAttribute("alLiveLearnings", alLiveLearnings);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
			
		}
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

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}

}
