package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LeaveRegister  extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(LeaveRegister.class);
	
	String strLocation;
	String strDepartment;
	String strSbu;
	
	String paycycle;
	String strPaycycleDuration;
	String strMonth;
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_service;
	String f_org;
	
	List<FillOrganisation> organisationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillPayCycles> paycycleList;
	List<FillMonth> monthList;
	List<FillWLocation> wLocationList;
	
	String calendarYear; 
	List<FillCalendarYears> calendarYearList;
	
	public String execute() throws Exception {
      
		session = request.getSession(); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		  
		request.setAttribute(TITLE, TLeaveRegister);
		request.setAttribute(PAGE, PLeaveRegister);
		
		UtilityFunctions uF = new UtilityFunctions();

		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(ACCOUNTANT)
			&& !strUserType.equalsIgnoreCase(MANAGER) && !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(CEO) 
			&& !strUserType.equalsIgnoreCase(RECRUITER))) {
		 	
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED;
		}
		
		if(getF_org()==null || getF_org().trim().equals("")){
			setF_org((String)session.getAttribute(ORGID));
		}

		if (getStrPaycycleDuration() == null || getStrPaycycleDuration().trim().equals("") || getStrPaycycleDuration().trim().equalsIgnoreCase("NULL")) {
			setStrPaycycleDuration("M");
		}
		
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		
		viewLeaveRegister(uF);

		return loadLeaveRegister(uF);

	}
	
	
	public String loadLeaveRegister(UtilityFunctions uF) {
		monthList = new FillMonth().fillMonth();
		calendarYearList = new FillCalendarYears(request).fillCalendarYears(CF);
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		paycycleList = new FillPayCycles(getStrPaycycleDuration(), request).fillPayCycles(CF, getF_org());
		getSelectedFilter(uF);
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if(strUserType != null && !strUserType.equals(MANAGER)) {
			alFilter.add("ORGANISATION");
			if(getF_org()!=null) {
				String strOrg="";
				for(int i=0;organisationList!=null && i<organisationList.size();i++) {
					if(getF_org().equals(organisationList.get(i).getOrgId())) {
						strOrg=organisationList.get(i).getOrgName();
					}
				}
				if(strOrg!=null && !strOrg.equals("")) {
					hmFilter.put("ORGANISATION", strOrg);
				} else {
					hmFilter.put("ORGANISATION", "All Organisations");
				}
			} else {
				hmFilter.put("ORGANISATION", "All Organisations");
			}
			
			
			alFilter.add("LOCATION");
			if(getF_strWLocation()!=null) {
				String strLocation="";
				int k=0;
				for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
					for(int j=0;j<getF_strWLocation().length;j++) {
						if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
							if(k==0) {
								strLocation=wLocationList.get(i).getwLocationName();
							} else {
								strLocation+=", "+wLocationList.get(i).getwLocationName();
							}
							k++;
						}
					}
				}
				if(strLocation!=null && !strLocation.equals("")) {
					hmFilter.put("LOCATION", strLocation);
				} else {
					hmFilter.put("LOCATION", "All Locations");
				}
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
			
			
			alFilter.add("DEPARTMENT");
			if(getF_department()!=null) {
				String strDepartment="";
				int k=0;
				for(int i=0;departmentList!=null && i<departmentList.size();i++) {
					for(int j=0;j<getF_department().length;j++) {
						if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
							if(k==0) {
								strDepartment=departmentList.get(i).getDeptName();
							} else {
								strDepartment+=", "+departmentList.get(i).getDeptName();
							}
							k++;
						}
					}
				}
				if(strDepartment!=null && !strDepartment.equals("")) {
					hmFilter.put("DEPARTMENT", strDepartment);
				} else {
					hmFilter.put("DEPARTMENT", "All Departments");
				}
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
			
			
			alFilter.add("SERVICE");
			if(getF_service()!=null) {
				String strService="";
				int k=0;
				for(int i=0;serviceList!=null && i<serviceList.size();i++) {
					for(int j=0;j<getF_service().length;j++) {
						if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
							if(k==0) {
								strService=serviceList.get(i).getServiceName();
							} else {
								strService+=", "+serviceList.get(i).getServiceName();
							}
							k++;
						}
					}
				}
				if(strService!=null && !strService.equals("")) {
					hmFilter.put("SERVICE", strService);
				} else {
					hmFilter.put("SERVICE", "All SBUs");
				}
			} else {
				hmFilter.put("SERVICE", "All SBUs");
			}
		}
		
		alFilter.add("CALENDARYEAR");
		if(getCalendarYear() != null && !getCalendarYear().trim().equals("") && !getCalendarYear().trim().equalsIgnoreCase("NULL")) {
			String strCal = "";
			for(int i=0;calendarYearList!=null && i<calendarYearList.size();i++) {
				if(getCalendarYear().equals(calendarYearList.get(i).getCalendarYearId())) {
					strCal = calendarYearList.get(i).getCalendarYearName();
				}
			}
			if(strCal!=null && !strCal.equals("")) {
				hmFilter.put("CALENDARYEAR", strCal);
			} else {
				hmFilter.put("CALENDARYEAR", "-");
			}
		} else {
			hmFilter.put("CALENDARYEAR", "-");
		}
		
		alFilter.add("MONTH");
		int nselectedMonth = uF.parseToInt(getStrMonth());
		String strMonth = uF.getMonth(nselectedMonth);
		hmFilter.put("MONTH", strMonth);
		
		String selectedFilter= CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	
	public String viewLeaveRegister(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String> hmServiceMap =  CF.getServicesMap(con, true);
			Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap(con);
			
			/*String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;

			if (getCalendarYear() != null && !getCalendarYear().trim().equals("") && !getCalendarYear().trim().equalsIgnoreCase("NULL")) {
				strCalendarYearDates = getCalendarYear().split("-");
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			} else {
				strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			}
			
			if(getStrMonth() == null){
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			
			int nselectedMonth = uF.parseToInt(getStrMonth());
			int nFYSMonth = uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "MM"));
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			if(nselectedMonth>=nFYSMonth) {
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "yyyy")));
			} else {
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, "yyyy")));
			}*/
			
			String[] strPayCycleDates = null;
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getPaycycle().split("-");
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			
			if(getStrMonth() ==null){
				setStrMonth(uF.parseToInt(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, "MM"))+"");
//				setStrYear(uF.parseToInt(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, "yyyy"))+"");
			}
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1 );
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, "yyyy")));
			int minDays = uF.parseToInt(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, "dd"));
			cal.set(Calendar.DAY_OF_MONTH, minDays);
		
			String daysDiff = uF.dateDifference(strPayCycleDates[0], DATE_FORMAT, strPayCycleDates[1], DATE_FORMAT);
			int maxDays = uF.parseToInt(daysDiff);
			
			
