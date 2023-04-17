package com.konnect.jpms.service.Impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import com.konnect.jpms.dao.AttendanceDao;
import com.konnect.jpms.dao.Impl.AttendanceDaoImpl;
import com.konnect.jpms.service.AttendanceService;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;

public class AttendanceServiceImpl implements IConstants,AttendanceService {

	AttendanceDao attendanceDao=new AttendanceDaoImpl();
	
//	public static void main(String s[]){
		
//		AttendanceServiceImpl attendanceServiceImpl=new AttendanceServiceImpl();
//		
//		Database db=new Database();
//		Connection con=null;
//		con=db.makeConnection(con);
//		UtilityFunctions uF=new UtilityFunctions();
//		CommonFunctions CF=new CommonFunctions();
//		CF.setMaxTimeLimitOUT("240");
//		List<String> previousDateRoster=new ArrayList<String>();
//		previousDateRoster.add("2014-04-01");
//		previousDateRoster.add("22:00:00");
//		previousDateRoster.add("06:00:00");
//		previousDateRoster.add("");
//		previousDateRoster.add("8");
//		
//		List<String> currentDateRoster=new ArrayList<String>();
//		currentDateRoster.add("2014-04-02");
//		currentDateRoster.add("22:00:00");
//		currentDateRoster.add("06:00:00");
//		currentDateRoster.add("");
//		currentDateRoster.add("8");
//		
//		
//		String currentDate="2014-04-02";
//		String currentTime="06:45:00";
//		String previousInTime="2014-04-01 22:30:00";
//		String currentInTime="2014-04-02 15:30:00";
//		String[] paycycle={"01/04/2014","30/04/2014","4"};
//		attendanceServiceImpl.insertAttendanceService(con, uF, CF, 1,0, previousDateRoster, currentDateRoster, currentDate, currentTime, previousInTime, currentInTime, 555, 1,"60",1,paycycle);
////		attendanceServiceImpl.updateAttendanceService(con, uF, CF, currentDateRoster, currentDate, currentTime, currentInTime, 222,1, false,60,1,paycycle);
//	}
	
