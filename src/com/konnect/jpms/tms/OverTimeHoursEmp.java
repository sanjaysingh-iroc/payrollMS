package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OverTimeHoursEmp extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	// String strEmpId;
	String strSelectedEmpId;
	String strType;
	String strPAY;
	// String strPC; 
	String strD1;
	String strD2;
	String payCycleEnd = null;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF;
	private String level;

	
	List<FillLevel> levelList;
	String paycycle;
	List<FillPayCycles> payCycleList;
	// List<FillEmployee> empList;
	List<FillEmployee> empNamesList;
	String profileEmpId;
	
	List<FillOrganisation> orgList;	
	String f_org;
	
	String location;
	List<FillWLocation> workLocationList;
	
	String submit;
	String payCycleNo;

	private static Logger log = Logger.getLogger(ClockEntries.class);

	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		log.debug("ClockEntries: execute()");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		if (strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
			request.setAttribute(TITLE, "Over Time Request");
		} else {
			request.setAttribute(TITLE, "Over Time Request");
		}

		request.setAttribute(PAGE, "/jsp/tms/OverTimeHoursEmp.jsp");
		
		profileEmpId = (String) request.getParameter("profileEmpId");

		strPAY = (String) request.getParameter("PAY");
		strType = (String) request.getParameter("T");

		
		loadClockEntries(uF);
		
		String[] strPayCycleDates = null;

		if (getPaycycle() != null) {

			strPayCycleDates = getPaycycle().split("-");
			strD1 = strPayCycleDates[0];
			strD2 = strPayCycleDates[1];
			setPayCycleNo(strPayCycleDates[2]);
		} else {

//			strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]);

			strD1 = strPayCycleDates[0];
			strD2 = strPayCycleDates[1];
			setPayCycleNo(strPayCycleDates[2]);
		}

		log.debug("strD1 ===> " + strD1);
		log.debug("strD2 ===> " + strD2);

		

		log.debug("strUserType==>" + strUserType);
		log.debug("getStrSelectedEmpId()==>" + getStrSelectedEmpId());
		log.debug("getPaycycle()==>" + getPaycycle());

		log.debug("strSelectedEmpId==>" + strSelectedEmpId);

		if(getStrSelectedEmpId()==null || getStrSelectedEmpId().equals("")){
			setStrSelectedEmpId(strSessionEmpId);
		}
		
//		System.out.println("getStrSelectedEmpId====>"+getStrSelectedEmpId());
		log.debug("strUserType ===> " + strUserType);
		log.debug("strSessionEmpId ===> " + strSessionEmpId);
		log.debug("etStrSelectedEmpId() ===> " + getStrSelectedEmpId());

		
//		System.out.println("strtype====>"+strType);
		
		String str = viewClockEntries(strD1, strD2);
		
		
		if(getSubmit()!=null){
			insertOvertTime(uF);
		}
		
		getOverTimeDetails(uF);
		
		return loadClockEntries(uF);

	}

