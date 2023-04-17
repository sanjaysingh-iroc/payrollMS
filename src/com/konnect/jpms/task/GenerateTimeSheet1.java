package com.konnect.jpms.task;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class GenerateTimeSheet1 implements ServletRequestAware, ServletResponseAware, IStatements, IConstants {

	CommonFunctions CF;
	HttpSession session;
	HttpServletRequest request;
	HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {

		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	HSSFSheet monthly_time_sheet;
	HSSFSheet conveyance_sheet;
	HSSFWorkbook workbook;

	{
		workbook = new HSSFWorkbook();
		monthly_time_sheet = workbook.createSheet("Time Report");
		conveyance_sheet = workbook.createSheet("Conveyance Sheet");
	}

	public String execute() {

		
//		Map<String, Map<String, Map<String, String>>> projectdetails = new LinkedHashMap<String, Map<String, Map<String, String>>>();
		List<String> nameheaderlist = new ArrayList<String>();
		List<String> alTaskIds = new ArrayList<String>();
		Map<String, List<String>> hmTaskAndSubTaskIds = new LinkedHashMap<String, List<String>>();
		Map<String, List<String>> hmTaskAndSubTaskData = new LinkedHashMap<String, List<String>>();
		List<Integer> leavesdaylist = new ArrayList<Integer>();
		List<Integer> holidayslist = new ArrayList<Integer>();
		Map<String, Map<String, String>> hmfinaltask = new HashMap<String, Map<String, String>>();
		List<String> idList = new ArrayList<String>();
		Map<String, List<String>> mapconveyancetravel = new HashMap<String, List<String>>();
		Map<String, List<String>> mapconveyanceothers = new HashMap<String, List<String>>();
		try {
			session = request.getSession();
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if (CF == null)
				return "login";
			UtilityFunctions UF = new UtilityFunctions();
			if (UF.parseToInt(getDownloadSubmit()) == 1) {

				saveTimesheet(UF, CF);
				return "submit";
			} else {
//				fillsheet(projectdetails, nameheaderlist, leavesdaylist, holidayslist, hmfinaltask, idList, mapconveyancetravel, mapconveyanceothers);
				fillsheet(alTaskIds, hmTaskAndSubTaskIds, hmTaskAndSubTaskData, nameheaderlist, leavesdaylist, holidayslist, hmfinaltask, idList, mapconveyancetravel, mapconveyanceothers);
//				createExcelFile(projectdetails, nameheaderlist, leavesdaylist, holidayslist, hmfinaltask, idList, mapconveyancetravel, mapconveyanceothers);
				createExcelFile(alTaskIds, hmTaskAndSubTaskIds, hmTaskAndSubTaskData, nameheaderlist, leavesdaylist, holidayslist, hmfinaltask, idList, mapconveyancetravel, mapconveyanceothers);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return null;
	}

	int coloumncountmain = 0;
	String downloadSubmit;
	String datefrom;
	String strPC;

	public String getDatefrom() {
		return datefrom;
	}

	public void setDatefrom(String reportdatefrom) {
		this.datefrom = reportdatefrom;
	}

	String dateto;

	public String getDateto() {
		return dateto;
	}

	public void setDateto(String reportdateto) {
		this.dateto = reportdateto;
	}

	String empid;

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public void saveTimesheet(UtilityFunctions uF, CommonFunctions CF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		try {
			con = db.makeConnection(con);

			pst = con
					.prepareStatement("insert into project_timesheet (timesheet_paycycle, timesheet_from, timesheet_to, emp_id, timesheet_generated_date) values (?,?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getStrPC()));
			pst.setDate(2, uF.getDateFormat(getDatefrom(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getDateto(), DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getEmpid()));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();

			pst = con
					.prepareStatement("insert into task_activity(task_date, start_time,end_time,emp_id,activity,_comment,sent,is_billable,issent_timesheet, timesheet_start_date,timesheet_end_date, timesheet_paycycle) values(?,?,?,?,?,?,'n',?,?,?,?,?)");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setTime(2, uF.getCurrentTime(CF.getStrTimeZone()));
			pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(getEmpid()));
			pst.setString(5, "Timesheet Sent");
			pst.setString(6, "Timesheet sent ");
			pst.setBoolean(7, false);
			pst.setBoolean(8, true);
			pst.setDate(9, uF.getDateFormat(getDatefrom(), DATE_FORMAT));
			pst.setDate(10, uF.getDateFormat(getDateto(), DATE_FORMAT));
			pst.setInt(11, uF.parseToInt(getStrPC()));
			pst.executeUpdate();
			pst.close();

			request.setAttribute("STATUS_MSG", "<span style=\"color: green; font-size: 10px; float: right; width: 200px;\">Timesheet submitted successfully!!!</span>");

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("STATUS_MSG", "<span style=\"color: red; font-size: 10px; float: right; width: 200px;\">Timesheet could not be sent!!!</span>");
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void createExcelFile(List<String> alTaskIds, Map<String, List<String>> hmTaskAndSubTaskIds, Map<String, List<String>> hmTaskAndSubTaskData, List<String> nameheaderlist, List<Integer> leavesdaylist, List<Integer> holidayslist,
			Map<String, Map<String, String>> hmfinaltask, List<String> idList, Map<String, List<String>> mapconveyancetravel, Map<String, List<String>> mapconveyanceothers)
			throws IOException {
		FileOutputStream fos = null;
		try {
			writeTimeSheetReport(alTaskIds, hmTaskAndSubTaskIds, hmTaskAndSubTaskData, nameheaderlist, leavesdaylist, holidayslist, hmfinaltask, idList);
			writeConveyanceSheetReport(nameheaderlist, mapconveyancetravel, mapconveyanceothers);
			String reportName = getReportName(); // get report name as per
													// client requirement.

			/*
			 * fos = new FileOutputStream(new File("/home/dailyhrz/Desktop/" +
			 * reportName + ".xls")); workbook.write(fos);
			 */

			FileOutputStream fileOut = null;
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			try {
				workbook.write(buffer);
				buffer.close();

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			response.setHeader("Content-Disposition", "attachment; filename=\"" + reportName + ".xls\"");
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());

			try {
				ServletOutputStream op = response.getOutputStream();
				op = response.getOutputStream();
				op.write(buffer.toByteArray());
				op.flush();
				op.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	
	public void fillsheet(List<String> alTaskIds, Map<String, List<String>> hmTaskAndSubTaskIds, Map<String, List<String>> hmTaskAndSubTaskData, List<String> nameheaderlist, List<Integer> leavesdaylist, List<Integer> holidayslist,
			Map<String, Map<String, String>> hmfinaltask, List<String> idList, Map<String, List<String>> mapconveyancetravel, Map<String, List<String>> mapconveyanceothers)
			throws SQLException {

		Connection con = null;
		UtilityFunctions UF = new UtilityFunctions();
		ResultSet rs = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		boolean flagMiddleName = UF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		PreparedStatement pst = null;
		session = request.getSession();
		UtilityFunctions uF = new UtilityFunctions();
		CF = (CommonFunctions) session.getAttribute("CF");
	
		// ******** LEAVES.......

		Map<String, Map<String, String>> hmleaves = CF.getActualLeaveDates(con, CF, uF, getDatefrom(), getDateto(), null, true, null);
		Map<String, String> innerleaves = null;

		if (hmleaves.keySet().contains(getEmpid())) {
			innerleaves = hmleaves.get(getEmpid());
		}
		if (innerleaves != null) {
			Set datesset = innerleaves.keySet();
			Iterator itr = datesset.iterator();
			while (itr.hasNext()) {
				String datestring = itr.next().toString();
				leavesdaylist.add(uF.parseToInt(uF.getDateFormat(datestring, DATE_FORMAT, "dd")));

			}

		}

		pst = con.prepareStatement("select epd.emp_fname,emp_mname,epd.emp_mname,epd.emp_lname,epd.empcode,emp_bank_acct_nbr,emp_pan_no,di.dept_name from employee_personal_details epd join employee_official_details eod on(epd.emp_per_id=eod.emp_id) join department_info di on(eod.depart_id=di.dept_id) where epd.emp_per_id=?");
		pst.setInt(1, UF.parseToInt(getEmpid()));
		rs1 = pst.executeQuery();
		while (rs1.next()) {
			
			String strEmpMName = "";
			if(flagMiddleName) {
				if(rs1.getString("emp_mname") != null && rs1.getString("emp_mname").trim().length()>0) {
					strEmpMName = " "+rs1.getString("emp_mname");
				}
			}
			
			nameheaderlist.add(rs1.getString("emp_fname") +strEmpMName+" "+rs1.getString("emp_lname"));
			nameheaderlist.add(rs1.getString("empcode"));
			nameheaderlist.add(rs1.getString("emp_bank_acct_nbr"));
			nameheaderlist.add(rs1.getString("emp_pan_no"));
			nameheaderlist.add(rs1.getString("dept_name"));
		}
		rs1.close();
		pst.close();
		
		// strQuery.append("select client_name,task_date,sum(actual_hrs) as actual_hrs from(select client_name,activity_name,task_date,SUM(actual_hrs) as actual_hrs from(select clnt.client_name,pmnt.client_id ,ai.pro_id,ai.activity_name,ta.activity_id,ta.task_date,ta.actual_hrs from task_activity ta  join activity_info ai on ta.activity_id=ai.task_id  join projectmntnc pmnt on ai.pro_id=pmnt.pro_id join client_details clnt on clnt.client_id=pmnt.client_id where ta.emp_id='1207') as a   group by client_name,activity_name,task_date order by client_name)as c group by client_name,task_date order by client_name");
		// System.out.println(strQuery.toString());
		try {
			pst = con.prepareStatement("select activity_id, activity from task_activity where emp_id =? and (activity_id is null or activity_id=0)and task_date between ? and ? order by activity");
			pst.setInt(1, uF.parseToInt(getEmpid()));
			pst.setDate(2, UF.getDateFormat(getDatefrom(), DATE_FORMAT));
			pst.setDate(3, UF.getDateFormat(getDateto(), DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(!alTaskIds.contains(rs.getString("activity"))) {
					alTaskIds.add(rs.getString("activity"));
				}
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select task_id, pro_id, activity_name, parent_task_id from activity_info ai where " +
//				"resource_ids like '%,"+getEmpid()+",%' order by pro_id");
			pst = con.prepareStatement("select ai.task_id, p.pro_id, p.pro_name, p.client_id, c.client_name, ai.activity_name, " +
					"ai.parent_task_id from activity_info ai, projectmntnc p, client_details c where c.client_id = p.client_id " +
					"and ai.pro_id = p.pro_id and ai.resource_ids like '%,"+getEmpid()+",%' order by p.pro_id");
			rs = pst.executeQuery();
//			System.out.println("pst =======>> " + pst);
			List<String> alSubTaskIds = new ArrayList<String>();
			List<String> alTaskData = new ArrayList<String>();
			while(rs.next()) {
				if(rs.getInt("parent_task_id") > 0) {
					alSubTaskIds = hmTaskAndSubTaskIds.get(rs.getString("parent_task_id"));
					if(alSubTaskIds == null) alSubTaskIds = new ArrayList<String>();
					alSubTaskIds.add(rs.getString("task_id"));
					hmTaskAndSubTaskIds.put(rs.getString("parent_task_id"), alSubTaskIds);
					
					alTaskData = hmTaskAndSubTaskData.get(rs.getString("parent_task_id"));
					if(alTaskData == null) alTaskData = new ArrayList<String>();
					alTaskData.add(rs.getString("task_id"));
					alTaskData.add(rs.getString("pro_name"));
					alTaskData.add(rs.getString("client_name"));
					alTaskData.add(rs.getString("activity_name"));
					alTaskData.add(rs.getString("parent_task_id"));
					hmTaskAndSubTaskData.put(rs.getString("parent_task_id"), alTaskData);
				}
				if(rs.getInt("parent_task_id") == 0) {
					alTaskIds.add(rs.getString("task_id"));
					
					alTaskData = hmTaskAndSubTaskData.get(rs.getString("task_id"));
					if(alTaskData == null) alTaskData = new ArrayList<String>();
					alTaskData.add(rs.getString("task_id"));
					alTaskData.add(rs.getString("pro_name"));
					alTaskData.add(rs.getString("client_name"));
					alTaskData.add(rs.getString("activity_name"));
					alTaskData.add(rs.getString("parent_task_id"));
					hmTaskAndSubTaskData.put(rs.getString("task_id"), alTaskData);
				}
			}
			rs.close();
			pst.close();
//			System.out.println("hmTaskAndSubTaskData ===>> " + hmTaskAndSubTaskData);

//			pst = con.prepareStatement("select client_name,task_location,task_date,SUM(actual_hrs) as actual_hrs from (select clnt.client_name,pmnt.client_id ,ai.pro_id,ai.activity_name,ta.activity_id,ta.task_date,ta.actual_hrs,task_location from task_activity ta left join activity_info ai on ta.activity_id=ai.task_id left join projectmntnc pmnt on ai.pro_id=pmnt.pro_id left join client_details clnt on clnt.client_id=pmnt.client_id where ta.emp_id=? and task_date>=? and task_date<=?) as a  group by client_name,task_date,task_location order by client_name");
			pst = con.prepareStatement("select client_name,task_location,task_date,activity_id,activity, SUM(actual_hrs) as actual_hrs from " +
					"(select clnt.client_name,pmnt.client_id ,ai.pro_id,ai.activity_name,ta.activity,ta.activity_id,ta.task_date,ta.actual_hrs,task_location " +
					"from task_activity ta left join activity_info ai on ta.activity_id=ai.task_id left join projectmntnc pmnt on ai.pro_id=pmnt.pro_id " +
					"left join client_details clnt on clnt.client_id=pmnt.client_id where ta.emp_id=? and task_date>=? and task_date<=?) as a " +
					"group by client_name,activity,activity_id,task_date,task_location order by client_name");
			pst.setInt(1, UF.parseToInt(getEmpid()));
			pst.setDate(2, UF.getDateFormat(getDatefrom(), DATE_FORMAT));
			pst.setDate(3, UF.getDateFormat(getDateto(), DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmTaskDataDatewise = new HashMap<String, Map<String, String>>();
			Map<String, String> hmDatewiseTotalHours = new HashMap<String, String>();
			while (rs.next()) {
				String activityId = rs.getString("activity_id");
				if(rs.getInt("activity_id") == 0) {
					activityId = rs.getString("activity");
				}
				
				if (rs.getString("task_location") != null && rs.getString("task_location").equalsIgnoreCase("ONS")) {
					Map<String, String> hmInnerData = hmTaskDataDatewise.get(activityId+"_"+rs.getString("task_location"));
					if (hmInnerData == null) hmInnerData = new HashMap<String, String>();
					hmInnerData.put(rs.getString("task_date"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("actual_hrs"))));
					hmTaskDataDatewise.put(activityId+"_"+rs.getString("task_location"), hmInnerData);

				} else if (rs.getString("task_location") == null || rs.getString("task_location").equalsIgnoreCase("OFS") || rs.getString("task_location").equalsIgnoreCase("")) {
					Map<String, String> hmInnerData = hmTaskDataDatewise.get(activityId+"_OFS");
					if (hmInnerData == null) hmInnerData = new HashMap<String, String>();
					hmInnerData.put(rs.getString("task_date"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("actual_hrs"))));
					hmTaskDataDatewise.put(activityId+"_OFS", hmInnerData);
				}
				
				
				double dblTotHrs = uF.parseToDouble(hmDatewiseTotalHours.get(rs.getString("task_date")));
				dblTotHrs += rs.getDouble("actual_hrs");
				hmDatewiseTotalHours.put(rs.getString("task_date"), dblTotHrs+"");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmTaskDataDatewise", hmTaskDataDatewise);
			request.setAttribute("hmDatewiseTotalHours", hmDatewiseTotalHours);
			
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekendMap = CF.getWeekEndDateList(con, getDatefrom(), getDateto(), CF, uF, hmWeekEndHalfDates, null);
//			String strWLocationId = hmEmpWLocation.get(getEmpid());
			
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, getDatefrom(), getDateto(), alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekendMap, hmEmpLevelMap, hmEmpWLocation, hmWeekEndHalfDates);

			Map hmHolidays = new HashMap();
			Map hmHolidayDates = new HashMap();
			CF.getHolidayList(con, request, getDatefrom(), getDateto(), CF,hmHolidayDates, hmHolidays, true);
			
			request.setAttribute("hmWeekendMap", hmWeekendMap);
			request.setAttribute("hmRosterWeekEndDates", hmRosterWeekEndDates);
			request.setAttribute("alEmpCheckRosterWeektype", alEmpCheckRosterWeektype);
			
			request.setAttribute("hmHolidayDates", hmHolidayDates);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);

		}

		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "dd")));
		cal.set(Calendar.MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "MM")) - 1);
		cal.set(Calendar.YEAR, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "yyyy")));

		Calendar cal1 = GregorianCalendar.getInstance();
		cal1.set(Calendar.DAY_OF_MONTH, UF.parseToInt(UF.getDateFormat(getDateto(), DATE_FORMAT, "dd")));
		cal1.set(Calendar.MONTH, UF.parseToInt(UF.getDateFormat(getDateto(), DATE_FORMAT, "MM")) - 1);
		cal1.set(Calendar.YEAR, UF.parseToInt(UF.getDateFormat(getDateto(), DATE_FORMAT, "yyyy")));

		coloumncountmain = daysBetween(cal.getTime(), cal1.getTime()); 

	}
	
	
	
		
	// // filling all cell values from database ....
	/*public void fillsheet(Map<String, Map<String, Map<String, String>>> projectdetails, List<String> nameheaderlist, List<Integer> leavesdaylist, List<Integer> holidayslist,
			Map<String, Map<String, String>> hmfinaltask, List<String> idList, Map<String, List<String>> mapconveyancetravel, Map<String, List<String>> mapconveyanceothers)
			throws SQLException {

		Connection con = null;

		ResultSet rs = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		PreparedStatement pst = null;
		session = request.getSession();
		UtilityFunctions uF = new UtilityFunctions();
		CF = (CommonFunctions) session.getAttribute("CF");
		UtilityFunctions UF = new UtilityFunctions();
		// ******** LEAVES.......

//		Map<String, Map<String, String>> hmleaves = CF.getLeaveDates(con, getDatefrom(), getDateto(), CF, null, true, null);
		Map<String, Map<String, String>> hmleaves = CF.getActualLeaveDates(con, CF, uF, getDatefrom(), getDateto(), null, true, null);
		Map<String, String> innerleaves = null;

		if (hmleaves.keySet().contains(getEmpid())) {
			innerleaves = hmleaves.get(getEmpid());
		}
		if (innerleaves != null) {
			Set datesset = innerleaves.keySet();
			Iterator itr = datesset.iterator();
			while (itr.hasNext()) {
				String datestring = itr.next().toString();
//				leavesdaylist.add(Integer.parseInt(datestring.substring(0, 2)));
				leavesdaylist.add(uF.parseToInt(uF.getDateFormat(datestring, DATE_FORMAT, "dd")));

			}

		}

		// ********** HOLIDAYS.....
		
		 * Map hmWeekendMap = getWeekEndDateList(con, getDatefrom(),
		 * getDateto(), CF, UF); System.out.println("Listing holidayslist"
		 * +hmWeekendMap);
		 * 
		 * Set<String> set=hmWeekendMap.keySet(); Set<Integer> finaldateset=new
		 * HashSet<Integer>(); Iterator iterator=set.iterator();
		 * while(iterator.hasNext()){
		 * finaldateset.add(Integer.parseInt(iterator.
		 * next().toString().substring(0,2))); }
		 * iterator=finaldateset.iterator(); while(iterator.hasNext()){
		 * holidayslist.add(Integer.parseInt(iterator.next().toString())); }
		 * System.out.println(holidayslist+
		 * "Printing holoidays list==============================");
		 
		StringBuilder strQuery = new StringBuilder();
		// String str =
		// "select epd.emp_fname,epd.emp_lname,epd.empcode,emp_bank_acct_nbr,emp_pan_no,di.dept_name from employee_personal_details epd join employee_official_details eod on(epd.emp_per_id=eod.emp_id) join department_info di on(eod.depart_id=di.dept_id) where epd.emp_per_id=?";

		 pst = con.prepareStatement("select epd.emp_fname,epd.emp_lname,epd.empcode,emp_bank_acct_nbr,emp_pan_no,di.dept_name from employee_personal_details epd join employee_official_details eod on(epd.emp_per_id=eod.emp_id) join department_info di on(eod.depart_id=di.dept_id) where epd.emp_per_id=?");
		pst.setInt(1, UF.parseToInt(getEmpid()));
		rs1 = pst.executeQuery();

		while (rs1.next()) {
			nameheaderlist.add(rs1.getString("emp_fname") + " "+rs1.getString("emp_lname"));
			nameheaderlist.add(rs1.getString("empcode"));
			nameheaderlist.add(rs1.getString("emp_bank_acct_nbr"));
			nameheaderlist.add(rs1.getString("emp_pan_no"));
			nameheaderlist.add(rs1.getString("dept_name"));

		}
		rs1.close();
		pst.close();
		
		// strQuery.append("select client_name,task_date,sum(actual_hrs) as actual_hrs from(select client_name,activity_name,task_date,SUM(actual_hrs) as actual_hrs from(select clnt.client_name,pmnt.client_id ,ai.pro_id,ai.activity_name,ta.activity_id,ta.task_date,ta.actual_hrs from task_activity ta  join activity_info ai on ta.activity_id=ai.task_id  join projectmntnc pmnt on ai.pro_id=pmnt.pro_id join client_details clnt on clnt.client_id=pmnt.client_id where ta.emp_id='1207') as a   group by client_name,activity_name,task_date order by client_name)as c group by client_name,task_date order by client_name");
		// System.out.println(strQuery.toString());
		try {

			pst = con.prepareStatement("select client_name,task_location,task_date,SUM(actual_hrs) as actual_hrs from (select clnt.client_name,pmnt.client_id ,ai.pro_id,ai.activity_name,ta.activity_id,ta.task_date,ta.actual_hrs,task_location from task_activity ta left join activity_info ai on ta.activity_id=ai.task_id left join projectmntnc pmnt on ai.pro_id=pmnt.pro_id left join client_details clnt on clnt.client_id=pmnt.client_id where ta.emp_id=? and task_date>=? and task_date<=?) as a  group by client_name,task_date,task_location order by client_name");
			pst.setInt(1, UF.parseToInt(getEmpid()));
			pst.setDate(2, UF.getDateFormat(getDatefrom(), DATE_FORMAT));
			pst.setDate(3, UF.getDateFormat(getDateto(), DATE_FORMAT));
			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			// Map<String,Map<String,Map<String,String>>> projectdetails=new
			// HashMap<String,Map<String,Map<String,String>>>();
			while (rs.next()) {
				Map<String, Map<String, String>> a = projectdetails.get(rs.getString("client_name"));
				if (a == null)
					a = new HashMap<String, Map<String, String>>();
				if (rs.getString("task_location") != null && rs.getString("task_location").equalsIgnoreCase("ONS")) {
					Map<String, String> b = a.get(rs.getString("task_location"));
					if (b == null)
						b = new HashMap<String, String>();
					b.put(rs.getString("task_date"), uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rs.getString("actual_hrs"))));
					a.put(rs.getString("task_location"), b);
					projectdetails.put(rs.getString("client_name"), a);

				} else if (rs.getString("task_location") == null || rs.getString("task_location").equalsIgnoreCase("OFS") || rs.getString("task_location").equalsIgnoreCase("")) {
					Map<String, String> b = a.get("OFS");
					if (b == null)
						b = new HashMap<String, String>();
					b.put(rs.getString("task_date"), uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rs.getString("actual_hrs"))));
					a.put("OFS", b);
					projectdetails.put(rs.getString("client_name"), a);
				}

			}
			rs.close();
			pst.close();

			if (projectdetails != null) {
				List checklist = new ArrayList<String>();
//				String strquerytask = "select client_name,activity_name,task_location from activity_info ai join projectmntnc using(pro_id) join client_details using(client_id) join task_activity ta on ta.activity_id=ai.task_id  where ai.emp_id=? and ai.taskstatus!='New Task' group by client_name,activity_name,task_location order by client_name";
//				String strquerytask = "select client_name,activity_name,task_location from activity_info ai join projectmntnc using(pro_id) join client_details using(client_id) join task_activity ta on ta.activity_id=ai.task_id  where ai.emp_id=? group by client_name,activity_name,task_location order by client_name";
				String strquerytask = "select client_name,activity_name, task_location, ta.activity from activity_info ai right join projectmntnc using(pro_id) right join client_details using(client_id) right join task_activity ta on ta.activity_id=ai.task_id  where ta.emp_id=? group by client_name,activity_name, ta.activity,task_location order by client_name ";
				pst = con.prepareStatement(strquerytask);
				pst.setInt(1, UF.parseToInt(getEmpid()));
				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();

				while (rs.next()) {
					Map<String, String> innerhmfinaltask = hmfinaltask.get(rs.getString("client_name"));
					if (innerhmfinaltask == null) {
						innerhmfinaltask = new HashMap<String, String>();
						if(rs.getString("activity_name")!=null){
							innerhmfinaltask.put(rs.getString("task_location"), rs.getString("activity_name"));
						}else{
							innerhmfinaltask.put(rs.getString("task_location"), rs.getString("activity"));
						}
						hmfinaltask.put(rs.getString("client_name"), innerhmfinaltask);
					} else {
						if (innerhmfinaltask.keySet().contains(rs.getString("task_location"))) {
							String tasknew = innerhmfinaltask.get(rs.getString("task_location"));
							
							if(rs.getString("activity_name")!=null){
								tasknew = tasknew + "," + rs.getString("activity_name");
							}else{
								tasknew = tasknew + "," + rs.getString("activity");
							}
							innerhmfinaltask.put(rs.getString("task_location"), tasknew);
						} else {
							if(rs.getString("activity_name")!=null){
								innerhmfinaltask.put(rs.getString("task_location"), rs.getString("activity_name"));
							}else{
								innerhmfinaltask.put(rs.getString("task_location"), rs.getString("activity"));
							}
							hmfinaltask.put(rs.getString("client_name"), innerhmfinaltask);
						}
					}
				}
				rs.close();
				pst.close();
				System.out.println("hmfinaltask ===>> " + hmfinaltask);
			}
			int no_recordstrav = 0;

			// conveyance sheet statement
			pst = con.prepareStatement("select is_billable,reimbursement_amount,travel_mode,no_person,travel_from,travel_to,no_days, travel_distance,travel_rate ,reimbursement_info,client_name from (select is_billable,reimbursement_amount,travel_mode,no_person,travel_from,travel_to,no_days,travel_distance,travel_rate ,reimbursement_info,reimbursement_type from emp_reimbursement  ereim where  ereim.emp_id=? and from_date=? and to_date = ? and reimbursement_type1='P' and approval_1=1 and approval_2=1) as a join  projectmntnc pmnt on cast(pmnt.pro_id as text)=a.reimbursement_type join client_details using(client_id) ");
			

			// statement.setDate(1, UF.getDateFormat(getDatefrom(),
			// DATE_FORMAT));
			// statement.setDate(2, UF.getDateFormat(getDateto(),
			// DATE_FORMAT));
			pst.setInt(1, UF.parseToInt(getEmpid()));
			pst.setDate(2, UF.getDateFormat(getDatefrom(), DATE_FORMAT));
			pst.setDate(3, UF.getDateFormat(getDateto(), DATE_FORMAT));

			rs = pst.executeQuery();
			while (rs.next()) {
				no_recordstrav++;
				List<String> conveyancelist = new ArrayList<String>();
				conveyancelist.add(rs.getString("travel_mode"));
				conveyancelist.add(rs.getString("no_person"));
				if (rs.getString("is_billable").equalsIgnoreCase("t")) {
					conveyancelist.add("YES");
				} else {
					conveyancelist.add("NO");
				}
				conveyancelist.add(rs.getString("travel_from"));
				conveyancelist.add(rs.getString("travel_to"));
				conveyancelist.add(rs.getString("no_days"));
				conveyancelist.add(rs.getString("travel_distance"));
				conveyancelist.add(rs.getString("travel_rate"));
				conveyancelist.add(rs.getString("reimbursement_amount"));

				mapconveyancetravel.put(no_recordstrav + rs.getString("client_name"), conveyancelist);

			}
			rs.close();
			pst.close();
			int no_rec_others = 0;
			// making conveyance others"""""
			PreparedStatement statementothers = con
					.prepareStatement("select is_billable,reimbursement_amount,travel_mode,no_person,travel_from,travel_to,no_days,travel_distance,travel_rate ,reimbursement_info from emp_reimbursement where approval_1=1 and approval_2=1 and (reimbursement_type1!='P') and emp_id=?  and from_date=? and to_date = ?");
			// statementothers.setDate(1, UF.getDateFormat(getDatefrom(),
			// DATE_FORMAT));
			// statementothers.setDate(2, UF.getDateFormat(getDateto(),
			// DATE_FORMAT));
			statementothers.setInt(1, UF.parseToInt(getEmpid()));
			statementothers.setDate(2, UF.getDateFormat(getDatefrom(), DATE_FORMAT));
			statementothers.setDate(3, UF.getDateFormat(getDateto(), DATE_FORMAT));

			rs = statementothers.executeQuery();
			while (rs.next()) {

				List<String> conveyancelist = new ArrayList<String>();
				conveyancelist.add(rs.getString("travel_mode"));
				conveyancelist.add(rs.getString("no_person"));

				if (rs.getString("is_billable").equalsIgnoreCase("t")) {
					conveyancelist.add("YES");
				} else {
					conveyancelist.add("NO");
				}
				conveyancelist.add(rs.getString("travel_from"));
				conveyancelist.add(rs.getString("travel_to"));
				conveyancelist.add(rs.getString("no_days"));
				conveyancelist.add(rs.getString("travel_distance"));
				conveyancelist.add(rs.getString("travel_rate"));
				conveyancelist.add(rs.getString("reimbursement_amount"));

				if (rs.getString("reimbursement_info") != null && rs.getString("reimbursement_info").equalsIgnoreCase("Travel")) {
					mapconveyancetravel.put(no_recordstrav + rs.getString("reimbursement_info"), conveyancelist);
				} else {
					no_rec_others++;
					mapconveyanceothers.put(no_rec_others + rs.getString("reimbursement_info"), conveyancelist);
				}
			}
			rs.close();
			pst.close();
			
			
			
			
			
			
			pst = con.prepareStatement("select * from project_timesheet pt left join employee_personal_details epd on epd.emp_per_id = pt.approved_by where emp_id = ? and timesheet_from<=? and timesheet_to>=?");
			pst.setInt(1, UF.parseToInt(getEmpid()));
			pst.setDate(2, UF.getDateFormat(getDatefrom(), DATE_FORMAT));
			pst.setDate(3, UF.getDateFormat(getDateto(), DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			int nApproved = 0;
			int timesheetId = 0;
			while(rs.next()){
				nApproved = UF.parseToInt(rs.getString("is_approved"));
				timesheetId = UF.parseToInt(rs.getString("timesheet_id"));
				
				hmApproveMap.put("SUBMITTED_ON", UF.getDateFormat(rs.getString("timesheet_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				if(rs.getString("emp_fname")!=null){
					hmApproveMap.put("APPROVED_BY", rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("nApproved", nApproved+"");
			request.setAttribute("timesheetId", timesheetId+"");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);

		}

		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "dd")));
		cal.set(Calendar.MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "MM")) - 1);
		cal.set(Calendar.YEAR, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "yyyy")));

		Calendar cal1 = GregorianCalendar.getInstance();
		cal1.set(Calendar.DAY_OF_MONTH, UF.parseToInt(UF.getDateFormat(getDateto(), DATE_FORMAT, "dd")));
		cal1.set(Calendar.MONTH, UF.parseToInt(UF.getDateFormat(getDateto(), DATE_FORMAT, "MM")) - 1);
		cal1.set(Calendar.YEAR, UF.parseToInt(UF.getDateFormat(getDateto(), DATE_FORMAT, "yyyy")));

		coloumncountmain = daysBetween(cal.getTime(), cal1.getTime()); 

	}*/

	Map<String, String> hmApproveMap = new HashMap<String, String>();
	
	public int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}

//	public void writeTimeSheetReport(Map<String, Map<String, Map<String, String>>> projectdetails, List<String> nameheaderlist, List<Integer> leavesdaylist,
//			List<Integer> holidayslist, Map<String, Map<String, String>> hmfinaltask, List<String> idList) {
//
//		Database db = new Database();
//		db.setRequest(request);
//		Connection con = null;
//		UtilityFunctions UF = new UtilityFunctions();
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		
//		int lessspace = 0;
//		if (coloumncountmain < 10)
//			lessspace = 1;
//
//		try {
//			con=db.makeConnection(con);	
//			Calendar calD = Calendar.getInstance();
//			calD.set(Calendar.DATE, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "dd")));
//			calD.set(Calendar.MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "MM"))-1);
//			calD.set(Calendar.YEAR, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "yyyy")));
//			
//			
//			String strDate = UF.getDateFormat(calD.get(Calendar.DATE)+"/"+ (calD.get(Calendar.MONTH)+1)+"/"+calD.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//			List alDates = new ArrayList();
//			
//			for(int i=0; i<31; i++){
//				strDate = UF.getDateFormat(calD.get(Calendar.DATE)+"/"+ (calD.get(Calendar.MONTH)+1)+"/"+calD.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//				
//				alDates.add(calD.get(Calendar.DATE));
//				calD.add(Calendar.DATE, 1);
//			}
//						
//
//			Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
//			if (hmEmpWlocation == null)
//				hmEmpWlocation = new HashMap();
//			String strWlocationId = hmEmpWlocation.get(getEmpid());
//
//			Row firmNameRow = monthly_time_sheet.createRow(5);
//			firmNameRow.setHeight((short) 450);
//			monthly_time_sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
//			for (int i = 0; i < 7 + coloumncountmain; i++) {
//				HSSFCellStyle styleForFirmName = workbook.createCellStyle();
//				Font firmNameFont = workbook.createFont();
//				firmNameFont.setFontHeight((short) 320);
//				firmNameFont.setBoldweight((short) 1000);
//				styleForFirmName.setFont(firmNameFont);
//				styleForFirmName.setBorderTop(CellStyle.BORDER_THIN);
//				Cell firmNameCell = firmNameRow.createCell(i);
//				if (i == 0) {
//					styleForFirmName.setBorderLeft(CellStyle.BORDER_THIN);
//					firmNameCell.setCellValue(" " + CF.getStrOrgName());
//				}
//				if (i == 6 + coloumncountmain) {
//					styleForFirmName.setBorderRight(CellStyle.BORDER_THIN);
//				}
//				firmNameCell.setCellStyle(styleForFirmName);
//			}
//
//			Row firmTypeRow = monthly_time_sheet.createRow(6);
//			monthly_time_sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));
//			for (int i = 0; i < 7 + coloumncountmain; i++) {
//				HSSFCellStyle styleForFirmType = workbook.createCellStyle();
//				Font firmTypeFont = workbook.createFont();
//				firmTypeFont.setFontHeight((short) 225);
//				firmTypeFont.setBoldweight((short) 1000);
//				styleForFirmType.setFont(firmTypeFont);
//				styleForFirmType.setBorderBottom(CellStyle.BORDER_THIN);
//				Cell firmTypeCell = firmTypeRow.createCell(i);
//				if (i == 0) {
//					styleForFirmType.setBorderLeft(CellStyle.BORDER_THIN);
//					firmTypeCell.setCellValue(" " + CF.getStrOrgSubTitle());
//				}
//				if (i == 6 + coloumncountmain) {
//					styleForFirmType.setBorderRight(CellStyle.BORDER_THIN);
//				}
//				firmTypeCell.setCellStyle(styleForFirmType);
//			}
//
//			Row firstBlankRow = monthly_time_sheet.createRow(7);
//			forBlankRowLeftAndRightBorder(firstBlankRow);
//
//			Row clientNameRow = monthly_time_sheet.createRow(8);
//			HSSFCellStyle rightBorderForClientSummary = workbook.createCellStyle();
//			rightBorderForClientSummary.setBorderRight(CellStyle.BORDER_THIN);
//			HSSFCellStyle clientSummary = workbook.createCellStyle();
//
//			Font clientNameCellFont = workbook.createFont();
//			clientNameCellFont.setFontHeight((short) 230);
//
//			Cell clientNameCell = clientNameRow.createCell(0);
//			clientNameCell.setCellValue(" Name : " + nameheaderlist.get(0));
//			clientSummary.setFont(clientNameCellFont);
//			clientSummary.setBorderLeft(CellStyle.BORDER_THIN);
//			clientNameCell.setCellStyle(clientSummary);
//
//			clientSummary.setFont(clientNameCellFont);
//			clientSummary.setBorderLeft(CellStyle.BORDER_THIN);
//
//			Cell clientBankACNoCell = clientNameRow.createCell(coloumncountmain);
//			clientBankACNoCell.setCellValue(" Bank A/C No.: " + UF.showData(nameheaderlist.get(2), ""));
//
//			Cell lastCellOfClientSummary1 = clientNameRow.createCell(coloumncountmain + 6);
//			lastCellOfClientSummary1.setCellStyle(rightBorderForClientSummary);
//
//			Row divisionRow = monthly_time_sheet.createRow(9);
//			divisionRow.setHeight((short) 300);
//			Cell clientDivisionCell = divisionRow.createCell(0);
//			clientDivisionCell.setCellValue(" Department : " + nameheaderlist.get(4));
//			clientDivisionCell.setCellStyle(clientSummary);
//
//			HSSFCellStyle cellStyleforReportName = workbook.createCellStyle();
//			Font reportNameFont = workbook.createFont();
//			reportNameFont.setFontHeight((short) 240);
//			reportNameFont.setBoldweight((short) 600);
//			cellStyleforReportName.setFont(reportNameFont);
//
//			Cell reportNameCell;
//			if (lessspace == 1) {
//				reportNameCell = divisionRow.createCell(4);
//			} else {
//				reportNameCell = divisionRow.createCell(6 + (coloumncountmain / 2));
//			}
//			reportNameCell.setCellValue("MONTHLY TIME REPORT");
//			reportNameCell.setCellStyle(cellStyleforReportName);
//
//			Cell clientPANNoCell = divisionRow.createCell(coloumncountmain);
//			clientPANNoCell.setCellValue(" PAN : " + nameheaderlist.get(3));
//
//			Cell lastCellOfClientSummary2 = divisionRow.createCell(coloumncountmain + 6);
//			lastCellOfClientSummary2.setCellStyle(rightBorderForClientSummary);
//
//			Row empCodeRow = monthly_time_sheet.createRow(10);
//			Cell clientEmpIdCell = empCodeRow.createCell(0);
//			clientEmpIdCell.setCellValue(" Employee Code : " + nameheaderlist.get(1));
//			clientEmpIdCell.setCellStyle(clientSummary);
//
//			Cell clientWorkingHrsCell = empCodeRow.createCell(coloumncountmain);
//			clientWorkingHrsCell.setCellValue(" ( Standard Hours per Day  :  8 ) : ");
//
//			Cell lastCellOfClientSummary3 = empCodeRow.createCell(coloumncountmain + 6);
//			lastCellOfClientSummary3.setCellStyle(rightBorderForClientSummary);
//
//			Row secondBlankRow = monthly_time_sheet.createRow(11);
//			forBlankRowLeftAndRightBorder(secondBlankRow);
//
//			Row headingRowDesc = monthly_time_sheet.createRow(12);
//			headingRowDesc.setHeight((short) 500);
//			HSSFCellStyle headingStyle = workbook.createCellStyle();
//			headingStyle.setBorderLeft(CellStyle.BORDER_THIN);
//			headingStyle.setBorderRight(CellStyle.BORDER_THIN);
//			headingStyle.setBorderTop(CellStyle.BORDER_THIN);
//			headingStyle.setAlignment(CellStyle.ALIGN_CENTER);
//			headingStyle.setWrapText(true);
//			headingStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
//			Font headingFont = workbook.createFont();
//			headingFont.setBoldweight((short) 1000);
//			headingStyle.setFont(headingFont);
//
//			monthly_time_sheet.setColumnWidth(0, 1000);
//			Cell srNoCell = headingRowDesc.createCell(0);
//			srNoCell.setCellValue(" Sr. ");
//			srNoCell.setCellStyle(headingStyle);
//
//			Cell nameOfClient = headingRowDesc.createCell(1);
//			nameOfClient.setCellValue("  Name of Client  ");
//
//			monthly_time_sheet.autoSizeColumn((short) 1);
//			nameOfClient.setCellStyle(headingStyle);
//			monthly_time_sheet.addMergedRegion(new CellRangeAddress(12, 12, 1, 2));
//
//			Cell assignment = headingRowDesc.createCell(3);
//			assignment.setCellValue("  Assignment  ");
//			monthly_time_sheet.autoSizeColumn((short) 3);
//			assignment.setCellStyle(headingStyle);
//
//			Cell totalDays = headingRowDesc.createCell(4);
//			totalDays.setCellValue("Total Days");
//			totalDays.setCellStyle(headingStyle);
//
//			Cell conveyExp = headingRowDesc.createCell(5);
//			conveyExp.setCellValue("Conv. Exp.");
//			conveyExp.setCellStyle(headingStyle);
//
//			for (int i = 6; i < 7 + coloumncountmain; i++) {
//				Cell cell = headingRowDesc.createCell(i);
//				HSSFCellStyle cellStyleHeadingMonth = workbook.createCellStyle();
//				Font font = workbook.createFont();
//				font.setBoldweight((short) 1000);
//				cellStyleHeadingMonth.setBorderTop(CellStyle.BORDER_THIN);
//				cellStyleHeadingMonth.setFont(font);
//				/*
//				 * if (i > 4) { cellStyleHeadingMonth
//				 * .setBorderBottom(CellStyle.BORDER_THIN); }
//				 */
//				if (i == 5) {
//					cellStyleHeadingMonth.setBorderLeft(CellStyle.BORDER_THIN);
//				}
//				if (i == 4 + (coloumncountmain / 2)) {
//					cell.setCellValue("Month: " + getDatefrom() + " to " + getDateto());
//					cell.setCellStyle(cellStyleHeadingMonth);
//				}
//				if (i == coloumncountmain + 6) {
//					cellStyleHeadingMonth.setBorderRight(CellStyle.BORDER_THIN);
//				}
//				cell.setCellStyle(cellStyleHeadingMonth);
//			}
//
//			Row headingRowNums = monthly_time_sheet.createRow(13);
//			headingRowNums.setHeight((short) 700);
//
//			for (int i = 0; i < 6; i++) {
//				HSSFCellStyle cellStyle = workbook.createCellStyle();
//				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
//				cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
//				if (i == 2) {
//					cellStyle.setBorderLeft(CellStyle.BORDER_NONE);
//				}
//				Cell cell = headingRowNums.createCell(i);
//				cell.setCellStyle(cellStyle);
//			}
//
//			Calendar cal = GregorianCalendar.getInstance();
//			cal.set(Calendar.DAY_OF_MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "dd")));
//			cal.set(Calendar.MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "MM")) - 1);
//			cal.set(Calendar.YEAR, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "yyyy")));
//
//			for (int i = 6, j = 1; i < 7 + coloumncountmain; i++, j++) {
//				Cell cell = headingRowNums.createCell(i);
//				HSSFCellStyle cellStyleHeadingMonth = workbook.createCellStyle();
//				cellStyleHeadingMonth.setBorderTop(CellStyle.BORDER_THIN);
//				cellStyleHeadingMonth.setBorderBottom(CellStyle.BORDER_THIN);
//				cellStyleHeadingMonth.setBorderLeft(CellStyle.BORDER_THIN);
//				cellStyleHeadingMonth.setBorderRight(CellStyle.BORDER_THIN);
//				cellStyleHeadingMonth.setAlignment(CellStyle.ALIGN_CENTER);
//				cellStyleHeadingMonth.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//				cell.setCellValue(cal.get(Calendar.DAY_OF_MONTH));
//				monthly_time_sheet.setColumnWidth(i, 1200);
//				cell.setCellStyle(cellStyleHeadingMonth);
//				cal.add(Calendar.DATE, 1);
//
//			}
//
//			// Map<String, String> mpprinttask = new HashMap<String, String>();
//			List<String> alclient_namelist = new ArrayList<String>();
//			Iterator iterator = projectdetails.keySet().iterator();
//			while (iterator.hasNext()) {
//				alclient_namelist.add((String) iterator.next());
//			}
//			int linecount = 14 + (projectdetails.keySet().size() * 2);
//			int procount = 0, taskcount = 0, dayscount = 0, dayrowcount = 0;
//			for (int i = 14, k = 1; i < linecount; i++, k++) {
//
//				cal = GregorianCalendar.getInstance();
//				cal.set(Calendar.DAY_OF_MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "dd")));
//				cal.set(Calendar.MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "MM")) - 1);
//				cal.set(Calendar.YEAR, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "yyyy")));
//
//				Row row = monthly_time_sheet.createRow(i);
//
//				for (int j = 0; j < 7 + coloumncountmain; j++) {
//					HSSFCellStyle cellStyle = workbook.createCellStyle();
//					cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
//					cellStyle.setBorderTop(CellStyle.BORDER_THIN);
//					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
//					cellStyle.setBorderRight(CellStyle.BORDER_THIN);
//
//					Cell cell = row.createCell(j);
//
//					if (i % 2 == 0 && j == 0) {
//						cell.setCellValue(k);
//						cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
//						k = k - 1;
//						dayrowcount++;
//					}
//					if (i % 2 != 0 && j == 0) {
//						cellStyle.setBorderTop(CellStyle.BORDER_NONE);
//					}
//					if (i % 2 == 0 && j == 1) {
//						cell.setCellValue(alclient_namelist.get(procount));
//						cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
//						procount++;
//					}
//					if (i % 2 != 0 && j == 1) {
//						cellStyle.setBorderTop(CellStyle.BORDER_NONE);
//					}
//					if (i % 2 == 0 && j == 2) {
//						cell.setCellValue("Onsite");
//						cell.setCellStyle(cellStyle);
//					}
//					if (i % 2 != 0 && j == 2) {
//						cell.setCellValue("Office");
//						cell.setCellStyle(cellStyle);
//					}
//					
//					
//					if (i % 2 == 0 && j == 3) {
//						Map<String, String> mpprinttask = (Map<String, String>) hmfinaltask.get(alclient_namelist.get(taskcount));
//						if (mpprinttask != null) {
//							cell.setCellValue(mpprinttask.get("ONS"));
//						}
//
//						cell.setCellStyle(cellStyle);
//
//					}
//					if (i % 2 != 0 && j == 3) {
//						Map<String, String> mpprinttask = (Map<String, String>) hmfinaltask.get(alclient_namelist.get(taskcount));
//						if (mpprinttask != null) {
//							cell.setCellValue(mpprinttask.get("OFS"));
//						}
//						cell.setCellStyle(cellStyle);
//						taskcount++;
//					}
//
//					if (i % 2 == 0 && j == 4) {
//						Map<String, Map<String, String>> hmactualhrs = (Map<String, Map<String, String>>) projectdetails.get(alclient_namelist.get(dayscount));
//						if (hmactualhrs.keySet().contains("ONS")) {
//							cell.setCellValue(hmactualhrs.get("ONS").keySet().size());
//						} else {
//							cell.setCellValue("");
//						}
//
//						cell.setCellStyle(cellStyle);
//
//					}
//					if (i % 2 != 0 && j == 4) {
//						Map<String, Map<String, String>> hmactualhrs = (Map<String, Map<String, String>>) projectdetails.get(alclient_namelist.get(dayscount));
//						if (hmactualhrs.keySet().contains("OFS")) {
//							cell.setCellValue(hmactualhrs.get("OFS").keySet().size());
//						} else {
//							cell.setCellValue("");
//						}
//						cell.setCellStyle(cellStyle);
//						dayscount++;
//
//					}
//					if (i % 2 == 0 && j == 5) {
//						cell.setCellValue("");
//						cell.setCellStyle(cellStyle);
//					}
//					if (i % 2 != 0 && j == 5) {
//						cell.setCellValue("");
//						cell.setCellStyle(cellStyle);
//					}
//					if (i % 2 == 0 && j == 5) {
//						cell.setCellValue("");
//						cell.setCellStyle(cellStyle);
//					}
//					if (i % 2 != 0 && j == 5) {
//						cell.setCellValue("");
//						cell.setCellStyle(cellStyle);
//					}
//
//					if (i % 2 == 0 && j > 5) {
//
//						Map<String, Map<String, String>> hmactualhrs = (Map<String, Map<String, String>>) projectdetails.get(alclient_namelist.get(dayrowcount - 1));
//						if (hmactualhrs.keySet().contains("ONS")) {
//
//							Map<String, String> mpdatesmap = hmactualhrs.get("ONS");
//							String datecomparison = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
//							cal.add(Calendar.DAY_OF_MONTH, 1);
//							if (mpdatesmap.keySet().contains(datecomparison)) {
//								cell.setCellValue(mpdatesmap.get(datecomparison));
//							} else {
//								cell.setCellValue("");
//							}
//
//						} else {
//							cell.setCellValue("");
//						}
//
//						cell.setCellStyle(cellStyle);
//
//					}
//					if (i % 2 != 0 && j > 5) {
//
//						Map<String, Map<String, String>> hmactualhrs = (Map<String, Map<String, String>>) projectdetails.get(alclient_namelist.get(dayrowcount - 1));
//						if (hmactualhrs.keySet().contains("OFS")) {
//
//							Map<String, String> mpdatesmap = hmactualhrs.get("OFS");
//							String datecomparison = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
//							cal.add(Calendar.DAY_OF_MONTH, 1);
//							if (mpdatesmap.keySet().contains(datecomparison)) {
//								cell.setCellValue(mpdatesmap.get(datecomparison));
//							} else {
//								cell.setCellValue("");
//							}
//
//						} else {
//							cell.setCellValue("");
//						}
//
//						cell.setCellStyle(cellStyle);
//					}
//
//					cell.setCellStyle(cellStyle);
//				}
//			}
//
//			Row totalHrsSpendWithClientsRow = monthly_time_sheet.createRow(linecount);
//			for (int i = 0; i < 7 + coloumncountmain; i++) {
//				HSSFCellStyle cellStyle = workbook.createCellStyle();
//				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
//				cellStyle.setBorderBottom(CellStyle.BORDER_DOUBLE);
//				if (i > 5) {
//					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
//					cellStyle.setBorderRight(CellStyle.BORDER_THIN);
//				}
//				Cell cell = totalHrsSpendWithClientsRow.createCell(i);
//				if (i == 0) {
//					Font font = workbook.createFont();
//					font.setBoldweight((short) 1000);
//					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
//					cell.setCellValue(" 1.  Total  Hours spent for client ");
//					cellStyle.setFont(font);
//					cell.setCellStyle(cellStyle);
//
//				}
//				cell.setCellStyle(cellStyle);
//			}
//			linecount++;
//			List<String> activities = getActivitiesList();
//			List<Date> dateList = getholidayslisttimesheet(strWlocationId);
//			for (int i = linecount, k = 1, m = 0; i < linecount + 6; i++, k++, m++) {
//				HSSFCellStyle cellStyle = workbook.createCellStyle();
//				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
//				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
//				cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
//				cellStyle.setBorderRight(CellStyle.BORDER_THIN);
//
//				Row row = monthly_time_sheet.createRow(i);
//				
//
//				cal = GregorianCalendar.getInstance();
//				cal.setTime(UF.getDateFormatUtil(getDatefrom(), DATE_FORMAT));
//
//				for (int j = 0; j < 7 + coloumncountmain; j++) {
//					Cell cell = row.createCell(j);
//					cell.setCellStyle(cellStyle);
//					if (j == 0) {
//						cell.setCellValue(k);
//					}
//					if (j == 1) {
//						cell.setCellValue(activities.get(m));
//					}
//					if (k == 5 && j == 4) {
//						cell.setCellValue(leavesdaylist.size());
//					}
//					if (k == 5 && j > 5) {
////						if (leavesdaylist.contains((j - 5))) {
//						if (leavesdaylist.contains(alDates.get(j-6))) {
//							cell.setCellValue("L" );
//
//						} else {
//							cell.setCellValue("");
//
//						}
//					}
//					if (k == 6 && j == 4) {
//						cell.setCellValue(dateList.size());
//					}
//					if (k == 6 && j > 5) {
//						if (dateList.contains(cal.getTime())) {
//							cell.setCellValue("H");
//						}
//
//						cal.add(Calendar.DATE, 1);
//					}
//
//				}
//			}
//
//			linecount = linecount + 6;
//			Row totalHrsSpendWithOtherActivityRow = monthly_time_sheet.createRow(linecount);
//			for (int i = 0; i < 7 + coloumncountmain; i++) {
//				HSSFCellStyle cellStyle = workbook.createCellStyle();
//				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
//				cellStyle.setBorderBottom(CellStyle.BORDER_DOUBLE);
//				if (i > 3) {
//					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
//					cellStyle.setBorderRight(CellStyle.BORDER_THIN);
//				}
//				Cell cell = totalHrsSpendWithOtherActivityRow.createCell(i);
//				if (i == 0) {
//					Font font = workbook.createFont();
//					font.setBoldweight((short) 1000);
//					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
//					cell.setCellValue(" 2.  Total Hours spent on other activities");
//					cellStyle.setFont(font);
//					cell.setCellStyle(cellStyle);
//				}
//				if (i == 4) {
//					cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
//					cell.setCellValue("0");
//					cell.setCellStyle(cellStyle);
//				}
//				cell.setCellStyle(cellStyle);
//			}
//			linecount++;
//			Row thirdBlankRow = monthly_time_sheet.createRow(linecount);
//			forBlankRowLeftAndRightBorder(thirdBlankRow);
//			linecount++;
//			Row grandTotRow = monthly_time_sheet.createRow(linecount);
//			for (int i = 0; i < 7 + coloumncountmain; i++) {
//				HSSFCellStyle cellStyle = workbook.createCellStyle();
//				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
//				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
//				if (i > 3) {
//					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
//					cellStyle.setBorderRight(CellStyle.BORDER_THIN);
//				}
//				Cell cell = grandTotRow.createCell(i);
//				if (i == 0) {
//					Font font = workbook.createFont();
//					font.setBoldweight((short) 1000);
//					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
//					cell.setCellValue(" 3.  Grand Total  ( 1 + 2)");
//					cellStyle.setFont(font);
//					cell.setCellStyle(cellStyle);
//				}
//				cell.setCellStyle(cellStyle);
//			}
//			linecount++;
//			Row extraWorkingDaysRow = monthly_time_sheet.createRow(linecount);
//			extraWorkingDaysRow.setHeight((short) 750);
//			for (int i = 0; i < 7 + coloumncountmain; i++) {
//				HSSFCellStyle cellStyle = workbook.createCellStyle();
//				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
//				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
//				cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//				if (i > 3) {
//					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
//					cellStyle.setBorderRight(CellStyle.BORDER_THIN);
//				}
//				Cell cell = extraWorkingDaysRow.createCell(i);
//				if (i == 0) {
//					Font font = workbook.createFont();
//					font.setBoldweight((short) 1000);
//					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
//					cell.setCellValue(" 4.    Extra Working days");
//					cellStyle.setFont(font);
//					cell.setCellStyle(cellStyle);
//				}
//				cell.setCellStyle(cellStyle);
//			}
//			linecount++;
//			Row fourthBlankRow = monthly_time_sheet.createRow(linecount);
//			forBlankRowLeftAndRightBorder(fourthBlankRow);
//			linecount++;
//			Row noteRow = monthly_time_sheet.createRow(linecount);
//			HSSFCellStyle leftBorderOnly = workbook.createCellStyle();
//			leftBorderOnly.setBorderLeft(CellStyle.BORDER_THIN);
//			Cell noteCell = noteRow.createCell(0);
//			noteCell.setCellValue("Note : Time report must be submitted on or before 26th Day of Every Month.");
//			noteCell.setCellStyle(leftBorderOnly);
//
//			Cell lastCellOfNote = noteRow.createCell(coloumncountmain + 6);
//			lastCellOfNote.setCellStyle(rightBorderForClientSummary);
//			linecount++;
//			Row fifthBlankRow = monthly_time_sheet.createRow(linecount);
//			forBlankRowLeftAndRightBorder(fifthBlankRow);
//			linecount++;
//			Row approvalRow = monthly_time_sheet.createRow(linecount);
//
//			Cell firstCellOfApprovalRow = approvalRow.createCell(0);
//			firstCellOfApprovalRow.setCellStyle(leftBorderOnly);
//
//			Cell preparedByCell = approvalRow.createCell(4);
//			preparedByCell.setCellValue("Prepared By : "+UF.showData((String)nameheaderlist.get(0), ""));
//
//			Cell checkedByCell = approvalRow.createCell(4 + coloumncountmain / 2);
//			checkedByCell.setCellValue("Checked By : "+UF.showData(hmApproveMap.get("APPROVED_BY"), ""));
//
//			Cell approvedByCell = approvalRow.createCell(coloumncountmain + 2);
//			approvedByCell.setCellValue("Approved by : "+UF.showData(hmApproveMap.get("APPROVED_BY"), ""));
//
//			Cell lastCellOfApproval = approvalRow.createCell(coloumncountmain + 6);
//			lastCellOfApproval.setCellStyle(rightBorderForClientSummary);
//			linecount++;
//			Row lastRow = monthly_time_sheet.createRow(linecount);
//			for (int i = 0; i < 7 + coloumncountmain; i++) {
//				HSSFCellStyle cellStyleForLastRow = workbook.createCellStyle();
//				cellStyleForLastRow.setBorderBottom(CellStyle.BORDER_THIN);
//				if (i == 0) {
//					cellStyleForLastRow.setBorderLeft(CellStyle.BORDER_THIN);
//				}
//				if (i == coloumncountmain + 6) {
//					cellStyleForLastRow.setBorderRight(CellStyle.BORDER_THIN);
//				}
//				Cell cell = lastRow.createCell(i);
//				cell.setCellStyle(cellStyleForLastRow);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	
	
	public void writeTimeSheetReport(List<String> alTaskIds, Map<String, List<String>> hmTaskAndSubTaskIds, Map<String, List<String>> hmTaskAndSubTaskData, List<String> nameheaderlist, List<Integer> leavesdaylist,
			List<Integer> holidayslist, Map<String, Map<String, String>> hmfinaltask, List<String> idList) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		int lessspace = 0;
		if (coloumncountmain < 10)
			lessspace = 1;

		try {
			con=db.makeConnection(con);	
			
			Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			if (hmEmpWlocation == null)
				hmEmpWlocation = new HashMap();
			String strWlocationId = hmEmpWlocation.get(getEmpid());

			
			Calendar calD = Calendar.getInstance();
			Map<String, Map<String, String>> hmTaskDataDatewise = (Map<String, Map<String, String>>) request.getAttribute("hmTaskDataDatewise");
			Map<String, String> hmDatewiseTotalHours = (Map<String, String>) request.getAttribute("hmDatewiseTotalHours");
			
			Map<String, Set<String>> hmWeekendMap = (Map<String, Set<String>>)request.getAttribute("hmWeekendMap");
			if(hmWeekendMap == null) hmWeekendMap = new HashMap<String, Set<String>>();
			
			Map<String, Set<String>> hmRosterWeekEndDates = (Map<String, Set<String>>)request.getAttribute("hmRosterWeekEndDates");
			if(hmRosterWeekEndDates == null) hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			
			List<String> alEmpCheckRosterWeektype = (List<String>)request.getAttribute("alEmpCheckRosterWeektype");
			if(alEmpCheckRosterWeektype == null) alEmpCheckRosterWeektype = new ArrayList<String>();
			
			Map hmHolidayDates = (Map)request.getAttribute("hmHolidayDates");
			if(hmHolidayDates == null) hmHolidayDates = new HashMap();
			
			Set<String> weeklyOffSet= hmWeekendMap.get(strWlocationId); //wid
			if(weeklyOffSet==null) weeklyOffSet=new HashSet<String>();
			
			Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(getEmpid());
			if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
			
//			System.out.println("hmTaskDataDatewise ===>> " + hmTaskDataDatewise);
			calD.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "dd")));
			calD.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "MM"))-1);
			calD.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "yyyy")));
			
			String strDate = uF.getDateFormat(calD.get(Calendar.DATE)+"/"+ (calD.get(Calendar.MONTH)+1)+"/"+calD.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
			List alDates = new ArrayList();
			
			for(int i=0; i<31; i++){
				strDate = uF.getDateFormat(calD.get(Calendar.DATE)+"/"+ (calD.get(Calendar.MONTH)+1)+"/"+calD.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
				
				alDates.add(calD.get(Calendar.DATE));
				calD.add(Calendar.DATE, 1);
			}


			
			Row firmNameRow = monthly_time_sheet.createRow(5);
			firmNameRow.setHeight((short) 450);
			monthly_time_sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
			for (int i = 0; i < 8 + coloumncountmain; i++) {
				HSSFCellStyle styleForFirmName = workbook.createCellStyle();
				Font firmNameFont = workbook.createFont();
				firmNameFont.setFontHeight((short) 320);
				firmNameFont.setBoldweight((short) 1000);
				styleForFirmName.setFont(firmNameFont);
				styleForFirmName.setBorderTop(CellStyle.BORDER_THIN);
				Cell firmNameCell = firmNameRow.createCell(i);
				if (i == 0) {
					styleForFirmName.setBorderLeft(CellStyle.BORDER_THIN);
					firmNameCell.setCellValue(" " + CF.getStrOrgName());
				}
				if (i == 7 + coloumncountmain) {
					styleForFirmName.setBorderRight(CellStyle.BORDER_THIN);
				}
				firmNameCell.setCellStyle(styleForFirmName);
			}

			Row firmTypeRow = monthly_time_sheet.createRow(6);
			monthly_time_sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));
			for (int i = 0; i < 8 + coloumncountmain; i++) {
				HSSFCellStyle styleForFirmType = workbook.createCellStyle();
				Font firmTypeFont = workbook.createFont();
				firmTypeFont.setFontHeight((short) 225);
				firmTypeFont.setBoldweight((short) 1000);
				styleForFirmType.setFont(firmTypeFont);
				styleForFirmType.setBorderBottom(CellStyle.BORDER_THIN);
				Cell firmTypeCell = firmTypeRow.createCell(i);
				if (i == 0) {
					styleForFirmType.setBorderLeft(CellStyle.BORDER_THIN);
					firmTypeCell.setCellValue(" " + CF.getStrOrgSubTitle());
				}
				if (i == 7 + coloumncountmain) {
					styleForFirmType.setBorderRight(CellStyle.BORDER_THIN);
				}
				firmTypeCell.setCellStyle(styleForFirmType);
			}

			Row firstBlankRow = monthly_time_sheet.createRow(7);
			forBlankRowLeftAndRightBorder(firstBlankRow);

			Row clientNameRow = monthly_time_sheet.createRow(8);
			HSSFCellStyle rightBorderForClientSummary = workbook.createCellStyle();
			rightBorderForClientSummary.setBorderRight(CellStyle.BORDER_THIN);
			HSSFCellStyle clientSummary = workbook.createCellStyle();

			Font clientNameCellFont = workbook.createFont();
			clientNameCellFont.setFontHeight((short) 230);

			Cell clientNameCell = clientNameRow.createCell(0);
			clientNameCell.setCellValue(" Name : " + nameheaderlist.get(0));
			clientSummary.setFont(clientNameCellFont);
			clientSummary.setBorderLeft(CellStyle.BORDER_THIN);
			clientNameCell.setCellStyle(clientSummary);

			clientSummary.setFont(clientNameCellFont);
			clientSummary.setBorderLeft(CellStyle.BORDER_THIN);

			Cell clientBankACNoCell = clientNameRow.createCell(coloumncountmain);
			clientBankACNoCell.setCellValue(" Bank A/C No.: " + uF.showData(nameheaderlist.get(2), ""));

			Cell lastCellOfClientSummary1 = clientNameRow.createCell(coloumncountmain + 7);
			lastCellOfClientSummary1.setCellStyle(rightBorderForClientSummary);

			Row divisionRow = monthly_time_sheet.createRow(9);
			divisionRow.setHeight((short) 300);
			Cell clientDivisionCell = divisionRow.createCell(0);
			clientDivisionCell.setCellValue(" Department : " + nameheaderlist.get(4));
			clientDivisionCell.setCellStyle(clientSummary);

			HSSFCellStyle cellStyleforReportName = workbook.createCellStyle();
			Font reportNameFont = workbook.createFont();
			reportNameFont.setFontHeight((short) 240);
			reportNameFont.setBoldweight((short) 600);
			cellStyleforReportName.setFont(reportNameFont);

			Cell reportNameCell;
			if (lessspace == 1) {
				reportNameCell = divisionRow.createCell(4);
			} else {
				reportNameCell = divisionRow.createCell(7 + (coloumncountmain / 2));
			}
			reportNameCell.setCellValue("MONTHLY TIME REPORT");
			reportNameCell.setCellStyle(cellStyleforReportName);

			Cell clientPANNoCell = divisionRow.createCell(coloumncountmain);
			clientPANNoCell.setCellValue(" PAN : " + nameheaderlist.get(3));

			Cell lastCellOfClientSummary2 = divisionRow.createCell(coloumncountmain + 7);
			lastCellOfClientSummary2.setCellStyle(rightBorderForClientSummary);

			Row empCodeRow = monthly_time_sheet.createRow(10);
			Cell clientEmpIdCell = empCodeRow.createCell(0);
			clientEmpIdCell.setCellValue(" Employee Code : " + nameheaderlist.get(1));
			clientEmpIdCell.setCellStyle(clientSummary);

			Cell clientWorkingHrsCell = empCodeRow.createCell(coloumncountmain);
			clientWorkingHrsCell.setCellValue(" ( Standard Hours per Day  :  8 ) : ");

			Cell lastCellOfClientSummary3 = empCodeRow.createCell(coloumncountmain + 7);
			lastCellOfClientSummary3.setCellStyle(rightBorderForClientSummary);

			Row secondBlankRow = monthly_time_sheet.createRow(11);
			forBlankRowLeftAndRightBorder(secondBlankRow);

			Row headingRowDesc = monthly_time_sheet.createRow(12);
			headingRowDesc.setHeight((short) 500);
			HSSFCellStyle headingStyle = workbook.createCellStyle();
			headingStyle.setBorderLeft(CellStyle.BORDER_THIN);
			headingStyle.setBorderRight(CellStyle.BORDER_THIN);
			headingStyle.setBorderTop(CellStyle.BORDER_THIN);
			headingStyle.setAlignment(CellStyle.ALIGN_CENTER);
			headingStyle.setWrapText(true);
			headingStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
			Font headingFont = workbook.createFont();
			headingFont.setBoldweight((short) 1000);
			headingStyle.setFont(headingFont);

			monthly_time_sheet.setColumnWidth(0, 1000);
			Cell srNoCell = headingRowDesc.createCell(0);
			srNoCell.setCellValue(" Sr. ");
			srNoCell.setCellStyle(headingStyle);

			Cell nameOfClient = headingRowDesc.createCell(1);
			nameOfClient.setCellValue("  Name of Client  ");
			monthly_time_sheet.autoSizeColumn((short) 1);
			nameOfClient.setCellStyle(headingStyle);
