package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateException extends ActionSupport implements IStatements, ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6957524740219848447L;
	private String strEmpId;
	private String strDate;
	private String strServiceId;
	private String strStatus;
	private String strActualStartTime;
	private String strActualEndTime;
	String strReason;
	String exceptionType;
	
	String strStartTime;
	String strEndTime;
	String exceptionMode;
	
	String strSessionEmpId;
	String strUserTypeId;
	HttpSession session;
	CommonFunctions CF;
	 String employeeReason;//Created By Dattatray Date : 12-11-21 Note: Reason Added
	public String execute() throws Exception {
		 
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserTypeId = (String)session.getAttribute(USERTYPEID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strEmpId = request.getParameter("EMPID");
		strDate = request.getParameter("DT");
		strServiceId = request.getParameter("SID");
		strStatus = request.getParameter("S");
//		strActualStartTime = request.getParameter("AST");
//		strActualEndTime = request.getParameter("AET");
		if(getStrReason()!=null && !getStrReason().equals("")) {
//			System.out.println("getExceptionType() ===>> " + getExceptionType());
			if(getExceptionType() != null && (getExceptionType().equals("HD") || getExceptionType().equals("FD"))) {
				updateHDFDException();
			} else {
				updateException();
			}
		}
		return SUCCESS;
	
	}

	private void updateHDFDException() {

		Connection con = null;
		PreparedStatement pst = null, pst1 = null, pst2 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			Map<String, String> hmLunchDeductionService = CF.getLunchDeductionServiceDetails(con);
			
			Map<String, String> hmEmpWlocationMap = new HashMap<String, String>();
			Map<String, String> hmEmpStateMap = new HashMap<String, String>();
			Map<String, String> hmEmpMertoMap = new HashMap<String, String>();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			
			Map<String, Map<String, String>> hmHalfDayFullDayMinHrs = CF.getWorkLocationHalfDayFullDayMinHours(con, uF, uF.getDateFormat(strDate, DBDATE, DATE_FORMAT));
			if(hmHalfDayFullDayMinHrs==null) hmHalfDayFullDayMinHrs = new HashMap<String, Map<String,String>>();
			
			Map<String, String> hmHDFDMinHrs = hmHalfDayFullDayMinHrs.get(hmEmpWlocationMap.get(strEmpId));
			if(hmHDFDMinHrs==null) hmHDFDMinHrs = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from roster_details where emp_id=? and _date=? and service_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strDate, DBDATE));
			pst.setInt(3, uF.parseToInt(strServiceId));
