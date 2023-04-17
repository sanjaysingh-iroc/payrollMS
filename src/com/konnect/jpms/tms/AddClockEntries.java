package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddClockEntries extends ActionSupport implements ServletRequestAware, IStatements {
   
	/**
	 *
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSelectedEmpId;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF = null;
	String divid;
	
	String paycycle;
	String f_org;
	String location;
	String level;
	 
	String strApStatus;
	String strApStatusTmp;
	String strOTMinuteStatus;
	String timeApprovalType;//Created By Dattatray Date:01-11-21
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		String strEdit = (String)request.getParameter("E");
		String strStatus = (String)request.getParameter("S");
		String strServiceId = (String)request.getParameter("SID");
		String strEmpID = (String)request.getParameter("EID");
//		String EID = (String)request.getParameter("EID");
		String strDate = (String)request.getParameter("DATE");
		String strAS = (String)request.getParameter("AS");
		String strAE = (String)request.getParameter("AE");
		String apStatus = (String) request.getParameter("apStatus");
//		String otMinuteStatus = (String) request.getParameter("otMinuteStatus");
		
		String strId = (String)request.getParameter("RID");
		String strType = (String)request.getParameter("T");
		if(strType!=null && strType.equals("bulk")) {
			//updateBulkClockEntries(strStatus, strId);
		}
		
		if(getStrApStatusTmp() == null) {
			setStrApStatusTmp(apStatus);
		}
//		System.out.println("paycycle======>"+paycycle);
//		System.out.println("getIsExceptionAutoApprove======>"+CF.getIsExceptionAutoApprove());
		if((uF.parseToInt(apStatus)!=1 || uF.parseToInt(getStrApStatus())!=1)) { //uF.parseToBoolean(CF.getIsExceptionAutoApprove()) && 
			if(getStrDelete()!=null && getStrDelete().equalsIgnoreCase("D")) {
				deleteClockEntries(strDate, strEmpID, strServiceId);
				return "delete";
			}
			
//			System.out.println("getStrStartTime() ======> " + getStrStartTime());
//			System.out.println("getStrE() ======> " + getStrE());
//			System.out.println("strStatus ======> " + strStatus);
//			System.out.println("strDate ======> " + strDate);
			if(getStrStartTime()!=null && getStrE()!=null) {
//				System.out.println("1");
				updateClockEntries();
//				System.out.println("getStrStatus()======>"+getStrStatus());
				if(getStrStatus()!=null && !getStrStatus().equals("") &&!getStrStatus().equalsIgnoreCase("NULL")) {
					return "exceptions";
				} else {
	//				return "clockentries";
					return "delete";
				}
				
			} else if(uF.parseToInt(strStatus)==-1) {
//				
//				System.out.println("2");
//				System.out.println("in -1 ======>>>>> ");
				setStrDate(strDate);
				setStrEmpId(strEmpID);
				setStrServiceId(strServiceId);
				setStrStatus(strStatus);
				
				updateExceptionStatus();
//				return SUCCESS;
				return "delete";
			}
		} else {
			if(getStrDelete()!=null && getStrDelete().equalsIgnoreCase("D")) {
				System.out.println("3");
				deleteClockEntries(strDate, strEmpID, strServiceId);
				return "delete";
			} else if(getStrStartTime()!=null && ((getStrE()!=null && getStrE().length()>0) || getStrE()==null)) {
				System.out.println("4");
				insertClockEntries();
				return "delete";
//				return SUCCESS;
			} if(getStrStartTime()!=null && getStrE()!=null) {
				System.out.println("5");
				updateClockEntries1();
				
				if(getStrStatus()!=null && !getStrStatus().equalsIgnoreCase("NULL")) {
					return "exceptions";
				} else {
					return "clockentries";  
				}
				
			} else if(uF.parseToInt(strStatus)==-1) {
				System.out.println("6");
				setStrDate(strDate);
				setStrEmpId(strEmpID);
				setStrServiceId(strServiceId);
				setStrStatus(strStatus);
				
				updateExceptionStatus();
				return SUCCESS;
			}
		}
		
		
		if(strAS!=null) {
			setStrStartTime(strAS);			
		}
		if(strAE!=null) {
			setStrEndTime(strAE);			
		}
		
		
		if(strEdit!=null) {
			setStrE(strEdit);			
		}
		
//		System.out.println("start time ; "+getStrStartTime()+" end time : "+getStrEndTime());
		loadClockEntries(strServiceId, strEmpID, strDate);
		return LOAD;

	}
	
	public void loadClockEntries(String strServiceId, String  strEmpID, String  strDate) {
//		System.out.println(" loadClockEntries()");
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);	
				// Started By Dattatray Date:01-11-21
			if (getTimeApprovalType() !=null && getTimeApprovalType().equals("individualCE")) {
				pst = con.prepareStatement("SELECT * FROM roster_details where _date = ? and service_id =? and emp_id =?");
				pst.setDate(1, uF.getDateFormat(strDate, DBDATE));
				pst.setInt(2, uF.parseToInt(strServiceId));
				pst.setInt(3, uF.parseToInt(strEmpID));
				rs = pst.executeQuery();
				if(rs.next()) {
					if(getStrStartTime()==null || (getStrStartTime()!=null && getStrStartTime().length()==0)) {
						setStrStartTime(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
					}
					if(getStrEndTime()==null || (getStrEndTime()!=null && getStrEndTime().length()==0)) {
						setStrEndTime(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
					}
				}
				rs.close();
				pst.close();
			}else {
				pst = con.prepareStatement("select * from exception_reason where emp_id =? and _date=? and service_id=?");
				pst.setInt(1, uF.parseToInt(strEmpID));
				pst.setDate(2, uF.getDateFormat(strDate, DBDATE));
				pst.setInt(3, uF.parseToInt(strServiceId));
//				System.out.println("pst1111======>"+pst);
				rs = pst.executeQuery();
				boolean flagIn=false;
				boolean flagOut=false;
				while(rs.next()) {
					if (rs.getString("in_timestamp") !=null && !rs.getString("in_timestamp").isEmpty() && rs.getString("out_timestamp") !=null && !rs.getString("out_timestamp").isEmpty()) {
						setStrStartTime(uF.getDateFormat(rs.getString("in_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
						setStrEndTime(uF.getDateFormat(rs.getString("out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
						setStrInOutType(rs.getString("in_out_type"));
						flagIn = true;
						flagOut = true;
					}else if (rs.getString("out_timestamp") !=null && !rs.getString("out_timestamp").isEmpty()) {
						setStrEndTime(uF.getDateFormat(rs.getString("out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
						setStrInOutType(rs.getString("in_out_type"));
						flagOut = true;
					}
					
				}
				rs.close();
				pst.close();
			}
			// Ended By Dattatray Date:01-11-21
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void insertClockEntries() {
		System.out.println(" insertClockEntries()");
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst1 = con.prepareStatement("select * from roster_details where emp_id =? and _date=? and service_id=?");
			pst1.setInt(1, uF.parseToInt(getStrEmpId()));
			pst1.setDate(2, uF.getDateFormat(getStrDate(), DBDATE));
			pst1.setInt(3, uF.parseToInt(getStrServiceId()));
//			System.out.println("pst1======>"+pst1);
			rs = pst1.executeQuery();
			Time rsStart = null;
			Time rsEnd = null;
			while(rs.next()) {
				rsStart = uF.getTimeFormat(rs.getString("_from"));
				rsEnd = uF.getTimeFormat(rs.getString("_to"));
			}
			rs.close();
			pst1.close();
			
			
			
			if(uF.parseToInt(getStrStatus()) == 1 || uF.parseToInt(getStrApStatusTmp()) == 1) {
				long lStart = 0L;
				long lEnd = 0L;
				
				if(getStrStartTime()!=null && getStrEndTime()!=null) {
					Time start = uF.getTimeFormat(getStrStartTime(), CF.getStrReportTimeFormat());
					Time end = uF.getTimeFormat(getStrEndTime(), CF.getStrReportTimeFormat());
					
					lStart = start.getTime();
					lEnd = end.getTime();
					
				}
				
				pst = con.prepareStatement(insertClockEntries11_N);
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setTimestamp(2, uF.getTimeStamp(""+getStrDate()+""+getStrStartTime(), DBDATE+DBTIME));
				pst.setTimestamp(3, uF.getTimeStamp(getStrDate()+""+getStrStartTime(), DBDATE+DBTIME));
				pst.setDouble(4, 0);
				pst.setString(5, "IN");
				pst.setInt(6, uF.parseToInt(getStrServiceId()));
				pst.setInt(7, 1);
				pst.setInt(8, uF.parseToInt(strSessionEmpId));
				pst.setDouble(9, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, rsStart.getTime())));
//				System.out.println("pst innn ======>"+pst);
				pst.execute();
				pst.clearParameters();
				
//				System.out.println("pst..>"+pst);
				
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setTimestamp(2, uF.getTimeStamp(""+getStrDate()+""+getStrEndTime(), DBDATE+DBTIME));
				pst.setTimestamp(3, uF.getTimeStamp(getStrDate()+""+getStrEndTime(), DBDATE+DBTIME));
				pst.setDouble(4, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, lEnd)));
				pst.setString(5, "OUT");
				pst.setInt(6, uF.parseToInt(getStrServiceId()));
				pst.setInt(7, 1);
				pst.setInt(8, uF.parseToInt(strSessionEmpId));
				pst.setDouble(9, uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd, rsEnd.getTime())));
//				System.out.println("pst outtt ======>"+pst);
				pst.execute();
				pst.close();
				
//				System.out.println("pst..>"+pst);
			}
			
			updateExceptionStatus();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void deleteClockEntries(String strDate, String strEmpId, String strServiceId) {
		System.out.println(" deleteClockEntries()");
		Connection con = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from attendance_details where emp_id=? and to_date(in_out_timestamp::text, 'YYYY-MM-DD')=? and service_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strDate, DBDATE));
			pst.setInt(3, uF.parseToInt(strServiceId));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from break_application_register where emp_id=? and _date=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strDate, DBDATE));				
			pst.execute();
			pst.close();
			
			if(uF.parseToBoolean(getStrOTMinuteStatus())) {
				pst = con.prepareStatement("delete from overtime_emp_minute_status where emp_id =? and ot_date=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getDateFormat(strDate, DBDATE));
				pst.execute();
	            pst.close();
			}
			
			/*request.setAttribute("STATUS_MSG", "<img src=\"images1/tick.png\" width=\"24px\">"); */
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-check checknew\" aria-hidden=\"true\"></i>"); 
				
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void updateClockEntries() {
//		System.out.println(" updateClockEntries()");
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs =null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			
			pst1 = con.prepareStatement("select * from roster_details where emp_id =? and _date=? and service_id=?");
			pst1.setInt(1, uF.parseToInt(getStrEmpId()));
			pst1.setDate(2, uF.getDateFormat(getStrDate(), DBDATE));
			pst1.setInt(3, uF.parseToInt(getStrServiceId()));
//			System.out.println("pst1======>"+pst1);
			rs = pst1.executeQuery();
			Time rsStart = null;
			Time rsEnd = null;
			while(rs.next()) {
				rsStart = uF.getTimeFormat(rs.getString("_from"), DBTIME);
				rsEnd = uF.getTimeFormat(rs.getString("_to"), DBTIME);
			}
			rs.close();
			pst1.close();
			// Started By Dattatray Date:07-10-21
			pst1 = con.prepareStatement("select * from exception_reason where emp_id =? and _date=? and service_id=? and status = 1");
			pst1.setInt(1, uF.parseToInt(getStrEmpId()));
			pst1.setDate(2, uF.getDateFormat(getStrDate(), DBDATE));
			pst1.setInt(3, uF.parseToInt(getStrServiceId()));
//			System.out.println("pst1111======>"+pst1);
			rs = pst1.executeQuery();
			String strInTimeException = null;
			String strOutTimeException = null;
			while(rs.next()) {
				strInTimeException = rs.getString("in_timestamp");
				strOutTimeException = rs.getString("out_timestamp");
			}
			rs.close();
			pst1.close();
			// Ended By Dattatray Date:07-10-21
			long lStart = 0L;
			long lEnd = 0L;
			
			if(getStrStartTime()!=null && getStrEndTime()!=null) {
//				System.out.println("getStrStartTime() ===>> " + getStrStartTime());
//				System.out.println("CF.getStrReportTimeFormat() ===>> " + CF.getStrReportTimeFormat());
				Time start = uF.getTimeFormat(getStrStartTime(), CF.getStrReportTimeFormat());
				Time end = uF.getTimeFormat(getStrEndTime(), CF.getStrReportTimeFormat());
				
				lStart = start.getTime();
				lEnd = end.getTime();
			}
			
//			PreparedStatement pst = con.prepareStatement(updateClockEntries2);
			//PreparedStatement pst = con.prepareStatement(updateClockEntries21);
			pst=con.prepareStatement("UPDATE attendance_details SET in_out_timestamp=?, hours_worked=?, early_late=?,approved=? WHERE emp_id=? " +
					"and in_out=? and service_id = ? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ?");
			// Created By Dattatray Date:03-12-21
			if (strInTimeException !=null && getStrStartTime() == null) {// Created By Dattatray Date:07-10-21
				pst.setTimestamp(1, uF.getTimeStamp(strInTimeException, DBDATE+" "+DBTIME));
			}else {
				pst.setTimestamp(1, uF.getTimeStamp(""+getStrDate()+""+getStrStartTime(), DBDATE+CF.getStrReportTimeFormat()));
			}
			
			pst.setDouble(2, 0);
			
			if(rsStart!=null && lStart>0 && lStart>rsStart.getTime()) { 
				pst.setDouble(3, uF.parseToDouble(uF.getTimeDiffInHoursMins(rsStart.getTime(), lStart)));
				pst.setInt(4, 1);				
				
			} else if(rsStart!=null && rsStart.getTime()>0 && rsStart.getTime()>lStart) {
				pst.setDouble(3, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, rsStart.getTime())));
				pst.setInt(4, 1);				
			} else {
				pst.setDouble(3, 0);
				pst.setInt(4, 0);				
			}
			pst.setInt(5, uF.parseToInt(getStrEmpId()));
			pst.setString(6, "IN");
			pst.setInt(7, uF.parseToInt(getStrServiceId()));
			pst.setDate(8, uF.getDateFormat(strDate, DBDATE));
