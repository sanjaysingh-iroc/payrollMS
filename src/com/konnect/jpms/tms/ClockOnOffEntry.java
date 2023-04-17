package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ClockOnOffEntry extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId;

	String strMessage;
	String strReason; 
	String strMode;
	String strNotify;
	String strNewTime;
	String strRosterStartTime;
	String strRosterEndTime;
	private String strApproval;
	String strTimeZone = null;
	
	String strCommonEmpId;
	
	String service;
	CommonFunctions CF = null;
	
	String strPrevMode;
	private boolean isRosterDependant;
	private boolean isRosterRequired;
	private boolean isSingleButtonClockOnOff;

	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strTimeZone = (String) session.getAttribute(O_TIME_ZONE);
		
		UtilityFunctions uF = new UtilityFunctions(); 
		
		if(getStrCommonEmpId()!=null){
			strEmpId = getStrCommonEmpId();
		}else{
			strEmpId = (String) session.getAttribute("EMPID");
		}
		
//		System.out.println("getIsRosterDependant ClockOnOffEntry ===>> " +getIsRosterDependant());
//		System.out.println("getIsRosterRequired ClockOnOffEntry ===>> " +getIsRosterRequired());
//		System.out.println("getIsSingleButtonClockOnOff ClockOnOffEntry ===>> " +getIsSingleButtonClockOnOff());
		
		if(!getIsRosterDependant() && !getIsRosterRequired()) {
			setClockEntryRosterIndependant(uF);
		} else {
			int nShiftBaseType = CF.getEmpShiftBaseType(uF,strEmpId,request);
			if(nShiftBaseType == 2) {
				setClockEntryShiftBase(uF);
			} else {
				setClockEntry(uF);
			}
		}
		return SUCCESS;
	}
	
	
	private void setClockEntryRosterIndependant(UtilityFunctions uF) {
//		System.out.println("================================== set clock entry =========================================");
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int DAY = cal.get(Calendar.DAY_OF_MONTH);
			
			int HOUR = cal.get(Calendar.HOUR_OF_DAY);
			int HOUR_A = cal.get(Calendar.HOUR_OF_DAY);
			int MINUTE_A = cal.get(Calendar.MINUTE);
			int MINUTE = cal.get(Calendar.MINUTE);

			boolean isCurrentRoster = new CommonFunctions(CF).isCurrentRostered(con,strEmpId);
//			System.out.println("isCurrentRoster==> " + isCurrentRoster);
			int nEmpServiceId = CF.getEmpServiceId(con, uF, strEmpId);
//			System.out.println("nEmpServiceId==> " + nEmpServiceId);
			
			if(!getIsRosterDependant()) {
				int nApproved = 0;
				if(uF.parseToInt(getStrApproval())==1) {
					nApproved = -2;
				}
				
				pst=con.prepareStatement("INSERT INTO attendance_punch_in_out_details (emp_id, punch_date_time, punch_date, punch_time, punch_mode, " +
					"user_location,latitude,longitude,punch_entry_mode) VALUES (?," +
					"to_timestamp('"+uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm")+"', 'yyyy-MM-dd HH24:MI:ss')," +
					"?,?, ?,?,?,?, ?)");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "" + DAY, "yyyyMMdd"));
				pst.setTime(3, uF.getTimeFormat(HOUR_A + ":" + MINUTE, TIME_FORMAT));
				pst.setString(4, getStrMode().toUpperCase());
//					pst.setString(4, "IN");
				pst.setString(5, "");
				pst.setDouble(6, 0.0d);
				pst.setDouble(7, 0.0d);
				pst.setString(8, "MANUAL");
//					System.out.println("in pst===>"+pst);
				int x = pst.executeUpdate();
			    pst.close();
			    
				if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")) {
					if(nEmpServiceId > 0) {
						pst = con.prepareStatement("SELECT * FROM attendance_details where emp_id =? and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD')=? and in_out='IN'");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "" + DAY, "yyyyMMdd"));
//						System.out.println("11 pst ===>> " + pst);
						rs = pst.executeQuery();
						boolean inFlag = false;
						while (rs.next()) {
							inFlag = true;
						}
						rs.close();
						pst.close();
						
						if(!inFlag) {
//							System.out.println("Rahul ------------------IN if--------------------- >> " + nEmpServiceId);
							pst = con.prepareStatement("INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, service_id,user_location) VALUES (?,?,?,?, ?,?,?,?, ?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setString(4, strMode);
							pst.setString(5, getStrReason());
							pst.setDouble(6, nApproved);
							pst.setDouble(7, 0);
							pst.setInt(8, nEmpServiceId);
							pst.setString(9, "WEB");
//							System.out.println("4 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
							
							if((getStrRosterStartTime()==null || getStrRosterStartTime().trim().equals("") || getStrRosterStartTime().trim().equalsIgnoreCase("NULL")) || (getStrRosterEndTime() == null || getStrRosterEndTime().trim().equals("") || getStrRosterEndTime().trim().equalsIgnoreCase("NULL"))){
								String userlocation = CF.getEmpWlocationId(con, uF, strEmpId); 
								pst = con.prepareStatement("select wlocation_id,wlocation_start_time,wlocation_end_time from work_location_info where wlocation_id =?");
								pst.setInt(1, uF.parseToInt(userlocation));
//								System.out.println("5 pst ===>> " + pst);
								rs = pst.executeQuery();
								while (rs.next()) {
									setStrRosterStartTime(rs.getString("wlocation_start_time"));
									setStrRosterEndTime(rs.getString("wlocation_end_time"));
								}
								rs.close();
								pst.close();
							}
							
							Time start = uF.getCurrentTime(CF.getStrTimeZone());
							Time end = uF.getTimeFormat(getStrRosterEndTime(), CF.getStrReportTimeFormat());
							
							if(getStrRosterStartTime()!=null) {
								start = uF.getTimeFormat(getStrRosterStartTime(), CF.getStrReportTimeFormat());
							}
							
							long lStart = 0;
							long lEnd = 0;
							if(start!=null && end!=null) {
								lStart = start.getTime();
								lEnd = end.getTime();
							}
//							System.out.println("getService()!=null start ===>> " + start+" -- lStart ===>> " + lStart);
//							System.out.println("getService()!=null end ===>> " + end+ " -- lEnd ===>> " + lEnd);
							
							pst = con.prepareStatement("INSERT INTO roster_details  (emp_id, _date, _from, _to, service_id, attended, actual_hours) VALUES (?,?,?,?,?,1,?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getDateFormat(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setTime(3, start);
							pst.setTime(4, end);
							pst.setInt(5, nEmpServiceId);
							pst.setDouble(6, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, lEnd)));
//							System.out.println("6 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
//							System.out.println("CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
							if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())) {
								CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked On ", -1, getStrReason());
							}
						}
					}
				} else if(getStrMode()!=null && getStrMode().equalsIgnoreCase("OUT")) {
//					System.out.println("------------------OUT---------------------");
					
					pst = con.prepareStatement("SELECT * FROM attendance_details where emp_id =? and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD')=? and in_out='OUT'");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "" + DAY, "yyyyMMdd"));
//					System.out.println("11 pst ===>> " + pst);
					rs = pst.executeQuery();
					boolean outFlag = false;
					while (rs.next()) {
						outFlag = true;
					}
					rs.close();
					pst.close();
					
					if(!outFlag) {
						pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//						System.out.println("10 pst ===>> " + pst);
						rs = pst.executeQuery();
						String strRosterDate = null;
						String strFrom = null;
						String strTo = null;
						int nCurrServiceId = 0;
						while (rs.next()) {
							strFrom = rs.getString("_from");
							strTo = rs.getString("_to");
							strRosterDate = rs.getString("_date");
							nCurrServiceId = uF.parseToInt(rs.getString("service_id"));
						}
						rs.close();
						pst.close();
						
						pst = con.prepareStatement("SELECT * FROM attendance_details where emp_id =? and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD')=? and in_out='IN'");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "" + DAY, "yyyyMMdd"));
//						System.out.println("11 pst ===>> " + pst);
						rs = pst.executeQuery();
						long IN = 0;
						String strTime = null;
						while (rs.next()) {
							IN = uF.getTimeStamp(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
							strTime = rs.getString("in_out_timestamp");
						}
						rs.close();
						pst.close();
//						System.out.println("IN ===>> " + IN +" -- strTime ===>> " + strTime);
						
						double dbl = 0.0d;
						long milliseconds3 = 0L;
						long milliseconds1 = 0L;
						long milliseconds2 = 0L;
						long milliseconds3a = 0L;
					
						
						if(strFrom!=null && strTo!=null) {
							
							Time entryTime = uF.getTimeFormat(strFrom, DBTIME);
							Time exitTime = uF.getTimeFormat(strTo, DBTIME);
							Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
	
							milliseconds1 = entryTime.getTime();
							milliseconds2 = exitTime.getTime();
							milliseconds3a = currentTime.getTime();
							
							if(entryTime.after(exitTime)) {
								milliseconds2 += 60 * 60 * 24 * 1000; 	
							}
							milliseconds3  = uF.getTimeStamp(strRosterDate+" "+uF.getCurrentTime(strTimeZone), DBTIMESTAMP).getTime();
							
							long diff = 0L;
	
							if(nApproved!=0) {
								dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds2, milliseconds3a));
								
								if(milliseconds2>milliseconds3a) {
									dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3a, milliseconds2));
								} else {
									dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds2, milliseconds3a));
								}
							}
//							System.out.println("strFrom!=null && strTo!=null dbl ===>> " + dbl);
						} else {
							milliseconds3  = uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+" "+uF.getCurrentTime(CF.getStrTimeZone()), DBTIMESTAMP).getTime();
//							System.out.println("strFrom!=null && strTo!=null milliseconds3 ===>> " + milliseconds3);
						}
						
						double dblHoursWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(IN, milliseconds3));
//						System.out.println("dblHoursWorked ===>> " + dblHoursWorked);
						
						String str = uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR + ":" + MINUTE, "HH:mm");
//						System.out.println("TIME===="+ str);
//						System.out.println("TIME Format===="+uF.getTimeStamp(str, "yyyy-MM-ddhh:mm"));
						
						if(strTo!=null) {						
							pst = con.prepareStatement("INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, hours_worked, notify_time, new_time, service_id,user_location) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setTimestamp(2, uF.getTimeStamp(str, "yyyy-MM-ddHH:mm"));
							pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setString(4, strMode);
							pst.setString(5, getStrReason());
							pst.setDouble(6, nApproved);
							pst.setDouble(7, dbl);
							pst.setDouble(8, dblHoursWorked);
							pst.setInt(9, ((uF.parseToBoolean(getStrNotify()))?1:0));
							pst.setTime(10, uF.getTimeFormat(getStrNewTime(), CF.getStrReportTimeFormat()));
							pst.setInt(11, nCurrServiceId);
							pst.setString(12, "WEB");
//							System.out.println("12 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
							pst = con.prepareStatement("update roster_details set attended=2 where emp_id=? and _date =? and service_id=?");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getDateFormat(str, "yyyy-MM-ddHH:mm"));
							pst.setInt(3, nCurrServiceId);
//							System.out.println("13 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
//							System.out.println("strTo!=null CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
							if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())){
								CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked Off ", -2, getStrReason());
							}
						} else {
							pst = con.prepareStatement("INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, hours_worked, service_id,user_location) VALUES (?,?,?,?, ?,?,?,?, ?,?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setTimestamp(2, uF.getTimeStamp(str, "yyyy-MM-ddHH:mm"));
							pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setString(4, strMode);
							pst.setString(5, getStrReason());
							pst.setDouble(6, nApproved);
							pst.setDouble(7, 0);
							pst.setDouble(8, dblHoursWorked);
							pst.setInt(9, nCurrServiceId);
							pst.setString(10, "WEB");
//							System.out.println("14 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
							Time entryTime = uF.getTimeFormat(strFrom, DBTIME);
							Time exitTime = uF.getTimeFormat(strTo, DBTIME);
							Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
	
							milliseconds1 = entryTime.getTime();
							milliseconds3a = currentTime.getTime();
	
							Time end = uF.getCurrentTime(CF.getStrTimeZone());
							if(getStrNewTime()!=null){
								end = uF.getTimeFormat(getStrNewTime(), CF.getStrReportTimeFormat());
							}
							
							dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3a));
							
							pst = con.prepareStatement("update roster_details set _to=?, actual_hours=?, attended=2 where emp_id=? and _date =? and service_id=?");
							pst.setTime(1, end);
							pst.setDouble(2, dbl);					
							pst.setInt(3, uF.parseToInt(strEmpId));
							pst.setDate(4, uF.getDateFormat(str, "yyyy-MM-ddHH:mm"));
							pst.setInt(5, nCurrServiceId);
//							System.out.println("15 pst ===>> " + pst);
							pst.execute();
							pst.close();		