//			System.out.println("2 pst======>"+pst);
			rs = pst.executeQuery();
			String strStart = null;
			String strEnd = null;
			while(rs.next()) {
				strStart = rs.getString("_from");
				strEnd = rs.getString("_to");
			}
            rs.close();
            pst.close();
			
			long lIn = 0;
			long lOut = 0;
			
			lIn = uF.getTimeFormat(strDate + strStart, DBDATE + DBTIME).getTime();
			if(getExceptionType() != null && getExceptionType().equals("HD")) {
				long oneminute=60000;
				double dblHDMinHrs = uF.parseToDouble(hmHDFDMinHrs.get("MIN_HRS_HD"));
				double dblOut = dblHDMinHrs*60*oneminute;
				lOut = lIn + uF.parseToInt(""+dblOut);
//				int s = (int)(lOut / 1000) % 60;
			    int m = (int)(lOut / (1000 * 60)) % 60;
			    int h = (int)(lOut / (1000 * 60 * 60)) % 24;
			    strEnd = h+":"+m;
			} else {
				lOut = uF.getTimeFormat(strDate + strEnd, DBDATE + DBTIME).getTime();
			}
			double dblHoursWorked = 0;
			if (lOut > 0) {
				dblHoursWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(lIn, lOut));
				boolean isLunchDeductionService = uF.parseToBoolean((String) hmLunchDeductionService.get(strServiceId));
				if(isLunchDeductionService) {
					dblHoursWorked = CF.calculateTimeDeduction(con, dblHoursWorked);
				}
			}

			try {
				
				if(uF.parseToInt(strStatus)==1) {
					pst = con.prepareStatement("UPDATE attendance_details SET in_out_timestamp=?, approved=?, approval_emp_id=?, " +
						" early_late=0,approval_reason=? WHERE emp_id=? and in_out=? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD')=? and service_id=?");
					pst.setTimestamp(1, uF.getTimeStamp(strDate + strStart, DBDATE + DBTIME));
					pst.setInt(2, uF.parseToInt(strStatus));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setString(4, getStrReason());
					pst.setInt(5, uF.parseToInt(strEmpId));
					pst.setString(6, "IN");
					pst.setDate(7, uF.getDateFormat(strDate, DBDATE));
					pst.setInt(8, uF.parseToInt(strServiceId));
//					System.out.println("pst 0 ===>> " + pst);
					pst.executeUpdate();
		            pst.close();
		            
		            pst = con.prepareStatement("UPDATE attendance_details SET in_out_timestamp=?, hours_worked=?, approved=?, approval_emp_id=?, " +
						" early_late=0,approval_reason=? WHERE emp_id=? and in_out=? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD')=? and service_id=?");
					pst.setTimestamp(1, uF.getTimeStamp(strDate + strEnd, DBDATE + DBTIME));
					pst.setDouble(2, dblHoursWorked);
					pst.setInt(3, uF.parseToInt(strStatus));
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
					pst.setString(5, getStrReason());
					pst.setInt(6, uF.parseToInt(strEmpId));
					pst.setString(7, "OUT");
					pst.setDate(8, uF.getDateFormat(strDate, DBDATE));
					pst.setInt(9, uF.parseToInt(strServiceId));
//					System.out.println("pst 1 ===>> " + pst);
					pst.executeUpdate();
		            pst.close();
		            
				} else {
					pst=con.prepareStatement("UPDATE attendance_details SET approved=?, approval_emp_id=?,approval_reason=?  " +
						" WHERE emp_id=? and in_out=? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ? and service_id=?");
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setString(3, getStrReason());
					pst.setInt(4, uF.parseToInt(strEmpId));
					pst.setString(5, "IN");
					pst.setDate(6, uF.getDateFormat(strDate, DBDATE));
					pst.setInt(7, uF.parseToInt(strServiceId));
//					System.out.println("pst 2 ===>> " + pst);
					pst.executeUpdate();
		            pst.close();
		            
		            pst=con.prepareStatement("UPDATE attendance_details SET approved=?, approval_emp_id=?,approval_reason=?  " +
						" WHERE emp_id=? and in_out=? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ? and service_id=?");
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setString(3, getStrReason());
					pst.setInt(4, uF.parseToInt(strEmpId));
					pst.setString(5, "OUT");
					pst.setDate(6, uF.getDateFormat(strDate, DBDATE));
					pst.setInt(7, uF.parseToInt(strServiceId));
//					System.out.println("pst 3 ===>> " + pst);
					pst.executeUpdate();
		            pst.close();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				if(pst !=null){
					pst.close();
				}
			}

			if(uF.parseToInt(strStatus)==1) {
				request.setAttribute("STATUS_MSG", "<img src=\"images1/icons/hd_tick_20x20.png\" border=\"0\" style=\"padding-left:10px\" title=\"Exception Approved\">");
			}
			
			if(uF.parseToInt(strStatus)==-1) {
				request.setAttribute("STATUS_MSG", "<img src=\"images1/icons/hd_cross_20x20.png\" border=\"0\" style=\"padding-left:10px\" title=\"Exception Denied\">");
			}
			
			pst = con.prepareStatement("update exception_reason set status=?,approved_date=?,approve_by=?,approved_user_type=? where emp_id=? and in_out_type=? and service_id=? and _date=?");
			pst.setInt(1, uF.parseToInt(strStatus));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(strUserTypeId));
			pst.setInt(5, uF.parseToInt(strEmpId));
			pst.setString(6, getExceptionType());
			pst.setInt(7, uF.parseToInt(strServiceId));
			pst.setDate(8, uF.getDateFormat(strDate, DBDATE));
