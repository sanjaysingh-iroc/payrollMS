package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TrainingScheduleOneDayDetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	private String trainingId;
	private String scheduleId;
	private String dayDate;
	private String dayCount;
	private String dayDescription;
	private String strDate;
	private String dayDesId;
	
	CommonFunctions CF=null;
	
	private static Logger log = Logger.getLogger(LearningPlanReasonPopup.class);
	
	public String execute() {
		
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
		return LOGIN;
		}
		UtilityFunctions uF = new UtilityFunctions();
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/training/TrainingScheduleOneDayDetails.jsp");
		request.setAttribute(TITLE, "Training Schedule Day Details");
//		System.out.println("in TrainingScheduleOneDayDetails ");
		getScheduleDayDetails(uF);
		return SUCCESS;

	}

	private void getScheduleDayDetails(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select day_description,day_date from training_schedule_details where training_schedule_details_id = ?");
			pst.setInt(1, uF.parseToInt(getDayDesId()));
//				System.out.println("pst ========== > " + pst);
			rst = pst.executeQuery();
			while (rst.next()) {
				setDayDescription(rst.getString("day_description"));
				setStrDate(uF.getDateFormat(rst.getString("day_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rst.close();
			pst.close();
			}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public String getTrainingId() {
		return trainingId;
	}
	public void setTrainingId(String trainingId) {
		this.trainingId = trainingId;
	}
	public String getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}
	public String getDayDate() {
		return dayDate;
	}
	public void setDayDate(String dayDate) {
		this.dayDate = dayDate;
	}
	public String getDayCount() {
		return dayCount;
	}
	public void setDayCount(String dayCount) {
		this.dayCount = dayCount;
	}

	public String getDayDescription() {
		return dayDescription;
	}

	public void setDayDescription(String dayDescription) {
		this.dayDescription = dayDescription;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String getDayDesId() {
		return dayDesId;
	}

	public void setDayDesId(String dayDesId) {
		this.dayDesId = dayDesId;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}
	
}