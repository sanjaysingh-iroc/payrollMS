package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.tms.ClockOnOffEntry;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class DashboardClockEntry extends ActionSupport implements ServletRequestAware, IStatements {

	private String dashboardClockEntryText1;
	private String dashboardClockEntryText2;
	private String strClock;
	private String strMessage;
	private String strNotRoster;
	private String strNotRosterY;
	List <FillServices> serviceList;
	private String strMode;
	private String strApproval;  
	 
	String strEmpId = null;
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(DashboardClockEntry.class);
	

	public String execute() throws Exception {

		HttpSession session = request.getSession();
		strEmpId = (String) session.getAttribute("EMPID");
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		
//		log.debug("getStrClock( Entry ) Read =====>" + getStrClock());
//		System.out.println("getStrClock( Entry ) Read =====>" + getStrClock());

		if (getStrClock() != null && getStrClock().length() > 0 && getStrClock().indexOf("Clock Off") > 0) {

//			log.debug("getStrClock( Entry )= In ====> CLOCK OFF ");
			setStrMode("OUT"); 
			clockOnOff("OUT");

		} else if (getStrClock() != null && getStrClock().length() > 0 && getStrClock().indexOf("Clock On") > 0) {

//			log.debug("getStrClock( Entry )== In ===> CLOCK ON");
			setStrMode("IN");
			clockOnOff("IN");
		}

		
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		
		try {
			
			UtilityFunctions uF = new UtilityFunctions();
			con = db.makeConnection(con);
			Map<String, String> hmServiceMap = CF.getServicesMap(con,true);

			int nPrevServiceId = 0;
			int nCurrServiceId = 0;
			int nCount = 0;
			
			pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			Time rosterStartTime = null;
//			Time currentEntryTime = uF.getCurrentTime(CF.getStrTimeZone());
			Time currentEntryTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME);
			while(rs.next()){
//				rosterStartTime = rs.getTime("_from");
				rosterStartTime = uF.getTimeFormat(rs.getDate("_date")+""+rs.getTime("_from"), DBDATE+DBTIME);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(selectRoster_N_COUNT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				nCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(selectRosterClockDetails_PREV_N_OUT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				nPrevServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				nCurrServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
			
//			log.debug("pst nServiceId===>"+pst);
			
			if(nCurrServiceId==0){
				if(nCount>1){
					pst = con.prepareStatement(selectRosterClockDetails_N_IN);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
				}else{
					pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				}
//				log.debug("pst nServiceId===>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					nCurrServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
			}
			
			if(nCurrServiceId==0){
				pst = con.prepareStatement(selectRosterClockDetails_N2_IN);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				log.debug("pst nServiceId===>"+pst);
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
			
			
//			if (!new CommonFunctions(CF).isRostered(strEmpId)) {
//
//				dashboardClockEntryText1 = "If you think, you should have roster please see your manager";
////				dashboardClockEntryText2 = "If this start/end time is not correct, please see your manager to update your roster";
//				dashboardClockEntryText2 = "";
//				
//				
//				
//				pst = con.prepareStatement(selectRosterDependent);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				rs = pst.executeQuery();
//				
//				String strEmpType = null;
//				boolean isRoster = false;
//				if(rs.next()){
//					
//					strEmpType = rs.getString("emptype");
//					isRoster = uF.parseToBoolean(rs.getString("is_roster"));
//				}
//				
//				if(strEmpType!=null && !isRoster){
//					dashboardClockEntryText1 = "";
//				}
//				
//
//			} else {

//				pst = con.prepareStatement(selectAttendenceClockDetails1);
				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));				
				pst.setString(2, "IN");
				pst.setString(3, "OUT");
				pst.setInt(4, uF.parseToInt(strEmpId));
				pst.setInt(5, nCurrServiceId); 
				rs = pst.executeQuery();
				boolean isIn = false;
				boolean isOut = false;

				String strInTime = null;
				String strOutTime = null;
				int nLastService = 0;

				while (rs.next()) {
					if (rs.getString("in_out") != null && rs.getString("in_out").equalsIgnoreCase("IN")) {
						isIn = true;
						strInTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
					} else if (rs.getString("in_out") != null && rs.getString("in_out").equalsIgnoreCase("OUT")) {
						isOut = true;
						strOutTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat());
						nLastService = rs.getInt("service_id");
					}
				}
				rs.close();
				pst.close();

				
				
//				log.debug(" =======  20  ======= "+nCurrServiceId);
//				System.out.println(" =======  20  ======= "+nCurrServiceId);
				
				if(isIn && isOut && new CommonFunctions(CF).isCurrentRostered(con,CF,strEmpId, nCurrServiceId)){
					setStrClock("Clock Off");
					setStrMode("IN");
					dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late";
					dashboardClockEntryText2 = "If this start/end time is not correct, please see your manager to update your roster";
//					log.debug(" =======  21  ======= ");
//					System.out.println(" =======  21  ======= ");
					
				}else if (isIn && isOut) {
					dashboardClockEntryText1 = "Your last clock off was successful at " + strOutTime + ".";
					dashboardClockEntryText2 = "";
//					System.out.println(" =======  21  A ======= ");
				} else if (!isIn && !isOut) {

					pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
					pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
					pst.setString(2, "IN");
					pst.setString(3, "OUT");
					pst.setInt(4, uF.parseToInt(strEmpId));
					pst.setInt(5, nPrevServiceId);
					rs = pst.executeQuery();
					boolean isPrevIn = false;
					boolean isPrevOut = false;
					while(rs.next()){
						
						if(rs.getString("in_out").equalsIgnoreCase("IN")){
							isPrevIn = true;
						}else if(rs.getString("in_out").equalsIgnoreCase("OUT")){
							isPrevOut = true;
						} 
					}
					rs.close();
					pst.close();
					
					if(isPrevIn && isPrevOut && new CommonFunctions(CF).isCurrentRostered(con,strEmpId)){
//						log.debug(" =======  23  ======= ");
//						System.out.println(" =======  23  ======= ");
						
						dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late";
						dashboardClockEntryText2 = "If this start/end time is not correct, please see your manager to update your roster";
					}else if(isPrevIn && !isPrevOut && new CommonFunctions(CF).isCurrentRostered(con,strEmpId)){
						
//						log.debug(" =======  24  ======= ");
//						System.out.println(" =======  24  ======= ");
						
//						pst = con.prepareStatement(selectRosterClockDetails);
//						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
						
						if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")){
							pst = con.prepareStatement(selectRosterClockDetails_N_IN);
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
							pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
						}else{
							pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
							
						}
						
						rs = pst.executeQuery();
						String _toPrev = null;
						while(rs.next()){
							_toPrev = rs.getString("_to");
						}
						rs.close();
						pst.close();
						
						long rosterTo = 0;
						long current = 0;
						if(_toPrev!=null){
							rosterTo = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone())+_toPrev, DBDATE+DBTIME).getTime();
							current = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME).getTime();
						}
						
//						log.debug("===1==== rosterTo=======>"+rosterTo);
//						log.debug("===1==== current=======>"+current);
						
//						System.out.println(" =======  24 0 ======= ");
						
						
						
						
//						if(rosterStartTime!=null && rosterStartTime.after(currentEntryTime)){
						if(rosterStartTime!=null && rosterStartTime.before(currentEntryTime)){
							
							if(rosterTo>current){
								
//								System.out.println(" =======  24 1 ======= ");
								dashboardClockEntryText1 = "You are clocked on for "+ hmServiceMap.get(nCurrServiceId+"")+", please clock off according to your roster";
							}else{
//								System.out.println(" =======  24 2 ======= ");
								dashboardClockEntryText1 = "Please CLOCK OFF now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
							}
						}else{
//							System.out.println(" =======  24 3 ======= ");
							dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
						}
						
						
						
						
						dashboardClockEntryText2 = "If this end time is not correct, please see your manager to update your roster";
					}else if(!isPrevIn && !isPrevOut && new CommonFunctions(CF).isCurrentRostered(con,CF,strEmpId, nCurrServiceId)){
//						log.debug(" =======  25  ======= ");
//						System.out.println(" =======  25  ======= ");
						dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
						dashboardClockEntryText2 = "If this start/end time is not correct, please see your manager to update your roster";
					}else if(!isIn && !isOut && !isPrevOut && nCurrServiceId>0){
//						log.debug(" =======  26  ======= ");
//						System.out.println(" =======  26  ======= ");
						dashboardClockEntryText1 = "Please CLOCK OFF now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
						dashboardClockEntryText2 = "If this end time is not correct, please see your manager to update your roster";
					} else if(!isIn && !isOut && !new CommonFunctions(CF).isCurrentRostered(con,CF,strEmpId, nCurrServiceId)){
//						log.debug(" =======  26  ======= ");
//						System.out.println(" =======  26A  ======= ");
						dashboardClockEntryText1 = "You are not rostered for today. Please CLOCK ON if you would like to manually clock on now anyway.";
						dashboardClockEntryText2 = "Please see your manager for any clarifications";
					} 
					
