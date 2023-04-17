package com.konnect.jpms.roster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.reports.RosterReport;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ShiftRosterReport extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	List<FillShift> shiftList;
	boolean isEmpUserType = false; 
	 
	String strAlphaValue = null;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(ShiftRosterReport.class);
	String f_org;
	String f_strWLocation;
	String f_department;
	String f_level;
	String f_service;

	List<FillOrganisation> organisationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	String strMonth;
	String strYear;
	String strWLocation;
	List<FillMonth> monthList;
	List<FillYears> yearList;
	List<FillWLocation> wLocationList;
	
	public String execute() throws Exception { 
		
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		 
		strAlphaValue = (String)request.getParameter("alphaValue");
		
		isEmpUserType = false;
		if (strUserType != null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
			request.setAttribute(PAGE, "/jsp/roster/ShiftRosterReportE.jsp");
			request.setAttribute(TITLE, TViewMyRoster);
			isEmpUserType = true;
		} else {
			request.setAttribute(PAGE, "/jsp/roster/ShiftRosterReport.jsp");
			request.setAttribute(TITLE, TViewRoster);
		}
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}

		viewRoster(uF);
		shiftDetails(uF);
		return loadRoster(uF);
	}

	public String loadRoster(UtilityFunctions uF) {
		paycycleList = new FillPayCycles(request).fillPayCycles(CF);
		
		monthList = new FillMonth().fillMonth();
		yearList = new FillYears().fillFutureYears(uF.getCurrentDate(CF.getStrTimeZone()), 2, 2);
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		
		return LOAD;
	}

	public String viewRoster(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {   
			
//			shiftList = new FillShift(request).fillShift();
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
//			Map<String, String> hmWeekEnds = CF.getWeekEndList();
			Map<String, String> hmWLocation = CF.getEmpWlocationMap(con);
			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
			List<String> _alHolidays = new ArrayList<String>();
			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
			Map<String, Map<String, String>> hmServicesWorkrdFor = new HashMap<String, Map<String, String>>();
			Map<String, String> hmServices = CF.getServicesMap(con,true);

//			List<List<String>> al = new ArrayList<List<String>>();
//			List<String> alDay = new ArrayList<String>();
//			List<String> alDate = new ArrayList<String>();
			List<String> alEmpId = new ArrayList<String>();

			List<String> _alDay = new ArrayList<String>();
			List<String> _alDate = new ArrayList<String>();

			List<String> _alDayF = new ArrayList<String>();
			List<String> _alDateF = new ArrayList<String>();
			
			String[] strPayCycleDates = null;

			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
			}			
			
			
			if(getStrMonth() ==null){
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
				setStrYear(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"))+"");
			}
			
			
			Map<String, List<String>> hmShiftCodeFromId = getShiftCodeFromId(con,uF);
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, strPayCycleDates[0], strPayCycleDates[1], CF, uF,null,null);
			
			/*pst = con.prepareStatement(selectDates);
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
			int i=0;
			while(rs.next()){
				
				_alDay.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()));
				_alDate.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
				
				_alDayF.add(uF.getDateFormat(rs.getString("_date"), DBDATE, "EEE"));
				_alDateF.add(uF.getDateFormat(rs.getString("_date"), DBDATE, "dd-MMM"));
				
				
				if (hmHolidayDates != null && hmHolidayDates.containsKey(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT))) {
					_alHolidays.add(i + "");
					_hmHolidaysColour.put(i + "", (String) hmHolidayDates.get(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)));
				}
				i++;
			}
			
			pst.close();
	*/
			
			
			
			
//			System.out.println("getStrMonth()===>"+getStrMonth());
//			System.out.println("getStrYear()===>"+getStrYear());
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1 );
			cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
			
			int maxDays = cal.getActualMaximum(Calendar.DATE);
			int minDays = cal.getActualMinimum(Calendar.DATE);
			
			
			cal.set(Calendar.DAY_OF_MONTH, minDays);
			
			
			String strD1 = null;
			for(int ii=0; ii<maxDays; ii++){
				
				strD1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"+ cal.get(Calendar.YEAR);
				
				_alDay.add(uF.getDateFormat(strD1, DATE_FORMAT, CF.getStrReportDayFormat()));
				_alDate.add(uF.getDateFormat(strD1, DATE_FORMAT, CF.getStrReportDateFormat()));
				
				_alDayF.add(uF.getDateFormat(strD1, DATE_FORMAT, "EEE"));
				_alDateF.add(uF.getDateFormat(strD1, DATE_FORMAT, "dd"));
				cal.add(Calendar.DATE, 1);
			}
			
			
			
			Map<String, String> hmHolidays = new HashMap<String, String>();
			Map<String, String> hmHolidayDates = new HashMap<String, String>();
			CF.getHolidayList(con,request,uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat(), DATE_FORMAT), uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat(), DATE_FORMAT), CF, hmHolidayDates, hmHolidays, true);
			
			for(int ii=0; ii<_alDate.size(); ii++){
				if (hmHolidayDates != null && hmHolidayDates.containsKey(uF.getDateFormat((String)_alDate.get(ii), CF.getStrReportDateFormat(), DATE_FORMAT))) {
					_alHolidays.add(ii + "");
					_hmHolidaysColour.put(ii + "", (String) hmHolidayDates.get(uF.getDateFormat((String)_alDate.get(ii), CF.getStrReportDateFormat(), DATE_FORMAT)));
				}
			}
			
			
			Map<String, Map<String, String>> hmWorkLocationInfo = CF.getWorkLocationMap(con);
			Map<String, String> hmEmpWorkLocationInfo = CF.getEmpWlocationMap(con);
			
			
			
