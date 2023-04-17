package com.konnect.jpms.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Types;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ClockOnOff extends ActionSupport implements IStatements, ServletRequestAware {

	private String userType;
	HttpSession session;
	CommonFunctions CF = null;
	String strEmpId; 
	
	private static Logger log = Logger.getLogger(ClockOnOff.class);
	
	public String execute() throws Exception {

		session = request.getSession(true);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		userType = (String)session.getAttribute(USERTYPE);
		String empType = (String) session.getAttribute(USERTYPE);
		String userTypeId = (String) session.getAttribute(USERTYPEID);
		strEmpId = (String) session.getAttribute("EMPID");

		request.setAttribute(TITLE, "Clock On/Off");
		request.setAttribute(PAGE, "/jsp/common/ClockOnOff.jsp");
		
		verifyClockDetails();
				
		return loadNavigationInner();
	}
	
public void verifyClockDetails(){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		CallableStatement cst = null;
		
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		
		try {
			
			con = db.makeConnection(con);
			
			
			String strPrevRosterDate = null;
			Time tPrevFrom = null;
			Time tPrevTo = null;
			
			String strRosterDate = null;
			String strFrom = null;
			String strTo = null;
			
			
			
			
			
			
			String strRosterStartTime = null;
			String strRosterEndTime = null;
			String strPrevRosterStartTime = null;
			String strPrevRosterEndTime = null;
			
			
			int nPrevServiceId = 0;
			int nCurrServiceId = 0;
			int nCount = 0;
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_roster_count(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT count(*) as cnt FROM roster_details WHERE emp_id = ? and _date = ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				nCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_out(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT * FROM roster_details WHERE emp_id =? and _date = ? and attended = 1 order by _from limit 1");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();			
			while(rs.next()){
				nPrevServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_out(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT * FROM roster_details WHERE emp_id =? and _date = ? and attended = 1 order by _from limit 1");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();	
			
			while(rs.next()){
				nCurrServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
			
			
			if(nCurrServiceId==0){
				if(nCount>1){
//					pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
					
					
//					con.setAutoCommit(false);
//					cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_in(?,?,?)}");
//					cst.registerOutParameter(1, Types.OTHER);
//					cst.setInt(2, uF.parseToInt(strEmpId));
//					cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//					cst.setTime(4, uF.getCurrentTime(CF.getStrTimeZone()));
					
					pst = con.prepareStatement("SELECT * FROM roster_details WHERE emp_id = ? and _date = ?" +
							" and (attended = 0 or attended = 2) and (_to>? OR _from>_to) order by _from limit 1");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
					
					
				}else{
//					pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					
					
//					con.setAutoCommit(false);
//					cst = con.prepareCall("{? = call sel_emp_roster(?,?)}");
//					cst.registerOutParameter(1, Types.OTHER);
//					cst.setInt(2, uF.parseToInt(strEmpId));
//					cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					
					pst = con.prepareStatement("SELECT * FROM roster_details WHERE emp_id = ? and _date = ?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					
				}
				
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				rs = pst.executeQuery();
				while(rs.next()){
					nCurrServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
			}
			
			
			if(nCurrServiceId==0){
				nCurrServiceId = nPrevServiceId;
			}
			
			
			
			
//			pst = con.prepareStatement(selectAttendenceClockDetails_N);
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
//			pst.setString(2, "OUT");
//			pst.setInt(3, uF.parseToInt(strEmpId));
//			pst.setInt(4, nPrevServiceId);
//			rs = pst.executeQuery();
//			
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_attendance_clock_details(?,?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//			cst.setString(3, "OUT");
//			cst.setInt(4, uF.parseToInt(strEmpId));
//			cst.setInt(5, nPrevServiceId);
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectAttendenceClockDetails_N);
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
			pst.setString(2, "OUT");
			pst.setInt(3, uF.parseToInt(strEmpId));
			pst.setInt(4, nPrevServiceId);
			rs = pst.executeQuery();

			boolean isPrevOut = false;
			boolean isPrevRoster = false;
			if(rs.next()){
				isPrevOut = true;
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement(selectAttendenceClockDetails_N);
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setString(2, "IN");
//			pst.setInt(3, uF.parseToInt(strEmpId));
//			pst.setInt(4, nCurrServiceId);
//			rs = pst.executeQuery();

			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_attendance_clock_details(?,?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//			cst.setString(3, "IN");
//			cst.setInt(4, uF.parseToInt(strEmpId));
//			cst.setInt(5, nPrevServiceId);
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectAttendenceClockDetails_N);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(2, "IN");
			pst.setInt(3, uF.parseToInt(strEmpId));
			pst.setInt(4, nCurrServiceId);
			rs = pst.executeQuery();
			
			boolean isCurrIn = false;
			if(rs.next()){
				isCurrIn = true;
			}
			rs.close();
			pst.close();
			
			if(!isCurrIn && !isPrevOut){
				
//				pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				rs = pst.executeQuery();
				
				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_out(?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setInt(2, uF.parseToInt(strEmpId));
//				cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone()));
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				
				pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				if(rs.next()){
					isPrevRoster = true;
					
					tPrevFrom = rs.getTime("_from");
					tPrevTo = rs.getTime("_to");
					strPrevRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();
				
			}
			
			
			
			
			if(tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() < tPrevTo.getTime()){
				
//				pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//				rs = pst.executeQuery();

				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_in(?,?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setInt(2, uF.parseToInt(strEmpId));
//				cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//				cst.setTime(4, uF.getCurrentTime(CF.getStrTimeZone()));
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				
				pst = con.prepareStatement(selectRosterClockDetails_N_IN);
				pst.setInt(2, uF.parseToInt(strEmpId));
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setTime(4, uF.getCurrentTime(CF.getStrTimeZone()));
				rs = pst.executeQuery();

				strRosterDate = null;
				strFrom = null;
				strTo = null;

				while (rs.next()) {
					strPrevRosterStartTime = uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat());
					strPrevRosterEndTime = uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat());
					strRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();

//				cst.close();
			}else if(tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() > tPrevTo.getTime()){
//				pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				rs = pst.executeQuery();
				
				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_out(?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setInt(2, uF.parseToInt(strEmpId));
//				cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone()));
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				
				pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
				rs = pst.executeQuery();

				strRosterDate = null;
				strFrom = null;
				strTo = null;

				
				while (rs.next()) {
					strPrevRosterStartTime = uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat());
					strPrevRosterEndTime = uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat());
					strRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();
//				cst.close();
			}else{
				
//				pst = con.prepareStatement(selectRosterClockDetails_N1);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				pst.setInt(3, nCurrServiceId);
//				rs = pst.executeQuery();
				
				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_roster(?,?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setInt(2, uF.parseToInt(strEmpId));
//				cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//				cst.setInt(4, nCurrServiceId);
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				
				pst = con.prepareStatement(selectRosterClockDetails_N1);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, nCurrServiceId);
				rs = pst.executeQuery();				
				
				strRosterDate = null;
				strFrom = null;
				strTo = null;

				while (rs.next()) {
					strRosterStartTime = uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat());
					strRosterEndTime = uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat());
					strRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();
//				cst.close();
			}
			
			
			
			
			
			
//			pst = con.prepareStatement(selectAttendenceClockDetails1_N);
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));			
//			pst.setInt(2, uF.parseToInt(strEmpId));
//			pst.setInt(3, nCurrServiceId);
//			rs = pst.executeQuery();
//			
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_attendance_clock_details(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));			
//			cst.setInt(3, uF.parseToInt(strEmpId));
//			cst.setInt(4, nCurrServiceId);
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			
			pst = con.prepareStatement(selectAttendenceClockDetails1_N);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));			
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setInt(3, nCurrServiceId);
			rs = pst.executeQuery();			
			
			boolean isIn=false;
			boolean isOut=false;
			
			while (rs.next()) {

				if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
					isIn=true;
				}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
					isOut=true;
				}
				
			}
			rs.close();
			pst.close();
			
			
			log.debug("isIn="+isIn+" isOut"+isOut);
			log.debug("strPrevRosterStartTime="+strPrevRosterStartTime);
			log.debug("isPrevOut="+isPrevOut+" isPrevOut="+isPrevOut);
			log.debug("isRosterDependency="+CF.isRosterDependency(con,strEmpId));
			
			
			
			if(isIn && isOut){
				request.setAttribute("ROSTER_TIME", ((strRosterEndTime!=null)?"Your rostered end time was :"+strRosterEndTime:""));
			}else if(!isIn && !isOut && strRosterStartTime!=null){
				request.setAttribute("ROSTER_TIME", ((strRosterStartTime!=null)?"Your rostered start time is :"+strRosterStartTime:""));
			}else if(!isIn && !isOut && strPrevRosterStartTime!=null){
				
//				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
//				pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
//				pst.setString(2, "IN");
//				pst.setString(3, "OUT");
//				pst.setInt(4, uF.parseToInt(strEmpId));
//				pst.setInt(5, nPrevServiceId);
//				rs = pst.executeQuery();
//				
//				
				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_attendance_clock_details(?,?,?,?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				cst.setString(3, "IN");
//				cst.setString(4, "OUT");
//				cst.setInt(5, uF.parseToInt(strEmpId));
//				cst.setInt(6, nPrevServiceId);
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
				pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
				pst.setString(2, "IN");
				pst.setString(3, "OUT");
				pst.setInt(4, uF.parseToInt(strEmpId));
				pst.setInt(5, nPrevServiceId);
				rs = pst.executeQuery();
				
				boolean isPrevIn = false;
				isPrevOut = false;
				
				while(rs.next()){
					
					if(rs.getString("in_out").equalsIgnoreCase("IN")){
						isPrevIn = true;
					}else if(rs.getString("in_out").equalsIgnoreCase("OUT")){
						isPrevOut = true;
					} 
				}
				rs.close();
				pst.close();
				
				if(isPrevIn && isPrevOut){
					request.setAttribute("ROSTER_TIME", "Your are not rostered for today");
				}else if(isPrevIn && !isPrevOut){
					request.setAttribute("ROSTER_TIME", ((strPrevRosterEndTime!=null)?"Your rostered end time is :"+strPrevRosterEndTime:""));
				}else if(!isPrevIn && !isPrevOut && CF.isRosterDependency(con,strEmpId)){
//					request.setAttribute("ROSTER_TIME", ("Your are not rostered for today"));
					request.setAttribute("ROSTER_TIME", ((strPrevRosterStartTime!=null)?"Your rostered start time is :"+strPrevRosterStartTime:""));
//				}else if(!isPrevIn && !isPrevOut && !new CommonFunctions().isRosterDependency(con,strEmpId)){
				}else if(!isPrevIn && !isPrevOut && !CF.isRosterDependency(con,strEmpId)){
					request.setAttribute("ROSTER_TIME", ((strPrevRosterStartTime!=null)?"Your rostered start time is :"+strPrevRosterStartTime:""));					
				}
				
				
			}else if(isIn){
				request.setAttribute("ROSTER_TIME", ((strRosterEndTime!=null)?"Your rostered end time is :"+strRosterEndTime:""));
			}else if(isOut){
				request.setAttribute("ROSTER_TIME", ((strRosterEndTime!=null)?"Your rostered start time is :"+strRosterStartTime:""));
			}
			
			
			
			
			
			/**
			 *  IS Roster Dependent
			 */
			
//			pst = con.prepareStatement(selectRosterDependent);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			rs = pst.executeQuery();
//			
			
			
//			con.setAutoCommit(false);
//			//SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE eod.supervisor_emp_id = epd.emp_per_id AND emp_id = $1;
//			cst = con.prepareCall("{? = call sel_emp_details(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT * FROM employee_official_details eod, employee_personal_details epd where eod.emp_id = epd.emp_per_id AND emp_id =?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			String strEmpType = null;
			boolean isRoster = false;
			if(rs.next()){
				strEmpType = rs.getString("emptype");
				isRoster = uF.parseToBoolean(rs.getString("is_roster"));
			}
			rs.close();
			pst.close();
			
			
			if(strEmpType!=null && !isRoster){
				
//				pst = con.prepareStatement(selectAttendenceClockDetailsInOut);
//				pst.setDate(1, uF.getCurrentDate());
//				pst.setString(2, "IN");
//				pst.setString(3, "OUT");
//				pst.setInt(4, uF.parseToInt(strEmpId));
//				rs = pst.executeQuery();
//				
//				while(rs.next()){
//					
//					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
//						isIn = true;	
//					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
//						isOut = true;
//					}
//				}
//				
//				
//				
				request.setAttribute("ROSTER_TIME", "");
				
				
			}
			
			
			
			request.setAttribute("CURRENT_DATE", uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "EEEE "+CF.getStrReportDateFormat()));
			request.setAttribute("CURRENT_TIME", uF.getDateFormat(uF.getCurrentTime(CF.getStrTimeZone())+"", DBTIME, CF.getStrReportTimeFormat()));
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(cst);
			db.closeConnection(con);
		}
		
	}
	

	private String loadNavigationInner() {
		return LOAD;
	}

	
	private HttpServletRequest request;
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