	public int insertAttendanceService(Connection con,UtilityFunctions uF,CommonFunctions CF, int preAttendanceCount,
			int currentAttendanceCount,List<String> previousDateRoster,	List<String> currentDateRoster,
			String strCurrentDate,String currentTime,String previousInTime,String currentInTime,int empId,int serviceId,String overTimeBuffer,int sessionEmpId,String[] paycycle){
		
		
		long aa=0;
		long time=0;
		java.util.Date presentDate=null;
		if( currentAttendanceCount==0 && preAttendanceCount==1){
			
			String strPreRosterInTime =null;
			String strPreRosterOutTime =null;
			String strPreviousDate=null;
			
			if (previousDateRoster != null) {
				strPreviousDate= previousDateRoster.get(0);
				strPreRosterInTime = previousDateRoster.get(1);
				strPreRosterOutTime = previousDateRoster.get(2);
			
			time=uF.getDateFormatUtil(strPreviousDate+" "+strPreRosterOutTime, DBDATE+" "+TIME_FORMAT).getTime();
			if(uF.getDateFormatUtil(strPreviousDate+" "+strPreRosterInTime, DBDATE+" "+TIME_FORMAT).getTime()>uF.getDateFormatUtil(strPreviousDate+" "+strPreRosterOutTime, DBDATE+" "+TIME_FORMAT).getTime()){
				time+=24*60*60*1000;
			}
			
			presentDate=uF.getDateFormatUtil(strCurrentDate+" "+currentTime, DBDATE+" "+TIME_FORMAT);
			
			aa=time-presentDate.getTime();
			aa=aa/(60*1000);
			aa+=uF.parseToInt(CF.getMaxTimeLimitOUT());
			}
		}
		
		if(aa>0){
			
			
			
			
			long hourworked=presentDate.getTime()-uF.getDateFormatUtil(previousInTime, DBDATE+" "+TIME_FORMAT).getTime();
				double hourworked1=getTimeDiffInHoursMins(hourworked,uF);
				if(hourworked1<0){
					hourworked1+=24;
				}
				attendanceDao.insertAttendanceEntry(con, CF,  uF, empId, serviceId, uF.getTimeStamp(previousDateRoster.get(0)+" "+currentTime, DBDATE+" "+TIME_FORMAT), hourworked1,getTimeDiffInHoursMins(presentDate.getTime()-time,uF), "OUT");
				if(overTimeBuffer!=null)
				insertOverTimeHoursService(con,  uF, CF, empId, time,presentDate.getTime(),uF.getDateFormat(previousDateRoster.get(0), DBDATE),uF.parseToDouble(overTimeBuffer),sessionEmpId,paycycle);
				return 3;
			
		}else if(currentAttendanceCount==0){
				
				String strRosterInTime =null;
				String strRosterDate=null;
				
				if (currentDateRoster != null) {
					strRosterDate= currentDateRoster.get(0);
					strRosterInTime = currentDateRoster.get(1);
				
				java.util.Date rosterDate=uF.getDateFormatUtil(strRosterDate+" "+strRosterInTime, DBDATE+" "+TIME_FORMAT);
				presentDate=uF.getDateFormatUtil(strRosterDate+" "+currentTime, DBDATE+" "+TIME_FORMAT);
				long preTime=presentDate.getTime()-rosterDate.getTime();
				attendanceDao.insertAttendanceEntry(con, CF,  uF, empId, serviceId, uF.getTimeStamp(strCurrentDate+" "+currentTime, DBDATE+" "+TIME_FORMAT), 0,getTimeDiffInHoursMins(preTime,uF), "IN");
				return 1;
				}
			}else if(currentAttendanceCount==1){
				
				String strRosterInTime =null;
				String strRosterOutTime =null;
				String strRosterDate=null;
				
				if (currentDateRoster != null) {
					strRosterDate= currentDateRoster.get(0);
					strRosterInTime = currentDateRoster.get(1);
					strRosterOutTime = currentDateRoster.get(2);
				
				java.util.Date rosterInDate=uF.getDateFormatUtil(strRosterDate+" "+strRosterInTime, DBDATE+" "+TIME_FORMAT);
				java.util.Date rosterOutDate=uF.getDateFormatUtil(strRosterDate+" "+strRosterOutTime, DBDATE+" "+TIME_FORMAT);
				long longrosterInDate=rosterInDate.getTime();
				long longrosterOutDate=rosterOutDate.getTime();
				
				java.util.Date presentInDate=uF.getDateFormatUtil(currentInTime, DBDATE+" "+TIME_FORMAT);
				java.util.Date presentOutDate=uF.getDateFormatUtil(strRosterDate+" "+currentTime, DBDATE+" "+TIME_FORMAT);
				long longpresentInDate=presentInDate.getTime();
				long longpresentOutDate=presentOutDate.getTime();
				
				if(longrosterInDate>longrosterOutDate){
					longrosterOutDate+=24*60*60*1000;
				}
				
				if(longpresentInDate>longpresentOutDate){
					longpresentOutDate+=24*60*60*1000;
				}
				

				long earlyLate=longpresentOutDate-longrosterOutDate;
				

				long hourworked=longpresentOutDate-uF.getDateFormatUtil(currentInTime, DBDATE+" "+TIME_FORMAT).getTime();
//				hourworked=hourworked/3600/1000;
				double hourworked1=getTimeDiffInHoursMins(hourworked,uF);
//				if(hourworked1<0){
//					hourworked1+=24;
//				}
				
				attendanceDao.insertAttendanceEntry(con, CF,  uF, empId, serviceId, uF.getTimeStamp(strRosterDate+" "+currentTime, DBDATE+" "+TIME_FORMAT), hourworked1,getTimeDiffInHoursMins(earlyLate,uF), "OUT");

				if(overTimeBuffer!=null)
				insertOverTimeHoursService(con,  uF, CF, empId, longrosterOutDate,longpresentOutDate,uF.getDateFormat(strRosterDate, DBDATE),uF.parseToDouble(overTimeBuffer),sessionEmpId,paycycle);

			
				
				return 2;
				}
			}
		return 0;
	}
	
