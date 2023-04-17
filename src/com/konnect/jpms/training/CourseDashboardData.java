package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CourseDashboardData extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	String strEmpOrgId = null;
	CommonFunctions CF = null;
	String strSessionEmpId = null;

	private String dataType;
	private String strCourseId;
	private String strAssessId;
	private String strSearchJob;
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId  = (String) session.getAttribute(ORGID);
		
//		System.out.println("CourseDashboardData getDataType()=======>"+getDataType());
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")){
			setDataType("C");
		}
		
		if(getDataType() != null && getDataType().equals("C")) {
			viewCourseDetails();
		} else if(getDataType() != null && getDataType().equals("A")) {
			viewAssessmentDetails();
		}
		
		if(strUserType != null && !strUserType.equals(EMPLOYEE)) {
			getSearchAutoCompleteData();
		}
		return LOAD;

	}

	private void getSearchAutoCompleteData() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			SortedSet<String> setDataList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			if(getDataType() != null && getDataType().equals("A")) {
				sbQuery.append("select * from assessment_details where assessment_details_id > 0 ");
			    
	    	    if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
					sbQuery.append(" and (upper(assessment_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
					
	            }
	    	    
	    	    pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst assessment search===> "+ pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					setDataList.add(rs.getString("assessment_name"));
				}
				rs.close();
				pst.close();
			}else if(getDataType() != null && getDataType().equals("C")){
				sbQuery.append("select * from course_details  where course_id > 0 ");
			    
	    	    if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
					sbQuery.append(" and (upper(course_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
					
	            }
	    	    
	    	    pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst course search===> "+ pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					setDataList.add(rs.getString("course_name"));
				}
				rs.close();
				pst.close();
			}
			
			StringBuilder sbData = null;
			Iterator<String> it = setDataList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
				if(sbData == null) {
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null) {
				sbData = new StringBuilder();
			}
			
