package com.konnect.jpms.tms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.leave.GetEmployeePolicyDetails;
import com.konnect.jpms.leave.ManagerLeaveApproval;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ImportAttendance1 extends ActionSupport implements ServletRequestAware, IConstants, IStatements {
	private static final long serialVersionUID = 1L;

	HttpServletRequest request;
	private File fileUpload1, fileUpload2, fileUpload3, fileUpload4;

	CommonFunctions CF;
	UtilityFunctions uF;
	HttpSession session;
	List<FillPayCycles> paycycleList;
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	String wLocation;
	String f_org;
	String paycycle;
	String check;

	String pageFrom;
	
	public String execute() {

		session = request.getSession();
		uF = new UtilityFunctions();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		orgList = new FillOrganisation(request).fillOrganisation();

		if (orgList != null && orgList.size() > 0) {
			if (getF_org() != null) {
				wLocationList = new FillWLocation(request).fillWLocation(getF_org());
				paycycleList = new FillPayCycles(request).fillPayCycles(CF,uF.parseToInt(getF_org()));
			} else {
				wLocationList = new FillWLocation(request).fillWLocation(orgList.get(0).getOrgId());
				paycycleList = new FillPayCycles(request).fillPayCycles(CF,uF.parseToInt(orgList.get(0).getOrgId()));
			}

		} else {
			wLocationList = new FillWLocation(request).fillWLocation();
			paycycleList = new FillPayCycles(request).fillPayCycles(CF);
		}

		/*
		 * boolean isView = CF.getAccess(session, request, uF); if(!isView){
		 * request.setAttribute(PAGE, PAccessDenied);
		 * request.setAttribute(TITLE, TAccessDenied); return ACCESS_DENIED; }
		 */

		request.setAttribute(PAGE, "/jsp/tms/ImportAttendance.jsp");
		request.setAttribute(TITLE, "Import Attendance");

		if (fileUpload1 != null) {
			ImportFormatCodeA0002(fileUpload1);
			return SUCCESS;
		}

		// else if(fileUpload2 != null){
		// format7Attendance(fileUpload2);
		// }
		else if (fileUpload3 != null) {
			ImportFormatCodeR0007(fileUpload3);
			return SUCCESS;
		} else if (check != null && getF_org() != null) {
			ImportFormatCodeR0008();
			return SUCCESS;
		} else if (fileUpload4 != null) {
			ImportFormatCodeR0010(fileUpload4);
			return SUCCESS;
		}

		return LOAD;
	}

	public File getFileUpload4() {
		return fileUpload4;
	}

	public void setFileUpload4(File fileUpload4) {
		this.fileUpload4 = fileUpload4;
	}

	private void ImportFormatCodeR0010(File path) {

		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement prepareStatement = null;
		PreparedStatement prepareStatement1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		UtilityFunctions uF = new UtilityFunctions();
		List<String> alReport = new ArrayList<String>();
		String dateFormat = "MM/dd/yyyy";
		String timeFormat = "HH:mm";
		// Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap();
		// CF.getEmpLevelMap();
		String[] strPayCycleDates = null;
		if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
			strPayCycleDates = CF.getPrevPayCycle(con, strPayCycleDates[1],CF.getStrTimeZone(), CF);
		}
		String strD1 = strPayCycleDates[0];
		String strD2 = strPayCycleDates[1];
		java.sql.Date D1 = uF.getDateFormat(strD1, DATE_FORMAT);
		java.sql.Date D2 = uF.getDateFormat(strD2, DATE_FORMAT);
		java.util.Date dt1 = new java.util.Date(D1.getTime());
		java.util.Date dt2 = new java.util.Date(D2.getTime());
		BufferedReader br = null;
		try {

			prepareStatement = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
							+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
			prepareStatement1 = con.prepareStatement("Insert into roster_details (emp_id,_date,_from,_to,isapproved,user_id,service_id,"
					+ "actual_hours,attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id)values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");

			Map<String, String> empMp = new HashMap<String, String>();
			Map<String, String> empjoiningDateMp = new HashMap<String, String>();
			Map<String, String> empEndMp = new HashMap<String, String>();
			Map<String, String> empServiceMp = new HashMap<String, String>();

			Map<String, String> empCodeMp = new HashMap<String, String>();
			List<String> empList = new ArrayList<String>();

			pst = con.prepareStatement("select emp_per_id,empcode,org_id,service_id,wlocation_id,joining_date,employment_end_date,biometrix_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id and org_id=? and wlocation_id=?  order by org_id");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));

			rs = pst.executeQuery();
			while (rs.next()) {
				empList.add(rs.getString("emp_per_id"));
				empMp.put(rs.getString("biometrix_id"),rs.getString("emp_per_id"));
				
				String strEmpServiceId = "0";
				if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
					String[] str = rs.getString("service_id").split(",");
					for (int z = 0; str != null && z < str.length; z++) {
						if (uF.parseToInt(str[z]) > 0) {
							strEmpServiceId = str[z];
						}
					}
				}				
				empServiceMp.put(rs.getString("emp_per_id"),strEmpServiceId);
				
				empEndMp.put(rs.getString("emp_per_id"),rs.getString("employment_end_date"));
				empjoiningDateMp.put(rs.getString("emp_per_id"),rs.getString("joining_date"));
				empCodeMp.put(rs.getString("emp_per_id"),rs.getString("empcode"));

			}
			rs.close();
			pst.close();

			Map<String, String> hmEmpLevelMap = new HashMap<String, String>();

			pst = con.prepareStatement("select * from level_details ld right join (select * from designation_details dd right join (select *, gd.designation_id as designationid from employee_official_details eod, grades_details gd where gd.grade_id=eod.grade_id and org_id=? and wlocation_id=?) a on a.designationid=dd.designation_id) a on a.level_id=ld.level_id");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			rs = pst.executeQuery();

			while (rs.next()) {
				hmEmpLevelMap.put(rs.getString("emp_id"),rs.getString("level_id"));
			}
			rs.close();
			pst.close();

			List<String> holidayList = new ArrayList<String>();
			pst = con.prepareStatement("select _date from holidays where org_id=? and wlocation_id=? and _date between ? and ? and (is_optional_holiday is null or is_optional_holiday=false)");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			pst.setDate(3, D1);
			pst.setDate(4, D2);
			rs = pst.executeQuery();
			while (rs.next()) {
				holidayList.add(rs.getString("_date"));
			}
			rs.close();
			pst.close();

			Map<String, String> leaveMp = new HashMap<String, String>();

			pst = con.prepareStatement("select leave_type_id,leave_type_name from leave_type where org_id=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			rs = pst.executeQuery();
			while (rs.next()) {
				leaveMp.put(rs.getString("leave_type_name"),rs.getString("leave_type_id"));

			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select wlocation_start_time,wlocation_end_time,wlocation_weeklyoff1,wlocation_weeklyoff2,wlocation_weeklyoff3,wlocation_weeklyofftype1,wlocation_weeklyofftype2,wlocation_weeklyofftype3 from work_location_info where  org_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			rs = pst.executeQuery();
			List<String> wlocationData = new ArrayList<String>();
			while (rs.next()) {
				wlocationData.add(rs.getString("wlocation_start_time"));
				wlocationData.add(rs.getString("wlocation_end_time"));

			}
			rs.close();
			pst.close();

			Map<String, List<String>> RosterMp = new HashMap<String, List<String>>();

			pst = con.prepareStatement("Select roster_id,rd.emp_id, _date,_from,_to,rd.service_id,actual_hours from roster_details rd,employee_official_details eod where rd.emp_id=eod.emp_id and org_id=? and wlocation_id=? and _date between ? and ?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			pst.setDate(3, D1);
			pst.setDate(4, D2);

			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("roster_id"));
				innerList.add(rs.getString("_from"));
				innerList.add(rs.getString("_to"));
				innerList.add(rs.getString("service_id"));
				innerList.add(rs.getString("actual_hours"));

				RosterMp.put(rs.getString("emp_id") + "_" + rs.getString("_date"),innerList);

			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("Select count(*) as attendance_count,ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date from attendance_details "
							+ " ad,employee_official_details eod where ad.emp_id=eod.emp_id and org_id=? and wlocation_id=? "
							+ "and to_date(in_out_timestamp::text,'yyyy-MM-dd') between  ?  and ?"
							+ " group by ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd')");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			pst.setDate(3, D1);
			pst.setDate(4, D2);
			rs = pst.executeQuery();
			Map<String, String> empAttendanceMp = new HashMap<String, String>();
			while (rs.next()) {
				empAttendanceMp.put(rs.getString("emp_id") + "_"+ rs.getString("attendance_date"),rs.getString("attendance_count"));

			}
			rs.close();
			pst.close();
			// ========================================================================================================================

			br = new BufferedReader(new FileReader(path));
			String line = null;
			br.readLine();
			SimpleDateFormat smft = new SimpleDateFormat(dateFormat);
			// Map<String,List<List<String>>> mp=new
			// HashMap<String,List<List<String>>>();
			List<List<String>> outerList = new ArrayList<List<String>>();
			// List<String> dateList=new ArrayList<String>();
			while ((line = br.readLine()) != null) {

				List<String> innerList = Arrays.asList(line.split("\t"));
				if (innerList.size() < 11) {
					innerList = Arrays.asList(line.split(","));
				}
				if (innerList.size() < 11) {
					continue;
				}
				;
				// String strDate=smft.format(smft.parse(innerList.get(2)));
				// List<List<String>> outerList=mp.get(strDate);
				// if(outerList==null)outerList=new ArrayList<List<String>>();
				outerList.add(innerList);
				// mp.put(strDate,outerList);
				// if(!dateList.contains(strDate)){
				// dateList.add(strDate);
				// }
			}

			// Set set =mp.keySet();
			// Collections.sort(dateList);

			for (int size1 = outerList.size() - 1; size1 > 0; size1--) {
				List<String> innerList = outerList.get(size1);
				if ("A".equalsIgnoreCase(innerList.get(13))) {
					continue;
				}
				String strStartDate = innerList.get(2);
				// uF.isDateBetween(startDate, endDate, betweenDate);
				if (!uF.isDateBetween(dt1, dt2, smft.parse(strStartDate))) {
					continue;
				}

				// List<List<String>> outerList=mp.get(dateList.get(size1));
				// System.out.println("dateList.get(size1)==="+dateList.get(size1));
				// for(int size=0;outerList!=null &&
				// size<outerList.size();size++){

				String empCode = innerList.get(0).trim();
				if (empCode.contains(".")) {
					empCode = empCode.substring(0, empCode.indexOf("."));

				}
				if (empCode.equals("0")) {
					continue;
				}
				int empId = uF.parseToInt(empMp.get(empCode));
				if (empId == 0) {
					continue;
				}

				String strInTime = innerList.get(8);
				String strOutTime = innerList.get(10);
				strStartDate = uF.getDateFormat(strStartDate, dateFormat,DBDATE);
				int servic_id = uF.parseToInt(empServiceMp.get(empId + ""));

				// ====================Insert Roster if not
				// Exist========================

				if (RosterMp.get(empId + "_" + strStartDate) != null) {
					// System.out.println("Roster Detail Already Exists======>");
				} else {

					String workInTime = wlocationData.get(0);
					String workOuttime = wlocationData.get(1);

					prepareStatement1.setInt(1, empId);
					prepareStatement1.setDate(2,uF.getDateFormat(strStartDate, DBDATE));
					prepareStatement1.setTime(3, new java.sql.Time(uF.getDateFormat(workInTime, DBTIME).getTime()));
					prepareStatement1.setTime(4, new java.sql.Time(uF.getDateFormat(workOuttime, DBTIME).getTime()));
					prepareStatement1.setBoolean(5, false);
					prepareStatement1.setInt(6, 310);
					prepareStatement1.setInt(7, servic_id); // service id
					prepareStatement1.setDouble(8, uF.parseToDouble(uF.getTimeDiffInHoursMins(uF.getDateFormat(workInTime, DBTIME).getTime(),uF.getDateFormat(workOuttime, DBTIME).getTime())));
					prepareStatement1.setInt(9, 0);
					prepareStatement1.setBoolean(10, false);
					prepareStatement1.setInt(11, 1);
					prepareStatement1.setDate(12,new java.sql.Date(System.currentTimeMillis()));
					prepareStatement1.setInt(13, 1);
					prepareStatement1.execute();
					prepareStatement1.clearParameters();

					List<String> innerList1 = new ArrayList<String>();
					innerList1.add("0");
					innerList1.add(workInTime);
					innerList1.add(workOuttime);
					innerList1.add(servic_id + "");
					innerList1.add(uF.getTimeDiffInHoursMins(uF.getDateFormat(workInTime, DBTIME).getTime(), uF.getDateFormat(workOuttime, DBTIME).getTime()));
					RosterMp.put(empId + "_" + strStartDate, innerList1);

				}
				// =======================Insert Roster
				// Finish===========================

				int i = uF.parseToInt(empAttendanceMp.get(empId + "_"
						+ strStartDate));
				if (i >= 2) {

				} else {

					String _fromTime = null;
					String _toTime = null;

					List<String> rosterList = RosterMp.get(empId + "_"+ strStartDate);
					if (rosterList != null) {

						_fromTime = rosterList.get(1);
						_toTime = rosterList.get(2);
						servic_id = uF.parseToInt(rosterList.get(3));
					}

					if (i == 0) {
						long lIn = 0;
						lIn = uF.getTimeFormat(strStartDate + strInTime,DBDATE + timeFormat).getTime();
						insertINEntry(con, prepareStatement, rs, uF, empId,servic_id, strStartDate, strInTime, _fromTime,DBDATE, timeFormat);
						updateBreakRegisters(uF, con, hmEmpLevelMap, "IN",strStartDate, _fromTime, lIn, strInTime, ""+ wLocation, empId);
						// IN CODING
					}
					// OUT CODING
					long lOut = 0;
					lOut = uF.getTimeFormat(strStartDate + strOutTime,DBDATE + timeFormat).getTime();
					insertOUTEntry(con, prepareStatement, rs, uF, empId,servic_id, strStartDate, strOutTime, _toTime,strInTime, DBDATE, timeFormat);
					updateBreakRegisters(uF, con, hmEmpLevelMap, "OUT",strStartDate, _fromTime, lOut, strInTime, ""+ wLocation, empId);
				}
				// ===================end import Attendance===================

				// }
				alReport.add("<li class=\"msg_success\" style=\"margin:0px\">"+ empCode + " Attendance Inserted Successfully.</li>");
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(prepareStatement1);
			db.closeStatements(prepareStatement);
			db.closeStatements(pst);
			db.closeConnection(con);
			try {
				br.close();
			} catch (Exception ex) {

			}
		}
		session.setAttribute("alReport", alReport);

	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	private void ImportFormatCodeR0008() {

		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement prepareStatement = null;
		PreparedStatement prepareStatement1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		List<String> alReport = new ArrayList<String>();
		// String dateFormat="dd.MM.yyyy";
		String timeFormat = "HH:mm:ss";
		// Map<String,String> hmEmpLevel = CF.getEmpLevelMap();
		// SimpleDateFormat parser1 = new SimpleDateFormat("HH:mm:ss");
		// SimpleDateFormat parser = new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String[] strPayCycleDates = null;
		if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
			// strPayCycleDates = CF.getPrevPayCycle(strPayCycleDates[1],
			// CF.getStrTimeZone(), CF);
		}
		String strD1 = strPayCycleDates[0];
		String strD2 = strPayCycleDates[1];
		// String strPC = strPayCycleDates[2];

		try {
			con = db.makeConnection(con);
			prepareStatement = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
							+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
			prepareStatement1 = con.prepareStatement("Insert into roster_details (emp_id,_date,_from,_to,isapproved,user_id,service_id," 
					+ "actual_hours,attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
			// Map<String,String> empMp=new HashMap<String,String>();
			// Map<String,String> OrgDateMp=new HashMap<String,String>();
			Map<String, String> empServiceMp = new HashMap<String, String>();
			// Map<String,String> empWlocationMp=new HashMap<String,String>();
			Map<String, String> empCodeMp = new HashMap<String, String>();
			Map<String, String> empjoiningDateMp = new HashMap<String, String>();
			Map<String, String> empEndMp = new HashMap<String, String>();
			List<String> empList = new ArrayList<String>();
			// =============================================================================================

			// Map<String,String> empMp=new HashMap<String,String>();
			// Map<String,String> empOrgMp=new LinkedHashMap<String,String>();
			// Map<String,String> empjoiningDateMp=new HashMap<String,String>();
			// Map<String,String> empEndMp=new HashMap<String,String>();
			// Map<String,String> empServiceMp=new HashMap<String,String>();

			// Map<String,String> empCodeMp=new HashMap<String,String>();
			// List<String> empList=new ArrayList<String>();

			pst = con.prepareStatement("select emp_per_id,empcode,org_id,service_id,wlocation_id,joining_date,employment_end_date from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id and org_id=? and wlocation_id=?  order by org_id");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			rs = pst.executeQuery();
			while (rs.next()) {
				empList.add(rs.getString("emp_per_id"));
				// empMp.put(rs.getString("empcode"),rs.getString("emp_per_id"));
				String strEmpServiceId = "0";
				if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
					String[] str = rs.getString("service_id").split(",");
					for (int z = 0; str != null && z < str.length; z++) {
						if (uF.parseToInt(str[z]) > 0) {
							strEmpServiceId = str[z];
						}
					}
				}				
				empServiceMp.put(rs.getString("emp_per_id"),strEmpServiceId);
				
				empEndMp.put(rs.getString("emp_per_id"),rs.getString("employment_end_date"));
				empjoiningDateMp.put(rs.getString("emp_per_id"),rs.getString("joining_date"));
				empCodeMp.put(rs.getString("emp_per_id"),rs.getString("empcode"));

			}
			rs.close();
			pst.close();

			List<String> holidayList = new ArrayList<String>();
			pst = con.prepareStatement("select _date from holidays where org_id=? and wlocation_id=? and _date between ? and ? and (is_optional_holiday is null or is_optional_holiday=false)");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				holidayList.add(rs.getString("_date"));
			}
			rs.close();
			pst.close();
			// System.out.println("pst====>>"+pst);
			// System.out.println("holidayList==>"+holidayList);

			pst = con.prepareStatement("select wlocation_start_time,wlocation_end_time,wlocation_weeklyoff1,wlocation_weeklyoff2,wlocation_weeklyoff3,wlocation_weeklyofftype1,wlocation_weeklyofftype2,wlocation_weeklyofftype3 from work_location_info where  org_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			rs = pst.executeQuery();
			String weeklyoff1 = null;
			String weeklyoff2 = null;
			String weeklyoff3 = null;

			String weeklyoff1type = null;
			String weeklyoff2type = null;
			String weeklyoff3type = null;
			// Map<String,List<String>> wLocationMp=new
			// HashMap<String,List<String>>();
			List<String> wlocationData = new ArrayList<String>();
			while (rs.next()) {
				// List<String> innerList=new ArrayList<String>();
				wlocationData.add(rs.getString("wlocation_start_time"));
				wlocationData.add(rs.getString("wlocation_end_time"));
				// wLocationMp.put(rs.getString("wlocation_id"),innerList);
				weeklyoff1 = rs.getString("wlocation_weeklyoff1");
				weeklyoff2 = rs.getString("wlocation_weeklyoff2");
				weeklyoff3 = rs.getString("wlocation_weeklyoff3");

				weeklyoff1type = rs.getString("wlocation_weeklyofftype1");
				weeklyoff2type = rs.getString("wlocation_weeklyofftype2");
				weeklyoff3type = rs.getString("wlocation_weeklyofftype3");

			}
			rs.close();
			pst.close();

			Map<String, List<String>> RosterMp = new HashMap<String, List<String>>();

			// if(strDate1!=null){
			pst = con.prepareStatement("Select roster_id,rd.emp_id, _date,_from,_to,rd.service_id,actual_hours from roster_details rd,employee_official_details eod where rd.emp_id=eod.emp_id and org_id=? and wlocation_id=? and _date between ? and ?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			// }else{
			// pst =
			// con.prepareStatement("Select roster_id,rd.emp_id, _date,_from,_to,rd.service_id,actual_hours from roster_details rd,employee_official_details eod where rd.emp_id=eod.emp_id and org_id=?");
			// pst.setInt(1,uF.parseToInt(orgList.get(i)));
			// }

			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("roster_id"));
				innerList.add(rs.getString("_from"));
				innerList.add(rs.getString("_to"));
				innerList.add(rs.getString("service_id"));
				innerList.add(rs.getString("actual_hours"));

				RosterMp.put(rs.getString("emp_id") + "_" + rs.getString("_date"),innerList);

			}
			rs.close();
			pst.close();

			// rosterOrg.put(orgList.get(i),RosterMp);

			// if(strDate1!=null){
			pst = con.prepareStatement("Select count(*) as attendance_count,ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date from attendance_details "
							+ " ad,employee_official_details eod where ad.emp_id=eod.emp_id and org_id=? and wlocation_id=? "
							+ "and to_date(in_out_timestamp::text,'yyyy-MM-dd') between  ?  and ?"
							+ " group by ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd')");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			// }else{
			// pst =
			// con.prepareStatement("Select count(*) as attendance_count,ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date from attendance_details "
			// +
			// " ad,employee_official_details eod where ad.emp_id=eod.emp_id and org_id=? "
			// +
			// " group by ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd')");
			// pst.setInt(1,uF.parseToInt(orgList.get(i)));
			//
			// }
			rs = pst.executeQuery();
			Map<String, String> empAttendanceMp = new HashMap<String, String>();
			while (rs.next()) {
				empAttendanceMp.put(rs.getString("emp_id") + "_"+ rs.getString("attendance_date"),rs.getString("attendance_count"));
			}
			rs.close();
			pst.close();
			// ======================================================================================================
			// Map<String,Map<String,List<String>>> rosterOrg=new
			// HashMap<String,Map<String,List<String>>>();
			// Map<String,Map<String,String>> attendanceOrg=new
			// HashMap<String,Map<String,String>>();
			// xzxz
			// Map<String,String[]> orgPaycycleMp=new
			// HashMap<String,String[]>();
			// for(int i=0;i<orgList.size();i++){
			// String strDate=OrgDateMp.get(orgList.get(i));
			// if(strDate==null){
			// strDate=actDate;
			// }
			// String strDate1=null;
			// String strEndDate1=null;

			// if(strDate==null){
			// String[] strPayCycleDate =
			// CF.getCurrentPayCycle(CF.getStrTimeZone(), CF);
			// strDate1=strPayCycleDate[0];
			// strEndDate1=strPayCycleDate[1];
			// orgPaycycleMp.put(orgList.get(i), strPayCycleDate);
			//
			// }else{
			// String[] strPayCycleDate =
			// CF.getCurrentPayCycle(CF.getStrTimeZone(),
			// uF.getDateFormatUtil(strDate, DATE_FORMAT), CF);
			// strDate1=strPayCycleDate[0];
			// strEndDate1=strPayCycleDate[1];
			// orgPaycycleMp.put(orgList.get(i), strPayCycleDate);
			// }

			// Map<String,List<String>> RosterMp=new
			// HashMap<String,List<String>>();

			// if(strD1!=null){
			// pst =
			// con.prepareStatement("Select roster_id,rd.emp_id, _date,_from,_to,rd.service_id,actual_hours from roster_details rd,employee_official_details eod where rd.emp_id=eod.emp_id and org_id=? and _date between ? and ?");
			// pst.setInt(1,uF.parseToInt(getF_org()));
			// pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			// pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			//
			//
			// // }else{
			// // pst =
			// con.prepareStatement("Select roster_id,rd.emp_id, _date,_from,_to,rd.service_id,actual_hours from roster_details rd,employee_official_details eod where rd.emp_id=eod.emp_id and org_id=?");
			// // pst.setInt(1,uF.parseToInt(getF_org()));
			// // }
			//
			// rs = pst.executeQuery();
			// while (rs.next()) {
			// List<String> innerList=new ArrayList<String>();
			// innerList.add(rs.getString("roster_id"));
			// innerList.add(rs.getString("_from"));
			// innerList.add(rs.getString("_to"));
			// innerList.add(rs.getString("service_id"));
			// innerList.add(rs.getString("actual_hours"));
			//
			// RosterMp.put(rs.getString("emp_id")+"_"+rs.getString("_date"),innerList);
			//
			//
			// }

			// rosterOrg.put(getF_org(),RosterMp);

			// if(strD1!=null){
			// pst =
			// con.prepareStatement("Select count(*) as attendance_count,ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date from attendance_details "
			// +
			// " ad,employee_official_details eod where ad.emp_id=eod.emp_id and org_id=? "
			// +
			// "and to_date(in_out_timestamp::text,'yyyy-MM-dd') between  ?  and ?"
			// +
			// " group by ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd')");
			// pst.setInt(1,uF.parseToInt(getF_org()));
			// pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			// pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));

			// pst =
			// con.prepareStatement("Select count(*) as attendance_count,ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date from attendance_details "
			// +
			// " ad,employee_official_details eod where ad.emp_id=eod.emp_id and org_id=? and wlocation_id=? "
			// +
			// "and to_date(in_out_timestamp::text,'yyyy-MM-dd') between  ?  and ?"
			// +
			// " group by ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd')");
			// pst.setInt(1,uF.parseToInt(getF_org()));
			// pst.setInt(2,uF.parseToInt(getwLocation()));
			// pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			// pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			//
			// // }else{
			// // pst =
			// con.prepareStatement("Select count(*) as attendance_count,ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date from attendance_details "
			// +
			// //
			// " ad,employee_official_details eod where ad.emp_id=eod.emp_id and org_id=? "
			// +
			// //
			// " group by ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd')");
			// // pst.setInt(1,uF.parseToInt(getF_org()));
			// //
			// // }
			// rs = pst.executeQuery();
			// Map<String,String> empAttendanceMp=new HashMap<String,String>();
			// while (rs.next()) {
			// empAttendanceMp.put(rs.getString("emp_id")+"_"+rs.getString("attendance_date"),rs.getString("attendance_count"));
			//
			// }
			// attendanceOrg.put(getF_org(), empAttendanceMp);
			// }
			// =============================================================================================================

			// Set<String> set=empOrgMp.keySet();
			// Iterator<String> it=set.iterator();
			// String oldOrg=null;
			// Map<String,List<String>> RosterMp=new
			// HashMap<String,List<String>>();
			// Map<String,String> empAttendanceMp=new HashMap<String,String>();
			// String[] payCycle=new String[3];
			List<String> dbDateList = getDateList(strD1, DATE_FORMAT, strD2,
					DATE_FORMAT, DBDATE);
			for (String emp_per_id : empList) {
				// String emp_per_id=it.next();
				java.util.Date joininigDate = uF.getDateFormatUtil(empjoiningDateMp.get(emp_per_id), DBDATE);
				java.util.Date empEndDate = uF.getDateFormatUtil(empEndMp.get(emp_per_id), DBDATE);
				String empcode = empCodeMp.get(emp_per_id);
				int servic_id = uF.parseToInt(empServiceMp.get(emp_per_id));
				// String org=empOrgMp.get(emp_per_id);
				// double actual_hours=0;

				// if(oldOrg==null || !org.equals(oldOrg) ){
				// System.out.println("org====>"+org);
				// RosterMp=rosterOrg.get(org);
				// empAttendanceMp=attendanceOrg.get(org);
				// payCycle=orgPaycycleMp.get(org);
				// dbDateList=getDateList(strD1, DATE_FORMAT, strD1,
				// DATE_FORMAT,DBDATE);
				// }
				//
				// if(payCycle[0]==null || payCycle[1]==null){
				// continue;
				// }

				for (String dbDate : dbDateList) {

					if (joininigDate != null && joininigDate.compareTo(uF.getDateFormatUtil(dbDate, DBDATE)) > 0) {
						continue;
					}

					if (empEndDate != null && empEndDate.compareTo(uF.getDateFormatUtil(dbDate, DBDATE)) < 0) {
						continue;
					}
					if (holidayList.contains(dbDate)) {
						continue;
					}

					String day = uF.getDateFormat(dbDate, DBDATE, "EEEE");
					// System.out.println("strDate===>"+strDate);

					if (weeklyoff1 != null && weeklyoff1.equalsIgnoreCase(day)) {
						if (weeklyoff1type != null && weeklyoff1type.equalsIgnoreCase("HD")) {
							// dblLeaveDed=.5;
						} else {
							// dblLeaveDed=1;
							continue;
						}
						// flag=true;
					}
					if (weeklyoff2 != null && weeklyoff2.equalsIgnoreCase(day)) {
						if (weeklyoff2type != null && weeklyoff2type.equalsIgnoreCase("HD")) {
							// dblLeaveDed=.5;
						} else {
							// dblLeaveDed=1;
							continue;
						}
						// flag=true;
					}
					if (weeklyoff3 != null && weeklyoff3.equalsIgnoreCase(day)) {
						if (weeklyoff3type != null && weeklyoff3type.equalsIgnoreCase("HD")) {
							// dblLeaveDed=.5;
						} else {
							// dblLeaveDed=1;
							continue;
						}
						// flag=true;
					}

					// ====================Insert Roster if not
					// Exist========================

					if (RosterMp.get(emp_per_id + "_" + dbDate) != null) {
						// System.out.println("Roster Detail Already Exists======>");
					} else {

						// String workInTime=null;
						// String workOuttime=null;
						// List<String>
						// workTime=wLocationMp.get(empWlocationMp.get(emp_per_id));
						// if(workTime==null){
						// alReport.add("This <strong>"+empcode+"</strong> 's Work Location is not found.");
						//
						// break;
						// }
						// if(workTime!=null){
						//
						// workInTime=workTime.get(0);
						// workOuttime=workTime.get(1);
						// }

						String workInTime = wlocationData.get(0);
						String workOuttime = wlocationData.get(1);

						// String weeklyoff1=workTime.get(2);
						// String weeklyoff2=workTime.get(3);
						// String weeklyoff3=workTime.get(4);
						//
						// String weeklyoff1type=workTime.get(5);
						// String weeklyoff2type=workTime.get(6);
						// String weeklyoff3type=workTime.get(7);

						
							// pst=con.prepareStatement("Insert into roster_details (emp_id,"
							// +
							// "_date," +
							// "_from," +
							// "_to," +
							// "isapproved," +
							// "user_id," +
							// "service_id," +
							// "actual_hours," +
							// "attended," +
							// "is_lunch_ded," +
							// "shift_id," +
							// "entry_date)" +
							// "values(?,?,?,?,?,?,?,?,?,?,?,?)");

							prepareStatement1.setInt(1,uF.parseToInt(emp_per_id));
							prepareStatement1.setDate(2,uF.getDateFormat(dbDate, DBDATE));
							prepareStatement1.setTime(3, new java.sql.Time(uF.getDateFormat(workInTime, DBTIME).getTime()));
							prepareStatement1.setTime(4, new java.sql.Time(uF.getDateFormat(workOuttime, DBTIME).getTime()));
							prepareStatement1.setBoolean(5, false);
							prepareStatement1.setInt(6, 310);
							prepareStatement1.setInt(7, servic_id); // service id
							prepareStatement1.setDouble(8,uF.parseToDouble(uF.getTimeDiffInHoursMins(uF.getDateFormat(workInTime,DBTIME).getTime(),uF.getDateFormat(workOuttime,DBTIME).getTime())));
							prepareStatement1.setInt(9, 0);
							prepareStatement1.setBoolean(10, false);
							prepareStatement1.setInt(11, 1);
							prepareStatement1.setDate(12, new java.sql.Date(System.currentTimeMillis()));
							prepareStatement1.setInt(13, 1);
							prepareStatement1.execute();
							prepareStatement1.clearParameters();
							// System.out.println("Successfull Roster ");
							List<String> innerList1 = new ArrayList<String>();
							innerList1.add("0");
							innerList1.add(workInTime);
							innerList1.add(workOuttime);
							innerList1.add(servic_id + "");
							innerList1.add(uF.getTimeDiffInHoursMins(uF.getDateFormat(workInTime, DBTIME).getTime(),uF.getDateFormat(workOuttime, DBTIME).getTime()));
							RosterMp.put(emp_per_id + "_" + dbDate, innerList1);
						

					}
					// =======================Insert Roster
					// Finish===========================

					int i = uF.parseToInt(empAttendanceMp.get(emp_per_id + "_"+ dbDate));
					if (i >= 2) {

					} else {

						// String
						// leaveType=leaveEmpMp.get(emp_per_id+"_"+dbDate);

						// if(leaveType!=null &&
						// leaveType.equalsIgnoreCase("FULL DAY")){
						// continue;
						// }

						String _fromTime = null;
						String _toTime = null;

						List<String> rosterList = RosterMp.get(emp_per_id + "_"+ dbDate);
						if (rosterList != null) {

							_fromTime = rosterList.get(1);
							// System.out.println("From time is true flag"+
							// _fromTime);
							_toTime = rosterList.get(2);
							servic_id = uF.parseToInt(rosterList.get(3));
							// actual_hours=uF.parseToDouble(rosterList.get(4));
						}

						if (i == 0) {

							insertINEntry(con, prepareStatement, rs, uF,uF.parseToInt(emp_per_id), servic_id,dbDate, _fromTime, _fromTime, DBDATE,timeFormat);
							// IN CODING
						}
						// OUT CODING

						// if(leaveType!=null &&
						// leaveType.equalsIgnoreCase("HALF DAY")){
						//
						// java.util.Date myDate =
						// parser.parse(dbDate+" "+_fromTime.toString());
						// Calendar cal =Calendar.getInstance();
						// cal.setTime(myDate);
						// cal.add(Calendar.HOUR_OF_DAY,4); // this will add two
						// hours
						// myDate = cal.getTime();
						// importAttendance.insertOUTEntry(con,pst,rs,uF,
						// uF.parseToInt(emp_per_id), servic_id, dbDate,
						// parser1.format(myDate),
						// _toTime,_fromTime,DBDATE,timeFormat);
						// }else{
						insertOUTEntry(con, prepareStatement, rs, uF,uF.parseToInt(emp_per_id), servic_id, dbDate,_toTime, _toTime, _fromTime, DBDATE, timeFormat);
						// }

						// alReport.add(empcode);
					}
					// ===================end import
					// Attendance===================

				}
				alReport.add("<li class=\"msg_success\" style=\"margin:0px\">"+ empcode + " Attendance Inserted Successfully.</li>");
				// oldOrg=org;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(prepareStatement1);
			db.closeStatements(prepareStatement);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		session.setAttribute("alReport", alReport);
	}

	private void ImportFormatCodeR0007(File path) {
		
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement prepareStatement = null;
		PreparedStatement prepareStatement1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		List<String> alReport = new ArrayList<String>();
		String dateFormat = "dd.MM.yyyy";
		String timeFormat = "HH:mm:ss";
		// Map<String,String> hmEmpLevel = CF.getEmpLevelMap();
		SimpleDateFormat parser1 = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// CF.getEmpLevelMap();
		String[] strPayCycleDates = null;
		if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
			strPayCycleDates = CF.getPrevPayCycle(con, strPayCycleDates[1],CF.getStrTimeZone(), CF);
		}
		String strD1 = strPayCycleDates[0];
		String strD2 = strPayCycleDates[1];
		FileInputStream fis = null;
		try {
			con = db.makeConnection(con);
			// ssd
			prepareStatement = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
							+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
			prepareStatement1 = con.prepareStatement("Insert into roster_details (emp_id,_date,_from,_to,isapproved,user_id,service_id,actual_hours,"
							+ "attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id)values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");

			// =============================================================================================
			fis = new FileInputStream(path);
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			//System.out.println("Start Reading Excelsheet.... ");
			XSSFSheet attendanceSheet = workbook.getSheetAt(0);

			List<List<String>> outerList = new ArrayList<List<String>>();

			Iterator rows = attendanceSheet.rowIterator();
			while (rows.hasNext()) {

				XSSFRow row = (XSSFRow) rows.next();
				Iterator cells = row.cellIterator();
				List<String> cellList = new ArrayList<String>();
				while (cells.hasNext()) {
					cellList.add(cells.next().toString().trim());
				}
				outerList.add(cellList);

			}
			// =============================================================================================

			Map<String, String> empMp = new HashMap<String, String>();
			// Map<String,String> empOrgMp=new LinkedHashMap<String,String>();
			Map<String, String> empjoiningDateMp = new HashMap<String, String>();
			Map<String, String> empEndMp = new HashMap<String, String>();
			Map<String, String> empServiceMp = new HashMap<String, String>();

			Map<String, String> empCodeMp = new HashMap<String, String>();
			List<String> empList = new ArrayList<String>();

			pst = con
					.prepareStatement("select emp_per_id,empcode,org_id,service_id,wlocation_id,joining_date,employment_end_date from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id and org_id=? and wlocation_id=?  order by org_id");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));

			rs = pst.executeQuery();
			while (rs.next()) {
				empList.add(rs.getString("emp_per_id"));
				empMp.put(rs.getString("empcode"), rs.getString("emp_per_id"));

				String strEmpServiceId = "0";
				if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
					String[] str = rs.getString("service_id").split(",");
					for (int z = 0; str != null && z < str.length; z++) {
						if (uF.parseToInt(str[z]) > 0) {
							strEmpServiceId = str[z];
						}
					}
				}				
				empServiceMp.put(rs.getString("emp_per_id"),strEmpServiceId);
				
				empEndMp.put(rs.getString("emp_per_id"),rs.getString("employment_end_date"));
				empjoiningDateMp.put(rs.getString("emp_per_id"),rs.getString("joining_date"));
				empCodeMp.put(rs.getString("emp_per_id"),rs.getString("empcode"));

			}
			rs.close();
			pst.close();

			Map<String, String> hmEmpLevelMap = new HashMap<String, String>();

			pst = con.prepareStatement("select * from level_details ld right join (select * from designation_details dd right join (select *, gd.designation_id as designationid from employee_official_details eod, grades_details gd where gd.grade_id=eod.grade_id and org_id=? and wlocation_id=?) a on a.designationid=dd.designation_id) a on a.level_id=ld.level_id");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmEmpLevelMap.put(rs.getString("emp_id"),rs.getString("level_id"));
			}
			rs.close();
			pst.close();

			List<String> holidayList = new ArrayList<String>();
			pst = con.prepareStatement("select _date from holidays where org_id=? and wlocation_id=? and _date between ? and ? and (is_optional_holiday is null or is_optional_holiday=false)");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				holidayList.add(rs.getString("_date"));
			}
			rs.close();
			pst.close();
			// System.out.println("pst====>>"+pst);
			// System.out.println("holidayList==>"+holidayList);
			Map<String, String> leaveMp = new HashMap<String, String>();

			pst = con.prepareStatement("select leave_type_id,leave_type_name from leave_type where org_id=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			rs = pst.executeQuery();
			while (rs.next()) {
				leaveMp.put(rs.getString("leave_type_name"),rs.getString("leave_type_id"));

			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select wlocation_start_time,wlocation_end_time,wlocation_weeklyoff1,wlocation_weeklyoff2,wlocation_weeklyoff3,wlocation_weeklyofftype1,wlocation_weeklyofftype2,wlocation_weeklyofftype3 from work_location_info where  org_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			rs = pst.executeQuery();
			String weeklyoff1 = null;
			String weeklyoff2 = null;
			String weeklyoff3 = null;

			String weeklyoff1type = null;
			String weeklyoff2type = null;
			String weeklyoff3type = null;
			String workInTime = null;
			String workOuttime = null;

			// Map<String,List<String>> wLocationMp=new
			// HashMap<String,List<String>>();
			// List<String> wlocationData=new ArrayList<String>();
			while (rs.next()) {
				// List<String> innerList=new ArrayList<String>();
				workInTime = rs.getString("wlocation_start_time");
				workOuttime = rs.getString("wlocation_end_time");
				// wLocationMp.put(rs.getString("wlocation_id"),innerList);
				weeklyoff1 = rs.getString("wlocation_weeklyoff1");
				weeklyoff2 = rs.getString("wlocation_weeklyoff2");
				weeklyoff3 = rs.getString("wlocation_weeklyoff3");

				weeklyoff1type = rs.getString("wlocation_weeklyofftype1");
				weeklyoff2type = rs.getString("wlocation_weeklyofftype2");
				weeklyoff3type = rs.getString("wlocation_weeklyofftype3");

			}
			rs.close();
			pst.close();

			Map<String, List<String>> RosterMp = new HashMap<String, List<String>>();

			// if(strDate1!=null){
			pst = con.prepareStatement("Select roster_id,rd.emp_id, _date,_from,_to,rd.service_id,actual_hours from roster_details rd,employee_official_details eod where rd.emp_id=eod.emp_id and org_id=? and wlocation_id=? and _date between ? and ?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			// }else{
			// pst =
			// con.prepareStatement("Select roster_id,rd.emp_id, _date,_from,_to,rd.service_id,actual_hours from roster_details rd,employee_official_details eod where rd.emp_id=eod.emp_id and org_id=?");
			// pst.setInt(1,uF.parseToInt(orgList.get(i)));
			// }

			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("roster_id"));
				innerList.add(rs.getString("_from"));
				innerList.add(rs.getString("_to"));
				innerList.add(rs.getString("service_id"));
				innerList.add(rs.getString("actual_hours"));

				RosterMp.put(rs.getString("emp_id") + "_" + rs.getString("_date"),innerList);

			}
			rs.close();
			pst.close();

			// rosterOrg.put(orgList.get(i),RosterMp);

			// if(strDate1!=null){
			pst = con.prepareStatement("Select count(*) as attendance_count,ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date from attendance_details "
							+ " ad,employee_official_details eod where ad.emp_id=eod.emp_id and org_id=? and wlocation_id=? "
							+ "and to_date(in_out_timestamp::text,'yyyy-MM-dd') between  ?  and ?"
							+ " group by ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd')");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getwLocation()));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			// }else{
			// pst =
			// con.prepareStatement("Select count(*) as attendance_count,ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date from attendance_details "
			// +
			// " ad,employee_official_details eod where ad.emp_id=eod.emp_id and org_id=? "
			// +
			// " group by ad.emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd')");
			// pst.setInt(1,uF.parseToInt(orgList.get(i)));
			//
			// }
			rs = pst.executeQuery();
			Map<String, String> empAttendanceMp = new HashMap<String, String>();
			while (rs.next()) {
				empAttendanceMp.put(rs.getString("emp_id") + "_"+ rs.getString("attendance_date"),rs.getString("attendance_count"));

			}
			rs.close();
			pst.close();
			// ========================================================================================================================

			// String actDate=null;
			// ManagerLeaveApproval leaveApproval=new ManagerLeaveApproval();
			// EmployeeLeaveEntry ele=new EmployeeLeaveEntry();
			Map<String, String> leaveEmpMp = new HashMap<String, String>();
			for (int i = 1; i < outerList.size(); i++) {

				List<String> innerList = outerList.get(i);

				String empCode = innerList.get(0).trim();
				if (empCode.contains(".")) {
					empCode = empCode.substring(0, empCode.indexOf("."));

				}
				int empId = uF.parseToInt(empMp.get(empCode));
				if (empId == 0) {
					continue;
				}

				String strStartDate = innerList.get(1);
				strStartDate = uF.getDateFormat(strStartDate, dateFormat,DATE_FORMAT);
				// if(i==1){
				// actDate=strStartDate;
				// }

				String strEndDate = innerList.get(2);
				// System.out.println("innerList.get(3)===>"+innerList.get(3));
				// System.out.println("leaveMp===>"+leaveMp);
				String leaveId = leaveMp.get(innerList.get(3).trim());
				String leaveType = innerList.get(4);
				// System.out.println("leaveId==="+leaveId);

				strEndDate = uF.getDateFormat(strEndDate, dateFormat,DATE_FORMAT);
				// String orgId=empOrgMp.get(empMp.get(innerList.get(0)));
				// OrgDateMp.put(orgId, strStartDate);
				// System.out.println("strStartDate===>"+strStartDate);
				getMap(leaveEmpMp, strStartDate, strEndDate, empId, leaveType);

				pst = con.prepareStatement("select * from emp_leave_entry where  emp_id=? and leave_from=? and leave_to=?");
				pst.setInt(1, empId);
				pst.setDate(2, uF.getDateFormat(strStartDate, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strEndDate, DATE_FORMAT));
				rs = pst.executeQuery();
				if (rs.next()) {
					continue;
				}
				rs.close();
				pst.close();
				// ele.setEmpId(empId+"");
				// ele.setTypeOfLeave(leaveId);
				if (leaveType.equalsIgnoreCase("FULL DAY")) {
					insertLeaveEntry(con, pst, rs, uF, empId,uF.parseToInt(leaveId), strStartDate, strEndDate,false,uF.parseToInt(hmEmpLevelMap.get(empId + "")),uF.parseToInt(getF_org()),uF.parseToInt(getwLocation()));
					// ele.setIsHalfDay(false);
				} else {
					insertLeaveEntry(con, pst, rs, uF, empId,uF.parseToInt(leaveId), strStartDate, strEndDate,true, uF.parseToInt(hmEmpLevelMap.get(empId + "")),uF.parseToInt(getF_org()),uF.parseToInt(getwLocation()));
					// ele.setIsHalfDay(true);
				}

				// ele.setLeaveFromTo(strStartDate);
				// ele.setLeaveToDate(strEndDate);
				// ele.setEntrydate(strStartDate);
				// ele.setIsCompensate("true");
				// ele.CF=CF;
				// ele.session=session;
				// ele.setServletRequest(request);
				// insertLeaveEntry(empId,uF.parseToInt(leaveId),strStartDate,strEndDate);

				// String leave_id=null;
				// pst =
				// con.prepareStatement("select max(leave_id)as leave_id from emp_leave_entry");
				// rs=pst.executeQuery();
				// while(rs.next()){
				// leave_id=rs.getString("leave_id");
				// }

				// if(uF.parseToInt(leave_id)!=0){
				// pst =
				// con.prepareStatement("select leave_id from leave_application_register where leave_id=(select max(leave_id)as leave_id from emp_leave_entry)");
				// pst.setInt(1,uF.parseToInt(leave_id));
				// rs=pst.executeQuery();
				//
				// if(rs.next()){
				// leave_id=rs.getString("leave_id");

				// }else{
				//
				// String leave_id=null;
				// pst =
				// con.prepareStatement("select max(leave_id)as leave_id from emp_leave_entry");
				// rs=pst.executeQuery();
				// while(rs.next()){
				// leave_id=rs.getString("leave_id");
				// }
				//
				// leaveApproval.setServletRequest(request);
				// leaveApproval.setLeaveId(leave_id);
				// leaveApproval.setTypeOfLeave(leaveId);
				// leaveApproval.setEmpId(empId+"");
				// leaveApproval.setIsapproved(1);
				// leaveApproval.setApprovalFromTo(strStartDate);
				// leaveApproval.setApprovalToDate(strEndDate);
				// leaveApproval.insertLeaveBalance(con,pst,rs,uF,hmEmpLevel,CF);
				// }
				// }
			}
			// System.out.println("leaveEmpMp==="+leaveEmpMp);
			// ======================================================================================================
			// Map<String,Map<String,List<String>>> rosterOrg=new
			// HashMap<String,Map<String,List<String>>>();
			// Map<String,Map<String,String>> attendanceOrg=new
			// HashMap<String,Map<String,String>>();
			// xzxz
			// Map<String,String[]> orgPaycycleMp=new
			// HashMap<String,String[]>();
			// for(int i=0;i<orgList.size();i++){
			// String strDate=OrgDateMp.get(orgList.get(i));
			// if(strDate==null){
			// strDate=actDate;
			// }
			// String strDate1=null;
			// String strEndDate1=null;

			// if(strDate==null){
			// String[] strPayCycleDate =
			// CF.getCurrentPayCycle(CF.getStrTimeZone(), CF);
			// strDate1=strPayCycleDate[0];
			// strEndDate1=strPayCycleDate[1];
			// orgPaycycleMp.put(orgList.get(i), strPayCycleDate);
			//
			// }else{
			// String[] strPayCycleDate =
			// CF.getCurrentPayCycle(CF.getStrTimeZone(),
			// uF.getDateFormatUtil(strDate, DATE_FORMAT), CF);
			// strDate1=strPayCycleDate[0];
			// strEndDate1=strPayCycleDate[1];
			// orgPaycycleMp.put(orgList.get(i), strPayCycleDate);
			// }

			// attendanceOrg.put(orgList.get(i), empAttendanceMp);
			// }
			// =============================================================================================================

			// Set<String> set=empOrgMp.keySet();
			// Iterator<String> it=set.iterator();
			// String oldOrg=null;
			// Map<String,List<String>> RosterMp=new
			// HashMap<String,List<String>>();
			// Map<String,String> empAttendanceMp=new HashMap<String,String>();
			// String[] payCycle=new String[3];
			List<String> dbDateList = getDateList(strD1, DATE_FORMAT, strD2,DATE_FORMAT, DBDATE);
			for (String emp_per_id : empList) {
				java.util.Date joininigDate = uF.getDateFormatUtil(empjoiningDateMp.get(emp_per_id), DBDATE);
				java.util.Date empEndDate = uF.getDateFormatUtil(empEndMp.get(emp_per_id), DBDATE);
				// if(d1.)
				// String emp_per_id=it.next();
				String empcode = empCodeMp.get(emp_per_id);
				int servic_id = uF.parseToInt(empServiceMp.get(emp_per_id));
				// String org=empOrgMp.get(emp_per_id);
				// double actual_hours=0;

				// if(oldOrg==null || !org.equals(oldOrg) ){
				// System.out.println("org====>"+org);
				// RosterMp=rosterOrg.get(org);
				// empAttendanceMp=attendanceOrg.get(org);
				// payCycle=orgPaycycleMp.get(org);
				// dbDateList=getDateList(payCycle[0], DATE_FORMAT, payCycle[1],
				// DATE_FORMAT,DBDATE);
				// }
				//
				// if(payCycle[0]==null || payCycle[1]==null){
				// continue;
				// }

				for (String dbDate : dbDateList) {

					if (joininigDate != null && joininigDate.compareTo(uF.getDateFormatUtil(dbDate, DBDATE)) > 0) {
						continue;
					}

					if (empEndDate != null && empEndDate.compareTo(uF.getDateFormatUtil(dbDate, DBDATE)) < 0) {
						continue;
					}
					// System.out.println("dbDate===="+dbDate);
					if (holidayList.contains(dbDate)) {
						continue;
					}

					String day = uF.getDateFormat(dbDate, DBDATE, "EEEE");
					// System.out.println("strDate===>"+strDate);

					if (weeklyoff1 != null && weeklyoff1.equalsIgnoreCase(day)) {
						if (weeklyoff1type != null && weeklyoff1type.equalsIgnoreCase("HD")) {
							// dblLeaveDed=.5;
						} else {
							// dblLeaveDed=1;
							continue;
						}
						// flag=true;
					}
					if (weeklyoff2 != null && weeklyoff2.equalsIgnoreCase(day)) {
						if (weeklyoff2type != null && weeklyoff2type.equalsIgnoreCase("HD")) {
							// dblLeaveDed=.5;
						} else {
							// dblLeaveDed=1;
							continue;
						}
						// flag=true;
					}
					if (weeklyoff3 != null && weeklyoff3.equalsIgnoreCase(day)) {
						if (weeklyoff3type != null && weeklyoff3type.equalsIgnoreCase("HD")) {
							// dblLeaveDed=.5;
						} else {
							// dblLeaveDed=1;
							continue;
						}
						// flag=true;
					}

					// ====================Insert Roster if not
					// Exist========================

					if (RosterMp.get(emp_per_id + "_" + dbDate) != null) {
						// System.out.println("Roster Detail Already Exists======>");
					} else {

						// List<String>
						// wlocationData=wLocationMp.get(empWlocationMp.get(emp_per_id+""));
						// if(workTime==null){
						// alReport.add("This <strong>"+empcode+"</strong> 's Work Location is not found.");
						//
						// break;
						// }
						// if(workTime!=null){

						// workInTime=wlocationData.get(0);
						// workOuttime=wlocationData.get(1);
						// }
//						try {
							// pst=con.prepareStatement("Insert into roster_details (emp_id,"
							// +
							// "_date," +
							// "_from," +
							// "_to," +
							// "isapproved," +
							// "user_id," +
							// "service_id," +
							// "actual_hours," +
							// "attended," +
							// "is_lunch_ded," +
							// "shift_id," +
							// "entry_date)" +
							// "values(?,?,?,?,?,?,?,?,?,?,?,?)");

							prepareStatement1.setInt(1,uF.parseToInt(emp_per_id));
							prepareStatement1.setDate(2,uF.getDateFormat(dbDate, DBDATE));
							prepareStatement1.setTime(3, new java.sql.Time(uF.getDateFormat(workInTime, DBTIME).getTime()));
							prepareStatement1.setTime(4, new java.sql.Time(uF.getDateFormat(workOuttime, DBTIME).getTime()));
							prepareStatement1.setBoolean(5, false);
							prepareStatement1.setInt(6, 310);
							prepareStatement1.setInt(7, servic_id); // service
																	// id
							prepareStatement1.setDouble(8,uF.parseToDouble(uF.getTimeDiffInHoursMins(uF.getDateFormat(workInTime,DBTIME).getTime(),uF.getDateFormat(workOuttime,DBTIME).getTime())));
							prepareStatement1.setInt(9, 0);
							prepareStatement1.setBoolean(10, false);
							prepareStatement1.setInt(11, 1);
							prepareStatement1.setDate(12, new java.sql.Date(System.currentTimeMillis()));
							prepareStatement1.setInt(13, 1);
							prepareStatement1.execute();
							prepareStatement1.clearParameters();
							
							// System.out.println("Successfull Roster ");
							List<String> innerList1 = new ArrayList<String>();
							innerList1.add("0");
							innerList1.add(workInTime);
							innerList1.add(workOuttime);
							innerList1.add(servic_id + "");
							innerList1.add(uF.getTimeDiffInHoursMins(uF.getDateFormat(workInTime, DBTIME).getTime(),uF.getDateFormat(workOuttime, DBTIME).getTime()));
							RosterMp.put(emp_per_id + "_" + dbDate, innerList1);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}

					}
					// =======================Insert Roster
					// Finish===========================

					int i = uF.parseToInt(empAttendanceMp.get(emp_per_id + "_"+ dbDate));
					if (i >= 2) {

					} else {
						// System.out.println("===>>>"+emp_per_id+"_"+dbDate);
						String leaveType = leaveEmpMp.get(emp_per_id + "_"+ dbDate);

						if (leaveType != null && leaveType.equalsIgnoreCase("FULL DAY")) {
							continue;
						}

						String _fromTime = null;
						String _toTime = null;

						List<String> rosterList = RosterMp.get(emp_per_id + "_"+ dbDate);
						if (rosterList != null) {

							_fromTime = rosterList.get(1);
							// System.out.println("From time is true flag"+
							// _fromTime);
							_toTime = rosterList.get(2);
							servic_id = uF.parseToInt(rosterList.get(3));
							// actual_hours=uF.parseToDouble(rosterList.get(4));
						}

						if (i == 0) {
							// System.out.println("===>>>"+emp_per_id+"_"+dbDate);
							insertINEntry(con, prepareStatement, rs, uF,uF.parseToInt(emp_per_id), servic_id,dbDate, _fromTime, _fromTime, DBDATE,timeFormat);
							// IN CODING
						}
						// OUT CODING

						if (leaveType != null && leaveType.equalsIgnoreCase("HALF DAY")) {

							java.util.Date myDate = parser.parse(dbDate + " "+ _fromTime.toString());
							Calendar cal = Calendar.getInstance();
							cal.setTime(myDate);
							cal.add(Calendar.HOUR_OF_DAY, 4); // this will add
																// two hours
							myDate = cal.getTime();
							// System.out.println("===>>>"+emp_per_id+"_"+dbDate);
							insertOUTEntry(con, prepareStatement, rs, uF,uF.parseToInt(emp_per_id), servic_id,dbDate, parser1.format(myDate), _toTime,_fromTime, DBDATE, timeFormat);
						} else {
							insertOUTEntry(con, prepareStatement, rs, uF,uF.parseToInt(emp_per_id), servic_id,dbDate, _toTime, _toTime, _fromTime,DBDATE, timeFormat);
						}

						// sbMessage.append("<li class=\"msg_error\" style=\"margin:0px\">"
						// + cell2.toString() + " " + cell3.toString()
						// +
						// " already exists, please check the Employee code of this employee and try again.</li>");

						// alReport.add("<li class=\"msg_error\" style=\"margin:0px\">"
						// + empcode + " Attendance already exists.</li>");

					}
					// ===================end import
					// Attendance===================

				}
				alReport.add("<li class=\"msg_success\" style=\"margin:0px\">"+ empcode + " Attendance Inserted Successfully.</li>");
				// oldOrg=org;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(prepareStatement1);
			db.closeStatements(prepareStatement);
			db.closeStatements(pst);
			db.closeConnection(con);
			try {
				fis.close();
			} catch (Exception ex) {

			}
			System.gc();
		}
		session.setAttribute("alReport", alReport);
	}

	public void getMap(Map<String, String> mp, String startDate,
			String endDate, int empId, String leaveType) {
		SimpleDateFormat sdf = new SimpleDateFormat(DBDATE);

		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DATE,uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "dd")));
		cal.set(Calendar.MONTH,uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "MM")) - 1);
		cal.set(Calendar.YEAR,uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "yyyy")));

		Calendar cal2 = GregorianCalendar.getInstance();
		cal2.set(Calendar.DATE,uF.parseToInt(uF.getDateFormat(endDate, DATE_FORMAT, "dd")));
		cal2.set(Calendar.MONTH,uF.parseToInt(uF.getDateFormat(endDate, DATE_FORMAT, "MM")) - 1);
		cal2.set(Calendar.YEAR,uF.parseToInt(uF.getDateFormat(endDate, DATE_FORMAT, "yyyy")));

		UtilityFunctions uF = new UtilityFunctions();
		int diff = uF.parseToInt(uF.dateDifference(startDate, DATE_FORMAT,endDate, DATE_FORMAT,CF.getStrTimeZone()));
		while (diff > 0) {
			diff--;
			mp.put(empId + "_" + sdf.format(cal.getTime()), leaveType);
			cal.add(Calendar.DATE, 1);

		}
	}

	public List<String> getDateList(String startDate, String startDateFormat,
			String endDate, String endDateFormat, String outputFormat) {
		List<String> dateList = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat(outputFormat);

		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(startDate,startDateFormat, "dd")));
		cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate,startDateFormat, "MM")) - 1);
		cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startDate,startDateFormat, "yyyy")));

		Calendar cal2 = GregorianCalendar.getInstance();
		cal2.set(Calendar.DATE,uF.parseToInt(uF.getDateFormat(endDate, endDateFormat, "dd")));
		cal2.set(Calendar.MONTH,uF.parseToInt(uF.getDateFormat(endDate, endDateFormat, "MM")) - 1);
		cal2.set(Calendar.YEAR,uF.parseToInt(uF.getDateFormat(endDate, endDateFormat, "yyyy")));

		UtilityFunctions uF = new UtilityFunctions();
		int diff = uF.parseToInt(uF.dateDifference(startDate, startDateFormat,endDate, endDateFormat,CF.getStrTimeZone()));
		while (diff > 0) {
			diff--;
			dateList.add(sdf.format(cal.getTime()));
			cal.add(Calendar.DATE, 1);

		}

		return dateList;
	}

	public File getFileUpload3() {
		return fileUpload3;
	}

	public void setFileUpload3(File fileUpload3) {
		this.fileUpload3 = fileUpload3;
	}

	private void ImportFormatCodeA0002(File path) {
		
		//System.out.println("format2Attendance====");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		List<String> alReport = new ArrayList<String>();
		// ImportAttendance importAttendance=new ImportAttendance();

		String dateFormat = "dd/MM/yyyy";
		String timeFormat = "HH:mm:ss";
		FileInputStream fis = null;
		List<String> alErrorList = new ArrayList<String>();
		try {
			//System.out.println("in try block====");
			con = db.makeConnection(con);
			con.setAutoCommit(false);

			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);

			fis = new FileInputStream(path);
			
			HSSFWorkbook workbook = new HSSFWorkbook(fis);
			
			HSSFSheet attendanceSheet = workbook.getSheetAt(0);
			
			List<String> dateList = new ArrayList<String>();
			List<List<String>> outerList = new ArrayList<List<String>>();
			SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			SimpleDateFormat parser1 = new SimpleDateFormat("HH:mm:ss");

			Iterator rows = attendanceSheet.rowIterator();
			int l = 0;
			int x11 =1;
			while (rows.hasNext()) {
				
				HSSFRow row = (HSSFRow) rows.next();
				Iterator cells = row.cellIterator();
				
				if (l == 0) {
					while (cells.hasNext()) {
//						String cell = cells.next().toString();
						HSSFCell cell = (HSSFCell) cells.next();
						dateList.add(uF.getCellString(cell, workbook, dateFormat, timeFormat));
					}
					l++;
					continue;
				}
				
				List<String> cellList = new ArrayList<String>();
				int x = 0;
				while (cells.hasNext()) {
//					cellList.add(cells.next().toString());
					HSSFCell cell = (HSSFCell) cells.next();
					cellList.add(uF.getCellString(cell, workbook, dateFormat, timeFormat));
				}
				outerList.add(cellList);
				
			}
			
//			System.out.println("outerList======>"+outerList.toString());
			
			boolean flag = false;
			if (dateList.size() < 3 || outerList.size() == 0) {
				alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">No Data Available in Sheet.</li>");
				flag = false;
			} else {
				// PreparedStatement prepareStatement =
				// con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
				// +
				// "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
				String strDate = null;
				String strEndDate = null;
				if (dateList != null) {
					strDate = dateList.get(2);
					strEndDate = dateList.get(dateList.size() - 1);
				}

				pst = con.prepareStatement("select wlocation_start_time,wlocation_end_time,wlocation_id from work_location_info");
				rs = pst.executeQuery();
				Map<String, List<String>> wLocationMp = new HashMap<String, List<String>>();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("wlocation_start_time"));
					innerList.add(rs.getString("wlocation_end_time"));
					wLocationMp.put(rs.getString("wlocation_id"), innerList);
				}
				rs.close();
				pst.close();

				pst = con.prepareStatement("select leave_type_id,is_compensatory,leave_type_code,org_id from leave_type");
				rs = pst.executeQuery();
				Map<String, Map<String, List<String>>> leaveTypeOrgMp1 = new HashMap<String, Map<String, List<String>>>();
				while (rs.next()) {
					Map<String, List<String>> leaveTypeOrgMp = leaveTypeOrgMp1.get(rs.getString("org_id"));
					if (leaveTypeOrgMp == null) leaveTypeOrgMp = new HashMap<String, List<String>>();

					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("leave_type_id"));
					innerList.add(rs.getString("is_compensatory"));

					leaveTypeOrgMp.put(rs.getString("leave_type_code"), innerList);

					leaveTypeOrgMp1.put(rs.getString("org_id"), leaveTypeOrgMp);
				}
				rs.close();
				pst.close();
				
				//System.out.println("outerList======>"+outerList.toString());
				for (int k = 0; k < outerList.size(); k++) {
					//System.out.println("in the outer loop ");
					
					List<String> innerList = outerList.get(k);
					//System.out.println("innerList=="+innerList);
					
					if(innerList != null && innerList.size() > 0){
						//System.out.println("in the inner loop ");
						String empcode = innerList.get(1);
//						String empcode = getStringValue(innerList.get(1));
//						System.out.println("empcode=="+empcode);
						
						/*if (empcode.contains(".")) {
							empcode = empcode.substring(0, empcode.indexOf("."));
						}*/
	
						int emp_per_id = 0;
	
						pst = con.prepareStatement("select emp_per_id,empcode,org_id,service_id,wlocation_id from employee_personal_details epd, employee_official_details eod " +
								" where epd.emp_per_id=eod.emp_id and empcode=? and eod.org_id > 0 and epd.is_alive = true");
						pst.setString(1, empcode);
						int servic_id = 0;
						int org_id = 0;
						String wlocation = null;
						rs = pst.executeQuery();
					   //System.out.println("pst for empid ===>> " + pst);
						while (rs.next()) {
							//System.out.println("in while of empid");
							emp_per_id = uF.parseToInt(rs.getString("emp_per_id"));
							org_id = uF.parseToInt(rs.getString("org_id"));
							
							if (rs.getString("service_id") != null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")) {
								String[] str = rs.getString("service_id").split(",");
								for (int z = 0; str != null && z < str.length; z++) {
									if (uF.parseToInt(str[z]) > 0) {
										servic_id = uF.parseToInt(str[z]);
										break;
									}
								}
							}
							
							wlocation = rs.getString("wlocation_id");
						}
						rs.close();
						pst.close();
	
						//System.out.println("emp_per_id=="+emp_per_id);
						
						if (emp_per_id > 0) {
							flag = uF.isThisDateValid(strDate, DATE_FORMAT);
							if (!flag) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check first date format for employee code '"+ empcode+ "' on "+ strDate+ ".</li>");
								break;
							}
	
							flag = uF.isThisDateValid(strEndDate, DATE_FORMAT);
							if (!flag) {
								alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check last date format for employee code '"+ empcode+ "' on "+ strEndDate+ ".</li>");
								break;
							}
							
							Map<String, List<String>> RosterMp = new HashMap<String, List<String>>();
							pst = con.prepareStatement("Select roster_id,emp_id, _date,_from,_to,service_id,actual_hours from roster_details where emp_id=? and  _date between ? and ?");
							pst.setInt(1, emp_per_id);
							pst.setDate(2, uF.getDateFormat(strDate, DATE_FORMAT));
							pst.setDate(3,uF.getDateFormat(strEndDate, DATE_FORMAT));
							rs = pst.executeQuery();
							while (rs.next()) {
								List<String> innerList1 = new ArrayList<String>();
								innerList1.add(rs.getString("roster_id"));
								innerList1.add(rs.getString("_from"));
								innerList1.add(rs.getString("_to"));
								innerList1.add(rs.getString("service_id"));
								innerList1.add(rs.getString("actual_hours"));
	
								RosterMp.put(rs.getString("emp_id") + "_"+ rs.getString("_date"), innerList1);
							}
							rs.close();
							pst.close();
	
							pst = con.prepareStatement("Select count(*) as attendance_count,emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd') as attendance_date from attendance_details where emp_id=? "
											+ "and to_date(in_out_timestamp::text,'yyyy-MM-dd') between  ?  and ?"
											+ " group by emp_id,to_date(in_out_timestamp::text,'yyyy-MM-dd')");
							pst.setInt(1, emp_per_id);
							pst.setDate(2, uF.getDateFormat(strDate, DATE_FORMAT));
							pst.setDate(3,uF.getDateFormat(strEndDate, DATE_FORMAT));
							rs = pst.executeQuery();
							Map<String, String> empAttendanceMp = new HashMap<String, String>();
							while (rs.next()) {
								empAttendanceMp.put(rs.getString("attendance_date"),rs.getString("attendance_count"));
							}
							rs.close();
							pst.close();
	
							// ===========================================================================================================
							// System.out.println("innerList=====>"+innerList.size());
							// System.out.println("dateList=====>"+dateList.size());
	//						 System.out.println("innerList=====>"+innerList.toString());
	//						 System.out.println("dateList=====>"+dateList.toString());
							Map<String, List<String>> hmLeaveDates = new HashMap<String, List<String>>();
							
							for (int j = 2; j < innerList.size(); j++) {
								String dataType = innerList.get(j).trim();
								
//								System.out.println("dataType=====>"+dataType);
								
//								 System.out.println(dataType+" dateList.get(j)=====>"+dateList.get(j)); 
								flag = uF.isThisDateValid(dateList.get(j),DATE_FORMAT);
								if (!flag) {
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check date format for employee code '"+ empcode+ "' on "+ dateList.get(j) + ".</li>");
									break;
								}
								
								boolean checkSalaryFlag = CF.checkSalaryForImportAttendance(con, CF, uF, emp_per_id, dateList.get(j), dateList.get(j), DATE_FORMAT);
								if(checkSalaryFlag){
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Salary already processed for employee code '" + empcode + "' on "+dateList.get(j)+".</li>");
									flag = false;
									break;
								}
								
								boolean checkAttendanceApproveFlag = CF.checkAttendanceApproveForImportAttendance(con, CF, uF, emp_per_id, dateList.get(j), dateList.get(j), DATE_FORMAT);
								if(checkAttendanceApproveFlag){
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Attendance already approved for employee code '" + empcode + "' on "+dateList.get(j)+".</li>");
									flag = false;
									break;
								}
								
						//===start parvez date: 04-08-2022===
								if(j==2){
									String strMonthMinMaxDate = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
									String[] arrMonthDates = strMonthMinMaxDate.split("::::");
									/*boolean checkLeaveFlag1 = CF.checkLeaveForImportAttendance(con, CF, uF, emp_per_id, arrMonthDates[0], arrMonthDates[1], DATE_FORMAT);
									if(checkLeaveFlag1){
										pst = con.prepareStatement("delete from emp_leave_entry where emp_id=? and ((? between approval_from and approval_to_date) " +
												" or (? between approval_from and approval_to_date) or (approval_from >= ? and approval_from<=?))");
										pst.setInt(1, emp_per_id);
										pst.setDate(2, uF.getDateFormat(arrMonthDates[0], DATE_FORMAT));
										pst.setDate(3, uF.getDateFormat(arrMonthDates[1], DATE_FORMAT));
										pst.setDate(4, uF.getDateFormat(arrMonthDates[0], DATE_FORMAT));
										pst.setDate(5, uF.getDateFormat(arrMonthDates[1], DATE_FORMAT));
										pst.executeUpdate();
										pst.close();
									}*/
									
									ArrayList<String> alApliedLeaveIds = new ArrayList<String>();
									pst = con.prepareStatement("select * from emp_leave_entry where emp_id=? and ((? between approval_from and approval_to_date) " +
											" or (? between approval_from and approval_to_date) or (approval_from >= ? and approval_from<=?)) and is_approved in (0,1) and (is_modify is null or is_modify=false)");
									pst.setInt(1, emp_per_id);
									pst.setDate(2, uF.getDateFormat(arrMonthDates[0], DATE_FORMAT));
									pst.setDate(3, uF.getDateFormat(arrMonthDates[1], DATE_FORMAT));
									pst.setDate(4, uF.getDateFormat(arrMonthDates[0], DATE_FORMAT));
									pst.setDate(5, uF.getDateFormat(arrMonthDates[1], DATE_FORMAT));
									// System.out.println("pst=====>"+pst);
									rs = pst.executeQuery();
									while (rs.next()) {
										alApliedLeaveIds.add(rs.getString("leave_id"));
									}
									rs.close();
									pst.close();
									
									if(alApliedLeaveIds != null && !alApliedLeaveIds.isEmpty()){
										for(int ik=0; ik<alApliedLeaveIds.size(); ik++){
											pst = con.prepareStatement("update leave_application_register set is_modify = true, modify_date=?, modify_by=? where leave_id= ?");
											pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
											pst.setInt(3, uF.parseToInt(alApliedLeaveIds.get(ik)));
											int x = pst.executeUpdate();
											pst.close();
											
											if(x > 0){
												pst = con.prepareStatement("update emp_leave_entry set is_modify = true, modify_date=?, modify_by=?, cancel_reason=? where leave_id=?");
												pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
												pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
												pst.setString(3, "Import attendance Data");
												pst.setInt(4, uF.parseToInt(alApliedLeaveIds.get(ik)));
												pst.executeUpdate();
												pst.close();
											}
										}
									}
									
									boolean checkAttendanceFlag1 = CF.checkAttendanceForImportAttendance(con, CF, uF, emp_per_id, arrMonthDates[0], arrMonthDates[1], DATE_FORMAT);
									if(checkAttendanceFlag1){
										pst = con.prepareStatement("delete from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd') between ? and ? ");
										pst.setInt(1, emp_per_id);
										pst.setDate(2, uF.getDateFormat(arrMonthDates[0], DATE_FORMAT));
										pst.setDate(3, uF.getDateFormat(arrMonthDates[1], DATE_FORMAT));
										pst.executeUpdate();
										pst.close();
									}
								}
						//===end parvez date: 04-08-2022===	
								
								boolean checkLeaveFlag = CF.checkLeaveForImportAttendance(con, CF, uF, emp_per_id, dateList.get(j), dateList.get(j), DATE_FORMAT);
								if(checkLeaveFlag){
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Leave already applied for employee code '" + empcode + "' on "+dateList.get(j)+".</li>");
									flag = false;
									break;
								}
								
								
								boolean checkAttendanceFlag = CF.checkAttendanceForImportAttendance(con, CF, uF, emp_per_id, dateList.get(j), dateList.get(j), DATE_FORMAT);
								if(checkAttendanceFlag){
									alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">" +
											"Attendance already existed for employee code '" + empcode + "' on "+dateList.get(j)+".</li>");
									flag = false;
									break;
								}
	
								if (dataType.equalsIgnoreCase("WO")) {
									if (RosterMp.get(emp_per_id+ "_"+ uF.getDateFormat(dateList.get(j),DATE_FORMAT, DBDATE)) != null) {
									} else {
										String workInTime = null;
										String workOuttime = null;
										List<String> workTime = wLocationMp.get(wlocation);
										if (workTime == null) {
											alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">"+ empcode+ "  's Work Location is not found.</li>");
											flag = false;
											break;
										}
										if (workTime != null) {
											workInTime = workTime.get(0);
											workOuttime = workTime.get(1);
										}
	
										pst = con.prepareStatement("Insert into roster_details (emp_id,_date,_from,_to,isapproved,user_id,service_id,actual_hours,attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id)"
														+ "values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
	
										pst.setInt(1, emp_per_id);
										pst.setDate(2, uF.getDateFormat(dateList.get(j), DATE_FORMAT));
										pst.setTime(3, new java.sql.Time(uF.getDateFormat(workInTime, DBTIME).getTime()));
										pst.setTime(4, new java.sql.Time(uF.getDateFormat(workOuttime, DBTIME).getTime()));
										pst.setBoolean(5, false);
										pst.setInt(6, 310);
										pst.setInt(7, servic_id); // service id
										pst.setDouble(8,uF.parseToDouble(uF.getTimeDiffInHoursMins(uF.getDateFormat(workInTime,DBTIME).getTime(),uF.getDateFormat(workOuttime,DBTIME).getTime())));
										pst.setInt(9, 0);
										pst.setBoolean(10, false);
										pst.setInt(11, 1);
										pst.setDate(12,new java.sql.Date(System.currentTimeMillis()));
										pst.setInt(13, 1);
//										System.out.println("pst--1=====>"+pst);
										pst.executeUpdate();
										pst.close();
	
										List<String> innerList1 = new ArrayList<String>();
										innerList1.add("0");
										innerList1.add(workInTime);
										innerList1.add(workOuttime);
										innerList1.add(servic_id + "");
										innerList1.add(uF.getTimeDiffInHoursMins(uF.getDateFormat(workInTime, DBTIME).getTime(),uF.getDateFormat(workOuttime, DBTIME).getTime()));
										
										RosterMp.put(emp_per_id+ "_"+ uF.getDateFormat(dateList.get(j),DATE_FORMAT, DBDATE),innerList1);
	
									}
								}
								
								if (dataType.equalsIgnoreCase("P") || dataType.contains("HD")) {
									//System.out.println("in datatype.equals(P || HD) ");
//									if (RosterMp.get(emp_per_id+ "_"+ uF.getDateFormat(dateList.get(j),DATE_FORMAT, DBDATE)) != null) {
//										//System.out.println("in rosterMp---");
//									} else {
//										
//										//System.out.println("in else part of rosterMp");
//										
//										String workInTime = null;
//										String workOuttime = null;
//										List<String> workTime = wLocationMp.get(wlocation);
//										if (workTime == null) {
//											alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">"+ empcode+ "  's Work Location is not found.</li>");
//											flag = false;
//											break;
//										}
//										if (workTime != null) {
//											workInTime = workTime.get(0);
//											workOuttime = workTime.get(1);
//										}
//	
//										pst = con.prepareStatement("Insert into roster_details (emp_id,_date,_from,_to,isapproved,user_id,service_id,actual_hours,attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id)"
//														+ "values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
//	
//										pst.setInt(1, emp_per_id);
//										pst.setDate(2, uF.getDateFormat(dateList.get(j), DATE_FORMAT));
//										pst.setTime(3, new java.sql.Time(uF.getDateFormat(workInTime, DBTIME).getTime()));
//										pst.setTime(4, new java.sql.Time(uF.getDateFormat(workOuttime, DBTIME).getTime()));
//										pst.setBoolean(5, false);
//										pst.setInt(6, 310);
//										pst.setInt(7, servic_id); // service id
//										pst.setDouble(8,uF.parseToDouble(uF.getTimeDiffInHoursMins(uF.getDateFormat(workInTime,DBTIME).getTime(),uF.getDateFormat(workOuttime,DBTIME).getTime())));
//										pst.setInt(9, 0);
//										pst.setBoolean(10, false);
//										pst.setInt(11, 1);
//										pst.setDate(12,new java.sql.Date(System.currentTimeMillis()));
//										pst.setInt(13, 1);
//										System.out.println("pst--1=====>"+pst);
//										pst.executeUpdate();
//										pst.close();
//	
//										List<String> innerList1 = new ArrayList<String>();
//										innerList1.add("0");
//										innerList1.add(workInTime);
//										innerList1.add(workOuttime);
//										innerList1.add(servic_id + "");
//										innerList1.add(uF.getTimeDiffInHoursMins(uF.getDateFormat(workInTime, DBTIME).getTime(),uF.getDateFormat(workOuttime, DBTIME).getTime()));
//										
//										RosterMp.put(emp_per_id+ "_"+ uF.getDateFormat(dateList.get(j),DATE_FORMAT, DBDATE),innerList1);
//	
//									}
	
									int i = uF.parseToInt(empAttendanceMp.get(emp_per_id+ "_"+ uF.getDateFormat(dateList.get(j),DATE_FORMAT, DBDATE)));
	//								System.out.println(i+" emp_per_id=====>"+emp_per_id+"---- uF.getDateFormat(dateList.get(j),DATE_FORMAT, DBDATE)=====>"+uF.getDateFormat(dateList.get(j),DATE_FORMAT, DBDATE));
									if (i >= 2) {
	
									} else {
										String _fromTime = null;
										String _toTime = null;
	
										List<String> rosterList = RosterMp.get(emp_per_id+ "_"+ uF.getDateFormat(dateList.get(j),DATE_FORMAT, DBDATE));
										if (rosterList != null) {
	
											_fromTime = rosterList.get(1);
											_toTime = rosterList.get(2);
											servic_id = uF.parseToInt(rosterList.get(3));
										}
	
										if (i == 0) {
											// IN CODING
											// insertINEntry(con,prepareStatement,rs,uF,
											// emp_per_id, servic_id,
											// dateList.get(j),
											// _fromTime,_fromTime,dateFormat,timeFormat);
	
											alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check time for employee code '"+ empcode+ "' on "+ dateList.get(j)+ ".</li>");
											long lStart = uF.getTimeFormat(dateList.get(j) + " "+ _fromTime,dateFormat + " " + timeFormat).getTime();
											long in = uF.getTimeFormat(dateList.get(j) + " "+ _fromTime,dateFormat + " " + timeFormat).getTime();
	
											pst = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
															+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
											pst.setInt(1, emp_per_id);
											pst.setTimestamp(2, uF.getTimeStamp(dateList.get(j) + " "+ _fromTime, dateFormat+ " " + timeFormat));
											pst.setString(3, " ");
											pst.setString(4, "IN");
											pst.setInt(5, 0);
											pst.setString(6, " ");
											pst.setNull(7, java.sql.Types.DOUBLE);
											pst.setTimestamp(8, uF.getTimeStamp(dateList.get(j) + " "+ _fromTime, dateFormat+ " " + timeFormat));
											pst.setInt(9, servic_id);
											if (in > 0 && in > lStart) {
												pst.setDouble(10,-uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));
											} else if (lStart > 0 && lStart > in) {
												pst.setDouble(10,uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
											} else {
												pst.setDouble(10, 0);
											}
//											System.out.println("pst--2=====>"+pst);
											pst.executeUpdate();
											pst.close();
											// prepareStatement.clearParameters();
	
										}
										// OUT CODING
										if (dataType.equalsIgnoreCase("HD") || dataType.contains("/HD")) {
											//System.out.println("in datatype.equals(HD/HD)");
	
											// sdsd
											java.util.Date myDate = parser.parse(dateList.get(j) + " "+ _fromTime.toString());
											Calendar cal = Calendar.getInstance();
											cal.setTime(myDate);
											cal.add(Calendar.HOUR_OF_DAY, 4); // this
																				// will
																				// add
																				// two
																				// hours
											myDate = cal.getTime();
	
											// insertOUTEntry(con,prepareStatement,rs,uF,
											// emp_per_id, servic_id,
											// dateList.get(j),
											// parser1.format(myDate),
											// _toTime,_fromTime,dateFormat,timeFormat);
	
											alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check time for employee code '"+ empcode+ "' on "+ dateList.get(j)+ ".</li>");
											long lStart = uF.getTimeFormat(dateList.get(j)+ " "+ parser1.format(myDate),dateFormat + " "+ timeFormat).getTime();
											long in = uF.getTimeFormat(dateList.get(j) + " "+ _toTime,dateFormat + " "+ timeFormat).getTime();
											long lEnd = uF.getTimeFormat(dateList.get(j) + " "+ _fromTime,dateFormat + " " + timeFormat).getTime();
	
											pst = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
															+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
											pst.setInt(1, emp_per_id);
											pst.setTimestamp(2,uF.getTimeStamp(dateList.get(j)+ " "+ parser1.format(myDate),dateFormat + " "+ timeFormat));
											pst.setString(3, " ");
											pst.setString(4, "OUT");
											pst.setInt(5, 0);
											pst.setString(6, " ");
											pst.setDouble(7, uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd,lStart)));
											pst.setTimestamp(8,uF.getTimeStamp(dateList.get(j)+ " "+ parser1.format(myDate),dateFormat + " "+ timeFormat));
											pst.setInt(9, servic_id);
											if (in > 0 && in > lStart) {
												pst.setDouble(10,-uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));
											} else if (lStart > 0 && lStart > in) {
												pst.setDouble(10,uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
											} else {
												pst.setDouble(10, 0);
											}
//											System.out.println("pst--3=====>"+pst);
											pst.executeUpdate();
											pst.close();
											// prepareStatement.clearParameters();
	
										} else {
											
											//System.out.println("in else part of HD/HD");
											// insertOUTEntry(con,prepareStatement,rs,uF,
											// emp_per_id, servic_id,
											// dateList.get(j), _toTime,
											// _toTime,_fromTime,dateFormat,timeFormat);
											alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check time for employee code '"+ empcode+ "' on "+ dateList.get(j)+ ".</li>");
											long lStart = uF.getTimeFormat(dateList.get(j) + " "+ _toTime,dateFormat + " "+ timeFormat).getTime();
											long in = uF.getTimeFormat(dateList.get(j) + " "+ _toTime,dateFormat + " "+ timeFormat).getTime();
											long lEnd = uF.getTimeFormat(dateList.get(j) + " "+ _fromTime,dateFormat + " " + timeFormat).getTime();
	
											pst = con.prepareStatement("insert into attendance_details(emp_id,in_out_timestamp,reason,in_out,approved,comments,hours_worked,"
															+ "in_out_timestamp_actual,service_id,early_late)values(?,?,?,?,?,?,?,?,?,?)");
											pst.setInt(1, emp_per_id);
											pst.setTimestamp(2,uF.getTimeStamp(dateList.get(j)+ " " + _toTime,dateFormat + " "+ timeFormat));
											pst.setString(3, " ");
											pst.setString(4, "OUT");
											pst.setInt(5, 0);
											pst.setString(6, " ");
											pst.setDouble(7, uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd,lStart)));
											pst.setTimestamp(8,uF.getTimeStamp(dateList.get(j)+ " " + _toTime,dateFormat + " "+ timeFormat));
											pst.setInt(9, servic_id);
											if (in > 0 && in > lStart) {
												pst.setDouble(10,-uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));
											} else if (lStart > 0 && lStart > in) {
												pst.setDouble(10,uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));
											} else {
												pst.setDouble(10, 0);
											}
