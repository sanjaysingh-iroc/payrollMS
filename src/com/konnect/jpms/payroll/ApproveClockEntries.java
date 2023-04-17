package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApproveClockEntries extends ActionSupport implements ServletRequestAware, IStatements {

	/** 
	 *   
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpID;
	String strUserType;

	String strFrmD1 = null;
	String strFrmD2 = null;
	String strD1 = null;
	String strD2 = null;
	String strPC = null;

	String strAlpha = null;

	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(ApproveClockEntries.class);
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);

		String APPROVE = (String) request.getParameter("approve");
		strAlpha = (String) request.getParameter("alphaValue");
		setStrAlpha(strAlpha);

		request.setAttribute(TITLE, TViewClock);
		strEmpID = (String) request.getParameter("EMPID");
		String strEmpType = (String) session.getAttribute("USERTYPE");
		String[] strPayCycleDates = null;

		if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
		} else {
			strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
		}

		strD1 = strPayCycleDates[0];
		strD2 = strPayCycleDates[1];
		
		
		
		
		
		
		strPC = (String) request.getParameter("PC");

		String referer = request.getHeader("Referer");

		if (referer != null) {
			int index1 = referer.indexOf(request.getContextPath());
			int index2 = request.getContextPath().length();
			referer = referer.substring(index1 + index2 + 1);
		}
		setRedirectUrl(referer);

		if (APPROVE != null) {

			request.setAttribute(PAGE, PReportClockManager);
			request.setAttribute(MESSAGE, "Payroll generated");
			approvePayrollEntries();
			updatePayrollDetails();

			
			com.konnect.jpms.export.PaySlips p = new com.konnect.jpms.export.PaySlips();
			p.setServletRequest(request);
			p.execute(getDtMin(), getDtMax());

			return SUCCESS;
		}
 
		if (strEmpType != null && (strEmpType.equalsIgnoreCase(ADMIN) || 
				strEmpType.equalsIgnoreCase(CEO) || strEmpType.equalsIgnoreCase(ACCOUNTANT) |
				strEmpType.equalsIgnoreCase(CFO) || strEmpType.equalsIgnoreCase(HRMANAGER) )) {
			request.setAttribute(PAGE, PApproveClockEntries);
			strEmpID = (String) session.getAttribute(EMPID);
			viewClockEntriesForPayrollApproval();
		}else{
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		
		
//		
//		if (strEmpID != null) {
//			request.setAttribute(PAGE, PReportClock);
//			viewClockEntries();
//		} else if (strEmpType != null && strEmpType.equalsIgnoreCase(ADMIN)) {
//			request.setAttribute(PAGE, PApproveClockEntries);
//			strEmpID = (String) session.getAttribute("EMPID");
//			viewClockEntriesForPayrollApproval();
//		} else if (strEmpType != null && strEmpType.equalsIgnoreCase(MANAGER)) {
//			request.setAttribute(PAGE, PReportClockManager);
//			strEmpID = (String) session.getAttribute("EMPID");
//			viewClockEntriesForPayrollApproval();
//
//		} else if (strEmpType != null && strEmpType.equalsIgnoreCase(EMPLOYEE)) {
//			request.setAttribute(PAGE, PReportClock);
//			strEmpID = (String) session.getAttribute("EMPID");
//			viewClockEntries();
//		} else {
//			request.setAttribute(PAGE, PReportClock);
//			strEmpID = (String) session.getAttribute("EMPID");
//			viewClockEntries();
//		}

		return loadClockEntries();

	}

	
	public String loadClockEntries() {
		paycycleList = new FillPayCycles(request).fillPayCycles(CF);
		return LOAD;
	}

//	List alWorkedDates = new ArrayList();
	Map hmEmpRosterLunchDeduction = new HashMap();
	Map<String, Map<String, String>> hmLeavesMap = null;
	Map<String, String> hmLeaves = null;
	
	public String approvePayrollEntries() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);

			List<String> _alDay = new ArrayList<String>();
			List<String> _alDate = new ArrayList<String>();

			Map<String, String> hmHolidays = new CommonFunctions(CF).getHolidayList(con,request);
//			Map<String, Map<String, String>> hmDailyRates = new CommonFunctions().getDailyRates(con);
			Map<String, String> hmLunchDeductionService = CF.getLunchDeductionServiceDetails(con);
			
			String[] strPayCycleDates = null;

			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");

			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
			}

			strPayCycleDates[0] = getDtMin();
			strPayCycleDates[1] = getDtMax();
 
//			hmLeavesMap = CF.getLeaveDates(con,strPayCycleDates[0], strPayCycleDates[1], CF, null, false, null);
			hmLeavesMap = CF.getActualLeaveDates(con, CF, uF, strPayCycleDates[0], strPayCycleDates[1], null, false, null);
			
			
			Map hmRosterLunchDeduction = new HashMap();
			pst = con.prepareStatement(selectRosterDetails11);
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], CF.getStrReportDateFormat()));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], CF.getStrReportDateFormat()));
			rs = pst.executeQuery();

			String strEmpIdNew = null;
			String strEmpIdOld = null;
			
			while (rs.next()) {
				String strServiceId = rs.getString("service_id");
				strEmpIdNew = rs.getString("emp_id");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					hmRosterLunchDeduction = new HashMap();
				}
				
				hmRosterLunchDeduction.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"_"+strServiceId, rs.getString("is_lunch_ded"));
				hmRosterLunchDeduction.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), "");
				hmEmpRosterLunchDeduction.put(strEmpIdNew, hmRosterLunchDeduction);
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			
//			log.debug("Start ===> " + strPayCycleDates[0]);
//			log.debug("End ===> " + strPayCycleDates[1]);

			if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) {
				pst = con.prepareStatement(selectClockEntriesAdminR_N);
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], CF.getStrReportDateFormat()));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], CF.getStrReportDateFormat()));
			} else {
				pst = con.prepareStatement(selectClockEntriesManagerR);
				pst.setInt(1, uF.parseToInt(strEmpID));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], CF.getStrReportDateFormat()));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], CF.getStrReportDateFormat()));
			}

			rs = pst.executeQuery();

//			log.debug("selectClockEntriesAdminR ===> " + pst);

			long _IN = 0L;
			long _OUT = 0L;
			double _TOTAL = 0.d;
			boolean isIn = false;
			boolean isOut = false;
			boolean isInOut = false;

			Map hmData = new HashMap();
			Map hm = new HashMap();

			String strOldEmpId = null;
			String strNewEmpId = null;
			String strOldDate = null;
			String strNewDate = null;
			String strServiceIdNew = null;
			String strServiceIdOld = null;
			
			Map<String, Map<String, String>> hmManagerAttendenceReport = new HashMap<String, Map<String, String>>();
			Map<String, String> hmPayMode = new HashMap<String, String>();

			Map<String, String> hmWorkedHours = new HashMap<String, String>();
			Map<String, String> hmLunchDeduction = new HashMap<String, String>();
			new CommonFunctions(CF).getDeductionTime(con,hmLunchDeduction);

			List alServiceId = new ArrayList();
			List alDates = new ArrayList();
			
			while (rs.next()) {
				String strInOut = rs.getString("in_out");
				strNewEmpId = rs.getString("emp_id");
				strServiceIdNew = rs.getString("service_id");

//				log.debug("strOldEmpId========>" + strOldEmpId + "<=====strNewEmpId====>" + strNewEmpId);

				strNewDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT);
				String strTime = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, TIME_FORMAT);

				
				
				if (strNewEmpId != null && !strNewEmpId.equalsIgnoreCase(strOldEmpId)) {
//					log.debug("hm reset====>");
				
				
					hm = new HashMap();
					isIn = false;
					isOut = false;
				}

				if (strNewDate != null && !strNewDate.equalsIgnoreCase(strOldDate)) {
//					log.debug("in out reset====>");
					isIn = false;
					isOut = false;

				} else if (strServiceIdNew != null && !strServiceIdNew.equalsIgnoreCase(strServiceIdOld)) {
//					log.debug("in out reset====>");
					isIn = false;
					isOut = false;

				}
				

				if (strInOut != null && strInOut.equalsIgnoreCase("IN") && !isIn) {
					_IN = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					isIn = true;
				} else if (strInOut != null && strInOut.equalsIgnoreCase("OUT") && !isOut) {
					_OUT = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					isOut = true;
				}

				// if (_OUT > _IN && isIn && isOut) {
				if (isIn && isOut) {

					Map hmInner = new HashMap();
					hmInner.put("IN", _IN + "");
					hmInner.put("OUT", _OUT + "");
					
					
					if(!alDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))){
						alDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
					}
					
					
					
					alServiceId = (List)hm.get("SERVICE_ID");
					if(alServiceId==null){
						alServiceId = new ArrayList();
					}
					
					if(!alServiceId.contains(rs.getString("service_id"))){
						alServiceId.add(rs.getString("service_id"));
					}
					
					
					hmInner.put("SERVICE_ID", strServiceIdNew);
					hm.put("SERVICE_ID", alServiceId);

					/**
					 * Lunch Time Deduction Calculation
					 */
					double dblHoursWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(_IN, _OUT));
					double dblLunchTime = uF.parseToDouble(hmLunchDeduction.get(O_LUNCH_DEDUCT_TIME));
					double dblLunch = uF.parseToDouble(hmLunchDeduction.get(O_LUNCH_DEDUCT));
					
					hmRosterLunchDeduction = (Map)hmEmpRosterLunchDeduction.get(strNewEmpId);
					if(hmRosterLunchDeduction==null){hmRosterLunchDeduction=new HashMap();}
					boolean isLunchDeductionService = uF.parseToBoolean((String)hmLunchDeductionService.get(strServiceIdNew));
					boolean isLunchDeduct = uF.parseToBoolean((String)hmRosterLunchDeduction.get(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_"+strServiceIdNew));
					
					
					if (dblHoursWorked >= dblLunchTime && isLunchDeduct && isLunchDeductionService){
						dblHoursWorked = dblHoursWorked - dblLunch;
					}

					hmInner.put("DIFF", dblHoursWorked + "");

					if (hmHolidays != null && hmHolidays.containsKey(strNewDate)) {
						hmInner.put("HOLIDAY", "TRUE");
					}

//					log.debug("_IN====>" + _IN + "=====_OUT====>" + _OUT);

					hm.put(strNewDate, alServiceId);
					hm.put(strNewDate+"_"+strServiceIdNew, hmInner);
					
//					hmData.put(strNewEmpId, hm);

				}
				
				hmData.put(strNewEmpId, hm);

				strOldEmpId = strNewEmpId;
				strOldDate = strNewDate;
				strServiceIdOld = strServiceIdNew;

			}
			rs.close();
			pst.close();

