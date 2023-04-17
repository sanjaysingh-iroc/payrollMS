package com.konnect.jpms.service;

import java.sql.Connection;
import java.util.List;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.UtilityFunctions;

public interface AttendanceService {

	public int insertAttendanceService(Connection con,UtilityFunctions uF,CommonFunctions CF, int preAttendanceCount,
			int currentAttendanceCount,List<String> previousDateRoster,	List<String> currentDateRoster,
			String strCurrentDate,String currentTime,String previousInTime,String currentInTime,int empId,int serviceId,String overTimeBuffer,int sessionEmpId,String[] paycycle);
	
	public int updateAttendanceService(Connection con,UtilityFunctions uF,CommonFunctions CF, List<String> currentDateRoster,
			String strCurrentDate,String currentTime,String currentInTime,int empId,int serviceId,boolean in_out,String overTimeBuffer,int sessionEmpId,String[] paycycle);
} 
