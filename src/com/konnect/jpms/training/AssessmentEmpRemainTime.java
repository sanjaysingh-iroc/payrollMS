package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AssessmentEmpRemainTime extends ActionSupport implements IStatements, ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6957524740219848447L;
	
	String strSessionEmpId;
	HttpSession session;
	CommonFunctions CF;
	
	private String assessmentId;
	private String lPlanId;
	private String timeDuration;
	 
	public String execute() throws Exception {
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		updateAssessmentRemainingTime();
		return SUCCESS;
	
	}

	public void updateAssessmentRemainingTime(){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			//emp_id,learning_plan_id,assessment_id,remaining_time,entry_date
			pst = con.prepareStatement("update assessment_emp_remain_time set remaining_time=?,entry_date=? " +
					"where emp_id=? and learning_plan_id=? and assessment_id=?");
			pst.setString(1, getTimeDuration());
			pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getlPlanId()));
			pst.setInt(5, uF.parseToInt(getAssessmentId()));
			int x = pst.executeUpdate();
			if(x == 0){
				pst = con.prepareStatement("insert into assessment_emp_remain_time (emp_id,learning_plan_id,assessment_id,remaining_time,entry_date)" +
						"values(?,?, ?,?, ?)");
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, uF.parseToInt(getlPlanId()));
				pst.setInt(3, uF.parseToInt(getAssessmentId()));
				pst.setString(4, getTimeDuration());
				pst.setTimestamp(5, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				int y = pst.executeUpdate();
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}

	public String getlPlanId() {
		return lPlanId;
	}

	public void setlPlanId(String lPlanId) {
		this.lPlanId = lPlanId;
	}

	public String getTimeDuration() {
		return timeDuration;
	}

	public void setTimeDuration(String timeDuration) {
		this.timeDuration = timeDuration;
	}
	
}