//			Map<String, String> hmHolidayDates = new HashMap();
//			Map<String, String> hmHolidayMap = new HashMap();
//			 
//			CF.getHolidayList(con,strPayCycleDates[0], strPayCycleDates[1], CF, hmHolidayDates, hmHolidayMap, false);
			Map hmEmpTypeMap = CF.getEmpTypeMap(con);

			Map hmPayrollFT = new HashMap();
			Map hmPayrollPT = new HashMap();
			CF.getDailyRates(con,hmPayrollFT, hmPayrollPT);
			
			
//			log.debug("hmData=" + hmData);

			pst = con.prepareStatement(selectSettings);
			rs = pst.executeQuery();
			double hrs = 0;
			while (rs.next()) {
				if (rs.getString("options").equalsIgnoreCase(O_STANDARD_FULL_TIME_HOURS)) {
					hrs = uF.parseToDouble(rs.getString("value"));
				}
			}
			rs.close();
			pst.close();

//			Map hmEmpPayMode = new CommonFunctions().getEmpPayMode();
			
			
//			pst = con.prepareStatement(insertPayroll);
			Set set = hmData.keySet();
			Iterator it = set.iterator();

			while (it.hasNext()) {	// Employees Loop

				String strEmpId = (String) it.next();
				String arr[] = getChbox();
				int x = ArrayUtils.contains(arr, strEmpId);
				if(x<0)continue;

				
//				log.debug(strEmpId+"  hmLeavesMap===>"+hmLeavesMap);
				
				hmLeaves = hmLeavesMap.get(strEmpId);
				if(hmLeaves==null){
					hmLeaves = new HashMap<String, String>();
				}
				
				Map hmInner = (HashMap) hmData.get(strEmpId);
				Set setInner = hmInner.keySet();
				Iterator itInner = setInner.iterator();
				
				//while (itInner.hasNext()) {			// Dates Loop
				for(int count=0; count<alDates.size(); count++){

//					String strDate = (String) itInner.next();
					String strDate = (String) alDates.get(count);
					
					
//					log.debug(strDate+"  hmInner ====>"+hmInner );
					
					List alServiceTemp = (List) hmInner.get(strDate);
					if(alServiceTemp==null){
						alServiceTemp = new ArrayList();
					}
//					alWorkedDates.add(strDate);
					
							
					for(int i=0; i<alServiceTemp.size(); i++){		// Services Loop
						String strServiceId = (String)alServiceTemp.get(i);
						
						

						
						Map hmEmpHourlyData = (HashMap) hmInner.get(strDate+"_"+strServiceId);
						if(hmEmpHourlyData==null){
							hmEmpHourlyData = new HashMap();
						}
						
						int nServiceId = uF.parseToInt((String)hmEmpHourlyData.get("SERVICE_ID"));
						 Map hmRates = (Map) hmPayrollFT.get("D" + strEmpId + "S" + ((nServiceId>0)?nServiceId:""));
						 if(hmRates==null){
							 hmRates = new HashMap();
						 }
						 String strPayMode = (String)hmRates.get("PAYMODE");
						
						
						if (x >= 0 && strPayMode != null && strPayMode.length() > 0 && nServiceId>0 && hmLeaves!=null && !hmLeaves.containsKey(strDate)) {
							pst = con.prepareStatement(insertPayroll);
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
							pst.setDate(4, uF.getDateFormat(strDate, CF.getStrReportDateFormat()));
							pst.setBoolean(5, ((x < 0) ? false : true));
							pst.setTime(6, uF.getTimeFormat(((String) hmEmpHourlyData.get("IN"))));
							pst.setTime(7, uF.getTimeFormat(((String) hmEmpHourlyData.get("OUT"))));
							pst.setDouble(8, uF.parseToDouble(((String) hmEmpHourlyData.get("DIFF"))));
							pst.setDate(9, uF.getDateFormat(strPayCycleDates[0], CF.getStrReportDateFormat()));
							pst.setDate(10, uF.getDateFormat(strPayCycleDates[1], CF.getStrReportDateFormat()));
							pst.setString(11, strPayMode);
							pst.setInt(12, nServiceId);

							pst.execute();
							pst.close();
//							pst.clearParameters();

						}
						
					}
										
				}

				String strEmpType = (String) hmEmpTypeMap.get(strEmpId);

//				log.debug("alDates=" + alDates);

				
				
				
				Set setLeaves = hmLeaves.keySet();
				Iterator itLeaves = setLeaves.iterator();
				
				while(itLeaves.hasNext()){
					String strDate = (String) itLeaves.next();

					Map hmEmpHourlyData = (HashMap) hmInner.get(strDate+"_");
					if(hmEmpHourlyData==null){
						hmEmpHourlyData = new HashMap();
					}
					int nServiceId = uF.parseToInt((String)hmEmpHourlyData.get("SERVICE_ID"));
					
					Map  hmRates = (Map) hmPayrollFT.get("D" + strEmpId + "S" + ((nServiceId>0)?nServiceId:""));
					if(hmRates==null){
						 hmRates = new HashMap();
					 }
					String strPayMode = (String)hmRates.get("PAYMODE");

					if (x >= 0 ) {
						pst = con.prepareStatement(insertPayroll);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
						pst.setDate(4, uF.getDateFormat(strDate, CF.getStrReportDateFormat()));
						pst.setBoolean(5, ((x < 0) ? false : true));
						pst.setTime(6, null);
						pst.setTime(7, null);
//						pst.setDouble(8, hrs);
						pst.setDouble(8, LeaveHours); // Leave Hours are different from standard Holiday hours
						pst.setDate(9, uF.getDateFormat(strPayCycleDates[0], CF.getStrReportDateFormat()));
						pst.setDate(10, uF.getDateFormat(strPayCycleDates[1], CF.getStrReportDateFormat()));
						pst.setString(11, strPayMode);
						pst.setInt(12, nServiceId);
						
						pst.execute();
						pst.close();
//						pst.clearParameters();
						
					}

				}
				
				
				Map hmRosterTemp = (Map)hmEmpRosterLunchDeduction.get(strEmpId);
				
				Set setK = hmHolidays.keySet();
				Iterator itK = setK.iterator();
				
				while(itK.hasNext()){
					String strDate = (String) itK.next();

					Map hmEmpHourlyData = (HashMap) hmInner.get(strDate+"_");
					if(hmEmpHourlyData==null){
						hmEmpHourlyData = new HashMap();
					}
					int nServiceId = uF.parseToInt((String)hmEmpHourlyData.get("SERVICE_ID"));
					
					Map  hmRates = (Map) hmPayrollFT.get("D" + strEmpId + "S" + ((nServiceId>0)?nServiceId:""));
					if(hmRates==null){
						 hmRates = new HashMap();
					 }
					String strPayMode = (String)hmRates.get("PAYMODE");

					if (x >= 0 && hmHolidays != null && hmHolidays.containsKey(strDate) && hmLeaves!=null && !hmLeaves.containsKey(strDate) && !hmRosterTemp.containsKey(strDate) && strEmpType != null && strEmpType.equalsIgnoreCase("FT")) {

						pst = con.prepareStatement(insertPayroll);
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
						pst.setDate(4, uF.getDateFormat(strDate, CF.getStrReportDateFormat()));
						pst.setBoolean(5, ((x < 0) ? false : true));
						pst.setTime(6, null);
						pst.setTime(7, null);
						pst.setDouble(8, hrs);
						pst.setDate(9, uF.getDateFormat(strPayCycleDates[0], CF.getStrReportDateFormat()));
						pst.setDate(10, uF.getDateFormat(strPayCycleDates[1], CF.getStrReportDateFormat()));
						pst.setString(11, strPayMode);
						pst.setInt(12, nServiceId);
						
						pst.execute();
						pst.close();
//						pst.clearParameters();
						
						

					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String viewClockEntries() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			Map<String, String> hm = new HashMap<String, String>();

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement(selectEmployeeV);
			pst.setInt(1, uF.parseToInt(strEmpID));
			rs = pst.executeQuery();
			String strEmpId = null;
			if (rs.next()) {

				strEmpId = rs.getString("emp_off_id");

				hm.put("EMPCODE", rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hm.put("NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hm.put("CITY", rs.getString("city_name"));
				hm.put("STATE", rs.getString("state_name"));
				hm.put("COUNTRY", rs.getString("country_name"));
				hm.put("PINCODE", rs.getString("emp_pincode"));
				hm.put("CONTACT", rs.getString("emp_contactno"));
				hm.put("IMAGE", rs.getString("emp_image"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(selectEmployeeR2V1);
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();

			if (rs.next()) {

				hm.put("M_EMPCODE", rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hm.put("M_NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hm.put("M_CONTACT", rs.getString("emp_contactno"));
			}
			rs.close();
			pst.close();

			List<String> _alDay = new ArrayList<String>();
			List<String> _alDate = new ArrayList<String>();

			pst = con.prepareStatement(selectDatesDesc);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while (rs.next()) {
				_alDate.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
				_alDay.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()));
			}
			rs.close();
			pst.close();

			List<String> alDay = new ArrayList<String>();
			List<String> alDate = new ArrayList<String>();
			List<String> alInOut = new ArrayList<String>();

			Map<String, String> hmHours = new HashMap<String, String>();
			Map<String, String> hmStart = new HashMap<String, String>();
			Map<String, String> hmEnd = new HashMap<String, String>();

			pst = con.prepareStatement(selectClockEntries);
			pst.setInt(1, uF.parseToInt(strEmpID));
			pst.setDate(2, uF.getDateFormat((String) _alDate.get(6), CF.getStrReportDateFormat()));
			pst.setDate(3, uF.getDateFormat((String) _alDate.get(0), CF.getStrReportDateFormat()));

			rs = pst.executeQuery();

			long _IN = 0L;
			long _OUT = 0L;
			long _TOTAL = 0L;
			boolean isOut = false;
			boolean isIn = false;
			boolean isInOut = false;

			while (rs.next()) {
				String str = rs.getString("in_out");

				if (!alDate.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
					alDate.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
					alDay.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDayFormat()));
					_IN = 0L;
					_OUT = 0L;
					isIn = false;
					isOut = false;
					isInOut = false;
				}

				if (str != null && str.equalsIgnoreCase("IN") && !isIn) {
					_IN = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();

					hmStart.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()), uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, "hh:mm a"));

					isIn = true;
				} else if (str != null && str.equalsIgnoreCase("OUT") && !isOut) {
					_OUT = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();

					hmEnd.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()), uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, "hh:mm a"));

					isOut = true;
				}

				if (_IN > 0 && _OUT > 0 && !isInOut) {

					hmHours.put(uF.getDateFormat(rs.getString("in_out_timestamp"), "yyyy-MM-dd HH:mm:ss", CF.getStrReportDateFormat()), uF.getTimeDiffInHoursMins(_IN, _OUT));

					_TOTAL += uF.getTimeDifference(_IN, _OUT);

					// _TOTAL += (((_OUT - _IN) > 0) ? (_OUT - _IN) : 0);

					isInOut = true;
				}

			}
			rs.close();
			pst.close();

			request.setAttribute("alInOut", alInOut);
			request.setAttribute("alDate", _alDate);
			request.setAttribute("alDay", _alDay);
			request.setAttribute("hmHours", hmHours);
			request.setAttribute("hmStart", hmStart);
			request.setAttribute("hmEnd", hmEnd);
			request.setAttribute("TOTAL", uF.getTimeDiffInHoursMins(0, _TOTAL));
			request.setAttribute("hmEmpData", hm);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String viewClockEntriesForPayrollApproval() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			

			List<String> _alDay = new ArrayList<String>();
			List<String> _alDate = new ArrayList<String>();

			con = db.makeConnection(con);
		
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmHolidays = CF.getHolidayList(con,request);
//			Map<String, Map<String, String>> hmLeavesMap = new CommonFunctions(CF).getLeaveDates(strD1, strD2);
//			hmLeavesMap = CF.getLeaveDates(con,strD1, strD2, CF, null, false, null);
			hmLeavesMap = CF.getActualLeaveDates(con, CF, uF, strD1, strD2, null, false, null);
			
			List<String> _alHolidays = new ArrayList<String>();
			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
			Map<String, String> hmEmpType = CF.getEmpTypeMap(con);
			Map<String, String> hmServices = CF.getServicesMap(con,true);

			Map<String, String> hmLunchDeduction = new HashMap<String, String>();
			CF.getDeductionTime(con,hmLunchDeduction);
			
			
			String[] strPayCycleDates = null;
			int ii = 0;

			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
			}