private void getOverTimeDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		Map<String, String> hmEmpOrgMap=new HashMap<String, String>();
		Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap(con);
		
		try {
			
			CF.getEmpWlocationMap(con, null, null, null, hmEmpOrgMap);
			
			
			Map<String,Map<String,String>> hmOvertimeType=new HashMap<String, Map<String,String>>();
			pst=con.prepareStatement("select * from overtime_details where org_id=? and level_id=? " +
					" and (date_from,date_to) overlaps (to_date(?::text,'yyyy-MM-dd'),to_date(?::text,'yyyy-MM-dd'))");
			pst.setInt(1, uF.parseToInt(hmEmpOrgMap.get(getStrSelectedEmpId())));
			pst.setInt(2, uF.parseToInt(hmEmpLevelMap.get(getStrSelectedEmpId())));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst====>"+pst);
			rs=pst.executeQuery();
			while(rs.next()){
				
				if(rs.getString("overtime_type").equals("PH")){
					Map<String,String> hmOvertime=new HashMap<String, String>();
					hmOvertime.put("BUFFER_STANDARD_TIME", rs.getString("buffer_standard_time"));
					hmOvertime.put("OVERTIME_HRS", rs.getString("over_time_hrs"));
					hmOvertime.put("FIXED_OVERTIME_HRS", rs.getString("fixed_overtime_hrs"));
					hmOvertime.put("MIN_OVER_TIME", rs.getString("min_over_time"));				
					hmOvertime.put("STANDARD_WKG_HRS", rs.getString("standard_wkg_hours"));
					hmOvertime.put("FIXED_STWKG_HRS", rs.getString("fixed_stwkg_hrs"));
					hmOvertime.put("OVERTIME_TYPE", rs.getString("overtime_type"));
					
					hmOvertimeType.put("PH", hmOvertime);
				}else if(rs.getString("overtime_type").equals("BH")){
					Map<String,String> hmOvertime=new HashMap<String, String>();
					hmOvertime.put("BUFFER_STANDARD_TIME", rs.getString("buffer_standard_time"));
					hmOvertime.put("OVERTIME_HRS", rs.getString("over_time_hrs"));
					hmOvertime.put("FIXED_OVERTIME_HRS", rs.getString("fixed_overtime_hrs"));
					hmOvertime.put("MIN_OVER_TIME", rs.getString("min_over_time"));				
					hmOvertime.put("STANDARD_WKG_HRS", rs.getString("standard_wkg_hours"));
					hmOvertime.put("FIXED_STWKG_HRS", rs.getString("fixed_stwkg_hrs"));
					hmOvertime.put("OVERTIME_TYPE", rs.getString("overtime_type"));
					
					hmOvertimeType.put("BH", hmOvertime);
				}else if(rs.getString("overtime_type").equals("EH")){
					Map<String,String> hmOvertime=new HashMap<String, String>();
					hmOvertime.put("BUFFER_STANDARD_TIME", rs.getString("buffer_standard_time"));
					hmOvertime.put("OVERTIME_HRS", rs.getString("over_time_hrs"));
					hmOvertime.put("FIXED_OVERTIME_HRS", rs.getString("fixed_overtime_hrs"));
					hmOvertime.put("MIN_OVER_TIME", rs.getString("min_over_time"));				
					hmOvertime.put("STANDARD_WKG_HRS", rs.getString("standard_wkg_hours"));
					hmOvertime.put("FIXED_STWKG_HRS", rs.getString("fixed_stwkg_hrs"));
					hmOvertime.put("OVERTIME_TYPE", rs.getString("overtime_type"));
					
					hmOvertimeType.put("EH", hmOvertime);
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmOvertimeType", hmOvertimeType);
//			System.out.println("hmOvertimeType===>"+hmOvertimeType);
			
			Map<String, String> hmEmpLocation=CF.getEmpWlocationMap(con);
			String userlocation=hmEmpLocation.get(strSessionEmpId);
			
			pst= con.prepareStatement("select wlocation_id,wlocation_start_time,wlocation_end_time from work_location_info where wlocation_id =?");
			pst.setInt(1, uF.parseToInt(userlocation));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			String locationstarttime=null;
			String locationendtime=null;				
			while(rs.next()){
				locationstarttime=rs.getString("wlocation_start_time");
				locationendtime=rs.getString("wlocation_end_time");
			}
			rs.close();
			pst.close();
			request.setAttribute("locationstarttime", locationstarttime);
			request.setAttribute("locationendtime", locationendtime);
			request.setAttribute("userlocation", userlocation);
			
			pst= con.prepareStatement("select actual_ot_hours,_date,approved_ot_hours from overtime_hours where paycle=? and emp_id=?");
			pst.setInt(1, uF.parseToInt(getPayCycleNo()));
			pst.setInt(2, uF.parseToInt(getStrSelectedEmpId()));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmActualOT=new HashMap<String, String>();
			Map<String, String> hmApproveOT=new HashMap<String, String>();
			while(rs.next()){
				hmActualOT.put((String)getStrSelectedEmpId()+"_"+uF.getDateFormat( rs.getString("_date"), DBDATE, "dd-MMM-yyyy"), rs.getString("actual_ot_hours"));
				hmApproveOT.put(uF.getDateFormat(rs.getString("_date"), DBDATE, "dd-MMM-yyyy")+"_"+(String)getStrSelectedEmpId(), uF.formatIntoTwoDecimal(rs.getDouble("approved_ot_hours")));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmActualOT", hmActualOT);
			request.setAttribute("hmApproveOT", hmApproveOT);
//			System.out.println("hmActualOT===>"+hmActualOT);
			
			
			Map<String, String> hmCheckPayroll = new HashMap<String, String>(); 
			pst = con.prepareStatement("select emp_id from payroll_generation where paycycle=? and emp_id=? group by emp_id");
			pst.setInt(1, uF.parseToInt(getPayCycleNo()));
			pst.setInt(2, uF.parseToInt(getStrSelectedEmpId()));
			rs = pst.executeQuery();
//			System.out.println("pst======>"+pst);
			while (rs.next()) {
				hmCheckPayroll.put(rs.getString("emp_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmCheckPayroll", hmCheckPayroll);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void insertOvertTime(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
//			System.out.println("in insert======>");				
			String[] seletedOtDate=request.getParameterValues("ot");		
			for(int i=0;seletedOtDate!=null && i<seletedOtDate.length;i++){
				String actual_ot_hours=request.getParameter("ot_"+getStrSelectedEmpId()+"_"+(String)seletedOtDate[i]);
				if(uF.parseToDouble(actual_ot_hours)>0){
					pst = con.prepareStatement("update overtime_hours set actual_ot_hours=?" +
							" where  paycle=? and emp_id=? and _date=? ");
					pst.setDouble(1, uF.parseToDouble(actual_ot_hours));  
					pst.setInt(2, uF.parseToInt(getPayCycleNo()));
					pst.setInt(3, uF.parseToInt(getStrSelectedEmpId()));
					pst.setDate(4, uF.getDateFormat((String)seletedOtDate[i],"dd-MMM-yyyy"));
					int x = pst.executeUpdate();
					pst.close();
					if(x==0){
						pst = con.prepareStatement("insert into overtime_hours(emp_id,paycle,paycycle_from,paycycle_to,_date,actual_ot_hours)" +
								"values(?,?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
						pst.setInt(2, uF.parseToInt(getPayCycleNo()));
						pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
						pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
						pst.setDate(5, uF.getDateFormat((String)seletedOtDate[i],"dd-MMM-yyyy"));
						pst.setDouble(6, uF.parseToDouble(actual_ot_hours));
						pst.execute();
						pst.close();
					}
				}			
			}
			
			/*
			 List allDate=(List) session.getAttribute("allDate");
			 System.out.println("allDate======>"+allDate);
			 for(int i=0;allDate!=null && i<allDate.size();i++){
			System.out.println("allDate.get(i)======>"+allDate.get(i));
			
			//overtime_hours_id,emp_id,paycle,paycycle_from,paycycle_to,_date,actual_ot_hours,
			String actual_ot_hours=request.getParameter("ot_"+getStrSelectedEmpId()+"_"+(String)allDate.get(i));
			System.out.println("actual_ot_hours======>"+actual_ot_hours);
			if(uF.parseToDouble(actual_ot_hours)>0){
				pst = con.prepareStatement("update overtime_hours set actual_ot_hours=?" +
						" where  paycle=? and emp_id=? and _date=? ");
				pst.setDouble(1, uF.parseToDouble(actual_ot_hours));
				pst.setInt(2, uF.parseToInt(getPayCycleNo()));
				pst.setInt(3, uF.parseToInt(getStrSelectedEmpId()));
				pst.setDate(4, uF.getDateFormat((String)allDate.get(i),"dd-MMM-yyyy"));
				int x = pst.executeUpdate();
				if(x==0){
					pst = con.prepareStatement("insert into overtime_hours(emp_id,paycle,paycycle_from,paycycle_to,_date,actual_ot_hours)" +
							"values(?,?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
					pst.setInt(2, uF.parseToInt(getPayCycleNo()));
					pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat((String)allDate.get(i),"dd-MMM-yyyy"));
					pst.setDouble(6, uF.parseToDouble(actual_ot_hours));
					pst.execute();
				}
			}
		}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadClockEntries(UtilityFunctions uF) {
		
		
		orgList = new FillOrganisation(request).fillOrganisation();
		if(uF.parseToInt(getF_org())==0){
			setF_org(orgList.get(0).getOrgId());
		}
		payCycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		workLocationList = new FillWLocation(request).fillWLocation(getF_org());
//		System.out.println(uF.parseToInt(getF_org())+"  getF_org()"+getF_org());
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		if (strUserType != null
				&& (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) || strUserType.equalsIgnoreCase(HRMANAGER)
						|| strUserType.equalsIgnoreCase(ACCOUNTANT) || strUserType.equalsIgnoreCase(MANAGER))) {
			// empList = new FillEmployee().fillEmployeeName(strUserType,
			// strSessionEmpId);
			log.debug("getLevel()==>" + getLevel());
			
			//empNamesList = new FillEmployee().fillEmployeeName(strD1, uF.parseToInt(getLevel()),uF.parseToInt(getF_org()));
			empNamesList = new FillEmployee(request).fillEmployeeName(strD1, uF.parseToInt(getLevel()),uF.parseToInt(getF_org()),uF.parseToInt(getLocation()));
		}
		log.debug("Returning LOAD");
		return LOAD;
	}

	public String viewClockEntries(String strPrevDate, String strNextDate) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			
			con = db.makeConnection(con);
			
			
			
			Map<String, String> hmHolidays = new HashMap<String, String>();
			Map<String, String> hmHolidayDates = new HashMap<String, String>();
			Map<String, Map<String, String>> hmWLocationHolidaysName = new HashMap<String, Map<String, String>>();
			Map<String,Set<String>> holidaysMp=CF.getHolidayList(con,request,uF,strD1, strD2);
			
			CF.getHolidayList(strD1,request, strD2, CF, hmHolidayDates, hmHolidays, true);
//			getWLocationHolidayList(strD1, strD2, CF, hmWLocationHolidaysColour, hmWLocationHolidaysName, hmWLocationHolidaysWeekEnd);
			CF.getWLocationHolidayList(con, uF, strD1, strD2, CF, null, hmWLocationHolidaysName, null);
			
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);			
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEndList = CF.getWeekEndDateList(con, strD1, strD2, CF, uF,hmWeekEndHalfDates,null);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, strD1, strD2,alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEndList,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
			
			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con,null, null);

			List<String> _alHolidays = new ArrayList<String>();
			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
			Map<String, String> hmEmpType = CF.getEmpTypeMap(con);
			Map<String, String> hmServices = CF.getServicesMap(con,true);

			Map<String, String> hmLunchDeductionService = CF.getLunchDeductionServiceDetails(con);

			List _alDay = new ArrayList();
			List _alDate = new ArrayList();

			Map<String, String> hmLunchDeduction = new HashMap<String, String>();
			CF.getDeductionTime(con,hmLunchDeduction);

			
			pst = con.prepareStatement(selectDates);
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			

			log.debug("pst======>" + pst);

			java.sql.Date dtMin = null;
			java.sql.Date dtMax = null;
			int ii = 0;

			rs = pst.executeQuery();

			while (rs.next()) {

				if (dtMin == null) {
					dtMin = rs.getDate("_date");
				}
				dtMax = rs.getDate("_date");

				_alDate.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
				_alDay.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()));

				if (hmHolidayDates != null && hmHolidayDates.containsKey(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()))) {
					_alHolidays.add(ii + "");
					_hmHolidaysColour.put(ii + "", (String) hmHolidayDates.get(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())));
				}
				ii++;
			}
			rs.close();
			pst.close();

			log.debug("dtMax======>" + dtMax);

			if (dtMax == null) { 

				pst = con.prepareStatement(selectDatesDesc);

				// String tempDate=uF.getDateFormat(payCycleEnd,
				// CF.getStrReportDateFormat(),"yyyy-MM-dd");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				// pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				log.debug("select dates to be displayed:::>>>>>>>>." + pst);
				rs = pst.executeQuery();
				dtMin = null;
				dtMax = null;

				while (rs.next()) {
					if (dtMax == null) {
						dtMax = rs.getDate("_date");
					}
					dtMin = rs.getDate("_date");
					_alDate.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
					_alDay.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()));

					if (hmHolidayDates != null && hmHolidayDates.containsKey(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()))) {
						_alHolidays.add(ii + "");
						_hmHolidaysColour.put(ii + "", (String) hmHolidayDates.get(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())));
					}
					ii++;

				}
				rs.close();
				pst.close();
			}

			Map<String, Map<String, String>> hmRosterHours = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmRosterServices = new HashMap<String, Map<String, String>>();
			new CommonFunctions(CF).getRosterHoursMap(con, dtMin, dtMax, hmRosterHours, hmRosterServices);
			Map hmRosterHoursEmp = (Map) hmRosterHours.get(getStrSelectedEmpId());
			if (hmRosterHoursEmp == null) {
				hmRosterHoursEmp = new HashMap();
			}
//			System.out.println("hmRosterHoursEmp=======>"+hmRosterHoursEmp);
			
			
		
			
			String strSelectedEmpType = null;
			String strWLocationId = null;
			pst = con.prepareStatement(selectEmployee2Details);
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				strSelectedEmpType = rs.getString("emptype");
				strWLocationId = rs.getString("wlocation_id");
			}
			rs.close();
			pst.close();

			Map hmRosterLunchDeduction = new HashMap();
			Map hmRosterStart = new HashMap();
			Map hmRosterEnd = new HashMap();

			List alDateServices_CE = new ArrayList();
			Map hmDateServices_CE = new HashMap();

			log.debug("dtMin======>" + dtMin);
			log.debug("dtMax======>" + dtMax);

			Map<String, Map<String, String>> hmEarlyLateReporting = new HashMap<String, Map<String, String>>();
			new CommonFunctions(CF).getEarlyLateReporting(con, dtMin, dtMax, hmEarlyLateReporting);
			
			String strOldEmpId = null;
			String strNewEmpId = null;
			String strServiceId = null;
			String strDateNew = null;
			String strDateOld = null;
			List alDateServices_TS = new ArrayList();
			Map hmDateServices_TS = new HashMap();
			List alHours = new ArrayList();
			Map<String, Map<String, String>> hmLeaveDatesType = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmLeavesMap = CF.getLeaveDates(con, strD1, strD2, CF, hmLeaveDatesType, false, null);
			Map<String, Map<String, String>> hmLeavesMap = CF.getActualLeaveDates(con, CF, uF, strD1, strD2, hmLeaveDatesType, false, null);
			Map<String, String> hmLeaves = null;
			Map<String, String> hmLeavePaid = new HashMap<String, String>();
			Map<String, String> hmLeavesColour = new HashMap<String, String>();
			CF.getLeavesAttributes(con, uF, hmLeavesColour, null);
			hmLeaves = hmLeavesMap.get(getStrSelectedEmpId());
			if (hmLeaves == null) {
				hmLeaves = new HashMap<String, String>();
			}
			CF.getEmployeePaidMap(con, getStrSelectedEmpId(), hmLeavePaid, null);

			boolean isFixedAdded = false;
			
			pst = con.prepareStatement(selectRosterDetails1);
			pst.setDate(1, dtMin);
			pst.setDate(2, dtMax);
			pst.setInt(3, uF.parseToInt(getStrSelectedEmpId()));
			rs = pst.executeQuery();
			
			while (rs.next()) {

				isFixedAdded = false;
				strNewEmpId = rs.getString("emp_id");
				strServiceId = rs.getString("service_id");
				strDateNew = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());


				if (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld)) {
					alDateServices_TS = new ArrayList();
				}
				if (strServiceId != null && !alDateServices_TS.contains(strServiceId) && !hmLeaves.containsKey(strDateNew)) {
					alDateServices_TS.add(strServiceId);
					hmDateServices_TS.put(strDateNew, alDateServices_TS);
				}

				// hmRosterDetails.put(rs.getString("emp_id"),
				// hmRosterDetailsTemp);
				hmRosterStart.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "_" + strServiceId,
						uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				hmRosterEnd.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "_" + strServiceId,
						uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
				hmRosterLunchDeduction.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "_" + strServiceId, rs.getString("is_lunch_ded"));

				String strTempDate = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());

				if (!alHours.contains(strTempDate)) {
					alHours.add(strTempDate);
				}

				if (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld)) {
					alDateServices_CE = new ArrayList();
				}
				if (strServiceId != null && !alDateServices_CE.contains(strServiceId)) {
					alDateServices_CE.add(strServiceId);
					hmDateServices_CE.put(strDateNew, alDateServices_CE);
				}

				strOldEmpId = strNewEmpId;
				strDateOld = strDateNew;
			}
			rs.close();
			pst.close();

			Map<String, String> hmEmpCodeDesig = new HashMap<String, String>();

			Map<String, Map<String, String>> hmPayrollFT = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmPayrollPT = new HashMap<String, Map<String, String>>();

			CF.getDailyRates(con, hmPayrollFT, hmPayrollPT);

			// pst = con.prepareStatement(selectEmployeeR3);
			// rs = pst.executeQuery();
			// while (rs.next()) {
			// hmEmpCodeDesig.put(rs.getString("emp_id"),
			// rs.getString("designation_id"));
			// }

			List alDay = new ArrayList();
			List alDate = new ArrayList();

			List alInOut = new ArrayList();

			Map<String, String> hmHoursActual = new HashMap<String, String>();
			Map<String, String> hmHours = new HashMap<String, String>();
			Map<String, String> hmStart = new HashMap<String, String>();
			Map<String, String> hmStartClockEntries = new HashMap<String, String>();
			Map<String, String> hmActualStartClockEntries = new HashMap<String, String>();
			Map<String, String> hmEnd = new HashMap<String, String>();
			Map<String, String> hmEndClockEntries = new HashMap<String, String>();
			Map<String, String> hmActualEndClockEntries = new HashMap<String, String>();
			Map<String, String> hmHoursRates = new HashMap<String, String>();
			Map<String, String> hmDailyRate = new HashMap<String, String>();
			Map<String, String> hmServicesWorkedFor = new HashMap<String, String>();

			
			Map<String, String> hmExceptions = new HashMap<String, String>();
			
			
			pst = con.prepareStatement(selectClockEntries);
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
			pst.setDate(2, dtMin);
			pst.setDate(3, dtMax);
			log.debug("selectClockEntries===>" + pst);
			rs = pst.executeQuery();
			long _IN = 0L;
			long _OUT = 0L;
			long _INActual = 0L;
			long _OUTActual = 0L;
			double _TOTALW1 = 0L;
			double _TOTALW2 = 0L;
			double _TOTALRosterW1 = 0L;
			double _TOTALRosterW2 = 0L;
			double _PAYTOTALW1 = 0L;
			double _PAYTOTALW2 = 0L;
			double _PAYTOTAL = 0L;
			double _TOTAL = 0L;
			boolean isOut = false;
			boolean isIn = false;
			boolean isInOut = false;
			String strPayMode = null;
			int dayCount = -1;
			Map hmRate = null;
			java.util.Date strWeek1Date = null;
			int nServiceIdNew = 0;
			int nServiceIdOld = 0;
			while (rs.next()) {

				strDateNew = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat());
				String str = rs.getString("in_out");
				nServiceIdNew = rs.getInt("service_id");
				int nApproved = rs.getInt("approved");
				// int nNotifyTime = rs.getInt("notify_time");
				// String strNewTime =
				// uF.getDateFormat(rs.getString("new_time"), DBTIME,
				// CF.getStrReportTimeFormat());

				if (!alDate.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
					alDate.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
					alDay.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDayFormat()));
					_IN = 0L;
					_OUT = 0L;
					_INActual = 0L;
					_OUTActual = 0L;
					isIn = false;
					isOut = false;
					isInOut = false;
					dayCount++;
				} else if (nServiceIdNew != nServiceIdOld) {
					_IN = 0L;
					_OUT = 0L;
					_INActual = 0L;
					_OUTActual = 0L;
					isIn = false;
					isOut = false;
					isInOut = false;
				}

				if (str != null && str.equalsIgnoreCase("IN") && !isIn) {
					_IN = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					// _INActual =
					// uF.getTimeFormat(rs.getString("in_out_timestamp_actual"),
					// DBTIMESTAMP).getTime();
					_INActual = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					hmStart.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					// hmStartClockEntries.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"),
					// DBTIMESTAMP,
					// CF.getStrReportDateFormat())+"_"+nServiceIdNew,
					// uF.getDateFormat(rs.getString("in_out_timestamp_actual"),
					// DBTIMESTAMP, CF.getStrReportTimeFormat()));
					hmStartClockEntries.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					
					hmActualStartClockEntries.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					
					isIn = true;

					// if(nApproved==1 && nNotifyTime==1 && strNewTime!=null){
					// String strTemp =
					// (String)hmRosterStart.get(uF.getDateFormat(rs.getString("in_out_timestamp"),
					// DBDATE, CF.getStrReportDateFormat())+"_"+nServiceIdNew);
					// hmRosterStart.put(uF.getDateFormat(rs.getString("in_out_timestamp"),
					// DBDATE, CF.getStrReportDateFormat())+"_"+nServiceIdNew,
					// strTemp+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src=\""+request.getContextPath()+"/images1/tick_16x16.png\" />");
					// }

					if (nApproved == 1) {
						String strTemp = (String) hmRosterStart.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew);
						hmRosterStart.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew, uF.showData(strTemp, "")
						// +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src=\""+request.getContextPath()+"/images1/tick_16x16.png\" />"
								);
					}
					
					
					
					hmExceptions.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew+"_IN", rs.getString("approved"));

				} else if (str != null && str.equalsIgnoreCase("OUT") && !isOut) {
					_OUT = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					// _OUTActual =
					// uF.getTimeFormat(rs.getString("in_out_timestamp_actual"),
					// DBTIMESTAMP).getTime();
					_OUTActual = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					hmEnd.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					// hmEndClockEntries.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"),
					// DBTIMESTAMP,
					// CF.getStrReportDateFormat())+"_"+nServiceIdNew,
					// uF.getDateFormat(rs.getString("in_out_timestamp_actual"),
					// DBTIMESTAMP, CF.getStrReportTimeFormat()));
					hmEndClockEntries.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					
					hmActualEndClockEntries.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					
					isOut = true;

					// if(nApproved==1 && nNotifyTime==1 && strNewTime!=null){
					// String strTemp =
					// (String)hmRosterEnd.get(uF.getDateFormat(rs.getString("in_out_timestamp"),
					// DBDATE, CF.getStrReportDateFormat())+"_"+nServiceIdNew);
					// hmRosterEnd.put(uF.getDateFormat(rs.getString("in_out_timestamp"),
					// DBDATE, CF.getStrReportDateFormat())+"_"+nServiceIdNew,
					// strTemp+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src=\""+request.getContextPath()+"/images1/tick_16x16.png\" />");
					// }

					if (nApproved == 1) {
						String strTemp = (String) hmRosterEnd.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew);
						hmRosterEnd.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew, uF.showData(strTemp, "")
						// +
						// "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src=\""+request.getContextPath()+"/images1/tick_16x16.png\" />"
								);
					}
					
					hmExceptions.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew+"_OUT", rs.getString("approved"));
				}

				// if(strDateNew!=null &&
				// !strDateNew.equalsIgnoreCase(strDateOld)){
				// alDateServices_TS = new ArrayList();
				// }
				if (nServiceIdNew > 0 && !alDateServices_TS.contains(nServiceIdNew + "")
						&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
					alDateServices_TS.add(nServiceIdNew + "");
					hmDateServices_TS.put(strDateNew, alDateServices_TS);
				}

				if (_IN > 0 && _OUT > 0 && !isInOut) {

					int i = _alDate.indexOf(strDateNew);

					/**
					 * LUNCH HOUR CALCULATION FOR DEDUCTION
					 * 
					 * */

					double dblHoursWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(_IN, _OUT));
					double dblHoursWorkedActual = uF.parseToDouble(uF.getTimeDiffInHoursMins(_INActual, _OUTActual));
					double dblLunchTime = uF.parseToDouble(hmLunchDeduction.get(O_LUNCH_DEDUCT_TIME));
					double dblLunch = uF.parseToDouble(hmLunchDeduction.get(O_LUNCH_DEDUCT));

					boolean isLunchDeductionService = uF.parseToBoolean((String) hmLunchDeductionService.get(nServiceIdNew + ""));
					// boolean isLunchDeduct =
					// uF.parseToBoolean((String)hmRosterLunchDeduction.get(uF.getDateFormat(rs.getString("in_out_timestamp_actual"),
					// DBTIMESTAMP,
					// CF.getStrReportDateFormat())+"_"+nServiceIdNew));
					boolean isLunchDeduct = uF.parseToBoolean((String) hmRosterLunchDeduction.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP,
							CF.getStrReportDateFormat())
							+ "_" + nServiceIdNew));

					if (dblHoursWorked >= dblLunchTime && isLunchDeduct && isLunchDeductionService) {
						dblHoursWorked = dblHoursWorked - dblLunch;
					}

					hmHours.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.formatIntoTwoDecimal(dblHoursWorked));
					hmHoursActual.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.formatIntoTwoDecimal(dblHoursWorkedActual));

					// String strHours = uF.getTimeDiffInHoursMins(_IN, _OUT);

					String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat());
					String strDesig = (String) hmEmpCodeDesig.get(getStrSelectedEmpId());

					// Map hmTemp = (HashMap) hmRosterDetails.get(EMPID);

					strServiceId = null;
					// String strServiceName = null;
					// if (hmTemp != null) {
					// strServiceId = (String) hmTemp.get(strDate);
					// // strServiceName = (String) hmServices.get((String)
					// hmTemp.get(strDate));
					// strServiceName = (String)
					// hmServices.get(nServiceIdNew+"");
					// }
					// if (strServiceName == null) {
					// strServiceName = "";
					// }

					hmServicesWorkedFor.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							(String) hmServices.get(nServiceIdNew + ""));

					String strEmpType = (String) hmEmpType.get(getStrSelectedEmpId());

					log.debug("strEmpType===>" + strEmpType);
					log.debug("hmPayrollPT===>" + hmPayrollPT);
					log.debug("hmPayrollFT===>" + hmPayrollFT);
					log.debug("strDesig===>" + strDesig);
					log.debug("strServiceId===>" + strServiceId);

					if (strEmpType != null && strEmpType.equalsIgnoreCase("PT")) {
						hmRate = (HashMap) hmPayrollPT.get("D" + strDesig + "S" + nServiceIdNew);
					} else {
						hmRate = (HashMap) hmPayrollFT.get("D" + strDesig + "S" + nServiceIdNew);
					}

					String strRate = null;
					String strLoading = null;
					if (hmRate != null) {
						strRate = (String) hmRate.get(uF.getDateFormat((String) _alDate.get(i), CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase());

						// strLoading = (String) hmRate.get("LOADING");
						strLoading = (String) hmRate.get(CF.getLoadingWeekDayCode(uF.getDateFormat((String) _alDate.get(i), CF.getStrReportDateFormat(),
								CF.getStrReportDayFormat()).toUpperCase()));
					} else {
						hmRate = new HashMap();
					}
					double dblRate = uF.parseToDouble(strRate);

					strPayMode = (String) hmRate.get("PAYMODE");

					if (strPayMode != null && strPayMode.equalsIgnoreCase("H") && !hmLeaves.containsKey(strDate)) {

						double rate = uF.parseToDouble(strRate);
						double loading = uF.parseToDouble(strLoading);
						double rateLoading = 0.0;

						// hmDailyRate.put(strDate, strRate);

//						log.debug("hmHolidays=" + hmHolidayDates);
						log.debug("strDate=" + strDate);
						log.debug("strEmpType=" + strEmpType);

						log.debug("rate=" + rate);

						if (strEmpType != null && strEmpType.equalsIgnoreCase("FT")) {
							if (hmHolidayDates != null && hmHolidayDates.containsKey(strDate)) {
								rateLoading = rate + (rate * loading) / 100;
								hmDailyRate.put(strDate + "_" + nServiceIdNew, uF.formatIntoTwoDecimal(rateLoading));
							} else {
								rateLoading = rate;
								hmDailyRate.put(strDate + "_" + nServiceIdNew, uF.formatIntoTwoDecimal(uF.parseToDouble(strRate)));
							}
						} else {
							rateLoading = rate;
							hmDailyRate.put(strDate + "_" + nServiceIdNew, uF.formatIntoTwoDecimal(uF.parseToDouble(strRate)));
						}

						_PAYTOTAL += uF.convertHoursIntoMinutes(dblHoursWorked) * rateLoading;
						hmHoursRates.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
								uF.formatIntoTwoDecimal(uF.convertHoursIntoMinutes(dblHoursWorked) * rateLoading) + "");
					} else if (!hmLeaves.containsKey(strDate)) {

						hmHoursRates.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew, "Fixed");
						if (!isFixedAdded) {
							_PAYTOTAL += uF.parseToDouble((String) hmRate.get("FIXED"));
							isFixedAdded = true;
						}

					}

					// _TOTAL = uF.parseToDouble(uF.getTimeDiffInHoursMins(_IN,
					// _OUT));
					_TOTAL = dblHoursWorked;

					isInOut = true;

					// if (_alDate != null && _alDate.size() >= 7) {
					// log.debug("_alDate====="+_alDate);
					// strWeek1Date = uF.getDateFormatUtil((String)
					// _alDate.get(7), CF.getStrReportDateFormat());
					// }

					if (_alDate != null && _alDate.size() >= 6) {
						log.debug("_alDate=====" + _alDate);
						strWeek1Date = uF.getDateFormatUtil((String) _alDate.get(6), CF.getStrReportDateFormat());
					}

					java.util.Date currentDate = uF.getDateFormatUtil(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()),
							CF.getStrReportDateFormat());
					if (strWeek1Date != null && strWeek1Date.after(currentDate)
							&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
						_TOTALW1 += _TOTAL;
						_TOTALRosterW1 += uF.parseToDouble((String) hmRosterHoursEmp.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP,
								CF.getStrReportDateFormat())));
					} else if (!hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
						_TOTALW2 += _TOTAL;
						_TOTAL = 0;
						_TOTALRosterW2 += uF.parseToDouble((String) hmRosterHoursEmp.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP,
								CF.getStrReportDateFormat())));
					}

				}

				nServiceIdOld = nServiceIdNew;
				strDateOld = strDateNew;

			}
			rs.close();
			pst.close();

			String[] label = new String[_alDate.size()];
			double[] workedHours = new double[_alDate.size()];
			double[] rosterHours = new double[_alDate.size()];

			for (int i = 0; i < _alDate.size(); i++) {
				label[i] = (String) _alDate.get(i);
				workedHours[i] = uF.parseToDouble((String) hmHours.get((String) _alDate.get(i)));
				rosterHours[i] = uF.parseToDouble((String) hmRosterHoursEmp.get((String) _alDate.get(i)));
			}

			/**
			 * START HOLIDAY PAYMENT SETTINGS FOR FULL TIME EMPLOYEES
			 * 
			 * */

			log.debug(getStrSelectedEmpId() + "=strSelectedEmpType==" + strSelectedEmpType);

			if (strSelectedEmpType != null && strSelectedEmpType.equalsIgnoreCase("FT")) {
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

				//log.debug(hrs + "=hmHolidays==" + hmHolidayDates);

				Set set = hmHolidayDates.keySet();
				Iterator it = set.iterator();
				while (it.hasNext()) {
					String strDate = (String) it.next();
					Date utDate = uF.getDateFormatUtil(strDate, CF.getStrReportDateFormat());

					if (!hmHours.containsKey(strDate) && !hmLeaves.containsKey(strDate)) {

						hmStart.put(strDate + "_", PublicHoliday);

						hmHours.put(strDate + "_", uF.formatIntoTwoDecimal(hrs));

						strServiceId = "";
						String strEmpType = (String) hmEmpType.get(getStrSelectedEmpId());

						if (strEmpType != null && strEmpType.equalsIgnoreCase("PT")) {
							hmRate = (HashMap) hmPayrollPT.get("D" + getStrSelectedEmpId() + "S" + strServiceId);
						} else {
							hmRate = (HashMap) hmPayrollFT.get("D" + getStrSelectedEmpId() + "S" + strServiceId);
						}
						if (hmRate == null) {
							hmRate = new HashMap();
						}

						String strRate = (String) hmRate.get(uF.getDateFormat(strDate, CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase());
						hmDailyRate.put(strDate + "_", uF.formatIntoTwoDecimal(uF.parseToDouble(strRate)));
						hmHoursRates.put(strDate + "_", uF.formatIntoTwoDecimal(uF.convertHoursIntoMinutes(hrs * uF.parseToDouble(strRate))));

						log.debug("strRate=" + strRate);

						if (strWeek1Date != null && strWeek1Date.after(utDate)) {
							_TOTALW1 += hrs;
							_PAYTOTALW1 += hrs * uF.parseToDouble(strRate);
							_PAYTOTAL += hrs * uF.parseToDouble(strRate);
						} else {
							_TOTALW2 += hrs;
							_PAYTOTAL += hrs * uF.parseToDouble(strRate);
						}

						log.debug("_TOTALW2=" + _TOTALW2);
						log.debug("_PAYTOTAL=" + _PAYTOTAL);

					}
				}

				/**
				 * END HOLIDAY PAYMENT SETTINGS FOR FULL TIME EMPLOYEES
				 * 
				 * */

				/**
				 * LEAVE MANAGEMENT
				 * */

				Set setLeaves = hmLeaves.keySet();
				Iterator itLeaves = setLeaves.iterator();
				while (itLeaves.hasNext()) {
					String strDate = (String) itLeaves.next();
					Date utDate = uF.getDateFormatUtil(strDate, DATE_FORMAT);

					String strLeaveType = (String) hmLeaves.get(strDate);
					boolean isPaidLeave = uF.parseToBoolean((String) hmLeavePaid.get(strLeaveType));

					hmStart.put(strDate + "_", (String) hmLeaves.get(strDate));
					hrs = LeaveHours; // Leaves standard hours are different
										// from holidays hours for Oracle CMS
										// client

					if (!isPaidLeave)
						continue;

					hmHours.put(strDate + "_", uF.formatIntoTwoDecimal(hrs));
					strServiceId = "";
					String strEmpType = (String) hmEmpType.get(getStrSelectedEmpId());

					if (strEmpType != null && strEmpType.equalsIgnoreCase("PT")) {
						hmRate = (HashMap) hmPayrollPT.get("D" + getStrSelectedEmpId() + "S" + strServiceId);
					} else {
						hmRate = (HashMap) hmPayrollFT.get("D" + getStrSelectedEmpId() + "S" + strServiceId);
					}
					if (hmRate == null) {
						hmRate = new HashMap();
					}

					// String strRate = (String)
					// hmRate.get(uF.getDateFormat(strDate,
					// CF.getStrReportDateFormat(),
					// CF.getStrReportDayFormat()).toUpperCase());

					String strRate = CF.getMinimumRateForPublicHolidays(con, getStrSelectedEmpId(), uF.getDateFormat(strDate, DATE_FORMAT, CF.getStrReportDayFormat())) + "";
					hmDailyRate.put(strDate + "_", uF.formatIntoTwoDecimal(uF.parseToDouble(strRate)));
					hmHoursRates.put(strDate + "_", uF.formatIntoTwoDecimal(uF.convertHoursIntoMinutes(hrs * uF.parseToDouble(strRate))));

					// log.debug("strRate="+strRate);

					if (strWeek1Date != null && strWeek1Date.after(utDate)) {

						log.debug("_TOTALW1=" + _TOTALW1 + " hrs=" + hrs);

						_TOTALW1 += hrs;
						_PAYTOTALW1 += hrs * uF.parseToDouble(strRate);
						_PAYTOTAL += hrs * uF.parseToDouble(strRate);
					} else {
						log.debug("_TOTALW2=" + _TOTALW2 + " hrs=" + hrs);

						_TOTALW2 += hrs;
						_PAYTOTAL += hrs * uF.parseToDouble(strRate);
					}

					log.debug("_PAYTOTAL=" + _PAYTOTAL);

				}
			}

			/**
			 * END LEAVE PAYMENT SETTINGS FOR FULL TIME EMPLOYEES
			 * 
			 * */

			// request.setAttribute("CHART_RSTER_VS_ACTUAL", new
			// BarChart().getChartWithMarks(workedHours, rosterHours, label));
			
//			System.out.println("hmHolidayDates=====>"+hmHolidayDates);
//			System.out.println("hmWeekEndList=====>"+hmWeekEndList);
			request.setAttribute("alInOut", alInOut);
			request.setAttribute("alDate", _alDate);
			request.setAttribute("alDay", _alDay);
			request.setAttribute("hmHours", hmHours);
			request.setAttribute("hmHoursActual", hmHoursActual);
			request.setAttribute("hmStart", hmStart);
			request.setAttribute("hmStartClockEntries", hmStartClockEntries);
			request.setAttribute("hmActualStartClockEntries", hmActualStartClockEntries);

			request.setAttribute("hmRosterStart", hmRosterStart);
			request.setAttribute("hmRosterEnd", hmRosterEnd);
			request.setAttribute("hmEnd", hmEnd);
			request.setAttribute("hmEndClockEntries", hmEndClockEntries);
			request.setAttribute("hmActualEndClockEntries", hmActualEndClockEntries);
			request.setAttribute("TOTALW1", _TOTALW1 + "");
			request.setAttribute("TOTALW2", _TOTALW2 + "");
			request.setAttribute("_TOTALRosterW1", _TOTALRosterW1 + "");
			request.setAttribute("_TOTALRosterW2", _TOTALRosterW2 + "");
			request.setAttribute("PAYTOTALW1", _PAYTOTALW1 + "");
			request.setAttribute("PAYTOTALW2", _PAYTOTALW2 + "");
			request.setAttribute("_PAYTOTAL", _PAYTOTAL + "");
			request.setAttribute("hmDateServices_CE", hmDateServices_CE);
			request.setAttribute("hmDateServices_TS", hmDateServices_TS);
			request.setAttribute("hmServices", hmServices);
			request.setAttribute("_alHolidays", _alHolidays);
			request.setAttribute("hmLeaves", hmLeaves);
			request.setAttribute("EMP_NAME", hmEmpNameMap.get(getStrSelectedEmpId()));
			request.setAttribute("EMPID", getStrSelectedEmpId());
			request.setAttribute("hmLeavesColour", hmLeavesColour);
			request.setAttribute("hmExceptions", hmExceptions);

			// System.out.println("hmLeaves====>"+hmLeaves);

			Map<String, String> hmFirstAidAllowance = new HashMap<String, String>();
			CF.getAllowanceMap(con, hmFirstAidAllowance);

			double dblAllowance = 0;
			if (hmFirstAidAllowance.containsKey(getStrSelectedEmpId())) {
				dblAllowance = CF.getAllowanceValue(con, _TOTALW1 + _TOTALW2, uF.parseToInt(getStrSelectedEmpId()));
			}

			log.debug("_PAYTOTAL=" + _PAYTOTAL);

			request.setAttribute("ALLOWANCE", uF.formatIntoTwoDecimal(dblAllowance));
			request.setAttribute("DEDUCTION", CF.getDeductionAmountMap(con, _PAYTOTAL) + "");
			request.setAttribute("hmWeekEndList", hmWeekEndList);
			request.setAttribute("strWLocationId", strWLocationId);
			request.setAttribute("hmDailyRate", hmDailyRate);
			request.setAttribute("hmHoursRates", hmHoursRates);
			request.setAttribute("strPayMode", strPayMode);
			if (hmRate == null) {
				hmRate = new HashMap();
			}
			request.setAttribute("FIXED", (String) hmRate.get("FIXED"));
			
		
			
			request.setAttribute("hmWLocationHolidaysName", hmWLocationHolidaysName);
			request.setAttribute("_hmHolidaysColour", _hmHolidaysColour);
			request.setAttribute("hmHolidayDates", hmHolidayDates);
			request.setAttribute("hmServicesWorkedFor", hmServicesWorkedFor);
			request.setAttribute("hmRosterHours", hmRosterHoursEmp);

			if (hmEarlyLateReporting != null && strUserType != null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
				request.setAttribute("hmEarlyLateReporting", hmEarlyLateReporting.get(getStrSelectedEmpId()));
			}
			request.setAttribute("hmRosterWeekEndDates", hmRosterWeekEndDates);
			request.setAttribute("alEmpCheckRosterWeektype", alEmpCheckRosterWeektype);		
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String viewClockEntriesForManager() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

//			Map hmEmpRates = new CommonFunctions().getDailyRates(con);
			Map hmEmpDesig = CF.getEmpDesigMap(con);

			Map<String, String> hmHolidays = CF.getHolidayList(con,request);
			List<String> _alHolidays = new ArrayList<String>();
			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
			Map<String, String> hmEmpType = CF.getEmpTypeMap(con);
			Map<String, String> hmServices = CF.getServicesMap(con, true);

			List _alDay = new ArrayList();
			List _alDate = new ArrayList();

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement(selectDatesDescPayroll);
			rs = pst.executeQuery();

			java.sql.Date dtMin = null;
			java.sql.Date dtMax = null;

			int i = 0;

			while (rs.next()) {

				if (dtMax == null) {
					dtMax = rs.getDate("_date");
				}
				dtMin = rs.getDate("_date");

				_alDate.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
				_alDay.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()));

				if (hmHolidays != null && hmHolidays.containsKey(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT))) {
					_alHolidays.add(i + "");
					_hmHolidaysColour.put(i + "", (String) hmHolidays.get(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)));
				}
				i++;

			}
			rs.close();
			pst.close();

			if (dtMax == null) {

				pst = con.prepareStatement(selectDatesDesc);
				pst.setDate(1, uF.getDateFormat(strD1, CF.getStrReportDateFormat()));
				pst.setDate(2, uF.getDateFormat(strD2, CF.getStrReportDateFormat()));
				rs = pst.executeQuery();
				dtMin = null;
				dtMax = null;

				while (rs.next()) {
					if (dtMax == null) {
						dtMax = rs.getDate("_date");
					}
					dtMin = rs.getDate("_date");
					_alDate.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
					_alDay.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()));

					if (hmHolidays != null && hmHolidays.containsKey(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT))) {
						_alHolidays.add(i + "");
						_hmHolidaysColour.put(i + "", (String) hmHolidays.get(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)));
					}
					i++;
				}
				rs.close();
				pst.close();
			}

			Map<String, Map<String, String>> hmEarlyLateReporting = new HashMap<String, Map<String, String>>();
			new CommonFunctions(CF).getEarlyLateReporting(con, dtMin, dtMax, hmEarlyLateReporting);
			Map<String, String> hmRosterDetailsTemp = new HashMap<String, String>();
			Map<String, Map<String, String>> hmRosterHours = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmRosterServices = new HashMap<String, Map<String, String>>();
			new CommonFunctions(CF).getRosterHoursMap(con,dtMin, dtMax, hmRosterHours, hmRosterServices);
			Map<String, Map<String, String>> hmRosterDetails = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement(selectRosterDetails1);
			pst.setDate(1, dtMin);
			pst.setDate(2, dtMax);
			pst.setInt(3, uF.parseToInt(getStrSelectedEmpId()));
			rs = pst.executeQuery();

			String strOldEmpId = null;
			String strNewEmpId = null;

			while (rs.next()) {
				strNewEmpId = rs.getString("emp_id");
				if (strOldEmpId != null && !strOldEmpId.equalsIgnoreCase(strNewEmpId)) {
					hmRosterDetailsTemp = new HashMap<String, String>();
				}
				hmRosterDetailsTemp.put(uF.getDateFormat(rs.getString("_date"), "yyyy-MM-dd", CF.getStrReportDateFormat()), rs.getString("service_id"));
				hmRosterDetails.put(rs.getString("emp_id"), hmRosterDetailsTemp);
				strOldEmpId = strNewEmpId;
			}
			rs.close();
			pst.close();

			Map<String, String> hm = new HashMap<String, String>();
			Map<String, String> hmEmpCodeDesig = new HashMap<String, String>();

			pst = con.prepareStatement(selectPayrollPolicy1);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmPayrollPolicy = new HashMap<String, Map<String, String>>();

			Map<String, Map<String, String>> hmPayrollFT = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmPayrollPT = new HashMap<String, Map<String, String>>();

			Map<String, String> hmInner = new HashMap<String, String>();
			while (rs.next()) {
				hm = new HashMap<String, String>();

				hm.put("MONDAY", rs.getString("monamount"));
				hm.put("TUESDAY", rs.getString("tuesamount"));
				hm.put("WEDNESDAY", rs.getString("wedamount"));
				hm.put("THURSDAY", rs.getString("thursamount"));
				hm.put("FRIDAY", rs.getString("friamount"));
				hm.put("SATURDAY", rs.getString("satamount"));
				hm.put("SUNDAY", rs.getString("sunamount"));
				hm.put("FIXED", rs.getString("fxdamount"));
				hm.put("PAYMODE", rs.getString("paymode"));

				hmPayrollPolicy.put(rs.getString("desig_id"), hm);

				if (rs.getString("emptype") != null && rs.getString("emptype").equalsIgnoreCase("PT")) {
					hmPayrollPT.put("D" + rs.getString("desig_id") + "S" + rs.getString("service_id"), hm);
				} else {
					hmPayrollFT.put("D" + rs.getString("desig_id") + "S" + rs.getString("service_id"), hm);
				}

			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(selectEmployeeR3);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmEmpCodeDesig.put(rs.getString("emp_id"), rs.getString("designation_id"));
			}
			rs.close();
			pst.close();

			long _IN = 0L;
			long _OUT = 0L;
			long _TOTAL = 0L;
			boolean isIn = false;
			boolean isOut = false;
			boolean isInOut = false;

			String strOld = null;
			String strNew = null;
			Map hmManagerAttendenceReport = new HashMap();
			Map hmWorkedHours = new HashMap();

			Map hmManagerDailyRate = new HashMap();
			Map hmManagerServicesWorked = new HashMap();
			Map hmDailyRate = new HashMap();
			Map hmServicesWorked = new HashMap();

			List alDay = new ArrayList();
			List alDate = new ArrayList();
			List alInOut = new ArrayList();

			List alEmpCode = new ArrayList();
			List alEmpCodeLink = new ArrayList();

			if (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) || strUserType.equalsIgnoreCase(HRMANAGER)
					|| strUserType.equalsIgnoreCase(ACCOUNTANT)) {
				pst = con.prepareStatement(selectClockEntriesAdminR_N);
				pst.setDate(1, dtMin);
				pst.setDate(2, dtMax);
			} else if (strUserType.equalsIgnoreCase(MANAGER)) {
				pst = con.prepareStatement(selectClockEntriesManagerR);
				pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
				pst.setDate(2, dtMin);
				pst.setDate(3, dtMax);
			} else {
				return ACCESS_DENIED;
			}

			rs = pst.executeQuery();

			while (rs.next()) {
				String str = rs.getString("in_out");

				strNew = rs.getString("emp_id");

				if (strNew != null && !strNew.equals(strOld)) {
					hmWorkedHours = new HashMap();
					alDay = new ArrayList();
					alDate = new ArrayList();
				}

				strOld = strNew;

				if (!alEmpCode.contains(strNew)) {
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
				
					
					alEmpCodeLink.add("<a href=\"?EMPID=" + rs.getString("emp_id") + "\">" + rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + "</a>");
					alEmpCode.add(strNew);
				}

				String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), "yyyy-MM-dd HH:mm:ss", CF.getStrReportDateFormat());

				if (!alDate.contains(strDate)) {

					alDate.add(strDate);
					alDay.add(uF.getDateFormat(rs.getString("in_out_timestamp"), "yyyy-MM-dd HH:mm:ss", CF.getStrReportDayFormat()));
					_IN = 0L;
					_OUT = 0L;
					isIn = false;
					isOut = false;
					isInOut = false;

				}

				if (str != null && str.equalsIgnoreCase("IN") && !isIn) {
					_IN = uF.getTimeFormat(rs.getString("in_out_timestamp"), "yyyy-MM-dd HH:mm:ss").getTime();
					isIn = true;
				} else if (str != null && str.equalsIgnoreCase("OUT") && !isOut) {
					_OUT = uF.getTimeFormat(rs.getString("in_out_timestamp"), "yyyy-MM-dd HH:mm:ss").getTime();
					isOut = true;
				}

				if (_IN > 0 && _OUT > 0 && !isInOut) {

					// _TOTAL += (((_OUT - _IN) > 0) ? (_OUT - _IN) : 0);
					_TOTAL += uF.getTimeDifference(_IN, _OUT);

					isInOut = true;

					// if (hmWorkedHours != null &&
					// !hmWorkedHours.containsKey(strDate)) {
					// hmWorkedHours.put(strDate, uF.getTimeDiffInHoursMins(_IN,
					// _OUT));
					// isInOut = true;
					// }

					// String strDesigId = (String)hmEmpDesig.get(strNew);
					// Map hmRates = (Map)hmEmpRates.get(strDesigId);
					// String strRate = (String)
					// hmRates.get((uF.getDateFormat(strDate, ReportDateFormat,
					// ReportDayFormat)).toUpperCase());
					// hmDailyRate.put(strDate, strRate);

					String strDesig = (String) hmEmpCodeDesig.get(strNew);

					Map hmTemp = (HashMap) hmRosterDetails.get(strNew);

					String strServiceId = null;
					// String strServiceName = null;
					if (hmTemp != null) {
						strServiceId = (String) hmTemp.get(strDate);
						// strServiceName = (String) hmServices.get((String)
						// hmTemp.get(strDate));
					}
					// if(strServiceName!=null){
					// strServiceName = " ("+strServiceName+")";
					// }else{
					// strServiceName="";
					// }

					Map hmRate = null;
					String strEmpType = (String) hmEmpType.get(strNew);

					if (strEmpType != null && strEmpType.equalsIgnoreCase("PT")) {
						hmRate = (HashMap) hmPayrollPT.get("D" + strDesig + "S" + strServiceId);
					} else {
						hmRate = (HashMap) hmPayrollFT.get("D" + strDesig + "S" + strServiceId);
					}

					String strRate = null;
					if (hmRate != null) {
						strRate = (String) hmRate.get(uF.getDateFormat(strDate, CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase());
					}
					double dblRate = uF.parseToDouble(strRate);
					hmDailyRate.put(strDate, strRate);

					// hmServicesWorked.put(strDate, strServiceName);

					// hmWorkedHours.put(strDate, uF.getTimeDiffInHoursMins(_IN,
					// _OUT) +strServiceName);
					hmWorkedHours.put(strDate, uF.getTimeDiffInHoursMins(_IN, _OUT));

				}

				Map hmTemp = (HashMap) hmManagerAttendenceReport.get(rs.getString("emp_id"));
				if (hmTemp == null || (hmTemp != null && !hmTemp.containsKey(strDate))) {
					hmManagerAttendenceReport.put(rs.getString("emp_id"), hmWorkedHours);
					hmManagerDailyRate.put(rs.getString("emp_id"), hmDailyRate);
					hmManagerServicesWorked.put(rs.getString("emp_id"), hmServicesWorked);
				}

			}
			rs.close();
			pst.close();

			request.setAttribute("alDate", _alDate);
			request.setAttribute("alDay", _alDay);
			request.setAttribute("hmManagerAttendenceReport", hmManagerAttendenceReport);
			request.setAttribute("alEmpCode", alEmpCode);
			request.setAttribute("alEmpCodeLink", alEmpCodeLink);
			request.setAttribute("_alHolidays", _alHolidays);
			request.setAttribute("_hmHolidaysColour", _hmHolidaysColour);
			request.setAttribute("hmManagerServicesWorked", hmManagerServicesWorked);
			request.setAttribute("hmRosterHoursDetails", hmRosterHours);
			request.setAttribute("hmRosterServices", hmRosterServices);

			if (strUserType != null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
				request.setAttribute("hmEarlyLateReporting", hmEarlyLateReporting);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillPayCycles> getPayCycleList() {
		return payCycleList;
	}

	public void setPayCycleList(List<FillPayCycles> payCycleList) {
		this.payCycleList = payCycleList;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}

	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
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

	public List<FillWLocation> getWorkLocationList() {
		return workLocationList;
	}

	public void setWorkLocationList(List<FillWLocation> workLocationList) {
		this.workLocationList = workLocationList;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getPayCycleNo() {
		return payCycleNo;
	}

	public void setPayCycleNo(String payCycleNo) {
		this.payCycleNo = payCycleNo;
	}

}
