package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.tms.ClockOnOffEntry;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DashboardClockEntry1 extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String dashboardClockEntryText1;
	private String strClock;
	private String strMessage;
	private String strNotRoster;
	private String strNotRosterY;
	List <FillServices> serviceList;
	private String strMode;
	private String strApproval;
	private String dashboardClockLabel;
	private String strAction;
	
	private String strPrevMode;
	private boolean isRosterDependant;
	private boolean isRosterRequired;
	private boolean isSingleButtonClockOnOff;
	
	private boolean isTodayClockOff;
	
	String strEmpId = null;
	HttpSession session;
	CommonFunctions CF;

	public String execute() throws Exception {
		session = request.getSession();
		strEmpId = (String) session.getAttribute("EMPID");
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF == null) return LOGIN;
		
//		System.out.println("getStrClock( Entry ) Read =====>" + getStrClock()+" -- getStrMode() ===>> " + getStrMode());
		UtilityFunctions uF = new UtilityFunctions();
		
		String strRosterDependant = request.getParameter("isRosterDependant");
		String strRosterRequired = request.getParameter("isRosterRequired");
		String strSingleButtonClockOnOff = request.getParameter("isSingleButtonClockOnOff");
		
//		System.out.println("strRosterDependant ===>> " + strRosterDependant);
//		System.out.println("strRosterRequired ===>> " + strRosterRequired);
//		System.out.println("strSingleButtonClockOnOff ===>> " + strSingleButtonClockOnOff);
//		strRosterDependant = "true";
//		strRosterRequired = "true";
		
		if(strRosterDependant != null && !strRosterDependant.equalsIgnoreCase("null")) {
			setRosterDependant(uF.parseToBoolean(strRosterDependant));
		}
		if(strRosterRequired != null && !strRosterRequired.equalsIgnoreCase("null")) {
			setRosterRequired(uF.parseToBoolean(strRosterRequired));
		}
		if(strSingleButtonClockOnOff != null && !strSingleButtonClockOnOff.equalsIgnoreCase("null")) {
			setSingleButtonClockOnOff(uF.parseToBoolean(strSingleButtonClockOnOff));
		}
//		System.out.println("isRosterDependant ===>> " + getIsRosterDependant());
//		System.out.println("isRosterRequired ===>> " + getIsRosterRequired());
//		System.out.println("isSingleButtonClockOnOff ===>> " + getIsSingleButtonClockOnOff());
//		System.out.println("getStrAction() ===>> " + getStrAction());
		
		if(!getIsRosterDependant() && !getIsRosterRequired()) {
//			System.out.println("getStrAction() ===>> " + getStrAction());
			if (getStrAction() != null && getStrAction().length() > 0 && getStrAction().equals("COFF")) {
				setStrMode("OUT"); 
				clockOnOffRosterIndependant("OUT");
			} else if (getStrAction() != null && getStrAction().length() > 0 && getStrAction().equals("CON")) {
				setStrMode("IN");
				clockOnOffRosterIndependant("IN");
			}
			clockOnOffButton(uF);
		} else {
			int nShiftBaseType = CF.getEmpShiftBaseType(uF,strEmpId,request);
//			System.out.println("nShiftBaseType ===>> " + nShiftBaseType);
			if(nShiftBaseType == 2) {
				if (getStrAction() != null && getStrAction().length() > 0 && getStrAction().equals("COFF")) {
					setStrMode("OUT"); 
					clockOnOffShiftBase("OUT");
				} else if (getStrAction() != null && getStrAction().length() > 0 && getStrAction().equals("CON")) {
					setStrMode("IN");
					clockOnOffShiftBase("IN");
				}
				clockOnOffButtonShiftBase(uF);
			} else {
				if (getStrAction() != null && getStrAction().length() > 0 && getStrAction().equals("COFF")) {
					setStrMode("OUT"); 
					clockOnOff("OUT");
				} else if (getStrAction() != null && getStrAction().length() > 0 && getStrAction().equals("CON")) {
					setStrMode("IN");
					clockOnOff("IN");
				}
				clockOnOffButton(uF);
	//			clockOnOffMessage(uF);  
			}
		}
		
		return SUCCESS;
	}

	private void clockOnOffRosterIndependant(String string) {
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT is_roster,is_roster_required FROM employee_official_details eod, user_details ud where eod.emp_id = ud.emp_id and eod.emp_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			if(rs.next()) {
				isRosterDependant = uF.parseToBoolean(rs.getString("is_roster")); 
				isRosterRequired = uF.parseToBoolean(rs.getString("is_roster_required"));
			}
			rs.close();
			pst.close();
			
			ClockOnOffEntry cooe = new ClockOnOffEntry();
			cooe.setServletRequest(request);
			cooe.setStrMode(strMode);
			cooe.setIsRosterDependant(getIsRosterDependant());
			cooe.setIsRosterRequired(getIsRosterRequired());
			cooe.setIsSingleButtonClockOnOff(getIsSingleButtonClockOnOff());
			cooe.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private void clockOnOffShiftBase(String strMode) {
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {

//			System.out.println("================================clockOnOffShiftBase======================================="); 
			
			con = db.makeConnection(con);
			
			String strOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			String strWlocatoinId = CF.getEmpWlocationId(con, uF, strEmpId);
			double dblShiftBaseBufferTime = CF.getEmpShiftBaseBufferTime(con, uF,strEmpId);
//			System.out.println("dblShiftBaseBufferTime===>"+dblShiftBaseBufferTime);
			
//			System.out.println("DCE1/183---strMode==>"+strMode); 
			if (strMode != null && strMode.equalsIgnoreCase("IN")) {
				
				/**
				 * Check Prev Time
				 * */
				pst = con.prepareStatement("select * from roster_details rd where rd.emp_id=? and _date=? ");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				boolean isPrevRoster = false;
				int nPrevServiceId = 0;
				boolean isPrevOverNight = false;
				String strPrevFrom = null;
				String strPrevTo = null;
				String strPrevRosterDate = null;
				while(rs.next()){
					nPrevServiceId = rs.getInt("service_id");
					isPrevRoster = true;
					
					strPrevFrom = rs.getString("_from");
					strPrevTo = rs.getString("_to");
					strPrevRosterDate = rs.getString("_date");
					
					String shiftBaseType = uF.compareShiftTime(rs.getString("_from"), DBTIME,rs.getString("_to"), DBTIME);
					if(shiftBaseType != null && shiftBaseType.trim().equalsIgnoreCase("Over Night")){
						isPrevOverNight = true;
					}
				}
				rs.close();
				pst.close();
				
//				boolean isAllowedPrevIn = false;
				/*if(isPrevOverNight){
//					Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
//					Time prevRosterStartTime = uF.getTimeFormat(strPrevFrom, DBTIME);
//					Time prevRosterEndTime = uF.getTimeFormat(strPrevTo, DBTIME);
					Time currentTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);
					Time prevRosterStartTime = uF.getTimeFormat(uF.getPrevDate(CF.getStrTimeZone())+strPrevFrom, DBDATE+DBTIME);
					Time prevRosterEndTime = uF.getTimeFormat(uF.getPrevDate(CF.getStrTimeZone())+strPrevTo, DBDATE+DBTIME);
					
					long milliseconds1 = prevRosterStartTime.getTime();
					long milliseconds2 = prevRosterEndTime.getTime();
					long milliseconds3 = currentTime.getTime();
					
					double dbl = 0.0d;
					if(milliseconds1 > milliseconds3){
						dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3, milliseconds1));
					} else {
						dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
					}
//					System.out.println("isAllowedPrevIn dbl===>"+dbl);
//					if(-dblShiftBaseBufferTime <= dbl && dbl <= dblShiftBaseBufferTime){
					if(dbl <= dblShiftBaseBufferTime){
						isAllowedPrevIn = true; 
					}
				}*/
				
//				System.out.println("111 isPrevRoster===>"+isPrevRoster+"--isPrevOverNight===>"+isPrevOverNight+"--isAllowedPrevIn==>"+isAllowedPrevIn);
				
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where ad.emp_id=rd.emp_id " +
						"and to_date(in_out_timestamp::text,'yyyy-MM-dd') = _date and ad.emp_id=? and _date = ? " +
						"and (in_out = ? or in_out = ?) ");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
				pst.setString(3, "IN");
				pst.setString(4, "OUT");
//				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				boolean isPrevIn = false;
				boolean isPrevOut = false;
				String strPrevInTime = null;
				String strPrevOutTime = null;
				while(rs.next()){
					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
						isPrevIn = true;
						strPrevInTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
						isPrevOut = true;
//						strPrevOutTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
						strPrevOutTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT);
					} 
					nPrevServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
				
//				System.out.println("111 isPrevIn===>"+isPrevIn+"--isPrevOut===>"+isPrevOut+"--strPrevInTime==>"+strPrevInTime+"--strPrevOutTime==>"+strPrevOutTime);
				
				/**
				 * Check Prev Time End
				 * */
				
				pst = con.prepareStatement("SELECT * FROM roster_details WHERE emp_id=? and _date=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("pst ==>" + pst);  
				rs = pst.executeQuery();
				String strRosterDate = null;
				String strFrom = null;
				String strTo = null;
				int nServiceId = 0;
				while (rs.next()) {
					strFrom = rs.getString("_from");
					strTo = rs.getString("_to");
					strRosterDate = rs.getString("_date");
					nServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where ad.emp_id=rd.emp_id " +
						"and to_date(in_out_timestamp::text,'yyyy-MM-dd') = _date and ad.emp_id=? and _date = ? " +
						"and (in_out = ? or in_out = ?) ");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(3, "IN");
				pst.setString(4, "OUT");
//				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				boolean isIn = false;
				boolean isOut = false;
				int nCurrServiceId = 0;
				String strInTime = null;
				String strOutTime = null;
				while(rs.next()){
					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
						isIn = true;
						strInTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
						isOut = true;
						strOutTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					} 
					nCurrServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
				
				/*double dblT = 0.0d;
//				double dblTOut = 0.0d;
				if(strFrom!=null && strTo!=null) {
					Time currentTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);
					Time prevRosterStartTime = uF.getTimeFormat(uF.getPrevDate(CF.getStrTimeZone())+strPrevFrom, DBDATE+DBTIME);
					Time entryTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+strFrom, DBDATE+DBTIME);
					Time exitTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+strTo, DBDATE+DBTIME);
					
					long milliseconds1 = entryTime.getTime();
					long milliseconds11 = prevRosterStartTime.getTime();
					long milliseconds2 = exitTime.getTime();
					long milliseconds3 = currentTime.getTime();

					if(milliseconds11>milliseconds3){
						dblT = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds3, milliseconds11));
					}else{
						dblT = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds11, milliseconds3));
					}
//					if(milliseconds2 > milliseconds3){
//						dblTOut = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds3, milliseconds2));
//					}
				}
				if(dblT > dblShiftBaseBufferTime){
					isAllowedPrevIn = true;
				}*/
				
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					
				String currDate = uF.getCurrentDate(CF.getStrTimeZone())+"";
				String currTime = uF.getCurrentTime(CF.getStrTimeZone())+"";
				String dateInString = currDate + " " + currTime;
				if(strRosterDate!=null) {
					dateInString = strRosterDate + " " + strFrom;
				}
//				System.out.println("dateInString ===>> " + dateInString);
				Date dtDate = sdf.parse(dateInString);
				Calendar calendar = Calendar.getInstance();
//				System.out.println("calendar.getTime() ===>> " + calendar.getTime());
				calendar.setTime(dtDate);
//				System.out.println("Assign calendar.getTime() ===>> " + calendar.getTime());
				calendar.add(Calendar.HOUR, -(int)dblShiftBaseBufferTime);
//				System.out.println("remove shiftBufferTime ===>> " + calendar.getTime());
				String strBufferRemoveTime = calendar.get(Calendar.YEAR) + "-"+ uF.zero((calendar.get(Calendar.MONTH) + 1)) + "-"+ uF.zero(calendar.get(Calendar.DATE)) + " " +uF.zero(calendar.get(Calendar.HOUR_OF_DAY))+ ":"+ uF.zero(calendar.get(Calendar.MINUTE))+ ":"+ uF.zero(calendar.get(Calendar.SECOND));
//				System.out.println("strBufferRemoveTime ===>> " +strBufferRemoveTime);
				
//				System.out.println("currTime ===>> " + currTime);
				Date dtCurrDate = sdf.parse(currDate+" "+currTime);
				calendar = Calendar.getInstance();
				calendar.setTime(dtCurrDate);
				String strCurrentTime = calendar.get(Calendar.YEAR) + "-"+ uF.zero((calendar.get(Calendar.MONTH) + 1)) + "-"+ uF.zero(calendar.get(Calendar.DATE)) + " " +uF.zero(calendar.get(Calendar.HOUR_OF_DAY))+ ":"+ uF.zero(calendar.get(Calendar.MINUTE))+ ":"+ uF.zero(calendar.get(Calendar.SECOND));
//				System.out.println("strCurrentTime ===>> " + strCurrentTime);
				
				
				
//				System.out.println("111 isIn===>"+isIn+"--isOut===>"+isOut+"--strInTime==>"+strInTime+"--strOutTime==>"+strOutTime);
				
				if(!isIn && !isOut && isPrevOverNight && !isPrevIn && !isPrevOut && uF.getDateFormatUtil(strCurrentTime+"", DBTIMESTAMP).before(uF.getDateFormatUtil(strBufferRemoveTime+"", DBTIMESTAMP)) && nPrevServiceId > 0){
//				if(!isIn && !isOut && isPrevOverNight && !isPrevIn && !isPrevOut && isAllowedPrevIn && nPrevServiceId > 0){
//					System.out.println("Prev In");
					if(strPrevFrom != null && !strPrevFrom.trim().equals("") && !strPrevFrom.trim().equalsIgnoreCase("NULL") 
							&& strPrevTo != null && !strPrevTo.trim().equals("") && !strPrevTo.trim().equalsIgnoreCase("NULL")) {
//						System.out.println("Prev In if");	
						Time entryPrevTime = uF.getTimeFormat(strPrevRosterDate+strPrevFrom, DBDATE+DBTIME);
						Time exitPrevTime = uF.getTimeFormat(strPrevRosterDate+strPrevTo, DBDATE+DBTIME);
						
						Time currentTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);

						long millisecondsPrev1 = entryPrevTime.getTime();
						long millisecondsPrev2 = exitPrevTime.getTime();
						long millisecondsPrev3 = currentTime.getTime();
						long diffPrev = 0L;
						
//						if(entryPrevTime.after(exitPrevTime)) {
//							millisecondsPrev2 += 60 * 60 * 24 * 1000; 	
//						}
						
						diffPrev = millisecondsPrev3 - millisecondsPrev1;
						
						long diffPrevSeconds = diffPrev / 1000;
						long diffPrevMinutes = diffPrev / (60 * 1000);
						
						pst = con.prepareStatement("SELECT * FROM roster_policy WHERE mode = ? and time_value " + ((diffPrevMinutes >= 0) ? ">" : "<") + "= ? " +
								"and policy_status=1 and effective_date<=? and org_id=? and wlocation_id=? order by time_value "+((diffPrevMinutes >= 0)?"":"desc")+" LIMIT 1");
						pst.setString(1, strMode);
						pst.setLong(2, diffPrevMinutes);
						pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strOrgId));
						pst.setInt(5, uF.parseToInt(strWlocatoinId));
//						System.out.println("IN pst==>" + pst);  
						rs = pst.executeQuery();  
						strMessage = null;
						if (diffPrevMinutes!=0 && CF.isRosterDependency(con,strEmpId) && rs.next() && rs.getString("message")!=null 
								&& !rs.getString("message").trim().equals("") && !rs.getString("message").trim().equalsIgnoreCase("NULL") 
								&& rs.getString("message").length()>0) {
							strMessage = rs.getString("message");
							strPrevMode = "true";
							if(rs.getBoolean("isapproval")) {
								setStrApproval("1");
							}
							rs.close();
							pst.close();
							
						} else {
							rs.close();
							pst.close();
							
							dashboardClockEntryText1="";
//							System.out.println("Out one Prev====");
							
							ClockOnOffEntry cooe = new ClockOnOffEntry();
							cooe.setServletRequest(request);
							cooe.setStrMode(strMode);	
							cooe.setStrPrevMode("true");
							cooe.setIsRosterDependant(getIsRosterDependant());
							cooe.setIsRosterRequired(getIsRosterRequired());
							cooe.setIsSingleButtonClockOnOff(getIsSingleButtonClockOnOff());
							cooe.execute();
						}
						
					}
				
				} else if(strFrom != null && !strFrom.trim().equals("") && !strFrom.trim().equalsIgnoreCase("NULL") 
						&& strTo != null && !strTo.trim().equals("") && !strTo.trim().equalsIgnoreCase("NULL")) {
					Time entryTime = uF.getTimeFormat(strRosterDate+strFrom, DBDATE+DBTIME);
					Time exitTime = uF.getTimeFormat(strRosterDate+strTo, DBDATE+DBTIME);
					
					Time currentTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);

					long milliseconds1 = entryTime.getTime();
					long milliseconds2 = exitTime.getTime();
					long milliseconds3 = currentTime.getTime();
					long diff = 0L;
					
					int nIN = 1;
					diff = milliseconds3 - milliseconds1;
					
					long diffSeconds = diff / 1000;
					long diffMinutes = diff / (60 * 1000);
					
					pst = con.prepareStatement("SELECT * FROM roster_policy WHERE mode = ? and time_value " + ((diffMinutes >= 0) ? ">" : "<") + "= ? " +
							"and policy_status=1 and effective_date<=? and org_id=? and wlocation_id=? order by time_value "+((diffMinutes >= 0)?"":"desc")+" LIMIT 1");
					pst.setString(1, strMode);
					pst.setLong(2, diffMinutes);
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strOrgId));
					pst.setInt(5, uF.parseToInt(strWlocatoinId));