//					log.debug(" isIn="+isIn+" isOut="+isOut+" ");
					

				} else if (isIn) {

//					log.debug(" =======  27  ======= ");
//					System.out.println(" =======  27  ======= ");
					
					Time entryTime = uF.getTimeFormat(strInTime, CF.getStrReportTimeFormat());
					Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);

					long milliseconds1 = entryTime.getTime();
					long milliseconds3 = currentTime.getTime();

					double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));

//					log.debug("entryTime===>" + entryTime);
//					log.debug("currentTime===>" + currentTime);
//					log.debug("dbl===>" + dbl);

//					if (dbl > 0.01) {
//						setStrClock("Clock On");
//						setStrMode("OUT");
//						dashboardClockEntryText1 = "Please CLOCK OFF now to avoid being late";
//						dashboardClockEntryText2 = "If this end time is not correct, please see your manager to update your roster";
//					} else { // clocked on
//						setStrClock(null);
//						setStrMode(null);
//						dashboardClockEntryText1 = "You are clocked on into the system.";
//					}

					
					
					if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")){
						pst = con.prepareStatement(selectRosterClockDetails_N_IN);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
					}else{
						pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						
					}
					rs = pst.executeQuery();
					String _toCurr = null;
					while(rs.next()){
						_toCurr = rs.getString("_to");
					}
					rs.close();
					pst.close();
					
					long rosterTo = 0;
					long current = 0;
					if(_toCurr!=null){
						rosterTo = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+_toCurr, DBDATE+DBTIME).getTime();
						current = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME).getTime();
					}
					