//			System.out.println("pst======>"+pst);
			int nIn = pst.executeUpdate();
			pst.close();
//			System.out.println("pst =1 "+pst);
			
			if(nIn==0) {
				pst = con.prepareStatement(insertClockEntries1_N1);
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				// Created By Dattatray Date:03-12-21
				if (strInTimeException !=null  && getStrStartTime() == null) {// Created By Dattatray Date:07-10-21
					pst.setTimestamp(2, uF.getTimeStamp(strInTimeException, DBDATE+" "+DBTIME));
					pst.setTimestamp(3, uF.getTimeStamp(strInTimeException, DBDATE+" "+DBTIME));
				}else {
					pst.setTimestamp(2, uF.getTimeStamp(""+getStrDate()+""+getStrStartTime(), DBDATE+CF.getStrReportTimeFormat()));
					pst.setTimestamp(3, uF.getTimeStamp(getStrDate()+""+getStrStartTime(), DBDATE+CF.getStrReportTimeFormat()));
				}
//				pst.setTimestamp(2, uF.getTimeStamp(""+getStrDate()+""+getStrStartTime(), DBDATE+CF.getStrReportTimeFormat()));
//				pst.setTimestamp(3, uF.getTimeStamp(getStrDate()+""+getStrStartTime(), DBDATE+CF.getStrReportTimeFormat()));
				pst.setDouble(4, 0);
				pst.setString(5, "IN");
				pst.setInt(6, uF.parseToInt(getStrServiceId()));
				
				pst.setInt(7, uF.parseToInt(strSessionEmpId));
				if(rsStart!=null && lStart>0 && lStart>rsStart.getTime()) { 
					pst.setDouble(8, uF.parseToDouble(uF.getTimeDiffInHoursMins(rsStart.getTime(), lStart)));
					//pst.setInt(9, -2);
					pst.setInt(9, 1);
				} else if(rsStart!=null && rsStart.getTime()>0 && rsStart.getTime()>lStart) {
					pst.setDouble(8, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, rsStart.getTime())));
					//pst.setInt(9, -2);
					pst.setInt(9, 1);
				} else {
					pst.setDouble(8, 0);
					pst.setInt(9, 0);
				}
