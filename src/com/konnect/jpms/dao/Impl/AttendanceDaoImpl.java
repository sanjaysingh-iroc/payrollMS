package com.konnect.jpms.dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.konnect.jpms.dao.AttendanceDao;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.UtilityFunctions;

public class AttendanceDaoImpl implements AttendanceDao {
	
	
	public void insertAttendanceEntry(Connection con,CommonFunctions CF, UtilityFunctions uF, int empId, int serviceId, java.sql.Timestamp timestamp,double hourworked,double early_late,String in_out) {
		PreparedStatement pst = null;
		try {

			pst =  con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
			+ "in_out_timestamp_actual,service_id,early_late,hours_worked_actual)values(?,?,?,?,?,?,?,?,?,?,?)");
			pst.setInt(1, empId);
			pst.setTimestamp(2,	timestamp);
			pst.setString(3, " ");
			pst.setString(4, in_out);
			pst.setInt(5, 1);
			pst.setString(6, " ");
			pst.setDouble(7,hourworked);
			pst.setTimestamp(8,timestamp);
			pst.setInt(9, serviceId);
     		pst.setDouble(10,early_late);
     		pst.setDouble(11,hourworked);
//			pst.setInt(12, CF.getMaxID(con, "atten_id", "attendance_details"));

			pst.execute();
			pst.close();

			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public int updateAttendanceEntry(Connection con,CommonFunctions CF, UtilityFunctions uF, int empId, int serviceId, java.sql.Timestamp timestamp,double hourworked,double early_late,String in_out,java.sql.Date date) {
		PreparedStatement pst = null;
		try {

			pst =  con.prepareStatement("update attendance_details set in_out_timestamp=?,hours_worked_actual=?,"
			+ "service_id=?,early_late=? where emp_id=? and cast(in_out_timestamp as date)=? and in_out=?");
			
			pst.setTimestamp(1,	timestamp);
			
			pst.setDouble(2,hourworked);
//			pst.setTimestamp(3,timestamp);
			pst.setInt(3, serviceId);
     		pst.setDouble(4,early_late);
     		pst.setInt(5, empId);
     		pst.setDate(6,date);
     		pst.setString(7, in_out);
     		int x= pst.executeUpdate();
			pst.close();
     		return x;

			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}
	
	public boolean deleteAttendanceEntry(Connection con,CommonFunctions CF, UtilityFunctions uF, int empId,java.sql.Date date) {
		PreparedStatement pst = null;
		try {

			pst =  con.prepareStatement("delete from attendance_details where emp_id=? and cast(in_out_timestamp ad date)=? ");
     		pst.setInt(1, empId);
     		pst.setDate(2,date);
			return pst.execute();

			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		 return false;
	}
	
	public boolean insertOverTimeHoursEntry(Connection con, CommonFunctions CF, UtilityFunctions uF, int empId, java.sql.Date date,double approvedHours,int approvedBy,java.sql.Date approvedDate,int paycycle, java.sql.Date paycycleFrom,java.sql.Date paycycleTo ) {
		PreparedStatement pst = null;
		try {

			pst = con.prepareStatement("insert into overtime_hours (emp_id,approved_ot_hours,approved_by," +
					"approve_date,paycle,paycycle_from,paycycle_to,_date) values(?,?,?,?,?,?,?,?)");
			pst.setInt(1, empId);
			pst.setDouble(2, approvedHours);
			pst.setInt(3, approvedBy);
			pst.setDate(4, approvedDate);
			pst.setInt(5, paycycle);
			pst.setDate(6,paycycleFrom);
			pst.setDate(7, paycycleTo);
			pst.setDate(8, date);
			pst.execute();
			pst.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	public int updateOverTimeHoursEntry(Connection con, CommonFunctions CF, int empId, java.sql.Date date,double approvedHours,int approvedBy,java.sql.Date approvedDate,int paycycle ) {
		PreparedStatement pst = null;
		try {

			pst = con.prepareStatement("update overtime_hours set approved_ot_hours=?,approved_by=?," +
					"approve_date=? "
							+ "where emp_id=?  and paycle=? and _date=?");
			
			pst.setDouble(1, approvedHours);
			pst.setInt(2, approvedBy);
			pst.setDate(3, approvedDate);
			pst.setInt(4, empId);
			pst.setInt(5, paycycle);
			pst.setDate(6, date);
			return pst.executeUpdate();
//			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}
	
	public boolean deleteOverTimeHoursEntry(Connection con,CommonFunctions CF, UtilityFunctions uF, int empId,java.sql.Date date,int paycycle) {
		PreparedStatement pst =null;
		try {

			pst =  con.prepareStatement("delete from overtime_hours where emp_id=? and _date=? and paycle=? ");
     		pst.setInt(1, empId);
     		pst.setDate(2,date);
     		pst.setInt(3, paycycle);
			return pst.execute();

			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		 return false;
	}
	
	public int insertRoster(Connection con, CommonFunctions CF, int empId, java.sql.Date date,double approvedHours,int approvedBy,java.sql.Date approvedDate,int paycycle ) {
		PreparedStatement pst =null;
		try {

			pst = con.prepareStatement("update overtime_hours set approved_ot_hours=?,approved_by=?," +
					"approve_date=? "
							+ "where emp_id=?  and paycle=? and _date=?");
			
			pst.setDouble(1, approvedHours);
			pst.setInt(2, approvedBy);
			pst.setDate(3, approvedDate);
			pst.setInt(4, empId);
			pst.setInt(5, paycycle);
			pst.setDate(6, date);
			return pst.executeUpdate();
//			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}
}
