package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillDepartment;
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

public class OverTimeHoursRegister  extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(AttendanceRegister.class);
	
	String paycycle;
	String strMonth;
	String strYear; 
	String strWLocation;
	String department;
	String service;
	String f_org;
	
	List<FillOrganisation> organisationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillMonth> monthList;
	List<FillYears> yearList;
	List<FillWLocation> wLocationList;
	
	List<FillPayCycles> payCycleList; 
	
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(TITLE, "Overtime Hours Register");
		request.setAttribute(PAGE, "/jsp/tms/OverTimeHoursRegister.jsp");
		

		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;  
		}*/

		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		//viewAttendanceRegister(uF);
		viewOvertimeHoursRegister(uF);

		return loadOvertimeHoursRegister(uF);

	}
	
	
	
	
	private void viewOvertimeHoursRegister(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			if(getStrMonth() ==null){
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
				setStrYear(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"))+"");
			}
			
			
			if(getStrWLocation()==null){
				setStrWLocation((String)session.getAttribute(WLOCATIONID));
			}
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
			List<String> alDates = new ArrayList<String>();
			
			Calendar cal = GregorianCalendar.getInstance();
			
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1 );
			cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
			
			int maxDays = cal.getActualMaximum(Calendar.DATE);
			int minDays = cal.getActualMinimum(Calendar.DATE);
			
			
			cal.set(Calendar.DAY_OF_MONTH, minDays);
			
			
			alInnerExport.add(new DataStyle("Overtime Hours Register",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Emp Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			String strD1 = null;
			for(int i=0; i<maxDays; i++){
				
				strD1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				
				
				alDates.add(uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT));
				alInnerExport.add(new DataStyle(uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				cal.add(Calendar.DATE, 1);
			}
			reportListExport.add(alInnerExport);
			
			
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con, null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			Map hmServiceMap =  CF.getServicesMap(con, true);
			Map hmEmpWlocation = CF.getEmpWlocationMap(con);
			
			List alEmployees  = new ArrayList();
			Map hmEmpServiceWorkedFor = new HashMap();
			List alServices  = new ArrayList();
			
			
			
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
				strQuery.append(" and service_id like '%,"+uF.parseToInt(getService())+",%'");
			}
			if(uF.parseToInt(getF_org())>0){
				strQuery.append(" and org_id ="+uF.parseToInt(getF_org()));
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
			
			
			
//			Map hmHolidays = new HashMap();
//			Map hmHolidayDates = new HashMap();
//			CF.getHolidayList(con, (String)alDates.get(0), (String)alDates.get(alDates.size()-1), CF, hmHolidayDates, hmHolidays, true);
			
			Map hmLeaveTypeDays = new HashMap();
//			Map hmLeavesMap = CF.getLeaveDates(con, (String)alDates.get(0), (String)alDates.get(alDates.size()-1), CF, hmLeaveTypeDays, false, null);
			Map hmLeavesMap = CF.getActualLeaveDates(con, CF, uF, (String)alDates.get(0), (String)alDates.get(alDates.size()-1), hmLeaveTypeDays, false, null);
			Map<String, String> hmLeaveColor = new HashMap<String, String>(); 
			CF.getLeavesAttributes(con, uF, hmLeaveColor, null);
			
			
			pst = con.prepareStatement("select * from overtime_hours where to_date(_date::text, 'YYYY-MM-DD') between ? and ? order by emp_id");
			pst.setDate(1, uF.getDateFormat((String)alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat((String)alDates.get(alDates.size()-1), DATE_FORMAT));
			
			rs = pst.executeQuery();
			
			
			Map hmEmpAttendance = new HashMap();
			Map hmEmpLateEarly = new HashMap();
						
			String strServiceIdNew = null;
			String strServiceIdOld = null;
			
			while(rs.next()){
				
				strEmpIdNew = rs.getString("emp_id");
				
				hmEmpAttendance.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_"+rs.getString("emp_id"), uF.formatIntoTwoDecimal(rs.getDouble("approved_ot_hours")));
				
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
			
			
			
			
			
			
			
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			List<List<String>> reportListPrint = new ArrayList<List<String>>();
			
			List<String> alInner = new ArrayList<String>();
			List<String> alInnerPrint = new ArrayList<String>();
			
			
			
			List alLegends = new ArrayList();
			for (int i=0; i<alEmployees.size(); i++){
//				System.out.println("(String)alEmployees.get(i)=+>"+(String)alEmployees.get(i));
				
				/*List<String> alServicesInner = (List)hmEmpServiceWorkedFor.get((String)alEmployees.get(i));
				if(alServicesInner==null)alServicesInner=new ArrayList<String>();
				
				for (int k=0; k<alServicesInner.size(); k++){*/
					
					
				
					alInner = new ArrayList<String>();
					alInnerPrint = new ArrayList<String>();
					alInnerExport = new ArrayList<DataStyle>();
					
					alInner.add(uF.showData((String)hmEmpName.get((String)alEmployees.get(i)),""));
					//alInner.add(uF.showData((String)hmServiceMap.get((String)alServicesInner.get(k)),""));
					
					alInnerPrint.add(uF.showData((String)hmEmpName.get((String)alEmployees.get(i)),""));
					//alInnerPrint.add(uF.showData((String)hmServiceMap.get((String)alServicesInner.get(k)),""));
					
					alInnerExport.add(new DataStyle(uF.showData((String)hmEmpName.get((String)alEmployees.get(i)),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					//alInnerExport.add(new DataStyle(uF.showData((String)hmServiceMap.get((String)alServicesInner.get(k)),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					
					for (int ii=0; ii<alDates.size(); ii++){
					
						String strWeekDay = uF.getDateFormat((String)alDates.get(ii), DATE_FORMAT, "EEEE");
						if(strWeekDay!=null){
							strWeekDay = strWeekDay.toUpperCase();
						}
				
						
						String strOvertime = (String)hmEmpAttendance.get((String)alDates.get(ii)+"_"+(String)alEmployees.get(i));
					
						
						java.util.Date dtDate = uF.getDateFormatUtil((String)alDates.get(ii), DATE_FORMAT);
						java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
						
//						String strColor = (String)hmWeekEnds.get(strWeekDay+"_"+strWLocationId);
						//String strColor = (String)hmWeekEndDates.get((String)alDates.get(ii)+"_"+strWLocationId);
		
						
						
						
//					System.out.println((String)alDates.get(ii)+"_"+strWLocationId+" hmWeekEndDates===>"+hmWeekEndDates );
						
						if(strOvertime!=null && !strOvertime.equals("")){
							alInner.add("<div style=\"text-align:center\" class=\"greenColor\">"+strOvertime+"</div>");
							alInnerPrint.add(strOvertime);
							alInnerExport.add(new DataStyle(strOvertime,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
						}else{
							alInner.add("");
							alInnerPrint.add("");
							alInnerExport.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
						}
						
					}
					reportList.add(alInner);
					reportListPrint.add(alInnerPrint);
					reportListExport.add(alInnerExport);
			
//				}
			}
			
			
			
			request.setAttribute("reportList", reportList);
			request.setAttribute("reportListPrint", reportListPrint);
			session.setAttribute("reportListExport", reportListExport);
			
			request.setAttribute("alLegends", alLegends);
			
			
			
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}




	public String loadOvertimeHoursRegister(UtilityFunctions uF) {
		
		
		
		payCycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		monthList = new FillMonth().fillMonth();
		yearList = new FillYears().fillFutureYears(uF.getCurrentDate(CF.getStrTimeZone()), 2, 2);
		organisationList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		
		return LOAD;
	}
	
	
	public String viewAttendanceRegister(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			
			
			
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
			
			
			alInnerExport.add(new DataStyle("Overtime Hours Register",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Emp Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			alInnerExport.add(new DataStyle("SBU",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			String strD1 = null;
			for(int i=0; i<maxDays; i++){
				
				strD1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				
				
				alDates.add(uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT));
				alInnerExport.add(new DataStyle(uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				cal.add(Calendar.DATE, 1);
			}
			reportListExport.add(alInnerExport);
			
			
			con = db.makeConnection(con);
			Map hmEmpName = CF.getEmpNameMap(con, null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			Map hmServiceMap =  CF.getServicesMap(con, true);
			
			
//			Map<String, Set<String>> hmWeekEndDates = CF.getWeekEndDateList(con, alDates.get(0), alDates.get(alDates.size()-1), CF, uF,null,null);
			Map hmEmpWlocation = CF.getEmpWlocationMap(con);
			
			List alEmployees  = new ArrayList();
			Map hmEmpServiceWorkedFor = new HashMap();
			List alServices  = new ArrayList();
			
			
			
			
			/*if(uF.parseToInt(getStrWLocation())==0){
				pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true order by emp_fname, emp_lname");
			}else{
				pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and eod.wlocation_id = ?  and is_alive = true order by emp_fname, emp_lname");
				pst.setInt(1, uF.parseToInt(getStrWLocation()));
			}*/
			
			
			StringBuilder strQuery = new StringBuilder();
			
			strQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true ");
			if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))){
				strQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			}
			
			if(uF.parseToInt(getStrWLocation())>0){
				strQuery.append("and wlocation_id="+uF.parseToInt(getStrWLocation()));
			}if(uF.parseToInt(getDepartment())>0){
				strQuery.append("and depart_id="+uF.parseToInt(getDepartment()));				
			}if(uF.parseToInt(getService())>0){
//				strQuery.append("and service_id ="+uF.parseToInt(getService()));
				strQuery.append(" and service_id like '%"+uF.parseToInt(getService())+",%'");
			}
			if(uF.parseToInt(getF_org())>0){
				strQuery.append(" and org_id ="+uF.parseToInt(getF_org()));
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
			
			
			
//			Map hmHolidays = new HashMap();
//			Map hmHolidayDates = new HashMap();
//			CF.getHolidayList(con, (String)alDates.get(0), (String)alDates.get(alDates.size()-1), CF, hmHolidayDates, hmHolidays, true);
			
			Map hmLeaveTypeDays = new HashMap();
//			Map hmLeavesMap = CF.getLeaveDates(con, (String)alDates.get(0), (String)alDates.get(alDates.size()-1), CF, hmLeaveTypeDays, false, null);
			Map hmLeavesMap = CF.getActualLeaveDates(con, CF, uF, (String)alDates.get(0), (String)alDates.get(alDates.size()-1), hmLeaveTypeDays, false, null);
			Map<String, String> hmLeaveColor = new HashMap<String, String>(); 
			CF.getLeavesAttributes(con, uF, hmLeaveColor, null);
			
			
			pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ? order by emp_id");
			pst.setDate(1, uF.getDateFormat((String)alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat((String)alDates.get(alDates.size()-1), DATE_FORMAT));
			
			rs = pst.executeQuery();
			
			
			Map hmEmpAttendance = new HashMap();
			Map hmEmpLateEarly = new HashMap();
						
			String strServiceIdNew = null;
			String strServiceIdOld = null;
			
			while(rs.next()){
				
				strEmpIdNew = rs.getString("emp_id");
				strServiceIdNew = rs.getString("service_id");
				String strWLocationId = (String)hmEmpWlocation.get(strEmpIdNew);
				
				
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					alServices  = new ArrayList();
				}
				
				if(!alServices.contains(rs.getString("service_id"))){
					alServices.add(rs.getString("service_id"));
				}
				
				
				
				
				hmEmpServiceWorkedFor.put(strEmpIdNew, alServices);
				
				
				hmEmpAttendance.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id")+"_"+rs.getString("emp_id"), "P");
				if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
					hmEmpLateEarly.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id")+"_"+rs.getString("emp_id"), rs.getString("early_late"));
				}
				
				
				
				
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
			
			
			
			
			
			
			
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			List<List<String>> reportListPrint = new ArrayList<List<String>>();
			
			List<String> alInner = new ArrayList<String>();
			List<String> alInnerPrint = new ArrayList<String>();
			
			
			
			List alLegends = new ArrayList();
			
			
			
			
//			alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
//			alInnerExport.add(new DataStyle("SBU",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			
			Map hmINBreaks = new HashMap();
			
			for (int i=0; i<alEmployees.size(); i++){
//				System.out.println("(String)alEmployees.get(i)=+>"+(String)alEmployees.get(i));
				
				Map hmEmpLeave = (Map)hmLeavesMap.get((String)alEmployees.get(i));
				if(hmEmpLeave==null)hmEmpLeave = new HashMap();
				
				Map hmEmpLeaveType = (Map)hmLeaveTypeDays.get((String)alEmployees.get(i));
				if(hmEmpLeaveType==null)hmEmpLeaveType = new HashMap();
				
//				String strWLocationId = (String)hmEmpWlocation.get((String)alEmployees.get(i));
//				Set<String> weeklyOffSet= hmWeekEndDates.get(strWLocationId);
//				System.out.println("hmLeavesMap=+>"+hmLeavesMap);
//				System.out.println("hmEmpLeave=+>"+hmEmpLeave);
				
				
				
				
				List<String> alServicesInner = (List)hmEmpServiceWorkedFor.get((String)alEmployees.get(i));
				if(alServicesInner==null)alServicesInner=new ArrayList<String>();
				
				for (int k=0; k<alServicesInner.size(); k++){
					
					
				
					alInner = new ArrayList<String>();
					alInnerPrint = new ArrayList<String>();
					alInnerExport = new ArrayList<DataStyle>();
					
					alInner.add(((k==0)?uF.showData((String)hmEmpName.get((String)alEmployees.get(i)),""):""));
					//alInner.add(uF.showData((String)hmServiceMap.get((String)alServicesInner.get(k)),""));
					
					alInnerPrint.add(((k==0)?uF.showData((String)hmEmpName.get((String)alEmployees.get(i)),""):""));
					//alInnerPrint.add(uF.showData((String)hmServiceMap.get((String)alServicesInner.get(k)),""));
					
					alInnerExport.add(new DataStyle(((k==0)?uF.showData((String)hmEmpName.get((String)alEmployees.get(i)),""):""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					//alInnerExport.add(new DataStyle(uF.showData((String)hmServiceMap.get((String)alServicesInner.get(k)),""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
					
					for (int ii=0; ii<alDates.size(); ii++){
					
						String strWeekDay = uF.getDateFormat((String)alDates.get(ii), DATE_FORMAT, "EEEE");
						if(strWeekDay!=null){
							strWeekDay = strWeekDay.toUpperCase();
						}
				
						
						String strArrendance = (String)hmEmpAttendance.get((String)alDates.get(ii)+"_"+(String)alServicesInner.get(k)+"_"+(String)alEmployees.get(i));
					
						
						java.util.Date dtDate = uF.getDateFormatUtil((String)alDates.get(ii), DATE_FORMAT);
						java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
						
//						String strColor = (String)hmWeekEnds.get(strWeekDay+"_"+strWLocationId);
//						String strColor = (String)hmWeekEndDates.get((String)alDates.get(ii)+"_"+strWLocationId);
//						String strColor ="";
//						if(weeklyOffSet.contains(alDates.get(ii))){
//							strColor=WEEKLYOFF_COLOR;
//						}
						
						
						
//					System.out.println((String)alDates.get(ii)+"_"+strWLocationId+" hmWeekEndDates===>"+hmWeekEndDates );
						
						
						
						if(dtCurrentDate!=null && dtCurrentDate.after(dtDate)){
							alInner.add("<div style=\"text-align:center\" class=\"redColor\">A</div>");
							alInnerPrint.add("A");
							alInnerExport.add(new DataStyle("A",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							
							
						}else{
							alInner.add("");
							alInnerPrint.add("");
							alInnerExport.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
							
							
//							System.out.println((String)alDates.get(ii)+"== 7 ==>"+"");
							
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


	public String getStrWLocation() {
		return strWLocation;
	}


	public void setStrWLocation(String strWLocation) {
		this.strWLocation = strWLocation;
	}


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
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