//			Map<String, Map<String, String>> hmLeavesMap = CF.getLeaveDates(con,uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat(), DATE_FORMAT), uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat(), DATE_FORMAT), CF, null, false, null);
			Map<String, Map<String, String>> hmLeavesMap = CF.getActualLeaveDates(con, CF, uF, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat(), DATE_FORMAT), uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat(), DATE_FORMAT), null, false, null);
			Map<String, String> hmLeavesColour = new HashMap<String, String>();
			CF.getLeavesAttributes(con, uF, hmLeavesColour, null);
			
			 
			Map hm = new HashMap();
			Map<String, String> hm1 = new HashMap<String, String>();
			Map<String, String> hm2 = new HashMap<String, String>();
			Map<String, String> hm3 = new HashMap<String, String>();

			if (strUserType != null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
				pst = con.prepareStatement(selectRosterDetailsV);
				pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID))); 
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			} else if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) || strUserType.equalsIgnoreCase(HRMANAGER) )) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("SELECT emp_per_id, rd.emp_id, rd.service_id,roster_id, _date, _from, _to, rd.shift_id, emp_fname,emp_mname, emp_lname FROM roster_details rd, employee_personal_details epd, employee_official_details eod WHERE eod.emp_id = epd.emp_per_id and eod.emp_id =rd.emp_id and rd.emp_id = epd.emp_per_id and _date between ? and ? and rd.service_id>0 ");
				
				if(uF.parseToInt(getF_department())>0){
					sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));	
				}
				if(uF.parseToInt(getF_service())>0){   
					sbQuery.append(" and rd.service_id  ="+uF.parseToInt(getF_service()));	
				}
				if(uF.parseToInt(getF_level())>0){
					sbQuery.append(" and grade_id in (select grade_id from grades_details gd, level_details ld, designation_details dd where ld.level_id = dd.level_id and dd.designation_id = gd.designation_id and dd.level_id = "+uF.parseToInt(getF_level())+") ");	
				}
				if(uF.parseToInt(getF_strWLocation())>0){
	                sbQuery.append(" and wlocation_id ="+uF.parseToInt(getF_strWLocation()));
	            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				
				sbQuery.append("  order by emp_fname, rd.emp_id, _date desc, _from");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat()));
				pst.setDate(2, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
			} else if (strUserType != null && strUserType.equalsIgnoreCase(MANAGER)) {
				pst = con.prepareStatement(selectRosterDetails_M);
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
			}else{
				return ACCESS_DENIED;
			}

			
			
			rs = pst.executeQuery();

			StringBuilder sb = new StringBuilder();
			List<String> alServices = new ArrayList<String>();
			
			  
			while (rs.next()) {
				List<String> alShift = new ArrayList<String>();
				
				if (strUserType != null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
					
					
					
					if (!alEmpId.contains(rs.getString("emp_per_id"))) {
						alEmpId.add(rs.getString("emp_per_id"));
					}

					hm1 =(Map<String, String>)  hm.get(rs.getString("emp_per_id"));
					if (hm1 == null) {
						hm1 = new HashMap<String, String>();
						hm2 = new HashMap<String, String>();
					}

					
					List<String> alCode = hmShiftCodeFromId.get(rs.getString("shift_id"));
					if(alCode==null)alCode=new ArrayList<String>();
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					hm1.put("EMPNAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
					hm1.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "FROM" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), (String)alCode.get(0));

					
					hm2.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), (String) hmServices.get(rs.getString("service_id")));
					hm2.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"ROSTER_ID", rs.getString("roster_id"));

					hm.put(rs.getString("emp_per_id"), hm1);

					hmServicesWorkrdFor.put(rs.getString("emp_per_id"), hm2);
					
					
					
					
				}else {
					
					hm3 =  (Map<String, String>)hm.get(rs.getString("emp_per_id")+"_"+rs.getString("service_id"));
					if(hm3==null){
						hm3  = new HashMap<String, String>();
					}
					
					if (!alEmpId.contains(rs.getString("emp_per_id"))) {
						alEmpId.add(rs.getString("emp_per_id"));
					}

					if (hm1 == null) {
						hm1 = new HashMap<String, String>();
						hm2 = new HashMap<String, String>();
					}

					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					hm1.put("EMPNAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
					hm1.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "FROM" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));

					
					hm2.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), (String) hmServices.get(rs.getString("service_id")));
					hm2.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"_"+rs.getString("service_id")+"ROSTER_ID", rs.getString("roster_id"));


					hmServicesWorkrdFor.put(rs.getString("emp_per_id"), hm2);
					
					List <String>wlocationData=null;
					
					alShift = hmShiftCodeFromId.get(rs.getString("shift_id"));
					if(alShift==null)alShift=new ArrayList<String>();