//			int minDays = cal.getActualMinimum(Calendar.DATE);
//			cal.set(Calendar.DAY_OF_MONTH, minDays); 
//			int maxDays = cal.getActualMaximum(Calendar.DATE);
			
			String strD1 = null;
			List<String> alDates = new ArrayList<String>();
			for(int i=0; i<maxDays; i++){
				strD1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
				
				alDates.add(uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT));
				cal.add(Calendar.DATE, 1);
			}
			
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con,  alDates.get(0), alDates.get(alDates.size()-1), CF, uF,hmWeekEndHalfDates,null);
			Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);			
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, alDates.get(0), alDates.get(alDates.size()-1),alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEnds,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
			
			Map<String,String> hmHolidays = new HashMap<String,String>();
			Map<String,String> hmHolidayDates = new HashMap<String,String>();
			CF.getHolidayList(con,request,alDates.get(0), alDates.get(alDates.size()-1), CF, hmHolidayDates, hmHolidays, true);
			Map<String, String> hmHolidayName = CF.getHolidayName(con, CF,alDates.get(0), alDates.get(alDates.size()-1));
			if(hmHolidayName == null) hmHolidayName = new HashMap<String, String>(); 
			
			List<String> alEmployees  = new ArrayList<String>();
			List<String> alLegends = new ArrayList<String>();
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and epd.is_delete=false " +
			"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) ");
			if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))){
				sbQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			}
			
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            }
            
            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}			
			
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString()); 
			pst.setDate(1, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				alEmployees.add(rs.getString("emp_per_id"));
			}
			rs.close();
			pst.close();
			
