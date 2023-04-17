package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmploymentType;
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

public class AttendanceRegister extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	String strBaseUserType = null;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(AttendanceRegister.class);
	
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	
	String paycycle;
	String strPaycycleDuration;
	private String strMonth;
	private String strYear;
	
	private String f_org;
	private String[] f_strWLocation;
	private String[] f_department;
	private String[] f_level;
	private String[] f_service;
	private String[] f_emptype;
	
	private List<FillMonth> monthList;
	private List<FillYears> yearList;
	List<FillPayCycles> paycycleList;
	private List<FillOrganisation> orgList;
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillEmploymentType> empTypeList;
	private String strEmpType;
	
	private String exportType;
	    
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(TITLE, TAttendanceRegister);
		request.setAttribute(PAGE, PAttendanceRegister);

//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}

		if(uF.parseToInt(getF_org()) == 0) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if (getStrPaycycleDuration() == null || getStrPaycycleDuration().trim().equals("") || getStrPaycycleDuration().trim().equalsIgnoreCase("NULL")) {
			setStrPaycycleDuration("M");
		}
		
		if(getStrLocation() != null && !getStrLocation().equals("") && !getStrLocation().equalsIgnoreCase("NULL")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("") && !getStrDepartment().equalsIgnoreCase("NULL")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("") && !getStrSbu().equalsIgnoreCase("NULL")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("") && !getStrLevel().equalsIgnoreCase("NULL")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		if(getStrEmpType() != null && !getStrEmpType().equals("") && !getStrEmpType().equalsIgnoreCase("NULL")) {
			setF_emptype(getStrEmpType().split(","));
		} else {
			setF_emptype(null);
		}
		
		viewAttendanceRegister(uF);

		return loadAttendanceRegister(uF);

	}
	
	public String loadAttendanceRegister(UtilityFunctions uF) {
		monthList = new FillMonth().fillMonth();
//		yearList = new FillYears().fillFutureYears(uF.getCurrentDate(CF.getStrTimeZone()), 2, 2);
		yearList = new FillYears(request).fillYearsFromStartToCurrent();
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		empTypeList = new FillEmploymentType().fillEmploymentType(request);
		
		paycycleList = new FillPayCycles(getStrPaycycleDuration(), request).fillPayCycles(CF, getF_org());
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
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
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getF_level().length;j++) {
					if(getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if(k==0) {
							strLevel=levelList.get(i).getLevelCodeName();
						} else {
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}
		
		alFilter.add("EMPTYPE");
		if(getF_emptype()!=null) {
			String strEmpType="";
			int k=0;
			for(int i=0;empTypeList!=null && i<empTypeList.size();i++) {
				for(int j=0;j<getF_emptype().length;j++) {
					if(getF_emptype()[j].equals(empTypeList.get(i).getEmpTypeId())) {
						if(k==0) {
							strEmpType=empTypeList.get(i).getEmpTypeName();
						} else {
							strEmpType+=", "+empTypeList.get(i).getEmpTypeName();
						}
						k++;
					}
				}
			}
			if(strEmpType!=null && !strEmpType.equals("")) {
				hmFilter.put("EMPTYPE", strEmpType);
			} else {
				hmFilter.put("EMPTYPE", "All Employeetype's");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employeetype's");
		}
		
		alFilter.add("MONTH");
		int nselectedMonth = uF.parseToInt(getStrMonth());
		String strMonth = uF.getMonth(nselectedMonth);
		hmFilter.put("MONTH", strMonth);
		
		alFilter.add("YEAR");
		hmFilter.put("YEAR", uF.showData(getStrYear(), ""));
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	public String viewAttendanceRegister(UtilityFunctions uF) {
			
		int Counter =0;
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String,String> hmEmpName = CF.getEmpNameMap(con, null, null);
//			Map hmEmpCode = CF.getEmpCodeMap();
//			Map<String,String> hmServiceMap =  CF.getServicesMap(con, true);
//			Map<String,String> hmDesigMap =  CF.getDesigMap(con);
			Map<String,String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String,String> hmLeaveCodeMap =  getLeaveCodeMap(con, uF, getF_org());
			
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
				setStrYear(uF.parseToInt(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, "yyyy"))+"");
			}
		List<String> alDates = new ArrayList<String>();
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1 );
			cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
			int minDays = uF.parseToInt(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, "dd"));
			cal.set(Calendar.DAY_OF_MONTH, minDays);
		
			String daysDiff = uF.dateDifference(strPayCycleDates[0], DATE_FORMAT, strPayCycleDates[1], DATE_FORMAT);
			int maxDays = uF.parseToInt(daysDiff);