//			log.debug("Start ===> " + strPayCycleDates[0]);
//			log.debug("End ===> " + strPayCycleDates[1]);

			pst = con.prepareStatement(selectDates);
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();

			while (rs.next()) {
				_alDate.add(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT));
				_alDay.add(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT));

				if (hmHolidays != null && hmHolidays.containsKey(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT))) {
					_alHolidays.add(ii + "");
					_hmHolidaysColour.put(ii + "", (String) hmHolidays.get(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)));
				}
				ii++;
			}
			rs.close();
			pst.close();

			Map<String, Map<String, String>> hmEarlyLateReporting = new HashMap<String, Map<String, String>>();
			Map<String, String> hmFirstAidAllowance = new HashMap<String, String>();
			new CommonFunctions(CF).getEarlyLateReporting(con,strPayCycleDates[0], strPayCycleDates[1], hmEarlyLateReporting);
			new CommonFunctions(CF).getAllowanceMap(con,hmFirstAidAllowance);
			Map hmEmpTypeMap = new CommonFunctions(CF).getEmpTypeMap(con);

			Map hmRosterDetailsTemp = new HashMap();
			Map<String, Map<String, String>> hmRosterDetails = new HashMap<String, Map<String, String>>();

			
			
			
			
			Map hmEmpRosterLunchDeduction = new HashMap();
			Map hmRosterLunchDeduction = new HashMap();
			pst = con.prepareStatement(selectRosterDetails11);
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();

			String strEmpIdNew = null;
			String strEmpIdOld = null;
			
			while (rs.next()) {
				String strServiceId = rs.getString("service_id");
				strEmpIdNew = rs.getString("emp_id");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					hmRosterLunchDeduction = new HashMap();
				}
				
				hmRosterLunchDeduction.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"_"+strServiceId, rs.getString("is_lunch_ded"));
				hmRosterLunchDeduction.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), "");
				hmEmpRosterLunchDeduction.put(strEmpIdNew, hmRosterLunchDeduction);
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();

			
			pst = con.prepareStatement(selectClockEntries_A);
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
			String strOldEmpId = null;
			String strNewEmpId = null;
			List alServices = new ArrayList();

			while (rs.next()) {
				strNewEmpId = rs.getString("emp_id");
				if (strOldEmpId != null && !strOldEmpId.equalsIgnoreCase(strNewEmpId)) {
					hmRosterDetailsTemp = new HashMap<String, String>();

				}

				alServices = (List) hmRosterDetailsTemp.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
				if (alServices == null) {
					alServices = new ArrayList();
				}

				if(!alServices.contains(rs.getString("service_id"))){
					alServices.add(rs.getString("service_id"));
				}
				

				hmRosterDetailsTemp.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()), alServices);
				hmRosterDetails.put(rs.getString("emp_id"), hmRosterDetailsTemp);
				strOldEmpId = strNewEmpId;
				
				