//					log.debug("===3==== rosterTo=======>"+uF.getCurrentDate(CF.getStrTimeZone())+_toCurr+"====="+rosterTo);
//					log.debug("===3==== current=======>"+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone())+"======="+current);
					
					
					if(rosterTo>current){
						dashboardClockEntryText1 = "You are clocked on for "+ hmServiceMap.get(nCurrServiceId+"")+", please clock off according to your roster";
					}else{
						dashboardClockEntryText1 = "Please CLOCK OFF now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
					}
					
					
					
					dashboardClockEntryText2 = "If this start/end time is not correct, please see your manager to update your roster";
				}else if(isOut){
//					log.debug(" =======  28  ======= ");
//					System.out.println(" =======  28  ======= ");
					setStrClock("Clock Off");
					setStrMode("IN");
					dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
					dashboardClockEntryText2 = "If this start/end time is not correct, please see your manager to update your roster";
				}else if(!isIn && !isOut && !new CommonFunctions(CF).isCurrentRostered(con,strEmpId)){
//					log.debug(" =======  29  ======= ");
//					System.out.println(" =======  29  ======= ");
					setStrClock(null);
					setStrMode(null);
					dashboardClockEntryText1 = "If you think, you should have roster please see your manager";
//					dashboardClockEntryText2 = "If this start/end time is not correct, please see your manager to update your roster";
					dashboardClockEntryText2 = "";
				}else if(!isIn && !isOut && new CommonFunctions(CF).isCurrentRostered(con,strEmpId)){
//					log.debug(" =======  30  ======= ");
//					System.out.println(" =======  30  ======= ");
					setStrClock("Clock Off");
					setStrMode("IN");
					dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late for "+ hmServiceMap.get(nCurrServiceId+"");
					dashboardClockEntryText2 = "If this start/end time is not correct, please see your manager to update your roster";
				}
//			}
			
			
				