//							System.out.println("strTo!=null else CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
							if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())) {
								CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked Off ", -2, getStrReason());
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
//		System.out.println("================================== end set clock entry =========================================");
	}

	
	
	
	
	private void setClockEntryShiftBase(UtilityFunctions uF) {
//		System.out.println("================================== set clock entry shift base=========================================");
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int DAY = cal.get(Calendar.DAY_OF_MONTH);
			
			int HOUR = cal.get(Calendar.HOUR_OF_DAY);
			int HOUR_A = cal.get(Calendar.HOUR_OF_DAY);
			int MINUTE_A = cal.get(Calendar.MINUTE);
			int MINUTE = cal.get(Calendar.MINUTE);
			
//			System.out.println("mode==>"+getStrMode());
//			System.out.println("getStrPrevMode==>"+getStrPrevMode()+"--istrue==>"+uF.parseToBoolean(getStrPrevMode()));
//			System.out.println("reason==>"+getStrReason());
//			System.out.println("strNotify==>"+getStrNotify());
//			System.out.println("strNewTime==>"+getStrNewTime());
			
			pst = con.prepareStatement(selectRosterDependent);
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("1 pst==>"+pst);
			rs = pst.executeQuery();
			boolean isRosterDependant = false;
			if(rs.next()){
				isRosterDependant = uF.parseToBoolean(rs.getString("is_roster"));
			}
			rs.close();
			pst.close();
//			System.out.println("isRosterDependant==>"+isRosterDependant);
			if(isRosterDependant){
				int nApproved = 0;
				if(uF.parseToInt(getStrApproval())==1) {
					nApproved = -2;
				}
				if(uF.parseToBoolean(getStrPrevMode())){
					if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")) {
						if(uF.parseToInt(getService()) > 0) {
//							System.out.println("------------------IN Prev if---------------------");
													
							pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//							System.out.println("3 pst ===>> " + pst);
							rs = pst.executeQuery();
							String strPrevRosterDate = null;
							String strPrevFrom = null;
							String strPrevTo = null;
							while (rs.next()) {
								strPrevFrom = rs.getString("_from");
								strPrevTo = rs.getString("_to");
								strPrevRosterDate = rs.getString("_date");
							}
							rs.close();
							pst.close();
							
							double dbl = 0.0d;
							if(strPrevFrom!=null && strPrevTo!=null) {
								Time entryPrevTime = uF.getTimeFormat(strPrevFrom, DBTIME);
								Time exitPrevTime = uF.getTimeFormat(strPrevTo, DBTIME);
								Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
								
								long milliseconds1 = entryPrevTime.getTime();
								long milliseconds2 = exitPrevTime.getTime();
								long milliseconds3 = currentTime.getTime();
								long diff = 0L;

								if(nApproved!=0){
									dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
									if(milliseconds1>milliseconds3){
										dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3, milliseconds1));
									}else{
										dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
									}
								}
//								System.out.println("dbl ===>> " + dbl);
							}
							
							pst = con.prepareStatement("INSERT INTO attendance_details  (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, service_id,user_location) VALUES (?,?,?,?, ?,?,?,?, ?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(strPrevRosterDate, DBDATE, DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(strPrevRosterDate, DBDATE, DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setString(4, strMode);
							pst.setString(5, getStrReason());
							pst.setDouble(6, nApproved);
							pst.setDouble(7, 0);
							pst.setInt(8, uF.parseToInt(getService()));
							pst.setString(9, "WEB");
//							System.out.println("4 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
							
							if((getStrRosterStartTime()==null || getStrRosterStartTime().trim().equals("") || getStrRosterStartTime().trim().equalsIgnoreCase("NULL")) || (getStrRosterEndTime() == null || getStrRosterEndTime().trim().equals("") || getStrRosterEndTime().trim().equalsIgnoreCase("NULL"))){
								String userlocation = CF.getEmpWlocationId(con, uF, strEmpId); 
								pst = con.prepareStatement("select wlocation_id,wlocation_start_time,wlocation_end_time from work_location_info where wlocation_id =?");
								pst.setInt(1, uF.parseToInt(userlocation));
//								System.out.println("5 pst ===>> " + pst);
								rs = pst.executeQuery();
								while (rs.next()) {
									setStrRosterStartTime(rs.getString("wlocation_start_time"));
									setStrRosterEndTime(rs.getString("wlocation_end_time"));
								}
								rs.close();
								pst.close();
							}
							
							Time start = uF.getCurrentTime(CF.getStrTimeZone());
							Time end = uF.getTimeFormat(getStrRosterEndTime(), CF.getStrReportTimeFormat());
							
							if(getStrRosterStartTime()!=null){
								start = uF.getTimeFormat(getStrRosterStartTime(), CF.getStrReportTimeFormat());
							}
							
							long lStart = 0;
							long lEnd = 0;
							if(start!=null && end!=null) {
								lStart = start.getTime();
								lEnd = end.getTime();
							}
//							System.out.println("getService()!=null start ===>> " + start+" -- lStart ===>> " + lStart);
//							System.out.println("getService()!=null end ===>> " + end+ " -- lEnd ===>> " + lEnd);
							
							pst = con.prepareStatement("INSERT INTO roster_details  (emp_id, _date, _from, _to, service_id, attended, actual_hours,shift_id,roster_weeklyoff_id) VALUES (?,?,?,?, ?,1,?,?, ?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getDateFormat(uF.getDateFormat(strPrevRosterDate, DBDATE, DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setTime(3, start);
							pst.setTime(4, end);
							pst.setInt(5, uF.parseToInt(getService()));
							pst.setDouble(6, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, lEnd)));
							pst.setInt(7, 1);
							pst.setInt(8, 1);
//							System.out.println("6 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
						} else {
//							System.out.println("------------------IN else---------------------");
							pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//							System.out.println("7 pst ===>> " + pst);
							rs = pst.executeQuery();
							String strPrevRosterDate = null;
							String strPrevFrom = null;
							String strPrevTo = null;
							int nPrevServiceId = 0;
							while (rs.next()) {
								strPrevFrom = rs.getString("_from");
								strPrevTo = rs.getString("_to");
								strPrevRosterDate = rs.getString("_date");
								nPrevServiceId = uF.parseToInt(rs.getString("service_id"));
							}
							rs.close();
							pst.close();
							
							if(nPrevServiceId > 0){
								double dbl = 0.0d;
								if(strPrevFrom!=null && strPrevTo!=null) {
									Time entryPrevTime = uF.getTimeFormat(strPrevFrom, DBTIME);
									Time exitPrevTime = uF.getTimeFormat(strPrevTo, DBTIME);
									Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
									
									long milliseconds1 = entryPrevTime.getTime();
									long milliseconds2 = exitPrevTime.getTime();
									long milliseconds3 = currentTime.getTime();
									long diff = 0L;
		
									if(nApproved!=0){
										dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
										if(milliseconds1>milliseconds3){
											dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3, milliseconds1));
										}else{
											dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
										}
									}
//									System.out.println("dbl ===>> " + dbl);
								}
								
								pst = con.prepareStatement("INSERT INTO attendance_details  (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, notify_time, new_time, service_id, user_location) VALUES (?,?,?,?, ?,?,?,?, ?,?,?)");
				 				pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(strPrevRosterDate, DBDATE, DBDATE) + "" + uF.getTimeFormat(HOUR + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
								pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(strPrevRosterDate, DBDATE, DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
								pst.setString(4, strMode);
								pst.setString(5, getStrReason());
								pst.setDouble(6, nApproved);
								pst.setDouble(7, dbl);
								pst.setInt(8, ((uF.parseToBoolean(getStrNotify()))?1:0));
								pst.setTime(9, uF.getTimeFormat(getStrNewTime(), CF.getStrReportTimeFormat()));
								pst.setInt(10, nPrevServiceId);
								pst.setString(11, "WEB");
//								System.out.println("8 pst ===>> " + pst);
								pst.execute();
								pst.close();
								
								pst = con.prepareStatement("update roster_details set attended=1 where emp_id=? and _date =? and service_id=?");
								pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setDate(2, uF.getDateFormat(uF.getDateFormat(strPrevRosterDate, DBDATE, DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
								pst.setInt(3, nPrevServiceId);
//								System.out.println("9 pst ===>> " + pst);
								pst.execute();
								pst.close();
								
							}
						}
					} else if(getStrMode()!=null && getStrMode().equalsIgnoreCase("OUT")) {
//						System.out.println("------------------OUT---------------------");
						
						pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//						System.out.println("1 pst ===>> " + pst);
						rs = pst.executeQuery();
						String strPrevRosterDate = null;
						String strPrevFrom = null;
						String strPrevTo = null;
						int nPrevServiceId = 0;
						while (rs.next()) {
							strPrevFrom = rs.getString("_from");
							strPrevTo = rs.getString("_to");
							strPrevRosterDate = rs.getString("_date");
							nPrevServiceId = uF.parseToInt(rs.getString("service_id"));
						}
						rs.close();
						pst.close();
						
						pst = con.prepareStatement("SELECT * FROM attendance_details where emp_id =? and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD')=? and in_out='IN'");
						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setDate(2, uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "" + DAY, "yyyyMMdd"));
						pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//						System.out.println("2 pst ===>> " + pst);
						rs = pst.executeQuery();
						long IN = 0;
						String strTime = null;
						while (rs.next()) {
							IN = uF.getTimeStamp(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
							strTime = rs.getString("in_out_timestamp");
						}
						rs.close();
						pst.close();
//						System.out.println("IN ===>> " + IN +" -- strTime ===>> " + strTime);
						
						double dblPrev = 0.0d;
						long millisecondsPrev3 = 0L;
						long millisecondsPrev1 = 0L;
						long millisecondsPrev2 = 0L;
						long millisecondsPrev3a = 0L;
					
						
						if(strPrevFrom!=null && strPrevTo!=null) {
							
//							Time entryPrevTime = uF.getTimeFormat(strPrevRosterDate+" "+strPrevFrom, DBTIMESTAMP);
//							Time exitPrevTime = uF.getTimeFormat(strPrevRosterDate+" "+strPrevTo, DBTIMESTAMP);
//							Time currentPrevTime = uF.getTimeFormat(strPrevRosterDate+" "+uF.getCurrentTime(strTimeZone)+"", DBTIMESTAMP);
							
							Time currentPrevTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
							Time entryPrevTime = uF.getTimeFormat(strPrevFrom, DBTIME);
							Time exitPrevTime = uF.getTimeFormat(strPrevTo, DBTIME);
							
//							System.out.println("entryPrevTime ===>> "+ entryPrevTime+"--exitPrevTime ===>> "+ exitPrevTime+"--currentPrevTime ===>> "+ currentPrevTime);
							millisecondsPrev1 = entryPrevTime.getTime();
							millisecondsPrev2 = exitPrevTime.getTime();
							millisecondsPrev3a = currentPrevTime.getTime();
//							System.out.println("millisecondsPrev1 ===>> "+ millisecondsPrev1+"--millisecondsPrev2 ===>> "+ millisecondsPrev2+"--millisecondsPrev3a ===>> "+ millisecondsPrev3a);
							
//							if(entryPrevTime.after(exitPrevTime)){
//								System.out.println("true===>> ");
//								millisecondsPrev2 += 60 * 60 * 24 * 1000; 	
//							}
//							System.out.println("millisecondsPrev2 ===>> "+ millisecondsPrev2+"--millisecondsPrev3 ===>> "+ millisecondsPrev3);
//							millisecondsPrev3  = uF.getTimeStamp(strPrevRosterDate+" "+uF.getCurrentTime(strTimeZone), DBTIMESTAMP).getTime();
							millisecondsPrev3  = uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+" "+uF.getCurrentTime(CF.getStrTimeZone()), DBTIMESTAMP).getTime();
//							System.out.println("millisecondsPrev3 ===>> "+ millisecondsPrev3);
							
							long diff = 0L;

							if(nApproved!=0){
								dblPrev = uF.parseToDouble(uF.getTimeDiffInHoursMins(millisecondsPrev2, millisecondsPrev3a));
								
								if(millisecondsPrev2>millisecondsPrev3a){
									dblPrev = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(millisecondsPrev3a, millisecondsPrev2));
								}else{
									dblPrev = uF.parseToDouble(uF.getTimeDiffInHoursMins(millisecondsPrev2, millisecondsPrev3a));
								}
								
							}
//							System.out.println("strPrevFrom!=null && strPrevTo!=null dblPrev ===>> " + dblPrev);
						} else {
							millisecondsPrev3  = uF.getTimeStamp(strPrevRosterDate+" "+uF.getCurrentTime(CF.getStrTimeZone()), DBTIMESTAMP).getTime();
//							System.out.println("strPrevFrom==null || strPrevTo==null millisecondsPrev3 ===>> " + millisecondsPrev3);
						}
						
						
						double dblPrevHoursWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(IN, millisecondsPrev3));
//						dblHoursWorked = CF.calculateTimeDeduction(con,dblHoursWorked);
//						System.out.println("dblPrevHoursWorked ===>> " + dblPrevHoursWorked);
						
//						String str = uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR + ":" + MINUTE, "HH:mm");
						String str = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone()), DBDATE, DBDATE) + "" + uF.getTimeFormat(HOUR + ":" + MINUTE, "HH:mm");
