package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

public class LearningPlanPreview extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	private String ID;
	private String weekdayValue;
	private String dayValue;
    
	CommonFunctions CF = null;
	private String learningPlanId;
	private String planId;
    
	
	public String execute() {

		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
	/*	System.out.println("PlanId------------------>"+learningPlanId);
		System.out.println("PlanId------------------>"+getLearningPlanId());*/
		String strID = request.getParameter("ID");
		
		request.setAttribute(PAGE, "/jsp/training/LearningPlanPreview.jsp");
	
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
//		System.out.println("Plan ID ===> "+getPlanId());
//		System.out.println("strOperation ===> "+strOperation);
		if(getPlanId() == null) {
			setPlanId(strID);
		}
		
//		System.out.println("getStepSubmit() ===> "+getStepSubmit());
//		System.out.println("getStepSave() ===> "+getStepSave());
		
		getQuestionList(uF);
		getQuestionSubType(uF);
		getLearningPlanData(getPlanId(), uF);

//		addCalenderEvents(uF);
//		List<String> reportListEmp = new ArrayList<String>();
		getTrainingCalendar(uF);
		getLearningCalendar(uF);
		
//		request.setAttribute("reportListEmp", reportListEmp);
	
		return LOAD;
	}
	

	public void getTrainingData(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			Map<String, List<String>> hmTrainingList = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select tp.plan_id,training_title,schedule_id,start_date,end_date,day_schedule_type,training_frequency from training_schedule ts, training_plan tp where ts.plan_id = tp.plan_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				List<String> trainingList = new ArrayList<String>();
				trainingList.add(rst.getString("plan_id"));
				trainingList.add(rst.getString("training_title"));
				trainingList.add("Training");
				hmTrainingList.put(rst.getString("plan_id"), trainingList);
			}
//			System.out.println("hmTrainingList ===> "+hmTrainingList);
			request.setAttribute("hmTrainingList", hmTrainingList);
			