//				System.out.println("pst======>"+pst);
				pst.execute();
				pst.close();
//				System.out.println("pst =2 "+pst);
			}
			
			
			//pst = con.prepareStatement(updateClockEntries22);
			pst=con.prepareStatement("UPDATE attendance_details SET in_out_timestamp=?, hours_worked=?, early_late=?,approved=? " +
					" WHERE emp_id=? and in_out=? and service_id = ? and TO_DATE(in_out_timestamp_actual::text, 'YYYY-MM-DD') = ?");
			// Created By Dattatray Date:03-12-21
			if (strOutTimeException !=null && getStrEndTime()==null) {// Created By Dattatray Date:07-10-21
				pst.setTimestamp(1, uF.getTimeStamp(strOutTimeException, DBDATE+" "+DBTIME));
			}else {
				pst.setTimestamp(1, uF.getTimeStamp(""+getStrDate()+""+getStrEndTime(), DBDATE+CF.getStrReportTimeFormat()));
			}
			pst.setDouble(2, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, lEnd)));
			
			if(rsEnd!=null && lEnd>0 && lEnd>rsEnd.getTime()) {
				pst.setDouble(3, uF.parseToDouble(uF.getTimeDiffInHoursMins(rsEnd.getTime(), lEnd)));
				pst.setInt(4, 1);				
			} else if(rsEnd!=null && rsEnd.getTime()>0 && rsEnd.getTime()>lEnd) {
				pst.setDouble(3, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd, rsEnd.getTime())));
				pst.setInt(4, 1);
			} else {
				pst.setDouble(3, 0);
				pst.setInt(4, 0);
			}
			pst.setInt(5, uF.parseToInt(getStrEmpId()));
			pst.setString(6, "OUT");
			pst.setInt(7, uF.parseToInt(getStrServiceId()));
			pst.setDate(8, uF.getDateFormat(strDate, DBDATE));