//				log.debug(rs.getString("in_out_timestamp")+" === >"+strNewEmpId+"=====hmRosterDetails====>"+hmRosterDetails);
				
			}
			rs.close();
			pst.close();

			Map<String, String> hm = new HashMap<String, String>();
			Map<String, String> hmEmpCodeDesig = new HashMap<String, String>();
			Map<String, String> hmLunchDeductionService = CF.getLunchDeductionServiceDetails(con);
			
			List<String> alDay = new ArrayList<String>();
			List<String> alDate = new ArrayList<String>();
			List<String> alInOut = new ArrayList<String>();

			List<String> alEmpName = new ArrayList<String>();
			List<String> alEmpCode = new ArrayList<String>();
			List<String> alEmpCodeLink = new ArrayList<String>();

			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) ||
					strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(ACCOUNTANT) ||
					strUserType.equalsIgnoreCase(CFO) || strUserType.equalsIgnoreCase(HRMANAGER))) {

				if (strAlpha != null) {
					pst = con.prepareStatement(selectClockEntriesAdminROAlpha);
					pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
					pst.setString(3, strAlpha + "%");

				} else {
					pst = con.prepareStatement(selectClockEntriesAdminRO);
					pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				}

			} else {
				pst = con.prepareStatement(selectClockEntriesManagerR);
				pst.setInt(1, uF.parseToInt(strEmpID));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			}

			rs = pst.executeQuery();

			long _IN = 0L;
			long _OUT = 0L;
			double _TOTAL = 0.d;
			boolean isIn = false;
			boolean isOut = false;
			boolean isInOut = false;

			String strOld = null;
			String strNew = null;

			String strServiceIdOld = null;
			String strServiceIdNew = null;

			Map<String, Map<String, String>> hmManagerAttendenceReport = new HashMap<String, Map<String, String>>();
			Map<String, String> hmPayMode = new HashMap<String, String>();

			Map<String, String> hmWorkedHours = new HashMap<String, String>();
			Map<String, String> hmLeavePaid = new HashMap<String, String>();

			while (rs.next()) {

				String str = rs.getString("in_out");
				strServiceIdNew = rs.getString("service_id");
				strNew = rs.getString("emp_id");

				
				CF.getEmployeePaidMap(con,strNewEmpId, hmLeavePaid, null);
							
				if (strNew != null && !strNew.equals(strOld)) {
					hmWorkedHours = new HashMap();
					alDay = new ArrayList();
					alDate = new ArrayList();

				}

				strOld = strNew;

				if (!alEmpCode.contains(strNew)) {
					alEmpCodeLink.add("<a href=\"" + request.getContextPath() + "/ClockEntries.action?T=T&PAY=Y&EMPID=" + rs.getString("emp_id") + "&PC=" + strPC + "&D1=" + strD1 + "&D2=" + strD2 + "\">" + rs.getString("emp_fname") + " "
							+ rs.getString("emp_lname") + "</a>");
					
					alEmpCode.add(strNew);
					String strEmpMName = "";
					
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					alEmpName.add(rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				}

				String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat());

				if (!alDate.contains(strDate)) {
					alDate.add(strDate);
					alDay.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDayFormat()));
					_IN = 0L;
					_OUT = 0L;
					isIn = false;
					isOut = false;
					isInOut = false;
				} else if (strServiceIdNew != null && !strServiceIdNew.equalsIgnoreCase(strServiceIdOld)) {
					_IN = 0L;
					_OUT = 0L;
					isIn = false;
					isOut = false;
					isInOut = false;
				}

				if (str != null && str.equalsIgnoreCase("IN") && !isIn) {
					_IN = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					isIn = true;
				} else if (str != null && str.equalsIgnoreCase("OUT") && !isOut) {
					_OUT = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					isOut = true;
				}

				double dblLunchTime = uF.parseToDouble(hmLunchDeduction.get(O_LUNCH_DEDUCT_TIME));
				double dblLunch = uF.parseToDouble(hmLunchDeduction.get(O_LUNCH_DEDUCT));
				hmRosterLunchDeduction = (Map)hmEmpRosterLunchDeduction.get(strNew);
				if(hmRosterLunchDeduction==null){hmRosterLunchDeduction=new HashMap();}
				boolean isLunchDeductionService = uF.parseToBoolean((String)hmLunchDeductionService.get(strServiceIdNew));
				boolean isLunchDeduct = uF.parseToBoolean((String)hmRosterLunchDeduction.get(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_"+strServiceIdNew));
				
				
//				log.debug(strNewEmpId+" hmEmpRosterLunchDeduction===>"+hmEmpRosterLunchDeduction);
//				
//				log.debug("hmLunchDeductionService===>"+hmLunchDeductionService);
//				log.debug("hmRosterLunchDeduction===>"+hmRosterLunchDeduction);
//				log.debug("Date=="+strNew+"=>"+uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_"+strServiceIdNew);
//				log.debug("isLunchDeduct===>"+isLunchDeduct);
//				log.debug("isLunchDeduct===>"+isLunchDeduct);
						
						

//				log.debug(strNew + "==strDate==" + strDate + "_IN===strDate=" + _IN + "_OUT==" + _OUT + "==isInOut=" + isInOut);

				if (_IN > 0 && _OUT > 0 && !isInOut) {

					_TOTAL = uF.parseToDouble(uF.getTimeDiffInHoursMins(_IN, _OUT));
					isInOut = true;

					if (_TOTAL >= dblLunchTime && isLunchDeduct && isLunchDeductionService) {
						_TOTAL = _TOTAL - dblLunch;
					}

					hmWorkedHours.put(strDate + "_" + strServiceIdNew, uF.formatIntoTwoDecimal(_TOTAL));

//					log.debug("hmWorkedHours===strDate=" + strDate + "_TOTAL==" + _TOTAL);
//					log.debug("hmWorkedHours===strDate=" + hmWorkedHours);
				}

				Map<String, String> hmTemp = (HashMap) hmManagerAttendenceReport.get(rs.getString("emp_id"));
				if (hmTemp == null || (hmTemp != null && !hmTemp.containsKey(strDate))) {
//					log.debug(rs.getString("emp_id") + "===hmWorkedHours===strDate= Add " + hmWorkedHours);
					hmManagerAttendenceReport.put(rs.getString("emp_id"), hmWorkedHours);
				}

				strServiceIdOld = strServiceIdNew;

			}
			rs.close();
			pst.close();

			Map<String, Map<String, String>> hmPayrollPolicy = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmPayrollFT = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmPayrollPT = new HashMap<String, Map<String, String>>();
			hmPayrollPolicy = new CommonFunctions(CF).getDailyRates(con,hmPayrollFT, hmPayrollPT);

//			log.debug("hmManagerAttendenceReport======>" + hmManagerAttendenceReport);

			pst = con.prepareStatement(selectEmployeeR3);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmEmpCodeDesig.put(rs.getString("emp_id"), rs.getString("designation_id"));
			}
			rs.close();
			pst.close();

			
			
			Set set = hmManagerAttendenceReport.keySet();
			Iterator it = set.iterator();
			double dblTotalWorkedHours = 0.0d;
			boolean isFixedAdded = false;
			boolean isHourlyPayMode = false;
			boolean isFixedPayMode = false;
			while (it.hasNext()) {
				
				dblTotalWorkedHours = 0.0d;
				isHourlyPayMode = false;
				isFixedPayMode = false;
				isFixedAdded = false;
				String empId = (String) it.next();

				Map hmDaysAmount = (HashMap) hmPayrollPolicy.get((String) hmEmpCodeDesig.get(empId));

				Map hmWH = (HashMap) hmManagerAttendenceReport.get(empId);

				double total = 0.0d;

				if (hmDaysAmount == null) {
					continue;
				}

				pst = con.prepareStatement(selectSettings);
				rs = pst.executeQuery();
				double hrs = 0;
				while (rs.next()) {
					if (rs.getString("options").equalsIgnoreCase(O_STANDARD_FULL_TIME_HOURS)) {
						hrs = uF.parseToDouble(rs.getString("value"));
					}
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
				Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
				if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();

				String strPayMode = null;
				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < _alDate.size(); i++) {
					sb = new StringBuilder();
					

						String strDate = (String) _alDate.get(i);
						String strDesig = (String) hmEmpCodeDesig.get(empId);

						String strCurrency = "";
						if(uF.parseToInt(hmEmpCurrency.get(empId)) > 0){
							Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(empId));
							if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
							strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
						} 
						
						hmLeaves = hmLeavesMap.get(empId);
						if(hmLeaves==null){
							hmLeaves = new HashMap();
						}
						Map hmTemp = (HashMap) hmRosterDetails.get(empId);

						
						String strServiceId = null;
						String strServiceName = null;
						List alServiceTemp = null;
						if (hmTemp != null) {
							alServiceTemp = (List) hmTemp.get(strDate);
							if (alServiceTemp == null) {
								alServiceTemp = new ArrayList();
								alServiceTemp.add("");
							}
							
							
							

							for (int j = 0; j < alServiceTemp.size(); j++) {
								
								
								strServiceId = (String) alServiceTemp.get(j);
								strServiceName = (String) hmServices.get(strServiceId);

								if (strServiceName != null) {
									strServiceName = " (" + strServiceName + ")";
								} else {
									strServiceName = "";
								}

								double dblTotal = uF.parseToDouble((String) hmWH.get((String) _alDate.get(i)+"_"+strServiceId));
								Map hmRostertemp = (Map)hmEmpRosterLunchDeduction.get(empId);
								if(hmRostertemp==null){
									hmRostertemp = new HashMap();
								}
								boolean isRostered = hmRostertemp.containsKey((String) _alDate.get(i));
								
								
								Map hmRate = null;
								String strEmpType = (String) hmEmpType.get(empId);

								if (strEmpType != null && strEmpType.equalsIgnoreCase("PT")) {
									hmRate = (HashMap) hmPayrollPT.get("D" + strDesig + "S" + ((hmLeaves.containsKey((String) _alDate.get(i)))?"":strServiceId));
								} else {
									hmRate = (HashMap) hmPayrollFT.get("D" + strDesig + "S" + ((hmLeaves.containsKey((String) _alDate.get(i)))?"":strServiceId));
								}

								String strRate = null;
								String strLoading = null;
								if (hmRate != null) {
									strRate = (String) hmRate.get(uF.getDateFormat((String) _alDate.get(i), CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase());
									if(hmLeaves.containsKey((String) _alDate.get(i))){
										strRate = CF.getMinimumRateForPublicHolidays(con,empId, uF.getDateFormat((String) _alDate.get(i), CF.getStrReportDateFormat(), CF.getStrReportDayFormat()))+"";
									}
									strPayMode = (String) hmRate.get("PAYMODE");
//									strLoading = (String) hmRate.get(CF.getLoadingWeekDayCode(con,uF.getDateFormat((String) _alDate.get(i), CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase())); ---> Sanjay
									strLoading = (String) hmRate.get(CF.getLoadingWeekDayCode(uF.getDateFormat((String) _alDate.get(i), CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase()));
								}
								double dblRate = uF.parseToDouble(strRate);

								
								String strLeaveType = (String)hmLeaves.get(strDate);
								boolean isPaidLeave = uF.parseToBoolean((String)hmLeavePaid.get(strLeaveType));
								
								
								if(empId.equalsIgnoreCase("155")){
									
								}
								
								
								
								if (strPayMode != null && strPayMode.equalsIgnoreCase("H")) {
									isHourlyPayMode = true;

									if(hmLeaves!=null && hmLeaves.containsKey((String) _alDate.get(i))){
										dblTotal = LeaveHours;
										
										if(isPaidLeave){
											sb.append(dblTotal + " x " + strCurrency+dblRate  + "("+(String)hmLeaves.get((String) _alDate.get(i))+")");
											hmWH.put((String) _alDate.get(i), sb.toString());
											total += uF.convertHoursIntoMinutes(dblTotal) * dblRate;
										}else{
											sb.append(hmLeaves.get((String) _alDate.get(i)));
											hmWH.put((String) _alDate.get(i), sb.toString());
										}
										
									} else if (hmHolidays.containsKey((String) _alDate.get(i)) && strEmpType != null && strEmpType.equalsIgnoreCase("FT")) {
										
										
										if (dblTotal == 0 && !isRostered) {
											if(sb.length()>1){
												sb.append("<br>");
											}
											
											dblTotal = hrs;
											dblRate = CF.getMinimumRateForPublicHolidays(con,empId, uF.getDateFormat(strDate, CF.getStrReportDateFormat(), CF.getStrReportDayFormat()));
											
											sb.append(dblTotal +" x "+strCurrency + dblRate);
											hmWH.put((String) _alDate.get(i), sb.toString());
											total += uF.convertHoursIntoMinutes(dblTotal) * (dblRate);
											
										}else if (dblTotal == 0 && isRostered) {
											if(sb.length()>1){
												sb.append("<br>");
											}
											sb.append(dblTotal);
											hmWH.put((String) _alDate.get(i), sb.toString());
										} else {
											if(sb.length()>1){
												sb.append("<br>");
											}
											sb.append(dblTotal + " x " + strCurrency+(dblRate + (dblRate * uF.parseToDouble(strLoading) / 100)) + strServiceName);
											hmWH.put((String) _alDate.get(i), sb.toString());
											total += uF.convertHoursIntoMinutes(dblTotal) * (dblRate + (dblRate * uF.parseToDouble(strLoading) / 100));
										}
									}else {
										if (dblTotal > 0) {
											if(sb.length()>1){
												sb.append("<br>");
											}
											sb.append(dblTotal + " x " + strCurrency+ dblRate + strServiceName);
										} else {
											if(sb.length()>1){
												sb.append("<br>");
											}
											sb.append(dblTotal + strServiceName);
										}

										hmWH.put((String) _alDate.get(i), sb.toString());
										total += uF.convertHoursIntoMinutes(dblTotal) * dblRate;
									}

								} else if (strPayMode != null && strPayMode.equalsIgnoreCase("X")) {
									isFixedPayMode = true;
									if(sb.length()>1){
										sb.append("<br>");
									}
									sb.append(dblTotal + strServiceName);
									
									hmWH.put((String) _alDate.get(i), sb.toString());
//									hmWH.put((String) _alDate.get(i), strServiceName);
									
									if(!isFixedAdded){
										total += uF.parseToDouble((String) hmDaysAmount.get("FIXED"));
										isFixedAdded = true;
									}
									
								}
								dblTotalWorkedHours += dblTotal;
								
								
								if(isFixedPayMode && isHourlyPayMode){
									hmPayMode.put(empId, "Hourly/Fixed");
								}else if(isFixedPayMode){
									//hmPayMode.put(empId, uF.charMapping(strPayMode));
									hmPayMode.put(empId, "Fixed");
								}else if(isHourlyPayMode){
//									hmPayMode.put(empId, uF.charMapping(strPayMode));
									hmPayMode.put(empId, "Hourly");
								}
									
							}
						
					}
						
						
						
						

				}

				
				
				

				
				
				
				
				
				hmWH.put("PAYGROSS", uF.formatIntoTwoDecimal(total));
				hmWH.put("PAYDEDUCTION", uF.formatIntoTwoDecimal(new CommonFunctions(CF).getDeductionAmountMap(con,total)));

				double dblAllowance = 0;
				if (hmFirstAidAllowance.containsKey(empId)) {
					dblAllowance = new CommonFunctions(CF).getAllowanceValue(con,dblTotalWorkedHours, uF.parseToInt(empId));
					dblAllowance = total * dblAllowance / 100;
				}

				hmWH.put("dblTotalWorkedHours", uF.formatIntoTwoDecimal(dblTotalWorkedHours));
				hmWH.put("PAYALLOWANCE", uF.formatIntoTwoDecimal(dblAllowance));
				if (dblTotalWorkedHours > 0) {
					hmWH.put("PAYNET", uF.formatIntoTwoDecimal(total + dblAllowance - new CommonFunctions(CF).getDeductionAmountMap(con,total)));
				}

				hmManagerAttendenceReport.put(empId, hmWH);
//				hmPayMode.put(empId, uF.charMapping(strPayMode));

			}

			pst = con.prepareStatement(selectPayrollEmpId);
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			List alEmpIdPayroll = new ArrayList();

			while (rs.next()) {
				if (!alEmpIdPayroll.contains(rs.getString("emp_id"))) {
					alEmpIdPayroll.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();

			request.setAttribute("DT_MIN", strPayCycleDates[0]);
			request.setAttribute("DT_MAX", strPayCycleDates[1]);

			request.setAttribute("alDate", _alDate);
			request.setAttribute("alDay", _alDay);
			request.setAttribute("hmManagerAttendenceReport", hmManagerAttendenceReport);
			request.setAttribute("alEmpCode", alEmpCode);
			request.setAttribute("alEmpName", alEmpName);
			request.setAttribute("alEmpCodeLink", alEmpCodeLink);
			request.setAttribute("hmPayMode", hmPayMode);

			request.setAttribute("_alHolidays", _alHolidays);
			request.setAttribute("_hmHolidaysColour", _hmHolidaysColour);

			request.setAttribute("hmPayrollPolicy", hmPayrollPolicy);
			request.setAttribute("hmEarlyLateReporting", hmEarlyLateReporting);
			request.setAttribute("alEmpIdPayroll", alEmpIdPayroll);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public void updatePayrollDetails() {

		Connection con = null;
		PreparedStatement pst = null, pst1 = null;
		ResultSet rst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		try {

			Map<String, String> hmHolidays = new CommonFunctions(CF).getHolidayList(con,request);
			Map<String, String> hmFirstAidAllowance = new HashMap<String, String>();

			new CommonFunctions(CF).getAllowanceMap(con,hmFirstAidAllowance);
			double first_aid_allowance = 0;

			Map hmPayrollFT = new HashMap();
			Map hmPayrollPT = new HashMap();
			new CommonFunctions(CF).getDailyRates(con,hmPayrollFT, hmPayrollPT);
			
			
			
			pst = con.prepareStatement(selectPayrollRatesForUpdate1_N);

//			log.debug(" 0 pst===>" + pst);

			rst = pst.executeQuery();

			while (rst.next()) {
				String strEmpId = rst.getString("emp_id");
				String strDate =  rst.getString("_date");
				
				hmLeaves = hmLeavesMap.get(strEmpId);
				if(hmLeaves==null){
					hmLeaves = new HashMap<String, String>();
				}
				
				int nServiceId = rst.getInt("service_id"); 
//				pst1 = con.prepareStatement(selectPayrollRatesForUpdate2);
//				pst1.setInt(1, rst.getInt("emp_id"));
//				pst1.setInt(2, nServiceId);
//
//				rs = pst1.executeQuery();
					String rate = null;
					double dblLoading = 0;

					
					Map hmRates = (Map)hmPayrollFT.get("D"+strEmpId+"S"+((nServiceId>0)?nServiceId:""));
					if(hmRates==null){
						hmRates = new HashMap();
					}
					
//					log.debug(uF.getDateFormat(strDate, DBDATE, CF.getStrReportDayFormat())+"====="+"D"+strEmpId+"S"+((nServiceId>0)?nServiceId:"")+"hmRates===>"+hmRates);
					
					
					String strPayMode = (String)hmRates.get("PAYMODE");
					
					
					if (strPayMode != null && strPayMode.equalsIgnoreCase("X")) {
						rate = (String)hmRates.get("FIXED");
						dblLoading = uF.parseToDouble((String)hmRates.get("LOADING"));
					} else if (((uF.getDateFormat(strDate, DBDATE, CF.getStrReportDayFormat())).toUpperCase()).equalsIgnoreCase("SUNDAY")) {
						rate = (String)hmRates.get("SUNDAY");
						dblLoading = uF.parseToDouble((String)hmRates.get("SUN_LOADING"));
					} else if (((uF.getDateFormat(strDate, DBDATE, CF.getStrReportDayFormat())).toUpperCase()).equalsIgnoreCase("MONDAY")) {
						rate = (String)hmRates.get("MONDAY");
						dblLoading = uF.parseToDouble((String)hmRates.get("MON_LOADING"));
					} else if (((uF.getDateFormat(strDate, DBDATE, CF.getStrReportDayFormat())).toUpperCase()).equalsIgnoreCase("TUESDAY")) {
						rate = (String)hmRates.get("TUESDAY");
						dblLoading = uF.parseToDouble((String)hmRates.get("TUES_LOADING"));
					} else if (((uF.getDateFormat(strDate, DBDATE, CF.getStrReportDayFormat())).toUpperCase()).equalsIgnoreCase("WEDNESDAY")) {
						rate = (String)hmRates.get("WEDNESDAY");
						dblLoading = uF.parseToDouble((String)hmRates.get("WED_LOADING"));
					} else if (((uF.getDateFormat(strDate, DBDATE, CF.getStrReportDayFormat())).toUpperCase()).equalsIgnoreCase("THURSDAY")) {
						rate = (String)hmRates.get("THURSDAY");
						dblLoading = uF.parseToDouble((String)hmRates.get("THURS_LOADING"));
					} else if (((uF.getDateFormat(strDate, DBDATE, CF.getStrReportDayFormat())).toUpperCase()).equalsIgnoreCase("FRIDAY")) {
						rate = (String)hmRates.get("FRIDAY");
						dblLoading = uF.parseToDouble((String)hmRates.get("FRI_LOADING"));
					} else if (((uF.getDateFormat(strDate, DBDATE, CF.getStrReportDayFormat())).toUpperCase()).equalsIgnoreCase("SATURDAY")) {
						rate = (String)hmRates.get("SATURDAY");
						dblLoading = uF.parseToDouble((String)hmRates.get("SAT_LOADING"));
					}

					Map hmWorkedDates = (Map)hmEmpRosterLunchDeduction.get(strEmpId);
					String strDateTemp = uF.getDateFormat(strDate, DBDATE, DATE_FORMAT);
					if (hmHolidays != null && hmHolidays.containsKey(strDateTemp) && !hmWorkedDates.containsKey(strDateTemp) && hmLeaves!=null && !hmLeaves.containsKey(strDateTemp)) {
//					if (hmHolidays != null && hmHolidays.containsKey(strDateTemp) && !hmWorkedDates.containsKey(strDateTemp)) {
						rate = CF.getMinimumRateForPublicHolidays(con,strEmpId,uF.getDateFormat(strDateTemp, CF.getStrReportDateFormat(), CF.getStrReportDayFormat()))+"";
					}
					
					String strDateTemp1 = uF.getDateFormat(strDate, DBDATE, CF.getStrReportDateFormat());
					
					
					
					
//					log.debug("strDateTemp1===>"+strDateTemp1);
//					log.debug("hmLeaves===>"+hmLeaves);
//					log.debug("hmWorkedDates===>"+hmWorkedDates);
//					log.debug("hmHolidays===>"+hmHolidays);
					
					
					pst = con.prepareStatement(updatePayrollRate_N);
					pst.setDouble(1, uF.parseToDouble(rate));
					pst.setDouble(2, ((hmLeaves!=null && !hmLeaves.containsKey(strDateTemp1) && hmHolidays != null && hmHolidays.containsKey(strDateTemp1) && hmWorkedDates.containsKey(strDateTemp1)) ? dblLoading : 0));
					pst.setDate(3, uF.getDateFormat(strDate, DBDATE));
					pst.setInt(4, uF.parseToInt(strEmpId));
					pst.setInt(5, nServiceId);
					pst.execute();
					pst.close();

					
					double incomeAmount = 0;
					if (strPayMode != null && strPayMode.equalsIgnoreCase("X")) {
						incomeAmount = uF.parseToDouble(rate);
					} else {

						incomeAmount = uF.convertHoursIntoMinutes(rst.getDouble("total_time")) * uF.parseToDouble(rate);

						if (hmHolidays != null && hmHolidays.containsKey(uF.getDateFormat(strDate, DBDATE, CF.getStrReportDateFormat())) && hmWorkedDates.containsKey(uF.getDateFormat(strDate, DBDATE, CF.getStrReportDateFormat())) && hmLeaves!=null && !hmLeaves.containsKey(strDateTemp)) {
							incomeAmount += incomeAmount * dblLoading / 100;
						}

						if (hmFirstAidAllowance.containsKey(rst.getString("emp_id"))) {
							first_aid_allowance = new CommonFunctions(CF).getAllowanceValue(con,rst.getDouble("total_time"), uF.parseToInt(rst.getString("emp_id")));
							first_aid_allowance = incomeAmount * first_aid_allowance / 100;
						}

					}

					double deductionAmount = new CommonFunctions(CF).getDeductionAmountMap(con,incomeAmount);
					double netAmount = incomeAmount - deductionAmount + first_aid_allowance;

					String arr[] = getChbox();
					int x = ArrayUtils.contains(arr, strEmpId);

					pst = con.prepareStatement(updatePayrollRate1);
					pst.setDouble(1, incomeAmount);
					pst.setDouble(2, deductionAmount);
					pst.setDouble(3, netAmount);
					pst.setDouble(4, first_aid_allowance);
					pst.setBoolean(5, ((x < 0) ? false : true));
					pst.setInt(6, rst.getInt("payroll_id"));

					pst.execute();
					pst.close();
					request.setAttribute(MESSAGE, "Payroll approved");

			}
			rst.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeResultSet(rst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}

	}

	String paycycle;
	List<FillPayCycles> paycycleList;
	String redirectUrl;
	String approve;
	String dtMin;
	String dtMax;
	String[] chbox;
	String[] empID;

	public String getApprove() {
		return approve;
	}

	public void setApprove(String approve) {
		this.approve = approve;
	}

	public String getDtMin() {
		return dtMin;
	}

	public void setDtMin(String dtMin) {
		this.dtMin = dtMin;
	}

	public String getDtMax() {
		return dtMax;
	}

	public void setDtMax(String dtMax) {
		this.dtMax = dtMax;
	}

	public String[] getChbox() {
		return chbox;
	}

	public void setChbox(String[] chbox) {
		this.chbox = chbox;
	}

	public String[] getEmpID() {
		return empID;
	}

	public void setEmpID(String[] empID) {
		this.empID = empID;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getStrFrmD1() {
		return strFrmD1;
	}

	public void setStrFrmD1(String strFrmD1) {
		this.strFrmD1 = strFrmD1;
	}

	public String getStrFrmD2() {
		return strFrmD2;
	}

	public void setStrFrmD2(String strFrmD2) {
		this.strFrmD2 = strFrmD2;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setStrAlpha(String strAlpha) {
		this.strAlpha = strAlpha;
	}

}
