package com.konnect.jpms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.UtilityFunctions;

public interface AttendanceDao {

	public void insertAttendanceEntry(Connection con,CommonFunctions CF, 
			UtilityFunctions uF, int empId, int serviceId,
			java.sql.Timestamp timestamp,double hourworked,double early_late,String in_out);
	
	
	public int updateAttendanceEntry(Connection con,CommonFunctions CF, 
			UtilityFunctions uF, int empId, int serviceId,
			java.sql.Timestamp timestamp,double hourworked,double early_late,String in_out,java.sql.Date date);
	
	
	public boolean deleteAttendanceEntry(Connection con,CommonFunctions CF, UtilityFunctions uF, int empId,java.sql.Date date);
	
	public boolean insertOverTimeHoursEntry(Connection con, CommonFunctions CF,
			UtilityFunctions uF, int empId, java.sql.Date date,double approvedHours,int approvedBy,java.sql.Date approvedDate,int paycycle,
			java.sql.Date paycycleFrom,java.sql.Date paycycleTo );
	 
	
	public boolean deleteOverTimeHoursEntry(Connection con,CommonFunctions CF, UtilityFunctions uF, int empId,java.sql.Date date,int paycycle) ;
	public int updateOverTimeHoursEntry(Connection con, CommonFunctions CF, int empId, java.sql.Date date,double approvedHours,int approvedBy,java.sql.Date approvedDate,int paycycle );
}