//				System.out.println(" =======  31  ======= ");
			
			/**
			 *  IS Roster Dependent
			 */
			
			
			
			pst = con.prepareStatement(selectRosterDependent);
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
			
			
			
			
//			boolean isIn = false;
//			boolean isOut = false;
			
			isIn = false;
			isOut = false;
			
			if(strEmpType!=null && !isRoster){
				
				
//				log.debug(" =======  2  ======= ");
//				System.out.println(" =======  31 0  ======= ");
				
				
				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(2, "IN");
				pst.setString(3, "OUT");
				pst.setInt(4, uF.parseToInt(strEmpId));
				pst.setInt(5, nCurrServiceId);
				rs = pst.executeQuery();
				while(rs.next()){
					
					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
						isIn = true;	
					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
						isOut = true;
					}
				}
				rs.close();
				pst.close();
				
				
				if(isIn && isOut){
					setStrClock(null);
					setStrMode(null);
					
				}else if(!isIn && !isOut){
					setStrClock("Clock Off");
					setStrMode("IN");
					dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late";
					dashboardClockEntryText2 = "If this start/end time is not correct, please see your manager to update your roster";
				}else if(isIn){
					setStrClock("Clock On");
					setStrMode("OUT");
					
					
					if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")){
						pst = con.prepareStatement(selectRosterClockDetails_N_IN);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
					}else{
						pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						
					}
					rs = pst.executeQuery();
					String _toCurr = null;
					while(rs.next()){
						_toCurr = rs.getString("_to");
					}
					rs.close();
					pst.close();
					
					
					long rosterTo = 0;
					long current = 0;
					if(_toCurr!=null){
						rosterTo = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+_toCurr, DBDATE+DBTIME).getTime();
						current = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME).getTime();
					}
					
