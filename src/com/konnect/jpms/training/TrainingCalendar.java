package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
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

public class TrainingCalendar extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId; 
	CommonFunctions CF = null;

	private static Logger log = Logger.getLogger(TrainingCalendar.class);
	private String trainingId;
	private boolean strTrainings;
	private String fromPage;
	public String execute() throws Exception {
 
		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);

		
		request.setAttribute(PAGE, "/jsp/training/TrainingCalendar.jsp");
		request.setAttribute(TITLE, "Training Calendar"); 
		
		UtilityFunctions uF = new UtilityFunctions(); 

		
		List<String> reportListEmp = new ArrayList<String>();
		
			getTrainingCalendar(uF, reportListEmp);
		
		request.setAttribute("reportListEmp", reportListEmp);
		
		return SUCCESS;
	}
	
	public void getTrainingCalendar(UtilityFunctions uF, List<String> reportListEmp){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select schedule_id,start_date,end_date,day_schedule_type,training_frequency,training_day,training_weekday from training_schedule where plan_id=?");
			pst.setInt(1, uF.parseToInt(getTrainingId()));
			
			rst=pst.executeQuery();
			String scheduleId = null;
			String startDate = null;
			String endDate = null;
			String scheduleType = null;
			String trainingFrequency = null;
			String trainingDay = null;
			String trainingWeekDay = null;
			while(rst.next()){	
				scheduleId = rst.getString("schedule_id");
				startDate = rst.getString("start_date");
				endDate = rst.getString("end_date");
				scheduleType = rst.getString("day_schedule_type");
				trainingFrequency = rst.getString("training_frequency");
				trainingDay = rst.getString("training_day");
				trainingWeekDay = rst.getString("training_weekday");
			}
			rst.close();
			pst.close();
			
			request.setAttribute("startDate", startDate);
			request.setAttribute("endDate", endDate);
			
			Map<String, String> hmDayDescription = new HashMap<String, String>();
			pst = con.prepareStatement("select training_schedule_id,day_date,day_description from training_schedule_details where training_id=? and training_schedule_id = ?");
			pst.setInt(1, uF.parseToInt(getTrainingId()));
			pst.setInt(2, uF.parseToInt(scheduleId));
			rst=pst.executeQuery();
			while(rst.next()){	
				String dayDate = uF.getDateFormat(rst.getString("day_date"), DBDATE, DATE_FORMAT);
				hmDayDescription.put(rst.getString("training_schedule_id")+"_"+dayDate, rst.getString("day_description"));
			}
			rst.close();
			pst.close();
			
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
			
			List<String> monthList = new ArrayList<String>();
			
			if(scheduleType != null && uF.parseToInt(scheduleType) == 1) {
				if(trainingFrequency!=null && trainingFrequency.equals("1")){
					List<String> wkDayList = new ArrayList<String>();
					if(wkDays != null && !wkDays.equals("")) {
						wkDayList = Arrays.asList(getAppendData(wkDays).split(","));
					}
					
					String strNewDate = null;
					int dayCnt = 0;
					
					java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
					Calendar cal = Calendar.getInstance();
				    cal.setTime(dtDate);
				    int month = cal.get(Calendar.MONTH);
					
					String strDay = new SimpleDateFormat("E").format(dtDate);
			        if(wkDayList != null && wkDayList.contains(strDay)){
			        	if(!monthList.contains(""+month)){
			        		monthList.add(""+month);
			        	}
			        	dayCnt++;
			        	String dayDesId = getDayDescriptionId(con, getTrainingId(), scheduleId, startDate, uF);
						reportListEmp.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(startDate, DBDATE, "dd")+")}");
			        } else if(wkDayList == null) {
			        	if(!monthList.contains(""+month)){
			        		monthList.add(""+month);
			        	}
			        	dayCnt++;
			        	String dayDesId = getDayDescriptionId(con, getTrainingId(), scheduleId, startDate, uF);
						reportListEmp.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(startDate, DBDATE, "dd")+")}");
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
							 	String dayDesId = getDayDescriptionId(con, getTrainingId(), scheduleId, ""+newDate, uF);
								reportListEmp.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
					        }else if(wkDayList == null) {
					        	if(!monthList.contains(""+month1)){
					        		monthList.add(""+month1);
					        	}
				        	 	dayCnt++;
						        String dayDesId = getDayDescriptionId(con, getTrainingId(), scheduleId, ""+newDate, uF);
						        reportListEmp.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
					        }
						strNewDate = newDate+"";
					}
				} else if(trainingFrequency!=null && trainingFrequency.equals("2")){
					List<String> wkDayList = new ArrayList<String>();
					wkDayList.add(trainingWeekDay);
					String strNewDate = null;
					int dayCnt = 0;
					
					java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
					Calendar cal = Calendar.getInstance();
				    cal.setTime(dtDate);
				    int month = cal.get(Calendar.MONTH);
					
					String strDay = new SimpleDateFormat("EEEE").format(dtDate);
			        if(wkDayList != null && wkDayList.contains(strDay)){
			        	if(!monthList.contains(""+month)){
			        		monthList.add(""+month);
			        	}
			        	dayCnt++;
			        	String dayDesId = getDayDescriptionId(con, getTrainingId(), scheduleId, startDate, uF);
						reportListEmp.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(startDate, DBDATE, "dd")+")}");
			        }
					for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
						if(strNewDate == null)
						strNewDate = startDate;
						Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
						java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
						cal.setTime(dtDate1);
				        int month1 = cal.get(Calendar.MONTH);
				        if(!monthList.contains(""+month1)){
			        		monthList.add(""+month1);
			        	}
						
						String strDay1 = new SimpleDateFormat("EEEE").format(dtDate1);
						 if(wkDayList != null && wkDayList.contains(strDay1)){
							if(!monthList.contains(""+month1)){
					        		monthList.add(""+month1);
				        	}
			        	 	dayCnt++;
					        String dayDesId = getDayDescriptionId(con, getTrainingId(), scheduleId, ""+newDate, uF);
					        reportListEmp.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
					     }
						strNewDate = newDate+"";
					}
				} else if(trainingFrequency!=null && trainingFrequency.equals("3")){
					if(trainingDay!=null){
						List<String> wkDayList = new ArrayList<String>();
						String strTDay = uF.parseToInt(trainingDay.trim()) < 10 ? "0"+trainingDay.trim() : trainingDay.trim();
						wkDayList.add(strTDay);
						String strNewDate = null;
						int dayCnt = 0;
						
						java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
						Calendar cal = Calendar.getInstance();
					    cal.setTime(dtDate);
					    int month = cal.get(Calendar.MONTH);
					    
						String strDay = new SimpleDateFormat("dd").format(dtDate);
				        if(wkDayList != null && wkDayList.contains(strDay)){
				        	if(!monthList.contains(""+month)){
				        		monthList.add(""+month);
				        	}
				        	dayCnt++;
				        	String dayDesId = getDayDescriptionId(con, getTrainingId(), scheduleId, startDate, uF);
							reportListEmp.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(startDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(startDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(startDate, DBDATE, "dd")+")}");
				        }
						for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
							if(strNewDate == null)
							strNewDate = startDate;
							Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
							java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
							cal.setTime(dtDate1);
					        int month1 = cal.get(Calendar.MONTH);
					        if(!monthList.contains(""+month1)){
				        		monthList.add(""+month1);
				        	}
					        
							String strDay1 = new SimpleDateFormat("dd").format(dtDate1);
							 if(wkDayList != null && wkDayList.contains(strDay1)){
								 if(!monthList.contains(""+month1)){
						        		monthList.add(""+month1);
					        	}
				        	 	dayCnt++;
						        String dayDesId = getDayDescriptionId(con, getTrainingId(), scheduleId, ""+newDate, uF);
						        reportListEmp.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+dayCnt+");',color:'#9D9C9C',title: 'Day "+dayCnt+"',start: new Date("+uF.getDateFormat(""+newDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(""+newDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(""+newDate, DBDATE, "dd")+")}");
						     }
							strNewDate = newDate+"";
						}
					}
				}
			} else if(scheduleType != null && uF.parseToInt(scheduleType) == 2) {
				Calendar cal = Calendar.getInstance();
				for(int i=0; frequencyDateList != null && !frequencyDateList.isEmpty() && i< frequencyDateList.size(); i++) {
					java.util.Date dtDate1 = uF.getDateFormatUtil(frequencyDateList.get(i), DBDATE);
					cal.setTime(dtDate1);
			        int month1 = cal.get(Calendar.MONTH);
			        if(!monthList.contains(""+month1)){
		        		monthList.add(""+month1);
		        	}
			        Date dtDate11 = uF.getDateFormat(frequencyDateList.get(i), DBDATE);
			        String dayDesId = getDayDescriptionId(con, getTrainingId(), scheduleId, ""+dtDate11, uF);
			        
					reportListEmp.add("{url:'javascript:openTrainingScheduleDayDetails("+dayDesId+", "+(i+1)+");',color:'#9D9C9C',title: 'Day "+(i+1)+"',start: new Date("+uF.getDateFormat(frequencyDateList.get(i), DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(frequencyDateList.get(i), DBDATE, "M"))-1)+", "+uF.getDateFormat(frequencyDateList.get(i), DBDATE, "dd")+")}");
				}
			}
			Collections.sort(monthList);
			request.setAttribute("monthList", monthList);
			
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
//			System.out.println("pst ====> " + pst);
			rst=pst.executeQuery();
			while(rst.next()){
				dayDesId = rst.getString("training_schedule_details_id");
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
		return dayDesId;
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
	
	public String getTrainingId() {
		return trainingId;
	}
	
	public void setTrainingId(String trainingId) {
		this.trainingId = trainingId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}
