package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

public class GetStageDetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private String stageTypeAndID;
	private String stageID;
	private String stageType;
	private String lstagetype;
	private String lstagename;
	private String lstagetypeid;
	private String count;
	private String planId;
	
	List<String> weekdaysValue = new ArrayList<String>();
	private String weekdayValue;
	private String dayValue;
	
	CommonFunctions CF = null;

	
	private static Logger log = Logger.getLogger(AddTrainingPlan.class);
	
	public String execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		
		request.setAttribute(PAGE, "/jsp/training/GetStageDetails.jsp");

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
//		System.out.println("GSD/62--Plan ID ===> "+getPlanId());
//		System.out.println("GSD/63--stageTypeAndID ===> "+ stageTypeAndID);
		if(stageTypeAndID != null){
			setStageType(stageTypeAndID.substring(0, 1));
			setStageID(stageTypeAndID.substring(1, stageTypeAndID.length()));
		}
//		System.out.println("GSD/68--stageType="+stageType);
//		System.out.println("GSD/69--stageID="+stageID);
		
		if(stageType != null && stageType.equals("T")){
			getTrainingData();
//			getTrainingData1();
		} else if(stageType != null && stageType.equals("C")){
			getCourseData();
		} else if(stageType != null && stageType.equals("A")){
			getAssessmentData();
	//===start parvez date: 22-09-2021===
		} else if(stageType != null && stageType.equals("V")){
			getVideoData();
		}
	//===end parvez date: 22-09-2021===
		
		return LOAD;
	}
	
	
	private void getAssessmentData() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			List<String> assessmentList = new ArrayList<String>();
			pst = con.prepareStatement("select * from assessment_details where assessment_details_id = ?");