//						System.out.println("TIME===="+ str);
//						System.out.println("TIME Format===="+uF.getTimeStamp(str, "yyyy-MM-ddhh:mm"));
						
						if(strPrevTo!=null) {						
							pst = con.prepareStatement("INSERT INTO attendance_details  (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, hours_worked, notify_time, new_time, service_id, user_location) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setTimestamp(2, uF.getTimeStamp(str, "yyyy-MM-ddHH:mm"));
//							pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone()), DBDATE, DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setString(4, strMode);
							pst.setString(5, getStrReason());
							pst.setDouble(6, nApproved);
							pst.setDouble(7, dblPrev);
							pst.setDouble(8, dblPrevHoursWorked);
							pst.setInt(9, ((uF.parseToBoolean(getStrNotify()))?1:0));
							pst.setTime(10, uF.getTimeFormat(getStrNewTime(), CF.getStrReportTimeFormat()));
							pst.setInt(11, nPrevServiceId);
							pst.setString(12, "WEB");
//							System.out.println("3 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
							pst = con.prepareStatement("update roster_details set attended=2 where emp_id=? and _date =? and service_id=?");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getDateFormat(str, "yyyy-MM-ddHH:mm"));
							pst.setInt(3, nPrevServiceId);
//							System.out.println("4 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
						} else {
							pst = con.prepareStatement("INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, hours_worked, service_id, user_location) VALUES (?,?,?,?, ?,?,?,?, ?,?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setTimestamp(2, uF.getTimeStamp(str, "yyyy-MM-ddHH:mm"));
//							pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone()), DBDATE, DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setString(4, strMode);
							pst.setString(5, getStrReason());
							pst.setDouble(6, nApproved);
							pst.setDouble(7, 0);
							pst.setDouble(8, dblPrevHoursWorked);
							pst.setInt(9, nPrevServiceId);
							pst.setString(10, "WEB");
//							System.out.println("5 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
//							Time entryTime = uF.getTimeFormat(strPrevRosterDate+strPrevFrom, DBDATE+DBTIME);
//							Time currentTime = uF.getTimeFormat(uF.getPrevDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME);
							
							Time currentPrevTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
							Time entryPrevTime = uF.getTimeFormat(strPrevFrom, DBTIME);
							Time exitPrevTime = uF.getTimeFormat(strPrevTo, DBTIME);

							millisecondsPrev1 = entryPrevTime.getTime();
							millisecondsPrev3a = currentPrevTime.getTime();

							Time end = uF.getCurrentTime(CF.getStrTimeZone());
							if(getStrNewTime()!=null){
								end = uF.getTimeFormat(getStrNewTime(), CF.getStrReportTimeFormat());
							}
							
							dblPrev = uF.parseToDouble(uF.getTimeDiffInHoursMins(millisecondsPrev1, millisecondsPrev3a));
							
							pst = con.prepareStatement("update roster_details set _to=?, actual_hours=?, attended=2 where emp_id=? and _date =? and service_id=?");
							pst.setTime(1, end);
							pst.setDouble(2, dblPrev);					
							pst.setInt(3, uF.parseToInt(strEmpId));
							pst.setDate(4, uF.getDateFormat(str, "yyyy-MM-ddHH:mm"));
							pst.setInt(5, nPrevServiceId);
//							System.out.println("6 pst ===>> " + pst);
							pst.execute();
							pst.close();		
							
						}						
					}
				} else {
					
					if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")) {
						if(uF.parseToInt(getService()) > 0) {
//							System.out.println("------------------IN if---------------------");
													
							pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//							System.out.println("3 pst ===>> " + pst);
							rs = pst.executeQuery();
							String strRosterDate = null;
							String strFrom = null;
							String strTo = null;
							while (rs.next()) {
								strFrom = rs.getString("_from");
								strTo = rs.getString("_to");
								strRosterDate = rs.getString("_date");
							}
							rs.close();
							pst.close();
							
							double dbl = 0.0d;
							if(strFrom!=null && strTo!=null) {
								Time entryTime = uF.getTimeFormat(strFrom, DBTIME);
								Time exitTime = uF.getTimeFormat(strTo, DBTIME);
								Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
								
								long milliseconds1 = entryTime.getTime();
								long milliseconds2 = exitTime.getTime();
								long milliseconds3 = currentTime.getTime();
								long diff = 0L;

								if(nApproved!=0){
									dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
									if(milliseconds1>milliseconds3){
										dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3, milliseconds1));
									}else{
										dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
									}
								}
//								System.out.println("dbl ===>> " + dbl);
							}
							
							pst = con.prepareStatement("INSERT INTO attendance_details  (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, service_id,user_location) VALUES (?,?,?,?, ?,?,?,?, ?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setString(4, strMode);
							pst.setString(5, getStrReason());
							pst.setDouble(6, nApproved);
							pst.setDouble(7, 0);
							pst.setInt(8, uF.parseToInt(getService()));
							pst.setString(9, "WEB");
//							System.out.println("4 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
							
							if((getStrRosterStartTime()==null || getStrRosterStartTime().trim().equals("") || getStrRosterStartTime().trim().equalsIgnoreCase("NULL")) || (getStrRosterEndTime() == null || getStrRosterEndTime().trim().equals("") || getStrRosterEndTime().trim().equalsIgnoreCase("NULL"))){
								String userlocation = CF.getEmpWlocationId(con, uF, strEmpId); 
								pst = con.prepareStatement("select wlocation_id,wlocation_start_time,wlocation_end_time from work_location_info where wlocation_id =?");
								pst.setInt(1, uF.parseToInt(userlocation));
//								System.out.println("5 pst ===>> " + pst);
								rs = pst.executeQuery();
								while (rs.next()) {
									setStrRosterStartTime(rs.getString("wlocation_start_time"));
									setStrRosterEndTime(rs.getString("wlocation_end_time"));
								}
								rs.close();
								pst.close();
							}
							
							Time start = uF.getCurrentTime(CF.getStrTimeZone());
							Time end = uF.getTimeFormat(getStrRosterEndTime(), CF.getStrReportTimeFormat());
							
							if(getStrRosterStartTime()!=null){
								start = uF.getTimeFormat(getStrRosterStartTime(), CF.getStrReportTimeFormat());
							}
							
							long lStart = 0;
							long lEnd = 0;
							if(start!=null && end!=null) {
								lStart = start.getTime();
								lEnd = end.getTime();
							}
//							System.out.println("getService()!=null start ===>> " + start+" -- lStart ===>> " + lStart);
//							System.out.println("getService()!=null end ===>> " + end+ " -- lEnd ===>> " + lEnd);
							
							pst = con.prepareStatement("INSERT INTO roster_details  (emp_id, _date, _from, _to, service_id, attended, actual_hours) VALUES (?,?,?,?,?,1,?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getDateFormat(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setTime(3, start);
							pst.setTime(4, end);
							pst.setInt(5, uF.parseToInt(getService()));
							pst.setDouble(6, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, lEnd)));
//							System.out.println("6 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
//							System.out.println("CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
							if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())) {
								CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked On ", -1, getStrReason());
							}
						} else {
//							System.out.println("------------------IN else---------------------");
							pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//							System.out.println("7 pst ===>> " + pst);
							rs = pst.executeQuery();
							String strRosterDate = null;
							String strFrom = null;
							String strTo = null;
							int nCurrServiceId = 0;
							while (rs.next()) {
								strFrom = rs.getString("_from");
								strTo = rs.getString("_to");
								strRosterDate = rs.getString("_date");
								nCurrServiceId = uF.parseToInt(rs.getString("service_id"));
							}
							rs.close();
							pst.close();
							
							if(nCurrServiceId > 0){
								double dbl = 0.0d;
								if(strFrom!=null && strTo!=null) {
									Time entryTime = uF.getTimeFormat(strFrom, DBTIME);
									Time exitTime = uF.getTimeFormat(strTo, DBTIME);
									Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
									
									long milliseconds1 = entryTime.getTime();
									long milliseconds2 = exitTime.getTime();
									long milliseconds3 = currentTime.getTime();
									long diff = 0L;
		
									if(nApproved!=0){
										dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
										if(milliseconds1>milliseconds3){
											dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3, milliseconds1));
										}else{
											dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
										}
									}
//									System.out.println("dbl ===>> " + dbl);
								}
								
								pst = con.prepareStatement("INSERT INTO attendance_details  (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, notify_time, new_time, service_id, user_location) VALUES (?,?,?,?, ?,?,?,?, ?,?,?)");
				 				pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
								pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
								pst.setString(4, strMode);
								pst.setString(5, getStrReason());
								pst.setDouble(6, nApproved);
								pst.setDouble(7, dbl);
								pst.setInt(8, ((uF.parseToBoolean(getStrNotify()))?1:0));
								pst.setTime(9, uF.getTimeFormat(getStrNewTime(), CF.getStrReportTimeFormat()));
								pst.setInt(10, nCurrServiceId);
								pst.setString(11, "WEB");
//								System.out.println("8 pst ===>> " + pst);
								pst.execute();
								pst.close();
								
								pst = con.prepareStatement("update roster_details set attended=1 where emp_id=? and _date =? and service_id=?");
								pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setDate(2, uF.getDateFormat(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
								pst.setInt(3, nCurrServiceId);
//								System.out.println("9 pst ===>> " + pst);
								pst.execute();
								pst.close();
									
//								System.out.println("CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
								if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())){
									CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked On ", -1, getStrReason());
								}
							}
						}
					} else if(getStrMode()!=null && getStrMode().equalsIgnoreCase("OUT")) {
//						System.out.println("------------------OUT---------------------");
						
						pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//						System.out.println("10 pst ===>> " + pst);
						rs = pst.executeQuery();
						String strRosterDate = null;
						String strFrom = null;
						String strTo = null;
						int nCurrServiceId = 0;
						while (rs.next()) {
							strFrom = rs.getString("_from");
							strTo = rs.getString("_to");
							strRosterDate = rs.getString("_date");
							nCurrServiceId = uF.parseToInt(rs.getString("service_id"));
						}
						rs.close();
						pst.close();
						
						pst = con.prepareStatement("SELECT * FROM attendance_details where emp_id =? and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD')=? and in_out='IN'");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "" + DAY, "yyyyMMdd"));
//						System.out.println("11 pst ===>> " + pst);
						rs = pst.executeQuery();
						long IN = 0;
						String strTime = null;
						while (rs.next()) {
							IN = uF.getTimeStamp(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
							strTime = rs.getString("in_out_timestamp");
						}
						rs.close();
						pst.close();
//						System.out.println("IN ===>> " + IN +" -- strTime ===>> " + strTime);
						
						double dbl = 0.0d;
						long milliseconds3 = 0L;
						long milliseconds1 = 0L;
						long milliseconds2 = 0L;
						long milliseconds3a = 0L;
					
						
						if(strFrom!=null && strTo!=null) {
							
//							Time entryTime = uF.getTimeFormat(strRosterDate+strFrom, DBDATE+DBTIME);
//							Time exitTime = uF.getTimeFormat(strRosterDate+strTo, DBDATE+DBTIME);
//							Time currentTime = uF.getTimeFormat(uF.getCurrentDate(strTimeZone)+""+uF.getCurrentTime(strTimeZone)+"", DBDATE+DBTIME);
							
							Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
							Time entryTime = uF.getTimeFormat(strFrom, DBTIME);
							Time exitTime = uF.getTimeFormat(strTo, DBTIME);

							milliseconds1 = entryTime.getTime();
							milliseconds2 = exitTime.getTime();
							milliseconds3a = currentTime.getTime();
							
							
							if(entryTime.after(exitTime)){
								milliseconds2 += 60 * 60 * 24 * 1000; 	
							}
							milliseconds3  = uF.getTimeStamp(strRosterDate+" "+uF.getCurrentTime(strTimeZone), DBTIMESTAMP).getTime();
							
							long diff = 0L;

							if(nApproved!=0){
								dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds2, milliseconds3a));
								
								if(milliseconds2>milliseconds3a){
									dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3a, milliseconds2));
								}else{
									dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds2, milliseconds3a));
								}
								
							}
//							System.out.println("strFrom!=null && strTo!=null dbl ===>> " + dbl);
						} else {
							milliseconds3  = uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+" "+uF.getCurrentTime(CF.getStrTimeZone()), DBTIMESTAMP).getTime();
//							System.out.println("strFrom!=null && strTo!=null milliseconds3 ===>> " + milliseconds3);
						}
						
						
						double dblHoursWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(IN, milliseconds3));
//						dblHoursWorked = CF.calculateTimeDeduction(con,dblHoursWorked);
//						System.out.println("dblHoursWorked ===>> " + dblHoursWorked);
						
						String str = uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR + ":" + MINUTE, "HH:mm");
//						System.out.println("TIME===="+ str);
//						System.out.println("TIME Format===="+uF.getTimeStamp(str, "yyyy-MM-ddhh:mm"));
						
						if(strTo!=null) {						
							pst = con.prepareStatement("INSERT INTO attendance_details  (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, hours_worked, notify_time, new_time, service_id, user_location) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setTimestamp(2, uF.getTimeStamp(str, "yyyy-MM-ddHH:mm"));
							pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setString(4, strMode);
							pst.setString(5, getStrReason());
							pst.setDouble(6, nApproved);
							pst.setDouble(7, dbl);
							pst.setDouble(8, dblHoursWorked);
							pst.setInt(9, ((uF.parseToBoolean(getStrNotify()))?1:0));
							pst.setTime(10, uF.getTimeFormat(getStrNewTime(), CF.getStrReportTimeFormat()));
							pst.setInt(11, nCurrServiceId);
							pst.setString(12, "WEB");
//							System.out.println("12 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
							pst = con.prepareStatement("update roster_details set attended=2 where emp_id=? and _date =? and service_id=?");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getDateFormat(str, "yyyy-MM-ddHH:mm"));
							pst.setInt(3, nCurrServiceId);
//							System.out.println("13 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
//							System.out.println("strTo!=null CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
							
							if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())){
								CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked Off ", -2, getStrReason());
							}
						} else {
							pst = con.prepareStatement("INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, hours_worked, service_id,user_location) VALUES (?,?,?,?, ?,?,?,?, ?,?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setTimestamp(2, uF.getTimeStamp(str, "yyyy-MM-ddHH:mm"));
							pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setString(4, strMode);
							pst.setString(5, getStrReason());
							pst.setDouble(6, nApproved);
							pst.setDouble(7, 0);
							pst.setDouble(8, dblHoursWorked);
							pst.setInt(9, nCurrServiceId);
							pst.setString(10, "WEB");
//							System.out.println("14 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
//							Time entryTime = uF.getTimeFormat(strRosterDate+strFrom, DBDATE+DBTIME);
//							Time currentTime = uF.getTimeFormat(uF.getCurrentDate(strTimeZone)+""+uF.getCurrentTime(strTimeZone)+"", DBDATE+DBTIME);
							
							Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
							if(strFrom != null && !strFrom.equals("")) {
								Time entryTime = uF.getTimeFormat(strFrom, DBTIME);
								Time exitTime = uF.getTimeFormat(strTo, DBTIME);
	
								milliseconds1 = entryTime.getTime();
								milliseconds3a = currentTime.getTime();
							}
							
							Time end = uF.getCurrentTime(CF.getStrTimeZone());
							if(getStrNewTime()!=null){
								end = uF.getTimeFormat(getStrNewTime(), CF.getStrReportTimeFormat());
							}
							
							dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3a));
							
							pst = con.prepareStatement("update roster_details set _to=?, actual_hours=?, attended=2 where emp_id=? and _date =? and service_id=?");
							pst.setTime(1, end);
							pst.setDouble(2, dbl);					
							pst.setInt(3, uF.parseToInt(strEmpId));
							pst.setDate(4, uF.getDateFormat(str, "yyyy-MM-ddHH:mm"));
							pst.setInt(5, nCurrServiceId);