//			Map hmLeavesRegister = CF.getLeaveDates(con, (String)alDates.get(0), (String)alDates.get(alDates.size()-1), CF, null, false, null);
			Map hmLeavesRegister = getLeaveDetails(con,alDates.get(0),alDates.get(alDates.size()-1),uF);
			Map hmLeavesColour = new HashMap();
			Map hmLeavesName = new HashMap();
			CF.getLeavesAttributes(con, uF, hmLeavesColour, hmLeavesName);
			
			Map<String, Map<String, String>> hmTravelRegister = CF.getTravelDetails(con,uF,alDates.get(0),alDates.get(alDates.size()-1));
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			List<List<String>> reportListPrint = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			List<String> alInnerPrint = new ArrayList<String>();
			
			for (int i=0; i<alEmployees.size(); i++){
				
				String strEmpId = (String)alEmployees.get(i);
				
				alInner = new ArrayList<String>();
				alInnerPrint = new ArrayList<String>();
				
				String strWLocationId = (String)hmEmpWlocation.get((String)alEmployees.get(i));
				
				alInner.add(uF.showData((String)hmEmpCode.get((String)alEmployees.get(i)),""));
				alInnerPrint.add(uF.showData((String)hmEmpCode.get((String)alEmployees.get(i)),""));
				
				alInner.add(uF.showData((String)hmEmpName.get((String)alEmployees.get(i)),""));
				alInnerPrint.add(uF.showData((String)hmEmpName.get((String)alEmployees.get(i)),""));
				
//				String level=hmEmpLevelMap.get((String)alEmployees.get(i));
//				Set<String> weeklyOffSet=hmWeekEnds.get(level);
				Set<String> weeklyOffSet= hmWeekEnds.get(strWLocationId);
				if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
				
				Map hmLeaves = (Map)hmLeavesRegister.get(strEmpId);
				if(hmLeaves==null)hmLeaves = new HashMap();
				
				Map<String, String> hmTravels = (Map<String, String>)hmTravelRegister.get(strEmpId);
				if(hmTravels == null) hmTravels = new HashMap<String, String>();
				
				Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get((String)alEmployees.get(i));
				if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
				
				Set<String> halfDayWeeklyOffSet= hmWeekEndHalfDates.get(strWLocationId);
				if(halfDayWeeklyOffSet==null) halfDayWeeklyOffSet=new HashSet<String>();
				
				for (int ii=0; ii<alDates.size(); ii++){
					
					
					//String strWeekDay = uF.getDateFormat((String)alDates.get(ii), DATE_FORMAT, "EEEE");
					String strWeekDay = uF.getDateFormat((String)alDates.get(ii), DATE_FORMAT, DATE_FORMAT);
					if(strWeekDay!=null){
						strWeekDay = strWeekDay.toUpperCase();
					}
			
					java.util.Date dtDate = uF.getDateFormatUtil((String)alDates.get(ii), DATE_FORMAT);
					java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
					
					String strLeaveCode = (String)hmLeaves.get((String)alDates.get(ii));
					String strColour = (String)hmLeavesColour.get(strLeaveCode);
					
					
					
					if(strColour==null){
						if(alEmpCheckRosterWeektype.contains(alEmployees.get(i))){
							if(rosterWeeklyOffSet.contains(alDates.get(ii))){
								strColour=WEEKLYOFF_COLOR;
							}
						}else if(weeklyOffSet.contains(alDates.get(ii))){
							strColour=WEEKLYOFF_COLOR;
						}else if(halfDayWeeklyOffSet.contains(alDates.get(ii))){
							strColour=WEEKLYOFF_COLOR;
						}
					}
					
//					alInner.add("<div style=\"width:100%;height:100%;text-align:center;background-color:"+strColour+"\">"+uF.showData((String)hmLeaves.get((String)alDates.get(ii)), "&nbsp;")+"</div>");
//					alInnerPrint.add(uF.showData((String)hmLeaves.get((String)alDates.get(ii)), ""));
					
					if(hmLeaves.containsKey((String)alDates.get(ii))){						
						alInner.add("<div style=\"width:100%;height:100%;text-align:center;background-color:"+strColour+"\">"+uF.showData((String)hmLeaves.get((String)alDates.get(ii)), "&nbsp;")+"</div>");
						alInnerPrint.add(uF.showData((String)hmLeaves.get((String)alDates.get(ii)), ""));						
					} else if(hmTravels.containsKey((String)alDates.get(ii))){						
						alInner.add("<div style=\"width:100%;height:100%;text-align:center;background-color:green;\">"+uF.showData((String)hmTravels.get((String)alDates.get(ii)), "&nbsp;")+"</div>");
						alInnerPrint.add(uF.showData((String)hmTravels.get((String)alDates.get(ii)), ""));
						
						if(!alLegends.contains("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"padding:1px;display: inline-block;margin-right:5px;width:38px;height:100%;background-color:green;text-align:center\" class=\"blueColor\">T</div>Travel Leave</div></div>")){
							alLegends.add("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"padding:1px;display: inline-block;margin-right:5px;width:38px;height:100%;background-color:green;text-align:center\" class=\"blueColor\">T</div>Travel Leave</div></div>");
						}
						
					}else if(hmHolidayDates.containsKey(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)){
						alInner.add("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"width:100%;display: inline-block;height:100%;text-align:center;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+"\">H</div></div></div>");
						alInnerPrint.add("H");
						
						if(!alLegends.contains("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"padding:1px;display: inline-block;margin-right:5px;width:38px;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"blueColor\"> H</div></div></div> "+uF.showData(hmHolidayName.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId), ""))){
							alLegends.add("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"padding:1px;display: inline-block;margin-right:5px;width:38px;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"blueColor\"> H</div></div></div> "+uF.showData(hmHolidayName.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId), ""));
						}						
					}else if(alEmpCheckRosterWeektype.contains(alEmployees.get(i))){
						if(rosterWeeklyOffSet.contains(alDates.get(ii))){
							alInner.add("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"width:100%;height:100%;text-align:center;display: inline-block;background-color:"+strColour+"\">W/O</div></div></div>");
							alInnerPrint.add("W/O");
							
							if(!alLegends.contains("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"padding:1px;display: inline-block;margin-right:5px;width:38px;height:100%;background-color:"+WEEKLYOFF_COLOR+";text-align:center\" class=\"blueColor\"> W/O</div> Weekly Off</div></div>")){
								alLegends.add("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"padding:1px;display: inline-block;margin-right:5px;width:38px;height:100%;background-color:"+WEEKLYOFF_COLOR+";text-align:center\" class=\"blueColor\"> W/O</div> Weekly Off</div></div>");
							}
						}else {
							alInner.add("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"width:100%;display: inline-block;height:100%;text-align:center;background-color:"+strColour+"\">"+uF.showData((String)hmLeaves.get((String)alDates.get(ii)), "&nbsp;")+"</div></div></div>");
							alInnerPrint.add(uF.showData((String)hmLeaves.get((String)alDates.get(ii)), ""));
						}
						
					}else if(weeklyOffSet.contains(alDates.get(ii))){
						alInner.add("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"width:100%;height:100%;display: inline-block;text-align:center;background-color:"+strColour+"\">W/O</div></div></div>");
						alInnerPrint.add("W/O");
						
						if(!alLegends.contains("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"padding:1px;display: inline-block;margin-right:5px;width:38px;height:100%;background-color:"+WEEKLYOFF_COLOR+";text-align:center\" class=\"blueColor\"> W/O</div> Weekly Off</div></div>")){
							alLegends.add("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"padding:1px;display: inline-block;margin-right:5px;width:38px;height:100%;background-color:"+WEEKLYOFF_COLOR+";text-align:center\" class=\"blueColor\"> W/O</div> Weekly Off</div></div>");
						}
						
					}else if(halfDayWeeklyOffSet.contains(alDates.get(ii))){
						alInner.add("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"width:100%;height:100%;display: inline-block;text-align:center;background-color:"+strColour+"\">W/HD</div></div></div>");
						alInnerPrint.add("W/HD");
						
						if(!alLegends.contains("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"padding:1px;display: inline-block;margin-right:5px;width:38px;height:100%;background-color:"+WEEKLYOFF_COLOR+";text-align:center\" class=\"blueColor\">W/HD</div> Half Day Weekly Off</div></div>")){
							alLegends.add("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"padding:1px;display: inline-block;margin-right:5px;width:38px;height:100%;background-color:"+WEEKLYOFF_COLOR+";text-align:center\" class=\"blueColor\">W/HD</div> Half Day Weekly Off</div></div>");
						}
						
					}else {
						alInner.add("<div class=\"custom-legend no-borderleft-for-legend\"><div class=\"legend-info\"><div style=\"width:100%;height:100%;display: inline-block;text-align:center;background-color:"+strColour+"\">"+uF.showData((String)hmLeaves.get((String)alDates.get(ii)), "&nbsp;")+"</div></div></div>");
						alInnerPrint.add(uF.showData((String)hmLeaves.get((String)alDates.get(ii)), ""));
					}
				}
				
				reportList.add(alInner);
				reportListPrint.add(alInnerPrint);
			}
			
			
			request.setAttribute("alDates", alDates);
			request.setAttribute("alEmployees", alEmployees);
			request.setAttribute("hmEmpWlocation", hmEmpWlocation);
			request.setAttribute("hmWeekEnds", hmWeekEnds);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmServiceMap", hmServiceMap);
			request.setAttribute("hmLeavesColour", hmLeavesColour);
			request.setAttribute("hmLeavesName", hmLeavesName);
			request.setAttribute("reportList", reportList);
			request.setAttribute("reportListPrint", reportListPrint);
			
			request.setAttribute("alLegends", alLegends);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public Map<String,Map<String,String>> getLeaveDetails(Connection con,String strDate1,String strDate2,UtilityFunctions uF){
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,Map<String,String>> getMap=new HashMap<String,Map<String,String>>();
		try{
			pst=con.prepareStatement("select lar.*,lt.leave_type_code from leave_application_register lar,leave_type lt where lar.leave_type_id=lt.leave_type_id and is_modify = false and _date between ? and ? order by emp_id");
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate2, DATE_FORMAT));
			rs=pst.executeQuery();
			
			while(rs.next()){
				Map<String,String> a=getMap.get(rs.getString("emp_id"));
				if(a==null)a=new HashMap<String,String>();
				
				a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_type_code"));
				getMap.put(rs.getString("emp_id"), a);
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
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
		return getMap;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;


	}

	public String getStrMonth() {
		return strMonth;
	}


	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}


	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}


	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
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


	public String[] getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String[] getF_department() {
		return f_department;
	}


	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}


	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public String getCalendarYear() {
		return calendarYear;
	}

	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
	}

	public List<FillCalendarYears> getCalendarYearList() {
		return calendarYearList;
	}

	public void setCalendarYearList(List<FillCalendarYears> calendarYearList) {
		this.calendarYearList = calendarYearList;
	}


	public String getStrLocation() {
		return strLocation;
	}


	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}


	public String getStrDepartment() {
		return strDepartment;
	}


	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}


	public String getStrSbu() {
		return strSbu;
	}


	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}


	public String getPaycycle() {
		return paycycle;
	}


	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}


	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}


	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}


	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}


	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

}