//			select * from course_assessment_details cad, question_bank qb where question_bank_id = assessment_id and course_assessment_id = ?
			pst.setInt(1, uF.parseToInt(stageID));
			rs = pst.executeQuery();
			while (rs.next()) {
				assessmentList.add(rs.getString("assessment_details_id"));
				assessmentList.add(rs.getString("assessment_name"));
				assessmentList.add("Assessment");
//				assessmentList.add(rs.getString("assessment_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("assessmentList", assessmentList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getCourseData() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			List<String> courseList = new ArrayList<String>();
			pst = con.prepareStatement("select * from course_details where course_id = ?");
			pst.setInt(1, uF.parseToInt(stageID));
			rs = pst.executeQuery();
			while (rs.next()) {
				courseList.add(rs.getString("course_id"));
				courseList.add(rs.getString("course_name"));
				courseList.add("Course");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("courseList", courseList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


//	public void getTrainingData() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rs = null;
//		UtilityFunctions uF = new UtilityFunctions();
//		try {
//			con = db.makeConnection(con);
//			List<String> trainingList = new ArrayList<String>();
//			pst = con.prepareStatement("select tp.plan_id,training_title,schedule_id,start_date,end_date,day_schedule_type,training_frequency from training_schedule ts, training_plan tp where ts.plan_id = tp.plan_id and ts.plan_id = ?");
//			pst.setInt(1, uF.parseToInt(stageID));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				trainingList.add(rs.getString("plan_id"));
//				trainingList.add(rs.getString("training_title"));
//				trainingList.add("Training");
//			}
//			request.setAttribute("trainingList", trainingList);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//	}

	
	public void getTrainingData() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			List<String> trainingList = new ArrayList<String>();

			pst = con.prepareStatement("select tp.plan_id,training_title,schedule_id,start_date,end_date,day_schedule_type,training_frequency from training_schedule ts, training_plan tp where ts.plan_id = tp.plan_id and ts.plan_id = ?");
			pst.setInt(1, uF.parseToInt(stageID));
			rst = pst.executeQuery();
			while (rst.next()) {
				trainingList.add(rst.getString("plan_id"));
				trainingList.add(rst.getString("training_title"));
				trainingList.add("Training");
			}
			rst.close();
			pst.close();
			request.setAttribute("trainingList", trainingList);
			
			String schedule_id = null;
			List<String> trainingDataList = new ArrayList<String>();
			pst = con.prepareStatement("select * from training_schedule join training_session using (schedule_id) where plan_id=?");
			pst.setInt(1, uF.parseToInt(stageID));
//			System.out.println("pst======>"+pst);
			rst = pst.executeQuery();
			String trainingSchedulePeriod = null;
			String scheduleTypeValue = null;
			Map<String, String> hmWeekdays = new HashMap<String, String>();
			while (rst.next()) {
				trainingDataList.add(stageID);
				trainingDataList.add(uF.getDateFormat(rst.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
				trainingDataList.add(uF.getDateFormat(rst.getString("end_date"), DBDATE, CF.getStrReportDateFormat()));
				trainingDataList.add(uF.getDateFormat(rst.getString("start_date"), DBDATE, DATE_FORMAT));
				trainingDataList.add(uF.getDateFormat(rst.getString("end_date"), DBDATE, DATE_FORMAT));
//				setNoofdaysTraining(rst.getString("training_duration"));
				trainingSchedulePeriod = rst.getString("training_frequency") != null ? rst.getString("training_frequency") : "1";
				trainingDataList.add(rst.getString("training_frequency") != null ? rst.getString("training_frequency") : "1");
				schedule_id = rst.getString("schedule_id");
				if (trainingSchedulePeriod != null && trainingSchedulePeriod.equals("2")) {
					weekdayValue = rst.getString("training_weekday");
					dayValue = "";
				} else if (trainingSchedulePeriod != null && trainingSchedulePeriod.equals("3")) {
					dayValue = rst.getString("training_day");
				}
				trainingDataList.add(rst.getString("training_duration_type"));
				trainingDataList.add(rst.getString("day_schedule_type") != null ? rst.getString("day_schedule_type") : "1");
				scheduleTypeValue = rst.getString("day_schedule_type") != null ? rst.getString("day_schedule_type") : "1";
				if(rst.getString("week_days") != null) {
					List<String> weekdayList = Arrays.asList(rst.getString("week_days").split(","));
				
					for (int i = 0; weekdayList != null && !weekdayList.isEmpty() && i < weekdayList.size(); i++) {
						if(weekdayList.get(i).trim().equals("Mon")){
							hmWeekdays.put(stageID+"_MON", "checked");
						} else if(weekdayList.get(i).trim().equals("Tue")){
							hmWeekdays.put(stageID+"_TUE", "checked");
						} else if(weekdayList.get(i).trim().equals("Wed")){
							hmWeekdays.put(stageID+"_WED", "checked");
						} else if(weekdayList.get(i).trim().equals("Thu")){
							hmWeekdays.put(stageID+"_THU", "checked");
						} else if(weekdayList.get(i).trim().equals("Fri")){
							hmWeekdays.put(stageID+"_FRI", "checked");
						} else if(weekdayList.get(i).trim().equals("Sat")){
							hmWeekdays.put(stageID+"_SAT", "checked");
						} else if(weekdayList.get(i).trim().equals("Sun")){
							hmWeekdays.put(stageID+"_SUN", "checked");
						} 
					}
				}
			}
			rst.close();
			pst.close();
			
//			System.out.println("weekdaysValue ===> "+weekdaysValue);
			List<List<String>> alSessionData = new ArrayList<List<String>>();
			pst=con.prepareStatement("select frequency,frequency_date,training_frequency,start_time,end_time,schedule_type,week_days" +
					" from training_schedule join training_session using (schedule_id) where plan_id=?");
			pst.setInt(1, uF.parseToInt(stageID));
			rst=pst.executeQuery();
			
			while(rst.next()){
				
				List<String> alInner= new ArrayList<String>();
				
				alInner.add(rst.getString("training_frequency"));
				alInner.add(uF.getDateFormat(rst.getString("frequency_date"), DBDATE, CF.getStrReportDateFormat()));
				
				String startTime="";
				if(rst.getString("start_time")!=null && !rst.getString("start_time").equals("")){
					startTime=rst.getString("start_time").substring(0,5);
				}					
				alInner.add(startTime);
				
				String endTime="";
				if(rst.getString("end_time")!=null && !rst.getString("end_time").equals("")){
					endTime=rst.getString("end_time").substring(0, 5);
				}
				alInner.add(endTime); 
				alInner.add(rst.getString("schedule_type"));
				alInner.add(rst.getString("week_days"));
				
			 alSessionData.add(alInner);
			}
			rst.close();
			pst.close();
			
			request.setAttribute("scheduleTypeValue", scheduleTypeValue);
			request.setAttribute("trainingSchedulePeriod", trainingSchedulePeriod);
			request.setAttribute("trainingDataList", trainingDataList);
			request.setAttribute("alSessionData", alSessionData);
			request.setAttribute("hmWeekdays", hmWeekdays);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String getAppendData(String strIds) {
		StringBuilder sb = new StringBuilder();
		if(strIds != null && !strIds.equals("")){
			
		List<String> idsList = Arrays.asList(strIds.split(","));
			if (idsList != null && !idsList.isEmpty()) {
				
				for (int i = 0; i < idsList.size(); i++) {
					if (i == 0) {
						sb.append(","+idsList.get(i).trim()+",");
					} else {
						sb.append(idsList.get(i).trim()+",");
					}
				}
			} else {
				return null;
			}
		}
		return sb.toString();
	}
	
	//===start parvez date: 22-09-2021===
	private void getVideoData() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			List<String> videoList = new ArrayList<String>();
			pst = con.prepareStatement("select * from learning_video_details where learning_video_id = ?");
			pst.setInt(1, uF.parseToInt(stageID));
			rs = pst.executeQuery();
			while (rs.next()) {
				videoList.add(rs.getString("learning_video_id"));
				videoList.add(rs.getString("learning_video_title"));
				videoList.add("Video");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("videoList", videoList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	//===end parvez date: 22-09-2021===

	public String getStageTypeAndID() {
		return stageTypeAndID;
	}

	public void setStageTypeAndID(String stageTypeAndID) {
		this.stageTypeAndID = stageTypeAndID;
	}

	public String getStageID() {
		return stageID;
	}

	public void setStageID(String stageID) {
		this.stageID = stageID;
	}

	public String getStageType() {
		return stageType;
	}

	public void setStageType(String stageType) {
		this.stageType = stageType;
	}

	public String getLstagetype() {
		return lstagetype;
	}

	public void setLstagetype(String lstagetype) {
		this.lstagetype = lstagetype;
	}

	public String getLstagename() {
		return lstagename;
	}

	public void setLstagename(String lstagename) {
		this.lstagename = lstagename;
	}

	public String getLstagetypeid() {
		return lstagetypeid;
	}

	public void setLstagetypeid(String lstagetypeid) {
		this.lstagetypeid = lstagetypeid;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public List<String> getWeekdaysValue() {
		return weekdaysValue;
	}

	public void setWeekdaysValue(List<String> weekdaysValue) {
		this.weekdaysValue = weekdaysValue;
	}

	public String getWeekdayValue() {
		return weekdayValue;
	}

	public void setWeekdayValue(String weekdayValue) {
		this.weekdayValue = weekdayValue;
	}

	public String getDayValue() {
		return dayValue;
	}

	public void setDayValue(String dayValue) {
		this.dayValue = dayValue;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;

	}

}
