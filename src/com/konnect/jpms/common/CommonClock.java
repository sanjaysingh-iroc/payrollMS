package com.konnect.jpms.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CommonClock extends ActionSupport implements ServletRequestAware, IStatements {

 	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	public String execute() throws Exception {
		session = request.getSession(true);
	 	 
		request.setAttribute(PAGE, PCommonClock);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null){
			CF = new CommonFunctions();
			CF.setRequest(request);
//			CF.setStrTimeZone("Asia/Calcutta");
//			CF.setStrReportDateFormat(DATE_FORMAT);
//			CF.setStrReportTimeAM_PMFormat(TIME_FORMAT_AM_PM);
			setCommonFunctionsAttributes();
		}
		
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		
		UtilityFunctions uF = new UtilityFunctions();
		boolean isValidUser = validatePassword(uF);
		
		if(uF.parseToInt(getStrEmpId())>0 && isValidUser){
			clockOnOff(uF);
		}
		
//		if(!isValidUser && getStrEmpPassword()!=null){
//			request.setAttribute(MESSAGE, ERRORM+"Wrong Password, please try again"+END);
//		}
		
		   
		if(getStrEmpCode()!=null){
			getEmpDetails(uF);
		}
		
		
		session.setAttribute(CommonFunctions, CF);
		request.setAttribute("CURRENT_DATE", uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
//		request.setAttribute("CLOCK", "0");
		
		return loadChangePassword();
		
	}
	
	public boolean validatePassword(UtilityFunctions uF){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean isValidUser = false;
		boolean isForcePassword = false;
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from user_details  where emp_id=? and password=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setString(2, getStrEmpPassword());
			rs = pst.executeQuery();
			
			while(rs.next()){
				isValidUser = true;
				isForcePassword = rs.getBoolean("is_forcepassword");
			}
			rs.close();
			pst.close();
			
			if(isValidUser){
				pst = con.prepareStatement("select * from settings  where options=?");
				pst.setString(1, O_ATTENDANCE_INTEGRATED_WITH_ACTIVITY);
				rs = pst.executeQuery();
				while(rs.next()){
					CF.setStrAttendanceIntegratedWithActivity(rs.getString("value"));
				}
				rs.close();
				pst.close();
			}
			
			if(!isValidUser && getStrEmpPassword()!=null){
				request.setAttribute(MESSAGE, ERRORM+"Wrong Password, please try again"+END);
			}
			if(isForcePassword){
				request.setAttribute(MESSAGE, ERRORM+"You are request to change password from your portal before clocking here."+END);
				isValidUser = false;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return isValidUser;
	}
	
	
	public void clockOnOff(UtilityFunctions uF){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			
			con = db.makeConnection(con);
			
			
			pst = con.prepareStatement("select * from roster_details where emp_id = ? and _date = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			
			rs = pst.executeQuery();
			
			String strFrom = null;
			String strTo = null;
			String serviceId = null;
			
			while(rs.next()){
				strFrom = rs.getString("_from");
				strTo = rs.getString("_to");
				serviceId = rs.getString("service_id");
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select * from attendance_details where emp_id = ? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			
			rs = pst.executeQuery();
			
			boolean isIn = false;
			boolean isOut = false;
			
			String strInTime = null;
			
			while(rs.next()){
				if("IN".equalsIgnoreCase(rs.getString("in_out"))){
					isIn = true;
					strInTime = rs.getString("in_out_timestamp");
				}
				if("OUT".equalsIgnoreCase(rs.getString("in_out"))){
					isOut = true;
				}
			}
			rs.close();
			pst.close();
			
			
			Time frmTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+strFrom, DBDATE+DBTIME);
			Time toTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+strTo, DBDATE+DBTIME);
			Time currTime = uF.getCurrentTime(CF.getStrTimeZone());
			
			
			
			long fromCurrent = 0;
			long toCurrent = 0;
			
			if(frmTime!=null && frmTime.getTime() >= currTime.getTime() && currTime.getTime()>0 && frmTime.getTime()>0){
				fromCurrent = uF.getTimeDifference(currTime.getTime(), frmTime.getTime());
				fromCurrent = fromCurrent / 60000;
			}
			
			if(toTime!=null && toTime.getTime() <= currTime.getTime() && currTime.getTime()>0 && toTime.getTime()>0){
				toCurrent = uF.getTimeDifference(toTime.getTime(), currTime.getTime());
				toCurrent = toCurrent / 60000;
			}
			
			
			
			

			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int DAY = cal.get(Calendar.DAY_OF_MONTH);

			
			int HOUR = cal.get(Calendar.HOUR_OF_DAY);
			int HOUR_A = cal.get(Calendar.HOUR_OF_DAY);
			int MINUTE_A = cal.get(Calendar.MINUTE);
			int MINUTE = cal.get(Calendar.MINUTE);

			
			
			
			
			
			Map hmTardy = CF.getTardyType(con);
			int nTardyIn = uF.parseToInt((String)hmTardy.get("TARDY_IN"));
			int nTardyOut = uF.parseToInt((String)hmTardy.get("TARDY_OUT"));
			
			
			int nApproved = 0;
			int RoundOff = 30;
			int mode = MINUTE % RoundOff;
			
			
			
			
			if(!isIn){
				
				if(Math.abs(nTardyIn)>=fromCurrent && fromCurrent>0){
					MINUTE = RoundOff - mode;
					cal.add(Calendar.MINUTE, MINUTE);
					HOUR = cal.get(Calendar.HOUR_OF_DAY);
					MINUTE = cal.get(Calendar.MINUTE);


					nApproved = 1;
					
					
				}
			}else{
				if(Math.abs(nTardyOut) >= toCurrent && toCurrent>0){
					MINUTE = RoundOff - mode;
					cal.add(Calendar.MINUTE, -mode);
					HOUR = cal.get(Calendar.HOUR_OF_DAY);
					MINUTE = cal.get(Calendar.MINUTE);
					
					nApproved = 1;
					
				}
			}
			
			if(!CF.isRosterDependency(con,strEmpId)){
				nApproved = 0;
			}
			
			
			
			double dblEarlyLate = 0;
			
			if(!isIn && strFrom!=null){
					dblEarlyLate = uF.parseToDouble(uF.getTimeDiffInHoursMins(currTime.getTime(), frmTime.getTime()));
			}else if(isIn && strTo!=null){
					dblEarlyLate = uF.parseToDouble(uF.getTimeDiffInHoursMins(currTime.getTime(), toTime.getTime()));
			}
			
			
			double dblHrsWorked = 0;
			if(isIn){
				Time inTime = uF.getTimeFormat(strInTime, DBTIMESTAMP);
				dblHrsWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(inTime.getTime(), currTime.getTime()));
			}
			
			
			
			long diff=0;
			
			if (!isIn && frmTime!=null) {
				
				diff = currTime.getTime() - frmTime.getTime();
			} else if(toTime!=null) {
				diff = currTime.getTime() - toTime.getTime();
			}

			long diffSeconds = diff / 1000;
			long diffMinutes = diff / (60 * 1000);
			
			
			
			
			pst = con.prepareStatement("SELECT * FROM roster_policy WHERE mode = ? and time_value " + ((diffMinutes >= 0) ? ">" : "<") + "= ? and policy_status=1 and effective_date<=? order by time_value "+((diffMinutes >= 0)?"":"desc")+" LIMIT 1");
			if(!isIn){
				pst.setString(1, "IN");
			}else{
				pst.setString(1, "OUT");
			}
			
			pst.setLong(2, diffMinutes);
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));

			rs = pst.executeQuery();  
			
			String strMessage = null;
			if (diffMinutes!=0 && CF.isRosterDependency(con,strEmpId) && rs.next() && rs.getString("message")!=null && rs.getString("message").length()>0 && getStrEmpPassword()==null) {
				strMessage = rs.getString("message");
				nApproved = -2;
			} else{
				rs.close();
				pst.close();
				
				
				
				if(getStrEmpReason()!=null && getStrEmpReason().length()>0){
					nApproved = -2;
				}
				
				
				pst = con.prepareStatement("insert into attendance_details (emp_id, in_out_timestamp, in_out, early_late, hours_worked, in_out_timestamp_actual, service_id, reason, approved) values (?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+" "+uF.getCurrentTime(CF.getStrTimeZone()), DBTIMESTAMP));
				
				if(!isIn){
					pst.setString(3, "IN");
				}else{
					pst.setString(3, "OUT");
				}
				
				
				pst.setDouble(4, dblEarlyLate);
				pst.setDouble(5, dblHrsWorked);
				pst.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+" "+uF.getCurrentTime(CF.getStrTimeZone()), DBTIMESTAMP));
				pst.setInt(7, uF.parseToInt(serviceId));
				pst.setString(8, uF.showData(getStrEmpReason(), "")); 
				pst.setInt(9, nApproved);
				pst.execute();
				pst.close();
				
				
				
				
				pst = con.prepareStatement("update roster_details set attended=? where emp_id = ? and _date=? and service_id = ?");
				if(!isIn){
					pst.setInt(1, 1);
				}else{
					pst.setInt(1, 2);
				}
				pst.setInt(2, uF.parseToInt(getStrEmpId()));
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt(serviceId));
				pst.execute();
				pst.close();
				
				
				
				
				
				if(!isIn){
					request.setAttribute(MESSAGE, SUCCESSM+"You have clocked on successfully at "+uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+" "+uF.getCurrentTime(CF.getStrTimeZone()), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat())+END);
					if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())){
						CF.clockOnOffEntryForActivity(con, uF, CF, getStrEmpId(), "Clocked On ", -1, getStrEmpReason());
					}
				}else{
					request.setAttribute(MESSAGE, SUCCESSM+"You have clocked off successfully at "+uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+" "+uF.getCurrentTime(CF.getStrTimeZone()), DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat())+END);
					if(uF.parseToBoolean(CF.getStrAttendanceIntegratedWithActivity())){
						CF.clockOnOffEntryForActivity(con, uF, CF, getStrEmpId(), "Clocked Off ", -2, getStrEmpReason());
					}
				}
				
				
				
				
				setStrEmpCode(null);
				setStrEmpId(null);
				setStrEmpPassword(null);
				setStrEmpReason(null);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void setCommonFunctionsAttributes(){
			
			Database db = new Database();
			db.setRequest(request);
			Connection con=null;
			PreparedStatement pst = null;
			ResultSet rst =null;
			try {
				
				con = db.makeConnection(con);
				
//				con.setAutoCommit(false);
//				CallableStatement cst = con.prepareCall("{? = call sel_settings()}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.execute();
//				ResultSet rst = (ResultSet) cst.getObject(1);
				
				pst = con.prepareStatement("select * FROM settings");
				rst = pst.executeQuery();
				
				while(rst.next()){
					
					if(rst.getString("options").equalsIgnoreCase(O_TIME_ZONE)){
						CF.setStrTimeZone(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_DATE_FORMAT)){
						CF.setStrReportDateFormat(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_TIME_FORMAT)){
						CF.setStrReportTimeFormat(rst.getString("value"));
					}else if(rst.getString("options").equalsIgnoreCase(O_COMMON_ATTEN_FORMAT)){
						CF.setStrCommonAttendanceFormat(rst.getString("value"));
					}
					CF.setStrReportTimeAM_PMFormat(TIME_FORMAT_AM_PM);
				}
				rst.close();
				pst.close();
				
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				db.closeResultSet(rst);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
	}
	
	public void getEmpDetails(UtilityFunctions uF){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con=null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		con = db.makeConnection(con);
		try {
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmDesMap = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpWLocationMap = CF.getEmpWlocationMap(con); 
			Map<String, Map<String, String>> hmWLocationMap = CF.getWorkLocationMap(con);
					
			
			
			if(CF.getStrCommonAttendanceFormat()!=null && CF.getStrCommonAttendanceFormat().equalsIgnoreCase(USER_NAME)){
				pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod, user_details ud where epd.emp_per_id = eod.emp_id and ud.emp_id = eod.emp_id and ud.emp_id = epd.emp_per_id and ud.username=?");
				pst.setString(1, getStrEmpCode());
			}else{
				pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and empcode = ?");
				pst.setString(1, getStrEmpCode());
			}
			
//			System.out.println("pst===>"+pst);
			
			rs = pst.executeQuery();
			Map<String, String> hmDetails = new HashMap<String, String>();
			int count = 0;
			while(rs.next()){
				count++;
				hmDetails.put("EMP_ID", rs.getString("emp_per_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmDetails.put("EMP_NAME", rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				hmDetails.put("EMP_IMAGE", rs.getString("emp_image"));
				hmDetails.put("EMP_CODE", rs.getString("empcode"));
				hmDetails.put("EMP_EMAIL", rs.getString("emp_email"));
				
				String strWlocationId = hmEmpWLocationMap.get(rs.getString("emp_per_id"));
				Map<String, String> hmWLocation = hmWLocationMap.get(strWlocationId);
				if(hmWLocation==null)hmWLocation =new HashMap();
				
				
				
				
				hmDetails.put("EMP_LOCATION", uF.showData(hmWLocation.get("WL_NAME"), "")+", "+uF.showData(hmWLocation.get("WL_CITY"), "")+" "+uF.showData(hmWLocation.get("WL_COUNTRY"), ""));
				
				hmDetails.put("EMP_DESIG", hmDesMap.get(rs.getString("emp_per_id")));
				hmDetails.put("EMP_MANAGER", hmEmpNameMap.get(rs.getString("supervisor_emp_id")));
				
				
				setStrEmpId(rs.getString("emp_per_id"));
				
				
				session.setAttribute(EMPID, rs.getString("emp_per_id"));
				session.setAttribute("EMPID", rs.getString("emp_per_id"));
				
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("hmDetails", hmDetails);
			
			if(count==0){
				
				if(CF.getStrCommonAttendanceFormat()!=null && CF.getStrCommonAttendanceFormat().equalsIgnoreCase(USER_NAME)){
					request.setAttribute(MESSAGE, ERRORM+"This username does not exist. Please try again."+END);
				}else{
					request.setAttribute(MESSAGE, ERRORM+"This username does not exist. Please try again."+END);
				}
				return;
				
			}
			
			pst = con.prepareStatement("select * from roster_details where emp_id = ? and _date = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			String strFrom = null;
			String strTo = null;
			String serviceId = null;
			String strDate=null;
			
			while(rs.next()){
				strFrom = rs.getString("_from");
				strTo = rs.getString("_to");
				serviceId = rs.getString("service_id");
				strDate = rs.getString("_date");
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select * from attendance_details where emp_id = ? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			boolean isIn = false;
			boolean isOut = false;
			String strInTime = null;
			while(rs.next()){
				if("IN".equalsIgnoreCase(rs.getString("in_out"))){
					isIn = true;
					strInTime = rs.getString("in_out_timestamp");
				}
				if("OUT".equalsIgnoreCase(rs.getString("in_out"))){
					isOut = true;
				}
			}
			rs.close();
			pst.close();
			
			
			if(!isIn && strFrom!=null){
				request.setAttribute("ROSTER_START_TIME", uF.getDateFormat(strDate+" "+strFrom, DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()));
				request.setAttribute("CLOCK", "1");
			}else if(isIn && strTo!=null){
				request.setAttribute("ROSTER_END_TIME", uF.getDateFormat(strDate+" "+strTo, DBTIMESTAMP, CF.getStrReportTimeAM_PMFormat()));
				request.setAttribute("CLOCK", "2");
			}
			
			
			Time frmTime = uF.getTimeFormat(strDate+strFrom, DBDATE+DBTIME);
			Time toTime = uF.getTimeFormat(strDate+strTo, DBDATE+DBTIME);
			Time currTime = uF.getCurrentTime(CF.getStrTimeZone());
			
			
			
			long fromCurrent = 0;
			long toCurrent = 0;
			
			if(frmTime!=null && frmTime.getTime() >= currTime.getTime() && currTime.getTime()>0 && frmTime.getTime()>0){
				fromCurrent = uF.getTimeDifference(frmTime.getTime(), frmTime.getTime());
				fromCurrent = fromCurrent / 60000;
			}
			
			if(toTime!=null && toTime.getTime() <= currTime.getTime() && currTime.getTime()>0 && toTime.getTime()>0){
				toCurrent = uF.getTimeDifference(toTime.getTime(), currTime.getTime());
				toCurrent = toCurrent / 60000;
			}
			
			
			
			

			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int DAY = cal.get(Calendar.DAY_OF_MONTH);

			
			int HOUR = cal.get(Calendar.HOUR_OF_DAY);
			int HOUR_A = cal.get(Calendar.HOUR_OF_DAY);
			int MINUTE_A = cal.get(Calendar.MINUTE);
			int MINUTE = cal.get(Calendar.MINUTE);

			
			
			
			
			
			Map hmTardy = CF.getTardyType(con);
			int nTardyIn = uF.parseToInt((String)hmTardy.get("TARDY_IN"));
			int nTardyOut = uF.parseToInt((String)hmTardy.get("TARDY_OUT"));
			
			
			int nApproved = -2;
			int RoundOff = 30;
			int mode = MINUTE % RoundOff;
			
			
			
			
			if(!isIn){
				
				if(Math.abs(nTardyIn)>=fromCurrent && fromCurrent>0){
					MINUTE = RoundOff - mode;
					cal.add(Calendar.MINUTE, MINUTE);
					HOUR = cal.get(Calendar.HOUR_OF_DAY);
					MINUTE = cal.get(Calendar.MINUTE);


					nApproved = 1;
					
					
				}
			}else{
				if(Math.abs(nTardyOut) >= toCurrent && toCurrent>0){
					MINUTE = RoundOff - mode;
					cal.add(Calendar.MINUTE, -mode);
					HOUR = cal.get(Calendar.HOUR_OF_DAY);
					MINUTE = cal.get(Calendar.MINUTE);
					
					nApproved = 1;
					
				}
			}
			
			if(!CF.isRosterDependency(con,strEmpId)){
				nApproved = 0;
			}
			
			
			
			double dblEarlyLate = 0;
			
			if(!isIn && strFrom!=null){
					dblEarlyLate = uF.parseToDouble(uF.getTimeDiffInHoursMins(currTime.getTime(), frmTime.getTime()));
			}else if(isIn && strTo!=null){
					dblEarlyLate = uF.parseToDouble(uF.getTimeDiffInHoursMins(currTime.getTime(), toTime.getTime()));
			}
			
			double dblHrsWorked = 0;
			if(isIn){
				Time inTime = uF.getTimeFormat(strInTime, DBTIMESTAMP);
				dblHrsWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(inTime.getTime(), currTime.getTime()));
			}
			
			
			
			long diff=0;
			
			if (!isIn && frmTime!=null) {
				
				diff = currTime.getTime() - frmTime.getTime();
			} else if(toTime!=null){
				diff = currTime.getTime() - toTime.getTime();
			}

			long diffSeconds = diff / 1000;
			long diffMinutes = diff / (60 * 1000);
			
			
			
			
			pst = con.prepareStatement("SELECT * FROM roster_policy WHERE mode = ? and time_value " + ((diffMinutes >= 0) ? ">" : "<") + "= ? and policy_status=1 and effective_date<=? order by time_value "+((diffMinutes >= 0)?"":"desc")+" LIMIT 1");
			if(!isIn){
				pst.setString(1, "IN");
			}else{
				pst.setString(1, "OUT");
			}
			pst.setLong(2, diffMinutes);
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();  
			String strMessage = null;
			if (diffMinutes!=0 && CF.isRosterDependency(con,strEmpId) && rs.next() && rs.getString("message")!=null && rs.getString("message").length()>0) {
				strMessage = rs.getString("message");
				nApproved = -2;
			} 
			rs.close();
			pst.close();
			
			
			request.setAttribute("strMessage", strMessage);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	String strEmpCode;
	String strEmpId;
	String strEmpPassword;
	String strEmpReason;
	
	
	public String loadChangePassword() {
		request.setAttribute(PAGE, PCommonClock);
		request.setAttribute(TITLE, "Clock on/off screen");
		
		return LOAD;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrEmpCode() {
		return strEmpCode;
	}

	public void setStrEmpCode(String strEmpCode) {
		this.strEmpCode = strEmpCode;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrEmpPassword() {
		return strEmpPassword;
	}

	public void setStrEmpPassword(String strEmpPassword) {
		this.strEmpPassword = strEmpPassword;
	}

	public String getStrEmpReason() {
		return strEmpReason;
	}

	public void setStrEmpReason(String strEmpReason) {
		this.strEmpReason = strEmpReason;
	}


}