//					
//					System.out.println("alShift= 11 ===>"+alShift);
//					
//					
//					alShift = ShiftCodeFromId(rs.getInt("shift_id"));
//					
//					System.out.println("alShift= 22 ===>"+alShift);
					
					String strWlocationId = null;
					
					hm3.put("EMPNAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
					if(rs!=null && rs.getString("shift_id")!=null && rs.getString("shift_id").equalsIgnoreCase("1")){
						
						strWlocationId = (String)hmEmpWorkLocationInfo.get(rs.getString("emp_per_id"));
						
						//wlocationData = workLocationInfo(uF.parseToInt(rs.getString("emp_per_id")));
					}
					
					if(strWlocationId==null && alShift!=null && alShift.size()>0){
						
						hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "FROM" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), alShift.get(0));
						hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "TO" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), alShift.get(2)+" "+uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat())+"-"+uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
					}else if(strWlocationId!=null){
						
						Map<String, String> hmLocationInfo = hmWorkLocationInfo.get(strWlocationId);
						if(hmLocationInfo==null)hmLocationInfo=new HashMap<String, String>();
						
						
						hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "FROM" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), alShift.get(0));
						hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "TO" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), alShift.get(0)+"  "+uF.getDateFormat((String)hmLocationInfo.get("WL_START_TIME"), DBTIME, CF.getStrReportTimeFormat())+"-"+uF.getDateFormat((String)hmLocationInfo.get("WL_END_TIME"), DBTIME, CF.getStrReportTimeFormat()));
						
						
						