	public int updateAttendanceService(Connection con,UtilityFunctions uF,CommonFunctions CF, List<String> currentDateRoster,
			String strCurrentDate,String currentTime,String currentInTime,int empId,int serviceId,boolean in_out,
			String overTimeBuffer,int sessionEmpId,String[] paycycle){
		
		
		if(in_out){//in
		String strRosterInTime =null;
		String strRosterDate=null;
		
		if (currentDateRoster != null) {
			strRosterDate= currentDateRoster.get(0);
			strRosterInTime = currentDateRoster.get(1);
		}
		java.util.Date rosterDate=uF.getDateFormatUtil(strRosterDate+" "+strRosterInTime, DBDATE+" "+TIME_FORMAT);
		java.util.Date presentDate=uF.getDateFormatUtil(strCurrentDate+" "+currentTime, DBDATE+" "+TIME_FORMAT);
		
		long preTime=presentDate.getTime()-rosterDate.getTime();
		return attendanceDao.updateAttendanceEntry(con, CF, uF, empId, serviceId, uF.getTimeStamp(strCurrentDate+" "+currentTime, DBDATE+" "+TIME_FORMAT), 0, getTimeDiffInHoursMins(preTime,uF), "IN", uF.getDateFormat(strCurrentDate, DBDATE));
		}else{//out
			
			String strRosterInTime =null;
			String strRosterOutTime =null;
			String strRosterDate=null;
			
			if (currentDateRoster != null) {
				strRosterDate= currentDateRoster.get(0);
				strRosterInTime = currentDateRoster.get(1);
				strRosterOutTime = currentDateRoster.get(2);
			}
			

			java.util.Date rosterInDate=uF.getDateFormatUtil(strRosterDate+" "+strRosterInTime, DBDATE+" "+TIME_FORMAT);
			java.util.Date rosterOutDate=uF.getDateFormatUtil(strRosterDate+" "+strRosterOutTime, DBDATE+" "+TIME_FORMAT);
			long longrosterInDate=rosterInDate.getTime();
			long longrosterOutDate=rosterOutDate.getTime();
			
			java.util.Date presentInDate=uF.getDateFormatUtil(currentInTime, DBDATE+" "+TIME_FORMAT);
			java.util.Date presentOutDate=uF.getDateFormatUtil(strRosterDate+" "+currentTime, DBDATE+" "+TIME_FORMAT);		
			long longpresentInDate=presentInDate.getTime();
			long longpresentOutDate=presentOutDate.getTime();
			
			if(longrosterInDate>longrosterOutDate){
				longrosterOutDate+=24*60*60*1000;
			}
			
			if(longpresentInDate>longpresentOutDate){
				longpresentOutDate+=24*60*60*1000;
			}
			

			long earlyLate=longpresentOutDate-longrosterOutDate;

			long hourworked=longpresentOutDate-uF.getDateFormatUtil(currentInTime, DBDATE+" "+TIME_FORMAT).getTime();
			double hourworked1=getTimeDiffInHoursMins(hourworked,uF);
			int x=attendanceDao.updateAttendanceEntry(con, CF, uF, empId, serviceId, uF.getTimeStamp(strCurrentDate+" "+currentTime, DBDATE+" "+TIME_FORMAT), hourworked1, getTimeDiffInHoursMins(earlyLate,uF), "OUT", uF.getDateFormat(strCurrentDate, DBDATE));
			if(overTimeBuffer!=null)
			insertOverTimeHoursService(con,  uF, CF, empId, longrosterOutDate,longpresentOutDate,uF.getDateFormat(strRosterDate, DBDATE),uF.parseToDouble(overTimeBuffer),sessionEmpId,paycycle);

			return x;
			
		}
		
	}
	
	
	public void insertOverTimeHoursService(Connection con,UtilityFunctions uF,CommonFunctions CF,int empId,long rosterTime,long attendanceTime,java.sql.Date date,double overTimeBuffer,int approvedEmpId,String[] paycycle){
		
		long a=attendanceTime-rosterTime;
		if(attendanceDao.updateOverTimeHoursEntry(con, CF, empId, date, ((a/(60*1000))>overTimeBuffer)?getTimeDiffInHoursMins( a,uF):0, approvedEmpId, date, uF.parseToInt(paycycle[2]))==0){
			if((a/(60*1000))>overTimeBuffer)
			attendanceDao.insertOverTimeHoursEntry(con, CF, uF, empId, date,((a/(60*1000))>overTimeBuffer)?getTimeDiffInHoursMins( a,uF):0, approvedEmpId, date, uF.parseToInt(paycycle[2]),uF.getDateFormat(paycycle[0], DATE_FORMAT),uF.getDateFormat(paycycle[1], DATE_FORMAT));
		}
	}
	
	
	public double getTimeDiffInHoursMins(long value,UtilityFunctions uF) {

		int diffHours = (int)(value / (1000 * 60 * 60));
		int diffMinutes = (int)((value % (1000 * 60 * 60)) / (1000 * 60));
		if(diffHours<0 && diffMinutes<0){
			diffMinutes=diffMinutes*-1;
		}else if(diffHours==0 && diffMinutes<0){
			diffMinutes=diffMinutes*-1;
			return uF.parseToDouble("-"+diffHours+"."+diffMinutes);
		}
		return uF.parseToDouble(diffHours+"."+diffMinutes);

	}
}