//					System.out.println("pst==>" + pst);  
					rs = pst.executeQuery();  
					strMessage = null;
					if (diffMinutes!=0 && CF.isRosterDependency(con,strEmpId) && rs.next() && rs.getString("message")!=null 
							&& !rs.getString("message").trim().equals("") && !rs.getString("message").trim().equalsIgnoreCase("NULL") 
							&& rs.getString("message").length()>0) {
						strMessage = rs.getString("message");
						
						if(rs.getBoolean("isapproval")) {
							setStrApproval("1");
						}
						rs.close();
						pst.close();
						
					} else {
						rs.close();
						pst.close();
						
						dashboardClockEntryText1="";
//						System.out.println("one ====");
						
						ClockOnOffEntry cooe = new ClockOnOffEntry();
						cooe.setServletRequest(request);
						cooe.setStrMode(strMode);
						cooe.setIsRosterDependant(getIsRosterDependant());
						cooe.setIsRosterRequired(getIsRosterRequired());
						cooe.setIsSingleButtonClockOnOff(getIsSingleButtonClockOnOff());
						cooe.execute();
					}
				} else if(!new CommonFunctions(CF).isCurrentRostered(con, strEmpId) && CF.isRosterDependency(con, strEmpId) && !getIsSingleButtonClockOnOff()) {
					strNotRoster = "Please choose cost center to clock on.";
					serviceList = new FillServices(request).fillServices(strEmpId);
				} else if(!new CommonFunctions(CF).isCurrentRostered(con, strEmpId) && !CF.isRosterDependency(con, strEmpId) && !getIsSingleButtonClockOnOff()) {
					strNotRosterY = "Please choose cost center to clock on.";
					serviceList = new FillServices(request).fillServices(strEmpId);
				} else {
					dashboardClockEntryText1="";
//					System.out.println("two ====>> " );
					
					ClockOnOffEntry cooe = new ClockOnOffEntry();
					cooe.setServletRequest(request);
					cooe.setStrMode(strMode);
					cooe.setIsRosterDependant(getIsRosterDependant());
					cooe.setIsRosterRequired(getIsRosterRequired());
					cooe.setIsSingleButtonClockOnOff(getIsSingleButtonClockOnOff());
					cooe.execute();
				}
			} if (strMode != null && strMode.equalsIgnoreCase("OUT")) {
				
				boolean noClockOffFlag = true;
				/**
				 * Check Prev Time
				 * */
				pst = con.prepareStatement("select * from roster_details rd where rd.emp_id=? and _date=? ");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				boolean isPrevRoster = false;
				int nPrevServiceId = 0;
				boolean isPrevOverNight = false;
				String strPrevFrom = null;
				String strPrevTo = null;
				String strPrevRosterDate = null;
				while(rs.next()){
					nPrevServiceId = rs.getInt("service_id");
					isPrevRoster = true;
					
					strPrevFrom = rs.getString("_from");
					strPrevTo = rs.getString("_to");
					strPrevRosterDate = rs.getString("_date");
					
					String shiftBaseType = uF.compareShiftTime(rs.getString("_from"), DBTIME,rs.getString("_to"), DBTIME);
					if(shiftBaseType != null && shiftBaseType.trim().equalsIgnoreCase("Over Night")){
						isPrevOverNight = true;
					}
				}
				rs.close();
				pst.close();
				
				/*boolean isAllowedPrevOut = false;
				if(isPrevOverNight){
					Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
					Time prevRosterStartTime = uF.getTimeFormat(strPrevFrom, DBTIME);
					Time prevRosterEndTime = uF.getTimeFormat(strPrevTo, DBTIME);
					
					long milliseconds1 = prevRosterStartTime.getTime();
					long milliseconds2 = prevRosterEndTime.getTime();
					long milliseconds3 = currentTime.getTime();
					
					double dbl = 0.0d;
					if(milliseconds2 > milliseconds3){
						dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3, milliseconds2));
					} else {
						dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds2, milliseconds3));
					}
//					System.out.println("isAllowedPrevOut dbl===>"+dbl);
//					if(dbl <= 2.0d){
					if(dbl <= dblShiftBaseBufferTime) {
						isAllowedPrevOut = true;
					}
				}*/
				
//				System.out.println("OUT isPrevRoster===>"+isPrevRoster+"--isPrevOverNight===>"+isPrevOverNight+"--isAllowedPrevOut==>"+isAllowedPrevOut);
				
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where ad.emp_id=rd.emp_id " +
						"and to_date(in_out_timestamp::text,'yyyy-MM-dd') = _date and ad.emp_id=? and _date = ? " +
						"and (in_out = ? or in_out = ?) ");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
				pst.setString(3, "IN");
				pst.setString(4, "OUT");
//				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				boolean isPrevIn = false;
				boolean isPrevOut = false;
				String strPrevInTime = null;
				String strPrevOutTime = null;
				while(rs.next()){
					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
						isPrevIn = true;
						strPrevInTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
						isPrevOut = true;
						strPrevOutTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					} 
					nPrevServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
				
				/**
				 * Check Prev Time End
				 * */
				
				
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where ad.emp_id=rd.emp_id " +
						"and to_date(in_out_timestamp::text,'yyyy-MM-dd') = _date and ad.emp_id=? and _date = ? " +
						"and (in_out = ? or in_out = ?) ");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(3, "IN");
				pst.setString(4, "OUT");