//			String schedule_id = null;
			
			Map<String, List<String>> hmTrainingDataList = new HashMap<String, List<String>>();
			
			pst = con.prepareStatement("select * from training_schedule join training_session using (schedule_id)");
			rst = pst.executeQuery();
			String trainingSchedulePeriod = null;
			String scheduleTypeValue = null;
			Map<String, String> hmWeekdays1 = new HashMap<String, String>();
			while (rst.next()) {
				List<String> trainingDataList = new ArrayList<String>();
				trainingDataList.add(rst.getString("plan_id"));
				trainingDataList.add(uF.getDateFormat(rst.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
				trainingDataList.add(uF.getDateFormat(rst.getString("end_date"), DBDATE, CF.getStrReportDateFormat()));
				trainingDataList.add(uF.getDateFormat(rst.getString("start_date"), DBDATE, DATE_FORMAT));
				trainingDataList.add(uF.getDateFormat(rst.getString("end_date"), DBDATE, DATE_FORMAT));
//				setNoofdaysTraining(rst.getString("training_duration"));
				trainingSchedulePeriod = rst.getString("training_frequency") != null ? rst.getString("training_frequency") : "1";
				trainingDataList.add(rst.getString("training_frequency") != null ? rst.getString("training_frequency") : "1");
//				schedule_id = rst.getString("schedule_id");
				if (trainingSchedulePeriod != null && trainingSchedulePeriod.equals("2")) {
					weekdayValue = rst.getString("training_weekday");
					dayValue = "";
				} else if (trainingSchedulePeriod != null && trainingSchedulePeriod.equals("3")) {
					dayValue = rst.getString("training_weekday");
				}
				trainingDataList.add(rst.getString("training_duration_type"));
				trainingDataList.add(rst.getString("day_schedule_type") != null ? rst.getString("day_schedule_type") : "1");
				scheduleTypeValue = rst.getString("day_schedule_type") != null ? rst.getString("day_schedule_type") : "1";
				
				List<String> weekdayList = new ArrayList<String>();
				if(rst.getString("week_days") != null) {
					weekdayList = Arrays.asList(rst.getString("week_days").split(","));
				}
				for (int i = 0; weekdayList != null && !weekdayList.isEmpty() && i < weekdayList.size(); i++) {
					if(weekdayList.get(i).trim().equals("Mon")){
						hmWeekdays1.put(rst.getString("plan_id")+"_MON", "checked");
					} else if(weekdayList.get(i).trim().equals("Tue")){
						hmWeekdays1.put(rst.getString("plan_id")+"_TUE", "checked");
					} else if(weekdayList.get(i).trim().equals("Wed")){
						hmWeekdays1.put(rst.getString("plan_id")+"_WED", "checked");
					} else if(weekdayList.get(i).trim().equals("Thu")){
						hmWeekdays1.put(rst.getString("plan_id")+"_THU", "checked");
					} else if(weekdayList.get(i).trim().equals("Fri")){
						hmWeekdays1.put(rst.getString("plan_id")+"_FRI", "checked");
					} else if(weekdayList.get(i).trim().equals("Sat")){
						hmWeekdays1.put(rst.getString("plan_id")+"_SAT", "checked");
					} else if(weekdayList.get(i).trim().equals("Sun")){
						hmWeekdays1.put(rst.getString("plan_id")+"_SUN", "checked");
					} 
				}
				
				hmTrainingDataList.put(rst.getString("plan_id"), trainingDataList);
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmTrainingDataList ===> "+hmTrainingDataList);
//			System.out.println("hmWeekdays1 ===> "+hmWeekdays1);
//			System.out.println("weekdaysValue ===> "+weekdaysValue);
			Map<String, List<List<String>>> hmSessionData = new HashMap<String, List<List<String>>>();
			List<List<String>> alSessionData = new ArrayList<List<String>>();
			pst=con.prepareStatement("select plan_id,frequency,frequency_date,training_frequency,start_time,end_time,schedule_type,week_days" +
					" from training_schedule join training_session using (schedule_id) order by session_id");
			rst=pst.executeQuery();
			
			while(rst.next()){
				alSessionData = hmSessionData.get(rst.getString("plan_id"));
				if(alSessionData == null) alSessionData = new ArrayList<List<String>>();
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
			 hmSessionData.put(rst.getString("plan_id"), alSessionData);
			 
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmTrainingDataList ===> "+hmTrainingDataList);
//			System.out.println("hmSessionData ===> "+hmSessionData);
			
			request.setAttribute("scheduleTypeValue", scheduleTypeValue);
			request.setAttribute("trainingSchedulePeriod", trainingSchedulePeriod);
			request.setAttribute("hmTrainingDataList", hmTrainingDataList);
			request.setAttribute("hmSessionData", hmSessionData);
			request.setAttribute("hmWeekdays1", hmWeekdays1);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	public void getTrainingCalendar(UtilityFunctions uF){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			
			con = db.makeConnection(con);
			getTrainingData(con, uF);
			
			pst = con.prepareStatement("select learning_plan_stage_id,learning_plan_stage_name_id from learning_plan_stage_details where learning_plan_id = ? and learning_type = 'Training' order by learning_plan_stage_id");
			pst.setInt(1, uF.parseToInt(getPlanId()));
			rst=pst.executeQuery();
			List<String> stageIdList = new ArrayList<String>();
//			System.out.println("pst ====> " + pst);
			while(rst.next()){
				stageIdList.add(rst.getString("learning_plan_stage_name_id"));
			}
			rst.close();
			pst.close();
			
			Map<String, List<String>> hmlTrainingDetails = new HashMap<String, List<String>>();
			Map<String, List<String>> hmlTrainingMonths = new HashMap<String, List<String>>();
			List<String> stageDetailList = new ArrayList<String>();
			List<String> monthList = new ArrayList<String>();
			
//			System.out.println("stageIdList ====> " + stageIdList);
			
			for (int a = 0; stageIdList != null && !stageIdList.isEmpty() && a < stageIdList.size(); a++) {
				monthList = hmlTrainingMonths.get(stageIdList.get(a));
				if(monthList == null) monthList = new ArrayList<String>();
			pst = con.prepareStatement("select training_title,schedule_id,start_date,end_date,day_schedule_type,training_frequency from training_schedule ts, training_plan tp where ts.plan_id = tp.plan_id and ts.plan_id = ?");
			pst.setInt(1, uF.parseToInt(stageIdList.get(a)));
			rst=pst.executeQuery();
			String scheduleId = null;
			String startDate = null;
			String endDate = null;
			String scheduleType = null;
			String trainingTitle = null;
			
			while(rst.next()){
				trainingTitle = rst.getString("training_title");
				scheduleId = rst.getString("schedule_id");
				startDate = rst.getString("start_date");
				endDate = rst.getString("end_date");
				scheduleType = rst.getString("day_schedule_type");
//				trainingFrequency = rst.getString("training_frequency");
			}
			rst.close();
			pst.close();
			
			request.setAttribute("startDate", startDate);
			request.setAttribute("endDate", endDate);
			
			Map<String, String> hmDayDescription = new HashMap<String, String>();
			pst = con.prepareStatement("select training_schedule_id,day_date,day_description from training_schedule_details where training_id=? and training_schedule_id = ?");
			pst.setInt(1, uF.parseToInt(stageIdList.get(a)));
			pst.setInt(2, uF.parseToInt(scheduleId));
			rst=pst.executeQuery();
			while(rst.next()){	
				String dayDate = uF.getDateFormat(rst.getString("day_date"), DBDATE, DATE_FORMAT);
				hmDayDescription.put(rst.getString("training_schedule_id")+"_"+dayDate, rst.getString("day_description"));
			}
			rst.close();
			pst.close();
			
			//System.out.println("scheduleType ===> " + scheduleType);
			String wkDays = null;
			List<String> frequencyDateList = new ArrayList<String>();
			if(scheduleType != null && uF.parseToInt(scheduleType) == 1) {
				pst = con.prepareStatement("select week_days from training_session where schedule_id = ? limit 1");
				pst.setInt(1, uF.parseToInt(scheduleId));
				rst=pst.executeQuery();
				while(rst.next()){	
					wkDays = rst.getString("week_days");
				}
				rst.close();
				pst.close();
			} else if(scheduleType != null && uF.parseToInt(scheduleType) == 2) {
				pst = con.prepareStatement("select frequency_date from training_session where schedule_id = ?");
				pst.setInt(1, uF.parseToInt(scheduleId));
				rst=pst.executeQuery();
				while(rst.next()){	
					frequencyDateList.add(rst.getString("frequency_date"));
				}
				rst.close();
				pst.close();
			}
			
			String dayCount = uF.dateDifference(startDate, DBDATE, endDate, DBDATE);
			
//			System.out.println("dayCount ===> " + dayCount);
			List<String> wkDayList = new ArrayList<String>();
			if(wkDays != null && !wkDays.equals("")) {
				wkDayList = Arrays.asList(getAppendData(wkDays).split(","));
			}
//			List<List<String>> daysList = new ArrayList<List<String>>();
			
//			System.out.println("scheduleType ===> " + scheduleType);
			
			if(scheduleType != null && uF.parseToInt(scheduleType) == 1) {
				String strNewDate = null;
				int dayCnt = 0;
//				List<String> innerList1 = new ArrayList<String>();
				
				java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
				
				 Calendar cal = Calendar.getInstance();
			        cal.setTime(dtDate);
			        int month = cal.get(Calendar.MONTH);
			        
				String strDay = new SimpleDateFormat("E").format(dtDate);
//		        System.out.println("Day is E ::::::: " + new SimpleDateFormat("E").format(dtDate));
//		        System.out.println("wkDayList ===> " + wkDayList);
		        if(wkDayList != null && wkDayList.contains(strDay)){
		        	if(!monthList.contains(""+month)){
		        		monthList.add(""+month);
		        	}
		        	dayCnt++;
		        	String dayDesId = getDayDescriptionId(con, stageIdList.get(a), scheduleId, startDate, uF);
		        	stageDetailList.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(startDate, DBDATE, "dd")+")}");
		        } else if(wkDayList == null) {
		        	if(!monthList.contains(""+month)){
		        		monthList.add(""+month);
		        	}
		        	dayCnt++;
		        	String dayDesId = getDayDescriptionId(con, stageIdList.get(a), scheduleId, startDate, uF);
		        	stageDetailList.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(startDate, DBDATE, "dd")+")}");
		        }
//				daysList.add(uF.getDateFormat(startDate, DBDATE, CF.getStrReportDateFormat()));
				for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
					if(strNewDate == null)
					strNewDate = startDate;
					Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
					java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
					
			        cal.setTime(dtDate1);
			        int month1 = cal.get(Calendar.MONTH);
			        
					String strDay1 = new SimpleDateFormat("E").format(dtDate1);
					//System.out.println("newDate ====> " + newDate);
					 if(wkDayList != null && wkDayList.contains(strDay1)){
						 if(!monthList.contains(""+month1)){
				        		monthList.add(""+month1);
				        	}
						 	dayCnt++;
						 	String dayDesId = getDayDescriptionId(con, stageIdList.get(a), scheduleId, ""+newDate, uF);
						 	stageDetailList.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
				        }else if(wkDayList == null) {
				        	 if(!monthList.contains(""+month1)){
					        		monthList.add(""+month1);
					        	}
				        	 	dayCnt++;
						        String dayDesId = getDayDescriptionId(con, stageIdList.get(a), scheduleId, ""+newDate, uF);
						        stageDetailList.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
				        }
					strNewDate = newDate+"";
				}
			} else if(scheduleType != null && uF.parseToInt(scheduleType) == 2) {
				Calendar cal = Calendar.getInstance();
				
				Collections.sort(frequencyDateList);
				for(int i=0; frequencyDateList != null && !frequencyDateList.isEmpty() && i< frequencyDateList.size(); i++) {
					java.util.Date dtDate1 = uF.getDateFormatUtil(frequencyDateList.get(i), DBDATE);
					cal.setTime(dtDate1);
			        int month1 = cal.get(Calendar.MONTH);
			        if(!monthList.contains(""+month1)){
		        		monthList.add(""+month1);
		        	}
			        Date dtDate11 = uF.getDateFormat(frequencyDateList.get(i), DBDATE);
			        String dayDesId = getDayDescriptionId(con, stageIdList.get(a), scheduleId, ""+dtDate11, uF);
			        
			        stageDetailList.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+(i+1)+");',color:'#9D9C9C',title: 'Day "+(i+1)+"',start: new Date("+uF.getDateFormat(frequencyDateList.get(i), DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(frequencyDateList.get(i), DBDATE, "M"))-1)+", "+uF.getDateFormat(frequencyDateList.get(i), DBDATE, "dd")+")}");
				}
			}
			
			
			hmlTrainingDetails.put(stageIdList.get(a), stageDetailList);
			request.setAttribute("hmlTrainingDetails", hmlTrainingDetails);
//			System.out.println("hmlTrainingDetails ====> " + hmlTrainingDetails);
			
			Collections.sort(monthList);
			hmlTrainingMonths.put(stageIdList.get(a), monthList);
			request.setAttribute("hmlTrainingMonths", hmlTrainingMonths);
//			System.out.println("hmlTrainingMonths ====> " + hmlTrainingMonths);
			
		}
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private String getDayDescriptionId(Connection con, String trainingId, String scheduleId, String dtDate1, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		String dayDesId = null;
		try {
			pst = con.prepareStatement("select training_schedule_details_id from training_schedule_details where training_id = ? and training_schedule_id = ? and day_date = ?");
			pst.setInt(1, uF.parseToInt(trainingId));
			pst.setInt(2, uF.parseToInt(scheduleId));
			pst.setDate(3, uF.getDateFormat(dtDate1, DBDATE));
			rst=pst.executeQuery();
			while(rst.next()){
				dayDesId = rst.getString("training_schedule_details_id");
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dayDesId;
	}
	
	
	public void getLearningCalendar(UtilityFunctions uF){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			
			con = db.makeConnection(con);
			
			List<List<String>> lStageDetailsList = new ArrayList<List<String>>();
			pst = con.prepareStatement("select learning_plan_stage_id,from_date,to_date,weekdays from learning_plan_stage_details where learning_plan_id = ? order by learning_plan_stage_id");
			pst.setInt(1, uF.parseToInt(getPlanId()));
			rst=pst.executeQuery();
			while(rst.next()){
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("learning_plan_stage_id"));
				innerList.add(rst.getString("from_date"));
				innerList.add(rst.getString("to_date"));
				innerList.add(rst.getString("weekdays"));
				lStageDetailsList.add(innerList);
			}
			rst.close();
			pst.close();

			Map<String, List<String>> hmlPlanStageDetails = new HashMap<String, List<String>>();
			Map<String, List<String>> hmlPlanStageMonths = new HashMap<String, List<String>>();
			List<String> stageDetailList = new ArrayList<String>();
			List<String> monthList = new ArrayList<String>();
			
			for (int a = 0; lStageDetailsList != null && !lStageDetailsList.isEmpty() && a < lStageDetailsList.size(); a++) {
				List<String> innerList = lStageDetailsList.get(a);
				String startDate = innerList.get(1);
				String endDate = innerList.get(2);
				String wkDays = innerList.get(3);
			stageDetailList = hmlPlanStageDetails.get(innerList.get(0));
			if(stageDetailList == null) stageDetailList = new ArrayList<String>();
			
			monthList = hmlPlanStageMonths.get(innerList.get(0));
			if(monthList == null) monthList = new ArrayList<String>();
			
			String dayCount = uF.dateDifference(startDate, DBDATE, endDate, DBDATE);
			
			List<String> wkDayList = new ArrayList<String>();
			if(wkDays != null && !wkDays.equals("")) {
				wkDayList = Arrays.asList(getAppendData(wkDays).split(","));
			}
			
				String strNewDate = null;
				int dayCnt = 0;
				int month = 0;
				String strDay = "";
				Calendar cal = Calendar.getInstance();
				if(startDate!=null && !startDate.trim().equals("")){
					java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
				    cal.setTime(dtDate);
				    month = cal.get(Calendar.MONTH);
					strDay = new SimpleDateFormat("E").format(dtDate);
				}
		        if(wkDayList != null && wkDayList.contains(strDay)){
		        	if(!monthList.contains(""+month)){
		        		monthList.add(""+month);
		        	}
		        	dayCnt++;
		        	stageDetailList.add("{url:'#',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(startDate, DBDATE, "dd")+")}");
		        } else if(wkDayList == null) {
		        	if(!monthList.contains(""+month)){
		        		monthList.add(""+month);
		        	}
		        	dayCnt++;
		        	stageDetailList.add("{url:'#',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(startDate, DBDATE, "dd")+")}");
		        }
				for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
					if(strNewDate == null)
					strNewDate = startDate;
					Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
					java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
					
			        cal.setTime(dtDate1);
			        int month1 = cal.get(Calendar.MONTH);
			        
					String strDay1 = new SimpleDateFormat("E").format(dtDate1);
					 if(wkDayList != null && wkDayList.contains(strDay1)){
						 if(!monthList.contains(""+month1)){
				        		monthList.add(""+month1);
				        	}
						 	dayCnt++;
						 	stageDetailList.add("{url:'#',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
				        }else if(wkDayList == null) {
				        	 if(!monthList.contains(""+month1)){
					        		monthList.add(""+month1);
					        	}
				        	 	dayCnt++;
				        	 	stageDetailList.add("{url:'#',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
				        }
					strNewDate = newDate+"";
				}
				
				hmlPlanStageDetails.put(innerList.get(0), stageDetailList);
				request.setAttribute("hmlPlanStageDetails", hmlPlanStageDetails);
				
				Collections.sort(monthList);
				hmlPlanStageMonths.put(innerList.get(0), monthList);
				request.setAttribute("hmlPlanStageMonths", hmlPlanStageMonths);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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

	/*private void addCalenderEvents(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		List<String> lPlanStageCalender = new ArrayList<String>();
		
		try {
			con = db.makeConnection(con);
			
			PreparedStatement pst = con.prepareStatement("select training_title,plan_id ,start_date," +
					"end_date,training_frequency,frequency_day,frequency_date  from training_schedule " +
					"join training_session using (schedule_id) join training_plan using(plan_id) where trainer_ids like '%,"+getEmpId()+",%'");
//			System.out.println("printing pst===="+pst);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				lPlanStageCalender.add("{url:'#',color:'#009988',title: '"+"Training : "+rs.getString("training_title")+"("+rs.getString("training_frequency")+")',start: new Date("+uF.getDateFormat(rs.getString("frequency_date"), DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(rs.getString("frequency_date"), DBDATE, "M"))-1)+", "+uF.getDateFormat(rs.getString("frequency_date"), DBDATE, "dd")+")}");
			}
//			   System.out.println("printing alTrainerCalender===="+alTrainerCalender);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		request.setAttribute("lPlanStageCalender", lPlanStageCalender);
	}*/
	
	private void getQuestionSubType(UtilityFunctions uF) {
		Map<String, List<List<String>>> answertypeSub = new HashMap<String, List<List<String>>>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);

		try {
			pst = con.prepareStatement("select * from appraisal_answer_type_sub order by appraisal_answer_type_sub_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				List<List<String>> outerList = answertypeSub.get(rst.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();

				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("score"));
				innerList.add(rst.getString("score_label"));
				outerList.add(innerList);
				answertypeSub.put(rst.getString("answer_type_id"), outerList);
			}
			rst.close();
			pst.close();

			request.setAttribute("answertypeSub", answertypeSub);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getQuestionList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		// Map<String, String> AppraisalQuestion = new HashMap<String,
		// String>();
		Map<String, List<String>> hmQuestion = new HashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from learning_plan_question_bank where learning_plan_id = ?");
			pst.setInt(1, uF.parseToInt(getPlanId()));
			rs = pst.executeQuery();
			List<List<String>> feedbackQueList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("learning_plan_question_bank_id"));
				innerList.add(rs.getString("learning_plan_question_text"));
				innerList.add(rs.getString("option_a"));
				innerList.add(rs.getString("option_b"));
				innerList.add(rs.getString("option_c"));
				innerList.add(rs.getString("option_d"));
				innerList.add(rs.getString("correct_ans"));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("answer_type"));
				feedbackQueList.add(innerList);
				hmQuestion.put(rs.getString("learning_plan_question_bank_id"), innerList);
			}
			rs.close();
			pst.close();
