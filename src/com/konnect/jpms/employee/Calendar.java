package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Calendar extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId; 
	String strUserType = null;
	CommonFunctions CF = null;
	String alertStatus;
	String alert_type;
	boolean strBirthdays;
	boolean strTrainings;
	boolean strInterviewsPending;
	boolean strInterviewsApproved;
	boolean strInterviewsDenied;
	boolean strEvents;
	boolean strVisits;
	String strFrm;
	private String dataType;
	private String fromPage;
	private String strAction = null;
	String strBaseUserType = null;
	
	public String execute() throws Exception {
 
		session = request.getSession();
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);//Created By Dattatray 15-06-2022
		if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		request.setAttribute(PAGE, "/jsp/employee/Calendar.jsp");
		request.setAttribute(TITLE, "My Calendar"); 
		//Created By Dattatray 14-06-2022
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}
	
		UtilityFunctions uF = new UtilityFunctions();
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-calendar\"></i><a href=\"Calendar.action\" style=\"color: #3c8dbc;\"> Calendar</a></li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		if(strUserType != null && strUserType.equals(EMPLOYEE) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(ADD_MY_INTERVIEWS_SCHEDULED_ALERT)){
			updateUserAlerts();
		}
//		System.out.println("fromPage1==>"+fromPage+"=>dataType==>"+dataType);
		if(getDataType() == null || getDataType().equals("")) {
			setDataType("C");
		}
//		System.out.println("strSessionEmpId ===>> " + strSessionEmpId);
		
		List<String> reportListEmp = new ArrayList<String>();
		if(getFromPage() != null) {
			getLeaves(uF, reportListEmp);
			getHolidays(uF, reportListEmp);
			getTravels(uF, reportListEmp);
		}
		
//		System.out.println("getStrBirthdays() ===>> " + getStrBirthdays());
//		System.out.println("getStrVisits() ===>> " + getStrVisits());
		if(getStrBirthdays()) {
			getBirthdays(uF, reportListEmp);
		}
		
		if(getStrInterviewsPending() || getStrInterviewsApproved() || getStrInterviewsDenied()){
			getInterviewCalendar(uF, reportListEmp);
		}
		if(getStrTrainings()) {
			getTrainingCalendar(uF, reportListEmp);               
			getLearningCalendar(uF, reportListEmp);
		}
		
		if(getStrEvents()) {
			getEvents(uF, reportListEmp);
		}
		if(getStrVisits()){
			getVisits(uF,reportListEmp);
		}
		
		if(getFromPage() == null) {
			
			setStrBirthdays(true);
			setStrTrainings(true);
			setStrInterviewsPending(true);
			setStrEvents(true);
			setStrVisits(true);
//			setStrInterviewsApproved(true);
//			setStrInterviewsDenied(true);
			
			getInterviewCalendar(uF, reportListEmp);
			getTrainingCalendar(uF, reportListEmp);
			getLearningCalendar(uF, reportListEmp);
			getBirthdays(uF, reportListEmp);
			getLeaves(uF, reportListEmp);
			getTravels(uF, reportListEmp);
			getHolidays(uF, reportListEmp);
			getEvents(uF,reportListEmp);
			getVisits(uF,reportListEmp);
			
		}

		request.setAttribute("reportListEmp", reportListEmp);
		System.out.println("fromPage==>"+fromPage);
		if(getFromPage() != null && getFromPage().equalsIgnoreCase("ajax")) {
//			return LOAD;
			return SUCCESS;
		}
		loadPageVisitAuditTrail();//Created By Dattatray 14-06-2022
		return SUCCESS;
	}

	//Created By Dattatray 15-06-2022
	private void loadPageVisitAuditTrail() {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,strSessionEmpId);
			StringBuilder builder = new StringBuilder();
			builder.append(hmEmpProfile.get(strSessionEmpId) +" accessed "+strAction);
			
			CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			db.closeConnection(con);
		}
	}

	
	private void getHolidays(UtilityFunctions uF, List<String> reportListEmp) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			String[] strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
			String strCalendarYearStart = strCalendarYearDates[0];
			String strCalendarYearEnd = strCalendarYearDates[1];

			con = db.makeConnection(con);
			
			List<Map<String,String>> alHolidayList = new ArrayList<Map<String,String>>();
			List<Map<String,String>> alOptHolidayList = new ArrayList<Map<String,String>>();
			
			// Started By dattatray date:09-12-21
			pst = con.prepareStatement("SELECT * FROM holidays where  wlocation_id in (select wlocation_id " +
					"from employee_official_details where emp_id=?) and org_id in(select org_id from employee_official_details where emp_id=?) order by _date");
//			pst = con.prepareStatement("SELECT * FROM holidays where _date between ? and ? and wlocation_id in (select wlocation_id " +
//					"from employee_official_details where emp_id=?) and org_id in(select org_id from employee_official_details where emp_id=?) order by _date");
//			pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));		
//			pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			// Ended By dattatray date:09-12-21
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				String strDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
				String strHolidayName = uF.showData(rs.getString("description"), "");
				String strHolidayColor = uF.showData(rs.getString("colour_code"), "");
				String strDBDate = uF.getDateFormat(rs.getString("_date"), DBDATE, "yyyy");//Created By Dattatray Date:10-12-21
				int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);//Created By Dattatray Date:10-12-21