//			System.out.println("pst exception_reason ===>> " + pst);
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}

	}
	
	

	public void updateException() {

		Connection con = null;
		PreparedStatement pst = null, pst1 = null, pst2 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmLunchDeductionService = CF.getLunchDeductionServiceDetails(con);
			
			pst = con.prepareStatement("select wlocation_id from employee_official_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("1 pst======>"+pst);
			rs = pst.executeQuery();
			String strWLocation = null;
			while(rs.next()){
				strWLocation = rs.getString("wlocation_id");
			}
            rs.close();
            pst.close();
			
			pst = con.prepareStatement("select * from roster_details where emp_id=? and _date=? and service_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strDate, DBDATE));
			pst.setInt(3, uF.parseToInt(strServiceId));
//			System.out.println("2 pst======>"+pst);
			rs = pst.executeQuery();
			String strStart = null;
			String strEnd = null;
			while(rs.next()) {
				strStart = rs.getString("_from");
				strEnd = rs.getString("_to");
			}
            rs.close();
            pst.close();
			
			String strDate1 = null;
			String strDate2 = null;


			pst = con.prepareStatement(selectClockEntries1_N);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setString(2, "IN");
			pst.setDate(3, uF.getDateFormat(strDate, DBDATE));
			pst.setInt(4, uF.parseToInt(strServiceId));
//			System.out.println("3 pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				strDate1 = uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DBDATE);
				strDate2 = uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DBDATE);
			}
            rs.close();
            pst.close();

			if (strDate2 == null) {
//				strDate2 = uF.getDateFormat(strDate, CF.getStrReportDateFormat())+"";
				strDate2 = uF.getDateFormat(strDate, DBDATE)+"";
			}

			
//			System.out.println("UExc/299--strStatus=="+strStatus);
			
			// Created By dattatray date:11-11-21 Note :  getExceptionMode().equals("IN") AND getExceptionMode().equals("OUT") if condition committed
			if(uF.parseToInt(strStatus) != -1) {
				
	//==start parvez date: 06-12-2021===
//			if (getExceptionMode()!=null && getExceptionMode().equals("IN") && getStrStartTime() != null && !getStrStartTime().equals("")) {
			if (getExceptionMode()!=null && getStrStartTime() != null && !getStrStartTime().equals("")) {
	//===end parvez date: 06-12-2021===

				long lIn = 0;
				long lOut = 0;
				
				/*if(uF.parseToInt(strStatus)==1) {
					strActualStartTime = strStart;
				}*/
					
//				System.out.println("CF.getStrReportTimeFormat()======>"+CF.getStrReportTimeFormat());
				
			//===start parvez date: 05-12-2021===
//				if (strDate2 != null && getStrEndTime() != null && !getStrEndTime().equalsIgnoreCase(NO_TIME_RECORD)) {
				if (strDate2 != null && getStrEndTime() != null && !getStrEndTime().equals("") && !getStrEndTime().equalsIgnoreCase(NO_TIME_RECORD)) {
			//===end parvez date: 05-12-2021===
					
//					lIn = uF.getTimeFormat(strDate2 + strActualStartTime, DBDATE + CF.getStrReportTimeFormat()).getTime();
					lIn = uF.getTimeFormat(strDate2 + getStrEndTime(), DBDATE + DBTIME).getTime();
					
				}

				pst = con.prepareStatement(selectClockEntries1_N);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setString(2, "OUT");
				pst.setDate(3, uF.getDateFormat(strDate2, DBDATE));
				pst.setInt(4, uF.parseToInt(strServiceId));
//				System.out.println("4 pst======>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					lOut = rs.getTimestamp("in_out_timestamp").getTime();
				}
	            rs.close();
	            pst.close();

				double dblHoursWorked = 0;
				if (lOut > 0) {
					dblHoursWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(lIn, lOut));
					boolean isLunchDeductionService = uF.parseToBoolean((String) hmLunchDeductionService.get(strServiceId));
					if(isLunchDeductionService) {
						dblHoursWorked = CF.calculateTimeDeduction(con, dblHoursWorked);
					}
				}

				try {
					
					if(uF.parseToInt(strStatus)==1) {
						//Created By Dattatray Date : 12-11-21 Note: Reason Added
						pst = con.prepareStatement("UPDATE attendance_details SET in_out_timestamp=?, approved=?, approval_emp_id=?, " +
							" early_late=0,approval_reason=?,reason=?  WHERE emp_id=? and in_out=? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ? and service_id=?"); //hours_worked=?, 
					} else {
						//pst = con.prepareStatement(updateClockEntries2_N);
						//Created By Dattatray Date : 12-11-21 Note: Reason Added
						pst=con.prepareStatement("UPDATE attendance_details SET in_out_timestamp=?, approved=?, approval_emp_id=?,approval_reason=?,reason=?  " +
							" WHERE emp_id=? and in_out=? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ? and service_id=?"); //hours_worked=?, 
					}
//					pst.setTimestamp(1, uF.getTimeStamp(strDate2 + strActualStartTime, DBDATE + CF.getStrReportTimeFormat()));
					System.out.println("UExep/361--date2="+strDate2);
					System.out.println("UExep/362--startTime="+getStrStartTime());
					System.out.println("UExep/362--endTime2="+getStrEndTime());
					pst.setTimestamp(1, uF.getTimeStamp(strDate2 + getStrStartTime(), DBDATE + DBTIME));
//					pst.setDouble(2, dblHoursWorked);
					pst.setInt(2, uF.parseToInt(strStatus));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setString(4, getStrReason());
					pst.setString(5, getEmployeeReason());//Created By Dattatray Date : 12-11-21 Note: Reason Added
					pst.setInt(6, uF.parseToInt(strEmpId));
					pst.setString(7, "IN");
					pst.setDate(8, uF.getDateFormat(strDate2, DBDATE));
					pst.setInt(9, uF.parseToInt(strServiceId));
//					System.out.println("5 pst======>"+pst);
					int xIn = pst.executeUpdate();
		            pst.close();
					
		            
		            if (xIn == 0) {
//						pst = con.prepareStatement(insertClockEntries1_N);
		            	//Created By Dattatray Date : 12-11-21 Note: Reason Added
						pst=con.prepareStatement("INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual, " +
							"in_out, service_id, approved, approval_emp_id,approval_reason,reason) VALUES (?,?,?,?, ?,?,?,?, ?)"); // hours_worked,
						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setTimestamp(2, uF.getTimeStamp(strDate2 + strActualEndTime, DBDATE + CF.getStrReportTimeFormat()));
						pst.setTimestamp(2, uF.getTimeStamp(strDate2 + getStrStartTime(), DBDATE + DBTIME));
//						pst.setTimestamp(3, uF.getTimeStamp(strDate2 + strActualEndTime, DBDATE + CF.getStrReportTimeFormat()));
						pst.setTimestamp(3, uF.getTimeStamp(strDate2 + getStrStartTime(), DBDATE + DBTIME));
//						pst.setDouble(4, dblHoursWorked);
						pst.setString(4, "IN");
						pst.setInt(5, uF.parseToInt(strServiceId));
						pst.setInt(6, uF.parseToInt(strStatus));
						pst.setInt(7, uF.parseToInt(strSessionEmpId));
						pst.setString(8, getStrReason());
						pst.setString(9, getEmployeeReason());//Created By Dattatray Date : 12-11-21 Note: Reason Added
//						System.out.println("5.1 pst======>"+pst);
						pst.execute();
			            pst.close();

					}
		            
//					if(uF.parseToInt(strStatus)<1){
//						updateBreakRegisters(uF, con, "IN", strDate2, strStart, lIn, strActualStartTime, strWLocation);
//					}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if(pst !=null){
						pst.close();
					}
				}


				try {

					boolean isLunchDeductionService = uF.parseToBoolean((String) hmLunchDeductionService.get(strServiceId));
					if(isLunchDeductionService) {
						dblHoursWorked = CF.calculateTimeDeduction(con, dblHoursWorked);
					}

					//pst = con.prepareStatement(updateClockEntries3_N);
					pst=con.prepareStatement("UPDATE attendance_details SET hours_worked=? WHERE emp_id=? and in_out=? and " +
							"TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ? and service_id=?");
					pst.setDouble(1, dblHoursWorked);
					pst.setInt(2, uF.parseToInt(strEmpId));
					pst.setString(3, "OUT");
					pst.setDate(4, uF.getDateFormat(strDate2, DBDATE));
					pst.setInt(5, uF.parseToInt(strServiceId));
//					System.out.println("6 pst======>"+pst);
					pst.execute();
		            pst.close();

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(pst!=null){
			            pst.close();
					}
				}
			}

		//===start parvez date: 06-12-2021===	