//											System.out.println("pst--4=====>"+pst);
											pst.executeUpdate();
											pst.close();
											// prepareStatement.clearParameters();
	
										}
									}
	
								}
	
								if (!dataType.equalsIgnoreCase("P") && !dataType.equalsIgnoreCase("HD") && !dataType.equalsIgnoreCase("H") && !dataType.equalsIgnoreCase("WO") && !dataType.equalsIgnoreCase("A")) {
									//System.out.println("in datatypes---");
									
									String dType = dataType;
									//System.out.println("dType"+dType);
									
									if (dataType.contains("/")) {
										dType = dataType.split("/")[0];
									}
									String leaveTypeId = null;
									boolean is_compensatory = false;
									//System.out.println("leaveTypeOrgMp1==="+leaveTypeOrgMp1);
									Map<String, List<String>> leaveTypeOrgMp = leaveTypeOrgMp1.get(org_id + "");
									//System.out.println("leaveTypeOrgMp=="+leaveTypeOrgMp);
									
									List<String> leaveInnerList = leaveTypeOrgMp.get(dType);
									//System.out.println("leaveInnerList=="+leaveInnerList);
									
									if (leaveInnerList == null) {
										alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">"+ empcode+ "  's leave is not set.</li>");
										flag = false;
										break;
									}
									if (leaveInnerList != null) {
										leaveTypeId = leaveInnerList.get(0);
										is_compensatory = uF.parseToBoolean(leaveInnerList.get(1));
									}
	
									if (leaveTypeId != null) {
										
										//System.out.println("leaveTypeId=="+leaveTypeId);
										if (dataType.contains("HD")) {
											alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check Leave Type code for employee code '"+ empcode+ "' on "+ dateList.get(j)+ ".</li>");
											pst = con.prepareStatement("select * from emp_leave_type where level_id = (select level_id from  designation_details dd, grades_details gd where gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?)) and leave_type_id = ? and org_id=? and wlocation_id=?");
											pst.setInt(1, emp_per_id);
											pst.setInt(2,uF.parseToInt(leaveTypeId));
											pst.setInt(3, org_id);
											pst.setInt(4, uF.parseToInt(wlocation));
											rs = pst.executeQuery();
											boolean isCompensate = false;
											boolean isPaid = false;
											while (rs.next()) {
												isPaid = uF.parseToBoolean(rs.getString("is_paid"));
												isCompensate = uF.parseToBoolean(rs.getString("is_compensatory"));
											}
											rs.close();
											pst.close();
	
											pst = con.prepareStatement("INSERT INTO emp_leave_entry (emp_id,leave_from,leave_to,entrydate,emp_no_of_leave,"
													+ "leave_type_id,reason,approval_from,approval_to_date, ishalfday, session_no, document_attached, is_approved, " +
													"ispaid,is_compensate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
											pst.setInt(1, emp_per_id);
											pst.setDate(2, uF.getDateFormat(dateList.get(j), DATE_FORMAT));
											pst.setDate(3, uF.getDateFormat(dateList.get(j), DATE_FORMAT));
											pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setDouble(5, 0.5);
											pst.setInt(6,uF.parseToInt(leaveTypeId));
											pst.setString(7,"Import attendance Data");
											pst.setDate(8, uF.getDateFormat(dateList.get(j), DATE_FORMAT));
											pst.setDate(9, uF.getDateFormat(dateList.get(j), DATE_FORMAT));
											pst.setBoolean(10, true);
											pst.setString(11, null);
											pst.setString(12, null);
											pst.setInt(13, 1);
											pst.setBoolean(14, isPaid);
											pst.setBoolean(15, isCompensate);
											int x = pst.executeUpdate();
											pst.close();
	
											if (x > 0) {
												String leave_id = null;
												pst = con.prepareStatement("select max(leave_id)as leave_id from emp_leave_entry");
												rs = pst.executeQuery();
												while (rs.next()) {
													leave_id = rs.getString("leave_id");
												}
												rs.close();
												pst.close();
	
												if (uF.parseToInt(leave_id) > 0) {
													ManagerLeaveApproval leaveApproval = new ManagerLeaveApproval();
													leaveApproval.setServletRequest(request);
													leaveApproval.setLeaveId(leave_id);
													leaveApproval.setTypeOfLeave(""+ uF.parseToInt(leaveTypeId));
													leaveApproval.setEmpId(""+ emp_per_id);
													leaveApproval.setIsapproved(1);
													leaveApproval.setApprovalFromTo(dateList.get(j));
													leaveApproval.setApprovalToDate(dateList.get(j));
													leaveApproval.setIsHalfDay(true);
													leaveApproval.insertLeaveBalance(con,pst,rs,uF,uF.parseToInt(hmEmpLevel.get(emp_per_id+ "")),CF);
												}
											} else {
												alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check Leave Type code for employee code '"+ empcode+ "' on "+ strDate+ ".</li>");
												flag = false;
												break;
											}
										} else {
											alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check Leave Type code for employee code '"+ empcode+ "' on "+ dateList.get(j)+ ".</li>");
											
											if(uF.parseToInt(leaveTypeId) > 0){
												List<String> alLeaveDate = (List<String>) hmLeaveDates.get(leaveTypeId+"_"+dType);
												if(alLeaveDate == null) alLeaveDate = new ArrayList<String>();
												
												alLeaveDate.add(dateList.get(j));
												hmLeaveDates.put(leaveTypeId+"_"+dType,alLeaveDate);
												
											} else {
												alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check Leave Type code for employee code '"+ empcode+ "' on "+ strDate+ ".</li>");
												flag = false;
												break;
											}
										}
									}
								}
								flag = true;
								//System.out.println("end of datatype");
							}
							
							//System.out.println("hmLeaveDates==>"+hmLeaveDates);
							
							Iterator<String> it = hmLeaveDates.keySet().iterator();
							while(it.hasNext()){
								//System.out.println("in while loop of hmleavedates--");
								String key = it.next();
								String[] temp = key.split("_");
								String leaveTypeId = temp[0];
								String leaveType = temp[1];
								
								List<String> alLeaveDate = hmLeaveDates.get(key);
								//System.out.println("alLeaveDate---"+alLeaveDate);
								
								if(alLeaveDate == null) alLeaveDate = new ArrayList<String>();
								int nLDSize = alLeaveDate.size();
								List<String> alLeave = new ArrayList<String>();
								for(int x = 0; x < nLDSize; x++){
									String strLeaveFromDate = alLeaveDate.get(x);
									if(alLeave.contains(strLeaveFromDate)){
										continue;
									}
									if(alLeave.size() > 0){
										String strAddedDate = alLeave.get(alLeave.size()-1) ;
										SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
							            java.util.Date date1 = sdf.parse(strAddedDate);
							            java.util.Date date2 = sdf.parse(strLeaveFromDate);
	//						            System.out.println("date1==>"+date1+"--date2==>"+date2);
										if(date1.after(date2) || date1.equals(date2)){
											continue;
										}
									}
									int nIndex = dateList.indexOf(strLeaveFromDate);
	//								System.out.println("alLeaveDate==>"+alLeaveDate.get(x)+"--INdex==>"+dateList.indexOf(strLeaveFromDate));
									String strLeaveEndDate = getLeaveEndDate(alLeaveDate.get(x),innerList,dateList,alLeaveDate,x,nIndex,nLDSize,leaveType);
	//								System.out.println(leaveType+"--strLeaveFromDate==>"+strLeaveFromDate+"--strLeaveEndDate==>"+strLeaveEndDate);
									boolean isLeaveExist = checkLeaveExist(con, uF, strLeaveFromDate, strLeaveEndDate, leaveTypeId, emp_per_id);
									if(!isLeaveExist){
										//System.out.println("in isleaveExist---");
										pst = con.prepareStatement("select * from emp_leave_type where level_id = (select level_id from  designation_details dd, grades_details gd where gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?)) and leave_type_id = ? and org_id=? and wlocation_id=?");
										pst.setInt(1, emp_per_id);
										pst.setInt(2,uF.parseToInt(leaveTypeId));
										pst.setInt(3, org_id);
										pst.setInt(4, uF.parseToInt(wlocation));
										rs = pst.executeQuery();
										//System.out.println("pst=for is paid /is compensatory===");
										boolean isCompensate = false;
										boolean isPaid = false;
										while (rs.next()) {
											isPaid = uF.parseToBoolean(rs.getString("is_paid"));
											isCompensate = uF.parseToBoolean(rs.getString("is_compensatory"));
										}
										rs.close();
										pst.close();
										
										
										pst = con.prepareStatement("INSERT INTO emp_leave_entry (emp_id,leave_from,leave_to,entrydate,emp_no_of_leave,"
														+ "leave_type_id,reason,approval_from,approval_to_date, ishalfday, session_no, "
														+ "document_attached, is_approved, ispaid,is_compensate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
										pst.setInt(1, emp_per_id);
										pst.setDate(2, uF.getDateFormat(strLeaveFromDate, DATE_FORMAT));
										pst.setDate(3, uF.getDateFormat(strLeaveEndDate, DATE_FORMAT));
										pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
										int nAppliedDays = uF.parseToInt(uF.dateDifference(strLeaveFromDate,DATE_FORMAT,strLeaveEndDate,DATE_FORMAT,CF.getStrTimeZone()));
										pst.setInt(5, nAppliedDays);
										pst.setInt(6,uF.parseToInt(leaveTypeId));
										pst.setString(7,"Import attendance Data");
										pst.setDate(8, uF.getDateFormat(strLeaveFromDate, DATE_FORMAT));
										pst.setDate(9, uF.getDateFormat(strLeaveEndDate, DATE_FORMAT));
										pst.setBoolean(10, false);
										pst.setString(11, null);
										pst.setString(12, null);
										pst.setInt(13, 1);
										pst.setBoolean(14, isPaid);
										pst.setBoolean(15, isCompensate);
										int y = pst.executeUpdate();
										//System.out.println("pst for insert in to leave entry-----");
										pst.close();
			
										if (y > 0) {
											String leave_id = null;
											pst = con.prepareStatement("select max(leave_id)as leave_id from emp_leave_entry");
											rs = pst.executeQuery();
											while (rs.next()) {
												leave_id = rs.getString("leave_id");
											}
											rs.close();
											pst.close();
											
											//System.out.println("pst for select max(leave_id)----");
											if (uF.parseToInt(leave_id) > 0) {		
												ManagerLeaveApproval leaveApproval = new ManagerLeaveApproval();
												leaveApproval.setServletRequest(request);
												leaveApproval.setLeaveId(leave_id);
												leaveApproval.setTypeOfLeave(""+ uF.parseToInt(leaveTypeId));
												leaveApproval.setEmpId(""+ emp_per_id);
												leaveApproval.setIsapproved(1);
												leaveApproval.setApprovalFromTo(strLeaveFromDate);
												leaveApproval.setApprovalToDate(strLeaveEndDate);
												leaveApproval.setIsHalfDay(false);
												leaveApproval.insertLeaveBalance(con,pst,rs,uF,uF.parseToInt(hmEmpLevel.get(emp_per_id+ "")),CF);
											}
										} else {
											alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check Leave Type code for employee code '"+ empcode+ "' on "+ strLeaveFromDate+ " to "+strLeaveEndDate+".</li>");
											flag = false;
											break;
										}									
										
									} else {
										alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Leave is already exist or check leave policy for employee code '"+ empcode+ "' on "+ strLeaveFromDate+ " to "+strLeaveEndDate+".</li>");
										flag = false;
										break;
									}
									
									if(!alLeave.contains(strLeaveFromDate)){
										alLeave.add(strLeaveFromDate);
									}
									if(!alLeave.contains(strLeaveEndDate)){
										alLeave.add(strLeaveEndDate);
									}								
								}
							}
						} 
						else {
							alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the employee code '"+ empcode + "'.</li>");
							flag = false;
							break;
						}
					} else {
						alErrorList.add("<li class=\"msg_error\" style=\"margin:0px\">Please check the excel sheet.</li>");
						flag = false;
						break;
					}
					//System.out.println("inner loop ends ");
				}// end main for loop
				
			}
			if (flag) {
				con.commit();
				session.setAttribute(MESSAGE, SUCCESSM+ "Attendance Imported Successfully!" + END);
			} else {
				con.rollback();
				if (alErrorList.size() > 0) {
					alReport.add(alErrorList.get(alErrorList.size() - 1));
				}
				session.setAttribute("alReport", alReport);
				session.setAttribute(MESSAGE,ERRORM+ "Attendance not imported. Please check imported file."+ END);
			}
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if (alErrorList.size() > 0) {
				alReport.add(alErrorList.get(alErrorList.size() - 1));
			}
			session.setAttribute("alReport", alReport);
			session.setAttribute(MESSAGE, ERRORM+ "Attendance not imported. Please check imported file."+ END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
			try {
				fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.gc();
		}
	}

	private boolean checkLeaveExist(Connection con, UtilityFunctions uF, String strLeaveFromDate, String strLeaveEndDate, String leaveTypeId, int empId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean isLeaveExist = false;
		try{
			GetEmployeePolicyDetails policyDetails = new GetEmployeePolicyDetails();
			policyDetails.request = request;
			policyDetails.session = session;
			policyDetails.CF = CF;
			policyDetails.setStrEmp(""+empId);
			policyDetails.setLeavetype(leaveTypeId);
			policyDetails.setStrD1(strLeaveFromDate);
			policyDetails.setStrD2(strLeaveEndDate);
			boolean flg = policyDetails.checkLeaveDates(uF);
			policyDetails.checkPayroll(uF);
			
			if(!flg){   
				boolean isWeekOffHoliday = policyDetails.checkWeekOffHoliday(uF);
//				boolean isWeekOffHoliday = false;
				if(!isWeekOffHoliday && uF.parseToBoolean(CF.getIsWorkFlow())){
					policyDetails.getLeavePolicyMember(uF);
				}
			}
			
//			String leavetype = (String) request.getAttribute("leavetype");
			String checkLeave = (String) request.getAttribute("checkLeave");
			Boolean isApproval =(Boolean)request.getAttribute("isApproval");
			String checkPayroll = (String) request.getAttribute("checkPayroll");
			String checkAttendance = (String) request.getAttribute("checkAttendance");
			String isWeekOffHoliday = (String) request.getAttribute("isWeekOffHoliday");
			
			if (uF.parseToInt(leaveTypeId)>0) {
//				System.out.println("1");
				if (uF.parseToBoolean(checkLeave)) {
//					System.out.println("2");
					isLeaveExist = true;
				} else if (uF.parseToBoolean(checkPayroll)) {
//					System.out.println("3");
					isLeaveExist = true;
				} else if (uF.parseToBoolean(checkAttendance)) {
//					System.out.println("4");
					isLeaveExist = true;
				} else if (uF.parseToBoolean(isWeekOffHoliday)) {
//					System.out.println("5");
					isLeaveExist = true;
				} else if (uF.parseToBoolean(CF.getIsWorkFlow())) {
//					System.out.println("6");
					if (!isApproval){
//						System.out.println("7");
						isLeaveExist = false;
					}else {
//						System.out.println("8");
						isLeaveExist = false;
					}
				} else {
//					System.out.println("9");
					isLeaveExist = false;
				}
			} else {
//				System.out.println("10");
				isLeaveExist = true;
			}
			
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
		return isLeaveExist;
	}

	private String getLeaveEndDate(String strFromDate, List<String> innerList, List<String> dateList, List<String> alLeaveDate, int x, int nIndex,int nLDSize, String leaveType) {
		String strLeaveEndDate = null;
		try{
			int nNewIndex = (nIndex + 1);
			String strDate = dateList.get(nNewIndex);
			String strVal = innerList.get(nNewIndex);
			if(strVal.trim().equalsIgnoreCase("WO") || strVal.trim().equalsIgnoreCase("H") || strVal.trim().equalsIgnoreCase(leaveType)){
				strLeaveEndDate = checkLeaveEndDate(alLeaveDate.get(x),innerList,dateList,alLeaveDate,x,nIndex,nLDSize,leaveType);
				if(strVal.trim().equalsIgnoreCase(leaveType) && (strLeaveEndDate == null || strLeaveEndDate.trim().equals("") || strLeaveEndDate.trim().equalsIgnoreCase("NULL"))){
					strLeaveEndDate = strDate;
				}
			}
			
			if(strLeaveEndDate == null || strLeaveEndDate.trim().equals("") || strLeaveEndDate.trim().equalsIgnoreCase("NULL")){
				strLeaveEndDate = strFromDate;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return strLeaveEndDate;
	}

	private String checkLeaveEndDate(String strFromDate, List<String> innerList, List<String> dateList, List<String> alLeaveDate, int x, int nIndex, int nLDSize,
			String leaveType) {
		String strLeaveEndDate = null;
		try{
			int nNewIndex = (nIndex + 1);
			String strDate = dateList.get(nNewIndex);
			String strVal = innerList.get(nNewIndex);
			if(strVal.trim().equalsIgnoreCase("WO") || strVal.trim().equalsIgnoreCase("H")  || strVal.trim().equalsIgnoreCase(leaveType)){
				strLeaveEndDate =  checkLeaveEndDate(alLeaveDate.get(x),innerList,dateList,alLeaveDate,x,nNewIndex,nLDSize,leaveType);
				if(strVal.trim().equalsIgnoreCase(leaveType) && (strLeaveEndDate == null || strLeaveEndDate.trim().equals("") || strLeaveEndDate.trim().equalsIgnoreCase("NULL"))){
					strLeaveEndDate = strDate;
				}
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return strLeaveEndDate;
	}

	public void insertINEntry(Connection con, PreparedStatement pst,
			ResultSet rs, UtilityFunctions uF, int empId, int serviceId,
			String strDate, String strTime, String rosterTime,
			String dateFormat, String timeFormat) {
		try {

			long lStart = uF.getTimeFormat(strDate + " " + strTime,dateFormat + " " + timeFormat).getTime();
			long in = uF.getTimeFormat(strDate + " " + rosterTime,dateFormat + " " + timeFormat).getTime();

			pst.setInt(1, empId);
			pst.setTimestamp(2,uF.getTimeStamp(strDate + " " + strTime, dateFormat + " "+ timeFormat));
			pst.setString(3, " ");
			pst.setString(4, "IN");
			pst.setInt(5, 0);
			pst.setString(6, " ");
			pst.setNull(7, java.sql.Types.DOUBLE);
			pst.setTimestamp(8,uF.getTimeStamp(strDate + " " + strTime, dateFormat + " "+ timeFormat));
			pst.setInt(9, serviceId);

			if (in > 0 && in > lStart) {
				pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));

			} else if (lStart > 0 && lStart > in) {
				pst.setDouble(10,uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));

			} else {
				pst.setDouble(10, 0);

			}

			pst.executeUpdate();
			pst.clearParameters();
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertOUTEntry(Connection con, PreparedStatement pst,
			ResultSet rs, UtilityFunctions uF, int empId, int serviceId,
			String strDate, String strTime, String rosterTime,
			String strInTime, String dateFormat, String timeformat) {
		try {

			long lStart = uF.getTimeFormat(strDate + " " + strTime,dateFormat + " " + timeformat).getTime();
			long in = uF.getTimeFormat(strDate + " " + rosterTime,dateFormat + " " + timeformat).getTime();
			long lEnd = uF.getTimeFormat(strDate + " " + strInTime,dateFormat + " " + timeformat).getTime();

			pst.setInt(1, empId);
			pst.setTimestamp(2,uF.getTimeStamp(strDate + " " + strTime, dateFormat + " "+ timeformat));
			pst.setString(3, " ");
			pst.setString(4, "OUT");
			pst.setInt(5, 0);
			pst.setString(6, " ");
			pst.setDouble(7,uF.parseToDouble(uF.getTimeDiffInHoursMins(lEnd, lStart)));
			pst.setTimestamp(8,uF.getTimeStamp(strDate + " " + strTime, dateFormat + " "+ timeformat));
			pst.setInt(9, serviceId);

			if (in > 0 && in > lStart) {
				pst.setDouble(10, -uF.parseToDouble(uF.getTimeDiffInHoursMins(lStart, in)));

			} else if (lStart > 0 && lStart > in) {
				pst.setDouble(10,uF.parseToDouble(uF.getTimeDiffInHoursMins(in, lStart)));

			} else {
				pst.setDouble(10, 0);

			}

			pst.executeUpdate();
			pst.clearParameters();

			// }

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
	}

	public File getFileUpload1() {
		return fileUpload1;
	}

	public void setFileUpload1(File fileUpload1) {
		this.fileUpload1 = fileUpload1;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public File getFileUpload2() {
		return fileUpload2;
	}

	public void setFileUpload2(File fileUpload2) {
		this.fileUpload2 = fileUpload2;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
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

	public String getwLocation() {
		return wLocation;
	}

	public void setwLocation(String wLocation) {
		this.wLocation = wLocation;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public String insertLeaveEntry(Connection con, PreparedStatement pst,
			ResultSet rs, UtilityFunctions uF, int empId, int leaveID,
			String startDate, String endDate, boolean isHalfDay, int levelId,
			int orgId, int wlocationId) {
		try {

			pst = con.prepareStatement("select * from emp_leave_type where level_id = (select level_id from  designation_details dd, grades_details gd where gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?)) and leave_type_id = ? and org_id=? and wlocation_id=?");
			pst.setInt(1, empId);
			pst.setInt(2, leaveID);
			pst.setInt(3, orgId);
			pst.setInt(4, wlocationId);
			rs = pst.executeQuery();
			boolean isCompensate = false;
			boolean isPaid = false;
			while (rs.next()) {
				isPaid = uF.parseToBoolean(rs.getString("is_paid"));
				isCompensate = uF.parseToBoolean(rs.getString("is_compensatory"));
			}
			rs.close();
			pst.close();

			// pst = con.prepareStatement(insertEmployeeLeaveEntry);
			pst = con.prepareStatement("INSERT INTO emp_leave_entry (emp_id,leave_from,leave_to,entrydate,emp_no_of_leave,"
							+ "leave_type_id,reason,approval_from,approval_to_date, ishalfday, session_no, "
							+ "document_attached, is_approved, ispaid,is_compensate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setInt(1, empId);
			if (isHalfDay) {
				pst.setDate(2, uF.getDateFormat(startDate, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(startDate, DATE_FORMAT));
			} else {
				pst.setDate(2, uF.getDateFormat(startDate, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(endDate, DATE_FORMAT));
			}
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			int nAppliedDays = 0;
			if (isHalfDay) {
				pst.setDouble(5, 0.5);
			} else {
				nAppliedDays = uF.parseToInt(uF.dateDifference(startDate,DATE_FORMAT, endDate, DATE_FORMAT,CF.getStrTimeZone()));
				pst.setInt(5, nAppliedDays);
			}
			pst.setInt(6, leaveID);
			pst.setString(7, "Import attendance Data");
			if (isHalfDay) {
				pst.setDate(8, uF.getDateFormat(startDate, DATE_FORMAT));
				pst.setDate(9, uF.getDateFormat(startDate, DATE_FORMAT));
			} else {
				pst.setDate(8, uF.getDateFormat(startDate, DATE_FORMAT));
				pst.setDate(9, uF.getDateFormat(endDate, DATE_FORMAT));
			}
			pst.setBoolean(10, isHalfDay);
			pst.setString(11, null);
			pst.setString(12, null);
			pst.setInt(13, 1);
			pst.setBoolean(14, isPaid);
			pst.setBoolean(15, isCompensate);
			pst.execute();
			pst.close();

			// System.out.println("getTypeOfLeave()===>"+getTypeOfLeave());
			String leave_id = null;
			pst = con
					.prepareStatement("select max(leave_id)as leave_id from emp_leave_entry");
			rs = pst.executeQuery();
			while (rs.next()) {
				leave_id = rs.getString("leave_id");
			}
			rs.close();
			pst.close();

			if (uF.parseToInt(leave_id) > 0) {

				ManagerLeaveApproval leaveApproval = new ManagerLeaveApproval();
				leaveApproval.setServletRequest(request);
				leaveApproval.setLeaveId(leave_id);
				leaveApproval.setTypeOfLeave("" + leaveID);
				leaveApproval.setEmpId("" + empId);
				leaveApproval.setIsapproved(1);
				leaveApproval.setApprovalFromTo(startDate);
				leaveApproval.setApprovalToDate(endDate);
				leaveApproval.setIsHalfDay(isHalfDay);
				leaveApproval.insertLeaveBalance(con, pst, rs, uF, levelId, CF);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return SUCCESS;

	}

	public void insertLeaveBalance(Connection con, PreparedStatement pst,
			ResultSet rs, UtilityFunctions uF, int leaveId, int typeOFLeave,
			int empId, int levelID, boolean isHalfday, String startDate,
			String endDate, int orgId, int wlocationId) {
		try {
			// Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap();
			// String levelID=(String)hmEmpLevel.get(getEmpId());
			// String strWLocationId = hmEmpWLocation.get(getEmpId());

			pst = con.prepareStatement("select is_compensate from emp_leave_entry where leave_id=?");
			pst.setInt(1, leaveId);
			rs = pst.executeQuery();
			boolean iscompensate = false;
			while (rs.next()) {
				iscompensate = uF.parseToBoolean(rs.getString("is_compensate"));
			}
			rs.close();
			pst.close();

			boolean isSandwichLeave = false;
			boolean isConstantBalance = false;
			boolean isPaid = false;
			boolean isCompensatary = false;
			int compensate_with = 0;
			List<String> prefix = new ArrayList<String>();
			List<String> suffix = new ArrayList<String>();
			List<String> sandwichleavetype = new ArrayList<String>();

			pst = con.prepareStatement("select * from emp_leave_type where leave_type_id=? and level_id = ? and org_id=? and wlocation_id=?");
			pst.setInt(1, typeOFLeave);
			pst.setInt(2, levelID);
			pst.setInt(3, orgId);
			pst.setInt(4, wlocationId);
			// System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				isSandwichLeave = uF.parseToBoolean(rs.getString("is_sandwich"));
				isConstantBalance = uF.parseToBoolean(rs.getString("is_constant_balance"));
				isCompensatary = rs.getBoolean("is_compensatory");
				compensate_with = rs.getInt("compensate_with");
				isPaid = uF.parseToBoolean(rs.getString("is_paid"));
				if (rs.getString("sandwich_leave_type") != null)
					sandwichleavetype = Arrays.asList(rs.getString("sandwich_leave_type").split(","));

				if (rs.getString("leave_suffix") != null)
					suffix = Arrays.asList(rs.getString("leave_suffix").split(","));
				if (rs.getString("leave_prefix") != null)
					prefix = Arrays.asList(rs.getString("leave_prefix").split(","));
			}
			rs.close();
			pst.close();

			// if(isCompensatary){
			// typeOFLeave=compensate_with;
			// }

			if (!iscompensate) {
				// Needs to add a condition if manager approves leaves other the
				// dates for which employee has applied.

				if (isPaid) {
					pst = con.prepareStatement("UPDATE emp_leave_entry SET ispaid=true WHERE leave_id=?");
					pst.setInt(1, leaveId);
					pst.execute();
					pst.close();
				}

				pst = con.prepareStatement("select * from leave_register1 where emp_id = ? and leave_type_id=? and _date=? ");
				pst.setInt(1, empId);
				pst.setInt(2, typeOFLeave);
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				double dblTakenLeaves = 0;
				while (rs.next()) {
					dblTakenLeaves = rs.getDouble("taken_paid")+ rs.getDouble("taken_unpaid");
				}
				rs.close();
				pst.close();

				pst = con.prepareStatement("select * from leave_register1 lr,(select max(_date) as _date, leave_type_id, emp_id from leave_register1 where _date<= ? and emp_id = ? and leave_type_id=? group by leave_type_id, emp_id ) lr1 where lr1._date= lr._date and lr.emp_id = lr1.emp_id and lr.leave_type_id = lr1.leave_type_id  and lr.emp_id = ? and lr.leave_type_id=?");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(2, empId);
				pst.setInt(3, typeOFLeave);
				pst.setInt(4, empId);
				pst.setInt(5, typeOFLeave);

				rs = pst.executeQuery();
				double dblBalance = 0;
				while (rs.next()) {
					dblBalance = uF.parseToDouble(rs.getString("balance"));
				}
				rs.close();
				pst.close();

				pst = con.prepareStatement("update leave_register set taken_leaves=? where emp_id = ? and leave_type_id=? and from_date<=? and to_date>=? ");
				if (isHalfday) {
					pst.setDouble(1, (dblTakenLeaves + 0.5));
				} else {
					pst.setDouble(1, (dblTakenLeaves + uF.parseToInt(uF.dateDifference(startDate, DATE_FORMAT, endDate,DATE_FORMAT,CF.getStrTimeZone()))));
				}
				pst.setInt(2, empId);
				pst.setInt(3, typeOFLeave);
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();

				double dblLeavesApproved = 0;

				if (isHalfday) {
					dblLeavesApproved = 0.5;
				} else {
					dblLeavesApproved = uF.parseToInt(uF.dateDifference(startDate, DATE_FORMAT, endDate, DATE_FORMAT,CF.getStrTimeZone()));
				}

				dblLeavesApproved = 0;
				if (isHalfday) {
					dblLeavesApproved = 0.5;
				} else {
					dblLeavesApproved = uF.parseToInt(uF.dateDifference(startDate, DATE_FORMAT, endDate, DATE_FORMAT,CF.getStrTimeZone()));
				}

				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "MM")) - 1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "yyyy")));

				Calendar cal2 = GregorianCalendar.getInstance();
				cal2.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(endDate,DATE_FORMAT, "dd")));
				cal2.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(endDate, DATE_FORMAT, "MM")) - 1);
				cal2.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(endDate,DATE_FORMAT, "yyyy")));

				double dblCount = 0;
				boolean isPaid1 = false;
				double dblLeaveDed = 0;
				double dblBalance1 = dblBalance;

				pst = con.prepareStatement("select * from work_location_info where wlocation_id=(select wlocation_id from employee_official_details where emp_id=?)");
				pst.setInt(1, empId);
				rs = pst.executeQuery();

				String weeklyoff1 = null;
				String weeklyoff2 = null;
				String weeklyoff3 = null;

				String weeklyoff1type = null;
				String weeklyoff2type = null;
				String weeklyoff3type = null;
				String wlocation_weeknos3 = null;

				while (rs.next()) {

					weeklyoff1 = rs.getString("wlocation_weeklyoff1");
					weeklyoff2 = rs.getString("wlocation_weeklyoff2");
					weeklyoff3 = rs.getString("wlocation_weeklyoff3");

					weeklyoff1type = rs.getString("wlocation_weeklyofftype1");
					weeklyoff2type = rs.getString("wlocation_weeklyofftype2");
					weeklyoff3type = rs.getString("wlocation_weeklyofftype3");
					wlocation_weeknos3 = rs.getString("wlocation_weeknos3");
				}
				rs.close();
				pst.close();

				double leave = 0;

				List<String> adDay = null;
				if (wlocation_weeknos3 != null) {
					adDay = Arrays.asList(wlocation_weeknos3.split(","));
				}

				if (adDay == null)
					adDay = new ArrayList<String>();

				// System.out.println("adDay "+adDay.toString());

				if (isSandwichLeave) {
					Calendar cal1 = (Calendar) cal.clone();
					while (true) {
						cal1.add(Calendar.DATE, -1);
						String strDate = cal1.get(Calendar.DATE) + "/"+ (cal1.get(Calendar.MONTH) + 1) + "/"+ cal1.get(Calendar.YEAR);
						Date dtCurrent1 = uF.getDateFormat(cal1.get(Calendar.DATE) + "/"+ (cal1.get(Calendar.MONTH) + 1) + "/"+ cal1.get(Calendar.YEAR), DATE_FORMAT);
						String day = uF.getDateFormat(strDate, DATE_FORMAT,"EEEE");

						// System.out.println("prefixday==>"+day);
						boolean flag = false;
						boolean flag1 = false;

						String prefix_type = "";

						pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and emp_id=?");
						pst.setDate(1, dtCurrent1);
						pst.setInt(2, empId);
						rs = pst.executeQuery();
						while (rs.next()) {
							flag1 = true;
						}
						rs.close();
						pst.close();

						if (!flag1) {

							boolean flag2 = false;
							pst = con.prepareStatement("select * from leave_application_register where _date=?  and emp_id=?");
							pst.setDate(1, dtCurrent1);
							pst.setInt(2, empId);
							rs = pst.executeQuery();
							while (rs.next()) {
								flag2 = true;
							}
							rs.close();
							pst.close();

							if (!flag2) {
								if (!prefix.contains("-2")) {
									pst = con.prepareStatement("select * from holidays where _date=? and wlocation_id=(select wlocation_id from employee_official_details where emp_id=?) and (is_optional_holiday is null or is_optional_holiday=false)");
									pst.setDate(1, dtCurrent1);
									pst.setInt(2, empId);
									rs = pst.executeQuery();
									while (rs.next()) {
										flag = true;
										prefix_type = "H";
									}
									rs.close();
									pst.close();
								}

								if (!flag) {

									if (!prefix.contains("-1")) {
										if (weeklyoff1 != null && weeklyoff1.equalsIgnoreCase(day)) {
											if (weeklyoff1type != null && weeklyoff1type.equalsIgnoreCase("HD")) {
												dblLeaveDed = .5;
											} else {
												dblLeaveDed = 1;
											}
											prefix_type = "WO";
											flag = true;
										}
										if (!flag && weeklyoff2 != null && weeklyoff2.equalsIgnoreCase(day)) {
											if (weeklyoff2type != null && weeklyoff2type.equalsIgnoreCase("HD")) {
												dblLeaveDed = .5;
											} else {
												dblLeaveDed = 1;
											}
											prefix_type = "WO";
											flag = true;
										}
										if (!flag && weeklyoff3 != null && weeklyoff3.equalsIgnoreCase(day)) {
											int checkWeek = getMonthCont(new UtilityFunctions(),strDate);
											// System.out.println("sandwich checkweek "+checkWeek);
											if (adDay.contains("" + checkWeek)) {
												if (weeklyoff3type != null && weeklyoff3type.equalsIgnoreCase("HD")) {
													dblLeaveDed = .5;
												} else {
													dblLeaveDed = 1;
												}
												prefix_type = "WO";
												flag = true;
											}
										}
									}
								}
							}
						}
						if (flag) {

							// Date dtCurrent =
							// uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)
							// + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT);
							if (isHalfday) {
								dblCount += 0.5;
							} else {

								if ((dblBalance - dblCount) == 0.5) {
									dblCount += 0.5;
								} else {
									dblCount++;
								}

							}

							if (dblBalance >= dblCount && isPaid) {
								isPaid1 = true;

								if (isHalfday) {
									dblBalance1 -= 0.5;
									dblLeaveDed = 0.5;
								} else {

									if (dblBalance1 >= 1) {
										dblBalance1 -= 1;
										dblLeaveDed = 1;
									} else if (dblBalance1 >= 0.5) {
										dblBalance1 -= 0.5;
										dblLeaveDed = 0.5;
									} else {
										dblLeaveDed = 0;
									}
								}
							} else {
								isPaid1 = false;
								if (isHalfday) {
									dblLeaveDed = 0.5;
								} else {
									dblLeaveDed = 1;
								}

							}

							if (isConstantBalance) {
								isPaid1 = true;
							}

							pst = con.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, is_paid, "
											+ "balance, _type,prefix_suffix,prefix_suffix_type,is_modify) values (?,?,?,?,?,?,?,?,?,?,?)");
							pst.setDate(1, dtCurrent1);
							pst.setInt(2, empId);
							pst.setInt(3, typeOFLeave);
							pst.setDouble(4, leaveId);
							pst.setDouble(5, dblLeaveDed);
							pst.setBoolean(6, isPaid1);
							pst.setDouble(7, dblBalance1);
							pst.setBoolean(8, true);
							pst.setString(9, "P");
							pst.setString(10, prefix_type);
							pst.setBoolean(11, false);
							pst.execute();
							pst.close();

							leave += dblLeaveDed;

						} else {
							break;
						}

					}

				}

				// =========================Prefix=====================
				if (!isSandwichLeave) {
					Calendar cal1 = (Calendar) cal.clone();
					while (true) {
						cal1.add(Calendar.DATE, -1);
						String strDate = cal1.get(Calendar.DATE) + "/"+ (cal1.get(Calendar.MONTH) + 1) + "/"+ cal1.get(Calendar.YEAR);
						Date dtCurrent1 = uF.getDateFormat(cal1.get(Calendar.DATE) + "/"+ (cal1.get(Calendar.MONTH) + 1) + "/"+ cal1.get(Calendar.YEAR), DATE_FORMAT);
						String day = uF.getDateFormat(strDate, DATE_FORMAT,"EEEE");

						if (!prefix.contains("-1") || !prefix.contains("-2")) {
							// System.out.println("prefixday==>"+day);
							boolean flag = false;

							boolean flag1 = false;

							String prefix_type = "";

							pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and emp_id=?");
							pst.setDate(1, dtCurrent1);
							pst.setInt(2, empId);
							rs = pst.executeQuery();
							while (rs.next()) {
								flag1 = true;
							}
							rs.close();
							pst.close();

							if (!flag1) {

								boolean flag2 = false;
								pst = con.prepareStatement("select * from leave_application_register where _date=?  and emp_id=?");
								pst.setDate(1, dtCurrent1);
								pst.setInt(2, empId);
								rs = pst.executeQuery();
								while (rs.next()) {
									flag2 = true;
								}
								rs.close();
								pst.close();

								if (!flag2) {
									if (!prefix.contains("-2")) {
										pst = con.prepareStatement("select * from holidays where _date=? and wlocation_id=(select wlocation_id from employee_official_details where emp_id=?) and (is_optional_holiday is null or is_optional_holiday=false)");
										pst.setDate(1, dtCurrent1);
										pst.setInt(2, empId);
										rs = pst.executeQuery();
										while (rs.next()) {
											flag = true;
											prefix_type = "H";
										}
										rs.close();
										pst.close();
									}

									if (!flag) {
										if (!prefix.contains("-1")) {

											if (weeklyoff1 != null && weeklyoff1.equalsIgnoreCase(day)) {
												if (weeklyoff1type != null && weeklyoff1type.equalsIgnoreCase("HD")) {
													dblLeaveDed = .5;
												} else {
													dblLeaveDed = 1;
												}
												prefix_type = "WO";
												flag = true;
											}
											if (!flag && weeklyoff2 != null && weeklyoff2.equalsIgnoreCase(day)) {
												if (weeklyoff2type != null && weeklyoff2type.equalsIgnoreCase("HD")) {
													dblLeaveDed = .5;
												} else {
													dblLeaveDed = 1;
												}
												prefix_type = "WO";
												flag = true;
											}
											if (!flag && weeklyoff3 != null && weeklyoff3.equalsIgnoreCase(day)) {
												int checkWeek = getMonthCont(uF, strDate);
												// System.out.println("prefix checkweek "+checkWeek);
												if (adDay.contains(""+ checkWeek)) {
													if (weeklyoff3type != null && weeklyoff3type.equalsIgnoreCase("HD")) {
														dblLeaveDed = .5;
													} else {
														dblLeaveDed = 1;
													}
													prefix_type = "WO";
													flag = true;
												}
											}
										}
									}
								}
							}
							if (flag) {

								// Date dtCurrent =
								// uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)
								// + 1)+"/"+cal.get(Calendar.YEAR),
								// DATE_FORMAT);
								if (isHalfday) {
									dblCount += 0.5;
								} else {

									if ((dblBalance - dblCount) == 0.5) {
										dblCount += 0.5;
									} else {
										dblCount++;
									}

								}

								if (dblBalance >= dblCount && isPaid) {
									isPaid1 = true;

									if (isHalfday) {
										dblBalance1 -= 0.5;
										dblLeaveDed = 0.5;
									} else {

										if (dblBalance1 >= 1) {
											dblBalance1 -= 1;
											dblLeaveDed = 1;
										} else if (dblBalance1 >= 0.5) {
											dblBalance1 -= 0.5;
											dblLeaveDed = 0.5;
										} else {
											dblLeaveDed = 0;
										}
									}
								} else {
									isPaid1 = false;
									if (isHalfday) {
										dblLeaveDed = 0.5;
									} else {
										dblLeaveDed = 1;
									}

								}

								if (isConstantBalance) {
									isPaid1 = true;
								}

								pst = con
										.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, is_paid, "
												+ "balance, _type,prefix_suffix,prefix_suffix_type,is_modify) values (?,?,?,?,?,?,?,?,?,?,?)");
								pst.setDate(1, dtCurrent1);
								pst.setInt(2, empId);
								pst.setInt(3, typeOFLeave);
								pst.setDouble(4, leaveId);
								pst.setDouble(5, dblLeaveDed);
								pst.setBoolean(6, isPaid1);
								pst.setDouble(7, dblBalance1);
								pst.setBoolean(8, true);
								pst.setString(9, "P");
								pst.setString(10, prefix_type);
								pst.setBoolean(11, false);
								pst.execute();
								pst.close();

								leave += dblLeaveDed;

							} else {
								break;
							}
						} else {
							break;
						}
					}
				}

				// ======================================================

				Calendar cal1 = (Calendar) cal2.clone();
				while (true) {
					cal1.add(Calendar.DATE, 1);
					String strDate = cal1.get(Calendar.DATE) + "/"
							+ (cal1.get(Calendar.MONTH) + 1) + "/"
							+ cal1.get(Calendar.YEAR);
					Date dtCurrent1 = uF.getDateFormat(
							cal1.get(Calendar.DATE) + "/"
									+ (cal1.get(Calendar.MONTH) + 1) + "/"
									+ cal1.get(Calendar.YEAR), DATE_FORMAT);
					String day = uF.getDateFormat(strDate, DATE_FORMAT, "EEEE");

					if (!suffix.contains("-1") || !suffix.contains("-2")) {
						// System.out.println("suffixday==>"+day);
						boolean flag = false;

						boolean flag1 = false;

						String prefix_type = "";

						pst = con
								.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and emp_id=?");
						pst.setDate(1, dtCurrent1);
						pst.setInt(2, empId);
						rs = pst.executeQuery();
						while (rs.next()) {
							flag1 = true;
						}
						rs.close();
						pst.close();

						if (!flag1) {

							boolean flag2 = false;
							pst = con
									.prepareStatement("select * from leave_application_register where _date=?  and emp_id=?");
							pst.setDate(1, dtCurrent1);
							pst.setInt(2, empId);
							rs = pst.executeQuery();
							while (rs.next()) {
								flag2 = true;
							}
							rs.close();
							pst.close();

							if (!flag2) {
								if (!suffix.contains("-2")) {
									pst = con
											.prepareStatement("select * from holidays where _date=? and wlocation_id=(select wlocation_id from employee_official_details where emp_id=?) and (is_optional_holiday is null or is_optional_holiday=false)");
									pst.setDate(1, dtCurrent1);
									pst.setInt(2, empId);

									rs = pst.executeQuery();
									while (rs.next()) {
										flag = true;
										prefix_type = "H";
									}
									rs.close();
									pst.close();
								}
							}
						}

						if (!flag) {
							if (!suffix.contains("-1")) {

								if (weeklyoff1 != null
										&& weeklyoff1.equalsIgnoreCase(day)) {
									if (weeklyoff1type != null
											&& weeklyoff1type
													.equalsIgnoreCase("HD")) {
										dblLeaveDed = .5;
									} else {
										dblLeaveDed = 1;
									}
									prefix_type = "WO";
									flag = true;
								}
								if (!flag && weeklyoff2 != null
										&& weeklyoff2.equalsIgnoreCase(day)) {
									if (weeklyoff2type != null
											&& weeklyoff2type
													.equalsIgnoreCase("HD")) {
										dblLeaveDed = .5;
									} else {
										dblLeaveDed = 1;
									}
									prefix_type = "WO";
									flag = true;
								}
								if (!flag && weeklyoff3 != null
										&& weeklyoff3.equalsIgnoreCase(day)) {
									int checkWeek = getMonthCont(
											new UtilityFunctions(), strDate);
									// System.out.println("suffix checkweek "+checkWeek);
									if (adDay.contains("" + checkWeek)) {
										if (weeklyoff3type != null
												&& weeklyoff3type
														.equalsIgnoreCase("HD")) {
											dblLeaveDed = .5;
										} else {
											dblLeaveDed = 1;
										}
										prefix_type = "WO";
										flag = true;
									}
								}
							}
						}

						if (flag) {

							// Date dtCurrent =
							// uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)
							// + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT);
							if (isHalfday) {
								dblCount += 0.5;
							} else {

								if ((dblBalance - dblCount) == 0.5) {
									dblCount += 0.5;
								} else {
									dblCount++;
								}

							}

							if (dblBalance >= dblCount && isPaid) {
								isPaid1 = true;

								if (isHalfday) {
									dblBalance1 -= 0.5;
									dblLeaveDed = 0.5;
								} else {

									if (dblBalance1 >= 1) {
										dblBalance1 -= 1;
										dblLeaveDed = 1;
									} else if (dblBalance1 >= 0.5) {
										dblBalance1 -= 0.5;
										dblLeaveDed = 0.5;
									} else {
										dblLeaveDed = 0;
									}
								}
							} else {
								isPaid1 = false;
								if (isHalfday) {
									dblLeaveDed = 0.5;
								} else {
									dblLeaveDed = 1;
								}

							}

							if (isConstantBalance) {
								isPaid1 = true;
							}

							pst = con
									.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, is_paid, "
											+ "balance, _type,prefix_suffix,prefix_suffix_type,is_modify) values (?,?,?,?,?,?,?,?,?,?,?)");
							pst.setDate(1, dtCurrent1);
							pst.setInt(2, empId);
							pst.setInt(3, typeOFLeave);
							pst.setDouble(4, leaveId);
							pst.setDouble(5, dblLeaveDed);
							pst.setBoolean(6, isPaid1);
							pst.setDouble(7, dblBalance1);
							pst.setBoolean(8, true);
							pst.setString(9, "S");
							pst.setString(10, prefix_type);
							pst.setBoolean(11, false);
							pst.execute();
							pst.close();

							leave += dblLeaveDed;

						} else {
							break;
						}
					} else {
						break;
					}
				}

				for (int i = 0; i < dblLeavesApproved; i++) {
					Date dtCurrent = uF.getDateFormat(
							cal.get(Calendar.DATE) + "/"
									+ (cal.get(Calendar.MONTH) + 1) + "/"
									+ cal.get(Calendar.YEAR), DATE_FORMAT);
					String strDate = cal.get(Calendar.DATE) + "/"
							+ (cal.get(Calendar.MONTH) + 1) + "/"
							+ cal.get(Calendar.YEAR);
					cal.add(Calendar.DATE, 1);

					if (isSandwichLeave) {

						boolean flag = false;

						if (sandwichleavetype.contains("-2")) {
							pst = con
									.prepareStatement("select * from holidays where _date=? and wlocation_id=(select wlocation_id from employee_official_details where emp_id=?) and (is_optional_holiday is null or is_optional_holiday=false)");
							pst.setDate(1, dtCurrent);
							pst.setInt(2, empId);
							rs = pst.executeQuery();

							while (rs.next()) {
								flag = true;
								break;
							}
							rs.close();
							pst.close();
							if (flag) {
								continue;
							}

						}

						if (sandwichleavetype.contains("-1")) {
							String day = uF.getDateFormat(strDate, DATE_FORMAT,
									"EEEE");
							// System.out.println("strDate===>"+strDate);

							if (weeklyoff1 != null
									&& weeklyoff1.equalsIgnoreCase(day)) {
								if (weeklyoff1type != null
										&& weeklyoff1type
												.equalsIgnoreCase("HD")) {
									dblLeaveDed = .5;
								} else {
									// dblLeaveDed=1;
									continue;
								}
								// flag=true;
							}
							if (weeklyoff2 != null
									&& weeklyoff2.equalsIgnoreCase(day)) {
								if (weeklyoff2type != null
										&& weeklyoff2type
												.equalsIgnoreCase("HD")) {
									dblLeaveDed = .5;
								} else {
									// dblLeaveDed=1;
									continue;
								}
								// flag=true;
							}
							if (weeklyoff3 != null
									&& weeklyoff3.equalsIgnoreCase(day)) {
								int checkWeek = getMonthCont(
										new UtilityFunctions(), strDate);

								if (adDay.contains("" + checkWeek)) {
									if (weeklyoff3type != null
											&& weeklyoff3type
													.equalsIgnoreCase("HD")) {
										dblLeaveDed = .5;
									} else {
										// dblLeaveDed=1;
										continue;
									}
								}
								// flag=true;
							}

						}

					}

					if (isHalfday) {
						dblCount += 0.5;
					} else {

						if ((dblBalance - dblCount) == 0.5) {
							dblCount += 0.5;
						} else {
							dblCount++;
						}

					}

					if (dblBalance >= dblCount && isPaid) {
						isPaid1 = true;

						if (isHalfday) {
							dblBalance1 -= 0.5;
							dblLeaveDed = 0.5;
						} else {

							if (dblBalance1 >= 1) {
								dblBalance1 -= 1;
								dblLeaveDed = 1;
							} else if (dblBalance1 >= 0.5) {
								dblBalance1 -= 0.5;
								dblLeaveDed = 0.5;
							} else {
								dblLeaveDed = 0;
							}
						}
					} else {
						isPaid1 = false;
						if (isHalfday) {
							dblLeaveDed = 0.5;
						} else {
							dblLeaveDed = 1;
						}

					}

					if (isConstantBalance) {
						isPaid1 = true;
					}

					pst = con
							.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, "
									+ "is_paid, balance, _type,is_modify) values (?,?,?,?,?,?,?,?,?)");
					pst.setDate(1, dtCurrent);
					pst.setInt(2, empId);
					pst.setInt(3, typeOFLeave);
					pst.setDouble(4, leaveId);
					pst.setDouble(5, dblLeaveDed);
					pst.setBoolean(6, isPaid1);
					pst.setDouble(7, dblBalance1);
					pst.setBoolean(8, true);
					pst.setBoolean(9, false);
					pst.execute();
					pst.close();

				}

				CF.updateLeaveRegister1(con, CF, uF, dblLeavesApproved + leave,
						0, typeOFLeave + "", empId + "");
			} else if (iscompensate) {
				// Needs to add a condition if manager approves leaves other the
				// dates for which employee has applied.

				if (isPaid) {
					pst = con
							.prepareStatement("UPDATE emp_leave_entry SET ispaid=true WHERE leave_id=?");
					pst.setInt(1, leaveId);
					pst.execute();
					pst.close();
				}

				pst = con
						.prepareStatement("select * from leave_register1 where emp_id = ? and leave_type_id=? and _date=? ");
				pst.setInt(1, empId);
				pst.setInt(2, typeOFLeave);
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				double dblTakenLeaves = 0;
				while (rs.next()) {
					dblTakenLeaves = rs.getDouble("taken_paid")
							+ rs.getDouble("taken_unpaid");
				}
				rs.close();
				pst.close();

				pst = con
						.prepareStatement("select * from leave_register1 lr,(select max(_date) as _date, leave_type_id, emp_id from leave_register1 where _date<= ? and emp_id = ? and leave_type_id=? group by leave_type_id, emp_id ) lr1 where lr1._date= lr._date and lr.emp_id = lr1.emp_id and lr.leave_type_id = lr1.leave_type_id  and lr.emp_id = ? and lr.leave_type_id=?");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(2, empId);
				pst.setInt(3, typeOFLeave);
				pst.setInt(4, empId);
				pst.setInt(5, typeOFLeave);

				rs = pst.executeQuery();
				double dblBalance = 0;
				while (rs.next()) {
					dblBalance = uF.parseToDouble(rs.getString("balance"));
				}
				rs.close();
				pst.close();

				// new
				pst = con
						.prepareStatement("update leave_register set taken_leaves=? where emp_id = ? and leave_type_id=? and from_date<=? and to_date>=? ");
				if (isHalfday) {
					pst.setDouble(1, (dblTakenLeaves + 0.5));
				} else {
					pst.setDouble(1, (dblTakenLeaves + uF.parseToInt(uF.dateDifference(startDate, DATE_FORMAT, endDate,DATE_FORMAT,CF.getStrTimeZone()))));
				}

				pst.setInt(2, empId);
				pst.setInt(3, typeOFLeave);
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();

				double dblLeavesApproved = 0;

				if (isHalfday) {
					dblLeavesApproved = 0.5;
				} else {
					dblLeavesApproved = uF.parseToInt(uF.dateDifference(startDate, DATE_FORMAT, endDate, DATE_FORMAT,CF.getStrTimeZone()));
				}

				dblLeavesApproved = 0;
				if (isHalfday) {
					dblLeavesApproved = 0.5;
				} else {
					dblLeavesApproved = uF.parseToInt(uF.dateDifference(startDate, DATE_FORMAT, endDate, DATE_FORMAT,CF.getStrTimeZone()));
				}

				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(
						startDate, DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(
						startDate, DATE_FORMAT, "MM")) - 1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(
						startDate, DATE_FORMAT, "yyyy")));

				double dblCount = 0;
				boolean isPaid1 = false;
				double dblLeaveDed = 0;
				double dblBalance1 = dblBalance;
				for (int i = 0; i < dblLeavesApproved; i++) {

					Date dtCurrent = uF.getDateFormat(
							cal.get(Calendar.DATE) + "/"
									+ (cal.get(Calendar.MONTH) + 1) + "/"
									+ cal.get(Calendar.YEAR), DATE_FORMAT);

					if (isHalfday) {
						dblCount += 0.5;
					} else {

						if ((dblBalance - dblCount) == 0.5) {
							dblCount += 0.5;
						} else {
							dblCount++;
						}

					}

					if (dblBalance >= dblCount && isPaid) {
						isPaid1 = true;

						if (isHalfday) {
							dblBalance1 += 0.5;
							dblLeaveDed = 0.5;
						} else {

							if (dblBalance1 >= 1) {
								dblBalance1 += 1;
								dblLeaveDed = 1;
							} else if (dblBalance1 >= 0.5) {
								dblBalance1 += 0.5;
								dblLeaveDed = 0.5;
							} else {
								dblLeaveDed = 0;
							}
						}
					} else {
						isPaid1 = false;
						if (isHalfday) {
							dblLeaveDed = 0.5;
						} else {
							dblLeaveDed = 1;
						}

					}

					pst = con
							.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, "
									+ "is_paid, balance, _type,is_modify) values (?,?,?,?,?,?,?,?,?)");
					pst.setDate(1, dtCurrent);
					pst.setInt(2, empId);
					pst.setInt(3, typeOFLeave);
					pst.setDouble(4, leaveId);
					pst.setDouble(5, dblLeaveDed);
					pst.setBoolean(6, isPaid1);
					pst.setDouble(7, dblBalance1);
					pst.setBoolean(8, true);
					pst.setBoolean(9, false);
					pst.execute();
					pst.close();

					cal.add(Calendar.DATE, 1);
				}

				CF.updateCompLeaveRegister1(con, CF, uF, dblLeavesApproved, 0,
						typeOFLeave + "", empId + "");

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public int getMonthCont(UtilityFunctions uF, String strDate) {

		Calendar mycal = Calendar.getInstance();
		mycal.setTime(uF.getDateFormat(strDate, DATE_FORMAT));

		java.util.Date d1 = uF.getDateFormatUtil(
				"01" + "/" + (mycal.get(Calendar.MONTH) + 1) + "/"
						+ mycal.get(Calendar.YEAR), DATE_FORMAT);
		java.util.Date d2 = uF.getDateFormatUtil(strDate, DATE_FORMAT);

		int cnt = 0;
		while (d1.compareTo(d2) <= 0) {
			cnt++;
			mycal.add(Calendar.DATE, -7);
			d2 = mycal.getTime();
			if (cnt == 10) {
				break;
			}
		}

		return cnt;

	}

	public String getStringValue(String str) {
		try {
//			System.out.println("str ===>> " + str);
			boolean numeric = true;
	        numeric = str.matches("-?\\d+(\\.\\d+)?");
			if(numeric) {
				str = String.valueOf(Double.valueOf(str).longValue());
			}
			str = str.trim();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return str;
	}
	
	private void updateBreakRegisters(UtilityFunctions uF, Connection con,
			Map<String, String> hmEmpLevelMap, String strMode, String strDate2,
			String strStart, long lIn, String strActualTime,
			String strWLocation, int strEmpId) {
		PreparedStatement pst = null;
		ResultSet rs =null;
		try {

			String[] arr = CF.getCurrentPayCycle(con, CF.getStrTimeZone(),
					uF.getDateFormatUtil(strDate2, DBDATE), CF);

			Map hmBreakBalance = new HashMap();
			Map hmBreakTaken = new HashMap();
			Map hmBreakUnPaid = new HashMap();

			String levelid = hmEmpLevelMap.get(strEmpId + "");

			// PreparedStatement pst =
			// con.prepareStatement("select a.emp_id, br.balance, a.break_type_id, br.taken_paid, br.taken_unpaid from break_register br, ( select max(_date) as _date,emp_id, break_type_id from break_register where _date <= ? group by emp_id,break_type_id ) a where br._date = a._date and br.emp_id = a.emp_id and br.break_type_id = a.break_type_id and a.emp_id = ?");
			/*
			 * PreparedStatement pst = con.prepareStatement(
			 * "select a.emp_id, br.balance, a.break_type_id, br.taken_paid, br.taken_unpaid from "
			 * +
			 * "break_register br, ( select max(register_id) as register_id,emp_id, break_type_id from break_register where _date <= ? "
			 * +
			 * "group by emp_id,break_type_id ) a join leave_break_type lbt on lbt.break_type_id=a.break_type_id where br.register_id = a.register_id and br.emp_id = a.emp_id and br.break_type_id = a.break_type_id "
			 * + "and a.emp_id = ?"); // pst.setDate(1,
			 * uF.getCurrentDate(CF.getStrTimeZone())); // pst.setDate(1,
			 * uF.getCurrentDate("2013-12-31")); pst.setDate(1,
			 * uF.getDateFormat(arr[1], DATE_FORMAT)); pst.setInt(2,
			 * uF.parseToInt(strEmpId)); ResultSet rs = pst.executeQuery();
			 * while(rs.next()){
			 * hmBreakBalance.put(rs.getString("break_type_id"),
			 * rs.getString("balance"));
			 * hmBreakTaken.put(rs.getString("break_type_id"),
			 * rs.getString("taken_paid"));
			 * hmBreakUnPaid.put(rs.getString("break_type_id"),
			 * rs.getString("taken_unpaid")); }
			 * System.out.println("=======>"+pst);
			 */

			Map<String, String> hmEmpBreakTaken = new HashMap<String, String>();
			// pst =
			// con.prepareStatement("select break_type_id,sum(leave_no)as no_of_leaves from break_application_register where emp_id=? and is_paid = true group by break_type_id");
			/*
			 * pst = con.prepareStatement(
			 * "select a.break_type_id,sum(leave_no)as no_of_leaves from break_application_register a join leave_break_type "
			 * +
			 * "lbt on lbt.break_type_id=a.break_type_id where emp_id=? and is_paid = true group by a.break_type_id "
			 * );
			 */
			pst = con
					.prepareStatement("select a.break_type_id,sum(leave_no)as no_of_leaves from break_application_register a "
							+ " join leave_break_type lbt on lbt.break_type_id=a.break_type_id where emp_id=? and is_paid = true "
							+ " and _date between ? and ? group by a.break_type_id");
			pst.setInt(1, strEmpId);
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmEmpBreakTaken.put(rs.getString("break_type_id"),
						rs.getString("no_of_leaves"));
				hmBreakTaken.put(rs.getString("break_type_id"),
						rs.getString("no_of_leaves"));
			}
			rs.close();
			pst.close();

			// pst =
			// con.prepareStatement("select a.break_type_id,days from break_policy a,emp_leave_break_type elt where a.break_type_id=elt.break_type_id and a.wlocation_id=elt.wlocation_id and a.wlocation_id=? and level_id=?");
			pst = con
					.prepareStatement("select a.break_type_id,days from (select a.break_type_id,days from break_policy a,emp_leave_break_type elt "
							+ " where a.break_type_id=elt.break_type_id and a.wlocation_id=elt.wlocation_id and a.wlocation_id=? and level_id=?) as a "
							+ " join leave_break_type lbt on lbt.break_type_id=a.break_type_id");
			pst.setInt(1, uF.parseToInt(strWLocation));
			pst.setInt(2, uF.parseToInt(levelid));
			rs = pst.executeQuery();
			while (rs.next()) {
				double totalBalance = uF.parseToDouble(rs.getString("days"))
						- uF.parseToDouble(hmEmpBreakTaken.get(rs
								.getString("break_type_id")));
				hmBreakBalance.put(rs.getString("break_type_id"), ""
						+ totalBalance);
				/*
				 * hmBreakTaken.put(rs.getString("break_type_id"),
				 * rs.getString("taken_paid"));
				 * hmBreakUnPaid.put(rs.getString("break_type_id"),
				 * rs.getString("taken_unpaid"));
				 */
			}
			rs.close();
			pst.close();

			long lIn1 = uF.getTimeFormat(strDate2 + strStart, DBDATE + DBTIME)
					.getTime();
			long tDiff = (lIn - lIn1);

			if (strMode != null && strMode.equalsIgnoreCase("IN")) {
				tDiff = (lIn - lIn1);
			} else {
				// tDiff = (lIn1 - lIn);
				tDiff = (lIn - lIn1);
			}

			long diffMinutes = 0;
			if (tDiff > 0 || tDiff < 0) {
				long diffHours = tDiff / (1000 * 60 * 60);
				// diffMinutes = (tDiff % (1000 * 60 * 60)) / (1000 * 60);
				diffMinutes = Math.abs((tDiff) / 60000);
			}

			if ((strMode.equalsIgnoreCase("IN") && tDiff < 0)
					|| strMode.equalsIgnoreCase("OUT") && tDiff > 0) {
				return;
			}

			// pst =
			// con.prepareStatement("select * from  break_policy where wlocation_id = ? and time_value>=? and _mode like ?  order by time_value limit 1");
			pst = con
					.prepareStatement("select * from  break_policy a join leave_break_type lbt on lbt.break_type_id=a.break_type_id "
							+ "where a.wlocation_id = ? and a.time_value>=? and a._mode like ?  order by a.time_value limit 1");
			pst.setInt(1, uF.parseToInt(strWLocation));
			pst.setDouble(2, diffMinutes);
			pst.setString(3, "%" + strMode + "%");
			rs = pst.executeQuery();
			String strBreakPolicyId = null;
			String strTimeValue = null;
			boolean isAvailable = false;

			while (rs.next()) {
				strBreakPolicyId = rs.getString("break_type_id");
				strTimeValue = rs.getString("time_value");

				isAvailable = true;
			}
			rs.close();
			pst.close();

			double dblBalance = uF.parseToDouble((String) hmBreakBalance
					.get(strBreakPolicyId));
			double dblTakenPaid = uF.parseToDouble((String) hmBreakTaken
					.get(strBreakPolicyId));
			double dblTakenUnPaid = uF.parseToDouble((String) hmBreakUnPaid
					.get(strBreakPolicyId));

			int k = 0;
			for (k = 0; k < 5 && dblBalance == 0 && isAvailable; k++) {

				if (dblBalance == 0) {
					// pst =
					// con.prepareStatement("select * from  break_policy where wlocation_id = ? and time_value > ? and _mode like ? order by time_value limit 1");
					pst = con
							.prepareStatement("select * from  break_policy a join leave_break_type lbt on lbt.break_type_id=a.break_type_id "
									+ "where a.wlocation_id = ? and a.time_value>=? and a._mode like ?  order by a.time_value limit 1");
					pst.setInt(1, uF.parseToInt(strWLocation));
					pst.setDouble(2, uF.parseToDouble(strTimeValue));
					pst.setString(3, "%" + strMode + "%");
					rs = pst.executeQuery();
					while (rs.next()) {
						strBreakPolicyId = rs.getString("break_type_id");
						strTimeValue = rs.getString("time_value");
					}
					rs.close();
					pst.close();

					dblBalance = uF.parseToDouble((String) hmBreakBalance
							.get(strBreakPolicyId));
					dblTakenPaid = uF.parseToDouble((String) hmBreakTaken
							.get(strBreakPolicyId));
					dblTakenUnPaid = uF.parseToDouble((String) hmBreakUnPaid
							.get(strBreakPolicyId));

				}

			}

			if (diffMinutes < 120 && dblBalance == 0) {

				strBreakPolicyId = "-2";
				dblBalance = uF.parseToDouble((String) hmBreakBalance
						.get(strBreakPolicyId));
				dblTakenPaid = uF.parseToDouble((String) hmBreakTaken
						.get(strBreakPolicyId));
				dblTakenUnPaid = uF.parseToDouble((String) hmBreakUnPaid
						.get(strBreakPolicyId));

				dblTakenUnPaid += 1;
				dblTakenPaid = 0;

			} else if (dblBalance == 0) {
				strBreakPolicyId = "-1";
				dblBalance = uF.parseToDouble((String) hmBreakBalance
						.get(strBreakPolicyId));
				dblTakenPaid = uF.parseToDouble((String) hmBreakTaken
						.get(strBreakPolicyId));
				dblTakenUnPaid = uF.parseToDouble((String) hmBreakUnPaid
						.get(strBreakPolicyId));

				dblTakenUnPaid += 1;
				dblTakenPaid = 0;
			} else {
				dblTakenPaid += 1;
				dblTakenUnPaid = 0;
			}

			pst = con
					.prepareStatement("insert into break_application_register (_date, emp_id, break_type_id, leave_no, is_paid, balance, _type) values (?,?,?,?,?,?,?)");
			pst.setDate(1, uF.getDateFormat(strDate2, DBDATE));
			pst.setInt(2, strEmpId);
			pst.setInt(3, uF.parseToInt(strBreakPolicyId));
			pst.setInt(4, 1);
			if (dblBalance == 0) {
				pst.setBoolean(5, false);
			} else {
				pst.setBoolean(5, true);
			}

			if (dblBalance > 0) {
				pst.setDouble(6, (dblBalance - 1));
			} else {
				pst.setDouble(6, dblBalance);
			}
			pst.setString(7, strMode);
			pst.execute();
			pst.close();

			pst = con.prepareStatement("update break_register set taken_paid =?,taken_unpaid =?, balance=? where break_type_id =? and _date=? and emp_id =?");
			pst.setDouble(1, (dblTakenPaid));
			pst.setDouble(2, (dblTakenUnPaid));

			if (dblBalance > 0) {
				pst.setDouble(3, (dblBalance - 1));
			} else {
				pst.setDouble(3, dblBalance);
			}

			pst.setInt(4, uF.parseToInt(strBreakPolicyId));
			pst.setDate(5, uF.getDateFormat(strDate2, DBDATE));
			pst.setInt(6, strEmpId);
			int x = pst.executeUpdate();
			pst.close();

			if (x == 0) {
				pst = con.prepareStatement("insert into break_register (_date, emp_id, taken_paid, balance, taken_unpaid, break_type_id) values (?,?,?,?,?,?)");

				pst.setDate(1, uF.getDateFormat(strDate2, DBDATE));
				pst.setInt(2, strEmpId);
				pst.setDouble(3, dblTakenPaid);
				if (dblBalance > 0) {
					pst.setDouble(4, (dblBalance - 1));
				} else {
					pst.setDouble(4, dblBalance);
				}

				pst.setDouble(5, dblTakenUnPaid);
				pst.setInt(6, uF.parseToInt(strBreakPolicyId));
				pst.execute();
				pst.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// private void format7Attendance(File path) {
	// Connection con = null;
	// PreparedStatement pst = null;
	// ResultSet rs =null;
	// Database db = new Database();
	// UtilityFunctions uF = new UtilityFunctions();
	// List alReport = new ArrayList();
	//
	// try {
	// con = db.makeConnection(con);
	//
	//
	//
	//
	// FileInputStream fis = new FileInputStream(path);
	// XSSFWorkbook workbook = new XSSFWorkbook(fis);
	// System.out.println("Start Reading Excelsheet.... ");
	// XSSFSheet attendanceSheet = workbook.getSheetAt(0);
	//
	// List<String> dateList = new ArrayList<String>();
	// List<List<String>> outerList=new ArrayList<List<String>>();
	//
	// Iterator rows = attendanceSheet.rowIterator();
	// int l=0;
	// while (rows.hasNext()) {
	//
	// XSSFRow row = (XSSFRow) rows.next();
	// Iterator cells = row.cellIterator();
	// if(l==0){
	// while (cells.hasNext()) {
	// String cell = cells.next().toString();
	// dateList.add(cell);
	// }
	// l++;
	// continue;
	// }
	// List<String> cellList = new ArrayList<String>();
	// while (cells.hasNext()) {
	// cellList.add(cells.next().toString());
	// }
	// outerList.add(cellList);
	//
	// }
	//
	//
	// Map<String,String> empMp=new HashMap<String,String>();
	// Map<String,String> empOrgMp=new HashMap<String,String>();
	// Map<String,String> empServiceMp=new HashMap<String,String>();
	//
	// pst =
	// con.prepareStatement("select emp_per_id,empcode,org_id,service_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id");
	// rs = pst.executeQuery();
	// while (rs.next()) {
	// empMp.put(rs.getString("empcode"),rs.getString("emp_per_id"));
	// empOrgMp.put(rs.getString("emp_per_id"),rs.getString("org_id"));
	// empServiceMp.put(rs.getString("emp_per_id"),rs.getString("service_id"));
	//
	// }
	//
	// // pst =
	// con.prepareStatement("select emp_per_id,empcode,org_id,service_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id");
	// // rs = pst.executeQuery();
	// // while (rs.next()) {
	// // empMp.put(rs.getString("empcode"),rs.getString("emp_per_id"));
	// // empOrgMp.put(rs.getString("emp_per_id"),rs.getString("org_id"));
	// //
	// empServiceMp.put(rs.getString("emp_per_id"),rs.getString("service_id"));
	// //
	// // }
	//
	//
	// for (int k=0;k<outerList.size();k++) {
	// List<String> innerList=outerList.get(k);
	// String empcode=innerList.get(1);
	// // System.out.println("empcode==="+empcode);
	// // int emp_per_id = 0;
	// int servic_id = 0;
	// double actual_hours = 0.0;
	// String orgId=null;
	//
	// if (empcode.contains(".")) {
	// empcode = empcode.substring(0, empcode.indexOf("."));
	// // System.out.println("emp code case 6====>??"+empcode);
	// }
	//
	// // Select Employ ID
	// // pst =
	// con.prepareStatement("Select emp_per_id from employee_personal_details where empcode=?");
	// // pst.setString(1, empcode);
	// // rs = pst.executeQuery();
	// // while (rs.next()) {
	// // emp_per_id = rs.getInt(1);
	// // }
	// int emp_per_id = uF.parseToInt(empMp.get(empcode));
	//
	// // System.out.println("emp_per_id==="+emp_per_id);
	//
	// if(emp_per_id==0){
	// continue;
	// }
	// orgId=empOrgMp.get(emp_per_id+"");
	// servic_id = uF.parseToInt(empServiceMp.get(emp_per_id+""));
	// //
	// ===========================================================================================================
	// // pst =
	// con.prepareStatement("Select service_id,org_id from employee_official_details where emp_id=?");
	// // pst.setInt(1, emp_per_id);
	// // rs = pst.executeQuery();
	// // if (rs.next()) {
	// // orgId=rs.getString("org_id");
	// // String temp = rs.getString(1);
	// // if(temp==null || temp.equals("") || temp.equals(",")){
	// // servic_id = 0;
	// // }else if (temp.contains(",")) {
	// // String str[] = temp.split(",");
	// // servic_id = uF.parseToInt(str[0]);
	// // } else {
	// // servic_id = uF.parseToInt(rs.getString(1));
	// // }
	// // }
	// // System.out.println("orgId==="+orgId);
	// for(int j=2;j<innerList.size();j++){
	//
	// String dataType=innerList.get(j);
	// // System.out.println("dataType====>"+dataType+"=====");
	//
	//
	// if(dataType.equalsIgnoreCase("P") || dataType.equalsIgnoreCase("HD")){
	//
	// pst =
	// con.prepareStatement("Select roster_id from roster_details where emp_id=? and _date=?");
	// pst.setInt(1, emp_per_id);
	// pst.setDate(2, getDate(dateList.get(j)));
	// rs = pst.executeQuery();
	// if (rs.next()) {
	// // System.out.println("Roster Detail Already Exists======>");
	// } else {
	// // Integer user_id = null;
	// String workInTime=null;
	// String workOuttime=null;
	// pst =
	// con.prepareStatement("select * from work_location_info where wlocation_id=(select wlocation_id from employee_official_details where emp_id=?)");
	// pst.setInt(1, emp_per_id);
	// rs = pst.executeQuery();
	// if (rs.next()) {
	// workInTime=rs.getString("wlocation_start_time");
	// workOuttime=rs.getString("wlocation_end_time");
	//
	// }
	// // System.out.println("cellList.get(j)====>"+innerList.get(j));
	// // if(innerList.get(j).equalsIgnoreCase("p"))
	// // {
	//
	// pst=con.prepareStatement("Insert into roster_details (emp_id," +
	// "_date," +
	// "_from," +
	// "_to," +
	// "isapproved," +
	// "user_id," +
	// "service_id," +
	// "actual_hours," +
	// "attended," +
	// "is_lunch_ded," +
	// "shift_id," +
	// "entry_date)" +
	// "values(?,?,?,?,?,?,?,?,?,?,?,?)");
	//
	// pst.setInt(1,emp_per_id);
	// pst.setDate(2,getDate(dateList.get(j)));
	// pst.setTime(3, new java.sql.Time(uF.getDateFormat(workInTime,
	// DBTIME).getTime()));
	// pst.setTime(4, new java.sql.Time(uF.getDateFormat(workOuttime,
	// DBTIME).getTime()));
	// pst.setBoolean(5, false);
	// pst.setInt(6,310);
	// pst.setInt(7,servic_id); //service id
	// pst.setDouble(8,8);
	// pst.setInt(9,0);
	// pst.setBoolean(10, false);
	// pst.setInt(11,0);
	// pst.setDate(12, new java.sql.Date(System.currentTimeMillis()));
	//
	// //System.out.println(" in roster else roster query is=====>"+pst);
	// pst.executeUpdate();
	// // System.out.println("Successfull Roster ");
	//
	//
	//
	// // }
	//
	// }
	// pst=con.prepareStatement("Select atten_id,in_out from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=?");
	// pst.setInt(1,emp_per_id);
	// pst.setDate(2,getDate(dateList.get(j)));
	// rs=pst.executeQuery();
	// int i=0;
	// while(rs.next())
	// {
	// i++;
	// }
	// // System.out.println("i===>>"+i);
	// if(i>=2){
	//
	// }else{
	// String _fromTime=null;
	// String _toTime=null;
	//
	// pst=con.prepareStatement("select _from,_to,service_id,actual_hours from roster_details where emp_id=? and _date=?");
	// pst.setInt(1, emp_per_id);
	// pst.setDate(2,getDate(dateList.get(j)));
	// rs=pst.executeQuery();
	// while(rs.next())
	// {
	// _fromTime=rs.getString(1);
	// // System.out.println("From time is true flag"+ _fromTime);
	// _toTime=rs.getString(2);
	// servic_id=rs.getInt(3);
	// actual_hours=rs.getDouble(4);
	// }
	//
	//
	//
	// if(i==0){
	// //IN CODING
	// pst=con.prepareStatement("insert into attendance_details(emp_id," +
	// "in_out_timestamp," +
	// "reason," +
	// "in_out," +
	// "approved," +
	// "comments," +
	// "hours_worked," +
	// "in_out_timestamp_actual," +
	// "service_id)values(?,?,?,?,?,?,?,?,?)");
	//
	// pst.setInt(1, emp_per_id);
	// pst.setString(3," ");
	// pst.setTimestamp(2,getTimeStamp(dateList.get(j),_fromTime));
	// // System.out.println("Time stamp returned "+
	// getTimeStamp(dateList.get(j),_fromTime));
	// pst.setString(4,"IN");
	// pst.setNull(7, java.sql.Types.DOUBLE);
	// pst.setTimestamp(8,getTimeStamp(dateList.get(j),_fromTime));
	// pst.setInt(5,1);
	// pst.setString(6," ");
	// pst.setInt(9,servic_id);
	//
	// pst.execute();
	// }
	// //OUT CODING
	// pst=con.prepareStatement("insert into attendance_details(emp_id," +
	// "in_out_timestamp," +
	// "reason," +
	// "in_out," +
	// "approved," +
	// "comments," +
	// "hours_worked," +
	// "in_out_timestamp_actual," +
	// "service_id)values(?,?,?,?,?,?,?,?,?)");
	//
	// pst.setInt(1, emp_per_id);
	// pst.setString(3," ");
	//
	// pst.setString(4,"OUT");
	// if(dataType.equalsIgnoreCase("HD")){
	// pst.setDouble(7,4);
	// SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	// java.util.Date myDate =
	// parser.parse(dateList.get(j)+" "+_fromTime.toString());
	// Calendar cal =Calendar.getInstance();
	// cal.setTime(myDate);
	// cal.add(Calendar.HOUR_OF_DAY,4); // this will add two hours
	// myDate = cal.getTime();
	// pst.setTimestamp(2,new java.sql.Timestamp(cal.getTime().getTime()));
	// }else{
	// pst.setTimestamp(2,getTimeStamp(dateList.get(j),_toTime));
	// pst.setDouble(7,actual_hours);
	// }
	//
	// pst.setTimestamp(8,getTimeStamp(dateList.get(j),_toTime));
	// pst.setInt(5,1);
	// pst.setString(6," ");
	// pst.setInt(9,servic_id);
	//
	// pst.execute();
	// alReport.add(empcode);
	// }
	//
	// }
	//
	// if(dataType.equalsIgnoreCase("PL") || dataType.equalsIgnoreCase("CL") ||
	// dataType.equalsIgnoreCase("SL") || dataType.equalsIgnoreCase("WO")){
	// pst=con.prepareStatement("select * from leave_type where leave_type_code=? and org_id=? ");
	// pst.setString(1, dataType);
	// pst.setInt(2,uF.parseToInt(orgId));
	// rs=pst.executeQuery();
	// String leaveId=null;
	// while(rs.next()){
	// leaveId=rs.getString("leave_type_id");
	// }
	//
	//
	// if(leaveId!=null){
	// pst=con.prepareStatement("insert into emp_leave_entry(emp_id," +
	// "leave_from," +
	// "leave_to," +
	// "entrydate," +
	// "emp_no_of_leave," +
	// "leave_type_id," +
	// "is_approved," +
	// "user_id," +
	// "approval_from," +
	// "approval_to_date," +
	// "encashment_status," +
	// "ishalfday," +
	// "ispaid,is_compensate" +
	// ")values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	//
	// pst.setInt(1, emp_per_id);
	// pst.setDate(2, uF.getDateFormat(dateList.get(j), "dd/MM/yyyy"));
	// pst.setDate(3, uF.getDateFormat(dateList.get(j), "dd/MM/yyyy"));
	// pst.setDate(4, uF.getDateFormat(dateList.get(j), "dd/MM/yyyy"));
	// //if(dataType.equalsIgnoreCase("EL") || dataType.equalsIgnoreCase("A")){
	// pst.setDouble(5, 1);
	// /*}else{
	// pst.setDouble(5, 0.5);
	// }*/
	//
	// pst.setInt(6, uF.parseToInt(leaveId));
	// pst.setInt(7, 1);
	// pst.setInt(8,uF.parseToInt((String) session.getAttribute(EMPID)));
	// pst.setDate(9, uF.getDateFormat(dateList.get(j), "dd/MM/yyyy"));
	// pst.setDate(10, uF.getDateFormat(dateList.get(j), "dd/MM/yyyy"));
	// pst.setBoolean(11, false);
	// pst.setBoolean(12, false);
	// pst.setBoolean(13, true);
	// pst.setBoolean(14, false);
	// int x=pst.executeUpdate();
	//
	// if(x>0){
	// pst=con.prepareStatement("select max(leave_id)as leave_id  from emp_leave_entry");
	// rs=pst.executeQuery();
	// String leave_id=null;
	// while(rs.next()){
	// leave_id=rs.getString("leave_id");
	// }
	// insertLeaveBalance(con,pst,rs,emp_per_id,uF.parseToInt(leaveId),dateList.get(j),dataType,leave_id);
	// }
	// }
	// }
	// }
	//
	// }
	//
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// // return FALSE;
	// }finally{
	// db.closeResultSet(rs);
	// db.closeStatements(pst);
	// db.closeConnection(con);
	// }
	// // return TRUE;
	// }

	/*
	 * public String insertAttendanceDetails(File path) { Connection con = null;
	 * Database db = new Database(); UtilityFunctions uF = new
	 * UtilityFunctions(); List alReport = new ArrayList(); ResultSet rs =null;
	 * try { con = db.makeConnection(con);
	 * 
	 * 
	 * PreparedStatement pst = null; java.sql.Time _fromTime=null; java.sql.Time
	 * _toTime=null;
	 * 
	 * FileInputStream fis = new FileInputStream(path); XSSFWorkbook workbook =
	 * new XSSFWorkbook(fis);
	 * System.out.println("Start Reading Excelsheet.... "); XSSFSheet
	 * attendanceSheet = workbook.getSheetAt(0);
	 * 
	 * List<String> dateList = new ArrayList<String>(); List<List<String>>
	 * outerList=new ArrayList<List<String>>();
	 * 
	 * Iterator rows = attendanceSheet.rowIterator(); int l=0; while
	 * (rows.hasNext()) {
	 * 
	 * XSSFRow row = (XSSFRow) rows.next(); Iterator cells = row.cellIterator();
	 * if(l==0){ while (cells.hasNext()) { String cell =
	 * cells.next().toString(); dateList.add(cell); } l++; continue; }
	 * List<String> cellList = new ArrayList<String>(); while (cells.hasNext())
	 * { cellList.add(cells.next().toString()); } outerList.add(cellList);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * for (int k=0;k<outerList.size();k++) { List<String>
	 * innerList=outerList.get(k); String empcode=innerList.get(1);
	 * System.out.println("empcode==="+empcode); int emp_per_id = 0; int
	 * servic_id = 0; double actual_hours = 0.0; String orgId=null;
	 * 
	 * if (empcode.contains(".")) { empcode = empcode.substring(0,
	 * empcode.indexOf(".")); //
	 * System.out.println("emp code case 6====>??"+empcode); }
	 * 
	 * // Select Employ ID pst = con.prepareStatement(
	 * "Select emp_per_id from employee_personal_details where empcode=?");
	 * pst.setString(1, empcode); rs = pst.executeQuery(); while (rs.next()) {
	 * emp_per_id = rs.getInt(1); }
	 * System.out.println("emp_per_id==="+emp_per_id);
	 * 
	 * //
	 * ========================================================================
	 * =================================== pst = con.prepareStatement(
	 * "Select service_id,org_id from employee_official_details where emp_id=?"
	 * ); pst.setInt(1, emp_per_id); rs = pst.executeQuery(); if (rs.next()) {
	 * orgId=rs.getString("org_id"); String temp = rs.getString(1);
	 * if(temp==null || temp.equals("") || temp.equals(",")){ servic_id = 0;
	 * }else if (temp.contains(",")) { String str[] = temp.split(","); //
	 * servic_id = Integer.parseInt(str[0]); servic_id = uF.parseToInt(str[0]);
	 * } else { // servic_id = Integer.parseInt(rsSid.getString(1)); servic_id =
	 * uF.parseToInt(rs.getString(1)); } }
	 * 
	 * for(int j=2;j<innerList.size();j++){
	 * 
	 * String dataType=innerList.get(j);
	 * System.out.println("dataType====>"+dataType+"====="); boolean
	 * isLeaveBalance=false; if(dataType.equalsIgnoreCase("A")){
	 * isLeaveBalance=checkLeaveBalance(con,pst,rs,emp_per_id,uF); }
	 * 
	 * if((dataType.equalsIgnoreCase("A") && isLeaveBalance) ||
	 * dataType.equalsIgnoreCase("P") || dataType.equalsIgnoreCase("HD")
	 * ||dataType.equalsIgnoreCase("EL") || dataType.equalsIgnoreCase("HD/EL")){
	 * 
	 * pst = con.prepareStatement(
	 * "Select roster_id from roster_details where emp_id=? and _date=?");
	 * pst.setInt(1, emp_per_id); pst.setDate(2, getDate(dateList.get(j))); rs =
	 * pst.executeQuery(); if (rs.next()) {
	 * System.out.println("Roster Detail Already Exists======>"); } else { //
	 * Integer user_id = null; String workInTime=null; String workOuttime=null;
	 * pst = con.prepareStatement(
	 * "select * from work_location_info where wlocation_id=(select wlocation_id from employee_official_details where emp_id=?)"
	 * ); pst.setInt(1, emp_per_id); rs = pst.executeQuery(); if (rs.next()) {
	 * workInTime=rs.getString("wlocation_start_time");
	 * workOuttime=rs.getString("wlocation_end_time");
	 * 
	 * } System.out.println("cellList.get(j)====>"+innerList.get(j)); //
	 * if(innerList.get(j).equalsIgnoreCase("p")) // {
	 * 
	 * pst=con.prepareStatement("Insert into roster_details (emp_id," + "_date,"
	 * + "_from," + "_to," + "isapproved," + "user_id," + "service_id," +
	 * "actual_hours," + "attended," + "is_lunch_ded," + "shift_id," +
	 * "entry_date)" + "values(?,?,?,?,?,?,?,?,?,?,?,?)");
	 * 
	 * pst.setInt(1,emp_per_id); pst.setDate(2,getDate(dateList.get(j)));
	 * pst.setTime(3, new java.sql.Time(uF.getDateFormat(workInTime,
	 * DBTIME).getTime())); pst.setTime(4, new
	 * java.sql.Time(uF.getDateFormat(workOuttime, DBTIME).getTime()));
	 * pst.setBoolean(5, false); pst.setInt(6,310); pst.setInt(7,servic_id);
	 * //service id pst.setDouble(8,8); pst.setInt(9,0); pst.setBoolean(10,
	 * false); pst.setInt(11,0); pst.setDate(12, new
	 * java.sql.Date(System.currentTimeMillis()));
	 * 
	 * //System.out.println(" in roster else roster query is=====>"+pst);
	 * pst.executeUpdate(); System.out.println("Successfull Roster ");
	 * 
	 * 
	 * 
	 * // }
	 * 
	 * } pst=con.prepareStatement(
	 * "Select atten_id,in_out from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')=?"
	 * ); pst.setInt(1,emp_per_id); pst.setDate(2,getDate(dateList.get(j)));
	 * rs=pst.executeQuery(); int i=0; while(rs.next()) { i++; }
	 * System.out.println("i===>>"+i); if(i>=2){
	 * 
	 * }else{
	 * 
	 * pst=con.prepareStatement(
	 * "select _from,_to,service_id,actual_hours from roster_details where emp_id=? and _date=?"
	 * ); pst.setInt(1, emp_per_id); pst.setDate(2,getDate(dateList.get(j)));
	 * rs=pst.executeQuery(); while(rs.next()) { _fromTime=rs.getTime(1);
	 * System.out.println("From time is true flag"+ _fromTime);
	 * _toTime=rs.getTime(2); servic_id=rs.getInt(3);
	 * actual_hours=rs.getDouble(4); }
	 * 
	 * 
	 * 
	 * if(i==0){ //IN CODING
	 * pst=con.prepareStatement("insert into attendance_details(emp_id," +
	 * "in_out_timestamp," + "reason," + "in_out," + "approved," + "comments," +
	 * "hours_worked," + "in_out_timestamp_actual," +
	 * "service_id)values(?,?,?,?,?,?,?,?,?)");
	 * 
	 * pst.setInt(1, emp_per_id); pst.setString(3," ");
	 * pst.setTimestamp(2,getTimeStamp(dateList.get(j),_fromTime));
	 * System.out.println("Time stamp returned "+
	 * getTimeStamp(dateList.get(j),_fromTime)); pst.setString(4,"IN");
	 * pst.setNull(7, java.sql.Types.DOUBLE);
	 * pst.setTimestamp(8,getTimeStamp(dateList.get(j),_fromTime));
	 * pst.setInt(5,1); pst.setString(6," "); pst.setInt(9,servic_id);
	 * 
	 * pst.execute(); } //OUT CODING
	 * pst=con.prepareStatement("insert into attendance_details(emp_id," +
	 * "in_out_timestamp," + "reason," + "in_out," + "approved," + "comments," +
	 * "hours_worked," + "in_out_timestamp_actual," +
	 * "service_id)values(?,?,?,?,?,?,?,?,?)");
	 * 
	 * pst.setInt(1, emp_per_id); pst.setString(3," ");
	 * 
	 * pst.setString(4,"OUT"); if(dataType.equalsIgnoreCase("HD") ||
	 * dataType.equalsIgnoreCase("HD/EL")){ pst.setDouble(7,4); SimpleDateFormat
	 * parser = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); java.util.Date
	 * myDate = parser.parse(dateList.get(j)+" "+_fromTime.toString()); Calendar
	 * cal =Calendar.getInstance(); cal.setTime(myDate);
	 * cal.add(Calendar.HOUR_OF_DAY,4); // this will add two hours myDate =
	 * cal.getTime(); pst.setTimestamp(2,new
	 * java.sql.Timestamp(cal.getTime().getTime())); }else{
	 * pst.setTimestamp(2,getTimeStamp(dateList.get(j),_toTime));
	 * pst.setDouble(7,actual_hours); }
	 * 
	 * pst.setTimestamp(8,getTimeStamp(dateList.get(j),_toTime));
	 * pst.setInt(5,1); pst.setString(6," "); pst.setInt(9,servic_id);
	 * 
	 * pst.execute(); alReport.add(empcode); }
	 * 
	 * if(dataType.equalsIgnoreCase("A") ||dataType.equalsIgnoreCase("EL")){
	 * pst=con.prepareStatement(
	 * "select * from leave_type where leave_type_code='EL' and org_id=? ");
	 * pst.setInt(1,uF.parseToInt(orgId)); rs=pst.executeQuery(); String
	 * leaveId=null; while(rs.next()){ leaveId=rs.getString("leave_type_id"); }
	 * 
	 * 
	 * if(leaveId!=null){
	 * pst=con.prepareStatement("insert into emp_leave_entry(emp_id," +
	 * "leave_from," + "leave_to," + "entrydate," + "emp_no_of_leave," +
	 * "leave_type_id," + "is_approved," + "user_id," + "approval_from," +
	 * "approval_to_date," + "encashment_status," + "ishalfday," +
	 * "ispaid,is_compensate" + ")values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	 * 
	 * pst.setInt(1, emp_per_id); pst.setDate(2,
	 * uF.getDateFormat(dateList.get(j), "dd/MM/yyyy")); pst.setDate(3,
	 * uF.getDateFormat(dateList.get(j), "dd/MM/yyyy")); pst.setDate(4,
	 * uF.getDateFormat(dateList.get(j), "dd/MM/yyyy"));
	 * if(dataType.equalsIgnoreCase("EL") || dataType.equalsIgnoreCase("A")){
	 * pst.setDouble(5, 1); }else{ pst.setDouble(5, 0.5); }
	 * 
	 * pst.setInt(6, uF.parseToInt(leaveId)); pst.setInt(7, 1);
	 * pst.setInt(8,uF.parseToInt((String) session.getAttribute(EMPID)));
	 * pst.setDate(9, uF.getDateFormat(dateList.get(j), "dd/MM/yyyy"));
	 * pst.setDate(10, uF.getDateFormat(dateList.get(j), "dd/MM/yyyy"));
	 * pst.setBoolean(11, false); pst.setBoolean(12, true); pst.setBoolean(13,
	 * true); pst.setBoolean(14, false); int x=pst.executeUpdate();
	 * 
	 * if(x>0){ pst=con.prepareStatement(
	 * "select max(leave_id)as leave_id  from emp_leave_entry");
	 * rs=pst.executeQuery(); String leave_id=null; while(rs.next()){
	 * leave_id=rs.getString("leave_id"); }
	 * insertLeaveBalance(con,pst,rs,emp_per_id
	 * ,uF.parseToInt(leaveId),dateList.get(j),dataType,leave_id); } } }
	 * 
	 * 
	 * 
	 * } if(dataType.equalsIgnoreCase("CL")){
	 * 
	 * } }
	 * 
	 * }
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace(); return FALSE; }finally{
	 * db.closeConnection(con); } return TRUE; }
	 */

	// private void insertLeaveBalance(Connection con, PreparedStatement pst,
	// ResultSet rs, int emp_per_id, int leaveTypeId, String strDate,
	// String leave_type, String leave_id) {
	//
	// try{
	//
	// Map<String,String> hmEmpLevel = CF.getEmpLevelMap();
	//
	// pst =
	// con.prepareStatement("select * from emp_leave_type where leave_type_id=? and  level_id = ? order by entrydate desc limit 1");
	// pst.setInt(1, leaveTypeId);
	// pst.setInt(2, uF.parseToInt(hmEmpLevel.get(""+emp_per_id)));
	// rs = pst.executeQuery();
	// boolean isPaid = false;
	//
	//
	// while(rs.next()){
	// isPaid = rs.getBoolean("is_paid");
	// }
	//
	// if(isPaid){
	// pst =
	// con.prepareStatement("UPDATE emp_leave_entry SET ispaid=true WHERE leave_id=?");
	// pst.setInt(1, uF.parseToInt(leave_id));
	// pst.execute();
	// }
	//
	//
	//
	// pst =
	// con.prepareStatement("select * from leave_register1 where emp_id = ? and leave_type_id=? and _date=? ");
	// pst.setInt(1, emp_per_id);
	// pst.setInt(2, leaveTypeId);
	// pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
	// rs = pst.executeQuery();
	// double dblTakenLeaves = 0;
	// while(rs.next()){
	// dblTakenLeaves = rs.getDouble("taken_paid") +
	// rs.getDouble("taken_unpaid");
	// }
	//
	//
	//
	// pst =
	// con.prepareStatement("select * from leave_register1 lr,(select max(_date) as _date, leave_type_id, emp_id from leave_register1 where _date<= ? and emp_id = ? and leave_type_id=? group by leave_type_id, emp_id ) lr1 where lr1._date= lr._date and lr.emp_id = lr1.emp_id and lr.leave_type_id = lr1.leave_type_id  and lr.emp_id = ? and lr.leave_type_id=?");
	// pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
	// pst.setInt(2, emp_per_id);
	// pst.setInt(3, leaveTypeId);
	// pst.setInt(4, emp_per_id);
	// pst.setInt(5, leaveTypeId);
	//
	// rs= pst.executeQuery();
	// double dblBalance = 0;
	// while(rs.next()){
	// dblBalance = uF.parseToDouble(rs.getString("balance"));
	// }
	//
	//
	//
	// // Needs to add a condition if manager approves leaves other the dates
	// for which employee has applied.
	//
	// pst =
	// con.prepareStatement("update leave_register set taken_leaves=? where emp_id = ? and leave_type_id=? and from_date<=? and to_date>=? ");
	// if(leave_type.trim().equalsIgnoreCase("HD") ||
	// leave_type.trim().equalsIgnoreCase("HD/EL") ||
	// leave_type.trim().equalsIgnoreCase("PL/HD")){
	// pst.setDouble(1, (dblTakenLeaves + 0.5));
	// }else{
	// pst.setDouble(1, (dblTakenLeaves + 1));
	// }
	//
	// pst.setInt(2, emp_per_id);
	// pst.setInt(3, leaveTypeId);
	// pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
	// pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
	// pst.execute();
	//
	//
	// double dblLeavesApproved = 0;
	//
	// if(leave_type.trim().equalsIgnoreCase("HD") ||
	// leave_type.trim().equalsIgnoreCase("PL/HD")){
	// dblLeavesApproved = 0.5;
	// }else{
	// dblLeavesApproved = 1;
	// }
	//
	// dblLeavesApproved = 0;
	// if(leave_type.trim().equalsIgnoreCase("HD") ||
	// leave_type.trim().equalsIgnoreCase("PL/HD")){
	// dblLeavesApproved = 0.5;
	// }else{
	// dblLeavesApproved = 1;
	// }
	//
	//
	// Calendar cal = GregorianCalendar.getInstance();
	// cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strDate,
	// DATE_FORMAT, "dd")));
	// cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strDate,
	// DATE_FORMAT, "MM")) - 1);
	// cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strDate,
	// DATE_FORMAT, "yyyy")));
	//
	//
	// double dblCount=0;
	// boolean isPaid1= false;
	// double dblLeaveDed = 0;
	// double dblBalance1 = dblBalance;
	// for(int i=0; i<dblLeavesApproved; i++){
	//
	//
	// Date dtCurrent =
	// uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) +
	// 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT);
	//
	// if(leave_type.trim().equalsIgnoreCase("HD")||
	// leave_type.trim().equalsIgnoreCase("PL/HD")){
	// dblCount+=0.5;
	// }else{
	//
	// if((dblBalance-dblCount)==0.5){
	// dblCount+=0.5;
	// }else{
	// dblCount++;
	// }
	//
	// }
	//
	//
	//
	// if(dblBalance>=dblCount && isPaid){
	// isPaid1 = true;
	//
	// if(leave_type.trim().equalsIgnoreCase("HD") ||
	// leave_type.trim().equalsIgnoreCase("PL/HD")){
	// dblBalance1 -= 0.5;
	// dblLeaveDed = 0.5;
	// }else{
	//
	// if(dblBalance1>=1){
	// dblBalance1 -= 1;
	// dblLeaveDed = 1;
	// }else if(dblBalance1>=0.5){
	// dblBalance1 -= 0.5;
	// dblLeaveDed = 0.5;
	// }else{
	// dblLeaveDed = 0;
	// }
	// }
	// }else{
	// isPaid1 = false;
	// if(leave_type.trim().equalsIgnoreCase("HD")||
	// leave_type.trim().equalsIgnoreCase("PL/HD")){
	// dblLeaveDed = 0.5;
	// }else{
	// dblLeaveDed = 1;
	// }
	//
	// }
	//
	//
	// pst =
	// con.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, is_paid, balance, _type) values (?,?,?,?,?,?,?,?)");
	// pst.setDate(1, dtCurrent);
	// pst.setInt(2, emp_per_id);
	// pst.setInt(3, leaveTypeId);
	// pst.setDouble(4, uF.parseToInt(leave_id));
	// pst.setDouble(5, dblLeaveDed);
	// pst.setBoolean(6, isPaid1);
	// pst.setDouble(7, dblBalance1);
	// pst.setBoolean(8, true);
	// pst.execute();
	//
	// cal.add(Calendar.DATE, 1);
	// }
	//
	//
	// CF.updateLeaveRegister1(con, CF, uF, dblLeavesApproved, 0,
	// ""+leaveTypeId, ""+emp_per_id);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// To get the leave type id passing with leave type code.
	// public int getLeaveTypeId(Connection con, PreparedStatement pst, String
	// leaveType,int emp_id){
	// int leaveTypeId = 0;
	// Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap();
	//
	// Map<String, String> hmEmpWlocationMap =new HashMap<String, String>();
	// Map<String, String> hmEmpOrgMap =new HashMap<String, String>();
	//
	// CF.getEmpWlocationMap(con, null, hmEmpWlocationMap, null, hmEmpOrgMap);
	//
	// try{
	// pst=con.prepareStatement("Select lt.leave_type_id from leave_type lt join emp_leave_type elt on elt.leave_type_id=lt.leave_type_id "
	// +
	// " and level_id=? and wlocation_id=? and lt.org_id=? where leave_type_code=?");
	// pst.setInt(1, uF.parseToInt(hmEmpLevelMap.get(""+emp_id)));
	// pst.setInt(2, uF.parseToInt(hmEmpWlocationMap.get(""+emp_id)));
	// pst.setInt(3, uF.parseToInt(hmEmpOrgMap.get(""+emp_id)));
	// pst.setString(4, leaveType);
	// ResultSet rsL = pst.executeQuery();
	// if(rsL.next()) {
	// leaveTypeId = rsL.getInt(1);
	// }
	//
	// }catch(Exception e){
	// e.printStackTrace();
	// return leaveTypeId;
	// }
	// return leaveTypeId;
	// }

}