//				System.out.println("strDBDate : "+strDBDate);
//				System.out.println("year : "+year);
				
				if(uF.parseToBoolean(rs.getString("is_optional_holiday"))){
					reportListEmp.add("{color:'"+strHolidayColor+"',title: '"+strHolidayName.replace("'", "")+"'," +
							"start: new Date("+uF.getDateFormat(strDate, DATE_FORMAT, "yyyy")+"" +
							", "+(uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "M"))-1)+"" +
							", "+uF.getDateFormat(strDate, DATE_FORMAT, "dd")+")}");
					if(year == uF.parseToInt(strDBDate)) {//Created By Dattatray Date:03-01-22
						Map<String,String> hmInner = new HashMap<String, String>();
						hmInner.put("HOLIDAY_NAME", strHolidayName);
						hmInner.put("HOLIDAY_COLOR", strHolidayColor);
					
						alOptHolidayList.add(hmInner);
					}
					
				} else {
					reportListEmp.add("{color:'"+strHolidayColor+"',title: '"+strHolidayName.replace("'", "")+"'," +
							"start: new Date("+uF.getDateFormat(strDate, DATE_FORMAT, "yyyy")+"" +
							", "+(uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "M"))-1)+"" +
							", "+uF.getDateFormat(strDate, DATE_FORMAT, "dd")+")}");
					
					if(year == uF.parseToInt(strDBDate)) {//Created By Dattatray Date:10-12-21
						Map<String,String> hmInner = new HashMap<String, String>();
						hmInner.put("HOLIDAY_NAME", strHolidayName);
						hmInner.put("HOLIDAY_COLOR", strHolidayColor);
						
						alHolidayList.add(hmInner);
					}
					
				}
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("alHolidayList", alHolidayList);
			request.setAttribute("alOptHolidayList", alOptHolidayList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getTravels(UtilityFunctions uF, List<String> reportListEmp) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from travel_application_register tar, emp_leave_entry ele where tar.travel_id=ele.leave_id and ele.emp_id=?" +
					"and ele.leave_type_id=-1 and istravel=true order by tar._date");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs=pst.executeQuery();
			Map<String, String> hmTravelDate = new LinkedHashMap<String, String>();
			while(rs.next()){
				hmTravelDate.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("plan_name"));
			}
			rs.close();
			pst.close();

			Iterator<String> it = hmTravelDate.keySet().iterator();
			while(it.hasNext()){
				String strDate = it.next();
				String strTravelPlan = hmTravelDate.get(strDate);
				
				reportListEmp.add("{color:'green',title: 'Travel-\\n"+uF.showData(strTravelPlan, "")+"'," +
						"start: new Date("+uF.getDateFormat(strDate, DATE_FORMAT, "yyyy")+"" +
						", "+(uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "M"))-1)+"" +
						", "+uF.getDateFormat(strDate, DATE_FORMAT, "dd")+")}");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getLeaves(UtilityFunctions uF, List<String> reportListEmp) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmLeaveTypeMap = CF.getLeaveTypeMap(con);
			if(hmLeaveTypeMap == null) hmLeaveTypeMap = new HashMap<String, String>();
			Map<String, String> hmLeavesColour = new HashMap<String, String>();
			CF.getLeavesColour(con, hmLeavesColour);
			
			pst = con.prepareStatement("select * from leave_application_register lar, emp_leave_entry ele where lar.leave_id=ele.leave_id and ele.emp_id=? " +
					"and lar.is_modify=false and lar.leave_type_id >0 and ele.leave_type_id>0 order by lar._date");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs=pst.executeQuery();
			Map<String, String> hmLeaveDate = new LinkedHashMap<String, String>();
			while(rs.next()){
				hmLeaveDate.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_type_id"));
			}
			rs.close();
			pst.close();

			Iterator<String> it = hmLeaveDate.keySet().iterator();
			Map<String, String> hmLeaveColor = new HashMap<String, String>();
			while(it.hasNext()){
				String strDate = it.next();
				String strLeaveTypeId = hmLeaveDate.get(strDate);
				String strLeaveTypeColor = uF.showData(hmLeavesColour.get(strLeaveTypeId), "");
				
				reportListEmp.add("{color:'"+strLeaveTypeColor+"',title: '"+uF.showData(hmLeaveTypeMap.get(strLeaveTypeId), "")+"'," +
						"start: new Date("+uF.getDateFormat(strDate, DATE_FORMAT, "yyyy")+"" +
						", "+(uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "M"))-1)+"" +
						", "+uF.getDateFormat(strDate, DATE_FORMAT, "dd")+")}");
				
				hmLeaveColor.put(hmLeaveTypeMap.get(strLeaveTypeId), strLeaveTypeColor);
			}
			
			request.setAttribute("hmLeaveColor", hmLeaveColor);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void updateUserAlerts() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		try {
			con = db.makeConnection(con);
			
			String strDomain = request.getServerName().split("\\.")[0];
			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setStrEmpId(""+nEmpId);
			userAlerts.set_type(ADD_MY_INTERVIEWS_SCHEDULED_ALERT);
			userAlerts.setStatus(UPDATE_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getTrainingCalendar(UtilityFunctions uF, List<String> reportListEmp) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			
			con = db.makeConnection(con);
//			getTrainingData(con, uF);
			

			pst = con.prepareStatement("select learning_plan_id from learning_plan_details where learner_ids like '%,"+strSessionEmpId+",%' ");
			rst=pst.executeQuery();
			List<String> lPlanIdList = new ArrayList<String>();
//			System.out.println("pst ====> " + pst);
			while(rst.next()){
				lPlanIdList.add(rst.getString("learning_plan_id"));
			}
			rst.close();
			pst.close();
			
			for (int b = 0; lPlanIdList != null && !lPlanIdList.isEmpty() && b < lPlanIdList.size(); b++) {
					pst = con.prepareStatement("select learning_plan_stage_id,learning_plan_stage_name_id from learning_plan_stage_details where learning_plan_id = ? and learning_type = 'Training' order by learning_plan_stage_id");
					pst.setInt(1, uF.parseToInt(lPlanIdList.get(b)));
					rst=pst.executeQuery();
					List<String> stageIdList = new ArrayList<String>();
//					System.out.println("pst ====> " + pst);
					while(rst.next()){
						stageIdList.add(rst.getString("learning_plan_stage_name_id"));
					}
					rst.close();
					pst.close();
					
//					Map<String, List<String>> hmlTrainingDetails = new HashMap<String, List<String>>();
//					Map<String, List<String>> hmlTrainingMonths = new HashMap<String, List<String>>();
//					List<String> stageDetailList = new ArrayList<String>();
//					List<String> monthList = new ArrayList<String>();
//					System.out.println("stageIdList ====> " + stageIdList);
					for (int a = 0; stageIdList != null && !stageIdList.isEmpty() && a < stageIdList.size(); a++) {
//						monthList = hmlTrainingMonths.get(stageIdList.get(a));
//						if(monthList == null) monthList = new ArrayList<String>();
					pst = con.prepareStatement("select training_title,schedule_id,start_date,end_date,day_schedule_type,training_frequency from training_schedule ts, training_plan tp where ts.plan_id = tp.plan_id and ts.plan_id = ?");
					pst.setInt(1, uF.parseToInt(stageIdList.get(a)));
					rst=pst.executeQuery();
					String scheduleId = null;
					String startDate = null;
					String endDate = null;
					String scheduleType = null;
					String trainingTitle = null;
					
					while(rst.next()) {
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
					while(rst.next()) {	
						String dayDate = uF.getDateFormat(rst.getString("day_date"), DBDATE, DATE_FORMAT);
						hmDayDescription.put(rst.getString("training_schedule_id")+"_"+dayDate, rst.getString("day_description"));
					}
					rst.close();
					pst.close();
					
//					System.out.println("scheduleType ===> " + scheduleType);
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
					
//					System.out.println("dayCount ===> " + dayCount);
					List<String> wkDayList = new ArrayList<String>();
					if(wkDays != null && !wkDays.equals("")) {
						wkDayList = Arrays.asList(getAppendData(wkDays).split(","));
					}
		//			List<List<String>> daysList = new ArrayList<List<String>>();
					
//					System.out.println("scheduleType ===> " + scheduleType);
//					System.out.println("wkDayList ===> " + wkDayList);
					
					if(scheduleType != null && uF.parseToInt(scheduleType) == 1) {
						String strNewDate = null;
						int dayCnt = 0;
		//				List<String> innerList1 = new ArrayList<String>();
						
						java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
						
						 java.util.Calendar cal = java.util.Calendar.getInstance();
					        cal.setTime(dtDate);
					        int month = cal.get(java.util.Calendar.MONTH);
					        
						String strDay = new SimpleDateFormat("E").format(dtDate);
		//		        System.out.println("Day is E ::::::: " + new SimpleDateFormat("E").format(dtDate));
		//		        System.out.println("wkDayList ===> " + wkDayList);
				        if(wkDayList != null && wkDayList.contains(strDay)) {
//				        	if(!monthList.contains(""+month)){
//				        		monthList.add(""+month);
//				        	}
				        	dayCnt++;
				        	String dayDesId = getDayDescriptionId(con, stageIdList.get(a), scheduleId, startDate, uF);
//				        	System.out.println("dayDesId ===>> " + dayDesId);
				        	reportListEmp.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: '"+trainingTitle+" Day "+dayCnt+"',start: new Date("+uF.getDateFormat(startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(startDate, DBDATE, "dd")+")}");
				        } else if(wkDayList == null) {
//				        	if(!monthList.contains(""+month)){
//				        		monthList.add(""+month);
//				        	}
				        	dayCnt++;
				        	String dayDesId = getDayDescriptionId(con, stageIdList.get(a), scheduleId, startDate, uF);
//				        	System.out.println("dayDesId else ===>> " + dayDesId);
				        	reportListEmp.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: '"+trainingTitle+" Day "+dayCnt+"',start: new Date("+uF.getDateFormat(startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(startDate, DBDATE, "dd")+")}");
				        }
		//				daysList.add(uF.getDateFormat(startDate, DBDATE, CF.getStrReportDateFormat()));
						for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
							if(strNewDate == null)
							strNewDate = startDate;
							Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
							java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
							
					        cal.setTime(dtDate1);
					        int month1 = cal.get(java.util.Calendar.MONTH);
					        
							String strDay1 = new SimpleDateFormat("E").format(dtDate1);
							//System.out.println("newDate ====> " + newDate);
							 if(wkDayList != null && wkDayList.contains(strDay1)){
//								 if(!monthList.contains(""+month1)){
//						        		monthList.add(""+month1);
//						        	}
								 	dayCnt++;
								 	String dayDesId = getDayDescriptionId(con, stageIdList.get(a), scheduleId, ""+newDate, uF);
								 	reportListEmp.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: '"+trainingTitle+" Day "+dayCnt+"',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
						        }else if(wkDayList == null) {
//						        	 if(!monthList.contains(""+month1)){
//							        		monthList.add(""+month1);
//							        	}
						        	 	dayCnt++;
								        String dayDesId = getDayDescriptionId(con, stageIdList.get(a), scheduleId, ""+newDate, uF);
								        reportListEmp.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: '"+trainingTitle+" Day "+dayCnt+"',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
						        }
							strNewDate = newDate+"";
						}
					} else if(scheduleType != null && uF.parseToInt(scheduleType) == 2) {
						java.util.Calendar cal = java.util.Calendar.getInstance();
						
						Collections.sort(frequencyDateList);
						for(int i=0; frequencyDateList != null && !frequencyDateList.isEmpty() && i< frequencyDateList.size(); i++) {
							if(frequencyDateList.get(i) != null && !frequencyDateList.get(i).equals("")) {
								java.util.Date dtDate1 = uF.getDateFormatUtil(frequencyDateList.get(i), DBDATE);
								cal.setTime(dtDate1);
	//					        int month1 = cal.get(java.util.Calendar.MONTH);
	//					        if(!monthList.contains(""+month1)){
	//				        		monthList.add(""+month1);
	//				        	}
						        Date dtDate11 = uF.getDateFormat(frequencyDateList.get(i), DBDATE);
						        String dayDesId = getDayDescriptionId(con, stageIdList.get(a), scheduleId, ""+dtDate11, uF);
						        
						        reportListEmp.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+(i+1)+");',color:'#9D9C9C',title: '"+trainingTitle+" Day "+(i+1)+"',start: new Date("+uF.getDateFormat(frequencyDateList.get(i), DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(frequencyDateList.get(i), DBDATE, "M"))-1)+", "+uF.getDateFormat(frequencyDateList.get(i), DBDATE, "dd")+")}");
							}
						}
					}
					
					