//			if (getExceptionMode()!=null && getExceptionMode().equals("OUT") && getStrEndTime()!= null && !getStrEndTime().equals("")) {
			if (getExceptionMode()!=null && getStrEndTime()!= null && !getStrEndTime().equals("")) {
		//===end parvez date: 06-12-2021===

				long lIn1 = 0;
				long lOut1 = 0;
				
				/*if(uF.parseToInt(strStatus)==1){
					strActualEndTime = strEnd;
				}*/
			
			//===start parvez date: 05-12-2021====
//				if (strDate2 != null && getStrEndTime() != null && !getStrEndTime().equalsIgnoreCase(NO_TIME_RECORD)) {
				if (strDate2 != null && getStrEndTime() != null && !getStrEndTime().equals("") && !getStrEndTime().equalsIgnoreCase(NO_TIME_RECORD)) {
			//===end parvez date: 05-12-2021===
					
//					lOut = uF.getTimeFormat(strDate2 + strActualEndTime, DBDATE + CF.getStrReportTimeFormat()).getTime();
					lOut1 = uF.getTimeFormat(strDate2 + getStrEndTime(), DBDATE + DBTIME).getTime();
//					System.out.println("lOut ===>> " + lOut);
				}

//				if (strDate2 == null) {
//					strDate2 = strDate;
//				}
				pst = con.prepareStatement(selectClockEntries1_N);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setString(2, "IN");
				pst.setDate(3, uF.getDateFormat(strDate2, DBDATE));
				pst.setInt(4, uF.parseToInt(strServiceId));
//				System.out.println("7 pst======>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
//					System.out.println("in ===>> " + rs.getTimestamp("in_out_timestamp"));
					lIn1 = rs.getTimestamp("in_out_timestamp").getTime();
				}
	            rs.close();
	            pst.close();
//	            System.out.println("lIn ===>> " + lIn);
	            