//			System.out.println("sbDatat==>"+sbData);
			request.setAttribute("sbData", sbData.toString());
				
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void viewAssessmentDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF =new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			
			Map<String, String> hmAssessmentData = new HashMap<String, String>();
			Map<String, List<String>> hmAllAssessmentData = new HashMap<String, List<String>>();
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from assessment_details where root_assessment_id is not null order by assessment_details_id desc");
			pst = con.prepareStatement(strQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("pst ====> "+pst);
			List<String> assessmentIdList = new ArrayList<String>();
			while (rst.next()) {
				assessmentIdList = hmAllAssessmentData.get(rst.getString("root_assessment_id"));
				if(assessmentIdList == null) assessmentIdList = new ArrayList<String>();
				
				assessmentIdList.add(rst.getString("assessment_details_id"));
				hmAllAssessmentData.put(rst.getString("root_assessment_id"), assessmentIdList);
				
				if(hmAssessmentData.get(rst.getString("root_assessment_id")) == null){
					hmAssessmentData.put(rst.getString("root_assessment_id"), rst.getString("assessment_details_id"));
				}
			}
			rst.close();
			pst.close();
			
			int i =0;
			List<String> assessmentIDList = new ArrayList<String>();
			strQuery = new StringBuilder();
			strQuery.append("select * from assessment_details where parent_assessment_id is null and root_assessment_id is null  ");
		    if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
		    	strQuery.append(" and (upper(assessment_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			strQuery.append(" order by assessment_details_id desc"); 
			pst = con.prepareStatement(strQuery.toString());
			rst = pst.executeQuery();
			while (rst.next()) {
				if(i == 0) {
					setStrAssessId(rst.getString("assessment_details_id"));
				}
				assessmentIDList.add(rst.getString("assessment_details_id"));
				
				i++;
			}
			rst.close();
			pst.close();
			
			request.setAttribute("assessmentIDList", assessmentIDList);
//			System.out.println("assessmentIDList ===> " + assessmentIDList);
//			System.out.println("hmAllAssessmentData ===> " + hmAllAssessmentData);
//			System.out.println("hmAssessmentData ===> " + hmAssessmentData);
			request.setAttribute("hmAllAssessmentData", hmAllAssessmentData);
			request.setAttribute("hmAssessmentData", hmAssessmentData);
			
			Map<String, String> hmSubjectName = CF.getSubjectsMap(con);
			Map<String, String> hmAssessmentDetails = new HashMap<String, String>();
			
			strQuery = new StringBuilder();
			strQuery.append("select * from assessment_details order by assessment_details_id");

			pst = con.prepareStatement(strQuery.toString());
			rst = pst.executeQuery();
		
			while (rst.next()) {
				StringBuilder sbStauts = new StringBuilder();
				boolean statusFlag = checkAssessmentStatus(con, rst.getString("assessment_details_id"));
				if(statusFlag == false) {
					sbStauts.append("<div style=\"float:left;border-left:4px solid #ff9a02;padding:10px;\" class=\"custom-legend pullout\"><div class=\"legend-info\"></div></div>");//3
				} else {
					sbStauts.append("<div style=\"float:left;border-left:4px solid #15AA08;padding:10px;\" class=\"custom-legend approved\"><div class=\"legend-info\"></div></div>");//3
				}
				
				hmAssessmentDetails.put(rst.getString("assessment_details_id")+"_NAME", rst.getString("assessment_name"));
				hmAssessmentDetails.put(rst.getString("assessment_details_id")+"_STATUS", sbStauts.toString());
				hmAssessmentDetails.put(rst.getString("assessment_details_id")+"_SUB", uF.showData(hmSubjectName.get(rst.getString("assessment_subject")),""));
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmAssessmentDetails", hmAssessmentDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	
	private void viewCourseDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF =new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			int j =0 ;
			Map<String, String> hmCourseData = new LinkedHashMap<String, String>();
			Map<String, List<String>> hmAllCourseData = new HashMap<String, List<String>>();
			
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from course_details where root_course_id is not null order by course_id desc");
			pst = con.prepareStatement(strQuery.toString());
			rst = pst.executeQuery();
//		    System.out.println("pst2 ====> "+pst);
			List<String> courseIdList = new ArrayList<String>();
			while (rst.next()) {
				courseIdList = hmAllCourseData.get(rst.getString("root_course_id"));
				if(courseIdList == null) courseIdList = new ArrayList<String>();
				
				courseIdList.add(rst.getString("course_id"));
				hmAllCourseData.put(rst.getString("root_course_id"), courseIdList);

				if(hmCourseData.get(rst.getString("root_course_id")) == null) {
					hmCourseData.put(rst.getString("root_course_id"), rst.getString("course_id"));
					
				}
			
			}
			rst.close();
			pst.close();
//			System.out.println("hmAllCourseData==>"+hmAllCourseData);
//			System.out.println("hmCourseData==>"+hmCourseData);
			
			List<String> courseIDList = new ArrayList<String>();
			strQuery = new StringBuilder();
			strQuery.append("select * from course_details where parent_course_id is null and root_course_id is null");
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("") &&  !getStrSearchJob().equalsIgnoreCase("null")) {
				strQuery.append(" and (upper(course_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
			strQuery.append(" order by course_id desc");
			pst = con.prepareStatement(strQuery.toString());
//			System.out.println("pst1====> "+pst);
			rst = pst.executeQuery();
			while (rst.next()) {
				
				if(j == 0 ) {
					setStrCourseId(rst.getString("course_id"));
				}
				
				courseIDList.add(rst.getString("course_id"));
				
				j++;
			}
			rst.close();
			pst.close();
			
//			System.out.println("java course id ==>"+getStrCourseId());
//			System.out.println("courseIDList==>"+courseIDList);
			Map<String, String> hmSubjectName = CF.getSubjectsMap(con);
			Map<String, String> hmCourseDetails = new LinkedHashMap<String, String>();
			strQuery = new StringBuilder();
			strQuery.append("select * from course_details order by course_id  desc");
			pst = con.prepareStatement(strQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("pst3 ====> "+pst);
			while (rst.next()) {
				StringBuilder sbStauts = new StringBuilder();
				boolean statusFlag = checkCourseStatus(con, rst.getString("course_id"));;
				if(statusFlag == false) {
					sbStauts.append("<div style=\"float:left;border-left:4px solid #ff9a02;padding:10px;\" class=\"custom-legend pullout\"><div class=\"legend-info\"></div></div>");//3
				} else {
					sbStauts.append("<div style=\"float:left;border-left:4px solid #15AA08;padding:10px;\" class=\"custom-legend approved\"><div class=\"legend-info\"></div></div>");//3
				}
				hmCourseDetails.put(rst.getString("course_id")+"_NAME", rst.getString("course_name"));
				hmCourseDetails.put(rst.getString("course_id")+"_STATUS", sbStauts.toString());
				hmCourseDetails.put(rst.getString("course_id")+"_SUB", uF.showData(hmSubjectName.get(rst.getString("course_subject")),""));
				
			}
			rst.close();
			pst.close();
			
			request.setAttribute("courseIDList", courseIDList);
			request.setAttribute("hmAllCourseData", hmAllCourseData);
			request.setAttribute("hmCourseData", hmCourseData);
			request.setAttribute("hmCourseDetails", hmCourseDetails);
		
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
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getStrCourseId() {
		return strCourseId;
	}

	public void setStrCourseId(String strCourseId) {
		this.strCourseId = strCourseId;
	}

	public String getStrAssessId() {
		return strAssessId;
	}

	public void setStrAssessId(String strAssessId) {
		this.strAssessId = strAssessId;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}
}
