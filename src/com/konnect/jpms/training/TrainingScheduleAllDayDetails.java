package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.performance.FillAttribute;
import com.konnect.jpms.performance.FillFrequency;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TrainingScheduleAllDayDetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	
	private String trainingID;

	CommonFunctions CF = null;

	

	private static Logger log = Logger.getLogger(TrainingScheduleAllDayDetails.class);
	
	public String execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		UtilityFunctions uF = new UtilityFunctions();
		String strID = request.getParameter("ID");
		
		loadFilledData(uF);
		return LOAD;

	}
	
	
	private void loadFilledData(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		try {
			con = db.makeConnection(con);
			getTrainingPlanNameById(con, uF);
			getAllDayDetails(con,uF);
		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeConnection(con);
		}

		}
	
	private String getTrainingPlanNameById(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		String trainingName = null;
		try {
				pst = con.prepareStatement("select training_title from training_plan where plan_id = ?");
				pst.setInt(1, uF.parseToInt(getTrainingID()));
				rst = pst.executeQuery();
				while (rst.next()) {
					trainingName = rst.getString("training_title");
				}
				rst.close();
				pst.close();
				request.setAttribute("trainingName", trainingName);
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
		return trainingName;
	}
	
	
	private void getAllDayDetails(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;
//		System.out.println("step ===> "+step);
		try {

			pst = con.prepareStatement("select schedule_id,start_date,end_date,day_schedule_type,training_frequency,training_day,training_weekday from training_schedule where plan_id=?");
			pst.setInt(1, uF.parseToInt(getTrainingID()));
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
			
			Map<String, String> hmDayDescription = new HashMap<String, String>();
			pst = con.prepareStatement("select training_schedule_id,day_date,day_description,long_description from training_schedule_details where training_id=? and training_schedule_id = ?");
			pst.setInt(1, uF.parseToInt(getTrainingID()));
			pst.setInt(2, uF.parseToInt(scheduleId));
			rst=pst.executeQuery();
			while(rst.next()){	
				String dayDate = uF.getDateFormat(rst.getString("day_date"), DBDATE, DATE_FORMAT);
				hmDayDescription.put(rst.getString("training_schedule_id")+"_"+dayDate+"_S", rst.getString("day_description"));
				hmDayDescription.put(rst.getString("training_schedule_id")+"_"+dayDate+"_L", rst.getString("long_description"));
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
			
			//System.out.println("dayCount ===> " + dayCount);
			
			List<String> daysList = new ArrayList<String>();
			Map<String, String> hmdaysDate = new HashMap<String, String>();
			
			if(scheduleType != null && uF.parseToInt(scheduleType) == 1) {
				if(trainingFrequency!=null && trainingFrequency.equals("1")){
					List<String> wkDayList = new ArrayList<String>();
					if(wkDays != null && !wkDays.equals("")) {
						wkDayList = Arrays.asList(getAppendData(wkDays).split(","));
					}
					
					String strNewDate = null;
					
					java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
					String strDay = new SimpleDateFormat("E").format(dtDate);
			        if(wkDayList != null && wkDayList.contains(strDay)){
			        	hmdaysDate.put(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT), uF.getDateFormat(startDate, DBDATE, CF.getStrReportDateFormat()));
						daysList.add(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT));
			        } else if(wkDayList == null) {
			        	hmdaysDate.put(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT), uF.getDateFormat(startDate, DBDATE, CF.getStrReportDateFormat()));
						daysList.add(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT));
			        }
					for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
						if(strNewDate == null)
						strNewDate = startDate;
						Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
						java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
						String strDay1 = new SimpleDateFormat("E").format(dtDate1);
						 if(wkDayList != null && wkDayList.contains(strDay1)){
							 	hmdaysDate.put(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT), uF.getDateFormat(""+newDate, DBDATE, CF.getStrReportDateFormat()));
								daysList.add(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT));	
					        }else if(wkDayList == null) {
					        	hmdaysDate.put(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT), uF.getDateFormat(""+newDate, DBDATE, CF.getStrReportDateFormat()));
								daysList.add(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT));
					        }
						strNewDate = newDate+"";
					}
				} else if(trainingFrequency!=null && trainingFrequency.equals("2")){
					if(trainingWeekDay!=null){
						List<String> wkDayList = new ArrayList<String>();
						wkDayList.add(trainingWeekDay);
						String strNewDate = null;
						
						java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
						String strDay = new SimpleDateFormat("EEEE").format(dtDate);
				        if(wkDayList != null && wkDayList.contains(strDay)){
				        	hmdaysDate.put(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT), uF.getDateFormat(startDate, DBDATE, CF.getStrReportDateFormat()));
							daysList.add(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT));
				        }
						for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
							if(strNewDate == null)
							strNewDate = startDate;
							Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
							java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
							String strDay1 = new SimpleDateFormat("EEEE").format(dtDate1);
							 if(wkDayList != null && wkDayList.contains(strDay1)){
								 	hmdaysDate.put(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT), uF.getDateFormat(""+newDate, DBDATE, CF.getStrReportDateFormat()));
									daysList.add(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT));	
						     }
							strNewDate = newDate+"";
						}
					}
				} else if(trainingFrequency!=null && trainingFrequency.equals("3")){
					if(trainingDay!=null){
						List<String> wkDayList = new ArrayList<String>();
						String strTDay = uF.parseToInt(trainingDay.trim()) < 10 ? "0"+trainingDay.trim() : trainingDay.trim();
						wkDayList.add(strTDay);
						String strNewDate = null;
						
						java.util.Date dtDate = uF.getDateFormatUtil(startDate, DBDATE);
						String strDay = new SimpleDateFormat("dd").format(dtDate);
				        if(wkDayList != null && wkDayList.contains(strDay)){
				        	hmdaysDate.put(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT), uF.getDateFormat(startDate, DBDATE, CF.getStrReportDateFormat()));
							daysList.add(uF.getDateFormat(startDate, DBDATE, DATE_FORMAT));
				        }
						for(int i=0; i< (uF.parseToInt(dayCount)-1); i++) {
							if(strNewDate == null)
							strNewDate = startDate;
							Date newDate = uF.getFutureDate(uF.getDateFormatUtil(strNewDate, DBDATE), 1);
							java.util.Date dtDate1 = uF.getDateFormatUtil(""+newDate, DBDATE);
							String strDay1 = new SimpleDateFormat("dd").format(dtDate1);
							 if(wkDayList != null && wkDayList.contains(strDay1)){
								 	hmdaysDate.put(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT), uF.getDateFormat(""+newDate, DBDATE, CF.getStrReportDateFormat()));
									daysList.add(uF.getDateFormat(""+newDate, DBDATE, DATE_FORMAT));	
						     }
							strNewDate = newDate+"";
						}
					}
				}
			} else if(scheduleType != null && uF.parseToInt(scheduleType) == 2) {

				for(int i=0; frequencyDateList != null && !frequencyDateList.isEmpty() && i< frequencyDateList.size(); i++) {
					hmdaysDate.put(uF.getDateFormat(frequencyDateList.get(i), DBDATE, DATE_FORMAT), uF.getDateFormat(frequencyDateList.get(i), DBDATE, CF.getStrReportDateFormat()));
					daysList.add(uF.getDateFormat(frequencyDateList.get(i), DBDATE, DATE_FORMAT));
				}
			}
			
			Collections.sort(daysList);
			request.setAttribute("daysList", daysList);
			request.setAttribute("hmdaysDate", hmdaysDate);
			request.setAttribute("scheduleId", scheduleId);
			request.setAttribute("hmDayDescription", hmDayDescription);
			
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
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;

	}

	
	public String getTrainingID() {
		return trainingID;
	}


	public void setTrainingID(String trainingID) {
		this.trainingID = trainingID;
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

}