//							System.out.println("15 pst ===>> " + pst);
							pst.execute();
							pst.close();		
							
//							System.out.println("strTo!=null else CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
							if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())){
								CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked Off ", -2, getStrReason());
							}
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
//		System.out.println("================================== end set clock entry shift base=========================================");
	}
	

	public void setClockInOutEntry(UtilityFunctions uF) {
//		System.out.println("================================== set clock entry =========================================");
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
				int nEmpId = 0;
				int nEmpServiceId=0;
				
				pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id=eod.emp_id and eod.emp_id=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				rs = pst.executeQuery();
				while(rs.next()) {
					nEmpId = uF.parseToInt(rs.getString("emp_id"));
					String serviceids = rs.getString("service_id");
					if(serviceids!=null && !serviceids.equals("")) {
						String[] temp=serviceids.split(",");
						nEmpServiceId = uF.parseToInt(temp[1]);
					}
				}
				rs.close();
				pst.close(); 	
				
				if(nEmpId > 0 && nEmpServiceId > 0) {
					pst = con.prepareStatement("SELECT timezone_region, timezone_country1, timezone_country2 FROM employee_official_details eod, " +
						"work_location_info wl, timezones tz where tz.timezone_id = wl.timezone_id and wl.wlocation_id = eod.wlocation_id " +
						"and emp_id = ?");
					pst.setInt(1, nEmpId);
					rs = pst.executeQuery();
					String strTimeZone = null;
					boolean isTimeZone = false;
					while (rs.next()) {
						isTimeZone = true;
						strTimeZone = rs.getString("timezone_region")+"/"+rs.getString("timezone_country1")+((rs.getString("timezone_country2")!=null && rs.getString("timezone_country2").length()>1)?"/"+rs.getString("timezone_country2"):"");
					}
					rs.close();
					pst.close();
					
					if(!isTimeZone) {
						strTimeZone = "Asia/Calcutta";
					}
					
					String setStrDate = uF.getDateFormat(""+uF.getCurrentDate(strTimeZone), DBDATE, DATE_FORMAT);
					String setStrTime = uF.getTimeFormatStr(""+uF.getCurrentTime(strTimeZone), DBTIME, TIME_FORMAT);
					
					pst = con.prepareStatement("select wlocation_id,wlocation_start_time,wlocation_end_time from work_location_info " +
						"where wlocation_id in (select eod.wlocation_id from employee_personal_details epd, employee_official_details eod " +
						"where epd.emp_per_id=eod.emp_id and eod.emp_id=?)");
					pst.setInt(1, nEmpId);
					rs = pst.executeQuery();
					String locationstarttime = null;
					String locationendtime = null;
					while (rs.next()) {
						locationstarttime = rs.getString("wlocation_start_time");
						locationendtime = rs.getString("wlocation_end_time");
					}
					rs.close();
					pst.close();
					
					pst=con.prepareStatement("select * from attendance_details where emp_id=? and service_id=? and upper(in_out)=? and to_date(in_out_timestamp::text,'yyyy-MM-dd') =?");
					pst.setInt(1, nEmpId);
					pst.setInt(2, nEmpServiceId);
	//				pst.setString(3, elementData.getStrInOutMode().toUpperCase());
					pst.setString(3, "IN");
					pst.setDate(4, uF.getCurrentDate(strTimeZone));
	//				System.out.println("pst===>"+pst);
					rs = pst.executeQuery();
					boolean flag = false;
					Timestamp inTime = null;
					while(rs.next()) {
						flag = true;
						inTime = rs.getTimestamp("in_out_timestamp");
					}
					rs.close();
					pst.close();
					
					if(!flag) {
						pst=con.prepareStatement("select * from roster_details where emp_id=? and service_id=? and _date=?");
						pst.setInt(1, nEmpId);
						pst.setInt(2, nEmpServiceId);
						pst.setDate(3, uF.getCurrentDate(strTimeZone));
						rs = pst.executeQuery();
						boolean rosterFlag = false;
						String strFrom = null;
						String strTo = null;
						String strRosterDate = null;
						while(rs.next()) {
							rosterFlag = true;
							
							if(uF.parseToInt(rs.getString("service_id")) > 0) {
								nEmpServiceId = uF.parseToInt(rs.getString("service_id"));
							}
							
							strFrom = rs.getString("_from");
							strTo = rs.getString("_to");
							strRosterDate = uF.getDateFormat(rs.getString("_date"),DBDATE, DATE_FORMAT);
						}
						rs.close();
						pst.close();
						
						if(!rosterFlag) {
							StringBuilder sbRosterQuery=new StringBuilder();
							sbRosterQuery.append("insert into roster_details (emp_id, _date, _from, _to, isapproved, user_id, service_id, actual_hours, " +
								"attended,is_lunch_ded,shift_id,entry_date) " +
								"values(?,?,?,?,?,(select user_id from user_details where emp_id=?),?,?,?,?,?,?)");
							pst = con.prepareStatement(sbRosterQuery.toString()); 
							Time startTime = uF.getTimeFormat(locationstarttime, DBTIME);
							long long_startTime = startTime.getTime();
	
							Time endTime = uF.getTimeFormat(locationendtime, DBTIME);
							long long_endTime = endTime.getTime();
	
							double total_time = uF.parseToDouble(uF.getTimeDiffInHoursMins(long_startTime,long_endTime));
							pst.setInt(1, nEmpId);
							pst.setDate(2,  uF.getCurrentDate(strTimeZone));
							pst.setTime(3, startTime);
							pst.setTime(4, endTime);
							pst.setBoolean(5, true);
							pst.setInt(6, 1);
							pst.setInt(7, nEmpServiceId);
							pst.setDouble(8, total_time);
							pst.setInt(9, 0);
							pst.setBoolean(10, false);
							pst.setInt(11, 0);
							pst.setDate(12, uF.getCurrentDate(strTimeZone));		
							pst.execute();
							pst.close();
							
							strFrom = locationstarttime;
							strTo = locationstarttime;
							strRosterDate = setStrDate;
						}
	//					System.out.println("in date time===>"+uF.getTimeStamp(elementData.getStrDate() + elementData.getStrTime(), DATE_FORMAT + TIME_FORMAT));
						String strMessage = null;
						int nApproved = 0;
						double dbl = 0.0d;
						
						pst=con.prepareStatement("INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual, hours_worked, " +
							"in_out, service_id,approval_reason,early_late,reason,user_location) VALUES (?," +
							"to_timestamp('"+uF.getTimeStamp(strRosterDate + strFrom, DATE_FORMAT + TIME_FORMAT)+"', 'yyyy-MM-dd HH24:MI:ss')," +
							"to_timestamp('"+uF.getTimeStamp(strRosterDate + strFrom, DATE_FORMAT + TIME_FORMAT)+"', 'yyyy-MM-dd HH24:MI:ss'),?, ?,?,?,?, ?,?)");
						pst.setInt(1, nEmpId);
						pst.setDouble(2, 0.0d);
	//					pst.setString(3, elementData.getStrInOutMode().toUpperCase());
						pst.setString(3, "IN");
						pst.setInt(4, nEmpServiceId);
						pst.setString(5, "");
						pst.setDouble(6, dbl);
						pst.setString(7, nApproved==-2 ? "System driven exception." : null);
						pst.setString(8, "WEB");
//						System.out.println("in pst===>"+pst);
						int x = pst.executeUpdate();
					    pst.close();
					    
					    pst = con.prepareStatement("INSERT INTO attendance_punch_in_out_details (emp_id, punch_date_time, punch_date, punch_time, punch_mode, " +
							"punch_entry_mode) VALUES (?," +
							"to_timestamp('"+uF.getTimeStamp(strRosterDate + strFrom, DATE_FORMAT + TIME_FORMAT)+"', 'yyyy-MM-dd HH24:MI:ss')," +
							"?,?, ?,?)");
						pst.setInt(1, nEmpId);
						pst.setDate(2, uF.getDateFormat(strRosterDate, DATE_FORMAT));
						pst.setTime(3, uF.getTimeFormat(setStrTime, TIME_FORMAT));
						pst.setString(4, "IN");
						pst.setString(5, "MANUAL");
//						System.out.println("in pst===>"+pst);
						pst.executeUpdate();
					    pst.close();
					    
					    if(x > 0) {
					    	pst = con.prepareStatement("update roster_details set attended=1 where emp_id=? and service_id=? and _date=?");
					    	pst.setInt(1, nEmpId);
							pst.setInt(2, nEmpServiceId);
							pst.setDate(3, uF.getCurrentDate(strTimeZone));
							pst.execute();
							pst.close();
							
							if(nApproved==-2) {
								pst = con.prepareStatement("insert into exception_reason (emp_id, given_reason, _date, in_out_type, service_id) values (?,?,?,?,?) ");
								pst.setInt(1, nEmpId);
								pst.setString(2, "System driven exception.");
								pst.setDate(3, uF.getCurrentDate(strTimeZone));
								pst.setString(4, "IN");
								pst.setInt(5, nEmpServiceId);
								int y = pst.executeUpdate();
								pst.close();
								
							}

		//					System.out.println("out date time===>"+uF.getTimeStamp(elementData.getStrDate() + elementData.getStrTime(), DATE_FORMAT + TIME_FORMAT));
							pst=con.prepareStatement("select * from attendance_details where emp_id=? and service_id=? and upper(in_out)=? and to_date(in_out_timestamp::text,'yyyy-MM-dd') =?");
							pst.setInt(1, nEmpId);
							pst.setInt(2, nEmpServiceId);
							pst.setString(3, "OUT");
							pst.setDate(4, uF.getCurrentDate(strTimeZone));
		//					System.out.println("out select pst===>"+pst);
							rs = pst.executeQuery();
							boolean outflag = false;
							while(rs.next()) {
								outflag = true;
							}
							rs.close();
							pst.close();
					
							if(!outflag) {
								pst=con.prepareStatement("select * from roster_details where emp_id=? and service_id=? and _date=?");
								pst.setInt(1, nEmpId);
								pst.setInt(2, nEmpServiceId);
								pst.setDate(3, uF.getCurrentDate(strTimeZone));
								rs = pst.executeQuery();
								while(rs.next()) {
									if(uF.parseToInt(rs.getString("service_id")) > 0) {
										nEmpServiceId = uF.parseToInt(rs.getString("service_id"));
									}
									
									strFrom = rs.getString("_from");
									strTo = rs.getString("_to");
									strRosterDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
								}
								rs.close();
								pst.close();
								
								Timestamp outTime = uF.getTimeStamp(strRosterDate + strTo, DATE_FORMAT + TIME_FORMAT);
								if(inTime == null) {
									inTime = uF.getTimeStamp(strRosterDate + strFrom, DATE_FORMAT + TIME_FORMAT);
								}
								long from = inTime.getTime();
								long to = outTime.getTime();
								
								double dblHoursWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(from, to));
								pst=con.prepareStatement("INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual, hours_worked, " +
									"in_out, service_id,approval_reason,early_late,reason,user_location) VALUES (?," +
									"to_timestamp('"+uF.getTimeStamp(strRosterDate + strTo, DATE_FORMAT + TIME_FORMAT)+"', 'yyyy-MM-dd HH24:MI:ss')," +
									"to_timestamp('"+uF.getTimeStamp(strRosterDate + strTo, DATE_FORMAT + TIME_FORMAT)+"', 'yyyy-MM-dd HH24:MI:ss'),?," +
									" ?,?,?,?, ?,?)");
								pst.setInt(1, nEmpId);
								pst.setDouble(2, dblHoursWorked);
								pst.setString(3, "OUT");
								pst.setInt(4, nEmpServiceId);
								pst.setString(5, "");
								pst.setDouble(6, dbl);
								pst.setString(7, nApproved ==-2 ? "System driven exception." : null);
								pst.setString(8, "WEB");
		//						System.out.println("out pst===>"+pst);
								x = pst.executeUpdate();
							    pst.close();
							    
							    pst=con.prepareStatement("INSERT INTO attendance_punch_in_out_details (emp_id, punch_date_time, punch_date, punch_time, punch_mode, " +
									"punch_entry_mode) VALUES (?," +
									"to_timestamp('"+uF.getTimeStamp(strRosterDate + strTo, DATE_FORMAT + TIME_FORMAT)+"', 'yyyy-MM-dd HH24:MI:ss')," +
									"?,?, ?,?)");
								pst.setInt(1, nEmpId);
								pst.setDate(2, uF.getDateFormat(strRosterDate, DATE_FORMAT));
								pst.setTime(3, uF.getTimeFormat(setStrTime, TIME_FORMAT));
								pst.setString(4, "OUT");
								pst.setString(5, "MANUAL");
		//						System.out.println("in pst===>"+pst);
								pst.executeUpdate();
							    pst.close();
							    
							    
							    if(x > 0) {
							    	pst = con.prepareStatement("update roster_details set attended=2 where emp_id=? and service_id=? and _date=?");
							    	pst.setInt(1, nEmpId);
									pst.setInt(2, nEmpServiceId);
									pst.setDate(3, uF.getCurrentDate(strTimeZone));
									pst.execute();
									pst.close();
									
									if(nApproved==-2) {
										pst = con.prepareStatement("insert into exception_reason (emp_id, given_reason, _date, in_out_type, service_id) values (?,?,?,?,?) ");
										pst.setInt(1, nEmpId);
										pst.setString(2, "System driven exception.");
										pst.setDate(3, uF.getCurrentDate(strTimeZone));
										pst.setString(4, "OUT");
										pst.setInt(5, nEmpServiceId);
										int y = pst.executeUpdate();
										pst.close();
										
									}
							    }
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
//		System.out.println(" ================================== end set clock entry ========================================= ");
	}
	
	
	
	
	public void setClockEntry(UtilityFunctions uF) {
//		System.out.println("================================== set clock entry =========================================");
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int DAY = cal.get(Calendar.DAY_OF_MONTH);
			
			int HOUR = cal.get(Calendar.HOUR_OF_DAY);
			int HOUR_A = cal.get(Calendar.HOUR_OF_DAY);
			int MINUTE_A = cal.get(Calendar.MINUTE);
			int MINUTE = cal.get(Calendar.MINUTE);

			boolean isCurrentRoster = new CommonFunctions(CF).isCurrentRostered(con,strEmpId);
//			System.out.println("isCurrentRoster==>"+isCurrentRoster);
			int nEmpServiceId = CF.getEmpServiceId(con,uF,strEmpId);
//			System.out.println("nEmpServiceId==>"+nEmpServiceId);
			
			boolean isSingleButtonClockOnOffFlag = false;
			if(uF.parseToInt(strEmpId) > 0) {
				pst = con.prepareStatement("select is_single_button_clock_on_off from user_details where emp_id=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
	//			System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					isSingleButtonClockOnOffFlag = rs.getBoolean("is_single_button_clock_on_off");
				}
				rs.close();
				pst.close();
			}
			
//			System.out.println("isSingleButtonClockOnOffFlag --------- ===>> " + isSingleButtonClockOnOffFlag);
			if(isSingleButtonClockOnOffFlag) {
//				insertTimeSingleButtonInOutAttendance(request, response, uF, elementData);
				setClockInOutEntry(uF);
			} else {
				pst = con.prepareStatement(selectRosterDependent);
				pst.setInt(1, uF.parseToInt(strEmpId));
	//			System.out.println("1 pst==>"+pst);
				rs = pst.executeQuery();
				boolean isRosterDependant = false;
				if(rs.next()) {
					isRosterDependant = uF.parseToBoolean(rs.getString("is_roster"));
				}
				rs.close();
				pst.close();
//				System.out.println("isRosterDependant==>"+isRosterDependant);
				if(isRosterDependant) {
					int nApproved = 0;
					if(uF.parseToInt(getStrApproval())==1) {
						nApproved = -2;
					}
//					System.out.println("getStrMode ====>>> " + getStrMode());
					if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")) {
						if(uF.parseToInt(getService()) > 0) {
//							System.out.println("------------------IN if---------------------");
							pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//							System.out.println("3 pst ===>> " + pst);
							rs = pst.executeQuery();
							String strRosterDate = null;
							String strFrom = null;
							String strTo = null;
							while (rs.next()) {
								strFrom = rs.getString("_from");
								strTo = rs.getString("_to");
								strRosterDate = rs.getString("_date");
							}
							rs.close();
							pst.close();
							
							double dbl = 0.0d;
							if(strFrom!=null && strTo!=null) {
								Time entryTime = uF.getTimeFormat(strFrom, DBTIME);
								Time exitTime = uF.getTimeFormat(strTo, DBTIME);
								Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
								
								long milliseconds1 = entryTime.getTime();
								long milliseconds2 = exitTime.getTime();
								long milliseconds3 = currentTime.getTime();
								long diff = 0L;
	
								if(nApproved!=0) {
									dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
									if(milliseconds1>milliseconds3) {
										dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3, milliseconds1));
									} else {
										dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
									}
								}
//								System.out.println("dbl ===>> " + dbl);
							}
							
							pst = con.prepareStatement("INSERT INTO attendance_details  (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, service_id,user_location) VALUES (?,?,?,?, ?,?,?,?, ?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setString(4, strMode);
							pst.setString(5, getStrReason());
							pst.setDouble(6, nApproved);
							pst.setDouble(7, 0);
							pst.setInt(8, uF.parseToInt(getService()));
							pst.setString(9, "WEB");
//							System.out.println("4 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
							
							if((getStrRosterStartTime()==null || getStrRosterStartTime().trim().equals("") || getStrRosterStartTime().trim().equalsIgnoreCase("NULL")) || (getStrRosterEndTime() == null || getStrRosterEndTime().trim().equals("") || getStrRosterEndTime().trim().equalsIgnoreCase("NULL"))){
								String userlocation = CF.getEmpWlocationId(con, uF, strEmpId); 
								pst = con.prepareStatement("select wlocation_id,wlocation_start_time,wlocation_end_time from work_location_info where wlocation_id =?");
								pst.setInt(1, uF.parseToInt(userlocation));
//								System.out.println("5 pst ===>> " + pst);
								rs = pst.executeQuery();
								while (rs.next()) {
									setStrRosterStartTime(rs.getString("wlocation_start_time"));
									setStrRosterEndTime(rs.getString("wlocation_end_time"));
								}
								rs.close();
								pst.close();
							}
							
							Time start = uF.getCurrentTime(CF.getStrTimeZone());
							Time end = uF.getTimeFormat(getStrRosterEndTime(), CF.getStrReportTimeFormat());
							
							if(getStrRosterStartTime()!=null){
								start = uF.getTimeFormat(getStrRosterStartTime(), CF.getStrReportTimeFormat());
							}
							
							long lStart = 0;
							long lEnd = 0;
							if(start!=null && end!=null) {
								lStart = start.getTime();
								lEnd = end.getTime();
							}
//							System.out.println("getService()!=null start ===>> " + start+" -- lStart ===>> " + lStart);
//							System.out.println("getService()!=null end ===>> " + end+ " -- lEnd ===>> " + lEnd);
							
							pst = con.prepareStatement("INSERT INTO roster_details  (emp_id, _date, _from, _to, service_id, attended, actual_hours) VALUES (?,?,?,?,?,1,?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getDateFormat(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setTime(3, start);
							pst.setTime(4, end);
							pst.setInt(5, uF.parseToInt(getService()));
							pst.setDouble(6, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, lEnd)));
//							System.out.println("6 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
//							System.out.println("CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
							if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())) {
								CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked On ", -1, getStrReason());
							}
						} else {
//							System.out.println("------------------IN else---------------------");
							pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//							System.out.println("7 pst ===>> " + pst);
							rs = pst.executeQuery();
							String strRosterDate = null;
							String strFrom = null;
							String strTo = null;
							int nCurrServiceId = 0;
							while (rs.next()) {
								strFrom = rs.getString("_from");
								strTo = rs.getString("_to");
								strRosterDate = rs.getString("_date");
								nCurrServiceId = uF.parseToInt(rs.getString("service_id"));
							}
							rs.close();
							pst.close();
							
							if(nCurrServiceId > 0) {
								double dbl = 0.0d;
								if(strFrom!=null && strTo!=null) {
									Time entryTime = uF.getTimeFormat(strFrom, DBTIME);
									Time exitTime = uF.getTimeFormat(strTo, DBTIME);
									Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
									
									long milliseconds1 = entryTime.getTime();
									long milliseconds2 = exitTime.getTime();
									long milliseconds3 = currentTime.getTime();
									long diff = 0L;
		
									if(nApproved!=0){
										dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
										if(milliseconds1>milliseconds3){
											dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3, milliseconds1));
										}else{
											dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
										}
									}
//									System.out.println("dbl ===>> " + dbl);
								}
								
								pst = con.prepareStatement("INSERT INTO attendance_details  (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, notify_time, new_time, service_id,user_location) VALUES (?,?,?,?, ?,?,?,?, ?,?,?)");
				 				pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
								pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
								pst.setString(4, strMode);
								pst.setString(5, getStrReason());
								pst.setDouble(6, nApproved);
								pst.setDouble(7, dbl);
								pst.setInt(8, ((uF.parseToBoolean(getStrNotify()))?1:0));
								pst.setTime(9, uF.getTimeFormat(getStrNewTime(), CF.getStrReportTimeFormat()));
								pst.setInt(10, nCurrServiceId);
								pst.setString(11, "WEB");
//								System.out.println("8 pst ===>> " + pst);
								pst.execute();
								pst.close();
								
								pst = con.prepareStatement("update roster_details set attended=1 where emp_id=? and _date =? and service_id=?");
								pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setDate(2, uF.getDateFormat(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
								pst.setInt(3, nCurrServiceId);
//								System.out.println("9 pst ===>> " + pst);
								pst.execute();
								pst.close();
									
//								System.out.println("CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
								if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())){
									CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked On ", -1, getStrReason());
								}
							}
						}
					} else if(getStrMode()!=null && getStrMode().equalsIgnoreCase("OUT")) {
//						System.out.println("------------------OUT---------------------");
						
						pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//						System.out.println("10 pst ===>> " + pst);
						rs = pst.executeQuery();
						String strRosterDate = null;
						String strFrom = null;
						String strTo = null;
						int nCurrServiceId = 0;
						while (rs.next()) {
							strFrom = rs.getString("_from");
							strTo = rs.getString("_to");
							strRosterDate = rs.getString("_date");
							nCurrServiceId = uF.parseToInt(rs.getString("service_id"));
						}
						rs.close();
						pst.close();
						
						pst = con.prepareStatement("SELECT * FROM attendance_details where emp_id =? and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD')=? and in_out='IN'");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "" + DAY, "yyyyMMdd"));