//					log.debug("===2==== rosterTo=======>"+rosterTo);
//					log.debug("===2==== current=======>"+current);
					
					if(rosterTo>current){
						dashboardClockEntryText1 = "You are clocked on to the system";
					}else{
						dashboardClockEntryText1 = "Please CLOCK OFF before you leave for the day";
					}
					
					dashboardClockEntryText2 = "If this start/end time is not correct, please see your manager to update your roster";
				}else if(isOut){
					setStrClock("Clock Off");
					setStrMode("IN");
					dashboardClockEntryText1 = "Please CLOCK ON now to avoid being late";
					dashboardClockEntryText2 = "If this start/end time is not correct, please see your manager to update your roster";
				}
				
				
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}

	public void clockOnOff(String strMode) {

		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;

		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);

			Map hmTardy = CF.getTardyType(con);
			int nTardyIn = uF.parseToInt((String)hmTardy.get("TARDY_IN"));
			int nTardyOut = uF.parseToInt((String)hmTardy.get("TARDY_OUT"));
		   
			
			int nPrevServiceId = 0;
			int nCurrServiceId = 0;
			int nCount = 0;
			
			pst = con.prepareStatement(selectRoster_N_COUNT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				nCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(selectRosterClockDetails_PREV_N_OUT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				nPrevServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				nCurrServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
			
			
			if(nCurrServiceId==0){
				if(nCount>1){
					pst = con.prepareStatement(selectRosterClockDetails_N_IN);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
				}else{
					pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				}
				rs = pst.executeQuery();
				while(rs.next()){
					nCurrServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
			}
			
			
			if(nCurrServiceId==0){
				pst = con.prepareStatement(selectRosterClockDetails_N2_IN);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
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
			
//			log.debug(" Dashboard nServiceId===>"+nCurrServiceId);
			
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int DAY = cal.get(Calendar.DAY_OF_MONTH);

			String strPrevRosterDate = null;
			Time tPrevFrom = null;
			Time tPrevTo = null;
			
			String strRosterDate = "";
			String strFrom = "00:00:00";
			String strTo = "00:00:00";
			
//			log.debug("MODE===========>"+strMode);
			
			
			if (strMode != null && strMode.equalsIgnoreCase("IN")) {
//				pst = con.prepareStatement("SELECT * FROM attendance_details where emp_id =? and TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD')=? and in_out='IN'");
				pst = con.prepareStatement(selectAttendenceClockDetails_N);
				
				pst.setDate(1, uF.getDateFormat(YEAR + "" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "" + DAY, "yyyyMMdd"));
				pst.setString(2, "IN");
				pst.setInt(3, uF.parseToInt(strEmpId));
				pst.setInt(4, nCurrServiceId);
				rs = pst.executeQuery();
				while (rs.next()) {
					return;
				}
				rs.close();
				pst.close();

			} else if (strMode != null && strMode.equalsIgnoreCase("OUT")) {
				pst = con.prepareStatement(selectAttendenceClockDetails_N);
				pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
				pst.setString(2, strMode);
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
				
				if(!isPrevOut){
					if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")){
						pst = con.prepareStatement(selectRosterClockDetails_N_IN);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
						pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
					}else{
						pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
						
					}
					rs = pst.executeQuery();
					if(rs.next()){
						isPrevRoster = true;
						
						tPrevFrom = rs.getTime("_from");
						tPrevTo = rs.getTime("_to");
						strPrevRosterDate = rs.getString("_date");
					}
					rs.close();
					pst.close();

				}else{
					pst = con.prepareStatement(selectAttendenceClockDetails_N);
					pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(2, strMode);
					pst.setInt(3, uF.parseToInt(strEmpId));
					pst.setInt(4, nCurrServiceId);
					rs = pst.executeQuery();

					if(rs.next()){
						return;
					}
					rs.close();
					pst.close();
				}
			}

			
			
			
			
//			
//			pst = con.prepareStatement("SELECT * FROM roster_details WHERE emp_id = ? and _date = ?");
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate());
//			rs = pst.executeQuery();
//
//			log.debug("pst 1 ====" + pst);
//
//			String strRosterDate = "";
//			String strFrom = "00:00:00";
//			String strTo = "00:00:00";
//
//			while (rs.next()) {
//				strFrom = rs.getString("_from");
//				strTo = rs.getString("_to");
//				strRosterDate = rs.getString("_date");
//			}
//
//			rs.close();
//			pst.close();
			
			
			if(tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() < tPrevTo.getTime()){
				
				if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")){
					if(nCount>1){
						pst = con.prepareStatement(selectRosterClockDetails_N_IN);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
						pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
					}else{
						pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
					}
					
				}else{
					pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
					
				}
//				System.out.println("1 pst======>"+pst);
				rs = pst.executeQuery();

				strRosterDate = null;
				strFrom = null;
				strTo = null;

				while (rs.next()) {
					strFrom = rs.getString("_from");
					strTo = rs.getString("_to");
					strRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();
			}else if(tPrevFrom!=null && tPrevTo!=null &&  tPrevFrom.getTime() > tPrevTo.getTime()){
				if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")){
					pst = con.prepareStatement(selectRosterClockDetails_N_IN);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
					pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
				}else{
					pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
					
				}
//				System.out.println("2 pst======>"+pst);
				rs = pst.executeQuery();

				strRosterDate = null;
				strFrom = null;
				strTo = null;

				while (rs.next()) {
					strFrom = rs.getString("_from");
					strTo = rs.getString("_to");
					strRosterDate = rs.getString("_date");
				}

				rs.close();
				pst.close();
			}else{
				if(getStrMode()!=null && getStrMode().equalsIgnoreCase("IN")){
					if(nCount>1){
						pst = con.prepareStatement(selectRosterClockDetails_N_IN);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
					}else{
						pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					}
					
				}else{
					pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					
				}
//				System.out.println("3 pst======>"+pst);
				rs = pst.executeQuery();

				strRosterDate = null;
				strFrom = null;
				strTo = null;
				int nServiceIdTemp = 0;

				while (rs.next()) {
					strFrom = rs.getString("_from");
					strTo = rs.getString("_to");
					strRosterDate = rs.getString("_date");
					nServiceIdTemp = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
				
				
//				log.debug("nServiceIdTemp===>"+nServiceIdTemp);
				
				if(nServiceIdTemp==0){
					pst = con.prepareStatement(selectRosterClockDetails_N2_IN);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					
//					log.debug("pst nServiceIdTemp  ===>"+pst);
					
//					System.out.println("4 pst======>"+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						strFrom = rs.getString("_from");
						strTo = rs.getString("_to");
						strRosterDate = rs.getString("_date");
						nServiceIdTemp = rs.getInt("service_id");
					}
					rs.close();
					pst.close();
				}
			}
			
			
			
			
//			System.out.println("strFrom===>"+strFrom);
//			System.out.println("strTo===>"+strTo);
			
			
			
			if(strFrom!=null && strTo!=null){
				
//				log.debug("strRosterDate==>"+strRosterDate);
//				log.debug("strFrom==>"+strFrom);
//				log.debug("strTo==>"+strTo);
//				log.debug("uF.getCurrentTime()==>"+uF.getCurrentTime(CF.getStrTimeZone()));
				

				Time entryTime = uF.getTimeFormat(strRosterDate+strFrom, DBDATE+DBTIME);
				Time exitTime = uF.getTimeFormat(strRosterDate+strTo, DBDATE+DBTIME);
//				Time exitTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+strTo, DBDATE+DBTIME);
				
				Time currentTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE+DBTIME);

				long milliseconds1 = entryTime.getTime();
				long milliseconds2 = exitTime.getTime();
				long milliseconds3 = currentTime.getTime();
				long diff = 0L;

				int nIN = 0;
				if (strMode.equals("IN")) {
					nIN = 1;
					diff = milliseconds3 - milliseconds1;
				} else {
					
					if(entryTime.after(exitTime)){
						milliseconds2 += 60 * 60 * 24 * 1000; 	
					}
					
					diff = milliseconds3 - milliseconds2;
				}

				long diffSeconds = diff / 1000;
				long diffMinutes = diff / (60 * 1000);

				rs = null;
				
				String strOrgId = CF.getEmpOrgId(con, uF, strEmpId);
				String strWlocatoinId = CF.getEmpWlocationId(con, uF, strEmpId);
				
//				pst = con.prepareStatement("SELECT * FROM roster_policy WHERE mode = ? and time_value " + ((diffMinutes >= 0) ? ">" : "<") + "= ? and time_type!= 'TARDY' order by time_value desc LIMIT 1");
				pst = con.prepareStatement("SELECT * FROM roster_policy WHERE mode = ? and time_value " + ((diffMinutes >= 0) ? ">" : "<") + "= ? " +
						"and policy_status=1 and effective_date<=? and org_id=? and wlocation_id=? order by time_value "+((diffMinutes >= 0)?"":"desc")+" LIMIT 1");
				pst.setString(1, strMode);
				pst.setLong(2, diffMinutes);
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt(strOrgId));
				pst.setInt(5, uF.parseToInt(strWlocatoinId));