//					hmlTrainingDetails.put(stageIdList.get(a), reportListEmp);
//					request.setAttribute("hmlTrainingDetails", hmlTrainingDetails);
//					System.out.println("hmlTrainingDetails ====> " + hmlTrainingDetails);
					
//					Collections.sort(monthList);
//					hmlTrainingMonths.put(stageIdList.get(a), monthList);
//					request.setAttribute("hmlTrainingMonths", hmlTrainingMonths);
//					System.out.println("hmlTrainingMonths ====> " + hmlTrainingMonths);
					
				}
//					System.out.println("reportListEmp ====> " + reportListEmp);
			}
//				System.out.println("reportListEmp out ====> " + reportListEmp);
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
//			System.out.println("pst ===>> " + pst);
			
			while(rst.next()) {
				dayDesId = rst.getString("training_schedule_details_id");
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
		return dayDesId;
	}
	
	
	public void getLearningCalendar(UtilityFunctions uF, List<String> reportListEmp){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select learning_plan_id from learning_plan_details where learner_ids like '%,"+strSessionEmpId+",%' ");
			rst=pst.executeQuery();
			List<String> lPlanIdList = new ArrayList<String>();
//			System.out.println("pst ====> " + pst);
			while(rst.next()) {
				lPlanIdList.add(rst.getString("learning_plan_id"));
			}
			rst.close();
			pst.close();
			
			for (int b=0; lPlanIdList != null && !lPlanIdList.isEmpty() && b < lPlanIdList.size(); b++) {
			
				List<List<String>> lStageDetailsList = new ArrayList<List<String>>();
				pst = con.prepareStatement("select learning_plan_stage_id,learning_plan_stage_name,learning_plan_stage_name_id,from_date,to_date," +
						"weekdays,learning_type from learning_plan_stage_details where learning_plan_id = ? and learning_type != 'Training' order by learning_plan_stage_id");
				pst.setInt(1, uF.parseToInt(lPlanIdList.get(b)));
				rst=pst.executeQuery();
				
				while(rst.next()){
					List<String> innerList = new ArrayList<String>();
					innerList.add(rst.getString("learning_plan_stage_id"));
					innerList.add(rst.getString("from_date"));
					innerList.add(rst.getString("to_date"));
					innerList.add(rst.getString("weekdays"));
					innerList.add(rst.getString("learning_plan_stage_name"));
					innerList.add(rst.getString("learning_plan_stage_name_id"));
					innerList.add(rst.getString("learning_type"));
					lStageDetailsList.add(innerList);
				}
				rst.close();
				pst.close();

//			Map<String, List<String>> hmlPlanStageDetails = new HashMap<String, List<String>>();
//			Map<String, List<String>> hmlPlanStageMonths = new HashMap<String, List<String>>();
//			List<String> stageDetailList = new ArrayList<String>();
//			List<String> monthList = new ArrayList<String>();
			
				for (int a = 0; lStageDetailsList != null && !lStageDetailsList.isEmpty() && a < lStageDetailsList.size(); a++) {
					List<String> innerList = lStageDetailsList.get(a);
					String startDate = innerList.get(1);
					String endDate = innerList.get(2);
					String wkDays = innerList.get(3);
					String trainingTitle = innerList.get(4);
					String courseOrAssessId = innerList.get(5);
					String learningType = innerList.get(6);
//				stageDetailList = hmlPlanStageDetails.get(innerList.get(0));
//				if(stageDetailList == null) stageDetailList = new ArrayList<String>();
				
//				monthList = hmlPlanStageMonths.get(innerList.get(0));
//				if(monthList == null) monthList = new ArrayList<String>();
				
				String dayCount = uF.dateDifference(startDate, DBDATE, endDate, DBDATE);
				
				List<String> wkDayList = new ArrayList<String>();
				if(wkDays != null && !wkDays.equals("")) {
					wkDayList = Arrays.asList(getAppendData(wkDays).split(","));
				}
				
				if(startDate != null && !startDate.equals("")) {
					String strNewDate = null;
					int dayCnt = 0;
					java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
					 java.util.Calendar cal = java.util.Calendar.getInstance();
				        cal.setTime(dtDate);
				        int month = cal.get(java.util.Calendar.MONTH);
				        
					String strDay = new SimpleDateFormat("E").format(dtDate);
			        if(wkDayList != null && wkDayList.contains(strDay)) {
//			        	if(!monthList.contains(""+month)){
//			        		monthList.add(""+month);
//			        	}
			        	dayCnt++;
			        	if(learningType != null && learningType.equals("Course")) {
			        		reportListEmp.add("{url:'javascript:viewCourseDetails("+courseOrAssessId+", \\'"+trainingTitle+"\\');',color:'#9D9C9C',title: '"+trainingTitle+" Day "+dayCnt+"',start: new Date("+uF.getDateFormat(startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(startDate, DBDATE, "dd")+")}");
			        	} else if(learningType != null && learningType.equals("Assessment")) {
			        		reportListEmp.add("{url:'javascript:viewAssessmentDetails("+courseOrAssessId+", \\'"+trainingTitle+"\\');',color:'#9D9C9C',title: '"+trainingTitle+" Day "+dayCnt+"',start: new Date("+uF.getDateFormat(startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(startDate, DBDATE, "dd")+")}");
			        	} 
			        } else if(wkDayList == null) {
//			        	if(!monthList.contains(""+month)) {
//			        		monthList.add(""+month);
//			        	}
			        	dayCnt++;
			        	if(learningType != null && learningType.equals("Course")) {
			        		reportListEmp.add("{url:'javascript:viewCourseDetails("+courseOrAssessId+", \\'"+trainingTitle+"\\');',color:'#9D9C9C',title: '"+trainingTitle+" Day "+dayCnt+"',start: new Date("+uF.getDateFormat(startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(startDate, DBDATE, "dd")+")}");
			        	} else if(learningType != null && learningType.equals("Assessment")) {
			        		reportListEmp.add("{url:'javascript:viewAssessmentDetails("+courseOrAssessId+", \\'"+trainingTitle+"\\');',color:'#9D9C9C',title: '"+trainingTitle+" Day "+dayCnt+"',start: new Date("+uF.getDateFormat(startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(startDate, DBDATE, "dd")+")}");
			        	} 
			        }
					for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
						if(strNewDate == null)
						strNewDate = startDate;
						Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
						java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
						
				        cal.setTime(dtDate1);
				        int month1 = cal.get(java.util.Calendar.MONTH);
				        
						String strDay1 = new SimpleDateFormat("E").format(dtDate1);
						 if(wkDayList != null && wkDayList.contains(strDay1)) {
//							 if(!monthList.contains(""+month1)){
//					        		monthList.add(""+month1);
//					        	}
							 	dayCnt++;
							 	if(learningType != null && learningType.equals("Course")) {
					        		reportListEmp.add("{url:'javascript:viewCourseDetails("+courseOrAssessId+", \\'"+trainingTitle+"\\');',color:'#9D9C9C',title: '"+trainingTitle+" Day "+dayCnt+"',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
					        	} else if(learningType != null && learningType.equals("Assessment")) {
					        		reportListEmp.add("{url:'javascript:viewAssessmentDetails("+courseOrAssessId+", \\'"+trainingTitle+"\\');',color:'#9D9C9C',title: '"+trainingTitle+" Day "+dayCnt+"',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
					        	}
					        } else if(wkDayList == null) {
//					        	 if(!monthList.contains(""+month1)){
//						        		monthList.add(""+month1);
//						        	}
					        	 	dayCnt++;
					        	 	if(learningType != null && learningType.equals("Course")) {
						        		reportListEmp.add("{url:'javascript:viewCourseDetails("+courseOrAssessId+", \\'"+trainingTitle+"\\');',color:'#9D9C9C',title: '"+trainingTitle+" Day "+dayCnt+"',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
						        	} else if(learningType != null && learningType.equals("Assessment")) {
						        		reportListEmp.add("{url:'javascript:viewAssessmentDetails("+courseOrAssessId+", \\'"+trainingTitle+"\\');',color:'#9D9C9C',title: '"+trainingTitle+" Day "+dayCnt+"',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
						        	}
					        }
						strNewDate = newDate+"";
					}
					
				}
//				System.out.println("reportListEmp 1 ====> " + reportListEmp);
				}
			}
//				System.out.println("reportListEmp out 1 ====> " + reportListEmp);
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
	
	
	
//	public void getTrainingCalendar(UtilityFunctions uF, List<String> reportListEmp){
//		Database db = new Database();
//		db.setRequest(request);
//		Connection con = null;
//		
//		try {
//			
//			con = db.makeConnection(con);
//			PreparedStatement pst = con.prepareStatement("select * from training_schedule ts, training_session tss, training_plan tp where ts.schedule_id = tss.schedule_id and  tp.plan_id = ts.plan_id and emp_ids like '%,"+strSessionEmpId+",%'");
//			ResultSet rs = pst.executeQuery();
//			
//			java.util.Calendar cal = GregorianCalendar.getInstance();
//			
//			while(rs.next()){
//
//				int nDateDiff = uF.parseToInt(uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("end_date"), DBDATE));
//
//				cal.set(java.util.Calendar.DATE, uF.parseToInt(uF.getDateFormat(rs.getString("start_date"), DBDATE, "dd")));
//				cal.set(java.util.Calendar.MONTH, uF.parseToInt(uF.getDateFormat(rs.getString("start_date"), DBDATE, "MM"))-1);
//				cal.set(java.util.Calendar.YEAR, uF.parseToInt(uF.getDateFormat(rs.getString("start_date"), DBDATE, "yyyy")));
//
//				
//				String strFreqType = null;
//				String strFreqDay = null;
//				
//				
//				int nDay = 0;
//				int nDate = 0;
//				int nFreqDay = 0;
//				int nFreqDate = 0;
//				
//				
//				if(rs.getString("frequency").equalsIgnoreCase("W")){
//					strFreqType = "W";
//					strFreqDay = rs.getString("frequency_day");
//					
//					if(strFreqDay!=null && strFreqDay.equalsIgnoreCase(SUNDAY)){ 
//						nFreqDay = java.util.Calendar.SUNDAY;
//					}else if(strFreqDay!=null && strFreqDay.equalsIgnoreCase(MONDAY)){
//						nFreqDay = java.util.Calendar.MONDAY;
//					}else if(strFreqDay!=null && strFreqDay.equalsIgnoreCase(TUESDAY)){
//						nFreqDay = java.util.Calendar.TUESDAY;
//					}else if(strFreqDay!=null && strFreqDay.equalsIgnoreCase(WEDNESDAY)){
//						nFreqDay = java.util.Calendar.WEDNESDAY;
//					}else if(strFreqDay!=null && strFreqDay.equalsIgnoreCase(THURSDAY)){
//						nFreqDay = java.util.Calendar.THURSDAY;
//					}else if(strFreqDay!=null && strFreqDay.equalsIgnoreCase(FRIDAY)){
//						nFreqDay = java.util.Calendar.FRIDAY;
//					}else if(strFreqDay!=null && strFreqDay.equalsIgnoreCase(SATURDAY)){
//						nFreqDay = java.util.Calendar.SATURDAY;
//					}
//				}else if(rs.getString("frequency").equalsIgnoreCase("M") && rs.getString("frequency_date")!=null){
//					strFreqType = "M";
//					nFreqDate = uF.parseToInt(uF.getDateFormat(rs.getString("frequency_date"), DBDATE, "MM"));
//				}else if(rs.getString("frequency").equalsIgnoreCase("OT")){
//					strFreqType = "OT";
//				}
//				
//				for(int i=0; i<nDateDiff; i++){
//					if(strFreqType!=null && strFreqType.equalsIgnoreCase("W")){
//						nDay = cal.get(java.util.Calendar.DAY_OF_WEEK);
//						if(nDay == nFreqDay){
//							String strDate = uF.getDateFormat(cal.get(java.util.Calendar.DATE)+"/"+(cal.get(java.util.Calendar.MONTH)+1)+"/"+cal.get(java.util.Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//							reportListEmp.add("{url:'MyLearningPlan.action',color:'#889988',title: '"+rs.getString("training_title")+"',start: new Date("+uF.getDateFormat(strDate, DATE_FORMAT, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "M"))-1)+", "+uF.getDateFormat(strDate, DATE_FORMAT, "dd")+")}");
//						}
//					}else if(strFreqType!=null && strFreqType.equalsIgnoreCase("M")){
//						nDate = cal.get(java.util.Calendar.DAY_OF_MONTH);
//						if(nDate == nFreqDate){
//							String strDate = uF.getDateFormat(cal.get(java.util.Calendar.DATE)+"/"+(cal.get(java.util.Calendar.MONTH)+1)+"/"+cal.get(java.util.Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//							reportListEmp.add("{url:'MyLearningPlan.action',color:'#889988',title: '"+rs.getString("training_title")+"',start: new Date("+uF.getDateFormat(strDate, DATE_FORMAT, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "M"))-1)+", "+uF.getDateFormat(strDate, DATE_FORMAT, "dd")+")}");
//						}
//					}
//					cal.add(java.util.Calendar.DATE, 1);
//				}
//				
//				if(strFreqType!=null && strFreqType.equalsIgnoreCase("OT") && rs.getString("frequency_date")!=null){
//					reportListEmp.add("{url:'MyLearningPlan.action',color:'#889988',title: '"+rs.getString("training_title")+"',start: new Date("+uF.getDateFormat(rs.getString("frequency_date"), DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(rs.getString("frequency_date"), DBDATE, "M"))-1)+", "+uF.getDateFormat(rs.getString("frequency_date"), DBDATE, "dd")+")}");
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeConnection(con);
//		}
//	}
	
	
	public void getInterviewCalendar(UtilityFunctions uF, List<String> reportListEmp){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			//String roundID = null;
			Map<String, String> hmChekCandiRound = new HashMap<String, String>();
			pst = con.prepareStatement("select * from candidate_interview_panel");
//			pst2.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while(rs.next()){
				if(rs.getString("status").equals("-1")){
					hmChekCandiRound.put(rs.getString("candidate_id")+"_"+rs.getString("recruitment_id"), "true");
				}
			}
			rs.close();
			pst.close();
			
			List<String> roundIDList = new ArrayList<String>();
			Map<String, List<String>> hmRecruitWiseRoundId = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select * from panel_interview_details where panel_emp_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while(rs.next()){
				roundIDList = hmRecruitWiseRoundId.get(rs.getString("recruitment_id"));
				if(roundIDList  == null) roundIDList = new ArrayList<String>();
				
				roundIDList.add(rs.getString("round_id"));
//				roundID = rs1.getString("round_id");
				hmRecruitWiseRoundId.put(rs.getString("recruitment_id"), roundIDList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmRecruitWiseRoundId ===> " + hmRecruitWiseRoundId);
			Set keys=hmRecruitWiseRoundId.keySet();
			Iterator it=keys.iterator();
			while(it.hasNext()){
				String key=(String)it.next();
//				System.out.println("Key ===> " + key);
				
				List<String> roundIDList1 = hmRecruitWiseRoundId.get(key);
				for (int i = 0; roundIDList1!= null && !roundIDList1.isEmpty() && i < roundIDList1.size(); i++) {
				
					pst = con.prepareStatement("select distinct(cad.candidate_id) as candidate_id, cad.recruitment_id, cpd.emp_fname,cpd.emp_mname, cpd.emp_lname, cad.job_code, " +
						"cip.interview_date, cip.is_selected, cip.is_interview_taken,cip.interview_time from candidate_interview_panel cip, candidate_personal_details cpd " +
				  		"join candidate_application_details cad on cad.candidate_id= cpd.emp_per_id where cip.candidate_id = cpd.emp_per_id " +
				  		"and cip.recruitment_id = cad.recruitment_id and cip.panel_round_id = ? and cip.panel_user_id = ? and cip.recruitment_id = ?");
//					PreparedStatement pst = con.prepareStatement("select * from candidate_interview_panel cip, candidate_personal_details cpd where cip.candidate_id = cpd.emp_per_id and panel_round_id = ?");
					pst.setInt(1, uF.parseToInt(roundIDList1.get(i)));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setInt(3, uF.parseToInt(key));
					rs = pst.executeQuery();
//					System.out.println("pst ===> "+pst);
					while(rs.next()){
						if(hmChekCandiRound != null && hmChekCandiRound.get(rs.getString("candidate_id")+"_"+rs.getString("recruitment_id")) != null &&
							hmChekCandiRound.get(rs.getString("candidate_id")+"_"+rs.getString("recruitment_id")).equals("true")){
							continue;
						}else{
							
							String strEmpMName = "";
							if(flagMiddleName) {
								if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
									strEmpMName = " "+rs.getString("emp_mname");
								}
							}
							
							String strStartDateTime = rs.getString("interview_date") +" " + rs.getString("interview_time");
							Timestamp dtEndTime = uF.getEndDateTime(strStartDateTime);
//							String strEndTime = uF.getDateFormat(dtEndTime+"", DBTIMESTAMP, DBTIMESTAMP);
//					        System.out.println(strStartDateTime + " -- strEndTime = " + strEndTime);
//							String strEndDateTime = rs.getString("interview_date") +" " + "10:30:00";
							
							if(uF.parseToInt(rs.getString("is_selected"))==1 && getStrInterviewsApproved()){
								reportListEmp.add("{url:'CandidateMyProfile.action?candID="+rs.getString("candidate_id")+"&recruitId="+rs.getString("recruitment_id")+"',color:'#336600',title: '"+rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+" approved by you',start: new Date("+uF.getDateFormat(rs.getString("interview_date"), DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(rs.getString("interview_date"), DBDATE, "M"))-1)+", "+uF.getDateFormat(rs.getString("interview_date"), DBDATE, "dd")+")}");	
							}else if(uF.parseToInt(rs.getString("is_selected"))==-1 && getStrInterviewsDenied()){
								reportListEmp.add("{url:'CandidateMyProfile.action?candID="+rs.getString("candidate_id")+"&recruitId="+rs.getString("recruitment_id")+"',color:'#FF0000',title: '"+rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+" rejected by you',start: new Date("+uF.getDateFormat(rs.getString("interview_date"), DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(rs.getString("interview_date"), DBDATE, "M"))-1)+", "+uF.getDateFormat(rs.getString("interview_date"), DBDATE, "dd")+")}");
							}else if(uF.parseToInt(rs.getString("is_selected"))==0 && getStrInterviewsPending()){
//								reportListEmp.add("{url:'CandidateMyProfile.action?candID="+rs.getString("candidate_id")+"&recruitId="+rs.getString("recruitment_id")+"',color:'#9900CC',title: '"+"Interview scheduled with "+rs.getString("emp_fname")+" "+rs.getString("emp_lname")+" for "+rs.getString("job_code")+"',start: new Date("+uF.getDateFormat(rs.getString("interview_date"), DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(rs.getString("interview_date"), DBDATE, "M"))-1)+", "+uF.getDateFormat(rs.getString("interview_date"), DBDATE, "dd")+")}");
								if(rs.getBoolean("is_interview_taken")) {
//									reportListEmp.add("{url:'javascript:openCandidateProfilePopup("+rs.getString("candidate_id")+","+rs.getString("recruitment_id")+");',color:'#9900CC', title: '"+"Interview scheduled with "+rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+" for "+rs.getString("job_code")+"',start: new Date("+uF.getDateFormat(rs.getString("interview_date"), DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(rs.getString("interview_date"), DBDATE, "M"))-1)+", "+uF.getDateFormat(rs.getString("interview_date"), DBDATE, "dd")+")}");
									reportListEmp.add("{url:'javascript:openCandidateProfilePopup("+rs.getString("candidate_id")+","+rs.getString("recruitment_id")+");',color:'#9900CC', title: '"+"Interview scheduled with "+rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+" for "+rs.getString("job_code")+"',start: '"+strStartDateTime+"', end: '"+dtEndTime+"'}"); //, end: '"+dtEndTime1+"'
								} else {
//									reportListEmp.add("{url:'javascript:openCandidateProfilePopup("+rs.getString("candidate_id")+","+rs.getString("recruitment_id")+");',color:'#9900CC', title: '"+"Interview scheduled with "+rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+" for "+rs.getString("job_code")+"',start: new Date("+uF.getDateFormat(rs.getString("interview_date"), DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(rs.getString("interview_date"), DBDATE, "M"))-1)+", "+uF.getDateFormat(rs.getString("interview_date"), DBDATE, "dd")+")}");
									reportListEmp.add("{url:'javascript:openCandidateProfilePopup("+rs.getString("candidate_id")+","+rs.getString("recruitment_id")+");',color:'#9900CC', title: '"+"Interview scheduled with "+rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname")+" for "+rs.getString("job_code")+"',start: '"+strStartDateTime+"', end: '"+dtEndTime+"'}"); //, end: '"+strEndDateTime+"'
								}
							}
						}
					}
					rs.close();
					pst.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}  
	
	
	
	public void getBirthdays(UtilityFunctions uF, List<String> reportListEmp){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		
		try {
			con = db.makeConnection(con);
			
			List<String> alBirthDates = new ArrayList<String>();
			List<String> alEmpIds = new ArrayList<String>();
			
			Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con, null, null);
			CF.getBirthday(con, uF, CF, hmEmployeeMap, request, alBirthDates, alEmpIds);
			
			for(int i=0; i<alBirthDates.size(); i++){
				reportListEmp.add("{color:'#336600',title: 'Its "+hmEmployeeMap.get(alEmpIds.get(i))+"\\'s birthday today.',start: new Date("+uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(alBirthDates.get(i), DBDATE, "M"))-1)+", "+uF.getDateFormat(alBirthDates.get(i), DBDATE, "dd")+")}");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
	}

	public Map<String,String> getHrDetails(Connection con)
	{
		PreparedStatement pst = null;
		Database db = new Database();
		ResultSet rs = null;
		Map<String,String> hmHRDetails = new HashMap<String,String>();
		try{
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from employee_official_details eod,employee_personal_details epd,user_details ud where epd.emp_per_id=eod.emp_id "
					+ "and epd.is_alive= true and ud.emp_id=epd.emp_per_id and (usertype_id = (select user_type_id "
					+ "from user_type where user_type = '" + ADMIN + "') or (usertype_id = (select user_type_id from user_type where user_type = '" + HRMANAGER
					+ "') ");
				sbQuery.append(")) order by epd.emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				while(rs.next()){
					hmHRDetails.put(rs.getString("emp_per_id"),rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
				}
				rs.close();
				pst.close();
		
		}catch (Exception e) {
			e.printStackTrace();
		} 
		return hmHRDetails;
	}
	
	private void getVisits(UtilityFunctions uF,List<String> reportListEmp){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
				con = db.makeConnection(con);
				Map<String,String> clientlist =CF.getProjectClientMap(con, uF);
				Map<String,String> hrList = getHrDetails(con);
				String[] strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				String strCalendarYearStart = strCalendarYearDates[0];
				String strCalendarYearEnd = strCalendarYearDates[1];
				
				pst = con.prepareStatement("select * from hr_client_visit_details where date between ? and ? order by date");
				pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));		
				pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
//				System.out.println("pst====>"+pst);
				while(rs.next()) {
					if(rs.getString("date")!=null && !rs.getString("date").equals("") ){
						java.sql.Date eDate = rs.getDate("date");
						String newVisitDate = uF.getDateFormatUtil(eDate, DBDATE);
						String strClientName = rs.getString("client_name");
						String[] clientName = strClientName.split(",");
						for(String client: clientName){
							if(client!=null && !client.equals("")){
							reportListEmp.add("{url:'javascript:openVisitPopup("+rs.getString("visit_id")+");',color:'#808080',title: '"+"visit for "+clientlist.get(client)+". See Details...!"+"',start: new Date("+uF.getDateFormat(newVisitDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(newVisitDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(newVisitDate, DBDATE, "dd")+")}");
							}
						}
					
					}
				}
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	private void getEvents(UtilityFunctions uF,List<String> reportListEmp){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			con = db.makeConnection(con);
			
			String[] strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
			String strCalendarYearStart = strCalendarYearDates[0];
			String strCalendarYearEnd = strCalendarYearDates[1];
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
//			Map<String, String> hmLevelMap = getLevelNameMap(con, uF);
			
			String logUserLevel = hmEmpLevelMap.get(strSessionEmpId);
//			System.out.println("logUserLevel==>"+logUserLevel);	
			
			pst = con.prepareStatement("SELECT * FROM events where (sharing_level is null or length(trim(sharing_level))=0 or sharing_level like '%,"+logUserLevel+",%' or added_by= ("+strSessionEmpId+")) and event_date between ? and ? order by event_date ");
			pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("event_date")!=null && !rs.getString("event_date").equals("") && rs.getString("event_end_date")!=null && !rs.getString("event_end_date").equals("")){
					String diffInDays = uF.dateDifference(uF.getDateFormat(rs.getString("event_date"), DBDATE, DATE_FORMAT), DATE_FORMAT,uF.getDateFormat(rs.getString("event_end_date"), DBDATE, DATE_FORMAT),DATE_FORMAT);
//					System.out.println("diffInDays==>"+diffInDays);
					if(uF.parseToInt(diffInDays)>1) {
						java.sql.Date eDate = rs.getDate("event_date");
						String eventDate = uF.getDateFormat(rs.getString("event_date"), DBDATE, DATE_FORMAT);
						for(int i =1;i<=(uF.parseToInt(diffInDays));i++) {
							String newEventDate = uF.getDateFormatUtil(eDate, DBDATE);
//							System.out.println("newEventDate ===>> " + newEventDate);
							reportListEmp.add("{url:'javascript:openEventPopup("+rs.getString("event_id")+");',color:'#a14f76',title: '"+rs.getString("event_title").replace("'", "\\\'")+",Posted by "+hmResourceName.get(rs.getString("added_by"))+". See Details...!"+"',start: new Date("+uF.getDateFormat(newEventDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(newEventDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(newEventDate, DBDATE, "dd")+")}");
							eDate = uF.getBiweeklyDate(eventDate, i);
						}
					} else {
//						System.out.println("event_date ===>> " + rs.getString("event_date"));
						reportListEmp.add("{url:'javascript:openEventPopup("+rs.getString("event_id")+");',color:'#a14f76',title: '"+rs.getString("event_title").replace("'", "\\\'")+",Posted by "+hmResourceName.get(rs.getString("added_by"))+". See Details...!"+"',start: new Date("+uF.getDateFormat(rs.getString("event_date"), DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(rs.getString("event_date"), DBDATE, "M"))-1)+", "+uF.getDateFormat(rs.getString("event_date"), DBDATE, "dd")+")}");
					}
				}
			}			                   
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private Map<String,String> getLevelNameMap(Connection con,UtilityFunctions uF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new LinkedHashMap<String,String>();
		try{
			
			pst = con.prepareStatement("SELECT * FROM level_details ");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name"));
			}
			rs.close();
			pst.close();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return hmLevelMap;
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


	public boolean getStrBirthdays() {
		return strBirthdays;
	}
	public void setStrBirthdays(boolean strBirthdays) {
		this.strBirthdays = strBirthdays;
	}
	public boolean getStrTrainings() {
		return strTrainings;
	}
	public void setStrTrainings(boolean strTrainings) {
		this.strTrainings = strTrainings;
	}
	public String getStrFrm() {
		return strFrm;
	}
	public void setStrFrm(String strFrm) {
		this.strFrm = strFrm;
	}
	public boolean getStrInterviewsPending() {
		return strInterviewsPending;
	}
	public void setStrInterviewsPending(boolean strInterviewsPending) {
		this.strInterviewsPending = strInterviewsPending;
	}
	public boolean getStrInterviewsApproved() {
		return strInterviewsApproved;
	}
	public void setStrInterviewsApproved(boolean strInterviewsApproved) {
		this.strInterviewsApproved = strInterviewsApproved;
	}
	public boolean getStrInterviewsDenied() {
		return strInterviewsDenied;
	}
	public void setStrInterviewsDenied(boolean strInterviewsDenied) {
		this.strInterviewsDenied = strInterviewsDenied;
	}

	public boolean getStrEvents() {
		return strEvents;
	}
	public void setStrEvents(boolean strEvents) {
		this.strEvents = strEvents;
	}
	
	
	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	public boolean getStrVisits() {
		return strVisits;
	}

	public void setStrVisits(boolean strVisits) {
		this.strVisits = strVisits;
	}
	
}