//			System.out.println("pst out ======>"+pst);
			nIn = pst.executeUpdate();
			pst.close();
	
//			System.out.println("pst =3 "+pst);
			
			
			if(nIn==0) {
				pst = con.prepareStatement(insertClockEntries1_N1);
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				if (strOutTimeException !=null && getStrEndTime()==null) {// Created By Dattatray Date:07-10-21
					pst.setTimestamp(2, uF.getTimeStamp(strOutTimeException, DBDATE+" "+DBTIME));
					pst.setTimestamp(3, uF.getTimeStamp(strOutTimeException, DBDATE+" "+DBTIME));
				}else {
					pst.setTimestamp(2, uF.getTimeStamp(""+getStrDate()+""+getStrEndTime(), DBDATE+CF.getStrReportTimeFormat()));
					pst.setTimestamp(3, uF.getTimeStamp(getStrDate()+""+getStrEndTime(), DBDATE+CF.getStrReportTimeFormat()));
				}
//				pst.setTimestamp(2, uF.getTimeStamp(""+getStrDate()+""+getStrEndTime(), DBDATE+CF.getStrReportTimeFormat()));
//				pst.setTimestamp(3, uF.getTimeStamp(getStrDate()+""+getStrEndTime(), DBDATE+CF.getStrReportTimeFormat()));
				pst.setDouble(4, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, lEnd)));
				pst.setString(5, "OUT");
				pst.setInt(6, uF.parseToInt(getStrServiceId()));
				
				pst.setInt(7, uF.parseToInt(strSessionEmpId));
				if(rsEnd!=null && lEnd>0 && lEnd>rsEnd.getTime()) {
					pst.setDouble(8, uF.parseToDouble(uF.getTimeDiffInHoursMins(rsEnd.getTime(), lEnd)));
					//pst.setInt(9, -2);
					pst.setInt(9, 1);
				} else if(rsEnd!=null && rsEnd.getTime()>0 && rsEnd.getTime()>lEnd) {
					pst.setDouble(8, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd, rsEnd.getTime())));
					//pst.setInt(9, -2);
					pst.setInt(9, 1);
				} else {
					pst.setDouble(8, 0);
					pst.setInt(9, 0);
				}