//			System.out.println("hmQuestion ====> "+hmQuestion);
			request.setAttribute("hmQuestion", hmQuestion);
			request.setAttribute("feedbackQueList", feedbackQueList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}


	private void getLearningPlanData(String strID, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		
		try {
				con = db.makeConnection(con);
				Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
				Map<String, String> hmSkillName = CF.getSkillNameMap(con);
				Map<String, String> hmAttribute = new HashMap<String, String>();
				pst = con.prepareStatement("select arribute_id,attribute_name from appraisal_attribute");
				rst = pst.executeQuery();
				while (rst.next()) {
					hmAttribute.put(rst.getString("arribute_id"), rst.getString("attribute_name"));
				}
				rst.close();
				pst.close();
		
				pst = con.prepareStatement("select min(from_date) as minDate, max(to_date) as maxDate from learning_plan_stage_details where learning_plan_id = ?");
				pst.setInt(1, uF.parseToInt(strID));
				rst = pst.executeQuery();
				String minFromDate = null, maxToDate = null; 
				while (rst.next()) {
					minFromDate = rst.getString("minDate");
					maxToDate = rst.getString("maxDate");
				}
				rst.close();
				pst.close();
				
				List<String> learningPlanList = new ArrayList<String>();
				pst = con.prepareStatement("select * from learning_plan_details where learning_plan_id = ?");
				pst.setInt(1, uF.parseToInt(getPlanId()));
				rst = pst.executeQuery();
				while (rst.next()) {
					String alignedWith = "";
					if(rst.getString("group_or_condition") != null && rst.getString("group_or_condition").equals("3")) {
						alignedWith = "General";
					} else if(rst.getString("group_or_condition") != null && rst.getString("group_or_condition").equals("2")) {
						alignedWith = "Gap";
					} else if(rst.getString("group_or_condition") != null && rst.getString("group_or_condition").equals("1")) {
						alignedWith = "Induction";
					} 
					learningPlanList.add(rst.getString("learning_plan_id"));
					learningPlanList.add(rst.getString("learning_plan_name"));
					learningPlanList.add(rst.getString("learning_plan_objective"));
					learningPlanList.add(alignedWith);
					learningPlanList.add(uF.showData(getAppendData(rst.getString("learner_ids"), hmEmpName),"")); //asignee
					learningPlanList.add(uF.showData(getAppendData(rst.getString("attribute_id"), hmAttribute),"")); //Associated With Attribute
					learningPlanList.add(uF.showData(CF.getCertificateName(con, rst.getString("certificate_id")), ""));
					
					learningPlanList.add(uF.showData(getAppendData(rst.getString("skills"), hmSkillName), ""));
					learningPlanList.add(uF.getDateFormat(minFromDate, DBDATE, CF.getStrReportDateFormat()));
					learningPlanList.add(uF.getDateFormat(maxToDate, DBDATE, CF.getStrReportDateFormat()));
					learningPlanList.add(getLearningStageType(con, uF, uF.parseToInt(strID)));
				}
				rst.close();
				pst.close();
				
				Map<String, String> hmWeekdays = new HashMap<String, String>();
				List<List<String>> stageList = new ArrayList<List<String>>();
				pst = con.prepareStatement("select * from learning_plan_stage_details where learning_plan_id = ?");
				pst.setInt(1, uF.parseToInt(strID));
				rst = pst.executeQuery();
				while (rst.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rst.getString("learning_plan_stage_id"));
					innerList.add(rst.getString("learning_plan_stage_name_id"));
					innerList.add(rst.getString("learning_plan_stage_name"));
					innerList.add(rst.getString("learning_type"));
					innerList.add(uF.getDateFormat(rst.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
					innerList.add(uF.getDateFormat(rst.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
//					innerList.add(rst.getString("from_time"));
					innerList.add(uF.getTimeFormatStr(rst.getString("from_time"), DBTIME, TIME_FORMAT));
					innerList.add(uF.getTimeFormatStr(rst.getString("to_time"), DBTIME, TIME_FORMAT));
					innerList.add(rst.getString("weekdays"));
					stageList.add(innerList);
					
					List<String> weekdayList = Arrays.asList(rst.getString("weekdays").split(","));
					for (int i = 0; weekdayList != null && !weekdayList.isEmpty() && i < weekdayList.size(); i++) {
						if(weekdayList.get(i).trim().equals("Mon")){
							hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_MON", "checked");
						} else if(weekdayList.get(i).trim().equals("Tue")){
							hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_TUE", "checked");
						} else if(weekdayList.get(i).trim().equals("Wed")){
							hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_WED", "checked");
						} else if(weekdayList.get(i).trim().equals("Thu")){
							hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_THU", "checked");
						} else if(weekdayList.get(i).trim().equals("Fri")){
							hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_FRI", "checked");
						} else if(weekdayList.get(i).trim().equals("Sat")){
							hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_SAT", "checked");
						} else if(weekdayList.get(i).trim().equals("Sun")){
							hmWeekdays.put(rst.getString("learning_plan_stage_id")+"_SUN", "checked");
						} 
					}
				}
				rst.close();
				pst.close();
//				System.out.println("Exist Stage IDs "+getExiststageIDs());
//				System.out.println("stageList ====== ===> " + stageList);
				request.setAttribute("stageList", stageList);
				request.setAttribute("hmWeekdays", hmWeekdays);
				request.setAttribute("learningPlanList", learningPlanList);
				
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	
private String getLearningStageType(Connection con, UtilityFunctions uF, int learningPlanId) {
		
		PreparedStatement pst = null;
		ResultSet rst=null;
		StringBuilder learningType = new StringBuilder();
		try {
			Map<String,String> hmwlocation = CF.getWLocationMap(con, null, null);
			
			pst = con.prepareStatement("select * from learning_plan_stage_details where learning_plan_id = ?");
			pst.setInt(1, learningPlanId);
			rst = pst.executeQuery();
			 List<String> learningTypeList = new ArrayList<String>();
			while(rst.next()) {
				learningTypeList.add(rst.getString("learning_type"));
			}
			rst.close();
			pst.close();
			
			int a=0,b=0,c=0;
			for (int i = 0; learningTypeList != null && !learningTypeList.isEmpty() && i < learningTypeList.size(); i++) {
				if(learningTypeList.get(i).equals("Training") && a == 0){
					a++;
				} else if(learningTypeList.get(i).equals("Course") && b == 0){
					b++;
				}  else if(learningTypeList.get(i).equals("Assessment") && c == 0){
					c++;
				} 
			}
				if(a == 1 && b == 0 && c == 0) {
					learningType.append("Training");
				} else if(a == 0 && b == 1 && c == 0) {
					learningType.append("Course");
				} else if(a == 0 && b == 0 && c == 1) {
					learningType.append("Assessment");
				} else if((a == 1 && b == 1 && c == 1) || (a == 1 && b == 1 && c == 0) || (a == 1 && b == 0 && c == 1) || (a == 0 && b == 1 && c == 1)) {
					learningType.append("Hybrid");
				}
				
		}catch(Exception e){
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
		return learningType.toString();
	}

	private String getAppendData(String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();

		if (strID != null && !strID.equals("") && !strID.isEmpty() && strID.length()>1) {
			strID = strID.substring(1, strID.length()-1);
			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()));
					} else {
						sb.append(", " + mp.get(temp[i].trim()));
					}
				}
			} else {
				return mp.get(strID);
			}

		} else {
			return null;
		}

		return sb.toString();
	}
		
		
//	public String getAppendData(String strIds) {
//		
//		StringBuilder sb = new StringBuilder();
//		if(strIds != null && !strIds.equals("")) {
//			
//			List<String> idsList = Arrays.asList(strIds.split(","));
//			if (idsList != null && !idsList.isEmpty()) {
//				
//				for (int i = 0; i < idsList.size(); i++) {
//					if (i == 0) {
//						sb.append("," + idsList.get(i).trim() + ",");
//					} else {
//						sb.append(idsList.get(i).trim() + ",");
//					}
//				}
//			} else {
//				return null;
//			}
//		}
//		return sb.toString();
//	}
	
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;

	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	private String step;

	// form parameters **********

	public String getStrEmployee() {
		return strEmployee;
	}

	public void setStrEmployee(String strEmployee) {
		this.strEmployee = strEmployee;
	}

	String strEmployee;


	// first step variables **********

	
	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
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


	public String getLearningPlanId() {
		return learningPlanId;
	}


	public void setLearningPlanId(String learningPlanId) {
		this.learningPlanId = learningPlanId;
	}

}