//						System.out.println("11 pst ===>> " + pst);
						rs = pst.executeQuery();
						long IN = 0;
						String strTime = null;
						while (rs.next()) {
							IN = uF.getTimeStamp(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
							strTime = rs.getString("in_out_timestamp");
						}
						rs.close();
						pst.close();
//						System.out.println("IN ===>> " + IN +" -- strTime ===>> " + strTime);
						
						double dbl = 0.0d;
						long milliseconds3 = 0L;
						long milliseconds1 = 0L;
						long milliseconds2 = 0L;
						long milliseconds3a = 0L;
					
						
						if(strFrom!=null && strTo!=null) {
	//						Time entryTime = uF.getTimeFormat(strRosterDate+strFrom, DBDATE+DBTIME);
	//						Time exitTime = uF.getTimeFormat(strRosterDate+strTo, DBDATE+DBTIME);
	//						Time currentTime = uF.getTimeFormat(uF.getCurrentDate(strTimeZone)+""+uF.getCurrentTime(strTimeZone)+"", DBDATE+DBTIME);
							
							Time entryTime = uF.getTimeFormat(strFrom, DBTIME);
							Time exitTime = uF.getTimeFormat(strTo, DBTIME);
							Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
	
							milliseconds1 = entryTime.getTime();
							milliseconds2 = exitTime.getTime();
							milliseconds3a = currentTime.getTime();
							
							
							if(entryTime.after(exitTime)){
								milliseconds2 += 60 * 60 * 24 * 1000; 	
							}
							milliseconds3  = uF.getTimeStamp(strRosterDate+" "+uF.getCurrentTime(CF.getStrTimeZone()), DBTIMESTAMP).getTime();
							
							long diff = 0L;
	
							if(nApproved!=0){
								dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds2, milliseconds3a));
								
								if(milliseconds2>milliseconds3a){
									dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3a, milliseconds2));
								}else{
									dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds2, milliseconds3a));
								}
								
							}
//							System.out.println("strFrom!=null && strTo!=null dbl ===>> " + dbl);
						} else {
							milliseconds3  = uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+" "+uF.getCurrentTime(CF.getStrTimeZone()), DBTIMESTAMP).getTime();
//							System.out.println("strFrom!=null && strTo!=null milliseconds3 ===>> " + milliseconds3);
						}
						
						
						double dblHoursWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(IN, milliseconds3));
	//					dblHoursWorked = CF.calculateTimeDeduction(con,dblHoursWorked);
//						System.out.println("dblHoursWorked ===>> " + dblHoursWorked);
						
						String str = uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR + ":" + MINUTE, "HH:mm");
//						System.out.println("TIME===="+ str);
//						System.out.println("TIME Format===="+uF.getTimeStamp(str, "yyyy-MM-ddhh:mm"));
						
						if(strTo!=null) {						
							pst = con.prepareStatement("INSERT INTO attendance_details  (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, hours_worked, notify_time, new_time, service_id,user_location) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setTimestamp(2, uF.getTimeStamp(str, "yyyy-MM-ddHH:mm"));
							pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setString(4, strMode);
							pst.setString(5, getStrReason());
							pst.setDouble(6, nApproved);
							pst.setDouble(7, dbl);
							pst.setDouble(8, dblHoursWorked);
							pst.setInt(9, ((uF.parseToBoolean(getStrNotify()))?1:0));
							pst.setTime(10, uF.getTimeFormat(getStrNewTime(), CF.getStrReportTimeFormat()));
							pst.setInt(11, nCurrServiceId);
							pst.setString(12, "WEB");
//							System.out.println("12 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
							pst = con.prepareStatement("update roster_details set attended=2 where emp_id=? and _date =? and service_id=?");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getDateFormat(str, "yyyy-MM-ddHH:mm"));
							pst.setInt(3, nCurrServiceId);
//							System.out.println("13 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
//							System.out.println("strTo!=null CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
							
							if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())){
								CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked Off ", -2, getStrReason());
							}
						} else {
							pst = con.prepareStatement("INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, hours_worked, service_id,user_location) VALUES (?,?,?,?, ?,?,?,?, ?,?)");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setTimestamp(2, uF.getTimeStamp(str, "yyyy-MM-ddHH:mm"));
							pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
							pst.setString(4, strMode);
							pst.setString(5, getStrReason());
							pst.setDouble(6, nApproved);
							pst.setDouble(7, 0);
							pst.setDouble(8, dblHoursWorked);
							pst.setInt(9, nCurrServiceId);
							pst.setString(10, "WEB");
//							System.out.println("14 pst ===>> " + pst);
							pst.execute();
							pst.close();
							
	//						Time entryTime = uF.getTimeFormat(strRosterDate+strFrom, DBDATE+DBTIME);
	//						Time currentTime = uF.getTimeFormat(uF.getCurrentDate(strTimeZone)+""+uF.getCurrentTime(strTimeZone)+"", DBDATE+DBTIME);
							
							Time entryTime = uF.getTimeFormat(strFrom, DBTIME);
							Time exitTime = uF.getTimeFormat(strTo, DBTIME);
							Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
	
							milliseconds1 = entryTime.getTime();
							milliseconds3a = currentTime.getTime();
	
							Time end = uF.getCurrentTime(CF.getStrTimeZone());
							if(getStrNewTime()!=null){
								end = uF.getTimeFormat(getStrNewTime(), CF.getStrReportTimeFormat());
							}
							
							dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3a));
							
							pst = con.prepareStatement("update roster_details set _to=?, actual_hours=?, attended=2 where emp_id=? and _date =? and service_id=?");
							pst.setTime(1, end);
							pst.setDouble(2, dbl);					
							pst.setInt(3, uF.parseToInt(strEmpId));
							pst.setDate(4, uF.getDateFormat(str, "yyyy-MM-ddHH:mm"));
							pst.setInt(5, nCurrServiceId);