//				System.out.println("pst out ======>"+pst);
				pst.execute();
				pst.close();
				
//				System.out.println("pst =4 "+pst);
				
			}
			
			pst = con.prepareStatement("update exception_reason set status=1 where emp_id=? and _date=? and service_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strDate, DBDATE));
			pst.setInt(3, uF.parseToInt(getStrServiceId()));
//			System.out.println("pst======>"+pst);
			nIn = pst.executeUpdate();
			pst.close();
			
			if(nIn>0 && uF.parseToInt(getStrStatus()) ==1) {
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(APPROVED_DENIED_EXCEPTION, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(getStrEmpId());
				nF.setStrDate(uF.getDateFormat(strDate, DBDATE, CF.getStrReportDateFormat()));
				nF.setEmailTemplate(true);
				nF.sendNotifications();
			}
			if(uF.parseToBoolean(getStrOTMinuteStatus())) {
				pst = con.prepareStatement("delete from overtime_emp_minute_status where emp_id =? and ot_date=?");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDate(2, uF.getDateFormat(getStrDate(), DBDATE));
				pst.execute();
	            pst.close();
			}
			
//			System.out.println("getStrApStatusTmp() ===>> " + getStrApStatusTmp());			
			if(uF.parseToInt(getStrApStatusTmp()) == 1) {
				request.setAttribute("STATUS_MSG", "<img src=\"images1/icons/hd_tick.png\" width=\"20px\">");
			} else {
				request.setAttribute("STATUS_MSG", "Updated");
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("STATUS_MSG", "update failed");
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}
	
	
	public void updateClockEntries1() {
		System.out.println(" updateClockEntries1()");
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			pst1 = con.prepareStatement("select * from roster_details where emp_id =? and _date=? and service_id=?");
			pst1.setInt(1, uF.parseToInt(getStrEmpId()));
			pst1.setDate(2, uF.getDateFormat(getStrDate(), DBDATE));
			pst1.setInt(3, uF.parseToInt(getStrServiceId()));
			rs = pst1.executeQuery();
			Time rsStart = null;
			Time rsEnd = null;
			while(rs.next()) {
				rsStart = uF.getTimeFormat(rs.getString("_from"), DBTIME);
				rsEnd = uF.getTimeFormat(rs.getString("_to"), DBTIME);
			}
			rs.close();
			pst1.close();
			
			long lStart = 0L;
			long lEnd = 0L;
			
			if(getStrStartTime()!=null && getStrEndTime()!=null) {
				Time start = uF.getTimeFormat(getStrStartTime(), CF.getStrReportTimeFormat());
				Time end = uF.getTimeFormat(getStrEndTime(), CF.getStrReportTimeFormat());
				lStart = start.getTime();
				lEnd = end.getTime();
			}
			
//			PreparedStatement pst = con.prepareStatement(updateClockEntries2);
			pst = con.prepareStatement(updateClockEntries21);
			
			pst.setTimestamp(1, uF.getTimeStamp(""+getStrDate()+""+getStrStartTime(), DBDATE+CF.getStrReportTimeFormat()));
			pst.setDouble(2, 0);
			if(rsStart!=null && lStart>0 && lStart>rsStart.getTime()) { 
				pst.setDouble(3, uF.parseToDouble(uF.getTimeDiffInHoursMins(rsStart.getTime(), lStart)));
			} else if(rsStart!=null && rsStart.getTime()>0 && rsStart.getTime()>lStart) {
				pst.setDouble(3, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, rsStart.getTime())));
			} else {
				pst.setDouble(3, 0);
			}
			pst.setInt(4, uF.parseToInt(getStrEmpId()));
			pst.setString(5, "IN");
			pst.setInt(6, uF.parseToInt(getStrServiceId()));
			pst.setDate(7, uF.getDateFormat(strDate, DBDATE));
			int nIn = pst.executeUpdate();
			pst.close();
//			System.out.println("pst =1 "+pst);
			
//			System.out.println(" update====================");
			if(nIn==0) {
				pst = con.prepareStatement(insertClockEntries1_N1);
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setTimestamp(2, uF.getTimeStamp(""+getStrDate()+""+getStrStartTime(), DBDATE+CF.getStrReportTimeFormat()));
				pst.setTimestamp(3, uF.getTimeStamp(getStrDate()+""+getStrStartTime(), DBDATE+CF.getStrReportTimeFormat()));
				pst.setDouble(4, 0);
				pst.setString(5, "IN");
				pst.setInt(6, uF.parseToInt(getStrServiceId()));
				pst.setInt(7, uF.parseToInt(strSessionEmpId));
				if(rsStart!=null && lStart>0 && lStart>rsStart.getTime()) {
					pst.setDouble(8, uF.parseToDouble(uF.getTimeDiffInHoursMins(rsStart.getTime(), lStart)));
					pst.setInt(9, -2);
				} else if(rsStart!=null && rsStart.getTime()>0 && rsStart.getTime()>lStart) {
					pst.setDouble(8, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, rsStart.getTime())));
					pst.setInt(9, -2);
				} else {
					pst.setDouble(8, 0);
					pst.setInt(9, 0);
				}
				pst.execute();
				pst.close();
