package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewAssessmentsForRound extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	CommonFunctions CF = null;
	String strAction = null;
	private static Logger log = Logger.getLogger(TakeInterview.class);

	String roundId;
	String operation;
	String recruitId;
	String assessmentId;
	
	public String execute() throws Exception {
	
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(PAGE, "/jsp/recruitment/ViewAssessmentsForRound.jsp");		
		request.setAttribute(TITLE, "Assessments");
		
		UtilityFunctions uF = new UtilityFunctions();
		
		viewAssessments(uF);
		if(getOperation() != null && getOperation().equals("add")) {
			addAssessmentToRound(uF);
		}
		return SUCCESS;

	}

	
	
	private void addAssessmentToRound(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			con=db.makeConnection(con);
			pst = con.prepareStatement("select * from assessment_details where assessment_details_id = ?");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
//			System.out.println("pst ===> " + pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			String assessmentName = null;
			while(rst.next()) {
				assessmentName = rst.getString("assessment_name");
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("update panel_interview_details set assessment_id =? where recruitment_id = ? and round_id = ? and panel_emp_id is null");
			pst.setInt(1, uF.parseToInt(getAssessmentId()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			pst.setInt(3, uF.parseToInt(getRoundId()));
			int x = pst.executeUpdate();
//			System.out.println("x ===> " + x+ " -- assessmentName ===>> " + assessmentName);
//			System.out.println("new Date ===> " + new Date());
			pst.close();
			if(x>0) {
				request.setAttribute("assessmentName", assessmentName);
			}

			
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



	private String viewAssessments(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			con=db.makeConnection(con);
			List<List<String>> assessmentList = new ArrayList<List<String>>();
		
			pst = con.prepareStatement("select * from assessment_details where parent_assessment_id > 0");
//			System.out.println("pst ===> " + pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			List<String> parentAssessList = new ArrayList<String>();
			while(rst.next()) {
				parentAssessList.add(rst.getString("parent_assessment_id"));
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select * from assessment_details ");
//			System.out.println("pst ===> " + pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			StringBuilder sbOption = new StringBuilder();
			while(rst.next()) {
				if(!parentAssessList.contains(rst.getString("assessment_details_id"))) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rst.getString("assessment_details_id"));
					innerList.add(rst.getString("assessment_name"));
					assessmentList.add(innerList);
					if(uF.parseToInt(getAssessmentId()) == uF.parseToInt(rst.getString("assessment_details_id"))) {
						sbOption.append("<option value='"+rst.getString("assessment_details_id")+"' selected>"+rst.getString("assessment_name")+"</option>");
					} else {
						sbOption.append("<option value='"+rst.getString("assessment_details_id")+"'>"+rst.getString("assessment_name")+"</option>");
					}
				}
			}
			rst.close();
			pst.close();
//			System.out.println("sbOption===> " + sbOption.toString());
			request.setAttribute("sbOption", sbOption.toString());
			request.setAttribute("assessmentList", assessmentList);
		
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getRoundId() {
		return roundId;
	}

	public void setRoundId(String roundId) {
		this.roundId = roundId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}
 	
}