//				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				boolean isIn = false;
				boolean isOut = false;
				int nCurrServiceId = 0;
				String strInTime = null;
				String strOutTime = null;
				while(rs.next()){
					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
						isIn = true;
						strInTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
						isOut = true;
						strOutTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					} 
					nCurrServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("SELECT * FROM roster_details WHERE emp_id=? and _date=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("pst ==>" + pst);  
				rs = pst.executeQuery();
				String strRosterDate = null;
				String strFrom = null;
				String strTo = null;
				int nServiceId = 0;
				while (rs.next()) {
					strFrom = rs.getString("_from");
					strTo = rs.getString("_to");
					strRosterDate = rs.getString("_date");
					nServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
				
				
				/*double dblT = 0.0d;
				double dblTOut = 0.0d;
				if(strFrom!=null && strTo!=null) {
					Time currentTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);
					Time entryTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+strFrom, DBDATE+DBTIME);
					Time exitTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+strTo, DBDATE+DBTIME);
					
					long milliseconds1 = entryTime.getTime();
					long milliseconds2 = exitTime.getTime();
					long milliseconds3 = currentTime.getTime();

					if(milliseconds1>milliseconds3){
						dblT = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds3, milliseconds1));
//					}else{
//						dblT = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
					}
					if(milliseconds2 > milliseconds3){
						dblTOut = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds3, milliseconds2));
					}
				}
				if(dblT > dblShiftBaseBufferTime){
					isAllowedPrevOut = true;
				}*/
				
//				System.out.println("isAllowedPrevOut ===>> " + isAllowedPrevOut);
				
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				String currDate = uF.getCurrentDate(CF.getStrTimeZone())+"";
				String currTime = uF.getCurrentTime(CF.getStrTimeZone())+"";
				String dateInString = currDate + " " + currTime;
				if(strRosterDate!=null) {
					dateInString = strRosterDate + " " + strFrom;
				}
				
				Calendar calendar = Calendar.getInstance();
				String strBufferRemoveTime = null;
				if(strPrevRosterDate!=null) {
					if(isPrevOverNight) {
						dateInString = uF.getDateFormat(uF.getFutureDate(uF.getDateFormatUtil(strPrevRosterDate, DBDATE), 1)+"", DBDATE, DBDATE) + " " + strPrevTo;
					} else {
						dateInString = strPrevRosterDate + " " + strPrevTo;
					}
					
//					System.out.println("dateInString ===>> " + dateInString + " -- strPrevTo ===>> " + strPrevTo);
					Date dtDate = sdf.parse(dateInString);
//					System.out.println("calendar.getTime() ===>> " + calendar.getTime());
					calendar.setTime(dtDate);
//					System.out.println("Assign calendar.getTime() ===>> " + calendar.getTime());
					calendar.add(Calendar.HOUR, +(int)dblShiftBaseBufferTime);
//					System.out.println("Add shiftBufferTime ===>> " + calendar.getTime());
					strBufferRemoveTime = calendar.get(Calendar.YEAR) + "-"+ uF.zero((calendar.get(Calendar.MONTH) + 1)) + "-"+ uF.zero(calendar.get(Calendar.DATE)) + " " +uF.zero(calendar.get(Calendar.HOUR_OF_DAY))+ ":"+ uF.zero(calendar.get(Calendar.MINUTE))+ ":"+ uF.zero(calendar.get(Calendar.SECOND));
//					System.out.println("+ strBufferRemoveTime ===>> " +strBufferRemoveTime);
				} else {
	//				String[] tempStrFrom = strFrom.split(":");
//					System.out.println("dateInString ===>> " + dateInString + " -- strFrom ===>> " + strFrom);
					Date dtDate = sdf.parse(dateInString);
//					System.out.println("calendar.getTime() ===>> " + calendar.getTime());
					calendar.setTime(dtDate);
//					System.out.println("Assign calendar.getTime() ===>> " + calendar.getTime());
					calendar.add(Calendar.HOUR, -(int)dblShiftBaseBufferTime);
//					System.out.println("remove shiftBufferTime ===>> " + calendar.getTime());
					strBufferRemoveTime = calendar.get(Calendar.YEAR) + "-"+ uF.zero((calendar.get(Calendar.MONTH) + 1)) + "-"+ uF.zero(calendar.get(Calendar.DATE)) + " " +uF.zero(calendar.get(Calendar.HOUR_OF_DAY))+ ":"+ uF.zero(calendar.get(Calendar.MINUTE))+ ":"+ uF.zero(calendar.get(Calendar.SECOND));
//					System.out.println("strBufferRemoveTime ===>> " +strBufferRemoveTime);
				}
				
//				System.out.println("dateInString ===>> " + dateInString + " -- strFrom ===>> " + strFrom);
//				Date dtDate = sdf.parse(dateInString);
//				Calendar calendar = Calendar.getInstance();
//				System.out.println("calendar.getTime() ===>> " + calendar.getTime());
//				calendar.setTime(dtDate);
//				System.out.println("Assign calendar.getTime() ===>> " + calendar.getTime());
//				calendar.add(Calendar.HOUR, -(int)dblShiftBaseBufferTime);
//				System.out.println("remove shiftBufferTime ===>> " + calendar.getTime());
//				String strBufferRemoveTime = calendar.get(Calendar.YEAR) + "-"+ uF.zero((calendar.get(Calendar.MONTH) + 1)) + "-"+ uF.zero(calendar.get(Calendar.DATE)) + " " +uF.zero(calendar.get(Calendar.HOUR_OF_DAY))+ ":"+ uF.zero(calendar.get(Calendar.MINUTE))+ ":"+ uF.zero(calendar.get(Calendar.SECOND));
////				System.out.println("strBufferRemoveTime ===>> " +strBufferRemoveTime);
				
//				System.out.println("currTime ===>> " + currTime);
				Date dtCurrDate = sdf.parse(currDate+" "+currTime);
				calendar = Calendar.getInstance();
				calendar.setTime(dtCurrDate);
				String strCurrentTime = calendar.get(Calendar.YEAR) + "-"+ uF.zero((calendar.get(Calendar.MONTH) + 1)) + "-"+ uF.zero(calendar.get(Calendar.DATE)) + " " +uF.zero(calendar.get(Calendar.HOUR_OF_DAY))+ ":"+ uF.zero(calendar.get(Calendar.MINUTE))+ ":"+ uF.zero(calendar.get(Calendar.SECOND));
//				System.out.println("strCurrentTime ===>> " + strCurrentTime);
				
				if(!isIn && !isOut && isPrevOverNight && isPrevIn && !isPrevOut && uF.getDateFormatUtil(strCurrentTime+"", DBTIMESTAMP).before(uF.getDateFormatUtil(strBufferRemoveTime+"", DBTIMESTAMP)) && nPrevServiceId > 0){
//				if(!isIn && !isOut && isPrevOverNight && isPrevIn && !isPrevOut && isAllowedPrevOut && nPrevServiceId > 0){
					if(strPrevFrom != null && !strPrevFrom.trim().equals("") && !strPrevFrom.trim().equalsIgnoreCase("NULL") 
							&& strPrevTo != null && !strPrevTo.trim().equals("") && !strPrevTo.trim().equalsIgnoreCase("NULL")) {
						
						Time entryPrevTime = uF.getTimeFormat(strPrevRosterDate+strPrevFrom, DBDATE+DBTIME);
						Time exitPrevTime = uF.getTimeFormat(strPrevRosterDate+strPrevTo, DBDATE+DBTIME);
						
						Time currentTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);

						long millisecondsPrev1 = entryPrevTime.getTime();
						long millisecondsPrev2 = exitPrevTime.getTime();
						long millisecondsPrev3 = currentTime.getTime();
						long diffPrev = 0L;
						
						if(entryPrevTime.after(exitPrevTime)) {
							millisecondsPrev2 += 60 * 60 * 24 * 1000; 	
						}
						
						diffPrev = millisecondsPrev3 - millisecondsPrev2;
						
						long diffPrevSeconds = diffPrev / 1000;
						long diffPrevMinutes = diffPrev / (60 * 1000);
						
						pst = con.prepareStatement("SELECT * FROM roster_policy WHERE mode = ? and time_value " + ((diffPrevMinutes >= 0) ? ">" : "<") + "= ? " +
								"and policy_status=1 and effective_date<=? and org_id=? and wlocation_id=? order by time_value "+((diffPrevMinutes >= 0)?"":"desc")+" LIMIT 1");
						pst.setString(1, strMode);
						pst.setLong(2, diffPrevMinutes);
						pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strOrgId));
						pst.setInt(5, uF.parseToInt(strWlocatoinId));
//						System.out.println("pst==>" + pst);  
						rs = pst.executeQuery();  
						strMessage = null;
						if (diffPrevMinutes!=0 && CF.isRosterDependency(con,strEmpId) && rs.next() && rs.getString("message")!=null 
								&& !rs.getString("message").trim().equals("") && !rs.getString("message").trim().equalsIgnoreCase("NULL") 
								&& rs.getString("message").length()>0) {
							strMessage = rs.getString("message");
							strPrevMode = "true";
							if(rs.getBoolean("isapproval")) {
								setStrApproval("1");
							}
							rs.close();
							pst.close();
							
						} else {
							rs.close();
							pst.close();
							
							dashboardClockEntryText1="";
//							System.out.println("Out one Prev====");
							
							ClockOnOffEntry cooe = new ClockOnOffEntry();
							cooe.setServletRequest(request);
							cooe.setStrMode(strMode);	
							cooe.setStrPrevMode("true");
							cooe.setIsRosterDependant(getIsRosterDependant());
							cooe.setIsRosterRequired(getIsRosterRequired());
							cooe.setIsSingleButtonClockOnOff(getIsSingleButtonClockOnOff());
							cooe.execute();
							
						}
					}
				
				} else if(strFrom != null && !strFrom.trim().equals("") && !strFrom.trim().equalsIgnoreCase("NULL") 
					&& strTo != null && !strTo.trim().equals("") && !strTo.trim().equalsIgnoreCase("NULL") && 
					strInTime != null && !strInTime.trim().equals("") && !strInTime.trim().equalsIgnoreCase("NULL")) {
					Time entryTime = uF.getTimeFormat(strRosterDate+strFrom, DBDATE+DBTIME);
					Time exitTime = uF.getTimeFormat(strRosterDate+strTo, DBDATE+DBTIME);
					
					Time currentTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);

					long milliseconds1 = entryTime.getTime();
					long milliseconds2 = exitTime.getTime();
					long milliseconds3 = currentTime.getTime();
					long diff = 0L;
					
					if(entryTime.after(exitTime)) {
						milliseconds2 += 60 * 60 * 24 * 1000; 	
					}
					
					diff = milliseconds3 - milliseconds2;
					
					long diffSeconds = diff / 1000;
					long diffMinutes = diff / (60 * 1000);
					
					pst = con.prepareStatement("SELECT * FROM roster_policy WHERE mode = ? and time_value " + ((diffMinutes >= 0) ? ">" : "<") + "= ? " +
							"and policy_status=1 and effective_date<=? and org_id=? and wlocation_id=? order by time_value "+((diffMinutes >= 0)?"":"desc")+" LIMIT 1");
					pst.setString(1, strMode);
					pst.setLong(2, diffMinutes);
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strOrgId));
					pst.setInt(5, uF.parseToInt(strWlocatoinId));
//					System.out.println("pst==>" + pst);  
					rs = pst.executeQuery();  
					strMessage = null;
					if (diffMinutes!=0 && CF.isRosterDependency(con,strEmpId) && rs.next() && rs.getString("message")!=null 
							&& !rs.getString("message").trim().equals("") && !rs.getString("message").trim().equalsIgnoreCase("NULL") 
							&& rs.getString("message").length()>0) {
						strMessage = rs.getString("message");
						
						if(rs.getBoolean("isapproval")) {
							setStrApproval("1");
						}
						rs.close();
						pst.close();
						
					} else {
						rs.close();
						pst.close();
						
						dashboardClockEntryText1="";
//						System.out.println("Out one ====");
						
						ClockOnOffEntry cooe = new ClockOnOffEntry();
						cooe.setServletRequest(request);
						cooe.setStrMode(strMode);
						cooe.setIsRosterDependant(getIsRosterDependant());
						cooe.setIsRosterRequired(getIsRosterRequired());
						cooe.setIsSingleButtonClockOnOff(getIsSingleButtonClockOnOff());
						cooe.execute();
						
						noClockOffFlag = true;
						
					}
					
				}/* else if(!new CommonFunctions(CF).isCurrentRostered(con, strEmpId) && CF.isRosterDependency(con, strEmpId)) {
					strNotRoster = "Please choose cost center to clock on.";
					serviceList = new FillServices(request).fillServices(strEmpId);
				} else if(!new CommonFunctions(CF).isCurrentRostered(con, strEmpId) && !CF.isRosterDependency(con, strEmpId)) {
					strNotRosterY = "Please choose cost center to clock on.";
					serviceList = new FillServices(request).fillServices(strEmpId);
				}*/ /*else {
					dashboardClockEntryText1="";
					System.out.println("Out two ====>> " );
					
					ClockOnOffEntry cooe = new ClockOnOffEntry();
					cooe.setServletRequest(request);
					cooe.setStrMode(strMode);
					cooe.execute();
				}*/
