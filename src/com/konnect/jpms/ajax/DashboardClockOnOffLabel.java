package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
  
public class DashboardClockOnOffLabel extends ActionSupport implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7749319646589345398L;
	private String dashboardClockLabel;
	private String strClock;
	private String strMode;
	
	String strEmpId = null; 
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(DashboardClockOnOffLabel.class);
	
	public String execute() throws Exception {

		System.out.println("strEmpId ======DASH===>"+getStrEmpId() );
		HttpSession session = request.getSession();
		 
		if(getStrEmpId()!=null){
			strEmpId = getStrEmpId();
		}else{
			strEmpId = (String)session.getAttribute("EMPID");
		}
		 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} 
		
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		
		try {
			
			UtilityFunctions uF = new UtilityFunctions();
			
			con = db.makeConnection(con);
			
			
			
			int nPrevServiceId = 0;
			int nCurrServiceId = 0;
			int nCount = 0;
			
			
			pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			
//			System.out.println("pst ===> "+pst);
			
			Time rosterStartTime = null;
//			Time currentEntryTime = uF.getCurrentTime(CF.getStrTimeZone());
			Time currentEntryTime = uF.getTimeFormat(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME);
			while(rs.next()) {
//				rosterStartTime = rs.getTime("_from");
				rosterStartTime = uF.getTimeFormat(rs.getDate("_date")+""+rs.getTime("_from"), DBDATE+DBTIME);
			}
			rs.close();
			pst.close();
//			System.out.println("rosterStartTime ===>> " + rosterStartTime + " -- currentEntryTime ===>> " + currentEntryTime);
			
			pst = con.prepareStatement(selectRoster_N_COUNT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				nCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
//			System.out.println("nCount ===>> " + nCount);
			
			
			pst = con.prepareStatement(selectRosterClockDetails_PREV_N_OUT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//			System.out.println("===>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				nPrevServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
//			System.out.println("nPrevServiceId===>"+nPrevServiceId);
			
			pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				nCurrServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
//			System.out.println("nCurrServiceId===>"+nCurrServiceId);
			
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
//				System.out.println("in 1 0 pst===>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					nCurrServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
//				System.out.println("in 1 0 nCurrServiceId ===> "+nCurrServiceId);
			}
			
			if(nCurrServiceId==0){
				pst = con.prepareStatement(selectRosterClockDetails_N2_IN);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("in 2 0 nCurrServiceId ===> "+nCurrServiceId +" -- pst ===>> " + pst);
				rs = pst.executeQuery();
				while(rs.next()){
					nCurrServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
//				System.out.println("in 2 0 nCurrServiceId ===> "+nCurrServiceId);
			}
			
			
			if(nCurrServiceId==0) {
				nCurrServiceId = nPrevServiceId;
			}
			
//			log.debug(" clock OFF nPrevServiceId===>"+nPrevServiceId);
//			log.debug(" clock OFF nCurrServiceId===>"+nCurrServiceId);
			
			
//			pst = con.prepareStatement(selectRosterClockDetails1);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate());
//			pst.setDate(3, uF.getPrevDate());
//			
//			log.debug("selectRosterClockDetails1="+pst);
//			
//			rs = pst.executeQuery();
//			boolean isRoster = false;
//			while (rs.next()) {
//				isRoster = true;
//			}
			
			
//			if (!new CommonFunctions(CF).isRostered(strEmpId)) {
//				setStrClock(null);
//				setStrMode(null);
//				dashboardClockLabel="Not Rostered!";
//			}else{
				
//				pst = con.prepareStatement(selectAttendenceClockDetails);
				pst = con.prepareStatement(selectAttendenceClockDetails_N);
			
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(2, "IN");
				pst.setInt(3, uF.parseToInt(strEmpId));
				pst.setInt(4, nCurrServiceId);
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				boolean isPrevInOut = false;
				if(rs.next()) {
					
					Time entryTime = uF.getTimeFormat(uF.getDateFormat(rs.getString("in_out_timestamp"),DBTIMESTAMP, DBTIME), DBTIME);
					Time currentTime = uF.getTimeFormat(uF.getCurrentTime(CF.getStrTimeZone()) + "", DBTIME);
					
					long milliseconds1 = entryTime.getTime();
					long milliseconds3 = currentTime.getTime();
					
					double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
					
//					log.debug("entryTime===>"+entryTime);
//					log.debug("currentTime===>"+currentTime);
//					log.debug("dbl===>"+dbl);
//					System.out.println("entryTime ===> " + entryTime + " -- currentTime ===> " + currentTime + " -- dbl ===> " + dbl);
					
					if(dbl>0.01) {
						setStrClock("Clock On");
						setStrMode("OUT");
						dashboardClockLabel="Clock Off";
						
					}else{
						setStrClock("Clock On");
						setStrMode("OUT");
						dashboardClockLabel="Clocked On";
						
					}
					
				} else {

					pst1 = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
					pst1.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
					pst1.setString(2, "IN");
					pst1.setString(3, "OUT");
					pst1.setInt(4, uF.parseToInt(strEmpId));
					pst1.setInt(5, nPrevServiceId);
//					System.out.println("selectAttendenceClockDetailsInOut===>"+pst);
					rs1 = pst1.executeQuery();
					boolean isPrevIn = false;
					boolean isPrevOut = false;
					while(rs1.next()){
						if(rs1.getString("in_out").equalsIgnoreCase("IN")){
							isPrevIn = true;
						}else if(rs1.getString("in_out").equalsIgnoreCase("OUT")){
							isPrevOut = true;
						} 
					}
					rs1.close();
					pst1.close();
					
					
					if(isPrevOut){
						isPrevInOut = true;
					}
//					System.out.println("isPrevIn =======>> "+isPrevIn + " -- isPrevInOut ===>> "+ isPrevInOut+" ===========");
					
					if(isPrevIn && isPrevOut && new CommonFunctions(CF).isCurrentRostered(con,strEmpId)){
						
//						System.out.println("======= 1 ===========");
						
						setStrClock("Clock Off");
						setStrMode("IN");
						dashboardClockLabel="Clock On";
					} else if(isPrevIn && !isPrevOut && new CommonFunctions(CF).isCurrentRostered(con,strEmpId) && rosterStartTime!=null && rosterStartTime.before(currentEntryTime)) {
						
//						System.out.println("======= 2 ===========");
						
//						System.out.println("rosterStartTime="+rosterStartTime);
//						System.out.println("currentEntryTime="+currentEntryTime);
						
						
						isPrevInOut = true;
						setStrClock("Clock On");
						setStrMode("OUT");
						dashboardClockLabel="Clock Off";
					}else if(isPrevIn && !isPrevOut && new CommonFunctions(CF).isCurrentRostered(con,strEmpId) && rosterStartTime!=null && !rosterStartTime.before(currentEntryTime)){
						
//						System.out.println("======= 2 A ===========");
						
						isPrevInOut = true;
						setStrClock("Clock Off");
						setStrMode("OUT");
						dashboardClockLabel="Clock On";
						
					} else if(!isPrevIn && !isPrevOut) {
						
//						System.out.println("======= 3 ===========");
						
						setStrClock("Clock Off");
						setStrMode("IN");
						dashboardClockLabel="Clock On";					
					} else {
						
//						System.out.println("======= 4 ===========");
						
						setStrClock(null);
						setStrMode(null);
						dashboardClockLabel="Not Rostered";
					}
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(2, "IN");
				pst.setString(3, "OUT");
				pst.setInt(4, uF.parseToInt(strEmpId));
				pst.setInt(5, nCurrServiceId);
//				System.out.println("1 pst=====>"+pst);
				rs = pst.executeQuery();
				boolean isIn = false;
				boolean isOut = false;
				int nLastService = 0;
				while(rs.next()){
					
					if(rs.getString("in_out").equalsIgnoreCase("IN")){
						isIn = true;
					}else if(rs.getString("in_out").equalsIgnoreCase("OUT")){
						isOut = true;
						nLastService = rs.getInt("service_id");
					} 
				}
				rs.close();
				pst.close();
				
//				System.out.println("isIn==>"+isIn);
//				System.out.println("isOut==>"+isOut);
//				System.out.println("isPrevInOut==>"+isPrevInOut);
//				System.out.println("isIn==>"+isIn);
				
				
//				if(isIn && isOut && CF.isCurrentRostered(con,CF,strEmpId, nCurrServiceId)){
				if(!isIn && !isOut && new CommonFunctions(CF).isCurrentRostered(con,CF,strEmpId, nCurrServiceId)){
					setStrClock("Clock Off");
					setStrMode("IN");
					dashboardClockLabel="Clock On";
					
//					System.out.println("======= 5 ===========");
					
				} else if(isIn && isOut) {
					setStrClock(null);
					setStrMode(null);
					dashboardClockLabel="Finished!";
					
//					System.out.println("======= 6 ===========");
					
//				} else if(isIn) {
				} else if(isIn && !isOut) {
					setStrClock("Clock On");
					setStrMode("OUT");
					dashboardClockLabel="Clock Off";
					
//					System.out.println("======= 7 ===========");
					
					
//				}else if(isOut){
				} else if(!isIn && isOut) {
					setStrClock("Clock Off");
					setStrMode("IN");
					dashboardClockLabel="Clock On";
					
//					System.out.println("======= 8 ===========");
					
				} else if(!isIn && !isOut && !isPrevInOut && !new CommonFunctions(CF).isCurrentRostered(con,CF,strEmpId, nCurrServiceId)) { 
					/*setStrClock("Clock On");
					setStrMode("IN");
					dashboardClockLabel="Clock Off";*/
					
					setStrClock("Clock Off");
					setStrMode("IN");
					dashboardClockLabel="Clock On";
					
//					System.out.println("======= "+isPrevInOut+" ===========");
					
//					System.out.println("======= 9 ===========");
				}else if(!isIn && !isOut && !isPrevInOut) {
					setStrClock("Clock Off");
					setStrMode("IN");
					dashboardClockLabel="Clock On";
					
//					System.out.println("======= 10 ===========");
					
				}
//			}
			
			
			
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
//			System.out.println("strEmpType ===>> " + strEmpType + " -- isRoster ===>> " + isRoster);
			
			
			
//			boolean isIn = false;
//			boolean isOut = false;
			
			isIn = false;
			isOut = false;
			
			if(strEmpType!=null && !isRoster) {
				
				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(2, "IN");
				pst.setString(3, "OUT");
				pst.setInt(4, uF.parseToInt(strEmpId));
				pst.setInt(5, nCurrServiceId);
//				System.out.println("2 pst=====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					
					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
						isIn = true;	
					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
						isOut = true;
					}
				}
				rs.close();
				pst.close();
//				System.out.println("!isRoster isIn ===>> " + isIn + " -- isOut ===>> " + isOut);
				
				if(isIn && isOut && new CommonFunctions(CF).isCurrentRostered(con,CF,strEmpId, 2)) {
					setStrClock("Clock Off");
					setStrMode("IN");
					dashboardClockLabel="Clock On";
				} else if(isIn && isOut) {
					setStrClock(null);
					setStrMode(null);
					dashboardClockLabel="Finished!";
				} else if(!isIn && !isOut) {
					setStrClock("Clock Off");
					setStrMode("IN");
					dashboardClockLabel="Clock On";
//				}else if(isIn) {
				}else if(isIn && !isOut) {
					setStrClock("Clock On");
					setStrMode("OUT");
					dashboardClockLabel="Clock Off";
//				}else if(isOut){
				} else if(!isIn && isOut) {
					setStrClock("Clock Off");
					setStrMode("IN");
					dashboardClockLabel="Clock On";
				}
//				System.out.println("dashboardClockLabel ===>> " + dashboardClockLabel);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
		
		return SUCCESS;
	}
	

	public String getStrClock() {
		return strClock;
	}

	public void setStrClock(String strClock) {
		this.strClock = strClock;
	}

	public String getDashboardClockLabel() {
		return dashboardClockLabel;
	}

	public void setDashboardClockLabel(String dashboardClockLabel) {
		this.dashboardClockLabel = dashboardClockLabel;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrMode() {
		return strMode;
	}

	public void setStrMode(String strMode) {
		this.strMode = strMode;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

}