//			monthly_time_sheet.addMergedRegion(new CellRangeAddress(12, 12, 1, 2));

			Cell nameOfProject = headingRowDesc.createCell(2);
			nameOfProject.setCellValue("  Name of Project  ");
			monthly_time_sheet.autoSizeColumn((short) 2);
			nameOfProject.setCellStyle(headingStyle);
			
			Cell assignment = headingRowDesc.createCell(3);
			assignment.setCellValue("  Assignment  ");
			monthly_time_sheet.autoSizeColumn((short) 3);
			assignment.setCellStyle(headingStyle);
			
			
			Cell onOffSite = headingRowDesc.createCell(4);
			onOffSite.setCellValue("  On/Off Site  ");
			monthly_time_sheet.autoSizeColumn((short) 4);
			onOffSite.setCellStyle(headingStyle);

			Cell totalDays = headingRowDesc.createCell(5);
			totalDays.setCellValue("Total Days");
			totalDays.setCellStyle(headingStyle);

			Cell conveyExp = headingRowDesc.createCell(6);
			conveyExp.setCellValue("Conv. Exp.");
			conveyExp.setCellStyle(headingStyle);

			for (int i = 7; i < 8 + coloumncountmain; i++) {
				Cell cell = headingRowDesc.createCell(i);
				HSSFCellStyle cellStyleHeadingMonth = workbook.createCellStyle();
				Font font = workbook.createFont();
				font.setBoldweight((short) 1000);
				cellStyleHeadingMonth.setBorderTop(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setFont(font);
				/*
				 * if (i > 4) { cellStyleHeadingMonth
				 * .setBorderBottom(CellStyle.BORDER_THIN); }
				 */
				if (i == 5) {
					cellStyleHeadingMonth.setBorderLeft(CellStyle.BORDER_THIN);
				}
				if (i == 4 + (coloumncountmain / 2)) {
					cell.setCellValue("Month: " + getDatefrom() + " to " + getDateto());
					cell.setCellStyle(cellStyleHeadingMonth);
				}
				if (i == coloumncountmain + 7) {
					cellStyleHeadingMonth.setBorderRight(CellStyle.BORDER_THIN);
				}
				cell.setCellStyle(cellStyleHeadingMonth);
			}

			Row headingRowNums = monthly_time_sheet.createRow(13);
			headingRowNums.setHeight((short) 700);

			for (int i = 0; i < 6; i++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
				cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
				if (i == 2) {
					cellStyle.setBorderLeft(CellStyle.BORDER_NONE);
				}
				Cell cell = headingRowNums.createCell(i);
				cell.setCellStyle(cellStyle);
			}

			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "yyyy")));

			for (int i = 7, j = 1; i < 8 + coloumncountmain; i++, j++) {
				Cell cell = headingRowNums.createCell(i);
				HSSFCellStyle cellStyleHeadingMonth = workbook.createCellStyle();
				cellStyleHeadingMonth.setBorderTop(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setBorderBottom(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setBorderLeft(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setBorderRight(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setAlignment(CellStyle.ALIGN_CENTER);
				cellStyleHeadingMonth.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				cell.setCellValue(cal.get(Calendar.DAY_OF_MONTH));
				monthly_time_sheet.setColumnWidth(i, 1200);
				cell.setCellStyle(cellStyleHeadingMonth);
				cal.add(Calendar.DATE, 1);

			}

//			Map<String, String> mpprinttask = new HashMap<String, String>();
//			List<String> alclient_namelist = new ArrayList<String>();
//			Iterator iterator = projectdetails.keySet().iterator();
//			while (iterator.hasNext()) {
//				alclient_namelist.add((String) iterator.next());
//			}
			int linecount = 14 + ((alTaskIds != null ? alTaskIds.size() : 0) * 2);
			int taskIndex = 0 ; //, taskcount = 0, dayscount = 0, dayrowcount = 0;
			for (int i = 14, k = 1; alTaskIds!=null && alTaskIds.size()>0 && i < linecount; i++, k++) {
				String taskId = alTaskIds.get(taskIndex);
				List<String> alTaskData = hmTaskAndSubTaskData.get(taskId);
				if(alTaskData == null) alTaskData = new ArrayList<String>();
				Map<String, String> hmInnerDataONS = hmTaskDataDatewise.get(taskId+"_ONS");
				Map<String, String> hmInnerDataOFS = hmTaskDataDatewise.get(taskId+"_OFS");
//				System.out.println("hmInnerData ===>> " + hmInnerData);
				cal = GregorianCalendar.getInstance();
				cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "MM")) - 1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "yyyy")));

				Row row = monthly_time_sheet.createRow(i);

				for (int j = 0; j < 8 + coloumncountmain; j++) {
					HSSFCellStyle cellStyle = workbook.createCellStyle();
					cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
					cellStyle.setBorderTop(CellStyle.BORDER_THIN);
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cellStyle.setBorderRight(CellStyle.BORDER_THIN);

					Cell cell = row.createCell(j);

					if (i % 2 == 0 && j == 0) {
						cell.setCellValue(k);
						cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
						k = k - 1;
//						dayrowcount++;
						
					}
					if (i % 2 != 0 && j == 0) {
						cellStyle.setBorderTop(CellStyle.BORDER_NONE);
						taskIndex++;
					}
					if (i % 2 == 0 && j == 1) {
						cell.setCellValue(alTaskData.size()>0 ? alTaskData.get(2) : "");
						cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
					}
					if (i % 2 != 0 && j == 1) {
						cellStyle.setBorderTop(CellStyle.BORDER_NONE);
					}
					if (i % 2 == 0 && j == 2) {
						cell.setCellValue(alTaskData.size()>0 ? alTaskData.get(1) : "");
						cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
					}
					if (i % 2 != 0 && j == 2) {
						cellStyle.setBorderTop(CellStyle.BORDER_NONE);
					}
					if (i % 2 == 0 && j == 3) {
						cell.setCellValue(alTaskData.size()>0 ? alTaskData.get(3) : taskId);
						cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
					}
					if (i % 2 != 0 && j == 3) {
						cellStyle.setBorderTop(CellStyle.BORDER_NONE);
					}
					if (i % 2 == 0 && j == 4) {
						cell.setCellValue("Onsite");
						cell.setCellStyle(cellStyle);
					}
					if (i % 2 != 0 && j == 4) {
						cell.setCellValue("Office");
						cell.setCellStyle(cellStyle);
					}
					
					if (i % 2 == 0 && j == 5) {
						Font font = workbook.createFont();
						font.setBoldweight((short) 1000);
						cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
						cell.setCellValue((hmTaskDataDatewise != null && hmTaskDataDatewise.get(taskId+"_ONS") != null) ? hmTaskDataDatewise.get(taskId+"_ONS").size()+"": "");
						cellStyle.setFont(font);
					}
					if (i % 2 != 0 && j == 5) {
						Font font = workbook.createFont();
						font.setBoldweight((short) 1000);
						cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
						cell.setCellValue((hmTaskDataDatewise != null && hmTaskDataDatewise.get(taskId+"_OFS") != null) ? hmTaskDataDatewise.get(taskId+"_OFS").size()+"" : "");
						cellStyle.setFont(font);
//						dayscount++;
					}
					
					if (i % 2 == 0 && j == 6) {
						cell.setCellValue("");
						cell.setCellStyle(cellStyle);
					}
					if (i % 2 != 0 && j == 6) {
						cell.setCellValue("");
						cell.setCellStyle(cellStyle);
					}
					

					if (i % 2 == 0 && j > 6) {
						String datecomparison = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
						cal.add(Calendar.DAY_OF_MONTH, 1);
						if (hmInnerDataONS != null && hmInnerDataONS.keySet().contains(datecomparison)) {
							cell.setCellValue(uF.getTotalTimeMinutes100To60(hmInnerDataONS.get(datecomparison))); //ONS
						} else {
							cell.setCellValue("");
						}
						cell.setCellStyle(cellStyle);
					}
					if (i % 2 != 0 && j > 6) {
						String datecomparison = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
						cal.add(Calendar.DAY_OF_MONTH, 1);
						if (hmInnerDataOFS != null && hmInnerDataOFS.keySet().contains(datecomparison)) {
							cell.setCellValue(uF.getTotalTimeMinutes100To60(hmInnerDataOFS.get(datecomparison))); //OFS
						} else {
							cell.setCellValue("");
						}
						cell.setCellStyle(cellStyle);
					}
					cell.setCellStyle(cellStyle);
				}
			}
			
			cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "yyyy")));

			Row totalHrsSpendWithClientsRow = monthly_time_sheet.createRow(linecount);
			for (int i = 0; i < 8 + coloumncountmain; i++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
				cellStyle.setBorderBottom(CellStyle.BORDER_DOUBLE);
				if (i > 5) {
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cellStyle.setBorderRight(CellStyle.BORDER_THIN);
				}
				Cell cell = totalHrsSpendWithClientsRow.createCell(i);
				if (i == 0) {
					Font font = workbook.createFont();
					font.setBoldweight((short) 1000);
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cell.setCellValue(" 1.  Total  Hours spent for client ");
					cellStyle.setFont(font);
					cell.setCellStyle(cellStyle);
				} else {
					if (i > 6) {
						String datecomparison = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
						cal.add(Calendar.DAY_OF_MONTH, 1);
						if (hmDatewiseTotalHours != null && hmDatewiseTotalHours.keySet().contains(datecomparison)) {
							Font font = workbook.createFont();
							font.setBoldweight((short) 1000);
							cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
							cell.setCellValue(uF.getTotalTimeMinutes100To60(hmDatewiseTotalHours.get(datecomparison)));
							cellStyle.setFont(font);
							
						} else {
							cell.setCellValue("");
						}
						cell.setCellStyle(cellStyle);
					}
				}
				cell.setCellStyle(cellStyle);
			}
			linecount++;
			
			cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "yyyy")));
			List<String> activities = getActivitiesList();
			List<Date> dateList = getholidayslisttimesheet(strWlocationId);
			for (int i = linecount, k = 1, m = 0; i < linecount + 6; i++, k++, m++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
				cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
				cellStyle.setBorderRight(CellStyle.BORDER_THIN);

				Row row = monthly_time_sheet.createRow(i);
				

//				cal = GregorianCalendar.getInstance();
//				cal.setTime(uF.getDateFormatUtil(getDatefrom(), DATE_FORMAT));

				for (int j = 0; j < 8 + coloumncountmain; j++) {
					Cell cell = row.createCell(j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						cell.setCellValue(k);
					}
					if (j == 1) {
						cell.setCellValue(activities.get(m));
					}
					if (k == 5 && j == 4) {
						cell.setCellValue(leavesdaylist.size());
					}
					if (k == 5 && j > 6) {
//						if (leavesdaylist.contains((j - 5))) {
						
						if (leavesdaylist.contains(alDates.get(j-7))) {
							cell.setCellValue("L" );

						} else {
							cell.setCellValue("");

						}
					}
					if (k == 6 && j == 4) {
						cell.setCellValue(dateList.size());
					}
					if (k == 6 && j > 6) {
						String datecomparison = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
						cal.add(Calendar.DAY_OF_MONTH, 1);
//						System.out.println("weeklyOffSet ===>> " + weeklyOffSet + " -- datecomparison ===>> " + datecomparison);
						if(alEmpCheckRosterWeektype.contains(getEmpid())) {
//							System.out.println("rosterWeeklyOffSet ===>> " + rosterWeeklyOffSet + " -- datecomparison ===>> " + datecomparison);
							if(rosterWeeklyOffSet.contains(datecomparison)) {
								cell.setCellValue("W/O");
							}
						} else if(weeklyOffSet.contains(datecomparison)) {
							cell.setCellValue("W/O");
						} else if(hmHolidayDates.toString().contains(uF.getDateFormat((String) datecomparison, DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWlocationId)) {
							cell.setCellValue("H");
						}
						
						/*if (dateList.contains(cal.getTime())) {
							cell.setCellValue("H");
						}*/

//						cal.add(Calendar.DATE, 1);
					}

				}
			}

			linecount = linecount + 6;
			Row totalHrsSpendWithOtherActivityRow = monthly_time_sheet.createRow(linecount);
			for (int i = 0; i < 8 + coloumncountmain; i++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
				cellStyle.setBorderBottom(CellStyle.BORDER_DOUBLE);
				if (i > 3) {
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cellStyle.setBorderRight(CellStyle.BORDER_THIN);
				}
				Cell cell = totalHrsSpendWithOtherActivityRow.createCell(i);
				if (i == 0) {
					Font font = workbook.createFont();
					font.setBoldweight((short) 1000);
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cell.setCellValue(" 2.  Total Hours spent on other activities");
					cellStyle.setFont(font);
					cell.setCellStyle(cellStyle);
				}
				if (i == 4) {
					cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
					cell.setCellValue("0");
					cell.setCellStyle(cellStyle);
				}
				cell.setCellStyle(cellStyle);
			}
			linecount++;
			Row thirdBlankRow = monthly_time_sheet.createRow(linecount);
			forBlankRowLeftAndRightBorder(thirdBlankRow);
			linecount++;
			
			cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getDatefrom(), DATE_FORMAT, "yyyy")));
			Row grandTotRow = monthly_time_sheet.createRow(linecount);
			for (int i = 0; i < 8 + coloumncountmain; i++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
				if (i > 3) {
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cellStyle.setBorderRight(CellStyle.BORDER_THIN);
				}
				Cell cell = grandTotRow.createCell(i);
				if (i == 0) {
					Font font = workbook.createFont();
					font.setBoldweight((short) 1000);
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cell.setCellValue(" 3.  Grand Total  ( 1 + 2)");
					cellStyle.setFont(font);
					cell.setCellStyle(cellStyle);
				} else {
					if (i > 6) {
						String datecomparison = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
						cal.add(Calendar.DAY_OF_MONTH, 1);
						if (hmDatewiseTotalHours != null && hmDatewiseTotalHours.keySet().contains(datecomparison)) {
							Font font = workbook.createFont();
							font.setBoldweight((short) 1000);
							cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
							cell.setCellValue(uF.getTotalTimeMinutes100To60(hmDatewiseTotalHours.get(datecomparison)));
							cellStyle.setFont(font);
							
						} else {
							cell.setCellValue("");
						}
						cell.setCellStyle(cellStyle);
					}
				}
				cell.setCellStyle(cellStyle);
			
			}
			
			linecount++;
			Row extraWorkingDaysRow = monthly_time_sheet.createRow(linecount);
			extraWorkingDaysRow.setHeight((short) 750);
			for (int i = 0; i < 8 + coloumncountmain; i++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
				cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				if (i > 3) {
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cellStyle.setBorderRight(CellStyle.BORDER_THIN);
				}
				Cell cell = extraWorkingDaysRow.createCell(i);
				if (i == 0) {
					Font font = workbook.createFont();
					font.setBoldweight((short) 1000);
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cell.setCellValue(" 4.    Extra Working days");
					cellStyle.setFont(font);
					cell.setCellStyle(cellStyle);
				}
				cell.setCellStyle(cellStyle);
			}
			linecount++;
			Row fourthBlankRow = monthly_time_sheet.createRow(linecount);
			forBlankRowLeftAndRightBorder(fourthBlankRow);
			linecount++;
			Row noteRow = monthly_time_sheet.createRow(linecount);
			HSSFCellStyle leftBorderOnly = workbook.createCellStyle();
			leftBorderOnly.setBorderLeft(CellStyle.BORDER_THIN);
			Cell noteCell = noteRow.createCell(0);
			noteCell.setCellValue("Note : Time report must be submitted on or before 26th Day of Every Month.");
			noteCell.setCellStyle(leftBorderOnly);

			Cell lastCellOfNote = noteRow.createCell(coloumncountmain + 7);
			lastCellOfNote.setCellStyle(rightBorderForClientSummary);
			linecount++;
			Row fifthBlankRow = monthly_time_sheet.createRow(linecount);
			forBlankRowLeftAndRightBorder(fifthBlankRow);
			linecount++;
			Row approvalRow = monthly_time_sheet.createRow(linecount);

			Cell firstCellOfApprovalRow = approvalRow.createCell(0);
			firstCellOfApprovalRow.setCellStyle(leftBorderOnly);

			Cell preparedByCell = approvalRow.createCell(4);
			preparedByCell.setCellValue("Prepared By : "+uF.showData((String)nameheaderlist.get(0), ""));

			Cell checkedByCell = approvalRow.createCell(4 + coloumncountmain / 2);
			checkedByCell.setCellValue("Checked By : "+uF.showData(hmApproveMap.get("APPROVED_BY"), ""));

			Cell approvedByCell = approvalRow.createCell(coloumncountmain + 2);
			approvedByCell.setCellValue("Approved by : "+uF.showData(hmApproveMap.get("APPROVED_BY"), ""));

			Cell lastCellOfApproval = approvalRow.createCell(coloumncountmain + 7);
			lastCellOfApproval.setCellStyle(rightBorderForClientSummary);
			linecount++;
			Row lastRow = monthly_time_sheet.createRow(linecount);
			for (int i = 0; i < 8 + coloumncountmain; i++) {
				HSSFCellStyle cellStyleForLastRow = workbook.createCellStyle();
				cellStyleForLastRow.setBorderBottom(CellStyle.BORDER_THIN);
				if (i == 0) {
					cellStyleForLastRow.setBorderLeft(CellStyle.BORDER_THIN);
				}
				if (i == coloumncountmain + 7) {
					cellStyleForLastRow.setBorderRight(CellStyle.BORDER_THIN);
				}
				Cell cell = lastRow.createCell(i);
				cell.setCellStyle(cellStyleForLastRow);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void writeConveyanceSheetReport(List<String> nameheaderlist, Map<String, List<String>> mapconveyancetravel, Map<String, List<String>> mapconveyanceothers) {
		Iterator iter = mapconveyanceothers.entrySet().iterator();
		Iterator iterator = mapconveyancetravel.entrySet().iterator();
		List<String> listconven = new ArrayList<String>();
		List<String> listothers = new ArrayList<String>();
		UtilityFunctions UF = new UtilityFunctions();
		while (iterator.hasNext()) {
			Entry entry = (Entry) iterator.next();
			listconven.add((String) entry.getKey());
		}

		while (iter.hasNext()) {
			Map.Entry pairs = (Map.Entry) iter.next();
			listothers.add((String) pairs.getKey());
		}
		try {
			conveyance_sheet.setColumnWidth(0, 1500);
			conveyance_sheet.setColumnWidth(1, 7500);
			conveyance_sheet.setColumnWidth(2, 4000);
			conveyance_sheet.setColumnWidth(3, 3000);
			conveyance_sheet.setColumnWidth(4, 3000);
			conveyance_sheet.setColumnWidth(5, 3000);
			conveyance_sheet.setColumnWidth(6, 3000);
			conveyance_sheet.setColumnWidth(7, 2000);
			conveyance_sheet.setColumnWidth(8, 3000);
			conveyance_sheet.setColumnWidth(9, 3000);
			conveyance_sheet.setColumnWidth(10, 3000);

			Row firmNameRow = conveyance_sheet.createRow(0);
			firmNameRow.setHeight((short) 450);
			conveyance_sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
			for (int i = 0; i < 11; i++) {
				HSSFCellStyle styleForFirmName = workbook.createCellStyle();
				Font firmNameFont = workbook.createFont();
				firmNameFont.setFontHeight((short) 320);
				firmNameFont.setBoldweight((short) 1000);
				styleForFirmName.setFont(firmNameFont);
				styleForFirmName.setBorderTop(CellStyle.BORDER_THIN);
				Cell firmNameCell = firmNameRow.createCell(i);
				if (i == 0) {
					styleForFirmName.setBorderLeft(CellStyle.BORDER_THIN);
					firmNameCell.setCellValue(" " + CF.getStrOrgName());
				}
				if (i == 10) {
					styleForFirmName.setBorderRight(CellStyle.BORDER_THIN);
				}
				firmNameCell.setCellStyle(styleForFirmName);
			}

			Row firmTypeRow = conveyance_sheet.createRow(1);
			conveyance_sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));
			for (int i = 0; i < 11; i++) {
				HSSFCellStyle styleForFirmType = workbook.createCellStyle();
				Font firmTypeFont = workbook.createFont();
				firmTypeFont.setFontHeight((short) 225);
				firmTypeFont.setBoldweight((short) 1000);
				styleForFirmType.setFont(firmTypeFont);
				styleForFirmType.setBorderBottom(CellStyle.BORDER_THIN);
				Cell firmTypeCell = firmTypeRow.createCell(i);
				if (i == 0) {
					styleForFirmType.setBorderLeft(CellStyle.BORDER_THIN);
					firmTypeCell.setCellValue(" " + CF.getStrOrgSubTitle());
				}
				if (i == 10) {
					styleForFirmType.setBorderRight(CellStyle.BORDER_THIN);
				}
				firmTypeCell.setCellStyle(styleForFirmType);
			}

			Row reportNameRow = conveyance_sheet.createRow(3);
			applyLeftAndRightBorderTable(reportNameRow);
			reportNameRow.setHeight((short) 300);

			HSSFCellStyle cellStyleforReportName = workbook.createCellStyle();
			Font reportNameFont = workbook.createFont();
			reportNameFont.setFontHeight((short) 280);
			reportNameFont.setBoldweight((short) 1000);
			cellStyleforReportName.setFont(reportNameFont);

			Cell reportNameCell = reportNameRow.createCell(3);
			reportNameCell.setCellValue("MONTHLY TIME REPORT (Expense Statement)");
			reportNameCell.setCellStyle(cellStyleforReportName);

			Row blankRow = conveyance_sheet.createRow(2);
			applyLeftAndRightBorderTable(blankRow);

			Row clientNameRow = conveyance_sheet.createRow(4);
			applyLeftAndRightBorderTable(clientNameRow);
			HSSFCellStyle clientSummary = workbook.createCellStyle();
			Font clientNameCellFont = workbook.createFont();
			clientNameCellFont.setFontHeight((short) 230);

			Cell clientNameCell = clientNameRow.createCell(0);
			clientNameCell.setCellValue(" Name : " + nameheaderlist.get(0));
			clientSummary.setFont(clientNameCellFont);
			clientSummary.setBorderLeft(CellStyle.BORDER_THIN);
			clientNameCell.setCellStyle(clientSummary);
			Cell currentDate = clientNameRow.createCell(8);
			currentDate.setCellValue(" Date : " + UF.getCurrentDate(CF.getStrTimeZone()));
			// currentDate.setCellValue(" Date : " + getDateto());

			Row divisionRow = conveyance_sheet.createRow(5);
			applyLeftAndRightBorderTable(divisionRow);
			Cell clientDivisionCell = divisionRow.createCell(0);
			clientDivisionCell.setCellValue(" Department : " + nameheaderlist.get(4));
			clientDivisionCell.setCellStyle(clientSummary);

			Row empCodeRow = conveyance_sheet.createRow(6);
			applyLeftAndRightBorderTable(empCodeRow);
			Cell clientEmpIdCell = empCodeRow.createCell(0);
			clientEmpIdCell.setCellValue(" Employee Code : " + nameheaderlist.get(1));
			clientEmpIdCell.setCellStyle(clientSummary);

			Row partisionRow = conveyance_sheet.createRow(7);
			forBlankRowsWithBorder(partisionRow);

			Row blankRow3 = conveyance_sheet.createRow(8);
			applyLeftAndRightBorderTable(blankRow3);

			Row statementNameRow = conveyance_sheet.createRow(9);
			applyLeftAndRightBorderTable(statementNameRow);

			HSSFCellStyle headingNameEffect = workbook.createCellStyle();
			Font headingName = workbook.createFont();
			headingName.setBoldweight((short) 1000);
			headingName.setFontHeight((short) 220);
			headingNameEffect.setFont(headingName);
			headingNameEffect.setBorderLeft(CellStyle.BORDER_THIN);
			Cell statementNameCell = statementNameRow.createCell(0);
			statementNameCell.setCellValue(" Conveyance and Other Expenses Statement for the month ending on " + getDateto());
			statementNameCell.setCellStyle(headingNameEffect);

			Row blankRow4 = conveyance_sheet.createRow(10);
			for (int i = 0; i < 11; i++) {
				HSSFCellStyle styleForPlaceHead = workbook.createCellStyle();
				if (i == 0) {
					styleForPlaceHead.setBorderLeft(CellStyle.BORDER_THIN);
				} else if (i == 6) {
					styleForPlaceHead.setBorderBottom(CellStyle.BORDER_THIN);
				} else if (i == 10) {
					styleForPlaceHead.setBorderRight(CellStyle.BORDER_THIN);
				} else {
					continue;
				}
				Cell cell = blankRow4.createCell(i);
				cell.setCellStyle(styleForPlaceHead);
			}

			Row statementHeadingRow = conveyance_sheet.createRow(11);
			statementHeadingRow.setHeight((short) 750);
			conveyance_sheet.addMergedRegion(new CellRangeAddress(11, 11, 5, 6));

			HSSFCellStyle headingEffect = workbook.createCellStyle();
			Font headings = workbook.createFont();
			headings.setBoldweight((short) 1000);
			headings.setFontHeight((short) 200);
			headingEffect.setFont(headings);
			headingEffect.setBorderTop(CellStyle.BORDER_THIN);
			headingEffect.setBorderLeft(CellStyle.BORDER_THIN);
			headingEffect.setBorderRight(CellStyle.BORDER_THIN);
			headingEffect.setAlignment(CellStyle.ALIGN_CENTER);
			headingEffect.setVerticalAlignment(CellStyle.VERTICAL_TOP);
			headingEffect.setWrapText(true);

			headingNameEffect.setFont(headings);

			List<String> heading = getHeading();
			for (int i = 0, j = 0; j < heading.size(); i++, j++) {
				if (i == 6) {
					i = i + 1;
				}
				Cell cell = statementHeadingRow.createCell(i);
				// conveyance_sheet.autoSizeColumn((short)200);
				cell.setCellValue(heading.get(j));
				cell.setCellStyle(headingEffect);
			}

			Row fromToForPlace = conveyance_sheet.createRow(12);
			for (int i = 0; i < 11; i++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
				cellStyle.setBorderRight(CellStyle.BORDER_THIN);
				cellStyle.setBorderTop(CellStyle.BORDER_NONE);

				/*
				 * if(i== 5 || i==6){ continue; }
				 */
				Cell cell = fromToForPlace.createCell(i);
				/*
				 * if(i==2 || i==4){
				 * //conveyance_sheet.autoSizeColumn((int)i,true); }
				 */
				cell.setCellStyle(cellStyle);
			}

			int k = 0, linecountothers, linecountconveyance, o = 0, linetotal = 0, amountcon = 0, amountothr = 0;
			Cell fromCell = fromToForPlace.createCell(5);
			fromCell.setCellValue("From");
			fromCell.setCellStyle(headingEffect);

			Cell toCell = fromToForPlace.createCell(6);
			toCell.setCellValue("To");
			toCell.setCellStyle(headingEffect);
			linecountconveyance = mapconveyancetravel.keySet().size();
			linecountothers = mapconveyanceothers.keySet().size();

			linetotal = 32 + linecountconveyance + linecountothers;
			for (int i = 13; i < linetotal; i++) {
				HSSFCellStyle designForParticular = workbook.createCellStyle();
				designForParticular.setBorderLeft(CellStyle.BORDER_THIN);
				designForParticular.setBorderRight(CellStyle.BORDER_THIN);
				Row row = conveyance_sheet.createRow(i);
				List<String> innervallist = null;
				if (i == 13) {
					designForParticular.setBorderTop(CellStyle.BORDER_THIN);
				}
				for (int j = 0; j < 11; j++) {
					Cell cell = row.createCell(j);
					cell.setCellStyle(designForParticular);

					if (i == 13 && j == 0) {
						HSSFCellStyle srNosA = workbook.createCellStyle();
						Font srNoFont = workbook.createFont();
						srNoFont.setBoldweight((short) 1000);
						srNosA.setAlignment(CellStyle.ALIGN_CENTER);
						srNosA.setBorderTop(CellStyle.BORDER_THIN);
						srNosA.setBorderLeft(CellStyle.BORDER_THIN);
						srNosA.setFont(srNoFont);
						cell.setCellValue("A");
						cell.setCellStyle(srNosA);
					}
					if (i == 13 && j == 1) {
						HSSFCellStyle conveyance = workbook.createCellStyle();
						Font fontForConveyance = workbook.createFont();
						fontForConveyance.setBoldweight((short) 1000);
						fontForConveyance.setUnderline((byte) 1);
						conveyance.setBorderTop(CellStyle.BORDER_THIN);
						conveyance.setBorderLeft(CellStyle.BORDER_THIN);
						conveyance.setFont(fontForConveyance);
						cell.setCellStyle(conveyance);
						cell.setCellValue("Conveyance:-");
						// conveyance_sheet.autoSizeColumn((short)j);
					}
					if (i >= 15 && i < 15 + linecountconveyance && j < 11) {

						if (j == 0) {
							k++;
							HSSFCellStyle srNosA = workbook.createCellStyle();
							Font srNoFont = workbook.createFont();
							srNoFont.setBoldweight((short) 1000);
							srNosA.setAlignment(CellStyle.ALIGN_CENTER);
							srNosA.setBorderLeft(CellStyle.BORDER_THIN);
							srNosA.setFont(srNoFont);
							cell.setCellValue(k);
							cell.setCellStyle(srNosA);
							// System.out.println("in first if j==0" + k);
							// System.out.println(mapconveyancetravel);

						} else if (j == 1) {
							cell.setCellValue(listconven.get(k - 1).substring(1));
							innervallist = mapconveyancetravel.get(listconven.get(k - 1));
							amountcon = amountcon + UF.parseToInt(innervallist.get(8));

						} else if (j > 1 && j < 11) {
							HSSFCellStyle srNosA = workbook.createCellStyle();
							srNosA.setAlignment(CellStyle.ALIGN_CENTER);
							srNosA.setBorderLeft(CellStyle.BORDER_THIN);
							srNosA.setBorderRight(CellStyle.BORDER_THIN);
							cell.setCellValue(innervallist.get(j - 2));
							cell.setCellStyle(srNosA);
						} else {

						}
					}

					// System.out.println("amount other====amountcon" +
					// amountothr + "con   " + amountcon);
					if (i == 23 + linecountconveyance && j == 0) {
						HSSFCellStyle srNosA = workbook.createCellStyle();
						Font srNoFont = workbook.createFont();
						srNoFont.setBoldweight((short) 1000);
						srNosA.setAlignment(CellStyle.ALIGN_CENTER);
						srNosA.setBorderLeft(CellStyle.BORDER_THIN);
						srNosA.setFont(srNoFont);
						cell.setCellStyle(srNosA);
						cell.setCellValue("B");
					}
					if (i == 23 + linecountconveyance && j == 1) {
						HSSFCellStyle conveyance = workbook.createCellStyle();
						Font fontForConveyance = workbook.createFont();
						fontForConveyance.setBoldweight((short) 1000);
						fontForConveyance.setUnderline((byte) 1);
						conveyance.setBorderLeft(CellStyle.BORDER_THIN);
						conveyance.setFont(fontForConveyance);
						cell.setCellStyle(conveyance);
						cell.setCellValue("Other Charges:-");
					}
					// conveyance_sheet.autoSizeColumn((int)j);
					if (i >= 25 + linecountconveyance && i < (25 + linecountconveyance + linecountothers) && j < 11) {
						if (j == 0) {
							o++;
							HSSFCellStyle srNosA = workbook.createCellStyle();
							Font srNoFont = workbook.createFont();
							srNoFont.setBoldweight((short) 1000);
							srNosA.setAlignment(CellStyle.ALIGN_CENTER);
							srNosA.setBorderLeft(CellStyle.BORDER_THIN);
							srNosA.setFont(srNoFont);
							cell.setCellValue(o);
							cell.setCellStyle(srNosA);

						} else if (j == 1) {
							cell.setCellValue(listothers.get(o - 1).substring(1));
							innervallist = mapconveyanceothers.get(listothers.get(o - 1));

							amountothr = amountothr + UF.parseToInt(innervallist.get(8));

						} else if (j > 1 && j < 11) {
							HSSFCellStyle srNosA = workbook.createCellStyle();
							srNosA.setAlignment(CellStyle.ALIGN_CENTER);
							srNosA.setBorderLeft(CellStyle.BORDER_THIN);
							srNosA.setBorderRight(CellStyle.BORDER_THIN);
							cell.setCellValue(innervallist.get(j - 2));
							cell.setCellStyle(srNosA);
						} else {

						}

					}
				}
			}

			Row grandTotalRow = conveyance_sheet.createRow(linetotal);
			Font styleForGrandTotal = workbook.createFont();
			styleForGrandTotal.setBoldweight((short) 1000);
			for (int i = 0; i < 11; i++) {
				Cell cell = grandTotalRow.createCell(i);
				HSSFCellStyle styleForTotalDes = workbook.createCellStyle();
				styleForTotalDes.setBorderBottom(CellStyle.BORDER_THIN);
				styleForTotalDes.setBorderTop(CellStyle.BORDER_THIN);
				styleForTotalDes.setFont(styleForGrandTotal);
				if (i == 0 || i == 10) {
					styleForTotalDes.setBorderLeft(CellStyle.BORDER_THIN);
				}
				if (i == 8) {
					cell.setCellValue("Grand Total");
				}
				if (i == 10) {
					cell.setCellValue(amountothr + amountcon);
					styleForTotalDes.setAlignment(CellStyle.ALIGN_RIGHT);
					styleForTotalDes.setBorderRight(CellStyle.BORDER_THIN);
				}
				cell.setCellStyle(styleForTotalDes);
			}
			linetotal++;

			Row blankRow5 = conveyance_sheet.createRow(linetotal);
			applyLeftAndRightBorderTable(blankRow5);
			linetotal++;
			Row noteRow = conveyance_sheet.createRow(linetotal);
			applyLeftAndRightBorderTable(noteRow);
			Cell noteCell = noteRow.createCell(0);
			HSSFCellStyle styleForNote = workbook.createCellStyle();
			styleForNote.setBorderLeft(CellStyle.BORDER_THIN);
			noteCell.setCellValue(" note");
			noteCell.setCellStyle(styleForNote);
			linetotal++;
			Row toAndFroOffice = conveyance_sheet.createRow(linetotal);
			applyLeftAndRightBorderTable(toAndFroOffice);
			Cell toAndFromOffCell = toAndFroOffice.createCell(0);
			toAndFromOffCell.setCellValue("  * -> To and Fro Office");
			toAndFromOffCell.setCellStyle(styleForNote);
			linetotal++;
			Row rule1 = conveyance_sheet.createRow(linetotal);
			applyLeftAndRightBorderTable(rule1);
			Cell rule1Cell = rule1.createCell(0);
			rule1Cell.setCellValue(" 1) Distance should be calculated from office to Client Place / Pickup Point or Home to Client Place / Pickup Point, whichever is less.");
			rule1Cell.setCellStyle(styleForNote);
			linetotal++;
			Row rule2 = conveyance_sheet.createRow(linetotal);
			applyLeftAndRightBorderTable(rule2);
			Cell rule2Cell = rule2.createCell(0);
			rule2Cell.setCellValue(" 2) Rickshaw Charges are not allowed except under exceptional situations and with Partners' Approval only.");
			rule2Cell.setCellStyle(styleForNote);
			linetotal++;
			for (int i = linetotal; i < linetotal + 6; i++) {
				Row blankRowAfterRules = conveyance_sheet.createRow(i);
				applyLeftAndRightBorderTable(blankRowAfterRules);
			}
			linetotal = linetotal + 6;
			Row signatureRow = conveyance_sheet.createRow(linetotal);
			applyLeftAndRightBorderTable(signatureRow);
			Cell preparedBy = signatureRow.createCell(1);
			preparedBy.setCellValue("Prepared By : "+UF.showData((String)nameheaderlist.get(0), ""));

			Cell checkedBy = signatureRow.createCell(4);
			checkedBy.setCellValue("Checked By : "+UF.showData(hmApproveMap.get("APPROVED_BY"), ""));

			Cell approvedBy = signatureRow.createCell(8);
			approvedBy.setCellValue("Approved By : "+UF.showData(hmApproveMap.get("APPROVED_BY"), ""));

			linetotal++;
			Row lastRow = conveyance_sheet.createRow(linetotal);
			forBlankRowsWithBorder(lastRow);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// conveyance sheet
	public void applyLeftAndRightBorderTable(Row blankRow) {
		Cell firstCellOfBlankRow = blankRow.createCell(0);
		HSSFCellStyle blankRowStyle1 = workbook.createCellStyle();
		blankRowStyle1.setBorderLeft(CellStyle.BORDER_THIN);
		firstCellOfBlankRow.setCellStyle(blankRowStyle1);

		Cell lastCellOfBlankRow = blankRow.createCell(10);
		HSSFCellStyle blankRowStyle2 = workbook.createCellStyle();
		blankRowStyle2.setBorderRight(CellStyle.BORDER_THIN);
		lastCellOfBlankRow.setCellStyle(blankRowStyle2);

	}

	// conveyance sheet
	public void forBlankRowsWithBorder(Row lastRow) {
		for (int i = 0; i < 11; i++) {
			HSSFCellStyle bottomBorder = workbook.createCellStyle();
			bottomBorder.setBorderBottom(CellStyle.BORDER_THIN);
			if (i == 0) {
				bottomBorder.setBorderLeft(CellStyle.BORDER_THIN);
			}
			if (i == 10) {
				bottomBorder.setBorderRight(CellStyle.BORDER_THIN);
			}
			Cell cell = lastRow.createCell(i);
			cell.setCellStyle(bottomBorder);
		}
	}

	// conveyance sheet
	public List<String> getHeading() {

		List<String> heading = new ArrayList<String>();
		heading.add("Sr.No");
		heading.add("Client");
		heading.add("Mode of Travel");
		heading.add("No. of Persons");
		heading.add("Chargeable to Client(Y/N)");
		heading.add("Place");
		heading.add("Days");
		heading.add("KM / Day *");
		heading.add("Rate / KM");
		heading.add("Amount Rs.");

		return heading;
	}

	// Time line sheet
	public List<String> getActivitiesList() {
		List<String> activities = new ArrayList<String>();
		activities.add(" Office Work :");
		activities.add(" Others(specify)");
		activities.add(" Training");
		activities.add(" Idle Time");
		activities.add(" Leave");
		activities.add(" Office Hoildays");

		return activities;
	}

	// Time line sheet
	public void forBlankRowLeftAndRightBorder(Row row) {
		for (int i = 0; i < 8 + coloumncountmain; i++) {
			HSSFCellStyle bottomBorder = workbook.createCellStyle();
			if (i == 0) {
				bottomBorder.setBorderLeft(CellStyle.BORDER_THIN);
			}
			if (i == coloumncountmain + 7) {
				bottomBorder.setBorderRight(CellStyle.BORDER_THIN);
			}
			Cell cell = row.createCell(i);
			cell.setCellStyle(bottomBorder);
		}
	}

	public String getReportName() {
		String name = "Timesheet_" + getEmpid() + "_" + getDatefrom() + "_" + getDateto();
		return name;
	}

	public String getDownloadSubmit() {
		return downloadSubmit;
	}

	public void setDownloadSubmit(String downloadSubmit) {
		this.downloadSubmit = downloadSubmit;
	}

	public String getStrPC() {
		return strPC;
	}

	public void setStrPC(String strPC) {
		this.strPC = strPC;
	}

	public List<Date> getholidayslisttimesheet(String strWlocationId) {

		UtilityFunctions UF = new UtilityFunctions();
		List<Date> dateList = new ArrayList<Date>();
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			Calendar cal3 = GregorianCalendar.getInstance();
			cal3.set(Calendar.DAY_OF_MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "dd")));
			cal3.set(Calendar.MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "MM")) - 1);
			cal3.set(Calendar.YEAR, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "yyyy")));

			
			con = db.makeConnection(con);

			Map<String, Set<String>> hmWeekendMap = CF.getWeekEndDateList(con, getDatefrom(), getDateto(), CF, UF,null,null);

			Set<String> set = new HashSet<String>();

			Iterator<String> itr = hmWeekendMap.keySet().iterator();
			while (itr.hasNext()) {
				String locId = itr.next();
				if (locId!=null && locId.equals(strWlocationId)) {
					set = hmWeekendMap.get(locId);
					//temp.substring(0, (temp.length()>10 ? 10 : temp.length())));
				}
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
//			System.out.println("getDatefrom() ==>>> " + getDatefrom()+ " -- getDateto() ===>> " + getDateto());
			Date datefromcheck = dateFormat.parse(getDatefrom());
			Date datetocheck = dateFormat.parse(getDateto());
//			System.out.println("set ==>>> " + set);
			itr = set.iterator();
			while (itr.hasNext()) {
				Date finaldate = dateFormat.parse(itr.next().toString());
				if (UF.isDateBetween(datefromcheck, datetocheck, finaldate)) {
					dateList.add(finaldate);
				}
			}
//			System.out.println("dateList ==>>> " + dateList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		return dateList;
	}

}