//				System.out.println("pst 2 ====" + pst);  
				rs = pst.executeQuery();  
				strMessage = null;
				if (diffMinutes!=0 && CF.isRosterDependency(con,strEmpId) && rs.next() && rs.getString("message")!=null && !rs.getString("message").trim().equals("") && rs.getString("message").length()>0) {
					strMessage = rs.getString("message");
					
					if(rs.getBoolean("isapproval")){
						setStrApproval("1");
					}
					
					log.debug(rs.getString("message"));

				} else {
					
					dashboardClockEntryText1="";
					
//					System.out.println("one ==== ClockOnOffEntry ===========>> ");
					
					ClockOnOffEntry cooe = new ClockOnOffEntry();
					cooe.setServletRequest(request);
					cooe.setStrMode(strMode);					
					cooe.execute();
					
					
				}
				rs.close();
				pst.close();
				
//				System.out.println("strMessage ====" + strMessage); 
//				System.out.println("getStrApproval ====" + getStrApproval()); 
				
			}else if(!new CommonFunctions(CF).isCurrentRostered(con,strEmpId) && CF.isRosterDependency(con,strEmpId)){
				strNotRoster="Please choose cost center to clock on.";
				serviceList = new FillServices(request).fillServices(strEmpId);
			}else if(!new CommonFunctions(CF).isCurrentRostered(con,strEmpId) && !CF.isRosterDependency(con,strEmpId)){
				strNotRosterY="Please choose cost center to clock on.";
				serviceList = new FillServices(request).fillServices(strEmpId);
			}else{
				dashboardClockEntryText1="";
				
//				System.out.println("two ==== ClockOnOffEntry ===========>> ");
				
				ClockOnOffEntry cooe = new ClockOnOffEntry();
				cooe.setServletRequest(request);
				cooe.setStrMode(strMode);
				cooe.execute();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

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

	public String getDashboardClockEntryText2() {
		return dashboardClockEntryText2;
	}

	public void setDashboardClockEntryText2(String dashboardClockEntryText2) {
		this.dashboardClockEntryText2 = dashboardClockEntryText2;
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

}
