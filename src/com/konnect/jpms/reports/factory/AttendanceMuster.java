package com.konnect.jpms.reports.factory;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AttendanceMuster extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ChildWorkerReport.class);
	HttpSession session;
	CommonFunctions CF = null;
	String strUserType = null;
	boolean isEmpUserType = false;
	
		
	String paycycle;
	String strMonth;
	String strYear;
	String strWLocation;
	String department;
	String service;
	
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillMonth> monthList;
	List<FillYears> yearList;
	List<FillWLocation> wLocationList;
	
	List<FillPayCycles> payCycleList;
	
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType =  (String)session.getAttribute(USERTYPE);
		
		request.setAttribute(PAGE, PAttendanceMuster);
		request.setAttribute(TITLE, TAttendanceMuster);

		viewAttendanceMuster();
		getAdultCount();
		
		return loadAttendanceMuster();

	}

	private void getAdultCount() {
  
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			//pst = con.prepareStatement("select * from employee_personal_details order by emp_fname, emp_lname");
			pst=con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and is_alive = true order by emp_fname, emp_lname");
			rs = pst.executeQuery();
			
			int adultCountMale = 0;
			int adultCountFeMale = 0;
			int adolescentCountMale = 0;
			int adolescentCountFeMale = 0;
			int childCountMale = 0;
			int childCountFeMale = 0;
			int adultCount = 0;
			int adolescentCount = 0;
			int childCount = 0;
			
			DateTime now = new DateTime();
			while(rs.next()){				
				
				int years =  0;
				if(rs.getString("emp_date_of_birth")!=null){
					DateMidnight birthdate = new DateMidnight(uF.parseToInt(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "yyyy")), uF.parseToInt(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM")), uF.parseToInt(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd")));
					Years age = Years.yearsBetween(birthdate, now);
					years = age.getYears();
				}
				
				if(years>18){
					if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equals("M")){
						adultCountMale++;
					}else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equals("F")){
						adultCountFeMale++;
					}
					adultCount++;
				}
				
				if(years>12 && years<18){
					if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equals("M")){
						adolescentCountMale++;
					}else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equals("F")){
						adolescentCountFeMale++;
					}
					adolescentCount++;
				}
				
				if(years<12){
					if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equals("M")){
						childCountMale++;
					}else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equals("F")){
						childCountFeMale++;
					}
					childCount++;
				}
				
			}
			rs.close();
			pst.close();
						
			request.setAttribute("adultCountMale",""+adultCountMale);
			request.setAttribute("adultCountFeMale",""+adultCountFeMale);
			request.setAttribute("adolescentCountMale",""+adolescentCountMale);
			request.setAttribute("adolescentCountFeMale",""+adolescentCountFeMale);
			request.setAttribute("childCountMale",""+childCountMale);
			request.setAttribute("childCountFeMale",""+childCountFeMale);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	public String loadAttendanceMuster() {
		UtilityFunctions uF=new UtilityFunctions();
		payCycleList = new FillPayCycles(request).fillPayCycles(CF);
		monthList = new FillMonth().fillMonth();
		yearList = new FillYears().fillFutureYears(uF.getCurrentDate(CF.getStrTimeZone()), 2, 2);
		wLocationList = new FillWLocation(request).fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment();
		serviceList = new FillServices(request).fillServices();

		return LOAD;
	}

	public String viewAttendanceMuster() {
  
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		try {
			
			Map<String, String>hmEmpName = CF.getEmpNameMap(con,null, null);
			Map<String, String>hmEmpCode = CF.getEmpCodeMap(con);
			Map<String, String>hmServiceMap =  CF.getServicesMap(con,true);
			
			if(getStrMonth() ==null){
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
				setStrYear(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"))+"");
			}
			
			
			if(getStrWLocation()==null){
				setStrWLocation((String)session.getAttribute(WLOCATIONID));
			}
			
			 
//			Map hmWeekEnds = CF.getWeekEndList();
			
			
			
			
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
			List<String> alDates = new ArrayList<String>();
			
			Calendar cal = GregorianCalendar.getInstance();
//			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strPayCycleStart, DATE_FORMAT, "dd")));
//			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strPayCycleStart, DATE_FORMAT, "MM")) -1 );
//			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strPayCycleStart, DATE_FORMAT, "yyyy")));
			
			
			
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1 );
			cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
			
			int maxDays = cal.getActualMaximum(Calendar.DATE);
			int minDays = cal.getActualMinimum(Calendar.DATE);
			
			
			cal.set(Calendar.DAY_OF_MONTH, minDays);
			
			
			alInnerExport.add(new DataStyle("Attendance Muster",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Emp Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			//alInnerExport.add(new DataStyle("SBU",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Gender",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			String strD1 = null;
			for(int i=0; i<maxDays; i++){
				
				strD1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				
				
				alDates.add(uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT));
				alInnerExport.add(new DataStyle(uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				cal.add(Calendar.DATE, 1);
			}
			reportListExport.add(alInnerExport);
			
			
		
			
			
			pst = con.prepareStatement("SELECT * FROM holidays WHERE _date BETWEEN ? AND ? and (is_optional_holiday is null or is_optional_holiday=false) order by _date desc");
			pst.setDate(1, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String,Map<String,Map<String,String>>> holidayWlocationMp=new HashMap<String,Map<String,Map<String,String>>>();
			while (rs.next()) {
				Map<String,Map<String,String>> holidayList=holidayWlocationMp.get(rs.getString("wlocation_id"));
				if(holidayList==null) holidayList=new HashMap<String,Map<String,String>>();

				Map<String,String> list=new HashMap<String,String>();
				list.put("HOLIDAY", rs.getString("description"));
				list.put("COLOR_CODE", rs.getString("colour_code"));
				holidayList.put(rs.getString("_date"),list);
				holidayWlocationMp.put(rs.getString("wlocation_id"),holidayList);
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEndDates = CF.getWeekEndDateList(con, alDates.get(0), alDates.get(alDates.size()-1), CF, uF,hmWeekEndHalfDates,null);
			Map<String, String>hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, alDates.get(0), alDates.get(alDates.size()-1),alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEndDates,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
			
			List<String> alEmployees  = new ArrayList<String>();
			Map<String, List<String>>hmEmpServiceWorkedFor = new HashMap<String, List<String>>();
			List<String> alServices  = new ArrayList<String>();
			
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
			
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true ");
			if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))){
				strQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			}
			
			if(uF.parseToInt(getStrWLocation())>0){
				strQuery.append("and wlocation_id="+uF.parseToInt(getStrWLocation()));
			}if(uF.parseToInt(getDepartment())>0){
				strQuery.append("and depart_id="+uF.parseToInt(getDepartment()));				
			}
			if(uF.parseToInt(getService())>0){
//				strQuery.append("and service_id ="+uF.parseToInt(getService()));
				strQuery.append(" and eod.service_id like '%,"+uF.parseToInt(getService())+",%'");
			}
			strQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(strQuery.toString());
			rs = pst.executeQuery();
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			while(rs.next()){
				
				strEmpIdNew = rs.getString("emp_per_id");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					alServices  = new ArrayList();
				}
				
				alEmployees.add(strEmpIdNew);
				
				
				String []arrServices = null;
				
				
				if(rs.getString("service_id")!=null){
					arrServices = rs.getString("service_id").split(",");
				}
				
				for(int i=0; arrServices!=null && i<arrServices.length; i++){
					if(!alServices.contains(arrServices[0])){
						alServices.add(arrServices[0]);
					}
				}
				
				
				hmEmpServiceWorkedFor.put(strEmpIdNew, alServices);
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ? order by emp_id");
			pst.setDate(1, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmHalfDayAttendance = new HashMap<String, String>();
			Map<String,String> hmEmpAttendance = new HashMap<String,String>();
			Map<String,String> hmEmpLateEarly = new HashMap<String,String>();
			
			while(rs.next()){
				
				strEmpIdNew = rs.getString("emp_id");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					alServices  = new ArrayList<String>();
				}
				
				if(!alServices.contains(rs.getString("service_id"))){
					alServices.add(rs.getString("service_id"));
				}
				hmEmpServiceWorkedFor.put(strEmpIdNew, alServices);
				
				hmEmpAttendance.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id")+"_"+rs.getString("emp_id"), "P");
				if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
					hmEmpLateEarly.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id")+"_"+rs.getString("emp_id"), rs.getString("early_late"));
				}
				
				if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
					double workingHour=rs.getDouble("hours_worked");
					if(workingHour<5){
						hmHalfDayAttendance.put(rs.getString("emp_id")+"_"+uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBDATE, DATE_FORMAT),""+workingHour);
					}
				}
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			
			
//			Map<String, String> hmHolidays = new HashMap<String, String>();
//			Map<String, String> hmHolidayDates = new HashMap<String, String>();
//			CF.getHolidayList(alDates.get(0), alDates.get(alDates.size()-1), CF, hmHolidayDates, hmHolidays, true);

			
			Map<String, Map<String, String>> hmLeaveTypeDays = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmLeavesMap = CF.getLeaveDates(con,alDates.get(0), alDates.get(alDates.size()-1), CF, hmLeaveTypeDays, false, null);
			Map<String, Map<String, String>> hmLeavesMap = CF.getActualLeaveDates(con, CF, uF, alDates.get(0), alDates.get(alDates.size()-1), hmLeaveTypeDays, false, null);
			Map<String, String> hmLeaveColor = new HashMap<String, String>(); 
			CF.getLeavesAttributes(con, uF, hmLeaveColor, null);
			
			
			pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ? order by emp_id");
//			pst.setDate(1, uF.getDateFormat(strPayCycleStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleEnd, DATE_FORMAT));
			pst.setDate(1, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String,String> hmEmpGender=new HashMap<String, String>();
			String strServiceIdNew = null;
			String strServiceIdOld = null;
			
			while(rs.next()){
				
				strEmpIdNew = rs.getString("emp_id");
				strServiceIdNew = rs.getString("service_id");
				String strWLocationId = hmEmpWlocation.get(strEmpIdNew);
				
				
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					alServices  = new ArrayList();
				}
				
				if(!alServices.contains(rs.getString("service_id"))){
					alServices.add(rs.getString("service_id"));
				}
				hmEmpServiceWorkedFor.put(strEmpIdNew, alServices);
				
				hmEmpAttendance.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id")+"_"+rs.getString("emp_id"), "P");
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			
			
			
			request.setAttribute("alDates", alDates);
			request.setAttribute("alEmployees", alEmployees);
			request.setAttribute("hmEmpAttendance", hmEmpAttendance);
			request.setAttribute("hmEmpServiceWorkedFor", hmEmpServiceWorkedFor);
			
			request.setAttribute("hmEmpWlocation", hmEmpWlocation);
//			request.setAttribute("hmWeekEnds", hmWeekEnds);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmServiceMap", hmServiceMap);
						
			
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and is_alive = true order by emp_fname, emp_lname");
			rs = pst.executeQuery();
			while(rs.next()){
				hmEmpGender.put(rs.getString("emp_id"),rs.getString("emp_gender"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpGender", hmEmpGender);
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			List<List<String>> reportListPrint = new ArrayList<List<String>>();
			
			List<String> alInner = new ArrayList<String>();
			List<String> alInnerPrint = new ArrayList<String>();
			
			
			
			List<String> alLegends = new ArrayList<String>();
			
			
			
			
//			alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
//			alInnerExport.add(new DataStyle("SBU",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			
			
			for (int i=0; i<alEmployees.size(); i++){
//				System.out.println("alEmployees.get(i)=+>"+alEmployees.get(i));
				
				Map<String, String> hmEmpLeave = hmLeavesMap.get(alEmployees.get(i));
				if(hmEmpLeave==null)hmEmpLeave = new HashMap<String, String>();
				
				Map<String, String> hmEmpLeaveType = hmLeaveTypeDays.get(alEmployees.get(i));
				if(hmEmpLeaveType==null)hmEmpLeaveType = new HashMap<String, String>();
				
				
				
//				System.out.println("hmLeavesMap=+>"+hmLeavesMap);
//				System.out.println("hmEmpLeave=+>"+hmEmpLeave);
				
				
				
				
				List<String> alServicesInner = hmEmpServiceWorkedFor.get(alEmployees.get(i));
				if(alServicesInner==null)alServicesInner=new ArrayList<String>();
				
				for (int k=0; k<alServicesInner.size(); k++){
					
					
				
					alInner = new ArrayList<String>();
					alInnerPrint = new ArrayList<String>();
					alInnerExport = new ArrayList<DataStyle>();
					
					alInner.add(((k==0)?uF.showData(hmEmpName.get(alEmployees.get(i)),""):""));
					//alInner.add(uF.showData(hmServiceMap.get(alServicesInner.get(k)),""));
					alInner.add(uF.showData(hmEmpGender.get(alEmployees.get(i)),""));
					
					alInnerPrint.add(((k==0)?uF.showData(hmEmpName.get(alEmployees.get(i)),""):""));
					//alInnerPrint.add(uF.showData(hmServiceMap.get(alServicesInner.get(k)),""));
					alInnerPrint.add(uF.showData(hmEmpGender.get(alEmployees.get(i)),""));
					
					alInnerExport.add(new DataStyle(((k==0)?uF.showData(hmEmpName.get(alEmployees.get(i)),""):""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					//alInnerExport.add(new DataStyle(uF.showData(hmServiceMap.get(alServicesInner.get(k)),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpGender.get(alEmployees.get(i)),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					
					for (int ii=0; ii<alDates.size(); ii++){
					
						String strWeekDay = uF.getDateFormat(alDates.get(ii), DATE_FORMAT, "EEEE");
						if(strWeekDay!=null){
							strWeekDay = strWeekDay.toUpperCase();
						}
				
						String strWLocationId = hmEmpWlocation.get(alEmployees.get(i));
						Map<String,Map<String,String>> holidaysMp1=holidayWlocationMp.get(strWLocationId);
						if(holidaysMp1==null) holidaysMp1=new HashMap<String,Map<String,String>>();
						
						String strArrendance = hmEmpAttendance.get(alDates.get(ii)+"_"+alServicesInner.get(k)+"_"+alEmployees.get(i));
						
						Set<String> weeklyOffSet=hmWeekEndDates.get(strWLocationId);
						if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
						
						Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(alEmployees.get(i));
						if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
						
						Set<String> halfDayWeeklyOffSet= hmWeekEndHalfDates.get(strWLocationId);
						if(halfDayWeeklyOffSet==null) halfDayWeeklyOffSet=new HashSet<String>();
						
						java.util.Date dtDate = uF.getDateFormatUtil(alDates.get(ii), DATE_FORMAT);
						java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
						String strColor = null;
						
						if(alEmpCheckRosterWeektype.contains(alEmployees.get(i))){
							if(rosterWeeklyOffSet.contains(alDates.get(ii))){
								strColor=WEEKLYOFF_COLOR;
							}
						}else if(weeklyOffSet.contains(alDates.get(ii))){
							strColor=WEEKLYOFF_COLOR;
						}else if(halfDayWeeklyOffSet.contains(alDates.get(ii))){
							strColor=WEEKLYOFF_COLOR;
						}
						
						if(hmEmpLeave.containsKey(alDates.get(ii))){
							
							if("H".equals(hmEmpLeaveType.get(alDates.get(ii)))){
								
								
								alInner.add("<div style=\"width:100%;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+(strArrendance!=null?"/P":"/A")+"</div>");
								alInnerPrint.add(hmEmpLeave.get(alDates.get(ii))+(strArrendance!=null?"/P":"/A"));
								alInnerExport.add(new DataStyle(hmEmpLeave.get(alDates.get(ii))+"/P",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
								
								if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:20px;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"/P"+"</div>")){
									alLegends.add("<div style=\"float:left;margin-right:5px;width:20px;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"/P"+"</div>");
								}
								
								
								
							}else{
								alInner.add("<div style=\"width:100%;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"</div>");
								alInnerPrint.add(hmEmpLeave.get(alDates.get(ii)));
								alInnerExport.add(new DataStyle((alDates.get(ii)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
								
								if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:20px;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"</div> Leave")){
									alLegends.add("<div style=\"float:left;margin-right:5px;width:20px;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"</div> Leave");
								}
							}
							
							
						}else if(holidaysMp1.containsKey(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat()))){
							if(strArrendance!=null){
								alInner.add("<div style=\"width:100%;height:100%;background-color:"+holidaysMp1.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat()))+";text-align:center\" class=\"greenColor\">P</div>");
								alInnerPrint.add("P");
								alInnerExport.add(new DataStyle("P",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							}else{
								alInner.add("<div style=\"width:100%;height:100%;background-color:"+holidaysMp1.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat()))+";text-align:center\" class=\"blueColor\"> H</div>");
								alInnerPrint.add("H");
								alInnerExport.add(new DataStyle("H",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
								
								if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:20px;height:100%;background-color:"+holidaysMp1.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat()))+";text-align:center\" class=\"blueColor\"> H</div> Public Holiday")){
									alLegends.add("<div style=\"float:left;margin-right:5px;width:20px;height:100%;background-color:"+holidaysMp1.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat()))+";text-align:center\" class=\"blueColor\"> H</div> Public Holiday");
								}
								
							}
							
						}else if(alEmpCheckRosterWeektype.contains(alEmployees.get(i))){
							if(rosterWeeklyOffSet.contains(alDates.get(ii))){
								alInner.add("<div style=\"width:100%;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div>");
								alInnerPrint.add("W/O");
								alInnerExport.add(new DataStyle("W/O",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));

								if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:20px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div> Weekly Off")){
									alLegends.add("<div style=\"float:left;margin-right:5px;width:20px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div> Weekly Off");
								}
								
							}else if(strArrendance!=null){
								if(hmBreakPolicy.containsKey(alEmployees.get(i)+"_"+alDates.get(ii))){
									alInner.add("<div style=\"text-align:center\" class=\"greenColor\">"+hmBreakPolicy.get(alEmployees.get(i)+"_"+alDates.get(ii))+"</div>");
									alInnerPrint.add("P");
									alInnerExport.add(new DataStyle(hmBreakPolicy.get(alEmployees.get(i)+"_"+alDates.get(ii)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
								}else if(hmHalfDayAttendance.containsKey(alEmployees.get(i)+"_"+alDates.get(ii))){
									alInner.add("<div style=\"text-align:center\" class=\"greenColor\">HD/P</div>");
									alInnerPrint.add("HD/P");
									alInnerExport.add(new DataStyle("HD/P",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
								}else{
									alInner.add("<div style=\"text-align:center\" class=\"greenColor\">P</div>");
									alInnerPrint.add("P");
									alInnerExport.add(new DataStyle("P",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
								}
							}else if(dtCurrentDate!=null && dtCurrentDate.after(dtDate)){
								alInner.add("<div style=\"text-align:center\" class=\"redColor\">A</div>");
								alInnerPrint.add("A");
								alInnerExport.add(new DataStyle("A",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							}else{
								alInner.add("");
								alInnerPrint.add("");
								alInnerExport.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							}
							
						}else if(weeklyOffSet.contains(alDates.get(ii))){
							alInner.add("<div style=\"width:100%;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div>");
							alInnerPrint.add("W/O");
							alInnerExport.add(new DataStyle("W/O",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							
							if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:20px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div> Weekly Off")){
								alLegends.add("<div style=\"float:left;margin-right:5px;width:20px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div> Weekly Off");
							}
							
						}else if(halfDayWeeklyOffSet.contains(alDates.get(ii))){
							alInner.add("<div style=\"width:100%;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\">W/HD</div>");
							alInnerPrint.add("W/HD");
							alInnerExport.add(new DataStyle("W/HD",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							
							if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:20px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\">W/HD</div> Half Day Weekly Off")){
								alLegends.add("<div style=\"float:left;margin-right:5px;width:20px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\">W/HD</div> Half Day Weekly Off");
							}
							
						}else if(strArrendance!=null){
							if(hmBreakPolicy.containsKey(alEmployees.get(i)+"_"+alDates.get(ii))){
								alInner.add("<div style=\"text-align:center\" class=\"greenColor\">"+hmBreakPolicy.get(alEmployees.get(i)+"_"+alDates.get(ii))+"</div>");
								alInnerPrint.add("P");
								alInnerExport.add(new DataStyle(hmBreakPolicy.get(alEmployees.get(i)+"_"+alDates.get(ii)),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							}else if(hmHalfDayAttendance.containsKey(alEmployees.get(i)+"_"+alDates.get(ii))){
								alInner.add("<div style=\"text-align:center\" class=\"greenColor\">HD/P</div>");
								alInnerPrint.add("HD/P");
								alInnerExport.add(new DataStyle("HD/P",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							}else{
								alInner.add("<div style=\"text-align:center\" class=\"greenColor\">P</div>");
								alInnerPrint.add("P");
								alInnerExport.add(new DataStyle("P",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							}
						}else if(dtCurrentDate!=null && dtCurrentDate.after(dtDate)){
							alInner.add("<div style=\"text-align:center\" class=\"redColor\">A</div>");
							alInnerPrint.add("A");
							alInnerExport.add(new DataStyle("A",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
						}else{
							alInner.add("");
							alInnerPrint.add("");
							alInnerExport.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
						}
						
					}
					reportList.add(alInner);
					reportListPrint.add(alInnerPrint);
					reportListExport.add(alInnerExport);
			
				}
			}
			
			
			
			request.setAttribute("reportList", reportList);
			request.setAttribute("reportListPrint", reportListPrint);
			session.setAttribute("reportListExport", reportListExport);
			
			request.setAttribute("alLegends", alLegends);
			
			

			if(getPdfGeneration()!=null && getPdfGeneration().equalsIgnoreCase("true")){
				
				AttendanceMusterPdf objPdf = new AttendanceMusterPdf(reportListExport,response);
				objPdf.exportPdf();
				
				}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
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
	String pdfGeneration;

	public String getPdfGeneration() {
		return pdfGeneration;
	}


	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
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

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
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

	public List<FillPayCycles> getPayCycleList() {
		return payCycleList;
	}

	public void setPayCycleList(List<FillPayCycles> payCycleList) {
		this.payCycleList = payCycleList;
	}

	public void setPdfGeneration(String pdfGeneration) {
		this.pdfGeneration = pdfGeneration;
	}

	HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		// 
		this.response=response;
		
	}

}


	class AttendanceMusterPdf {
	
		
		private Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
		private Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
		private Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
		private Font small = new Font(Font.FontFamily.TIMES_ROMAN,7);
		private Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
		

		
		List reportList;
		AttendanceMusterPdf(List reportList, HttpServletResponse response){
			this.reportList = reportList;
			this.response=response;
		}
		

		public void exportPdf(){
			
			
			
			 ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
			 Document document = new Document();
			
			 try{  
					PdfWriter.getInstance(document, bos);
					
					document.open();
						
					Paragraph blankSpace = new Paragraph(" ");
				Paragraph title = new Paragraph("FORM 22",heading);
				title.setAlignment(Element.ALIGN_CENTER);
				Paragraph subTitle = new Paragraph("(Prescribed under Rule 127(2))",heading);
				subTitle.setAlignment(Element.ALIGN_CENTER);
				Paragraph returnType = new Paragraph("Half Yearly Return",normalwithbold);
				returnType.setAlignment(Element.ALIGN_CENTER);
				Paragraph returnPeriod = new Paragraph("For the half year ending June.....................",heading);
				returnPeriod.setAlignment(Element.ALIGN_CENTER);
				Paragraph note = new Paragraph("( This return should be sent to the prescribed authority latest by 15the July of current year)",heading);
				note.setAlignment(Element.ALIGN_CENTER);
				Paragraph registrationNo = new Paragraph("Registration No.............................",normal);
				registrationNo.setIndentationLeft(100);
				Paragraph licenceNo = new Paragraph("Licence No.................................",normal);
				licenceNo.setIndentationLeft(100);
				Paragraph nicCodeNo = new Paragraph("NIC Code No.............................",normal);
				nicCodeNo.setIndentationLeft(100);
				Paragraph licenceDetails = new Paragraph("( As given in the licence)",normal);
				licenceDetails.setIndentationLeft(100);
								
				
				PdfPTable table = new PdfPTable(4);
				table.setWidthPercentage(100);
				
				
				int[] cols = {50,20,10,20};
				table.setWidths(cols);
				
				PdfPCell factoryName1 = new PdfPCell(new Paragraph("1. Name of Factory \n\n\n",normal));
				PdfPCell blank1 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell colon1 = new PdfPCell(new Paragraph(":",normal));
				PdfPCell factoryName2 = new PdfPCell(new Paragraph(" ",normal));
				disableBorder(factoryName1);
				disableBorder(blank1);
				disableBorder(colon1);
				disableBorder(factoryName2);
				table.addCell(factoryName1);
				table.addCell(blank1);
				table.addCell(colon1);
				table.addCell(factoryName2);
				
				PdfPCell occupierName1 = new PdfPCell(new Paragraph("2. Name of occupier \n\n\n",normal));
				PdfPCell blank2 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell colon2 = new PdfPCell(new Paragraph(":",normal));
				PdfPCell occupierName2 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(occupierName1);
				disableBorder(blank2);
				disableBorder(colon2);
				disableBorder(occupierName2);
				
				table.addCell(occupierName1);
				table.addCell(blank2);
				table.addCell(colon2);
				table.addCell(occupierName2);
				
				PdfPCell managerName1 = new PdfPCell(new Paragraph("3. Name of Manager \n\n\n",normal));
				PdfPCell blank3 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell colon3 = new PdfPCell(new Paragraph(":",normal));
				PdfPCell managerName2 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(managerName1);
				disableBorder(blank3);
				disableBorder(colon3);
				disableBorder(managerName2);
				
				table.addCell(managerName1);
				table.addCell(blank3);
				table.addCell(colon3);
				table.addCell(managerName2);
				
				PdfPCell districtName1 = new PdfPCell(new Paragraph("4. District \n\n\n",normal));
				PdfPCell blank4 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell colon4 = new PdfPCell(new Paragraph(":",normal));
				PdfPCell districtName2 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(districtName1);
				disableBorder(blank4);
				disableBorder(colon4);
				disableBorder(districtName2);
				
				table.addCell(districtName1);
				table.addCell(blank4);
				table.addCell(colon4);
				table.addCell(districtName2);
				
				PdfPCell factoryAddress1 = new PdfPCell(new Paragraph("5 Full Postal Address of the factory (including PIN code)",normal));
				PdfPCell blank5 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell colon5 = new PdfPCell(new Paragraph(":",normal));
				PdfPCell factoryAddress2 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(factoryAddress1);
				disableBorder(blank5);
				disableBorder(colon5);
				disableBorder(factoryAddress2);
				
				table.addCell(factoryAddress1);
				table.addCell(blank5);
				table.addCell(colon5);
				table.addCell(factoryAddress2);
				
				PdfPCell industry = new PdfPCell(new Paragraph("1. Industry",normal));
				PdfPCell blank6 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank7 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank8 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(industry);
				disableBorder(blank6);
				disableBorder(blank7);
				disableBorder(blank8);
				
				table.addCell(industry);
				table.addCell(blank6);
				table.addCell(blank7);
				table.addCell(blank8);
				
				PdfPCell natureOfIndustry = new PdfPCell(new Paragraph("a) Nature of industry \n (see Explanatory Note)",normal));
				PdfPCell blank9 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank10 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank11 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(natureOfIndustry);
				disableBorder(blank9);
				disableBorder(blank10);
				disableBorder(blank11);
				
				table.addCell(natureOfIndustry);
				table.addCell(blank9);
				table.addCell(blank10);
				table.addCell(blank11);
				
				PdfPCell sectorOfIndustry = new PdfPCell(new Paragraph("b) Sector of Industry (Mention whether \n Establishment belongs to Public or \n Private Sector(See Explanatory Note 2)",normal));
				PdfPCell blank12 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank13 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank14 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(sectorOfIndustry);
				disableBorder(blank12);
				disableBorder(blank13);
				disableBorder(blank14);
				
				table.addCell(sectorOfIndustry);
				table.addCell(blank12);
				table.addCell(blank13);
				table.addCell(blank14);
				
				PdfPCell actOfCompany = new PdfPCell(new Paragraph("c) Section of the Act under which the \n Factory is covered (Please \n Please tick( ) the appropriate box )",normal));
				PdfPCell section = new PdfPCell(new Paragraph("2m (i)\n 2m (ii) \n Section 85 ",normal));
				PdfPCell blank15 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank16 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(actOfCompany);
				disableBorder(section);
				disableBorder(blank15);
				disableBorder(blank16);
				
				table.addCell(actOfCompany);
				table.addCell(blank15);
				table.addCell(section);
				table.addCell(blank16);
				
				PdfPCell workingDaysInYear = new PdfPCell(new Paragraph("2. Number of days factory worked during the \n Half year ending 30th june",normal));
				PdfPCell colon17 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell colon6 = new PdfPCell(new Paragraph(":",normal));
				PdfPCell blank19 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(workingDaysInYear);
				disableBorder(colon17);
				disableBorder(colon6);
				disableBorder(blank19);
				
				table.addCell(workingDaysInYear);
				table.addCell(colon17);
				table.addCell(colon6);
				table.addCell(blank19);
				
				PdfPCell noOfEmpWorked = new PdfPCell(new Paragraph("3. Number of man-days worked during the \n Half year ending 30th June",normal));
				PdfPCell blank20 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell colon7 = new PdfPCell(new Paragraph(":",normal));
				PdfPCell blank21 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(noOfEmpWorked);
				disableBorder(blank20);
				disableBorder(colon7);
				disableBorder(blank21);
				
				table.addCell(noOfEmpWorked);
				table.addCell(blank20);
				table.addCell(colon7);
				table.addCell(blank21);
				
				PdfPCell adults1 = new PdfPCell(new Paragraph("a) Adults",normal));
				PdfPCell menOrWomen1 = new PdfPCell(new Paragraph("i) Men \nii) Women ",normal));
				PdfPCell blank22 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank23 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(adults1);
				disableBorder(menOrWomen1);
				disableBorder(blank22);
				disableBorder(blank23);
				
				table.addCell(adults1);
				table.addCell(menOrWomen1);
				table.addCell(blank22);
				table.addCell(blank23);
				
				PdfPCell adolescent1 = new PdfPCell(new Paragraph("b) Adolescent",normal));
				PdfPCell maleOrFemale1 = new PdfPCell(new Paragraph("i) Male \nii) Female ",normal));
				PdfPCell blank24 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank25 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(adolescent1);
				disableBorder(maleOrFemale1);
				disableBorder(blank24);
				disableBorder(blank25);
				
				table.addCell(adolescent1);
				table.addCell(maleOrFemale1);
				table.addCell(blank24);
				table.addCell(blank25);
				
				PdfPCell children1 = new PdfPCell(new Paragraph("c) Children",normal));
				PdfPCell boysOrGirl1 = new PdfPCell(new Paragraph("i) Boys \nii) Girls ",normal));
				PdfPCell blank26 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank27 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(children1);
				disableBorder(boysOrGirl1);
				disableBorder(blank26);
				disableBorder(blank27);
				
				table.addCell(children1);
				table.addCell(boysOrGirl1);
				table.addCell(blank26);
				table.addCell(blank27);
				
				PdfPCell blank28 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell total1 = new PdfPCell(new Paragraph(" Total ",normal));
				PdfPCell colon8 = new PdfPCell(new Paragraph(": ",normal));
				PdfPCell blank29 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(blank28);
				disableBorder(total1);
				disableBorder(colon8);
				disableBorder(blank29);
				
				table.addCell(blank28);
				table.addCell(total1);
				table.addCell(colon8);
				table.addCell(blank29);
				//---
				PdfPCell avgNoOfEmpWorkedDaily = new PdfPCell(new Paragraph("4. Average number of workers employed daily \n (See Explanatory Note -3)",normal));
				PdfPCell blank30 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank31 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank32 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(avgNoOfEmpWorkedDaily);
				disableBorder(blank30);
				disableBorder(blank31);
				disableBorder(blank32);
				
				table.addCell(avgNoOfEmpWorkedDaily);
				table.addCell(blank30);
				table.addCell(blank31);
				table.addCell(blank32);
				
				PdfPCell adults2 = new PdfPCell(new Paragraph("a) Adults",normal));
				PdfPCell menOrWomen2 = new PdfPCell(new Paragraph("i) Men \nii) Women ",normal));
				PdfPCell blank33 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank34 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(adults2);
				disableBorder(menOrWomen2);
				disableBorder(blank33);
				disableBorder(blank34);
				
				table.addCell(adults2);
				table.addCell(menOrWomen2);
				table.addCell(blank33);
				table.addCell(blank34);
				
				PdfPCell adolescent2 = new PdfPCell(new Paragraph("b) Adolescent",normal));
				PdfPCell maleOrFemale2 = new PdfPCell(new Paragraph("i) Male \nii) Female ",normal));
				PdfPCell blank35 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank36 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(adolescent2);
				disableBorder(maleOrFemale2);
				disableBorder(blank35);
				disableBorder(blank36);
				
				table.addCell(adolescent2);
				table.addCell(maleOrFemale2);
				table.addCell(blank35);
				table.addCell(blank36);
				
				PdfPCell children2 = new PdfPCell(new Paragraph("c) Children",normal));
				PdfPCell boysOrGirl2 = new PdfPCell(new Paragraph("i) Boys \nii) Girls ",normal));
				PdfPCell blank37 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank38 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(children2);
				disableBorder(boysOrGirl2);
				disableBorder(blank37);
				disableBorder(blank38);
				
				table.addCell(children2);
				table.addCell(boysOrGirl2);
				table.addCell(blank37);
				table.addCell(blank38);
				
				PdfPCell blank39 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell total2 = new PdfPCell(new Paragraph(" Total ",normal));
				PdfPCell colon9 = new PdfPCell(new Paragraph(": ",normal));
				PdfPCell blank40 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(blank39);
				disableBorder(total2);
				disableBorder(colon9);
				disableBorder(blank40);
				
				table.addCell(blank39);
				table.addCell(total2);
				table.addCell(colon9);
				table.addCell(blank40);
				
				PdfPCell medicalInfo = new PdfPCell(new Paragraph("5. Medical information \n a) Total number of workers employed in Hazardous processes \n b) Name of the hazardous agents \n c) Number of medical officers employed :",normal));
				PdfPCell blank41 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank42 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank43 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(medicalInfo);
				disableBorder(blank41);
				disableBorder(blank42);
				disableBorder(blank43);
				
				table.addCell(medicalInfo);
				table.addCell(blank41);
				table.addCell(blank42);
				table.addCell(blank43);
				
				PdfPCell fullTime = new PdfPCell(new Paragraph("i) Full-time",normal));
				PdfPCell colon10 = new PdfPCell(new Paragraph(":",normal));
				PdfPCell blank44 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank45 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(fullTime);
				disableBorder(colon10);
				disableBorder(blank44);
				disableBorder(blank45);
				
				table.addCell(fullTime);
				table.addCell(colon10);
				table.addCell(blank44);
				table.addCell(blank45);
				
				PdfPCell partTime = new PdfPCell(new Paragraph("ii) Part time",normal));
				PdfPCell colon11 = new PdfPCell(new Paragraph(":",normal));
				PdfPCell blank46 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank47 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(partTime);
				disableBorder(colon11);
				disableBorder(blank46);
				disableBorder(blank47);
				
				table.addCell(partTime);
				table.addCell(colon11);
				table.addCell(blank46);
				table.addCell(blank47);
				
				PdfPCell examinedWorkers = new PdfPCell(new Paragraph("d) Number of workers examined by \n Factory Medical Officer \n",normal));
				PdfPCell blank48 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank49 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank50 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(examinedWorkers);
				disableBorder(blank48);
				disableBorder(blank49);
				disableBorder(blank50);
				
				table.addCell(examinedWorkers);
				table.addCell(blank48);
				table.addCell(blank49);
				table.addCell(blank50);
				
				PdfPCell workersInHarzordsProcess = new PdfPCell(new Paragraph("i) Workers working in hazardous process:",normal));
				PdfPCell colon12 = new PdfPCell(new Paragraph(":",normal));
				PdfPCell blank51 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank52 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(workersInHarzordsProcess);
				disableBorder(colon12);
				disableBorder(blank51);
				disableBorder(blank52);
				
				table.addCell(workersInHarzordsProcess);
				table.addCell(colon12);
				table.addCell(blank51);
				table.addCell(blank52);
				
				PdfPCell others = new PdfPCell(new Paragraph("ii) Others \n\n",normal));
				PdfPCell blank53 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank54 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell blank55 = new PdfPCell(new Paragraph(" ",normal));
				
				disableBorder(others);
				disableBorder(blank53);
				disableBorder(blank54);
				disableBorder(blank55);
				
				table.addCell(others);
				table.addCell(blank53);
				table.addCell(blank54);
				table.addCell(blank55);
				
				PdfPCell date = new PdfPCell(new Paragraph("Date :",normal));
				PdfPCell blank56 = new PdfPCell(new Paragraph(" ",normal));
				PdfPCell signature = new PdfPCell(new Paragraph("Signature of Manager \n ( Name in Block letters) ",normal));
				signature.setColspan(2);
				disableBorder(date);
				disableBorder(blank56);
				disableBorder(signature);
				table.addCell(date);
				table.addCell(blank56);
				table.addCell(signature);
				
				Paragraph noteTitle = new Paragraph("Explanatory Note",normal);
				noteTitle.setAlignment(Element.ALIGN_CENTER);
				
				Paragraph noteInDetail = new Paragraph("1. Mention what is actually manufactured including repairs of all types," +
						" following the NIG Code at the four digit level. \n" +
						" 2. Establishment in ‘Public Sector’ means an establishment owned, controlled" +
						" or managed by(i) The Government or the Department of the Government," +
						" or(ii) a Government Company as defined in Section 617 of the Companies" +
						" Act 1956, or (iii) a Corporation established by or under Central, Provincial or" +
						" State Act, which is owned, controlled, managed by the Government, or (iv) a" +
						" Local authority.\n\n" +
						" Establishment in ‘Joint Sector’ means an establishment managed, by the" +
						" Government and Private Entrepreneur.\n\n" +
						" Establishment in ‘Co-operative Sector’ means an establishment " +
						" managed, by Co-operative society registered under the Co-operative" +
						" societies Act, 1912. \n\n" +
						"3. (i) Working day should be taken to be a day on which the establishment" +
						"actually worked and manufacturing process was carried on including the" +
						"day on which although no manufacturing process was carried on but more" +
						"than 50% of the workers (preceding the date under consideration) were" +
						"deployed on maintenance and repair work, etc. on close days. Days on" +
						"which the factory was closed for whatever cause and days on which no" +
						"manufacturing process was carried on should not be treated as working days." +
						"(i) For seasonal factories* information about working season and off" +
						"season should be given separately. \n\n" +
						"‘Seasonal Factory’ means a factory which is exclusively engaged in one or" +
						"more of the following manufacturing processes namely cotton ginning, cotton" +
						"or jute pressing decortication of ground nuts, the manufacture of coffee," +
						"indigo, lac, rubber, sugar (including gur) or tea or any manufacturing process" +
						"which is incidental to or connected with any of the aforesaid processes, and" +
						"includes a factory which is engaged for a period of not exceeding seven months in a year.\n\n" +
						"(a) in any process of blending, packing or re-packing of tea or coffee, \n or \n" +
						"(b) in such other manufacturing process as the Central Government" +
						"may, by notification in the Official Gazettee, specify.\n\n" +
						"The expression ‘manufacturing process’ and ‘power’ shall have the" +
						"meanings respectively assigned to them in the Factories Act, 1948 (63 of 1948) \n\n" +
						"4. Mandays worked should be the aggregate number of attendance of all the" +
						"workers, covered, under the Act, in all shifts on all the working days. In" +
						"reckoning attendance, should be counted and all employees should be" +
						"included, whether they are employed directly or under contractors" +
						"(Apprentices, who are not covered under the apprentices Act, 1961, are also" +
						"to be included). Attendance on separate shift (e.g. night and day shifts)" +
						"should be counted separately. Partial attendance for less than half a shift on" +
						"a working days should be ignored while attendance for half a shift or more" +
						"on such day should be treated as full attendance. \n" +
						"5. The average daily number should be calculated by dividing the aggregate" +
						"number of attendance (man-days worked) on working days by the number of" +
						"working days during the half-year.",normal); 
				
				
				document.add(title);
				document.add(subTitle);
				document.add(returnType);
				document.add(returnPeriod);
				document.add(note);
				document.add(registrationNo);
				document.add(licenceNo);
				document.add(nicCodeNo);
				document.add(blankSpace);
				document.add(licenceDetails);
				document.add(blankSpace);
				document.add(table);
				document.add(blankSpace);
				document.add(noteInDetail);
					
					
					document.close();
					
					
					response.setContentType("application/pdf");         
					 response.setContentLength(bos.size());
					 response.setHeader("Content-Disposition", "attachment; filename=AttendanceMuster_Pdf.pdf");
					
					 ServletOutputStream out = response.getOutputStream();         
					 bos.writeTo(out);         
					 out.flush();      
					 bos.close();
					 out.close();
						
			}catch(Exception e){
				e.printStackTrace();
			}
		
			
		}
		
		private HttpServletResponse response;
		
		public void disableBorder(PdfPCell cell){
			cell.disableBorderSide(Rectangle.TOP);
			cell.disableBorderSide(Rectangle.BOTTOM);
			cell.disableBorderSide(Rectangle.LEFT);
			cell.disableBorderSide(Rectangle.RIGHT);
			
		}
	
}