//						if(wlocationData.size()!=0 && alShift!=null && alShift.size()>0){
//							
//							
//							hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "FROM" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), alShift.get(0));
//							hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "TO" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), alShift.get(0)+"  "+uF.getDateFormat(wlocationData.get(0), DBTIME, CF.getStrReportTimeFormat())+"-"+uF.getDateFormat(wlocationData.get(1), DBTIME, CF.getStrReportTimeFormat()));
//						}else if(alShift!=null && alShift.size()>0){
//							hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "FROM" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), alShift.get(0));
//							hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "TO" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), alShift.get(2)+"  "+"NA");
//						}
					}
					hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), (String) hmServices.get(rs.getString("service_id")));
					hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"_"+rs.getString("service_id")+"ROSTER_ID", rs.getString("roster_id"));

					hm.put(rs.getString("emp_per_id")+"_"+rs.getString("service_id"), hm3);
					
					alServices =(List<String>) hm.get(rs.getString("emp_per_id"));
					if(alServices==null){
						alServices = new ArrayList<String>();
					}
					
					if(!alServices.contains(rs.getString("service_id"))){
						alServices.add(rs.getString("service_id"));
					}
					
					
					hm.put(rs.getString("emp_per_id"), alServices);
					
				}
				

				
				
				int nOrgId = uF.parseToInt(hmEmpOrgId.get(rs.getString("emp_per_id")));
				shiftList = new FillShift(request).fillShiftByOrg(nOrgId);

				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				sb.append(""+			
				"<div id=\"popup_name"+rs.getString("roster_id")+"\" class=\"popup_block posfix\">" +
				"<a href=\"javascript:void(0)\" onclick=\"hideBlock('popup_name"+rs.getString("roster_id")+"');\" class=\"close\"><img src=\""+request.getContextPath()+"/images/close_pop.png\" class=\"btn_close\" title=\"Close Window\" alt=\"Close\" /></a>"+
						"<h2 class=\"alignCenter\">Roster of "+rs.getString("emp_fname")+strEmpMName+" " + rs.getString("emp_lname")+" for "+uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"</h2><br/>"+
				"<form action=\"UpdateShiftRosterReport.action\">"+
				"<table cellpadding=\"0\" cellspacing=\"0\" align=\"center\" width=\"250px\">"+				
				"<tr>"+
				"<td style=\"color:white\" align=\"right\">Select Shift</td><td class=\"\" align=\"center\"> <select name=\"shift_id\" style=\"width:85px\" align=\"center\">");
				for (int j=0; shiftList!=null && j<shiftList.size(); j++) {
					if( alShift!=null && alShift.size()>=2 && alShift.get(2).equalsIgnoreCase(((FillShift)shiftList.get(j)).getShiftCode())){
						sb.append("<option value="+((FillShift)shiftList.get(j)).getShiftId()+" selected=\"selected\"> "+((FillShift)shiftList.get(j)).getShiftCode()+"</option>"); 
					}else{
						sb.append("<option value="+((FillShift)shiftList.get(j)).getShiftId()+" > "+((FillShift)shiftList.get(j)).getShiftCode()+"</option>"); 
					}		
				}
				
				
				
				sb.append("</select> </td>"+
				"</tr>" );
				if( alShift!=null && alShift.size()>=2 && "Custom".equalsIgnoreCase(alShift.get(2))){
					sb.append("<tr>"+
					"<td class=\"reportHeading\">Start Time</td><td class=\"reportHeading\">End Time</td>"+
					"</tr><tr>"+
					"<td class=\"\"><input style=\"width:100px\" name=\"_from\" type=\"text\" value=\""+uF.showData(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()),"")+"\" ></td><td class=\"reportLabel\"><input style=\"width:100px\" name=\"_to\" type=\"text\" value=\""+uF.showData(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()),"")+"\" ></td>"+
					"</tr>");
				}
				sb.append("<tr>"+
				"<td height=\"60px\"  class=\"\" colspan=\"2\" align=\"center\" ><input name=\"UPD\" type=\"submit\" value=\"Update\" class=\"input_button\">&nbsp;" +
				"<input onclick=\"return confirm('Are you sure you want to delete this roster entry of "+rs.getString("emp_fname")+strEmpMName+" " + rs.getString("emp_lname")+" for "+uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"?')\" type=\"submit\" name=\"DEL\" value=\"Delete\" class=\"input_button\">"+
				"<input name=\"roster_id\" type=\"hidden\" value=\""+rs.getString("roster_id")+"\" ></td>"+
				"</tr>"+
				"</table>"+
				"</form></div>");

			}	
			rs.close();
			pst.close();

			Map<String, String> hmRosterServiceName = new HashMap<String, String>();
			Map<String, Map<String, String>> hmRosterServiceId = new HashMap<String, Map<String, String>>();
			List<String> alServiceId = new ArrayList<String>();
			
			
			CF.getRosterServicesIDList(con,CF,strPayCycleDates[0], strPayCycleDates[1], hmRosterServiceName, hmRosterServiceId, alServiceId);
			hmRosterServiceName = CF.getServicesMap(con,true);
			
			
			pst = con.prepareStatement("select service_id from employee_official_details where emp_id = ?");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
	
			String []arrServiceId = null;
			if (rs.next()) {
				if(rs.getString("service_id")!=null){
					arrServiceId = rs.getString("service_id").split(",");
				}
			}	
			rs.close();
			pst.close();
			
			for(int ii=0; arrServiceId!=null && ii<arrServiceId.length; ii++){
				if(!alServiceId.contains(arrServiceId[ii])){
					alServiceId.add(arrServiceId[ii]);
				}
			}
			

			request.setAttribute("alDay", _alDay);
			request.setAttribute("alDate", _alDate);
			request.setAttribute("alDayF", _alDayF);
			request.setAttribute("alDateF", _alDateF);
			request.setAttribute("alEmpId", alEmpId);
			request.setAttribute("hmList", hm);
			request.setAttribute("_alHolidays", _alHolidays);
			request.setAttribute("hmHolidays", hmHolidays);
			request.setAttribute("hmHolidayDates", hmHolidayDates);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmWeekEnds", hmWeekEnds);
			
			request.setAttribute("hmServicesWorkrdFor", hmServicesWorkrdFor);
			request.setAttribute("hmRosterServiceId", hmRosterServiceId);
			request.setAttribute("hmRosterServiceName", hmRosterServiceName);
			request.setAttribute("alServiceId", alServiceId);
			request.setAttribute("hmServices", hmServices);
			request.setAttribute("empRosterDetails", sb.toString());
			request.setAttribute("paycycleDuration",CF.getStrPaycycleDuration());
			request.setAttribute("hmLeavesMap",hmLeavesMap);
			request.setAttribute("hmLeavesColour",hmLeavesColour);
			
			
			request.setAttribute("CC", new FillServices(request).fillServicesHtml());
			
			request.setAttribute("hmEmpOrgId", hmEmpOrgId);
			
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	
	public Map<String, List<String>> getShiftCodeFromId(Connection con, UtilityFunctions uF){
		
		Map<String, List<String>> hmShiftDetails = new HashMap<String, List<String>>();
		List<String> alCode=new ArrayList<String>();
		PreparedStatement pst=null;
		ResultSet rs= null;
		try {
				pst = con.prepareStatement("SELECT * FROM shift_details ");
				rs = pst.executeQuery();
				while(rs.next()){
					alCode=new ArrayList<String>();
					
					if(rs.getString("shift_code")!=null && rs.getString("shift_code").equalsIgnoreCase("ST")){
						alCode.add(rs.getString("shift_code"));
					}else{
						alCode.add(("<div style=\"height:19px;  background-color:"+rs.getString("colour_code")+"\">"+rs.getString("shift_code")+"</div>"));
					}
					alCode.add(rs.getString("shift_type"));
					alCode.add(rs.getString("shift_code"));
					
					hmShiftDetails.put(rs.getString("shift_id"), alCode);
					
				}	
				rs.close();
				pst.close();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmShiftDetails;
	}
	
	public void shiftDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		List<String> shiftDetails =new ArrayList<String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM shift_details where org_id=? order by shift_code");
			pst.setInt(1, uF.parseToInt(getF_org()));
			rs = pst.executeQuery();
			while(rs.next()){
				if(!(rs.getString("shift_code").equalsIgnoreCase("ST"))){
					shiftDetails.add(rs.getString("colour_code"));
					shiftDetails.add(rs.getString("shift_code"));
					shiftDetails.add(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
					shiftDetails.add(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
					shiftDetails.add(uF.getDateFormat(rs.getString("break_start"), DBTIME, CF.getStrReportTimeFormat()));
					shiftDetails.add(uF.getDateFormat(rs.getString("break_end"), DBTIME, CF.getStrReportTimeFormat()));
				}
			}	
			rs.close();
			pst.close();
			request.setAttribute("shiftDetails", shiftDetails);
		} catch (Exception e) {
			e.printStackTrace();
//					
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	String paycycle;
	List<FillPayCycles> paycycleList;
	
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

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}

	public String getStrWLocation() {
		return strWLocation;
	}

	public void setStrWLocation(String strWLocation) {
		this.strWLocation = strWLocation;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillYears> getYearList() {
		return yearList;
	}

	public void setYearList(List<FillYears> yearList) {
		this.yearList = yearList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getF_service() {
		return f_service;
	}

	public void setF_service(String f_service) {
		this.f_service = f_service;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}


}