//				System.out.println("DCE1/877----noClockOffFlag=="+noClockOffFlag);
				setTodayClockOff(noClockOffFlag);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void clockOnOffButtonShiftBase(UtilityFunctions uF) {
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		
		try {
//			System.out.println("================================== clockOnOffButtonShiftBase =========================================");
			con = db.makeConnection(con);
			
			Map<String, String> hmServiceMap = CF.getServicesMap(con,true);
			boolean isCurrentRoster = new CommonFunctions(CF).isCurrentRostered(con,strEmpId);
//			System.out.println("isCurrentRoster==>"+isCurrentRoster);
			int nEmpServiceId = CF.getEmpServiceId(con,uF,strEmpId);
//			System.out.println("nEmpServiceId==>"+nEmpServiceId);
			
			double dblShiftBaseBufferTime = CF.getEmpShiftBaseBufferTime(con, uF,strEmpId);
//			System.out.println("dblShiftBaseBufferTime===>"+dblShiftBaseBufferTime);
			
			pst = con.prepareStatement("SELECT is_roster,is_roster_required FROM employee_official_details eod, user_details ud where eod.emp_id = ud.emp_id and eod.emp_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			if(rs.next()) {
				isRosterDependant = uF.parseToBoolean(rs.getString("is_roster"));
				isRosterRequired = uF.parseToBoolean(rs.getString("is_roster_required"));
			}
			rs.close();
			pst.close();
//			request.setAttribute("isRosterDependant", isRosterDependant);
			
//			System.out.println("isRosterDependant ==> " + isRosterDependant);
			if(isRosterDependant) {
				/**
				 * Check Prev Time
				 * */
				pst = con.prepareStatement("select * from roster_details rd where rd.emp_id=? and _date=? ");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				boolean isPrevRoster = false;
				int nPrevServiceId = 0;
				boolean isPrevOverNight = false;
				String strPrevFrom = null;
				String strPrevTo = null;
				String strPrevRosterDate = null;
				while(rs.next()){
					nPrevServiceId = rs.getInt("service_id");
					isPrevRoster = true;
					
					strPrevFrom = rs.getString("_from");
					strPrevTo = rs.getString("_to");
					strPrevRosterDate = rs.getString("_date");
					
					String shiftBaseType = uF.compareShiftTime(rs.getString("_from"), DBTIME,rs.getString("_to"), DBTIME);
					if(shiftBaseType != null && shiftBaseType.trim().equalsIgnoreCase("Over Night")) {
						isPrevOverNight = true;
					}
				}
				rs.close();
				pst.close();
//				System.out.println("isPrevOverNight ===>>>>>>>>> " + isPrevOverNight);
				
//				boolean isAllowedPrevOut = false;
//				if(isPrevOverNight) {
//					Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
//					Time prevRosterStartTime = uF.getTimeFormat(strPrevFrom, DBTIME);
//					Time prevRosterEndTime = uF.getTimeFormat(strPrevTo, DBTIME);
////					Time currentTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);
////					Time prevRosterStartTime = uF.getTimeFormat(uF.getPrevDate(CF.getStrTimeZone())+strPrevFrom, DBDATE+DBTIME);
////					Time prevRosterEndTime = uF.getTimeFormat(uF.getPrevDate(CF.getStrTimeZone())+strPrevTo, DBDATE+DBTIME);
//					
//					long milliseconds1 = prevRosterStartTime.getTime();
//					long milliseconds2 = prevRosterEndTime.getTime();
//					long milliseconds3 = currentTime.getTime();
//					
//					double dbl = 0.0d;
//					if(milliseconds2 > milliseconds3){
//						dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3, milliseconds2));
//					} else {
//						dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds2, milliseconds3));
//					}
//					System.out.println("isAllowedPrevOut dbl===>"+dbl);
////					if(dbl <= 2.0d){
//					if(dbl <= dblShiftBaseBufferTime) {
//						isAllowedPrevOut = true;
//					}
//				}
				
//				boolean isAllowedPrevIn = false;
//				if(isPrevOverNight) {
////					Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
////					Time prevRosterStartTime = uF.getTimeFormat(strPrevFrom, DBTIME);
////					Time prevRosterEndTime = uF.getTimeFormat(strPrevTo, DBTIME);
//					Time currentTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);
//					Time prevRosterStartTime = uF.getTimeFormat(uF.getPrevDate(CF.getStrTimeZone())+strPrevFrom, DBDATE+DBTIME);
//					Time prevRosterEndTime = uF.getTimeFormat(uF.getPrevDate(CF.getStrTimeZone())+strPrevTo, DBDATE+DBTIME);
//					
//					long milliseconds1 = prevRosterStartTime.getTime();
//					long milliseconds2 = prevRosterEndTime.getTime();
//					long milliseconds3 = currentTime.getTime();
//					
//					double dbl = 0.0d;
//					if(milliseconds1 > milliseconds3){
//						dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3, milliseconds1));
//					} else {
//						dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
//					}
////					System.out.println("isAllowedPrevIn dbl===>"+dbl);
////					if(dbl <= 2.0d){
//					if(dbl <= dblShiftBaseBufferTime){
//						isAllowedPrevIn = true;
//					}
//				}
				
//				System.out.println("isPrevRoster===>"+isPrevRoster+"--isPrevOverNight===>"+isPrevOverNight+"--isAllowedPrevOut==>"+isAllowedPrevOut+"--isAllowedPrevIn==>"+isAllowedPrevIn);
				
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where ad.emp_id=rd.emp_id " +
					"and to_date(in_out_timestamp::text,'yyyy-MM-dd') = _date and ad.emp_id=? and _date = ? and (in_out = ? or in_out = ?) ");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
				pst.setString(3, "IN");
				pst.setString(4, "OUT");
//				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				boolean isPrevIn = false;
				boolean isPrevOut = false;
				String strPrevInTime = null;
				String strPrevOutTime = null;
				while(rs.next()){
					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
						isPrevIn = true;
						strPrevInTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
						isPrevOut = true;
						strPrevOutTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					} 
					nPrevServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
//				System.out.println("isPrevIn===>"+isPrevIn+"--isPrevOut===>"+isPrevOut+"--strPrevInTime===>"+strPrevInTime+"--strPrevOutTime===>"+strPrevOutTime);
				
				/**
				 * Check Prev Time End
				 * */
				
//				int nCurrServiceId = 0;
//				boolean isPrevOverNight = false;
//				String strPrevFrom = null;
//				String strPrevTo = null;
//				String strPrevRosterDate = null;
//				if(!isPrevOut) {
//					pst = con.prepareStatement("select * from roster_details rd where rd.emp_id=? and _date=? ");
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
////					System.out.println("pst===>"+pst);
//					rs = pst.executeQuery();
//					while(rs.next()){
//						nCurrServiceId = rs.getInt("service_id");
//						strFrom = rs.getString("_from");
//						strTo = rs.getString("_to");
//						strRosterDate = rs.getString("_date");
//						
//						String shiftBaseType = uF.compareShiftTime(rs.getString("_from"), DBTIME,rs.getString("_to"), DBTIME);
//						if(shiftBaseType != null && shiftBaseType.trim().equalsIgnoreCase("Over Night")){
//							isOverNight = true;
//						}
//					}
//					rs.close();
//					pst.close();
//				}
					
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where ad.emp_id=rd.emp_id and " +
					"to_date(in_out_timestamp::text,'yyyy-MM-dd') = _date and ad.emp_id=? and _date = ? and (in_out = ? or in_out = ?)");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(3, "IN");
				pst.setString(4, "OUT");
//				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				boolean isIn = false;
				boolean isOut = false;
				int nCurrServiceId = 0;
				String strInTime = null;
				String strOutTime = null;
				while(rs.next()){
					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
						isIn = true;
						strInTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
						isOut = true;
						strOutTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					} 
					nCurrServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from roster_details rd where rd.emp_id=? and _date=? ");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				boolean isOverNight = false;
				String strFrom = null;
				String strTo = null;
				String strRosterDate = null;
				while(rs.next()){
					nCurrServiceId = rs.getInt("service_id");
					strFrom = rs.getString("_from");
					strTo = rs.getString("_to");
					strRosterDate = rs.getString("_date");
					
					String shiftBaseType = uF.compareShiftTime(rs.getString("_from"), DBTIME,rs.getString("_to"), DBTIME);
					if(shiftBaseType != null && shiftBaseType.trim().equalsIgnoreCase("Over Night")){
						isOverNight = true;
					}
				}
				rs.close();
				pst.close();
				
//				System.out.println("DCE1/1114---isOverNight=="+isOverNight);
				/*double dblT = 0.0d;
				double dblTOut = 0.0d;
				if(strFrom!=null && strTo!=null) {
					Time currentTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);
					Time entryTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+strFrom, DBDATE+DBTIME);
					Time exitTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+strTo, DBDATE+DBTIME);
					
					long milliseconds1 = entryTime.getTime();
					long milliseconds2 = exitTime.getTime();
					long milliseconds3 = currentTime.getTime();

					if(milliseconds1>milliseconds3){
						dblT = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds3, milliseconds1));
//					}else{
//						dblT = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
					}
					if(milliseconds2 > milliseconds3){
						dblTOut = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds3, milliseconds2));
					}
				}
				if(dblT > dblShiftBaseBufferTime){
					isAllowedPrevOut = true;
				}*/
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				String currDate = uF.getCurrentDate(CF.getStrTimeZone())+"";
				String currTime = uF.getCurrentTime(CF.getStrTimeZone())+"";
				String dateInString = currDate + " " + currTime;
				if(strRosterDate!=null) {
					dateInString = strRosterDate + " " + strFrom;
				}
				
				Calendar calendar = Calendar.getInstance();
				String strBufferRemoveTime = null;
				if(strPrevRosterDate!=null) {
					if(isPrevOverNight) {
						dateInString = uF.getDateFormat(uF.getFutureDate(uF.getDateFormatUtil(strPrevRosterDate, DBDATE), 1)+"", DBDATE, DBDATE) + " " + strPrevTo;
					} else {
						dateInString = strPrevRosterDate + " " + strPrevTo;
					}
					
//					System.out.println("dateInString ===>> " + dateInString + " -- strPrevTo ===>> " + strPrevTo);
					Date dtDate = sdf.parse(dateInString);
//					System.out.println("calendar.getTime() ===>> " + calendar.getTime());
					calendar.setTime(dtDate);
//					System.out.println("Assign calendar.getTime() ===>> " + calendar.getTime());
					calendar.add(Calendar.HOUR, +(int)dblShiftBaseBufferTime);
//					System.out.println("Add shiftBufferTime ===>> " + dblShiftBaseBufferTime+"---Calendar.HOUR=="+Calendar.HOUR);
					strBufferRemoveTime = calendar.get(Calendar.YEAR) + "-"+ uF.zero((calendar.get(Calendar.MONTH) + 1)) + "-"+ uF.zero(calendar.get(Calendar.DATE)) + " " +uF.zero(calendar.get(Calendar.HOUR_OF_DAY))+ ":"+ uF.zero(calendar.get(Calendar.MINUTE))+ ":"+ uF.zero(calendar.get(Calendar.SECOND));
//					System.out.println("+ strBufferRemoveTime ===>> " +strBufferRemoveTime);
				} else {
	//				String[] tempStrFrom = strFrom.split(":");
//					System.out.println("dateInString ===>> " + dateInString + " -- strFrom ===>> " + strFrom);
					Date dtDate = sdf.parse(dateInString);
//					System.out.println("calendar.getTime() ===>> " + calendar.getTime());
					calendar.setTime(dtDate);
//					System.out.println("Assign calendar.getTime() ===>> " + calendar.getTime());
					calendar.add(Calendar.HOUR, -(int)dblShiftBaseBufferTime);
//					System.out.println("remove shiftBufferTime ===>> " + calendar.getTime());
					strBufferRemoveTime = calendar.get(Calendar.YEAR) + "-"+ uF.zero((calendar.get(Calendar.MONTH) + 1)) + "-"+ uF.zero(calendar.get(Calendar.DATE)) + " " +uF.zero(calendar.get(Calendar.HOUR_OF_DAY))+ ":"+ uF.zero(calendar.get(Calendar.MINUTE))+ ":"+ uF.zero(calendar.get(Calendar.SECOND));
//					System.out.println("strBufferRemoveTime ===>> " +strBufferRemoveTime);
				}
				
//				System.out.println("currTime ===>> " + currTime);
				Date dtCurrDate = sdf.parse(currDate+" "+currTime);
				calendar = Calendar.getInstance();
				calendar.setTime(dtCurrDate);
				String strCurrentTime = calendar.get(Calendar.YEAR) + "-"+ uF.zero((calendar.get(Calendar.MONTH) + 1)) + "-"+ uF.zero(calendar.get(Calendar.DATE)) + " " +uF.zero(calendar.get(Calendar.HOUR_OF_DAY))+ ":"+ uF.zero(calendar.get(Calendar.MINUTE))+ ":"+ uF.zero(calendar.get(Calendar.SECOND));
				
//				System.out.println("isIn ===>> " + isIn+"--isOut="+isOut+"--isPrevOverNight="+isPrevOverNight+"--isPrevIn="+isPrevIn);
//				System.out.println("isPrevOut ===>> " + isPrevOut+"--strCurrentTime="+strCurrentTime+"--strBufferRemoveTime="+strBufferRemoveTime+"--nPrevServiceId="+nPrevServiceId);
				
				if(isIn && isOut) {
//					System.out.println("DCE1/1188---if===>");
					setStrClock(null);
					setStrMode(null);
					dashboardClockLabel="Finished!";
					dashboardClockEntryText1 = "Your last clock off was successful at " + strOutTime + ".";
//				} else if(!isIn && !isOut && isPrevOverNight && !isPrevIn && !isPrevOut && isAllowedPrevIn && nPrevServiceId > 0){
				} else if(!isIn && !isOut && isPrevOverNight && !isPrevIn && !isPrevOut && uF.getDateFormatUtil(strCurrentTime+"", DBTIMESTAMP).before(uF.getDateFormatUtil(strBufferRemoveTime+"", DBTIMESTAMP)) && nPrevServiceId > 0){
//					System.out.println("DCE1/1195---0 else if isPrevOverNight ===>");
					setStrClock("Clock Off");
					setStrMode("IN");
					setStrPrevMode("true");
					dashboardClockLabel="Clock On";
					dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late.";
//				} else if(!isIn && !isOut && isPrevOverNight && isPrevIn && !isPrevOut && isAllowedPrevOut && nPrevServiceId > 0 ){ //&& dblT > dblShiftBaseBufferTime
				} else if(!isIn && !isOut && isPrevOverNight && isPrevIn && !isPrevOut && uF.getDateFormatUtil(strCurrentTime+"", DBTIMESTAMP).before(uF.getDateFormatUtil(strBufferRemoveTime+"", DBTIMESTAMP)) && nPrevServiceId > 0 ){ //&& dblT > dblShiftBaseBufferTime
//					System.out.println("DCE1/1202---1 else if isPrevOverNight ===>");
					setStrClock("Clock On");
					setStrMode("OUT");
					setStrPrevMode("true");
					dashboardClockLabel="Clock Off";
					dashboardClockEntryText1 = "Please CLOCK OFF now to avoid being late for "+ hmServiceMap.get(nPrevServiceId+"");
					
				} else if(!isIn && !isOut && nCurrServiceId > 0){
//					System.out.println("DCE1/1210---1 else if===>");
					
//					if(!isOverNight){
//						double dbl = 0.0d;
//						if(strFrom!=null && strTo!=null) {
//							Time entryTime = uF.getTimeFormat(strFrom, DBTIME);
//							Time exitTime = uF.getTimeFormat(strTo, DBTIME);
//							Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
//							
//							long milliseconds1 = entryTime.getTime();
//							long milliseconds2 = exitTime.getTime();
//							long milliseconds3 = currentTime.getTime();
//	
//							if(milliseconds1>milliseconds3){
//								dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3, milliseconds1));
//							}else{
//								dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
//							}
////							System.out.println("dbl ===>> " + dbl);
//						}
//						if(-2.0d <= dbl && dbl <= 2.0d){
//						if((-dblShiftBaseBufferTime <= dbl && dbl <= dblShiftBaseBufferTime) || dblT==0 || dblShiftBaseBufferTime==0){
					if(!isOverNight || uF.getDateFormatUtil(strBufferRemoveTime+"", DBTIMESTAMP).before(uF.getDateFormatUtil(strCurrentTime+"", DBTIMESTAMP))){
//						System.out.println("DCE1/1234---isTodayClockOff=="+isTodayClockOff);
						if(isTodayClockOff()) {
							setStrClock(null);
							setStrMode(null);
							dashboardClockLabel="Time Out";
							/*dashboardClockEntryText1 = "You can not Clock Off for previous day, it's new day. Please refresh the page and Clock On for today.";*/
							dashboardClockEntryText1 = "It's a new day! You can not Clock Off for yesterday. Please refresh the page and Clock On for today.";
						} else {
							setStrClock("Clock Off");
							setStrMode("IN");
							dashboardClockLabel="Clock On";
							dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late.";
						}
					} else {
//							System.out.println("DCE1/1248---in else dbl ===>> ");
						if(!isIn && !isOut && isPrevOverNight && isPrevIn && isPrevOut) {
//								System.out.println("!isIn && !isOut && isPrevOverNight && isPrevIn && isPrevOut if===>");
							setStrClock(null);
							setStrMode(null);
							dashboardClockLabel="Finished!";
							dashboardClockEntryText1 = "Your last clock off was successful at " + strPrevOutTime + ".";
						} else {
							setStrClock(null);
							setStrMode(null);
							dashboardClockLabel="";
							dashboardClockEntryText1 = "Since you do not have roster beyond "+dblShiftBaseBufferTime+" hrs of the roster time. Your condition has moved to exception.";
						}
					}
//					}
				} else if(!isIn && !isOut && nCurrServiceId == 0){
//					System.out.println("DCE1/1262---2 else if===>");
					setStrClock("Clock Off");
					setStrMode("IN");
					dashboardClockLabel="Clock On";
					dashboardClockEntryText1 = "You are not rostered for today. Please CLOCK ON if you would like to manually clock on now anyway.";
				} else if(isIn && !isOut && nCurrServiceId > 0){
//					System.out.println("DCE1/1268--- 3 else if===>");
					if(isOverNight){
						/*double dbl = 0.0d;
						if(strFrom!=null && strTo!=null) {
							Time entryTime = uF.getTimeFormat(uF.getPrevDate(CF.getStrTimeZone())+strFrom, DBDATE+DBTIME);
							Time exitTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+strTo, DBDATE+DBTIME);
							Time currentTime = uF.getTimeFormat(uF.getPrevDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);
							
							long milliseconds1 = entryTime.getTime();
							long milliseconds2 = exitTime.getTime();
							long milliseconds3 = currentTime.getTime();
	
							if(milliseconds2>milliseconds3){
								dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3, milliseconds2));
							}else{
								dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds2, milliseconds3));
							}
//							System.out.println("dbl clock off ===>> " + dbl);
						}*/
//						if(dbl <= 2.0d){
//						if(dbl <= dblShiftBaseBufferTime || dblTOut==0 || dblShiftBaseBufferTime==0){	
						if(uF.getDateFormatUtil(strBufferRemoveTime+"", DBTIMESTAMP).before(uF.getDateFormatUtil(strCurrentTime+"", DBTIMESTAMP))){
							setStrClock("Clock On");
							setStrMode("OUT");
							dashboardClockLabel="Clock Off";					
							dashboardClockEntryText1 = "Please CLOCK OFF now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
						} else {
//							System.out.println("in else clock of dbl ===>> " + dbl);
							setStrClock(null);
							setStrMode(null);
							dashboardClockLabel="";
							dashboardClockEntryText1 = "Since you do not have roster beyond "+dblShiftBaseBufferTime+" hrs of the roster time. Your condition has moved to exception.";
						}
					} else {
						/*double dbl = 0.0d;
						if(strFrom!=null && strTo!=null) {
							Time entryTime = uF.getTimeFormat(strFrom, DBTIME);
							Time exitTime = uF.getTimeFormat(strTo, DBTIME);
							Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
							
							long milliseconds1 = entryTime.getTime();
							long milliseconds2 = exitTime.getTime();
							long milliseconds3 = currentTime.getTime();
	
							if(milliseconds2>milliseconds3){
								dbl = uF.parseToDouble("-"+uF.getTimeDiffInHoursMins(milliseconds3, milliseconds2));
							}else{
								dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds2, milliseconds3));
							}
//							System.out.println("dbl clock off ===>> " + dbl);
						}*/
//						if(dbl <= 2.0d){
//						if(dbl <= dblShiftBaseBufferTime || dblTOut==0 || dblShiftBaseBufferTime==0){
//						System.out.println("DCE1/1321----else ===>> ");
						if(!isOverNight || uF.getDateFormatUtil(strBufferRemoveTime+"", DBTIMESTAMP).before(uF.getDateFormatUtil(strCurrentTime+"", DBTIMESTAMP))){
							setStrClock("Clock On");
							setStrMode("OUT");
							dashboardClockLabel="Clock Off";					
							dashboardClockEntryText1 = "Please CLOCK OFF now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
						} else {
//							System.out.println("in else clock of dbl ===>> " + dbl);
							setStrClock(null);
							setStrMode(null);
							dashboardClockLabel="";
							dashboardClockEntryText1 = "Since you do not have roster beyond "+dblShiftBaseBufferTime+" hrs of the roster time. Your condition has moved to exception.";
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
	}

	public void clockOnOffButton(UtilityFunctions uF) {
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		
		try {
//			System.out.println("================================== clockOnOffButton =========================================");
			con = db.makeConnection(con);
			
			Map<String, String> hmServiceMap = CF.getServicesMap(con,true);
			boolean isCurrentRoster = new CommonFunctions(CF).isCurrentRostered(con,strEmpId);
//			System.out.println("isCurrentRoster==>"+isCurrentRoster);
			int nEmpServiceId = CF.getEmpServiceId(con,uF,strEmpId);
//			System.out.println("nEmpServiceId==>"+nEmpServiceId);
			
			pst = con.prepareStatement("SELECT is_roster,is_roster_required,is_single_button_clock_on_off FROM employee_official_details eod, user_details ud where eod.emp_id = ud.emp_id and eod.emp_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			if(rs.next()) {
				isRosterDependant = uF.parseToBoolean(rs.getString("is_roster"));
				isRosterRequired = uF.parseToBoolean(rs.getString("is_roster_required"));
				isSingleButtonClockOnOff = uF.parseToBoolean(rs.getString("is_single_button_clock_on_off"));
			}
			rs.close();
			pst.close();
//			request.setAttribute("isRosterDependant", isRosterDependant);
			
//			System.out.println("isRosterDependant ================> " + isRosterDependant);
			int nShiftBaseType = CF.getEmpShiftBaseType(uF,strEmpId,request);
//			System.out.println("nShiftBaseType ===>> " + nShiftBaseType);
			if(isRosterDependant && nShiftBaseType == 2) {
				clockOnOffButtonShiftBase(uF);
			} else if(isRosterDependant) {
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where ad.emp_id=rd.emp_id " +
						"and to_date(in_out_timestamp::text,'yyyy-MM-dd') = _date and ad.emp_id=? and _date = ? " +
						"and (in_out = ? or in_out = ?) ");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(3, "IN");
				pst.setString(4, "OUT");
//				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				boolean isIn = false;
				boolean isOut = false;
				int nCurrServiceId = 0;
				String strInTime = null;
				String strOutTime = null;
				while(rs.next()){
					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
						isIn = true;
						strInTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
						isOut = true;
						strOutTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					} 
					nCurrServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from roster_details rd where rd.emp_id=? and _date=? ");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					nCurrServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
				
				if(isIn && isOut) {
//					System.out.println("if===>");
					setStrClock(null);
					setStrMode(null);
					dashboardClockLabel="Finished!";
					dashboardClockEntryText1 = "Your last clock off was successful at " + strOutTime + ".";
				} else if(!isIn && !isOut && nCurrServiceId > 0){
					if(isTodayClockOff()) {
						setStrClock(null);
						setStrMode(null);
						dashboardClockLabel="Time Out";
						dashboardClockEntryText1 = "It's a new day! You can not Clock Off for yesterday. Please refresh the page and Clock On for today.";
					} else {
//						System.out.println("1 else if===>");
						setStrClock("Clock Off");
						setStrMode("IN");
						dashboardClockLabel="Clock On";
						dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late";
					}
				} else if(!isIn && !isOut && nCurrServiceId == 0){
//					System.out.println("2 else if===>");
					setStrClock("Clock Off");
					setStrMode("IN");
					dashboardClockLabel="Clock On";
					dashboardClockEntryText1 = "You are not rostered for today. Please CLOCK ON if you would like to manually clock on now anyway.";
				} else if(isIn && !isOut && nCurrServiceId > 0){
//					System.out.println("3 else if===>");
					setStrClock("Clock On");
					setStrMode("OUT");
					dashboardClockLabel="Clock Off";					
					dashboardClockEntryText1 = "Please CLOCK OFF now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
				}  
			} else {
				if (getStrAction() != null && getStrAction().length() > 0 && getStrAction().equals("COFF")) {
					dashboardClockEntryText1 = "Your are clocked off successfully.";
				} else if (getStrAction() != null && getStrAction().length() > 0 && getStrAction().equals("CON")) {
					dashboardClockEntryText1 = "Your are clocked on successfully.";
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void clockOnOff(String strMode) {
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {

//			System.out.println("===clockOnOff==="); 
			
			con = db.makeConnection(con);
			
			String strOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			String strWlocatoinId = CF.getEmpWlocationId(con, uF, strEmpId);
			
			pst = con.prepareStatement("SELECT * FROM roster_details WHERE emp_id=? and _date=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst ==>" + pst);  
			rs = pst.executeQuery();
			String strRosterDate = null;
			String strFrom = null;
			String strTo = null;
			int nServiceId = 0;
			while (rs.next()) {
				strFrom = rs.getString("_from");
				strTo = rs.getString("_to");
				strRosterDate = rs.getString("_date");
				nServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
			
//			System.out.println("DCE1/1507---strMode =====>>>> " + strMode);
			if (strMode != null && strMode.equalsIgnoreCase("IN")) {
				if(strFrom != null && !strFrom.trim().equals("") && !strFrom.trim().equalsIgnoreCase("NULL") 
						&& strTo != null && !strTo.trim().equals("") && !strTo.trim().equalsIgnoreCase("NULL")) {
					Time entryTime = uF.getTimeFormat(strRosterDate+strFrom, DBDATE+DBTIME);
					Time exitTime = uF.getTimeFormat(strRosterDate+strTo, DBDATE+DBTIME);
					
					Time currentTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);

					long milliseconds1 = entryTime.getTime();
					long milliseconds2 = exitTime.getTime();
					long milliseconds3 = currentTime.getTime();
					long diff = 0L;
					
					int nIN = 1;
					diff = milliseconds3 - milliseconds1;
					
					long diffSeconds = diff / 1000;
					long diffMinutes = diff / (60 * 1000);
					
					pst = con.prepareStatement("SELECT * FROM roster_policy WHERE mode = ? and time_value " + ((diffMinutes >= 0) ? ">" : "<") + "= ? " +
							"and policy_status=1 and effective_date<=? and org_id=? and wlocation_id=? order by time_value "+((diffMinutes >= 0)?"":"desc")+" LIMIT 1");
					pst.setString(1, strMode);
					pst.setLong(2, diffMinutes);
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strOrgId));
					pst.setInt(5, uF.parseToInt(strWlocatoinId));
//					System.out.println("pst==>" + pst);  
					rs = pst.executeQuery();  
					strMessage = null;
					if (diffMinutes!=0 && CF.isRosterDependency(con,strEmpId) && rs.next() && rs.getString("message")!=null 
							&& !rs.getString("message").trim().equals("") && !rs.getString("message").trim().equalsIgnoreCase("NULL") 
							&& rs.getString("message").length()>0) {
						strMessage = rs.getString("message");
						
						if(rs.getBoolean("isapproval")) {
							setStrApproval("1");
						}
						rs.close();
						pst.close();
						
					} else {
						rs.close();
						pst.close();
						
						dashboardClockEntryText1="";
//						System.out.println("one ====");
						
						ClockOnOffEntry cooe = new ClockOnOffEntry();
						cooe.setServletRequest(request);
						cooe.setStrMode(strMode);
						cooe.setIsRosterDependant(getIsRosterDependant());
						cooe.setIsRosterRequired(getIsRosterRequired());
						cooe.setIsSingleButtonClockOnOff(getIsSingleButtonClockOnOff());
						cooe.execute();
					}
					
				} else if(!new CommonFunctions(CF).isCurrentRostered(con, strEmpId) && CF.isRosterDependency(con, strEmpId) && !getIsSingleButtonClockOnOff()) {
					strNotRoster = "Please choose cost center to clock on.";
					serviceList = new FillServices(request).fillServices(strEmpId);
				} else if(!new CommonFunctions(CF).isCurrentRostered(con, strEmpId) && !CF.isRosterDependency(con, strEmpId) && !getIsSingleButtonClockOnOff()) {
					strNotRosterY = "Please choose cost center to clock on.";
					serviceList = new FillServices(request).fillServices(strEmpId);
				} else {
					dashboardClockEntryText1="";
//					System.out.println("two ====>> " );
					
					ClockOnOffEntry cooe = new ClockOnOffEntry();
					cooe.setServletRequest(request);
					cooe.setStrMode(strMode);
					cooe.setIsRosterDependant(getIsRosterDependant());
					cooe.setIsRosterRequired(getIsRosterRequired());
					cooe.setIsSingleButtonClockOnOff(getIsSingleButtonClockOnOff());
					cooe.execute();
				}
				
			} if (strMode != null && strMode.equalsIgnoreCase("OUT")) {
				
				boolean noClockOffFlag = true;
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where ad.emp_id=rd.emp_id " +
					"and to_date(in_out_timestamp::text,'yyyy-MM-dd') = _date and ad.emp_id=? and _date = ? " +
					"and (in_out = ? or in_out = ?) ");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(3, "IN");
				pst.setString(4, "OUT");
//				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				int nCurrServiceId = 0;
				String strInTime = null;
				String strOutTime = null;
				while(rs.next()){
					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
						strInTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
						strOutTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					} 
					nCurrServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
				
				if(strFrom != null && !strFrom.trim().equals("") && !strFrom.trim().equalsIgnoreCase("NULL") 
					&& strTo != null && !strTo.trim().equals("") && !strTo.trim().equalsIgnoreCase("NULL") 
					&& strInTime != null && !strInTime.trim().equals("") && !strInTime.trim().equalsIgnoreCase("NULL")) {
					Time entryTime = uF.getTimeFormat(strRosterDate+strFrom, DBDATE+DBTIME);
					Time exitTime = uF.getTimeFormat(strRosterDate+strTo, DBDATE+DBTIME);
					
					Time currentTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);

					long milliseconds1 = entryTime.getTime();
					long milliseconds2 = exitTime.getTime();
					long milliseconds3 = currentTime.getTime();
					long diff = 0L;
					
					if(entryTime.after(exitTime)) {
						milliseconds2 += 60 * 60 * 24 * 1000; 	
					}
					
					diff = milliseconds3 - milliseconds2;
					
					long diffSeconds = diff / 1000;
					long diffMinutes = diff / (60 * 1000);
					
					pst = con.prepareStatement("SELECT * FROM roster_policy WHERE mode = ? and time_value " + ((diffMinutes >= 0) ? ">" : "<") + "= ? " +
							"and policy_status=1 and effective_date<=? and org_id=? and wlocation_id=? order by time_value "+((diffMinutes >= 0)?"":"desc")+" LIMIT 1");
					pst.setString(1, strMode);
					pst.setLong(2, diffMinutes);
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strOrgId));
					pst.setInt(5, uF.parseToInt(strWlocatoinId));
//					System.out.println("pst==>" + pst);  
					rs = pst.executeQuery();  
					strMessage = null;
					if (diffMinutes!=0 && CF.isRosterDependency(con,strEmpId) && rs.next() && rs.getString("message")!=null 
							&& !rs.getString("message").trim().equals("") && !rs.getString("message").trim().equalsIgnoreCase("NULL") 
							&& rs.getString("message").length()>0) {
						strMessage = rs.getString("message");
						
						if(rs.getBoolean("isapproval")) {
							setStrApproval("1");
						}
						rs.close();
						pst.close();
						
					} else {
						rs.close();
						pst.close();
						
						dashboardClockEntryText1="";
//						System.out.println("Out one ====");
						
						ClockOnOffEntry cooe = new ClockOnOffEntry();
						cooe.setServletRequest(request);
						cooe.setStrMode(strMode);
						cooe.setIsRosterDependant(getIsRosterDependant());
						cooe.setIsRosterRequired(getIsRosterRequired());
						cooe.setIsSingleButtonClockOnOff(getIsSingleButtonClockOnOff());
						cooe.execute();
						
						noClockOffFlag = false;
					}
					
				}/* else if(!new CommonFunctions(CF).isCurrentRostered(con, strEmpId) && CF.isRosterDependency(con, strEmpId)) {
					strNotRoster = "Please choose cost center to clock on.";
					serviceList = new FillServices(request).fillServices(strEmpId);
				} else if(!new CommonFunctions(CF).isCurrentRostered(con, strEmpId) && !CF.isRosterDependency(con, strEmpId)) {
					strNotRosterY = "Please choose cost center to clock on.";
					serviceList = new FillServices(request).fillServices(strEmpId);
				}*/ /* else {
					dashboardClockEntryText1="";
//					System.out.println("Out two ====>> " );
					
					ClockOnOffEntry cooe = new ClockOnOffEntry();
					cooe.setServletRequest(request);
					cooe.setStrMode(strMode);
					cooe.setIsRosterDependant(getIsRosterDependant());
					cooe.setIsRosterRequired(getIsRosterRequired());
					cooe.setIsSingleButtonClockOnOff(getIsSingleButtonClockOnOff());
					cooe.execute();
				}*/
				setTodayClockOff(noClockOffFlag);
//				System.out.println("DCE1/1689----noClockOffFlag=="+noClockOffFlag);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
//	public void clockOnOffButton(UtilityFunctions uF) {
////		System.out.println("strEmpId ======DASH===>"+strEmpId);
//		PreparedStatement pst = null;
//		PreparedStatement pst1 = null;
//		Database db = new Database();
//		db.setRequest(request);
//		Connection con = null;
//		ResultSet rs = null;
//		ResultSet rs1 = null;
//		
//		try {
////			System.out.println("================================== clockOnOffButton =========================================");
//			con = db.makeConnection(con);
//			
//			int nPrevServiceId = 0;
//			int nCurrServiceId = 0;
//			int nCount = 0;
//			
//			pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
////			System.out.println("pst ===> "+pst);
//			rs = pst.executeQuery();
//			Time rosterStartTime = null;
////			Time currentEntryTime = uF.getCurrentTime(CF.getStrTimeZone());
//			Time currentEntryTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME);
//			while(rs.next()) {
////				rosterStartTime = rs.getTime("_from");
//				rosterStartTime = uF.getTimeFormat(rs.getDate("_date")+""+rs.getTime("_from"), DBDATE+DBTIME);
//			}
//			rs.close();
//			pst.close();
////			System.out.println("rosterStartTime ===>> " + rosterStartTime + " -- currentEntryTime ===>> " + currentEntryTime);
//			
//			pst = con.prepareStatement(selectRoster_N_COUNT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
////			System.out.println("===>"+pst);
//			rs = pst.executeQuery();
//			while(rs.next()){
//				nCount = rs.getInt("cnt");
//			}
//			rs.close();
//			pst.close();
////			System.out.println("nCount ===>> " + nCount);
//			
//			
//			pst = con.prepareStatement(selectRosterClockDetails_PREV_N_OUT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
////			System.out.println("===>"+pst);
//			rs = pst.executeQuery();
//			while(rs.next()){
//				nPrevServiceId = rs.getInt("service_id");
//			}
//			rs.close();
//			pst.close();
////			System.out.println("nPrevServiceId===>"+nPrevServiceId);
//			
//			pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
////			System.out.println("===>"+pst);
//			rs = pst.executeQuery();
//			while(rs.next()){
//				nCurrServiceId = rs.getInt("service_id");
//			}
//			rs.close();
//			pst.close();
////			System.out.println("nCurrServiceId===>"+nCurrServiceId);
//			
//			if(nCurrServiceId==0){
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
////				System.out.println("in 1 0 pst===>"+pst);
//				rs = pst.executeQuery();
//				while(rs.next()){
//					nCurrServiceId = rs.getInt("service_id");
//				}
//				rs.close();
//				pst.close();
////				System.out.println("in 1 0 nCurrServiceId ===> "+nCurrServiceId);
//			}
//			
//			if(nCurrServiceId==0){
//				pst = con.prepareStatement(selectRosterClockDetails_N2_IN);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
////				System.out.println("in 2 0 nCurrServiceId ===> "+nCurrServiceId +" -- pst ===>> " + pst);
//				rs = pst.executeQuery();
//				while(rs.next()){
//					nCurrServiceId = rs.getInt("service_id");
//				}
//				rs.close();
//				pst.close();
////				System.out.println("in 2 0 nCurrServiceId ===> "+nCurrServiceId);
//			}
//			
//			
//			if(nCurrServiceId==0) {
//				nCurrServiceId = nPrevServiceId;
//			}
//			
//				pst = con.prepareStatement(selectAttendenceClockDetails_N);
//				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//				pst.setString(2, "IN");
//				pst.setInt(3, uF.parseToInt(strEmpId));
//				pst.setInt(4, nCurrServiceId);
////				System.out.println("pst ===>> " + pst);
//				rs = pst.executeQuery();
//				boolean isPrevInOut = false;
//				if(rs.next()) {
//					
//					Time entryTime = uF.getTimeFormat(uF.getDateFormat(rs.getString("in_out_timestamp"),DBTIMESTAMP, DBTIME), DBTIME);
//					Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
//					
//					long milliseconds1 = entryTime.getTime();
//					long milliseconds3 = currentTime.getTime();
//					
//					double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
//					
////					log.debug("entryTime===>"+entryTime);
////					log.debug("currentTime===>"+currentTime);
////					log.debug("dbl===>"+dbl);
////					System.out.println("entryTime ===> " + entryTime + " -- currentTime ===> " + currentTime + " -- dbl ===> " + dbl);
//					
//					if(dbl>0.01) {
//						setStrClock("Clock On");
//						setStrMode("OUT");
//						dashboardClockLabel="Clock Off";
//						
//					}else{
//						setStrClock("Clock On");
//						setStrMode("OUT");
//						dashboardClockLabel="Clocked On";
//						
//					}
//					
//				} else {
//
//					pst1 = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
//					pst1.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
//					pst1.setString(2, "IN");
//					pst1.setString(3, "OUT");
//					pst1.setInt(4, uF.parseToInt(strEmpId));
//					pst1.setInt(5, nPrevServiceId);
////					System.out.println("selectAttendenceClockDetailsInOut===>"+pst);
//					rs1 = pst1.executeQuery();
//					boolean isPrevIn = false;
//					boolean isPrevOut = false;
//					while(rs1.next()){
//						if(rs1.getString("in_out").equalsIgnoreCase("IN")){
//							isPrevIn = true;
//						}else if(rs1.getString("in_out").equalsIgnoreCase("OUT")){
//							isPrevOut = true;
//						} 
//					}
//					rs1.close();
//					pst1.close();
//					
//					
//					if(isPrevOut){
//						isPrevInOut = true;
//					}
////					System.out.println("isPrevIn =======>> "+isPrevIn + " -- isPrevInOut ===>> "+ isPrevInOut+" ===========");
//					
//					if(isPrevIn && isPrevOut && new CommonFunctions(CF).isCurrentRostered(con,strEmpId)) {
//						
////						System.out.println("======= 1 ===========");
//						
//						setStrClock("Clock Off");
//						setStrMode("IN");
//						dashboardClockLabel="Clock On";
//					} else if(isPrevIn && !isPrevOut && new CommonFunctions(CF).isCurrentRostered(con,strEmpId) && rosterStartTime!=null && rosterStartTime.before(currentEntryTime)) {
//						
////						System.out.println("======= 2 ===========");
//						
////						System.out.println("rosterStartTime="+rosterStartTime);
////						System.out.println("currentEntryTime="+currentEntryTime);
//						
//						
//						isPrevInOut = true;
//						setStrClock("Clock On");
//						setStrMode("OUT");
//						dashboardClockLabel="Clock Off";
//					}else if(isPrevIn && !isPrevOut && new CommonFunctions(CF).isCurrentRostered(con,strEmpId) && rosterStartTime!=null && !rosterStartTime.before(currentEntryTime)){
//						
////						System.out.println("======= 2 A ===========");
//						
//						isPrevInOut = true;
//						setStrClock("Clock Off");
//						setStrMode("OUT");
//						dashboardClockLabel="Clock On";
//						
//					} else if(!isPrevIn && !isPrevOut) {
//						
////						System.out.println("======= 3 ===========");
//						
//						setStrClock("Clock Off");
//						setStrMode("IN");
//						dashboardClockLabel="Clock On";					
//					} else {
//						
////						System.out.println("======= 4 ===========");
//						
////						setStrClock(null);
////						setStrMode(null);
////						dashboardClockLabel="Not Rostered";
//						
//						setStrClock("Clock Off");
//						setStrMode("IN");
//						dashboardClockLabel="Clock On";
//					}
//				}
//				rs.close();
//				pst.close();
//				
//				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
//				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//				pst.setString(2, "IN");
//				pst.setString(3, "OUT");
//				pst.setInt(4, uF.parseToInt(strEmpId));
//				pst.setInt(5, nCurrServiceId);
////				System.out.println("1 pst=====>"+pst);
//				rs = pst.executeQuery();
//				boolean isIn = false;
//				boolean isOut = false;
//				int nLastService = 0;
//				while(rs.next()){
//					
//					if(rs.getString("in_out").equalsIgnoreCase("IN")){
//						isIn = true;
//					}else if(rs.getString("in_out").equalsIgnoreCase("OUT")){
//						isOut = true;
//						nLastService = rs.getInt("service_id");
//					} 
//				}
//				rs.close();
//				pst.close();
//				
////				System.out.println("isIn==>"+isIn);
////				System.out.println("isOut==>"+isOut);
////				System.out.println("isPrevInOut==>"+isPrevInOut);
////				System.out.println("isIn==>"+isIn);
//				
//				
////				if(isIn && isOut && CF.isCurrentRostered(con,CF,strEmpId, nCurrServiceId)){
//				if(!isIn && !isOut && new CommonFunctions(CF).isCurrentRostered(con,CF,strEmpId, nCurrServiceId)){
//					setStrClock("Clock Off");
//					setStrMode("IN");
//					dashboardClockLabel="Clock On";
//					
////					System.out.println("======= 5 ===========");
//					
//				} else if(isIn && isOut) {
//					setStrClock(null);
//					setStrMode(null);
//					dashboardClockLabel="Finished!";
//					
////					System.out.println("======= 6 ===========");
//					
////				} else if(isIn) {
//				} else if(isIn && !isOut) {
//					setStrClock("Clock On");
//					setStrMode("OUT");
//					dashboardClockLabel="Clock Off";
//					
////					System.out.println("======= 7 ===========");
//					
//					
////				}else if(isOut){
//				} else if(!isIn && isOut) {
//					setStrClock("Clock Off");
//					setStrMode("IN");
//					dashboardClockLabel="Clock On";
//					
////					System.out.println("======= 8 ===========");
//					
//				} else if(!isIn && !isOut && !isPrevInOut && !new CommonFunctions(CF).isCurrentRostered(con,CF,strEmpId, nCurrServiceId)) { 
//					/*setStrClock("Clock On");
//					setStrMode("IN");
//					dashboardClockLabel="Clock Off";*/
//					
//					setStrClock("Clock Off");
//					setStrMode("IN");
//					dashboardClockLabel="Clock On";
//					
////					System.out.println("======= "+isPrevInOut+" ===========");
//					
////					System.out.println("======= 9 ===========");
//				}else if(!isIn && !isOut && !isPrevInOut) {
//					setStrClock("Clock Off");
//					setStrMode("IN");
//					dashboardClockLabel="Clock On";
//					
////					System.out.println("======= 10 ===========");
//					
//				}
////			}
//			
//			
//			
//			/**
//			 *  IS Roster Dependent
//			 */
//			
//			pst = con.prepareStatement(selectRosterDependent);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			rs = pst.executeQuery();
//			
//			String strEmpType = null;
//			boolean isRoster = false;
//			if(rs.next()){
//				strEmpType = rs.getString("emptype");
//				isRoster = uF.parseToBoolean(rs.getString("is_roster"));
//			}
//			rs.close();
//			pst.close();
////			System.out.println("strEmpType ===>> " + strEmpType + " -- isRoster ===>> " + isRoster);
//			
//			
//			isIn = false;
//			isOut = false;
//			
//			if(strEmpType!=null && !isRoster) {
//				
//				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
//				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//				pst.setString(2, "IN");
//				pst.setString(3, "OUT");
//				pst.setInt(4, uF.parseToInt(strEmpId));
//				pst.setInt(5, nCurrServiceId);
////				System.out.println("2 pst=====>"+pst);
//				rs = pst.executeQuery();
//				while(rs.next()) {
//					
//					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
//						isIn = true;	
//					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
//						isOut = true;
//					}
//				}
//				rs.close();
//				pst.close();
////				System.out.println("!isRoster isIn ===>> " + isIn + " -- isOut ===>> " + isOut);
//				
//				if(isIn && isOut && new CommonFunctions(CF).isCurrentRostered(con,CF,strEmpId, 2)) {
//					setStrClock("Clock Off");
//					setStrMode("IN");
//					dashboardClockLabel="Clock On";
//				} else if(isIn && isOut) {
//					setStrClock(null);
//					setStrMode(null);
//					dashboardClockLabel="Finished!";
//				} else if(!isIn && !isOut) {
//					setStrClock("Clock Off");
//					setStrMode("IN");
//					dashboardClockLabel="Clock On";
////				}else if(isIn) {
//				}else if(isIn && !isOut) {
//					setStrClock("Clock On");
//					setStrMode("OUT");
//					dashboardClockLabel="Clock Off";
////				}else if(isOut){
//				} else if(!isIn && isOut) {
//					setStrClock("Clock Off");
//					setStrMode("IN");
//					dashboardClockLabel="Clock On";
//				}
////				System.out.println("dashboardClockLabel ===>> " + dashboardClockLabel);
//			}
//			
////			System.out.println("End ===>> ");
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		} finally {
//			db.closeResultSet(rs);
//			db.closeResultSet(rs1);
//			db.closeStatements(pst);
//			db.closeStatements(pst1);
//			db.closeConnection(con);
//		}
//		
//	}
	
	
//	public void clockOnOffMessage(UtilityFunctions uF) {
//
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		Connection con = null;
//		ResultSet rs = null;
//		
//		try {
//			System.out.println(" ===================== clockOnOffMessage =========================");
//			con = db.makeConnection(con);
//			Map<String, String> hmServiceMap = CF.getServicesMap(con,true);
//
//			int nPrevServiceId = 0;
//			int nCurrServiceId = 0;
//			int nCount = 0;
//			
//			pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			Time rosterStartTime = null;
//			Time currentEntryTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME);
//			while(rs.next()) {
//				if(rs.getDate("_date") !=null && !rs.getDate("_date").equals("") && rs.getTime("_from") != null && !rs.getTime("_from").equals("")) {
//					rosterStartTime = uF.getTimeFormat(rs.getDate("_date")+""+rs.getTime("_from"), DBDATE+DBTIME);
//				}
//			}
//			rs.close();
//			pst.close();
////			System.out.println("currentEntryTime ===>> " + currentEntryTime +" -- rosterStartTime ===>> " + rosterStartTime);
//			
//			pst = con.prepareStatement(selectRoster_N_COUNT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				nCount = rs.getInt("cnt");
//			}
//			rs.close();
//			pst.close();
////			System.out.println("nCount ===>> " + nCount);
//			
//			pst = con.prepareStatement(selectRosterClockDetails_PREV_N_OUT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			while(rs.next()) {
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
//			while(rs.next()) {
//				nCurrServiceId = rs.getInt("service_id");
//			}
//			rs.close();
//			pst.close();
////			System.out.println("nCurrServiceId ===>> " + nCurrServiceId);
//			
//			if(nCurrServiceId==0) {
//				if(nCount>1) {
//					pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//				} else {
//					pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				}
//				rs = pst.executeQuery();
//				while(rs.next()) {
//					nCurrServiceId = rs.getInt("service_id");
//				}
//				rs.close();
//				pst.close();
////				System.out.println("nCurrServiceId == 0 ===>> " + nCurrServiceId);
//			}
//			
//			if(nCurrServiceId==0) {
//				pst = con.prepareStatement(selectRosterClockDetails_N2_IN);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				rs = pst.executeQuery();
//				while(rs.next()) {
//					nCurrServiceId = rs.getInt("service_id");
//				}
//				rs.close();
//				pst.close();
////				System.out.println("nCurrServiceId ==0 == 0 ===>> " + nCurrServiceId);
//			}
//			
//			
//			if(nCurrServiceId==0) {
//				nCurrServiceId = nPrevServiceId;
//			}
//			
//				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
//				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));				
//				pst.setString(2, "IN");
//				pst.setString(3, "OUT");
//				pst.setInt(4, uF.parseToInt(strEmpId));
//				pst.setInt(5, nCurrServiceId); 
//				rs = pst.executeQuery();
//				boolean isIn = false;
//				boolean isOut = false;
//
//				String strInTime = null;
//				String strOutTime = null;
//				int nLastService = 0;
//
//				while (rs.next()) {
//					if (rs.getString("in_out") != null && rs.getString("in_out").equalsIgnoreCase("IN")) {
//						isIn = true;
//						strInTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
//					} else if (rs.getString("in_out") != null && rs.getString("in_out").equalsIgnoreCase("OUT")) {
//						isOut = true;
//						strOutTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
//						nLastService = rs.getInt("service_id");
//					}
//				}
//				rs.close();
//				pst.close();
////				System.out.println("isIn ===>> " + isIn +" -- isOut ===>> " + isOut);
////				System.out.println("strInTime ===>> " + strInTime +" -- strOutTime ===>> " + strOutTime +" -- nLastService ===>> " + nLastService);
//				
////				System.out.println(" =======  20  ======= "+nCurrServiceId);
//				
//				if(isIn && isOut && new CommonFunctions(CF).isCurrentRostered(con,CF,strEmpId, nCurrServiceId)) {
//					setStrClock("Clock Off");
//					setStrMode("IN");
//					dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late";
////					System.out.println(" =======  21  ======= ");
//					
//				} else if (isIn && isOut) {
//					dashboardClockEntryText1 = "Your last clock off was successful at " + strOutTime + ".";
////					System.out.println(" =======  21  A ======= ");
//				} else if (!isIn && !isOut) {
//
//					pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
//					pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
//					pst.setString(2, "IN");
//					pst.setString(3, "OUT");
//					pst.setInt(4, uF.parseToInt(strEmpId));
//					pst.setInt(5, nPrevServiceId);
//					rs = pst.executeQuery();
//					boolean isPrevIn = false;
//					boolean isPrevOut = false;
//					while(rs.next()) {
//						
//						if(rs.getString("in_out").equalsIgnoreCase("IN")) {
//							isPrevIn = true;
//						} else if(rs.getString("in_out").equalsIgnoreCase("OUT")) {
//							isPrevOut = true;
//						} 
//					}
//					rs.close();
//					pst.close();
//					
//					if(isPrevIn && isPrevOut && new CommonFunctions(CF).isCurrentRostered(con,strEmpId)) {
//						System.out.println(" =======  23  ======= ");
//						
//						dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late";
//					} else if(isPrevIn && !isPrevOut && new CommonFunctions(CF).isCurrentRostered(con,strEmpId)) {
//						
//						System.out.println(" =======  24  ======= ");
//						
////						pst = con.prepareStatement(selectRosterClockDetails);
////						pst.setInt(1, uF.parseToInt(strEmpId));
////						pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//						
//						if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")) {
//							pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//							pst.setInt(1, uF.parseToInt(strEmpId));
//							pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//							pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//						} else {
//							pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//							pst.setInt(1, uF.parseToInt(strEmpId));
//							pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//							
//						}
//						
//						rs = pst.executeQuery();
//						String _toPrev = null;
//						while(rs.next()) {
//							_toPrev = rs.getString("_to");
//						}
//						rs.close();
//						pst.close();
////						System.out.println("_toPrev ===>> " + _toPrev);
//						
//						long rosterTo = 0;
//						long current = 0;
//						if(_toPrev!=null) {
//							rosterTo = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone())+_toPrev, DBDATE+DBTIME).getTime();
//							current = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME).getTime();
//						}
//						
//						System.out.println(" =======  24 0 ======= ");
//						
////						if(rosterStartTime!=null && rosterStartTime.after(currentEntryTime)) {
//						if(rosterStartTime!=null && rosterStartTime.before(currentEntryTime)) {
//							
//							if(rosterTo>current) {
//								
//								System.out.println(" =======  24 1 ======= ");
//								dashboardClockEntryText1 = "You are clocked on for "+ hmServiceMap.get(nCurrServiceId+"")+", please clock off according to your roster";
//							} else {
//								System.out.println(" =======  24 2 ======= ");
//								dashboardClockEntryText1 = "Please CLOCK OFF now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
//							}
//						} else {
//							System.out.println(" =======  24 3 ======= ");
//							dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
//						}
//						
//					} else if(!isPrevIn && !isPrevOut && new CommonFunctions(CF).isCurrentRostered(con,CF,strEmpId, nCurrServiceId)) {
//						System.out.println(" =======  25  ======= ");
//						dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
//					} else if(!isIn && !isOut && !isPrevOut && nCurrServiceId>0) {
//						System.out.println(" =======  26  ======= ");
//						dashboardClockEntryText1 = "Please CLOCK OFF now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
//					} else if(!isIn && !isOut && !new CommonFunctions(CF).isCurrentRostered(con,CF,strEmpId, nCurrServiceId)) {
//						System.out.println(" =======  26A  ======= ");
//						dashboardClockEntryText1 = "You are not rostered for today. Please CLOCK ON if you would like to manually clock on now anyway.";
//					} 
//					
//				} else if (isIn) {
//
//					System.out.println(" =======  27  ======= ");
//					
//					Time entryTime = uF.getTimeFormat(strInTime, CF.getStrReportTimeFormat());
//					Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
//
//					long milliseconds1 = entryTime.getTime();
//					long milliseconds3 = currentTime.getTime();
//
//					double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
//
//					
//					if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")) {
//						pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//					} else {
//						pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//						
//					}
//					rs = pst.executeQuery();
//					String _toCurr = null;
//					while(rs.next()) {
//						_toCurr = rs.getString("_to");
//					}
//					rs.close();
//					pst.close();
//					System.out.println("_toCurr ===>> " + _toCurr);
//					
//					long rosterTo = 0;
//					long current = 0;
//					if(_toCurr!=null) {
//						rosterTo = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+_toCurr, DBDATE+DBTIME).getTime();
//						current = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME).getTime();
//					}
//					
//					if(rosterTo>current) {
//						dashboardClockEntryText1 = "You are clocked on for "+ hmServiceMap.get(nCurrServiceId+"")+", please clock off according to your roster";
//					} else {
//						dashboardClockEntryText1 = "Please CLOCK OFF now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
//					}
//					
//				} else if(isOut) {
//					System.out.println(" =======  28  ======= ");
//					setStrClock("Clock Off");
//					setStrMode("IN");
//					dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
//				} else if(!isIn && !isOut && !new CommonFunctions(CF).isCurrentRostered(con,strEmpId)) {
//					System.out.println(" =======  29  ======= ");
//					setStrClock(null);
//					setStrMode(null);
//					dashboardClockEntryText1 = "If you think, you should have roster please see your manager";
////					dashboardClockEntryText2 = "If this start/end time is not correct, please see your manager to update your roster";
//				} else if(!isIn && !isOut && new CommonFunctions(CF).isCurrentRostered(con,strEmpId)) {
//					System.out.println(" =======  30  ======= ");
//					setStrClock("Clock Off");
//					setStrMode("IN");
//					dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
//				}
////			}
//				
//				System.out.println(" =======  31  ======= ");
//			
//			/**
//			 *  IS Roster Dependent
//			 */
//			
//			pst = con.prepareStatement(selectRosterDependent);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			rs = pst.executeQuery();
//			
//			String strEmpType = null;
//			boolean isRoster = false;
//			if(rs.next()) {
//				strEmpType = rs.getString("emptype");
//				isRoster = uF.parseToBoolean(rs.getString("is_roster"));
//			}
//			rs.close();
//			pst.close();
//			System.out.println("strEmpType ===>> " + strEmpType +" -- isRoster ===>> " + isRoster);
//			
////			boolean isIn = false;
////			boolean isOut = false;
//			
//			isIn = false;
//			isOut = false;
//			
//			if(strEmpType!=null && !isRoster) {
//				
//				System.out.println(" =======  31 0  ======= ");
//				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
//				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//				pst.setString(2, "IN");
//				pst.setString(3, "OUT");
//				pst.setInt(4, uF.parseToInt(strEmpId));
//				pst.setInt(5, nCurrServiceId);
//				rs = pst.executeQuery();
//				while(rs.next()) {
//					
//					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")) {
//						isIn = true;	
//					} else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")) {
//						isOut = true;
//					}
//				}
//				rs.close();
//				pst.close();
//				
//				
//				if(isIn && isOut) {
//					setStrClock(null);
//					setStrMode(null);
//					
//				} else if(!isIn && !isOut) {
//					setStrClock("Clock Off");
//					setStrMode("IN");
//					dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late";
//				} else if(isIn) {
//					setStrClock("Clock On");
//					setStrMode("OUT");
//					
//					if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")) {
//						pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//					} else {
//						pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//						
//					}
//					rs = pst.executeQuery();
//					String _toCurr = null;
//					while(rs.next()) {
//						_toCurr = rs.getString("_to");
//					}
//					rs.close();
//					pst.close();
//					System.out.println("_toCurr ===>> " + _toCurr);
//					
//					long rosterTo = 0;
//					long current = 0;
//					if(_toCurr!=null) {
//						rosterTo = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+_toCurr, DBDATE+DBTIME).getTime();
//						current = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME).getTime();
//					}
//					
//					if(rosterTo>current) {
//						dashboardClockEntryText1 = "You are clocked on to the system";
//					} else {
//						dashboardClockEntryText1 = "Please CLOCK OFF before you leave for the day";
//					}
//					
//				} else if(isOut) {
//					setStrClock("Clock Off");
//					setStrMode("IN");
//					dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late";
//				}
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
//	public void clockOnOff(String strMode) {
//
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		Connection con = null;
//		ResultSet rs = null;
//
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//
//			con = db.makeConnection(con);
//
//			Map hmTardy = CF.getTardyType(con);
//			int nTardyIn = uF.parseToInt((String)hmTardy.get("TARDY_IN"));
//			int nTardyOut = uF.parseToInt((String)hmTardy.get("TARDY_OUT"));
//		   
//			
//			int nPrevServiceId = 0;
//			int nCurrServiceId = 0;
//			int nCount = 0;
//			
//			pst = con.prepareStatement(selectRoster_N_COUNT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				nCount = rs.getInt("cnt");
//			}
//			rs.close();
//			pst.close();
//			
//			
//			pst = con.prepareStatement(selectRosterClockDetails_PREV_N_OUT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				nPrevServiceId = rs.getInt("service_id");
//			}
//			rs.close();
//			pst.close();
//			
//			
//			pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				nCurrServiceId = rs.getInt("service_id");
//			}
//			rs.close();
//			pst.close();
//			
//			
//			if(nCurrServiceId==0) {
//				if(nCount>1) {
//					pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//				} else {
//					pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				}
//				rs = pst.executeQuery();
//				while(rs.next()) {
//					nCurrServiceId = rs.getInt("service_id");
//				}
//				rs.close();
//				pst.close();
//			}
//			
//			
//			if(nCurrServiceId==0) {
//				pst = con.prepareStatement(selectRosterClockDetails_N2_IN);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				rs = pst.executeQuery();
//				while(rs.next()) {
//					nCurrServiceId = rs.getInt("service_id");
//				}
//				rs.close();
//				pst.close();
//			}
//			
//			if(nCurrServiceId==0) {
//				nCurrServiceId = nPrevServiceId;
//			}
//			
//			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//			int YEAR = cal.get(Calendar.YEAR);
//			int MONTH = cal.get(Calendar.MONTH) + 1;
//			int DAY = cal.get(Calendar.DAY_OF_MONTH);
//
//			String strPrevRosterDate = null;
//			Time tPrevFrom = null;
//			Time tPrevTo = null;
//			
//			String strRosterDate = "";
//			String strFrom = "00:00:00";
//			String strTo = "00:00:00";
//			
//			
//			if (strMode != null && strMode.equalsIgnoreCase("IN")) {
////				pst = con.prepareStatement("SELECT * FROM attendance_details where emp_id =? and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD')=? and in_out='IN'");
//				pst = con.prepareStatement(selectAttendenceClockDetails_N);
//				
//				pst.setDate(1, uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "" + DAY, "yyyyMMdd"));
//				pst.setString(2, "IN");
//				pst.setInt(3, uF.parseToInt(strEmpId));
//				pst.setInt(4, nCurrServiceId);
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					return;
//				}
//				rs.close();
//				pst.close();
//
//			} else if (strMode != null && strMode.equalsIgnoreCase("OUT")) {
//				pst = con.prepareStatement(selectAttendenceClockDetails_N);
//				pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
//				pst.setString(2, strMode);
//				pst.setInt(3, uF.parseToInt(strEmpId));
//				pst.setInt(4, nPrevServiceId);
//				rs = pst.executeQuery();
//				boolean isPrevOut = false;
//				boolean isPrevRoster = false;
//				if(rs.next()) {
//					isPrevOut = true;
//				}
//				rs.close();
//				pst.close();
//				
//				if(!isPrevOut) {
//					if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")) {
//						pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//						pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//					} else {
//						pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//						
//					}
//					rs = pst.executeQuery();
//					if(rs.next()) {
//						isPrevRoster = true;
//						
//						tPrevFrom = rs.getTime("_from");
//						tPrevTo = rs.getTime("_to");
//						strPrevRosterDate = rs.getString("_date");
//					}
//					rs.close();
//					pst.close();
//
//				} else {
//					pst = con.prepareStatement(selectAttendenceClockDetails_N);
//					pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setString(2, strMode);
//					pst.setInt(3, uF.parseToInt(strEmpId));
//					pst.setInt(4, nCurrServiceId);
//					rs = pst.executeQuery();
//
//					if(rs.next()) {
//						return;
//					}
//					rs.close();
//					pst.close();
//				}
//			}
//
//			
//			if(tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() < tPrevTo.getTime()) {
//				
//				if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")) {
//					if(nCount>1) {
//						pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//						pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//					} else {
//						pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
//						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//					}
//					
//				} else {
//					pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//					
//				}
////				System.out.println("1 pst======>"+pst);
//				rs = pst.executeQuery();
//
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
//				pst.close();
//			} else if(tPrevFrom!=null && tPrevTo!=null &&  tPrevFrom.getTime() > tPrevTo.getTime()) {
//				if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")) {
//					pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//					pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//				} else {
//					pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//					
//				}
////				System.out.println("2 pst======>"+pst);
//				rs = pst.executeQuery();
//
//				strRosterDate = null;
//				strFrom = null;
//				strTo = null;
//
//				while (rs.next()) {
//					strFrom = rs.getString("_from");
//					strTo = rs.getString("_to");
//					strRosterDate = rs.getString("_date");
//				}
//
//				rs.close();
//				pst.close();
//			} else {
//				if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")) {
//					if(nCount>1) {
//						pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//					} else {
//						pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
//						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					}
//					
//				} else {
//					pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					
//				}
////				System.out.println("3 pst======>"+pst);
//				rs = pst.executeQuery();
//
//				strRosterDate = null;
//				strFrom = null;
//				strTo = null;
//				int nServiceIdTemp = 0;
//
//				while (rs.next()) {
//					strFrom = rs.getString("_from");
//					strTo = rs.getString("_to");
//					strRosterDate = rs.getString("_date");
//					nServiceIdTemp = rs.getInt("service_id");
//				}
//				rs.close();
//				pst.close();
//				
//				
//				if(nServiceIdTemp==0) {
//					pst = con.prepareStatement(selectRosterClockDetails_N2_IN);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					
////					System.out.println("4 pst======>"+pst);
//					rs = pst.executeQuery();
//					while (rs.next()) {
//						strFrom = rs.getString("_from");
//						strTo = rs.getString("_to");
//						strRosterDate = rs.getString("_date");
//						nServiceIdTemp = rs.getInt("service_id");
//					}
//					rs.close();
//					pst.close();
//				}
//			}
//			
//			
////			System.out.println("strFrom===>"+strFrom);
////			System.out.println("strTo===>"+strTo);
//			
//			if(strFrom!=null && strTo!=null) {
//				
//				Time entryTime = uF.getTimeFormat(strRosterDate+strFrom, DBDATE+DBTIME);
//				Time exitTime = uF.getTimeFormat(strRosterDate+strTo, DBDATE+DBTIME);
////				Time exitTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+strTo, DBDATE+DBTIME);
//				
//				Time currentTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);
//
//				long milliseconds1 = entryTime.getTime();
//				long milliseconds2 = exitTime.getTime();
//				long milliseconds3 = currentTime.getTime();
//				long diff = 0L;
//
//				int nIN = 0;
//				if (strMode.equals("IN")) {
//					nIN = 1;
//					diff = milliseconds3 - milliseconds1;
//				} else {
//					
//					if(entryTime.after(exitTime)) {
//						milliseconds2 += 60 * 60 * 24 * 1000; 	
//					}
//					
//					diff = milliseconds3 - milliseconds2;
//				}
//
//				long diffSeconds = diff / 1000;
//				long diffMinutes = diff / (60 * 1000);
//
//				rs = null;
//				
//				String strOrgId = CF.getEmpOrgId(con, uF, strEmpId);
//				String strWlocatoinId = CF.getEmpWlocationId(con, uF, strEmpId);
//				
////				pst = con.prepareStatement("SELECT * FROM roster_policy WHERE mode = ? and time_value " + ((diffMinutes >= 0) ? ">" : "<") + "= ? and time_type!= 'TARDY' order by time_value desc LIMIT 1");
//				pst = con.prepareStatement("SELECT * FROM roster_policy WHERE mode = ? and time_value " + ((diffMinutes >= 0) ? ">" : "<") + "= ? " +
//						"and policy_status=1 and effective_date<=? and org_id=? and wlocation_id=? order by time_value "+((diffMinutes >= 0)?"":"desc")+" LIMIT 1");
//				pst.setString(1, strMode);
//				pst.setLong(2, diffMinutes);
//				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//				pst.setInt(4, uF.parseToInt(strOrgId));
//				pst.setInt(5, uF.parseToInt(strWlocatoinId));
////				System.out.println("pst 2 ====" + pst);  
//				rs = pst.executeQuery();  
//				strMessage = null;
//				if (diffMinutes!=0 && CF.isRosterDependency(con,strEmpId) && rs.next() && rs.getString("message")!=null && !rs.getString("message").trim().equals("") && rs.getString("message").length()>0) {
//					strMessage = rs.getString("message");
//					
//					if(rs.getBoolean("isapproval")) {
//						setStrApproval("1");
//					}
//					
//				} else {
//					
//					dashboardClockEntryText1="";
//					System.out.println("one ====");
//					
//					ClockOnOffEntry cooe = new ClockOnOffEntry();
//					cooe.setServletRequest(request);
//					cooe.setStrMode(strMode);					
//					cooe.execute();
//				}
//				rs.close();
//				pst.close();
//				
////				System.out.println("strMessage ====" + strMessage); 
////				System.out.println("getStrApproval ====" + getStrApproval()); 
//				
//			} else if(!new CommonFunctions(CF).isCurrentRostered(con, strEmpId) && CF.isRosterDependency(con, strEmpId)) {
//				strNotRoster = "Please choose cost center to clock on.";
//				serviceList = new FillServices(request).fillServices(strEmpId);
//			} else if(!new CommonFunctions(CF).isCurrentRostered(con, strEmpId) && !CF.isRosterDependency(con, strEmpId)) {
//				strNotRosterY = "Please choose cost center to clock on.";
//				serviceList = new FillServices(request).fillServices(strEmpId);
//			} else {
//				dashboardClockEntryText1="";
//				System.out.println("two ====>> " );
//				
//				ClockOnOffEntry cooe = new ClockOnOffEntry();
//				cooe.setServletRequest(request);
//				cooe.setStrMode(strMode);
//				cooe.execute();
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	

	public String getStrClock() {
		return strClock;
	}

	public void setStrClock(String strClock) {
		this.strClock = strClock;
	}

	public String getDashboardClockEntryText1() {
		return dashboardClockEntryText1;
	}

	public void setDashboardClockEntryText1(String dashboardClockEntryText1) {
		this.dashboardClockEntryText1 = dashboardClockEntryText1;
	}

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

	public String getStrMode() {
		return strMode;
	}

	public void setStrMode(String strMode) {
		this.strMode = strMode;
	}

	public String getStrNotRoster() {
		return strNotRoster;
	}

	public void setStrNotRoster(String strNotRoster) {
		this.strNotRoster = strNotRoster;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public String getStrNotRosterY() {
		return strNotRosterY;
	}

	public void setStrNotRosterY(String strNotRosterY) {
		this.strNotRosterY = strNotRosterY;
	}

	public String getStrApproval() {
		return strApproval;
	}

	public void setStrApproval(String strApproval) {
		this.strApproval = strApproval;
	}

	public String getDashboardClockLabel() {
		return dashboardClockLabel;
	}

	public void setDashboardClockLabel(String dashboardClockLabel) {
		this.dashboardClockLabel = dashboardClockLabel;
	}

	public String getStrAction() {
		return strAction;
	}

	public void setStrAction(String strAction) {
		this.strAction = strAction;
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

	public void setRosterDependant(boolean isRosterDependant) {
		this.isRosterDependant = isRosterDependant;
	}

	public boolean getIsRosterRequired() {
		return isRosterRequired;
	}

	public void setRosterRequired(boolean isRosterRequired) {
		this.isRosterRequired = isRosterRequired;
	}

	public boolean getIsSingleButtonClockOnOff() {
		return isSingleButtonClockOnOff;
	}

	public void setSingleButtonClockOnOff(boolean isSingleButtonClockOnOff) {
		this.isSingleButtonClockOnOff = isSingleButtonClockOnOff;
	}

	public boolean isTodayClockOff() {
		return isTodayClockOff;
	}

	public void setTodayClockOff(boolean isTodayClockOff) {
		this.isTodayClockOff = isTodayClockOff;
	}
	
}