//				System.out.println("pst =2 "+pst);
			}
			
			
			pst = con.prepareStatement(updateClockEntries22);
			pst.setTimestamp(1, uF.getTimeStamp(""+getStrDate()+""+getStrEndTime(), DBDATE+CF.getStrReportTimeFormat()));
			pst.setDouble(2, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, lEnd)));
			if(rsEnd!=null && lEnd>0 && lEnd>rsEnd.getTime()) {
				pst.setDouble(3, uF.parseToDouble(uF.getTimeDiffInHoursMins(rsEnd.getTime(), lEnd)));
			} else if(rsEnd!=null && rsEnd.getTime()>0 && rsEnd.getTime()>lEnd) {
				pst.setDouble(3, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd, rsEnd.getTime())));
			} else {
				pst.setDouble(3, 0);
			}
			pst.setInt(4, uF.parseToInt(getStrEmpId()));
			pst.setString(5, "OUT");
			pst.setInt(6, uF.parseToInt(getStrServiceId()));
			pst.setDate(7, uF.getDateFormat(strDate, DBDATE));
			nIn = pst.executeUpdate();
			pst.close();
	
//			System.out.println("pst =3 "+pst);
			
			
			if(nIn==0) {
				pst = con.prepareStatement(insertClockEntries1_N1);
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setTimestamp(2, uF.getTimeStamp(""+getStrDate()+""+getStrEndTime(), DBDATE+CF.getStrReportTimeFormat()));
				pst.setTimestamp(3, uF.getTimeStamp(getStrDate()+""+getStrEndTime(), DBDATE+CF.getStrReportTimeFormat()));
				pst.setDouble(4, uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, lEnd)));
				pst.setString(5, "OUT");
				pst.setInt(6, uF.parseToInt(getStrServiceId()));
				pst.setInt(7, uF.parseToInt(strSessionEmpId));
				if(rsEnd!=null && lEnd>0 && lEnd>rsEnd.getTime()) {
					pst.setDouble(8, uF.parseToDouble(uF.getTimeDiffInHoursMins(rsEnd.getTime(), lEnd)));
					pst.setInt(9, -2);
				} else if(rsEnd!=null && rsEnd.getTime()>0 && rsEnd.getTime()>lEnd) {
					pst.setDouble(8, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd, rsEnd.getTime())));
					pst.setInt(9, -2);
				} else {
					pst.setDouble(8, 0);
					pst.setInt(9, 0);
				}
				pst.execute();
				pst.close();