//	            System.out.println();
				double dblHoursWorked1 = 0;
				if (lIn1 > 0) {
					dblHoursWorked1 = uF.parseToDouble(uF.getTimeDiffInHoursMins(lIn1, lOut1));
					boolean isLunchDeductionService = uF.parseToBoolean((String) hmLunchDeductionService.get(strServiceId));
					if(isLunchDeductionService) {
						dblHoursWorked1 = CF.calculateTimeDeduction(con, dblHoursWorked1);
					}
				}

				try {
					
					if(uF.parseToInt(strStatus)==1) {
						pst = con.prepareStatement("UPDATE attendance_details SET in_out_timestamp=?, hours_worked=? , approved=?, approval_emp_id=?," +
							" early_late=0,approval_reason=?,reason=?  WHERE emp_id=? and in_out=? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ? " +
							" and service_id=?");//Created By Dattatray Date : 12-11-21 Note: Reason Added
					} else {
						//pst = con.prepareStatement(updateClockEntries2_N);
						pst=con.prepareStatement("UPDATE attendance_details SET in_out_timestamp=?, hours_worked=? , approved=?, approval_emp_id=?  " +
							",approval_reason=?,reason=? WHERE emp_id=? and in_out=? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ? and service_id=?");//Created By Dattatray Date : 12-11-21 Note: Reason Added
					}
//					pst.setTimestamp(1, uF.getTimeStamp(strDate2 + strActualEndTime, DBDATE + CF.getStrReportTimeFormat()));
					pst.setTimestamp(1, uF.getTimeStamp(strDate2 + getStrEndTime(), DBDATE + DBTIME));
					pst.setDouble(2, dblHoursWorked1);
					pst.setInt(3, uF.parseToInt(strStatus));
					pst.setInt(4, uF.parseToInt(strSessionEmpId));
					pst.setString(5, getStrReason());
					pst.setString(6, getEmployeeReason());//Created By Dattatray Date : 12-11-21 Note: Reason Added
					pst.setInt(7, uF.parseToInt(strEmpId));
					pst.setString(8, "OUT");
					pst.setDate(9, uF.getDateFormat(strDate2, DBDATE));
					pst.setInt(10, uF.parseToInt(strServiceId));
//					System.out.println("8 pst======>"+pst);
					int xOut = pst.executeUpdate();
		            pst.close();

					if (xOut == 0) {
//						pst = con.prepareStatement(insertClockEntries1_N);
						pst=con.prepareStatement("INSERT INTO attendance_details (emp_id, in_out_timestamp, in_out_timestamp_actual , hours_worked, " +
								"in_out, service_id, approved, approval_emp_id,approval_reason,reason) VALUES (?,?,?,?, ?,?,?,?, ?,?)");//Created By Dattatray Date : 12-11-21 Note: Reason Added
						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setTimestamp(2, uF.getTimeStamp(strDate2 + strActualEndTime, DBDATE + CF.getStrReportTimeFormat()));
						pst.setTimestamp(2, uF.getTimeStamp(strDate2 + getStrEndTime(), DBDATE + DBTIME));
//						pst.setTimestamp(3, uF.getTimeStamp(strDate2 + strActualEndTime, DBDATE + CF.getStrReportTimeFormat()));
						pst.setTimestamp(3, uF.getTimeStamp(strDate2 + getStrEndTime(), DBDATE + DBTIME));
						pst.setDouble(4, dblHoursWorked1);
						pst.setString(5, "OUT");
						pst.setInt(6, uF.parseToInt(strServiceId));
						pst.setInt(7, uF.parseToInt(strStatus));
						pst.setInt(8, uF.parseToInt(strSessionEmpId));
						pst.setString(9, getStrReason());
						pst.setString(10, getEmployeeReason());//Created By Dattatray Date : 12-11-21
//						System.out.println("9 pst======>"+pst);
						pst.execute();
			            pst.close();

					}
//					if(uF.parseToInt(strStatus)<1){
//						updateBreakRegisters(uF, con, "OUT", strDate2, strEnd, lOut, strActualEndTime, strWLocation);
//					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(pst!=null){
			            pst.close();
					}
				}

			}
			}
			
			if(uF.parseToInt(strStatus)==1) {
				
				request.setAttribute("STATUS_MSG", "<img src=\"images1/icons/hd_tick_20x20.png\" border=\"0\" style=\"padding-left:10px\" title=\"Exception Approved\">");
				
//				Notifications nF = new Notifications(APPROVED_DENIED_EXCEPTION, CF);
//				nF.setStrEmpId(strEmpId);
//				nF.setStrDate(uF.getDateFormat(strDate2, DBDATE, CF.getStrReportDateFormat()));
//				nF.setStrApprvedDenied("approved");
//				nF.setStrHostAddress(CF.getStrEmailLocalHost());
//				nF.setStrHostPort(CF.getStrHostPort());
//				nF.setStrContextPath(request.getContextPath());
//				nF.setEmailTemplate(true);
//				nF.sendNotifications();
			}
			
			if(uF.parseToInt(strStatus)==-1) {
				
				request.setAttribute("STATUS_MSG", "<img src=\"images1/icons/hd_cross_20x20.png\" border=\"0\" style=\"padding-left:10px\" title=\"Exception Denied\">");
				
//				Notifications nF = new Notifications(APPROVED_DENIED_EXCEPTION, CF);
//				nF.setStrEmpId(strEmpId);
//				nF.setStrDate(uF.getDateFormat(strDate2, DBDATE, CF.getStrReportDateFormat()));
//				nF.setStrApprvedDenied("denied");
//				nF.setStrHostAddress(CF.getStrEmailLocalHost());
//				nF.setStrHostPort(CF.getStrHostPort());
//				nF.setStrContextPath(request.getContextPath());
//				nF.setEmailTemplate(true);
//				nF.sendNotifications();
			}
			
			pst = con.prepareStatement("update exception_reason set status=?,approved_date=?,approve_by=?,approved_user_type=?,approve_by_reason=? where emp_id=? and in_out_type=? and service_id=? and _date=?");
			pst.setInt(1, uF.parseToInt(strStatus));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(strUserTypeId));
			pst.setString(5, uF.showData(getStrReason(), ""));//Created By Dattatray Date:10-11-21
			pst.setInt(6, uF.parseToInt(strEmpId));
			if (getExceptionMode()!=null && getExceptionMode().equals("OUT") && getStrEndTime()!=null && !getStrEndTime().equals("")) {
				pst.setString(7, "OUT");
			} else if (getExceptionMode()!=null && getExceptionMode().equals("IN") && getStrStartTime()!=null && !getStrStartTime().equals("")) {
				pst.setString(7, "IN");
			} else {
				pst.setString(7, "IN_OUT");
			}
			pst.setInt(8, uF.parseToInt(strServiceId));
			pst.setDate(9, uF.getDateFormat(strDate2, DBDATE));