//							System.out.println("15 pst ===>> " + pst);
							pst.execute();
							pst.close();		
							
//							System.out.println("strTo!=null else CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
							if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())){
								CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked Off ", -2, getStrReason());
							}
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
//		System.out.println("================================== end set clock entry =========================================");
	}
	
	
	
//	public void setClockEntry(){
//		
//		PreparedStatement pst = null, pst1 = null;
//		Database db = new Database();
//		db.setRequest(request);
//		Connection con = null;
//		ResultSet rs = null;
//		
//		try {
//			
//			UtilityFunctions uF = new UtilityFunctions();
//			con = db.makeConnection(con);
//			
//			int nPrevServiceId = 0;
//			int nCurrServiceId = 0;
//			int nCount = 0;
//			
//			pst = con.prepareStatement(selectRoster_N_COUNT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			
//			rs = pst.executeQuery();
//			while(rs.next()){
//				nCount = rs.getInt("cnt");
//			}
//			rs.close();
//			pst.close();
//			
////			System.out.println("nCount ===>> " + nCount);
//			
//			pst = con.prepareStatement(selectRosterClockDetails_PREV_N_OUT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			while(rs.next()){
//				nPrevServiceId = rs.getInt("service_id");
//			}
//			rs.close();
//			pst.close();
////			System.out.println("nPrevServiceId ===>> " + nPrevServiceId);
//			
//			pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			while(rs.next()){
//				nCurrServiceId = rs.getInt("service_id");
//			}
//			rs.close();
//			pst.close();
////			System.out.println("nCurrServiceId ===>> " + nCurrServiceId);
//			
////			log.debug(request.getContextPath()+ " : "+"pst nServiceId===>"+pst);
//			
//			if(nCurrServiceId==0) {
//				if(nCount>1){
//					pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//				}else{
//					pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				}
////				log.debug(request.getContextPath()+ " : "+"pst nServiceId===>"+pst);
////				System.out.println("pst nCurrServiceId == 0 in ===>> " +pst);
//				rs = pst.executeQuery();
//				while(rs.next()){
//					nCurrServiceId = rs.getInt("service_id");
//				}
//				rs.close();
//				pst.close();
////				System.out.println("nCurrServiceId == 0 in ===>> " +nCurrServiceId);
//			}
//			
//			
//			if(nCurrServiceId==0) {
//				pst = con.prepareStatement(selectRosterClockDetails_N2_IN);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
////				log.debug(request.getContextPath()+ " : "+"pst nServiceId===>"+pst);
////				System.out.println("pst nCurrServiceId == 0 in_2 ===>> " +pst);
//				rs = pst.executeQuery();
//				while(rs.next()){
//					nCurrServiceId = rs.getInt("service_id");
//				}
//				rs.close();
//				pst.close();
////				System.out.println("nCurrServiceId == 0 in_2 ===>> " +nCurrServiceId);
//			}
//			
//			if(nCurrServiceId==0) {
//				nCurrServiceId = nPrevServiceId;
//			}
////			System.out.println("nCurrServiceId ===>> " +nCurrServiceId);
//			
//			if(getService()!=null){
//				nCurrServiceId = uF.parseToInt(getService());
////				System.out.println("nCurrServiceId getService()!=null ===>> " +nCurrServiceId);
//			}
//			
////			log.debug(request.getContextPath()+ " : "+"nServiceId===>"+nCurrServiceId);
//			
//			
//			pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
//			pst.setString(2, "IN");
//			pst.setString(3, "OUT");
//			pst.setInt(4, uF.parseToInt(strEmpId));
//			pst.setInt(5, nPrevServiceId);
//
////			log.debug(request.getContextPath()+ " : "+" =======  22 C ======= ");
////			log.debug(request.getContextPath()+ " : "+"selectAttendenceClockDetailsInOut===>"+pst);
//			
//			rs = pst.executeQuery();
//			
//			boolean isPrevIn1 = false;
//			boolean isPrevOut1 = false;
//			
//			while(rs.next()){
//				
//				if(rs.getString("in_out").equalsIgnoreCase("IN")){
//					isPrevIn1 = true;
//				}else if(rs.getString("in_out").equalsIgnoreCase("OUT")){
//					isPrevOut1 = true;
//				} 
//			}
//			rs.close();
//			pst.close();
////			System.out.println("isPrevIn1 ===>> " + isPrevIn1 + " -- isPrevOut1 ===>> " + isPrevOut1);
//			
//			
//			pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N1);
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setString(2, "IN");
//			pst.setString(3, "OUT");
//			pst.setInt(4, uF.parseToInt(strEmpId));
//
////			log.debug(request.getContextPath()+ " : "+" =======  22 C ======= ");
////			log.debug(request.getContextPath()+ " : "+"selectAttendenceClockDetailsInOut===>"+pst);
//			
//			rs = pst.executeQuery();
//			
//			boolean isCurrIn1 = false;
//			boolean isCurrOut1 = false;
//			while(rs.next()) {
//				if(rs.getString("in_out").equalsIgnoreCase("IN")){
//					isCurrIn1 = true;
//				}else if(rs.getString("in_out").equalsIgnoreCase("OUT")){
//					isCurrOut1 = true;
//				} 
//			}
//			rs.close();
//			pst.close();
////			System.out.println("isCurrIn1 ===>> " + isCurrIn1 + " -- isCurrOut1 ===>> " + isCurrOut1);
//			
//			if(!isCurrIn1 && isPrevIn1 && !isPrevOut1){
//				nCurrServiceId = nPrevServiceId;
//			}
////			System.out.println("nCurrServiceId ===>> " +nCurrServiceId);
//			
//			
//			
//			if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")) {
//				if(nCount>1){
//					pst1 = con.prepareStatement(selectRosterClockDetails_N_IN);
//					pst1.setInt(1, uF.parseToInt(strEmpId));
//					pst1.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst1.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//				} else {
//					pst1 = con.prepareStatement(selectRosterClockDetails_N1_IN);
//					pst1.setInt(1, uF.parseToInt(strEmpId));
//					pst1.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				}
//				
//			}else{
//				pst1 = con.prepareStatement(selectRosterClockDetails_N_OUT);
//				pst1.setInt(1, uF.parseToInt(strEmpId));
//				pst1.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			}
////			System.out.println("pst1 ===>> " + pst1);
//			rs = pst1.executeQuery();
//			String strRosterDate = null;
//			String strFrom = null;
//			String strTo = null;
//			while (rs.next()) {
//				strFrom = rs.getString("_from");
//				strTo = rs.getString("_to");
//				strRosterDate = rs.getString("_date");
//			}
//			rs.close();
//			pst1.close();
////			System.out.println("strFrom ===>> " + strFrom);
////			System.out.println("strTo ===>> " + strTo);
////			System.out.println("strRosterDate ===>> " + strRosterDate);
//			
////			log.debug(request.getContextPath()+ " : "+"pst1=====>"+pst1);
////			log.debug(request.getContextPath()+ " : "+"strFrom=====>"+strFrom);
////			log.debug(request.getContextPath()+ " : "+"strTo=====>"+strTo);
////			log.debug(request.getContextPath()+ " : "+"strRosterDate=====>"+strRosterDate);
//			
//			
//			long from = 0L;
//			long to = 0L;
////			long current = uF.getTimeFormat(""+uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME, CF.getStrReportTimeFormat()), CF.getStrReportTimeFormat()).getTime();
//			long current = uF.getTimeFormat(""+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME ).getTime();
//			
//			if(strRosterDate!=null && strFrom!=null){
////				 from = uF.getTimeFormat(uF.getDateFormat(strRosterDate+strFrom, DBDATE+DBTIME, CF.getStrReportTimeFormat()), CF.getStrReportTimeFormat()).getTime();
//				from = uF.getTimeFormat(strRosterDate+strFrom, DBDATE+DBTIME ).getTime();
//			}
//			if(strRosterDate!=null && strTo!=null){
////				to = uF.getTimeFormat(""+uF.getDateFormat(strRosterDate+strTo, DBDATE+DBTIME, CF.getStrReportTimeFormat()), CF.getStrReportTimeFormat()).getTime();
//				to = uF.getTimeFormat(strRosterDate+strTo, DBDATE+DBTIME).getTime();
//			}
////			System.out.println(" from ===>> " + from + " -- to ===>> "  + to);
//			
//			long fromCurrent = 0;
//			long toCurrent = 0;
//			
//			if(from >= current && current>0 && from>0){
//				fromCurrent = uF.getTimeDifference(current, from);
//				fromCurrent = fromCurrent / 60000;
//			}
//			
//			if(to <= current && current>0 && to>0){
//				toCurrent = uF.getTimeDifference(to, current);
//				toCurrent = toCurrent / 60000;
//			}
////			System.out.println(" fromCurrent ===>> " + fromCurrent + " -- toCurrent ===>> "  + toCurrent);
//			
//			
////			log.debug(request.getContextPath()+ " : "+"to=====>"+to);
////			log.debug(request.getContextPath()+ " : "+"from=====>"+from);
////			log.debug(request.getContextPath()+ " : "+"current=====>"+current);
////			log.debug(request.getContextPath()+ " : "+"fromCurrent=====>"+fromCurrent);
////			log.debug(request.getContextPath()+ " : "+"toCurrent=====>"+toCurrent);
//			
//
////			Calendar cal = GregorianCalendar.getInstance();
//			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//			
////			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strRosterDate, DBDATE, "yyyy")));
////			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strRosterDate, DBDATE, "MM")) - 1 );
////			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(strRosterDate, DBDATE, "dd")));
//			
//			
//			int YEAR = cal.get(Calendar.YEAR);
//			int MONTH = cal.get(Calendar.MONTH) + 1;
//			int DAY = cal.get(Calendar.DAY_OF_MONTH);
//
//			
//			int HOUR = cal.get(Calendar.HOUR_OF_DAY);
//			int HOUR_A = cal.get(Calendar.HOUR_OF_DAY);
//			int MINUTE_A = cal.get(Calendar.MINUTE);
//			int MINUTE = cal.get(Calendar.MINUTE);
//
//			
//
////			pst = con.prepareStatement("SELECT * FROM attendance_details where emp_id =? and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD')=? and in_out='IN'");
//			pst = con.prepareStatement(selectAttendenceClockDetails_N);			
//			pst.setDate(1, uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "" + DAY, "yyyyMMdd"));
//			pst.setString(2, "IN");
//			pst.setInt(3, uF.parseToInt(strEmpId));
//			pst.setInt(4, nCurrServiceId);
////			System.out.println("pst =====>> " + pst);
//			rs = pst.executeQuery();
//			long IN = 0;
//			String strTime = null;
//			while (rs.next()) {
//				IN = uF.getTimeStamp(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
//				strTime = rs.getString("in_out_timestamp");
//			}
//			rs.close();
//			pst.close();
////			System.out.println("IN ===>> " + IN +" -- strTime ===>> " + strTime);
//			
//			Map hmTardy = CF.getTardyType(con);
////			System.out.println("hmTardy ===>> " + hmTardy);
//			int nTardyIn = uF.parseToInt((String)hmTardy.get("TARDY_IN"));
//			int nTardyOut = uF.parseToInt((String)hmTardy.get("TARDY_OUT"));
//			
//			int nApproved = 0;
//			int RoundOff = 30;
//			int mode = MINUTE % RoundOff;
//
//			
//			
////			System.out.println("strMode==>"+strMode);
////			System.out.println("nTardyIn==>"+nTardyIn);
////			System.out.println("fromCurrent==>"+fromCurrent);
//			
////			System.out.println("strMode ===>> " + strMode);
//			if (strMode != null && strMode.equalsIgnoreCase("IN") && IN == 0) {
////				System.out.println(" =============== strMode.equalsIgnoreCase(IN) ================== ");
//				if(Math.abs(nTardyIn)>=fromCurrent && fromCurrent>0) {
//					MINUTE = RoundOff - mode;
//					cal.add(Calendar.MINUTE, MINUTE);
//					HOUR = cal.get(Calendar.HOUR_OF_DAY);
//					MINUTE = cal.get(Calendar.MINUTE);
//					nApproved = 1;
//					
////					System.out.println("Approved ===>> " + nApproved);
//					
//				}
//				
////				System.out.println("========= getStrApproval()===> " + getStrApproval());
//				
//				if(uF.parseToInt(getStrApproval())==1) {
//					nApproved = -2;
//				}
////				System.out.println("Approved ===>> " + nApproved);
//				
//				/**
//				 * If the employee is not roster dependent then it will automatically mark it as 0 which does not require any 
//				 * approval 
//				 */
////				System.out.println("isRosterDependency ===>> " + CF.isRosterDependency(con,strEmpId));
//				if(!CF.isRosterDependency(con,strEmpId)) {
//					nApproved = 0;
//				}
//				
//				
//				if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")) {
//					if(nCount>1) {
//						pst1 = con.prepareStatement(selectRosterClockDetails_N_IN);
//						pst1.setInt(1, uF.parseToInt(strEmpId));
//						pst1.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst1.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//					}else{
//						pst1 = con.prepareStatement(selectRosterClockDetails_N1_IN);
//						pst1.setInt(1, uF.parseToInt(strEmpId));
//						pst1.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					}
//					
//				} else {
//					pst1 = con.prepareStatement(selectRosterClockDetails_N_OUT);
//					pst1.setInt(1, uF.parseToInt(strEmpId));
//					pst1.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					
//				}
//				System.out.println("pst1 ===>> " + pst1);
//				rs = pst1.executeQuery();
//
////				log.debug(request.getContextPath()+ " : "+"pst1="+pst1);
//				strRosterDate = null;
//				strFrom = null;
//				strTo = null;
//
//				while (rs.next()) {
//					strFrom = rs.getString("_from");
//					strTo = rs.getString("_to");
//					strRosterDate = rs.getString("_date");
//				}
//				rs.close();
//				pst1.close();
//
//				System.out.println("strFrom ===>> " + strFrom);
//				System.out.println("strTo ===>> " + strTo);
//				System.out.println("strRosterDate ===>> " + strRosterDate);
//				
//				double dbl = 0.0d;
//				if(strFrom!=null && strTo!=null) {
//					Time entryTime = uF.getTimeFormat(strFrom, DBTIME);
//					Time exitTime = uF.getTimeFormat(strTo, DBTIME);
//					Time currentTime = uF.getTimeFormat(uF.getCurrentTime(strTimeZone) + "", DBTIME);
//
//					
////					log.debug(request.getContextPath()+ " : "+"entryTime="+entryTime);
////					log.debug(request.getContextPath()+ " : "+"currentTime="+currentTime);
////					
////					log.debug(request.getContextPath()+ " : "+"entryTime="+entryTime);
////					log.debug(request.getContextPath()+ " : "+"currentTime="+currentTime);
//					
//					
//					long milliseconds1 = entryTime.getTime();
//					long milliseconds2 = exitTime.getTime();
//					long milliseconds3 = currentTime.getTime();
//					long diff = 0L;
//
//					if(nApproved!=0){
//						dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
//						if(milliseconds1>milliseconds3){
//							dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3, milliseconds1));
//						}else{
//							dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
//						}
//					}
//					System.out.println("dbl ===>> " + dbl);
//					
//					
////					log.debug(request.getContextPath()+ " : "+"dbl 11="+uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
////					log.debug(request.getContextPath()+ " : "+"dbl="+dbl);
//					
//					
//				}
//				
//				
////				log.debug(request.getContextPath()+ " : "+"dbl========>="+dbl);
//				
//				
////				log.debug(request.getContextPath()+ " : "+"getService()========>="+getService());
////				
////				log.debug(request.getContextPath()+ " : "+"HOUR_A========>="+HOUR_A);
////				log.debug(request.getContextPath()+ " : "+"MINUTE_A========>="+MINUTE_A);
////				log.debug(request.getContextPath()+ " : "+"MINUTE========>="+MINUTE);
//				System.out.println("getService() ===>> " + getService());
//				System.out.println("HOUR_A ===>> " + HOUR_A);
//				System.out.println("MINUTE_A ===>> " + MINUTE_A);
//				System.out.println("MINUTE ===>> " + MINUTE);
//				
//				if(getService()!=null) {
//					
//					int nServiceId = uF.parseToInt(getService());
//					if(nServiceId == 0){
//						pst = con.prepareStatement("SELECT * FROM employee_official_details eod, employee_personal_details epd " +
//								"WHERE epd.emp_per_id=eod.emp_id and eod.emp_id=? ");
//						pst.setInt(1, uF.parseToInt(strEmpId));
//						rs = pst.executeQuery();
//						while (rs.next()) {
//							if(uF.parseToInt(rs.getString("emp_id")) > 0 && rs.getString("service_id")!=null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equals("NULL")){
//								String[] tmp =  rs.getString("service_id").trim().split(",");
//								setService(tmp[1].trim());								
//							}
//						}
//						rs.close();
//						pst.close();
//					}
//					
//					pst = con.prepareStatement("INSERT INTO attendance_details  (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, service_id) VALUES (?,?,?,?,?,?,?,?)");
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
//					pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
//					pst.setString(4, strMode);
//					pst.setString(5, getStrReason());
//					pst.setDouble(6, nApproved);
//					pst.setDouble(7, 0);
//					pst.setInt(8, uF.parseToInt(getService()));
//					pst.execute();
//					pst.close();
//					System.out.println("getService()!=null pst ===>> " + pst);
////					log.debug(request.getContextPath()+ " : "+"MINUTE========>="+pst);
//					
//					if((getStrRosterStartTime()==null || getStrRosterStartTime().trim().equals("") || getStrRosterStartTime().trim().equalsIgnoreCase("NULL")) || (getStrRosterEndTime() == null || getStrRosterEndTime().trim().equals("") || getStrRosterEndTime().trim().equalsIgnoreCase("NULL"))){
//						Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
//						String userlocation = hmEmpLocation.get(strEmpId);
//
//						pst = con.prepareStatement("select wlocation_id,wlocation_start_time,wlocation_end_time from work_location_info where wlocation_id =?");
//						pst.setInt(1, uF.parseToInt(userlocation));
//						rs = pst.executeQuery();
//						while (rs.next()) {
//							setStrRosterStartTime(rs.getString("wlocation_start_time"));
//							setStrRosterEndTime(rs.getString("wlocation_end_time"));
//						}
//						rs.close();
//						pst.close();
//					}
//					
//					Time start = uF.getCurrentTime(CF.getStrTimeZone());
//					Time end = uF.getTimeFormat(getStrRosterEndTime(), CF.getStrReportTimeFormat());
//					
//					if(getStrRosterStartTime()!=null){
//						start = uF.getTimeFormat(getStrRosterStartTime(), CF.getStrReportTimeFormat());
//					}
//					
//					long lStart = 0;
//					long lEnd = 0;
//					if(start!=null && end!=null) {
//						lStart = start.getTime();
//						lEnd = end.getTime();
//					}
//					System.out.println("getService()!=null start ===>> " + start+" -- lStart ===>> " + lStart);
//					System.out.println("getService()!=null end ===>> " + end+ " -- lEnd ===>> " + lEnd);
//					
////					log.debug(request.getContextPath()+ " : "+"INSERT ===> "+pst);
//					
//					pst = con.prepareStatement("INSERT INTO roster_details  (emp_id, _date, _from, _to, service_id, attended, actual_hours) VALUES (?,?,?,?,?,1,?)");
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getDateFormat(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
////					pst.setTime(3, uF.getTimeFormat(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
//					pst.setTime(3, start);
//					pst.setTime(4, end);
//					pst.setInt(5, uF.parseToInt(getService()));
//					pst.setDouble(6, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, lEnd)));
//					pst.execute();
//					pst.close();
//					System.out.println("getService()!=null pst ===>> " + pst);
//					
////					log.debug(request.getContextPath()+ " : "+"INSERT ===> "+pst);
////					log.debug(request.getContextPath()+ " : "+"INSERT Roster ===> "+pst);
//					
//					System.out.println("CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
//					if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())) {
//						CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked On ", -1, getStrReason());
//					}
//					
//					
//				} else {
////					if(getStrNewTime()!=null && !getStrNewTime().trim().equals("") && !getStrNewTime().trim().equalsIgnoreCase("NULL")){
//						pst = con.prepareStatement("INSERT INTO attendance_details  (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, notify_time, new_time, service_id) VALUES (?,?,?,?,?,?,?,?,?,?)");
//		 				pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
//						pst.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
//						pst.setString(4, strMode);
//						pst.setString(5, getStrReason());
//						pst.setDouble(6, nApproved);
//						pst.setDouble(7, dbl);
//						pst.setInt(8, ((uF.parseToBoolean(getStrNotify()))?1:0));
//						pst.setTime(9, uF.getTimeFormat(getStrNewTime(), CF.getStrReportTimeFormat()));
//						pst.setInt(10, nCurrServiceId);
//	//					if(nCurrServiceId>0){
//						pst.execute();
//						pst.close();
//						System.out.println("getService()!=null else pst ===>> " + pst);	
//	//					}
//						
//						pst1 = con.prepareStatement("update roster_details set attended=1 where emp_id=? and _date =? and service_id=?");
//						pst1.setInt(1, uF.parseToInt(strEmpId));
//						pst1.setDate(2, uF.getDateFormat(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE, "HH:mm"), "yyyy-MM-ddHH:mm"));
//						pst1.setInt(3, nCurrServiceId);
//	//					if(nCurrServiceId>0){
//						pst1.execute();
//						pst1.close();
//						System.out.println("getService()!=null else pst1 ===>> " + pst1);
//	//					}
//							
//						System.out.println("CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
//						if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())){
//							CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked On ", -1, getStrReason());
//						}
////					}
//				}
//				
//				session.setAttribute("clock_inout_reminder", null);
//				
//			}
//			
//			
//			
//			nApproved = 0;
//			String strPrevRosterDate = null;
//			Time tPrevFrom = null;
//			Time tPrevTo = null;
//			
//			if (strMode != null && strMode.equalsIgnoreCase("OUT")) {
//				System.out.println(" =============== strMode.equalsIgnoreCase(OUT) ================== ");
//				if(Math.abs(nTardyOut) >= toCurrent && toCurrent>0){
//					MINUTE = RoundOff - mode;
//					cal.add(Calendar.MINUTE, -mode);
//					HOUR = cal.get(Calendar.HOUR_OF_DAY);
//					MINUTE = cal.get(Calendar.MINUTE);
//					nApproved = 1;
//					
//					System.out.println("Approved ===>> " + nApproved);
//				}
//				
//				
//				System.out.println("========= getStrApproval()===> " + getStrApproval());
//				
//				if(uF.parseToInt(getStrApproval())==1){
//					nApproved = -2;
//				}
//				
//				/**
//				 * If the employee is not roster dependent then it will automatically mark it as 0 which does not require any 
//				 * approval 
//				 */
//				
//				System.out.println("isRosterDependency ===>> " + CF.isRosterDependency(con,strEmpId));
//				if(!CF.isRosterDependency(con,strEmpId)){
//					nApproved = 0;
//				}
//				
//				
//				pst1 = con.prepareStatement(selectAttendenceClockDetails_N);
//				pst1.setDate(1, uF.getPrevDate(strTimeZone));
//				pst1.setString(2, strMode);
//				pst1.setInt(3, uF.parseToInt(strEmpId));
//				pst1.setInt(4, nCurrServiceId);
//				System.out.println("pst1 ===>> " + pst1);
//				rs = pst1.executeQuery();
//				boolean isPrevOut = false;
//				boolean isPrevRoster = false;
//				if(rs.next()) {
//					isPrevOut = true;
//				}
//				rs.close();
//				pst1.close();
//				System.out.println("isPrevOut ===>> " + isPrevOut);
////				log.debug(request.getContextPath()+ " : "+"pst1===>"+pst1);
//				
//				pst1 = con.prepareStatement(selectAttendenceClockDetails_N);
//				pst1.setDate(1, uF.getPrevDate(strTimeZone));
//				pst1.setString(2, "IN");
//				pst1.setInt(3, uF.parseToInt(strEmpId));
//				pst1.setInt(4, nCurrServiceId);
//				System.out.println("pst1 ===>> " + pst1);
//				rs = pst1.executeQuery();
//				boolean isPrevIn = false;
//				if(rs.next()){
//					isPrevIn = true;
//				}
//				rs.close();
//				pst1.close();
//				System.out.println("isPrevIn ===>> " + isPrevIn);
//				
//				
////				log.debug(request.getContextPath()+ " : "+"pst1 2 ===>"+pst1);
//				
//				if(!isPrevOut && isPrevIn){
//					if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")){
//						pst1 = con.prepareStatement(selectRosterClockDetails_N_IN);
//						pst1.setInt(1, uF.parseToInt(strEmpId));
//						pst1.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//						pst1.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//					}else{
//						pst1 = con.prepareStatement(selectRosterClockDetails_N_OUT);
//						pst1.setInt(1, uF.parseToInt(strEmpId));
//						pst1.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//					}
//					System.out.println("!isPrevOut && isPrevIn pst1 =====>> " + pst1);
////					log.debug(request.getContextPath()+ " : "+"pst1 3 ===>"+pst1);
//					
//					rs = pst1.executeQuery();
//					if(rs.next()){
//						isPrevRoster = true;
//						
//						tPrevFrom = rs.getTime("_from");
//						tPrevTo = rs.getTime("_to");
//						strPrevRosterDate = rs.getString("_date");						
//					}
//					rs.close();
//					pst1.close();
//					System.out.println("!isPrevOut && isPrevIn tPrevFrom =====>> " + tPrevFrom +" -- tPrevTo ===>> " + tPrevTo +" -- strPrevRosterDate ===>> " + strPrevRosterDate); 
//				}
//				
//				if(tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() < tPrevTo.getTime()) {
//					
//					if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")){
//						pst1 = con.prepareStatement(selectRosterClockDetails_N_IN);
//						pst1.setInt(1, uF.parseToInt(strEmpId));
//						pst1.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//						pst1.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//					}else{
//						pst1 = con.prepareStatement(selectRosterClockDetails_N_OUT);
//						pst1.setInt(1, uF.parseToInt(strEmpId));
//						pst1.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//						
//					}
//					System.out.println("tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() < tPrevTo.getTime() pst1 ===>> " + pst1);
//					rs = pst1.executeQuery();
//					strRosterDate = null;
//					strFrom = null;
//					strTo = null;
//					while (rs.next()) {
//						strFrom = rs.getString("_from");
//						strTo = rs.getString("_to");
//						strRosterDate = rs.getString("_date");						
//					}
//					rs.close();
//					pst1.close();
//					System.out.println("tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() < tPrevTo.getTime() strFrom ===>> " + strFrom +" -- strTo ===>> " + strTo + " -- strRosterDate ===>> " + strRosterDate);
//					
//				} else if(tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() > tPrevTo.getTime()) {
//					if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")){
//						pst1 = con.prepareStatement(selectRosterClockDetails_N_IN);
//						pst1.setInt(1, uF.parseToInt(strEmpId));
//						pst1.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//						pst1.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//					}else{
//						pst1 = con.prepareStatement(selectRosterClockDetails_N_OUT);
//						pst1.setInt(1, uF.parseToInt(strEmpId));
//						pst1.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//						
//					}
//					System.out.println("tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() > tPrevTo.getTime() pst1 ===>> " + pst1);
//					rs = pst1.executeQuery();
//
//					strRosterDate = null;
//					strFrom = null;
//					strTo = null;
//
//					while (rs.next()) {
//						strFrom = rs.getString("_from");
//						strTo = rs.getString("_to");
//						strRosterDate = rs.getString("_date");
//					}
//					rs.close();
//					pst1.close();
//					System.out.println("tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() > tPrevTo.getTime() strFrom ===>> " + strFrom +" -- strTo ===>> " + strTo + " -- strRosterDate ===>> " + strRosterDate);
//				} else {
//					if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")) {
//						if(nCount>1){
//							pst1 = con.prepareStatement(selectRosterClockDetails_N_IN);
//							pst1.setInt(1, uF.parseToInt(strEmpId));
//							pst1.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//							pst1.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//						}else{
//							pst1 = con.prepareStatement(selectRosterClockDetails_N1_IN);
//							pst1.setInt(1, uF.parseToInt(strEmpId));
//							pst1.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//							pst1.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//						}
//						
//					}else{
//						pst1 = con.prepareStatement(selectRosterClockDetails_N_OUT);
//						pst1.setInt(1, uF.parseToInt(strEmpId));
//						pst1.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//						
//					}
//					System.out.println("pst1 ===>> " + pst1);
//					rs = pst1.executeQuery();
//					
////					System.out.println("======+FROM   TO===>"+pst1);
//					strRosterDate = null;
//					strFrom = null;
//					strTo = null;
//					while (rs.next()) {
//						strFrom = rs.getString("_from");
//						strTo = rs.getString("_to");
//						strRosterDate = rs.getString("_date");						
//					}
//					rs.close();
//					pst1.close();
//					System.out.println("strFrom ===>> " + strFrom +" -- strTo ===>> " + strTo + " -- strRosterDate ===>> " + strRosterDate);
//				}
//				
////				System.out.println("strFrom==>"+strFrom);
////				System.out.println("strTo==>"+strTo);
//				
//				if(strRosterDate!=null) {
//					
//					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strRosterDate, DBDATE, "yyyy")));
//					cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strRosterDate, DBDATE, "MM")) - 1 );
//					cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(strRosterDate, DBDATE, "dd")));
//					
//					YEAR = cal.get(Calendar.YEAR);
//					MONTH = cal.get(Calendar.MONTH) + 1;
//					DAY = cal.get(Calendar.DAY_OF_MONTH);
//				}	
//					
//					pst = con.prepareStatement("SELECT * FROM attendance_details where emp_id =? and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD')=? and in_out='IN'");
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "" + DAY, "yyyyMMdd"));
//					rs = pst.executeQuery();
//					System.out.println("pst ===>> " + pst);
//					IN = 0;
//					strTime = null;
//					while (rs.next()) {
//						IN = uF.getTimeStamp(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
//						strTime = rs.getString("in_out_timestamp");
//					}
//					rs.close();
//					pst.close();
//					System.out.println("IN ===>> " + IN +" -- strTime ===>> " + strTime);
//				
//				
//				double dbl = 0.0d;
//				long milliseconds3 = 0L;
//				long milliseconds1 = 0L;
//				long milliseconds2 = 0L;
//				long milliseconds3a = 0L;
//			
//				
//				if(strFrom!=null && strTo!=null) {
//					
//					Time entryTime = uF.getTimeFormat(strRosterDate+strFrom, DBDATE+DBTIME);
//					Time exitTime = uF.getTimeFormat(strRosterDate+strTo, DBDATE+DBTIME);
//					Time currentTime = uF.getTimeFormat(uF.getCurrentDate(strTimeZone)+""+uF.getCurrentTime(strTimeZone)+"", DBDATE+DBTIME);
//
//					milliseconds1 = entryTime.getTime();
//					milliseconds2 = exitTime.getTime();
//					milliseconds3a = currentTime.getTime();
//					
//					
//					if(entryTime.after(exitTime)){
//						milliseconds2 += 60 * 60 * 24 * 1000; 	
//					}
//					
//					log.debug(request.getContextPath()+ " : "+exitTime+" strRosterDate="+strRosterDate);
//					
////					long milliseconds3  = uF.getTimeStamp(uF.getCurrentDate()+" "+uF.getCurrentTime(), "yyyy-MM-dd hh:mm:ss").getTime();
//					milliseconds3  = uF.getTimeStamp(strRosterDate+" "+uF.getCurrentTime(strTimeZone), DBTIMESTAMP).getTime();
//					
//					long diff = 0L;
//
//					if(nApproved!=0){
//						dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds2, milliseconds3a));
//						
//						if(milliseconds2>milliseconds3a){
//							dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3a, milliseconds2));
//						}else{
//							dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds2, milliseconds3a));
//						}
//						
//					}
//					System.out.println("strFrom!=null && strTo!=null dbl ===>> " + dbl);
//				} else {
//					milliseconds3  = uF.getTimeStamp(uF.getCurrentDate(strTimeZone)+" "+uF.getCurrentTime(strTimeZone), DBTIMESTAMP).getTime();
//					System.out.println("strFrom!=null && strTo!=null milliseconds3 ===>> " + milliseconds3);
//				}
//				
//				
//				double dblHoursWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(IN, milliseconds3));
////				dblHoursWorked = CF.calculateTimeDeduction(con,dblHoursWorked);
//				System.out.println("dblHoursWorked ===>> " + dblHoursWorked);
//				
//				String str = uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR + ":" + MINUTE, "HH:mm");
//				System.out.println("TIME===="+ str);
//				System.out.println("TIME Format===="+uF.getTimeStamp(str, "yyyy-MM-ddhh:mm"));
////				log.debug(request.getContextPath()+ " : "+"TIME===="+ str);
////				log.debug(request.getContextPath()+ " : "+"TIME Format===="+uF.getTimeStamp(str, "yyyy-MM-ddhh:mm"));
//				
//				
//				   
//				if(strTo!=null) {
//					
//					pst1 = con.prepareStatement("INSERT INTO attendance_details  (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, hours_worked, notify_time, new_time, service_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
//					pst1.setInt(1, uF.parseToInt(strEmpId));
//					pst1.setTimestamp(2, uF.getTimeStamp(str, "yyyy-MM-ddHH:mm"));
//					pst1.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
//					pst1.setString(4, strMode);
//					pst1.setString(5, getStrReason());
//					pst1.setDouble(6, nApproved);
//					pst1.setDouble(7, dbl);
//					pst1.setDouble(8, dblHoursWorked);
//					pst1.setInt(9, ((uF.parseToBoolean(getStrNotify()))?1:0));
//					pst1.setTime(10, uF.getTimeFormat(getStrNewTime(), CF.getStrReportTimeFormat()));
//					pst1.setInt(11, nCurrServiceId);
//					pst1.execute();
//					pst1.close();
//					System.out.println("strTo!=null pst1 ===>> " + pst1);
//					
////					System.out.println("pst1=   111   =="+pst1);
//					
//					pst1 = con.prepareStatement("update roster_details set attended=2 where emp_id=? and _date =? and service_id=?");
//					pst1.setInt(1, uF.parseToInt(strEmpId));
//					pst1.setDate(2, uF.getDateFormat(str, "yyyy-MM-ddHH:mm"));
//					pst1.setInt(3, nCurrServiceId);
////					if(nCurrServiceId>0){
//					pst1.execute();
//					pst1.close();
//					System.out.println("strTo!=null pst1 ===>> " + pst1);
////					}
//					
//					System.out.println("strTo!=null CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
//					if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())){
//						CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked Off ", -2, getStrReason());
//					}
//					CF.updateLeaveRegisterForCompensatoryLeaves(con, CF, uF, uF.getDateFormat(str, "yyyy-MM-ddHH:mm", DATE_FORMAT), strEmpId);	
//				} else {
//					pst1 = con.prepareStatement("INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual, in_out, reason, approved, early_late, hours_worked, service_id) VALUES (?,?,?,?,?,?,?,?,?)");
//					pst1.setInt(1, uF.parseToInt(strEmpId));
//					pst1.setTimestamp(2, uF.getTimeStamp(str, "yyyy-MM-ddHH:mm"));
//					pst1.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + ((DAY < 10) ? "0" + DAY : DAY), "yyyyMMdd", DBDATE) + "" + uF.getTimeFormat(HOUR_A + ":" + MINUTE_A, "HH:mm"), "yyyy-MM-ddHH:mm"));
//					pst1.setString(4, strMode);
//					pst1.setString(5, getStrReason());
//					pst1.setDouble(6, nApproved);
//					pst1.setDouble(7, 0);
//					pst1.setDouble(8, dblHoursWorked);
//					pst1.setInt(9, nCurrServiceId);
//					
////					if(nCurrServiceId>0){
//					pst1.execute();
//					pst1.close();
//					System.out.println("strTo!=null else pst1 ===>> " + pst1);
////					}
//					
//					System.out.println("strTo!=null else CF.getStrAttendanceIntegratedWithActivity() ===>> " + CF.getStrAttendanceIntegratedWithActivity());
//					if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())){
//						CF.clockOnOffEntryForActivity(con, uF, CF, strEmpId, "Clocked Off ", -2, getStrReason());
//					}
//					
//					
//					Time entryTime = uF.getTimeFormat(strRosterDate+strFrom, DBDATE+DBTIME);
//					Time currentTime = uF.getTimeFormat(uF.getCurrentDate(strTimeZone)+""+uF.getCurrentTime(strTimeZone)+"", DBDATE+DBTIME);
//
//					milliseconds1 = entryTime.getTime();
//					milliseconds3a = currentTime.getTime();
//
//					Time end = uF.getCurrentTime(CF.getStrTimeZone());
//					if(getStrNewTime()!=null){
//						end = uF.getTimeFormat(getStrNewTime(), CF.getStrReportTimeFormat());
//					}
//					
//					dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3a));
//					
//					pst1 = con.prepareStatement("update roster_details set _to=?, actual_hours=?, attended=2 where emp_id=? and _date =? and service_id=?");
////					pst1.setTime(1, uF.getTimeFormat(str, "yyyy-MM-ddHH:mm"));
//					pst1.setTime(1, end);
//					pst1.setDouble(2, dbl);					
//					pst1.setInt(3, uF.parseToInt(strEmpId));
//					pst1.setDate(4, uF.getDateFormat(str, "yyyy-MM-ddHH:mm"));
//					pst1.setInt(5, nCurrServiceId);
////					if(nCurrServiceId>0){
//					pst1.execute();
//					pst1.close();
//					System.out.println("strTo!=null else pst1 ===>> " + pst1);
////					}
//				
//					CF.updateLeaveRegisterForCompensatoryLeaves(con, CF, uF, uF.getDateFormat(str, "yyyy-MM-ddHH:mm", DATE_FORMAT), strEmpId);
//				}
//				
//			}
//
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(request.getContextPath()+ " : "+ e.getClass() + ": " +  e.getMessage(), e);
//		}finally{
//			db.closeResultSet(rs);
//			db.closeStatements(pst1);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrMessage() {
		return strMessage;
	}

	public void setStrMessage(String strMessage) {
		this.strMessage = strMessage;
	}

	public String getStrReason() {
		return strReason;
	}

	public void setStrReason(String strReason) {
		this.strReason = strReason;
	}

	public String getStrMode() {
		return strMode;
	}

	public void setStrMode(String strMode) {
		this.strMode = strMode;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getStrNotify() {
		return strNotify;
	}

	public void setStrNotify(String strNotify) {
		this.strNotify = strNotify;
	}

	public String getStrNewTime() {
		return strNewTime;
	}

	public void setStrNewTime(String strNewTime) {
		this.strNewTime = strNewTime;
	}

	public String getStrRosterStartTime() {
		return strRosterStartTime;
	}

	public void setStrRosterStartTime(String strRosterStartTime) {
		this.strRosterStartTime = strRosterStartTime;
	}

	public String getStrRosterEndTime() {
		return strRosterEndTime;
	}

	public void setStrRosterEndTime(String strRosterEndTime) {
		this.strRosterEndTime = strRosterEndTime;
	}

	public String getStrApproval() {
		return strApproval;
	}

	public void setStrApproval(String strApproval) {
		this.strApproval = strApproval;
	}

	public String getStrCommonEmpId() {
		return strCommonEmpId;
	}

	public void setStrCommonEmpId(String strCommonEmpId) {
		this.strCommonEmpId = strCommonEmpId;
	}

	public String getStrPrevMode() {
		return strPrevMode;
	}

	public void setStrPrevMode(String strPrevMode) {
		this.strPrevMode = strPrevMode;
	}

	public boolean getIsRosterDependant() {
		return isRosterDependant;
	}

	public void setIsRosterDependant(boolean isRosterDependant) {
		this.isRosterDependant = isRosterDependant;
	}

	public boolean getIsRosterRequired() {
		return isRosterRequired;
	}

	public void setIsRosterRequired(boolean isRosterRequired) {
		this.isRosterRequired = isRosterRequired;
	}


	public boolean getIsSingleButtonClockOnOff() {
		return isSingleButtonClockOnOff;
	}


	public void setIsSingleButtonClockOnOff(boolean isSingleButtonClockOnOff) {
		this.isSingleButtonClockOnOff = isSingleButtonClockOnOff;
	}
	
}