//			cal.getActualMaximum(Calendar.DATE);
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
			alInnerExport.add(new DataStyle("Attendance Register For Month of "+ uF.showData(uF.getMonth(uF.parseToInt(getStrMonth())), "")+" "+ uF.parseToInt(getStrYear()), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Sr.NO",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Organization",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Work Location",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Gender",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Designation",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employement Type",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			String strD1 = null;
			for(int i=0; i<maxDays; i++){
				
				strD1 = uF.zero(cal.get(Calendar.DAY_OF_MONTH)) + "/" + uF.zero(cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR);
				
				alDates.add(uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT));
				alInnerExport.add(new DataStyle(uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				cal.add(Calendar.DATE, 1);
			}
			alInnerExport.add(new DataStyle("Total Days",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
		    Iterator<String> it11= hmLeaveCodeMap.keySet().iterator();
		    while(it11.hasNext()){
		    	String strId = it11.next();
		    	String strValue = hmLeaveCodeMap.get(strId);
		        alInnerExport.add(new DataStyle(strValue,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		    }
		
			reportListExport.add(alInnerExport);
				
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEndDates = CF.getWeekEndDateList(con, alDates.get(0), alDates.get(alDates.size()-1), CF, uF,hmWeekEndHalfDates,null);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF,alDates.get(0), alDates.get(alDates.size()-1),alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEndDates,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
//			System.out.println("alEmpCheckRosterWeektype ===>> " + alEmpCheckRosterWeektype);
			List<String> alEmployees  = new ArrayList<String>();
			Map<String,List<String>> hmEmpServiceWorkedFor = new HashMap<String,List<String>>();
			List<String> alServices  = new ArrayList<String>();
			
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and epd.is_delete=false " +
				"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) ");
//			sbQuery.append(" and emp_id in (522) ");
			if(strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strBaseUserType.equalsIgnoreCase(HOD))) {
				sbQuery.append("and emp_id in (select emp_id from employee_official_details " +
					"where (supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+" " +
					"or hod_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+"))");		
				if(getF_emptype()!=null && getF_emptype().length>0){
					sbQuery.append("and emp_id in (select emp_id from employee_official_details where emptype in ('"+StringUtils.join(getF_emptype(), "' ,'")+"') ");
				}
			} else {
				if(getF_level()!=null && getF_level().length>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }
	            if(getF_department()!=null && getF_department().length>0){
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            
	            if(getF_service()!=null && getF_service().length>0){
	                sbQuery.append(" and (");
	                for(int i=0; i<getF_service().length; i++){
	                	sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
	                    
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
			}
			
        	if(getF_emptype()!=null && getF_emptype().length>0){
				sbQuery.append("and eod.emptype in ('"+StringUtils.join(getF_emptype(), "' ,'")+"') ");
			}
            		 
            sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();			
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			
			while(rs.next()){
				
				strEmpIdNew = rs.getString("emp_per_id");
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					alServices  = new ArrayList<String>();
				}
				
				alEmployees.add(strEmpIdNew);
				
				
				String []arrServices = null;
				
				
				if(rs.getString("service_id")!=null){
					arrServices = rs.getString("service_id").split(",");
				}
				
				for(int i=0; arrServices!=null && i<arrServices.length; i++){
					if(!alServices.contains(arrServices[i]) && uF.parseToInt(arrServices[i]) > 0){
						alServices.add(arrServices[i]);
					}
				}
				
				
				hmEmpServiceWorkedFor.put(strEmpIdNew, alServices);
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmBreakTypeCode = new HashMap<String, String>();
			pst = con.prepareStatement("select * from leave_break_type");
			rs = pst.executeQuery();
			while(rs.next()){
				hmBreakTypeCode.put(rs.getString("break_type_id"), rs.getString("break_type_code"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmBreakPolicy = new HashMap<String, String>();
			pst = con.prepareStatement("select * from break_application_register where _date between ? and ?");
			pst.setDate(1, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				hmBreakPolicy.put(rs.getString("emp_id")+"_"+uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), hmBreakTypeCode.get(rs.getString("break_type_id")));
			}
			rs.close();
			pst.close();
			
			Map<String,String> hmHolidays = new HashMap<String,String>();
			Map<String,String> hmHolidayDates = new HashMap<String,String>();
			CF.getHolidayList(con,request, alDates.get(0), alDates.get(alDates.size()-1), CF, hmHolidayDates, hmHolidays, true);
			Map<String, String> hmHolidayName = CF.getHolidayName(con, CF,alDates.get(0), alDates.get(alDates.size()-1));
			if(hmHolidayName == null) hmHolidayName = new HashMap<String, String>();
			
			Map<String, Map<String, String>> hmLeaveTypeDays = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmLeavesMap = CF.getLeaveDates(con, alDates.get(0), alDates.get(alDates.size()-1), CF, hmLeaveTypeDays, false, null);
			Map<String, Map<String, String>> hmLeavesMap = CF.getActualLeaveDates(con, CF, uF, alDates.get(0), alDates.get(alDates.size()-1), hmLeaveTypeDays, false, null);
			Map<String, String> hmLeaveColor = new HashMap<String, String>(); 
			CF.getLeavesAttributes(con, uF, hmLeaveColor, null);
			
			Map<String, String> hmEmpWlocationMap = new HashMap<String, String>();
			Map<String, String> hmEmpStateMap = new HashMap<String, String>();
			Map<String, String> hmEmpMertoMap = new HashMap<String, String>();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			Map<String, Map<String, String>> hmHalfDayFullDayMinHrs = CF.getWorkLocationHalfDayFullDayMinHours(con, uF, alDates.get(alDates.size()-1));
			if(hmHalfDayFullDayMinHrs==null) hmHalfDayFullDayMinHrs = new HashMap<String, Map<String,String>>();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from attendance_details where to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ? ");
//			sbQuery.append(" and emp_id in (522) ");
			if(strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strBaseUserType.equalsIgnoreCase(HOD))){
				sbQuery.append("and emp_id in (select emp_id from employee_official_details " +
						"where (supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+" " +
						"or hod_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+"))");		
				if(getF_emptype()!=null && getF_emptype().length>0){
					sbQuery.append("and emp_id in (select emp_id from employee_official_details where emptype in ('"+StringUtils.join(getF_emptype(), "' ,'")+"') ");
				}
			} else {
				if((getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_service()!=null && getF_service().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append("and emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
				}
				if(getF_level()!=null && getF_level().length>0){
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }
	            if(getF_department()!=null && getF_department().length>0){
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            
	            if(getF_service()!=null && getF_service().length>0){
	                sbQuery.append(" and (");
	                for(int i=0; i<getF_service().length; i++){
	                	sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
	                    
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
	            if(getF_emptype()!=null && getF_emptype().length>0){
					sbQuery.append("and emptype in ('"+StringUtils.join(getF_emptype(), "' ,'")+"') ");
				}
	            
	            if((getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_service()!=null && getF_service().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append(") ");
				}
			}
			sbQuery.append("order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println("pst for attendance====>"+pst);
			Map<String, String> hmHalfDayAttendance = new HashMap<String, String>();
			Map<String, String> hmAbsentAttendance = new HashMap<String, String>();
			Map<String, String> hmEmpAttendance = new HashMap<String, String>();
			Map<String, String> hmEmpLateEarly = new HashMap<String, String>();
			while(rs.next()) {
				
				strEmpIdNew = rs.getString("emp_id");
				Map<String, String> hmHDFDMinHrs = hmHalfDayFullDayMinHrs.get(hmEmpWlocationMap.get(strEmpIdNew));
				if(hmHDFDMinHrs==null) hmHDFDMinHrs = new HashMap<String, String>();
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					alServices  = new ArrayList<String>();
				}
				
				if(!alServices.contains(rs.getString("service_id"))) {
					alServices.add(rs.getString("service_id"));
				}
				
				hmEmpServiceWorkedFor.put(strEmpIdNew, alServices);
				
//				hmEmpAttendance.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id")+"_"+rs.getString("emp_id"), "P");
//				if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")) {
//					hmEmpLateEarly.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id")+"_"+rs.getString("emp_id"), rs.getString("early_late"));
//				}
				
				if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")) {
					hmEmpAttendance.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id")+"_"+rs.getString("emp_id"), "P");
					double workingHour = rs.getDouble("hours_worked");
//					if(workingHour < 5) {
					if (workingHour >= uF.parseToDouble(hmHDFDMinHrs.get("MIN_HRS_HD")) && workingHour < uF.parseToDouble(hmHDFDMinHrs.get("MIN_HRS_FD"))) {
						hmHalfDayAttendance.put(rs.getString("emp_id")+"_"+uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBDATE, DATE_FORMAT), ""+workingHour);
					} else if (workingHour < uF.parseToDouble(hmHDFDMinHrs.get("MIN_HRS_FD"))) {
						hmAbsentAttendance.put(rs.getString("emp_id")+"_"+uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBDATE, DATE_FORMAT), ""+workingHour);
					}
				}
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpAttendance ===>> " + hmEmpAttendance);
//			System.out.println("hmAbsentAttendance ===>> " + hmAbsentAttendance);
			
			//System.out.println("alDates"+alDates.size());
			request.setAttribute("alDates", alDates);
			request.setAttribute("alEmployees", alEmployees);
			request.setAttribute("hmEmpAttendance", hmEmpAttendance);
			request.setAttribute("hmEmpServiceWorkedFor", hmEmpServiceWorkedFor);
			
			request.setAttribute("hmEmpWlocation", hmEmpWlocation);
			request.setAttribute("hmEmpName", hmEmpName);
			
			
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			
			if(hmEmpCode == null) hmEmpCode = new HashMap<String, String>();
			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
			if(hmEmpOrgId == null) hmEmpOrgId = new HashMap<String, String>();
			Map<String, String> hmOrg = CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			Map<String, String> hmWlocationMap = CF.getWLocationMap(con, null, null);
			if(hmWlocationMap == null) hmWlocationMap = new HashMap<String, String>();
			Map<String, String> hmEmpDept = CF.getEmpDepartmentMap(con);
			if(hmEmpDept == null) hmEmpDept = new HashMap<String, String>();
			Map<String, String> hmDeptMap = CF.getDeptMap(con);
			if(hmDeptMap == null) hmDeptMap = new HashMap<String, String>();
			
			Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
			if(hmEmpGenderMap == null) hmEmpGenderMap = new HashMap<String, String>();
			
			Map<String,String> hmEmpTypeMap = CF.getEmpTypeMap(con);
			if(hmEmpTypeMap==null) hmEmpTypeMap = new HashMap<String, String>();
			
			Map<String, String> hmEmployementTypeMap = CF.getEmployementTypeMap();
			List<List<String>> reportList = new ArrayList<List<String>>();
			List<List<String>> reportListPrint = new ArrayList<List<String>>();
			
			List<String> alInner = new ArrayList<String>();
			List<String> alInnerPrint = new ArrayList<String>();
			
			List<String> alLegends = new ArrayList<String>(); 
			
//			Map<String,String> hmINBreaks = new HashMap<String,String>();
			Map<String,Map<String,String>> leaveEmpMap=getLeaveDetails(con,alDates.get(0),alDates.get(alDates.size()-1),uF);
			Map<String, Map<String, String>> hmTravelRegister = CF.getTravelDetails(con,uF,alDates.get(0),alDates.get(alDates.size()-1));
			
//			System.out.println("alEmployees.size()"+alEmployees.size());
			for (int i=0; i<alEmployees.size(); i++){
				
				/*if(uF.parseToInt(alEmployees.get(i)) != 13) {
					continue;
				}*/
//				Map<String,String> hmEmpLeave = hmLeavesMap.get(alEmployees.get(i));
				
				Map<String,String> hmEmpLeave = leaveEmpMap.get(alEmployees.get(i));
				if(hmEmpLeave==null)hmEmpLeave = new HashMap<String,String>();
				
				Map<String,String> hmEmpTravel = hmTravelRegister.get(alEmployees.get(i));
				if(hmEmpTravel==null) hmEmpTravel = new HashMap<String,String>();
				
				Map<String,String> hmEmpLeaveType = hmLeaveTypeDays.get(alEmployees.get(i));
				if(hmEmpLeaveType==null)hmEmpLeaveType = new HashMap<String,String>();
				
				String strWLocationId = hmEmpWlocation.get(alEmployees.get(i));
				Set<String> weeklyOffSet= hmWeekEndDates.get(strWLocationId);
//				String strLevelId = hmEmpLevelMap.get(alEmployees.get(i));
//				Set<String> weeklyOffSet= hmWeekEndDates.get(strLevelId);
				if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
				
				Set<String> halfDayWeeklyOffSet= hmWeekEndHalfDates.get(strWLocationId);
				if(halfDayWeeklyOffSet==null) halfDayWeeklyOffSet=new HashSet<String>();
				
				Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(alEmployees.get(i));
				if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
				
				List<String> alServicesInner = hmEmpServiceWorkedFor.get(alEmployees.get(i));
				//System.out.println("hmEmpServiceWorkedFor size===>"+hmEmpServiceWorkedFor.size());
				
				if(alServicesInner==null)alServicesInner=new ArrayList<String>();
				for (int k=0; k<alServicesInner.size(); k++){
					
					alInner = new ArrayList<String>();
					alInnerPrint = new ArrayList<String>();
					alInnerExport = new ArrayList<DataStyle>();
				    Counter++;   
					
				    alInner.add(String.valueOf(Counter));
					alInner.add((k==0)?uF.showData((String)hmEmpCode.get(alEmployees.get(i)),""):"");
					alInner.add((k==0)?uF.showData(hmEmpName.get(alEmployees.get(i)),""):"");
					alInner.add((k==0)?uF.showData(hmOrg.get(hmEmpOrgId.get(alEmployees.get(i))),""):"");
					alInner.add((k==0)?uF.showData(hmWlocationMap.get(strWLocationId),""):"");
					alInner.add((k==0)?uF.showData(hmDeptMap.get(hmEmpDept.get(alEmployees.get(i))),""):"");
					alInner.add((k==0)?uF.showData(hmEmpGenderMap.get(alEmployees.get(i)),""):"");
					alInner.add(uF.showData(hmEmpDesigMap.get(alEmployees.get(i)),""));
					alInner.add((k==0)?uF.showData(hmEmployementTypeMap.get(hmEmpTypeMap.get(alEmployees.get(i))),""):"");
				
					alInnerPrint.add(String.valueOf(Counter));
					alInnerPrint.add((k==0)?uF.showData((String)hmEmpCode.get(alEmployees.get(i)),""):"");
					alInnerPrint.add((k==0)?uF.showData(hmEmpName.get(alEmployees.get(i)),""):"");
					alInnerPrint.add((k==0)?uF.showData(hmOrg.get(hmEmpOrgId.get(alEmployees.get(i))),""):"");
					alInnerPrint.add((k==0)?uF.showData(hmWlocationMap.get(strWLocationId),""):"");
					alInnerPrint.add((k==0)?uF.showData(hmDeptMap.get(hmEmpDept.get(alEmployees.get(i))),""):"");
					alInnerPrint.add((k==0)?uF.showData(hmEmpGenderMap.get(alEmployees.get(i)),""):"");
					alInnerPrint.add(uF.showData(hmEmpDesigMap.get(alEmployees.get(i)),""));
					alInnerPrint.add((k==0)?uF.showData(hmEmployementTypeMap.get(hmEmpTypeMap.get(alEmployees.get(i))),""):"");
					
					alInnerExport.add(new DataStyle((k==0)?uF.showData(String.valueOf(Counter),""):"",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle((k==0)?uF.showData((String)hmEmpCode.get(alEmployees.get(i)),""):"",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle((k==0)?uF.showData(hmEmpName.get(alEmployees.get(i)),""):"",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle((k==0)?uF.showData(hmOrg.get(hmEmpOrgId.get(alEmployees.get(i))),""):"",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle((k==0)?uF.showData(hmWlocationMap.get(strWLocationId),""):"",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle((k==0)?uF.showData(hmDeptMap.get(hmEmpDept.get(alEmployees.get(i))),""):"",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle((k==0)?uF.showData(hmEmpGenderMap.get(alEmployees.get(i)),""):"",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpDesigMap.get(alEmployees.get(i)),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle((k==0)?uF.showData(hmEmployementTypeMap.get(hmEmpTypeMap.get(alEmployees.get(i))),""):"",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					
					Map<String,String> hmLeaveCnt = new HashMap<String, String>();
//					System.out.println(" alDates.size()"+ alDates.size());
					double absentCnt=0;
					for (int ii=0; ii<alDates.size(); ii++) {
					
						String strWeekDay = uF.getDateFormat(alDates.get(ii), DATE_FORMAT, "EEEE");
						if(strWeekDay!=null){
							strWeekDay = strWeekDay.toUpperCase();
						}
						
						String strArrendance = hmEmpAttendance.get(alDates.get(ii)+"_"+alServicesInner.get(k)+"_"+alEmployees.get(i));
						
//						System.out.println("strArrendance----->"+strArrendance);
//						System.out.println(alEmployees.get(i)+"====="+alServicesInner.get(k)+"====="+alDates.get(ii)+"=======strArrendance====="+strArrendance);
						
						java.util.Date dtDate = uF.getDateFormatUtil(alDates.get(ii), DATE_FORMAT);
						java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
						
						if(hmEmpLeave.containsKey(alDates.get(ii))){
//							if(alEmployees.get(i).equals("12")){
//								System.out.println("==1==="+alDates.get(ii));
//							}
							
							if("H".equals(hmEmpLeaveType.get(alDates.get(ii)))) {
//								
								double dblLeaveCnt = uF.parseToDouble(hmLeaveCnt.get(alEmployees.get(i)+"_"+hmEmpLeave.get(alDates.get(ii))));
								dblLeaveCnt +=0.5;
								
								hmLeaveCnt.put(alEmployees.get(i)+"_"+hmEmpLeave.get(alDates.get(ii)),""+dblLeaveCnt);
							
								if(hmHolidayDates.containsKey(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)) {
									
									alInner.add("<div style=\"width:100%;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"blueColor\"> [H] "+hmEmpLeave.get(alDates.get(ii))+" (HD) </div>");
									alInnerPrint.add(" [H] "+hmEmpLeave.get(alDates.get(ii))+" (HD) ");
									alInnerExport.add(new DataStyle(" [H] "+hmEmpLeave.get(alDates.get(ii))+" (HD) ",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
									
									if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"blueColor\"> H</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> "+uF.showData(hmHolidayName.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId), "")+"</div>")) {
										alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"blueColor\"> H</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> "+uF.showData(hmHolidayName.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId), "")+"</div>");
									}
									
								} else if(rosterWeeklyOffSet.contains(alDates.get(ii))) {
										String strColor=WEEKLYOFF_COLOR;
										alInner.add("<div style=\"width:100%;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> [W/O] "+hmEmpLeave.get(alDates.get(ii))+" (HD) </div>");
										alInnerPrint.add("[W/O] "+hmEmpLeave.get(alDates.get(ii))+" (HD) ");
										alInnerExport.add(new DataStyle("[W/O] "+hmEmpLeave.get(alDates.get(ii))+" (HD) ",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
										
										if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Weekly Off</div>")) {
											alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Weekly Off</div>");
										}
									
								} else {
									alInner.add("<div style=\"width:100%;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+" (HD) "+(strArrendance!=null?"/P":"/A")+"</div>");
									alInnerPrint.add(hmEmpLeave.get(alDates.get(ii))+" (HD) "+(strArrendance!=null?"/P":"/A"));
									alInnerExport.add(new DataStyle(hmEmpLeave.get(alDates.get(ii))+" (HD) "+(strArrendance!=null?"/P":"/A"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
									
									if(strArrendance==null || strArrendance.equals("")) {
										absentCnt +=0.5;
	//									System.out.println("strArrendance -- absentCnt 0.5 ===>> " + absentCnt);
									}
									
									if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"/P"+"</div>")){
										alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"/P"+"</div>");
									}
								}
							} else {

								double dblLeaveCnt = uF.parseToDouble(hmLeaveCnt.get(alEmployees.get(i)+"_"+hmEmpLeave.get(alDates.get(ii))));
								dblLeaveCnt +=1;
								
								hmLeaveCnt.put(alEmployees.get(i)+"_"+hmEmpLeave.get(alDates.get(ii)),""+dblLeaveCnt);
								if(hmHolidayDates.containsKey(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)) {
									alInner.add("<div style=\"width:100%;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"blueColor\"> [H] "+hmEmpLeave.get(alDates.get(ii))+"</div>");
									alInnerPrint.add(" [H] "+hmEmpLeave.get(alDates.get(ii)));
									alInnerExport.add(new DataStyle(" [H] "+hmEmpLeave.get(alDates.get(ii)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
									
									if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"blueColor\"> H</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> "+uF.showData(hmHolidayName.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId), "")+"</div>")) {
										alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"blueColor\"> H</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> "+uF.showData(hmHolidayName.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId), "")+"</div>");
									}
									
								} else if(rosterWeeklyOffSet.contains(alDates.get(ii))) {
										String strColor=WEEKLYOFF_COLOR;
										alInner.add("<div style=\"width:100%;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> [W/O] "+hmEmpLeave.get(alDates.get(ii))+"</div>");
										alInnerPrint.add("[W/O] "+hmEmpLeave.get(alDates.get(ii)));
										alInnerExport.add(new DataStyle("[W/O] "+hmEmpLeave.get(alDates.get(ii)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
										
										if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Weekly Off</div>")) {
											alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Weekly Off</div>");
										}
									
								} else {
									alInner.add("<div style=\"width:100%;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"</div>");
									alInnerPrint.add(hmEmpLeave.get(alDates.get(ii)));
									alInnerExport.add(new DataStyle(hmEmpLeave.get(alDates.get(ii)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
									
									if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Leave</div>")){
										alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Leave</div>");
									}
								}
							}
						} else if(hmEmpTravel.containsKey(alDates.get(ii))){
							
							if("H".equals(hmEmpLeaveType.get(alDates.get(ii)))) {
								alInner.add("<div style=\"width:100%;height:100%;background-color:green;color:white;text-align:center\">"+hmEmpTravel.get(alDates.get(ii))+" (HD) </div>");
								alInnerPrint.add(hmEmpTravel.get(alDates.get(ii))+" (HD) ");
								alInnerExport.add(new DataStyle(hmEmpTravel.get(alDates.get(ii))+" (HD) ",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
								
								if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:green;color:white;text-align:center\">"+hmEmpTravel.get(alDates.get(ii))+"</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Travel Leave</div>")) {
									alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:green;color:white;text-align:center\">"+hmEmpTravel.get(alDates.get(ii))+"</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Travel Leave</div>");
								}
								
							} else {
								alInner.add("<div style=\"width:100%;height:100%;background-color:green;color:white;text-align:center\">"+hmEmpTravel.get(alDates.get(ii))+"</div>");
								alInnerPrint.add(hmEmpTravel.get(alDates.get(ii)));
								alInnerExport.add(new DataStyle(hmEmpTravel.get(alDates.get(ii)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
								
								if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:green;text-align:center\">"+hmEmpTravel.get(alDates.get(ii))+"</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Travel Leave</div>")){
									alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:green;text-align:center\">"+hmEmpTravel.get(alDates.get(ii))+"</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Travel Leave</div>");
								}
							}
						} else if(hmHolidayDates.containsKey(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)) {
//							if(alEmployees.get(i).equals("12")){
//							System.out.println("==3==="+alDates.get(ii));
//						}
						if(strArrendance!=null) {
//							if(alEmployees.get(i).equals("12")){
//								System.out.println("==4==="+alDates.get(ii));
//							}
							alInner.add("<div style=\"width:100%;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"greenColor\">P</div>");
							alInnerPrint.add("P");
							alInnerExport.add(new DataStyle("P",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
						} else {
//							if(alEmployees.get(i).equals("12")){
//								System.out.println("==5==="+alDates.get(ii));
//							}
							alInner.add("<div style=\"width:100%;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"blueColor\"> H</div>");
							alInnerPrint.add("H");
							alInnerExport.add(new DataStyle("H",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							
							if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"blueColor\"> H</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> "+uF.showData(hmHolidayName.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId), "")+"</div>")){
								alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"blueColor\"> H</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> "+uF.showData(hmHolidayName.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId), "")+"</div>");
							}
							
						}
					} else if(alEmpCheckRosterWeektype.contains(alEmployees.get(i))) {
	//							if(alEmployees.get(i).equals("12")){
	//							System.out.println("==6==="+alDates.get(ii));
	//						}
							if(rosterWeeklyOffSet.contains(alDates.get(ii))) {
	//							if(alEmployees.get(i).equals("12")){
	//								System.out.println("==7==="+alDates.get(ii));
	//							}
								String strColor=WEEKLYOFF_COLOR;
								alInner.add("<div style=\"width:100%;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div>");
								alInnerPrint.add("W/O");
								alInnerExport.add(new DataStyle("W/O",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
	
								if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Weekly Off</div>")){
									alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Weekly Off</div>");
								}
								
							} else if(strArrendance!=null) {
	//							if(alEmployees.get(i).equals("12")){
	//								System.out.println("==8==="+alDates.get(ii));
	//							}
								if(hmBreakPolicy.containsKey(alEmployees.get(i)+"_"+alDates.get(ii))){
	//								if(alEmployees.get(i).equals("12	System.out.println("");")){
	//									System.out.println("==9==="+alDates.get(ii));
	//								}
									alInner.add("<div style=\"text-align:center\" class=\"greenColor\">"+hmBreakPolicy.get(alEmployees.get(i)+"_"+alDates.get(ii))+"</div>");
									alInnerPrint.add("P");
									alInnerExport.add(new DataStyle(hmBreakPolicy.get(alEmployees.get(i)+"_"+alDates.get(ii)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
								} else if(hmHalfDayAttendance.containsKey(alEmployees.get(i)+"_"+alDates.get(ii))) {
	//								if(alEmployees.get(i).equals("12")){
	//									System.out.println("==10==="+alDates.get(ii));
	//								}
									
									alInner.add("<div style=\"text-align:center\" class=\"greenColor\">HD/P</div>");
									alInnerPrint.add("HD/P");
									alInnerExport.add(new DataStyle("HD/P",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
									absentCnt +=0.5;
	//								System.out.println("absentCnt 0.5 ===>> " + absentCnt);
								} else if(hmAbsentAttendance.containsKey(alEmployees.get(i)+"_"+alDates.get(ii))){
		//							if(alEmployees.get(i).equals("12")){
		//								System.out.println("==18==="+alDates.get(ii));
		//							}
									alInner.add("<div style=\"text-align:center\" class=\"redColor\">A</div>");
									absentCnt++;
	//								System.out.println("absentCnt ===>> " + absentCnt);
									alInnerPrint.add("A");
									alInnerExport.add(new DataStyle("A",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
								} else {
	//								if(alEmployees.get(i).equals("12")){
	//									System.out.println("==11==="+alDates.get(ii));
	//								}
									alInner.add("<div style=\"text-align:center\" class=\"greenColor\">P</div>");
									alInnerPrint.add("P");
									alInnerExport.add(new DataStyle("P",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
								}
							} else if(dtCurrentDate!=null && dtCurrentDate.after(dtDate)) {
	//							if(alEmployees.get(i).equals("12")){
	//								System.out.println("==12==="+alDates.get(ii));
	//							}
								alInner.add("<div style=\"text-align:center\" class=\"redColor\">A</div>");
								absentCnt++;
	//							System.out.println("absentCnt ===>> " + absentCnt);
								alInnerPrint.add("A");
								alInnerExport.add(new DataStyle("A",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							} else {
	//							if(alEmployees.get(i).equals("12")){
	//								System.out.println("==13==="+alDates.get(ii));
	//							}
								alInner.add("");
								alInnerPrint.add("");
								alInnerExport.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							}
							
						} else if(strArrendance!=null) {
//							if(alEmployees.get(i).equals("12")){
//								System.out.println("==16==="+alDates.get(ii));
	//						}
							if(hmBreakPolicy.containsKey(alEmployees.get(i)+"_"+alDates.get(ii))) {
	//							if(alEmployees.get(i).equals("12")){
	//								System.out.println("==17==="+alDates.get(ii));
	//							}
								alInner.add("<div style=\"text-align:center\" class=\"greenColor\">"+hmBreakPolicy.get(alEmployees.get(i)+"_"+alDates.get(ii))+"</div>");
								alInnerPrint.add("P");
								alInnerExport.add(new DataStyle(hmBreakPolicy.get(alEmployees.get(i)+"_"+alDates.get(ii)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							} else if(hmHalfDayAttendance.containsKey(alEmployees.get(i)+"_"+alDates.get(ii))){
//								if(alEmployees.get(i).equals("12")){
//									System.out.println("==18==="+alDates.get(ii));
//								}
								alInner.add("<div style=\"text-align:center\" class=\"greenColor\">HD/P</div>");
								alInnerPrint.add("HD/P");
								alInnerExport.add(new DataStyle("HD/P",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
								absentCnt +=0.5;
//								System.out.println("absentCnt 0.5 ===>> " + absentCnt);
							} else if(hmAbsentAttendance.containsKey(alEmployees.get(i)+"_"+alDates.get(ii))){
	//							if(alEmployees.get(i).equals("12")){
	//								System.out.println("==18==="+alDates.get(ii));
	//							}
								alInner.add("<div style=\"text-align:center;\" class=\"redColor\">A</div>");
								absentCnt++;
//								System.out.println("absentCnt ===>> " + absentCnt);
								alInnerPrint.add("A");
								alInnerExport.add(new DataStyle("A",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							} else {
	//							if(alEmployees.get(i).equals("12")){
	//								System.out.println("==19==="+alDates.get(ii));
	//							}
								alInner.add("<div style=\"text-align:center\" class=\"greenColor\">P</div>");
								alInnerPrint.add("P");
								alInnerExport.add(new DataStyle("P",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							}
						} else if(weeklyOffSet.contains(alDates.get(ii))) {
//							if(alEmployees.get(i).equals("12")){
//								System.out.println("==14==="+alDates.get(ii));
//							}
							String strColor=WEEKLYOFF_COLOR;
							
							alInner.add("<div style=\"width:100%;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div>");
							alInnerPrint.add("W/O");
							alInnerExport.add(new DataStyle("W/O",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));

							if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Weekly Off</div>")){
								alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Weekly Off</div>");
							}
							
						} else if(halfDayWeeklyOffSet.contains(alDates.get(ii))) {
//							if(alEmployees.get(i).equals("12")){
//								System.out.println("==15==="+alDates.get(ii));
//							}
							String strColor=WEEKLYOFF_COLOR;
							alInner.add("<div style=\"width:100%;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\">W/HD</div>");
							alInnerPrint.add("W/HD");
							alInnerExport.add(new DataStyle("W/HD",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));

							if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\">W/HD</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Half Day Weekly Off</div>")){
								alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\">W/HD</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Half Day Weekly Off</div>");
							}
							
						} else if(dtCurrentDate!=null && dtCurrentDate.after(dtDate)) {
//							if(alEmployees.get(i).equals("12")){
//								System.out.println("==20=="+alDates.get(ii));
//							}
							
							alInner.add("<div style=\"text-align:center\" class=\"redColor\">A</div>");
							absentCnt++;
//							System.out.println("absentCnt ===>> " + absentCnt);
							alInnerPrint.add("A");
							alInnerExport.add(new DataStyle("A",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
						} else {
//							if(alEmployees.get(i).equals("12")){
//								System.out.println("==21==="+alDates.get(ii));
//							}
							alInner.add("");
							alInnerPrint.add("");
							alInnerExport.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
						}
					}
					//System.out.println("alServicesInner"+alServicesInner.size());
					//System.out.println("hmEmpServiceWorkedFor"+hmEmpServiceWorkedFor.size());
					
					alInner.add(""+alDates.size());
			    	alInnerPrint.add(""+alDates.size());
					alInnerExport.add(new DataStyle(""+alDates.size(),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
					alInner.add(""+absentCnt); // no. of Absent of emp
					alInnerPrint.add(""+absentCnt);
					alInnerExport.add(new DataStyle(""+absentCnt,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					
				    Iterator<String> it12= hmLeaveCodeMap.keySet().iterator();
				    while(it12.hasNext()) {
				    	String strId = it12.next();
				    	String strValue = hmLeaveCodeMap.get(strId);
				    	double dblLeaveCount = uF.parseToDouble(hmLeaveCnt.get(alEmployees.get(i)+"_"+strValue));
				    	alInner.add(""+dblLeaveCount);
				    	alInnerPrint.add(""+dblLeaveCount);
						alInnerExport.add(new DataStyle(""+dblLeaveCount,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				    }				
					reportList.add(alInner);
					reportListPrint.add(alInnerPrint);
					reportListExport.add(alInnerExport);
				}
			}
//			System.out.println("alInner size()"+alInner.size());
//			System.out.println("reportList ===>> " + reportList);
			request.setAttribute("reportList", reportList);
			request.setAttribute("reportListPrint", reportListPrint);
			session.setAttribute("reportListExport", reportListExport);
		
			request.setAttribute("alLegends", alLegends);
			request.setAttribute("hmLeaveCodeMap", hmLeaveCodeMap);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	private Map<String, String> getLeaveCodeMap(Connection con, UtilityFunctions uF, String orgId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLeaveCodeMap = new HashMap<String,String>();
		try {

			pst = con.prepareStatement("SELECT * FROM leave_type where org_id=? order by leave_type_name");
			pst.setInt(1, uF.parseToInt(orgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmLeaveCodeMap.put(rs.getString("leave_type_id"), rs.getString("leave_type_code"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmLeaveCodeMap;

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

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
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

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
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

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
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

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}
	public String[] getF_emptype() {
		return f_emptype;
	}

	public void setF_emptype(String[] f_emptype) {
		this.f_emptype = f_emptype;
	}

	public List<FillEmploymentType> getEmpTypeList() {
		return empTypeList;
	}

	public void setEmpTypeList(List<FillEmploymentType> empTypeList) {
		this.empTypeList = empTypeList;
	}

	public String getStrEmpType() {
		return strEmpType;
	}

	public void setStrEmpType(String strEmpType) {
		this.strEmpType = strEmpType;
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

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}

	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}
	
}