//			System.out.println("10 pst======>"+pst);
			pst.execute();
            pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}

	}
	
	
	
	private void updateBreakRegisters(UtilityFunctions uF, Connection con, String strMode, String strDate2, String strStart, long lIn, String strActualTime, String strWLocation){
		try {
			String orgId = CF.getEmpOrgId(con, uF, strEmpId);
			
//			String []arr = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			String [] arr = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, orgId);
			
			Map hmBreakBalance = new HashMap();
			Map hmBreakTaken = new HashMap();
			Map hmBreakUnPaid = new HashMap();
			
			Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap(con);
			String levelid=hmEmpLevelMap.get(strEmpId);
			
//			PreparedStatement pst = con.prepareStatement("select a.emp_id, br.balance, a.break_type_id, br.taken_paid, br.taken_unpaid from break_register br, ( select max(_date) as _date,emp_id, break_type_id from break_register where _date <= ? group by emp_id,break_type_id ) a where br._date = a._date and br.emp_id = a.emp_id and br.break_type_id = a.break_type_id and a.emp_id = ?");
			/*PreparedStatement pst = con.prepareStatement("select a.emp_id, br.balance, a.break_type_id, br.taken_paid, br.taken_unpaid from " +
					"break_register br, ( select max(register_id) as register_id,emp_id, break_type_id from break_register where _date <= ? " +
					"group by emp_id,break_type_id ) a join leave_break_type lbt on lbt.break_type_id=a.break_type_id where br.register_id = a.register_id and br.emp_id = a.emp_id and br.break_type_id = a.break_type_id " +
					"and a.emp_id = ?");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setDate(1, uF.getCurrentDate("2013-12-31"));
			pst.setDate(1, uF.getDateFormat(arr[1], DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(strEmpId));
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				hmBreakBalance.put(rs.getString("break_type_id"), rs.getString("balance"));
				hmBreakTaken.put(rs.getString("break_type_id"), rs.getString("taken_paid"));
				hmBreakUnPaid.put(rs.getString("break_type_id"), rs.getString("taken_unpaid"));
			}
			System.out.println("=======>"+pst);*/
			
			Map<String,String> hmEmpBreakTaken=new HashMap<String, String>();
			//pst = con.prepareStatement("select break_type_id,sum(leave_no)as no_of_leaves from break_application_register where emp_id=? and is_paid = true group by break_type_id");
			/*pst = con.prepareStatement("select a.break_type_id,sum(leave_no)as no_of_leaves from break_application_register a join leave_break_type " +
					"lbt on lbt.break_type_id=a.break_type_id where emp_id=? and is_paid = true group by a.break_type_id ");*/
			PreparedStatement pst = con.prepareStatement("select a.break_type_id,sum(leave_no)as no_of_leaves from break_application_register a " +
					" join leave_break_type lbt on lbt.break_type_id=a.break_type_id where emp_id=? and is_paid = true " +
					" and _date between ? and ? and is_modify=false group by a.break_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT)); 
//			System.out.println("=======>"+pst);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				hmEmpBreakTaken.put(rs.getString("break_type_id"), rs.getString("no_of_leaves"));
				hmBreakTaken.put(rs.getString("break_type_id"), rs.getString("no_of_leaves"));
			}
            rs.close();
            pst.close();
			
			//pst = con.prepareStatement("select a.break_type_id,days from break_policy a,emp_leave_break_type elt where a.break_type_id=elt.break_type_id and a.wlocation_id=elt.wlocation_id and a.wlocation_id=? and level_id=?");
			pst = con.prepareStatement("select a.break_type_id,days from (select a.break_type_id,days from break_policy a,emp_leave_break_type elt " +
					" where a.break_type_id=elt.break_type_id and a.wlocation_id=elt.wlocation_id and a.wlocation_id=? and level_id=?) as a " +
					" join leave_break_type lbt on lbt.break_type_id=a.break_type_id");
			pst.setInt(1, uF.parseToInt(strWLocation));
			pst.setInt(2, uF.parseToInt(levelid));
//			System.out.println("=======>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
//				System.out.println("days=======>"+rs.getString("days"));
//				System.out.println("break_type_id=======>"+rs.getString("break_type_id"));
				double totalBalance=uF.parseToDouble(rs.getString("days"))-uF.parseToDouble(hmEmpBreakTaken.get(rs.getString("break_type_id")));
				hmBreakBalance.put(rs.getString("break_type_id"), ""+totalBalance);
				/*hmBreakTaken.put(rs.getString("break_type_id"), rs.getString("taken_paid"));
				hmBreakUnPaid.put(rs.getString("break_type_id"), rs.getString("taken_unpaid"));*/
			}
            rs.close();
            pst.close();
			
			
			long lIn1 = uF.getTimeFormat(strDate2 + strStart, DBDATE + DBTIME).getTime();
			long tDiff = (lIn - lIn1);
			
			if(strMode!=null && strMode.equalsIgnoreCase("IN")){
				tDiff = (lIn - lIn1);
			}else{
//				tDiff = (lIn1 - lIn);
				tDiff = (lIn - lIn1);
			}
			
			long diffMinutes = 0;
			if(tDiff>0 || tDiff<0){
				long diffHours = tDiff / (1000 * 60 * 60);
//				diffMinutes = (tDiff % (1000 * 60 * 60)) / (1000 * 60);
				diffMinutes = Math.abs((tDiff)/60000);   
			}
			
			
			if( (strMode.equalsIgnoreCase("IN") && tDiff<0) || strMode.equalsIgnoreCase("OUT") && tDiff>0){
				return;
			}

			
//			pst = con.prepareStatement("select * from  break_policy where wlocation_id = ? and time_value>=? and _mode like ?  order by time_value limit 1");
			pst = con.prepareStatement("select * from  break_policy a join leave_break_type lbt on lbt.break_type_id=a.break_type_id " +
				"where a.wlocation_id = ? and a.time_value>=? and a._mode like ?  order by a.time_value limit 1");
			pst.setInt(1, uF.parseToInt(strWLocation));
			pst.setDouble(2, diffMinutes);
			pst.setString(3, "%"+strMode+"%");
			rs = pst.executeQuery();
			String strBreakPolicyId = null;
			String strTimeValue = null;
			boolean isAvailable = false;
//			System.out.println("=======>"+pst);
//			System.out.println("strBreakPolicyId=1==>"+strBreakPolicyId);
			
			while(rs.next()){
				strBreakPolicyId = rs.getString("break_type_id");
				strTimeValue = rs.getString("time_value");
				
				isAvailable = true;
//				System.out.println("strBreakPolicyId=2==>"+strBreakPolicyId);
			}
            rs.close();
            pst.close();
//			System.out.println("pst===>"+pst);
			
			double dblBalance = uF.parseToDouble((String)hmBreakBalance.get(strBreakPolicyId));
			double dblTakenPaid = uF.parseToDouble((String)hmBreakTaken.get(strBreakPolicyId));
			double dblTakenUnPaid = uF.parseToDouble((String)hmBreakUnPaid.get(strBreakPolicyId));
			
//			System.out.println("dblBalance==>"+dblBalance);
//			System.out.println(strBreakPolicyId+" hmBreakBalance==>"+hmBreakBalance);
			
			int k=0;
			for(k=0; k<5 && dblBalance==0 && isAvailable; k++){
				if(dblBalance==0) {
					//pst = con.prepareStatement("select * from  break_policy where wlocation_id = ? and time_value > ? and _mode like ? order by time_value limit 1");
					pst = con.prepareStatement("select * from  break_policy a join leave_break_type lbt on lbt.break_type_id=a.break_type_id " +
						"where a.wlocation_id = ? and a.time_value>=? and a._mode like ?  order by a.time_value limit 1");					
					pst.setInt(1, uF.parseToInt(strWLocation));
					pst.setDouble(2, uF.parseToDouble(strTimeValue));
					pst.setString(3, "%"+strMode+"%");
//					System.out.println("=======>"+pst);
					rs = pst.executeQuery();
					while(rs.next()) {
						strBreakPolicyId = rs.getString("break_type_id");
						strTimeValue = rs.getString("time_value");
					}
		            rs.close();
		            pst.close();
					
					dblBalance = uF.parseToDouble((String)hmBreakBalance.get(strBreakPolicyId));
					dblTakenPaid = uF.parseToDouble((String)hmBreakTaken.get(strBreakPolicyId));
					dblTakenUnPaid = uF.parseToDouble((String)hmBreakUnPaid.get(strBreakPolicyId));
//					System.out.println("dblBalance= k="+k+" =>"+dblBalance+" pst="+pst);
				}
			}
			
			
			if(diffMinutes<120 && dblBalance==0) {
				strBreakPolicyId = "-2";
				dblBalance = uF.parseToDouble((String)hmBreakBalance.get(strBreakPolicyId));
				dblTakenPaid = uF.parseToDouble((String)hmBreakTaken.get(strBreakPolicyId));
				dblTakenUnPaid = uF.parseToDouble((String)hmBreakUnPaid.get(strBreakPolicyId));
				dblTakenUnPaid += 1;
				dblTakenPaid = 0;
				
			} else if(dblBalance==0) {							
				strBreakPolicyId = "-1";
				dblBalance = uF.parseToDouble((String)hmBreakBalance.get(strBreakPolicyId));
				dblTakenPaid = uF.parseToDouble((String)hmBreakTaken.get(strBreakPolicyId));
				dblTakenUnPaid = uF.parseToDouble((String)hmBreakUnPaid.get(strBreakPolicyId));
				
				dblTakenUnPaid += 1;
				dblTakenPaid = 0;
			} else {
				dblTakenPaid += 1;
				dblTakenUnPaid = 0;
			}
				
			pst = con.prepareStatement("insert into break_application_register(_date, emp_id, break_type_id, leave_no, is_paid, balance, _type) values (?,?,?,?,?,?,?)");
			pst.setDate(1, uF.getDateFormat(strDate2, DBDATE));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setInt(3, uF.parseToInt(strBreakPolicyId));
			pst.setInt(4, 1);
			if(dblBalance==0) {
				pst.setBoolean(5, false);
			} else {
				pst.setBoolean(5, true);
			}
			if(dblBalance>0) {
				pst.setDouble(6, (dblBalance - 1));
			} else {
				pst.setDouble(6, dblBalance);
			}
			pst.setString(7, strMode);
			pst.execute();
            pst.close();
			
			pst = con.prepareStatement("update break_register set taken_paid =?,taken_unpaid =?, balance=? where break_type_id =? and _date=? and emp_id =?");
			pst.setDouble(1, (dblTakenPaid));
			pst.setDouble(2, (dblTakenUnPaid));
			if(dblBalance>0) {
				pst.setDouble(3, (dblBalance - 1));
			} else {
				pst.setDouble(3, dblBalance);
			}
			pst.setInt(4, uF.parseToInt(strBreakPolicyId));
			pst.setDate(5, uF.getDateFormat(strDate2, DBDATE));
			pst.setInt(6, uF.parseToInt(strEmpId));
			int x = pst.executeUpdate();
            pst.close();
			
//			System.out.println("== update =>"+pst);
			
			if(x==0) {
				pst = con.prepareStatement("insert into break_register(_date, emp_id, taken_paid, balance, taken_unpaid, break_type_id) values (?,?,?,?,?,?)");
				pst.setDate(1, uF.getDateFormat(strDate2, DBDATE));
				pst.setInt(2, uF.parseToInt(strEmpId));
				pst.setDouble(3, dblTakenPaid);
				if(dblBalance>0) {
					pst.setDouble(4, (dblBalance - 1));
				} else {
					pst.setDouble(4, dblBalance);
				}
				pst.setDouble(5, dblTakenUnPaid);
				pst.setInt(6, uF.parseToInt(strBreakPolicyId));
				pst.execute();
	            pst.close();
//				System.out.println("== insert ===>> " + pst);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrReason() {
		return strReason;
	}

	public void setStrReason(String strReason) {
		this.strReason = strReason;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public String getStrStartTime() {
		return strStartTime;
	}

	public void setStrStartTime(String strStartTime) {
		this.strStartTime = strStartTime;
	}

	public String getStrEndTime() {
		return strEndTime;
	}

	public void setStrEndTime(String strEndTime) {
		this.strEndTime = strEndTime;
	}

	public String getExceptionMode() {
		return exceptionMode;
	}

	public void setExceptionMode(String exceptionMode) {
		this.exceptionMode = exceptionMode;
	}

	public String getEmployeeReason() {
		return employeeReason;
	}

	public void setEmployeeReason(String employeeReason) {
		this.employeeReason = employeeReason;
	}

}