//				System.out.println("pst =4 "+pst);
			}
			
			
			pst = con.prepareStatement("update exception_reason set status=1 where emp_id=? and _date=? and service_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strDate, DBDATE));
			pst.setInt(3, uF.parseToInt(getStrServiceId()));
			nIn = pst.executeUpdate();
			pst.close();
			
			if(nIn>0 && uF.parseToInt(getStrStatus()) ==1) {
				Notifications nF = new Notifications(APPROVED_DENIED_EXCEPTION, CF);
				nF.setStrEmpId(getStrEmpId());
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrDate(uF.getDateFormat(strDate, DBDATE, CF.getStrReportDateFormat()));
				nF.setEmailTemplate(true);
				nF.sendNotifications();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
	}
	
	public void updateExceptionStatus() {
		System.out.println(" updateExceptionStatus()");
		Connection con = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update exception_reason set status=? where _date=? and service_id=? and emp_id=?");
			pst.setInt(1, uF.parseToInt(getStrStatus())!=0 ? uF.parseToInt(getStrStatus()) : uF.parseToInt(getStrApStatusTmp()));
			pst.setDate(2, uF.getDateFormat(getStrDate(), DBDATE));
			pst.setInt(3, uF.parseToInt(getStrServiceId()));
			pst.setInt(4, uF.parseToInt(getStrEmpId()));
			pst.execute();
			pst.close();
//			System.out.println("pst updateExceptionStatus ==>> " + pst);
			
			
			if(uF.parseToInt(getStrStatus())==1 || uF.parseToInt(getStrApStatusTmp())==1) {
				request.setAttribute("STATUS_MSG", "<img src=\"images1/icons/hd_tick.png\" width=\"20px\">");
			} else {
				request.setAttribute("STATUS_MSG", "<img src=\"images1/icons/hd_cross_16x16.png\" width=\"20px\">");
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	String strDate;
	String strEmpId;
	String strServiceId;
	String strStartTime;
	String strEndTime;
	String strStatus;
	String strE;
	String strDelete;
	String strInOutType;
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrServiceId() {
		return strServiceId;
	}

	public void setStrServiceId(String strServiceId) {
		this.strServiceId = strServiceId;
	}

	public String getStrStatus() {
		return strStatus;
	}

	public void setStrStatus(String strStatus) {
		this.strStatus = strStatus;
	}




	public String getStrE() {
		return strE;
	}




	public void setStrE(String strE) {
		this.strE = strE;
	}

	public String getStrDelete() {
		return strDelete;
	}

	public void setStrDelete(String strDelete) {
		this.strDelete = strDelete;
	}

	public String getDivid() {
		return divid;
	}

	public void setDivid(String divid) {
		this.divid = divid;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getStrApStatus() {
		return strApStatus;
	}

	public void setStrApStatus(String strApStatus) {
		this.strApStatus = strApStatus;
	}

	public String getStrApStatusTmp() {
		return strApStatusTmp;
	}

	public void setStrApStatusTmp(String strApStatusTmp) {
		this.strApStatusTmp = strApStatusTmp;
	}
	public String getStrOTMinuteStatus() {
		return strOTMinuteStatus;
	}

	public void setStrOTMinuteStatus(String strOTMinuteStatus) {
		this.strOTMinuteStatus = strOTMinuteStatus;
	}

	public String getStrInOutType() {
		return strInOutType;
	}

	public void setStrInOutType(String strInOutType) {
		this.strInOutType = strInOutType;
	}

	public String getTimeApprovalType() {
		return timeApprovalType;
	}

	public void setTimeApprovalType(String timeApprovalType) {
		this.timeApprovalType = timeApprovalType;
	}
	
	
}